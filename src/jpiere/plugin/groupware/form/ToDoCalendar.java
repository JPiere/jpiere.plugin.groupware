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
import java.util.Set;

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
import org.adempiere.webui.window.FDialog;
import org.compiere.model.MColumn;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MRefList;
import org.compiere.model.MSysConfig;
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
import jpiere.plugin.groupware.window.I_ToDoCalendarEventReceiver;
import jpiere.plugin.groupware.window.I_ToDoPopupwindowCaller;
import jpiere.plugin.groupware.window.PersonalToDoPopupWindow;;

/**
 *
 * JPIERE-0471: ToDo Calendar
 *
 * h.hagiwara
 *
 */
public class ToDoCalendar implements I_ToDoPopupwindowCaller, I_ToDoCalendarEventReceiver, IFormController, EventListener<Event>, ValueChangeListener {

	//private static CLogger log = CLogger.getCLogger(ToDoCalendar.class);

	private CustomForm form;

	private Properties ctx = Env.getCtx();

	@Override
	public ADForm getForm()
	{
		return form;
	}

	/** ToDo Controler **/
	//HashMap<AD_User_ID, Calendars>
	private HashMap<Integer,Calendars> 	map_Calendars = new HashMap<Integer,Calendars>();

	//HashMap<AD_User_ID, HashMap<JP_ToDo_ID, CalendarEvent>>
	private HashMap<Integer,HashMap<Integer,ToDoCalendarEvent>> map_CalendarEvent_User = new HashMap<Integer,HashMap<Integer,ToDoCalendarEvent>>();
	private HashMap<Integer,HashMap<Integer,ToDoCalendarEvent>> map_CalendarEvent_Team = new HashMap<Integer,HashMap<Integer,ToDoCalendarEvent>>();


	/** Parameters **/
	private int p_login_User_ID = 0;
	private int p_AD_User_ID = 0;
	private int p_SelectedTab_AD_User_ID = 0;
	private int p_OldSelectedTab_AD_User_ID = 0;

	private int p_JP_Team_ID = 0;
	private MTeam m_Team = null;

	private int p_JP_ToDo_Category_ID = 0;
	private String p_JP_ToDo_Status = null ;
	private boolean p_IsDisplaySchedule = true;
	private boolean p_IsDisplayTask = false;

	private String p_JP_FristDayOfWeek = MGroupwareUser.JP_FIRSTDAYOFWEEK_Sunday;
	private String p_JP_ToDo_Main_Calendar_View = MGroupwareUser.JP_TODO_MAIN_CALENDAR_VIEW_Team;

	private String p_CalendarMold = null;

	private MGroupwareUser m_GroupwareUser = null;


	/** Noth Components **/
	private WSearchEditor editor_AD_User_ID;
	private WSearchEditor editor_JP_ToDo_Category_ID;
	private WSearchEditor editor_JP_Team_ID ;
	private WYesNoEditor  editor_IsDisplaySchedule ;
	private WYesNoEditor  editor_IsDisplayTask ;

	private Label label_AD_User_ID ;
	private Label label_JP_ToDo_Category_ID ;
	private Label label_JP_Team_ID ;

	private MLookup lookup_JP_ToDo_Category_ID;
	private Button button_Customize;
	private Label label_DisplayPeriod;


	/** Center **/
	private Center mainBorderLayout_Center;

	private Tabbox tabbox;
	private Tab tab_p_AD_User_ID;
	private Tabpanel tabpanel_p_AD_User_ID;


	/** West Components **/
	JPierePersonalToDoGadget personalToDoGadget_Schedule = null;
	JPierePersonalToDoGadget personalToDoGadget_Task = null;
	JPierePersonalToDoGadget personalToDoGadget_Memo = null;


	/** Customize PopupWindow Components **/
	private Popup popup = null;

	private WTableDirEditor editor_JP_FirstDayOfWeek ;
	private WNumberEditor   editor_JP_ToDo_Calendar_BeginTime ;
	private WNumberEditor   editor_JP_ToDo_Calendar_EndTime ;
	private WTableDirEditor editor_JP_ToDo_Main_Calendar_View ;
	private WYesNoEditor    editor_IsDisplaySchedule_For_Custom ;
	private WYesNoEditor    editor_IsDisplayTask_For_Custom ;


	private Label label_JP_FirstDayOfWeek;
	private Label label_JP_ToDo_Calendar_BeginTime;
	private Label label_JP_ToDo_Calendar_EndTime;
	private Label label_JP_ToDo_Main_Calendar_View;

	private Button button_Customize_Save;


	/** Interface **/
	List<MToDo> list_ToDoes = null;


	//Statics
	public final static String BUTTON_PREVIOUS = "PREVIOUS";
	public final static String BUTTON_NEXT = "NEXT";
	public final static String BUTTON_NEW = "NEW";
	public final static String BUTTON_TODAY = "TODAY";

	public final static String BUTTON_REFRESH = "REFRESH";
	public final static String BUTTON_CUSTOMIZE = "CUSTOMIZE";
	public final static String BUTTON_CUSTOMIZE_SAVE = "CUSTOMIZE_SAVE";

	public final static String JP_TODO_CALENDAR_MAX_MEMBER ="JP_TODO_CALENDAR_MAX_MEMBER";
	public final static String CSS_DEFAULT_TAB_STYLE ="border-top: 4px solid #ACD5EE;";


	/**
	 * Constructor
	 */
    public ToDoCalendar()
    {
		p_AD_User_ID = Env.getAD_User_ID(ctx);
		p_login_User_ID = p_AD_User_ID;
		p_SelectedTab_AD_User_ID = p_AD_User_ID;
		p_OldSelectedTab_AD_User_ID = p_AD_User_ID;

		map_Calendars.put(p_AD_User_ID, createInitialMainCalendar());

		initZk();

		updateDateLabel();

		updateCalendarModel(true,false, p_AD_User_ID);

    }



    /**
     * Create Initial Main Calendar
     *
     * Create initial main calendar. Get default Value from Groupware user if any.
     *
     * @return Calendars
     */
    private Calendars createInitialMainCalendar()
    {
    	Calendars calendars = new Calendars();

		calendars.invalidate();

		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_CREATE, this);
		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_EDIT, this);
		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_UPDATE,this);
//		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_MOUSE_OVER, this);
		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_DAY,this);
