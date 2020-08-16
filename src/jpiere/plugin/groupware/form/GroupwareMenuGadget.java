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

import org.adempiere.util.Callback;
import org.adempiere.webui.adwindow.ADTabpanel;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.dashboard.DashboardPanel;
import org.adempiere.webui.exception.ApplicationException;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ITheme;
import org.adempiere.webui.theme.ThemeManager;
import org.compiere.model.MMenu;
import org.compiere.model.MQuery;
import org.compiere.model.MTree;
import org.compiere.model.MTreeNode;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.A;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Layout;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vlayout;

import jpiere.plugin.groupware.model.MGroupwareUser;


/**
 * JPIERE-0472: ToDo Menu Gadget
 *
 * @author h.hagiwara
 *
 * Ref:DPFavourites.java
 *
 */
public class GroupwareMenuGadget extends DashboardPanel implements EventListener<Event> {

	private static final long serialVersionUID = 8398216266900311289L;

	private static final String NODE_ID_ATTR = "Node_ID";


	private Layout vLayout_ToDoMenu;

	private List<A> links = new ArrayList<>();

	private Map<Integer, MTreeNode> nodeMap;
	private MTreeNode rootNode;


	public GroupwareMenuGadget()
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

		for (MTreeNode nd : list)
		{
			addNode(nd);
		}

	}

	private void addNode(MTreeNode nd)
	{
		addNode(nd.getNode_ID(), nd.toString().trim(), nd.getDescription(), getIconFile(nd), (nd.isWindow() && !nd.isForm()));
	}



	protected void addNode(int nodeId, String label, String description, String imageSrc, boolean addNewBtn)
	{
		Hlayout hbox = new Hlayout();
		hbox.setSclass("favourites-item");
		hbox.setSpacing("0px");
		hbox.setValign("middle");
		vLayout_ToDoMenu.appendChild(hbox);

		A btnToDoMenu = new A();
		btnToDoMenu.setAttribute(NODE_ID_ATTR, String.valueOf(nodeId));
		hbox.appendChild(btnToDoMenu);
		btnToDoMenu.setLabel(label);
		btnToDoMenu.setTooltiptext(description);
		if (ThemeManager.isUseFontIconForImage())
			btnToDoMenu.setIconSclass(imageSrc);
		else if (imageSrc.startsWith(ITheme.THEME_PATH_PREFIX))
			btnToDoMenu.setImage(imageSrc);
		else
			btnToDoMenu.setImage(ThemeManager.getThemeResource(imageSrc));
		btnToDoMenu.addEventListener(Events.ON_CLICK, this);
		btnToDoMenu.setSclass("menu-href");

		if (addNewBtn)
		{
			Toolbarbutton newBtn = new Toolbarbutton(null, ThemeManager.getThemeResource("images/New16.png"));
			if (ThemeManager.isUseFontIconForImage())
			{
				newBtn.setImage(null);
				newBtn.setIconSclass("z-icon-New");
			}
			newBtn.setAttribute(NODE_ID_ATTR, String.valueOf(nodeId));
			hbox.appendChild(newBtn);
			newBtn.addEventListener(Events.ON_CLICK, this);
			newBtn.setSclass("fav-new-btn");
			newBtn.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "New")));
		}
		links.add(btnToDoMenu);
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
		if(comp instanceof A)
		{
			A btn = (A) comp;

			int menuId = 0;
			try
			{
				menuId = Integer.valueOf((String)btn.getAttribute(NODE_ID_ATTR));
			}
			catch (Exception e) {

			}

			if(menuId > 0) SessionManager.getAppDesktop().onMenuSelected(menuId);
		}
		else if (comp instanceof Toolbarbutton) {
			Toolbarbutton btn = (Toolbarbutton) comp;

			int menuId = 0;
			try
			{
				menuId = Integer.valueOf((String)btn.getAttribute(NODE_ID_ATTR));
			}
			catch (Exception e) {

			}

			if(menuId > 0)
			{
				try
	            {
					MMenu menu = new MMenu(Env.getCtx(), menuId, null);

		    		MQuery query = new MQuery("");
	        		query.addRestriction("1=2");
					query.setRecordCount(0);

					SessionManager.getAppDesktop().openWindow(menu.getAD_Window_ID(), query, new Callback<ADWindow>() {

						@Override
						public void onCallback(ADWindow result) {
							if(result == null)
			    				return;

							result.getADWindowContent().onNew();
							ADTabpanel adtabpanel = (ADTabpanel) result.getADWindowContent().getADTab().getSelectedTabpanel();
							adtabpanel.focusToFirstEditor(false);
						}
					});
	            }
	            catch (Exception e)
	            {
	                throw new ApplicationException(e.getMessage(), e);
	            }
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
