package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.AddCustomerFilesDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.MyApplication;

public class CustomerDocumentDL extends BaseDL{
//	CREATE TABLE tblBrand
//	(
//	BrandId VARCHAR,
//	BrandName VARCHAR,
//	ParentCode VARCHAR
//	, "BrandImage" VARCHAR)
	private static final String CUSTOMER_DOCUMENT = "tblCustomerDoc";  //tblCustomerDoc
	public CustomerDocumentDL()
	{
		super(CUSTOMER_DOCUMENT);
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
	

	public void insertCustomerDocuments(Vector<AddCustomerFilesDO> vecAddCustomerFilesDO,String CustomerId){
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteDatabase objSqliteDB = null;
			SQLiteStatement stmtRecCount = null,stmtInsert = null,stmtUpdate=null;
			if(vecAddCustomerFilesDO != null && vecAddCustomerFilesDO.size() >0)
			{
				try
				{
					objSqliteDB = DatabaseHelper.openDataBase();
					
					String insertQuery 	= "INSERT INTO " + CUSTOMER_DOCUMENT + "(CustomerCode,DocName,ImagePath,Status,Date)" +
							" VALUES (?,?,?,?,?)";

					String updateQuery 	= "UPDATE "+CUSTOMER_DOCUMENT+" set DocName=?,Status=?,Date=?,ImagePath=? WHERE CustomerCode=?";

					stmtRecCount 		= objSqliteDB.compileStatement("select count(*) from "+CUSTOMER_DOCUMENT+" WHERE CustomerCode=?");

					stmtInsert 			= objSqliteDB.compileStatement(insertQuery);
					stmtUpdate 			= objSqliteDB.compileStatement(updateQuery);

					for (AddCustomerFilesDO addCustomerFilesDO : vecAddCustomerFilesDO) 
					{
						addCustomerFilesDO.CustomerCode = CustomerId;

						stmtRecCount.bindString(1, addCustomerFilesDO.CustomerCode+"");
						long count = stmtRecCount.simpleQueryForLong();
						if(count > 0)
						{
							stmtUpdate.bindString(1, addCustomerFilesDO.DocumentName+"");
							stmtUpdate.bindLong(2, StringUtils.getLong(addCustomerFilesDO.Status));
							stmtUpdate.bindString(3, addCustomerFilesDO.CreatedOn+"");
							stmtUpdate.bindString(4, addCustomerFilesDO.ServerFilePath+"");
							stmtUpdate.bindString(5, addCustomerFilesDO.CustomerCode+"");
							LogUtils.errorLog("update tblCustomerDoc", "updated");
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, addCustomerFilesDO.CustomerCode+"");
							stmtInsert.bindString(2, addCustomerFilesDO.DocumentName+"");
							stmtInsert.bindString(3, addCustomerFilesDO.ServerFilePath+"");
							stmtInsert.bindLong(4, StringUtils.getLong(addCustomerFilesDO.Status));
							stmtInsert.bindString(5, addCustomerFilesDO.CreatedOn+"");
							LogUtils.errorLog("update tblCustomerDoc", "updated");
							stmtInsert.executeInsert();
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					if(stmtInsert != null)
						stmtInsert.close();
					if(stmtUpdate != null)
						stmtUpdate.close();
					objSqliteDB.close();
				}
			}
		}
	}
	public void upadateCustomerDocuments(Vector<AddCustomerFilesDO> vecAddCustomerFilesDO){
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteDatabase objSqliteDB = null;
			SQLiteStatement stmtRecCount = null,stmtInsert = null,stmtUpdate=null;
			if(vecAddCustomerFilesDO != null && vecAddCustomerFilesDO.size() >0)
			{
				try
				{
					objSqliteDB = DatabaseHelper.openDataBase();
					
					String insertQuery 	= "INSERT INTO " + CUSTOMER_DOCUMENT + "(CustomerCode,DocName,ImagePath,Status,Date)" +
							" VALUES (?,?,?,?,?)";

					String updateQuery 	= "UPDATE "+CUSTOMER_DOCUMENT+" set DocName=?,Status=?,Date=?,ImagePath=? WHERE CustomerCode=?";

					stmtRecCount 		= objSqliteDB.compileStatement("select count(*) from "+CUSTOMER_DOCUMENT+" WHERE CustomerCode=?");

					stmtInsert 			= objSqliteDB.compileStatement(insertQuery);
					stmtUpdate 			= objSqliteDB.compileStatement(updateQuery);

					for (AddCustomerFilesDO addCustomerFilesDO : vecAddCustomerFilesDO) 
					{

						stmtRecCount.bindString(1, addCustomerFilesDO.CustomerCode+"");
						long count = stmtRecCount.simpleQueryForLong();
						if(count > 0)
						{
							stmtUpdate.bindString(1, addCustomerFilesDO.DocumentName+"");
							stmtUpdate.bindLong(2, StringUtils.getLong(addCustomerFilesDO.Status));
							stmtUpdate.bindString(3, addCustomerFilesDO.CreatedOn+"");
							stmtUpdate.bindString(4, addCustomerFilesDO.ServerFilePath+"");
							stmtUpdate.bindString(5, addCustomerFilesDO.CustomerCode+"");
							LogUtils.errorLog("update tblCustomerDoc", "updated");
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, addCustomerFilesDO.CustomerCode+"");
							stmtInsert.bindString(2, addCustomerFilesDO.DocumentName+"");
							stmtInsert.bindString(3, addCustomerFilesDO.ServerFilePath+"");
							stmtInsert.bindLong(4, StringUtils.getLong(addCustomerFilesDO.Status));
							stmtInsert.bindString(5, addCustomerFilesDO.CreatedOn+"");
							LogUtils.errorLog("insert tblCustomerDoc", "updated");
							stmtInsert.executeInsert();
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					if(stmtInsert != null)
						stmtInsert.close();
					if(stmtUpdate != null)
						stmtUpdate.close();
					objSqliteDB.close();
				}
			}
		}
	}
//	BrandId VARCHAR,
//	BrandName VARCHAR,
//	ParentCode VARCHAR
//	, "BrandImage" VARCHAR
	public Vector<AddCustomerFilesDO> getDocumentsByCustomer(String customerId,String status){
		Vector<AddCustomerFilesDO> vecAddCustomerFilesDO = new Vector<AddCustomerFilesDO>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				String distnictSelectQuery= "SELECT * FROM " + CUSTOMER_DOCUMENT +" Where CustomerCode='"+customerId+"' and Status='"+status+"' ";
				cursor = getCursor(distnictSelectQuery, null);
				
				if(cursor.moveToFirst())
				{
					do {
						AddCustomerFilesDO addCustomerFilesDO = new AddCustomerFilesDO();
						addCustomerFilesDO.CustomerCode = cursor.getString(cursor.getColumnIndex("CustomerCode"));
						addCustomerFilesDO.DocumentName = cursor.getString(cursor.getColumnIndex("DocName"));
						addCustomerFilesDO.ServerFilePath = cursor.getString(cursor.getColumnIndex("ImagePath"));
						addCustomerFilesDO.Status = cursor.getString(cursor.getColumnIndex("Status"));
						addCustomerFilesDO.CreatedOn = cursor.getString(cursor.getColumnIndex("Date"));
						vecAddCustomerFilesDO.add(addCustomerFilesDO);
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
		
		return vecAddCustomerFilesDO;
	}
	public Vector<AddCustomerFilesDO> getAllLocalDocuments(String status)
	{
		Vector<AddCustomerFilesDO> vecAddCustomerFilesDO = new Vector<AddCustomerFilesDO>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteDatabase sqLiteDatabase 	= null;
			Cursor cursor = null;
			try 
			{ 
				sqLiteDatabase 	= 	DatabaseHelper.openDataBase();
				String query 	= 	"SELECT * FROM " + CUSTOMER_DOCUMENT +" Where Status='"+status+"' ";
				cursor 			= 	sqLiteDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do {
						AddCustomerFilesDO addCustomerFilesDO = new AddCustomerFilesDO();
						addCustomerFilesDO.CustomerCode = cursor.getString(cursor.getColumnIndex("CustomerCode"));
						addCustomerFilesDO.DocumentName = cursor.getString(cursor.getColumnIndex("DocName"));
						addCustomerFilesDO.ServerFilePath = cursor.getString(cursor.getColumnIndex("ImagePath"));
						addCustomerFilesDO.Status = cursor.getString(cursor.getColumnIndex("Status"));
						addCustomerFilesDO.CreatedOn = cursor.getString(cursor.getColumnIndex("Date"));
						vecAddCustomerFilesDO.add(addCustomerFilesDO);
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
		
		return vecAddCustomerFilesDO;
	}
	
	
	public int getCompletedSurveyCount(String UserId)
	{
		int surveyCount=0;
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteDatabase sqLiteDatabase 	= null;
			Cursor cursor = null;
			
			try 
			{ 
				sqLiteDatabase 	= 	DatabaseHelper.openDataBase();
				String query 	= 	"SELECT count(*) from tblSurvey S Inner Join tblUserSurveyAnswer US on S.SurveyId=US.SurveyId and US.UserId='"+UserId+"' ";
				cursor 			= 	sqLiteDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do 
					{
						surveyCount =cursor.getInt(0);
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
			}
		}
		
		return surveyCount;
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
				String distnictSelectQuery= "SELECT BrandName FROM "+ CUSTOMER_DOCUMENT +" where BrandId ='"+ brandID +"'";
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
				String distnictSelectQuery= "SELECT BrandId FROM "+ CUSTOMER_DOCUMENT +" GROUP BY BrandId";
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
				String distnictSelectQuery= "SELECT BrandImage FROM "+ CUSTOMER_DOCUMENT +" where BrandId ='"+ brandID +"'";
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