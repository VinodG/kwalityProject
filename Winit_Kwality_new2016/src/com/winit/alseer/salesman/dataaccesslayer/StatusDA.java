package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.StatusDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.sfa.salesman.MyApplication;

public class StatusDA
{

	/**
	 * Method to insert Option status  information in to Status Table
	 * @param statusinfo
	 */
	public boolean insertOptionStatus(StatusDO statusDO)
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			
			try {
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT Count(*) from tblStatus WHERE Customersite =? and Date =? and Action =? and Type =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblStatus(UUid, Userid, Customersite, Date, Visitcode,JourneyCode,Status,Action,Type) VALUES(?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblStatus SET UUid = ?, Userid = ?, Visitcode = ?, JourneyCode=?, Status=?  where Customersite= ? and Date =? and Action =? and Type =?");
				
				if(statusDO!=null)
				{
					stmtSelectRec.bindString(1, statusDO.Customersite);
					stmtSelectRec.bindString(2, statusDO.Date);
					stmtSelectRec.bindString(3, statusDO.Action);
					stmtSelectRec.bindString(4, statusDO.Type);
					long countRec = stmtSelectRec.simpleQueryForLong();

					if(countRec >0)
					{
						stmtUpdate.bindString(1, statusDO.UUid);
						stmtUpdate.bindString(2, statusDO.Userid);
						stmtUpdate.bindString(3, statusDO.Visitcode);
						stmtUpdate.bindString(4, statusDO.JourneyCode);
						stmtUpdate.bindString(5, statusDO.Status);
						stmtUpdate.bindString(6, statusDO.Customersite);
						stmtUpdate.bindString(7, statusDO.Date);
						stmtUpdate.bindString(8, statusDO.Action);
						stmtUpdate.bindString(9, statusDO.Type);
						
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1, statusDO.UUid);
						stmtInsert.bindString(2, statusDO.Userid);
						stmtInsert.bindString(3, statusDO.Customersite);
						stmtInsert.bindString(4, statusDO.Date);
						stmtInsert.bindString(5, statusDO.Visitcode);
						stmtInsert.bindString(6, statusDO.JourneyCode);
						stmtInsert.bindString(7, statusDO.Status);
						stmtInsert.bindString(8, statusDO.Action);
						stmtInsert.bindString(9, statusDO.Type);
						stmtInsert.executeInsert();
					}
					
					LogUtils.debug("statusDA", "insertOptionStatus"+statusDO.Type);
				}
				
				stmtSelectRec.close();
				stmtUpdate.close();
				stmtInsert.close();
				
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}finally{
				
				if(objSqliteDB!= null)
					objSqliteDB.close();
			}
			return true;
		}
	}
	
	public Vector<StatusDO> getAllOptionsStatus()
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			StatusDO obj = null;
			Vector<StatusDO> vecStatusDO = new Vector<StatusDO>();
			
			try {
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM tblStatus";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						obj = new StatusDO();
						obj.UUid 	 				= cursor.getString(0);
						obj.Userid	 				= cursor.getString(1);
						obj.Customersite		 	= cursor.getString(2);
						obj.Date 					= cursor.getString(3);
						obj.Visitcode 				= cursor.getString(4);
						obj.JourneyCode		 		= cursor.getString(5);
						obj.Status 					= cursor.getString(6);
						obj.Action 					= cursor.getString(7);
						obj.Type 					= cursor.getString(8);
						vecStatusDO.add(obj);
						
					}while(cursor.moveToNext());
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();

				if(mDatabase !=null)
					mDatabase.close();
			}
			return vecStatusDO;
		}
	}
	
	public Vector<String> getCompletedOptionsStatus(String customerId, String action)
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			Vector<String> vecStatusDO = new Vector<String>();
			String Date = CalendarUtils.getCurrentDateAsStringforStoreCheck();
			
			try {
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT Type FROM tblStatus where Customersite = '"+customerId+"' and Action = '"+action+"' and Date like '"+Date+"%' ";//and Status = '"+1+"'
				cursor = mDatabase.rawQuery(query, null);
				LogUtils.debug("statusDA", "getCompletedOptionsStatus");
				if(cursor.moveToFirst())
				{
					do
					{
						String task	 		= cursor.getString(0);
						vecStatusDO.add(task);
						LogUtils.debug("getCompletedOptionsStatus", task);
					}while(cursor.moveToNext());
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();

				if(mDatabase !=null)
					mDatabase.close();
			}
			return vecStatusDO;
		}
	}
	
	/**
	 * Get Un-uploaded Status
	 * @return Vector<StatusDO>
	 */
	public Vector<StatusDO> getUnUploadedStatusDO()
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Vector<StatusDO> vecStatusDO = new Vector<StatusDO>();
			StatusDO objStatusDO = null;
			SQLiteDatabase objSqlite = null;
			Cursor cursor = null;
			try
			{
				objSqlite = DatabaseHelper.openDataBase();
				String query = "SELECT * FROM tblStatus WHERE Status ='0'";
				
				cursor = objSqlite.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						objStatusDO = new StatusDO();
						objStatusDO.UUid 	 				= cursor.getString(0);
						objStatusDO.Userid	 				= cursor.getString(1);
						objStatusDO.Customersite		 	= cursor.getString(2);
						objStatusDO.Date 					= cursor.getString(3);
						objStatusDO.Visitcode 				= cursor.getString(4);
						objStatusDO.JourneyCode		 		= cursor.getString(5);
						objStatusDO.Status 					= cursor.getString(6);
						objStatusDO.Action 					= cursor.getString(7);
						objStatusDO.Type 					= cursor.getString(8);
						vecStatusDO.add(objStatusDO);
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
				if(objSqlite != null && objSqlite.isOpen())
					objSqlite.close();
			}
			return vecStatusDO;
		}
	}
	
	/**
	 * Update tblStatusDO
	 * @param vecStatusDO
	 * @return boolean
	 */
	public boolean updatetblStatus(Vector<StatusDO> vecStatusDO)
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
		
			try {
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				String query = "UPDATE tblStatus SET Status=? where Customersite= ? and Date =? and Action =? and Type =?";
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement(query);
				
				for (int i = 0; i < vecStatusDO.size(); i++) 
				{
					StatusDO statusDO = vecStatusDO.get(i);
					if(statusDO!=null)
					{
						stmtUpdate.bindString(1, "1");
						stmtUpdate.bindString(2, statusDO.Customersite);
						stmtUpdate.bindString(3, statusDO.Date);
						stmtUpdate.bindString(4, statusDO.Action);
						stmtUpdate.bindString(5, statusDO.Type);
						
						stmtUpdate.execute();
					}
				}
				stmtUpdate.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
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
}
