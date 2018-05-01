package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.Vector;

import android.app.Dialog;
import android.content.Intent;
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
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.ARCollectionDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.InitiativesDA;
import com.winit.alseer.salesman.dataaccesslayer.StatusDA;
import com.winit.alseer.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.PendingInvicesDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;

public class CheckInOptionActivityNewUser extends BaseActivity
{
	private LinearLayout llCheckINOption,llCreditValues,llCreditLimitVal,llOutStandingVal,llAvailableLimitVal;
	private RelativeLayout ll_salesorder,ll_collection,ll_missedorder,ll_orderlist;
	private JourneyPlanDO mallsDetails ;
	private TextView tvCreditLimit,tvCreditLimitVal,tvAvailLimt,tvAvailLimtVal/*,tvOverDueAmount,tvOverDueAmountVal*/,tvOutstandingAmount,tvOutstandingAmountval;
	private TextView tvHeadTitle,tvCustomerAddressinoptions;
	private LinearLayout btnTakeNewOrder, /*btnTakeAdvanceOrder*/ btnOrderList, btnPendinInvoices, btnAssetScan,llReturnOrder, btnReplacements, llStoreCheck;
	private ImageView img_salesordercomplete,img_collectioncomplete,img_missedordercomplete,img_orderlistcomplete,sepTakeNewOrder,sepReturnNewOrder,sepOrderlist,seppendingInvoice,sepAssetScan/*, ivReturnOrder*/;
	private Vector<String> vecstatusDO;
	
	@Override
	public void initialize()
	{
		
		ivDivider.setVisibility(View.VISIBLE);
		btnCartCount.setVisibility(View.VISIBLE);
		ivShoppingCart.setVisibility(View.VISIBLE);
		btnCartCount.bringToFront();
		
		llCheckINOption = (LinearLayout) inflater.inflate(R.layout.custom_popup_check_in_new_customer, null);
		llBody.addView(llCheckINOption, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		AppConstants.isTaskCompleted = false;
		AppConstants.isServeyCompleted = false;
		if(getIntent().getExtras() != null)
			mallsDetails = (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
		initializeControles();  
		
//		tvHeadTitle.setText(mallsDetails.siteName  + " ("+mallsDetails.partyName+")");
		tvHeadTitle.setText(mallsDetails.siteName);
		
		
		tvCustomerAddressinoptions.setText(getAddress(mallsDetails));
		
		if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equals(AppConstants.SALESMAN_GT))
		{
//			ivTakeOrderSap.setVisibility(View.VISIBLE);
			btnTakeNewOrder.setVisibility(View.VISIBLE);
			llReturnOrder.setVisibility(View.VISIBLE);
		}
		else if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
		{
//			ivTakeOrderSap.setVisibility(View.GONE);
			btnTakeNewOrder.setVisibility(View.GONE);
			btnPendinInvoices.setVisibility(View.GONE);
			
			sepTakeNewOrder.setVisibility(View.VISIBLE);
			seppendingInvoice.setVisibility(View.GONE);
		}
		else
		{
//			ivTakeOrderSap.setVisibility(View.GONE);
//			ivReturnOrder.setVisibility(View.GONE);
			btnTakeNewOrder.setVisibility(View.GONE);
//			llReturnOrder.setVisibility(View.GONE);
		}
		
		
		
		ll_salesorder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
//				if(!mallsDetails.blockedStatus.equalsIgnoreCase("true"))
//				{
					Intent intent = new Intent(CheckInOptionActivityNewUser.this, SalesManRecommendedOrder.class);
					intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
					intent.putExtra("mallsDetails",mallsDetails);
					intent.putExtra("from", "checkin");
					startActivity(intent);
//				}
//				else
//					showCustomDialog(CheckInOptionActivityNewUser.this, getString(R.string.alert), "Customer has been blocked, please contact to sales manager.", "OK", null, "");
			}
		});
		
		ll_collection.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(CheckInOptionActivityNewUser.this, PendingInvoices.class);
				intent.putExtra("arcollection", true);
				intent.putExtra("mallsDetails", mallsDetails);
				intent.putExtra("fromMenu", true);
				intent.putExtra("AR", true);
				startActivity(intent);
			}
		});
		
		
		ll_missedorder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{

				
			}
		});
		
		ll_orderlist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Intent intent =	new Intent(CheckInOptionActivityNewUser.this,  SalesmanOrderList.class);
				intent.putExtra("object", mallsDetails);
				startActivity(intent);
				
			}
		});
		
		
