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

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Properties;

import org.compiere.model.MMessage;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0469: JPiere Groupware
 *
 * MToDoReminder
 *
 * @author h.hagiwara
 *
 */
public class MToDoReminder extends X_JP_ToDo_Reminder {

	private MToDo parent = null;

	public MToDoReminder(Properties ctx, int JP_ToDo_Team_ID, String trxName)
	{
		super(ctx, JP_ToDo_Team_ID, trxName);
	}


	public MToDoReminder(Properties ctx, ResultSet rs, String trxName)
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

		//** Check User**/
		if(!newRecord)
		{
			int loginUser  = Env.getAD_User_ID(getCtx());
			if(loginUser == getParent().getAD_User_ID() || loginUser == getParent().getCreatedBy() || loginUser == getCreatedBy())
			{
				;//Updatable

			}else {

				MMessage msg = MMessage.get(getCtx(), "AccessCannotUpdate");
				return msg.get_Translation("MsgText") + " - "+ msg.get_Translation("MsgTip");
			}

		}

		if(is_ValueChanged(MToDoReminder.COLUMNNAME_IsConfirmed))
		{
			if(isConfirmed() && getJP_Confirmed() == null)
			{
				setJP_Confirmed(Timestamp.valueOf(LocalDateTime.now()));
				setProcessed(true);

			}else {

				setJP_Confirmed(null);
				setProcessed(false);
			}
		}

		//*** Check Statistics info ***//
		if(getJP_ToDo_Team_Reminder_ID() != 0 && isConfirmed() && is_ValueChanged("IsConfirmed"))
		{
			MToDoTeamReminder teamToDoReminder = new MToDoTeamReminder(getCtx(), getJP_ToDo_Team_Reminder_ID(), get_TrxName());
			if(MToDoTeamReminder.JP_MANDATORY_STATISTICS_INFO_None.equals(teamToDoReminder.getJP_Mandatory_Statistics_Info()))
			{
				;//Noting to do;

			}else if(MToDoTeamReminder.JP_MANDATORY_STATISTICS_INFO_YesNo.equals(teamToDoReminder.getJP_Mandatory_Statistics_Info())){

				if(Util.isEmpty(getJP_Statistics_YesNo()))
				{
					String msg = Msg.getElement(getCtx(), MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info);
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_Statistics_YesNo)};
					return msg + " : " + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
				}

			}else if(MToDoTeamReminder.JP_MANDATORY_STATISTICS_INFO_Choice.equals(teamToDoReminder.getJP_Mandatory_Statistics_Info())){

				if(Util.isEmpty(getJP_Statistics_Choice()))
				{
					String msg = Msg.getElement(getCtx(), MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info);
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_Statistics_Choice)};
					return msg + " : " + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
				}

			}else if(MToDoTeamReminder.JP_MANDATORY_STATISTICS_INFO_DateAndTime.equals(teamToDoReminder.getJP_Mandatory_Statistics_Info())){

				if(getJP_Statistics_DateAndTime() == null)
				{
					String msg = Msg.getElement(getCtx(), MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info);
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_Statistics_DateAndTime)};
					return msg + " : " + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
				}

			}else if(MToDoTeamReminder.JP_MANDATORY_STATISTICS_INFO_Number.equals(teamToDoReminder.getJP_Mandatory_Statistics_Info())){

				if(get_Value("JP_Statistics_Number") == null)
				{
					String msg = Msg.getElement(getCtx(), MToDoTeam.COLUMNNAME_JP_Mandatory_Statistics_Info);
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_Statistics_Number)};
					return msg + " : " + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
				}
			}

		}//if(getJP_ToDo_Team_Reminder_ID() != 0)

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
		//** Check User**/
		int loginUser  = Env.getAD_User_ID(getCtx());
		if(loginUser == getParent().getAD_User_ID() || loginUser == getParent().getCreatedBy() || loginUser == getCreatedBy())
		{
			//Deleteable;

		}else {

			MMessage msg = MMessage.get(getCtx(), "AccessCannotUpdate");
			return msg.get_Translation("MsgText") + " - "+ msg.get_Translation("MsgTip");
		}


		return null;
	}



	private MToDo getParent()
	{
		if(parent == null)
			parent = new MToDo(getCtx(), getJP_ToDo_ID(), get_TrxName());

		return parent;
	}

}
