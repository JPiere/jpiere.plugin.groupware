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
package jpiere.plugin.groupware.base;

import java.util.Properties;

import org.compiere.model.MClient;
import org.compiere.model.MRole;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.Env;



/**
 *  JPIERE-0469
 *  JPiere Plugin Groupware Model validator
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPierePluginGroupwareModelValidator implements ModelValidator {

	//private static CLogger log = CLogger.getCLogger(JPierePluginGroupwareModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
	}

	@Override
	public int getAD_Client_ID()
	{
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		Properties ctx = Env.getCtx();
		MRole role = MRole.get(ctx, AD_Role_ID);
		Env.setContext(ctx, "#PreferenceType", role.getPreferenceType());

		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception
	{
		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		return null;
	}



}
