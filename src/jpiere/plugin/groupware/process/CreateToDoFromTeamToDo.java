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

import org.compiere.model.MUser;
import org.compiere.model.PO;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

import jpiere.plugin.groupware.model.MTeam;
import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.model.MToDoTeam;
import jpiere.plugin.groupware.util.GroupwareTeamUtil;

/**
 * JPIERE-0469: Create ToDo From Team ToDo
 *
 * @author h.hagiwara
 *
 */
public class CreateToDoFromTeamToDo extends SvrProcess {

	private int JP_ToDo_Team_ID = 0;

	@Override
	protected void prepare()
	{
		JP_ToDo_Team_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception
	{

		MToDoTeam teamToDo = new MToDoTeam(getCtx(), JP_ToDo_Team_ID, get_TrxName());
		MUser[] users = teamToDo.getAdditionalTeamMemberUser();

		int JP_Team_ID = teamToDo.getJP_Team_ID();

		if(teamToDo.getJP_Team_ID() != 0)
		{
			MTeam team = new MTeam(getCtx(),JP_Team_ID, get_TrxName() );
			MUser[] teammember = team.getTeamMemberUser();
			users = GroupwareTeamUtil.addTeamMember(users,teammember);
		}

		if(users.length == 0)
		{
			throw new Exception("だれもいません！");//TODO
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

				newToDo.setAD_Org_ID(0);
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


		return Msg.getMsg(getCtx(), "Success") + " " + Msg.getMsg(getCtx(), "Created")+":" + created;
	}

}
