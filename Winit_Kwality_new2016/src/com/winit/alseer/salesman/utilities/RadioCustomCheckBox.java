package com.winit.alseer.salesman.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.winit.kwalitysfa.salesman.R;

public class RadioCustomCheckBox extends ImageView implements OnClickListener
{
	private boolean isChecked = false;
	private OnCheckedChangeListener listener;
	
	/**
	 * Constructor 
	 * @param context
	 */
	public RadioCustomCheckBox(Context context)
	{
		super(context);
		
		this.setOnClickListener(this);
		
		setChecked(false);
	}
	
	/**
	 * Constructor 
	 * @param context
	 * @param attrs
	 */
	public RadioCustomCheckBox(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		this.setOnClickListener(this);
		
		setChecked(false);
	}
	
	/**
	 * Mehtod to set checked or not
	 * @param checked
	 */
	public void setChecked(boolean checked)
	{
		if(checked)
			setBackgroundResource(R.drawable.chcek);
		else
			setBackgroundResource(R.drawable.uncheck);

		isChecked = checked;
	}
	
	/**
	 * method to return is Checked or not
	 * @return
	 */
	public boolean isChecked()
	{
		return isChecked;
	}

	/**
	 * method to set OnChecked Change Listener
	 * @param listener
	 */
	public void setOnCheckedChangeListener(OnCheckedChangeListener listener)
	{
		this.listener = listener;
	}
	
	/** interface to Checked Change Listener
	 */
	public static interface OnCheckedChangeListener
	{
		public void onCheckedChanged(RadioCustomCheckBox view, boolean checked);
	}

	@Override
	public void onClick(View view) 
	{
		if(isChecked)
		{
			view.setBackgroundResource(R.drawable.uncheck);
			isChecked = false;
		}
		else
		{
			view.setBackgroundResource(R.drawable.chcek);
			isChecked = true;
		}
		
		if(listener != null)
			listener.onCheckedChanged((RadioCustomCheckBox)view, isChecked);
	}
}
