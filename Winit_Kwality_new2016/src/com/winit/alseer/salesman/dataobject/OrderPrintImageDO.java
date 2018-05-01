package com.winit.alseer.salesman.dataobject;

import java.io.Serializable;

public class OrderPrintImageDO implements Serializable{

	 public String TrxCode = "";
	 public String UserCode = "";
	 public String ImageType = ""; //InvoiceImage
	 public String ImagePath = "";
	 public String CaptureDate = "";

	 public int status;

	 private static final String MODULE="PrintInvoiceImage";
	 public  static final String INVOICE_IMAGE_TYPE="InvoiceImage";
	 public static final int STATUS_UPLOADED=100;
	 private static final int STATUS_ERROR_WHILE_UPLOADING=-100;

	public  static  final int STATUS_UPLOADED_WITH_IMAGE = 1;

	 public static final String getModule(){
		 return MODULE;
	 }
	 public static final int getUploaded(){
		 return STATUS_UPLOADED;
	 }
	 public static final int getError(){
		 return STATUS_ERROR_WHILE_UPLOADING;
	 }

//	 ("TrxCode" VARCHAR, "ImagePath" VARCHAR, "ImageType" VARCHAR, "UserCode" VARCHAR, "CaptureDate" DATETIME, "Status" INTEGER)
	 
}
