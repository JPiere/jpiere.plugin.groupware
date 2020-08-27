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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Tabbox;
import org.adempiere.webui.component.Tabpanel;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WNumberEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WTableDirEditor;
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
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.Query;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.calendar.Calendars;
import org.zkoss.calendar.api.CalendarEvent;
import org.zkoss.calendar.api.CalendarModel;
import org.zkoss.calendar.event.CalendarsEvent;
import org.zkoss.calendar.impl.SimpleCalendarModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Center;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.North;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.West;

import jpiere.plugin.groupware.model.MGroupwareUser;
import jpiere.plugin.groupware.model.MTeam;
import jpiere.plugin.groupware.model.MTeamMember;
import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoCategory;
import jpiere.plugin.groupware.model.MToDoTeam;
import jpiere.plugin.groupware.util.GroupwareToDoUtil;
import jpiere.plugin.groupware.window.I_CallerToDoPopupwindow;
import jpiere.plugin.groupware.window.PersonalToDoPopupWindow;

/**
 *
 * JPIERE-0471: ToDo Calendar
 *
 * h.hagiwara
 *
 */
public class ToDoCalendar implements I_CallerToDoPopupwindow, IFormController, EventListener<Event>, ValueChangeListener {

	//private static CLogger log = CLogger.getCLogger(ToDoCalendar.class);

	private CustomForm form;

	private Properties ctx = Env.getCtx();

	@Override
	public ADForm getForm()
	{
		return form;
	}

	private HashMap<Integer,Calendars> 	map_Calendars = new HashMap<Integer,Calendars>();

	private HashMap<Integer,List<ToDoCalendarEvent>> map_ScheduleCalendarEvent_Personal = new HashMap<Integer,List<ToDoCalendarEvent>>();
	private HashMap<Integer,List<ToDoCalendarEvent>> map_TaskCalendarEvent_Psersonal = new HashMap<Integer,List<ToDoCalendarEvent>>();

	private HashMap<Integer,List<ToDoCalendarEvent>> map_ScheduleCalendarEvent_Team = new HashMap<Integer,List<ToDoCalendarEvent>>();
	private HashMap<Integer,List<ToDoCalendarEvent>> map_TaskCalendarEvent_Team = new HashMap<Integer,List<ToDoCalendarEvent>>();

	Center mainBorderLayout_Center;

	//Query Parameter
	private int p_login_User_ID = 0;
	private int p_AD_User_ID = 0;
	private int p_SelectedTab_User_ID = 0;
	private int p_OldSelectedTab_User_ID = 0;

	private MGroupwareUser groupwareUser = null;
	private int p_JP_Team_ID = 0;
	private MTeam m_Team = null;

	private int p_JP_ToDo_Category_ID = 0;
	private String p_JP_ToDo_Status = MToDo.JP_TODO_STATUS_NotYetStarted;
	private boolean p_IsDisplaySchedule = true;
	private boolean p_IsDisplayTask = false;
	private String p_JP_ToDo_Main_Calendar_View = MGroupwareUser.JP_TODO_MAIN_CALENDAR_VIEW_Personal;

	private String p_CalendarMold = null;


	private MLookup lookup_JP_ToDo_Category_ID;

	private WSearchEditor editor_AD_User_ID;
	private WSearchEditor editor_JP_ToDo_Category_ID;
	private WSearchEditor editor_JP_Team_ID ;
	private WTableDirEditor editor_JP_FirstDayOfWeek ;
	private WNumberEditor editor_JP_ToDo_Calendar_BeginTime ;
	private WNumberEditor editor_JP_ToDo_Calendar_EndTime ;
	private WTableDirEditor editor_JP_ToDo_Main_Calendar_View ;

	private Label label_AD_User_ID ;
	private Label label_JP_ToDo_Category_ID ;
	private Label label_JP_Team_ID ;
	private Label label_JP_FirstDayOfWeek;
	private Label label_JP_ToDo_Calendar_BeginTime;
	private Label label_JP_ToDo_Calendar_EndTime;
	private Label label_JP_ToDo_Main_Calendar_View;


	private Button btn_ThreeLines;

	private String p_JP_FristDayOfWeek = MGroupwareUser.JP_FIRSTDAYOFWEEK_Sunday;

	private Tabbox tabbox;
	private Tab tab_p_AD_User_ID;
	private Tabpanel tabpanel_p_AD_User_ID;

	//West Gadget
	JPierePersonalToDoGadget personalToDoGadget_Schedule = null;
	JPierePersonalToDoGadget personalToDoGadget_Task = null;
	JPierePersonalToDoGadget personalToDoGadget_Memo = null;


    public ToDoCalendar()
    {
		p_AD_User_ID = Env.getAD_User_ID(ctx);
		p_login_User_ID = p_AD_User_ID;
		p_SelectedTab_User_ID = p_AD_User_ID;
		p_OldSelectedTab_User_ID = p_AD_User_ID;

		map_Calendars.put(p_AD_User_ID, createInitialCalendar());

		initZk();

		updateDateLabel();
		refresh();

    }


    private Calendars createInitialCalendar()
    {
    	Calendars calendars = new Calendars();

		calendars.invalidate();

		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_CREATE, this);
		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_EDIT, this);
		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_UPDATE,this);
//		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_MOUSE_OVER, this);
		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_DAY,this);
