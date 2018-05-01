package com.winit.alseer.salesman.common;

import java.util.Vector;

import android.R.color;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.dataobject.AssetDO;
import com.winit.alseer.salesman.dataobject.BrandDO;
import com.winit.alseer.salesman.dataobject.CategoryDO;
import com.winit.alseer.salesman.dataobject.CompBrandDO;
import com.winit.alseer.salesman.dataobject.CompCategoryDO;
import com.winit.alseer.salesman.dataobject.CompetitorItemDO;
import com.winit.alseer.salesman.dataobject.EmployeeDo;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.OptionsDO;
import com.winit.alseer.salesman.dataobject.UOMConversionFactorDO;
import com.winit.alseer.salesman.dataobject.UserSurveyAnswerDO;
import com.winit.alseer.salesman.dataobject.VehicleDO;
import com.winit.alseer.salesman.dataobject.WareHouseDetailDO;
import com.winit.alseer.salesman.imageloader.UrlImageViewHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;

/**
 * Description of class : To create custom Alert Dialog Box Single Choice Item.
 */
public class CustomBuilder 
{
	private Context context;
	private CustomDialog customDialog;
	private ListView listView;
	private GridView gridView;
	private TextView tvNoSearchFound;
	private FilterListAdapter filterListAdapter;
	private FilterGridtAdapter filterGridtAdapter;
	private Vector<View> vecVisibleCountryCells;
	private Vector<Object> vecData;
	private Object selObj;
	private OnClickListener listener;
	private String title;
	private boolean isCancellable;
	private boolean isHavingGridView=false;
	private boolean isSearchInvisible = false, isWareHouse = false;;
	private String replace ="../";
	
	private final String SELECT = "Select";
	
	/**
	 *  Constructor 
	 * @param context
	 * @param title
	 * @param isCancellable
	 */
	public CustomBuilder(Context context, String title, boolean isCancellable)
	{
		this.context = context;
		this.title = title;
		this.isCancellable = isCancellable;
	}
	/**
	 *  Constructor 
	 * @param context
	 * @param title
	 * @param isCancellable
	 * @param isHavingGridView
	 */
	public CustomBuilder(Context context, String title, boolean isCancellable,boolean isHavingGridView)
	{
		this.context = context;
		this.title = title;
		this.isCancellable = isCancellable;
		this.isHavingGridView=isHavingGridView;
	}
	
	/**
	 * Method to set Single Choice Items 
	 * @param vecData
	 * @param selObj
	 * @param listener
	 */
	@SuppressWarnings("unchecked")
	public void setSingleChoiceItems(Object vecData, Object selObj, OnClickListener listener)
	{
		this.vecData  = (Vector<Object>)vecData;
		this.selObj   = selObj;
		this.listener = listener;
		if(selObj == null)
			this.selObj = new Object();
	}
	
	/**
	 * Method to set Single Choice Items 
	 * @param vecData
	 * @param selObj
	 * @param listener
	 */
	@SuppressWarnings("unchecked")
	public void setSingleChoiceItems(String title,Object vecData, Object selObj, OnClickListener listener)
	{
		this.title = title;
		this.vecData  = (Vector<Object>)vecData;
		this.selObj   = selObj;
		this.listener = listener;
		if(selObj == null)
			this.selObj = new Object();
	}
	
	
	@SuppressWarnings("unchecked")
	public void setSingleChoiceItems(Object vecData, Object selObj, OnClickListener listener, boolean isSearchInvisible)
	{
		this.vecData  = (Vector<Object>)vecData;
		this.selObj   = selObj;
		this.listener = listener;
		this.isSearchInvisible = isSearchInvisible;
		if(selObj == null)
			this.selObj = new Object();
	}
	
