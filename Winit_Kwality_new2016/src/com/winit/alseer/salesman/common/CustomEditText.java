package com.winit.alseer.salesman.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.winit.alseer.salesman.listeners.KeyClickListener;

public class CustomEditText extends EditText implements OnClickListener
{
	
	KeyClickListener keyClickListener;
	/**
	 * Constructor 
	 * @param context
	 */
	public CustomEditText(Context context)
	{
		super(context);
		keyClickListener = (KeyClickListener) context;
		this.setOnClickListener(this);
	}
	
	/**
	 * Constructor 
	 * @param context
	 * @param attrs
	 */
	public CustomEditText(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		keyClickListener = (KeyClickListener) context;
		this.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View view) 
	{
		if(keyClickListener!=null)
			keyClickListener.onEditClick((CustomEditText)view,-1);
	}


}
