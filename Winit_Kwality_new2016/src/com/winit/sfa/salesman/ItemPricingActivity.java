package com.winit.sfa.salesman;

import java.util.Collection;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.adapter.ItemPricingAdapter;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.alseer.salesman.dataaccesslayer.ProductsDA;
import com.winit.alseer.salesman.dataobject.CategoryDO;
import com.winit.alseer.salesman.dataobject.ProductsDO;
import com.winit.kwalitysfa.salesman.R;

@SuppressLint("DefaultLocale")
public class ItemPricingActivity extends BaseActivity{

	// add_new_sku_popup.xml
	private LinearLayout llItemPrice;
	private TextView tvItemPriceHeader,tvCatagories,tvNoItemFound;
	private Button btnitempricefinish;
	private EditText etSearch;
	private Vector<CategoryDO> vecAllCategories=new Vector<CategoryDO>();
	private ListView lvItemsprice;
	private ItemPricingAdapter itemPricingAdapter;
	private Vector<ProductsDO> vecProductsDo = new Vector<ProductsDO>();
	private Vector<ProductsDO> vecProductsDoTemp = new Vector<ProductsDO>();
	public String selectedCategory = "";
//	private RelativeLayout rl_catagories;
	
	private ImageView ivSearchCross;
	
	@Override
	public void initialize() 
	{
		llItemPrice = (LinearLayout) inflater.inflate(R.layout.itemprice, null);
		llBody.addView(llItemPrice, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);

		intialiseControls();
		loadData();
		lvItemsprice.setCacheColorHint(0);
		tvItemPriceHeader.setText("Item Details");
		tvNoItemFound.setText("No Items Available");
		lvItemsprice.setVerticalScrollBarEnabled(false);
		lvItemsprice.setDivider(getResources().getDrawable(R.drawable.saparetor));
		lvItemsprice.setCacheColorHint(0);
		lvItemsprice.setFadingEdgeLength(0);
		
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				getSearchItems("");
			}
		});
		etSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!TextUtils.isEmpty(s))
					ivSearchCross.setVisibility(View.VISIBLE);
				else
					ivSearchCross.setVisibility(View.GONE);
				getSearchItems(s.toString().toLowerCase());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		btnitempricefinish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{

				finish();
			}
		});
