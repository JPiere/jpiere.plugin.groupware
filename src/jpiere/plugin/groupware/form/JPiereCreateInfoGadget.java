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
package jpiere.plugin.groupware.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.activation.MimetypesFileTypeMap;

import org.adempiere.webui.component.DynamicMediaLink;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Group;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.dashboard.DashboardPanel;
import org.adempiere.webui.theme.ThemeManager;
import org.compiere.model.MAttachment;
import org.compiere.model.MAttachmentEntry;
import org.compiere.model.MClient;
import org.compiere.model.MRole;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.zkoss.util.media.AMedia;
import org.zkoss.zul.Html;
import org.zkoss.zul.Row;

import jpiere.plugin.groupware.model.MInfoGadget;
import jpiere.plugin.groupware.model.MInfoGadgetCategory;


/**
 *  JPiere Plugins(JPPS) Dashboard Gadget Create Info Gadget
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereCreateInfoGadget extends DashboardPanel {

	private Grid grid = GridFactory.newGridLayout();
	private Rows gridRows = grid.newRows();
	private Language lang = Env.getLanguage(Env.getCtx());
	private boolean isMultiLingual = true;

	public JPiereCreateInfoGadget(int JP_InfoGadgetCategory_ID)
	{
		super();
		this.appendChild(grid);

		MInfoGadgetCategory infoGadgetCategory = new MInfoGadgetCategory(Env.getCtx(), JP_InfoGadgetCategory_ID, null);
		if(infoGadgetCategory.getJP_PageSize()>0)
		{
			grid.setMold("paging");
			grid.setPageSize(infoGadgetCategory.getJP_PageSize());
			grid.setPagingPosition("top");
		}

		Calendar  calendar = Calendar.getInstance();
		long longtime = calendar.getTimeInMillis();
		Timestamp timestamp = new Timestamp(longtime);
		String systemTime = timestamp.toString();
		StringBuilder whereClause = new StringBuilder(" AND IsActive='Y' AND DateFrom <= TO_DATE('"+ systemTime +"','YYYY-MM-DD HH24:MI:SS') AND DateTo >= TO_DATE('"+systemTime+"','YYYY-MM-DD HH24:MI:SS')");
										whereClause.append(" AND PublishStatus = 'R' " );
		if(Env.getAD_Client_ID(Env.getCtx())==0)
		{
			whereClause.append(" AND AD_Client_ID = 0");
		}else{
			whereClause.append(" AND AD_Client_ID in(0,"+Env.getAD_Client_ID(Env.getCtx())+") ");
		}

		MRole role = MRole.get(Env.getCtx(), Env.getAD_Role_ID(Env.getCtx()));
		String orgAccessSQL = role.getOrgWhere(false);
		if( orgAccessSQL != null)
			whereClause.append(" AND ").append(orgAccessSQL);

		StringBuilder orderClause = new StringBuilder(" Date1 DESC, JP_InfoGadget_ID DESC");

		MInfoGadget[] infoGadgets = infoGadgetCategory.getInfoGadgets(whereClause.toString(),orderClause.toString(),infoGadgetCategory.getMaxQueryRecords());
		for(int i = 0; infoGadgets.length > i; i++)
		{
			createInfo(infoGadgets[i]);
		}

		//When no data.
		if(infoGadgets.length==0)
		{
			Row row = gridRows.newRow();
			row.setClass("jpiere-infogadget-content");
			row.appendChild(new Html(infoGadgetCategory.getHelp()));
		}


	}

	MTable table_Trl = MTable.get(Env.getCtx(), "JP_InfoGadget_Trl");
	MClient client = MClient.get(Env.getCtx());

	private void createInfo(MInfoGadget infoGadget)
	{

		if(table_Trl == null)
		{
			isMultiLingual = false;

		}else if(!client.isMultiLingualDocument()){

			isMultiLingual = false;

		}else{

			isMultiLingual = true;
		}

		org.adempiere.webui.component.Row outerRow = new org.adempiere.webui.component.Row();
		gridRows.appendChild(outerRow);

		Grid innerGrid = new Grid();
		innerGrid.setSclass("jpiere-infogadget");
		outerRow.appendChild(innerGrid);

		org.zkoss.zul.Rows innerRows = new org.zkoss.zul.Rows();
		innerGrid.appendChild(innerRows);

		SimpleDateFormat sdf = lang.getDateFormat();
		String groupTitle = null;

		MUser user = null;
		if(infoGadget.get_Value("AD_User_ID")!=null)
			user = MUser.get(Env.getCtx(), infoGadget.getAD_User_ID());

		if(isMultiLingual)
		{
			if(user==null || infoGadget.getJP_InfoGadgetCategory().getJP_UserDisplayPosition().equals("N"))
				groupTitle = new String(sdf.format(infoGadget.getDate1()) +" "+ infoGadget.get_Translation("Name"));
			else if(infoGadget.getJP_InfoGadgetCategory().getJP_UserDisplayPosition().equals("R"))
				groupTitle = new String(sdf.format(infoGadget.getDate1()) +" "+ infoGadget.get_Translation("Name") +" ["+user.getName()+"]" );
			else
				groupTitle = new String(sdf.format(infoGadget.getDate1()) +" ["+user.getName()+"] " + infoGadget.get_Translation("Name"));

		}else{
			if(user==null || infoGadget.getJP_InfoGadgetCategory().getJP_UserDisplayPosition().equals("N"))
				groupTitle = new String(sdf.format(infoGadget.getDate1()) +" "+ infoGadget.getName());
			else if(infoGadget.getJP_InfoGadgetCategory().getJP_UserDisplayPosition().equals("R"))
				groupTitle = new String(sdf.format(infoGadget.getDate1()) +" "+ infoGadget.getName() +" ["+user.getName()+"]");
			else
				groupTitle = new String(sdf.format(infoGadget.getDate1()) +" ["+user.getName()+"] " + infoGadget.getName());
		}

		Group innerRowGroup = new Group(groupTitle);
		innerRowGroup.setSclass("jpiere-infogadget-header");
		innerRows.appendChild(innerRowGroup);

		org.adempiere.webui.component.Row innerRow = new org.adempiere.webui.component.Row();
		innerRow.setSclass("jpiere-infogadget-content");

		Html html = null;
		if(isMultiLingual)
		{
			html = new Html(infoGadget.get_Translation("HTML"));
		}else{
			html = new Html(infoGadget.getHTML());
		}

		innerRow.appendChild(html);
		innerRow.setGroup(innerRowGroup);
		innerRows.appendChild(innerRow);

		MAttachment attachment = infoGadget.getAttachment();
		if(attachment != null)
		{
			MAttachmentEntry[] entry =attachment.getEntries();
			for(int j = 0; entry.length > j; j++)
			{
				MimetypesFileTypeMap mimeMap = new MimetypesFileTypeMap();
				File file = entry[j].getFile();

				try
				{
					AMedia media = new AMedia(file, mimeMap.getContentType(file), null);
					DynamicMediaLink link = new DynamicMediaLink();
					innerRow = new org.adempiere.webui.component.Row();
					innerRow.setSclass("jpiere-infogadget-attachment");
					innerRow.setGroup(innerRowGroup);
					innerRows.appendChild(innerRow);
					innerRow.appendChild(link);

					link.setImage(ThemeManager.getThemeResource("images/Attachment24.png"));
					link.setMedia(media);
					link.setLabel(media.getName());
					link.setStyle("margin: 5px;");
				} catch (FileNotFoundException e) {
					;
				}

			}//for

		}


		if(infoGadget.isCollapsedByDefault())
		{
			innerRowGroup.setOpen(false);
			innerRow.setVisible(false);
		}else{
			innerRowGroup.setOpen(true);
			innerRow.setVisible(true);
		}



	}


}
