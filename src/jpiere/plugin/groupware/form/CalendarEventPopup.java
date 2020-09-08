package jpiere.plugin.groupware.form;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.WDatetimeEditor;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WNumberEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.editor.WYesNoEditor;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.MColumn;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Div;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Vlayout;

import jpiere.plugin.groupware.model.I_ToDo;
import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoTeam;

public class CalendarEventPopup extends Popup {

	private Properties ctx = Env.getCtx();

	private boolean p_IsPersonalToDo = false;

	/*** Web Components ***/
	// WEditors & Labels
	private Map<String, Label> map_Label = new HashMap<String, Label>();
	private Map<String, WEditor> map_Editor = new HashMap<String, WEditor>();

	public CalendarEventPopup()
	{
		this.setWidgetAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "processButtonPopup");
		createLabelMap();
		createEditorMap();
		createPopup();
	}

	public CalendarEventPopup(boolean visible)
	{
		super(visible);
		this.setWidgetAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "processButtonPopup");
		createLabelMap();
		createEditorMap();
		createPopup();

	}

	private void createLabelMap()
	{
		map_Label.put(MToDo.COLUMNNAME_AD_User_ID, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_AD_User_ID)) );
		map_Label.put(MToDo.COLUMNNAME_JP_ToDo_Type, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Type)) );
		map_Label.put(MToDo.COLUMNNAME_JP_ToDo_Category_ID, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Category_ID)) );
		map_Label.put(MToDo.COLUMNNAME_Name, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_Name)) );
		map_Label.put(MToDo.COLUMNNAME_Description, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_Description)) );
		map_Label.put(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime)) );
		map_Label.put(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime)) );
		map_Label.put(MToDo.COLUMNNAME_JP_ToDo_Status, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Status)) );
		map_Label.put(MToDo.COLUMNNAME_IsOpenToDoJP, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_IsOpenToDoJP)) );

		if(p_IsPersonalToDo)
		{
			map_Label.put(MToDo.COLUMNNAME_Comments, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_Comments)) );
			map_Label.put(MToDo.COLUMNNAME_JP_Statistics_YesNo, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_YesNo)));
			map_Label.put(MToDo.COLUMNNAME_JP_Statistics_Choice, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_Choice)));
			map_Label.put(MToDo.COLUMNNAME_JP_Statistics_DateAndTime, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_DateAndTime)));
			map_Label.put(MToDo.COLUMNNAME_JP_Statistics_Number, new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_Number)));

		}else {
			map_Label.put(MToDoTeam.COLUMNNAME_JP_Team_ID, new Label(Msg.getElement(ctx, MToDoTeam.COLUMNNAME_JP_Team_ID)) );
			map_Label.put(MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info, new Label(Msg.getElement(ctx, MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info)) );
		}
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
		String validationCode = null;
		MLookup lookup_JP_ToDo_Category_ID = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_ToDo_Category_ID),  DisplayType.Search);
		lookup_JP_ToDo_Category_ID.getLookupInfo().ValidationCode = validationCode;
		WSearchEditor editor_JP_ToDo_Category_ID = new WSearchEditor(lookup_JP_ToDo_Category_ID, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Category_ID), null, false, true, true);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_Category_ID.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_ToDo_Category_ID, editor_JP_ToDo_Category_ID);


		//*** JP_Team_ID ***//
		if(!p_IsPersonalToDo)
		{
			validationCode = "JP_Team.AD_User_ID IS NULL OR JP_Team.AD_User_ID=" + Env.getAD_User_ID(ctx);//Login user
			MLookup lookup_JP_Team_ID = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDoTeam.Table_Name, MToDoTeam.COLUMNNAME_JP_Team_ID),  DisplayType.Search);
			lookup_JP_Team_ID.getLookupInfo().ValidationCode = validationCode;
			WSearchEditor editor_JP_Team_ID = new WSearchEditor(lookup_JP_Team_ID, Msg.getElement(ctx, MToDoTeam.COLUMNNAME_JP_Team_ID), null, false, true, true);
			ZKUpdateUtil.setHflex(editor_JP_Team_ID.getComponent(), "true");
			map_Editor.put(MToDoTeam.COLUMNNAME_JP_Team_ID, editor_JP_Team_ID);
		}


		//*** Name ***//
		WStringEditor editor_Name = new WStringEditor(MToDo.COLUMNNAME_Name, true, true, true, 30, 30, "", null);
		ZKUpdateUtil.setHflex(editor_Name.getComponent(), "true");
		editor_Name.getComponent().setRows(p_IsPersonalToDo == true ? 2 : 3);
		map_Editor.put(MToDo.COLUMNNAME_Name, editor_Name);


		//*** Description ***//
		WStringEditor editor_Description = new WStringEditor(MToDo.COLUMNNAME_Description, true, true, true, 30, 30, "", null);
		ZKUpdateUtil.setHflex(editor_Description.getComponent(), "true");
		editor_Description.getComponent().setRows(p_IsPersonalToDo == true ? 3 : 5);
		map_Editor.put(MToDo.COLUMNNAME_Description, editor_Description);


		//*** Comments ***//
		if(p_IsPersonalToDo)
		{
			WStringEditor editor_Comments = new WStringEditor(MToDo.COLUMNNAME_Comments, true, true, true, 30, 30, "", null);
			ZKUpdateUtil.setHflex(editor_Comments.getComponent(), "true");
			editor_Comments.getComponent().setRows(3);
			map_Editor.put(MToDo.COLUMNNAME_Comments, editor_Comments);
		}

		//*** JP_ToDo_ScheduledStartTime ***//
		WDatetimeEditor editor_JP_ToDo_ScheduledStartTime = new WDatetimeEditor(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime, false, true, true, null);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_ScheduledStartTime.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime, editor_JP_ToDo_ScheduledStartTime);


		//*** JP_ToDo_ScheduledEndTime ***//
		WDatetimeEditor editor_JP_ToDo_ScheduledEndTime = new WDatetimeEditor(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime, false, true, true, null);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_ScheduledEndTime.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime, editor_JP_ToDo_ScheduledEndTime);


		//*** JP_ToDo_Status ***//
		MLookup lookup_JP_ToDo_Status = MLookupFactory.get(ctx, 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_ToDo_Status),  DisplayType.List);
		WTableDirEditor editor_JP_ToDo_Status = new WTableDirEditor(lookup_JP_ToDo_Status, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Status), null, true, true, true);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_Status.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_ToDo_Status, editor_JP_ToDo_Status);

		//*** IsOpenToDoJP ***//
		WYesNoEditor editor_IsOpenToDoJP = new WYesNoEditor(MToDo.COLUMNNAME_IsOpenToDoJP, Msg.getElement(ctx, MToDo.COLUMNNAME_IsOpenToDoJP), null, true, true, true);
		map_Editor.put(MToDo.COLUMNNAME_IsOpenToDoJP, editor_IsOpenToDoJP);


		//*** Statistics Info ***/
		if(p_IsPersonalToDo)
		{
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

		}else {

			//*** JP_Mandatory_Statistics_Info ***//
			MLookup lookup_JP_Mandatory_Statistics_Info = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDoTeam.Table_Name,  MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info),  DisplayType.List);
			WTableDirEditor editor_JP_Mandatory_Statistics_Info= new WTableDirEditor(MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info, true, true, true, lookup_JP_Mandatory_Statistics_Info);
			ZKUpdateUtil.setHflex(editor_JP_Mandatory_Statistics_Info.getComponent(), "true");
			map_Editor.put(MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info, editor_JP_Mandatory_Statistics_Info);

		}
	}


	private void createPopup()
	{
		this.setWidgetAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "processButtonPopup");

		Vlayout centerContent = new Vlayout();
		//Div centerContent = new Div();
		this.appendChild(centerContent);
		ZKUpdateUtil.setVflex(this, "min");
		ZKUpdateUtil.setHflex(this, "min");


		Grid grid = GridFactory.newGridLayout();
		ZKUpdateUtil.setVflex(grid, "min");
		ZKUpdateUtil.setHflex(grid, "min");
		centerContent.appendChild(grid);

		Rows rows = grid.newRows();

		//*** AD_User_ID ***//
		Row row = rows.newRow();
		rows.appendChild(row);
		row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_AD_User_ID), true),2);
		row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_AD_User_ID).getComponent(),4);


		//*** JP_ToDo_Type ***//
		row = rows.newRow();
		row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_Type), true),2);
		row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Type).getComponent(),4);


		//*** JP_ToDo_Category_ID ***//
		row = rows.newRow();
		row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_Category_ID), false),2);
		row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Category_ID).getComponent(),4);
		map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Category_ID).showMenu();


		//*** Name ***//
		row = rows.newRow();
		row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_Name), true),2);
		row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_Name).getComponent(),4);


		//*** Description ***//
		row = rows.newRow();
		row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_Description), false),2);
		row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_Description).getComponent(),4);


		//*** Comments ***//
		if(p_IsPersonalToDo)
		{
			row = rows.newRow();
			row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_Comments), false),2);
			row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_Comments).getComponent(),4);
		}

		if(!p_IsPersonalToDo)
		{
			row = rows.newRow();
			row.appendCellChild(createLabelDiv(map_Label.get(MToDoTeam.COLUMNNAME_JP_Team_ID), false),2);
			row.appendCellChild(map_Editor.get(MToDoTeam.COLUMNNAME_JP_Team_ID).getComponent(),4);
		}

		//*** JP_ToDo_ScheduledStartTime ***//
		row = rows.newRow();
		row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime), true),2);
		row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime).getComponent(),4);


		//*** JP_ToDo_ScheduledEndTime ***//
		row = rows.newRow();
		row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime), true),2);
		row.appendCellChild(map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime).getComponent(),4);


		//*** JP_ToDo_Status ***//
		row = rows.newRow();
		row.appendCellChild(createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_Status), true),2);
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



	private Div createLabelDiv(Label label, boolean isMandatory )
	{
		label.setMandatory(isMandatory);
		Div div = new Div();
		div.setSclass("form-label");
		div.appendChild(label);
		if(isMandatory)
			div.appendChild(label.getDecorator());

		return div;
	}




	public void setToDoCalendarEvent(ToDoCalendarEvent event)
	{
		this.setStyle("border: 2px solid " + event.getHeaderColor() + ";");

		I_ToDo todo = event.getToDo();

		map_Editor.get("AD_User_ID").setValue(todo.getAD_User_ID());
		map_Editor.get("Name").setValue(todo.getName());
		map_Editor.get("Description").setValue(todo.getDescription());

	}

}
