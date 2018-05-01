package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.CashDenominationDO;
import com.winit.alseer.salesman.dataobject.CashDenominationDetailDO;
import com.winit.sfa.salesman.MyApplication;


public class CashPaymentDenominationDA 
{
	
	
	public static boolean insertCashPaymentDenominations(Vector<CashDenominationDO> vecCashDenomination,String paymentCode)
	{
		synchronized(MyApplication.MyLock) 
		{
//			boolean result = true;
//			SQLiteDatabase objSqliteDB = null;
//			try 
//			{
//				objSqliteDB = DatabaseHelper.openDataBase();
//				
//				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblPaymentCashDenomination (PaymentCode, CashDenominationCode, TotalAmount,CreatedBy) VALUES(?,?,?,?)");
//				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("select COUNT(*) from tblPaymentCashDenomination where PaymentCode =?");
//				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("");
//				 
//				for (CashDenominationDO cashDenominationDO : vecCashDenomination) 
//				{
//					CashDenominationDetailDO cashDenominationDetailDO  = cashDenominationDO.detailDO;
//					int totalCount	= cashDenominationDetailDO.TotalCount;
//					
//					if(totalCount>0)
//					{
//						
//						if(objSqliteDB == null || !objSqliteDB.isOpen())
//							objSqliteDB = DatabaseHelper.openDataBase();
//						
//						stmtSelectRec.bindString(1, cashDenominationDetailDO.PaymentCode);
//						long count = stmtSelectRec.simpleQueryForLong();
//						if(count > 0)
//						{
////							stmtUpdate.bindString(1, cashDenominationDetailDO.CashDenamationCode);
////							stmtUpdate.bindString(2, cashDenominationDetailDO.TotalCount);
////							stmtUpdate.bindString(3, cashDenominationDetailDO.CreatedOn);
////							stmtUpdate.bindString(4, cashDenominationDetailDO.PaymentCode);
////							stmtUpdate.execute();
//						}
//						else
//						{
//							cashDenominationDetailDO.CashDenamationCode = cashDenominationDO.CashDenamationCode;
//							stmtInsert.bindString(1, paymentCode);
//							stmtInsert.bindString(2, cashDenominationDetailDO.CashDenamationCode);
//							stmtInsert.bindLong(3, cashDenominationDetailDO.TotalCount);
//							stmtInsert.bindString(4, cashDenominationDetailDO.CreatedOn);
//							stmtInsert.executeInsert();
//							
//							insertCashPaymentDenominationsDetails(objSqliteDB,cashDenominationDetailDO,paymentCode);
//						}
//					}
//				}
//				stmtSelectRec.close();
//				stmtInsert.close();
//				stmtUpdate.close();
//			} 
//			catch (Exception e) 
//			{
//				e.printStackTrace();
//				result =  false;
//			}
//			finally
//			{
//				if(objSqliteDB != null)
//					objSqliteDB.close();
//			}
			return true;
		}
	}
	
	
	public  static boolean insertCashPaymentDenominationsDetails(SQLiteDatabase objSqliteDB,CashDenominationDetailDO cashDenominationDetailDO,String paymentCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
//			SQLiteDatabase objSqliteDB = null;
			try 
			{
//				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("select COUNT(*) from tblPaymentCashDenominationDetail where PaymentCode=? AND SerialNumber=?");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblPaymentCashDenominationDetail (PaymentCode,CashDenominationCode,SerialNumber,CreatedBy) VALUES(?,?,?,?)");
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("");
				 
				for (String serialNo : cashDenominationDetailDO.vecSerialNumbers) 
				{
					stmtSelectRec.bindString(1, cashDenominationDetailDO.PaymentCode);
					stmtSelectRec.bindString(2, serialNo);
					long count = stmtSelectRec.simpleQueryForLong();
					if(count > 0)
					{
						
//						stmtUpdate.bindString(1, cashDenominationDetailDO.CashDenamationCode);
//						stmtUpdate.bindString(2, cashDenominationDetailDO.CreatedOn);
//						stmtUpdate.bindString(3, cashDenominationDetailDO.PaymentCode);
//						stmtUpdate.bindString(4, serialNo);
//						stmtUpdate.execute();
					}
					else
					{
						if(!serialNo.equalsIgnoreCase(""))
						{
							stmtInsert.bindString(1, paymentCode);
							stmtInsert.bindString(2, cashDenominationDetailDO.CashDenamationCode);
							stmtInsert.bindString(3, serialNo);
							stmtInsert.bindString(4, cashDenominationDetailDO.CreatedOn);
							stmtInsert.executeInsert();
						}
					}
				}
				stmtSelectRec.close();
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
//				if(objSqliteDB != null)
//					objSqliteDB.close();
			}
			return result;
		}
	}
	
	
	
	public Vector<CashDenominationDetailDO>  getCashPaymentDenominations()
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
						 cashDenominationDetailDO.TotalCount            = cursor.getInt(cursor.getColumnIndex("TotalAmount"));
						 cashDenominationDetailDO.CreatedOn             = cursor.getString(cursor.getColumnIndex("CreatedOn"));
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
