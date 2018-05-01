/**
 * 
 */
package com.winit.alseer.salesman.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataobject.BrandDO;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.listeners.StoreCheck;
import com.winit.kwalitysfa.salesman.R;

/**
 * @author Aritra.Pal
 *
 */
public class CaptureInventoryExpandableListViewAdapter extends BaseExpandableListAdapter
{
	
	private Vector<String> vecCategoryIds;
	private HashMap<String, ArrayList<ProductDO>> hashProductWithBrand;
	private Context context;
	private StoreCheck storeCheck;
	
	/**
	 * @param context 
	 * @param storeCheck 
	 * @param hashProductWithBrand2
	 */
	public CaptureInventoryExpandableListViewAdapter(Context context, HashMap<String, ArrayList<ProductDO>> hashProductWithBrand, StoreCheck storeCheck) 
	{
		this.context = context;
		this.hashProductWithBrand = hashProductWithBrand;
		this.storeCheck = storeCheck;
		initializeBrands();
	}
	
	private void initializeBrands(){
		vecCategoryIds = new Vector<String>();
		if(hashProductWithBrand!=null && hashProductWithBrand.size()>0){
			Set<String> keyset = hashProductWithBrand.keySet();
			Iterator<String> iterator = keyset.iterator();
			while(iterator.hasNext())
				vecCategoryIds.add(iterator.next());
		}
		sortVectorperDisplayOredr(vecCategoryIds);
	}
	
	private void sortVectorperDisplayOredr(Vector<String> vecProducts) 
	{
		Collections.sort(vecProducts, new Comparator<String>()
		{
			@Override
			public int compare(String lhs, String rhs) 
			{
				return lhs.compareToIgnoreCase(rhs);
			}
		});
	}

	@Override
	public int getGroupCount() {
		Log.d("GroupViewSize", ""+vecCategoryIds.size());
		return vecCategoryIds.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
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
	public View getGroupView(final int groupPosition, boolean isExpanded,View convertView, ViewGroup parent)
	{
		convertView  		= LayoutInflater.from(context).inflate(R.layout.list_brand_group_item, null);
		TextView tvBrandName = (TextView)convertView.findViewById(R.id.tvBrandName);
		tvBrandName.setText(vecCategoryIds.get(groupPosition));
		//tvBrandName.setBackgroundColor(Color.WHITE);
		tvBrandName.setTextColor(Color.parseColor("#00A0D8"));
		tvBrandName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		final ExpandableListView mExpandableListView = (ExpandableListView) parent;
		mExpandableListView.expandGroup(groupPosition);
		return convertView;
	}

	boolean isBrandSearched = false;
	
	@Override
	public View getChildView(final int groupPosition, int childPosition,boolean isLastChild, View convertView, ViewGroup parent) 
	{
//		int gridChildHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, context.getResources().getDimension(R.dimen.size120), context.getResources().getDisplayMetrics());
		ArrayList<ProductDO> vectorProductDOs = hashProductWithBrand.get(vecCategoryIds.get(groupPosition));
		convertView					= (LinearLayout)LayoutInflater.from(context).inflate(R.layout.store_check_list_cell_new,null);
		GridView lvStoreCheck 		= (GridView) convertView.findViewById(R.id.lvStoreCheck);
		if(vectorProductDOs !=null)
		{
			CaptureInventoryGridAdapter adapter = new CaptureInventoryGridAdapter(context,vectorProductDOs,storeCheck);
			lvStoreCheck.setAdapter(adapter);
		}

//		int height = (int) Math.ceil(vectorProductDOs.size() / 2.0f);
//		convertView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, height*gridChildHeight));

//		float height = 110f;
		float height = 180f;
		int gridChildHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, /*84.882f*/height, context.getResources().getDisplayMetrics());

		int totalRowCount = (int) Math.ceil(vectorProductDOs.size() / 2f);
		int totalHeight = totalRowCount*gridChildHeight;
		if(totalRowCount > 1)
			totalHeight = totalHeight - totalRowCount*(10+totalRowCount*3);

		convertView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, totalHeight));
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	/**
	 * @param hashProductWithClassification
	 */
	public void refresh(HashMap<BrandDO, ArrayList<ProductDO>> hashProductWithClassification) 
	{
		
//		this.hashProductWithBrand = hashProductWithClassification;
		vecCategoryIds.clear();
		notifyDataSetChanged();
		notifyDataSetChanged();
		initializeBrands();
		notifyDataSetChanged();
//		notifyDataSetInvalidated();
	}

	public void refreshExpandableAdapter(HashMap<String, ArrayList<ProductDO>> tmpSearched) 
	{
		this.hashProductWithBrand = tmpSearched;
		vecCategoryIds.clear();
		notifyDataSetChanged();
		initializeBrands();
		notifyDataSetChanged();		
	}
	
}
