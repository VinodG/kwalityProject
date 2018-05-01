package com.winit.alseer.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.Customer_InvoiceDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.OrderDO;
import com.winit.alseer.salesman.dataobject.PaymentDetailDO;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.MyApplication;

public class PaymentSummeryDA 
{
	public ArrayList<Customer_InvoiceDO> getCustomerInvoice(boolean isPreseller,String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<Customer_InvoiceDO> vecCustomerInvoices = null;
			Cursor cursor  = null;
			Cursor innerCursor = null;
			String query   = "";
			query = "SELECT DISTINCT PH.SiteId, PD.PaymentTypeCode, CS.SiteName, PH.ReceiptId, PH.AMOUNT,PD.CCNo,PD.ChequeNo, PH.AppPaymentId,PD.Amount,PH.PaymentDate,CS.CurrencyCode,PD.ChequeDate, PD.UserDefinedBankName " +
					"FROM tblPaymentHeader PH,tblPaymentDetail PD,tblCustomer CS where PH.ReceiptId=PD.ReceiptNo and PH.SiteId=CS.Site AND PH.PaymentDate LIKE '"+date+"%' ORDER BY PD.PaymentTypeCode";
					
			try
			{
				DatabaseHelper.openDataBase();
				cursor = DatabaseHelper._database.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					vecCustomerInvoices = new ArrayList<Customer_InvoiceDO>();
					do
					{
						Customer_InvoiceDO object 	= new Customer_InvoiceDO();
						object.customerSiteId 		= cursor.getString(0);
						object.reciptType 			= cursor.getString(1);
						object.siteName 			= cursor.getString(2);
						object.receiptNo 			= cursor.getString(3);
						object.invoiceTotal 	    = cursor.getString(4);
						object.creditCardNo 		= cursor.getString(5);
						object.chequeNo 			= cursor.getString(6);
						object.uuid 				= cursor.getString(7);
						object.invoiceTotal 		= cursor.getString(8);
						object.reciptDate 			= cursor.getString(9);
						object.currencyCode			= cursor.getString(10);
						object.chequeDate			= cursor.getString(11);
						object.bankName				= cursor.getString(12);
						
						innerCursor = DatabaseHelper._database.rawQuery("SELECT TrxCode,Amount,ReceiptId FROM tblPaymentInvoice where ReceiptId ='"+object.receiptNo+"'", null);
						if(innerCursor.moveToFirst())
						{
							object.vecPaymentDetailDOs = new Vector<PaymentDetailDO>();
							do 
							{
								PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
								paymentDetailDO.invoiceNumber = innerCursor.getString(0);
								paymentDetailDO.invoiceAmount = innerCursor.getString(1); 
								object.vecPaymentDetailDOs.add(paymentDetailDO);
								
							}while(innerCursor.moveToNext());
						}
						if(innerCursor!=null && !innerCursor.isClosed())
							innerCursor.close();
						
						vecCustomerInvoices.add(object);
						
					}while(cursor.moveToNext());
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				
				return vecCustomerInvoices;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(innerCursor!=null && !innerCursor.isClosed())
					innerCursor.close();
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				DatabaseHelper.closedatabase();
			}
			return null;
		}
	}

	public Vector<Customer_InvoiceDO> getPaymentSummaryDetail(String toDate)
	{
		synchronized(MyApplication.MyLock)
		{
			Object object[] = new Object[3];
			HashMap<String,Customer_InvoiceDO> hmCashDetails = new HashMap<String, Customer_InvoiceDO>();
			HashMap<String,Customer_InvoiceDO> hmChequeDetails = new HashMap<String, Customer_InvoiceDO>();
			float totalCollection=0.0f;
			Customer_InvoiceDO customer_InvoiceDO = null;
			Vector<Customer_InvoiceDO> vecpaymentDetail = new Vector<Customer_InvoiceDO>();
			Cursor cursor  = null;
			Cursor innerCursor = null;
			String query   = "";
			query = "SELECT DISTINCT PH.SiteId, PD.PaymentTypeCode, CS.SiteName, PH.ReceiptId, PH.AMOUNT,PD.CCNo,PD.ChequeNo, PH.AppPaymentId,PD.Amount,PH.PaymentDate,CS.CurrencyCode,PD.ChequeDate, PD.UserDefinedBankName,PD.PaymentNote,PH.Division " +
					"FROM tblPaymentHeader PH,tblPaymentDetail PD,tblCustomer CS where PH.ReceiptId=PD.ReceiptNo and PH.SiteId=CS.Site AND PH.PaymentDate Like '"+toDate+"%' ORDER BY PH.ReceiptId DESC";

			try
			{
				DatabaseHelper.openDataBase();
				cursor = DatabaseHelper._database.rawQuery(query, null);

				if(cursor.moveToFirst())
				{

					do
					{
						customer_InvoiceDO = new Customer_InvoiceDO();
						customer_InvoiceDO.customerSiteId 		= cursor.getString(0);
						customer_InvoiceDO.reciptType 			= cursor.getString(1);
						customer_InvoiceDO.siteName 			= cursor.getString(2);
						customer_InvoiceDO.receiptNo 			= cursor.getString(3);
						customer_InvoiceDO.invoiceTotal 	    = cursor.getString(4);
						customer_InvoiceDO.creditCardNo 		= cursor.getString(5);
						customer_InvoiceDO.chequeNo 			= cursor.getString(6);
						customer_InvoiceDO.uuid 				= cursor.getString(7);
						customer_InvoiceDO.amount 				= cursor.getString(8);
						totalCollection = totalCollection+StringUtils.getFloat(customer_InvoiceDO.amount);
						customer_InvoiceDO.reciptDate 			= cursor.getString(9);
						customer_InvoiceDO.currencyCode			= cursor.getString(10);
						customer_InvoiceDO.chequeDate			= cursor.getString(11);
						customer_InvoiceDO.bankName				= cursor.getString(12);
						customer_InvoiceDO.paymentType			= cursor.getString(13);

						customer_InvoiceDO.Division				= StringUtils.getInt(cursor.getString(14));

//						innerCursor = DatabaseHelper._database.rawQuery("SELECT TrxCode,Amount,ReceiptId,InvoiceDate FROM tblPaymentInvoice where ReceiptId ='"+customer_InvoiceDO.receiptNo+"'", null);

						String queryPayDetail = "SELECT PI.TrxCode,PI.Amount,PI.ReceiptId,PEN.InvoiceDate " +
								"FROM tblPaymentInvoice PI LEFT JOIN tblPendingInvoices PEN ON PI.TrxCode = PEN.InvoiceNumber " +
								"WHERE PI.ReceiptId ='"+customer_InvoiceDO.receiptNo+"'";
						innerCursor = DatabaseHelper._database.rawQuery(queryPayDetail, null);
						if(innerCursor.moveToFirst())
						{
							customer_InvoiceDO.vecPaymentDetailDOs = new Vector<PaymentDetailDO>();
							do
							{
								PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
								paymentDetailDO.invoiceNumber = innerCursor.getString(0);
								paymentDetailDO.invoiceAmount = innerCursor.getString(1);
								paymentDetailDO.invoiceDate = innerCursor.getString(3);
								customer_InvoiceDO.vecPaymentDetailDOs.add(paymentDetailDO);

							}while(innerCursor.moveToNext());
						}
						if(innerCursor!=null && !innerCursor.isClosed())
							innerCursor.close();

						vecpaymentDetail.add(customer_InvoiceDO);


					}while(cursor.moveToNext());

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
				if(innerCursor!=null && !innerCursor.isClosed())
					innerCursor.close();
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				DatabaseHelper.closedatabase();
			}
			return vecpaymentDetail;
		}
	}

	public Vector<Customer_InvoiceDO> getPaymentSummaryDetail_Food(String toDate, int divisionFood)
	{
		synchronized(MyApplication.MyLock)
		{
			Object object[] = new Object[3];
			HashMap<String,Customer_InvoiceDO> hmCashDetails = new HashMap<String, Customer_InvoiceDO>();
			HashMap<String,Customer_InvoiceDO> hmChequeDetails = new HashMap<String, Customer_InvoiceDO>();
			float totalCollection=0.0f;
			Customer_InvoiceDO customer_InvoiceDO = null;
			Vector<Customer_InvoiceDO> vecpaymentDetail = new Vector<Customer_InvoiceDO>();
			Cursor cursor  = null;
			Cursor innerCursor = null;
			String query   = "";
			query = "SELECT DISTINCT PH.SiteId, PD.PaymentTypeCode, CS.SiteName, PH.ReceiptId, PH.AMOUNT,PD.CCNo,PD.ChequeNo, PH.AppPaymentId,PD.Amount,PH.PaymentDate,CS.CurrencyCode,PD.ChequeDate, PD.UserDefinedBankName,PD.PaymentNote,PH.Division " +
					"FROM tblPaymentHeader PH,tblPaymentDetail PD,tblCustomer CS where PH.ReceiptId=PD.ReceiptNo and PH.SiteId=CS.Site AND PH.PaymentDate Like '"+toDate+"%' AND PH.Division = '"+divisionFood+"' ORDER BY PH.ReceiptId DESC";

			try
			{
				DatabaseHelper.openDataBase();
				cursor = DatabaseHelper._database.rawQuery(query, null);

				if(cursor.moveToFirst())
				{

					do
					{
						customer_InvoiceDO = new Customer_InvoiceDO();
						customer_InvoiceDO.customerSiteId 		= cursor.getString(0);
						customer_InvoiceDO.reciptType 			= cursor.getString(1);
						customer_InvoiceDO.siteName 			= cursor.getString(2);
						customer_InvoiceDO.receiptNo 			= cursor.getString(3);
						customer_InvoiceDO.invoiceTotal 	    = cursor.getString(4);
						customer_InvoiceDO.creditCardNo 		= cursor.getString(5);
						customer_InvoiceDO.chequeNo 			= cursor.getString(6);
						customer_InvoiceDO.uuid 				= cursor.getString(7);
						customer_InvoiceDO.amount 				= cursor.getString(8);
						totalCollection = totalCollection+StringUtils.getFloat(customer_InvoiceDO.amount);
						customer_InvoiceDO.reciptDate 			= cursor.getString(9);
						customer_InvoiceDO.currencyCode			= cursor.getString(10);
						customer_InvoiceDO.chequeDate			= cursor.getString(11);
						customer_InvoiceDO.bankName				= cursor.getString(12);
						customer_InvoiceDO.paymentType			= cursor.getString(13);

						customer_InvoiceDO.Division				= StringUtils.getInt(cursor.getString(14));

//						innerCursor = DatabaseHelper._database.rawQuery("SELECT TrxCode,Amount,ReceiptId,InvoiceDate FROM tblPaymentInvoice where ReceiptId ='"+customer_InvoiceDO.receiptNo+"'", null);

						String queryPayDetail = "SELECT PI.TrxCode,PI.Amount,PI.ReceiptId,PEN.InvoiceDate " +
								"FROM tblPaymentInvoice PI LEFT JOIN tblPendingInvoices PEN ON PI.TrxCode = PEN.InvoiceNumber " +
								"WHERE PI.ReceiptId ='"+customer_InvoiceDO.receiptNo+"'";
						innerCursor = DatabaseHelper._database.rawQuery(queryPayDetail, null);
						if(innerCursor.moveToFirst())
						{
							customer_InvoiceDO.vecPaymentDetailDOs = new Vector<PaymentDetailDO>();
							do
							{
								PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
								paymentDetailDO.invoiceNumber = innerCursor.getString(0);
								paymentDetailDO.invoiceAmount = innerCursor.getString(1);
								paymentDetailDO.invoiceDate = innerCursor.getString(3);
								customer_InvoiceDO.vecPaymentDetailDOs.add(paymentDetailDO);

							}while(innerCursor.moveToNext());
						}
						if(innerCursor!=null && !innerCursor.isClosed())
							innerCursor.close();

						vecpaymentDetail.add(customer_InvoiceDO);


					}while(cursor.moveToNext());

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
				if(innerCursor!=null && !innerCursor.isClosed())
					innerCursor.close();
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				DatabaseHelper.closedatabase();
			}
			return vecpaymentDetail;
		}
	}
	
	/**
	 * payment Summary for all Customers
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public Object[] getPaymentSummary(String fromDate, String toDate)
	{
		synchronized(MyApplication.MyLock) 
		{
			Object object[] = new Object[3];
			LinkedHashMap<String,Customer_InvoiceDO> hmCashDetails = new LinkedHashMap<String, Customer_InvoiceDO>();
			LinkedHashMap<String,Customer_InvoiceDO> hmChequeDetails = new LinkedHashMap<String, Customer_InvoiceDO>();
			float totalCollection=0.0f;
			Customer_InvoiceDO customer_InvoiceDO = null;
			Cursor cursor  = null;
			Cursor innerCursor = null;
			String query   = "";
			query = "SELECT DISTINCT PH.SiteId, PD.PaymentTypeCode, CS.SiteName, PH.ReceiptId, PH.AMOUNT,PD.CCNo,PD.ChequeNo, PH.AppPaymentId,PD.Amount,PH.PaymentDate,CS.CurrencyCode,PD.ChequeDate, PD.UserDefinedBankName,PD.PaymentNote,PH.Division " +
					"FROM tblPaymentHeader PH,tblPaymentDetail PD,tblCustomer CS where PH.ReceiptId=PD.ReceiptNo and PH.SiteId=CS.Site AND Date(PH.PaymentDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') ORDER BY PH.ReceiptId DESC";
					
			try
			{
				DatabaseHelper.openDataBase();
				cursor = DatabaseHelper._database.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					
					do
					{
						customer_InvoiceDO = new Customer_InvoiceDO();
						customer_InvoiceDO.customerSiteId 		= cursor.getString(0);
						customer_InvoiceDO.reciptType 			= cursor.getString(1);
						customer_InvoiceDO.siteName 			= cursor.getString(2);
						customer_InvoiceDO.receiptNo 			= cursor.getString(3);
						customer_InvoiceDO.invoiceTotal 	    = cursor.getString(4);
						customer_InvoiceDO.creditCardNo 		= cursor.getString(5);
						customer_InvoiceDO.chequeNo 			= cursor.getString(6);
						customer_InvoiceDO.uuid 				= cursor.getString(7);
						customer_InvoiceDO.amount 				= cursor.getString(8);
						totalCollection = totalCollection+StringUtils.getFloat(customer_InvoiceDO.amount);
						customer_InvoiceDO.reciptDate 			= cursor.getString(9);
						customer_InvoiceDO.currencyCode			= cursor.getString(10);
						customer_InvoiceDO.chequeDate			= cursor.getString(11);
						customer_InvoiceDO.bankName				= cursor.getString(12);
						customer_InvoiceDO.paymentType			= cursor.getString(13);

						customer_InvoiceDO.Division				= StringUtils.getInt(cursor.getString(14));
						
//						innerCursor = DatabaseHelper._database.rawQuery("SELECT TrxCode,Amount,ReceiptId,InvoiceDate FROM tblPaymentInvoice where ReceiptId ='"+customer_InvoiceDO.receiptNo+"'", null);
						
						String queryPayDetail = "SELECT PI.TrxCode,PI.Amount,PI.ReceiptId,PEN.InvoiceDate " +
								"FROM tblPaymentInvoice PI LEFT JOIN tblPendingInvoices PEN ON PI.TrxCode = PEN.InvoiceNumber " +
								"WHERE PI.ReceiptId ='"+customer_InvoiceDO.receiptNo+"'";
						innerCursor = DatabaseHelper._database.rawQuery(queryPayDetail, null);
						if(innerCursor.moveToFirst())
						{
							customer_InvoiceDO.vecPaymentDetailDOs = new Vector<PaymentDetailDO>();
							do 
							{
								PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
								paymentDetailDO.invoiceNumber = innerCursor.getString(0);
								paymentDetailDO.invoiceAmount = innerCursor.getString(1);
								paymentDetailDO.invoiceDate = innerCursor.getString(3); 
								customer_InvoiceDO.vecPaymentDetailDOs.add(paymentDetailDO);
								
							}while(innerCursor.moveToNext());
						}
						if(innerCursor!=null && !innerCursor.isClosed())
							innerCursor.close();
						if(customer_InvoiceDO.paymentType.equalsIgnoreCase(AppConstants.PAYMENT_NOTE_CASH))
						{
							hmCashDetails.put(customer_InvoiceDO.receiptNo,customer_InvoiceDO);
						}
						else
						{
							Vector<NameIDDo> vecDetails = new Vector<NameIDDo>();
							
							NameIDDo nameIDDo = new NameIDDo();
							nameIDDo.chequeNumber = customer_InvoiceDO.chequeNo;
							nameIDDo.chequeAmount = customer_InvoiceDO.amount;
							nameIDDo.chequeDate = customer_InvoiceDO.chequeDate;
							nameIDDo.BankName = customer_InvoiceDO.bankName;
							
							if(hmChequeDetails.containsKey(customer_InvoiceDO.receiptNo))
								vecDetails = hmChequeDetails.get(customer_InvoiceDO.receiptNo).vecChequeDetails;
							
							vecDetails.add(nameIDDo);
							customer_InvoiceDO.vecChequeDetails = vecDetails;
							hmChequeDetails.put(customer_InvoiceDO.receiptNo,customer_InvoiceDO);
						}
						
						
					}while(cursor.moveToNext());
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				
				object [0] = hmCashDetails;
				object [1] = hmChequeDetails;
				object [2] = totalCollection;
				
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(innerCursor!=null && !innerCursor.isClosed())
					innerCursor.close();
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				DatabaseHelper.closedatabase();
			}
			return object;
		}
	}
	
	/**
	 * Get Customer payment summary 
	 * @param fromDate
	 * @param toDate
	 * @param site 
	 * @return
	 */
	public Object[] getCustomerPaymentSummary(String fromDate, String toDate, String site)
	{
		synchronized(MyApplication.MyLock) 
		{
			Object object[] = new Object[3];
			LinkedHashMap<String,Customer_InvoiceDO> hmCashDetails = new LinkedHashMap<String, Customer_InvoiceDO>();
			LinkedHashMap<String,Customer_InvoiceDO> hmChequeDetails = new LinkedHashMap<String, Customer_InvoiceDO>();
			float totalCollection=0.0f;
			Customer_InvoiceDO customer_InvoiceDO = null;
			Cursor cursor  = null;
			Cursor innerCursor = null;
			String query   = "";
			query = "SELECT DISTINCT PH.SiteId, PD.PaymentTypeCode, CS.SiteName, PH.ReceiptId, PH.AMOUNT,PD.CCNo,PD.ChequeNo, " +
					"PH.AppPaymentId,PD.Amount,PH.PaymentDate,CS.CurrencyCode,PD.ChequeDate, PD.UserDefinedBankName,PD.PaymentNote,PH.Division " +
					"FROM tblPaymentHeader PH,tblPaymentDetail PD,tblCustomer CS where PH.ReceiptId=PD.ReceiptNo and " +
					"PH.SiteId=CS.Site AND PH.SiteId ='"+site+"' AND Date(PH.PaymentDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') ORDER BY PH.PaymentDate DESC";
					
			try
			{
				DatabaseHelper.openDataBase();
				cursor = DatabaseHelper._database.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					
					do
					{
						customer_InvoiceDO = new Customer_InvoiceDO();
						customer_InvoiceDO.customerSiteId 		= cursor.getString(0);
						customer_InvoiceDO.reciptType 			= cursor.getString(1);
						customer_InvoiceDO.siteName 			= cursor.getString(2);
						customer_InvoiceDO.receiptNo 			= cursor.getString(3);
						customer_InvoiceDO.invoiceTotal 	    = cursor.getString(4);
						customer_InvoiceDO.creditCardNo 		= cursor.getString(5);
						customer_InvoiceDO.chequeNo 			= cursor.getString(6);
						customer_InvoiceDO.uuid 				= cursor.getString(7);
						customer_InvoiceDO.amount 				= cursor.getString(8);
						totalCollection = totalCollection+StringUtils.getFloat(customer_InvoiceDO.amount);
						customer_InvoiceDO.reciptDate 			= cursor.getString(9);
						customer_InvoiceDO.currencyCode			= cursor.getString(10);
						customer_InvoiceDO.chequeDate			= cursor.getString(11);
						customer_InvoiceDO.bankName				= cursor.getString(12);
						customer_InvoiceDO.paymentType			= cursor.getString(13);
						customer_InvoiceDO.Division				= StringUtils.getInt(cursor.getString(14));

						
//						innerCursor = DatabaseHelper._database.rawQuery("SELECT TrxCode,Amount,ReceiptId FROM tblPaymentInvoice where ReceiptId ='"+customer_InvoiceDO.receiptNo+"'", null);
						
						String queryPayDetail = "SELECT PI.TrxCode,PI.Amount,PI.ReceiptId,PEN.InvoiceDate " +
								"FROM tblPaymentInvoice PI LEFT JOIN tblPendingInvoices PEN ON PI.TrxCode = PEN.InvoiceNumber " +
								"WHERE PI.ReceiptId ='"+customer_InvoiceDO.receiptNo+"'";
						innerCursor = DatabaseHelper._database.rawQuery(queryPayDetail, null);
						if(innerCursor.moveToFirst())
						{
							customer_InvoiceDO.vecPaymentDetailDOs = new Vector<PaymentDetailDO>();
							do 
							{
								PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
								paymentDetailDO.invoiceNumber = innerCursor.getString(0);
								paymentDetailDO.invoiceAmount = innerCursor.getString(1);
								paymentDetailDO.invoiceDate = innerCursor.getString(3); 
								customer_InvoiceDO.vecPaymentDetailDOs.add(paymentDetailDO);
								
							}while(innerCursor.moveToNext());
						}
						if(innerCursor!=null && !innerCursor.isClosed())
							innerCursor.close();
						if(customer_InvoiceDO.paymentType.equalsIgnoreCase(AppConstants.PAYMENT_NOTE_CASH))
						{
							hmCashDetails.put(customer_InvoiceDO.receiptNo,customer_InvoiceDO);
						}
						else
						{
							Vector<NameIDDo> vecDetails = new Vector<NameIDDo>();
							
							NameIDDo nameIDDo = new NameIDDo();
							nameIDDo.chequeNumber = customer_InvoiceDO.chequeNo;
							nameIDDo.chequeAmount = customer_InvoiceDO.amount;
							nameIDDo.chequeDate = customer_InvoiceDO.chequeDate;
							nameIDDo.BankName = customer_InvoiceDO.bankName;
							
							if(hmChequeDetails.containsKey(customer_InvoiceDO.receiptNo))
								vecDetails = hmChequeDetails.get(customer_InvoiceDO.receiptNo).vecChequeDetails;
							
							vecDetails.add(nameIDDo);
							customer_InvoiceDO.vecChequeDetails = vecDetails;
							hmChequeDetails.put(customer_InvoiceDO.receiptNo,customer_InvoiceDO);
						}
						
						
					}while(cursor.moveToNext());
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				
				object [0] = hmCashDetails;
				object [1] = hmChequeDetails;
				object [2] = totalCollection;
				
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(innerCursor!=null && !innerCursor.isClosed())
					innerCursor.close();
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				DatabaseHelper.closedatabase();
			}
			return object;
		}
	}
	
	/**
	 * Get Customer payment summary By Recept ID
	 * @param fromDate
	 * @param toDate
	 * @param site 
	 * @return
	 */
	public Customer_InvoiceDO getCustomerPaymentSummaryByReceiptID(String fromDate, String toDate, String site, String receiptId)
	{
		synchronized(MyApplication.MyLock) 
		{
			Object object[] = new Object[3];
			HashMap<String,Customer_InvoiceDO> hmCashDetails = new HashMap<String, Customer_InvoiceDO>();
			HashMap<String,Customer_InvoiceDO> hmChequeDetails = new HashMap<String, Customer_InvoiceDO>();
			float totalCollection=0.0f;
			Customer_InvoiceDO customer_InvoiceDO = null;
			Cursor cursor  = null;
			Cursor innerCursor = null;
			String query   = "";
			query = "SELECT DISTINCT PH.SiteId, PD.PaymentTypeCode, CS.SiteName, PH.ReceiptId, PH.AMOUNT,PD.CCNo,PD.ChequeNo, " +
					"PH.AppPaymentId,PD.Amount,PH.PaymentDate,CS.CurrencyCode,PD.ChequeDate, PD.UserDefinedBankName,PD.PaymentNote " +
					"FROM tblPaymentHeader PH,tblPaymentDetail PD,tblCustomer CS where PH.ReceiptId=PD.ReceiptNo and " +
					"PH.SiteId=CS.Site AND PH.SiteId ='"+site+"' AND  PH.ReceiptId = '"+receiptId+"' AND Date(PH.PaymentDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') ORDER BY PH.PaymentDate DESC";
					
			try
			{
				DatabaseHelper.openDataBase();
				cursor = DatabaseHelper._database.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					
					do
					{
						customer_InvoiceDO = new Customer_InvoiceDO();
						customer_InvoiceDO.customerSiteId 		= cursor.getString(0);
						customer_InvoiceDO.reciptType 			= cursor.getString(1);
						customer_InvoiceDO.siteName 			= cursor.getString(2);
						customer_InvoiceDO.receiptNo 			= cursor.getString(3);
						customer_InvoiceDO.invoiceTotal 	    = cursor.getString(4);
						customer_InvoiceDO.creditCardNo 		= cursor.getString(5);
						customer_InvoiceDO.chequeNo 			= cursor.getString(6);
						customer_InvoiceDO.uuid 				= cursor.getString(7);
						customer_InvoiceDO.amount 				= cursor.getString(8);
						totalCollection = totalCollection+StringUtils.getFloat(customer_InvoiceDO.amount);
						customer_InvoiceDO.reciptDate 			= cursor.getString(9);
						customer_InvoiceDO.currencyCode			= cursor.getString(10);
						customer_InvoiceDO.chequeDate			= cursor.getString(11);
						customer_InvoiceDO.bankName				= cursor.getString(12);
						customer_InvoiceDO.paymentType			= cursor.getString(13);
						
						innerCursor = DatabaseHelper._database.rawQuery("SELECT TrxCode,Amount,ReceiptId FROM tblPaymentInvoice where ReceiptId ='"+customer_InvoiceDO.receiptNo+"'", null);
						if(innerCursor.moveToFirst())
						{
							customer_InvoiceDO.vecPaymentDetailDOs = new Vector<PaymentDetailDO>();
							do 
							{
								PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
								paymentDetailDO.invoiceNumber = innerCursor.getString(0);
								paymentDetailDO.invoiceAmount = innerCursor.getString(1); 
								customer_InvoiceDO.vecPaymentDetailDOs.add(paymentDetailDO);
								
							}while(innerCursor.moveToNext());
						}
						if(innerCursor!=null && !innerCursor.isClosed())
							innerCursor.close();
						if(customer_InvoiceDO.paymentType.equalsIgnoreCase(AppConstants.PAYMENT_NOTE_CASH))
						{
							hmCashDetails.put(customer_InvoiceDO.receiptNo,customer_InvoiceDO);
						}
						else
						{
							Vector<NameIDDo> vecDetails = new Vector<NameIDDo>();
							
							NameIDDo nameIDDo = new NameIDDo();
							nameIDDo.chequeNumber = customer_InvoiceDO.chequeNo;
							nameIDDo.chequeAmount = customer_InvoiceDO.amount;
							
							if(hmChequeDetails.containsKey(customer_InvoiceDO.receiptNo))
								vecDetails = hmChequeDetails.get(customer_InvoiceDO.receiptNo).vecChequeDetails;
							
							vecDetails.add(nameIDDo);
							customer_InvoiceDO.vecChequeDetails = vecDetails;
							hmChequeDetails.put(customer_InvoiceDO.receiptNo,customer_InvoiceDO);
						}
						
						
					}while(cursor.moveToNext());
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				
				object [0] = hmCashDetails;
				object [1] = hmChequeDetails;
				object [2] = totalCollection;
				
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(innerCursor!=null && !innerCursor.isClosed())
					innerCursor.close();
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				DatabaseHelper.closedatabase();
			}
//			return object;
			return customer_InvoiceDO;
		}
	}
	
	/**
	 * Get customer order summary by receipt id
	 * @param isPreseller
	 * @param date
	 * @return
	 */
//	public Customer_InvoiceDO getCustomerOrderSummaryByReceiptId(String empNo,String fromDate,String toDate,String customerSiteID, String receiptID)
//	{
//		synchronized(MyApplication.MyLock) 
//		{
//			SQLiteDatabase slDatabase = null;
//			Cursor cursor = null;
//			HashMap<Integer,Vector<TrxHeaderDO>> hmOrders   =  new HashMap<Integer, Vector<TrxHeaderDO>>();
//			int trxType = 0;
//			Vector<TrxHeaderDO> vecDetails = new Vector<TrxHeaderDO>();
//			try
//			{
//				slDatabase 	= 	DatabaseHelper.openDataBase();
//				String query = "SELECT TX.TrxCode,TX.AppTrxId,TX.OrgCode,TX.JourneyCode,TX.VisitCode,TX.UserCode,TX.ClientCode,TX.ClientBranchCode, " +
//						       "TX.TrxDate,TX.TrxType,TX.CurrencyCode,TX.PaymentType,TX.TotalAmount,TX.TotalDiscountAmount,TX.TotalTAXAmount ,TX.TrxReasonCode, " +
//						       "TX.ReferenceCode ,TX.ClientSignature ,TX.SalesmanSignature ,TX.Status ,TX.FreeNote ,TX.CreatedOn,TX.PreTrxCode ,TX.TRXStatus , " +
//						       "TX.BranchPlantCode ,TX.PrintingTimes ,TX.ApproveByCode ,TX.ApprovedDate ,TX.LPOCode ,TX.DeliveryDate , TX.UserCreditAccountCode,TC.SiteName,TC.Address1,TX.TrxSubType " +
//						       "FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site " +
//						       "AND UserCode ='"+empNo+"' AND  Date(TrxDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') " +
//						       "AND TrxType !='"+AppConstants.CART_TYPE+"' AND TX.ClientCode = '"+customerSiteID+"' AND TX.TrxCode='"+receiptID+"'  ORDER BY TX.TrxDate DESC";
//				
//				cursor		=	slDatabase.rawQuery(query, null);
//			
//				if(cursor.moveToFirst())
//				{
//					do
//					{
//						TrxHeaderDO orderDO 		= new TrxHeaderDO();
//						
//						orderDO.trxCode    			= 	cursor.getString(0);
//						orderDO.appTrxId			= 	cursor.getString(1);
//						orderDO.orgCode				= 	cursor.getString(2);
//						orderDO.journeyCode			= 	cursor.getString(3);
//						orderDO.visitCode			= 	cursor.getString(4);
//						orderDO.userCode			= 	cursor.getString(5);
//						orderDO.clientCode			= 	cursor.getString(6);
//						orderDO.clientBranchCode	= 	cursor.getString(7);
//						orderDO.trxDate				= 	cursor.getString(8);
//						orderDO.trxType				= 	cursor.getInt(9);	
//						orderDO.currencyCode		= 	cursor.getString(10);
//						orderDO.paymentType			= 	cursor.getInt(11);
//						orderDO.totalAmount			= 	cursor.getFloat(12);
//						orderDO.totalDiscountAmount	= 	cursor.getFloat(13);
//						orderDO.totalTAXAmount		= 	cursor.getFloat(14);
//						orderDO.trxReasonCode		= 	cursor.getString(15);
//						
//						orderDO.referenceCode		= 	cursor.getString(16);
//						orderDO.clientSignature		= 	cursor.getString(17);
//						orderDO.salesmanSignature	= 	cursor.getString(18);
//						orderDO.status				= 	cursor.getInt(19);
//						
//						orderDO.freeNote			= 	cursor.getString(20);
//						orderDO.createdOn			= 	cursor.getString(21);
//						orderDO.preTrxCode			= 	cursor.getString(22);
//						orderDO.trxStatus			= 	cursor.getInt(23);
//						
//						orderDO.branchPlantCode		= 	cursor.getString(24);
//						orderDO.printingTimes		= 	cursor.getInt(25);
//						orderDO.approveByCode		= 	cursor.getString(26);
//						orderDO.approvedDate		= 	cursor.getString(27);
//						orderDO.lPOCode				= 	cursor.getString(28);
//						orderDO.deliveryDate		= 	cursor.getString(29);
//						orderDO.userCreditAccountCode= 	cursor.getString(30);
//						
//						orderDO.siteName			 = 	cursor.getString(31);
//						orderDO.address				 = 	cursor.getString(32);
//						orderDO.trxSubType			 =  cursor.getInt(33);
//						
//						orderDO.arrTrxDetailsDOs	=   getProductsOfOrder(slDatabase, orderDO.trxCode);
//						orderDO.arrPromotionDOs	    =   getTRXPromotions(slDatabase, orderDO.trxCode);
//						
//						
//						if(orderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER())
//						{
//							
//						
//							/*
//							 * Missed Order
//							 */
//							
//							
//							Predicate<TrxDetailsDO> missedSearchItem = new Predicate<TrxDetailsDO>() {
//								public boolean apply(TrxDetailsDO trxDetailsDO) {
//									return trxDetailsDO.missedBU>0;
//								}
//							};
//							Collection<TrxDetailsDO> miisedfilteredResult =filter(orderDO.arrTrxDetailsDOs,missedSearchItem);
//							
//							if(miisedfilteredResult!=null && miisedfilteredResult.size()>0)
//							{
//								TrxHeaderDO trxHeaderDO		 = (TrxHeaderDO) orderDO.clone();
//								
//								trxHeaderDO.trxType			 = TrxHeaderDO.get_TRXTYPE_MISSED_ORDER();
//								trxHeaderDO.arrTrxDetailsDOs = (ArrayList<TrxDetailsDO>) miisedfilteredResult;
//								
//								if(!hmOrders.containsKey(trxHeaderDO.trxType))
//									vecDetails = new Vector<TrxHeaderDO>();
//								else
//									vecDetails = hmOrders.get(trxHeaderDO.trxType);
//								
//								vecDetails.add(trxHeaderDO);
//								hmOrders.put(trxHeaderDO.trxType, vecDetails);
//							}
//							
//							Predicate<TrxDetailsDO> salesSearchItem = new Predicate<TrxDetailsDO>() {
//								public boolean apply(TrxDetailsDO trxDetailsDO) {
//									return trxDetailsDO.quantityBU>0;
//								}
//							};
//							Collection<TrxDetailsDO> salesfilteredResult =filter(new Vector<TrxDetailsDO>(
//							        (ArrayList<TrxDetailsDO>)orderDO.arrTrxDetailsDOs),salesSearchItem);
//							
//							orderDO.arrTrxDetailsDOs = (ArrayList<TrxDetailsDO>) salesfilteredResult;
//							
//							if(orderDO.trxSubType== TrxHeaderDO.get_TRX_SUBTYPE_SAVED_ORDER() && orderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
//								trxType = TrxHeaderDO.get_TRXTYPE_SAVED_ORDER();
//							else if(orderDO.trxSubType == TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER() && orderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
//								trxType = TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER();
//							else
//								trxType = orderDO.trxType;
//						}
//						else
//							trxType = orderDO.trxType;
////						if(!hmOrders.containsKey(TrxHeaderDO.get_TRXTYPE_SAVED_ORDER()))
////							vecDetails = new Vector<TrxHeaderDO>();
////						else
////							vecDetails = hmOrders.get(TrxHeaderDO.get_TRXTYPE_SAVED_ORDER());
//						
//						
//						if(!hmOrders.containsKey(trxType))
//							vecDetails = new Vector<TrxHeaderDO>();
//						else
//							vecDetails = hmOrders.get(trxType);
//						
//						
//						if(orderDO.arrTrxDetailsDOs!=null && orderDO.arrTrxDetailsDOs.size()>0)
//						{
//							vecDetails.add(orderDO);
//							hmOrders.put(trxType, vecDetails);
//						}
//						
//							
//					}
//					while(cursor.moveToNext());
//				}
//				
//				if(cursor != null && !cursor.isClosed())
//					cursor.close();
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//			finally
//			{
//				if(slDatabase != null)
//					slDatabase.close();
//			}
//			return hmOrders;
//		}
//	}updatePaymentStatus
	
	public HashMap<String, String> getTotalAmount(boolean isPreseller,String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, String> hsTotal = new HashMap<String, String>(); //tblPaymentHeader
			Cursor cursor  = null;
			String query   = "";
			query = "SELECT PD.PaymentTypeCode,SUM(PD.Amount) FROM tblPaymentDetail PD INNER JOIN tblPaymentHeader PH ON PH.ReceiptId = PD.ReceiptNo WHERE PH.PaymentDate LIKE '"+date+"%' GROUP BY PD.PaymentTypeCode";
					
			try
			{
				DatabaseHelper.openDataBase();
				cursor = DatabaseHelper._database.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						hsTotal.put(cursor.getString(0), cursor.getString(1));
						
					}while(cursor.moveToNext());
					
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
				DatabaseHelper.closedatabase();
			}
			return hsTotal;
		}
	}
	
	
	public Vector<OrderDO> getPresellerReturnOrderList()
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<OrderDO> vecOrderList 	= new Vector<OrderDO>();
			SQLiteDatabase sqLiteDatabase 	= null;
			Cursor cursor = null;
			String query ="SELECT  TG.GRVId, TG.CustomerSiteId ,TCS.SiteName,TG .GRVDate  from tblGRV TG , tblCustomerSites TCS where TG.CustomerSiteId = TCS .CustomerSiteId";
					
			try
			{
				sqLiteDatabase 	= DatabaseHelper.openDataBase();
				cursor 			= sqLiteDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						OrderDO object 			= 	new OrderDO();
						object.OrderId 			= 	cursor.getString(0);
						object.CustomerSiteId 	= 	cursor.getString(1);
						object.strCustomerName	= 	cursor.getString(2);
						object.InvoiceDate		= 	cursor.getString(3);
						object.orderType 		= 	"RETURNORDER";
						
						vecOrderList.add(object);
						
					}
					while(cursor.moveToNext());
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				
				return vecOrderList;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return new Vector<OrderDO>();
		}
	}
}

