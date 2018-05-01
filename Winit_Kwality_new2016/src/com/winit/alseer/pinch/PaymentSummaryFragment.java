package com.winit.alseer.pinch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.common.CustomBuilderProduct;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.VehicleDA;
import com.winit.alseer.salesman.dataobject.AgencyNewDo;
import com.winit.alseer.salesman.dataobject.Customer_InvoiceDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.PaymentDetailDO;
import com.winit.alseer.salesman.dataobject.PaymentHeaderDO;
import com.winit.alseer.salesman.dataobject.PendingInvicesDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;
import com.winit.sfa.salesman.OrderSummaryDetail;
import com.winit.sfa.salesman.WoosimPrinterActivity;

@SuppressLint("ValidFragment")
public class PaymentSummaryFragment extends Fragment {

	private HashMap<String,Customer_InvoiceDO> hmItems;
	public  PaymentDetailAdapter adapterForCapture;
	private ExpandableListView expandableList;
	private TextView tvNoItem;
	private LayoutInflater inflater;
	 
	//====================================newly added for food===============================kwality =200 and pic = 100
		 AgencyNewDo AgnCustom;
		 private Vector<AgencyNewDo> vecagn= null;
		 int printTypeIce =100;
		 private Vector<String> printOptions= null;
		 Context context;
	 

	
	@SuppressLint("ValidFragment")
	public PaymentSummaryFragment(HashMap<String,Customer_InvoiceDO> hmItems, Context context) 
	{
		this.hmItems = hmItems;
		this.context=context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

//	@Override
//	public void onResume() {
//		super.onResume();
//	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		LinearLayout llMain = (LinearLayout) inflater.inflate(R.layout.fragment_payment_summary, null);
		this.inflater 	  = inflater;
		expandableList 	  = (ExpandableListView) llMain.findViewById(R.id.expandableList);
		tvNoItem		  = (TextView) llMain.findViewById(R.id.tvNoItem);
		
		adapterForCapture = new PaymentDetailAdapter(hmItems);
		expandableList.setAdapter(adapterForCapture);
		
		if(hmItems!=null && hmItems.size()>0)
		{
			expandableList.setVisibility(View.VISIBLE);
			tvNoItem.setVisibility(View.GONE);
		}
		else
		{
			tvNoItem.setText("No payments collected.");
			expandableList.setVisibility(View.GONE);
			tvNoItem.setVisibility(View.VISIBLE);
		}
		
		((BaseActivity) getActivity()).setTypeFaceRobotoNormal(llMain);
		return llMain;
	}
	
	
	
	public class PaymentDetailAdapter extends BaseExpandableListAdapter {
		
		private Vector<String> vecKeys;
		private Vector<Customer_InvoiceDO> vecCustomerInvoiceDOs;
		
		private HashMap<String,Customer_InvoiceDO> hmItemsL;
		public PaymentDetailAdapter(
				HashMap<String,Customer_InvoiceDO> hmItems) {
			hmItemsL=hmItems;
			initializeCategories();
		}

		public void initializeCategories(){
			if (hmItemsL != null && !hmItemsL.isEmpty()) {
				vecCustomerInvoiceDOs = new Vector<Customer_InvoiceDO>();
				vecKeys = new Vector<String>();
				Set<String> set = hmItemsL.keySet();
				Iterator<String> iterator = set.iterator();
				while (iterator.hasNext())
					vecKeys.add(iterator.next());
				for (int i = 0; i < vecKeys.size(); i++) {
					vecCustomerInvoiceDOs.add(hmItemsL.get(vecKeys.get(i)));
				}
//				sort(vecKeys);
				sortByDate(vecCustomerInvoiceDOs);
			}
		}

