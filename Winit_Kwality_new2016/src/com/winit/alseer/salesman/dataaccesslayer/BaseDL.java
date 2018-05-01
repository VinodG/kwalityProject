package com.winit.alseer.salesman.dataaccesslayer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;

public abstract class BaseDL 
{
	protected SQLiteDatabase database;
	protected String tableName = "";
	
	protected BaseDL(String tblName)
	{
		this.tableName = tblName;
	}
	
	protected  void openTransaction() throws SQLException
	{
		if(database == null || !database.isOpen())
			database = DatabaseHelper.openDataBase();
		database.beginTransaction();
	}
	
	protected  void openDataBase() throws SQLException
	{
		database = DatabaseHelper.openDataBase();
	}
	
	protected  void closeDatabase()
	{
		if(database != null && database.isOpen())
    	database.close();
	}
	
	protected  void closeTransaction()
	{
		if(database != null && database.isOpen())
		{
			database.endTransaction();
			database.close();
		}
	}
	
	protected SQLiteStatement getSqlStatement(String sqlQuery)
	{
		if(database != null && !database.isOpen())
			database = DatabaseHelper.openDataBase();
		SQLiteStatement statement = database.compileStatement(sqlQuery);
		return statement;
	}
	
	protected void setTransaction()
	{
		database.setTransactionSuccessful();
	}
	
	protected void insert(ContentValues values)
	{
		database.insert(tableName, null, values);
	}
	
	protected void update(ContentValues values, String whereClause , String[] whereArgs)
	{
		database.update(tableName, values, whereClause, whereArgs);
	}
	protected long insertData(ContentValues values)
	{
		return database.insert(tableName, null, values);
	}
	
	protected Cursor getCursor(String rawQuery, String[] selArgs)
	{
		return database.rawQuery(rawQuery, selArgs);
	}
	
	protected void delete(String whereClause, String[] whereArgs)
	{
		database.delete(tableName, whereClause, whereArgs);
	}
	
	protected void excuteQuery(String rawQuery)
	{
		 database.execSQL(rawQuery);
	}
	protected Cursor getQueryCursor(String[] columns,String selection,String[] selectionArgs,
			String groupBy,String having,String orderBy)
	{
		return database.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
	}
	
	public Cursor getOfflineData(String querySnippet)
	{
		Cursor cursor = null;
		try 
		{
			openDataBase();
			cursor = getCursor(querySnippet, null);
			cursor.getCount();
		} 
		catch (Exception e) 
		{
		}
		finally
		{
			closeDatabase();
		}
		
		return cursor;
	}
	
}

