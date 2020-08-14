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

/**
 * JPIERE-0470 Personal ToDo Popup Window
 *
 *
 * @author h.hagiwara
 *
 */
public interface I_CallerPersonalToDoPopupwindow {


	public List<MToDo> getListToDoes();

	public Timestamp getInitialScheduledStartTime();

	public Timestamp getInitialScheduledEndTime();

	public int getInitial_User_ID();

	public String getInitial_ToDo_Type();

	public boolean refresh(String JP_ToDO_Type);

}
