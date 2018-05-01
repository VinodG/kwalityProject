package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.Vector;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilderProduct;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.ARCollectionDA;
import com.winit.alseer.salesman.dataaccesslayer.AgencyDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.InitiativesDA;
import com.winit.alseer.salesman.dataaccesslayer.StaticSurveyDL;
import com.winit.alseer.salesman.dataaccesslayer.StatusDA;
import com.winit.alseer.salesman.dataobject.AgencyNewDo;
import com.winit.alseer.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.PendingInvicesDO;
import com.winit.alseer.salesman.dataobject.StatusDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;

public class CheckInOptionActivity extends BaseActivity
{
	private LinearLayout llCheckINOption,llCreditValues,llCreditLimitVal,llOutStandingVal,llAvailableLimitVal;
	private RelativeLayout ll_CustomerInfoNew,ll_storecheck,ll_salesorder,ll_collection,ll_returnorder,ll_tasks,
					ll_Savedorder,ll_CustomerInfo,ll_CustomerStatement,ll_CheckOut,
					ll_BeforeService,ll_AfterService,ll_FOC;
	private JourneyPlanDO mallsDetailsScreen ;
	private TextView tvCreditLimit,tvCreditLimitVal,tvAvailLimt,tvAvailLimtVal,tvCustomerCode,tvOutstandingAmount,tvOutstandingAmountval;
	private TextView tvHeadTitle,tvCustomerAddressinoptions, tvTask,tvAssets,tvReturnOrder,tvFOC,
					tvCollection,tvSalesOrder,tvStoreCheck,tvSavedOrder,tvCustomerInfo,tvBeforeService,tvAfterService,tvCustomerStatement;
	private LinearLayout btnTakeNewOrder, btnOrderList, btnPendinInvoices, btnAssetScan,llReturnOrder, btnReplacements, llStoreCheck;
	private ImageView ivSalesOrder,ivReturnOrder,ivAssets,ivTask,img_storecheckcomplete,
					img_salesordercomplete,img_collectioncomplete,img_returnordercomplete,img_assetescomplete,img_taskscomplete,
					sepTakeNewOrder,sepReturnNewOrder,sepOrderlist,img_FOCcomplete,ivFOC,ivstorecheck,
					seppendingInvoice,sepAssetScan,img_Savedordercomplete,img_CustomerInfocomplete,ivSavedorder,ivCustomerInfo,
					img_beforeService,img_afterService,ivBeforeSAervice,ivAfterService,ivcollectioncomplete,ivCustomerStatement;
	private Vector<String> vecstatusDO;
	
