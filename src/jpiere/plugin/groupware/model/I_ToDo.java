/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere is maintained by OSS ERP Solutions Co., Ltd.                        *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/
package jpiere.plugin.groupware.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
*
* JPIERE-0469: Model of Abstract ToDo
* JPIERE-0471: ToDo Calendar
*
* h.hagiwara
*
*/
@SuppressWarnings("all")
public interface I_ToDo
{

	/**JP_ToDo_ID or JP_ToDo_Team_ID**/
	public int get_ID();

	public Object get_Value(String columnName);


	/**JP_ToDo_ID**/
	//public void setJP_ToDo_ID (int JP_ToDo_ID);
	//public int getJP_ToDo_ID();


	/**JP_ToDo_Team_ID **/
	//public void setJP_ToDo_Team_ID (int JP_ToDo_Team_ID);
	//public int getJP_ToDo_Team_ID();
	public int getParent_Team_ToDo_ID();


	/** AD_Client_ID **/
	//public int getAD_Client_ID();


	/** AD_Org_ID **/
	public void setAD_Org_ID (int AD_Org_ID);
	//public int getAD_Org_ID();


	/** AD_User_ID **/
	public void setAD_User_ID (int AD_User_ID);
	public int getAD_User_ID();


	/** JP_ToDo_Type **/
	public void setJP_ToDo_Type (String JP_ToDo_Type);
	public String getJP_ToDo_Type();


	/** JP_ToDo_Category_ID **/
	public void setJP_ToDo_Category_ID (int JP_ToDo_Category_ID);
	public int getJP_ToDo_Category_ID();


	/** Team **/
	public void setJP_Team_ID (int JP_Team_ID);
	public int getJP_Team_ID();

	/** Name **/
	public void setName (String Name);
	public String getName();


	/** Description **/
	public void setDescription (String Description);
	public String getDescription();


	/** Comments **/
	public void setComments (String Comments);
	public String getComments();

	/** IsActive **/
	//public void setIsActive (boolean IsActive);
	//public boolean isActive();


	/** IsOpenToDo **/
	public void setIsOpenToDoJP (boolean IsOpenToDoJP);
	public boolean isOpenToDoJP();


	/** Processed **/
	//public void setProcessed (boolean Processed);
	public boolean isProcessed();


	/** JP_ToDo_ScheduledStartTime **/
	public void setJP_ToDo_ScheduledStartTime (Timestamp JP_ToDo_ScheduledStartTime);
	public Timestamp getJP_ToDo_ScheduledStartTime();


	/** JP_ToDo_ScheduledEndTime **/
	public void setJP_ToDo_ScheduledEndTime (Timestamp JP_ToDo_ScheduledEndTime);
	public Timestamp getJP_ToDo_ScheduledEndTime();

	/** JP_ToDo_Status **/
	public void setJP_ToDo_Status (String JP_ToDo_Status);
	public String getJP_ToDo_Status();




	/** JP_Statistics_YesNo **/
	public void setJP_Statistics_YesNo (String JP_Statistics_YesNo);
	public String getJP_Statistics_YesNo();

	/** JP_Statistics_Choice **/
	public void setJP_Statistics_Choice (String JP_Statistics_Choice);
	public String getJP_Statistics_Choice();

	/** JP_Statistics_DateAndTime **/
	public void setJP_Statistics_DateAndTime (Timestamp JP_Statistics_DateAndTime);
	public Timestamp getJP_Statistics_DateAndTime();

	/** JP_Statistics_Number **/
	public void setJP_Statistics_Number (BigDecimal JP_Statistics_Number);
	public BigDecimal getJP_Statistics_Number();


	/** Mandatory Statistics Info */
	public void setJP_Mandatory_Statistics_Info (String JP_Mandatory_Statistics_Info);
	public String getJP_Mandatory_Statistics_Info();


	/** CreatedBy **/
	public int getCreatedBy();


	/** JP_ToDo_StartTime **/
	//public void setJP_ToDo_StartTime (Timestamp JP_ToDo_StartTime);
	//public Timestamp getJP_ToDo_StartTime();

	/** JP_ToDo_EndTime **/
	//public void setJP_ToDo_EndTime (Timestamp JP_ToDo_EndTime);
	//public Timestamp getJP_ToDo_EndTime();



	/** C_Project_ID **/
	//public void setC_Project_ID (int C_Project_ID);
	//public int getC_Project_ID();

	/**C_ProjectPhase_ID **/
	//public void setC_ProjectPhase_ID (int C_ProjectPhase_ID);
	//public int getC_ProjectPhase_ID();

	/** C_ProjectTask_ID **/
	//public void setC_ProjectTask_ID (int C_ProjectTask_ID);
	//public int getC_ProjectTask_ID();




	public String beforeSavePreCheck(boolean newRecord);

	public boolean save();

	public String beforeDeletePreCheck();

	public boolean delete(boolean force);

}
