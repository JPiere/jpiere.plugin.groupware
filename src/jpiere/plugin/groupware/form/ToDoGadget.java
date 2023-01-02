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
import java.util.List;
import java.util.Properties;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.ToolBarButton;
import org.adempiere.webui.dashboard.DashboardPanel;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WNumberEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.MColumn;
import org.compiere.model.MForm;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.Query;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;

import jpiere.plugin.groupware.model.I_ToDo;
import jpiere.plugin.groupware.model.MGroupwareUser;
import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoTeam;
import jpiere.plugin.groupware.util.GroupwareToDoUtil;
import jpiere.plugin.groupware.window.I_ToDoCalendarEventReceiver;
import jpiere.plugin.groupware.window.I_ToDoPopupwindowCaller;
import jpiere.plugin.groupware.window.ToDoPopupWindow;


/**
 *  JPiere Plugins(JPPS) Dashboard Gadget Create Info Gadget
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class ToDoGadget extends DashboardPanel implements I_ToDoCalendarGadget, I_ToDoPopupwindowCaller, I_ToDoCalendarEventReceiver, EventListener<Event>, ValueChangeListener {


	private Properties ctx = Env.getCtx();
	private String p_JP_ToDo_Calendar = MGroupwareUser.JP_TODO_CALENDAR_PersonalToDo;

	private int p_AD_User_ID = 0;
	private int login_User_ID = 0;

	private String p_JP_ToDo_Type = MToDo.JP_TODO_TYPE_Task;

	private LocalDateTime p_LocalDateTime =null;

	private Timestamp today = null;

	private Language lang = Env.getLanguage(Env.getCtx());

	private WDateEditor editor_Date = null;
	private final static String EDITOR_DATE = "DATE";

	private WNumberEditor editor_Days = null;
	private int p_Days = 5;
	private final static String EDITOR_DAYS = "DAYS";

	private List<I_ToDo>  list_ToDoes = null;

	private boolean isDashboardGadget = false;

	private Div headerArea = new Div();
	private Div messageArea = new Div();
	private Div contentsArea = new Div();
	private Div footerArea = new Div();

	private MGroupwareUser p_GroupwareUser = null;
	private boolean p_IsToDoMouseoverPopup = true;

	//Popup
	private CalendarEventPopup popup_CalendarEvent = new CalendarEventPopup();

	private final static String BUTTON_NAME_PREVIOUS_DAY = "PREVIOUSDAY";
	private final static String BUTTON_NAME_NEXT_DAY = "NEXTDAY";
	private final static String BUTTON_NAME_NEW_TODO = "NEW";
	private final static String BUTTON_NAME_REFRESH = "REFRESH";
	private final static String BUTTON_NAME_CALENDER = "CALENDER";


	/**
	 * Constructor for using as Dashboard Gadget
	 */
	public ToDoGadget()
	{
		super();
		p_GroupwareUser = MGroupwareUser.get(ctx, Env.getAD_User_ID(ctx));
		if(p_GroupwareUser != null)
		{
			if(!Util.isEmpty(p_GroupwareUser.getJP_ToDo_Type()))
				init(p_GroupwareUser.getJP_ToDo_Type(), p_GroupwareUser.getJP_ToDo_Calendar(),  true);

			p_IsToDoMouseoverPopup = p_GroupwareUser.isToDoMouseoverPopupJP();

		}else {

			init(MToDo.JP_TODO_TYPE_Task, MGroupwareUser.JP_TODO_CALENDAR_PersonalToDo, true);
		}
	}


	/**
	 * Constructor for not using as Dashboard Gadget
	 */
	public ToDoGadget(String JP_ToDo_Type, String JP_ToDo_Calendar)
	{
		super();
		p_GroupwareUser = MGroupwareUser.get(ctx, Env.getAD_User_ID(ctx));
		if(p_GroupwareUser != null)
		{
			p_IsToDoMouseoverPopup = p_GroupwareUser.isToDoMouseoverPopupJP();
		}

		init(JP_ToDo_Type, JP_ToDo_Calendar, false);
	}

	public void setIsToDoMouseoverPopup(boolean IsToDoMouseoverPopup)
	{
		p_IsToDoMouseoverPopup = IsToDoMouseoverPopup;
	}

	/**
	 * Initialization Personal ToDo Gadget
	 *
	 *
	 * @param JP_ToDo_Type
	 * @param isDashboardGadget
	 */
	public void init(String JP_ToDo_Type, String JP_ToDo_Calendar, Boolean isDashboardGadget)
	{
		this.isDashboardGadget = isDashboardGadget;
		this.p_JP_ToDo_Calendar = JP_ToDo_Calendar;

		setSclass("views-box");

		p_JP_ToDo_Type = JP_ToDo_Type;
		p_AD_User_ID = Env.getAD_User_ID(ctx);
		login_User_ID = p_AD_User_ID;

		p_LocalDateTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN);

		editor_Date = new WDateEditor(EDITOR_DATE, false, false, true, "");
		editor_Date.setValue(Timestamp.valueOf(p_LocalDateTime));
		
		editor_Days = new WNumberEditor(EDITOR_DAYS,true, false,true, DisplayType.Integer, "");
		editor_Days.setValue(p_Days);

		today = Timestamp.valueOf(LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN));

		createHeader();
		createMessage();
		createContents();
		
		//From iDempiere ver10. IDEMPIERE-5467 - Implement IsRange for Info Window fields 
		//We have to separate setting valueChengeListenr timing at WDateEditor adn WDateTimeEditor.
		editor_Date.addValueChangeListener(this);
		editor_Days.addValueChangeListener(this);

		this.appendChild(headerArea);
		this.appendChild(messageArea);
		this.appendChild(contentsArea);
		this.appendChild(footerArea);

	}


	/**
	 * Create Header for using as Dashboard Gadget
	 *
	 */
	private void createHeader()
	{
		if(!isDashboardGadget)
			return;

		Grid grid = GridFactory.newGridLayout();
		headerArea.appendChild(grid);
		Rows gridRows = grid.newRows();
		Row row = gridRows.newRow();


		/**User Search Field**/
		//JP_ToDo_Calendar
		MLookup  lookup_JP_ToDo_Calendar = MLookupFactory.get(ctx, 0,  0, MColumn.getColumn_ID(MGroupwareUser.Table_Name, MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar),  DisplayType.List);
		WTableDirEditor editor_JP_ToDo_Calendar= new WTableDirEditor(MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar, true, false, true, lookup_JP_ToDo_Calendar);
		editor_JP_ToDo_Calendar.setValue(p_JP_ToDo_Calendar);
		editor_JP_ToDo_Calendar.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_Calendar.getComponent(), "true");
		Label label_JP_ToDo_Calendar = new Label(Msg.getElement(ctx, MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar));

		row.appendCellChild(GroupwareToDoUtil.createLabelDiv(editor_JP_ToDo_Calendar, label_JP_ToDo_Calendar, false),1);
		row.appendCellChild(editor_JP_ToDo_Calendar.getComponent(),1);

		row = gridRows.newRow();

		MLookup lookupUser = MLookupFactory.get(ctx, 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_AD_User_ID),  DisplayType.Search);
		WSearchEditor userSearchEditor = new WSearchEditor("AD_User_ID", true, false, true, lookupUser);
		userSearchEditor.setValue(p_AD_User_ID);
		userSearchEditor.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(userSearchEditor.getComponent(), "true");

		Label label_User = new Label(Msg.translate(ctx, "AD_User_ID"));
		row.appendCellChild(GroupwareToDoUtil.createLabelDiv(userSearchEditor, label_User, false),1);
		row.appendCellChild(userSearchEditor.getComponent(),1);


		/**ToDo Type List Field**/
		MLookup lookupToDoType = MLookupFactory.get(ctx, 0,  0, MColumn.getColumn_ID(MToDo.Table_Name,  MToDo.COLUMNNAME_JP_ToDo_Type),  DisplayType.List);
		WTableDirEditor toDoListEditor = new WTableDirEditor(MToDo.COLUMNNAME_JP_ToDo_Type, true, false, true, lookupToDoType);
		toDoListEditor.setValue(p_JP_ToDo_Type);
		toDoListEditor.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(toDoListEditor.getComponent(), "true");

		Label label_ToDoType = new Label(Msg.translate(ctx, MToDo.COLUMNNAME_JP_ToDo_Type));
		row.appendCellChild(GroupwareToDoUtil.createLabelDiv(toDoListEditor ,label_ToDoType, false),1);
		row.appendCellChild(toDoListEditor.getComponent(),1);
	}


	/**
	 * Create Message
	 *
	 */
	private void createMessage()
	{
		if(messageArea.getFirstChild() != null)
			messageArea.getFirstChild().detach();

		if(!isDashboardGadget &&( MToDo.JP_TODO_TYPE_Task.equals(p_JP_ToDo_Type) || MToDo.JP_TODO_TYPE_Memo.equals(p_JP_ToDo_Type)) )
			return;

		Hlayout hlayout = new Hlayout();
		messageArea.appendChild(hlayout);

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		if(p_AD_User_ID == 0)
		{
			WStringEditor editor_Text = new WStringEditor();
			editor_Text.setReadWrite(false);
			editor_Text.setValue(Msg.getMsg(ctx,"enter") + ":" +Msg.getElement(ctx, "AD_User_ID"));
			hlayout.appendChild(editor_Text.getComponent());

		}else if(MToDo.JP_TODO_TYPE_Task.equals(p_JP_ToDo_Type)) {

			WStringEditor editor_Text = new WStringEditor();
			editor_Text.setReadWrite(false);
			editor_Text.setValue(Msg.getMsg(ctx, "JP_UnfinishedTasks"));//Unfinished Tasks
			hlayout.appendChild(editor_Text.getComponent());

		}else if(MToDo.JP_TODO_TYPE_Schedule.equals(p_JP_ToDo_Type)) {

			Button leftBtn = new Button();
			if (ThemeManager.isUseFontIconForImage())
				leftBtn.setIconSclass("z-icon-MoveLeft");
			else
				leftBtn.setImage(ThemeManager.getThemeResource("images/MoveLeft16.png"));
			leftBtn.setClass("btn-small");
			leftBtn.setName(BUTTON_NAME_PREVIOUS_DAY);
			leftBtn.addEventListener(Events.ON_CLICK, this);
			hlayout.appendChild(leftBtn);

			editor_Date.removeValuechangeListener(this);
			editor_Date.setValue(Timestamp.valueOf(p_LocalDateTime));
			editor_Date.addValueChangeListener(this);
			
			hlayout.appendChild(editor_Date.getComponent());

			hlayout.appendChild(GroupwareToDoUtil.createLabelDiv(null, " - ", true));

			ZKUpdateUtil.setWidth(editor_Days.getComponent(), "50px");
			hlayout.appendChild(editor_Days.getComponent());
			hlayout.appendChild(GroupwareToDoUtil.createLabelDiv(null, Msg.getMsg(ctx, "JP_Days"), true));

			Button rightBtn = new Button();
			if (ThemeManager.isUseFontIconForImage())
				rightBtn.setIconSclass("z-icon-MoveRight");
			else
				rightBtn.setImage(ThemeManager.getThemeResource("images/MoveRight16.png"));
			rightBtn.setClass("btn-small");
			rightBtn.addEventListener(Events.ON_CLICK, this);
			rightBtn.setName(BUTTON_NAME_NEXT_DAY);
			hlayout.appendChild(rightBtn);


		}else if(MToDo.JP_TODO_TYPE_Memo.equals(p_JP_ToDo_Type)) {

			WStringEditor editor_Text = new WStringEditor();
			editor_Text.setReadWrite(false);
			editor_Text.setValue(Msg.getMsg(ctx, "JP_UnfinishedMemo"));//Unfinished Memo
			hlayout.appendChild(editor_Text.getComponent());

		}else {

			WStringEditor editor_Text = new WStringEditor();
			editor_Text.setReadWrite(false);
			editor_Text.setValue(Msg.getMsg(ctx,"enter") + ":" +Msg.getElement(ctx, "JP_ToDo_Type"));
			hlayout.appendChild(editor_Text.getComponent());

		}

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		if(!isDashboardGadget)
			return ;

		Button createNewToDo = new Button();
		if (ThemeManager.isUseFontIconForImage())
			createNewToDo.setIconSclass("z-icon-New");
		else
			createNewToDo.setImage(ThemeManager.getThemeResource("images/New16.png"));
		createNewToDo.setClass("btn-small");
		createNewToDo.setName(BUTTON_NAME_NEW_TODO);
		createNewToDo.addEventListener(Events.ON_CLICK, this);
		createNewToDo.setId(String.valueOf(0));
		hlayout.appendChild(createNewToDo);

		Button refresh = new Button();
		if (ThemeManager.isUseFontIconForImage())
			refresh.setIconSclass("z-icon-Refresh");
		else
			refresh.setImage(ThemeManager.getThemeResource("images/Refresh16.png"));
		refresh.setClass("btn-small");
		refresh.setName(BUTTON_NAME_REFRESH);
		refresh.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(refresh);

		Button calander = new Button();
		if (ThemeManager.isUseFontIconForImage())
			calander.setIconSclass("z-icon-Calendar");
		else
			calander.setImage(ThemeManager.getThemeResource("images/Calendar16.png"));
		calander.setClass("btn-small");
		calander.setName(BUTTON_NAME_CALENDER);
		calander.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(calander);

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

	}


	/**
	 * Create Contents as ToDo Area
	 *
	 */
	public void createContents()
	{

		if(contentsArea.getFirstChild() != null)
			contentsArea.getFirstChild().detach();

		StringBuilder whereClause = null;
		StringBuilder orderClause = null;
		ArrayList<Object> list_parameters  = new ArrayList<Object>();
		Object[] parameters = null;


		if(MToDo.JP_TODO_TYPE_Task.equals(p_JP_ToDo_Type) || MToDo.JP_TODO_TYPE_Memo.equals(p_JP_ToDo_Type))
		{
			whereClause = new StringBuilder(" AD_Client_ID =? AND AD_User_ID = ? AND JP_ToDo_Type = ? AND IsActive='Y' AND JP_ToDo_Status <> ?");
			orderClause = new StringBuilder("JP_ToDo_ScheduledEndTime");
			list_parameters.add(Env.getAD_Client_ID(ctx));
			list_parameters.add(p_AD_User_ID);
			list_parameters.add(p_JP_ToDo_Type);
			list_parameters.add(MToDo.JP_TODO_STATUS_Completed);

		}else if(MToDo.JP_TODO_TYPE_Schedule.equals(p_JP_ToDo_Type)) {

			whereClause = new StringBuilder(" AD_Client_ID =? AND AD_User_ID = ? AND JP_ToDo_Type = ? AND IsActive='Y' AND JP_ToDo_ScheduledStartTime < ? AND JP_ToDo_ScheduledEndTime >= ?");
			orderClause = new StringBuilder("JP_ToDo_ScheduledStartTime");

			LocalDateTime toDayMin = LocalDateTime.of(p_LocalDateTime.toLocalDate(), LocalTime.MIN);
			LocalDateTime toDayMax = LocalDateTime.of(p_LocalDateTime.toLocalDate().plusDays(p_Days), LocalTime.MAX);

			list_parameters.add(Env.getAD_Client_ID(ctx));
			list_parameters.add(p_AD_User_ID);
			list_parameters.add(p_JP_ToDo_Type);
			list_parameters.add(Timestamp.valueOf(toDayMax));
			list_parameters.add(Timestamp.valueOf(toDayMin));

		}else {

			whereClause = new StringBuilder(" AD_Client_ID =? AND AD_User_ID = ? ");
			orderClause = new StringBuilder("JP_ToDo_ScheduledEndTime");
			list_parameters.add(Env.getAD_Client_ID(ctx));
			list_parameters.add(p_AD_User_ID);

		}


		if(p_Delete_ToDo_ID != 0)
		{
			if(MGroupwareUser.JP_TODO_CALENDAR_PersonalToDo.equals(p_JP_ToDo_Calendar))
			{
				whereClause = whereClause.append(" AND JP_ToDo_ID <> ?");
			}else {
				whereClause = whereClause.append(" AND JP_ToDo_Team_ID <> ?");
			}

			list_parameters.add(p_Delete_ToDo_ID);
		}

		if(login_User_ID != p_AD_User_ID)
		{
			whereClause = whereClause.append(" AND (IsOpenToDoJP='Y' OR CreatedBy = ?)");
			list_parameters.add(login_User_ID);
		}

		parameters = list_parameters.toArray(new Object[list_parameters.size()]);

		list_ToDoes = getToDoes(whereClause.toString(), orderClause.toString(), parameters);
		p_Delete_ToDo_ID = 0;

		if(list_ToDoes.size() <= 0)
		{
			contentsArea.appendChild(new Label(Msg.getMsg(ctx, "not.found")));
			return ;
		}

		Grid grid = GridFactory.newGridLayout();
		grid.setMold("paging");
		if(isDashboardGadget)
			grid.setPageSize(20); //default=20
		else
			grid.setPageSize(10);
		grid.setPagingPosition("top");
		contentsArea.appendChild(grid);

		Rows gridRows = grid.newRows();
		int counter = 0;
		for (I_ToDo toDo : list_ToDoes)
		{
			Row row = gridRows.newRow();
			ToolBarButton btn = new ToolBarButton(toDo.getName());
			btn.setSclass("link");
			createTitle(toDo, btn);
			btn.addEventListener(Events.ON_CLICK, this);
			btn.addEventListener(Events.ON_MOUSE_OVER, this);
			//btn.setId(String.valueOf(toDo.get_ID()));
			btn.setAttribute("index", counter);
			counter++;
			row.appendChild(btn);
		}//for
	}



	private String team = "["+ Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_ToDo_Team_ID) +"] ";

	/**
	 *
	 * Create ToDo Title
	 *
	 * @param toDo
	 * @param btn
	 */
	private void createTitle(I_ToDo toDo, ToolBarButton btn)
	{
		if(MToDo.JP_TODO_TYPE_Task.equals(p_JP_ToDo_Type))
		{
			Timestamp scheduledEndDay = Timestamp.valueOf(LocalDateTime.of(toDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime().toLocalDate(), LocalTime.MIN));
			if(today.compareTo(scheduledEndDay) < 0)
			{
				if (ThemeManager.isUseFontIconForImage())
					btn.setIconSclass("z-icon-Info-Circle");
				else
					btn.setImage(ThemeManager.getThemeResource("images/InfoIndicator16.png"));

			}else if(today.compareTo(scheduledEndDay) == 0){

				if (ThemeManager.isUseFontIconForImage())
					btn.setIconSclass("z-icon-Exclamation-Triangle");
				else
					btn.setImage(ThemeManager.getThemeResource("images/mSetVariable.png"));

			}else if(today.compareTo(scheduledEndDay) > 0) {

				if (ThemeManager.isUseFontIconForImage())
					btn.setIconSclass("z-icon-Minus-Circle");
				else
					btn.setImage(ThemeManager.getThemeResource("images/ErrorIndicator16.png"));

			}

			if(toDo.getParent_Team_ToDo_ID() == 0)
			{
				btn.setLabel(formattedDate(toDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime()) + " " + toDo.getName());
			}else {
				btn.setLabel(formattedDate(toDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime())
						+" ["+ Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Team_ID) +"] "+toDo.getName());
			}

		}else if(MToDo.JP_TODO_TYPE_Schedule.equals(p_JP_ToDo_Type)) {

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
			}else {
				isAllDaySchedule = false;
			}

			if(isOneDaySchedule)
			{
				if (ThemeManager.isUseFontIconForImage())
					btn.setIconSclass("z-icon-Clock");
				else
					btn.setImage(ThemeManager.getThemeResource("images/InfoSchedule16.png"));

				if(isAllDaySchedule)
				{
					btn.setLabel(formattedDate(toDo.getJP_ToDo_ScheduledStartTime().toLocalDateTime()) +" "+ (isTeamToDo ? team : "") + toDo.getName());

				}else {

					btn.setLabel(formattedDate(toDo.getJP_ToDo_ScheduledStartTime().toLocalDateTime()) + " " + startTime.toString() + " - " +endTime.toString() + " " + (isTeamToDo ? team : "") + toDo.getName());

				}

			}else {

				if (ThemeManager.isUseFontIconForImage())
					btn.setIconSclass("z-icon-Calendar-O");
				else
					btn.setImage(ThemeManager.getThemeResource("images/Register16.png"));

				if(isAllDaySchedule)
				{
					btn.setLabel(formattedDate(toDo.getJP_ToDo_ScheduledStartTime().toLocalDateTime()) +" - "
									+formattedDate(toDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime()) +" " + (isTeamToDo ? team : "") +toDo.getName());

				}else {

					if(startTime.compareTo(LocalTime.MIN) == 0)
					{
						btn.setLabel(formattedDate(toDo.getJP_ToDo_ScheduledStartTime().toLocalDateTime()) + " - " +
								formattedDate(toDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime()) +" " + endTime.toString()  + " " + (isTeamToDo ? team : "") +toDo.getName());

					}else if(endTime.compareTo(LocalTime.MIN) == 0) {

						btn.setLabel(formattedDate(toDo.getJP_ToDo_ScheduledStartTime().toLocalDateTime()) +" " + startTime.toString() + " - " +
								formattedDate(toDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime()) + " "  + (isTeamToDo ? team : "") +toDo.getName());

					}else {

						btn.setLabel(formattedDate(toDo.getJP_ToDo_ScheduledStartTime().toLocalDateTime()) + " "  + startTime.toString() + " - " +
								formattedDate(toDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime()) + " " + endTime.toString() + " " +  (isTeamToDo ? team : "") +toDo.getName());

					}

				}

			}

		}else if(MToDo.JP_TODO_TYPE_Memo.equals(p_JP_ToDo_Type)) {

			if (ThemeManager.isUseFontIconForImage())
				btn.setIconSclass("z-icon-Edit");
			else
				btn.setImage(ThemeManager.getThemeResource("images/Editor16.png"));

			if(toDo.getParent_Team_ToDo_ID() == 0)
			{
				btn.setLabel(toDo.getName());
			}else {
				btn.setLabel(" ["+ Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Team_ID) +"] "+toDo.getName());
			}


		}
	}


	/**
	 * Format Date
	 *
	 * @param dateTime
	 * @return
	 */
	private String formattedDate(LocalDateTime dateTime)
	{
		return lang.getDateFormat().format(Timestamp.valueOf(dateTime));
	}



	@Override
	public void valueChange(ValueChangeEvent evt)
	{
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();

		if(MToDo.COLUMNNAME_AD_User_ID.equals(name))
		{
			if(value == null)
				p_AD_User_ID = 0;
			else
				p_AD_User_ID = Integer.parseInt(value.toString());

		}else if(MToDo.COLUMNNAME_JP_ToDo_Type.equals(name)) {

			if(MToDo.JP_TODO_TYPE_Task.equals(value))
				p_JP_ToDo_Type = MToDo.JP_TODO_TYPE_Task;
			else if(MToDo.JP_TODO_TYPE_Schedule.equals(value))
				p_JP_ToDo_Type = MToDo.JP_TODO_TYPE_Schedule;
			else if(MToDo.JP_TODO_TYPE_Memo.equals(value))
				p_JP_ToDo_Type = MToDo.JP_TODO_TYPE_Memo;
			else
				p_JP_ToDo_Type = null;

		}else if(EDITOR_DATE.equals(name)) {

			if(value == null)
			{
				p_LocalDateTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN);
				editor_Date.setValue(Timestamp.valueOf(p_LocalDateTime));

			}else {

				p_LocalDateTime = ((Timestamp)value).toLocalDateTime();

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
				if(0 < p_Days && p_Days < 32)
				{
					;//Noting to Do

				}else {

					WNumberEditor comp = (WNumberEditor)evt.getSource();
					String msg = "1 ～ 31";
					throw new WrongValueException(comp.getComponent(), msg);
				}

			}

		}else if(MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar.equals(name)) {

			if(value == null)
			{
				WTableDirEditor comp = (WTableDirEditor)evt.getSource();
				String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MGroupwareUser.COLUMNNAME_JP_ToDo_Calendar);//
				throw new WrongValueException(comp.getComponent(), msg);
			}

			p_JP_ToDo_Calendar = value.toString();
		}


		createMessage();
		createContents();
	}



	@Override
	public void onEvent(Event event) throws Exception
	{
		Component comp = event.getTarget();
		String eventName = event.getName();
		if(Events.ON_CLICK.equals(eventName))
		{
			if(comp instanceof Button)
			{
				Button btn = (Button) comp;
				String btnName = btn.getName();
				if(BUTTON_NAME_PREVIOUS_DAY.equals(btnName))
				{
					p_LocalDateTime = p_LocalDateTime.minusDays(1);
					createMessage();
					createContents();

				}else if(BUTTON_NAME_NEXT_DAY.equals(btnName)){

					p_LocalDateTime = p_LocalDateTime.plusDays(1);
					createMessage();
					createContents();

				}else if(BUTTON_NAME_NEW_TODO.equals(btnName)){

					ToDoPopupWindow todoWindow = new ToDoPopupWindow(this, -1);
					todoWindow.addToDoCalenderEventReceiver(this);
					if(i_ToDoPopupwindowCaller instanceof ToDoCalendar)
					{
						ToDoCalendar todocalendar = (ToDoCalendar)i_ToDoPopupwindowCaller;
						todoWindow.addToDoCalenderEventReceiver(todocalendar);
					}

					SessionManager.getAppDesktop().showWindow(todoWindow);

				}else if(BUTTON_NAME_REFRESH.equals(btnName)){

					createContents();

				}else if(BUTTON_NAME_CALENDER.equals(btnName)){

					MForm form = GroupwareToDoUtil.getToDoCallendarForm();
					SessionManager.getAppDesktop().openForm(form.getAD_Form_ID());

				}

			}else if(comp instanceof ToolBarButton) {

				Object list_index = comp.getAttribute("index");
				int index = Integer.valueOf(list_index.toString()).intValue();
				ToDoPopupWindow todoWindow = new ToDoPopupWindow(this, index);
				todoWindow.addToDoCalenderEventReceiver(this);
				if(i_ToDoPopupwindowCaller instanceof ToDoCalendar)
				{
					ToDoCalendar todocalendar = (ToDoCalendar)i_ToDoPopupwindowCaller;
					todoWindow.addToDoCalenderEventReceiver(todocalendar);
				}

				SessionManager.getAppDesktop().showWindow(todoWindow);

			}

		}else if(Events.ON_MOUSE_OVER.equals(eventName)) {

			if(p_IsToDoMouseoverPopup)
			{
				Object list_index = comp.getAttribute("index");
				int index = Integer.valueOf(list_index.toString()).intValue();

				popup_CalendarEvent.setToDoCalendarEvent(list_ToDoes.get(index), null);
				popup_CalendarEvent.setPage(this.getPage());
				popup_CalendarEvent.open(comp,"end_before");
			}

		}

	}

	/**
	 * Execute SQL
	 *
	 * @param whereClause
	 * @param orderClause
	 * @param parameters
	 * @return
	 */
	private List<I_ToDo> getToDoes(String whereClause, String orderClause, Object ...parameters)
	{

		if(MGroupwareUser.JP_TODO_CALENDAR_PersonalToDo.equals(p_JP_ToDo_Calendar))
		{
			List<MToDo> m_list = new Query(ctx, MToDo.Table_Name, whereClause.toString(), null)
											.setParameters(parameters)
											.setOrderBy(orderClause)
											.list();

			List<I_ToDo> i_list = new ArrayList<I_ToDo>();
			for(MToDo todo : m_list)
			{
				i_list.add(todo);
			}

			return i_list;

		}else {

			List<MToDoTeam> m_list = new Query(ctx, MToDoTeam.Table_Name, whereClause.toString(), null)
					.setParameters(parameters)
					.setOrderBy(orderClause)
					.list();

			List<I_ToDo> i_list = new ArrayList<I_ToDo>();
			for(MToDoTeam todo : m_list)
			{
				i_list.add(todo);
			}

			return i_list;

		}
	}



	@Override
	public int getDefault_AD__User_ID()
	{
		return p_AD_User_ID;
	}



	@Override
	public String getDefault_JP_ToDo_Type()
	{
		return p_JP_ToDo_Type;
	}



	@Override
	public List<I_ToDo>  getToDoList()
	{
		return list_ToDoes;
	}



	I_ToDoPopupwindowCaller i_ToDoPopupwindowCaller;

	public void setToDoPopupwindowCaller(I_ToDoPopupwindowCaller todoPopupwindowcaller)
	{
		this.i_ToDoPopupwindowCaller = todoPopupwindowcaller;
	}




	private List<I_ToDoCalendarEventReceiver>  list_ToDoCalendarEventReceiver = new ArrayList<I_ToDoCalendarEventReceiver>();
	public void addToDoCalenderEventReceiver(I_ToDoCalendarEventReceiver calendar)
	{
		list_ToDoCalendarEventReceiver.add(calendar);
	}



	@Override
	public boolean update(I_ToDo todo)
	{
		if(p_AD_User_ID == todo.getAD_User_ID())
		{
			if(MToDo.JP_TODO_TYPE_Schedule.equals(todo.getJP_ToDo_Type())
					&& MToDo.JP_TODO_TYPE_Schedule.equals(p_JP_ToDo_Type))
			{
				LocalDate start = p_LocalDateTime.toLocalDate();
				LocalDate end = start.plusDays(p_Days);
				if(start.compareTo(todo.getJP_ToDo_ScheduledEndTime().toLocalDateTime().toLocalDate()) <= 0
						& end.compareTo(todo.getJP_ToDo_ScheduledStartTime().toLocalDateTime().toLocalDate()) >= 0)
				{
					if(todo instanceof MToDo)
					{
						if(MGroupwareUser.JP_TODO_CALENDAR_PersonalToDo.equals(p_JP_ToDo_Calendar))
						{
							createContents();
						}

					}else {

						if(MGroupwareUser.JP_TODO_CALENDAR_TeamToDo.equals(p_JP_ToDo_Calendar))
						{
							createContents();
						}
					}
				}

			}else if(MToDo.JP_TODO_TYPE_Task.equals(todo.getJP_ToDo_Type())
					&& MToDo.JP_TODO_TYPE_Task.equals(p_JP_ToDo_Type)) {

				if(!MToDo.JP_TODO_STATUS_Completed.equals(todo.getJP_ToDo_Status()))
				{
					if(todo instanceof MToDo)
					{
						if(MGroupwareUser.JP_TODO_CALENDAR_PersonalToDo.equals(p_JP_ToDo_Calendar))
						{
							createContents();
						}

					}else {

						if(MGroupwareUser.JP_TODO_CALENDAR_TeamToDo.equals(p_JP_ToDo_Calendar))
						{
							createContents();
						}
					}
				}

			}else if(MToDo.JP_TODO_TYPE_Memo.equals(todo.getJP_ToDo_Type())
					&& MToDo.JP_TODO_TYPE_Memo.equals(p_JP_ToDo_Type)) {

				if(!MToDo.JP_TODO_STATUS_Completed.equals(todo.getJP_ToDo_Status()))
				{
					if(todo instanceof MToDo)
					{
						if(MGroupwareUser.JP_TODO_CALENDAR_PersonalToDo.equals(p_JP_ToDo_Calendar))
						{
							createContents();
						}

					}else {

						if(MGroupwareUser.JP_TODO_CALENDAR_TeamToDo.equals(p_JP_ToDo_Calendar))
						{
							createContents();
						}
					}
				}
			}
		}

		return true;
	}



	@Override
	public boolean create(I_ToDo todo)
	{
		if(p_AD_User_ID == todo.getAD_User_ID())
		{
			if(MToDo.JP_TODO_TYPE_Schedule.equals(todo.getJP_ToDo_Type())
					&& MToDo.JP_TODO_TYPE_Schedule.equals(p_JP_ToDo_Type))
			{
				LocalDate start = p_LocalDateTime.toLocalDate();
				LocalDate end = start.plusDays(p_Days);
				if(start.compareTo(todo.getJP_ToDo_ScheduledEndTime().toLocalDateTime().toLocalDate()) <= 0
						& end.compareTo(todo.getJP_ToDo_ScheduledStartTime().toLocalDateTime().toLocalDate()) >= 0)
				{
					if(todo instanceof MToDo)
					{
						if(MGroupwareUser.JP_TODO_CALENDAR_PersonalToDo.equals(p_JP_ToDo_Calendar))
						{
							createContents();
						}

					}else {

						if(MGroupwareUser.JP_TODO_CALENDAR_TeamToDo.equals(p_JP_ToDo_Calendar))
						{
							createContents();
						}
					}
				}

			}else if(MToDo.JP_TODO_TYPE_Task.equals(todo.getJP_ToDo_Type())
					&& MToDo.JP_TODO_TYPE_Task.equals(p_JP_ToDo_Type)) {

				if(!MToDo.JP_TODO_STATUS_Completed.equals(todo.getJP_ToDo_Status()))
				{
					if(todo instanceof MToDo)
					{
						if(MGroupwareUser.JP_TODO_CALENDAR_PersonalToDo.equals(p_JP_ToDo_Calendar))
						{
							createContents();
						}

					}else {

						if(MGroupwareUser.JP_TODO_CALENDAR_TeamToDo.equals(p_JP_ToDo_Calendar))
						{
							createContents();
						}
					}
				}

			}else if(MToDo.JP_TODO_TYPE_Memo.equals(todo.getJP_ToDo_Type())
					&& MToDo.JP_TODO_TYPE_Memo.equals(p_JP_ToDo_Type)) {

				if(!MToDo.JP_TODO_STATUS_Completed.equals(todo.getJP_ToDo_Status()))
				{
					if(todo instanceof MToDo)
					{
						if(MGroupwareUser.JP_TODO_CALENDAR_PersonalToDo.equals(p_JP_ToDo_Calendar))
						{
							createContents();
						}

					}else {

						if(MGroupwareUser.JP_TODO_CALENDAR_TeamToDo.equals(p_JP_ToDo_Calendar))
						{
							createContents();
						}
					}
				}
			}
		}

		return true;
	}



	int p_Delete_ToDo_ID = 0;

	@Override
	public boolean delete(I_ToDo todo)
	{
		int todo_AD_User_ID = todo.getAD_User_ID();

		if(p_AD_User_ID == todo_AD_User_ID)
		{
			if(MToDo.JP_TODO_TYPE_Schedule.equals(todo.getJP_ToDo_Type())
					&& MToDo.JP_TODO_TYPE_Schedule.equals(p_JP_ToDo_Type))
			{
				LocalDate start = p_LocalDateTime.toLocalDate();
				LocalDate end = start.plusDays(p_Days);
				if(start.compareTo(todo.getJP_ToDo_ScheduledEndTime().toLocalDateTime().toLocalDate()) <= 0
						& end.compareTo(todo.getJP_ToDo_ScheduledStartTime().toLocalDateTime().toLocalDate()) >= 0)
				{
					if(todo instanceof MToDo)
					{
						if(MGroupwareUser.JP_TODO_CALENDAR_PersonalToDo.equals(p_JP_ToDo_Calendar))
						{
							p_Delete_ToDo_ID = todo.get_ID();
							createContents();
						}

					}else {

						if(MGroupwareUser.JP_TODO_CALENDAR_TeamToDo.equals(p_JP_ToDo_Calendar))
						{
							p_Delete_ToDo_ID = todo.get_ID();
							createContents();
						}
					}

				}

			}else if(MToDo.JP_TODO_TYPE_Task.equals(todo.getJP_ToDo_Type())
					&& MToDo.JP_TODO_TYPE_Task.equals(p_JP_ToDo_Type)) {

				if(!MToDo.JP_TODO_STATUS_Completed.equals(todo.getJP_ToDo_Status()))
				{
					if(todo instanceof MToDo)
					{
						if(MGroupwareUser.JP_TODO_CALENDAR_PersonalToDo.equals(p_JP_ToDo_Calendar))
						{
							p_Delete_ToDo_ID = todo.get_ID();
							createContents();
						}

					}else {

						if(MGroupwareUser.JP_TODO_CALENDAR_TeamToDo.equals(p_JP_ToDo_Calendar))
						{
							p_Delete_ToDo_ID = todo.get_ID();
							createContents();
						}
					}
				}

			}else if(MToDo.JP_TODO_TYPE_Memo.equals(todo.getJP_ToDo_Type())
					&& MToDo.JP_TODO_TYPE_Memo.equals(p_JP_ToDo_Type)) {

				if(!MToDo.JP_TODO_STATUS_Completed.equals(todo.getJP_ToDo_Status()))
				{
					if(todo instanceof MToDo)
					{
						if(MGroupwareUser.JP_TODO_CALENDAR_PersonalToDo.equals(p_JP_ToDo_Calendar))
						{
							p_Delete_ToDo_ID = todo.get_ID();
							createContents();
						}

					}else {

						if(MGroupwareUser.JP_TODO_CALENDAR_TeamToDo.equals(p_JP_ToDo_Calendar))
						{
							p_Delete_ToDo_ID = todo.get_ID();
							createContents();
						}
					}
				}
			}
		}

		return true;
	}


	@Override
	public boolean refresh(I_ToDo todo)//TODO
	{
		if(todo == null)
		{
			createContents();
			return true;

		}else {

			return update(todo);

		}
	}



	@Override
	public Timestamp getDefault_JP_ToDo_ScheduledStartTime()
	{
		return Timestamp.valueOf(LocalDateTime.of(p_LocalDateTime.toLocalDate(), LocalTime.NOON));
	}



	@Override
	public Timestamp getDefault_JP_ToDo_ScheduledEndTime()
	{
		return Timestamp.valueOf(LocalDateTime.of(p_LocalDateTime.toLocalDate(), LocalTime.NOON));
	}



	@Override
	public void setAD_User_ID(int AD_User_ID)
	{
		p_AD_User_ID = AD_User_ID;
		createContents();
	}



	@Override
	public int getDefault_JP_ToDo_Category_ID()
	{
		return 0;
	}


	@Override
	public int getWindowNo()
	{
		if(i_ToDoPopupwindowCaller != null)
		{
			return i_ToDoPopupwindowCaller.getWindowNo();
		}

		return 0;
	}


	@Override
	public String getJP_ToDo_Calendar()
	{
		return p_JP_ToDo_Calendar;
	}


}
