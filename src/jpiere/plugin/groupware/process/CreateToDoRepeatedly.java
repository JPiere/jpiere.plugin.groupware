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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_C_NonBusinessDay;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.X_C_NonBusinessDay;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

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
	private int p_JP_NonBusinessDayCalendar_ID = 0;
	private int p_C_Country_ID = 0;
	private String p_JP_NonBusinessDayToDoHandling = null;
	private boolean p_IsCopyReminderJP = false;
	private boolean p_IsScheduledStartDateEndOfMonth = false;
	private boolean p_IsScheduledEndDateEndOfMonth = false;

	private MToDo m_ToDo = null;
	private List<Long> diffList_RemindTime = new ArrayList<Long> ();

	/*** Variable ***/
	private Timestamp v_JP_ToDo_ScheduledStartTime = null;
	private Timestamp v_JP_ToDo_ScheduledEndTime = null;
	private MToDo v_ToDo = null;

	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
			{
				;
			}else if (name.equals("JP_Offset_Value")){
				p_JP_Offset_Value = para[i].getParameterAsInt();
			}else if (name.equals("JP_Repetition_Interval")){
				p_JP_Repetition_Interval = para[i].getParameterAsString();
			}else if (name.equals("DateTo")){
				p_DateTo = para[i].getParameterAsTimestamp();
			}else if (name.equals("JP_NonBusinessDayCalendar_ID")){
				p_JP_NonBusinessDayCalendar_ID = para[i].getParameterAsInt();
			}else if (name.equals("C_Country_ID")){
				p_C_Country_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_NonBusinessDayToDoHandling")){
				p_JP_NonBusinessDayToDoHandling = para[i].getParameterAsString();
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
		v_ToDo = m_ToDo;

		if(MToDo.JP_TODO_TYPE_Memo.equals(m_ToDo.getJP_ToDo_Type()))
		{
			if(getTable_ID()==0)//Process is Called From ToDoPopupWindow.
			{
				return "JP_ToDo_Memo_CouldNotCreateRepeatedly";//We can not create ToDo repeatedly that ToDo type is Memo.

			}else {//Process is Called From Personal ToDo Window

				throw new Exception(Msg.getMsg(getCtx(), "JP_ToDo_Memo_CouldNotCreateRepeatedly"));
			}
		}

		if(m_ToDo.getJP_ToDo_Team_ID() > 0)
		{

			if(getTable_ID()==0)//Process is Called From ToDoPopupWindow.
			{
				//You can not create Personal ToDo repeatedly that was created from Team ToDo.
				return "JP_ToDo_NotCreatPersonalToDoFromTeamToDo";

			}else {//Process is Called From Personal ToDo Window

				throw new Exception(Msg.getMsg(getCtx(), "JP_ToDo_NotCreatPersonalToDoFromTeamToDo"));
			}
		}


		MToDoReminder[] reminders = m_ToDo.getReminders();
		Timestamp scheduledEndTime = null;
		Timestamp remindTime = null;
		for(int i = 0; i < reminders.length; i++)
		{
			scheduledEndTime = m_ToDo.getJP_ToDo_ScheduledEndTime();
			remindTime = reminders[i].getJP_ToDo_RemindTime();
			diffList_RemindTime.add(scheduledEndTime.getTime() - remindTime.getTime());
		}

		//Check Last Day of Month
		if(p_JP_Repetition_Interval.equals("M"))//Month
		{
			LocalDate localDate = m_ToDo.getJP_ToDo_ScheduledStartDate().toLocalDateTime().toLocalDate();
			LocalDate endOfMonth =localDate.with(TemporalAdjusters.lastDayOfMonth());
			if(localDate.compareTo(endOfMonth) == 0)
			{
				p_IsScheduledStartDateEndOfMonth = true;
			}

			localDate = m_ToDo.getJP_ToDo_ScheduledEndDate().toLocalDateTime().toLocalDate();
			endOfMonth =localDate.with(TemporalAdjusters.lastDayOfMonth());
			if(localDate.compareTo(endOfMonth) == 0)
			{
				p_IsScheduledEndDateEndOfMonth = true;
			}
		}

		if(m_ToDo.getJP_Processing1().equals("Y"))
		{
			if(getTable_ID()==0)//Process is Called From ToDoPopupWindow.
			{
				return "JP_ToDo_AlreadyCreatedRepeatedly";//ToDo have already been created repeatedly. So, you can not create ToDo repeatedly.

			}else {//Process is Called From Personal ToDo Window

				throw new AdempiereException(Msg.getMsg(getCtx(), "JP_ToDo_AlreadyCreatedRepeatedly"));
			}
		}


		v_JP_ToDo_ScheduledStartTime = calculateNextScheduleTime(m_ToDo.getJP_ToDo_ScheduledStartTime());
		if(p_IsScheduledStartDateEndOfMonth)
		{
			LocalDate localDate = v_JP_ToDo_ScheduledStartTime.toLocalDateTime().toLocalDate();
			LocalDate endOfMonth =localDate.with(TemporalAdjusters.lastDayOfMonth());
			LocalTime localTime = v_JP_ToDo_ScheduledStartTime.toLocalDateTime().toLocalTime();
			v_JP_ToDo_ScheduledStartTime = Timestamp.valueOf(LocalDateTime.of(endOfMonth, localTime)) ;
		}

		v_JP_ToDo_ScheduledEndTime 	= calculateNextScheduleTime(m_ToDo.getJP_ToDo_ScheduledEndTime());
		if(p_IsScheduledEndDateEndOfMonth)
		{
			LocalDate localDate = v_JP_ToDo_ScheduledEndTime.toLocalDateTime().toLocalDate();
			LocalDate endOfMonth =localDate.with(TemporalAdjusters.lastDayOfMonth());
			LocalTime localTime = v_JP_ToDo_ScheduledEndTime.toLocalDateTime().toLocalTime();
			v_JP_ToDo_ScheduledEndTime = Timestamp.valueOf(LocalDateTime.of(endOfMonth, localTime)) ;
		}


		p_DateTo = Timestamp.valueOf(LocalDateTime.of(p_DateTo.toLocalDateTime().toLocalDate(), LocalTime.MAX));

		while(p_DateTo.compareTo(v_JP_ToDo_ScheduledStartTime) >= 0)
		{
			createToDo();

			v_JP_ToDo_ScheduledStartTime = calculateNextScheduleTime(v_JP_ToDo_ScheduledStartTime);
			if(p_IsScheduledStartDateEndOfMonth)
			{
				LocalDate localDate = v_JP_ToDo_ScheduledStartTime.toLocalDateTime().toLocalDate();
				LocalDate endOfMonth =localDate.with(TemporalAdjusters.lastDayOfMonth());
				LocalTime localTime = v_JP_ToDo_ScheduledStartTime.toLocalDateTime().toLocalTime();
				v_JP_ToDo_ScheduledStartTime = Timestamp.valueOf(LocalDateTime.of(endOfMonth, localTime)) ;
			}

			v_JP_ToDo_ScheduledEndTime = calculateNextScheduleTime(v_JP_ToDo_ScheduledEndTime);
			if(p_IsScheduledEndDateEndOfMonth)
			{
				LocalDate localDate = v_JP_ToDo_ScheduledEndTime.toLocalDateTime().toLocalDate();
				LocalDate endOfMonth =localDate.with(TemporalAdjusters.lastDayOfMonth());
				LocalTime localTime = v_JP_ToDo_ScheduledEndTime.toLocalDateTime().toLocalTime();
				v_JP_ToDo_ScheduledEndTime = Timestamp.valueOf(LocalDateTime.of(endOfMonth, localTime)) ;
			}

		}

		m_ToDo.setJP_Processing1("Y");
		m_ToDo.saveEx(get_TrxName());

		if(getTable_ID()==0)//Process is Called From ToDoPopupWindow.
		{
			return "Success";

		}else {//Process is Called From Personal ToDo Window

			return Msg.getMsg(getCtx(), "Success");

		}
	}

	private void createToDo()
	{
		MToDo new_ToDo = new MToDo(getCtx(), 0, get_TrxName());
		PO.copyValues(v_ToDo, new_ToDo);

		//ScheduledStartDate & Time
		if(MToDo.JP_TODO_TYPE_Schedule.equals(new_ToDo.getJP_ToDo_Type()))
		{
			if(Util.isEmpty(p_JP_NonBusinessDayToDoHandling) || p_JP_NonBusinessDayToDoHandling.equals("B"))//Create ToDo
			{
				new_ToDo.setJP_ToDo_ScheduledStartDate(v_JP_ToDo_ScheduledStartTime);
				new_ToDo.setJP_ToDo_ScheduledStartTime(v_JP_ToDo_ScheduledStartTime);

			}else {

				boolean isNonBusinessDay = checkNonBusinessDay(v_JP_ToDo_ScheduledStartTime.toLocalDateTime().toLocalDate());
				if(isNonBusinessDay)
				{
					if( p_JP_NonBusinessDayToDoHandling.equals("A") || p_JP_Repetition_Interval.equals("D"))//Day
					{
						return ;
					}else if( p_JP_NonBusinessDayToDoHandling.equals("C")) {//Pre Day

						Timestamp ts = preDay(v_JP_ToDo_ScheduledStartTime);
						new_ToDo.setJP_ToDo_ScheduledStartDate(ts);
						new_ToDo.setJP_ToDo_ScheduledStartTime(ts);

					}else if( p_JP_NonBusinessDayToDoHandling.equals("D")) {//next Day

						Timestamp ts = nextDay(v_JP_ToDo_ScheduledStartTime);
						new_ToDo.setJP_ToDo_ScheduledStartDate(ts);
						new_ToDo.setJP_ToDo_ScheduledStartTime(ts);
					}

				}else {

					new_ToDo.setJP_ToDo_ScheduledStartDate(v_JP_ToDo_ScheduledStartTime);
					new_ToDo.setJP_ToDo_ScheduledStartTime(v_JP_ToDo_ScheduledStartTime);

				}

			}
		}

		//ScheduledEndDate & Time
		if(Util.isEmpty(p_JP_NonBusinessDayToDoHandling))
		{
			new_ToDo.setJP_ToDo_ScheduledEndDate(v_JP_ToDo_ScheduledEndTime);
			new_ToDo.setJP_ToDo_ScheduledEndTime(v_JP_ToDo_ScheduledEndTime);
		}else {

			boolean isNonBusinessDay = checkNonBusinessDay(v_JP_ToDo_ScheduledEndTime.toLocalDateTime().toLocalDate());
			if(isNonBusinessDay)
			{
				if( p_JP_NonBusinessDayToDoHandling.equals("A") || p_JP_Repetition_Interval.equals("D"))//Do not create
				{
					return ;

				}else if( p_JP_NonBusinessDayToDoHandling.equals("C")) {//Pre Day

					Timestamp ts = preDay(v_JP_ToDo_ScheduledEndTime);
					new_ToDo.setJP_ToDo_ScheduledEndDate(ts);
					new_ToDo.setJP_ToDo_ScheduledEndTime(ts);

				}else if( p_JP_NonBusinessDayToDoHandling.equals("D")) {//next Day

					Timestamp ts = nextDay(v_JP_ToDo_ScheduledEndTime);
					new_ToDo.setJP_ToDo_ScheduledEndDate(ts);
					new_ToDo.setJP_ToDo_ScheduledEndTime(ts);
				}

			}else {

				new_ToDo.setJP_ToDo_ScheduledEndDate(v_JP_ToDo_ScheduledEndTime);
				new_ToDo.setJP_ToDo_ScheduledEndTime(v_JP_ToDo_ScheduledEndTime);

			}

		}

		//Adjust ScheduledStartDate & Time
		if(new_ToDo.getJP_ToDo_ScheduledStartDate().compareTo(new_ToDo.getJP_ToDo_ScheduledEndDate()) > 0
				|| MToDo.JP_TODO_TYPE_Task.equals(new_ToDo.getJP_ToDo_Type()))
		{
			new_ToDo.setJP_ToDo_ScheduledStartDate(v_JP_ToDo_ScheduledEndTime);
			new_ToDo.setJP_ToDo_ScheduledStartTime(v_JP_ToDo_ScheduledEndTime);
		}


		new_ToDo.setJP_ToDo_Related_ID(p_JP_ToDo_ID);
		new_ToDo.setJP_ToDo_Status(MToDoTeam.JP_TODO_STATUS_NotYetStarted);
		new_ToDo.saveEx(get_TrxName());

		if(p_IsCopyReminderJP)
			createToDoRemainder(new_ToDo);

		v_ToDo = new_ToDo;
	}

	private void createToDoRemainder(MToDo  new_ToDo)
	{
		MToDoReminder[] m_ToDoReminders = v_ToDo.getReminders();
		MToDoReminder tdr = null;
		Timestamp remindTime = null;
		for(int i = 0; i < m_ToDoReminders.length; i++)
		{
			tdr = new MToDoReminder(getCtx(), 0, get_TrxName());
			tdr.setAD_Org_ID(0);
			tdr.setJP_ToDo_ID(new_ToDo.getJP_ToDo_ID());
			tdr.setJP_ToDo_ReminderType(m_ToDoReminders[i].getJP_ToDo_ReminderType());
			tdr.setDescription(m_ToDoReminders[i].getDescription());

			//Calculate Remind Time
			remindTime = new Timestamp(new_ToDo.getJP_ToDo_ScheduledEndTime().getTime() - diffList_RemindTime.get(i));
			tdr.setJP_ToDo_RemindTime(remindTime);

			tdr.setIsSentReminderJP(false);
			tdr.saveEx(get_TrxName());
		}

	}

	private Timestamp calculateNextScheduleTime(Timestamp timestamp)
	{
		return GroupwareTeamUtil.calculateNextScheduleTime(timestamp, p_JP_Offset_Value, p_JP_Repetition_Interval);
	}

	List<X_C_NonBusinessDay> list_NonBusinessDays = null;

	private boolean checkNonBusinessDay(LocalDate localDate)
	{
		if(list_NonBusinessDays == null)
		{
			StringBuilder whereClause = null;
			StringBuilder orderClause = null;
			ArrayList<Object> list_parameters  = new ArrayList<Object>();
			Object[] parameters = null;

			LocalDateTime toDayMin = LocalDateTime.of(m_ToDo.getJP_ToDo_ScheduledStartTime().toLocalDateTime().toLocalDate(), LocalTime.MIN);
			LocalDateTime toDayMax = LocalDateTime.of(p_DateTo.toLocalDateTime().toLocalDate(), LocalTime.MAX);


			//AD_Client_ID
			whereClause = new StringBuilder(" AD_Client_ID=? ");
			list_parameters.add(Env.getAD_Client_ID(getCtx()));

			//C_Calendar_ID
			whereClause = whereClause.append(" AND C_Calendar_ID = ? ");
			list_parameters.add(p_JP_NonBusinessDayCalendar_ID);

			//Date1
			whereClause = whereClause.append(" AND Date1 <= ? AND Date1 >= ? AND IsActive='Y' ");
			list_parameters.add(Timestamp.valueOf(toDayMax));
			list_parameters.add(Timestamp.valueOf(toDayMin));

			//C_Country_ID
			if(p_C_Country_ID == 0)
			{
				whereClause = whereClause.append(" AND C_Country_ID IS NULL ");

			}else {
				whereClause = whereClause.append(" AND ( C_Country_ID IS NULL OR C_Country_ID = ? ) ");
				list_parameters.add(p_C_Country_ID);
			}

			parameters = list_parameters.toArray(new Object[list_parameters.size()]);
			orderClause = new StringBuilder("Date1");


			list_NonBusinessDays = new Query(Env.getCtx(), I_C_NonBusinessDay.Table_Name, whereClause.toString(), null)
												.setParameters(parameters)
												.setOrderBy(orderClause.toString())
												.list();
		}

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

	private Timestamp preDay(Timestamp timestamp)
	{
		LocalDate localDate = timestamp.toLocalDateTime().toLocalDate();
		LocalTime localTime = timestamp.toLocalDateTime().toLocalTime();
		boolean isNonBusinessDay = true;
		while(isNonBusinessDay)
		{
			localDate = localDate.minusDays(1);
			isNonBusinessDay = checkNonBusinessDay(localDate);
		}

		return Timestamp.valueOf(LocalDateTime.of(localDate, localTime));
	}

	private Timestamp nextDay(Timestamp timestamp)
	{
		LocalDate localDate = timestamp.toLocalDateTime().toLocalDate();
		LocalTime localTime = timestamp.toLocalDateTime().toLocalTime();
		boolean isNonBusinessDay = true;
		while(isNonBusinessDay)
		{
			localDate = localDate.plusDays(1);
			isNonBusinessDay = checkNonBusinessDay(localDate);
		}

		return Timestamp.valueOf(LocalDateTime.of(localDate, localTime));
	}
}
