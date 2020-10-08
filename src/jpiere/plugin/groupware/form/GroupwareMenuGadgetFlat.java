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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.ToolBarButton;
import org.adempiere.webui.dashboard.DashboardPanel;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.compiere.model.MTree;
import org.compiere.model.MTreeNode;
import org.compiere.util.Env;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Layout;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vlayout;

import jpiere.plugin.groupware.model.MGroupwareUser;


/**
 * JPIERE-0472: ToDo Flat Menu Gadget
 *
 * @author h.hagiwara
 *
 * Ref:DPFavourites.java
 *
 */
public class GroupwareMenuGadgetFlat extends DashboardPanel implements EventListener<Event> {

	private static final long serialVersionUID = 8398216266900311289L;

	private static final String NODE_ID = "Node_ID";

	private Layout vLayout_ToDoMenu;

	private Map<Integer, MTreeNode> nodeMap;
	private MTreeNode rootNode;


	public GroupwareMenuGadgetFlat()
	{
		super();

		init();

		Panel panel = new Panel();
		this.appendChild(panel);

		Panelchildren toDoMenuContent = new Panelchildren();
		panel.appendChild(toDoMenuContent);
		vLayout_ToDoMenu = new Vlayout();
		this.setSclass("favourites-box");
		toDoMenuContent.appendChild(vLayout_ToDoMenu);
		createToDoMenuPanel();

	}

	private void init()
	{
		nodeMap = new LinkedHashMap<>();

		MGroupwareUser  gUser = MGroupwareUser.get(Env.getCtx(), Env.getAD_User_ID(Env.getCtx()));

		int AD_Tree_ID = 0;
		if(gUser != null)
		{
			AD_Tree_ID = MGroupwareUser.get(Env.getCtx(), Env.getAD_User_ID(Env.getCtx())).getAD_Tree_Menu_ID();
		}else {

			AD_Tree_ID  = 0;

		}

		if(AD_Tree_ID == 0)
		{
			return ;
		}

		MTree vTree = new MTree(Env.getCtx(), AD_Tree_ID, false, true, false, null);
		rootNode = vTree.getRoot();
		Enumeration<?> enTop = rootNode.children();
		while(enTop.hasMoreElements())
		{
			MTreeNode ndTop = (MTreeNode)enTop.nextElement();
			Enumeration<?> en = ndTop.preorderEnumeration();
			while (en.hasMoreElements())
			{
				MTreeNode nd = (MTreeNode)en.nextElement();
				if(!nd.isSummary())
				{
					nodeMap.put(nd.getNode_ID(), nd);
				}
			}
		}
	}

	private void createToDoMenuPanel()
	{
		List<MTreeNode> list = new ArrayList<>();
		for(int key : nodeMap.keySet()) {
			list.add(nodeMap.get(key));
		}

		setSclass("views-box");

		Grid grid = GridFactory.newGridLayout();
		grid.setMold("paging");
		grid.setPageSize(20); //default=20

		grid.setPagingPosition("top");
		vLayout_ToDoMenu.appendChild(grid);

		Rows gridRows = grid.newRows();
		for (MTreeNode node  : list)
		{
			Row row = gridRows.newRow();
			ToolBarButton btn = new ToolBarButton(node.toString().trim());
			btn.setSclass("link");
			btn.setLabel(node.toString().trim());
			if (ThemeManager.isUseFontIconForImage())
			{
				btn.setIconSclass(getIconFile(node));
			}else {
				btn.setImage(ThemeManager.getThemeResource(getIconFile(node)));
			}
			btn.addEventListener(Events.ON_CLICK, this);
			int node_ID = node.getNode_ID();
			btn.setAttribute(NODE_ID, String.valueOf(node_ID));
			row.appendChild(btn);
		}//for

	}


    public void onEvent(Event event)
    {
        Component comp = event.getTarget();
        String eventName = event.getName();

        if(eventName.equals(Events.ON_CLICK))
        {
            doOnClick(comp);
        }

	}

	private void doOnClick(Component comp)
	{
		if (comp instanceof Toolbarbutton)
		{
			Toolbarbutton btn = (Toolbarbutton) comp;

			int menuId = 0;
			try
			{
				menuId = Integer.valueOf((String)btn.getAttribute(NODE_ID));
			}
			catch (Exception e) {

			}

			if(menuId > 0)
			{
				 SessionManager.getAppDesktop().onMenuSelected(menuId);
			}
		}
	}


	private String getIconFile(MTreeNode mt)
	{
		if (ThemeManager.isUseFontIconForImage())
		{
			return getIconSclass(mt);
		}

		if (mt.isWindow())
			return "images/mWindow.png";
		if (mt.isReport())
			return "images/mReport.png";
		if (mt.isProcess() || mt.isTask())
			return "images/mProcess.png";
		if (mt.isWorkFlow())
			return "images/mWorkFlow.png";
		if (mt.isForm())
			return "images/mForm.png";
		if (mt.isInfo())
			return "images/mInfo.png";
		return "images/mWindow.png";
	}

	private String getIconSclass(MTreeNode mt)
	{
		if (mt.isWindow())
			return "z-icon-Window";
		if (mt.isReport())
			return "z-icon-Report";
		if (mt.isProcess() || mt.isTask())
			return "z-icon-Task";
		if (mt.isWorkFlow())
			return "z-icon-WorkFlow";
		if (mt.isForm())
			return "z-icon-Form";
		if (mt.isInfo())
			return "z-icon-Info";
		return "z-icon-Window";
	}
}
