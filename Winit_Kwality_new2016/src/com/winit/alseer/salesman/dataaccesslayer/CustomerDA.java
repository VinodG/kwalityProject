package com.winit.alseer.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.internal.cu;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.alseer.salesman.dataobject.CustomerDao;
import com.winit.alseer.salesman.dataobject.CustomerSpecialDayDO;
import com.winit.alseer.salesman.dataobject.CustomerVisitDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.MonthlyDataDO;
import com.winit.alseer.salesman.dataobject.SurveyCustomerDeatislDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.MyApplication;


public class CustomerDA 
{
	public boolean insertCutomers(ArrayList<CustomerDao> arCustomerDao)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblCustomer(id, Site, SiteName, CustomerId, CustomerStatus,CustAcctCreationDate," +
						"PartyName, ChannelCode,SubChannelCode,RegionCode,CountryCode,Category,Address1,Address2,Address3,Address4,PoNumber, City,PaymentType,PaymentTermCode," +
						"CreditLimit,GeoCodeX,GeoCodeY,PASSCODE,Email,ContactPersonName,PhoneNumber,AppCustomerId,MobileNumber1,MobileNumber2,Website,CustomerType," +
						"CreatedBy,ModifiedBy,Source,CustomerCategory,CustomerSubCategory,CustomerGroupCode) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomer WHERE id = ?");
				
				
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblCustomer SET Site=?, SiteName=?, CustomerId=?, CustomerStatus=?,CustAcctCreationDate=?," +
						"PartyName=?, ChannelCode=?,SubChannelCode=?,RegionCode=?,CountryCode=?,Category=?,Address1=?,Address2=?,Address3=?,Address4=?,PoNumber=?, City=?,PaymentType=?,PaymentTermCode=?," +
						"CreditLimit=?,GeoCodeX=?,GeoCodeY=?,PASSCODE=?,Email=?,ContactPersonName=?,PhoneNumber=?,AppCustomerId=?,MobileNumber1=?,MobileNumber2=?,Website=?,CustomerType=?," +
						"CreatedBy=?,ModifiedBy=?,Source=?,CustomerCategory=?,CustomerSubCategory=?,CustomerGroupCode=?  WHERE id = ?");
				 
				for(int i=0;i<arCustomerDao.size();i++)
				{
					CustomerDao userJourneyPlan = arCustomerDao.get(i);
					stmtSelectRec.bindLong(1, userJourneyPlan.id);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(userJourneyPlan != null )
						{
							stmtUpdate.bindString(1, userJourneyPlan.site);
							stmtUpdate.bindString(2, userJourneyPlan.SiteName);
							stmtUpdate.bindString(3, userJourneyPlan.CustomerId);
							stmtUpdate.bindString(4, userJourneyPlan.CustomerStatus);
							stmtUpdate.bindString(5, userJourneyPlan.CustAcctCreationDate+"");
							stmtUpdate.bindString(6, userJourneyPlan.PartyName);
							stmtUpdate.bindString(7, userJourneyPlan.ChannelCode);
							stmtUpdate.bindString(8, userJourneyPlan.SubChannelCode);
							stmtUpdate.bindString(9, userJourneyPlan.RegionCode);
							stmtUpdate.bindString(10, userJourneyPlan.CountryCode);
							stmtUpdate.bindString(11, userJourneyPlan.Category);
							stmtUpdate.bindString(12, userJourneyPlan.Address1);
							stmtUpdate.bindString(13, userJourneyPlan.Address2);
							stmtUpdate.bindString(14, userJourneyPlan.Address3);
							stmtUpdate.bindString(15, userJourneyPlan.Address4);
							stmtUpdate.bindString(16, userJourneyPlan.PoNumber);
							stmtUpdate.bindString(17, userJourneyPlan.City);
							stmtUpdate.bindString(18, userJourneyPlan.PaymentType);
							stmtUpdate.bindString(19, userJourneyPlan.PaymentTermCode);
							stmtUpdate.bindString(20, userJourneyPlan.CreditLimit);
							stmtUpdate.bindString(21, userJourneyPlan.GeoCodeX);
							stmtUpdate.bindString(22, userJourneyPlan.GeoCodeY);
							stmtUpdate.bindString(23, userJourneyPlan.PASSCODE);
							stmtUpdate.bindString(24, userJourneyPlan.Email);
							stmtUpdate.bindString(25, userJourneyPlan.ContactPersonName);
							stmtUpdate.bindString(26, userJourneyPlan.PhoneNumber);
							stmtUpdate.bindString(27, userJourneyPlan.AppCustomerId);
							stmtUpdate.bindString(28, userJourneyPlan.MobileNumber1);
							stmtUpdate.bindString(29, userJourneyPlan.MobileNumber2);
							stmtUpdate.bindString(30, userJourneyPlan.Website);
							stmtUpdate.bindString(31, userJourneyPlan.CustomerType);
							stmtUpdate.bindString(32, userJourneyPlan.CreatedBy);
							stmtUpdate.bindString(33, userJourneyPlan.ModifiedBy);
							stmtUpdate.bindString(34, userJourneyPlan.Source);
							stmtUpdate.bindString(35, userJourneyPlan.CustomerCategory);
							stmtUpdate.bindString(36, userJourneyPlan.CustomerSubCategory);
							stmtUpdate.bindString(37, userJourneyPlan.CustomerGroupCode);
							stmtUpdate.bindLong(38, userJourneyPlan.id);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(userJourneyPlan != null )
						{
							stmtInsert.bindLong(1, userJourneyPlan.id);
							stmtInsert.bindString(2, userJourneyPlan.site);
							stmtInsert.bindString(3, userJourneyPlan.SiteName);
							stmtInsert.bindString(4, userJourneyPlan.CustomerId);
							stmtInsert.bindString(5, userJourneyPlan.CustomerStatus);
							stmtInsert.bindString(6, userJourneyPlan.CustAcctCreationDate+"");
							stmtInsert.bindString(7, userJourneyPlan.PartyName);
							stmtInsert.bindString(8, userJourneyPlan.ChannelCode);
							stmtInsert.bindString(9, userJourneyPlan.SubChannelCode);
							stmtInsert.bindString(10, userJourneyPlan.RegionCode);
							stmtInsert.bindString(11, userJourneyPlan.CountryCode);
							stmtInsert.bindString(12, userJourneyPlan.Category);
							stmtInsert.bindString(13, userJourneyPlan.Address1);
							stmtInsert.bindString(14, userJourneyPlan.Address2);
							stmtInsert.bindString(15, userJourneyPlan.Address3);
							stmtInsert.bindString(16, userJourneyPlan.Address4);
							stmtInsert.bindString(17, userJourneyPlan.PoNumber);
							stmtInsert.bindString(18, userJourneyPlan.City);
							stmtInsert.bindString(19, userJourneyPlan.PaymentType);
							stmtInsert.bindString(20, userJourneyPlan.PaymentTermCode);
							stmtInsert.bindString(21, userJourneyPlan.CreditLimit);
							stmtInsert.bindString(22, userJourneyPlan.GeoCodeX);
							stmtInsert.bindString(23, userJourneyPlan.GeoCodeY);
							stmtInsert.bindString(24, userJourneyPlan.PASSCODE);
							stmtInsert.bindString(25, userJourneyPlan.Email);
							stmtInsert.bindString(26, userJourneyPlan.ContactPersonName);
							stmtInsert.bindString(27, userJourneyPlan.PhoneNumber);
							stmtInsert.bindString(28, userJourneyPlan.AppCustomerId);
							stmtInsert.bindString(29, userJourneyPlan.MobileNumber1);
							stmtInsert.bindString(30, userJourneyPlan.MobileNumber2);
							stmtInsert.bindString(31, userJourneyPlan.Website);
							stmtInsert.bindString(32, userJourneyPlan.CustomerType);
							stmtInsert.bindString(33, userJourneyPlan.CreatedBy);
							stmtInsert.bindString(34, userJourneyPlan.ModifiedBy);
							stmtInsert.bindString(35, userJourneyPlan.Source);
							stmtInsert.bindString(36, userJourneyPlan.CustomerCategory);
							stmtInsert.bindString(37, userJourneyPlan.CustomerSubCategory);
							stmtInsert.bindString(38, userJourneyPlan.CustomerGroupCode);
							insertCustomerSpecialDay(userJourneyPlan.vecCustomerSplDay, objSqliteDB);
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
	public boolean isCustomerIsInJourneyPlan(String clientCode,String date)
	{
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase objSqliteDB=null;
			boolean isCustomerIsInJourneyPlan = false;
			try {
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT count(*) FROM tblDailyJourneyPlan where ClientCode='"+clientCode+"' AND JourneyDate like '"+date+"%'");
				long countRec = stmtSelectRec.simpleQueryForLong();
				if(countRec>0)
					isCustomerIsInJourneyPlan = true;
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(objSqliteDB!=null && objSqliteDB.isOpen())
					objSqliteDB.close();
			}
			return isCustomerIsInJourneyPlan;
		}
	}
	public String getCusotmerPriceClass(String customerSiteId){
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursor=null;
			String priceList="";
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor = sqLiteDatabase.rawQuery("Select PriceList from tblCustomer where Site='"+customerSiteId+"'", null);
				if(cursor.moveToFirst())
					priceList = cursor.getString(0);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return priceList;
		}
	}
	
	public String getChannelName(String channelCode){
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursor=null;
			String channelName="";
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor = sqLiteDatabase.rawQuery("Select Name from tblChannel where Code='"+channelCode+"'", null);
				if(cursor.moveToFirst())
					channelName = cursor.getString(0);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return channelName;
		}
	}
	
	public String[] getChannelCodes(){
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursor=null;
			String channelCodes[]= new String[2];
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor = sqLiteDatabase.rawQuery("Select TC.Code,TSB.Code from tblChannel TC INNER JOIN tblSubChannel TSB WHERE TC.Name=TSB.NAME", null);
				if(cursor.moveToFirst())
				{
					channelCodes[0] = cursor.getString(0);
					channelCodes[1] = cursor.getString(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return channelCodes;
		}
	}
	
	public String insertCustomer(CustomerDao userJourneyPlan,String vehicleNumber)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			String strCustomerId = "";
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				//Opening the database
				String query = "SELECT id from tblOfflineData where Type ='"+AppConstants.Customer+"' AND status = 0 AND id NOT IN(SELECT CustomerSiteId FROM tblCustomerSites) Order By id Limit 1";
				Cursor cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					strCustomerId = cursor.getString(0);
					userJourneyPlan.site = strCustomerId;
					userJourneyPlan.CustomerId = strCustomerId;
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				Log.e("strCustomerId", "strCustomerId - "+strCustomerId);
				
				objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+strCustomerId+"'");
				
				
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblCustomer(id, Site, SiteName, CustomerId, CustomerStatus,CustAcctCreationDate," +
						"PartyName, ChannelCode,SubChannelCode,RegionCode,CountryCode,Category,Address1,Address2,Address3,Address4,PoNumber, City,PaymentType,PaymentTermCode," +
						"CreditLimit,GeoCodeX,GeoCodeY,PASSCODE,Email,ContactPersonName,PhoneNumber,AppCustomerId,MobileNumber1,MobileNumber2,Website,CustomerType," +
						"CreatedBy,ModifiedBy,Source,CustomerCategory,CustomerSubCategory,CustomerGroupCode,CreatedDate,CreatedTime,PriceList,Attribute8) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				
				if(userJourneyPlan != null && userJourneyPlan.CustomerId != null && !userJourneyPlan.CustomerId.equalsIgnoreCase(""))
				{
					stmtInsert.bindLong(1, userJourneyPlan.id);
					stmtInsert.bindString(2, userJourneyPlan.site);
					stmtInsert.bindString(3, userJourneyPlan.SiteName);
					stmtInsert.bindString(4, userJourneyPlan.CustomerId);
					stmtInsert.bindString(5, userJourneyPlan.CustomerStatus);
					stmtInsert.bindLong(6, userJourneyPlan.CustAcctCreationDate);
					stmtInsert.bindString(7, userJourneyPlan.PartyName);
					stmtInsert.bindString(8, userJourneyPlan.ChannelCode);
					stmtInsert.bindString(9, userJourneyPlan.SubChannelCode);
					stmtInsert.bindString(10, userJourneyPlan.RegionCode);
					stmtInsert.bindString(11, userJourneyPlan.CountryCode);
					stmtInsert.bindString(12, userJourneyPlan.Category);
					stmtInsert.bindString(13, userJourneyPlan.Address1);
					stmtInsert.bindString(14, userJourneyPlan.Address2);
					stmtInsert.bindString(15, userJourneyPlan.Address3);
					stmtInsert.bindString(16, userJourneyPlan.Address4);
					stmtInsert.bindString(17, userJourneyPlan.PoNumber);
					stmtInsert.bindString(18, userJourneyPlan.City);
					stmtInsert.bindString(19, userJourneyPlan.PaymentType);
					stmtInsert.bindString(20, userJourneyPlan.PaymentTermCode);
					stmtInsert.bindString(21, userJourneyPlan.CreditLimit);
					stmtInsert.bindString(22, ""+userJourneyPlan.GeoCodeX);
					stmtInsert.bindString(23, ""+userJourneyPlan.GeoCodeY);
					stmtInsert.bindString(24, userJourneyPlan.PASSCODE);
					stmtInsert.bindString(25, userJourneyPlan.Email);
					stmtInsert.bindString(26, userJourneyPlan.ContactPersonName);
					stmtInsert.bindString(27, userJourneyPlan.PhoneNumber);
					stmtInsert.bindString(28, userJourneyPlan.AppCustomerId);
					stmtInsert.bindString(29, userJourneyPlan.MobileNumber1);
					stmtInsert.bindString(30, userJourneyPlan.MobileNumber2);
					stmtInsert.bindString(31, userJourneyPlan.Website);
					stmtInsert.bindString(32, userJourneyPlan.CustomerType);
					stmtInsert.bindString(33, userJourneyPlan.CreatedBy);
					stmtInsert.bindString(34, userJourneyPlan.ModifiedBy);
					stmtInsert.bindString(35, userJourneyPlan.Source);
					stmtInsert.bindString(36, userJourneyPlan.CustomerCategory);
					stmtInsert.bindString(37, userJourneyPlan.CustomerSubCategory);
					stmtInsert.bindString(38, userJourneyPlan.CustomerGroupCode);
					stmtInsert.bindString(39, userJourneyPlan.createdDate);
					stmtInsert.bindString(40, userJourneyPlan.createdTime);
					stmtInsert.bindString(41, userJourneyPlan.PriceList);
					stmtInsert.bindString(42, vehicleNumber);
					stmtInsert.executeInsert();
					
				}
				
				stmtInsert.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(objSqliteDB!=null)
				{
					objSqliteDB.close();
				}
			}
			return strCustomerId;
		}
	}
	
	public boolean insertCustomerVisits(CustomerVisitDO userJourneyPlan)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblCustomerVisit(CustomerVisitId, UserCode, JourneyCode, VisitCode, ClientCode," +
						"Date, ArrivalTime,OutTime,TotalTimeInMins,Latitude,Longitude,CustomerVisitAppId,IsProductiveCall,VehicleCode,TypeOfCall,DriverCode,Reason,ReasonType) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomerVisit WHERE CustomerVisitId = ?");
				
				
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblCustomerVisit SET UserCode=?, JourneyCode=?, VisitCode=?, CustomerVisitId=?," +
						"Date=?, ArrivalTime=?,OutTime=?,TotalTimeInMins=?,Latitude=?,Longitude=?,CustomerVisitAppId=?,IsProductiveCall=?,TypeOfCall =?,Reason =?, ReasonType=? WHERE CustomerVisitId = ?");
				 
				SQLiteStatement stmtUpdateVisit = objSqliteDB.compileStatement("UPDATE tblDailyJourneyPlan SET VisitStatus = ? WHERE ClientCode =?");
				
				stmtSelectRec.bindString(1, userJourneyPlan.CustomerVisitId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				if(userJourneyPlan.Latitude == 0)
					userJourneyPlan.Latitude = 25.123456;
				if(countRec != 0)
				{
					stmtUpdate.bindString(1, userJourneyPlan.UserCode);
					stmtUpdate.bindString(2, userJourneyPlan.JourneyCode);
					stmtUpdate.bindString(3, userJourneyPlan.VisitCode);
					stmtUpdate.bindString(4, userJourneyPlan.CustomerVisitId);
					stmtUpdate.bindString(5, userJourneyPlan.Date);
					stmtUpdate.bindString(6, userJourneyPlan.ArrivalTime);
					stmtUpdate.bindString(7, userJourneyPlan.OutTime);
					stmtUpdate.bindString(8, userJourneyPlan.TotalTimeInMins);
					stmtUpdate.bindDouble(9, userJourneyPlan.Latitude);
					stmtUpdate.bindDouble(10, userJourneyPlan.Longitude);
					stmtUpdate.bindString(11, userJourneyPlan.CustomerVisitAppId);
					stmtUpdate.bindString(12, userJourneyPlan.IsProductiveCall);
					stmtUpdate.bindString(13, userJourneyPlan.TypeOfCall);
					stmtUpdate.bindString(14, userJourneyPlan.reason);
					stmtUpdate.bindString(15, userJourneyPlan.reasonType);
					stmtUpdate.bindString(16, userJourneyPlan.CustomerVisitId);
					stmtUpdate.execute();
				}
				else
				{
					stmtInsert.bindString(1, userJourneyPlan.CustomerVisitId);
					stmtInsert.bindString(2, userJourneyPlan.UserCode);
					stmtInsert.bindString(3, userJourneyPlan.JourneyCode);
					stmtInsert.bindString(4, userJourneyPlan.VisitCode);
					stmtInsert.bindString(5, userJourneyPlan.ClientCode);/** Sample : 5735 */
					stmtInsert.bindString(6, userJourneyPlan.Date);/** Sample : 2014-12-19T15:35:09 */
					stmtInsert.bindString(7, userJourneyPlan.ArrivalTime);
					stmtInsert.bindString(8, userJourneyPlan.OutTime);
					stmtInsert.bindString(9, userJourneyPlan.TotalTimeInMins);
					stmtInsert.bindDouble(10, userJourneyPlan.Latitude);
					stmtInsert.bindDouble(11, userJourneyPlan.Longitude);
					stmtInsert.bindString(12, userJourneyPlan.CustomerVisitAppId);
					stmtInsert.bindString(13, userJourneyPlan.IsProductiveCall);
					stmtInsert.bindString(14, userJourneyPlan.vehicleNo);
					stmtInsert.bindString(15, userJourneyPlan.TypeOfCall);
					stmtInsert.bindString(16, userJourneyPlan.UserId);
					stmtInsert.bindString(17, userJourneyPlan.reason);
					stmtInsert.bindString(18, userJourneyPlan.reasonType);
					stmtInsert.executeInsert();
				}
				
				if(userJourneyPlan.IsProductiveCall.equalsIgnoreCase("1"))
					deleteZeroSales(userJourneyPlan.ClientCode);
				
				stmtUpdateVisit.bindString(1, "1");
				stmtUpdateVisit.bindString(2, userJourneyPlan.ClientCode);
				stmtUpdateVisit.execute();
				
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();
				stmtUpdateVisit.close();
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
	
	public void updateCustomerLocation(CustomerDao objCustomerDao)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				if(objSqliteDB == null || !objSqliteDB.isOpen())
					objSqliteDB = DatabaseHelper.openDataBase();
				
				String updateSpecialDayQuery 	= "UPDATE tblCustomer SET ContactPersonName=?,GeoCodeX=?, GeoCodeY=?, MobileNumber1=?,BlockedStatus ='False'  WHERE Site = ?";
				String selectSpecialDayQuery	= "SELECT COUNT(*) from tblCustomer WHERE Site = ?";
					
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement(selectSpecialDayQuery);
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement(updateSpecialDayQuery);
				
				
				stmtSelectRec.bindString(1, ""+objCustomerDao.site);
				long countRec = stmtSelectRec.simpleQueryForLong();
				if(countRec != 0)
				{
					stmtUpdate.bindString(1, ""+objCustomerDao.ContactPersonName);
					stmtUpdate.bindString(2, ""+objCustomerDao.GeoCodeX);
					stmtUpdate.bindString(3, ""+objCustomerDao.GeoCodeY);
					stmtUpdate.bindString(4, ""+objCustomerDao.MobileNumber1);
					stmtUpdate.bindString(5, ""+objCustomerDao.site);
					
					stmtUpdate.execute();
					
					insertCustomerSpecialDay(objCustomerDao.vecCustomerSplDay,objSqliteDB);
				}
				stmtSelectRec.close();
				stmtUpdate.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
//				if(objSqliteDB != null)
//					objSqliteDB.close();
			}
		}
	}
	public void updateCustomerLocationNew(CustomerDao objCustomerDao)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				if(objSqliteDB == null || !objSqliteDB.isOpen())
					objSqliteDB = DatabaseHelper.openDataBase();

				String updateSpecialDayQuery 	= "UPDATE tblCustomer SET  GeoCodeX=?, GeoCodeY=?, BlockedStatus ='True'  WHERE Site = ?";
				String selectSpecialDayQuery	= "SELECT COUNT(*) from tblCustomer WHERE Site = ?";

				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement(selectSpecialDayQuery);
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement(updateSpecialDayQuery);


				stmtSelectRec.bindString(1, ""+objCustomerDao.site);
				long countRec = stmtSelectRec.simpleQueryForLong();
				if(countRec != 0)
				{
					stmtUpdate.bindString(1, ""+objCustomerDao.GeoCodeX);
					stmtUpdate.bindString(2, ""+objCustomerDao.GeoCodeY);
					stmtUpdate.bindString(3, ""+objCustomerDao.site);

					stmtUpdate.execute();

					insertCustomerSpecialDay(objCustomerDao.vecCustomerSplDay,objSqliteDB);
				}
				stmtSelectRec.close();
				stmtUpdate.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
