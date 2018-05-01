/**
 * 
 */
package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.SellingSKUClassification;
import com.winit.sfa.salesman.MyApplication;

/**
 * @author Aritra.Pal
 *
 */
public class SellingSKUClassificationsDA 
{

	/**
	 * @param vecSellingSKU
	 */
//	public boolean insertSellingSKU(Vector<SellingSKU> vecSellingSKU) 
//	{
//		synchronized(MyApplication.MyLock) 
//		{
//			SQLiteDatabase objSqLiteDatabase = null;
//			try
//			{
//				objSqLiteDatabase = DatabaseHelper.openDataBase();
//				SQLiteStatement sqLiteStatement = objSqLiteDatabase.compileStatement("INSERT INTO tblSellingSKUClassification (SellingSKUClassificationId, Code, Description, Sequence, SalesOrgCode, IsActive, ModifiedDate, ModifiedTime) VALUES(?,?,?,?,?,?,?,?)");
//				for(int i = 0; vecSellingSKU !=null && i < vecSellingSKU.size(); i++)
//				{
//					SellingSKU objSellingSKUDO = vecSellingSKU.get(i);
//					
//					sqLiteStatement.bindLong(1, objSellingSKUDO.SellingSkusClassificationId);
//					sqLiteStatement.bindString(2, objSellingSKUDO.Code);
//					sqLiteStatement.bindString(3, objSellingSKUDO.Description);
//					sqLiteStatement.bindLong(4, objSellingSKUDO.Sequence);
//					sqLiteStatement.bindString(5, objSellingSKUDO.SalesOrgCode);
//					sqLiteStatement.bindLong(6, objSellingSKUDO.IsActive == true? 1 : 0);
//					sqLiteStatement.bindLong(7, objSellingSKUDO.ModifiedDate);
//					sqLiteStatement.bindLong(8, objSellingSKUDO.ModifiedTime);
//					
//					sqLiteStatement.executeInsert();
//				}
//				sqLiteStatement.close();
//				
//				return true;
//			}
//			catch(Exception ex)
//			{
//				ex.printStackTrace();
//				return false;
//			}
//			finally
//			{
//				if(objSqLiteDatabase!=null)
//				{
//					objSqLiteDatabase.close();
//				}
//			}
//		}
//	}
	
	
	public boolean insertSellingSKU(Vector<SellingSKUClassification> vecSellingSKU)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblSellingSKUClassification WHERE Code = ?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblSellingSKUClassification (SellingSKUClassificationId, Code, Description, Sequence, SalesOrgCode, IsActive, ModifiedDate, ModifiedTime) VALUES(?,?,?,?,?,?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblSellingSKUClassification SET SellingSKUClassificationId=?, Description=?, Sequence=?, SalesOrgCode=?, IsActive=?, ModifiedDate=?, ModifiedTime=? WHERE Code = ?");
			
			for(SellingSKUClassification objSellingSKUDO : vecSellingSKU)
			{
				stmtSelectRec.bindString(1, objSellingSKUDO.Code);
				long countRec = stmtSelectRec.simpleQueryForLong();
				
				if(objSellingSKUDO != null)
				{
					if (countRec != 0) 
					{
						stmtUpdate.bindLong(1, objSellingSKUDO.SellingSkusClassificationId);
						stmtUpdate.bindString(2, objSellingSKUDO.Description);
						stmtUpdate.bindLong(3,objSellingSKUDO.Sequence);
						stmtUpdate.bindString(4, objSellingSKUDO.SalesOrgCode);
						stmtUpdate.bindLong(5, objSellingSKUDO.IsActive == true? 1 : 0);
						stmtUpdate.bindLong(6, objSellingSKUDO.ModifiedDate);
						stmtUpdate.bindLong(7, objSellingSKUDO.ModifiedTime);
						stmtUpdate.bindString(8, objSellingSKUDO.Code);
						
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindLong(1, objSellingSKUDO.SellingSkusClassificationId);
						stmtInsert.bindString(2, objSellingSKUDO.Code);
						stmtInsert.bindString(3, objSellingSKUDO.Description);
						stmtInsert.bindLong(4, objSellingSKUDO.Sequence);
						stmtInsert.bindString(5, objSellingSKUDO.SalesOrgCode);
						stmtInsert.bindLong(6, objSellingSKUDO.IsActive == true? 1 : 0);
						stmtInsert.bindLong(7, objSellingSKUDO.ModifiedDate);
						stmtInsert.bindLong(8, objSellingSKUDO.ModifiedTime);
						
						stmtInsert.executeInsert();
					}
				}
			}
			
			stmtSelectRec.close();
			stmtUpdate.close();
			stmtInsert.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
		finally
		{
			if(objSqliteDB != null)
			{
				
				objSqliteDB.close();
			}
		}
		return true;
	}
	
