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

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MMessage;
import org.compiere.util.Env;
import org.compiere.util.Util;

/**
 * JPIERE-0469: JPiere Groupware
 *
 * @author h.hagiwara
 *
 */
public class MToDoTeamReminder extends X_JP_ToDo_Team_Reminder {

	private MToDoTeam parent = null;

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



	private MToDoTeam getParent()
	{
		if(parent == null)
			parent = new MToDoTeam(getCtx(), getJP_ToDo_Team_ID(), get_TrxName());

		return parent;
	}



	static public boolean sendMailRemainder(Properties ctx, int JP_ToDo_Team_Reminder_ID, String trxName)
	{
		return sendMailRemainder(ctx, new MToDoTeamReminder(ctx, JP_ToDo_Team_Reminder_ID, trxName), trxName);
	}

	static public boolean sendMailRemainder(Properties ctx, MToDoTeamReminder teamToDoreminder, String trxName)
	{
		MToDoTeam teamToDo = new MToDoTeam(ctx, teamToDoreminder.getJP_ToDo_Team_ID(), trxName);
		MToDo[] todoes = teamToDo.getToDoes();

		MToDoReminder todoReminder = null;
		for(int i = 0; i < todoes.length; i++)
		{
			todoReminder = new MToDoReminder(ctx, 0, trxName);
			todoReminder.setAD_Org_ID(todoes[i].getAD_Org_ID());
			todoReminder.setJP_ToDo_ID(todoes[i].getJP_ToDo_ID());
			todoReminder.setJP_ToDo_Team_Reminder_ID(teamToDoreminder.getJP_ToDo_Team_Reminder_ID());
			todoReminder.setJP_ToDo_ReminderType(MToDoReminder.JP_TODO_REMINDERTYPE_Mail);
			todoReminder.setJP_ToDo_RemindTime(teamToDoreminder.getJP_ToDo_RemindTime());
			todoReminder.setDescription(teamToDoreminder.getDescription());
			todoReminder.save(trxName);
			MToDoReminder.sendMailRemainder(ctx, todoReminder, trxName);
		}

		teamToDoreminder.isProcessingReminder = true;
		teamToDoreminder.setIsSentReminderJP(true);
		teamToDoreminder.setProcessed(true);
		teamToDoreminder.saveEx(trxName);
		teamToDoreminder.isProcessingReminder = false;

		return true;
	}


}
