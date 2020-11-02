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


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Mask;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.event.ActionEvent;
import org.adempiere.webui.event.ActionListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.factory.ButtonFactory;
import org.adempiere.webui.part.WindowContainer;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.MColumn;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Center;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.North;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.South;
import org.zkoss.zul.ext.Sortable;

import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoReminder;
import jpiere.plugin.groupware.model.MToDoTeam;



/**
 *
 * JPIERE-0473 Personal ToDo List Popup Window
 *
 *
 * @author h.hagiwara
 *
 */
public class PersonalToDoListWindow extends Window implements EventListener<Event>, WTableModelListener, ActionListener, Sortable<Object>
{
	/**	Logger			*/
	protected CLogger log = CLogger.getCLogger(getClass());
	private Properties ctx = null;

	private Borderlayout layout = null;

	private ToDoPopupWindow todoPopupWindow = null;
	private MToDoTeam m_TeamToDo = null;

	private final static String BUTTON_NAME_ZOOM_PERSONALTODO = "ZOOM_P";
	private Button zoomPersonalToDoBtn = null;

	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = 304878472233552113L;

	public PersonalToDoListWindow(ToDoPopupWindow todoPopupWindow, MToDoTeam todoTeam) throws Exception
	{
		ctx = Env.getCtx();

		this.todoPopupWindow = todoPopupWindow;
		this.m_TeamToDo = todoTeam;

		addEventListener(WindowContainer.ON_WINDOW_CONTAINER_SELECTION_CHANGED_EVENT, this);
		addEventListener(Events.ON_CLOSE, this);

		setTitle(m_TeamToDo.getName());
		setAttribute(Window.MODE_KEY, Window.MODE_HIGHLIGHTED);
		setBorder("normal");
		setClosable(true);
		int height = SessionManager.getAppDesktop().getClientInfo().desktopHeight * 80 / 100;
		int width = SessionManager.getAppDesktop().getClientInfo().desktopWidth * 80 / 100;
		ZKUpdateUtil.setWidth(this, width + "px");
		ZKUpdateUtil.setHeight(this, height + "px");
		this.setContentStyle("overflow: auto");


		layout = new Borderlayout();
		this.appendChild(layout);


		//North
		North noth = new North();
		layout.appendChild(noth);


		//Center
		Center center = new Center();
		layout.appendChild(center);


		StringBuilder sqlSELECT = new StringBuilder("SELECT ");
		StringBuilder sqlFROM = new StringBuilder(" FROM JP_ToDo ");
		StringBuilder sqlWHERE = new StringBuilder(" WHERE JP_ToDo.JP_ToDo_Team_ID = ? ");
		StringBuilder sqlOrder = new StringBuilder(" ORDER BY JP_ToDo.AD_User_ID");

		//JP_ToDo_ID(1)
		sqlSELECT.append(" JP_ToDo.JP_ToDo_ID");

		//User(2)
		String eSql = MLookupFactory.getLookup_TableDirEmbed(Env.getLanguage(ctx), MToDo.COLUMNNAME_AD_User_ID, MToDo.Table_Name);
		sqlSELECT.append(", (").append(eSql).append(") AS User");

		//Comments(3)
		sqlSELECT.append(", JP_ToDo.Comments");

		MColumn m_Column = MColumn.get(ctx, MToDo.Table_Name, MToDo.COLUMNNAME_JP_ToDo_Status);
		int AD_Reference_Value_ID = m_Column.getAD_Reference_Value_ID();

		//ToDo Status(4)
		if (Env.isBaseLanguage(Env.getLanguage(ctx), "AD_Ref_List"))
		{
			sqlSELECT.append(", AD_Ref_List.Name AS JP_ToDo_Status ");

			sqlFROM.append(" LEFT OUTER JOIN  AD_Ref_List ON (JP_ToDo.JP_ToDo_Status = AD_Ref_List.Value AND AD_Ref_List.AD_Reference_ID = ?) ");
		}
		else
		{

			sqlSELECT.append(", AD_Ref_List_Trl.Name AS JP_ToDo_Status ");

			sqlFROM.append(" LEFT OUTER JOIN AD_Ref_List ON (JP_ToDo.JP_ToDo_Status = AD_Ref_List.Value AND AD_Ref_List.AD_Reference_ID = ?) ");
			sqlFROM.append(" LEFT OUTER JOIN AD_Ref_List_Trl ON (AD_Ref_List.AD_Ref_List_ID = AD_Ref_List_Trl.AD_Ref_List_ID AND AD_Ref_List_Trl.AD_Language = ?) ");
		}


		//JP_Statistics_YesNo(5)
		sqlSELECT.append(", JP_ToDo.JP_Statistics_YesNo AS JP_Statistics_YesNo ");

		//JP_Statistics_Choice(6)
		sqlSELECT.append(", JP_ToDo.JP_Statistics_Choice AS JP_Statistics_Choice ");

		//JP_Statistics_DateAndTime(7)
		sqlSELECT.append(", JP_ToDo.JP_Statistics_DateAndTime AS JP_Statistics_DateAndTime ");

		//JP_Statistics_DateAndTime(8)
		sqlSELECT.append(", JP_ToDo.JP_Statistics_Number AS JP_Statistics_Number ");

		StringBuilder sql = sqlSELECT.append(sqlFROM).append(sqlWHERE).append(sqlOrder);


		ArrayList<PersonalToDoModel> list = new ArrayList<PersonalToDoModel>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			pstmt.setInt(1, AD_Reference_Value_ID);
			if (Env.isBaseLanguage(Env.getLanguage(ctx), "AD_Ref_List"))
			{
				pstmt.setInt(2, m_TeamToDo.get_ID());
			}else {
				pstmt.setString(2, Env.getAD_Language(ctx));
				pstmt.setInt(3, m_TeamToDo.get_ID());
			}

			rs = pstmt.executeQuery();

			PersonalToDoModel todo = null;
			while (rs.next())
			{
				todo = new PersonalToDoModel();
				todo.JP_ToDo_ID = rs.getInt(1);
				todo.user = rs.getString(2);
				todo.comments = rs.getString(3);
				todo.status = rs.getString(4);

				todo.JP_Statistics_YesNo = rs.getString(5);
				todo.JP_Statistics_Choice = rs.getString(6);
				todo.JP_Statistics_DateAndTime = rs.getTimestamp(7);
				todo.JP_Statistics_Number = rs.getBigDecimal(8);

				list.add(todo);
			}

		}catch (Exception e){

			throw e;

		}finally{

			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		if(list.size()==0)
		{
			center.appendChild(new Label(Msg.getMsg(ctx, "not.found")));
			return ;
		}

		Grid grid = new Grid();
		ZKUpdateUtil.setVflex(grid, true);
		grid.setMold("paging");
		grid.setPageSize(20);
		grid.setPagingPosition("bottom");
		center.appendChild(grid);

		org.zkoss.zul.Columns columns = new Columns();
		grid.appendChild(columns);

		org.zkoss.zul.Column column = new Column();
		columns.appendChild(column);
		column.setLabel("");
		column.setWidth("22px");
		Radiogroup radioGroup = new Radiogroup();
		column.appendChild(radioGroup);

		//Reminder
		column = new Column();
		columns.appendChild(column);
		column.setLabel("");
		column.setWidth("30px");

		//User
		column = new Column();
		columns.appendChild(column);
		column.setLabel(Msg.getElement(ctx, MToDo.COLUMNNAME_AD_User_ID));
		column.setWidth("20%");

		//ToDo Status
		column = new Column();
		columns.appendChild(column);
		column.setLabel(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Status));
		column.setWidth("10%");

