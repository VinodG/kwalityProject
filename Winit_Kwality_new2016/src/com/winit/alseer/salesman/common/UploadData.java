package com.winit.alseer.salesman.common;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.winit.alseer.parsers.InsertCustomerParser;
import com.winit.alseer.parsers.InsertLoadParser;
import com.winit.alseer.parsers.InsertOrdersParser;
import com.winit.alseer.parsers.InsertPaymentParser;
import com.winit.alseer.parsers.UploadChequeImagesParser;
import com.winit.alseer.parsers.UploadGrvImagesParser;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.InventoryDA;
import com.winit.alseer.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.alseer.salesman.dataaccesslayer.MyActivitiesDA;
import com.winit.alseer.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.alseer.salesman.dataaccesslayer.ReturnOrderDA;
import com.winit.alseer.salesman.dataaccesslayer.TransferInOutDA;
import com.winit.alseer.salesman.dataaccesslayer.VehicleDA;
import com.winit.alseer.salesman.dataobject.CustomerDao;
import com.winit.alseer.salesman.dataobject.CustomerVisitDO;
import com.winit.alseer.salesman.dataobject.DamageImageDO;
import com.winit.alseer.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.alseer.salesman.dataobject.InventoryDO;
import com.winit.alseer.salesman.dataobject.JouneyStartDO;
import com.winit.alseer.salesman.dataobject.LoadRequestDO;
import com.winit.alseer.salesman.dataobject.MallsDetails;
import com.winit.alseer.salesman.dataobject.MyActivityDO;
import com.winit.alseer.salesman.dataobject.NewCustomerDO;
import com.winit.alseer.salesman.dataobject.PostPaymentDONew;
import com.winit.alseer.salesman.dataobject.PostPaymentDetailDONew;
import com.winit.alseer.salesman.dataobject.PostReasonDO;
import com.winit.alseer.salesman.dataobject.TransferInoutDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.UploadImage;
import com.winit.alseer.salesman.utilities.UploadImages;
import com.winit.alseer.salesman.utilities.UploadTransactions;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class UploadData 
{
	private Preference preference;
	private String strResponse = "";
	private ConnectionExceptionListener connectionExceptionListener;
	private Context context;
	private CommonDA commonDA;
	
	public UploadData(Context context, ConnectionExceptionListener connectionExceptionListener, String date)
	{
		this.context					 = 	context;
		preference 						 = 	new Preference(context);
		this.connectionExceptionListener = 	connectionExceptionListener;
		commonDA = new CommonDA();
		Intent intent=new Intent(context,UploadImages.class);
		context.startService(intent);
		
		Intent uploadTraIntent=new Intent(context,UploadTransactions.class);
		uploadTraIntent.putExtra(AppConstants.DATE_KEY, date);
		context.startService(uploadTraIntent);
	}
	
	/*@Override
	protected String doInBackground(String... params)
	{
		try 
		{
			if(isNetworkConnectionAvailable(context))
			{
				uploadLoadRequest();
				uploadCustomer();
				updateUploadedCustomer();
				uploadOrders();
				uploadCollectedReturnOrders();
				uploadFreeItemOrders();
				uploadAdvanceOrder();
				uploadPayments();
//				uploadStock();
				postJournyLogOnSever(preference.getStringFromPreference(Preference.SALESMANCODE, ""));
				postReturnStock();
				uploadTransferInOut();
				uploadCheckINDemandInventory();
				uploadSkipReason();
				uploadJourneyStart();
				uploadMyActivities();
				uploadCustomerVisit();
				uplaodPasscode();
				uploadInventory();
				uploadCustomerspeialDay();
				uploadGRVImages();
				uploadChequeImages();
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return strResponse;
	}
	@Override
	protected void onPostExecute(String result) 
	{
		LogUtils.infoLog("Service running ", "strResponse = "+strResponse);
		super.onPostExecute(result);
	}*/
	
	public void updateUploadedCustomer()
	{
//		ArrayList<NameIDDo> arrayList = new CommonDA().getUnpostedCustomerId();
//		if(arrayList != null && arrayList.size() > 0)
//			new CommonDA().updateCreatedCustomers(arrayList);
	}
	
	public boolean isNetworkConnectionAvailable(Context context) 
	{
		// checking the Internet availability
		boolean isNetworkConnectionAvailable = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context	.getSystemService("connectivity");
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		if (activeNetworkInfo != null)
		{
			isNetworkConnectionAvailable = activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
		}
		return isNetworkConnectionAvailable;
	}
	
	private void uploadTransferInOut()
	{
		
		try
		{
			
			TransferInOutDA transferInOutDA = new TransferInOutDA();
			Vector<TransferInoutDO> vecTransferInoutDOs = new Vector<TransferInoutDO>();
			vecTransferInoutDOs = transferInOutDA.getUnuploadedTransferData();
			if(vecTransferInoutDOs != null && vecTransferInoutDOs.size() > 0)
			{
				if(new ConnectionHelper(null).sendRequest(context,BuildXMLRequest.PostTransferOuts(vecTransferInoutDOs), ServiceURLs.PostTransferOuts))
					transferInOutDA.updateTransferInOUTStatusNew(vecTransferInoutDOs, "Y");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
	
//	public boolean uploadOrders()
//	{
//		try
//		{
//			
//			final Vector<TrxHeaderDO> vecSalesOrders  	 = 	commonDA.getAllSalesOrderToPost(preference.getStringFromPreference(Preference.EMP_NO, ""));
//			final InsertOrdersParser insertOrdersParser  = new InsertOrdersParser(context);
//			if(vecSalesOrders != null && vecSalesOrders.size() > 0)
//			{
//				////Changed here code
//				ConnectionHelper connectionHelper = new ConnectionHelper(null);
//				connectionHelper.sendRequest_Bulk(context,BuildXMLRequest.postTrxDetailsFromXML(vecSalesOrders, preference.getStringFromPreference(Preference.EMP_NO, "")), insertOrdersParser, ServiceURLs.PostTrxDetailsFromXML);
//			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//        return true;
//	}
	public boolean uploadCollectedReturnOrders()
	{
		try
		{
			final Vector<TrxHeaderDO> vecSalesOrders  	 = 	commonDA.getAllCollectedReturnOrderToPost(preference.getStringFromPreference(Preference.EMP_NO, ""));
			final InsertOrdersParser insertOrdersParser  = new InsertOrdersParser(context);
			if(vecSalesOrders != null && vecSalesOrders.size() > 0)
			{
				////Changed here code
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				connectionHelper.sendRequest_Bulk(context,BuildXMLRequest.sendAllOrders(vecSalesOrders, preference.getStringFromPreference(Preference.EMP_NO, "")), insertOrdersParser, ServiceURLs.PostTrxDetails);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        return true;
	}
	private boolean uploadAdvanceOrder()
	{
		
		try
		{
			
			/*List<AdvanceOrderDO> vecSalesOrders 	= 	new Vector<AdvanceOrderDO>();
			vecSalesOrders  						= 	commonDA.getAllAdvanceOrderToPost(preference.getStringFromPreference(Preference.EMP_NO, ""));
			
			if(vecSalesOrders.size() > 5)
			{
				vecSalesOrders = vecSalesOrders.subList(0, 5);
			}
			final InsertOrdersParserAdvance insertOrdersParser 	= new InsertOrdersParserAdvance(context);
			if(vecSalesOrders != null && vecSalesOrders.size() > 0)
			{
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				connectionHelper.sendRequest_Bulk(context,BuildXMLRequest.UpdateDeliveryStatus(vecSalesOrders, preference.getStringFromPreference(Preference.EMP_NO, "")), insertOrdersParser, ServiceURLs.UpdateDeliveryStatus);
			}*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        return true;
	}
	private boolean uploadFreeItemOrders()
	{
		try
		{
			
			/*Vector<OrderDO> vecSalesOrders 	= 	new Vector<OrderDO>();
			vecSalesOrders  				= 	commonDA.getAllFreeDeliveryOrderToPost(preference.getStringFromPreference(Preference.EMP_NO, ""));
			final InsertOrdersParser insertOrdersParser 	= new InsertOrdersParser(context);
			if(vecSalesOrders != null && vecSalesOrders.size() > 0)
			{
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				connectionHelper.sendRequest_Bulk(context,BuildXMLRequest.sendFreeDeliveryOrders(vecSalesOrders, preference.getStringFromPreference(Preference.EMP_NO, "")), insertOrdersParser, ServiceURLs.PostTrxDetails);
			}*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        return true;
	}
	
	public boolean uploadPayments()
	{
		
		boolean isPaymentsUploaded = false;
		try
		{
			Vector<PostPaymentDONew> vecPayments =   new Vector<PostPaymentDONew>();
			vecPayments						  =   commonDA.getAllPaymentsToPostNew(preference.getStringFromPreference(Preference.SALESMANCODE, ""), CalendarUtils.getOrderPostDate(), preference.getStringFromPreference(Preference.USER_ID, ""));
			ConnectionHelper connectionHelper = new ConnectionHelper(null);
			if(vecPayments != null && vecPayments.size() > 0)
			{
				InsertPaymentParser insertPaymentParser	= new InsertPaymentParser(context);
				connectionHelper.sendRequest_Bulk(context,BuildXMLRequest.postPayments(vecPayments),insertPaymentParser, ServiceURLs.PostPaymentDetailsFromXML);
				isPaymentsUploaded = insertPaymentParser.getStatus();
				
				if(isPaymentsUploaded)
					commonDA.updatePaymentStatus(vecPayments);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        return isPaymentsUploaded;
	}
	
	public boolean uploadCustomer()
	{
		try
		{
			Vector<NewCustomerDO> vector = new CommonDA().getNewCustomerToUpload();
			final InsertCustomerParser insertCustomerParser = new InsertCustomerParser(context);
			final ConnectionHelper connectionHelper 		= new ConnectionHelper(null);
			if(vector !=null && vector.size() > 0)
			{
				String route_code = preference.getStringFromPreference(Preference.ROUTE_CODE, "");
				connectionHelper.sendRequest_Bulk(context,BuildXMLRequest.insertHHCustomer(vector, route_code), insertCustomerParser, ServiceURLs.INSERTHH_CUSTOMER_OFFLINE);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        return true;
	}
	
	
	public boolean uploadStock()
	{
		try
		{
			ArrayList<DeliveryAgentOrderDetailDco> arrayList = new VehicleDA().getAllItemToUpload(CalendarUtils.getOrderPostDate());
			final ConnectionHelper connectionHelper 		= new ConnectionHelper(null);
			if(arrayList !=null && arrayList.size() > 0)
			{
				boolean isUploaded = connectionHelper.sendRequest(context,BuildXMLRequest.insertStock(preference.getStringFromPreference(Preference.EMP_NO, ""),arrayList), ServiceURLs.InsertStock);
				if(isUploaded)
					new VehicleDA().updateVMInventoryFromService();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        return true;
	}
	
	public void postJournyLogOnSever(final String salesmanCode)
	{
		try
		{
			ArrayList<MallsDetails> vecJournyLog  =		new CustomerDetailsDA().getJournyLog(salesmanCode,CalendarUtils.getOrderPostDate());
			if(vecJournyLog != null && vecJournyLog.size() > 0)
			{
				final ConnectionHelper connectionHelper = new ConnectionHelper(null);
				boolean isSubmitted = connectionHelper.sendRequest(context,BuildXMLRequest.insertCustomerVisit(vecJournyLog,preference.getStringFromPreference(Preference.EMP_NO, "")), ServiceURLs.InsertCustomerVisit);
				if(isSubmitted)
					new CustomerDetailsDA().updateJourneyLogStatus();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void postReturnStock()
	{
		try
		{
			ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct = new VehicleDA().getAllItemToReturn();
			
			if(vecOrdProduct != null && vecOrdProduct.size() > 0)
			{
				final String strEmpNo = preference.getStringFromPreference(Preference.EMP_NO,"");
				final boolean isUpdateReturnStock = new ConnectionHelper(null).sendRequest(context,BuildXMLRequest.updateReturnStock(vecOrdProduct, strEmpNo), ServiceURLs.UpdateReturnStock);
				if(isUpdateReturnStock)
					new VehicleDA().updateReturnstockPostStatus(vecOrdProduct);
			}
		}
		catch(Exception e)
		{
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
				InsertLoadParser insertLoadParser = new InsertLoadParser(context);
				new ConnectionHelper(null).sendRequest(context,BuildXMLRequest.uploadLoadRequests(vecLoad), insertLoadParser, ServiceURLs.PostMovementDetailsFromXML);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void uploadSkipReason()
	{
		
		try
		{
			ArrayList<PostReasonDO> vecArrayList = new CommonDA().getSkipReasonsToPost();
			
			if(vecArrayList != null && vecArrayList.size() > 0)
			{
				if(new ConnectionHelper(null).sendRequest(context,BuildXMLRequest.postReasons(vecArrayList), ServiceURLs.InsertSkippingReason))
				{
//					new CommonDA().updateSkipReasonNew(vecArrayList, CalendarUtils.getCurrentDateAsString());
					new CommonDA().updateSkipReasonNew(vecArrayList, CalendarUtils.getOrderPostDate());
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void uploadJourneyStart()
	{
		
		try
		{
			final Vector<JouneyStartDO> vecJourneyStart = new JourneyPlanDA().getJourneyStart();
			
			boolean isSuccess = true, isSuccessDriver = true;
			for(JouneyStartDO journey : vecJourneyStart)
			{ 
				if(journey.StoreKeeperSignatureStartDay != null) 
				{
					if(new File(journey.StoreKeeperSignatureStartDay).exists())
					{
						String server_path = new UploadImage().uploadImage(context, journey.StoreKeeperSignatureStartDay, ServiceURLs.stockverifiedsignature, true);
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
						String server_path = new UploadImage().uploadImage(context, journey.StoreKeeperSignatureEndDay, ServiceURLs.stockverifiedsignature, true);
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
						String server_path = new UploadImage().uploadImage(context, journey.SalesmanSignatureStartDay, ServiceURLs.stockverifiedsignature, true);
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
						String server_path = new UploadImage().uploadImage(context, journey.SalesmanSignatureEndDay, ServiceURLs.stockverifiedsignature, true);
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
				if(new ConnectionHelper(null).sendRequest(context,BuildXMLRequest.getStartJournyStart(vecJourneyStart), ServiceURLs.PostJourneyDetails))
				{
					for(JouneyStartDO journey : vecJourneyStart)
						new JourneyPlanDA().updateJourneyStartUploadStatus(true, journey.journeyAppId); 
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void uploadCustomerVisit()
	{
		
		try
		{
			Vector<CustomerVisitDO> vecCusotmerVisit = new CustomerDA().getCustomerVisit();
			if(vecCusotmerVisit.size()>0 && new ConnectionHelper(null).sendRequest(context,BuildXMLRequest.getCustomerVisitXML(vecCusotmerVisit), ServiceURLs.PostClientVisits))
			{
				for(CustomerVisitDO journey : vecCusotmerVisit)
					new CustomerDA().updateCustomerVisitUploadStatus(true, journey.CustomerVisitAppId); 
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void uploadMyActivities()
	{ 
//		 Commented part is previous static survey..we are not using that now..so that part is commented.
		
		try
		{
			Vector<MyActivityDO> vecMyActivityDOs = new MyActivitiesDA().getAllUnUploadedActivities();
//		Vector<CustomerSurveyDO> vecCustomerSurveyDOs = new MyActivitiesDA().getUnUploadedServey();
			
			if((vecMyActivityDOs != null && vecMyActivityDOs.size() > 0) /*|| (vecCustomerSurveyDOs != null && vecCustomerSurveyDOs.size() > 0)*/) 
			{
				final ConnectionHelper connectionHelper 		= new ConnectionHelper(null);
				boolean isPost = connectionHelper.sendRequest(context,BuildXMLRequest.postTask(vecMyActivityDOs/*, vecCustomerSurveyDOs*/), ServiceURLs.InsertTaskOrder);
				
				if(isPost)
				{
					new MyActivitiesDA().updateActivities(vecMyActivityDOs);
					Log.e("Posted to server", "true");
//				new MyActivitiesDA().updateCustomerServey(vecCustomerSurveyDOs);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private void uplaodPasscode()
	{
		
		try
		{
			String passcode = new CommonDA().getUsedPasscode();
			if(passcode != null && passcode.length() > 0)
			{
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				boolean isUpdated = connectionHelper.sendRequest(context, BuildXMLRequest.updatePasscodeStatus(preference.getStringFromPreference(Preference.EMP_NO, ""), passcode),ServiceURLs.UPDATE_PASSCODE_STATUS);
				if(isUpdated)
					new CommonDA().deletePasscode(passcode);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean uploadInventory()
	{
		
		try
		{
			Vector<InventoryDO> vecInventory 	= 	new Vector<InventoryDO>();
			CommonDA commonDA 					= 	new CommonDA();
			vecInventory  						= 	commonDA.getAllStoreCheck(preference.getStringFromPreference(Preference.EMP_NO, ""));
			
			if(vecInventory != null && vecInventory.size() > 0)
			{
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				boolean isUploaded = connectionHelper.sendRequest(context, BuildXMLRequest.sendAllStoreCheck(vecInventory), ServiceURLs.PostStorecheckDetails);
				if(isUploaded)
					commonDA.updateInventory(vecInventory);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        return true;
	}
	
	/**
	 * 
	 */
	private boolean uploadCustomerspeialDay() 
	{
		
		try
		{
			Vector<CustomerDao> vecCustomerDao = new CustomerDA().getCustomerDao();
			if(vecCustomerDao != null && vecCustomerDao.size() > 0)
			{
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				boolean isUploaded = connectionHelper.sendRequest(context, BuildXMLRequest.updateCustomerDetails(vecCustomerDao), ServiceURLs.UpdateCustomerDetails);
				if(isUploaded)
					new CustomerDA().updateCustomerDao(vecCustomerDao);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
				ConnectionHelper connectionHelper = new ConnectionHelper(null);

				UploadGrvImagesParser uploadGrvImagesParser = new UploadGrvImagesParser(context);
				connectionHelper.sendRequest_Bulk(context, BuildXMLRequest.uploadGRVImages(hmImages),uploadGrvImagesParser, ServiceURLs.PostGRVImages);
				boolean isImagesUploaded = uploadGrvImagesParser.getStatus();
				
				if(isImagesUploaded)
					new ReturnOrderDA().updateDamagedImageUploadDetailStatus(uploadGrvImagesParser.getData());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
				ConnectionHelper connectionHelper = new ConnectionHelper(null);

				UploadChequeImagesParser uploadChequeImagesParser = new UploadChequeImagesParser(context);
				connectionHelper.sendRequest_Bulk(context, BuildXMLRequest.uploadChequeImages(hmImages),uploadChequeImagesParser, ServiceURLs.PostChequeImages);
				boolean isImagesUploaded = uploadChequeImagesParser.getStatus();
				
				if(isImagesUploaded)
					new PaymentDetailDA().updateImageUploadDetailStatus(hmImages);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