//		calendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_WEEK, this);


		if(m_GroupwareUser == null)
		{
			m_GroupwareUser = MGroupwareUser.get(ctx, p_login_User_ID);

			if(m_GroupwareUser != null)
			{
				p_IsDisplaySchedule = m_GroupwareUser.isDisplayScheduleJP();
				p_IsDisplayTask = m_GroupwareUser.isDisplayTaskJP();
				calendars.setBeginTime(m_GroupwareUser.getJP_ToDo_Calendar_BeginTime());
				calendars.setEndTime(m_GroupwareUser.getJP_ToDo_Calendar_EndTime());

				String fdow = m_GroupwareUser.getJP_FirstDayOfWeek();
				if(!Util.isEmpty(fdow))
				{
					int AD_Column_ID = MColumn.getColumn_ID(MGroupwareUser.Table_Name, MGroupwareUser.COLUMNNAME_JP_FirstDayOfWeek);
					int AD_Reference_Value_ID = MColumn.get(ctx, AD_Column_ID).getAD_Reference_Value_ID();
					MRefList refList =MRefList.get(ctx, AD_Reference_Value_ID, fdow,null);
					p_JP_FristDayOfWeek = refList.getValue();
					calendars.setFirstDayOfWeek(refList.getName());
				}

				p_JP_ToDo_Main_Calendar_View = m_GroupwareUser.getJP_ToDo_Main_Calendar_View();

			}
		}

		//set Calendar Mold
		if(m_GroupwareUser != null && !Util.isEmpty(m_GroupwareUser.getJP_DefaultCalendarView()))
		{
			String calendarView = m_GroupwareUser.getJP_DefaultCalendarView();
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



    /**
     * Initialization ZK
     */
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
		mainBorderLayout_Center.appendChild(createCenterContents());

		//***************** WEST **************************//

		West mainBorderLayout_West = new West();
		mainBorderLayout_West.setSplittable(true);
		mainBorderLayout_West.setCollapsible(true);
		mainBorderLayout_West.setOpen(true);
		mainBorderLayout_West.setDroppable("true");
		ZKUpdateUtil.setWidth(mainBorderLayout_West, "25%");
		mainBorderLayout.appendChild(mainBorderLayout_West);
		mainBorderLayout_West.appendChild(createWestContents());

    }



    /**
     * Create Noth contents of Borderlayout.
     *
     * @return Div
     */
    private Div createNorthContents()
    {
    	Div outerDiv = new Div();
		ZKUpdateUtil.setVflex(outerDiv, "max");
		ZKUpdateUtil.setHflex(outerDiv, "max");
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
		MLookup lookup_JP_ToDo_Status = MLookupFactory.get(ctx, 0,  0, MColumn.getColumn_ID(MGroupwareUser.Table_Name, MGroupwareUser.COLUMNNAME_JP_ToDo_Status),  DisplayType.List);
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


		editor_IsDisplaySchedule = new WYesNoEditor(MGroupwareUser.COLUMNNAME_IsDisplayScheduleJP, Msg.getElement(ctx,MGroupwareUser.COLUMNNAME_IsDisplayScheduleJP), null, true, false, true);
		editor_IsDisplaySchedule.setValue(p_IsDisplaySchedule);
		editor_IsDisplaySchedule.addValueChangeListener(this);
		row.appendChild(GroupwareToDoUtil.createEditorDiv(editor_IsDisplaySchedule, true));


		editor_IsDisplayTask = new WYesNoEditor(MGroupwareUser.COLUMNNAME_IsDisplayTaskJP, Msg.getElement(ctx,MGroupwareUser.COLUMNNAME_IsDisplayTaskJP), null, true, false, true);
		editor_IsDisplayTask.setValue(p_IsDisplayTask);
		editor_IsDisplayTask.addValueChangeListener(this);
		row.appendChild(GroupwareToDoUtil.createEditorDiv(editor_IsDisplayTask, true));


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
		createNewToDo.setName(BUTTON_NEW);
		createNewToDo.addEventListener(Events.ON_CLICK, this);
		createNewToDo.setId(String.valueOf(0));
		createNewToDo.setLabel(Msg.getMsg(ctx, "NewRecord"));
		ZKUpdateUtil.setHflex(createNewToDo, "true");
		row.appendCellChild(createNewToDo,2);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());


		Button refresh = new Button();
		refresh.setImage(ThemeManager.getThemeResource("images/Refresh16.png"));
		refresh.setName(BUTTON_REFRESH);
		refresh.addEventListener(Events.ON_CLICK, this);
		refresh.setLabel(Msg.getMsg(ctx, "Refresh"));
		ZKUpdateUtil.setHflex(refresh, "true");
		row.appendCellChild(refresh, 2);


		row.appendChild(GroupwareToDoUtil.getDividingLine());

		Button oneDayView = new Button();
		oneDayView.setLabel(Msg.getMsg(ctx,"Day"));
		//oneDayView.setClass("btn-small");
		oneDayView.setName(GroupwareToDoUtil.CALENDAR_ONEDAY_VIEW);
		oneDayView.addEventListener(Events.ON_CLICK, this);
		ZKUpdateUtil.setHflex(oneDayView, "true");
		row.appendCellChild(oneDayView);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());

		Button fivDayView = new Button();
		fivDayView.setLabel(Msg.getMsg(ctx,"5Days"));//
		//oneDayView.setClass("btn-small");
		fivDayView.setName(GroupwareToDoUtil.CALENDAR_FIVEDAYS_VIEW );
		fivDayView.addEventListener(Events.ON_CLICK, this);
		ZKUpdateUtil.setHflex(fivDayView, "true");
		row.appendCellChild(fivDayView);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());

		Button sevenDayView = new Button();
		sevenDayView.setLabel(Msg.getMsg(ctx, "Week"));
		//sevenDayView.setClass("btn-small");
		sevenDayView.setName(GroupwareToDoUtil.CALENDAR_SEVENDAYS_VIEW);
		sevenDayView.addEventListener(Events.ON_CLICK, this);
		ZKUpdateUtil.setHflex(sevenDayView, "true");
		row.appendCellChild(sevenDayView);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());


		Button monthDayView = new Button();
		monthDayView.setLabel(Msg.getMsg(ctx, "Month"));
		//monthDayView.setClass("btn-small");
		monthDayView.setName(GroupwareToDoUtil.CALENDAR_MONTH_VIEW);
		monthDayView.addEventListener(Events.ON_CLICK, this);
		ZKUpdateUtil.setHflex(monthDayView, "true");
		row.appendCellChild(monthDayView);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());


		row.appendChild(GroupwareToDoUtil.getDividingLine());


		Button leftBtn = new Button();
		leftBtn.setImage(ThemeManager.getThemeResource("images/MoveLeft16.png"));
		//leftBtn.setClass("btn-small");
		leftBtn.setName(BUTTON_PREVIOUS);
		leftBtn.addEventListener(Events.ON_CLICK, this);
		leftBtn.setLabel(" ");
		ZKUpdateUtil.setHflex(leftBtn, "true");
		row.appendCellChild(leftBtn);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());

		Button today = new Button();
		today.setLabel(Msg.getMsg(ctx, "Today"));
		//today.setClass("btn-small");
		today.setName(BUTTON_TODAY);
		today.addEventListener(Events.ON_CLICK, this);
		row.appendChild(today);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());

		Button rightBtn = new Button();
		rightBtn.setImage(ThemeManager.getThemeResource("images/MoveRight16.png"));
		//rightBtn.setClass("btn-small");
		rightBtn.addEventListener(Events.ON_CLICK, this);
		rightBtn.setName(BUTTON_NEXT);
		rightBtn.setLabel(" ");
		ZKUpdateUtil.setHflex(rightBtn, "true");
		row.appendCellChild(rightBtn);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());

		row.appendChild(GroupwareToDoUtil.getDividingLine());


		label_DisplayPeriod = new Label();
		updateDateLabel();

		row.appendChild(GroupwareToDoUtil.createLabelDiv(null,  Msg.getMsg(ctx, "JP_DisplayPeriod") + " : ", true));
		row.appendChild(GroupwareToDoUtil.createLabelDiv(null, label_DisplayPeriod, true));

		row.appendChild(GroupwareToDoUtil.createSpaceDiv());
		row.appendChild(GroupwareToDoUtil.getDividingLine());
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());

		//Customize
		button_Customize = new Button();
		button_Customize.setImage(ThemeManager.getThemeResource("images/Customize16.png"));
		button_Customize.addEventListener(Events.ON_CLICK, this);
		button_Customize.setName(BUTTON_CUSTOMIZE);
		button_Customize.setLabel(" ");
		ZKUpdateUtil.setVflex(button_Customize, "max");
		ZKUpdateUtil.setHflex(button_Customize, "max");
		row.appendCellChild(button_Customize);

		initCustomizePopupWindow();

    	return outerDiv;

    }



    /**
     * Initialization Customize Popup Window
     */
    private void initCustomizePopupWindow()
    {
		//First day ot week
		MLookup lookup_FirstDayOfWeek = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MGroupwareUser.Table_Name, MGroupwareUser.COLUMNNAME_JP_FirstDayOfWeek),  DisplayType.List);
		editor_JP_FirstDayOfWeek = new WTableDirEditor(MGroupwareUser.COLUMNNAME_JP_FirstDayOfWeek, false, false, true, lookup_FirstDayOfWeek);
		editor_JP_FirstDayOfWeek.setValue(p_JP_FristDayOfWeek);
		editor_JP_FirstDayOfWeek.addValueChangeListener(this);
		label_JP_FirstDayOfWeek = new Label(Msg.getElement(ctx, MGroupwareUser.COLUMNNAME_JP_FirstDayOfWeek));

		//Display Schedule
		editor_IsDisplaySchedule_For_Custom = new WYesNoEditor(MGroupwareUser.COLUMNNAME_IsDisplayScheduleJP, Msg.getElement(ctx,MGroupwareUser.COLUMNNAME_IsDisplayScheduleJP), null, true, false, true);
		editor_IsDisplaySchedule_For_Custom.setValue(p_IsDisplaySchedule);
		editor_IsDisplaySchedule_For_Custom.addValueChangeListener(this);

		//Display Task
		editor_IsDisplayTask_For_Custom = new WYesNoEditor(MGroupwareUser.COLUMNNAME_IsDisplayTaskJP, Msg.getElement(ctx,MGroupwareUser.COLUMNNAME_IsDisplayTaskJP), null, true, false, true);
		editor_IsDisplayTask_For_Custom.setValue(p_IsDisplayTask);
		editor_IsDisplayTask_For_Custom.addValueChangeListener(this);

		//JP_ToDo_Calendar_BeginTime
		editor_JP_ToDo_Calendar_BeginTime = new WNumberEditor(MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_BeginTime, false, false, true, DisplayType.Integer, "");
		if(m_GroupwareUser != null)
			editor_JP_ToDo_Calendar_BeginTime.setValue(m_GroupwareUser.getJP_ToDo_Calendar_BeginTime());
		else
			editor_JP_ToDo_Calendar_BeginTime.setValue(0);
		editor_JP_ToDo_Calendar_BeginTime.addValueChangeListener(this);
		label_JP_ToDo_Calendar_BeginTime = new Label(Msg.getElement(ctx, MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_BeginTime));

		//JP_ToDo_Calendar_EndTime
		editor_JP_ToDo_Calendar_EndTime = new WNumberEditor(MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_EndTime, false, false, true, DisplayType.Integer, "");
		if(m_GroupwareUser != null)
			editor_JP_ToDo_Calendar_EndTime.setValue(m_GroupwareUser.getJP_ToDo_Calendar_EndTime());
		else
			editor_JP_ToDo_Calendar_EndTime.setValue(0);
		editor_JP_ToDo_Calendar_EndTime.addValueChangeListener(this);
		label_JP_ToDo_Calendar_EndTime = new Label(Msg.getElement(ctx, MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_EndTime));

		//JP_ToDo_Main_Calendar_View
		MLookup lookup_Main_Calendar_View = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MGroupwareUser.Table_Name, MGroupwareUser.COLUMNNAME_JP_ToDo_Main_Calendar_View),  DisplayType.List);
		editor_JP_ToDo_Main_Calendar_View = new WTableDirEditor(MGroupwareUser.COLUMNNAME_JP_ToDo_Main_Calendar_View, false, false, true, lookup_Main_Calendar_View);
		editor_JP_ToDo_Main_Calendar_View.setValue(p_JP_ToDo_Main_Calendar_View);
		editor_JP_ToDo_Main_Calendar_View.addValueChangeListener(this);
		label_JP_ToDo_Main_Calendar_View = new Label(Msg.getElement(ctx, MGroupwareUser.COLUMNNAME_JP_ToDo_Main_Calendar_View));


		//Save Button
		button_Customize_Save = new Button();
		button_Customize_Save.setImage(ThemeManager.getThemeResource("images/Save16.png"));
		button_Customize_Save.addEventListener(Events.ON_CLICK, this);
		button_Customize_Save.setName(BUTTON_CUSTOMIZE_SAVE);
		button_Customize_Save.setLabel(Msg.getMsg(ctx, "save"));
		button_Customize_Save.setVisible(true);
		ZKUpdateUtil.setHflex(button_Customize_Save, "true");


		if(m_GroupwareUser == null)
			button_Customize_Save.setVisible(false);


    }



    /**
     * Create Center contents of Borderlayout.
     *
     * @return Div
     */
    private Div createCenterContents()
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

			MGroupwareUser gUser = MGroupwareUser.get(ctx, p_AD_User_ID);
			if(gUser == null || gUser.getJP_ColorPicker() == null)
			{
				tab_p_AD_User_ID.setStyle(CSS_DEFAULT_TAB_STYLE);
			}else {
    			String css = "border-top: 4px solid " + gUser.getJP_ColorPicker() + ";" ;
    			tab_p_AD_User_ID.setStyle(css);
			}

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

	    			MGroupwareUser gUser = MGroupwareUser.get(ctx, menbers[i].getAD_User_ID());
	    			if(gUser == null || gUser.getJP_ColorPicker() == null)
	    			{
	    				tab.setStyle(CSS_DEFAULT_TAB_STYLE);
	    			}else {
		    			String css = "border-top: 4px solid " + gUser.getJP_ColorPicker() + ";" ;
		    			tab.setStyle(css);
	    			}
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



    /**
     * Create West contents of Borderlayout.
     *
     * @return Div
     */
    private Div createWestContents()
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

		GroupwareMenuGadgetFlat toDoMenu = new GroupwareMenuGadgetFlat();
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
		personalToDoGadget_Schedule.setCallerPersonalToDoPopupwindow(this);
		personalToDoGadget_Schedule.addToDoCalenderEventReceiver(this);
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
		personalToDoGadget_Task.setCallerPersonalToDoPopupwindow(this);
		personalToDoGadget_Task.addToDoCalenderEventReceiver(this);
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
		personalToDoGadget_Memo.setCallerPersonalToDoPopupwindow(this);
		personalToDoGadget_Memo.addToDoCalenderEventReceiver(this);

		groupBox3.appendChild(personalToDoGadget_Memo);

    	return div;

    }


    /**
     * Value Chenge Events
     */
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

			Calendars from = map_Calendars.get(p_SelectedTab_AD_User_ID);
			p_OldSelectedTab_AD_User_ID = p_SelectedTab_AD_User_ID;
			p_SelectedTab_AD_User_ID = p_AD_User_ID;
			map_Calendars.clear();
			map_Calendars.put(p_AD_User_ID, createSyncCalendars(from));


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

				//The ToDo category have been cleared as the user has changed.
				throw new WrongValueException(editor_JP_ToDo_Category_ID.getComponent(), Msg.getMsg(ctx, "JP_ToDo_Category_Cleared"));

			}

			tabbox = null;
		  	mainBorderLayout_Center.getFirstChild().detach();
			mainBorderLayout_Center.appendChild(createCenterContents());

			if(p_JP_Team_ID == 0)
				updateCalendarModel(true,false, 0);
			else
				updateCalendarModel(true,true, 0);

			refreshWest(null,false);

		}else if(MToDo.COLUMNNAME_JP_ToDo_Category_ID.equals(name)){

			if(value == null)
			{
				p_JP_ToDo_Category_ID = 0;
			}else {
				p_JP_ToDo_Category_ID = Integer.parseInt(value.toString());
			}

			resetSelectedTabCalendarModel();

		}else if(MTeam.COLUMNNAME_JP_Team_ID.equals(name)){

			if(value == null)
			{
				p_JP_Team_ID = 0;
				m_Team = null;
				tab_p_AD_User_ID.setLabel(MUser.get(ctx, p_AD_User_ID).getName());

			}else {

				p_JP_Team_ID = Integer.parseInt(value.toString());
				m_Team = new MTeam(ctx, p_JP_Team_ID, null);

				MTeamMember[] member = m_Team.getTeamMember();
				int JP_ToDo_Calendar_Max_Member = MSysConfig.getIntValue(JP_TODO_CALENDAR_MAX_MEMBER, 100, Env.getAD_Client_ID(ctx));

				if(member.length == 0 || (member.length == 1 && member[0].getAD_User_ID() == p_AD_User_ID))
				{
					p_JP_Team_ID = 0;
					m_Team = null;
					tab_p_AD_User_ID.setLabel(MUser.get(ctx, p_AD_User_ID).getName());
					editor_JP_Team_ID.setValue(0);

					//There are no users on the team, or there are no users on the team except the selected user.
					FDialog.error(form.getWindowNo(), "Error", Msg.getMsg(ctx, "JP_Team_No_Users_Except_Selected_User"));

					return ;
				}


				if(member.length > JP_ToDo_Calendar_Max_Member)
				{
					p_JP_Team_ID = 0;
					m_Team = null;
					tab_p_AD_User_ID.setLabel(MUser.get(ctx, p_AD_User_ID).getName());
					editor_JP_Team_ID.setValue(0);

					//The number of users belonging to the selected team has exceeded the maximum number of users that can be displayed on the calendar.
					FDialog.error(form.getWindowNo(), "Error", Msg.getMsg(ctx, "JP_ToDo_Calendar_Max_Member", new Object[] {member.length,JP_ToDo_Calendar_Max_Member}));

					return ;
				}

				tab_p_AD_User_ID.setLabel(MUser.get(ctx, p_AD_User_ID).getName() + " & "  + Msg.getElement(ctx, MTeam.COLUMNNAME_JP_Team_ID));

			}

			MGroupwareUser gUser = MGroupwareUser.get(ctx, p_AD_User_ID);
			if(gUser == null || gUser.getJP_ColorPicker() == null)
			{
				tab_p_AD_User_ID.setStyle(CSS_DEFAULT_TAB_STYLE);
			}else {
    			String css = "border-top: 4px solid " + gUser.getJP_ColorPicker() + ";" ;
    			tab_p_AD_User_ID.setStyle(css);
			}

			Calendars from = map_Calendars.get(p_SelectedTab_AD_User_ID);
			Calendars to = map_Calendars.get(p_AD_User_ID);
			map_Calendars.clear();

			if(p_AD_User_ID == p_SelectedTab_AD_User_ID)
			{
				map_Calendars.put(p_AD_User_ID, from);
			}else {
				map_Calendars.put(p_AD_User_ID, syncCalendars(from, to));
			}

			p_OldSelectedTab_AD_User_ID = p_SelectedTab_AD_User_ID;
			p_SelectedTab_AD_User_ID = p_AD_User_ID;


		  	mainBorderLayout_Center.getFirstChild().detach();
			mainBorderLayout_Center.appendChild(createCenterContents());

			updateCalendarModel(false, true, 0);


		}else if(MGroupwareUser.COLUMNNAME_IsDisplayScheduleJP.equals(name)) {

			p_IsDisplaySchedule = (boolean)value;
			editor_IsDisplaySchedule.setValue(value);
			editor_IsDisplaySchedule_For_Custom.setValue(value);
			if(editor_IsDisplaySchedule_For_Custom.isVisible())
				button_Customize_Save.setDisabled(false);

			resetSelectedTabCalendarModel();

		}else if(MGroupwareUser.COLUMNNAME_IsDisplayTaskJP.equals(name)) {

			p_IsDisplayTask = (boolean)value;
			editor_IsDisplayTask.setValue(value);
			editor_IsDisplayTask_For_Custom.setValue(value);
			if(editor_IsDisplayTask_For_Custom.isVisible())
				button_Customize_Save.setDisabled(false);

			resetSelectedTabCalendarModel();

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

			resetSelectedTabCalendarModel();

		}else if(MGroupwareUser.COLUMNNAME_JP_FirstDayOfWeek.equals(name)){

			p_JP_FristDayOfWeek = value.toString();

			int AD_Column_ID = MColumn.getColumn_ID(MGroupwareUser.Table_Name, MGroupwareUser.COLUMNNAME_JP_FirstDayOfWeek);
			int AD_Reference_Value_ID = MColumn.get(ctx, AD_Column_ID).getAD_Reference_Value_ID();
			MRefList refList =MRefList.get(ctx, AD_Reference_Value_ID, value.toString(),null);

			map_Calendars.get(p_AD_User_ID).setFirstDayOfWeek(refList.getName());

			updateDateLabel();
			updateCalendarModel(false, false, 0);

			if(button_Customize_Save.isVisible())
				button_Customize_Save.setDisabled(false);

		}else if(MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar_BeginTime.equals(name)){

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

			if(button_Customize_Save.isVisible())
				button_Customize_Save.setDisabled(false);

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
			if(button_Customize_Save.isVisible())
				button_Customize_Save.setDisabled(false);

		}else if(MGroupwareUser.COLUMNNAME_JP_ToDo_Main_Calendar_View.equals(name)){

			p_JP_ToDo_Main_Calendar_View = value.toString();

			if(button_Customize_Save.isVisible())
				button_Customize_Save.setDisabled(false);

			resetSelectedTabCalendarModel();

		}
	}



	/**
	 * Event Process
	 */
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
				if(BUTTON_NEW.equals(btnName))
				{
					list_ToDoes = null;
					p_CalendarsEventBeginDate = null;
					p_CalendarsEventEndDate =  null;

					PersonalToDoPopupWindow todoWindow = new PersonalToDoPopupWindow(this, -1);
					todoWindow.addToDoCalenderEventReceiver(this);
					todoWindow.addToDoCalenderEventReceiver(personalToDoGadget_Schedule);
					todoWindow.addToDoCalenderEventReceiver(personalToDoGadget_Task);
					todoWindow.addToDoCalenderEventReceiver(personalToDoGadget_Memo);
					SessionManager.getAppDesktop().showWindow(todoWindow);

				}else if(BUTTON_PREVIOUS.equals(btnName))
				{
					map_Calendars.get(p_SelectedTab_AD_User_ID).previousPage();

					updateDateLabel();
					updateCalendarModel(false,false, 0);

				}else if(BUTTON_NEXT.equals(btnName)){

					map_Calendars.get(p_SelectedTab_AD_User_ID).nextPage();

					updateDateLabel();
					updateCalendarModel(false,false, 0);

				}else if(BUTTON_REFRESH.equals(btnName)){

					if(p_JP_Team_ID > 0)
					{
					  	mainBorderLayout_Center.getFirstChild().detach();
						mainBorderLayout_Center.appendChild(createCenterContents());
						updateCalendarModel(true,true, 0);

					}else {

						updateCalendarModel(true,false, 0);

					}

					refreshWest(null, false);

				}else if(BUTTON_TODAY.equals(btnName)){

					map_Calendars.get(p_SelectedTab_AD_User_ID).setCurrentDate(Calendar.getInstance(map_Calendars.get(p_SelectedTab_AD_User_ID).getDefaultTimeZone()).getTime());

					updateDateLabel();
					updateCalendarModel(false,false, 0);

				}else if(GroupwareToDoUtil.CALENDAR_ONEDAY_VIEW.equals(btnName)){

					p_CalendarMold = GroupwareToDoUtil.CALENDAR_ONEDAY_VIEW;
					setCalendarMold(1);
					updateDateLabel();
					updateCalendarModel(false,false, 0);

				}else if(GroupwareToDoUtil.CALENDAR_FIVEDAYS_VIEW.equals(btnName)){

					p_CalendarMold = GroupwareToDoUtil.CALENDAR_FIVEDAYS_VIEW;
					setCalendarMold(5);

					updateDateLabel();
					updateCalendarModel(false,false, 0);

				}else if(GroupwareToDoUtil.CALENDAR_SEVENDAYS_VIEW.equals(btnName)){

					p_CalendarMold = GroupwareToDoUtil.CALENDAR_SEVENDAYS_VIEW;
					setCalendarMold(7);

					updateDateLabel();
					updateCalendarModel(false,false, 0);

				}else if(GroupwareToDoUtil.CALENDAR_MONTH_VIEW.equals(btnName)){

					p_CalendarMold = GroupwareToDoUtil.CALENDAR_MONTH_VIEW;
					setCalendarMold(0);

					updateDateLabel();
					updateCalendarModel(false, false, 0);

				}else if(BUTTON_CUSTOMIZE.equals(btnName)){

					createCustomizePopupWindow();

				}else if(BUTTON_CUSTOMIZE_SAVE.equals(btnName)){

					if(m_GroupwareUser != null)
					{
						if(editor_JP_FirstDayOfWeek.isVisible())
						{
							m_GroupwareUser.setJP_FirstDayOfWeek(p_JP_FristDayOfWeek);
						}

						if(editor_IsDisplaySchedule_For_Custom.isVisible())
							m_GroupwareUser.setIsDisplayScheduleJP(p_IsDisplaySchedule);

						if(editor_IsDisplayTask_For_Custom.isVisible())
							m_GroupwareUser.setIsDisplayTaskJP(p_IsDisplayTask);

						if(editor_JP_ToDo_Calendar_BeginTime.isVisible())
						{
							Object beginTime = editor_JP_ToDo_Calendar_BeginTime.getValue();
							if(beginTime != null)
								m_GroupwareUser.setJP_ToDo_Calendar_BeginTime(Integer.valueOf(beginTime.toString()));
						}

						if(editor_JP_ToDo_Calendar_EndTime.isVisible())
						{
							Object endTime = editor_JP_ToDo_Calendar_EndTime.getValue();
							if(endTime != null)
								m_GroupwareUser.setJP_ToDo_Calendar_EndTime(Integer.valueOf(endTime.toString()));
						}

						if(editor_JP_ToDo_Main_Calendar_View.isVisible())
						{
							Object JP_ToDo_Main_Calendar_View = editor_JP_ToDo_Main_Calendar_View.getValue();
							if(JP_ToDo_Main_Calendar_View != null)
								m_GroupwareUser.setJP_ToDo_Main_Calendar_View(JP_ToDo_Main_Calendar_View.toString());
						}

						try
						{
							m_GroupwareUser.saveEx();

						}catch (Exception e) {

							FDialog.error(form.getWindowNo(),"Error", e.getMessage());//TODO: 保存時のエラーメッセージ処理の改善

							return;
						}

						if(button_Customize_Save.isVisible())
							button_Customize_Save.setDisabled(true);
					}
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

						if(m_GroupwareUser == null)
						{
							AEnv.zoom(MTable.getTable_ID(MGroupwareUser.Table_Name), 0);

						}else {

							MGroupwareUser gUser = MGroupwareUser.get(ctx, Integer.valueOf(value.toString()));
							AEnv.zoom(MTable.getTable_ID(MGroupwareUser.Table_Name), gUser.getJP_GroupwareUser_ID());
						}
					}

				}else if(label_JP_Team_ID.equals(comp)) {

					Object value = editor_JP_Team_ID.getValue();
					if(value == null || Util.isEmpty(value.toString()))
					{
						AEnv.zoom(MTable.getTable_ID(MTeam.Table_Name), 0);
					}else {
						AEnv.zoom(MTable.getTable_ID(MTeam.Table_Name), Integer.valueOf(value.toString()));
					}

				}else if(label_JP_ToDo_Category_ID.equals(comp)) {

					Object value = editor_JP_ToDo_Category_ID.getValue();
					if(value == null || Util.isEmpty(value.toString()))
					{
						AEnv.zoom(MTable.getTable_ID(MToDoCategory.Table_Name), 0);
					}else {
						AEnv.zoom(MTable.getTable_ID(MToDoCategory.Table_Name), Integer.valueOf(value.toString()));
					}
				}

			}else if(comp instanceof Tab) {

				Object obj_AD_User_ID = comp.getAttribute("AD_User_ID");
				int AD_User_ID = Integer.valueOf(obj_AD_User_ID.toString());
				p_OldSelectedTab_AD_User_ID = p_SelectedTab_AD_User_ID;
				p_SelectedTab_AD_User_ID = AD_User_ID;


				Tabpanel tabpanel = tabbox.getTabpanel(tabbox.getSelectedIndex());

				if(p_AD_User_ID == p_SelectedTab_AD_User_ID)
				{
					updateCalendarModel(false,false, 0);
					syncCalendars(map_Calendars.get(p_OldSelectedTab_AD_User_ID), map_Calendars.get(p_SelectedTab_AD_User_ID));

				}else if(tabpanel.getFirstChild() == null) {

					updateCalendarModel(false,false, 0);
					Calendars  calendars = map_Calendars.get(p_SelectedTab_AD_User_ID);
					tabpanel.appendChild(calendars);

				}else {

					updateCalendarModel(false,false, 0);
					syncCalendars(map_Calendars.get(p_OldSelectedTab_AD_User_ID), map_Calendars.get(p_SelectedTab_AD_User_ID));
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
				todoWindow.addToDoCalenderEventReceiver(this);
				todoWindow.addToDoCalenderEventReceiver(personalToDoGadget_Schedule);
				todoWindow.addToDoCalenderEventReceiver(personalToDoGadget_Task);
				todoWindow.addToDoCalenderEventReceiver(personalToDoGadget_Memo);

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
					list_ToDoes.add(ce.getToDo());

					p_CalendarsEventBeginDate = ce.getToDo().getJP_ToDo_ScheduledStartTime();
					p_CalendarsEventEndDate = ce.getToDo().getJP_ToDo_ScheduledEndTime();

					PersonalToDoPopupWindow todoWindow = new PersonalToDoPopupWindow(this, 0);
					todoWindow.addToDoCalenderEventReceiver(this);
					todoWindow.addToDoCalenderEventReceiver(personalToDoGadget_Schedule);
					todoWindow.addToDoCalenderEventReceiver(personalToDoGadget_Task);
					todoWindow.addToDoCalenderEventReceiver(personalToDoGadget_Memo);

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
			updateCalendarModel(false,false, 0);

		}else if (GroupwareToDoUtil.CALENDAR_EVENT_WEEK.equals(eventName)){

			//I don't know this Event

		}else if (Events.ON_CLOSE.equals(eventName)) {

			if(comp instanceof Tab)//Tab Close
			{
				Tab tab = (Tab)comp;
				int deleteTab_AD_User_ID = ((Integer)tab.getAttribute("AD_User_ID")).intValue();
				Calendars deleteCalendars = map_Calendars.get(deleteTab_AD_User_ID);

				map_Calendars.remove(deleteTab_AD_User_ID);
				map_CalendarEvent_Team.remove(deleteTab_AD_User_ID);

				if(p_SelectedTab_AD_User_ID == deleteTab_AD_User_ID)
				{
					int tabSize = tabbox.getTabs().getChildren().size();
					int nextTabIndex = tab.getIndex();
					if((nextTabIndex+1) < tabSize)
					{
						nextTabIndex++;
					}else {
						nextTabIndex--;
					}

					Tabpanel tabpanel = tabbox.getTabpanel(nextTabIndex);
					int next_AD_User_ID = ((Integer)tabpanel.getAttribute("AD_User_ID")).intValue();
					p_OldSelectedTab_AD_User_ID = next_AD_User_ID;
					p_SelectedTab_AD_User_ID = next_AD_User_ID;

					updateCalendarModel(false, false, 0);
					if(tabpanel.getFirstChild() == null)
					{
						Calendars  calendars = map_Calendars.get(p_SelectedTab_AD_User_ID);
						tabpanel.appendChild(calendars);
					}else {
						syncCalendars(deleteCalendars==null? map_Calendars.get(p_AD_User_ID):deleteCalendars, map_Calendars.get(p_SelectedTab_AD_User_ID));
					}

				}else if(p_AD_User_ID == p_SelectedTab_AD_User_ID){

					updateCalendarModel(false,false, 0);

				}

			}//if(comp instanceof Tab)

		}else if(eventName.equals("onEventUpdate")){

			if(event instanceof CalendarsEvent)	//Drag & Drop
			{
				CalendarsEvent calEvent = (CalendarsEvent) event;
				ToDoCalendarEvent todoEvent = (ToDoCalendarEvent) calEvent.getCalendarEvent();
				MToDo todo = todoEvent.getToDo();
				HashMap<Integer, ToDoCalendarEvent> events = null;

				if(todo.getJP_ToDo_Type().equals(MToDo.JP_TODO_TYPE_Schedule))
				{
					Timestamp startTime = new Timestamp(calEvent.getBeginDate().getTime());
					todo.setJP_ToDo_ScheduledStartTime(startTime);

					//Adjust
					Timestamp endTime = new Timestamp(calEvent.getEndDate().getTime());
					Timestamp schesuledEndTime = todo.getJP_ToDo_ScheduledEndTime();
					if(schesuledEndTime.toLocalDateTime().toLocalTime() == LocalTime.MIN)
					{
						endTime = Timestamp.valueOf(LocalDateTime.of(endTime.toLocalDateTime().toLocalDate(), LocalTime.MIN));
					}

					todo.setJP_ToDo_ScheduledEndTime(endTime);

					if(p_AD_User_ID == p_SelectedTab_AD_User_ID)
					{
						if(p_AD_User_ID == todo.getAD_User_ID())
						{
							events = map_CalendarEvent_User.get(p_AD_User_ID);
						}else {
							events = map_CalendarEvent_Team.get(todo.getAD_User_ID());
						}

					}else {

						events = map_CalendarEvent_Team.get(p_SelectedTab_AD_User_ID);

					}

				}else if(todo.getJP_ToDo_Type().equals(MToDo.JP_TODO_TYPE_Task)) {

					todo.setJP_ToDo_ScheduledEndTime(new Timestamp(calEvent.getBeginDate().getTime()));
					if(p_AD_User_ID == p_SelectedTab_AD_User_ID)
					{
						if(p_AD_User_ID == todo.getAD_User_ID())
						{
							events = map_CalendarEvent_User.get(p_AD_User_ID);
						}else {
							events = map_CalendarEvent_Team.get(todo.getAD_User_ID());
						}
					}else {
						events = map_CalendarEvent_Team.get(p_SelectedTab_AD_User_ID);
					}

				}

				ToDoCalendarEvent oldEvent = events.get(todo.getJP_ToDo_ID());
				ToDoCalendarEvent newEvent = new ToDoCalendarEvent(todo);

				events.put(todo.getJP_ToDo_ID(), newEvent);
				if(!todo.save())
				{
					//TODO エラー処理
				}

				if(p_AD_User_ID == p_SelectedTab_AD_User_ID)
				{
					updateCalendarEvent(oldEvent, newEvent);
					refreshWest(todo.getJP_ToDo_Type(), false);

				}else {

					updateCalendarModel(false, false, p_SelectedTab_AD_User_ID);
				}

			}

		}

	}



	/**
	 * Update Displayed Calender period.
	 */
	private void updateDateLabel()
	{
		Date b = map_Calendars.get(p_SelectedTab_AD_User_ID).getBeginDate();
		Date e = map_Calendars.get(p_SelectedTab_AD_User_ID).getEndDate();

		LocalDateTime local = new Timestamp(e.getTime()).toLocalDateTime();
		e = new Date(Timestamp.valueOf(local.minusDays(1)).getTime());

		SimpleDateFormat sdfV = DisplayType.getDateFormat();
		//sdfV.setTimeZone(calendars.getDefaultTimeZone());

		label_DisplayPeriod.setValue(sdfV.format(b) + " - " + sdfV.format(e));
	}



	/**
	 * Create new Calendar. The View is Synchronized with view of argument calenders.
	 *
	 * @param syncFromCalendars
	 * @return Calendars
	 */
	private Calendars createSyncCalendars(Calendars syncFromCalendars)
	{
		Calendars syncToCalendars = new Calendars();

		syncToCalendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_CREATE, this);
		syncToCalendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_EDIT, this);
		syncToCalendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_UPDATE,this);
