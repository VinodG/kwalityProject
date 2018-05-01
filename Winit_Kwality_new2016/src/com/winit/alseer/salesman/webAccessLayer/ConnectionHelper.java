package com.winit.alseer.salesman.webAccessLayer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.conn.ConnectTimeoutException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.winit.alseer.parsers.GetAllServey;
import com.winit.alseer.parsers.GetSurveyDetailsByUserIdHandlerJson;
import com.winit.alseer.parsers.GetSurveyListByUserIdBySyncJson;
import com.winit.alseer.parsers.GetUserSurveyAnswerByUserIdHandlerJson;
import com.winit.alseer.parsers.SubmitSurveyHandlerJson;
import com.winit.alseer.parsers.SurveyListHandlerJson;
import com.winit.alseer.parsers.SurveyQuestionsHandlerJson;
import com.winit.alseer.parsers.VersionCheckingHandler;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.NetworkUtility;

/**
 * Class to setting url connection
 */
public class ConnectionHelper
{
	public static final int TIMEOUT_CONNECT_MILLIS 	= 30000;
	public static final int TIMEOUT_READ_MILLIS 	= TIMEOUT_CONNECT_MILLIS - 5000;
	//Initializing Variable
	
	public interface ConnectionExceptionListener
	{
		public void onConnectionException(Object msg);
	}
	public interface ConnectionHelperListener
	{
		public void onResponseRetrieved(String msg);
	}
	
	private String strRequest,methodName;
	private DefaultHandler handler;
	/**
	 * Method to setting url connection
	 * @param strUrl
	 * @param handler
	 */
	
	private ConnectionExceptionListener listener;
	
	public ConnectionHelper(ConnectionExceptionListener listener)
	{
		this.listener = listener;
	}
	
	/**
	 * Method to create session for c2dm notifications
	 * @param RegistrationID
	 * @param deviceId
	 * @param appVersion
	 * @param deviceVersion
	 */
	//this class is used to handle UI request by thread
	private final class UIHandler extends Handler
	{
	    public static final int DISPLAY_UI_DIALOG = 1;
	    public UIHandler(Looper looper)
	    {
	        super(looper);
	    }

	    @Override
	    public void handleMessage(Message msg)
	    {
	        switch(msg.what)
	        {
		        case UIHandler.DISPLAY_UI_DIALOG:
		        {
		        	if(listener != null)
					{
						listener.onConnectionException(msg.obj);
					}
		        }
		        default:
		            break;
	        }
	    }
	}

