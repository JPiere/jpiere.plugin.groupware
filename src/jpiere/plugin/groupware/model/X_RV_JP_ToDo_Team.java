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

/** Generated Model for RV_JP_ToDo_Team
 *  @author iDempiere (generated) 
 *  @version Release 7.1 - $Id$ */
public class X_RV_JP_ToDo_Team extends PO implements I_RV_JP_ToDo_Team, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200822L;

    /** Standard Constructor */
    public X_RV_JP_ToDo_Team (Properties ctx, int RV_JP_ToDo_Team_ID, String trxName)
    {
      super (ctx, RV_JP_ToDo_Team_ID, trxName);
      /** if (RV_JP_ToDo_Team_ID == 0)
        {
        } */
    }

    /** Load Constructor */
    public X_RV_JP_ToDo_Team (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_RV_JP_ToDo_Team[")
        .append(get_ID()).append("]");
      return sb.toString();
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
			set_ValueNoCheck (COLUMNNAME_C_ProjectPhase_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_ProjectPhase_ID, Integer.valueOf(C_ProjectPhase_ID));
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
			set_ValueNoCheck (COLUMNNAME_C_ProjectTask_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_ProjectTask_ID, Integer.valueOf(C_ProjectTask_ID));
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
			set_ValueNoCheck (COLUMNNAME_C_Project_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Project_ID, Integer.valueOf(C_Project_ID));
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

	/** Set Open ToDo.
		@param IsOpenToDoJP Open ToDo	  */
	public void setIsOpenToDoJP (boolean IsOpenToDoJP)
	{
		set_Value (COLUMNNAME_IsOpenToDoJP, Boolean.valueOf(IsOpenToDoJP));
	}

	/** Get Open ToDo.
		@return Open ToDo	  */
	public boolean isOpenToDoJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsOpenToDoJP);
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

	public I_JP_ToDo_Category getJP_ToDo_Category() throws RuntimeException
    {
		return (I_JP_ToDo_Category)MTable.get(getCtx(), I_JP_ToDo_Category.Table_Name)
			.getPO(getJP_ToDo_Category_ID(), get_TrxName());	}

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

	/** Not yet started = NY */
	public static final String JP_TODO_PERSONAL_STATUS_NotYetStarted = "NY";
	/** Work in progress = WP */
	public static final String JP_TODO_PERSONAL_STATUS_WorkInProgress = "WP";
	/** Completed = CO */
	public static final String JP_TODO_PERSONAL_STATUS_Completed = "CO";
	/** Set Personal ToDo Status.
		@param JP_ToDo_Personal_Status Personal ToDo Status	  */
	public void setJP_ToDo_Personal_Status (String JP_ToDo_Personal_Status)
	{

		set_Value (COLUMNNAME_JP_ToDo_Personal_Status, JP_ToDo_Personal_Status);
	}

	/** Get Personal ToDo Status.
		@return Personal ToDo Status	  */
	public String getJP_ToDo_Personal_Status () 
	{
		return (String)get_Value(COLUMNNAME_JP_ToDo_Personal_Status);
	}

	public org.compiere.model.I_AD_User getJP_ToDo_Personal_User() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getJP_ToDo_Personal_User_ID(), get_TrxName());	}

	/** Set Personal ToDo User.
		@param JP_ToDo_Personal_User_ID Personal ToDo User	  */
	public void setJP_ToDo_Personal_User_ID (int JP_ToDo_Personal_User_ID)
	{
		if (JP_ToDo_Personal_User_ID < 1) 
			set_Value (COLUMNNAME_JP_ToDo_Personal_User_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ToDo_Personal_User_ID, Integer.valueOf(JP_ToDo_Personal_User_ID));
	}

	/** Get Personal ToDo User.
		@return Personal ToDo User	  */
	public int getJP_ToDo_Personal_User_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ToDo_Personal_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Not yet started = NY */
	public static final String JP_TODO_TEAM_STATUS_NotYetStarted = "NY";
	/** Work in progress = WP */
	public static final String JP_TODO_TEAM_STATUS_WorkInProgress = "WP";
	/** Completed = CO */
	public static final String JP_TODO_TEAM_STATUS_Completed = "CO";
	/** Set Team ToDo Status.
		@param JP_ToDo_Team_Status Team ToDo Status	  */
	public void setJP_ToDo_Team_Status (String JP_ToDo_Team_Status)
	{

		set_Value (COLUMNNAME_JP_ToDo_Team_Status, JP_ToDo_Team_Status);
	}

	/** Get Team ToDo Status.
		@return Team ToDo Status	  */
	public String getJP_ToDo_Team_Status () 
	{
		return (String)get_Value(COLUMNNAME_JP_ToDo_Team_Status);
	}

	public org.compiere.model.I_AD_User getJP_ToDo_Team_User() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getJP_ToDo_Team_User_ID(), get_TrxName());	}

	/** Set Team ToDo User.
		@param JP_ToDo_Team_User_ID Team ToDo User	  */
	public void setJP_ToDo_Team_User_ID (int JP_ToDo_Team_User_ID)
	{
		if (JP_ToDo_Team_User_ID < 1) 
			set_Value (COLUMNNAME_JP_ToDo_Team_User_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ToDo_Team_User_ID, Integer.valueOf(JP_ToDo_Team_User_ID));
	}

	/** Get Team ToDo User.
		@return Team ToDo User	  */
	public int getJP_ToDo_Team_User_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ToDo_Team_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Task = T */
	public static final String JP_TODO_TYPE_Task = "T";
	/** Schedule = S */
	public static final String JP_TODO_TYPE_Schedule = "S";
	/** Memo = M */
	public static final String JP_TODO_TYPE_Memo = "M";
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

	/** Set Team ToDo.
		@param RV_JP_ToDo_Team_ID Team ToDo	  */
	public void setRV_JP_ToDo_Team_ID (int RV_JP_ToDo_Team_ID)
	{
		if (RV_JP_ToDo_Team_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_RV_JP_ToDo_Team_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_RV_JP_ToDo_Team_ID, Integer.valueOf(RV_JP_ToDo_Team_ID));
	}

	/** Get Team ToDo.
		@return Team ToDo	  */
	public int getRV_JP_ToDo_Team_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_RV_JP_ToDo_Team_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}