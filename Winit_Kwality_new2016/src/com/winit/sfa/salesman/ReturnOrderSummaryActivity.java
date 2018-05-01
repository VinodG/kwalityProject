package com.winit.sfa.salesman;

import httpimage.HttpImageManager;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.MyApplicationNew;
import com.winit.kwalitysfa.salesman.R;
public class ReturnOrderSummaryActivity extends BaseActivity
{
	private LinearLayout llReturnOrder;
	private TextView tvHeader,tvNoDataFound;
	private ListView lvReturnList;
	private ReturnOrderAdapter adapter;
	private ArrayList<TrxDetailsDO> arrayList;
	private Button btnReprint;
	private TrxHeaderDO trxHeaderDO;
	@Override
	public void initialize() 
	{
		llReturnOrder 	 = (LinearLayout)inflater.inflate(R.layout.activity_return_order_summary, null);
		llBody.addView(llReturnOrder,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		if(getIntent().getExtras()!=null){
			trxHeaderDO	 	=	(TrxHeaderDO) getIntent().getExtras().get("trxHeaderDO");
			arrayList = (ArrayList<TrxDetailsDO>) getIntent().getExtras().get("trxDetail");
		}
		initializeControls();
		loadImages();
		btnReprint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showLoader(getResources().getString(R.string.loading));
				new Thread(new Runnable() 
				{
					@Override
					public void run() 
					{
						final JourneyPlanDO mallsDetails	=	new CustomerDetailsDA().getCustometBySiteId(trxHeaderDO.clientCode);
						runOnUiThread(new Runnable() 
						{
							@Override
							public void run() 
							{
								hideLoader();
								if(arrayList != null && arrayList.size() > 0 && trxHeaderDO != null)
								{
									Intent intent = new Intent(ReturnOrderSummaryActivity.this, WoosimPrinterActivity.class);
									intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES);
									intent.putExtra("trxHeaderDO", trxHeaderDO);
									intent.putExtra("mallsDetails", mallsDetails);
									intent.putExtra("str", " - Reprint");
									startActivity(intent);
								}
								else
									showCustomDialog(ReturnOrderSummaryActivity.this, "Warning !", "Error occurred while printing.", "OK", null, "");
							}
						});
					}
				}).start();
			}
		});
	}
	
	private void loadImages()
	{
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				
			}
		}).start();
	}
	
	private void initializeControls()
	{
		tvHeader				=	(TextView)llReturnOrder.findViewById(R.id.tvHeader);
		tvNoDataFound			=	(TextView)llReturnOrder.findViewById(R.id.tvNoDataFound);
		lvReturnList 			= 	(ListView)llReturnOrder.findViewById(R.id.lv);
		btnReprint				= 	(Button)llReturnOrder.findViewById(R.id.btnReprint);
		setTypeFaceRobotoNormal(llReturnOrder);
		tvHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnReprint.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		lvReturnList.setCacheColorHint(0);
		lvReturnList.setDividerHeight(0);
		
		if(arrayList!=null && arrayList.size()>0)
		{
			adapter					=   new ReturnOrderAdapter(arrayList);
			lvReturnList.setAdapter(adapter);
			
			tvNoDataFound.setVisibility(View.GONE);
			lvReturnList.setVisibility(View.VISIBLE);
		}
		else
		{
			tvNoDataFound.setVisibility(View.VISIBLE);
			lvReturnList.setVisibility(View.GONE);
		}
	}
	
	public HttpImageManager getHttpImageManager() {
		return ((MyApplicationNew) ((Activity) ReturnOrderSummaryActivity.this)
				.getApplication()).getHttpImageManager();
	}
	
	public class ReturnOrderAdapter extends BaseAdapter {

		private ArrayList<TrxDetailsDO> arrayList;
		
		public ReturnOrderAdapter(ArrayList<TrxDetailsDO> arrayList)
		{
			this.arrayList = arrayList;
		}
		@Override
		public int getCount() {
			if(arrayList != null && arrayList.size() > 0)
				return arrayList.size();
			else
				return 0;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		public void refresh(ArrayList<TrxDetailsDO> arrayList)
		{
			this.arrayList = arrayList;
			if(arrayList != null && arrayList.size() > 0)
			{
				tvNoDataFound.setVisibility(View.GONE);
			}
			else
				tvNoDataFound.setVisibility(View.VISIBLE);
			notifyDataSetChanged();
		}
		
		public class ViewHolder
		{
			public TextView tvItemCode,tvRemarkTag,tvItemDesc,tvUOM,tvReqQty,tvApproved,tvCollected,tvReturnType,tvRemark,tvExpiryDate,tvReason,tvReturnTypeTag,tvExpiryDateTag;
			public ImageView ivThumb;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			final ViewHolder viewHolder;
			if(convertView == null)
			{
				convertView 	 			=   (LinearLayout)inflater.inflate(R.layout.return_order_summary_list_item, null);
				viewHolder				    =   new ViewHolder();
				viewHolder.tvItemCode 		= 	(TextView)convertView.findViewById(R.id.tvItemCode);
				viewHolder.tvItemDesc 		= 	(TextView)convertView.findViewById(R.id.tvItemDesc);
				viewHolder.tvUOM 			= 	(TextView)convertView.findViewById(R.id.tvUOM);
				viewHolder.tvReqQty 		= 	(TextView)convertView.findViewById(R.id.tvReqQty);
				viewHolder.tvApproved 		= 	(TextView)convertView.findViewById(R.id.tvApproved);
				viewHolder.tvCollected  	= 	(TextView)convertView.findViewById(R.id.tvCollected);
				viewHolder.ivThumb	 		= 	(ImageView)convertView.findViewById(R.id.ivThumb);
				viewHolder.tvReturnType 	= 	(TextView)convertView.findViewById(R.id.tvReturnType);
				viewHolder.tvReason 		= 	(TextView)convertView.findViewById(R.id.tvReason);
				viewHolder.tvRemarkTag 		= 	(TextView)convertView.findViewById(R.id.tvRemarkTag);
				viewHolder.tvExpiryDate 	= 	(TextView)convertView.findViewById(R.id.tvExpiryDate);
				viewHolder.tvRemark 		= 	(TextView)convertView.findViewById(R.id.tvRemark);
				viewHolder.tvReturnTypeTag	=	(TextView)convertView.findViewById(R.id.tvReturnTypeTag);
				viewHolder.tvExpiryDateTag	=	(TextView)convertView.findViewById(R.id.tvExpiryDateTag);
				
				convertView.setTag(viewHolder);
			}
			else
				viewHolder = (ViewHolder) convertView.getTag();
			
			TrxDetailsDO trxDetailsDO	= arrayList.get(position);
			
			viewHolder.tvItemCode.setText(trxDetailsDO.itemCode+"");
			viewHolder.tvItemDesc.setText(trxDetailsDO.itemDescription+"");
			viewHolder.tvUOM.setText(trxDetailsDO.UOM+"");
			viewHolder.tvReqQty.setText(trxDetailsDO.requestedBU+"");
			viewHolder.tvApproved.setText(trxDetailsDO.approvedBU+"");
			viewHolder.tvCollected.setText(trxDetailsDO.collectedBU+"");
			
			if(trxDetailsDO.itemType.equalsIgnoreCase("D"))
			{
				viewHolder.tvReason.setVisibility(View.VISIBLE);
				viewHolder.tvReason.setText(trxDetailsDO.reason);
				viewHolder.tvReturnType.setText(getString(R.string.non_sellable));
				viewHolder.tvReturnType.setTextColor(getResources().getColor(R.color.red_dark));
			}
			else
			{
				viewHolder.tvReason.setVisibility(View.GONE);
				viewHolder.tvReturnType.setText(getString(R.string.sellable));
				viewHolder.tvReturnType.setTextColor(getResources().getColor(R.color.green_dark));
			}
			
			if(trxDetailsDO.expiryDate.equalsIgnoreCase(""))
			{
				trxDetailsDO.expiryDate = "N/A";
				viewHolder.tvExpiryDate.setText(trxDetailsDO.expiryDate+"");
			}
			else
				viewHolder.tvExpiryDate.setText(CalendarUtils.getFormatedDatefromStringWithOnlyTime(trxDetailsDO.expiryDate+""));
			
			viewHolder.tvRemark.setText(""+trxDetailsDO.trxDetailsNote);
			setTypeFaceRobotoNormal((ViewGroup)convertView);
			
			if(trxDetailsDO.vecDamageImages!=null && trxDetailsDO.vecDamageImages.size()>0)
			{
				viewHolder.ivThumb.setVisibility(View.VISIBLE);
				final Uri uri = Uri.parse(trxDetailsDO.vecDamageImages.get(0).ImagePath);
				if (uri != null) 
				{
					String imagePath = trxDetailsDO.vecDamageImages.get(0).ImagePath;
					if(!TextUtils.isEmpty(imagePath) && imagePath.contains(".."))
						imagePath = imagePath.replace("../", ServiceURLs.IMAGE_GLOBAL_URL);
					final Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,viewHolder.ivThumb,trxDetailsDO.vecDamageImages.get(0).ImagePath));
					if(bitmap != null) 
					{
						viewHolder.ivThumb.setImageBitmap(bitmap);
						viewHolder.ivThumb.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								showFullView(bitmap);
							}
						});
					}
				}
			}else{
				viewHolder.ivThumb.setVisibility(View.VISIBLE);
				viewHolder.ivThumb.setImageResource(R.drawable.no_image);
			}
			
			viewHolder.tvItemCode.setTypeface(AppConstants.Roboto_Condensed_Bold);
			viewHolder.tvReturnTypeTag.setTypeface(AppConstants.Roboto_Condensed_Bold);
			viewHolder.tvExpiryDateTag.setTypeface(AppConstants.Roboto_Condensed_Bold);
			viewHolder.tvRemarkTag.setTypeface(AppConstants.Roboto_Condensed_Bold);
			return convertView;
		}
	}

	private  Dialog dialogFullImage;
	private final int ZOOM_IN = 0, ZOOM_OUT = 1;
	/**
	 * show dialog for full image
	 * @param bitmap
	 */
	private void showFullView(Bitmap bitmap)
	{
		if(dialogFullImage != null && dialogFullImage.isShowing())
			dialogFullImage.dismiss();
		final RelativeLayout llGal = (RelativeLayout) inflater.inflate(R.layout.image_full_dialog,null);
		dialogFullImage = new Dialog(ReturnOrderSummaryActivity.this,R.style.Theme_Transparent);
		dialogFullImage.setContentView(llGal, new LayoutParams(preference.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 600),preference.getIntFromPreference(Preference.DEVICE_DISPLAY_HEIGHT, 1080) - 150));
		dialogFullImage.setCancelable(true);
		
		LinearLayout llGalleryDialog = (LinearLayout) llGal.findViewById(R.id.llGalleryDialog);
		
		llGalleryDialog.setLayoutParams(new RelativeLayout.LayoutParams(preference.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 600),preference.getIntFromPreference(Preference.DEVICE_DISPLAY_HEIGHT, 1080) - 200));
		
		ImageView ivFullImage = (ImageView) llGal.findViewById(R.id.ivFullImage);
		
		LinearLayout.LayoutParams LayoutParams = new LinearLayout.LayoutParams(preference.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 600) - 120,preference.getIntFromPreference(Preference.DEVICE_DISPLAY_HEIGHT, 600) - 300);
		LayoutParams.setMargins(0, 0, 10, 5);
		ivFullImage.setLayoutParams(LayoutParams);
		
		ImageView ivClose = (ImageView)llGal.findViewById(R.id.ivClose);
		ivClose.bringToFront();
		
		ivFullImage.setImageBitmap(bitmap);
		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				setAnimation(llGal,ZOOM_OUT);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						dialogFullImage.cancel();
					}
				}, 1000);
			}
		});

//		startAnimation();
		setAnimation(llGal,ZOOM_IN);
		dialogFullImage.show();
		dialogFullImage.setOnCancelListener(new OnCancelListener() 
		{

			@Override
			public void onCancel(final DialogInterface dialog) {
				setAnimation(llGal,ZOOM_OUT);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run()
					{
						dialog.cancel();
					}
				}, 1000);

			}
		});
		dialogFullImage.setCanceledOnTouchOutside(true);
	}
	
	/**
	 * Animation for zoom-in and zoom-out
	 * @param view
	 * @param TYPE
	 */
	private void setAnimation(View view,int TYPE)
	{
		Animation a = null ;
		switch (TYPE) {
		case ZOOM_IN:
			a = AnimationUtils.loadAnimation(this, R.anim.zoomin);
			break;
		case ZOOM_OUT:
			a = AnimationUtils.loadAnimation(this, R.anim.zoomout);
			break;
		}
		if(a != null)
		{
			a.reset();
			view.clearAnimation();
			view.startAnimation(a);
		}
	}	
}
