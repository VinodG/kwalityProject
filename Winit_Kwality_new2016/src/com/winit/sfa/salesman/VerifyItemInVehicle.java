package com.winit.sfa.salesman;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.parsers.VerifyMovementParser;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Base64;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.common.CustomEditText;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.InventoryDA;
import com.winit.alseer.salesman.dataaccesslayer.ProductsDA;
import com.winit.alseer.salesman.dataaccesslayer.VehicleDA;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.UOMConversionFactorDO;
import com.winit.alseer.salesman.dataobject.VanLoadDO;
import com.winit.alseer.salesman.dataobject.VerifyRequestDO;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;
public class VerifyItemInVehicle extends BaseActivity
{
	//Initialization and declaration of variables
	private LinearLayout llOrderSheet,llOrderListView,/*llOrdersheetVerify,*/llNotVerified/*,llTopVerified */;
	private Button btnOrdersheetVerify,btnPrint, btnContinue;
	private OrderSheetAdapter ordersheetadapter;
	public static ArrayList<VanLoadDO> vecOrdProduct;
	private TextView /*tvPageTitle,*/tvHeader, /*tvOrdersheetHeader,*/ tvNoItemFound/*, tvItemList*/
	 				 /*tvUsername,*/ /*tvDate, tvVehicleCode*/;
	private ListView lvItemList ; 
	private ArrayList<Integer> isClicked;
	private ImageView ivCheckAllItems;
	private MyView myViewManager;
	private MyView myViewDriver;
	private Paint mPaint;
	private EditText etSearch;
	
	private ArrayList<VanLoadDO> vecVanLoadTemp;
	
	private String movementId = "", movementType;
	
	
	private int listScrollState;
	
	private int status = -1;
	HashMap<String,UOMConversionFactorDO> hashArrUoms = new HashMap<String, UOMConversionFactorDO>();
	
	private ImageView ivSearchCross;
	
	@Override
	public void initialize() 
	{
		//inflate the order-sheet layout
		llOrderSheet 	 = (LinearLayout)inflater.inflate(R.layout.item_list_to_verify_latest, null);
		llBody.addView(llOrderSheet,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		if(getIntent().getExtras()!=null)
		{
			movementId = getIntent().getExtras().getString("movementId");
			movementType = getIntent().getExtras().getString("movementType");
		}
		
		intialiseControls();
		
		isClicked		=	new ArrayList<Integer>();
		
		lvItemList.setVerticalScrollBarEnabled(false);
		lvItemList.setDivider(getResources().getDrawable(R.drawable.saparetor));
		lvItemList.setCacheColorHint(0);
		lvItemList.setFadingEdgeLength(0);
		lvItemList.setAdapter(ordersheetadapter = new OrderSheetAdapter(new ArrayList<VanLoadDO>()));
		llOrderListView.addView(lvItemList, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		lvItemList.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) 
			{
				listScrollState = scrollState;
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
			}
		});
		
