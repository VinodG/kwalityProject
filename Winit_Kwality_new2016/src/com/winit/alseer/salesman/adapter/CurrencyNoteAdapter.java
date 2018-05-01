/**
 * 
 */
package com.winit.alseer.salesman.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.winit.kwalitysfa.salesman.R;

public class CurrencyNoteAdapter extends BaseAdapter
{
   
	private Context context;	
	private ArrayList<String> vecDetails;
	
	/**
	 * @param receivePaymentActivity
	 * @param vector
	 */
	public CurrencyNoteAdapter(Context context,ArrayList<String> vecDetails) 
	{
		this.context			= context;
		this.vecDetails 		= (ArrayList<String>) vecDetails.clone();
	}
	
	public ArrayList<String> getDetails()
	{
		return vecDetails;
	}

	/**
	 * @param vecCurrencyNoteDetail
	 */
	public void refresh(ArrayList<String> vecDetails) 
	{
		this.vecDetails = vecDetails;
		notifyDataSetChanged();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() 
	{
		if(vecDetails != null && vecDetails.size() > 0)
		    return	vecDetails.size();
		else
		   return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) 
	{
		return vecDetails.get(position);
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
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		convertView = LayoutInflater.from(context).inflate(R.layout.currencynotedetailcell, null);
		final EditText edtNoteDetailLeft = (EditText) convertView.findViewById(R.id.edtNoteDetailLeft);
		final ImageView ivDelete 		= (ImageView) convertView.findViewById(R.id.ivDelete);
		
		edtNoteDetailLeft.setTag(position);
		//edtNoteDetailLeft.setText(vecCurrencyNoteDet.get(position).rupee);
		edtNoteDetailLeft.setText(vecDetails.get(position));
		ivDelete.setTag(edtNoteDetailLeft);
		ivDelete.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				((EditText)ivDelete.getTag()).setText("");
//				notifyDataSetChanged();
			}
		});
		
		edtNoteDetailLeft.addTextChangedListener(new TextWatcher()
		{
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
				vecDetails.set(position, edtNoteDetailLeft.getText().toString());
			}
		});
		
		return convertView;
	}
	
	
}
