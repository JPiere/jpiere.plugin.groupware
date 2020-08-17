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

/** Generated Model for JP_GroupwareUser
 *  @author iDempiere (generated) 
 *  @version Release 7.1 - $Id$ */
public class X_JP_GroupwareUser extends PO implements I_JP_GroupwareUser, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200817L;

    /** Standard Constructor */
    public X_JP_GroupwareUser (Properties ctx, int JP_GroupwareUser_ID, String trxName)
    {
      super (ctx, JP_GroupwareUser_ID, trxName);
      /** if (JP_GroupwareUser_ID == 0)
        {
			setAD_Tree_Menu_ID (0);
			setAD_User_ID (0);
// null
			setIsDisplayScheduleJP (true);
// Y
			setIsDisplayTaskJP (false);
// N
			setJP_GroupwareUser_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_GroupwareUser (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_GroupwareUser[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_Tree getAD_Tree_Menu() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Tree)MTable.get(getCtx(), org.compiere.model.I_AD_Tree.Table_Name)
			.getPO(getAD_Tree_Menu_ID(), get_TrxName());	}

	/** Set Menu Tree.
		@param AD_Tree_Menu_ID 
		Tree of the menu
	  */
	public void setAD_Tree_Menu_ID (int AD_Tree_Menu_ID)
	{
		if (AD_Tree_Menu_ID < 1) 
			set_Value (COLUMNNAME_AD_Tree_Menu_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Tree_Menu_ID, Integer.valueOf(AD_Tree_Menu_ID));
	}

	/** Get Menu Tree.
		@return Tree of the menu
	  */
	public int getAD_Tree_Menu_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Tree_Menu_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Display Schedule.
		@param IsDisplayScheduleJP Display Schedule	  */
	public void setIsDisplayScheduleJP (boolean IsDisplayScheduleJP)
	{
		set_Value (COLUMNNAME_IsDisplayScheduleJP, Boolean.valueOf(IsDisplayScheduleJP));
	}

	/** Get Display Schedule.
		@return Display Schedule	  */
	public boolean isDisplayScheduleJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsDisplayScheduleJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Display Task.
		@param IsDisplayTaskJP Display Task	  */
	public void setIsDisplayTaskJP (boolean IsDisplayTaskJP)
	{
		set_Value (COLUMNNAME_IsDisplayTaskJP, Boolean.valueOf(IsDisplayTaskJP));
	}

	/** Get Display Task.
		@return Display Task	  */
	public boolean isDisplayTaskJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsDisplayTaskJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
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

	/** JP_FirstDayOfWeek AD_Reference_ID=167 */
	public static final int JP_FIRSTDAYOFWEEK_AD_Reference_ID=167;
	/** Sunday = 7 */
	public static final String JP_FIRSTDAYOFWEEK_Sunday = "7";
	/** Monday = 1 */
	public static final String JP_FIRSTDAYOFWEEK_Monday = "1";
	/** Tuesday = 2 */
	public static final String JP_FIRSTDAYOFWEEK_Tuesday = "2";
	/** Wednesday = 3 */
	public static final String JP_FIRSTDAYOFWEEK_Wednesday = "3";
	/** Thursday = 4 */
	public static final String JP_FIRSTDAYOFWEEK_Thursday = "4";
	/** Friday = 5 */
	public static final String JP_FIRSTDAYOFWEEK_Friday = "5";
	/** Saturday = 6 */
	public static final String JP_FIRSTDAYOFWEEK_Saturday = "6";
	/** Set First Day of Week.
		@param JP_FirstDayOfWeek First Day of Week	  */
	public void setJP_FirstDayOfWeek (String JP_FirstDayOfWeek)
	{

		set_Value (COLUMNNAME_JP_FirstDayOfWeek, JP_FirstDayOfWeek);
	}

	/** Get First Day of Week.
		@return First Day of Week	  */
	public String getJP_FirstDayOfWeek () 
	{
		return (String)get_Value(COLUMNNAME_JP_FirstDayOfWeek);
	}

	/** Set Groupware User Preference.
		@param JP_GroupwareUser_ID Groupware User Preference	  */
	public void setJP_GroupwareUser_ID (int JP_GroupwareUser_ID)
	{
		if (JP_GroupwareUser_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_GroupwareUser_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_GroupwareUser_ID, Integer.valueOf(JP_GroupwareUser_ID));
	}

	/** Get Groupware User Preference.
		@return Groupware User Preference	  */
	public int getJP_GroupwareUser_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_GroupwareUser_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_GroupwareUser_UU.
		@param JP_GroupwareUser_UU JP_GroupwareUser_UU	  */
	public void setJP_GroupwareUser_UU (String JP_GroupwareUser_UU)
	{
		set_Value (COLUMNNAME_JP_GroupwareUser_UU, JP_GroupwareUser_UU);
	}

	/** Get JP_GroupwareUser_UU.
		@return JP_GroupwareUser_UU	  */
	public String getJP_GroupwareUser_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_GroupwareUser_UU);
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

	/** Completed = CO */
	public static final String JP_TODO_STATUS_Completed = "CO";
	/** Not yet started = NY */
	public static final String JP_TODO_STATUS_NotYetStarted = "NY";
	/** Work in progress = WP */
	public static final String JP_TODO_STATUS_WorkInProgress = "WP";
	/** Not Completed = NC */
	public static final String JP_TODO_STATUS_NotCompleted = "NC";
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
}