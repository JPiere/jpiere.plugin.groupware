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
import java.util.HashMap;
import java.util.Map;
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
import org.adempiere.webui.editor.WEditor;
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
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Html;
import org.zkoss.zul.North;
import org.zkoss.zul.South;

import jpiere.plugin.groupware.form.JPierePersonalToDoGadget;
import jpiere.plugin.groupware.model.MToDo;

/**
 * JPIERE-0470 Personal ToDo Popup Window
 *
 *
 * @author h.hagiwara
 *
 */
public class PersonalToDoPopupWindow extends Window implements EventListener<Event>,ValueChangeListener {

	private static final long serialVersionUID = 7757368164776005797L;

	private static final CLogger log = CLogger.getCLogger(PersonalToDoPopupWindow.class);

	/*** WEditors & Labels **/
	private Map<String, Label> map_Label = new HashMap<String, Label>();
	private Map<String, WEditor> map_Editor = new HashMap<String, WEditor>();

	/** Control Parameters	*/
	private boolean p_IsUpdatable = false;
	private boolean p_IsNewRecord = false;
	private boolean p_IsTeamToDo = false;

	private MToDo p_MToDo = null;
	private int p_JP_ToDo_ID = 0;
	private int p_AD_User_ID = 0;
	private String p_JP_ToDo_Type = null;
	private Timestamp p_SelectedDate = null;


	private North north = null;
	private Center center = null;
	private ConfirmPanel confirmPanel;

	private JPierePersonalToDoGadget i_CallPersonalToDoPopupwindow;

	private Properties ctx = null;

	public PersonalToDoPopupWindow(Event event, JPierePersonalToDoGadget parent)
	{
		super();
		this.i_CallPersonalToDoPopupwindow = parent;
		ctx = Env.getCtx();

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


		Component comp = event.getTarget();
		String string_ID = comp.getId();
		if(Util.isEmpty(string_ID))
		{
			;//TODO エラー

		}else {

			p_JP_ToDo_ID = Integer.valueOf(string_ID).intValue();
		}

		updateControlParameter(p_JP_ToDo_ID);
		updateWindowTitle();

		createLabelMap();
		createEditorMap();

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


		confirmPanel = new ConfirmPanel(true);
		confirmPanel.addActionListener(this);

		South southPane = new South();
		southPane.setSclass("dialog-footer");
		borderlayout.appendChild(southPane);
		southPane.appendChild(confirmPanel);

	}

	private void updateControlParameter(int JP_ToDO_ID)
	{
		p_JP_ToDo_ID = JP_ToDO_ID;
		p_SelectedDate = i_CallPersonalToDoPopupwindow.getSelectedDate();

		if(p_JP_ToDo_ID == 0)
		{
			p_IsNewRecord = true;
			p_MToDo = null;
			p_AD_User_ID = i_CallPersonalToDoPopupwindow.getAD_User_ID();
			p_JP_ToDo_Type = i_CallPersonalToDoPopupwindow.getJP_ToDo_Type();

		}else {

			p_IsNewRecord = false;
			p_MToDo = new MToDo(ctx, p_JP_ToDo_ID, null);
			p_AD_User_ID = p_MToDo.getAD_User_ID();
			p_JP_ToDo_Type = p_MToDo.getJP_ToDo_Type();
		}

		if(p_IsNewRecord)
		{
			p_IsUpdatable = true;

		}else {

			if(p_AD_User_ID == Env.getAD_User_ID(ctx) || p_MToDo.getCreatedBy() == Env.getAD_User_ID(ctx))
			{
				if(p_MToDo.isProcessed())
					p_IsUpdatable = false;
				else
					p_IsUpdatable = true;

			}else {

				p_IsUpdatable = false;
			}
		}


		if(p_IsNewRecord)
			p_IsTeamToDo = false;
		else if(p_MToDo.getJP_ToDo_Team_ID() == 0)
			p_IsTeamToDo = false;
		else
			p_IsTeamToDo = true;

	}

	private void createLabelMap()
	{
		MTable m_table = MTable.get(ctx, MToDo.Table_Name);
		MColumn[] m_Columns = m_table.getColumns(false);
		String columnName = null;
		Label label = null;
		for(int i = 0; i < m_Columns.length; i++)
		{
			columnName = m_Columns[i].getColumnName();
			label = new Label(Msg.getElement(ctx, columnName));
			map_Label.put(columnName,label);
		}
	}

