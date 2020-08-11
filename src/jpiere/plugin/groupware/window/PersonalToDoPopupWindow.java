/******************************************************************************
 * Copyright (C) 2008 Elaine Tan                                              *
 * Copyright (C) 2008 Idalica Corporation                                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package jpiere.plugin.groupware.window;

import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

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
import org.adempiere.webui.editor.WDatetimeEditor;
import org.adempiere.webui.editor.WNumberEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.editor.WYesNoEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.MColumn;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Center;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.South;

import jpiere.plugin.groupware.form.JPierePersonalToDoGadget;
import jpiere.plugin.groupware.model.MToDo;

/**
 *
 * @author Elaine
 *
 */
public class PersonalToDoPopupWindow extends Window implements EventListener<Event>,ValueChangeListener {

	private static final long serialVersionUID = 7757368164776005797L;

	private static final CLogger log = CLogger.getCLogger(PersonalToDoPopupWindow.class);

	/*** WEditors & Labels **/
	private WSearchEditor editor_AD_User_ID = null;
	private Label label_AD_User_ID = null;

	private WTableDirEditor editor_JP_ToDo_Type = null;
	private Label label_JP_ToDo_Type = null;

	private WSearchEditor editor_JP_ToDo_Category_ID = null;
	private Label label_JP_ToDo_Category_ID = null;

	private WStringEditor editor_Name = null;
	private Label label_Name = null;

	private WStringEditor editor_Description = null;
	private Label label_Description = null;

	private WStringEditor editor_Comments = null;
	private Label label_Comments = null;

	private WDatetimeEditor editor_JP_ToDo_ScheduledStartTime = null;
	private Label label_JP_ToDo_ScheduledStartTime = null;

	private WDatetimeEditor editor_JP_ToDo_ScheduledEndTime = null;
	private Label label_JP_ToDo_ScheduledEndTime = null;

	private WTableDirEditor editor_JP_ToDo_Status = null;
	private Label label_JP_ToDo_Status = null;

	private WYesNoEditor editor_IsOpenToDoJP = null;
	private Label label_IsOpenToDoJP = null;

	private WTableDirEditor editor_JP_Statistics_YesNo = null;
	private Label label_JP_Statistics_YesNo = null;

	private WTableDirEditor editor_JP_Statistics_Choice = null;
	private Label label_JP_Statistics_Choic = null;

	private WDatetimeEditor editor_JP_Statistics_DateAndTime = null;
	private Label label_JP_Statistics_DateAndTime = null;

	private WNumberEditor editor_JP_Statistics_Number = null;
	private Label label_JP_Statistics_Number = null;



	/** Read Only				*/
	private boolean isUpdatable = false;
	private boolean isNewRecord = false;
	private boolean isTeamToDo = false;

	private int p_Record_ID = 0;
	private MToDo m_ToDo = null;
	private int p_AD_User_ID = 0;
	private String p_JP_ToDo_Type = null;
	private Timestamp p_SelectedDate = null;


	private ConfirmPanel confirmPanel;

	private JPierePersonalToDoGadget parent;


