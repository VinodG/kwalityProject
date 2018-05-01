package com.winit.alseer.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.CashDenomDO;
import com.winit.alseer.salesman.dataobject.CashDenominationDO;
import com.winit.alseer.salesman.dataobject.DailySumDO;
import com.winit.alseer.salesman.dataobject.DailySummaryDO;
import com.winit.alseer.salesman.dataobject.Denominations;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.BaseActivity;
import com.winit.sfa.salesman.MyApplication;


public class CashDenominationDA
{
	public static Vector<CashDenominationDO>  getCashDenominations()
	{
		synchronized(MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			Vector<CashDenominationDO> vecCashDenominationDOs = new Vector<CashDenominationDO>();
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String selectDenominationQuery = "SELECT * FROM tblCashDenomination order by Amount desc ";

				cursor =  objSqliteDB.rawQuery(selectDenominationQuery, null);
				if(cursor.moveToFirst())
				{
					do
					{
						CashDenominationDO  cashDenominationDO = new CashDenominationDO();
						cashDenominationDO.CashDenamationId    = cursor.getString(cursor.getColumnIndex("CashDenominationId"));
						cashDenominationDO.CashDenamationCode  = cursor.getString(cursor.getColumnIndex("CashDenominationCode"));
						cashDenominationDO.Name                = cursor.getString(cursor.getColumnIndex("Name"));
						cashDenominationDO.Thumb               = cursor.getString(cursor.getColumnIndex("Thumb"));
						cashDenominationDO.Amount              = cursor.getString(cursor.getColumnIndex("Amount"));
						cashDenominationDO.Picture             = cursor.getString(cursor.getColumnIndex("Picture"));
						cashDenominationDO.CreatedOn           = cursor.getString(cursor.getColumnIndex("CreatedBy"));

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
//			 String list[] = {"1000CD","500CD","200CD","100CD"};
//			 String amount[] = {"1000","500","200","100"};
//			 if(vecCashDenominationDOs == null || vecCashDenominationDOs.size() == 0)
//			 {
//				 
//				 for(int i= 0;i<4;i++)
//				 {
//					 
//					 CashDenominationDO  cashDenominationDO = new CashDenominationDO();
//					 cashDenominationDO.CashDenamationId    = ""+i;
//					 cashDenominationDO.CashDenamationCode  = list[i];
//					 cashDenominationDO.Name                = amount[i];
//					 cashDenominationDO.Amount              = amount[i];
//					 vecCashDenominationDOs.add(cashDenominationDO);
//				 }
//			 }
			return vecCashDenominationDOs;
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
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblCashDenomination (CashDenominationId , CashDenominationCode , Name ,Amount ,Thumb ,Picture ) VALUES(?,?,?,?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblCashDenomination SET CashDenominationCode = ?, Name =?, Amount=?,Thumb=?, Picture=? WHERE CashDenominationId = ?");

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
						stmtUpdate.bindString(6, cashDenominationDO.CashDenamationId);
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

	public void insertDenominations(CashDenomDO objCashDenom)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSQLite = null;
			String insertQuery = "INSERT INTO tblUserDenominationDetails (UserCode, CollectionDate, " +
					"HelperUserCode, ThousandNotes, FiveHNotes, TwoHNotes, HundredNotes, FiftyNotes, TwentyNotes, TenNotes, FiveNotes, " +
					"Coins, OtherCurrency, OtherCurrencyAmount, Vouchers, VoucherAmount, CreditNotes, CreditNoteAmount, " +
					"Status,RouteNo,Division) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			try
			{
				objSQLite = DatabaseHelper.openDataBase();
				SQLiteStatement stmtInsert 	  = objSQLite.compileStatement(insertQuery);

				/******************Ice Cream**************************/
				stmtInsert.bindString(1, objCashDenom.UserCode);
				stmtInsert.bindString(2, objCashDenom.CollectionDate);
				stmtInsert.bindString(3, objCashDenom.HelperUserCode);
				stmtInsert.bindLong(4, objCashDenom.objIceCreamDenom.Units1000);
				stmtInsert.bindLong(5, objCashDenom.objIceCreamDenom.Units500);
				stmtInsert.bindLong(6, objCashDenom.objIceCreamDenom.Units200);
				stmtInsert.bindLong(7, objCashDenom.objIceCreamDenom.Units100);
				stmtInsert.bindLong(8, objCashDenom.objIceCreamDenom.Units50);
				stmtInsert.bindLong(9, objCashDenom.objIceCreamDenom.Units20);
				stmtInsert.bindLong(10, objCashDenom.objIceCreamDenom.Units10);
				stmtInsert.bindLong(11, objCashDenom.objIceCreamDenom.Units5);
				stmtInsert.bindDouble(12, objCashDenom.objIceCreamDenom.UnitsCoin);
				stmtInsert.bindString(13, ""+objCashDenom.objIceCreamDenom.OtherCurrencyNote);
				stmtInsert.bindString(14, ""+objCashDenom.objIceCreamDenom.OtherCurrency);
				stmtInsert.bindLong(15, objCashDenom.objIceCreamDenom.VoucherNo);
				stmtInsert.bindString(16, ""+objCashDenom.objIceCreamDenom.Voucher);
				stmtInsert.bindLong(17, objCashDenom.objIceCreamDenom.CashPaidCrNote);
				stmtInsert.bindString(18, ""+objCashDenom.objIceCreamDenom.CashPaidCredit);
				stmtInsert.bindString(19, "False");
				stmtInsert.bindString(20, ""+objCashDenom.RouteNo);
				stmtInsert.bindString(21, "0");
				stmtInsert.executeInsert();


				/************************Food********************************/
				stmtInsert.bindString(1, objCashDenom.UserCode);
				stmtInsert.bindString(2, objCashDenom.CollectionDate);
				stmtInsert.bindString(3, objCashDenom.HelperUserCode);
				stmtInsert.bindLong(4, objCashDenom.objFoodDenom.Units1000);
				stmtInsert.bindLong(5, objCashDenom.objFoodDenom.Units500);
				stmtInsert.bindLong(6, objCashDenom.objFoodDenom.Units200);
				stmtInsert.bindLong(7, objCashDenom.objFoodDenom.Units100);
				stmtInsert.bindLong(8, objCashDenom.objFoodDenom.Units50);
				stmtInsert.bindLong(9, objCashDenom.objFoodDenom.Units20);
				stmtInsert.bindLong(10, objCashDenom.objFoodDenom.Units10);
				stmtInsert.bindLong(11, objCashDenom.objFoodDenom.Units5);
				stmtInsert.bindDouble(12, objCashDenom.objFoodDenom.UnitsCoin);
				stmtInsert.bindString(13, ""+objCashDenom.objFoodDenom.OtherCurrencyNote);
				stmtInsert.bindString(14, ""+objCashDenom.objFoodDenom.OtherCurrency);
				stmtInsert.bindLong(15, objCashDenom.objFoodDenom.VoucherNo);
				stmtInsert.bindString(16, ""+objCashDenom.objFoodDenom.Voucher);
				stmtInsert.bindLong(17, objCashDenom.objFoodDenom.CashPaidCrNote);
				stmtInsert.bindString(18, ""+objCashDenom.objFoodDenom.CashPaidCredit);
				stmtInsert.bindString(19, "False");
				stmtInsert.bindString(20, ""+objCashDenom.RouteNo);
				stmtInsert.bindString(21, "1");
				stmtInsert.executeInsert();

			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(objSQLite != null && objSQLite.isOpen())
				{
					objSQLite.close();
				}
			}
		}
	}

	public Vector<CashDenomDO> getAllCashDenom()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSQLIte = null;
			Cursor cursor = null;
			Vector<CashDenomDO> vecCashDenom = new Vector<CashDenomDO>();
			CashDenomDO objCashdenom = null;
			try
			{
				objSQLIte = DatabaseHelper.openDataBase();

				String query = "SELECT * FROM tblUserDenominationDetails WHERE Status='False'";

				cursor = objSQLIte.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					Denominations objDenom;
					do
					{
						objCashdenom = new CashDenomDO();
						objCashdenom.UserCode = cursor.getString(1);
						objCashdenom.CollectionDate = cursor.getString(2);
						objCashdenom.HelperUserCode = cursor.getString(3);

						if(StringUtils.getInt(cursor.getString(27)) > 0)
							objDenom = objCashdenom.objFoodDenom;
						else
							objDenom = objCashdenom.objIceCreamDenom;

						objDenom.Units1000 = cursor.getInt(4);
						objDenom.Units500 = cursor.getInt(5);
						objDenom.Units200 = cursor.getInt(6);
						objDenom.Units100 = cursor.getInt(7);
						objDenom.Units50 = cursor.getInt(8);
						objDenom.Units20 = cursor.getInt(9);
						objDenom.Units10 = cursor.getInt(10);
						objDenom.Units5 = cursor.getInt(11);
						objDenom.UnitsCoin = cursor.getInt(12);
						objDenom.OtherCurrencyNote = cursor.getInt(13);
						objDenom.OtherCurrency = cursor.getInt(14);
						objDenom.VoucherNo = cursor.getInt(15);
						objDenom.Voucher = cursor.getDouble(16);
						objDenom.CashPaidCrNote = cursor.getInt(17);
						objDenom.CashPaidCredit = cursor.getDouble(18);

						objCashdenom.RouteNo = cursor.getString(26);

						vecCashDenom.add(objCashdenom);
					}while(cursor.moveToNext());
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(objSQLIte != null && objSQLIte.isOpen())
					objSQLIte.close();
			}
			return vecCashDenom;
		}
	}

	public CashDenomDO getCashDenom(String date)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSQLIte = null;
			Cursor cursor = null;
			CashDenomDO objCashdenom = null;
			try
			{
				objSQLIte = DatabaseHelper.openDataBase();

				String query = "SELECT * FROM tblUserDenominationDetails WHERE CollectionDate LIKE '"+date+"%'";

				cursor = objSQLIte.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					Denominations objDenom;
					objCashdenom 					= new CashDenomDO();
					objCashdenom.UserCode 			= cursor.getString(1);
					objCashdenom.CollectionDate 	= cursor.getString(2);
					objCashdenom.HelperUserCode 	= cursor.getString(3);
					objCashdenom.RouteNo 			= cursor.getString(26);

					do {
						if(StringUtils.getInt(cursor.getString(27)) > 0)
							objDenom 	= objCashdenom.objFoodDenom;
						else
							objDenom 	= objCashdenom.objIceCreamDenom;

						objDenom.Units1000 			= cursor.getInt(4);
						objDenom.Units500 			= cursor.getInt(5);
						objDenom.Units200 			= cursor.getInt(6);
						objDenom.Units100 			= cursor.getInt(7);
						objDenom.Units50 			= cursor.getInt(8);
						objDenom.Units20 			= cursor.getInt(9);
						objDenom.Units10 			= cursor.getInt(10);
						objDenom.Units5 			= cursor.getInt(11);
						objDenom.UnitsCoin 			= cursor.getDouble(12);
						objDenom.OtherCurrencyNote 	= cursor.getInt(13);
						objDenom.OtherCurrency 		= cursor.getDouble(14);
						objDenom.VoucherNo 			= cursor.getInt(15);
						objDenom.Voucher 			= cursor.getDouble(16);
						objDenom.CashPaidCrNote 	= cursor.getInt(17);
						objDenom.CashPaidCredit 	= cursor.getDouble(18);
					} while (cursor.moveToNext());
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(objSQLIte != null && objSQLIte.isOpen())
					objSQLIte.close();
			}
			return objCashdenom;
		}
	}

	public String getRouteNo(String userId)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSQLite = null;
			Cursor cursor = null;
			String routeNo = "";
			try
			{

				objSQLite = DatabaseHelper.openDataBase();
				String query = "SELECT RouteNumber  FROM tblUsers WHERE UserCode='"+userId+"'";
				cursor = objSQLite.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					routeNo = cursor.getString(0);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(objSQLite != null && objSQLite.isOpen())
					objSQLite.close();
			}
			return routeNo;
		}
	}

	public void updateAllCashDenom()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSQLite = null;
			try
			{
				objSQLite = DatabaseHelper.openDataBase();
				String query = "UPDATE tblUserDenominationDetails SET Status='True'";
				SQLiteStatement updateCashDenom = objSQLite.compileStatement(query);
				updateCashDenom.execute();

				updateCashDenom.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(objSQLite != null && objSQLite.isOpen())
					objSQLite.close();
			}
		}
	}
	
	/*public ArrayList<DailySummaryDO> getDailySummary(String date)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqliteDB = null;
			Cursor cursor = null;
			ArrayList<DailySummaryDO> vecDailySummary = new ArrayList<DailySummaryDO>();
			DailySummaryDO objDailySummaryDO = null;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				String query = "";
				
				query = "SELECT * " +
						"FROM (Select Distinct TH.trxCode, C.Site as SiteId, C.SiteName as SiteName," +
						"(TH.TotalAmount - TH.TotalDiscountAmount) as Amount,'Sales' as Type,1 AS PRIORITY " +
						"FROM tblCustomer C  Inner Join tblTrxHeader TH on C.Site =TH.ClientCode " +
						"where TrxType=1 and TRXStatus > 0 and TH.trxDate like '"+date+"%' " +
						"UNION " +
						"Select Distinct TH.trxCode, C.Site as SiteId, C.SiteName as SiteName, " +
						"(TH.TotalAmount - TH.TotalDiscountAmount) as Amount,'FOC' as Type,3 AS PRIORITY " +
						"FROM tblCustomer C Inner Join tblTrxHeader TH on C.Site =TH.ClientCode " +
						"where TrxType=6 and TH.trxDate like '"+date+"%' " +
						"UNION " +
						"Select Distinct TH.trxCode, C.Site as SiteId, C.SiteName as SiteName, " +
						"(TH.TotalAmount - TH.TotalDiscountAmount) as Amount,'Return' As Type ,2 AS PRIORITY " +
						"FROM tblCustomer C Inner Join tblTrxHeader TH on C.Site =TH.ClientCode " +
						"where TrxType=4 and TH.trxDate like '"+date+"%' " +
						"UNION " +
						"Select Distinct PH.ReceiptId,C.Site as SiteId, C.SiteName as SiteName,PD.Amount as Amount," +
						"PD.PaymentNote As Type ," +
						"CASE WHEN PD.PaymentNote = 'CASH' THEN  4 WHEN PD.PaymentNote = 'CHEQUE' THEN 5 END AS PRIORITY " +
						"FROM tblCustomer C Inner Join tblPaymentDetail PD on PD.ReceiptNo=PH.ReceiptId " +
						"Inner Join tblPaymentHeader PH on C.Site =PH.SiteId " +
						"where PH.PaymentDate like '"+date+"%' " +
						"UNION " +
						"SELECT '',C.Site as SiteId, C.SiteName as SiteName,'' AS Amount,'ZERO SALES' As Type ,6 AS PRIORITY " +
						"FROM tblCustomer C INNER JOIN tblSkipReasons SR ON C.Site = SR.CustomerSiteId " +
						"WHERE SR.SkipDate like '"+date+"%' " +
						"UNION " +
						"SELECT  '',JP.ClientCode as SiteId,C.SiteName as SiteName, '' AS Amount,'UNVISITED' As Type,7 AS PRIORITY " +
						"FROM tblDailyJourneyPlan JP INNER JOIN tblCustomer C ON JP.ClientCode = C.Site " +
						"WHERE ClientCode NOT IN(SELECT ClientCode FROM tblCustomerVisit WHERE Date LIKE '"+date+"%') " +
						"AND ClientCode NOT IN(SELECT ClientCode FROM tblUnvisitedCustomers WHERE JourneyDate LIKE '"+date+"%') " +
						"AND JourneyDate LIKE '"+date+"%' ) " +
						"ORDER BY PRIORITY ASC";
				
				cursor = sqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						objDailySummaryDO			=	new DailySummaryDO();
						objDailySummaryDO.DocNo		=   cursor.getString(0);
						objDailySummaryDO.siteID	=   cursor.getString(1);
						objDailySummaryDO.siteName	=   cursor.getString(2);
						objDailySummaryDO.amount	=   cursor.getString(3);
						objDailySummaryDO.type		=   cursor.getString(4);
						objDailySummaryDO.Priority	=   cursor.getString(5);
						vecDailySummary.add(objDailySummaryDO);
					} while (cursor.moveToNext());
				}
						
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqliteDB != null && sqliteDB.isOpen())
					sqliteDB.close();
			}
			return vecDailySummary;
		}
	}*/

	public DailySumDO getDailySummaryWithQuantity(String date, String userId)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqliteDB = null;
			Cursor cursor = null;
			DailySumDO objDailySumDO = new DailySumDO();
			ArrayList<DailySummaryDO> arrDailySummary = new ArrayList<DailySummaryDO>();
			LinkedHashMap<String, ArrayList<DailySummaryDO>> hashDailySummary = new LinkedHashMap<String, ArrayList<DailySummaryDO>>();
			DailySummaryDO objDailySummaryDO = null;
			CashDenomDO objCashdenom = new CashDenomDO();
			double[] total = {0,0,0,0,0,0,0,0,0,0,0};
			double[] Grstotal = {0,0,0,0,0,0,0,0,0,0,0};
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				String query = "";

				query = "SELECT * FROM (" +
						"Select Distinct TH.trxCode, C.Site as SiteId, C.SiteName as SiteName," +
						"(TH.TotalAmount - TH.TotalDiscountAmount+TH.VAT) as Amount,'Sales' as Type,1 AS PRIORITY, D.QTY AS Quantity,TH.Division AS Division,TH.TotalAmount AS TOTALAMOUNT " +
						"FROM tblCustomer C  " +
						"Inner Join tblTrxHeader TH on C.Site =TH.ClientCode " +
						"INNER JOIN (SELECT TrxCode,SUM(QuantityLevel1) AS Qty,TrxCode From tblTrxDetail  Group By TrxCode) AS D ON D.TrxCode=TH.TrxCode " +
						"where TrxType="+TrxHeaderDO.get_TRXTYPE_SALES_ORDER()+" and TRXStatus ="+TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED()+" and TH.UserCode='"+userId+"' and TH.trxDate like '"+date+"%' " +

						"UNION " +
						"Select Distinct TH.trxCode, C.Site as SiteId, C.SiteName as SiteName," +
						"(TH.TotalAmount - TH.TotalDiscountAmount+TH.VAT) as Amount,'PreSales' as Type,1 AS PRIORITY, D.QTY AS Quantity,TH.Division AS Division,TH.TotalAmount AS TOTALAMOUNT " +
						"FROM tblCustomer C  " +
						"Inner Join tblTrxHeader TH on C.Site =TH.ClientCode " +
						"INNER JOIN (SELECT TrxCode,SUM(QuantityLevel1) AS Qty,TrxCode From tblTrxDetail  Group By TrxCode) AS D ON D.TrxCode=TH.TrxCode " +
						"where TrxType="+TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER()+" and TRXStatus ="+TrxHeaderDO.get_TRX_STATUS_PRESALES_ORDER()+" and TH.UserCode='"+userId+"' and TH.trxDate like '"+date+"%' " +

						"UNION " +
						"Select Distinct TH.trxCode, C.Site as SiteId, C.SiteName as SiteName, " +
						"(TH.TotalAmount - TH.TotalDiscountAmount+TH.VAT) as Amount,'FOC' as Type,3 AS PRIORITY, D.QTY AS Quantity,TH.Division AS Division,TH.TotalAmount AS TOTALAMOUNT " +
						"FROM tblCustomer C " +
						"Inner Join tblTrxHeader TH on C.Site =TH.ClientCode " +
						"INNER JOIN (SELECT TrxCode,SUM(QuantityLevel1) AS Qty,TrxCode From tblTrxDetail  Group By TrxCode) AS D ON D.TrxCode=TH.TrxCode " +
						"where TrxType=6 and TH.trxDate like '"+date+"%' " +

						"UNION " +
						"Select Distinct TH.trxCode, C.Site as SiteId, C.SiteName as SiteName, " +
						"(TH.TotalAmount - TH.TotalDiscountAmount+TH.VAT) as Amount," +
						"CASE " +
						"WHEN D.ItemType ='D' THEN 'Return-NS' " +
						"WHEN D.ItemType='G' THEN 'Return-S' END As Type ,2 AS PRIORITY, D.QTY AS Quantity,TH.Division AS Division,TH.TotalAmount AS TOTALAMOUNT " +
						"FROM tblCustomer C " +
						"Inner Join tblTrxHeader TH on C.Site =TH.ClientCode " +
						"INNER JOIN (SELECT TrxCode,SUM(QuantityLevel1) AS Qty,TrxCode,ItemType From tblTrxDetail  Group By TrxCode) AS D ON D.TrxCode=TH.TrxCode " +
						"where TrxType=4 and TH.trxDate like '"+date+"%' "+

						"UNION " +
						"Select Distinct PH.ReceiptId,C.Site as SiteId, C.SiteName as SiteName,PD.Amount as Amount,PD.PaymentNote As Type ," +
						"CASE WHEN PD.PaymentNote = 'CASH' THEN  4 WHEN PD.PaymentNote = 'CHEQUE' THEN 5 END AS PRIORITY, " +
						"'' AS Quantity,PH.Division AS Division,'' AS TOTALAMOUNT " +
						"FROM tblPaymentHeader PH " +
						"Inner Join tblPaymentDetail PD on PH.ReceiptId = PD.ReceiptNo " +
						"Inner Join tblCustomer C on PH.SiteId = C.Site " +
						"where PH.PaymentDate like '"+date+"%' " +
					      /*
					      "UNION " +
					      "SELECT 'ZERO SALES',C.Site as SiteId, C.SiteName as SiteName,'' AS Amount,SR.Reason As Type ," +
					      "6 AS PRIORITY, '' AS Quantity,'' AS Division,'' AS TOTALAMOUNT " +
					      "FROM tblCustomer C " +
					      "INNER JOIN tblSkipReasons SR ON C.Site = SR.CustomerSiteId " +
					      "WHERE SR.SkipDate like '"+date+"%' " +*/

