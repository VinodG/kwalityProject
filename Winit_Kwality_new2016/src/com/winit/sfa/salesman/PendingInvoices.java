package com.winit.sfa.salesman;
import java.util.ArrayList;
import java.util.Vector;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.ARCollectionDA;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.PendingInvicesDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;


public class PendingInvoices extends BaseActivity
{
	//declaration of variables
	private LinearLayout llArCollection,llMiddleLayout,llNxt,llHeader;
	private TextView tvTotalAmount, tvTotalAmountValue , tvTotalAmountDue, tvTotalDueValue ,tvAmountDue, tvUnpaid, 
					 tvARHead ,tvCreditLimit , tvCustomerName,tvCustomerLocation, tvAmount,
					 tvCreditLimitValue, tvCreditTime, tvCreditTimeValue, tvNoPendingInvoices;
	private TextView tvNxt, tvOnAccount;
	private UnPaidInvoiceAdapter objUnPaidInvoiceAdapter;
	private ListView lvUnPaidInvoices;
	private ImageView ivCheckAll;
	private float  amountDue = 0 , selectedAmount = 0;
	private ArrayList<Integer> isClicked;
	private ARCollectionDA arCollectionBL;
	private ArrayList<PendingInvicesDO> vecPendingInvoices;
	private ArrayList<PendingInvicesDO> arrInvoiceNumbers ;
	private View etInvoiceNumber = null;
	private CustomDialog tempcustomDialogs;
	private JourneyPlanDO mallsDetails;
	private boolean isARCollection = false, isExceed = false, isPartial, isFromPayment;
	private String reason = "";
	private boolean isFromOrderPreview=false;
	private Button btnPrint;
	private ImageView ivDivHeaderPrint;
	private TrxHeaderDO trxHeaderDO;
	private int Division = 0;
	private boolean frommenu=false;
	
