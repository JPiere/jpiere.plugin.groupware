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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Properties;

import org.adempiere.webui.ClientInfo;
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
import org.compiere.util.CLogger;
import org.compiere.util.DB;
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
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.South;
import org.zkoss.zul.ext.Sortable;

import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoReminder;
import jpiere.plugin.groupware.model.MToDoTeam;
import jpiere.plugin.groupware.model.MToDoTeamReminder;
import jpiere.plugin.groupware.util.GroupwareToDoUtil;



/**
 *
 * JPIERE-0480 Personal ToDo Reminder List Popup Window
 *
 *
 * @author h.hagiwara
 *
 */
public class PersonalToDoReminderListWindow extends Window implements EventListener<Event>, WTableModelListener, ActionListener, Sortable<Object>
{
	/**	Logger			*/
	protected CLogger log = CLogger.getCLogger(getClass());
	private Properties ctx = null;

	private Borderlayout layout = null;

	private ReminderPopupWindow reminderPopupWindow = null;
	private MToDoTeamReminder m_TeamToDoReminder = null;

	private final static String BUTTON_NAME_ZOOM_PERSONALTODO = "ZOOM_P";
	private Button zoomPersonalToDoBtn = null;

	private boolean mobile = false;

	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = 304878472233552113L;

	public PersonalToDoReminderListWindow(ReminderPopupWindow reminderPopupWindow, MToDoTeamReminder todoTeamReminder) throws Exception
	{
		ctx = Env.getCtx();
		mobile = ClientInfo.isMobile();

		this.reminderPopupWindow = reminderPopupWindow;
		this.m_TeamToDoReminder = todoTeamReminder;

		addEventListener(WindowContainer.ON_WINDOW_CONTAINER_SELECTION_CHANGED_EVENT, this);
		addEventListener(Events.ON_CLOSE, this);

		setTitle(Msg.getMsg(ctx, "JP_ToDo_PersonalToDoReminderList") + " : " + GroupwareToDoUtil.trimName(m_TeamToDoReminder.getJP_ToDo_Team().getName()));
		setAttribute(Window.MODE_KEY, Window.MODE_HIGHLIGHTED);
		setBorder("normal");
		setClosable(true);

		if(mobile)
		{
			ZKUpdateUtil.setWindowWidthX(this,  SessionManager.getAppDesktop().getClientInfo().desktopWidth);
			ZKUpdateUtil.setWindowHeightX(this,  SessionManager.getAppDesktop().getClientInfo().desktopHeight);
		}else {
			int height = SessionManager.getAppDesktop().getClientInfo().desktopHeight * 80 / 100;
			int width = SessionManager.getAppDesktop().getClientInfo().desktopWidth * 80 / 100;
			ZKUpdateUtil.setWidth(this, width + "px");
			ZKUpdateUtil.setHeight(this, height + "px");
		}
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
		StringBuilder sqlFROM = new StringBuilder(" FROM JP_ToDo_Reminder INNER JOIN JP_ToDo ON (JP_ToDo_Reminder.JP_ToDo_ID = JP_ToDo.JP_ToDo_ID) ");
		StringBuilder sqlWHERE = new StringBuilder(" WHERE JP_ToDo_Reminder.JP_ToDo_Team_Reminder_ID = ? ");
		StringBuilder sqlOrder = new StringBuilder(" ORDER BY JP_ToDo.AD_User_ID ");

		//JP_ToDo_Reminder_ID(1)
		sqlSELECT.append(" JP_ToDo_Reminder.JP_ToDo_Reminder_ID");

		//User(2)
		String eSql = MLookupFactory.getLookup_TableDirEmbed(Env.getLanguage(ctx), MToDo.COLUMNNAME_AD_User_ID, MToDo.Table_Name);
		sqlSELECT.append(", (").append(eSql).append(") AS User");

		//Comments(3)
		sqlSELECT.append(", JP_ToDo_Reminder.Comments");

		//ToDoStatus(4)
		MColumn m_Column = MColumn.get(ctx, MToDo.Table_Name, MToDo.COLUMNNAME_JP_ToDo_Status);
		int AD_Reference_Value_ID = m_Column.getAD_Reference_Value_ID();

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

		//IsSentReminderJP(5)
		sqlSELECT.append(", JP_ToDo_Reminder.IsSentReminderJP AS IsSentReminderJP ");

		//IsConfirmed(6)
		sqlSELECT.append(", JP_ToDo_Reminder.IsConfirmed AS IsConfirmed ");

		//JP_Confirmed(7)
		sqlSELECT.append(", JP_ToDo_Reminder.JP_Confirmed AS JP_Confirmed ");

		//Processed(8)
		sqlSELECT.append(", JP_ToDo_Reminder.Processed AS Processed ");

		//JP_Statistics_YesNo(9)
		sqlSELECT.append(", JP_ToDo_Reminder.JP_Statistics_YesNo AS JP_Statistics_YesNo ");

		//JP_Statistics_Choice(10)
		sqlSELECT.append(", JP_ToDo_Reminder.JP_Statistics_Choice AS JP_Statistics_Choice ");

		//JP_Statistics_DateAndTime(11)
		sqlSELECT.append(", JP_ToDo_Reminder.JP_Statistics_DateAndTime AS JP_Statistics_DateAndTime ");

		//JP_Statistics_DateAndTime(12)
		sqlSELECT.append(", JP_ToDo_Reminder.JP_Statistics_Number AS JP_Statistics_Number ");

		StringBuilder sql = sqlSELECT.append(sqlFROM).append(sqlWHERE).append(sqlOrder);


		ArrayList<PersonalToDoReminderModel> list = new ArrayList<PersonalToDoReminderModel>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			pstmt.setInt(1, AD_Reference_Value_ID);
			if (Env.isBaseLanguage(Env.getLanguage(ctx), "AD_Ref_List"))
			{
				pstmt.setInt(2, m_TeamToDoReminder.get_ID());
			}else {
				pstmt.setString(2, Env.getAD_Language(ctx));
				pstmt.setInt(3, m_TeamToDoReminder.get_ID());
			}

			rs = pstmt.executeQuery();

			PersonalToDoReminderModel todo = null;
			while (rs.next())
			{
				todo = new PersonalToDoReminderModel();
				todo.JP_ToDo_Reminder_ID = rs.getInt(1);
				todo.user = rs.getString(2);
				todo.comments = rs.getString(3);
				todo.status = rs.getString(4);

				todo.IsSentReminderJP = "Y".equals(rs.getString(5));
				todo.IsConfirmed = "Y".equals(rs.getString(6));
				todo.JP_Confirmed = rs.getTimestamp(7);
				todo.Processed = "Y".equals(rs.getString(8));

				todo.JP_Statistics_YesNo = rs.getString(9);
				todo.JP_Statistics_Choice = rs.getString(10);
				todo.JP_Statistics_DateAndTime = rs.getTimestamp(11);
				todo.JP_Statistics_Number = rs.getBigDecimal(12);

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

		//icon
		//column = new Column();
		//columns.appendChild(column);
		//column.setLabel("");
		//column.setWidth("30px");

		//User
		column = new Column();
		columns.appendChild(column);
		column.setLabel(Msg.getElement(ctx, MToDo.COLUMNNAME_AD_User_ID));
		ZKUpdateUtil.setHflex(column, "min");;

		//ToDo Status
		column = new Column();
		columns.appendChild(column);
		column.setLabel(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_Status));
		ZKUpdateUtil.setHflex(column, "min");

		//IsSentReminderJP
		column = new Column();
		columns.appendChild(column);
		column.setLabel(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_IsSentReminderJP));
		ZKUpdateUtil.setHflex(column, "min");