//						"UNION " +
//						"SELECT 'ZERO SALES',C.Site as SiteId, C.SiteName as SiteName,'' AS Amount, IFNULL(SR .Reason,TCV. reasontype)  As Type ," +
//						"6 AS PRIORITY, '' AS Quantity,'' AS Division,'' AS TOTALAMOUNT " +
//						"FROM tblCustomer C " +
//						" INNER JOIN tblcustomervisit TCV    on  TCV.clientcode = C.customerId "+
//						" LEFT JOIN tblSkipReasons SR on SR .CustomerSiteId = TCV.clientcode "+
//						" where TCV.Date like  '"+date+"%' AND  (TCV.IsProductiveCall = '0' OR TCV.IsProductiveCall = '') " +
//						"AND ClientCode  NOT IN (select clientcode  from tblTrxheader where TrxDate like '"+date+"%') "+

//						"UNION " +
//						" Select  distinct 'ZERO SALES',SiteId ,SiteName,Amount, Type , PRIORITY,Quantity, Division ,TOTALAMOUNT   from " +
//						"( SELECT 'ZERO SALES',C.Site as SiteId, C.SiteName as SiteName,'' AS Amount, IFNULL(SR .Reason,TCV. reasontype)  As Type ," +
//						"6 AS PRIORITY, '' AS Quantity,'' AS Division,'' AS TOTALAMOUNT " +
//						"FROM tblCustomer C " +
//						" INNER JOIN tblcustomervisit TCV    on  TCV.clientcode = C.customerId "+
//						" LEFT JOIN tblSkipReasons SR on SR .CustomerSiteId = TCV.clientcode "+
//						" where TCV.Date like  '"+date+"%' AND  (TCV.IsProductiveCall = '0' OR TCV.IsProductiveCall = '') " +
//						")" +
////						"AND ClientCode  NOT IN (select clientcode  from tblTrxheader where TrxDate like '"+date+"%') "+
//// taking tblcustomer, tblcustomervisit, tblskipreason  and only return order for particular customer is also considered as Zero sales
//


						"UNION " +
						" Select  distinct 'ZERO SALES',SiteId ,SiteName,Amount, Type , PRIORITY,Quantity, Division ,TOTALAMOUNT   from " +
						"( SELECT 'ZERO SALES',C.Site as SiteId, C.SiteName as SiteName,'' AS Amount, IFNULL(SR .Reason,TCV. reasontype)  As Type ," +
						"6 AS PRIORITY, '' AS Quantity,'' AS Division,'' AS TOTALAMOUNT " +
						"FROM tblCustomer C " +
						" INNER JOIN tblcustomervisit TCV    on  TCV.clientcode = C.customerId "+
						" LEFT JOIN tblSkipReasons SR on SR .CustomerSiteId = TCV.clientcode "+
						" where TCV.Date like  '"+date+"%' AND  C.Site not in " +
						"(Select distinct (clientcode) from tblTrxHeader  " +
						" where trxtype  in ('"+TrxHeaderDO.TRX_TYPE_SALES_ORDER+"','"+TrxHeaderDO.TRX_TYPE_ADVANCE_ORDER+"','"+TrxHeaderDO.TRX_TYPE_PRE_SALES_ORDER+"' ) " +
						  " and trxstatus  in ('"+TrxHeaderDO.TRX_STATUS_PRESALES_ORDER+"','"+TrxHeaderDO.TRX_STATUS_SALES_ORDER_DELIVERED+"','"+TrxHeaderDO.TRX_STATUS_ADVANCE_ORDER_DELIVERED+"' )  and trxdate like '"+date+"%' )  " +
						")" +
