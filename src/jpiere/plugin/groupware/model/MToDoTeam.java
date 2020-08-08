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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MUser;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Msg;

/**
 * JPIERE-0469: JPiere Groupware
 *
 * @author h.hagiwara
 *
 */
public class MToDoTeam extends X_JP_ToDo_Team {

	public MToDoTeam(Properties ctx, int JP_ToDo_Team_ID, String trxName)
	{
		super(ctx, JP_ToDo_Team_ID, trxName);
	}


	public MToDoTeam(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}


	@Override
	protected boolean beforeSave(boolean newRecord)
	{

		setAD_Org_ID(0);

		if(getJP_ToDo_Category_ID() != 0 && (newRecord || is_ValueChanged(MToDoTeam.COLUMNNAME_JP_ToDo_Category_ID)))
		{
			if(MToDoCategory.get(getCtx(), getJP_ToDo_Category_ID()).getAD_User_ID() != 0)
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_Personal_ToDo_Category") );//Personal ToDo Category cannot be used.
				return false;
			}
		}

		if(getJP_ToDo_ScheduledStartTime() != null
				&& getJP_ToDo_ScheduledEndTime() != null)
		{
			if(getJP_ToDo_ScheduledStartTime().after(getJP_ToDo_ScheduledEndTime()))
			{
				log.saveError("Error", Msg.getElement(getCtx(), "JP_ToDo_ScheduledStartTime") + " > " +  Msg.getElement(getCtx(), "JP_ToDo_ScheduledEndTime") );
				return false;
			}

		}

		if(newRecord || is_ValueChanged(MToDoTeam.COLUMNNAME_JP_ToDo_Status))
		{

			if(MToDoTeam.JP_TODO_STATUS_NotYetStarted.equals(getJP_ToDo_Status()))
			{
				setJP_ToDo_StartTime(null);
				setJP_ToDo_EndTime(null);
				setProcessed(false);

			}else if(MToDoTeam.JP_TODO_STATUS_WorkInProgress.equals(getJP_ToDo_Status())) {

				if(getJP_ToDo_StartTime() == null)
					setJP_ToDo_StartTime(new Timestamp(System.currentTimeMillis()));
				setJP_ToDo_EndTime(null);
				setProcessed(false);

			}else if(MToDoTeam.JP_TODO_STATUS_Completed.equals(getJP_ToDo_Status())) {

				if(getJP_ToDo_StartTime() == null)
					setJP_ToDo_StartTime(new Timestamp(System.currentTimeMillis()));
				setJP_ToDo_EndTime(new Timestamp(System.currentTimeMillis()));
				setProcessed(true);

			}
		}

		return true;
	}


	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{

		String sql = "UPDATE JP_ToDo t"
				+ " SET JP_ToDo_Category_ID = ?"
				+ " , JP_ToDo_Type = ? "
				+ " , Name = ? "
				+ " , Description = ? "
				+ " , JP_ToDo_ScheduledStartTime = ? "
				+ " , JP_ToDo_ScheduledEndTime = ? "
				+ " , C_Project_ID = ? "
				+ " , C_ProjectPhase_ID = ? "
				+ " , C_ProjectTask_ID = ? "
				+ "WHERE JP_ToDo_Team_ID= ? ";

			Object[] para = {
					getJP_ToDo_Category_ID() == 0 ? null:getJP_ToDo_Category_ID()
					, getJP_ToDo_Type()
					, getName()
					, getDescription()
					, getJP_ToDo_ScheduledStartTime()
					,getJP_ToDo_ScheduledEndTime()
					,getC_Project_ID() == 0 ? null : getC_Project_ID()
					,getC_ProjectPhase_ID() == 0 ? null : getC_ProjectPhase_ID()
					,getC_ProjectTask_ID() == 0 ? null : getC_ProjectTask_ID()
					,getJP_ToDo_Team_ID()
					};

			DB.executeUpdate(sql, para, false, get_TrxName());

		return true;
	}

	protected MUser[] m_AdditionalTeamMemberUser = null;

	public MUser[] getAdditionalTeamMemberUser()
	{
		return getAdditionalTeamMemberUser(false);
	}

	public MUser[] getAdditionalTeamMemberUser(boolean requery)
	{
		if (m_AdditionalTeamMemberUser != null && m_AdditionalTeamMemberUser.length >= 0 && !requery)	//	re-load
			return m_AdditionalTeamMemberUser;
		//
		ArrayList<MUser> list = new ArrayList<MUser>();
		String sql = "SELECT u.* FROM JP_ToDo_Member_Additional m INNER JOIN AD_User u ON (m.AD_User_ID=u.AD_User_ID) WHERE m.JP_ToDo_Team_ID=? AND m.IsActive='Y' AND u.IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getJP_ToDo_Team_ID());
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

		m_AdditionalTeamMemberUser = list.toArray(new MUser[list.size()]);

		return m_AdditionalTeamMemberUser;

	}

	/**
	 * getAdditionalTeamMember
	 */
	protected MToDoMemberAdditional[] m_AdditionalTeamMember = null;

	public MToDoMemberAdditional[] getAdditionalTeamMember()
	{
		return getAdditionalTeamMember(false);
	}

	public MToDoMemberAdditional[] getAdditionalTeamMember(boolean requery)
	{

		if (m_AdditionalTeamMember != null && m_AdditionalTeamMember.length >= 0 && !requery)	//	re-load
			return m_AdditionalTeamMember;
		//

		StringBuilder whereClauseFinal = new StringBuilder(MToDoMemberAdditional.COLUMNNAME_JP_ToDo_Team_ID+" =?");

		//
		List<MToDoMemberAdditional> list = new Query(getCtx(), MToDoMemberAdditional.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										//.setOrderBy(orderClause)
										.list();


		m_AdditionalTeamMember = list.toArray(new MToDoMemberAdditional[list.size()]);

		return m_AdditionalTeamMember;

	}

	/**
	 * getTeamToDoReminder
	 */
	protected MToDoTeamReminder[] m_TeamToDoReminders = null;

	public MToDoTeamReminder[] getReminders()
	{
		return getReminders(false);
	}

	public MToDoTeamReminder[] getReminders(boolean requery)
	{

		if (m_TeamToDoReminders != null && m_TeamToDoReminders.length >= 0 && !requery)	//	re-load
			return m_TeamToDoReminders;
		//

		StringBuilder whereClauseFinal = new StringBuilder(MToDoTeamReminder.COLUMNNAME_JP_ToDo_Team_ID+" =? AND IsActive = 'Y'");

		//
		List<MToDoTeamReminder> list = new Query(getCtx(), MToDoTeamReminder.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										//.setOrderBy(orderClause)
										.list();

		m_TeamToDoReminders = list.toArray(new MToDoTeamReminder[list.size()]);

		return m_TeamToDoReminders;

	}

	/**
	 * getToDoes
	 */
	protected MToDo[] m_ToDoes = null;

	public MToDo[] getToDoes()
	{
		return getToDoes(false);
	}

	public MToDo[] getToDoes(boolean requery)
	{

		if (m_ToDoes != null && m_ToDoes.length >= 0 && !requery)	//	re-load
			return m_ToDoes;
		//

		StringBuilder whereClauseFinal = new StringBuilder(MToDo.COLUMNNAME_JP_ToDo_Team_ID+" =?");

		//
		List<MToDo> list = new Query(getCtx(), MToDo.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										//.setOrderBy(orderClause)
										.list();


		m_ToDoes = list.toArray(new MToDo[list.size()]);

		return m_ToDoes;

	}

}
