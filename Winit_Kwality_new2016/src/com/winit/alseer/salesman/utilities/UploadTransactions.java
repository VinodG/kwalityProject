package com.winit.alseer.salesman.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.winit.alseer.parsers.GetInsertInvoicePrintImageParser;
import com.winit.alseer.parsers.InsertCustomerParser;
import com.winit.alseer.parsers.InsertLoadParser;
import com.winit.alseer.parsers.InsertOrdersParser;
import com.winit.alseer.parsers.InsertPaymentParser;
import com.winit.alseer.parsers.UploadChequeImagesParser;
import com.winit.alseer.parsers.UploadGrvImagesParser;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CashDenominationDA;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.InventoryDA;
import com.winit.alseer.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.alseer.salesman.dataaccesslayer.MyActivitiesDA;
import com.winit.alseer.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.alseer.salesman.dataaccesslayer.ReturnOrderDA;
import com.winit.alseer.salesman.dataaccesslayer.StatusDA;
import com.winit.alseer.salesman.dataaccesslayer.TransferInOutDA;
import com.winit.alseer.salesman.dataaccesslayer.VehicleDA;
import com.winit.alseer.salesman.dataobject.CashDenomDO;
import com.winit.alseer.salesman.dataobject.CustomerDao;
import com.winit.alseer.salesman.dataobject.CustomerVisitDO;
import com.winit.alseer.salesman.dataobject.DamageImageDO;
import com.winit.alseer.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.alseer.salesman.dataobject.InventoryDO;
import com.winit.alseer.salesman.dataobject.JouneyStartDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.LoadRequestDO;
import com.winit.alseer.salesman.dataobject.MallsDetails;
import com.winit.alseer.salesman.dataobject.MyActivityDO;
import com.winit.alseer.salesman.dataobject.NewCustomerDO;
import com.winit.alseer.salesman.dataobject.OrderPrintImageDO;
import com.winit.alseer.salesman.dataobject.PostPaymentDONew;
import com.winit.alseer.salesman.dataobject.PostPaymentDetailDONew;
import com.winit.alseer.salesman.dataobject.PostReasonDO;
import com.winit.alseer.salesman.dataobject.StatusDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class UploadTransactions extends IntentService{

	private Preference preference;
	private static TransactionProcessListner transactionProcessListner;
	private static Transactions currentTransaction=Transactions.NONE;
	private static TransactionSatus currentTransactionSatus=TransactionSatus.NONE;
	public enum Transactions{
		LOADREQUEST,
		CUSTOMERS,
		UPDATECUSTOMERDETAIL,
		ORDERS,
		MODIFIEDORDERS,
		COLLECTEDRETURNORDER,
		ADVANCEORDER,
		PAYMENTS,
		JOURNEYLOG,
		RETURNSTOCK,
		TRANSFERINOUT,
		CHECKINDEMAND,
		SKIPREASON,
		MYACTVITIES,
		CUSTOMERVISITS,
		PASSCODES,
		INVENTORY,
//		SERVICECAPTURE,
		CASHDENOM,
		UNVISITED,
		STATUS,
		JOURNEYSTART,
		CUSTOMERSPECIALDAY,
		GRVIMAGES,
		CHEQUEIMAGES,
		INSERT_INVOICE_PRINT_IMAGE,
		ALL,
		NONE
	}
	public enum TransactionSatus{
		START,
		ERROR_NO_INTERNETCONNECTION,
		ERROR_TIMEOUT,
		ERROR_EXCEPTION,
		SUCCESS,
		FAILURE,
		NODATA,
		END,
		NONE
	}
	public interface TransactionProcessListner{
		public void transactionStatus(Transactions transactions,TransactionSatus transactionSatus);
		public void error(TransactionSatus transactionSatus);
		public void currentTransaction(Transactions transactions);
	}
	public UploadTransactions() {
		super("UploadTransactions");
	}

	public static void setTransactionProcessListner(TransactionProcessListner transaction){
		transactionProcessListner=transaction;
		updateTransactionStatus();
	}
	public static void resetTransactionProcessListner(){
		transactionProcessListner=null;
		currentTransaction=Transactions.NONE;
		currentTransactionSatus=TransactionSatus.NONE;
				
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		preference 						 = 	new Preference(this);
		Log.e("UploadTransactions", "onHandleIntent start");
		if(NetworkUtility.isNetworkConnectionAvailable(this)){
			
			  uploadTransaction(Transactions.LOADREQUEST);
			uploadTransaction( Transactions.CUSTOMERS);
			uploadTransaction(Transactions.UPDATECUSTOMERDETAIL);
			uploadTransaction(Transactions.ORDERS);
			uploadTransaction(Transactions.MODIFIEDORDERS);
			uploadTransaction(Transactions.COLLECTEDRETURNORDER);
			uploadTransaction(Transactions.ADVANCEORDER);
			uploadTransaction(Transactions.PAYMENTS);
			uploadTransaction(Transactions.JOURNEYLOG);
			uploadTransaction(Transactions.RETURNSTOCK);
			uploadTransaction(Transactions.TRANSFERINOUT);
			uploadTransaction(Transactions.CHECKINDEMAND);
			uploadTransaction(Transactions.SKIPREASON);
			uploadTransaction(Transactions.JOURNEYSTART);
			uploadTransaction(Transactions.MYACTVITIES);
			uploadTransaction(Transactions.CUSTOMERVISITS);
			uploadTransaction(Transactions.PASSCODES);
			uploadTransaction(Transactions.INVENTORY);
//			uploadTransaction(Transactions.SERVICECAPTURE);
			uploadTransaction(Transactions.CASHDENOM);
			uploadTransaction(Transactions.UNVISITED);
			uploadTransaction(Transactions.STATUS);
//			uploadTransaction(Transactions.CUSTOMERSPECIALDAY);
			uploadTransaction(Transactions.GRVIMAGES);
			uploadTransaction(Transactions.CHEQUEIMAGES);
//			uploadTransaction(Transactions.INSERT_INVOICE_PRINT_IMAGE);





			currentTransaction=Transactions.NONE;
			currentTransactionSatus=TransactionSatus.END;
			updateTransactionStatus();
		}else{
			
			Log.e("UploadTransactions", "onHandleIntent error");
			if(transactionProcessListner!=null)
				transactionProcessListner.error(TransactionSatus.ERROR_NO_INTERNETCONNECTION);
		}
		
		Log.e("UploadTransactions", "onHandleIntent end");
	}
	private static void updateTransactionStatus(){
		try {
			if(transactionProcessListner!=null){
				transactionProcessListner.currentTransaction(currentTransaction);
				transactionProcessListner.transactionStatus(currentTransaction, currentTransactionSatus);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//tblTrxLogDetails
	private void uploadTransaction(Transactions transaction){
		try {
			
			Log.e("UploadTransactions", "uploadTransaction "+transaction);
			switch (transaction) {
			case LOADREQUEST:
				uploadLoadRequest();
				break;
			case CUSTOMERS:
				uploadCustomer();
				break;
			case ORDERS:
				uploadOrders();
				break;
			case COLLECTEDRETURNORDER:
				uploadCollectedReturnOrders();
				break;	
			case PAYMENTS:
				uploadPayments();
				break;
			case INVENTORY:
				uploadInventory();
				break;
//			case SERVICECAPTURE:  tblcustomervisit
//				uploadServiceImage();
//				break;
			case CASHDENOM:
				uploadCashDenom();
				break;
			case UNVISITED:
				uploadUnVisitedCustomer();
				break;
			case STATUS:
				uploadStatus();
				break;
			case GRVIMAGES:
				uploadGRVImages();
				break;
			case CHEQUEIMAGES:
				uploadChequeImages();
				break;
			case  INSERT_INVOICE_PRINT_IMAGE :
				uploadInvoicePrintImages();
				break;
			case ADVANCEORDER:
				break;
			case CHECKINDEMAND:
				uploadCheckINDemandInventory();
				break;
			case CUSTOMERSPECIALDAY:
				uploadCustomerspeialDay();
				break;
			case CUSTOMERVISITS:
				uploadCustomerVisit();
				break;
			case JOURNEYLOG:
				uploadJournyLogOnSever();
				break;
			case JOURNEYSTART:
				uploadJourneyStart();
				break;
			case MYACTVITIES:
				uploadMyActivities();
				break;
			case PASSCODES:
				uploadPasscode();
				break;
			case RETURNSTOCK:
				uploadReturnStock();
				break;
			case SKIPREASON:
				uploadSkipReason();
				break;
			case TRANSFERINOUT:
				uploadTransferInOut();
				break;
			case MODIFIEDORDERS:
				uploadModifiedOrders();
				break;
			case UPDATECUSTOMERDETAIL:
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void uploadLoadRequest() 
	{
		try
		{
			ArrayList<LoadRequestDO> vecLoad = new InventoryDA().getAllLoadRequestToPost();
			if(vecLoad != null && vecLoad.size() > 0)
			{
				currentTransaction =Transactions.LOADREQUEST;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				InsertLoadParser insertLoadParser = new InsertLoadParser(this);
				new ConnectionHelper(null).sendRequest(this,BuildXMLRequest.uploadLoadRequests(vecLoad), insertLoadParser, ServiceURLs.PostMovementDetailsFromXML);
				currentTransaction =Transactions.LOADREQUEST;
				currentTransactionSatus=TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			currentTransaction =Transactions.LOADREQUEST;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
	
	private void uploadCustomer()
	{
		try
		{
			Vector<NewCustomerDO> vector = new CommonDA().getNewCustomerToUpload();
			final InsertCustomerParser insertCustomerParser = new InsertCustomerParser(this);
			final ConnectionHelper connectionHelper 		= new ConnectionHelper(null);
			if(vector !=null && vector.size() > 0)
			{
				currentTransaction =Transactions.CUSTOMERS;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				
				String route_code = preference.getStringFromPreference(Preference.ROUTE_CODE, "");
				connectionHelper.sendRequest_Bulk(this,BuildXMLRequest.insertHHCustomer(vector, route_code), insertCustomerParser, ServiceURLs.INSERTHH_CUSTOMER_OFFLINE);
				
				
				currentTransaction =Transactions.CUSTOMERS;
				currentTransactionSatus=TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			currentTransaction =Transactions.CUSTOMERS;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
	private boolean uploadOrders()
	{
		try
		{
			int MAX_ORDER_PUSH_LOOP_COUNT = new CommonDA().getUnUploadedOrderStatus(preference.getStringFromPreference(Preference.EMP_NO, ""));
			Vector<TrxHeaderDO> vecSalesOrders  	 = 	new CommonDA().getAllSalesOrderToPost(preference.getStringFromPreference(Preference.EMP_NO, ""));
			final InsertOrdersParser insertOrdersParser  = new InsertOrdersParser(this);
			if(vecSalesOrders != null && vecSalesOrders.size() > 0)
			{
				currentTransaction =Transactions.ORDERS;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				int limit = 0;
				do
				{
					for(TrxHeaderDO trxHeaderDO : vecSalesOrders)
					{
						
						ConnectionHelper connectionHelper = new ConnectionHelper(null);
						connectionHelper.sendRequest_Bulk(this,BuildXMLRequest.postTrxDetailsFromXML(trxHeaderDO, preference.getStringFromPreference(Preference.EMP_NO, "")), insertOrdersParser, ServiceURLs.PostTrxDetailsFromXML);
					}
				
					vecSalesOrders  	 = 	new CommonDA().getAllSalesOrderToPost(preference.getStringFromPreference(Preference.EMP_NO, ""));
					limit++;
					
				}while(vecSalesOrders.size() > 0 && /*AppConstants.MAX_ORDER_PUSH_LIMIT*/MAX_ORDER_PUSH_LOOP_COUNT>=limit);
				
				currentTransaction      = Transactions.ORDERS;
				currentTransactionSatus = TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			currentTransaction =Transactions.ORDERS;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
        return true;
	}
	
	private boolean uploadModifiedOrders()
	{
//		try
//		{
//			final Vector<TrxHeaderDO> vecSalesOrders  	 = 	new CommonDA().getAllModifiedOrderToPost(preference.getStringFromPreference(Preference.EMP_NO, ""));
//			final InsertOrdersParser insertOrdersParser  = new InsertOrdersParser(this);
//			if(vecSalesOrders != null && vecSalesOrders.size() > 0)
//			{
//				currentTransaction =Transactions.MODIFIEDORDERS;
//				currentTransactionSatus=TransactionSatus.START;
//				updateTransactionStatus();
//				
//				ConnectionHelper connectionHelper = new ConnectionHelper(null);
//				connectionHelper.sendRequest_Bulk(this,BuildXMLRequest.postTrxDetails(vecSalesOrders, preference.getStringFromPreference(Preference.EMP_NO, "")), insertOrdersParser, ServiceURLs.PostTrxDetails);
//				
//				currentTransaction = Transactions.MODIFIEDORDERS;
//				currentTransactionSatus=TransactionSatus.SUCCESS;
//				updateTransactionStatus();
//			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			currentTransaction =Transactions.MODIFIEDORDERS;
//			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
//			updateTransactionStatus();
//		}
        return true;
	}
	
	private boolean uploadCollectedReturnOrders()
	{
//		try
//		{
//			final Vector<TrxHeaderDO> vecSalesOrders  	 = 	new CommonDA().getAllCollectedReturnOrderToPost(preference.getStringFromPreference(Preference.EMP_NO, ""));
//			final InsertOrdersParser insertOrdersParser  = new InsertOrdersParser(this);
//			if(vecSalesOrders != null && vecSalesOrders.size() > 0)
//			{
//				currentTransaction =Transactions.COLLECTEDRETURNORDER;
//				currentTransactionSatus=TransactionSatus.START;
//				updateTransactionStatus();
//				
//				ConnectionHelper connectionHelper = new ConnectionHelper(null);
//				connectionHelper.sendRequest_Bulk(this,BuildXMLRequest.sendAllOrders(vecSalesOrders, preference.getStringFromPreference(Preference.EMP_NO, "")), insertOrdersParser, ServiceURLs.PostTrxDetails);
//				
//				currentTransaction =Transactions.COLLECTEDRETURNORDER;
//				currentTransactionSatus=TransactionSatus.END;
//				updateTransactionStatus();
//			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			
//			currentTransaction =Transactions.COLLECTEDRETURNORDER;
//			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
//			updateTransactionStatus();
//		}
        return true;
	}
	private void uploadPayments()
	{
		try
		{
			CommonDA commonDA = new CommonDA();
			Vector<PostPaymentDONew> vecPayments =   new Vector<PostPaymentDONew>();
			vecPayments						  =   commonDA.getAllPaymentsToPostNew(preference.getStringFromPreference(Preference.SALESMANCODE, ""), CalendarUtils.getOrderPostDate(), preference.getStringFromPreference(Preference.USER_ID, ""));
			ConnectionHelper connectionHelper = new ConnectionHelper(null);
			if(vecPayments != null && vecPayments.size() > 0)
			{
				currentTransaction =Transactions.PAYMENTS;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				
				InsertPaymentParser insertPaymentParser	= new InsertPaymentParser(this);
				connectionHelper.sendRequest_Bulk(this,BuildXMLRequest.postPayments(vecPayments),insertPaymentParser, ServiceURLs.PostPaymentDetailsFromXML);
				boolean isPaymentsUploaded = insertPaymentParser.getStatus();
				
				if(isPaymentsUploaded)
					commonDA.updatePaymentStatus(vecPayments);
				
				currentTransaction =Transactions.PAYMENTS;
				currentTransactionSatus=TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			currentTransaction =Transactions.PAYMENTS;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
	private void uploadJournyLogOnSever()
	{
		try
		{
			String salesmanCode  = preference.getStringFromPreference(Preference.SALESMANCODE, "");
			ArrayList<MallsDetails> vecJournyLog  =		new CustomerDetailsDA().getJournyLog(salesmanCode,CalendarUtils.getOrderPostDate());
			if(vecJournyLog != null && vecJournyLog.size() > 0)
			{
				currentTransaction =Transactions.JOURNEYLOG;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				
				final ConnectionHelper connectionHelper = new ConnectionHelper(null);
				boolean isSubmitted = connectionHelper.sendRequest(this,BuildXMLRequest.insertCustomerVisit(vecJournyLog,preference.getStringFromPreference(Preference.EMP_NO, "")), ServiceURLs.InsertCustomerVisit);
				if(isSubmitted)
					new CustomerDetailsDA().updateJourneyLogStatus();
				
				currentTransaction =Transactions.JOURNEYLOG;
				currentTransactionSatus=TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			currentTransaction =Transactions.JOURNEYLOG;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
	private void uploadReturnStock()
	{
		try
		{
			ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct = new VehicleDA().getAllItemToReturn();
			if(vecOrdProduct != null && vecOrdProduct.size() > 0)
			{
				currentTransaction =Transactions.RETURNSTOCK;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				
				final String strEmpNo = preference.getStringFromPreference(Preference.EMP_NO,"");
				final boolean isUpdateReturnStock = new ConnectionHelper(null).sendRequest(this,BuildXMLRequest.updateReturnStock(vecOrdProduct, strEmpNo), ServiceURLs.UpdateReturnStock);
				if(isUpdateReturnStock)
					new VehicleDA().updateReturnstockPostStatus(vecOrdProduct);
				
				currentTransaction =Transactions.RETURNSTOCK;
				currentTransactionSatus=TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			currentTransaction =Transactions.RETURNSTOCK;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
	private void uploadTransferInOut()
	{
		try
		{
			TransferInOutDA transferInOutDA = new TransferInOutDA();
//			Vector<TransferInoutDO> vecTransferInoutDOs = new Vector<TransferInoutDO>();
//			vecTransferInoutDOs = transferInOutDA.getUnuploadedTransferData();
			ArrayList<LoadRequestDO> vecTransferInoutDOs = new ArrayList<LoadRequestDO>();
			vecTransferInoutDOs = transferInOutDA.getAllVanTransferLoadRequestToPost();
			if(vecTransferInoutDOs != null && vecTransferInoutDOs.size() > 0)
			{
				currentTransaction =Transactions.TRANSFERINOUT;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				
//				if(new ConnectionHelper(null).sendRequest(this,BuildXMLRequest.PostTransferOuts(vecTransferInoutDOs), ServiceURLs.PostTransferOuts))
//					transferInOutDA.updateTransferInOUTStatusNew(vecTransferInoutDOs, "Y");
				
				if(new ConnectionHelper(null).sendRequest(this,BuildXMLRequest.UpdateVanTransferStockUpload(vecTransferInoutDOs), ServiceURLs.UpdateTransferInOrOutStock))
					transferInOutDA.updateVanTransferStatus(vecTransferInoutDOs, "Y");
				
				currentTransaction =Transactions.TRANSFERINOUT;
				currentTransactionSatus=TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			currentTransaction =Transactions.TRANSFERINOUT;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
	private void uploadCheckINDemandInventory() 
	{
//		Vector<CheckInDemandInventoryDO> vector = commonDA.getCheckINDemandInventory();
//		if(vector != null && vector.size() > 0)
//		{
//			ConnectionHelper connectionHelper 	  = new ConnectionHelper(null);
//			boolean isUploaded = connectionHelper.sendRequest(context,BuildXMLRequest.insertCheckInDemandStock(vector), ServiceURLs.ImportCheckInDemandStock);
//		
//			if(isUploaded)
//				commonDA.UpdateDemandInventory(vector, 1);
//		}
	}
	private void uploadSkipReason()
	{
		try
		{
			ArrayList<PostReasonDO> vecArrayList = new CommonDA().getSkipReasonsToPost();
			if(vecArrayList != null && vecArrayList.size() > 0)
			{
				currentTransaction =Transactions.SKIPREASON;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				if(new ConnectionHelper(null).sendRequest(this,BuildXMLRequest.postReasons(vecArrayList), ServiceURLs.InsertSkippingReason))
				{
//					new CommonDA().updateSkipReasonNew(vecArrayList, CalendarUtils.getCurrentDateAsString());
					new CommonDA().updateSkipReasonNew(vecArrayList, CalendarUtils.getOrderPostDate());
				}
				
				currentTransaction =Transactions.SKIPREASON;
				currentTransactionSatus=TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			currentTransaction =Transactions.SKIPREASON;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
	
	private void uploadJourneyStart()
	{
		try
		{
			final Vector<JouneyStartDO> vecJourneyStart = new JourneyPlanDA().getJourneyStart();
			
			currentTransaction =Transactions.JOURNEYSTART;
			currentTransactionSatus=TransactionSatus.START;
			updateTransactionStatus();
			
			boolean isSuccess = true, isSuccessDriver = true;
			for(JouneyStartDO journey : vecJourneyStart)
			{ 
				if(journey.StoreKeeperSignatureStartDay != null) 
				{
					if(new File(journey.StoreKeeperSignatureStartDay).exists())
					{
						String server_path = new UploadImage().uploadImage(this, journey.StoreKeeperSignatureStartDay, ServiceURLs.stockverifiedsignature, true);
						if(server_path != null && server_path.length() > 0)
						{
							journey.StoreKeeperSignatureStartDay = server_path;
							new JourneyPlanDA().updateJourneyStartSignature(AppConstants.STORE_SIGN_START, server_path, journey.journeyAppId);
						}
						else
							isSuccess = false;
					}
					
					if(new File(journey.StoreKeeperSignatureEndDay).exists())
					{
						String server_path = new UploadImage().uploadImage(this, journey.StoreKeeperSignatureEndDay, ServiceURLs.stockverifiedsignature, true);
						if(server_path != null && server_path.length() > 0)
						{
							journey.StoreKeeperSignatureEndDay = server_path;
							new JourneyPlanDA().updateJourneyStartSignature(AppConstants.STORE_SIGN_END, server_path, journey.journeyAppId);
						}
						else
							isSuccessDriver = true;
					}
					
					if(new File(journey.SalesmanSignatureStartDay).exists())
					{
						String server_path = new UploadImage().uploadImage(this, journey.SalesmanSignatureStartDay, ServiceURLs.stockverifiedsignature, true);
						if(server_path != null && server_path.length() > 0)
						{
							journey.SalesmanSignatureStartDay = server_path;
							new JourneyPlanDA().updateJourneyStartSignature(AppConstants.SALES_SIGN_START, server_path, journey.journeyAppId);
						}
						else
							isSuccessDriver = false;
					}
					
					if(new File(journey.SalesmanSignatureEndDay).exists())
					{
						String server_path = new UploadImage().uploadImage(this, journey.SalesmanSignatureEndDay, ServiceURLs.stockverifiedsignature, true);
						if(server_path != null && server_path.length() > 0)
						{
							journey.SalesmanSignatureEndDay = server_path;
							new JourneyPlanDA().updateJourneyStartSignature(AppConstants.SALES_SIGN_END, server_path, journey.journeyAppId);
						}
						else
							isSuccessDriver = true;
					}
				}
			}
			if(isSuccess && isSuccessDriver && vecJourneyStart != null && vecJourneyStart.size()>0)
			{
				if(new ConnectionHelper(null).sendRequest(this,BuildXMLRequest.getStartJournyStart(vecJourneyStart), ServiceURLs.PostJourneyDetails))
				{
					for(JouneyStartDO journey : vecJourneyStart)
						new JourneyPlanDA().updateJourneyStartUploadStatus(true, journey.journeyAppId); 
				}
			}
			currentTransaction =Transactions.JOURNEYSTART;
			currentTransactionSatus=TransactionSatus.SUCCESS;
			updateTransactionStatus();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			currentTransaction =Transactions.JOURNEYSTART;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
	private void uploadMyActivities()
	{ 
		try
		{
			Vector<MyActivityDO> vecMyActivityDOs = new MyActivitiesDA().getAllUnUploadedActivities();
			if((vecMyActivityDOs != null && vecMyActivityDOs.size() > 0)) 
			{
				currentTransaction =Transactions.MYACTVITIES;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				
				final ConnectionHelper connectionHelper 		= new ConnectionHelper(null);
				boolean isPost = connectionHelper.sendRequest(this,BuildXMLRequest.postTask(vecMyActivityDOs/*, vecCustomerSurveyDOs*/), ServiceURLs.InsertTaskOrder);
				if(isPost)
					new MyActivitiesDA().updateActivities(vecMyActivityDOs);
				
				currentTransaction =Transactions.MYACTVITIES;
				currentTransactionSatus=TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			currentTransaction =Transactions.MYACTVITIES;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
	private void uploadPasscode()
	{
		
		try
		{
			String passcode = new CommonDA().getUsedPasscode();
			if(passcode != null && passcode.length() > 0)
			{
				currentTransaction =Transactions.PASSCODES;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				boolean isUpdated = connectionHelper.sendRequest(this, BuildXMLRequest.updatePasscodeStatus(preference.getStringFromPreference(Preference.EMP_NO, ""), passcode),ServiceURLs.UPDATE_PASSCODE_STATUS);
				if(isUpdated)
					new CommonDA().deletePasscode(passcode);
				
				currentTransaction =Transactions.PASSCODES;
				currentTransactionSatus=TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			currentTransaction =Transactions.PASSCODES;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
	private void uploadCustomerVisit()
	{
		try
		{
			Vector<CustomerVisitDO> vecCusotmerVisit = new CustomerDA().getCustomerVisit();
			if(vecCusotmerVisit.size()>0 && new ConnectionHelper(null).sendRequest(this,BuildXMLRequest.getCustomerVisitXML(vecCusotmerVisit), ServiceURLs.PostClientVisits))
			{
				currentTransaction =Transactions.CUSTOMERVISITS;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				
				for(CustomerVisitDO journey : vecCusotmerVisit)
					new CustomerDA().updateCustomerVisitUploadStatus(true, journey.CustomerVisitAppId);
				
				currentTransaction =Transactions.CUSTOMERVISITS;
				currentTransactionSatus=TransactionSatus.END;
				updateTransactionStatus();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			currentTransaction =Transactions.CUSTOMERVISITS;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
	private void uploadInventory()
	{
		try
		{
			Vector<InventoryDO> vecInventory 	= 	new Vector<InventoryDO>();
			CommonDA commonDA 					= 	new CommonDA();
			vecInventory  						= 	commonDA.getAllStoreCheck(preference.getStringFromPreference(Preference.EMP_NO, ""));
			
			if(vecInventory != null && vecInventory.size() > 0)
			{
				currentTransaction =Transactions.INVENTORY;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				boolean isUploaded = connectionHelper.sendRequest(this, BuildXMLRequest.sendAllStoreCheck(vecInventory), ServiceURLs.PostStorecheckDetails);
				if(isUploaded)
					commonDA.updateInventory(vecInventory);
				
				currentTransaction =Transactions.INVENTORY;
				currentTransactionSatus=TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			currentTransaction =Transactions.INVENTORY;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
	
//	private void uploadServiceImage()
//	{
//		try
//		{
//			Vector<ServiceCaptureDO> vecServiceCaptureDOs 	= new Vector<ServiceCaptureDO>();
//			StoreCheckDA storecheckDA						= new StoreCheckDA();
//			vecServiceCaptureDOs 							= new StoreCheckDA().getServiceCapture();
//			
//			if(vecServiceCaptureDOs != null && vecServiceCaptureDOs.size() > 0)
//			{
//				currentTransaction =Transactions.SERVICECAPTURE;
//				currentTransactionSatus=TransactionSatus.START;
//				updateTransactionStatus();
//				
//				ConnectionHelper connectionHelper = new ConnectionHelper(null);
//				ServiceCaptureParser serviceCaptureParser = null;
//
//				for (int i = 0; i < vecServiceCaptureDOs.size(); i++) 
//				{
//					if(vecServiceCaptureDOs.get(i).BeforeImage != null && !vecServiceCaptureDOs.get(i).BeforeImage.contains("../Data"))
//					{
//						String cameraImage = new UploadImage().uploadImage(this, vecServiceCaptureDOs.get(i).BeforeImage, "ServiceCapture", true);
//						if(cameraImage != null && !cameraImage.equalsIgnoreCase(""))
//							vecServiceCaptureDOs.get(i).BeforeImage = cameraImage;
//					}
//					if(vecServiceCaptureDOs.get(i).AfterImage != null && !vecServiceCaptureDOs.get(i).AfterImage.contains("../Data"))
//					{
//						String cameraImage = new UploadImage().uploadImage(this, vecServiceCaptureDOs.get(i).AfterImage, "ServiceCapture", true);
//						if(cameraImage != null && !cameraImage.equalsIgnoreCase(""))
//							vecServiceCaptureDOs.get(i).AfterImage = cameraImage;
//					}
//					serviceCaptureParser = new ServiceCaptureParser(getApplicationContext(),vecServiceCaptureDOs.get(i));
//					connectionHelper.sendRequest(getApplicationContext(), BuildXMLRequest.InsertServiceCaptureSingle(vecServiceCaptureDOs.get(i)), serviceCaptureParser, ServiceURLs.InsertServiceCapture);
//				}
//				
//				currentTransaction =Transactions.SERVICECAPTURE;
//				currentTransactionSatus=TransactionSatus.SUCCESS;
//				updateTransactionStatus();
//			}
//		}
//		catch (Exception ex)
//		{
//			ex.printStackTrace();
//			
//			currentTransaction =Transactions.SERVICECAPTURE;
//			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
//			updateTransactionStatus();
//		}
//	}
	
	private void uploadCashDenom()
	{
		try
		{
			Vector<CashDenomDO> vecCashdenom 		= new Vector<CashDenomDO>();
			CashDenominationDA cashDenomDA			= new CashDenominationDA();
			vecCashdenom 							= cashDenomDA.getAllCashDenom();
			
			if(vecCashdenom != null && vecCashdenom.size() > 0)
			{
				currentTransaction =Transactions.CASHDENOM;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				boolean isUploaded = connectionHelper.sendRequest(this, BuildXMLRequest.sendAllCashDenom(preference.getStringFromPreference(preference.USER_ID, ""),vecCashdenom), ServiceURLs.PostCasnDenomintaionsFromXML);
				if(isUploaded)
					cashDenomDA.updateAllCashDenom();
				
				currentTransaction =Transactions.CASHDENOM;
				currentTransactionSatus=TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			
			currentTransaction =Transactions.CASHDENOM;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
	
	private void uploadUnVisitedCustomer()
	{
		try
		{
			ArrayList<JourneyPlanDO> arrJopurneyPlanDO 		= new ArrayList<JourneyPlanDO>();
			CustomerDA customerDA							= new CustomerDA();
			arrJopurneyPlanDO 								= customerDA.getAllUnVisitedCustomerdetail();
			
			if(arrJopurneyPlanDO != null && arrJopurneyPlanDO.size() > 0)
			{
				currentTransaction =Transactions.UNVISITED;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				boolean isUploaded = connectionHelper.sendRequest(this, BuildXMLRequest.sendUnVisitedCustomers(arrJopurneyPlanDO), ServiceURLs.PostUnvisitedCustomersFromXML);
//				if(isUploaded)
//					cashDenomDA.updateAllCashDenom();
				
				currentTransaction =Transactions.UNVISITED;
				currentTransactionSatus=TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			
			currentTransaction =Transactions.UNVISITED;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
	
	private void uploadStatus()
	{
		try
		{
			StatusDA statusDA = new StatusDA();
			Vector<StatusDO> vecStatus = statusDA.getUnUploadedStatusDO();
			
			if(vecStatus != null && vecStatus.size() > 0)
			{
				currentTransaction =Transactions.STATUS;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				boolean isUploaded = connectionHelper.sendRequest(this, BuildXMLRequest.sendAllStatus(vecStatus), ServiceURLs.InsertDeviceStatus);
				if(isUploaded)
					statusDA.updatetblStatus(vecStatus);
				
				currentTransaction =Transactions.STATUS;
				currentTransactionSatus=TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			
			currentTransaction =Transactions.STATUS;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
	private boolean uploadCustomerspeialDay() 
	{
		
		try
		{
//			Vector<CustomerDao> vecCustomerDao = new CustomerDA().getCustomerDao();
			Vector<CustomerDao> vecCustomerDao = new CustomerDA().getCustomerExcess();
			if(vecCustomerDao != null && vecCustomerDao.size() > 0)
			{
				
				currentTransaction =Transactions.CUSTOMERSPECIALDAY;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				boolean isUploaded = connectionHelper.sendRequest(this, BuildXMLRequest.updateCustomerDetails(vecCustomerDao), ServiceURLs.UpdateCustomerDetails);
				if(isUploaded)
				{
//					new CustomerDA().updateCustomerDao(vecCustomerDao);
					new CustomerDA().updateTBLCustomer(vecCustomerDao);
				}
				
				currentTransaction =Transactions.CUSTOMERSPECIALDAY;
				currentTransactionSatus=TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			currentTransaction =Transactions.CUSTOMERSPECIALDAY;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
		return true;
	}
	private void uploadGRVImages()
	{
		try
		{
			HashMap<String,ArrayList<DamageImageDO>> hmImages = new HashMap<String, ArrayList<DamageImageDO>>();
			
			hmImages = new ReturnOrderDA().getAllGRVImages();
			if(hmImages != null && hmImages.size() > 0)
			{
				currentTransaction =Transactions.GRVIMAGES;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				
				
				ConnectionHelper connectionHelper = new ConnectionHelper(null);

				UploadGrvImagesParser uploadGrvImagesParser = new UploadGrvImagesParser(this);
				connectionHelper.sendRequest_Bulk(this, BuildXMLRequest.uploadGRVImages(hmImages),uploadGrvImagesParser, ServiceURLs.PostGRVImages);
				boolean isImagesUploaded = uploadGrvImagesParser.getStatus();
				
				if(isImagesUploaded)
					new ReturnOrderDA().updateDamagedImageUploadDetailStatus(uploadGrvImagesParser.getData());
				
				currentTransaction =Transactions.GRVIMAGES;
				currentTransactionSatus=TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			currentTransaction =Transactions.GRVIMAGES;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
	
	private void uploadInvoicePrintImages()
	{
		try
		{
			ArrayList<OrderPrintImageDO> arr=null;

			arr = new ReturnOrderDA().getAllOrderPrintImagesNew();
			currentTransaction = Transactions.INSERT_INVOICE_PRINT_IMAGE;
			currentTransactionSatus = TransactionSatus.START;
			updateTransactionStatus();
			if(arr != null && arr.size() > 0)
			{
				for (int i = 0;i<arr.size();i++) {



					ConnectionHelper connectionHelper = new ConnectionHelper(null);

					GetInsertInvoicePrintImageParser parser = new GetInsertInvoicePrintImageParser(this, arr.get(i));
					ArrayList<OrderPrintImageDO> list = new ArrayList<OrderPrintImageDO>();
					list.add(arr.get(i));
					connectionHelper.sendRequest_Bulk(this, BuildXMLRequest.uploadInsertInvoicePrintImage(list ), parser, ServiceURLs.InsertInvoicePrintImage);
					boolean isImagesUploaded = parser.getStatus();

//					if (isImagesUploaded)
//						new ReturnOrderDA().updateDamagedImageUploadDetailStatus(parser.getData());

//					new ReturnOrderDA().updateOrderPrintImagesStatus(arr.get(i),arr.get(i).ImagePath);


//					currentTransaction = Transactions.INSERT_INVOICE_PRINT_IMAGE;
//					currentTransactionSatus = TransactionSatus.SUCCESS;
//					updateTransactionStatus();
				}
									currentTransaction = Transactions.INSERT_INVOICE_PRINT_IMAGE;
					currentTransactionSatus = TransactionSatus.SUCCESS;
					updateTransactionStatus();

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			currentTransaction =Transactions.INSERT_INVOICE_PRINT_IMAGE;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}

	private void uploadChequeImages()
	{
		try
		{
			HashMap<String,ArrayList<PostPaymentDetailDONew>> hmImages = new HashMap<String, ArrayList<PostPaymentDetailDONew>>();
			
			hmImages = new PaymentDetailDA().getPaymentChequeImages();
			if(hmImages != null && hmImages.size() > 0)
			{
				currentTransaction =Transactions.CHEQUEIMAGES;
				currentTransactionSatus=TransactionSatus.START;
				updateTransactionStatus();
				
				ConnectionHelper connectionHelper = new ConnectionHelper(null);

				UploadChequeImagesParser uploadChequeImagesParser = new UploadChequeImagesParser(this);
				connectionHelper.sendRequest_Bulk(this, BuildXMLRequest.uploadChequeImages(hmImages),uploadChequeImagesParser, ServiceURLs.PostChequeImages);
				boolean isImagesUploaded = uploadChequeImagesParser.getStatus();
				
				if(isImagesUploaded)
					new PaymentDetailDA().updateImageUploadDetailStatus(hmImages);
				
				currentTransaction =Transactions.CHEQUEIMAGES;
				currentTransactionSatus=TransactionSatus.SUCCESS;
				updateTransactionStatus();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			currentTransaction =Transactions.CHEQUEIMAGES;
			currentTransactionSatus=TransactionSatus.ERROR_EXCEPTION;
			updateTransactionStatus();
		}
	}
}
