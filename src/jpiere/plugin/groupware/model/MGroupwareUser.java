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
import java.util.Properties;

import org.compiere.util.CCache;
import org.compiere.util.DB;


/**
 * JPIERE-0469: Groupware User
 *
 * @author h.hagiwara
 *
 *
 */
public class MGroupwareUser extends X_JP_GroupwareUser {

	private static final long serialVersionUID = -7852064990205629285L;

	public MGroupwareUser(Properties ctx, int JP_GroupwareUser_ID, String trxName)
	{
		super(ctx, JP_GroupwareUser_ID, trxName);
	}

	public MGroupwareUser(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}


	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(getJP_ToDo_Calendar_BeginTime() >= getJP_ToDo_Calendar_EndTime())
		{
			log.saveError("Error", "時間の入力が間違っています。");//TODO
			return false;
		}

		return true;
	}



	@Override
	protected boolean afterDelete(boolean success)
	{
		s_cache.put (getAD_User_ID(), this);
		return true;
	}



	/**	Cache				*/
	private static CCache<Integer,MGroupwareUser>	s_cache = new CCache<Integer,MGroupwareUser>(Table_Name, 20);

	public static MGroupwareUser get (Properties ctx, int AD_User_ID)
	{
		Integer ii = Integer.valueOf(AD_User_ID);
		MGroupwareUser gUser = (MGroupwareUser)s_cache.get(ii);
		if (gUser != null)
			return gUser;

		String sql = "SELECT * FROM JP_GroupwareUser WHERE AD_User_ID=? ";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, AD_User_ID);
			rs = pstmt.executeQuery();
			if (rs.next())
				gUser = new MGroupwareUser (ctx, rs, null);
		}
		catch (Exception e)
		{
			//log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		if (gUser != null && gUser.get_ID () != 0)
		{
			s_cache.put (AD_User_ID, gUser);
			return gUser;
		}else {
			return null;
		}

	}	//	get



}
