package com.winit.alseer.salesman.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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

import android.content.Context;
import android.util.Log;

import com.winit.alseer.parsers.ImageUploadParser;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class UploadImage 
{
	public String uploadImage(Context context, String fileName, String module, boolean isLocal) 
	{
		InputStream is = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			Log.e("ImagePath", "image = " + fileName);
			
			HttpPost httppost = new HttpPost(getImageURL(context)
					+ "/uploadfile/upload.aspx?Module="+module);
			
			File filePath = new File(fileName);

			if (filePath.exists()) {
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
			fileName = parseImageUploadResponse(context, is);
			Log.e("ImagePath", "image = " + fileName);

			return fileName;
		} catch (ClientProtocolException e) 
		{
			e.printStackTrace();
			if(isLocal)
				uploadImage(context, fileName, module, false ) ;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			if(isLocal)
				uploadImage(context, fileName, module, false ) ;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			if(isLocal)
				uploadImage(context, fileName, module, false ) ;
		}
		return "";
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

	private String getImageURL(Context mContext)
	{
		String URL = ServiceURLs.IMAGE_GLOBAL_URL;
		return URL;
	}
}
