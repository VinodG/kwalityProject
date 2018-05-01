package com.winit.alseer.pinch;

import httpimage.HttpImageManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.CustomEditText;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataobject.HHInventryQTDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.UOMConversionFactorDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.MyApplicationNew;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;
import com.winit.sfa.salesman.MyApplication;
import com.winit.sfa.salesman.SalesManRecommendedOrder;

@SuppressLint("ValidFragment")
public class ListBrandRecomendedFragment extends Fragment {
	private HashMap<String, Vector<TrxDetailsDO>> hmItems;
	public  CaptureInventaryAdapter adapterForCapture;
	private HashMap<String, HHInventryQTDO> hmInventory;
	private ExpandableListView expandableList;
	private TextView tvNoSearchResult,tvBrands;
	
	private LayoutInflater inflater;
	private int listScrollState;
	
	private Vector<String> arrBrands=new Vector<String>();
	private int TRXTYPE_ORDER;
	
	private int widthFor2Views = 0;
	private JourneyPlanDO mallsDetails;
	
	@SuppressLint("ValidFragment")
	public ListBrandRecomendedFragment(JourneyPlanDO mallsDetails, int pos,
			HashMap<String, Vector<TrxDetailsDO>> hashMap, HashMap<String, HHInventryQTDO> hmInventory,Vector<String> arrBrands, int TRXTYPE_ORDER) 
	{
		this.mallsDetails = mallsDetails;
		this.hmItems = hashMap;
		this.hmInventory = hmInventory;
		this.arrBrands = arrBrands;
		if(arrBrands!=null && !arrBrands.contains("All Brands"))
			this.arrBrands.add(0, "All Brands");
		this.TRXTYPE_ORDER = TRXTYPE_ORDER;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		LinearLayout llMain = (LinearLayout) inflater.inflate(R.layout.brand_recommended_order_list, null);
		this.inflater = inflater;
		expandableList = (ExpandableListView) llMain.findViewById(R.id.expandableList);
		tvNoSearchResult =  (TextView) llMain.findViewById(R.id.tvNoSearchResult);
		tvBrands=  (TextView) llMain.findViewById(R.id.tvAgencies);
		
		
		TextView tvCases			=	(TextView) llMain.findViewById(R.id.tvCases);
		TextView tvUnits			=	(TextView) llMain.findViewById(R.id.tvUnits);
		
		TextView tvVanQty			=	(TextView) llMain.findViewById(R.id.tvVanQty);
		TextView tvMissedQty		=	(TextView) llMain.findViewById(R.id.tvMissedQty);
		ImageView ivVanQtyDivider	=	(ImageView) llMain.findViewById(R.id.ivVanQtyDivider);
		ImageView ivUnitsDivider	=	(ImageView) llMain.findViewById(R.id.ivUnitsDivider);
		ImageView ivMissedDivider	=	(ImageView) llMain.findViewById(R.id.ivMissedDivider);
		
		if(TRXTYPE_ORDER != TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && TRXTYPE_ORDER != TrxHeaderDO.get_TYPE_FREE_DELIVERY()
				&& TRXTYPE_ORDER != TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
		{
			widthFor2Views = (int) (60*((BaseActivity)getActivity()).px);
			tvCases.setLayoutParams(new LinearLayout.LayoutParams(widthFor2Views, LayoutParams.WRAP_CONTENT));
			tvUnits.setLayoutParams(new LinearLayout.LayoutParams(widthFor2Views, LayoutParams.WRAP_CONTENT));
			ivVanQtyDivider.setVisibility(View.VISIBLE);
			tvVanQty.setVisibility(View.INVISIBLE);
			tvMissedQty.setVisibility(View.INVISIBLE);
			ivMissedDivider.setVisibility(View.INVISIBLE);
			ivUnitsDivider.setVisibility(View.INVISIBLE);
		}
		else if((((BaseActivity)getActivity()).preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(((BaseActivity)getActivity()).preference.PRESELLER) 
				&& TRXTYPE_ORDER == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER()))
		{
			widthFor2Views = (int) (60*((BaseActivity)getActivity()).px);
			tvCases.setLayoutParams(new LinearLayout.LayoutParams(widthFor2Views, LayoutParams.WRAP_CONTENT));
			tvUnits.setLayoutParams(new LinearLayout.LayoutParams(widthFor2Views, LayoutParams.WRAP_CONTENT));
			ivVanQtyDivider.setVisibility(View.VISIBLE);
			tvVanQty.setVisibility(View.INVISIBLE);
			tvMissedQty.setVisibility(View.INVISIBLE);
			ivMissedDivider.setVisibility(View.INVISIBLE);
			ivUnitsDivider.setVisibility(View.INVISIBLE);
		}
		else
		{
			tvVanQty.setVisibility(View.VISIBLE);
//			tvMissedQty.setVisibility(View.VISIBLE);
			ivVanQtyDivider.setVisibility(View.VISIBLE);
			ivMissedDivider.setVisibility(View.VISIBLE);
		}
		
/*		
		tvBrands.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
//				showAllBrands(v,arrBrands);
			}
		});*/
		

		tvNoSearchResult.setTypeface(AppConstants.Roboto_Condensed_Bold);

		adapterForCapture = new CaptureInventaryAdapter(hmItems);
		expandableList.setAdapter(adapterForCapture);
		expandableList.setOnScrollListener(new OnScrollListener()
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
		
		((BaseActivity) getActivity()).setTypeFaceRobotoNormal(llMain);
		if(hmItems!=null && hmItems.size()>0){
			tvNoSearchResult.setVisibility(View.GONE);
			expandableList.setVisibility(View.VISIBLE);
		}else{
			tvNoSearchResult.setVisibility(View.VISIBLE);
			expandableList.setVisibility(View.GONE);
		}
		return llMain;
	}
	
	public class CaptureInventaryAdapter extends BaseExpandableListAdapter {
//		private View view;
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

					try {
						if(listScrollState == 0)
							handleText(s,null);
					} catch (Exception e) {
						e.printStackTrace();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				if(((BaseActivity)getActivity()).viewForEditText!=null)
				{
					TrxDetailsDO objDo  = (TrxDetailsDO) ((BaseActivity)getActivity()).viewForEditText.getTag(R.string.key_product);
					((TextView)((BaseActivity)getActivity()).viewForEditText.getTag(R.string.key_missed_qty)).setText(objDo.missedBU+"");
					((TextView)((BaseActivity)getActivity()).viewForEditText.getTag(R.string.key_uom)).setTag(R.string.request_Bu,""+objDo.requestedSalesBU);
					

//					if(objDo.priceUsedLevel1 >0)
//						((TextView)view.getTag(R.string.key_price)).setText(((BaseActivity)getActivity()).decimalFormat.format(objDo.priceUsedLevel1));
//					else
//						((TextView)view.getTag(R.string.key_price)).setText(((BaseActivity)getActivity()).decimalFormat.format(objDo.basePrice));
					
//					if(objDo.quantityLevel1 >0)
//					{
//						((LinearLayout)view.getTag(R.string.key_gsv_ll)).setVisibility(View.VISIBLE);
//						((ImageView)view.getTag(R.string.key_gsv_iv_div)).setVisibility(View.VISIBLE);
//						
//						((TextView)view.getTag(R.string.key_gsv_label)).setVisibility(View.VISIBLE);
//						((TextView)view.getTag(R.string.key_gsv)).setVisibility(View.VISIBLE);
//						
//						((TextView)view.getTag(R.string.key_gsv)).setText(((BaseActivity)getActivity()).decimalFormat.format(objDo.priceUsedLevel1*objDo.quantityLevel1));
//					}
//					else
//					{
//						((TextView)view.getTag(R.string.key_gsv_label)).setVisibility(View.GONE);
//						((TextView)view.getTag(R.string.key_gsv)).setVisibility(View.GONE);
//						
//						((LinearLayout)view.getTag(R.string.key_gsv_ll)).setVisibility(View.GONE);
//						((ImageView)view.getTag(R.string.key_gsv_iv_div)).setVisibility(View.GONE);
//					}
					
				}
			}
		}
		private Vector<String> vecCategoryIds;
		private HashMap<String, Vector<TrxDetailsDO>> hmItemsL;
		public CaptureInventaryAdapter(
				HashMap<String, Vector<TrxDetailsDO>> hmItems) {
			hmItemsL=hmItems;
			initializeCategories();
		}
		
		public void refresh(HashMap<String, Vector<TrxDetailsDO>> tmpSearched) {
			if(tmpSearched!=null && tmpSearched.size()>0){
				this.hmItemsL=tmpSearched;
				adapterForCapture = new CaptureInventaryAdapter(hmItemsL);
				expandableList.setAdapter(adapterForCapture);
				tvNoSearchResult.setVisibility(View.GONE);
				expandableList.setVisibility(View.VISIBLE);
			}else{
				tvNoSearchResult.setVisibility(View.VISIBLE);
				expandableList.setVisibility(View.GONE);
			}
		}

		public void initializeCategories(){
			if (hmItemsL != null) {
				vecCategoryIds = new Vector<String>();
				Set<String> set = hmItemsL.keySet();
				Iterator<String> iterator = set.iterator();
				while (iterator.hasNext())
					vecCategoryIds.add(iterator.next());
				if(vecCategoryIds.size() > 1)
					sort(vecCategoryIds);
			}
		}
		
		public void sort(Vector<String> vec){
			Collections.sort(vec, new Comparator<String>() {
			    @Override
			   public int compare(String s1, String s2) {
			    	return s1.compareToIgnoreCase(s2);
			    }
			});
		}
		
		TrxDetailsDO objItem;
		private void handleText(CharSequence s,TrxDetailsDO obj) {

			synchronized (MyApplication.SALES_UNITS_LOCK) {
				try {
					objItem = null;
					if (((BaseActivity)getActivity()).viewForEditText != null) {
						objItem = (TrxDetailsDO) ((BaseActivity)getActivity()).viewForEditText.getTag(R.string.key_product);
					}
					else if(obj!=null)
						objItem = obj;
					if (objItem != null)
					{
						if(s==null)
							s="";
						objItem.requestedSalesBU = StringUtils.getFloat(s.toString());
						UOMConversionFactorDO uomFactorDO = objItem.hashArrUoms.get(objItem.itemCode+/*objItem.UOM+*/"UNIT");
						int quanity=0;
						int eaConversion=0;
						if(uomFactorDO!=null)
						{
//							quanity =(int) (uomFactorDO.eaConversion*objItem.requestedSalesBU);
							quanity =(int) (objItem.requestedSalesBU);
							objItem.missedBU = ((SalesManRecommendedOrder)getActivity()).isInventoryAvail(objItem,quanity);
							if(uomFactorDO!=null && uomFactorDO.eaConversion!=0){
								eaConversion=(int) uomFactorDO.eaConversion;
//								objItem.quantityLevel1 = (quanity-objItem.missedBU)/uomFactorDO.eaConversion;
								objItem.quantityLevel1 = (quanity-objItem.missedBU);
							}
							else
								objItem.quantityLevel1 = (quanity-objItem.missedBU);
							objItem.quantityLevel2=0;
							objItem.quantityLevel3=0;
						}
						LogUtils.debug("quantityBU", "quanity:"+quanity);
						LogUtils.debug("quantityBU", "missedBU:"+objItem.missedBU);
						objItem.quantityBU = quanity-objItem.missedBU;
						
						((SalesManRecommendedOrder)getActivity()).updateDistinctItem(objItem);
						if(((BaseActivity)getActivity()).viewForEditText!=null){
							new Handler().postDelayed(new Runnable()
							{
								@Override
								public void run() 
								{
									LogUtils.debug("quantityBU", "quantityBU"+objItem.quantityBU);
									if(objItem.quantityBU>0){
										LinearLayout llGSVValue = (LinearLayout) ((BaseActivity)getActivity()).viewForEditText.getTag(R.string.key_gsv_ll);
										ImageView ivSeprator = (ImageView) ((BaseActivity)getActivity()).viewForEditText.getTag(R.string.key_gsv_iv_div);
										llGSVValue.setVisibility(View.VISIBLE);
										ivSeprator.setVisibility(View.VISIBLE);
										TextView tvTotalValue = (TextView) ((BaseActivity)getActivity()).viewForEditText.getTag(R.string.key_gsv);//TRXTYPE_ORDER
										String gsv = "";
//								if(TRXTYPE_ORDER == TrxHeaderDO.get_TYPE_FREE_DELIVERY())
//									gsv=objItem.UOM+" "+((BaseActivity)getActivity()).decimalFormat.format(objItem.quantityLevel1)+" X "+(0.0)+" = 0.0"/*+(((BaseActivity)getActivity()).decimalFormat.format(objItem.quantityLevel1*objItem.priceUsedLevel1))*/;
//								else
										gsv=objItem.UOM+" "+((BaseActivity)getActivity()).decimalFormat.format(objItem.quantityLevel1)+" X "+(objItem.priceUsedLevel1)+" = "+(((BaseActivity)getActivity()).decimalFormat.format(objItem.quantityLevel1*objItem.priceUsedLevel1));
										tvTotalValue.setText(gsv);
										
										objItem.promotionalDiscountAmount = calculateDiscount(objItem);
										objItem.statementDiscountAmnt = calculatestatementDiscount(objItem);
										
									}else{
										LinearLayout llGSVValue = (LinearLayout) ((BaseActivity)getActivity()).viewForEditText.getTag(R.string.key_gsv_ll);
										ImageView ivSeprator = (ImageView) ((BaseActivity)getActivity()).viewForEditText.getTag(R.string.key_gsv_iv_div);
										llGSVValue.setVisibility(View.GONE);
										ivSeprator.setVisibility(View.GONE);
									}
									
									//Added to fix stuck of keyboard.
									((BaseActivity)getActivity()).viewForEditText.requestFocus();
								}
							}, 100);
						}
						
					}	
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		
		private float calculateDiscount(TrxDetailsDO objItem) 
		{
			float discount = 0.0f;
			if(mallsDetails != null && TRXTYPE_ORDER != TrxHeaderDO.get_TYPE_FREE_DELIVERY())
				discount = (float) ((objItem.CSPrice * objItem.quantityBU * (StringUtils.getDouble(mallsDetails.PromotionalDiscount)+((SalesManRecommendedOrder)getActivity()).promotionDisc))/100);

			return discount;
		}
		private float calculatestatementDiscount(TrxDetailsDO objItem) 
		{
			float discount = 0.0f;
			if(mallsDetails != null && TRXTYPE_ORDER != TrxHeaderDO.get_TYPE_FREE_DELIVERY())
				discount = (float) ((objItem.CSPrice * objItem.quantityBU * (StringUtils.getDouble(mallsDetails.statementdiscount)+((SalesManRecommendedOrder)getActivity()).promotionDisc))/100);

			return discount;
		}

		@Override
		public int getGroupCount() {
			return vecCategoryIds.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			
			if(hmItemsL.get(vecCategoryIds.get(groupPosition))!=null)
				return hmItemsL.get(vecCategoryIds.get(groupPosition)).size();
			
			return 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			
			if(convertView == null)
			convertView = inflater .inflate(R.layout.list_brand_group_item, null);
			TextView tvBrandName = (TextView) convertView .findViewById(R.id.tvBrandName);
			tvBrandName.setText(vecCategoryIds.get(groupPosition));
			expandableList.expandGroup(groupPosition);
			return convertView;
		}

		@Override
		public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			try {
				final ViewHolder viewHolder;
				
				final TrxDetailsDO trxDetailsDO = hmItemsL.get(vecCategoryIds.get(groupPosition)).get(childPosition);
				if(convertView == null)
				{
					convertView = inflater.inflate( R.layout.brand_recommended_order_list_item_cell, null);
					viewHolder = new ViewHolder();
					
					viewHolder.tvHeaderText = (TextView) convertView.findViewById(R.id.tvHeaderText);
					viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
					viewHolder.tvVanQty = (TextView) convertView.findViewById(R.id.tvVanQty);
					viewHolder.tvUOM = (TextView) convertView.findViewById(R.id.tvItemUOM);
					viewHolder.tvMissedQty = (TextView) convertView.findViewById(R.id.tvMissedQty);
					viewHolder.tvDescription2 = (TextView) convertView.findViewById(R.id.tvDescription2);
					viewHolder.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
					viewHolder.evUnits = (CustomEditText) convertView.findViewById(R.id.evUnits);
					viewHolder.ivItem = (ImageView) convertView.findViewById(R.id.ivItem);
					viewHolder.tvDiscountAplied = (TextView) convertView.findViewById(R.id.tvDiscountAplied);
					viewHolder.ivDivider = (ImageView) convertView.findViewById(R.id.ivDivider);
					
					viewHolder.tvUnitPriceLabel = (TextView) convertView.findViewById(R.id.tvUnitPriceLabel);
					viewHolder.tvUnitPrice = (TextView) convertView.findViewById(R.id.tvUnitPrice);
					viewHolder.tvTotalPrice = (TextView) convertView.findViewById(R.id.tvTotalPrice);
					viewHolder.tvTotalPriceLabel = (TextView) convertView.findViewById(R.id.tvTotalPriceLabel);
					viewHolder.rlVanMissCellDivider	=	(ImageView) convertView.findViewById(R.id.rlVanMissCellDivider);
					viewHolder.evUnitsCellDivider	=	(ImageView) convertView.findViewById(R.id.evUnitsCellDivider);
					
					viewHolder.llGRVLabel		=	(LinearLayout) convertView.findViewById(R.id.llGRVLabel);
					viewHolder.ivGRVLabelSep	=	(ImageView) convertView.findViewById(R.id.ivGRVLabelSep);
					viewHolder.llClickToDownLoad = (LinearLayout) convertView.findViewById(R.id.llClickToDownLoad);					
					convertView.setTag(viewHolder);
				}
				else
					viewHolder = (ViewHolder) convertView.getTag();
				
				viewHolder.ivItem.setVisibility(View.GONE);
//				viewHolder.llClickToDownLoad.setPadding(3, 3, -10, 3);
//				if(TRXTYPE_ORDER != TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && TRXTYPE_ORDER != TrxHeaderDO.get_TYPE_FREE_DELIVERY())
				if(TRXTYPE_ORDER != TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && TRXTYPE_ORDER != TrxHeaderDO.get_TYPE_FREE_DELIVERY()
						&& TRXTYPE_ORDER != TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
				{
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthFor2Views,LayoutParams.WRAP_CONTENT,2);
					viewHolder.tvUOM.setLayoutParams(params);
					viewHolder.evUnits.setLayoutParams(params);
					
					viewHolder.tvMissedQty.setVisibility(View.INVISIBLE);
					viewHolder.ivDivider.setVisibility(View.INVISIBLE);
					viewHolder.tvVanQty.setVisibility(View.INVISIBLE);
					viewHolder.rlVanMissCellDivider.setVisibility(View.VISIBLE);
					viewHolder.evUnitsCellDivider.setVisibility(View.INVISIBLE);
				}
				else if((((BaseActivity)getActivity()).preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(((BaseActivity)getActivity()).preference.PRESELLER) 
						&& TRXTYPE_ORDER == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER()))
				{
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthFor2Views,LayoutParams.WRAP_CONTENT,2);
					viewHolder.tvUOM.setLayoutParams(params);
					viewHolder.evUnits.setLayoutParams(params);
					
					viewHolder.tvMissedQty.setVisibility(View.INVISIBLE);
					viewHolder.ivDivider.setVisibility(View.INVISIBLE);
					viewHolder.tvVanQty.setVisibility(View.INVISIBLE);
					viewHolder.rlVanMissCellDivider.setVisibility(View.VISIBLE);
					viewHolder.evUnitsCellDivider.setVisibility(View.INVISIBLE);
				}
				else
				{
					viewHolder.tvMissedQty.setVisibility(View.VISIBLE);
					viewHolder.ivDivider.setVisibility(View.VISIBLE);
					viewHolder.tvVanQty.setVisibility(View.VISIBLE);
					viewHolder.rlVanMissCellDivider.setVisibility(View.VISIBLE);
				}
				
				viewHolder.tvHeaderText.setText(trxDetailsDO.itemCode);
				viewHolder.tvHeaderText.setTypeface(AppConstants.Roboto_Condensed_Bold);
				viewHolder.tvDescription.setText(trxDetailsDO.itemDescription);
				viewHolder.tvDescription2.setTypeface(AppConstants.Roboto_Condensed_Bold);
				viewHolder.tvUOM.setTag(trxDetailsDO.UOM);
				viewHolder.tvUOM.setTag(R.string.request_Bu,""+((int)trxDetailsDO.requestedSalesBU));//casting to int to remove decimal
				viewHolder.tvUOM.setTag(R.string.missed_Bu,viewHolder.tvMissedQty);
				viewHolder.tvUOM.setTag(R.string.key_gsv,viewHolder.tvTotalPrice);
				viewHolder.tvUOM.setTag(R.string.key_gsv_label,viewHolder.tvTotalPriceLabel);
				viewHolder.tvUOM.setTag(R.string.key_gsv_ll,viewHolder.llGRVLabel);
				viewHolder.tvUOM.setTag(R.string.key_gsv_iv_div,viewHolder.ivGRVLabelSep);
				viewHolder.tvDescription2.setText(trxDetailsDO.brandName);
				
				viewHolder.tvUnitPriceLabel.setText("Price ("+"AED"+")");
				viewHolder.tvTotalPriceLabel.setText("GSV:");
				
				if(trxDetailsDO.CSPrice >0)
				{
					viewHolder.tvUnitPrice.setText("UNIT: "+((BaseActivity)getActivity()).decimalFormat.format(trxDetailsDO.CSPrice));
				}
				else
				{
//					viewHolder.tvUnitPrice.setText(((BaseActivity)getActivity()).decimalFormat.format(trxDetailsDO.basePrice));
					viewHolder.tvUnitPrice.setText("PCS: "+((BaseActivity)getActivity()).decimalFormat.format(trxDetailsDO.basePrice)+" | "+"UNIT: "+((BaseActivity)getActivity()).decimalFormat.format(trxDetailsDO.basePrice));
				}

				((SalesManRecommendedOrder)getActivity()).getModifiedData(trxDetailsDO);
				
				if(trxDetailsDO.quantityBU>0)
				{
					viewHolder.llGRVLabel.setVisibility(View.VISIBLE);
					viewHolder.ivGRVLabelSep.setVisibility(View.VISIBLE);
//					String gsv="PCS "+trxDetailsDO.quantityBU+" X "+((BaseActivity)getActivity()).decimalFormat.format(trxDetailsDO.EAPrice)+" = "+(((BaseActivity)getActivity()).decimalFormat.format(trxDetailsDO.quantityBU*trxDetailsDO.EAPrice));
					
					String gsv = "";
					if(TRXTYPE_ORDER == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() && !((BaseActivity)getActivity()).preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(((BaseActivity)getActivity()).preference.PRESELLER))
						gsv="UNIT "+trxDetailsDO.quantityLevel1+" X "+((BaseActivity)getActivity()).decimalFormat.format(trxDetailsDO.CSPrice)+" = "+(((BaseActivity)getActivity()).decimalFormat.format(trxDetailsDO.quantityLevel1*trxDetailsDO.CSPrice));
					else
						gsv="UNIT "+trxDetailsDO.requestedSalesBU+" X "+((BaseActivity)getActivity()).decimalFormat.format(trxDetailsDO.CSPrice)+" = "+(((BaseActivity)getActivity()).decimalFormat.format(trxDetailsDO.requestedSalesBU*trxDetailsDO.CSPrice));
					viewHolder.tvTotalPrice.setText(gsv);
				}
				else
				{
					viewHolder.llGRVLabel.setVisibility(View.INVISIBLE);
					viewHolder.ivGRVLabelSep.setVisibility(View.INVISIBLE);
				}
				
				if(hmInventory.containsKey(trxDetailsDO.itemCode))
				{
					HHInventryQTDO inventryDO = hmInventory.get(trxDetailsDO.itemCode);
					viewHolder.tvVanQty.setText((int)(inventryDO.totalQt/trxDetailsDO.hashArrUoms.get(trxDetailsDO.itemCode+"UNIT").eaConversion) + "");
//					viewHolder.tvVanQty.setText((int)(inventryDO.totalQt) + "");
				}
				else
					viewHolder.tvVanQty.setText(0+ "");
				
				viewHolder.tvUOM.setText(trxDetailsDO.UOM);
				viewHolder.evUnits.setTag(R.string.key_product, trxDetailsDO);
					
//				String imageURL = "brandimages/"+trxDetailsDO.itemGroupLevel5+".png";
				String imageURL = getURL(trxDetailsDO);
				final Uri uri = Uri.parse(imageURL);
				if (uri != null) {
					Bitmap bitmap = getHttpImageManager().loadImage(
							new HttpImageManager.LoadRequest(uri, viewHolder.ivItem,imageURL));
					if (bitmap != null) {
						viewHolder.ivItem.setImageBitmap(bitmap);
					}
				}	
				
				if (trxDetailsDO.requestedSalesBU <= 0)
					viewHolder.evUnits.setText("");
				else
					viewHolder.evUnits.setText(""+(int)trxDetailsDO.requestedSalesBU);

				viewHolder.tvMissedQty.setText(trxDetailsDO.missedBU+"");
				viewHolder.evUnits.setTag(R.string.key_missed_qty,viewHolder.tvMissedQty);
				viewHolder.evUnits.setTag(R.string.key_price,viewHolder.tvUnitPrice);
				viewHolder.evUnits.setTag(R.string.key_gsv,viewHolder.tvTotalPrice);
				viewHolder.evUnits.setTag(R.string.key_gsv_label,viewHolder.tvTotalPriceLabel);
				viewHolder.evUnits.setTag(R.string.key_uom,viewHolder.tvUOM);
				viewHolder.evUnits.setTag(R.string.key_gsv_ll,viewHolder.llGRVLabel);
				viewHolder.evUnits.setTag(R.string.key_gsv_iv_div,viewHolder.ivGRVLabelSep);
				
				viewHolder.evUnits.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {

						if (hasFocus) {
							
							((BaseActivity)getActivity()).viewForEditText  = v;
							viewHolder.evUnits.addTextChangedListener(new TextChangeListener());
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									try {
										((BaseActivity) getActivity()).hideKeyBoard(viewHolder.evUnits);
										boolean isCentre = false;
										if(TRXTYPE_ORDER == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && TRXTYPE_ORDER == TrxHeaderDO.get_TYPE_FREE_DELIVERY())
											isCentre = true;
										((BaseActivity) getActivity()).onKeyboardFocus(viewHolder.evUnits,trxDetailsDO.uomLevelUsed,isCentre);	
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}, 10);
						}
					}
				});

				
				if(trxDetailsDO.productStatus==0)
					convertView.setBackgroundColor(Color.WHITE);
				else if(trxDetailsDO.productStatus==1)
					convertView.setBackgroundColor(Color.WHITE);
				else if(trxDetailsDO.productStatus==2)
				{
					viewHolder.evUnits.setClickable(false);
					viewHolder.evUnits.setEnabled(false);
					convertView.setBackgroundColor(Color.parseColor("#FBE5E9"));
				}
				viewHolder.tvDiscountAplied.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});

