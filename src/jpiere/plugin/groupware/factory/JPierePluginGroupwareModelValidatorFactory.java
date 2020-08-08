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
package jpiere.plugin.groupware.factory;

import org.adempiere.base.IModelValidatorFactory;
import org.compiere.model.ModelValidator;

/**
 *  JPIERE-0469
 *  JPiere Plugin Groupware Model validator Factory
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPierePluginGroupwareModelValidatorFactory implements IModelValidatorFactory {

	/**
	 * default constructor
	 */
	public JPierePluginGroupwareModelValidatorFactory() {
	}

	/* (non-Javadoc)
	 * @see org.adempiere.base.IModelValidatorFactory#newModelValidatorInstance(java.lang.String)
	 */
	@Override
	public ModelValidator newModelValidatorInstance(String className) {
		ModelValidator validator = null;

		if (className.startsWith("jpiere.plugin.groupware")) {
			Class<?> clazz = null;

			//use context classloader if available
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader != null) {
				try {
					clazz = classLoader.loadClass(className);
				}
				catch (ClassNotFoundException ex) {
				}
			}
			if (clazz == null) {
				classLoader = this.getClass().getClassLoader();
				try {
					clazz = classLoader.loadClass(className);
				}
				catch (ClassNotFoundException ex) {
				}
			}
			if (clazz != null) {
				try {
					validator = (ModelValidator)clazz.getDeclaredConstructor().newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				new Exception("Failed to load model validator class " + className).printStackTrace();
			}
		}

		return validator;
	}

}
