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
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;

public class MInfoGadgetCategory extends X_JP_InfoGadgetCategory {

	private static final long serialVersionUID = 5825106918191924411L;

	public MInfoGadgetCategory(Properties ctx, int JP_InfoGadgetCategory_ID,
			String trxName) {
		super(ctx, JP_InfoGadgetCategory_ID, trxName);
	}

	public MInfoGadgetCategory(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public MInfoGadget[] getInfoGadgets(String whereClause, String orderClause, int maxRows)
	{

		StringBuilder whereClauseFinal = new StringBuilder(MInfoGadget.COLUMNNAME_JP_InfoGadgetCategory_ID +"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MInfoGadget.COLUMNNAME_Date1;
		//
//		List<MInfoGadget> list = new Query(Env.getCtx(), MInfoGadget.Table_Name, whereClauseFinal.toString(), get_TrxName())
//										.setParameters(get_ID())
//										.setOrderBy(orderClause)
//										.list();

		ArrayList<MInfoGadget> list = new ArrayList<MInfoGadget>();
		StringBuilder sql = new StringBuilder("SELECT * FROM " + MInfoGadget.Table_Name + " WHERE " + MInfoGadget.COLUMNNAME_JP_InfoGadgetCategory_ID + "=" + get_ID());
							sql.append(whereClause);
							sql.append(" ORDER BY ").append(orderClause);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			if (maxRows > 0)
			{
				pstmt.setMaxRows(maxRows);
			}
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MInfoGadget (getCtx(), rs, get_TrxName()));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return list.toArray(new MInfoGadget[list.size()]);
	}

	/**	Info Gadget belon to Category					*/
	protected MInfoGadget[] 	m_InfoGadgets = null;

	/**
	 * 	Get Lines of Order
	 * 	@param requery requery
	 * 	@param orderBy optional order by column
	 * 	@return lines
	 */
	public MInfoGadget[] getInfoGadgets (boolean requery, String orderBy)
	{
		if (m_InfoGadgets != null && !requery) {
			set_TrxName(m_InfoGadgets, get_TrxName());
			return m_InfoGadgets;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "Date1 DESC";

		m_InfoGadgets = getInfoGadgets(null, orderClause, 0);
		return m_InfoGadgets;
	}	//	getLines

	/**
	 * 	Get Lines of Order.
	 * 	(used by web store)
	 * 	@return lines
	 */
	public MInfoGadget[] getInfoGadgets()
	{
		return getInfoGadgets(false, null);
	}	//	getLines

	/**
	 * 	Get BPartner with Value
	 *	@param ctx context
	 *	@param Value value
	 *	@return BPartner or null
	 */
	public static MInfoGadgetCategory get (Properties ctx, String Value)
	{
		if (Value == null || Value.length() == 0)
			return null;
		final String whereClause = "Value=? AND AD_Client_ID=?";
		MInfoGadgetCategory retValue = new Query(ctx, MInfoGadgetCategory.Table_Name, whereClause, null)
		.setParameters(Value,Env.getAD_Client_ID(ctx))
		.firstOnly();
		return retValue;
	}	//	get

}
