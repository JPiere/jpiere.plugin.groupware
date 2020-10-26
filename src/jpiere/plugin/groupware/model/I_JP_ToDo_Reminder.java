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

/** Generated Interface for JP_ToDo_Reminder
 *  @author iDempiere (generated) 
 *  @version Release 7.1
 */
@SuppressWarnings("all")
public interface I_JP_ToDo_Reminder 
{

    /** TableName=JP_ToDo_Reminder */
    public static final String Table_Name = "JP_ToDo_Reminder";

    /** AD_Table_ID=1000257 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 6 - System - Client 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(6);

    /** Load Meta Data */

    /** Column name AD_BroadcastMessage_ID */
    public static final String COLUMNNAME_AD_BroadcastMessage_ID = "AD_BroadcastMessage_ID";

	/** Set Broadcast Message.
	  * Broadcast Message
	  */
	public void setAD_BroadcastMessage_ID (int AD_BroadcastMessage_ID);

	/** Get Broadcast Message.
	  * Broadcast Message
	  */
	public int getAD_BroadcastMessage_ID();

	public org.compiere.model.I_AD_BroadcastMessage getAD_BroadcastMessage() throws RuntimeException;

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

    /** Column name AD_UserMail_ID */
    public static final String COLUMNNAME_AD_UserMail_ID = "AD_UserMail_ID";

	/** Set User Mail.
	  * Mail sent to the user
	  */
	public void setAD_UserMail_ID (int AD_UserMail_ID);

	/** Get User Mail.
	  * Mail sent to the user
	  */
	public int getAD_UserMail_ID();

	public org.compiere.model.I_AD_UserMail getAD_UserMail() throws RuntimeException;

    /** Column name BroadcastFrequency */
    public static final String COLUMNNAME_BroadcastFrequency = "BroadcastFrequency";

	/** Set Broadcast Frequency.
	  * How Many Times Message Should be Broadcasted
	  */
	public void setBroadcastFrequency (String BroadcastFrequency);

	/** Get Broadcast Frequency.
	  * How Many Times Message Should be Broadcasted
	  */
	public String getBroadcastFrequency();

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

    /** Column name IsConfirmed */
    public static final String COLUMNNAME_IsConfirmed = "IsConfirmed";

	/** Set Confirmed.
	  * Assignment is confirmed
	  */
	public void setIsConfirmed (boolean IsConfirmed);

	/** Get Confirmed.
	  * Assignment is confirmed
	  */
	public boolean isConfirmed();

    /** Column name IsSentReminderJP */
    public static final String COLUMNNAME_IsSentReminderJP = "IsSentReminderJP";

	/** Set Sent Reminder	  */
	public void setIsSentReminderJP (boolean IsSentReminderJP);

	/** Get Sent Reminder	  */
	public boolean isSentReminderJP();

    /** Column name JP_Confirmed */
    public static final String COLUMNNAME_JP_Confirmed = "JP_Confirmed";

	/** Set Confirmed	  */
	public void setJP_Confirmed (Timestamp JP_Confirmed);

	/** Get Confirmed	  */
	public Timestamp getJP_Confirmed();

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

    /** Column name JP_ToDo_ID */
    public static final String COLUMNNAME_JP_ToDo_ID = "JP_ToDo_ID";

	/** Set Personal ToDo	  */
	public void setJP_ToDo_ID (int JP_ToDo_ID);

	/** Get Personal ToDo	  */
	public int getJP_ToDo_ID();

	public I_JP_ToDo getJP_ToDo() throws RuntimeException;

    /** Column name JP_ToDo_RemindTime */
    public static final String COLUMNNAME_JP_ToDo_RemindTime = "JP_ToDo_RemindTime";

	/** Set Remind Time	  */
	public void setJP_ToDo_RemindTime (Timestamp JP_ToDo_RemindTime);

	/** Get Remind Time	  */
	public Timestamp getJP_ToDo_RemindTime();

    /** Column name JP_ToDo_ReminderType */
    public static final String COLUMNNAME_JP_ToDo_ReminderType = "JP_ToDo_ReminderType";

	/** Set Reminder Type	  */
	public void setJP_ToDo_ReminderType (String JP_ToDo_ReminderType);

	/** Get Reminder Type	  */
	public String getJP_ToDo_ReminderType();

    /** Column name JP_ToDo_Reminder_ID */
    public static final String COLUMNNAME_JP_ToDo_Reminder_ID = "JP_ToDo_Reminder_ID";

	/** Set ToDo Reminder	  */
	public void setJP_ToDo_Reminder_ID (int JP_ToDo_Reminder_ID);

	/** Get ToDo Reminder	  */
	public int getJP_ToDo_Reminder_ID();

    /** Column name JP_ToDo_Reminder_UU */
    public static final String COLUMNNAME_JP_ToDo_Reminder_UU = "JP_ToDo_Reminder_UU";

	/** Set JP_ToDo_Reminder_UU	  */
	public void setJP_ToDo_Reminder_UU (String JP_ToDo_Reminder_UU);

	/** Get JP_ToDo_Reminder_UU	  */
	public String getJP_ToDo_Reminder_UU();

    /** Column name JP_ToDo_Team_Reminder_ID */
    public static final String COLUMNNAME_JP_ToDo_Team_Reminder_ID = "JP_ToDo_Team_Reminder_ID";

	/** Set JP_ToDo_Team_Reminder	  */
	public void setJP_ToDo_Team_Reminder_ID (int JP_ToDo_Team_Reminder_ID);

	/** Get JP_ToDo_Team_Reminder	  */
	public int getJP_ToDo_Team_Reminder_ID();

	public I_JP_ToDo_Team_Reminder getJP_ToDo_Team_Reminder() throws RuntimeException;

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
