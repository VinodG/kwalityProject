package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.winit.alseer.salesman.dataobject.StoreAuditQuestionsDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.sfa.salesman.MyApplication;

public class StoreAuditQuestionsDA extends BaseDL
{
	private static final String TABLE_NAME = "tblStoreAuditQuestions";
	
	public StoreAuditQuestionsDA()
	{
		super(TABLE_NAME);
	}

	public void insertStoreAuditQuestions(StoreAuditQuestionsDO storeAuditQuestionsDO)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteStatement stmtRecCount = null,stmtInsert = null,stmtUpdate=null;
			if(storeAuditQuestionsDO != null)
			{
				try
				{
					openTransaction();
					String insertQuery 	= "INSERT INTO " + TABLE_NAME + "( questionid,question,category," +
							"type,customerid)" +
							" VALUES (?,?,?,?,?)";
					
					String updateQuery 	= "UPDATE "+TABLE_NAME+" set question=?, category=?,type=?, customerid=? WHERE questionid=?";
					
					stmtRecCount 		= getSqlStatement("select count(*) from "+TABLE_NAME+" WHERE questionid=?");
					
					stmtInsert 			= getSqlStatement(insertQuery);
					stmtUpdate 			= getSqlStatement(updateQuery);
					
					stmtRecCount.bindString(1, storeAuditQuestionsDO.questionid+"");
					
					long count = stmtRecCount.simpleQueryForLong();

					if(count > 0)
					{
						stmtUpdate.bindString(1, storeAuditQuestionsDO.question);
						stmtUpdate.bindString(2, storeAuditQuestionsDO.category);
						stmtUpdate.bindString(3, storeAuditQuestionsDO.type);
						stmtUpdate.bindString(4, storeAuditQuestionsDO.customerid);
						stmtUpdate.bindString(5, storeAuditQuestionsDO.questionid);
						LogUtils.errorLog("update Database med tblStoreAuditQuestions ", "updated");
						stmtUpdate.executeInsert();
					}
					else
					{
						stmtInsert.bindString(1, storeAuditQuestionsDO.questionid);
						stmtInsert.bindString(2, storeAuditQuestionsDO.question);
						stmtInsert.bindString(3, storeAuditQuestionsDO.category);
						stmtInsert.bindString(4, storeAuditQuestionsDO.type);
						stmtInsert.bindString(5, storeAuditQuestionsDO.customerid);
						LogUtils.errorLog("Insert Database med tblStoreAuditQuestions ", "updated");
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
	
	public void insertStoreAuditQuestions(Vector<StoreAuditQuestionsDO> vecStoreAuditQuestionsDOs)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteStatement stmtRecCount = null,stmtInsert = null,stmtUpdate=null;
			if(vecStoreAuditQuestionsDOs != null && vecStoreAuditQuestionsDOs.size() >0)
			{
				try
				{
					openTransaction();
					String insertQuery 	= "INSERT INTO " + TABLE_NAME + "( questionid,question,category," +
							"type,customerid)" +
							" VALUES (?,?,?,?,?)";
					
					String updateQuery 	= "UPDATE "+TABLE_NAME+" set question=?, category=?,type=?, customerid=? WHERE questionid=?";
					
					stmtRecCount 		= getSqlStatement("select count(*) from "+TABLE_NAME+" WHERE questionid=?");
					
					stmtInsert 			= getSqlStatement(insertQuery);
					stmtUpdate 			= getSqlStatement(updateQuery);
					
					for (StoreAuditQuestionsDO storeAuditQuestionsDO : vecStoreAuditQuestionsDOs) 
					{
						stmtRecCount.bindString(1, storeAuditQuestionsDO.questionid+"");
						long count = stmtRecCount.simpleQueryForLong();
						
						if(count > 0)
						{
							stmtUpdate.bindString(1, storeAuditQuestionsDO.question);
							stmtUpdate.bindString(2, storeAuditQuestionsDO.category);
							stmtUpdate.bindString(3, storeAuditQuestionsDO.type);
							stmtUpdate.bindString(4, storeAuditQuestionsDO.customerid);
							stmtUpdate.bindString(5, storeAuditQuestionsDO.questionid);
							LogUtils.errorLog("update Database med tblStoreAuditQuestions ", "updated");
							stmtUpdate.executeInsert();
						}
						else
						{
							stmtInsert.bindString(1, storeAuditQuestionsDO.questionid);
							stmtInsert.bindString(2, storeAuditQuestionsDO.question);
							stmtInsert.bindString(3, storeAuditQuestionsDO.category);
							stmtInsert.bindString(4, storeAuditQuestionsDO.type);
							stmtInsert.bindString(5, storeAuditQuestionsDO.customerid);
							LogUtils.errorLog("Insert Database med tblStoreAuditQuestions ", "updated");
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
	
	
	public Vector<StoreAuditQuestionsDO> getAllStoreAuditQuestions(String categoryName,String type)
	{
		Vector<StoreAuditQuestionsDO> vecStoreAuditQuestionsDOs = new Vector<StoreAuditQuestionsDO>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				String query = "";
				if(categoryName != null)
					query = "SELECT * FROM " + TABLE_NAME +" where category='"+categoryName+"' and  type like '%"+type+"%' order by rowid DESC ";
				else
					query = "SELECT * FROM " + TABLE_NAME +" where type like '%"+type+"%' order by rowid DESC ";
				
				Log.d("productQuery_StoreAuditQuestions", query);
				cursor = getCursor(query, null);
//				cursor = getCursor("SELECT * FROM " + TABLE_NAME +" where type='"+type+"' order by rowid DESC ", null);
				
				if(cursor.moveToFirst())
				{
					do 
					{
						StoreAuditQuestionsDO questionsDO = new StoreAuditQuestionsDO();
						questionsDO.questionid = cursor.getString(cursor.getColumnIndex("questionid"));
						questionsDO.question = cursor.getString(cursor.getColumnIndex("question"));
						questionsDO.category = cursor.getString(cursor.getColumnIndex("category"));
						questionsDO.type = cursor.getString(cursor.getColumnIndex("type"));
						questionsDO.customerid = cursor.getString(cursor.getColumnIndex("customerid"));
						
						vecStoreAuditQuestionsDOs.add(questionsDO);
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
		
		return vecStoreAuditQuestionsDOs;
	}
	
	public StoreAuditQuestionsDO getStoreAuditQuestions(String questionId)
	{
		StoreAuditQuestionsDO questionsDO = new StoreAuditQuestionsDO();
		
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				cursor = getCursor("SELECT * FROM " + TABLE_NAME +" where questionid = "+"'"+questionId+"'", null);
				if(cursor.moveToFirst())
				{
					do 
					{
						questionsDO.questionid = cursor.getString(cursor.getColumnIndex("questionid"));
						questionsDO.question = cursor.getString(cursor.getColumnIndex("question"));
						questionsDO.category = cursor.getString(cursor.getColumnIndex("category"));
						questionsDO.type = cursor.getString(cursor.getColumnIndex("type"));
						questionsDO.customerid = cursor.getString(cursor.getColumnIndex("customerid"));
						
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
		
		return questionsDO;
	}
	
	
	public void deleteStoreAuditQuestions()
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