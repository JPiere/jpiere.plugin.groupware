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

/** Generated Interface for JP_ToDo
 *  @author iDempiere (generated) 
 *  @version Release 7.1
 */
@SuppressWarnings("all")
public interface I_JP_ToDo 
{

    /** TableName=JP_ToDo */
    public static final String Table_Name = "JP_ToDo";

    /** AD_Table_ID=1000256 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 2 - Client 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(2);

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

    /** Column name AD_Table_ID */
    public static final String COLUMNNAME_AD_Table_ID = "AD_Table_ID";

	/** Set Table.
	  * Database Table information
	  */
	public void setAD_Table_ID (int AD_Table_ID);

	/** Get Table.
	  * Database Table information
	  */
	public int getAD_Table_ID();

	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException;

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

    /** Column name Comments */
    public static final String COLUMNNAME_Comments = "Comments";

	/** Set Comments.
	  * Comments or additional information
	  */
	public void setComments (String Comments);

	/** Get Comments.
	  * Comments or additional information
	  */
	public String getComments();

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

    /** Column name IsOpenToDoJP */
    public static final String COLUMNNAME_IsOpenToDoJP = "IsOpenToDoJP";

	/** Set Open ToDo	  */
	public void setIsOpenToDoJP (boolean IsOpenToDoJP);

	/** Get Open ToDo	  */
	public boolean isOpenToDoJP();

    /** Column name JP_Processing1 */
    public static final String COLUMNNAME_JP_Processing1 = "JP_Processing1";

	/** Set Process Now	  */
	public void setJP_Processing1 (String JP_Processing1);

	/** Get Process Now	  */
	public String getJP_Processing1();

    /** Column name JP_Statistics_Choice */
    public static final String COLUMNNAME_JP_Statistics_Choice = "JP_Statistics_Choice";

	/** Set Choice	  */
	public void setJP_Statistics_Choice (String JP_Statistics_Choice);

	/** Get Choice	  */
	public String getJP_Statistics_Choice();

    /** Column name JP_Statistics_DateAndTime */
    public static final String COLUMNNAME_JP_Statistics_DateAndTime = "JP_Statistics_DateAndTime";

	/** Set Date and Time	  */
	public void setJP_Statistics_DateAndTime (Timestamp JP_Statistics_DateAndTime);

	/** Get Date and Time	  */
	public Timestamp getJP_Statistics_DateAndTime();

    /** Column name JP_Statistics_Number */
    public static final String COLUMNNAME_JP_Statistics_Number = "JP_Statistics_Number";

	/** Set Number	  */
	public void setJP_Statistics_Number (BigDecimal JP_Statistics_Number);

	/** Get Number	  */
	public BigDecimal getJP_Statistics_Number();

    /** Column name JP_Statistics_YesNo */
    public static final String COLUMNNAME_JP_Statistics_YesNo = "JP_Statistics_YesNo";

	/** Set Yes / No	  */
	public void setJP_Statistics_YesNo (String JP_Statistics_YesNo);

	/** Get Yes / No	  */
	public String getJP_Statistics_YesNo();

    /** Column name JP_ToDo_Category_ID */
    public static final String COLUMNNAME_JP_ToDo_Category_ID = "JP_ToDo_Category_ID";

	/** Set ToDo Category	  */
	public void setJP_ToDo_Category_ID (int JP_ToDo_Category_ID);

	/** Get ToDo Category	  */
	public int getJP_ToDo_Category_ID();

	public I_JP_ToDo_Category getJP_ToDo_Category() throws RuntimeException;

    /** Column name JP_ToDo_EndTime */
    public static final String COLUMNNAME_JP_ToDo_EndTime = "JP_ToDo_EndTime";

	/** Set End Time	  */
	public void setJP_ToDo_EndTime (Timestamp JP_ToDo_EndTime);

	/** Get End Time	  */
	public Timestamp getJP_ToDo_EndTime();

    /** Column name JP_ToDo_ID */
    public static final String COLUMNNAME_JP_ToDo_ID = "JP_ToDo_ID";