	private Vector<AgencyNewDo> vecagn= null;
	AgencyNewDo AgnCustom;
	private boolean isSalesOrderBlocked=false;
	BroadcastReceiver refreshCheckInOption =new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			LogUtils.debug("refreshListCheckInOption", "updateData");
			onResume();
		}
	};

	boolean isForceCheckIn= false;
	boolean isVanSaleUser = false;
	
	@Override
	public void initialize()
	{
		ivDivider.setVisibility(View.VISIBLE);
		btnCartCount.setVisibility(View.VISIBLE);
		ivShoppingCart.setVisibility(View.VISIBLE);
		btnCartCount.bringToFront();
		
		if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
		{
			llCheckINOption = (LinearLayout) inflater.inflate(R.layout.check_in_preseller, null);
			isVanSaleUser=false;
		}
		else
		{
			llCheckINOption = (LinearLayout) inflater.inflate(R.layout.check_in, null);
			isVanSaleUser=true;
		}
		llBody.addView(llCheckINOption, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		AppConstants.isTaskCompleted = false;
		AppConstants.isServeyCompleted = false;
		if(getIntent().getExtras() != null)
		{
			mallsDetailsScreen = (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			isForceCheckIn = (boolean) getIntent().getExtras().getBoolean("isForceCheckIn");
		}
			
		initializeControles(); 
		tvCustomerCode.setVisibility(View.GONE);
//		tvCustomerCode.setText("Customer Code : "+mallsDetails.site);
//		tvHeadTitle.setText(mallsDetails.siteName  + " ("+mallsDetails.partyName+")");
//		tvHeadTitle.setText(mallsDetails.siteName+" ["+mallsDetails.site+"]");
		if(mallsDetailsScreen == null)
			mallsDetailsScreen = mallsDetailss;
		else
			mallsDetailss = mallsDetailsScreen;
		
		if(mallsDetailsScreen != null)
			tvCustomerAddressinoptions.setText(getAddress(mallsDetailsScreen));
		
		ll_storecheck.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(CheckInOptionActivity.this, CaptureInventoryActivity.class);
				intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
				intent.putExtra("mallsDetails",mallsDetailsScreen);
				intent.putExtra("from", "checkin");
				intent.putExtra("checkType", AppConstants.Capture_Inventory);
				intent.putExtra("isDone", vecstatusDO.contains(AppConstants.Type_StoreCheck));
				startActivity(intent);
			}
		});
		ll_BeforeService.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				//camera
				captureImage(AppConstants.CAMERA_PIC_BEFORE_SERVICE);
			}
		});
		ll_AfterService.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				captureImage(AppConstants.CAMERA_PIC_AFTER_SERVICE);
			}
		});
		ll_salesorder.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{

				if(isSalesOrderBlocked)
				{
					showCustomDialog(CheckInOptionActivity.this,"Alert!","Credit Limit Exceed, Collect Outstanding Payments","Ok","","");
				}else{
					CustomBuilderProduct builder = new CustomBuilderProduct(CheckInOptionActivity.this, "Please Select\nRequest Type", true);
					builder.setSingleChoiceItems(vecagn, null,new CustomBuilderProduct.OnClickListener()
					{

						@Override
						public void onClick(CustomBuilderProduct builder, Object selectedObject) {
							if(selectedObject!=null)
								AgnCustom = (AgencyNewDo) selectedObject;
							builder.dismiss();

//			    		showOrderTypesDialog();
							Intent intent = new Intent(CheckInOptionActivity.this, SalesManRecommendedOrder.class);
							intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
							intent.putExtra("mallsDetails",mallsDetailsScreen);
							intent.putExtra("from", "checkin");
							intent.putExtra("Invoice_Type", AppConstants.Invoice_Type_Credit);

							if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
								intent.putExtra("TRX_TYPE", TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER());
							else
								intent.putExtra("TRX_TYPE", TrxHeaderDO.get_TRXTYPE_SALES_ORDER());
							intent.putExtra("TRX_SUB_TYPE", TrxHeaderDO.get_TRX_SUBTYPE_SALES_ORDER());

							if(AgnCustom!=null)
								intent.putExtra(AppConstants.DIVISION, AgnCustom.Priority);
							startActivity(intent);

						}
					});

					builder.show();

				}



			
			}
		});
		ll_FOC.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				CustomBuilderProduct builder = new CustomBuilderProduct(CheckInOptionActivity.this, "Please Select\nRequest Type", true);
				builder.setSingleChoiceItems(vecagn, null,new CustomBuilderProduct.OnClickListener() {
					
					@Override
					public void onClick(CustomBuilderProduct builder, Object selectedObject) {
						if(selectedObject!=null)
						AgnCustom = (AgencyNewDo) selectedObject;
						builder.dismiss();
			    		
			    		Intent intent = new Intent(CheckInOptionActivity.this, SalesManRecommendedOrder.class);
						intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
						intent.putExtra("mallsDetails",mallsDetailsScreen);
						intent.putExtra("from", "checkin");
						if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
							intent.putExtra("TRX_TYPE", TrxHeaderDO.get_TYPE_FREE_ORDER());
						else
							intent.putExtra("TRX_TYPE", TrxHeaderDO.get_TYPE_FREE_DELIVERY());
						intent.putExtra("TRX_SUB_TYPE", TrxHeaderDO.get_TRX_SUBTYPE_SALES_ORDER());

						if(AgnCustom!=null)
							intent.putExtra(AppConstants.DIVISION, AgnCustom.Priority);
						startActivity(intent);
					}
				});
				builder.show();							
			}
		});
		
		ll_collection.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				
				CustomBuilderProduct builder = new CustomBuilderProduct(CheckInOptionActivity.this, "Please Select\nRequest Type", true);
				builder.setSingleChoiceItems(vecagn, null,new CustomBuilderProduct.OnClickListener() {
					
					@Override
					public void onClick(CustomBuilderProduct builder, Object selectedObject) {
						
						AgnCustom = (AgencyNewDo) selectedObject;
						builder.dismiss();
			    		
						Intent intent = new Intent(CheckInOptionActivity.this, PendingInvoices.class);
						intent.putExtra("arcollection", true);
						intent.putExtra("mallsDetails", mallsDetailsScreen);
						intent.putExtra("fromMenu", true);
						intent.putExtra("AR", true);
						
						if(AgnCustom!=null)
							intent.putExtra(AppConstants.DIVISION, AgnCustom.Priority);
						startActivity(intent);

					}
				});

				builder.show();	
		}
		});
		
		ll_returnorder.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				
				CustomBuilderProduct builder = new CustomBuilderProduct(CheckInOptionActivity.this, "Please Select\nRequest Type", true);
				builder.setSingleChoiceItems(vecagn, null,new CustomBuilderProduct.OnClickListener() {
					
					@Override
					public void onClick(CustomBuilderProduct builder, Object selectedObject) {
						if(selectedObject!=null)
						AgnCustom = (AgencyNewDo) selectedObject;
						builder.dismiss();
			    		
						if(AgnCustom!=null)
							showOrderTypesDialog(AgnCustom);
						else
							showOrderTypesDialog(null);
						
					}
				});
				builder.show();
				
				
				
//				Intent intent = null;
//				intent  =	new Intent(CheckInOptionActivity.this,SalesmanReturnOrder.class);
//				intent.putExtra("mallsDetails", mallsDetailsScreen);
//				intent.putExtra("from", "checkINOption");
//				startActivity(intent);
			}
		});
		
		ll_tasks.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				showTasksDialog();
			}
		});
		
		ll_CustomerStatement.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Intent intent =	new Intent(CheckInOptionActivity.this,  CustomerStatementActivity.class);
				intent.putExtra("mallsDetails", mallsDetailsScreen);
				startActivity(intent);
			}
		});
		
		// for Payment Summary Report
		ll_CustomerInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Intent intent =	new Intent(CheckInOptionActivity.this,  CustomerPaymentSummaryActivity.class);
				intent.putExtra("mallsDetails", mallsDetailsScreen);
//				intent.putExtra("fromCustomerInfo", true);
				startActivity(intent);
				
			}
		});
		
//		if(!preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
//		{
			ll_Savedorder.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) 
				{
					//saved order summary
					Intent intent = new Intent(CheckInOptionActivity.this, CustomerOrderSummary.class);
					intent.putExtra("mallsDetails",mallsDetailsScreen);
					startActivity(intent);
				}
			});
			
			ll_CustomerInfoNew.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) 
				{
					Intent intent =	new Intent(CheckInOptionActivity.this,  SalesmanCheckIn.class);
					intent.putExtra("mallsDetails", mallsDetailsScreen);
					intent.putExtra("fromCustomerInfo", true);
					startActivity(intent);
					
				}
			});
			
			ll_CheckOut.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) 
				{
					showCustomDialog(CheckInOptionActivity.this, getString(R.string.warning), "Do you want to check out?", "Yes", "No", "checkout");
				}
			});