		if(vecOrdProduct == null || vecOrdProduct.size() == 0)
		{
			new Thread(new Runnable()
			{
				@Override
				public void run() 
				{
					showLoader("Please wait...");
					hashArrUoms = new ProductsDA().getUOMConversion(TrxDetailsDO.getItemUomLevel1());
					vecOrdProduct = new VehicleDA().getAllItemToVerifyByMovementID(movementId);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() 
						{
							hideLoader();
							if(vecOrdProduct == null || vecOrdProduct.size() == 0)
								btnOrdersheetVerify.setVisibility(View.GONE);
							ordersheetadapter.refresh(vecOrdProduct);
						}
					});
				}
			}).start();
		}
		else
		{
			ordersheetadapter.refresh(vecOrdProduct);
		}
		
		ivCheckAllItems.setTag("unchecked");
		ivCheckAllItems.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(v.getTag().toString().equalsIgnoreCase("unchecked"))
				{
					v.setTag("checked");
					((ImageView)v).setImageResource(R.drawable.checkbox_white);
					isClicked.clear();
					for(int j = 0; j < vecOrdProduct.size() ; j++)
					{
						vecOrdProduct.get(j).ischecked = true;
						isClicked.add(j);
					}
				}
				else
				{
					v.setTag("unchecked");
					((ImageView)v).setImageResource(R.drawable.uncheckbox_white);
					for(int j = 0; j < vecOrdProduct.size() ; j++)
					{
						vecOrdProduct.get(j).ischecked = false;
					}
					isClicked.clear();
				}
				ordersheetadapter.refresh();
			}
		});
		
		//Button order sheet verify
		btnOrdersheetVerify.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				btnOrdersheetVerify.setClickable(false);
				btnOrdersheetVerify.setEnabled(false);
				
				new Handler().postDelayed(new Runnable() 
				{
					@Override
					public void run() 
					{
						btnOrdersheetVerify.setClickable(true);
						btnOrdersheetVerify.setEnabled(true);
					}
				}, 500);
				switch (validatingAllItems()) 
				{
				case 1:
					showCustomDialog(VerifyItemInVehicle.this, "Alert !", "There is no item for verification.", "OK", null, null);				
					break;
				case 2:
//					showCustomDialog(VerifyItemInVehicle.this, "Alert !", "Few Items are not verified, do you want to continue? ", getString(R.string.Yes), getString(R.string.No), "ContinuePartial");
					showCustomDialog(VerifyItemInVehicle.this, "Alert !", "Few Items are not verified, please verify all items.", getString(R.string.OK), null, "ContinuePartial");
					break;
				default:
					updatePartialData();
					break;
				}
			}
		});
		
		btnContinue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				
				setResult(1111);
				finish();
				
			}
		});
		
		btnPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(VerifyItemInVehicle.this,WoosimPrinterActivity.class);
				intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_VERIFY_INVENTOTY);
				startActivity(intent);
			}
		});
		
		setTypeFaceRobotoNormal(llOrderSheet);
		
		vecVanLoadTemp = new ArrayList<VanLoadDO>();
		etSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!TextUtils.isEmpty(s))
					ivSearchCross.setVisibility(View.VISIBLE);
				else
					ivSearchCross.setVisibility(View.GONE);
				vecVanLoadTemp.clear();
				String str = s.toString().toLowerCase();
				for (int i = 0; vecOrdProduct!=null && i < vecOrdProduct.size(); i++) {
					VanLoadDO VanLoadDO = vecOrdProduct.get(i);
					if(VanLoadDO.ItemCode.toLowerCase().contains(str.toLowerCase().trim()) || VanLoadDO.Description.toLowerCase().contains(str.toLowerCase().trim()))
					{
						if(!vecVanLoadTemp.contains(VanLoadDO))
							vecVanLoadTemp.add(VanLoadDO);
					}
				}
				
				if(str.length() > 0)
					ordersheetadapter.refresh(vecVanLoadTemp);
				else
					ordersheetadapter.refresh(vecOrdProduct);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				ordersheetadapter.refresh(vecOrdProduct);
			}
		});
		
		btnContinue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnPrint.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnOrdersheetVerify.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
