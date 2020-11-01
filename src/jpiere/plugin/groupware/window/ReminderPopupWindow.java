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
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.editor.WTimeEditor;
import org.adempiere.webui.editor.WYesNoEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.MColumn;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
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

import jpiere.plugin.groupware.model.I_ToDo;
import jpiere.plugin.groupware.model.I_ToDoReminder;
import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoReminder;
import jpiere.plugin.groupware.model.MToDoTeam;
import jpiere.plugin.groupware.model.MToDoTeamReminder;
import jpiere.plugin.groupware.util.GroupwareToDoUtil;

public class ReminderPopupWindow extends Window implements EventListener<Event> ,ValueChangeListener {


	private static final CLogger log = CLogger.getCLogger(ReminderPopupWindow.class);

	private Properties ctx = null;

	private ToDoPopupWindow p_TodoPopupWindow = null;
	private I_ToDo p_iToDo = null;
	private I_ToDoReminder i_Reminder = null;
	private int p_Reminder_ID = 0;
	private boolean p_IsPersonalToDo = false;
	private boolean p_IsNewRecord = true;
	private boolean p_IsUpdatable = false;
	private boolean p_haveParentTeamToDo = false;
	private boolean p_IsDirty = false;

	private Timestamp p_Now = null;


	private int p_Add_Hours = 5;
	private int p_Add_Mins = 15;

	private North north ;
	private Center center;
	private ConfirmPanel confirmPanel;

	private final static String BUTTON_NAME_UNDO = "REDO";
	private final static String BUTTON_NAME_SAVE = "SAVE";
	private final static String BUTTON_NAME_DELETE = "DELETE";

	private final static String BUTTON_NAME_ADD_HOURS = "ADD_HOURS";
	private final static String BUTTON_NAME_ADD_MINS = "ADD_MINS";

	/*** Web Components ***/
	// WEditors & Labels
	private Map<String, Label> map_Label = new HashMap<String, Label>();
	private Map<String, WEditor> map_Editor = new HashMap<String, WEditor>();


	//Buttons
	private Button undoBtn = null;
	private Button saveBtn = null;
	private Button deleteBtn = null;
	private Button addHoursBtn = null;
	private Button addMinsBtn = null;

