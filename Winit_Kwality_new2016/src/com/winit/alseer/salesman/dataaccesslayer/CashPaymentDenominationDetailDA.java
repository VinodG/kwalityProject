package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.CashDenominationDetailDO;
import com.winit.sfa.salesman.MyApplication;


public class CashPaymentDenominationDetailDA 
{
	
	public boolean insertCashPaymentDenominationsDetails(Vector<CashDenominationDetailDO> vecCashDenominationDetailDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("select *from tblCashPaymentDenomination");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblCashPaymentDenomination (PaymentCode,CashDenamationCode,TotalCount,CreatedOn) VALUES(?,?,?)");
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("");
				 
				for (CashDenominationDetailDO cashDenominationDetailDO : vecCashDenominationDetailDOs) 
				{
					for (String serialNo : cashDenominationDetailDO.vecSerialNumbers) 
					{
						stmtSelectRec.bindString(1, cashDenominationDetailDO.PaymentCode);
						stmtSelectRec.bindString(2, serialNo);
						long count = stmtSelectRec.simpleQueryForLong();
						if(count > 0)
						{
							
							stmtUpdate.bindString(1, cashDenominationDetailDO.CashDenamationCode);
							stmtUpdate.bindString(2, cashDenominationDetailDO.CreatedOn);
							stmtUpdate.bindString(3, cashDenominationDetailDO.PaymentCode);
							stmtUpdate.bindString(4, serialNo);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, cashDenominationDetailDO.PaymentCode);
							stmtInsert.bindString(2, serialNo);
							stmtInsert.bindString(3, cashDenominationDetailDO.CashDenamationCode);
							stmtInsert.bindString(4, cashDenominationDetailDO.CreatedOn);
							stmtInsert.executeInsert();
		
						}
					}
					stmtSelectRec.close();
					stmtInsert.close();
					stmtUpdate.close();
				}
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
	
	
	public Vector<CashDenominationDetailDO>  getCashPaymentDenominationsDetails()
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor cursor = null;
			 Vector<CashDenominationDetailDO> vecCashDenominationDetailDOs = new Vector<CashDenominationDetailDO>();
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String selectDenominationQuery = "";
				 
				 cursor =  objSqliteDB.rawQuery(selectDenominationQuery, null);
				 if(cursor.moveToFirst())
				 {
					 do 
					 {
						 CashDenominationDetailDO  cashDenominationDetailDO = new CashDenominationDetailDO();
						 cashDenominationDetailDO.PaymentCode           = cursor.getString(cursor.getColumnIndex("PaymentCode"));
						 cashDenominationDetailDO.CashDenamationCode    = cursor.getString(cursor.getColumnIndex("CashDenamationCode"));
						 cashDenominationDetailDO.TotalCount            = cursor.getInt(cursor.getColumnIndex("TotalCount"));
						 cashDenominationDetailDO.CreatedOn             = cursor.getString(cursor.getColumnIndex("CreatedOn"));
						 //for serialNumbers ?? 
						 vecCashDenominationDetailDOs.add(cashDenominationDetailDO);
						 
					 } while (cursor.moveToNext());
						 
				 }
				
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return vecCashDenominationDetailDOs;
		}
	}
}
