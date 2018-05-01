package com.winit.alseer.parsers;

import java.io.InputStream;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.winit.alseer.salesman.dataobject.SurveyListDO;
import com.winit.alseer.salesman.utilities.LogUtils;

public class SurveyListHandlerJson extends BaseHandler
{

	boolean currentElement = false;
	StringBuffer sb;
	private Vector<SurveyListDO> vecSurveyListDO;
	private SurveyListDO surveyListDO;

	private int response_code = -1;
	private String response_message = null;

	public SurveyListHandlerJson(InputStream is) 
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
				JSONArray jsonArray = json.getJSONArray("SurveyList");
				
				if(jsonArray != null && jsonArray.length() > 0)
				{
					vecSurveyListDO = new Vector<SurveyListDO>();
					for(int i = 0; i<jsonArray.length(); i++)
					{
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						
						surveyListDO = new SurveyListDO();
						
				    // response
					/*
				      "SurveyId"		: 2,
				      "SurveyTypeCode"	: "CUSTOMER",
				      "SurveyName"		: "Test Survey",
				      "SurveyCode"		: "3D2B80B4-7E04-4043-8D84-551B730032DB",
				      "StartDate"		: "2014-09-27T00:00:00",
				      "EndDate"			: "2016-09-27T00:00:00",
				      "IsConfidential"	: true,
				      "IsActive"		: false,
				      "StatusCode"		: "DRAFT",
				      "CreatedBy"		: 3,
				      "CreatedOn"		: "2014-09-27T20:30:33",
				      "ModifiedBy"		: 3,
				      "ModifiedOn"		: "2014-09-27T20:30:33",
				      "UserSurveyCount"	: 0,
				      "Description"		: "",
				      "SurveyQuestionList": []
				      */
						
						surveyListDO.surveyId 			= jsonObject.getString("SurveyId");
						surveyListDO.surveyTypeCode 	= jsonObject.getString("SurveyTypeCode");
						surveyListDO.surveyName 		= jsonObject.getString("SurveyName");
						surveyListDO.surveyCode 		= jsonObject.getString("SurveyCode");
						surveyListDO.startDate 			= jsonObject.getString("StartDate");
						surveyListDO.endDate 			= jsonObject.getString("EndDate");
						surveyListDO.isConfidential 	= jsonObject.getString("IsConfidential");
						surveyListDO.isActive 			= jsonObject.getString("IsActive");
						surveyListDO.statusCode 		= jsonObject.getString("StatusCode");
						surveyListDO.createdBy 			= jsonObject.getString("CreatedBy");
						surveyListDO.createdOn 			= jsonObject.getString("CreatedOn");
						surveyListDO.modifiedBy 		= jsonObject.getString("ModifiedBy");
						surveyListDO.modifiedOn 		= jsonObject.getString("ModifiedOn");
						surveyListDO.userSurveyCount 	= jsonObject.getString("UserSurveyCount");
						surveyListDO.description 		= jsonObject.getString("Description");
						//vinod -------------
						JSONArray innerJsonArray = jsonObject.getJSONArray("SurveyQuestionList");
						if(innerJsonArray != null && innerJsonArray.length() > 0)
						{
							for(int j = 0; j<innerJsonArray.length(); j++)
							{
								surveyListDO.surveyQuestionList.add(innerJsonArray.getJSONObject(i).getString(""));
								surveyListDO.surveyQuestionList.add(innerJsonArray.getString(j));
							}
						}
						//----------
						
						if(surveyListDO != null)
						{
							vecSurveyListDO.add(surveyListDO);
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
		if(vecSurveyListDO != null && vecSurveyListDO.size() > 0)
			return vecSurveyListDO;
		else
			return null;
	}


}
