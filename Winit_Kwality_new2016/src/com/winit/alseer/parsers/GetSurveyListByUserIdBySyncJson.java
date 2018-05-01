package com.winit.alseer.parsers;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.StaticSurveyDL;
import com.winit.alseer.salesman.dataobject.OptionsDO;
import com.winit.alseer.salesman.dataobject.SurveyListDO;
import com.winit.alseer.salesman.dataobject.SurveyQuestionNewDO;
import com.winit.alseer.salesman.dataobject.UserSurveyDO;
import com.winit.alseer.salesman.utilities.LogUtils;

public class GetSurveyListByUserIdBySyncJson extends BaseHandler
{
	boolean currentElement = false;
	StringBuffer sb;
	
	private Vector<SurveyListDO> vecSurveyList = new Vector<SurveyListDO>();
	private SurveyListDO surveyListDO;

	private int response_code = -1;
	private String response_message = null;
	private Context contex;
	private Preference prefrec;
	private String UserId;
	public GetSurveyListByUserIdBySyncJson(InputStream is,Context context)
	{
		super(is);
		this.contex = context;
		prefrec = new Preference(contex);
		parse(is) ;
	}
	

	public void parse(InputStream is) 
	{
		String strResponse = getStringFromInputStream(is);
		try
		{
			JSONObject json = new JSONObject(strResponse);
			
			response_code	 = json.getInt("Status");
			response_message = json.getString("Message");
			
			if(response_message.equalsIgnoreCase("SUCCESS"))
			{
				//For Survey
				JSONArray jsonSurveyArray = json.getJSONArray("SurveyList");
				if(jsonSurveyArray != null && jsonSurveyArray.length() > 0)
			    {
					  vecSurveyList = new Vector<SurveyListDO>();	
					
					  for(int s = 0; s<jsonSurveyArray.length(); s++)
					  {
							JSONObject jsSurvey = jsonSurveyArray.getJSONObject(s);
							
							surveyListDO = new SurveyListDO();
							surveyListDO.surveyId 			= jsSurvey.getString("SurveyId");
							surveyListDO.surveyTypeCode 	= jsSurvey.getString("SurveyTypeCode");
							surveyListDO.surveyName 		= jsSurvey.getString("SurveyName");
							surveyListDO.surveyCode 		= jsSurvey.getString("SurveyCode");
							surveyListDO.startDate 			= jsSurvey.getString("StartDate");
							surveyListDO.endDate 			= jsSurvey.getString("EndDate");
							surveyListDO.isConfidential 	= jsSurvey.getString("IsConfidential");
							surveyListDO.isActive 			= jsSurvey.getString("IsActive");
							surveyListDO.statusCode 		= jsSurvey.getString("StatusCode");
							surveyListDO.createdBy 			= jsSurvey.getString("CreatedBy");
							surveyListDO.createdOn 			= jsSurvey.getString("CreatedOn");
							surveyListDO.modifiedBy 		= jsSurvey.getString("ModifiedBy");
							surveyListDO.modifiedOn 		= jsSurvey.getString("ModifiedOn");
							surveyListDO.userSurveyCount 	= jsSurvey.getString("UserSurveyCount");
							surveyListDO.description 		= jsSurvey.getString("Description");
							
							//For Questions
							JSONArray jsonQuestionArray = jsSurvey.getJSONArray("SurveyQuestionList");
							if(jsonQuestionArray != null && jsonQuestionArray.length() > 0)
							{
								for(int i = 0; i<jsonQuestionArray.length(); i++)
								{
									JSONObject jsonObject = jsonQuestionArray.getJSONObject(i);
									
									SurveyQuestionNewDO questionsDO = new SurveyQuestionNewDO();
									
									questionsDO.AnswerType 			= jsonObject.getString("AnswerType");
									questionsDO.ConditionType 		= jsonObject.getString("ConditionType");
									questionsDO.ConditionValue 		= jsonObject.getString("ConditionValue");
									questionsDO.CurrentQuestionId 	= jsonObject.getString("CurrentQuestionId");
									questionsDO.IsMandatory 		= jsonObject.getString("IsMandatory");
									questionsDO.ParentId 			= jsonObject.getString("ParentId");
									questionsDO.QuestionName 		= jsonObject.getString("QuestionName");
									questionsDO.QuestionShortForm 	= jsonObject.getString("QuestionShortForm");
									questionsDO.SequenceNumber 		= jsonObject.getString("SequenceNumber");
									questionsDO.SurveyCode 			= jsonObject.getString("SurveyCode");
									questionsDO.SurveyQuestionId 	= jsonObject.getString("SurveyQuestionId");
									
									//For Options
									JSONArray jsonOptionsArray = jsonObject.getJSONArray("Options");
									if(jsonOptionsArray != null && jsonOptionsArray.length() > 0)
									{
										for(int j = 0; j<jsonOptionsArray.length(); j++)
										{
											JSONObject optionJsonObject = jsonOptionsArray.getJSONObject(j);
											OptionsDO option = new OptionsDO();
											option.EmotionName 				= optionJsonObject.getString("EmotionName");
											option.ImagePath 				= optionJsonObject.getString("ImagePath");
											option.OptionName 				= optionJsonObject.getString("OptionName");
											option.SequenceNumber 			= optionJsonObject.getString("SequenceNumber");
											option.SurveyQuestionId 		= questionsDO.SurveyQuestionId;
											option.SurveyQuestionOptionId 	= optionJsonObject.getString("SurveyQuestionOptionId");
											option.isActive 				= optionJsonObject.getString("isActive");
											
											questionsDO.vecOptions.add(option);
										}
									}
									
									surveyListDO.vecQuestions.add(questionsDO);
								}
							}
							
							vecSurveyList.add(surveyListDO);
						 }
					  //Insesrt
					  insetAllSurveyData(prefrec.getStringFromPreference(Preference.INT_USER_ID,""));
				   }
			}
			else
			{
				LogUtils.errorLog("SurveyListHandlerJson", "Failed");
			}
		}
		catch (JSONException ex) 
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		sb.append(ch, start, length);
	}

	public Object getData()
	{
		if(vecSurveyList != null && vecSurveyList.size() > 0)
			return vecSurveyList;
		else
			return null;
	}

	public Object getErrorData() 
	{
		return null;
	}
	
	public void insetAllSurveyData(String userId)
	{
		StaticSurveyDL SSL = new StaticSurveyDL();
		Vector<UserSurveyDO> vecUserSurvey = new Vector<UserSurveyDO>();
		if(vecSurveyList.size()>0)
		{
			//Survey
			SSL.insertSurvey(vecSurveyList);
			
			for(Iterator<SurveyListDO> isl=vecSurveyList.iterator();isl.hasNext();)
			{
					SurveyListDO SLO = isl.next();
					UserSurveyDO userSur = new UserSurveyDO();
					userSur.UserId = userId;
					userSur.isActive = "true";
					userSur.SurveyId = SLO.surveyId;
					userSur.UserSurveyId = "";
					vecUserSurvey.add(userSur);
					//SurveyQuestions
					SSL.insertSurveyQuestions(SLO.vecQuestions);
				
					for(Iterator<SurveyQuestionNewDO> ques=SLO.vecQuestions.iterator();ques.hasNext();)
					{
						SurveyQuestionNewDO SQS = ques.next();
						//SurveyQuestionOptions
						SSL.insertSurveyQuestionsOptions(SQS.vecOptions);
					}
				
			}
			//UserSurvey
			SSL.insertUserSurvey(vecUserSurvey);
		}
		
	}
}
