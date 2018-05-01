package com.winit.alseer.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.PaymentCashDenominationDO;
import com.winit.alseer.salesman.dataobject.PaymentDetailDO;
import com.winit.alseer.salesman.dataobject.PaymentHeaderDO;
import com.winit.alseer.salesman.dataobject.PaymentInvoiceDO;
import com.winit.alseer.salesman.dataobject.PendingInvicesDO;
import com.winit.alseer.salesman.dataobject.PostPaymentDetailDO;
import com.winit.alseer.salesman.dataobject.PostPaymentDetailDONew;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.MyApplication;

public class PaymentDetailDA 
{
	public boolean insertPaymentDetails(PaymentHeaderDO paymentHeaderDO,String SalesmanCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB 			   = DatabaseHelper.openDataBase();
				
				//generating the Order ID here
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblPaymentHeader (AppPaymentId,RowStatus,ReceiptId,PreReceiptId," +
																		 "PaymentDate,SiteId,EmpNo,Amount,CurrencyCode,Rate,VisitCode,PaymentStatus," +
																		 "CustomerSignature,Status,AppPayementHeaderId,PaymentType, VehicleCode, CollectedBy,SalesOrgCode,Division) " +
																		 "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtDetailInsert = objSqliteDB.compileStatement("INSERT INTO tblPaymentDetail (RowStatus,ReceiptNo,LineNo,PaymentTypeCode," +
						 "BankCode,ChequeDate,ChequeNo,CCNo,CCExpiry,PaymentStatus,PaymentNote,UserDefinedBankName,Status,Amount,ChequeImagePath,ImageUploadStatus,Branch) " +
						 "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
								
				SQLiteStatement stmtInvoiceInsert = objSqliteDB.compileStatement("INSERT INTO tblPaymentInvoice (RowStatus,ReceiptId,TrxCode,TrxType," +
						 "Amount,CurrencyCode,Rate,PaymentStatus,PaymentType,CashDiscount) " +
						 "VALUES(?,?,?,?,?,?,?,?,?,?)");
				
				
				
				SQLiteStatement stmtInsertCashDenominations 		= objSqliteDB.compileStatement("INSERT INTO tblPaymentCashDenomination (PaymentCode, CashDenominationCode, TotalAmount,CreatedBy) VALUES(?,?,?,?)");
				 
				
				if(paymentHeaderDO != null)
				{
					stmtInsert.bindString(1,  paymentHeaderDO.appPaymentId);
					stmtInsert.bindString(2,  paymentHeaderDO.rowStatus);
					stmtInsert.bindString(3,  paymentHeaderDO.receiptId);
					stmtInsert.bindString(4,  paymentHeaderDO.preReceiptId);
					stmtInsert.bindString(5,  paymentHeaderDO.paymentDate);
					stmtInsert.bindString(6,  paymentHeaderDO.siteId);
					stmtInsert.bindString(7,  paymentHeaderDO.empNo);
					stmtInsert.bindString(8,  paymentHeaderDO.amount);
					stmtInsert.bindString(9,  paymentHeaderDO.currencyCode);
					stmtInsert.bindString(10, paymentHeaderDO.rate);
					stmtInsert.bindString(11, paymentHeaderDO.visitCode);
					stmtInsert.bindString(12, paymentHeaderDO.paymentStatus);
					stmtInsert.bindString(13, paymentHeaderDO.customerSignature);
					stmtInsert.bindString(14, paymentHeaderDO.status);
					stmtInsert.bindString(15, ""+paymentHeaderDO.appPayementHeaderId);
					stmtInsert.bindString(16, ""+paymentHeaderDO.paymentType);
					stmtInsert.bindString(17, ""+paymentHeaderDO.vehicleNo);
					stmtInsert.bindString(18, ""+paymentHeaderDO.salesmanCode);
					stmtInsert.bindString(19, ""+paymentHeaderDO.salesOrgCode);
					stmtInsert.bindString(20, ""+paymentHeaderDO.Division);
					
					for(PaymentDetailDO paymentDetailDO : paymentHeaderDO.vecPaymentDetails)
					{
						stmtDetailInsert.bindString(1,  paymentDetailDO.RowStatus);
						stmtDetailInsert.bindString(2,  paymentDetailDO.ReceiptNo);
						stmtDetailInsert.bindString(3,  paymentDetailDO.LineNo);
						stmtDetailInsert.bindString(4,  paymentDetailDO.PaymentTypeCode);
						stmtDetailInsert.bindString(5,  paymentDetailDO.BankCode);
						stmtDetailInsert.bindString(6,  paymentDetailDO.ChequeDate);
						stmtDetailInsert.bindString(7,  paymentDetailDO.ChequeNo);
						stmtDetailInsert.bindString(8,  paymentDetailDO.CCNo);
						stmtDetailInsert.bindString(9,  paymentDetailDO.CCExpiry);
						stmtDetailInsert.bindString(10, paymentDetailDO.PaymentStatus);
						stmtDetailInsert.bindString(11, paymentDetailDO.PaymentNote);
						stmtDetailInsert.bindString(12, paymentDetailDO.UserDefinedBankName);
						stmtDetailInsert.bindString(13, paymentHeaderDO.status);
						stmtDetailInsert.bindString(14, paymentDetailDO.Amount);
						stmtDetailInsert.bindString(15, paymentDetailDO.ChequeImagePath);
						stmtDetailInsert.bindLong(16, paymentDetailDO.imageUploadStatus);
						stmtDetailInsert.bindString(17, paymentDetailDO.branchName);
						
						stmtDetailInsert.executeInsert();
						if(paymentDetailDO.arrayList!=null){
							for (PaymentCashDenominationDO obj : paymentDetailDO.arrayList) 
							{
								stmtInsertCashDenominations.bindString(1, paymentHeaderDO.receiptId);
								stmtInsertCashDenominations.bindString(2, obj.cashDenominationCode);
								stmtInsertCashDenominations.bindLong(3, obj.totalAmount);
								stmtInsertCashDenominations.bindString(4, obj.createdBy);
								stmtInsertCashDenominations.executeInsert();
								insertCashPaymentDenominationsDetails(objSqliteDB,paymentHeaderDO.receiptId,obj.cashDenominationCode,obj.createdBy,obj.arrDenominationDetail);
							}
						}
					}
					
					for(PaymentInvoiceDO paymentInvoiceDO : paymentHeaderDO.vecPaymentInvoices)
					{
						stmtInvoiceInsert.bindString(1, paymentInvoiceDO.RowStatus);
						stmtInvoiceInsert.bindString(2, paymentInvoiceDO.ReceiptId);
						stmtInvoiceInsert.bindString(3, paymentInvoiceDO.TrxCode);
						stmtInvoiceInsert.bindString(4, paymentInvoiceDO.TrxType);
						stmtInvoiceInsert.bindString(5, paymentInvoiceDO.Amount);
						stmtInvoiceInsert.bindString(6, paymentInvoiceDO.CurrencyCode);
						stmtInvoiceInsert.bindString(7, paymentInvoiceDO.Rate);
						stmtInvoiceInsert.bindString(8, paymentInvoiceDO.PaymentStatus);
						stmtInvoiceInsert.bindString(9, paymentInvoiceDO.PaymentType);
						stmtInvoiceInsert.bindString(10,paymentInvoiceDO.CashDiscount);
						stmtInvoiceInsert.executeInsert();
					}
					stmtInsert.executeInsert();
					
					objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+paymentHeaderDO.receiptId+"'");
				}
				
				stmtInvoiceInsert.close();
				stmtDetailInsert.close();
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
					objSqliteDB.close();
			}
			return true;
		}
	}


	private static boolean insertCashPaymentDenominationsDetails(SQLiteDatabase objSqliteDB,String paymentCode,String cashDenominationCode,String createdby, ArrayList<String> arrSerialNumbers)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			try 
			{
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblPaymentCashDenominationDetail (PaymentCode,CashDenominationCode,SerialNumber,CreatedBy) VALUES(?,?,?,?)");
				for (String serialNo : arrSerialNumbers) 
				{
					stmtInsert.bindString(1, paymentCode);
					stmtInsert.bindString(2, cashDenominationCode);
					stmtInsert.bindString(3, serialNo);
					stmtInsert.bindString(4, createdby);
					stmtInsert.executeInsert();
				}
				stmtInsert.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result =  false;
			}
			return result;
		}
	}
	public int getProductivePayments(boolean isTotalBill)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			int status = 0;
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				
				String strQuery = "";
				if(isTotalBill)
				{
					strQuery = "select Count(SiteId) from tblPaymentHeader";
				}
				else
				{
					strQuery = "select Count(DISTINCT SiteId) from tblPaymentHeader";
				}
				
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
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
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return status;
		}
	}
	
	public String getReceiptNo(String SalesmanCode, Preference preference, int division)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			String strReceiptNo = "";
			try 
			{
				objSqliteDB 	= 	DatabaseHelper.openDataBase();
				String type 	=	"";
				/*if(division <= 0)
					type = AppConstants.Receipt;
				else 
					type = AppConstants.Food_Receipt;*/
				if(division <= 0)
					type = AppConstants.Receipt;
				else if(division == 1)
					type = AppConstants.Food_Receipt;
				else
					type = AppConstants.TPT_Receipt;
				
				String query 	= 	"SELECT id from tblOfflineData where Type ='"+type+"' AND status = 0 AND id NOT IN(SELECT ReceiptId FROM tblPaymentHeader) Order By id Limit 1";
				cursor 			= 	objSqliteDB.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					strReceiptNo = cursor.getString(0);
					preference.saveStringInPreference(Preference.RECIEPT_NO, strReceiptNo);
					preference.commitPreference();
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return strReceiptNo;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return strReceiptNo;
		}
	}
	
	public void updateInVoiceNumberforPresellerFinalize(String Recpno, String TrxCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			boolean result =false;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				SQLiteStatement stmtUpdateOrder = objSqliteDB.compileStatement("Update tblPaymentInvoice set TrxCode = ? where ReceiptId =?");
				
						stmtUpdateOrder.bindString(1, TrxCode);
						stmtUpdateOrder.bindString(2, Recpno);
						
						stmtUpdateOrder.execute();
			
				stmtUpdateOrder.close();
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			
		}
	}
	public boolean updatePaymentStatus(ArrayList<PendingInvicesDO> arrInvoiceNumbers, String status)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			boolean result =false;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				SQLiteStatement stmtUpdateOrder = objSqliteDB.compileStatement("Update tblPendingInvoices set Status = ?, BalanceAmount=? where InvoiceNumber =?");
				for(PendingInvicesDO objInvicesDO : arrInvoiceNumbers)
				{
					if(StringUtils.getFloat(objInvicesDO.lastbalance) == StringUtils.getFloat(objInvicesDO.balance))
					{
						stmtUpdateOrder.bindString(1, status);
						stmtUpdateOrder.bindString(2, "0");
						stmtUpdateOrder.bindString(3, objInvicesDO.invoiceNo);
						stmtUpdateOrder.execute();
					}
					else
					{
						float amount = StringUtils.getFloat(objInvicesDO.lastbalance)-StringUtils.getFloat(objInvicesDO.balance);
						stmtUpdateOrder.bindString(1, "N");
						stmtUpdateOrder.bindString(2, ""+amount);
						stmtUpdateOrder.bindString(3, objInvicesDO.invoiceNo);
						stmtUpdateOrder.execute();
					}
				}
				
				stmtUpdateOrder.close();
				result = true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	
	public boolean updatePaymentAmount(String amount, String srtCustomerSiteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			boolean result =false;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				SQLiteStatement stmtUpdateOrder = objSqliteDB.compileStatement("Update tblCustomerSites set TotalOutstandingBalance = ? where CustomerSiteId =?");
				stmtUpdateOrder.bindString(1, amount);
				stmtUpdateOrder.bindString(2, srtCustomerSiteId);
				
				stmtUpdateOrder.execute();
				stmtUpdateOrder.close();
				
				
				result = true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	
	public boolean updateChequeImageStatus(PostPaymentDetailDONew paymentDetailDO,String serverUrl)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			boolean result =false;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				SQLiteStatement stmtUpdateOrder = objSqliteDB.compileStatement("Update tblPaymentDetail  set ImageUploadStatus=?, ChequeImagePath=? where ReceiptNo=? and LineNo=? and ChequeImagePath=?");
				stmtUpdateOrder.bindLong(1, paymentDetailDO.ImageUploadStatus);
				stmtUpdateOrder.bindString(2, serverUrl);
				stmtUpdateOrder.bindString(3, paymentDetailDO.ReceiptNo);
				stmtUpdateOrder.bindString(4, paymentDetailDO.LineNo);
				stmtUpdateOrder.bindString(5, paymentDetailDO.ChequeImagePath);
				stmtUpdateOrder.execute();
				stmtUpdateOrder.close();
				
				result = true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	
	public void updateDamagedImageUploadDetailStatus(ArrayList<String> arrList){
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase=null;
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				
				String orderNo = "";
				
				for(String key:arrList)
					orderNo = orderNo + key +",";
				
				if(orderNo!=null && orderNo.length()>0)
				{
					orderNo = orderNo.substring(0,orderNo.length()-1);
					
					SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement("Update tblOrderImage  set Status=? Where OrderNo IN('"+orderNo+"')");
					sqLiteStatement.bindLong(1,1);
					sqLiteStatement.execute();
					sqLiteStatement.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
		}
	}
	
	public HashMap<String,ArrayList<PostPaymentDetailDONew>> getPaymentChequeImages() {
		synchronized (MyApplication.MyLock) {
			HashMap<String,ArrayList<PostPaymentDetailDONew>> hmImages = new HashMap<String, ArrayList<PostPaymentDetailDONew>>();
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursorChequeImages=null;
			String query="SELECT distinct TPD.RowStatus,TPD.ReceiptNo,TPD.LineNo,TPD.PaymentTypeCode,TPD.BankCode,TPD.ChequeDate,TPD.ChequeNo,TPD.CCNo,TPD.CCExpiry,TPD.PaymentStatus," +
								"TPD.PaymentNote,TPD.UserDefinedBankName,TPD.Status,TPD.Amount,TPD.ChequeImagePath,TPD.ImageUploadStatus,TPH.SalesOrgCode from tblPaymentDetail TPD INNER JOIN tblPaymentHeader TPH where TPH.Status = '"+TrxHeaderDO.get_TRX_Upload_STATUS()+"' AND TPH.ReceiptId = TPD.ReceiptNo AND TPD.ImageUploadStatus ='"+TrxHeaderDO.get_TRX_Image_Upload_STATUS()+"'";
			
			try {
				
				ArrayList<PostPaymentDetailDONew> arrList = new ArrayList<PostPaymentDetailDONew>();
				
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursorChequeImages = sqLiteDatabase.rawQuery(query, null);
				if(cursorChequeImages.moveToFirst()){
					do
					{
						
						PostPaymentDetailDONew objPaymentDetailDO = new PostPaymentDetailDONew();
						
						objPaymentDetailDO.RowStatus 			= cursorChequeImages.getString(0);
						objPaymentDetailDO.ReceiptNo 			= cursorChequeImages.getString(1);
						objPaymentDetailDO.LineNo 				= cursorChequeImages.getString(2);
						objPaymentDetailDO.PaymentTypeCode 		= cursorChequeImages.getString(3);
						objPaymentDetailDO.BankCode				= cursorChequeImages.getString(4);
						objPaymentDetailDO.ChequeDate 			= cursorChequeImages.getString(5);
						objPaymentDetailDO.ChequeNo 			= cursorChequeImages.getString(6);
						objPaymentDetailDO.CCNo 				= cursorChequeImages.getString(7);
						objPaymentDetailDO.CCExpiry 			= cursorChequeImages.getString(8);
						objPaymentDetailDO.PaymentStatus 		= cursorChequeImages.getString(9);
						objPaymentDetailDO.PaymentNote			= cursorChequeImages.getString(10);
						objPaymentDetailDO.UserDefinedBankName  = cursorChequeImages.getString(11);
						objPaymentDetailDO.Status 				= cursorChequeImages.getString(12);
						objPaymentDetailDO.Amount				= cursorChequeImages.getString(13);
						objPaymentDetailDO.ChequeImagePath		= cursorChequeImages.getString(14);
						objPaymentDetailDO.ImageUploadStatus	= cursorChequeImages.getInt(15);
						objPaymentDetailDO.salesOrgCode			= cursorChequeImages.getString(16);
						if(hmImages.containsKey(objPaymentDetailDO.ReceiptNo))
							arrList = hmImages.get(objPaymentDetailDO.ReceiptNo);
						else
							arrList = new ArrayList<PostPaymentDetailDONew>();
						
						arrList.add(objPaymentDetailDO);
						hmImages.put(objPaymentDetailDO.ReceiptNo, arrList);
					} 
					while (cursorChequeImages.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursorChequeImages!=null && !cursorChequeImages.isClosed())
					cursorChequeImages.close();
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return hmImages;
		}
	}
	
	
	
	public ArrayList<PostPaymentDetailDONew> getAllChequeImages() {
		synchronized (MyApplication.MyLock) {
			ArrayList<PostPaymentDetailDONew> arrChequeImageDOs=new ArrayList<PostPaymentDetailDONew>();
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursorChequeImages=null;
			String query="SELECT RowStatus,ReceiptNo,LineNo,PaymentTypeCode,BankCode,ChequeDate,ChequeNo,CCNo,CCExpiry,PaymentStatus," +
								"PaymentNote,UserDefinedBankName,Status,Amount,ChequeImagePath,ImageUploadStatus from tblPaymentDetail where ImageUploadStatus=0 AND ChequeImagePath!='' AND PaymentNote='"+AppConstants.PAYMENT_NOTE_CHEQUE+"'";
			
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursorChequeImages = sqLiteDatabase.rawQuery(query, null);
				if(cursorChequeImages.moveToFirst()){
					do
					{
						
						PostPaymentDetailDONew objPaymentDetailDO = new PostPaymentDetailDONew();
						
						objPaymentDetailDO.RowStatus 			= cursorChequeImages.getString(0);
						objPaymentDetailDO.ReceiptNo 			= cursorChequeImages.getString(1);
						objPaymentDetailDO.LineNo 				= cursorChequeImages.getString(2);
						objPaymentDetailDO.PaymentTypeCode 		= cursorChequeImages.getString(3);
						objPaymentDetailDO.BankCode				= cursorChequeImages.getString(4);
						objPaymentDetailDO.ChequeDate 			= cursorChequeImages.getString(5);
						objPaymentDetailDO.ChequeNo 			= cursorChequeImages.getString(6);
						objPaymentDetailDO.CCNo 				= cursorChequeImages.getString(7);
						objPaymentDetailDO.CCExpiry 			= cursorChequeImages.getString(8);
						objPaymentDetailDO.PaymentStatus 		= cursorChequeImages.getString(9);
						objPaymentDetailDO.PaymentNote			= cursorChequeImages.getString(10);
						objPaymentDetailDO.UserDefinedBankName  = cursorChequeImages.getString(11);
						objPaymentDetailDO.Status 				= cursorChequeImages.getString(12);
						objPaymentDetailDO.Amount				= cursorChequeImages.getString(13);
						objPaymentDetailDO.ChequeImagePath		= cursorChequeImages.getString(14);
						objPaymentDetailDO.ImageUploadStatus	= cursorChequeImages.getInt(15);
						
						arrChequeImageDOs.add(objPaymentDetailDO);
					} 
					while (cursorChequeImages.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursorChequeImages!=null && !cursorChequeImages.isClosed())
					cursorChequeImages.close();
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return arrChequeImageDOs;
		}
	}
	
	
	public Vector<String> getSalesmanCount()
	{
		synchronized(MyApplication.MyLock) 
		{
			String query="select distinct PresellerId from tblDeliverySites";
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursor =null;
			Vector<String> vecPresellers = new Vector<String>();
			try
			{
				sqLiteDatabase 	=	DatabaseHelper.openDataBase();
				cursor			=	sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do 
					{
						vecPresellers.add(cursor.getString(0));
					} while (cursor.moveToNext());
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();	
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
					
			}
			return vecPresellers;
		}
	}
	
	public boolean insertOfflineData(Vector<NameIDDo> vecPaymentReceipts, String salesmanCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			SQLiteStatement insertStament = null, selectStament = null;
			String selectStmt 	= "SELECT COUNT(*) from tblOfflineData WHERE Id =?";
			String query 		= "INSERT INTO tblOfflineData (Id, SalesmanCode, Type, status) VALUES(?,?,?,?)";
			
			try
			{
				sqLiteDatabase 	= 	DatabaseHelper.openDataBase();
				selectStament 	= 	sqLiteDatabase.compileStatement(selectStmt);
				insertStament	=	sqLiteDatabase.compileStatement(query);
				for(NameIDDo nameIDDo : vecPaymentReceipts)
				{
					selectStament.bindString(1, nameIDDo.strId);
					long count = selectStament.simpleQueryForLong();
					
					if(count == 0)
					{
						insertStament.bindString(1, nameIDDo.strId);
						insertStament.bindString(2, salesmanCode);
						insertStament.bindString(3, nameIDDo.strType);
						insertStament.bindString(4, "0");
						insertStament.executeInsert();
					}
				}
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(insertStament!=null)
					insertStament.close();
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
			return true;
		}
	}
	
	//method to get the reason vector by reason type
	public ArrayList<PostPaymentDetailDO> getDetailByReceiptId(String strReceiptId)
	{
		ArrayList<PostPaymentDetailDO> arrInvicesDOs = new ArrayList<PostPaymentDetailDO>();
		String strQuery = "SELECT INVOICE_NUMBER, INVOICE_AMOUNT, COUPON_NO, INVOICE_TOTAL FROM tblPaymentDetail where  RECEIPT_NUMBER= '"+strReceiptId+"'";
			
		 SQLiteDatabase objSqliteDB =	null;
		 Cursor cursor 				= 	null;
		 try
		 {
			 objSqliteDB 	= DatabaseHelper.openDataBase();
			 cursor 		= objSqliteDB.rawQuery(strQuery, null);
			 if(cursor.moveToFirst())
			 {
				 do
				 {
					 PostPaymentDetailDO objInvicesDO	=  new PostPaymentDetailDO();
					 objInvicesDO.invoiceNumber 		=  cursor.getString(0);
					 objInvicesDO.invoiceAmount			=  cursor.getString(1);
					 objInvicesDO.CouponNo				=  cursor.getString(2);
					 objInvicesDO.totalAmount			=  cursor.getString(3);
					 arrInvicesDOs.add(objInvicesDO);
				 }
				 while(cursor.moveToNext());
			 } 
			 
			 if(cursor != null && !cursor.isClosed())
				 cursor.close();
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 finally
		 {
			 if(cursor != null && !cursor.isClosed())
				 cursor.close();
			 
			 if(objSqliteDB != null)
			 {
			 	objSqliteDB.close();
			 }
		 }
		 return arrInvicesDOs;
	}

	public String getReceiptNo(String orderId)
	{
		 String strQuery = "SELECT PN.ReceiptId FROM tblPaymentHeader PN, tblPaymentInvoice PD WHERE PD.ReceiptID= PN.ReceiptId  AND TrxCode = '"+orderId+"' ORDER BY PaymentDate DESC LIMIT 1";
		 String receiptNo = "";
		 SQLiteDatabase objSqliteDB =	null;
		 Cursor cursor 				= 	null;
		 try
		 {
			 objSqliteDB 	= DatabaseHelper.openDataBase();
			 cursor 		= objSqliteDB.rawQuery(strQuery, null);
			 if(cursor.moveToFirst())
			 {
				 receiptNo = cursor.getString(0);
			 } 
			 
			 if(cursor != null && !cursor.isClosed())
				 cursor.close();
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 finally
		 {
			 if(cursor != null && !cursor.isClosed())
				 cursor.close();
			 
			 if(objSqliteDB != null)
			 	objSqliteDB.close();
		 }
		 return receiptNo;
	}
	
	public boolean isPaymentDone(String orderId)
	{
		synchronized(MyApplication.MyLock)
		{
			boolean isPaymentDone = false;
			 String strQuery = "SELECT * from tblPaymentInvoice where TrxCode = '"+orderId+"'";
			 SQLiteDatabase objSqliteDB =	null;
			 Cursor cursor 				= 	null;
			 try
			 {
				 objSqliteDB 	= DatabaseHelper.openDataBase();
				 cursor 		= objSqliteDB.rawQuery(strQuery, null);
				 if(cursor.moveToFirst())
				 {
					 isPaymentDone = true;
				 } 
				 
				 if(cursor != null && !cursor.isClosed())
					 cursor.close();
				 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
			 finally
			 {
				 if(cursor != null && !cursor.isClosed())
					 cursor.close();
				 
				 if(objSqliteDB != null)
				 	objSqliteDB.close();
			 }
			 return isPaymentDone;
		}
	}
	
	public void updateImageUploadDetailStatus(HashMap<String,ArrayList<PostPaymentDetailDONew>> hmImages){
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase=null;
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				
				String orderNo = "";
				
				for(String key:hmImages.keySet())
					orderNo = orderNo + key +",";
				
				if(orderNo!=null && orderNo.length()>0)
				{
					orderNo = orderNo.substring(0,orderNo.length()-1);
					
					SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement("Update tblPaymentDetail set ImageUploadStatus=? Where ReceiptNo IN('"+orderNo+"')");
					sqLiteStatement.bindLong(1,1);
					sqLiteStatement.execute();
					sqLiteStatement.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
		}
	}
	
	public void updateCustomerLimit(String site, double totalPaid)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqliteDB = null;
			Cursor cursor = null;
			SQLiteStatement stmtInvoiceInsert = null;
			double attribute10 = 0;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				String queryAtttribute10 = "SELECT Attribute10 FROM tblCustomer WHERE Site='"+site+"'";
				cursor = sqliteDB.rawQuery(queryAtttribute10, null);
				if(cursor != null && cursor.moveToFirst())
				{
					attribute10 = cursor.getDouble(0);
					
					stmtInvoiceInsert = sqliteDB.compileStatement("UPDATE tblCustomer SET Attribute10=?,BlockedStatus=? WHERE Site=?");
					
					if(totalPaid > 0)
						stmtInvoiceInsert.bindDouble(1, (attribute10 + totalPaid));
					else
						stmtInvoiceInsert.bindDouble(1, (totalPaid));
					
					stmtInvoiceInsert.bindString(2,  "True");
					stmtInvoiceInsert.bindString(3,  site);
					stmtInvoiceInsert.executeInsert();
					stmtInvoiceInsert.close();
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
		}
	}
	
	public double getCustomerExcess(String site)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqliteDB = null;
			Cursor cursor = null;
			double attribute10 = 0;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				String queryAtttribute10 = "SELECT Attribute10 FROM tblCustomer WHERE Site='"+site+"'";
				cursor = sqliteDB.rawQuery(queryAtttribute10, null);
				if(cursor != null && cursor.moveToFirst())
				{
					attribute10 = cursor.getDouble(0);
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
			return attribute10;
		}
	}
}
