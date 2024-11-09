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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.theme.ThemeManager;
import org.compiere.model.MColumn;
import org.compiere.model.MRefList;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoTeam;


/**
*
* JPIERE-0473 Personal ToDo List Popup Window
*
*
* @author h.hagiwara
*
*/
public class PersonalToDoListRowRenderer implements RowRenderer<PersonalToDoModel> {

	private PersonalToDoListWindow personalToDoListWindow = null;
	private Radiogroup radioGroup = null;
	private MToDoTeam m_TeamToDo = null;


	public PersonalToDoListRowRenderer(PersonalToDoListWindow personalToDoListWindow, Radiogroup radioGroup)
	{
		this.personalToDoListWindow = personalToDoListWindow;
		this.m_TeamToDo = personalToDoListWindow.getMToDoTeam();
		this.radioGroup= radioGroup;
	}

	@Override
	public void render(Row row, PersonalToDoModel data, int index) throws Exception
	{
		personalToDoListWindow.setJP_ToDo_ID(0);

		//Radio
		Cell cell = new Cell();
		Radio radio = new Radio();
		radio.setRadiogroup(radioGroup);
		radio.setAttribute(MToDo.COLUMNNAME_JP_ToDo_ID,data.JP_ToDo_ID);
		radio.addEventListener(Events.ON_CHECK, personalToDoListWindow);

		cell.appendChild(radio);
		row.appendChild(cell);

		//Reminder button
		cell = new Cell();
		Button reminderBtn = new Button();
		if (ThemeManager.isUseFontIconForImage())
			reminderBtn.setIconSclass("z-icon-Request");
		else
			reminderBtn.setImage(ThemeManager.getThemeResource("images/Request16.png"));
		reminderBtn.setClass("btn-small");
		reminderBtn.setName(ToDoPopupWindow.BUTTON_NAME_REMINDER);
		reminderBtn.setTooltiptext(Msg.getMsg(Env.getCtx(), "JP_Reminder"));
		reminderBtn.addEventListener(Events.ON_CLICK, personalToDoListWindow);
		reminderBtn.setAttribute(MToDo.COLUMNNAME_JP_ToDo_ID,data.JP_ToDo_ID);
		cell.appendChild(reminderBtn);
		row.appendChild(cell);

		//User
		cell = new Cell();
		cell.appendChild(new Label(data.user));
		row.appendChild(cell);

		//ToDo Status
		cell = new Cell();
		cell.appendChild(new Label(data.status));
		row.appendChild(cell);

		if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_None.equals(m_TeamToDo.getJP_Mandatory_Statistics_Info()))
		{
			;//Nothing To Do
		}else {

			cell = new Cell();
			if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_YesNo.equals(m_TeamToDo.getJP_Mandatory_Statistics_Info()))
			{

				if(Util.isEmpty(data.JP_Statistics_YesNo))
				{
					cell.appendChild(new Label(""));
				}else {
					int AD_Column_ID = MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_Statistics_YesNo);
					MColumn column = MColumn.get(Env.getCtx(), AD_Column_ID);
					int reference_ID = column.getAD_Reference_Value_ID();
					String statistics_info = MRefList.getListName(Env.getCtx(), reference_ID, data.JP_Statistics_YesNo);
					cell.appendChild(new Label(statistics_info));
				}

			}else if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_Choice.equals(m_TeamToDo.getJP_Mandatory_Statistics_Info())) {

				if(Util.isEmpty(data.JP_Statistics_Choice))
				{
					cell.appendChild(new Label(""));
				}else {
					int AD_Column_ID = MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_JP_Statistics_Choice);
					MColumn column = MColumn.get(Env.getCtx(), AD_Column_ID);
					int reference_ID = column.getAD_Reference_Value_ID();
					String statistics_info = MRefList.getListName(Env.getCtx(), reference_ID, data.JP_Statistics_Choice);
					cell.appendChild(new Label(statistics_info));
				}

			}else if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_DateAndTime.equals(m_TeamToDo.getJP_Mandatory_Statistics_Info())) {

				if(data.JP_Statistics_DateAndTime != null)
				{
					SimpleDateFormat sdfV = DisplayType.getDateFormat();
					Date dateAndTime = new Date(data.JP_Statistics_DateAndTime.getTime());
					String string_Date = sdfV.format(dateAndTime);
					String string_Time = data.JP_Statistics_DateAndTime.toLocalDateTime().toLocalTime().toString();
					cell.appendChild(new Label(string_Date + " " +string_Time));
				}

			}else if(MToDoTeam.JP_MANDATORY_STATISTICS_INFO_Number.equals(m_TeamToDo.getJP_Mandatory_Statistics_Info())) {

				if(data.JP_Statistics_Number != null)
				{
					cell.appendChild(new Label(data.JP_Statistics_Number.toString()));
				}
			}
			row.appendChild(cell);
		}

		//Comments
		cell = new Cell();
		cell.appendChild(new Label(data.comments));
		row.appendChild(cell);




	}

}
