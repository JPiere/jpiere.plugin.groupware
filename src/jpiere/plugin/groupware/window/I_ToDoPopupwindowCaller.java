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

import java.sql.Timestamp;
import java.util.List;

import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoTeam;

/**
 * JPIERE-0470 Personal ToDo Popup Window
 *
 * I_ToDoPopupwindowCaller send ToDo to PopupWindow and set Default Value.
 *
 *
 * @author h.hagiwara
 *
 */
public interface I_ToDoPopupwindowCaller {


	public List<MToDo> getPersonalToDoList();

	public List<MToDoTeam> getTeamToDoList();



	public Timestamp getDefault_JP_ToDo_ScheduledStartTime();

	public Timestamp getDefault_JP_ToDo_ScheduledEndTime();

	public int getDefault_AD__User_ID();

	public int getDefault_JP_ToDo_Category_ID();

	public String getDefault_JP_ToDo_Type();

	public int getWindowNo();

}