//		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_WEEK, this);


		if(groupwareUser == null)
		{
			groupwareUser = MGroupwareUser.get(ctx, p_login_User_ID);

			if(groupwareUser != null)
			{
				p_IsDisplaySchedule = groupwareUser.isDisplayScheduleJP();
				p_IsDisplayTask = groupwareUser.isDisplayTaskJP();
				calendars.setBeginTime(groupwareUser.getJP_ToDo_Calendar_BeginTime());
				calendars.setEndTime(groupwareUser.getJP_ToDo_Calendar_EndTime());

				String fdow = groupwareUser.getJP_FirstDayOfWeek();
				if(!Util.isEmpty(fdow))
				{
					int AD_Column_ID = MColumn.getColumn_ID(MGroupwareUser.Table_Name, MGroupwareUser.COLUMNNAME_JP_FirstDayOfWeek);
					int AD_Reference_Value_ID = MColumn.get(ctx, AD_Column_ID).getAD_Reference_Value_ID();
					MRefList refList =MRefList.get(ctx, AD_Reference_Value_ID, fdow,null);
					p_JP_FristDayOfWeek = refList.getValue();
					calendars.setFirstDayOfWeek(refList.getName());
				}

				p_JP_ToDo_Main_Calendar_View = groupwareUser.getJP_ToDo_Main_Calendar_View();

			}
		}

		//set Calendar Mold
		if(groupwareUser != null && !Util.isEmpty(groupwareUser.getJP_DefaultCalendarView()))
		{
			String calendarView = groupwareUser.getJP_DefaultCalendarView();
			if(MGroupwareUser.JP_DEFAULTCALENDARVIEW_Month.equals(calendarView))
			{
				p_CalendarMold = GroupwareToDoUtil.CALENDAR_MONTH_VIEW;
				calendars.setMold("month");
				calendars.setDays(0);

			}else if(MGroupwareUser.JP_DEFAULTCALENDARVIEW_Week.equals(calendarView)){

				p_CalendarMold = GroupwareToDoUtil.CALENDAR_SEVENDAYS_VIEW;
				calendars.setMold("default");
				calendars.setDays(7);

			}else if(MGroupwareUser.JP_DEFAULTCALENDARVIEW_FiveDays.equals(calendarView)){

				p_CalendarMold = GroupwareToDoUtil.CALENDAR_FIVEDAYS_VIEW;
				calendars.setMold("default");
				calendars.setDays(5);
			}else if(MGroupwareUser.JP_DEFAULTCALENDARVIEW_Day.equals(calendarView)){

				p_CalendarMold = GroupwareToDoUtil.CALENDAR_ONEDAY_VIEW;
				calendars.setMold("default");
				calendars.setDays(1);
			}

		}else {

			p_CalendarMold  = GroupwareToDoUtil.CALENDAR_SEVENDAYS_VIEW;
			calendars.setMold("default");
			calendars.setDays(7);
		}

		return calendars;
    }

    private void initZk()
    {
    	form = new CustomForm();
    	Borderlayout mainBorderLayout = new Borderlayout();
    	form.appendChild(mainBorderLayout);

		ZKUpdateUtil.setWidth(mainBorderLayout, "99%");
		ZKUpdateUtil.setHeight(mainBorderLayout, "100%");


		//***************** NORTH **************************//

		North mainBorderLayout_North = new North();
		mainBorderLayout_North.setSplittable(false);
		mainBorderLayout_North.setCollapsible(false);
		mainBorderLayout_North.setOpen(true);
		mainBorderLayout.appendChild(mainBorderLayout_North);
		mainBorderLayout_North.appendChild(createNorthContents());


		//***************** CENTER **************************//

		mainBorderLayout_Center = new Center();
		mainBorderLayout.appendChild(mainBorderLayout_Center);
		mainBorderLayout_Center.appendChild(createCenterContents());//TODO

		//***************** WEST **************************//

		West mainBorderLayout_West = new West();
		mainBorderLayout_West.setSplittable(true);
		mainBorderLayout_West.setCollapsible(true);
		mainBorderLayout_West.setOpen(true);
		mainBorderLayout_West.setDroppable("true");
		ZKUpdateUtil.setWidth(mainBorderLayout_West, "25%");
		mainBorderLayout.appendChild(mainBorderLayout_West);
		mainBorderLayout_West.appendChild(createWestContents());


		Calendars cal = map_Calendars.get(p_SelectedTab_User_ID);
		setCalendarMold(cal.getDays());

    }


    private void getToDoCalendarEvents()
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
			orderClause = new StringBuilder("AD_User_ID, JP_ToDo_ScheduledStartTime");

			Timestamp timestamp_Begin = new Timestamp(map_Calendars.get(p_AD_User_ID).getBeginDate().getTime());
			Timestamp timestamp_End = new Timestamp(map_Calendars.get(p_AD_User_ID).getEndDate().getTime());

			LocalDateTime toDayMin = LocalDateTime.of(timestamp_Begin.toLocalDateTime().toLocalDate(), LocalTime.MIN);
			LocalDateTime toDayMax = LocalDateTime.of(timestamp_End.toLocalDateTime().toLocalDate(), LocalTime.MAX);

			list_parameters.add(Timestamp.valueOf(toDayMax));
			list_parameters.add(Timestamp.valueOf(toDayMin));

			//JP_TODO_TYPE_Schedule
			whereClauseSchedule = whereClauseSchedule.append(" AND JP_ToDo_Type = ? ");
			list_parameters.add(MToDo.JP_TODO_TYPE_Schedule);

			//Team
			if(p_JP_Team_ID==0)
			{
				whereClauseSchedule = whereClauseSchedule.append(" AND AD_User_ID = ? ");
				list_parameters.add(p_AD_User_ID);

			}else {

				whereClauseSchedule = whereClauseSchedule.append(" AND AD_User_ID IN (").append(createInUserClause(list_parameters)).append(")");

			}

			//Category
			if(p_JP_ToDo_Category_ID > 0)
			{
				whereClauseSchedule = whereClauseSchedule.append(" AND JP_ToDo_Category_ID = ? ");
				list_parameters.add(p_JP_ToDo_Category_ID);
			}

			//Status
			if(!Util.isEmpty(p_JP_ToDo_Status))
			{
				if(MGroupwareUser.JP_TODO_STATUS_NotCompleted.equals(p_JP_ToDo_Status))
				{
					whereClauseTask = whereClauseSchedule.append(" AND JP_ToDo_Status IN (?,?) ");
					list_parameters.add(MGroupwareUser.JP_TODO_STATUS_NotYetStarted);
					list_parameters.add(MGroupwareUser.JP_TODO_STATUS_WorkInProgress);
				}else {
					whereClauseSchedule = whereClauseSchedule.append(" AND JP_ToDo_Status = ? ");
					list_parameters.add(p_JP_ToDo_Status);
				}
			}

			if(p_login_User_ID == p_AD_User_ID && p_JP_Team_ID == 0)
			{
				//Noting to do;

			}else {
				whereClauseSchedule = whereClauseSchedule.append(" AND (IsOpenToDoJP='Y' OR CreatedBy = ?)");
				list_parameters.add(p_login_User_ID);
			}

    	}

		if(p_IsDisplayTask)
		{
			//JP_ToDo_ScheduledStartTime
			whereClauseTask = new StringBuilder(" JP_ToDo_ScheduledEndTime <= ? AND JP_ToDo_ScheduledEndTime >= ? AND IsActive='Y' ");//1 - 2
			orderClause = new StringBuilder("AD_User_ID, JP_ToDo_ScheduledEndTime");

			Timestamp timestamp_Begin = new Timestamp(map_Calendars.get(p_AD_User_ID).getBeginDate().getTime());
			Timestamp timestamp_End = new Timestamp(map_Calendars.get(p_AD_User_ID).getEndDate().getTime());

			LocalDateTime toDayMin = LocalDateTime.of(timestamp_Begin.toLocalDateTime().toLocalDate(), LocalTime.MIN);
			LocalDateTime toDayMax = LocalDateTime.of(timestamp_End.toLocalDateTime().toLocalDate(), LocalTime.MAX);

			list_parameters.add(Timestamp.valueOf(toDayMax));
			list_parameters.add(Timestamp.valueOf(toDayMin));

			//JP_TODO_TYPE_Schedule
			whereClauseTask = whereClauseTask.append(" AND JP_ToDo_Type = ? ");
			list_parameters.add(MToDo.JP_TODO_TYPE_Task);

			//Team
			if(p_JP_Team_ID==0)
			{
				whereClauseTask = whereClauseTask.append(" AND AD_User_ID = ? ");
				list_parameters.add(p_AD_User_ID);

			}else {

				whereClauseTask = whereClauseTask.append(" AND AD_User_ID IN (").append(createInUserClause(list_parameters)).append(")");
			}

			//Category
			if(p_JP_ToDo_Category_ID > 0)
			{
				whereClauseTask = whereClauseTask.append(" AND JP_ToDo_Category_ID = ? ");
				list_parameters.add(p_JP_ToDo_Category_ID);
			}

			//Status
			if(!Util.isEmpty(p_JP_ToDo_Status))
			{
				if(MGroupwareUser.JP_TODO_STATUS_NotCompleted.equals(p_JP_ToDo_Status))
				{
					whereClauseTask = whereClauseTask.append(" AND JP_ToDo_Status IN (?,?) ");
					list_parameters.add(MGroupwareUser.JP_TODO_STATUS_NotYetStarted);
					list_parameters.add(MGroupwareUser.JP_TODO_STATUS_WorkInProgress);

				}else {

					whereClauseTask = whereClauseTask.append(" AND JP_ToDo_Status = ? ");
					list_parameters.add(p_JP_ToDo_Status);
				}
			}

			if(p_login_User_ID == p_AD_User_ID && p_JP_Team_ID == 0)
			{
				//Noting to do;

			}else {
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

//		List<ToDoCalendarEvent> list_CalEvents = GroupwareToDoUtil.getToDoCalendarEvents(map_Calendars.get(p_AD_User_ID), p_JP_Team_ID > 0 ? true : false, whereClauseFinal.toString(), orderClause.toString(), parameters);

		List<MToDo> list_ToDoes = new Query(Env.getCtx(), MToDo.Table_Name, whereClauseFinal.toString(), null)
				.setParameters(parameters)
				.setOrderBy(orderClause.toString())
				.list();


		map_TaskCalendarEvent_Psersonal.clear();
		map_ScheduleCalendarEvent_Personal.clear();

		map_TaskCalendarEvent_Team.clear();
		map_ScheduleCalendarEvent_Team.clear();

		List<ToDoCalendarEvent> eventList = null;
		ToDoCalendarEvent event_Personal = null;
		ToDoCalendarEvent event_Team = null;

		for(MToDo todo :list_ToDoes)//TODO
		{
			event_Personal = new ToDoCalendarEvent(todo, map_Calendars.get(p_AD_User_ID), false);
			event_Team = new ToDoCalendarEvent(todo, map_Calendars.get(p_AD_User_ID), true);

			if(MToDo.JP_TODO_TYPE_Task.equals(todo.getJP_ToDo_Type()))
			{

				eventList = map_TaskCalendarEvent_Psersonal.get(event_Personal.getToDoD().getAD_User_ID());
				if(eventList == null)
				{
					eventList = new ArrayList<ToDoCalendarEvent>();
					eventList.add(event_Personal);
					map_TaskCalendarEvent_Psersonal.put(event_Personal.getToDoD().getAD_User_ID(), eventList);
				}else {
					eventList.add(event_Personal);
				}

				eventList = map_TaskCalendarEvent_Team.get(event_Team.getToDoD().getAD_User_ID());
				if(eventList == null)
				{
					eventList = new ArrayList<ToDoCalendarEvent>();
					eventList.add(event_Team);
					map_TaskCalendarEvent_Team.put(event_Team.getToDoD().getAD_User_ID(), eventList);
				}else {
					eventList.add(event_Team);
				}


			}else {

				eventList = map_ScheduleCalendarEvent_Personal.get(event_Personal.getToDoD().getAD_User_ID());
				if(eventList == null)
				{
					eventList = new ArrayList<ToDoCalendarEvent>();
					eventList.add(event_Personal);
					map_ScheduleCalendarEvent_Personal.put(event_Personal.getToDoD().getAD_User_ID(), eventList);
				}else {
					eventList.add(event_Personal);
				}

				eventList = map_ScheduleCalendarEvent_Team.get(event_Team.getToDoD().getAD_User_ID());
				if(eventList == null)
				{
					eventList = new ArrayList<ToDoCalendarEvent>();
					eventList.add(event_Team);
					map_ScheduleCalendarEvent_Team.put(event_Team.getToDoD().getAD_User_ID(), eventList);
				}else {
					eventList.add(event_Team);
				}

			}

		}//for

		return ;
    }



    private String createInUserClause(ArrayList<Object> list_parameters)
    {

    	StringBuilder users = new StringBuilder("?");
    	list_parameters.add(p_AD_User_ID);

    	String Q = ",?";
    	MTeamMember[] member = m_Team.getTeamMember();
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
    	Div outerDiv = new Div();
    	outerDiv.setStyle("padding:4px 2px 4px 2px; margin-bottom:4px; border: solid 2px #dddddd;");
    	Vlayout vlayout = new Vlayout();
    	outerDiv.appendChild(vlayout);


		Grid grid = GridFactory.newGridLayout();
		ZKUpdateUtil.setVflex(grid, "max");
		ZKUpdateUtil.setHflex(grid, "min");
		vlayout.appendChild(grid);

		Rows rows = grid.newRows();
		Row row = rows.newRow();

		row.appendChild(GroupwareToDoUtil.getDividingLine());

		//User Search
		MLookup lookupUser = MLookupFactory.get(ctx, 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_AD_User_ID),  DisplayType.Search);
		editor_AD_User_ID = new WSearchEditor(MToDo.COLUMNNAME_AD_User_ID, true, false, true, lookupUser);
		editor_AD_User_ID.setValue(p_AD_User_ID);
		editor_AD_User_ID.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_AD_User_ID.getComponent(), "true");

		label_AD_User_ID = new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_AD_User_ID));
		label_AD_User_ID.setId(MToDo.COLUMNNAME_AD_User_ID);
		label_AD_User_ID.addEventListener(Events.ON_CLICK, this);

		row.appendChild(GroupwareToDoUtil.createLabelDiv(editor_AD_User_ID, label_AD_User_ID, true));
		row.appendChild(editor_AD_User_ID.getComponent());
		editor_AD_User_ID.showMenu();


		row.appendChild(GroupwareToDoUtil.createSpaceDiv());


		//ToDo Category Search
		lookup_JP_ToDo_Category_ID = MLookupFactory.get(ctx, 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_ToDo_Category_ID),  DisplayType.Search);
		String validationCode = null;
		if(p_AD_User_ID == 0)
		{
			validationCode = "JP_ToDo_Category.AD_User_ID IS NULL";
		}else {
			validationCode = "JP_ToDo_Category.AD_User_ID IS NULL OR JP_ToDo_Category.AD_User_ID=" + p_AD_User_ID;
		}

		lookup_JP_ToDo_Category_ID.getLookupInfo().ValidationCode = validationCode;
		editor_JP_ToDo_Category_ID = new WSearchEditor(MToDo.COLUMNNAME_JP_ToDo_Category_ID, false, false, true, lookup_JP_ToDo_Category_ID);
		editor_JP_ToDo_Category_ID.setValue(null);
		editor_JP_ToDo_Category_ID.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_Category_ID.getComponent(), "true");


		label_JP_ToDo_Category_ID = new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Category_ID));
		label_JP_ToDo_Category_ID.addEventListener(Events.ON_CLICK, this);

		row.appendChild(GroupwareToDoUtil.createLabelDiv(editor_JP_ToDo_Category_ID, label_JP_ToDo_Category_ID, true));
		row.appendChild(editor_JP_ToDo_Category_ID.getComponent());
		editor_JP_ToDo_Category_ID.showMenu();

		row.appendChild(GroupwareToDoUtil.createSpaceDiv());

		//ToDo Status List
		MLookup lookup_JP_ToDo_Status = MLookupFactory.get(ctx, 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_ToDo_Status),  DisplayType.List);
		WTableDirEditor editor_JP_ToDo_Status = new WTableDirEditor(MToDo.COLUMNNAME_JP_ToDo_Status, false, false, true, lookup_JP_ToDo_Status);
		editor_JP_ToDo_Status.addValueChangeListener(this);
		//ZKUpdateUtil.setHflex(editor_JP_ToDo_Status.getComponent(), "true");

		row.appendChild(GroupwareToDoUtil.createLabelDiv(editor_JP_ToDo_Status, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Status), true));
		row.appendChild(editor_JP_ToDo_Status.getComponent());


		row.appendChild(GroupwareToDoUtil.createSpaceDiv());


		//Team Searh
		MLookup lookupTeam = MLookupFactory.get(ctx, 0,  0, MColumn.getColumn_ID(MToDoTeam.Table_Name, MTeam.COLUMNNAME_JP_Team_ID),  DisplayType.Search);
		editor_JP_Team_ID = new WSearchEditor( MTeam.COLUMNNAME_JP_Team_ID, false, false, true, lookupTeam);
		editor_JP_Team_ID.setValue(p_JP_Team_ID);
		editor_JP_Team_ID.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_Team_ID.getComponent(), "true");

		label_JP_Team_ID = new Label(Msg.getElement(ctx, MTeam.COLUMNNAME_JP_Team_ID));
		label_JP_Team_ID.addEventListener(Events.ON_CLICK, this);

		row.appendChild(GroupwareToDoUtil.createLabelDiv(editor_JP_Team_ID, label_JP_Team_ID, true));
		row.appendChild(editor_JP_Team_ID.getComponent());
		editor_JP_Team_ID.showMenu();


		row.appendChild(GroupwareToDoUtil.getDividingLine());
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());


		WYesNoEditor IsDisplaySchedule = new WYesNoEditor(MGroupwareUser.COLUMNNAME_IsDisplayScheduleJP, Msg.getElement(ctx,MGroupwareUser.COLUMNNAME_IsDisplayScheduleJP), null, true, false, true);
		IsDisplaySchedule.setValue(p_IsDisplaySchedule);
		IsDisplaySchedule.addValueChangeListener(this);
		row.appendChild(GroupwareToDoUtil.createEditorDiv(IsDisplaySchedule, true));


		WYesNoEditor IsDisplayTask = new WYesNoEditor(MGroupwareUser.COLUMNNAME_IsDisplayTaskJP, Msg.getElement(ctx,MGroupwareUser.COLUMNNAME_IsDisplayTaskJP), null, true, false, true);
		IsDisplayTask.setValue(p_IsDisplayTask);
		IsDisplayTask.addValueChangeListener(this);
		row.appendChild(GroupwareToDoUtil.createEditorDiv(IsDisplayTask, true));


		/******************** 2nd floor *********************************/

		grid = GridFactory.newGridLayout();
		ZKUpdateUtil.setVflex(grid, "max");
		ZKUpdateUtil.setHflex(grid, "min");
		vlayout.appendChild(grid);

		rows = grid.newRows();
		row = rows.newRow();


		row.appendChild(GroupwareToDoUtil.getDividingLine());

		Button createNewToDo = new Button();
		createNewToDo.setImage(ThemeManager.getThemeResource("images/New16.png"));
		createNewToDo.setName(GroupwareToDoUtil.BUTTON_NEW);
		createNewToDo.addEventListener(Events.ON_CLICK, this);
		createNewToDo.setId(String.valueOf(0));
		createNewToDo.setLabel(Msg.getMsg(ctx, "NewRecord"));
		row.appendCellChild(createNewToDo,2);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());


		Button refresh = new Button();
		refresh.setImage(ThemeManager.getThemeResource("images/Refresh16.png"));
		refresh.setName(GroupwareToDoUtil.BUTTON_REFRESH);
		refresh.addEventListener(Events.ON_CLICK, this);
		refresh.setLabel(Msg.getMsg(ctx, "Refresh"));
		row.appendCellChild(refresh, 2);


		row.appendChild(GroupwareToDoUtil.getDividingLine());


		Button oneDayView = new Button();
		oneDayView.setLabel(Msg.getMsg(ctx,"Day"));
		//oneDayView.setClass("btn-small");
		oneDayView.setName(GroupwareToDoUtil.CALENDAR_ONEDAY_VIEW);
		oneDayView.addEventListener(Events.ON_CLICK, this);
		row.appendChild(oneDayView);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());

		Button fivDayView = new Button();
		fivDayView.setLabel(Msg.getMsg(ctx,"5Days"));//
		//oneDayView.setClass("btn-small");
		fivDayView.setName(GroupwareToDoUtil.CALENDAR_FIVEDAYS_VIEW );
		fivDayView.addEventListener(Events.ON_CLICK, this);
		row.appendChild(fivDayView);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());

		Button sevenDayView = new Button();
		sevenDayView.setLabel(Msg.getMsg(ctx, "Week"));
		//sevenDayView.setClass("btn-small");
		sevenDayView.setName(GroupwareToDoUtil.CALENDAR_SEVENDAYS_VIEW);
		sevenDayView.addEventListener(Events.ON_CLICK, this);
		row.appendChild(sevenDayView);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());


		Button monthDayView = new Button();
		monthDayView.setLabel(Msg.getMsg(ctx, "Month"));
		//monthDayView.setClass("btn-small");
		monthDayView.setName(GroupwareToDoUtil.CALENDAR_MONTH_VIEW);
		monthDayView.addEventListener(Events.ON_CLICK, this);
		row.appendChild(monthDayView);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());


		row.appendChild(GroupwareToDoUtil.getDividingLine());


		Button leftBtn = new Button();
		leftBtn.setImage(ThemeManager.getThemeResource("images/MoveLeft16.png"));
		//leftBtn.setClass("btn-small");
		leftBtn.setName(GroupwareToDoUtil.BUTTON_PREVIOUS);
		leftBtn.addEventListener(Events.ON_CLICK, this);
		leftBtn.setLabel(" ");
		row.appendChild(leftBtn);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());

		Button today = new Button();
		today.setLabel(Msg.getMsg(ctx, "Today"));
		//today.setClass("btn-small");
		today.setName(GroupwareToDoUtil.BUTTON_TODAY);
		today.addEventListener(Events.ON_CLICK, this);
		row.appendChild(today);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());

		Button rightBtn = new Button();
		rightBtn.setImage(ThemeManager.getThemeResource("images/MoveRight16.png"));
		//rightBtn.setClass("btn-small");
		rightBtn.addEventListener(Events.ON_CLICK, this);
		rightBtn.setName(GroupwareToDoUtil.BUTTON_NEXT);
		rightBtn.setLabel(" ");
		row.appendChild(rightBtn);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());

		row.appendChild(GroupwareToDoUtil.getDividingLine());


		label_DisplayPeriod = new Label();
		updateDateLabel();

		row.appendChild(GroupwareToDoUtil.createLabelDiv(null,  Msg.getMsg(ctx, "JP_DisplayPeriod") + " : ", true));
		row.appendChild(GroupwareToDoUtil.createLabelDiv(null, label_DisplayPeriod, true));

		row.appendChild(GroupwareToDoUtil.getDividingLine());


		//Three Lines
		btn_ThreeLines = new Button();
		btn_ThreeLines.setImage(ThemeManager.getThemeResource("images/threelines.png"));
		//btn_ThreeLines.setClass("btn-small");
		btn_ThreeLines.addEventListener(Events.ON_CLICK, this);
		btn_ThreeLines.setName(GroupwareToDoUtil.BUTTON_THREE_LINES);
		btn_ThreeLines.setLabel(" ");
		row.appendChild(btn_ThreeLines);
		//ZKUpdateUtil.setHflex(btn_ThreeLines, "true");
		//row.appendCellChild(btn_ThreeLines);


		//First day ot week
		MLookup lookup_FirstDayOfWeek = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MGroupwareUser.Table_Name, MGroupwareUser.COLUMNNAME_JP_FirstDayOfWeek),  DisplayType.List);
		editor_JP_FirstDayOfWeek = new WTableDirEditor(MGroupwareUser.COLUMNNAME_JP_FirstDayOfWeek, false, false, true, lookup_FirstDayOfWeek);
		editor_JP_FirstDayOfWeek.setValue(p_JP_FristDayOfWeek);
		editor_JP_FirstDayOfWeek.addValueChangeListener(this);
		label_JP_FirstDayOfWeek = new Label(Msg.getElement(ctx, MGroupwareUser.COLUMNNAME_JP_FirstDayOfWeek));

		//JP_ToDo_Calendar_BeginTime
		editor_JP_ToDo_Calendar_BeginTime = new WNumberEditor(MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_BeginTime, false, false, true, DisplayType.Integer, "");
		if(groupwareUser != null)
			editor_JP_ToDo_Calendar_BeginTime.setValue(groupwareUser.getJP_ToDo_Calendar_BeginTime());
		else
			editor_JP_ToDo_Calendar_BeginTime.setValue(0);
		editor_JP_ToDo_Calendar_BeginTime.addValueChangeListener(this);
		label_JP_ToDo_Calendar_BeginTime = new Label(Msg.getElement(ctx, MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_BeginTime));

		//JP_ToDo_Calendar_EndTime
		editor_JP_ToDo_Calendar_EndTime = new WNumberEditor(MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_EndTime, false, false, true, DisplayType.Integer, "");
		if(groupwareUser != null)
			editor_JP_ToDo_Calendar_EndTime.setValue(groupwareUser.getJP_ToDo_Calendar_EndTime());
		else
			editor_JP_ToDo_Calendar_EndTime.setValue(0);
		editor_JP_ToDo_Calendar_EndTime.addValueChangeListener(this);
		label_JP_ToDo_Calendar_EndTime = new Label(Msg.getElement(ctx, MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_EndTime));


		MLookup lookup_Main_Calendar_View = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MGroupwareUser.Table_Name, MGroupwareUser.COLUMNNAME_JP_ToDo_Main_Calendar_View),  DisplayType.List);
		editor_JP_ToDo_Main_Calendar_View = new WTableDirEditor(MGroupwareUser.COLUMNNAME_JP_ToDo_Main_Calendar_View, false, false, true, lookup_Main_Calendar_View);
		editor_JP_ToDo_Main_Calendar_View.setValue(p_JP_ToDo_Main_Calendar_View);
		editor_JP_ToDo_Main_Calendar_View.addValueChangeListener(this);
		label_JP_ToDo_Main_Calendar_View = new Label(Msg.getElement(ctx, MGroupwareUser.COLUMNNAME_JP_ToDo_Main_Calendar_View));

    	return outerDiv;

    }

    public Div createCenterContents()//TODO
    {
    	Div outerDiv = new Div();
    	outerDiv.setHeight("100%");

		if(tabbox != null)
		{
			tabbox.getTabs().detach();
			tabbox.getTabpanels().detach();
			tabbox.setParent(outerDiv);

		}else {

    		tabbox = new Tabbox();
    		tabbox.setParent(outerDiv);
    		ZKUpdateUtil.setWidth(tabbox, "100%");
    		ZKUpdateUtil.setHeight(tabbox, "100%");
    		ZKUpdateUtil.setHflex(tabbox, "1");
    		ZKUpdateUtil.setVflex(tabbox, "1");

    		tab_p_AD_User_ID  = new Tab(MUser.get(ctx, p_AD_User_ID).getName());
    		tab_p_AD_User_ID.setAttribute("AD_User_ID", p_AD_User_ID);
    		tab_p_AD_User_ID.setImage(ThemeManager.getThemeResource("images/BPartner16.png"));
    		tab_p_AD_User_ID.setClosable(false);
    		tab_p_AD_User_ID.addEventListener(Events.ON_CLICK, this);

    		tabpanel_p_AD_User_ID = new Tabpanel();
    		tabpanel_p_AD_User_ID.setAttribute("AD_User_ID", p_AD_User_ID);
    		tabpanel_p_AD_User_ID.appendChild(map_Calendars.get(p_AD_User_ID));
		}

		Tabs tabs = new Tabs();
		tabbox.appendChild(tabs);
		tabs.appendChild(tab_p_AD_User_ID);

		Tabpanels tabpanels = new Tabpanels();
		tabbox.appendChild(tabpanels);
		tabpanels.appendChild(tabpanel_p_AD_User_ID);


		if(p_JP_Team_ID != 0)
		{
			MTeamMember[] menbers = m_Team.getTeamMember();
			Tab tab = null;
			Tabpanel tabpanel = null;
		  	for(int i = 0; i < menbers.length; i++)
	    	{
	    		if(p_AD_User_ID != menbers[i].getAD_User_ID())
	    		{
	    			tab = new Tab(MUser.get(ctx, menbers[i].getAD_User_ID()).getName());
	    			tab.setAttribute("AD_User_ID", menbers[i].getAD_User_ID());
	    			tab.setClosable(true);
	    			tab.addEventListener(Events.ON_CLOSE, this);//onClose
	    			tab.addEventListener(Events.ON_CLICK, this);
	    			tabs.appendChild(tab);

	    			tabpanel = new Tabpanel();
	    			tabpanel.setAttribute("AD_User_ID", menbers[i].getAD_User_ID());
	    			tabpanels.appendChild(tabpanel);
	    		}//if

	    	}//for

		}//if(p_JP_Team_ID != 0)

    	return outerDiv;
    }

    public Div createWestContents()
    {
    	Div div = new Div();
		Vlayout vlayout = new Vlayout();
		vlayout.setDroppable("false");
		div.appendChild(vlayout);


		//Menu
		Groupbox groupBox0 = new Groupbox();
		groupBox0.setOpen(false);
		groupBox0.setDraggable("false");
		groupBox0.setMold("3d");
		groupBox0.setWidgetListener("onOpen", "this.caption.setIconSclass('z-icon-caret-' + (event.open ? 'down' : 'right'));");
		vlayout.appendChild(groupBox0);

		Caption caption0 = new Caption(Msg.getMsg(ctx, "Menu"));
		caption0.setIconSclass("z-icon-caret-right");
		groupBox0.appendChild(caption0);

		GroupwareMenuGadget toDoMenu = new GroupwareMenuGadget();
		groupBox0.appendChild(toDoMenu);


		//Schedule
		Groupbox groupBox1 = new Groupbox();
		groupBox1.setOpen(true);
		groupBox1.setDraggable("false");
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
		groupBox2.setDraggable("false");
		groupBox2.setMold("3d");
		groupBox2.setWidgetListener("onOpen", "this.caption.setIconSclass('z-icon-caret-' + (event.open ? 'down' : 'right'));");
		vlayout.appendChild(groupBox2);

		Caption caption2 = new Caption(Msg.getMsg(ctx, "JP_UnfinishedTasks"));//Unfinished Tasks
		caption2.setIconSclass("z-icon-caret-down");
		groupBox2.appendChild(caption2);

		personalToDoGadget_Task = new JPierePersonalToDoGadget(MToDo.JP_TODO_TYPE_Task);
		groupBox2.appendChild(personalToDoGadget_Task);


		//Unfinished Memo
		Groupbox groupBox3 = new Groupbox();
		groupBox3.setOpen(true);
		groupBox3.setDraggable("false");
		groupBox3.setMold("3d");
		groupBox3.setWidgetListener("onOpen", "this.caption.setIconSclass('z-icon-caret-' + (event.open ? 'down' : 'right'));");
		vlayout.appendChild(groupBox3);

		Caption caption3 = new Caption(Msg.getMsg(ctx, "JP_UnfinishedMemo"));//Unfinished Memo
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
				editor_AD_User_ID.setValue(null);

			}else {
				p_AD_User_ID = Integer.parseInt(value.toString());
			}

			p_OldSelectedTab_User_ID = p_SelectedTab_User_ID;
			p_SelectedTab_User_ID = p_AD_User_ID;
			map_Calendars.clear();
			map_Calendars.put(p_AD_User_ID, createInitialCalendar());


			String validationCode = null;
			if(evt.getNewValue()==null)
			{
				validationCode = "JP_ToDo_Category.AD_User_ID IS NULL";
			}else {
				validationCode = "JP_ToDo_Category.AD_User_ID IS NULL OR JP_ToDo_Category.AD_User_ID=" + (Integer)evt.getNewValue();
			}

			lookup_JP_ToDo_Category_ID.getLookupInfo().ValidationCode = validationCode;
			editor_JP_ToDo_Category_ID.setValue(null);

			int old_JP_ToDo_Category_ID = p_JP_ToDo_Category_ID;
			p_JP_ToDo_Category_ID = 0;

			if(p_AD_User_ID == 0)
			{
				Object obj = evt.getSource();
				if(obj instanceof WEditor)
				{
					WEditor editor = (WEditor)obj;
					String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_AD_User_ID);
					throw new WrongValueException(editor.getComponent(), msg);
				}

			}else if(old_JP_ToDo_Category_ID > 0) {

				throw new WrongValueException(editor_JP_ToDo_Category_ID.getComponent(), "ユーザーが変更になったのでカテゴリがリフレッシュされました。");//TODO:

			}

			refresh(null);

			tabbox = null;

		  	mainBorderLayout_Center.getFirstChild().detach();
			mainBorderLayout_Center.appendChild(createCenterContents());


		}else if(MToDo.COLUMNNAME_JP_ToDo_Category_ID.equals(name)){

			if(value == null)
			{
				p_JP_ToDo_Category_ID = 0;
			}else {
				p_JP_ToDo_Category_ID = Integer.parseInt(value.toString());
			}

			refresh();

		}else if(MTeam.COLUMNNAME_JP_Team_ID.equals(name)){//TODO

			if(value == null)
			{
				p_JP_Team_ID = 0;
				m_Team = null;
				tab_p_AD_User_ID.setLabel(MUser.get(ctx, p_AD_User_ID).getName());

			}else {

				p_JP_Team_ID = Integer.parseInt(value.toString());
				m_Team = new MTeam(ctx, p_JP_Team_ID, null);
				tab_p_AD_User_ID.setLabel(MUser.get(ctx, p_AD_User_ID).getName() + " & "  + Msg.getElement(ctx, MTeam.COLUMNNAME_JP_Team_ID));

			}

			Calendars from = map_Calendars.get(p_SelectedTab_User_ID);
			Calendars to = map_Calendars.get(p_AD_User_ID);
			map_Calendars.clear();
			map_Calendars.put(p_AD_User_ID, syncCalendars(from, to));

			p_OldSelectedTab_User_ID = p_SelectedTab_User_ID;
			p_SelectedTab_User_ID = p_AD_User_ID;

			refresh();

		  	mainBorderLayout_Center.getFirstChild().detach();
			mainBorderLayout_Center.appendChild(createCenterContents());


			//refresh();

		}else if(MGroupwareUser.COLUMNNAME_IsDisplayScheduleJP.equals(name)) {

			p_IsDisplaySchedule = (boolean)value;
			refresh();

		}else if(MGroupwareUser.COLUMNNAME_IsDisplayTaskJP.equals(name)) {

			p_IsDisplayTask = (boolean)value;
			refresh();

		}else if(MToDo.COLUMNNAME_JP_ToDo_Status.equals(name)){

			if(value == null)
			{
				p_JP_ToDo_Status = null;
			}else {

				if(Util.isEmpty(value.toString()))
				{
					p_JP_ToDo_Status = null;
				}else {
					p_JP_ToDo_Status = value.toString();
				}
			}

			refresh();

		}else if(MGroupwareUser.COLUMNNAME_JP_FirstDayOfWeek.equals(name)){

			editor_JP_FirstDayOfWeek.setValue(value.toString());

			int AD_Column_ID = MColumn.getColumn_ID(MGroupwareUser.Table_Name, MGroupwareUser.COLUMNNAME_JP_FirstDayOfWeek);
			int AD_Reference_Value_ID = MColumn.get(ctx, AD_Column_ID).getAD_Reference_Value_ID();
			MRefList refList =MRefList.get(ctx, AD_Reference_Value_ID, value.toString(),null);

			map_Calendars.get(p_AD_User_ID).setFirstDayOfWeek(refList.getName());

			updateDateLabel();
			refresh();

		}else if(MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_BeginTime.equals(name)){//TODO

			if(value == null)
			{
				String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_BeginTime);//。
				throw new WrongValueException(editor_JP_ToDo_Calendar_BeginTime.getComponent(), msg);
			}

			int beginTime = ((Integer)value).intValue();
			editor_JP_ToDo_Calendar_BeginTime.setValue(beginTime);

			if(beginTime < 0)
			{

				String msg = Msg.getMsg(Env.getCtx(), "LessThanMinValue", new Object[] {0}) ;
				throw new WrongValueException(editor_JP_ToDo_Calendar_BeginTime.getComponent(), msg);
			}

			if(beginTime >= 24)
			{
				String msg = Msg.getMsg(Env.getCtx(), "MoreThanMaxValue", new Object[] {23});
				throw new WrongValueException(editor_JP_ToDo_Calendar_BeginTime.getComponent(), msg);
			}


			Object obj_EndTime = editor_JP_ToDo_Calendar_EndTime.getValue();
			if(obj_EndTime == null)
			{
				String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_EndTime);
				throw new WrongValueException(editor_JP_ToDo_Calendar_EndTime.getComponent(), msg);
			}

			int endTime =  ((BigDecimal)obj_EndTime).intValue();
			if(beginTime >= endTime)
			{
				String msg = Msg.getElement(Env.getCtx(), MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_BeginTime) + " >= " + Msg.getElement(Env.getCtx(), MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_EndTime);
				throw new WrongValueException(editor_JP_ToDo_Calendar_BeginTime.getComponent(), msg);
			}

			map_Calendars.get(p_AD_User_ID).setBeginTime(beginTime);
			createThreeLinesPopup();

		}else if(MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_EndTime.equals(name)){

			if(value == null)
			{
				String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_EndTime);//。
				throw new WrongValueException(editor_JP_ToDo_Calendar_EndTime.getComponent(), msg);
			}

			int endTime = ((Integer)value).intValue();
			editor_JP_ToDo_Calendar_EndTime.setValue(endTime);

			if(endTime < 1)
			{

				String msg = Msg.getMsg(Env.getCtx(), "LessThanMinValue", new Object[] {1}) ;
				throw new WrongValueException(editor_JP_ToDo_Calendar_EndTime.getComponent(), msg);
			}

			if(endTime > 24)
			{
				String msg = Msg.getMsg(Env.getCtx(), "MoreThanMaxValue", new Object[] {24});
				throw new WrongValueException(editor_JP_ToDo_Calendar_EndTime.getComponent(), msg);
			}


			Object obj_BeginTime = editor_JP_ToDo_Calendar_BeginTime.getValue();
			if(obj_BeginTime == null)
			{
				String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_BeginTime);
				throw new WrongValueException(editor_JP_ToDo_Calendar_BeginTime.getComponent(), msg);
			}

			int beginTime =  ((BigDecimal)obj_BeginTime).intValue();
			if(beginTime >= endTime)
			{
				String msg = Msg.getElement(Env.getCtx(), MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_BeginTime) + " >= " + Msg.getElement(Env.getCtx(), MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_EndTime);
				throw new WrongValueException(editor_JP_ToDo_Calendar_EndTime.getComponent(), msg);
			}

			map_Calendars.get(p_AD_User_ID).setEndTime(endTime);
			createThreeLinesPopup();
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
					map_Calendars.get(p_SelectedTab_User_ID).previousPage();

					updateDateLabel();
