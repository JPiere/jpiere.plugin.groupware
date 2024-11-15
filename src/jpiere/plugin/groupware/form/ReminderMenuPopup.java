package jpiere.plugin.groupware.form;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.factory.ButtonFactory;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Center;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.North;
import org.zkoss.zul.Popup;

import jpiere.plugin.groupware.model.I_ToDo;
import jpiere.plugin.groupware.model.I_ToDoReminder;
import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoReminder;
import jpiere.plugin.groupware.model.MToDoTeam;
import jpiere.plugin.groupware.model.MToDoTeamReminder;
import jpiere.plugin.groupware.window.PersonalToDoListWindow;
import jpiere.plugin.groupware.window.ReminderPopupWindow;
import jpiere.plugin.groupware.window.ToDoPopupWindow;


/**
 * JPIERE-0480 Reminder Menu Popup Window - Reminder List
 *
 *
 * @author h.hagiwara
 *
 */
public class ReminderMenuPopup extends Popup implements EventListener<Event>{

	private static final long serialVersionUID = -717238391369592882L;

	private Properties ctx = Env.getCtx();

	private Button createNewBtn = null;
	public final static String BUTTON_NEW_REMINDER = "NEW_REMINDER";
	public final static String BUTTON_UPDATE_REMINDER = "UPDATE_REMINDER";

	public final static String I_REMINDER = "I_ToDoReminder";

	private ToDoPopupWindow p_TodoPopupWindow = null;
	private PersonalToDoListWindow p_PersonalTodoListWindow = null;

	private I_ToDo p_iToDo;

	public ReminderMenuPopup(PersonalToDoListWindow PersonalTodoListWindow, I_ToDo p_iToDo) throws Exception
	{
		this.p_PersonalTodoListWindow = PersonalTodoListWindow;
		this.p_iToDo = p_iToDo;
		init();

	}

	public ReminderMenuPopup(ToDoPopupWindow todoPopupWindow, I_ToDo p_iToDo) throws Exception
	{
		this.p_TodoPopupWindow = todoPopupWindow;
		this.p_iToDo = p_iToDo;
		init();
	}

