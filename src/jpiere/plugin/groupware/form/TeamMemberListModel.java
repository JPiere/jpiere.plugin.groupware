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

import java.util.ArrayList;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataListener;


/**
*
* JPIERE-0473 Personal ToDo List Popup Window
*
*
* @author h.hagiwara
*
*/
public class TeamMemberListModel implements ListModel<Object>
{

	ArrayList<TeamMemberModel> list ;

	public TeamMemberListModel(ArrayList<TeamMemberModel> list )
	{
		this.list=list;
	}

	@Override
	public TeamMemberModel getElementAt(int index)
	{
		return list.get(index);
	}

	@Override
	public int getSize()
	{
		return list.size();
	}

	@Override
	public void addListDataListener(ListDataListener l)
	{
		;
	}

	@Override
	public void removeListDataListener(ListDataListener l)
	{
		;
	}

}
