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
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
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

	JPierePersonalToDoGadget todoG = null;

    public ToDoCalendar()
    {
    	form = new CustomForm();
    	Borderlayout mainBorderLayout = new Borderlayout();
    	form.appendChild(mainBorderLayout);

		ZKUpdateUtil.setWidth(mainBorderLayout, "99%");
		ZKUpdateUtil.setHeight(mainBorderLayout, "100%");

		calendars= new Calendars();

		//***************** NORTH **************************//

		North mainBorderLayout_North = new North();
//		mainBorderLayout_North.setStyle("border: none");
		mainBorderLayout_North.setSplittable(false);
		mainBorderLayout_North.setCollapsible(false);
		mainBorderLayout_North.setOpen(true);
		//mainBorderLayout_North.setTitle("トップコンテンツ");
		mainBorderLayout.appendChild(mainBorderLayout_North);
		mainBorderLayout_North.appendChild(createMainController(this));


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

		Vlayout vlayout = new Vlayout();
		vlayout.setDroppable("true");
		mainBorderLayout_West.appendChild(vlayout);

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


		Groupbox groupBox1 = new Groupbox();
		groupBox1.setOpen(true);
		groupBox1.setDraggable("true");
		groupBox1.setMold("3d");
		groupBox1.setWidgetListener("onOpen", "this.caption.setIconSclass('z-icon-caret-' + (event.open ? 'down' : 'right'));");
		vlayout.appendChild(groupBox1);

		Caption caption1 = new Caption("予定");//TODO 多言語化
		caption1.setIconSclass("z-icon-caret-down");
		groupBox1.appendChild(caption1);

		JPierePersonalToDoGadget todoS = new JPierePersonalToDoGadget("S");
		groupBox1.appendChild(todoS);


		Groupbox groupBox2 = new Groupbox();
		groupBox2.setOpen(true);
		groupBox2.setDraggable("true");
		groupBox2.setMold("3d");
		groupBox2.setWidgetListener("onOpen", "this.caption.setIconSclass('z-icon-caret-' + (event.open ? 'down' : 'right'));");
		vlayout.appendChild(groupBox2);

		Caption caption2 = new Caption("完了してないタスク");//TODO 多言語化
		caption2.setIconSclass("z-icon-caret-down");
		groupBox2.appendChild(caption2);

		JPierePersonalToDoGadget todoT = new JPierePersonalToDoGadget("T");
		groupBox2.appendChild(todoT);
		todoG = todoT;

		Groupbox groupBox3 = new Groupbox();
		groupBox3.setOpen(false);
		groupBox3.setDraggable("true");
		groupBox3.setMold("3d");
		groupBox3.setWidgetListener("onOpen", "this.caption.setIconSclass('z-icon-caret-' + (event.open ? 'down' : 'right'));");
		vlayout.appendChild(groupBox3);

		Caption caption3 = new Caption("完了してないメモ");//TODO 多言語化
		caption3.setIconSclass("z-icon-caret-right");
		groupBox3.appendChild(caption3);

		JPierePersonalToDoGadget todoM = new JPierePersonalToDoGadget("M");
		groupBox3.appendChild(todoM);


		ArrayList<ToDoCalendarEvent> events = new ArrayList<ToDoCalendarEvent>();
		List<MToDo> list_ToDoes =  todoS.getListToDoes();
		for(MToDo toDo : list_ToDoes)
		{
			events.add(new ToDoCalendarEvent(toDo));
		}

		SimpleCalendarModel scm = new SimpleCalendarModel();
		calendars.setModel(scm);

		scm.clear();
		for (ToDoCalendarEvent event : events)
			scm.add(event);

		calendars.addEventListener("onEventCreate", this);
		calendars.addEventListener("onEventEdit", this);

		calendars.invalidate();

    }

    public Div createMainController(EventListener<Event> eventListener)
    {
    	Div div = new Div();
		Hlayout hlayout = new Hlayout();
		div.appendChild(hlayout);

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		Button createNewToDo = new Button();
		createNewToDo.setImage(ThemeManager.getThemeResource("images/New16.png"));
		createNewToDo.setClass("btn-small");
		createNewToDo.setName(GroupwareToDoUtil.BUTTON_NEW);
		createNewToDo.addEventListener(Events.ON_CLICK, this);
		createNewToDo.setId(String.valueOf(0));
		hlayout.appendChild(createNewToDo);

		Button refresh = new Button();
		refresh.setImage(ThemeManager.getThemeResource("images/Refresh16.png"));
		refresh.setClass("btn-small");
		refresh.setName(GroupwareToDoUtil.BUTTON_REFRESH);
		refresh.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(refresh);

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		//User Search Field
		hlayout.appendChild(GroupwareToDoUtil.createLabelDiv(Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_AD_User_ID), true, true));

		MLookup lookupUser = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_AD_User_ID),  DisplayType.Search);
		WSearchEditor userSearchEditor = new WSearchEditor(MToDo.COLUMNNAME_AD_User_ID, true, false, true, lookupUser);
		p_Initial_User_ID = Env.getAD_User_ID(ctx);
		userSearchEditor.setValue(p_Initial_User_ID);
		userSearchEditor.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(userSearchEditor.getComponent(), "true");
		hlayout.appendChild(userSearchEditor.getComponent());

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());


		//Team Search Field
		hlayout.appendChild(GroupwareToDoUtil.createLabelDiv(Msg.getElement(Env.getCtx(), MTeam.COLUMNNAME_JP_Team_ID), false, true));

		MLookup lookupTeam = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDoTeam.Table_Name, MTeam.COLUMNNAME_JP_Team_ID),  DisplayType.Search);
		WSearchEditor teamSearchEditor = new WSearchEditor( MTeam.COLUMNNAME_JP_Team_ID, true, false, true, lookupTeam);
		teamSearchEditor.setValue(null);
		teamSearchEditor.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(teamSearchEditor.getComponent(), "true");
		hlayout.appendChild(teamSearchEditor.getComponent());
		//teamSearchEditor.setVisible(false);

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		WYesNoEditor IsDisplaySchedule = new WYesNoEditor("IsDisplaySchedule", "予定を表示する", null, true, false, true);//TODO : 多言語化
		IsDisplaySchedule.setValue(true);
		IsDisplaySchedule.addValueChangeListener(this);
		hlayout.appendChild(GroupwareToDoUtil.createEditorDiv(IsDisplaySchedule, true));

		WYesNoEditor IsDisplayTask = new WYesNoEditor("IsDisplayTask", "タスクを表示する", null, true, false, true);//TODO : 多言語化
		IsDisplayTask.setValue(false);
		IsDisplayTask.addValueChangeListener(this);
		hlayout.appendChild(GroupwareToDoUtil.createEditorDiv(IsDisplayTask, true));

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		Button leftBtn = new Button();
		leftBtn.setImage(ThemeManager.getThemeResource("images/MoveLeft16.png"));
		leftBtn.setClass("btn-small");
		leftBtn.setName(GroupwareToDoUtil.BUTTON_PREVIOUS);
		leftBtn.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(leftBtn);

		Button today = new Button();
		today.setLabel("今日");
		today.setClass("btn-small");
		today.setName(GroupwareToDoUtil.BUTTON_TODAY);
		today.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(today);

		Button rightBtn = new Button();
		rightBtn.setImage(ThemeManager.getThemeResource("images/MoveRight16.png"));
		rightBtn.setClass("btn-small");
		rightBtn.addEventListener(Events.ON_CLICK, this);
		rightBtn.setName(GroupwareToDoUtil.BUTTON_NEXT);
		hlayout.appendChild(rightBtn);

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		hlayout.appendChild(GroupwareToDoUtil.createLabelDiv("表示形式:", false, true));//TODO 多言語化

		Button oneDayView = new Button();
		oneDayView.setLabel("日");
		oneDayView.setClass("btn-small");
		oneDayView.setName(GroupwareToDoUtil.BUTTON_ONEDAY_VIEW);
		oneDayView.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(oneDayView);

		Button sevenDayView = new Button();
		sevenDayView.setLabel("週");
		sevenDayView.setClass("btn-small");
		sevenDayView.setName(GroupwareToDoUtil.BUTTON_SEVENDAYS_VIEW);
		sevenDayView.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(sevenDayView);

		Button monthDayView = new Button();
		monthDayView.setLabel("月");
		monthDayView.setClass("btn-small");
		monthDayView.setName(GroupwareToDoUtil.BUTTON_MONTH_VIEW);
		monthDayView.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(monthDayView);

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		hlayout.appendChild(GroupwareToDoUtil.createLabelDiv("表示期間:", false, true));//TODO 多言語化

		lblDate = new Label();
		updateDateLabel();
		hlayout.appendChild(GroupwareToDoUtil.createLabelDiv(lblDate, false, true));

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

    	return div;

    }

    public Div createSubController(EventListener<Event> eventListener, String JP_ToDo_Type)
    {

    	return null;

    }


	@Override
	public void valueChange(ValueChangeEvent evt)
	{
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();

		if(MToDo.COLUMNNAME_AD_User_ID.equals(name))
		{
			if(value == null)
				p_Initial_User_ID = 0;
			else
				p_Initial_User_ID = Integer.parseInt(value.toString());

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

				}else if(GroupwareToDoUtil.BUTTON_NEXT.equals(btnName)){

					calendars.nextPage();
					updateDateLabel();

				}else if(GroupwareToDoUtil.BUTTON_REFRESH.equals(btnName)){

					;
				}else if(GroupwareToDoUtil.BUTTON_TODAY.equals(btnName)){

					calendars.setCurrentDate(Calendar.getInstance(calendars.getDefaultTimeZone()).getTime());
					updateDateLabel();

				}else if(GroupwareToDoUtil.BUTTON_ONEDAY_VIEW.equals(btnName)){

					divTabClicked(1);
					updateDateLabel();

				}else if(GroupwareToDoUtil.BUTTON_SEVENDAYS_VIEW.equals(btnName)){

					divTabClicked(7);
					updateDateLabel();

				}else if(GroupwareToDoUtil.BUTTON_MONTH_VIEW.equals(btnName)){

					divTabClicked(0);
					updateDateLabel();

				}

			}

		}else if (eventName.equals("onEventCreate")) {

			if (event instanceof CalendarsEvent)
			{
				list_ToDoes = null;

				CalendarsEvent calendarsEvent = (CalendarsEvent) event;
				p_CalendarsEventBeginDate = new Timestamp(calendarsEvent.getBeginDate().getTime());
				p_CalendarsEventEndDate = new Timestamp(calendarsEvent.getEndDate().getTime());

				PersonalToDoPopupWindow todoWindow = new PersonalToDoPopupWindow(this, -1);
				SessionManager.getAppDesktop().showWindow(todoWindow);
			}
		}
		else if (eventName.equals("onEventEdit")) {
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
		}
	}

	private void divTabClicked(int days)
	{
		if (days > 0) {
			calendars.setMold("default");
			calendars.setDays(days);
		} else {
			calendars.setMold("month");
		}

	}

	private Label lblDate;
	private void updateDateLabel()
	{
		Date b = calendars.getBeginDate();
		Date e = calendars.getEndDate();

		LocalDateTime local = new Timestamp(e.getTime()).toLocalDateTime();
		e = new Date(Timestamp.valueOf(local.minusDays(1)).getTime());

		SimpleDateFormat sdfV = DisplayType.getDateFormat();
		//sdfV.setTimeZone(calendars.getDefaultTimeZone());

		lblDate.setValue(sdfV.format(b) + " - " + sdfV.format(e));
	}


	List<MToDo> list_ToDoes = null;

	@Override
	public List<MToDo> getListToDoes()
	{
		return list_ToDoes;
	}


	int p_Initial_User_ID = 0;

	@Override
	public int getInitial_User_ID()//TODO
	{
		return p_Initial_User_ID;
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

	@Override
	public boolean refresh()//TODO
	{

		return false;
	}


	private Timestamp p_CalendarsEventBeginDate = null;

	@Override
	public Timestamp getInitialScheduledStartTime()
	{
		if(p_CalendarsEventBeginDate == null)
		{
			return new Timestamp(calendars.getCurrentDate().getTime());//TODO 時間の端数処理

		}else {
			return p_CalendarsEventBeginDate;
		}
	}

	private Timestamp p_CalendarsEventEndDate = null;

	@Override
	public Timestamp getInitialScheduledEndTime()
	{
		if(p_CalendarsEventEndDate == null)
		{
			return new Timestamp(calendars.getCurrentDate().getTime());//TODO 時間の端数処理

		}else {
			return p_CalendarsEventEndDate;
		}
	}
}
