/**
 * 
 */
package com.winit.alseer.salesman.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.winit.alseer.salesman.dataobject.PendingInvicesDO;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;

/**
 * @author Aritra.Pal
 *
 */
public class InvoiceAmountAdapter extends BaseAdapter
{

	private Context context;
	private ArrayList<PendingInvicesDO> arrInvoiceNumbers ;
	private float totalInvoiceAmount = 0.0f;
	/**
	 * @param receivePaymentActivity
	 */
	public InvoiceAmountAdapter(Context context, ArrayList<PendingInvicesDO> arrInvoiceNumbers) 
	{
		this.context = context;
		this.arrInvoiceNumbers = arrInvoiceNumbers;
	}
	
	/**
	 * @param vecInvoiceAmt2
	 */
	public void refreh(ArrayList<PendingInvicesDO> arrInvoiceNumbers) 
	{
		this.arrInvoiceNumbers = arrInvoiceNumbers;
		notifyDataSetChanged();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() 
	{
		if(arrInvoiceNumbers != null)
			return arrInvoiceNumbers.size();
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) 
	{
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		convertView = LayoutInflater.from(context).inflate(R.layout.paymentlistcell, null);
		
		PendingInvicesDO invoiceAmountDO = arrInvoiceNumbers.get(position);
		
		TextView tvInvoiceNumber = (TextView) convertView.findViewById(R.id.tvInvoiceNumber);
		TextView tvInvoiceType = (TextView) convertView.findViewById(R.id.tvInvoiceType);
		TextView tvInvoiceAmt = (TextView) convertView.findViewById(R.id.tvInvoiceAmt);
		
		tvInvoiceNumber.setText(invoiceAmountDO.invoiceNo);
		tvInvoiceType.setText("Sales Invoice");
		tvInvoiceAmt.setText(((BaseActivity)context).amountFormate.format(StringUtils.getFloat(invoiceAmountDO.balance)));
		
		return convertView;
	}
}
