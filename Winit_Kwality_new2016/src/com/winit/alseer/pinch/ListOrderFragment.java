package com.winit.alseer.pinch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.viewpager.extensions.PagerSlidingTabStrip;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;
import com.winit.sfa.salesman.OrderSummaryDetail;
import com.winit.sfa.salesman.ReturnOrderSummaryActivityNew;
import com.winit.sfa.salesman.SalesmanReturnOrder;
import com.winit.sfa.salesman.SavedOrderSummaryDetail;

import java.util.HashMap;
import java.util.Vector;

@SuppressLint("ValidFragment")
public class ListOrderFragment extends Fragment
{
	private ListView lvOrderList;
	private OrderListAdapter orderListAdapter;
	private Vector<TrxHeaderDO> vecOrderList;
	private Context context;
	private TextView tvNoDataFound;
	private LinearLayout llMain;
	private int type,trxStatus=-1;
	private final int NONE = -1;
	private int APPROVED_RETURN_ORDER_REQUEST = 1000;
	private Vector<TrxHeaderDO> vecOrderListTemp;
	private PagerSlidingTabStrip tabs;
	private  boolean fromOrderSummary = false;
	private int pos;
	private Preference preference;
	int i=0;
	String tag="";
	HashMap<String, String> hmShiftCodes = new HashMap<String, String>();
	
	private JourneyPlanDO mallsDetailsScreen ;
	
	public ListOrderFragment(Context context,int position, Vector<TrxHeaderDO> vecOrderList) {
		this.vecOrderList = vecOrderList;
		this.context = context;
		preference				=   new Preference(context);
	}
	
	public ListOrderFragment(Context context,int position, Vector<TrxHeaderDO> vecOrderList,int trxStatus) {
		this.vecOrderList = vecOrderList;
		this.context = context;
		this.trxStatus = trxStatus;
		preference				=   new Preference(context);
	}
	
	public ListOrderFragment(Context context,int position, Vector<TrxHeaderDO> vecOrderList,PagerSlidingTabStrip tabs, boolean fromOrderSummary) {
		this.vecOrderList = vecOrderList;
		this.context = context;
		this.pos = position;
		this.tabs = tabs;
		this.fromOrderSummary = fromOrderSummary;
		preference				=   new Preference(context);
	}
	