		@Override
		public int getGroupCount() {
			
//			if(vecKeys!=null && vecKeys.size()>0)
//				return vecKeys.size();
			
			if(vecCustomerInvoiceDOs!=null && vecCustomerInvoiceDOs.size()>0)
				return vecCustomerInvoiceDOs.size();
			
			return 0;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return 1;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
//			Customer_InvoiceDO customer_InvoiceDO = hmItemsL.get(vecKeys.get(groupPosition));
			convertView = inflater .inflate(R.layout.list_item_header_small, null);
//			TextView tvCustomerName = (TextView) convertView.findViewById(R.id.tvReceiptNO);
//			if(customer_InvoiceDO!=null)
//				tvCustomerName.setText("Receipt NO: "+customer_InvoiceDO.receiptNo);
			convertView.setVisibility(View.GONE);
			expandableList.expandGroup(groupPosition);
			expandableList.setDividerHeight(-25);
			return convertView;
		}

		@Override
		public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			final ViewHolder viewHolder;
			
			final Customer_InvoiceDO customer_InvoiceDO = vecCustomerInvoiceDOs.get(groupPosition);
//			final Customer_InvoiceDO customer_InvoiceDO = hmItemsL.get(vecKeys.get(groupPosition));
			if(convertView == null)
			{
				convertView = inflater.inflate( R.layout.payment_summary_list_item_cell, null);
				viewHolder = new ViewHolder();
				
				viewHolder.tvReceiptNO = (TextView) convertView.findViewById(R.id.tvReceiptNO);
				viewHolder.tvReceiptNOValue = (TextView) convertView.findViewById(R.id.tvReceiptNOValue);
				
				viewHolder.tvCustomerName 		  = (TextView) convertView.findViewById(R.id.tvCustomerName);
				
				viewHolder.tvCashAmountHeader     = (TextView) convertView.findViewById(R.id.tvCashAmountHeader);
				viewHolder.tvCashAmount 		   = (TextView) convertView.findViewById(R.id.tvCashAmount);
				viewHolder.tvTotalInvoiceAmount    = (TextView) convertView.findViewById(R.id.tvTotalInvoiceAmount);
				viewHolder.tvTotalInvoiceAmountTag = (TextView) convertView.findViewById(R.id.tvTotalInvoiceAmountTag);
				viewHolder.llInvoiceDetailsSale   = (LinearLayout) convertView.findViewById(R.id.llInvoiceDetailsSale);
				viewHolder.llInvoiceDetailsReturn = (LinearLayout) convertView.findViewById(R.id.llInvoiceDetailsReturn);
				viewHolder.llCheck				   = (LinearLayout) convertView.findViewById(R.id.llCheck);
				viewHolder.llCash				   = (LinearLayout) convertView.findViewById(R.id.llCash);
				viewHolder.tvPaymentDate		    = (TextView) convertView.findViewById(R.id.tvPaymentDate);
				viewHolder.ivLineCheckTopBar		= (ImageView) convertView.findViewById(R.id.ivLineCheckTopBar);
				viewHolder.ivPrintReceipt		= (ImageView) convertView.findViewById(R.id.ivPrintReceipt);
				
				convertView.setTag(viewHolder);
			}
			else
				viewHolder = (ViewHolder) convertView.getTag();
			
