/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere is maintained by OSS ERP Solutions Co., Ltd.                        *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/

package jpiere.plugin.groupware.form;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WYesNoEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.MColumn;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MRefList;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.calendar.Calendars;
import org.zkoss.calendar.api.CalendarEvent;
import org.zkoss.calendar.event.CalendarsEvent;
import org.zkoss.calendar.impl.SimpleCalendarModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Center;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.North;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.West;

import jpiere.plugin.groupware.model.MTeam;
import jpiere.plugin.groupware.model.MTeamMember;
import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoTeam;
import jpiere.plugin.groupware.util.GroupwareToDoUtil;
import jpiere.plugin.groupware.window.I_CallerPersonalToDoPopupwindow;
import jpiere.plugin.groupware.window.PersonalToDoPopupWindow;

/**
 *
 * JPIERE-0471: ToDo Calendar
 *
 * h.hagiwara
 *
 */
public class ToDoCalendar implements I_CallerPersonalToDoPopupwindow, IFormController, EventListener<Event>, ValueChangeListener {

	private static CLogger log = CLogger.getCLogger(ToDoCalendar.class);

	private CustomForm form;

	private Properties ctx = Env.getCtx();

	@Override
	public ADForm getForm()
	{
		return form;
	}

	private Calendars calendars = null;

	//Query Parameter
	private int p_login_User_ID = 0;
	private int p_AD_User_ID = 0;
	private int p_JP_Team_ID = 0;
	private boolean p_IsDisplaySchedule = true;
	private boolean p_IsDisplayTask = false;

	private String p_CalendarMold = GroupwareToDoUtil.BUTTON_SEVENDAYS_VIEW;

	//West Gadget
	JPierePersonalToDoGadget personalToDoGadget_Schedule = null;
	JPierePersonalToDoGadget personalToDoGadget_Task = null;
	JPierePersonalToDoGadget personalToDoGadget_Memo = null;


    public ToDoCalendar()
    {
    	form = new CustomForm();
    	Borderlayout mainBorderLayout = new Borderlayout();
    	form.appendChild(mainBorderLayout);

		ZKUpdateUtil.setWidth(mainBorderLayout, "99%");
		ZKUpdateUtil.setHeight(mainBorderLayout, "100%");

		p_AD_User_ID = Env.getAD_User_ID(ctx);
		p_login_User_ID = p_AD_User_ID;

		calendars= new Calendars();
		calendars.invalidate();

		calendars.setMold("default");
		calendars.setDays(7);

		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_CREATE, this);
		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_EDIT, this);
		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_UPDATE,this);
//		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_MOUSE_OVER, this);
		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_DAY,this);