//					salesorder --->	Select distinct (clientcode) from tblTrxHeader  where trxtype  in (1,3,5) and trxstatus  in (200,100,3)  and trxdate like '2017-09-06%'
//						"AND ClientCode  NOT IN (select clientcode  from tblTrxheader where TrxDate like '"+date+"%') "+
// taking tblcustomer, tblcustomervisit, tblskipreason  and only return order for particular customer is also considered as Zero sales

////						   SELECT 'ZERO SALES',C.Site as SiteId, C.SiteName as SiteName,'' AS Amount,ifnull(SR .Reason,TCV. reasontype)  As Type ,
//6 AS PRIORITY, '' AS Quantity,'' AS Division,'' AS TOTALAMOUNT FROM tblCustomer
//C INNER JOIN tblcustomervisit TCV    on  TCV.clientcode = C.customerId
//left join tblSkipReasons SR on SR .CustomerSiteId = TCV.clientcode
// where TCV.Date like  '2017-08-01%'  AND  (TCV.IsProductiveCall = '0' OR TCV.IsProductiveCall = '')
// AND TCV.Date like '2017-08-01%' AND ClientCode  NOT IN(select clientcode  from tblTrxheader where TrxDate like '2017-08-01%')

						"UNION  " +
						"SELECT 'UNVISITED',JP.ClientCode as SiteId,C.SiteName as SiteName, '' AS Amount,UC.Reason As Type," +
						"7 AS PRIORITY, '' AS Quantity,'' AS Division,'' AS TOTALAMOUNT " +
						"FROM tblDailyJourneyPlan JP " +
						"INNER JOIN tblCustomer C ON JP.ClientCode = C.Site " +
						"LEFT OUTER JOIN tblUnvisitedCustomers UC ON C.Site=UC.ClientCode " +
						"WHERE JP.ClientCode " +
						"NOT IN(SELECT ClientCode FROM tblCustomerVisit WHERE Date LIKE '"+date+"%') " +
						"AND  JP.JourneyDate LIKE '"+date+"%'" +

						") ORDER BY PRIORITY ASC";
				
				/*query = "SELECT * FROM (" +
						"Select Distinct TH.trxCode, C.Site as SiteId, C.SiteName as SiteName," +
						"(TH.TotalAmount - TH.TotalDiscountAmount) as Amount,'Sales' as Type,1 AS PRIORITY, D.QTY AS Quantity,TH.Division AS Division " +
						"FROM tblCustomer C  " +
						"Inner Join tblTrxHeader TH on C.Site =TH.ClientCode " +
						"INNER JOIN (SELECT TrxCode,SUM(QuantityLevel1) AS Qty,TrxCode From tblTrxDetail  Group By TrxCode) AS D ON D.TrxCode=TH.TrxCode " +
						"where TrxType="+TrxHeaderDO.get_TRXTYPE_SALES_ORDER()+" and TRXStatus ="+TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED()+" and TH.UserCode='"+userId+"' and TH.trxDate like '"+date+"%' " +
						
						"UNION " +
						"Select Distinct TH.trxCode, C.Site as SiteId, C.SiteName as SiteName," +
						"(TH.TotalAmount - TH.TotalDiscountAmount) as Amount,'PreSales' as Type,1 AS PRIORITY, D.QTY AS Quantity,TH.Division AS Division " +
						"FROM tblCustomer C  " +
						"Inner Join tblTrxHeader TH on C.Site =TH.ClientCode " +
						"INNER JOIN (SELECT TrxCode,SUM(QuantityLevel1) AS Qty,TrxCode From tblTrxDetail  Group By TrxCode) AS D ON D.TrxCode=TH.TrxCode " +
						"where TrxType="+TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER()+" and TRXStatus ="+TrxHeaderDO.get_TRX_STATUS_PRESALES_ORDER()+" and TH.UserCode='"+userId+"' and TH.trxDate like '"+date+"%' " +
						
						"UNION " +
						"Select Distinct TH.trxCode, C.Site as SiteId, C.SiteName as SiteName, " +
						"(TH.TotalAmount - TH.TotalDiscountAmount) as Amount,'FOC' as Type,3 AS PRIORITY, D.QTY AS Quantity,TH.Division AS Division " +
						"FROM tblCustomer C " +
						"Inner Join tblTrxHeader TH on C.Site =TH.ClientCode " +
						"INNER JOIN (SELECT TrxCode,SUM(QuantityLevel1) AS Qty,TrxCode From tblTrxDetail  Group By TrxCode) AS D ON D.TrxCode=TH.TrxCode " +
						"where TrxType=6 and TH.trxDate like '"+date+"%' " +
						
						"UNION " +
						"Select Distinct TH.trxCode, C.Site as SiteId, C.SiteName as SiteName, " +
						"(TH.TotalAmount - TH.TotalDiscountAmount) as Amount," +
						"CASE " +
						"WHEN D.ItemType ='D' THEN 'Return-NS' " +
						"WHEN D.ItemType='G' THEN 'Return-S' END As Type ,2 AS PRIORITY, D.QTY AS Quantity,TH.Division AS Division " +
						"FROM tblCustomer C " +
						"Inner Join tblTrxHeader TH on C.Site =TH.ClientCode " +
						"INNER JOIN (SELECT TrxCode,SUM(QuantityLevel1) AS Qty,TrxCode,ItemType From tblTrxDetail  Group By TrxCode) AS D ON D.TrxCode=TH.TrxCode " +
						"where TrxType=4 and TH.trxDate like '"+date+"%' "+
						
						"UNION " +
						"Select Distinct PH.ReceiptId,C.Site as SiteId, C.SiteName as SiteName,PD.Amount as Amount,PD.PaymentNote As Type ," +
						"CASE WHEN PD.PaymentNote = 'CASH' THEN  4 WHEN PD.PaymentNote = 'CHEQUE' THEN 5 END AS PRIORITY, " +
						"'' AS Quantity,PH.Division AS Division " +
						"FROM tblPaymentHeader PH " +
						"Inner Join tblPaymentDetail PD on PH.ReceiptId = PD.ReceiptNo " +
						"Inner Join tblCustomer C on PH.SiteId = C.Site " +
						"where PH.PaymentDate like '"+date+"%' " +
						
						"UNION " +
						"SELECT 'ZERO SALES',C.Site as SiteId, C.SiteName as SiteName,'' AS Amount,SR.Reason As Type ," +
						"6 AS PRIORITY, '' AS Quantity,'' AS Division " +
						"FROM tblCustomer C " +
						"INNER JOIN tblSkipReasons SR ON C.Site = SR.CustomerSiteId " +
						"WHERE SR.SkipDate like '"+date+"%' " +
						
						"UNION  " +
						"SELECT 'UNVISITED',JP.ClientCode as SiteId,C.SiteName as SiteName, '' AS Amount,UC.Reason As Type," +
						"7 AS PRIORITY, '' AS Quantity,'' AS Division " +
						"FROM tblDailyJourneyPlan JP " +
						"INNER JOIN tblCustomer C ON JP.ClientCode = C.Site " +
						"LEFT OUTER JOIN tblUnvisitedCustomers UC ON C.Site=UC.ClientCode " +
						"WHERE JP.ClientCode " +
						"NOT IN(SELECT ClientCode FROM tblCustomerVisit WHERE Date LIKE '"+date+"%') " +
						"AND  JP.JourneyDate LIKE '"+date+"%'" +
						
						") ORDER BY PRIORITY ASC";*/

				cursor = sqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						objDailySummaryDO			=	new DailySummaryDO();
						objDailySummaryDO.DocNo		=   cursor.getString(0);
						objDailySummaryDO.siteID	=   cursor.getString(1);
						objDailySummaryDO.siteName	=   cursor.getString(2);
						objDailySummaryDO.amount	=   cursor.getString(3);
						objDailySummaryDO.type		=   cursor.getString(4);
						objDailySummaryDO.Priority	=   cursor.getString(5);
						objDailySummaryDO.Quantity	=   cursor.getString(6);
						objDailySummaryDO.Division	=   cursor.getString(7);
						objDailySummaryDO.TotalAmnt	=   cursor.getString(8);

						arrDailySummary = hashDailySummary.get(objDailySummaryDO.Priority);
						if(arrDailySummary != null)
							arrDailySummary.add(objDailySummaryDO);
						else {
							arrDailySummary = new ArrayList<DailySummaryDO>();
							arrDailySummary.add(objDailySummaryDO);
							hashDailySummary.put(objDailySummaryDO.Priority, arrDailySummary);
						}

						if(objDailySummaryDO.Priority.equalsIgnoreCase("1"))//SALES
						{
							total[0] += StringUtils.getFloat(objDailySummaryDO.amount);
							Grstotal[0] += StringUtils.getFloat(objDailySummaryDO.TotalAmnt);
						}
						else if(objDailySummaryDO.Priority.equalsIgnoreCase("2")){//GRV
							total[1] += StringUtils.getFloat(objDailySummaryDO.amount);
							Grstotal[1] += StringUtils.getFloat(objDailySummaryDO.TotalAmnt);
						}
						else if(objDailySummaryDO.Priority.equalsIgnoreCase("3"))//FOC
						{
							total[2] += StringUtils.getFloat(objDailySummaryDO.amount);
							Grstotal[2] += StringUtils.getFloat(objDailySummaryDO.TotalAmnt);
						}
						else if(objDailySummaryDO.Priority.equalsIgnoreCase("4")){//CASH
							total[3] += StringUtils.getFloat(objDailySummaryDO.amount);
							Grstotal[3] += StringUtils.getFloat(objDailySummaryDO.TotalAmnt);
						}
						else if(objDailySummaryDO.Priority.equalsIgnoreCase("5")){//CHEQUE
							total[4] += StringUtils.getFloat(objDailySummaryDO.amount);
							Grstotal[4] += StringUtils.getFloat(objDailySummaryDO.TotalAmnt);
						}
						else if(objDailySummaryDO.Priority.equalsIgnoreCase("6")){//ZERO SALES
							total[5] += 1;
							Grstotal[5] += 1;

						}
						else if(objDailySummaryDO.Priority.equalsIgnoreCase("7")){//UNVISITED
							total[6] += 1;
							Grstotal[6] += 1;
						}

						if(objDailySummaryDO.Priority.equalsIgnoreCase("1") ||
								objDailySummaryDO.Priority.equalsIgnoreCase("3")){//SALES + FOC
							total[7] += StringUtils.getFloat(objDailySummaryDO.Quantity);//Quantity
							Grstotal[7] += StringUtils.getFloat(objDailySummaryDO.TotalAmnt);//Quantity
						}

						if(!TextUtils.isEmpty(objDailySummaryDO.type) && objDailySummaryDO.type.equalsIgnoreCase("Sales") && StringUtils.getInt(objDailySummaryDO.Division) == 0)
						{
							total[8] += StringUtils.getFloat(objDailySummaryDO.amount);//TOTAL ICECREAM
							Grstotal[8] += StringUtils.getFloat(objDailySummaryDO.TotalAmnt);//TOTAL ICECREAM
						}
						else if(!TextUtils.isEmpty(objDailySummaryDO.type) && objDailySummaryDO.type.equalsIgnoreCase("Sales") && StringUtils.getInt(objDailySummaryDO.Division) == 1)
						{
							total[9] += StringUtils.getFloat(objDailySummaryDO.amount);//TOTAL FOOD
							Grstotal[9] += StringUtils.getFloat(objDailySummaryDO.TotalAmnt);//TOTAL FOOD
						}
						else if(!TextUtils.isEmpty(objDailySummaryDO.type) && objDailySummaryDO.type.equalsIgnoreCase("Sales") && StringUtils.getInt(objDailySummaryDO.Division) == 2)
						{
							total[10] += StringUtils.getFloat(objDailySummaryDO.amount);//TOTAL FOOD
							Grstotal[10] += StringUtils.getFloat(objDailySummaryDO.TotalAmnt);//TOTAL FOOD
						}


					} while (cursor.moveToNext());
				}



				if(cursor != null && !cursor.isClosed())
					cursor.close();

				/**
				 * ZERO SALES VISIT
				 */
