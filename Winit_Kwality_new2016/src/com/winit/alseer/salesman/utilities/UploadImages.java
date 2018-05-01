package com.winit.alseer.salesman.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

import com.winit.alseer.parsers.ImageUploadParser;
import com.winit.alseer.parsers.ServiceCaptureParser;
import com.winit.alseer.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.alseer.salesman.dataaccesslayer.ReturnOrderDA;
import com.winit.alseer.salesman.dataaccesslayer.StoreCheckDA;
import com.winit.alseer.salesman.dataobject.DamageImageDO;
import com.winit.alseer.salesman.dataobject.OrderPrintImageDO;
import com.winit.alseer.salesman.dataobject.PostPaymentDetailDONew;
import com.winit.alseer.salesman.dataobject.ServiceCaptureDO;
import com.winit.alseer.salesman.utilities.UploadTransactions.TransactionSatus;
import com.winit.alseer.salesman.utilities.UploadTransactions.Transactions;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class UploadImages extends IntentService 
{
	ArrayList<DamageImageDO> arrAllDamagedImages = new ArrayList<DamageImageDO>();
	ArrayList<OrderPrintImageDO> arrOrderPrintImageDO= new ArrayList<OrderPrintImageDO>();
	ArrayList<PostPaymentDetailDONew> arrImages = new ArrayList<PostPaymentDetailDONew>();
	
	ReturnOrderDA returnOrderDA =null;
	PaymentDetailDA paymentDetailDA =null;
	public UploadImages() 
	{
		super("UploadImages");
	}

	@Override
	protected void onHandleIntent(Intent intent) 
	{
		uploadServiceImage();
		
		uploadGRVImages();
		uploadChequeImages();
//		uploadOrderPrintImages();
	}
	
	private void uploadOrderPrintImages()
	{
		returnOrderDA = new ReturnOrderDA();
		arrOrderPrintImageDO = new ReturnOrderDA().getAllOrderPrintImages();
		for (OrderPrintImageDO orderPrintImageDO : arrOrderPrintImageDO) {
			boolean isError=false;
			InputStream is = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(String.format(ServiceURLs.IMAGE_GLOBAL_URL_FULL,OrderPrintImageDO.getModule()));
//				HttpPost httppost = new HttpPost(String.format("http://10.20.53.52/KwalitySFA/uploadfile/upload.aspx?Module=%s","PrintInvoiceImage"));
				File filePath = new File(orderPrintImageDO.ImagePath);

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
				String serverUrl = parseImageUploadResponse(UploadImages.this, is);
				orderPrintImageDO.status=OrderPrintImageDO.getUploaded();
				returnOrderDA.updateOrderPrintImagesStatus(orderPrintImageDO, serverUrl);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				isError=true;
			} catch (IOException e) {
				e.printStackTrace();
				isError=true;
			} catch (Exception e) {
				e.printStackTrace();
				isError=true;
			}finally{
				if(isError){
					orderPrintImageDO.status=DamageImageDO.getError();
					returnOrderDA.updateOrderPrintImagesStatus(orderPrintImageDO, orderPrintImageDO.ImagePath);
				}
			}
			
		}
	}
	private void uploadGRVImages()
	{
		returnOrderDA = new ReturnOrderDA();
		arrAllDamagedImages = new ReturnOrderDA().getAllDamagedImages();
		for (DamageImageDO damageImageDO : arrAllDamagedImages) {
			boolean isError=false;
			InputStream is = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(String.format(ServiceURLs.IMAGE_GLOBAL_URL_FULL,DamageImageDO.getModule()));
				File filePath = new File(damageImageDO.ImagePath);

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
				String serverUrl = parseImageUploadResponse(UploadImages.this, is);
				damageImageDO.status=DamageImageDO.getUploaded();
				returnOrderDA.updateDamagedImageStatus(damageImageDO, serverUrl);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				isError=true;
			} catch (IOException e) {
				e.printStackTrace();
				isError=true;
			} catch (Exception e) {
				e.printStackTrace();
				isError=true;
			}finally{
				if(isError){
					damageImageDO.status=DamageImageDO.getError();
					returnOrderDA.updateDamagedImageStatus(damageImageDO, damageImageDO.ImagePath);
				}
			}

		}
	}

	private void uploadChequeImages()
	{
		paymentDetailDA = new PaymentDetailDA();
		arrImages = paymentDetailDA.getAllChequeImages();
		for (PostPaymentDetailDONew postPaymentDetailDONew : arrImages) {
			boolean isError=false;
			InputStream is = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(String.format(ServiceURLs.IMAGE_GLOBAL_URL_FULL,PostPaymentDetailDONew.getModule()));
				File filePath = new File(postPaymentDetailDONew.ChequeImagePath);

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
				String serverUrl = parseImageUploadResponse(UploadImages.this, is);
				postPaymentDetailDONew.ImageUploadStatus =PostPaymentDetailDONew.getUploaded();
				paymentDetailDA.updateChequeImageStatus(postPaymentDetailDONew, serverUrl);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				isError=true;
			} catch (IOException e) {
				e.printStackTrace();
				isError=true;
			} catch (Exception e) {
				e.printStackTrace();
				isError=true;
			}finally{
				if(isError){
					postPaymentDetailDONew.ImageUploadStatus=DamageImageDO.getError();
					paymentDetailDA.updateChequeImageStatus(postPaymentDetailDONew, postPaymentDetailDONew.ChequeImagePath);
				}
			}
			
		}
	}
	
	private void uploadServiceImage()
	{
		try
		{
			Vector<ServiceCaptureDO> vecServiceCaptureDOs 	= new Vector<ServiceCaptureDO>();
			vecServiceCaptureDOs 							= new StoreCheckDA().getServiceCapture();
			
			if(vecServiceCaptureDOs != null && vecServiceCaptureDOs.size() > 0)
			{
				
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				ServiceCaptureParser serviceCaptureParser = null;

				for (int i = 0; i < vecServiceCaptureDOs.size(); i++) 
				{
					if(vecServiceCaptureDOs.get(i).BeforeImage != null && !vecServiceCaptureDOs.get(i).BeforeImage.contains("../Data"))
					{
						String cameraImage = new UploadImage().uploadImage(this, vecServiceCaptureDOs.get(i).BeforeImage, "ServiceCapture", true);
						if(cameraImage != null && !cameraImage.equalsIgnoreCase(""))
							vecServiceCaptureDOs.get(i).BeforeImage = cameraImage;
					}
					if(vecServiceCaptureDOs.get(i).AfterImage != null && !vecServiceCaptureDOs.get(i).AfterImage.contains("../Data"))
					{
						String cameraImage = new UploadImage().uploadImage(this, vecServiceCaptureDOs.get(i).AfterImage, "ServiceCapture", true);
						if(cameraImage != null && !cameraImage.equalsIgnoreCase(""))
							vecServiceCaptureDOs.get(i).AfterImage = cameraImage;
					}
					serviceCaptureParser = new ServiceCaptureParser(getApplicationContext(),vecServiceCaptureDOs.get(i));
					connectionHelper.sendRequest(getApplicationContext(), BuildXMLRequest.InsertServiceCaptureSingle(vecServiceCaptureDOs.get(i)), serviceCaptureParser, ServiceURLs.InsertServiceCapture);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static String parseImageUploadResponse(Context context,InputStream inputStream) {
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
