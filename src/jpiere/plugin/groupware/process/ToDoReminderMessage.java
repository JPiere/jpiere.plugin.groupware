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
import java.util.List;

import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

import jpiere.plugin.groupware.model.MToDoReminder;
import jpiere.plugin.groupware.model.MToDoTeamReminder;


/**
 * JPIERE-0480: Send Message Process of ToDo Reminder
 *
 * @author h.hagiwara
 *
 */
public class ToDoReminderMessage extends SvrProcess {

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
			sendMessagePersonalToDoRemainder();

		}else if(MToDoReminder.Table_Name.equals(m_Table.getTableName())) {

			if(record_ID == 0)
			{
				sendMessagePersonalToDoRemainder();
			}else {
				sendMessagePersonalToDoRemainder(new MToDoReminder(getCtx(), record_ID, get_TrxName()));
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
			sendMessagePersonalToDoRemainder();

		}

		return Msg.getMsg(getCtx(), "Success");
	}

	private boolean sendMessagePersonalToDoRemainder() throws Exception
	{
		Timestamp remindTime = Timestamp.valueOf(now.plusMinutes(plusMin));
		StringBuilder whereClauseFinal = new StringBuilder(" AD_Client_ID = ? ")
												.append(" AND IsSentReminderJP = 'N' AND JP_ToDo_ReminderType = 'B' AND ")
												.append(MToDoReminder.COLUMNNAME_JP_ToDo_RemindTime + " <= ? ");
		List<MToDoReminder> list = new Query(getCtx(), MToDoReminder.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(p_AD_Client_ID, remindTime)
										.list();

		for(MToDoReminder reminder : list)
		{
			sendMessagePersonalToDoRemainder(reminder);
			commitEx();
		}

		return true;
	}

	private boolean sendMessagePersonalToDoRemainder(MToDoReminder todoReminder) throws Exception
	{
		int AD_BroadcastMessage_ID = todoReminder.sendMessageRemainder();
		todoReminder.setAD_BroadcastMessage_ID(AD_BroadcastMessage_ID);
		todoReminder.setIsSentReminderJP(true);
		todoReminder.saveEx();

		if(AD_BroadcastMessage_ID > 0)
		{
			return true;
		}else {
			return false;
		}

	}

	private boolean createPersonalToDoRemainderFromTeamToDoReminder() throws Exception
	{
		Timestamp remindTime = Timestamp.valueOf(now.plusMinutes(plusMin));
		StringBuilder whereClauseFinal = new StringBuilder(" AD_Client_ID = ? ")
												.append(" AND IsSentReminderJP = 'N' AND JP_ToDo_ReminderType = 'B' AND ")
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

	private boolean createPersonalToDoRemainderFromTeamToDoReminder(MToDoTeamReminder reminder) throws Exception
	{
		return reminder.createPersonalToDoRemainder();
	}
}