//		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_WEEK, this);

		//***************** NORTH **************************//

		North mainBorderLayout_North = new North();
		mainBorderLayout_North.setSplittable(false);
		mainBorderLayout_North.setCollapsible(false);
		mainBorderLayout_North.setOpen(true);
		mainBorderLayout.appendChild(mainBorderLayout_North);
		mainBorderLayout_North.appendChild(createNorthContents());


		//***************** CENTER **************************//

		Center mainBorderLayout_Center = new Center();
		mainBorderLayout.appendChild(mainBorderLayout_Center);

		mainBorderLayout_Center.appendChild(calendars);

		//***************** WEST **************************//

		West mainBorderLayout_West = new West();
		mainBorderLayout_West.setSplittable(true);
		mainBorderLayout_West.setCollapsible(true);
		mainBorderLayout_West.setOpen(true);
		mainBorderLayout_West.setDroppable("true");
		ZKUpdateUtil.setWidth(mainBorderLayout_West, "25%");
		mainBorderLayout.appendChild(mainBorderLayout_West);
		mainBorderLayout_West.appendChild(createWestContents());


		//***************** Get ToDoes **************************//

		 List<ToDoCalendarEvent> list_ToDoes = getToDoCalendarEvents();


		SimpleCalendarModel scm = new SimpleCalendarModel();
		calendars.setModel(scm);

		scm.clear();
		for (ToDoCalendarEvent event : list_ToDoes)
			scm.add(event);

    }


    private List<ToDoCalendarEvent> getToDoCalendarEvents()
    {
		StringBuilder whereClauseFinal = null;
		StringBuilder whereClauseSchedule = null;
		StringBuilder whereClauseTask = null;
		StringBuilder orderClause = null;
		ArrayList<Object> list_parameters  = new ArrayList<Object>();
		Object[] parameters = null;

		if(p_IsDisplaySchedule)
		{
			//JP_ToDo_ScheduledStartTime
			whereClauseSchedule = new StringBuilder(" JP_ToDo_ScheduledStartTime <= ? AND JP_ToDo_ScheduledEndTime >= ? AND IsActive='Y' ");//1 - 2
			orderClause = new StringBuilder("JP_ToDo_ScheduledStartTime");

			Timestamp timestamp_Begin = new Timestamp(calendars.getBeginDate().getTime());
			Timestamp timestamp_End = new Timestamp(calendars.getEndDate().getTime());

			LocalDateTime toDayMin = LocalDateTime.of(timestamp_Begin.toLocalDateTime().toLocalDate(), LocalTime.MIN);
			LocalDateTime toDayMax = LocalDateTime.of(timestamp_End.toLocalDateTime().toLocalDate(), LocalTime.MAX);

			list_parameters.add(Timestamp.valueOf(toDayMax));
			list_parameters.add(Timestamp.valueOf(toDayMin));

			//JP_TODO_TYPE_Schedule
			whereClauseSchedule = whereClauseSchedule.append(" AND JP_ToDo_Type = ? ");
			list_parameters.add(MToDo.JP_TODO_TYPE_Schedule);

			if(p_JP_Team_ID==0)
			{
				whereClauseSchedule = whereClauseSchedule.append(" AND AD_User_ID = ? ");
				list_parameters.add(p_AD_User_ID);

			}else {

				whereClauseSchedule = whereClauseSchedule.append(" AND AD_User_ID IN (").append(createInUserClause(list_parameters)).append(")");

			}

			if(p_login_User_ID != p_AD_User_ID)
			{
				whereClauseSchedule = whereClauseSchedule.append(" AND (IsOpenToDoJP='Y' OR CreatedBy = ?)");
				list_parameters.add(p_login_User_ID);
			}

    	}

		if(p_IsDisplayTask)
		{
			//JP_ToDo_ScheduledStartTime
			whereClauseTask = new StringBuilder(" JP_ToDo_ScheduledEndTime <= ? AND JP_ToDo_ScheduledEndTime >= ? AND IsActive='Y' ");//1 - 2
			orderClause = new StringBuilder("JP_ToDo_ScheduledEndTime");

			Timestamp timestamp_Begin = new Timestamp(calendars.getBeginDate().getTime());
			Timestamp timestamp_End = new Timestamp(calendars.getEndDate().getTime());

			LocalDateTime toDayMin = LocalDateTime.of(timestamp_Begin.toLocalDateTime().toLocalDate(), LocalTime.MIN);
			LocalDateTime toDayMax = LocalDateTime.of(timestamp_End.toLocalDateTime().toLocalDate(), LocalTime.MAX);

			list_parameters.add(Timestamp.valueOf(toDayMax));
			list_parameters.add(Timestamp.valueOf(toDayMin));

			//JP_TODO_TYPE_Schedule
			whereClauseTask = whereClauseTask.append(" AND JP_ToDo_Type = ? ");
			list_parameters.add(MToDo.JP_TODO_TYPE_Task);

			if(p_JP_Team_ID==0)
			{
				whereClauseTask = whereClauseTask.append(" AND AD_User_ID = ? ");
				list_parameters.add(p_AD_User_ID);

			}else {

				whereClauseTask = whereClauseTask.append(" AND AD_User_ID IN (").append(createInUserClause(list_parameters)).append(")");
			}

			if(p_login_User_ID != p_AD_User_ID)
			{
				whereClauseTask = whereClauseTask.append(" AND (IsOpenToDoJP='Y' OR CreatedBy = ?)");
				list_parameters.add(p_login_User_ID);
			}

		}

		if(p_IsDisplaySchedule && p_IsDisplayTask)
		{
			whereClauseFinal = new StringBuilder("(").append( whereClauseSchedule.append(") OR (").append(whereClauseTask).append(")") );

		}else if(p_IsDisplaySchedule) {

			whereClauseFinal = whereClauseSchedule;

		}else if(p_IsDisplayTask) {

			whereClauseFinal = whereClauseTask;
		}

		parameters = list_parameters.toArray(new Object[list_parameters.size()]);

		return GroupwareToDoUtil.getToDoCalendarEvents(p_CalendarMold, p_JP_Team_ID > 0 ? true : false, whereClauseFinal.toString(), orderClause.toString(), parameters);
    }

    private String createInUserClause(ArrayList<Object> list_parameters)
    {

    	StringBuilder users = new StringBuilder("?");
    	list_parameters.add(p_AD_User_ID);

    	String Q = ",?";
    	MTeam team = new MTeam(ctx, p_JP_Team_ID, null);
    	MTeamMember[] member = team.getTeamMember();
    	for(int i = 0; i < member.length; i++)
    	{
    		if(p_AD_User_ID != member[i].getAD_User_ID())
    		{
	    		users = users.append(Q);
	    		list_parameters.add(member[i].getAD_User_ID());
    		}
    	}

    	return users.toString();

    }

    public Div createNorthContents()
    {
    	Div div = new Div();
		Hlayout hlayout = new Hlayout();
		div.appendChild(hlayout);

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		Button createNewToDo = new Button();
		createNewToDo.setImage(ThemeManager.getThemeResource("images/New16.png"));
		//createNewToDo.setClass("btn-small");
		createNewToDo.setName(GroupwareToDoUtil.BUTTON_NEW);
		createNewToDo.addEventListener(Events.ON_CLICK, this);
		createNewToDo.setId(String.valueOf(0));
		createNewToDo.setLabel("新規作成");//TODO
		hlayout.appendChild(createNewToDo);

		Button refresh = new Button();
		refresh.setImage(ThemeManager.getThemeResource("images/Refresh16.png"));
		//refresh.setClass("btn-small");
		refresh.setName(GroupwareToDoUtil.BUTTON_REFRESH);
		refresh.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(refresh);

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		//User Search Field
		hlayout.appendChild(GroupwareToDoUtil.createLabelDiv(Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_AD_User_ID), true, true));

		MLookup lookupUser = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_AD_User_ID),  DisplayType.Search);
		WSearchEditor userSearchEditor = new WSearchEditor(MToDo.COLUMNNAME_AD_User_ID, true, false, true, lookupUser);
		userSearchEditor.setValue(p_AD_User_ID);
		userSearchEditor.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(userSearchEditor.getComponent(), "true");
		hlayout.appendChild(userSearchEditor.getComponent());

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());


		//Team Search Field
		hlayout.appendChild(GroupwareToDoUtil.createLabelDiv(Msg.getElement(Env.getCtx(), MTeam.COLUMNNAME_JP_Team_ID), false, true));

		MLookup lookupTeam = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDoTeam.Table_Name, MTeam.COLUMNNAME_JP_Team_ID),  DisplayType.Search);
		WSearchEditor teamSearchEditor = new WSearchEditor( MTeam.COLUMNNAME_JP_Team_ID, true, false, true, lookupTeam);
		teamSearchEditor.setValue(p_JP_Team_ID);
		teamSearchEditor.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(teamSearchEditor.getComponent(), "true");
		hlayout.appendChild(teamSearchEditor.getComponent());
		//teamSearchEditor.setVisible(false);

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		WYesNoEditor IsDisplaySchedule = new WYesNoEditor("IsDisplaySchedule", "予定を表示する", null, true, false, true);//TODO : 多言語化
		IsDisplaySchedule.setValue(p_IsDisplaySchedule);
		IsDisplaySchedule.addValueChangeListener(this);
		hlayout.appendChild(GroupwareToDoUtil.createEditorDiv(IsDisplaySchedule, true));

		WYesNoEditor IsDisplayTask = new WYesNoEditor("IsDisplayTask", "タスクを表示する", null, true, false, true);//TODO : 多言語化
		IsDisplayTask.setValue(p_IsDisplayTask);
		IsDisplayTask.addValueChangeListener(this);
		hlayout.appendChild(GroupwareToDoUtil.createEditorDiv(IsDisplayTask, true));

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		Button leftBtn = new Button();
		leftBtn.setImage(ThemeManager.getThemeResource("images/MoveLeft16.png"));
		//leftBtn.setClass("btn-small");
		leftBtn.setName(GroupwareToDoUtil.BUTTON_PREVIOUS);
		leftBtn.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(leftBtn);

		Button today = new Button();
		today.setLabel("今日");
		//today.setClass("btn-small");
		today.setName(GroupwareToDoUtil.BUTTON_TODAY);
		today.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(today);

		Button rightBtn = new Button();
		rightBtn.setImage(ThemeManager.getThemeResource("images/MoveRight16.png"));
		//rightBtn.setClass("btn-small");
		rightBtn.addEventListener(Events.ON_CLICK, this);
		rightBtn.setName(GroupwareToDoUtil.BUTTON_NEXT);
		hlayout.appendChild(rightBtn);

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		Button oneDayView = new Button();
		oneDayView.setLabel("日");
		//oneDayView.setClass("btn-small");
		oneDayView.setName(GroupwareToDoUtil.BUTTON_ONEDAY_VIEW);
		oneDayView.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(oneDayView);

		Button sevenDayView = new Button();
		sevenDayView.setLabel("週");
		//sevenDayView.setClass("btn-small");
		sevenDayView.setName(GroupwareToDoUtil.BUTTON_SEVENDAYS_VIEW);
		sevenDayView.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(sevenDayView);

		Button monthDayView = new Button();
		monthDayView.setLabel("月");
		//monthDayView.setClass("btn-small");
		monthDayView.setName(GroupwareToDoUtil.BUTTON_MONTH_VIEW);
		monthDayView.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(monthDayView);

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());


		label_DisplayPeriod = new Label();
		updateDateLabel();
		hlayout.appendChild(GroupwareToDoUtil.createLabelDiv(label_DisplayPeriod, false, true));

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

    	return div;

    }

    public Div createWestContents()
    {
    	Div div = new Div();
		Vlayout vlayout = new Vlayout();
		vlayout.setDroppable("true");
		div.appendChild(vlayout);


		//ToDo管理メニュー
		Groupbox groupBox0 = new Groupbox();
		groupBox0.setOpen(false);
		groupBox0.setDraggable("true");
		groupBox0.setMold("3d");
		groupBox0.setWidgetListener("onOpen", "this.caption.setIconSclass('z-icon-caret-' + (event.open ? 'down' : 'right'));");
		vlayout.appendChild(groupBox0);

		Caption caption0 = new Caption("ToDo管理メニュー");//TODO 多言語化
		caption0.setIconSclass("z-icon-caret-right");
		groupBox0.appendChild(caption0);
		groupBox0.appendChild(new Label("ToDo管理の業務メニュを表示したい!!"));


		//Schedule
		Groupbox groupBox1 = new Groupbox();
		groupBox1.setOpen(true);
		groupBox1.setDraggable("true");
		groupBox1.setMold("3d");
		groupBox1.setWidgetListener("onOpen", "this.caption.setIconSclass('z-icon-caret-' + (event.open ? 'down' : 'right'));");
		vlayout.appendChild(groupBox1);

		MColumn colmn = MColumn.get(ctx, MToDo.Table_Name,MToDo.COLUMNNAME_JP_ToDo_Type);
		String scheduleName = MRefList.getListName(ctx, colmn.getAD_Reference_Value_ID(), "S");

		Caption caption1 = new Caption(scheduleName);
		caption1.setIconSclass("z-icon-caret-down");
		groupBox1.appendChild(caption1);

		personalToDoGadget_Schedule = new JPierePersonalToDoGadget(MToDo.JP_TODO_TYPE_Schedule);
		groupBox1.appendChild(personalToDoGadget_Schedule);


		//Unfinished Tasks
		Groupbox groupBox2 = new Groupbox();
		groupBox2.setOpen(true);
		groupBox2.setDraggable("true");
		groupBox2.setMold("3d");
		groupBox2.setWidgetListener("onOpen", "this.caption.setIconSclass('z-icon-caret-' + (event.open ? 'down' : 'right'));");
		vlayout.appendChild(groupBox2);

		Caption caption2 = new Caption(Msg.getMsg(Env.getCtx(), "JP_UnfinishedTasks"));//Unfinished Tasks
		caption2.setIconSclass("z-icon-caret-down");
		groupBox2.appendChild(caption2);

		personalToDoGadget_Task = new JPierePersonalToDoGadget(MToDo.JP_TODO_TYPE_Task);
		groupBox2.appendChild(personalToDoGadget_Task);


		//Unfinished Memo
		Groupbox groupBox3 = new Groupbox();
		groupBox3.setOpen(true);
		groupBox3.setDraggable("true");
		groupBox3.setMold("3d");
		groupBox3.setWidgetListener("onOpen", "this.caption.setIconSclass('z-icon-caret-' + (event.open ? 'down' : 'right'));");
		vlayout.appendChild(groupBox3);

		Caption caption3 = new Caption(Msg.getMsg(Env.getCtx(), "JP_UnfinishedMemo"));//Unfinished Memo
		caption3.setIconSclass("z-icon-caret-down");
		groupBox3.appendChild(caption3);

		personalToDoGadget_Memo= new JPierePersonalToDoGadget(MToDo.JP_TODO_TYPE_Memo);
		groupBox3.appendChild(personalToDoGadget_Memo);

    	return div;

    }


	@Override
	public void valueChange(ValueChangeEvent evt)
	{
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();

		if(MToDo.COLUMNNAME_AD_User_ID.equals(name))
		{
			if(value == null)
			{
				p_AD_User_ID = 0;
			}else {
				p_AD_User_ID = Integer.parseInt(value.toString());
			}

			refresh(null);

		}else if(MTeam.COLUMNNAME_JP_Team_ID.equals(name)){

			if(value == null)
			{
				p_JP_Team_ID = 0;
			}else {
				p_JP_Team_ID = Integer.parseInt(value.toString());
			}

			refresh();

		}else if("IsDisplaySchedule".equals(name)) {

			p_IsDisplaySchedule = (boolean)value;
			refresh();

		}else if("IsDisplayTask".equals(name)) {

			p_IsDisplayTask = (boolean)value;
			refresh();
		}

	}


	@Override
	public void onEvent(Event event) throws Exception
	{
		Component comp = event.getTarget();
		String eventName = event.getName();
		if(eventName.equals(Events.ON_CLICK))
		{
			if(comp instanceof Button)
			{
				Button btn = (Button) comp;
				String btnName = btn.getName();
				if(GroupwareToDoUtil.BUTTON_NEW.equals(btnName))
				{
					list_ToDoes = null;
					p_CalendarsEventBeginDate = null;
					p_CalendarsEventEndDate =  null;

					PersonalToDoPopupWindow todoWindow = new PersonalToDoPopupWindow(this, -1);
					SessionManager.getAppDesktop().showWindow(todoWindow);

				}else if(GroupwareToDoUtil.BUTTON_PREVIOUS.equals(btnName))
				{
					calendars.previousPage();
					updateDateLabel();
					refresh();

				}else if(GroupwareToDoUtil.BUTTON_NEXT.equals(btnName)){

					calendars.nextPage();
					updateDateLabel();
					refresh();

				}else if(GroupwareToDoUtil.BUTTON_REFRESH.equals(btnName)){

					refresh(null);

				}else if(GroupwareToDoUtil.BUTTON_TODAY.equals(btnName)){

					calendars.setCurrentDate(Calendar.getInstance(calendars.getDefaultTimeZone()).getTime());
					updateDateLabel();
					refresh();

				}else if(GroupwareToDoUtil.BUTTON_ONEDAY_VIEW.equals(btnName)){

					p_CalendarMold = GroupwareToDoUtil.BUTTON_ONEDAY_VIEW;
					setCalendarMold(1);
					updateDateLabel();
					refresh();

				}else if(GroupwareToDoUtil.BUTTON_SEVENDAYS_VIEW.equals(btnName)){

					p_CalendarMold = GroupwareToDoUtil.BUTTON_SEVENDAYS_VIEW;
					setCalendarMold(7);
					updateDateLabel();
					refresh();

				}else if(GroupwareToDoUtil.BUTTON_MONTH_VIEW.equals(btnName)){

					p_CalendarMold = GroupwareToDoUtil.BUTTON_MONTH_VIEW;
					setCalendarMold(0);
					updateDateLabel();
					refresh();

				}

			}

		}else if (GroupwareToDoUtil.CALENDAR_EVENT_CREATE.equals(eventName)) {

			if (event instanceof CalendarsEvent)
			{
				list_ToDoes = null;

				CalendarsEvent calendarsEvent = (CalendarsEvent) event;
				p_CalendarsEventBeginDate = new Timestamp(calendarsEvent.getBeginDate().getTime());
				p_CalendarsEventEndDate = new Timestamp(calendarsEvent.getEndDate().getTime());

				PersonalToDoPopupWindow todoWindow = new PersonalToDoPopupWindow(this, -1);
				SessionManager.getAppDesktop().showWindow(todoWindow);
			}

		}else if (GroupwareToDoUtil.CALENDAR_EVENT_EDIT.equals(eventName)) {
			if (event instanceof CalendarsEvent)
			{
				CalendarsEvent calendarsEvent = (CalendarsEvent) event;
				CalendarEvent calendarEvent = calendarsEvent.getCalendarEvent();

				if (calendarEvent instanceof ToDoCalendarEvent)
				{
					ToDoCalendarEvent ce = (ToDoCalendarEvent) calendarEvent;

					list_ToDoes = new ArrayList<MToDo>();
					list_ToDoes.add(ce.getToDoD());

					p_CalendarsEventBeginDate = ce.getToDoD().getJP_ToDo_ScheduledStartTime();
					p_CalendarsEventEndDate = ce.getToDoD().getJP_ToDo_ScheduledEndTime();

					PersonalToDoPopupWindow todoWindow = new PersonalToDoPopupWindow(this, 0);
					SessionManager.getAppDesktop().showWindow(todoWindow);
				}
			}

		}else if (GroupwareToDoUtil.CALENDAR_EVENT_MOUSE_OVER.equals(eventName)){

		}else if (GroupwareToDoUtil.CALENDAR_EVENT_DAY.equals(eventName)){

			Calendars cal = (Calendars)comp;
			Date date =  (Date)event.getData();
			cal.setCurrentDate(date);

			p_CalendarMold = GroupwareToDoUtil.BUTTON_ONEDAY_VIEW;
			setCalendarMold(1);
			updateDateLabel();
			refresh();

		}else if (GroupwareToDoUtil.CALENDAR_EVENT_WEEK.equals(eventName)){

			//I don't know this Event

		}else {

			//impossible
		}
	}

	private void setCalendarMold(int days)
	{
		if (days > 0)
		{
			calendars.setMold("default");
			calendars.setDays(days);
		} else {
			calendars.setMold("month");
		}

	}

	private Label label_DisplayPeriod;
	private void updateDateLabel()
	{
		Date b = calendars.getBeginDate();
		Date e = calendars.getEndDate();

		LocalDateTime local = new Timestamp(e.getTime()).toLocalDateTime();
		e = new Date(Timestamp.valueOf(local.minusDays(1)).getTime());

		SimpleDateFormat sdfV = DisplayType.getDateFormat();
		//sdfV.setTimeZone(calendars.getDefaultTimeZone());

		label_DisplayPeriod.setValue(sdfV.format(b) + " - " + sdfV.format(e));
	}


	List<MToDo> list_ToDoes = null;

	@Override
	public List<MToDo> getListToDoes()
	{
		return list_ToDoes;
	}


	@Override
	public int getInitial_User_ID()
	{
		return p_AD_User_ID;
	}

	@Override
	public String getInitial_ToDo_Type()
	{
		if(list_ToDoes == null)
		{
			return MToDo.JP_TODO_TYPE_Schedule;
		}else {

			return list_ToDoes.get(0).getJP_ToDo_Type();
		}

	}


	private void refresh()
	{
		SimpleCalendarModel scm = (SimpleCalendarModel)calendars.getModel();
		scm.clear();

		if(!p_IsDisplaySchedule && !p_IsDisplayTask)
		{
			;//Noting to do;
		}else {

			List<ToDoCalendarEvent> list_CalEvents = getToDoCalendarEvents();
			for (ToDoCalendarEvent event : list_CalEvents)
				scm.add(event);
		}

		calendars.setModel(scm);
	}


	@Override
	public boolean refresh(String JP_ToDo_Type)
	{
		refresh();
		refreshWest(JP_ToDo_Type);

		return true;
	}

	public boolean refreshWest(String JP_ToDo_Type)
	{
		personalToDoGadget_Schedule.setAD_User_ID(p_AD_User_ID);
		personalToDoGadget_Task.setAD_User_ID(p_AD_User_ID);
		personalToDoGadget_Memo.setAD_User_ID(p_AD_User_ID);

		if(Util.isEmpty(JP_ToDo_Type))
		{
			personalToDoGadget_Schedule.refresh(MToDo.JP_TODO_TYPE_Schedule);
			personalToDoGadget_Task.refresh(MToDo.JP_TODO_TYPE_Task);
			personalToDoGadget_Memo.refresh(MToDo.JP_TODO_TYPE_Memo);
		}else {
			personalToDoGadget_Schedule.refresh(JP_ToDo_Type);
			personalToDoGadget_Task.refresh(JP_ToDo_Type);
			personalToDoGadget_Memo.refresh(JP_ToDo_Type);
		}

		return true;
	}

	private Timestamp p_CalendarsEventBeginDate = null;

	@Override
	public Timestamp getInitialScheduledStartTime()
	{
		Timestamp timestamp = null;
		if(p_CalendarsEventBeginDate == null)
		{
			timestamp = new Timestamp(calendars.getCurrentDate().getTime());

		}else {
			timestamp = p_CalendarsEventBeginDate;
		}

		return Timestamp.valueOf(LocalDateTime.of(timestamp.toLocalDateTime().toLocalDate(), timestamp.toLocalDateTime().toLocalTime()));
	}

	private Timestamp p_CalendarsEventEndDate = null;

	@Override
	public Timestamp getInitialScheduledEndTime()
	{
		Timestamp timestamp = null;
		if(p_CalendarsEventEndDate == null)
		{
			timestamp = new Timestamp(calendars.getCurrentDate().getTime());

		}else {
			timestamp =  p_CalendarsEventEndDate;
		}

		return Timestamp.valueOf(LocalDateTime.of(timestamp.toLocalDateTime().toLocalDate(), timestamp.toLocalDateTime().toLocalTime()));
	}
}
