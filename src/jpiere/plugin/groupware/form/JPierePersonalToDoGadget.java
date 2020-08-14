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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.ToolBarButton;
import org.adempiere.webui.dashboard.DashboardPanel;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.MColumn;
import org.compiere.model.MForm;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.Query;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;

import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.util.GroupwareToDoUtil;
import jpiere.plugin.groupware.window.PersonalToDoPopupWindow;


/**
 *  JPiere Plugins(JPPS) Dashboard Gadget Create Info Gadget
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPierePersonalToDoGadget extends DashboardPanel implements EventListener<Event>, ValueChangeListener {

	Div headerArea = new Div();
	Div messageArea = new Div();
	Div contentsArea = new Div();
	Div footerArea = new Div();

	private int p_AD_User_ID = 0;
	private String p_JP_ToDo_Type = "T";
	private LocalDateTime p_LocalDateTime =null;
	private String p_FormattedLocalDateTime = null;

	private Timestamp today = null;


	private final static String BUTTON_NAME_PREVIOUS_DAY = "PREVIOUSDAY";
	private final static String BUTTON_NAME_NEXT_DAY = "NEXTDAY";
	private final static String BUTTON_NAME_NEW_TODO = "NEW";
	private final static String BUTTON_NAME_REFRESH = "REFRESH";
	private final static String BUTTON_NAME_CALENDER = "CALENDER";

	private Language lang = Env.getLanguage(Env.getCtx());
	private int login_User_ID = 0;
	private WDateEditor editor_Date = null;

	private List<MToDo>  list_ToDoes = null;

	public JPierePersonalToDoGadget()
	{
		super();
		init("T");
	}

	public JPierePersonalToDoGadget(String JP_ToDo_Type)
	{
		super();
		init(JP_ToDo_Type);
	}

	public void init(String JP_ToDo_Type)
	{

		setSclass("views-box");

		p_JP_ToDo_Type = JP_ToDo_Type;
		p_AD_User_ID = Env.getAD_User_ID(Env.getCtx());
		login_User_ID = p_AD_User_ID;

		p_LocalDateTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN);
		p_FormattedLocalDateTime = formattedDate(p_LocalDateTime) ;

		editor_Date = new WDateEditor("JP_ToDoScheduledDate", false, false, true, "");
		editor_Date.setValue(Timestamp.valueOf(p_LocalDateTime));
		editor_Date.addValueChangeListener(this);

		today = Timestamp.valueOf(LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN));

		createHeader();
		createMessage();
		createContents();

		this.appendChild(headerArea);
		this.appendChild(messageArea);
		this.appendChild(contentsArea);
		this.appendChild(footerArea);

	}

	private void createHeader()
	{
		Grid grid = GridFactory.newGridLayout();
		headerArea.appendChild(grid);
		Rows gridRows = grid.newRows();
		Row row = gridRows.newRow();


		/**User Search Field**/
		Label label_User = new Label(Msg.translate(Env.getCtx(), "AD_User_ID"));
		row.appendCellChild(createLabelDiv(label_User, true),1);

		MLookup lookupUser = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name, MToDo.COLUMNNAME_AD_User_ID),  DisplayType.Search);
		WSearchEditor userSearchEditor = new WSearchEditor("AD_User_ID", true, false, true, lookupUser);
		userSearchEditor.setValue(p_AD_User_ID);
		userSearchEditor.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(userSearchEditor.getComponent(), "true");
		row.appendCellChild(userSearchEditor.getComponent(),1);

		/**ToDo Type List Field**/
		Label label_ToDoType = new Label(Msg.translate(Env.getCtx(), MToDo.COLUMNNAME_JP_ToDo_Type));
		row.appendCellChild(createLabelDiv(label_ToDoType, true),1);

		MLookup lookupToDoType = MLookupFactory.get(Env.getCtx(), 0,  0, MColumn.getColumn_ID(MToDo.Table_Name,  MToDo.COLUMNNAME_JP_ToDo_Type),  DisplayType.List);
		WTableDirEditor toDoListEditor = new WTableDirEditor(MToDo.COLUMNNAME_JP_ToDo_Type, true, false, true, lookupToDoType);
		toDoListEditor.setValue(p_JP_ToDo_Type);
		toDoListEditor.addValueChangeListener(this);
		ZKUpdateUtil.setHflex(toDoListEditor.getComponent(), "true");
		row.appendCellChild(toDoListEditor.getComponent(),1);
	}

	private Div createLabelDiv(Label label, boolean isMandatory )
	{
		label.rightAlign();
		label.setMandatory(isMandatory);
		Div div = new Div();
		div.setSclass("form-label");
		div.appendChild(label);
		if(isMandatory)
			div.appendChild(label.getDecorator());

		return div;
	}

	private void createMessage()
	{
		if(messageArea.getFirstChild() != null)
			messageArea.getFirstChild().detach();

		Hlayout hlayout = new Hlayout();
		messageArea.appendChild(hlayout);

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		if(p_AD_User_ID == 0)
		{
			WStringEditor editor_Text = new WStringEditor();
			editor_Text.setReadWrite(false);
			editor_Text.setValue(Msg.getMsg(Env.getCtx(),"enter") + ":" +Msg.getElement(Env.getCtx(), "AD_User_ID"));
			hlayout.appendChild(editor_Text.getComponent());

		}else if(MToDo.JP_TODO_TYPE_Task.equals(p_JP_ToDo_Type)) {

			WStringEditor editor_Text = new WStringEditor();
			editor_Text.setReadWrite(false);
			editor_Text.setValue(Msg.getMsg(Env.getCtx(), "JP_UnfinishedTasks"));//Unfinished Tasks
			hlayout.appendChild(editor_Text.getComponent());

		}else if(MToDo.JP_TODO_TYPE_Schedule.equals(p_JP_ToDo_Type)) {

			Button leftBtn = new Button();
			leftBtn.setImage(ThemeManager.getThemeResource("images/MoveLeft16.png"));
			leftBtn.setClass("btn-small");
			leftBtn.setName(BUTTON_NAME_PREVIOUS_DAY);
			leftBtn.addEventListener(Events.ON_CLICK, this);
			hlayout.appendChild(leftBtn);

			editor_Date.setValue(Timestamp.valueOf(p_LocalDateTime));
			hlayout.appendChild(editor_Date.getComponent());

			Button rightBtn = new Button();
			rightBtn.setImage(ThemeManager.getThemeResource("images/MoveRight16.png"));
			rightBtn.setClass("btn-small");
			rightBtn.addEventListener(Events.ON_CLICK, this);
			rightBtn.setName(BUTTON_NAME_NEXT_DAY);
			hlayout.appendChild(rightBtn);


		}else if(MToDo.JP_TODO_TYPE_Memo.equals(p_JP_ToDo_Type)) {

			WStringEditor editor_Text = new WStringEditor();
			editor_Text.setReadWrite(false);
			editor_Text.setValue(Msg.getMsg(Env.getCtx(), "JP_UnfinishedMemo"));//Unfinished Memo
			hlayout.appendChild(editor_Text.getComponent());

		}else {

			WStringEditor editor_Text = new WStringEditor();
			editor_Text.setReadWrite(false);
			editor_Text.setValue(Msg.getMsg(Env.getCtx(),"enter") + ":" +Msg.getElement(Env.getCtx(), "JP_ToDo_Type"));
			hlayout.appendChild(editor_Text.getComponent());

		}

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

		Button createNewToDo = new Button();
		createNewToDo.setImage(ThemeManager.getThemeResource("images/New16.png"));
		createNewToDo.setClass("btn-small");
		createNewToDo.setName(BUTTON_NAME_NEW_TODO);
		createNewToDo.addEventListener(Events.ON_CLICK, this);
		createNewToDo.setId(String.valueOf(0));
		hlayout.appendChild(createNewToDo);

		Button refresh = new Button();
		refresh.setImage(ThemeManager.getThemeResource("images/Refresh16.png"));
		refresh.setClass("btn-small");
		refresh.setName(BUTTON_NAME_REFRESH);
		refresh.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(refresh);

		Button calander = new Button();
		calander.setImage(ThemeManager.getThemeResource("images/Calendar16.png"));
		calander.setClass("btn-small");
		calander.setName(BUTTON_NAME_CALENDER);
		calander.addEventListener(Events.ON_CLICK, this);
		hlayout.appendChild(calander);

		hlayout.appendChild(GroupwareToDoUtil.getDividingLine());

	}

	public void createContents()
	{

		if(contentsArea.getFirstChild() != null)
			contentsArea.getFirstChild().detach();

		StringBuilder whereClause = null;
		StringBuilder orderClause = null;
		ArrayList<Object> list_parameters  = new ArrayList<Object>();
		Object[] parameters = null;


		if(MToDo.JP_TODO_TYPE_Task.equals(p_JP_ToDo_Type) || MToDo.JP_TODO_TYPE_Memo.equals(p_JP_ToDo_Type))
		{
			whereClause = new StringBuilder(" AD_User_ID = ? AND JP_ToDo_Type = ? AND IsActive='Y' AND JP_ToDo_Status <> ?");
			orderClause = new StringBuilder("JP_ToDo_ScheduledEndTime");
			list_parameters.add(p_AD_User_ID);
			list_parameters.add(p_JP_ToDo_Type);
			list_parameters.add(MToDo.JP_TODO_STATUS_Completed);

		}else if(MToDo.JP_TODO_TYPE_Schedule.equals(p_JP_ToDo_Type)) {

			whereClause = new StringBuilder(" AD_User_ID = ? AND JP_ToDo_Type = ? AND IsActive='Y' AND JP_ToDo_ScheduledStartTime < ? AND JP_ToDo_ScheduledEndTime >= ?");
			orderClause = new StringBuilder("JP_ToDo_ScheduledStartTime");

			LocalDateTime toDayMin = LocalDateTime.of(p_LocalDateTime.toLocalDate(), LocalTime.MIN);
			LocalDateTime toDayMax = LocalDateTime.of(p_LocalDateTime.toLocalDate(), LocalTime.MAX);
			list_parameters.add(p_AD_User_ID);
			list_parameters.add(p_JP_ToDo_Type);
			list_parameters.add(Timestamp.valueOf(toDayMax));
			list_parameters.add(Timestamp.valueOf(toDayMin));

		}else {

			whereClause = new StringBuilder(" AD_User_ID = ? ");
			orderClause = new StringBuilder("JP_ToDo_ScheduledEndTime");
			list_parameters.add(0);

		}

		if(login_User_ID != p_AD_User_ID)
		{
			whereClause = whereClause.append(" AND (IsOpenToDoJP='Y' OR CreatedBy = ?)");
			list_parameters.add(login_User_ID);
		}

		parameters = list_parameters.toArray(new Object[list_parameters.size()]);

		list_ToDoes = getToDoes(whereClause.toString(), orderClause.toString(), parameters);

		if(list_ToDoes.size() <= 0)
		{
			contentsArea.appendChild(new Label(Msg.getMsg(Env.getCtx(), "not.found")));
			return ;
		}

		Grid grid = GridFactory.newGridLayout();
		grid.setMold("paging");
		//grid.setPageSize(20); //default=20
		grid.setPagingPosition("top");
		contentsArea.appendChild(grid);

		Rows gridRows = grid.newRows();
		int counter = 0;
		for (MToDo toDo : list_ToDoes)
		{
			Row row = gridRows.newRow();
			ToolBarButton btn = new ToolBarButton(toDo.getName());
			btn.setSclass("link");
			createTitle(toDo, btn);
			btn.addEventListener(Events.ON_CLICK, this);
			btn.setId(String.valueOf(toDo.getJP_ToDo_ID()));
			btn.setAttribute("index", counter);
			counter++;
			row.appendChild(btn);
		}//for
	}


	private void createTitle(MToDo toDo, ToolBarButton btn)
	{
		if(MToDo.JP_TODO_TYPE_Task.equals(p_JP_ToDo_Type))
		{
			Timestamp scheduledEndDay = Timestamp.valueOf(LocalDateTime.of(toDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime().toLocalDate(), LocalTime.MIN));
			if(today.compareTo(scheduledEndDay) < 0)
			{
				btn.setImage(ThemeManager.getThemeResource("images/" + "InfoIndicator16.png"));

			}else if(today.compareTo(scheduledEndDay) == 0){

				btn.setImage(ThemeManager.getThemeResource("images/" + "mSetVariable.png"));

			}else if(today.compareTo(scheduledEndDay) > 0) {

				btn.setImage(ThemeManager.getThemeResource("images/" + "ErrorIndicator16.png"));

			}

			if(toDo.getJP_ToDo_Team_ID() == 0)
			{
				btn.setLabel(formattedDate(toDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime()) + " " + toDo.getName());
			}else {
				btn.setLabel(formattedDate(toDo.getJP_ToDo_ScheduledEndTime().toLocalDateTime())
						+" ["+ Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_ToDo_Team_ID) +"] "+toDo.getName());
			}

		}else if(MToDo.JP_TODO_TYPE_Schedule.equals(p_JP_ToDo_Type)) {

			Timestamp scheduledStartTime = toDo.getJP_ToDo_ScheduledStartTime();
			Timestamp scheduledEndTime  = toDo.getJP_ToDo_ScheduledEndTime();

			String formattedscheduledStartTime = formattedDate(scheduledStartTime.toLocalDateTime()) ;
			String formattedscheduledEndTime = formattedDate(scheduledEndTime.toLocalDateTime()) ;

			if(p_FormattedLocalDateTime.equals(formattedscheduledStartTime) && p_FormattedLocalDateTime.equals(formattedscheduledEndTime))
			{
				btn.setImage(ThemeManager.getThemeResource("images/" + "InfoSchedule16.png"));
				LocalTime startTime = scheduledStartTime.toLocalDateTime().toLocalTime();
				LocalTime endTime = scheduledEndTime.toLocalDateTime().toLocalTime();

				if(toDo.getJP_ToDo_Team_ID() == 0)
				{
					btn.setLabel(p_FormattedLocalDateTime + " " + startTime.toString() + " - " + endTime.toString() + " " + toDo.getName());
				}else {
					btn.setLabel(p_FormattedLocalDateTime + " " + startTime.toString() + " - " + endTime.toString()
						+" ["+ Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_ToDo_Team_ID) +"] "+toDo.getName()) ;
				}

			}else {

				btn.setImage(ThemeManager.getThemeResource("images/" + "Register16.png"));
				if(toDo.getJP_ToDo_Team_ID() == 0)
				{
					btn.setLabel(formattedscheduledStartTime + " - " + formattedscheduledEndTime + " " + toDo.getName());
				}else {
					btn.setLabel(formattedscheduledStartTime + " - " + formattedscheduledEndTime
						+" ["+ Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_ToDo_Team_ID) +"] "+toDo.getName()) ;
				}


			}

		}else if(MToDo.JP_TODO_TYPE_Memo.equals(p_JP_ToDo_Type)) {

			btn.setImage(ThemeManager.getThemeResource("images/" + "Editor16.png"));
			if(toDo.getJP_ToDo_Team_ID() == 0)
			{
				btn.setLabel(toDo.getName());
			}else {
				btn.setLabel(" ["+ Msg.getElement(Env.getCtx(), MToDo.COLUMNNAME_JP_ToDo_Team_ID) +"] "+toDo.getName());
			}


		}
	}

	private String formattedDate(LocalDateTime dateTime)
	{
		return lang.getDateFormat().format(Timestamp.valueOf(dateTime));
	}

	@Override
	public void valueChange(ValueChangeEvent evt)
	{
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();

		if(MToDo.COLUMNNAME_AD_User_ID.equals(name))
		{
			if(value == null)
				p_AD_User_ID = 0;
			else
				p_AD_User_ID = Integer.parseInt(value.toString());

		}else if(MToDo.COLUMNNAME_JP_ToDo_Type.equals(name)) {

			if(MToDo.JP_TODO_TYPE_Task.equals(value))
				p_JP_ToDo_Type = MToDo.JP_TODO_TYPE_Task;
			else if(MToDo.JP_TODO_TYPE_Schedule.equals(value))
				p_JP_ToDo_Type = MToDo.JP_TODO_TYPE_Schedule;
			else if(MToDo.JP_TODO_TYPE_Memo.equals(value))
				p_JP_ToDo_Type = MToDo.JP_TODO_TYPE_Memo;
			else
				p_JP_ToDo_Type = null;

		}else if("JP_ToDoScheduledDate".equals(name)) {

			if(value == null)
			{
				p_LocalDateTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN);
				editor_Date.setValue(Timestamp.valueOf(p_LocalDateTime));

			}else {

				p_LocalDateTime = ((Timestamp)value).toLocalDateTime();

			}

			p_FormattedLocalDateTime = formattedDate(p_LocalDateTime) ;
		}

		createMessage();
		createContents();
	}

	@Override
	public void onEvent(Event event) throws Exception
	{
		Component comp = event.getTarget();
		String eventName = event.getName();
		if(eventName.equals(Events.ON_CLICK))
		{
			if(comp instanceof Button)
			{
				Button btn = (Button) comp;
				String btnName = btn.getName();
				if(BUTTON_NAME_PREVIOUS_DAY.equals(btnName))
				{
					p_LocalDateTime = p_LocalDateTime.minusDays(1);
					p_FormattedLocalDateTime = formattedDate(p_LocalDateTime) ;
					createMessage();
					createContents();

				}else if(BUTTON_NAME_NEXT_DAY.equals(btnName)){

					p_LocalDateTime = p_LocalDateTime.plusDays(1);
					p_FormattedLocalDateTime = formattedDate(p_LocalDateTime) ;
					createMessage();
					createContents();

				}else if(BUTTON_NAME_NEW_TODO.equals(btnName)){

					PersonalToDoPopupWindow todoWindow = new PersonalToDoPopupWindow(this, -1);
					SessionManager.getAppDesktop().showWindow(todoWindow);

				}else if(BUTTON_NAME_REFRESH.equals(btnName)){

					createContents();

				}else if(BUTTON_NAME_CALENDER.equals(btnName)){

					MForm form = GroupwareToDoUtil.getToDoCallendarForm();
					SessionManager.getAppDesktop().openForm(form.getAD_Form_ID());

				}

			}else if(comp instanceof ToolBarButton) {

				Object list_index = comp.getAttribute("index");
				int index = Integer.valueOf(list_index.toString()).intValue();
				PersonalToDoPopupWindow todoWindow = new PersonalToDoPopupWindow(this, index);
				SessionManager.getAppDesktop().showWindow(todoWindow);

			}

		}

	}

	private List<MToDo> getToDoes(String whereClause, String orderClause, Object ...parameters)
	{

		List<MToDo> list = new Query(Env.getCtx(), MToDo.Table_Name, whereClause.toString(), null)
										.setParameters(parameters)
										.setOrderBy(orderClause)
										.list();
		return list;
	}

	public int getAD_User_ID()
	{
		return p_AD_User_ID;
	}

	public String getJP_ToDo_Type()
	{
		return p_JP_ToDo_Type;
	}

	public Timestamp getSelectedDate()
	{
		return Timestamp.valueOf(p_LocalDateTime);
	}

	public List<MToDo>  getListToDoes()
	{
		return list_ToDoes;
	}
}
