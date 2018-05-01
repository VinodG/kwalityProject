package com.winit.alseer.salesman.webAccessLayer;

import java.io.IOException;
import java.io.InputStream;

public class RestClient 
{
	/**
	 * Method to send the request to the server.
	 * @param method
	 * @param parameters
	 * @return InputStream
	 * @throws IOException
	 */
	public InputStream sendRequest(String parameters, String userId) throws IOException
	{
		return new HttpHelper().sendPOSTRequest(ServiceURLs.SERVEY_MAIN_URL,parameters, userId);
	}
	
	public InputStream sendRequestNew(String parameters, String userId) throws IOException
	{
		return new HttpHelper().sendPOSTRequest(ServiceURLs.PostSurveyAnswersURL,parameters, userId);
	}
}
