package com.winit.alseer.parsers;

import java.io.InputStream;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.winit.alseer.salesman.dataobject.OptionsDO;
import com.winit.alseer.salesman.dataobject.SurveyQuestionNewDO;
import com.winit.alseer.salesman.dataobject.UserSurveyAnswerDO;
import com.winit.alseer.salesman.utilities.LogUtils;

public class SurveyQuestionsHandlerJson extends BaseHandler
{
	boolean currentElement = false;
	StringBuffer sb;
	private Vector<SurveyQuestionNewDO> vecQuestionsDO;
	private SurveyQuestionNewDO questionsDO;

	private int response_code = -1;
	private String response_message = null;

	public SurveyQuestionsHandlerJson(InputStream is) 
	{
		super(is);
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
				JSONArray jsonArray = json.getJSONArray("SurveyQuestionList");
				
				if(jsonArray != null && jsonArray.length() > 0)
				{
					vecQuestionsDO = new Vector<SurveyQuestionNewDO>();
					for(int i = 0; i<jsonArray.length(); i++)
					{
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						
						questionsDO = new SurveyQuestionNewDO();
						
						//new response
						/* "SurveyQuestionId": 53,
				            "SurveyCode": "DF179D90-BDDC-4D1C-A98F-544817CB235E",
				            "SequenceNumber": 1,
				            "QuestionName": "imge1",
				            "AnswerType": "IMAGE",
				            "IsMandatory": false,
				            "Options": [],
				            "ParentId": 0,
				            "ConditionType": "",
				            "ConditionValue": 0,
				            "CurrentQuestionId": 0,
				            "Childs": [],
				            "QuestionShortForm": "",
				            "UserSurveyAnswerList": [],
				            "SurveyQuestionOptionList": null,
				            "ChildQuestionList": null*/
						
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
						
						JSONArray optionsJsonArray = jsonObject.getJSONArray("Options");
						if(optionsJsonArray != null && optionsJsonArray.length() > 0)
						{
							for(int j = 0; j<optionsJsonArray.length(); j++)
							{
								JSONObject optionJsonObject = optionsJsonArray.getJSONObject(j);
								OptionsDO option = new OptionsDO();
								option.EmotionName 				= optionJsonObject.getString("EmotionName");
								option.ImagePath 				= optionJsonObject.getString("ImagePath");
								option.OptionName 				= optionJsonObject.getString("OptionName");
								option.SequenceNumber 			= optionJsonObject.getString("SequenceNumber");
								option.SurveyQuestionId 		= optionJsonObject.getString("SurveyQuestionId");
								option.SurveyQuestionOptionId 	= optionJsonObject.getString("SurveyQuestionOptionId");
								option.isActive 				= optionJsonObject.getString("isActive");
								if(option != null)
									questionsDO.vecOptions.add(option);
							}
						}
						
						JSONArray userAnswerjsonArray = jsonObject.getJSONArray("UserSurveyAnswerList");
						
						if(userAnswerjsonArray != null && userAnswerjsonArray.length() > 0)
						{
							for(int p = 0; p<userAnswerjsonArray.length(); p++)
							{
								JSONObject answuerJsonObj = userAnswerjsonArray.getJSONObject(p);
								
								UserSurveyAnswerDO userSurveyAnswerResponse = new UserSurveyAnswerDO();
								
								userSurveyAnswerResponse.CreatedBy=answuerJsonObj.getString("CreatedBy");
								userSurveyAnswerResponse.CreatedOn=answuerJsonObj.getString("CreatedOn");
								userSurveyAnswerResponse.EmotionName =answuerJsonObj.getString("EmotionName");
								userSurveyAnswerResponse.Ex1=answuerJsonObj.getString("Ex1");
								userSurveyAnswerResponse.FirstName=answuerJsonObj.getString("FirstName");
								userSurveyAnswerResponse.From=answuerJsonObj.getString("From");
								userSurveyAnswerResponse.ImagePath =answuerJsonObj.getString("ImagePath");
								userSurveyAnswerResponse.IsActive=answuerJsonObj.getString("IsActive");
								userSurveyAnswerResponse.LastName=answuerJsonObj.getString("LastName");
								userSurveyAnswerResponse.ModifiedBy=answuerJsonObj.getString("ModifiedBy");
								userSurveyAnswerResponse.ModifiedOn=answuerJsonObj.getString("ModifiedOn");
								userSurveyAnswerResponse.UploadStatus=answuerJsonObj.getString("PNR");
								userSurveyAnswerResponse.Remarks=answuerJsonObj.getString("Remarks");
								userSurveyAnswerResponse.Answer=answuerJsonObj.getString("SurveyAnswer");
								userSurveyAnswerResponse.SurveyId =answuerJsonObj.getString("SurveyId");
								userSurveyAnswerResponse.SurveyOptionId=answuerJsonObj.getString("SurveyOptionId");
								userSurveyAnswerResponse.SurveyQuestionId=answuerJsonObj.getString("SurveyQuestionId");
								userSurveyAnswerResponse.To=answuerJsonObj.getString("To");
								userSurveyAnswerResponse.UserId=answuerJsonObj.getString("UserId");
								userSurveyAnswerResponse.UserSurveyAnswerDetailsDco =answuerJsonObj.getString("UserSurveyAnswerDetailsDco");
								userSurveyAnswerResponse.UserSurveyAnswerId=answuerJsonObj.getString("UserSurveyAnswerId");
								
								
								if(userSurveyAnswerResponse != null)
								{
									questionsDO.vecUserSurveyAnswers.add(userSurveyAnswerResponse);
								}
							}
						
						}
						
						if(questionsDO != null)
						{
							vecQuestionsDO.add(questionsDO);
						}
					}
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
		if(vecQuestionsDO != null && vecQuestionsDO.size() > 0)
			return vecQuestionsDO;
		else
			return null;
	}

	
}
