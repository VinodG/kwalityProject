package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.winit.alseer.salesman.dataobject.ShareOfShelfDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.sfa.salesman.MyApplication;

public class ShareOfShelfDA extends BaseDL
{
	private static final String TABLE_NAME = "tblShareOfShelf";
	
	public ShareOfShelfDA()
	{
		super(TABLE_NAME);
	}

	public void insertShareOfShelf(ShareOfShelfDO shareOfShelfDO)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteStatement stmtRecCount = null,stmtInsert = null,stmtUpdate=null;
			if(shareOfShelfDO != null)
			{
				try
				{
					openTransaction();
					String insertQuery 	= "INSERT INTO " + TABLE_NAME + "( brandid,category,date," +
							"categorysize,sizeinmeters,marketsize,status)" +
							" VALUES (?,?,?,?,?,?,?)";
					
					String updateQuery 	= "UPDATE "+TABLE_NAME+" set category=?, date=?,categorysize=?, sizeinmeters=?, marketsize=?, status=?  WHERE brandid=?";
					
					stmtRecCount 		= getSqlStatement("select count(*) from "+TABLE_NAME+" WHERE brandid=?");
					
					stmtInsert 			= getSqlStatement(insertQuery);
					stmtUpdate 			= getSqlStatement(updateQuery);
					
					stmtRecCount.bindString(1, shareOfShelfDO.brandid+"");
					
					long count = stmtRecCount.simpleQueryForLong();

					if(count > 0)
					{
						stmtUpdate.bindString(1, shareOfShelfDO.category);
						stmtUpdate.bindString(2, shareOfShelfDO.date);
						stmtUpdate.bindLong(3, shareOfShelfDO.categorysize);
						stmtUpdate.bindLong(4, shareOfShelfDO.sizeinmeters);
						stmtUpdate.bindLong(5, shareOfShelfDO.marketsize);
						stmtUpdate.bindString(6, shareOfShelfDO.status);
						stmtUpdate.bindString(7, shareOfShelfDO.brandid);
						LogUtils.errorLog("update Database med tblShareOfShelf ", "updated");
						stmtUpdate.executeInsert();
					}
					else
					{
						stmtInsert.bindString(1, shareOfShelfDO.brandid);
						stmtInsert.bindString(2, shareOfShelfDO.category);
						stmtInsert.bindString(3, shareOfShelfDO.date);
						stmtInsert.bindLong(4, shareOfShelfDO.categorysize);
						stmtInsert.bindLong(5, shareOfShelfDO.sizeinmeters);
						stmtInsert.bindLong(6, shareOfShelfDO.marketsize);
						stmtInsert.bindString(7, shareOfShelfDO.status);
						LogUtils.errorLog("Insert Database med tblShareOfShelf ", "updated");
						stmtInsert.executeInsert();
					}
					setTransaction();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					closeTransaction();
					if(stmtInsert != null)
						stmtInsert.close();
				}
			}
		}
	}
	
	public void insertCompetitor(Vector<ShareOfShelfDO> vecShareOfShelfDOs)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteStatement stmtRecCount = null,stmtInsert = null,stmtUpdate=null;
			if(vecShareOfShelfDOs != null && vecShareOfShelfDOs.size() >0)
			{
				try
				{
					openTransaction();
					String insertQuery 	= "INSERT INTO " + TABLE_NAME + "( brandid,category,date," +
							"categorysize,sizeinmeters,marketsize,status)" +
							" VALUES (?,?,?,?,?,?,?)";
					
					String updateQuery 	= "UPDATE "+TABLE_NAME+" set category=?, date=?,categorysize=?, sizeinmeters=?, marketsize=?, status=?  WHERE brandid=?";
					
					stmtRecCount 		= getSqlStatement("select count(*) from "+TABLE_NAME+" WHERE brandid=?");
					
					stmtInsert 			= getSqlStatement(insertQuery);
					stmtUpdate 			= getSqlStatement(updateQuery);
					
					for (ShareOfShelfDO shareOfShelfDO : vecShareOfShelfDOs) 
					{
						stmtRecCount.bindString(1, shareOfShelfDO.brandid+"");
						long count = stmtRecCount.simpleQueryForLong();
						if(count > 0)
						{
							stmtUpdate.bindString(1, shareOfShelfDO.category);
							stmtUpdate.bindString(2, shareOfShelfDO.date);
							stmtUpdate.bindLong(3, shareOfShelfDO.categorysize);
							stmtUpdate.bindLong(4, shareOfShelfDO.sizeinmeters);
							stmtUpdate.bindLong(5, shareOfShelfDO.marketsize);
							stmtUpdate.bindString(6, shareOfShelfDO.status);
							stmtUpdate.bindString(7, shareOfShelfDO.brandid);
							LogUtils.errorLog("update Database med tblShareOfShelf ", "updated");
							stmtUpdate.executeInsert();
						}
						else
						{
							stmtInsert.bindString(1, shareOfShelfDO.brandid);
							stmtInsert.bindString(2, shareOfShelfDO.category);
							stmtInsert.bindString(3, shareOfShelfDO.date);
							stmtInsert.bindLong(4, shareOfShelfDO.categorysize);
							stmtInsert.bindLong(5, shareOfShelfDO.sizeinmeters);
							stmtInsert.bindLong(6, shareOfShelfDO.marketsize);
							stmtInsert.bindString(7, shareOfShelfDO.status);
							LogUtils.errorLog("Insert Database med tblShareOfShelf ", "updated");
							stmtInsert.executeInsert();
						}
					}
					setTransaction();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					closeTransaction();
					if(stmtInsert != null)
						stmtInsert.close();
				}
			}
		}
	}
	
	public Vector<ShareOfShelfDO> getAllShareOfShelfDO(String categoryId)
	{
		Vector<ShareOfShelfDO> vecShareOfShelfDOs = new Vector<ShareOfShelfDO>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				String query = "";
				if(categoryId != null)
					query = "SELECT distinct TP.Brand,TB.BrandName,TP.Category, TP.Classifications  FROM tblProducts TP inner join tblBrand TB on TP.Brand = TB.BrandId where TP.Category='"+categoryId+"'";
				else
					query = "SELECT distinct TP.Brand,TB.BrandName,TP.Category, TP.Classifications  FROM tblProducts TP inner join tblBrand TB on TP.Brand = TB.BrandId GROUP BY TB.BrandName";
				
				Log.d("productQuery_ShareOfShelfDO", query);
				cursor = getCursor(query, null);
				if(cursor.moveToFirst())
				{
					do 
					{
						ShareOfShelfDO shareOfShelfDO = new ShareOfShelfDO();
						shareOfShelfDO.brandid = cursor.getString(0);
						shareOfShelfDO.brandName = cursor.getString(1);
						shareOfShelfDO.category = cursor.getString(2);
						shareOfShelfDO.subCategory = cursor.getString(3);
						vecShareOfShelfDOs.add(shareOfShelfDO);
					} while (cursor.moveToNext());
				}
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				closeDatabase();
			}
		}
		
		return vecShareOfShelfDOs;
	}
	
	public ShareOfShelfDO getShareOfShelfDO(String brandid)
	{
		ShareOfShelfDO shareOfShelfDO = new ShareOfShelfDO();
		
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				cursor = getCursor("SELECT * FROM " + TABLE_NAME +" where brandid = "+"'"+brandid+"'", null);
				if(cursor.moveToFirst())
				{
					do 
					{
						shareOfShelfDO.brandid = cursor.getString(cursor.getColumnIndex("brandid"));
						shareOfShelfDO.category = cursor.getString(cursor.getColumnIndex("category"));
						shareOfShelfDO.date = cursor.getString(cursor.getColumnIndex("date"));
						shareOfShelfDO.categorysize = cursor.getInt(cursor.getColumnIndex("categorysize"));
						shareOfShelfDO.sizeinmeters = cursor.getInt(cursor.getColumnIndex("sizeinmeters"));
						shareOfShelfDO.marketsize = cursor.getInt(cursor.getColumnIndex("marketsize"));
						shareOfShelfDO.status = cursor.getString(cursor.getColumnIndex("status"));
						
					} while (cursor.moveToNext());
				}
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				closeDatabase();
			}
		}
		
		return shareOfShelfDO;
	}
	
	
	public void deleteShareOFShelf()
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			try 
			{
				openDataBase();
				delete(null, null);
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				closeDatabase();
			}
		}
	}
}