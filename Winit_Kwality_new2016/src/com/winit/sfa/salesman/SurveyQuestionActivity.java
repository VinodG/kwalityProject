package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.Vector;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.adapter.AnswerAdapter;
import com.winit.alseer.salesman.adapter.QuestionAdapter;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.StaticSurveyDL;
import com.winit.alseer.salesman.dataobject.OptionsDO;
import com.winit.alseer.salesman.dataobject.SubmitReviewDO;
import com.winit.alseer.salesman.dataobject.SurveyListDO;
import com.winit.alseer.salesman.dataobject.SurveyQuestionNewDO;
import com.winit.alseer.salesman.dataobject.UserSurveyAnswerDO;
import com.winit.alseer.salesman.imageloader.UploadImage;
import com.winit.alseer.salesman.listeners.DropdownSelectionListner;
import com.winit.alseer.salesman.utilities.CalendarUtils;
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
public class SurveyQuestionActivity extends BaseActivity implements DropdownSelectionListner
{

	private LinearLayout llSurveyQuestions ;
	private ListView lvQuestions;
	private TextView tvNoQuestions,tvSurveyName;
	private Vector<SurveyQuestionNewDO> vecQuestionsDO;
	private QuestionAdapter questionAdapter;
	private AnswerAdapter answerAdapter;
	private Button btnSubmit,btnCancle; 
	private UploadImage uploadImage;
	private String surveyId,SurveyName="";
	private String userId="6";
	private String customerId="1";
	private SurveyListDO surveyList;
	