//		rl_catagories.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) 
//			{
//				tvCatagories.performClick();
//			}
//		});
		tvCatagories.setTag(-1);
		tvCatagories.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view) 
			{
//				showAllBrands(tvAgencies,getBrands());
				Vector<CategoryDO> filteredCategory=getCategoryByType();
				if(filteredCategory!=null){
					filteredCategory.get(0).categoryName="All Categories";
					showCategories(view, filteredCategory, "Select Category",1);
				}
				
			}
		});
		setTypeFaceRobotoNormal(llItemPrice);
		btnitempricefinish.setTypeface(AppConstants.Roboto_Condensed);
	}
	
	private void loadData() 
	{
		showLoader("Loading Please Wait...");
		new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				vecProductsDo = new ProductsDA().getItemsPriceDetails();
				vecAllCategories = new CaptureInventryDA().getAllCategories();

				runOnUiThread(new  Runnable()
				{
					public void run() 
					{
						hideLoader();
						if(vecProductsDo!=null && vecProductsDo.size()>0)
						{
							lvItemsprice.setAdapter(itemPricingAdapter=new ItemPricingAdapter(vecProductsDo,ItemPricingActivity.this));
							tvNoItemFound.setVisibility(View.GONE);
							lvItemsprice.setVisibility(View.VISIBLE);
						}	
						else
						{
							tvNoItemFound.setVisibility(View.VISIBLE);
							lvItemsprice.setVisibility(View.GONE);
						}
					}
				});
			}
		}).start();		
	}

	/** initializing all the Controls  of ItemPricing class **/
	
	private void intialiseControls() 
	{

		tvItemPriceHeader 		= (TextView) llItemPrice.findViewById(R.id.tvItemPriceHeader);
		tvNoItemFound 			= (TextView) llItemPrice.findViewById(R.id.tvNoItemFound);
		tvCatagories 			= (TextView) llItemPrice.findViewById(R.id.tvCatagories);
		btnitempricefinish 		= (Button) llItemPrice.findViewById(R.id.btnitempricefinish);
		etSearch 		= (EditText) llItemPrice.findViewById(R.id.etSearch);
		ivSearchCross			=	(ImageView)llItemPrice.findViewById(R.id.ivSearchCross);
		lvItemsprice 			= (ListView) llItemPrice.findViewById(R.id.lvItemsprice);
//		rl_catagories			= (RelativeLayout) llItemPrice.findViewById(R.id.rl_catagories);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	public Vector<CategoryDO> getCategoryByType()
	{
		Predicate<CategoryDO> searchItem = new Predicate<CategoryDO>() {
			public boolean apply(CategoryDO categoryDO) {
				return TextUtils.isEmpty(categoryDO.parentCode) ||categoryDO.parentCode.equalsIgnoreCase("ALL");
			}
		};
		Collection<CategoryDO> filteredResult = filter(vecAllCategories, searchItem);
		return new Vector<CategoryDO>(filteredResult);
	}
	
	private void showCategories(final View v, Vector<CategoryDO> vacCategoryDOs,String title,final int type) 
	{
		 CustomBuilder customBuilder = new CustomBuilder(ItemPricingActivity.this, title, false);
		 customBuilder.setSingleChoiceItems(vacCategoryDOs, v.getTag(), new CustomBuilder.OnClickListener()
		 {
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject) 
			{
				v.setTag((CategoryDO)selectedObject);
				((TextView)v).setText(((CategoryDO)selectedObject).categoryName);
				
//				etitempriceSearch.setText("");
				builder.dismiss();
				switch (type) {
				case 1:
					selectedCategory=((CategoryDO)selectedObject).categoryId;
					if(selectedCategory.equalsIgnoreCase("ALL"))
						selectedCategory=null;
					getSearchItems("");
					
					break;
				default:
					break;
				}
				
			}
		});
		 customBuilder.show();
    }
	
	@SuppressLint("DefaultLocale")
	public void getSearchItems(String text) 
	{
		String str="";
		vecProductsDoTemp.clear();
		if(selectedCategory!=null)
			str = selectedCategory.toString().toLowerCase();
		if(text == null || text.equalsIgnoreCase(""))
		{
			for (int i = 0; vecProductsDo!=null && i < vecProductsDo.size(); i++) {
				ProductsDO productsDO = vecProductsDo.get(i);
				if(productsDO.Category.toLowerCase().contains(str.trim()) || productsDO.Category.toLowerCase().contains(str.trim()))
				{
					if(!vecProductsDoTemp.contains(productsDO))
						vecProductsDoTemp.add(productsDO);
				}
			}
		}
		else
		{
			if(str == null || str.equalsIgnoreCase(""))
			{
				for (int i = 0; vecProductsDo!=null && i < vecProductsDo.size(); i++) {
					ProductsDO productsDO = vecProductsDo.get(i);
					if(productsDO.ItemCode.toLowerCase().contains(text.trim()) || productsDO.Description.toLowerCase().contains(text.trim()))
					{
						if(!vecProductsDoTemp.contains(productsDO))
							vecProductsDoTemp.add(productsDO);
					}
				}
			}
			else
			{
				for (int i = 0; vecProductsDo!=null && i < vecProductsDo.size(); i++) {
					ProductsDO productsDO = vecProductsDo.get(i);
					if(productsDO.Category.toLowerCase().contains(str.trim()) 
							&& (productsDO.ItemCode.toLowerCase().contains(text.trim()) || productsDO.Description.toLowerCase().contains(text.trim())))
					{
						if(!vecProductsDoTemp.contains(productsDO))
							vecProductsDoTemp.add(productsDO);
					}
				}
			}
		}
		if(vecProductsDoTemp.size() > 0)
		{
			itemPricingAdapter.refresh(vecProductsDoTemp);
			tvNoItemFound.setVisibility(View.GONE);
			lvItemsprice.setVisibility(View.VISIBLE);
		}
		else
		{
			tvNoItemFound.setVisibility(View.VISIBLE);
			lvItemsprice.setVisibility(View.GONE);
//			itemPricingAdapter.refresh(vecProductsDo);
		}
	}
		
}
