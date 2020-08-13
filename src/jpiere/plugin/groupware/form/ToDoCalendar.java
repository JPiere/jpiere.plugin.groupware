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

import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Calendar;
import org.zkoss.zul.Center;
import org.zkoss.zul.North;

/**
 *
 * JPIERE-0471: ToDo Calendar
 *
 * h.hagiwara
 *
 */
public class ToDoCalendar implements IFormController, EventListener<Event>, ValueChangeListener {

	private static CLogger log = CLogger.getCLogger(ToDoCalendar.class);

	private CustomForm form;



    public ToDoCalendar()
    {
    	form = new CustomForm();
    	Borderlayout mainBorderLayout = new Borderlayout();
    	form.appendChild(mainBorderLayout);

		ZKUpdateUtil.setWidth(mainBorderLayout, "99%");
		ZKUpdateUtil.setHeight(mainBorderLayout, "100%");

		North mainBorderLayout_North = new North();
		mainBorderLayout_North.setStyle("border: none");
		mainBorderLayout.appendChild(mainBorderLayout_North);

		mainBorderLayout_North.appendChild(new Label(Msg.getElement(Env.getCtx(), "C_Calendar_ID")));


		Center mainBorderLayout_Center = new Center();
		mainBorderLayout.appendChild(mainBorderLayout_Center);

		Calendar calendar = new Calendar();
		mainBorderLayout_Center.appendChild(calendar);

    }


	@Override
	public ADForm getForm()
	{
		return form;
	}


	@Override
	public void valueChange(ValueChangeEvent evt)
	{
		;
	}


	@Override
	public void onEvent(Event event) throws Exception
	{
		;
	}
}