			if(customer_InvoiceDO.paymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
			{
				viewHolder.llCheck.setVisibility(View.GONE);
				viewHolder.llCash.setVisibility(View.VISIBLE);
				viewHolder.ivLineCheckTopBar.setVisibility(View.GONE);
			}
			else
			{
				viewHolder.llCheck.removeAllViews();
				viewHolder.llCheck.setVisibility(View.VISIBLE);
				viewHolder.llCash.setVisibility(View.GONE);
				viewHolder.ivLineCheckTopBar.setVisibility(View.VISIBLE);
				
				for(int i=0; customer_InvoiceDO.vecChequeDetails != null &&  i<customer_InvoiceDO.vecChequeDetails.size(); i++ )
				{
					NameIDDo objDetailDO 	    		= 	customer_InvoiceDO.vecChequeDetails.get(i);
					LinearLayout llChequeDetailCell 	= 	(LinearLayout) inflater.inflate(R.layout.cheque_details_cell, null);
					TextView tvCheckNumber 				= 	(TextView) llChequeDetailCell.findViewById(R.id.tvCheckNumber);
					TextView tvCheckAmount 				= 	(TextView) llChequeDetailCell.findViewById(R.id.tvCheckAmount);
					TextView tvDate 					= 	(TextView) llChequeDetailCell.findViewById(R.id.tvDate);
					TextView tvBankNameNumber 			= 	(TextView) llChequeDetailCell.findViewById(R.id.tvBankNameNumber);
					
					ImageView ivDivCheckItem			= 	(ImageView) llChequeDetailCell.findViewById(R.id.ivDivCheckItem);
					
					tvCheckNumber.setText(objDetailDO.chequeNumber);
					tvCheckAmount.setText(((BaseActivity)getActivity()).decimalFormat.format(StringUtils.getFloat(objDetailDO.chequeAmount)));
					
//					if(customer_InvoiceDO.chequeDate != null && customer_InvoiceDO.chequeDate.contains("T"))
//						tvDate.setText(customer_InvoiceDO.chequeDate.split("T")[0]);
//					else if(customer_InvoiceDO.chequeDate != null && customer_InvoiceDO.chequeDate.contains(" "))
//						tvDate.setText(customer_InvoiceDO.chequeDate.split(" ")[0]);
//					else
						tvDate.setText(CalendarUtils.getDateToShow(objDetailDO.chequeDate));
					tvBankNameNumber.setText(objDetailDO.BankName);
					
					if(i == customer_InvoiceDO.vecChequeDetails.size()-1)
						ivDivCheckItem.setVisibility(View.GONE);
					
					viewHolder.llCheck.addView(llChequeDetailCell, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
				}
			}
			viewHolder.ivPrintReceipt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					

					//===========================================newly added========================================	
					
					if(customer_InvoiceDO.Division==1 || customer_InvoiceDO.Division==2)
					{
						if(customer_InvoiceDO.Division==1  && vecagn.get(1)!=null )
							vecagn.get(1).AgencyName="Print-Kwality";
						else
							vecagn.get(1).AgencyName="Print-TPT";
						CustomBuilderProduct builder = new CustomBuilderProduct(context, "Please Select\nRequest Type", true);
						builder.setSingleChoiceItems(vecagn, null,new CustomBuilderProduct.OnClickListener()
						 {
							
							@Override
							public void onClick(CustomBuilderProduct builder, Object selectedObject) {
								if(selectedObject!=null)
									AgnCustom = (AgencyNewDo) selectedObject;
								builder.dismiss();
								
								if(AgnCustom!=null && AgnCustom.Priority==200)
								{
									printTypeIce= 200;
								}
								else
									printTypeIce= 100;
								
								printeReceipt(customer_InvoiceDO);

							}
							});
							
							builder.show();	
					}
					else{
						printeReceipt(customer_InvoiceDO);	
					}
//					printeReceipt(customer_InvoiceDO);
				}
			});
			viewHolder.tvPaymentDate.setText(CalendarUtils.getDateToShow(customer_InvoiceDO.reciptDate));
//			viewHolder.tvPaymentDate.setText(customer_InvoiceDO.reciptDate+""/*CalendarUtils.getFormattedSummaryDate(customer_InvoiceDO.reciptDate+"")*/);
			((BaseActivity)getActivity()).setTypeFaceRobotoNormal((ViewGroup)convertView);
			viewHolder.tvCashAmountHeader.setTypeface(AppConstants.Roboto_Condensed);
			viewHolder.tvReceiptNOValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
			
			viewHolder.tvReceiptNO.setText("Receipt No: ");
			viewHolder.tvReceiptNOValue.setText(""+customer_InvoiceDO.receiptNo);
			
			viewHolder.tvCustomerName.setText(customer_InvoiceDO.siteName+"["+customer_InvoiceDO.customerSiteId+"]");
			