//					refresh();

				}else if(GroupwareToDoUtil.BUTTON_NEXT.equals(btnName)){

					map_Calendars.get(p_SelectedTab_User_ID).nextPage();

					updateDateLabel();
//					refresh();

				}else if(GroupwareToDoUtil.BUTTON_REFRESH.equals(btnName)){

					refresh(null);

				}else if(GroupwareToDoUtil.BUTTON_TODAY.equals(btnName)){

					map_Calendars.get(p_SelectedTab_User_ID).setCurrentDate(Calendar.getInstance(map_Calendars.get(p_SelectedTab_User_ID).getDefaultTimeZone()).getTime());

					updateDateLabel();
//					refresh();

				}else if(GroupwareToDoUtil.CALENDAR_ONEDAY_VIEW.equals(btnName)){

					p_CalendarMold = GroupwareToDoUtil.CALENDAR_ONEDAY_VIEW;
					setCalendarMold(1);
					updateDateLabel();
					refresh();

				}else if(GroupwareToDoUtil.CALENDAR_FIVEDAYS_VIEW.equals(btnName)){

					p_CalendarMold = GroupwareToDoUtil.CALENDAR_FIVEDAYS_VIEW;
					setCalendarMold(5);

					updateDateLabel();
					refresh();

				}else if(GroupwareToDoUtil.CALENDAR_SEVENDAYS_VIEW.equals(btnName)){

					p_CalendarMold = GroupwareToDoUtil.CALENDAR_SEVENDAYS_VIEW;
					setCalendarMold(7);

					updateDateLabel();
					refresh();

				}else if(GroupwareToDoUtil.CALENDAR_MONTH_VIEW.equals(btnName)){

					p_CalendarMold = GroupwareToDoUtil.CALENDAR_MONTH_VIEW;
					setCalendarMold(0);

					updateDateLabel();
					refresh();

				}else if(GroupwareToDoUtil.BUTTON_THREE_LINES.equals(btnName)){

					createThreeLinesPopup();

					return;
				}

			}else if(comp instanceof Label){

				//Zoom AD_User_ID -> Groupware User window
				if(label_AD_User_ID.equals(comp))
				{
					Object value = editor_AD_User_ID.getValue();
					if(value == null || Util.isEmpty(value.toString()))
					{
						AEnv.zoom(MTable.getTable_ID(MGroupwareUser.Table_Name), 0);
					}else {

						if(groupwareUser == null)
						{
							AEnv.zoom(MTable.getTable_ID(MGroupwareUser.Table_Name), 0);

						}else {

							MGroupwareUser gUser = MGroupwareUser.get(ctx, Integer.valueOf(value.toString()));
							AEnv.zoom(MTable.getTable_ID(MGroupwareUser.Table_Name), gUser.getJP_GroupwareUser_ID());
						}
					}

				}else if(label_JP_ToDo_Category_ID.equals(comp)) {

					Object value = editor_JP_Team_ID.getValue();
					if(value == null || Util.isEmpty(value.toString()))
					{
						AEnv.zoom(MTable.getTable_ID(MTeam.Table_Name), 0);
					}else {
						AEnv.zoom(MTable.getTable_ID(MTeam.Table_Name), Integer.valueOf(value.toString()));
					}

				}else if(label_JP_Team_ID.equals(comp)) {

					Object value = editor_JP_ToDo_Category_ID.getValue();
					if(value == null || Util.isEmpty(value.toString()))
					{
						AEnv.zoom(MTable.getTable_ID(MToDoCategory.Table_Name), 0);
					}else {
						AEnv.zoom(MTable.getTable_ID(MToDoCategory.Table_Name), Integer.valueOf(value.toString()));
					}
				}

			}else if(comp instanceof Tab) {//TODO

				Object obj_AD_User_ID = comp.getAttribute("AD_User_ID");
				int AD_User_ID = Integer.valueOf(obj_AD_User_ID.toString());
				p_OldSelectedTab_User_ID = p_SelectedTab_User_ID;
				p_SelectedTab_User_ID = AD_User_ID;

				Tabpanel tabpanel = tabbox.getTabpanel(tabbox.getSelectedIndex());

				if(tabpanel.getFirstChild() == null)
				{

					Calendars  calendars = createSyncCalendars(map_Calendars.get(p_OldSelectedTab_User_ID));
					map_Calendars.put(p_SelectedTab_User_ID, calendars);
					tabpanel.appendChild(calendars);

					SimpleCalendarModel scm =null;
					CalendarModel  cm = calendars.getModel();
					if(cm == null)
					{
						scm = new SimpleCalendarModel();
					}else {
						scm = (SimpleCalendarModel)cm;
					}

					scm.clear();

					List<ToDoCalendarEvent> list_CalEvents = null;
					list_CalEvents = map_ScheduleCalendarEvent_Personal.get(p_SelectedTab_User_ID);
					if(list_CalEvents != null)
					{
						for (ToDoCalendarEvent evt : list_CalEvents)
							scm.add(evt);
					}

					list_CalEvents = map_TaskCalendarEvent_Psersonal.get(p_SelectedTab_User_ID);
					if(list_CalEvents != null)
					{
						for (ToDoCalendarEvent evt : list_CalEvents)
							scm.add(evt);
					}

					calendars.setModel(scm);

				}else {

					syncCalendars(map_Calendars.get(p_OldSelectedTab_User_ID), map_Calendars.get(p_SelectedTab_User_ID));
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

			p_CalendarMold = GroupwareToDoUtil.CALENDAR_ONEDAY_VIEW;
			setCalendarMold(1);
			updateDateLabel();
			refresh();

		}else if (GroupwareToDoUtil.CALENDAR_EVENT_WEEK.equals(eventName)){

			//I don't know this Event

		}else if (Events.ON_CLOSE.equals(eventName)) {

			int a = 1+1;

			int b = a+1;


		}
	}


	private Calendars createSyncCalendars(Calendars from)
	{
		Calendars to = new Calendars();


		to.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_CREATE, this);
		to.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_EDIT, this);
		to.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_UPDATE,this);
