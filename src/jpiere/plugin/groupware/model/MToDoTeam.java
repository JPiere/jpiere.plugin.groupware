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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MMessage;
import org.compiere.model.MUser;
import org.compiere.model.Query;
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
public class MToDoTeam extends X_JP_ToDo_Team implements I_ToDo{

	private static final long serialVersionUID = 3049052489341303453L;

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
		String msg = beforeSavePreCheck(newRecord);
		if(!Util.isEmpty(msg))
		{
			log.saveError("Error", msg);
			return false;
		}

		//*** ToDo Status***//
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

	public String beforeSavePreCheck(boolean newRecord)
	{
		//** Check User**/
		if(!newRecord)
		{
			int loginUser  = Env.getAD_User_ID(getCtx());
			if(loginUser == getAD_User_ID() || loginUser == getCreatedBy())
			{
				;//Updatable

			}else{
				MMessage msg = MMessage.get(getCtx(), "AccessCannotUpdate");//You cannot update this record - You don't have the privileges
				return msg.get_Translation("MsgText") + " - "+ msg.get_Translation("MsgTip");
			}
		}

		//*** Check ToDo Type ***//
		if(Util.isEmpty(getJP_ToDo_Type()))
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_ToDo_Type)};
			return Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
		}

		//*** Check ToDo Category ***//
		if(getJP_ToDo_Category_ID() != 0 && (newRecord || is_ValueChanged(MToDoTeam.COLUMNNAME_JP_ToDo_Category_ID)))
		{
			if(MToDoCategory.get(getCtx(), getJP_ToDo_Category_ID()).getAD_User_ID() != 0)
			{
				//Personal ToDo Category cannot be used.
				return Msg.getMsg(getCtx(), "JP_Personal_ToDo_Category") ;
			}
		}

		//*** Check Schedule Time ***//
		if(newRecord || is_ValueChanged(MToDoTeam.COLUMNNAME_JP_ToDo_Type) || is_ValueChanged(MToDoTeam.COLUMNNAME_IsStartDateAllDayJP) || is_ValueChanged(MToDoTeam.COLUMNNAME_IsEndDateAllDayJP)
				|| is_ValueChanged(MToDoTeam.COLUMNNAME_JP_ToDo_ScheduledStartDate) || is_ValueChanged(MToDoTeam.COLUMNNAME_JP_ToDo_ScheduledEndDate)
				|| is_ValueChanged(MToDoTeam.COLUMNNAME_JP_ToDo_ScheduledStartTime) || is_ValueChanged(MToDoTeam.COLUMNNAME_JP_ToDo_ScheduledEndTime))
		{

			if(MToDoTeam.JP_TODO_TYPE_Schedule.equals(getJP_ToDo_Type()))
			{
				if(getJP_ToDo_ScheduledStartDate() == null)
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), MToDoTeam.COLUMNNAME_JP_ToDo_ScheduledStartDate)};
					return Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
				}

				LocalDate localStartDate = getJP_ToDo_ScheduledStartDate().toLocalDateTime().toLocalDate();
				LocalTime localStartTime = null;
				if(isStartDateAllDayJP())
				{
					localStartTime = LocalTime.MIN;

				}else {

					if(getJP_ToDo_ScheduledStartTime() == null)
					{
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), MToDoTeam.COLUMNNAME_JP_ToDo_ScheduledStartTime)};
						return Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
					}

					localStartTime = getJP_ToDo_ScheduledStartTime().toLocalDateTime().toLocalTime();
				}

				setJP_ToDo_ScheduledStartTime(Timestamp.valueOf(LocalDateTime.of(localStartDate,localStartTime)));
				setJP_ToDo_ScheduledStartDate(getJP_ToDo_ScheduledStartTime());
			}



			if( MToDoTeam.JP_TODO_TYPE_Task.equals(getJP_ToDo_Type()) || MToDoTeam.JP_TODO_TYPE_Schedule.equals(getJP_ToDo_Type()) )
			{
				if(getJP_ToDo_ScheduledEndDate() == null)
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), MToDoTeam.COLUMNNAME_JP_ToDo_ScheduledEndDate)};
					return Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
				}

				LocalDate localEndDate = getJP_ToDo_ScheduledEndDate().toLocalDateTime().toLocalDate();
				LocalTime localEndTime = null;
				if(isEndDateAllDayJP())
				{
					localEndTime = LocalTime.MIN;

				}else {

					if(getJP_ToDo_ScheduledEndTime() == null)
					{
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), MToDoTeam.COLUMNNAME_JP_ToDo_ScheduledEndTime)};
						return Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
					}

					localEndTime = getJP_ToDo_ScheduledEndTime().toLocalDateTime().toLocalTime();

				}

				setJP_ToDo_ScheduledEndTime(Timestamp.valueOf(LocalDateTime.of(localEndDate,localEndTime)));
				setJP_ToDo_ScheduledEndDate(getJP_ToDo_ScheduledEndTime());

				if(MToDoTeam.JP_TODO_TYPE_Schedule.equals(getJP_ToDo_Type()))
				{
					if(getJP_ToDo_ScheduledStartTime().after(getJP_ToDo_ScheduledEndTime()))
					{
						return Msg.getElement(getCtx(), "JP_ToDo_ScheduledStartTime") + " > " +  Msg.getElement(getCtx(), "JP_ToDo_ScheduledEndTime") ;
					}

				}else if(MToDoTeam.JP_TODO_TYPE_Task.equals(getJP_ToDo_Type())) {

					setJP_ToDo_ScheduledStartDate(getJP_ToDo_ScheduledEndDate());
					setJP_ToDo_ScheduledStartTime(getJP_ToDo_ScheduledEndTime());
				}

			}else if(MToDoTeam.JP_TODO_TYPE_Memo.equals(getJP_ToDo_Type())){

				setJP_ToDo_ScheduledStartDate(null);
				setJP_ToDo_ScheduledStartTime(null);
				setJP_ToDo_ScheduledEndDate(null);
				setJP_ToDo_ScheduledEndTime(null);
			}
		}

		return null;
	}


	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{

		if(!newRecord && success)
		{
			String sql = "UPDATE JP_ToDo t"
					+ " SET JP_ToDo_Category_ID = ?"
					+ " , JP_ToDo_Type = ? "
					+ " , Name = ? "
					+ " , Description = ? "
					+ " , URL = ? "
					+ " , JP_ToDo_ScheduledStartDate = ? "
					+ " , IsStartDateAllDayJP = ? "
					+ " , JP_ToDo_ScheduledStartTime = ? "
					+ " , JP_ToDo_ScheduledEndDate = ? "
					+ " , IsEndDateAllDayJP = ? "
					+ " , JP_ToDo_ScheduledEndTime = ? "
					+ " , C_Project_ID = ? "
					+ " , C_ProjectPhase_ID = ? "
					+ " , C_ProjectTask_ID = ? "
					+ " , AD_Org_ID = ? "
					+ " , IsOpenToDoJP = ? "
					+ "WHERE JP_ToDo_Team_ID= ? ";

				Object[] para = {
						getJP_ToDo_Category_ID() == 0 ? null:getJP_ToDo_Category_ID()
						, getJP_ToDo_Type()
						, getName()
						, getDescription()
						, getURL()
						, getJP_ToDo_ScheduledStartDate()
						, isStartDateAllDayJP()? "Y":"N"
						, getJP_ToDo_ScheduledStartTime()
						, getJP_ToDo_ScheduledEndDate()
						, isEndDateAllDayJP()? "Y":"N"
						, getJP_ToDo_ScheduledEndTime()
						, getC_Project_ID() == 0 ? null : getC_Project_ID()
						, getC_ProjectPhase_ID() == 0 ? null : getC_ProjectPhase_ID()
						, getC_ProjectTask_ID() == 0 ? null : getC_ProjectTask_ID()
						, getAD_Org_ID()
						, isOpenToDoJP()? "Y":"N"
						,getJP_ToDo_Team_ID()
						};

				DB.executeUpdate(sql, para, false, get_TrxName());
		}

		if(success && !newRecord && is_ValueChanged(MToDoTeam.COLUMNNAME_JP_ToDo_Status))
		{
			if(MToDoTeam.JP_TODO_STATUS_Completed.equals(getJP_ToDo_Status()))
			{
				MToDoTeamReminder[] reminders = getReminders();
				for(int i = 0;  i < reminders.length; i++)
				{
					reminders[i].setProcessed(true);
					reminders[i].saveEx(get_TrxName());
				}

				MToDo[] todoes = getToDoes(true);
				for(int i = 0; i < todoes.length; i++)
				{
					todoes[i].setProcessed(true);
					todoes[i].saveEx(get_TrxName());
				}

			}else {

				if(MToDoTeam.JP_TODO_STATUS_Completed.equals(get_ValueOld(MToDoTeam.COLUMNNAME_JP_ToDo_Status)))
				{
					MToDoTeamReminder[] reminders = getReminders();
					for(int i = 0;  i < reminders.length; i++)
					{
						if(!reminders[i].isSentReminderJP())
						{
							reminders[i].setProcessed(false);
							reminders[i].saveEx(get_TrxName());
						}
					}

					MToDo[] todoes = getToDoes(true);
					for(int i = 0; i < todoes.length; i++)
					{
						if(!MToDo.JP_TODO_STATUS_Completed.equals(todoes[i].getJP_ToDo_Status()))
						{
							todoes[i].setProcessed(false);
							todoes[i].saveEx(get_TrxName());
						}
					}
				}
			}
		}

		return true;
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
		//** Check User**/
		int loginUser  = Env.getAD_User_ID(getCtx());
		if(loginUser == getAD_User_ID() || loginUser == getCreatedBy())
		{
			//Deleteable;

		}else{

			MMessage msg = MMessage.get(getCtx(), "AccessCannotUpdate");
			return msg.get_Translation("MsgText") + " - "+ msg.get_Translation("MsgTip");
		}


		return null;
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


	public static ArrayList<MToDoTeam>  getRelatedTeamToDos(Properties ctx, MToDoTeam m_TeamToDo, ArrayList<MToDoTeam> list_ToDoTeam, Timestamp time, boolean isIncludingIndirectRelationships, String trxName)
	{
		if(list_ToDoTeam == null)
		{
			list_ToDoTeam = new ArrayList<MToDoTeam>();
		}

		StringBuilder whereClauseFinal = null;
		String orderClause = MToDoTeam.COLUMNNAME_JP_ToDo_ScheduledStartTime;
		List<MToDoTeam> list = null;

		if(m_TeamToDo.getJP_Processing3().equals("N"))
		{
			whereClauseFinal = new StringBuilder(" JP_ToDo_Team_Related_ID =? AND JP_ToDo_Team_ID <> ?");
			if(time == null)
			{
				list = new Query(ctx, MToDoTeam.Table_Name, whereClauseFinal.toString(), trxName)
						.setParameters(m_TeamToDo.getJP_ToDo_Team_Related_ID(), m_TeamToDo.getJP_ToDo_Team_ID())
						.setOrderBy(orderClause)
						.list();
			}else {

				whereClauseFinal = whereClauseFinal.append(" AND JP_ToDo_ScheduledStartTime >= ?");
				list = new Query(ctx, MToDoTeam.Table_Name, whereClauseFinal.toString(), trxName)
						.setParameters(m_TeamToDo.getJP_ToDo_Team_Related_ID(), m_TeamToDo.getJP_ToDo_Team_ID(), time)
						.setOrderBy(orderClause)
						.list();
			}

		}else {

			whereClauseFinal = new StringBuilder(" (JP_ToDo_Team_Related_ID =? OR JP_ToDo_Team_Related_ID =?) AND JP_ToDo_Team_ID <> ?");
			if(time == null)
			{
				list = new Query(ctx, MToDoTeam.Table_Name, whereClauseFinal.toString(), trxName)
						.setParameters(m_TeamToDo.getJP_ToDo_Team_Related_ID(), m_TeamToDo.getJP_ToDo_Team_ID(), m_TeamToDo.getJP_ToDo_Team_ID())
						.setOrderBy(orderClause)
						.list();
			}else {

				whereClauseFinal = whereClauseFinal.append(" AND JP_ToDo_ScheduledStartTime >= ?");
				list = new Query(ctx, MToDoTeam.Table_Name, whereClauseFinal.toString(), trxName)
						.setParameters(m_TeamToDo.getJP_ToDo_Team_Related_ID(), m_TeamToDo.getJP_ToDo_Team_ID(), m_TeamToDo.getJP_ToDo_Team_ID(), time)
						.setOrderBy(orderClause)
						.list();
			}
		}

		boolean isContained = false;
		for(MToDoTeam teamToDo : list)
		{
			isContained = false;
			for(MToDoTeam td : list_ToDoTeam)
			{
				if(teamToDo.getJP_ToDo_Team_ID() == td.getJP_ToDo_Team_ID())
				{
					isContained = true;
				}
			}

			if(isContained)
				continue;

			list_ToDoTeam.add(teamToDo);
			if(isIncludingIndirectRelationships)
			{
				if(teamToDo.getJP_Processing3().equals("Y"))
				{
					list_ToDoTeam = MToDoTeam.getRelatedTeamToDos(ctx, teamToDo, list_ToDoTeam, time, true, trxName);
				}

			}

		}

		return list_ToDoTeam;
	}


	@Override
	public int getParent_Team_ToDo_ID()
	{
		return 0;
	}


	@Override
	public void setComments(String Comments)
	{
		return;
	}


	@Override
	public void setJP_Statistics_YesNo(String JP_Statistics_YesNo)
	{
		return;
	}


	@Override
	public void setJP_Statistics_Choice(String JP_Statistics_Choice)
	{
		return;
	}


	@Override
	public void setJP_Statistics_DateAndTime(Timestamp JP_Statistics_DateAndTime)
	{
		return;
	}


	@Override
	public void setJP_Statistics_Number(BigDecimal JP_Statistics_Number)
	{
		return;
	}


	@Override
	public String getJP_Statistics_YesNo()
	{
		return null;
	}


	@Override
	public String getJP_Statistics_Choice()
	{
		return null;
	}


	@Override
	public Timestamp getJP_Statistics_DateAndTime()
	{
		return null;
	}


	@Override
	public BigDecimal getJP_Statistics_Number()
	{
		return null;
	}


	@Override
	public String getComments()
	{
		return null;
	}


	@Override
	public void setUpdated(Timestamp updated)
	{
		set_ValueNoCheck("Updated", updated);
	}


	@Override
	public boolean isCreatedToDoRepeatedly()
	{
		return getJP_Processing3().equals("Y");
	}


	@Override
	public void setisCreatedToDoRepeatedly(boolean Processed)
	{
		setJP_Processing3(Processed == true? "Y" : "N");
	}


	@Override
	public int getRelated_ToDo_ID()
	{
		return getJP_ToDo_Team_Related_ID();
	}



}
