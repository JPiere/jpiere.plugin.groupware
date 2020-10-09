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
package jpiere.plugin.groupware.base;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.adempiere.util.Callback;
import org.adempiere.webui.adwindow.validator.WindowValidator;
import org.adempiere.webui.adwindow.validator.WindowValidatorEvent;
import org.adempiere.webui.adwindow.validator.WindowValidatorEventType;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoTeam;


/**
 *  JPIERE-0469
 *  Team ToDo Window validator
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class TeamToDoWindowValidator implements WindowValidator {

	@Override
	public void onWindowEvent(WindowValidatorEvent event, Callback<Boolean> callback)
	{
		if(event.getName().equals(WindowValidatorEventType.BEFORE_SAVE.getName()))
		{
			/**
			 * AFTER_SAVE could not get old value.
			 * So, I use BEFORE_SAVE.
			 **/

			GridTab gridTab =event.getWindow().getADWindowContent().getActiveGridTab();
			int Record_ID = gridTab.getRecord_ID();
			if(gridTab.getTabNo() == 0 && Record_ID > 0 )
			{
				MToDoTeam m_ToDo =	new MToDoTeam(Env.getCtx(), Record_ID, null);
				Timestamp old_ScheduledStartTime = m_ToDo.getJP_ToDo_ScheduledStartTime();
				Timestamp old_ScheduledEndTime = m_ToDo.getJP_ToDo_ScheduledEndTime();

				GridField gf_JP_ToDo_ScheduledStartDate = gridTab.getField(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartDate);
				GridField gf_JP_ToDo_ScheduledStartTime = gridTab.getField(MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime);
				GridField gf_IsStartDateAllDayJP = gridTab.getField(MToDo.COLUMNNAME_IsStartDateAllDayJP);

				GridField gf_JP_ToDo_ScheduledEndDate = gridTab.getField(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndDate);
				GridField gf_JP_ToDo_ScheduledEndTime = gridTab.getField(MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime);
				GridField gf_IsEndDateAllDayJP = gridTab.getField(MToDo.COLUMNNAME_IsEndDateAllDayJP);

				Timestamp new_ScheduledStartDate = (Timestamp)gf_JP_ToDo_ScheduledStartDate.getValue();
				Timestamp new_ScheduledStartTime = (Timestamp)gf_JP_ToDo_ScheduledStartTime.getValue();
				boolean  isStartDateAllDayJP = (boolean)gf_IsStartDateAllDayJP.getValue();
				if(isStartDateAllDayJP)
				{
					new_ScheduledStartTime = Timestamp.valueOf(LocalDateTime.of(new_ScheduledStartDate.toLocalDateTime().toLocalDate(), LocalTime.MIN));
				}else {
					new_ScheduledStartTime = Timestamp.valueOf(LocalDateTime.of(new_ScheduledStartDate.toLocalDateTime().toLocalDate(),new_ScheduledStartTime.toLocalDateTime().toLocalTime()));
				}

				Timestamp new_ScheduledEndDate = (Timestamp)gf_JP_ToDo_ScheduledEndDate.getValue();
				Timestamp new_ScheduledEndTime = (Timestamp)gf_JP_ToDo_ScheduledEndTime.getValue();
				boolean  isEndDateAllDayJP = (boolean)gf_IsEndDateAllDayJP.getValue();
				if(isEndDateAllDayJP)
				{
					new_ScheduledEndTime = Timestamp.valueOf(LocalDateTime.of(new_ScheduledEndDate.toLocalDateTime().toLocalDate(), LocalTime.MIN));
				}else {
					new_ScheduledEndTime = Timestamp.valueOf(LocalDateTime.of(new_ScheduledEndDate.toLocalDateTime().toLocalDate(),new_ScheduledEndTime.toLocalDateTime().toLocalTime()));
				}

				GridField gf_JP_ToDo_Type = gridTab.getField(MToDo.COLUMNNAME_JP_ToDo_Type);
				String JP_ToDo_Type = gf_JP_ToDo_Type.getValue().toString();
				if(MToDo.JP_TODO_TYPE_Task.equals(JP_ToDo_Type))
				{
					new_ScheduledStartTime = new_ScheduledEndTime;
				}

				if(new_ScheduledStartTime.compareTo(old_ScheduledStartTime) != 0
						||	new_ScheduledEndTime.compareTo(old_ScheduledEndTime) != 0)
				{
					ArrayList<MToDoTeam> list = MToDoTeam.getRelatedTeamToDos(Env.getCtx(), m_ToDo, null, old_ScheduledStartTime, true, null);

					if(list.size() > 0)
					{
						long between_ScheduledStartMins = ChronoUnit.MINUTES.between(old_ScheduledStartTime.toLocalDateTime(), new_ScheduledStartTime.toLocalDateTime());
						long between_ScheduledEndMins = ChronoUnit.MINUTES.between(old_ScheduledEndTime.toLocalDateTime(), new_ScheduledEndTime.toLocalDateTime());

						Callback<Boolean> isRelaredToDoUpdate = new Callback<Boolean>()
						{
								@Override
								public void onCallback(Boolean result)
								{
									if(result)
									{
										Timestamp scheduledStartTime = null;
										Timestamp scheduledEndTime = null;

										for(MToDoTeam todo : list)
										{
											if(m_ToDo.getJP_ToDo_Team_ID() == todo.getJP_ToDo_Team_ID())
												continue;

											scheduledStartTime = Timestamp.valueOf(todo.getJP_ToDo_ScheduledStartTime().toLocalDateTime().plusMinutes(between_ScheduledStartMins));
											scheduledEndTime = Timestamp.valueOf(todo.getJP_ToDo_ScheduledEndTime().toLocalDateTime().plusMinutes(between_ScheduledEndMins));

											todo.setJP_ToDo_ScheduledStartDate(scheduledStartTime);
											todo.setJP_ToDo_ScheduledStartTime(scheduledStartTime);
											todo.setIsStartDateAllDayJP(isStartDateAllDayJP);

											todo.setJP_ToDo_ScheduledEndDate(scheduledEndTime);
											todo.setJP_ToDo_ScheduledEndTime(scheduledEndTime);
											todo.setIsEndDateAllDayJP(isEndDateAllDayJP);
											if(!todo.save())
											{
												;//TODO エラー処理
											}
										}

									}
								}
						};
						FDialog.ask(gridTab.getWindowNo(), null, "JP_ToDo_Update_CreatedRepeatedly1", Msg.getMsg(Env.getCtx(), "JP_ToDo_Update_CreatedRepeatedly2"), isRelaredToDoUpdate);

					}//if(list.size() > 0)
				}
			}//if(gridTab.getTabNo() == 0 )
		}//AFTER_SAVE

		callback.onCallback(true);

	}

}
