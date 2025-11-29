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
 *  @version Release 12 - $Id$ */
@org.adempiere.base.Model(table="JP_GroupwareUser")
public class X_JP_GroupwareUser extends PO implements I_JP_GroupwareUser, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20251129L;

    /** Standard Constructor */
    public X_JP_GroupwareUser (Properties ctx, int JP_GroupwareUser_ID, String trxName)
    {
      super (ctx, JP_GroupwareUser_ID, trxName);
      /** if (JP_GroupwareUser_ID == 0)
        {
			setAD_Tree_Menu_ID (0);
			setAD_User_ID (0);
// null
			setIsDisplayNonBusinessDayJP (true);
// Y
			setIsDisplayScheduleJP (true);
// Y
			setIsDisplayTaskJP (false);
// N
			setIsToDoMouseoverPopupJP (true);
// Y
			setJP_Add_Hours (0);
// 5
			setJP_Add_Mins (0);
// 15
			setJP_GroupwareUser_ID (0);
			setJP_ToDo_Calendar (null);
// P
			setJP_ToDo_Calendar_BeginTime (0);
// 0
			setJP_ToDo_Calendar_EndTime (0);
// 24
			setJP_ToDo_Main_Calendar (null);
// T
        } */
    }

    /** Standard Constructor */
    public X_JP_GroupwareUser (Properties ctx, int JP_GroupwareUser_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_GroupwareUser_ID, trxName, virtualColumns);
      /** if (JP_GroupwareUser_ID == 0)
        {
			setAD_Tree_Menu_ID (0);
			setAD_User_ID (0);
// null
			setIsDisplayNonBusinessDayJP (true);
// Y
			setIsDisplayScheduleJP (true);
// Y
			setIsDisplayTaskJP (false);
// N
			setIsToDoMouseoverPopupJP (true);
// Y
			setJP_Add_Hours (0);
// 5
			setJP_Add_Mins (0);
// 15
			setJP_GroupwareUser_ID (0);
			setJP_ToDo_Calendar (null);
// P
			setJP_ToDo_Calendar_BeginTime (0);
// 0
			setJP_ToDo_Calendar_EndTime (0);
// 24
			setJP_ToDo_Main_Calendar (null);
// T
        } */
    }

    /** Standard Constructor */
    public X_JP_GroupwareUser (Properties ctx, String JP_GroupwareUser_UU, String trxName)
    {
      super (ctx, JP_GroupwareUser_UU, trxName);
      /** if (JP_GroupwareUser_UU == null)
        {
			setAD_Tree_Menu_ID (0);
			setAD_User_ID (0);
// null
			setIsDisplayNonBusinessDayJP (true);
// Y
			setIsDisplayScheduleJP (true);
// Y
			setIsDisplayTaskJP (false);
// N
			setIsToDoMouseoverPopupJP (true);
// Y
			setJP_Add_Hours (0);
// 5
			setJP_Add_Mins (0);
// 15
			setJP_GroupwareUser_ID (0);
			setJP_ToDo_Calendar (null);
// P
			setJP_ToDo_Calendar_BeginTime (0);
// 0
			setJP_ToDo_Calendar_EndTime (0);
// 24
			setJP_ToDo_Main_Calendar (null);
// T
        } */
    }

    /** Standard Constructor */
    public X_JP_GroupwareUser (Properties ctx, String JP_GroupwareUser_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_GroupwareUser_UU, trxName, virtualColumns);
      /** if (JP_GroupwareUser_UU == null)
        {
			setAD_Tree_Menu_ID (0);
			setAD_User_ID (0);
// null
			setIsDisplayNonBusinessDayJP (true);
// Y
			setIsDisplayScheduleJP (true);
// Y
			setIsDisplayTaskJP (false);
// N
			setIsToDoMouseoverPopupJP (true);
// Y
			setJP_Add_Hours (0);
// 5
			setJP_Add_Mins (0);
// 15
			setJP_GroupwareUser_ID (0);
			setJP_ToDo_Calendar (null);
// P
			setJP_ToDo_Calendar_BeginTime (0);
// 0
			setJP_ToDo_Calendar_EndTime (0);
// 24
			setJP_ToDo_Main_Calendar (null);
// T
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
      StringBuilder sb = new StringBuilder ("X_JP_GroupwareUser[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_Tree getAD_Tree_Menu() throws RuntimeException
	{
		return (org.compiere.model.I_AD_Tree)MTable.get(getCtx(), org.compiere.model.I_AD_Tree.Table_ID)
			.getPO(getAD_Tree_Menu_ID(), get_TrxName());
	}

	/** Set Menu Tree.
		@param AD_Tree_Menu_ID Tree of the menu
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
	public int getAD_Tree_Menu_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Tree_Menu_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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
			set_ValueNoCheck (COLUMNNAME_AD_User_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
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

	public org.compiere.model.I_C_Country getC_Country() throws RuntimeException
	{
		return (org.compiere.model.I_C_Country)MTable.get(getCtx(), org.compiere.model.I_C_Country.Table_ID)
			.getPO(getC_Country_ID(), get_TrxName());
	}

	/** Set Country.
		@param C_Country_ID Country 
	*/
	public void setC_Country_ID (int C_Country_ID)
	{
		if (C_Country_ID < 1)
			set_Value (COLUMNNAME_C_Country_ID, null);
		else
			set_Value (COLUMNNAME_C_Country_ID, Integer.valueOf(C_Country_ID));
	}

	/** Get Country.
		@return Country 
	  */
	public int getC_Country_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Country_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ProjectPhase getC_ProjectPhase() throws RuntimeException
	{
		return (org.compiere.model.I_C_ProjectPhase)MTable.get(getCtx(), org.compiere.model.I_C_ProjectPhase.Table_ID)
			.getPO(getC_ProjectPhase_ID(), get_TrxName());
	}

	/** Set Project Phase.
		@param C_ProjectPhase_ID Phase of a Project
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
	public int getC_ProjectPhase_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ProjectPhase_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ProjectTask getC_ProjectTask() throws RuntimeException
	{
		return (org.compiere.model.I_C_ProjectTask)MTable.get(getCtx(), org.compiere.model.I_C_ProjectTask.Table_ID)
			.getPO(getC_ProjectTask_ID(), get_TrxName());
	}

	/** Set Project Task.
		@param C_ProjectTask_ID Actual Project Task in a Phase
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
	public int getC_ProjectTask_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ProjectTask_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Project getC_Project() throws RuntimeException
	{
		return (org.compiere.model.I_C_Project)MTable.get(getCtx(), org.compiere.model.I_C_Project.Table_ID)
			.getPO(getC_Project_ID(), get_TrxName());
	}

	/** Set Project.
		@param C_Project_ID Financial Project
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
	public int getC_Project_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Project_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Display Nonbusiness Day.
		@param IsDisplayNonBusinessDayJP Display Nonbusiness Day
	*/
	public void setIsDisplayNonBusinessDayJP (boolean IsDisplayNonBusinessDayJP)
	{
		set_Value (COLUMNNAME_IsDisplayNonBusinessDayJP, Boolean.valueOf(IsDisplayNonBusinessDayJP));
	}

	/** Get Display Nonbusiness Day.
		@return Display Nonbusiness Day	  */
	public boolean isDisplayNonBusinessDayJP()
	{
		Object oo = get_Value(COLUMNNAME_IsDisplayNonBusinessDayJP);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Display Schedule.
		@param IsDisplayScheduleJP Display Schedule
	*/
	public void setIsDisplayScheduleJP (boolean IsDisplayScheduleJP)
	{
		set_Value (COLUMNNAME_IsDisplayScheduleJP, Boolean.valueOf(IsDisplayScheduleJP));
	}

	/** Get Display Schedule.
		@return Display Schedule	  */
	public boolean isDisplayScheduleJP()
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
		@param IsDisplayTaskJP Display Task
	*/
	public void setIsDisplayTaskJP (boolean IsDisplayTaskJP)
	{
		set_Value (COLUMNNAME_IsDisplayTaskJP, Boolean.valueOf(IsDisplayTaskJP));
	}

	/** Get Display Task.
		@return Display Task	  */
	public boolean isDisplayTaskJP()
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

	/** Set Show ToDo Mouseover Popup.
		@param IsToDoMouseoverPopupJP Show ToDo Mouseover Popup
	*/
	public void setIsToDoMouseoverPopupJP (boolean IsToDoMouseoverPopupJP)
	{
		set_Value (COLUMNNAME_IsToDoMouseoverPopupJP, Boolean.valueOf(IsToDoMouseoverPopupJP));
	}

	/** Get Show ToDo Mouseover Popup.
		@return Show ToDo Mouseover Popup	  */
	public boolean isToDoMouseoverPopupJP()
	{
		Object oo = get_Value(COLUMNNAME_IsToDoMouseoverPopupJP);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Hours to Add.
		@param JP_Add_Hours Hours to Add
	*/
	public void setJP_Add_Hours (int JP_Add_Hours)
	{
		set_Value (COLUMNNAME_JP_Add_Hours, Integer.valueOf(JP_Add_Hours));
	}

	/** Get Hours to Add.
		@return Hours to Add	  */
	public int getJP_Add_Hours()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Add_Hours);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Mins to Add.
		@param JP_Add_Mins Mins to Add
	*/
	public void setJP_Add_Mins (int JP_Add_Mins)
	{
		set_Value (COLUMNNAME_JP_Add_Mins, Integer.valueOf(JP_Add_Mins));
	}

	/** Get Mins to Add.
		@return Mins to Add	  */
	public int getJP_Add_Mins()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Add_Mins);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Color Picker.
		@param JP_ColorPicker Color Picker
	*/
	public void setJP_ColorPicker (String JP_ColorPicker)
	{
		set_Value (COLUMNNAME_JP_ColorPicker, JP_ColorPicker);
	}

	/** Get Color Picker.
		@return Color Picker	  */
	public String getJP_ColorPicker()
	{
		return (String)get_Value(COLUMNNAME_JP_ColorPicker);
	}

	/** Set Color Picker2.
		@param JP_ColorPicker2 Color Picker2
	*/
	public void setJP_ColorPicker2 (String JP_ColorPicker2)
	{
		set_Value (COLUMNNAME_JP_ColorPicker2, JP_ColorPicker2);
	}

	/** Get Color Picker2.
		@return Color Picker2	  */
	public String getJP_ColorPicker2()
	{
		return (String)get_Value(COLUMNNAME_JP_ColorPicker2);
	}

	/** Day = 01 */
	public static final String JP_DEFAULTCALENDARVIEW_Day = "01";
	/** Five Days = 05 */
	public static final String JP_DEFAULTCALENDARVIEW_FiveDays = "05";
	/** Week = 07 */
	public static final String JP_DEFAULTCALENDARVIEW_Week = "07";
	/** Month = 31 */
	public static final String JP_DEFAULTCALENDARVIEW_Month = "31";
	/** Set Default Calendar View.
		@param JP_DefaultCalendarView Default Calendar View
	*/
	public void setJP_DefaultCalendarView (String JP_DefaultCalendarView)
	{

		set_Value (COLUMNNAME_JP_DefaultCalendarView, JP_DefaultCalendarView);
	}

	/** Get Default Calendar View.
		@return Default Calendar View	  */
	public String getJP_DefaultCalendarView()
	{
		return (String)get_Value(COLUMNNAME_JP_DefaultCalendarView);
	}

	/** JP_FirstDayOfWeek AD_Reference_ID=167 */
	public static final int JP_FIRSTDAYOFWEEK_AD_Reference_ID=167;
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
	/** Sunday = 7 */
	public static final String JP_FIRSTDAYOFWEEK_Sunday = "7";
	/** Set First Day of Week.
		@param JP_FirstDayOfWeek First Day of Week
	*/
	public void setJP_FirstDayOfWeek (String JP_FirstDayOfWeek)
	{

		set_Value (COLUMNNAME_JP_FirstDayOfWeek, JP_FirstDayOfWeek);
	}

	/** Get First Day of Week.
		@return First Day of Week	  */
	public String getJP_FirstDayOfWeek()
	{
		return (String)get_Value(COLUMNNAME_JP_FirstDayOfWeek);
	}

	/** Set Groupware User Preference.
		@param JP_GroupwareUser_ID Groupware User Preference
	*/
	public void setJP_GroupwareUser_ID (int JP_GroupwareUser_ID)
	{
		if (JP_GroupwareUser_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_GroupwareUser_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_GroupwareUser_ID, Integer.valueOf(JP_GroupwareUser_ID));
	}

	/** Get Groupware User Preference.
		@return Groupware User Preference	  */
	public int getJP_GroupwareUser_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_GroupwareUser_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_GroupwareUser_UU.
		@param JP_GroupwareUser_UU JP_GroupwareUser_UU
	*/
	public void setJP_GroupwareUser_UU (String JP_GroupwareUser_UU)
	{
		set_Value (COLUMNNAME_JP_GroupwareUser_UU, JP_GroupwareUser_UU);
	}

	/** Get JP_GroupwareUser_UU.
		@return JP_GroupwareUser_UU	  */
	public String getJP_GroupwareUser_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_GroupwareUser_UU);
	}

	public org.compiere.model.I_C_Calendar getJP_NonBusinessDayCalendar() throws RuntimeException
	{
		return (org.compiere.model.I_C_Calendar)MTable.get(getCtx(), org.compiere.model.I_C_Calendar.Table_ID)
			.getPO(getJP_NonBusinessDayCalendar_ID(), get_TrxName());
	}

	/** Set Nonbusiness Day Calendar.
		@param JP_NonBusinessDayCalendar_ID Nonbusiness Day Calendar
	*/
	public void setJP_NonBusinessDayCalendar_ID (int JP_NonBusinessDayCalendar_ID)
	{
		if (JP_NonBusinessDayCalendar_ID < 1)
			set_Value (COLUMNNAME_JP_NonBusinessDayCalendar_ID, null);
		else
			set_Value (COLUMNNAME_JP_NonBusinessDayCalendar_ID, Integer.valueOf(JP_NonBusinessDayCalendar_ID));
	}

	/** Get Nonbusiness Day Calendar.
		@return Nonbusiness Day Calendar	  */
	public int getJP_NonBusinessDayCalendar_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_NonBusinessDayCalendar_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Nonbusiness Day Color.
		@param JP_NonBusinessDayColor Nonbusiness Day Color
	*/
	public void setJP_NonBusinessDayColor (String JP_NonBusinessDayColor)
	{
		set_Value (COLUMNNAME_JP_NonBusinessDayColor, JP_NonBusinessDayColor);
	}

	/** Get Nonbusiness Day Color.
		@return Nonbusiness Day Color	  */
	public String getJP_NonBusinessDayColor()
	{
		return (String)get_Value(COLUMNNAME_JP_NonBusinessDayColor);
	}

	public I_JP_Team getJP_Team() throws RuntimeException
	{
		return (I_JP_Team)MTable.get(getCtx(), I_JP_Team.Table_ID)
			.getPO(getJP_Team_ID(), get_TrxName());
	}

	/** Set Team.
		@param JP_Team_ID Team
	*/
	public void setJP_Team_ID (int JP_Team_ID)
	{
		if (JP_Team_ID < 1)
			set_Value (COLUMNNAME_JP_Team_ID, null);
		else
			set_Value (COLUMNNAME_JP_Team_ID, Integer.valueOf(JP_Team_ID));
	}

	/** Get Team.
		@return Team	  */
	public int getJP_Team_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Team_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Personal ToDo = P */
	public static final String JP_TODO_CALENDAR_PersonalToDo = "P";
	/** Team ToDo = T */
	public static final String JP_TODO_CALENDAR_TeamToDo = "T";
	/** Set ToDo Calendar.
		@param JP_ToDo_Calendar ToDo Calendar
	*/
	public void setJP_ToDo_Calendar (String JP_ToDo_Calendar)
	{

		set_Value (COLUMNNAME_JP_ToDo_Calendar, JP_ToDo_Calendar);
	}

	/** Get ToDo Calendar.
		@return ToDo Calendar	  */
	public String getJP_ToDo_Calendar()
	{
		return (String)get_Value(COLUMNNAME_JP_ToDo_Calendar);
	}

	/** Set ToDo Calendar Begin Time.
		@param JP_ToDo_Calendar_BeginTime ToDo Calendar Begin Time
	*/
	public void setJP_ToDo_Calendar_BeginTime (int JP_ToDo_Calendar_BeginTime)
	{
		set_Value (COLUMNNAME_JP_ToDo_Calendar_BeginTime, Integer.valueOf(JP_ToDo_Calendar_BeginTime));
	}

	/** Get ToDo Calendar Begin Time.
		@return ToDo Calendar Begin Time	  */
	public int getJP_ToDo_Calendar_BeginTime()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ToDo_Calendar_BeginTime);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set ToDo Calendar End Time.
		@param JP_ToDo_Calendar_EndTime ToDo Calendar End Time
	*/
	public void setJP_ToDo_Calendar_EndTime (int JP_ToDo_Calendar_EndTime)
	{
		set_Value (COLUMNNAME_JP_ToDo_Calendar_EndTime, Integer.valueOf(JP_ToDo_Calendar_EndTime));
	}

	/** Get ToDo Calendar End Time.
		@return ToDo Calendar End Time	  */
	public int getJP_ToDo_Calendar_EndTime()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ToDo_Calendar_EndTime);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ToDo_Category getJP_ToDo_Category() throws RuntimeException
	{
		return (I_JP_ToDo_Category)MTable.get(getCtx(), I_JP_ToDo_Category.Table_ID)
			.getPO(getJP_ToDo_Category_ID(), get_TrxName());
	}

	/** Set ToDo Category.
		@param JP_ToDo_Category_ID ToDo Category
	*/
	public void setJP_ToDo_Category_ID (int JP_ToDo_Category_ID)
	{
		if (JP_ToDo_Category_ID < 1)
			set_Value (COLUMNNAME_JP_ToDo_Category_ID, null);
		else
			set_Value (COLUMNNAME_JP_ToDo_Category_ID, Integer.valueOf(JP_ToDo_Category_ID));
	}

	/** Get ToDo Category.
		@return ToDo Category	  */
	public int getJP_ToDo_Category_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ToDo_Category_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Include Team member&#039;s ToDo = T */
	public static final String JP_TODO_MAIN_CALENDAR_IncludeTeamMemberSToDo = "T";
	/** User &#039;s ToDo Only = U */
	public static final String JP_TODO_MAIN_CALENDAR_UserSToDoOnly = "U";
	/** Set Main Calendar&#039;s ToDo .
		@param JP_ToDo_Main_Calendar Main Calendar&#039;s ToDo 
	*/
	public void setJP_ToDo_Main_Calendar (String JP_ToDo_Main_Calendar)
	{

		set_Value (COLUMNNAME_JP_ToDo_Main_Calendar, JP_ToDo_Main_Calendar);
	}

	/** Get Main Calendar&#039;s ToDo .
		@return Main Calendar&#039;s ToDo 	  */
	public String getJP_ToDo_Main_Calendar()
	{
		return (String)get_Value(COLUMNNAME_JP_ToDo_Main_Calendar);
	}

	/** Completed = CO */
	public static final String JP_TODO_STATUS_Completed = "CO";
	/** Not Completed = NC */
	public static final String JP_TODO_STATUS_NotCompleted = "NC";
	/** Not yet started = NY */
	public static final String JP_TODO_STATUS_NotYetStarted = "NY";
	/** Work in progress = WP */
	public static final String JP_TODO_STATUS_WorkInProgress = "WP";
	/** Set ToDo Status.
		@param JP_ToDo_Status ToDo Status
	*/
	public void setJP_ToDo_Status (String JP_ToDo_Status)
	{

		set_Value (COLUMNNAME_JP_ToDo_Status, JP_ToDo_Status);
	}

	/** Get ToDo Status.
		@return ToDo Status	  */
	public String getJP_ToDo_Status()
	{
		return (String)get_Value(COLUMNNAME_JP_ToDo_Status);
	}

	/** Memo = M */
	public static final String JP_TODO_TYPE_Memo = "M";
	/** Schedule = S */
	public static final String JP_TODO_TYPE_Schedule = "S";
	/** Task = T */
	public static final String JP_TODO_TYPE_Task = "T";
	/** Set ToDo Type.
		@param JP_ToDo_Type ToDo Type
	*/
	public void setJP_ToDo_Type (String JP_ToDo_Type)
	{

		set_Value (COLUMNNAME_JP_ToDo_Type, JP_ToDo_Type);
	}

	/** Get ToDo Type.
		@return ToDo Type	  */
	public String getJP_ToDo_Type()
	{
		return (String)get_Value(COLUMNNAME_JP_ToDo_Type);
	}
}