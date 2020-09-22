package jpiere.plugin.groupware.form;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WDatetimeEditor;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WNumberEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.editor.WTimeEditor;
import org.adempiere.webui.editor.WYesNoEditor;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.MColumn;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Vlayout;

import jpiere.plugin.groupware.model.I_ToDo;
import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoCategory;
import jpiere.plugin.groupware.model.MToDoTeam;
import jpiere.plugin.groupware.util.GroupwareToDoUtil;


/**
*
* JPIERE-0476: ToDo Calendar Event Popup
*
* h.hagiwara
*
*/
public class CalendarEventPopup extends Popup implements EventListener<Event>{

	private Properties ctx = Env.getCtx();

	/*** Web Components ***/
	// WEditors & Labels
	private Map<String, Label> map_Label = new HashMap<String, Label>();
	private Map<String, WEditor> map_Editor = new HashMap<String, WEditor>();

	//Buttons
	private Button detachPopupBtn = null;
	private Button zoomPersonalToDoBtn = null;
	private Button zoomTeamToDoBtn = null;


	private final static String DETACH_POPUP = "DETACH";
	private final static String ZOOM_PERSONALTODO = "ZOOM_P";
	private final static String ZOOM_TEAMTODO = "ZOOM_T";

	public CalendarEventPopup()
	{
		createLabelMap();
		createEditorMap();
		createButton();
	}

	public CalendarEventPopup(boolean visible)
	{
		super(visible);
		createLabelMap();
		createEditorMap();
		createButton();
	}