//				if(objSqliteDB != null)
//					objSqliteDB.close();
			}
		}
	}

	private void insertCustomerSpecialDay(Vector<CustomerSpecialDayDO> vecCustomerSpecialDayDO,SQLiteDatabase objSqliteDB)
	{
		synchronized (MyApplication.MyLock)
		{
			try 
			{
				if(objSqliteDB == null || !objSqliteDB.isOpen())
					objSqliteDB = DatabaseHelper.openDataBase();
				
				String insertSpecialDayQuery 	= "INSERT INTO tblCustomerSpecialDay (CustomerCode , Day , Description , Status) VALUES(?,?,?,?)";
				String updateSpecialDayQuery 	= "UPDATE tblCustomerSpecialDay SET Day=?, Description=?,Status=?  WHERE CustomerCode = ?";
				String selectSpecialDayQuery	= "SELECT COUNT(*) from tblCustomerSpecialDay WHERE CustomerCode = ?";
					
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement(selectSpecialDayQuery);
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement(insertSpecialDayQuery);
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement(updateSpecialDayQuery);
				
				
				for (int i = 0; i < vecCustomerSpecialDayDO.size(); i++) 
				{
					stmtSelectRec.bindString(1, vecCustomerSpecialDayDO.get(i).customerCode);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{
						stmtUpdate.bindString(1, vecCustomerSpecialDayDO.get(i).specialDay);
						stmtUpdate.bindString(2, vecCustomerSpecialDayDO.get(i).description);
						stmtUpdate.bindString(3, vecCustomerSpecialDayDO.get(i).status);
						stmtUpdate.bindString(4, vecCustomerSpecialDayDO.get(i).customerCode);
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1, vecCustomerSpecialDayDO.get(i).customerCode);
						stmtInsert.bindString(2, vecCustomerSpecialDayDO.get(i).specialDay);
						stmtInsert.bindString(3, vecCustomerSpecialDayDO.get(i).description);
						stmtInsert.bindString(4, vecCustomerSpecialDayDO.get(i).status);
						stmtInsert.executeInsert();
					}
				}
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
//				if(objSqliteDB != null)
//					objSqliteDB.close();
			}
		}
	}
	
	public CustomerDao getCustomerDetails(String site)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			CustomerDao objCustomerDao = null;
			CustomerSpecialDayDO splDayDO = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String splDayQuery = "SELECT TC.Site,TC.ContactPersonName,TC.GeoCodeX,TC.GeoCodeY,TC.MobileNumber1,TCD.CustomerCode,TCD.Day,TCD.Description  FROM tblCustomer TC Left outer JOIN tblCustomerSpecialDay TCD on TC.Site = TCD.CustomerCode where TC.Site ='"+site+"'";
				cursor =  objSqliteDB.rawQuery(splDayQuery, null);
				 if(cursor.moveToFirst())
				 {
					 objCustomerDao						= new CustomerDao();
					 objCustomerDao.site				= cursor.getString(0);
					 objCustomerDao.ContactPersonName	= cursor.getString(1);
					 objCustomerDao.GeoCodeX			= cursor.getString(2);
					 objCustomerDao.GeoCodeY			= cursor.getString(3);
					 objCustomerDao.MobileNumber1		= cursor.getString(4);
					 
					 splDayDO 				= new CustomerSpecialDayDO();
					 splDayDO.customerCode 	= cursor.getString(5);
					 splDayDO.specialDay  	= cursor.getString(6);
					 splDayDO.description  	= cursor.getString(7);



					 objCustomerDao.vecCustomerSplDay.add(splDayDO);
				 }
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			}
			return objCustomerDao;
		}
	}
	
	public Vector<CustomerDao> getCustomerDao()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			CustomerDao objCustomerDao = null;
			CustomerSpecialDayDO splDayDO = null;
			Vector<CustomerDao> vecCustomerDao = new Vector<CustomerDao>();
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String splDayQuery = "SELECT TC.Site,TC.Address1,TC.Address2,TC.GeoCodeX,TC.GeoCodeY,TC.MobileNumber1,TC.ContactPersonName, TCSD.CustomerCode , TCSD.Day , TCSD.Description,TC.Attribute10  from tblCustomer TC INNER JOIN tblCustomerSpecialDay TCSD on TCSD.CustomerCode = TC.Site where TCSD.Status ='N'";
				cursor =  objSqliteDB.rawQuery(splDayQuery, null);
				 if(cursor.moveToFirst())
				 {
					 do
					 {
						 objCustomerDao			= new CustomerDao();
						 objCustomerDao.site	= cursor.getString(0);
						 objCustomerDao.Address1= cursor.getString(1);
						 objCustomerDao.Address2= cursor.getString(2);
						 objCustomerDao.GeoCodeX= cursor.getString(3);
						 objCustomerDao.GeoCodeY= cursor.getString(4);
						 objCustomerDao.MobileNumber1= cursor.getString(5);
						 objCustomerDao.ContactPersonName= cursor.getString(6);
						 splDayDO 				= new CustomerSpecialDayDO();
						 splDayDO.customerCode 	= cursor.getString(7);
						 splDayDO.specialDay  	= cursor.getString(8);
						 splDayDO.description  	= cursor.getString(9);
						 
						 if(cursor.getString(10) != null)
							 objCustomerDao.Attribute10  	= cursor.getString(10);
						
						 objCustomerDao.vecCustomerSplDay.add(splDayDO);
						 
						 vecCustomerDao.add(objCustomerDao);
					 }while(cursor.moveToNext());
				 }
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			}
			return vecCustomerDao;
		}
	}
	
	public Vector<CustomerDao> getCustomerExcess()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			CustomerDao objCustomerDao = null;
			Vector<CustomerDao> vecCustomerDao = new Vector<CustomerDao>();
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String splDayQuery = "SELECT TC.Site,TC.Address1,TC.Address2,TC.GeoCodeX,TC.GeoCodeY,TC.MobileNumber1,TC.ContactPersonName,TC.Attribute10 from tblCustomer TC where TC.BlockedStatus ='True'";
				cursor =  objSqliteDB.rawQuery(splDayQuery, null);
				 if(cursor.moveToFirst())
				 {
					 do
					 {
						 objCustomerDao			= new CustomerDao();
						 objCustomerDao.site	= cursor.getString(0);
						 objCustomerDao.Address1= cursor.getString(1);
						 objCustomerDao.Address2= cursor.getString(2);
						 objCustomerDao.GeoCodeX= cursor.getString(3);
						 objCustomerDao.GeoCodeY= cursor.getString(4);
						 objCustomerDao.MobileNumber1= cursor.getString(5);
						 objCustomerDao.ContactPersonName= cursor.getString(6);
						 
						 if(cursor.getString(7) != null)
							 objCustomerDao.Attribute10  	= cursor.getString(7);
						 
						 vecCustomerDao.add(objCustomerDao);
					 }while(cursor.moveToNext());
				 }
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			}
			return vecCustomerDao;
		}
	}
	public boolean  getCustomerExcessNew(String site)
	{
		synchronized (MyApplication.MyLock)
		{
			boolean flag=false;
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			CustomerDao objCustomerDao = null;
			Vector<CustomerDao> vecCustomerDao = new Vector<CustomerDao>();
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String splDayQuery = "SELECT  count(*) from tblCustomer TC where TC.Site='"+site+"' and TC.BlockedStatus  ='True'";
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement(splDayQuery );

				long countRec = stmtSelectRec.simpleQueryForLong();
				cursor =  objSqliteDB.rawQuery(splDayQuery, null);
				 if(countRec > 0)
				 {
				 flag= true;
				 }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			}
			return flag;
		}
	}

	public Vector<CustomerSpecialDayDO> getCustomerSpecialDay(String customerCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			Vector<CustomerSpecialDayDO> vecSplDay = new Vector<CustomerSpecialDayDO>();
			CustomerSpecialDayDO splDayDO;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String splDayQuery = "SELECT customerCode, specialDay, description, Status FROM tblCustomerSpecialDay WHERE customerCode = '"+customerCode+"'";
				 
				 cursor =  objSqliteDB.rawQuery(splDayQuery, null);
				 if(cursor.moveToFirst())
				 {
					 do
					 {
						 splDayDO 				= new CustomerSpecialDayDO();
						 splDayDO.customerCode 	= cursor.getString(0);
						 splDayDO.specialDay  	= cursor.getString(1);
						 splDayDO.description  	= cursor.getString(2);
						 splDayDO.status	  	= cursor.getString(3);
						 vecSplDay.add(splDayDO);
					 }while(cursor.moveToNext());
				 }
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return vecSplDay;
		}
	}
	
	public void updateCustomerVisit(TrxHeaderDO trxHeaderDO){
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase=null;
			try {
				sqLiteDatabase =DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate 		= sqLiteDatabase.compileStatement("UPDATE tblCustomerVisit SET IsProductiveCall=?  WHERE VisitCode = ?");
				stmtUpdate.bindLong(1, 1);
				stmtUpdate.bindString(2, trxHeaderDO.visitCode);
				stmtUpdate.execute();
				stmtUpdate.close();
				
				deleteZeroSales(trxHeaderDO.clientCode);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
		}
	}
	public void deleteZeroSales(String clientCode)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursor = null;
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				LogUtils.debug("deleteZeroSales", clientCode);
				String selectQuery = "SELECT COUNT(*) FROM tblSkipReasons WHERE CustomerSiteId ='"+clientCode+"'";
				String deleteQuery = "DELETE FROM tblSkipReasons WHERE CustomerSiteId ='"+clientCode+"'";
				cursor = sqLiteDatabase.rawQuery(selectQuery, null);
				if(cursor != null && cursor.moveToFirst())
				{
					if(cursor.getInt(0) > 0)
						sqLiteDatabase.execSQL(deleteQuery);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
		}
	}
	public void updateCustomerCheckOutTime(String outTime)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				ContentValues cv = new ContentValues();
					cv.put("OutTime", outTime);
				objSqliteDB.update("tblCustomerVisit", cv, " IFNULL(OutTime,'') = ''", null);
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
	
	//Journey Call
	public void updateCustomerProductivity(String site, String date, String typeOfCall, String productive)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				ContentValues cv = new ContentValues();
				cv.put("IsProductiveCall", productive);
				
				objSqliteDB.update("tblCustomerVisit", cv, "ClientCode = '"+site+"' AND Date LIKE '"+date+"%'", null);
				
				if(productive.equalsIgnoreCase("1"))
					deleteZeroSales(site);
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
	
	public void updateLastJourneyLog()
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor nCursor = null, cursor = null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT ClientCode FROM tblCustomerVisit WHERE OutTime =''";
				 nCursor = objSqliteDB.rawQuery(suery, null);
				 SQLiteStatement stmtUpdate = 	objSqliteDB.compileStatement("Update tblCustomerVisit set Status=? ,OutTime = ?,TotalTimeInMins =? where ClientCode =? AND ArrivalTime =?");
				 
				 if(nCursor.moveToFirst())
				 {
					 do
					 {
						 cursor	 =	objSqliteDB.rawQuery("select ArrivalTime from tblCustomerVisit where ClientCode ='"+nCursor.getString(0)+"' AND OutTime =''", null);
						 if(cursor.moveToFirst())
						 {
							 String outDate = CalendarUtils.getCurrentDateTime();
							 stmtUpdate.bindString(1, "N");
							 stmtUpdate.bindString(2, outDate);
							 long minutes = CalendarUtils.getDateDifferenceInMinutes(cursor.getString(0), outDate);
							 stmtUpdate.bindString(3, ""+minutes);
							 
							 stmtUpdate.bindString(4, nCursor.getString(0));
							 stmtUpdate.bindString(5, cursor.getString(0));
							 stmtUpdate.execute();
						 }
					 }
					 while(nCursor.moveToNext());
				 }
				 
				 if(nCursor!=null && !nCursor.isClosed())
					 nCursor.close();
				 
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 
				 stmtUpdate.close();
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
		}
	}
	
	public boolean isCheckedIn()
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor nCursor = null;
			 boolean isCheckIn = false;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT ClientCode FROM tblCustomerVisit WHERE OutTime =''";
				 nCursor = objSqliteDB.rawQuery(suery, null);
				 
				 if(nCursor.moveToFirst())
				 {
					 isCheckIn = true;
				 }
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 if(nCursor!=null && !nCursor.isClosed())
					 nCursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return isCheckIn;
		}
	}
	
	public void updateCustomerVisitUploadStatus(boolean isUploaded, String CustomerVisitAppId)
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
				objSqliteDB.update("tblCustomerVisit", cv, "CustomerVisitAppId = ?", new String[]{CustomerVisitAppId});
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
	
	public Vector<CustomerVisitDO>  getCustomerVisit()
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor cursor = null;
			 Vector<CustomerVisitDO> vecCustomerVisit = new Vector<CustomerVisitDO>();
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT * FROM tblCustomerVisit WHERE IFNULL(Status, 'N') = 'N' and IFNULL(OutTime, '') != ''";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 while(cursor.moveToNext())
				 {
					 CustomerVisitDO  c = new CustomerVisitDO();
					 c.CustomerVisitId = cursor.getString(0);
					 c.UserCode = cursor.getString(1);
					 c.JourneyCode = cursor.getString(2);
					 c.VisitCode = cursor.getString(3);
					 c.ClientCode = cursor.getString(4);
					 c.Date = cursor.getString(5);
					 c.ArrivalTime = cursor.getString(6);
					 c.OutTime = cursor.getString(7);
					 c.TotalTimeInMins = ""+cursor.getInt(8);
					 c.Latitude = cursor.getDouble(9);
					 c.Longitude = cursor.getDouble(10);
					 c.CustomerVisitAppId = cursor.getString(11);
					 c.IsProductiveCall = ""+cursor.getInt(12);
					 c.TypeOfCall = cursor.getString(13);
					 c.vehicleNo = cursor.getString(16);
					 c.UserId = cursor.getString(17);
					 c.reasonType = cursor.getString(18);
					 c.reason = cursor.getString(19);
					 vecCustomerVisit.add(c);
				 }
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return vecCustomerVisit;
		}
	}
	
	public SurveyCustomerDeatislDO  getCustomerSurveyDetails(String Usercode)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			SurveyCustomerDeatislDO surveyDo = new SurveyCustomerDeatislDO();
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT ClientCode,VisitCode FROM tblCustomerVisit WHERE UserCode = '"+Usercode+"'";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 while(cursor.moveToNext())
				 {
					 surveyDo.clientCode = cursor.getString(0);
					 surveyDo.visitCode  = cursor.getString(1);
					
				 }
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return surveyDo;
		}
	}
	
	public CustomerCreditLimitDo getCustomerCreditLimit(String siteNo) //modified since TPT brand is added for some of views in sqlite
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 CustomerCreditLimitDo customerLimit = new CustomerCreditLimitDo();
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT Site,CREDIT_LIMIT,Outstanding,FoodOutstanding,TPTOutstanding,BalanceLimit,ExcessAmount " +
				 		"FROM vw_CustomerCreditLimit WHERE Site = '"+siteNo+"'";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 if(cursor != null && cursor.moveToNext())
				 {
					 customerLimit.site 					= cursor.getString(0);
					 customerLimit.creditLimit 				= cursor.getString(1);
					 customerLimit.outStandingAmount 		= cursor.getString(2);
					 customerLimit.outStandingFoodAmount 	= cursor.getString(3);
					 customerLimit.outStandingTPTAmount     = cursor.getString(4);
					 customerLimit.availbleLimit 			= cursor.getString(5);
					 customerLimit.excessPayment 			= cursor.getString(6);
				 }
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 
				 String queryMonthlySalesValue="Select SalesAmount from tblCustomerMonthlySales where CustomerCode='"+siteNo+"'";
				 cursor =  objSqliteDB.rawQuery(queryMonthlySalesValue, null);
				 if(cursor != null && cursor.moveToFirst()){
					 customerLimit.monthlySalesAmount = cursor.getString(0);
				 }
				 
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 
				 
				 String lastSalesInvoiceAmount="SELECT (TotalAmount  - TotalDiscountAmount) FROM tblTrxHeader where ClientCode = '"+siteNo+"' order by TrxDate DESC limit 1 ";
				 cursor =  objSqliteDB.rawQuery(lastSalesInvoiceAmount, null);
				 if(cursor != null && cursor.moveToFirst()){
					 customerLimit.lastSalesInvoiceAmount = cursor.getString(0);
				 }else{
					 customerLimit.lastSalesInvoiceAmount=customerLimit.monthlySalesAmount;
				 }
				 
