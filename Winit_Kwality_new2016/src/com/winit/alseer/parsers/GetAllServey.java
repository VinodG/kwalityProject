package com.winit.alseer.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.winit.alseer.salesman.dataobject.ServeyListDO;
import com.winit.alseer.salesman.dataobject.ServeyOptionsDO;
import com.winit.alseer.salesman.dataobject.ServeyQuestionsDO;

public class GetAllServey 
{
	private int status = -1;
	private String message;
	
	private ArrayList<ServeyListDO> vecServeyListDOs;
	private ServeyListDO serveyListDO;
	private ServeyQuestionsDO serveyQuestionsDO;
	private ServeyOptionsDO serveyOptionsDO;
	
	
	public void setInputStream(InputStream inputStream)
	{
		String strResponse = getStringFromInputStream(inputStream);
		try 
		{
			JSONObject jsonObject = new JSONObject(strResponse);
			if(jsonObject.has("SurveyList"))
			{
				vecServeyListDOs = new ArrayList<ServeyListDO>();
				JSONArray jsonArrayGroups = jsonObject.getJSONArray("SurveyList");
				for (int i = 0; i < jsonArrayGroups.length(); i++) 
				{
					JSONObject jsonObject2 = jsonArrayGroups.getJSONObject(i);
					serveyListDO = new ServeyListDO();
					
					serveyListDO.SurveyId = jsonObject2.getString("SurveyId");
					serveyListDO.SurveyTitle = jsonObject2.getString("SurveyTitle");
					serveyListDO.Guidelines = jsonObject2.getString("Guidelines");
					serveyListDO.AssignedCount = jsonObject2.getString("AssignedCount");
					serveyListDO.ImageUrl = jsonObject2.getString("ImageUrl");
					serveyListDO.VideoUrl = jsonObject2.getString("VideoUrl");
					serveyListDO.UserAnswerCount = jsonObject2.getString("UserAnswerCount");
					
					if(jsonObject2.has("SurveyQuestions"))
					{
						JSONArray jsonArrayGroups2 = jsonObject2.getJSONArray("SurveyQuestions");
						serveyListDO.vecQuestionsDOs = new ArrayList<ServeyQuestionsDO>();
						for (int j = 0; j < jsonArrayGroups2.length(); j++) 
						{
							JSONObject jsonObject3 = jsonArrayGroups2.getJSONObject(j);
							serveyQuestionsDO = new ServeyQuestionsDO();
							
							serveyQuestionsDO.SurveyQuestionId = jsonObject3.getString("SurveyQuestionId");
							serveyQuestionsDO.SurveyId = jsonObject3.getString("SurveyId");
							serveyQuestionsDO.Question = jsonObject3.getString("Question");
							serveyQuestionsDO.AnswerType = jsonObject3.getString("AnswerType");
							
							if(jsonObject3.has("SurveyOptions"))
							{
								JSONArray jsonArrayGroups3 = jsonObject3.getJSONArray("SurveyOptions");
								serveyQuestionsDO.vecOptionsDOs = new ArrayList<ServeyOptionsDO>();
								for (int k = 0; k < jsonArrayGroups3.length(); k++) 
								{
									JSONObject jsonObject4 = jsonArrayGroups3.getJSONObject(k);
									serveyOptionsDO = new ServeyOptionsDO();
									
									serveyOptionsDO.SurveyOptionId = jsonObject4.getString("SurveyOptionId");
									serveyOptionsDO.SurveyQuestionId = jsonObject4.getString("SurveyQuestionId");
									serveyOptionsDO.SurveyOption = jsonObject4.getString("SurveyOption");
									serveyQuestionsDO.vecOptionsDOs.add(serveyOptionsDO);
								}
							}
							
							serveyListDO.vecQuestionsDOs.add(serveyQuestionsDO);
						}
					}
//					if(jsonObject2.has("Feelings"))
//					{
//						JSONArray jsonArrayGroups3 = jsonObject2.getJSONArray("Feelings");
//						AppConstants.vecFeelingsDOs = new ArrayList<FeelingsDO>();
//						for (int j = 0; j < jsonArrayGroups3.length(); j++) 
//						{
//							JSONObject jsonObject4 = jsonArrayGroups3.getJSONObject(j);
//							FeelingsDO feelingsDO = new FeelingsDO();
//							
//							feelingsDO.FeelingId = jsonObject4.getString("FeelingId");
//							feelingsDO.Name = jsonObject4.getString("Name");
//							feelingsDO.ImageUrl = jsonObject4.getString("ImageUrl");
//							feelingsDO.ImageHoverUrl = jsonObject4.getString("ImageHoverUrl");
//							AppConstants.vecFeelingsDOs.add(feelingsDO);
//						}
//					}
					if(!serveyListDO.SurveyId.equalsIgnoreCase("101"))
						vecServeyListDOs.add(serveyListDO);
				}
			}
		} catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList<ServeyListDO> getData()
	{
		return vecServeyListDOs;
	}
	protected String getStringFromInputStream(InputStream inputStream)
	{
		String str = "";
		if(inputStream != null)
		{
			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();

			String line;
			try {

				br = new BufferedReader(new InputStreamReader(inputStream));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			str = sb.toString();
		}
		return str;
	}
}