//				 String queryZerosales = "select COUNT(clientcode) from tblCustomerVisit where IsProductiveCall = '0' AND Date like '"+date+"%' AND ClientCode  NOT IN(select clientcode  from tblTrxheader where TrxDate like '"+date+"%')";
				/*String queryZerosales = "select COUNT(clientcode) from tblCustomerVisit where (IsProductiveCall = '0' OR IsProductiveCall = '') AND Date like '"+date+"%' AND ClientCode  NOT IN(select clientcode  from tblTrxheader where TrxDate like '"+date+"%')"; //vinod
				cursor = sqliteDB.rawQuery(queryZerosales, null);
				if(cursor != null && cursor.moveToFirst())
				{
					total[5] = cursor.getInt(0);
				}*/

				if(cursor != null && !cursor.isClosed())
					cursor.close();

				if(arrDailySummary != null)
				{
//					objDailySumDO.arrDailySummary = arrDailySummary;
					objDailySumDO.hashDailySummary = hashDailySummary;
					objDailySumDO.total = total;
					objDailySumDO.grstotal=Grstotal;
				}

				/**
				 * Route Number
				 */
				String queryRoute = "SELECT RouteNumber  FROM tblUsers WHERE UserCode='"+userId+"'";
				cursor = sqliteDB.rawQuery(queryRoute, null);
				if(cursor != null && cursor.moveToFirst())
				{
					objDailySumDO.vehicleNo = cursor.getString(0);
				}

				if(cursor != null && !cursor.isClosed())
					cursor.close();

				/**
				 * Cash denomination details
				 */
				String queryCashDenom = "SELECT * FROM tblUserDenominationDetails WHERE CollectionDate LIKE '"+CalendarUtils.getFormatedDatefromString(date)+"%'";

				cursor = sqliteDB.rawQuery(queryCashDenom, null);
				if(cursor != null && cursor.moveToFirst())
				{
					Denominations objDenom;
					objCashdenom 					= new CashDenomDO();
					objCashdenom.UserCode 			= cursor.getString(1);
					objCashdenom.CollectionDate 	= cursor.getString(2);
					objCashdenom.HelperUserCode 	= cursor.getString(3);
					do {
						if(StringUtils.getInt(cursor.getString(27)) > 0)
							objDenom = objCashdenom.objFoodDenom;
						else
							objDenom = objCashdenom.objIceCreamDenom;

						objDenom.Units1000 			= cursor.getInt(4);
						objDenom.Units500 			= cursor.getInt(5);
						objDenom.Units200 			= cursor.getInt(6);
						objDenom.Units100 			= cursor.getInt(7);
						objDenom.Units50 			= cursor.getInt(8);
						objDenom.Units20 			= cursor.getInt(9);
						objDenom.Units10 			= cursor.getInt(10);
						objDenom.Units5 			= cursor.getInt(11);
						objDenom.UnitsCoin 			= cursor.getDouble(12);
						objDenom.OtherCurrencyNote 	= cursor.getInt(13);
						objDenom.OtherCurrency 		= cursor.getDouble(14);
						objDenom.VoucherNo 			= cursor.getInt(15);
						objDenom.Voucher 			= cursor.getDouble(16);
						objDenom.CashPaidCrNote 	= cursor.getInt(17);
						objDenom.CashPaidCredit 	= cursor.getDouble(18);
						objCashdenom.Division		= cursor.getString(19);
					} while (cursor.moveToNext());
				}

				objCashdenom.RouteNo = objDailySumDO.vehicleNo;

				objDailySumDO.objCashDenom = objCashdenom;
				objDailySumDO.helperName = objCashdenom.HelperUserCode;

				objDailySumDO.dayOfWeek = CalendarUtils.getCurrentDateForJourneyPlan(date);

				if(cursor != null && !cursor.isClosed())
					cursor.close();

				/**
				 * Total Visit
				 */
				String queryVisit = "SELECT COUNT(DISTINCT ClientCode) FROM tblCustomerVisit WHERE Date LIKE '"+date+"%'";
				cursor = sqliteDB.rawQuery(queryVisit, null);
				if(cursor != null && cursor.moveToFirst())
				{
					objDailySumDO.totalVisit = cursor.getInt(0);
				}

				if(cursor != null && !cursor.isClosed())
					cursor.close();

				/**
				 * Odometer data
				 */
				String queryOdometer = "SELECT StartTime," +
						"CASE " +
						"WHEN StartTime = EndTime THEN '' " +
						"ELSE EndTime END AS EndTime ," +
						"OdometerReadingStart,OdometerReadingEnd " +
						"FROM tblJourney " +
						"WHERE UserCode='"+userId+"' AND Date LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				cursor = sqliteDB.rawQuery(queryOdometer, null);
				if(cursor != null && cursor.moveToFirst())
				{
					objDailySumDO.startTime = cursor.getString(0);
					objDailySumDO.endTime 	= cursor.getString(1);
					objDailySumDO.startKM 	= cursor.getString(2);
					objDailySumDO.endKM 	= cursor.getString(3);
				}

				//for EOT problem it was added

						if(isEOTDone(userId ))


				//-----------
				if(cursor != null && !cursor.isClosed())
					cursor.close();

				/**
				 * JP CALLS
				 */
				sqliteDB = DatabaseHelper.openDataBase();
				String queryJPCalls = "SELECT TotalScheduledCalls " +
						"FROM tblTrxLogHeader " +
						"WHERE Date LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				cursor = sqliteDB.rawQuery(queryJPCalls, null);
				if(cursor != null && cursor.moveToFirst())
				{
					objDailySumDO.totalScheduledCall = cursor.getString(0);
				}

				if(cursor != null && !cursor.isClosed())
					cursor.close();

				String queryActualCallsJP = "SELECT COUNT(DISTINCT JP.ClientCode) " +
						"FROM tblDailyJourneyPlan JP " +
						"INNER JOIN tblCustomer C ON JP.ClientCode = C.Site " +
						"WHERE ClientCode IN(SELECT ClientCode FROM tblCustomerVisit " +
						"WHERE Date LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%') " +
						"AND JourneyDate LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				cursor = sqliteDB.rawQuery(queryActualCallsJP, null);
				if(cursor != null && cursor.moveToFirst())
				{
					objDailySumDO.totalActualCall 	= cursor.getString(0);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqliteDB != null && sqliteDB.isOpen())
					sqliteDB.close();
			}
			return objDailySumDO;
		}
	}
	public boolean isEOTDone(String userID)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteDatabase sqliteDB = null;
			Cursor cursor = null;
			boolean isEOTDone = false;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT COUNT(*) FROM tblTrxLogDetails WHERE TrxType = 'isEOT' AND IsJP = 'True' AND CustomerCode = '"+userID+"' AND Date LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				cursor = sqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					if(cursor.getInt(0) > 0)
						isEOTDone = true;
				}
			}
			catch (Exception ex)
			{
				isEOTDone = true;
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqliteDB != null && sqliteDB.isOpen())
					sqliteDB.close();
			}
			return isEOTDone;
//			return false;
		}
	}
}