	public PersonalToDoPopupWindow(Event event, JPierePersonalToDoGadget parent) {

		super();
		this.parent = parent;

		Component comp = event.getTarget();
		String string_ID = comp.getId();
		if(Util.isEmpty(string_ID))
		{
			;//TODO エラー

		}else {

			p_Record_ID = Integer.valueOf(string_ID).intValue();
		}

		Properties ctx = Env.getCtx();
		p_AD_User_ID = parent.getAD_User_ID();
		p_JP_ToDo_Type = parent.getJP_ToDo_Type();
		p_SelectedDate = parent.getSelectedDate();

		if(p_Record_ID != 0 )
		{
			m_ToDo = new MToDo(ctx, p_Record_ID, null);
			if(p_AD_User_ID == Env.getAD_User_ID(ctx) || m_ToDo.getCreatedBy() == Env.getAD_User_ID(ctx))
			{
				isNewRecord = false;
				if(m_ToDo.isProcessed())
					isUpdatable = false;
				else
					isUpdatable = true;
			}else {

				isNewRecord = false;
				isUpdatable = false;
			}
		}else {

			isNewRecord = true;
			isUpdatable = true;

		}

		if(p_Record_ID == 0)
			isTeamToDo = false;
		else if(m_ToDo.getJP_ToDo_Team_ID() == 0)
			isTeamToDo = false;
		else
			isTeamToDo = true;


		//Window Title
		if(p_Record_ID != 0 )
		{
			if(m_ToDo.getJP_ToDo_Team_ID() == 0)
			{
				setTitle("[" + Msg.getElement(Env.getCtx(),MToDo.COLUMNNAME_JP_ToDo_ID) + "] "
						+ Msg.getElement(Env.getCtx(),MToDo.COLUMNNAME_CreatedBy)
						+ ":" + MUser.getNameOfUser(m_ToDo.getCreatedBy()));

			}else {

				setTitle("[" + Msg.getElement(Env.getCtx(),MToDo.COLUMNNAME_JP_ToDo_Team_ID) + "] "
						+ Msg.getElement(Env.getCtx(),MToDo.COLUMNNAME_CreatedBy)
						+ ":" + MUser.getNameOfUser(m_ToDo.getCreatedBy()));
			}

		}else {
			setTitle("[" + Msg.getElement(Env.getCtx(),MToDo.COLUMNNAME_JP_ToDo_ID) + "]");
		}

		setAttribute(Window.MODE_KEY, Window.MODE_HIGHLIGHTED);
		if (!ThemeManager.isUseCSSForWindowSize()) {
			ZKUpdateUtil.setWindowWidthX(this, 400);
			ZKUpdateUtil.setWindowHeightX(this, 600);
		} else {
			addCallback(AFTER_PAGE_ATTACHED, t -> {
				ZKUpdateUtil.setCSSHeight(this);
				ZKUpdateUtil.setCSSWidth(this);
			});
		}
		this.setSclass("popup-dialog request-dialog");
		this.setBorder("normal");
		this.setShadow(true);
		this.setClosable(true);


		//*****************************************************************//

		Grid grid = GridFactory.newGridLayout();

		Rows rows = new Rows();
		grid.appendChild(rows);

		//*** AD_User_ID ***//
		MLookup lookup_AD_User_ID = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_AD_User_ID),  DisplayType.Search);
		editor_AD_User_ID = new WSearchEditor(lookup_AD_User_ID, Msg.getElement(ctx, MToDo.COLUMNNAME_AD_User_ID), null, true, isTeamToDo? true : !isUpdatable, true);
		if(isNewRecord)
		{
			editor_AD_User_ID.setValue(p_AD_User_ID);
		}else {
			editor_AD_User_ID.setValue(p_AD_User_ID);
		}
		editor_AD_User_ID.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_AD_User_ID.getComponent(), "true");

		label_AD_User_ID = editor_AD_User_ID.getLabel();
		label_AD_User_ID.setMandatory(true);
		Div div_AD_User_ID = new Div();
		div_AD_User_ID.setSclass("form-label");
		div_AD_User_ID.appendChild(label_AD_User_ID);
		div_AD_User_ID.appendChild(label_AD_User_ID.getDecorator());

		Row row = new Row();
		rows.appendChild(row);
		row.appendCellChild(div_AD_User_ID,2);
		row.appendCellChild(editor_AD_User_ID.getComponent(),4);


		//*** JP_ToDo_Type ***//
		label_JP_ToDo_Type = new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Type));
		label_JP_ToDo_Type.setMandatory(true);
		Div div_JP_ToDo_Type = new Div();
		div_JP_ToDo_Type.setSclass("form-label");
		div_JP_ToDo_Type.appendChild(label_JP_ToDo_Type);
		div_JP_ToDo_Type.appendChild(label_JP_ToDo_Type.getDecorator());

		MLookup lookup_JP_ToDo_Type = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name,  MToDo.COLUMNNAME_JP_ToDo_Type),  DisplayType.List);
		editor_JP_ToDo_Type = new WTableDirEditor(lookup_JP_ToDo_Type, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Type), null, true, isTeamToDo? true : !isUpdatable, true);
		if(isNewRecord)
		{
			editor_JP_ToDo_Type.setValue(p_JP_ToDo_Type);
		}else {
			editor_JP_ToDo_Type.setValue(m_ToDo.getJP_ToDo_Type());
		}

		editor_JP_ToDo_Type.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_Type.getComponent(), "true");

		row = new Row();
		rows.appendChild(row);
		row.appendCellChild(div_JP_ToDo_Type,2);
		row.appendCellChild(editor_JP_ToDo_Type.getComponent(),4);


		//*** JP_ToDo_Category_ID ***//
		label_JP_ToDo_Category_ID = new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Category_ID));
		Div div_JP_ToDo_Category_ID = new Div();
		div_JP_ToDo_Category_ID.setSclass("form-label");
		div_JP_ToDo_Category_ID.appendChild(label_JP_ToDo_Category_ID);

		MLookup lookup_JP_ToDo_Category_ID = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_ToDo_Category_ID),  DisplayType.Search);
		editor_JP_ToDo_Category_ID = new WSearchEditor(lookup_JP_ToDo_Category_ID, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Category_ID), null, false, isTeamToDo? true : !isUpdatable, true);
		if(isNewRecord)
		{
			;
		}else {
			editor_JP_ToDo_Category_ID.setValue(m_ToDo.getJP_ToDo_Category_ID());
		}
		editor_JP_ToDo_Category_ID.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_Category_ID.getComponent(), "true");

		row = new Row();
		rows.appendChild(row);
		row.appendCellChild(div_JP_ToDo_Category_ID,2);
		row.appendCellChild(editor_JP_ToDo_Category_ID.getComponent(),4);


		//*** Name ***//
		label_Name = new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_Name));
		label_Name.setMandatory(true);
		Div div_Name = new Div();
		div_Name.setSclass("form-label");
		div_Name.appendChild(label_Name);
		div_Name.appendChild(label_Name.getDecorator());

		editor_Name = new WStringEditor(MToDo.COLUMNNAME_Name, true, isTeamToDo? true : !isUpdatable, true, 30, 30, "", null);
		if(isNewRecord)
		{
			;
		}else {
			editor_Name.setValue(m_ToDo.getName());
		}
		editor_Name.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_Name.getComponent(), "true");
		editor_Name.getComponent().setRows(2);

		row = new Row();
		rows.appendChild(row);
		row.appendCellChild(div_Name,2);
		row.appendCellChild(editor_Name.getComponent(),4);


		//*** Description ***//
		Label label_Description = new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_Description));
		Div div_Description = new Div();
		div_Description.setSclass("form-label");
		div_Description.appendChild(label_Description);

		editor_Description = new WStringEditor(MToDo.COLUMNNAME_Description, true, isTeamToDo? true : !isUpdatable, true, 30, 30, "", null);
		if(isNewRecord)
		{
			;
		}else {
			editor_Description.setValue(m_ToDo.getDescription());
		}
		editor_Description.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_Description.getComponent(), "true");
		editor_Description.getComponent().setRows(2);

		row = new Row();
		rows.appendChild(row);
		row.appendCellChild(div_Description,2);
		row.appendCellChild(editor_Description.getComponent(),4);


		//*** Comments ***//
		Label label_Comments = new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_Comments));
		Div div_Comments = new Div();
		div_Comments.setSclass("form-label");
		div_Comments.appendChild(label_Comments);

		editor_Comments = new WStringEditor(MToDo.COLUMNNAME_Comments, true, !isUpdatable, true, 30, 30, "", null);
		if(isNewRecord)
		{
			;
		}else {
			editor_Comments.setValue(m_ToDo.getComments());
		}

		editor_Comments.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_Comments.getComponent(), "true");
		editor_Comments.getComponent().setRows(2);

		row = new Row();
		rows.appendChild(row);
		row.appendCellChild(div_Comments,2);
		row.appendCellChild(editor_Comments.getComponent(),4);


		//*** JP_ToDo_ScheduledStartTime ***//
		Label label_JP_ToDo_ScheduledStartTime = new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime));
		label_JP_ToDo_ScheduledStartTime.setMandatory(true);
		Div div_JP_ToDo_ScheduledStartTime = new Div();
		div_JP_ToDo_ScheduledStartTime.setSclass("form-label");
		div_JP_ToDo_ScheduledStartTime.appendChild(label_JP_ToDo_ScheduledStartTime);
		div_JP_ToDo_ScheduledStartTime.appendChild(label_JP_ToDo_ScheduledStartTime.getDecorator());

		editor_JP_ToDo_ScheduledStartTime = new WDatetimeEditor(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime, false, isTeamToDo? true : !isUpdatable, true, null);
		if(isNewRecord)
		{
			editor_JP_ToDo_ScheduledStartTime.setValue(p_SelectedDate);
		}else {
			editor_JP_ToDo_ScheduledStartTime.setValue(m_ToDo.getJP_ToDo_ScheduledStartTime());
		}
		editor_JP_ToDo_ScheduledStartTime.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_ScheduledStartTime.getComponent(), "true");

		row = new Row();
		rows.appendChild(row);
		row.appendCellChild(div_JP_ToDo_ScheduledStartTime,2);
		row.appendCellChild(editor_JP_ToDo_ScheduledStartTime.getComponent(),4);