//		to.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_MOUSE_OVER, this);
		to.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_DAY,this);
//		to.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_WEEK, this);

		return syncCalendars(from, to);

	}

	private Calendars syncCalendars(Calendars from, Calendars to)
	{
		to.setMold(from.getMold());
		to.setDays(from.getDays());

		to.setCurrentDate(from.getCurrentDate());

		to.setBeginTime(from.getBeginTime());
		to.setEndTime(from.getEndTime());

		to.setFirstDayOfWeek(from.getFirstDayOfWeek());
		to.invalidate();

		return to;
	}

	Popup popup = null;
	private void createThreeLinesPopup()
	{
		Grid grid = null;
		if(popup == null)
		{
			popup = new Popup();
			popup.setWidgetAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "processButtonPopup");
			grid = GridFactory.newGridLayout();
			ZKUpdateUtil.setVflex(grid, "min");
			ZKUpdateUtil.setHflex(grid, "min");
			popup.appendChild(grid);
		}else {
			grid =(Grid)popup.getFirstChild();
			grid.detach();
			grid = GridFactory.newGridLayout();
			ZKUpdateUtil.setVflex(grid, "min");
			ZKUpdateUtil.setHflex(grid, "min");
			popup.appendChild(grid);
		}

		Rows rows = grid.newRows();
		Row row = rows.newRow();

		row.appendChild(GroupwareToDoUtil.createLabelDiv(editor_JP_FirstDayOfWeek, label_JP_FirstDayOfWeek, true));
		row.appendChild(editor_JP_FirstDayOfWeek.getComponent());

		row = rows.newRow();
		row.appendChild(GroupwareToDoUtil.createLabelDiv(editor_JP_ToDo_Calendar_BeginTime, label_JP_ToDo_Calendar_BeginTime, true));
		row.appendChild(editor_JP_ToDo_Calendar_BeginTime.getComponent());
		row = rows.newRow();

		row.appendChild(GroupwareToDoUtil.createLabelDiv(editor_JP_ToDo_Calendar_EndTime, label_JP_ToDo_Calendar_EndTime, true));
		row.appendChild(editor_JP_ToDo_Calendar_EndTime.getComponent());

		row = rows.newRow();
		row.appendChild(GroupwareToDoUtil.createLabelDiv(editor_JP_ToDo_Main_Calendar_View, label_JP_ToDo_Main_Calendar_View, true));
		row.appendChild(editor_JP_ToDo_Main_Calendar_View.getComponent());

		popup.setPage(btn_ThreeLines.getPage());
		popup.open(btn_ThreeLines, "after_start");

	}

	private void setCalendarMold(int days)
	{
		Calendars calendars = map_Calendars.get(p_SelectedTab_User_ID);
		if (days == 7)
		{
			calendars.setMold("default");
			calendars.setDays(days);
			editor_JP_FirstDayOfWeek.setVisible(true);
			label_JP_FirstDayOfWeek.setVisible(true);

			editor_JP_ToDo_Calendar_BeginTime.setVisible(true);
			label_JP_ToDo_Calendar_BeginTime.setVisible(true);
			editor_JP_ToDo_Calendar_EndTime.setVisible(true);
			label_JP_ToDo_Calendar_EndTime.setVisible(true);
		}
		else  if (days > 0)
		{
			calendars.setMold("default");
			calendars.setDays(days);
			editor_JP_FirstDayOfWeek.setVisible(false);
			label_JP_FirstDayOfWeek.setVisible(false);

			editor_JP_ToDo_Calendar_BeginTime.setVisible(true);
			label_JP_ToDo_Calendar_BeginTime.setVisible(true);
			editor_JP_ToDo_Calendar_EndTime.setVisible(true);
			label_JP_ToDo_Calendar_EndTime.setVisible(true);

		} else {

			calendars.setMold("month");
			editor_JP_FirstDayOfWeek.setVisible(true);
			label_JP_FirstDayOfWeek.setVisible(true);

			editor_JP_ToDo_Calendar_BeginTime.setVisible(false);
			label_JP_ToDo_Calendar_BeginTime.setVisible(false);
			editor_JP_ToDo_Calendar_EndTime.setVisible(false);
			label_JP_ToDo_Calendar_EndTime.setVisible(false);
		}

	}

	private Label label_DisplayPeriod;
	private void updateDateLabel()
	{
		Date b = map_Calendars.get(p_SelectedTab_User_ID).getBeginDate();
		Date e = map_Calendars.get(p_SelectedTab_User_ID).getEndDate();

		LocalDateTime local = new Timestamp(e.getTime()).toLocalDateTime();
		e = new Date(Timestamp.valueOf(local.minusDays(1)).getTime());

		SimpleDateFormat sdfV = DisplayType.getDateFormat();
		//sdfV.setTimeZone(calendars.getDefaultTimeZone());

		label_DisplayPeriod.setValue(sdfV.format(b) + " - " + sdfV.format(e));
	}


	List<MToDo> list_ToDoes = null;

	@Override
	public List<MToDo> getPersonalToDoList()
	{
		return list_ToDoes;
	}


	@Override
	public int getDefault_AD__User_ID()
	{
		return p_SelectedTab_User_ID;
	}


	@Override
	public int getDefault_JP_ToDo_Category_ID()
	{
		return p_JP_ToDo_Category_ID;
	}


	@Override
	public String getDefault_JP_ToDo_Type()
	{
		if(list_ToDoes == null)
		{
			if(p_IsDisplaySchedule && !p_IsDisplayTask)
			{
				return MToDo.JP_TODO_TYPE_Schedule;

			}else if (!p_IsDisplaySchedule && p_IsDisplayTask) {

				return MToDo.JP_TODO_TYPE_Task;

			}else if (!p_IsDisplaySchedule && !p_IsDisplayTask) {

				return MToDo.JP_TODO_TYPE_Memo;

			}

			return MToDo.JP_TODO_TYPE_Schedule;
		}else {

			return list_ToDoes.get(0).getJP_ToDo_Type();
		}

	}


	private void refresh()
	{

		getToDoCalendarEvents();

		if(MGroupwareUser.JP_TODO_MAIN_CALENDAR_VIEW_Personal.equals(p_JP_ToDo_Main_Calendar_View) || p_JP_Team_ID == 0)
		{
			SimpleCalendarModel scm =null;
			CalendarModel  cm = map_Calendars.get(p_AD_User_ID).getModel();
			if(cm == null)
			{
				scm = new SimpleCalendarModel();
			}else {
				scm = (SimpleCalendarModel)cm;
			}

			scm.clear();

			if(!p_IsDisplaySchedule && !p_IsDisplayTask)
			{
				map_Calendars.get(p_AD_User_ID).setModel(scm);

			}else {

				if(p_JP_Team_ID == 0)
				{
					List<ToDoCalendarEvent> list_CalEvents = null;
					list_CalEvents = map_ScheduleCalendarEvent_Personal.get(p_AD_User_ID);
					if(list_CalEvents != null)
					{
						for (ToDoCalendarEvent event : list_CalEvents)
							scm.add(event);
					}

					list_CalEvents = map_TaskCalendarEvent_Psersonal.get(p_AD_User_ID);
					if(list_CalEvents != null)
					{
						for (ToDoCalendarEvent event : list_CalEvents)
							scm.add(event);
					}

				}else {

					List<ToDoCalendarEvent> list_CalEvents = null;
					MTeamMember[] members = m_Team.getTeamMember();
					for(int i = 0; i < members.length; i++)
					{

						list_CalEvents = map_ScheduleCalendarEvent_Personal.get(members[i].getAD_User_ID());
						if(list_CalEvents != null)
						{
							for (ToDoCalendarEvent event : list_CalEvents)
								scm.add(event);
						}

						list_CalEvents = map_TaskCalendarEvent_Psersonal.get(members[i].getAD_User_ID());
						if(list_CalEvents != null)
						{
							for (ToDoCalendarEvent event : list_CalEvents)
								scm.add(event);
						}
					}//for
				}
			}

			map_Calendars.get(p_AD_User_ID).setModel(scm);
			//createCenterContents();

		}else {

			if(!p_IsDisplaySchedule && !p_IsDisplayTask)
			{
				;//TODO 要実装

			}else {

				int tabindex = tabbox.getSelectedIndex();

				Tabpanel panel =tabbox.getTabpanel(tabindex);


				Object obj_AD_User_ID = panel.getAttribute("AD_User_ID");
				int AD_User_ID = Integer.valueOf(obj_AD_User_ID.toString());

				List<ToDoCalendarEvent> list_CalEvents = null;
				SimpleCalendarModel scm =null;
				CalendarModel  cm = map_Calendars.get(AD_User_ID).getModel();
				if(cm == null)
				{
					scm = new SimpleCalendarModel();
				}else {
					scm = (SimpleCalendarModel)cm;
				}

				scm.clear();

				if(MGroupwareUser.JP_TODO_MAIN_CALENDAR_VIEW_Team.equals(p_JP_ToDo_Main_Calendar_View))
				{
					list_CalEvents = map_ScheduleCalendarEvent_Team.get(p_AD_User_ID);
					if(list_CalEvents != null)
					{
						for (ToDoCalendarEvent event : list_CalEvents)
							scm.add(event);
					}

					list_CalEvents = map_TaskCalendarEvent_Team.get(p_AD_User_ID);
					if(list_CalEvents != null)
					{
						for (ToDoCalendarEvent event : list_CalEvents)
							scm.add(event);
					}

					MTeamMember[] members = m_Team.getTeamMember();
					for(int i = 0; i < members.length; i++)
					{
						if(p_AD_User_ID == members[i].getAD_User_ID())
							continue;

						list_CalEvents = map_ScheduleCalendarEvent_Team.get(members[i].getAD_User_ID());
						if(list_CalEvents != null)
						{
							for (ToDoCalendarEvent event : list_CalEvents)
								scm.add(event);
						}

						list_CalEvents = map_TaskCalendarEvent_Team.get(members[i].getAD_User_ID());
						if(list_CalEvents != null)
						{
							for (ToDoCalendarEvent event : list_CalEvents)
								scm.add(event);
						}
					}

				}else {

					list_CalEvents = map_ScheduleCalendarEvent_Personal.get(p_AD_User_ID);
					if(list_CalEvents != null)
					{
						for (ToDoCalendarEvent event : list_CalEvents)
							scm.add(event);
					}

					list_CalEvents = map_TaskCalendarEvent_Psersonal.get(p_AD_User_ID);
					if(list_CalEvents != null)
					{
						for (ToDoCalendarEvent event : list_CalEvents)
							scm.add(event);
					}

				}

				map_Calendars.get(p_AD_User_ID).setModel(scm);

			}

		}

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
	public Timestamp getDefault_JP_ToDo_ScheduledStartTime()
	{
		Timestamp timestamp = null;
		if(p_CalendarsEventBeginDate == null)
		{
			timestamp = new Timestamp(map_Calendars.get(p_AD_User_ID).getCurrentDate().getTime());
			LocalDateTime ldt = timestamp.toLocalDateTime();

			return Timestamp.valueOf(LocalDateTime.of(ldt.toLocalDate(), LocalTime.MIN));


		}else {

			timestamp = p_CalendarsEventBeginDate;

			return Timestamp.valueOf(LocalDateTime.of(timestamp.toLocalDateTime().toLocalDate(), timestamp.toLocalDateTime().toLocalTime()));
		}


	}

	private Timestamp p_CalendarsEventEndDate = null;

	@Override
	public Timestamp getDefault_JP_ToDo_ScheduledEndTime()
	{
		Timestamp timestamp = null;
		if(p_CalendarsEventEndDate == null)
		{
			timestamp = new Timestamp(map_Calendars.get(p_AD_User_ID).getCurrentDate().getTime());
			LocalDateTime ldt = timestamp.toLocalDateTime();

			return Timestamp.valueOf(LocalDateTime.of(ldt.toLocalDate(), LocalTime.MIN));

		}else {

			timestamp =  p_CalendarsEventEndDate;

			if(GroupwareToDoUtil.CALENDAR_MONTH_VIEW.equals(p_CalendarMold))
			{
				timestamp = p_CalendarsEventBeginDate;

			}else if(GroupwareToDoUtil.CALENDAR_SEVENDAYS_VIEW.equals(p_CalendarMold) || GroupwareToDoUtil.CALENDAR_ONEDAY_VIEW.equals(p_CalendarMold)
																				|| GroupwareToDoUtil.CALENDAR_FIVEDAYS_VIEW.equals(p_CalendarMold) ) {

				LocalTime start = p_CalendarsEventBeginDate.toLocalDateTime().toLocalTime();
				LocalTime end = p_CalendarsEventEndDate.toLocalDateTime().toLocalTime();

				if(start.compareTo(LocalTime.MIN) == 0 && end.compareTo(LocalTime.MIN) == 0)
				{
					timestamp = p_CalendarsEventBeginDate;
				}

			}

			return Timestamp.valueOf(LocalDateTime.of(timestamp.toLocalDateTime().toLocalDate(), timestamp.toLocalDateTime().toLocalTime()));
		}


	}


	@Override
	public List<MToDoTeam> getTeamToDoList()
	{
		return null;
	}
}