//		tvPageTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}
	/** initializing all the Controls  of DeliveryVerifyItemList class **/
	public void intialiseControls()
	{
		//getting ids
		llOrderListView 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llordersheet);
		btnOrdersheetVerify		=	(Button)llOrderSheet.findViewById(R.id.btnOrdersheetVerify);
		tvNoItemFound			=	(TextView)llOrderSheet.findViewById(R.id.tvNoItemFound);
		ivCheckAllItems			=	(ImageView)llOrderSheet.findViewById(R.id.ivCheckAllItems);
		btnPrint				=	(Button)llOrderSheet.findViewById(R.id.btnPrint);
		btnContinue				=	(Button)llOrderSheet.findViewById(R.id.btnContinue);
		etSearch			=	(EditText)llOrderSheet.findViewById(R.id.etSearch);
		
		ivSearchCross			=	(ImageView)llOrderSheet.findViewById(R.id.ivSearchCross);
		
		llNotVerified				=	(LinearLayout)llOrderSheet.findViewById(R.id.llNotVerified);
		tvHeader				=	(TextView)llOrderSheet.findViewById(R.id.tvHeader);
		
		lvItemList = new ListView(this);
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);

		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
        
		btnLogo.setEnabled(false);
		btnLogo.setClickable(false);
	}
	
	private static class ViewHolder
	{
		LinearLayout llCellClick;
		TextView tvProductKey,tvVendorName,tvExpiryDate,tvItemBatchCode,tvExpiryDateText,tvItemBatchCodeText;
		ImageView ivAcceptCheck;
		CustomEditText etQtyInCS;
	}
	
	public class OrderSheetAdapter extends BaseAdapter
	{
		ArrayList<VanLoadDO> vecOrder;
		private boolean isCheckVisible = true;
		public OrderSheetAdapter(ArrayList<VanLoadDO> vec)
		{
			this.vecOrder=vec;
		}
		public void refresh() 
		{
			notifyDataSetChanged();
		}
		public void refresh(boolean isCheckVisible) 
		{
			this.isCheckVisible = isCheckVisible;
			notifyDataSetChanged();
		}
		
		
		@Override
		public int getCount() 
		{
			if(vecOrder != null && vecOrder.size() > 0)
				return vecOrder.size();
			else
				return 0;
		}
		@Override
		public Object getItem(int position) 
		{
			return position;
		}
		@Override
		public long getItemId(int position) 
		{
			return 0;
		}
		public void refresh(ArrayList<VanLoadDO> vec)
		{
			this.vecOrder = vec;
			notifyDataSetChanged();
			if(vecOrder != null && vecOrder.size() > 0){
				tvNoItemFound.setVisibility(View.GONE);
			}
			else{
				tvNoItemFound.setVisibility(View.VISIBLE);
			}
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final ViewHolder viewHolder;
			VanLoadDO ordPro = vecOrder.get(position);
			//inflate invoice_list_cell layout here
			if(convertView == null)
			{
				convertView			   	 	 	 =	(LinearLayout)getLayoutInflater().inflate(R.layout.item_list_verify_cell,null);
				viewHolder				 	 	 =  new ViewHolder();
				viewHolder.llCellClick		 	 =	(LinearLayout)convertView.findViewById(R.id.llCellClick);
				viewHolder.tvProductKey		 	 =	(TextView)convertView.findViewById(R.id.tvProductKey);
				viewHolder.tvVendorName		 	 =	(TextView)convertView.findViewById(R.id.tvVendorName);
				viewHolder.etQtyInCS			 =	(CustomEditText)convertView.findViewById(R.id.etQtyInCS);
				
				viewHolder.tvExpiryDate		 	 =	(TextView)convertView.findViewById(R.id.tvExpiryDate);
				viewHolder.tvItemBatchCode	 	 =	(TextView)convertView.findViewById(R.id.tvItemBatchCode);
				viewHolder.tvExpiryDateText		 =	(TextView)convertView.findViewById(R.id.tvExpiryDateText);
				viewHolder.tvItemBatchCodeText	 =	(TextView)convertView.findViewById(R.id.tvItemBatchCodeText);
				viewHolder.ivAcceptCheck		 =	(ImageView)convertView.findViewById(R.id.ivAcceptCheckItems);
				convertView.setTag(viewHolder);
			}
			else
				viewHolder	= (ViewHolder) convertView.getTag();
			
			if(isCheckVisible)
				viewHolder.ivAcceptCheck.setVisibility(View.VISIBLE);
			else{
				viewHolder.ivAcceptCheck.setVisibility(View.GONE);
			}
			/**
			 * As per requested by Fayaz remove editing of quantity while verifying item.
			 */
			viewHolder.etQtyInCS.setEnabled(false);
			viewHolder.etQtyInCS.setFocusable(false);
//			viewHolder.etQtyInCS.setTag(R.string.key_field_type,"UNIT");
//			viewHolder.etQtyInCS.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//				@Override
//				public void onFocusChange(View v, boolean hasFocus) {
//
//					if (hasFocus) {
//						view  = v;
//						viewHolder.etQtyInCS.addTextChangedListener(new TextChangeListener());
//						new Handler().postDelayed(new Runnable() {
//							@Override
//							public void run() {
//								hideKeyBoard(viewHolder.etQtyInCS);
//								onKeyboardFocus(viewHolder.etQtyInCS,0,true);
//							}
//						}, 10);
//					}
//					else
//						view = null;
//				}
//			});
			viewHolder.tvProductKey.setText(ordPro.ItemCode);
			viewHolder.tvVendorName.setText(""+ordPro.Description);
			
			viewHolder.tvExpiryDate.setVisibility(View.GONE);
			viewHolder.tvExpiryDateText.setVisibility(View.GONE);
			
			viewHolder.tvItemBatchCode.setVisibility(View.GONE);
			viewHolder.tvItemBatchCodeText.setVisibility(View.GONE);
			
			viewHolder.etQtyInCS.setTag(ordPro);

			if(ordPro.inProcessQuantityLevel1 > 0)
				viewHolder.etQtyInCS.setText(""+ordPro.shippedQuantityLevel1);
			else
				viewHolder.etQtyInCS.setText(""+0);
			
			viewHolder.tvProductKey.setTypeface(AppConstants.Roboto_Condensed);
			viewHolder.tvVendorName.setTypeface(AppConstants.Roboto_Condensed_Bold);
			//setting Enabled false to Edit Text
			
			if(isClicked != null && ordPro.ischecked )
				viewHolder.ivAcceptCheck.setImageResource(R.drawable.checkbox);
			else
				viewHolder.ivAcceptCheck.setImageResource(R.drawable.uncheckbox);
			
			viewHolder.ivAcceptCheck.setTag(position);
			viewHolder.llCellClick.setTag(position);
			//Click event for llLayout
			viewHolder.llCellClick.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					int mPosition = StringUtils.getInt(v.getTag().toString());
					if(!isClicked.contains(mPosition))
					{
						isClicked.add(mPosition);
						vecOrder.get(mPosition).ischecked = true; 
						viewHolder.ivAcceptCheck.setImageResource(R.drawable.checkbox);
					}
					else
					{
						isClicked.remove((Integer)mPosition);
						vecOrder.get(mPosition).ischecked = false;
						viewHolder.ivAcceptCheck.setImageResource(R.drawable.uncheckbox);
					}
					if(isAllVerified())
					{
						ivCheckAllItems.setTag("checked");
						ivCheckAllItems.setImageResource(R.drawable.checkbox_white);
					}
					else
					{
						ivCheckAllItems.setTag("unchecked");
						ivCheckAllItems.setImageResource(R.drawable.uncheckbox_white);
					}
				}
			});
			//Click event for ivAcceptCheck
			viewHolder.ivAcceptCheck.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					int mPosition = StringUtils.getInt(v.getTag().toString());
					if(!isClicked.contains(mPosition))
					{
						isClicked.add(mPosition);
						vecOrder.get(mPosition).ischecked = true;
						viewHolder.ivAcceptCheck.setImageResource(R.drawable.checkbox);
					}
					else
					{
						isClicked.remove((Integer)mPosition);
						vecOrder.get(mPosition).ischecked = false;
						viewHolder.ivAcceptCheck.setImageResource(R.drawable.uncheckbox);
					}
					if(isAllVerified())
					{
						ivCheckAllItems.setTag("checked");
						ivCheckAllItems.setImageResource(R.drawable.checkbox_white);
					}
					else
					{
						ivCheckAllItems.setTag("unchecked");
						ivCheckAllItems.setImageResource(R.drawable.uncheckbox_white);
					}
				}
			});
			
			return convertView;
		}
	}	
	
	private boolean isAllVerified()
	{
		if(isClicked.size() == vecOrdProduct.size())
		{
			return true;
		}
		return false;
	}
	private int validatingAllItems()
	{
		int responce = 0;
		if(vecOrdProduct == null || vecOrdProduct.size() == 0)//tvNoItemFound.isShown()
			responce = 1;
		else if((isClicked.size() != vecOrdProduct.size()) || (vecOrdProduct.size() > ordersheetadapter.vecOrder.size()))
			responce =  2;
		return responce;
	}
	
	@Override
	public void onPause() 
	{
		super.onPause();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		vecOrdProduct=null;
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("verify"))
		{
			ivCheckAllItems.setVisibility(View.GONE);
			btnPrint.setVisibility(View.VISIBLE);
			btnContinue.setText("Finish");
			btnContinue.setVisibility(View.VISIBLE);
			btnOrdersheetVerify.setVisibility(View.GONE);
			ordersheetadapter.refresh(false);
			setResult(1111);
			finish();
		}
		else if(from.equalsIgnoreCase("UOMConversion"))
			finish();
		/*else if(from.equalsIgnoreCase("ContinuePartial")){
			updatePartialData();
		}*/
	}
	private void updatePartialData() {
		new Thread(new Runnable() {
			
			boolean isShippedQtyCalculated=true;
			@Override
			public void run() {
				hashArrUoms = new ProductsDA().getUOMConversion(TrxDetailsDO.getItemUomLevel1());
				for(VanLoadDO vanLoadDO:vecOrdProduct)
				{
					UOMConversionFactorDO uomConversionFactorDO = hashArrUoms.get(vanLoadDO.ItemCode);
					if(uomConversionFactorDO!=null)
					{
						int exConversion = (int) uomConversionFactorDO.eaConversion;
						vanLoadDO.ShippedQuantity = vanLoadDO.shippedQuantityLevel1*exConversion + vanLoadDO.shippedQuantityLevel3;
						Log.e("ShippedQuantity",""+vanLoadDO.ShippedQuantity+"="+vanLoadDO.shippedQuantityLevel1*exConversion+"==="+vanLoadDO.shippedQuantityLevel3);
					}
					else {
						isShippedQtyCalculated=false;						
						Log.e("ShippedQuantity","No Uom Conversion");
					}

					if(vanLoadDO.shippedQuantityLevel1>vanLoadDO.ShippedQuantity)
						isShippedQtyCalculated=false;
					
					/*if(vanLoadDO.ischecked)
					{
						UOMConversionFactorDO uomConversionFactorDO=hashArrUoms.get(vanLoadDO.ItemCode);
						if(uomConversionFactorDO!=null)
						{
							int exConversion=(int) uomConversionFactorDO.eaConversion;
							vanLoadDO.ShippedQuantity=vanLoadDO.shippedQuantityLevel1*exConversion+vanLoadDO.shippedQuantityLevel3;
							Log.e("ShippedQuantity",""+vanLoadDO.ShippedQuantity+"="+vanLoadDO.shippedQuantityLevel1*exConversion+"==="+vanLoadDO.shippedQuantityLevel3);
						}
						else
							Log.e("ShippedQuantity","No Uom Conversion");

						if(vanLoadDO.shippedQuantityLevel1>vanLoadDO.ShippedQuantity)
							isShippedQtyCalculated=false;
					}
					else
					{
						vanLoadDO.SellableQuantity = 0;
						vanLoadDO.shippedQuantityLevel1 = 0;
						vanLoadDO.shippedQuantityLevel3 = 0;
						vanLoadDO.ShippedQuantity =0;
					}*/
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(isShippedQtyCalculated)
							showSignatureDialog();
						else
							showCustomDialog(VerifyItemInVehicle.this,getString(R.string.warning),getString(R.string.please_try_again),getString(R.string.OK),null,"UOMConversion");
					}
				});
			}
		}).start();
	}
	private boolean isVarifcationSignatureDone = false;
	private boolean isVarSignDoneDriver = false;
	private boolean isCollected=false;
	@SuppressWarnings("deprecation")
	private void showSignatureDialog()
	{
		final Dialog dialog 			= new Dialog(this,R.style.Dialog);
		LinearLayout llSignature 	  	= (LinearLayout) inflater.inflate(R.layout.signature_driver_supervsor_new, null);
		final LinearLayout llSignSupervisor = (LinearLayout)llSignature.findViewById(R.id.llSignSupervisor);
		final LinearLayout llSignDriver = (LinearLayout)llSignature.findViewById(R.id.llSignDriver);
		final LinearLayout llLPO		= (LinearLayout)llSignature.findViewById(R.id.llLPO);
	
		TextView tvSalesmanSignature	= (TextView)llSignature.findViewById(R.id.tvSalesmanSignature);
		TextView tvLogisticsSignature	= (TextView)llSignature.findViewById(R.id.tvLogisticsSignature);
		Button btnOK 					= (Button)llSignature.findViewById(R.id.btnOK);
		Button btnSKCear 				= (Button)llSignature.findViewById(R.id.btnSKCear);
		Button btnDriverCear 			= (Button)llSignature.findViewById(R.id.btnDriverCear);
		Button btnCancle 			= (Button)llSignature.findViewById(R.id.btnCancle);
		
		dialog.addContentView(llSignature,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		dialog.show();
		
		llLPO.setVisibility(View.GONE);
		
		isVarifcationSignatureDone = false;
		isVarSignDoneDriver = false;
		
		myViewManager  = new MyView(this, false);
		myViewManager.setDrawingCacheEnabled(true);
		myViewManager.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT , (int)(180 * px)));
		myViewManager.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		llSignSupervisor.addView(myViewManager);
		
		
		myViewDriver  = new MyView(this, true);
		myViewDriver.setDrawingCacheEnabled(true);
		myViewDriver.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT , (int)(180 * px)));
		myViewDriver.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		llSignDriver.addView(myViewDriver);
		
		setTypeFaceRobotoNormal(llSignature);