	private void createLabelMap()
	{
		map_Label.put(MToDo.COLUMNNAME_AD_User_ID, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_AD_User_ID)) );
		map_Label.put(MToDo.COLUMNNAME_JP_ToDo_Type, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Type)) );
		map_Label.put(MToDo.COLUMNNAME_JP_ToDo_Category_ID, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Category_ID)) );
		map_Label.put(MToDo.COLUMNNAME_Name, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_Name)) );
		map_Label.put(MToDo.COLUMNNAME_Description, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_Description)) );
		map_Label.put(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartDate, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_ScheduledStartDate)) );
		map_Label.put(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime)) );
		map_Label.put(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndDate, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_ScheduledEndDate)) );
		map_Label.put(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime)) );
		map_Label.put(MToDo.COLUMNNAME_JP_ToDo_Status, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Status)) );
		map_Label.put(MToDo.COLUMNNAME_IsOpenToDoJP, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_IsOpenToDoJP)) );

		map_Label.put(MToDo.COLUMNNAME_Comments, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_Comments)) );
		map_Label.put(MToDo.COLUMNNAME_JP_Statistics_YesNo, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_YesNo)));
		map_Label.put(MToDo.COLUMNNAME_JP_Statistics_Choice, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_Choice)));
		map_Label.put(MToDo.COLUMNNAME_JP_Statistics_DateAndTime, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_DateAndTime)));
		map_Label.put(MToDo.COLUMNNAME_JP_Statistics_Number, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_Number)));

		map_Label.put(MToDoTeam.COLUMNNAME_JP_Team_ID, new Label(Msg.getElement(ctx, MToDoTeam.COLUMNNAME_JP_Team_ID)) );
		map_Label.put(MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info, new Label(Msg.getElement(ctx, MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info)) );

	}

	private void createEditorMap()
	{
		//*** AD_User_ID ***//
		MLookup lookup_AD_User_ID = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_AD_User_ID),  DisplayType.Search);
		WSearchEditor Editor_AD_User_ID = new WSearchEditor(lookup_AD_User_ID, Msg.getElement(ctx, MToDo.COLUMNNAME_AD_User_ID), null, true, true, false);
		ZKUpdateUtil.setHflex(Editor_AD_User_ID.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_AD_User_ID, Editor_AD_User_ID);


		//*** JP_ToDo_Type ***//
		MLookup lookup_JP_ToDo_Type = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name,  MToDo.COLUMNNAME_JP_ToDo_Type),  DisplayType.List);
		//WTableDirEditor editor_JP_ToDo_Type = new WTableDirEditor(lookup_JP_ToDo_Type, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Type), null, true, p_IsTeamToDo? true : !p_IsUpdatable, true);
		WTableDirEditor editor_JP_ToDo_Type = new WTableDirEditor(MToDo.COLUMNNAME_JP_ToDo_Type, true, true, true, lookup_JP_ToDo_Type);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_Type.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_ToDo_Type, editor_JP_ToDo_Type);


		//*** JP_ToDo_Category_ID ***//
		MLookup lookup_JP_ToDo_Category_ID = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_ToDo_Category_ID),  DisplayType.Search);
		WSearchEditor editor_JP_ToDo_Category_ID = new WSearchEditor(lookup_JP_ToDo_Category_ID, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Category_ID), null, false, true, true);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_Category_ID.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_ToDo_Category_ID, editor_JP_ToDo_Category_ID);


		//*** JP_Team_ID ***//
		MLookup lookup_JP_Team_ID = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDoTeam.Table_Name, MToDoTeam.COLUMNNAME_JP_Team_ID),  DisplayType.Search);
		WSearchEditor editor_JP_Team_ID = new WSearchEditor(lookup_JP_Team_ID, Msg.getElement(ctx, MToDoTeam.COLUMNNAME_JP_Team_ID), null, false, true, true);
		ZKUpdateUtil.setHflex(editor_JP_Team_ID.getComponent(), "true");
		map_Editor.put(MToDoTeam.COLUMNNAME_JP_Team_ID, editor_JP_Team_ID);


		//*** Name ***//
		WStringEditor editor_Name = new WStringEditor(MToDo.COLUMNNAME_Name, true, true, true, 30, 30, "", null);
		ZKUpdateUtil.setHflex(editor_Name.getComponent(), "true");
		editor_Name.getComponent().setRows(2);
		map_Editor.put(MToDo.COLUMNNAME_Name, editor_Name);


		//*** Description ***//
		WStringEditor editor_Description = new WStringEditor(MToDo.COLUMNNAME_Description, true, true, true, 30, 30, "", null);
		ZKUpdateUtil.setHflex(editor_Description.getComponent(), "true");
		editor_Description.getComponent().setRows(3);
		map_Editor.put(MToDo.COLUMNNAME_Description, editor_Description);


		//*** Comments ***//
		WStringEditor editor_Comments = new WStringEditor(MToDo.COLUMNNAME_Comments, false, true, false, 30, 30, "", null);
		ZKUpdateUtil.setHflex(editor_Comments.getComponent(), "true");
		editor_Comments.getComponent().setRows(3);
		map_Editor.put(MToDo.COLUMNNAME_Comments, editor_Comments);


		//*** JP_ToDo_ScheduledStartDate ***//
		WDateEditor editor_JP_ToDo_ScheduledStartDate = new WDateEditor(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartDate, false, true, false, null);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_ScheduledStartDate.getComponent(), "true");
		map_Editor.put("JP_ToDo_ScheduledStartDate", editor_JP_ToDo_ScheduledStartDate);

		//*** IsStarDateAllDayJP ***//
		WYesNoEditor editor_IsStartDateAllDayJP = new WYesNoEditor(MToDo.COLUMNNAME_IsStartDateAllDayJP, Msg.getElement(ctx, MToDo.COLUMNNAME_IsStartDateAllDayJP), null, true, true, false);
		map_Editor.put(MToDo.COLUMNNAME_IsStartDateAllDayJP, editor_IsStartDateAllDayJP);

		//*** JP_ToDo_ScheduledStartTime ***//
		WTimeEditor editor_JP_ToDo_ScheduledStartTime = new WTimeEditor(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime, false,true, false, null);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_ScheduledStartTime.getComponent(), "true");
		Timebox startTimebox = editor_JP_ToDo_ScheduledStartTime.getComponent();
		startTimebox.setFormat("HH:mm");
		startTimebox.setButtonVisible(false);
		map_Editor.put(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime, editor_JP_ToDo_ScheduledStartTime);


		//*** JP_ToDo_ScheduledEndDate ***//
		WDateEditor editor_JP_ToDo_ScheduledEndDate = new WDateEditor(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndDate, false, true, false, null);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_ScheduledEndDate.getComponent(), "true");
		map_Editor.put("JP_ToDo_ScheduledEndDate", editor_JP_ToDo_ScheduledEndDate);

		//*** IsEndDateAllDayJP ***//
		WYesNoEditor editor_IsEndDateAllDayJP = new WYesNoEditor(MToDo.COLUMNNAME_IsEndDateAllDayJP, Msg.getElement(ctx, MToDo.COLUMNNAME_IsEndDateAllDayJP), null, true, true, false);
		map_Editor.put(MToDo.COLUMNNAME_IsEndDateAllDayJP, editor_IsEndDateAllDayJP);

		//*** JP_ToDo_ScheduledEndTime ***//
		WTimeEditor editor_JP_ToDo_ScheduledEndTime = new WTimeEditor(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime, false, true , false, null);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_ScheduledEndTime.getComponent(), "true");
		Timebox endTimebox = editor_JP_ToDo_ScheduledEndTime.getComponent();
		endTimebox.setFormat("HH:mm");
		endTimebox.setButtonVisible(false);
		map_Editor.put(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime, editor_JP_ToDo_ScheduledEndTime);


		//*** JP_ToDo_Status ***//
		MLookup lookup_JP_ToDo_Status = MLookupFactory.get(ctx, 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_ToDo_Status),  DisplayType.List);
		WTableDirEditor editor_JP_ToDo_Status = new WTableDirEditor(lookup_JP_ToDo_Status, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Status), null, true, true, true);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_Status.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_ToDo_Status, editor_JP_ToDo_Status);


		//*** IsOpenToDoJP ***//
		WYesNoEditor editor_IsOpenToDoJP = new WYesNoEditor(MToDo.COLUMNNAME_IsOpenToDoJP, Msg.getElement(ctx, MToDo.COLUMNNAME_IsOpenToDoJP), null, true, true, true);
		map_Editor.put(MToDo.COLUMNNAME_IsOpenToDoJP, editor_IsOpenToDoJP);


		//Statistics Info

		//*** JP_Statistics_YesNo  ***//
		MLookup lookup_JP_Statistics_YesNo = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_Statistics_YesNo),  DisplayType.List);
		WTableDirEditor editor_JP_Statistics_YesNo = new WTableDirEditor(lookup_JP_Statistics_YesNo, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_YesNo), null, false, true, true);
		ZKUpdateUtil.setHflex(editor_JP_Statistics_YesNo.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_Statistics_YesNo, editor_JP_Statistics_YesNo);

		//*** JP_Statistics_Choice ***//
		MLookup lookup_JP_Statistics_Choice = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_Statistics_Choice),  DisplayType.List);
		WTableDirEditor editor_JP_Statistics_Choice = new WTableDirEditor(lookup_JP_Statistics_Choice, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_Choice), null, false, true, true);
		ZKUpdateUtil.setHflex(editor_JP_Statistics_Choice.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_Statistics_Choice, editor_JP_Statistics_Choice);

		//*** JP_Statistics_DateAndTime ***//
		WDatetimeEditor editor_JP_Statistics_DateAndTime = new WDatetimeEditor(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_DateAndTime), null, false, true, true);
		ZKUpdateUtil.setHflex((HtmlBasedComponent)editor_JP_Statistics_DateAndTime.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_Statistics_DateAndTime, editor_JP_Statistics_DateAndTime);

		//*** JP_Statistics_Number ***//
		WNumberEditor editor_JP_Statistics_Number = new WNumberEditor(MToDo.COLUMNNAME_JP_Statistics_Number, false, true, true, DisplayType.Number, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_Number));
		ZKUpdateUtil.setHflex(editor_JP_Statistics_Number.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_Statistics_Number, editor_JP_Statistics_Number);


		//*** JP_Mandatory_Statistics_Info ***//
		MLookup lookup_JP_Mandatory_Statistics_Info = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDoTeam.Table_Name,  MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info),  DisplayType.List);
		WTableDirEditor editor_JP_Mandatory_Statistics_Info= new WTableDirEditor(MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info, true, true, true, lookup_JP_Mandatory_Statistics_Info);
		ZKUpdateUtil.setHflex(editor_JP_Mandatory_Statistics_Info.getComponent(), "true");
		map_Editor.put(MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info, editor_JP_Mandatory_Statistics_Info);


	}

	private void createButton()
	{
		//Delete popup
		detachPopupBtn = new Button();
		detachPopupBtn.setImage(ThemeManager.getThemeResource("images/X8.png"));
		detachPopupBtn.setClass("btn-small");
		detachPopupBtn.setName(DETACH_POPUP);
		detachPopupBtn.addEventListener(Events.ON_CLICK, this);

		//Personal ToDo Zoom Button
		zoomPersonalToDoBtn = new Button();
		zoomPersonalToDoBtn.setImage(ThemeManager.getThemeResource("images/Zoom16.png"));
		zoomPersonalToDoBtn.setClass("btn-small");
		zoomPersonalToDoBtn.setName(ZOOM_PERSONALTODO);
		zoomPersonalToDoBtn.setTooltiptext(Msg.getMsg(ctx, "JP_Zoom_To_PersonalToDo"));
		zoomPersonalToDoBtn.addEventListener(Events.ON_CLICK, this);

		//Team ToDo Zoom Button
		zoomTeamToDoBtn = new Button();
		zoomTeamToDoBtn.setImage(ThemeManager.getThemeResource("images/ZoomAcross16.png"));
		zoomTeamToDoBtn.setClass("btn-small");
		zoomTeamToDoBtn.setName(ZOOM_TEAMTODO);
		zoomTeamToDoBtn.setTooltiptext(Msg.getMsg(ctx, "JP_Zoom_To_TeamToDo"));
		zoomTeamToDoBtn.addEventListener(Events.ON_CLICK, this);

	}


	private void createPopup(ToDoCalendarEvent event)
	{
		if(this.getFirstChild() != null)
		{
			this.getFirstChild().detach();
		}else {

			ZKUpdateUtil.setVflex(this, "min");
			ZKUpdateUtil.setHflex(this, "min");

		}

		String popupColor = null;
		if(event == null)
		{
			if(p_I_ToDo.getJP_ToDo_Category_ID() == 0)
			{
				popupColor = GroupwareToDoUtil.DEFAULT_COLOR1;

			}else {

				MToDoCategory todoCategory = MToDoCategory.get(ctx, p_I_ToDo.getJP_ToDo_Category_ID());
				if(Util.isEmpty(todoCategory.getJP_ColorPicker()))
				{
					popupColor = GroupwareToDoUtil.DEFAULT_COLOR1;
				}else {
					popupColor = todoCategory.getJP_ColorPicker();
				}

			}

		}else {

			if(event.getHeaderColor() == null)
			{
				popupColor = GroupwareToDoUtil.DEFAULT_COLOR1;
			}else {
				popupColor = event.getHeaderColor() ;
			}
		}

		this.setStyle("border: 2px solid " + popupColor + ";");

		Vlayout popupContent = new Vlayout();
		this.appendChild(popupContent);
		ZKUpdateUtil.setVflex(popupContent, "min");
		ZKUpdateUtil.setHflex(popupContent, "min");

		Hlayout hlyaout = new Hlayout();
		hlyaout.setStyle("padding:2px 2px 2px 2px; background-color:" + popupColor + ";");
		ZKUpdateUtil.setVflex(hlyaout, "100%");
		ZKUpdateUtil.setHflex(hlyaout, "100%");
		popupContent.appendChild(hlyaout);

		hlyaout.appendChild(detachPopupBtn);

		String header = null;
		String name = GroupwareToDoUtil.trimName(MUser.getNameOfUser(p_I_ToDo.getCreatedBy()));
		if(p_IsPersonalToDo)
		{
			if(p_I_ToDo.getParent_Team_ToDo_ID() == 0)
			{
				header = "[" + Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_ID) + "] "
						+ Msg.getElement(Env.getCtx(),MToDo.COLUMNNAME_CreatedBy)
						+ ":" + name;

			}else {

				header = "[" + Msg.getElement(ctx,MToDo.COLUMNNAME_JP_ToDo_Team_ID) + "] "
						+ Msg.getElement(Env.getCtx(),MToDo.COLUMNNAME_CreatedBy)
						+ ":" + name;
			}

		}else {

			header = "[" + Msg.getElement(ctx,MToDo.COLUMNNAME_JP_ToDo_Team_ID) + "] "
					+ Msg.getElement(Env.getCtx(),MToDo.COLUMNNAME_CreatedBy)
					+ ":" + name;

		}

		Label label_header = new Label(header);
		label_header.setStyle("color:#ffffff; white-space: nowrap; ");//white-space: nowrap;text-overflow: ellipsis;
		hlyaout.appendChild(GroupwareToDoUtil.createLabelDiv(null, label_header,true));


		hlyaout = new Hlayout();
		popupContent.appendChild(hlyaout);

		hlyaout.appendChild(GroupwareToDoUtil.getDividingLine());
		hlyaout.appendChild(zoomPersonalToDoBtn);
		hlyaout.appendChild(zoomTeamToDoBtn);
		hlyaout.appendChild(GroupwareToDoUtil.getDividingLine());

		Grid grid = GridFactory.newGridLayout();
		ZKUpdateUtil.setVflex(grid, "min");
		ZKUpdateUtil.setHflex(grid, "min");
		popupContent.appendChild(grid);

		Rows rows = grid.newRows();

		//*** AD_User_ID ***//
		Row row = rows.newRow();
		rows.appendChild(row);
		row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDo.COLUMNNAME_AD_User_ID), true),2);
		row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_AD_User_ID).getComponent(),4);


		//*** JP_ToDo_Type ***//
		row = rows.newRow();
		row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_Type), true),2);
		row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Type).getComponent(),4);


		//*** JP_ToDo_Category_ID ***//
		row = rows.newRow();
		row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_Category_ID), false),2);
		row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Category_ID).getComponent(),4);
		//map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Category_ID).showMenu();


		//*** Name ***//
		row = rows.newRow();
		row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDo.COLUMNNAME_Name), true),2);
		row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_Name).getComponent(),4);


		//*** Description ***//
		row = rows.newRow();
		row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDo.COLUMNNAME_Description), false),2);
		row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_Description).getComponent(),4);


		if(p_IsPersonalToDo)
		{
			//*** Comments ***//
			row = rows.newRow();
			row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDo.COLUMNNAME_Comments), false),2);
			row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_Comments).getComponent(),4);

		}else {

			//*** Team ***//
			row = rows.newRow();
			row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDoTeam.COLUMNNAME_JP_Team_ID), false),2);
			row.appendCellChild(map_Editor.get(MToDoTeam.COLUMNNAME_JP_Team_ID).getComponent(),4);
		}

		//*** JP_ToDo_ScheduledStartDate & Time ***//
		if(MToDo.JP_TODO_TYPE_Schedule.equals(p_JP_ToDo_Type))
		{
			row = rows.newRow();
			row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartDate), true),2);
			row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartDate).getComponent(),2);
			row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_IsStartDateAllDayJP).getComponent(),2);
			if(p_I_ToDo.isStartDateAllDayJP())
			{
				;//Noting to do
			}else {
				row = rows.newRow();
				row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime), true),2);
				Timebox comp = (Timebox)map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime).getComponent();
				row.appendCellChild(comp,2);
			}

		}

		//*** JP_ToDo_ScheduledEndDate & Time ***//
		if(MToDo.JP_TODO_TYPE_Schedule.equals(p_JP_ToDo_Type) || MToDo.JP_TODO_TYPE_Task.equals(p_JP_ToDo_Type) )
		{
			row = rows.newRow();
			row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndDate), true),2);
			row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndDate).getComponent(),2);
			row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_IsEndDateAllDayJP).getComponent(),2);
			if(p_I_ToDo.isEndDateAllDayJP())
			{
				;//Noting to do
			}else {
				row = rows.newRow();
				row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime), true),2);
				Timebox comp = (Timebox)map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime).getComponent();
				row.appendCellChild(comp,2);
			}
		}

		//*** JP_ToDo_Status ***//
		row = rows.newRow();
		row.appendCellChild(GroupwareToDoUtil.createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_Status), true),2);
		row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Status).getComponent(),2);


		//*** IsOpenToDoJP ***//
		Div div_IsOpenToDoJP = new Div();
		div_IsOpenToDoJP.appendChild(map_Editor.get(MToDo.COLUMNNAME_IsOpenToDoJP).getComponent());
		row.appendCellChild(div_IsOpenToDoJP,2);


		/********************************************************************************************
		 * Statistics Info
		 ********************************************************************************************/

