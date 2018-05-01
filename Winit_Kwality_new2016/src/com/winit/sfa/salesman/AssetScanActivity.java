package com.winit.sfa.salesman;

import java.io.ByteArrayOutputStream;

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
import com.winit.alseer.salesman.common.Base64;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.BarcodeScanInfoDA;
import com.winit.alseer.salesman.dataobject.MallsDetails;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.kwalitysfa.salesman.R;

public class AssetScanActivity extends BaseActivity
{
	//declaration of variables
	private LinearLayout llAssetscan;
	private Button btnAssetscanSubmit,btnAssetscanCancel;
	private EditText edtAssetscanComments, etQuantity;
	private TextView tvType, tvLocation, tvTime, tvTimeBold, tvLocationBold, tvTypeBold, tvQuantityTitle;
	private ImageView ivScanCode;
	private BarcodeScanInfoDA barcodeScanInfoBL;
	private Bitmap bitmap;
	
	@Override
	public void initialize() 
	{
		//inflate the asset-scan layout
		llAssetscan 		= (LinearLayout)inflater.inflate(R.layout.assetscan, null);
		llBody.addView(llAssetscan,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		intialiseControls();
		
		barcodeScanInfoBL 	= new BarcodeScanInfoDA();
		
		if( AppConstants.objScanResultObject!=null)
		{
			bitmap = BitmapFactory.decodeByteArray( AppConstants.objScanResultObject.barcodeImage , 0,  AppConstants.objScanResultObject.barcodeImage.length);
			ivScanCode.setImageBitmap(bitmap);
			tvType.setText(""+AppConstants.objScanResultObject.type);
			tvTime.setText(""+AppConstants.objScanResultObject.time);
			tvLocation.setText(""+preference.getStringFromPreference("CUREENT_LOCATION_NAME",""));
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
				if(etQuantity.getText().toString().equalsIgnoreCase(""))
				{
					AppConstants.objScanResultObject.comments 	= ""+edtAssetscanComments.getText().toString();
					AppConstants.objScanResultObject.location	= ""+preference.getStringFromPreference("CUREENT_LOCATION_NAME","");
					AppConstants.objScanResultObject.productId	= ""+AppConstants.objScanResultObject.type;
					AppConstants.objScanResultObject.customerSiteId = preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");
					AppConstants.objScanResultObject.EmpId	= ""+preference.getStringFromPreference(Preference.SALESMANCODE, "");
					barcodeScanInfoBL.insertBarcodeScanInfo(AppConstants.objScanResultObject);
					showCustomDialog(AssetScanActivity.this, getResources().getString(R.string.warning), "Please enter quantity.", getResources().getString(R.string.OK), null, "");
				}
				else
				{
					showLoader(getResources().getString(R.string.please_wait));
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							MallsDetails objMallsDetails = preference.getMallsDetailsObjectFromPreferenceNew(Preference.CUSTOMER_DETAIL);
							
							if(AppConstants.objScanResultObject!=null && objMallsDetails != null && bitmap!= null)
							{
								AppConstants.objScanResultObject.comments 		= ""+edtAssetscanComments.getText().toString();
								AppConstants.objScanResultObject.productId		= ""+AppConstants.objScanResultObject.type;
								AppConstants.objScanResultObject.type		    = ""+AppConstants.objScanResultObject.type;
								AppConstants.objScanResultObject.EmpId	    	= ""+preference.getStringFromPreference(Preference.SALESMANCODE, "");
								AppConstants.objScanResultObject.location   	= objMallsDetails.Address1+" "+objMallsDetails.Address2;
								AppConstants.objScanResultObject.customerSiteId = ""+preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");
								AppConstants.objScanResultObject.Quantity 		= ""+etQuantity.getText().toString();
								AppConstants.objScanResultObject.time			= CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00";
								
								ByteArrayOutputStream stream = new ByteArrayOutputStream();
								bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
								AppConstants.objScanResultObject.strBarcodeImage = Base64.encodeBytes(stream.toByteArray());
								
								if(barcodeScanInfoBL.insertBarcodeScanInfo(AppConstants.objScanResultObject))
								{
									showCustomDialog(AssetScanActivity.this, getResources().getString(R.string.successful), "Scan Result submitted successfully.", getResources().getString(R.string.OK), null, "submitted");
								}
								else
									showCustomDialog(AssetScanActivity.this, getResources().getString(R.string.warning), "Error occurred while submitting.", getResources().getString(R.string.OK), null, "");
							}
							else
								showCustomDialog(AssetScanActivity.this, getResources().getString(R.string.warning), "Error occurred while submitting.", getResources().getString(R.string.OK), null, "");
							hideLoader();
						}
					}).start();
				}
			}
		});
		
		setTypeFaceRobotoNormal(llAssetscan);
	}
	public void intialiseControls()
	{
		btnAssetscanSubmit		 =  (Button)llAssetscan.findViewById(R.id.btnAssetscanSubmit);
		btnAssetscanCancel 		 =  (Button)llAssetscan.findViewById(R.id.btnAssetscanCancel);
		edtAssetscanComments	 =  (EditText)llAssetscan.findViewById(R.id.edtAssetscanComments);
		etQuantity				 =  (EditText)llAssetscan.findViewById(R.id.etQuantity);
		ivScanCode				 =	(ImageView)llAssetscan.findViewById(R.id.ivScanCode);	
		tvType					 =	(TextView)llAssetscan.findViewById(R.id.tvType);
		tvLocation				 =	(TextView)llAssetscan.findViewById(R.id.tvLocation);
		tvTime					 =	(TextView)llAssetscan.findViewById(R.id.tvTime);
		tvTypeBold				 =	(TextView)llAssetscan.findViewById(R.id.tvTypeBold);
		tvLocationBold			 =	(TextView)llAssetscan.findViewById(R.id.tvLocationBold);
		tvTimeBold				 =	(TextView)llAssetscan.findViewById(R.id.tvTimeBold);
		tvQuantityTitle			 =	(TextView)llAssetscan.findViewById(R.id.tvQuantityTitle);
	/*	
		tvQuantityTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnAssetscanSubmit.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnAssetscanCancel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		edtAssetscanComments.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etQuantity.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvType.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
		tvLocation.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
		tvTime.setTypeface(AppConstants.Helvetica_LT_57_Condensed);	
		tvTypeBold.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvLocationBold.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvTimeBold.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);	*/
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