	//Methof to show the Single Choice Itemsm Dialog
	public void show()
	{
		if(vecData == null)
			return;
		
		vecVisibleCountryCells = new Vector<View>();
		
		//Inflating the country_popup Layout
		View mView 		= LayoutInflater.from(context).inflate(R.layout.custom_builder, null);

		if(isHavingGridView)
			mView.setPadding(30, 30, 30, 30);
		
		customDialog 	= new CustomDialog(context, mView, new Preference(context).getIntFromPreference(AppConstants.Device_Display_Width,AppConstants.DEVICE_DISPLAY_WIDTH_DEFAULT),  new Preference(context).getIntFromPreference(AppConstants.Device_Display_Height,AppConstants.DEVICE_DISPLAY_HEIGHT_DEFAULT), isCancellable);
		
		//Finding the ID's
		LinearLayout llView			= (LinearLayout) mView.findViewById(R.id.llView);
		TextView tvTitleBuider 		= (TextView) mView.findViewById(R.id.tvTitleBuider);
		final EditText etSearch = (EditText) mView.findViewById(R.id.etSearch);
		ImageView ivPopupClose 		= (ImageView) mView.findViewById(R.id.ivPopupClose);
		final ImageView ivSearchCross 		= (ImageView) mView.findViewById(R.id.ivSearchCross);
		RelativeLayout rlSearch 		= (RelativeLayout) mView.findViewById(R.id.rlSearch);
		ImageView ivDiv 		= (ImageView) mView.findViewById(R.id.ivDiv);
		
		if(isSearchInvisible)
		{
			rlSearch.setVisibility(View.GONE);
			etSearch.setVisibility(View.GONE);
//			ivDiv.setVisibility(View.VISIBLE);
		}
		else
		{
			rlSearch.setVisibility(View.GONE);
			etSearch.setVisibility(View.VISIBLE);
//			ivDiv.setVisibility(View.GONE);
		}
		
		setTypeFace(llView);
		tvTitleBuider.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		if(title.contains(SELECT))
		{
			tvTitleBuider.setText(title);
			etSearch.setHint(title);
		}
		else
		{
			tvTitleBuider.setText("Select "+title);
			etSearch.setHint("Search "+title);
		}
		tvNoSearchFound = (TextView) mView.findViewById(R.id.tvNoSearchFound);
		tvNoSearchFound.setTypeface(AppConstants.Roboto_Condensed_Bold);
		//ListView
		listView = (ListView) mView.findViewById(R.id.lvSelectCountry);
		listView.setDivider(context.getResources().getDrawable(R.drawable.saparetor));
		listView.setFadingEdgeLength(0);
		listView.setCacheColorHint(0);
		listView.setVerticalScrollBarEnabled(false);
		listView.setSmoothScrollbarEnabled(true);
		
		
		filterListAdapter = new FilterListAdapter(vecData);
		listView.setSelector(color.transparent);
		//Setting the Adapter
		listView.setAdapter(filterListAdapter);
		
		
		//GridView
		gridView = (GridView) mView.findViewById(R.id.gvSelectCountry);
		gridView.setSelector(color.transparent);
		if(isHavingGridView)
		{
			gridView.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			filterGridtAdapter = new FilterGridtAdapter(vecData);
			gridView.setAdapter(filterGridtAdapter);
		}
		else
		{
			gridView.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
		
		
		ivPopupClose.setOnTouchListener(new OnTouchListener() 
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				dismiss();
				v.setClickable(true);
				v.setEnabled(true);
				return true;
			}
		});
		
		//Functionality for listView Item Click
		listView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				for(int i = 0; i < vecVisibleCountryCells.size(); i++)
				{
					View visibleCountryCell = vecVisibleCountryCells.get(i);
					((ImageView)visibleCountryCell.findViewById(R.id.ivSelected)).setBackgroundResource(R.drawable.rbtn_h);
				}
			    
				((ImageView)view.findViewById(R.id.ivSelected)).setBackgroundResource(R.drawable.rbtn);
				
				listener.onClick(CustomBuilder.this, view.getTag());
			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
//				for(int i = 0; i < vecVisibleCountryCells.size(); i++)
//				{
//					View visibleCountryCell = vecVisibleCountryCells.get(i);
//					((ImageView)visibleCountryCell.findViewById(R.id.ivSelected)).setBackgroundResource(R.drawable.rbtn_h);
//				}
//			    
//				((ImageView)view.findViewById(R.id.ivSelected)).setBackgroundResource(R.drawable.rbtn);
				
