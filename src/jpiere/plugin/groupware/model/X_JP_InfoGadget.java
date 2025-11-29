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
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for JP_InfoGadget
 *  @author iDempiere (generated)
 *  @version Release 12 - $Id$ */
@org.adempiere.base.Model(table="JP_InfoGadget")
public class X_JP_InfoGadget extends PO implements I_JP_InfoGadget, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20251129L;

    /** Standard Constructor */
    public X_JP_InfoGadget (Properties ctx, int JP_InfoGadget_ID, String trxName)
    {
      super (ctx, JP_InfoGadget_ID, trxName);
      /** if (JP_InfoGadget_ID == 0)
        {
			setDate1 (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDateFrom (new Timestamp( System.currentTimeMillis() ));
// @SQL= SELECT DATE_TRUNC('day', TO_DATE('@#Date@', 'YYYY-MM-DD')) 
			setDateTo (new Timestamp( System.currentTimeMillis() ));
// @SQL= SELECT DATE_TRUNC('month', TO_DATE('@#Date@', 'YYYY-MM-DD')) + CAST('1 month' AS INTERVAL)-1
			setIsCollapsedByDefault (false);
// N
			setJP_InfoGadgetCategory_ID (0);
			setJP_InfoGadget_ID (0);
			setName (null);
			setPublishStatus (null);
// U
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_InfoGadget (Properties ctx, int JP_InfoGadget_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_InfoGadget_ID, trxName, virtualColumns);
      /** if (JP_InfoGadget_ID == 0)
        {
			setDate1 (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDateFrom (new Timestamp( System.currentTimeMillis() ));
// @SQL= SELECT DATE_TRUNC('day', TO_DATE('@#Date@', 'YYYY-MM-DD')) 
			setDateTo (new Timestamp( System.currentTimeMillis() ));
// @SQL= SELECT DATE_TRUNC('month', TO_DATE('@#Date@', 'YYYY-MM-DD')) + CAST('1 month' AS INTERVAL)-1
			setIsCollapsedByDefault (false);
// N
			setJP_InfoGadgetCategory_ID (0);
			setJP_InfoGadget_ID (0);
			setName (null);
			setPublishStatus (null);
// U
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_InfoGadget (Properties ctx, String JP_InfoGadget_UU, String trxName)
    {
      super (ctx, JP_InfoGadget_UU, trxName);
      /** if (JP_InfoGadget_UU == null)
        {
			setDate1 (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDateFrom (new Timestamp( System.currentTimeMillis() ));
// @SQL= SELECT DATE_TRUNC('day', TO_DATE('@#Date@', 'YYYY-MM-DD')) 
			setDateTo (new Timestamp( System.currentTimeMillis() ));
// @SQL= SELECT DATE_TRUNC('month', TO_DATE('@#Date@', 'YYYY-MM-DD')) + CAST('1 month' AS INTERVAL)-1
			setIsCollapsedByDefault (false);
// N
			setJP_InfoGadgetCategory_ID (0);
			setJP_InfoGadget_ID (0);
			setName (null);
			setPublishStatus (null);
// U
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_InfoGadget (Properties ctx, String JP_InfoGadget_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_InfoGadget_UU, trxName, virtualColumns);
      /** if (JP_InfoGadget_UU == null)
        {
			setDate1 (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDateFrom (new Timestamp( System.currentTimeMillis() ));
// @SQL= SELECT DATE_TRUNC('day', TO_DATE('@#Date@', 'YYYY-MM-DD')) 
			setDateTo (new Timestamp( System.currentTimeMillis() ));
// @SQL= SELECT DATE_TRUNC('month', TO_DATE('@#Date@', 'YYYY-MM-DD')) + CAST('1 month' AS INTERVAL)-1
			setIsCollapsedByDefault (false);
// N
			setJP_InfoGadgetCategory_ID (0);
			setJP_InfoGadget_ID (0);
			setName (null);
			setPublishStatus (null);
// U
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_JP_InfoGadget (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_InfoGadget[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
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

	/** Set Date.
		@param Date1 Date when business is not conducted
	*/
	public void setDate1 (Timestamp Date1)
	{
		set_Value (COLUMNNAME_Date1, Date1);
	}

	/** Get Date.
		@return Date when business is not conducted
	  */
	public Timestamp getDate1()
	{
		return (Timestamp)get_Value(COLUMNNAME_Date1);
	}

	/** Set Date From.
		@param DateFrom Starting date for a range
	*/
	public void setDateFrom (Timestamp DateFrom)
	{
		set_Value (COLUMNNAME_DateFrom, DateFrom);
	}

	/** Get Date From.
		@return Starting date for a range
	  */
	public Timestamp getDateFrom()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateFrom);
	}

	/** Set Date To.
		@param DateTo End date of a date range
	*/
	public void setDateTo (Timestamp DateTo)
	{
		set_Value (COLUMNNAME_DateTo, DateTo);
	}

	/** Get Date To.
		@return End date of a date range
	  */
	public Timestamp getDateTo()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateTo);
	}

	/** Set Description.
		@param Description Optional short description of the record
	*/
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set HTML.
		@param HTML HTML
	*/
	public void setHTML (String HTML)
	{
		set_Value (COLUMNNAME_HTML, HTML);
	}

	/** Get HTML.
		@return HTML	  */
	public String getHTML()
	{
		return (String)get_Value(COLUMNNAME_HTML);
	}

	/** Set Collapsed By Default.
		@param IsCollapsedByDefault Flag to set the initial state of collapsible field group.
	*/
	public void setIsCollapsedByDefault (boolean IsCollapsedByDefault)
	{
		set_Value (COLUMNNAME_IsCollapsedByDefault, Boolean.valueOf(IsCollapsedByDefault));
	}

	/** Get Collapsed By Default.
		@return Flag to set the initial state of collapsible field group.
	  */
	public boolean isCollapsedByDefault()
	{
		Object oo = get_Value(COLUMNNAME_IsCollapsedByDefault);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public I_JP_InfoGadgetCategory getJP_InfoGadgetCategory() throws RuntimeException
	{
		return (I_JP_InfoGadgetCategory)MTable.get(getCtx(), I_JP_InfoGadgetCategory.Table_ID)
			.getPO(getJP_InfoGadgetCategory_ID(), get_TrxName());
	}

	/** Set Info Gadget Category.
		@param JP_InfoGadgetCategory_ID Info Gadget Category
	*/
	public void setJP_InfoGadgetCategory_ID (int JP_InfoGadgetCategory_ID)
	{
		if (JP_InfoGadgetCategory_ID < 1)
			set_Value (COLUMNNAME_JP_InfoGadgetCategory_ID, null);
		else
			set_Value (COLUMNNAME_JP_InfoGadgetCategory_ID, Integer.valueOf(JP_InfoGadgetCategory_ID));
	}

	/** Get Info Gadget Category.
		@return Info Gadget Category	  */
	public int getJP_InfoGadgetCategory_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_InfoGadgetCategory_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JPiere Information Gadget.
		@param JP_InfoGadget_ID JPiere Information Gadget
	*/
	public void setJP_InfoGadget_ID (int JP_InfoGadget_ID)
	{
		if (JP_InfoGadget_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_InfoGadget_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_InfoGadget_ID, Integer.valueOf(JP_InfoGadget_ID));
	}

	/** Get JPiere Information Gadget.
		@return JPiere Information Gadget	  */
	public int getJP_InfoGadget_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_InfoGadget_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_InfoGadget_UU.
		@param JP_InfoGadget_UU JP_InfoGadget_UU
	*/
	public void setJP_InfoGadget_UU (String JP_InfoGadget_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_InfoGadget_UU, JP_InfoGadget_UU);
	}

	/** Get JP_InfoGadget_UU.
		@return JP_InfoGadget_UU	  */
	public String getJP_InfoGadget_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_InfoGadget_UU);
	}

	/** Set Name.
		@param Name Alphanumeric identifier of the entity
	*/
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName()
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** PublishStatus AD_Reference_ID=310 */
	public static final int PUBLISHSTATUS_AD_Reference_ID=310;
	/** Released = R */
	public static final String PUBLISHSTATUS_Released = "R";
	/** Test = T */
	public static final String PUBLISHSTATUS_Test = "T";
	/** Under Revision = U */
	public static final String PUBLISHSTATUS_UnderRevision = "U";
	/** Void = V */
	public static final String PUBLISHSTATUS_Void = "V";
	/** Set Publication Status.
		@param PublishStatus Status of Publication
	*/
	public void setPublishStatus (String PublishStatus)
	{

		set_Value (COLUMNNAME_PublishStatus, PublishStatus);
	}

	/** Get Publication Status.
		@return Status of Publication
	  */
	public String getPublishStatus()
	{
		return (String)get_Value(COLUMNNAME_PublishStatus);
	}

	/** Set Search Key.
		@param Value Search key for the record in the format required - must be unique
	*/
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue()
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}