package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.dataobject.BrandDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.sfa.salesman.MyApplication;

public class BrandsDL extends BaseDL{
//	CREATE TABLE tblBrand
//	(
//	BrandId VARCHAR,
//	BrandName VARCHAR,
//	ParentCode VARCHAR
//	, "BrandImage" VARCHAR)
	private static final String BRANDS_TABLE = "tblBrand";
	public BrandsDL()
	{
		super(BRANDS_TABLE);
	}

	public void insertBrands(Vector<BrandDO> vecBrandDOs){
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteStatement stmtRecCount = null,stmtInsert = null,stmtUpdate=null;
			if(vecBrandDOs != null && vecBrandDOs.size() >0)
			{
				try
				{
					openTransaction();
					String insertQuery 	= "INSERT INTO " + BRANDS_TABLE + "( BrandId,BrandName,ParentCode,BrandImage)" +
							" VALUES (?,?,?,?)";
					
					String updateQuery 	= "UPDATE "+BRANDS_TABLE+" set BrandName=?,ParentCode=?, BrandImage=? WHERE BrandId=?";
					
					stmtRecCount 		= getSqlStatement("select count(*) from "+BRANDS_TABLE+" WHERE BrandId=?");
					
					stmtInsert 			= getSqlStatement(insertQuery);
					stmtUpdate 			= getSqlStatement(updateQuery);
					
					for (BrandDO BrandDO : vecBrandDOs) 
					{
						stmtRecCount.bindString(1, BrandDO.brandId+"");
						long count = stmtRecCount.simpleQueryForLong();
						if(count > 0)
						{
							stmtUpdate.bindString(1, BrandDO.brandName);
							stmtUpdate.bindString(2, "ParentCode");
							stmtUpdate.bindString(3, BrandDO.image);
							stmtUpdate.bindString(4, BrandDO.brandId+"");
							LogUtils.errorLog("update Database brands", "updated");
							stmtUpdate.executeInsert();
						}
						else
						{
							stmtInsert.bindString(1, BrandDO.brandId+"");
							stmtInsert.bindString(2, BrandDO.brandName);
							stmtInsert.bindString(3, "ParentCode");
							stmtInsert.bindString(4, BrandDO.image);
							LogUtils.errorLog("update Database brands", "updated");
							stmtInsert.executeInsert();
					}
					setTransaction();
				}
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
//	BrandId VARCHAR,
//	BrandName VARCHAR,
//	ParentCode VARCHAR
//	, "BrandImage" VARCHAR
	public Vector<BrandDO> getAllBrands(){
		Vector<BrandDO> vecBrandDOs = new Vector<BrandDO>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				String distnictSelectQuery= "SELECT * FROM " + BRANDS_TABLE +" GROUP BY BrandId";
				String normalQuery = "SELECT * FROM tblBrand";
				cursor = getCursor(distnictSelectQuery, null);
				if(cursor.moveToFirst())
				{
					do {
						BrandDO BrandDO = new BrandDO();
						BrandDO.brandId = cursor.getString(cursor.getColumnIndex("BrandId"));
						BrandDO.brandName = cursor.getString(cursor.getColumnIndex("BrandName"));
						BrandDO.image = cursor.getString(cursor.getColumnIndex("BrandImage"));
						vecBrandDOs.add(BrandDO);
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
		
		return vecBrandDOs;
	}
	
	public String getBrandNamefromBrandID(String brandID)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			String brandName = "";
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				String distnictSelectQuery= "SELECT BrandName FROM "+ BRANDS_TABLE +" where BrandId ='"+ brandID +"'";
				cursor = getCursor(distnictSelectQuery, null);
				if(cursor.moveToFirst())
				{
					do 
					{
						brandName = cursor.getString(cursor.getColumnIndex("BrandName"));
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
			return brandName;
		}
	}
	
	public Vector<String> getBrandAllBrandID()
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			String brandName = "";
			Cursor cursor = null;
			Vector<String> vecBrandId = new Vector<String>();
			try 
			{ 
				openDataBase();
				String distnictSelectQuery= "SELECT BrandId FROM "+ BRANDS_TABLE +" GROUP BY BrandId";
				cursor = getCursor(distnictSelectQuery, null);
				if(cursor.moveToFirst())
				{
					do 
					{
						brandName = cursor.getString(cursor.getColumnIndex("BrandId"));
						vecBrandId.add(brandName);
					} 
					while (cursor.moveToNext());
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
			return vecBrandId;
		}
	}
	
	public String getBrandNamefromBrandImage(String brandID)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			String brandName = "";
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				String distnictSelectQuery= "SELECT BrandImage FROM "+ BRANDS_TABLE +" where BrandId ='"+ brandID +"'";
				cursor = getCursor(distnictSelectQuery, null);
				if(cursor.moveToFirst())
				{
					do 
					{
						brandName = cursor.getString(cursor.getColumnIndex("BrandImage"));
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
			return brandName;
		}
	}
	
	public void deleteBrandsByBrandsId(String brandid)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			try 
			{
				openDataBase();
				delete("BrandId = "+brandid, null);
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
	
	public void deleteBrands()
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