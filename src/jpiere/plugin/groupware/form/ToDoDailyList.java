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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.ToolBarButton;
import org.adempiere.webui.editor.WDateEditor;
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
import org.compiere.model.MRole;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.Query;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.calendar.api.CalendarEvent;
import org.zkoss.calendar.event.CalendarsEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
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

import jpiere.plugin.groupware.model.I_ToDo;
import jpiere.plugin.groupware.model.MGroupwareUser;
import jpiere.plugin.groupware.model.MTeam;
import jpiere.plugin.groupware.model.MTeamMember;
import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoCategory;
import jpiere.plugin.groupware.model.MToDoTeam;
import jpiere.plugin.groupware.util.GroupwareToDoUtil;
import jpiere.plugin.groupware.window.I_ToDoCalendarEventReceiver;
import jpiere.plugin.groupware.window.I_ToDoPopupwindowCaller;
import jpiere.plugin.groupware.window.ToDoPopupWindow;;

/**
 *
 * JPIERE-0477: ToDo Daily List
 *
 * h.hagiwara
 *
 */
public class ToDoDailyList implements I_ToDoPopupwindowCaller, I_ToDoCalendarEventReceiver, IFormController, EventListener<Event>, ValueChangeListener {

	//private static CLogger log = CLogger.getCLogger(ToDoCalendar.class);

	private CustomForm form;

	private Properties ctx = Env.getCtx();

	@Override
	public ADForm getForm()
	{
		return form;
	}

	/** ToDo Controler **/

	//HashMap<LocalDate, HashMap<AD_User_ID, HashMap<Integer, CalendarEvent>>
	private HashMap<LocalDate, HashMap<Integer, ArrayList<ToDoCalendarEvent>>> map_AcquiredCalendarEvent_User = new HashMap<LocalDate, HashMap<Integer, ArrayList<ToDoCalendarEvent>>>();
	private HashMap<LocalDate, HashMap<Integer, ArrayList<ToDoCalendarEvent>>> map_AcquiredCalendarEvent_Team = new HashMap<LocalDate, HashMap<Integer, ArrayList<ToDoCalendarEvent>>>();

	private HashMap<LocalDate, String> map_DayOfWeek = new HashMap<LocalDate, String> ();

	/** Parameters **/
	private int p_login_User_ID = 0;
	private int p_AD_User_ID = 0;

	private int p_JP_Team_ID = 0;
	private MTeam m_Team = null;

	private int p_JP_ToDo_Category_ID = 0;
	private String p_JP_ToDo_Status = null ;
	private boolean p_IsDisplaySchedule = true;
	private boolean p_IsDisplayTask = false;



	private String p_JP_ToDo_Calendar = MGroupwareUser.JP_TODO_CALENDAR_PersonalToDo;


	private MGroupwareUser m_GroupwareUser = null;
	private MRole m_Role = MRole.getDefault();


	/** Noth Components **/
	private WSearchEditor editor_AD_User_ID;
	private WSearchEditor editor_JP_ToDo_Category_ID;
	private WSearchEditor editor_JP_Team_ID ;
	private WYesNoEditor  editor_IsDisplaySchedule ;
	private WYesNoEditor  editor_IsDisplayTask ;
	private WTableDirEditor editor_JP_ToDo_Calendar ;
	private WDateEditor editor_Date = null;
	private final static String EDITOR_DATE = "DATE";

	private WNumberEditor editor_Days = null;
	private final static String EDITOR_DAYS = "DAYS";
	private int p_Days = 5;

	private MLookup lookup_JP_ToDo_Category_ID;
	private MLookup lookup_JP_ToDo_Calendar;

	private Button leftBtn ;
	private Button rightBtn ;

	/** Center **/
	private Center mainBorderLayout_Center;
	private Vlayout center ;
	private Div center_UserToDo;
	private Div center_TeamToDo;



	/** West Components **/
	ToDoGadget personalToDoGadget_Schedule = null;
	ToDoGadget personalToDoGadget_Task = null;
	ToDoGadget personalToDoGadget_Memo = null;

	ToDoGadget teamToDoGadget_Schedule = null;
	ToDoGadget teamToDoGadget_Task = null;
	ToDoGadget teamToDoGadget_Memo = null;


	//Popup
	private CalendarEventPopup popup_CalendarEvent = new CalendarEventPopup();


	/** Label **/
	private Label label_AD_User_ID ;
	private Label label_JP_ToDo_Category_ID ;
	private Label label_JP_Team_ID ;
	private Label label_JP_ToDo_Calendar;


	/** Interface **/
	private List<I_ToDo> list_ToDoes = null;


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


	LocalDateTime p_LocalDateTime =  null;


	/**
	 * Constructor
	 */
    public ToDoDailyList()
    {
		p_AD_User_ID = Env.getAD_User_ID(ctx);
		p_login_User_ID = p_AD_User_ID;

		p_LocalDateTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN);

		initZk();

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

		queryDailyToDo_User();
		center_UserToDo = renderDailyToDoList_User();
		//center_TeamToDo = createTeamDailyToDo();

		center = new Vlayout();
		center.setDroppable("false");
		ZKUpdateUtil.setWidth(center, "100%");
		ZKUpdateUtil.setHeight(center, "100%");
		ZKUpdateUtil.setHflex(center, "1");
		ZKUpdateUtil.setVflex(center, "1");
		center.setStyle("overflow-y:scroll;");
		center.appendChild(center_UserToDo);

		mainBorderLayout_Center.appendChild(center);


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

		//JP_ToDo_Calendar
		lookup_JP_ToDo_Calendar = MLookupFactory.get(ctx, 0,  0, MColumn.getColumn_ID(MGroupwareUser.Table_Name, MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar),  DisplayType.List);
		editor_JP_ToDo_Calendar= new WTableDirEditor(MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar, true, false, true, lookup_JP_ToDo_Calendar);
		editor_JP_ToDo_Calendar.setValue(p_JP_ToDo_Calendar);
		editor_JP_ToDo_Calendar.addValueChangeListener(this);
		label_JP_ToDo_Calendar = new Label(Msg.getElement(ctx, MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar));
		row.appendChild(GroupwareToDoUtil.createLabelDiv(editor_JP_ToDo_Calendar, label_JP_ToDo_Calendar, true));
		row.appendChild(editor_JP_ToDo_Calendar.getComponent());

		row.appendChild(GroupwareToDoUtil.getDividingLine());

