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

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.adempiere.base.IModelFactory;
import org.adempiere.base.Service;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.WEditorPopupMenu;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.GridTab;
import org.compiere.model.MClient;
import org.compiere.model.MColumn;
import org.compiere.model.MLanguage;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTable;
import org.compiere.model.MToolBarButton;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.zkforge.ckez.CKeditor;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.North;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.impl.XulElement;

/**
 * JPiere CKEditor
 *
 * JPIERE-0109
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPiereCKEditor implements EventListener<Event>, ValueChangeListener, IFormController{

	private  static CLogger log = CLogger.getCLogger(JPiereCKEditor.class);

	private String baselang = Language.getBaseAD_Language();

	private JPiereCKEditorForm   wysiwygEditorForm =null;

	private String baseTableName ;
	private String trlTableName ;

	private String columnName ;
	private String keyColumnName;

	private int record_ID = 0;

	private PO po ;
	private boolean isMultiLingual = true;
	private boolean isSameClientData = false;

	private final String JPIERE_CKEDITOR_IMAGE_PATH = MSysConfig.getValue("JPIERE_CKEDITOR_IMAGE_PATH", Env.getAD_Client_ID(Env.getCtx()));
	private final String JPIERE_CKEDITOR_CUSTOM_CONFIGURATIONS_PATH = MSysConfig.getValue("JPIERE_CKEDITOR_CUSTOM_CONFIGURATIONS_PATH", Env.getAD_Client_ID(Env.getCtx()));

	/**********************************************************************
	 * UI Component
	 **********************************************************************/

	private Borderlayout mainLayout = new Borderlayout();
	private North north = new North();
	private Center center = new Center();
	private South south = new South();

	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();

	private WTableDirEditor tableDirEditor;

	private Combobox imagePathCombobox;

	private String[] imageDirs;
	private String imagePath;

	private Button SaveButton;

	private CKeditor ckeditor = new CKeditor();


	/**
	 * Constractor
	 *
	 * @throws IOException
	 */
    public JPiereCKEditor() throws IOException
    {
    	wysiwygEditorForm = new JPiereCKEditorForm(this);
		LayoutUtils.addSclass("jpiere-ckeditor-form", wysiwygEditorForm);
    }

	public ADForm getForm()
	{

		return wysiwygEditorForm;
	}


	public void initForm()
	{
		String errorMessage = prepare();
		if(errorMessage != null)
		{
			north.appendChild(new Text(errorMessage));
			mainLayout.appendChild(north);
			return;
		}
		zkInit();
		LayoutUtils.sendDeferLayoutEvent(mainLayout, 100);
	}

	private String prepare()
	{
		wysiwygEditorForm.setSizable(true);
		wysiwygEditorForm.setClosable(true);
		wysiwygEditorForm.setMaximizable(true);
		ZKUpdateUtil.setWidth(wysiwygEditorForm, "80%");
		ZKUpdateUtil.setHeight(wysiwygEditorForm, "80%");
		wysiwygEditorForm.appendChild (mainLayout);
		LayoutUtils.addSclass("jpiere-ckeditor-form-content", mainLayout);
		wysiwygEditorForm.setBorder("normal");

		ProcessInfo pInfo = wysiwygEditorForm.getProcessInfo();

		if(pInfo == null)
		{
			return "ProcessInfo is Null";
		}else{
			record_ID = pInfo.getRecord_ID();
		}


		GridTab gTab = wysiwygEditorForm.getGridTab();
		baseTableName = gTab.getTableName();

		MTable table_Trl = MTable.get(Env.getCtx(), baseTableName + "_Trl");
		MClient client = MClient.get(Env.getCtx());

		if(table_Trl == null)
		{
			isMultiLingual = false;

		}else if(!client.isMultiLingualDocument()){

			isMultiLingual = false;

		}else{

			isMultiLingual = true;
			trlTableName = baseTableName + "_Trl";
		}

		MToolBarButton[]  toolBarButtons =MToolBarButton.getProcessButtonOfTab(gTab.getAD_Tab_ID(),null);
		int counter = 0;
		for(int i = 0; toolBarButtons.length > i; i++)
		{

			if(toolBarButtons[i].getAD_Process_ID() == pInfo.getAD_Process_ID())
			{
				counter++;
				if(counter > 1)
				{
					return pInfo.getTitle()+" process can set only one per one tab";
				}

				columnName = toolBarButtons[i].getComponentName();

			}
		}


		List<IModelFactory> factoryList = Service.locator().list(IModelFactory.class).getServices();
		if (factoryList != null)
		{
			for(IModelFactory factory : factoryList) {
				po = factory.getPO(baseTableName, record_ID, null);
				if (po != null)
					break;
			}
		}


		if (po == null)
		{
			return "PO is Null";
		}

		int columnIndex = po.get_ColumnIndex(columnName);
		if(columnIndex < 0)
		{
			return "Error of column setting";
		}

		String[] keyColumns = po.get_KeyColumns();
		if(keyColumns.length > 1)
		{
			return "key column must be only one." ;
		}
		keyColumnName = keyColumns[0];

		if(po.getAD_Client_ID()==Env.getAD_Client_ID(Env.getCtx()))
		{
			isSameClientData = true;
		}

		return null;
	}


	private void zkInit()
	{
		/*Main Layout(Borderlayout)*/
		ZKUpdateUtil.setWidth(mainLayout, "100%");
		ZKUpdateUtil.setHeight(mainLayout, "100%");

		mainLayout.appendChild(north);

		//Search Parameter Panel
		north.appendChild(parameterPanel);
		north.setStyle("border: none");
		parameterPanel.appendChild(parameterLayout); 		//parameterLayout is Grid
		ZKUpdateUtil.setWidth(parameterLayout, "100%");
		Rows parameterLayoutRows = parameterLayout.newRows();
		Row row = null;


		row = parameterLayoutRows.newRow();

		//List of Launguage
		if(isMultiLingual)
		{
			String languageElementName = Msg.getElement(Env.getCtx(), MLanguage.COLUMNNAME_AD_Language);
			org.adempiere.webui.component.Label languageLabel = new org.adempiere.webui.component.Label (languageElementName);
			row.appendCellChild(languageLabel.rightAlign(),1);

			int AD_Column_ID = MColumn.getColumn_ID(MClient.Table_Name, MClient.COLUMNNAME_AD_Language);
			MLookup lookup = MLookupFactory.get (Env.getCtx(), wysiwygEditorForm.getWindowNo(), 0, AD_Column_ID, DisplayType.Table);

			tableDirEditor = new WTableDirEditor("AD_Language", false, false, true, lookup);
			tableDirEditor.addValueChangeListener(this);
			row.appendCellChild(tableDirEditor.getComponent(),2);

			//Popup Menu
			WEditorPopupMenu  popupMenu = tableDirEditor.getPopupMenu();
			List<Component> listcomp = popupMenu.getChildren();
			Menuitem menuItem = null;
			String image = null;
			for(Component comp : listcomp)
			{
				if(comp instanceof Menuitem)
				{
					menuItem = (Menuitem)comp;
					image = menuItem.getImage();
					if(image != null && (image.endsWith("Zoom16.png")||image.endsWith("Refresh16.png")
							|| image.endsWith("New16.png") || image.endsWith("InfoBPartner16.png")) )
					{
						menuItem.setVisible(true);
					}else{
						menuItem.setVisible(false);
					}
				}
			}//for

	        if (popupMenu != null)
	        {
	        	popupMenu.addMenuListener((ContextMenuListener)tableDirEditor);
	        	row.appendChild(popupMenu);
	        	popupMenu.addContextElement((XulElement) tableDirEditor.getComponent());
	        }

		}//if(isMultiLingual)



		//Image Path ComboBox
		if(JPIERE_CKEDITOR_IMAGE_PATH != null)
		{
			imageDirs = JPIERE_CKEDITOR_IMAGE_PATH.split(";");

	        for(int i = 0; imageDirs.length > i ; i++)
	        {
	        	if(i==0)
	        	{
	        		imagePathCombobox = new Combobox();
	        		imagePathCombobox.setAutocomplete(true);
	        		imagePathCombobox.setAutodrop(true);
	        		imagePathCombobox.setId("lstImagePath");
	        		imagePathCombobox.addEventListener(Events.ON_SELECT, this);

	        		imagePath = imageDirs[i];
	        		String pathElementName = Msg.getElement(Env.getCtx(), "File_Directory");
	    			org.adempiere.webui.component.Label imagePathLabel = new org.adempiere.webui.component.Label (pathElementName);
	    			row.appendCellChild(imagePathLabel.rightAlign(),1);

	        		imagePathCombobox.setValue(imageDirs[i]);
	        		setImagePath(imageDirs[i]);
	        		row.appendCellChild(imagePathCombobox,2);
	        	}

	        	imagePathCombobox.appendItem(imageDirs[i]);
	        }

		}


    	//Save Button
		if(isSameClientData)
		{
			SaveButton = new Button(Msg.getMsg(Env.getCtx(), "save"));
			SaveButton.setId("SaveButton");
			SaveButton.addActionListener(this);
			SaveButton.setEnabled(true);
			SaveButton.setImage(ThemeManager.getThemeResource("images/Save16.png"));
			ZKUpdateUtil.setWidth(SaveButton, "100%");
			row.appendCellChild(SaveButton);
		}

		//for space under Button
		row = parameterLayoutRows.newRow();
				row.appendCellChild(new Space(),1);

		//Edit Area
		mainLayout.appendChild(center);
			ckeditor.setValue((String)po.get_Value(columnName));
			ckeditor.setVflex("1");
			Map<String,Object> lang = new HashMap<String,Object>();
			lang.put("language", Language.getLoginLanguage().getAD_Language());
			ckeditor.setConfig(lang);
			if(JPIERE_CKEDITOR_CUSTOM_CONFIGURATIONS_PATH != null)
				ckeditor.setCustomConfigurationsPath(JPIERE_CKEDITOR_CUSTOM_CONFIGURATIONS_PATH);
//			ckeditor.setCustomConfigurationsPath("/images/config.js");
//			ckeditor.setToolbar("MyToolbar");
			center.appendChild(ckeditor);

		mainLayout.appendChild(south);
		ZKUpdateUtil.setHeight(south, "0px");
	}


	private String m_trxName;

	@Override
	public void onEvent(Event e) throws Exception
	{

		if (e == null)
		{
			return;

		}else if(e.getTarget().equals(SaveButton)){

			if(po != null && !isMultiLingual)
			{
				po.set_ValueNoCheck(columnName, ckeditor.getValue());
				po.saveEx();

			}else if(isMultiLingual){

				Trx trx = null;
				if (m_trxName == null)
				{
					StringBuilder l_trxname = new StringBuilder("CKEditor").append(baseTableName);
					if (l_trxname.length() > 23)
						l_trxname.setLength(23);
					m_trxName = Trx.createTrxName(l_trxname.toString());
					trx = Trx.get(m_trxName, true);
				}

				StringBuilder sqlupdate = null;
				if (tableDirEditor.getValue()== null || tableDirEditor.getValue().toString().equals(baselang))
				{
					po.set_ValueNoCheck(columnName, ckeditor.getValue());
					sqlupdate = new StringBuilder("UPDATE ")
						.append(baseTableName).append(" SET ").append(columnName).append("='").append(ckeditor.getValue()).append("'")
						.append(" WHERE ").append(keyColumnName).append("=").append(po.get_ID());

				}else{//Update Trl
					 sqlupdate = new StringBuilder("UPDATE ")
						.append(trlTableName).append(" SET ").append(columnName).append("='").append(ckeditor.getValue()).append("'")
						.append(" WHERE ").append(keyColumnName).append("=").append(po.get_ID())
						.append(" AND AD_Language =").append(DB.TO_STRING(tableDirEditor.getValue().toString()));
				}

				int no = DB.executeUpdate(sqlupdate.toString(), m_trxName);
				if(no == 1)
				{
					trx.commit();
					m_trxName = null;
				}else{
					trx.rollback();
					m_trxName = null;
					throw new Exception(Msg.getMsg(Env.getCtx(), "SaveError"));
				}


			}else{
				throw new Exception(Msg.getMsg(Env.getCtx(), "SaveError"));
			}

		}else if(e.getTarget().equals(imagePathCombobox)){

			String tempPath = ((Combobox)e.getTarget()).getValue();

			//Check path;
			boolean isOK = false;
			for(int i = 0; imageDirs.length > i ; i++)
			{
				if(tempPath.equals(imageDirs[i]))
				{
					isOK = true;
					imagePath = tempPath;
				}
			}

			if(!isOK)
			{
				imagePathCombobox.setValue(imagePath);
				throw new Exception(tempPath + Msg.getMsg(Env.getCtx(), "not.found"));
			}

			center.getChildren().remove(ckeditor);

			ckeditor = new CKeditor();
			ckeditor.setVflex("1");
			Map<String,Object> lang = new HashMap<String,Object>();
			lang.put("language", Language.getLoginLanguage().getAD_Language());
			ckeditor.setConfig(lang);
			if(JPIERE_CKEDITOR_CUSTOM_CONFIGURATIONS_PATH != null)
				ckeditor.setCustomConfigurationsPath(JPIERE_CKEDITOR_CUSTOM_CONFIGURATIONS_PATH);

			setImagePath(imagePath);
			refreshCKEditor();

			center.appendChild(ckeditor);
		}

	}//onEvent()



	public void valueChange(ValueChangeEvent e)
	{
		tableDirEditor.setValue(e.getNewValue());
		refreshCKEditor();
	}//valueChange(ValueChangeEvent e)

	private void setImagePath(String path)
	{
		ckeditor.setFilebrowserImageBrowseUrl(path);
		ckeditor.setFilebrowserImageUploadUrl(path);
		ckeditor.setFilebrowserBrowseUrl(path);
		ckeditor.setFilebrowserUploadUrl(path);
		ckeditor.setFilebrowserFlashBrowseUrl(path);
		ckeditor.setFilebrowserFlashUploadUrl(path);
	}

	private void refreshCKEditor()
	{
		if (tableDirEditor.getValue()== null || tableDirEditor.getValue().toString().equals(baselang))
		{
			ckeditor.setValue((String)po.get_Value(columnName));

		}else{
			StringBuilder sql = new StringBuilder("SELECT ").append(columnName).append(" FROM ").append(trlTableName)
										.append(" WHERE ").append(keyColumnName).append("=").append(po.get_ID())
										.append(" AND AD_Language =").append(DB.TO_STRING(tableDirEditor.getValue().toString()));
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql.toString(), null);
				rs = pstmt.executeQuery();
				if(rs.next())
					ckeditor.setValue(rs.getString(1));
			}
			catch (Exception exception)
			{
				log.log(Level.SEVERE, sql.toString(), exception);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		}
	}

}
