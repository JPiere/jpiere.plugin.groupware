package jpiere.plugin.groupware.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.compiere.model.MUser;
import org.compiere.util.Util;

public class GroupwareTeamUtil {


	/** Const **/
	public final static String JP_REPETITION_INTERVAL_EVERYDAYS = "D";
	public final static String JP_REPETITION_INTERVAL_EVERYMONTHS = "M";
	public final static String JP_REPETITION_INTERVAL_EVERYWEEKS = "W";
	public final static String JP_REPETITION_INTERVAL_EVERYYEARS = "Y";

	static public HashMap<Integer, MUser>  subtractionTeamMember (MUser[] user1, MUser[] user2)
	{

		HashMap<Integer, MUser> userMap = new HashMap<Integer, MUser>();

		for(int i = 0; i < user1.length; i++)
		{
			boolean isSameUser = false;
			for(int j = 0; j < user2.length; j++)
			{
				if(user1[i].getAD_User_ID() == user2[j].getAD_User_ID())
				{
					isSameUser = true;
					break;
				}

			}

			if(!isSameUser)
			{
				userMap.put(user1[i].getAD_User_ID(),user1[i]);
			}

		}

		return userMap;
	}

	static public MUser[]  addTeamMember (MUser[] user1, MUser[] user2)
	{

		HashMap<Integer, MUser> userMap = new HashMap<Integer, MUser>();

		for(int i = 0; i < user1.length; i++)
		{
			userMap.put(user1[i].getAD_User_ID(),user1[i]);
		}

		for(int i = 0; i < user2.length; i++)
		{
			userMap.put(user2[i].getAD_User_ID(),user2[i]);
		}

		return userMap.values().toArray(new MUser[userMap.size()]);
	}

	static public Timestamp calculateNextScheduleTime(Timestamp timestamp, int offset, String JP_Repetition_Interval)
	{
		if(timestamp == null || offset == 0 || Util.isEmpty(JP_Repetition_Interval))
		{
			return null;
		}

		LocalDateTime localDateTime = timestamp.toLocalDateTime();

		if(JP_REPETITION_INTERVAL_EVERYDAYS.equals(JP_Repetition_Interval))
		{
			return 	Timestamp.valueOf(localDateTime.plusDays(offset));

		}else if(JP_REPETITION_INTERVAL_EVERYWEEKS.equals(JP_Repetition_Interval)){

			return 	Timestamp.valueOf(localDateTime = localDateTime.plusWeeks(offset));

		}else if(JP_REPETITION_INTERVAL_EVERYMONTHS.equals(JP_Repetition_Interval)){

			return 	Timestamp.valueOf(localDateTime = localDateTime.plusMonths(offset));

		}else if(JP_REPETITION_INTERVAL_EVERYYEARS.equals(JP_Repetition_Interval)){

			return 	Timestamp.valueOf(localDateTime = localDateTime.plusYears(offset));

		}else {
			return null;
		}

	}
}
