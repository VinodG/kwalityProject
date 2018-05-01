package com.winit.alseer.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.CategorieDO;
import com.winit.alseer.salesman.dataobject.InventoryGroupDO;
import com.winit.alseer.salesman.dataobject.ServiceCaptureDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.MyApplication;

public class StoreCheckDA 
{
	// visit store value
	public int getVisitvalue(String siteid,String date) 
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		int count=0;
		synchronized(MyApplication.MyLock) 
		{
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				String query = "select (TSC.TotalApplicableCount* 100)/TSC.TotalCount AS GoldenStore from tblStoreCheck TSC INNER JOIN tblDailyJourneyPlan TDJ ON TSC.ClientCode = TDJ.ClientCode where TSC.ClientCode ='"+siteid+"' and TSC.Date  like '"+date+"%' and TDJ.JourneyDate like '"+date+"%'";
				cursor  = mDatabase.rawQuery(query, null);

				if(cursor != null && cursor.getCount() > 0)
				{
					if(cursor.moveToFirst())
					{
						count=Integer.valueOf(cursor.getString(0));
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
				if(mDatabase != null)
					mDatabase.close();
			}
		}
		return count;
	}
	
	//get golden value
	public int getGoldenstoreCount() 
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		int goldencount=0;
		synchronized(MyApplication.MyLock) 
		{
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("select SettingValue from tblSettings where SettingName = 'PerfectStorePercentage'", null);
				if(cursor != null && cursor.getCount() > 0)
				{
					if(cursor.moveToFirst())
					{
						goldencount=Integer.valueOf(cursor.getString(0));
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
				if(mDatabase != null)
					mDatabase.close();
			}
		}
		return goldencount;
	}
	
	public void insertStoreCheckGroup(Vector<InventoryGroupDO> vecGroupDOs)
	{
		synchronized(MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				 
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblStoreCheckGroup (StorecheckGroupId, StoreCheckId, ItemGroupLevel,ItemGroupLevelName, TotalCount, TotalNotAvailableCount) VALUES(?,?,?,?,?,?)");
				
				if(vecGroupDOs != null)
				{
					for (InventoryGroupDO objinveInventoryGroupDO : vecGroupDOs) 
					{
						stmtInsert.bindString(1, objinveInventoryGroupDO.StorecheckGroupId);
						stmtInsert.bindString(2, objinveInventoryGroupDO.StoreCheckId);
						stmtInsert.bindString(3, objinveInventoryGroupDO.ItemGroupLevel);
						stmtInsert.bindString(4, objinveInventoryGroupDO.ItemGroupLevelName);
						stmtInsert.bindLong(5, objinveInventoryGroupDO.TotalCount);
						stmtInsert.bindLong(6, objinveInventoryGroupDO.TotalNotAvailableCount);
					}
					stmtInsert.executeInsert();
					stmtInsert.close();
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	/**
	 *Get Store Check categories 
	 * @return Vector<CategorieDO>
	 */
	public Vector<CategorieDO> getStoreCheckCategories(String clientCode) 
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		Vector<CategorieDO> vecCategorieDO = new Vector<CategorieDO>();
		CategorieDO objCategorie = null;
		synchronized(MyApplication.MyLock) 
		{
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				String checkedCategories="";
				String qyery1="SELECT group_concat(distinct CategoryCode) FROM tblStoreCheckItem TSCI inner join tblStoreCheck TSC on TSCI.StoreCheckId=TSC.StoreCheckId and TSC.ClientCode='"+clientCode+"' and Date like '"+CalendarUtils.getOrderPostDate()+"%'";
				cursor  = mDatabase.rawQuery(qyery1, null);
				if(cursor!=null && cursor.moveToFirst()){
					checkedCategories = cursor.getString(0);
				}
				ArrayList<String> vecCategories = new ArrayList<String>();
				if(!TextUtils.isEmpty(checkedCategories) && checkedCategories.contains(",")){
					String arr[]=checkedCategories.split(",");
					vecCategories.addAll(Arrays.asList(arr));
				}else if(!TextUtils.isEmpty(checkedCategories)){
					vecCategories.add(checkedCategories);
				}
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				String query = "SELECT CategoryId, CategoryName, CategoryIcon FROM tblCategory where CategoryId in(select distinct category from tblProducts) ";
				cursor  = mDatabase.rawQuery(query, null);
				if(cursor != null && cursor.getCount() > 0)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							objCategorie				= new CategorieDO();
							objCategorie.CategoryId		=	cursor.getString(0);
							objCategorie.CategoryName	=	cursor.getString(1);
							objCategorie.CategoryIcon	=	cursor.getString(2);
							if(vecCategories.contains(objCategorie.CategoryId))
								objCategorie.isDone=true;
							vecCategorieDO.add(objCategorie);
						}while(cursor.moveToNext());
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
				if(mDatabase != null)
					mDatabase.close();
			}
		}
		return vecCategorieDO;
	}

