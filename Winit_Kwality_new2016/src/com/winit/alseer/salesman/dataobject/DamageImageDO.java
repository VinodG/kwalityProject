package com.winit.alseer.salesman.dataobject;

import java.io.Serializable;

public class DamageImageDO implements Serializable{

	 public String OrderNo = "";
	 public String ItemCode = "";
	 public String LineNo = "";
	 public String ImagePath = "";
	 public String CapturedDate = "";
	 public String FileType="";
	 public int status;
	 public String trxCode; 
	 private static final String MODULE="GRVIMAGES";
	 private static final int STATUS_UPLOADED=100;
	 private static final int STATUS_ERROR_WHILE_UPLOADING=-100;
	 
	 public static final String getModule(){
		 return MODULE;
	 }
	 public static final int getUploaded(){
		 return STATUS_UPLOADED;
	 }
	 public static final int getError(){
		 return STATUS_ERROR_WHILE_UPLOADING;
	 }
	 
}
