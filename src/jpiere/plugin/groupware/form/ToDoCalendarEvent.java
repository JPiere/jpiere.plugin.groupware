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
package jpiere.plugin.groupware.form;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import org.compiere.model.MUser;
import org.compiere.util.Env;
import org.zkoss.calendar.Calendars;
import org.zkoss.calendar.impl.SimpleCalendarEvent;

import jpiere.plugin.groupware.model.MGroupwareUser;
import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoCategory;
import jpiere.plugin.groupware.util.GroupwareToDoUtil;

/**
*
* JPIERE-0471: ToDo Calendar
*
* h.hagiwara
*
*/
public class ToDoCalendarEvent extends SimpleCalendarEvent {

	private static final long serialVersionUID = 2289841014956779967L;

	private MToDo m_ToDo = null ;

	private static final int INITIAL_TASK_HOUR = 1;
	private static final long JUDGMENT_LONG_TIME_HOURES = 12;
	private static final long JUDGMENT_SHORT_TIME_MINUTE = 30;

	private LocalDate begin_LocalDate = null;
	private LocalTime begin_LocalTime = null;

	private LocalTime end_LocalTime  = null;
	private LocalDate end_LocalDate = null;

	private boolean isSameDate = false;	//for Change Dispay Text Info;
	private boolean isLongTime = false;	//for Change Dispay Text Info;
	private boolean isShortTime = false;	//For Adjust Display Area;

	private Calendars calendars= null;
	private boolean isDisplayUserName = false;

	public ToDoCalendarEvent(MToDo toDo, Calendars calendars, boolean isDisplayUserName)
	{
		super();
		this.m_ToDo = toDo;
		this.calendars = calendars;
		this.isDisplayUserName = isDisplayUserName;

		adjustTimeToZK();
		adjustDisplayText();
		setColor();

		this.setLocked(true);
	}


	/**
	 * Adjust Time form Timestamp of JPiere to Bigen data and End data of ZK Calendar Event	 *
	 */
	private void adjustTimeToZK()
	{

		Timestamp begin_Timestamp = m_ToDo.getJP_ToDo_ScheduledStartTime();
		Timestamp end_Timestamp = m_ToDo.getJP_ToDo_ScheduledEndTime();

		if(m_ToDo.getJP_ToDo_Type().equals(MToDo.JP_TODO_TYPE_Schedule))
		{

			/********************************************************************************************************
			 * Adjust  Begin Time
			 ********************************************************************************************************/

			begin_LocalDate = begin_Timestamp.toLocalDateTime().toLocalDate();
			begin_LocalTime = begin_Timestamp.toLocalDateTime().toLocalTime();
			this.setBeginDate(new Date(begin_Timestamp.getTime()));



			/********************************************************************************************************
			 * Adjust End Time
			 ********************************************************************************************************/
			end_Timestamp = m_ToDo.getJP_ToDo_ScheduledEndTime();
			end_LocalDate = end_Timestamp.toLocalDateTime().toLocalDate();
			end_LocalTime = end_Timestamp.toLocalDateTime().toLocalTime();

			isSameDate =(begin_LocalDate.compareTo(end_LocalDate) == 0);
			isShortTime =judgmentOfShortTime(begin_Timestamp, end_Timestamp);

			//00:00 is considered All day
			if(end_LocalTime.compareTo(LocalTime.MIN) == 0)
			{
				end_LocalTime = LocalTime.MAX;
			}


			if(begin_Timestamp.compareTo(end_Timestamp) >= 0)
			{
				if(begin_Timestamp.compareTo(end_Timestamp) == 0 && begin_LocalTime.equals(LocalTime.MIN))
				{
					end_LocalTime = LocalTime.MAX;

				}else {

					end_LocalTime = begin_LocalTime.plusMinutes(JUDGMENT_SHORT_TIME_MINUTE);
					if(begin_LocalTime.compareTo(end_LocalTime) < 0)
					{
						;//Noting to do
					}else {
						end_LocalTime = LocalTime.MAX;
					}

				}

			}else {

				if(isShortTime)
				{
					end_LocalTime = begin_LocalTime.plusMinutes(JUDGMENT_SHORT_TIME_MINUTE);
				}
			}

			end_Timestamp = Timestamp.valueOf(LocalDateTime.of(end_Timestamp.toLocalDateTime().toLocalDate(), end_LocalTime));
			this.setEndDate(new Date(end_Timestamp.getTime()));


			isLongTime =judgmentOfLongTime(begin_Timestamp, end_Timestamp);



		}else if(m_ToDo.getJP_ToDo_Type().equals(MToDo.JP_TODO_TYPE_Task)) {


			/********************************************************************************************************
			 * Adjust Begin Time
			 ********************************************************************************************************/

			begin_LocalTime = end_Timestamp.toLocalDateTime().toLocalTime();
			this.setBeginDate(new Date(end_Timestamp.getTime()));


			/********************************************************************************************************
			 * Adjust  End Time
			 ********************************************************************************************************/

			end_LocalTime = end_Timestamp.toLocalDateTime().toLocalTime();

			end_LocalTime = begin_LocalTime.plusHours(INITIAL_TASK_HOUR);
			if(begin_LocalTime.compareTo(end_LocalTime) < 0)
			{
				;//Noting to do
			}else {
				end_LocalTime = LocalTime.MAX;
			}

			end_Timestamp = Timestamp.valueOf(LocalDateTime.of(end_Timestamp.toLocalDateTime().toLocalDate(), end_LocalTime));
			this.setEndDate(new Date(end_Timestamp.getTime()));
		}
	}//adjustTimeToZK



