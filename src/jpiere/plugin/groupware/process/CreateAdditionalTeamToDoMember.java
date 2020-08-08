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

import org.compiere.model.MUser;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.plugin.groupware.model.MTeam;
import jpiere.plugin.groupware.model.MToDoMemberAdditional;
import jpiere.plugin.groupware.model.MToDoTeam;

/**
 * JPIERE-0469: Create Additional Team ToDo Member from Team
 *
 * @author h.hagiwara
 *
 */
public class CreateAdditionalTeamToDoMember extends SvrProcess {

	private int p_JP_ToDo_Team_ID = 0;
	private int p_JP_Team_ID = 0;

	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("JP_Team_ID")){
				p_JP_Team_ID = para[i].getParameterAsInt();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for

		p_JP_ToDo_Team_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception
	{
		if(p_JP_Team_ID == 0)
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_Team_ID")};
			String msg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);//Team field  is mandatory.
			throw new Exception(msg);
		}

		MTeam team = new MTeam(getCtx(), p_JP_Team_ID, get_TrxName());
		MUser[] teamMember = team.getTeamMemberUser();

		MToDoTeam teamToDo = new MToDoTeam(getCtx(), p_JP_ToDo_Team_ID, get_TrxName());
		MToDoMemberAdditional[]  additionalTeamMember =  teamToDo.getAdditionalTeamMember();

		boolean isAlreadyRegistered = false;
		int created = 0;
		for(int i = 0; i < teamMember.length; i++)
		{
			isAlreadyRegistered = false;
			for(int j = 0; j < additionalTeamMember.length; j++)
			{
				if(teamMember[i].getAD_User_ID() == additionalTeamMember[j].getAD_User_ID())
				{
					isAlreadyRegistered = true;
					break;
				}

			}//for j

			if(!isAlreadyRegistered)
			{
				MToDoMemberAdditional newMember = new MToDoMemberAdditional(getCtx(), 0, get_TrxName());
				newMember.setAD_Org_ID(0);
				newMember.setJP_ToDo_Team_ID(p_JP_ToDo_Team_ID);
				newMember.setAD_User_ID(teamMember[i].getAD_User_ID());
				newMember.saveEx(get_TrxName());
				created++;
			}

		}//For i


		return Msg.getMsg(getCtx(), "Success") + " " + Msg.getMsg(getCtx(), "Created")+":" + created;
	}

}
