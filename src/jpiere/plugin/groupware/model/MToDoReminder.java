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
import org.compiere.model.Query;
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
public class MToDoReminder extends X_JP_ToDo_Reminder implements I_ToDoReminder {

	public static final String COLUMNNAME_JP_ToDo_RemindDate = "JP_ToDo_RemindDate";
	private MToDo parent = null;

	protected String		m_RemindMsg = null;

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

		if(!newRecord && is_ValueChanged(MToDoReminder.COLUMNNAME_IsConfirmed) && isConfirmed() && getJP_Confirmed() == null)
		{
			setJP_Confirmed(Timestamp.valueOf(LocalDateTime.now()));
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

	public boolean setMailRemainder()
	{
		return false;
	}

	public boolean sendMailRemainder()
	{
		MClient client =  MClient.get(getCtx(), getAD_Client_ID());
		MToDo todo = new MToDo(getCtx(), getJP_ToDo_ID(), get_TrxName());
		MUser to = new MUser (getCtx(), todo.getAD_User_ID(), get_TrxName());

		String subject = Msg.getElement(getCtx(), MToDoReminder.COLUMNNAME_JP_ToDo_Reminder_ID) + " : "+todo.getName();
		StringBuilder message = new StringBuilder();

		SimpleDateFormat sdfV = DisplayType.getDateFormat();

		if(MToDo.JP_TODO_TYPE_Schedule.equals(todo.getJP_ToDo_Type()))
		{
			Date startDate = new Date(todo.getJP_ToDo_ScheduledStartDate().getTime());
			String string_StartDate = sdfV.format(startDate);
			message.append(Msg.getElement(getCtx(), MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime)).append(" : ").append(string_StartDate);
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

			message.append(Msg.getElement(getCtx(), MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime)).append(" : ").append(string_EndDate);
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

		message.append(getDescription());

		EMail email = client.createEMail(to.getEMail(), subject, message.toString(), false);

		boolean isOK = EMail.SENT_OK.equals(email.send());

		if(isOK)
		{
			this.setIsSentReminderJP(true);
		}else {
			m_RemindMsg = Msg.getMsg(getCtx(), "RequestActionEMailError");
		}

		MUserMail userMail = new MUserMail(getCtx(), 0, get_TrxName());
		userMail.setMessageID(MToDo.COLUMNNAME_JP_ToDo_ID +" = "+ todo.getJP_ToDo_ID() + " - " + MToDoReminder.COLUMNNAME_JP_ToDo_Reminder_ID +" = "+ getJP_ToDo_Reminder_ID());
		userMail.setAD_User_ID(todo.getAD_User_ID());
		userMail.setEMailFrom(client.getRequestEMail());
		userMail.setRecipientTo(to.getEMail());
		userMail.setSubject(isOK ? "": (m_RemindMsg+" > ") + subject);
		userMail.setMailText(message.toString());
		userMail.setIsDelivered(isOK ? "Y" : "N");
		userMail.save(get_TrxName());

		this.setAD_UserMail_ID(userMail.getAD_UserMail_ID());
		this.isProcessingReminder = true;
		this.saveEx(get_TrxName());
		this.isProcessingReminder = false;

		return isOK;
	}

	public boolean setMessageRemainder()
	{
		return false;
	}


	public int sendMessageRemainder()
	{
		MBroadcastMessage bm = new MBroadcastMessage(getCtx(), 0, get_TrxName());
		bm.setAD_Org_ID(getAD_Org_ID());
		bm.setBroadcastType(MBroadcastMessage.BROADCASTTYPE_ImmediatePlusLogin);
		bm.setTarget(MBroadcastMessage.TARGET_User);
		bm.setAD_User_ID(getParent().getAD_User_ID());

		int login_User_ID = Env.getAD_User_ID(getCtx());
		getParent();
		if(parent.getAD_User_ID() == login_User_ID)
		{
			bm.setBroadcastType(MBroadcastMessage.BROADCASTTYPE_Login);
		}else {
			bm.setBroadcastType(MBroadcastMessage.BROADCASTTYPE_ImmediatePlusLogin);
		}


		if(Util.isEmpty(getBroadcastFrequency()))
		{
			bm.setBroadcastFrequency(MBroadcastMessage.BROADCASTFREQUENCY_UntilAcknowledge);

		}else if(MToDoReminder.BROADCASTFREQUENCY_UntilAcknowledge.equals(getBroadcastFrequency()) ){

			bm.setBroadcastFrequency(MBroadcastMessage.BROADCASTFREQUENCY_UntilAcknowledge);

		}else if(MToDoReminder.BROADCASTFREQUENCY_UntilComplete.equals(getBroadcastFrequency()) ){

			bm.setBroadcastFrequency(MBroadcastMessage.BROADCASTFREQUENCY_UntilExpiration);
			bm.setExpiration(Timestamp.valueOf("9999-12-31 00:00:00"));

		}else if(MToDoReminder.BROADCASTFREQUENCY_UntilScheduledEndTime.equals(getBroadcastFrequency()) ){

			bm.setBroadcastFrequency(MBroadcastMessage.BROADCASTFREQUENCY_UntilExpiration);
			bm.setExpiration(parent.getJP_ToDo_ScheduledEndTime());

		}else if(MToDoReminder.BROADCASTFREQUENCY_JustOnce.equals(getBroadcastFrequency()) ){

			bm.setBroadcastFrequency(MBroadcastMessage.BROADCASTFREQUENCY_JustOnce);

		}else if(MToDoReminder.BROADCASTFREQUENCY_UntilScheduledEndTimeOrComplete.equals(getBroadcastFrequency()) ){

			bm.setBroadcastFrequency(MBroadcastMessage.BROADCASTFREQUENCY_UntilExpiration);
			bm.setExpiration(parent.getJP_ToDo_ScheduledEndTime());

		}else if(MToDoReminder.BROADCASTFREQUENCY_UntilScheduledEndTimeOrAcknowledge.equals(getBroadcastFrequency()) ){

			bm.setBroadcastFrequency(MBroadcastMessage.BROADCASTFREQUENCY_UntilExpirationOrAcknowledge);
			bm.setExpiration(parent.getJP_ToDo_ScheduledEndTime());
		}


		SimpleDateFormat sdfV = DisplayType.getDateFormat();
		StringBuilder message = new StringBuilder();

		String p_Start = "<p class=\"z-label\">";
		String p_Start_Description = "<p class=\"z-label\" style=\"text-align:left;\">";
		String p_End = "</p>";
		String br = "<BR />";

		message.append("<h4>").append(parent.getName()).append(p_End).append("</h4>");

		if(MToDo.JP_TODO_TYPE_Schedule.equals(parent.getJP_ToDo_Type()))
		{
			Date startDate = new Date(parent.getJP_ToDo_ScheduledStartDate().getTime());
			String string_StartDate = sdfV.format(startDate);
			message.append(p_Start).append(Msg.getElement(getCtx(), MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime)).append(" : ").append(string_StartDate);
			if(parent.isStartDateAllDayJP())
			{
				;

			}else {

				String string_StartTime = parent.getJP_ToDo_ScheduledStartTime().toLocalDateTime().toLocalTime().toString();
				message.append(" ").append(string_StartTime);
			}

			message.append(p_End);
		}

		if(MToDo.JP_TODO_TYPE_Schedule.equals(parent.getJP_ToDo_Type()) || MToDo.JP_TODO_TYPE_Task.equals(parent.getJP_ToDo_Type()))
		{
			Date endDate = new Date(parent.getJP_ToDo_ScheduledEndDate().getTime());
			String string_EndDate = sdfV.format(endDate);

			message.append(p_Start).append(Msg.getElement(getCtx(), MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime)).append(" : ").append(string_EndDate);
			if(parent.isEndDateAllDayJP())
			{
				;

			}else {

				String string_EndTime = parent.getJP_ToDo_ScheduledEndTime().toLocalDateTime().toLocalTime().toString();
				message.append(" ").append(string_EndTime);
			}

			message.append(p_End);
		}

		if(MToDo.JP_TODO_TYPE_Memo.equals(parent.getJP_ToDo_Type()))
		{
			;
		}else {

			message.append(br);
		}

		message.append(p_Start_Description).append(getDescription()).append(p_End);

		bm.setBroadcastMessage(message.toString());

		bm.saveEx(get_TrxName());
		if(get_TrxName() != null)
			Trx.get(get_TrxName(), true).commit();

		BroadcastMsgUtil.publishBroadcastMessage(bm.getAD_BroadcastMessage_ID(), get_TrxName());

		return bm.getAD_BroadcastMessage_ID();
	}


	@Override
	public void setJP_Mandatory_Statistics_Info(String JP_Mandatory_Statistics_Info)
	{
		;
	}


	@Override
	public String getJP_Mandatory_Statistics_Info()
	{
		return null;
	}


	@Override
	public void setUpdated(Timestamp updated)
	{
		set_ValueNoCheck("Updated", updated);
	}


	@Override
	public int getJP_ToDo_Team_ID()
	{
		return 0;
	}


	@Override
	public void setJP_ToDo_Team_ID(int JP_ToDo_Team_ID)
	{
		;
	}

	@Override
	public String getRemindMsg()
	{
		return m_RemindMsg;
	}	//	getProcessMsg


	static public MToDoReminder getFromBroadcastMessage(Properties ctx, int AD_BroadcastMessage_ID, String trxName)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MToDoReminder.COLUMNNAME_AD_BroadcastMessage_ID+"=? ");
		String		orderClause = "";

		MToDoReminder reminder = new Query(ctx, MToDoReminder.Table_Name, whereClauseFinal.toString(), trxName)
										.setParameters(AD_BroadcastMessage_ID)
										.setOrderBy(orderClause)
										.first();

		return reminder;
	}

}
