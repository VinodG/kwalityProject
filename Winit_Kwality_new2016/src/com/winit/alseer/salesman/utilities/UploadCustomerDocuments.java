package com.winit.alseer.salesman.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.winit.alseer.parsers.AddNewCustomerParser;
import com.winit.alseer.parsers.ImageUploadParser;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDocumentDL;
import com.winit.alseer.salesman.dataobject.AddCustomerFilesDO;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class UploadCustomerDocuments extends IntentService 
{
	Vector<AddCustomerFilesDO> arrAddCustomerFilesDO = new Vector<AddCustomerFilesDO>();
	ConnectionHelper connectionHelper 		= new ConnectionHelper(null);
	public UploadCustomerDocuments() 
	{
		super("UploadCustomerDocuments");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) 
	{
		arrAddCustomerFilesDO = new CustomerDocumentDL().getAllLocalDocuments("0");
		for (AddCustomerFilesDO addCustomerFilesDO : arrAddCustomerFilesDO) 
		{
			boolean isError=false;
			InputStream is = null;
			try 
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(String.format(
						ServiceURLs.IMAGE_GLOBAL_URL_FOR_SURVEY,AppConstants.Customer));
				File filePath = new File(addCustomerFilesDO.ServerFilePath);

				if (filePath.exists()) 
				{
					Log.e("uplaod", "called");
					MultipartEntity mpEntity = new MultipartEntity();
					ContentBody cbFile = new FileBody(filePath, "image/png");
					mpEntity.addPart("FileName", cbFile);
					httppost.setEntity(mpEntity);

					HttpResponse response;
					response = httpclient.execute(httppost);
					HttpEntity resEntity = response.getEntity();
					is = resEntity.getContent();

				}
				String serverUrl = parseImageUploadResponse(UploadCustomerDocuments.this, is);
				if(serverUrl!=null && !serverUrl.equalsIgnoreCase(""))
				{
					serverUrl = serverUrl.replace("http://208.109.154.54/krsurveyservice", "..");
					addCustomerFilesDO.ServerFilePath = serverUrl;
				}
				
			} 
			catch (ClientProtocolException e) {
				e.printStackTrace();
				isError=true;
			} catch (IOException e) {
				e.printStackTrace();
				isError=true;
			} catch (Exception e) {
				e.printStackTrace();
				isError=true;
			}
				
		}
		if(arrAddCustomerFilesDO!=null && arrAddCustomerFilesDO.size()>0)
		{
			AddNewCustomerParser addNewCustomerParser =new AddNewCustomerParser(null);
			connectionHelper.sendBlkInsertHHCustomer(BuildXMLRequest.insertBlkInsertCustomerDocument(arrAddCustomerFilesDO),addNewCustomerParser, ServiceURLs.BlkInsertCustomerDocument);
			if(addNewCustomerParser!=null)
			{
				if(addNewCustomerParser.getStatus())
				{
					for (AddCustomerFilesDO addCustomerFilesDO : arrAddCustomerFilesDO) 
					{
						addCustomerFilesDO.Status="1";
					}
					new CustomerDocumentDL().upadateCustomerDocuments(arrAddCustomerFilesDO);
				}
			}
		}
		
	}
	public static String parseImageUploadResponse(Context context,
			InputStream inputStream) {
		try {
			SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
			XMLReader xr = sp.getXMLReader();
			ImageUploadParser handler = new ImageUploadParser(context);
			xr.setContentHandler(handler);
			xr.parse(new InputSource(inputStream));
			return handler.getUploadedFileName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
