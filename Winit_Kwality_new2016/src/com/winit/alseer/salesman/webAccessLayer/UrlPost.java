package com.winit.alseer.salesman.webAccessLayer;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.sfa.salesman.MyApplication;

import static com.winit.alseer.salesman.webAccessLayer.ConnectionHelper.writeIntoLog;

/**
 * class for setting the connection for web services and for getting response as InputStream
 */
public class UrlPost 
{
	// Declaring variables
	private HttpURLConnection connection;
	private InputStream inputStream;
	private GZIPInputStream gZipInputStream;
	private GZIPOutputStream gZipOutStream;
	// Initializing
	public static int TIMEOUT_CONNECT_MILLIS = 150000;
	public static int TIMEOUT_READ_MILLIS 	 = 150000;
//	private static final int TIMEOUT_CONNECT_MILLIS =180000;
//	private static final int TIMEOUT_READ_MILLIS 	= 180000;
	/**
	 * method to soap request
	 * 
	 * @param xmlString
	 * @param url
	 * @param soapUrl
	 * @return
	 * @throws Exception
	 */
	public InputStream soapPost(String xmlString, URL url, String soapUrl)	throws Exception
	{
		try 
		{
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setConnectTimeout(TIMEOUT_CONNECT_MILLIS);
			connection.setReadTimeout(TIMEOUT_READ_MILLIS);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type","text/xml;charset=UTF-8");
			connection.setRequestProperty("SOAPAction", soapUrl);
			connection.getOutputStream();
			PrintWriter writer = new PrintWriter(connection.getOutputStream());
			writer.write(xmlString);
			writer.flush();

			inputStream = connection.getInputStream();
//			int res = connection.getResponseCode();
//			String resMsg = connection.getResponseMessage();
//
//			LogUtils.errorLog("soapPost", soapUrl+" start");
//			if(!soapUrl.contains("Hello"))
//				writeIntoLog("soapPost "+soapUrl+" start "+ CalendarUtils.getCurrentDateAsString()+"\n");
//			System.setProperty("http.keepAlive", "false");
////			startTimer(soapUrl);
//			connection = (HttpURLConnection) url.openConnection();
//			connection.setDoInput(true);
//			connection.setDoOutput(true);
//			connection.setUseCaches(false);
//			connection.setConnectTimeout(TIMEOUT_CONNECT_MILLIS);
//			connection.setReadTimeout(TIMEOUT_READ_MILLIS);
//			connection.setRequestMethod("POST");
//			connection.setRequestProperty("Content-Type","text/xml;charset=UTF-8");
////			connection.setRequestProperty("UserCode",getUserCode(preference));
//			if(!soapUrl.contains("Hello"))
//				connection.setRequestProperty("SOAPAction", soapUrl);
//   				connection.setRequestProperty("Connection","Close");
//			    connection.setRequestProperty("Accept-Encoding","gzip, deflate");
//			gZipOutStream = new GZIPOutputStream(connection.getOutputStream());
//    PrintWriter writer = new PrintWriter(gZipOutStream);
////			PrintWriter writer = new PrintWriter(connection.getOutputStream());
//			writer.write(xmlString);
//			writer.flush();
//			inputStream = connection.getInputStream();
//    gZipInputStream = new GZIPInputStream(inputStream);
//			LogUtils.errorLog("soapPost", soapUrl+" end ");
//			writeIntoLog("soapPost "+soapUrl+" end "+CalendarUtils.getCurrentDateAsString()+"\n");
////			setEOTStatus(preference);

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return inputStream;
	}
	
	/**
	 * method to soap request
	 * 
	 * @param xmlString
	 * @param url
	 * @param soapUrl
	 * @return
	 * @throws Exception
	 */
	public InputStream soapPost(String xmlString, URL url, String soapUrl, Preference preference, String empNo)	throws Exception
	{
		synchronized (MyApplication.Service_Lock)
		{
			try 
			{
				connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.setConnectTimeout(TIMEOUT_CONNECT_MILLIS);
				connection.setReadTimeout(TIMEOUT_READ_MILLIS);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type","text/xml;charset=UTF-8");
				connection.setRequestProperty("EmpNo",empNo);
				connection.setRequestProperty("OrderType","");
//				connection.setRequestProperty("Content-Length", ""	+ xmlString.length());
				connection.setRequestProperty("SOAPAction", soapUrl);
				PrintWriter writer = new PrintWriter(connection.getOutputStream());
				writer.write(xmlString);
				writer.flush();
				inputStream = connection.getInputStream();
				
				String isEOTDone = connection.getHeaderField("IsEOTDone");
				String isAdvanceEOTDone = connection.getHeaderField("IsAdvanceEOTDone");
				if(preference != null){
					if(isEOTDone != null && isEOTDone.equalsIgnoreCase("true"))
					{
						if(!preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
							preference.saveBooleanInPreference(Preference.IS_EOT_DONE, true);
					}
//					if(isAdvanceEOTDone != null && isAdvanceEOTDone.equalsIgnoreCase("true"))
//					{
//						if(!preference.getbooleanFromPreference(Preference.IS_ADV_EOT_DONE, false))
//							preference.saveBooleanInPreference(Preference.IS_ADV_EOT_DONE, true);
//					}
					
					preference.commitPreference();
				}
				
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return inputStream;
		}
	}
}
