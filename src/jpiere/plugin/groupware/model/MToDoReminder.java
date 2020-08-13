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