		//IsConfirmed
		column = new Column();
		columns.appendChild(column);
		column.setLabel(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_IsConfirmed));
		ZKUpdateUtil.setHflex(column, "min");

		//JP_Confirmed
		column = new Column();
		columns.appendChild(column);
		column.setLabel(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_Confirmed));
		ZKUpdateUtil.setHflex(column, "min");

		//Processed
		column = new Column();
		columns.appendChild(column);
		column.setLabel(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_Processed));
		ZKUpdateUtil.setHflex(column, "min");

		//Comments
		column = new Column();
		columns.appendChild(column);
		column.setLabel(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_ToDo_Reminder_ID) + " - " + Msg.getElement(ctx, MToDoReminder.COLUMNNAME_Comments));

		PersonalToDoReminderListModel listModel = new PersonalToDoReminderListModel(list);
		grid.setModel(listModel);

		PersonalToDoReminderListRowRenderer rowRenderer = new PersonalToDoReminderListRowRenderer(this, radioGroup);
		grid.setRowRenderer(rowRenderer);

		if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_None.equals(m_TeamToDoReminder.getJP_Mandatory_Statistics_Info()))
		{
			;//Noting To Do
		}else {

			column = new Column();
			columns.appendChild(column);
			column.setLabel(Msg.getElement(ctx, MToDoTeamReminder.COLUMNNAME_JP_Mandatory_Statistics_Info));
			ZKUpdateUtil.setHflex(column, "min");
		}

		//South
		South south = new South();
		layout.appendChild(south);

		Grid southContent = GridFactory.newGridLayout();
		south.appendChild(southContent);

		if(!mobile)
		{
			columns = new Columns();
			southContent.appendChild(columns);
			for(int i = 0; i < 6; i++)
				columns.appendChild(new Column());
		}

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

	public MToDoTeamReminder getMToDoTeamReminder()
	{
		return m_TeamToDoReminder;
	}

	@Override
	public void tableChanged(WTableModelEvent event)
	{
		;
	}

	private int JP_ToDo_Reminder_ID = 0;
	public void setJP_ToDo_Reminder_ID(int JP_ToDo_Reminder_ID)
	{
		this.JP_ToDo_Reminder_ID = JP_ToDo_Reminder_ID;
		if(JP_ToDo_Reminder_ID == 0)
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
				setJP_ToDo_Reminder_ID(((Integer)comp.getAttribute(MToDoReminder.COLUMNNAME_JP_ToDo_Reminder_ID)).intValue());
			}

		}else if(Events.ON_CLICK.equals(event.getName())) {

			if(comp instanceof Button)
			{
				Button btn = (Button)comp;

				if(btn.getName().equals(PersonalToDoReminderListWindow.BUTTON_NAME_ZOOM_PERSONALTODO))
				{
					AEnv.zoom(MTable.getTable_ID(MToDoReminder.Table_Name), JP_ToDo_Reminder_ID);
					dispose();
					ToDoPopupWindow todoPopupWindow = reminderPopupWindow.getToDoPopupWindow();
					reminderPopupWindow.detach();
					todoPopupWindow.onClose();


				}else if(btn.getName().equals(ToDoPopupWindow.BUTTON_NAME_REMINDER)){

//					int JP_ToDo_ID  =  Integer.parseInt(btn.getAttribute(MToDo.COLUMNNAME_JP_ToDo_ID).toString());
//					ReminderMenuPopup reminderMenuPopup = new ReminderMenuPopup(this, new MToDo(Env.getCtx(), JP_ToDo_ID, null));
//					reminderMenuPopup.setPage(btn.getPage());
//					reminderMenuPopup.open(btn,"end_before");

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

