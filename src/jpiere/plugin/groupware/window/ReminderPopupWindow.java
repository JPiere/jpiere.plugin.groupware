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
package jpiere.plugin.groupware.window;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.util.Callback;
import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WDatetimeEditor;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WNumberEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.editor.WTimeEditor;
import org.adempiere.webui.editor.WUrlEditor;
import org.adempiere.webui.editor.WYesNoEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridField;
import org.compiere.model.GridFieldVO;
import org.compiere.model.MColumn;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MTable;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
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
import org.zkoss.zul.South;
import org.zkoss.zul.Timebox;

import jpiere.plugin.groupware.form.TeamMemberPopup;
import jpiere.plugin.groupware.model.I_ToDo;
import jpiere.plugin.groupware.model.I_ToDoReminder;
import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoReminder;
import jpiere.plugin.groupware.model.MToDoTeam;
import jpiere.plugin.groupware.model.MToDoTeamReminder;
import jpiere.plugin.groupware.util.GroupwareToDoUtil;


/**
 * JPIERE-0480 ToDo Reminder Popup Window
 *
 *
 * @author h.hagiwara
 *
 */
public class ReminderPopupWindow extends Window implements EventListener<Event> ,ValueChangeListener {


	private static final CLogger log = CLogger.getCLogger(ReminderPopupWindow.class);

	private Properties ctx = null;

	private ToDoPopupWindow p_TodoPopupWindow = null;
	private PersonalToDoListWindow p_PersonalTodoListWindow = null;

	private I_ToDo p_iToDo = null;
	private I_ToDoReminder i_Reminder = null;
	private int p_Reminder_ID = 0;
	private boolean p_IsPersonalToDo = false;
	private boolean p_IsNewRecord = true;
	private boolean p_IsUpdatable = false;
	private boolean p_haveParentTeamToDoReminder = false;
	private boolean p_IsUpdatebale_IsConfirmed = false;
	private boolean p_IsUpdatebale_Comments = false;
	private boolean p_IsUpdatebale_StatisticsInfo = false;
	private boolean p_IsDirty = false;
	private int  p_Login_User_ID = 0;

	private int p_Add_Hours = 5;
	private int p_Add_Mins = 15;

	private North north ;
	private Center center;
	private ConfirmPanel confirmPanel;

	//*** Constants ***//
	private final static String BUTTON_NAME_ZOOM_PERSONALTODO_REMINDER = "ZOOM_P";
	private final static String BUTTON_NAME_ZOOM_TEAMTODO_REMINDER = "ZOOM_T";

	private final static String BUTTON_NAME_UNDO = "REDO";
	private final static String BUTTON_NAME_SAVE = "SAVE";
	private final static String BUTTON_NAME_DELETE = "DELETE";

	private final static String BUTTON_NAME_ADD_HOURS = "ADD_HOURS";
	private final static String BUTTON_NAME_ADD_MINS = "ADD_MINS";

	private final static String BUTTON_NAME_SHOW_TEAM_MEMBER = "SHOW_TEAM_MEMBER";
	private final static String BUTTON_NAME_SHOW_TEAM_TODO_REMINDER = "SHOW_TEAM_TODO_REMINDER";

	/*** Web Components ***/
	// WEditors & Labels
	private Map<String, Label> map_Label = new HashMap<String, Label>();
	private Map<String, WEditor> map_Editor = new HashMap<String, WEditor>();


	//Buttons
	private Button zoomPersonalToDoReminderBtn = null;
	private Button zoomTeamToDoReminderBtn = null;
	private Button undoBtn = null;
	private Button saveBtn = null;
	private Button deleteBtn = null;
	private Button addHoursBtn = null;
	private Button addMinsBtn = null;

	private Button showTeamMemberBtn = null;
	private Button showTeamToDoReminderBtn = null;

	private boolean mobile = false;

	public ReminderPopupWindow(PersonalToDoListWindow PersonalTodoListWindow, I_ToDo i_ToDo, int reminder_ID)
	{
		super();
		ctx = Env.getCtx();
		mobile = ClientInfo.isMobile();
		p_Login_User_ID = Env.getAD_User_ID(ctx);
		this.p_PersonalTodoListWindow = PersonalTodoListWindow;
		this.p_TodoPopupWindow = PersonalTodoListWindow.getToDoPopupWindow();
		init(i_ToDo, reminder_ID);
	}

	public ReminderPopupWindow(ToDoPopupWindow todoPopupWindow, I_ToDo i_ToDo, int reminder_ID)
	{
		super();
		ctx = Env.getCtx();
		mobile = ClientInfo.isMobile();
		p_Login_User_ID = Env.getAD_User_ID(ctx);
		this.p_TodoPopupWindow = todoPopupWindow;
		init(i_ToDo, reminder_ID);
	}

	private void init(I_ToDo i_ToDo, int reminder_ID)
	{
		this.setSclass("popup-dialog request-dialog");
		this.setBorder("normal");
		this.setShadow(true);
		this.setClosable(true);


		this.p_iToDo = i_ToDo;
		this.p_Reminder_ID = reminder_ID;
		if(reminder_ID == 0)
			p_IsNewRecord = true;
		else
			p_IsNewRecord = false;

		if(i_ToDo.get_TableName().equals(MToDo.Table_Name))
		{
			p_IsPersonalToDo = true;
			i_Reminder = new MToDoReminder(ctx, reminder_ID, null);
			if(reminder_ID == 0)
			{
				i_Reminder.setAD_Org_ID(i_ToDo.getAD_Org_ID());
				i_Reminder.setJP_ToDo_ID(i_ToDo.get_ID());
				i_Reminder.setJP_ToDo_ReminderType(MToDoReminder.JP_TODO_REMINDERTYPE_SendMail);
			}
			String name = GroupwareToDoUtil.trimName(p_iToDo.getName());
			this.setTitle("["+Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_ToDo_Reminder_ID)+"] " + name);

		}else {

			p_IsPersonalToDo = false;
			i_Reminder = new MToDoTeamReminder(ctx, reminder_ID, null);
			if(reminder_ID == 0)
			{
				i_Reminder.setAD_Org_ID(i_ToDo.getAD_Org_ID());
				i_Reminder.setJP_ToDo_Team_ID(i_ToDo.get_ID());
				i_Reminder.setJP_ToDo_ReminderType(MToDoReminder.JP_TODO_REMINDERTYPE_SendMail);
			}
			String name = GroupwareToDoUtil.trimName(p_iToDo.getName());
			this.setTitle("["+Msg.getElement(ctx, MToDoTeamReminder.COLUMNNAME_JP_ToDo_Team_Reminder_ID)+"] " + name);
		}
		updateControlParameter();
		createLabelMap();
		createEditorMap();
		updateEditorStatus();
		updateEditorValue();

		Borderlayout borderlayout = new Borderlayout();
		this.appendChild(borderlayout);
		ZKUpdateUtil.setHflex(borderlayout, "1");
		ZKUpdateUtil.setVflex(borderlayout, "1");

		north = new North();
		north = updateNorth();
		borderlayout.appendChild(north);

		center = new Center();
		ZKUpdateUtil.setVflex(center, "max");

		center.setSclass("dialog-content");
		center.setAutoscroll(true);
		center = updateCenter();
		borderlayout.appendChild(center);

		if(p_IsNewRecord)
		{
			p_IsNewRecord = true;
			South southPane = new South();
			southPane.setSclass("dialog-footer");
			borderlayout.appendChild(southPane);
			confirmPanel = new ConfirmPanel(true);
			confirmPanel.addActionListener(this);
			southPane.appendChild(confirmPanel);

		}