	private Vector<SurveyQuestionNewDO> vecAnswers=new Vector<SurveyQuestionNewDO>();
	
	
	@Override
	public void initialize() 
	{
		llSurveyQuestions			=	(LinearLayout)inflater.inflate(R.layout.survey_question,null);
		llBody.addView(llSurveyQuestions,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		uploadImage = new UploadImage();
		
		initializeControls();
		setTypeFaceRobotoNormal(llSurveyQuestions);
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		userId = new Preference(this).getStringFromPreference(Preference.EMP_NO, "");
		customerId = new Preference(this).getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");
		
		vecQuestionsDO = new Vector<SurveyQuestionNewDO>();
		questionAdapter = new QuestionAdapter(SurveyQuestionActivity.this,SurveyQuestionActivity.this,vecQuestionsDO);
		lvQuestions.setAdapter(questionAdapter);
		
		
		if(getIntent().getExtras().getString("For") != null)
		{
			if(getIntent().getExtras().getString("For").equalsIgnoreCase("Answers"))
			{
				ArrayList<SurveyQuestionNewDO> ques = (ArrayList<SurveyQuestionNewDO>) getIntent().getExtras().getSerializable("Answers");
				vecAnswers.addAll(ques);
				
				SurveyName= getIntent().getExtras().getString("SurveyName");
				tvSurveyName.setText(SurveyName);
				answerAdapter = new AnswerAdapter(SurveyQuestionActivity.this,SurveyQuestionActivity.this, vecAnswers);
				lvQuestions.setAdapter(answerAdapter);
				btnSubmit.setVisibility(View.GONE);
				btnCancle.setVisibility(View.GONE);
				
			}
			else if(getIntent().getExtras().getString("For").equalsIgnoreCase("Questions"))
			{
//				SurveyListDO
				surveyList = (SurveyListDO) getIntent().getExtras().getSerializable("SurveyListDO");
				surveyId = surveyList.surveyId;
				SurveyName= surveyList.surveyName;
				tvSurveyName.setText(SurveyName);
				showLoader("SurveyQuestions Loading...");
				getSurveyQuestionsData(surveyList);
				btnSubmit.setVisibility(View.VISIBLE);
				btnCancle.setVisibility(View.VISIBLE);
			}
		}
		tvSurveyName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnCancle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnSubmit.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
	}
	
	private void initializeControls()
	{
		lvQuestions	  = (ListView)llSurveyQuestions.findViewById(R.id.lvQuestions);
		tvNoQuestions = (TextView)llSurveyQuestions.findViewById(R.id.tvNoQuestions);
		tvSurveyName = (TextView)llSurveyQuestions.findViewById(R.id.tvSurveyName);
		btnSubmit 	= (Button)llSurveyQuestions.findViewById(R.id.btnSubmit);
		btnCancle	= (Button)llSurveyQuestions.findViewById(R.id.btnCancle);
		
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				validateQuestions();
				
			}
		});
		btnCancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				finish();
				
			}
		});
	}
	
	
	private void validateQuestions()
	{
		if(questionAdapter!=null)
		{
			
			int count = 0;
			for(SurveyQuestionNewDO questions: questionAdapter.getAdapterData())
			{
				if(questions.IsMandatory.equalsIgnoreCase("true") && questions.Answer.equalsIgnoreCase(""))
				{
					count += 1;
				}
			}
			
			if(count != 0)
			{
				showCustomDialog(SurveyQuestionActivity.this, "Alert", "Please answer the mandatory questions.", "OK", "", "");
			}
			else
			{
				//post survey data to server
				uploadFiles(questionAdapter.getAdapterData(),customerId);
			}
			
		}
	}
	
	private void uploadFiles(final Vector<SurveyQuestionNewDO> vector, final String cutomer_id)
	{
		showLoader("Loading");
		Thread thread = new Thread(new Runnable()
		{
		    @Override
		    public void run() 
		    {
		    	if(NetworkUtility.isNetworkConnectionAvailable(SurveyQuestionActivity.this))
				{
		    		postSurveyToServer(vector, cutomer_id);
				}
		    	else
		    	{
		    		String userSurveyAnswerId=""+new StaticSurveyDL().generateOfflineUserSurveyAnswerId();
		    		String userId = preference.getStringFromPreference(Preference.INT_USER_ID, "");
		    		storeSurveyInLocalDatabase(vector, userId,userSurveyAnswerId,"false");
		    	}
		    	runOnUiThread(new  Runnable() 
		    	{
					public void run() 
					{
						hideLoader();
						setResult(2000);
						finish();
					}
				});
		    }
		});

		thread.start();
	}
	
	private void storeSurveyInLocalDatabase(final Vector<SurveyQuestionNewDO> vector, final String userID,final String userSurveyAnswerId,String uploadStatus)
	{
		//For inserting data into tblUserSurveyAnswer
		UserSurveyAnswerDO objUserSurveyAnswerDO = new UserSurveyAnswerDO();
			objUserSurveyAnswerDO.UserSurveyAnswerId= userSurveyAnswerId;
			objUserSurveyAnswerDO.SurveyId=surveyList.surveyId;
			objUserSurveyAnswerDO.UserId=userID;
			objUserSurveyAnswerDO.IsActive="True";
			objUserSurveyAnswerDO.CreatedBy=userID;
			objUserSurveyAnswerDO.CreatedOn=CalendarUtils.getCurrentDateAsStringForJourneyPlan();//CalendarUtils.getCurrentDateAsString();//vinod
			objUserSurveyAnswerDO.ModifiedBy=userID;
			objUserSurveyAnswerDO.ModifiedOn=CalendarUtils.getCurrentDateAsStringForJourneyPlan();//CalendarUtils.getCurrentDateAsString();
			objUserSurveyAnswerDO.FirstName=customerId;
			objUserSurveyAnswerDO.LastName="";
			objUserSurveyAnswerDO.UploadStatus=uploadStatus;
			objUserSurveyAnswerDO.From="";
			objUserSurveyAnswerDO.To="";
			objUserSurveyAnswerDO.Ex1="";
			objUserSurveyAnswerDO.Remarks="";
		Vector<UserSurveyAnswerDO> vecUserSurveyAnswerDO = new Vector<UserSurveyAnswerDO>();
		vecUserSurveyAnswerDO.add(objUserSurveyAnswerDO);
		new StaticSurveyDL().insertUserSurveyAnswer(vecUserSurveyAnswerDO);
		
		//For inserting data into tblUserSurveyAnswerDetails
		saveQuestions(vector,userSurveyAnswerId);
		
	}
	public  void saveQuestions(Vector<SurveyQuestionNewDO> vecQuestions,String userSurveyAnswerId)
	{
		Vector<UserSurveyAnswerDO> vecUserSurveyAnswerDO = new Vector<UserSurveyAnswerDO>();
		for (int i = 0; i < vecQuestions.size(); i++)
		{
			if(vecQuestions.get(i).AnswerType.equalsIgnoreCase("CHECKBOX"))
			{
				if(vecQuestions.get(i).vecOptions!=null && vecQuestions.get(i).vecOptions.size()>0)
				{
					for(OptionsDO options:vecQuestions.get(i).vecOptions)
					{
						if(options.isChecked)
						{
							UserSurveyAnswerDO objUserSurveyAnswerDO = new UserSurveyAnswerDO();
							
							objUserSurveyAnswerDO.SurveyQuestionId=vecQuestions.get(i).SurveyQuestionId;
							objUserSurveyAnswerDO.SurveyOptionId=options.SurveyQuestionOptionId;
							objUserSurveyAnswerDO.AnswerType=vecQuestions.get(i).AnswerType;
							objUserSurveyAnswerDO.Answer=options.SurveyQuestionOptionId;
							objUserSurveyAnswerDO.UserSurveyAnswerId=userSurveyAnswerId;
							
							vecUserSurveyAnswerDO.add(objUserSurveyAnswerDO);
							
						}
					}
				}
			}
			else
			{
				UserSurveyAnswerDO objAnswerDO = new UserSurveyAnswerDO();
				
				objAnswerDO.SurveyQuestionId = vecQuestions.get(i).SurveyQuestionId;
				objAnswerDO.SurveyOptionId = vecQuestions.get(i).SurveyQuestionOptionId;
				objAnswerDO.AnswerType = vecQuestions.get(i).AnswerType;
				objAnswerDO.Answer = vecQuestions.get(i).Answer;
				objAnswerDO.UserSurveyAnswerId=userSurveyAnswerId;
				vecUserSurveyAnswerDO.add(objAnswerDO);
			}
		}
		
		new StaticSurveyDL().insertUserSurveyAnswerDetails(vecUserSurveyAnswerDO);
		
	}
	private void postSurveyToServer(final Vector<SurveyQuestionNewDO> vector, final String cutomer_id)
	{
		try 
        {
        	for (int i = 0; i < vector.size(); i++) 
			{
        		switch (AppConstants.getOptionsTypes(vector.get(i).AnswerType)) 
        		{
					case MEDIA:
						String imagePath = uploadImage.uploadImage(SurveyQuestionActivity.this,vector.get(i).Answer, "Survey", true,getMimeType(vector.get(i).Answer));
						vector.get(i).Answer=imagePath;
						break;

					default:
						break;
				}
				
			}
        	
        	
        	ConnectionHelper connectionHelper = new ConnectionHelper(null);
        	final SubmitReviewDO submitReviewDO =  (SubmitReviewDO) connectionHelper.sendRequestForSurveyModule(SurveyQuestionActivity.this, BuildXMLRequest.uploadSurvey(surveyId, questionAdapter.getAdapterData(),cutomer_id,userId), ServiceURLs.GET_ADD_SURVEY_ANSWER);
        	
        	runOnUiThread(new Runnable() 
        	{
				public void run() 
				{
					hideLoader();
					if(submitReviewDO!=null)
					{
						//Update  user with the dialog regarding success/failure of the posting survey.
						String userSurveyAnswerId=""+new StaticSurveyDL().generateOfflineUserSurveyAnswerId();
			    		String userId = preference.getStringFromPreference(Preference.INT_USER_ID, "");
			    		storeSurveyInLocalDatabase(vector, userId,userSurveyAnswerId,"true");
						showCustomDialog(SurveyQuestionActivity.this, "Alert!", "Data posted succesfully", "OK", "", "SurveyDataPost");
					}
					
				}
			});
        	
        	
        } catch (Exception e) 
        {
        	hideLoader();
            e.printStackTrace();
        }
	}
	
	@SuppressWarnings("unchecked")
	private void  getSurveyQuestionsData(final SurveyListDO surveyList)
	{
       
		new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				
				if(surveyList != null )
				{
//					if(NetworkUtility.isNetworkConnectionAvailable(SurveyQuestionActivity.this))
//					{
//						ConnectionHelper connectionHelper = new ConnectionHelper(null);
//						vecQuestionsDO = (Vector<SurveyQuestionNewDO>) connectionHelper.sendRequestForSurveyModule(SurveyQuestionActivity.this, BuildXMLRequest.getSurveyQuestions(surveyList.surveyId), ServiceURLs.GET_ALL_SURVEY_QUESTIONS);
//					}
//					else
//					{
						vecQuestionsDO = new StaticSurveyDL().getQuestionsBySurveyCode(surveyList.surveyCode,preference.getStringFromPreference(Preference.INT_USER_ID,""),surveyList.surveyId,"",false);
//					}
				}
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						
						if(vecQuestionsDO != null && vecQuestionsDO.size() > 0)
						{
							lvQuestions.setVisibility(View.VISIBLE);
							tvNoQuestions.setVisibility(View.GONE);
							questionAdapter.refresh(vecQuestionsDO);
						}
						else
						{
							lvQuestions.setVisibility(View.GONE);
							tvNoQuestions.setVisibility(View.VISIBLE);
						}
						
						hideLoader();
						
					}
				});
			}
		}).start();
		
		
	}

	//
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 400)
		{
			   switch (resultCode) 
			   {
					case RESULT_CANCELED:
						Log.i("Camera", "User cancelled");
						break;
	
					case RESULT_OK:
						if(data!=null && data.getExtras().containsKey("QuestionsDO"))
						{
							SurveyQuestionNewDO questionDo = (SurveyQuestionNewDO) data.getExtras().getSerializable("QuestionsDO");
							refreshWithNew(questionDo);
							questionAdapter.refresh(vecQuestionsDO);
							
						}
						break;
				}

		}
		
	}
	private void refreshWithNew(SurveyQuestionNewDO questionDo)
	{
		for(SurveyQuestionNewDO ques:vecQuestionsDO)
		{
			if(ques.SurveyQuestionId.equalsIgnoreCase(questionDo.SurveyQuestionId))
			{
				ques.Answer = questionDo.Answer;
				if(!questionDo.Answer.equalsIgnoreCase(""))
				{
					ques.vecMediaAnswers.add(questionDo.Answer);
				}
			}
		}
	}
	@Override
	public void onOptionSelected(SurveyQuestionNewDO questionDo) 
	{
		for(SurveyQuestionNewDO ques:vecQuestionsDO)
		{
			if(ques.SurveyQuestionId.equalsIgnoreCase(questionDo.SurveyQuestionId))
			{
				ques.Answer = questionDo.Answer;
				ques.SurveyQuestionOptionId = questionDo.SurveyQuestionOptionId;
			}
		}
		
		questionAdapter.refresh(vecQuestionsDO);
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		if(from.equalsIgnoreCase("SurveyDataPost"))
		{
			setResult(2000);
			finish();
		}
	}
}