//		to.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_MOUSE_OVER, this);
		syncToCalendars.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_DAY,this);
//		to.addEventListener(GroupwareToDoUtil.CALENDAR_EVENT_WEEK, this);

		return syncCalendars(syncFromCalendars, syncToCalendars);

	}



	/**
	 * Synchronize Calendar view. And Synchronize Main Calendars View if any.
	 *
	 * @param syncFromCalendars
	 * @return Calendars
	 */
	private Calendars syncCalendars(Calendars syncFromCalendars, Calendars syncToCalendars)
	{
		syncToCalendars.setMold(syncFromCalendars.getMold());
		syncToCalendars.setDays(syncFromCalendars.getDays());

		syncToCalendars.setCurrentDate(syncFromCalendars.getCurrentDate());

		syncToCalendars.setBeginTime(syncFromCalendars.getBeginTime());
		syncToCalendars.setEndTime(syncFromCalendars.getEndTime());

		syncToCalendars.setFirstDayOfWeek(syncFromCalendars.getFirstDayOfWeek());
		syncToCalendars.invalidate();

		//Synchronize Main Calendars View if any.
		if(map_Calendars != null && map_Calendars.size() > 0)
		{
			Calendars mainCalendars = map_Calendars.get(p_AD_User_ID);
			mainCalendars.setMold(syncFromCalendars.getMold());
			mainCalendars.setDays(syncFromCalendars.getDays());

			mainCalendars.setCurrentDate(syncFromCalendars.getCurrentDate());

			mainCalendars.setBeginTime(syncFromCalendars.getBeginTime());
			mainCalendars.setEndTime(syncFromCalendars.getEndTime());

			mainCalendars.setFirstDayOfWeek(syncFromCalendars.getFirstDayOfWeek());
			mainCalendars.invalidate();
		}

		return syncToCalendars;
	}



	/**
	 * Create Customize PopupWindow
	 */
	private void createCustomizePopupWindow()
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

			button_Customize_Save.setDisabled(true);

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

		if(m_GroupwareUser != null)
		{
			row = rows.newRow();
			row.appendChild(new Div());
			row.appendChild(GroupwareToDoUtil.createEditorDiv(editor_IsDisplaySchedule_For_Custom, true));

			row = rows.newRow();
			row.appendChild(new Div());
			row.appendChild(GroupwareToDoUtil.createEditorDiv(editor_IsDisplayTask_For_Custom, true));

		}else {

			editor_IsDisplaySchedule_For_Custom.setVisible(false);
			editor_IsDisplayTask_For_Custom.setVisible(false);
		}

		row = rows.newRow();
		row.appendChild(GroupwareToDoUtil.createLabelDiv(editor_JP_ToDo_Calendar_BeginTime, label_JP_ToDo_Calendar_BeginTime, true));
		row.appendChild(editor_JP_ToDo_Calendar_BeginTime.getComponent());

		row = rows.newRow();
		row.appendChild(GroupwareToDoUtil.createLabelDiv(editor_JP_ToDo_Calendar_EndTime, label_JP_ToDo_Calendar_EndTime, true));
		row.appendChild(editor_JP_ToDo_Calendar_EndTime.getComponent());

		row = rows.newRow();
		row.appendChild(GroupwareToDoUtil.createLabelDiv(editor_JP_ToDo_Main_Calendar_View, label_JP_ToDo_Main_Calendar_View, true));
		row.appendChild(editor_JP_ToDo_Main_Calendar_View.getComponent());

		if(m_GroupwareUser != null)
		{
			row = rows.newRow();
			row.appendCellChild(button_Customize_Save,2);
		}

		popup.setPage(button_Customize.getPage());
		popup.open(button_Customize, "after_start");

	}



	/**
	 * Set Calendar Mold
	 *
	 * @param days
	 */
	private void setCalendarMold(int days)
	{
		Calendars calendars = map_Calendars.get(p_SelectedTab_AD_User_ID);
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
			calendars.setDays(0);

			editor_JP_FirstDayOfWeek.setVisible(true);
			label_JP_FirstDayOfWeek.setVisible(true);

			editor_JP_ToDo_Calendar_BeginTime.setVisible(false);
			label_JP_ToDo_Calendar_BeginTime.setVisible(false);

			editor_JP_ToDo_Calendar_EndTime.setVisible(false);
			label_JP_ToDo_Calendar_EndTime.setVisible(false);
		}
	}

	private void deleteCalendarEvent(ToDoCalendarEvent deleteEvent)
	{
		Calendars calendars = map_Calendars.get(p_SelectedTab_AD_User_ID);
		SimpleCalendarModel	scm = (SimpleCalendarModel)calendars.getModel();
		scm.remove(deleteEvent);
	}

	private void createCalendarEvent(ToDoCalendarEvent newEvent)
	{
		if(isSkip(newEvent))
			return ;

		Calendars calendars = map_Calendars.get(p_SelectedTab_AD_User_ID);
		SimpleCalendarModel	scm = (SimpleCalendarModel)calendars.getModel();

		boolean isGetToDoCalendarEventRange = false;
		if(p_AD_User_ID == p_SelectedTab_AD_User_ID)
		{
			isGetToDoCalendarEventRange = isGetToDoCalendarEventRange_User(newEvent);
		}else {
			isGetToDoCalendarEventRange = isGetToDoCalendarEventRange_Team(newEvent);
		}

		if(isGetToDoCalendarEventRange)
		{
			setEventTextAndColor(newEvent);
			scm.add(newEvent);
		}

	}

	private void updateCalendarEvent(ToDoCalendarEvent oldEvent, ToDoCalendarEvent newEvent)
	{
		Calendars calendars = map_Calendars.get(p_SelectedTab_AD_User_ID);
		SimpleCalendarModel	scm = (SimpleCalendarModel)calendars.getModel();
		scm.remove(oldEvent);

		if(isSkip(newEvent))
			return ;


		boolean isGetToDoCalendarEventRange = false;
		if(p_AD_User_ID == p_SelectedTab_AD_User_ID)
		{
			isGetToDoCalendarEventRange = isGetToDoCalendarEventRange_User(newEvent);
		}else {
			isGetToDoCalendarEventRange = isGetToDoCalendarEventRange_Team(newEvent);
		}

		if(isGetToDoCalendarEventRange)
		{
			setEventTextAndColor(newEvent);
			scm.add(newEvent);
		}

	}


	private boolean isGetToDoCalendarEventRange_User(ToDoCalendarEvent event)
	{
		if(event.getToDo().getJP_ToDo_Type().equals(MToDo.JP_TODO_TYPE_Schedule))
		{
			if(event.getToDo().getJP_ToDo_ScheduledStartTime().compareTo(ts_GetToDoCalendarEventEnd_User) <= 0
					&& event.getToDo().getJP_ToDo_ScheduledEndTime().compareTo(ts_GetToDoCalendarEventBegin_User) >= 0)
			{
				return true;
			}else {
				return false;
			}


		}else if(event.getToDo().getJP_ToDo_Type().equals(MToDo.JP_TODO_TYPE_Task)) {

			if(event.getToDo().getJP_ToDo_ScheduledEndTime().compareTo(ts_GetToDoCalendarEventBegin_User) >= 0
					&& event.getToDo().getJP_ToDo_ScheduledEndTime().compareTo(ts_GetToDoCalendarEventEnd_User) <= 0)
			{
				return true;
			}else {
				return false;
			}

		}

		return true;
	}


	private boolean isGetToDoCalendarEventRange_Team(ToDoCalendarEvent event)
	{
		if(event.getToDo().getJP_ToDo_Type().equals(MToDo.JP_TODO_TYPE_Schedule))
		{
			if(event.getToDo().getJP_ToDo_ScheduledStartTime().compareTo(ts_GetToDoCalendarEventEnd_Team) <= 0
					&& event.getToDo().getJP_ToDo_ScheduledEndTime().compareTo(ts_GetToDoCalendarEventBegin_Team) >= 0)
			{
				return true;
			}else {
				return false;
			}


		}else if(event.getToDo().getJP_ToDo_Type().equals(MToDo.JP_TODO_TYPE_Task)) {

			if(event.getToDo().getJP_ToDo_ScheduledEndTime().compareTo(ts_GetToDoCalendarEventBegin_Team) >= 0
					&& event.getToDo().getJP_ToDo_ScheduledEndTime().compareTo(ts_GetToDoCalendarEventEnd_Team) <= 0)
			{
				return true;
			}else {
				return false;
			}

		}

		return true;
	}


	/**
	 * Update Calendar Modle.
	 *
	 *
	 * @param isUserRequery
	 * @param isTeamRequery
	 * @param update_AD_User_ID
	 */
	private void updateCalendarModel(boolean isUserRequery, boolean isTeamRequery, int update_AD_User_ID)//TODO
	{

		//Query of Main Tab
		if(isUserRequery ||  p_AD_User_ID == update_AD_User_ID)
		{
			ts_GetToDoCalendarEventBegin_User = new Timestamp(map_Calendars.get(p_AD_User_ID).getBeginDate().getTime());
			ts_GetToDoCalendarEventEnd_User = new Timestamp(map_Calendars.get(p_AD_User_ID).getEndDate().getTime());
			map_CalendarEvent_User.clear();

			queryToDoCalendarEvents_User();

		}else {

			Timestamp calendar_Begin = new Timestamp(map_Calendars.get(p_AD_User_ID).getBeginDate().getTime());
			Timestamp calendar_End =  new Timestamp(map_Calendars.get(p_AD_User_ID).getEndDate().getTime());
			Timestamp temp_Begin = ts_GetToDoCalendarEventBegin_User;
			Timestamp temp_End = ts_GetToDoCalendarEventEnd_User;

			if(calendar_Begin.compareTo(ts_GetToDoCalendarEventBegin_User) < 0) // calendar_Begin < now
			{
				ts_GetToDoCalendarEventBegin_User = calendar_Begin;

				if(ts_GetToDoCalendarEventEnd_User.compareTo(calendar_End) > 0) //calendar_Begin < now  > calender_End
				{
					ts_GetToDoCalendarEventEnd_User = temp_Begin;
					queryToDoCalendarEvents_User();
					ts_GetToDoCalendarEventEnd_User = temp_End;

				}else { // calendar_Begin <  now < calender_End

					ts_GetToDoCalendarEventEnd_User = calendar_End;
					map_CalendarEvent_User.clear();
					queryToDoCalendarEvents_User();
				}

			}else { //  calendar_Begin >= now

				if(ts_GetToDoCalendarEventEnd_User.compareTo(calendar_End) >= 0) // calender_End  <  now
				{
					;// Noting to do;

				}else { // calender_End  >=  End

					ts_GetToDoCalendarEventBegin_User = temp_End;
					ts_GetToDoCalendarEventEnd_User = calendar_End;
					queryToDoCalendarEvents_User();
					ts_GetToDoCalendarEventBegin_User = temp_Begin;
				}
			}

		}

		//Query of Sub Tab
		if(isTeamRequery)
		{
			if(p_AD_User_ID == update_AD_User_ID)
			{
				queryToDoCalendarEvents_Team(-1);
			}else if (update_AD_User_ID > 0 ){
				queryToDoCalendarEvents_Team(update_AD_User_ID);
			}else {
				ts_GetToDoCalendarEventBegin_Team= new Timestamp(map_Calendars.get(p_AD_User_ID).getBeginDate().getTime());
				ts_GetToDoCalendarEventEnd_Team = new Timestamp(map_Calendars.get(p_AD_User_ID).getEndDate().getTime());
				map_CalendarEvent_Team.clear();

				queryToDoCalendarEvents_Team(0);
			}

		}else if(p_JP_Team_ID > 0) {

			Timestamp calendar_Begin = new Timestamp(map_Calendars.get(p_AD_User_ID).getBeginDate().getTime());
			Timestamp calendar_End =  new Timestamp(map_Calendars.get(p_AD_User_ID).getEndDate().getTime());
			Timestamp temp_Begin = ts_GetToDoCalendarEventBegin_Team;
			Timestamp temp_End = ts_GetToDoCalendarEventEnd_Team;

			if(calendar_Begin.compareTo(ts_GetToDoCalendarEventBegin_Team) < 0) // calendar_Begin < now
			{
				ts_GetToDoCalendarEventBegin_Team = calendar_Begin;

				if(ts_GetToDoCalendarEventEnd_Team.compareTo(calendar_End) > 0) //calendar_Begin < now  > calender_End
				{
					ts_GetToDoCalendarEventEnd_Team = temp_Begin;
					queryToDoCalendarEvents_Team(0);
					ts_GetToDoCalendarEventEnd_Team = temp_End;

				}else { // calendar_Begin <  now < calender_End

					ts_GetToDoCalendarEventEnd_Team = calendar_End;
					map_CalendarEvent_Team.clear();

					queryToDoCalendarEvents_Team(0);
				}

			}else { //  calendar_Begin >= now

				if(ts_GetToDoCalendarEventEnd_Team.compareTo(calendar_End) >= 0) // calender_End  <  now
				{
					;// Noting to do;

				}else { // calender_End  >=  End

					ts_GetToDoCalendarEventBegin_Team = temp_End;
					ts_GetToDoCalendarEventEnd_Team = calendar_End;
					queryToDoCalendarEvents_Team(0);
					ts_GetToDoCalendarEventBegin_Team = temp_Begin;
				}
			}

		}


		//Update Calenders Model
		SimpleCalendarModel scm =null;
		Calendars calendars = map_Calendars.get(p_SelectedTab_AD_User_ID);
		if(calendars == null)
		{
			Calendars from = map_Calendars.get(p_OldSelectedTab_AD_User_ID);
			if(from == null)
				from = map_Calendars.get(p_AD_User_ID);
			calendars = createSyncCalendars(from);
			map_Calendars.put(p_SelectedTab_AD_User_ID, calendars);
		}

		CalendarModel  cm = calendars.getModel();
		if(cm == null)
		{
			scm = new SimpleCalendarModel();
		}else {
			scm = (SimpleCalendarModel)cm;
		}

		scm.clear();

		HashMap<Integer, ToDoCalendarEvent> map_CalEvents = null;
		if(p_SelectedTab_AD_User_ID == p_AD_User_ID) //Main Tab
		{
			map_CalEvents = map_CalendarEvent_User.get(p_AD_User_ID);
			if(map_CalEvents != null)
			{
				Set<Integer> keySet = map_CalEvents.keySet();
				ToDoCalendarEvent toDoCalEvent = null;
				for (Integer JP_ToDo_ID : keySet)
				{
					toDoCalEvent = map_CalEvents.get(JP_ToDo_ID);

					if(isSkip(toDoCalEvent))
						continue;

					setEventTextAndColor(toDoCalEvent);
					scm.add(toDoCalEvent);
				}
			}


			if(MGroupwareUser.JP_TODO_MAIN_CALENDAR_VIEW_Team.equals(p_JP_ToDo_Main_Calendar_View) && p_JP_Team_ID > 0 && m_Team != null)
			{
				int tabSize = tabbox.getTabs().getChildren().size();
				Tabpanel tabpanel = null;
				int AD_User_ID = 0;
				for(int i = 0; i < tabSize; i++)
				{
					tabpanel = tabbox.getTabpanel(i);
					AD_User_ID = ((Integer)tabpanel.getAttribute("AD_User_ID")).intValue();

					if(p_AD_User_ID == AD_User_ID)
						continue;


					map_CalEvents = map_CalendarEvent_Team.get(AD_User_ID);
					if(map_CalEvents != null)
					{
						Set<Integer> keySet = map_CalEvents.keySet();
						ToDoCalendarEvent toDoCalEvent = null;
						for (Integer JP_ToDo_ID : keySet)
						{
							toDoCalEvent = map_CalEvents.get(JP_ToDo_ID);

							if(isSkip(toDoCalEvent))
								continue;

							setEventTextAndColor(toDoCalEvent);
							scm.add(toDoCalEvent);
						}
					}

				}//for

			}

		}else {//Sub Tab


			map_CalEvents = map_CalendarEvent_Team.get(p_SelectedTab_AD_User_ID);
			if(map_CalEvents != null)
			{
				Set<Integer> keySet = map_CalEvents.keySet();
				ToDoCalendarEvent toDoCalEvent = null;
				for (Integer JP_ToDo_ID : keySet)
				{
					toDoCalEvent = map_CalEvents.get(JP_ToDo_ID);

					if(isSkip(toDoCalEvent))
						continue;

					setEventTextAndColor(toDoCalEvent);
					scm.add(toDoCalEvent);
				}
			}


		}

		calendars.setModel(scm);
	}


	private void resetSelectedTabCalendarModel()//TODO
	{
		//Reset Update Calenders Model
		SimpleCalendarModel scm =null;
		Calendars calendars = map_Calendars.get(p_SelectedTab_AD_User_ID);
		if(calendars == null)
		{
			Calendars from = map_Calendars.get(p_OldSelectedTab_AD_User_ID);
			if(from == null)
				from = map_Calendars.get(p_AD_User_ID);
			calendars = createSyncCalendars(from);
			map_Calendars.put(p_SelectedTab_AD_User_ID, calendars);
		}

		CalendarModel  cm = calendars.getModel();
		if(cm == null)
		{
			scm = new SimpleCalendarModel();
		}else {
			scm = (SimpleCalendarModel)cm;
		}

		scm.clear();

		HashMap<Integer, ToDoCalendarEvent> map_CalEvents = null;
		if(p_SelectedTab_AD_User_ID == p_AD_User_ID) //Main Tab
		{
			map_CalEvents = map_CalendarEvent_User.get(p_AD_User_ID);
			if(map_CalEvents != null)
			{
				Set<Integer> keySet = map_CalEvents.keySet();
				ToDoCalendarEvent toDoCalEvent = null;
				for (Integer JP_ToDo_ID : keySet)
				{
					toDoCalEvent = map_CalEvents.get(JP_ToDo_ID);

					if(isSkip(toDoCalEvent))
						continue;

					setEventTextAndColor(toDoCalEvent);
					scm.add(toDoCalEvent);
				}
			}


			if(MGroupwareUser.JP_TODO_MAIN_CALENDAR_VIEW_Team.equals(p_JP_ToDo_Main_Calendar_View) && p_JP_Team_ID > 0 && m_Team != null)
			{
				int tabSize = tabbox.getTabs().getChildren().size();
				Tabpanel tabpanel = null;
				int AD_User_ID = 0;
				for(int i = 0; i < tabSize; i++)
				{
					tabpanel = tabbox.getTabpanel(i);
					AD_User_ID = ((Integer)tabpanel.getAttribute("AD_User_ID")).intValue();

					if(p_AD_User_ID == AD_User_ID)
						continue;


					map_CalEvents = map_CalendarEvent_Team.get(AD_User_ID);
					if(map_CalEvents != null)
					{
						Set<Integer> keySet = map_CalEvents.keySet();
						ToDoCalendarEvent toDoCalEvent = null;
						for (Integer JP_ToDo_ID : keySet)
						{
							toDoCalEvent = map_CalEvents.get(JP_ToDo_ID);

							if(isSkip(toDoCalEvent))
								continue;

							setEventTextAndColor(toDoCalEvent);
							scm.add(toDoCalEvent);
						}
					}

				}//for

			}

		}else {//Sub Tab


			map_CalEvents = map_CalendarEvent_Team.get(p_SelectedTab_AD_User_ID);
			if(map_CalEvents != null)
			{
				Set<Integer> keySet = map_CalEvents.keySet();
				ToDoCalendarEvent toDoCalEvent = null;
				for (Integer JP_ToDo_ID : keySet)
				{
					toDoCalEvent = map_CalEvents.get(JP_ToDo_ID);

					if(isSkip(toDoCalEvent))
						continue;

					setEventTextAndColor(toDoCalEvent);
					scm.add(toDoCalEvent);
				}
			}


		}

		calendars.setModel(scm);
	}


	/**
	 * Judge of to display ToDo.
	 *
	 * @param event
	 * @return
	 */
	private boolean isSkip(ToDoCalendarEvent event)
	{
		if(!p_IsDisplaySchedule)
		{
			if(event.getToDo().getJP_ToDo_Type().equals(MToDo.JP_TODO_TYPE_Schedule))
				return true;
		}

		if(!p_IsDisplayTask)
		{
			if(event.getToDo().getJP_ToDo_Type().equals(MToDo.JP_TODO_TYPE_Task))
				return true;
		}

		if(p_JP_ToDo_Category_ID > 0)
		{
			if(event.getToDo().getJP_ToDo_Category_ID() != p_JP_ToDo_Category_ID)
				return true;
		}

		if(!Util.isEmpty(p_JP_ToDo_Status))
		{
			if(MGroupwareUser.JP_TODO_STATUS_NotCompleted.equals(p_JP_ToDo_Status))
			{
				if(event.getToDo().getJP_ToDo_Status().equals(MGroupwareUser.JP_TODO_STATUS_NotYetStarted)
						|| event.getToDo().getJP_ToDo_Status().equals(MGroupwareUser.JP_TODO_STATUS_WorkInProgress))
				{
					;//Noting to do;

				}else {

					return true;
				}
			}else {

				if(!event.getToDo().getJP_ToDo_Status().equals(p_JP_ToDo_Status))
					return true;
			}

		}

		return false;
	}



	Timestamp ts_GetToDoCalendarEventBegin_User = null;
	Timestamp ts_GetToDoCalendarEventEnd_User = null;

	/**
	 * Get Main User's Calendar Event.
	 */
    private void queryToDoCalendarEvents_User()
    {

		StringBuilder whereClauseFinal = null;
		StringBuilder whereClauseSchedule = null;
		StringBuilder whereClauseTask = null;
		StringBuilder orderClause = null;
		ArrayList<Object> list_parameters  = new ArrayList<Object>();
		Object[] parameters = null;


		if(ts_GetToDoCalendarEventBegin_User == null)
			ts_GetToDoCalendarEventBegin_User = new Timestamp(map_Calendars.get(p_AD_User_ID).getBeginDate().getTime());

		if(ts_GetToDoCalendarEventEnd_User == null)
			ts_GetToDoCalendarEventEnd_User = new Timestamp(map_Calendars.get(p_AD_User_ID).getEndDate().getTime());

		LocalDateTime toDayMin = LocalDateTime.of(ts_GetToDoCalendarEventBegin_User.toLocalDateTime().toLocalDate(), LocalTime.MIN);
		LocalDateTime toDayMax = LocalDateTime.of(ts_GetToDoCalendarEventEnd_User.toLocalDateTime().toLocalDate(), LocalTime.MAX);

		//JP_ToDo_ScheduledStartTime
		whereClauseSchedule = new StringBuilder(" JP_ToDo_ScheduledStartTime <= ? AND JP_ToDo_ScheduledEndTime >= ? AND IsActive='Y' ");//1 - 2
		orderClause = new StringBuilder("AD_User_ID, JP_ToDo_ScheduledStartTime");

		list_parameters.add(Timestamp.valueOf(toDayMax));
		list_parameters.add(Timestamp.valueOf(toDayMin));

		//JP_TODO_TYPE_Schedule
		whereClauseSchedule = whereClauseSchedule.append(" AND JP_ToDo_Type = ? ");
		list_parameters.add(MToDo.JP_TODO_TYPE_Schedule);

		//AD_User_ID
		whereClauseSchedule = whereClauseSchedule.append(" AND AD_User_ID = ? ");
		list_parameters.add(p_AD_User_ID);

		if(p_login_User_ID == p_AD_User_ID)
		{
			//Noting to do;

		}else {

			whereClauseSchedule = whereClauseSchedule.append(" AND (IsOpenToDoJP='Y' OR CreatedBy = ?)");
			list_parameters.add(p_login_User_ID);
		}


		//JP_ToDo_ScheduledStartTime
		whereClauseTask = new StringBuilder(" JP_ToDo_ScheduledEndTime <= ? AND JP_ToDo_ScheduledEndTime >= ? AND IsActive='Y' ");//1 - 2
		orderClause = new StringBuilder("AD_User_ID, JP_ToDo_ScheduledEndTime");

		list_parameters.add(Timestamp.valueOf(toDayMax));
		list_parameters.add(Timestamp.valueOf(toDayMin));

		//JP_TODO_TYPE_Schedule
		whereClauseTask = whereClauseTask.append(" AND JP_ToDo_Type = ? ");
		list_parameters.add(MToDo.JP_TODO_TYPE_Task);

		//AD_User_ID
		whereClauseTask = whereClauseTask.append(" AND AD_User_ID = ? ");
		list_parameters.add(p_AD_User_ID);

		if(p_login_User_ID == p_AD_User_ID)
		{
			//Noting to do;

		}else {
			whereClauseTask = whereClauseTask.append(" AND (IsOpenToDoJP='Y' OR CreatedBy = ?)");
			list_parameters.add(p_login_User_ID);
		}


		whereClauseFinal = new StringBuilder("(").append( whereClauseSchedule.append(") OR (").append(whereClauseTask).append(")") );



		parameters = list_parameters.toArray(new Object[list_parameters.size()]);

		List<MToDo> list_ToDoes = new Query(Env.getCtx(), MToDo.Table_Name, whereClauseFinal.toString(), null)
											.setParameters(parameters)
											.setOrderBy(orderClause.toString())
											.list();


		if(list_ToDoes == null || list_ToDoes.size() == 0)
		{
			return ;
		}

		HashMap<Integer,ToDoCalendarEvent> eventMap = null;
		ToDoCalendarEvent event = null;

		for(MToDo todo :list_ToDoes)
		{
			event = new ToDoCalendarEvent(todo);
			eventMap = map_CalendarEvent_User.get(p_AD_User_ID);
			if(eventMap == null)
			{
				eventMap = new HashMap<Integer, ToDoCalendarEvent>();
				eventMap.put(todo.getJP_ToDo_ID(), event);
				map_CalendarEvent_User.put(p_AD_User_ID, eventMap);
			}else {
				eventMap.put(todo.getJP_ToDo_ID(), event);
			}

		}//for

		return ;
    }



	Timestamp ts_GetToDoCalendarEventBegin_Team = null;
	Timestamp ts_GetToDoCalendarEventEnd_Team = null;

	/**
	 * Get Team User's Calendar Event.
	 */
    private void queryToDoCalendarEvents_Team(int AD_User_ID)
    {
    	if(AD_User_ID < 0 )
    		return ;

    	if(p_JP_Team_ID == 0 || m_Team == null)
    	{
    		return ;
    	}

		StringBuilder whereClauseFinal = null;
		StringBuilder whereClauseSchedule = null;
		StringBuilder whereClauseTask = null;
		StringBuilder orderClause = null;
		ArrayList<Object> list_parameters  = new ArrayList<Object>();
		Object[] parameters = null;

		if(ts_GetToDoCalendarEventBegin_Team == null)
			ts_GetToDoCalendarEventBegin_Team = new Timestamp(map_Calendars.get(p_SelectedTab_AD_User_ID).getBeginDate().getTime());

		if(ts_GetToDoCalendarEventEnd_Team == null)
			ts_GetToDoCalendarEventEnd_Team = new Timestamp(map_Calendars.get(p_SelectedTab_AD_User_ID).getEndDate().getTime());

		LocalDateTime toDayMin = LocalDateTime.of(ts_GetToDoCalendarEventBegin_Team.toLocalDateTime().toLocalDate(), LocalTime.MIN);
		LocalDateTime toDayMax = LocalDateTime.of(ts_GetToDoCalendarEventEnd_Team.toLocalDateTime().toLocalDate(), LocalTime.MAX);


		//JP_ToDo_ScheduledStartTime
		whereClauseSchedule = new StringBuilder(" JP_ToDo_ScheduledStartTime <= ? AND JP_ToDo_ScheduledEndTime >= ? AND IsActive='Y' ");//1 - 2
		orderClause = new StringBuilder("AD_User_ID, JP_ToDo_ScheduledStartTime");

		list_parameters.add(Timestamp.valueOf(toDayMax));
		list_parameters.add(Timestamp.valueOf(toDayMin));

		//JP_TODO_TYPE_Schedule
		whereClauseSchedule = whereClauseSchedule.append(" AND JP_ToDo_Type = ? ");
		list_parameters.add(MToDo.JP_TODO_TYPE_Schedule);

		//AD_User_ID
		if(AD_User_ID > 0)
		{
			whereClauseSchedule = whereClauseSchedule.append(" AND AD_User_ID = ? ");
			list_parameters.add(AD_User_ID);

		}else {

			whereClauseSchedule = whereClauseSchedule.append(" AND AD_User_ID IN (").append(createInUserClause(list_parameters)).append(")");
		}

		whereClauseSchedule = whereClauseSchedule.append(" AND (IsOpenToDoJP='Y' OR CreatedBy = ?)");
		list_parameters.add(p_login_User_ID);



		//JP_ToDo_ScheduledStartTime
		whereClauseTask = new StringBuilder(" JP_ToDo_ScheduledEndTime <= ? AND JP_ToDo_ScheduledEndTime >= ? AND IsActive='Y' ");//1 - 2
		orderClause = new StringBuilder("AD_User_ID, JP_ToDo_ScheduledEndTime");

		list_parameters.add(Timestamp.valueOf(toDayMax));
		list_parameters.add(Timestamp.valueOf(toDayMin));

		//JP_TODO_TYPE_Schedule
		whereClauseTask = whereClauseTask.append(" AND JP_ToDo_Type = ? ");
		list_parameters.add(MToDo.JP_TODO_TYPE_Task);

		//AD_User_ID
		whereClauseTask = whereClauseTask.append(" AND AD_User_ID IN (").append(createInUserClause(list_parameters)).append(")");


		whereClauseTask = whereClauseTask.append(" AND (IsOpenToDoJP='Y' OR CreatedBy = ?)");
		list_parameters.add(p_login_User_ID);


		whereClauseFinal = new StringBuilder("(").append( whereClauseSchedule.append(") OR (").append(whereClauseTask).append(")") );

		parameters = list_parameters.toArray(new Object[list_parameters.size()]);


		List<MToDo> list_ToDoes = new Query(Env.getCtx(), MToDo.Table_Name, whereClauseFinal.toString(), null)
										.setParameters(parameters)
										.setOrderBy(orderClause.toString())
										.list();

		if(list_ToDoes == null || list_ToDoes.size() == 0)
		{
			return ;
		}

		HashMap<Integer,ToDoCalendarEvent> eventMap = null;
		ToDoCalendarEvent event = null;

		for(MToDo todo :list_ToDoes)
		{
			event = new ToDoCalendarEvent(todo);
			eventMap = map_CalendarEvent_Team.get(event.getToDo().getAD_User_ID());
			if(eventMap == null)
			{
				eventMap = new HashMap<Integer, ToDoCalendarEvent>();
				eventMap.put(todo.getJP_ToDo_ID(), event);
				map_CalendarEvent_Team.put(event.getToDo().getAD_User_ID(), eventMap);
			}else {
				eventMap.put(todo.getJP_ToDo_ID(), event);
			}

		}//for

		return ;
    }



    /**
     * Create SQL Sentence of AD_User_ID In ().
     *
     * @param list_parameters
     * @return
     */
    private String createInUserClause(ArrayList<Object> list_parameters)
    {

    	StringBuilder users = null;
    	String Q = ",?";

    	MTeamMember[] member = m_Team.getTeamMember();
    	for(int i = 0; i < member.length; i++)
    	{
    		if(p_AD_User_ID != member[i].getAD_User_ID())
    		{
    			if(users == null)
    			{
    				users = new StringBuilder("?");
    				list_parameters.add(member[i].getAD_User_ID());

    			}else {

		    		users = users.append(Q);
		    		list_parameters.add(member[i].getAD_User_ID());
    			}
    		}
    	}

    	return users.toString();

    }



    /**
     *
     * Set Text and Color to Calendar Event.
     *
     * @param event
     */
	private void setEventTextAndColor(ToDoCalendarEvent event)
	{

		Calendars cal = map_Calendars.get(p_SelectedTab_AD_User_ID);

		if(MGroupwareUser.JP_TODO_MAIN_CALENDAR_VIEW_Team.equals(p_JP_ToDo_Main_Calendar_View) && p_JP_Team_ID > 0 && p_SelectedTab_AD_User_ID == p_AD_User_ID)
		{

			if(cal.getMold().equalsIgnoreCase("MONTH"))
			{
				if(event.isShortTime)
				{
					event.setTitle(event.team_Month_Short_Title);
					event.setContent(event.team_Month_Short_Content);
					event.setHeaderColor(event.team_Month_Short_HeaderColor);
					event.setContentColor(event.team_Month_Short_ContentColor);

				}else if(event.isMiddleTime) {

					event.setTitle(event.team_Month_Middle_Title);
					event.setContent(event.team_Month_Middle_Content);
					event.setHeaderColor(event.team_Month_Middle_HeaderColor);
					event.setContentColor(event.team_Month_Middle_ContentColor);

				}else {

					event.setTitle(event.team_Month_Long_Title);
					event.setContent(event.team_Month_Long_Content);
					event.setHeaderColor(event.team_Month_Long_HeaderColor);
					event.setContentColor(event.team_Month_Long_ContentColor);
				}

			}else {

				if(event.isShortTime)
				{
					event.setTitle(event.team_Default_Short_Title);
					event.setContent(event.team_Default_Short_Content);
					event.setHeaderColor(event.team_Default_Short_HeaderColor);
					event.setContentColor(event.team_Default_Short_ContentColor);

				}else if(event.isMiddleTime) {

					event.setTitle(event.team_Default_Middle_Title);
					event.setContent(event.team_Default_Middle_Content);
					event.setHeaderColor(event.team_Default_Middle_HeaderColor);
					event.setContentColor(event.team_Default_Middle_ContentColor);

				}else {

					event.setTitle(event.team_Default_Long_Title);
					event.setContent(event.team_Default_Long_Content);
					event.setHeaderColor(event.team_Default_Long_HeaderColor);
					event.setContentColor(event.team_Default_Long_ContentColor);
				}
			}


		}else {


			if(cal.getMold().equalsIgnoreCase("MONTH"))
			{
				if(event.isShortTime)
				{
					event.setTitle(event.personal_Month_Short_Title);
					event.setContent(event.personal_Month_Short_Content);
					event.setHeaderColor(event.personal_Month_Short_HeaderColor);
					event.setContentColor(event.personal_Month_Short_ContentColor);

				}else if(event.isMiddleTime) {

					event.setTitle(event.personal_Month_Middle_Title);
					event.setContent(event.personal_Month_Middle_Content);
					event.setHeaderColor(event.personal_Month_Middle_HeaderColor);
					event.setContentColor(event.personal_Month_Middle_ContentColor);

				}else {

					event.setTitle(event.personal_Month_Long_Title);
					event.setContent(event.personal_Month_Long_Content);
					event.setHeaderColor(event.personal_Month_Long_HeaderColor);
					event.setContentColor(event.personal_Month_Long_ContentColor);
				}

			}else {

				if(event.isShortTime)
				{
					event.setTitle(event.personal_Default_Short_Title);
					event.setContent(event.personal_Default_Short_Content);
					event.setHeaderColor(event.personal_Default_Short_HeaderColor);
					event.setContentColor(event.personal_Default_Short_ContentColor);

				}else if(event.isMiddleTime) {

					event.setTitle(event.personal_Default_Middle_Title);
					event.setContent(event.personal_Default_Middle_Content);
					event.setHeaderColor(event.personal_Default_Middle_HeaderColor);
					event.setContentColor(event.personal_Default_Middle_ContentColor);

				}else {

					event.setTitle(event.personal_Default_Long_Title);
					event.setContent(event.personal_Default_Long_Content);
					event.setHeaderColor(event.personal_Default_Long_HeaderColor);
					event.setContentColor(event.personal_Default_Long_ContentColor);
				}
			}

		}

	}



	/**
	 * Get Personal ToDo List (Implement of I_CallerToDoPopupwindow)
	 */
	@Override
	public List<MToDo> getPersonalToDoList()
	{
		return list_ToDoes;
	}


	/**
	 * Get Default AD_User_ID (Implement of I_CallerToDoPopupwindow)
	 */
	@Override
	public int getDefault_AD__User_ID()
	{
		return p_SelectedTab_AD_User_ID;
	}


	/**
	 * Get Default JP_ToDo_Category_ID (Implement of I_CallerToDoPopupwindow)
	 */
	@Override
	public int getDefault_JP_ToDo_Category_ID()
	{
		return p_JP_ToDo_Category_ID;
	}


	/**
	 * Get Default JP_ToDo_Type (Implement of I_CallerToDoPopupwindow)
	 */
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

	@Override
	public boolean update(MToDo todo)
	{
		ToDoCalendarEvent oldEvent = null;
		ToDoCalendarEvent newEvent = null;
		if(todo.getAD_User_ID() == p_AD_User_ID)
		{
			oldEvent = map_CalendarEvent_User.get(todo.getAD_User_ID()).get(todo.getJP_ToDo_ID());
			map_CalendarEvent_User.get(todo.getAD_User_ID()).remove(todo.getJP_ToDo_ID());

			newEvent = new ToDoCalendarEvent(todo);
			map_CalendarEvent_User.get(todo.getAD_User_ID()).put(todo.getJP_ToDo_ID(), newEvent);

		}else {

			oldEvent = map_CalendarEvent_Team.get(todo.getAD_User_ID()).get(todo.getJP_ToDo_ID());
			map_CalendarEvent_Team.get(todo.getAD_User_ID()).remove(todo.getJP_ToDo_ID());

			newEvent = new ToDoCalendarEvent(todo);
			map_CalendarEvent_Team.get(todo.getAD_User_ID()).put(todo.getJP_ToDo_ID(), newEvent);
		}

		updateCalendarEvent(oldEvent, newEvent);

		return true;
	}

	@Override
	public boolean create(MToDo todo)
	{
		ToDoCalendarEvent newEvent = null;
		if(todo.getAD_User_ID() == p_AD_User_ID)
		{
			newEvent = new ToDoCalendarEvent(todo);
			map_CalendarEvent_User.get(todo.getAD_User_ID()).put(todo.getJP_ToDo_ID(), newEvent);

		}else {

			newEvent = new ToDoCalendarEvent(todo);
			map_CalendarEvent_Team.get(todo.getAD_User_ID()).put(todo.getJP_ToDo_ID(), newEvent);
		}

		createCalendarEvent(newEvent);

		return true;
	}

	@Override
	public boolean delete(MToDo deleteToDo)
	{

		ToDoCalendarEvent deleteEvent = null;
		if(deleteToDo.getAD_User_ID() == p_AD_User_ID)
		{
			deleteEvent = map_CalendarEvent_User.get(deleteToDo.getAD_User_ID()).get(deleteToDo.getJP_ToDo_ID());
			map_CalendarEvent_User.get(deleteToDo.getAD_User_ID()).remove(deleteToDo.getJP_ToDo_ID());


		}else {

			deleteEvent = map_CalendarEvent_Team.get(deleteToDo.getAD_User_ID()).get(deleteToDo.getJP_ToDo_ID());
			map_CalendarEvent_Team.get(deleteToDo.getAD_User_ID()).remove(deleteToDo.getJP_ToDo_ID());

		}

		deleteCalendarEvent(deleteEvent);


		return true;
	}

	/**
	 * Refresh (Implement of I_CallerToDoPopupwindow)
	 */