	public ListOrderFragment(Context context,int position, Vector<TrxHeaderDO> vecOrderList,PagerSlidingTabStrip tabs, boolean fromOrderSummary, JourneyPlanDO mallsDetailsScreen) {
		this.vecOrderList = vecOrderList;
		this.context = context;
		this.pos = position;
		this.tabs = tabs;
		this.fromOrderSummary = fromOrderSummary;
		this.mallsDetailsScreen = mallsDetailsScreen;
		preference				=   new Preference(context);
	}


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		llMain 	= (LinearLayout) inflater.inflate(R.layout.listview,null);
		initializeControls();
		return llMain;
	}
	
	private void initializeControls()
	{
		tvNoDataFound		=	(TextView)llMain.findViewById(R.id.tvNoDataFound);
		lvOrderList			=	(ListView)llMain.findViewById(R.id.lv);
		lvOrderList.setCacheColorHint(0);
		lvOrderList.setScrollbarFadingEnabled(true);
		hmShiftCodes=new CustomerDA().getShiftToCodes();
		if(vecOrderList!=null && vecOrderList.size()>0)
		{
			type  = vecOrderList.get(0).trxType;
			
			orderListAdapter = new OrderListAdapter(vecOrderList);
			lvOrderList.setAdapter(orderListAdapter);
		
					
			tvNoDataFound.setVisibility(View.GONE);
			lvOrderList.setVisibility(View.VISIBLE);
		}
		else
		{
			tvNoDataFound.setVisibility(View.VISIBLE);
			lvOrderList.setVisibility(View.GONE);
		}
	}
	
	public class OrderListAdapter extends BaseAdapter
	{
		private Vector<TrxHeaderDO> vecOrderList;
		public OrderListAdapter(Vector<TrxHeaderDO> vecOrderList) 
		{
			this.vecOrderList = vecOrderList;
		}
		@Override
		public int getCount() 
		{
			if(vecOrderList!=null && vecOrderList.size()>0)
				return vecOrderList.size();
			
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
			return position;
		}
		public void refresh(Vector<TrxHeaderDO> vecOrder)
		{
			this.vecOrderList=vecOrder;
			if(vecOrderList.size() > 0)
			{
				orderListAdapter.refresh(vecOrderList);
				tvNoDataFound.setVisibility(View.GONE);
				lvOrderList.setVisibility(View.VISIBLE);
			}
			else
			{
				tvNoDataFound.setVisibility(View.GONE);
				lvOrderList.setVisibility(View.VISIBLE);
			}
			notifyDataSetChanged();
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			final TrxHeaderDO trxHeaderDO			    = (TrxHeaderDO) vecOrderList.get(position);
			
			if(convertView == null)
				convertView					    =	(LinearLayout)LayoutInflater.from(context).inflate(R.layout.delivery_status_list_item, null);
			
			TextView tvJournyeDate			 	=	(TextView)convertView.findViewById(R.id.tvJournyeDate);
			TextView tvJournyeDateYear		  	=	(TextView)convertView.findViewById(R.id.tvJournyeDateYear);
			TextView tvTeleOrderNo		  		=	(TextView)convertView.findViewById(R.id.tvTeleOrderNo);
			TextView tvOrderAmount		  		=	(TextView)convertView.findViewById(R.id.tvOrderAmount);
			
			TextView tvOrderNo					=	(TextView)convertView.findViewById(R.id.tvOrderNo);
			TextView tvCustomerName				=	(TextView)convertView.findViewById(R.id.tvCustomerName);
			TextView tvCustomerLocation			=	(TextView)convertView.findViewById(R.id.tvCustomerLocation);
			TextView tvSatus					=	(TextView)convertView.findViewById(R.id.tvStatus);
			
			TextView tvCustomerPrice			=	(TextView)convertView.findViewById(R.id.tvCustomerPrice);
			TextView tvCustomerPriceUnit		=	(TextView)convertView.findViewById(R.id.tvCustomerPriceUnit);
			ImageView sideView					=   (ImageView)convertView.findViewById(R.id.sideView);
			ImageView ivDelete					=   (ImageView)convertView.findViewById(R.id.ivDelete);
			
			tvSatus.setVisibility(View.GONE);
			ivDelete.setVisibility(View.GONE);
			
			String date[] = CalendarUtils.getRequiredDateFormat(""+trxHeaderDO.trxDate).split("zz");
			tvJournyeDate.setText(date[0]);
			tvJournyeDateYear.setText(date[1]);
			
			if(type == TrxHeaderDO.get_TRXTYPE_MISSED_ORDER())
			{
				tvCustomerPrice.setText("N/A");
				tvCustomerPriceUnit.setVisibility(View.GONE);
			}
			else
			{
				tvCustomerPriceUnit.setVisibility(View.VISIBLE);
				
				
				if(tabs!=null && pos==5) /*&& preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER)*/
				tvCustomerPrice.setText(""+(((BaseActivity)getActivity()).amountFormate).format(trxHeaderDO.totalAmount)  );
				else
//					tvCustomerPrice.setText(""+(((BaseActivity)getActivity()).amountFormate).format(trxHeaderDO.totalAmount-trxHeaderDO.totalDiscountAmount));
                {
                    double total  = trxHeaderDO.totalAmount-StringUtils.getDouble(StringUtils.getDouble(trxHeaderDO.promotionalDiscount)*StringUtils.getDouble(trxHeaderDO.totalAmount+"")/100+"")
							/*StringUtils.getDouble(trxHeaderDO.promotionalDiscount)*/
							-((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100)
							- StringUtils.getFloat( trxHeaderDO.rateDiff+""  )
							+trxHeaderDO.totalVATAmount;
//                    double total  = trxHeaderDO.totalAmount-StringUtils.getDouble(StringUtils.getDouble(trxHeaderDO.promotionalDiscount)*StringUtils.getDouble(trxHeaderDO.totalAmount+"")/100+"")
//							/*StringUtils.getDouble(trxHeaderDO.promotionalDiscount)*/
//							-((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100)
//							- StringUtils.getFloat( trxHeaderDO.rateDiff+""  );
                    tvCustomerPrice.setText(""+(((BaseActivity)getActivity()).amountFormate).format(total ));
                }
				tvCustomerPriceUnit.setText(""+trxHeaderDO.currencyCode);
			
			}
			
			if(type == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && trxHeaderDO.trxStatus != TrxHeaderDO.get_TRX_STATUS_SAVED())
			{
				tvOrderAmount.setText("Invoice Amount");
			}
			else if(type == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
			{
				if(!TextUtils.isEmpty(trxHeaderDO.referenceCode) && !trxHeaderDO.referenceCode.equalsIgnoreCase(trxHeaderDO.trxCode))
					ivDelete.setVisibility(View.INVISIBLE);
				else
					ivDelete.setVisibility(View.VISIBLE);
			}
			
//			tvTeleOrderNo.setVisibility(View.VISIBLE);
			tvOrderNo.setText("Invoice No: "+trxHeaderDO.trxCode);
//			if(trxHeaderDO.referenceCode!=null && !trxHeaderDO.referenceCode.equalsIgnoreCase("null") && !trxHeaderDO.referenceCode.equalsIgnoreCase("") && !trxHeaderDO.referenceCode.equalsIgnoreCase(trxHeaderDO.trxCode))
//				tvTeleOrderNo.setText("("+trxHeaderDO.referenceCode+")");
//			else
//				tvTeleOrderNo.setText("");
			
			if(trxHeaderDO.status == TrxHeaderDO.get_TRX_DATA_PENDING_STATUS())
				sideView.setBackgroundColor(getActivity().getResources().getColor(R.color.status_pending));
			else if(trxHeaderDO.status == TrxHeaderDO.get_TRX_DATA_PENDING_STATUS_UNKNOWN())
				sideView.setBackgroundColor(getActivity().getResources().getColor(R.color.status_unknown));
//				sideView.setBackgroundColor(getActivity().getResources().getColor(R.color.status_delievered));
			else if(trxHeaderDO.status == TrxHeaderDO.get_TRX_DATA_PUSHED_STATUS())
				sideView.setBackgroundColor(getActivity().getResources().getColor(R.color.status_delievered));
			else
				sideView.setVisibility(View.INVISIBLE);
			
			tvCustomerName.setText("["+trxHeaderDO.clientCode+"] "+trxHeaderDO.siteName);
//			tvCustomerLocation.setText(trxHeaderDO.address+"");
			tvCustomerLocation.setText(hmShiftCodes.get(trxHeaderDO.clientCode)+"");

			ivDelete.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					((BaseActivity)context).showCustomDialog(context, getString(R.string.warning), "Are you sure you wish to delete "+trxHeaderDO.trxCode+" order?", "Yes", "No", "delete_saved,"+trxHeaderDO.trxCode);
//					cancelSavedOrder(trxHeaderDO.trxCode);
				}
			});
			
			convertView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent intent = null;
					TrxHeaderDO objOrderList = 	(TrxHeaderDO) v.getTag();;

					if(type == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
						trxStatus = objOrderList.trxStatus;
					else if(objOrderList.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED() || objOrderList.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED_MODIFIED())
						trxStatus = objOrderList.trxStatus;
					else if((objOrderList.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() || objOrderList.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
							&& objOrderList.trxStatus == TrxHeaderDO.get_TRX_STATUS_ADVANCE_ORDER_DELIVERED()
							&& !((BaseActivity)getActivity()).preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(((BaseActivity)getActivity()).preference.PRESELLER))
						trxStatus = objOrderList.trxStatus;
					else
						trxStatus = NONE;

 					switch (trxStatus)
					{
						case TrxHeaderDO.TRX_STATUS_GRV_APPROVED:
							intent = new Intent(context , SalesmanReturnOrder.class);
							intent.putExtra("trxHeaderDO", objOrderList);
							intent.putExtra("isFromReturn",true);
							intent.putExtra("mallsDetails", ((BaseActivity)getActivity()).mallsDetailss);
							startActivityForResult(intent,APPROVED_RETURN_ORDER_REQUEST);
							break;
						case NONE:
							objOrderList = (TrxHeaderDO) v.getTag();
							intent = new Intent(context , OrderSummaryDetail.class);
							intent.putExtra("trxHeaderDO", objOrderList);
							intent.putExtra("isFromOrderSummary",true);
							intent.putExtra("mallsDetails", ((BaseActivity)getActivity()).mallsDetailss);
							startActivity(intent);
							break;
						case TrxHeaderDO.TRX_STATUS_SAVED:
						case TrxHeaderDO.TRX_STATUS_ADVANCE_ORDER_DELIVERED:
						case TrxHeaderDO.TRX_STATUS_SAVED_MODIFY:
						{
							if(type == TrxHeaderDO.get_TRXTYPE_MISSED_ORDER()){
								intent = new Intent(context , OrderSummaryDetail.class);
								intent.putExtra("trxHeaderDO", objOrderList);
								intent.putExtra("isFromOrderSummary",true);
								intent.putExtra("mallsDetails", ((BaseActivity)getActivity()).mallsDetailss);
								startActivity(intent);
							}else{
								intent = new Intent(context ,SavedOrderSummaryDetail.class);
								intent.putExtra("trxHeaderDO", objOrderList);
								intent.putExtra("fromOrderSummary", fromOrderSummary);
								if(mallsDetailsScreen != null)
									intent.putExtra("mallsDetails", mallsDetailsScreen);
								else
									intent.putExtra("mallsDetails", ((BaseActivity)getActivity()).mallsDetailss);
								startActivity(intent);
							}
						}
							break;
						default:
							objOrderList = (TrxHeaderDO) v.getTag();
							intent = new Intent(context , ReturnOrderSummaryActivityNew.class);
//							intent = new Intent(context , OrderSummaryDetail.class);
							intent.putExtra("trxDetail", objOrderList.arrTrxDetailsDOs);
							intent.putExtra("trxHeaderDO", objOrderList);
							intent.putExtra("mallsDetails", ((BaseActivity)getActivity()).mallsDetailss);
							startActivity(intent);
							break;
					}
				}
			});
			convertView.setTag(trxHeaderDO);

			((BaseActivity)context).setTypeFaceRobotoNormal((ViewGroup) convertView);
			
			tvOrderNo.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvCustomerName.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvCustomerPrice.setTypeface(AppConstants.Roboto_Condensed_Bold);
			
			return convertView;
		}
	}

	public void cancelSavedOrder(final String trxCode)
	{
		((BaseActivity)getActivity()).showLoader("cancelling order...");
		new Thread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				new OrderDA().cancelSavedOrder(trxCode);
				for (int i = 0; i < vecOrderList.size(); i++) 
				{
					if(vecOrderList.get(i).trxCode.equalsIgnoreCase(trxCode))
					{
						vecOrderList.remove(i);
						break;
					}
				}
				
				((BaseActivity)getActivity()).runOnUiThread(new Runnable() 
				{
					
					@Override
					public void run() {
						
						((BaseActivity)getActivity()).hideLoader();
						orderListAdapter.notifyDataSetChanged();
						if(tabs != null && vecOrderList != null)
							tabs.setTabText(pos, vecOrderList.size());
						
						((BaseActivity)getActivity()).uploadData();
					}
				});
			}
		}).start();
	}
}