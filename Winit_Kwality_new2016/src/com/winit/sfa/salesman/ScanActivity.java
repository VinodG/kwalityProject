//package com.winit.sfa.salesman;
//
//
//import android.content.Intent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.LinearLayout.LayoutParams;
//import android.widget.TextView;
//
//import com.google.zxing.client.android.CaptureActivity;
//import com.winit.alseer.salesman.common.AppConstants;
//import com.winit.alseer.salesman.common.CustomDialog;
//import com.winit.alseer.salesman.common.Preference;
//import com.winit.alseer.salesman.dataaccesslayer.ScanResultObject;
//import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
//
//public class ScanActivity extends BaseActivity
//{
//	//declaration of variables
//	private LinearLayout llScan;
//	private TextView tvHeadTitle, tvOther;
//	private Button btnStartSale;
//	private JourneyPlanDO mallsDetails ;
//	private ImageView ivScanFreezer;
//	
//	@SuppressWarnings("deprecation")
//	@Override
//	public void initialize() 
//	{
//		llScan = (LinearLayout)inflater.inflate(R.layout.scan, null);
//		
//		if(getIntent().getExtras() != null)
//			mallsDetails = (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
//		
//		intialiseControls();
//		
//		llBody.addView(llScan,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//		setTypeFaceRobotoNormal(llScan);
//		ivScanFreezer.setOnClickListener(new OnClickListener() 
//		{
//			@Override
//			public void onClick(View v) 
//			{
//				AppConstants.objScanResultObject = null;
//				Intent intent	=	new Intent(ScanActivity.this,CaptureActivity.class);
//				startActivityForResult(intent, AppConstants.REQUEST_CODE);
//			}
//		});
//		
//		btnStartSale.setOnClickListener(new OnClickListener() 
//		{
//			@Override
//			public void onClick(View v) 
//			{
//				if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_GT))
//				{
////					Intent intent = new Intent(ScanActivity.this, CaptureInventoryCategory.class);
////					Intent intent = new Intent(ScanActivity.this, StoreCheckActivity.class);
//					Intent intent = new Intent(ScanActivity.this, StoreCheckCatagoryActivity.class);
//					intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
//					intent.putExtra("mallsDetails",mallsDetails);
//					intent.putExtra("from", "checkin");
//					startActivity(intent);
//					finish();
//				}
//				else
//				{
//					Intent intent = new Intent(ScanActivity.this, SalesmanOrderList.class);
//					intent.putExtra("object",mallsDetails);
//					startActivity(intent);
//					finish();
//				}
//			}
//		});
//	}
//	
//	public void intialiseControls()
//	{
//		tvHeadTitle 	= (TextView)llScan.findViewById(R.id.tvHeadTitle);
//		tvOther 		= (TextView)llScan.findViewById(R.id.tvOther);
//		ivScanFreezer 	= (ImageView)llScan.findViewById(R.id.ivScanFreezer);
//		btnStartSale	= (Button)llScan.findViewById(R.id.btnStartSale);
//		
//		/*tvHeadTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		tvOther.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		btnStartSale.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
//	}
//	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
//	{
//		if(AppConstants.objScanResultObject!=null)
//			showScanResultPopup(AppConstants.objScanResultObject);
//		else
//			showCustomDialog(ScanActivity.this, "Alert !", "Please scan the freezer before capturing the inventory.", "Ok", null, "");
//			
//		super.onActivityResult(requestCode, resultCode, data);
//	}
//	
//	private void showScanResultPopup(ScanResultObject mScanResultObject)
//	{
//		View view = inflater.inflate(R.layout.scan_result_popup, null);
//		final CustomDialog mCustomDialog = new CustomDialog(ScanActivity.this, view, preference
//				.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40, LayoutParams.WRAP_CONTENT, true);
//		mCustomDialog.setCancelable(true);
//		
//		
//		TextView tvTitlePopup 		= (TextView) view.findViewById(R.id.tvTitlePopup);
//		TextView tvCustmerName 		= (TextView) view.findViewById(R.id.tvCustmerName);
//		TextView tvFreezerID 		= (TextView) view.findViewById(R.id.tvFreezerID);
//		TextView tvFreezerIDVal 	= (TextView) view.findViewById(R.id.tvFreezerIDVal);
//		TextView tvFreezerType 		= (TextView) view.findViewById(R.id.tvFreezerType);
//		TextView tvFreezerTypeVal 	= (TextView) view.findViewById(R.id.tvFreezerTypeVal);
//		TextView tvAddress 			= (TextView) view.findViewById(R.id.tvAddress);
//		TextView tvAddressVal 		= (TextView) view.findViewById(R.id.tvAddressVal);
//		TextView tvLatitude 		= (TextView) view.findViewById(R.id.tvLatitude);
//		TextView tvLatitudeVal 		= (TextView) view.findViewById(R.id.tvLatitudeVal);
//		TextView tvLongitude 		= (TextView) view.findViewById(R.id.tvLongitude);
//		TextView tvLongitudeVal 	= (TextView) view.findViewById(R.id.tvLongitudeVal);
//		Button btnYes 				= (Button)   view.findViewById(R.id.btnYesPopup);
//		
//		/*tvTitlePopup.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		tvCustmerName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		tvFreezerID.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		tvFreezerIDVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		tvFreezerType.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		tvFreezerTypeVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		tvAddress.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		tvAddressVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		tvLatitude.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		tvLatitudeVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		tvLongitude.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		tvLongitudeVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
//		
//		tvCustmerName.setText(mallsDetails.siteName+" ["+mallsDetails.site+"]");
//		tvFreezerIDVal.setText(mScanResultObject.barcodeId);
//		tvFreezerTypeVal.setText("120 ltr.");
//		tvAddressVal.setText(mallsDetails.addresss1 + ", "+mallsDetails.addresss2 );
//		tvLatitudeVal.setText(""+mallsDetails.geoCodeX);
//		tvLongitudeVal.setText(""+mallsDetails.geoCodeY);
//		
//		btnYes.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v) 
//			{
//				mCustomDialog.dismiss();
//				if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_GT))
//				{
////					Intent intent = new Intent(ScanActivity.this, CaptureInventoryCategory.class);
//					Intent intent = new Intent(ScanActivity.this, StoreCheckCatagoryActivity.class);
//					intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
//					intent.putExtra("mallsDetails",mallsDetails);
//					intent.putExtra("from", "checkin");
//					startActivity(intent);
//					finish();
//				}
//				else
//				{
//					Intent intent = new Intent(ScanActivity.this, SalesmanOrderList.class);
//					intent.putExtra("object",mallsDetails);
//					startActivity(intent);
//					finish();
//				}
//			}
//		});
//		try{
//		if (!mCustomDialog.isShowing())
//			mCustomDialog.show();
//		}catch(Exception e){}
//	}
//}
