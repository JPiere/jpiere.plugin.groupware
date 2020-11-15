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
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

import jpiere.plugin.groupware.model.I_ToDoReminder;
import jpiere.plugin.groupware.model.MToDoReminder;


/**
*
* JPIERE-0480 Reminder Menu Popup Window - Reminder List
*
*
* @author h.hagiwara
*
*/
public class ReminderMenuRowRenderer implements RowRenderer<I_ToDoReminder> {

	private static int reminderType_Reference_ID = 0;

	private ReminderMenuPopup reminderMenuPopup = null;


	public ReminderMenuRowRenderer(ReminderMenuPopup reminderMenuPopup)
	{
		this.reminderMenuPopup = reminderMenuPopup;
	}

	@Override
	public void render(Row row, I_ToDoReminder data, int index) throws Exception
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
		reminderBtn.setAttribute(ReminderMenuPopup.I_REMINDER, data);
		cell.appendChild(reminderBtn);
		row.appendChild(cell);

		//Remind Time
		cell = new Cell();
		SimpleDateFormat sdfV = DisplayType.getDateFormat();
		String date = sdfV.format(data.getJP_ToDo_RemindTime());
		cell.appendChild(new Label(date + " " + data.getJP_ToDo_RemindTime().toLocalDateTime().toLocalTime().toString().substring(0, 5) ));
		row.appendChild(cell);

		//Remind Type
		cell = new Cell();
		if(reminderType_Reference_ID == 0)
		{
			int AD_Column_ID = MColumn.getColumn_ID(MToDoReminder.Table_Name, MToDoReminder.COLUMNNAME_JP_ToDo_ReminderType);
			MColumn col_JP_ToDo_ReminderType = MColumn.get(Env.getCtx(), AD_Column_ID);
			reminderType_Reference_ID = col_JP_ToDo_ReminderType.getAD_Reference_Value_ID();
		}
		String JP_ToDo_ReminderType = MRefList.getListName(Env.getCtx(), reminderType_Reference_ID, data.getJP_ToDo_ReminderType());
		cell.appendChild(new Label(JP_ToDo_ReminderType));
		row.appendChild(cell);

	}

}
