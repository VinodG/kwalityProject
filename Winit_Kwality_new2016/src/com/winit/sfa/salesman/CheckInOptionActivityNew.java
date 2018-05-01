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
import android.widget.GridView;
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
import com.winit.alseer.salesman.dataaccesslayer.StaticSurveyDL;
import com.winit.alseer.salesman.dataaccesslayer.StatusDA;
import com.winit.alseer.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.PendingInvicesDO;
import com.winit.alseer.salesman.dataobject.StatusDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;

public class CheckInOptionActivityNew extends BaseActivity
{
	private LinearLayout llCheckINOption;
	private JourneyPlanDO mallsDetails ;
	private Vector<String> vecstatusDO;
	private CheckInOptionAdapter checkInOptionAdapter;
	
	@Override
	public void initialize()
	{
		ivDivider.setVisibility(View.VISIBLE);
		btnCartCount.setVisibility(View.VISIBLE);
		ivShoppingCart.setVisibility(View.VISIBLE);
		btnCartCount.bringToFront();
		
		llCheckINOption = (LinearLayout) inflater.inflate(R.layout.custom_popup_check_in_new, null);
		llBody.addView(llCheckINOption, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		AppConstants.isTaskCompleted = false;
		AppConstants.isServeyCompleted = false;
		
		if(getIntent().getExtras() != null)
			mallsDetails = (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
		
		//initializeControles
		tvHeadTitle			= (TextView) llCheckINOption.findViewById(R.id.tvHeadTitle);
		gvCheckInOptions	= (GridView) llCheckINOption.findViewById(R.id.gvCheckInOptions);
		
		tvHeadTitle.setText(mallsDetails.siteName+" ["+mallsDetails.site+"]");
		
		setTypeFaceRobotoNormal(llCheckINOption);
		tvHeadTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
			isPreSaler = 1;
		else
			isPreSaler = 0;
		
		checkInOptionAdapter = new CheckInOptionAdapter();
		gvCheckInOptions.setAdapter(checkInOptionAdapter);
	}
	
	//new modifications
	private int isPreSaler = -1;
	private TextView tvHeadTitle;
	private GridView gvCheckInOptions;
	
	private String[] strOptionsVanSales = {"Store Check","Sales Order",
											"Collection","Return Order",
											"Advance Order","Order Summary",
											"Payment Summary","Customer Statement",
											"Tasks","Check out"};
	
	private String[] strOptionsPreSales = {"Store Check","Sales Order",
											"Collection","Return Order",
											"Tasks","Check out"};
	
	private int[] drawablesOptionsVanSales_h = {R.drawable.checkstore,R.drawable.salesorder_h,
												R.drawable.collection,R.drawable.returnorder_h,
												R.drawable.orderlist_h,R.drawable.savedorder_h,
												R.drawable.customer_info,R.drawable.customer_statement,
												R.drawable.task_h,R.drawable.missedorder};
	
	private int[] drawablesOptionsVanSales_n = {R.drawable.checkstore,R.drawable.salesorder,
												R.drawable.collection,R.drawable.returnorder,
												R.drawable.orderlist,R.drawable.savedorder,
												R.drawable.customer_info,R.drawable.customer_statement,
												R.drawable.task,R.drawable.missedorder};
	
	private int[] drawablesOptionsPreSales_h = {R.drawable.checkstore,R.drawable.salesorder_h,
												R.drawable.collection,R.drawable.returnorder_h,
												R.drawable.task_h,R.drawable.customer_info};

	private int[] drawablesOptionsPreSales_n = {R.drawable.checkstore,R.drawable.salesorder,
												R.drawable.collection,R.drawable.returnorder,
												R.drawable.task,R.drawable.customer_info};
	
	private class ViewHolder{
		RelativeLayout rlOption;
		ImageView ivOptioncomplete, ivOption;
		TextView tvOption;
	}
	
	private class CheckInOptionAdapter extends BaseAdapter{

		private int isStoreChecked = 0;
		
		@Override
		public int getCount() {
			if(isPreSaler == 1)
				return strOptionsPreSales.length;
			else if(isPreSaler == 0)
				return strOptionsVanSales.length;
			else return 0;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		public void refreshStoreChecked(int isStoreChecked){
			this.isStoreChecked = isStoreChecked;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder viewHolder;
			
			if(convertView == null)
			{
				viewHolder = new ViewHolder();
				convertView	= inflater.inflate(R.layout.custom_popup_check_in_new_item, null);
				
				viewHolder.rlOption 		= (RelativeLayout) convertView.findViewById(R.id.rlOption);
				viewHolder.tvOption 		= (TextView) convertView.findViewById(R.id.tvOption);
				viewHolder.ivOption 		= (ImageView) convertView.findViewById(R.id.ivOption);
				viewHolder.ivOptioncomplete = (ImageView) convertView.findViewById(R.id.ivOptioncomplete);
				
				convertView.setTag(viewHolder);
			}
			else
				viewHolder = (ViewHolder)convertView.getTag();
			
			
			// setting tag to LL for getting position
			viewHolder.rlOption.setTag(position);
			
			
			// setting texts to Options
			if(isPreSaler == 1)
				viewHolder.tvOption.setText(strOptionsPreSales[position]);
			else
				viewHolder.tvOption.setText(strOptionsVanSales[position]);
			
			

			// for Store Check always selected
			if(position == 0)
			{
				viewHolder.tvOption.setTextColor(getResources().getColor(R.color.black));
			}
			// for collections always selected
			else if(position == 2)
			{
				if(mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
				{
					viewHolder.tvOption.setTextColor(getResources().getColor(R.color.gray_light));
					viewHolder.ivOption.setImageResource(drawablesOptionsPreSales_h[position]);
				}
				else
				{
					viewHolder.ivOption.setImageResource(drawablesOptionsPreSales_n[position]);
					viewHolder.tvOption.setTextColor(getResources().getColor(R.color.black));
				}
			}
			
			// setting selected Options according to store checked
			if(isStoreChecked == 1)
			{
				viewHolder.tvOption.setTextColor(getResources().getColor(R.color.black));
				
				// setting selected presales Options after store checked
				if(isPreSaler == 1)
				{
					viewHolder.ivOption.setImageResource(drawablesOptionsPreSales_n[position]);
					
					if(position == 1)
					{
						if(vecstatusDO.contains(AppConstants.Type_PresalesOrder))
							viewHolder.ivOptioncomplete.setVisibility(View.VISIBLE);
						else
							viewHolder.ivOptioncomplete.setVisibility(View.INVISIBLE);
					}
					else if(position == 4)
					{
						if(vecstatusDO.contains(AppConstants.Type_Task))
							viewHolder.ivOptioncomplete.setVisibility(View.VISIBLE);
						else
							viewHolder.ivOptioncomplete.setVisibility(View.INVISIBLE);
					}
				}
				else
				{
					// setting completions of vansales Options after store checked differ from presales
					
					viewHolder.ivOption.setImageResource(drawablesOptionsVanSales_n[position]);
					
					if(position == 4)
					{
						if(vecstatusDO.contains(AppConstants.Type_AdvancedOrder))
							viewHolder.ivOptioncomplete.setVisibility(View.VISIBLE);
						else
							viewHolder.ivOptioncomplete.setVisibility(View.INVISIBLE);
					}
					else if(position == 5)
					{
						if(vecstatusDO.contains(AppConstants.Type_SavedOrder))
							viewHolder.ivOptioncomplete.setVisibility(View.VISIBLE);
						else
							viewHolder.ivOptioncomplete.setVisibility(View.INVISIBLE);
					}
					else if(position == 8)
					{
						if(vecstatusDO.contains(AppConstants.Type_Task))
							viewHolder.ivOptioncomplete.setVisibility(View.VISIBLE);
						else
							viewHolder.ivOptioncomplete.setVisibility(View.INVISIBLE);
					}
				}
				
				// setting completions of 4 common Options after store checked
				if(position == 0)
					viewHolder.ivOptioncomplete.setVisibility(View.VISIBLE);
				if(position == 1)
				{
					if(vecstatusDO.contains(AppConstants.Type_SalesOrder))
						viewHolder.ivOptioncomplete.setVisibility(View.VISIBLE);
					else
						viewHolder.ivOptioncomplete.setVisibility(View.INVISIBLE);
				}
				if(position == 2)
				{
					if(vecstatusDO.contains(AppConstants.Type_Collections))
						viewHolder.ivOptioncomplete.setVisibility(View.VISIBLE);
					else
						viewHolder.ivOptioncomplete.setVisibility(View.INVISIBLE);
				}
				if(position == 3)
				{
					if(vecstatusDO.contains(AppConstants.Type_ReturnOrder))
						viewHolder.ivOptioncomplete.setVisibility(View.VISIBLE);
					else
						viewHolder.ivOptioncomplete.setVisibility(View.INVISIBLE);
				}
			}
			else
			{
				// setting selection of Options without store checked
				if(isPreSaler == 1)
					viewHolder.ivOption.setImageResource(drawablesOptionsPreSales_h[position]);
				else
					viewHolder.ivOption.setImageResource(drawablesOptionsVanSales_h[position]);
				
				if(position != 0 || position != 2)
					viewHolder.tvOption.setTextColor(getResources().getColor(R.color.gray_light));
			}
			
			viewHolder.rlOption.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int pos = (Integer)v.getTag();
					if(isPreSaler == 1)
					{
						if(pos == 1)
						{
							if(mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) && pos == 1)
								clickCheckInOptionsPreSaler((Integer)v.getTag());
						}
						else
							clickCheckInOptionsPreSaler((Integer)v.getTag());
					}
					else
					{
						if(isStoreChecked == 0)
						{
							if(pos == 0 || pos == 2)
								clickCheckInOptionsVanSales(pos);
						}
						else
						{
							if(pos == 1)
							{
								if(mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) && pos == 1)
									clickCheckInOptionsPreSaler((Integer)v.getTag());
							}
							else
								clickCheckInOptionsVanSales(pos);
						}
					}
				}
			});
			
			return convertView;
		}
	}
	
	private void updatePreSalesView()
	{
		
	}
	
	private void updateVanSalesView()
	{
		
	}
	
	private void clickCheckInOptionsVanSales(int position){
		Intent intent = null;
		switch (position) {
		case 0: //ll_storecheck
			intent = new Intent(CheckInOptionActivityNew.this, CaptureInventoryActivity.class);
			intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
			intent.putExtra("mallsDetails",mallsDetails);
			intent.putExtra("from", "checkin");
			intent.putExtra("checkType", AppConstants.Capture_Inventory);
			startActivity(intent);
			break;
			
		case 1: //ll_salesorder
			intent = new Intent(CheckInOptionActivityNew.this, SalesManRecommendedOrder.class);
			intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
			intent.putExtra("mallsDetails",mallsDetails);
			intent.putExtra("from", "checkin");
			
			if(isPreSaler == 1)
				intent.putExtra("TRX_TYPE", TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER());
			else
				intent.putExtra("TRX_TYPE", TrxHeaderDO.get_TRXTYPE_SALES_ORDER());
			
			intent.putExtra("TRX_SUB_TYPE", TrxHeaderDO.get_TRX_SUBTYPE_SALES_ORDER());
			startActivity(intent);
			break;
			
		case 2: //ll_collection
			intent = new Intent(CheckInOptionActivityNew.this, PendingInvoices.class);
			intent.putExtra("arcollection", true);
			intent.putExtra("mallsDetails", mallsDetails);
			intent.putExtra("fromMenu", true);
			intent.putExtra("AR", true);
			startActivity(intent);
			break;
			
		case 3: //ll_returnorder
			intent  =	new Intent(CheckInOptionActivityNew.this,SalesmanReturnOrder.class);
			intent.putExtra("mallsDetails", mallsDetails);
			intent.putExtra("from", "checkINOption");
			startActivity(intent);
			break;
			
		case 4: //ll_orderlist
			intent =	new Intent(CheckInOptionActivityNew.this,  SalesmanOrderList.class);
			intent.putExtra("object", mallsDetails);
			startActivity(intent);
			break;
			
		case 5: //ll_Savedorder	//saved order summary
			intent = new Intent(CheckInOptionActivityNew.this, CustomerOrderSummary.class);
			intent.putExtra("mallsDetails",mallsDetails);
			startActivity(intent);
			break;
			
		case 6: // for Payment Summary Report	ll_CustomerInfo
			intent =	new Intent(CheckInOptionActivityNew.this,  CustomerPaymentSummaryActivity.class);
			intent.putExtra("mallsDetails", mallsDetails);
			startActivity(intent);
			break;
			
		case 7: //ll_CustomerStatement
			intent =	new Intent(CheckInOptionActivityNew.this,  CustomerStatementActivity.class);
			intent.putExtra("mallsDetails", mallsDetails);
			startActivity(intent);
			break;
			
		case 8: //ll_tasks
			showTasksDialog();
			break;
			
		case 9: //ll_CheckOut
			showCustomDialog(CheckInOptionActivityNew.this, getString(R.string.warning), "Do you want to check out?", "Yes", "No", "checkout");
			break;
			
		default:
			break;
		}
	}
	
	private void clickCheckInOptionsPreSaler(int position){
		Intent intent = null;
		switch (position) {
		case 0:	//ll_storecheck
			intent = new Intent(CheckInOptionActivityNew.this, CaptureInventoryActivity.class);
			intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
			intent.putExtra("mallsDetails",mallsDetails);
			intent.putExtra("from", "checkin");
			intent.putExtra("checkType", AppConstants.Capture_Inventory);
			startActivity(intent);
			break;
			
		case 1: //ll_salesorder
			intent = new Intent(CheckInOptionActivityNew.this, SalesManRecommendedOrder.class);
			intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
			intent.putExtra("mallsDetails",mallsDetails);
			intent.putExtra("from", "checkin");
			
			if(isPreSaler == 1)
				intent.putExtra("TRX_TYPE", TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER());
			else
				intent.putExtra("TRX_TYPE", TrxHeaderDO.get_TRXTYPE_SALES_ORDER());
			
			intent.putExtra("TRX_SUB_TYPE", TrxHeaderDO.get_TRX_SUBTYPE_SALES_ORDER());
			startActivity(intent);
			break;
			
		case 2: //ll_collection
			intent = new Intent(CheckInOptionActivityNew.this, PendingInvoices.class);
			intent.putExtra("arcollection", true);
			intent.putExtra("mallsDetails", mallsDetails);
			intent.putExtra("fromMenu", true);
			intent.putExtra("AR", true);
			startActivity(intent);
			break;
			
		case 3: //ll_returnorder
			intent  =	new Intent(CheckInOptionActivityNew.this,SalesmanReturnOrder.class);
			intent.putExtra("mallsDetails", mallsDetails);
			intent.putExtra("from", "checkINOption");
			startActivity(intent);
			break;
			
		case 4: //ll_tasks
			showTasksDialog();
			break;
			
		case 5: // for Payment Summary Report	ll_CustomerInfo
			intent =	new Intent(CheckInOptionActivityNew.this,  CustomerPaymentSummaryActivity.class);
			intent.putExtra("mallsDetails", mallsDetails);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
	private void updateData()
	{
		if(vecstatusDO!=null&&vecstatusDO.size()>0)
		{
			if(vecstatusDO.contains(AppConstants.Type_StoreCheck))
			{
				checkInOptionAdapter.refreshStoreChecked(1);
			}
			else
			{
				checkInOptionAdapter.refreshStoreChecked(0);
			}
		}
		else
		{
			checkInOptionAdapter.refreshStoreChecked(0);
		}
	}
	
	private void checkBlockedCustomer()
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
		if(mallsDetails.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
		{
			checkBlockedCustomer();
		}
		else
		{
		}
	}
	private PopupWindow pendingInvoicesPopup;
	private void showPendingInvoicePopup(ArrayList<PendingInvicesDO> vecPendingInvoices)
	{
		pendingInvoicesPopup = new PopupWindow(CheckInOptionActivityNew.this);
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
				Intent intent = new Intent(CheckInOptionActivityNew.this, PendingInvoices.class);
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
				performCheckouts(true);
			}
		});
		
		pendingInvoicesPopup.showAtLocation(llCheckINOption, Gravity.CENTER, 0, 0);
	}
	private class PendingInvoiceAdapter extends BaseAdapter
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
			setTypeFaceRobotoNormal(llLayout);
			return llLayout;
		}
	}
	
	@Override
	public void onBackPressed() 
	{
		showCustomDialog(this, getString(R.string.warning), "Do you want to check out?", "Yes", "No", "checkout");
	}
	
	private void showTasksDialog()
	{
		final Vector<String> vecInitiativeIds = new InitiativesDA().getAllInitiativesIdsForCustomer(mallsDetails.site);
		int count = new StaticSurveyDL().getCompletedSurveyCount(preference.getStringFromPreference(Preference.INT_USER_ID,""));
		
		 LinearLayout viewDetails = (LinearLayout)LayoutInflater.from(getApplicationContext()).inflate(R.layout.tasks_popup, null);
	        
		 LinearLayout llSurveyExecution     = (LinearLayout)viewDetails.findViewById(R.id.llSurveyExecution);
		 LinearLayout llInitiatives         = (LinearLayout)viewDetails.findViewById(R.id.llInitiatives);
		 LinearLayout llCaptureCompetitors  = (LinearLayout)viewDetails.findViewById(R.id.llCaptureCompetitors);
		 TextView tvInitiativeCount 		= (TextView)viewDetails.findViewById(R.id.tvInitiativeCount);
		 TextView tvSurveyExecutionCount 	= (TextView)viewDetails.findViewById(R.id.tvSurveyExecutionCount);
		 TextView tvTaskHeader 				= (TextView)viewDetails.findViewById(R.id.tvTaskHeader);
		 
		 if(vecInitiativeIds != null )
			 tvInitiativeCount.setText(vecInitiativeIds.size()+"");
		 
		 tvSurveyExecutionCount.setText(""+count);
		 
		 final Dialog dialogDetails = new Dialog(CheckInOptionActivityNew.this, android.R.style.Theme_Holo_Dialog);
		 dialogDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 dialogDetails.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		 dialogDetails.addContentView(viewDetails, new LayoutParams(preference.getIntFromPreference(AppConstants.Device_Display_Width, AppConstants.DEVICE_DISPLAY_WIDTH_DEFAULT) - 160, LayoutParams.WRAP_CONTENT));
		 dialogDetails.show();
		 
		 
		 llCaptureCompetitors.setOnClickListener(new OnClickListener() 
		 {
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(CheckInOptionActivityNew.this, CompetitorsListActivity.class);
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
					Intent intent = new Intent(CheckInOptionActivityNew.this, InitiativesListActivity.class);
					intent.putExtra("Object", mallsDetails);
					startActivity(intent);
					dialogDetails.dismiss();
				}
				else
				{
					dialogDetails.dismiss();
					showCustomDialog(CheckInOptionActivityNew.this, "Alert", "No Initiatives for this visit.", "Ok", "", "Initiatives");
				}
			}
		});
		 
		 llSurveyExecution.setOnClickListener(new OnClickListener() 
		 {
			
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(CheckInOptionActivityNew.this, SurveyNewActivity.class);
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
		statusDO.Customersite =mallsDetails.site;
		statusDO.Date = CalendarUtils.getCurrentDate();
		statusDO.Visitcode=mallsDetails.VisitCode;
		statusDO.JourneyCode = mallsDetails.JourneyCode;
		statusDO.Status = "1";
		statusDO.Action = AppConstants.Action_CheckIn;
		statusDO.Type = AppConstants.Type_StoreCheck;
		new StatusDA().insertOptionStatus(statusDO);			
	}
}
