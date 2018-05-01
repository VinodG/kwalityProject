/**
 * 
 */
package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.NotificationDO;
import com.winit.sfa.salesman.MyApplication;

/**
 * @author Aritra.Pal
 *
 */
public class NotificationDA 
{
	/**
	 * Insert Notification
	 * @param objNotificationDO
	 * @return Boolean
	 */
	public boolean insertNotifications(NotificationDO objNotificationDO)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblNotificationMethod(From,MessageTitle,Description,Date) VALUES(?,?,?,?)");
				
				if(objNotificationDO != null)
				{
					stmtInsert.bindString(1, objNotificationDO.From);
					stmtInsert.bindString(2, objNotificationDO.MessageTitle);
					stmtInsert.bindString(3, objNotificationDO.Description);
					stmtInsert.bindString(4, objNotificationDO.Date);
					stmtInsert.executeInsert();
				}
				stmtInsert.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB!= null)
					objSqliteDB.close();
			}
			return true;
		}
	}
	
	/**
	 * Gets All the Notification
	 * @return Vector
	 */
	public Vector<NotificationDO> getAllNotification()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			Vector<NotificationDO> vecNotificationDOs = new Vector<NotificationDO>();
			NotificationDO objNotificationDO = null;
			
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT * from tblNotificationMethod";
				cursor = objSqliteDB.rawQuery(query, null);
				
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						objNotificationDO = new NotificationDO();
						objNotificationDO.From			=	cursor.getString(0);
						objNotificationDO.MessageTitle	=	cursor.getString(1);
						objNotificationDO.Description	=	cursor.getString(2);
						objNotificationDO.Date			=	cursor.getString(3);
						vecNotificationDOs.add(objNotificationDO);
					} while(cursor.moveToNext());
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(objSqliteDB != null && objSqliteDB.isOpen())
					objSqliteDB.close();
			}
			return vecNotificationDOs;
		}
	}
}
