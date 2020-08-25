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

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Util;

import jpiere.plugin.groupware.model.MJPToDoTeam;


/**
 *  JPIERE-0469
 *  JPiere Plugin Groupware Model Factory
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPierePluginGroupwareModelFactory implements IModelFactory {

	private static CCache<String,Class<?>> s_classCache = new CCache<String,Class<?>>(null, "PO_Class", 20, false);
	private final static CLogger s_log = CLogger.getCLogger(JPierePluginGroupwareModelFactory.class);

	@Override
	public Class<?> getClass(String tableName)
	{
		if(tableName.startsWith("JP_ToDo") ||  tableName.startsWith("JP_Team") || tableName.startsWith("JP_Groupware"))
		{
			if (tableName.endsWith("_Trl"))
				return null;

			//check cache
			Class<?> cache = s_classCache.get(tableName);
			if (cache != null)
			{
				//Object.class indicate no generated PO class for tableName
				if (cache.equals(Object.class))
					return null;
				else
					return cache;
			}

			String className = tableName;
			int index = className.indexOf('_');
			if (index > 0)
			{
				if (index < 3)		//	AD_, A_
					 className = className.substring(index+1);
				/* DELETEME: this part is useless - teo_sarca, [ 1648850 ]
				else
				{
					String prefix = className.substring(0,index);
					if (prefix.equals("Fact"))		//	keep custom prefix
						className = className.substring(index+1);
				}
				*/
			}
			//	Remove underlines
			className = Util.replace(className, "_", "");

			//	Search packages
			StringBuffer name = new StringBuffer("jpiere.plugin.groupware.model").append(".M").append(className);
			Class<?> clazz = getPOclass(name.toString(), tableName);
			if (clazz != null)
			{
				s_classCache.put(tableName, clazz);
				return clazz;
			}


			//	Adempiere Extension
			clazz = getPOclass("jpiere.plugin.groupware.model.X_" + tableName, tableName);
			if (clazz != null)
			{
				s_classCache.put(tableName, clazz);
				return clazz;
			}

		}else if(tableName.equals("RV_JP_ToDo_Team")) {

			return MJPToDoTeam.class;
		}

		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {

		if(tableName.startsWith("JP_ToDo") ||  tableName.startsWith("JP_Team"))
		{
			Class<?> clazz = getClass(tableName);
			if (clazz == null)
			{
				return null;
			}

			boolean errorLogged = false;
			try
			{
				Constructor<?> constructor = null;
				try
				{
					constructor = clazz.getDeclaredConstructor(new Class[]{Properties.class, int.class, String.class});
				}
				catch (Exception e)
				{
					String msg = e.getMessage();
					if (msg == null)
						msg = e.toString();
					s_log.warning("No transaction Constructor for " + clazz + " (" + msg + ")");
				}

				PO po = constructor!=null ? (PO)constructor.newInstance(new Object[] {Env.getCtx(), Integer.valueOf(Record_ID), trxName}) : null;
				return po;
			}
			catch (Exception e)
			{
				if (e.getCause() != null)
				{
					Throwable t = e.getCause();
					s_log.log(Level.SEVERE, "(id) - Table=" + tableName + ",Class=" + clazz, t);
					errorLogged = true;
					if (t instanceof Exception)
						s_log.saveError("Error", (Exception)e.getCause());
					else
						s_log.saveError("Error", "Table=" + tableName + ",Class=" + clazz);
				}
				else
				{
					s_log.log(Level.SEVERE, "(id) - Table=" + tableName + ",Class=" + clazz, e);
					errorLogged = true;
					s_log.saveError("Error", "Table=" + tableName + ",Class=" + clazz);
				}
			}
			if (!errorLogged)
				s_log.log(Level.SEVERE, "(id) - Not found - Table=" + tableName
					+ ", Record_ID=" + Record_ID);

			return null;

		}else if(tableName.equals("RV_JP_ToDo_Team")) {

			return new MJPToDoTeam(Env.getCtx(), Record_ID, trxName);
		}

		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {

		if(tableName.startsWith("JP_ToDo") ||  tableName.startsWith("JP_Team"))
		{
			Class<?> clazz = getClass(tableName);
			if (clazz == null)
			{
				return null;
			}

			boolean errorLogged = false;
			try
			{
				Constructor<?> constructor = clazz.getDeclaredConstructor(new Class[]{Properties.class, ResultSet.class, String.class});
				PO po = (PO)constructor.newInstance(new Object[] {Env.getCtx(), rs, trxName});
				return po;
			}
			catch (Exception e)
			{
				s_log.log(Level.SEVERE, "(rs) - Table=" + tableName + ",Class=" + clazz, e);
				errorLogged = true;
				s_log.saveError("Error", "Table=" + tableName + ",Class=" + clazz);
			}
			if (!errorLogged)
				s_log.log(Level.SEVERE, "(rs) - Not found - Table=" + tableName);

			return null;

		}else if(tableName.equals("RV_JP_ToDo_Team")) {

			return new MJPToDoTeam(Env.getCtx(), rs, trxName);
		}

		return null;
	}


	/**
	 * Get PO class
	 * @param className fully qualified class name
	 * @param tableName Optional. If specified, the loaded class will be validated for that table name
	 * @return class or null
	 */
	private Class<?> getPOclass (String className, String tableName)
	{
		try
		{
			Class<?> clazz = Class.forName(className);
			// Validate if the class is for specified tableName
			if (tableName != null)
			{
				String classTableName = clazz.getField("Table_Name").get(null).toString();
				if (!tableName.equals(classTableName))
				{
					if (s_log.isLoggable(Level.FINEST)) s_log.finest("Invalid class for table: " + className+" (tableName="+tableName+", classTableName="+classTableName+")");
					return null;
				}
			}
			//	Make sure that it is a PO class
			Class<?> superClazz = clazz.getSuperclass();
			while (superClazz != null)
			{
				if (superClazz == PO.class)
				{
					if (s_log.isLoggable(Level.FINE)) s_log.fine("Use: " + className);
					return clazz;
				}
				superClazz = superClazz.getSuperclass();
			}
		}
		catch (Exception e)
		{
		}
		if (s_log.isLoggable(Level.FINEST)) s_log.finest("Not found: " + className);
		return null;
	}	//	getPOclass
}
