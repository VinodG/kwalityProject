package com.winit.alseer.salesman.common;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
	InputStream is = null;
	Context mcontext;
	String file_name;
	String module;
	boolean isLocal;
	String mimeType;

	public String uploadImage(Context context, String fileName, String module,
			boolean isLocal,String mimeType) {
		this.mcontext = context;
		this.file_name = fileName;
		this.module = module;
		this.isLocal = isLocal;
		this.mimeType = mimeType;

		try {
			final HttpClient httpclient = new DefaultHttpClient();
			Log.e("ImagePath", "image = " + fileName);
			// HttpPost httppost = new HttpPost(ServiceURLs.IMAGE_MAIN_URL
			// + "UploadImage.aspx?Module="+module);

			final HttpPost httppost = new HttpPost(getImageURL(context)
					+ "?Module=" + module);

			// long boundry = System.currentTimeMillis();
			// httppost.addHeader("Content-Type",
			// "multipart/form-data; boundary="
			// + boundry);
			File filePath = new File(fileName);

			if (filePath.exists())
			{
				Log.e("uplaod", "called");
				MultipartEntity mpEntity = new MultipartEntity();
				ContentBody cbFile = new FileBody(filePath, mimeType);
				mpEntity.addPart("FileName", cbFile);
				httppost.setEntity(mpEntity);

//				Thread thread = new Thread(new Runnable() {
//					@Override
//					public void run() {
						try 
						{
							HttpResponse response;
							response = httpclient.execute(httppost);
							HttpEntity resEntity = response.getEntity();
							is = resEntity.getContent();

							file_name = parseImageUploadResponse(mcontext, is);
							Log.e("ImagePath", "image = " + file_name);
						} catch (Exception e) {
							e.printStackTrace();
							runOneMoreTime();
						}
//					}
//				});
//
//				thread.start();

			}

			return file_name;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			if (isLocal)
				uploadImage(context, fileName, module, false,mimeType);
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

	private String getImageURL(Context mContext) {
		String URL = ServiceURLs.IMAGE_GLOBAL_URL;
		return URL;
	}

	private void runOneMoreTime() {
		if (isLocal)
			uploadImage(mcontext, file_name, module, false,mimeType);
	}
}
