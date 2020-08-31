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
	private static final long JUDGMENT_Middle_TIME_HOURES = 12;
	private static final long JUDGMENT_SHORT_TIME_MINUTE = 30;

	private LocalDate begin_LocalDate = null;
	private LocalTime begin_LocalTime = null;

	private LocalTime end_LocalTime  	= null;
	private LocalDate end_LocalDate		= null;


	/** Type **/
	private boolean isSameDate 	= false;
	public boolean isLongTime 		= false;
	public boolean isMiddleTime 	= false;
	public boolean isShortTime 	= false;


	/**Text**/
	public String personal_Month_Long_Title  = null;
	public String personal_Month_Long_Content = null;
	public String personal_Month_Middle_Title  = null;
	public String personal_Month_Middle_Content = null;
	public String personal_Month_Short_Title  = null;
	public String personal_Month_Short_Content = null;

	public String team_Month_Long_Title  = null;
	public String team_Month_Long_Content = null;
	public String team_Month_Middle_Title  = null;
	public String team_Month_Middle_Content = null;
	public String team_Month_Short_Title  = null;
	public String team_Month_Short_Content = null;

	public String personal_Default_Long_Title  = null;
	public String personal_Default_Long_Content = null;
	public String personal_Default_Middle_Title  = null;
	public String personal_Default_Middle_Content = null;
	public String personal_Default_Short_Title  = null;
	public String personal_Default_Short_Content = null;

	public String team_Default_Long_Title  = null;
	public String team_Default_Long_Content = null;
	public String team_Default_Middle_Title  = null;
	public String team_Default_Middle_Content = null;
	public String team_Default_Short_Title  = null;
	public String team_Default_Short_Content = null;


	/**Color**/
	public String personal_Month_Long_HeaderColor  = null;
	public String personal_Month_Long_ContentColor = null;
	public String personal_Month_Middle_HeaderColor = null;
	public String personal_Month_Middle_ContentColor = null;
	public String personal_Month_Short_HeaderColor = null;
	public String personal_Month_Short_ContentColor = null;

	public String team_Month_Long_HeaderColor = null;
	public String team_Month_Long_ContentColor = null;
	public String team_Month_Middle_HeaderColor  = null;
	public String team_Month_Middle_ContentColor = null;
	public String team_Month_Short_HeaderColor= null;
	public String team_Month_Short_ContentColor = null;

	public String personal_Default_Long_HeaderColor  = null;
	public String personal_Default_Long_ContentColor = null;
	public String personal_Default_Middle_HeaderColor  = null;
	public String personal_Default_Middle_ContentColor = null;
	public String personal_Default_Short_HeaderColor  = null;
	public String personal_Default_Short_ContentColor = null;

	public String team_Default_Long_HeaderColor  = null;
	public String team_Default_Long_ContentColor = null;
	public String team_Default_Middle_HeaderColor  = null;
	public String team_Default_Middle_ContentColor = null;
	public String team_Default_Short_HeaderColor  = null;
	public String team_Default_Short_ContentColor = null;


	public ToDoCalendarEvent(MToDo toDo)
	{
		super();
		this.m_ToDo = toDo;

		adjustTimeToZK();
		adjustDisplayText();
		setColor();

		int login_AD_User_ID = Env.getAD_User_ID(Env.getCtx());
		if(m_ToDo.getAD_User_ID() == login_AD_User_ID || m_ToDo.getCreatedBy() == login_AD_User_ID)
			this.setLocked(false);
		else
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


			isMiddleTime =judgmentOfMiddleTime(begin_Timestamp, end_Timestamp);



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

			if(m_ToDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime().toLocalTime() == LocalTime.MIN )
			{
				end_LocalTime = LocalTime.MAX;

			}else {

				end_LocalTime = begin_LocalTime.plusHours(INITIAL_TASK_HOUR);
				if(begin_LocalTime.compareTo(end_LocalTime) < 0)//In case of end_LocalTime is Tommorow
				{
					;//Noting to do
				}else {
					end_LocalTime = LocalTime.MAX;
				}

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

		if(MToDo.JP_TODO_TYPE_Schedule.equals(m_ToDo.getJP_ToDo_Type()))
		{
			if(isSameDate)
			{
				if(isMiddleTime)
				{
					isLongTime = false;
					isMiddleTime = true;
					isShortTime = false;

					String begin_FormatTime = (begin_LocalTime == null ? null :  begin_LocalTime.toString().substring(0, 5));
					String end_FormatTime = 	(end_LocalTime == null ? null :  end_LocalTime.toString().substring(0, 5));

					if(begin_LocalTime == LocalTime.MIN && end_LocalTime == LocalTime.MAX)
					{
						//Month - Middle
						personal_Month_Middle_Title   = m_ToDo.getName();
						personal_Month_Middle_Content = (" ") +  m_ToDo.getName();
						team_Month_Middle_Title 	= m_ToDo.getName();
						team_Month_Middle_Content 	= userName +  m_ToDo.getName() ;

						//Default - Middle
						personal_Default_Middle_Title   = personal_Month_Middle_Title;
						personal_Default_Middle_Content = personal_Month_Middle_Content;
						team_Default_Middle_Title 	= team_Month_Middle_Title;
						team_Default_Middle_Content 	= team_Month_Middle_Content;

					}else {

						//Month - Middle
						personal_Month_Middle_Title = m_ToDo.getName();
						personal_Month_Middle_Content = begin_FormatTime + " - " + end_FormatTime +  (" ")  +  m_ToDo.getName();
						team_Month_Middle_Title = m_ToDo.getName();
						team_Month_Middle_Content = begin_FormatTime + " - " + end_FormatTime +  userName + " ";


						//Default - Middle
						personal_Default_Middle_Title = personal_Month_Middle_Title;
						personal_Default_Middle_Content = personal_Month_Middle_Content;
						team_Default_Middle_Title = team_Month_Middle_Title;
						team_Default_Middle_Content = team_Month_Middle_Content;

					}

				}else {

					isLongTime = false;
					isMiddleTime = false;
					isShortTime = true;

					//Month - Short
					personal_Month_Short_Title = null;
					personal_Month_Short_Content =(" ") +  m_ToDo.getName();
					team_Month_Short_Title = null;
					team_Month_Short_Content = userName +  m_ToDo.getName();

					//Default - Short
					personal_Default_Short_Title = m_ToDo.getName();
					personal_Default_Short_Content = m_ToDo.getDescription();
					team_Default_Short_Title = userName;
					team_Default_Short_Content = m_ToDo.getName();

				}

			}else { //Long Text Space -> Dispay Content Text

				isLongTime = true;
				isMiddleTime = false;
				isShortTime = false;

				String begin_FromatDate = (begin_LocalTime == null ? null : GroupwareToDoUtil.dateFormat(begin_LocalDate));
				String end_FromatDate = (end_LocalTime == null ? null : GroupwareToDoUtil.dateFormat(end_LocalDate));

				this.setTitle(null);
				if(begin_LocalTime == LocalTime.MIN )
				{
					if(end_LocalTime == LocalTime.MAX)
					{
						//Month - Long
						personal_Month_Long_Title = null;
						personal_Month_Long_Content = begin_FromatDate + " - " + end_FromatDate +  (" ")   + m_ToDo.getName();
						team_Month_Long_Title = null;
						team_Month_Long_Content = begin_FromatDate + " - " + end_FromatDate +  userName + m_ToDo.getName();


						//Default - Long
						personal_Default_Long_Title = personal_Month_Long_Title;
						personal_Default_Long_Content = personal_Month_Long_Content;
						team_Default_Long_Title = team_Month_Long_Title;
						team_Default_Long_Content = team_Month_Long_Content;

					}else {

						//Month - Long
						personal_Month_Long_Title = null;
						personal_Month_Long_Content = begin_FromatDate + " - " + end_FromatDate +" "+ end_LocalTime.toString().substring(0, 5) + " " + (" ")   + m_ToDo.getName();
						team_Month_Long_Title = null;
						team_Month_Long_Content = begin_FromatDate + " - " + end_FromatDate +" "+ end_LocalTime.toString().substring(0, 5) + " " + userName  + m_ToDo.getName();


						//Default - Long
						personal_Default_Long_Title = personal_Month_Long_Title;
						personal_Default_Long_Content = personal_Month_Long_Content;
						team_Default_Long_Title = team_Month_Long_Title;
						team_Default_Long_Content = team_Month_Long_Content;
					}


				}else if(end_LocalTime == LocalTime.MAX){

					//Month - Long
					personal_Month_Long_Content = begin_FromatDate+" "+begin_LocalTime.toString().substring(0, 5)+ " - "	+ end_FromatDate + (" ")   + m_ToDo.getName();
					team_Month_Long_Content  = begin_FromatDate+" "+begin_LocalTime.toString().substring(0, 5)+ " - " 	+ end_FromatDate + userName  + m_ToDo.getName();

					//Default - Long
					personal_Default_Long_Content = personal_Month_Long_Content;
					team_Default_Long_Content = team_Month_Long_Content;


				}else {

					//Month - Long
					personal_Month_Long_Content = begin_FromatDate+" "+begin_LocalTime.toString().substring(0, 5)+ " - " + end_FromatDate +" "+ end_LocalTime.toString().substring(0, 5) + " " + (" ")   + m_ToDo.getName();
					team_Month_Long_Content = begin_FromatDate+" "+begin_LocalTime.toString().substring(0, 5)+ " - " + end_FromatDate +" "+ end_LocalTime.toString().substring(0, 5) + " " + userName   + m_ToDo.getName();

					//Default - Long
					personal_Default_Long_Content = personal_Month_Long_Content;
					team_Default_Long_Content = team_Month_Long_Content;
				}

			}

		}else if(MToDo.JP_TODO_TYPE_Task.equals(m_ToDo.getJP_ToDo_Type())) {


			isLongTime = false;

			if(m_ToDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime().toLocalTime() == LocalTime.MIN )
			{
				isMiddleTime = true;
				isShortTime = false;

				//Month - Middle
				personal_Month_Middle_Title = null;
				personal_Month_Middle_Content = (" ") +  m_ToDo.getName();
				team_Month_Middle_Title = null;
				team_Month_Middle_Content = userName +  m_ToDo.getName() ;

				//Default - Midle
				personal_Default_Middle_Title = null;
				personal_Default_Middle_Content = m_ToDo.getName() + " " +  m_ToDo.getDescription() ;
				team_Default_Middle_Title = null;
				team_Default_Middle_Content = userName + " " + m_ToDo.getName() ;

			}else {

				isMiddleTime = false;
				isShortTime = true;

				//Month - Short
				personal_Month_Short_Title = null;
				personal_Month_Short_Content =(" ") +  m_ToDo.getName();
				team_Month_Short_Title = null;
				team_Month_Short_Content =userName +  m_ToDo.getName();

				//Default - Short
				personal_Default_Short_Title = m_ToDo.getName();
				personal_Default_Short_Content = m_ToDo.getName() + " " + m_ToDo.getDescription();
				team_Default_Short_Title = userName;
				team_Default_Short_Content = userName + " " + m_ToDo.getName();
			}


		}
	}//adjustDisplayText




	/**
	 * Set Color from JPiere to ZK Calendar Event
	 */
	private void setColor()
	{

		if(m_ToDo.getJP_ToDo_Category_ID() > 0)
		{
			 MToDoCategory category = MToDoCategory.get(m_ToDo.getCtx(), m_ToDo.getJP_ToDo_Category_ID());

			 //Month - Long
			 personal_Month_Long_HeaderColor  = category.getJP_ColorPicker();
			 personal_Month_Long_ContentColor = category.getJP_ColorPicker();

			//Month - Middle
			 personal_Month_Middle_HeaderColor  = category.getJP_ColorPicker();
			 personal_Month_Middle_ContentColor = category.getJP_ColorPicker();

			//Month - Short
			 personal_Month_Short_HeaderColor  = category.getJP_ColorPicker();
			 personal_Month_Short_ContentColor = category.getJP_ColorPicker();

			 //Default - Long
			 personal_Default_Long_HeaderColor  = category.getJP_ColorPicker();
			 personal_Default_Long_ContentColor  = category.getJP_ColorPicker();

			 //Default - Middle
			 personal_Default_Middle_HeaderColor  = category.getJP_ColorPicker();
			 personal_Default_Middle_ContentColor  = category.getJP_ColorPicker();

			//Default - Short
			 personal_Default_Short_HeaderColor  = category.getJP_ColorPicker();
			 personal_Default_Short_ContentColor  = category.getJP_ColorPicker2();


		}

		MGroupwareUser gUser = MGroupwareUser.get(m_ToDo.getCtx(), m_ToDo.getAD_User_ID());

		if(gUser != null)
		{
			 //Month - Long
			 team_Month_Long_HeaderColor  = gUser.getJP_ColorPicker();
			 team_Month_Long_ContentColor = gUser.getJP_ColorPicker();

			//Month - Middle
			 team_Month_Middle_HeaderColor  = gUser.getJP_ColorPicker();
			 team_Month_Middle_ContentColor = gUser.getJP_ColorPicker();

			//Month - Short
			 team_Month_Short_HeaderColor  = gUser.getJP_ColorPicker();
			 team_Month_Short_ContentColor = gUser.getJP_ColorPicker();

			 //Default - Long
			 team_Default_Long_HeaderColor  = gUser.getJP_ColorPicker();
			 team_Default_Long_ContentColor  = gUser.getJP_ColorPicker();

			 //Default - Middle
			 team_Default_Middle_HeaderColor  = gUser.getJP_ColorPicker();
			 team_Default_Middle_ContentColor  = gUser.getJP_ColorPicker();

			//Default - Short
			 team_Default_Short_HeaderColor  = gUser.getJP_ColorPicker();
			 team_Default_Short_ContentColor  = gUser.getJP_ColorPicker2();
		}


	}//setColor



	/**
	 * Util metod
	 *
	 * @param bigin
	 * @param end
	 * @return
	 */
	private boolean judgmentOfMiddleTime(Timestamp begin, Timestamp end)
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
			if(scheduleTime >= JUDGMENT_Middle_TIME_HOURES)
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

	public MToDo getToDo()
	{
		return m_ToDo;
	}

}
