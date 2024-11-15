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
package jpiere.plugin.groupware.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.compiere.model.MMessage;
import org.compiere.model.MUser;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0469: JPiere Groupware
 *
 * @author h.hagiwara
 *
 */
public class MToDoTeamReminder extends X_JP_ToDo_Team_Reminder implements I_ToDoReminder  {

	private static final long serialVersionUID = -1296743262558933321L;

	private MToDoTeam parent = null;

	protected String  m_RemindMsg = null;

	public MToDoTeamReminder(Properties ctx, int JP_ToDo_Team_Reminder_ID, String trxName)
	{
		super(ctx, JP_ToDo_Team_Reminder_ID, trxName);
	}

	public MToDoTeamReminder(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	private boolean isProcessingReminder = false;

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		String msg = beforeSavePreCheck(newRecord);
		if(!Util.isEmpty(msg))
		{
			log.saveError("Error", msg);
			return false;
		}

		return true;
	}

	public String beforeSavePreCheck(boolean newRecord)
	{

		//** Check User**/
		if(!newRecord && !isProcessingReminder)
		{
			int loginUser  = Env.getAD_User_ID(getCtx());
			if(loginUser == getParent().getAD_User_ID() || loginUser == getParent().getCreatedBy() || loginUser == getCreatedBy())
			{
				;//Updatable

			}else {

				MMessage msg = MMessage.get(getCtx(), "AccessCannotUpdate");
				return msg.get_Translation("MsgText") + " - "+ msg.get_Translation("MsgTip");
			}

		}

		return null;
	}


	@Override
	protected boolean beforeDelete()
	{
		String msg = beforeDeletePreCheck();
		if(!Util.isEmpty(msg))
		{
			log.saveError("Error", msg);
			return false;
		}

		return true;
	}


	public String beforeDeletePreCheck()
	{
		//** Check User**/
		int loginUser  = Env.getAD_User_ID(getCtx());
		if(loginUser == getParent().getAD_User_ID() || loginUser == getParent().getCreatedBy() || loginUser == getCreatedBy())
		{
			//Deleteable;

		}else {

			MMessage msg = MMessage.get(getCtx(), "AccessCannotUpdate");
			return msg.get_Translation("MsgText") + " - "+ msg.get_Translation("MsgTip");
		}


		return null;
	}


	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{
		if(!newRecord && success)
		{
			String sql = "UPDATE JP_ToDo_Reminder t"
					+ " SET "
					+ " Description = ? "
					+ " , URL = ? "
					+ "WHERE JP_ToDo_Team_Reminder_ID= ? ";

				Object[] para = {
						getDescription()
						, getURL()
						,getJP_ToDo_Team_Reminder_ID()
						};

				DB.executeUpdate(sql, para, false, get_TrxName());
		}

		return true;
	}


	private MToDoTeam getParent()
	{
		if(parent == null)
			parent = new MToDoTeam(getCtx(), getJP_ToDo_Team_ID(), get_TrxName());

		return parent;
	}


