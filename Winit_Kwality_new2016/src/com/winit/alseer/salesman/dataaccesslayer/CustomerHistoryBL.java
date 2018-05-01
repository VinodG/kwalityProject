package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.CustomerHistoryDO;
import com.winit.alseer.salesman.dataobject.CustomerReportDO;
import com.winit.alseer.salesman.dataobject.NewBarDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.MyApplication;

public class CustomerHistoryBL 
{


	public boolean inserCustomerHistory(Vector<CustomerHistoryDO> veCustomerHistoryDO)
	{
		synchronized(MyApplication.MyLock) 
		{
	//		deleteCustomerHistory();
			SQLiteDatabase objSqLiteDatabase = null;
			try
			{
				objSqLiteDatabase = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtInsert = objSqLiteDatabase.compileStatement("INSERT INTO tblCustomerHistory (CustomerSiteId, Month, Year,PreviuosYearAchieved,CurrentYearAchieved) VALUES(?,?,?,?,?)");
				 
				for(int i =0 ; i < veCustomerHistoryDO.size() ; i++)
				{
					CustomerHistoryDO objCustomerHistoryDO = veCustomerHistoryDO.get(i);
					for(int j=0 ; j < objCustomerHistoryDO.vecReportDO.size(); j++)
					{
						CustomerReportDO objCustomerReportDO = objCustomerHistoryDO.vecReportDO.get(j);
						stmtInsert.bindString(1, objCustomerHistoryDO.CustomerSiteID);
						stmtInsert.bindString(2, objCustomerReportDO.month);
						stmtInsert.bindString(3, "");
						stmtInsert.bindDouble(4, objCustomerReportDO.PreviousMonthAchieved);
						stmtInsert.bindDouble(5, objCustomerReportDO.CurrentMonthAchieved);
						stmtInsert.executeInsert();
					}
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqLiteDatabase != null)
					objSqLiteDatabase.close();
			}
			return true;
		}
	}
	public Vector<NewBarDO> getCustomerHistory(String CustomerSiteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			LogUtils.errorLog("presellerId", "presellerId "+CustomerSiteId);
			Vector<NewBarDO> vecbBarDO = new Vector<NewBarDO>();
			
			String strQuery = "Select * from tblCustomerHistory where CustomerSiteId= '"+CustomerSiteId+"' order by Month asc" ;
			
	//		String strQuery = "Select * from tblPresellerDailyTargets where TargetId= (Select TargetId from tblPresellerTarget where PresellerId ='"+presellerId+"' AND Month = '"+month+"' AND Year = '"+year+"')" ;		 		
			SQLiteDatabase mDatabase = null;
			Cursor cursor =null;
			LogUtils.errorLog("strQuery", "strQuery "+strQuery);		
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor = mDatabase.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					do
					{
						NewBarDO newBarDO = new NewBarDO();
						newBarDO.MONTH = cursor.getInt(1);
						newBarDO.previousMonth_Graph_Value = cursor.getInt(3);
						newBarDO.currentMonth_Graph_Value = cursor.getInt(4);
						vecbBarDO.add(newBarDO);
					}
					while(cursor.moveToNext());
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(mDatabase!=null)
					mDatabase.close();
			}
				
			
			
			return vecbBarDO;
		}
	}
	public void deleteCustomerHistory()
	{
		synchronized(MyApplication.MyLock) 
		{
			DatabaseHelper.get("delete from tblCustomerHistory");
		}
	}
	
	public Vector<String> getCustomerSites(String customerSiteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<String> vecCustomers = new Vector<String>() ;
			
			String strQuery = "Select TotalOutstandingBalance from tblCustomerSites where CustomerSiteId= '"+customerSiteId+"'" ;
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			LogUtils.errorLog("strQuery", "strQuery "+strQuery);		
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor = mDatabase.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					do
					{
						vecCustomers.add(cursor.getString(0));
					}
					while(cursor.moveToNext());
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(mDatabase!=null)
					mDatabase.close();
			}
				
			return vecCustomers;
		}
	}
	public boolean getCustomerAmount(String customerSiteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = false;
			String strQuery = "Select TotalOutstandingBalance from tblCustomerSites where CustomerSiteId= '"+customerSiteId+"' and TotalOutstandingBalance > 0" ;
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			LogUtils.errorLog("strQuery", "strQuery "+strQuery);		
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor = mDatabase.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
					result =  true;
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(mDatabase!=null)
					mDatabase.close();
			}
			return result;
		}
	}

	public float getCustomerBalance(String customerSiteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			float fBalance = 0.0f;
			
			String strQuery = "Select TotalOutstandingBalance from tblCustomerSites where CustomerSiteId= '"+customerSiteId+"'" ;
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null ;
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor = mDatabase.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					fBalance = StringUtils.getFloat(cursor.getString(0));
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(mDatabase!=null)
					mDatabase.close();
			}
			return fBalance;
		}
	}
	
	public String getCountOfSurvedCustomer()
	{
		synchronized(MyApplication.MyLock) 
		{
			String fBalance = "0";
			
			String strQuery = "select count (DISTINCT ClientCode) from tblCustomerVisit" ;
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null ;
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor = mDatabase.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					fBalance = cursor.getString(0);
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(mDatabase!=null)
					mDatabase.close();
			}
			return fBalance;
		}
	}
	
}