//	@Override
//	public boolean refresh(int AD_User_ID, String JP_ToDo_Type , boolean isRefreshChain)
//	{
//
//		updateCalendarModel(false,true, AD_User_ID);
//		refreshWest(JP_ToDo_Type, false);
//
//		return true;
//	}


	/**
	 * Refresh West components of Borderlayout
	 *
	 * @param JP_ToDo_Type
	 * @return boolean
	 */
	private boolean refreshWest(String JP_ToDo_Type,boolean isRefreshChain)
	{
		personalToDoGadget_Schedule.setAD_User_ID(p_AD_User_ID);
		personalToDoGadget_Task.setAD_User_ID(p_AD_User_ID);
		personalToDoGadget_Memo.setAD_User_ID(p_AD_User_ID);

//		if(Util.isEmpty(JP_ToDo_Type))
//		{
//			personalToDoGadget_Schedule.refresh(p_AD_User_ID, MToDo.JP_TODO_TYPE_Schedule,isRefreshChain);
//			personalToDoGadget_Task.refresh(p_AD_User_ID, MToDo.JP_TODO_TYPE_Task ,isRefreshChain);
//			personalToDoGadget_Memo.refresh(p_AD_User_ID, MToDo.JP_TODO_TYPE_Memo, isRefreshChain);
//		}else {
//			personalToDoGadget_Schedule.refresh(p_AD_User_ID, JP_ToDo_Type, isRefreshChain);
//			personalToDoGadget_Task.refresh(p_AD_User_ID, JP_ToDo_Type, isRefreshChain);
//			personalToDoGadget_Memo.refresh(p_AD_User_ID, JP_ToDo_Type, isRefreshChain);
//		}

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