			viewHolder.tvCashAmount.setText(((BaseActivity)getActivity()).decimalFormat.format(StringUtils.getFloat(customer_InvoiceDO.amount)));
			viewHolder.tvTotalInvoiceAmount.setText(((BaseActivity)getActivity()).decimalFormat.format(StringUtils.getFloat(customer_InvoiceDO.invoiceTotal)));
			
			viewHolder.llInvoiceDetailsSale.removeAllViews();
//			viewHolder.llInvoiceDetailsReturn.removeAllViews();
			for(int i=0; customer_InvoiceDO.vecPaymentDetailDOs != null &&  i<customer_InvoiceDO.vecPaymentDetailDOs.size(); i++ )
			{
				PaymentDetailDO objDetailDO 	    = 	customer_InvoiceDO.vecPaymentDetailDOs.get(i);
				LinearLayout llPaymentDetailCell 	= 	(LinearLayout) inflater.inflate(R.layout.payment_details_cell, null);
				TextView tvInvoiceNumber 			= 	(TextView) llPaymentDetailCell.findViewById(R.id.tvInvoiceNumber);
				TextView tvInvoiceAmmount 			= 	(TextView) llPaymentDetailCell.findViewById(R.id.tvInvoiceAmmount);
				
				ImageView ivDivChild = (ImageView)llPaymentDetailCell.findViewById(R.id.ivDivChild);
				tvInvoiceNumber.setTypeface(AppConstants.Roboto_Condensed_Bold);
				tvInvoiceAmmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
				
				if(i == customer_InvoiceDO.vecPaymentDetailDOs.size()-1)
					ivDivChild.setVisibility(View.GONE);
				
				tvInvoiceNumber.setText(objDetailDO.invoiceNumber);
				float amt = StringUtils.getFloat(((BaseActivity)getActivity()).decimalFormat.format(StringUtils.getFloat(objDetailDO.invoiceAmount)));
//				if(amt > 0)
//				{
					tvInvoiceAmmount.setText(""+amt);
					viewHolder.llInvoiceDetailsSale.addView(llPaymentDetailCell, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
//				}
//				else
//				{
//					tvInvoiceAmmount.setText(""+Math.abs(amt));
//					viewHolder.llInvoiceDetailsReturn.addView(llPaymentDetailCell, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
//				}
//				
				llPaymentDetailCell.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//to do
//						showDetails();
					}
				});
			}
			return convertView;
		}


		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
	}
	
	public void sort(Vector<String> vec){
		Collections.sort(vec, new Comparator<String>() {
		    @Override
		   public int compare(String s1, String s2) {
		    	return s1.compareToIgnoreCase(s2);
		    }
		});
	}
	public void sortByDate(Vector<Customer_InvoiceDO> vec){
		Collections.sort(vec, new Comparator<Customer_InvoiceDO>() {
		    @Override
		   public int compare(Customer_InvoiceDO s1, Customer_InvoiceDO s2) {
		    	return s2.reciptDate.compareToIgnoreCase(s1.reciptDate);
		    }
		});
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
	    super.setUserVisibleHint(isVisibleToUser);
	    if (isVisibleToUser && adapterForCapture != null) {
	    	adapterForCapture.notifyDataSetChanged();
	    }else{
	    }
	}
	
	private class ViewHolder
	{
		TextView tvReceiptNOValue,tvReceiptNO,tvCustomerName,tvCashAmountHeader,tvPaymentDate,tvCashAmount, tvTotalInvoiceAmount, tvTotalInvoiceAmountTag;
		LinearLayout llCheck,llCash,llInvoiceDetailsSale,llInvoiceDetailsReturn;
		ImageView ivLineCheckTopBar,ivPrintReceipt;
	}
	JourneyPlanDO mallsDetails;
	PaymentHeaderDO paymentHeaderDO;
	ArrayList<PendingInvicesDO> arrInvoiceNumbers;
	private void printeReceipt(final Customer_InvoiceDO customer_InvoiceDO) {
		((BaseActivity)getActivity()).showLoader(getString(R.string.please_wait));
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					mallsDetails = new CustomerDetailsDA().getCustometBySiteId(customer_InvoiceDO.customerSiteId);
					
					paymentHeaderDO						= 	new PaymentHeaderDO();
					paymentHeaderDO.rowStatus 			=  "1";
					paymentHeaderDO.receiptId 			=  customer_InvoiceDO.receiptNo;
					paymentHeaderDO.preReceiptId 		=  customer_InvoiceDO.receiptNo;
					paymentHeaderDO.paymentDate 		=  customer_InvoiceDO.reciptDate;
					paymentHeaderDO.siteId 				=  mallsDetails.site;
					paymentHeaderDO.empNo 				=  ((BaseActivity)getActivity()).preference.getStringFromPreference(Preference.EMP_NO, "");
					paymentHeaderDO.amount 				=  ""+((BaseActivity)getActivity()).decimalFormat.format(StringUtils.getFloat(customer_InvoiceDO.amount));
					paymentHeaderDO.currencyCode 		=  ((BaseActivity)getActivity()).curencyCode+"";
					paymentHeaderDO.rate 				=  "1";
					paymentHeaderDO.visitCode 			=  ""+mallsDetails.VisitCode;
					paymentHeaderDO.paymentStatus 		=  "0";
					paymentHeaderDO.status = "0";
					paymentHeaderDO.salesOrgCode		=   ((BaseActivity)getActivity()).preference.getStringFromPreference(Preference.ORG_CODE,"");
					paymentHeaderDO.appPayementHeaderId	= 	paymentHeaderDO.appPaymentId;
					paymentHeaderDO.paymentType 		=	"Collection";
					paymentHeaderDO.salesmanCode 		=	mallsDetails.salesmanCode;
					
//===========================newly added for food==================================
					paymentHeaderDO.Division=customer_InvoiceDO.Division;
					
					if(paymentHeaderDO.vehicleNo == null || paymentHeaderDO.vehicleNo.equalsIgnoreCase(""))
						paymentHeaderDO.vehicleNo = new VehicleDA().getVechicleNO(paymentHeaderDO.empNo);
					if(!customer_InvoiceDO.paymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
					{
						for(int i=0; customer_InvoiceDO.vecChequeDetails != null &&  i<customer_InvoiceDO.vecChequeDetails.size(); i++ )
						{
							NameIDDo objDetailDO 	    		= 	customer_InvoiceDO.vecChequeDetails.get(i);
							PaymentDetailDO payment = new PaymentDetailDO();
							payment.PaymentTypeCode = customer_InvoiceDO.paymentType;//nameIDDo
							payment.ChequeNo = objDetailDO.chequeNumber==null?"":objDetailDO.chequeNumber;
							payment.ChequeDate = objDetailDO.chequeDate==null?"":objDetailDO.chequeDate;
							payment.BankName = objDetailDO.BankName==null?"":objDetailDO.BankName;
							payment.Amount = objDetailDO.chequeAmount==null?"":objDetailDO.chequeAmount;
							paymentHeaderDO.vecPaymentDetails.add(payment);
						}
					}
					arrInvoiceNumbers = new ArrayList<PendingInvicesDO>();
					if(customer_InvoiceDO != null && customer_InvoiceDO.vecPaymentDetailDOs != null && customer_InvoiceDO.vecPaymentDetailDOs.size() > 0)
					{
						for(PaymentDetailDO paymentDetailDO:customer_InvoiceDO.vecPaymentDetailDOs){
							PendingInvicesDO obj = new PendingInvicesDO();
							obj.payingAmount=paymentDetailDO.invoiceAmount;
							obj.balance=paymentDetailDO.invoiceAmount;
							obj.invoiceNo=paymentDetailDO.invoiceNumber;
//						obj.invoiceDate=CalendarUtils.getOrderPostDate();
							obj.invoiceDate=paymentDetailDO.invoiceDate;
							arrInvoiceNumbers.add(obj);
						}
					}
					
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							
														
							gotoWoomsimprinterActivity(customer_InvoiceDO);
						}

						
					});
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private Dialog dialogDetails;
	/**
	 * For show payment details.
	 */
	private void showDetails()
	{
		// Details
		LinearLayout viewDetails = (LinearLayout)inflater.inflate(R.layout.competitor_details, null);

//		btn_submit       = (Button)viewDetails.findViewById(R.id.btnSubmit);
//		ivCompetitor 	 = (ImageView)viewDetails.findViewById(R.id.ivCompetitor);
//		//			btnCaptureImage  = (Button)viewDetails.findViewById(R.id.btnCaptureImage);
//		tvBrandOur 		 = (TextView)viewDetails.findViewById(R.id.tvBrandOur);
//		tvCompany 		 = (TextView)viewDetails.findViewById(R.id.tvCompany);
//		tvBrandComp 		 = (TextView)viewDetails.findViewById(R.id.tvBrandComp);
//		tvPrice          = (TextView)viewDetails.findViewById(R.id.tvPrice);
//		tvDateTime       = (TextView)viewDetails.findViewById(R.id.tvDateTime);
//		tvNote           = (TextView)viewDetails.findViewById(R.id.tvNote);
//
//		vVCompetitor     = (PlayVideoView)viewDetails.findViewById(R.id.vVCompetitor);
//		rlVideo 		 = (RelativeLayout)viewDetails.findViewById(R.id.rlVideo);
//		btnPlay			 = (ImageView)viewDetails.findViewById(R.id.btnPlay);

		dialogDetails = new Dialog(getActivity(), android.R.style.Theme_Holo_Dialog);
		dialogDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogDetails.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogDetails.addContentView(viewDetails, new LayoutParams(AppConstants.DEVICE_WIDTH-20, AppConstants.DEVICE_HEIGHT-20));
		dialogDetails.show();

		((BaseActivity) getActivity()).setTypeFaceRobotoNormal(viewDetails);
	}
	
	//=================================newly added food =============================
	
	private void gotoWoomsimprinterActivity(Customer_InvoiceDO customer_InvoiceDO) {
		// TODO Auto-generated method stub
		((BaseActivity)getActivity()).hideLoader();
		Intent intent = new Intent(getActivity(), WoosimPrinterActivity.class);
		intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_RECEIPT);
		
		intent.putExtra("totalAmount", ((BaseActivity)getActivity()).decimalFormat.format(StringUtils.getFloat(customer_InvoiceDO.amount)));
		intent.putExtra("strReceiptNo", customer_InvoiceDO.receiptNo);
		intent.putExtra("selectedAmount", ((BaseActivity)getActivity()).decimalFormat.format(StringUtils.getFloat(customer_InvoiceDO.amount)));
		intent.putExtra("paymentHeaderDO", paymentHeaderDO);
		intent.putExtra("mallsDetails", mallsDetails);
		intent.putExtra("arrInvoiceNumbers", arrInvoiceNumbers);
		if(customer_InvoiceDO.paymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
			intent.putExtra("paymode", AppConstants.PAYMENT_NOTE_CASH);
		else
			intent.putExtra("paymode", AppConstants.PAYMENT_NOTE_CHEQUE);
		
		//======================================== Newly Added by for food=========================							
		intent.putExtra("PrintTypeIce", printTypeIce);
//===================================================================	
		startActivityForResult(intent, 1000);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		vecagn=new Vector<AgencyNewDo>();
		AgencyNewDo agencyone=new AgencyNewDo();
		agencyone.AgencyId="1";
		agencyone.AgencyName="Print-PIC";
		agencyone.Priority=100;
		vecagn.add(agencyone);
		AgencyNewDo agencytwo=new AgencyNewDo();
		agencytwo.AgencyId="2";
		agencytwo.AgencyName="Print-TPT";
		agencytwo.Priority=200;
		vecagn.add(agencytwo);
//        printOptions=new Vector<String>();
//		 printOptions.add(0, "Kwality");
//		 printOptions.add(1,"PIC");
	
		
	}
	
}