				viewHolder.btnDelete.setTag(trxDetailsDO);
				viewHolder.btnDelete.setVisibility(View.GONE);


				convertView.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) 
					{
//						if(trxDetailsDO.productStatus  == 0)
//						{
//							trxDetailsDO.productStatus  = 1;
//							viewHolder.btnDelete.setVisibility(View.VISIBLE);
//							Animation animation  = AnimationUtils.loadAnimation(getActivity(), R.anim.right_to_left);
//							viewHolder.btnDelete.startAnimation(animation);
//						}
						return false;
					}
				});
				viewHolder.tvUOM.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						showItemUOM(v, trxDetailsDO);
					}
				});
				viewHolder.btnDelete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
//						trxDetailsDO.productStatus  = 2;
//						trxDetailsDO.quantityLevel3 = 0;
//						trxDetailsDO.quantityBU = 0;
//						removeProduct(groupPosition, childPosition);
//						((SalesManRecommendedOrder)getActivity()).updateDistinctItem(trxDetailsDO);
//						viewHolder.btnDelete.setVisibility(View.GONE);
					}
				});	
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			return convertView;
		}
		
		private String getURL(TrxDetailsDO trxDetailsDO)
		{
			String URL = "";
			
			if(trxDetailsDO.brandImage != null && trxDetailsDO.brandImage.length() > 0 && trxDetailsDO.brandImage.contains("../"))
			{
				try {
					URL = trxDetailsDO.brandImage.replace("..", ServiceURLs.IMAGE_GLOBAL_URL+"");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else 
				URL = "brandimages/"+trxDetailsDO.itemGroupLevel5+".png";
			
			return URL;
		}

		private void removeProduct(int groupPosition, int childPosition) {
			adapterForCapture.notifyDataSetChanged();
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
	}
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
	    super.setUserVisibleHint(isVisibleToUser);
	    if (isVisibleToUser && adapterForCapture != null) {
	    	adapterForCapture.notifyDataSetChanged();
	    }
	}
	
	private class ViewHolder
	{
		LinearLayout llGRVLabel, llClickToDownLoad;
		ImageView ivGRVLabelSep;
		TextView tvHeaderText, tvDescription, tvVanQty, tvMissedQty, tvDescription2, tvDiscountAplied,tvUOM,tvUnitPriceLabel,tvUnitPrice,tvTotalPriceLabel,tvTotalPrice;
		Button btnDelete;
		CustomEditText evUnits;
		ImageView ivItem, rlVanMissCellDivider,ivDivider,evUnitsCellDivider;
		RelativeLayout rlCell;
	}
/*	 private void showAllBrands(final View v, Vector<String> vecAllBrands) 
	 {
		 CustomBuilder customBuilder = new CustomBuilder(getActivity(), "Select Brands", false);
		 customBuilder.setSingleChoiceItems(vecAllBrands, v.getTag(), new CustomBuilder.OnClickListener()
		 {
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject) 
			{
				v.setTag((String)selectedObject);
				((TextView)v).setText((String)selectedObject);
				getSearchItems((String)selectedObject, true);
				edtSearch.setText("");
				builder.dismiss();
			}
		});
		 customBuilder.show();
     }*/
	 
	 private void showItemUOM(final View v, final TrxDetailsDO trxDetailsDO) 
	 {
		 if(trxDetailsDO.arrUoms.size()>0 ){
			 CustomBuilder customBuilder = new CustomBuilder(getActivity(), "UOM", false);
			 customBuilder.setSingleChoiceItems(trxDetailsDO.arrUoms, trxDetailsDO.UOM, new CustomBuilder.OnClickListener()
			 {
				@Override
				public void onClick(CustomBuilder builder, Object selectedObject) 
				{
					v.setTag((String)selectedObject);
					((TextView)v).setText((String)selectedObject);
					trxDetailsDO.UOM = (String)selectedObject;
					if(adapterForCapture!=null){
						((BaseActivity)getActivity()).viewForEditText = null;
						adapterForCapture.handleText((String)v.getTag(R.string.request_Bu),trxDetailsDO);
					}
						
					try {
						TextView tvMissedQty = (TextView) v.getTag(R.string.missed_Bu);
						tvMissedQty.setText(""+trxDetailsDO.missedBU);
						if(trxDetailsDO.quantityBU>0){
							LinearLayout llGSVValue = (LinearLayout) v.getTag(R.string.key_gsv_ll);
							ImageView ivSeprator = (ImageView) v.getTag(R.string.key_gsv_iv_div);
							llGSVValue.setVisibility(View.VISIBLE);
							ivSeprator.setVisibility(View.VISIBLE);
							TextView tvTotalValue = (TextView) v.getTag(R.string.key_gsv);
//							String gsv="PCS "+trxDetailsDO.quantityBU+" X "+((BaseActivity)getActivity()).decimalFormat.format(trxDetailsDO.EAPrice)+" = "+(((BaseActivity)getActivity()).decimalFormat.format(trxDetailsDO.quantityBU*trxDetailsDO.EAPrice));
							String gsv="UNIT "+trxDetailsDO.requestedSalesBU+" X "+((BaseActivity)getActivity()).decimalFormat.format(trxDetailsDO.CSPrice)+" = "+(((BaseActivity)getActivity()).decimalFormat.format(trxDetailsDO.requestedSalesBU*trxDetailsDO.CSPrice));
							tvTotalValue.setText(gsv);
						}else{
							LinearLayout llGSVValue = (LinearLayout) v.getTag(R.string.key_gsv_ll);
							ImageView ivSeprator = (ImageView) v.getTag(R.string.key_gsv_iv_div);
							llGSVValue.setVisibility(View.GONE);
							ivSeprator.setVisibility(View.GONE);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					builder.dismiss();
				}
			});
			 customBuilder.show();
		 }
     }
	 public HttpImageManager getHttpImageManager() {
			return ((MyApplicationNew) ((Activity) getActivity())
					.getApplication()).getHttpImageManager();
		}
}