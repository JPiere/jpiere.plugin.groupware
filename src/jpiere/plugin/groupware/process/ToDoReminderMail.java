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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

import jpiere.plugin.groupware.model.MToDoReminder;
import jpiere.plugin.groupware.model.MToDoTeamReminder;


/**
 * JPIERE-0480: Send Mail Process of ToDo Reminder
 *
 * @author h.hagiwara
 *
 */
public class ToDoReminderMail extends SvrProcess {

	private int p_AD_Client_ID = 0;

	private int record_ID = 0;
	private MTable m_Table = null;
	private LocalDateTime now = LocalDateTime.now();

	private long plusMin = 5;

	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			;
		}

		record_ID = getRecord_ID();
		p_AD_Client_ID = getAD_Client_ID();

		int AD_Table_ID = getProcessInfo().getTable_ID();
		if(AD_Table_ID != 0)
			m_Table = MTable.get(getCtx(), AD_Table_ID);
	}

	@Override
	protected String doIt() throws Exception
	{
		if(m_Table == null)
		{
			createPersonalToDoRemainderFromTeamToDoReminder();
			sendMailFromPersonalToDoRemainder();

		}else if(MToDoReminder.Table_Name.equals(m_Table.getTableName())) {

			if(record_ID == 0)
			{
				sendMailFromPersonalToDoRemainder();
			}else {

				MToDoReminder reminder = new MToDoReminder(getCtx(), record_ID, get_TrxName());
				if(!sendMailFromPersonalToDoRemainder(reminder))
				{
					addLog(0, null, null, Msg.getMsg(getCtx(), "RequestActionEMailError")+ " : JP_ToDo_Reminder_ID = " + reminder.get_ID()
									,MTable.getTable_ID(MToDoReminder.Table_Name), reminder.get_ID() );
				}
			}

		}else if(MToDoTeamReminder.Table_Name.equals(m_Table.getTableName())) {

			if(record_ID == 0)
			{
				createPersonalToDoRemainderFromTeamToDoReminder();
			}else {
				createPersonalToDoRemainderFromTeamToDoReminder(new MToDoTeamReminder(getCtx(), record_ID, get_TrxName()));
			}

		}else {

			createPersonalToDoRemainderFromTeamToDoReminder();
			sendMailFromPersonalToDoRemainder();

		}

		return Msg.getMsg(getCtx(), "Success");
	}

	private boolean sendMailFromPersonalToDoRemainder() throws SQLException
	{
		Timestamp remindTime = Timestamp.valueOf(now.plusMinutes(plusMin));
		StringBuilder whereClauseFinal = new StringBuilder(" AD_Client_ID = ? AND Processed = 'N' ")
														.append(" AND JP_ToDo_ReminderType = 'M' AND IsActive = 'Y'  AND ")
														.append(MToDoReminder.COLUMNNAME_JP_SendMailNextTime + " <= ? ");
		List<MToDoReminder> list = new Query(getCtx(), MToDoReminder.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(p_AD_Client_ID, remindTime)
										.list();

		for(MToDoReminder reminder : list)
		{
			if(!sendMailFromPersonalToDoRemainder(reminder))
			{
				addLog(Msg.getMsg(getCtx(), "RequestActionEMailError")+ " : JP_ToDo_Reminder_ID = " + reminder.get_ID());
			}

			commitEx();
		}

		return true;
	}


	private boolean sendMailFromPersonalToDoRemainder(MToDoReminder reminder )
	{
		int AD_UserMail_ID = reminder.sendMailRemainder();
		if(AD_UserMail_ID > 0)
		{
			reminder.saveEx();
			return true;
		}else {
			return false;
		}

	}


	private boolean createPersonalToDoRemainderFromTeamToDoReminder() throws SQLException
	{
		Timestamp remindTime = Timestamp.valueOf(now.plusMinutes(plusMin));
		StringBuilder whereClauseFinal = new StringBuilder(" AD_Client_ID = ? AND Processed = 'N' ")
														.append(" AND IsSentReminderJP = 'N' AND JP_ToDo_ReminderType = 'M' AND IsActive = 'Y' AND ")
														.append(MToDoTeamReminder.COLUMNNAME_JP_ToDo_RemindTime + " <= ? ");
		List<MToDoTeamReminder> list = new Query(getCtx(), MToDoTeamReminder.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(p_AD_Client_ID, remindTime)
										.list();

		for(MToDoTeamReminder reminder : list)
		{
			createPersonalToDoRemainderFromTeamToDoReminder(reminder);
			commitEx();
		}

		return true;
	}

	private boolean createPersonalToDoRemainderFromTeamToDoReminder(MToDoTeamReminder reminder)
	{
		return reminder.createPersonalToDoRemainder();
	}
}
