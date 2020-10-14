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
package jpiere.plugin.groupware.window;


import java.util.Comparator;
import java.util.Properties;

import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.event.ActionEvent;
import org.adempiere.webui.event.ActionListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.part.WindowContainer;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.North;
import org.zkoss.zul.ext.Sortable;



/**
 *
 * JPIERE-0473 Personal ToDo List Popup Window
 *
 *
 * @author h.hagiwara
 *
 */
public class PersonalToDoListWindow extends Window implements EventListener<Event>, WTableModelListener, ActionListener, Sortable<Object>
{
	/**	Logger			*/
	protected CLogger log = CLogger.getCLogger(getClass());
	private Properties ctx = null;

	private Borderlayout layout = null;



	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = 304878472233552113L;

	public PersonalToDoListWindow(ToDoPopupWindow todoPopupWindow)
	{
		ctx = Env.getCtx();

		init();

		addEventListener(WindowContainer.ON_WINDOW_CONTAINER_SELECTION_CHANGED_EVENT, this);
		addEventListener(Events.ON_CLOSE, this);
	}



	private void init()
	{
		setTitle(Msg.getMsg(ctx, "JP_ToDo_PersonalToDoList"));

		layout = new Borderlayout();
		this.appendChild(layout);

		//North
		North noth = new North();
		layout.appendChild(noth);
		noth.appendChild(new Label(Msg.getMsg(ctx, "not.found")));

		setAttribute(Window.MODE_KEY, Window.MODE_HIGHLIGHTED);
		setBorder("normal");
		setClosable(true);

		int height = SessionManager.getAppDesktop().getClientInfo().desktopHeight * 85 / 100;
		int width = SessionManager.getAppDesktop().getClientInfo().desktopWidth * 80 / 100;
		ZKUpdateUtil.setWidth(this, width + "px");
		ZKUpdateUtil.setHeight(this, height + "px");
		this.setContentStyle("overflow: auto");
	}





	@Override
	public void tableChanged(WTableModelEvent event)
	{
		;
	}


	@Override
	public void onEvent(Event event) throws Exception
	{
		if(event.getName().equals(Events.ON_CLOSE))
		{
			dispose();
		}

	}

    public void dispose()
    {
        this.detach();
    }   //  dispose


	@Override
	public void actionPerformed(ActionEvent event)
	{
		;
	}

	@Override
	public void sort(Comparator<Object> cmpr, boolean ascending)
	{
		;
	}



	@Override
	public String getSortDirection(Comparator<Object> cmpr)
	{
		return null;
	}



}
