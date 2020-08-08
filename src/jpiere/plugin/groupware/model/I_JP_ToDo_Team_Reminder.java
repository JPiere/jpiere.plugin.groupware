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

/** Generated Interface for JP_ToDo_Team_Reminder
 *  @author iDempiere (generated) 
 *  @version Release 7.1
 */
@SuppressWarnings("all")
public interface I_JP_ToDo_Team_Reminder 
{

    /** TableName=JP_ToDo_Team_Reminder */
    public static final String Table_Name = "JP_ToDo_Team_Reminder";

    /** AD_Table_ID=1000255 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

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

    /** Column name IsSentReminderJP */
    public static final String COLUMNNAME_IsSentReminderJP = "IsSentReminderJP";

	/** Set Sent Reminder	  */
	public void setIsSentReminderJP (boolean IsSentReminderJP);

	/** Get Sent Reminder	  */
	public boolean isSentReminderJP();

    /** Column name JP_Mandatory_Statistics_Info */
    public static final String COLUMNNAME_JP_Mandatory_Statistics_Info = "JP_Mandatory_Statistics_Info";

	/** Set Mandatory Statistics Info	  */
	public void setJP_Mandatory_Statistics_Info (String JP_Mandatory_Statistics_Info);

	/** Get Mandatory Statistics Info	  */
	public String getJP_Mandatory_Statistics_Info();

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

    /** Column name JP_ToDo_Team_ID */
    public static final String COLUMNNAME_JP_ToDo_Team_ID = "JP_ToDo_Team_ID";

	/** Set Team ToDo	  */
	public void setJP_ToDo_Team_ID (int JP_ToDo_Team_ID);

	/** Get Team ToDo	  */
	public int getJP_ToDo_Team_ID();

	public I_JP_ToDo_Team getJP_ToDo_Team() throws RuntimeException;

    /** Column name JP_ToDo_Team_Reminder_ID */
    public static final String COLUMNNAME_JP_ToDo_Team_Reminder_ID = "JP_ToDo_Team_Reminder_ID";

	/** Set JP_ToDo_Team_Reminder	  */
	public void setJP_ToDo_Team_Reminder_ID (int JP_ToDo_Team_Reminder_ID);

	/** Get JP_ToDo_Team_Reminder	  */
	public int getJP_ToDo_Team_Reminder_ID();

    /** Column name JP_ToDo_Team_Reminder_UU */
    public static final String COLUMNNAME_JP_ToDo_Team_Reminder_UU = "JP_ToDo_Team_Reminder_UU";

	/** Set JP_ToDo_Team_Reminder_UU	  */
	public void setJP_ToDo_Team_Reminder_UU (String JP_ToDo_Team_Reminder_UU);

	/** Get JP_ToDo_Team_Reminder_UU	  */
	public String getJP_ToDo_Team_Reminder_UU();

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
