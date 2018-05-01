package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.WareHouseDetailDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.sfa.salesman.MyApplication;

public class WareHouseDA 
{

	public Vector<WareHouseDetailDO> getAllWareHouse() 
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			Vector<WareHouseDetailDO> vecWareHouseDetail = new Vector<WareHouseDetailDO>();
			WareHouseDetailDO objWareHouseDetail = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String suery = "SELECT * FROM tblWareHouse";
				 
				cursor =  objSqliteDB.rawQuery(suery, null);
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						objWareHouseDetail = new WareHouseDetailDO();
						objWareHouseDetail.WareHouseCode	=	""+cursor.getString(0);
						objWareHouseDetail.WareHouseName	=	""+cursor.getString(1);
						objWareHouseDetail.SalesOrgCode		=	""+cursor.getString(2);
						vecWareHouseDetail.add(objWareHouseDetail);
					}while(cursor.moveToNext());
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
				if(objSqliteDB!=null)
					 objSqliteDB.close();
			}
			
			return vecWareHouseDetail;
		}
	}
	 
	public Vector<WareHouseDetailDO> getAllWareHouseforthisSalesPerson(String SalesOrgCode) 
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			Vector<WareHouseDetailDO> vecWareHouseDetail = new Vector<WareHouseDetailDO>();
			WareHouseDetailDO objWareHouseDetail = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String suery = "SELECT * FROM tblWareHouse";// where SalesOrgCode ='"+SalesOrgCode+"'";
				 
				cursor =  objSqliteDB.rawQuery(suery, null);
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						objWareHouseDetail = new WareHouseDetailDO();
						objWareHouseDetail.WareHouseCode	=	""+cursor.getString(0);
						objWareHouseDetail.WareHouseName	=	""+cursor.getString(1);
						objWareHouseDetail.SalesOrgCode		=	""+cursor.getString(2);
						vecWareHouseDetail.add(objWareHouseDetail);
					}while(cursor.moveToNext());
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
				if(objSqliteDB!=null)
					 objSqliteDB.close();
			}
			
			return vecWareHouseDetail;
		}
	}
	public boolean getLoadStatus(String empNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean isTrue = true;
			SQLiteDatabase sqLiteDatabase =null;
			SQLiteStatement selectStament =null;
			try
			{
				String query = "SELECT COUNT(*) FROM tblMovementHeader WHERE UserCode ='"+empNo+"' and MovementDate like '"+CalendarUtils.getTodaydate()+"'";
				sqLiteDatabase = DatabaseHelper.openDataBase();
				selectStament = sqLiteDatabase.compileStatement(query);
				long count  = selectStament.simpleQueryForLong();
				if(count>0)
					isTrue = false;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}

			return isTrue;
		}
	}
}
