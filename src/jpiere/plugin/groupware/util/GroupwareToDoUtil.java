package jpiere.plugin.groupware.util;

import java.util.List;

import org.compiere.model.MForm;
import org.compiere.model.Query;
import org.compiere.util.Env;

public class GroupwareToDoUtil {


	/** Const **/
	public final static String JP_REPETITION_INTERVAL_EVERYDAYS = "D";
	public final static String JP_REPETITION_INTERVAL_EVERYMONTHS = "M";
	public final static String JP_REPETITION_INTERVAL_EVERYWEEKS = "W";
	public final static String JP_REPETITION_INTERVAL_EVERYYEARS = "Y";


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
}
