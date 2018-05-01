package com.winit.alseer.salesman.dataaccesslayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.sfa.salesman.MyApplication;

public class EOTDA 
{
	public boolean getEOTDetailsForVansales()
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			boolean isDatatoUpload = false;
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			int statusCount = 0;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String queryTRXHeader = "SELECT COUNT(*) FROM tblTrxHeader WHERE Status='0'"; 
				String queryPaymentHeader = "SELECT COUNT(*) FROM tblPaymentHeader WHERE Status='0'";
				String queryMovementHeader = "SELECT COUNT(*) FROM tblMovementHeader WHERE Status='N'";
				//StoreCheck
				String queryStoreCheckEOT = "SELECT COUNT(*) FROM tblStoreCheck WHERE Status='0'";
				
				cursor = objSqliteDB.rawQuery(queryTRXHeader, null);
				if(cursor != null && cursor.moveToFirst())
				{//Checking tblTrxHeader
					statusCount = cursor.getInt(0);
					if(statusCount <= 0)
					{
						if(cursor != null && !cursor.isClosed())
							cursor.close();
						
						cursor = objSqliteDB.rawQuery(queryPaymentHeader, null);
						if(cursor != null && cursor.moveToFirst())
						{//Checking tblPaymentHeader
							statusCount = cursor.getInt(0);
							if(statusCount <= 0)
							{
								if(cursor != null && !cursor.isClosed())
									cursor.close();
								
								cursor = objSqliteDB.rawQuery(queryMovementHeader, null);
								if(cursor != null && cursor.moveToFirst())
								{//Checking tblMovementHeader
									statusCount = cursor.getInt(0);
									if(cursor != null && !cursor.isClosed())
										cursor.close();
									
									//Checking tblStoreCheck
									cursor = objSqliteDB.rawQuery(queryStoreCheckEOT, null);
									if(cursor != null && cursor.moveToFirst())
									{
										statusCount = cursor.getInt(0);
										if(cursor != null && !cursor.isClosed())
											cursor.close();
									}
								}
							}
						}
					}
				}
				
				if(statusCount > 0)
					isDatatoUpload = true;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return isDatatoUpload;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(objSqliteDB != null && objSqliteDB.isOpen())
					objSqliteDB.close();
			}
			return isDatatoUpload;
		}
	}
	
	public boolean getEOTDetailsForPreSales()
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			boolean isDatatoUpload = false;
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			int statusCount = 0;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String queryTRXHeader = "SELECT COUNT(*) FROM tblTrxHeader WHERE Status='0'"; 
				String queryPaymentHeader = "SELECT COUNT(*) FROM tblPaymentHeader WHERE Status='0'";
				
				//Storecheck
				String queryStoreCheckEOT = "SELECT COUNT(*) FROM tblStoreCheck WHERE Status='0'";
				
				cursor = objSqliteDB.rawQuery(queryTRXHeader, null);
				if(cursor != null && cursor.moveToFirst())
				{//Checking tblTrxHeader
					statusCount = cursor.getInt(0);
					if(statusCount <= 0)
					{
						if(cursor != null && !cursor.isClosed())
							cursor.close();
						
						cursor = objSqliteDB.rawQuery(queryPaymentHeader, null);
						if(cursor != null && cursor.moveToFirst())
						{//Checking tblPaymentHeader
							statusCount = cursor.getInt(0);
							if(cursor != null && !cursor.isClosed())
								cursor.close();
							
							//Checking tblStoreCheck
							cursor = objSqliteDB.rawQuery(queryStoreCheckEOT, null);
							if(cursor != null && cursor.moveToFirst())
							{
								statusCount = cursor.getInt(0);
								if(cursor != null && !cursor.isClosed())
									cursor.close();
							}
						}
					}
				}
				
				if(statusCount > 0)
					isDatatoUpload = true;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return isDatatoUpload;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(objSqliteDB != null && objSqliteDB.isOpen())
					objSqliteDB.close();
			}
			return isDatatoUpload;
		}
	}
	
	public boolean isEOTDone(String userID) 
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteDatabase sqliteDB = null;
			Cursor cursor = null;
			boolean isEOTDone = false;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT COUNT(*) FROM tblTrxLogDetails WHERE TrxType = 'isEOT' AND IsJP = 'True' AND CustomerCode = '"+userID+"' AND Date LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				cursor = sqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					if(cursor.getInt(0) > 0)
						isEOTDone = true;
				}
			}
			catch (Exception ex)
			{
				isEOTDone = true;
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqliteDB != null && sqliteDB.isOpen())
					sqliteDB.close();
			}
			return isEOTDone;
//			return false;
		}
	}
	
	public void insertEOT(String userID,String userName,String status)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteDatabase sqliteDB = null;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				String query = "INSERT INTO tblTrxLogDetails(Date,CustomerCode,CustomerName,TrxType,IsJP) VALUES(?,?,?,?,?)";
				SQLiteStatement stmtInsertEOT = sqliteDB.compileStatement(query);
				
				String querySelect = "SELECT COUNT(*) FROM tblTrxLogDetails WHERE TrxType = 'isEOT' AND CustomerCode = '"+userID+"' AND Date LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				SQLiteStatement stmtSelectRecCat 	= sqliteDB.compileStatement(querySelect);
				
				String queryUpdate = "UPDATE tblTrxLogDetails SET Date=?,CustomerCode=?,CustomerName=?,TrxType=?,IsJP=? WHERE TrxType =? AND CustomerCode =? AND Date =?";
				SQLiteStatement stmtUpdateCat   	= sqliteDB.compileStatement(queryUpdate);
				
				long countRec = stmtSelectRecCat.simpleQueryForLong();
				if (countRec != 0) 
				{
					stmtUpdateCat.bindString(1, CalendarUtils.getCurrentDateAsStringforStoreCheck());
					stmtUpdateCat.bindString(2,	userID);
					stmtUpdateCat.bindString(3, userName);
					stmtUpdateCat.bindString(4, "isEOT");
					stmtUpdateCat.bindString(5, status);
					
					stmtUpdateCat.bindString(6, "isEOT");
					stmtUpdateCat.bindString(7,	userID);
					stmtUpdateCat.bindString(8, CalendarUtils.getCurrentDateAsStringforStoreCheck());
					stmtUpdateCat.executeInsert();
				}
				else
				{
					stmtInsertEOT.bindString(1, CalendarUtils.getCurrentDateAsStringforStoreCheck());
					stmtInsertEOT.bindString(2,	userID);
					stmtInsertEOT.bindString(3, userName);
					stmtInsertEOT.bindString(4, "isEOT");
					stmtInsertEOT.bindString(5, status);
					stmtInsertEOT.executeInsert();
				}
				
				stmtInsertEOT.close();
				stmtUpdateCat.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(sqliteDB != null)
					sqliteDB.close();
			}
		}
	}
	
	public String getSalesmanContactNo(String userID)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteDatabase sqliteDB = null;
			Cursor cursor = null;
			String salesmanContact = "";
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT PhoneNumber FROM tblUsers WHERE UserCode = '"+userID+"'";
				cursor = sqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					salesmanContact = cursor.getString(0);
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
				if(sqliteDB != null && sqliteDB.isOpen())
					sqliteDB.close();
			}
			return salesmanContact;
		}
	}
}