	public Vector<SellingSKUClassification> getAllSellingSKUClassifications()
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<SellingSKUClassification> vecSellingSKU = null;
			SellingSKUClassification objSellingSKU = null;
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursor =null;
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				String query = "Select SellingSKUClassificationId,Description from tblSellingSKUClassification  where (IsActive='True'  OR IsActive =1) order by Sequence  asc";
				cursor = sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					vecSellingSKU = new Vector<SellingSKUClassification>();
					do
					{
						objSellingSKU = new SellingSKUClassification();
						objSellingSKU.SellingSkusClassificationId		= cursor.getInt(0);
						objSellingSKU.Description						= cursor.getString(1);
						vecSellingSKU.add(objSellingSKU);
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
			}
			return vecSellingSKU;
		}
	}
	
	public SellingSKUClassification getCustomerSellingSKUGroup(JourneyPlanDO journeyPlanDO, SQLiteDatabase sqLiteDatabase)
	{
		synchronized(MyApplication.MyLock) 
		{
			SellingSKUClassification gSellingSKU = null;
			Cursor cursor =null, cursor2= null;
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT Name, FieldName FROM tblCustomerSellingSKUGroup ORDER BY Priority DESC";
				cursor = sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						SellingSKUClassification sellingSKU = new SellingSKUClassification();
						sellingSKU.Name		= cursor.getString(0);
						sellingSKU.FieldName= cursor.getString(1);
						
						query = "SELECT COUNT(*) FROM tblProducts P INNER JOIN tblSellingSKU S ON S.ItemCode = P.ItemCode "+ 
								"WHERE GroupType = '"+sellingSKU.Name+"'";
						
						String temp = "";
						if(sellingSKU.FieldName.equalsIgnoreCase("Code"))
						{
							sellingSKU.Code = journeyPlanDO.site;
							temp = " AND GroupCode ='"+journeyPlanDO.site+"' ";
						}
						else if(sellingSKU.FieldName.equalsIgnoreCase("Attribute8"))
						{
							sellingSKU.Code = journeyPlanDO.Attribute8;
							temp = " AND GroupCode ='"+journeyPlanDO.Attribute8+"' "; 
						}
						else if(sellingSKU.FieldName.equalsIgnoreCase("ChannelCode"))
						{
							sellingSKU.Code = journeyPlanDO.channelCode;
							temp = " AND GroupCode ='"+journeyPlanDO.channelCode+"' ";
						}
						
						query = query + temp;
						
						cursor2 = sqLiteDatabase.rawQuery(query, null);
						if(cursor2.moveToFirst())
						{
							int count = cursor2.getInt(0);
							if(count > 0)
							{
								gSellingSKU = sellingSKU;
								break;
							}
						}
						
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
				if(cursor2 != null && !cursor2.isClosed())
					cursor2.close();
			}
			return gSellingSKU;
		}
	}
	
	public SellingSKUClassification getCustomerNewSellingSKUGroup(JourneyPlanDO journeyPlanDO, SQLiteDatabase sqLiteDatabase)
	{
		synchronized(MyApplication.MyLock) 
		{
			SellingSKUClassification gSellingSKU = new SellingSKUClassification();
			
			Cursor cursor =null, cursor2= null;
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM tblSellingSKUClassification where Description = "+"'"+"New Selling"+"'";
				
				cursor = sqLiteDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						gSellingSKU.SellingSkusClassificationId = cursor.getInt(cursor.getColumnIndex("SellingSKUClassificationId"));
						gSellingSKU.Code = cursor.getString(cursor.getColumnIndex("Code"));
						gSellingSKU.Description = cursor.getString(cursor.getColumnIndex("Description"));
						gSellingSKU.Sequence = cursor.getInt(cursor.getColumnIndex("Sequence"));
						gSellingSKU.SalesOrgCode = cursor.getString(cursor.getColumnIndex("SalesOrgCode"));
						gSellingSKU.IsActive = Boolean.parseBoolean(cursor.getColumnIndex("IsActive")+"");
						gSellingSKU.ModifiedDate = cursor.getInt(cursor.getColumnIndex("ModifiedDate"));
						gSellingSKU.ModifiedTime = cursor.getInt(cursor.getColumnIndex("ModifiedTime"));
						
						
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
				if(cursor2 != null && !cursor2.isClosed())
					cursor2.close();
			}
			return gSellingSKU;
		}
	}

}