		if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_None.equals(m_TeamToDo.getJP_Mandatory_Statistics_Info()))
		{
			;//Noting To Do
		}else {

			column = new Column();
			columns.appendChild(column);
			column.setLabel(Msg.getElement(ctx, MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info));
			column.setWidth("10%");
		}

		//Comments
		column = new Column();
		columns.appendChild(column);
		column.setLabel(Msg.getElement(ctx, MToDo.COLUMNNAME_Comments));


		PersonalToDoListModel listModel = new PersonalToDoListModel(list);
		grid.setModel(listModel);

		PersonalToDoListRowRenderer rowRenderer = new PersonalToDoListRowRenderer(this, radioGroup);
		grid.setRowRenderer(rowRenderer);


		//South
		South south = new South();
		layout.appendChild(south);

		Grid southContent = GridFactory.newGridLayout();
		south.appendChild(southContent);

		columns = new Columns();
		southContent.appendChild(columns);
		for(int i = 0; i < 6; i++)
			columns.appendChild(new Column());

		Rows southContentRows = new Rows();
		southContent.appendChild(southContentRows);

		Row row1 = new Row();
		southContentRows.appendChild(row1);

		zoomPersonalToDoBtn = ButtonFactory.createButton(Msg.getMsg(Env.getCtx(), "Zoom"), null, "");
		if (ThemeManager.isUseFontIconForImage())
			zoomPersonalToDoBtn.setIconSclass("z-icon-Zoom");
		else
			zoomPersonalToDoBtn.setImage(ThemeManager.getThemeResource("images/Zoom24.png"));
		//zoomPersonalToDoBtn.setClass("btn-small");
		zoomPersonalToDoBtn.setName(BUTTON_NAME_ZOOM_PERSONALTODO);
		zoomPersonalToDoBtn.setTooltiptext(Msg.getMsg(ctx, "JP_Zoom_To_PersonalToDo"));
		zoomPersonalToDoBtn.addEventListener(Events.ON_CLICK, this);
		zoomPersonalToDoBtn.setDisabled(true);

		ZKUpdateUtil.setHflex(zoomPersonalToDoBtn, "1");
		row1.appendChild(zoomPersonalToDoBtn);

		return ;

	}

	public MToDoTeam getMToDoTeam()
	{
		return m_TeamToDo;
	}

	@Override
	public void tableChanged(WTableModelEvent event)
	{
		;
	}

	private int JP_ToDO_ID = 0;
	public void setJP_ToDo_ID(int JP_ToDo_ID)
	{
		this.JP_ToDO_ID = JP_ToDo_ID;
		if(JP_ToDo_ID == 0)
		{
			zoomPersonalToDoBtn.setDisabled(true);
		}else {
			zoomPersonalToDoBtn.setDisabled(false);
		}
	}

	@Override
	public void onEvent(Event event) throws Exception
	{

		Component comp = event.getTarget();

		if(event.getName().equals(Events.ON_CLOSE))
		{
			dispose();

		}else if(Events.ON_CHECK.equals(event.getName())) {

			if(comp instanceof Radio)
			{
				setJP_ToDo_ID(((Integer)comp.getAttribute(MToDo.COLUMNNAME_JP_ToDo_ID)).intValue());
			}

		}else if(Events.ON_CLICK.equals(event.getName())) {

			if(comp instanceof Button)
			{
				Button btn = (Button)comp;

				if(btn.getName().equals(PersonalToDoListWindow.BUTTON_NAME_ZOOM_PERSONALTODO))
				{
					AEnv.zoom(MTable.getTable_ID(MToDo.Table_Name), JP_ToDO_ID);
					dispose();
					todoPopupWindow.detach();
				}else if(btn.getName().equals(ToDoPopupWindow.BUTTON_NAME_REMINDER)){

					createReminderPopupWindow(btn);

				}else if(btn.getName().equals(ToDoPopupWindow.BUTTON_NEW_REMINDER)) {//TODO

					int JP_ToDo_ID  =  Integer.parseInt(btn.getAttribute(MToDo.COLUMNNAME_JP_ToDo_ID).toString());

					ReminderPopupWindow rpw = new ReminderPopupWindow(this, new MToDo(ctx, JP_ToDo_ID, null), 0);
					this.appendChild(rpw);
					if (ClientInfo.isMobile())
					{
						rpw.doHighlighted();
					}
					else
					{
						showBusyMask(this);
						LayoutUtils.openOverlappedWindow(this, rpw, "middle_center");
						rpw.focus();
					}

					reminderPopup.close();

				}else if(btn.getName().equals(ToDoPopupWindow.BUTTON_UPDATE_REMINDER)) {//TODO

					int reminder_ID = Integer.parseInt(btn.getAttribute(MToDoReminder.COLUMNNAME_JP_ToDo_Reminder_ID).toString());
					int JP_ToDo_ID  =  Integer.parseInt(btn.getAttribute(MToDo.COLUMNNAME_JP_ToDo_ID).toString());

					ReminderPopupWindow rpw = new ReminderPopupWindow(this, new MToDo(ctx, JP_ToDo_ID, null), reminder_ID);
					this.appendChild(rpw);
					if (ClientInfo.isMobile())
					{
						rpw.doHighlighted();
					}
					else
					{
						showBusyMask(this);
						LayoutUtils.openOverlappedWindow(this, rpw, "middle_center");
						rpw.focus();
					}

					reminderPopup.close();
				}
			}

		}
	}

	public void showBusyMask(Window window)
	{
		appendChild(getMask());
		StringBuilder script = new StringBuilder("var w=zk.Widget.$('#");
		script.append(getUuid()).append("');");
		if (window != null) {
			script.append("var d=zk.Widget.$('#").append(window.getUuid()).append("');w.busy=d;");
		} else {
			script.append("w.busy=true;");
		}
		Clients.response(new AuScript(script.toString()));
	}

	private Mask mask = null;
	private Div getMask()
	{
		if (mask == null) {
			mask = new Mask();
		}
		return mask;
	}

	public void hideBusyMask()
	{
		if (mask != null && mask.getParent() != null) {
			mask.detach();
			StringBuilder script = new StringBuilder("var w=zk.Widget.$('#");
			script.append(getUuid()).append("');if(w) w.busy=false;");
			Clients.response(new AuScript(script.toString()));
		}
	}

	Popup reminderPopup = null;

	private void createReminderPopupWindow(Button reminderBtn)
	{
		int JP_ToDo_ID  =  Integer.parseInt(reminderBtn.getAttribute(MToDo.COLUMNNAME_JP_ToDo_ID).toString());

		reminderPopup = new Popup();
		reminderPopup.setWidgetAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "reminderButtonPopup");

		org.adempiere.webui.component.Grid grid = GridFactory.newGridLayout();
		ZKUpdateUtil.setVflex(grid, "min");
		ZKUpdateUtil.setHflex(grid, "min");
		reminderPopup.appendChild(grid);
		Rows rows = grid.newRows();
		Row row = rows.newRow();

		//Create New Reminder Button
		Button btn = new Button();
		if (ThemeManager.isUseFontIconForImage())
			btn.setIconSclass("z-icon-New");
		else
			btn.setImage(ThemeManager.getThemeResource("images/New16.png"));
		btn.setClass("btn-small");
		btn.setStyle("text-align: left");
		btn.setName(ToDoPopupWindow.BUTTON_NEW_REMINDER);
		btn.setLabel(Msg.getMsg(ctx, "JP_ToDo_Reminder_Create"));//Create ToDo Reminder
		btn.setAttribute(MToDo.COLUMNNAME_JP_ToDo_ID, JP_ToDo_ID);
		btn.addEventListener(Events.ON_CLICK, this);
		row.appendCellChild(btn);

		//Get Reminders
		SimpleDateFormat sdfV = DisplayType.getDateFormat();
		String whereClause = " JP_ToDo_ID=? ";
		String orderClause ="JP_ToDo_RemindTime";

		List<MToDoReminder> list = new Query(ctx, MToDoReminder.Table_Name, whereClause, null)
				.setParameters(JP_ToDo_ID)
				.setOrderBy(orderClause)
				.list();

		Timestamp remindTime = null;
		for(MToDoReminder reminder : list)
		{
			row = rows.newRow();

			btn = new Button();
			if (ThemeManager.isUseFontIconForImage())
				btn.setIconSclass("z-icon-Request");
			else
				btn.setImage(ThemeManager.getThemeResource("images/Request16.png"));
			btn.setClass("btn-small");
			btn.setStyle("text-align: left");
			btn.setName(ToDoPopupWindow.BUTTON_UPDATE_REMINDER);
			remindTime = reminder.getJP_ToDo_RemindTime();
			btn.setLabel(sdfV.format(remindTime) + " " + remindTime.toLocalDateTime().toLocalTime().toString().substring(0, 5));
			btn.setAttribute(MToDoReminder.COLUMNNAME_JP_ToDo_Reminder_ID, reminder.getJP_ToDo_Reminder_ID());
			btn.setAttribute(MToDo.COLUMNNAME_JP_ToDo_ID, JP_ToDo_ID);
			btn.addEventListener(Events.ON_CLICK, this);

			ZKUpdateUtil.setHflex(btn, "true");
			row.appendCellChild(btn);
		}


		reminderPopup.setPage(reminderBtn.getPage());
		reminderPopup.open(reminderBtn, "after_start");

		return ;

	}


    public void dispose()
    {
        this.detach();
    }   //  dispose


	@Override
	public void actionPerformed(ActionEvent event)
	{
		;
	}

	@Override
	public void sort(Comparator<Object> cmpr, boolean ascending)
	{
		;
	}



	@Override
	public String getSortDirection(Comparator<Object> cmpr)
	{
		return null;
	}

}

