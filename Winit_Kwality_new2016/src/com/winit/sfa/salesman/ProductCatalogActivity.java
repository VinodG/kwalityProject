package com.winit.sfa.salesman;

import java.util.Vector;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.adapter.ProductCatlogAdapter;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataaccesslayer.BrandsDL;
import com.winit.alseer.salesman.dataaccesslayer.ProductsDA;
import com.winit.alseer.salesman.dataobject.BrandDO;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.viewpager.extensions.PagerSlidingTabStrip;
import com.winit.kwalitysfa.salesman.R;

public class ProductCatalogActivity extends BaseActivity
{
	private LinearLayout llProductCatalog;
	private String[] TITLES = {"Body Care", "Pet Nutrition", "Cosmetics","Paper Products","Snacks"};
//	private PagerSlidingTabStripNew tabs;
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;
	private TextView tvLu, tvOrderAttTitle, tvOrderAttValue, tvCallUsTitle, tvCallUsValue;
	private TextView /*tvItemName,*/tvItemDesc;
	private Vector<BrandDO> vecBrandDos;
//	private int[] imagesbodycare = {R.drawable.dettol1,R.drawable.dettol2,R.drawable.dettol3,R.drawable.dettol4,R.drawable.dettol5,R.drawable.dettol6,R.drawable.dettol7,R.drawable.dettol8,R.drawable.dettol9,R.drawable.dettol10};
//	private String[] stringbodycare = {"Original Soap","Skincare Soap","Cool Soap","Re-Energize Soap","Antiseptic Liquid","Hand Sanitizer","Extreme Bodywash","Aqua Bodywash","Nourishing Bodywash","Soothing Bodywash"};
//	
//	private int[] imagespetnutrition = {R.drawable.purina1,R.drawable.purina2,R.drawable.purina3,R.drawable.purina4,R.drawable.purina5,R.drawable.purina6,R.drawable.purina7,R.drawable.purina8,R.drawable.purina9,R.drawable.purina10,R.drawable.purina11};
//	private String[] stringpetnutrition = {"Purina ONE","Friskies","Purina Pro Plan","Purina Cat Chow","Tidy Cats","Be Happy","Whisker Lickin′s","Deli-Cat","Kit & Kaboodle","Purina Veterinary Diets","Purina Veterinary Diets"};
//	
//	private int[] imagescosmetics = {R.drawable.yardley1,R.drawable.yardley2,R.drawable.yardley3,R.drawable.yardley4,R.drawable.yardley5,R.drawable.yardley6,R.drawable.yardley7,R.drawable.yardley8,R.drawable.yardley9,R.drawable.yardley10};
//	private String[] stringcosmetics = {"English Lavender Moisturising Body Lotion 250ml","Eau de Toilette 125ml","Body Spray 75ml","Talc 200g","Nourishing Hand and Nail Cream 100ml","April Violets","Soap 3x100g","Diamond EDT Free Sample 1ml","Daisy Eau de Toilette 1ml","Polaire Eau de Toilette 1ml"};
//	
//	private int[] imagespaperproducts = {R.drawable.kleenex1,R.drawable.kleenex2,R.drawable.kleenex3,R.drawable.kleenex4,R.drawable.kleenex5,R.drawable.kleenex6,R.drawable.kleenex7,R.drawable.kleenex8,R.drawable.kleenex9,R.drawable.kleenex10};
//	private String[] stringpaperproducts = {"Kleenex Everyday Tissues","Kleenex Cool Touch Tissues","Kleenex Ultra Soft Tissues","Kleenex Lotion Tissues","Kleenex Expressions Tissues","Anti-Viral","Kleenex On-the-Go Tissues","Hand Towels","Dinner Napkins","Splash 'N Go! Moist Wipes"};
//	
//	private int[] imagessnacks = {R.drawable.ritz1,R.drawable.ritz2,R.drawable.ritz3,R.drawable.ritz4,R.drawable.ritz5,R.drawable.ritz6,R.drawable.ritz7,R.drawable.ritz8,R.drawable.ritz9,R.drawable.ritz10};
//	private String[] stringsnacks = {"RITZ Original","Mini RITZ Original","RITZ Original 400g","RITZ Crackerfuls Cheddar","RITZ Munchables Buttery Thins","RITZ Buttery Garlic","RITZ Real Cheddar Cheese","RITZ 30% less fat","RITZ 100% Whole Grain","RITZ Less Sodium"};
	