	/**
	 * Adjustment Text form JPiere ToDo to ZK Calendar Event
	 */
	private void adjustDisplayText()
	{
		String userName = " [" +MUser.get(Env.getCtx(), m_ToDo.getAD_User_ID()).getName() + "] " ;
		String calendarMold = calendars.getMold();

		if(MToDo.JP_TODO_TYPE_Schedule.equals(m_ToDo.getJP_ToDo_Type()))
		{
			if(isSameDate)
			{
				if(isLongTime)
				{
					String begin_FormatTime = (begin_LocalTime == null ? null :  begin_LocalTime.toString().substring(0, 5));
					String end_FormatTime = 	(end_LocalTime == null ? null :  end_LocalTime.toString().substring(0, 5));

					if(begin_LocalTime == LocalTime.MIN && end_LocalTime == LocalTime.MAX)
					{
						this.setTitle(m_ToDo.getName());
						this.setContent((isDisplayUserName? userName :" ") +  m_ToDo.getName() );
					}else {
						this.setTitle(m_ToDo.getName());
						this.setContent(begin_FormatTime + " - " + end_FormatTime +  (isDisplayUserName? userName :" ")  + (isDisplayUserName ? " " : m_ToDo.getName()) );
					}

				}else {

					if(GroupwareToDoUtil.CALENDAR_MONTH_VIEW.equalsIgnoreCase(calendarMold))
					{
						this.setTitle(null);
						this.setContent((isDisplayUserName ? userName :" ") +  m_ToDo.getName());

					}else {

						this.setTitle(isDisplayUserName ? userName :  m_ToDo.getName());
						this.setContent(isDisplayUserName ? m_ToDo.getName() : m_ToDo.getDescription());
					}
				}

			}else { //Long Text Space -> Dispay Content Text

				String begin_FromatDate = (begin_LocalTime == null ? null : GroupwareToDoUtil.dateFormat(begin_LocalDate));
				String end_FromatDate = (end_LocalTime == null ? null : GroupwareToDoUtil.dateFormat(end_LocalDate));

				this.setTitle(m_ToDo.getName());
				this.setContent(begin_FromatDate + " - " + end_FromatDate +  (isDisplayUserName? userName :" ")   + m_ToDo.getName());

			}

		}else if(MToDo.JP_TODO_TYPE_Task.equals(m_ToDo.getJP_ToDo_Type())) {


			if(GroupwareToDoUtil.CALENDAR_MONTH_VIEW.equalsIgnoreCase(calendarMold))
			{
				if(m_ToDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime().toLocalTime() == LocalTime.MIN )//TODO
				{
					this.setTitle(m_ToDo.getName());
					this.setContent((isDisplayUserName ? userName :" ") +  m_ToDo.getName() );

				}else {
					String begin_FormatTime = (begin_LocalTime == null ? null :  begin_LocalTime.toString().substring(0, 5));
					this.setTitle(m_ToDo.getName());
					this.setContent(begin_FormatTime + (isDisplayUserName ? userName :" ") +  m_ToDo.getName() );
				}

			}else {

				this.setTitle(isDisplayUserName? userName : m_ToDo.getName());
				this.setContent(isDisplayUserName? m_ToDo.getName() :  m_ToDo.getDescription());


			}
		}
	}//adjustDisplayText


