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

import org.adempiere.webui.panel.ADForm;

/**
 * JPiere CKEditor Form
 *
 * JPIERE-0109
 *
 * @author Trek Global(Copy from WTabEditorForm)
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPiereCKEditorForm extends ADForm
{
	/**
	 *
	 */
	private static final long serialVersionUID = -2533099650671242190L;

	private JPiereCKEditor te;

	public JPiereCKEditorForm(JPiereCKEditor wTabEditor) {
		te = wTabEditor;
	}

	@Override
	public Mode getWindowMode() {
		return Mode.HIGHLIGHTED;
	}

	@Override
	public boolean setVisible(boolean visible) {
		 boolean ok = super.setVisible(visible);
		 if (visible && getProcessInfo() != null)
			 te.initForm();
		 return ok;
	}

	@Override
	protected void initForm() {
	}

}
