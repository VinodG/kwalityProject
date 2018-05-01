package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.CashDenominationDO;
import com.winit.alseer.salesman.dataobject.SurveryOptionDO;
import com.winit.alseer.salesman.dataobject.TrxLogDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxLogHeaders;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.MyApplication;


public class TransactionsLogsDA extends BaseDL
{

	private static final String TXN_LOG_DETAILS = "tblTrxLogDetails";
	public TransactionsLogsDA()
	{
		super(TXN_LOG_DETAILS);
	}
	
	
	
	public  int  getTransactionsLogCount(String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor cursor = null;
			 int count = 0;
			 try
			 {
				 String query = "";
				 
				 objSqliteDB = DatabaseHelper.openDataBase();
				query= "SELECT count(*) from tblTrxLogHeader where Date='"+date+"'  " ;
				 
				 cursor =  objSqliteDB.rawQuery(query, null);
				 if(cursor.moveToFirst())
				 {
					 count = StringUtils.getInt(cursor.getString(0));
						 
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
			 return count;
		}
	}
	public  int  getJourneyPlanCountCount(String UserId)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor cursor = null;
			 int count = 0;
			 try
			 {
				 String query = "";
				 
				objSqliteDB = DatabaseHelper.openDataBase();
				query= "SELECT count(*) from tblDailyJourneyPlan where UserCode='"+UserId+"' ";
				 
				 cursor =  objSqliteDB.rawQuery(query, null);
				 if(cursor.moveToFirst())
				 {
					 count = StringUtils.getInt(cursor.getString(0));
						 
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
			 return count;
		}
	}
	
	
	
	
	public void updateLogReport(TrxLogDetailsDO trxLogDetailsDO)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteDatabase mDatabase = null;
			SQLiteStatement statementSelectFromVisit=null;
			SQLiteStatement statementSelectFromtblTrxLogDetails=null;
			String queryCustomerVisit="select count(ClientCode) from tblCustomerVisit where ClientCode=? and Date like ?"; 
			String queryFromtblTrxLogDetails="select count(CustomerCode) from tblTrxLogDetails where CustomerCode=? and TrxType like ? and Date like ?"; 
			try
			{
				checkAndInsertForTodayLogReport();//checking if any record fr
				
				mDatabase = DatabaseHelper.openDataBase();
				String currentDate = CalendarUtils.getOrderPostDate();
				String query = "";
				
						
				if(trxLogDetailsDO.columnName.equalsIgnoreCase(TrxLogHeaders.COL_TOTAL_ACTUAL_CALLS))
				{
					statementSelectFromVisit=mDatabase.compileStatement(queryCustomerVisit);
					statementSelectFromVisit.bindString(1, trxLogDetailsDO.CustomerCode);
					statementSelectFromVisit.bindString(2, CalendarUtils.getOrderPostDate()+"%");
					long count=statementSelectFromVisit.simpleQueryForLong();
					if(count==0)
					{
						if(trxLogDetailsDO.IsJp.equalsIgnoreCase("True"))
							query = "update tblTrxLogHeader  set "+trxLogDetailsDO.columnName+" =("+trxLogDetailsDO.columnName+"+1),CurrentMonthlySales = (CurrentMonthlySales + 1) where  Date='"+currentDate+"'";
						else
							query = "update tblTrxLogHeader  set "+trxLogDetailsDO.columnName+" =("+trxLogDetailsDO.columnName+"+1) where  Date='"+currentDate+"'";
						mDatabase.execSQL(query);
					}
				}
				else
				{
					if(trxLogDetailsDO.columnName.equalsIgnoreCase(TrxLogHeaders.COL_TOTAL_SALES))
					{
						statementSelectFromtblTrxLogDetails=mDatabase.compileStatement(queryFromtblTrxLogDetails);
						statementSelectFromtblTrxLogDetails.bindString(1, trxLogDetailsDO.CustomerCode);
						statementSelectFromtblTrxLogDetails.bindString(2, AppConstants.INVOICES+"%");
						statementSelectFromtblTrxLogDetails.bindString(3, CalendarUtils.getOrderPostDate()+"%");
						long count=statementSelectFromtblTrxLogDetails.simpleQueryForLong();

						if(count==0)
							query = "update tblTrxLogHeader  set "+trxLogDetailsDO.columnName+" =("+trxLogDetailsDO.columnName+"+'"+trxLogDetailsDO.Amount+"') ,TotalProductiveCalls = (TotalProductiveCalls +1) where  Date='"+currentDate+"'";
						else
							query = "update tblTrxLogHeader  set "+trxLogDetailsDO.columnName+" =("+trxLogDetailsDO.columnName+"+'"+trxLogDetailsDO.Amount+"') where  Date='"+currentDate+"'";
						mDatabase.execSQL(query);
					}
					else if(trxLogDetailsDO.columnName.equalsIgnoreCase(TrxLogHeaders.COL_TOTAL_CREDIT_NOTES))
					{
						query = "update tblTrxLogHeader  set "+trxLogDetailsDO.columnName+" =("+trxLogDetailsDO.columnName+"+'"+trxLogDetailsDO.Amount+"')  where  Date='"+currentDate+"'";
						mDatabase.execSQL(query);
					}
					else if(trxLogDetailsDO.columnName.equalsIgnoreCase(TrxLogHeaders.COL_TOTAL_COLLECTIONS))
					{
						query = "update tblTrxLogHeader  set "+trxLogDetailsDO.columnName+" =("+trxLogDetailsDO.columnName+"+'"+trxLogDetailsDO.Amount+"')  where  Date='"+currentDate+"'";
						mDatabase.execSQL(query);
					}
					Vector<TrxLogDetailsDO> vecDetails = new Vector<TrxLogDetailsDO>();
					vecDetails.add(trxLogDetailsDO);
					insertTrxLogDetails(vecDetails,mDatabase);
					
				}
				
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				if(mDatabase!=null && mDatabase.isOpen())
					mDatabase.close();
			}
		}
	}
	
	private void checkAndInsertForTodayLogReport()
	{
		try {
			TrxLogHeaders header = new TrxLogHeaders();
			if(getTransactionsLogCount(CalendarUtils.getOrderPostDate())>0)
			{
				
			}
			else
			{
				header.TotalActualCalls = 0;
				header.TotalProductiveCalls=0;
				header.TotalCollections=0;
				header.TotalCreditNotes=0;
				header.TotalSales=0;
				header.TotalScheduledCalls=new CustomerDetailsDA().getJourneyPlanCount();
				header.CurrentMonthlySales=0;
				header.TrxDate = CalendarUtils.getOrderPostDate();
				Vector<TrxLogHeaders> vecTrxLogHeaders= new Vector<TrxLogHeaders>();
				vecTrxLogHeaders.add(header);
				insertTrxLogHeaders(vecTrxLogHeaders);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getCounts(String query)
	{
		int count = 0;
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			
			try 
			{ 
				cursor = getCursor(query, null);
				if(cursor.moveToFirst())
				{
					count = StringUtils.getInt(cursor.getString(0));
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
		
		return count;
	  }
	
	public static Vector<TrxLogHeaders>  getTrxLogHeaders(String fromDate,String toDate,int mdt)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor cursor = null,innercursor = null;
			 Vector<TrxLogHeaders> vecCashDenominationDOs = new Vector<TrxLogHeaders>();
			 try
			 {
				 String query = "";
				// CurrentMonthlySales  is using as Planned Actual Calls
				 objSqliteDB = DatabaseHelper.openDataBase();
				 if(mdt==0)
				 {
					 query="SELECT TotalScheduledCalls,TotalActualCalls,TotalProductiveCalls,TotalSales,TotalCreditNotes,TotalCollections,CurrentMonthlySales FROM tblTrxLogHeader where Date ='"+fromDate+"'";
				 }
				 else
				 {
					 query="SELECT sum(TotalScheduledCalls),sum(TotalActualCalls),sum(TotalProductiveCalls),sum(TotalSales),sum(TotalCreditNotes),sum(TotalCollections),sum(CurrentMonthlySales) FROM tblTrxLogHeader where Date LIKE  '"+CalendarUtils.getCurrentMonth()+"%'" ;
				 }
				 
				 cursor =  objSqliteDB.rawQuery(query, null);
				 innercursor =  objSqliteDB.rawQuery(query, null);
				 if(cursor.moveToFirst())
				 {
					 do 
					 {
						 TrxLogHeaders  cashDenominationDO 		= new TrxLogHeaders();
						 
						 cashDenominationDO.TotalScheduledCalls     = 	cursor.getInt(0);
						 cashDenominationDO.TotalActualCalls        =  	cursor.getInt(1);
						 cashDenominationDO.TotalProductiveCalls    =   cursor.getInt(2);
						 cashDenominationDO.TotalSales    			= 	StringUtils.getDouble(cursor.getString(3));
						 cashDenominationDO.TotalCreditNotes    	= 	StringUtils.getDouble(cursor.getString(4));
						 cashDenominationDO.TotalCollections    	=	StringUtils.getDouble(cursor.getString(5));
						 cashDenominationDO.TotalActualCallsPlanned  = 	StringUtils.getInt(cursor.getString(6));
						 String planed ="";
						 if(mdt==0)
						 {
							 planed =	"Select SUM(CustomerCode) From ( SELECT Count(Distinct CustomerCode) " +
								 		"CustomerCode, strftime('%d-%m-%Y', TimeStamp) as VisitDate FROM tblTrxLogDetails " +
								 		"where IsJP = 'True'AND TrxType = 'Invoices' AND TimeStamp Like '"+fromDate+"%' " +
								 		"Group By strftime('%d-%m-%Y', TimeStamp) )";
						 }
						 else
						 {
							 planed =	"Select SUM(CustomerCode) From ( SELECT Count(Distinct CustomerCode) " +
								 		"CustomerCode, strftime('%d-%m-%Y', TimeStamp) as VisitDate FROM tblTrxLogDetails " +
								 		"where IsJP = 'True'AND TrxType = 'Invoices' AND TimeStamp Like '"+CalendarUtils.getCurrentMonth()+"%' " +
								 		"Group By strftime('%d-%m-%Y', TimeStamp) )";
						 }
						 innercursor =  objSqliteDB.rawQuery(planed, null);
						 if(innercursor.moveToFirst())
							 cashDenominationDO.TotalProductiveCallsPlanned  = 	innercursor.getInt(0);
//						  This is only for not showing negative in case data corruption
//						 if(cashDenominationDO.TotalActualCallsPlanned >cashDenominationDO.TotalActualCalls)
//							 cashDenominationDO.TotalActualCallsPlanned = cashDenominationDO.TotalActualCalls;
//						 if(cashDenominationDO.TotalProductiveCallsPlanned >cashDenominationDO.TotalProductiveCalls)
//							 cashDenominationDO.TotalProductiveCallsPlanned = cashDenominationDO.TotalProductiveCalls;
						 
						 cashDenominationDO.vecTrxLogDetailsDO 		= 	getTraxDetails(objSqliteDB,fromDate,toDate,mdt);
						 vecCashDenominationDOs.add(cashDenominationDO);
						 
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
			 return vecCashDenominationDOs;
		}
	}
	public static TrxLogHeaders  getCurrentMonthDetails(String fromDate,String toDate,int mdt)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor cursor = null,innercursor = null;
			 TrxLogHeaders  cashDenominationDO 		= new TrxLogHeaders();
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String query;
				 
				 // Here it is reversed because the first print data is taken directly from the screen and its other data is taken from the below query.
				 if(mdt==0)
				 {
					 query="SELECT sum(TotalScheduledCalls),sum(TotalActualCalls),sum(TotalProductiveCalls)," +
						 		"sum(TotalSales),sum(TotalCreditNotes),sum(TotalCollections),sum(CurrentMonthlySales) " +
						 		"FROM tblTrxLogHeader where Date LIKE  '"+CalendarUtils.getCurrentMonth()+"%'" ;
					
				 }
				 else
				 {
					 query="SELECT TotalScheduledCalls,TotalActualCalls,TotalProductiveCalls,TotalSales," +
						 		"TotalCreditNotes,TotalCollections,CurrentMonthlySales FROM tblTrxLogHeader where Date ='"+CalendarUtils.getOrderPostDate()+"'";
				 }
				 
				 cursor =  objSqliteDB.rawQuery(query, null);
				 if(cursor.moveToFirst())
				 {
					 cashDenominationDO.TotalScheduledCalls     = 	cursor.getInt(0);
					 cashDenominationDO.TotalActualCalls        =  	cursor.getInt(1);
					 cashDenominationDO.TotalProductiveCalls    =   cursor.getInt(2);
					 cashDenominationDO.TotalSales    			= 	StringUtils.getDouble(cursor.getString(3));
					 cashDenominationDO.TotalCreditNotes    	= 	StringUtils.getDouble(cursor.getString(4));
					 cashDenominationDO.TotalCollections    	=	StringUtils.getDouble(cursor.getString(5));
					 cashDenominationDO.TotalActualCallsPlanned  = 	StringUtils.getInt(cursor.getString(6));
					 String planed ="";
					 if(mdt==0)
					 {
						 planed =	"Select SUM(CustomerCode) From ( SELECT Count(Distinct CustomerCode) " +
							 		"CustomerCode, strftime('%d-%m-%Y', TimeStamp) as VisitDate FROM tblTrxLogDetails " +
							 		"where IsJP = 'True'AND TrxType = '"+AppConstants.INVOICES+"' AND TimeStamp Like '"+CalendarUtils.getCurrentMonth()+"%' " +
							 		"Group By strftime('%d-%m-%Y', TimeStamp) )";
					 }
					 else
					 {
						 planed =	"Select SUM(CustomerCode) From ( SELECT Count(Distinct CustomerCode) " +
						 		"CustomerCode, strftime('%d-%m-%Y', TimeStamp) as VisitDate FROM tblTrxLogDetails " +
						 		"where IsJP = 'True'AND TrxType = '"+AppConstants.INVOICES+"' AND TimeStamp Like '"+CalendarUtils.getOrderPostDate()+"%' " +
						 		"Group By strftime('%d-%m-%Y', TimeStamp) )";
					 }
					 innercursor =  objSqliteDB.rawQuery(planed, null);
					 if(innercursor.moveToFirst())
						 cashDenominationDO.TotalProductiveCallsPlanned  = 	innercursor.getInt(0);
//					  This is only for not showing negative in case data corruption 
//					 if(cashDenominationDO.TotalActualCallsPlanned >cashDenominationDO.TotalActualCalls)
//						 cashDenominationDO.TotalActualCallsPlanned = cashDenominationDO.TotalActualCalls;
//					 if(cashDenominationDO.TotalProductiveCallsPlanned >cashDenominationDO.TotalProductiveCalls)
//						 cashDenominationDO.TotalProductiveCallsPlanned = cashDenominationDO.TotalProductiveCalls;
					 
						 
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
			 return cashDenominationDO;
		}
	}
	public static Vector<TrxLogDetailsDO> getTraxDetails(SQLiteDatabase mDatabase, String fromDate,String toDate,int mdt)
	{
		synchronized (MyApplication.MyLock) {
			
			Cursor cursor = null;
			SurveryOptionDO obj = null;
			Vector<TrxLogDetailsDO> vecsurveyoptiondo = new Vector<TrxLogDetailsDO>();
			
			try 
			{
				String query="";
				mDatabase = DatabaseHelper.openDataBase();
				if(mdt==0)
				{
					query = "SELECT Date,CustomerCode,CustomerName,IsJP,TrxType,DocumentNumber,Amount,TimeStamp FROM tblTrxLogDetails where TimeStamp like '"+fromDate+"%' order by TimeStamp DESC";
				}
				else
				{
					query = "SELECT Date,CustomerCode,CustomerName,IsJP,TrxType,DocumentNumber,Amount,TimeStamp FROM tblTrxLogDetails where TimeStamp LIKE '"+CalendarUtils.getCurrentMonth()+"%' order by TimeStamp DESC";
				}
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						 TrxLogDetailsDO  cashDenominationDO 	= new TrxLogDetailsDO();
						 cashDenominationDO.Date    			= cursor.getString(0);
						 cashDenominationDO.CustomerCode     	= cursor.getString(1);
						 cashDenominationDO.CustomerName        = cursor.getString(2);
						 cashDenominationDO.IsJp    			=  cursor.getString(3);
						 cashDenominationDO.TrxType    			= cursor.getString(4)!=null?cursor.getString(4):"";
						 cashDenominationDO.DocumentNumber    	= cursor.getString(5);
						 cashDenominationDO.Amount    			= StringUtils.getDouble(cursor.getString(6));
						 cashDenominationDO.TimeStamp     		= cursor.getString(7);
						 
						 vecsurveyoptiondo.add(cashDenominationDO);
					}
					while(cursor.moveToNext());
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
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
			}
			
			return vecsurveyoptiondo;
		}
	}
	public boolean insertCashDenominations(Vector<CashDenominationDO> vecCashDenominationDOs)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblCashDenomination WHERE CashDenominationId = ?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblCashDenomination (CashDenominationId , CashDenominationCode , Name ,Amount ,Thumb ,Picture,CreatedBy,ModifiedBy,ModifiedDate,ModifiedTime) VALUES(?,?,?,?,?,?,?,?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblCashDenomination SET CashDenominationCode = ?, Name =?, Amount=?,Thumb=?, Picture=?,CreatedBy=?,ModifiedBy=?,ModifiedDate=?,ModifiedTime=? WHERE CashDenominationId = ?");
			
			for(CashDenominationDO cashDenominationDO : vecCashDenominationDOs)
			{
				stmtSelectRec.bindString(1, cashDenominationDO.CashDenamationId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				
				if(cashDenominationDO != null)
				{
					if (countRec != 0) 
					{
						stmtUpdate.bindString(1, cashDenominationDO.CashDenamationCode);
						stmtUpdate.bindString(2, cashDenominationDO.Name);
						stmtUpdate.bindString(3, cashDenominationDO.Amount);
						stmtUpdate.bindString(4, cashDenominationDO.Thumb);
						stmtUpdate.bindString(5, cashDenominationDO.Picture);
						stmtUpdate.bindString(6, cashDenominationDO.CreatedBy);
						stmtUpdate.bindString(7, cashDenominationDO.ModifiedBy);
						stmtUpdate.bindString(8, cashDenominationDO.ModifiedDate);
						stmtUpdate.bindString(9, cashDenominationDO.ModifiedTime);
						stmtUpdate.bindString(10, cashDenominationDO.CashDenamationId);
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1, cashDenominationDO.CashDenamationId);
						stmtInsert.bindString(2, cashDenominationDO.CashDenamationCode);
						stmtInsert.bindString(3, cashDenominationDO.Name);
						stmtInsert.bindString(4, cashDenominationDO.Amount);
						stmtInsert.bindString(5, cashDenominationDO.Thumb);
						stmtInsert.bindString(6, cashDenominationDO.Picture);
						stmtInsert.bindString(7, cashDenominationDO.CreatedBy);
						stmtInsert.bindString(8, cashDenominationDO.ModifiedBy);
						stmtInsert.bindString(9, cashDenominationDO.ModifiedDate);
						stmtInsert.bindString(10,cashDenominationDO.ModifiedTime);
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
	
	public boolean insertTrxLogHeaders(Vector<TrxLogHeaders> vecCashDenominationDOs)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblTrxLogHeader WHERE Date = ?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblTrxLogHeader (Date, TotalScheduledCalls,TotalActualCalls, TotalProductiveCalls, TotalSales, TotalCreditNotes, TotalCollections, CurrentMonthlySales) VALUES(?,?,?,?,?,?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblTrxLogHeader SET TotalScheduledCalls=?,TotalActualCalls=?, TotalProductiveCalls=?, TotalSales=?, TotalCreditNotes=?, TotalCollections=?, CurrentMonthlySales=? WHERE Date = ?");
			
			for(TrxLogHeaders cashDenominationDO : vecCashDenominationDOs)
			{
				stmtSelectRec.bindString(1, cashDenominationDO.TrxDate);
				long countRec = stmtSelectRec.simpleQueryForLong();
				
				if(cashDenominationDO != null)
				{
					if (countRec != 0) 
					{
						
						stmtUpdate.bindLong(1, cashDenominationDO.TotalScheduledCalls);
						stmtUpdate.bindLong(2, cashDenominationDO.TotalActualCalls);
						stmtUpdate.bindLong(3, cashDenominationDO.TotalProductiveCalls);
						stmtUpdate.bindLong(4, (long) cashDenominationDO.TotalSales);
						stmtUpdate.bindLong(5, (long) cashDenominationDO.TotalCreditNotes);
						stmtUpdate.bindLong(6, (long) cashDenominationDO.TotalCollections);
						stmtUpdate.bindLong(7, (long) cashDenominationDO.CurrentMonthlySales);
						stmtUpdate.bindString(8, cashDenominationDO.TrxDate);
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1, cashDenominationDO.TrxDate);
						stmtInsert.bindLong(2, cashDenominationDO.TotalScheduledCalls);
						stmtInsert.bindLong(3, cashDenominationDO.TotalActualCalls);
						stmtInsert.bindLong(4, cashDenominationDO.TotalProductiveCalls);
						stmtInsert.bindLong(5, (long) cashDenominationDO.TotalSales);
						stmtInsert.bindLong(6, (long) cashDenominationDO.TotalCreditNotes);
						stmtInsert.bindLong(7, (long) cashDenominationDO.TotalCollections);
						stmtInsert.bindLong(8, (long) cashDenominationDO.CurrentMonthlySales);
						
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
	
	public boolean updateScheduledCallTrxLogHeaders(Vector<TrxLogHeaders> vecTrxLogDetail)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblTrxLogHeader WHERE Date = ?");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblTrxLogHeader SET TotalScheduledCalls=? WHERE Date = ?");
			
			for(TrxLogHeaders trxHeaderLogDO : vecTrxLogDetail)
			{
				stmtSelectRec.bindString(1, trxHeaderLogDO.TrxDate);
				long countRec = stmtSelectRec.simpleQueryForLong();
				
				if(trxHeaderLogDO != null)
				{
					if (countRec != 0) 
					{
						stmtUpdate.bindLong(1, trxHeaderLogDO.TotalScheduledCalls);
						stmtUpdate.bindString(2, trxHeaderLogDO.TrxDate);
						stmtUpdate.execute();
					}
				}
			}
			
			stmtSelectRec.close();
			stmtUpdate.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB != null && objSqliteDB.isOpen())
			{
				objSqliteDB.close();
			}
		}
		return true;
	}
	
	public void insertTrxLogDetails(Vector<TrxLogDetailsDO> trxLogDetailsDOs,SQLiteDatabase objSqliteDB)
	{
		try
		{
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblTrxLogDetails (Date, CustomerCode,CustomerName, IsJP, TrxType, DocumentNumber,Amount, TimeStamp) VALUES(?,?,?,?,?,?,?,?)");
			
			for(TrxLogDetailsDO trxLogDetailsDO : trxLogDetailsDOs)
			{
				stmtInsert.bindString(1, ""+trxLogDetailsDO.Date);
				stmtInsert.bindString(2, ""+trxLogDetailsDO.CustomerCode);
				stmtInsert.bindString(3, ""+trxLogDetailsDO.CustomerName);
				stmtInsert.bindString(4, ""+trxLogDetailsDO.IsJp);
				stmtInsert.bindString(5, ""+trxLogDetailsDO.TrxType);
				stmtInsert.bindString(6, ""+trxLogDetailsDO.DocumentNumber);
				stmtInsert.bindString(7, ""+trxLogDetailsDO.Amount);
				stmtInsert.bindString(8, ""+trxLogDetailsDO.TimeStamp);
				
				stmtInsert.executeInsert();
			}
			
			stmtInsert.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(objSqliteDB!=null && objSqliteDB.isOpen())
				objSqliteDB.close();
		}
	}
	public boolean insertTrxLogDetails(Vector<TrxLogDetailsDO> vecCashDenominationDOs)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblTrxLogDetails WHERE Date = ? and DocumentNumber=?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblTrxLogDetails (Date, CustomerCode,CustomerName, IsJP, TrxType, DocumentNumber,Amount, TimeStamp) VALUES(?,?,?,?,?,?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblTrxLogDetails SET CustomerCode=?,CustomerName=?, IsJP=?, TrxType=?, Amount=?, TimeStamp=? WHERE Date = ? and DocumentNumber=?");
			
			for(TrxLogDetailsDO cashDenominationDO : vecCashDenominationDOs)
			{
				stmtSelectRec.bindString(1, cashDenominationDO.Date);
				stmtSelectRec.bindString(2, cashDenominationDO.DocumentNumber);
				long countRec = stmtSelectRec.simpleQueryForLong();
				
				if(cashDenominationDO != null)
				{
					if (countRec != 0) 
					{
						
						
						stmtUpdate.bindString(1, cashDenominationDO.CustomerCode);
						stmtUpdate.bindString(2, cashDenominationDO.CustomerName);
						stmtUpdate.bindString(3, cashDenominationDO.IsJp);
						stmtUpdate.bindString(4, cashDenominationDO.TrxType);
						stmtUpdate.bindLong(5,	(long) cashDenominationDO.Amount);
						stmtUpdate.bindString(6, cashDenominationDO.TimeStamp);
						stmtUpdate.bindString(7, cashDenominationDO.Date);
						stmtUpdate.bindString(8, cashDenominationDO.DocumentNumber);
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1, cashDenominationDO.Date);
						stmtInsert.bindString(2, cashDenominationDO.CustomerCode);
						stmtInsert.bindString(3, cashDenominationDO.CustomerName);
						stmtInsert.bindString(4, cashDenominationDO.IsJp);
						stmtInsert.bindString(5, cashDenominationDO.TrxType);
						stmtInsert.bindString(6, cashDenominationDO.DocumentNumber);
						stmtInsert.bindLong(7, 	(long) cashDenominationDO.Amount);
						stmtInsert.bindString(8, cashDenominationDO.TimeStamp);
						
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
	
	public String[] getTotalSalesCollection(boolean today)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqliteDB = null;
			String[] totalSaleColl = {"0","0"};
			Cursor cursorTotalSales = null,cursorTotalColl = null;
			String date = "";
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				if(today)
					date = CalendarUtils.getCurrentDateAsStringforStoreCheck();
				else
					date = CalendarUtils.getCurrentMonth();
				String totalSalesQuery = "SELECT SUM(TotalAmount-TotalDiscountAmount) AS INVOICE FROM tblTrxHeader WHERE TrxDate LIKE '"+date+"%' AND TrxType = 1 AND TRXStatus = 200";
				String totalCollQuery = "SELECT SUM(Amount) FROM tblPaymentHeader WHERE PaymentDate LIKE '"+date+"%'";
				
				cursorTotalSales = sqliteDB.rawQuery(totalSalesQuery, null);
				cursorTotalColl = sqliteDB.rawQuery(totalCollQuery, null);
				if(cursorTotalSales != null && cursorTotalSales.moveToFirst())
				{
					if(cursorTotalSales.getString(0) == null)
						totalSaleColl[0] = "0";
					else
						totalSaleColl[0] = ""+cursorTotalSales.getString(0);
				}
				if(cursorTotalColl != null && cursorTotalColl.moveToFirst())
				{
					if(cursorTotalColl.getString(0) == null)
						totalSaleColl[1] = "0";
					else
						totalSaleColl[1] = ""+cursorTotalColl.getString(0);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursorTotalSales != null && !cursorTotalSales.isClosed())
					cursorTotalSales.close();
				if(cursorTotalColl != null && !cursorTotalColl.isClosed())
					cursorTotalColl.close();
				if(sqliteDB != null && sqliteDB.isOpen())
					sqliteDB.close();
			}
			return totalSaleColl;
		}
	}
}
