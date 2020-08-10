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
package jpiere.plugin.groupware.process;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Level;

import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoReminder;
import jpiere.plugin.groupware.model.MToDoTeam;
import jpiere.plugin.groupware.util.GroupwareTeamUtil;

/**
 * JPIERE-0469: Create ToDo Repeatedly
 *
 * @author h.hagiwara
 *
 */
public class CreateToDoRepeatedly extends SvrProcess {

	/** Param **/
	private int p_JP_ToDo_ID = 0;
	private int p_JP_Offset_Value = 0;
	private String p_JP_Repetition_Interval = null;
	private Timestamp p_DateTo = null;
	private boolean p_IsCopyReminderJP = false;
	private MToDo m_ToDo = null;


	/*** Variable ***/
	private Timestamp v_JP_ToDo_ScheduledStartTime = null;
	private Timestamp v_JP_ToDo_ScheduledEndTime = null;
	private Timestamp v_JudgmentTime = null;
	private MToDo v_TeamToDo = null;

	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("JP_Offset_Value")){
				p_JP_Offset_Value = para[i].getParameterAsInt();
			}else if (name.equals("JP_Repetition_Interval")){
				p_JP_Repetition_Interval = para[i].getParameterAsString();
			}else if (name.equals("DateTo")){
				p_DateTo = para[i].getParameterAsTimestamp();
			}else if (name.equals("IsCopyReminderJP")){
				p_IsCopyReminderJP = para[i].getParameterAsBoolean();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for

		p_JP_ToDo_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception
	{
		m_ToDo = new MToDo(getCtx(), p_JP_ToDo_ID, get_TrxName());
		v_TeamToDo = m_ToDo;
		if(v_TeamToDo.getJP_ToDo_ScheduledStartTime() == null && v_TeamToDo.getJP_ToDo_ScheduledEndTime() ==null)
		{
			String msg = Msg.getMsg(getCtx(), "JP_PleaseInputToField") + " " +Msg.getElement(getCtx(), "JP_ToDo_ScheduledStartTime")
																		+ " or " + Msg.getElement(getCtx(), "JP_ToDo_ScheduledEndTime");
			throw new Exception(msg);
		}

		if(m_ToDo.getJP_Processing1().equals("Y"))
		{
			throw new Exception(Msg.getElement(getCtx(), "Processed"));
		}

		v_JP_ToDo_ScheduledStartTime = m_ToDo.getJP_ToDo_ScheduledStartTime() ;
		if(v_JP_ToDo_ScheduledStartTime != null)
			v_JP_ToDo_ScheduledStartTime = calculateNextScheduleTime(v_JP_ToDo_ScheduledStartTime);

		v_JP_ToDo_ScheduledEndTime = m_ToDo.getJP_ToDo_ScheduledEndTime() ;
		if(v_JP_ToDo_ScheduledEndTime != null)
			v_JP_ToDo_ScheduledEndTime = calculateNextScheduleTime(v_JP_ToDo_ScheduledEndTime);

		v_JudgmentTime = v_JP_ToDo_ScheduledStartTime == null ? v_JP_ToDo_ScheduledEndTime :  v_JP_ToDo_ScheduledStartTime;
		p_DateTo = Timestamp.valueOf(LocalDateTime.of(p_DateTo.toLocalDateTime().toLocalDate(), LocalTime.MAX));


		int created = 0;
		while(p_DateTo.compareTo(v_JudgmentTime) >= 0)
		{
			createToDo();
			created++;

			if(v_JP_ToDo_ScheduledStartTime != null)
				v_JP_ToDo_ScheduledStartTime = calculateNextScheduleTime(v_JP_ToDo_ScheduledStartTime);

			if(v_JP_ToDo_ScheduledEndTime != null)
				v_JP_ToDo_ScheduledEndTime = calculateNextScheduleTime(v_JP_ToDo_ScheduledEndTime);

			v_JudgmentTime = v_JP_ToDo_ScheduledStartTime == null ? v_JP_ToDo_ScheduledEndTime :  v_JP_ToDo_ScheduledStartTime;
		}

		m_ToDo.setJP_Processing1("Y");
		m_ToDo.saveEx(get_TrxName());

		return Msg.getMsg(getCtx(), "Success") + " " + Msg.getMsg(getCtx(), "Created")+":" + created;
	}

	private void createToDo()
	{
		MToDo new_ToDo = new MToDo(getCtx(), 0, get_TrxName());
		PO.copyValues(v_TeamToDo, new_ToDo);
		new_ToDo.setJP_ToDo_ScheduledStartTime(v_JP_ToDo_ScheduledStartTime);
		new_ToDo.setJP_ToDo_ScheduledEndTime(v_JP_ToDo_ScheduledEndTime);
		new_ToDo.setJP_ToDo_Related_ID(p_JP_ToDo_ID);
		new_ToDo.setJP_ToDo_Status(MToDoTeam.JP_TODO_STATUS_NotYetStarted);
		new_ToDo.saveEx(get_TrxName());

		if(p_IsCopyReminderJP)
			createToDoRemainder(new_ToDo);

		v_TeamToDo = new_ToDo;
	}

	private void createToDoRemainder(MToDo  new_TeamToDo)
	{
		MToDoReminder[] m_ToDoReminders = v_TeamToDo.getReminders();
		MToDoReminder tdr = null;
		for(int i = 0; i < m_ToDoReminders.length; i++)
		{
			tdr = new MToDoReminder(getCtx(), 0, get_TrxName());
			tdr.setAD_Org_ID(0);
			tdr.setJP_ToDo_ID(new_TeamToDo.getJP_ToDo_ID());
			tdr.setJP_ToDo_ReminderType(m_ToDoReminders[i].getJP_ToDo_ReminderType());
			tdr.setDescription(m_ToDoReminders[i].getDescription());
			tdr.setJP_ToDo_RemindTime(calculateNextScheduleTime(m_ToDoReminders[i].getJP_ToDo_RemindTime()));
			tdr.setIsSentReminderJP(false);
			tdr.saveEx(get_TrxName());
		}

	}

	private Timestamp calculateNextScheduleTime(Timestamp timestamp)
	{
		return GroupwareTeamUtil.calculateNextScheduleTime(timestamp, p_JP_Offset_Value, p_JP_Repetition_Interval);
	}

}
