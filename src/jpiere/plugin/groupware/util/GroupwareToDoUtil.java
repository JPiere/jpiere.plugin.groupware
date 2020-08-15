package jpiere.plugin.groupware.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.adempiere.webui.component.Label;
import org.adempiere.webui.editor.WEditor;
import org.compiere.model.MForm;
import org.compiere.model.Query;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;

import jpiere.plugin.groupware.form.ToDoCalendarEvent;
import jpiere.plugin.groupware.model.MToDo;

public class GroupwareToDoUtil {


	/***********
	 * CONST
	 ***********/

	//JP_REPETITION_INTERVAL
	public final static String JP_REPETITION_INTERVAL_EVERYDAYS = "D";
	public final static String JP_REPETITION_INTERVAL_EVERYMONTHS = "M";
	public final static String JP_REPETITION_INTERVAL_EVERYWEEKS = "W";
	public final static String JP_REPETITION_INTERVAL_EVERYYEARS = "Y";

	//Calendar Event
	public final static String CALENDAR_EVENT_CREATE = "onEventCreate";
	public final static String CALENDAR_EVENT_EDIT = "onEventEdit";
	public final static String CALENDAR_EVENT_UPDATE = "onEventUpdate";

	public final static String CALENDAR_EVENT_MOUSE_OVER = "onMouseOver";

	public final static String CALENDAR_EVENT_DAY = "onDayClick";
	public final static String CALENDAR_EVENT_WEEK = "onWeekClick";
	public final static String CALENDAR_EVENT_MONTH = "onMonthkClick";

	//Button Name
	public final static String BUTTON_PREVIOUS = "PREVIOUS";
	public final static String BUTTON_NEXT = "NEXT";
	public final static String BUTTON_NEW = "NEW";
	public final static String BUTTON_REFRESH = "REFRESH";
	public final static String BUTTON_ONEDAY_VIEW = "ONE";
	public final static String BUTTON_SEVENDAYS_VIEW = "SEVEN";
	public final static String BUTTON_MONTH_VIEW = "MONTH";
	public final static String BUTTON_TODAY = "TODAY";

	//other
	public final static long JUDGMENT_LONG_TIME_HOURES = 12;
	public final static long JUDGMENT_SHORT_TIME_MINUTE = 30;

	static public MForm getToDoCallendarForm()
	{
		StringBuilder whereClause = new StringBuilder("classname=?");

		List<MForm> list = new Query(Env.getCtx(), MForm.Table_Name, whereClause.toString(), null)
							.setParameters("jpiere.plugin.groupware.form.ToDoCalendar")
							.list();


		if(list.size() > 0)
			return list.get(0);

		return null;
	}

	static public Div getDividingLine()
	{
		Div div = new Div();
		div.appendChild(new Html("&nbsp;"));
		div.setStyle("display: inline-block; border-left: 1px dotted #888888;margin: 5px 2px 0px 2px;");
		return div;
	}

	static public Div createLabelDiv(String string, boolean isMandatory, boolean isPositionAdjust )
	{
		Label label = new Label(string);
		return createLabelDiv(label , isMandatory, isPositionAdjust);
	}

	static public Div createLabelDiv(Label label , boolean isMandatory, boolean isPositionAdjust )
	{
		label.rightAlign();
		label.setMandatory(isMandatory);
		Div div = new Div();
		div.setSclass("form-label");
		if(isPositionAdjust)
			div.setStyle("padding-top:4px");
		div.appendChild(label);
		if(isMandatory)
			div.appendChild(label.getDecorator());

		return div;
	}

	static public Div createEditorDiv(WEditor editor, boolean isPositionAdjust )
	{
		Div div = new Div();
		if(isPositionAdjust)
			div.setStyle("padding-top:4px");
		div.appendChild(editor.getComponent());

		return div;
	}

	static public List<ToDoCalendarEvent> getToDoCalendarEvents(String calendarMold, boolean isDisplayName, String whereClause, String orderClause, Object ...parameters)
	{

		List<MToDo> list_ToDoes = new Query(Env.getCtx(), MToDo.Table_Name, whereClause.toString(), null)
										.setParameters(parameters)
										.setOrderBy(orderClause)
										.list();

		ArrayList<ToDoCalendarEvent> list_Events = new ArrayList<ToDoCalendarEvent>();
		for(MToDo toDo : list_ToDoes)
		{
			list_Events.add(new ToDoCalendarEvent(toDo,calendarMold, isDisplayName));
		}

		return list_Events;
	}

	static public boolean judgmentOfLongTime(Timestamp bigin, Timestamp end)
	{
		//Adjust Begin Time
		LocalDate begin_LocalDate = bigin.toLocalDateTime().toLocalDate();
		LocalTime begin_LocalTime = bigin.toLocalDateTime().toLocalTime();

		//Adjust End Time
		LocalDate end_LocalDate = end.toLocalDateTime().toLocalDate();
		LocalTime end_LocalTime = end.toLocalDateTime().toLocalTime();

		if((begin_LocalDate.compareTo(end_LocalDate) == 0))
		{
			int scheduleTime = end_LocalTime.minusHours(begin_LocalTime.getHour()).getHour();
			if(scheduleTime >= JUDGMENT_LONG_TIME_HOURES)
			{
				return true;
			}else {
				return false;
			}

		}else {

			return true;
		}

	}

	static public boolean judgmentOfShortTime(Timestamp bigin, Timestamp end)
	{
		//Adjust Begin Time
		LocalDate begin_LocalDate = bigin.toLocalDateTime().toLocalDate();
		LocalTime begin_LocalTime = bigin.toLocalDateTime().toLocalTime();

		//Adjust End Time
		LocalDate end_LocalDate = end.toLocalDateTime().toLocalDate();
		LocalTime end_LocalTime = end.toLocalDateTime().toLocalTime();

		if((begin_LocalDate.compareTo(end_LocalDate) == 0))
		{
			int scheduleHour = end_LocalTime.minusHours(begin_LocalTime.getHour()).getHour();
			if(scheduleHour < 1)
			{
				int sheduleMinute = end_LocalTime.minusMinutes(begin_LocalTime.getMinute()).getMinute();
				if(sheduleMinute < JUDGMENT_SHORT_TIME_MINUTE)
				{
					return true;
				}else {
					return false;
				}
			}else {
				return false;
			}

		}else {

			return false;
		}

	}

	private static SimpleDateFormat dateFormater = DisplayType.getDateFormat();

	public static String dateFormat(LocalDate date)
	{
		return dateFormater.format(Date.valueOf(date));
	}
}