	public void sendRequest(Context mContext, String strRequest,DefaultHandler handler, String methodName) 
	{
        ServiceURLs.MAIN_URL = getURL(mContext);
		UrlPost objPostforXml = new UrlPost(); 
		UrlPost.TIMEOUT_CONNECT_MILLIS 	= 300000;
		UrlPost.TIMEOUT_READ_MILLIS 	= 300000;
		this.strRequest = strRequest;
		this.methodName = methodName; 
		this.handler 	= handler;
		
		try 
		{
			
			if(methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetails)||methodName.equalsIgnoreCase(ServiceURLs.PostMovementDetailsFromXML)||methodName.equalsIgnoreCase(ServiceURLs.ShipStockMovementsFromXML))
			{
				writeIntoLog("\n--------------------------------------------------------");
				writeIntoLog("\n Posting Time: " + new Date().toString());
				writeIntoLog("\n URL: " + ServiceURLs.MAIN_URL);
				writeIntoLog("\n SoapAction: " + ServiceURLs.SOAPAction+methodName);
				writeIntoLog("\n Request: " + strRequest);
			}
			else
			{
				writeIntoLog("all", methodName, strRequest, "");
			}
			
			if(strRequest.contains("&"))
				strRequest = strRequest.replace("&", "And");
			
			Log.e(methodName+" Request", "strRequest - "+strRequest);
			
			InputStream inputStream = objPostforXml.soapPost(strRequest, new URL(ServiceURLs.MAIN_URL), ServiceURLs.SOAPAction+methodName);
			
			SAXParserFactory spf	=	SAXParserFactory.newInstance();
			SAXParser sp			=	spf.newSAXParser();
			XMLReader xr			=	sp.getXMLReader();
			
			xr.setContentHandler(handler);

			
			//to print the response change the method name here
			if(methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetails))
			{
				if(inputStream != null)
					writeIntoLog("\n\n Response: ", inputStream);
				else
					writeIntoLog("\n\n Response: NULL");
				
				if(handler != null)
					xr.parse(new InputSource(new FileInputStream(AppConstants.DATABASE_PATH+"OrderResponse.xml")));
				
				File tempFile = new File(AppConstants.DATABASE_PATH+"OrderResponse.xml");
				if(tempFile.exists())
					tempFile.delete();
			}
			else
			{
				if(handler != null)
					xr.parse(new InputSource(inputStream));
			}
		} 
		catch (ConnectTimeoutException e)
		{
			
			e.printStackTrace();
				sendRequest(mContext, strRequest,handler, methodName);
		}
		catch (SocketTimeoutException e)
		{
			e.printStackTrace();
			//calling handle ui request method to handle SocketTimeoutException
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public void sendRequest(String strRequest,DefaultHandler handler, String methodName, String empNo, Preference preference) 
	{
		UrlPost objPostforXml = new UrlPost(); 
		UrlPost.TIMEOUT_CONNECT_MILLIS 	= 150000;
		UrlPost.TIMEOUT_READ_MILLIS 	= 150000;
		this.strRequest = strRequest;
		this.methodName = methodName; 
		this.handler 	= handler;
		
		try 
		{
			if(methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetailsFromXMLWithAuth))
			{
				writeIntoLog("\n--------------------------------------------------------");
				writeIntoLog("\n Posting Time: " + new Date().toString());
				writeIntoLog("\n URL: " + ServiceURLs.MAIN_URL);
				writeIntoLog("\n SoapAction: " + ServiceURLs.SOAPAction+methodName);
				writeIntoLog("\n Request: " + strRequest);
			}
			else
			{
				writeIntoLog("all", methodName, strRequest, "");
			}
			InputStream inputStream = objPostforXml.soapPost(strRequest, new URL(ServiceURLs.MAIN_URL), ServiceURLs.SOAPAction+methodName, preference, empNo);
			
			SAXParserFactory spf	=	SAXParserFactory.newInstance();
			SAXParser sp			=	spf.newSAXParser();
			XMLReader xr			=	sp.getXMLReader();
			
			xr.setContentHandler(handler);
			
			//to print the response change the method name here
			if(methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetailsFromXMLWithAuth))
			{
				if(inputStream != null)
					writeIntoLog("\n\n Response: ", inputStream);
				else
					writeIntoLog("\n\n Response: NULL");
				
				if(handler != null)
					xr.parse(new InputSource(new FileInputStream(AppConstants.DATABASE_PATH+"OrderResponse.xml")));
				
				File tempFile = new File(AppConstants.DATABASE_PATH+"OrderResponse.xml");
				if(tempFile.exists())
					tempFile.delete();
			}
			else
			{
				if(handler != null)
					xr.parse(new InputSource(inputStream));
			}
		} 
		catch (ConnectTimeoutException e)
		{
			
			e.printStackTrace();
			//calling handle ui request method to handle ConnectTimeoutException
			handleUIRequest(methodName);
		}
		catch (SocketTimeoutException e)
		{
			e.printStackTrace();
			//calling handle ui request method to handle SocketTimeoutException
			handleUIRequest(methodName);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			handleUIRequest(methodName);
		}
	}
	
	public void sendRequest_Bulk(Context mContext, String strRequest,DefaultHandler handler, String methodName) 
	{
		ServiceURLs.MAIN_URL  = getURL(mContext);
	
		UrlPost objPostforXml 			= new UrlPost();
		UrlPost.TIMEOUT_CONNECT_MILLIS 	= 600000;
		UrlPost.TIMEOUT_READ_MILLIS 	= 600000;
		this.strRequest = strRequest;
		this.methodName = methodName;
		this.handler 	= handler;
		
		try 
		{
			if(methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetails)||methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetailsFromXML))
			{
				writeIntoLog("\n--------------------------------------------------------");
				writeIntoLog("\n Posting Time: " + new Date().toString());
				writeIntoLog("\n URL: " + ServiceURLs.MAIN_URL);
				writeIntoLog("\n SoapAction: " + ServiceURLs.SOAPAction+methodName);
				writeIntoLog("\n Request: " + strRequest);
			}
			else
			{
				writeIntoLog("all", methodName, strRequest, "");
			}
			InputStream inputStream = objPostforXml.soapPost(strRequest, new URL(ServiceURLs.MAIN_URL), ServiceURLs.SOAPAction+methodName);
			
			SAXParserFactory spf	=	SAXParserFactory.newInstance();
			SAXParser sp			=	spf.newSAXParser();
			XMLReader xr			=	sp.getXMLReader();
			
			xr.setContentHandler(handler);
			
			//to print the response change the method name here
			if(methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetails)||methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetailsFromXML))
			{
				if(inputStream != null)
					writeIntoLog("\n\n Response: ", inputStream);
				else
					writeIntoLog("\n\n Response: NULL");
				
				if(handler != null)
					xr.parse(new InputSource(new FileInputStream(AppConstants.DATABASE_PATH+"OrderResponse.xml")));
				
				File tempFile = new File(AppConstants.DATABASE_PATH+"OrderResponse.xml");
				if(tempFile.exists())
					tempFile.delete();
			}
			else
			{
				if(handler != null)
					xr.parse(new InputSource(inputStream));
			}
		} 
		catch (ConnectTimeoutException e)
		{
			e.printStackTrace();
		}
		catch (SocketTimeoutException e)
		{
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	public void sendBlkInsertHHCustomer(String strRequest,DefaultHandler handler, String methodName) 
	{
	
		UrlPost objPostforXml 			= new UrlPost();
		UrlPost.TIMEOUT_CONNECT_MILLIS 	= 600000;
		UrlPost.TIMEOUT_READ_MILLIS 	= 600000;
		this.strRequest = strRequest;
		this.methodName = methodName;
		this.handler 	= handler;
		
		try 
		{
			if(methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetails)||methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetailsFromXML))
			{
				writeIntoLog("\n--------------------------------------------------------");
				writeIntoLog("\n Posting Time: " + new Date().toString());
				writeIntoLog("\n URL: " + ServiceURLs.MAIN_URL);
				writeIntoLog("\n SoapAction: " + ServiceURLs.SOAPAction+methodName);
				writeIntoLog("\n Request: " + strRequest);
			}
			else
			{
				writeIntoLog("all", methodName, strRequest, "");
			}
			
//			if(methodName.equalsIgnoreCase(ServiceURLs.BlkInsertHHCustomer))
//				ServiceURLs.MAIN_URL=ServiceURLs.CUSTOMER_URL_FOR_KM;
//			else
				ServiceURLs.MAIN_URL=ServiceURLs.MAIN_GLOBAL_URL;
			
			InputStream inputStream = objPostforXml.soapPost(strRequest, new URL(ServiceURLs.MAIN_URL), ServiceURLs.SOAPAction+methodName);
			
			SAXParserFactory spf	=	SAXParserFactory.newInstance();
			SAXParser sp			=	spf.newSAXParser();
			XMLReader xr			=	sp.getXMLReader();
			
			xr.setContentHandler(handler);
			
			//to print the response change the method name here
			if(methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetails)||methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetailsFromXML))
			{
				if(inputStream != null)
					writeIntoLog("\n\n Response: ", inputStream);
				else
					writeIntoLog("\n\n Response: NULL");
				
				if(handler != null)
					xr.parse(new InputSource(new FileInputStream(AppConstants.DATABASE_PATH+"OrderResponse.xml")));
				
				File tempFile = new File(AppConstants.DATABASE_PATH+"OrderResponse.xml");
				if(tempFile.exists())
					tempFile.delete();
			}
			else
			{
				if(handler != null)
					xr.parse(new InputSource(inputStream));
			}
		} 
		catch (ConnectTimeoutException e)
		{
			e.printStackTrace();
		}
		catch (SocketTimeoutException e)
		{
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * method to handle the single line response
	 * @param strRequest
	 * @param handler
	 * @param methodName
	 * 
	 */
	
	public boolean sendRequest(Context mContext, String strRequest, String methodName) 
	{
		ServiceURLs.MAIN_URL  = getURL(mContext);
		UrlPost objPostforXml = new UrlPost();
		UrlPost.TIMEOUT_CONNECT_MILLIS 	= 150000;
		UrlPost.TIMEOUT_READ_MILLIS 	= 150000;
		try 
		{
			InputStream inputStream = objPostforXml.soapPost(strRequest, new URL(ServiceURLs.MAIN_URL), ServiceURLs.SOAPAction+methodName);
			
			if(methodName.equalsIgnoreCase("PostStoreCheckAndItemDetails"))
			writeIntoLog("all", methodName, strRequest, "");
			if(inputStream != null)
			{
				//to taking one line
			  	BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
			  	String success = "";
			  	success = r.readLine();
			  	LogUtils.errorLog("success - ", ""+success);
				
			  	writeIntoLog("all", methodName, strRequest, success);
			  	
			  	if(success.contains("<Status>Success</Status>"))
			  	{
			  		return true;
			  	}
			  	else
			  	{
			  		writeIntoLog("error", methodName, strRequest, success);
			  		return false;
			  	}
			}
		} 
		catch (ConnectTimeoutException e)
		{
			//calling handle ui request method to handle ConnectTimeoutException
			e.printStackTrace();
		}
		catch (SocketTimeoutException e)
		{
			//calling handle ui request method to handle SocketTimeoutException
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * method to handle the single line response
	 * @param strRequest
	 * @param handler
	 * @param methodName
	 */
	public boolean sendRequest_Bulk(Context mContext, String strRequest, String methodName) 
	{
		ServiceURLs.MAIN_URL  = getURL(mContext);
		UrlPost objPostforXml = new UrlPost();
		UrlPost.TIMEOUT_CONNECT_MILLIS 	= 600000;
		UrlPost.TIMEOUT_READ_MILLIS 	= 600000;
		try 
		{
			InputStream inputStream = objPostforXml.soapPost(strRequest, new URL(ServiceURLs.MAIN_URL), ServiceURLs.SOAPAction+methodName);
			
			if(
					methodName.equalsIgnoreCase(ServiceURLs.PostPaymentDetailsFromXML)
					||methodName.equalsIgnoreCase(ServiceURLs.INSERT_EOT)
					||methodName.equalsIgnoreCase(ServiceURLs.UPDATE_PASSCODE_STATUS))
			{
				//to taking one line
			  	BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
			  	String success = "";
			  	success = r.readLine();
			  	LogUtils.errorLog("success - ", ""+success);
				
			  	writeIntoLog("all", methodName, strRequest, success);
			  	
			  	if(success.contains("<Status>Success</Status>"))
			  	{
			  		return true;
			  	}
			  	else
			  	{
			  		writeIntoLog("error", methodName, strRequest, success);
			  		return false;
			  	}
			}
		} 
		catch (ConnectTimeoutException e)
		{
			//calling handle ui request method to handle ConnectTimeoutException
			e.printStackTrace();
		}
		catch (SocketTimeoutException e)
		{
			//calling handle ui request method to handle SocketTimeoutException
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return false;
	}
	//to handle UI request
	 public void handleUIRequest(String message)
	 {
		 try
		 {
			 Thread uiThread = new HandlerThread("UIHandler");
			 uiThread.start();
			 UIHandler uiHandler = new UIHandler(((HandlerThread) uiThread).getLooper());
	
			 Message msg = uiHandler.obtainMessage(UIHandler.DISPLAY_UI_DIALOG);
			 msg.obj = message;
			 uiHandler.sendMessage(msg);
		 }
		 catch (Exception e) 
		 {
			e.printStackTrace();
		 }
	 }
	 
	public static void writeIntoLog(String str, InputStream is) throws IOException
	{
		try
		{
			 BufferedInputStream bis = new BufferedInputStream(is);
			 FileOutputStream fosOrderFile = new FileOutputStream(AppConstants.DATABASE_PATH+"OrderResponse.xml");
			 BufferedOutputStream bossOrderFile = new BufferedOutputStream(fosOrderFile);
			 deleteLogFile(Environment.getExternalStorageDirectory().toString()+"/OrderLog.txt");
			 FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/OrderLog.txt", true);
			 BufferedOutputStream bos = new BufferedOutputStream(fos);
			 
			 bos.write(str.getBytes());
			 
			 byte byt[] = new byte[1024];
			 int noBytes;
			 
			 while((noBytes = bis.read(byt)) != -1)
			 {	 
				 bossOrderFile.write(byt,0,noBytes);
				 bos.write(byt,0,noBytes);
			 }
			 
			 bossOrderFile.flush();
			 bossOrderFile.close();
			 fosOrderFile.close();
			 bos.flush();
			 bos.close();
			 fos.close();
			 bis.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	 }
	
	public static void writeIntoLog(String str) throws IOException
	{
		try
		{
			deleteLogFile(Environment.getExternalStorageDirectory().toString()+"/OrderLog.txt");
			FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/OrderLog.txt", true);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(str.getBytes());
			
			bos.flush();
			bos.close();
			fos.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	 }
	
	public static void writeIntoLogForDeliveryAgent(String str) throws IOException
	{
		try
		{
			deleteLogFile(Environment.getExternalStorageDirectory().toString()+"/DeliveryLog.txt");
			FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/DeliveryLog.txt", true);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(str.getBytes());
			 
			bos.flush();
			bos.close();
			fos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	 }
	
	public static void writeErrorLogForDeliveryAgent(String str) throws IOException
	{
		try
		{
			 FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/DeliveryErrorLog.txt", true);
			 BufferedOutputStream bos = new BufferedOutputStream(fos);
			 bos.write(str.getBytes());
			 
			 bos.flush();
			 bos.close();
			 fos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	 }
	 
	 /**
	 * This method stores InputStream data into a file at specified location
	 * @param is
	 */
	public static void convertResponseToFile(InputStream is, String method) throws IOException
	{
		try
		{
			 BufferedInputStream bis 	= 	new BufferedInputStream(is);
			 FileOutputStream fos 		= 	new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/"+method+"Response.xml");
			 BufferedOutputStream bos 	= 	new BufferedOutputStream(fos);
			 
			 byte byt[] = new byte[1024];
			 int noBytes;
			 
			 while((noBytes = bis.read(byt)) != -1)
				 bos.write(byt,0,noBytes);
			 
			 bos.flush();
			 bos.close();
			 fos.close();
			 bis.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	 }
	
	private void writeIntoLog(String type,String methodName, String strRequest, String success)
	{
		try 
		{
			if(type.equalsIgnoreCase("all"))
			{
				writeIntoLogForDeliveryAgent("\n--------------------------------------------------------");
				writeIntoLogForDeliveryAgent("\n Posting Time: " + new Date().toString());
				writeIntoLogForDeliveryAgent("\n URL: " + ServiceURLs.MAIN_URL);
				writeIntoLogForDeliveryAgent("\n SoapAction: " + ServiceURLs.SOAPAction+methodName);
				writeIntoLogForDeliveryAgent("\n--------------------------------------------------------");
				writeIntoLogForDeliveryAgent("\n Request: " + strRequest);
				writeIntoLogForDeliveryAgent("\n Response: " + success);
			}
			else
			{
				writeErrorLogForDeliveryAgent("\n--------------------------------------------------------");
		  		writeErrorLogForDeliveryAgent("\n Posting Time: " + new Date().toString());
		  		writeErrorLogForDeliveryAgent("\n URL: " + ServiceURLs.MAIN_URL);
		  		writeErrorLogForDeliveryAgent("\n SoapAction: " + ServiceURLs.SOAPAction+methodName);
		  		writeErrorLogForDeliveryAgent("\n--------------------------------------------------------");
		  		writeErrorLogForDeliveryAgent("\n Request: " + strRequest);
		  		writeErrorLogForDeliveryAgent("\n Response: " + success);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public static void deleteLogFile(String path)
	{
		try
		{
			File file = new File(path);
			if(file.exists())
			{
				long sizeInMB = file.length()/1048576;
				if(sizeInMB == 5)
					file.delete();
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private String getURL(Context mContext)
	{   
		 String URL = ServiceURLs.MAIN_GLOBAL_URL;
		 return URL;
	}
	
	private String getSurveyURL(Context mContext,String methodName)
	{   
		String URL=null;
		if(methodName.equalsIgnoreCase(ServiceURLs.GET_ALL_SURVEY))
		{
			URL = ServiceURLs.SURVEY_HOST_URL+methodName;
		}
		else if(methodName.equalsIgnoreCase(ServiceURLs.GET_ALL_SURVEY_QUESTIONS))
		{
			URL = ServiceURLs.SURVEY_HOST_URL+methodName;
		}
		else if(methodName.equalsIgnoreCase(ServiceURLs.GET_ADD_SURVEY_ANSWER))
		{
			URL = ServiceURLs.SURVEY_HOST_URL+methodName;
		}
		else if(methodName.equalsIgnoreCase(ServiceURLs.GET_SURVEY_DETAILS_BY_USER_ID))
		{
			URL = ServiceURLs.SURVEY_HOST_URL+methodName;
		}
		else if(methodName.equalsIgnoreCase(ServiceURLs.GET_USER_SURVEY_ANSWER_BY_USER_ID))
		{
			URL = ServiceURLs.SURVEY_HOST_URL+methodName;
		}
		else if(methodName.equalsIgnoreCase(ServiceURLs.GET_SURVEY_LIST_BY_USER_ID_BY_SYNC))
		{
			URL = ServiceURLs.SURVEY_HOST_URL+methodName;
		}
		
		return URL;
	}
	
	public boolean startDataDownloadNew(String parameters, Context mContext, String userId, GetAllServey getAllServey)
	{
		if(NetworkUtility.isNetworkConnectionAvailable(mContext))
		{
			InputStream isResponse = null;
			
			try 
			{
				isResponse = new RestClient().sendRequestNew(parameters, userId);
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
	// 29/10/2014 7.40 pm By Dalayya For Only SurveyModule
	
	public Object sendRequestForSurveyModule(Context mContext, String strRequest,String methodName) 
	{
		Object result=null;
		InputStream inputStream=null;
        ServiceURLs.MAIN_URL = getSurveyURL(mContext,methodName);
        
    	if(methodName.equalsIgnoreCase(ServiceURLs.GET_ALL_SURVEY) ||
    	    methodName.equalsIgnoreCase(ServiceURLs.GET_ALL_SURVEY_QUESTIONS) ||
    	    methodName.equalsIgnoreCase(ServiceURLs.GET_SURVEY_DETAILS_BY_USER_ID) ||
    	    methodName.equalsIgnoreCase(ServiceURLs.GET_SURVEY_LIST_BY_USER_ID_BY_SYNC) ||
    	    methodName.equalsIgnoreCase(ServiceURLs.GET_USER_SURVEY_ANSWER_BY_USER_ID))
		{
    		ServiceURLs.MAIN_URL += strRequest;
		}
    	
        
		UrlPost.TIMEOUT_CONNECT_MILLIS 	= 150000;
		UrlPost.TIMEOUT_READ_MILLIS 	= 150000;
		this.strRequest = strRequest;
		this.methodName = methodName; 
		
		try 
		{
			if(methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetails))
			{
				writeIntoLog("\n--------------------------------------------------------");
				writeIntoLog("\n Posting Time: " + new Date().toString());
				writeIntoLog("\n URL: " + ServiceURLs.MAIN_URL);
				writeIntoLog("\n SoapAction: " + ServiceURLs.SOAPAction+methodName);
				writeIntoLog("\n Request: " + strRequest);
			}
			else
			{
				writeIntoLog("all", methodName, strRequest, "");
			}
			
			Log.e(methodName+" Request", "strRequest - "+strRequest);
			
			
			//For Getting InputStream 
			if(methodName.equalsIgnoreCase(ServiceURLs.GET_ADD_SURVEY_ANSWER))
			{
				inputStream = new HttpHelper().sendPOSTRequestForSurvey(ServiceURLs.MAIN_URL, strRequest);
			}
			else
				inputStream = new HttpHelper().sendRequestForSurvey(ServiceURLs.MAIN_URL, null);
			
			//For Handling response with handlers
			if(methodName.equalsIgnoreCase(ServiceURLs.GET_ALL_SURVEY))
			{
				SurveyListHandlerJson surveyList = new SurveyListHandlerJson(inputStream);
				result = surveyList.getData();
			}
			else if(methodName.equalsIgnoreCase(ServiceURLs.GET_ALL_SURVEY_QUESTIONS))
			{
				SurveyQuestionsHandlerJson surveyQuestions = new SurveyQuestionsHandlerJson(inputStream);
				result = surveyQuestions.getData();
			}
			else if(methodName.equalsIgnoreCase(ServiceURLs.GET_ADD_SURVEY_ANSWER))
			{
				SubmitSurveyHandlerJson submitSurveyHandlerJson = new SubmitSurveyHandlerJson(inputStream);
				result = submitSurveyHandlerJson.getData();
			}
			else if(methodName.equalsIgnoreCase(ServiceURLs.GET_SURVEY_DETAILS_BY_USER_ID))
			{
				GetSurveyDetailsByUserIdHandlerJson getSurveyDetailsByUserIdHandlerJson = new GetSurveyDetailsByUserIdHandlerJson(inputStream);
				result = getSurveyDetailsByUserIdHandlerJson.getData();
			}
			else if(methodName.equalsIgnoreCase(ServiceURLs.GET_USER_SURVEY_ANSWER_BY_USER_ID))
			{
				GetUserSurveyAnswerByUserIdHandlerJson getUserSurveyAnswerByUserIdHandlerJson = new GetUserSurveyAnswerByUserIdHandlerJson(inputStream);
				result = getUserSurveyAnswerByUserIdHandlerJson.getData();
			}
			else if(methodName.equalsIgnoreCase(ServiceURLs.GET_SURVEY_LIST_BY_USER_ID_BY_SYNC))
			{
				GetSurveyListByUserIdBySyncJson GetSurveyListByUserIdBySyncJson = new GetSurveyListByUserIdBySyncJson(inputStream,mContext);
				result = GetSurveyListByUserIdBySyncJson.getData();
			}
		
		} 
		catch (ConnectTimeoutException e)
		{
			
			e.printStackTrace();
				sendRequest(mContext, strRequest,handler, methodName);
		}
		catch (SocketTimeoutException e)
		{
			e.printStackTrace();
			//calling handle ui request method to handle SocketTimeoutException
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return result;
	}
	
	public Object sendRequest_VersionManagement(Context mContext, String strRequest,VersionCheckingHandler handler, String methodName) 
	{
		Object result=null;
        ServiceURLs.MAIN_URL = "http://10.20.53.18/Kwality/Services.asmx";
		UrlPost objPostforXml = new UrlPost(); 
		UrlPost.TIMEOUT_CONNECT_MILLIS 	= 150000;
		UrlPost.TIMEOUT_READ_MILLIS 	= 150000;
		this.strRequest = strRequest;
		this.methodName = methodName; 
		this.handler 	= handler;
		
		try 
		{
			if(methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetails))
			{
				writeIntoLog("\n--------------------------------------------------------");
				writeIntoLog("\n Posting Time: " + new Date().toString());
				writeIntoLog("\n URL: " + ServiceURLs.MAIN_URL);
				writeIntoLog("\n SoapAction: " + ServiceURLs.SOAPAction+methodName);
				writeIntoLog("\n Request: " + strRequest);
			}
			else
			{
				writeIntoLog("all", methodName, strRequest, "");
			}
			
			if(strRequest.contains("&"))
				strRequest = strRequest.replace("&", "And");
			
			Log.e(methodName+" Request", "strRequest - "+strRequest);
			
			InputStream inputStream = objPostforXml.soapPost(strRequest, new URL(ServiceURLs.MAIN_URL), ServiceURLs.SOAPAction+methodName);
			
			SAXParserFactory spf	=	SAXParserFactory.newInstance();
			SAXParser sp			=	spf.newSAXParser();
			XMLReader xr			=	sp.getXMLReader();
			
			xr.setContentHandler(handler);
			
			//to print the response change the method name here
			if(methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetails))
			{
				if(inputStream != null)
					writeIntoLog("\n\n Response: ", inputStream);
				else
					writeIntoLog("\n\n Response: NULL");
				
				if(handler != null)
					xr.parse(new InputSource(new FileInputStream(AppConstants.DATABASE_PATH+"OrderResponse.xml")));
				
				File tempFile = new File(AppConstants.DATABASE_PATH+"OrderResponse.xml");
				if(tempFile.exists())
					tempFile.delete();
			}
			else
			{
				if(handler != null)
					xr.parse(new InputSource(inputStream));
			}
			
			result = handler.getData();
		} 
		catch (ConnectTimeoutException e)
		{
			
			e.printStackTrace();
				sendRequest(mContext, strRequest,handler, methodName);
		}
		catch (SocketTimeoutException e)
		{
			e.printStackTrace();
			//calling handle ui request method to handle SocketTimeoutException
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return result;
	}
	
	public static void writeIntoLog(String str, String filename) throws IOException
	{
		try
		{
			deleteLogFile(Environment.getExternalStorageDirectory().toString()+"/"+filename+".xml");
			FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/"+filename+".xml", true);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(str.getBytes());
			
			bos.flush();
			bos.close();
			fos.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	 }
	
	public boolean checkClearDataPermission(Context mContext, String strRequest,DefaultHandler handler, String methodName) 
	{
	
		UrlPost objPostforXml 			= new UrlPost();
		UrlPost.TIMEOUT_CONNECT_MILLIS 	= 600000;
		UrlPost.TIMEOUT_READ_MILLIS 	= 600000;
		this.strRequest = strRequest;
		this.methodName = methodName;
		this.handler 	= handler;
		
		try 
		{
			InputStream inputStream = objPostforXml.soapPost(strRequest, new URL(ServiceURLs.CHECK_CLEAR_DATA_PERMISSION), ServiceURLs.SOAPAction+methodName);
			SAXParserFactory spf	=	SAXParserFactory.newInstance();
			SAXParser sp			=	spf.newSAXParser();
			XMLReader xr			=	sp.getXMLReader();
			xr.setContentHandler(handler);
			if(inputStream != null)
			{
				//to taking one line
			  	BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
			  	String success = "";
			  	success = r.readLine();
			  	LogUtils.errorLog("success - ", ""+success);
				
			  	writeIntoLog("all", methodName, strRequest, success);
			  	
			  	if(success.contains("<boolean>true</boolean>")||success.contains("<GetClearDataPermissionResult>true</GetClearDataPermissionResult>"))
			  	{
			  		return true;
			  	}
			  	else
			  	{
			  		writeIntoLog("error", methodName, strRequest, success);
			  		return false;
			  	}
			}
		} 
		catch (ConnectTimeoutException e)
		{
			e.printStackTrace();
		}
		catch (SocketTimeoutException e)
		{
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return false;
	}
}
