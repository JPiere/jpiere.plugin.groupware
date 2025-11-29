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
package jpiere.plugin.groupware.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MMessage;
import org.compiere.model.MRole;
import org.compiere.model.MUser;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0469: JPiere Groupware
 *
 * @author h.hagiwara
 *
 */
public class MTeam extends X_JP_Team {

	private static final long serialVersionUID = 6589983440692603772L;
	
	/**	Cache						*/
	private static CCache<Integer,MTeam> s_cache	= new CCache<Integer,MTeam>(Table_Name, 100, 10);	//	10 minutes

	public static MTeam get (Properties ctx, int JP_Team_ID)
	{
		if (JP_Team_ID <= 0)
		{
			return null;
		}
		Integer key = Integer.valueOf(JP_Team_ID);
		MTeam retValue = (MTeam) s_cache.get (key);
		if (retValue != null)
		{
			return retValue;
		}
		retValue = new MTeam (ctx, JP_Team_ID, null);
		if (retValue.get_ID () != 0)
		{
			s_cache.put (key, retValue);
		}
		return retValue;
	}	//	get

	public MTeam(Properties ctx, int JP_Team_ID, String trxName)
	{
		super(ctx, JP_Team_ID, trxName);
	}

	public MTeam(Properties ctx, String JP_Team_UU, String trxName)
	{
		super(ctx, JP_Team_UU, trxName);
	}

	
	public MTeam(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		String msg = beforeSavePreCheck(newRecord);
		if(!Util.isEmpty(msg))
		{
			log.saveError("Error", msg);
			return false;
		}

		return true;
	}

	public String beforeSavePreCheck(boolean newRecord)
	{
		setAD_Org_ID(0);

		if(getAD_User_ID() == 0)
		{
			int AD_Role_ID = Env.getAD_Role_ID(getCtx());
			MRole role = MRole.get(getCtx(), AD_Role_ID);
			if(!MRole.PREFERENCETYPE_Client.equals(role.getPreferenceType()))
			{
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "AD_User_ID")};
				return Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);//User field  is mandatory.
			}

		}

		int AD_User_ID = Env.getAD_User_ID(getCtx());
		if(getAD_User_ID() != 0 && getAD_User_ID() != AD_User_ID)
		{
			MMessage msg = MMessage.get(getCtx(), "AccessCannotUpdate");//You cannot update this record - You don't have the privileges
			String msgString = msg.get_Translation("MsgText") + " - "+ msg.get_Translation("MsgTip");
			return msgString + " : " + Msg.getMsg(getCtx(), "JP_DifferentUser");//Different User
		}

		return null;
	}


	@Override
	protected boolean beforeDelete()
	{
		String msg = beforeDeletePreCheck();
		if(!Util.isEmpty(msg))
		{
			log.saveError("Error", msg);
			return false;
		}

		return true;
	}

	public String beforeDeletePreCheck()
	{

		if(getAD_User_ID() == 0)
		{
			int AD_Role_ID = Env.getAD_Role_ID(getCtx());
			MRole role = MRole.get(getCtx(), AD_Role_ID);
			if(!MRole.PREFERENCETYPE_Client.equals(role.getPreferenceType()))
			{
				MMessage msg = MMessage.get(getCtx(), "AccessCannotUpdate");//You cannot update this record - You don't have the privileges
				return msg.get_Translation("MsgText") + " - "+ msg.get_Translation("MsgTip");
			}

		}

		int AD_User_ID = Env.getAD_User_ID(getCtx());
		if(getAD_User_ID() != 0 && getAD_User_ID() != AD_User_ID)
		{
			MMessage msg = MMessage.get(getCtx(), "AccessCannotUpdate");//You cannot update this record - You don't have the privileges
			String msgString = msg.get_Translation("MsgText") + " - "+ msg.get_Translation("MsgTip");
			return msgString + " : " + Msg.getMsg(getCtx(), "JP_DifferentUser");//Different User
		}

		return null;
	}

	protected MUser[] 	m_TeamMemberUser = null;

	public MUser[] getTeamMemberUser()
	{
		return getTeamMemberUser(false);
	}

	public MUser[] getTeamMemberUser(boolean requery)
	{

		if (m_TeamMemberUser != null && m_TeamMemberUser.length >= 0 && !requery)	//	re-load
			return m_TeamMemberUser;
		//
		ArrayList<MUser> list = new ArrayList<MUser>();
		String sql = "SELECT u.* FROM JP_Team_Member m INNER JOIN AD_User u ON (m.AD_User_ID=u.AD_User_ID) WHERE m.JP_Team_ID=? AND m.IsActive='Y' AND u.IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getJP_Team_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MUser (getCtx(), rs, get_TrxName()));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		m_TeamMemberUser = list.toArray(new MUser[list.size()]);

		return m_TeamMemberUser;

	}

	protected MTeamMember[] 	m_TeamMember= null;

	public MTeamMember[] getTeamMember()
	{
		return getTeamMember(false);
	}

	public MTeamMember[] getTeamMember(boolean requery)
	{

		if (m_TeamMember != null && m_TeamMember.length >= 0 && !requery)	//	re-load
			return m_TeamMember;
		//

		StringBuilder whereClauseFinal = new StringBuilder(MTeamMember.COLUMNNAME_JP_Team_ID+" =?");

		//
		List<MTeamMember> list = new Query(getCtx(), MTeamMember.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										//.setOrderBy(orderClause)
										.list();


		m_TeamMember = list.toArray(new MTeamMember[list.size()]);

		return m_TeamMember;

	}
}
