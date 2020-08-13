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
import java.time.LocalDateTime;
import java.util.Date;

import org.zkoss.calendar.impl.SimpleCalendarEvent;

import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoCategory;

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

	private MToDo m_ToDo = null ;

	public ToDoCalendarEvent(MToDo toDo)
	{
		super();
		this.m_ToDo = toDo;

		if(m_ToDo.getJP_ToDo_Type().equals(MToDo.JP_TODO_TYPE_Schedule))
		{
			this.setBeginDate(new Date(toDo.getJP_ToDo_ScheduledStartTime().getTime()));
			this.setEndDate(new Date(toDo.getJP_ToDo_ScheduledEndTime().getTime()));

		}else if(m_ToDo.getJP_ToDo_Type().equals(MToDo.JP_TODO_TYPE_Task)) {

			this.setBeginDate(new Date(toDo.getJP_ToDo_ScheduledEndTime().getTime()));

			LocalDateTime local = toDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime();
			this.setEndDate(new Date(Timestamp.valueOf(local.plusHours(1)).getTime()));

		}else {
			;//TODO エラー
		}

		this.setTitle(toDo.getName());
		this.setContent(toDo.getDescription());

		if(m_ToDo.getJP_ToDo_Category_ID() > 0)
		{
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