//		}
		
		llReturnOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = null;
				intent  =	new Intent(CheckInOptionActivity.this,  SalesManTakeReturnOrder.class);
				intent.putExtra("mallsDetails", mallsDetailsScreen);
				intent.putExtra("from", "checkINOption");
				startActivity(intent);
			}
		});
		
		btnTakeNewOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(CheckInOptionActivity.this, SalesManTakeOrder.class);
				intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
				intent.putExtra("mallsDetails",mallsDetailsScreen);
				intent.putExtra("from", "checkin");
				startActivity(intent);
			}
		});
		
		btnReplacements.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent =	new Intent(CheckInOptionActivity.this,  SalesManTakeReturnOrder.class);
				intent.putExtra("mallsDetails", mallsDetailsScreen);
				intent.putExtra("from", "replacement");
				startActivity(intent);
			}
		});
		
		setTypeFaceRobotoNormal(llCheckINOption);
		tvHeadTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		setTypeFaceRobotoBold(llCreditLimitVal);
		setTypeFaceRobotoBold(llOutStandingVal);
		setTypeFaceRobotoBold(llAvailableLimitVal);
		
	}
	
	private void initializeControles()
	{
		tvHeadTitle					= (TextView) llCheckINOption.findViewById(R.id.tvHeadTitle);
		tvCustomerAddressinoptions  = (TextView) llCheckINOption.findViewById(R.id.tvCustomerAddressinoptions);
		llCreditValues 				= (LinearLayout) llCheckINOption.findViewById(R.id.llCreditValues);
		llCreditLimitVal			= (LinearLayout) llCheckINOption.findViewById(R.id.llCreditLimitVal);;
		llOutStandingVal			= (LinearLayout) llCheckINOption.findViewById(R.id.llOutStandingVal);;
		llAvailableLimitVal			= (LinearLayout) llCheckINOption.findViewById(R.id.llAvailableLimitVal);
		tvCreditLimit				= (TextView) llCheckINOption.findViewById(R.id.tvCreditLimit);
		tvCreditLimitVal  			= (TextView) llCheckINOption.findViewById(R.id.tvCreditLimitVal);
		tvAvailLimt					= (TextView) llCheckINOption.findViewById(R.id.tvAvailLimt);
		tvAvailLimtVal  			= (TextView) llCheckINOption.findViewById(R.id.tvAvailLimtVal);
		tvCustomerCode  			= (TextView) llCheckINOption.findViewById(R.id.tvCustomerCode);
		tvOutstandingAmount			= (TextView) llCheckINOption.findViewById(R.id.tvOutstandingAmount);
		tvOutstandingAmountval  	= (TextView) llCheckINOption.findViewById(R.id.tvOutstandingAmountval);
		
		btnTakeNewOrder				= (LinearLayout) llCheckINOption.findViewById(R.id.btnTakeNewOrder);
		btnOrderList				= (LinearLayout) llCheckINOption.findViewById(R.id.btnOrderList);
		btnPendinInvoices			= (LinearLayout) llCheckINOption.findViewById(R.id.btnPendinInvoices);
		btnAssetScan				= (LinearLayout) llCheckINOption.findViewById(R.id.btnAssetScan);
		llReturnOrder				= (LinearLayout) llCheckINOption.findViewById(R.id.llReturnOrder);
		ll_storecheck				= (RelativeLayout) llCheckINOption.findViewById(R.id.ll_storecheck);
		ll_BeforeService			= (RelativeLayout) llCheckINOption.findViewById(R.id.ll_BeforeService);
		ll_AfterService				= (RelativeLayout) llCheckINOption.findViewById(R.id.ll_AfterService);
		ll_salesorder				= (RelativeLayout) llCheckINOption.findViewById(R.id.ll_salesorder);
		ll_collection				= (RelativeLayout) llCheckINOption.findViewById(R.id.ll_collection);
		ll_returnorder				= (RelativeLayout) llCheckINOption.findViewById(R.id.ll_returnorder);
		ll_tasks		    		= (RelativeLayout) llCheckINOption.findViewById(R.id.ll_tasks);
		ll_CustomerStatement        = (RelativeLayout) llCheckINOption.findViewById(R.id.ll_CustomerStatement);
		ll_FOC				        = (RelativeLayout) llCheckINOption.findViewById(R.id.ll_FOC);
		
		ivSalesOrder				= (ImageView) llCheckINOption.findViewById(R.id.ivSalesOrder);
		ivReturnOrder				= (ImageView) llCheckINOption.findViewById(R.id.ivReturnOrder);
		ivTask						= (ImageView) llCheckINOption.findViewById(R.id.ivTask);

		img_storecheckcomplete		= (ImageView) llCheckINOption.findViewById(R.id.img_storecheckcomplete);
		img_salesordercomplete		= (ImageView) llCheckINOption.findViewById(R.id.img_salesordercomplete);
		img_collectioncomplete		= (ImageView) llCheckINOption.findViewById(R.id.img_collectioncomplete);
		img_returnordercomplete		= (ImageView) llCheckINOption.findViewById(R.id.img_returnordercomplete);
		img_taskscomplete			= (ImageView) llCheckINOption.findViewById(R.id.img_taskscomplete);
		img_beforeService			= (ImageView) llCheckINOption.findViewById(R.id.img_beforeService);
		img_afterService			= (ImageView) llCheckINOption.findViewById(R.id.img_afterService);
		ivcollectioncomplete		= (ImageView) llCheckINOption.findViewById(R.id.ivcollectioncomplete);//
		ivCustomerStatement			= (ImageView) llCheckINOption.findViewById(R.id.ivCustomerStatement);
		ivAfterService				= (ImageView) llCheckINOption.findViewById(R.id.ivAfterService);
		ivBeforeSAervice			= (ImageView) llCheckINOption.findViewById(R.id.ivBeforeSAervice);//
		img_FOCcomplete				= (ImageView) llCheckINOption.findViewById(R.id.img_FOCcomplete);
		ivFOC						= (ImageView) llCheckINOption.findViewById(R.id.ivFOC);
		ivstorecheck				= (ImageView) llCheckINOption.findViewById(R.id.ivstorecheck);
		
		btnReplacements				= (LinearLayout) llCheckINOption.findViewById(R.id.btnReplacements);
		
		tvTask					= (TextView) llCheckINOption.findViewById(R.id.tvTask);
		tvReturnOrder  			= (TextView) llCheckINOption.findViewById(R.id.tvReturnOrder);
		tvCollection  			= (TextView) llCheckINOption.findViewById(R.id.tvCollection);
		tvSalesOrder  			= (TextView) llCheckINOption.findViewById(R.id.tvSalesOrder);
		tvStoreCheck  			= (TextView) llCheckINOption.findViewById(R.id.tvStoreCheck);
		tvSavedOrder  			= (TextView) llCheckINOption.findViewById(R.id.tvSavedOrder);//
		tvCustomerInfo  		= (TextView) llCheckINOption.findViewById(R.id.tvCustomerInfo);
		tvCustomerStatement		= (TextView) llCheckINOption.findViewById(R.id.tvCustomerStatement);
		tvAfterService			= (TextView) llCheckINOption.findViewById(R.id.tvAfterService);
		tvBeforeService			= (TextView) llCheckINOption.findViewById(R.id.tvBeforeService);
		tvFOC					= (TextView) llCheckINOption.findViewById(R.id.tvFOC);
		
		img_Savedordercomplete		= (ImageView) llCheckINOption.findViewById(R.id.img_Savedordercomplete);//
		img_CustomerInfocomplete	= (ImageView) llCheckINOption.findViewById(R.id.img_CustomerInfocomplete);
		
		ivSavedorder				= (ImageView) llCheckINOption.findViewById(R.id.ivSavedorder);//
		ivCustomerInfo		   		 	= (ImageView) llCheckINOption.findViewById(R.id.ivCustomerInfo);
		
		ll_Savedorder    			= (RelativeLayout) llCheckINOption.findViewById(R.id.ll_Savedorder);//
		ll_CustomerInfo    			= (RelativeLayout) llCheckINOption.findViewById(R.id.ll_CustomerInfo); // used for payment summary report
		ll_CheckOut                 = (RelativeLayout) llCheckINOption.findViewById(R.id.ll_CheckOut);

		// preseller
//		if(!preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
		{
			ll_CustomerInfoNew		= (RelativeLayout) llCheckINOption.findViewById(R.id.ll_CustomerInfoNew);// used for CustomerInfo

			img_assetescomplete			= (ImageView) llCheckINOption.findViewById(R.id.img_assetescomplete);

			ivAssets					= (ImageView) llCheckINOption.findViewById(R.id.ivAssets);
			
			tvAssets  			= (TextView) llCheckINOption.findViewById(R.id.tvAssets);
		}
		
	}
	protected void checkBlockedCustomer()
	{
		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable() 
		{
			private CustomerCreditLimitDo creditLimit;
			private float overdue;
			ArrayList<PendingInvicesDO> vecPendingInvoices;
			@Override
			public void run() 
			{
				creditLimit = new CustomerDA().getCustomerCreditLimit(mallsDetailsScreen.site);
				overdue = new CustomerDA().getOverdueAmount(mallsDetailsScreen.site);
				if(StringUtils.getFloat(creditLimit.availbleLimit)<=0||overdue >0)
					 vecPendingInvoices = new ARCollectionDA().getListPendingInvoices(mallsDetailsScreen.site);
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						hideLoader();
						tvCreditLimitVal.setText(""+amountFormate.format(StringUtils.getFloat(creditLimit.creditLimit))+"");
						tvAvailLimtVal.setText(""+amountFormate.format(StringUtils.getFloat(creditLimit.availbleLimit))+"");
						tvOutstandingAmountval.setText(""+amountFormate.format(StringUtils.getFloat(creditLimit.outStandingAmount))+"");
//						tvOverDueAmountVal.setText("AED "+decimalFormat.format(overdue));
						if(vecPendingInvoices!=null && vecPendingInvoices.size()>0) {
							showPendingInvoicePopup(vecPendingInvoices);
							isSalesOrderBlocked=true;
						}else
							isSalesOrderBlocked=false;
					}
				});
				
			}
		}).start();
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AppConstants.ACTION_REFRESH_CHECKIN_OPTION);
		registerReceiver(refreshCheckInOption, intentFilter);
		
		AppConstants.isServeyCompleted = false;
		new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				vecagn= new AgencyDA().getAgencyDetail();
				vecstatusDO = new StatusDA().getCompletedOptionsStatus(mallsDetailsScreen.site,AppConstants.Action_CheckIn);
				runOnUiThread(new  Runnable() 
				{
					public void run() 
					{
						updateData();
					}
				});
			}
		}).start();
		
		if(pendingInvoicesPopup!=null&&pendingInvoicesPopup.isShowing())
			pendingInvoicesPopup.dismiss();
		if(mallsDetailsScreen.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
		{
			llCreditValues.setVisibility(View.VISIBLE);
			checkBlockedCustomer();
		}
		else
		{
			llCreditValues.setVisibility(View.GONE);
		}
//		checkBlockedCustomer();
		
	}
	@Override
	protected void onDestroy() 
	{
		unregisterReceiver(refreshCheckInOption);
		super.onDestroy();
	};
	PopupWindow pendingInvoicesPopup;
	public void showPendingInvoicePopup(ArrayList<PendingInvicesDO> vecPendingInvoices)
	{
		pendingInvoicesPopup = new PopupWindow(CheckInOptionActivity.this);
		View pendingInvoices 		= inflater.inflate(R.layout.pending_invoices_popup, null);
		pendingInvoicesPopup = new PopupWindow(pendingInvoices,LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, false);
		pendingInvoicesPopup.setOutsideTouchable(false);

		ListView lvpendingInvoices 	= (ListView) pendingInvoices.findViewById(R.id.lvPendingInvoices);
		Button btnCheckOut 			= (Button) pendingInvoices.findViewById(R.id.btnNoPopup);
		Button btnOk 				= (Button) pendingInvoices.findViewById(R.id.btnYesPopup);
		lvpendingInvoices.setAdapter(new PendingInvoiceAdapter(vecPendingInvoices));
		
		btnOk.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				
//				if(vecstatusDO.contains(AppConstants.Type_StoreCheck) && vecstatusDO.contains(AppConstants.Type_BeforService))
//				{
//					CustomBuilderProduct builder = new CustomBuilderProduct(CheckInOptionActivity.this, "Please Select\nRequest Type", false);
//					builder.setSingleChoiceItems(vecagn, null,new CustomBuilderProduct.OnClickListener() {
//
//						@Override
//						public void onClick(CustomBuilderProduct builder, Object selectedObject) {
//
//							AgnCustom = (AgencyNewDo) selectedObject;
//							builder.dismiss();
//
//							Intent intent = new Intent(CheckInOptionActivity.this, PendingInvoices.class);
//							intent.putExtra("arcollection", true);
//							intent.putExtra("mallsDetails", mallsDetailsScreen);
//							intent.putExtra("fromMenu", true);
//							intent.putExtra("AR", true);
//
//							if(AgnCustom!=null)
//								intent.putExtra(AppConstants.DIVISION, AgnCustom.Priority);
//							startActivity(intent);
//
//						}
//					});
//
//					builder.show();
//				}
					
					
				/*Intent intent = new Intent(CheckInOptionActivity.this, PendingInvoices.class);
				intent.putExtra("arcollection", true);
				intent.putExtra("mallsDetails", mallsDetailsScreen);
				intent.putExtra("fromMenu", false);
				intent.putExtra("isExceed", true);
				intent.putExtra("AR", true);
				startActivityForResult(intent, 1000);*/
				pendingInvoicesPopup.dismiss();
			}
		});
		btnCheckOut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pendingInvoicesPopup.dismiss();
				
				showpop();
				