				listener.onClick(CustomBuilder.this, view.getTag());
			}
		});
		ivSearchCross.setOnTouchListener(new OnTouchListener() 
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				tvNoSearchFound.setVisibility(View.GONE);
				if(!isHavingGridView)
				{
					gridView.setVisibility(View.GONE);
					listView.setVisibility(View.VISIBLE);
					filterListAdapter.refresh(vecData);
				}
				else
				{
					gridView.setVisibility(View.VISIBLE);
					listView.setVisibility(View.GONE);
					filterGridtAdapter.refresh(vecData);
				}
				return true;
			}
		});
		//Functionality for etSelectItem
		etSearch.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{}
			
			@Override
			public void afterTextChanged(Editable s) 
			{
				if(!s.toString().equalsIgnoreCase(""))
				{
					Vector<Object> vecTemp = new Vector<Object>();
					for(int i = 0; vecData != null && i < vecData.size(); i++)
					{
						Object obj = vecData.get(i);
						String field = "";
						
						//Comparing the Objects
						if(obj instanceof NameIDDo)
							field = ((NameIDDo)obj).strName;
						
						else if(obj instanceof UOMConversionFactorDO)
							field = ((UOMConversionFactorDO)obj).UOM;
						
						else if(obj instanceof CompBrandDO)
							field = ((CompBrandDO)obj).Brand;
						
						else if(obj instanceof CompCategoryDO)
							field = ((CompCategoryDO)obj).Category;
						
						else if(obj instanceof VehicleDO)
							field = ((VehicleDO)obj).VEHICLE_NO;
						
//						else if(obj instanceof JourneyPlanDO)
//							field = ""+((JourneyPlanDO)obj).siteName;
						
						else if(obj instanceof WareHouseDetailDO)
							field = ((WareHouseDetailDO)obj).WareHouseName;
						else if(obj instanceof CategoryDO)
							field = ((CategoryDO)obj).categoryName;

						else if(obj instanceof AssetDO)
							field = ""+((AssetDO)obj).name;
						
						else if(obj instanceof OptionsDO)
							field = ""+((OptionsDO)obj).OptionName;
					
						else if(obj instanceof UserSurveyAnswerDO)
							field = ""+((UserSurveyAnswerDO)obj).CreatedOn;
						
						else if(obj instanceof String)
							field = ((String)obj);
						
						else if(obj instanceof BrandDO)
							field = ((BrandDO)obj).brandName;
						else if(obj instanceof CategoryDO)
							field = ((CategoryDO)obj).categoryName;
						
						else if(obj instanceof CompetitorItemDO)
						{
							if(title.equalsIgnoreCase("Select Company"))
								field = ((CompetitorItemDO)obj).company;
							else
								field = ((CompetitorItemDO)obj).brandname;
						}
							
						if(field.toLowerCase().contains(s.toString().toLowerCase()))
						{
							vecTemp.add(vecData.get(i));
						}
					}
					if(vecTemp.size() > 0)
					{
						tvNoSearchFound.setVisibility(View.GONE);
						if(!isHavingGridView)
						{
							gridView.setVisibility(View.GONE);
							listView.setVisibility(View.VISIBLE);
							filterListAdapter.refresh(vecTemp);
						}
						else
						{
							gridView.setVisibility(View.VISIBLE);
							listView.setVisibility(View.GONE);
							filterGridtAdapter.refresh(vecTemp);
						}
					}
					else
					{
						tvNoSearchFound.setVisibility(View.VISIBLE);
						listView.setVisibility(View.GONE);
						gridView.setVisibility(View.GONE);
					}
				}
				else
				{
					tvNoSearchFound.setVisibility(View.GONE);
					if(!isHavingGridView)
					{
						gridView.setVisibility(View.GONE);
						listView.setVisibility(View.VISIBLE);
						filterListAdapter.refresh(vecData);
					}
					else
					{
						gridView.setVisibility(View.VISIBLE);
						listView.setVisibility(View.GONE);
						filterGridtAdapter.refresh(vecData);
					}
				}
			}
		});
		
		customDialog.show();
	}
	
	public void dismiss()
	{
		customDialog.dismiss();
	}
	public void setTypeFace(ViewGroup group) 
	{
	     int count = group.getChildCount();
	     View v;
	     for(int i = 0; i < count; i++) {
	         v = group.getChildAt(i);
	         if(v instanceof TextView || v instanceof Button || v instanceof EditText/*etc.*/)
	             ((TextView)v).setTypeface(AppConstants.Roboto_Condensed);
	         else if(v instanceof ViewGroup)
	        	 setTypeFace((ViewGroup)v);
	     }
	}
	
	private class FilterListAdapter extends BaseAdapter
	{
		private Vector<Object> vecData;

		public FilterListAdapter(Vector<Object> vecData) 
		{
			this.vecData = vecData;
		}

		@Override
		public int getCount() 
		{
			if(vecData == null)
				return 0;
			else 
				return vecData.size();
		}

		@Override
		public Object getItem(int position) 
		{
			return position;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			Object obj = vecData.get(position);
			
			//Inflating the country_cell Layout
			if(convertView == null)
			{
				convertView = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.custom_builder_cell, null);
				vecVisibleCountryCells.add(convertView);
			}
			
			//Finding the Id's
			ImageView ivImageDialog = (ImageView)convertView.findViewById(R.id.ivImageDialog);
			TextView tvSelectUrCountry  = (TextView)convertView.findViewById(R.id.tvSelectName);
			TextView tvCount	 		= (TextView)convertView.findViewById(R.id.tvCount);
			ImageView ivSelected = (ImageView)convertView.findViewById(R.id.ivSelected);
			
			String name = "", count = ""; boolean isShowAsSelected = false;
			//in Case of OrderListDO
			if(obj instanceof NameIDDo)
			{
				NameIDDo objNameIDDo = ((NameIDDo)obj);
				name  				 = objNameIDDo.strName;
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				if(selObj instanceof NameIDDo && ((NameIDDo)selObj).strName == objNameIDDo.strName)
					isShowAsSelected = true;
			}
			else if(obj instanceof CompBrandDO)
			{
				CompBrandDO compBrandDO = ((CompBrandDO)obj);
				name  				 = compBrandDO.Brand;
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				if(selObj instanceof CompBrandDO && ((CompBrandDO)selObj).Brand == compBrandDO.Brand)
					isShowAsSelected = true;
			}
			else if(obj instanceof CompCategoryDO)
			{
				CompCategoryDO compCategoryDO = ((CompCategoryDO)obj);
				name  				 = compCategoryDO.Category;
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				if(selObj instanceof CompCategoryDO && ((CompCategoryDO)selObj).Category == compCategoryDO.Category)
					isShowAsSelected = true;
			}
			else if(obj instanceof VehicleDO)
			{
				VehicleDO vehicleDO = ((VehicleDO)obj);
				name = ((VehicleDO)obj).VEHICLE_NO;
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				if(selObj instanceof VehicleDO && ((VehicleDO)selObj).VEHICLE_NO ==  vehicleDO.VEHICLE_NO)
					isShowAsSelected = true;
			}
			else if(obj instanceof EmployeeDo)
			{
				EmployeeDo employeeDo = ((EmployeeDo)obj);
				name = ((EmployeeDo)obj).strEmpName;
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				if(selObj instanceof EmployeeDo && ((EmployeeDo)selObj).strEmpName ==  employeeDo.strEmpName)
					isShowAsSelected = true;
			}
			/*else if(obj instanceof JourneyPlanDO)
			{
				JourneyPlanDO customerDo = ((JourneyPlanDO)obj);
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				name = ""+customerDo.site+" - "+customerDo.siteName+" - "+customerDo.city+" L";
				if(selObj instanceof JourneyPlanDO && ((JourneyPlanDO)selObj).site == customerDo.site)
					isShowAsSelected = true;
			}*/
			else if(obj instanceof AssetDO)
			{
				AssetDO assets = ((AssetDO)obj);
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				name = ""+assets.name+" - "+assets.assetType+" - "+assets.capacity;
				if(selObj instanceof AssetDO && ((AssetDO)selObj).assetId == assets.assetId)
					isShowAsSelected = true;
			}
			else if(obj instanceof String)
			{
				name = ((String)obj);
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				if(selObj instanceof String && ((String)selObj).equalsIgnoreCase(name))
					isShowAsSelected = true;
			}
			else if(obj instanceof UOMConversionFactorDO)
			{
				UOMConversionFactorDO uomConversionFactorDO = ((UOMConversionFactorDO)obj);
				name = uomConversionFactorDO.UOM;
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				if(selObj instanceof UOMConversionFactorDO && (((UOMConversionFactorDO)selObj).UOM).equalsIgnoreCase(uomConversionFactorDO.UOM))
					isShowAsSelected = true;
			}
			else if(obj instanceof CategoryDO)
			{
				CategoryDO categoryDO = ((CategoryDO)obj);
				name = categoryDO.categoryName;
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				if(selObj instanceof CategoryDO && (((CategoryDO)selObj).categoryId).equalsIgnoreCase(categoryDO.categoryId))
					isShowAsSelected = true;
				else if(!(selObj instanceof CategoryDO)&& position==0)
					isShowAsSelected = true;
			}
			else if(obj instanceof WareHouseDetailDO)
			{
				WareHouseDetailDO wareHouse = ((WareHouseDetailDO)obj);
				tvCount.setVisibility(View.VISIBLE);
				ivSelected.setVisibility(View.VISIBLE);
				name = ""+wareHouse.WareHouseCode;
				count = ""+wareHouse.WareHouseName;
				isWareHouse = true;
				if(selObj instanceof WareHouseDetailDO && ((WareHouseDetailDO)selObj).WareHouseCode == wareHouse.WareHouseCode)
					isShowAsSelected = true;
			}
			
			else if(obj instanceof UserSurveyAnswerDO)
			{
				UserSurveyAnswerDO userSurveyAnswerDO = ((UserSurveyAnswerDO)obj);
				tvCount.setVisibility(View.GONE);
				tvSelectUrCountry.setVisibility(View.VISIBLE);
				ivSelected.setVisibility(View.VISIBLE);
				
				name=""+userSurveyAnswerDO.CreatedOn.split("T")[0] +"    "+ userSurveyAnswerDO.FirstName;
				
				if(selObj instanceof UserSurveyAnswerDO && ((UserSurveyAnswerDO)selObj).UserSurveyAnswerId == userSurveyAnswerDO.UserSurveyAnswerId)
					isShowAsSelected = true;
			}
			else if(obj instanceof OptionsDO)
			{
				OptionsDO optionsDO = ((OptionsDO)obj);
				tvCount.setVisibility(View.GONE);
				tvSelectUrCountry.setVisibility(View.VISIBLE);
				ivSelected.setVisibility(View.VISIBLE);
				
				name=""+optionsDO.OptionName;
				
				if(selObj instanceof OptionsDO && ((OptionsDO)selObj).SurveyQuestionOptionId == optionsDO.SurveyQuestionOptionId)
					isShowAsSelected = true;
			}
			
			
			else if(obj instanceof BrandDO)
			{
				BrandDO brandDO = (BrandDO) obj;
				name = brandDO.brandName;
				ivImageDialog.setVisibility(View.VISIBLE);
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				
				if(!brandDO.image.equalsIgnoreCase(""))
				{
					if(brandDO.image != null)
					{
						if(brandDO.image.contains(replace))
						{
							brandDO.image = brandDO.image.replace(replace, ServiceURLs.MAIN_GLOBAL_URL.split("Services")[0]);
						}
						
						UrlImageViewHelper.setUrlDrawable(ivImageDialog, brandDO.image, R.drawable.empty_photo,UrlImageViewHelper.CACHE_DURATION_ONE_DAY);
					}
				}
				else
					ivImageDialog.setImageResource(R.drawable.empty_photo);
			}
			else if(obj instanceof CompetitorItemDO)
			{
				CompetitorItemDO competitorItemDO = (CompetitorItemDO) obj;
				
				if(title.equalsIgnoreCase("Select Company"))
					name = competitorItemDO.company;
				else
					name = competitorItemDO.brandname;
				
				ivImageDialog.setVisibility(View.VISIBLE);
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				
				if(!competitorItemDO.imagepath.equalsIgnoreCase(""))
				{
					if(competitorItemDO.imagepath != null)
					{
						if(competitorItemDO.imagepath.contains(replace))
						{
							competitorItemDO.imagepath = competitorItemDO.imagepath.replace(replace, ServiceURLs.MAIN_GLOBAL_URL.split("Services")[0]);
						}
						
						UrlImageViewHelper.setUrlDrawable(ivImageDialog, competitorItemDO.imagepath, R.drawable.empty_photo,UrlImageViewHelper.CACHE_DURATION_ONE_DAY);
					}
				}
				else
					ivImageDialog.setImageResource(R.drawable.empty_photo);
			}
			tvSelectUrCountry.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvCount.setTypeface(AppConstants.Roboto_Condensed_Bold);
			
			tvSelectUrCountry.setText(name);
			if(isWareHouse)
				tvCount.setText(count);
			else
				tvCount.setText("Order ("+count+")");
			
			if(isShowAsSelected)
				ivSelected.setBackgroundResource(R.drawable.rbtn);
			else 
				ivSelected.setBackgroundResource(R.drawable.rbtn_h);
			
			convertView.setTag(obj);
			
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			return convertView;
		}
		//Method to refresh the List View
		private void refresh(Vector<Object> vecData) 
		{
			this.vecData = vecData;
			this.notifyDataSetChanged();
		}
	}
	
	private class FilterGridtAdapter extends BaseAdapter
	{
		private Vector<Object> vecData;

		public FilterGridtAdapter(Vector<Object> vecData) 
		{
			this.vecData = vecData;
		}

		@Override
		public int getCount() 
		{
			if(vecData == null)
				return 0;
			else 
				return vecData.size();
		}

		@Override
		public Object getItem(int position) 
		{
			return position;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			Object obj = vecData.get(position);
			
			//Inflating the country_cell Layout
			if(convertView == null)
			{
				convertView = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.custom_builder_cell_for_grid, null);
				vecVisibleCountryCells.add(convertView);
			}
			
			//Finding the Id's
			ImageView ivImageDialog = (ImageView)convertView.findViewById(R.id.ivImageDialog);
			TextView tvSelectUrCountry  = (TextView)convertView.findViewById(R.id.tvSelectName);
			
			String name = "", count = ""; boolean isShowAsSelected = false;
			//in Case of OrderListDO
			if(obj instanceof BrandDO)
			{
				BrandDO brandDO = (BrandDO) obj;
				name = brandDO.brandName;
				ivImageDialog.setVisibility(View.VISIBLE);
				
				if(!brandDO.image.equalsIgnoreCase(""))
				{
					if(brandDO.image != null)
					{
						brandDO.image = brandDO.image.replaceAll(" ", "%20");
						if(brandDO.image.contains(replace))
						{
							brandDO.image = brandDO.image.replace(replace, ServiceURLs.MAIN_GLOBAL_URL.split("Services")[0]);
						}
						
						UrlImageViewHelper.setUrlDrawable(ivImageDialog, brandDO.image, R.drawable.empty_photo,UrlImageViewHelper.CACHE_DURATION_ONE_DAY);
					}
				}
				else
					ivImageDialog.setImageResource(R.drawable.empty_photo);
			}
			
			tvSelectUrCountry.setTypeface(AppConstants.Roboto_Condensed_Bold);
			
			tvSelectUrCountry.setText(name);
			
			convertView.setTag(obj);
			
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			return convertView;
		}
		//Method to refresh the List View
		private void refresh(Vector<Object> vecData) 
		{
			this.vecData = vecData;
			this.notifyDataSetChanged();
		}
	}
	
	public interface OnClickListener 
	{
		public void onClick(CustomBuilder builder, Object selectedObject);
	}
}
