package com.winit.alseer.salesman.dataobject;



@SuppressWarnings("serial")
public class PostPaymentDetailDONew extends BaseComparableDO
{
	public String RowStatus 			= "";
	public String ReceiptNo 			= "";
	public String LineNo 				= "";
	public String PaymentTypeCode 		= "";
	public String BankCode				= "";
	public String ChequeDate 			= "";
	public String ChequeNo 				= "";
	public String CCNo 					= "";
	public String CCExpiry 				= "";
	public String PaymentStatus 		= "";
	public String PaymentNote			= "";
	public String UserDefinedBankName   = "";
	public String Status 				= "";
	public String Amount				= "";
	public String ChequeImagePath		= "";
	public int ImageUploadStatus;
	public String salesOrgCode = "";
	public String branchName			= "";
	
	 private static final String MODULE="chequeimages";
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