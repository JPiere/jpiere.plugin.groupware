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
package jpiere.plugin.groupware.process;

import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MUser;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

import jpiere.plugin.groupware.model.MTeam;
import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoMemberAdditional;
import jpiere.plugin.groupware.model.MToDoTeam;
import jpiere.plugin.groupware.util.GroupwareTeamUtil;

/**
 * JPIERE-0469: Create ToDo From Team ToDo
 *
 * @author h.hagiwara
 *
 */
public class CreateToDoFromTeamToDo extends SvrProcess {

	private int p_JP_ToDo_Team_ID = 0;

	@SuppressWarnings("unused")
	private int p_JP_Team_ID = 0;

	private String p_JP_ToDo_Member_Additional_ID = null;

	@Override
	protected void prepare()
	{
		p_JP_ToDo_Team_ID = getRecord_ID();

		//JP_ToDo_Team_Member_ID

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("JP_Team_ID")){
				p_JP_Team_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_ToDo_Member_Additional_ID")){
				p_JP_ToDo_Member_Additional_ID = para[i].getParameterAsString();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for
	}

	@Override
	protected String doIt() throws Exception
	{

		MToDoTeam teamToDo = new MToDoTeam(getCtx(), p_JP_ToDo_Team_ID, get_TrxName());
		MUser[] users = teamToDo.getAdditionalTeamMemberUser();

		//Additional Member
		if(p_JP_ToDo_Member_Additional_ID != null)
		{
			String[] additionalUser_IDs = p_JP_ToDo_Member_Additional_ID.split(",");
			boolean isAlreadyMember = false;
			int additionalUser_ID = 0;
			for(int i = 0; i < additionalUser_IDs.length; i++)
			{
				isAlreadyMember = false;
				additionalUser_ID = Integer.valueOf(additionalUser_IDs[i]).intValue();
				for(int j = 0; j < users.length; j++)
				{
					if(users[j].getAD_User_ID() == additionalUser_ID)
					{
						isAlreadyMember=true;
						break;
					}
				}//for j

				if(!isAlreadyMember)
				{
					MToDoMemberAdditional user = new MToDoMemberAdditional(getCtx(), 0, get_TrxName());
					user.setAD_Org_ID(teamToDo.getAD_Org_ID());
					user.setAD_User_ID(additionalUser_ID);
					user.setJP_ToDo_Team_ID(teamToDo.getJP_ToDo_Team_ID());
					user.saveEx(get_TrxName());
				}

			}//for i

			users = teamToDo.getAdditionalTeamMemberUser(true);
		}

		int JP_Team_ID = teamToDo.getJP_Team_ID();

		if(teamToDo.getJP_Team_ID() != 0)
		{
			MTeam team = new MTeam(getCtx(),JP_Team_ID, get_TrxName() );
			MUser[] teammember = team.getTeamMemberUser();
			users = GroupwareTeamUtil.addTeamMember(users,teammember);
		}

		if(users.length == 0)
		{
			if(getTable_ID()==0)//Process is Called From ToDoPopupWindow.
			{
				return "JP_ToDo_NoUserToCreatePersonalToDo";//There are not any users to create a Personal ToDo.

			}else {//Process is Called From Personal ToDo Window

				throw new AdempiereException(Msg.getMsg(getCtx(), "JP_ToDo_NoUserToCreatePersonalToDo"));
			}
		}


		MToDo[] toDoes = teamToDo.getToDoes();
		boolean isAlreadyRegistered = false;
		int created = 0;

		for(int i = 0; i < users.length; i++)
		{
			isAlreadyRegistered = false;
			for(int j = 0; j < toDoes.length; j++)
			{
				if(users[i].getAD_User_ID() == toDoes[j].getAD_User_ID())
				{
					isAlreadyRegistered = true;
					break;
				}

			}//for j

			if(!isAlreadyRegistered)
			{
				MToDo newToDo = new MToDo(getCtx(), 0 , get_TrxName());
				PO.copyValues(teamToDo, newToDo);

				newToDo.setAD_Org_ID(teamToDo.getAD_Org_ID());
				newToDo.setJP_ToDo_Team_ID(teamToDo.getJP_ToDo_Team_ID());
				newToDo.setAD_User_ID(users[i].getAD_User_ID());
				newToDo.setJP_ToDo_Status(MToDo.JP_TODO_STATUS_NotYetStarted);
				newToDo.setJP_ToDo_StartTime(null);
				newToDo.setJP_ToDo_EndTime(null);
				newToDo.setProcessed(false);
				newToDo.saveEx(get_TrxName());
				created++;
			}

		}//For i

		if(getTable_ID()==0)//Process is Called From ToDoPopupWindow.
		{
			return "Success";

		}else {//Process is Called From Personal ToDo Window

			return Msg.getMsg(getCtx(), "Success") + " " + Msg.getMsg(getCtx(), "Created")+":" + created;

		}

	}

}
