/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package jpiere.plugin.groupware.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for JP_ToDo_Member_Additional
 *  @author iDempiere (generated)
 *  @version Release 12 - $Id$ */
@org.adempiere.base.Model(table="JP_ToDo_Member_Additional")
public class X_JP_ToDo_Member_Additional extends PO implements I_JP_ToDo_Member_Additional, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20251129L;

    /** Standard Constructor */
    public X_JP_ToDo_Member_Additional (Properties ctx, int JP_ToDo_Member_Additional_ID, String trxName)
    {
      super (ctx, JP_ToDo_Member_Additional_ID, trxName);
      /** if (JP_ToDo_Member_Additional_ID == 0)
        {
			setJP_ToDo_Member_Additional_ID (0);
			setJP_ToDo_Team_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_JP_ToDo_Member_Additional (Properties ctx, int JP_ToDo_Member_Additional_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_ToDo_Member_Additional_ID, trxName, virtualColumns);
      /** if (JP_ToDo_Member_Additional_ID == 0)
        {
			setJP_ToDo_Member_Additional_ID (0);
			setJP_ToDo_Team_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_JP_ToDo_Member_Additional (Properties ctx, String JP_ToDo_Member_Additional_UU, String trxName)
    {
      super (ctx, JP_ToDo_Member_Additional_UU, trxName);
      /** if (JP_ToDo_Member_Additional_UU == null)
        {
			setJP_ToDo_Member_Additional_ID (0);
			setJP_ToDo_Team_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_JP_ToDo_Member_Additional (Properties ctx, String JP_ToDo_Member_Additional_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_ToDo_Member_Additional_UU, trxName, virtualColumns);
      /** if (JP_ToDo_Member_Additional_UU == null)
        {
			setJP_ToDo_Member_Additional_ID (0);
			setJP_ToDo_Team_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_ToDo_Member_Additional (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 6 - System - Client
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_JP_ToDo_Member_Additional[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getAD_User_ID(), get_TrxName());
	}

	/** Set User/Contact.
		@param AD_User_ID User within the system - Internal or Business Partner Contact
	*/
	public void setAD_User_ID (int AD_User_ID)
	{
		if (AD_User_ID < 1)
			set_Value (COLUMNNAME_AD_User_ID, null);
		else
			set_Value (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	}

	/** Get User/Contact.
		@return User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Additional Team ToDo Member.
		@param JP_ToDo_Member_Additional_ID Additional Team ToDo Member
	*/
	public void setJP_ToDo_Member_Additional_ID (int JP_ToDo_Member_Additional_ID)
	{
		if (JP_ToDo_Member_Additional_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_ToDo_Member_Additional_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_ToDo_Member_Additional_ID, Integer.valueOf(JP_ToDo_Member_Additional_ID));
	}

	/** Get Additional Team ToDo Member.
		@return Additional Team ToDo Member	  */
	public int getJP_ToDo_Member_Additional_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ToDo_Member_Additional_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_ToDo_Member_Additional_UU.
		@param JP_ToDo_Member_Additional_UU JP_ToDo_Member_Additional_UU
	*/
	public void setJP_ToDo_Member_Additional_UU (String JP_ToDo_Member_Additional_UU)
	{
		set_Value (COLUMNNAME_JP_ToDo_Member_Additional_UU, JP_ToDo_Member_Additional_UU);
	}

	/** Get JP_ToDo_Member_Additional_UU.
		@return JP_ToDo_Member_Additional_UU	  */
	public String getJP_ToDo_Member_Additional_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_ToDo_Member_Additional_UU);
	}

	public I_JP_ToDo_Team getJP_ToDo_Team() throws RuntimeException
	{
		return (I_JP_ToDo_Team)MTable.get(getCtx(), I_JP_ToDo_Team.Table_ID)
			.getPO(getJP_ToDo_Team_ID(), get_TrxName());
	}

	/** Set Team ToDo.
		@param JP_ToDo_Team_ID Team ToDo
	*/
	public void setJP_ToDo_Team_ID (int JP_ToDo_Team_ID)
	{
		if (JP_ToDo_Team_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_ToDo_Team_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_ToDo_Team_ID, Integer.valueOf(JP_ToDo_Team_ID));
	}

	/** Get Team ToDo.
		@return Team ToDo	  */
	public int getJP_ToDo_Team_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ToDo_Team_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}