//				 String queryExcessPayment = "SELECT Attribute10 FROM tblCustomer WHERE Site='"+siteNo+"'";
//				 cursor = objSqliteDB.rawQuery(queryExcessPayment, null);
//				 if(cursor != null && cursor.moveToFirst()){
//					 customerLimit.excessPayment = cursor.getString(0);
//				 }
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return customerLimit;
		}
	}
	
/*
	public CustomerCreditLimitDo getCustomerCreditLimit(String siteNo)
	{
		synchronized(MyApplication.MyLock)
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 CustomerCreditLimitDo customerLimit = new CustomerCreditLimitDo();
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT Site,CREDIT_LIMIT,Outstanding,FoodOutstanding,BalanceLimit,ExcessAmount " +
				 		"FROM vw_CustomerCreditLimit WHERE Site = '"+siteNo+"'";

				 cursor =  objSqliteDB.rawQuery(suery, null);
				 if(cursor != null && cursor.moveToNext())
				 {
					 customerLimit.site 					= cursor.getString(0);
					 customerLimit.creditLimit 				= cursor.getString(1);
					 customerLimit.outStandingAmount 		= cursor.getString(2);
					 customerLimit.outStandingFoodAmount 	= cursor.getString(3);
					 customerLimit.availbleLimit 			= cursor.getString(4);
					 customerLimit.excessPayment 			= cursor.getString(5);
				 }
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();

				 String queryMonthlySalesValue="Select SalesAmount from tblCustomerMonthlySales where CustomerCode='"+siteNo+"'";
				 cursor =  objSqliteDB.rawQuery(queryMonthlySalesValue, null);
				 if(cursor != null && cursor.moveToFirst()){
					 customerLimit.monthlySalesAmount = cursor.getString(0);
				 }

				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();


				 String lastSalesInvoiceAmount="SELECT (TotalAmount  - TotalDiscountAmount) FROM tblTrxHeader where ClientCode = '"+siteNo+"' order by TrxDate DESC limit 1 ";
				 cursor =  objSqliteDB.rawQuery(lastSalesInvoiceAmount, null);
				 if(cursor != null && cursor.moveToFirst()){
					 customerLimit.lastSalesInvoiceAmount = cursor.getString(0);
				 }else{
					 customerLimit.lastSalesInvoiceAmount=customerLimit.monthlySalesAmount;
				 }

//				 String queryExcessPayment = "SELECT Attribute10 FROM tblCustomer WHERE Site='"+siteNo+"'";
//				 cursor = objSqliteDB.rawQuery(queryExcessPayment, null);
//				 if(cursor != null && cursor.moveToFirst()){
//					 customerLimit.excessPayment = cursor.getString(0);
//				 }
			 }
			 catch (Exception e)
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return customerLimit;
		}
	}

*/
	public HashMap<String, CustomerCreditLimitDo> 	getCreditLimits()
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 HashMap<String, CustomerCreditLimitDo> hmLimits = new HashMap<String, CustomerCreditLimitDo>();
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT Site,CREDIT_LIMIT,Outstanding,FoodOutstanding,TPTOutstanding,  BalanceLimit, (Outstanding+FoodOutstanding+TPTOutstanding ) as totaloutstanding,ExcessAmount " +
				 		"FROM vw_CustomerCreditLimit";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 if(cursor.moveToFirst())
				 {
					 do
					 {
						 CustomerCreditLimitDo customerLimit = new CustomerCreditLimitDo();
						 customerLimit.site = cursor.getString(0);
						 customerLimit.creditLimit = cursor.getString(1);
						 customerLimit.outStandingAmount = cursor.getString(2);
						 customerLimit.outStandingFoodAmount = cursor.getString(3);
						 customerLimit.outStandingTPTAmount = cursor.getString(4);
						 customerLimit.availbleLimit = cursor.getString(5);
						 customerLimit.totalOutStandingAmount = cursor.getString(6);
						 if(StringUtils.getFloat(customerLimit.availbleLimit)<0)
							 customerLimit.availbleLimit=""+0;
						 hmLimits.put(customerLimit.site, customerLimit);
					 }
					 while(cursor.moveToNext());
				 }
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return hmLimits;
		}
	}
	
	public HashMap<String, Float> getOverDueAmount()
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 HashMap<String, Float> hmOverDue = new HashMap<String, Float>();
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT * FROM vm_OverDue";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 if(cursor.moveToFirst())
				 {
					 do
					 {
						 hmOverDue.put(cursor.getString(0), cursor.getFloat(1));
					 }
					 while(cursor.moveToNext());
				 }
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return hmOverDue;
		}
	}
	public HashMap<String, Float> getOverDueAmountNew()
	{
		synchronized(MyApplication.MyLock)
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 HashMap<String, Float> hmOverDue = new HashMap<String, Float>();
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
//				 select   customersiteid,  sum( TPI.BalanceAmount  ) ,  ((julianday('now') - julianday(TPI.invoicedate))  -TC .paymenttermcode ) AS DDIFF  ,TC .paymenttermcode as PT   from tblPendingInvoices TPI  Inner join  tblcustomer TC  on TC.site = TPI.customersiteid   where DDIFF>0  group by customersiteid
				 String suery = "select   customersiteid,  sum( TPI.BalanceAmount  ) ,  ((julianday('now') - julianday(TPI.invoicedate))  -TC .paymenttermcode ) AS DDIFF  ,TC .paymenttermcode as PT   from tblPendingInvoices TPI " +
						 " Inner join  tblcustomer TC  on TC.site = TPI.customersiteid   where DDIFF>0  group by customersiteid";

				 cursor =  objSqliteDB.rawQuery(suery, null);
				 if(cursor.moveToFirst())
				 {
					 do
					 {
						 hmOverDue.put(cursor.getString(0), cursor.getFloat(1));
					 }
					 while(cursor.moveToNext());
				 }
			 }
			 catch (Exception e)
			 {
				e.printStackTrace();
			 }
			 finally
			 {

				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return hmOverDue;
		}
	}
	public HashMap<String, String> getShiftToCodes()
	{
		synchronized(MyApplication.MyLock)
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 HashMap<String, String> hmShiftCodes = new HashMap<String, String>();
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT site, Attribute3 FROM tblCustomer";

				 cursor =  objSqliteDB.rawQuery(suery, null);
				 if(cursor.moveToFirst())
				 {
					 do
					 {
						 hmShiftCodes.put(cursor.getString(0), cursor.getString(1));
					 }
					 while(cursor.moveToNext());
				 }
			 }
			 catch (Exception e)
			 {
				e.printStackTrace();
			 }
			 finally
			 {

				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return hmShiftCodes;
		}
	}

	public float getOverdueAmount(String siteNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 float orverdue = 0;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT SUM (BalanceAmount) FROM tblPendingInvoices WHERE CustomerSiteId = '"+siteNo+"' AND (IsOutStanding = 'true' OR IsOutStanding = '1')";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 if(cursor.moveToNext())
					 orverdue = cursor.getFloat(0);
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return orverdue;
		}
	}
	
	public String getCustomerStoreGrowth(String customerID)
	{
		synchronized(MyApplication.MyLock) 
		{
			String storeGrowth = "0";
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT StoreGrowth FROM tblCustomer WHERE CustomerId = '"+customerID+"'";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 if(cursor.moveToNext())
				 {
					 storeGrowth = cursor.getString(0)==null ||  cursor.getString(0).equalsIgnoreCase("null") ?"0":cursor.getString(0);
				 }
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return storeGrowth;
		}
	}
	
	public CustomerDao getCustomerforStoreCheck(String site)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 
			 CustomerDao objcusCustomerDao = new CustomerDao();
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT ChannelCode, RegionCode FROM tblCustomer WHERE Site = '"+site+"'";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 if(cursor.moveToNext())
				 {
					 objcusCustomerDao.ChannelCode = cursor.getString(0);
					 objcusCustomerDao.RegionCode = cursor.getString(1);
				 }
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null&& objSqliteDB.isOpen())
					 objSqliteDB.close();
			 }
			 return objcusCustomerDao;
		}
	}

	/**
	 * @param vecCustomerDao
	 */
	public void updateCustomerDao(Vector<CustomerDao> vecCustomerDao) 
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				if(objSqliteDB == null || !objSqliteDB.isOpen())
					objSqliteDB = DatabaseHelper.openDataBase();
				
				String updateSpecialDayQuery 	= "UPDATE tblCustomerSpecialDay SET Status='Y' WHERE CustomerCode = ?";
					
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement(updateSpecialDayQuery);
				
				for (int i = 0; i < vecCustomerDao.size(); i++) 
				{
					stmtUpdate.bindString(1, vecCustomerDao.get(i).site);
					stmtUpdate.execute();
				}
				stmtUpdate.close();
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
	
	/**
	 * @param vecCustomerDao
	 */
	public void updateTBLCustomer(Vector<CustomerDao> vecCustomerDao) 
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				if(objSqliteDB == null || !objSqliteDB.isOpen())
					objSqliteDB = DatabaseHelper.openDataBase();
				
				String updateSpecialDayQuery 	= "UPDATE tblCustomer SET BlockedStatus='False' WHERE Site= ?";
					
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement(updateSpecialDayQuery);
				
				for (int i = 0; i < vecCustomerDao.size(); i++) 
				{
					stmtUpdate.bindString(1, vecCustomerDao.get(i).site);
					stmtUpdate.execute();
				}
				stmtUpdate.close();
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
	
	public boolean insertCustomerSync(String customerCode,int status)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomerSync WHERE CustomerCode =?");
				SQLiteStatement stmtInsert 	= objSqliteDB.compileStatement("INSERT INTO tblCustomerSync (CustomerCode, Status) VALUES(?,?)");
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblCustomerSync set Status=? WHERE CustomerCode =?");
				
				stmtSelectRec.bindString(1, customerCode);
				long countRec = stmtSelectRec.simpleQueryForLong();
				if(countRec != 0)
				{	
					stmtUpdate.bindLong(1, status);
					stmtUpdate.bindString(2, customerCode);
					stmtUpdate.execute();
				}
				else
				{
					stmtInsert.bindString(1, customerCode);
					stmtInsert.bindLong(2, status);
					stmtInsert.executeInsert();
				}
				
				result =  true;
				
				
				
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
	public ArrayList<MonthlyDataDO> getMonthlyData(String site) 
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqliteDB = null;
			Cursor cursor = null;
			ArrayList<MonthlyDataDO> arrMonthlyData = new ArrayList<MonthlyDataDO>();
			MonthlyDataDO objMonthlyDataDO = null;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT  RangeMonth,RangeYear, Amount, Bills FROM tblCustomerMonthwiseSale WHERE Range = 'Monthly' AND Code = '"+site+"'";
				
				ArrayList<String> arrMonth = CalendarUtils.getMonthofYear();
				cursor = sqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						objMonthlyDataDO = new MonthlyDataDO();
						objMonthlyDataDO.month_year = cursor.getString(0)+"-"+cursor.getString(1);
						objMonthlyDataDO.month_sales = cursor.getString(2);
						objMonthlyDataDO.month_bills = cursor.getString(3);
						arrMonthlyData.add(objMonthlyDataDO);
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
				if(sqliteDB != null && sqliteDB.isOpen())
					sqliteDB.close();
			}
			return arrMonthlyData;
		}
	}
	
	public ArrayList<MonthlyDataDO> getStoreGrowthData(String site) 
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqliteDB = null;
			Cursor cursor = null;
			ArrayList<MonthlyDataDO> arrMonthlyData = new ArrayList<MonthlyDataDO>();
			MonthlyDataDO objMonthlyDataDO = null;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT  RangeYear, Amount, Bills FROM tblCustomerMonthwiseSale WHERE Range = 'Yearly' AND Code = '"+site+"'";
				
				ArrayList<String> arrMonth = CalendarUtils.getMonthofYear();
				cursor = sqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						objMonthlyDataDO = new MonthlyDataDO();
						objMonthlyDataDO.month_year = cursor.getString(0);
						objMonthlyDataDO.month_sales = cursor.getString(1);
						objMonthlyDataDO.month_bills = cursor.getString(2);
						arrMonthlyData.add(objMonthlyDataDO);
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
				if(sqliteDB != null && sqliteDB.isOpen())
					sqliteDB.close();
			}
			return arrMonthlyData;
		}
	}

	public boolean insertUnVisitedCutomers(ArrayList<JourneyPlanDO> arCustomerDao)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblUnvisitedCustomers(UserCode, JourneyCode, ClientCode, JourneyDate, ReasonType,Reason)" +
						" VALUES(?,?,?,?,?,?)");
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblUnvisitedCustomers WHERE ClientCode = ?");
				
				
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblUnvisitedCustomers SET UserCode=?, JourneyCode=?, JourneyDate=?, ReasonType=?,Reason=?" +
						" WHERE ClientCode = ?");
				 
				for(int i=0;i<arCustomerDao.size();i++)
				{
					JourneyPlanDO userJourneyPlan = arCustomerDao.get(i);
					stmtSelectRec.bindString(1, userJourneyPlan.clientCode);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(userJourneyPlan != null )
						{
							stmtUpdate.bindString(1, ""+userJourneyPlan.userID);
							stmtUpdate.bindString(2, ""+userJourneyPlan.JourneyCode);
							stmtUpdate.bindString(3, ""+userJourneyPlan.JourneyDate);
							stmtUpdate.bindString(4, ""+userJourneyPlan.reasonType);
							stmtUpdate.bindString(5, ""+userJourneyPlan.reasonForSkip+"");
							stmtUpdate.bindString(6, ""+userJourneyPlan.clientCode);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(userJourneyPlan != null )
						{
							stmtInsert.bindString(1, ""+userJourneyPlan.userID);
							stmtInsert.bindString(2, ""+userJourneyPlan.JourneyCode);
							stmtInsert.bindString(3, ""+userJourneyPlan.clientCode);
							stmtInsert.bindString(4, ""+userJourneyPlan.JourneyDate);
							stmtInsert.bindString(5, ""+userJourneyPlan.reasonType);
							stmtInsert.bindString(6, ""+userJourneyPlan.reasonForSkip);
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
	
	public ArrayList<JourneyPlanDO> getAllUnVisitedCustomerdetail()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqliteDB = null;
			Cursor cursor = null;
			ArrayList<JourneyPlanDO> arrJOurneyPlanDO = new ArrayList<JourneyPlanDO>();
			JourneyPlanDO objJOurneyPlanDO = null;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT * FROM tblUnvisitedCustomers";
				cursor = sqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						objJOurneyPlanDO = new JourneyPlanDO();
						objJOurneyPlanDO.userID = cursor.getString(0);
						objJOurneyPlanDO.JourneyCode = cursor.getString(1);
						objJOurneyPlanDO.clientCode = cursor.getString(2);
						objJOurneyPlanDO.JourneyDate = cursor.getString(3);
						objJOurneyPlanDO.reasonType = cursor.getString(4);
						objJOurneyPlanDO.reasonForSkip = cursor.getString(5);
						arrJOurneyPlanDO.add(objJOurneyPlanDO);
					}while(cursor.moveToNext());
				}
			}
			catch(Exception ex)
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
			return arrJOurneyPlanDO;
		}
	}
	
	public void updateSalesOutlet(String reasonType, String reason,String clientCode)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			String reasontype="";
			String reasonn="";
			try 
			{
				if(objSqliteDB == null || !objSqliteDB.isOpen())
					objSqliteDB = DatabaseHelper.openDataBase();
				
				String updateSpecialDayQuery 	= "UPDATE tblCustomerVisit SET ReasonType=?,Reason=?,Status='N' WHERE ClientCode = ? AND Date LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				String GetReasonQuery 	= "Select ReasonType,Reason from tblCustomerVisit WHERE ClientCode = '"+clientCode+"' AND Date LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				String selectSpecialDayQuery	= "SELECT COUNT(*) from tblCustomer WHERE Site = ?";
					
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement(selectSpecialDayQuery);
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement(updateSpecialDayQuery);
				
				cursor = objSqliteDB.rawQuery(GetReasonQuery, null);
				if(cursor != null && cursor.moveToFirst())
				{
					reasontype = cursor.getString(0);
					reasonn = cursor.getString(1);
				}
				
				stmtSelectRec.bindString(1, ""+clientCode);
				long countRec = stmtSelectRec.simpleQueryForLong();
				if(countRec != 0)
				{
					if(!TextUtils.isEmpty(reasontype))
					stmtUpdate.bindString(1, reasontype+","+reasonType);
					else
						stmtUpdate.bindString(1, reasonType);
						
					if(!TextUtils.isEmpty(reasonn))
					stmtUpdate.bindString(2, reasonn+","+reason);
					else
						stmtUpdate.bindString(2, reason);
					
					stmtUpdate.bindString(3, ""+clientCode);					
					stmtUpdate.execute();
				}
				stmtSelectRec.close();
				stmtUpdate.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
//				if(objSqliteDB != null)
//					objSqliteDB.close();
			}
		}
	}
	
	public void updateLastJourneyLog(String visitCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 SQLiteStatement stmtUpdate = 	objSqliteDB.compileStatement("Update tblCustomerVisit set Status=? ,OutTime = ?,TotalTimeInMins =? where VisitCode =? ");
				 Cursor cursor	 =	objSqliteDB.rawQuery("select ArrivalTime from tblCustomerVisit where VisitCode ='"+visitCode+"'", null);
				 if(cursor.moveToFirst())
				 {
					 String outDate = CalendarUtils.getCurrentDateTime();
					 stmtUpdate.bindString(1, "N");
					 stmtUpdate.bindString(2, outDate);
					 long minutes = CalendarUtils.getDateDifferenceInMinutes(cursor.getString(0), outDate);
					 stmtUpdate.bindString(3, ""+minutes);
					 stmtUpdate.bindString(4, visitCode);
					 stmtUpdate.execute();
				 }
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 
				 stmtUpdate.close();
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
		}
	}
}
