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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Properties;

import org.adempiere.model.MBroadcastMessage;
import org.compiere.model.MClient;
import org.compiere.model.MMessage;
import org.compiere.model.MUser;
import org.compiere.model.MUserMail;
import org.compiere.util.DisplayType;
import org.compiere.util.EMail;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.Util;
import org.idempiere.broadcast.BroadcastMsgUtil;

/**
 * JPIERE-0469: JPiere Groupware
 *
 * MToDoReminder
 *
 * @author h.hagiwara
 *
 */
public class MToDoReminder extends X_JP_ToDo_Reminder {

	private MToDo parent = null;

	public MToDoReminder(Properties ctx, int JP_ToDo_Reminder_ID, String trxName)
	{
		super(ctx, JP_ToDo_Reminder_ID, trxName);
	}


	public MToDoReminder(Properties ctx, ResultSet rs, String trxName)
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

		if(is_ValueChanged(MToDoReminder.COLUMNNAME_IsConfirmed))
		{
			if(isConfirmed() && getJP_Confirmed() == null)
			{
				setJP_Confirmed(Timestamp.valueOf(LocalDateTime.now()));
				setProcessed(true);

			}else {

				setJP_Confirmed(null);
				setProcessed(false);
			}
		}

		//*** Check Statistics info ***//
		if(getJP_ToDo_Team_Reminder_ID() != 0 && isConfirmed() && is_ValueChanged("IsConfirmed"))
		{
			MToDoTeamReminder teamToDoReminder = new MToDoTeamReminder(getCtx(), getJP_ToDo_Team_Reminder_ID(), get_TrxName());
			if(MToDoTeamReminder.JP_MANDATORY_STATISTICS_INFO_None.equals(teamToDoReminder.getJP_Mandatory_Statistics_Info()))
			{
				;//Noting to do;

			}else if(MToDoTeamReminder.JP_MANDATORY_STATISTICS_INFO_YesNo.equals(teamToDoReminder.getJP_Mandatory_Statistics_Info())){

				if(Util.isEmpty(getJP_Statistics_YesNo()))
				{
					String msg = Msg.getElement(getCtx(), MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info);
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_Statistics_YesNo)};
					return msg + " : " + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
				}

			}else if(MToDoTeamReminder.JP_MANDATORY_STATISTICS_INFO_Choice.equals(teamToDoReminder.getJP_Mandatory_Statistics_Info())){

				if(Util.isEmpty(getJP_Statistics_Choice()))
				{
					String msg = Msg.getElement(getCtx(), MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info);
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_Statistics_Choice)};
					return msg + " : " + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
				}

			}else if(MToDoTeamReminder.JP_MANDATORY_STATISTICS_INFO_DateAndTime.equals(teamToDoReminder.getJP_Mandatory_Statistics_Info())){

				if(getJP_Statistics_DateAndTime() == null)
				{
					String msg = Msg.getElement(getCtx(), MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info);
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_Statistics_DateAndTime)};
					return msg + " : " + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
				}

			}else if(MToDoTeamReminder.JP_MANDATORY_STATISTICS_INFO_Number.equals(teamToDoReminder.getJP_Mandatory_Statistics_Info())){

				if(get_Value("JP_Statistics_Number") == null)
				{
					String msg = Msg.getElement(getCtx(), MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info);
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_Statistics_Number)};
					return msg + " : " + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
				}
			}

		}//if(getJP_ToDo_Team_Reminder_ID() != 0)

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



	private MToDo getParent()
	{
		if(parent == null)
			parent = new MToDo(getCtx(), getJP_ToDo_ID(), get_TrxName());

		return parent;
	}

	static public boolean sendMailRemainder(Properties ctx, int JP_ToDo_Reminder_ID, String trxName)//TODO
	{

		return sendMailRemainder(ctx, new MToDoReminder(ctx, JP_ToDo_Reminder_ID, trxName) , trxName);
	}

	static public boolean sendMailRemainder(Properties ctx, MToDoReminder reminder, String trxName)//TODO
	{
		int AD_Client_ID = Env.getAD_Client_ID(ctx);
		MClient client =  MClient.get(ctx, AD_Client_ID);
		MToDo todo = new MToDo(ctx, reminder.getJP_ToDo_ID(), trxName);
		MUser to = new MUser (ctx, todo.getAD_User_ID(), trxName);

		String subject = Msg.getElement(ctx, MToDoReminder.COLUMNNAME_JP_ToDo_Reminder_ID) + " : "+todo.getName();
		StringBuilder message = new StringBuilder();

		SimpleDateFormat sdfV = DisplayType.getDateFormat();

		if(MToDo.JP_TODO_TYPE_Schedule.equals(todo.getJP_ToDo_Type()))
		{
			Date startDate = new Date(todo.getJP_ToDo_ScheduledStartDate().getTime());
			String string_StartDate = sdfV.format(startDate);
			message.append(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime)).append(" : ").append(string_StartDate);
			if(todo.isStartDateAllDayJP())
			{
				message.append(System.lineSeparator());

			}else {

				String string_StartTime = todo.getJP_ToDo_ScheduledStartTime().toLocalDateTime().toLocalTime().toString();
				message.append(" ").append(string_StartTime).append(System.lineSeparator());
			}
		}

		if(MToDo.JP_TODO_TYPE_Schedule.equals(todo.getJP_ToDo_Type()) || MToDo.JP_TODO_TYPE_Task.equals(todo.getJP_ToDo_Type()))
		{
			Date endDate = new Date(todo.getJP_ToDo_ScheduledEndDate().getTime());
			String string_EndDate = sdfV.format(endDate);

			message.append(Msg.getElement(ctx, MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime)).append(" : ").append(string_EndDate);
			if(todo.isEndDateAllDayJP())
			{
				message.append(System.lineSeparator());

			}else {

				String string_EndTime = todo.getJP_ToDo_ScheduledEndTime().toLocalDateTime().toLocalTime().toString();
				message.append(" ").append(string_EndTime).append(System.lineSeparator());
			}
		}

		if(MToDo.JP_TODO_TYPE_Memo.equals(todo.getJP_ToDo_Type()))
		{
			;
		}else {
			message.append(System.lineSeparator());
		}

		message.append(reminder.getDescription());

		EMail email = client.createEMail(to.getEMail(), subject, message.toString(), false);

		boolean isOK = EMail.SENT_OK.equals(email.send());
		if(isOK)
		{
			reminder.isProcessingReminder = true;
			reminder.setIsSentReminderJP(true);
			reminder.setProcessed(true);
			reminder.saveEx(trxName);
			reminder.isProcessingReminder = false;
		}

		MUserMail userMail = new MUserMail(ctx, 0, trxName);
		userMail.setMessageID(MToDo.COLUMNNAME_JP_ToDo_ID +" = "+ todo.getJP_ToDo_ID() + " - " + MToDoReminder.COLUMNNAME_JP_ToDo_Reminder_ID +" = "+ reminder.getJP_ToDo_Reminder_ID());
		userMail.setAD_User_ID(todo.getAD_User_ID());
		userMail.setEMailFrom(client.getRequestEMail());
		userMail.setRecipientTo(to.getEMail());
		userMail.setSubject(subject);
		userMail.setMailText(message.toString());
		userMail.setIsDelivered(isOK ? "Y" : "N");
		userMail.save(trxName);

		return true;
	}

	static public boolean sendMessageRemainder(Properties ctx, int JP_ToDo_Reminder_ID, String trxName)//TODO
	{
		return sendMessageRemainder(ctx, new MToDoReminder(ctx, JP_ToDo_Reminder_ID, trxName) , trxName);
	}

	static public boolean sendMessageRemainder(Properties ctx, MToDoReminder reminder, String trxName)//TODO
	{
		MBroadcastMessage bm = new MBroadcastMessage(ctx, 0, trxName);
		bm.setAD_Org_ID(reminder.getAD_Org_ID());
		bm.setBroadcastMessage(reminder.getDescription());
//		bm.setBroadcastType(MBroadcastMessage.BROADCASTTYPE_Login);
		bm.setBroadcastType(MBroadcastMessage.BROADCASTTYPE_ImmediatePlusLogin);
		bm.setTarget(MBroadcastMessage.TARGET_User);
		bm.setAD_User_ID(reminder.getParent().getAD_User_ID());
		bm.setBroadcastFrequency(MBroadcastMessage.BROADCASTFREQUENCY_UntilAcknowledge);
		//bm.setExpiration(reminder.getParent().getJP_ToDo_ScheduledEndTime());//TODOメモの時の対応
		bm.saveEx(trxName);
		Trx.get(trxName, true).commit();

		BroadcastMsgUtil.publishBroadcastMessage(bm.getAD_BroadcastMessage_ID(), trxName);

		return true;
	}

}
