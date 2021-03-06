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

/** Generated Model for JP_Team_Member
 *  @author iDempiere (generated) 
 *  @version Release 7.1 - $Id$ */
public class X_JP_Team_Member extends PO implements I_JP_Team_Member, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200806L;

    /** Standard Constructor */
    public X_JP_Team_Member (Properties ctx, int JP_Team_Member_ID, String trxName)
    {
      super (ctx, JP_Team_Member_ID, trxName);
      /** if (JP_Team_Member_ID == 0)
        {
			setJP_Team_ID (0);
			setJP_Team_Member_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_Team_Member (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 2 - Client 
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
      StringBuffer sb = new StringBuffer ("X_JP_Team_Member[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getAD_User_ID(), get_TrxName());	}

	/** Set User/Contact.
		@param AD_User_ID 
		User within the system - Internal or Business Partner Contact
	  */
	public void setAD_User_ID (int AD_User_ID)
	{
		if (AD_User_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_AD_User_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	}

	/** Get User/Contact.
		@return User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	public I_JP_Team getJP_Team() throws RuntimeException
    {
		return (I_JP_Team)MTable.get(getCtx(), I_JP_Team.Table_Name)
			.getPO(getJP_Team_ID(), get_TrxName());	}

	/** Set Team.
		@param JP_Team_ID Team	  */
	public void setJP_Team_ID (int JP_Team_ID)
	{
		if (JP_Team_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_Team_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_Team_ID, Integer.valueOf(JP_Team_ID));
	}

	/** Get Team.
		@return Team	  */
	public int getJP_Team_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Team_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Team Member.
		@param JP_Team_Member_ID Team Member	  */
	public void setJP_Team_Member_ID (int JP_Team_Member_ID)
	{
		if (JP_Team_Member_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_Team_Member_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_Team_Member_ID, Integer.valueOf(JP_Team_Member_ID));
	}

	/** Get Team Member.
		@return Team Member	  */
	public int getJP_Team_Member_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Team_Member_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_Team_Member_UU.
		@param JP_Team_Member_UU JP_Team_Member_UU	  */
	public void setJP_Team_Member_UU (String JP_Team_Member_UU)
	{
		set_Value (COLUMNNAME_JP_Team_Member_UU, JP_Team_Member_UU);
	}

	/** Get JP_Team_Member_UU.
		@return JP_Team_Member_UU	  */
	public String getJP_Team_Member_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_Team_Member_UU);
	}
}