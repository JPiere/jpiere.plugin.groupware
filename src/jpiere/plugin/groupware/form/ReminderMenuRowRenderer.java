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

import java.text.SimpleDateFormat;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.theme.ThemeManager;
import org.compiere.model.MColumn;
import org.compiere.model.MRefList;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

import jpiere.plugin.groupware.model.I_ToDoReminder;
import jpiere.plugin.groupware.model.MTeam;
import jpiere.plugin.groupware.model.MToDoReminder;
import jpiere.plugin.groupware.model.MToDoTeamReminder;
import jpiere.plugin.groupware.util.GroupwareToDoUtil;


/**
*
* JPIERE-0480 Reminder Menu Popup Window - Reminder List
*
*
* @author h.hagiwara
*
*/
public class ReminderMenuRowRenderer implements RowRenderer<I_ToDoReminder> {

	private static int remindTarget_Reference_ID = 0;
	private static int reminderType_Reference_ID = 0;
	private static int mailFrequency_Reference_ID = 0;
	private static int broadcastFrequency_Reference_ID = 0;

	private ReminderMenuPopup reminderMenuPopup = null;


	public ReminderMenuRowRenderer(ReminderMenuPopup reminderMenuPopup)
	{
		this.reminderMenuPopup = reminderMenuPopup;
	}

	@Override
	public void render(Row row, I_ToDoReminder i_ToDoReminder, int index) throws Exception
	{
		//Image
		Cell cell = new Cell();
		Button reminderBtn = new Button();
		if (ThemeManager.isUseFontIconForImage())
			reminderBtn.setIconSclass("z-icon-Request");
		else
			reminderBtn.setImage(ThemeManager.getThemeResource("images/Request16.png"));
		reminderBtn.setClass("btn-small");
		reminderBtn.setName(ReminderMenuPopup.BUTTON_UPDATE_REMINDER);
		reminderBtn.setTooltiptext(Msg.getMsg(Env.getCtx(), "JP_Reminder"));
		reminderBtn.addEventListener(Events.ON_CLICK, reminderMenuPopup);
		reminderBtn.setAttribute(ReminderMenuPopup.I_REMINDER, i_ToDoReminder);
		cell.appendChild(reminderBtn);
		row.appendChild(cell);


		//Remind Time
		cell = new Cell();
		SimpleDateFormat sdfV = DisplayType.getDateFormat();
		String date = sdfV.format(i_ToDoReminder.getJP_ToDo_RemindTime());
		cell.appendChild(new Label(date + " " + i_ToDoReminder.getJP_ToDo_RemindTime().toLocalDateTime().toLocalTime().toString().substring(0, 5) ));
		row.appendChild(cell);


		if(MToDoTeamReminder.Table_Name.equals(i_ToDoReminder.get_TableName()))
		{
			//Remind Target
			cell = new Cell();
			if(remindTarget_Reference_ID == 0)
			{
				int AD_Column_ID = MColumn.getColumn_ID(MToDoTeamReminder.Table_Name, MToDoTeamReminder.COLUMNNAME_JP_ToDo_RemindTarget);
				MColumn col_JP_ToDo_RemindTarget = MColumn.get(Env.getCtx(), AD_Column_ID);
				remindTarget_Reference_ID = col_JP_ToDo_RemindTarget.getAD_Reference_Value_ID();
			}
			String JP_ToDo_RemindTarget = MRefList.getListName(Env.getCtx(), remindTarget_Reference_ID, i_ToDoReminder.getJP_ToDo_RemindTarget());
			cell.appendChild(new Label(JP_ToDo_RemindTarget));
			row.appendChild(cell);

			cell = new Cell();
			if(i_ToDoReminder.getJP_Team_ID()>0)
			{
				MTeam team = MTeam.get(Env.getCtx(), i_ToDoReminder.getJP_Team_ID());
				cell.appendChild(new Label(GroupwareToDoUtil.trimName(team.getName())));
			}else {
				;
			}
			row.appendChild(cell);
		}

		//Remind Type
		cell = new Cell();
		if(reminderType_Reference_ID == 0)
		{
			int AD_Column_ID = MColumn.getColumn_ID(MToDoReminder.Table_Name, MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType);
			MColumn col_JP_ToDo_ReminderType = MColumn.get(Env.getCtx(), AD_Column_ID);
			reminderType_Reference_ID = col_JP_ToDo_ReminderType.getAD_Reference_Value_ID();
		}
		String JP_ToDo_ReminderType = MRefList.getListName(Env.getCtx(), reminderType_Reference_ID, i_ToDoReminder.getJP_ToDo_ReminderType());
		cell.appendChild(new Label(JP_ToDo_ReminderType));
		row.appendChild(cell);


		//Frequency
		cell = new Cell();
		if(MToDoReminder.JP_TODO_REMINDERTYPE_SendMail.equals(i_ToDoReminder.getJP_ToDo_ReminderType()))
		{
			if(mailFrequency_Reference_ID == 0)
			{
				int AD_Column_ID = MColumn.getColumn_ID(MToDoReminder.Table_Name, MToDoReminder.COLUMNNAME_JP_MailFrequency);
				MColumn col_JP_MailFrequency = MColumn.get(Env.getCtx(), AD_Column_ID);
				mailFrequency_Reference_ID = col_JP_MailFrequency.getAD_Reference_Value_ID();
			}

			String JP_MailFrequency = MRefList.getListName(Env.getCtx(), mailFrequency_Reference_ID, i_ToDoReminder.getJP_MailFrequency());
			cell.appendChild(new Label(JP_MailFrequency));

		}else {

			if(broadcastFrequency_Reference_ID == 0)
			{
				int AD_Column_ID = MColumn.getColumn_ID(MToDoReminder.Table_Name, MToDoReminder.COLUMNNAME_BroadcastFrequency);
				MColumn col_BroadcastFrequency = MColumn.get(Env.getCtx(), AD_Column_ID);
				broadcastFrequency_Reference_ID = col_BroadcastFrequency.getAD_Reference_Value_ID();
			}

			String BroadcastFrequency = MRefList.getListName(Env.getCtx(), broadcastFrequency_Reference_ID, i_ToDoReminder.getBroadcastFrequency());
			cell.appendChild(new Label(BroadcastFrequency));
		}
		row.appendChild(cell);


		//SentReminderJP
		cell = new Cell();
		cell.setStyle("text-align:center;");
		Checkbox cb = new Checkbox();
		cb.setChecked(i_ToDoReminder.isSentReminderJP());
		cb.setDisabled(true);
		cell.appendChild(cb);
		row.appendChild(cell);

		//Processed
		cell = new Cell();
		cell.setStyle("text-align:center;");
		cb = new Checkbox();
		cb.setChecked(i_ToDoReminder.isProcessed());
		cb.setDisabled(true);
		cell.appendChild(cb);
		row.appendChild(cell);

		//isConfirmed
		if(MToDoReminder.Table_Name.equals(i_ToDoReminder.get_TableName()))
		{
			cell = new Cell();
			cell.setStyle("text-align:center;");
			cb = new Checkbox();
			cb.setChecked(i_ToDoReminder.isConfirmed());
			cb.setDisabled(true);
			cell.appendChild(cb);
			row.appendChild(cell);
		}

	}

}
