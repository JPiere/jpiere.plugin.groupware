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
import java.util.List;

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
import org.zkoss.calendar.Calendars;
import org.zkoss.calendar.impl.SimpleCalendarModel;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Center;
import org.zkoss.zul.Div;
import org.zkoss.zul.North;
import org.zkoss.zul.West;

import jpiere.plugin.groupware.model.MToDo;

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
//		mainBorderLayout_North.setStyle("border: none");
		mainBorderLayout_North.setSplittable(false);
		mainBorderLayout_North.setCollapsible(false);
		mainBorderLayout_North.setOpen(true);
		//mainBorderLayout_North.setTitle("トップコンテンツ");
		mainBorderLayout.appendChild(mainBorderLayout_North);

		mainBorderLayout_North.appendChild(new Label(Msg.getElement(Env.getCtx(), "C_Calendar_ID")));


		Center mainBorderLayout_Center = new Center();
		mainBorderLayout.appendChild(mainBorderLayout_Center);

		Calendars calendars= new Calendars();
		mainBorderLayout_Center.appendChild(calendars);

		//***************** WEST **************************//

		West mainBorderLayout_West = new West();
		mainBorderLayout_West.setSplittable(true);
		mainBorderLayout_West.setCollapsible(true);
		mainBorderLayout_West.setOpen(true);
		ZKUpdateUtil.setWidth(mainBorderLayout_West, "25%");
		mainBorderLayout.appendChild(mainBorderLayout_West);

		Div div_West = new Div();
		mainBorderLayout_West.appendChild(div_West);

		JPierePersonalToDoGadget todoS = new JPierePersonalToDoGadget("S");
		div_West.appendChild(todoS);

		JPierePersonalToDoGadget todoT = new JPierePersonalToDoGadget("T");
		div_West.appendChild(todoT);

		JPierePersonalToDoGadget todoM = new JPierePersonalToDoGadget("M");
		div_West.appendChild(todoM);



		ArrayList<ToDoCalendarEvent> events = new ArrayList<ToDoCalendarEvent>();
		List<MToDo> list_ToDoes =  todoS.getListToDoes();
		for(MToDo toDo : list_ToDoes)
		{
			events.add(new ToDoCalendarEvent(toDo));
		}

		SimpleCalendarModel scm = new SimpleCalendarModel();
		calendars.setModel(scm);

		scm.clear();
		for (ToDoCalendarEvent event : events)
			scm.add(event);

		calendars.invalidate();

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
