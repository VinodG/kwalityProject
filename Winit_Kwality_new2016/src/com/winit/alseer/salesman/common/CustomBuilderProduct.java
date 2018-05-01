package com.winit.alseer.salesman.common;

import java.util.Vector;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.Toast;

import com.winit.alseer.salesman.dataobject.AgencyNewDo;
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
public class CustomBuilderProduct 
{
	private Context context;
	private CustomDialog customDialog;
	private ListView listView;
	private TextView tvNoSearchFound;
	private FilterListAdapter filterListAdapter;
	
	private Vector<View> vecVisibleCountryCells;
	private Vector<Object> vecData;
	private Object selObj;
	private OnClickListener listener;
	private String title;
	private boolean isCancellable;
	private boolean isHavingGridView=false;
	private boolean isSearchInvisible = false, isWareHouse = false;;
	private String replace ="../";
	String printfromActivity;
	
	private final String SELECT = "Select";
	
	/**
	 *  Constructor 
	 * @param context
	 * @param title
	 * @param isCancellable
	 */
	public CustomBuilderProduct(Context context, String title, boolean isCancellable)
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
	 */
	public CustomBuilderProduct(Context context, String title, boolean isCancellable, String printfromActivity)
	{
		this.context = context;
		this.title = title;
		this.isCancellable = isCancellable;
		this.printfromActivity=printfromActivity;
	}
	/**
	 *  Constructor 
	 * @param context
	 * @param title
	 * @param isCancellable
	 * @param isHavingGridView
	 */
	public CustomBuilderProduct(Context context, String title, boolean isCancellable,boolean isHavingGridView)
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
		View mView 		= LayoutInflater.from(context).inflate(R.layout.custom_builder_abhi, null);

		if(isHavingGridView)
			mView.setPadding(30, 30, 30, 30);
		
		customDialog 	= new CustomDialog(context, mView, new Preference(context).getIntFromPreference(AppConstants.Device_Display_Width,AppConstants.DEVICE_DISPLAY_WIDTH_DEFAULT),  new Preference(context).getIntFromPreference(AppConstants.Device_Display_Height,AppConstants.DEVICE_DISPLAY_HEIGHT_DEFAULT), isCancellable);
		
		//Finding the ID's
		LinearLayout llView			= (LinearLayout) mView.findViewById(R.id.llView);
		TextView tvTitleBuider 		= (TextView) mView.findViewById(R.id.tvTitleBuider);
		final EditText etSearch = (EditText) mView.findViewById(R.id.etSearch);
	//	ImageView ivPopupClose 		= (ImageView) mView.findViewById(R.id.ivPopupClose);
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
		//listView.setDivider(context.getResources().getDrawable(R.drawable.saparetor));
		listView.setFadingEdgeLength(0);
		listView.setCacheColorHint(0);
		listView.setVerticalScrollBarEnabled(false);
		listView.setSmoothScrollbarEnabled(true);
		
		
		filterListAdapter = new FilterListAdapter(vecData);
		listView.setSelector(color.transparent);
		//Setting the Adapter
		listView.setAdapter(filterListAdapter);
		
		
	
	/*	
		ivpopup.setOnTouchListener(new OnTouchListener() 
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				dismiss();
				v.setClickable(true);
				v.setEnabled(true);
				return true;
			}
		});*/
		
		//Functionality for listView Item Click
		listView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				/*for(int i = 0; i < vecVisibleCountryCells.size(); i++)
				{
					View visibleCountryCell = vecVisibleCountryCells.get(i);
					((ImageView)visibleCountryCell.findViewById(R.id.ivSelected)).setBackgroundResource(R.drawable.rbtn_h);
				}
			    
				((ImageView)view.findViewById(R.id.ivSelected)).setBackgroundResource(R.drawable.rbtn);*/
				
				listener.onClick(CustomBuilderProduct.this, view.getTag());
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
					
					listView.setVisibility(View.VISIBLE);
					filterListAdapter.refresh(vecData);
				}
				else
				{
					
					listView.setVisibility(View.GONE);
				
				}
				return true;
			}
		});
		//Functionality for etSelectItem
	/*	etSearch.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{}
			*/
	/*		@Override
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

							filterListAdapter.refresh(vecTemp);
						}
						else
						{
							
							listView.setVisibility(View.GONE);
							filterGridtAdapter.refresh(vecTemp);
						}
					}
					else
					{
						tvNoSearchFound.setVisibility(View.VISIBLE);
						listView.setVisibility(View.GONE);
						
					}
				}
				else
				{
					tvNoSearchFound.setVisibility(View.GONE);
					if(!isHavingGridView)
					{
						
						listView.setVisibility(View.VISIBLE);
						filterListAdapter.refresh(vecData);
					}
					else
					{
						
						listView.setVisibility(View.GONE);
						filterGridtAdapter.refresh(vecData);
					}
				}
			}
		});*/
		
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
				convertView = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.custom_builder_cell_abhi, null);
				vecVisibleCountryCells.add(convertView);
			}
			
			//Finding the Id's
			ImageView ivImageDialog = (ImageView)convertView.findViewById(R.id.ivImageDialog);
			TextView tvSelectUrCountry  = (TextView)convertView.findViewById(R.id.tvSelectName);
			//TextView tvCount	 		= (TextView)convertView.findViewById(R.id.tvCount);
		//	ImageView ivSelected = (ImageView)convertView.findViewById(R.id.ivSelected);
			
			String name = "", count = ""; boolean isShowAsSelected = false;
			//in Case of OrderListDO
			
			if(obj instanceof AgencyNewDo)
			{
				AgencyNewDo agenncyDo = ((AgencyNewDo)obj);
				name = ((AgencyNewDo)obj).AgencyName;
			//	tvCount.setVisibility(View.GONE);
		//		ivSelected.setVisibility(View.VISIBLE);
				if(selObj instanceof AgencyNewDo && ((AgencyNewDo)selObj).AgencyName ==  agenncyDo.AgencyName)
					isShowAsSelected = true;
			}
			
			tvSelectUrCountry.setTypeface(AppConstants.Roboto_Condensed_Bold);
	//		tvCount.setTypeface(AppConstants.Roboto_Condensed_Bold);
			
			tvSelectUrCountry.setText(name);
			if(position%2!=0)
				tvSelectUrCountry.setBackgroundResource(R.drawable.btn_salable);
			else
				tvSelectUrCountry.setBackgroundResource(R.drawable.btn_nonsalable);
		
			
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
		public void onClick(CustomBuilderProduct builder, Object selectedObject);
	}

	
}
