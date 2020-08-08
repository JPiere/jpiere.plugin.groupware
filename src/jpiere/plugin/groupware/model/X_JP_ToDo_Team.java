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
import org.compiere.util.KeyNamePair;

/** Generated Model for JP_ToDo_Team
 *  @author iDempiere (generated) 
 *  @version Release 7.1 - $Id$ */
public class X_JP_ToDo_Team extends PO implements I_JP_ToDo_Team, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200808L;

    /** Standard Constructor */
    public X_JP_ToDo_Team (Properties ctx, int JP_ToDo_Team_ID, String trxName)
    {
      super (ctx, JP_ToDo_Team_ID, trxName);
      /** if (JP_ToDo_Team_ID == 0)
        {
			setJP_Mandatory_Statistics_Info (null);
// NO
			setJP_Processing1 (null);
// N
			setJP_Processing2 (null);
// N
			setJP_Processing3 (null);
// N
			setJP_ToDo_Status (null);
// NY
			setJP_ToDo_Team_ID (0);
			setJP_ToDo_Type (null);
// T
			setName (null);
			setProcessed (false);
// N
        } */
    }

    /** Load Constructor */
    public X_JP_ToDo_Team (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
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
      StringBuffer sb = new StringBuffer ("X_JP_ToDo_Team[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Table)MTable.get(getCtx(), org.compiere.model.I_AD_Table.Table_Name)
			.getPO(getAD_Table_ID(), get_TrxName());	}

	/** Set Table.
		@param AD_Table_ID 
		Database Table information
	  */
	public void setAD_Table_ID (int AD_Table_ID)
	{
		if (AD_Table_ID < 1) 
			set_Value (COLUMNNAME_AD_Table_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Table_ID, Integer.valueOf(AD_Table_ID));
	}

	/** Get Table.
		@return Database Table information
	  */
	public int getAD_Table_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Table_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ProjectPhase getC_ProjectPhase() throws RuntimeException
    {
		return (org.compiere.model.I_C_ProjectPhase)MTable.get(getCtx(), org.compiere.model.I_C_ProjectPhase.Table_Name)
			.getPO(getC_ProjectPhase_ID(), get_TrxName());	}

	/** Set Project Phase.
		@param C_ProjectPhase_ID 
		Phase of a Project
	  */
	public void setC_ProjectPhase_ID (int C_ProjectPhase_ID)
	{
		if (C_ProjectPhase_ID < 1) 
			set_Value (COLUMNNAME_C_ProjectPhase_ID, null);
		else 
			set_Value (COLUMNNAME_C_ProjectPhase_ID, Integer.valueOf(C_ProjectPhase_ID));
	}

	/** Get Project Phase.
		@return Phase of a Project
	  */
	public int getC_ProjectPhase_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ProjectPhase_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ProjectTask getC_ProjectTask() throws RuntimeException
    {
		return (org.compiere.model.I_C_ProjectTask)MTable.get(getCtx(), org.compiere.model.I_C_ProjectTask.Table_Name)
			.getPO(getC_ProjectTask_ID(), get_TrxName());	}

	/** Set Project Task.
		@param C_ProjectTask_ID 
		Actual Project Task in a Phase
	  */
	public void setC_ProjectTask_ID (int C_ProjectTask_ID)
	{
		if (C_ProjectTask_ID < 1) 
			set_Value (COLUMNNAME_C_ProjectTask_ID, null);
		else 
			set_Value (COLUMNNAME_C_ProjectTask_ID, Integer.valueOf(C_ProjectTask_ID));
	}

	/** Get Project Task.
		@return Actual Project Task in a Phase
	  */
	public int getC_ProjectTask_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ProjectTask_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Project getC_Project() throws RuntimeException
    {
		return (org.compiere.model.I_C_Project)MTable.get(getCtx(), org.compiere.model.I_C_Project.Table_Name)
			.getPO(getC_Project_ID(), get_TrxName());	}

	/** Set Project.
		@param C_Project_ID 
		Financial Project
	  */
	public void setC_Project_ID (int C_Project_ID)
	{
		if (C_Project_ID < 1) 
			set_Value (COLUMNNAME_C_Project_ID, null);
		else 
			set_Value (COLUMNNAME_C_Project_ID, Integer.valueOf(C_Project_ID));
	}

	/** Get Project.
		@return Financial Project
	  */
	public int getC_Project_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Project_ID);
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

	/** Set Process Now.
		@param JP_Processing1 Process Now	  */
	public void setJP_Processing1 (String JP_Processing1)
	{
		set_Value (COLUMNNAME_JP_Processing1, JP_Processing1);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing1 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing1);
	}

	/** Set Process Now.
		@param JP_Processing2 Process Now	  */
	public void setJP_Processing2 (String JP_Processing2)
	{
		set_Value (COLUMNNAME_JP_Processing2, JP_Processing2);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing2 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing2);
	}

	/** Set Process Now.
		@param JP_Processing3 Process Now	  */
	public void setJP_Processing3 (String JP_Processing3)
	{
		set_Value (COLUMNNAME_JP_Processing3, JP_Processing3);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing3 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing3);
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
			set_Value (COLUMNNAME_JP_Team_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Team_ID, Integer.valueOf(JP_Team_ID));
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

	public I_JP_ToDo_Category getJP_ToDo_Category() throws RuntimeException
    {
		return (I_JP_ToDo_Category)MTable.get(getCtx(), I_JP_ToDo_Category.Table_Name)
			.getPO(getJP_ToDo_Category_ID(), get_TrxName());	}

	/** Set ToDo Category.
		@param JP_ToDo_Category_ID ToDo Category	  */
	public void setJP_ToDo_Category_ID (int JP_ToDo_Category_ID)
	{
		if (JP_ToDo_Category_ID < 1) 
			set_Value (COLUMNNAME_JP_ToDo_Category_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ToDo_Category_ID, Integer.valueOf(JP_ToDo_Category_ID));
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

	/** Set End Time.
		@param JP_ToDo_EndTime End Time	  */
	public void setJP_ToDo_EndTime (Timestamp JP_ToDo_EndTime)
	{
		set_Value (COLUMNNAME_JP_ToDo_EndTime, JP_ToDo_EndTime);
	}

	/** Get End Time.
		@return End Time	  */
	public Timestamp getJP_ToDo_EndTime () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ToDo_EndTime);
	}

	/** Set Scheduled End Time.
		@param JP_ToDo_ScheduledEndTime Scheduled End Time	  */
	public void setJP_ToDo_ScheduledEndTime (Timestamp JP_ToDo_ScheduledEndTime)
	{
		set_Value (COLUMNNAME_JP_ToDo_ScheduledEndTime, JP_ToDo_ScheduledEndTime);
	}

	/** Get Scheduled End Time.
		@return Scheduled End Time	  */
	public Timestamp getJP_ToDo_ScheduledEndTime () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ToDo_ScheduledEndTime);
	}

	/** Set Scheduled Start Time.
		@param JP_ToDo_ScheduledStartTime Scheduled Start Time	  */
	public void setJP_ToDo_ScheduledStartTime (Timestamp JP_ToDo_ScheduledStartTime)
	{
		set_Value (COLUMNNAME_JP_ToDo_ScheduledStartTime, JP_ToDo_ScheduledStartTime);
	}

	/** Get Scheduled Start Time.
		@return Scheduled Start Time	  */
	public Timestamp getJP_ToDo_ScheduledStartTime () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ToDo_ScheduledStartTime);
	}

	/** Set Start Time.
		@param JP_ToDo_StartTime Start Time	  */
	public void setJP_ToDo_StartTime (Timestamp JP_ToDo_StartTime)
	{
		set_Value (COLUMNNAME_JP_ToDo_StartTime, JP_ToDo_StartTime);
	}

	/** Get Start Time.
		@return Start Time	  */
	public Timestamp getJP_ToDo_StartTime () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ToDo_StartTime);
	}

	/** Not yet started = NY */
	public static final String JP_TODO_STATUS_NotYetStarted = "NY";
	/** Work in progress = WP */
	public static final String JP_TODO_STATUS_WorkInProgress = "WP";
	/** Completed = CO */
	public static final String JP_TODO_STATUS_Completed = "CO";
	/** Set ToDo Status.
		@param JP_ToDo_Status ToDo Status	  */
	public void setJP_ToDo_Status (String JP_ToDo_Status)
	{

		set_Value (COLUMNNAME_JP_ToDo_Status, JP_ToDo_Status);
	}

	/** Get ToDo Status.
		@return ToDo Status	  */
	public String getJP_ToDo_Status () 
	{
		return (String)get_Value(COLUMNNAME_JP_ToDo_Status);
	}

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

	public I_JP_ToDo_Team getJP_ToDo_Team_Related() throws RuntimeException
    {
		return (I_JP_ToDo_Team)MTable.get(getCtx(), I_JP_ToDo_Team.Table_Name)
			.getPO(getJP_ToDo_Team_Related_ID(), get_TrxName());	}

	/** Set Releated Team ToDo.
		@param JP_ToDo_Team_Related_ID Releated Team ToDo	  */
	public void setJP_ToDo_Team_Related_ID (int JP_ToDo_Team_Related_ID)
	{
		if (JP_ToDo_Team_Related_ID < 1) 
			set_Value (COLUMNNAME_JP_ToDo_Team_Related_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ToDo_Team_Related_ID, Integer.valueOf(JP_ToDo_Team_Related_ID));
	}

	/** Get Releated Team ToDo.
		@return Releated Team ToDo	  */
	public int getJP_ToDo_Team_Related_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ToDo_Team_Related_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_ToDo_Team_UU.
		@param JP_ToDo_Team_UU JP_ToDo_Team_UU	  */
	public void setJP_ToDo_Team_UU (String JP_ToDo_Team_UU)
	{
		set_Value (COLUMNNAME_JP_ToDo_Team_UU, JP_ToDo_Team_UU);
	}

	/** Get JP_ToDo_Team_UU.
		@return JP_ToDo_Team_UU	  */
	public String getJP_ToDo_Team_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ToDo_Team_UU);
	}

	/** ToDo = T */
	public static final String JP_TODO_TYPE_ToDo = "T";
	/** Schedule = S */
	public static final String JP_TODO_TYPE_Schedule = "S";
	/** Set ToDo Type.
		@param JP_ToDo_Type ToDo Type	  */
	public void setJP_ToDo_Type (String JP_ToDo_Type)
	{

		set_Value (COLUMNNAME_JP_ToDo_Type, JP_ToDo_Type);
	}

	/** Get ToDo Type.
		@return ToDo Type	  */
	public String getJP_ToDo_Type () 
	{
		return (String)get_Value(COLUMNNAME_JP_ToDo_Type);
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

	/** Set Record ID.
		@param Record_ID 
		Direct internal record ID
	  */
	public void setRecord_ID (int Record_ID)
	{
		if (Record_ID < 0) 
			set_ValueNoCheck (COLUMNNAME_Record_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Record_ID, Integer.valueOf(Record_ID));
	}

	/** Get Record ID.
		@return Direct internal record ID
	  */
	public int getRecord_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Record_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}