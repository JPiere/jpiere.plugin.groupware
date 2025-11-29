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

/** Generated Interface for JP_ToDo_Reminder_Log
 *  @author iDempiere (generated) 
 *  @version Release 12
 */
@SuppressWarnings("all")
public interface I_JP_ToDo_Reminder_Log 
{

    /** TableName=JP_ToDo_Reminder_Log */
    public static final String Table_Name = "JP_ToDo_Reminder_Log";

    /** AD_Table_ID=1000260 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 7 - System - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(7);

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

	/** Get Tenant.
	  * Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within tenant
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within tenant
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

    /** Column name IsError */
    public static final String COLUMNNAME_IsError = "IsError";

	/** Set Error.
	  * An Error occurred in the execution
	  */
	public void setIsError (boolean IsError);

	/** Get Error.
	  * An Error occurred in the execution
	  */
	public boolean isError();

    /** Column name JP_ToDo_Reminder_ID */
    public static final String COLUMNNAME_JP_ToDo_Reminder_ID = "JP_ToDo_Reminder_ID";

	/** Set ToDo Reminder	  */
	public void setJP_ToDo_Reminder_ID (int JP_ToDo_Reminder_ID);

	/** Get ToDo Reminder	  */
	public int getJP_ToDo_Reminder_ID();

	public I_JP_ToDo_Reminder getJP_ToDo_Reminder() throws RuntimeException;

    /** Column name JP_ToDo_Reminder_Log_ID */
    public static final String COLUMNNAME_JP_ToDo_Reminder_Log_ID = "JP_ToDo_Reminder_Log_ID";

	/** Set ToDo Reminder Log	  */
	public void setJP_ToDo_Reminder_Log_ID (int JP_ToDo_Reminder_Log_ID);

	/** Get ToDo Reminder Log	  */
	public int getJP_ToDo_Reminder_Log_ID();

    /** Column name JP_ToDo_Reminder_Log_UU */
    public static final String COLUMNNAME_JP_ToDo_Reminder_Log_UU = "JP_ToDo_Reminder_Log_UU";

	/** Set JP_ToDo_Reminder_Log_UU	  */
	public void setJP_ToDo_Reminder_Log_UU (String JP_ToDo_Reminder_Log_UU);

	/** Get JP_ToDo_Reminder_Log_UU	  */
	public String getJP_ToDo_Reminder_Log_UU();

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