//		label_JP_ToDo_ScheduledStartTime.setVisible(false);
//		editor_JP_ToDo_ScheduledStartTime.setVisible(false);

		//*** JP_ToDo_ScheduledEndTime ***//
		Label label_JP_ToDo_ScheduledEndTime = new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime));
		label_JP_ToDo_ScheduledEndTime.setMandatory(true);
		Div div_JP_ToDo_ScheduledEndTime = new Div();
		div_JP_ToDo_ScheduledEndTime.setSclass("form-label");
		div_JP_ToDo_ScheduledEndTime.appendChild(label_JP_ToDo_ScheduledEndTime);
		div_JP_ToDo_ScheduledEndTime.appendChild(label_JP_ToDo_ScheduledEndTime.getDecorator());

		editor_JP_ToDo_ScheduledEndTime = new WDatetimeEditor(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime, false, isTeamToDo? true :!isUpdatable, true, null);
		if(isNewRecord)
		{
			editor_JP_ToDo_ScheduledEndTime.setValue(p_SelectedDate);
		}else {
			editor_JP_ToDo_ScheduledEndTime.setValue(m_ToDo.getJP_ToDo_ScheduledEndTime());
		}
		editor_JP_ToDo_ScheduledEndTime.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_ScheduledEndTime.getComponent(), "true");

		row = new Row();
		rows.appendChild(row);
		row.appendCellChild(div_JP_ToDo_ScheduledEndTime,2);
		row.appendCellChild(editor_JP_ToDo_ScheduledEndTime.getComponent(),4);


		//*** JP_ToDo_Status ***//
		Label label_JP_ToDo_Status = new Label(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Status));
		label_JP_ToDo_Status.setMandatory(true);
		Div div_JP_ToDo_Status = new Div();
		div_JP_ToDo_Status.setSclass("form-label");
		div_JP_ToDo_Status.appendChild(label_JP_ToDo_Status);
		div_JP_ToDo_Status.appendChild(label_JP_ToDo_Status.getDecorator());

		MLookup lookup_JP_ToDo_Status = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_ToDo_Status),  DisplayType.List);
		editor_JP_ToDo_Status = new WTableDirEditor(lookup_JP_ToDo_Status, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Status), null, true, isUpdatable? false: true, true);
		if(isNewRecord)
		{
			editor_JP_ToDo_Status.setValue(MToDo.JP_TODO_STATUS_NotYetStarted);
		}else {
			editor_JP_ToDo_Status.setValue(m_ToDo.getJP_ToDo_Status());
		}

		editor_JP_ToDo_Status.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_Status.getComponent(), "true");

		row = new Row();
		rows.appendChild(row);
		row.appendCellChild(div_JP_ToDo_Status,2);
		row.appendCellChild(editor_JP_ToDo_Status.getComponent(),2);


		/** IsOpenToDoJP **/
		editor_IsOpenToDoJP = new WYesNoEditor(MToDo.COLUMNNAME_JP_Statistics_YesNo, Msg.getElement(ctx, MToDo.COLUMNNAME_IsOpenToDoJP), null, true, !isUpdatable, true);
		if(isNewRecord)
		{
			editor_IsOpenToDoJP.setValue("Y");
		}else {
			editor_IsOpenToDoJP.setValue(m_ToDo.isOpenToDoJP()==true ? "Y" : "N");
		}

		Div div_IsOpenToDoJP = new Div();
		div_IsOpenToDoJP.appendChild(editor_IsOpenToDoJP.getComponent());
		div_IsOpenToDoJP.appendChild(editor_IsOpenToDoJP.getLabel());
		row.appendCellChild(div_IsOpenToDoJP,2);


		/********************************************************************************************
		 * Statistics Info
		 ********************************************************************************************/

		row = new Row();
		rows.appendChild(row);
		Groupbox statisticsInfo_GroupBox = new Groupbox();
		statisticsInfo_GroupBox.setOpen(true);
		row.appendCellChild(statisticsInfo_GroupBox,6);
		statisticsInfo_GroupBox.appendChild(new Caption(Msg.getMsg(Env.getCtx(),"JP_StatisticsInfo")));
		Grid statisticsInfo_Grid  = GridFactory.newGridLayout();
		statisticsInfo_Grid.setStyle("background-color: #E9F0FF");
		statisticsInfo_Grid.setStyle("border: none");
		statisticsInfo_GroupBox.appendChild(statisticsInfo_Grid);

		Rows statisticsInfo_rows = new Rows();
		statisticsInfo_Grid.appendChild(statisticsInfo_rows);
		row = new Row();
		statisticsInfo_rows.appendChild(row);

		/** JP_Statistics_YesNo  **/
		MLookup lookup_JP_Statistics_YesNo = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_Statistics_YesNo),  DisplayType.List);
		editor_JP_Statistics_YesNo = new WTableDirEditor(lookup_JP_Statistics_YesNo, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_YesNo), null, false, !isUpdatable, true);
		if(isNewRecord)
		{

		}else {
			editor_JP_Statistics_YesNo.setValue(m_ToDo.getJP_Statistics_YesNo());
		}
		editor_JP_Statistics_YesNo.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_Statistics_YesNo.getComponent(), "true");

		row = new Row();
		statisticsInfo_rows.appendChild(row);
		row.appendCellChild(editor_JP_Statistics_YesNo.getLabel().rightAlign(),2);
		row.appendCellChild(editor_JP_Statistics_YesNo.getComponent(),4);


		/** JP_Statistics_Choice **/
		MLookup lookup_JP_Statistics_Choice = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_Statistics_Choice),  DisplayType.List);
		editor_JP_Statistics_Choice = new WTableDirEditor(lookup_JP_Statistics_Choice, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_Choice), null, false, !isUpdatable, true);
		if(isNewRecord)
		{

		}else {
			editor_JP_Statistics_Choice.setValue(m_ToDo.getJP_Statistics_Choice());
		}
		editor_JP_Statistics_Choice.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_Statistics_Choice.getComponent(), "true");

		row = new Row();
		statisticsInfo_rows.appendChild(row);
		row.appendCellChild(editor_JP_Statistics_Choice.getLabel().rightAlign(),2);
		row.appendCellChild(editor_JP_Statistics_Choice.getComponent(),4);


		/** JP_Statistics_DateAndTime **/
		editor_JP_Statistics_DateAndTime = new WDatetimeEditor(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_DateAndTime), null, false, !isUpdatable, true);
		if(isNewRecord)
		{

		}else {
			editor_JP_Statistics_DateAndTime.setValue(m_ToDo.getJP_Statistics_DateAndTime());
		}
		ZKUpdateUtil.setHflex((HtmlBasedComponent)editor_JP_Statistics_DateAndTime.getComponent(), "true");

		row = new Row();
		statisticsInfo_rows.appendChild(row);
		row.appendCellChild(editor_JP_Statistics_DateAndTime.getLabel().rightAlign(),2);
		row.appendCellChild(editor_JP_Statistics_DateAndTime.getComponent(),4);


		/** JP_Statistics_Number **/
		editor_JP_Statistics_Number = new WNumberEditor(MToDo.COLUMNNAME_JP_Statistics_Number, false, !isUpdatable, true, DisplayType.Number, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_Number));
		if(isNewRecord)
		{

		}else {
			editor_JP_Statistics_Number.setValue(m_ToDo.getJP_Statistics_Number());
		}
		row = new Row();
		statisticsInfo_rows.appendChild(row);
		row.appendCellChild(editor_JP_Statistics_Number.getLabel().rightAlign(),2);
		row.appendCellChild(editor_JP_Statistics_Number.getComponent(),4);


		Borderlayout borderlayout = new Borderlayout();
		this.appendChild(borderlayout);
		ZKUpdateUtil.setHflex(borderlayout, "1");
		ZKUpdateUtil.setVflex(borderlayout, "1");

		//TODO
		Button refresh = new Button();
		refresh.setImage(ThemeManager.getThemeResource("images/" + "Refresh16.png"));
		refresh.setClass("btn-small");
		refresh.setId(comp.getId());
		refresh.addEventListener(Events.ON_CLICK, this);
		borderlayout.appendNorth(refresh);

		Center centerPane = new Center();
		centerPane.setSclass("dialog-content");
		centerPane.setAutoscroll(true);
		borderlayout.appendChild(centerPane);

		centerPane.appendChild(grid);
		ZKUpdateUtil.setVflex(grid, "min");
		ZKUpdateUtil.setHflex(grid, "1");
		ZKUpdateUtil.setVflex(centerPane, "min");


		confirmPanel = new ConfirmPanel(true);
		confirmPanel.addActionListener(this);

		South southPane = new South();
		southPane.setSclass("dialog-footer");
		borderlayout.appendChild(southPane);
		southPane.appendChild(confirmPanel);

	}

	public void onEvent(Event e) throws Exception
	{
		Component comp = e.getTarget();
		Object list_index = comp.getAttribute("index");
		String eventName = e.getName();

		if (e.getTarget() == confirmPanel.getButton(ConfirmPanel.A_OK))
		{

			if(!isUpdatable)
			{
				return;
			}

			if(p_Record_ID == 0)
				m_ToDo = new MToDo(Env.getCtx(), 0, null);

			m_ToDo.setAD_Org_ID(0);

			//Check AD_User_ID
			if(editor_AD_User_ID.getValue() == null || ((Integer)editor_AD_User_ID.getValue()).intValue() == 0)
			{
				FDialog.error(0, this, Msg.translate(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), editor_AD_User_ID.getColumnName()));
				return ;
			}else {
				m_ToDo.setAD_User_ID((Integer)editor_AD_User_ID.getValue());
			}

			//Check JP_ToDo_Type
			if(editor_JP_ToDo_Type.getValue() == null || Util.isEmpty(editor_JP_ToDo_Type.getValue().toString()))
			{
				FDialog.error(0, this, Msg.translate(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), editor_JP_ToDo_Type.getColumnName()));
				return ;
			}else {
				m_ToDo.setJP_ToDo_Type((String)editor_JP_ToDo_Type.getValue());
			}

			//Check JP_ToDo_Category_ID
			if(editor_JP_ToDo_Category_ID.getValue() == null || ((Integer)editor_JP_ToDo_Category_ID.getValue()).intValue() == 0)
			{
				;
			}else {
				m_ToDo.setJP_ToDo_Category_ID((Integer)editor_JP_ToDo_Category_ID.getValue());
			}

			//Check Name
			if(editor_Name.getValue() == null || Util.isEmpty(editor_Name.getValue().toString()))
			{
				FDialog.error(0, this, Msg.translate(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "Name"));
				return ;
			}else {
				m_ToDo.setName((String)editor_Name.getValue());
			}

			//Check Description
			if(editor_Description.getValue() == null || Util.isEmpty(editor_Description.getValue().toString()))
			{

			}else {
				m_ToDo.setDescription(editor_Description.getValue().toString());
			}

			//Check Comments
			if(editor_Comments.getValue() == null || Util.isEmpty(editor_Comments.getValue().toString()))
			{

			}else {
				m_ToDo.setComments(editor_Comments.getValue().toString());
			}

			m_ToDo.setJP_ToDo_ScheduledStartTime((Timestamp)editor_JP_ToDo_ScheduledStartTime.getValue());
			m_ToDo.setJP_ToDo_ScheduledEndTime((Timestamp)editor_JP_ToDo_ScheduledEndTime.getValue());

			//Check Description
			if(editor_JP_ToDo_Status.getValue() == null || Util.isEmpty(editor_JP_ToDo_Status.getValue().toString()))
			{
				FDialog.error(0, this, Msg.translate(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "JP_ToDo_Status"));
				return ;

			}else {
				m_ToDo.setJP_ToDo_Status(editor_JP_ToDo_Status.getValue().toString());
			}



			String msg = m_ToDo.beforeSavePreCheck(true);
			if(!Util.isEmpty(msg))
			{
				FDialog.error(0, this, msg);
				return;
			}

			if (m_ToDo.save())
			{
				if (log.isLoggable(Level.FINE)) log.fine("R_Request_ID=" + m_ToDo.getJP_ToDo_ID());

				//Events.postEvent("onRefresh", parent, null);
				parent.createContents();

//					Events.echoEvent("onRefresh", parent, null);
			}
			else
			{
				FDialog.error(0, this, "Request record not saved");//TODO 多言語化: 予期せぬエラー / 保存できませんでした。
				return;
			}




			this.detach();
		}
		else if (e.getTarget() == confirmPanel.getButton(ConfirmPanel.A_CANCEL))
		{
			this.detach();
		}else {

			AEnv.zoom(MTable.getTable_ID(MToDo.Table_Name), Integer.valueOf(comp.getId()).intValue());
			this.detach();
		}
	}



	@Override
	public void valueChange(ValueChangeEvent evt)
	{
		;
	}
}
