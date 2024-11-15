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
import java.util.Properties;

/**
 * JPIERE-0480: JPiere Groupware - ToDo Reminder
 *
 * @author h.hagiwara
 *
 */
public class MToDoReminderLog extends X_JP_ToDo_Reminder_Log {

	private static final long serialVersionUID = -4498527822321393510L;

	public MToDoReminderLog(Properties ctx, int JP_ToDo_Reminder_Log_ID, String trxName)
	{
		super(ctx, JP_ToDo_Reminder_Log_ID, trxName);
	}

	public MToDoReminderLog(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

}
