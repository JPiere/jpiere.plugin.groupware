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

import org.adempiere.webui.component.Label;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;


/**
*
* JPIERE-0473 Personal ToDo List Popup Window
*
*
* @author h.hagiwara
*
*/
public class TeamMemberListRowRenderer implements RowRenderer<TeamMemberModel> {

	@SuppressWarnings("unused")
	private TeamMemberPopup personalToDoListWindow = null;


	public TeamMemberListRowRenderer(TeamMemberPopup personalToDoListWindow)
	{
		this.personalToDoListWindow = personalToDoListWindow;
	}

	@Override
	public void render(Row row, TeamMemberModel data, int index) throws Exception
	{

		//User
		Cell cell = new Cell();
		cell.appendChild(new Label(data.user));
		row.appendChild(cell);

		//EMail
		cell = new Cell();
		cell.appendChild(new Label(data.EMail));
		row.appendChild(cell);

	}

}
