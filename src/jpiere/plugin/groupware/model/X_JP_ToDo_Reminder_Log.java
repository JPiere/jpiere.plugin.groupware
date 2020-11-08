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

/** Generated Model for JP_ToDo_Reminder_Log
 *  @author iDempiere (generated) 
 *  @version Release 7.1 - $Id$ */
public class X_JP_ToDo_Reminder_Log extends PO implements I_JP_ToDo_Reminder_Log, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20201108L;

    /** Standard Constructor */
    public X_JP_ToDo_Reminder_Log (Properties ctx, int JP_ToDo_Reminder_Log_ID, String trxName)
    {
      super (ctx, JP_ToDo_Reminder_Log_ID, trxName);
      /** if (JP_ToDo_Reminder_Log_ID == 0)
        {
			setIsError (false);
// N
			setJP_ToDo_Reminder_Log_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_ToDo_Reminder_Log (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ToDo_Reminder_Log[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_BroadcastMessage getAD_BroadcastMessage() throws RuntimeException
    {
		return (org.compiere.model.I_AD_BroadcastMessage)MTable.get(getCtx(), org.compiere.model.I_AD_BroadcastMessage.Table_Name)
			.getPO(getAD_BroadcastMessage_ID(), get_TrxName());	}

	/** Set Broadcast Message.
		@param AD_BroadcastMessage_ID 
		Broadcast Message
	  */
	public void setAD_BroadcastMessage_ID (int AD_BroadcastMessage_ID)
	{
		if (AD_BroadcastMessage_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_AD_BroadcastMessage_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_AD_BroadcastMessage_ID, Integer.valueOf(AD_BroadcastMessage_ID));
	}

	/** Get Broadcast Message.
		@return Broadcast Message
	  */
	public int getAD_BroadcastMessage_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_BroadcastMessage_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_UserMail getAD_UserMail() throws RuntimeException
    {
		return (org.compiere.model.I_AD_UserMail)MTable.get(getCtx(), org.compiere.model.I_AD_UserMail.Table_Name)
			.getPO(getAD_UserMail_ID(), get_TrxName());	}

	/** Set User Mail.
		@param AD_UserMail_ID 
		Mail sent to the user
	  */
	public void setAD_UserMail_ID (int AD_UserMail_ID)
	{
		if (AD_UserMail_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_AD_UserMail_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_AD_UserMail_ID, Integer.valueOf(AD_UserMail_ID));
	}

	/** Get User Mail.
		@return Mail sent to the user
	  */
	public int getAD_UserMail_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_UserMail_ID);
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

	/** Set Error.
		@param IsError 
		An Error occurred in the execution
	  */
	public void setIsError (boolean IsError)
	{
		set_Value (COLUMNNAME_IsError, Boolean.valueOf(IsError));
	}

	/** Get Error.
		@return An Error occurred in the execution
	  */
	public boolean isError () 
	{
		Object oo = get_Value(COLUMNNAME_IsError);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	public I_JP_ToDo_Reminder getJP_ToDo_Reminder() throws RuntimeException
    {
		return (I_JP_ToDo_Reminder)MTable.get(getCtx(), I_JP_ToDo_Reminder.Table_Name)
			.getPO(getJP_ToDo_Reminder_ID(), get_TrxName());	}

	/** Set ToDo Reminder.
		@param JP_ToDo_Reminder_ID ToDo Reminder	  */
	public void setJP_ToDo_Reminder_ID (int JP_ToDo_Reminder_ID)
	{
		if (JP_ToDo_Reminder_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ToDo_Reminder_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ToDo_Reminder_ID, Integer.valueOf(JP_ToDo_Reminder_ID));
	}

	/** Get ToDo Reminder.
		@return ToDo Reminder	  */
	public int getJP_ToDo_Reminder_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ToDo_Reminder_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set ToDo Reminder Log.
		@param JP_ToDo_Reminder_Log_ID ToDo Reminder Log	  */
	public void setJP_ToDo_Reminder_Log_ID (int JP_ToDo_Reminder_Log_ID)
	{
		if (JP_ToDo_Reminder_Log_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ToDo_Reminder_Log_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ToDo_Reminder_Log_ID, Integer.valueOf(JP_ToDo_Reminder_Log_ID));
	}

	/** Get ToDo Reminder Log.
		@return ToDo Reminder Log	  */
	public int getJP_ToDo_Reminder_Log_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ToDo_Reminder_Log_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_ToDo_Reminder_Log_UU.
		@param JP_ToDo_Reminder_Log_UU JP_ToDo_Reminder_Log_UU	  */
	public void setJP_ToDo_Reminder_Log_UU (String JP_ToDo_Reminder_Log_UU)
	{
		set_Value (COLUMNNAME_JP_ToDo_Reminder_Log_UU, JP_ToDo_Reminder_Log_UU);
	}

	/** Get JP_ToDo_Reminder_Log_UU.
		@return JP_ToDo_Reminder_Log_UU	  */
	public String getJP_ToDo_Reminder_Log_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ToDo_Reminder_Log_UU);
	}
}