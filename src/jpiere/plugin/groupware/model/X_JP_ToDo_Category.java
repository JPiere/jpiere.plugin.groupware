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
import org.compiere.util.KeyNamePair;

/** Generated Model for JP_ToDo_Category
 *  @author iDempiere (generated) 
 *  @version Release 7.1 - $Id$ */
public class X_JP_ToDo_Category extends PO implements I_JP_ToDo_Category, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200921L;

    /** Standard Constructor */
    public X_JP_ToDo_Category (Properties ctx, int JP_ToDo_Category_ID, String trxName)
    {
      super (ctx, JP_ToDo_Category_ID, trxName);
      /** if (JP_ToDo_Category_ID == 0)
        {
			setJP_ToDo_Category_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_JP_ToDo_Category (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ToDo_Category[")
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

	/** Set Color Picker.
		@param JP_ColorPicker Color Picker	  */
	public void setJP_ColorPicker (String JP_ColorPicker)
	{
		set_Value (COLUMNNAME_JP_ColorPicker, JP_ColorPicker);
	}

	/** Get Color Picker.
		@return Color Picker	  */
	public String getJP_ColorPicker () 
	{
		return (String)get_Value(COLUMNNAME_JP_ColorPicker);
	}

	/** Set Color Picker2.
		@param JP_ColorPicker2 Color Picker2	  */
	public void setJP_ColorPicker2 (String JP_ColorPicker2)
	{
		set_Value (COLUMNNAME_JP_ColorPicker2, JP_ColorPicker2);
	}

	/** Get Color Picker2.
		@return Color Picker2	  */
	public String getJP_ColorPicker2 () 
	{
		return (String)get_Value(COLUMNNAME_JP_ColorPicker2);
	}

	/** Set ToDo Category.
		@param JP_ToDo_Category_ID ToDo Category	  */
	public void setJP_ToDo_Category_ID (int JP_ToDo_Category_ID)
	{
		if (JP_ToDo_Category_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ToDo_Category_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ToDo_Category_ID, Integer.valueOf(JP_ToDo_Category_ID));
	}

	/** Get ToDo Category.
		@return ToDo Category	  */
	public int getJP_ToDo_Category_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ToDo_Category_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_ToDo_Category_UU.
		@param JP_ToDo_Category_UU JP_ToDo_Category_UU	  */
	public void setJP_ToDo_Category_UU (String JP_ToDo_Category_UU)
	{
		set_Value (COLUMNNAME_JP_ToDo_Category_UU, JP_ToDo_Category_UU);
	}

	/** Get JP_ToDo_Category_UU.
		@return JP_ToDo_Category_UU	  */
	public String getJP_ToDo_Category_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ToDo_Category_UU);
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getName());
    }
}