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

	/**
	 *
	 */
	private static final long serialVersionUID = 2289841014956779967L;
	private static final int TASK_HOUR = 1;

	private MToDo m_ToDo = null ;

	public ToDoCalendarEvent(MToDo toDo, String calendarMold, boolean isDisplayUserName)
	{
		super();
		this.m_ToDo = toDo;

		if(m_ToDo.getJP_ToDo_Type().equals(MToDo.JP_TODO_TYPE_Schedule))
		{

			/********************************************************************************************************
			 * Adjust  Begin Time
			 ********************************************************************************************************/
			Timestamp begin_Timestamp = toDo.getJP_ToDo_ScheduledStartTime();
			LocalDate begin_LocalDate = begin_Timestamp.toLocalDateTime().toLocalDate();
			LocalTime begin_LocalTime = begin_Timestamp.toLocalDateTime().toLocalTime();
			this.setBeginDate(new Date(begin_Timestamp.getTime()));

			/********************************************************************************************************
			 * Adjust End Time
			 ********************************************************************************************************/
			Timestamp end_Timestamp = toDo.getJP_ToDo_ScheduledEndTime();
			LocalDate end_LocalDate = end_Timestamp.toLocalDateTime().toLocalDate();
			LocalTime end_LocalTime = end_Timestamp.toLocalDateTime().toLocalTime();
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

					end_LocalTime = begin_LocalTime.plusMinutes(GroupwareToDoUtil.JUDGMENT_SHORT_TIME_MINUTE);
					if(begin_LocalTime.compareTo(end_LocalTime) < 0)
					{
						;//Noting to do
					}else {
						end_LocalTime = LocalTime.MAX;
					}

				}
			}else {

				//For Adjust Display Area
				boolean isShortTime =GroupwareToDoUtil.judgmentOfShortTime(begin_Timestamp, end_Timestamp);
				if(isShortTime)
				{
					end_LocalTime = begin_LocalTime.plusMinutes(GroupwareToDoUtil.JUDGMENT_SHORT_TIME_MINUTE);
				}
			}

			end_Timestamp = Timestamp.valueOf(LocalDateTime.of(end_Timestamp.toLocalDateTime().toLocalDate(), end_LocalTime));
			this.setEndDate(new Date(end_Timestamp.getTime()));


			/********************************************************************************************************
			 * Adjust Display Info
			 ********************************************************************************************************/
			boolean isSameDate =(begin_LocalDate.compareTo(end_LocalDate) == 0);
			boolean isLongTime =GroupwareToDoUtil.judgmentOfLongTime(begin_Timestamp, end_Timestamp);

			if(GroupwareToDoUtil.BUTTON_ONEDAY_VIEW.equals(calendarMold))
			{
				if(isDisplayUserName)
				{
					if(isSameDate)
					{
						if(isLongTime)
						{
							this.setTitle(toDo.getName());

							if(begin_LocalTime == LocalTime.MIN && end_LocalTime == LocalTime.MAX)
							{
								this.setContent(begin_LocalTime.toString().substring(0, 5) + " [" +  MUser.get(Env.getCtx(), toDo.getAD_User_ID()).getName() + "] " + toDo.getName());
							}else {
								this.setContent(begin_LocalTime.toString().substring(0, 5) + " - " + end_LocalTime.toString().substring(0, 5) + " ["  +  MUser.get(Env.getCtx(), toDo.getAD_User_ID()).getName()+ "] ");
							}
						}else {

							this.setTitle(" [" + MUser.get(Env.getCtx(), toDo.getAD_User_ID()).getName()+ "] " );
							this.setContent(toDo.getName());
						}

					}else {

						this.setTitle(toDo.getName());
						this.setContent(GroupwareToDoUtil.dateFormat(begin_LocalDate) + " - " + GroupwareToDoUtil.dateFormat(end_LocalDate) +" [" +  MUser.get(Env.getCtx(), toDo.getAD_User_ID()).getName() + "] " + toDo.getName());

					}


				}else {

					if(isSameDate)
					{
						if(isLongTime)
						{
							this.setTitle(toDo.getName());

							if(begin_LocalTime == LocalTime.MIN && end_LocalTime == LocalTime.MAX)
							{
								this.setContent(begin_LocalTime.toString().substring(0, 5) + " : " +  toDo.getName());
							}else {
								this.setContent(begin_LocalTime.toString().substring(0, 5) + " - " + end_LocalTime.toString().substring(0, 5) +" : " + toDo.getName());
							}
						}else {
							this.setTitle(toDo.getName());
							this.setContent(toDo.getDescription());
						}


					}else {

						this.setTitle(toDo.getName());
						this.setContent(GroupwareToDoUtil.dateFormat(begin_LocalDate) + " - " + GroupwareToDoUtil.dateFormat(end_LocalDate) +" : " +  toDo.getName());

					}
				}

			}else if(GroupwareToDoUtil.BUTTON_SEVENDAYS_VIEW.equals(calendarMold)) {

				if(isDisplayUserName)
				{
					if(isSameDate)
					{
						if(isLongTime)
						{
							this.setTitle(toDo.getName());

							if(begin_LocalTime == LocalTime.MIN && end_LocalTime == LocalTime.MAX)
							{
								this.setContent(begin_LocalTime.toString().substring(0, 5) + " [" +  MUser.get(Env.getCtx(), toDo.getAD_User_ID()).getName() + "] " +  toDo.getName());
							}else {
								this.setContent(begin_LocalTime.toString().substring(0, 5) + " - " + end_LocalTime.toString().substring(0, 5) +" [" +  MUser.get(Env.getCtx(), toDo.getAD_User_ID()).getName()+"] " +  toDo.getName());
							}
						}else {

							this.setTitle("["+MUser.get(Env.getCtx(), toDo.getAD_User_ID()).getName()+"]");
							this.setContent(toDo.getName());
						}

					}else {

						this.setTitle(toDo.getName());
						this.setContent(GroupwareToDoUtil.dateFormat(begin_LocalDate) + " - " + GroupwareToDoUtil.dateFormat(end_LocalDate) +" [" +  MUser.get(Env.getCtx(), toDo.getAD_User_ID()).getName() + "] " +  toDo.getName());

					}


				}else {

					if(isSameDate)
					{
						if(isLongTime)
						{
							this.setTitle(toDo.getName());

							if(begin_LocalTime == LocalTime.MIN && end_LocalTime == LocalTime.MAX)
							{
								this.setContent(begin_LocalTime.toString().substring(0, 5) + " : " +  toDo.getName());
							}else {
								this.setContent(begin_LocalTime.toString().substring(0, 5) + " - " + end_LocalTime.toString().substring(0, 5) +" : " +  toDo.getName());
							}
						}else {
							this.setTitle(toDo.getName());
							this.setContent(toDo.getDescription());
						}


					}else {

						this.setTitle(toDo.getName());
						this.setContent(GroupwareToDoUtil.dateFormat(begin_LocalDate) + " - " + GroupwareToDoUtil.dateFormat(end_LocalDate) +" : " +  toDo.getName());

					}
				}


			}else if(GroupwareToDoUtil.BUTTON_MONTH_VIEW.equals(calendarMold)) {

				if(isDisplayUserName)
				{
					if(isSameDate)
					{
						if(isLongTime)
						{
							this.setTitle(toDo.getName());
							if(begin_LocalTime == LocalTime.MIN && end_LocalTime == LocalTime.MAX)
							{
								this.setContent(begin_LocalTime.toString().substring(0, 5) + " [" +  MUser.get(Env.getCtx(), toDo.getAD_User_ID()).getName() + "] " +  toDo.getName());
							}else {
								this.setContent(begin_LocalTime.toString().substring(0, 5) + " - " + end_LocalTime.toString().substring(0, 5) +" [" +  MUser.get(Env.getCtx(), toDo.getAD_User_ID()).getName() +"] " +  toDo.getName());
							}
						}else {

							this.setTitle(toDo.getName());
							this.setContent(begin_LocalTime.toString().substring(0, 5) + " [" + MUser.get(Env.getCtx(), toDo.getAD_User_ID()).getName() +"] " +  toDo.getName());
						}

					}else {

						this.setTitle(toDo.getName());
						this.setContent(GroupwareToDoUtil.dateFormat(begin_LocalDate) + " - " + GroupwareToDoUtil.dateFormat(end_LocalDate) +" [" +  MUser.get(Env.getCtx(), toDo.getAD_User_ID()).getName()+ "] " +  toDo.getName());

					}


				}else {

					if(isSameDate)
					{
						if(isLongTime)
						{
							this.setTitle(toDo.getName());

							if(begin_LocalTime == LocalTime.MIN && end_LocalTime == LocalTime.MAX)
							{
								this.setContent(begin_LocalTime.toString().substring(0, 5) + " : " +  toDo.getName());
							}else {
								this.setContent(begin_LocalTime.toString().substring(0, 5) + " - " + end_LocalTime.toString().substring(0, 5) +" -" +  toDo.getName());
							}

						}else {
							this.setTitle(toDo.getName());
							this.setContent(begin_LocalTime.toString().substring(0, 5) + " : " + toDo.getName());
						}


					}else {

						this.setTitle(toDo.getName());
						this.setContent(GroupwareToDoUtil.dateFormat(begin_LocalDate) + " - " + GroupwareToDoUtil.dateFormat(end_LocalDate) +" : "  +  toDo.getName());

					}
				}


			}else {

				;//impossible
			}

		}else if(m_ToDo.getJP_ToDo_Type().equals(MToDo.JP_TODO_TYPE_Task)) {

			/********************************************************************************************************
			 * Adjust Begin Time
			 ********************************************************************************************************/
			Timestamp begin_Timestamp = toDo.getJP_ToDo_ScheduledEndTime();
			LocalTime begin_LocalTime = begin_Timestamp.toLocalDateTime().toLocalTime();
			this.setBeginDate(new Date(begin_Timestamp.getTime()));



			/********************************************************************************************************
			 * Adjust  End Time
			 ********************************************************************************************************/
			Timestamp end_Timestamp = toDo.getJP_ToDo_ScheduledEndTime();
			LocalTime end_LocalTime = end_Timestamp.toLocalDateTime().toLocalTime();


			end_LocalTime = begin_Timestamp.toLocalDateTime().toLocalTime().plusHours(TASK_HOUR);
			if(begin_LocalTime.compareTo(end_LocalTime) < 0)
			{
				;//Noting to do
			}else {
				end_LocalTime = LocalTime.MAX;
			}

			end_Timestamp = Timestamp.valueOf(LocalDateTime.of(end_Timestamp.toLocalDateTime().toLocalDate(), end_LocalTime));
			this.setEndDate(new Date(end_Timestamp.getTime()));


			/********************************************************************************************************
			 * Adjust Display Info
			 ********************************************************************************************************/

			if(GroupwareToDoUtil.BUTTON_ONEDAY_VIEW.equals(calendarMold))
			{
				if(isDisplayUserName)
				{
					this.setTitle("["+MUser.get(Env.getCtx(), toDo.getAD_User_ID()).getName() +"] " + toDo.getName());
					this.setContent(toDo.getName());

				}else {

					this.setTitle(toDo.getName());
					this.setContent(toDo.getDescription());
				}

			}else if(GroupwareToDoUtil.BUTTON_SEVENDAYS_VIEW.equals(calendarMold)) {

				if(isDisplayUserName)
				{
					this.setTitle("["+MUser.get(Env.getCtx(), toDo.getAD_User_ID()).getName() +"] " + toDo.getName());
					this.setContent(toDo.getName());

				}else {

					this.setTitle(toDo.getName());
					this.setContent(toDo.getDescription());
				}


			}else if(GroupwareToDoUtil.BUTTON_MONTH_VIEW.equals(calendarMold)) {

				if(isDisplayUserName)
				{

					this.setTitle(toDo.getName());
					this.setContent(begin_LocalTime.toString().substring(0, 5) + " [" + MUser.get(Env.getCtx(), toDo.getAD_User_ID()).getName() +"] " + toDo.getName());


				}else {

					this.setTitle(toDo.getName());
					this.setContent(begin_LocalTime.toString().substring(0, 5) + " : " + toDo.getName());
				}


			}else {

				;//impossible
			}

		}else {
			;//impossible
		}



		/********************************************************************************************************
		 * Color
		 ********************************************************************************************************/

		if(!isDisplayUserName && m_ToDo.getJP_ToDo_Category_ID() > 0)
		{
			MToDoCategory category = MToDoCategory.get(toDo.getCtx(), m_ToDo.getJP_ToDo_Category_ID());
			this.setHeaderColor(category.getJP_ColorPicker());
			this.setContentColor(category.getJP_ColorPicker2());

		}else if(isDisplayUserName){//TODO : I would like to Set User Color

			MToDoCategory category = MToDoCategory.get(toDo.getCtx(), m_ToDo.getJP_ToDo_Category_ID());
			this.setHeaderColor(category.getJP_ColorPicker());
			this.setContentColor(category.getJP_ColorPicker2());


		}else {

			this.setHeaderColor(null);
			this.setContentColor(null);

		}

		this.setLocked(true);
	}

	public MToDo getToDoD() {
		return m_ToDo;
	}


}