//		row = rows.newRow();
//		Groupbox statisticsInfo_GroupBox = new Groupbox();
//		statisticsInfo_GroupBox.setOpen(true);
//		row.appendCellChild(statisticsInfo_GroupBox,6);

//		String caption = Msg.getMsg(Env.getCtx(),"JP_StatisticsInfo");
//		if(p_IsPersonalToDo && p_ParentTeamToDo != null)
//		{
//			if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_None.equals(p_ParentTeamToDo.getJP_Mandatory_Statistics_Info()))
//			{
//
//			}else if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_YesNo.equals(p_ParentTeamToDo.getJP_Mandatory_Statistics_Info())) {
//
//				caption = caption + " [" + Msg.getElement(ctx, "IsMandatory") + ":" + map_Label.get(MToDo.COLUMNNAME_JP_Statistics_YesNo).getValue() + "]";
//
//			}else if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_Choice.equals(p_ParentTeamToDo.getJP_Mandatory_Statistics_Info())) {
//
//				caption = caption + " [" + Msg.getElement(ctx, "IsMandatory") + ":" + map_Label.get(MToDo.COLUMNNAME_JP_Statistics_Choice).getValue() + "]";
//
//			}else if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_DateAndTime.equals(p_ParentTeamToDo.getJP_Mandatory_Statistics_Info())) {
//
//				caption = caption + " [" + Msg.getElement(ctx, "IsMandatory") + ":" + map_Label.get(MToDo.COLUMNNAME_JP_Statistics_DateAndTime).getValue() + "]";
//
//			}else if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_Number.equals(p_ParentTeamToDo.getJP_Mandatory_Statistics_Info())) {
//
//				caption = caption + " [" + Msg.getElement(ctx, "IsMandatory") + ":" + map_Label.get(MToDo.COLUMNNAME_JP_Statistics_Number).getValue() + "]";
//
//			}else {
//				;
//			}
//		}
//
//		statisticsInfo_GroupBox.appendChild(new Caption(caption));
//		Grid statisticsInfo_Grid  = GridFactory.newGridLayout();
//		statisticsInfo_Grid.setStyle("background-color: #E9F0FF");
//		statisticsInfo_Grid.setStyle("border: none");
//		statisticsInfo_GroupBox.appendChild(statisticsInfo_Grid);
//
//		Rows statisticsInfo_rows = statisticsInfo_Grid.newRows();
//
//		if(p_IsPersonalToDo)
//		{
//			String JP_Mandatory_Statistics_Info = null;
//			if(p_ParentTeamToDo!=null)
//			{
//				JP_Mandatory_Statistics_Info = p_ParentTeamToDo.getJP_Mandatory_Statistics_Info();
//			}
//
//			//*** JP_Statistics_YesNo  ***//
//			row = statisticsInfo_rows.newRow();
//			if(p_haveParentTeamToDo && MToDoTeam.JP_MANDATORY_STATISTICS_INFO_YesNo.equals(JP_Mandatory_Statistics_Info))
//			{
//				row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_Statistics_YesNo), true),2);
//			}else {
//				row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_Statistics_YesNo), false),2);
//			}
//			row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_YesNo).getComponent(),4);
//
//
//			//*** JP_Statistics_Choice ***//
//			row = statisticsInfo_rows.newRow();
//			if(p_haveParentTeamToDo && MToDoTeam.JP_MANDATORY_STATISTICS_INFO_Choice.equals(JP_Mandatory_Statistics_Info))
//			{
//				row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_Statistics_Choice), true),2);
//			}else{
//				row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_Statistics_Choice), false),2);
//			}
//			row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_Choice).getComponent(),4);
//
//
//			//*** JP_Statistics_DateAndTime ***//
//			row = statisticsInfo_rows.newRow();
//			if(p_haveParentTeamToDo && MToDoTeam.JP_MANDATORY_STATISTICS_INFO_DateAndTime.equals(JP_Mandatory_Statistics_Info))
//			{
//				row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_Statistics_DateAndTime), true),2);
//			}else {
//				row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_Statistics_DateAndTime), false),2);
//			}
//			row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_DateAndTime).getComponent(),4);
//
//
//			//*** JP_Statistics_Number ***//
//			row = statisticsInfo_rows.newRow();
//			if(p_haveParentTeamToDo && MToDoTeam.JP_MANDATORY_STATISTICS_INFO_Number.equals(JP_Mandatory_Statistics_Info))
//			{
//				row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_Statistics_Number), true),2);
//			}else {
//				row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_Statistics_Number), false),2);
//			}
//			row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_Number).getComponent(),4);
//
//		}else {
//
//			row = statisticsInfo_rows.newRow();
//			row.appendCellChild(createLabelDiv(map_Label.get(MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info), false),2);
//			row.appendCellChild(map_Editor.get(MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info).getComponent(),4);
//		}


	}


	private I_ToDo p_I_ToDo = null;
	private boolean p_IsPersonalToDo = true;
	private String p_JP_ToDo_Type = MToDo.JP_TODO_TYPE_Schedule;


	public void setToDoCalendarEvent(I_ToDo i_ToDo, ToDoCalendarEvent event)
	{
		if(i_ToDo == null)
			return ;

		p_I_ToDo = i_ToDo;
		if(p_I_ToDo instanceof MToDo)
		{
			p_IsPersonalToDo = true;

			zoomPersonalToDoBtn.setVisible(true);
			if(p_I_ToDo.getParent_Team_ToDo_ID() > 0)
			{
				zoomTeamToDoBtn.setDisabled(false);
			}else {
				zoomTeamToDoBtn.setDisabled(true);
			}


		}else {

			p_IsPersonalToDo = false;

			zoomPersonalToDoBtn.setVisible(false);
			zoomTeamToDoBtn.setDisabled(false);
		}

		p_JP_ToDo_Type = p_I_ToDo.getJP_ToDo_Type();

		map_Editor.get(MToDo.COLUMNNAME_AD_User_ID).setValue(p_I_ToDo.getAD_User_ID());
		map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Type).setValue(p_I_ToDo.getJP_ToDo_Type());
		if(p_I_ToDo.getJP_ToDo_Category_ID() > 0)
			map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Category_ID).setValue(p_I_ToDo.getJP_ToDo_Category_ID());
		else
			map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Category_ID).setValue(null);
		map_Editor.get(MToDo.COLUMNNAME_Name).setValue(p_I_ToDo.getName());
		map_Editor.get(MToDo.COLUMNNAME_Description).setValue(p_I_ToDo.getDescription());

		if(p_IsPersonalToDo)
		{
			map_Label.get(MToDo.COLUMNNAME_Comments).setVisible(true);
			map_Editor.get(MToDo.COLUMNNAME_Comments).setVisible(true);
			map_Editor.get(MToDo.COLUMNNAME_Comments).setValue(p_I_ToDo.getComments());

			map_Label.get(MToDoTeam.COLUMNNAME_JP_Team_ID).setVisible(false);
			map_Editor.get(MToDoTeam.COLUMNNAME_JP_Team_ID).setVisible(false);
			map_Editor.get(MToDoTeam.COLUMNNAME_JP_Team_ID).setValue(null);

		}else {

			map_Label.get(MToDo.COLUMNNAME_Comments).setVisible(false);
			map_Editor.get(MToDo.COLUMNNAME_Comments).setVisible(false);
			map_Editor.get(MToDo.COLUMNNAME_Comments).setValue(null);

			map_Label.get(MToDoTeam.COLUMNNAME_JP_Team_ID).setVisible(true);
			map_Editor.get(MToDoTeam.COLUMNNAME_JP_Team_ID).setVisible(true);
			if(p_I_ToDo.getJP_Team_ID() > 0)
				map_Editor.get(MToDoTeam.COLUMNNAME_JP_Team_ID).setValue(p_I_ToDo.getJP_Team_ID());
			else
				map_Editor.get(MToDoTeam.COLUMNNAME_JP_Team_ID).setValue(null);

		}

		if(MToDo.JP_TODO_TYPE_Schedule.equals(p_JP_ToDo_Type))
		{
			map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartDate).setVisible(true);
			map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartDate).setVisible(true);
			map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartDate).setValue(p_I_ToDo.getJP_ToDo_ScheduledStartTime());

			map_Editor.get(MToDo.COLUMNNAME_IsStartDateAllDayJP).setVisible(true);
			map_Editor.get(MToDo.COLUMNNAME_IsStartDateAllDayJP).setValue(p_I_ToDo.isStartDateAllDayJP());

			if(p_I_ToDo.isStartDateAllDayJP())
			{
				map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime).setVisible(false);
				map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime).setVisible(false);
				map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime).setValue(null);
			}else {
				map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime).setVisible(true);
				map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime).setVisible(true);
				map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime).setValue(p_I_ToDo.getJP_ToDo_ScheduledStartTime());
			}


		}else {

			map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartDate).setVisible(false);
			map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartDate).setVisible(false);
			map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartDate).setValue(null);

			map_Editor.get(MToDo.COLUMNNAME_IsStartDateAllDayJP).setVisible(false);

			map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime).setVisible(false);
			map_Editor.get(MToDoTeam.COLUMNNAME_JP_ToDo_ScheduledStartTime).setVisible(false);
			map_Editor.get(MToDoTeam.COLUMNNAME_JP_ToDo_ScheduledStartTime).setValue(null);

		}


		if(MToDo.JP_TODO_TYPE_Schedule.equals(p_JP_ToDo_Type) || MToDo.JP_TODO_TYPE_Task.equals(p_JP_ToDo_Type) )
		{
			map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndDate).setVisible(true);
			map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndDate).setVisible(true);
			map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndDate).setValue(p_I_ToDo.getJP_ToDo_ScheduledEndTime());

			map_Editor.get(MToDo.COLUMNNAME_IsEndDateAllDayJP).setVisible(true);
			map_Editor.get(MToDo.COLUMNNAME_IsEndDateAllDayJP).setValue(p_I_ToDo.isEndDateAllDayJP());

			if(p_I_ToDo.isEndDateAllDayJP())
			{
				map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime).setVisible(false);
				map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime).setVisible(false);
				map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime).setValue(null);
			}else {
				map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime).setVisible(true);
				map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime).setVisible(true);
				map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime).setValue(p_I_ToDo.getJP_ToDo_ScheduledEndTime());
			}

		}else {

			map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndDate).setVisible(false);
			map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndDate).setVisible(false);
			map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndDate).setValue(null);

			map_Editor.get(MToDo.COLUMNNAME_IsEndDateAllDayJP).setVisible(false);

			map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime).setVisible(false);
			map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime).setVisible(false);
			map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime).setValue(null);
		}

		map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Status).setValue(p_I_ToDo.getJP_ToDo_Status());
		map_Editor.get(MToDo.COLUMNNAME_IsOpenToDoJP).setValue(p_I_ToDo.isOpenToDoJP());

		createPopup(event);
	}

	@Override
	public void onEvent(Event event) throws Exception
	{
		Component comp = event.getTarget();

		if(comp instanceof Button)
		{

			Button btn = (Button) comp;
			String btnName = btn.getName();

			if(DETACH_POPUP.equals(btnName))
			{

				this.detach();

			}else if(ZOOM_PERSONALTODO.equals(btnName))
			{

				AEnv.zoom(MTable.getTable_ID(MToDo.Table_Name), p_I_ToDo.get_ID());
				this.detach();

			}else if(ZOOM_TEAMTODO.equals(btnName)){

				if(p_IsPersonalToDo)
					AEnv.zoom(MTable.getTable_ID(MToDoTeam.Table_Name), p_I_ToDo.getParent_Team_ToDo_ID());
				else
					AEnv.zoom(MTable.getTable_ID(MToDoTeam.Table_Name), p_I_ToDo.get_ID());
				this.detach();

			}

		}


	}

}
