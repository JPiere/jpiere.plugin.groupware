package jpiere.plugin.groupware.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
*
* JPIERE-0480: ToDo Reminder
*
* h.hagiwara
*
*/
public interface I_ToDoReminder {

	public int getJP_ToDo_Team_Reminder_ID  () ;

	/** JP_ToDo_ID	  */
	public void setJP_ToDo_ID (int JP_ToDo_ID);
	public int getJP_ToDo_ID();

	/** JP_ToDo_Team_ID	  */
	public int getJP_ToDo_Team_ID();
	public void setJP_ToDo_Team_ID (int JP_ToDo_Team_ID);

	/** AD_Org_ID **/
	public void setAD_Org_ID (int AD_Org_ID);
	public int getAD_Org_ID();


	public void setJP_ToDo_ReminderType (String JP_ToDo_ReminderType);
	public String getJP_ToDo_ReminderType () ;

	public void setJP_ToDo_RemindTime (Timestamp JP_ToDo_RemindTime);
	public Timestamp getJP_ToDo_RemindTime();

	public void setBroadcastFrequency (String BroadcastFrequency);

	public String getBroadcastFrequency();

	public void setIsSentReminderJP (boolean IsSentReminderJP);

	public boolean isSentReminderJP();

	public void setDescription (String Description);

	public String getDescription();

	public void setComments (String Comments);

	public String getComments();

	public void setIsConfirmed (boolean IsConfirmed);

	public boolean isConfirmed();

	public void setJP_Confirmed (Timestamp JP_Confirmed);

	public Timestamp getJP_Confirmed();

	public void setJP_Mandatory_Statistics_Info (String JP_Mandatory_Statistics_Info);

	public String getJP_Mandatory_Statistics_Info();

	public void setJP_Statistics_Choice (String JP_Statistics_Choice);

	public String getJP_Statistics_Choice();

	public void setJP_Statistics_DateAndTime (Timestamp JP_Statistics_DateAndTime);

	public Timestamp getJP_Statistics_DateAndTime();

	public void setJP_Statistics_Number (BigDecimal JP_Statistics_Number);

	public BigDecimal getJP_Statistics_Number();

	public void setJP_Statistics_YesNo (String JP_Statistics_YesNo);

	public String getJP_Statistics_YesNo();


	/** Processed **/
	public void setProcessed (boolean Processed);
	public boolean isProcessed();

	/** IsActive **/
	public void setIsActive (boolean IsActive);
	public boolean isActive();

	/** CreatedBy **/
	public int getCreatedBy();

	/** Updated **/
	public Timestamp getUpdated();

	/** Updated **/
	public void setUpdated(Timestamp updated);

	/** get_TableName **/
	public String get_TableName();

	public boolean is_ValueChanged (String columnName);

	public String beforeSavePreCheck(boolean newRecord);

	public boolean save();

	public String beforeDeletePreCheck();

	public boolean delete(boolean force);

	public String getRemindMsg();

	/** URL **/
	public void setURL (String URL);
	public String getURL();

	/** JP_MailFrequency **/
	public void setJP_MailFrequency (String JP_MailFrequency);
	public String getJP_MailFrequency();

	/** Set Remind Target	  */
	public void setJP_ToDo_RemindTarget (String JP_ToDo_RemindTarget);
	public String getJP_ToDo_RemindTarget();
}
