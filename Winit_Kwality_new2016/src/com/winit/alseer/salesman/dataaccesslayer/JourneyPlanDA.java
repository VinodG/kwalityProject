package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.JouneyStartDO;
import com.winit.alseer.salesman.dataobject.JourneyDO;
import com.winit.alseer.salesman.dataobject.RouteClientDO;
import com.winit.alseer.salesman.dataobject.RouteDO;
import com.winit.alseer.salesman.dataobject.TrxLogHeaders;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.sfa.salesman.MyApplication;

public class JourneyPlanDA 
{
	public boolean insertJourneyPlan(Vector<JourneyDO> vecJourneyPlanDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblJourneyPlan WHERE JourneyPlanId = ?");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblJourneyPlan(JourneyPlanId,UserCode,Name,Description,StartDate, EndDate,ModifiedDate,ModifiedTime,LifeCycle,CreatedBy,ModifiedBy) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblJourneyPlan SET UserCode=?,Name=?,Description=?,StartDate=?, EndDate=?,ModifiedDate=?,ModifiedTime=?,LifeCycle=?,CreatedBy=?,ModifiedBy=? WHERE JourneyPlanId = ?");
				 
				for(int i=0;i<vecJourneyPlanDOs.size();i++)
				{
					JourneyDO userJourneyPlan = vecJourneyPlanDOs.get(i);
					stmtSelectRec.bindLong(1, userJourneyPlan.journeyPlanId);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(userJourneyPlan != null )
						{
							stmtUpdate.bindString(1, userJourneyPlan.UserCode);
							stmtUpdate.bindString(2, userJourneyPlan.Name);
							stmtUpdate.bindString(3, userJourneyPlan.Description);
							stmtUpdate.bindString(4, userJourneyPlan.StartDate);
							stmtUpdate.bindString(5, userJourneyPlan.EndDate);
							stmtUpdate.bindString(6, userJourneyPlan.ModifiedDate);
							stmtUpdate.bindString(7, userJourneyPlan.ModifiedTime);
							stmtUpdate.bindString(8, userJourneyPlan.LifeCycle);
							stmtUpdate.bindString(9, userJourneyPlan.CreatedBy);
							stmtUpdate.bindString(10, userJourneyPlan.ModifiedBy);
							stmtUpdate.bindLong(11, userJourneyPlan.journeyPlanId);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(userJourneyPlan != null )
						{
							stmtInsert.bindLong(1, userJourneyPlan.journeyPlanId);
							stmtInsert.bindString(2, userJourneyPlan.UserCode);
							stmtInsert.bindString(3, userJourneyPlan.Name);
							stmtInsert.bindString(4, userJourneyPlan.Description);
							stmtInsert.bindString(5, userJourneyPlan.StartDate);
							stmtInsert.bindString(6, userJourneyPlan.EndDate);
							stmtInsert.bindString(7, userJourneyPlan.ModifiedDate);
							stmtInsert.bindString(8, userJourneyPlan.ModifiedTime);
							stmtInsert.bindString(9, userJourneyPlan.LifeCycle);
							stmtInsert.bindString(10, userJourneyPlan.CreatedBy);
							stmtInsert.bindString(11, userJourneyPlan.ModifiedBy);
							
							
							stmtInsert.executeInsert();
						}
					}
				}
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result =  false;
			}
			
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	
	public boolean insertRoute(Vector<RouteDO> vecRoute)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblRoute WHERE RouteId = ?");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblRoute(RouteId,Name,Description,JourneyPlanId,StartDay, Status,IsClone,CloneForRouteId,ModifiedDate,ModifiedTime) VALUES(?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblRoute SET Name=?, Description=?,JourneyPlanId=?,StartDate=?, Status=?,IsClone=?,CloneForRouteId=?,ModifiedDate=?,ModifiedTime=?  WHERE RouteId = ?");
				 
				for(int i=0;i<vecRoute.size();i++)
				{
					RouteDO userJourneyPlan = vecRoute.get(i);
					stmtSelectRec.bindLong(1, userJourneyPlan.routeId);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(userJourneyPlan != null )
						{
							stmtUpdate.bindString(1, userJourneyPlan.Name);
							stmtUpdate.bindString(2, userJourneyPlan.Description);
							stmtUpdate.bindString(3, userJourneyPlan.JourneyPlanId);
							stmtUpdate.bindString(4, userJourneyPlan.StartDay);
							stmtUpdate.bindString(5, userJourneyPlan.Status);
							stmtUpdate.bindString(6, userJourneyPlan.IsClone);
							stmtUpdate.bindString(7, userJourneyPlan.CloneForRouteId);
							stmtUpdate.bindString(8, userJourneyPlan.ModifiedDate);
							stmtUpdate.bindString(9, userJourneyPlan.ModifiedTime);
							stmtUpdate.bindLong(10, userJourneyPlan.routeId);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(userJourneyPlan != null )
						{
							stmtInsert.bindLong(1, userJourneyPlan.routeId);
							stmtInsert.bindString(2, userJourneyPlan.Name);
							stmtInsert.bindString(3, userJourneyPlan.Description);
							stmtInsert.bindString(4, userJourneyPlan.JourneyPlanId);
							stmtInsert.bindString(5, userJourneyPlan.StartDay);
							stmtInsert.bindString(6, userJourneyPlan.Status);
							stmtInsert.bindString(7, userJourneyPlan.IsClone);
							stmtInsert.bindString(8, userJourneyPlan.CloneForRouteId);
							stmtInsert.bindString(9, userJourneyPlan.ModifiedDate);
							stmtInsert.bindString(10, userJourneyPlan.ModifiedTime);
							stmtInsert.executeInsert();
						}
					}
				}
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result =  false;
			}
			
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	
	
	public boolean insertRouteClient(Vector<RouteClientDO> vecRoute)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblRouteClient WHERE RouteClientId = ?");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblRouteClient(RouteClientId,RouteId,ClientCode,TimeIn,TimeOut,Sequence, Status,ModifiedDate,ModifiedTime) VALUES(?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblRouteClient SET RouteId=?,ClientCode=?,TimeIn=?,TimeOut=?,Sequence=?, Status=?,ModifiedDate=?,ModifiedTime=?  WHERE RouteClientId = ?");
				 
				for(int i=0;i<vecRoute.size();i++)
				{
					RouteClientDO userJourneyPlan = vecRoute.get(i);
					stmtSelectRec.bindLong(1, userJourneyPlan.RouteClientId);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(userJourneyPlan != null )
						{
							stmtUpdate.bindLong(1, userJourneyPlan.RouteId);
							stmtUpdate.bindString(2, userJourneyPlan.ClientCode);
							stmtUpdate.bindString(3, userJourneyPlan.TimeIn);
							stmtUpdate.bindString(4, userJourneyPlan.TimeOut);
							stmtUpdate.bindString(5, userJourneyPlan.Sequence);
							stmtUpdate.bindString(6, userJourneyPlan.Status);
							stmtUpdate.bindString(7, ""+userJourneyPlan.ModifiedDate);
							stmtUpdate.bindString(8, ""+userJourneyPlan.ModifiedTime);
							stmtUpdate.bindLong(9, userJourneyPlan.RouteClientId);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(userJourneyPlan != null )
						{
							stmtInsert.bindLong(1, userJourneyPlan.RouteClientId);
							stmtInsert.bindLong(2, userJourneyPlan.RouteId);
							stmtInsert.bindString(3, userJourneyPlan.ClientCode);
							stmtInsert.bindString(4, userJourneyPlan.TimeIn);
							stmtInsert.bindString(5, userJourneyPlan.TimeOut);
							stmtInsert.bindString(6, userJourneyPlan.Sequence);
							stmtInsert.bindString(7, userJourneyPlan.Status);
							stmtInsert.bindString(8, ""+userJourneyPlan.ModifiedDate);
							stmtInsert.bindString(9, ""+userJourneyPlan.ModifiedTime);
							stmtInsert.executeInsert();
						}
					}
				}
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result =  false;
			}
			
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return result;
		}
	}

	public boolean insertJourneyStarts(JouneyStartDO journtyStartDo)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblJourney(JourneyId, UserCode,JourneyCode,Date,StartTime,EndTime, TotalTimeInMins," +
													"OdometerReading,IsVanStockVerified,VerifiedBy,JourneyAppId, OdometerReadingStart, OdometerReadingEnd, StoreKeeperSignatureStartDay, SalesmanSignatureStartDay, StoreKeeperSignatureEndDay, SalesmanSignatureEndDay, VehicleCode) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				stmtInsert.bindString(1, journtyStartDo.journeyAppId);
				stmtInsert.bindString(2, journtyStartDo.userCode);
				stmtInsert.bindString(3, journtyStartDo.journeyCode);
				stmtInsert.bindString(4, journtyStartDo.date);
				stmtInsert.bindString(5, journtyStartDo.startTime);
				stmtInsert.bindString(6, journtyStartDo.endTime);
				stmtInsert.bindLong(7, journtyStartDo.TotalTimeInMins);
				stmtInsert.bindString(8, journtyStartDo.OdometerReadingStart);
				stmtInsert.bindString(9, journtyStartDo.IsVanStockVerified);
				stmtInsert.bindString(10, journtyStartDo.VerifiedBy);
				stmtInsert.bindString(11, journtyStartDo.journeyAppId);
				stmtInsert.bindString(12, journtyStartDo.OdometerReadingStart);
				stmtInsert.bindString(13, journtyStartDo.OdometerReadingEnd);
				stmtInsert.bindString(14, journtyStartDo.StoreKeeperSignatureStartDay);
				stmtInsert.bindString(15, journtyStartDo.SalesmanSignatureStartDay);
				stmtInsert.bindString(16, journtyStartDo.StoreKeeperSignatureEndDay);
				stmtInsert.bindString(17, journtyStartDo.SalesmanSignatureEndDay);
				stmtInsert.bindString(18, journtyStartDo.vehicleCode);
				stmtInsert.executeInsert();
				stmtInsert.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result =  false;
			}
			
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	public boolean isOdometerReadingDoneForTheDay(String date){
		synchronized (MyApplication.APP_DB_LOCK) {
			SQLiteDatabase sqLiteDatabase=null;
			boolean isOdometerRedingDone=false;
			Cursor cursor=null;
			try {
				String query="SELECT OdometerReadingStart FROM tblJourney where Date like '"+date+"%' and OdometerReadingStart >0";
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor = sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst()){
					isOdometerRedingDone=true;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return isOdometerRedingDone;
		}
	}
	public boolean updaateJourneyEnds(JouneyStartDO journtyStartDo)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblJourney(JourneyId, UserCode,JourneyCode,Date,StartTime,EndTime, TotalTimeInMins," +
				"OdometerReading,IsVanStockVerified,VerifiedBy,JourneyAppId, OdometerReadingStart, OdometerReadingEnd, StoreKeeperSignatureStartDay, SalesmanSignatureStartDay, StoreKeeperSignatureEndDay, SalesmanSignatureEndDay, VehicleCode) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblJourney SET UserCode =?,Date =?, EndTime =?, TotalTimeInMins =?," +
													"OdometerReading =?, OdometerReadingEnd =?, Status = ?, StoreKeeperSignatureEndDay =?, SalesmanSignatureEndDay = ? WHERE JourneyCode LIKE ?");  
				//StartTime =?,
				int count = 0;
				Cursor cursor = objSqliteDB.rawQuery("SELECT COUNT(*) from tblJourney", null);
				if(cursor!= null && cursor.moveToNext())
					count = cursor.getInt(0);
				cursor.close();
				if(count>0)
				{
					stmtUpdate.bindString(1, journtyStartDo.userCode);
					stmtUpdate.bindString(2, journtyStartDo.date);
//					stmtUpdate.bindString(3, journtyStartDo.startTime);
					stmtUpdate.bindString(3, journtyStartDo.endTime);
					stmtUpdate.bindLong(4, journtyStartDo.TotalTimeInMins);
					stmtUpdate.bindString(5, journtyStartDo.odometerReading);
//					stmtUpdate.bindString(6, journtyStartDo.OdometerReadingStart);
					stmtUpdate.bindString(6, journtyStartDo.OdometerReadingEnd);
					stmtUpdate.bindString(7, "N");
					stmtUpdate.bindString(8, journtyStartDo.StoreKeeperSignatureEndDay);
					stmtUpdate.bindString(9, journtyStartDo.SalesmanSignatureEndDay);
					stmtUpdate.bindString(10, journtyStartDo.journeyCode+"%");
					stmtUpdate.execute();
				}
				else
				{
					stmtInsert.bindString(1, journtyStartDo.journeyAppId);
					stmtInsert.bindString(2, journtyStartDo.userCode);
					stmtInsert.bindString(3, journtyStartDo.journeyCode);
					stmtInsert.bindString(4, journtyStartDo.date);
					stmtInsert.bindString(5, journtyStartDo.startTime);
					stmtInsert.bindString(6, journtyStartDo.endTime);
					stmtInsert.bindLong(7, journtyStartDo.TotalTimeInMins);
					stmtInsert.bindString(8, journtyStartDo.OdometerReadingStart);
					stmtInsert.bindString(9, journtyStartDo.IsVanStockVerified);
					stmtInsert.bindString(10, journtyStartDo.VerifiedBy);
					stmtInsert.bindString(11, journtyStartDo.journeyAppId);
					stmtInsert.bindString(12, journtyStartDo.OdometerReadingStart);
					stmtInsert.bindString(13, journtyStartDo.OdometerReadingEnd);
					stmtInsert.bindString(14, journtyStartDo.StoreKeeperSignatureStartDay);
					stmtInsert.bindString(15, journtyStartDo.SalesmanSignatureStartDay);
					stmtInsert.bindString(16, journtyStartDo.StoreKeeperSignatureEndDay);
					stmtInsert.bindString(17, journtyStartDo.SalesmanSignatureEndDay);
					stmtInsert.bindString(18, journtyStartDo.vehicleCode);
					stmtInsert.executeInsert();
				}
				stmtUpdate.close();
				stmtInsert.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result =  false;
			}
			
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	
	public void updateJourneyStartSignature(int type, String VerifiedSignature, String journeyAppId)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<JouneyStartDO> vecJourney = new Vector<JouneyStartDO>();
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				ContentValues cv = new ContentValues();
				if(type == AppConstants.STORE_SIGN_START)
					cv.put("StoreKeeperSignatureStartDay", VerifiedSignature);
				else if(type == AppConstants.STORE_SIGN_END)
					cv.put("StoreKeeperSignatureEndDay", VerifiedSignature);
				if(type == AppConstants.SALES_SIGN_START)
					cv.put("SalesmanSignatureStartDay", VerifiedSignature);
				else if(type == AppConstants.SALES_SIGN_END)
					cv.put("SalesmanSignatureEndDay", VerifiedSignature);
				
				objSqliteDB.update("tblJourney", cv, "JourneyAppId = ?", new String[]{journeyAppId});
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}

			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	public Vector<JouneyStartDO> getJourneyStart()
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<JouneyStartDO> vecJourney = new Vector<JouneyStartDO>();
			JouneyStartDO jouneyStartDO = null;
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "Select * from tblJourney where IFNULL(Status, 'N') = 'N' limit 1";
				cursor = objSqliteDB.rawQuery(query, null);
				
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						jouneyStartDO  = new JouneyStartDO();
						jouneyStartDO.userCode = cursor.getString(1);
						jouneyStartDO.journeyCode = cursor.getString(2);
						jouneyStartDO.date = cursor.getString(3);
						jouneyStartDO.startTime = cursor.getString(4);
						jouneyStartDO.endTime = cursor.getString(5);
						jouneyStartDO.TotalTimeInMins = cursor.getInt(6);
						jouneyStartDO.odometerReading = cursor.getString(7);
						jouneyStartDO.IsVanStockVerified = cursor.getString(8);
						jouneyStartDO.VerifiedBy = cursor.getString(9);
						jouneyStartDO.journeyAppId = cursor.getString(10);
						jouneyStartDO.OdometerReadingStart = cursor.getString(13);
						jouneyStartDO.OdometerReadingEnd = cursor.getString(14);
						jouneyStartDO.StoreKeeperSignatureStartDay = cursor.getString(15);
						jouneyStartDO.SalesmanSignatureStartDay = cursor.getString(16);
						jouneyStartDO.StoreKeeperSignatureEndDay = cursor.getString(17);
						jouneyStartDO.SalesmanSignatureEndDay = cursor.getString(18);
						jouneyStartDO.vehicleCode = cursor.getString(19);
						vecJourney.add(jouneyStartDO);
					}while(cursor.moveToNext());
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}

			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(objSqliteDB != null && objSqliteDB.isOpen())
					objSqliteDB.close();
			}
			return vecJourney;
		}
	}
	
	public void updateJourneyStartUploadStatus(boolean isUploaded, String journeyAppId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				ContentValues cv = new ContentValues();
				if(isUploaded)
					cv.put("Status", "Y");
				else
					cv.put("Status", "N");
				objSqliteDB.update("tblJourney", cv, "JourneyAppId = ?", new String[]{journeyAppId});
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}

			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	public boolean insertDailyRouteClient(Vector<RouteClientDO> vecRoute)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
				
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblDailyJourneyPlan(UserCode,ClientCode,JourneyDate,RouteClientId,StartTime,EndTime," +
																				"Sequence,VisitStatus,VisitCode,IsDeleted,ModifiedDate,ModifiedTime) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = null;
								 
				 
				for(RouteClientDO userJourneyPlan : vecRoute)
				{
					
					String date = "";
					
					if(userJourneyPlan.JourneyDate.contains("T"))
						date = userJourneyPlan.JourneyDate.split("T")[0];
					else
						date = userJourneyPlan.JourneyDate;
					
					SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblDailyJourneyPlan WHERE UserCode=? AND ClientCode = ? AND JourneyDate like '%"+date+"%'");
					stmtSelectRec.bindString(1, userJourneyPlan.UserCode);
					stmtSelectRec.bindString(2, userJourneyPlan.ClientCode);
//					stmtSelectRec.bindString(3, userJourneyPlan.JourneyDate);
					
					stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblDailyJourneyPlan SET RouteClientId=?,StartTime=?,EndTime=?," +
							"Sequence=?,IsDeleted=?,ModifiedDate=?,ModifiedTime=? WHERE UserCode=? AND ClientCode = ? AND JourneyDate like '%"+date+"%'");
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						stmtUpdate.bindString(1, ""+userJourneyPlan.RouteClientId);
						stmtUpdate.bindString(2, ""+userJourneyPlan.TimeIn);
						stmtUpdate.bindString(3, ""+userJourneyPlan.TimeOut);
						stmtUpdate.bindString(4, ""+userJourneyPlan.Sequence);
						stmtUpdate.bindString(5, ""+userJourneyPlan.IsDeleted);
						stmtUpdate.bindString(6, ""+userJourneyPlan.ModifiedDate);
						stmtUpdate.bindString(7, ""+userJourneyPlan.ModifiedTime);
						stmtUpdate.bindString(8, ""+userJourneyPlan.UserCode);
						stmtUpdate.bindString(9,""+userJourneyPlan.ClientCode);
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1, ""+userJourneyPlan.UserCode);
						stmtInsert.bindString(2, ""+userJourneyPlan.ClientCode);
						stmtInsert.bindString(3, ""+userJourneyPlan.JourneyDate);
						stmtInsert.bindString(4, ""+userJourneyPlan.RouteClientId);
						stmtInsert.bindString(5, ""+userJourneyPlan.TimeIn);
						stmtInsert.bindString(6, ""+userJourneyPlan.TimeOut);
						stmtInsert.bindString(7, ""+userJourneyPlan.Sequence);
						stmtInsert.bindString(8, ""+userJourneyPlan.Status);
						stmtInsert.bindString(9, ""+userJourneyPlan.VisitCode);
						stmtInsert.bindString(10, ""+userJourneyPlan.IsDeleted);
						stmtInsert.bindString(11, ""+userJourneyPlan.ModifiedDate);
						stmtInsert.bindString(12, ""+userJourneyPlan.ModifiedTime);
						stmtInsert.executeInsert();
					}
					stmtSelectRec.close();
				}
				stmtInsert.close();
				if(stmtUpdate != null)
					stmtUpdate.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result =  false;
			}
			
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			
			//Adding Scheduled call from sync
			checkAndInsertForTodayLogReport();
			return result;
		}
	}
	
	private void checkAndInsertForTodayLogReport()
	{
		TrxLogHeaders header = new TrxLogHeaders();
		if(new TransactionsLogsDA().getTransactionsLogCount(CalendarUtils.getOrderPostDate())>0)
		{
			header.TotalScheduledCalls=new CustomerDetailsDA().getJourneyPlanCount();
			header.TrxDate = CalendarUtils.getOrderPostDate();
			Vector<TrxLogHeaders> vecTrxLogHeaders= new Vector<TrxLogHeaders>();
			vecTrxLogHeaders.add(header);
			new TransactionsLogsDA().updateScheduledCallTrxLogHeaders(vecTrxLogHeaders);
		}
		else
		{
			header.TotalActualCalls = 0;
			header.TotalProductiveCalls=0;
			header.TotalCollections=0;
			header.TotalCreditNotes=0;
			header.TotalSales=0;
			header.TotalScheduledCalls=new CustomerDetailsDA().getJourneyPlanCount();
			header.CurrentMonthlySales=0;
			header.TrxDate = CalendarUtils.getOrderPostDate();
			Vector<TrxLogHeaders> vecTrxLogHeaders= new Vector<TrxLogHeaders>();
			vecTrxLogHeaders.add(header);
			new TransactionsLogsDA().insertTrxLogHeaders(vecTrxLogHeaders);
		}
		
	}
	
	public int isDayStarted(String userCode)
	{
		synchronized (MyApplication.MyLock)
		{
			int dayStarted = 0;
			SQLiteDatabase sqliteDB = null;
			Cursor cursor = null;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT COUNT(*) FROM tblJourney WHERE UserCode='"+userCode+"' AND StartTime LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				
				cursor = sqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					dayStarted = cursor.getInt(0);
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
			return dayStarted;
		}
	}
}
