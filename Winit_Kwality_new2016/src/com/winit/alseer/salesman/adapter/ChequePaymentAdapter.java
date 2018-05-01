/**
 * 
 */
package com.winit.alseer.salesman.adapter;

import java.util.Calendar;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.dataobject.ChequePaymentDetailDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.ReceivePaymentActivity;

/**
 * @author Aritra.Pal
 *
 */
public class ChequePaymentAdapter extends PagerAdapter
{

	 private Context context;
	 private Vector<ChequePaymentDetailDO> vecChequePayment;
	 private boolean isCheque;
	 //cal
	 private Calendar cal;
	 private int day;
	 private int month;
	 private int year; 
	 private NameIDDo nameIDDo = new NameIDDo();
	 private String dateFormat = "";
	 
//	private HashMap<String,Float> hashChequeDetails;
	
	 private Vector<NameIDDo> vecBankDetails ;
	/**
	 * @param receivePaymentActivity
	 * @param vecChequePayment
	 * @param b
	 */
	public ChequePaymentAdapter(Context context,Vector<ChequePaymentDetailDO> vecChequePayment,Vector<NameIDDo> vecBankDetails, boolean isCheque) 
	{
		this.context = context;
		this.vecChequePayment = vecChequePayment;
		this.isCheque = isCheque;
		this.vecBankDetails = vecBankDetails;
		
	   cal = Calendar.getInstance();
	   day = cal.get(Calendar.DAY_OF_MONTH);
	   month = cal.get(Calendar.MONTH);
	   year = cal.get(Calendar.YEAR);
	   
	}


	/**
	 * @param vecChequePayment2
	 */
	public void refresh(Vector<ChequePaymentDetailDO> vecChequePayment) 
	{
		this.vecChequePayment = vecChequePayment;
		notifyDataSetChanged();
	}
	
	public void refreshImage(int position,String filePath) 
	{
		if(position < vecChequePayment.size())
		{
			vecChequePayment.get(position).filePath = filePath;
			notifyDataSetChanged();
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() 
	{
		if(vecChequePayment != null && vecChequePayment.size() > 0)
			return vecChequePayment.size();
		else
		   return 0;
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#getItemPosition(java.lang.Object)
	 */
	@Override
	public int getItemPosition(Object object) 
	{
		return POSITION_NONE;
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view.ViewGroup, int)
	 */
	private boolean isEnabled = true;
	private EditText edtChequeNumber,edtAmount,edtBranchName;
	private TextView edtBankName,edtDate;
	@Override
	public Object instantiateItem(ViewGroup container, final int position) 
	{
		View convertView = LayoutInflater.from(context).inflate(R.layout.paymentchequecell, null);
		
		edtChequeNumber 	    = (EditText) convertView.findViewById(R.id.edtChequeNumber);
		edtBankName		    	= (TextView) convertView.findViewById(R.id.edtBankName);
		edtDate					= (TextView) convertView.findViewById(R.id.edtDate);
		edtAmount				= (EditText) convertView.findViewById(R.id.edtAmount);
		edtBranchName			= (EditText) convertView.findViewById(R.id.edtBranchName);
		
		if(isEnabled)
		{
			edtChequeNumber.setEnabled(isEnabled);
			edtAmount.setEnabled(isEnabled);
			edtDate.setEnabled(isEnabled);
			edtBankName.setEnabled(isEnabled);
			
			final ChequePaymentDetailDO chequePaymentDetailDO = vecChequePayment.get(position);
	        edtAmount.setText(chequePaymentDetailDO.Amount+"");
	        edtBankName.setText(chequePaymentDetailDO.BankName+"");
	        edtChequeNumber.setText(chequePaymentDetailDO.ChequeNumber+"");
	        edtDate.setText(chequePaymentDetailDO.Date+"");
	        edtDate.setText(CalendarUtils.getFormatedDatefromStringCheque(chequePaymentDetailDO.Date+""));
	        edtBranchName.setText(chequePaymentDetailDO.BranchName);
	        edtBranchName.addTextChangedListener(new TextWatcher()
	        {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				
				@Override
				public void afterTextChanged(Editable s) 
				{
					chequePaymentDetailDO.BranchName = edtBranchName.getText().toString();
				}
			});
			edtAmount.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) 
				{
				}
				
				@Override
				public void afterTextChanged(Editable s) 
				{
					vecChequePayment.get(position).Amount = edtAmount.getText().toString();
					((ReceivePaymentActivity)context).getChequeAmount(edtAmount);
//					((ReceivePaymentActivity)context).updatePayments();
				}
			});
			
			edtChequeNumber.setOnEditorActionListener(new OnEditorActionListener()
			{
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
				{
					if(actionId == EditorInfo.IME_ACTION_NEXT)
					{
						edtBankName.performClick();
					}
					return true;
				}
			});

			edtBankName.setTag(chequePaymentDetailDO);
			edtBankName.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(final View v) 
				{
					showAllBank(v,vecBankDetails);
				}
			});
			
			edtChequeNumber.addTextChangedListener(new TextWatcher() 
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
				}
				