//		llStoreCheck.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				Intent intent = new Intent(CheckInOptionActivity.this, CaptureInventoryCategory.class);
//				intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
//				intent.putExtra("mallsDetails",mallsDetails);
//				intent.putExtra("from", "checkin");
//				startActivity(intent);
//			}
//		});
		
	/*	showLoader("Please wait...");
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				StatusDO statusDO = new StatusDO();
				statusDO.UUid ="";
				statusDO.Userid = preference.getStringFromPreference(Preference.SALESMANCODE, "");
				statusDO.Customersite =mallsDetails.site;
				statusDO.Date = CalendarUtils.getCurrentDate();
				statusDO.Visitcode=mallsDetails.VisitCode;
				statusDO.JourneyCode = mallsDetails.JourneyCode;
				statusDO.Status = "1";
				statusDO.Action = "CheckInOption";
				statusDO.Type = "StoreCheck";
				new StatusDA().insertOptionStatus(statusDO);
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run()
					{
						hideLoader();
					}
				});
			}
		}).start();*/
		llReturnOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = null;
				intent  =	new Intent(CheckInOptionActivityNewUser.this,  SalesManTakeReturnOrder.class);
				intent.putExtra("mallsDetails", mallsDetails);
				intent.putExtra("from", "checkINOption");
				startActivity(intent);
			}
		});
		
		btnTakeNewOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(CheckInOptionActivityNewUser.this, SalesManTakeOrder.class);
				intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
				intent.putExtra("mallsDetails",mallsDetails);
				intent.putExtra("from", "checkin");
				startActivity(intent);
			}
		});
		
		
//		btnTakeAdvanceOrder.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				Intent intent = new Intent(CheckInOptionActivity.this, SalesManTakeAdvanceOrder.class);
//				intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
//				intent.putExtra("mallsDetails",mallsDetails);
//				intent.putExtra("from", "checkin");
//				startActivity(intent);
//			}
//		});
//		btnOrderList.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				Intent intent =	new Intent(CheckInOptionActivity.this,  SalesmanOrderList.class);
//				intent.putExtra("object", mallsDetails);
//				startActivity(intent);
////				finish();
//			}
//		});
//		btnPendinInvoices.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				Intent intent = new Intent(CheckInOptionActivity.this, PendingInvoices.class);
//				intent.putExtra("arcollection", true);
//				intent.putExtra("mallsDetails", mallsDetails);
//				intent.putExtra("fromMenu", true);
//				intent.putExtra("AR", true);
//				startActivity(intent);
//			}
//		});
//		btnAssetScan.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				Intent intent = new Intent(CheckInOptionActivity.this, CustomerAssetsListActivity.class);
//				intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
//				intent.putExtra("mallsDetails",mallsDetails);
//				startActivity(intent);
////				finish();
//			}
//		});
		
		btnReplacements.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent =	new Intent(CheckInOptionActivityNewUser.this,  SalesManTakeReturnOrder.class);
				intent.putExtra("mallsDetails", mallsDetails);
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
//		tvOverDueAmount					= (TextView) llCheckINOption.findViewById(R.id.tvOverDueAmount);
//		tvOverDueAmountVal  = (TextView) llCheckINOption.findViewById(R.id.tvOverDueAmountVal);
		tvOutstandingAmount			= (TextView) llCheckINOption.findViewById(R.id.tvOutstandingAmount);
		tvOutstandingAmountval  	= (TextView) llCheckINOption.findViewById(R.id.tvOutstandingAmountval);
		
		btnTakeNewOrder				= (LinearLayout) llCheckINOption.findViewById(R.id.btnTakeNewOrder);
		btnOrderList				= (LinearLayout) llCheckINOption.findViewById(R.id.btnOrderList);
		btnPendinInvoices			= (LinearLayout) llCheckINOption.findViewById(R.id.btnPendinInvoices);
		btnAssetScan				= (LinearLayout) llCheckINOption.findViewById(R.id.btnAssetScan);
		llReturnOrder				= (LinearLayout) llCheckINOption.findViewById(R.id.llReturnOrder);
		ll_salesorder				= (RelativeLayout) llCheckINOption.findViewById(R.id.ll_salesorder);
		ll_collection				= (RelativeLayout) llCheckINOption.findViewById(R.id.ll_collection);
		ll_missedorder    			= (RelativeLayout) llCheckINOption.findViewById(R.id.ll_missedorder);
		ll_orderlist    			= (RelativeLayout) llCheckINOption.findViewById(R.id.ll_orderlist);

		img_salesordercomplete		= (ImageView) llCheckINOption.findViewById(R.id.img_salesordercomplete);
		img_collectioncomplete		= (ImageView) llCheckINOption.findViewById(R.id.img_collectioncomplete);
		img_missedordercomplete		= (ImageView) llCheckINOption.findViewById(R.id.img_missedordercomplete);
		img_orderlistcomplete		= (ImageView) llCheckINOption.findViewById(R.id.img_orderlistcomplete);
		btnReplacements				= (LinearLayout) llCheckINOption.findViewById(R.id.btnReplacements);
		
