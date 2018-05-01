package com.winit.alseer.parsers;

import java.io.InputStream;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.winit.alseer.salesman.dataobject.UserSurveyAnswerDO;
import com.winit.alseer.salesman.utilities.LogUtils;

public class GetUserSurveyAnswerByUserIdHandlerJson extends BaseHandler
{
	boolean currentElement = false;
	StringBuffer sb;
	private Vector<UserSurveyAnswerDO> vecUserSurveyAnswerResponse;
	private UserSurveyAnswerDO userSurveyAnswerResponse;

	private int response_code = -1;
	private String response_message = null;


	public GetUserSurveyAnswerByUserIdHandlerJson(InputStream is)
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
				JSONArray jsonArray = json.getJSONArray("UserSurveyAnswerList");
				
				if(jsonArray != null && jsonArray.length() > 0)
				{
					vecUserSurveyAnswerResponse = new Vector<UserSurveyAnswerDO>();
					for(int i = 0; i<jsonArray.length(); i++)
					{
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						
						userSurveyAnswerResponse = new UserSurveyAnswerDO();
						
						userSurveyAnswerResponse.CreatedBy=jsonObject.getString("CreatedBy");
						userSurveyAnswerResponse.CreatedOn=jsonObject.getString("CreatedOn");
						userSurveyAnswerResponse.EmotionName =jsonObject.getString("EmotionName");
						userSurveyAnswerResponse.Ex1=jsonObject.getString("Ex1");
						userSurveyAnswerResponse.FirstName=jsonObject.getString("FirstName");
						userSurveyAnswerResponse.From=jsonObject.getString("From");
						userSurveyAnswerResponse.ImagePath =jsonObject.getString("ImagePath");
						userSurveyAnswerResponse.IsActive=jsonObject.getString("IsActive");
						userSurveyAnswerResponse.LastName=jsonObject.getString("LastName");
						userSurveyAnswerResponse.ModifiedBy=jsonObject.getString("ModifiedBy");
						userSurveyAnswerResponse.ModifiedOn=jsonObject.getString("ModifiedOn");
						userSurveyAnswerResponse.UploadStatus=jsonObject.getString("PNR");
						userSurveyAnswerResponse.Remarks=jsonObject.getString("Remarks");
						userSurveyAnswerResponse.Answer=jsonObject.getString("SurveyAnswer");
						userSurveyAnswerResponse.SurveyId =jsonObject.getString("SurveyId");
						userSurveyAnswerResponse.SurveyOptionId=jsonObject.getString("SurveyOptionId");
						userSurveyAnswerResponse.SurveyQuestionId=jsonObject.getString("SurveyQuestionId");
						userSurveyAnswerResponse.To=jsonObject.getString("To");
						userSurveyAnswerResponse.UserId=jsonObject.getString("UserId");
						userSurveyAnswerResponse.UserSurveyAnswerDetailsDco =jsonObject.getString("UserSurveyAnswerDetailsDco");
						userSurveyAnswerResponse.UserSurveyAnswerId=jsonObject.getString("UserSurveyAnswerId");
						
						
						if(userSurveyAnswerResponse != null)
						{
							vecUserSurveyAnswerResponse.add(userSurveyAnswerResponse);
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
		if(vecUserSurveyAnswerResponse != null && vecUserSurveyAnswerResponse.size() > 0)
			return vecUserSurveyAnswerResponse;
		else
			return null;
	}

	public Object getErrorData() 
	{
		return null;
	}
	
}