		//Create New ToDo Button
		Button createNewToDo = new Button();
		createNewToDo.setImage(ThemeManager.getThemeResource("images/New16.png"));
		createNewToDo.setName(BUTTON_NEW);
		createNewToDo.addEventListener(Events.ON_CLICK, this);
		createNewToDo.setId(String.valueOf(0));
		createNewToDo.setLabel(Msg.getMsg(ctx, "NewRecord"));
		ZKUpdateUtil.setHflex(createNewToDo, "true");
		row.appendCellChild(createNewToDo,2);
		row.appendChild(GroupwareToDoUtil.createSpaceDiv());

		//Refresh Button
		Button refresh = new Button();
		refresh.setImage(ThemeManager.getThemeResource("images/Refresh16.png"));
		refresh.setName(BUTTON_REFRESH);
		refresh.addEventListener(Events.ON_CLICK, this);
		refresh.setLabel(Msg.getMsg(ctx, "Refresh"));
		ZKUpdateUtil.setHflex(refresh, "true");
		row.appendCellChild(refresh, 2);


		row.appendChild(GroupwareToDoUtil.getDividingLine());


		leftBtn = new Button();
		leftBtn.setImage(ThemeManager.getThemeResource("images/MoveLeft16.png"));
		//leftBtn.setClass("btn-small");
		leftBtn.setName(BUTTON_PREVIOUS);
		leftBtn.addEventListener(Events.ON_CLICK, this);
		row.appendChild(leftBtn);

		editor_Date = new WDateEditor(EDITOR_DATE, false, false, true, "");
		editor_Date.setValue(Timestamp.valueOf(p_LocalDateTime));
		editor_Date.addValueChangeListener(this);
		row.appendChild(editor_Date.getComponent());

		row.appendChild(GroupwareToDoUtil.createLabelDiv(null, " - ", true));

		editor_Days = new WNumberEditor(EDITOR_DAYS,true, false,true, DisplayType.Integer, "");
		editor_Days.setValue(p_Days);
		editor_Days.addValueChangeListener(this);
		ZKUpdateUtil.setWidth(editor_Days.getComponent(), "50px");
		row.appendChild(editor_Days.getComponent());
		row.appendChild(GroupwareToDoUtil.createLabelDiv(null, Msg.getMsg(ctx, "JP_Days"), true));

		rightBtn = new Button();
		rightBtn.setImage(ThemeManager.getThemeResource("images/MoveRight16.png"));
		//rightBtn.setClass("btn-small");
		rightBtn.addEventListener(Events.ON_CLICK, this);
		rightBtn.setName(BUTTON_NEXT);
		row.appendChild(rightBtn);

		row.appendChild(GroupwareToDoUtil.getDividingLine());

		row.appendChild(GroupwareToDoUtil.createSpaceDiv());

