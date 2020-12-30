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

/** Generated Model for JP_InfoGadgetCategory
 *  @author iDempiere (generated) 
 *  @version Release 8.2 - $Id$ */
public class X_JP_InfoGadgetCategory extends PO implements I_JP_InfoGadgetCategory, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20201230L;

    /** Standard Constructor */
    public X_JP_InfoGadgetCategory (Properties ctx, int JP_InfoGadgetCategory_ID, String trxName)
    {
      super (ctx, JP_InfoGadgetCategory_ID, trxName);
      /** if (JP_InfoGadgetCategory_ID == 0)
        {
			setJP_InfoGadgetCategory_ID (0);
			setJP_PageSize (0);
// 5
			setJP_UserDisplayPosition (null);
// N
			setMaxQueryRecords (0);
// 0
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_JP_InfoGadgetCategory (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 7 - System - Client - Org 
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
      StringBuilder sb = new StringBuilder ("X_JP_InfoGadgetCategory[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
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

	/** Set Comment/Help.
		@param Help 
		Comment or Hint
	  */
	public void setHelp (String Help)
	{
		set_Value (COLUMNNAME_Help, Help);
	}

	/** Get Comment/Help.
		@return Comment or Hint
	  */
	public String getHelp () 
	{
		return (String)get_Value(COLUMNNAME_Help);
	}

	/** Set Info Gadget Category.
		@param JP_InfoGadgetCategory_ID Info Gadget Category	  */
	public void setJP_InfoGadgetCategory_ID (int JP_InfoGadgetCategory_ID)
	{
		if (JP_InfoGadgetCategory_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_InfoGadgetCategory_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_InfoGadgetCategory_ID, Integer.valueOf(JP_InfoGadgetCategory_ID));
	}

	/** Get Info Gadget Category.
		@return Info Gadget Category	  */
	public int getJP_InfoGadgetCategory_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_InfoGadgetCategory_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_InfoGadgetCategory_UU.
		@param JP_InfoGadgetCategory_UU JP_InfoGadgetCategory_UU	  */
	public void setJP_InfoGadgetCategory_UU (String JP_InfoGadgetCategory_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_InfoGadgetCategory_UU, JP_InfoGadgetCategory_UU);
	}

	/** Get JP_InfoGadgetCategory_UU.
		@return JP_InfoGadgetCategory_UU	  */
	public String getJP_InfoGadgetCategory_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_InfoGadgetCategory_UU);
	}

	/** Set Page Size.
		@param JP_PageSize Page Size	  */
	public void setJP_PageSize (int JP_PageSize)
	{
		set_Value (COLUMNNAME_JP_PageSize, Integer.valueOf(JP_PageSize));
	}

	/** Get Page Size.
		@return Page Size	  */
	public int getJP_PageSize () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PageSize);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** None = N */
	public static final String JP_USERDISPLAYPOSITION_None = "N";
	/** Right side of the title = R */
	public static final String JP_USERDISPLAYPOSITION_RightSideOfTheTitle = "R";
	/** Left side of the title = L */
	public static final String JP_USERDISPLAYPOSITION_LeftSideOfTheTitle = "L";
	/** Set User Display Position.
		@param JP_UserDisplayPosition 
		JPIERE-0110:JPPS
	  */
	public void setJP_UserDisplayPosition (String JP_UserDisplayPosition)
	{

		set_Value (COLUMNNAME_JP_UserDisplayPosition, JP_UserDisplayPosition);
	}

	/** Get User Display Position.
		@return JPIERE-0110:JPPS
	  */
	public String getJP_UserDisplayPosition () 
	{
		return (String)get_Value(COLUMNNAME_JP_UserDisplayPosition);
	}

	/** Set Max Query Records.
		@param MaxQueryRecords 
		If defined, you cannot query more records as defined - the query criteria needs to be changed to query less records
	  */
	public void setMaxQueryRecords (int MaxQueryRecords)
	{
		set_Value (COLUMNNAME_MaxQueryRecords, Integer.valueOf(MaxQueryRecords));
	}

	/** Get Max Query Records.
		@return If defined, you cannot query more records as defined - the query criteria needs to be changed to query less records
	  */
	public int getMaxQueryRecords () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MaxQueryRecords);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue () 
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}