				@Override
				public void afterTextChanged(Editable s) 
				{
					vecChequePayment.get(position).ChequeNumber = edtChequeNumber.getText().toString();
//					((ReceivePaymentActivity)context).updatePayments();
				}
			});
			
			edtDate.setTag(chequePaymentDetailDO);
			edtDate.setOnClickListener(new OnClickListener()
			{
				@SuppressWarnings("deprecation")
				@Override
				public void onClick(View v) 
				{
				     showDatePickerDialog((TextView) v,chequePaymentDetailDO);
//				     vecChequePayment.get(position).Date = dateFormat;
				}
			});
		}
		else
		{
			edtChequeNumber.setEnabled(isEnabled);
			edtAmount.setEnabled(isEnabled);
			edtDate.setEnabled(isEnabled);
			edtBankName.setEnabled(isEnabled);
		}
		
        
		((ViewPager) container).addView(convertView);
		return convertView;
	}
	
	public void setEnabled(boolean isEnabled)
	{
		this.isEnabled = isEnabled;
//		if(edtChequeNumber!=null && edtAmount!=null && edtDate!=null && edtBankName!=null)
//		{
//			edtChequeNumber.setEnabled(isEnabled);
//			edtAmount.setEnabled(isEnabled);
//			edtDate.setEnabled(isEnabled);
//			edtBankName.setEnabled(isEnabled);
//			
//			edtChequeNumber.setClickable(isEnabled);
//			edtAmount.setClickable(isEnabled);
//			edtDate.setClickable(isEnabled);
//			edtBankName.setClickable(isEnabled);
//			edtChequeNumber.setFocusable(isEnabled);
//			edtAmount.setFocusable(isEnabled);
//		}
		notifyDataSetChanged();
	}
	
	@SuppressLint("NewApi")
	private void showDatePickerDialog(final TextView edtDate,final ChequePaymentDetailDO chequePaymentDetailDO)
	{
		final Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH);
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		 
		DatePickerDialog dpd = new DatePickerDialog(context,new DatePickerDialog.OnDateSetListener() 
		{
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
            {
            	int month1=monthOfYear+1;
            	String month=String.valueOf(month1);
            	if(month1<=9)
            		month="0"+String.valueOf(month1);
            	if(dayOfMonth < 10)
            		dateFormat = year+"-"+month+"-"+"0"+dayOfMonth;
            	else
            		dateFormat = year+"-"+month+"-"+dayOfMonth;
            	((ChequePaymentDetailDO)edtDate.getTag()).Date = dateFormat+"T00:00:00";
            	edtDate.setText(CalendarUtils.getMonthAsShortString(monthOfYear)+" "+dayOfMonth+CalendarUtils.getDateNotation(dayOfMonth)+" "+year);
            }
        }, mYear, mMonth, mDay);
		
		/***Below method sets max date selection upto 3months but it requires more than api level 11****/
//		int nextYear = mYear;
//		int nextMonth = mMonth+3;
//		int nextDay = mDay-1;
//		if(nextMonth > 11)
//		{
//			nextYear++;
//			nextMonth=nextMonth-11;Tservice
//			nextDay=mDay-1;
//		}
//		Calendar c1 = Calendar.getInstance();
//		
//		c1.set(nextYear, nextMonth, nextDay);  
//		Date sDate = (Date) c1.getTime();
//		dpd.getDatePicker().setMaxDate(sDate.getTime());
		
		dpd.show();	
	}
     
	 
	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#isViewFromObject(android.view.View, java.lang.Object)
	 */
	@Override
	public boolean isViewFromObject(View view, Object arg1) 
	{
		return view == ((View)arg1);
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#destroyItem(android.view.View, int, java.lang.Object)
	 */
	@Override
	public void destroyItem(View container, int position, Object view) 
	{
		((ViewPager) container).removeView((View) view);
	}
	
	@Override
	public void finishUpdate(View arg0) 
	{
	  
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) 
	{
	    
	}

	@Override
	public Parcelable saveState() 
	{
		return null;
	}

	@Override
	public void startUpdate(View arg0) 
	{
	   
	}
	
	public Vector<ChequePaymentDetailDO> getCheckPaymentDetails()
	{
		if(vecChequePayment != null && vecChequePayment.size() > 0)
			return vecChequePayment;
		else
			return null;
	}
	
	 private void showAllBank(final View v, Vector<NameIDDo> vecBankDetails) 
	 {
		 CustomBuilder customBuilder = new CustomBuilder(context, "Select Bank", false);
		 customBuilder.setSingleChoiceItems(vecBankDetails, v.getTag(), new CustomBuilder.OnClickListener()
		 {
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject) 
			{
				nameIDDo = (NameIDDo) selectedObject;
				((TextView)v).setText(nameIDDo.strName);
				((ChequePaymentDetailDO)v.getTag()).BankName = nameIDDo.strName;
				((ChequePaymentDetailDO)v.getTag()).BankID = nameIDDo.strId;
				builder.dismiss();
//				((ReceivePaymentActivity)context).updatePayments();
			}
		});
		 customBuilder.show();
     }
	 
//	 public NameIDDo getBankDetails()
//	 {
//		 return nameIDDo;
//	 }
}