//				performCheckouts(true);
				
			}

			
		});
		
		pendingInvoicesPopup.showAtLocation(llCheckINOption, Gravity.CENTER, 0, 0);

	}
	private void showpop() {
		showCustomDialog(this, getString(R.string.warning), "Do you want to check out?", "Yes", "No", "checkout", false);
		
	}
	public class PendingInvoiceAdapter extends BaseAdapter
	{
		private ArrayList<PendingInvicesDO> vctPendingInvicesDOs;
		public PendingInvoiceAdapter(ArrayList<PendingInvicesDO> vecPendingInvicesDOs) 
		{
			vctPendingInvicesDOs = vecPendingInvicesDOs;
		}

		@Override
		public int getCount() 
		{
			if(vctPendingInvicesDOs != null && vctPendingInvicesDOs.size() > 0)
			return vctPendingInvicesDOs.size();
			
			return 0;
		}
		public void refresh()
		{
			notifyDataSetChanged();
		}

		@Override
		public Object getItem(int arg0) 
		{
			return null;
		}

		@Override
		public long getItemId(int arg0) 
		{
			return 0;
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) 
		{
			PendingInvicesDO pendingInvicesDO = vctPendingInvicesDOs.get(arg0);
			LinearLayout llLayout 	= (LinearLayout)getLayoutInflater().inflate(R.layout.pending_invoice_cell, null);
			TextView tvAmount		= (TextView)llLayout.findViewById(R.id.tvAmount);
			TextView tvBalance	= (TextView)llLayout.findViewById(R.id.tvBalance);
			TextView tvDueDate		= (TextView)llLayout.findViewById(R.id.tvDueDate);
			TextView tvInvoiceName	= (TextView)llLayout.findViewById(R.id.tvInvoiceName);
			
		
			
			tvBalance.setText(""+amountFormate.format(StringUtils.getFloat(pendingInvicesDO.balance)));
			tvAmount.setText(""+amountFormate.format(StringUtils.getFloat(pendingInvicesDO.totalAmount)));
			tvInvoiceName.setText(pendingInvicesDO.invoiceNo);
			tvDueDate.setText(CalendarUtils.getFormatedDatefromString(pendingInvicesDO.invoiceDate));
			if(pendingInvicesDO.IsOutStanding.equalsIgnoreCase("true"))
			{
				tvInvoiceName.setTextColor(getResources().getColor(R.color.red_dark));
			}
			
			
//			tvAmount.setText(Html.fromHtml("<font color = #454545> "+curencyCode+"&nbsp;&nbsp;&nbsp;</font>"+decimalFormat.format(StringUtils.getFloat(pendingInvicesDO.lastbalance))));
			

			
		
			setTypeFaceRobotoNormal(llLayout);
			return llLayout;
		}
	}
	
	@Override
	public void onBackPressed() 
	{
//		super.onBackPressed();
		showCustomDialog(this, getString(R.string.warning), "Do you want to check out?", "Yes", "No", "checkout");

	}
	
	private void showTasksDialog()
	{
		final Vector<String> vecInitiativeIds = new InitiativesDA().getAllInitiativesIdsForCustomer(mallsDetailsScreen.site);
		int count = new StaticSurveyDL().getCompletedSurveyCount(preference.getStringFromPreference(Preference.INT_USER_ID,""));
//		final Vector<String> vecInitiativeIds = new InitiativesDA().getAllInitiatives(preference.getStringFromPreference(Preference.CUSTOMER_ID, "15574"));
		
		 LinearLayout viewDetails = (LinearLayout)LayoutInflater.from(getApplicationContext()).inflate(R.layout.tasks_popup, null);
	        
		 LinearLayout llSurveyExecution     = (LinearLayout)viewDetails.findViewById(R.id.llSurveyExecution);
		 LinearLayout llInitiatives         = (LinearLayout)viewDetails.findViewById(R.id.llInitiatives);
		 LinearLayout llCaptureCompetitors  = (LinearLayout)viewDetails.findViewById(R.id.llCaptureCompetitors);
		 TextView tvInitiativeCount 		= (TextView)viewDetails.findViewById(R.id.tvInitiativeCount);
		 TextView tvSurveyExecutionCount 	= (TextView)viewDetails.findViewById(R.id.tvSurveyExecutionCount);
		 TextView tvTaskHeader 				= (TextView)viewDetails.findViewById(R.id.tvTaskHeader);
		 
		 // Initiative set to gone according to the testers bug report
//		 llInitiatives.setVisibility(View.GONE);
		 
		 if(vecInitiativeIds != null )
			 tvInitiativeCount.setText(vecInitiativeIds.size()+"");
		 
		 tvSurveyExecutionCount.setText(""+count);
		 
		 final Dialog dialogDetails = new Dialog(CheckInOptionActivity.this, android.R.style.Theme_Holo_Dialog);
		 dialogDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 dialogDetails.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		 dialogDetails.addContentView(viewDetails, new LayoutParams(preference.getIntFromPreference(AppConstants.Device_Display_Width, AppConstants.DEVICE_DISPLAY_WIDTH_DEFAULT) - 160, LayoutParams.WRAP_CONTENT));
		 dialogDetails.show();
		 
		 
		 llCaptureCompetitors.setOnClickListener(new OnClickListener() 
		 {
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(CheckInOptionActivity.this, CompetitorsListActivity.class);
				intent.putExtra("Object", mallsDetailsScreen);
				startActivity(intent);
				dialogDetails.dismiss();
			}
		});
		 
		 llInitiatives.setOnClickListener(new OnClickListener() 
		 {
			@Override
			public void onClick(View v) 
			{
				if(vecInitiativeIds.size() > 0)
				{
					Intent intent = new Intent(CheckInOptionActivity.this, InitiativesListActivity.class);
					intent.putExtra("Object", mallsDetailsScreen);
					startActivity(intent);
					dialogDetails.dismiss();
				}
				else
				{
					dialogDetails.dismiss();
					showCustomDialog(CheckInOptionActivity.this, "Alert", "No Initiatives for this visit.", "Ok", "", "Initiatives");
				}
			}
		});
		 
		 llSurveyExecution.setOnClickListener(new OnClickListener() 
		 {
			
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(CheckInOptionActivity.this, SurveyNewActivity.class);
				startActivity(intent);
				dialogDetails.dismiss();
			}
		});
			
		 setTypeFaceRobotoNormal(viewDetails);
		 tvTaskHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}
	
	public void statusUpdate()
	{
		StatusDO statusDO = new StatusDO();
		statusDO.UUid ="";
		statusDO.Userid = preference.getStringFromPreference(Preference.SALESMANCODE, "");
		statusDO.Customersite =mallsDetailsScreen.site;
		statusDO.Date = CalendarUtils.getCurrentDate();
		statusDO.Visitcode=mallsDetailsScreen.VisitCode;
		statusDO.JourneyCode = mallsDetailsScreen.JourneyCode;
		statusDO.Status = "1";
		statusDO.Action = AppConstants.Action_CheckIn;
		statusDO.Type = AppConstants.Type_StoreCheck;
		new StatusDA().insertOptionStatus(statusDO);			
	}
	
	public void updateData()//broadcast
	{
		if(vecstatusDO!=null&&vecstatusDO.size()>0)
		{
			if(vecstatusDO.contains(AppConstants.Type_BeforService))
			{
				ll_storecheck.setClickable(true);
				ll_storecheck.setEnabled(true);
				
				ivstorecheck.setImageResource(R.drawable.checkstore);
				
				tvStoreCheck.setTextColor(getResources().getColor(R.color.black));
			}
			else
			{
				ll_storecheck.setClickable(false);
				ll_storecheck.setEnabled(false);
				
				ivstorecheck.setImageResource(R.drawable.checkstore_h);
				
				tvStoreCheck.setTextColor(getResources().getColor(R.color.gray_light));
			}
			
			if(vecstatusDO.contains(AppConstants.Type_StoreCheck) && vecstatusDO.contains(AppConstants.Type_BeforService))
			{
				ll_salesorder.setClickable(true);
				ll_salesorder.setEnabled(true);
				ll_returnorder.setClickable(true);
				ll_returnorder.setEnabled(true);
				ll_tasks.setClickable(true);
				ll_tasks.setEnabled(true);
				
				ll_Savedorder.setClickable(true);
				ll_Savedorder.setEnabled(true);
				
				ll_collection.setClickable(true);
				ll_collection.setEnabled(true);
				ll_CustomerInfo.setClickable(true);
				ll_CustomerInfo.setEnabled(true);
				ll_CustomerStatement.setClickable(true);
				ll_CustomerStatement.setEnabled(true);
				ll_AfterService.setClickable(true);
				ll_AfterService.setEnabled(true);
				ll_FOC.setClickable(true);
				ll_FOC.setEnabled(true);
				
				ivSalesOrder.setImageResource(R.drawable.salesorder);
				ivReturnOrder.setImageResource(R.drawable.returnorder);
				ivTask.setImageResource(R.drawable.task);
				ivSavedorder.setImageResource(R.drawable.savedorder);
				
				ivcollectioncomplete.setImageResource(R.drawable.collection);
				ivCustomerInfo.setImageResource(R.drawable.customer_info);
				ivCustomerStatement.setImageResource(R.drawable.customer_statement);
				ivAfterService.setImageResource(R.drawable.afterservice);
				ivFOC.setImageResource(R.drawable.foc);
				tvSalesOrder.setTextColor(getResources().getColor(R.color.black));
				tvReturnOrder.setTextColor(getResources().getColor(R.color.black));
				tvTask.setTextColor(getResources().getColor(R.color.black));
				tvSavedOrder.setTextColor(getResources().getColor(R.color.black));
				
				tvCollection.setTextColor(getResources().getColor(R.color.black));
				tvCustomerInfo.setTextColor(getResources().getColor(R.color.black));
				tvCustomerStatement.setTextColor(getResources().getColor(R.color.black));
				tvAfterService.setTextColor(getResources().getColor(R.color.black));
				tvFOC.setTextColor(getResources().getColor(R.color.black));
				
				ll_returnorder.setClickable(true);
				ivReturnOrder.setImageResource(R.drawable.returnorder);
				tvReturnOrder.setTextColor(getResources().getColor(R.color.black));
				
				ll_CustomerInfo.setClickable(true);
				tvCustomerInfo.setTextColor(getResources().getColor(R.color.black));
				
//				if(!preference.getStringFromPreference(preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
				{
					ll_CustomerInfoNew.setClickable(false);
					ll_CustomerInfoNew.setEnabled(false);

					ivAssets.setImageResource(R.drawable.assest);

					tvAssets.setTextColor(getResources().getColor(R.color.black));
				}
			}
			else
			{
				ll_returnorder.setClickable(false);
				ll_returnorder.setEnabled(false);
				ll_tasks.setClickable(false);
				ll_tasks.setEnabled(false);
				ll_Savedorder.setClickable(false);
				ll_Savedorder.setEnabled(false);
				
				ll_collection.setClickable(false);
				ll_collection.setEnabled(false);
				ll_CustomerInfo.setClickable(false);
				ll_CustomerInfo.setEnabled(false);
				ll_CustomerStatement.setClickable(false);
				ll_CustomerStatement.setEnabled(false);
				ll_AfterService.setClickable(false);
				ll_AfterService.setEnabled(false);
				ll_FOC.setClickable(false);
				ll_FOC.setEnabled(false);
				
				ivSalesOrder.setImageResource(R.drawable.salesorder_h);
				ivReturnOrder.setImageResource(R.drawable.returnorder_h);
				ivTask.setImageResource(R.drawable.task_h);
				ivSavedorder.setImageResource(R.drawable.savedorder_h);
				
				ivcollectioncomplete.setImageResource(R.drawable.collection_h);
				ivCustomerInfo.setImageResource(R.drawable.customer_info_h);
				ivCustomerStatement.setImageResource(R.drawable.customer_statement_h);
				ivAfterService.setImageResource(R.drawable.afterservice_h);
				ivFOC.setImageResource(R.drawable.foc_h);
				
				tvSalesOrder.setTextColor(getResources().getColor(R.color.gray_light));
				tvReturnOrder.setTextColor(getResources().getColor(R.color.gray_light));
				tvTask.setTextColor(getResources().getColor(R.color.gray_light));
				tvSavedOrder.setTextColor(getResources().getColor(R.color.gray_light));
				tvCustomerInfo.setTextColor(getResources().getColor(R.color.gray_light));
				
				tvCollection.setTextColor(getResources().getColor(R.color.gray_light));
				tvCustomerInfo.setTextColor(getResources().getColor(R.color.gray_light));
				tvCustomerStatement.setTextColor(getResources().getColor(R.color.gray_light));
				tvAfterService.setTextColor(getResources().getColor(R.color.gray_light));
				tvFOC.setTextColor(getResources().getColor(R.color.gray_light));
				
//				if(!preference.getStringFromPreference(preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
				{
					ll_CustomerInfoNew.setClickable(false);
					ll_CustomerInfoNew.setEnabled(false);

					ivAssets.setImageResource(R.drawable.assest_h);

					tvAssets.setTextColor(getResources().getColor(R.color.gray_light));
				}
			}
			if(vecstatusDO.contains(AppConstants.Type_StoreCheck))
				img_storecheckcomplete.setVisibility(View.VISIBLE);
			else
				img_storecheckcomplete.setVisibility(View.INVISIBLE);
			
			if(vecstatusDO.contains(AppConstants.Type_BeforService))
				img_beforeService.setVisibility(View.VISIBLE);
			else
				img_beforeService.setVisibility(View.INVISIBLE);
			
			if(vecstatusDO.contains(AppConstants.Type_AfterService))
				img_afterService.setVisibility(View.VISIBLE);
			else
				img_afterService.setVisibility(View.INVISIBLE);
			
			if(vecstatusDO.contains(AppConstants.Type_FOCOrder))
				img_FOCcomplete.setVisibility(View.VISIBLE);
			else
				img_FOCcomplete.setVisibility(View.INVISIBLE);
		
			if(vecstatusDO.contains(AppConstants.Type_SalesOrder))
				img_salesordercomplete.setVisibility(View.VISIBLE);
			else
				img_salesordercomplete.setVisibility(View.INVISIBLE);
			
			if(preference.getStringFromPreference(preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
			{
				if(vecstatusDO.contains(AppConstants.Type_PresalesOrder))
					img_salesordercomplete.setVisibility(View.VISIBLE);
				else
					img_salesordercomplete.setVisibility(View.INVISIBLE);
			}
			
			if(vecstatusDO.contains(AppConstants.Type_Collections))
				img_collectioncomplete.setVisibility(View.VISIBLE);
			else
				img_collectioncomplete.setVisibility(View.INVISIBLE);
			
			if(vecstatusDO.contains(AppConstants.Type_ReturnOrder))
				img_returnordercomplete.setVisibility(View.VISIBLE);
			else
				img_returnordercomplete.setVisibility(View.INVISIBLE);
			
			if(vecstatusDO.contains(AppConstants.Type_Task))
				img_taskscomplete.setVisibility(View.VISIBLE);
			else
				img_taskscomplete.setVisibility(View.INVISIBLE);
			
			if(vecstatusDO.contains(AppConstants.Type_SavedOrder))
				img_Savedordercomplete.setVisibility(View.VISIBLE);
			else
				img_Savedordercomplete.setVisibility(View.INVISIBLE);
			
//			if(!preference.getStringFromPreference(preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
			{
				if(vecstatusDO.contains(AppConstants.Type_Assets))
					img_assetescomplete.setVisibility(View.VISIBLE);
				else
					img_assetescomplete.setVisibility(View.INVISIBLE);
			}
		}
		else
		{
			ll_salesorder.setClickable(false);
			ll_salesorder.setEnabled(false);
			ll_returnorder.setClickable(false);
			ll_returnorder.setEnabled(false);
			ll_tasks.setClickable(false);
			ll_tasks.setEnabled(false);
			ll_Savedorder.setClickable(false);
			ll_Savedorder.setEnabled(false);
			
			ll_storecheck.setClickable(false);
			ll_storecheck.setEnabled(false);
			ll_collection.setClickable(false);
			ll_collection.setEnabled(false);
			ll_CustomerInfo.setClickable(false);
			ll_CustomerInfo.setEnabled(false);
			ll_CustomerStatement.setClickable(false);
			ll_CustomerStatement.setEnabled(false);
			ll_AfterService.setClickable(false);
			ll_AfterService.setEnabled(false);
			ll_FOC.setClickable(false);
			ll_FOC.setEnabled(false);
			
			ivSalesOrder.setImageResource(R.drawable.salesorder_h);
			ivReturnOrder.setImageResource(R.drawable.returnorder_h);
			ivTask.setImageResource(R.drawable.task_h);
			ivSavedorder.setImageResource(R.drawable.savedorder_h);
			
			ivcollectioncomplete.setImageResource(R.drawable.collection_h);
			ivCustomerInfo.setImageResource(R.drawable.customer_info_h);
			ivCustomerStatement.setImageResource(R.drawable.customer_statement_h);
			ivAfterService.setImageResource(R.drawable.afterservice_h);
			ivFOC.setImageResource(R.drawable.foc_h);
			
			tvSalesOrder.setTextColor(getResources().getColor(R.color.gray_light));
			tvReturnOrder.setTextColor(getResources().getColor(R.color.gray_light));
			tvTask.setTextColor(getResources().getColor(R.color.gray_light));
			tvSavedOrder.setTextColor(getResources().getColor(R.color.gray_light));
			tvCustomerInfo.setTextColor(getResources().getColor(R.color.gray_light));
			
			tvCollection.setTextColor(getResources().getColor(R.color.gray_light));
			tvCustomerInfo.setTextColor(getResources().getColor(R.color.gray_light));
			tvCustomerStatement.setTextColor(getResources().getColor(R.color.gray_light));
			tvAfterService.setTextColor(getResources().getColor(R.color.gray_light));
			tvFOC.setTextColor(getResources().getColor(R.color.gray_light));
			
//			if(!preference.getStringFromPreference(preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
			{
				ivAssets.setImageResource(R.drawable.assest_h);

				tvAssets.setTextColor(getResources().getColor(R.color.gray_light));
			}
		}
		
		if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equals(AppConstants.SALESMAN_GT))
		{
			btnTakeNewOrder.setVisibility(View.VISIBLE);
			llReturnOrder.setVisibility(View.VISIBLE);
		}
		else if(mallsDetailsScreen.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
		{
			btnTakeNewOrder.setVisibility(View.GONE);
			btnPendinInvoices.setVisibility(View.GONE);
			
			sepTakeNewOrder.setVisibility(View.VISIBLE);
			seppendingInvoice.setVisibility(View.GONE);
		}
		else
			btnTakeNewOrder.setVisibility(View.GONE);


		if( isForceCheckIn && isVanSaleUser )
		{
			ll_salesorder.setClickable(false);
			ll_salesorder.setEnabled(false);
			ivSalesOrder.setImageResource(R.drawable.salesorder_h);
			tvSalesOrder.setTextColor(getResources().getColor(R.color.gray_light));

			ll_FOC.setClickable(false);
			ll_FOC.setEnabled(false);
			ivFOC.setImageResource(R.drawable.foc_h);
			tvFOC.setTextColor(getResources().getColor(R.color.gray_light));

		}

	}
	
	private void showOrderTypesDialog(final AgencyNewDo AgnCustom)
	{
		final Vector<String> vecInitiativeIds = new InitiativesDA().getAllInitiativesIdsForCustomer(mallsDetailss.site);
		int count = new StaticSurveyDL().getCompletedSurveyCount(preference.getStringFromPreference(Preference.INT_USER_ID,""));
		
		 LinearLayout viewDetails 		= (LinearLayout)LayoutInflater.from(CheckInOptionActivity.this).inflate(R.layout.cash_credit_invoice_popup, null);
	        
		 Button btn_Salable 			= (Button)viewDetails.findViewById(R.id.btn_Salable);
		 Button btn_NonSalable			= (Button)viewDetails.findViewById(R.id.btn_NonSalable);
		 TextView tv_poptitle 			= (TextView)viewDetails.findViewById(R.id.tv_poptitle);
		 final Dialog dialogDetails 	= new Dialog(CheckInOptionActivity.this, android.R.style.Theme_Holo_Dialog);
		 dialogDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 dialogDetails.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		 dialogDetails.addContentView(viewDetails, new LayoutParams(preference.getIntFromPreference(AppConstants.Device_Display_Width, AppConstants.DEVICE_DISPLAY_WIDTH_DEFAULT) - 160, LayoutParams.WRAP_CONTENT));
		 dialogDetails.show();
		 
		 btn_Salable.setOnClickListener(new OnClickListener() 
		 {
			@Override
			public void onClick(View v) 
			{
				Intent intent = null;
				intent  =	new Intent(CheckInOptionActivity.this,SalesmanReturnOrder.class);
				intent.putExtra("mallsDetails", mallsDetailsScreen);
				intent.putExtra("from", "checkINOption");
				intent.putExtra("Return_Type", AppConstants.Return_Type_Salable);
				
				if(AgnCustom!=null)
					intent.putExtra(AppConstants.DIVISION, AgnCustom.Priority);
				
				startActivity(intent);
				dialogDetails.dismiss();
			}
		});
		 
		 btn_NonSalable.setOnClickListener(new OnClickListener() 
		 {
			@Override
			public void onClick(View v)
			{
				Intent intent = null;
				intent  =	new Intent(CheckInOptionActivity.this,SalesmanReturnOrder.class);
				intent.putExtra("mallsDetails", mallsDetailsScreen);
				intent.putExtra("from", "checkINOption");
				intent.putExtra("Return_Type", AppConstants.Return_Type_Non_Salable);
				
				if(AgnCustom!=null)
					intent.putExtra(AppConstants.DIVISION, AgnCustom.Priority);
				
				startActivity(intent);
				dialogDetails.dismiss();
			}
		});
		 
		tv_poptitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}
}
