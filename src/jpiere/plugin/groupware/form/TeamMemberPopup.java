package jpiere.plugin.groupware.form;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.factory.ButtonFactory;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Center;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Popup;
import org.zkoss.zul.South;

import jpiere.plugin.groupware.model.MTeam;
import jpiere.plugin.groupware.model.MTeamMember;
import jpiere.plugin.groupware.model.MToDo;
import jpiere.plugin.groupware.window.ReminderPopupWindow;
import jpiere.plugin.groupware.window.ToDoPopupWindow;


/**
 * JPIERE-0473 Personal ToDo Popup Window - Team Member Popup
 *
 *
 * @author h.hagiwara
 *
 */
public class TeamMemberPopup extends Popup implements EventListener<Event>{

	private static final long serialVersionUID = -8336091996041137809L;

	private Properties ctx = Env.getCtx();

	private Button zoomTeamBtn = null;

	private Window popupWindow = null;

	private int JP_Team_ID = 0;

	public TeamMemberPopup(Window popupWindow, int JP_Team_ID) throws Exception
	{
		this.popupWindow = popupWindow;
		this.JP_Team_ID = JP_Team_ID;

		zoomTeamBtn = ButtonFactory.createButton(Msg.getMsg(Env.getCtx(), "Zoom"), null, "");
		if (ThemeManager.isUseFontIconForImage())
			zoomTeamBtn.setIconSclass("z-icon-Zoom");
		else
			zoomTeamBtn.setImage(ThemeManager.getThemeResource("images/Zoom16.png"));
		//zoomTeamBtn.setClass("btn-small");
		zoomTeamBtn.setName("ZOOM");
		//zoomTeamBtn.setTooltiptext(Msg.getMsg(ctx, "JP_Zoom_To_TeamToDo"));
		zoomTeamBtn.addEventListener(Events.ON_CLICK, this);

		if(JP_Team_ID == 0)
		{
			this.appendChild(zoomTeamBtn);

			return ;
		}


		StringBuilder sqlSELECT = new StringBuilder("SELECT ");
		StringBuilder sqlFROM = new StringBuilder(" FROM JP_Team_Member INNER JOIN AD_User ON (JP_Team_Member.AD_User_ID = AD_User.AD_User_ID) ");
		StringBuilder sqlWHERE = new StringBuilder(" WHERE JP_Team_Member.JP_Team_ID = ? ");
		StringBuilder sqlOrder = new StringBuilder(" ORDER BY JP_Team_Member.AD_User_ID");

		//AD_User_ID(1)
		sqlSELECT.append(" JP_Team_Member.AD_User_ID");

		//User(2)
		String eSql = MLookupFactory.getLookup_TableDirEmbed(Env.getLanguage(ctx), MToDo.COLUMNNAME_AD_User_ID, MTeamMember.Table_Name);
		sqlSELECT.append(", (").append(eSql).append(") AS User");

		//EMail(3)
		sqlSELECT.append(", AD_User.EMail");

		StringBuilder sql = sqlSELECT.append(sqlFROM).append(sqlWHERE).append(sqlOrder);

		ArrayList<TeamMemberModel> list = new ArrayList<TeamMemberModel>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			pstmt.setInt(1, JP_Team_ID);
			rs = pstmt.executeQuery();

			TeamMemberModel todo = null;
			while (rs.next())
			{
				todo = new TeamMemberModel();
				todo.AD_User_ID = rs.getInt(1);
				todo.user = rs.getString(2);
				todo.EMail = rs.getString(3);
				list.add(todo);
			}

		}catch (Exception e){

			throw e;

		}finally{

			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		if(list.size() == 0)
		{
			Div div = new Div();
			this.appendChild(div);
			div.appendChild(new Label(Msg.getMsg(ctx, "not.found")));

			div = new Div();
			this.appendChild(div);
			div.appendChild(zoomTeamBtn);

			return ;
		}


		ZKUpdateUtil.setWidth(this, 360 + "px");
		ZKUpdateUtil.setHeight(this, 380 + "px");

		Borderlayout popupContent = new Borderlayout();
		this.appendChild(popupContent);
		ZKUpdateUtil.setVflex(popupContent, "max");
		ZKUpdateUtil.setHflex(popupContent, "min");

		//Center
		Center center = new Center();
		popupContent.appendChild(center);

		Grid grid = new Grid();
		ZKUpdateUtil.setVflex(grid, true);
		grid.setMold("paging");
		grid.setPageSize(10);
		grid.setPagingPosition("top");
		center.appendChild(grid);

		org.zkoss.zul.Columns columns = new Columns();
		columns.setSizable(true);
		grid.appendChild(columns);

		//User
		org.zkoss.zul.Column column = new Column();
		columns.appendChild(column);
		column.setLabel(Msg.getElement(ctx, MUser.COLUMNNAME_AD_User_ID));


		//EMail
		column = new Column();
		columns.appendChild(column);
		column.setLabel(Msg.getElement(ctx, MUser.COLUMNNAME_EMail));

		TeamMemberListModel listModel = new TeamMemberListModel(list);
		grid.setModel(listModel);

		TeamMemberListRowRenderer rowRenderer = new TeamMemberListRowRenderer(this);
		grid.setRowRenderer(rowRenderer);


		//South
		South south = new South();
		popupContent.appendChild(south);

		ZKUpdateUtil.setHflex(zoomTeamBtn, "max");
		south.appendChild(zoomTeamBtn);

		return ;
	}


	@Override
	public void onEvent(Event event) throws Exception
	{
		AEnv.zoom(MTable.getTable_ID(MTeam.Table_Name), JP_Team_ID);
		if(popupWindow instanceof ToDoPopupWindow)
		{
			popupWindow.dispose();
		}else if(popupWindow instanceof ReminderPopupWindow) {

			ReminderPopupWindow rpw = (ReminderPopupWindow)popupWindow;
			ToDoPopupWindow todoPopupWindow = rpw.getToDoPopupWindow();

			rpw.onClose();
			todoPopupWindow.dispose();

		}else {
			popupWindow.dispose();
		}

		this.detach();
	}

}
