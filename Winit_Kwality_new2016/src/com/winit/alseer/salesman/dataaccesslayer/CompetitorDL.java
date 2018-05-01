package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.dataobject.CompetitorItemDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.sfa.salesman.MyApplication;

public class CompetitorDL extends BaseDL
{
	private static final String COMPETITER_TABLE = "tblCompetitor";
	private static final String COMPETITER_BRANDS = "tblCompetitorBrand";
	
	public CompetitorDL()
	{
		super(COMPETITER_TABLE);
	}

	public void insertCompetitor(CompetitorItemDO competitorDO){
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteStatement stmtRecCount = null,stmtInsert = null,stmtUpdate=null;
			if(competitorDO != null)
			{
				try
				{
					openTransaction();
					String insertQuery 	= "INSERT INTO " + COMPETITER_TABLE + "( UID,competetor_id,category," +
							"ourBrand,brandname,company,imagepath,store,price,activity,createdon)" +
							" VALUES (?,?,?,?,?,?,?,?,?,?,?)";
					
					String updateQuery 	= "UPDATE "+COMPETITER_TABLE+" set UID=?, category=?,ourBrand=?, brandname=?, company=?, imagepath=? ,store=? ,price=? ,activity=? ,createdon=? WHERE competetor_id=?";
					
					stmtRecCount 		= getSqlStatement("select count(*) from "+COMPETITER_TABLE+" WHERE competetor_id=?");
					
					stmtInsert 			= getSqlStatement(insertQuery);
					stmtUpdate 			= getSqlStatement(updateQuery);
					
					stmtRecCount.bindString(1, competitorDO.competetor_id+"");
					
					long count = stmtRecCount.simpleQueryForLong();

					if(count > 0)
					{
						stmtUpdate.bindString(1, competitorDO.UID);
						stmtUpdate.bindString(2, competitorDO.category);
						stmtUpdate.bindString(3, competitorDO.ourBrand);
						stmtUpdate.bindString(4, competitorDO.brandname);
						stmtUpdate.bindString(5, competitorDO.company);
						stmtUpdate.bindString(6, competitorDO.imagepath);
						stmtUpdate.bindString(7, competitorDO.store);
						stmtUpdate.bindString(8, competitorDO.price);
						stmtUpdate.bindString(9, competitorDO.activity);
						stmtUpdate.bindString(10, competitorDO.createdon);
						stmtUpdate.bindString(11, competitorDO.competetor_id);
						LogUtils.errorLog("update Database med tblCompetitor ", "updated");
						stmtUpdate.executeInsert();
					}
					else
					{
						stmtInsert.bindString(1, competitorDO.UID);
						stmtInsert.bindString(2, competitorDO.competetor_id);
						stmtInsert.bindString(3, competitorDO.category);
						stmtInsert.bindString(4, competitorDO.ourBrand);
						stmtInsert.bindString(5, competitorDO.brandname);
						stmtInsert.bindString(6, competitorDO.company);
						stmtInsert.bindString(7, competitorDO.imagepath);
						stmtInsert.bindString(8, competitorDO.store);
						stmtInsert.bindString(9, competitorDO.price);
						stmtInsert.bindString(10, competitorDO.activity);
						stmtInsert.bindString(11, competitorDO.createdon);
						LogUtils.errorLog("Insert Database med tblCompetitor ", "updated");
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
	
	public void insertCompetitor(Vector<CompetitorItemDO> vecCompetitorDOs){
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteStatement stmtRecCount = null,stmtInsert = null,stmtUpdate=null;
			if(vecCompetitorDOs != null && vecCompetitorDOs.size() >0)
			{
				try
				{
					openTransaction();
					String insertQuery 	= "INSERT INTO " + COMPETITER_TABLE + "( UID,competetor_id,category," +
							"ourBrand,brandname,company,imagepath,store,price,activity,createdon)" +
							" VALUES (?,?,?,?,?,?,?,?,?,?,?)";
					
					String updateQuery 	= "UPDATE "+COMPETITER_TABLE+" set UID=?, category=?,ourBrand=?, brandname=?, company=?, imagepath=? ,store=? ,price=? ,activity=? ,createdon=? WHERE competetor_id=?";
					
					stmtRecCount 		= getSqlStatement("select count(*) from "+COMPETITER_TABLE+" WHERE competetor_id=?");
					
					stmtInsert 			= getSqlStatement(insertQuery);
					stmtUpdate 			= getSqlStatement(updateQuery);
					
					for (CompetitorItemDO competitorDO : vecCompetitorDOs) 
					{
						stmtRecCount.bindString(1, competitorDO.competetor_id+"");
						long count = stmtRecCount.simpleQueryForLong();
						if(count > 0)
						{
							stmtUpdate.bindString(1, competitorDO.UID);
							stmtUpdate.bindString(2, competitorDO.category);
							stmtUpdate.bindString(3, competitorDO.ourBrand);
							stmtUpdate.bindString(4, competitorDO.brandname);
							stmtUpdate.bindString(5, competitorDO.company);
							stmtUpdate.bindString(6, competitorDO.imagepath);
							stmtUpdate.bindString(7, competitorDO.store);
							stmtUpdate.bindString(8, competitorDO.price);
							stmtUpdate.bindString(9, competitorDO.activity);
							stmtUpdate.bindString(10, competitorDO.createdon);
							stmtUpdate.bindString(11, competitorDO.competetor_id);
							LogUtils.errorLog("update Database med tblCompetitor ", "updated");
							stmtUpdate.executeInsert();
						}
						else
						{
							stmtInsert.bindString(1, competitorDO.UID);
							stmtInsert.bindString(2, competitorDO.competetor_id);
							stmtInsert.bindString(3, competitorDO.category);
							stmtInsert.bindString(4, competitorDO.ourBrand);
							stmtInsert.bindString(5, competitorDO.brandname);
							stmtInsert.bindString(6, competitorDO.company);
							stmtInsert.bindString(7, competitorDO.imagepath);
							stmtInsert.bindString(8, competitorDO.store);
							stmtInsert.bindString(9, competitorDO.price);
							stmtInsert.bindString(10, competitorDO.activity);
							stmtInsert.bindString(11, competitorDO.createdon);
							LogUtils.errorLog("Insert Database med tblCompetitor ", "updated");
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
	
	public void updateCompetitorPrice(String _id, String createdon, String price)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
				SQLiteStatement stmtUpdate = null;
				try
				{
					openTransaction();
					String updateQuery 	= "UPDATE " + COMPETITER_TABLE + " SET price = '"+price+"' WHERE _id = '"+_id+"' AND createdon = '"+createdon+"'";
					
					stmtUpdate 	= getSqlStatement(updateQuery);
					stmtUpdate.execute();
					setTransaction();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					closeTransaction();
					stmtUpdate.close();
				}
		}
	}
	
	public Vector<CompetitorItemDO> getAllCompetitorDO(){
		Vector<CompetitorItemDO> vecCompetitorDOs = new Vector<CompetitorItemDO>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				cursor = getCursor("SELECT * FROM " + COMPETITER_TABLE +" order by rowid DESC ", null);
				if(cursor.moveToFirst())
				{
					do {
						CompetitorItemDO competitorDO = new CompetitorItemDO();
						competitorDO.UID = cursor.getString(cursor.getColumnIndex("UID"));
						competitorDO.competetor_id = cursor.getString(cursor.getColumnIndex("competetor_id"));
						competitorDO.category = cursor.getString(cursor.getColumnIndex("category"));
						competitorDO.ourBrand = cursor.getString(cursor.getColumnIndex("ourBrand"));
						competitorDO.brandname = cursor.getString(cursor.getColumnIndex("brandname"));
						competitorDO.company = cursor.getString(cursor.getColumnIndex("company"));
						competitorDO.imagepath = cursor.getString(cursor.getColumnIndex("imagepath"));
						competitorDO.store = cursor.getString(cursor.getColumnIndex("store"));
						competitorDO.price = cursor.getString(cursor.getColumnIndex("price"));
						competitorDO.activity = cursor.getString(cursor.getColumnIndex("activity"));
						competitorDO.createdon = cursor.getString(cursor.getColumnIndex("createdon"));
						vecCompetitorDOs.add(competitorDO);
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
		
		return vecCompetitorDOs;
	}
	
	public Vector<CompetitorItemDO> getAllCompetitorBrandDO(){
		Vector<CompetitorItemDO> vecCompetitorDOs = new Vector<CompetitorItemDO>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				cursor = getCursor("SELECT * FROM " + COMPETITER_BRANDS +" order by rowid DESC ", null);
				if(cursor.moveToFirst())
				{
					do 
					{
						CompetitorItemDO competitorDO = new CompetitorItemDO();
						competitorDO.UID = cursor.getString(cursor.getColumnIndex("UID"));
						competitorDO.competetor_id = cursor.getString(cursor.getColumnIndex("CompetitorBrandId"));
						competitorDO.brandname = cursor.getString(cursor.getColumnIndex("BrandName"));
						competitorDO.company = cursor.getString(cursor.getColumnIndex("Company"));
						competitorDO.activity = cursor.getString(cursor.getColumnIndex("Type"));
						vecCompetitorDOs.add(competitorDO);
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
		
		return vecCompetitorDOs;
	}
	
	public Vector<String> getAllCompany(){
		Vector<String> vecCompany = new Vector<String>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				cursor = getCursor("SELECT Distinct Company FROM " + COMPETITER_TABLE +" order by rowid DESC ", null);
				if(cursor.moveToFirst())
				{
					do {
						String brand = cursor.getString(cursor.getColumnIndex("Company"));
						vecCompany.add(brand);
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
		
		return vecCompany;
	}
	
	public Vector<String> getAllCompetitorCompanys()
	{
		Vector<String> vecCompany = new Vector<String>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				cursor = getCursor("SELECT Distinct Company FROM " + COMPETITER_BRANDS +" order by rowid DESC ", null);
				if(cursor.moveToFirst())
				{
					do {
						String brand = cursor.getString(cursor.getColumnIndex("Company"));
						vecCompany.add(brand);
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
		
		return vecCompany;
	}
	
	public Vector<String> getAllCompetitorBrands()
	{
		Vector<String> vecCompany = new Vector<String>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				cursor = getCursor("SELECT Distinct BrandName FROM " + COMPETITER_BRANDS +" order by rowid DESC ", null);
				if(cursor.moveToFirst())
				{
					do {
						String brand = cursor.getString(cursor.getColumnIndex("BrandName"));
						vecCompany.add(brand);
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
		
		return vecCompany;
	}
	
	public Vector<String> getAllBrandsByCompany(String company){
		Vector<String> vecbrands = new Vector<String>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			
//			 ("UID" INTEGER,"competetor_id" VARCHAR PRIMARY KEY  DEFAULT (null) ,"category" VARCHAR NOT NULL ,"ourBrand" 
//					 VARCHAR NOT NULL ,"brandname" VARCHAR,"company" VARCHAR,"imagepath" VARCHAR,"store" VARCHAR,"price" 
//					 VARCHAR,"activity" VARCHAR,"createdon" VARCHAR)
			 
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				cursor = getCursor("SELECT Distinct brandname FROM " + COMPETITER_TABLE +" where company = '"+company+"' order by rowid DESC ", null);
				if(cursor.moveToFirst())
				{
					do {
						String brand = cursor.getString(cursor.getColumnIndex("brandname"));
						vecbrands.add(brand);
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
		
		return vecbrands;
	}
	
	public void deleteCompetitorByCompetitorId(String CompetitorId)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			try 
			{
				openDataBase();
				delete("competetor_id = "+CompetitorId, null);
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
	
	public void deleteCompetitor()
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