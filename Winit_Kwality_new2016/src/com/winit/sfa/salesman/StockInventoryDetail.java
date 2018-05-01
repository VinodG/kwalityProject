package com.winit.sfa.salesman;

import java.util.ArrayList;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataobject.OrderDO;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.kwalitysfa.salesman.R;

public class StockInventoryDetail extends BaseActivity
{
	//declaration of variables
	private LinearLayout llStockInventory , llAddItems;
	private TextView tvStockInventory, tvCodeEOP, tvCasesEOP, tvUnitsEOP, tvNoOrderFound;
	private Button btnFinish;
	private EditText etSearch;
	private ListView lvSalesOrder;
	private ArrayList<ProductDO> vecOrderDetail;
	private OrderDO objoOrderDO;
	private OrderDetailAdapter orderDetailAdapter;
	
	private ImageView ivSearchCross;
	
	@Override
	public void initialize() 
	{
		//inflate the summary-of-day layout
		llStockInventory 	= (LinearLayout) inflater.inflate(R.layout.stock_inventory, null);
		//Adding the layout into base middle layout
		llBody.addView(llStockInventory,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		//Initialing all the controls
		intialiseControls();
		setTypeFaceRobotoNormal(llStockInventory);
		
		
		btnFinish.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				vecOrderDetail =  new CommonDA().getStockInventory();
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						//setting adapter to the list view
						orderDetailAdapter = new OrderDetailAdapter(vecOrderDetail);
						lvSalesOrder.setAdapter(orderDetailAdapter);
						llAddItems.addView(lvSalesOrder, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT );
						if(vecOrderDetail!=null && vecOrderDetail.size()>0)
						{
							tvNoOrderFound.setVisibility(View.GONE);
							llAddItems.setVisibility(View.VISIBLE);
						}
						else
						{
							llAddItems.setVisibility(View.GONE);
							tvNoOrderFound.setVisibility(View.VISIBLE);
						}
						hideLoader();
					}
				});
			}
		}).start();
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				orderDetailAdapter.refresh(vecOrderDetail);
			}
		});
		etSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(!TextUtils.isEmpty(s))
					ivSearchCross.setVisibility(View.VISIBLE);
				else
					ivSearchCross.setVisibility(View.GONE);
				if(s.toString()!=null)
				{
					ArrayList <ProductDO> vecTemp = new ArrayList<ProductDO>();
					for(int index = 0; vecOrderDetail != null && index < vecOrderDetail.size(); index++)
					{
						ProductDO obj 		= (ProductDO) vecOrderDetail.get(index);
						String strText 		= ((ProductDO)obj).SKU;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()))
						{
							vecTemp.add(obj);
						}
					}
					if(vecTemp!=null)
						orderDetailAdapter.refresh(vecTemp);
				}
				else
				{
					orderDetailAdapter.refresh(vecOrderDetail);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) 
			{
				
			}
			@Override
			public void afterTextChanged(Editable s) 
			{
				
			}
		});
	}
	public void intialiseControls()
	{
		//getting Id's of TextView
		llAddItems			=	(LinearLayout)llStockInventory.findViewById(R.id.llAddItems);
		tvStockInventory	=	(TextView)llStockInventory.findViewById(R.id.tvStockInventory);
		tvCodeEOP			=	(TextView)llStockInventory.findViewById(R.id.tvCodeEOP);
		tvCasesEOP			=	(TextView)llStockInventory.findViewById(R.id.tvCasesEOP);
		tvUnitsEOP			=	(TextView)llStockInventory.findViewById(R.id.tvUnitsEOP);
		tvNoOrderFound		=	(TextView)llStockInventory.findViewById(R.id.tvNoOrderFound);
		//getting Id's of Button
		btnFinish			=	(Button)llStockInventory.findViewById(R.id.btnFinish);
		etSearch			=	(EditText)llStockInventory.findViewById(R.id.etSearch);
		ivSearchCross			=	(ImageView)llStockInventory.findViewById(R.id.ivSearchCross);
		
		//setting the Type-face
		/*tvStockInventory.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCodeEOP.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCasesEOP.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvUnitsEOP.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvNoOrderFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnFinish.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etSearch.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		//initializing the List View
		lvSalesOrder 	=	new ListView(this);
		//setting properties to ListView
		lvSalesOrder.setCacheColorHint(0);
		lvSalesOrder.setVerticalScrollBarEnabled(false);
		lvSalesOrder.setDivider(getResources().getDrawable(R.drawable.saparetor));
		lvSalesOrder.setFadingEdgeLength(0);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	

	@Override
	public void onBackPressed()
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else
		{
 			finish();
			setResult(2000);
		}
	}
	
	private class OrderDetailAdapter extends BaseAdapter
	{
		ArrayList<ProductDO> vecOrderDetail;
		public OrderDetailAdapter(ArrayList<ProductDO> vecOrderDetail)
		{
			this.vecOrderDetail = vecOrderDetail;
		}

		@Override
		public int getCount() 
		{
			if(vecOrderDetail != null && vecOrderDetail.size() > 0)
				return vecOrderDetail.size();
			return 0;
		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int position) 
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			//getting the current productDO from the Vector
			ProductDO productDO			= 	vecOrderDetail.get(position);
			//Inflating the inventory_cell layout
			if(convertView == null)
				convertView					= 	(LinearLayout)getLayoutInflater().inflate(R.layout.inventory_cell,null);
			//Getting the Id's 
			TextView tvHeaderText		= 	(TextView)convertView.findViewById(R.id.tvHeaderText);
			TextView tvDescription		= 	(TextView)convertView.findViewById(R.id.tvDescription);
			EditText evCases			= 	(EditText)convertView.findViewById(R.id.evCases);
			EditText evUnits			= 	(EditText)convertView.findViewById(R.id.evUnits);
	    	
			//setting texts and Type-faces
			/*evCases.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			evUnits.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvHeaderText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvDescription.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
			*/
			tvHeaderText.setText(productDO.SKU);
			tvDescription.setText(productDO.Description);
			
			evCases.setText(productDO.preCases);
			evUnits.setText(productDO.preUnits);
			
			//Disable the edit texts
			evCases.setEnabled(false);
			evUnits.setEnabled(false);
			//Setting focusable false for both Edit texts
			evCases.setFocusable(false);
			evUnits.setFocusable(false);
			
			//setting layout Params for the convertView(List Cell)
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, (int)(40 * BaseActivity.px)));
			return convertView;
		}
		
		public void refresh(ArrayList<ProductDO> vecTemp)
		{
			this.vecOrderDetail = vecTemp;
			if(vecOrderDetail!=null && vecOrderDetail.size()>0)
			{
				tvNoOrderFound.setVisibility(View.GONE);
				llAddItems.setVisibility(View.VISIBLE);
			}
			else
			{
				llAddItems.setVisibility(View.GONE);
				tvNoOrderFound.setVisibility(View.VISIBLE);
			}
			notifyDataSetChanged();
		}
	}
}
