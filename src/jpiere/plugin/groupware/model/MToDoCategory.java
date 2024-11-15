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
import java.util.Properties;

import org.compiere.model.MMessage;
import org.compiere.model.MRole;
import org.compiere.util.CCache;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0469: ToDo & Schedule Management
 *
 * @author h.hagiwara
 *
 */
public class MToDoCategory extends X_JP_ToDo_Category {

	private static final long serialVersionUID = -7982664399088585073L;

	public MToDoCategory(Properties ctx, int JP_ToDo_Category_ID, String trxName)
	{
		super(ctx, JP_ToDo_Category_ID, trxName);
	}

	public MToDoCategory(Properties ctx, ResultSet rs, String trxName)
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

		int loginUser = Env.getAD_User_ID(getCtx());
		if(getAD_User_ID() != 0 && getAD_User_ID() != loginUser)
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

		int loginUser = Env.getAD_User_ID(getCtx());
		if(getAD_User_ID() != 0 && getAD_User_ID() != loginUser)
		{
			MMessage msg = MMessage.get(getCtx(), "AccessCannotUpdate");//You cannot update this record - You don't have the privileges
			String msgString = msg.get_Translation("MsgText") + " - "+ msg.get_Translation("MsgTip");
			return msgString + " : " + Msg.getMsg(getCtx(), "JP_DifferentUser");//Different User
		}

		return null;
	}

	/**	Cache				*/
	private static CCache<Integer,MToDoCategory>	s_cache = new CCache<Integer,MToDoCategory>(Table_Name, 20);

	public static MToDoCategory get (Properties ctx, int JP_ToDo_Category_ID)
	{
		Integer ii = Integer.valueOf(JP_ToDo_Category_ID);
		MToDoCategory retValue = (MToDoCategory)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MToDoCategory (ctx, JP_ToDo_Category_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ToDo_Category_ID, retValue);
		return retValue;
	}	//	get


}