    	return outerDiv;

    }

    private int weekdays_Reference_ID = 0;

    private void queryDailyToDo_User()
    {
    	if(weekdays_Reference_ID == 0)
    	{
			MColumn column = MColumn.get(ctx, MGroupwareUser.Table_Name, MGroupwareUser.COLUMNNAME_JP_FirstDayOfWeek);
			weekdays_Reference_ID = column.getAD_Reference_Value_ID();
    	}

    	LocalDate localDate = null;
    	String dayOfWeek = null;
    	for(int i = 0; i < p_Days; i++)
    	{
    		localDate = p_LocalDateTime.toLocalDate().plusDays(i);
    		dayOfWeek = map_DayOfWeek.get(localDate);
    		if(dayOfWeek == null)
    		{
    			map_DayOfWeek.put(localDate, MRefList.getListName(ctx, weekdays_Reference_ID, String.valueOf(localDate.getDayOfWeek().getValue())));
    		}

    		if(map_AcquiredCalendarEvent_User.get(localDate) == null)
    			queryToDoCalendarEvents_User(localDate);
    	}
    }


    /**
     * Create Center contents of Borderlayout.
     *
     * @return Div
     */
    private Div renderDailyToDoList_User()
    {
    	//Get Color
		MGroupwareUser user = MGroupwareUser.get(ctx, p_AD_User_ID);
		String color1 = null;
		String color2 = null;
		if(user == null)
		{
			color1 = GroupwareToDoUtil.DEFAULT_COLOR1;
			color2 = GroupwareToDoUtil.DEFAULT_COLOR2;

		}else {

			if(!Util.isEmpty(user.getJP_ColorPicker()) && !Util.isEmpty(user.getJP_ColorPicker2()))
			{
				color1 = user.getJP_ColorPicker() ;
				color2 = user.getJP_ColorPicker2() ;

			}else if(!Util.isEmpty(user.getJP_ColorPicker()) && Util.isEmpty(user.getJP_ColorPicker2())){

				color1 = user.getJP_ColorPicker() ;
				color2 = GroupwareToDoUtil.DEFAULT_COLOR2;

			}else if(Util.isEmpty(user.getJP_ColorPicker()) && !Util.isEmpty(user.getJP_ColorPicker2())){

				color1 = GroupwareToDoUtil.DEFAULT_COLOR1;
				color2 = user.getJP_ColorPicker2() ;

			}else {

				color1 = GroupwareToDoUtil.DEFAULT_COLOR1;
				color2 = GroupwareToDoUtil.DEFAULT_COLOR2;

			}
		}
		color2 = "#dddddd";

       	Div div = new Div();
		Vlayout vlayout = new Vlayout();
		vlayout.setDroppable("false");
		div.appendChild(vlayout);

		//Unfinished Tasks
		Groupbox groupBox = new Groupbox();
		groupBox.setOpen(true);
		groupBox.setStyle("border: solid 1px "+ color1 +";");
		groupBox.setDraggable("false");
		groupBox.setMold("3d");
		groupBox.setWidgetListener("onOpen", "this.caption.setIconSclass('z-icon-caret-' + (event.open ? 'down' : 'right'));");
		vlayout.appendChild(groupBox);


		Caption caption= new Caption(MUser.get(ctx, p_AD_User_ID).getName());
		caption.setIconSclass("z-icon-caret-down");
		groupBox.appendChild(caption);

		Hlayout hlayout = new Hlayout();
		hlayout.setDroppable("false");
		groupBox.appendChild(hlayout);

		HashMap<Integer, ArrayList<ToDoCalendarEvent>>  map_OneDayUserEvent = null;
		LocalDate localDate = null;
		Grid grid = null;
		Vlayout day = null;
		Label day_label = null;
		Div day_header = null;
		Div day_Content = null;
    	for(int i =0 ; i < p_Days; i++)
    	{
    		localDate = p_LocalDateTime.toLocalDate().plusDays(i);
    		map_OneDayUserEvent =  map_AcquiredCalendarEvent_User.get(localDate);

    		grid = null;
    		if(map_OneDayUserEvent != null)
    		{
	    		ArrayList<ToDoCalendarEvent>  list_OneDayUserEvent =  map_OneDayUserEvent.get(p_AD_User_ID);
				grid = createGrid(list_OneDayUserEvent, localDate);
    		}

			day = new Vlayout();
			hlayout.appendChild(day);
			ZKUpdateUtil.setHflex(day, "1");
			day.setStyle("padding:2px 2px 2px 2px; margin-bottom:4px; border: solid 1px "+ color2 +";"); //#dddddd;

			day_label = new Label(formattedDate(localDate) + " ("+map_DayOfWeek.get(localDate)+")");
			day_label.setStyle("text-align: center; color:#ffffff ");

			day_header = new Div();
			day_header.appendChild(day_label);
			day_header.setStyle("padding:4px 2px 4px 4px; background-color:"+ color1 +";");
			day.appendChild(day_header);


			day_Content = new Div();
			day_Content.setClass("views-box");

			if(grid == null)
			{
				day_Content.appendChild(new Label(Msg.getMsg(ctx, "not.found")));
			}else {
				day_Content.appendChild(grid);
			}
			day.appendChild(day_Content);
    	}

       	return div;

    }


    private void queryDailyToDo_Team()
    {
    	LocalDate localDate = null;
    	for(int i = 0; i < p_Days; i++)
    	{
    		localDate = p_LocalDateTime.toLocalDate().plusDays(i);
    		if(map_AcquiredCalendarEvent_Team.get(localDate) == null)
    			queryToDoCalendarEvents_Team(localDate);
    	}
    }

    private Div renderDailyToDoList_Team()
    {
    	if(p_JP_Team_ID == 0)
    	{
    		return null;
    	}

    	Div div = new Div();
		Vlayout vlayout = new Vlayout();
		vlayout.setDroppable("false");
		div.appendChild(vlayout);

		Groupbox groupBox = null;
		Caption caption = null;
		Hlayout hlayout = null;

		MTeamMember[] member =  m_Team.getTeamMember();
		for(int i = 0; i < member.length; i++)
		{
			if(p_AD_User_ID == member[i].getAD_User_ID())
				continue;

	    	//Get Color
			MGroupwareUser user = MGroupwareUser.get(ctx, member[i].getAD_User_ID());
			String color1 = null;
			String color2 = null;
			if(user == null)
			{
				color1 = GroupwareToDoUtil.DEFAULT_COLOR1;
				color2 = GroupwareToDoUtil.DEFAULT_COLOR2;

			}else {

				if(!Util.isEmpty(user.getJP_ColorPicker()) && !Util.isEmpty(user.getJP_ColorPicker2()))
				{
					color1 = user.getJP_ColorPicker() ;
					color2 = user.getJP_ColorPicker2() ;

				}else if(!Util.isEmpty(user.getJP_ColorPicker()) && Util.isEmpty(user.getJP_ColorPicker2())){

					color1 = user.getJP_ColorPicker() ;
					color2 = GroupwareToDoUtil.DEFAULT_COLOR2;

				}else if(Util.isEmpty(user.getJP_ColorPicker()) && !Util.isEmpty(user.getJP_ColorPicker2())){

					color1 = GroupwareToDoUtil.DEFAULT_COLOR1;
					color2 = user.getJP_ColorPicker2() ;

				}else {

					color1 = GroupwareToDoUtil.DEFAULT_COLOR1;
					color2 = GroupwareToDoUtil.DEFAULT_COLOR2;

				}
			}
			color2 = "#dddddd";

			groupBox = new Groupbox();
			groupBox.setOpen(true);
			groupBox.setStyle("border: solid 2px "+ color1 +";");
			groupBox.setDraggable("false");
			groupBox.setMold("3d");
			groupBox.setWidgetListener("onOpen", "this.caption.setIconSclass('z-icon-caret-' + (event.open ? 'down' : 'right'));");
			vlayout.appendChild(groupBox);

			caption= new Caption(MUser.get(ctx, member[i].getAD_User_ID()).getName());
			caption.setIconSclass("z-icon-caret-down");
			groupBox.appendChild(caption);

			hlayout = new Hlayout();
			hlayout.setDroppable("false");
			groupBox.appendChild(hlayout);

			HashMap<Integer, ArrayList<ToDoCalendarEvent>>  map_OneDayTeamEvent =  null;
			LocalDate localDate = null;
			Grid grid = null;
			Vlayout day = null;
			Div day_header = null;
			Label day_label = null;
			Div day_Content = null;
			for(int j =0 ; j < p_Days; j++)
	    	{
				grid = null;
				localDate = p_LocalDateTime.toLocalDate().plusDays(j);
				map_OneDayTeamEvent =  map_AcquiredCalendarEvent_Team.get(localDate);
	    		if(map_OneDayTeamEvent != null)
	    		{
		    		ArrayList<ToDoCalendarEvent>  list_OneDayUserToDo =  map_OneDayTeamEvent.get(member[i].getAD_User_ID());
					grid = createGrid(list_OneDayUserToDo, localDate);
	    		}

				day = new Vlayout();
				hlayout.appendChild(day);
				ZKUpdateUtil.setHflex(day, "1");
				day.setStyle("padding:2px 2px 2px 2px; margin-bottom:4px; border: solid 2px "+ color2 +";");//#dddddd;

				day_label = new Label(formattedDate(localDate) + " ("+map_DayOfWeek.get(localDate)+")");
				day_label.setStyle("text-align: center; color:#ffffff ");

				day_header = new Div();
				day_header.setStyle("padding:4px 2px 4px 4px; background-color:"+ color1 +";");
				day_header.appendChild(day_label);
				day.appendChild(day_header);

				day_Content = new Div();
				day_Content.setClass("views-box");

				if(grid == null)
				{
					day_Content.appendChild(new Label(Msg.getMsg(ctx, "not.found")));
				}else {
					day_Content.appendChild(grid);
				}
				day.appendChild(day_Content);

	    	}


		}

    	return div;
    }


    private Grid createGrid(ArrayList<ToDoCalendarEvent>  map_ToDo, LocalDate localDate)
    {
    	if(map_ToDo == null)
    		return null;

		if(map_ToDo == null || map_ToDo.size() == 0)
			return null;

		Grid grid = GridFactory.newGridLayout();
		grid.setMold("paging");
		grid.setPageSize(10);
		grid.setPagingPosition("bottom");

		Rows gridRows = grid.newRows();
		Row row = null;
		ToolBarButton btn = null;
		int count = 0;
		int skipCount = 0;
		for (ToDoCalendarEvent toDoCalEvent : map_ToDo)
		{
			count++;
			if(isSkip(toDoCalEvent))
			{
				skipCount++;
				if(count == map_ToDo.size())//last
				{
					if(count == skipCount)
					{
						return null;
					}

				}
				continue;
			}

			row = gridRows.newRow();
			btn = new ToolBarButton(toDoCalEvent.getToDo().getName());
			btn.setSclass("link");
			createTitle(toDoCalEvent.getToDo(), btn, localDate);
			btn.addEventListener(Events.ON_CLICK, this);
			btn.addEventListener(Events.ON_MOUSE_OVER, this);
			btn.setAttribute("ToDo", toDoCalEvent);
			row.appendChild(btn);


		}

    	return grid;
    }


    private Timestamp today = Timestamp.valueOf(LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN));
    private String team = "["+ Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_ToDo_Team_ID) +"] ";

	private void createTitle(I_ToDo toDo, ToolBarButton btn,  LocalDate localDate)
	{

		if(MToDo.JP_TODO_TYPE_Task.equals(toDo.getJP_ToDo_Type()))
		{
			Timestamp scheduledEndDay = Timestamp.valueOf(LocalDateTime.of(toDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime().toLocalDate(), LocalTime.MIN));
			if(today.compareTo(scheduledEndDay) < 0)
			{
				btn.setImage(ThemeManager.getThemeResource("images/InfoIndicator16.png"));

			}else if(today.compareTo(scheduledEndDay) == 0){

				if(MToDo.JP_TODO_STATUS_Completed.equals(toDo.getJP_ToDo_Status()))
				{
					btn.setImage(ThemeManager.getThemeResource("images/InfoIndicator16.png"));
				}else {
					btn.setImage(ThemeManager.getThemeResource("images/mSetVariable.png"));
				}

			}else if(today.compareTo(scheduledEndDay) > 0) {

				if(MToDo.JP_TODO_STATUS_Completed.equals(toDo.getJP_ToDo_Status()))
				{
					btn.setImage(ThemeManager.getThemeResource("images/InfoIndicator16.png"));
				}else {
					btn.setImage(ThemeManager.getThemeResource("images/ErrorIndicator16.png"));
				}

			}

			LocalTime time = toDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime().toLocalTime();
			if(toDo.getParent_Team_ToDo_ID() == 0)
			{
				if(time.compareTo(LocalTime.MIN) == 0)
				{
					btn.setLabel(toDo.getName());
				}else {
					btn.setLabel(time.toString() + " " + toDo.getName());
				}

			}else {

				if(time.compareTo(LocalTime.MIN) == 0)
				{
					btn.setLabel(team + toDo.getName());

				}else {

					btn.setLabel(time.toString() + team +toDo.getName());
				}
			}

		}else if(MToDo.JP_TODO_TYPE_Schedule.equals(toDo.getJP_ToDo_Type())) {

			Timestamp scheduledStartTime = toDo.getJP_ToDo_ScheduledStartTime();
			LocalDate startDate = scheduledStartTime.toLocalDateTime().toLocalDate();
			LocalTime startTime = scheduledStartTime.toLocalDateTime().toLocalTime();

			Timestamp scheduledEndTime  = toDo.getJP_ToDo_ScheduledEndTime();
			LocalDate endDate = scheduledEndTime.toLocalDateTime().toLocalDate();
			LocalTime endTime = scheduledEndTime.toLocalDateTime().toLocalTime();

			boolean isTeamToDo = false;
			boolean isOneDaySchedule = false;
			boolean isAllDaySchedule = false;



			if(toDo.getParent_Team_ToDo_ID() > 0)
			{
				isTeamToDo = true;
			}

			if(startDate.compareTo(endDate) == 0 )
			{
				isOneDaySchedule = true;
			}


			if(startTime.compareTo(LocalTime.MIN) == 0 && endTime.compareTo(LocalTime.MIN) == 0)
			{
				isAllDaySchedule = true;

			}else if(startTime.compareTo(LocalTime.MIN) == 0 && endTime.compareTo(LocalTime.MIN) != 0){

				if(localDate.compareTo(endDate) == 0)
				{
					isAllDaySchedule = false;
				}else {
					isAllDaySchedule = true;
				}

			}else if(startTime.compareTo(LocalTime.MIN) != 0 && endTime.compareTo(LocalTime.MIN) == 0){

				if(localDate.compareTo(startDate) == 0)
				{
					isAllDaySchedule = false;
				}else {
					isAllDaySchedule = true;
				}

			}else if(startTime.compareTo(LocalTime.MIN) != 0 && endTime.compareTo(LocalTime.MIN) != 0){


				if(localDate.compareTo(startDate) == 0)
				{
					isAllDaySchedule = false;
				}else if(localDate.compareTo(endDate) == 0){
					isAllDaySchedule = false;
				}else {
					isAllDaySchedule = true;
				}

			}

			if(isOneDaySchedule)
			{
				btn.setImage(ThemeManager.getThemeResource("images/InfoSchedule16.png"));

				if(isAllDaySchedule)
				{
					btn.setLabel((isTeamToDo ? team : "") + toDo.getName());

				}else {

					btn.setLabel(startTime.toString() + " - " +endTime.toString() + " " + (isTeamToDo ? team : "") + toDo.getName());

				}

			}else {

				btn.setImage(ThemeManager.getThemeResource("images/Register16.png"));

				if(isAllDaySchedule)
				{
					btn.setLabel((isTeamToDo ? team : "") +toDo.getName());

				}else {

					if(localDate.compareTo(startDate) == 0)
					{
						btn.setLabel(startTime.toString() + " - 24:00 " + (isTeamToDo ? team : "") +toDo.getName());

					}else if(localDate.compareTo(endDate) == 0) {

						btn.setLabel(LocalTime.MIN.toString() + " - "  + endTime.toString() + " " + (isTeamToDo ? team : "") +toDo.getName());
					}

				}

			}

		}

	}



	/**
	 * Format Date
	 *
	 * @param dateTime
	 * @return
	 */
	private Language lang = Env.getLanguage(Env.getCtx());

	private String formattedDate(LocalDate date)
	{
		return lang.getDateFormat().format(Timestamp.valueOf(LocalDateTime.of(date, LocalTime.MIN) ));
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

			refreshToDoList(true);

		}else if(MToDo.COLUMNNAME_JP_ToDo_Category_ID.equals(name)){

			if(value == null)
			{
				p_JP_ToDo_Category_ID = 0;
			}else {
				p_JP_ToDo_Category_ID = Integer.parseInt(value.toString());
			}

			refreshToDoList(false);

		}else if(MTeam.COLUMNNAME_JP_Team_ID.equals(name)){

			if(value == null)
			{
				p_JP_Team_ID = 0;
				m_Team = null;

				if(center_TeamToDo != null)
					center_TeamToDo.detach();

			}else {

				p_JP_Team_ID = Integer.parseInt(value.toString());
				m_Team = new MTeam(ctx, p_JP_Team_ID, null);

				MTeamMember[] member = m_Team.getTeamMember();
				int JP_ToDo_Calendar_Max_Member = MSysConfig.getIntValue(JP_TODO_CALENDAR_MAX_MEMBER, 100, Env.getAD_Client_ID(ctx));

				if(member.length == 0 || (member.length == 1 && member[0].getAD_User_ID() == p_AD_User_ID))
				{
					p_JP_Team_ID = 0;
					m_Team = null;
					editor_JP_Team_ID.setValue(0);

					//There are no users on the team, or there are no users on the team except the selected user.
					FDialog.error(form.getWindowNo(), "Error", Msg.getMsg(ctx, "JP_Team_No_Users_Except_Selected_User"));

					return ;
				}


				if(member.length > JP_ToDo_Calendar_Max_Member)
				{
					p_JP_Team_ID = 0;
					m_Team = null;
					editor_JP_Team_ID.setValue(0);

					//The number of users belonging to the selected team has exceeded the maximum number of users that can be displayed on the calendar.
					FDialog.error(form.getWindowNo(), "Error", Msg.getMsg(ctx, "JP_ToDo_Calendar_Max_Member", new Object[] {member.length,JP_ToDo_Calendar_Max_Member}));

					return ;
				}


				refreshToDoList_Team(true);

			}


		}else if(MGroupwareUser.COLUMNNAME_IsDisplayScheduleJP.equals(name)) {

			p_IsDisplaySchedule = (boolean)value;
			editor_IsDisplaySchedule.setValue(value);
			refreshToDoList(false);

		}else if(MGroupwareUser.COLUMNNAME_IsDisplayTaskJP.equals(name)) {

			p_IsDisplayTask = (boolean)value;
			editor_IsDisplayTask.setValue(value);
			refreshToDoList(false);

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

			refreshToDoList(false);

		}else if(MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar.equals(name)) {

			if(value == null)
			{
				WTableDirEditor comp = (WTableDirEditor)evt.getSource();
				String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar);//
				throw new WrongValueException(comp.getComponent(), msg);
			}

			p_JP_ToDo_Calendar = value.toString();
			editor_JP_ToDo_Calendar.setValue(value);

			refreshToDoList(true);

		}else if(EDITOR_DATE.equals(name)) {

			if(value == null)
			{
				WDateEditor comp = (WDateEditor)evt.getSource();
				String msg = Msg.getMsg(Env.getCtx(), "FillMandatory");
				throw new WrongValueException(comp.getComponent(), msg);
			}

			if(value instanceof Timestamp)
			{

				Timestamp ts = (Timestamp)value;
				p_LocalDateTime = ts.toLocalDateTime();
				refreshToDoList(false);

			}


		}else if(EDITOR_DAYS.equals(name)) {

			if(value == null)
			{
				WNumberEditor comp = (WNumberEditor)evt.getSource();
				String msg = Msg.getMsg(Env.getCtx(), "FillMandatory");
				throw new WrongValueException(comp.getComponent(), msg);
			}

			if(value instanceof Integer)
			{

				p_Days = ((Integer) value).intValue();
				if(0 < p_Days && p_Days < 8)
				{
					;//Noting to Do

				}else {

					WNumberEditor comp = (WNumberEditor)evt.getSource();
					String msg = "1 ～ 7";
					throw new WrongValueException(comp.getComponent(), msg);
				}

				refreshToDoList(false);

			}

		}

	}


	private void refreshToDoList(boolean isAllRefresh)
	{
		refreshToDoList_User(isAllRefresh);
		refreshToDoList_Team(isAllRefresh);
	}

	private void refreshToDoList_User(boolean isAllRefresh)
	{
		if(isAllRefresh)
		{
	    	if(map_AcquiredCalendarEvent_User != null)
	    		map_AcquiredCalendarEvent_User.clear();
		}

		queryDailyToDo_User();

		if(center_UserToDo != null)
			center_UserToDo.detach();

		center_UserToDo = renderDailyToDoList_User();
		if(center_UserToDo != null)
		{
			if(p_JP_Team_ID == 0)
			{
				center.appendChild(center_UserToDo);

			}else {

				if(center_TeamToDo != null)
				{
					center.insertBefore(center_UserToDo, center_TeamToDo);
				}else {
					center.appendChild(center_UserToDo);
				}
			}
		}

	}

	private void refreshToDoList_Team(boolean isAllRefresh)
	{
		if(p_JP_Team_ID > 0)
		{
			if(isAllRefresh)
			{
		    	if(map_AcquiredCalendarEvent_Team != null)
		    		map_AcquiredCalendarEvent_Team.clear();
			}

			queryDailyToDo_Team();

			if(center_TeamToDo != null)
				center_TeamToDo.detach();

			center_TeamToDo = renderDailyToDoList_Team();
			if(center_TeamToDo != null)
				center.appendChild(center_TeamToDo);
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

		if(Events.ON_MOUSE_OVER.equals(eventName))
		{
			Object obj_ToDoCalendarEvent = comp.getAttribute("ToDo");
			ToDoCalendarEvent todoEvent = (ToDoCalendarEvent)obj_ToDoCalendarEvent;
			if(p_JP_Team_ID == 0)
			{
				popup_CalendarEvent.setToDoCalendarEvent(todoEvent.getToDo(), null);
			}else {

				todoEvent.setHeaderColor(todoEvent.team_Default_Long_HeaderColor);
				todoEvent.setContentColor(todoEvent.team_Default_Long_ContentColor);

				popup_CalendarEvent.setToDoCalendarEvent(todoEvent.getToDo(), todoEvent);
			}
			popup_CalendarEvent.setPage(form.getPage());
			popup_CalendarEvent.open(comp,"end_before");

		}else  if(eventName.equals(Events.ON_CLICK)) {

			if(comp instanceof Button)
			{
				Button btn = (Button) comp;
				String btnName = btn.getName();
				if(BUTTON_NEW.equals(btnName))
				{
					ToDoPopupWindow todoWindow = new ToDoPopupWindow(this, -1);
					todoWindow.addToDoCalenderEventReceiver(this);
					SessionManager.getAppDesktop().showWindow(todoWindow);

				}else if(BUTTON_REFRESH.equals(btnName)){

					refreshToDoList(true);

				}else if(BUTTON_PREVIOUS.equals(btnName)) {

					p_LocalDateTime = p_LocalDateTime.minusDays(1);
					editor_Date.setValue(Timestamp.valueOf(p_LocalDateTime));
					refreshToDoList(false);

				}else if(BUTTON_NEXT.equals(btnName)) {

					p_LocalDateTime = p_LocalDateTime.plusDays(1);
					editor_Date.setValue(Timestamp.valueOf(p_LocalDateTime));
					refreshToDoList(false);

				}

			}else if(comp instanceof ToolBarButton){

				Object obj_ToDoCalendarEvent = comp.getAttribute("ToDo");
				ToDoCalendarEvent todoEvent = (ToDoCalendarEvent)obj_ToDoCalendarEvent;

				list_ToDoes = new ArrayList<I_ToDo>();
				list_ToDoes.add(todoEvent.getToDo());

				ToDoPopupWindow todoWindow = new ToDoPopupWindow(this, 0);
				todoWindow.addToDoCalenderEventReceiver(this);

				SessionManager.getAppDesktop().showWindow(todoWindow);

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

			}


		}else if (CalendarsEvent.ON_EVENT_CREATE.equals(eventName)) {

			if (event instanceof CalendarsEvent)
			{
				list_ToDoes = null;

				ToDoPopupWindow todoWindow = new ToDoPopupWindow(this, -1);
				todoWindow.addToDoCalenderEventReceiver(this);

				SessionManager.getAppDesktop().showWindow(todoWindow);
			}

		}else if (CalendarsEvent.ON_EVENT_EDIT.equals(eventName)) {

			if (event instanceof CalendarsEvent)
			{
				CalendarsEvent calendarsEvent = (CalendarsEvent) event;
				CalendarEvent calendarEvent = calendarsEvent.getCalendarEvent();

				if (calendarEvent instanceof ToDoCalendarEvent)
				{
					ToDoCalendarEvent ce = (ToDoCalendarEvent) calendarEvent;

					list_ToDoes = new ArrayList<I_ToDo>();
					list_ToDoes.add(ce.getToDo());

					ToDoPopupWindow todoWindow = new ToDoPopupWindow(this, 0);
					todoWindow.addToDoCalenderEventReceiver(this);

					SessionManager.getAppDesktop().showWindow(todoWindow);

				}
			}

		}

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


	/**
	 * Get Main User's Calendar Event.
	 */
    private void queryToDoCalendarEvents_User(LocalDate localDate)
    {
		StringBuilder whereClause = null;
		StringBuilder orderClause = null;
		ArrayList<Object> list_parameters  = new ArrayList<Object>();
		Object[] parameters = null;


		LocalDateTime toDayMin = LocalDateTime.of(localDate, LocalTime.MIN);
		LocalDateTime toDayMax = LocalDateTime.of(localDate, LocalTime.MAX);


		/**
		 *  SQL of Get Schedule
		 **/
		//AD_Client_ID
		whereClause = new StringBuilder(" AD_Client_ID=? ");
		list_parameters.add(Env.getAD_Client_ID(ctx));

		//AD_User_ID
		whereClause = whereClause.append(" AND AD_User_ID = ? ");
		list_parameters.add(p_AD_User_ID);

		//JP_ToDo_ScheduledStartTime
		whereClause = whereClause.append(" AND JP_ToDo_ScheduledStartTime < ? AND JP_ToDo_ScheduledEndTime >= ? AND IsActive='Y' ");//1 - 2
		list_parameters.add(Timestamp.valueOf(toDayMax));
		list_parameters.add(Timestamp.valueOf(toDayMin));

		//JP_TODO_TYPE
		whereClause = whereClause.append(" AND JP_ToDo_Type IN ('S','T') ");
		//list_parameters.add(MToDo.JP_TODO_TYPE_Schedule);

		//Authorization Check
		if(p_login_User_ID == p_AD_User_ID)
		{
			//Noting to do;

		}else {

			whereClause = whereClause.append(" AND (IsOpenToDoJP='Y' OR CreatedBy = ?)");
			list_parameters.add(p_login_User_ID);
		}

		//Org Access
		String orgAccessSQL = m_Role.getOrgWhere(false);
		if(!Util.isEmpty(orgAccessSQL))
		{
			whereClause = whereClause.append(" AND " + orgAccessSQL);
		}

		/**
		 * Execution SQL
		 */
		parameters = list_parameters.toArray(new Object[list_parameters.size()]);
		orderClause = new StringBuilder("AD_User_ID, JP_ToDo_ScheduledStartTime, JP_ToDo_ScheduledEndTime DESC, JP_ToDo_Type");

		if(MGroupwareUser.JP_TODO_CALENDAR_PersonalToDo.equals(p_JP_ToDo_Calendar))//Search Personal ToDo
		{


			List<MToDo> list_ToDoes = new Query(Env.getCtx(), MToDo.Table_Name, whereClause.toString(), null)
												.setParameters(parameters)
												.setOrderBy(orderClause.toString())
												.list();

			HashMap<Integer, ArrayList<ToDoCalendarEvent>> map_OneDayUserEvent =  map_AcquiredCalendarEvent_User.get(localDate);
			if(map_OneDayUserEvent == null)
			{
				map_OneDayUserEvent = new HashMap<Integer, ArrayList<ToDoCalendarEvent>> ();
				map_AcquiredCalendarEvent_User.put(localDate, map_OneDayUserEvent);
			}

			map_OneDayUserEvent.clear();

			if(list_ToDoes == null || list_ToDoes.size() == 0)
			{
				return ;
			}

			ArrayList<ToDoCalendarEvent> list_OneDayUserEvent = null;
			ToDoCalendarEvent event = null;

			for(MToDo todo :list_ToDoes)
			{
				event = new ToDoCalendarEvent(todo);
				list_OneDayUserEvent = map_OneDayUserEvent.get(todo.getAD_User_ID());
				if(list_OneDayUserEvent == null)
				{
					list_OneDayUserEvent = new ArrayList<ToDoCalendarEvent>();
					list_OneDayUserEvent.add(event);
					map_OneDayUserEvent.put(todo.getAD_User_ID(), list_OneDayUserEvent);
				}else {
					list_OneDayUserEvent.add(event);
				}

			}//for

		}else {//Search Team ToDo


			List<MToDoTeam> list_ToDoes = new Query(Env.getCtx(), MToDoTeam.Table_Name, whereClause.toString(), null)
												.setParameters(parameters)
												.setOrderBy(orderClause.toString())
												.list();

			HashMap<Integer, ArrayList<ToDoCalendarEvent>> map_OneDayUserEvent =  map_AcquiredCalendarEvent_User.get(localDate);
			if(map_OneDayUserEvent == null)
			{
				map_OneDayUserEvent = new HashMap<Integer, ArrayList<ToDoCalendarEvent>> ();
				map_AcquiredCalendarEvent_User.put(localDate, map_OneDayUserEvent);
			}

			map_OneDayUserEvent.clear();

			if(list_ToDoes == null || list_ToDoes.size() == 0)
			{
				return ;
			}

			ArrayList<ToDoCalendarEvent> list_OneDayUserEvent = null;
			ToDoCalendarEvent event = null;

			for(MToDoTeam todo :list_ToDoes)
			{
				event = new ToDoCalendarEvent(todo);
				list_OneDayUserEvent = map_OneDayUserEvent.get(todo.getAD_User_ID());
				if(list_OneDayUserEvent == null)
				{
					list_OneDayUserEvent = new ArrayList<ToDoCalendarEvent>();
					list_OneDayUserEvent.add(event);
					map_OneDayUserEvent.put(todo.getAD_User_ID(), list_OneDayUserEvent);
				}else {
					list_OneDayUserEvent.add(event);
				}

			}//for
		}

		return ;
    }



	/**
	 * Get Team User's Calendar Event.
	 */
    private void queryToDoCalendarEvents_Team(LocalDate localDate)
    {
		StringBuilder whereClause = null;
		StringBuilder orderClause = null;
		ArrayList<Object> list_parameters  = new ArrayList<Object>();
		Object[] parameters = null;

		LocalDateTime toDayMin = LocalDateTime.of(localDate, LocalTime.MIN);
		LocalDateTime toDayMax = LocalDateTime.of(localDate, LocalTime.MAX);

		/**
		 *  SQL of Get Schedule
		 **/
		//AD_Client_ID
		whereClause = new StringBuilder(" AD_Client_ID=? ");
		list_parameters.add(Env.getAD_Client_ID(ctx));

		//AD_User_ID
		whereClause = whereClause.append(" AND AD_User_ID IN (").append(createInUserClause(list_parameters)).append(")");

		//JP_ToDo_ScheduledStartTime
		whereClause = whereClause.append(" AND JP_ToDo_ScheduledStartTime < ? AND JP_ToDo_ScheduledEndTime >= ? AND IsActive='Y' ");
		list_parameters.add(Timestamp.valueOf(toDayMax));
		list_parameters.add(Timestamp.valueOf(toDayMin));

		//JP_TODO_TYPE_Schedule
		whereClause = whereClause.append(" AND JP_ToDo_Type IN ('S','T') ");
		//list_parameters.add(MToDo.JP_TODO_TYPE_Schedule);

		//Authorization Check
		whereClause = whereClause.append(" AND (IsOpenToDoJP='Y' OR CreatedBy = ?)");
		list_parameters.add(p_login_User_ID);

		//Org Access
		String orgAccessSQL = m_Role.getOrgWhere(false);
		if(!Util.isEmpty(orgAccessSQL))
		{
			whereClause = whereClause.append(" AND " + orgAccessSQL);
		}

		/**
		 * Execution SQL
		 */
		parameters = list_parameters.toArray(new Object[list_parameters.size()]);
		orderClause = new StringBuilder("AD_User_ID, JP_ToDo_ScheduledStartTime, JP_ToDo_ScheduledEndTime DESC, JP_ToDo_Type");

		if(MGroupwareUser.JP_TODO_CALENDAR_PersonalToDo.equals(p_JP_ToDo_Calendar))//Search Personal ToDo
		{
			List<MToDo> list_ToDoes = new Query(Env.getCtx(), MToDo.Table_Name, whereClause.toString(), null)
											.setParameters(parameters)
											.setOrderBy(orderClause.toString())
											.list();


			HashMap<Integer, ArrayList<ToDoCalendarEvent>> map_OneDayTeamEvent = null;
			map_OneDayTeamEvent =  map_AcquiredCalendarEvent_Team.get(localDate);
			if(map_OneDayTeamEvent == null)
			{
				map_OneDayTeamEvent = new HashMap<Integer, ArrayList<ToDoCalendarEvent>> ();
				map_AcquiredCalendarEvent_Team.put(localDate, map_OneDayTeamEvent);
			}

			map_OneDayTeamEvent.clear();

			if(list_ToDoes == null || list_ToDoes.size() == 0)
			{
				return ;
			}

			ArrayList<ToDoCalendarEvent> list_OneDayUserEvent = null;
			ToDoCalendarEvent event = null;

			for(MToDo todo :list_ToDoes)
			{
				event = new ToDoCalendarEvent(todo);
				list_OneDayUserEvent = map_OneDayTeamEvent.get(todo.getAD_User_ID());
				if(list_OneDayUserEvent == null)
				{
					list_OneDayUserEvent = new ArrayList<ToDoCalendarEvent>();
					list_OneDayUserEvent.add(event);
					map_OneDayTeamEvent.put(todo.getAD_User_ID(), list_OneDayUserEvent);
				}else {
					list_OneDayUserEvent.add(event);
				}

			}

		}else { //Search Team ToDo

			List<MToDoTeam> list_ToDoes = new Query(Env.getCtx(), MToDoTeam.Table_Name, whereClause.toString(), null)
					.setParameters(parameters)
					.setOrderBy(orderClause.toString())
					.list();


			HashMap<Integer, ArrayList<ToDoCalendarEvent>> map_OneDayTeamEvent = null;
			map_OneDayTeamEvent =  map_AcquiredCalendarEvent_Team.get(localDate);
			if(map_OneDayTeamEvent == null)
			{
				map_OneDayTeamEvent = new HashMap<Integer, ArrayList<ToDoCalendarEvent>> ();
				map_AcquiredCalendarEvent_Team.put(localDate, map_OneDayTeamEvent);
			}

			map_OneDayTeamEvent.clear();

			if(list_ToDoes == null || list_ToDoes.size() == 0)
			{
				return ;
			}

			ArrayList<ToDoCalendarEvent> list_OneDayUserEvent = null;
			ToDoCalendarEvent event = null;

			for(MToDoTeam todo :list_ToDoes)
			{
				event = new ToDoCalendarEvent(todo);
				list_OneDayUserEvent = map_OneDayTeamEvent.get(todo.getAD_User_ID());
				if(list_OneDayUserEvent == null)
				{
					list_OneDayUserEvent = new ArrayList<ToDoCalendarEvent>();
					list_OneDayUserEvent.add(event);
					map_OneDayTeamEvent.put(todo.getAD_User_ID(), list_OneDayUserEvent);
				}else {
					list_OneDayUserEvent.add(event);
				}

			}

		}
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
	 * Get Personal ToDo List (Implement of I_ToDoPopupwindowCaller)
	 */
	@Override
	public List<I_ToDo> getToDoList()
	{
		return list_ToDoes;
	}


	/**
	 * Get Default AD_User_ID (Implement of I_CallerToDoPopupwindow)
	 */
	@Override
	public int getDefault_AD__User_ID()
	{
		return p_AD_User_ID;
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
		if(list_ToDoes == null || list_ToDoes.size() == 0)
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
	public boolean update(I_ToDo todo)
	{
		int AD_User_ID = todo.getAD_User_ID();

		if(p_AD_User_ID == AD_User_ID)
		{
			refreshToDoList_User(true);
		}else {

			MTeamMember[] member = m_Team.getTeamMember();
			boolean isMember = false;
			for(int i = 0; i < member.length; i++)
			{
				if(member[i].getAD_User_ID() == AD_User_ID)
				{
					isMember = true;
					break;
				}
			}

			if(isMember)
			{
				refreshToDoList_Team(true);
			}
		}

		return true;
	}

	@Override
	public boolean create(I_ToDo todo)
	{
		if(MToDo.JP_TODO_TYPE_Memo.equals(todo.getJP_ToDo_Type()))
			return true;

		int AD_User_ID = todo.getAD_User_ID();
		LocalDate start_LocalDate = todo.getJP_ToDo_ScheduledStartTime().toLocalDateTime().toLocalDate();
		LocalDate end_LocalDate = todo.getJP_ToDo_ScheduledEndTime().toLocalDateTime().toLocalDate();

		HashMap<Integer, ArrayList<ToDoCalendarEvent>>  oneday_UsersEventMap = null;
		boolean isRender = false;

		if(p_AD_User_ID == AD_User_ID)
		{

			while(start_LocalDate.compareTo(end_LocalDate) <= 0)
			{
				oneday_UsersEventMap = map_AcquiredCalendarEvent_User.get(start_LocalDate);
				if(oneday_UsersEventMap == null)
				{
					start_LocalDate = start_LocalDate.plusDays(1);
					continue;
				}

				queryToDoCalendarEvents_User(start_LocalDate);
				start_LocalDate = start_LocalDate.plusDays(1);
				isRender = true;
			}

			if(isRender)
			{
				if(center_UserToDo != null)
					center_UserToDo.detach();

				center_UserToDo = renderDailyToDoList_User();
				if(center_UserToDo != null)
				{
					if(p_JP_Team_ID == 0)
					{
						center.appendChild(center_UserToDo);

					}else {

						if(center_TeamToDo != null)
						{
							center.insertBefore(center_UserToDo, center_TeamToDo);
						}else {
							center.appendChild(center_UserToDo);
						}
					}
				}
			}


		}else {

			MTeamMember[] member = m_Team.getTeamMember();
			boolean isMember = false;
			for(int i = 0; i < member.length; i++)
			{
				if(member[i].getAD_User_ID() == AD_User_ID)
				{
					isMember = true;
					break;
				}
			}

			if(isMember)
			{
				while(start_LocalDate.compareTo(end_LocalDate) <= 0)
				{
					oneday_UsersEventMap = map_AcquiredCalendarEvent_Team.get(start_LocalDate);
					if(oneday_UsersEventMap == null)
					{
						start_LocalDate = start_LocalDate.plusDays(1);
						continue;
					}

					queryToDoCalendarEvents_Team(start_LocalDate);
					start_LocalDate = start_LocalDate.plusDays(1);
					isRender = true;
				}

				if(isRender)
				{
					if(center_TeamToDo != null)
						center_TeamToDo.detach();

					center_TeamToDo = renderDailyToDoList_Team();
					if(center_TeamToDo != null)
						center.appendChild(center_TeamToDo);
				}
			}
		}

		return true;
	}

	@Override
	public boolean delete(I_ToDo deleteToDo)
	{
		int AD_User_ID = deleteToDo.getAD_User_ID();
		LocalDate start_LocalDate = deleteToDo.getJP_ToDo_ScheduledStartTime().toLocalDateTime().toLocalDate();
		LocalDate end_LocalDate = deleteToDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime().toLocalDate();

		HashMap<Integer, ArrayList<ToDoCalendarEvent>>  oneday_UsersEventMap = null;
		ArrayList<ToDoCalendarEvent> oneday_UserEventList = null;

		while(start_LocalDate.compareTo(end_LocalDate) <= 0)
		{
			if(p_AD_User_ID == AD_User_ID)
			{
				oneday_UsersEventMap = map_AcquiredCalendarEvent_User.get(start_LocalDate);
			}else {
				oneday_UsersEventMap = map_AcquiredCalendarEvent_Team.get(start_LocalDate);
			}


			if(oneday_UsersEventMap == null)
			{
				start_LocalDate = start_LocalDate.plusDays(1);
				continue;
			}

			oneday_UserEventList = oneday_UsersEventMap.get(AD_User_ID);
			for(int j = 0; j < oneday_UserEventList.size(); j++ )
			{
				if(oneday_UserEventList.get(j).getToDo().get_ID() ==deleteToDo.get_ID())
				{
					oneday_UserEventList.remove(j);
					break;
				}

			}

			start_LocalDate = start_LocalDate.plusDays(1);
		}


		if(p_JP_Team_ID == 0)
		{
			if(center_UserToDo != null)
				center_UserToDo.detach();

			center_UserToDo = renderDailyToDoList_User();
			if(center_UserToDo != null)
				center.appendChild(center_UserToDo);

		}else {

			if(p_AD_User_ID == AD_User_ID)
			{
				if(center_UserToDo != null)
					center_UserToDo.detach();

				center_UserToDo = renderDailyToDoList_User();
				if(center_UserToDo != null && center_TeamToDo != null)
					center.insertBefore(center_UserToDo, center_TeamToDo);

			}else {

				if(center_TeamToDo != null)
					center_TeamToDo.detach();

				center_TeamToDo = renderDailyToDoList_Team();
				if(center_TeamToDo != null)
					center.appendChild(center_TeamToDo);
			}
		}

		return true;
	}


	@Override
	public boolean refresh(I_ToDo todo)
	{
		refreshToDoList(true);
		return true;
	}


	@Override
	public Timestamp getDefault_JP_ToDo_ScheduledStartTime()
	{
		return Timestamp.valueOf(LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN));
	}

	@Override
	public Timestamp getDefault_JP_ToDo_ScheduledEndTime()
	{
		return Timestamp.valueOf(LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN));
	}


	@Override
	public int getWindowNo()
	{
		return form.getWindowNo();
	}



	@Override
	public String getJP_ToDo_Calendar()
	{
		return p_JP_ToDo_Calendar;
	}


}