//		ivTakeOrderSap		= (ImageView) llCheckINOption.findViewById(R.id.ivTakeOrderSap);
//		btnTakeAdvanceOrder = (LinearLayout) llCheckINOption.findViewById(R.id.btnTakeAdvanceOrder);
//		ivTakeOrderSap		= (ImageView) llCheckINOption.findViewById(R.id.ivTakeOrderSap);
		
//		sepTakeNewOrder		= (ImageView) llCheckINOption.findViewById(R.id.sepTakeNewOrder);
//		sepReturnNewOrder	= (ImageView) llCheckINOption.findViewById(R.id.sepReturnNewOrder);
//		sepOrderlist		= (ImageView) llCheckINOption.findViewById(R.id.sepOrderlist);
//		seppendingInvoice	= (ImageView) llCheckINOption.findViewById(R.id.seppendingInvoice);
//		sepAssetScan		= (ImageView) llCheckINOption.findViewById(R.id.sepAssetScan);
//		ivReturnOrder		= (ImageView) llCheckINOption.findViewById(R.id.ivReturnOrder);
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
				creditLimit = new CustomerDA().getCustomerCreditLimit(mallsDetails.site);
				overdue = new CustomerDA().getOverdueAmount(mallsDetails.site);
				if(StringUtils.getFloat(creditLimit.availbleLimit)<=0||overdue >0)
					 vecPendingInvoices = new ARCollectionDA().getListPendingInvoices(mallsDetails.site);
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
						if(vecPendingInvoices!=null && vecPendingInvoices.size()>0)
							showPendingInvoicePopup(vecPendingInvoices);
					}
				});
				
			}
		}).start();
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		AppConstants.isServeyCompleted = false;
		new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				vecstatusDO = new StatusDA().getCompletedOptionsStatus(mallsDetails.site,AppConstants.Action_CheckIn);
//				mallsDetails.cartItemCount = new OrderDA().getCartCount(mallsDetails.site, AppConstants.CART_TYPE);
				runOnUiThread(new  Runnable() 
				{
					public void run() 
					{
//						btnCartCount.setText(mallsDetails.cartItemCount+"");
						if(vecstatusDO!=null&&vecstatusDO.size()>0)
						{
							
							if(vecstatusDO.contains(AppConstants.Type_SalesOrder))
								img_salesordercomplete.setVisibility(View.VISIBLE);
							else
								img_salesordercomplete.setVisibility(View.INVISIBLE);
							
							if(vecstatusDO.contains(AppConstants.Type_Collections))
								img_collectioncomplete.setVisibility(View.VISIBLE);
							else
								img_collectioncomplete.setVisibility(View.INVISIBLE);
							
							if(vecstatusDO.contains(AppConstants.Type_MissedOrder))
								img_missedordercomplete.setVisibility(View.VISIBLE);
							else
								img_missedordercomplete.setVisibility(View.INVISIBLE);
							
							if(vecstatusDO.contains(AppConstants.Type_AdvancedOrder))
								img_orderlistcomplete.setVisibility(View.VISIBLE);
							else
								img_orderlistcomplete.setVisibility(View.INVISIBLE);
						}
					}
				});
				
			}
		}).start();
		
		if(pendingInvoicesPopup!=null&&pendingInvoicesPopup.isShowing())
			pendingInvoicesPopup.dismiss();
		if(mallsDetails.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
		{
			llCreditValues.setVisibility(View.VISIBLE);
			checkBlockedCustomer();
		}
		else
		{
			llCreditValues.setVisibility(View.GONE);
		}
		
	}
	PopupWindow pendingInvoicesPopup;
	public void showPendingInvoicePopup(ArrayList<PendingInvicesDO> vecPendingInvoices)
	{
		pendingInvoicesPopup = new PopupWindow(CheckInOptionActivityNewUser.this);
		View pendingInvoices 		= inflater.inflate(R.layout.pending_invoices_popup, null);
		pendingInvoicesPopup = new PopupWindow(pendingInvoices,LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
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
				Intent intent = new Intent(CheckInOptionActivityNewUser.this, PendingInvoices.class);
				intent.putExtra("arcollection", true);
				intent.putExtra("mallsDetails", mallsDetails);
				intent.putExtra("fromMenu", false);
				intent.putExtra("isExceed", true);
				intent.putExtra("AR", true);
				startActivityForResult(intent, 1000);
				
			}
		});
		btnCheckOut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pendingInvoicesPopup.dismiss();
