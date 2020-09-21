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

/** Generated Model for JP_ToDo_Team_Reminder
 *  @author iDempiere (generated) 
 *  @version Release 7.1 - $Id$ */
public class X_JP_ToDo_Team_Reminder extends PO implements I_JP_ToDo_Team_Reminder, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200921L;

    /** Standard Constructor */
    public X_JP_ToDo_Team_Reminder (Properties ctx, int JP_ToDo_Team_Reminder_ID, String trxName)
    {
      super (ctx, JP_ToDo_Team_Reminder_ID, trxName);
      /** if (JP_ToDo_Team_Reminder_ID == 0)
        {
			setDescription (null);
			setIsSentReminderJP (false);
// N
			setJP_Mandatory_Statistics_Info (null);
// NO
			setJP_ToDo_RemindTime (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setJP_ToDo_ReminderType (null);
// M
			setJP_ToDo_Team_ID (0);
			setJP_ToDo_Team_Reminder_ID (0);
			setProcessed (false);
// N
        } */
    }

    /** Load Constructor */
    public X_JP_ToDo_Team_Reminder (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ToDo_Team_Reminder[")
        .append(get_ID()).append("]");
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

	/** Yes / No = YN */
	public static final String JP_MANDATORY_STATISTICS_INFO_YesNo = "YN";
	/** Choice = CC */
	public static final String JP_MANDATORY_STATISTICS_INFO_Choice = "CC";
	/** Date and Time = DT */
	public static final String JP_MANDATORY_STATISTICS_INFO_DateAndTime = "DT";
	/** Number = NM */
	public static final String JP_MANDATORY_STATISTICS_INFO_Number = "NM";
	/** None = NO */
	public static final String JP_MANDATORY_STATISTICS_INFO_None = "NO";
	/** Set Mandatory Statistics Info.
		@param JP_Mandatory_Statistics_Info Mandatory Statistics Info	  */
	public void setJP_Mandatory_Statistics_Info (String JP_Mandatory_Statistics_Info)
	{

		set_Value (COLUMNNAME_JP_Mandatory_Statistics_Info, JP_Mandatory_Statistics_Info);
	}

	/** Get Mandatory Statistics Info.
		@return Mandatory Statistics Info	  */
	public String getJP_Mandatory_Statistics_Info () 
	{
		return (String)get_Value(COLUMNNAME_JP_Mandatory_Statistics_Info);
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

	public I_JP_ToDo_Team getJP_ToDo_Team() throws RuntimeException
    {
		return (I_JP_ToDo_Team)MTable.get(getCtx(), I_JP_ToDo_Team.Table_Name)
			.getPO(getJP_ToDo_Team_ID(), get_TrxName());	}

	/** Set Team ToDo.
		@param JP_ToDo_Team_ID Team ToDo	  */
	public void setJP_ToDo_Team_ID (int JP_ToDo_Team_ID)
	{
		if (JP_ToDo_Team_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ToDo_Team_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ToDo_Team_ID, Integer.valueOf(JP_ToDo_Team_ID));
	}

	/** Get Team ToDo.
		@return Team ToDo	  */
	public int getJP_ToDo_Team_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ToDo_Team_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

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

	/** Set JP_ToDo_Team_Reminder_UU.
		@param JP_ToDo_Team_Reminder_UU JP_ToDo_Team_Reminder_UU	  */
	public void setJP_ToDo_Team_Reminder_UU (String JP_ToDo_Team_Reminder_UU)
	{
		set_Value (COLUMNNAME_JP_ToDo_Team_Reminder_UU, JP_ToDo_Team_Reminder_UU);
	}

	/** Get JP_ToDo_Team_Reminder_UU.
		@return JP_ToDo_Team_Reminder_UU	  */
	public String getJP_ToDo_Team_Reminder_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ToDo_Team_Reminder_UU);
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