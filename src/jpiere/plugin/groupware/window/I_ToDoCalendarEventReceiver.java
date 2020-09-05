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

import jpiere.plugin.groupware.model.I_ToDo;

/**
 * JPIERE-0470 Personal ToDo Popup Window
 *
 * I_ToDoCalendarEventReceiver
 *
 * ToDoCalendarEventReceiver can receive ToDo Calender Event from ToDo Popup Window
 *
 * @author h.hagiwara
 *
 */
public interface I_ToDoCalendarEventReceiver {

	public boolean update(I_ToDo todo);

	public boolean create(I_ToDo todo);

	public boolean delete(I_ToDo todo);

	public boolean refresh(I_ToDo todo);

}