	/**
	 * Get Store Check Id if Store Check already done
	 * @param site
	 * @return String
	 */
	public String getStoreCheckID(String site) 
	{
		synchronized (MyApplication.MyLock)
		{
			String storeCheckID = "";
			SQLiteDatabase objSQLite = null;
			Cursor cursor = null;
			try
			{
				objSQLite = DatabaseHelper.openDataBase();
				String query = "SELECT DISTINCT StoreCheckId FROM tblStoreCheck WHERE ClientCode = '"+site+"'";
				cursor	=	objSQLite.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					storeCheckID	=	cursor.getString(0);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && cursor.isClosed())
				{
					cursor.close();
				}
			}
			return storeCheckID;
		}
	}
	
	public void insertServiceImage(String UserCode,String CustomerCode,String imagePath,int SERVICE_TYPE)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT COUNT(*) FROM tblServiceCapture WHERE UserCode='"+UserCode+"' " +
						"AND CustomerCode='"+CustomerCode+"' AND CreatedDate LIKE'"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				cursor = objSqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					if(cursor.getInt(0) > 0)
					{
						String queryUpdate = "";
						if(SERVICE_TYPE == AppConstants.CAMERA_PIC_BEFORE_SERVICE)
							queryUpdate = "UPDATE tblServiceCapture SET BeforeImage=?,Status=?,CreatedDate=? WHERE UserCode=? AND CustomerCode=? AND CreatedDate LIKE ?";
						else if(SERVICE_TYPE == AppConstants.CAMERA_PIC_AFTER_SERVICE)
							queryUpdate = "UPDATE tblServiceCapture SET AfterImage=?,Status=?,CreatedDate=? WHERE UserCode=? AND CustomerCode=? AND CreatedDate LIKE ?";
						
						SQLiteStatement stmtUpdate = objSqliteDB.compileStatement(queryUpdate);
						stmtUpdate.bindString(1, imagePath);
						if(SERVICE_TYPE == AppConstants.CAMERA_PIC_BEFORE_SERVICE)
							stmtUpdate.bindLong(2, 1);
						else if(SERVICE_TYPE == AppConstants.CAMERA_PIC_AFTER_SERVICE)
							stmtUpdate.bindLong(2, 3);

						stmtUpdate.bindString(3, CalendarUtils.getCurrentDateTime());
						stmtUpdate.bindString(4, UserCode);
						stmtUpdate.bindString(5, CustomerCode);
						stmtUpdate.bindString(6, CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%");
						
						stmtUpdate.execute();
						stmtUpdate.close();
					}
					else
					{
						String insertQuery = "";
						int status = 1;
						if(SERVICE_TYPE == AppConstants.CAMERA_PIC_BEFORE_SERVICE)
						{
							insertQuery = "INSERT INTO tblServiceCapture (UserCode, CustomerCode, BeforeImage, CreatedDate,Status) VALUES(?,?,?,?,?)";
							status = 1;
						}
						else if(SERVICE_TYPE == AppConstants.CAMERA_PIC_AFTER_SERVICE)
						{
							insertQuery = "INSERT INTO tblServiceCapture (UserCode, CustomerCode, AfterImage, CreatedDate,Status) VALUES(?,?,?,?,?)";
							status = 3;							
						}
						
						SQLiteStatement stmtInsert = objSqliteDB.compileStatement(insertQuery);
						stmtInsert.bindString(1, UserCode);
						stmtInsert.bindString(2, CustomerCode);
						stmtInsert.bindString(3, imagePath);
//						stmtInsert.bindString(4, CalendarUtils.getCurrentDateAsStringforStoreCheck());
						stmtInsert.bindString(4, CalendarUtils.getCurrentDateTime());
						stmtInsert.bindLong(5, status);
						
						stmtInsert.executeInsert();
						stmtInsert.close();
					}
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
		}
	}
	
	public void updateServicecapture(Vector<ServiceCaptureDO> vecServicecapture)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqliteDB = null;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				if(vecServicecapture != null && vecServicecapture.size() > 0)
				{
					for (int i = 0; i < vecServicecapture.size(); i++) 
					{
						String queryUpdate = "UPDATE tblServiceCapture SET BeforeImage=?,AfterImage=?,Status=? WHERE UserCode=? AND CustomerCode=? AND CreatedDate LIKE ?";
						SQLiteStatement stmtUpdate = sqliteDB.compileStatement(queryUpdate);
						
						if(vecServicecapture.get(i).BeforeImage != null)
							stmtUpdate.bindString(1, vecServicecapture.get(i).BeforeImage);
						else
							stmtUpdate.bindString(1, "");
						
						if(vecServicecapture.get(i).AfterImage != null)
							stmtUpdate.bindString(2, vecServicecapture.get(i).AfterImage);
						else
							stmtUpdate.bindString(2, "");
						
						if(vecServicecapture.get(i).Status == 1)
							stmtUpdate.bindLong(3, 2);
						else if(vecServicecapture.get(i).Status == 3)
							stmtUpdate.bindLong(3, 4);

						stmtUpdate.bindString(4, vecServicecapture.get(i).UserCode);
						stmtUpdate.bindString(5, vecServicecapture.get(i).CustomerCode);
						stmtUpdate.bindString(6, vecServicecapture.get(i).CreatedDate+"%");
						
						stmtUpdate.execute();
						stmtUpdate.close();
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(sqliteDB != null && sqliteDB.isOpen())
					sqliteDB.close();
			}
		}
	}
	
	public void updateServicecaptureDO(ServiceCaptureDO objServicecapture)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqliteDB = null;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				if(objServicecapture != null)
				{
					String queryUpdate = "UPDATE tblServiceCapture SET BeforeImage=?,AfterImage=?,Status=? WHERE UserCode=? AND CustomerCode=? AND CreatedDate LIKE ?";
					SQLiteStatement stmtUpdate = sqliteDB.compileStatement(queryUpdate);
					
					if(objServicecapture.BeforeImage != null)
						stmtUpdate.bindString(1, objServicecapture.BeforeImage);
					else
						stmtUpdate.bindString(1, "");
					
					if(objServicecapture.AfterImage != null)
						stmtUpdate.bindString(2, objServicecapture.AfterImage);
					else
						stmtUpdate.bindString(2, "");
					
					stmtUpdate.bindLong(3, objServicecapture.Status);
					stmtUpdate.bindString(4, objServicecapture.UserCode);
					stmtUpdate.bindString(5, objServicecapture.CustomerCode);
					stmtUpdate.bindString(6, objServicecapture.CreatedDate+"%");
					
					stmtUpdate.execute();
					stmtUpdate.close();
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(sqliteDB != null && sqliteDB.isOpen())
					sqliteDB.close();
			}
		}
	}
	
	public Vector<ServiceCaptureDO> getServiceCapture()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqliteDB = null;
			Cursor cursor = null;
			Vector<ServiceCaptureDO> vecServiceCaptureDOs = null;
			ServiceCaptureDO objServiceCaptureDO = null;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT * FROM tblServiceCapture WHERE (Status=1 OR Status=3)";
				cursor = sqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					vecServiceCaptureDOs = new Vector<ServiceCaptureDO>();
					do
					{
						objServiceCaptureDO = new ServiceCaptureDO();
						
						objServiceCaptureDO.UserCode 		= cursor.getString(0);
						objServiceCaptureDO.CustomerCode 	= cursor.getString(1);
						objServiceCaptureDO.BeforeImage 	= cursor.getString(2);
						objServiceCaptureDO.AfterImage 		= cursor.getString(3);
						objServiceCaptureDO.CreatedDate 	= cursor.getString(4);
						objServiceCaptureDO.Status		 	= cursor.getInt(5);
						
						vecServiceCaptureDOs.add(objServiceCaptureDO);
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
					cursor .close();
				if(sqliteDB != null && sqliteDB.isOpen())
					sqliteDB.close();
			}
			return vecServiceCaptureDOs;//tblCustomerVisit
		}
	}
	
	public int getServiceCapturePending(String userCode,String CustomerCode)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqliteDB = null;
			Cursor cursor = null;
			int count = 0;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				String query = "";
				query = "SELECT COUNT(*) FROM tblServiceCapture WHERE UserCode='"+userCode+"' AND CustomerCode='"+CustomerCode+"'";
				
				cursor = sqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					count = cursor.getInt(0);
					if(count == 0)
						count = 1;
					else
					{
						if(cursor != null && !cursor.isClosed())
							cursor .close();
						
						query = "SELECT COUNT(*) FROM tblServiceCapture " +
								"WHERE (Status!=3 AND Status!=4) AND UserCode='"+userCode+"' " +
								"AND CustomerCode='"+CustomerCode+"'";
						cursor = sqliteDB.rawQuery(query, null);
						if(cursor != null && cursor.moveToFirst())
						{
							count = cursor.getInt(0);
						}
						if(cursor != null && !cursor.isClosed())
							cursor .close();
						
						if(count == 0)
						{
//							query = "SELECT CreatedDate FROM tblServiceCapture " +
//									"WHERE (Status=3 OR Status=4) AND UserCode='"+userCode+"' AND CustomerCode='"+CustomerCode+"'";
							
//							cursor = sqliteDB.rawQuery(query, null);
//							String createdDate = "";
//							if(cursor != null && cursor.moveToFirst())
//							{
//								createdDate = cursor.getString(0);
//								if(CalendarUtils.getDiffBtwDatesInMinutes(createdDate, CalendarUtils.getCurrentDateTime()) > 2)
//									count = 1;
//							}
							
							query = "SELECT COUNT(*) " +
									"FROM tblCustomerVisit CV " +
									"INNER JOIN tblServiceCapture SC ON CV.UserCode=SC.UserCode " +
									"AND CV.ClientCode=SC.CustomerCode AND CV.ArrivalTime > SC.CreatedDate " +
									"WHERE SC.CustomerCode ='"+CustomerCode+"' " +
									"AND SC.UserCode = '"+userCode+"' " +
									"AND SC.CreatedDate LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%' " +
									"AND CV.ArrivalTime LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%' " +
									"AND (SC.Status=3 OR SC.Status=4)";
							cursor = sqliteDB.rawQuery(query, null);
							String createdDate = "";
							if(cursor != null && cursor.moveToFirst())
							{
								createdDate = cursor.getString(0);
								if(StringUtils.getInt(createdDate) > 0)
									count = 1;
							}
						}
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor .close();
				if(sqliteDB != null && sqliteDB.isOpen())
					sqliteDB.close();
			}
			return count;
		}
	}
	
	public void deleteServiceCapture(Vector<ServiceCaptureDO> vecServiceCaptureDOs) 
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objsqlite = null;
			Cursor cursor = null;
			try
			{
				objsqlite = DatabaseHelper.openDataBase();
				for (int i = 0; i < vecServiceCaptureDOs.size(); i++)
				{
					String query = "DELETE FROM tblServiceCapture WHERE UserCode='"+vecServiceCaptureDOs.get(i).UserCode+"' " +
							"AND CustomerCode='"+vecServiceCaptureDOs.get(i).UserCode+"'";
					objsqlite.execSQL(query);
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
				if(objsqlite != null && objsqlite.isOpen())
					objsqlite.close();
			}
		}
	}
}
