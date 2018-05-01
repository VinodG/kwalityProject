package com.winit.sfa.salesman;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.parsers.GetAllServey;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataobject.ServeyListDO;
import com.winit.alseer.salesman.utilities.NetworkUtility;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.RestClient;
import com.winit.kwalitysfa.salesman.R;

public class ServeyListActivity extends BaseActivity {

	private LinearLayout llServuey;
	private Button btnRefresh;
	private  TasktodoAdapter taskAdap;
	private  ArrayList<ServeyListDO> vecServeyListDOs;
	private  ListView lv_tasktodo;
	private  TextView tvNoRecords;
	@SuppressWarnings("deprecation")
	@Override
	public void initialize() 
	{
		llServuey = (LinearLayout) getLayoutInflater().inflate(R.layout.servey_list, null);
		llBody.addView(llServuey, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		lv_tasktodo = (ListView) llServuey.findViewById(R.id.lv_tasktodo);
		btnRefresh = (Button) llServuey.findViewById(R.id.btnRefresh);
		
		lv_tasktodo.setCacheColorHint(0);
		lv_tasktodo.setFadingEdgeLength(0);
		
		taskAdap = new TasktodoAdapter(new ArrayList<ServeyListDO>());
		lv_tasktodo.setAdapter(taskAdap);
		
	    taskAdap.refreshList(vecServeyListDOs);
		
	    tvNoRecords = (TextView) llServuey.findViewById(R.id.tvNoRecords);
	    if(vecServeyListDOs != null && vecServeyListDOs.size()>0)
	    {
	    	lv_tasktodo.setVisibility(View.VISIBLE);
	    	tvNoRecords.setVisibility(View.GONE);
	    }
	    else
	    {
	    	lv_tasktodo.setVisibility(View.GONE);
	    	tvNoRecords.setVisibility(View.VISIBLE);
	    }
	    
	    loadServey();
	    
	    setTypeFaceRobotoNormal(llServuey);
	    
	    btnRefresh.setOnClickListener(new OnClickListener()
	    {
			@Override
			public void onClick(View v)
			{
				loadServey();
			}
		});
	}
	
	public class TasktodoAdapter extends BaseAdapter
	{
		ArrayList<ServeyListDO> vecServeyListDOs;

		public TasktodoAdapter(ArrayList<ServeyListDO> vecServeyListDOs)
		{
			this.vecServeyListDOs = vecServeyListDOs;
		}

		@Override
		public int getCount()
		{
			if(vecServeyListDOs!=null && vecServeyListDOs.size()>0)
			{
				return vecServeyListDOs.size();
			}
			else
			return 0;
		}

		@Override
		public Object getItem(int arg0) 
		{
			return arg0;
		}

		@Override
		public long getItemId(int arg0) 
		{
			return arg0;
		}

		@Override
		public View getView(final int pos, View converView, ViewGroup arg2)
		{
			converView = getLayoutInflater().inflate(R.layout.task_list_content, null);
			TextView tvTaskName	= (TextView) converView.findViewById(R.id.tvTaskName);
			TextView tvTaskDesc	= (TextView) converView.findViewById(R.id.tvTaskDesc);
			ImageView ivImage	= (ImageView) converView.findViewById(R.id.ivImage);
			
			final ServeyListDO serveyListDO = vecServeyListDOs.get(pos);
			
			tvTaskName.setText(serveyListDO.SurveyTitle);
			tvTaskDesc.setText(serveyListDO.Guidelines);
			
			if(AppConstants.hmSurvey != null && AppConstants.hmSurvey.size() > 0 && AppConstants.hmSurvey.containsKey(serveyListDO.SurveyId))
				ivImage.setImageResource(R.drawable.check1_new);
			else
				ivImage.setImageResource(R.drawable.arrow_1);
			
			converView.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					Intent objIntent = new Intent(ServeyListActivity.this, ServeyDetailsActivity.class);
					objIntent.putExtra("Object", serveyListDO);
					startActivityForResult(objIntent, 1000);
				}
			});
			setTypeFaceRobotoNormal((ViewGroup)converView);
			return converView;
		}
		public void refreshList(ArrayList<ServeyListDO> vecServeyListDOs)
		{
			this.vecServeyListDOs = vecServeyListDOs;
			notifyDataSetChanged();
		}
	}
	
	private void loadServey()
	{
		if(isNetworkConnectionAvailable(ServeyListActivity.this))
		{
			showLoader("Please wait...");
			new Thread(new Runnable()
			{
				@Override
				public void run() {
					
					ConnectionHelper connectionHelper = new ConnectionHelper(ServeyListActivity.this);
					final GetAllServey getAllServey = new GetAllServey();
					connectionHelper.startDataDownload("{\"SurveyId\":0}", ServeyListActivity.this, preference.getStringFromPreference(Preference.EMP_NO, "")+"", getAllServey);
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							hideLoader();
							vecServeyListDOs = getAllServey.getData();
							if(vecServeyListDOs != null && vecServeyListDOs.size()>0)
						    {
								lv_tasktodo.setAdapter(new TasktodoAdapter(vecServeyListDOs));
						    	lv_tasktodo.setVisibility(View.VISIBLE);
						    	tvNoRecords.setVisibility(View.GONE);
						    }
						    else
						    {
						    	lv_tasktodo.setVisibility(View.GONE);
						    	tvNoRecords.setVisibility(View.VISIBLE);
						    }
						}
					});
				}
			}).start();
		}
		else
			showCustomDialog(ServeyListActivity.this,"Alert !" ,getString(R.string.no_internet), "Ok", null, "");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 2222)
		{
			setResult(2222);
			finish();
		}
		else if(requestCode == 1000 && resultCode == 1000)
		{
			if(taskAdap != null)
			{
				taskAdap = new TasktodoAdapter(vecServeyListDOs);
				lv_tasktodo.setAdapter(taskAdap);
			}
		}
	}
	
	/**
	 * Method to start downloading the data.
	 * @param method
	 * @param parameters
	 * @return boolean
	 */
	public boolean startDataDownload(String parameters, Context mContext, String userId, GetAllServey getAllServey)
	{
		if(NetworkUtility.isNetworkConnectionAvailable(mContext))
		{
			InputStream isResponse = null;
			
			try 
			{
				isResponse = new RestClient().sendRequest(parameters, userId);
				if(isResponse != null)
				{
					getAllServey.setInputStream(isResponse);
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				// closing the stream object
		    	try
		    	{
			    	if(isResponse != null)
			    	{
			    		isResponse.close();
			    		isResponse = null;
			    	}
		    	}catch(Exception e){}
			}
			return true;
		}
		else
		{
			return false;
		}
	}
}
