package com.winit.sfa.salesman;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;

public class ScanItemActivity extends BaseActivity
{
	//declaration of variables
	private LinearLayout llAssetscan;
	private Button btnAssetscanSubmit,btnAssetscanCancel;
	private EditText etQuantity;
	private TextView tvTypeBold, tvType, tvQuantityTitle;
	private ImageView ivScanCode;
	private Bitmap bitmap;
	private DeliveryAgentOrderDetailDco dco;
	
	@Override
	public void initialize() 
	{
		//inflate the asset-scan layout
		llAssetscan 		= (LinearLayout)inflater.inflate(R.layout.itemscan, null);
		llBody.addView(llAssetscan,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		intialiseControls();
		setTypeFaceRobotoNormal(llAssetscan);
		
		if( AppConstants.objScanResultObject!=null)
		{
			bitmap = BitmapFactory.decodeByteArray( AppConstants.objScanResultObject.barcodeImage , 0,  AppConstants.objScanResultObject.barcodeImage.length);
			ivScanCode.setImageBitmap(bitmap);
			tvType.setText(""+AppConstants.objScanResultObject.couponNo);
		}
		
		btnAssetscanCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
		
		btnAssetscanSubmit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showLoader(getResources().getString(R.string.please_wait));
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						dco = new DeliveryAgentOrderDetailDco();
						if(AppConstants.objScanResultObject!=null)
						{
							dco.itemCode 		= 	AppConstants.objScanResultObject.couponNo;
							dco.itemDescription = 	AppConstants.objScanResultObject.couponNo;
							dco.unitPerCase 	=	 1;
							dco.preCases 		=	 StringUtils.getFloat(etQuantity.getText().toString());
							dco.preUnits		= 	 (int) dco.preCases;
							
							Intent intent = new Intent();
							intent.putExtra("dco", dco);
							setResult(5000, intent);
							finish();
						}
						else
							showCustomDialog(ScanItemActivity.this, getResources().getString(R.string.warning), "Error occurred while submitting.", getResources().getString(R.string.OK), null, "");
						hideLoader();
					}
				}).start();
			}
		});
	}
	public void intialiseControls()
	{
		btnAssetscanSubmit		 =  (Button)llAssetscan.findViewById(R.id.btnAssetscanSubmit);
		btnAssetscanCancel 		 =  (Button)llAssetscan.findViewById(R.id.btnAssetscanCancel);
		etQuantity				 =  (EditText)llAssetscan.findViewById(R.id.etQuantity);
		ivScanCode				 =	(ImageView)llAssetscan.findViewById(R.id.ivScanCode);	
		tvType					 =	(TextView)llAssetscan.findViewById(R.id.tvType);
		tvTypeBold				 =	(TextView)llAssetscan.findViewById(R.id.tvTypeBold);
		tvQuantityTitle			 =	(TextView)llAssetscan.findViewById(R.id.tvQuantityTitle);
		
		/*tvQuantityTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnAssetscanSubmit.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnAssetscanCancel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etQuantity.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvType.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvTypeBold.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
	}
	
	@Override
	public void onButtonYesClick(String from)
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("submitted"))
		{
			finish();
		}
	}
}
