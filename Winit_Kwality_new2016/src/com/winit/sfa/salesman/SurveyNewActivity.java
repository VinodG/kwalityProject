package com.winit.sfa.salesman;

import java.util.Locale;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.winit.alseer.salesman.adapter.SurveyTaskAdapter;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.StaticSurveyDL;
import com.winit.alseer.salesman.dataobject.SubmitReviewDO;
import com.winit.alseer.salesman.dataobject.SurveyListDO;
import com.winit.alseer.salesman.dataobject.SurveyQuestionNewDO;
import com.winit.alseer.salesman.dataobject.UserSurveyAnswerDO;
import com.winit.alseer.salesman.imageloader.UploadImage;
import com.winit.alseer.salesman.listeners.SurveyPopupListner;
import com.winit.alseer.salesman.utilities.NetworkUtility;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;

/*In This Survey Module the Usage of UserId is bit confusion which is using both online and offline.
 * In online the UserID value is taken from Preference.EMP_NO and 
 *  In offline the UserID value is taken from Preference.INT_USER_ID which was recently added by backend people 
 *  in LoginUserResponse only for SurveyModle and its database operations.
 * */
public class SurveyNewActivity extends BaseActivity implements SurveyPopupListner
{

	private LinearLayout llSurveyNew ;
	private ListView lvSurvey;
	private Vector<SurveyListDO> vecSearch,vecSurveyListDO;
	private SurveyTaskAdapter taskAdapter;
	private PopupWindow popupWindowStores;
	private Vector<UserSurveyAnswerDO> vecUserSurveyAnswerResponse;
	private Vector<SurveyQuestionNewDO> vecQuestionsDO;
	private EditText etSearch;
	private TextView tvEmpty,tvHeader;
	private String UserId="6";
	private UploadImage uploadImage;
	private String customerId="1";
	
	private ImageView ivSearchCross;
	