	private void init()throws Exception
	{
		ZKUpdateUtil.setVflex(this, "min");
		ZKUpdateUtil.setHflex(this, "min");

		Borderlayout popupContent = new Borderlayout();
		ZKUpdateUtil.setVflex(popupContent, "min");
		ZKUpdateUtil.setHflex(popupContent, "min");
		this.appendChild(popupContent);

		North north = new North();
		ZKUpdateUtil.setVflex(north, "min");
		ZKUpdateUtil.setHflex(north, "min");

		createNewBtn = ButtonFactory.createButton(Msg.getMsg(ctx, "JP_ToDo_Reminder_Create"), null, "");
		if (ThemeManager.isUseFontIconForImage())
			createNewBtn.setIconSclass("z-icon-New");
		else
			createNewBtn.setImage(ThemeManager.getThemeResource("images/New16.png"));
		createNewBtn.setName(BUTTON_NEW_REMINDER);
		createNewBtn.addEventListener(Events.ON_CLICK, this);

		north.appendChild(createNewBtn);
		popupContent.appendChild(north);

		//Get Reminders
		ArrayList<I_ToDoReminder> list = new ArrayList<I_ToDoReminder> ();
		if(MToDo.Table_Name.equals(p_iToDo.get_TableName()))
		{
			StringBuilder sql = new StringBuilder("SELECT * FROM JP_ToDo_Reminder WHERE JP_ToDo_ID=? ORDER BY JP_ToDo_RemindTime ASC");

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql.toString(), null);
				pstmt.setInt(1, p_iToDo.get_ID());
				rs = pstmt.executeQuery();

				while (rs.next())
				{
					I_ToDoReminder model = new MToDoReminder(Env.getCtx(), rs, null);
					list.add(model);
				}

			}catch (Exception e){

				throw e;

			}finally{

				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}


		}else {

			StringBuilder sql = new StringBuilder("SELECT * FROM JP_ToDo_Team_Reminder WHERE JP_ToDo_Team_ID=? ORDER BY JP_ToDo_RemindTime ASC");

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql.toString(), null);
				pstmt.setInt(1, p_iToDo.get_ID());
				rs = pstmt.executeQuery();

				while (rs.next())
				{
					I_ToDoReminder model = new MToDoTeamReminder(Env.getCtx(), rs, null);
					list.add(model);
				}

			}catch (Exception e){

				throw e;

			}finally{

				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}

		}

		if(list.size() == 0)
		{
			return ;
		}

		//Center
		Center center = new Center();
		ZKUpdateUtil.setVflex(center, "min");
		ZKUpdateUtil.setHflex(center, "min");
		popupContent.appendChild(center);

		Grid grid = new Grid();
		ZKUpdateUtil.setVflex(grid, "min");
		ZKUpdateUtil.setHflex(grid, "min");
		grid.setMold("paging");
		grid.setPageSize(10);
		grid.setPagingPosition("top");
		center.appendChild(grid);

		org.zkoss.zul.Columns columns = new Columns();
		columns.setSizable(true);
		grid.appendChild(columns);

		org.zkoss.zul.Column column = null;

		//Reminder Image
		column = new Column();
		columns.appendChild(column);
		column.setLabel("");
		column.setWidth("30px");


		//RemindTime
		column = new Column();
		columns.appendChild(column);
		column.setLabel(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime));

		if(MToDoTeam.Table_Name.equals(p_iToDo.get_TableName()))
		{
			column = new Column();
			columns.appendChild(column);
			column.setLabel(Msg.getElement(ctx, MToDoTeamReminder.COLUMNNAME_JP_ToDo_RemindTarget));

			column = new Column();
			columns.appendChild(column);
			column.setLabel(Msg.getElement(ctx, MToDoTeamReminder.COLUMNNAME_JP_Team_ID));
		}

		//RemindType
		column = new Column();
		columns.appendChild(column);
		column.setLabel(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType));


		//Frequency
		column = new Column();
		columns.appendChild(column);
		ZKUpdateUtil.setWidth(column, "180px");
		column.setLabel(Msg.getElement(ctx, "Frequency"));


		//IsSentReminderJP
		column = new Column();
		columns.appendChild(column);
		column.setLabel(Msg.getMsg(ctx, "JP_Sent"));


		//Processed
		column = new Column();
		columns.appendChild(column);
		column.setLabel(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_Processed));

		//IsConfirmed
		if(MToDo.Table_Name.equals(p_iToDo.get_TableName()))
		{
			column = new Column();
			columns.appendChild(column);
			column.setLabel(Msg.getElement(ctx, MToDoReminder.COLUMNNAME_IsConfirmed));
		}

		ReminderMenuListModel listModel = new ReminderMenuListModel(list);
		grid.setModel(listModel);

		ReminderMenuRowRenderer rowRenderer = new ReminderMenuRowRenderer(this);
		grid.setRowRenderer(rowRenderer);

		return ;
	}


	@Override
	public void onEvent(Event event) throws Exception
	{
		Component comp = event.getTarget();
		Button btn = (Button) comp;
		String btnName = btn.getName();

		if(BUTTON_NEW_REMINDER.equals(btnName))
		{
			ReminderPopupWindow rpw = null;
			if(p_TodoPopupWindow != null)
			{
				rpw = new ReminderPopupWindow(p_TodoPopupWindow, p_iToDo, 0);
				p_TodoPopupWindow.appendChild(rpw);
				if (ClientInfo.isMobile())
				{
					rpw.doHighlighted();
				}
				else
				{
					p_TodoPopupWindow.showBusyMask(rpw);
					LayoutUtils.openOverlappedWindow(p_TodoPopupWindow, rpw, "middle_center");
					rpw.focus();
				}

			}else {

				rpw = new ReminderPopupWindow(p_PersonalTodoListWindow, p_iToDo, 0);
				p_PersonalTodoListWindow.appendChild(rpw);
				if (ClientInfo.isMobile())
				{
					rpw.doHighlighted();
				}
				else
				{
					p_PersonalTodoListWindow.showBusyMask(rpw);
					LayoutUtils.openOverlappedWindow(p_PersonalTodoListWindow, rpw, "middle_center");
					rpw.focus();
				}
			}

		}else if(BUTTON_UPDATE_REMINDER.equals(btnName)) {

			if(p_TodoPopupWindow != null)
			{
				I_ToDoReminder reminder = (I_ToDoReminder)comp.getAttribute(ReminderMenuPopup.I_REMINDER);
				ReminderPopupWindow rpw = new ReminderPopupWindow(p_TodoPopupWindow, p_iToDo, reminder.get_ID());
				p_TodoPopupWindow.appendChild(rpw);

				if (ClientInfo.isMobile())
				{
					rpw.doHighlighted();
				}
				else
				{
					p_TodoPopupWindow.showBusyMask(rpw);
					LayoutUtils.openOverlappedWindow(p_TodoPopupWindow, rpw, "middle_center");
					rpw.focus();
				}

			}else {

				I_ToDoReminder reminder = (I_ToDoReminder)comp.getAttribute(ReminderMenuPopup.I_REMINDER);
				ReminderPopupWindow rpw = new ReminderPopupWindow(p_PersonalTodoListWindow, p_iToDo, reminder.get_ID());
				p_PersonalTodoListWindow.appendChild(rpw);
				if (ClientInfo.isMobile())
				{
					rpw.doHighlighted();
				}
				else
				{
					p_PersonalTodoListWindow.showBusyMask(rpw);
					LayoutUtils.openOverlappedWindow(p_PersonalTodoListWindow, rpw, "middle_center");
					rpw.focus();
				}

			}

		}

		this.close();
	}


}
