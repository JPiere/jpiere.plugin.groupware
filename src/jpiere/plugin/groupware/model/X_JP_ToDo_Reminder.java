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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for JP_ToDo_Reminder
 *  @author iDempiere (generated) 
 *  @version Release 7.1 - $Id$ */
public class X_JP_ToDo_Reminder extends PO implements I_JP_ToDo_Reminder, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200921L;

    /** Standard Constructor */
    public X_JP_ToDo_Reminder (Properties ctx, int JP_ToDo_Reminder_ID, String trxName)
    {
      super (ctx, JP_ToDo_Reminder_ID, trxName);
      /** if (JP_ToDo_Reminder_ID == 0)
        {
			setDescription (null);
			setIsConfirmed (false);
// N
			setIsSentReminderJP (false);
// N
			setJP_ToDo_ID (0);
			setJP_ToDo_RemindTime (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setJP_ToDo_ReminderType (null);
// M
			setJP_ToDo_Reminder_ID (0);
			setProcessed (false);
        } */
    }

    /** Load Constructor */
    public X_JP_ToDo_Reminder (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ToDo_Reminder[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Comments.
		@param Comments 
		Comments or additional information
	  */
	public void setComments (String Comments)
	{
		set_Value (COLUMNNAME_Comments, Comments);
	}

	/** Get Comments.
		@return Comments or additional information
	  */
	public String getComments () 
	{
		return (String)get_Value(COLUMNNAME_Comments);
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

	/** Set Confirmed.
		@param IsConfirmed 
		Assignment is confirmed
	  */
	public void setIsConfirmed (boolean IsConfirmed)
	{
		set_Value (COLUMNNAME_IsConfirmed, Boolean.valueOf(IsConfirmed));
	}

	/** Get Confirmed.
		@return Assignment is confirmed
	  */
	public boolean isConfirmed () 
	{
		Object oo = get_Value(COLUMNNAME_IsConfirmed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Sent Reminder.
		@param IsSentReminderJP Sent Reminder	  */
	public void setIsSentReminderJP (boolean IsSentReminderJP)
	{
		set_Value (COLUMNNAME_IsSentReminderJP, Boolean.valueOf(IsSentReminderJP));
	}

	/** Get Sent Reminder.
		@return Sent Reminder	  */
	public boolean isSentReminderJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsSentReminderJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Confirmed.
		@param JP_Confirmed Confirmed	  */
	public void setJP_Confirmed (Timestamp JP_Confirmed)
	{
		set_Value (COLUMNNAME_JP_Confirmed, JP_Confirmed);
	}

	/** Get Confirmed.
		@return Confirmed	  */
	public Timestamp getJP_Confirmed () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_Confirmed);
	}

	/** A = A */
	public static final String JP_STATISTICS_CHOICE_A = "A";
	/** B = B */
	public static final String JP_STATISTICS_CHOICE_B = "B";
	/** C = C */
	public static final String JP_STATISTICS_CHOICE_C = "C";
	/** D = D */
	public static final String JP_STATISTICS_CHOICE_D = "D";
	/** E = E */
	public static final String JP_STATISTICS_CHOICE_E = "E";
	/** Set Choice.
		@param JP_Statistics_Choice Choice	  */
	public void setJP_Statistics_Choice (String JP_Statistics_Choice)
	{

		set_Value (COLUMNNAME_JP_Statistics_Choice, JP_Statistics_Choice);
	}

	/** Get Choice.
		@return Choice	  */
	public String getJP_Statistics_Choice () 
	{
		return (String)get_Value(COLUMNNAME_JP_Statistics_Choice);
	}

	/** Set Date and Time.
		@param JP_Statistics_DateAndTime Date and Time	  */
	public void setJP_Statistics_DateAndTime (Timestamp JP_Statistics_DateAndTime)
	{
		set_Value (COLUMNNAME_JP_Statistics_DateAndTime, JP_Statistics_DateAndTime);
	}

	/** Get Date and Time.
		@return Date and Time	  */
	public Timestamp getJP_Statistics_DateAndTime () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_Statistics_DateAndTime);
	}

	/** Set Number.
		@param JP_Statistics_Number Number	  */
	public void setJP_Statistics_Number (BigDecimal JP_Statistics_Number)
	{
		set_Value (COLUMNNAME_JP_Statistics_Number, JP_Statistics_Number);
	}

	/** Get Number.
		@return Number	  */
	public BigDecimal getJP_Statistics_Number () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_Statistics_Number);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** JP_Statistics_YesNo AD_Reference_ID=319 */
	public static final int JP_STATISTICS_YESNO_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String JP_STATISTICS_YESNO_Yes = "Y";
	/** No = N */
	public static final String JP_STATISTICS_YESNO_No = "N";
	/** Set Yes / No.
		@param JP_Statistics_YesNo Yes / No	  */
	public void setJP_Statistics_YesNo (String JP_Statistics_YesNo)
	{

		set_Value (COLUMNNAME_JP_Statistics_YesNo, JP_Statistics_YesNo);
	}

	/** Get Yes / No.
		@return Yes / No	  */
	public String getJP_Statistics_YesNo () 
	{
		return (String)get_Value(COLUMNNAME_JP_Statistics_YesNo);
	}

	public I_JP_ToDo getJP_ToDo() throws RuntimeException
    {
		return (I_JP_ToDo)MTable.get(getCtx(), I_JP_ToDo.Table_Name)
			.getPO(getJP_ToDo_ID(), get_TrxName());	}

	/** Set Personal ToDo.
		@param JP_ToDo_ID Personal ToDo	  */
	public void setJP_ToDo_ID (int JP_ToDo_ID)
	{
		if (JP_ToDo_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ToDo_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ToDo_ID, Integer.valueOf(JP_ToDo_ID));
	}

	/** Get Personal ToDo.
		@return Personal ToDo	  */
	public int getJP_ToDo_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ToDo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Remind Time.
		@param JP_ToDo_RemindTime Remind Time	  */
	public void setJP_ToDo_RemindTime (Timestamp JP_ToDo_RemindTime)
	{
		set_Value (COLUMNNAME_JP_ToDo_RemindTime, JP_ToDo_RemindTime);
	}

	/** Get Remind Time.
		@return Remind Time	  */
	public Timestamp getJP_ToDo_RemindTime () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ToDo_RemindTime);
	}

	/** Mail = M */
	public static final String JP_TODO_REMINDERTYPE_Mail = "M";
	/** Set Reminder Type.
		@param JP_ToDo_ReminderType Reminder Type	  */
	public void setJP_ToDo_ReminderType (String JP_ToDo_ReminderType)
	{

		set_Value (COLUMNNAME_JP_ToDo_ReminderType, JP_ToDo_ReminderType);
	}

	/** Get Reminder Type.
		@return Reminder Type	  */
	public String getJP_ToDo_ReminderType () 
	{
		return (String)get_Value(COLUMNNAME_JP_ToDo_ReminderType);
	}

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

	/** Set JP_ToDo_Reminder_UU.
		@param JP_ToDo_Reminder_UU JP_ToDo_Reminder_UU	  */
	public void setJP_ToDo_Reminder_UU (String JP_ToDo_Reminder_UU)
	{
		set_Value (COLUMNNAME_JP_ToDo_Reminder_UU, JP_ToDo_Reminder_UU);
	}

	/** Get JP_ToDo_Reminder_UU.
		@return JP_ToDo_Reminder_UU	  */
	public String getJP_ToDo_Reminder_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ToDo_Reminder_UU);
	}

	public I_JP_ToDo_Team_Reminder getJP_ToDo_Team_Reminder() throws RuntimeException
    {
		return (I_JP_ToDo_Team_Reminder)MTable.get(getCtx(), I_JP_ToDo_Team_Reminder.Table_Name)
			.getPO(getJP_ToDo_Team_Reminder_ID(), get_TrxName());	}

	/** Set JP_ToDo_Team_Reminder.
		@param JP_ToDo_Team_Reminder_ID JP_ToDo_Team_Reminder	  */
	public void setJP_ToDo_Team_Reminder_ID (int JP_ToDo_Team_Reminder_ID)
	{
		if (JP_ToDo_Team_Reminder_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ToDo_Team_Reminder_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ToDo_Team_Reminder_ID, Integer.valueOf(JP_ToDo_Team_Reminder_ID));
	}

	/** Get JP_ToDo_Team_Reminder.
		@return JP_ToDo_Team_Reminder	  */
	public int getJP_ToDo_Team_Reminder_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ToDo_Team_Reminder_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed () 
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}
}