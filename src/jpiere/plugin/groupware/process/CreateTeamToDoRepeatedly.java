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

import org.adempiere.util.ProcessUtil;
import org.compiere.model.MUser;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;
import org.compiere.util.Trx;

import jpiere.plugin.groupware.model.MToDoMemberAdditional;
import jpiere.plugin.groupware.model.MToDoTeam;
import jpiere.plugin.groupware.model.MToDoTeamReminder;
import jpiere.plugin.groupware.util.GroupwareTeamUtil;

/**
 * JPIERE-0469: Create Team ToDo Repeatedly
 *
 * @author h.hagiwara
 *
 */
public class CreateTeamToDoRepeatedly extends SvrProcess {

	/** Param **/
	private int p_JP_ToDo_Team_ID = 0;
	private int p_JP_Offset_Value = 0;
	private String p_JP_Repetition_Interval = null;
	private Timestamp p_DateTo = null;
	private boolean p_IsCopyReminderJP = false;
	private boolean p_IsCreateToDoJP = false;
	private MToDoTeam m_TeamToDo = null;


	/*** Variable ***/
	private Timestamp v_JP_ToDo_ScheduledStartTime = null;
	private Timestamp v_JP_ToDo_ScheduledEndTime = null;
	private Timestamp v_JudgmentTime = null;
	private MToDoTeam v_TeamToDo = null;

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
			}else if (name.equals("IsCreateToDoJP")){
				p_IsCreateToDoJP = para[i].getParameterAsBoolean();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for

		p_JP_ToDo_Team_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception
	{
		m_TeamToDo = new MToDoTeam(getCtx(), p_JP_ToDo_Team_ID, get_TrxName());
		v_TeamToDo = m_TeamToDo;
		if(v_TeamToDo.getJP_ToDo_ScheduledStartTime() == null && v_TeamToDo.getJP_ToDo_ScheduledEndTime() ==null)
		{
			String msg = Msg.getMsg(getCtx(), "JP_PleaseInputToField") + " " +Msg.getElement(getCtx(), "JP_ToDo_ScheduledStartTime")
																		+ " or " + Msg.getElement(getCtx(), "JP_ToDo_ScheduledEndTime");
			throw new Exception(msg);
		}

		if(m_TeamToDo.getJP_Processing3().equals("Y"))
		{
			throw new Exception(Msg.getElement(getCtx(), "Processed"));
		}

		v_JP_ToDo_ScheduledStartTime = m_TeamToDo.getJP_ToDo_ScheduledStartTime() ;
		if(v_JP_ToDo_ScheduledStartTime != null)
			v_JP_ToDo_ScheduledStartTime = calculateNextScheduleTime(v_JP_ToDo_ScheduledStartTime);

		v_JP_ToDo_ScheduledEndTime = m_TeamToDo.getJP_ToDo_ScheduledEndTime() ;
		if(v_JP_ToDo_ScheduledEndTime != null)
			v_JP_ToDo_ScheduledEndTime = calculateNextScheduleTime(v_JP_ToDo_ScheduledEndTime);

		v_JudgmentTime = v_JP_ToDo_ScheduledStartTime == null ? v_JP_ToDo_ScheduledEndTime :  v_JP_ToDo_ScheduledStartTime;
		p_DateTo = Timestamp.valueOf(LocalDateTime.of(p_DateTo.toLocalDateTime().toLocalDate(), LocalTime.MAX));

		int created = 0;
		while(p_DateTo.compareTo(v_JudgmentTime) >= 0)
		{
			createTeamToDo();
			created++;

			if(v_JP_ToDo_ScheduledStartTime != null)
				v_JP_ToDo_ScheduledStartTime = calculateNextScheduleTime(v_JP_ToDo_ScheduledStartTime);

			if(v_JP_ToDo_ScheduledEndTime != null)
				v_JP_ToDo_ScheduledEndTime = calculateNextScheduleTime(v_JP_ToDo_ScheduledEndTime);

			v_JudgmentTime = v_JP_ToDo_ScheduledStartTime == null ? v_JP_ToDo_ScheduledEndTime :  v_JP_ToDo_ScheduledStartTime;
		}

		m_TeamToDo.setJP_Processing3("Y");
		m_TeamToDo.saveEx(get_TrxName());

		return Msg.getMsg(getCtx(), "Success") + " " + Msg.getMsg(getCtx(), "Created")+":" + created;
	}

	private void createTeamToDo()
	{
		MToDoTeam new_TeamToDo = new MToDoTeam(getCtx(), 0, get_TrxName());
		PO.copyValues(v_TeamToDo, new_TeamToDo);
		new_TeamToDo.setJP_ToDo_ScheduledStartTime(v_JP_ToDo_ScheduledStartTime);
		new_TeamToDo.setJP_ToDo_ScheduledEndTime(v_JP_ToDo_ScheduledEndTime);
		new_TeamToDo.setJP_ToDo_Team_Related_ID(p_JP_ToDo_Team_ID);
		new_TeamToDo.setJP_ToDo_Status(MToDoTeam.JP_TODO_STATUS_NotYetStarted);
		new_TeamToDo.saveEx(get_TrxName());

		createAdditionalTeamMemberUser(new_TeamToDo);
		if(p_IsCopyReminderJP)
			createTeamToDoRemainder(new_TeamToDo);

		if(p_IsCreateToDoJP)
			createToDo(new_TeamToDo);

		v_TeamToDo = new_TeamToDo;
	}

	private void createAdditionalTeamMemberUser(MToDoTeam new_TeamToDo)
	{
		MUser[] users = m_TeamToDo.getAdditionalTeamMemberUser();
		MToDoMemberAdditional tdma = null;
		for(int i = 0; i < users.length; i++)
		{
			tdma = new MToDoMemberAdditional(getCtx(), 0, get_TrxName());
			tdma.setJP_ToDo_Team_ID(new_TeamToDo.getJP_ToDo_Team_ID());
			tdma.setAD_Org_ID(0);
			tdma.setAD_User_ID(users[i].getAD_User_ID());
			tdma.saveEx(get_TrxName());
		}
	}

	private void createTeamToDoRemainder(MToDoTeam  new_TeamToDo)
	{
		MToDoTeamReminder[] m_TeamToDoReminders = v_TeamToDo.getReminders();
		MToDoTeamReminder tdtr = null;
		for(int i = 0; i < m_TeamToDoReminders.length; i++)
		{
			tdtr = new MToDoTeamReminder(getCtx(), 0, get_TrxName());
			tdtr.setAD_Org_ID(0);
			tdtr.setJP_ToDo_Team_ID(new_TeamToDo.getJP_ToDo_Team_ID());
			tdtr.setJP_ToDo_ReminderType(m_TeamToDoReminders[i].getJP_ToDo_ReminderType());
			tdtr.setDescription(m_TeamToDoReminders[i].getDescription());
			tdtr.setJP_ToDo_RemindTime(calculateNextScheduleTime(m_TeamToDoReminders[i].getJP_ToDo_RemindTime()));
			tdtr.setIsSentReminderJP(false);
			tdtr.setJP_Mandatory_Statistics_Info(m_TeamToDoReminders[i].getJP_Mandatory_Statistics_Info());
			tdtr.saveEx(get_TrxName());
		}

	}

	private void createToDo(MToDoTeam  new_TeamToDo)
	{
		String className = "jpiere.plugin.groupware.process.CreateToDoFromTeamToDo";
		ProcessInfo pi = new ProcessInfo("Create ToDo From Team ToDo", 0);
		pi.setClassName(className);
		pi.setAD_Client_ID(getAD_Client_ID());
		pi.setAD_User_ID(getAD_User_ID());
		pi.setAD_PInstance_ID(getAD_PInstance_ID());
		pi.setRecord_ID(new_TeamToDo.getJP_ToDo_Team_ID());
		ProcessUtil.startJavaProcess(getCtx(), pi, Trx.get(get_TrxName(), true), false, null);

	}

	private Timestamp calculateNextScheduleTime(Timestamp timestamp)
	{
		return GroupwareTeamUtil.calculateNextScheduleTime(timestamp, p_JP_Offset_Value, p_JP_Repetition_Interval);
	}

}