	@Override
	public void initialize() 
	{
		llSurveyNew			=	(LinearLayout)inflater.inflate(R.layout.survey_new,null);
		llBody.addView(llSurveyNew,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		uploadImage = new UploadImage();
		
		UserId = preference.getStringFromPreference(Preference.EMP_NO,"");
		customerId = new Preference(this).getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");
		
		initializeControls();
		setTypeFaceRobotoNormal(llSurveyNew);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		vecSurveyListDO = new Vector<SurveyListDO>();
		vecSearch = new Vector<SurveyListDO>();
		
		taskAdapter = new SurveyTaskAdapter(SurveyNewActivity.this,SurveyNewActivity.this, vecSurveyListDO);
		lvSurvey.setAdapter(taskAdapter);
		
		showLoader("Survey Loading...");
		loadSurveyData(UserId);
		tvHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}
	
	private void initializeControls()
	{
		lvSurvey 	= (ListView)llSurveyNew.findViewById(R.id.lvTasks);
		etSearch	= (EditText)llSurveyNew.findViewById(R.id.etSearch);
		ivSearchCross			=	(ImageView)llSurveyNew.findViewById(R.id.ivSearchCross);
		tvEmpty		= (TextView)llSurveyNew.findViewById(R.id.tvEmpty);
		tvHeader	= (TextView)llSurveyNew.findViewById(R.id.tvHeader);
		
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				noData();
			}
		});

		etSearch.setHint("Search By Survey");
		etSearch.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(!TextUtils.isEmpty(s))
					ivSearchCross.setVisibility(View.VISIBLE);
				else
					ivSearchCross.setVisibility(View.GONE);
					if(!s.toString().equalsIgnoreCase(""))
					{
						vecSearch.clear();
						String str = s.toString().toLowerCase(Locale.US);
						
						if(vecSurveyListDO != null && vecSurveyListDO.size() > 0)
						{
							for(SurveyListDO files : vecSurveyListDO)
							{
								if(files.surveyName!=null && !files.surveyName.equalsIgnoreCase(""))
								{
									String st = files.surveyName.toLowerCase(Locale.US);
									if(st.contains(str))
									{
										vecSearch.add(files);
									}
								}
							}
						}
							
						if(vecSearch.size() == 0)
						{
							tvEmpty.setVisibility(View.VISIBLE);
							tvEmpty.setText("No Survey Found.");
							lvSurvey.setVisibility(View.GONE);
						}
						else
						{
							tvEmpty.setVisibility(View.GONE);
							lvSurvey.setVisibility(View.VISIBLE);
							if(taskAdapter!=null)
							{
								taskAdapter.refresh(vecSearch);
							}
							else
							{
								taskAdapter = new SurveyTaskAdapter(SurveyNewActivity.this,SurveyNewActivity.this, vecSearch);
								lvSurvey.setAdapter(taskAdapter);
							}
							if(vecSearch.size()==0){
								tvEmpty.setVisibility(View.VISIBLE);
								tvEmpty.setText("No Survey Found.");
								lvSurvey.setVisibility(View.GONE);
							}
						}
					}
					else
					{
						noData();
					}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) 
			{
			}
			
			@Override
			public void afterTextChanged(Editable s) 
			{
			}
		});
	}
	
	private void noData()
	{
		if(vecSurveyListDO!=null && vecSurveyListDO.size()>0){
			tvEmpty.setVisibility(View.GONE);
			lvSurvey.setVisibility(View.VISIBLE);	
			taskAdapter.refresh(vecSurveyListDO);
		}else{
			tvEmpty.setVisibility(View.VISIBLE);
			tvEmpty.setText("No Survey Found.");
			lvSurvey.setVisibility(View.GONE);
		}
	}

	
	@SuppressWarnings("unchecked")
	private void loadSurveyData(final String userId)
	{
	
			if(userId != null && userId.length() > 0 )
			{
				new Thread(new Runnable() {
					
					@Override
					public void run() 
					{
						
//						if(NetworkUtility.isNetworkConnectionAvailable(SurveyNewActivity.this))
//						{
//							ConnectionHelper connectionHelper = new ConnectionHelper(null);
//						 	vecSurveyListDO= (Vector<SurveyListDO>) connectionHelper.sendRequestForSurveyModule(SurveyNewActivity.this, BuildXMLRequest.getSurveyList(userId), ServiceURLs.GET_ALL_SURVEY);
//					 	}
//						else
//						{
							vecSurveyListDO = new StaticSurveyDL().getAllSurveyListByUserId(preference.getStringFromPreference(Preference.INT_USER_ID,""));
//						}
						runOnUiThread(new Runnable()
						{
							@Override
							public void run() 
							{
								
								if(vecSurveyListDO!=null && vecSurveyListDO.size()>0)
								{
									tvEmpty.setVisibility(View.GONE);
									lvSurvey.setVisibility(View.VISIBLE);
									
									if(taskAdapter!=null)
										taskAdapter.refresh(vecSurveyListDO);
									else
									{
										taskAdapter = new SurveyTaskAdapter(SurveyNewActivity.this,SurveyNewActivity.this, vecSurveyListDO);
										lvSurvey.setAdapter(taskAdapter);
									}
								}
								else
								{
									tvEmpty.setVisibility(View.VISIBLE);
									tvEmpty.setText("No Survey Found.");
									lvSurvey.setVisibility(View.GONE);
								}
								hideLoader();	
//								submitOfflineSurveryToServer();
							}
						});
						
					}
				}).start();
			}
			
		
		
	}
    
	
	//OfflineSurvery
	private void submitOfflineSurveryToServer()
	{
		if(NetworkUtility.isNetworkConnectionAvailable(SurveyNewActivity.this))
		{
			showLoader("Submitting Survey");
			new Thread(new Runnable() 
			{
				
				@Override
				public void run()
				{
					Vector<UserSurveyAnswerDO> vecOfflineUserSurvery = new StaticSurveyDL().getOfflineUserSurveyAnswers(preference.getStringFromPreference(Preference.INT_USER_ID, ""), "false");
					if(vecOfflineUserSurvery!=null && vecOfflineUserSurvery.size()>0)
					{
						for(int su=0;su<vecOfflineUserSurvery.size();su++)
						{
							UserSurveyAnswerDO userSurveyAnswerDO=vecOfflineUserSurvery.get(su);
							Vector<SurveyQuestionNewDO> vecOffQues = new StaticSurveyDL().getQuestionsBySurveyCode(userSurveyAnswerDO.SurveyCode, preference.getStringFromPreference(Preference.INT_USER_ID, ""), userSurveyAnswerDO.SurveyId, userSurveyAnswerDO.UserSurveyAnswerId, true);
							postSurveyToServer(vecOffQues, customerId, userSurveyAnswerDO.SurveyId,userSurveyAnswerDO.UserSurveyAnswerId);
						}
					}
					runOnUiThread(new Runnable() 
		        	{
						public void run() 
						{
							hideLoader();
						}
					});
				}
			}).start();
		}
	}
	
	private void postSurveyToServer(final Vector<SurveyQuestionNewDO> vector, final String cutomer_id, final String surveryId, String userSurveyAnswerId)
	{
		try 
        {
        	for (int i = 0; i < vector.size(); i++) 
			{
        		switch (AppConstants.getOptionsTypes(vector.get(i).AnswerType)) 
        		{
					case MEDIA:
						String imagePath = uploadImage.uploadImage(SurveyNewActivity.this,vector.get(i).Answer, "Survey", true,getMimeType(vector.get(i).Answer));
						vector.get(i).Answer=imagePath;
						break;

					default:
						break;
				}
				
			}
        	
        	
        	ConnectionHelper connectionHelper = new ConnectionHelper(null);
        	final SubmitReviewDO submitReviewDO =  (SubmitReviewDO) connectionHelper.sendRequestForSurveyModule(SurveyNewActivity.this, BuildXMLRequest.uploadOfflineSurvey(surveryId, vector,cutomer_id,UserId), ServiceURLs.GET_ADD_SURVEY_ANSWER);
        	
        	if(submitReviewDO!=null)
        	{
        		new StaticSurveyDL().updateUserSurveyAnswer("true", userSurveyAnswerId, preference.getStringFromPreference(Preference.INT_USER_ID,""), surveryId);
        	}
        	
        	
        	
        	
        } catch (Exception e) 
        {
        	hideLoader();
            e.printStackTrace();
        }
	}
	
	@Override
	public void onSelectedSurveyCount(final SurveyListDO surveyListDO) 
	{
		
		//Need to show survey details which alresdy posted
		
		showLoader("Loading..");
		
		new Thread(new Runnable() {
			
			@Override
			public void run()
			{
//				if(NetworkUtility.isNetworkConnectionAvailable(SurveyNewActivity.this))
//				{
//					ConnectionHelper connectionHelper = new ConnectionHelper(null);
//					vecUserSurveyAnswerResponse= (Vector<UserSurveyAnswerDO>) connectionHelper.sendRequestForSurveyModule(SurveyNewActivity.this,
//							BuildXMLRequest.getUserSurveyAnswerByUserId(UserId, surveyListDO.surveyId), ServiceURLs.GET_USER_SURVEY_ANSWER_BY_USER_ID);
//				}
//				else
//				{
					vecUserSurveyAnswerResponse = new StaticSurveyDL().getUserSurveyAnswers(preference.getStringFromPreference(Preference.INT_USER_ID,""), surveyListDO.surveyId);
//				}
				
				runOnUiThread(new Runnable() 
				{
					public void run() 
					{
						hideLoader();
						if(vecUserSurveyAnswerResponse!=null && vecUserSurveyAnswerResponse.size()>0)
						{
//							popupWindowStores = popupWindowTypes(vecUserSurveyAnswerResponse,surveyListDO);
//							popupWindowStores.showAtLocation(lvSurvey, Gravity.CENTER, 0, 0);
							showUserSurveyAnswer(vecUserSurveyAnswerResponse,surveyListDO);
						}
					}
				});
			}
		}).start();
		
		
		
		
	}
	
	private void showUserSurveyAnswer(final Vector<UserSurveyAnswerDO> vecUserSurveyAnswerResponse, final SurveyListDO surveyListDO) 
	{
		CustomBuilder builder = new CustomBuilder(SurveyNewActivity.this, "Survey Details By User", true);
		builder.setSingleChoiceItems(vecUserSurveyAnswerResponse, null, new CustomBuilder.OnClickListener() 
		{
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject) 
			{
				final UserSurveyAnswerDO objUserSurveyAnswerDO = (UserSurveyAnswerDO) selectedObject;
	    		builder.dismiss();
    			builder.dismiss();
    			new Thread(new Runnable() {
					
					@Override
					public void run() 
					{
						vecQuestionsDO = new StaticSurveyDL().getQuestionsBySurveyCode(surveyListDO.surveyCode, preference.getStringFromPreference(Preference.INT_USER_ID,""), surveyListDO.surveyId,objUserSurveyAnswerDO.UserSurveyAnswerId, true);
						runOnUiThread( new Runnable() {
							public void run() 
							{
								if(vecQuestionsDO!=null && vecQuestionsDO.size()>0)
								{
									String surveyId = surveyListDO.surveyId;
									
									Intent intent = new Intent(SurveyNewActivity.this, SurveyQuestionActivity.class);
									Bundle bundle = new Bundle();
									bundle.putSerializable("Answers", vecQuestionsDO);
									bundle.putString("For","Answers");
									intent.putExtra("SurveyName",surveyListDO.surveyName);
									intent.putExtras(bundle);
									startActivityForResult(intent,1000);
									
								}
							}
						});
						
					}
				}).start();
		    }
		}); 
		builder.show();				
	}
	
