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
package jpiere.plugin.groupware.window;

import java.math.BigDecimal;
import java.sql.Timestamp;


/**
*
* JPIERE-0473 Personal ToDo List Popup Window
*
*
* @author h.hagiwara
*
*/
public class PersonalToDoReminderModel {

	public int JP_ToDo_Reminder_ID = 0;
	public String user = null;
	public String comments = null;
	public String status = null;
	public boolean IsSentReminderJP = false;
	public boolean IsConfirmed = false;
	public Timestamp JP_Confirmed = null;
	public boolean Processed = false;

	public String JP_Statistics_YesNo = null;
	public String JP_Statistics_Choice=null;
	public Timestamp JP_Statistics_DateAndTime = null;
	public BigDecimal JP_Statistics_Number = null;

	public PersonalToDoReminderModel()
	{
		;
	}

}
