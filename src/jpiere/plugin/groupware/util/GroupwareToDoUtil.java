package jpiere.plugin.groupware.util;

import java.util.List;

import org.compiere.model.MForm;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;

public class GroupwareToDoUtil {


	/***********
	 * CONST
	 ***********/

	//JP_REPETITION_INTERVAL
	public final static String JP_REPETITION_INTERVAL_EVERYDAYS = "D";
	public final static String JP_REPETITION_INTERVAL_EVERYMONTHS = "M";
	public final static String JP_REPETITION_INTERVAL_EVERYWEEKS = "W";
	public final static String JP_REPETITION_INTERVAL_EVERYYEARS = "Y";


	//Button Name
	public final static String BUTTON_PREVIOUS = "PREVIOUS";
	public final static String BUTTON_NEXT = "NEXT";
	public final static String BUTTON_NEW = "NEW";
	public final static String BUTTON_REFRESH = "REFRESH";
	public final static String BUTTON_ONEDAY_VIEW = "ONE";
	public final static String BUTTON_SEVENDAYS_VIEW = "SEVEN";
	public final static String BUTTON_MONTH_VIEW = "MONTH";
	public final static String BUTTON_TODAY = "TODAY";


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
}