//	private PopupWindow popupWindowTypes(final Vector<UserSurveyAnswerDO> vecUserSurveyAnswerResponse, final SurveyListDO surveyListDO) 
//	{
//		popupWindowStores = new PopupWindow(SurveyNewActivity.this);
//        
//		LinearLayout llVIew = (LinearLayout) inflater.inflate(R.layout.survey_popup, null);
//		
//		ListView listViewStores =(ListView) llVIew.findViewById(R.id.lvSurveyPopup);
//		TextView tvPopupTitle =(TextView) llVIew.findViewById(R.id.tvPopupTitle);
//
//		tvPopupTitle.setText("Survey Details By User");
//		
//        listViewStores.setAdapter(new StoresAdapter(SurveyNewActivity.this,vecUserSurveyAnswerResponse));
//         
//        popupWindowStores.setFocusable(true);
//        popupWindowStores.setWidth(((preference.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 720))/4)*3);
//        popupWindowStores.setHeight(((preference.getIntFromPreference(Preference.DEVICE_DISPLAY_HEIGHT, 1280)/2)));
//        popupWindowStores.setContentView(llVIew);
//        
//        setTypeFaceRobotoNormal(llVIew);
//        listViewStores.setOnItemClickListener(new OnItemClickListener() 
//        {
//
//			@SuppressWarnings("unchecked")
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, final int position,
//					long arg3) 
//			{
//				popupWindowStores.dismiss();
//				popupWindowStores = null;
//				
//				new Thread(new Runnable() {
//					
//					@Override
//					public void run() 
//					{
////						if(NetworkUtility.isNetworkConnectionAvailable(SurveyNewActivity.this))
////						{
////							ConnectionHelper connectionHelper = new ConnectionHelper(null);
////							vecQuestionsDO= (Vector<SurveyQuestionNewDO>) connectionHelper.sendRequestForSurveyModule(SurveyNewActivity.this, 
////									BuildXMLRequest.getSurveyDetailsByUserId(UserId, vecUserSurveyAnswerResponse.get(position).UserSurveyAnswerId), ServiceURLs.GET_SURVEY_DETAILS_BY_USER_ID);
////						}
////						else
////						{
//							vecQuestionsDO = new StaticSurveyDL().getQuestionsBySurveyCode(surveyListDO.surveyCode, preference.getStringFromPreference(Preference.INT_USER_ID,""), surveyListDO.surveyId,vecUserSurveyAnswerResponse.get(position).UserSurveyAnswerId, true);
////						}
//						
//						runOnUiThread( new Runnable() {
//							public void run() 
//							{
//								if(vecQuestionsDO!=null && vecQuestionsDO.size()>0)
//								{
//									String surveyId = surveyListDO.surveyId;
//									
//									Intent intent = new Intent(SurveyNewActivity.this, SurveyQuestionActivity.class);
//									Bundle bundle = new Bundle();
//									bundle.putSerializable("Answers", vecQuestionsDO);
//									bundle.putString("For","Answers");
//									intent.putExtra("SurveyName",surveyListDO.surveyName);
//									intent.putExtras(bundle);
//									startActivityForResult(intent,1000);
//									
//								}
//							}
//						});
//						
//					}
//				}).start();
//				
//				
//			}
//		});
//        
//        return popupWindowStores;
//	}
	private class StoresAdapter extends BaseAdapter
	{
	    private Context context;
		private Vector<UserSurveyAnswerDO> vecUserSurveyAnswer;
		public StoresAdapter(Context context,Vector<UserSurveyAnswerDO> vecUserSurveyAnswerResponse) 
		{
			this.vecUserSurveyAnswer = vecUserSurveyAnswerResponse;
			this.context     = context;
		}
		@Override
		public int getCount() {
			if(vecUserSurveyAnswer != null && vecUserSurveyAnswer.size() > 0)
				return vecUserSurveyAnswer.size();
			return 0;
		}
	
		@Override
		public Object getItem(int position) 
		{
			return vecUserSurveyAnswer.get(position);
		}
	
		@Override
		public long getItemId(int position) 
		{
			return position;
		}
	    public void refreshAdapter(Vector<UserSurveyAnswerDO> vecStores)
	    {
	    	this.vecUserSurveyAnswer = vecStores;
	    	notifyDataSetChanged();
	    }
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
				convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, null);
				TextView tvBrandNam = (TextView) convertView.findViewById(R.id.tvBrandName);
				tvBrandNam.setText(vecUserSurveyAnswer.get(position).CreatedOn.split("T")[0] +"    "+ vecUserSurveyAnswer.get(position).FirstName);
				tvBrandNam.setTextSize(getResources().getDimension(R.dimen.size18));
				
			return convertView;
		}
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		
		if(resultCode==2000 && requestCode==1000)
		{
			showLoader("Survey Loading...");
			loadSurveyData(UserId);
		}
	}
	
	@Override
	protected void onPause() 
	{
		// TODO Auto-generated method stub
		super.onPause();
		if(popupWindowStores!=null && popupWindowStores.isShowing())
			popupWindowStores.dismiss();
	}
	@Override
	protected void onResume() 
	{
		super.onResume();
		showLoader("Survey Loading...");
		loadSurveyData(UserId);
	}
	
}
