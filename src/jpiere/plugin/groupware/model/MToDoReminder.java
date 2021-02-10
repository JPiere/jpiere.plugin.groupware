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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.adempiere.model.MBroadcastMessage;
import org.adempiere.webui.apps.AEnv;
import org.compiere.model.I_C_NonBusinessDay;
import org.compiere.model.MClient;
import org.compiere.model.MMessage;
import org.compiere.model.MUser;
import org.compiere.model.MUserMail;
import org.compiere.model.Query;
import org.compiere.model.X_C_NonBusinessDay;
import org.compiere.util.DB;
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

	public void setProcessingReminder(boolean isProcessingReminder)
	{
		this.isProcessingReminder = isProcessingReminder;
	}


	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		String msg = beforeSavePreCheck(newRecord);
		if(!Util.isEmpty(msg))
		{
			log.saveError("Error", msg);
			return false;
		}

		if(newRecord)
		{
			setIsConfirmed(false);
			setJP_Confirmed(null);
		}

		if(!newRecord && is_ValueChanged(MToDoReminder.COLUMNNAME_IsConfirmed) && isConfirmed() && getJP_Confirmed() == null)
		{
			setJP_Confirmed(Timestamp.valueOf(LocalDateTime.now()));
		}

		if(newRecord || (is_ValueChanged(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime) && !isSentReminderJP()) )
		{
			if(MToDoReminder.JP_TODO_REMINDERTYPE_SendMail.equals(getJP_ToDo_ReminderType()))
				setJP_SendMailNextTime(getJP_ToDo_RemindTime());
			else
				setJP_SendMailNextTime(null);
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
		if(getJP_ToDo_Team_Reminder_ID() != 0 && isConfirmed())
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


	public int sendMailRemainder()
	{
		MClient client =  MClient.get(getCtx(), getAD_Client_ID());
		MToDo todo = new MToDo(getCtx(), getJP_ToDo_ID(), get_TrxName());
		MUser to = new MUser (getCtx(), todo.getAD_User_ID(), get_TrxName());

		String subject = Msg.getElement(getCtx(), MToDoReminder.COLUMNNAME_JP_ToDo_Reminder_ID) + " : "+todo.getName();

		StringBuilder message = new StringBuilder(todo.getName()).append(System.lineSeparator());
		try
		{
			if(!Util.isEmpty(AEnv.getApplicationUrl()))
				message.append(AEnv.getZoomUrlTableID(todo)).append(System.lineSeparator()).append(System.lineSeparator());
		}catch (Exception e) {
			;
		}

		SimpleDateFormat sdfV = DisplayType.getDateFormat();

		if(MToDo.JP_TODO_TYPE_Schedule.equals(todo.getJP_ToDo_Type()))
		{
			Date startDate = new Date(todo.getJP_ToDo_ScheduledStartDate().getTime());
			String string_StartDate = sdfV.format(startDate);

			if(todo.isStartDateAllDayJP())
			{
				message.append(Msg.getElement(getCtx(), MToDo.COLUMNNAME_JP_ToDo_ScheduledStartDate)).append(" : ").append(string_StartDate);
				message.append(System.lineSeparator());

			}else {

				message.append(Msg.getElement(getCtx(), MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime)).append(" : ").append(string_StartDate);
				String string_StartTime = todo.getJP_ToDo_ScheduledStartTime().toLocalDateTime().toLocalTime().toString();
				message.append(" ").append(string_StartTime).append(System.lineSeparator());
			}
		}

		if(MToDo.JP_TODO_TYPE_Schedule.equals(todo.getJP_ToDo_Type()) || MToDo.JP_TODO_TYPE_Task.equals(todo.getJP_ToDo_Type()))
		{
			Date endDate = new Date(todo.getJP_ToDo_ScheduledEndDate().getTime());
			String string_EndDate = sdfV.format(endDate);

			if(todo.isEndDateAllDayJP())
			{
				message.append(Msg.getElement(getCtx(), MToDo.COLUMNNAME_JP_ToDo_ScheduledEndDate)).append(" : ").append(string_EndDate);
				message.append(System.lineSeparator());

			}else {

				message.append(Msg.getElement(getCtx(), MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime)).append(" : ").append(string_EndDate);
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

		if(!Util.isEmpty(getURL()))
		{
			message.append(Msg.getElement(getCtx(), MToDoReminder.COLUMNNAME_URL) + " ").append(getURL());
			message.append(System.lineSeparator());
			message.append(System.lineSeparator());
		}

		message.append(getDescription()).append(System.lineSeparator()).append(System.lineSeparator());

		try
		{
			String reminderURL = Msg.getElement(getCtx(), MToDoReminder.COLUMNNAME_JP_ToDo_Reminder_ID) + " : "+ AEnv.getZoomUrlTableID(this);
			message.append(reminderURL).append(System.lineSeparator());
		}catch (Exception e) {
			//noting to do;
		}


		EMail email = client.createEMail(to.getEMail(), subject, message.toString(), false);

		boolean isOK = EMail.SENT_OK.equals(email.send());

		if(isOK)
		{
			setIsSentReminderJP(true);
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

		setAD_UserMail_ID(userMail.getAD_UserMail_ID());
		isProcessingReminder = true;
		if(isOK)
			updateSendMailNextTime();
		saveEx(get_TrxName());
		isProcessingReminder = false;

		MToDoReminderLog reminderlog = new MToDoReminderLog(getCtx(), 0, get_TrxName());
		reminderlog.setJP_ToDo_Reminder_ID(getJP_ToDo_Reminder_ID());

		if(!isOK)
		{
			reminderlog.setIsError(true);
			reminderlog.setDescription(m_RemindMsg);

		}else {
			reminderlog.setAD_UserMail_ID(userMail.getAD_UserMail_ID());
			reminderlog.setIsError(false);
		}

		if(reminderlog.save(get_TrxName()))
		{
			if(get_TrxName() != null)
				Trx.get(get_TrxName(), true).commit();
		}

		if(isOK)
		{
			return userMail.getAD_UserMail_ID();
		}else {
			return 0;
		}
	}

	private void updateSendMailNextTime()
	{
		if(!MToDoReminder.JP_TODO_REMINDERTYPE_SendMail.equals(getJP_ToDo_ReminderType()))
			return;

		if(MToDoReminder.JP_MAILFREQUENCY_JustOne.equals(getJP_MailFrequency()))
		{
			setProcessed(true);
			return ;
		}

		LocalDate date = LocalDate.now();
		LocalTime time = getJP_ToDo_RemindTime().toLocalDateTime().toLocalTime();
		Timestamp JP_SendMailNextTime = nextDay(date, time);

		if(MToDoReminder.JP_MAILFREQUENCY_OnceADayUntilAcknowledge.equals(getJP_MailFrequency()))
		{
			if(isConfirmed())
			{
				setProcessed(true);
			}else {
				setJP_SendMailNextTime(JP_SendMailNextTime);
			}

		}else if(MToDoReminder.JP_MAILFREQUENCY_OnceADayUntilComplete.equals(getJP_MailFrequency())){

			if(MToDo.JP_TODO_STATUS_Completed.equals(getParent().getJP_ToDo_Status()))
			{
				setProcessed(true);
			}else {
				setJP_SendMailNextTime(JP_SendMailNextTime);
			}

		}else if(MToDoReminder.JP_MAILFREQUENCY_OnceADayUntilScheduledEndTime.equals(getJP_MailFrequency())){

			Timestamp scheduledEndTime =	getParent().getJP_ToDo_ScheduledEndTime();
			if(scheduledEndTime == null)
			{
				setJP_SendMailNextTime(JP_SendMailNextTime);

			}else if(scheduledEndTime.compareTo(JP_SendMailNextTime) >= 0) {

				setJP_SendMailNextTime(JP_SendMailNextTime);

			}else {

				setProcessed(true);
			}

		}else if(MToDoReminder.JP_MAILFREQUENCY_OnceADayUntilScheduledEndTimeOrAcknowledge.equals(getJP_MailFrequency())){

			Timestamp scheduledEndTime =	getParent().getJP_ToDo_ScheduledEndTime();
			if(isConfirmed())
			{
				setProcessed(true);

			}else if(scheduledEndTime == null) {

				setJP_SendMailNextTime(JP_SendMailNextTime);

			}else if(scheduledEndTime.compareTo(JP_SendMailNextTime) >= 0) {

				setJP_SendMailNextTime(JP_SendMailNextTime);

			}else {

				setProcessed(true);
			}

		}else if(MToDoReminder.JP_MAILFREQUENCY_OnceADayUntilScheduledEndTimeOrComplete.equals(getJP_MailFrequency())){

			Timestamp scheduledEndTime =	getParent().getJP_ToDo_ScheduledEndTime();
			if(MToDo.JP_TODO_STATUS_Completed.equals(getParent().getJP_ToDo_Status()))
			{
				setProcessed(true);

			}else if(scheduledEndTime == null) {

				setJP_SendMailNextTime(JP_SendMailNextTime);

			}else if(scheduledEndTime.compareTo(JP_SendMailNextTime) >= 0) {

				setJP_SendMailNextTime(JP_SendMailNextTime);

			}else {

				setProcessed(true);

			}
		}
	}

	private Timestamp nextDay(LocalDate localDate, LocalTime localTime)
	{
		boolean isNonBusinessDay = true;
		while(isNonBusinessDay)
		{
			localDate = localDate.plusDays(1);
			isNonBusinessDay = checkNonBusinessDay(localDate);
		}

		return Timestamp.valueOf(LocalDateTime.of(localDate, localTime));
	}

	private boolean checkNonBusinessDay(LocalDate localDate)
	{
		MGroupwareUser m_GroupwareUser = MGroupwareUser.get(getCtx(), getParent().getAD_User_ID());
		if(m_GroupwareUser == null)
			return false;

		StringBuilder whereClause = null;
		StringBuilder orderClause = null;
		ArrayList<Object> list_parameters  = new ArrayList<Object>();
		Object[] parameters = null;

		LocalDateTime toDayMin = LocalDateTime.of(localDate, LocalTime.MIN);
		LocalDateTime toDayMax = LocalDateTime.of(localDate, LocalTime.MAX);

		//AD_Client_ID
		whereClause = new StringBuilder(" AD_Client_ID=? ");
		list_parameters.add(Env.getAD_Client_ID(getCtx()));

		//C_Calendar_ID
		whereClause = whereClause.append(" AND C_Calendar_ID = ? ");
		list_parameters.add(m_GroupwareUser.getJP_NonBusinessDayCalendar_ID());

		//Date1
		whereClause = whereClause.append(" AND Date1 <= ? AND Date1 >= ? AND IsActive='Y' ");
		list_parameters.add(Timestamp.valueOf(toDayMax));
		list_parameters.add(Timestamp.valueOf(toDayMin));

		//C_Country_ID
		if(m_GroupwareUser.getC_Country_ID() == 0)
		{
			whereClause = whereClause.append(" AND C_Country_ID IS NULL ");

		}else {
			whereClause = whereClause.append(" AND ( C_Country_ID IS NULL OR C_Country_ID = ? ) ");
			list_parameters.add(m_GroupwareUser.getC_Country_ID());
		}

		parameters = list_parameters.toArray(new Object[list_parameters.size()]);
		orderClause = new StringBuilder("Date1");


		List<X_C_NonBusinessDay> list_NonBusinessDays = new Query(Env.getCtx(), I_C_NonBusinessDay.Table_Name, whereClause.toString(), null)
											.setParameters(parameters)
											.setOrderBy(orderClause.toString())
											.list();

		boolean isNonBusinessDay = false;
		for(X_C_NonBusinessDay nonBusinessDay : list_NonBusinessDays )
		{
			if(nonBusinessDay.getDate1().toLocalDateTime().toLocalDate().compareTo(localDate) == 0 )
			{
				isNonBusinessDay = true;
				break;
			}
		}

		return isNonBusinessDay;

	}


	public int sendMessageRemainder()
	{
		int AD_BroadcastMessage_ID = 0;
		MBroadcastMessage bm = new MBroadcastMessage(getCtx(), AD_BroadcastMessage_ID, get_TrxName());
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
			bm.setExpiration(parent.getJP_ToDo_ScheduledEndTime() == null ? Timestamp.valueOf("9999-12-31 00:00:00") : parent.getJP_ToDo_ScheduledEndTime());

		}else if(MToDoReminder.BROADCASTFREQUENCY_JustOnce.equals(getBroadcastFrequency()) ){

			bm.setBroadcastFrequency(MBroadcastMessage.BROADCASTFREQUENCY_JustOnce);

		}else if(MToDoReminder.BROADCASTFREQUENCY_UntilScheduledEndTimeOrComplete.equals(getBroadcastFrequency()) ){

			bm.setBroadcastFrequency(MBroadcastMessage.BROADCASTFREQUENCY_UntilExpiration);
			bm.setExpiration(parent.getJP_ToDo_ScheduledEndTime() == null ? Timestamp.valueOf("9999-12-31 00:00:00") : parent.getJP_ToDo_ScheduledEndTime());

		}else if(MToDoReminder.BROADCASTFREQUENCY_UntilScheduledEndTimeOrAcknowledge.equals(getBroadcastFrequency()) ){

			bm.setBroadcastFrequency(MBroadcastMessage.BROADCASTFREQUENCY_UntilExpirationOrAcknowledge);
			bm.setExpiration(parent.getJP_ToDo_ScheduledEndTime() == null ? Timestamp.valueOf("9999-12-31 00:00:00") : parent.getJP_ToDo_ScheduledEndTime());
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
			if(parent.isStartDateAllDayJP())
			{
				message.append(p_Start).append(Msg.getElement(getCtx(), MToDo.COLUMNNAME_JP_ToDo_ScheduledStartDate)).append(" : ").append(string_StartDate);
			}else {
				message.append(p_Start).append(Msg.getElement(getCtx(), MToDo.COLUMNNAME_JP_ToDo_ScheduledStartTime)).append(" : ").append(string_StartDate);
				String string_StartTime = parent.getJP_ToDo_ScheduledStartTime().toLocalDateTime().toLocalTime().toString();
				message.append(" ").append(string_StartTime);
			}

			message.append(p_End);
		}

		if(MToDo.JP_TODO_TYPE_Schedule.equals(parent.getJP_ToDo_Type()) || MToDo.JP_TODO_TYPE_Task.equals(parent.getJP_ToDo_Type()))
		{
			Date endDate = new Date(parent.getJP_ToDo_ScheduledEndDate().getTime());
			String string_EndDate = sdfV.format(endDate);
			if(parent.isEndDateAllDayJP())
			{
				message.append(p_Start).append(Msg.getElement(getCtx(), MToDo.COLUMNNAME_JP_ToDo_ScheduledEndDate)).append(" : ").append(string_EndDate);

			}else {

				message.append(p_Start).append(Msg.getElement(getCtx(), MToDo.COLUMNNAME_JP_ToDo_ScheduledEndTime)).append(" : ").append(string_EndDate);
				String string_EndTime = parent.getJP_ToDo_ScheduledEndTime().toLocalDateTime().toLocalTime().toString();
				message.append(" ").append(string_EndTime);
			}

			message.append(p_End);
		}

		if(!Util.isEmpty(getURL()))
		{
			message.append(p_Start).append(Msg.getElement(getCtx(), MToDoReminder.COLUMNNAME_URL) + " ")
									.append("<a href=\"").append(getURL()).append("\"  target=\"_blank\">")
									.append(getURL()).append("</a>").append(p_End);
		}

		if(MToDo.JP_TODO_TYPE_Memo.equals(parent.getJP_ToDo_Type()))
		{
			;
		}else {

			message.append(br);
		}

		message.append(p_Start_Description).append(getDescription()).append(p_End);


		bm.setBroadcastMessage(message.toString());

		if(bm.save(get_TrxName()))
		{
			if(get_TrxName() != null)
				Trx.get(get_TrxName(), true).commit();

			BroadcastMsgUtil.publishBroadcastMessage(bm.getAD_BroadcastMessage_ID(), get_TrxName());

		}else {

			m_RemindMsg = Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "AD_BroadcastMessage_ID");
			AD_BroadcastMessage_ID = -1;
		}

		AD_BroadcastMessage_ID = bm.getAD_BroadcastMessage_ID();
		if(AD_BroadcastMessage_ID > 0)
		{
			setAD_BroadcastMessage_ID(AD_BroadcastMessage_ID);
			setIsSentReminderJP(true);
			setProcessingReminder(true);
			saveEx(get_TrxName());
			setProcessingReminder(false);
		}

		//log
		MToDoReminderLog reminderlog = new MToDoReminderLog(getCtx(), 0, get_TrxName());
		reminderlog.setJP_ToDo_Reminder_ID(getJP_ToDo_Reminder_ID());

		if(AD_BroadcastMessage_ID <= 0)
		{
			reminderlog.setIsError(true);
			reminderlog.setDescription(m_RemindMsg);

		}else {
			reminderlog.setAD_BroadcastMessage_ID(AD_BroadcastMessage_ID);
			reminderlog.setIsError(false);
		}

		if(reminderlog.save(get_TrxName()))
		{
			if(get_TrxName() != null)
				Trx.get(get_TrxName(), true).commit();
		}



		return AD_BroadcastMessage_ID;
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


	@Override
	public void setJP_ToDo_RemindTarget(String JP_ToDo_RemindTarget)
	{
		;
	}


	@Override
	public String getJP_ToDo_RemindTarget()
	{
		return null;
	}

	public boolean stopBroadcastMessage()
	{
		if(getAD_BroadcastMessage_ID() > 0)
		{
			MBroadcastMessage mbMessage = new MBroadcastMessage(Env.getCtx(), getAD_BroadcastMessage_ID(), get_TrxName() );
			if (!mbMessage.isExpired() && mbMessage.isPublished())
			{
				String sql = "UPDATE AD_Note SET Processed='Y' WHERE AD_BroadcastMessage_ID = ?";
				DB.executeUpdateEx(sql, new Object[] {getAD_BroadcastMessage_ID()}, null);
				mbMessage.setProcessed(true);
				mbMessage.setExpired(true);
				mbMessage.saveEx();
			}

		}else {
			return false;
		}
		return true;
	}


	public boolean processedReminder()
	{
		stopBroadcastMessage();
		setProcessed(true);
		saveEx(get_TrxName());

		return true;
	}


	public boolean reprocessReminder()
	{
		if(isProcessed() == false)
			return false;

		if(MToDoReminder.BROADCASTFREQUENCY_UntilComplete.equals(getBroadcastFrequency())
				|| (MToDoReminder.BROADCASTFREQUENCY_UntilAcknowledge.equals(getBroadcastFrequency()) && !isConfirmed()) )
		{
			int AD_BroadcastMessage_ID = sendMessageRemainder();
			setAD_BroadcastMessage_ID(AD_BroadcastMessage_ID);
			setProcessed(false);
			saveEx(get_TrxName());

			return true;

		}else if(MToDoReminder.BROADCASTFREQUENCY_UntilScheduledEndTimeOrComplete.equals(getBroadcastFrequency())
				|| MToDoReminder.BROADCASTFREQUENCY_UntilScheduledEndTime.equals(getBroadcastFrequency())
				|| (MToDoReminder.BROADCASTFREQUENCY_UntilScheduledEndTimeOrAcknowledge.equals(getBroadcastFrequency()) && !isConfirmed()) 	) {

			if(getParent().getJP_ToDo_ScheduledEndTime() == null || getParent().getJP_ToDo_ScheduledEndTime().compareTo(Timestamp.valueOf(LocalDateTime.now())) > 0)
			{
				int AD_BroadcastMessage_ID = sendMessageRemainder();
				setAD_BroadcastMessage_ID(AD_BroadcastMessage_ID);
				setProcessed(false);
				saveEx(get_TrxName());

				return true;
			}

		} else if(MToDoReminder.BROADCASTFREQUENCY_JustOnce.equals(getBroadcastFrequency())) {

			if(!isSentReminderJP())
			{
				setProcessed(false);
				saveEx(get_TrxName());

				return true;
			}

		}else if(MToDoReminder.JP_MAILFREQUENCY_OnceADayUntilComplete.equals(getJP_MailFrequency())
				|| (MToDoReminder.JP_MAILFREQUENCY_OnceADayUntilAcknowledge.equals(getJP_MailFrequency()) && !isConfirmed()) ) {

			setProcessed(false);
			saveEx(get_TrxName());

			return true;

		}else if(MToDoReminder.JP_MAILFREQUENCY_OnceADayUntilScheduledEndTimeOrComplete.equals(getJP_MailFrequency())
					|| MToDoReminder.JP_MAILFREQUENCY_OnceADayUntilScheduledEndTime.equals(getJP_MailFrequency())
					|| (MToDoReminder.JP_MAILFREQUENCY_OnceADayUntilScheduledEndTimeOrAcknowledge.equals(getJP_MailFrequency())&& !isConfirmed()) ) {

			if(getParent().getJP_ToDo_ScheduledEndTime() == null || getParent().getJP_ToDo_ScheduledEndTime().compareTo(Timestamp.valueOf(LocalDateTime.now())) > 0)
			{
				setProcessed(false);
				saveEx(get_TrxName());

				return true;
			}


		}else if(MToDoReminder.JP_MAILFREQUENCY_JustOne.equals(getJP_MailFrequency())) {

			if(!isSentReminderJP())
			{
				setProcessed(false);
				saveEx(get_TrxName());

				return true;
			}

		}

		return false;
	}


	@Override
	public int getJP_Team_ID()
	{
		return 0;
	}


	@Override
	public void setJP_Team_ID(int JP_Team_ID)
	{
		;
	}

}