	private void createEditorMap()//TODO
	{
		//*** AD_User_ID ***//
		MLookup lookup_AD_User_ID = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_AD_User_ID),  DisplayType.Search);
		WSearchEditor Editor_AD_User_ID = new WSearchEditor(lookup_AD_User_ID, Msg.getElement(ctx, MToDo.COLUMNNAME_AD_User_ID), null, true, p_IsTeamToDo? true : !p_IsUpdatable, true);
		Editor_AD_User_ID.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(Editor_AD_User_ID.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_AD_User_ID, Editor_AD_User_ID);


		//*** JP_ToDo_Type ***//
		MLookup lookup_JP_ToDo_Type = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name,  MToDo.COLUMNNAME_JP_ToDo_Type),  DisplayType.List);
		WTableDirEditor editor_JP_ToDo_Type = new WTableDirEditor(lookup_JP_ToDo_Type, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Type), null, true, p_IsTeamToDo? true : !p_IsUpdatable, true);
		editor_JP_ToDo_Type.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_Type.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_ToDo_Type, editor_JP_ToDo_Type);


		//*** JP_ToDo_Category_ID ***//
		MLookup lookup_JP_ToDo_Category_ID = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_ToDo_Category_ID),  DisplayType.Search);
		WSearchEditor editor_JP_ToDo_Category_ID = new WSearchEditor(lookup_JP_ToDo_Category_ID, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Category_ID), null, false, p_IsTeamToDo? true : !p_IsUpdatable, true);
		editor_JP_ToDo_Category_ID.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_Category_ID.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_ToDo_Category_ID, editor_JP_ToDo_Category_ID);


		//*** Name ***//
		WStringEditor editor_Name = new WStringEditor(MToDo.COLUMNNAME_Name, true, p_IsTeamToDo? true : !p_IsUpdatable, true, 30, 30, "", null);
		editor_Name.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_Name.getComponent(), "true");
		editor_Name.getComponent().setRows(2);
		map_Editor.put(MToDo.COLUMNNAME_Name, editor_Name);


		//*** Description ***//
		WStringEditor editor_Description = new WStringEditor(MToDo.COLUMNNAME_Description, true, p_IsTeamToDo? true : !p_IsUpdatable, true, 30, 30, "", null);
		editor_Description.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_Description.getComponent(), "true");
		editor_Description.getComponent().setRows(2);
		map_Editor.put(MToDo.COLUMNNAME_Description, editor_Description);


		//*** Comments ***//
		WStringEditor editor_Comments = new WStringEditor(MToDo.COLUMNNAME_Comments, true, !p_IsUpdatable, true, 30, 30, "", null);
		editor_Comments.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_Comments.getComponent(), "true");
		editor_Comments.getComponent().setRows(2);
		map_Editor.put(MToDo.COLUMNNAME_Comments, editor_Comments);


		//*** JP_ToDo_ScheduledStartTime ***//
		WDatetimeEditor editor_JP_ToDo_ScheduledStartTime = new WDatetimeEditor(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime, false, p_IsTeamToDo? true : !p_IsUpdatable, true, null);
		editor_JP_ToDo_ScheduledStartTime.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_ScheduledStartTime.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime, editor_JP_ToDo_ScheduledStartTime);


		//*** JP_ToDo_ScheduledEndTime ***//
		WDatetimeEditor editor_JP_ToDo_ScheduledEndTime = new WDatetimeEditor(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime, false, p_IsTeamToDo? true :!p_IsUpdatable, true, null);
		editor_JP_ToDo_ScheduledEndTime.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_ScheduledEndTime.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime, editor_JP_ToDo_ScheduledEndTime);


		//*** JP_ToDo_Status ***//
		MLookup lookup_JP_ToDo_Status = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_ToDo_Status),  DisplayType.List);
		WTableDirEditor editor_JP_ToDo_Status = new WTableDirEditor(lookup_JP_ToDo_Status, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Status), null, true, p_IsUpdatable? false: true, true);
		editor_JP_ToDo_Status.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_ToDo_Status.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_ToDo_Status, editor_JP_ToDo_Status);

		//*** IsOpenToDoJP ***//
		WYesNoEditor editor_IsOpenToDoJP = new WYesNoEditor(MToDo.COLUMNNAME_JP_Statistics_YesNo, Msg.getElement(ctx, MToDo.COLUMNNAME_IsOpenToDoJP), null, true, !p_IsUpdatable, true);
		map_Editor.put(MToDo.COLUMNNAME_JP_Statistics_YesNo, editor_IsOpenToDoJP);


		//*** JP_Statistics_YesNo  ***//
		MLookup lookup_JP_Statistics_YesNo = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_Statistics_YesNo),  DisplayType.List);
		WTableDirEditor editor_JP_Statistics_YesNo = new WTableDirEditor(lookup_JP_Statistics_YesNo, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_YesNo), null, false, !p_IsUpdatable, true);
		editor_JP_Statistics_YesNo.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_Statistics_YesNo.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_Statistics_YesNo, editor_JP_Statistics_YesNo);


		//*** JP_Statistics_Choice ***//
		MLookup lookup_JP_Statistics_Choice = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_Statistics_Choice),  DisplayType.List);
		WTableDirEditor editor_JP_Statistics_Choice = new WTableDirEditor(lookup_JP_Statistics_Choice, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_Choice), null, false, !p_IsUpdatable, true);
		editor_JP_Statistics_Choice.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(editor_JP_Statistics_Choice.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_Statistics_Choice, editor_JP_Statistics_Choice);


		//*** JP_Statistics_DateAndTime ***//
		WDatetimeEditor editor_JP_Statistics_DateAndTime = new WDatetimeEditor(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_DateAndTime), null, false, !p_IsUpdatable, true);
		ZKUpdateUtil.setHflex((HtmlBasedComponent)editor_JP_Statistics_DateAndTime.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_Statistics_DateAndTime, editor_JP_Statistics_DateAndTime);


		//*** JP_Statistics_Number ***//
		WNumberEditor editor_JP_Statistics_Number = new WNumberEditor(MToDo.COLUMNNAME_JP_Statistics_Number, false, !p_IsUpdatable, true, DisplayType.Number, Msg.getElement(ctx, MToDo.COLUMNNAME_JP_Statistics_Number));
		ZKUpdateUtil.setHflex(editor_JP_Statistics_Number.getComponent(), "true");
		map_Editor.put(MToDo.COLUMNNAME_JP_Statistics_Number, editor_JP_Statistics_Number);
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

	private void updateWindowTitle()
	{
		if(p_JP_ToDo_ID != 0 )
		{
			if(p_MToDo.getJP_ToDo_Team_ID() == 0)
			{
				setTitle("[" + Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_ID) + "] "
						+ Msg.getElement(Env.getCtx(),MToDo.COLUMNNAME_CreatedBy)
						+ ":" + MUser.getNameOfUser(p_MToDo.getCreatedBy()));

			}else {

				setTitle("[" + Msg.getElement(ctx,MToDo.COLUMNNAME_JP_ToDo_Team_ID) + "] "
						+ Msg.getElement(Env.getCtx(),MToDo.COLUMNNAME_CreatedBy)
						+ ":" + MUser.getNameOfUser(p_MToDo.getCreatedBy()));
			}

		}else {
			setTitle("[" + Msg.getElement(ctx,MToDo.COLUMNNAME_JP_ToDo_ID) + "]");
		}

	}

	private North updateNorth()
	{
		if(north.getFirstChild() != null)
			north.getFirstChild().detach();

		if(p_IsNewRecord)
			return north;

		Hlayout hlyaout = new Hlayout();
		north.appendChild(hlyaout);

		Div div1 = new Div();
		div1.appendChild(new Html("&nbsp;"));
		div1.setStyle("display: inline-block; border-left: 1px dotted #888888;margin: 5px 2px 0px 2px;");
		hlyaout.appendChild(div1);

		Button zoom = new Button();
		zoom.setImage(ThemeManager.getThemeResource("images/" + "Zoom16.png"));
		zoom.setClass("btn-small");
		zoom.setId(String.valueOf(p_JP_ToDo_ID));
		zoom.addEventListener(Events.ON_CLICK, this);
		hlyaout.appendChild(zoom);

		Div div2 = new Div();
		div2.appendChild(new Html("&nbsp;"));
		div2.setStyle("display: inline-block; border-left: 1px dotted #888888;margin: 5px 2px 0px 2px;");
		hlyaout.appendChild(div2);

		String imageLeft = "MoveLeft16.png";
		String imageRight = "MoveRight16.png";

		Button leftBtn = new Button();
		leftBtn.setImage(ThemeManager.getThemeResource("images/" + imageLeft));
		leftBtn.setClass("btn-small");
		leftBtn.setName("Pre");
		leftBtn.addEventListener(Events.ON_CLICK, this);
		hlyaout.appendChild(leftBtn);

		Button rightBtn = new Button();
		rightBtn.setImage(ThemeManager.getThemeResource("images/" + imageRight));
		rightBtn.setClass("btn-small");
		rightBtn.addEventListener(Events.ON_CLICK, this);
		rightBtn.setName("Next");
		hlyaout.appendChild(rightBtn);

		Div div3 = new Div();
		div3.appendChild(new Html("&nbsp;"));
		div3.setStyle("display: inline-block; border-left: 1px dotted #888888;margin: 5px 2px 0px 2px;");
		hlyaout.appendChild(div3);

		return north;

	}

	private Center updateCenter()
	{
		if(center.getFirstChild() != null)
			center.getFirstChild().detach();

		Div centerContent = new Div();
		center.appendChild(centerContent);
		ZKUpdateUtil.setVflex(center, "min");

		Grid grid = GridFactory.newGridLayout();
		ZKUpdateUtil.setVflex(grid, "min");
		ZKUpdateUtil.setHflex(grid, "1");
		centerContent.appendChild(grid);

		Rows rows = grid.newRows();

		//*** AD_User_ID ***//
		Div div_Label = createLabelDiv(map_Label.get(MToDo.COLUMNNAME_AD_User_ID), true);
		WEditor editor = map_Editor.get(MToDo.COLUMNNAME_AD_User_ID);
		if(p_IsNewRecord)
		{
			editor.setValue(p_AD_User_ID);
		}else {
			editor.setValue(p_AD_User_ID);
		}
		Row row = rows.newRow();
		rows.appendChild(row);
		row.appendCellChild(div_Label,2);
		row.appendCellChild(editor.getComponent(),4);


		//*** JP_ToDo_Type ***//
		div_Label = createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_Type), true);
		editor = map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Type);
		if(p_IsNewRecord)
		{
			editor.setValue(p_JP_ToDo_Type);
		}else {
			editor.setValue(p_MToDo.getJP_ToDo_Type());
		}
		row = rows.newRow();
		row.appendCellChild(div_Label,2);
		row.appendCellChild(editor.getComponent(),4);


		//*** JP_ToDo_Category_ID ***//
		div_Label = createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_Category_ID), false);
		editor = map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Category_ID);
		if(p_IsNewRecord)
		{
			;
		}else {
			editor.setValue(p_MToDo.getJP_ToDo_Category_ID());
		}
		row = rows.newRow();
		row.appendCellChild(div_Label,2);
		row.appendCellChild(editor.getComponent(),4);


		//*** Name ***//
		div_Label = createLabelDiv(map_Label.get(MToDo.COLUMNNAME_Name), true);
		editor =  map_Editor.get(MToDo.COLUMNNAME_Name);
		if(p_IsNewRecord)
		{
			;
		}else {
			editor.setValue(p_MToDo.getName());
		}
		row = rows.newRow();
		row.appendCellChild(div_Label,2);
		row.appendCellChild(editor.getComponent(),4);


		//*** Description ***//
		div_Label = createLabelDiv(map_Label.get(MToDo.COLUMNNAME_Description), false);
		editor =  map_Editor.get(MToDo.COLUMNNAME_Description);
		if(p_IsNewRecord)
		{
			;
		}else {
			editor.setValue(p_MToDo.getDescription());
		}
		row = rows.newRow();
		row.appendCellChild(div_Label,2);
		row.appendCellChild(editor.getComponent(),4);


		//*** Comments ***//
		div_Label = createLabelDiv(map_Label.get(MToDo.COLUMNNAME_Comments), false);
		editor =  map_Editor.get(MToDo.COLUMNNAME_Comments);
		if(p_IsNewRecord)
		{
			;
		}else {
			editor.setValue(p_MToDo.getComments());
		}
		row = rows.newRow();
		row.appendCellChild(div_Label,2);
		row.appendCellChild(editor.getComponent(),4);


		//*** JP_ToDo_ScheduledStartTime ***//
		div_Label = createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime), true);
		editor =  map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime);
		if(p_IsNewRecord)
		{
			editor.setValue(p_SelectedDate);
		}else {
			editor.setValue(p_MToDo.getJP_ToDo_ScheduledStartTime());
		}
		row = rows.newRow();
		row.appendCellChild(div_Label,2);
		row.appendCellChild(editor.getComponent(),4);


		//*** JP_ToDo_ScheduledEndTime ***//
		div_Label = createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime), true);
		editor =  map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime);
		if(p_IsNewRecord)
		{
			editor.setValue(p_SelectedDate);
		}else {
			editor.setValue(p_MToDo.getJP_ToDo_ScheduledEndTime());
		}
		row = rows.newRow();
		row.appendCellChild(div_Label,2);
		row.appendCellChild(editor.getComponent(),4);


		//*** JP_ToDo_Status ***//
		div_Label = createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_ToDo_Status), true);
		editor =  map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Status);
		if(p_IsNewRecord)
		{
			editor.setValue(MToDo.JP_TODO_STATUS_NotYetStarted);
		}else {
			editor.setValue(p_MToDo.getJP_ToDo_Status());
		}
		row = rows.newRow();
		row.appendCellChild(div_Label,2);
		row.appendCellChild(editor.getComponent(),2);


		//*** IsOpenToDoJP ***//
		editor =  map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_YesNo);
		if(p_IsNewRecord)
		{
			editor.setValue("Y");
		}else {
			editor.setValue(p_MToDo.isOpenToDoJP()==true ? "Y" : "N");
		}

		Div div_IsOpenToDoJP = new Div();
		div_IsOpenToDoJP.appendChild(editor.getComponent());
		row.appendCellChild(div_IsOpenToDoJP,2);


		/********************************************************************************************
		 * Statistics Info
		 ********************************************************************************************/

		row = rows.newRow();
		Groupbox statisticsInfo_GroupBox = new Groupbox();
		statisticsInfo_GroupBox.setOpen(true);
		row.appendCellChild(statisticsInfo_GroupBox,6);
		statisticsInfo_GroupBox.appendChild(new Caption(Msg.getMsg(Env.getCtx(),"JP_StatisticsInfo")));
		Grid statisticsInfo_Grid  = GridFactory.newGridLayout();
		statisticsInfo_Grid.setStyle("background-color: #E9F0FF");
		statisticsInfo_Grid.setStyle("border: none");
		statisticsInfo_GroupBox.appendChild(statisticsInfo_Grid);

		Rows statisticsInfo_rows = statisticsInfo_Grid.newRows();


		/** JP_Statistics_YesNo  **/
		div_Label = createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_Statistics_YesNo), false);
		editor =  map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_YesNo);
		if(p_IsNewRecord)
		{

		}else {
			editor.setValue(p_MToDo.getJP_Statistics_YesNo());
		}
		row = statisticsInfo_rows.newRow();
		row.appendCellChild(div_Label,2);
		row.appendCellChild(editor.getComponent(),4);


		/** JP_Statistics_Choice **/
		div_Label = createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_Statistics_Choice), false);
		editor =  map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_Choice);
		if(p_IsNewRecord)
		{

		}else {
			editor.setValue(p_MToDo.getJP_Statistics_Choice());
		}
		row = statisticsInfo_rows.newRow();
		row.appendCellChild(div_Label,2);
		row.appendCellChild(editor.getComponent(),4);


		/** JP_Statistics_DateAndTime **/
		div_Label = createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_Statistics_DateAndTime), false);
		editor =  map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_DateAndTime);
		if(p_IsNewRecord)
		{

		}else {
			editor.setValue(p_MToDo.getJP_Statistics_DateAndTime());
		}
		row = statisticsInfo_rows.newRow();
		row.appendCellChild(div_Label,2);
		row.appendCellChild(editor.getComponent(),4);


		//TODO
		/** JP_Statistics_Number **/
		div_Label = createLabelDiv(map_Label.get(MToDo.COLUMNNAME_JP_Statistics_Number), false);
		editor =  map_Editor.get(MToDo.COLUMNNAME_JP_Statistics_Number);
		if(p_IsNewRecord)
		{

		}else {
			editor.setValue(p_MToDo.getJP_Statistics_Number());
		}
		row = statisticsInfo_rows.newRow();
		row.appendCellChild(div_Label,2);
		row.appendCellChild(editor.getComponent(),4);


		return center;
	}

	public void onEvent(Event e) throws Exception
	{
		Component comp = e.getTarget();
		Object list_index = comp.getAttribute("index");
		String eventName = e.getName();

		if (e.getTarget() == confirmPanel.getButton(ConfirmPanel.A_OK))
		{

			if(!p_IsUpdatable)
			{
				return;
			}

			if(p_JP_ToDo_ID == 0)
				p_MToDo = new MToDo(Env.getCtx(), 0, null);

			p_MToDo.setAD_Org_ID(0);

			//Check AD_User_ID
			WEditor editor = map_Editor.get(MToDo.COLUMNNAME_AD_User_ID);
			if(editor.getValue() == null || ((Integer)editor.getValue()).intValue() == 0)
			{
				FDialog.error(0, this, Msg.translate(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_AD_User_ID));
				return ;
			}else {
				p_MToDo.setAD_User_ID((Integer)editor.getValue());
			}

			//Check JP_ToDo_Type
			editor = map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Type);
			if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
			{
				FDialog.error(0, this, Msg.translate(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_ToDo_Type));
				return ;
			}else {
				p_MToDo.setJP_ToDo_Type((String)editor.getValue());
			}

			//Check JP_ToDo_Category_ID
			editor = map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Category_ID);
			if(editor.getValue() == null || ((Integer)editor.getValue()).intValue() == 0)
			{
				;
			}else {
				p_MToDo.setJP_ToDo_Category_ID((Integer)editor.getValue());
			}

			//Check Name
			editor = map_Editor.get(MToDo.COLUMNNAME_Name);
			if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
			{
				FDialog.error(0, this, Msg.translate(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_Name));
				return ;
			}else {
				p_MToDo.setName((String)editor.getValue());
			}

			//Check Description
			editor = map_Editor.get(MToDo.COLUMNNAME_Description);
			if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
			{

			}else {
				p_MToDo.setDescription(editor.getValue().toString());
			}

			//Check Comments
			editor = map_Editor.get(MToDo.COLUMNNAME_Comments);
			if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
			{

			}else {
				p_MToDo.setComments(editor.getValue().toString());
			}

			//Check JP_ToDo_ScheduledStartTime
			editor = map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime);
			p_MToDo.setJP_ToDo_ScheduledStartTime((Timestamp)editor.getValue());

			//Check JP_ToDo_ScheduledEndTime
			editor = map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime);
			p_MToDo.setJP_ToDo_ScheduledEndTime((Timestamp)editor.getValue());

			//Check JP_ToDo_Status
			editor = map_Editor.get(MToDo.COLUMNNAME_JP_ToDo_Status);
			if(editor.getValue() == null || Util.isEmpty(editor.getValue().toString()))
			{
				FDialog.error(0, this, Msg.translate(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_ToDo_Status));
				return ;

			}else {
				p_MToDo.setJP_ToDo_Status(editor.getValue().toString());
			}



			String msg = p_MToDo.beforeSavePreCheck(true);
			if(!Util.isEmpty(msg))
			{
				FDialog.error(0, this, msg);
				return;
			}

			if (p_MToDo.save())
			{
				if (log.isLoggable(Level.FINE)) log.fine("R_Request_ID=" + p_MToDo.getJP_ToDo_ID());

				//Events.postEvent("onRefresh", parent, null);
				i_CallPersonalToDoPopupwindow.createContents();

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
