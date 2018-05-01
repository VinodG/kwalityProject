//package com.winit.sfa.salesman;
//
//import java.util.Vector;
//
//import android.content.Intent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.winit.alseer.salesman.common.AppConstants;
//import com.winit.alseer.salesman.dataaccesslayer.UserInfoDA;
//import com.winit.alseer.salesman.dataobject.LoginUserInfo;
//import com.winit.kwality.salesman.R;
//
//public class MessangingUserListActivity extends BaseActivity
//{
//	private LinearLayout llUsersList ;
//	private TextView tvHeader;
//	private ListView lvUsers;
//	private UserListAdapter adapter;
//	private Vector<LoginUserInfo> vecUsers;
//	
//	@Override
//	public void initialize() 
//	{
//		llUsersList			=	(LinearLayout)inflater.inflate(R.layout.customerlist,null);
//		llBody.addView(llUsersList,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//		
//		initializeControls();
//		loadData();
//		
//		setTypeFaceRobotoNormal(llUsersList);
//		tvHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
//	}
//	
//	
//	private void loadData()
//	{
//		showLoader("Loading...");
//		new Thread(new Runnable() 
//		{
//			
//			@Override
//			public void run() 
//			{
//				vecUsers = new UserInfoDA().getAllUsersInfo();
//				runOnUiThread(new Runnable() 
//				{
//					public void run() 
//					{
//						if(adapter != null)
//							adapter.refreshList(vecUsers);
//						hideLoader();
//					}
//				});
//			}
//		}).start();
//	}
//	
//	private void initializeControls()
//	{
//		tvHeader  = (TextView) llUsersList.findViewById(R.id.tvHeader);
//		lvUsers   = (ListView) llUsersList.findViewById(R.id.lvUsers);
//		
//		adapter = new UserListAdapter(new Vector<LoginUserInfo>());
//		lvUsers.setAdapter(adapter);
//	}
//	
//	private class UserListAdapter extends BaseAdapter
//	{
//		private Vector<LoginUserInfo> vecUsers;
//		
//		public UserListAdapter(Vector<LoginUserInfo> vecUsers)
//		{
//			this.vecUsers = vecUsers;
//		}
//		
//		@Override
//		public int getCount() 
//		{
//			if(vecUsers != null && vecUsers.size() > 0)
//				return vecUsers.size();
//			return 0;
//		}
//
//		@Override
//		public Object getItem(int position) 
//		{
//			return position;
//		}
//
//		@Override
//		public long getItemId(int position) 
//		{
//			return position;
//		}
//		
//		public void refreshList(Vector<LoginUserInfo> vecUsers) 
//		{
//			this.vecUsers = vecUsers;
//			notifyDataSetChanged();
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) 
//		{
//			LoginUserInfo userInfo  = vecUsers.get(position);
//			
//			convertView	= (LinearLayout)getLayoutInflater().inflate(R.layout.customer_list_cell,null);
//			
//			TextView tvCustomerName 		= (TextView) convertView.findViewById(R.id.tvCustomerName);
//			TextView tvCustomerDescription 	= (TextView) convertView.findViewById(R.id.tvCustomerDescription);
//			TextView tvTime 				= (TextView) convertView.findViewById(R.id.tvTime);
//			ImageView ivCustomer			= (ImageView)convertView.findViewById(R.id.ivCustomer);
//			
//			tvCustomerName.setText(userInfo.strUserName);
//			tvCustomerName.setTypeface(AppConstants.Roboto_Condensed_Bold);
//			tvCustomerDescription.setText(userInfo.strUserId);
//			tvCustomerDescription.setTypeface(AppConstants.Roboto_Condensed);
//			
//			convertView.setOnClickListener(new OnClickListener() 
//			{
//				
//				@Override
//				public void onClick(View v) 
//				{
//					Intent intent = new Intent(MessangingUserListActivity.this, MessageViewActivity.class);
//					startActivity(intent);
//					
//				}
//			});
//			
//			
//			
//			return convertView;
//		}
//		
//	}
//}