	@Override
	public void initialize() 
	{
		showLoader("Loading Products...");
		llProductCatalog = (LinearLayout) inflater.inflate(R.layout.product_catlog, null);
		
//		tabs = (PagerSlidingTabStripNew)llProductCatalog.findViewById(R.id.tabs);
		tabs = (PagerSlidingTabStrip)llProductCatalog.findViewById(R.id.tabs);
		tvLu = (TextView)llProductCatalog.findViewById(R.id.tvLu);
		llBody.addView(llProductCatalog, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		tvLu.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		tvOrderAttTitle	= (TextView)llProductCatalog.findViewById(R.id.tvOrderAttTitle);
		tvOrderAttValue	= (TextView)llProductCatalog.findViewById(R.id.tvOrderAttValue);
		tvCallUsTitle	= (TextView)llProductCatalog.findViewById(R.id.tvCallUsTitle);
		tvCallUsValue	= (TextView)llProductCatalog.findViewById(R.id.tvCallUsValue);
		
//		tvItemName = (TextView)llProductCatalog.findViewById(R.id.tvItemName);
		tvItemDesc = (TextView)llProductCatalog.findViewById(R.id.tvItemDesc);
//		tvItemName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		tvOrderAttTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvOrderAttValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCallUsTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCallUsValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		loadData();
		
		pager = (ViewPager) llProductCatalog.findViewById(R.id.pager);
		adapter = new MyPagerAdapter(vecBrandDos);
		pager.setAdapter(adapter);
		pager.setOffscreenPageLimit(vecBrandDos.size()-1);
	
//		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
//				.getDisplayMetrics());
//	
//		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);
		
		pager.setEnabled(false);
		pager.setClickable(false);
		
		hideLoader();
		
		
//		tabs.setOnPageChangeListener(new OnPageChangeListener() {
//			
//			@Override
//			public void onPageSelected(int arg0) 
//			{
//				switch (tabs.getSelectedTab()) {
//				case 0:
//					tvItemDesc.setText(stringbodycare[0]);
//					break;
//				case 1:
//					tvItemDesc.setText(stringpetnutrition[0]);
//					break;
//				case 2:
//					tvItemDesc.setText(stringcosmetics[0]);
//					break;
//				case 3:
//					tvItemDesc.setText(stringpaperproducts[0]);
//					break;
//				case 4:
//					tvItemDesc.setText(stringsnacks[0]);
//					break;
//				}
//
//			}
//			
//			@Override
//			public void onPageScrolled(int arg0, float arg1, int arg2) 
//			{
//				
//			}
//			
//			@Override
//			public void onPageScrollStateChanged(int arg0) 
//			{
//				
//			}
//		});
		
//		pager.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				return true;
//			}
//		});
//		
				
	}
	
	
	private class MyPagerAdapter extends PagerAdapter 
	{
		private Vector<BrandDO> vecBrandDOs;
		
		public MyPagerAdapter(Vector<BrandDO> vecBrandDos)
		{
			this.vecBrandDOs = vecBrandDos;
		}
		@Override
		public CharSequence getPageTitle(int position) 
		{
			if(vecBrandDOs != null && vecBrandDOs.size() > 0)
				return vecBrandDOs.get(position).brandName;
			else
				return "";
		}

		@Override
		public int getCount() 
		{
			if(vecBrandDOs != null && vecBrandDOs.size() > 0)
				return vecBrandDOs.size();
			else
				return 0;
			
		}
		
		@Override
		public Object instantiateItem(View collection, int position)
		{
            LinearLayout llFlightStatusContent = getProductCatlogView(vecBrandDOs.get(position).brandId);
            ((ViewPager) collection).addView(llFlightStatusContent, position);
            return llFlightStatusContent;
		}

		@Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((LinearLayout) view);
        }

		

		@Override
		public boolean isViewFromObject(View view, Object object)
		{
			return view == ((LinearLayout) object);
		}
		
	}
	private LinearLayout getProductCatlogView(String brandId)
	{
		Vector<ProductDO> vecProductDOs = new ProductsDA().getProductsDetailsByBrandId(brandId);
		
		LinearLayout llCatLOgView = (LinearLayout) getLayoutInflater().inflate(R.layout.productcatlog_new, null);
		GridView coverFlow = (GridView) llCatLOgView.findViewById(R.id.gvProducts);
		
		ProductCatlogAdapter coverFlowAdapter = new ProductCatlogAdapter(ProductCatalogActivity.this, vecProductDOs);
		coverFlow.setAdapter(coverFlowAdapter);
		
		
		return llCatLOgView;
	}
	
	
	private void loadData()
	{
		vecBrandDos = new BrandsDL().getAllBrands();
	}
}