	public boolean createPersonalToDoRemainder()
	{
		getParent();
		MToDo[] todoes = parent.getToDoes(true);
		if(todoes.length == 0)
		{
			//Personal ToDo have not been created.
			m_RemindMsg = Msg.getMsg(getCtx(), "JP_PersonalToDoNotCreated") ;
			return false;
		}

		MUser[] users = null;
		MToDoReminder todoReminder = null;

		if(getJP_Team_ID() > 0)
		{
			MTeam team = new MTeam(getCtx(),getJP_Team_ID(), get_TrxName() );
			users = team.getTeamMemberUser();
		}
		int counter = 0;
		for(int i = 0; i < todoes.length; i++)
		{
			//Check Target
			if(MToDoTeamReminder.JP_TODO_REMINDTARGET_AllUserOfPersonalToDo.equals(getJP_ToDo_RemindTarget()))
			{
				;

			}else if(MToDoTeamReminder.JP_TODO_REMINDTARGET_UserOfUncompletePersonalToDo.equals(getJP_ToDo_RemindTarget())) {

				if(MToDo.JP_TODO_STATUS_Completed.equals(todoes[i].getJP_ToDo_Status()))
				{
					continue;
				}

			}else if(MToDoTeamReminder.JP_TODO_REMINDTARGET_UserOfNotYetStartedPersonalToDo.equals(getJP_ToDo_RemindTarget())) {

				if(!MToDo.JP_TODO_STATUS_NotYetStarted.equals(todoes[i].getJP_ToDo_Status()))
				{
					continue;
				}

			}else if(MToDoTeamReminder.JP_TODO_REMINDTARGET_UserOfWorkInProgressPersonalToDo.equals(getJP_ToDo_RemindTarget())) {

				if(!MToDo.JP_TODO_STATUS_WorkInProgress.equals(todoes[i].getJP_ToDo_Status()))
				{
					continue;
				}

			}else if(MToDoTeamReminder.JP_TODO_REMINDTARGET_UserOfCompletedPersonalToDo.equals(getJP_ToDo_RemindTarget())) {

				if(!MToDo.JP_TODO_STATUS_Completed.equals(todoes[i].getJP_ToDo_Status()))
				{
					continue;
				}
			}

			//Check Team
			if(users != null)
			{
				boolean isIncludeTeam = false;
				for(int j = 0; j < users.length; j++)
				{
					if(todoes[i].getAD_User_ID() == users[j].getAD_User_ID())
					{
						isIncludeTeam = true;
						break;
					}
				}

				if(!isIncludeTeam)
					continue;
			}

			todoReminder = new MToDoReminder(getCtx(), 0, get_TrxName());
			todoReminder.setAD_Org_ID(todoes[i].getAD_Org_ID());
			todoReminder.setJP_ToDo_ID(todoes[i].getJP_ToDo_ID());
			todoReminder.setJP_ToDo_Team_Reminder_ID(getJP_ToDo_Team_Reminder_ID());
			todoReminder.setJP_ToDo_ReminderType(getJP_ToDo_ReminderType());
			todoReminder.setJP_ToDo_RemindTime(getJP_ToDo_RemindTime());
			todoReminder.setJP_MailFrequency(getJP_MailFrequency());
			todoReminder.setBroadcastFrequency(getBroadcastFrequency());
			todoReminder.setDescription(getDescription());
			todoReminder.setURL(getURL());
			todoReminder.setJP_SendMailNextTime(getJP_ToDo_RemindTime());
			todoReminder.saveEx(get_TrxName());
			counter++;
		}

		m_RemindMsg = Msg.getMsg(getCtx(), "Created") + " : " + counter;

		isProcessingReminder = true;
		setIsSentReminderJP(true);
		setProcessed(true);
		saveEx(get_TrxName());
		isProcessingReminder = false;

		return true;
	}


	@Override
	public void setIsConfirmed(boolean IsConfirmed)
	{
		;
	}

	@Override
	public boolean isConfirmed()
	{
		return false;
	}

	@Override
	public void setJP_Confirmed(Timestamp JP_Confirmed)
	{
		;
	}

	@Override
	public Timestamp getJP_Confirmed()
	{
		return null;
	}

	@Override
	public void setComments(String Comments)
	{
		;
	}

	@Override
	public String getComments()
	{
		return null;
	}

	@Override
	public void setJP_Statistics_Choice(String JP_Statistics_Choice)
	{
		;
	}

	@Override
	public String getJP_Statistics_Choice()
	{
		return null;
	}

	@Override
	public void setJP_Statistics_DateAndTime(Timestamp JP_Statistics_DateAndTime)
	{
		;
	}

	@Override
	public Timestamp getJP_Statistics_DateAndTime()
	{
		return null;
	}

	@Override
	public void setJP_Statistics_Number(BigDecimal JP_Statistics_Number)
	{
		;
	}

	@Override
	public BigDecimal getJP_Statistics_Number()
	{
		return null;
	}

	@Override
	public void setJP_Statistics_YesNo(String JP_Statistics_YesNo)
	{
		;
	}

	@Override
	public String getJP_Statistics_YesNo()
	{
		return null;
	}

	@Override
	public void setUpdated(Timestamp updated)
	{
		set_ValueNoCheck("Updated", updated);
	}

	@Override
	public void setJP_ToDo_ID(int JP_ToDo_ID)
	{
		;
	}

	@Override
	public int getJP_ToDo_ID()
	{
		return 0;
	}

	@Override
	public String getRemindMsg()
	{
		return m_RemindMsg;
	}


}
