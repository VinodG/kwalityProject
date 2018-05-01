package com.winit.alseer.parsers;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.winit.alseer.salesman.dataobject.SubmitReviewDO;
import com.winit.alseer.salesman.utilities.LogUtils;

public class SubmitSurveyHandlerJson extends BaseHandler
{
	boolean currentElement = false;
	StringBuffer sb;
	private SubmitReviewDO submitReview;
	private int response_code = -1;
	private String response_message = null;


	public SubmitSurveyHandlerJson(InputStream is)
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

				submitReview = new SubmitReviewDO();
				String  userSurveyAnswerId = json.getString("UserSurveyAnswerId");
				submitReview.Message = response_message;
				submitReview.UserSurveyAnswerId = userSurveyAnswerId;
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
		if(submitReview != null )
			return submitReview;
		else
			return null;
	}
	public Object getErrorData() 
	{
		return null;
	}
}
