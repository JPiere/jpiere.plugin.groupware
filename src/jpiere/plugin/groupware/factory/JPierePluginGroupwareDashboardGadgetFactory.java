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
package jpiere.plugin.groupware.factory;


import org.adempiere.webui.factory.IDashboardGadgetFactory;
import org.zkoss.zk.ui.Component;

import jpiere.plugin.groupware.form.GroupwareMenuGadgetFlat;
import jpiere.plugin.groupware.form.ToDoGadget;


/**
 *  JPiere Plugins(JPPS) Groupware Dashboard Gadget Form Factory
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPierePluginGroupwareDashboardGadgetFactory implements IDashboardGadgetFactory {

	@Override
	public Component getGadget(String uri, Component parent)
	{

		if (uri != null && uri.startsWith("JP_Groupware"))
		{
			if(uri.equals("JP_Groupware=ToDoGadget"))
			{
				return new ToDoGadget();
			}else if(uri.equals("JP_Groupware=GroupwareMenuGadget")) {
				return new GroupwareMenuGadgetFlat();
			}

		}

		return null;
	}

}