	public ReminderPopupWindow(ToDoPopupWindow todoPopupWindow, I_ToDo i_ToDo, int reminder_ID)
	{
		super();
		ctx = Env.getCtx();
		this.setSclass("popup-dialog request-dialog");
		this.setBorder("normal");
		this.setShadow(true);
		this.setClosable(true);

		this.p_TodoPopupWindow = todoPopupWindow;
		this.p_iToDo = i_ToDo;
		this.p_Reminder_ID = reminder_ID;
		this.p_Now = Timestamp.valueOf(LocalDateTime.now());

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
			this.setTitle(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_ToDo_Reminder_ID));

		}else {

			p_IsPersonalToDo = false;
			i_Reminder = new MToDoTeamReminder(ctx, reminder_ID, null);
			if(reminder_ID == 0)
			{
				i_Reminder.setAD_Org_ID(i_ToDo.getAD_Org_ID());
				i_Reminder.setJP_ToDo_Team_ID(i_ToDo.get_ID());
				i_Reminder.setJP_ToDo_ReminderType(MToDoReminder.JP_TODO_REMINDERTYPE_SendMail);
			}
			this.setTitle(Msg.getElement(ctx, MToDoTeamReminder.COLUMNNAME_JP_ToDo_Team_Reminder_ID));
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

		if(p_IsNewRecord)
		{
			if(p_IsPersonalToDo)
			{
				ZKUpdateUtil.setWindowWidthX(this, 480);
				ZKUpdateUtil.setWindowHeightX(this, 400);
			} else {
				ZKUpdateUtil.setWindowWidthX(this, 480);
				ZKUpdateUtil.setWindowHeightX(this, 400);
			}

		}else {

			ZKUpdateUtil.setWindowWidthX(this, 480);
			ZKUpdateUtil.setWindowHeightX(this, 400);
		}


	}

	private void updateControlParameter()
	{

		if(p_Reminder_ID == 0)
		{
			p_IsNewRecord = true;
			p_IsUpdatable = true;

		}else {

			p_IsNewRecord = false;
			if(i_Reminder.isProcessed())
			{
				p_IsUpdatable = false;
			}else {
				p_IsUpdatable = true;
			}
		}
	}

	private void createLabelMap()
	{
		map_Label.put(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType)) );
		map_Label.put(MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime)) );
		map_Label.put(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime)) );
		map_Label.put(MToDoReminder.COLUMNNAME_BroadcastFrequency, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_BroadcastFrequency)) );
		map_Label.put(MToDoReminder.COLUMNNAME_Description, new Label(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_Description)) );
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
			map_Label.put(MToDoTeamReminder.COLUMNNAME_JP_Mandatory_Statistics_Info, new Label(Msg.getElement(ctx, MToDoTeamReminder.COLUMNNAME_JP_Mandatory_Statistics_Info)) );
		}
	}

	private void createEditorMap()
	{
		//*** JP_ToDo_ReminderType ***//
		MLookup lookup_JP_ToDo_ReminderType = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDoReminder.Table_Name,  MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType),  DisplayType.List);
		WTableDirEditor editor_JP_ToDo_ReminderType = new WTableDirEditor(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType, true, p_haveParentTeamToDo? true : !p_IsUpdatable, true, lookup_JP_ToDo_ReminderType);
		editor_JP_ToDo_ReminderType.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_ReminderType.getComponent(), "true");
		map_Editor.put(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType, editor_JP_ToDo_ReminderType);

		//*** JP_ToDo_RemindDate ***//
		WDateEditor editor_JP_ToDo_RemindDate = new WDateEditor(MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate, false, p_haveParentTeamToDo? true : !p_IsUpdatable, true, null);
		editor_JP_ToDo_RemindDate.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_RemindDate.getComponent(), "true");
		map_Editor.put(MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate, editor_JP_ToDo_RemindDate);

		//*** JP_ToDo_RemindTime ***//
		WTimeEditor editor_JP_ToDo_RemindTime = new WTimeEditor(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime, false, p_haveParentTeamToDo? true : !p_IsUpdatable, true, null);
		editor_JP_ToDo_RemindTime.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_RemindTime.getComponent(), "true");
		Timebox reminderTimebox = editor_JP_ToDo_RemindTime.getComponent();
		reminderTimebox.setFormat("HH:mm");
		map_Editor.put(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime, editor_JP_ToDo_RemindTime);

		//*** BroadcastFrequency ***//
		MLookup lookup_BroadcastFrequency = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDoReminder.Table_Name,  MToDoReminder.COLUMNNAME_BroadcastFrequency),  DisplayType.List);
		WTableDirEditor editor_BroadcastFrequency = new WTableDirEditor(MToDoReminder.COLUMNNAME_BroadcastFrequency, true, p_haveParentTeamToDo? true : !p_IsUpdatable, true, lookup_BroadcastFrequency);
		editor_BroadcastFrequency.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_BroadcastFrequency.getComponent(), "true");
		map_Editor.put(MToDoReminder.COLUMNNAME_BroadcastFrequency, editor_BroadcastFrequency);


		//*** Description ***//
		WStringEditor editor_Description = new WStringEditor(MToDoReminder.COLUMNNAME_Description, true, p_haveParentTeamToDo? true : !p_IsUpdatable, true, 30, 30, "", null);
		editor_Description.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_Description.getComponent(), "true");
		editor_Description.getComponent().setRows(5);
		map_Editor.put(MToDoReminder.COLUMNNAME_Description, editor_Description);


		if(p_IsPersonalToDo)
		{
			WStringEditor editor_Comments = new WStringEditor(MToDoReminder.COLUMNNAME_Comments, true, p_haveParentTeamToDo? true : !p_IsUpdatable, true, 30, 30, "", null);
			editor_Comments.addValueChangeListener(this);
			ZKUpdateUtil.setHflex(editor_Comments.getComponent(), "true");
			editor_Comments.getComponent().setRows(3);
			map_Editor.put(MToDoReminder.COLUMNNAME_Comments, editor_Comments);
		}

		//*** IsSentReminderJP ***//
		WYesNoEditor editor_IsSentReminderJP = new WYesNoEditor(MToDoReminder.COLUMNNAME_IsSentReminderJP, Msg.getElement(ctx, MToDoReminder.COLUMNNAME_IsSentReminderJP), null, true, true, true);
		editor_IsSentReminderJP.addValueChangeListener(this);
		map_Editor.put(MToDoReminder.COLUMNNAME_IsSentReminderJP, editor_IsSentReminderJP);


		//*** IsConfirmed ***//
		boolean isReadonlyConfirmed = true;
		int login_User_ID = Env.getAD_User_ID(ctx);
		if(p_iToDo.getAD_User_ID() != login_User_ID)
		{
			isReadonlyConfirmed = true;

		}else {

			if(MToDoReminder.JP_TODO_REMINDERTYPE_BroadcastMessage.equals(i_Reminder.getJP_ToDo_ReminderType()))
			{
				isReadonlyConfirmed = true;
			}else {

				if(i_Reminder.isConfirmed())
				{
					isReadonlyConfirmed = true;
				}else if (!i_Reminder.isSentReminderJP()) {
					isReadonlyConfirmed = true;
				}else {
					isReadonlyConfirmed = false;
				}
			}
		}
		WYesNoEditor editor_IsConfirmed = new WYesNoEditor(MToDoReminder.COLUMNNAME_IsConfirmed, Msg.getElement(ctx, MToDoReminder.COLUMNNAME_IsConfirmed), null, true, isReadonlyConfirmed, true);
		editor_IsConfirmed.addValueChangeListener(this);
		map_Editor.put(MToDoReminder.COLUMNNAME_IsConfirmed, editor_IsConfirmed);


		//*** JP_ToDo_RemindTime ***//
		WDatetimeEditor editor_JP_Confirmed = new WDatetimeEditor(MToDoReminder.COLUMNNAME_JP_Confirmed, false, p_haveParentTeamToDo? true : true, true, null);
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
		;
	}

	private void updateEditorValue()
	{
		if(p_IsNewRecord)
		{
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType).setValue(i_Reminder.getJP_ToDo_ReminderType());
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate).setValue(Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN)));
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime).setValue(Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.now())));
			map_Editor.get(MToDoReminder.COLUMNNAME_BroadcastFrequency).setValue(null);

			map_Editor.get(MToDoReminder.COLUMNNAME_IsSentReminderJP).setValue("N");
			map_Editor.get(MToDoReminder.COLUMNNAME_IsConfirmed).setValue("N");
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_Confirmed).setValue(null);

		}else {

			map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType).setValue(i_Reminder.getJP_ToDo_ReminderType());
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindDate).setValue(i_Reminder.getJP_ToDo_RemindTime());
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime).setValue(i_Reminder.getJP_ToDo_RemindTime());
			map_Editor.get(MToDoReminder.COLUMNNAME_BroadcastFrequency).setValue(i_Reminder.getBroadcastFrequency());
			map_Editor.get(MToDoReminder.COLUMNNAME_Description).setValue(i_Reminder.getDescription());

			map_Editor.get(MToDoReminder.COLUMNNAME_IsSentReminderJP).setValue(i_Reminder.isSentReminderJP());
			map_Editor.get(MToDoReminder.COLUMNNAME_IsConfirmed).setValue(i_Reminder.isConfirmed());
			map_Editor.get(MToDoReminder.COLUMNNAME_JP_Confirmed).setValue(i_Reminder.getJP_Confirmed());


			if(p_IsPersonalToDo)
			{
				map_Editor.get(MToDo.COLUMNNAME_Comments).setValue(i_Reminder.getComments());
				map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_YesNo).setValue(i_Reminder.getJP_Statistics_YesNo());
				map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_Choice).setValue(i_Reminder.getJP_Statistics_Choice());
				map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_DateAndTime).setValue(i_Reminder.getJP_Statistics_DateAndTime());
				map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_Number).setValue(i_Reminder.getJP_Statistics_Number());
			}else {

				map_Editor.get(MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info).setValue(i_Reminder.getJP_Mandatory_Statistics_Info());
			}

		}
	}

	private North updateNorth()//TODO
	{
		if(north.getFirstChild() != null)
			north.getFirstChild().detach();

		if(p_IsNewRecord)
			return north;

		Hlayout hlyaout = new Hlayout();
		hlyaout.setStyle("margin:2px 2px 2px 2px; padding:2px 2px 2px 2px;");// border: solid 1px #dddddd;
		north.appendChild(hlyaout);

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
		deleteBtn.setEnabled(p_IsUpdatable);
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
			addHoursBtn.setVisible(p_haveParentTeamToDo? false : p_IsUpdatable);
			addHoursBtn.addEventListener(Events.ON_CLICK, this);
			ZKUpdateUtil.setHflex(addHoursBtn, "true");
		}

		if(addMinsBtn == null)
		{
			addMinsBtn = new Button();
			addMinsBtn.setClass("btn-small");
			addMinsBtn.setName(BUTTON_NAME_ADD_MINS);
			addMinsBtn.setLabel("+"+p_Add_Mins+Msg.getMsg(ctx, "JP_Mins"));
			addMinsBtn.setVisible(p_haveParentTeamToDo? false : p_IsUpdatable);
			addMinsBtn.addEventListener(Events.ON_CLICK, this);
			ZKUpdateUtil.setHflex(addMinsBtn, "true");
		}

		Div centerContent = new Div();
		center.appendChild(centerContent);
		ZKUpdateUtil.setVflex(center, "min");

		Grid grid = GridFactory.newGridLayout();
		ZKUpdateUtil.setVflex(grid, "min");
		ZKUpdateUtil.setHflex(grid, "1");
		centerContent.appendChild(grid);

		Rows rows = grid.newRows();

		//*** JP_ToDo_ReminderType ***//
		Row row = rows.newRow();
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
   		}

		//*** Description ***//
		row = rows.newRow();
		row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoReminder.COLUMNNAME_Description), false),2);
		row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_Description).getComponent(),4);


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
			row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_IsConfirmed).getComponent(),2);

			row = rows.newRow();
			row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoReminder.COLUMNNAME_JP_Confirmed), true),2);
			row.appendCellChild(map_Editor.get(MToDoReminder.COLUMNNAME_JP_Confirmed).getComponent(),4);
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
				row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_Statistics_YesNo), true),2);
				row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_YesNo).getComponent(),4);
			}

			//*** JP_Statistics_Choice ***//
			if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_Choice.equals(JP_Mandatory_Statistics_Info))
			{
				row = statisticsInfo_rows.newRow();
				row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_Statistics_Choice), true),2);
				row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_Choice).getComponent(),4);
			}

			//*** JP_Statistics_DateAndTime ***//
			if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_DateAndTime.equals(JP_Mandatory_Statistics_Info))
			{
				row = statisticsInfo_rows.newRow();
				row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_Statistics_DateAndTime), true),2);
				row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_DateAndTime).getComponent(),4);
			}

			//*** JP_Statistics_Number ***//
			if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_Number.equals(JP_Mandatory_Statistics_Info))
			{
				row = statisticsInfo_rows.newRow();
				row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_Statistics_Number), true),2);
				row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_Number).getComponent(),4);
			}

		}else {

			row = statisticsInfo_rows.newRow();
			row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info), false),3);
			row.appendCellChild(map_Editor.get(MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info).getComponent(),3);

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
					}

				}else if(btn== confirmPanel.getButton(ConfirmPanel.A_CANCEL)) {

					this.onClose();

				}

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
			}
		}

	}

	private boolean saveReminder()//TODO
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
				i_Reminder.setIsActive(db_Reminder.isActive());
				i_Reminder.setIsSentReminderJP(db_Reminder.isSentReminderJP());
				i_Reminder.setProcessed(db_Reminder.isProcessed());
				i_Reminder.setUpdated(db_Reminder.getUpdated());

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


		//JP_ToDo_ReminderType
		WEditor editor = map_Editor.get(MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType);
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

		//Description
		editor = map_Editor.get(MToDoReminder.COLUMNNAME_Description);
		if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
		{
			String msg = Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MToDoReminder.COLUMNNAME_Description);
			throw new WrongValueException(editor.getComponent(), msg);

		}else {
			i_Reminder.setDescription((String)editor.getValue());
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

			if(!i_Reminder.isSentReminderJP())
			{
				if(i_Reminder.getJP_ToDo_RemindTime().compareTo(p_Now) <= 0)
				{
					if(MToDoReminder.JP_TODO_REMINDERTYPE_SendMail.equals(i_Reminder.getJP_ToDo_ReminderType()))
					{
						i_Reminder.sendMailRemainder();
					}else if(MToDoReminder.JP_TODO_REMINDERTYPE_BroadcastMessage.equals(i_Reminder.getJP_ToDo_ReminderType())) {
						i_Reminder.sendMessageRemainder();
					}
				}

			}
			updateControlParameter();
			updateEditorValue();
			updateEditorStatus();
			updateNorth();
			updateCenter();
		}
		else
		{
			FDialog.error(0, this, Msg.getMsg(ctx, "SaveError") + " : "+ Msg.getMsg(ctx, "JP_UnexpectedError"));
			return false;
		}

		return true;
	}

    @Override
	public void onClose()
    {
    	p_TodoPopupWindow.hideBusyMask();
		super.onClose();
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

		}
	}

}