	/** Set ToDo	  */
	public void setJP_ToDo_ID (int JP_ToDo_ID);

	/** Get ToDo	  */
	public int getJP_ToDo_ID();

    /** Column name JP_ToDo_Related_ID */
    public static final String COLUMNNAME_JP_ToDo_Related_ID = "JP_ToDo_Related_ID";

	/** Set Related ToDo	  */
	public void setJP_ToDo_Related_ID (int JP_ToDo_Related_ID);

	/** Get Related ToDo	  */
	public int getJP_ToDo_Related_ID();

	public I_JP_ToDo getJP_ToDo_Related() throws RuntimeException;

    /** Column name JP_ToDo_ScheduledEndTime */
    public static final String COLUMNNAME_JP_ToDo_ScheduledEndTime = "JP_ToDo_ScheduledEndTime";

	/** Set Scheduled End Time	  */
	public void setJP_ToDo_ScheduledEndTime (Timestamp JP_ToDo_ScheduledEndTime);

	/** Get Scheduled End Time	  */
	public Timestamp getJP_ToDo_ScheduledEndTime();

    /** Column name JP_ToDo_ScheduledStartTime */
    public static final String COLUMNNAME_JP_ToDo_ScheduledStartTime = "JP_ToDo_ScheduledStartTime";

	/** Set Scheduled Start Time	  */
	public void setJP_ToDo_ScheduledStartTime (Timestamp JP_ToDo_ScheduledStartTime);

	/** Get Scheduled Start Time	  */
	public Timestamp getJP_ToDo_ScheduledStartTime();

    /** Column name JP_ToDo_StartTime */
    public static final String COLUMNNAME_JP_ToDo_StartTime = "JP_ToDo_StartTime";

	/** Set Start Time	  */
	public void setJP_ToDo_StartTime (Timestamp JP_ToDo_StartTime);

	/** Get Start Time	  */
	public Timestamp getJP_ToDo_StartTime();

    /** Column name JP_ToDo_Status */
    public static final String COLUMNNAME_JP_ToDo_Status = "JP_ToDo_Status";

	/** Set ToDo Status	  */
	public void setJP_ToDo_Status (String JP_ToDo_Status);

	/** Get ToDo Status	  */
	public String getJP_ToDo_Status();

    /** Column name JP_ToDo_Team_ID */
    public static final String COLUMNNAME_JP_ToDo_Team_ID = "JP_ToDo_Team_ID";

	/** Set Team ToDo	  */
	public void setJP_ToDo_Team_ID (int JP_ToDo_Team_ID);

	/** Get Team ToDo	  */
	public int getJP_ToDo_Team_ID();

	public I_JP_ToDo_Team getJP_ToDo_Team() throws RuntimeException;

    /** Column name JP_ToDo_Type */
    public static final String COLUMNNAME_JP_ToDo_Type = "JP_ToDo_Type";

	/** Set ToDo Type	  */
	public void setJP_ToDo_Type (String JP_ToDo_Type);

	/** Get ToDo Type	  */
	public String getJP_ToDo_Type();

    /** Column name JP_ToDo_UU */
    public static final String COLUMNNAME_JP_ToDo_UU = "JP_ToDo_UU";

	/** Set JP_ToDo_UU	  */
	public void setJP_ToDo_UU (String JP_ToDo_UU);

	/** Get JP_ToDo_UU	  */
	public String getJP_ToDo_UU();

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name Processed */
    public static final String COLUMNNAME_Processed = "Processed";

	/** Set Processed.
	  * The document has been processed
	  */
	public void setProcessed (boolean Processed);

	/** Get Processed.
	  * The document has been processed
	  */
	public boolean isProcessed();

    /** Column name Record_ID */
    public static final String COLUMNNAME_Record_ID = "Record_ID";

	/** Set Record ID.
	  * Direct internal record ID
	  */
	public void setRecord_ID (int Record_ID);

	/** Get Record ID.
	  * Direct internal record ID
	  */
	public int getRecord_ID();

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