	/**
	 * Set Color from JPiere to ZK Calendar Event
	 */
	private void setColor()
	{
		String calendarMold = calendars.getMold();

		if(!isDisplayUserName && m_ToDo.getJP_ToDo_Category_ID() > 0)
		{
			MToDoCategory category = MToDoCategory.get(m_ToDo.getCtx(), m_ToDo.getJP_ToDo_Category_ID());

			if(GroupwareToDoUtil.CALENDAR_MONTH_VIEW.equalsIgnoreCase(calendarMold))
			{

				this.setHeaderColor(category.getJP_ColorPicker());
				this.setContentColor(category.getJP_ColorPicker());

			}else {

				if(isLongTime)
				{
					this.setHeaderColor(category.getJP_ColorPicker());
					this.setContentColor(category.getJP_ColorPicker());
				}else {
					this.setHeaderColor(category.getJP_ColorPicker());
					this.setContentColor(category.getJP_ColorPicker2());
				}

			}

		}else if(isDisplayUserName){

			MGroupwareUser gUser = MGroupwareUser.get(m_ToDo.getCtx(), m_ToDo.getAD_User_ID());

			if(gUser != null)
			{
				if(GroupwareToDoUtil.CALENDAR_MONTH_VIEW.equalsIgnoreCase(calendarMold))
				{

					this.setHeaderColor(gUser.getJP_ColorPicker());
					this.setContentColor(gUser.getJP_ColorPicker());

				}else {

					if(isLongTime)
					{
						this.setHeaderColor(gUser.getJP_ColorPicker());
						this.setContentColor(gUser.getJP_ColorPicker());
					}else {
						this.setHeaderColor(gUser.getJP_ColorPicker());
						this.setContentColor(gUser.getJP_ColorPicker2());
					}

				}
			}
		}

	}//setColor



	/**
	 * Util metod
	 *
	 * @param bigin
	 * @param end
	 * @return
	 */
	private boolean judgmentOfLongTime(Timestamp begin, Timestamp end)
	{
		//Adjust Begin Time
		LocalDate begin_LocalDate = begin.toLocalDateTime().toLocalDate();
		LocalTime begin_LocalTime = begin.toLocalDateTime().toLocalTime();

		//Adjust End Time
		LocalDate end_LocalDate = end.toLocalDateTime().toLocalDate();
		LocalTime end_LocalTime = end.toLocalDateTime().toLocalTime();

		if((begin_LocalDate.compareTo(end_LocalDate) == 0))
		{
			int scheduleTime = end_LocalTime.minusHours(begin_LocalTime.getHour()).getHour();
			if(scheduleTime >= JUDGMENT_LONG_TIME_HOURES)
			{
				return true;
			}else {
				return false;
			}

		}else {

			return true;
		}

	}

	private boolean judgmentOfShortTime(Timestamp begin, Timestamp end)
	{
		//Adjust Begin Time
		LocalDate begin_LocalDate = begin.toLocalDateTime().toLocalDate();
		LocalTime begin_LocalTime = begin.toLocalDateTime().toLocalTime();

		//Adjust End Time
		LocalDate end_LocalDate = end.toLocalDateTime().toLocalDate();
		LocalTime end_LocalTime = end.toLocalDateTime().toLocalTime();

		if((begin_LocalDate.compareTo(end_LocalDate) == 0))
		{
			int scheduleHour = end_LocalTime.minusHours(begin_LocalTime.getHour()).getHour();
			if(scheduleHour < 1)
			{
				int sheduleMinute = end_LocalTime.minusMinutes(begin_LocalTime.getMinute()).getMinute();
				if(sheduleMinute < JUDGMENT_SHORT_TIME_MINUTE)
				{
					return true;
				}else {
					return false;
				}
			}else {
				return false;
			}

		}else {

			return false;
		}

	}

	public MToDo getToDoD()
	{
		return m_ToDo;
	}


}
