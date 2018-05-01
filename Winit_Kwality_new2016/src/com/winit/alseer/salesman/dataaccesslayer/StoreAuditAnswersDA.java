package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.StoreAuditQuestionsDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.sfa.salesman.MyApplication;

public class StoreAuditAnswersDA
{
	private static final String TABLE_NAME = "tblStoreAuditAnswers";
	
	public void insertStoreAuditQuestions(StoreAuditQuestionsDO storeAuditQuestionsDO)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteDatabase objSqliteDB = null;
			SQLiteStatement stmtRecCount = null,stmtInsert = null,stmtUpdate=null;
			if(storeAuditQuestionsDO != null)
			{
				try
				{
					objSqliteDB = DatabaseHelper.openDataBase();
					String insertQuery 	= "INSERT INTO " + TABLE_NAME + "( question,answer,customerid," +
							"date,status)" + " VALUES (?,?,?,?,?)";
					
					String updateQuery 	= "UPDATE "+TABLE_NAME+" set answer=?, customerid=?,date=?, status=? WHERE question=?";
					
					stmtRecCount 		= objSqliteDB.compileStatement("select count(*) from "+TABLE_NAME+" WHERE question=?");
					stmtRecCount 		= objSqliteDB.compileStatement("select count(*) from "+TABLE_NAME+" WHERE question=?");
					
					stmtInsert 			= objSqliteDB.compileStatement(insertQuery);
					stmtUpdate 			= objSqliteDB.compileStatement(updateQuery);
					
					stmtRecCount.bindString(1, storeAuditQuestionsDO.question+"");
					
					long count = stmtRecCount.simpleQueryForLong();

					if(count > 0)
					{
						stmtUpdate.bindString(1, storeAuditQuestionsDO.Answer);
						stmtUpdate.bindString(2, storeAuditQuestionsDO.customerid);
						stmtUpdate.bindString(3, storeAuditQuestionsDO.date);
						stmtUpdate.bindString(4, storeAuditQuestionsDO.status);
						stmtUpdate.bindString(5, storeAuditQuestionsDO.question);
						LogUtils.errorLog("update Database med tblStoreAuditAnswers ", "updated");
						stmtUpdate.executeInsert();
					}
					else
					{
						stmtInsert.bindString(1, storeAuditQuestionsDO.question);
						stmtInsert.bindString(2, storeAuditQuestionsDO.Answer);
						stmtInsert.bindString(3, storeAuditQuestionsDO.customerid);
						stmtInsert.bindString(4, storeAuditQuestionsDO.date);
						stmtInsert.bindString(5, storeAuditQuestionsDO.status);
						LogUtils.errorLog("Insert Database med tblStoreAuditAnswers ", "inserted");
						stmtInsert.executeInsert();
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
					if(objSqliteDB != null && objSqliteDB.isOpen())
						objSqliteDB.close();
				}
			}
		}
	}
	
	public void insertStoreAuditAnswers(Vector<StoreAuditQuestionsDO> vecAnswersDOs)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteDatabase objSqliteDB = null;
			SQLiteStatement stmtRecCount = null,stmtInsert = null,stmtUpdate=null;
			
			if(vecAnswersDOs != null && vecAnswersDOs.size() >0)
			{
				try
				{
					objSqliteDB = DatabaseHelper.openDataBase();
					String insertQuery 	= "INSERT INTO " + TABLE_NAME + "( question,answer,customerid," +
							"date,status)" + " VALUES (?,?,?,?,?)";
					
					String updateQuery 	= "UPDATE "+TABLE_NAME+" set answer=?, customerid=?,date=?, status=? WHERE question=?";
					
					stmtRecCount 		= objSqliteDB.compileStatement("select count(*) from "+TABLE_NAME+" WHERE question=?");
					
					stmtInsert 			= objSqliteDB.compileStatement(insertQuery);
					stmtUpdate 			= objSqliteDB.compileStatement(updateQuery);
					
					for (StoreAuditQuestionsDO auditAnswersDO : vecAnswersDOs) 
					{
						if(!auditAnswersDO.Answer.equalsIgnoreCase(""))
							auditAnswersDO.status="True";
						else
							auditAnswersDO.status="False";
						auditAnswersDO.date = CalendarUtils.getTodaydate();
						
						stmtRecCount.bindString(1, auditAnswersDO.question+"");
						long count = stmtRecCount.simpleQueryForLong();
						
						if(count > 0)
						{
							stmtUpdate.bindString(1, ""+auditAnswersDO.Answer);
							stmtUpdate.bindString(2, ""+auditAnswersDO.customerid);
							stmtUpdate.bindString(3, ""+auditAnswersDO.date);
							stmtUpdate.bindString(4, ""+auditAnswersDO.status);
							stmtUpdate.bindString(5, ""+auditAnswersDO.question);
							LogUtils.errorLog("update Database med tblStoreAuditAnswers ", "updated");
							stmtUpdate.executeInsert();
						}
						else
						{
							stmtInsert.bindString(1, ""+auditAnswersDO.question);
							stmtInsert.bindString(2, ""+auditAnswersDO.Answer);
							stmtInsert.bindString(3, ""+auditAnswersDO.customerid);
							stmtInsert.bindString(4, ""+auditAnswersDO.date);
							stmtInsert.bindString(5, ""+auditAnswersDO.status);
							LogUtils.errorLog("Insert Database med tblStoreAuditAnswers ", "inserted");
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
					if(objSqliteDB != null && objSqliteDB.isOpen())
						objSqliteDB.close();
				}
			}
		}
	}
	
	
	public Vector<StoreAuditQuestionsDO> getAllStoreAuditAnswers()
	{
		Vector<StoreAuditQuestionsDO> vecStoreAuditAnswersDOs = new Vector<StoreAuditQuestionsDO>();
		
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			try 
			{ 
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT * FROM " + TABLE_NAME +" order by rowid DESC ";
				cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do 
					{
						StoreAuditQuestionsDO questionsDO = new StoreAuditQuestionsDO();
						questionsDO.question = cursor.getString(cursor.getColumnIndex("question"));
						questionsDO.Answer = cursor.getString(cursor.getColumnIndex("answer"));
						questionsDO.customerid = cursor.getString(cursor.getColumnIndex("customerid"));
						questionsDO.date = cursor.getString(cursor.getColumnIndex("date"));
						questionsDO.status = cursor.getString(cursor.getColumnIndex("status"));
						
						vecStoreAuditAnswersDOs.add(questionsDO);
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
				if(objSqliteDB != null && objSqliteDB.isOpen())
					objSqliteDB.close();
			}
		}
		return vecStoreAuditAnswersDOs;
	}
	
	public StoreAuditQuestionsDO getStoreAuditAnswers(String question)
	{
		StoreAuditQuestionsDO answersDO = new StoreAuditQuestionsDO();
		
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			try 
			{ 
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT * FROM " + TABLE_NAME +" where question = "+"'"+question+"'";
				cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do 
					{
						answersDO.question = cursor.getString(cursor.getColumnIndex("question"));
						answersDO.Answer = cursor.getString(cursor.getColumnIndex("answer"));
						answersDO.customerid = cursor.getString(cursor.getColumnIndex("customerid"));
						answersDO.date = cursor.getString(cursor.getColumnIndex("date"));
						answersDO.status = cursor.getString(cursor.getColumnIndex("status"));
						
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
				if(objSqliteDB != null && objSqliteDB.isOpen())
					objSqliteDB.close();
			}
		}
		return answersDO;
	}
}