//		setTypeFaceRobotoBold(llSignature);
		tvSalesmanSignature.setTypeface(Typeface.DEFAULT_BOLD);
		tvLogisticsSignature.setTypeface(Typeface.DEFAULT_BOLD);
		btnOK.setTypeface(Typeface.DEFAULT_BOLD);
		btnCancle.setTypeface(Typeface.DEFAULT_BOLD);
		btnSKCear.setTypeface(Typeface.DEFAULT_BOLD);
		btnDriverCear.setTypeface(Typeface.DEFAULT_BOLD);
		
		tvLogisticsSignature.setText("Requested Salesman Signature");
		
		btnOK.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(!isVarifcationSignatureDone)
					showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), "Please take logistic's signature.", "OK", null, "");
				else if(!isVarSignDoneDriver)
					showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), "Please sign before submitting the stock verification.", "OK", null, "");
				else	
				{
					showLoader("Please wait...");
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							
							VerifyRequestDO verifyRequestDO = new VerifyRequestDO();
							
							verifyRequestDO.movementCode 		= movementId;
							verifyRequestDO.movementType 		= movementType;
							verifyRequestDO.movementStatus 		= "99";
							verifyRequestDO.vecVanLodDOs = vecOrdProduct;
							Bitmap bitmap = getBitmap(myViewManager);
							ByteArrayOutputStream stream = new ByteArrayOutputStream();
							if(bitmap != null)
							{
								bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
								verifyRequestDO.logisticSignature = Base64.encodeBytes(stream.toByteArray());
							}
							
							//customer signature
							Bitmap image = getBitmap(myViewDriver);
							ByteArrayOutputStream streams = new ByteArrayOutputStream();
							if(image != null)
							{
								image.compress(Bitmap.CompressFormat.JPEG, 100, streams);
								verifyRequestDO.salesmanSignature = Base64.encodeBytes(streams.toByteArray());
							}
							
							if(isNetworkConnectionAvailable(VerifyItemInVehicle.this))
							{
								final boolean isUpdated = uploadLoadRequest(verifyRequestDO);
								
								if(isUpdated)
								{
									status = 1;
									new InventoryDA().updateMovemetStatuStatus(movementId, 100,verifyRequestDO.vecVanLodDOs);
									String empNo = preference.getStringFromPreference(Preference.EMP_NO, "");
									
									loadAllMovements_Sync("Refreshing data...",empNo);
									loadVanStock_Sync("Loading Stock...", empNo);
									
									isCollected=true;
								}
							}
							else 
								status = 2;
								
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									dialog.dismiss();
									if(status == 1)
									{
										showCustomDialog(VerifyItemInVehicle.this, getString(R.string.verified), getString(R.string.stock_verified_in_the_van_as_per_the_order),
												getString(R.string.OK), null, "verify", false);
									}
									else if(status == 2)
										showCustomDialog(VerifyItemInVehicle.this, "Alert !", "There is no internet connection. Please check your internet connection.", "OK", null, null);
									else
										showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), "Error occurred while verifying. Please try again.", getString(R.string.OK), null, "");
									hideLoader();
								}
							});
						}
					}).start();
				}
			}
		});
		
		btnDriverCear.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				isVarSignDoneDriver = false;
				if(myViewDriver != null)
					myViewDriver.clearCanvas();
			}
		});
		
		btnSKCear.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				isVarifcationSignatureDone = false;
				if(myViewManager != null)
					myViewManager.clearCanvas();
			}
		});
		btnCancle.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				dialog.dismiss();
			}
		});
	}
	@Override
	public void onBackPressed() {
		if(isCollected){
			setResult(1111);
			finish();
		}else
		super.onBackPressed();
	}
	
	public class MyView extends View 
	{
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        float x,y;
        int width = 480, height = 800;
        private boolean isDriver = false;
        
        @SuppressWarnings("deprecation")
		public MyView(Context c, boolean isDriver)
        {
            super(c);
            Display display = 	getWindowManager().getDefaultDisplay(); 
            width 			= 	display.getWidth();
            height 			= 	display.getHeight();
            this.isDriver = isDriver; 
            if(mBitmap != null)
            	mBitmap.recycle();
            
            mBitmap 		= 	Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            mCanvas 		= 	new Canvas(mBitmap);
            mPath 			= 	new Path();
            mBitmapPaint	= 	new Paint(Paint.DITHER_FLAG);
           
            mBitmapPaint.setAntiAlias(true);
            mBitmapPaint.setDither(true);
            mBitmapPaint.setColor(Color.BLACK);
            mBitmapPaint.setStyle(Paint.Style.STROKE);
            mBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
            mBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
            mBitmapPaint.setStrokeWidth(2);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh)
        {
            super.onSizeChanged(w, h, oldw, oldh);
        }
        
        @Override
        protected void onDraw(Canvas canvas) 
        {
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
        }
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        
        private void touch_start(float x, float y)
        {
            mPath.reset();
            mPath.moveTo(x, y);
            mPath.addCircle(x, y,(.3f),Direction.CW);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) 
        {
          float dx = Math.abs(x - mX);
          float dy = Math.abs(y - mY);
          if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) 
          {
           mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
           mX = x;
           mY = y;
          }
	    }
	    private void touch_up()
	    {
	         mPath.lineTo(mX, mY);
	         mCanvas.drawPath(mPath, mPaint);
	         mPath.reset();
	     }
        
	    public void clearCanvas()
	    {
	    	mBitmap 		= 	Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
	        mCanvas 		= 	new Canvas(mBitmap);
	    	invalidate();
	    }
	    
        @Override
        public boolean onTouchEvent(MotionEvent event) 
        {
             x = event.getX();
             y = event.getY();
            
            switch (event.getAction()) 
            {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                    
                case MotionEvent.ACTION_MOVE:
                	if(this.isDriver)
                		isVarSignDoneDriver = true;
                	else
                		isVarifcationSignatureDone = true;
                    touch_move(x, y);
                    invalidate();
                    break;
                    
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
        	return true;
       }
	}
	
	private Bitmap getBitmap(MyView myView)
	{
		Bitmap bitmap = myView.getDrawingCache(true);
		return bitmap;
	}
	
	private boolean uploadLoadRequest(VerifyRequestDO verifyRequestDO) 
	{
		try
		{
			if(verifyRequestDO != null)
			{
				VerifyMovementParser insertLoadParser = new VerifyMovementParser(VerifyItemInVehicle.this);
				new ConnectionHelper(null).sendRequest(VerifyItemInVehicle.this,BuildXMLRequest.VerifyRequestRequests(verifyRequestDO), insertLoadParser, ServiceURLs.ShipStockMovementsFromXML);
				return insertLoadParser.getStatus();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return true;
	}
	private View view;
	class TextChangeListener implements TextWatcher {

		public TextChangeListener() {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			}

		@Override
		public void afterTextChanged(Editable s) {
			if(view!=null && listScrollState == 0)
			{
				VanLoadDO vanLoadDO = (VanLoadDO) view.getTag();
				int qty=StringUtils.getInt(s.toString());
				if(vanLoadDO != null)
				{
					if(((String)view.getTag(R.string.key_field_type)).equalsIgnoreCase("UNIT")){
						if(qty>vanLoadDO.inProcessQuantityLevel1){
							showToast("Entered quantity should not be greater than approved quantity.");
							vanLoadDO.shippedQuantityLevel1=vanLoadDO.inProcessQuantityLevel1;
							((EditText)view).setText(""+vanLoadDO.shippedQuantityLevel1);
						}else
							vanLoadDO.shippedQuantityLevel1 = qty;
					}
					else{
						if(qty>vanLoadDO.inProcessQuantityLevel3){
							showToast("Entered quantity should not be greater than approved quantity.");
							vanLoadDO.shippedQuantityLevel3=vanLoadDO.inProcessQuantityLevel3;
							((EditText)view).setText(""+vanLoadDO.shippedQuantityLevel3);
						}else
							vanLoadDO.shippedQuantityLevel3 = qty;
					}
				}
			}
			
		}
	}
}
