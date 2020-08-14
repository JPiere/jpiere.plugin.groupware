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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.zkoss.calendar.Calendars;
import org.zkoss.calendar.api.CalendarEvent;
import org.zkoss.calendar.event.CalendarsEvent;
import org.zkoss.calendar.impl.SimpleCalendarModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Center;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.North;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.West;

import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.util.GroupwareToDoUtil;
import jpiere.plugin.groupware.window.PersonalToDoPopupWindow;

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

	@Override
	public ADForm getForm()
	{
		return form;
	}

	private Calendars calendars = null;

	JPierePersonalToDoGadget todoG = null;

    public ToDoCalendar()
    {
    	form = new CustomForm();
    	Borderlayout mainBorderLayout = new Borderlayout();
    	form.appendChild(mainBorderLayout);

		ZKUpdateUtil.setWidth(mainBorderLayout, "99%");
		ZKUpdateUtil.setHeight(mainBorderLayout, "100%");

		calendars= new Calendars();

		//***************** NORTH **************************//

		North mainBorderLayout_North = new North();
//		mainBorderLayout_North.setStyle("border: none");
		mainBorderLayout_North.setSplittable(false);
		mainBorderLayout_North.setCollapsible(false);
		mainBorderLayout_North.setOpen(true);
		//mainBorderLayout_North.setTitle("トップコンテンツ");
		mainBorderLayout.appendChild(mainBorderLayout_North);
		mainBorderLayout_North.appendChild(createMainController(this));


		//***************** CENTER **************************//

		Center mainBorderLayout_Center = new Center();
		mainBorderLayout.appendChild(mainBorderLayout_Center);

		mainBorderLayout_Center.appendChild(calendars);

		//***************** WEST **************************//

		West mainBorderLayout_West = new West();
		mainBorderLayout_West.setSplittable(true);
		mainBorderLayout_West.setCollapsible(true);
		mainBorderLayout_West.setOpen(true);
		mainBorderLayout_West.setDroppable("true");
		ZKUpdateUtil.setWidth(mainBorderLayout_West, "25%");
		mainBorderLayout.appendChild(mainBorderLayout_West);

		Vlayout vlayout = new Vlayout();
		vlayout.setDroppable("true");
		mainBorderLayout_West.appendChild(vlayout);

		Groupbox groupBox1 = new Groupbox();
		groupBox1.setOpen(true);
		groupBox1.setDraggable("true");
		groupBox1.setMold("3d");
		groupBox1.setWidgetListener("onOpen", "this.caption.setIconSclass('z-icon-caret-' + (event.open ? 'down' : 'right'));");
		vlayout.appendChild(groupBox1);

		Caption caption = new Caption("予定");
		caption.setIconSclass("z-icon-caret-down");
		groupBox1.appendChild(caption);

		JPierePersonalToDoGadget todoS = new JPierePersonalToDoGadget("S");
		groupBox1.appendChild(todoS);


		Groupbox groupBox2 = new Groupbox();
		groupBox2.setOpen(true);
		groupBox2.setDraggable("true");
		groupBox2.setMold("3d");
		groupBox2.setWidgetListener("onOpen", "this.caption.setIconSclass('z-icon-caret-' + (event.open ? 'down' : 'right'));");
		vlayout.appendChild(groupBox2);

		Caption caption2 = new Caption("完了してないタスク");
		caption2.setIconSclass("z-icon-caret-down");
		groupBox2.appendChild(caption2);

		JPierePersonalToDoGadget todoT = new JPierePersonalToDoGadget("T");
		groupBox2.appendChild(todoT);
		todoG = todoT;

		Groupbox groupBox3 = new Groupbox();
		groupBox3.setOpen(false);
		groupBox3.setDraggable("true");
		groupBox3.setMold("3d");
		groupBox3.setWidgetListener("onOpen", "this.caption.setIconSclass('z-icon-caret-' + (event.open ? 'down' : 'right'));");
		vlayout.appendChild(groupBox3);

		Caption caption3 = new Caption("完了してないメモ");
		caption3.setIconSclass("z-icon-caret-right");
		groupBox3.appendChild(caption3);

		JPierePersonalToDoGadget todoM = new JPierePersonalToDoGadget("M");
		groupBox3.appendChild(todoM);


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

		calendars.addEventListener("onEventCreate", this);
		calendars.addEventListener("onEventEdit", this);

		calendars.invalidate();

    }

    public Div createMainController(EventListener<Event> eventListener)
    {
    	Div div = new Div();
		Hlayout hlayout = new Hlayout();
		div.appendChild(hlayout);

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		Button createNewToDo = new Button();
		createNewToDo.setImage(ThemeManager.getThemeResource("images/New16.png"));
		createNewToDo.setClass("btn-small");
		createNewToDo.setName(GroupwareToDoUtil.BUTTON_NEW);
		createNewToDo.addEventListener(Events.ON_CLICK, this);
		createNewToDo.setId(String.valueOf(0));
		hlayout.appendChild(createNewToDo);

		Button leftBtn = new Button();
		leftBtn.setImage(ThemeManager.getThemeResource("images/MoveLeft16.png"));
		leftBtn.setClass("btn-small");
		leftBtn.setName(GroupwareToDoUtil.BUTTON_PREVIOUS);
		leftBtn.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(leftBtn);

		Button rightBtn = new Button();
		rightBtn.setImage(ThemeManager.getThemeResource("images/MoveRight16.png"));
		rightBtn.setClass("btn-small");
		rightBtn.addEventListener(Events.ON_CLICK, this);
		rightBtn.setName(GroupwareToDoUtil.BUTTON_NEXT);
		hlayout.appendChild(rightBtn);

		Button refresh = new Button();
		refresh.setImage(ThemeManager.getThemeResource("images/Refresh16.png"));
		refresh.setClass("btn-small");
		refresh.setName(GroupwareToDoUtil.BUTTON_REFRESH);
		refresh.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(refresh);

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		Button today = new Button();
		today.setLabel("今日");
		today.setClass("btn-small");
		today.setName(GroupwareToDoUtil.BUTTON_TODAY);
		today.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(today);

		Button oneDayView = new Button();
		oneDayView.setLabel("日");
		oneDayView.setClass("btn-small");
		oneDayView.setName(GroupwareToDoUtil.BUTTON_ONEDAY_VIEW);
		oneDayView.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(oneDayView);

		Button sevenDayView = new Button();
		sevenDayView.setLabel("週");
		sevenDayView.setClass("btn-small");
		sevenDayView.setName(GroupwareToDoUtil.BUTTON_SEVENDAYS_VIEW);
		sevenDayView.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(sevenDayView);

		Button monthDayView = new Button();
		monthDayView.setLabel("月");
		monthDayView.setClass("btn-small");
		monthDayView.setName(GroupwareToDoUtil.BUTTON_MONTH_VIEW);
		monthDayView.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(monthDayView);

		lblDate = new Label("aaaaaa");
		//lblDate.addEventListener(Events.ON_CREATE, this);
		updateDateLabel();
		hlayout.appendChild(lblDate);

    	return div;

    }

    public Div createSubController(EventListener<Event> eventListener, String JP_ToDo_Type)
    {

    	return null;

    }


	@Override
	public void valueChange(ValueChangeEvent evt)
	{
		;
	}


	@Override
	public void onEvent(Event event) throws Exception
	{
		Component comp = event.getTarget();
		String eventName = event.getName();
		if(eventName.equals(Events.ON_CLICK))
		{
			if(comp instanceof Button)
			{
				Button btn = (Button) comp;
				String btnName = btn.getName();
				if(GroupwareToDoUtil.BUTTON_NEW.equals(btnName))
				{
					PersonalToDoPopupWindow todoWindow = new PersonalToDoPopupWindow(todoG, -1);
					SessionManager.getAppDesktop().showWindow(todoWindow);

				}else if(GroupwareToDoUtil.BUTTON_PREVIOUS.equals(btnName))
				{
					calendars.previousPage();
					updateDateLabel();

				}else if(GroupwareToDoUtil.BUTTON_NEXT.equals(btnName)){

					calendars.nextPage();
					updateDateLabel();

				}else if(GroupwareToDoUtil.BUTTON_REFRESH.equals(btnName)){

					;
				}else if(GroupwareToDoUtil.BUTTON_TODAY.equals(btnName)){

					calendars.setCurrentDate(Calendar.getInstance(calendars.getDefaultTimeZone()).getTime());
					updateDateLabel();

				}else if(GroupwareToDoUtil.BUTTON_ONEDAY_VIEW.equals(btnName)){

					divTabClicked(1);
					updateDateLabel();

				}else if(GroupwareToDoUtil.BUTTON_SEVENDAYS_VIEW.equals(btnName)){

					divTabClicked(7);
					updateDateLabel();

				}else if(GroupwareToDoUtil.BUTTON_MONTH_VIEW.equals(btnName)){

					divTabClicked(0);
					updateDateLabel();

				}

			}

		}else if (eventName.equals("onEventCreate")) {
			if (event instanceof CalendarsEvent) {
				CalendarsEvent calendarsEvent = (CalendarsEvent) event;
				PersonalToDoPopupWindow todoWindow = new PersonalToDoPopupWindow(todoG, -1);
				SessionManager.getAppDesktop().showWindow(todoWindow);
			}
		}
		else if (eventName.equals("onEventEdit")) {
			if (event instanceof CalendarsEvent)
			{
				CalendarsEvent calendarsEvent = (CalendarsEvent) event;
				CalendarEvent calendarEvent = calendarsEvent.getCalendarEvent();

				if (calendarEvent instanceof ToDoCalendarEvent)
				{
					ToDoCalendarEvent ce = (ToDoCalendarEvent) calendarEvent;

					PersonalToDoPopupWindow todoWindow = new PersonalToDoPopupWindow(todoG, -1);
					SessionManager.getAppDesktop().showWindow(todoWindow);
				}
			}
		}
	}

	private void divTabClicked(int days)
	{
		if (days > 0) {
			calendars.setMold("default");
			calendars.setDays(days);
		} else {
			calendars.setMold("month");
		}

	}

	private Label lblDate;
	private void updateDateLabel()
	{
		Date b = calendars.getBeginDate();
		Date e = calendars.getEndDate();

		//TODO 要日付調整 -> これを調整しないと終わりの日付の1日多い…
//		LocalDateTime local = new Timestamp(e.getTime()).toLocalDateTime();
//		e = new Date(Timestamp.valueOf(local.minusDays(1)).getTime());

		SimpleDateFormat sdfV = DisplayType.getDateFormat();
		sdfV.setTimeZone(calendars.getDefaultTimeZone());
		//sdfV.setTimeZone(calendars.getTimeZones().g);
		lblDate.setValue(sdfV.format(b) + " - " + sdfV.format(e));
	}
}