	@SuppressWarnings("deprecation")
	@Override
	public void initialize()
	{
		llArCollection 		= (LinearLayout)getLayoutInflater().inflate(R.layout.ar_collection, null);
		llBody.addView(llArCollection,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		intialiseControls();
		setTypeFaceRobotoNormal(llArCollection);
		tvARHead.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvOnAccount.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvNxt.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCustomerName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		//this vector will hold all the invoice selected from the AR collection and current invoice number
		arrInvoiceNumbers 	= new ArrayList<PendingInvicesDO>();
		arCollectionBL		= new ARCollectionDA();
		
		//getting extras
		if(getIntent().getExtras()!= null)
		{
			mallsDetails		= 	(JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			isARCollection 		= 	getIntent().getExtras().getBoolean("AR");
			isExceed 			= 	getIntent().getExtras().getBoolean("isExceed");
			isFromOrderPreview	= 	getIntent().getExtras().getBoolean("isFromOrderPreview");
			isFromPayment		= 	getIntent().getExtras().getBoolean("isFromPayment");
			trxHeaderDO			= 	(TrxHeaderDO) getIntent().getExtras().get("trxHeaderDO");
			
			if(getIntent().hasExtra("fromMenu"))
				frommenu=getIntent().getExtras().getBoolean("fromMenu");
			
			if(getIntent().hasExtra(AppConstants.DIVISION))
				Division = getIntent().getExtras().getInt(AppConstants.DIVISION);
		}
		vecPendingInvoices  = new ArrayList<PendingInvicesDO>();
		lvUnPaidInvoices = new ListView(PendingInvoices.this);
		lvUnPaidInvoices.setCacheColorHint(0);
		lvUnPaidInvoices.setVerticalFadingEdgeEnabled(false);
		lvUnPaidInvoices.setSelector(R.color.transparent);
		lvUnPaidInvoices.setVerticalScrollBarEnabled(false);
//		lvUnPaidInvoices.setDivider(getResources().getDrawable(R.drawable.saparetor));
		lvUnPaidInvoices.setDivider(null);
		objUnPaidInvoiceAdapter = new UnPaidInvoiceAdapter(new ArrayList<PendingInvicesDO>());
		lvUnPaidInvoices.setAdapter(objUnPaidInvoiceAdapter);
		
		tvCustomerName.setText(mallsDetails.siteName+" ["+mallsDetails.site+"]");
		tvCustomerLocation.setText(getAddress(mallsDetails));
		
		showLoader(getString(R.string.please_wait));
		
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				vecPendingInvoices  = arCollectionBL.getPendingInvoices(mallsDetails.site,Division);
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						objUnPaidInvoiceAdapter.refreshList(vecPendingInvoices);
					}
				});
			}
		}).start();
		
		ivCheckAll.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(v.getTag().toString().equalsIgnoreCase("unchecked"))
				{
					ivCheckAll.setTag("checked");
					ivCheckAll.setImageResource(R.drawable.checkbox_white);
					isClicked.clear();
					for(int j = 0; j < vecPendingInvoices.size() ; j++)
					{
						isClicked.add(j);
					}
					objUnPaidInvoiceAdapter.refresh();
				}
				else
				{
					ivCheckAll.setTag("unchecked");
					ivCheckAll.setImageResource(R.drawable.uncheckbox_white);
					isClicked.clear();
					objUnPaidInvoiceAdapter.refresh();
				}
			}
		});
		tvNxt.setVisibility(View.VISIBLE);
			tvNxt.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					int count 	= 0;
					count 		= getCheckedItemsCount();
					
					if(count > 0)
					{
						/*if(selectedAmount < 0)
							showCustomDialog(PendingInvoices.this, getString(R.string.warning), "Return invoice amount should not be greater than sales invoice amount.",getString(R.string.OK),null, "");
						else */if(arrInvoiceNumbers != null && arrInvoiceNumbers.size() >= vecPendingInvoices.size())
						{
							if(!isPartial)
							{
//								Intent intent = new Intent(PendingInvoices.this, ReceivePaymentBySalesman.class);
								Intent intent = new Intent(PendingInvoices.this, ReceivePaymentActivity.class);
								intent.putExtra("arcollection", isARCollection);
								intent.putExtra("selectedAmount", selectedAmount);
								intent.putExtra("InvoiceNumbers", arrInvoiceNumbers);
								intent.putExtra("mallsDetails", mallsDetails);
								intent.putExtra("isFromOrderPreview", isFromOrderPreview);
								intent.putExtra("isExceed", isExceed);
								intent.putExtra("isFromPayment", isFromPayment);
								intent.putExtra("strPaymentType", "Pending Invoice");
								intent.putExtra("trxHeaderDO", trxHeaderDO);
								intent.putExtra(AppConstants.DIVISION, Division);
								intent.putExtra("fromMenu", frommenu);
								showPaymentModePopup(intent);
							}
							else
								showOptionDialog();
						}
						else if(isPartial)
							showOptionDialog();
						else
							showCustomDialog(PendingInvoices.this, getString(R.string.warning), "Do you want to skip unselected invoices?",getString(R.string.Yes),getString(R.string.No), "skip");
					}
					else if(selectedAmount == 0 && isClicked != null && isClicked.size() > 0)
						showCustomDialog(PendingInvoices.this, getString(R.string.warning), "Invoice amount can not be Zero.", getString(R.string.OK),null,  "");
					else
						showCustomDialog(PendingInvoices.this, getString(R.string.warning),"Please select atleast one invoice to make payment.", getString(R.string.OK),null,  "");
				}
			});
			btnPrint.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent(PendingInvoices.this,WoosimPrinterActivity.class);
					intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_PENDING_INVOICE);
					intent.putExtra("arrInvoiceNumbers", vecPendingInvoices);
					intent.putExtra("mallsDetails", mallsDetails);
					startActivity(intent);
				}
			});
		
		tvTotalDueValue.setText(curencyCode+"   "+amountDue);
		llMiddleLayout.addView(lvUnPaidInvoices,  new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		
		if(vecPendingInvoices.size() > 0)
		{
			llHeader.setVisibility(View.VISIBLE);
			tvNoPendingInvoices.setVisibility(View.GONE);
			llNxt.setVisibility(View.VISIBLE);
			btnPrint.setVisibility(View.VISIBLE);
			ivDivHeaderPrint.setVisibility(View.VISIBLE);
		}
		else
		{
			llHeader.setVisibility(View.GONE);
			tvNoPendingInvoices.setVisibility(View.VISIBLE);
			llNxt.setVisibility(View.GONE);
			btnPrint.setVisibility(View.GONE);
			ivDivHeaderPrint.setVisibility(View.GONE);
		}
		tvOnAccount.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showAddInvoicePopup();
			}
		});
		
		btnPrint.setTypeface(Typeface.DEFAULT_BOLD);
		tvNxt.setTypeface(Typeface.DEFAULT_BOLD);
		tvOnAccount.setTypeface(Typeface.DEFAULT_BOLD);
	}

	//function for initializing variables
	private void intialiseControls() 
	{
		llMiddleLayout 		= (LinearLayout)llArCollection.findViewById(R.id.llMiddleLayout);
		ivCheckAll			= (ImageView)llArCollection.findViewById(R.id.ivCheckAll);
		tvTotalAmount 		= (TextView)llArCollection.findViewById(R.id.tvTotalAmount);
		tvTotalAmountDue	= (TextView)llArCollection.findViewById(R.id.tvTotalAmountDue);
		tvAmountDue			= (TextView)llArCollection.findViewById(R.id.tvAmountDue);
		tvUnpaid			= (TextView)llArCollection.findViewById(R.id.tvUnPaid);
		tvARHead			= (TextView)llArCollection.findViewById(R.id.tvPageTitle);
		tvTotalAmountValue  = (TextView)llArCollection.findViewById(R.id.tvTotalAmountValue);
		tvTotalDueValue     = (TextView)llArCollection.findViewById(R.id.tvTotalDueValue);
		tvCreditLimit		= (TextView)llArCollection.findViewById(R.id.tvCredit_Limit);
		tvCreditLimitValue	= (TextView)llArCollection.findViewById(R.id.tvCredit_Limit_value);
		tvCreditTime		= (TextView)llArCollection.findViewById(R.id.tvCredit_Time);
		tvCreditTimeValue	= (TextView)llArCollection.findViewById(R.id.tvCredit_Time_Value);
		tvNoPendingInvoices	= (TextView)llArCollection.findViewById(R.id.tvNoPendingInvoice);
		tvAmount			= (TextView)llArCollection.findViewById(R.id.tvAmount);
		llNxt				= (LinearLayout)llArCollection.findViewById(R.id.llNxt);
		llHeader			= (LinearLayout)llArCollection.findViewById(R.id.llHeader);
		tvNxt 				= (TextView)llArCollection.findViewById(R.id.tvNxt);
		tvOnAccount		= (TextView)llArCollection.findViewById(R.id.tvOnAccount);
		tvCustomerName 		= (TextView)llArCollection.findViewById(R.id.tvCustomerName); 
		tvCustomerLocation	= (TextView)llArCollection.findViewById(R.id.tvCustomerLocation);
		btnPrint			= (Button)llArCollection.findViewById(R.id.btnPrint);
		ivDivHeaderPrint	= (ImageView)llArCollection.findViewById(R.id.ivDivHeaderPrint);
		
		tvARHead.setText("AR Collection");
		//setting type-face on controls 
	
		/*tvARHead.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvTotalAmountValue.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvTotalDueValue.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCreditLimit.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCreditLimitValue.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCreditTime.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCreditTimeValue.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvNoPendingInvoices.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnNxt.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnAddInvoices.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvAmount.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		tvOnAccount.setVisibility(View.VISIBLE);
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
//		tvPrint.setTypeface(AppConstants.Roboto_Condensed_Bold);
//		tvAddInvoices.setTypeface(AppConstants.Roboto_Condensed_Bold);
//		tvNxt.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}
	@Override
	protected void onResume() 
	{
		super.onResume();
//		if(arrInvoiceNumbers != null && arrInvoiceNumbers.size() > 0)
//			arrInvoiceNumbers.clear();
	}
	
	public class UnPaidInvoiceAdapter extends BaseAdapter
	{
		private View view;
		private ArrayList<PendingInvicesDO> vctPendingInvicesDOs;
		public UnPaidInvoiceAdapter(ArrayList<PendingInvicesDO> vecPendingInvicesDOs) 
		{
			vctPendingInvicesDOs = vecPendingInvicesDOs;
			isClicked = new ArrayList<Integer>();
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
		public View getView(final int position, View convertView, ViewGroup arg2) 
		{
			PendingInvicesDO pendingInvicesDO = vctPendingInvicesDOs.get(position);
			final LinearLayout llLayout 	= (LinearLayout)getLayoutInflater().inflate(R.layout.ar_colloection_cell, null);
			Button btnArrow			= (Button)llLayout.findViewById(R.id.btnArrow);
			TextView tvAmount		= (TextView)llLayout.findViewById(R.id.tvAmount);
			TextView tvDescription	= (TextView)llLayout.findViewById(R.id.tvDescription);
			TextView tvDueDate		= (TextView)llLayout.findViewById(R.id.tvDueDate);
			TextView tvHeaderText	= (TextView)llLayout.findViewById(R.id.tvHeaderText);
			TextView tvInvType		= (TextView)llLayout.findViewById(R.id.tvInvType);
			TextView tvInvoiceAmt	= (TextView)llLayout.findViewById(R.id.tvInvoiceAmt);
			
			final EditText etEnterAmount = (EditText)llLayout.findViewById(R.id.etEnterAmount);
			
			final ImageView ivCheck	= (ImageView)llLayout.findViewById(R.id.ivCheck);
			
//			tvDescription.setVisibility(View.GONE);
			tvDueDate.setVisibility(View.GONE);
			btnArrow.setVisibility(View.GONE);
			
			String dueDate = "";
			boolean isDueDateOver = false;
			if(pendingInvicesDO.invoiceDueDate != null && !pendingInvicesDO.invoiceDueDate.contains("0001-01-01"))
			{
				if(pendingInvicesDO.invoiceDueDate.contains("T"))
				{
					dueDate = pendingInvicesDO.invoiceDueDate.split("T")[0];
					String currentDate = CalendarUtils.getCurrentDateAsStringforStoreCheck();
				
					int diff = CalendarUtils.getDiffBtwDatesInDays(currentDate, dueDate);
					if(diff >= 0)
						isDueDateOver = false;
					else
						isDueDateOver = true;
				}
			}
			
			if(isDueDateOver)
				llLayout.setBackgroundColor(Color.parseColor("#30FC5A5A"));
			else
				llLayout.setBackgroundColor(Color.parseColor("#00000000"));
			
			etEnterAmount.setTag(pendingInvicesDO);
			/*etEnterAmount.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvAmount.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvDescription.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvHeaderText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
			
			if(mallsDetails.customerPaymentType != null && mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT) && StringUtils.getFloat(pendingInvicesDO.balance) > 0)
			{
				etEnterAmount.setEnabled(true);
				etEnterAmount.setFocusable(true);
				etEnterAmount.setFocusableInTouchMode(true);				
			}
			else
			{
//				etEnterAmount.setEnabled(false);
				etEnterAmount.setFocusable(false);
				etEnterAmount.setFocusableInTouchMode(false);
			}
			
			final String balance = amountFormate.format(StringUtils.getFloat(pendingInvicesDO.balance));
			if(StringUtils.getFloat(pendingInvicesDO.balance) < 0)
			{
				tvAmount.setText(""+amountFormate.format(StringUtils.getFloat(pendingInvicesDO.totalAmount)));
				tvInvType.setText("Return Invoice");
				tvInvoiceAmt.setText(""+amountFormate.format(StringUtils.getFloat(pendingInvicesDO.lastbalance)));
//				etEnterAmount.setText(amountFormate.format(StringUtils.getFloat(pendingInvicesDO.balance)));
//				tvAmount.setText(Html.fromHtml("<font color = #454545> "+"&nbsp;&nbsp;&nbsp;</font>"+decimalFormat.format(Math.abs(StringUtils.getFloat(pendingInvicesDO.lastbalance)))));
			}
			else 
			{
//				etEnterAmount.setText(amountFormate.format(StringUtils.getFloat(pendingInvicesDO.balance)));
				tvAmount.setText(""+amountFormate.format(StringUtils.getFloat(pendingInvicesDO.lastbalance)));
//				tvAmount.setText(Html.fromHtml("<font color = #454545> "+"&nbsp;&nbsp;&nbsp;</font>"+decimalFormat.format(StringUtils.getFloat(pendingInvicesDO.lastbalance))));
				tvInvType.setText("Sales Invoice");
			}
			tvInvoiceAmt.setText(""+amountFormate.format(StringUtils.getFloat(pendingInvicesDO.totalAmount)));
			if(pendingInvicesDO.invoiceDateToShow!=null && !pendingInvicesDO.invoiceDateToShow.equalsIgnoreCase(""))
			{
				if(!dueDate.equalsIgnoreCase(""))
					tvDescription.setText(pendingInvicesDO.invoiceDateToShow+" - "+CalendarUtils.getFormatedDatefromString(dueDate));
				else
					tvDescription.setText(pendingInvicesDO.invoiceDateToShow);
				tvDescription.setVisibility(View.VISIBLE);
			}
			else
				tvDescription.setVisibility(View.GONE);
			if(pendingInvicesDO.IsOutStanding.equalsIgnoreCase("true"))
			{
				tvHeaderText.setTextColor(getResources().getColor(R.color.alserr_line_green));
				tvHeaderText.setText(pendingInvicesDO.invoiceNo);
			}
			else
			{
				tvHeaderText.setTextColor(getResources().getColor(R.color.gray_dark));
				tvHeaderText.setText(pendingInvicesDO.invoiceNo);
			}
			if(isClicked != null && isClicked.contains(position)){
				ivCheck.setImageResource(R.drawable.checkbox);
//				etEnterAmount.setEnabled(true);
				etEnterAmount.setFocusable(true);
				etEnterAmount.setFocusableInTouchMode(true);
				etEnterAmount.setClickable(true);
				etEnterAmount.setText(""+amountFormate.format((StringUtils.getFloat(pendingInvicesDO.totalAmount)-StringUtils.getFloat(pendingInvicesDO.balance))));
			}
			else{
				etEnterAmount.setText(decimalFormat.format(0));
//				etEnterAmount.setEnabled(false);
				etEnterAmount.setFocusable(false);
				etEnterAmount.setFocusableInTouchMode(false);
				etEnterAmount.setClickable(false);
				ivCheck.setImageResource(R.drawable.uncheckbox);
			}
			
			ivCheck.setTag(position);
			llLayout.setTag(position);
			
			String bal = balance;
			if(bal.contains(","))
				bal = bal.replace(",", "");
			etEnterAmount.setText(""+amountFormate.format((StringUtils.getFloat(pendingInvicesDO.totalAmount) - StringUtils.getFloat(bal))));
			
			llLayout.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					ivCheckAll.setTag("unchecked");
					ivCheckAll.setImageResource(R.drawable.uncheckbox);
					int pos = StringUtils.getInt(v.getTag().toString());
					if(!isClicked.contains(pos))
					{
						isClicked.add(pos);
						ivCheck.setImageResource(R.drawable.checkbox);
					}
					else 
					{
						isClicked.remove((Integer)pos);
						etEnterAmount.setFocusable(false);
						etEnterAmount.setFocusableInTouchMode(false);
						ivCheck.setImageResource(R.drawable.uncheckbox);
					}
					if(isAllselected(isClicked))
					{
						ivCheckAll.setTag("checked");
						ivCheckAll.setImageResource(R.drawable.checkbox_white);
					}
					else
					{
						ivCheckAll.setTag("unchecked");
						ivCheckAll.setImageResource(R.drawable.uncheckbox_white);
					}
				}
			});
			
			ivCheck.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					llLayout.performClick();
				}
			});
			
//			etEnterAmount.setOnClickListener(new OnClickListener()
//			{
//				@Override
//				public void onClick(View v) 
//				{
//					llLayout.performClick();
//					
//					view  = v;
//					new Handler().postDelayed(new Runnable() {
//						@Override
//						public void run() {
//							try {
//								hideKeyBoard(etEnterAmount);
//								boolean isCentre = false;
//								onKeyboardFocus(etEnterAmount,0,false);	
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//							
//							
//						}
//					}, 100);
//				}
//			});
			
//			etEnterAmount.setOnFocusChangeListener(new OnFocusChangeListener() 
//			{
//				@Override
//				public void onFocusChange(View v, boolean hasFocus) 
//				{
//					if (hasFocus) 
//					{
//						view  = v;
//						new Handler().postDelayed(new Runnable() {
//							@Override
//							public void run() {
//								try {
//									hideKeyBoard(etEnterAmount);
//									boolean isCentre = false;
//									onKeyboardFocus(etEnterAmount,0,false);	
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
//								
//								
//							}
//						}, 100);
//					}
//				}
//			});
			etEnterAmount.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence charsequence, int i, int j, int k)
				{
					PendingInvicesDO pendingInvicesDO = (PendingInvicesDO) etEnterAmount.getTag();
					pendingInvicesDO.balance = charsequence.toString();
					
					if(StringUtils.getFloat(pendingInvicesDO.balance) > StringUtils.getFloat(pendingInvicesDO.lastbalance))
					{
						pendingInvicesDO.balance = pendingInvicesDO.lastbalance;
						etEnterAmount.setText(""+pendingInvicesDO.balance);
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence charsequence, int i, int j, int k) 
				{
				}
				
				@Override
				public void afterTextChanged(Editable editable) 
				{
				}
			});
			
			setTypeFaceRobotoNormal(llLayout);
			return llLayout;
		}
		
		public void refreshList(ArrayList<PendingInvicesDO> vctPendingInvicesDOs)
		{
			this.vctPendingInvicesDOs = vctPendingInvicesDOs;
			notifyDataSetChanged();
			if(isAllselected(isClicked))
			{
				ivCheckAll.setTag("checked");
				ivCheckAll.setImageResource(R.drawable.checkbox_white);
			}
			else
			{
				ivCheckAll.setTag("unchecked");
				ivCheckAll.setImageResource(R.drawable.uncheckbox_white);
			}
			
			if(vecPendingInvoices.size() > 0)
			{
				llHeader.setVisibility(View.VISIBLE);
				tvNoPendingInvoices.setVisibility(View.GONE);
				llNxt.setVisibility(View.VISIBLE);
				btnPrint.setVisibility(View.VISIBLE);
				ivDivHeaderPrint.setVisibility(View.VISIBLE);
			}
			hideLoader();
		}
	}
	
	private void showOptionDialog()
	{
		Vector<String> vecStrings = new CommonDA().getReasonBasedOnType(AppConstants.PARTIAL_PAYMENT);
		CustomBuilder builder = new CustomBuilder(PendingInvoices.this, "Select Reason", true);
		builder.setSingleChoiceItems(vecStrings, "", new CustomBuilder.OnClickListener() 
		{
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject) 
			{
				String str = (String) selectedObject;
				reason = str;
				builder.dismiss();
				onButtonYesClick("skip");
		    }
		}); 
		builder.show();
	}
	
	
	
	private int getCheckedItemsCount()
	{
		int count 		  = 	0;
		arrInvoiceNumbers = 	new ArrayList<PendingInvicesDO>();
		selectedAmount 	  = 	0;
		isPartial 		  = 	false;
		 
		 if(isClicked != null && isClicked.size() > 0)
		 {
			 for(int i = 0; i < isClicked.size() ; i++)
			 {
				 if(StringUtils.getFloat(vecPendingInvoices.get(isClicked.get(i)).balance) != 0)
				 {
					 count++;
					 selectedAmount = selectedAmount+StringUtils.getFloat(decimalFormat.format( StringUtils.getFloat(vecPendingInvoices.get(isClicked.get(i)).balance)));
					 
					 PendingInvicesDO pendingInvicesDO = vecPendingInvoices.get(isClicked.get(i));
					 pendingInvicesDO.payingAmount = pendingInvicesDO.balance;
					 arrInvoiceNumbers.add(pendingInvicesDO);
				 }
				 
				 if(StringUtils.getFloat(vecPendingInvoices.get(isClicked.get(i)).balance) != /*StringUtils.getFloat(decimalFormat.format( */StringUtils.getFloat(vecPendingInvoices.get(isClicked.get(i)).lastbalance)/*))*/)
					 isPartial = true;
			 }
		 }
		return count;
	}
	
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("skip"))
		{
			
			Intent intent = new Intent(PendingInvoices.this, ReceivePaymentActivity.class);
			intent.putExtra("arcollection", isARCollection);
			intent.putExtra("selectedAmount", selectedAmount);
			intent.putExtra("InvoiceNumbers", arrInvoiceNumbers);
			intent.putExtra("mallsDetails", mallsDetails);
			intent.putExtra("isFromOrderPreview", isFromOrderPreview);
			intent.putExtra("isExceed", isExceed);
			intent.putExtra("reason", reason);
			intent.putExtra("strPaymentType", "Pending Invoice");
			intent.putExtra("isFromPayment", isFromPayment);
			intent.putExtra("trxHeaderDO", trxHeaderDO);
			intent.putExtra("fromMenu", frommenu);
			intent.putExtra(AppConstants.DIVISION, Division);
			showPaymentModePopup(intent);
//			if(isExceed || isFromPayment)
//				startActivityForResult(intent, 5000);
//			else
//				startActivityForResult(intent, 1000);
		}
		else if(from.equalsIgnoreCase("Unallocated"))
		{
			PendingInvicesDO pendingInvicesDO = new PendingInvicesDO();
			pendingInvicesDO.invoiceNo 		= "Unallocated";
			pendingInvicesDO.balance 		= ((EditText)etInvoiceNumber).getText().toString();
			pendingInvicesDO.lastbalance	= pendingInvicesDO.balance;
			pendingInvicesDO.invoiceDate 	= CalendarUtils.getOrderPostDate();
			pendingInvicesDO.invoiceDateToShow = CalendarUtils.getFormatedDatefromString(pendingInvicesDO.invoiceDate);
			pendingInvicesDO.isNewleyAdded	=	true;
			pendingInvicesDO.orderId 		= " ";
			vecPendingInvoices.add(pendingInvicesDO);
			if (tempcustomDialogs!=null && tempcustomDialogs.isShowing())
				tempcustomDialogs.dismiss();
			
			isClicked.add(vecPendingInvoices.size()-1);
			objUnPaidInvoiceAdapter.refreshList(vecPendingInvoices);
		}
	}
	
	@Override
	public void onButtonNoClick(String from)
	{
		super.onButtonNoClick(from);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(resultCode == 5000 && requestCode == 5000 && data!=null)
		{
			selectedAmount = data.getExtras().getFloat("totalInvoiceAmount");
			String trxcode=  data.getExtras().getString("TrxCode");
			Intent intent  = new Intent();
			intent.putExtra("totalPaidAmount",selectedAmount);
			intent.putExtra("trxcode", trxcode);
			setResult(5000,intent);
			finish();
		}
		else if(requestCode == 5000 )
		{
			finish();
		}
			
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private boolean isAllselected(ArrayList<Integer> isClicked)
	{
		if(isClicked.size() == vecPendingInvoices.size())
			return true;
		
		return false;
	}
	
	private void showAddInvoicePopup()
	{
		View view = inflater.inflate(R.layout.eot_popup, null);
		final CustomDialog customDialogs = new CustomDialog(PendingInvoices.this, view, preference	.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40,LayoutParams.WRAP_CONTENT, true);
		customDialogs.setCancelable(true);
		TextView tvTitle 				= (TextView) view.findViewById(R.id.tvTitlePopup);
		Button btnOkPopup 				= (Button) 	 view.findViewById(R.id.btnOkPopup);
		final EditText etEnterValue 	= (EditText) view.findViewById(R.id.etEnterValue);
		EditText etEnterReasons			= (EditText) view.findViewById(R.id.etEnterReason);
		final EditText etAmountdecimal	= (EditText) view.findViewById(R.id.etAmountdecimal);
		etEnterReasons.setVisibility(View.GONE);
		
		TextView tvSelectReason		= (TextView) view.findViewById(R.id.tvSelectReason);
		tvSelectReason.setVisibility(View.GONE);
		
		tvTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnOkPopup.setTypeface(AppConstants.Roboto_Condensed_Bold);
		etEnterValue.setTypeface(AppConstants.Roboto_Condensed);
		etAmountdecimal.setTypeface(AppConstants.Roboto_Condensed);
		
		etAmountdecimal.setVisibility(View.VISIBLE);
		etEnterValue.setHint("Enter Invoice Number");
		etEnterValue.setInputType(InputType.TYPE_CLASS_TEXT);
		etAmountdecimal.setHint("Enter amount");
		
		int maxLength = 10;
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(maxLength);
		etAmountdecimal.setFilters(FilterArray);
		
		tvTitle.setText("Add Invoice");
		btnOkPopup.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				if(etEnterValue.getText().toString().equalsIgnoreCase(""))
					showCustomDialog(PendingInvoices.this,getString(R.string.warning), "Please enter invoice number.", getString(R.string.OK), null, "");
				else if(etAmountdecimal.getText().toString().equalsIgnoreCase(""))
					showCustomDialog(PendingInvoices.this,getString(R.string.warning), "Please enter amount.", getString(R.string.OK), null, "");
				else
				{
					String invoiceNumber = etEnterValue.getText().toString().trim();
					boolean isValid 	 = getValidateInvoiceNumber(invoiceNumber);
					
					if(!isValid)
						showCustomDialog(PendingInvoices.this,getString(R.string.warning), "Entered invoice number already exists.", getString(R.string.OK), null, "");
					else if(new CommonDA().checkInvoiceNumbers(etEnterValue.getText().toString().trim(), etAmountdecimal.getText().toString().trim()))
						showCustomDialog(PendingInvoices.this,getString(R.string.warning), "You have already paid for entered invoice number. Please enter another invoice number and try again.", getString(R.string.OK), null, "");
					else
					{
						PendingInvicesDO pendingInvicesDO = new PendingInvicesDO();
						pendingInvicesDO.invoiceNo 		= invoiceNumber;
						pendingInvicesDO.balance 		= decimalFormat.format(StringUtils.getDouble(etAmountdecimal.getText().toString()));
						pendingInvicesDO.lastbalance	= pendingInvicesDO.balance;
						pendingInvicesDO.invoiceDate 	= CalendarUtils.getOrderPostDate();
						pendingInvicesDO.invoiceDateToShow	= CalendarUtils.getFormatedDatefromString(pendingInvicesDO.invoiceDate);
						pendingInvicesDO.orderId 		= " ";
						pendingInvicesDO.isNewleyAdded	=	true;
						vecPendingInvoices.add(pendingInvicesDO);
						if (customDialogs.isShowing())
							customDialogs.dismiss();
						
						isClicked.add(vecPendingInvoices.size()-1);
						objUnPaidInvoiceAdapter.refreshList(vecPendingInvoices);
					}
				}
			}
		});
		if (!customDialogs.isShowing())
			customDialogs.show();
	}
	
	private boolean getValidateInvoiceNumber(String invoice)
	{
		boolean isValid = true;
		
		if(vecPendingInvoices != null && vecPendingInvoices.size() > 0)
		{
			for(PendingInvicesDO pInvicesDO : vecPendingInvoices)
			{
				if(pInvicesDO.invoiceNo.equalsIgnoreCase(invoice))
				{
					isValid = false;
					break;
				}
			}
		}
		
		return isValid;
	}
	
	@Override
	public void performPasscodeAction(NameIDDo nameIDDo, String from, boolean isCheckOut)
	{
		onButtonYesClick("skip");
	}
	
	@Override
	public void onBackPressed() 
	{
		setResult(100);
		finish();
	}
	
	private void showPaymentModePopup(final Intent intent)
	{
		View view = inflater.inflate(R.layout.payment_mode_popup, null);
		final CustomDialog customDialog = new CustomDialog(PendingInvoices.this, view);
		customDialog.setCancelable(true);
		
		Button btn_CashPayment = (Button) view.findViewById(R.id.btn_CashPayment);
		Button btn_ChequePayment = (Button) view.findViewById(R.id.btn_ChequePayment);
		
		btn_CashPayment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent.putExtra("paymode",AppConstants.PAYMENT_NOTE_CASH);
				
				if(isExceed || isFromPayment)
					startActivityForResult(intent, 5000);
				else
					startActivityForResult(intent, 1000);
				
				customDialog.dismiss();
			}
		});
		
		
		btn_ChequePayment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent.putExtra("paymode", AppConstants.PAYMENT_NOTE_CHEQUE);
				if(isExceed || isFromPayment)
					startActivityForResult(intent, 5000);
				else
					startActivityForResult(intent, 1000);
				
				customDialog.dismiss();
			}
		});
		
		if(!customDialog.isShowing())
			customDialog.show();
		
	}
	
	
}