//				finish();
				performCheckouts(true);
			}
		});
		
		pendingInvoicesPopup.showAtLocation(llCheckINOption, Gravity.CENTER, 0, 0);
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
		final Vector<String> vecInitiativeIds = new InitiativesDA().getAllInitiativesIdsForCustomer(preference.getStringFromPreference(Preference.CUSTOMER_ID, "15574"));
		
//		final Vector<String> vecInitiativeIds = new InitiativesDA().getAllInitiatives(preference.getStringFromPreference(Preference.CUSTOMER_ID, "15574"));
		
		 LinearLayout viewDetails = (LinearLayout)LayoutInflater.from(getApplicationContext()).inflate(R.layout.tasks_popup, null);
	        
		 LinearLayout llSurveyExecution     = (LinearLayout)viewDetails.findViewById(R.id.llSurveyExecution);
		 LinearLayout llInitiatives         = (LinearLayout)viewDetails.findViewById(R.id.llInitiatives);
		 LinearLayout llCaptureCompetitors  = (LinearLayout)viewDetails.findViewById(R.id.llCaptureCompetitors);
		 TextView tvInitiativeCount 		= (TextView)viewDetails.findViewById(R.id.tvInitiativeCount);
		 TextView tvSurveyExecutionCount 	= (TextView)viewDetails.findViewById(R.id.tvSurveyExecutionCount);
		 TextView tvTaskHeader 				= (TextView)viewDetails.findViewById(R.id.tvTaskHeader);
		 
		 if(vecInitiativeIds != null )
			 tvInitiativeCount.setText(vecInitiativeIds.size()+"");
		 
		 final Dialog dialogDetails = new Dialog(CheckInOptionActivityNewUser.this, android.R.style.Theme_Holo_Dialog);
		 dialogDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 dialogDetails.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		 dialogDetails.addContentView(viewDetails, new LayoutParams(preference.getIntFromPreference(AppConstants.Device_Display_Width, AppConstants.DEVICE_DISPLAY_WIDTH_DEFAULT) - 160, LayoutParams.WRAP_CONTENT));
		 dialogDetails.show();
		 
		 
		 llCaptureCompetitors.setOnClickListener(new OnClickListener() 
		 {
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(CheckInOptionActivityNewUser.this, CompetitorsListActivity.class);
				intent.putExtra("Object", mallsDetails);
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
					Intent intent = new Intent(CheckInOptionActivityNewUser.this, InitiativesListActivity.class);
					startActivity(intent);
					dialogDetails.dismiss();
				}
				else
				{
					dialogDetails.dismiss();
					showCustomDialog(CheckInOptionActivityNewUser.this, "Alert", "No Initiatives for this visit.", "Ok", "", "Initiatives");
				}
			}
		});
		 
		 llSurveyExecution.setOnClickListener(new OnClickListener() 
		 {
			
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(CheckInOptionActivityNewUser.this, SurveyNewActivity.class);
				startActivity(intent);
			}
		});
			
		 setTypeFaceRobotoNormal(viewDetails);
		 tvTaskHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}
}
