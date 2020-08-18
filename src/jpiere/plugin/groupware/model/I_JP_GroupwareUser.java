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
package jpiere.plugin.groupware.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for JP_GroupwareUser
 *  @author iDempiere (generated) 
 *  @version Release 7.1
 */
@SuppressWarnings("all")
public interface I_JP_GroupwareUser 
{

    /** TableName=JP_GroupwareUser */
    public static final String Table_Name = "JP_GroupwareUser";

    /** AD_Table_ID=1000258 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 7 - System - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(7);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name AD_Tree_Menu_ID */
    public static final String COLUMNNAME_AD_Tree_Menu_ID = "AD_Tree_Menu_ID";

	/** Set Menu Tree.
	  * Tree of the menu
	  */
	public void setAD_Tree_Menu_ID (int AD_Tree_Menu_ID);

	/** Get Menu Tree.
	  * Tree of the menu
	  */
	public int getAD_Tree_Menu_ID();

	public org.compiere.model.I_AD_Tree getAD_Tree_Menu() throws RuntimeException;

    /** Column name AD_User_ID */
    public static final String COLUMNNAME_AD_User_ID = "AD_User_ID";

	/** Set User/Contact.
	  * User within the system - Internal or Business Partner Contact
	  */
	public void setAD_User_ID (int AD_User_ID);

	/** Get User/Contact.
	  * User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID();

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException;

    /** Column name C_ProjectPhase_ID */
    public static final String COLUMNNAME_C_ProjectPhase_ID = "C_ProjectPhase_ID";

	/** Set Project Phase.
	  * Phase of a Project
	  */
	public void setC_ProjectPhase_ID (int C_ProjectPhase_ID);

	/** Get Project Phase.
	  * Phase of a Project
	  */
	public int getC_ProjectPhase_ID();

	public org.compiere.model.I_C_ProjectPhase getC_ProjectPhase() throws RuntimeException;

    /** Column name C_ProjectTask_ID */
    public static final String COLUMNNAME_C_ProjectTask_ID = "C_ProjectTask_ID";

	/** Set Project Task.
	  * Actual Project Task in a Phase
	  */
	public void setC_ProjectTask_ID (int C_ProjectTask_ID);

	/** Get Project Task.
	  * Actual Project Task in a Phase
	  */
	public int getC_ProjectTask_ID();

	public org.compiere.model.I_C_ProjectTask getC_ProjectTask() throws RuntimeException;

    /** Column name C_Project_ID */
    public static final String COLUMNNAME_C_Project_ID = "C_Project_ID";

	/** Set Project.
	  * Financial Project
	  */
	public void setC_Project_ID (int C_Project_ID);

	/** Get Project.
	  * Financial Project
	  */
	public int getC_Project_ID();

	public org.compiere.model.I_C_Project getC_Project() throws RuntimeException;

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name IsDisplayScheduleJP */
    public static final String COLUMNNAME_IsDisplayScheduleJP = "IsDisplayScheduleJP";

	/** Set Display Schedule	  */
	public void setIsDisplayScheduleJP (boolean IsDisplayScheduleJP);

	/** Get Display Schedule	  */
	public boolean isDisplayScheduleJP();

    /** Column name IsDisplayTaskJP */
    public static final String COLUMNNAME_IsDisplayTaskJP = "IsDisplayTaskJP";

	/** Set Display Task	  */
	public void setIsDisplayTaskJP (boolean IsDisplayTaskJP);

	/** Get Display Task	  */
	public boolean isDisplayTaskJP();

    /** Column name JP_ColorPicker */
    public static final String COLUMNNAME_JP_ColorPicker = "JP_ColorPicker";

	/** Set Color Picker	  */
	public void setJP_ColorPicker (String JP_ColorPicker);

	/** Get Color Picker	  */
	public String getJP_ColorPicker();

    /** Column name JP_ColorPicker2 */
    public static final String COLUMNNAME_JP_ColorPicker2 = "JP_ColorPicker2";

	/** Set Color Picker2	  */
	public void setJP_ColorPicker2 (String JP_ColorPicker2);

	/** Get Color Picker2	  */
	public String getJP_ColorPicker2();

    /** Column name JP_DefaultCalendarView */
    public static final String COLUMNNAME_JP_DefaultCalendarView = "JP_DefaultCalendarView";

	/** Set Default Calendar View	  */
	public void setJP_DefaultCalendarView (String JP_DefaultCalendarView);

	/** Get Default Calendar View	  */
	public String getJP_DefaultCalendarView();

    /** Column name JP_FirstDayOfWeek */
    public static final String COLUMNNAME_JP_FirstDayOfWeek = "JP_FirstDayOfWeek";

	/** Set First Day of Week	  */
	public void setJP_FirstDayOfWeek (String JP_FirstDayOfWeek);

	/** Get First Day of Week	  */
	public String getJP_FirstDayOfWeek();

    /** Column name JP_GroupwareUser_ID */
    public static final String COLUMNNAME_JP_GroupwareUser_ID = "JP_GroupwareUser_ID";

	/** Set Groupware User Preference	  */
	public void setJP_GroupwareUser_ID (int JP_GroupwareUser_ID);

	/** Get Groupware User Preference	  */
	public int getJP_GroupwareUser_ID();

    /** Column name JP_GroupwareUser_UU */
    public static final String COLUMNNAME_JP_GroupwareUser_UU = "JP_GroupwareUser_UU";

	/** Set JP_GroupwareUser_UU	  */
	public void setJP_GroupwareUser_UU (String JP_GroupwareUser_UU);

	/** Get JP_GroupwareUser_UU	  */
	public String getJP_GroupwareUser_UU();

    /** Column name JP_Team_ID */
    public static final String COLUMNNAME_JP_Team_ID = "JP_Team_ID";

	/** Set Team	  */
	public void setJP_Team_ID (int JP_Team_ID);

	/** Get Team	  */
	public int getJP_Team_ID();

	public I_JP_Team getJP_Team() throws RuntimeException;

    /** Column name JP_ToDo_Category_ID */
    public static final String COLUMNNAME_JP_ToDo_Category_ID = "JP_ToDo_Category_ID";

	/** Set ToDo Category	  */
	public void setJP_ToDo_Category_ID (int JP_ToDo_Category_ID);

	/** Get ToDo Category	  */
	public int getJP_ToDo_Category_ID();

	public I_JP_ToDo_Category getJP_ToDo_Category() throws RuntimeException;

    /** Column name JP_ToDo_Status */
    public static final String COLUMNNAME_JP_ToDo_Status = "JP_ToDo_Status";

	/** Set ToDo Status	  */
	public void setJP_ToDo_Status (String JP_ToDo_Status);

	/** Get ToDo Status	  */
	public String getJP_ToDo_Status();

    /** Column name JP_ToDo_Type */
    public static final String COLUMNNAME_JP_ToDo_Type = "JP_ToDo_Type";

	/** Set ToDo Type	  */
	public void setJP_ToDo_Type (String JP_ToDo_Type);

	/** Get ToDo Type	  */
	public String getJP_ToDo_Type();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();
}