		if(mobile)
		{
			ZKUpdateUtil.setWindowWidthX(this,  SessionManager.getAppDesktop().getClientInfo().desktopWidth);
			ZKUpdateUtil.setWindowHeightX(this,  SessionManager.getAppDesktop().getClientInfo().desktopHeight);
		}else {
			ZKUpdateUtil.setWindowWidthX(this, 480);
			ZKUpdateUtil.setWindowHeightX(this, 480);
		}

	}

	private void updateControlParameter()
	{

		if(p_IsNewRecord)
		{
			p_IsUpdatable = true;
			p_IsUpdatebale_Comments = true;
			p_haveParentTeamToDoReminder = false;

		}else {

			if(i_Reminder.get_TableName().equals(MToDoReminder.Table_Name))
			{
				if(i_Reminder.getJP_ToDo_Team_Reminder_ID() == 0)
					p_haveParentTeamToDoReminder = false;
				else
					p_haveParentTeamToDoReminder = true;

			}else {
				p_haveParentTeamToDoReminder = false;
			}

			if(i_Reminder.isSentReminderJP() || i_Reminder.isProcessed() || !i_Reminder.isActive() || p_haveParentTeamToDoReminder )
			{
				p_IsUpdatable = false;

			}else {

				if(p_iToDo.getAD_User_ID() == p_Login_User_ID)
				{
					p_IsUpdatable = true;
				}
				else if(i_Reminder.getCreatedBy()  == p_Login_User_ID )
				{
					p_IsUpdatable = true;

				}else {

					p_IsUpdatable = false;

				}
			}

			//*** Comments ***//
			if(p_iToDo.getAD_User_ID() == p_Login_User_ID || i_Reminder.getCreatedBy()  == p_Login_User_ID)
			{
				p_IsUpdatebale_Comments = true;
			}else {
				p_IsUpdatebale_Comments = false;
			}
		}

		//StatisticsInfo

		if(p_iToDo.getAD_User_ID() == p_Login_User_ID)
			p_IsUpdatebale_StatisticsInfo = true;
		else
			p_IsUpdatebale_StatisticsInfo = false;

		//*** IsConfirmed ***//
		if(p_iToDo.getAD_User_ID() != p_Login_User_ID)
		{
			p_IsUpdatebale_IsConfirmed = false;

		}else {

			if(i_Reminder.isConfirmed())
			{
				p_IsUpdatebale_IsConfirmed = false;
			}else {
				p_IsUpdatebale_IsConfirmed = true;
			}

		}
	}

	private void createLabelMap()
	{
		map_Label.put(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType)) );
		map_Label.put(MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime)) );
		map_Label.put(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime)) );
		map_Label.put(MToDoReminder.COLUMNNAME_JP_MailFrequency, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_MailFrequency)) );
		map_Label.put(MToDoReminder.COLUMNNAME_BroadcastFrequency, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_BroadcastFrequency)) );
		map_Label.put(MToDoReminder.COLUMNNAME_Description, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_Description)) );
		map_Label.put(MToDoReminder.COLUMNNAME_URL, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_URL)) );
		map_Label.put(MToDoReminder.COLUMNNAME_IsSentReminderJP, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_IsSentReminderJP)) );
		map_Label.put(MToDoReminder.COLUMNNAME_IsConfirmed, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_IsConfirmed)) );
		map_Label.put(MToDoReminder.COLUMNNAME_JP_Confirmed, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_Confirmed)) );

		if(p_IsPersonalToDo)
		{
			map_Label.put(MToDoReminder.COLUMNNAME_Comments, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_Comments)) );
			map_Label.put(MToDoReminder.COLUMNNAME_IsConfirmed, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_IsConfirmed)) );
			map_Label.put(MToDoReminder.COLUMNNAME_JP_Statistics_YesNo, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_Statistics_YesNo)));
			map_Label.put(MToDoReminder.COLUMNNAME_JP_Statistics_Choice, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_Statistics_Choice)));
			map_Label.put(MToDoReminder.COLUMNNAME_JP_Statistics_DateAndTime, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_Statistics_DateAndTime)));
			map_Label.put(MToDoReminder.COLUMNNAME_JP_Statistics_Number, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_Statistics_Number)));

		}else {
			map_Label.put(MToDoTeamReminder.COLUMNNAME_JP_ToDo_RemindTarget, new Label(Msg.getElement(ctx, MToDoTeamReminder.COLUMNNAME_JP_ToDo_RemindTarget)) );
			map_Label.put(MToDoTeamReminder.COLUMNNAME_JP_Mandatory_Statistics_Info, new Label(Msg.getElement(ctx, MToDoTeamReminder.COLUMNNAME_JP_Mandatory_Statistics_Info)) );
			map_Label.put(MToDoTeamReminder.COLUMNNAME_JP_Team_ID, new Label(Msg.getElement(ctx, MToDoTeamReminder.COLUMNNAME_JP_Team_ID)) );
		}
	}

	private void createEditorMap()
	{
		//*** JP_ToDo_ReminderType ***//
		MLookup lookup_JP_ToDo_ReminderType = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDoReminder.Table_Name,  MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType),  DisplayType.List);
		WTableDirEditor editor_JP_ToDo_ReminderType = new WTableDirEditor(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType, true, p_haveParentTeamToDoReminder? true : !p_IsUpdatable, true, lookup_JP_ToDo_ReminderType);
		editor_JP_ToDo_ReminderType.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_ReminderType.getComponent(), "true");
		map_Editor.put(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType, editor_JP_ToDo_ReminderType);


		//*** JP_ToDo_RemindDate ***//
		WDateEditor editor_JP_ToDo_RemindDate = new WDateEditor(MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate, false, p_haveParentTeamToDoReminder? true : !p_IsUpdatable, true, null);
		editor_JP_ToDo_RemindDate.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_RemindDate.getComponent(), "true");
		map_Editor.put(MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate, editor_JP_ToDo_RemindDate);


		//*** JP_ToDo_RemindTime ***//
		WTimeEditor editor_JP_ToDo_RemindTime = new WTimeEditor(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime, false, p_haveParentTeamToDoReminder? true : !p_IsUpdatable, true, null);
		editor_JP_ToDo_RemindTime.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_RemindTime.getComponent(), "true");
		Timebox reminderTimebox = editor_JP_ToDo_RemindTime.getComponent();
		reminderTimebox.setFormat("HH:mm");
		map_Editor.put(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime, editor_JP_ToDo_RemindTime);


		//*** Mail Frequency ***//
		MLookup lookup_MailFrequency = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDoReminder.Table_Name,  MToDoReminder.COLUMNNAME_JP_MailFrequency),  DisplayType.List);
		WTableDirEditor editor_MailFrequency = new WTableDirEditor(MToDoReminder.COLUMNNAME_JP_MailFrequency, true, p_haveParentTeamToDoReminder? true : !p_IsUpdatable, true, lookup_MailFrequency);
		editor_MailFrequency.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_MailFrequency.getComponent(), "true");
		map_Editor.put(MToDoReminder.COLUMNNAME_JP_MailFrequency, editor_MailFrequency);


		//*** BroadcastFrequency ***//
		MLookup lookup_BroadcastFrequency = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDoReminder.Table_Name,  MToDoReminder.COLUMNNAME_BroadcastFrequency),  DisplayType.List);
		WTableDirEditor editor_BroadcastFrequency = new WTableDirEditor(MToDoReminder.COLUMNNAME_BroadcastFrequency, true, p_haveParentTeamToDoReminder? true : !p_IsUpdatable, true, lookup_BroadcastFrequency);
		editor_BroadcastFrequency.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_BroadcastFrequency.getComponent(), "true");
		map_Editor.put(MToDoReminder.COLUMNNAME_BroadcastFrequency, editor_BroadcastFrequency);


		//*** Description ***//
		WStringEditor editor_Description = new WStringEditor(MToDoReminder.COLUMNNAME_Description, true, p_haveParentTeamToDoReminder? true : !p_IsUpdatable, true, 30, 30, "", null);
		editor_Description.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_Description.getComponent(), "true");
		editor_Description.getComponent().setRows(5);
		map_Editor.put(MToDoReminder.COLUMNNAME_Description, editor_Description);


		//*** URL ***//
		GridFieldVO gridFieldVO = GridFieldVO.createParameter(ctx, 0, 0, 0, 0, MToDo.COLUMNNAME_URL, MToDo.COLUMNNAME_URL, DisplayType.URL, 0, false, false, null);
		WUrlEditor editor_URL = new WUrlEditor(new GridField(gridFieldVO));
		editor_URL.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_URL.getComponent(), "true");
		map_Editor.put("URL", editor_URL);


		//*** IsSentReminderJP ***//
		WYesNoEditor editor_IsSentReminderJP = new WYesNoEditor(MToDoReminder.COLUMNNAME_IsSentReminderJP, Msg.getElement(ctx, MToDoReminder.COLUMNNAME_IsSentReminderJP), null, true, true, true);
		editor_IsSentReminderJP.addValueChangeListener(this);
		map_Editor.put(MToDoReminder.COLUMNNAME_IsSentReminderJP, editor_IsSentReminderJP);


		//*** IsConfirmedJP ***//
		WYesNoEditor editor_IsConfirmed = new WYesNoEditor(MToDoReminder.COLUMNNAME_IsConfirmed, Msg.getElement(ctx, MToDoReminder.COLUMNNAME_IsConfirmed), null, true, p_IsUpdatebale_IsConfirmed, true);
		editor_IsConfirmed.addValueChangeListener(this);
		map_Editor.put(MToDoReminder.COLUMNNAME_IsConfirmed, editor_IsConfirmed);


		//*** JP_ToDo_RemindTime ***//
		WDatetimeEditor editor_JP_Confirmed = new WDatetimeEditor(MToDoReminder.COLUMNNAME_JP_Confirmed, false, p_haveParentTeamToDoReminder? true : true, true, null);
		editor_JP_Confirmed.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_Confirmed.getComponent(), "true");
		map_Editor.put(MToDoReminder.COLUMNNAME_JP_Confirmed, editor_JP_Confirmed);


		if(p_IsPersonalToDo)
		{
			//*** Comments  ***//
			WStringEditor editor_Comments = new WStringEditor(MToDoReminder.COLUMNNAME_Comments, true, !p_IsUpdatable, true, 30, 30, "", null);
			editor_Comments.addValueChangeListener(this);
			ZKUpdateUtil.setHflex(editor_Comments.getComponent(), "true");
			editor_Comments.getComponent().setRows(3);
			map_Editor.put(MToDoReminder.COLUMNNAME_Comments, editor_Comments);


			//*** JP_Statistics_YesNo  ***//
			MLookup lookup_JP_Statistics_YesNo = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDoReminder.Table_Name, MToDoReminder.COLUMNNAME_JP_Statistics_YesNo),  DisplayType.List);
			WTableDirEditor editor_JP_Statistics_YesNo = new WTableDirEditor(lookup_JP_Statistics_YesNo, Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_Statistics_YesNo), null, false, !p_IsUpdatable, true);
			editor_JP_Statistics_YesNo.addValueChangeListener(this);
			ZKUpdateUtil.setHflex(editor_JP_Statistics_YesNo.getComponent(), "true");
			map_Editor.put(MToDoReminder.COLUMNNAME_JP_Statistics_YesNo, editor_JP_Statistics_YesNo);


			//*** JP_Statistics_Choice ***//
			MLookup lookup_JP_Statistics_Choice = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDoReminder.Table_Name, MToDoReminder.COLUMNNAME_JP_Statistics_Choice),  DisplayType.List);
			WTableDirEditor editor_JP_Statistics_Choice = new WTableDirEditor(lookup_JP_Statistics_Choice, Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_Statistics_Choice), null, false, !p_IsUpdatable, true);
			editor_JP_Statistics_Choice.addValueChangeListener(this);
			ZKUpdateUtil.setHflex(editor_JP_Statistics_Choice.getComponent(), "true");
			map_Editor.put(MToDoReminder.COLUMNNAME_JP_Statistics_Choice, editor_JP_Statistics_Choice);


			//*** JP_Statistics_DateAndTime ***//
			WDatetimeEditor editor_JP_Statistics_DateAndTime = new WDatetimeEditor(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_Statistics_DateAndTime), null, false, !p_IsUpdatable, true);
			editor_JP_Statistics_DateAndTime.addValueChangeListener(this);
			ZKUpdateUtil.setHflex((HtmlBasedComponent)editor_JP_Statistics_DateAndTime.getComponent(), "true");
			map_Editor.put(MToDoReminder.COLUMNNAME_JP_Statistics_DateAndTime, editor_JP_Statistics_DateAndTime);


			//*** JP_Statistics_Number ***//
			WNumberEditor editor_JP_Statistics_Number = new WNumberEditor(MToDoReminder.COLUMNNAME_JP_Statistics_Number, false, !p_IsUpdatable, true, DisplayType.Number, Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_Statistics_Number));
			editor_JP_Statistics_Number.addValueChangeListener(this);
			ZKUpdateUtil.setHflex(editor_JP_Statistics_Number.getComponent(), "true");
			map_Editor.put(MToDoReminder.COLUMNNAME_JP_Statistics_Number, editor_JP_Statistics_Number);

		}else {

			//*** JP_ToDo_RemindTarget ***//
			MLookup lookup_JP_ToDo_ReminderTarget = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDoTeamReminder.Table_Name,  MToDoTeamReminder.COLUMNNAME_JP_ToDo_RemindTarget),  DisplayType.List);
			WTableDirEditor editor_JP_ToDo_RemindTarget = new WTableDirEditor(MToDoTeamReminder.COLUMNNAME_JP_ToDo_RemindTarget, true, p_haveParentTeamToDoReminder? true : !p_IsUpdatable, true, lookup_JP_ToDo_ReminderTarget);
			editor_JP_ToDo_RemindTarget.addValueChangeListener(this);
			ZKUpdateUtil.setHflex(editor_JP_ToDo_RemindTarget.getComponent(), "true");
			map_Editor.put(MToDoTeamReminder.COLUMNNAME_JP_ToDo_RemindTarget, editor_JP_ToDo_RemindTarget);


			//*** JP_Team_ID ***//
			String validationCode = "JP_Team.AD_User_ID IS NULL OR JP_Team.AD_User_ID=" + Env.getAD_User_ID(ctx);//Login user
			MLookup lookup_JP_Team_ID = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDoTeamReminder.Table_Name, MToDoTeamReminder.COLUMNNAME_JP_Team_ID),  DisplayType.Search);
			lookup_JP_Team_ID.getLookupInfo().ValidationCode = validationCode;
			WSearchEditor editor_JP_Team_ID = new WSearchEditor(lookup_JP_Team_ID, Msg.getElement(ctx, MToDoTeamReminder.COLUMNNAME_JP_Team_ID), null, false, !p_IsUpdatable, true);
			editor_JP_Team_ID.addValueChangeListener(this);
			ZKUpdateUtil.setHflex(editor_JP_Team_ID.getComponent(), "true");
			map_Editor.put(MToDoTeamReminder.COLUMNNAME_JP_Team_ID, editor_JP_Team_ID);


			//*** JP_Mandatory_Statistics_Info ***//
			MLookup lookup_JP_Mandatory_Statistics_Info = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDoTeam.Table_Name,  MToDoTeamReminder.COLUMNNAME_JP_Mandatory_Statistics_Info),  DisplayType.List);
			WTableDirEditor editor_JP_Mandatory_Statistics_Info= new WTableDirEditor(MToDoTeamReminder.COLUMNNAME_JP_Mandatory_Statistics_Info, true, !p_IsUpdatable, true, lookup_JP_Mandatory_Statistics_Info);
			editor_JP_Mandatory_Statistics_Info.addValueChangeListener(this);
			ZKUpdateUtil.setHflex(editor_JP_Mandatory_Statistics_Info.getComponent(), "true");
			map_Editor.put(MToDoTeamReminder.COLUMNNAME_JP_Mandatory_Statistics_Info, editor_JP_Mandatory_Statistics_Info);

		}
	}

	private void updateEditorStatus()
	{
		if(p_IsNewRecord)
		{
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType).setReadWrite(true);
		}else {
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType).setReadWrite(false);
		}

		map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate).setReadWrite(p_IsUpdatable);
		map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime).setReadWrite(p_IsUpdatable);
		map_Editor.get(MToDoReminder.COLUMNNAME_BroadcastFrequency).setReadWrite(p_IsUpdatable);
		map_Editor.get(MToDoReminder.COLUMNNAME_JP_MailFrequency).setReadWrite(p_IsUpdatable);
		map_Editor.get(MToDoReminder.COLUMNNAME_Description).setReadWrite(p_IsUpdatable);
		map_Editor.get(MToDoReminder.COLUMNNAME_URL).setReadWrite(p_IsUpdatable);
		map_Editor.get(MToDoReminder.COLUMNNAME_IsSentReminderJP).setReadWrite(false);

		if(p_IsPersonalToDo)
		{
			map_Editor.get(MToDoReminder.COLUMNNAME_Comments).setReadWrite(p_IsUpdatebale_Comments);
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_Confirmed).setReadWrite(false);
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_YesNo).setReadWrite(p_IsUpdatebale_StatisticsInfo);
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_Choice).setReadWrite(p_IsUpdatebale_StatisticsInfo);
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_DateAndTime).setReadWrite(p_IsUpdatebale_StatisticsInfo);
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_Number).setReadWrite(p_IsUpdatebale_StatisticsInfo);
		}else {
			map_Editor.get(MToDoTeamReminder.COLUMNNAME_JP_ToDo_RemindTarget).setReadWrite(p_IsUpdatable);
			map_Editor.get(MToDoTeamReminder.COLUMNNAME_JP_Team_ID).setReadWrite(p_IsUpdatable);
		}

		if(i_Reminder.isConfirmed())
			map_Editor.get(MToDoReminder.COLUMNNAME_IsConfirmed).setReadWrite(false);
		else
			map_Editor.get(MToDoReminder.COLUMNNAME_IsConfirmed).setReadWrite(p_IsUpdatebale_IsConfirmed);

	}

	private void updateEditorValue()
	{
		if(p_IsNewRecord)
		{
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType).setValue(i_Reminder.getJP_ToDo_ReminderType());
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate).setValue(Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN)));
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime).setValue(Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.now())));
			map_Editor.get(MToDoReminder.COLUMNNAME_IsSentReminderJP).setValue("N");

			map_Editor.get(MToDoReminder.COLUMNNAME_JP_MailFrequency).setValue(MToDoReminder.JP_MAILFREQUENCY_JustOne);
			map_Editor.get(MToDoReminder.COLUMNNAME_BroadcastFrequency).setValue(MToDoReminder.BROADCASTFREQUENCY_JustOnce);

			if(p_IsPersonalToDo)
			{
				map_Editor.get(MToDoReminder.COLUMNNAME_IsConfirmed).setValue("N");
				map_Editor.get(MToDoReminder.COLUMNNAME_JP_Confirmed).setValue(null);

			}else {

				map_Editor.get(MToDoTeamReminder.COLUMNNAME_JP_ToDo_RemindTarget).setValue(MToDoTeamReminder.JP_TODO_REMINDTARGET_AllUserOfPersonalToDo);
				map_Editor.get(MToDoTeamReminder.COLUMNNAME_JP_Team_ID).setValue(null);
			}

		}else {

			map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType).setValue(i_Reminder.getJP_ToDo_ReminderType());
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate).setValue(i_Reminder.getJP_ToDo_RemindTime());
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime).setValue(i_Reminder.getJP_ToDo_RemindTime());
			map_Editor.get(MToDoReminder.COLUMNNAME_Description).setValue(i_Reminder.getDescription());
			map_Editor.get(MToDoReminder.COLUMNNAME_URL).setValue(i_Reminder.getURL());
			map_Editor.get(MToDoReminder.COLUMNNAME_IsSentReminderJP).setValue(i_Reminder.isSentReminderJP());

			map_Editor.get(MToDoReminder.COLUMNNAME_JP_MailFrequency).setValue(i_Reminder.getJP_MailFrequency());
			map_Editor.get(MToDoReminder.COLUMNNAME_BroadcastFrequency).setValue(i_Reminder.getBroadcastFrequency());

			if(p_IsPersonalToDo)
			{
				map_Editor.get(MToDoReminder.COLUMNNAME_Comments).setValue(i_Reminder.getComments());
				map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_YesNo).setValue(i_Reminder.getJP_Statistics_YesNo());
				map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_Choice).setValue(i_Reminder.getJP_Statistics_Choice());
				map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_DateAndTime).setValue(i_Reminder.getJP_Statistics_DateAndTime());
				map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_Number).setValue(i_Reminder.getJP_Statistics_Number());

				map_Editor.get(MToDoReminder.COLUMNNAME_IsConfirmed).setValue(i_Reminder.isConfirmed());
				map_Editor.get(MToDoReminder.COLUMNNAME_JP_Confirmed).setValue(i_Reminder.getJP_Confirmed());

			}else {

				map_Editor.get(MToDoTeamReminder.COLUMNNAME_JP_ToDo_RemindTarget).setValue(i_Reminder.getJP_ToDo_RemindTarget());
				map_Editor.get(MToDoTeamReminder.COLUMNNAME_JP_Team_ID).setValue(i_Reminder.getJP_Team_ID() == 0? null : i_Reminder.getJP_Team_ID());
				map_Editor.get(MToDoTeamReminder.COLUMNNAME_JP_Mandatory_Statistics_Info).setValue(i_Reminder.getJP_Mandatory_Statistics_Info());
			}

		}
	}

	private North updateNorth()
	{
		if(north.getFirstChild() != null)
			north.getFirstChild().detach();

		if(p_IsNewRecord)
			return north;

		Hlayout hlyaout = new Hlayout();
		hlyaout.setStyle("margin:2px 2px 2px 2px; padding:2px 2px 2px 2px;");// border: solid 1px #dddddd;
		north.appendChild(hlyaout);

		//Personal ToDo Zoom Button
		if(p_IsPersonalToDo)
		{
			if(zoomPersonalToDoReminderBtn == null)
			{
				zoomPersonalToDoReminderBtn = new Button();
				if (ThemeManager.isUseFontIconForImage())
					zoomPersonalToDoReminderBtn.setIconSclass("z-icon-Zoom");
				else
					zoomPersonalToDoReminderBtn.setImage(ThemeManager.getThemeResource("images/Zoom16.png"));
				zoomPersonalToDoReminderBtn.setClass("btn-small");
				zoomPersonalToDoReminderBtn.setName(BUTTON_NAME_ZOOM_PERSONALTODO_REMINDER);
				zoomPersonalToDoReminderBtn.setTooltiptext(Msg.getMsg(ctx, "JP_Zoom_To_PersonalToDoReminder"));//Zoom to Personal ToDo Reminder
				zoomPersonalToDoReminderBtn.addEventListener(Events.ON_CLICK, this);
			}
			hlyaout.appendChild(zoomPersonalToDoReminderBtn);
		}


		//Team ToDo Zoom Button
		if(zoomTeamToDoReminderBtn == null)
		{
			zoomTeamToDoReminderBtn = new Button();
			if (ThemeManager.isUseFontIconForImage())
				zoomTeamToDoReminderBtn.setIconSclass("z-icon-ZoomAcross");
			else
				zoomTeamToDoReminderBtn.setImage(ThemeManager.getThemeResource("images/ZoomAcross16.png"));
			zoomTeamToDoReminderBtn.setClass("btn-small");
			zoomTeamToDoReminderBtn.setName(BUTTON_NAME_ZOOM_TEAMTODO_REMINDER);
			zoomTeamToDoReminderBtn.setTooltiptext(Msg.getMsg(ctx, "JP_Zoom_To_TeamToDoReminder"));//Zoom to Team ToDo Reminder
			zoomTeamToDoReminderBtn.addEventListener(Events.ON_CLICK, this);
		}

		if(p_IsPersonalToDo)
		{
			if(i_Reminder.getJP_ToDo_Team_Reminder_ID() == 0)
			{
				zoomTeamToDoReminderBtn.setEnabled(false);
			}else {
				zoomTeamToDoReminderBtn.setEnabled(true);
			}
		}else {
			zoomTeamToDoReminderBtn.setEnabled(true);
		}
		hlyaout.appendChild(zoomTeamToDoReminderBtn);


		hlyaout.appendChild(GroupwareToDoUtil.getDividingLine());

		//Undo Button
		if(undoBtn == null)
		{
			undoBtn = new Button();
			if (ThemeManager.isUseFontIconForImage())
				undoBtn.setIconSclass("z-icon-Ignore");
			else
				undoBtn.setImage(ThemeManager.getThemeResource("images/Undo16.png"));
			undoBtn.setClass("btn-small");
			undoBtn.setName(BUTTON_NAME_UNDO);
			undoBtn.setTooltiptext(Msg.getMsg(ctx, "Ignore"));
			undoBtn.addEventListener(Events.ON_CLICK, this);
		}
		if(p_IsDirty)
			undoBtn.setEnabled(true);
		else
			undoBtn.setEnabled(false);
		hlyaout.appendChild(undoBtn);


		//Save Button
		if(saveBtn == null)
		{
			saveBtn = new Button();
			if (ThemeManager.isUseFontIconForImage())
				saveBtn.setIconSclass("z-icon-Save");
			else
				saveBtn.setImage(ThemeManager.getThemeResource("images/Save16.png"));
			saveBtn.setClass("btn-small");
			saveBtn.setName(BUTTON_NAME_SAVE);
			saveBtn.setTooltiptext(Msg.getMsg(ctx, "Save"));
			saveBtn.addEventListener(Events.ON_CLICK, this);
		}
		if(p_IsDirty)
			saveBtn.setEnabled(true);
		else
			saveBtn.setEnabled(false);
		hlyaout.appendChild(saveBtn);

		hlyaout.appendChild(GroupwareToDoUtil.getDividingLine());

		//Delete Button
		if(deleteBtn == null)
		{
			deleteBtn = new Button();
			if (ThemeManager.isUseFontIconForImage())
				deleteBtn.setIconSclass("z-icon-Delete");
			else
				deleteBtn.setImage(ThemeManager.getThemeResource("images/Delete16.png"));
			deleteBtn.setClass("btn-small");
			deleteBtn.setName(BUTTON_NAME_DELETE);
			deleteBtn.setTooltiptext(Msg.getMsg(ctx, "Delete"));
			deleteBtn.addEventListener(Events.ON_CLICK, this);
		}

		if(i_Reminder.isProcessed())
		{
			deleteBtn.setEnabled(false);
		}else {
			deleteBtn.setEnabled(p_IsUpdatable);
		}
		hlyaout.appendChild(deleteBtn);

		return north;
	}

	private Center updateCenter()
	{
		if(center.getFirstChild() != null)
			center.getFirstChild().detach();

		if(addHoursBtn == null)
		{
			addHoursBtn = new Button();
			addHoursBtn.setClass("btn-small");
			addHoursBtn.setName(BUTTON_NAME_ADD_HOURS);
			addHoursBtn.setLabel("+"+p_Add_Hours+Msg.getMsg(ctx, "JP_Hours"));
			addHoursBtn.setVisible(p_haveParentTeamToDoReminder? false : p_IsUpdatable);
			addHoursBtn.addEventListener(Events.ON_CLICK, this);
			ZKUpdateUtil.setHflex(addHoursBtn, "true");
		}

		if(addMinsBtn == null)
		{
			addMinsBtn = new Button();
			addMinsBtn.setClass("btn-small");
			addMinsBtn.setName(BUTTON_NAME_ADD_MINS);
			addMinsBtn.setLabel("+"+p_Add_Mins+Msg.getMsg(ctx, "JP_Mins"));
			addMinsBtn.setVisible(p_haveParentTeamToDoReminder? false : p_IsUpdatable);
			addMinsBtn.addEventListener(Events.ON_CLICK, this);
			ZKUpdateUtil.setHflex(addMinsBtn, "true");
		}

		if(showTeamMemberBtn == null)
		{
			showTeamMemberBtn = new Button();
			if (ThemeManager.isUseFontIconForImage())
				showTeamMemberBtn.setIconSclass("z-icon-BPartner");
			else
				showTeamMemberBtn.setImage(ThemeManager.getThemeResource("images/BPartner16.png"));
			showTeamMemberBtn.setClass("btn-small");
			showTeamMemberBtn.setStyle("float:right;");
			showTeamMemberBtn.setName(BUTTON_NAME_SHOW_TEAM_MEMBER);
			showTeamMemberBtn.setLabel(Msg.getElement(ctx, MToDoTeam.COLUMNNAME_JP_Team_ID));
			showTeamMemberBtn.setVisible(true);
			showTeamMemberBtn.addEventListener(Events.ON_CLICK, this);
			ZKUpdateUtil.setHflex(showTeamMemberBtn, "max");
		}

		if(showTeamToDoReminderBtn == null)
		{
			showTeamToDoReminderBtn = new Button();
			if (ThemeManager.isUseFontIconForImage())
				showTeamToDoReminderBtn.setIconSclass("z-icon-Report");
			else
				showTeamToDoReminderBtn.setImage(ThemeManager.getThemeResource("images/Report16.png"));
			showTeamToDoReminderBtn.setName(BUTTON_NAME_SHOW_TEAM_TODO_REMINDER);
			showTeamToDoReminderBtn.setLabel(Msg.getMsg(ctx, "JP_ToDo_PersonalToDoReminderList"));
			showTeamToDoReminderBtn.setVisible(true);
			showTeamToDoReminderBtn.addEventListener(Events.ON_CLICK, this);
			ZKUpdateUtil.setHflex(showTeamToDoReminderBtn, "true");
		}

		Div centerContent = new Div();
		center.appendChild(centerContent);
		ZKUpdateUtil.setHeight(centerContent, "100%");

		Grid grid = GridFactory.newGridLayout();
		ZKUpdateUtil.setHeight(grid, "100%");
		ZKUpdateUtil.setHflex(grid, "1");
		centerContent.appendChild(grid);

		Rows rows = grid.newRows();
		Row row = null;
		if(!p_IsPersonalToDo)
		{
			//*** JP_ToDo_RemindTarget ***//
			row = rows.newRow();
			rows.appendChild(row);
			row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoTeamReminder.COLUMNNAME_JP_ToDo_RemindTarget), true),2);
			row.appendCellChild(map_Editor.get(MToDoTeamReminder.COLUMNNAME_JP_ToDo_RemindTarget).getComponent(),4);

			//*** JP_Team_ID ***//
			row = rows.newRow();
			rows.appendChild(row);
			//row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoTeamReminder.COLUMNNAME_JP_Team_ID), false),2);
			row.appendCellChild(showTeamMemberBtn,2);
			row.appendCellChild(map_Editor.get(MToDoTeamReminder.COLUMNNAME_JP_Team_ID).getComponent(),4);
		}


		//*** JP_ToDo_ReminderType ***//
		row = rows.newRow();
		rows.appendChild(row);
		row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType), true),2);
		row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType).getComponent(),4);


		//** JP_ToDo_RemindDateTime **//
		row = rows.newRow();
		row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate), true),2);
		row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate).getComponent(),2);

		row = rows.newRow();
		row.appendCellChild(new Label(""),2);
		row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime).getComponent(),2);
		row.appendCellChild(addHoursBtn,1);
		row.appendCellChild(addMinsBtn,1);


		//*** JP_ToDo_ReminderType ***//
		if(MToDoReminder.JP_TODO_REMINDERTYPE_BroadcastMessage.equals(i_Reminder.getJP_ToDo_ReminderType()))
   		{
			row = rows.newRow();
			rows.appendChild(row);
			row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoReminder.COLUMNNAME_BroadcastFrequency), true),2);
			row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_BroadcastFrequency).getComponent(),4);

   		}else if (MToDoReminder.JP_TODO_REMINDERTYPE_SendMail.equals(i_Reminder.getJP_ToDo_ReminderType())) {

			row = rows.newRow();
			rows.appendChild(row);
			row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoReminder.COLUMNNAME_JP_MailFrequency), true),2);
			row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_JP_MailFrequency).getComponent(),4);
   		}


		//*** Description ***//
		row = rows.newRow();
		row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoReminder.COLUMNNAME_Description), true),2);
		row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_Description).getComponent(),4);


		//*** URL ***//
		row = rows.newRow();
		row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDo.COLUMNNAME_URL), false),2);
		row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_URL).getComponent(),4);


		if(p_IsPersonalToDo)
		{
			row = rows.newRow();
			row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoReminder.COLUMNNAME_Comments), false),2);
			row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_Comments).getComponent(),4);
		}

		if(!p_IsNewRecord)
		{
			row = rows.newRow();
			row.appendCellChild(new Label(),2);
			row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_IsSentReminderJP).getComponent(),2);

			if(p_IsPersonalToDo)
			{
				row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_IsConfirmed).getComponent(),2);

				row = rows.newRow();
				row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoReminder.COLUMNNAME_JP_Confirmed), false),2);
				row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_JP_Confirmed).getComponent(),4);
			}
		}

		if(!p_IsPersonalToDo && !p_IsNewRecord)
		{
			row = rows.newRow();
			row.appendCellChild(showTeamToDoReminderBtn,6);
		}

		/********************************************************************************************
		 * Statistics Info
		 ********************************************************************************************/
		if(p_IsPersonalToDo && p_Reminder_ID == 0)
		{
			return center;

		}else if(p_IsPersonalToDo && p_Reminder_ID > 0) {

			MToDoReminder reminder = new MToDoReminder(ctx, p_Reminder_ID, null);
			if(reminder.getJP_ToDo_Team_Reminder_ID() == 0)
			{
				return center;

			}else {

				MToDoTeamReminder teamReminder = new MToDoTeamReminder(ctx, reminder.getJP_ToDo_Team_Reminder_ID(), null);
				if(MToDoTeamReminder.JP_MANDATORY_STATISTICS_INFO_None.equals(teamReminder.getJP_Mandatory_Statistics_Info()))
				{
					return center;
				}
			}
		}

		row = rows.newRow();
		Groupbox statisticsInfo_GroupBox = new Groupbox();
		statisticsInfo_GroupBox.setOpen(true);
		row.appendCellChild(statisticsInfo_GroupBox,6);

		String caption = Msg.getMsg(Env.getCtx(),"JP_StatisticsInfo");
		statisticsInfo_GroupBox.appendChild(new Caption(caption));
		Grid statisticsInfo_Grid  = GridFactory.newGridLayout();
		statisticsInfo_Grid.setStyle("background-color: #E9F0FF");
		statisticsInfo_Grid.setStyle("border: none");
		statisticsInfo_GroupBox.appendChild(statisticsInfo_Grid);

		Rows statisticsInfo_rows = statisticsInfo_Grid.newRows();

		if(p_IsPersonalToDo)
		{
			String JP_Mandatory_Statistics_Info = null;
			if(i_Reminder.getJP_ToDo_Team_Reminder_ID() > 0)
			{

				JP_Mandatory_Statistics_Info = new MToDoTeamReminder(ctx,i_Reminder.getJP_ToDo_Team_Reminder_ID() , null).getJP_Mandatory_Statistics_Info();
			}

			//*** JP_Statistics_YesNo  ***//
			if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_YesNo.equals(JP_Mandatory_Statistics_Info))
			{
				row = statisticsInfo_rows.newRow();
				row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoReminder.COLUMNNAME_JP_Statistics_YesNo), true),2);
				row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_YesNo).getComponent(),4);
				map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_YesNo).dynamicDisplay();
			}

			//*** JP_Statistics_Choice ***//
			if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_Choice.equals(JP_Mandatory_Statistics_Info))
			{
				row = statisticsInfo_rows.newRow();
				row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoReminder.COLUMNNAME_JP_Statistics_Choice), true),2);
				row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_Choice).getComponent(),4);
				map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_Choice).dynamicDisplay();
			}

			//*** JP_Statistics_DateAndTime ***//
			if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_DateAndTime.equals(JP_Mandatory_Statistics_Info))
			{
				row = statisticsInfo_rows.newRow();
				row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoReminder.COLUMNNAME_JP_Statistics_DateAndTime), true),2);
				row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_DateAndTime).getComponent(),4);
			}

			//*** JP_Statistics_Number ***//
			if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_Number.equals(JP_Mandatory_Statistics_Info))
			{
				row = statisticsInfo_rows.newRow();
				row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoReminder.COLUMNNAME_JP_Statistics_Number), true),2);
				row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_Number).getComponent(),4);
			}

		}else {

			row = statisticsInfo_rows.newRow();
			row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoTeamReminder.COLUMNNAME_JP_Mandatory_Statistics_Info), false),3);
			row.appendCellChild(map_Editor.get(MToDoTeamReminder.COLUMNNAME_JP_Mandatory_Statistics_Info).getComponent(),3);

		}


		return center;
	}

	@Override
	public void onEvent(Event event) throws Exception
	{
		Component comp = event.getTarget();

		if(comp instanceof Button)
		{

			Button btn = (Button) comp;
			String btnName = btn.getName();

			if(p_IsNewRecord)
			{
				if(btn == confirmPanel.getButton(ConfirmPanel.A_OK))
				{
					if(saveReminder())
					{
						this.onClose();
						return;
					}

				}else if(btn== confirmPanel.getButton(ConfirmPanel.A_CANCEL)) {

					this.onClose();
					return ;

				}
			}


			if(BUTTON_NAME_SAVE.equals(btnName))
			{
				saveReminder();

			}else if(BUTTON_NAME_DELETE.equals(btnName)){

				if(deleteReminder())
					this.onClose();

			}else if(BUTTON_NAME_UNDO.equals(btnName)) {

				p_IsDirty = false;
				//updateControlParameter(list_ToDoes.get(index).getJP_ToDo_ID());
				//updateWindowTitle();
				updateEditorValue();
				//updateEditorStatus();
				updateNorth();
				updateCenter();

			}else if(BUTTON_NAME_ADD_HOURS.equals(btnName) || BUTTON_NAME_ADD_MINS.equals(btnName)) {

				p_IsDirty = true;
				updateNorth();

				WDateEditor editor_RemindDate = (WDateEditor)map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate);
				Timestamp ts_RemindDate =(Timestamp)editor_RemindDate.getValue();
				if(ts_RemindDate == null)
				{
					String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime);
					throw new WrongValueException(editor_RemindDate.getComponent(), msg);
				}

				WTimeEditor editor_RemindTime = (WTimeEditor)map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime);
				Timestamp ts_RemindTime =(Timestamp)editor_RemindTime.getValue();
				LocalTime local_RemindTime = null;
				if(ts_RemindTime == null)
				{
					local_RemindTime = LocalTime.MIN;
				}else {
					local_RemindTime = ts_RemindTime.toLocalDateTime().toLocalTime();
					if(BUTTON_NAME_ADD_HOURS.equals(btnName))
					{
						local_RemindTime = local_RemindTime.plusHours(p_Add_Hours);
					}else if(BUTTON_NAME_ADD_MINS.equals(btnName)) {
						local_RemindTime = local_RemindTime.plusMinutes(p_Add_Mins);
					}
				}
				ts_RemindTime = Timestamp.valueOf(LocalDateTime.of(ts_RemindDate.toLocalDateTime().toLocalDate(), local_RemindTime));
				editor_RemindDate.setValue(ts_RemindTime);
				editor_RemindTime.setValue(ts_RemindTime);
				editor_RemindTime.getComponent().focus();

			}else if(BUTTON_NAME_ZOOM_PERSONALTODO_REMINDER.equals(btnName)){

				AEnv.zoom(MTable.getTable_ID(MToDoReminder.Table_Name), p_Reminder_ID);
				if(p_PersonalTodoListWindow != null)
				{
		    		p_PersonalTodoListWindow.hideBusyMask();
		    		p_PersonalTodoListWindow.dispose();
				}

				if(p_TodoPopupWindow != null)
				{
		    		p_TodoPopupWindow.hideBusyMask();
		    		p_TodoPopupWindow.dispose();
				}
			   	detach();

			}else if(BUTTON_NAME_ZOOM_TEAMTODO_REMINDER.equals(btnName)){

				if(p_IsPersonalToDo)
					AEnv.zoom(MTable.getTable_ID(MToDoTeamReminder.Table_Name), i_Reminder.getJP_ToDo_Team_Reminder_ID());
				else
					AEnv.zoom(MTable.getTable_ID(MToDoTeamReminder.Table_Name), p_Reminder_ID);

				if(p_PersonalTodoListWindow != null)
				{
		    		p_PersonalTodoListWindow.hideBusyMask();
		    		p_PersonalTodoListWindow.dispose();
				}

				if(p_TodoPopupWindow != null)
				{
		    		p_TodoPopupWindow.hideBusyMask();
		    		p_TodoPopupWindow.dispose();
				}
				detach();

			}else if(BUTTON_NAME_SHOW_TEAM_MEMBER.equals(btnName)) {

				TeamMemberPopup teampMemberPopup = new TeamMemberPopup(this, getJP_Team_ID());
				teampMemberPopup.setPage(showTeamMemberBtn.getPage());
				teampMemberPopup.open(showTeamMemberBtn,"start_before");

			}else if(BUTTON_NAME_SHOW_TEAM_TODO_REMINDER.equals(btnName)) {

				PersonalToDoReminderListWindow personalToDoReminderListWindow = new PersonalToDoReminderListWindow(this, (MToDoTeamReminder)i_Reminder);
				personalToDoReminderListWindow.setVisible(true);
				personalToDoReminderListWindow.setStyle("border: 2px");
				personalToDoReminderListWindow.setClosable(true);
				AEnv.showWindow(personalToDoReminderListWindow);

			}
		}
	}//onEvent

	private boolean saveReminder()
	{

		if(p_IsNewRecord)
		{
			;

		}else {

			//**Exclusive Control Start**//
			I_ToDoReminder db_Reminder = null;
			if(p_IsPersonalToDo)
			{
				db_Reminder = new MToDoReminder(ctx, p_Reminder_ID, null);
			}else {
				db_Reminder = new MToDoTeamReminder(ctx, p_Reminder_ID, null);
			}

			if(i_Reminder.getUpdated().compareTo(db_Reminder.getUpdated()) != 0)
			{
				//Current ToDo was changed by another user, so refreshed.
				FDialog.info(0, this, "JP_ToDo_CurrentToDoModified");

				i_Reminder.setAD_Org_ID(db_Reminder.getAD_Org_ID());
				i_Reminder.setJP_ToDo_ReminderType(db_Reminder.getJP_ToDo_ReminderType());
				i_Reminder.setJP_ToDo_RemindTime(db_Reminder.getJP_ToDo_RemindTime());
				i_Reminder.setDescription(db_Reminder.getDescription());
				i_Reminder.setURL(db_Reminder.getURL());
				i_Reminder.setIsActive(db_Reminder.isActive());
				i_Reminder.setIsSentReminderJP(db_Reminder.isSentReminderJP());
				i_Reminder.setProcessed(db_Reminder.isProcessed());
				i_Reminder.setUpdated(db_Reminder.getUpdated());

				i_Reminder.setBroadcastFrequency(db_Reminder.getBroadcastFrequency());
				i_Reminder.setJP_MailFrequency(db_Reminder.getJP_MailFrequency());

				if(p_IsPersonalToDo)
				{
					i_Reminder.setIsConfirmed(db_Reminder.isConfirmed());
					i_Reminder.setJP_Confirmed(db_Reminder.getJP_Confirmed());
					i_Reminder.setComments(db_Reminder.getComments());

					i_Reminder.setJP_Statistics_Choice(db_Reminder.getJP_Statistics_Choice());
					i_Reminder.setJP_Statistics_DateAndTime(db_Reminder.getJP_Statistics_DateAndTime());
					i_Reminder.setJP_Statistics_Number(db_Reminder.getJP_Statistics_Number());
					i_Reminder.setJP_Statistics_YesNo(db_Reminder.getJP_Statistics_YesNo());

				}else {

					i_Reminder.setJP_ToDo_RemindTarget(db_Reminder.getJP_ToDo_RemindTarget());
					i_Reminder.setJP_Team_ID(db_Reminder.getJP_Team_ID());
					i_Reminder.setJP_Mandatory_Statistics_Info(db_Reminder.getJP_Mandatory_Statistics_Info());

				}

				p_IsDirty = false;
				updateControlParameter();
				//updateWindowTitle();
				updateEditorValue();
				updateEditorStatus();
				updateNorth();
				updateCenter();

				return true;
			}
			//**Exclusive Control End**//
		}

		WEditor editor = null;


		if(!p_IsPersonalToDo)
		{
			//JP_ToDo_RemindTarget
			editor = map_Editor.get(MToDoTeamReminder.COLUMNNAME_JP_ToDo_RemindTarget);
			if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
			{
				String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MToDoTeamReminder.COLUMNNAME_JP_ToDo_RemindTarget);
				throw new WrongValueException(editor.getComponent(), msg);

			}else {
				i_Reminder.setJP_ToDo_RemindTarget((String)editor.getValue());
			}


			//JP_Team_ID
			editor = map_Editor.get(MToDoTeamReminder.COLUMNNAME_JP_Team_ID);
			if(editor.getValue() == null || ((Integer)editor.getValue()).intValue() == 0)
			{
				i_Reminder.setJP_Team_ID(0);
			}else {
				i_Reminder.setJP_Team_ID((Integer)editor.getValue());
			}
		}


		//JP_ToDo_ReminderType
		editor = map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType);
		if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
		{
			String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType);
			throw new WrongValueException(editor.getComponent(), msg);

		}else {
			i_Reminder.setJP_ToDo_ReminderType((String)editor.getValue());
		}


		//JP_ToDo_RemindTime
		editor = map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime);
		if(editor.getValue() == null)
		{
			String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime);
			throw new WrongValueException(editor.getComponent(), msg);

		}else {
			i_Reminder.setJP_ToDo_RemindTime((Timestamp)editor.getValue());
		}


		//BroadcastFrequency
		if(MToDoReminder.JP_TODO_REMINDERTYPE_SendMail.equals(i_Reminder.getJP_ToDo_ReminderType()))
		{
			i_Reminder.setBroadcastFrequency(null);

		}else {

			editor = map_Editor.get(MToDoReminder.COLUMNNAME_BroadcastFrequency);
			if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
			{
				String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MToDoReminder.COLUMNNAME_BroadcastFrequency);
				throw new WrongValueException(editor.getComponent(), msg);

			}else {
				i_Reminder.setBroadcastFrequency((String)editor.getValue());
			}
		}

		//MailFrequency
		if(MToDoReminder.COLUMNNAME_BroadcastFrequency.equals(i_Reminder.getJP_ToDo_ReminderType()))
		{
			i_Reminder.setJP_MailFrequency(null);

		}else {

			editor = map_Editor.get(MToDoReminder.COLUMNNAME_JP_MailFrequency);
			if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
			{
				String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MToDoReminder.COLUMNNAME_JP_MailFrequency);
				throw new WrongValueException(editor.getComponent(), msg);

			}else {
				i_Reminder.setJP_MailFrequency((String)editor.getValue());
			}
		}

		//Description
		editor = map_Editor.get(MToDoReminder.COLUMNNAME_Description);
		if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
		{
			String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MToDoReminder.COLUMNNAME_Description);
			throw new WrongValueException(editor.getComponent(), msg);

		}else {
			i_Reminder.setDescription((String)editor.getValue());
		}


		//URL
		editor = map_Editor.get(MToDoReminder.COLUMNNAME_URL);
		if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
		{
			i_Reminder.setURL(null);
		}else {
			i_Reminder.setURL(editor.getValue().toString());
		}


		if(p_IsPersonalToDo)
		{

			//Comments
			editor = map_Editor.get(MToDoReminder.COLUMNNAME_Comments);
			if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
			{
				i_Reminder.setComments(null);

			}else {
				i_Reminder.setComments((String)editor.getValue());
			}

			//IsConfirmed
			editor = map_Editor.get(MToDoReminder.COLUMNNAME_IsConfirmed);
			i_Reminder.setIsConfirmed(((boolean)editor.getValue()));

			//Set JP_Statistics_YesNo
			editor = map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_YesNo);
			if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
			{
				i_Reminder.setJP_Statistics_YesNo(null);
			}else {
				i_Reminder.setJP_Statistics_YesNo(((String)editor.getValue()));
			}

			//Set JP_Statistics_Choice
			editor = map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_Choice);
			if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
			{
				i_Reminder.setJP_Statistics_Choice(null);
			}else {
				i_Reminder.setJP_Statistics_Choice(((String)editor.getValue()));
			}

			//Set JP_Statistics_DateAndTime
			editor = map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_DateAndTime);
			if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
			{
				i_Reminder.setJP_Statistics_DateAndTime(null);
			}else {
				i_Reminder.setJP_Statistics_DateAndTime(((Timestamp)editor.getValue()));
			}

			//Set JP_Statistics_Number
			editor = map_Editor.get(MToDoReminder.COLUMNNAME_JP_Statistics_Number);
			if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
			{
				i_Reminder.setJP_Statistics_Number(null);
			}else {
				i_Reminder.setJP_Statistics_Number(((BigDecimal)editor.getValue()));
			}

		}else {

			//Set JP_Mandatory_Statistics_Info
			editor = map_Editor.get(MToDoTeamReminder.COLUMNNAME_JP_Mandatory_Statistics_Info);
			if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
			{
				i_Reminder.setJP_Mandatory_Statistics_Info(MToDoTeamReminder.JP_MANDATORY_STATISTICS_INFO_None);

			}else {
				i_Reminder.setJP_Mandatory_Statistics_Info(editor.getValue().toString());
			}
		}

		//Check BeforeSave()
		String msg = i_Reminder.beforeSavePreCheck(true);
		if(!Util.isEmpty(msg))
		{
			FDialog.error(0, this, msg);
			return false;
		}


		//Save
		if (i_Reminder.save())
		{
			if (log.isLoggable(Level.FINE)) log.fine("JP_ToDo_ID=" + p_iToDo.get_ID());

			p_IsDirty = false;
			updateControlParameter();
			updateEditorValue();
			updateEditorStatus();
			updateNorth();
			updateCenter();
		}
		else
		{
			FDialog.error(0, this, "SaveError", Msg.getMsg(ctx, "JP_UnexpectedError"));
			return false;
		}

		return true;
	}

	private boolean deleteReminder()
	{
		return i_Reminder.delete(false);
	}


	private boolean sendReminder()
	{
		if(!i_Reminder.isSentReminderJP() && !i_Reminder.isProcessed())
		{
			Timestamp now = Timestamp.valueOf(LocalDateTime.now());
			if(i_Reminder.get_ID() != 0 && i_Reminder.getJP_ToDo_RemindTime().compareTo(now) <= 0)
			{
				if(p_IsPersonalToDo)
				{
					MToDoReminder todoReminder = (MToDoReminder)i_Reminder;
					if(MToDoReminder.JP_TODO_REMINDERTYPE_SendMail.equals(todoReminder.getJP_ToDo_ReminderType()))
					{
						int AD_UserMail_ID = todoReminder.sendMailRemainder();

						if(AD_UserMail_ID > 0)
						{
							todoReminder.setAD_UserMail_ID(AD_UserMail_ID);
							todoReminder.setIsSentReminderJP(true);
							todoReminder.saveEx();

							FDialog.info(0, this, "SendMail", i_Reminder.getRemindMsg());
						}else{
							FDialog.error(0, this, "Error", i_Reminder.getRemindMsg());
						}

					}else if(MToDoReminder.JP_TODO_REMINDERTYPE_BroadcastMessage.equals(todoReminder.getJP_ToDo_ReminderType())) {

						int AD_BroadcastMessage_ID = todoReminder.sendMessageRemainder();

						if(AD_BroadcastMessage_ID > 0 )
						{
							todoReminder.setAD_BroadcastMessage_ID(AD_BroadcastMessage_ID);
							todoReminder.setIsSentReminderJP(true);
							if(MToDoReminder.BROADCASTFREQUENCY_JustOnce.equals(todoReminder.getBroadcastFrequency()))
								todoReminder.setProcessed(true);
							todoReminder.saveEx();

							FDialog.info(0, this, "MessageSent", i_Reminder.getRemindMsg());

						}else {

							FDialog.error(0, this, "Error", i_Reminder.getRemindMsg());
						}
					}

				}else {

					MToDoTeamReminder todoReminder = (MToDoTeamReminder)i_Reminder;
					if(todoReminder.createPersonalToDoRemainder())
					{
						FDialog.info(0, this, "JP_CreatePersonalToDoReminder", i_Reminder.getRemindMsg());
					}else {
						FDialog.error(0, this, "Error", i_Reminder.getRemindMsg());
					}

				}
			}
		}

		return true;
	}

    @Override
	public void onClose()
    {
		if(p_IsDirty)
		{

			FDialog.ask(0, null, "SaveChanges?", new Callback<Boolean>() {//Do you want to save changes?

				@Override
				public void onCallback(Boolean result)
				{
					if (result)
					{
						if(!saveReminder())
							return ;

					}else{
						;
					}

					sendReminder();

				   	if(p_TodoPopupWindow != null)
			    		p_TodoPopupWindow.hideBusyMask();
			    	else
			    		p_PersonalTodoListWindow.hideBusyMask();
				   	detach();
		        }

			});//FDialog.

		}else {

			int loginUser  = Env.getAD_User_ID(ctx);
			if(p_iToDo.getAD_User_ID() == loginUser || i_Reminder.getCreatedBy() == loginUser)
				sendReminder();

		   	if(p_TodoPopupWindow != null)
		   	{
	    		p_TodoPopupWindow.hideBusyMask();
		   	}

		   	if(p_PersonalTodoListWindow != null)
		   	{
	    		p_PersonalTodoListWindow.hideBusyMask();
		   	}
			super.onClose();
		}

	}

	@Override
	public void valueChange(ValueChangeEvent evt)
	{
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();

		if(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType.equals(name))
		{
			i_Reminder.setJP_ToDo_ReminderType(value.toString());
			updateCenter();

		}else if(MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate.equals(name) || MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate.equals(name)) {

			WDateEditor editor_RemindDate = (WDateEditor)map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate);
			Timestamp ts_RemindDate =(Timestamp)editor_RemindDate.getValue();
			if(ts_RemindDate == null)
			{
				String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime);
				throw new WrongValueException(editor_RemindDate.getComponent(), msg);
			}

			WTimeEditor editor_RemindTime = (WTimeEditor)map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime);
			Timestamp ts_RemindTime =(Timestamp)editor_RemindTime.getValue();
			if(ts_RemindTime == null)
			{
				String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime);
				throw new WrongValueException(editor_RemindDate.getComponent(), msg);
			}
			ts_RemindTime = Timestamp.valueOf(LocalDateTime.of(ts_RemindDate.toLocalDateTime().toLocalDate(), ts_RemindTime.toLocalDateTime().toLocalTime()));
			editor_RemindDate.setValue(ts_RemindTime);
			editor_RemindTime.setValue(ts_RemindTime);

		}else if(MToDoReminder.COLUMNNAME_URL.equals(name)) {

			map_Editor.get(MToDoReminder.COLUMNNAME_URL).setValue(value.toString());

		}

		p_IsDirty = true;
		updateNorth();
	}

	public int getJP_Team_ID()
	{
		Object value = map_Editor.get(MToDoTeamReminder.COLUMNNAME_JP_Team_ID).getValue();
		return value == null ? 0 : ((Integer)value).intValue();
	}

	public ToDoPopupWindow getToDoPopupWindow()
	{
		return p_TodoPopupWindow;
	}

}
