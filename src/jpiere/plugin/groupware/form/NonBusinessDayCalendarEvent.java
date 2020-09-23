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

import org.compiere.model.I_C_NonBusinessDay;
import org.compiere.model.MCountry;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.calendar.impl.SimpleCalendarEvent;

import jpiere.plugin.groupware.model.MGroupwareUser;

/**
*
* JPIERE-0471: ToDo Calendar
*
* h.hagiwara
*
*/
public class NonBusinessDayCalendarEvent extends SimpleCalendarEvent {


	public NonBusinessDayCalendarEvent(I_C_NonBusinessDay nonBusinessDay, MGroupwareUser gUser)
	{

		if(Util.isEmpty(nonBusinessDay.getName()))
		{
			String name = Msg.getElement(Env.getCtx(), "C_NonBusinessDay_ID");
			if(nonBusinessDay.getC_Country_ID() == 0)
			{
				setTitle(name);
				setContent(name);
			}else {
				MCountry country = MCountry.get(Env.getCtx(), nonBusinessDay.getC_Country_ID());
				String countryName = country.getTrlName();
				setTitle(name+" ["+countryName+"]");
				setContent(name+" ["+countryName+"]");

			}

		}else {
			if(nonBusinessDay.getC_Country_ID() == 0)
			{
				setTitle(nonBusinessDay.getName());
				setContent(nonBusinessDay.getName());
			}else {

				MCountry country = MCountry.get(Env.getCtx(), nonBusinessDay.getC_Country_ID());
				String countryName = country.getTrlName();
				setTitle(nonBusinessDay.getName()+" ["+countryName+"]");
				setContent(nonBusinessDay.getName()+" ["+countryName+"]");
			}
		}

		LocalDate localDate = nonBusinessDay.getDate1().toLocalDateTime().toLocalDate();
		Timestamp ts_From = Timestamp.valueOf(LocalDateTime.of(localDate, LocalTime.MIN));
		Timestamp ts_To= Timestamp.valueOf(LocalDateTime.of(localDate, LocalTime.MAX));

		this.setBeginDate(new Date(ts_From.getTime()));
		this.setEndDate(new Date(ts_To.getTime()));

		if(Util.isEmpty(gUser.getJP_NonBusinessDayColor()))
		{
			this.setHeaderColor("#ff0000");
			this.setContentColor("#ff0000");

		}else {

			this.setHeaderColor(gUser.getJP_NonBusinessDayColor());
			this.setContentColor(gUser.getJP_NonBusinessDayColor());

		}

		this.setLocked(true);

	}

}
