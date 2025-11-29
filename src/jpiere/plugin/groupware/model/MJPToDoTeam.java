package jpiere.plugin.groupware.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MJPToDoTeam extends X_RV_JP_ToDo_Team {

	private static final long serialVersionUID = -534634760355304016L;

	public MJPToDoTeam(Properties ctx, int RV_JP_ToDo_Team_ID, String trxName) 
	{
		super(ctx, RV_JP_ToDo_Team_ID, trxName);
	}

	public MJPToDoTeam(Properties ctx, String RV_JP_ToDo_Team_UU, String trxName) 
	{
		super(ctx, RV_JP_ToDo_Team_UU, trxName);
	}
	
	public MJPToDoTeam(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}

}
