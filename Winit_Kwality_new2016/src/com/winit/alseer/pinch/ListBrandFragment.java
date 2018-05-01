//package com.winit.alseer.pinch;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Set;
//import java.util.Vector;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AbsListView;
//import android.widget.BaseExpandableListAdapter;
//import android.widget.ExpandableListView;
//import android.widget.FrameLayout.LayoutParams;
//import android.widget.GridView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.winit.alseer.salesman.adapter.StoreCheckGridAdapter;
//import com.winit.alseer.salesman.common.AppConstants;
//import com.winit.alseer.salesman.dataobject.BrandDO;
//import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
//import com.winit.alseer.salesman.dataobject.ProductDO;
//import com.winit.alseer.salesman.listeners.StoreCheck;
//import com.winit.sfa.salesman.BaseActivity;
//import com.winit.sfa.salesman.R;
//
//@SuppressLint("ValidFragment")
//public class ListBrandFragment extends Fragment
//{
////	public MainCategoryDO mainCategoryDo;
//	private JourneyPlanDO mallsDetail;
//	private StoreCheck storeCheck;
//	private Context context;
////	public CaptureInventaryItems captureInventaryItems;
//	public CaptureInventaryAdapter captureInventaryAdapter;
//	private ExpandableListView expandableList;
//	private LayoutInflater inflater;
//	private LinearLayout llHeader,llCode;
//	private  HashMap<BrandDO, ArrayList<ProductDO>> hashProductWithBrand;
//	
//	/**
//	 * @param mallsDetails
//	 * @param position
//	 * @param hashProductWithClassification
//	 * @param storeCheck2
//	 */
//	public ListBrandFragment(JourneyPlanDO mallsDetails,HashMap<BrandDO, ArrayList<ProductDO>> hashProductWithBrand,StoreCheck storeCheck) 
//	{
//		this.mallsDetail = mallsDetails;
//		this.hashProductWithBrand = hashProductWithBrand;
//		this.storeCheck = storeCheck;
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//	}
//	
//	/* (non-Javadoc)
//	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
//	 */
//	@Override
//	public void onAttach(Activity activity) 
//	{
//		super.onAttach(activity);
//		Log.e("onAttach", "onAttach");
//	}
//	/* (non-Javadoc)
//	 * @see android.support.v4.app.Fragment#onResume()
//	 */
//	@Override
//	public void onResume() 
//	{
//		super.onResume();
//		if(captureInventaryAdapter!=null)
//		{
//			captureInventaryAdapter.notifyDataSetChanged();
//		}
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
//	{
//		LinearLayout llMain 	 =   (LinearLayout) inflater.inflate(R.layout.brand_recommended_order_list,null);
//		this.inflater 			 =   inflater;
//		context					 =   getActivity();
//		expandableList  		 =   (ExpandableListView) llMain.findViewById(R.id.expandableList);
//		llHeader				 =   (LinearLayout)llMain.findViewById(R.id.llHeader);
//		llCode					 =   (LinearLayout)llMain.findViewById(R.id.llCode);
//		
//		llHeader.setVisibility(View.GONE);
//		llCode.setVisibility(View.GONE);
//		
//		expandableList.setCacheColorHint(0);
//		
//		captureInventaryAdapter = new CaptureInventaryAdapter(hashProductWithBrand);
//		expandableList.setAdapter(captureInventaryAdapter);
//		((BaseActivity)getActivity()).setTypeFaceRobotoNormal(llMain);
//		return llMain;
//	}
//	
//	
//	public class CaptureInventaryAdapter extends BaseExpandableListAdapter
//	{
//		
//		private Vector<BrandDO> vecCategoryIds;
//		private  HashMap<BrandDO, ArrayList<ProductDO>> hashProductWithBrand;
//		
//		/**
//		 * @param hashProductWithBrand2
//		 */
//		public CaptureInventaryAdapter(HashMap<BrandDO, ArrayList<ProductDO>> hashProductWithBrand) {
//			this.hashProductWithBrand = hashProductWithBrand;
//			initializeBrands();
//		}
//		
//		private void initializeBrands(){
//			vecCategoryIds = new Vector<BrandDO>();
//			if(hashProductWithBrand!=null && hashProductWithBrand.size()>0){
//				Set<BrandDO> keyset = hashProductWithBrand.keySet();
//				Iterator<BrandDO> iterator = keyset.iterator();
//				while(iterator.hasNext())
//					vecCategoryIds.add(iterator.next());
//			}
//			sortVectorperDisplayOredr(vecCategoryIds);
//		}
//		
//		private void sortVectorperDisplayOredr(Vector<BrandDO> vecProducts) 
//		{
//			Collections.sort(vecProducts, new Comparator<BrandDO>()
//			{
//				@Override
//				public int compare(BrandDO lhs, BrandDO rhs) 
//				{
//					return lhs.brandName.equalsIgnoreCase(rhs.brandName)?1:0;
//				}
//			});
//		}
//
//		@Override
//		public int getGroupCount() {
//			Log.d("GroupViewSize", ""+vecCategoryIds.size());
//			return vecCategoryIds.size();
//		}
//
//		@Override
//		public int getChildrenCount(int groupPosition) {
//			return 1;
//		}
//
//		@Override
//		public Object getGroup(int groupPosition) {
//			return null;
//		}
//
//		@Override
//		public Object getChild(int groupPosition, int childPosition) {
//			return null;
//		}
//
//		@Override
//		public long getGroupId(int groupPosition) {
//			return 0;
//		}
//
//		@Override
//		public long getChildId(int groupPosition, int childPosition) {
//			return 0;
//		}
//
//		@Override
//		public boolean hasStableIds() {
//			return false;
//		}
//
//		@Override
//		public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent)
//		{
//			convertView  		= inflater.inflate(R.layout.list_brand_group_item, null);
//			TextView tvBrandName = (TextView)convertView.findViewById(R.id.tvBrandName);
//			tvBrandName.setText(vecCategoryIds.get(groupPosition).brandName);
//			tvBrandName.setBackgroundColor(Color.WHITE);
//			tvBrandName.setTextColor(Color.parseColor("#54A663"));
//			tvBrandName.setTypeface(AppConstants.Roboto_Condensed_Bold);
//			
//			expandableList.expandGroup(groupPosition);
//			return convertView;
//		}
//
//		boolean isBrandSearched = false;
//		
//		@Override
//		public View getChildView(final int groupPosition, int childPosition,boolean isLastChild, View convertView, ViewGroup parent) 
//		{
//			int gridChildHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 86, getResources().getDisplayMetrics());
//			ArrayList<ProductDO> vectorProductDOs = hashProductWithBrand.get(vecCategoryIds.get(groupPosition));
//			convertView					= (LinearLayout)inflater.inflate(R.layout.store_check_list_cell_new,null);
//			GridView lvStoreCheck 		= (GridView) convertView.findViewById(R.id.lvStoreCheck);
//			if(vectorProductDOs !=null)
//			{
//				StoreCheckGridAdapter adapter = new StoreCheckGridAdapter(context,vectorProductDOs,storeCheck);
//				lvStoreCheck.setAdapter(adapter);
//			}
//			
//			int height = (int) Math.ceil(vectorProductDOs.size() / 2.0f);
//			convertView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, height*gridChildHeight));
//			return convertView;
//		}
//
//		@Override
//		public boolean isChildSelectable(int groupPosition, int childPosition) {
//			return false;
//		}
//
//		/**
//		 * @param tmpSearched
//		 */
//		public void refresh(HashMap<BrandDO, ArrayList<ProductDO>> tmpSearched) 
//		{
//			this.hashProductWithBrand = tmpSearched;
//			initializeBrands();
//			notifyDataSetInvalidated();
//		}
//	
//	}
//
//	public void refresh(HashMap<BrandDO, ArrayList<ProductDO>> tmpSearched) 
//	{
//		captureInventaryAdapter = new CaptureInventaryAdapter(tmpSearched);
//		expandableList.setAdapter(captureInventaryAdapter);
//	}
//	/* (non-Javadoc)
//	 * @see android.support.v4.app.Fragment#setUserVisibleHint(boolean)
//	 */
//	@Override
//	public void setUserVisibleHint(boolean isVisibleToUser) 
//	{
//		super.setUserVisibleHint(isVisibleToUser);
//		if(isVisibleToUser && captureInventaryAdapter!=null)
//		{
//			captureInventaryAdapter.notifyDataSetChanged();
//		}
//	}
//}