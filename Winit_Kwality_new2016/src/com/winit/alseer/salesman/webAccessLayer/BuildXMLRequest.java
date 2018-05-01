package com.winit.alseer.salesman.webAccessLayer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import android.util.Log;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataaccesslayer.ScanResultObject;
import com.winit.alseer.salesman.dataobject.AddCustomerFilesDO;
import com.winit.alseer.salesman.dataobject.AdvanceOrderDO;
import com.winit.alseer.salesman.dataobject.AssetRequestDO;
import com.winit.alseer.salesman.dataobject.AssetServiceDO;
import com.winit.alseer.salesman.dataobject.AssetTrackingDetailDo;
import com.winit.alseer.salesman.dataobject.AssetTrackingDo;
import com.winit.alseer.salesman.dataobject.CashDenomDO;
import com.winit.alseer.salesman.dataobject.CashDenominationDetailDO;
import com.winit.alseer.salesman.dataobject.CheckInDemandInventoryDO;
import com.winit.alseer.salesman.dataobject.CompetitorItemDO;
import com.winit.alseer.salesman.dataobject.CustomerDO;
import com.winit.alseer.salesman.dataobject.CustomerDao;
import com.winit.alseer.salesman.dataobject.CustomerSurveyDO;
import com.winit.alseer.salesman.dataobject.CustomerSurveyDONew;
import com.winit.alseer.salesman.dataobject.CustomerVisitDO;
import com.winit.alseer.salesman.dataobject.DamageImageDO;
import com.winit.alseer.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.alseer.salesman.dataobject.Denominations;
import com.winit.alseer.salesman.dataobject.InitiativeTradePlanImageDO;
import com.winit.alseer.salesman.dataobject.InventoryDO;
import com.winit.alseer.salesman.dataobject.InventoryDetailDO;
import com.winit.alseer.salesman.dataobject.InventoryGroupDO;
import com.winit.alseer.salesman.dataobject.JouneyStartDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.LoadRequestDO;
import com.winit.alseer.salesman.dataobject.LoadRequestDetailDO;
import com.winit.alseer.salesman.dataobject.MallsDetails;
import com.winit.alseer.salesman.dataobject.MyActivityDO;
import com.winit.alseer.salesman.dataobject.NewCustomerDO;
import com.winit.alseer.salesman.dataobject.NotesObject;
import com.winit.alseer.salesman.dataobject.OptionsDO;
import com.winit.alseer.salesman.dataobject.OrderDO;
import com.winit.alseer.salesman.dataobject.OrderPrintImageDO;
import com.winit.alseer.salesman.dataobject.PostPaymentDO;
import com.winit.alseer.salesman.dataobject.PostPaymentDONew;
import com.winit.alseer.salesman.dataobject.PostPaymentDetailDO;
import com.winit.alseer.salesman.dataobject.PostPaymentDetailDONew;
import com.winit.alseer.salesman.dataobject.PostPaymentInviceDO;
import com.winit.alseer.salesman.dataobject.PostReasonDO;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.dataobject.QuestionOptionDO;
import com.winit.alseer.salesman.dataobject.ServiceCaptureDO;
import com.winit.alseer.salesman.dataobject.StatusDO;
import com.winit.alseer.salesman.dataobject.SurveyQuestionDONew;
import com.winit.alseer.salesman.dataobject.SurveyQuestionNewDO;
import com.winit.alseer.salesman.dataobject.TransferInoutDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.TrxPromotionDO;
import com.winit.alseer.salesman.dataobject.UserSurveyAnswerDO;
import com.winit.alseer.salesman.dataobject.VanLoadDO;
import com.winit.alseer.salesman.dataobject.VerifyRequestDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;

public class BuildXMLRequest 
{
	public static ArrayList<String> arrOrderNumbers;
	private final static String SOAP_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
										"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
										"<soap:Body>";
	
	private final static String SOAP_FOOTER="</soap:Body>"+
									"</soap:Envelope>";
	
	public static String loginRequest(String UserName, String password, String gcmId)
	{
		LogUtils.errorLog("CheckLogin","CheckLogin - "+UserName);
		StringBuilder sb = new StringBuilder();
		sb.append(SOAP_HEADER)
		.append("<CheckLogin xmlns=\"http://tempuri.org/\">")
		.append("<UserName>").append(UserName).append("</UserName>" )
		.append("<Password>").append(password).append("</Password>" )
		.append("<GCMKey>").append(gcmId).append("</GCMKey>" )
		.append("</CheckLogin>")
		.append(SOAP_FOOTER);
		
		LogUtils.errorLog("CheckLogin",""+sb.toString());
		
		return sb.toString();
	}

	public static String postPayments(Vector<PostPaymentDONew> vecPayments) {
		StringBuilder sb = new StringBuilder();

		sb.append(SOAP_HEADER)
				.append("<PostPaymentDetailsFromXML xmlns=\"http://tempuri.org/\">")
				.append("<objPaymentHeaderDcos>");

		// String strPayments = "";
		// String strXML = SOAP_HEADER+
		// "<PostPaymentDetailsFromXML xmlns=\"http://tempuri.org/\">" +
		// "<objPaymentHeaderDcos>";
		StringBuilder strPayments = new StringBuilder();
		for (PostPaymentDONew postPaymentDO : vecPayments) {
			strPayments.append("<PaymentHeaderDco>")
					.append("<Receipt_Number>")
					.append(postPaymentDO.ReceiptId).append("</Receipt_Number>")
					.append("<AppId>").append(postPaymentDO.AppPaymentId).append("</AppId>")
					.append("<SITE_NUMBER>").append(postPaymentDO.SiteId).append("</SITE_NUMBER>")
					.append("<ReceiptDate>").append(postPaymentDO.PaymentDate).append("</ReceiptDate>")
					.append("<EmpNo>").append(postPaymentDO.EmpNo).append("</EmpNo>")
					.append("<Amount>").append(postPaymentDO.Amount).append("</Amount>")
					.append("<JourneyCode>").append(postPaymentDO.JourneyCode).append("</JourneyCode>")
					.append("<VisitCode>").append(postPaymentDO.VisitCode).append("</VisitCode>")
					.append("<PaymentType>").append(postPaymentDO.PaymentType).append("</PaymentType>")
					.append("<VehicleCode>").append(postPaymentDO.vehicleNo).append("</VehicleCode>")
					.append("<CollectedBy>").append(postPaymentDO.salesmanCode).append("</CollectedBy>")
					.append("<SalesOrgCode>").append(postPaymentDO.salesOrgCode).append("</SalesOrgCode>")
					.append("<Division>").append(postPaymentDO.Division).append("</Division>")
					.append("<CustomerSignature>").append(URLEncoder.encode(postPaymentDO.CustomerSignature))
					.append("</CustomerSignature>");

			// String strPaymentDetails = "";
			StringBuilder strPaymentDetails = new StringBuilder();
			for (PostPaymentDetailDONew obPaymentDetailDO : postPaymentDO.vecPaymentDetailDOs) {
				try {
					strPaymentDetails
							.append("<PaymentDetailDco>")
							.append("<Receipt_Number>").append(obPaymentDetailDO.ReceiptNo).append("</Receipt_Number>")
							.append("<LineNo>").append(obPaymentDetailDO.LineNo).append("</LineNo>")
							.append("<PaymentMode>").append(obPaymentDetailDO.PaymentTypeCode).append("</PaymentMode>")
							.append("<BankCode>").append(obPaymentDetailDO.BankCode).append("</BankCode>")
							.append("<Branch>").append(URLEncoder.encode(obPaymentDetailDO.branchName,"UTF-8")).append("</Branch>")
							.append("<OtherBankName>")
							.append(URLEncoder.encode(obPaymentDetailDO.UserDefinedBankName,"UTF-8"))
							.append("</OtherBankName>")
							// enable it once it is done frombackend
							.append("<ChequeDate>").append(obPaymentDetailDO.ChequeDate).append("</ChequeDate>")
							.append("<ChequeNo>").append(obPaymentDetailDO.ChequeNo).append("</ChequeNo>")
							.append("<CCNo>").append(obPaymentDetailDO.CCNo).append("</CCNo>")
							.append("<CCExpiry>").append(obPaymentDetailDO.CCExpiry).append("</CCExpiry>")
							.append("<SalesOrgCode>").append(postPaymentDO.salesOrgCode).append("</SalesOrgCode>")
							.append("<ChequeImagePath>").append(URLEncoder.encode(obPaymentDetailDO.ChequeImagePath,"UTF-8")).append("</ChequeImagePath>")
							.append("<Amount>").append(obPaymentDetailDO.Amount).append("</Amount>")
							.append("</PaymentDetailDco>");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			// String strPaymentInvice = "";
			StringBuilder strPaymentInvice = new StringBuilder();
			for (PostPaymentInviceDO obPaymentDetailDO : postPaymentDO.vecPostPaymentInviceDOs) {
				strPaymentInvice.append("<PaymentInvoiceDco>")
						.append("<Receipt_Number>")
						.append(obPaymentDetailDO.ReceiptId)
						.append("</Receipt_Number>").append("<Invoice_Number>")
						.append(obPaymentDetailDO.TrxCode)
						.append("</Invoice_Number>").append("<TrxType>")
						.append(obPaymentDetailDO.TrxType).append("</TrxType>")
						.append("<SalesOrgCode>")
						.append(postPaymentDO.salesOrgCode)
						.append("</SalesOrgCode>").append("<Amount>")
						.append(obPaymentDetailDO.Amount).append("</Amount>")
						.append("</PaymentInvoiceDco>");
			}
			strPayments.append("<PaymentDetails>").append(strPaymentDetails)
					.append("</PaymentDetails>").append("<PaymentInvoices>")
					.append(strPaymentInvice).append("</PaymentInvoices>");
			// strPayments
			// +="<PaymentDetails>"+strPaymentDetails+"</PaymentDetails><PaymentInvoices>"+strPaymentInvice+"</PaymentInvoices>";

			// String strPaymentCashDenominations = "";
			StringBuilder strPaymentCashDenominations = new StringBuilder();
			for (CashDenominationDetailDO obPaymentDetailDO : postPaymentDO.vecCashDenominationDOs) {
				strPaymentCashDenominations
						.append("<PaymentCashDenominationDco>")
						.append("<PaymentCode>")
						.append(obPaymentDetailDO.PaymentCode)
						.append("</PaymentCode>")
						.append("<CashDenominationCode>")
						.append(obPaymentDetailDO.CashDenamationCode)
						.append("</CashDenominationCode>")
						.append("<TotalCount>")
						.append(obPaymentDetailDO.TotalCount)
						.append("</TotalCount>")
						.append("</PaymentCashDenominationDco>");
			}

			strPayments.append("<PaymentCashDenominations>")
					.append(strPaymentCashDenominations)
					.append("</PaymentCashDenominations>");

			// String strPaymentCashDenominationDetails = "";
			StringBuilder strPaymentCashDenominationDetails = new StringBuilder();
			for (CashDenominationDetailDO obPaymentDetailDO : postPaymentDO.vecCashDenominationDetailDOs) {
				strPaymentCashDenominationDetails
						.append("<PaymentCashDenominationDetailDco>")
						.append("<PaymentCode>")
						.append(obPaymentDetailDO.PaymentCode)
						.append("</PaymentCode>").append("<SerialNumber>")
						.append(obPaymentDetailDO.SerialNumber)
						.append("</SerialNumber>")
						.append("<CashDenominationCode>")
						.append(obPaymentDetailDO.CashDenamationCode)
						.append("</CashDenominationCode>")
						.append("</PaymentCashDenominationDetailDco >");
			}

			strPayments.append("<PaymentCashDenominationDetails>")
					.append(strPaymentCashDenominationDetails)
					.append("</PaymentCashDenominationDetails>")
					.append("</PaymentHeaderDco>");

		}

		sb.append(strPayments).append("</objPaymentHeaderDcos>")
				.append("</PostPaymentDetailsFromXML>").append(SOAP_FOOTER);
		
		try
		{
			ConnectionHelper.writeIntoLog(sb.toString(), "postPayments");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		LogUtils.errorLog("PostPaymentDetails",""+sb.toString());
		return sb.toString();
	}
	
	public static String GetTrxLogDataSync(String userCode,int lastDate,int lastTime)
	{
		StringBuilder strXML = new StringBuilder();
				strXML.append(SOAP_HEADER)
					.append("<GetTrxLogDataSync  xmlns=\"http://tempuri.org/\">")
					.append("<UserCode>").append(userCode).append("</UserCode>")
					.append("<lsd>").append(lastDate).append("</lsd>")
					.append("<lst>").append(lastTime).append("</lst>")
					.append("</GetTrxLogDataSync>")
					.append(SOAP_FOOTER) ;
		LogUtils.errorLog("GetTrxLogDataSync",""+strXML);
		return strXML.toString();
	}

//	public static String postPayments(Vector<PostPaymentDONew> vecPayments)
//	{
//		StringBuilder sb = new StringBuilder();
//		StringBuilder sb1 = new StringBuilder();
//		StringBuilder sb2 = new StringBuilder();
//		
//		sb.append(SOAP_HEADER)
//		.append("<PostPaymentDetailsFromXML xmlns=\"http://tempuri.org/\">")
//		.append("<objPaymentHeaderDcos>");
//		
//		String strPayments = "";
//		String strXML = SOAP_HEADER+
//						"<PostPaymentDetailsFromXML xmlns=\"http://tempuri.org/\">" +
//						"<objPaymentHeaderDcos>";
//		
//		for(PostPaymentDONew postPaymentDO : vecPayments)
//		{
//			strPayments +=  "<PaymentHeaderDco>"+
//							"<Receipt_Number>"+postPaymentDO.ReceiptId+"</Receipt_Number>"+
//							"<AppId>"+postPaymentDO.AppPaymentId+"</AppId>"+
//							"<SITE_NUMBER>"+postPaymentDO.SiteId+"</SITE_NUMBER>"+
//							"<ReceiptDate>"+postPaymentDO.PaymentDate+"</ReceiptDate>"+
//							"<EmpNo>"+postPaymentDO.EmpNo+"</EmpNo>"+
//							"<Amount>"+postPaymentDO.Amount+"</Amount>"+
//							"<JourneyCode>"+postPaymentDO.JourneyCode+"</JourneyCode>" +
//					  		"<VisitCode>"+postPaymentDO.VisitCode+"</VisitCode>" +
//							"<PaymentType>"+postPaymentDO.PaymentType+"</PaymentType>"+
//							"<VehicleCode>"+postPaymentDO.vehicleNo+"</VehicleCode>"+
//							"<CollectedBy>"+postPaymentDO.salesmanCode+"</CollectedBy>"+
//							"<SalesOrgCode>"+postPaymentDO.salesOrgCode+"</SalesOrgCode>"+
//							"<CustomerSignature>"+URLEncoder.encode(postPaymentDO.CustomerSignature)+"</CustomerSignature>";
//			                
//							String strPaymentDetails = "";
//							for(PostPaymentDetailDONew obPaymentDetailDO : postPaymentDO.vecPaymentDetailDOs)
//							{
//								try {
//									strPaymentDetails += "<PaymentDetailDco>"+
//										 				 "<Receipt_Number>"+obPaymentDetailDO.ReceiptNo+"</Receipt_Number>"+
//										 				 "<LineNo>"+obPaymentDetailDO.LineNo+"</LineNo>"+
//										 				 "<PaymentMode>"+obPaymentDetailDO.PaymentTypeCode+"</PaymentMode>"+
//										 				 "<BankCode>"+obPaymentDetailDO.BankCode+"</BankCode>"+
//										 				 "<OtherBankName>"+URLEncoder.encode(obPaymentDetailDO.UserDefinedBankName,"UTF-8")+"</OtherBankName>"+//enable it once it is done frombackend
//										 				 "<ChequeDate>"+obPaymentDetailDO.ChequeDate+"</ChequeDate>"+
//										 				 "<ChequeNo>"+obPaymentDetailDO.ChequeNo+"</ChequeNo>"+
//										 				 "<CCNo>"+obPaymentDetailDO.CCNo+"</CCNo>"+
//										 				 "<CCExpiry>"+obPaymentDetailDO.CCExpiry+"</CCExpiry>"+
//										 				"<SalesOrgCode>"+postPaymentDO.salesOrgCode+"</SalesOrgCode>"+
//										 				"<ChequeImagePath>"+obPaymentDetailDO.ChequeImagePath+"</ChequeImagePath>"+
//										 				 "<Amount>"+obPaymentDetailDO.Amount+"</Amount>"+
//										 				 "</PaymentDetailDco>";
//								} catch (UnsupportedEncodingException e) {
//									e.printStackTrace();
//								}
//							}
//							String strPaymentInvice = "";
//							for(PostPaymentInviceDO obPaymentDetailDO : postPaymentDO.vecPostPaymentInviceDOs)
//							{
//								strPaymentInvice += "<PaymentInvoiceDco>"+
//									 				 "<Receipt_Number>"+obPaymentDetailDO.ReceiptId+"</Receipt_Number>"+
//									 				 "<Invoice_Number>"+obPaymentDetailDO.TrxCode+"</Invoice_Number>"+
//									 				 "<TrxType>"+obPaymentDetailDO.TrxType+"</TrxType>"+
//									 				"<SalesOrgCode>"+postPaymentDO.salesOrgCode+"</SalesOrgCode>"+
//									 				 "<Amount>"+obPaymentDetailDO.Amount+"</Amount>"+
//									 				 "</PaymentInvoiceDco>";
//							}
//							strPayments	+="<PaymentDetails>"+strPaymentDetails+"</PaymentDetails><PaymentInvoices>"+strPaymentInvice+"</PaymentInvoices>";
//			
//							String strPaymentCashDenominations = "";
//							for(CashDenominationDetailDO obPaymentDetailDO : postPaymentDO.vecCashDenominationDOs)
//							{
//								strPaymentCashDenominations += "<PaymentCashDenominationDco>"+
//									 				 "<PaymentCode>"+obPaymentDetailDO.PaymentCode+"</PaymentCode>"+
//									 				 "<CashDenominationCode>"+obPaymentDetailDO.CashDenamationCode+"</CashDenominationCode>"+
//									 				 "<TotalCount>"+obPaymentDetailDO.TotalCount+"</TotalCount>"+
//									 				 "</PaymentCashDenominationDco>";
//							}
//							
//							strPayments	+="<PaymentCashDenominations>"+strPaymentCashDenominations+"</PaymentCashDenominations>";
//							
//							String strPaymentCashDenominationDetails = "";
//							for(CashDenominationDetailDO obPaymentDetailDO : postPaymentDO.vecCashDenominationDetailDOs)
//							{
//								strPaymentCashDenominationDetails += "<PaymentCashDenominationDetailDco>"+
//											"<PaymentCode>"+obPaymentDetailDO.PaymentCode+"</PaymentCode>"+
//											"<SerialNumber>"+obPaymentDetailDO.SerialNumber+"</SerialNumber>"+
//											"<CashDenominationCode>"+obPaymentDetailDO.CashDenamationCode+"</CashDenominationCode>"+
//											"</PaymentCashDenominationDetailDco >";
//							}
//							
//							
//							strPayments	+="<PaymentCashDenominationDetails>"+strPaymentCashDenominationDetails+"</PaymentCashDenominationDetails></PaymentHeaderDco>";
//
//							
//		}
//		strXML = strXML+strPayments+ "</objPaymentHeaderDcos>"+
//									"</PostPaymentDetailsFromXML>"+
//						SOAP_FOOTER;
////		writeToSdcards(strXML);
//		return strXML;
//	}
	public static String postSinglePayments(PostPaymentDO postPaymentDO) {
		StringBuilder sb = new StringBuilder();
		sb.append(SOAP_HEADER)
				.append("<InsertRecipt xmlns=\"http://tempuri.org/\">")
				.append("<ReceiptResponse>").append("<Receipts>")
				.append("<Receipt>");

		StringBuilder strPayments = new StringBuilder();
		strPayments.append("<RECEIPT_TYPE>").append(postPaymentDO.receiptType)
				.append("</RECEIPT_TYPE>").append("<RECEIPT_METHOD>")
				.append(postPaymentDO.PaymentMode).append("</RECEIPT_METHOD>")
				.append("<RECEIPT_NUMBER>").append(postPaymentDO.PaymentId)
				.append("</RECEIPT_NUMBER>").append("<RECEIPT_ID>").append(0)
				.append("</RECEIPT_ID>").append("<CUSTOMER_NO>")
				.append(postPaymentDO.customerId).append("</CUSTOMER_NO>")
				.append("<SITE_NUMBER>").append(postPaymentDO.customerSiteId)
				.append("</SITE_NUMBER>").append("<RECEIPT_DATE>")
				.append(postPaymentDO.PaymentDate).append("</RECEIPT_DATE>")
				.append("<CHECK_DATE>").append(postPaymentDO.ChequeDate)
				.append("</CHECK_DATE>").append("<AMOUNT>")
				.append(postPaymentDO.Amount).append("</AMOUNT>")
				.append("<BANK>").append(postPaymentDO.BankName)
				.append("</BANK>").append("<CHECK_NO>")
				.append(postPaymentDO.ChequeNumber).append("</CHECK_NO>")
				.append("<CC_NO>").append(postPaymentDO.CreditCardNumber)
				.append("</CC_NO>").append("<CC_EXPIRY>")
				.append(postPaymentDO.ExpiryDate).append("</CC_EXPIRY>")
				.append("<COUPON_NO>").append(postPaymentDO.CouponNo)
				.append("</COUPON_NO>").append("<AppReceiptId>")
				.append(postPaymentDO.strUUID).append("</AppReceiptId>")
				.append("<CREATEDBY>").append(postPaymentDO.CREATEDBY)
				.append("</CREATEDBY>").append("<ReceiptDetails>");
		StringBuilder strPaymentDetails = new StringBuilder();
		for (PostPaymentDetailDO obPaymentDetailDO : postPaymentDO.vecPaymentDetailDOs) {
			strPaymentDetails.append("<ReceiptDetail>")
					.append("<INVOICE_NUMBER>")
					.append(obPaymentDetailDO.invoiceNumber)
					.append("</INVOICE_NUMBER>").append("<INVOICE_AMOUNT>")
					.append(obPaymentDetailDO.invoiceAmount)
					.append("</INVOICE_AMOUNT>").append("<AppReceiptId>")
					.append(postPaymentDO.strUUID).append("</AppReceiptId>")// added
																			// new
																			// by
																			// anil
					.append("</ReceiptDetail>");
		}

		sb.append(strPayments).append(strPaymentDetails)
				.append("</ReceiptDetails>").append("</Receipt>")
				.append("</Receipts>").append("</ReceiptResponse>")
				.append("</InsertRecipt>").append(SOAP_FOOTER);
		
		LogUtils.errorLog("InsertRecipt",""+sb.toString());
		return sb.toString();
	}
//	public static String postSinglePayments(PostPaymentDO postPaymentDO)
//	{
//		String strPayments = "", strPaymentDetails= "";
//		String strXML = SOAP_HEADER+
//						"<InsertRecipt xmlns=\"http://tempuri.org/\">" +
//						"<ReceiptResponse>"+
//						"<Receipts>"+
//						"<Receipt>";
//			strPayments +=  "<RECEIPT_TYPE>"+postPaymentDO.receiptType+"</RECEIPT_TYPE>"+
//							"<RECEIPT_METHOD>"+postPaymentDO.PaymentMode+"</RECEIPT_METHOD>"+
//							"<RECEIPT_NUMBER>"+postPaymentDO.PaymentId+"</RECEIPT_NUMBER>"+
//							"<RECEIPT_ID>"+0+"</RECEIPT_ID>"+
//							"<CUSTOMER_NO>"+postPaymentDO.customerId+"</CUSTOMER_NO>"+
//							"<SITE_NUMBER>"+postPaymentDO.customerSiteId+"</SITE_NUMBER>"+
//							"<RECEIPT_DATE>"+postPaymentDO.PaymentDate+"</RECEIPT_DATE>"+
//							"<CHECK_DATE>"+postPaymentDO.ChequeDate+"</CHECK_DATE>"+
//							"<AMOUNT>"+postPaymentDO.Amount+"</AMOUNT>"+
//							"<BANK>"+postPaymentDO.BankName+"</BANK>"+
//							"<CHECK_NO>"+postPaymentDO.ChequeNumber+"</CHECK_NO>"+
//							"<CC_NO>"+postPaymentDO.CreditCardNumber+"</CC_NO>"+
//							"<CC_EXPIRY>"+postPaymentDO.ExpiryDate+"</CC_EXPIRY>"+
//							"<COUPON_NO>"+postPaymentDO.CouponNo+"</COUPON_NO>"+
//							"<AppReceiptId>"+postPaymentDO.strUUID+"</AppReceiptId>" +
//							"<CREATEDBY>"+postPaymentDO.CREATEDBY+"</CREATEDBY>" +
//							"<ReceiptDetails>";
//			for(PostPaymentDetailDO obPaymentDetailDO : postPaymentDO.vecPaymentDetailDOs)
//			{
//				strPaymentDetails += "<ReceiptDetail>"+
//									 "<INVOICE_NUMBER>"+obPaymentDetailDO.invoiceNumber+"</INVOICE_NUMBER>"+
//									 "<INVOICE_AMOUNT>"+obPaymentDetailDO.invoiceAmount+"</INVOICE_AMOUNT>"+
//									 "<AppReceiptId>"+postPaymentDO.strUUID+"</AppReceiptId>" +//added new by anil
//									 "</ReceiptDetail>";
//			}
//		strXML = strXML+strPayments+strPaymentDetails+ "</ReceiptDetails>"+"</Receipt>"+"</Receipts>"+
//						"</ReceiptResponse>"+
//						"</InsertRecipt>"+
//						SOAP_FOOTER;
//		return strXML;
//	}
	
	public static String postSingleCouponPayments(PostPaymentDO postPaymentDO) {
		StringBuilder sb = new StringBuilder();
		sb.append(SOAP_HEADER)
				.append("<InsertReciptOnlineCoupons xmlns=\"http://tempuri.org/\">")
				.append("<Receipt>");

		StringBuilder strPayments = new StringBuilder();
		strPayments.append("<RECEIPT_TYPE>").append(postPaymentDO.receiptType)
				.append("</RECEIPT_TYPE>").append("<RECEIPT_METHOD>")
				.append(postPaymentDO.PaymentMode).append("</RECEIPT_METHOD>")
				.append("<RECEIPT_NUMBER>").append(postPaymentDO.PaymentId)
				.append("</RECEIPT_NUMBER>").append("<RECEIPT_ID>").append(0)
				.append("</RECEIPT_ID>").append("<CUSTOMER_NO>")
				.append(postPaymentDO.customerId).append("</CUSTOMER_NO>")
				.append("<SITE_NUMBER>").append(postPaymentDO.customerSiteId)
				.append("</SITE_NUMBER>").append("<RECEIPT_DATE>")
				.append(postPaymentDO.PaymentDate).append("</RECEIPT_DATE>")
				.append("<CHECK_DATE>").append(postPaymentDO.ChequeDate)
				.append("</CHECK_DATE>").append("<AMOUNT>")
				.append(postPaymentDO.Amount).append("</AMOUNT>")
				.append("<BANK>").append(postPaymentDO.BankName)
				.append("</BANK>").append("<CHECK_NO>")
				.append(postPaymentDO.ChequeNumber).append("</CHECK_NO>")
				.append("<CC_NO>").append(postPaymentDO.CreditCardNumber)
				.append("</CC_NO>").append("<CC_EXPIRY>")
				.append(postPaymentDO.ExpiryDate).append("</CC_EXPIRY>")
				.append("<COUPON_NO>").append(postPaymentDO.CouponNo)
				.append("</COUPON_NO>").append("<AppReceiptId>")
				.append(postPaymentDO.strUUID).append("</AppReceiptId>")
				.append("<CREATEDBY>").append(postPaymentDO.CREATEDBY)
				.append("</CREATEDBY>").append("<ReceiptDetails>");
		StringBuilder strPaymentDetails = new StringBuilder();
		for (PostPaymentDetailDO obPaymentDetailDO : postPaymentDO.vecPaymentDetailDOs) {
			strPaymentDetails.append("<ReceiptDetail>")
					.append("<INVOICE_NUMBER>")
					.append(obPaymentDetailDO.invoiceNumber)
					.append("</INVOICE_NUMBER>").append("<INVOICE_AMOUNT>")
					.append(obPaymentDetailDO.invoiceAmount)
					.append("</INVOICE_AMOUNT>").append("<AppReceiptId>")
					.append(postPaymentDO.strUUID).append("</AppReceiptId>")// added
																			// new
																			// by
																			// anil
					.append("</ReceiptDetail>");
		}

		sb.append(strPayments).append(strPaymentDetails)
				.append("</ReceiptDetails>").append("</Receipt>")
				.append("</InsertReciptOnlineCoupons>").append(SOAP_FOOTER);
		

		LogUtils.errorLog("InsertReciptOnlineCoupons",""+sb.toString());
		return sb.toString();
	}
//	public static String postSingleCouponPayments(PostPaymentDO postPaymentDO)
//	{
//		String strPayments = "", strPaymentDetails= "";
//		String strXML = SOAP_HEADER+
//						"<InsertReciptOnlineCoupons xmlns=\"http://tempuri.org/\">"+
//						"<Receipt>";
//			strPayments +=  "<RECEIPT_TYPE>"+postPaymentDO.receiptType+"</RECEIPT_TYPE>"+
//							"<RECEIPT_METHOD>"+postPaymentDO.PaymentMode+"</RECEIPT_METHOD>"+
//							"<RECEIPT_NUMBER>"+postPaymentDO.PaymentId+"</RECEIPT_NUMBER>"+
//							"<RECEIPT_ID>"+0+"</RECEIPT_ID>"+
//							"<CUSTOMER_NO>"+postPaymentDO.customerId+"</CUSTOMER_NO>"+
//							"<SITE_NUMBER>"+postPaymentDO.customerSiteId+"</SITE_NUMBER>"+
//							"<RECEIPT_DATE>"+postPaymentDO.PaymentDate+"</RECEIPT_DATE>"+
//							"<CHECK_DATE>"+postPaymentDO.ChequeDate+"</CHECK_DATE>"+
//							"<AMOUNT>"+postPaymentDO.Amount+"</AMOUNT>"+
//							"<BANK>"+postPaymentDO.BankName+"</BANK>"+
//							"<CHECK_NO>"+postPaymentDO.ChequeNumber+"</CHECK_NO>"+
//							"<CC_NO>"+postPaymentDO.CreditCardNumber+"</CC_NO>"+
//							"<CC_EXPIRY>"+postPaymentDO.ExpiryDate+"</CC_EXPIRY>"+
//							"<COUPON_NO>"+postPaymentDO.CouponNo+"</COUPON_NO>"+
//							"<AppReceiptId>"+postPaymentDO.strUUID+"</AppReceiptId>" +
//							"<CREATEDBY>"+postPaymentDO.CREATEDBY+"</CREATEDBY>" +
//							"<ReceiptDetails>";
//			
//			for(PostPaymentDetailDO obPaymentDetailDO : postPaymentDO.vecPaymentDetailDOs)
//			{
//				strPaymentDetails += "<ReceiptDetail>"+
//									 "<INVOICE_NUMBER>"+obPaymentDetailDO.invoiceNumber+"</INVOICE_NUMBER>"+
//									 "<INVOICE_AMOUNT>"+obPaymentDetailDO.invoiceAmount+"</INVOICE_AMOUNT>"+
//									 "<AppReceiptId>"+obPaymentDetailDO.detailUUID+"</AppReceiptId>" +//added new by anil
//									 "</ReceiptDetail>";
//			}
//		strXML = strXML+strPayments+strPaymentDetails+ "</ReceiptDetails>"+"</Receipt>"+
//	    				"</InsertReciptOnlineCoupons>"+
//						SOAP_FOOTER;
//		
//		LogUtils.errorLog("strXML", "strXML - "+strXML);
//		return strXML;
//	}
	
	public static String deleteOrder(String strOrderId,String appId)
	{

		  StringBuilder strXML = new StringBuilder();
		  strXML.append(SOAP_HEADER)
				.append("<DeleteOrderFromApp xmlns=\"http://tempuri.org/\">")
				.append("<OrderNumber>").append(strOrderId).append("</OrderNumber>")
				.append("<AppOrderId>").append(appId).append("</AppOrderId>")
				.append("</DeleteOrderFromApp>")
				.append(SOAP_FOOTER);
		LogUtils.errorLog("deleteOrder",""+strXML);
		return strXML.toString();
//		String strXML = SOAP_HEADER+
//						"<DeleteOrderFromApp xmlns=\"http://tempuri.org/\">"+
//						"<OrderNumber>"+strOrderId+"</OrderNumber>"+
//						"<AppOrderId>"+appId+"</AppOrderId>"+
//						"</DeleteOrderFromApp>"+
//						SOAP_FOOTER;
//		LogUtils.errorLog("deleteOrder",""+strXML);
//		return strXML;
	}
	
	
	//changePasswordRequest
	public static String changePasswordRequest(String UserName, String OldPassword, String NewPassword)
	{
		
		  StringBuilder strXML = new StringBuilder();
		  strXML.append(SOAP_HEADER)
				.append("<ChangePassword xmlns=\"http://tempuri.org/\">")
				.append("<UserName>").append(UserName).append("</UserName>")
				.append("<OldPassword>").append(OldPassword).append("</OldPassword>")
				.append("<NewPassword>").append(NewPassword).append("</NewPassword>")
				.append("</ChangePassword>")
				.append(SOAP_FOOTER);
		LogUtils.errorLog("ChangePassword",""+strXML);
		
//		
//		String strXML1 =SOAP_HEADER+ "<ChangePassword xmlns=\"http://tempuri.org/\">"+
//							"<UserName>"+UserName+"</UserName>" +
//							"<OldPassword>"+OldPassword+"</OldPassword>" +
//							"<NewPassword>"+NewPassword+"</NewPassword>" +
//						"</ChangePassword>"+
//						SOAP_FOOTER	;
		return strXML.toString();
	}
	
	//getJourneyPlanReqest
	public static String getJourneyPlanReqest(String UserId, String Token, String Date)
	{
		  StringBuilder strXML = new StringBuilder();
		  strXML.append("<JourneyPlan>")
				.append("<UserId>").append(UserId).append("</UserId>")
				.append("<strToken>").append(Token).append("</strToken>")
				.append("<Date>").append(Date).append("</Date>")
				.append("</JourneyPlan>");
		
//		String strXML = "<JourneyPlan>" +
//							"<UserId>"+UserId+"</UserId>" +
//							"<strToken>"+Token+"</strToken>" +
//							"<Date>"+Date+"</Date>" +
//						"</JourneyPlan>";
		return strXML.toString();
	}
	
	//getPreseller Messages
	public static String getPresellerMessages(String UserId, String strSyncTime)
	{
		LogUtils.errorLog("getPresellerMessages - ", ""+strSyncTime);
		
		StringBuilder strXML = new StringBuilder();
		  strXML.append(SOAP_HEADER)
		        .append("<getMessages xmlns=\"http://tempuri.org/\">")
		        .append("<UserId>").append(UserId).append("</UserId>")
		        .append("<LastSyncDate>").append(strSyncTime).append("</LastSyncDate>")
		        .append("</getMessages>")
		  		.append(SOAP_FOOTER);
		
//		String strXML = SOAP_HEADER + "<getMessages xmlns=\"http://tempuri.org/\">"+
//							"<UserId>"+UserId+"</UserId>" +
//							"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
//							"</getMessages>"+
//						SOAP_FOOTER ;
		return strXML.toString();
	}
	public static String getAllPriceWithSync(String strEmpId, String lsd, String lst)
	{
		StringBuilder strXML = new StringBuilder();
		
		strXML.append(SOAP_HEADER)
					.append("<GetAllHHPriceWithSync xmlns=\"http://tempuri.org/\">")
					.append("<UserCode>").append(strEmpId).append("</UserCode>")
				    .append("<lsd>").append(lsd).append("</lsd>")
				    .append("<lst>").append(lst).append("</lst>")
				    .append("</GetAllHHPriceWithSync>")
					.append(SOAP_FOOTER);
		return strXML.toString();
	}
	public static String getAllRegions(String strSyncTime)
	{
		LogUtils.errorLog("getAllRegions - ", ""+strSyncTime);
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
		 			.append("<GetAllLocations xmlns=\"http://tempuri.org/\">")
	      			.append("<LastSyncDate>").append(strSyncTime).append("</LastSyncDate>")
	      			.append("</GetAllLocations>")
						.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	//getAllItems for products
		public static String getAllItems(String strSyncTime)
		{
			LogUtils.errorLog("getAllItems - ", ""+strSyncTime);
			StringBuilder strXML = new StringBuilder();
			
			strXML.append(SOAP_HEADER)
						.append("<GetAllItems xmlns=\"http://tempuri.org/\">")
						.append("<LastSyncDate>").append(strSyncTime).append("</LastSyncDate>")
						.append("</GetAllItems>")
							.append(SOAP_FOOTER) ;
			return strXML.toString();
		}
	
	
	//sendMessageRequest
	public static String deleteMessageRequest(String messagesIds,String userId)
	{
		StringBuilder strXML = new StringBuilder();
		
		strXML.append(SOAP_HEADER)   
					.append("<DeleteMessages xmlns=\"http://tempuri.org/\">")
					.append("<MessagesIds>").append(messagesIds).append("</MessagesIds>")
					.append("<UserId>").append(userId).append("</UserId>")
					.append("</DeleteMessages>")
	      				.append(SOAP_FOOTER);
		return strXML.toString();
	}

	//AddCustomerNotesRequest
	public static String addCustomerNotesRequest(String PreSellerId, String CustomerId, String Subject, String Description, String Image, String Date)
	{
		StringBuilder strXML = new StringBuilder();
		
		strXML.append(SOAP_HEADER)
						.append("<AddCustomerNotesRequest>")
							.append("<PreSellerId>").append(PreSellerId).append("</PreSellerId>")
							.append("<CustomerId>").append(CustomerId).append("</CustomerId>")
							.append("<Subject>").append(Subject).append("</Subject>")
							.append("<Description>").append(Description).append("</Description>")
							.append("<Image>").append(Image).append("</Image>")
							.append("<Date>").append(Date).append("</Date>")
						.append("</AddCustomerNotesRequest>")
						.append(SOAP_FOOTER);
		
		return strXML.toString();
	}
	
	/**New StringBuffer RQ inplace of String RQ
	 * From GetMasterData to AllUsers
	 * */
	//GetMasterData
			public static String getMasterDate(String empNo)
			{
				StringBuilder strXML = new StringBuilder();
				strXML.append(SOAP_HEADER)
							.append("<GetMasterDataFile xmlns=\"http://tempuri.org/\">" )
							.append("<UserCode>").append(empNo).append("</UserCode>")
							.append("<lsd>0</lsd>")
							 .append("<lst>0</lst>")
							.append("</GetMasterDataFile>")
			      				.append(SOAP_FOOTER) ;
			      				LogUtils.errorLog("strXML",""+strXML);
				return strXML.toString();
			}
			public static String getDiscount(String salemanCode, String lsd,  String lst)
			{
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
						.append("<GetDiscounts xmlns=\"http://tempuri.org/\">")
						     .append("<UserCode>").append(salemanCode).append("</UserCode>")
						     .append("<lsd>").append(lsd).append("</lsd>")
						     .append("<lst>").append(lst).append("</lst>")
						   .append("</GetDiscounts>")
			      				.append(SOAP_FOOTER) ;
			      				LogUtils.errorLog("strXML",""+strXML);
				return strXML.toString();
			}
			public static String getCustomerGroupById(String UserId, String strSyncTime)
			{
				LogUtils.errorLog("getCustomerGroupById - ", "getCustomerGroupById"+strSyncTime);
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
								.append("<GetCustomerGroupByUserId xmlns=\"http://tempuri.org/\">" )
								.append("<UserId>").append(UserId).append("</UserId>" )
								.append("<LastSyncDate>").append(strSyncTime).append("</LastSyncDate>")
							.append("</GetCustomerGroupByUserId>")
			      				.append(SOAP_FOOTER) ;
			      				LogUtils.errorLog("strXML",""+strXML);
				return strXML.toString();
			}
			
			public static String getetCustomersByUserIdWithoutInvoice(String UserId, String strSyncTime)
			{
				LogUtils.errorLog("GetCustomersByUserIdWithoutInvoice  - ", ""+strSyncTime);
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
							.append("<GetCustomersByUserIdWithoutInvoice xmlns=\"http://tempuri.org/\">" )
							.append("<UserId>").append(UserId).append("</UserId>" )
							.append("<LastSyncDate>").append(strSyncTime).append("</LastSyncDate>")
							.append("</GetCustomersByUserIdWithoutInvoice>")
								.append(SOAP_FOOTER) ;
				return strXML.toString();
			}
			public static String getetCustomersPendingInvoice(String UserId, String strSyncTime)
			{
				LogUtils.errorLog("GetPendingSalesInvoice   - ", "strSyncTime - "+strSyncTime);
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
							.append("<GetPendingSalesInvoice xmlns=\"http://tempuri.org/\">" )
							.append("<UserId>").append(UserId).append("</UserId>" )
							.append("<LastSyncDate>").append(strSyncTime).append("</LastSyncDate>")
							.append("</GetPendingSalesInvoice>")
								.append(SOAP_FOOTER) ;
				return strXML.toString();
			}
			public static String getTopSelling(String Selesman, String strSyncTime)
			{
				LogUtils.errorLog("getTopSelling - ", ""+strSyncTime);
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
							.append("<GetTopSellingItems xmlns=\"http://tempuri.org/\">" )
							.append("<SALESMAN>").append(Selesman).append("</SALESMAN>" )
							.append("<LastSyncDate>").append(strSyncTime).append("</LastSyncDate>")
							.append("</GetTopSellingItems>")
								.append(SOAP_FOOTER) ;
				return strXML.toString();
			}
			
			public static String getMustHave(String Selesman, String strSyncTime)
			{
				LogUtils.errorLog("getMustHave - ", ""+strSyncTime);
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
							.append("<GetMustHaveItems xmlns=\"http://tempuri.org/\">" )
							.append("<SALESMAN>").append(Selesman).append("</SALESMAN>" )
							.append("<LastSyncDate>").append(strSyncTime).append("</LastSyncDate>")
							.append("</GetMustHaveItems>")
								.append(SOAP_FOOTER) ;
				return strXML.toString();
			}

			//method to get the Journey plan by Salesman Id
			public static String getJourneyPlan(String SalesmanId, String strSyncTime)
			{
				LogUtils.errorLog("getJourneyPlan - ", ""+strSyncTime);
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
							.append("<GetBeats xmlns=\"http://tempuri.org/\">")
							.append("<SalesManCode>").append(SalesmanId).append("</SalesManCode>")
							.append("<LastSyncDate>").append(strSyncTime).append("</LastSyncDate>")
							.append("</GetBeats>")
								.append(SOAP_FOOTER) ;
				return strXML.toString();
			}
			
			//AddNotes
			public static String inserNotes(NotesObject objNotesObject)
			{
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
								.append("<InsertNote xmlns=\"http://tempuri.org/\">")
									.append("<CustomerId>").append(objNotesObject.Customer_ID).append("</CustomerId>" )
									.append("<PresellerId>").append(objNotesObject.Emp_Id).append("</PresellerId>" )
									.append("<Subject>").append(objNotesObject.Note_Title).append("</Subject>" )
									.append("<Description>").append(objNotesObject.Note_Description).append("</Description>" )
									.append("<Image>").append(objNotesObject.image).append("</Image>" )
								.append("</InsertNote>")
								.append(SOAP_FOOTER) ;
				
				return strXML.toString();
			}

			 //AllRoles
			  public static String getAllRoles()
			  {
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER).append("<GetAllRoles xmlns=\"http://tempuri.org/\" />")
				.append(SOAP_FOOTER) ;
				return strXML.toString();
			  }
				
			//AllUsers
			public static String getAllUsers(String strSyncTime)
			{
				LogUtils.errorLog("getAllUsers - ", ""+strSyncTime);
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
				  			  .append("<GetAllUsers xmlns=\"http://tempuri.org/\">")
			      			  .append("<LastSyncDate>").append(strSyncTime).append("</LastSyncDate>")
			      			  .append("</GetAllUsers>")
						.append(SOAP_FOOTER) ;
			    return strXML.toString();
			}
			
			
			//PresellerTargets
			public static String getPresellerTargets(String strSyncTime)
			{
				LogUtils.errorLog("getPresellerTargets - ", ""+strSyncTime);
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
				  			  .append("<GetPreSalerAchievedTargetBySalesmancode xmlns=\"http://tempuri.org/\">")
			      			  .append("<Salesmancode>").append(strSyncTime).append("</Salesmancode>")
			      			  .append("</GetPreSalerAchievedTargetBySalesmancode>")
						.append(SOAP_FOOTER) ;
			    return strXML.toString();
			}
			
			// Customer History
			public static String getCustomerHistory(String EmpNo)
			{
				LogUtils.errorLog("getCustomerHistory - ", ""+EmpNo);
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
				  			  .append("<getCustomerHistory xmlns=\"http://tempuri.org/\">")
			      			  .append("<SalesManCode>").append(EmpNo).append("</SalesManCode>")
			      			  .append("</getCustomerHistory>")
						.append(SOAP_FOOTER) ;
				LogUtils.errorLog("strXML", "strXML"+strXML);
			    return strXML.toString();
			}
			
			// Customer History
			public static String getCustomerHistoryWithSync(String EmpNo,String LastSync)
			{
				LogUtils.errorLog("getCustomerHistorywithSync - ", "getCustomerHistorywithSync"+LastSync);
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
				  			  .append("<getCustomerHistorywithSync xmlns=\"http://tempuri.org/\">")
			      			  .append("<SalesManCode>").append(EmpNo).append("</SalesManCode>")
			      			  .append("<LastSync>").append(LastSync).append("</LastSync>")
			      			  .append("</getCustomerHistorywithSync>")
						.append(SOAP_FOOTER) ;
				LogUtils.errorLog("strXML", "strXML"+strXML);
			    return strXML.toString();
			}
			
			//Competetor Survey
			public static String getCompetitorDetail(String empNo, String lastSyncDate)
			{
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
							.append("<GetCompetitorDetail xmlns=\"http://tempuri.org/\">")
			    			.append("<LastSyncDate>").append(lastSyncDate).append("</LastSyncDate>")
			    			.append("<EmpId>").append(empNo).append("</EmpId>")
			    			.append("</GetCompetitorDetail>")
								.append(SOAP_FOOTER) ;
				LogUtils.errorLog("DeleteReceiptDetailsFromApp",""+strXML);
				return strXML.toString();
			}
			
			public static String getAllMovement(String empNo, String lastSyncDate)
			{
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
							.append("<GetAllMovements_Sync  xmlns=\"http://tempuri.org/\">")
							.append("<UserCode>").append(empNo).append("</UserCode>")
			    			.append("<LastSyncDate>").append(lastSyncDate).append("</LastSyncDate>")
			    			.append("</GetAllMovements_Sync >")
								.append(SOAP_FOOTER) ;
				return strXML.toString();
			}
			
			public static String getActiveStatus(String empNo, String lsd, String lst)
			{
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
							.append("<GetAppActiveStatus  xmlns=\"http://tempuri.org/\">")
							.append("<UserCode>").append(empNo).append("</UserCode>")
			    			.append("<lsd>").append(lsd).append("</lsd>")
			    			.append("<lst>").append(lst).append("</lst>")
			    			.append("</GetAppActiveStatus>")
								.append(SOAP_FOOTER) ;
				return strXML.toString();
			}
			
			
			public static String getVanStockLogDetail(String userCode, String lsd, String lst)
			{
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
							.append("<GetVanStockLogDetail xmlns=\"http://tempuri.org/\">")
			     			.append("<UserCode>").append(userCode).append("</UserCode>")
			     			.append("<lsd>").append(lsd).append("</lsd>")
			    			.append("<lst>").append(lst).append("</lst>")
			     			.append("</GetVanStockLogDetail>")
								.append(SOAP_FOOTER) ;
				return strXML.toString();
			}
			 
		   
			// Customer History
			public static String getAllTransferDate(String salesmanCode,String LastSync)
			{
				StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
				  			  .append("<GetAllTransfers xmlns=\"http://tempuri.org/\">")
				  			  .append("<SalesmanCode>").append(salesmanCode).append("</SalesmanCode>")
			      			  .append("<Type>").append(1).append("</Type>")
			      			  .append("<LastSyncDate>").append(LastSync).append("</LastSyncDate>")
			      			  .append("</GetAllTransfers>")
						.append(SOAP_FOOTER) ;
			    return strXML.toString();
			}
	//GetMasterData
//	public static String getMasterDate(String empNo)
//	{
//		String strXML = SOAP_HEADER+
//						"<GetMasterDataFile xmlns=\"http://tempuri.org/\">" +
//						"<UserCode>"+empNo+"</UserCode>"+
//						 "<lsd>0</lsd>"+
//					      "<lst>0</lst>"+
//						"</GetMasterDataFile>"+
//	      				SOAP_FOOTER;;
//	      				LogUtils.errorLog("strXML",""+strXML);
//		return strXML;
//	}
//	public static String getDiscount(String salemanCode, String lsd,  String lst)
//	{
//		String strXML = SOAP_HEADER+
//				 "<GetDiscounts xmlns=\"http://tempuri.org/\">"+
//				      "<UserCode>"+salemanCode+"</UserCode>"+
//				      "<lsd>"+lsd+"</lsd>"+
//				      "<lst>"+lst+"</lst>"+
//				    "</GetDiscounts>"+
//	      				SOAP_FOOTER;;
//	      				LogUtils.errorLog("strXML",""+strXML);
//		return strXML;
//	}
//	public static String getCustomerGroupById(String UserId, String strSyncTime)
//	{
//		LogUtils.errorLog("getCustomerGroupById - ", "getCustomerGroupById"+strSyncTime);
//		String strXML =  SOAP_HEADER+"<GetCustomerGroupByUserId xmlns=\"http://tempuri.org/\">" +
//							"<UserId>"+UserId+"</UserId>" +
//							"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
//						"</GetCustomerGroupByUserId>"+
//	      				SOAP_FOOTER;;
//	      				LogUtils.errorLog("strXML",""+strXML);
//		return strXML;
//	}
//	
//	public static String getetCustomersByUserIdWithoutInvoice(String UserId, String strSyncTime)
//	{
//		LogUtils.errorLog("GetCustomersByUserIdWithoutInvoice  - ", ""+strSyncTime);
//		String strXML =  SOAP_HEADER+
//						"<GetCustomersByUserIdWithoutInvoice xmlns=\"http://tempuri.org/\">" +
//						"<UserId>"+UserId+"</UserId>" +
//						"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
//						"</GetCustomersByUserIdWithoutInvoice>"+
//						SOAP_FOOTER;;
//		return strXML;
//	}
//	public static String getetCustomersPendingInvoice(String UserId, String strSyncTime)
//	{
//		LogUtils.errorLog("GetPendingSalesInvoice   - ", "strSyncTime - "+strSyncTime);
//		String strXML =  SOAP_HEADER+
//						"<GetPendingSalesInvoice xmlns=\"http://tempuri.org/\">" +
//						"<UserId>"+UserId+"</UserId>" +
//						"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
//						"</GetPendingSalesInvoice>"+
//						SOAP_FOOTER;;
//		return strXML;
//	}
//	public static String getTopSelling(String Selesman, String strSyncTime)
//	{
//		LogUtils.errorLog("getTopSelling - ", ""+strSyncTime);
//		String strXML =  SOAP_HEADER+
//						"<GetTopSellingItems xmlns=\"http://tempuri.org/\">" +
//						"<SALESMAN>"+Selesman+"</SALESMAN>" +
//						"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
//						"</GetTopSellingItems>"+
//						SOAP_FOOTER;;
//		return strXML;
//	}
//	
//	public static String getMustHave(String Selesman, String strSyncTime)
//	{
//		LogUtils.errorLog("getMustHave - ", ""+strSyncTime);
//		String strXML =  SOAP_HEADER+
//						"<GetMustHaveItems xmlns=\"http://tempuri.org/\">" +
//						"<SALESMAN>"+Selesman+"</SALESMAN>" +
//						"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
//						"</GetMustHaveItems>"+
//						SOAP_FOOTER;;
//		return strXML;
//	}
//
//	//method to get the Journey plan by Salesman Id
//	public static String getJourneyPlan(String SalesmanId, String strSyncTime)
//	{
//		LogUtils.errorLog("getJourneyPlan - ", ""+strSyncTime);
//		String strXML =  SOAP_HEADER+"<GetBeats xmlns=\"http://tempuri.org/\">"+
//						"<SalesManCode>"+SalesmanId+"</SalesManCode>"+
//						"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
//						"</GetBeats>"+
//						SOAP_FOOTER;;
//		return strXML;
//	}
//	
//	//AddNotes
//	public static String inserNotes(NotesObject objNotesObject)
//	{
//		String strXML = SOAP_HEADER+
//							"<InsertNote xmlns=\"http://tempuri.org/\">" +
//								"<CustomerId>"+objNotesObject.Customer_ID+"</CustomerId>" +
//								"<PresellerId>"+objNotesObject.Emp_Id+"</PresellerId>" +
//								"<Subject>"+objNotesObject.Note_Title+"</Subject>" +
//								"<Description>"+objNotesObject.Note_Description+"</Description>" +
//								"<Image>"+objNotesObject.image+"</Image>" +
//							"</InsertNote>"+
//						SOAP_FOOTER;
//		
//		return strXML;
//	}
//
//	 //AllRoles
//	  public static String getAllRoles()
//	  {
//		String strXML = SOAP_HEADER + "<GetAllRoles xmlns=\"http://tempuri.org/\" />"+
//		SOAP_FOOTER ;
//		return strXML;
//	  }
//		
//	//AllUsers
//	public static String getAllUsers(String strSyncTime)
//	{
//		LogUtils.errorLog("getAllUsers - ", ""+strSyncTime);
//		String strXML = SOAP_HEADER +
//		  			   "<GetAllUsers xmlns=\"http://tempuri.org/\">"+
//	      			   "<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
//	      			   "</GetAllUsers>"+
//				SOAP_FOOTER ;
//	    return strXML;
//	}
//	
//	
//	//PresellerTargets
//	public static String getPresellerTargets(String strSyncTime)
//	{
//		LogUtils.errorLog("getPresellerTargets - ", ""+strSyncTime);
//		String strXML = SOAP_HEADER +
//		  			   "<GetPreSalerAchievedTargetBySalesmancode xmlns=\"http://tempuri.org/\">"+
//	      			   "<Salesmancode>"+strSyncTime+"</Salesmancode>"+
//	      			   "</GetPreSalerAchievedTargetBySalesmancode>"+
//				SOAP_FOOTER ;
//	    return strXML;
//	}
//	
//	// Customer History
//	public static String getCustomerHistory(String EmpNo)
//	{
//		LogUtils.errorLog("getCustomerHistory - ", ""+EmpNo);
//		String strXML = SOAP_HEADER +
//		  			   "<getCustomerHistory xmlns=\"http://tempuri.org/\">"+
//	      			   "<SalesManCode>"+EmpNo+"</SalesManCode>"+
//	      			   "</getCustomerHistory>"+
//				SOAP_FOOTER ;
//		LogUtils.errorLog("strXML", "strXML"+strXML);
//	    return strXML;
//	}
//	
//	// Customer History
//	public static String getCustomerHistoryWithSync(String EmpNo,String LastSync)
//	{
//		LogUtils.errorLog("getCustomerHistorywithSync - ", "getCustomerHistorywithSync "+LastSync);
//		String strXML = SOAP_HEADER +
//		  			   "<getCustomerHistorywithSync xmlns=\"http://tempuri.org/\">"+
//	      			   "<SalesManCode>"+EmpNo+"</SalesManCode>"+
//	      			   "<LastSync>"+LastSync+"</LastSync>"+
//	      			   "</getCustomerHistorywithSync>"+
//				SOAP_FOOTER ;
//		LogUtils.errorLog("strXML", "strXML"+strXML);
//	    return strXML;
//	}
//	
//	//Competetor Survey
//	public static String getCompetitorDetail(String empNo, String lastSyncDate)
//	{
//		String strXML = SOAP_HEADER+
//						"<GetCompetitorDetail xmlns=\"http://tempuri.org/\">"+
//	    				"<LastSyncDate>"+lastSyncDate+"</LastSyncDate>"+
//	    				"<EmpId>"+empNo+"</EmpId>"+
//	    				"</GetCompetitorDetail>"+
//						SOAP_FOOTER;
//		LogUtils.errorLog("DeleteReceiptDetailsFromApp",""+strXML);
//		return strXML;
//	}
//	
//	public static String getAllMovement(String empNo, String lastSyncDate)
//	{
//		String strXML = SOAP_HEADER+
//						"<GetAllMovements_Sync  xmlns=\"http://tempuri.org/\">"+
//						"<UserCode>"+empNo+"</UserCode>"+
//	    				"<LastSyncDate>"+lastSyncDate+"</LastSyncDate>"+
//	    				"</GetAllMovements_Sync >"+
//						SOAP_FOOTER;
//		return strXML;
//	}
//	
//	public static String getActiveStatus(String empNo, String lsd, String lst)
//	{
//		String strXML = SOAP_HEADER+
//						"<GetAppActiveStatus  xmlns=\"http://tempuri.org/\">"+
//						"<UserCode>"+empNo+"</UserCode>"+
//	    				"<lsd>"+lsd+"</lsd>"+
//	    				"<lst>"+lst+"</lst>"+
//	    				"</GetAppActiveStatus>"+
//						SOAP_FOOTER;
//		return strXML;
//	}
//	
//	
//	public static String getVanStockLogDetail(String userCode, String lsd, String lst)
//	{
//		String strXML = SOAP_HEADER+
//						"<GetVanStockLogDetail xmlns=\"http://tempuri.org/\">"+
//	     				"<UserCode>"+userCode+"</UserCode>"+
//	     				"<lsd>"+lsd+"</lsd>"+
//	    				"<lst>"+lst+"</lst>"+
//	     				"</GetVanStockLogDetail>"+
//						SOAP_FOOTER;
//		return strXML;
//	}
//	 
//   
//	// Customer History
//	public static String getAllTransferDate(String salesmanCode,String LastSync)
//	{
//		String strXML = SOAP_HEADER +
//		  			   "<GetAllTransfers xmlns=\"http://tempuri.org/\">"+
//		  			   "<SalesmanCode>"+salesmanCode+"</SalesmanCode>"+
//	      			   "<Type>"+1+"</Type>"+
//	      			   "<LastSyncDate>"+LastSync+"</LastSyncDate>"+
//	      			   "</GetAllTransfers>"+
//				SOAP_FOOTER ;
//	    return strXML;
//	}
	//AllUsers
	
			public static String sendAllOrders(Vector<TrxHeaderDO> vecCompleteOrders,
					String strEmpId) {

				StringBuilder strXML = new StringBuilder();

				strXML.append(SOAP_HEADER).append(
						"<PostTrxDetails xmlns=\"http://tempuri.org/\">");

				StringBuilder strOrder = new StringBuilder();
				StringBuilder strBody = new StringBuilder();

				strBody.append("<objTrxHeaderDcos>");

				for (TrxHeaderDO trxHeaderDO : vecCompleteOrders) {
					try {
						strOrder.append("<TrxHeaderDco>")
								.append("<AppTrxId>")
								.append(trxHeaderDO.appTrxId)
								.append("</AppTrxId>")
								.append("<BindTrxStatus></BindTrxStatus>")
								.append("<BindTrxType></BindTrxType>")
								.append("<ClientBranchCode>")
//								.append(trxHeaderDO.clientCode)
								.append(URLEncoder.encode(trxHeaderDO.clientCode,"UTF-8"))
								.append("</ClientBranchCode>")
								.append("<ClientCode>")
//							.append(trxHeaderDO.clientCode)
								.append(URLEncoder.encode(trxHeaderDO.clientCode,"UTF-8"))
								.append("</ClientCode>")
								.append("<ClientSignature>")
								.append(URLEncoder.encode("" + trxHeaderDO.clientSignature)
										+ "</ClientSignature>")
								.append("<CurrencyCode>")
								.append(trxHeaderDO.currencyCode)
								.append("</CurrencyCode>")
								.append("<DeliveryDate>")
								.append(trxHeaderDO.deliveryDate)
								.append("</DeliveryDate>")
								.append("<FinalAmount>0</FinalAmount>")
								.append("<FreeNote>")
								.append(trxHeaderDO.freeNote)
								.append("</FreeNote>")
								.append("<JourneyCode>")
								.append(trxHeaderDO.journeyCode)
								.append("</JourneyCode>")
								.append("<LPOCode>")
								.append(trxHeaderDO.lPOCode)
								.append("</LPOCode>")
								.append("<OrgCode>")
								.append(trxHeaderDO.orgCode)
								.append("</OrgCode>")
								.append("<PaymentCode></PaymentCode>")
								.append("<PaymentType>")
								.append(trxHeaderDO.paymentType)
								.append("</PaymentType>")
								.append("<PreTrxCode>")
								.append(trxHeaderDO.preTrxCode)
								.append("</PreTrxCode>")
								.append("<PrintingTimes>")
								.append(trxHeaderDO.printingTimes)
								.append("</PrintingTimes>")
								.append("<ReferenceCode>")
								.append(trxHeaderDO.referenceCode)
								.append("</ReferenceCode>")
								.append("<RemainingAmount>")
								.append(trxHeaderDO.remainingAmount)
								.append("</RemainingAmount>")
								.append("<SalesmanSignature>")
								.append(URLEncoder.encode(""
										+ trxHeaderDO.salesmanSignature)
										+ "</SalesmanSignature>")
								.append("<SettlementCode></SettlementCode>")
								.append("<TRXStatus>")
								.append(trxHeaderDO.trxStatus)
								.append("</TRXStatus>")
								.append("<TotalAmount>")
								.append(trxHeaderDO.totalAmount)
								.append("</TotalAmount>")
								.append("<TotalDiscountAmount>")
								.append(trxHeaderDO.totalDiscountAmount)
								.append("</TotalDiscountAmount>")
								.append("<TotalTAXAmount>")
								.append(trxHeaderDO.totalTAXAmount)
								.append("</TotalTAXAmount>")
								.append("<TrxCode>")
								.append(trxHeaderDO.trxCode)
								.append("</TrxCode>")
								.append("<TrxDate>")
								.append(trxHeaderDO.trxDate)
								.append("</TrxDate>")
								.append("<ApprovedDate>")
								.append(trxHeaderDO.trxDate)
								.append("</ApprovedDate>")
								.append("<TrxReasonCode>")
								.append(trxHeaderDO.trxReasonCode)
								.append("</TrxReasonCode>")
								.append("<TrxSequence></TrxSequence>")
								.append("<TrxType>")
								.append(trxHeaderDO.trxType)
								.append("</TrxType>")
								.append("<UserCode>")
								.append(trxHeaderDO.userCode)
								.append("</UserCode>")
								.append("<UserCreditAccountCode>")
								.append(trxHeaderDO.userCreditAccountCode)
								.append("</UserCreditAccountCode>")
								.append("<VisitCode>")
								.append(trxHeaderDO.visitCode)
								.append("</VisitCode>")
								.append("<VisitID>0</VisitID>")
								.append("<GeoX>")
								.append(trxHeaderDO.geoLatitude)
								.append("</GeoX>")
								.append("<GeoY>")
								.append(trxHeaderDO.geoLongitude)
								.append("</GeoY>")
								.append(getProductDetailsNew(trxHeaderDO.arrTrxDetailsDOs,
										trxHeaderDO))
								.append(getProductPromotionsNew(trxHeaderDO.arrPromotionDOs))
								// .append(getProductImages(trxHeaderDO.vecProductDO))
								.append("</TrxHeaderDco>");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				strBody.append(strOrder);
				strXML.append(strBody).append("</objTrxHeaderDcos>")
						.append("</PostTrxDetails>").append(SOAP_FOOTER);

				System.out.println(strXML);

				return strXML.toString();
			}

			public static String postTrxDetailsFromXML(TrxHeaderDO trxHeaderDO, String strEmpId) 
			{
				StringBuilder strXML = new StringBuilder();

				strXML.append(SOAP_HEADER).append(
						"<PostTrxDetailsFromXML xmlns=\"http://tempuri.org/\">");

				StringBuilder strOrder = new StringBuilder();
				StringBuilder strBody = new StringBuilder();

				strBody.append("<objTrxHeaderDcos>");

//				for (TrxHeaderDO trxHeaderDO : vecCompleteOrders) {
					try
					{

						strOrder.append("<TrxHeaderDco>")
								.append("<AppTrxId>")
								.append(trxHeaderDO.appTrxId)
								.append("</AppTrxId>")
								.append("<BindTrxStatus></BindTrxStatus>")
								.append("<BindTrxType></BindTrxType>")
								.append("<ClientBranchCode>")
								.append(URLEncoder.encode(trxHeaderDO.clientCode,"UTF-8"))
								.append("</ClientBranchCode>")
								.append("<ClientCode>")
//								.append(trxHeaderDO.clientCode)
								.append(URLEncoder.encode(trxHeaderDO.clientCode,"UTF-8"))
								.append("</ClientCode>")
//								.append("<ClientSignature>")
//								.append(URLEncoder.encode("" + trxHeaderDO.clientSignature)
//										+ "</ClientSignature>")
								.append("<CurrencyCode>")
								.append(trxHeaderDO.currencyCode)
								.append("</CurrencyCode>")
								.append("<DeliveryDate>")
								.append(trxHeaderDO.deliveryDate)
								.append("</DeliveryDate>")
								.append("<FinalAmount>0</FinalAmount>")
								.append("<FreeNote>")
								.append(URLEncoder.encode(trxHeaderDO.freeNote))
								.append("</FreeNote>")
								.append("<JourneyCode>")
								.append(trxHeaderDO.journeyCode)
								.append("</JourneyCode>")
								.append("<LPOCode>")
								.append(""+trxHeaderDO.lPOCode)
								.append("</LPOCode>")
								.append("<OrgCode>")
								.append(trxHeaderDO.orgCode)
								.append("</OrgCode>")
								.append("<PaymentCode></PaymentCode>")
								.append("<PaymentType>")
								.append(trxHeaderDO.paymentType)
								.append("</PaymentType>")
								.append("<PreTrxCode>")
								.append(trxHeaderDO.preTrxCode)
								.append("</PreTrxCode>")
								.append("<PrintingTimes>")
								.append(trxHeaderDO.printingTimes)
								.append("</PrintingTimes>")
								.append("<ReferenceCode>")
								.append(trxHeaderDO.referenceCode)
								.append("</ReferenceCode>")
								.append("<RemainingAmount>")
								.append(trxHeaderDO.remainingAmount)
								.append("</RemainingAmount>")
//								.append("<SalesmanSignature>")
//								.append(URLEncoder.encode(""+ trxHeaderDO.salesmanSignature)+ "</SalesmanSignature>")
								.append("<SettlementCode></SettlementCode>")
								.append("<TRXStatus>")
								.append(trxHeaderDO.trxStatus)
								.append("</TRXStatus>")
								.append("<TotalAmount>")
								.append(trxHeaderDO.totalAmount)
								.append("</TotalAmount>")
								
								.append("<BranchPlantCode>")
								.append(trxHeaderDO.statementDiscount)
								.append("</BranchPlantCode>")
								
								.append("<TotalDiscountAmount>")
								.append(trxHeaderDO.totalDiscountAmount)
								.append("</TotalDiscountAmount>")
								.append("<TotalTAXAmount>")
								.append(trxHeaderDO.totalTAXAmount)
								.append("</TotalTAXAmount>")
								.append("<TrxCode>")
								.append(trxHeaderDO.trxCode)
								.append("</TrxCode>")
								.append("<TrxDate>")
								.append(trxHeaderDO.trxDate)
								.append("</TrxDate>")
								.append("<ApprovedDate>")
								.append(trxHeaderDO.trxDate)
								.append("</ApprovedDate>")
								.append("<TrxReasonCode>")
								.append(trxHeaderDO.trxReasonCode)
								.append("</TrxReasonCode>")
								.append("<TrxSequence></TrxSequence>")
								.append("<TrxType>")
								.append(trxHeaderDO.trxType)
								.append("</TrxType>")
								.append("<UserCode>")
								.append(trxHeaderDO.userCode)
								.append("</UserCode>")
								.append("<UserCreditAccountCode>")
								.append(trxHeaderDO.userCreditAccountCode)
								.append("</UserCreditAccountCode>")
								.append("<VisitCode>")
								.append(trxHeaderDO.visitCode)
								.append("</VisitCode>")
								.append("<VisitID>0</VisitID>")
								.append("<GeoX>")
								.append(trxHeaderDO.geoLatitude)
								.append("</GeoX>")
								.append("<GeoY>")
								.append(trxHeaderDO.geoLongitude)
								.append("</GeoY>")
								.append("<TrxSubType>")
								.append(trxHeaderDO.trxSubType+"")
								.append("</TrxSubType>")
								.append("<SplDisc>")
								.append(trxHeaderDO.specialDiscPercent+"")
								.append("</SplDisc>")
								.append("<LPO>")
								.append(URLEncoder.encode(trxHeaderDO.LPONo+""))
								.append("</LPO>")
//								.append("<Narrotion>")
//								.append(URLEncoder.encode(trxHeaderDO.Narration+""))
//								.append("</Narrotion>")
                                .append("<Narrotion>")
                                .append(trxHeaderDO.PromotionReason)
                                .append("</Narrotion>")
								.append("<Reason>")
								.append(URLEncoder.encode(trxHeaderDO.returnReason+""))
								.append("</Reason>")
								.append("<RateDiff>")
								.append(trxHeaderDO.rateDiff+"")
								.append("</RateDiff>")
								.append("<PresellerCode>")
								.append(trxHeaderDO.PreSellerUserCode+"")
								.append("</PresellerCode>")
								.append("<Division>")
								.append(trxHeaderDO.Division+"")
								.append("</Division>")
								.append("<VAT>")
								.append(trxHeaderDO.totalVATAmount+"")
								.append("</VAT>")
								.append(getProductDetailsNew(trxHeaderDO.arrTrxDetailsDOs,
										trxHeaderDO))
								.append(getProductPromotionsNew(trxHeaderDO.arrPromotionDOs))
								// .append(getProductImages(trxHeaderDO.vecProductDO))
								.append("</TrxHeaderDco>");
					
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
//				}
				strBody.append(strOrder);
				strXML.append(strBody).append("</objTrxHeaderDcos>")
						.append("</PostTrxDetailsFromXML>").append(SOAP_FOOTER);

				System.out.println(strXML);

				return strXML.toString();
			}
			
			public static String postTrxDetailFromXML(
					TrxHeaderDO trxHeaderDO, String strEmpId) {
				StringBuilder strXML = new StringBuilder();

				strXML.append(SOAP_HEADER).append(
						"<PostTrxDetailsFromXML xmlns=\"http://tempuri.org/\">");

				StringBuilder strOrder = new StringBuilder();
				StringBuilder strBody = new StringBuilder();

				strBody.append("<objTrxHeaderDcos>");

				if(trxHeaderDO != null)
				{
					try {
						strOrder.append("<TrxHeaderDco>")
								.append("<AppTrxId>")
								.append(trxHeaderDO.appTrxId)
								.append("</AppTrxId>")
								.append("<BindTrxStatus></BindTrxStatus>")
								.append("<BindTrxType></BindTrxType>")
								.append("<ClientBranchCode>")
								.append(URLEncoder.encode(trxHeaderDO.clientCode,"UTF-8"))
								.append("</ClientBranchCode>")
								.append("<ClientCode>")
								.append(URLEncoder.encode(trxHeaderDO.clientCode,"UTF-8"))
								.append("</ClientCode>")
								.append("<ClientSignature>")
								.append(URLEncoder.encode("" + trxHeaderDO.clientSignature)
										+ "</ClientSignature>")
								.append("<CurrencyCode>")
								.append(trxHeaderDO.currencyCode)
								.append("</CurrencyCode>")
								.append("<DeliveryDate>")
								.append(trxHeaderDO.deliveryDate)
								.append("</DeliveryDate>")
								.append("<FinalAmount>0</FinalAmount>")
								.append("<FreeNote>")
								.append(URLEncoder.encode(trxHeaderDO.freeNote))
								.append("</FreeNote>")
								.append("<JourneyCode>")
								.append(trxHeaderDO.journeyCode)
								.append("</JourneyCode>")
								.append("<LPOCode>")
								.append(""+trxHeaderDO.lPOCode)
								.append("</LPOCode>")
								.append("<OrgCode>")
								.append(trxHeaderDO.orgCode)
								.append("</OrgCode>")
								.append("<PaymentCode></PaymentCode>")
								.append("<PaymentType>")
								.append(trxHeaderDO.paymentType)
								.append("</PaymentType>")
								.append("<PreTrxCode>")
								.append(trxHeaderDO.preTrxCode)
								.append("</PreTrxCode>")
								.append("<PrintingTimes>")
								.append(trxHeaderDO.printingTimes)
								.append("</PrintingTimes>")
								.append("<ReferenceCode>")
								.append(trxHeaderDO.referenceCode)
								.append("</ReferenceCode>")
								.append("<RemainingAmount>")
								.append(trxHeaderDO.remainingAmount)
								.append("</RemainingAmount>")
								.append("<SalesmanSignature>")
								.append(URLEncoder.encode(""+ trxHeaderDO.salesmanSignature)+ "</SalesmanSignature>")
								.append("<SettlementCode></SettlementCode>")
								.append("<TRXStatus>")
								.append(trxHeaderDO.trxStatus)
								.append("</TRXStatus>")
								.append("<TotalAmount>")
								.append(trxHeaderDO.totalAmount)
								.append("</TotalAmount>")
								.append("<TotalDiscountAmount>")
								.append(trxHeaderDO.totalDiscountAmount)
								.append("</TotalDiscountAmount>")
								.append("<TotalTAXAmount>")
								.append(trxHeaderDO.totalTAXAmount)
								.append("</TotalTAXAmount>")
								.append("<TrxCode>")
								.append(trxHeaderDO.trxCode)
								.append("</TrxCode>")
								.append("<TrxDate>")
								.append(trxHeaderDO.trxDate)
								.append("</TrxDate>")
								.append("<ApprovedDate>")
								.append(trxHeaderDO.trxDate)
								.append("</ApprovedDate>")
								.append("<TrxReasonCode>")
								.append(trxHeaderDO.trxReasonCode)
								.append("</TrxReasonCode>")
								.append("<TrxSequence></TrxSequence>")
								.append("<TrxType>")
								.append(trxHeaderDO.trxType)
								.append("</TrxType>")
								.append("<UserCode>")
								.append(trxHeaderDO.userCode)
								.append("</UserCode>")
								.append("<UserCreditAccountCode>")
								.append(trxHeaderDO.userCreditAccountCode)
								.append("</UserCreditAccountCode>")
								.append("<VisitCode>")
								.append(trxHeaderDO.visitCode)
								.append("</VisitCode>")
								.append("<VisitID>0</VisitID>")
								.append("<GeoX>")
								.append(trxHeaderDO.geoLatitude)
								.append("</GeoX>")
								.append("<GeoY>")
								.append(trxHeaderDO.geoLongitude)
								.append("</GeoY>")
								.append("<TrxSubType>")
								.append(trxHeaderDO.trxSubType+"")
								.append("</TrxSubType>")
								.append("<SplDisc>")
								.append(trxHeaderDO.specialDiscPercent+"")
								.append("</SplDisc>")
								.append("<LPO>")
								.append(URLEncoder.encode(trxHeaderDO.LPONo+""))
								.append("</LPO>")
								.append("<Narrotion>")
								.append(URLEncoder.encode(trxHeaderDO.Narration+""))
								.append("</Narrotion>")
								.append("<Reason>")
								.append(URLEncoder.encode(trxHeaderDO.returnReason+""))
								.append("</Reason>")
								.append("<RateDiff>")
								.append(trxHeaderDO.rateDiff+"")
								.append("</RateDiff>")
								.append("<PresellerCode>")
								.append(trxHeaderDO.PreSellerUserCode+"")
								.append("</PresellerCode>")
								.append("<Division>")
								.append(trxHeaderDO.Division+"")
								.append("</Division>")
								.append(getProductDetailsNew(trxHeaderDO.arrTrxDetailsDOs,
										trxHeaderDO))
								.append(getProductPromotionsNew(trxHeaderDO.arrPromotionDOs))
								// .append(getProductImages(trxHeaderDO.vecProductDO))
								.append("</TrxHeaderDco>");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				strBody.append(strOrder);
				strXML.append(strBody).append("</objTrxHeaderDcos>")
						.append("</PostTrxDetailsFromXML>").append(SOAP_FOOTER);

				System.out.println(strXML);

				return strXML.toString();
			}
			
			public static String postTrxDetails(
					Vector<TrxHeaderDO> vecCompleteOrders, String strEmpId) {
				StringBuilder strXML = new StringBuilder();

				strXML.append(SOAP_HEADER).append(
						"<PostTrxDetails xmlns=\"http://tempuri.org/\">");

				StringBuilder strOrder = new StringBuilder();
				StringBuilder strBody = new StringBuilder();

				strBody.append("<objTrxHeaderDcos>");

				for (TrxHeaderDO trxHeaderDO : vecCompleteOrders) {
					try {
						strOrder.append("<TrxHeaderDco>")
								.append("<AppTrxId>")
								.append(trxHeaderDO.appTrxId)
								.append("</AppTrxId>")
								.append("<BindTrxStatus></BindTrxStatus>")
								.append("<BindTrxType></BindTrxType>")
								.append("<ClientBranchCode>")
								.append(URLEncoder.encode(trxHeaderDO.clientCode,"UTF-8"))
								.append("</ClientBranchCode>")
								.append("<ClientCode>")
								.append(URLEncoder.encode(trxHeaderDO.clientCode,"UTF-8"))
								.append("</ClientCode>")
								.append("<ClientSignature>")
								.append(URLEncoder.encode("" + trxHeaderDO.clientSignature)
										+ "</ClientSignature>")
								.append("<CurrencyCode>")
								.append(trxHeaderDO.currencyCode)
								.append("</CurrencyCode>")
								.append("<DeliveryDate>")
								.append(trxHeaderDO.deliveryDate)
								.append("</DeliveryDate>")
								.append("<FinalAmount>0</FinalAmount>")
								.append("<FreeNote>")
								.append(trxHeaderDO.freeNote)
								.append("</FreeNote>")
								.append("<JourneyCode>")
								.append(trxHeaderDO.journeyCode)
								.append("</JourneyCode>")
								.append("<LPOCode>")
								.append(trxHeaderDO.lPOCode)
								.append("</LPOCode>")
								.append("<OrgCode>")
								.append(trxHeaderDO.orgCode)
								.append("</OrgCode>")
								.append("<PaymentCode></PaymentCode>")
								.append("<PaymentType>")
								.append(trxHeaderDO.paymentType)
								.append("</PaymentType>")
								.append("<PreTrxCode>")
								.append(trxHeaderDO.preTrxCode)
								.append("</PreTrxCode>")
								.append("<PrintingTimes>")
								.append(trxHeaderDO.printingTimes)
								.append("</PrintingTimes>")
								.append("<ReferenceCode>")
								.append(trxHeaderDO.referenceCode)
								.append("</ReferenceCode>")
								.append("<RemainingAmount>")
								.append(trxHeaderDO.remainingAmount)
								.append("</RemainingAmount>")
								.append("<SalesmanSignature>")
								.append(URLEncoder.encode(""
										+ trxHeaderDO.salesmanSignature)
										+ "</SalesmanSignature>")
								.append("<SettlementCode></SettlementCode>")
								.append("<TRXStatus>")
								.append(trxHeaderDO.trxStatus)
								.append("</TRXStatus>")
								.append("<TotalAmount>")
								.append(trxHeaderDO.totalAmount)
								.append("</TotalAmount>")
								.append("<TotalDiscountAmount>")
								.append(trxHeaderDO.totalDiscountAmount)
								.append("</TotalDiscountAmount>")
								.append("<TotalTAXAmount>")
								.append(trxHeaderDO.totalTAXAmount)
								.append("</TotalTAXAmount>")
								.append("<TrxCode>")
								.append(trxHeaderDO.trxCode)
								.append("</TrxCode>")
								.append("<TrxDate>")
								.append(trxHeaderDO.trxDate)
								.append("</TrxDate>")
								.append("<ApprovedDate>")
								.append(trxHeaderDO.trxDate)
								.append("</ApprovedDate>")
								.append("<TrxReasonCode>")
								.append(trxHeaderDO.trxReasonCode)
								.append("</TrxReasonCode>")
								.append("<TrxSequence></TrxSequence>")
								.append("<TrxType>")
								.append(trxHeaderDO.trxType)
								.append("</TrxType>")
								.append("<UserCode>")
								.append(trxHeaderDO.userCode)
								.append("</UserCode>")
								.append("<UserCreditAccountCode>")
								.append(trxHeaderDO.userCreditAccountCode)
								.append("</UserCreditAccountCode>")
								.append("<VisitCode>")
								.append(trxHeaderDO.visitCode)
								.append("</VisitCode>")
								.append("<VisitID>0</VisitID>")
								.append("<GeoX>")
								.append(trxHeaderDO.geoLatitude)
								.append("</GeoX>")
								.append("<GeoY>")
								.append(trxHeaderDO.geoLongitude)
								.append("</GeoY>")
								.append("<AdvRefTrxCode>")
								.append(trxHeaderDO.referenceCode+"")
								.append("</AdvRefTrxCode>")
								.append("<TrxSubType>")
								.append(trxHeaderDO.trxSubType+"")
								.append("</TrxSubType>")
								.append(getProductDetailsNew(trxHeaderDO.arrTrxDetailsDOs,
										trxHeaderDO))
								.append(getProductPromotionsNew(trxHeaderDO.arrPromotionDOs))
								// .append(getProductImages(trxHeaderDO.vecProductDO))
								.append("</TrxHeaderDco>");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				strBody.append(strOrder);
				strXML.append(strBody).append("</objTrxHeaderDcos>")
						.append("</PostTrxDetails>").append(SOAP_FOOTER);

				System.out.println(strXML);

				return strXML.toString();
			}

			private static String getProductDetailsNew(
					ArrayList<TrxDetailsDO> arrTrxDetailsDOs, TrxHeaderDO orderDO) {
				StringBuilder strXML = new StringBuilder();
				strXML.append("<TrxDetails>");
				if (arrTrxDetailsDOs != null && arrTrxDetailsDOs.size() > 0) {
					for (TrxDetailsDO trxDetailsDO : arrTrxDetailsDOs) {
						try {
							strXML.append("<TrxDetailDco>")
									.append("<AffectedStock>")
									.append(trxDetailsDO.affectedStock)
									.append("</AffectedStock>")
									.append("<ApprovedBU>")
									.append(trxDetailsDO.approvedBU)
									.append("</ApprovedBU>")
									.append("<BasePrice>")
									.append(trxDetailsDO.basePrice)
									.append("</BasePrice>")
									.append("<CalculatedDiscountAmount>")
									.append(trxDetailsDO.calculatedDiscountAmount)
									.append("</CalculatedDiscountAmount>")
									.append("<CalculatedDiscountPercentage>")
									.append(trxDetailsDO.calculatedDiscountPercentage)
									.append("</CalculatedDiscountPercentage>")
									.append("<CancelledQuantity>")
									.append(trxDetailsDO.cancelledQuantity)
									.append("</CancelledQuantity>")
									.append("<CollectedBU>")
									.append(trxDetailsDO.collectedBU)
									.append("</CollectedBU>")
									.append("<DistributionCode>")
									.append(trxDetailsDO.distributionCode)
									.append("</DistributionCode>")
									.append("<ExpiryDate>")
									.append(trxDetailsDO.expiryDate)
									.append("</ExpiryDate>")
									.append("<FinalBU>")
									.append(trxDetailsDO.finalBU)
									.append("</FinalBU>")
									.append("<InProcessQuantity>")
									.append(trxDetailsDO.inProcessQuantity)
									.append("</InProcessQuantity>")
									.append("<ItemAltDescription>")
									.append(URLEncoder.encode(trxDetailsDO.itemAltDescription,"UTF-8"))
									.append("</ItemAltDescription>")
									.append("<ItemCode>")
									.append(trxDetailsDO.itemCode)
									.append("</ItemCode>")
									.append("<ItemDescription>")
									.append(URLEncoder.encode(
											trxDetailsDO.itemDescription, "UTF-8"))
									.append("</ItemDescription>")
									.append("<ItemGroupLevel5>")
									.append(trxDetailsDO.itemGroupLevel5)
									.append("</ItemGroupLevel5>")
									.append("<ItemType>")
									.append(trxDetailsDO.itemType)
									.append("</ItemType>")
									.append("<LineNo>")
									.append(trxDetailsDO.lineNo)
									.append("</LineNo>")
									.append("<OrgCode>")
									.append(trxDetailsDO.orgCode)
									.append("</OrgCode>")
									.append("<PriceUsedLevel1>")
									.append(trxDetailsDO.priceUsedLevel1)
									.append("</PriceUsedLevel1>")
									.append("<PriceUsedLevel2>")
									.append(trxDetailsDO.priceUsedLevel2)
									.append("</PriceUsedLevel2>")
									.append("<PriceUsedLevel3>")
									.append(trxDetailsDO.priceUsedLevel3)
									.append("</PriceUsedLevel3>")
									.append("<PromoID>")
									.append(trxDetailsDO.promoID)
									.append("</PromoID>")
									.append("<PromoType>")
									.append(trxDetailsDO.promoType)
									.append("</PromoType>")
									.append("<QuantityBU>")
									.append(trxDetailsDO.quantityBU)
									.append("</QuantityBU>")
									.append("<MissedBU>")
									.append(trxDetailsDO.missedBU)
									.append("</MissedBU>")
									.append("<QuantityLevel1>")
									.append(trxDetailsDO.quantityLevel1)
									.append("</QuantityLevel1>")
									.append("<QuantityLevel2>")
									.append(trxDetailsDO.quantityLevel2)
									.append("</QuantityLevel2>")
									.append("<QuantityLevel3>")
									.append(trxDetailsDO.quantityLevel3)
									.append("</QuantityLevel3>")
									.append("<Reason>")
									.append(trxDetailsDO.reason)
									.append("</Reason>")
									.append("<RelatedLineID>")
									.append(trxDetailsDO.relatedLineID)
									.append("</RelatedLineID>")
									.append("<RequestedBU>")
									.append(trxDetailsDO.requestedBU)
									.append("</RequestedBU>")
									.append("<ShippedQuantity>")
									.append(trxDetailsDO.shippedQuantity)
									.append("</ShippedQuantity>")
									.append("<SuggestedBU>")
									.append(trxDetailsDO.suggestedBU)
									.append("</SuggestedBU>")
									.append("<TRXStatus>")
									.append(trxDetailsDO.trxStatus)
									.append("</TRXStatus>")
									.append("<TaxPercentage>")
									.append(trxDetailsDO.taxPercentage)
									.append("</TaxPercentage>")
									.append("<TaxType>")
									.append(trxDetailsDO.taxType)
									.append("</TaxType>")
									.append("<TotalDiscountAmount>")
									.append(trxDetailsDO.totalDiscountAmount)
//									.append(trxDetailsDO.calculatedDiscountAmount)
									.append("</TotalDiscountAmount>")
									.append("<TotalDiscountPercentage>")
									.append(trxDetailsDO.totalDiscountPercentage)
//									.append(trxDetailsDO.calculatedDiscountPercentage)
									.append("</TotalDiscountPercentage>")
									.append("<TrxCode>")
									.append(trxDetailsDO.trxCode)
									.append("</TrxCode>")
									.append("<TrxDetailsNote>")
									.append(URLEncoder.encode(
											trxDetailsDO.trxDetailsNote, "UTF-8"))
									.append("</TrxDetailsNote>")
									.append("<TrxReasonCode>")
									.append(trxDetailsDO.trxReasonCode)
									.append("</TrxReasonCode>").append("<UOM>")
									.append(trxDetailsDO.UOM).append("</UOM>")
									.append("<UserDiscountAmount>")
									.append(trxDetailsDO.userDiscountAmount)
									.append("</UserDiscountAmount>")
									.append("<UserDiscountPercentage>")
									.append(trxDetailsDO.userDiscountPercentage)
									.append("</UserDiscountPercentage>")
									.append("<VAT>")
									.append(trxDetailsDO.vatPercentage+"")
									.append("</VAT>")
									.append("<TotalVATAmount>")
									.append(trxDetailsDO.VATAmountNew+"")
									.append("</TotalVATAmount>")
									.append("<BatchNumber>")
									.append(trxDetailsDO.batchCode)
									.append("</BatchNumber>").append("</TrxDetailDco>");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				strXML.append("</TrxDetails>");
				// System.out.println(strXML.toString());
				return strXML.toString();
			}

			private static String getProductPromotionsNew(
					ArrayList<TrxPromotionDO> arrPromotionDOs) {
				StringBuilder strXML = new StringBuilder();
				// String strXML = "";

				if (arrPromotionDOs != null && arrPromotionDOs.size() > 0) {
					strXML.append("<TrxPromotions>");
					for (TrxPromotionDO productDO : arrPromotionDOs) {
						strXML.append("<TrxPromotionDco >").append("<CreatedOn>")
								.append(productDO.createdOn).append("</CreatedOn>")
								.append("<DiscountAmount>")
								.append(productDO.discountAmount)
								.append("</DiscountAmount>")
								.append("<DiscountPercentage>")
								.append(productDO.discountPercentage)
								.append("</DiscountPercentage>")
								.append("<FactSheetCode>")
								.append(productDO.factSheetCode)
								.append("</FactSheetCode>").append("<IsStructural>")
								.append(productDO.isStructural)
								.append("</IsStructural>").append("<ItemCode>")
								.append(productDO.itemCode).append("</ItemCode>")
								.append("<ItemType>").append(productDO.itemType)
								.append("</ItemType>").append("<OrgCode>")
								.append(productDO.orgCode).append("</OrgCode>")
								.append("<PromotionID>").append(productDO.promotionID)
								.append("</PromotionID>").append("<PromotionType>")
								.append(productDO.promotionType)
								.append("</PromotionType>").append("<PushedOn>")
								.append(productDO.pushedOn).append("</PushedOn>")
								.append("<Status>").append(productDO.status)
								.append("</Status>").append("<TrxCode>")
								.append(productDO.trxCode).append("</TrxCode>")
								.append("<TrxDetailsLineNo>")
								.append(productDO.trxDetailsLineNo)
								.append("</TrxDetailsLineNo>").append("<TrxStatus>")
								.append(productDO.trxStatus).append("</TrxStatus>")
								.append("</TrxPromotionDco >");
					}
					strXML.append("</TrxPromotions>");
				}
				// System.out.println(strXML);
				return strXML.toString();
			}
			
//	public static String sendAllOrders(Vector<TrxHeaderDO> vecCompleteOrders, String strEmpId)
//	{
//		LogUtils.errorLog("strEmpId", "strEmpId - "+strEmpId);
//		String strOrder  = ""; 
//		String strXML = SOAP_HEADER + "<PostTrxDetails xmlns=\"http://tempuri.org/\">";
//				String strBody = "<objTrxHeaderDcos>";
//  							  		for(TrxHeaderDO trxHeaderDO : vecCompleteOrders)
//  							  		{
//								  		strOrder  = strOrder+"<TrxHeaderDco>" +
//								  		"<AppTrxId>"+trxHeaderDO.appTrxId+"</AppTrxId>" +
//								  		"<BindTrxStatus></BindTrxStatus>" +
//								  		"<BindTrxType></BindTrxType>" +
//								  		"<ClientBranchCode>"+trxHeaderDO.clientCode+"</ClientBranchCode>" +
//								  		"<ClientCode>"+trxHeaderDO.clientCode+"</ClientCode>" +
//								  		"<ClientSignature>"+URLEncoder.encode(""+trxHeaderDO.clientSignature)+"</ClientSignature>" +
//								  		"<CurrencyCode>"+trxHeaderDO.currencyCode+"</CurrencyCode>" +
//								  		"<DeliveryDate>"+trxHeaderDO.deliveryDate+"</DeliveryDate>" +
//								  		"<FinalAmount>0</FinalAmount>" +
//								  		"<FreeNote>"+trxHeaderDO.freeNote+"</FreeNote>" +
//								  		"<JourneyCode>"+trxHeaderDO.journeyCode+"</JourneyCode>" +
//								  		"<LPOCode>"+trxHeaderDO.lPOCode+"</LPOCode>" +
//								  		"<OrgCode>"+trxHeaderDO.orgCode+"</OrgCode>" +
//								  		"<PaymentCode></PaymentCode>" +
//								  		"<PaymentType>"+trxHeaderDO.paymentType+"</PaymentType>" +
//								  		"<PreTrxCode>"+trxHeaderDO.preTrxCode+"</PreTrxCode>" +
//								  		"<PrintingTimes>"+trxHeaderDO.printingTimes+"</PrintingTimes>" +
//								  		"<ReferenceCode>"+trxHeaderDO.referenceCode+"</ReferenceCode>" +
//								  		"<RemainingAmount>"+trxHeaderDO.remainingAmount+"</RemainingAmount>" +
//								  		"<SalesmanSignature>"+URLEncoder.encode(""+trxHeaderDO.salesmanSignature)+"</SalesmanSignature>" +
//								  		"<SettlementCode></SettlementCode>" +
//								  		"<TRXStatus>"+trxHeaderDO.trxStatus+"</TRXStatus>" +
//								  		"<TotalAmount>"+trxHeaderDO.totalAmount+"</TotalAmount>" +
//								  		"<TotalDiscountAmount>"+trxHeaderDO.totalDiscountAmount+"</TotalDiscountAmount>" +
//								  		"<TotalTAXAmount>"+trxHeaderDO.totalTAXAmount+"</TotalTAXAmount>" +
//								  		"<TrxCode>"+trxHeaderDO.trxCode+"</TrxCode>" +
//								  		"<TrxDate>"+trxHeaderDO.trxDate+"</TrxDate>"+ 
//								  		"<ApprovedDate>"+trxHeaderDO.trxDate+"</ApprovedDate>"+ 
//								  		"<TrxReasonCode>"+trxHeaderDO.trxReasonCode+"</TrxReasonCode>" +
//								  		"<TrxSequence></TrxSequence>" +
//								  		"<TrxType>"+trxHeaderDO.trxType+"</TrxType>" +
//								  		"<UserCode>"+trxHeaderDO.userCode+"</UserCode>" +
//								  		"<UserCreditAccountCode>"+trxHeaderDO.userCreditAccountCode+"</UserCreditAccountCode>"+ 
//								  		"<VisitCode>"+trxHeaderDO.visitCode+"</VisitCode>" +
//								  		"<VisitID>0</VisitID>" +
//								  		"<GeoX>"+trxHeaderDO.geoLatitude+"</GeoX>" +
//								  		"<GeoY>"+trxHeaderDO.geoLongitude+"</GeoY>"+ 
//								  		getProductDetailsNew(trxHeaderDO.arrTrxDetailsDOs, trxHeaderDO)+
//								  		getProductPromotionsNew(trxHeaderDO.arrPromotionDOs)+
////								  		getProductImages(trxHeaderDO.vecProductDO)+
//    							  		"</TrxHeaderDco>";
//  							  		}
//	      					strBody = strBody+strOrder;
//	      					strXML = strXML+strBody+
//	      							  "</objTrxHeaderDcos>"+
//	      							  "</PostTrxDetails>"+
//	      							  SOAP_FOOTER ;
//	      					LogUtils.errorLog("strXML", ""+strXML);
//	      					
//	    return strXML;
//	}
	
//	public static String postTrxDetailsFromXML(Vector<TrxHeaderDO> vecCompleteOrders, String strEmpId)
//	{
//		LogUtils.errorLog("strEmpId", "strEmpId - "+strEmpId);
//		String strOrder  = ""; 
//		String strXML = SOAP_HEADER + "<PostTrxDetailsFromXML xmlns=\"http://tempuri.org/\">";
//				String strBody = "<objTrxHeaderDcos>";
//  							  		for(TrxHeaderDO trxHeaderDO : vecCompleteOrders)
//  							  		{
//								  		strOrder  = strOrder+"<TrxHeaderDco>" +
//								  		"<AppTrxId>"+trxHeaderDO.appTrxId+"</AppTrxId>" +
//								  		"<BindTrxStatus></BindTrxStatus>" +
//								  		"<BindTrxType></BindTrxType>" +
//								  		"<ClientBranchCode>"+trxHeaderDO.clientCode+"</ClientBranchCode>" +
//								  		"<ClientCode>"+trxHeaderDO.clientCode+"</ClientCode>" +
//								  		"<ClientSignature>"+URLEncoder.encode(""+trxHeaderDO.clientSignature)+"</ClientSignature>" +
//								  		"<CurrencyCode>"+trxHeaderDO.currencyCode+"</CurrencyCode>" +
//								  		"<DeliveryDate>"+trxHeaderDO.deliveryDate+"</DeliveryDate>" +
//								  		"<FinalAmount>0</FinalAmount>" +
//								  		"<FreeNote>"+trxHeaderDO.freeNote+"</FreeNote>" +
//								  		"<JourneyCode>"+trxHeaderDO.journeyCode+"</JourneyCode>" +
//								  		"<LPOCode>"+trxHeaderDO.lPOCode+"</LPOCode>" +
//								  		"<OrgCode>"+trxHeaderDO.orgCode+"</OrgCode>" +
//								  		"<PaymentCode></PaymentCode>" +
//								  		"<PaymentType>"+trxHeaderDO.paymentType+"</PaymentType>" +
//								  		"<PreTrxCode>"+trxHeaderDO.preTrxCode+"</PreTrxCode>" +
//								  		"<PrintingTimes>"+trxHeaderDO.printingTimes+"</PrintingTimes>" +
//								  		"<ReferenceCode>"+trxHeaderDO.referenceCode+"</ReferenceCode>" +
//								  		"<RemainingAmount>"+trxHeaderDO.remainingAmount+"</RemainingAmount>" +
//								  		"<SalesmanSignature>"+URLEncoder.encode(""+trxHeaderDO.salesmanSignature)+"</SalesmanSignature>" +
//								  		"<SettlementCode></SettlementCode>" +
//								  		"<TRXStatus>"+trxHeaderDO.trxStatus+"</TRXStatus>" +
//								  		"<TotalAmount>"+trxHeaderDO.totalAmount+"</TotalAmount>" +
//								  		"<TotalDiscountAmount>"+trxHeaderDO.totalDiscountAmount+"</TotalDiscountAmount>" +
//								  		"<TotalTAXAmount>"+trxHeaderDO.totalTAXAmount+"</TotalTAXAmount>" +
//								  		"<TrxCode>"+trxHeaderDO.trxCode+"</TrxCode>" +
//								  		"<TrxDate>"+trxHeaderDO.trxDate+"</TrxDate>"+ 
//								  		"<ApprovedDate>"+trxHeaderDO.trxDate+"</ApprovedDate>"+ 
//								  		"<TrxReasonCode>"+trxHeaderDO.trxReasonCode+"</TrxReasonCode>" +
//								  		"<TrxSequence></TrxSequence>" +
//								  		"<TrxType>"+trxHeaderDO.trxType+"</TrxType>" +
//								  		"<UserCode>"+trxHeaderDO.userCode+"</UserCode>" +
//								  		"<UserCreditAccountCode>"+trxHeaderDO.userCreditAccountCode+"</UserCreditAccountCode>"+ 
//								  		"<VisitCode>"+trxHeaderDO.visitCode+"</VisitCode>" +
//								  		"<VisitID>0</VisitID>" +
//								  		"<GeoX>"+trxHeaderDO.geoLatitude+"</GeoX>" +
//								  		"<GeoY>"+trxHeaderDO.geoLongitude+"</GeoY>"+ 
//								  		getProductDetailsNew(trxHeaderDO.arrTrxDetailsDOs, trxHeaderDO)+
//								  		getProductPromotionsNew(trxHeaderDO.arrPromotionDOs)+
////								  		getProductImages(trxHeaderDO.vecProductDO)+
//    							  		"</TrxHeaderDco>";
//  							  		}
//	      					strBody = strBody+strOrder;
//	      					strXML = strXML+strBody+
//	      							  "</objTrxHeaderDcos>"+
//	      							  "</PostTrxDetailsFromXML>"+
//	      							  SOAP_FOOTER ;
//	      					LogUtils.errorLog("strXML", ""+strXML);
//	      					
//	    return strXML;
//	}
			
			public static String sendFreeDeliveryOrders(
					Vector<OrderDO> vecCompleteOrders, String strEmpId) {
				DecimalFormat df = new DecimalFormat("##.##");
				// LogUtils.errorLog("strEmpId", "strEmpId - "+strEmpId);

				StringBuilder strXML = new StringBuilder();

				strXML.append(SOAP_HEADER)
						.append("<PostTrxDetailsFromXMLWithAuth xmlns=\"http://tempuri.org/\">");

				StringBuilder strOrder = new StringBuilder();
				StringBuilder strBody = new StringBuilder();
				strBody.append("<objTrxHeaderDcos>");

				for (OrderDO orderDO : vecCompleteOrders) {
					strOrder.append("<TrxHeaderDco>")
							.append("<OrderId></OrderId>")
							.append("<OrderNumber>")
							.append(orderDO.OrderId)
							.append("</OrderNumber>")
							.append("<AppId>")
							.append(orderDO.strUUID)
							.append("</AppId>")
							.append("<JourneyCode>")
							.append(orderDO.JourneyCode)
							.append("</JourneyCode>")
							.append("<VisitCode>")
							.append(orderDO.VisitCode)
							.append("</VisitCode>")
							.append("<EmpNo>")
							.append(orderDO.empNo)
							.append("</EmpNo>")
							.append("<Site_Number>")
							.append(orderDO.CustomerSiteId)
							.append("</Site_Number>")
							.append("<Site_Name>")
							.append(orderDO.strCustomerName)
							.append("</Site_Name>")
							.append("<Order_Date>")
							.append(orderDO.InvoiceDate)
							.append("</Order_Date>")
							.append("<Order_Type>")
							.append(orderDO.orderType)
							.append("</Order_Type>")
							.append("<OrderType>")
							.append(orderDO.orderSubType)
							.append("</OrderType>")
							.append("<OrderBookingType>")
							.append(orderDO.orderSubType)
							.append("</OrderBookingType>")
							.append("<CurrencyCode>")
							.append(orderDO.CurrencyCode)
							.append("</CurrencyCode>")
							.append("<PaymentType>")
							.append(orderDO.PaymentType)
							.append("</PaymentType>")
							.append("<TrxReasonCode>")
							.append(orderDO.TrxReasonCode)
							.append("</TrxReasonCode>")
							.append("<RemainingAmount>")
							.append("0")
							.append("</RemainingAmount>")
							.append("<TotalAmount>0</TotalAmount>")
							.append("<TotalDiscountAmount>0</TotalDiscountAmount>")
							.append("<TotalTAXAmount>")
							.append("0")
							.append("</TotalTAXAmount>")
							.append("<ReferenceCode>")
							.append("0")
							.append("</ReferenceCode>")
							.append("<CustomerSignature>")
							.append(URLEncoder.encode("" + orderDO.strCustomerSign)
									+ "</CustomerSignature>")
							.append("<SalesmanSignature>")
							.append(URLEncoder.encode("" + orderDO.strPresellerSign)
									+ "</SalesmanSignature>")
							.append("<Status>")
							.append("0")
							.append("</Status>")
							.append("<VisitID>")
							.append(orderDO.VisitCode)
							.append("</VisitID>")
							.append("<VehicleCode>")
							.append(orderDO.vehicleNo)
							.append("</VehicleCode>")
							.append("<DeliveredBy>")
							.append(orderDO.salesmanCode)
							.append("</DeliveredBy>")
							.append("<FreeNote>")
							.append("N/A")
							.append("</FreeNote>")
							.append("<PreTrxCode>")
							.append("0")
							.append("</PreTrxCode>")
							.append("<TRXStatus>")
							.append(orderDO.TRXStatus)
							.append("</TRXStatus>")
							.append("<ConsolidatedTrxCode>")
							.append("0")
							.append("</ConsolidatedTrxCode>")
							.append("<BranchPlantCode>")
							.append("0")
							.append("</BranchPlantCode>")
							.append("<PaymentCode>")
							.append(orderDO.PaymentCode)
							.append("</PaymentCode>")
							.append("<ApproveByCode>")
							.append("0")
							.append("</ApproveByCode>")
							.append("<ApprovedDate>")
							.append(orderDO.InvoiceDate)
							.append("</ApprovedDate>")
							.append("<LPOCode>")
							.append(orderDO.LPOCode)
							.append("</LPOCode>")
							.append("<TrxSequence>")
							.append(orderDO.OrderId)
							.append("</TrxSequence>")
							.append("<StampImage>")
							.append(URLEncoder.encode("" + orderDO.StampImage))
							.append("</StampImage>")
							.append("<DeliveryDate>")
							.append(orderDO.DeliveryDate)
							.append("</DeliveryDate>")
							.append("<StampDate>")
							.append(orderDO.InvoiceDate)
							.append("</StampDate>")
							.append("<UserCreditAccountCode>")
							.append("0")
							.append("</UserCreditAccountCode>")
							.append("<PushedOn>")
							.append(orderDO.InvoiceDate)
							.append("</PushedOn>")
							.append("<SettlementCode>")
							.append("0")
							.append("</SettlementCode>")
							.append("<ShippedQuantity>")
							.append("0")
							.append("</ShippedQuantity>")
							.append("<CancelledQuantity>")
							.append("0")
							.append("</CancelledQuantity>")
							.append("<InProcessQuantity>")
							.append("0")
							.append("</InProcessQuantity>")
							.append(getProductDetails(orderDO.vecProductDO, orderDO))
							.append(getProductPromotions(orderDO.vecProductDOPromotions))
							.append("</TrxHeaderDco>");
				}
				strBody.append(strOrder);
				strXML.append(strBody).append("</objTrxHeaderDcos>")
						.append("</PostTrxDetailsFromXMLWithAuth>").append(SOAP_FOOTER);
				
				LogUtils.errorLog("PostTrxDetailsFromXMLWithAuth", ""+strXML.toString());

				return strXML.toString();
			}

			private static String getProductDetails(Vector<ProductDO> arrTrxDetailsDOs,
					OrderDO orderDO) {
				int count = 1;
				StringBuffer strXML = new StringBuffer();
				strXML.append("<TrxDetails>");
				if (arrTrxDetailsDOs != null && arrTrxDetailsDOs.size() > 0) {
					for (ProductDO productDO : arrTrxDetailsDOs) {
						if (productDO.LineNo == null || productDO.LineNo.length() <= 0)
							productDO.LineNo = "" + (arrTrxDetailsDOs.size() + count++);
						if (productDO.cases == null || productDO.cases.equals(""))
							productDO.cases = "0";

						String expDate = productDO.strExpiryDate;

						// if(expDate == null || expDate.length() <=0 )
						// expDate = CalendarUtils.getOrderPostDate();

						strXML.append("<TrxDetailDco>")
								.append("<LineNo>")
								.append(productDO.LineNo)
								.append("</LineNo>")
								.append("<OrderNumber>")
								.append(productDO.OrderNo)
								.append("</OrderNumber>")
								.append("<ItemCode>")
								.append(productDO.SKU)
								.append("</ItemCode>")
								.append("<ReasonCode>")
								.append((productDO.reason == null
										|| productDO.reason.trim().equalsIgnoreCase("") ? "0"
										: productDO.reason))
								.append("</ReasonCode>")
								.append("<TrxDetailsNote></TrxDetailsNote>")
								.append("<ItemType>")
								.append(productDO.ItemType)
								.append("</ItemType>")
								.append("<BasePrice>0</BasePrice>")
								.append("<UOM>")
								.append(productDO.UOM)
								.append("</UOM>")
								.append("<Cases>")
								.append(productDO.cases)
								.append("</Cases>")
								.append("<Units>")
								.append(productDO.totalUnits)
								.append("</Units>")
								// .append("<Units>").append(productDO.units).append("</Units>")
								// .append("<Units>").append((StringUtils.getFloat(productDO.units)+StringUtils.getFloat(productDO.cases)*productDO.UnitsPerCases)).append("</Units>")
								.append("<TotalUnits>0</TotalUnits>")
								.append("<QuantityBU>0</QuantityBU>")
								.append("<RequestedBU>0</RequestedBU>")
								.append("<ApprovedBU>0</ApprovedBU>")
								.append("<CollectedBU>0</CollectedBU>")
								.append("<FinalBU>0</FinalBU>")
								.append("<QuantitySU>0</QuantitySU>")
								.append("<PriceDefaultLevel1>0</PriceDefaultLevel1>")
								.append("<PriceDefaultLevel2>0</PriceDefaultLevel2>")
								.append("<PriceDefaultLevel3>0</PriceDefaultLevel3>")
								.append("<PriceUsedLevel1>")
								.append(productDO.priceUsedLevel1)
								.append("</PriceUsedLevel1>")
								.append("<PriceUsedLevel2>")
								.append(productDO.priceUsedLevel2)
								.append("</PriceUsedLevel2>")
								.append("<PriceUsedLevel3>0</PriceUsedLevel3>")
								.append("<TaxPercentage>")
								.append(0)
								.append("</TaxPercentage>")
								.append("<TotalDiscountPercentage>0</TotalDiscountPercentage>")
								.append("<TotalDiscountAmount>")
								.append(productDO.discountAmount)
								.append("</TotalDiscountAmount>")
								.append("<CalculatedDiscountPercentage>0</CalculatedDiscountPercentage>")
								.append("<CalculatedDiscountAmount>0</CalculatedDiscountAmount>")
								.append("<UserDiscountPercentage>0</UserDiscountPercentage>")
								.append("<UserDiscountAmount>0</UserDiscountAmount>")
								.append("<ItemDescription>")
								.append(URLEncoder.encode("" + productDO.Description))
								.append("</ItemDescription>")
								.append("<ItemAltDescription>")
								.append(URLEncoder.encode("" + productDO.Description1))
								.append("</ItemAltDescription>")
								.append("<DistributionCode>0</DistributionCode>")
								.append("<AffectedStock>0</AffectedStock>")
								.append("<Status>0</Status>")
								.append("<PromoID>")
								.append(productDO.ProductId)
								.append("</PromoID>")
								.append("<PromoType>")
								.append(productDO.promotionType)
								.append("</PromoType>")
								.append("<TRXStatus>0</TRXStatus>")
								.append("<ExpiryDate>")
								.append(expDate)
								.append("</ExpiryDate>")
								.append("<RelatedLineID>")
								.append((productDO.RelatedLineId == null
										|| productDO.RelatedLineId.equalsIgnoreCase("") ? "0"
										: productDO.RelatedLineId))
								.append("</RelatedLineID>")
								.append("<ItemGroupLevel5></ItemGroupLevel5>")
								.append("<TaxType>0</TaxType>")
								.append("<SuggestedFreeBU>0</SuggestedFreeBU>")
								.append("<PushedOn>").append(orderDO.InvoiceDate)
								.append("</PushedOn>").append("<TrxReasonCode>")
								.append(productDO.reason).append("</TrxReasonCode>")
								.append("<EmptyJarQuantity>0</EmptyJarQuantity>")
								.append("<PromotionLineNo>")
								.append(productDO.PromoLineNo)
								.append("</PromotionLineNo>").append("<PromotionCode>")
								.append(productDO.promoCode).append("</PromotionCode>")
								.append("<RecommendedQty>")
								.append(productDO.recomUnits)
								.append("</RecommendedQty>").append("<BatchNumber>")
								.append(productDO.BatchCode).append("</BatchNumber>")
								.append("</TrxDetailDco>");
					}
				}
				strXML.append("</TrxDetails>");
				
				
				
				return strXML.toString();
			}

			private static String getProductPromotions(Vector<ProductDO> arrPromotionDOs) {
				StringBuffer strXML = new StringBuffer();

				if (arrPromotionDOs != null && arrPromotionDOs.size() > 0) {
					strXML.append("<TrxPromotions>");
					for (ProductDO productDO : arrPromotionDOs) {
						strXML.append("<TrxPromotionDco>").append("<OrderNumber>")
								.append(productDO.OrderNo).append("</OrderNumber>")
								.append("<ItemCode>").append(productDO.SKU)
								.append("</ItemCode>").append("<DiscountAmount>")
								.append(productDO.discountAmount)
								.append("</DiscountAmount>")
								.append("<DiscountPercentage>")
								.append(productDO.Discount)
								.append("</DiscountPercentage>")
								.append("<PromotionID>").append(productDO.promotionId)
								.append("</PromotionID>").append("<Status>0</Status>")
								.append("<OrderStatus>0</OrderStatus>")
								.append("<PromotionType>")
								.append(productDO.promotionType)
								.append("</PromotionType>")
								.append("<TrxDetailsLineNo></TrxDetailsLineNo>")
								.append("<ItemType>").append(productDO.ItemType)
								.append("</ItemType>").append("<IsStructural>")
								.append(productDO.IsStructural)
								.append("</IsStructural>")
								.append("<PushedOn></PushedOn>")
								.append("</TrxPromotionDco>");
					}
					strXML.append("</TrxPromotions>");
				}
				
				return strXML.toString();
			}
			private static String getProductImages(Vector<ProductDO> vecProductDo)
			{
				StringBuffer strXML = new StringBuffer();
				strXML.append("<OrderImages>");
				if(vecProductDo != null && vecProductDo.size() >0)
				{
					for (ProductDO productDO : vecProductDo)
					{
						if(productDO.vecDamageImagesNew != null && productDO.vecDamageImagesNew.size() > 0)
						{
							for (DamageImageDO damageImageDO : productDO.vecDamageImagesNew) 
							{
								strXML.append("<OrderImageDco>")
							.append("<OrderNo>").append(damageImageDO.OrderNo).append("</OrderNo>")
							.append("<ItemCode>").append(damageImageDO.ItemCode).append("</ItemCode>")
							.append("<LineNo>").append(damageImageDO.LineNo).append("</LineNo>")
							.append("<ImagePath>").append(damageImageDO.ImagePath).append("</ImagePath>")
							.append("<CapturedDate>").append(damageImageDO.CapturedDate).append("</CapturedDate>")
							.append("</OrderImageDco>");
							}
						}
					}
				}
				strXML.append("</OrderImages>");
				
				return strXML.toString();
			}
			
//	public static String sendFreeDeliveryOrders(Vector<OrderDO> vecCompleteOrders, String strEmpId)
//	{
//		DecimalFormat df = new DecimalFormat("##.##");
//		LogUtils.errorLog("strEmpId", "strEmpId - "+strEmpId);
//		String strOrder  = ""; 
//		String strXML = SOAP_HEADER + "<PostTrxDetailsFromXMLWithAuth xmlns=\"http://tempuri.org/\">";
//				String strBody = "<objTrxHeaderDcos>";
//  							  		for(OrderDO orderDO : vecCompleteOrders)
//  							  		{
//								  		strOrder  = strOrder+"<TrxHeaderDco>" +
//								  		"<OrderId></OrderId>" +
//								  		"<OrderNumber>"+orderDO.OrderId+"</OrderNumber>" +
//								  		"<AppId>"+orderDO.strUUID+"</AppId>" +
//								  		"<JourneyCode>"+orderDO.JourneyCode+"</JourneyCode>" +
//								  		"<VisitCode>"+orderDO.VisitCode+"</VisitCode>" +
//								  		"<EmpNo>"+orderDO.empNo+"</EmpNo>" +
//								  		"<Site_Number>"+orderDO.CustomerSiteId+"</Site_Number>" +
//								  		"<Site_Name>"+orderDO.strCustomerName+"</Site_Name>" +
//								  		"<Order_Date>"+orderDO.InvoiceDate+"</Order_Date>" +
//								  		"<Order_Type>"+orderDO.orderType+"</Order_Type>" +
//								  		"<OrderType>"+orderDO.orderSubType+"</OrderType>" +
//								  		"<OrderBookingType>"+orderDO.orderSubType+"</OrderBookingType>" +
//								  		"<CurrencyCode>"+orderDO.CurrencyCode+"</CurrencyCode>" +
//								  		"<PaymentType>"+orderDO.PaymentType+"</PaymentType>" +
//								  		"<TrxReasonCode>"+orderDO.TrxReasonCode+"</TrxReasonCode>" +
//								  		"<RemainingAmount>"+"0"+"</RemainingAmount>" +
//								  		"<TotalAmount>0</TotalAmount>" +
//		
//								  		"<TotalDiscountAmount>0</TotalDiscountAmount>" +
//								  		"<TotalTAXAmount>"+"0"+"</TotalTAXAmount>" +
//								  		"<ReferenceCode>"+"0"+"</ReferenceCode>" +
//								  		"<CustomerSignature>"+URLEncoder.encode(""+orderDO.strCustomerSign)+"</CustomerSignature>" +
//								  		"<SalesmanSignature>"+URLEncoder.encode(""+orderDO.strPresellerSign)+"</SalesmanSignature>" +
//								  		"<Status>"+"0"+"</Status>" +
//								  		"<VisitID>"+orderDO.VisitCode+"</VisitID>" +
//								  		"<VehicleCode>"+orderDO.vehicleNo+"</VehicleCode>" +
//								  		"<DeliveredBy>"+orderDO.salesmanCode+"</DeliveredBy>" +
//								  		"<FreeNote>"+"N/A"+"</FreeNote>" +
//								  		"<PreTrxCode>"+"0"+"</PreTrxCode>" +
//								  		"<TRXStatus>"+orderDO.TRXStatus+"</TRXStatus>" +
//								  		"<ConsolidatedTrxCode>"+"0"+"</ConsolidatedTrxCode>" +
//								  		"<BranchPlantCode>"+"0"+"</BranchPlantCode>" +
//								  		"<PaymentCode>"+orderDO.PaymentCode+"</PaymentCode>" +
//								  		"<ApproveByCode>"+"0"+"</ApproveByCode>" +
//								  		"<ApprovedDate>"+orderDO.InvoiceDate+"</ApprovedDate>" +
//								  		"<LPOCode>"+orderDO.LPOCode+"</LPOCode>" +
//								  		"<TrxSequence>"+orderDO.OrderId+"</TrxSequence>" +
//								  		"<StampImage>"+URLEncoder.encode(""+orderDO.StampImage)+"</StampImage>" +
//								  		"<DeliveryDate>"+orderDO.DeliveryDate+"</DeliveryDate>" +
//								  		"<StampDate>"+orderDO.InvoiceDate+"</StampDate>" +
//								  		"<UserCreditAccountCode>"+"0"+"</UserCreditAccountCode>" +
//								  		"<PushedOn>"+orderDO.InvoiceDate+"</PushedOn>" +
//								  		"<SettlementCode>"+"0"+"</SettlementCode>" +
//								  		"<ShippedQuantity>"+"0"+"</ShippedQuantity>" +
//								  		"<CancelledQuantity>"+"0"+"</CancelledQuantity>" +
//								  		"<InProcessQuantity>"+"0"+"</InProcessQuantity>"+ 
//								  		getProductDetails(orderDO.vecProductDO, orderDO)+
//								  		getProductPromotions(orderDO.vecProductDOPromotions)+
//    							  		"</TrxHeaderDco>";
//  							  		}
//	      					strBody = strBody+strOrder;
//	      					strXML = strXML+strBody+
//	      							  "</objTrxHeaderDcos>"+
//	      							  "</PostTrxDetailsFromXMLWithAuth>"+
//	      							  SOAP_FOOTER ;
//	      					LogUtils.errorLog("strXML", ""+strXML);
//	    return strXML;
//	}
	
	public static String UpdateDeliveryStatus(List<AdvanceOrderDO> vecCompleteOrders, String strEmpId)
	{
		DecimalFormat df = new DecimalFormat("##.##");
		LogUtils.errorLog("strEmpId", "strEmpId - "+strEmpId);
		String strOrder  = ""; 
		String strXML = SOAP_HEADER + "<UpdateDeliveryStatus xmlns=\"http://tempuri.org/\">";
				String strBody = "<objTrxHeaderDcos>";
  							  		for(AdvanceOrderDO orderDO : vecCompleteOrders)
  							  		{
								  		strOrder  = strOrder+"<TrxHeaderDco>" +
//								  		"<OrderId></OrderId>" +
								  		"<OrderNumber>"+orderDO.OrderId+"</OrderNumber>" +
								  		"<AppId>"+orderDO.AppId+"</AppId>" +
//								  		"<JourneyCode></JourneyCode>" +
//								  		"<VisitCode></VisitCode>" +
//								  		"<EmpNo></EmpNo>" +
//								  		"<Site_Number></Site_Number>" +
//								  		"<Order_Date></Order_Date>" +
//								  		"<Order_Type></Order_Type>" +
//								  		"<OrderType></OrderType>" +
//								  		"<OrderBookingType></OrderBookingType>" +
//								  		"<CurrencyCode></CurrencyCode>" +
//								  		"<PaymentType></PaymentType>" +
//								  		"<TrxReasonCode></TrxReasonCode>" +
//								  		"<RemainingAmount>"+"0"+"</RemainingAmount>" +
//								  		"<TotalAmount></TotalAmount>" +
//								  		"<TotalDiscountAmount></TotalDiscountAmount>" +
//								  		"<TotalTAXAmount>"+"0"+"</TotalTAXAmount>" +
//								  		"<ReferenceCode>"+"0"+"</ReferenceCode>" +
////								  	"<CustomerSignature>"+URLEncoder.encode(""+orderDO.strCustomerSign)+"</CustomerSignature>" +
////								  	"<SalesmanSignature>"+URLEncoder.encode(""+orderDO.strPresellerSign)+"</SalesmanSignature>" +
//								  		"<CustomerSignature></CustomerSignature>" +
//								  		"<SalesmanSignature></SalesmanSignature>" +
//								  		"<Status>"+"0"+"</Status>" +
//								  		"<VisitID></VisitID>" +
//								  		"<FreeNote>"+"N/A"+"</FreeNote>" +
//								  		"<PreTrxCode>"+"0"+"</PreTrxCode>" +
								  		"<TRXStatus>"+orderDO.TRXStatus+"</TRXStatus>" +
//								  		"<ConsolidatedTrxCode>"+"0"+"</ConsolidatedTrxCode>" +
//								  		"<BranchPlantCode>"+"0"+"</BranchPlantCode>" +
//								  		"<PaymentCode></PaymentCode>" +
//								  		"<ApproveByCode>"+"0"+"</ApproveByCode>" +
//								  		"<ApprovedDate></ApprovedDate>" +
//								  		"<LPOCode></LPOCode>" +
//								  		"<TrxSequence>"+orderDO.OrderId+"</TrxSequence>" +
//								  		"<StampImage></StampImage>" +
//								  		"<DeliveryDate></DeliveryDate>" +
//								  		"<StampDate></StampDate>" +
//								  		"<UserCreditAccountCode>"+"0"+"</UserCreditAccountCode>" +
//								  		"<PushedOn></PushedOn>" +
//								  		"<SettlementCode>"+"0"+"</SettlementCode>" +
//								  		"<ShippedQuantity>"+"0"+"</ShippedQuantity>" +
//								  		"<CancelledQuantity>"+"0"+"</CancelledQuantity>" +
//								  		"<InProcessQuantity>"+"0"+"</InProcessQuantity>"+ 
//								  		getProductDetails(orderDO.vecProductDO, orderDO)+
//							  			getProductPromotions(orderDO.vecProductDOPromotions)+
    							  		"</TrxHeaderDco>";
  							  		}
	      					strBody = strBody+strOrder;
	      					strXML = strXML+strBody+
	      							  "</objTrxHeaderDcos>"+
	      							  "</UpdateDeliveryStatus>"+
	      							  SOAP_FOOTER ;
	      					LogUtils.errorLog("strXML", ""+strXML);
	    return strXML;
	}
	
//	private static String getProductDetails(Vector<ProductDO> arrTrxDetailsDOs, OrderDO orderDO)
//	{
//		int count = 1;
//		String strXML = "";
//		strXML = strXML + "<TrxDetails>";
//		if(arrTrxDetailsDOs != null && arrTrxDetailsDOs.size() >0)
//		{
//			for (ProductDO productDO : arrTrxDetailsDOs)
//			{
//				if(productDO.LineNo == null || productDO.LineNo.length() <= 0)
//					productDO.LineNo = ""+(arrTrxDetailsDOs.size() + count++);
//				if(productDO.cases == null || productDO.cases.equals(""))
//					productDO.cases = "0";
//				
//				String expDate = productDO.strExpiryDate;
//				
//				if(expDate == null || expDate.length() <=0 )
//					expDate = CalendarUtils.getOrderPostDate();
//				
//				strXML = strXML + "<TrxDetailDco>"+
//						"<LineNo>"+productDO.LineNo+"</LineNo>"+
//						"<OrderNumber>"+productDO.OrderNo+"</OrderNumber>"+
//						"<ItemCode>"+productDO.SKU+"</ItemCode>"+
//						"<ReasonCode>"+(productDO.reason == null || productDO.reason.trim().equalsIgnoreCase("") ? "0" : productDO.reason) +"</ReasonCode>"+
//						"<TrxDetailsNote></TrxDetailsNote>"+
//						"<ItemType>"+productDO.ItemType+"</ItemType>"+
//						"<BasePrice>0</BasePrice>"+
//						"<UOM>"+productDO.UOM+"</UOM>"+
//						"<Cases>"+productDO.cases+"</Cases>"+
//						"<Units>"+productDO.totalUnits+"</Units>"+
////						"<Units>"+productDO.units+"</Units>"+
////						"<Units>"+(StringUtils.getFloat(productDO.units)+StringUtils.getFloat(productDO.cases)*productDO.UnitsPerCases)+"</Units>"+
//						"<TotalUnits>0</TotalUnits>"+
//						"<QuantityBU>0</QuantityBU>"+
//						"<RequestedBU>0</RequestedBU>"+
//						"<ApprovedBU>0</ApprovedBU>"+
//						"<CollectedBU>0</CollectedBU>"+
//						"<FinalBU>0</FinalBU>"+
//						"<QuantitySU>0</QuantitySU>"+
//						"<PriceDefaultLevel1>0</PriceDefaultLevel1>"+
//						"<PriceDefaultLevel2>0</PriceDefaultLevel2>"+
//						"<PriceDefaultLevel3>0</PriceDefaultLevel3>"+
//						"<PriceUsedLevel1>"+productDO.priceUsedLevel1+"</PriceUsedLevel1>"+
//						"<PriceUsedLevel2>"+productDO.priceUsedLevel2+"</PriceUsedLevel2>"+
//						"<PriceUsedLevel3>0</PriceUsedLevel3>"+
//						"<TaxPercentage>"+0+"</TaxPercentage>"+
//						"<TotalDiscountPercentage>0</TotalDiscountPercentage>"+
//						"<TotalDiscountAmount>"+productDO.discountAmount+"</TotalDiscountAmount>"+
//						"<CalculatedDiscountPercentage>0</CalculatedDiscountPercentage>"+
//						"<CalculatedDiscountAmount>0</CalculatedDiscountAmount>"+
//						"<UserDiscountPercentage>0</UserDiscountPercentage>"+
//						"<UserDiscountAmount>0</UserDiscountAmount>"+
//						"<ItemDescription>"+URLEncoder.encode(""+productDO.Description)+"</ItemDescription>"+
//						"<ItemAltDescription>"+URLEncoder.encode(""+productDO.Description1)+"</ItemAltDescription>"+
//						"<DistributionCode>0</DistributionCode>"+
//						"<AffectedStock>0</AffectedStock>"+
//						"<Status>0</Status>"+
//						"<PromoID>"+productDO.ProductId+"</PromoID>"+
//						"<PromoType>"+productDO.promotionType+"</PromoType>"+
//						"<TRXStatus>0</TRXStatus>"+
//						"<ExpiryDate>"+expDate+"</ExpiryDate>"+
//						"<RelatedLineID>"+(productDO.RelatedLineId == null || productDO.RelatedLineId.equalsIgnoreCase("") ? "0" : productDO.RelatedLineId)+"</RelatedLineID>"+
//						"<ItemGroupLevel5></ItemGroupLevel5>"+
//						"<TaxType>0</TaxType>"+
//						"<SuggestedFreeBU>0</SuggestedFreeBU>"+
//						"<PushedOn>"+orderDO.InvoiceDate+"</PushedOn>"+
//						"<TrxReasonCode>"+productDO.reason+"</TrxReasonCode>"+
//						"<EmptyJarQuantity>0</EmptyJarQuantity>"+
//						"<PromotionLineNo>"+productDO.PromoLineNo+"</PromotionLineNo>"+
//						"<PromotionCode>"+productDO.promoCode+"</PromotionCode>"+
//						"<RecommendedQty>"+productDO.recomUnits+"</RecommendedQty>"+
//						"<BatchNumber>"+productDO.BatchCode+"</BatchNumber>"+
//						"</TrxDetailDco>";	
//			}
//		}
//		strXML = strXML +"</TrxDetails>";
//		return strXML;
//	}
//	private static String getProductDetailsNew(ArrayList<TrxDetailsDO> arrTrxDetailsDOs, TrxHeaderDO orderDO)
//	{
//		String strXML = "";
//		strXML = strXML + "<TrxDetails>";
//		if(arrTrxDetailsDOs != null && arrTrxDetailsDOs.size() >0)
//		{
//			for (TrxDetailsDO trxDetailsDO : arrTrxDetailsDOs)
//			{
//				try {
//					strXML = strXML + "<TrxDetailDco>"+
//							"<AffectedStock>"+trxDetailsDO.affectedStock+"</AffectedStock>"+
//							"<ApprovedBU>"+trxDetailsDO.approvedBU+"</ApprovedBU>"+
//							"<BasePrice>"+trxDetailsDO.basePrice+"</BasePrice>"+
//							"<CalculatedDiscountAmount>"+trxDetailsDO.calculatedDiscountAmount+"</CalculatedDiscountAmount>"+
//							"<CalculatedDiscountPercentage>"+trxDetailsDO.calculatedDiscountPercentage+"</CalculatedDiscountPercentage>"+
//							"<CancelledQuantity>"+trxDetailsDO.cancelledQuantity+"</CancelledQuantity>"+
//							"<CollectedBU>"+trxDetailsDO.collectedBU+"</CollectedBU>"+
//							"<DistributionCode>"+trxDetailsDO.distributionCode+"</DistributionCode>"+
//							"<ExpiryDate>"+trxDetailsDO.expiryDate+"</ExpiryDate>"+
//							"<FinalBU>"+trxDetailsDO.finalBU+"</FinalBU>"+
//							"<InProcessQuantity>"+trxDetailsDO.inProcessQuantity+"</InProcessQuantity>"+
//							"<ItemAltDescription>"+trxDetailsDO.itemAltDescription+"</ItemAltDescription>"+
//							"<ItemCode>"+trxDetailsDO.itemCode+"</ItemCode>"+
//							"<ItemDescription>"+URLEncoder.encode(trxDetailsDO.itemDescription,"UTF-8")+"</ItemDescription>"+
//							"<ItemGroupLevel5>"+trxDetailsDO.itemGroupLevel5+"</ItemGroupLevel5>"+
//							"<ItemType>"+trxDetailsDO.itemType+"</ItemType>"+
//							"<LineNo>"+trxDetailsDO.lineNo+"</LineNo>"+
//							"<OrgCode>"+trxDetailsDO.orgCode+"</OrgCode>"+
//							"<PriceUsedLevel1>"+trxDetailsDO.priceUsedLevel1+"</PriceUsedLevel1>"+
//							"<PriceUsedLevel2>"+trxDetailsDO.priceUsedLevel2+"</PriceUsedLevel2>"+
//							"<PriceUsedLevel3>"+trxDetailsDO.priceUsedLevel3+"</PriceUsedLevel3>"+
//							"<PromoID>"+trxDetailsDO.promoID+"</PromoID>"+
//							"<PromoType>"+trxDetailsDO.promoType+"</PromoType>"+
//							"<QuantityBU>"+trxDetailsDO.quantityBU+"</QuantityBU>"+
//							"<MissedBU>"+trxDetailsDO.missedBU+"</MissedBU>"+
//							"<QuantityLevel1>"+trxDetailsDO.quantityLevel1+"</QuantityLevel1>"+
//							"<QuantityLevel2>"+trxDetailsDO.quantityLevel2+"</QuantityLevel2>"+
//							"<QuantityLevel3>"+trxDetailsDO.quantityLevel3+"</QuantityLevel3>"+
//							"<Reason>"+trxDetailsDO.reason+"</Reason>"+
//							"<RelatedLineID>"+trxDetailsDO.relatedLineID+"</RelatedLineID>"+
//							"<RequestedBU>"+trxDetailsDO.requestedBU+"</RequestedBU>"+
//							"<ShippedQuantity>"+trxDetailsDO.shippedQuantity+"</ShippedQuantity>"+
//							"<SuggestedBU>"+trxDetailsDO.suggestedBU+"</SuggestedBU>"+
//							"<TRXStatus>"+trxDetailsDO.trxStatus+"</TRXStatus>"+
//							"<TaxPercentage>"+trxDetailsDO.taxPercentage+"</TaxPercentage>"+
//							"<TaxType>"+trxDetailsDO.taxType+"</TaxType>"+
//							"<TotalDiscountAmount>"+trxDetailsDO.totalDiscountAmount+"</TotalDiscountAmount>"+
//							"<TotalDiscountPercentage>"+trxDetailsDO.totalDiscountPercentage+"</TotalDiscountPercentage>"+
//							"<TrxCode>"+trxDetailsDO.trxCode+"</TrxCode>"+
//							"<TrxDetailsNote>"+URLEncoder.encode(trxDetailsDO.trxDetailsNote,"UTF-8")+"</TrxDetailsNote>"+
//							"<TrxReasonCode>"+trxDetailsDO.trxReasonCode+"</TrxReasonCode>"+
//							"<UOM>"+trxDetailsDO.UOM+"</UOM>"+
//							"<UserDiscountAmount>"+trxDetailsDO.userDiscountAmount+"</UserDiscountAmount>"+
//							"<UserDiscountPercentage>"+trxDetailsDO.userDiscountPercentage+"</UserDiscountPercentage>"+
//							"<BatchNumber>"+trxDetailsDO.batchCode+"</BatchNumber>"+
//							"</TrxDetailDco>";
//				} catch (Exception e) {
//					e.printStackTrace();
//				}	
//			}
//		}
//		strXML = strXML +"</TrxDetails>";
//		return strXML;
//	}
	
//	private static String getProductImages(Vector<ProductDO> vecProductDo)
//	{
//		String strXML = "";
//		strXML = strXML + "<OrderImages>";
//		if(vecProductDo != null && vecProductDo.size() >0)
//		{
//			for (ProductDO productDO : vecProductDo)
//			{
//				if(productDO.vecDamageImagesNew != null && productDO.vecDamageImagesNew.size() > 0)
//				{
//					for (DamageImageDO damageImageDO : productDO.vecDamageImagesNew) {
//						strXML = strXML + "<OrderImageDco>"+
//						"<OrderNo>"+damageImageDO.OrderNo+"</OrderNo>"+
//						"<ItemCode>"+damageImageDO.ItemCode+"</ItemCode>"+
//						"<LineNo>"+damageImageDO.LineNo+"</LineNo>"+
//						"<ImagePath>"+damageImageDO.ImagePath+"</ImagePath>"+
//						"<CapturedDate>"+damageImageDO.CapturedDate+"</CapturedDate>"+
//						"</OrderImageDco>";
//					}
//				}
//			}
//		}
//		strXML = strXML + "</OrderImages>";
//		return strXML;
//	}
	
//	private static String getProductPromotions(Vector<ProductDO> arrPromotionDOs)
//	{
//		String strXML = "";
//		
//		if(arrPromotionDOs != null && arrPromotionDOs.size() >0)
//		{
//			strXML = strXML + "<TrxPromotions>";
//			for (ProductDO productDO : arrPromotionDOs)
//			{
//				strXML = strXML + "<TrxPromotionDco>"+
//						"<OrderNumber>"+productDO.OrderNo+"</OrderNumber>"+
//						"<ItemCode>"+productDO.SKU+"</ItemCode>"+
//						"<DiscountAmount>"+productDO.discountAmount+"</DiscountAmount>"+
//						"<DiscountPercentage>"+productDO.Discount+"</DiscountPercentage>"+
//						"<PromotionID>"+productDO.promotionId+"</PromotionID>"+
//						"<Status>0</Status>"+
//						"<OrderStatus>0</OrderStatus>"+
//						"<PromotionType>"+productDO.promotionType+"</PromotionType>"+
//						"<TrxDetailsLineNo></TrxDetailsLineNo>"+
//						"<ItemType>"+productDO.ItemType+"</ItemType>"+
//						"<IsStructural>"+productDO.IsStructural+"</IsStructural>"+
//						"<PushedOn></PushedOn>"+
//						"</TrxPromotionDco>";	
//			}
//			strXML = strXML + "</TrxPromotions>";
//		}
//		return strXML;
//	}
//	private static String getProductPromotionsNew(ArrayList<TrxPromotionDO> arrPromotionDOs)
//	{
//		String strXML = "";
//		
//		if(arrPromotionDOs != null && arrPromotionDOs.size() >0)
//		{
//			strXML = strXML + "<TrxPromotions>";
//			for (TrxPromotionDO productDO : arrPromotionDOs)
//			{
//				strXML = strXML + "<TrxPromotionDco >"+
//						"<CreatedOn>"+productDO.createdOn+"</CreatedOn>"+
//						"<DiscountAmount>"+productDO.discountAmount+"</DiscountAmount>"+
//						"<DiscountPercentage>"+productDO.discountPercentage+"</DiscountPercentage>"+
//						"<FactSheetCode>"+productDO.factSheetCode+"</FactSheetCode>"+
//						"<IsStructural>"+productDO.isStructural+"</IsStructural>"+
//						"<ItemCode>"+productDO.itemCode+"</ItemCode>"+
//						"<ItemType>"+productDO.itemType+"</ItemType>"+
//						"<OrgCode>"+productDO.orgCode+"</OrgCode>"+
//						"<PromotionID>"+productDO.promotionID+"</PromotionID>"+
//						"<PromotionType>"+productDO.promotionType+"</PromotionType>"+
//						"<PushedOn>"+productDO.pushedOn+"</PushedOn>"+
//						"<Status>"+productDO.status+"</Status>"+
//						"<TrxCode>"+productDO.trxCode+"</TrxCode>"+
//						"<TrxDetailsLineNo>"+productDO.trxDetailsLineNo+"</TrxDetailsLineNo>"+
//						"<TrxStatus>"+productDO.trxStatus+"</TrxStatus>"+
//						"</TrxPromotionDco >";	
//			}
//			strXML = strXML + "</TrxPromotions>";
//		}
//		return strXML;
//	}
	public static String createNewCustomerList(Vector<CustomerDO> vecNewCustomers)
	{
		StringBuffer strXML = new StringBuffer();
		
		
		StringBuffer strCustomers= new StringBuffer();
		
		for(CustomerDO customerDO :vecNewCustomers)
		{
//			strCustomers = strCustomers+
////							 "&lt;Customer&gt;"+
////							 	"&lt;SiteId&gt;"+customerDO.customerSiteID+"&lt;/SiteId&gt;"+ 
////								"&lt;SALESMAN&gt;" +customerDO.presellerId+"&lt;/SALESMAN&gt;"+
////								"&lt;PARTY_NAME&gt;"+customerDO.customerName+"&lt;/PARTY_NAME&gt;"+
////								"&lt;REGION_CODE&gt;"+customerDO.regionCode+"&lt;/REGION_CODE&gt;"+ 
////								"&lt;REGION_DESCRIPTION&gt;"+customerDO.regionDescription+"&lt;/REGION_DESCRIPTION&gt;"+ 
////								"&lt;COUNTRY_CODE&gt;"+customerDO.countryCode+"&lt;/COUNTRY_CODE&gt;"+ 
////								"&lt;COUNTRY_DESCRIPTION&gt;"+customerDO.countryDescription+"&lt;/COUNTRY_DESCRIPTION&gt;"+ 
////								"&lt;ADDRESS1&gt;"+customerDO.customerStreet+"&lt;/ADDRESS1&gt;"+ 
////								"&lt;ADDRESS2&gt;"+customerDO.customerBuilding+"&lt;/ADDRESS2&gt;"+ 
////								"&lt;ADDRESS3&gt;"+customerDO.telephone_No+"&lt;/ADDRESS3&gt;"+ 
////								"&lt;ADDRESS4&gt;"+customerDO.fax_No+"&lt;/ADDRESS4&gt;"+ 
////								"&lt;PO_BOX_NUMBER&gt;"+customerDO.post_BoxNo+"&lt;/PO_BOX_NUMBER&gt;"+ 
////								"&lt;CITY&gt;"+customerDO.customerLocation+"&lt;/CITY&gt;"+
////								"&lt;PAYMENT_TYPE&gt;"+customerDO.payment_Type+"&lt;/PAYMENT_TYPE&gt;"+
////								"&lt;PAYMENT_TERM_CODE&gt;"+customerDO.payment_Terms_Code+"&lt;/PAYMENT_TERM_CODE&gt;"+ 
////								"&lt;PAYMENT_TERM_DESCRIPTION&gt;"+customerDO.payment_Terms+"&lt;/PAYMENT_TERM_DESCRIPTION&gt;"+ 
////								"&lt;CREDIT_LIMIT&gt;"+customerDO.credit_Limit+"&lt;/CREDIT_LIMIT&gt;"+ 
////								"&lt;GEO_CODE_X&gt;"+customerDO.Customer_Latitude+"&lt;/GEO_CODE_X&gt;"+ 
////								"&lt;GEO_CODE_Y&gt;"+customerDO.Customer_Longitude+"&lt;/GEO_CODE_Y&gt;" +
////								"&lt;EMAIL&gt;"+customerDO.email_Id+"&lt;/EMAIL&gt;" +	
////							    "&lt;CONTACTPERSONNAME&gt;"+customerDO.contactPersonName+"&lt;/CONTACTPERSONNAME&gt;"+
////							    "&lt;CONTACTPERSONMOBILENO&gt;"+customerDO.contactPersonMobileNumber+"&lt;/CONTACTPERSONMOBILENO&gt;"+
////							"&lt;/Customer&gt;";
					strCustomers.append("<Customer>")
						   	.append("<SiteId>").append(customerDO.customerSiteID).append("</SiteId>") 
							.append("<SALESMAN>").append(customerDO.presellerId).append("</SALESMAN>")
							.append("<PARTY_NAME>").append(customerDO.customerName).append("</PARTY_NAME>")
							.append("<REGION_CODE>").append(""+customerDO.regionCode).append("</REGION_CODE>")
							.append("<REGION_DESCRIPTION>").append(""+customerDO.regionDescription).append("</REGION_DESCRIPTION>") 
							.append("<COUNTRY_CODE>").append(""+customerDO.countryCode).append("</COUNTRY_CODE>") 
							.append("<COUNTRY_DESCRIPTION>").append(""+customerDO.countryDescription).append("</COUNTRY_DESCRIPTION>")
							.append("<ADDRESS1>").append(customerDO.customerStreet).append("</ADDRESS1>") 
							.append("<ADDRESS2>").append(customerDO.customerBuilding).append("</ADDRESS2>") 
							.append("<ADDRESS3>").append(customerDO.telephone_No).append("</ADDRESS3>") 
							.append("<ADDRESS4>").append(customerDO.fax_No).append("</ADDRESS4>") 
							.append("<PO_BOX_NUMBER>").append(customerDO.post_BoxNo).append("</PO_BOX_NUMBER>") 
							.append("<CITY>").append(customerDO.customerLocation).append("</CITY>")
							.append("<PAYMENT_TYPE>").append(customerDO.payment_Type).append("</PAYMENT_TYPE>")
							.append("<PAYMENT_TERM_CODE>").append(customerDO.payment_Terms_Code).append("</PAYMENT_TERM_CODE>") 
							.append("<PAYMENT_TERM_DESCRIPTION>").append(customerDO.payment_Terms).append("</PAYMENT_TERM_DESCRIPTION>") 
							.append("<CREDIT_LIMIT>").append(customerDO.credit_Limit).append("</CREDIT_LIMIT>") 
							.append("<GEO_CODE_X>").append(customerDO.Customer_Latitude).append("</GEO_CODE_X>") 
							.append("<GEO_CODE_Y>").append(customerDO.Customer_Longitude).append("</GEO_CODE_Y>" )
							.append("<EMAIL>").append(customerDO.email_Id).append("</EMAIL>")	
							.append("<CONTACTPERSONNAME>").append(customerDO.contactPersonName).append("</CONTACTPERSONNAME>")
							.append("<CONTACTPERSONMOBILENO>").append(customerDO.contactPersonMobileNumber).append("</CONTACTPERSONMOBILENO>")
							.append("<AppKey>").append(customerDO.strUUID).append("</AppKey>")
						    .append("</Customer>");
		}
		
		  strXML.append(SOAP_HEADER)     
				.append("<InsertCustomer xmlns=\"http://tempuri.org/\">")
				.append("<CustomerRequest>")
				.append("<Customers>")
				.append(strCustomers)
				.append("</Customers>")
				.append("</CustomerRequest>")
			    .append("</InsertCustomer>")
				.append(SOAP_FOOTER) ;
		
		  LogUtils.errorLog("InsertCustomer", ""+strXML.toString());
		return strXML.toString();
	}
//	public static String createNewCustomerList(Vector<CustomerDO> vecNewCustomers)
//	{
//		String strCustomers="";
//		
//		for(CustomerDO customerDO :vecNewCustomers)
//		{
//			strCustomers = strCustomers+
////							 "&lt;Customer&gt;"+
////							 	"&lt;SiteId&gt;"+customerDO.customerSiteID+"&lt;/SiteId&gt;"+ 
////								"&lt;SALESMAN&gt;" +customerDO.presellerId+"&lt;/SALESMAN&gt;"+
////								"&lt;PARTY_NAME&gt;"+customerDO.customerName+"&lt;/PARTY_NAME&gt;"+
////								"&lt;REGION_CODE&gt;"+customerDO.regionCode+"&lt;/REGION_CODE&gt;"+ 
////								"&lt;REGION_DESCRIPTION&gt;"+customerDO.regionDescription+"&lt;/REGION_DESCRIPTION&gt;"+ 
////								"&lt;COUNTRY_CODE&gt;"+customerDO.countryCode+"&lt;/COUNTRY_CODE&gt;"+ 
////								"&lt;COUNTRY_DESCRIPTION&gt;"+customerDO.countryDescription+"&lt;/COUNTRY_DESCRIPTION&gt;"+ 
////								"&lt;ADDRESS1&gt;"+customerDO.customerStreet+"&lt;/ADDRESS1&gt;"+ 
////								"&lt;ADDRESS2&gt;"+customerDO.customerBuilding+"&lt;/ADDRESS2&gt;"+ 
////								"&lt;ADDRESS3&gt;"+customerDO.telephone_No+"&lt;/ADDRESS3&gt;"+ 
////								"&lt;ADDRESS4&gt;"+customerDO.fax_No+"&lt;/ADDRESS4&gt;"+ 
////								"&lt;PO_BOX_NUMBER&gt;"+customerDO.post_BoxNo+"&lt;/PO_BOX_NUMBER&gt;"+ 
////								"&lt;CITY&gt;"+customerDO.customerLocation+"&lt;/CITY&gt;"+
////								"&lt;PAYMENT_TYPE&gt;"+customerDO.payment_Type+"&lt;/PAYMENT_TYPE&gt;"+
////								"&lt;PAYMENT_TERM_CODE&gt;"+customerDO.payment_Terms_Code+"&lt;/PAYMENT_TERM_CODE&gt;"+ 
////								"&lt;PAYMENT_TERM_DESCRIPTION&gt;"+customerDO.payment_Terms+"&lt;/PAYMENT_TERM_DESCRIPTION&gt;"+ 
////								"&lt;CREDIT_LIMIT&gt;"+customerDO.credit_Limit+"&lt;/CREDIT_LIMIT&gt;"+ 
////								"&lt;GEO_CODE_X&gt;"+customerDO.Customer_Latitude+"&lt;/GEO_CODE_X&gt;"+ 
////								"&lt;GEO_CODE_Y&gt;"+customerDO.Customer_Longitude+"&lt;/GEO_CODE_Y&gt;" +
////								"&lt;EMAIL&gt;"+customerDO.email_Id+"&lt;/EMAIL&gt;" +	
////							    "&lt;CONTACTPERSONNAME&gt;"+customerDO.contactPersonName+"&lt;/CONTACTPERSONNAME&gt;"+
////							    "&lt;CONTACTPERSONMOBILENO&gt;"+customerDO.contactPersonMobileNumber+"&lt;/CONTACTPERSONMOBILENO&gt;"+
////							"&lt;/Customer&gt;";
//						   "<Customer>"+
//						   		"<SiteId>"+customerDO.customerSiteID+"</SiteId>"+ 
//								"<SALESMAN>" +customerDO.presellerId+"</SALESMAN>"+
//								"<PARTY_NAME>"+customerDO.customerName+"</PARTY_NAME>"+
//								"<REGION_CODE>"+""+customerDO.regionCode+"</REGION_CODE>"+ 
//								"<REGION_DESCRIPTION>"+""+customerDO.regionDescription+"</REGION_DESCRIPTION>"+ 
//								"<COUNTRY_CODE>"+""+customerDO.countryCode+"</COUNTRY_CODE>"+ 
//								"<COUNTRY_DESCRIPTION>"+""+customerDO.countryDescription+"</COUNTRY_DESCRIPTION>"+ 
//								"<ADDRESS1>"+customerDO.customerStreet+"</ADDRESS1>"+ 
//								"<ADDRESS2>"+customerDO.customerBuilding+"</ADDRESS2>"+ 
//								"<ADDRESS3>"+customerDO.telephone_No+"</ADDRESS3>"+ 
//								"<ADDRESS4>"+customerDO.fax_No+"</ADDRESS4>"+ 
//								"<PO_BOX_NUMBER>"+customerDO.post_BoxNo+"</PO_BOX_NUMBER>"+ 
//								"<CITY>"+customerDO.customerLocation+"</CITY>"+
//								"<PAYMENT_TYPE>"+customerDO.payment_Type+"</PAYMENT_TYPE>"+
//								"<PAYMENT_TERM_CODE>"+customerDO.payment_Terms_Code+"</PAYMENT_TERM_CODE>"+ 
//								"<PAYMENT_TERM_DESCRIPTION>"+customerDO.payment_Terms+"</PAYMENT_TERM_DESCRIPTION>"+ 
//								"<CREDIT_LIMIT>"+customerDO.credit_Limit+"</CREDIT_LIMIT>"+ 
//								"<GEO_CODE_X>"+customerDO.Customer_Latitude+"</GEO_CODE_X>"+ 
//								"<GEO_CODE_Y>"+customerDO.Customer_Longitude+"</GEO_CODE_Y>" +
//								"<EMAIL>"+customerDO.email_Id+"</EMAIL>" +	
//							    "<CONTACTPERSONNAME>"+customerDO.contactPersonName+"</CONTACTPERSONNAME>"+
//							    "<CONTACTPERSONMOBILENO>"+customerDO.contactPersonMobileNumber+"</CONTACTPERSONMOBILENO>"+
//							    "<AppKey>"+customerDO.strUUID+"</AppKey>"+
//							"</Customer>";
//		}
//		String strXML = SOAP_HEADER +     
//						"<InsertCustomer xmlns=\"http://tempuri.org/\">"+
//							"<CustomerRequest>"+
//							"<Customers>"+
//								strCustomers+
//							"</Customers>"+
//							"</CustomerRequest>"+
//						"</InsertCustomer>"+
//	      				SOAP_FOOTER ;
//		return strXML;
//	}
	
	/**
	 * */
	public static String getAllNotesByUserAndCustomerId(String salesmanCode,String customerId, String strSyncTime)
	{
		LogUtils.errorLog("getAllNotesByUserAndCustomerId - ", ""+strSyncTime);
		StringBuilder strXML = new StringBuilder();
			  strXML.append(SOAP_HEADER)     
					.append("<GetNotes xmlns=\"http://tempuri.org/\">")
	      			.append("<CustomerId>").append(customerId).append("</CustomerId>")
	      			.append("<PresellerId>").append(salesmanCode).append("</PresellerId>")
	      			.append("<LastSyncDate>").append(strSyncTime).append("</LastSyncDate>")
	      			.append("</GetNotes>")
	      			.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	public static String deleteNotes(String noteId)
	{
		StringBuilder strXML = new StringBuilder();
			  strXML.append(SOAP_HEADER)     
					.append("<DeleteNotes  xmlns=\"http://tempuri.org/\">")
					.append("<NoteIds>").append(noteId).append("</NoteIds>")
					.append("</DeleteNotes >")
					.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	
	public static String updateCustomerGeoLocation(JourneyPlanDO mallsDetails, String empNo)
	{
		StringBuilder strXML = new StringBuilder();
			  strXML.append(SOAP_HEADER)     
					.append("<UpdateClientLocation xmlns=\"http://tempuri.org/\">")
					.append("<Site>").append(mallsDetails.site).append("</Site>")
					.append("<Latitude>").append(mallsDetails.geoCodeX).append("</Latitude>")
					.append("<Longitude>").append(mallsDetails.geoCodeY).append("</Longitude>")
					.append("</UpdateClientLocation>")
					.append(SOAP_FOOTER) ;
		return strXML.toString();
	}

	public static String getConfirmOrderDetailsforDA(String empNo,String currentDate)
	{
		StringBuilder strXML = new StringBuilder();
				strXML.append(SOAP_HEADER)     
					.append("<getConfirmOrderDetailsforDA xmlns=\"http://tempuri.org/\">")
					.append("<EmpNo>").append(empNo).append("</EmpNo>")
					.append("<Date>").append(currentDate).append("</Date>")
					.append("</getConfirmOrderDetailsforDA>")
					.append(SOAP_FOOTER) ;
		LogUtils.errorLog("strXML",""+strXML);
		return strXML.toString();
	}
	
	public static String getCustomerOrderHistoryBYDA (String empNo,String currentDate)
	{
		StringBuilder strXML = new StringBuilder();
				strXML.append(SOAP_HEADER)     
					.append("<GetCustomerOrderHistoryBYDA xmlns=\"http://tempuri.org/\">")
					.append("<DeliveryAgentId>").append(empNo).append("</DeliveryAgentId>")
					.append("<Date>").append(currentDate).append("</Date>")
					.append("</GetCustomerOrderHistoryBYDA>")
					.append(SOAP_FOOTER) ;
		LogUtils.errorLog("strXML",""+strXML);
		return strXML.toString();
	}
	
	
	public static String getDeliveryStatus(String empNo,String currentDate)
	{
		StringBuilder strXML = new StringBuilder();
			  strXML.append(SOAP_HEADER)     
					.append("<CheckDeliveryStatus xmlns=\"http://tempuri.org/\">")
					.append("<EmpNo>").append(empNo).append("</EmpNo>")
					.append("<Date>").append(currentDate).append("</Date>")
					.append("</CheckDeliveryStatus>")
					.append(SOAP_FOOTER) ;
		LogUtils.errorLog("strXML",""+strXML);
		return strXML.toString();
	}
	public static String getAllReasons(String strSyncTime)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)     
					.append("<GetAllReasons xmlns=\"http://tempuri.org/\">")
					.append("<LastSync>").append(strSyncTime).append("</LastSync>")
					.append("</GetAllReasons>")
					.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	/**
	 * Method to update the Delivery status of orders by delivery agent 
	 * @param noteId
	 * @return String
	 */
	
	/**
	 * 
	 * @param strSyncTime
	 * @return String
	 */
	public static String getSurveyQuestion(String strSyncTime)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)     
					.append("<GetQuestionAnswer xmlns=\"http://tempuri.org/\">")
					.append("<LastSync>").append(strSyncTime).append("</LastSync>")
					.append("</GetQuestionAnswer>")
					.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	
	public  static String generatePasscode(String preSellerId,String supervisorId)
	{

		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)     
	    			.append("<GetPassCode xmlns=\"http://tempuri.org/\">")
	    			.append("<SupervisorId>").append( supervisorId).append("</SupervisorId>")
	    			.append("<PreSellerId>").append( preSellerId).append("</PreSellerId>")
	    			.append("</GetPassCode>")
						.append(SOAP_FOOTER) ;
		LogUtils.errorLog("strXML",""+strXML);
		return strXML.toString();
	}
	
	
	//Check PassCode Status
	public static String insertEOT(String strEmpId, String strEOTType, String strReason, String strDateTime,String strOrders,String strInvoices, String strPayments, String signature)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
		 			.append("<InsertEOT xmlns=\"http://tempuri.org/\">")
	      			.append("<objEOTD>")
	      			.append("<EmpNo>").append(strEmpId).append("</EmpNo>")
	      			.append("<EOTType>").append(strEOTType).append("</EOTType>")
	      			.append("<Reason>").append(strReason).append("</Reason>")
	      			.append("<EOTTime>").append(strDateTime).append("</EOTTime>")
	      			.append("<OrderNumber>").append(strOrders).append("</OrderNumber>")
	      	        .append("<ReceiptNumber>").append(strPayments).append("</ReceiptNumber>")
	      	        .append("<InvoiceNumber>").append(strInvoices).append("</InvoiceNumber>")
//	      	        .append("<Signature></Signature>")
	      	        .append("<Signature>").append(URLEncoder.encode(""+signature)).append("</Signature>")
	      			.append("</objEOTD>")
	      		    .append("</InsertEOT>")
					.append(SOAP_FOOTER) ;
		LogUtils.errorLog("strXML - ", "strXML - "+strXML);
		return strXML.toString();
	}
	//login request
	public static String getAllCategory(String strSyncTime)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
					.append("<GetAllCategory xmlns=\"http://tempuri.org/\">")
					.append("<LastSync>").append(strSyncTime).append("</LastSync>")
					.append("</GetAllCategory>")
						.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	
	public static String getItemPricing(String strSyncTime)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
	    			.append("<getPrice xmlns=\"http://tempuri.org/\">")
	    			.append("<LastSyncDate>").append(strSyncTime).append("</LastSyncDate>")
	    			.append("</getPrice>")
						.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	
	public static String getFiveTopSellingItems(String empNo)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
	    			.append("<GetTopFiveSellingItem xmlns=\"http://tempuri.org/\" >")
	    			.append("<EmpNo>").append(empNo).append("</EmpNo>")
	    			.append("</GetTopFiveSellingItem>")
						.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	
	public static String getSplashScreenDataforSync(String UserCode , String lsd, String lst)
	{
		LogUtils.errorLog("GetHHSplashScreenDataforSync","GetHHSplashScreenDataforSync "+ lsd);
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
				
				.append("<GetHHSplashScreenDataforSync xmlns=\"http://tempuri.org/\">")
				.append("<UserCode>").append(UserCode).append("</UserCode>")
					.append("<lsd>").append(lsd).append("</lsd>")
					.append("<lst>").append(lst).append("</lst>")
					.append("</GetHHSplashScreenDataforSync>")
						.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	
	public static String getHouseholdMastersWithSync(String strSyncTime)
	{
		LogUtils.errorLog("GetHouseHoldMastersWithSync","GetHouseHoldMastersWithSync "+ strSyncTime);
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
					.append("<GetHouseHoldMastersWithSync xmlns=\"http://tempuri.org/\">")
					.append("<LastSyncDate>").append(strSyncTime).append("</LastSyncDate>")
					.append("</GetHouseHoldMastersWithSync>") 
						.append(SOAP_FOOTER) ;
      
		return strXML.toString();
	}

	/**
	 * @author kishore
	 * Insert Recording
	 * @param site
	 * @param supervisorId
	 * @param recName
	 * @param filePath
	 * @return
	 */
	public static String insertRecording(String site, String supervisorId, String recName, String filePath)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
						.append("<InsertRecording xmlns=\"http://tempuri.org/\">")
						.append("<objRecording>")
						.append("<Site>").append(site).append("</Site>")
						.append("<SupervisorId>").append(supervisorId).append("</SupervisorId>")
						.append("<RecordingName>").append(recName).append("</RecordingName>")
						.append("<FilePath>").append(filePath).append("</FilePath>")
						.append("</objRecording>")
						.append("</InsertRecording>")
						.append(SOAP_FOOTER) ;
		LogUtils.errorLog("requestXML", strXML.toString());
		return strXML.toString();
	}
	
	/**
	 * Delete Recording
	 * @param recordingId
	 * @return
	 */
	public static String deleteRecording(String recordingId)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
					.append("<DeleteRecording xmlns=\"http://tempuri.org/\">")
					.append("<RecordingId>").append(recordingId).append("</RecordingId>")
					.append("</DeleteRecording>")
						.append(SOAP_FOOTER) ;
		
		return strXML.toString();
	}
	
	/**
	 * Update Recording
	 * @param recordingId
	 * @param newName
	 * @return
	 */
	public static String updateRecording(String recordingId, String newName)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
					.append("<UpdateRecordingName xmlns=\"http://tempuri.org/\">")
					.append("<RecordingId>").append(recordingId).append("</RecordingId>")
					.append("<NewName>").append(newName).append("</NewName>")
					.append("</UpdateRecordingName>")
					.append(SOAP_FOOTER) ;

		return strXML.toString();
	}
	/**
	 * @param vecOptions
	 * @return String
	 */
	public static String postSurveyAnswer(String strCustomerSitId, String strPresellerId,String strSupervisorId, Vector<QuestionOptionDO> vecOptions)
	{
		StringBuilder strBody = new StringBuilder();
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)     
		  			.append("<ImportCustomersSurvey xmlns=\"http://tempuri.org/\">")
					.append("<CustomerSurveyRequest>")
					.append("<CustomersSurvey>")
					.append("<CustomerSurvey>")
					.append("<Site>").append(strCustomerSitId).append("</Site>")
					.append("<PresellerId>").append(strPresellerId).append("</PresellerId>")
					.append("<SupervisorId>").append(strSupervisorId).append("</SupervisorId>")
					.append("<Options>");
		
		 			for(QuestionOptionDO objQuestionOption : vecOptions)
		 			{
		 				strBody.append("<Option>")
		 		          		  .append("<QuestionId>").append(objQuestionOption.questionId).append("</QuestionId>")
		 		          		  .append("<Question>").append(objQuestionOption.questionDesc).append("</Question>")
		 		          		  .append("<OptionId>").append(objQuestionOption.optionId).append("</OptionId>")
		 						  .append("<Option>").append(objQuestionOption.optionDesc).append("</Option>")
		 		        		  .append("</Option>");
		 			}
		 			
		 		strXML.append(strBody)
					 .append("</Options>")
					 .append("</CustomerSurvey>")
					 .append("</CustomersSurvey>")
					 .append("</CustomerSurveyRequest>")
					 .append("</ImportCustomersSurvey>")
					 .append(SOAP_FOOTER) ;
//		writeToSdcards(strXML);
		return strXML.toString();
	}
	
	/**
	 * Fetch all deleted items request
	 * @param last sync time
	 * @return String request.
	 */
	public static String getAllDeletedItems(String lastSync)
	{
		LogUtils.errorLog("GetAllHHDeletedItems","GetAllHHDeletedItems "+ lastSync);
		
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
					.append("<GetAllHHDeletedItems  xmlns=\"http://tempuri.org/\">")
					.append("<LastSync>").append(lastSync).append("</LastSync>")
					.append("</GetAllHHDeletedItems >")
						.append(SOAP_FOOTER) ;
		
		return strXML.toString();
	}
	
	public static String getAllPassCode(String preselerId)
	{
		LogUtils.errorLog("GetAllPassCode - ", "GetAllPassCode - "+preselerId);
		
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
					.append("<GetAllPassCode xmlns=\"http://tempuri.org/\">")
					.append("<PreSellerId>").append(preselerId).append("</PreSellerId>")
					.append("</GetAllPassCode >")
					.append(SOAP_FOOTER) ;
		
		return strXML.toString();
		
	}
	
	public static String getAllVehicles(String empNo)
	{
		LogUtils.errorLog("getAllVehicles - ", "getAllVehicles - "+empNo);
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
					.append("<getAllVehicles xmlns=\"http://tempuri.org/\">")
					.append("<empNo>").append(empNo).append("</empNo>")
					.append("</getAllVehicles>")
					.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	
	public static String getAllAvailablePassCode(String empNo)
	{
		
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
					.append("<GetAllAvailablePassCode xmlns=\"http://tempuri.org/\">")
					.append("<EmpId>").append(empNo).append("</EmpId>")
					.append("</GetAllAvailablePassCode >")
						.append(SOAP_FOOTER) ;
		
		return strXML.toString();
		
//		String strXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
//				"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
//					"<soap:Body>" +
//						"<GetAllAvailablePassCode xmlns=\"http://tempuri.org/\">" +
//							"<EmpId>")empNo).append("</EmpId>" +
//						"</GetAllAvailablePassCode>" +
//					"</soap:Body>" +
//				"</soap:Envelope>";
//		return strXML.toString();
	}
	public static String getAllAvailableDAPassCode(String empNo)
	{
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
					.append("<GetAllAvailableDAPassCodeRegionWise xmlns=\"http://tempuri.org/\">")
					.append("<EmpId>").append(empNo).append("</EmpId>")
					.append("</GetAllAvailableDAPassCodeRegionWise >")
						.append(SOAP_FOOTER) ;
		
		return strXML.toString();
		
		
//		String strXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
//				"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
//					"<soap:Body>" +
//						"<GetAllAvailableDAPassCodeRegionWise xmlns=\"http://tempuri.org/\">" +
//							"<EmpId>")empNo).append("</EmpId>" +
//						"</GetAllAvailableDAPassCodeRegionWise>" +
//					"</soap:Body>" +
//				"</soap:Envelope>";
//		return strXML.toString();
	}
	/**
	 * method to post the Scaned data
	 * @param objScanResultObject 
	 * @return String
	 */
	public static String importBarCodeInfo(ScanResultObject objScanResultObject)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
					.append("<ImportBarCodeInfo xmlns=\"http://tempuri.org/\">")
					.append("<BarcodeInfoRequest>")
					.append("<Barcodes>")
					.append("<Barcode>")
					.append("<CustomerCode>").append(objScanResultObject.customerSiteId).append("</CustomerCode>")
					.append("<Type>").append(objScanResultObject.type).append("</Type>")
					.append("<Location>").append(objScanResultObject.location).append("</Location>")
					.append("<BarCode>").append(objScanResultObject.strBarcodeImage).append("</BarCode>")
					.append("<Comments>").append(objScanResultObject.comments).append("</Comments>")
					.append("<ScanTime>").append(objScanResultObject.time).append("</ScanTime>")
					.append("<ItemCode>").append(objScanResultObject.productId).append("</ItemCode>")
					.append("<SalesmanCode>").append(objScanResultObject.EmpId).append("</SalesmanCode>")
					.append("<Quantity>").append(objScanResultObject.Quantity).append("</Quantity>")
					.append("</Barcode>")
					.append("</Barcodes>")
					.append("</BarcodeInfoRequest>")
					.append("</ImportBarCodeInfo>")
						.append(SOAP_FOOTER) ;

		return strXML.toString();
	}
	
	public static String securityCheckedCompleted(String truckNo)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
					.append("<SecurityCheckedCompleted xmlns=\"http://tempuri.org/\">")
					.append("<TruckNumber>").append(truckNo).append("</TruckNumber>")
					.append("</SecurityCheckedCompleted>")
					.append(SOAP_FOOTER) ;
		LogUtils.errorLog("strXML", "strXML"+strXML);
		return strXML.toString();
	}
	
	public static String logisticsCheckedCompleted(String truckNo)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
					.append("<LogisticsCheckCompleted xmlns=\"http://tempuri.org/\">")
					.append("<OrderNumber>").append(truckNo).append("</OrderNumber>")
					.append("</LogisticsCheckCompleted>")
					.append(SOAP_FOOTER) ;
		LogUtils.errorLog("strXML", "strXML"+strXML);
		return strXML.toString();
	}
	
	public static String getOrderPerDay(String salesManCode, String date)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
					.append("<GetOrderPerDay xmlns=\"http://tempuri.org/\">")
					.append("<SalesmanCode>").append(salesManCode).append("</SalesmanCode>")
					.append("<FetchDate>").append(date).append("</FetchDate>")
					.append("</GetOrderPerDay>")
						.append(SOAP_FOOTER) ;
		LogUtils.errorLog("strXML", "strXML"+strXML);
		return strXML.toString();
	}
	
	public static String getReceiptPerDay(String salesManCode, String date)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
					.append("<GetReceiptPerDay xmlns=\"http://tempuri.org/\">")
					.append("<SalesmanCode>").append(salesManCode).append("</SalesmanCode>")
					.append("<FetchDate>").append(date).append("</FetchDate>")
					.append("</GetReceiptPerDay>")
						.append(SOAP_FOOTER) ;
		LogUtils.errorLog("strXML", "strXML"+strXML);
		return strXML.toString();
	}
	
	public static String getJournyLogPerDay(String salesManCode)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
					.append("<GetRoutePlanPerDay  xmlns=\"http://tempuri.org/\">")
					.append("<SalesManCode>").append(salesManCode).append("</SalesManCode>")
					.append("</GetRoutePlanPerDay >")
						.append(SOAP_FOOTER) ;

		LogUtils.errorLog("strXML", "strXML"+strXML);
		return strXML.toString();
	}
	
	public static String insertJournyLogPerDay(String salesManCode, String request)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
					.append("<InsertRoutePlanPerDay   xmlns=\"http://tempuri.org/\">")
					.append("<RequestString>").append(request).append("</RequestString>")
					.append("<SalesManCode>").append(salesManCode).append("</SalesManCode>")
					.append("</InsertRoutePlanPerDay  >")
						.append(SOAP_FOOTER) ;

		LogUtils.errorLog("strXML", "strXML"+strXML);
		return strXML.toString();
	}
	
	 
	public static String prepareJournyPlanXml(ArrayList<MallsDetails> vecJourneyLog)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<&lt;JournyLogs&gt;");
		for(MallsDetails planDO :vecJourneyLog)
		{
			builder.append("<&lt;JournyLog&gt;").
			append("&lt;DateOfJourney&gt;").append(planDO.dateofJorney).append("<&lt;/DateOfJourney&gt;").
			append("&lt;PresellerId&gt;").append(planDO.presellerId).append("<&lt;/PresellerId&gt;").
			append("&lt;CustomerSiteId&gt;").append(planDO.customerSiteId).append("<&lt;/CustomerSiteId&gt;").
			append("&lt;CustomerPasscode&gt;").append(planDO.customerPasscode).append("<&lt;/CustomerPasscode&gt;").
			append("&lt;Stop&gt;").append(planDO.Stop).append("<&lt;/Stop&gt;").
			append("&lt;Distance&gt;").append(planDO.Distance).append("<&lt;/Distance&gt;").
			append("&lt;TravelTime&gt;").append(planDO.TravelTime).append("<&lt;/TravelTime&gt;").
			append("&lt;ArrivalTime&gt;").append(planDO.ActualArrivalTime).append("<&lt;/ArrivalTime&gt;").
			append("&lt;SeviceTime&gt;").append(planDO.SeviceTime).append("<&lt;/SeviceTime&gt;").
			append("&lt;ReasonForSkip&gt;").append(planDO.reasonForSkip).append("<&lt;/ReasonForSkip&gt;").
			append("&lt;OutTime&gt;").append(planDO.ActualOutTime).append("<&lt;/OutTime&gt;").
			append("&lt;TotalTimeAtOutLet&gt;").append(planDO.TotalTime).append("<&lt;/TotalTimeAtOutLet&gt;").
			append("&lt;/JournyLog&gt;");
		}
		builder.append("<&lt;/JournyLogs&gt;");
		
		LogUtils.errorLog("JournyLogs", ""+builder.toString());
		
		return builder.toString();
	}
	
	//method to get all the EOT for supervisor
	public static String insertCustomerVisit(ArrayList<MallsDetails> vecJourneyLog,String empNo)
	{
		StringBuilder strBody = new StringBuilder();
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
		 			.append("<InsertCustomerVisit xmlns=\"http://tempuri.org/\">")
		 			.append("<ImportCustomerVisit>")
		 			.append("<CustomerVisits>");
						
		for(MallsDetails planDO :vecJourneyLog)
		{
			strBody.append("<CustomerVisit>")
							.append("<EmpNo>").append(empNo).append("</EmpNo>")
   							.append("<CustomerSiteId>").append(planDO.customerSiteId).append("</CustomerSiteId>")
   							.append("<Date>").append(CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00").append("</Date>")
   							.append("<ArrivalTime>").append(planDO.ActualArrivalTime).append("</ArrivalTime>")
   							.append("<OutTime>").append((planDO.ActualOutTime!=null?planDO.ActualOutTime:planDO.ActualArrivalTime)).append("</OutTime>")
   							.append("<TotalTimeInMins>").append((planDO.TotalTime!=null ? planDO.TotalTime : "1")).append("</TotalTimeInMins>")
   							.append("</CustomerVisit>");
		}

		strXML.append(strBody)
				.append("</CustomerVisits>")
				.append("</ImportCustomerVisit>")
				.append("</InsertCustomerVisit>")
				.append(SOAP_FOOTER) ;
		
		LogUtils.errorLog("getJourneyDetails", ""+strXML.toString());
		return strXML.toString();
	}
	
	//method to get all the EOT for supervisor
	public static String getEOTDetails(String empNo, String currentDate)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
		 			.append("<getEOTDetails xmlns=\"http://tempuri.org/\">")
					.append("<EmpNo>").append(empNo).append("</EmpNo>")
					.append("<DATE>").append(currentDate).append("</DATE>")
					.append("</getEOTDetails>")
						.append(SOAP_FOOTER) ;
		
		LogUtils.errorLog("getEOTDetails", "getEOTDetails "+strXML);
		return strXML.toString();
	}
	
	public static String prepareLogisticMismatchXml(ArrayList<DeliveryAgentOrderDetailDco> vector, String reason, String orderNumber)
	{
		StringBuilder strDiscrepency = new StringBuilder();
		for(DeliveryAgentOrderDetailDco obj : vector)
		{
			strDiscrepency.append("<Discrepancy>")
				.append("<OrderNumber>").append(orderNumber).append("</OrderNumber>")
				.append("<OrderItem>").append(obj.itemCode).append("</OrderItem>")
				.append("<ExpectedQuantity>").append(obj.preCases).append("</ExpectedQuantity>")
				.append("<ActualQuantity>").append(obj.cases).append("</ActualQuantity>")
				.append("</Discrepancy>");
		}
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
			.append("<LogisticSupervisorMismatch xmlns=\"http://tempuri.org/\">")
			.append("<LogisticSupervisorDiscrepancy>")
			.append("<Reason>").append(reason).append("</Reason>")
			.append("<Discrepancies>")
			.append(strDiscrepency)
			.append("</Discrepancies>")
			.append("</LogisticSupervisorDiscrepancy>")
			.append("</LogisticSupervisorMismatch>")
			.append(SOAP_FOOTER) ;
		
		LogUtils.errorLog("LogisticSupervisorMismatch", "strXML"+strXML);
		
		return strXML.toString();
	}
	
	//login request
	public static String updatePasscodeStatus(String strPresellerId, String passcode)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
				 	.append("<UpdateDAPassCodeStatus xmlns=\"http://tempuri.org/\">")
	     			.append("<EmpNo>").append(strPresellerId).append("</EmpNo>")
				 	.append("<PassCode>").append(passcode).append("</PassCode>")
				 	.append("</UpdateDAPassCodeStatus>")
						.append(SOAP_FOOTER) ;
		
		Log.e("strXML - ", "strXML - "+strXML);
		return strXML.toString();
	}
	
	public static String insertCustomerGeoCode(JourneyPlanDO journeyPlanDO , String EmpNo)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
		 		.append("<InsertCustomerGeoCode xmlns=\"http://tempuri.org/\">")
		 			.append("<CustomerGeoCodeRequest>")
		 			.append("<Customers>")
		 				.append("<Customer>")
		 					.append("<SiteNumber>").append(journeyPlanDO.site).append("</SiteNumber>")
		 					.append("<EmpNo>").append(EmpNo).append("</EmpNo>")
		 					.append("<GEO_CODE_X>").append(journeyPlanDO.geoCodeX).append("</GEO_CODE_X>")
		 					.append("<GEO_CODE_Y>").append(journeyPlanDO.geoCodeY).append("</GEO_CODE_Y>")
		 				.append("</Customer>")
		 			.append("</Customers>")
		 		.append("</CustomerGeoCodeRequest>")
		 		.append("</InsertCustomerGeoCode>")
				.append(SOAP_FOOTER) ;
		LogUtils.errorLog("InsertCustomerGeoCode", ""+strXML);
		return strXML.toString();
	}
	public static String getPasscodeForDa(String EmpNo)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
						.append("<GetAllAvailableDAPassCode xmlns=\"http://tempuri.org/\">")
 						.append("<EmpId>").append(EmpNo).append("</EmpId>")
 						.append("</GetAllAvailableDAPassCode>")
	     				.append(SOAP_FOOTER) ;
		LogUtils.errorLog("GetAllAvailableDAPassCode", ""+strXML);
		return strXML.toString();
	}
	
	public static String insertHHCustomer(Vector<NewCustomerDO> vecNewCustomerDO, String route)
	{
		
		StringBuilder strHHCustomer = new StringBuilder();
		
		for(int i = 0; i < vecNewCustomerDO.size(); i++)
		{
			NewCustomerDO obj = vecNewCustomerDO.get(i);
			
			strHHCustomer.append("<HHCustomerDco>")
							.append("<SiteId>").append(obj.CustomerSiteId).append("</SiteId>")
							.append("<SALESMAN>").append(obj.salesman).append("</SALESMAN>")
							.append("<SITE_NAME>").append(obj.customerName).append("</SITE_NAME>")
							.append("<PARTY_NAME>").append(obj.customerName).append("</PARTY_NAME>")
							.append("<REGION_CODE>").append(obj.region).append("</REGION_CODE>")
							.append("<REGION_DESCRIPTION>").append(obj.region).append("</REGION_DESCRIPTION>")
							.append("<COUNTRY_CODE>").append(obj.countryName).append("</COUNTRY_CODE>")
							.append("<COUNTRY_DESCRIPTION>").append(obj.countryDesc).append("</COUNTRY_DESCRIPTION>")
							.append("<ADDRESS1>").append(obj.address1).append("</ADDRESS1>")
							.append("<ADDRESS2>").append(obj.address2).append("</ADDRESS2>")
							.append("<ADDRESS3></ADDRESS3>")
							.append("<ADDRESS4></ADDRESS4>")
							.append("<PO_BOX_NUMBER></PO_BOX_NUMBER>")
							.append("<CITY>").append(obj.CITY).append("</CITY>")
							.append("<PAYMENT_TYPE></PAYMENT_TYPE>")
							.append("<PAYMENT_TERM_CODE></PAYMENT_TERM_CODE>")
							.append("<PAYMENT_TERM_DESCRIPTION></PAYMENT_TERM_DESCRIPTION>")
							.append("<CREDIT_LIMIT>0</CREDIT_LIMIT>")
							.append("<GEO_CODE_X>").append(obj.latitude).append("</GEO_CODE_X>")
							.append("<GEO_CODE_Y>").append(obj.longitude).append("</GEO_CODE_Y>")
							.append("<EMAIL>").append(obj.email).append("</EMAIL>")
							.append("<CONTACTPERSONNAME>").append(obj.contactPerson).append("</CONTACTPERSONNAME>")
							.append("<CONTACTPERSONMOBILENO>").append(obj.mobileNo).append("</CONTACTPERSONMOBILENO>")
							.append("<AppKey>").append(obj.AppUUID).append("</AppKey>")
							.append("<MOBILENO1>").append(obj.landline).append("</MOBILENO1>")
							.append("<CountryId>").append(obj.countryId).append("</CountryId>")
							.append("<DOB>").append(obj.DOB).append("</DOB>")
							.append("<AnniversaryDate>").append(obj.anniversary).append("</AnniversaryDate>")
							.append("<Source>").append(route).append("</Source>")
							.append("<customerType>").append(1).append("</customerType>")
							.append("<PASSCODE>").append(12345).append("</PASSCODE>") 
							.append("<Buyer>").append(obj.buyerStatus).append("</Buyer>") 
							.append("<CompetitionBrand>").append(obj.competitionBrand).append("</CompetitionBrand>") 
							.append("<SKU>").append(obj.sku).append("</SKU>") 
						.append("</HHCustomerDco>");
		}
		
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
					.append("<InsertHHCustomerOffline xmlns=\"http://tempuri.org/\">")
					.append("<objImportCustomer>")
					.append("<Customers>")
					.append(strHHCustomer)
					.append("</Customers>")
					.append("</objImportCustomer>")
					.append("</InsertHHCustomerOffline>")
					.append(SOAP_FOOTER) ;
		
		LogUtils.errorLog("error", strXML.toString());
		if(strXML.toString().contains("&"))
			strXML.toString().replace("&", "&amp;");
		
		LogUtils.errorLog("InsertHHCustomerOffline", ""+strXML);
		
		return strXML.toString();
	}
	
	public static String updateHHCustomerGEO(Vector<MallsDetails> vecNewCustomerDO)
	{
		StringBuilder strHHCustomer  = new StringBuilder();
		for(int i = 0; i < vecNewCustomerDO.size(); i++)
		{
			MallsDetails obj = vecNewCustomerDO.get(i);
			
			strHHCustomer.append("<HHCustomerDco>")
							.append("<SiteId>").append(obj.customerSiteId).append("</SiteId>")
							.append("<GEO_CODE_X>").append(obj.Latitude).append("</GEO_CODE_X>")
							.append("<GEO_CODE_Y>").append(obj.Longitude).append("</GEO_CODE_Y>")
						.append("</HHCustomerDco>");
		}
		
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
				 	.append("<UpdateHHCustomer xmlns=\"http://tempuri.org/\">")
					.append("<objImportCustomer>")
					.append("<Customers>")
					.append(strHHCustomer)
					.append("</Customers>")
					.append("</objImportCustomer>")
					.append("</UpdateHHCustomer>")
					.append(SOAP_FOOTER) ;
		
		LogUtils.errorLog("error", strXML.toString());
		
		if(strXML.toString().contains("&"))
			strXML.toString().replace("&", "&amp;");
		
		return strXML.toString();
	}
	
	public static String getSalesManLandmarkSynce(String salesmanCode, String lastSyncDate)
	{
		LogUtils.errorLog("GetSalesmanLandmarkWithSync - ", "GetSalesmanLandmarkWithSync "+lastSyncDate);
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
					.append("<GetSalesmanLandmarkWithSync xmlns=\"http://tempuri.org/\">")
					.append("<SalesmanCode>").append(salesmanCode).append("</SalesmanCode>")
					.append("<LastSyncDate>").append(lastSyncDate).append("</LastSyncDate>")
					.append("</GetSalesmanLandmarkWithSync>")
						.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	
	/**
	 * Method to build request to get all Vehicles
	 * @param strSyncTime
	 * @param string 
	 * @return String
	 */
	public static String getVehicles(String empId , String lsd, String lst)
	{
		LogUtils.errorLog("getVehicles - ", "getVehicles - "+lsd+" - "+lst);
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
		 			.append("<GetVehicles xmlns=\"http://tempuri.org/\">")
		 			.append("<UserCode>").append(empId).append("</UserCode>")
		 			.append("<lsd>").append(lsd).append("</lsd>")
		 			.append("<lst>").append(lst).append("</lst>")
		 			.append("</GetVehicles>")
						.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	
	/**
	 * Method to build request to get Things to focus
	 * @param strSyncTime
	 * @param string 
	 * @return String
	 */
	public static String getThingstofocus(String empId , String lsd, String lst)
	{
		LogUtils.errorLog("getThingstofocus - ", "getThingstofocus - "+lsd+" - "+lst);
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
		 			.append("<GetThingsToFocusesByUserId xmlns=\"http://tempuri.org/\">")
		 			.append("<UserCode>").append(empId).append("</UserCode>")
		 			.append("<lsd>").append(lsd).append("</lsd>")
		 			.append("<lst>").append(lst).append("</lst>")
		 			.append("</GetThingsToFocusesByUserId>")
					.append(SOAP_FOOTER) ;
		LogUtils.errorLog("getThingstofocus Reqyest ", strXML.toString());
		return strXML.toString();
		
	}
	
	/**
	 * Method to build request to get all Inventory
	 * @param strSyncTime
	 * @param string 
	 * @return String
	 */
	public static String getVMInventory(String strEmpNo , String strSyncTime, String strCurrentDate)
	{
		LogUtils.errorLog("getVMInventory - ", "getVMInventory - "+strSyncTime);
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
					.append("<GetVMSalesmanInventory xmlns=\"http://tempuri.org/\">")
	      			.append("<LastSyncDate></LastSyncDate>")
	      			.append("<Empno>").append(strEmpNo).append("</Empno>")
	      			.append("<Date>").append(strCurrentDate).append("</Date>")
	      			.append("</GetVMSalesmanInventory>")
						.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	
	/**
	 * Method to build request to get all Vehicles
	 * @param strSyncTime
	 * @param string 
	 * @return String
	 */
	public static String getSequenceNoBySalesmanForHH(String strSalesmanNo)
	{
		LogUtils.errorLog("GetSequenceNoBySalesmanForHH - ", "GetSequenceNoBySalesmanForHH "+strSalesmanNo);
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
		 			.append("<GetAvailableTrxNos xmlns=\"http://tempuri.org/\">")
		 			.append("<UserCode>").append(strSalesmanNo).append("</UserCode>")
		 			.append("</GetAvailableTrxNos>")
						.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	
	public static String getHHSummary(String empNo, String strSyncTime, String currentdate)
	{
		LogUtils.errorLog("getAllRegions - ", ""+strSyncTime);
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER) 
						 .append("<GetVMSummary xmlns=\"http://tempuri.org/\">")
					     .append("<LastSync>").append(strSyncTime).append("</LastSync>")
					     .append("<EmpNo>").append(empNo).append("</EmpNo>")
					     .append("<CurrentDate>").append(currentdate).append("</CurrentDate>")
					     .append("</GetVMSummary>")
						 .append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	public static String getAllHHCustomerDeletedItems(String empNo, String strSyncTime)
	{
		LogUtils.errorLog("GetAllHHCustomerDeletedItems - ", "GetAllHHCustomerDeletedItems - "+strSyncTime);
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
						.append("<GetAllHHCustomerDeletedItems xmlns=\"http://tempuri.org/\">")
						.append("<LastSync>").append(strSyncTime).append("</LastSync>")
						.append("<Empno>").append(empNo).append("</Empno>")
						.append("</GetAllHHCustomerDeletedItems>")
						  .append(SOAP_FOOTER) ;
		
		LogUtils.errorLog("strXML", "strXML - "+strXML);
		return strXML.toString();
	}
	
	public static String insertStock(String empNo, ArrayList<DeliveryAgentOrderDetailDco> arrayList)
	{
		StringBuilder body= new StringBuilder();
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
				 	.append("<InsertStock xmlns=\"http://tempuri.org/\">")
	      			.append("<objImportStock>")
	      			.append("<Inventories>");
		for(DeliveryAgentOrderDetailDco detailDco : arrayList)
		{
			body.append("<VMSalesmanInventoryDco>")
				.append("<VMSalesmanInventoryId>").append(0).append("</VMSalesmanInventoryId>")
				.append("<Date>").append(CalendarUtils.getOrderPostDate()).append("</Date>")
				.append("<EmpNo>").append(empNo).append("</EmpNo>")
				.append("<ItemCode>").append(detailDco.itemCode).append("</ItemCode>")
				.append("<PrimaryQuantity>").append(detailDco.preCases).append("</PrimaryQuantity>")
				.append("<SecondaryQuantity>").append(detailDco.preUnits).append("</SecondaryQuantity>")
				.append("</VMSalesmanInventoryDco>");
		}
		strXML.append(body) 
				.append("</Inventories>")
				.append("</objImportStock>")
	      		.append("</InsertStock>")
				.append(SOAP_FOOTER) ;
		
		LogUtils.errorLog("InsertStock", ""+strXML);
		return strXML.toString();
	}
	
	public static String insertCheckInDemandStock(Vector<CheckInDemandInventoryDO> vecInDemandInventoryDOs)
	{
		StringBuilder body = new StringBuilder();
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
				 	.append("<ImportCheckInDemandStock xmlns=\"http://tempuri.org/\">")
	      			.append("<objImportStock>")
	      			.append("<Inventories>");
		for(CheckInDemandInventoryDO checkInDemandInventoryDO : vecInDemandInventoryDOs)
		{
			body.append("<VMSalesmanInventoryDco>")
				.append("<VMSalesmanInventoryId>").append(0).append("</VMSalesmanInventoryId>")
				.append("<Date>").append(checkInDemandInventoryDO.Date).append("</Date>")
				.append("<EmpNo>").append(checkInDemandInventoryDO.EmpNo).append("</EmpNo>")
				.append("<ItemCode>").append(checkInDemandInventoryDO.ItemCode).append("</ItemCode>")
					
//				.append("<PrimaryQuantity>")detailDco.totalCases).append("</PrimaryQuantity>")
//				.append("<SecondaryQuantity>")detailDco.totalCases).append("</SecondaryQuantity>")
					
					//Changed by awaneesh
				.append("<PrimaryQuantity>").append(checkInDemandInventoryDO.PrimaryQuantity).append("</PrimaryQuantity>")
				.append("<SecondaryQuantity>").append(checkInDemandInventoryDO.SecondaryQuantity).append("</SecondaryQuantity>")
				.append("</VMSalesmanInventoryDco>");
		}
		strXML.append(body) 
				.append("</Inventories>")
				.append("</objImportStock>")
	      		.append("</ImportCheckInDemandStock>")
				 .append(SOAP_FOOTER) ;
		
		LogUtils.errorLog("strXML", "strXML - "+strXML);
		return strXML.toString();
	}
	
	public static String insertDemandStock(Vector<CheckInDemandInventoryDO> vecInDemandInventoryDOs)
	{
		StringBuilder body = new StringBuilder();
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
				 	.append("<InsertStock xmlns=\"http://tempuri.org/\">")
	      			.append("<objImportStock>")
	      			.append("<Inventories>");
		for(CheckInDemandInventoryDO checkInDemandInventoryDO : vecInDemandInventoryDOs)
		{
			body.append("<VMSalesmanInventoryDco>")
				.append("<VMSalesmanInventoryId>").append(0).append("</VMSalesmanInventoryId>")
				.append("<Date>").append(checkInDemandInventoryDO.Date).append("</Date>")
				.append("<EmpNo>").append(checkInDemandInventoryDO.EmpNo).append("</EmpNo>")
				.append("<ItemCode>").append(checkInDemandInventoryDO.ItemCode).append("</ItemCode>")
					
//				.append("<PrimaryQuantity>")detailDco.totalCases).append("</PrimaryQuantity>")
//				.append("<SecondaryQuantity>")detailDco.totalCases).append("</SecondaryQuantity>")
					
					//Changed by awaneesh
				.append("<PrimaryQuantity>").append(checkInDemandInventoryDO.PrimaryQuantity).append("</PrimaryQuantity>")
				.append("<SecondaryQuantity>").append(checkInDemandInventoryDO.SecondaryQuantity).append("</SecondaryQuantity>")
				.append("</VMSalesmanInventoryDco>");
		}
		strXML.append(body) 
				.append("</Inventories>")
				.append("</objImportStock>")
	      		.append("</InsertStock>")
				.append(SOAP_FOOTER) ;
		
		LogUtils.errorLog("strXML", "strXML - "+strXML);
		return strXML.toString();
	}
	
	public static String getAdvanceOrderByEmpNo(String empNo, String strSyncTime)
	{
		LogUtils.errorLog("GetAdvanceOrderByEmpNo - ", "GetAdvanceOrderByEmpNo - "+strSyncTime);
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
						.append("<GetAdvanceOrderByEmpNo xmlns=\"http://tempuri.org/\">")
						.append("<EmpNo>").append(empNo).append("</EmpNo>")
						.append("</GetAdvanceOrderByEmpNo>")
						.append(SOAP_FOOTER) ;
		
		LogUtils.errorLog("strXML", "strXML - "+strXML);
		return strXML.toString();
	}
	
	public static String updateReturnStock(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String strEmpNo)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
						.append("<UpdateReturnStock xmlns=\"http://tempuri.org/\">")
						.append("<objImportStock>")
						.append("<Inventories>").append(getVMSalesmanInventoryDcos(vecOrdProduct, strEmpNo)).append("</Inventories>")
						.append("</objImportStock>")
						.append("</UpdateReturnStock>")
							.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	
	private static String getVMSalesmanInventoryDcos(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String strEmpNo)
	{
		StringBuilder strXML = new StringBuilder();
		for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : vecOrdProduct) 
		{
			strXML.append("<VMSalesmanInventoryDco>")
					.append("<VMSalesmanInventoryId></VMSalesmanInventoryId>")	
					.append("<Date>").append(CalendarUtils.getOrderPostDate()).append("</Date>")
					.append("<EmpNo>").append(strEmpNo).append("</EmpNo>")
					.append("<ItemCode>").append(deliveryAgentOrderDetailDco.itemCode).append("</ItemCode>")
					.append("<PrimaryQuantity>0</PrimaryQuantity>")
					.append("<SecondaryQuantity>0</SecondaryQuantity>")
					.append("<PrimaryReturnQuantity>").append(deliveryAgentOrderDetailDco.preCases).append("</PrimaryReturnQuantity>")
					.append("<SecondaryReturnQuantity>").append(deliveryAgentOrderDetailDco.preUnits).append("</SecondaryReturnQuantity>")
					.append("<FeederEmpNo>0</FeederEmpNo>")
					.append("</VMSalesmanInventoryDco>");
		}
		return strXML.toString();
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static String getVMSalesmanInventoryDcoINUpload(TransferInoutDO transferInoutDO)
	{
		StringBuilder strXML = new StringBuilder();
		for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : transferInoutDO.vecOrderDetailDcos) 
		{
			strXML.append("<VMSalesmanInventoryDco>")
					.append("<VMSalesmanInventoryId></VMSalesmanInventoryId>")	
					.append("<Date>").append(CalendarUtils.getDeliverydate()).append("</Date>")
					.append("<EmpNo>").append(transferInoutDO.fromEmpNo).append("</EmpNo>")
					.append("<ItemCode>").append(deliveryAgentOrderDetailDco.itemCode).append("</ItemCode>")
					.append("<PrimaryQuantity>").append(deliveryAgentOrderDetailDco.preCases).append("</PrimaryQuantity>")
					.append("<SecondaryQuantity>").append(deliveryAgentOrderDetailDco.preUnits).append("</SecondaryQuantity>")
					.append("<PrimaryReturnQuantity>0</PrimaryReturnQuantity>")
					.append("<SecondaryReturnQuantity>0</SecondaryReturnQuantity>")
					.append("<FeederEmpNo>").append(transferInoutDO.toEmpNo).append("</FeederEmpNo>")
					.append("</VMSalesmanInventoryDco>");
		}
		return strXML.toString();
	}
	
	
	public static String PostTransferOuts(Vector<TransferInoutDO> vecTransferInoutDOs)
	{
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER) 
				.append("<PostTransfers xmlns=\"http://tempuri.org/\">")
				.append("<objTransferHeaderDcos>").append(getTransferHeaderDCO(vecTransferInoutDOs))
				.append("</objTransferHeaderDcos>")
				.append("</PostTransfers>")
				.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	
	private static String getTransferHeaderDCO(Vector<TransferInoutDO> vecTransferInoutDOs)
	{
		StringBuilder strXML = new StringBuilder();
		for (TransferInoutDO transferInoutDO : vecTransferInoutDOs) 
		{
			String transferType = "";
			if(transferInoutDO.trnsferType.equalsIgnoreCase("IN"))
				transferType = "1";
			else
				transferType = "0";
			strXML.append("<TransferHeaderDco>")
		.append("<TransferId>").append(transferInoutDO.InventoryUID).append("</TransferId>")
		.append("<SourceEmpCode>").append(transferInoutDO.fromEmpNo).append("</SourceEmpCode>")
		.append("<DestinationEmpCode>").append(transferInoutDO.toEmpNo).append("</DestinationEmpCode>")
		.append("<SourceVehicleCode>").append(transferInoutDO.sourceVNO).append("</SourceVehicleCode>")
		.append("<DestinationVehicleCode>").append(transferInoutDO.destVNO).append("</DestinationVehicleCode>")
		.append("<Date>").append(transferInoutDO.Date).append("</Date>")
		.append("<Type>").append(transferType).append("</Type>")
		.append("<OrderNumber>").append(transferInoutDO.sourceOrderID).append("</OrderNumber>")
		.append("<SourceOrderNumber>").append(transferInoutDO.destOrderID).append("</SourceOrderNumber>")
		.append("<Status>").append(0).append("</Status>")
		.append("<TransferDetailDcos>")
		.append(getTransferDcos(transferInoutDO.vecOrderDetailDcos, transferInoutDO.InventoryUID, transferInoutDO.sourceOrderID))
		.append("</TransferDetailDcos>")
		.append("</TransferHeaderDco>");
		}
		return strXML.toString();
	}
	
	private static String getTransferDcos(ArrayList<DeliveryAgentOrderDetailDco> vecOrderDetailDcos, String transferId, String orderNumber)
	{
		StringBuilder strXML = new StringBuilder();
		for (DeliveryAgentOrderDetailDco detailDco:  vecOrderDetailDcos) 
		{
			  strXML.append("<TransferDetailDco>")
					.append("<TransferDetailId>").append(detailDco.transferDetailID).append("</TransferDetailId>")
					.append("<TransferId>").append(transferId).append("</TransferId>")
					.append("<ItemCode>").append(detailDco.itemCode).append("</ItemCode>")
					.append("<ReqestedCases>").append(detailDco.preCases).append("</ReqestedCases>")
					.append("<ReqestedUnits>").append(detailDco.preUnits).append("</ReqestedUnits>")
					.append("<TransferredCases>").append(detailDco.preCases).append("</TransferredCases>")
					.append("<TransferredUnits>").append(detailDco.preUnits).append("</TransferredUnits>")
					.append("<ReqestedQty>").append(detailDco.totalCases).append("</ReqestedQty>")
					.append("<TransferredQty>").append(detailDco.totalCases).append("</TransferredQty>")
					.append("<OrderNumber>").append(orderNumber).append("</OrderNumber>")
					.append("</TransferDetailDco>");
		}
		return strXML.toString();
	}
	public static String UpdateVanTransferStockUpload(ArrayList<LoadRequestDO> vecTransferInoutDOs)
	{
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER) 
			.append("<UpdateTransferInOrOutStock xmlns=\"http://tempuri.org/\">")
			.append("<objTransferInOrOutStock>")
			.append("<Inventories>").append(getVanTransferStock(vecTransferInoutDOs)).append("</Inventories>")
			.append("</objTransferInOrOutStock>")
			.append("</UpdateTransferInOrOutStock>")
		.append(SOAP_FOOTER) ;
		
		LogUtils.debug("getVanTransferStock", strXML.toString());
		return strXML.toString();
	}
	private static String getVanTransferStock(ArrayList<LoadRequestDO> vecTransferInoutDOs)
	{
		StringBuilder strXML = new StringBuilder();
		for (LoadRequestDO objLoadRequestDO : vecTransferInoutDOs) 
		{
			for(LoadRequestDetailDO objloaDetailDO : objLoadRequestDO.vecItems)
			{
				strXML.append("<VMSalesmanInventoryDco>")
				.append("<VMSalesmanInventoryId></VMSalesmanInventoryId>")	
				.append("<Date>").append(CalendarUtils.getDeliverydate()).append("</Date>")
				.append("<EmpNo>").append(objLoadRequestDO.UserCode).append("</EmpNo>")
				.append("<ItemCode>").append(objloaDetailDO.ItemCode).append("</ItemCode>")
				.append("<PrimaryQuantity>").append(objloaDetailDO.inProcessQuantityLevel1).append("</PrimaryQuantity>")
				.append("<SecondaryQuantity>0</SecondaryQuantity>")
				.append("<PrimaryReturnQuantity>0</PrimaryReturnQuantity>")
				.append("<SecondaryReturnQuantity>0</SecondaryReturnQuantity>")
				.append("<FeederEmpNo>").append(objLoadRequestDO.WHKeeperCode).append("</FeederEmpNo>")
				.append("<AvailQty>0</AvailQty>")
				.append("<IsAppVerified>0</IsAppVerified>")
				.append("<MovementType>").append(objLoadRequestDO.MovementType).append("</MovementType>")
				.append("<MovementStatus>").append(objLoadRequestDO.MovementStatus).append("</MovementStatus>")
				.append("<MovementCode>").append(objLoadRequestDO.MovementCode).append("</MovementCode>")
				.append("</VMSalesmanInventoryDco>");
			}
		}
		
		return strXML.toString();
	}
	public static String UpdateTransferInStockUpload(TransferInoutDO transferInoutDO)
	{
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER) 
			.append("<UpdateTransferInOrOutStock xmlns=\"http://tempuri.org/\">")
			.append("<objTransferInOrOutStock>")
			.append("<Inventories>").append(getVMSalesmanInventoryDcoINUpload(transferInoutDO)).append("</Inventories>")
			.append("</objTransferInOrOutStock>")
			.append("</UpdateTransferInOrOutStock>")
		.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	///////////////////////////////////////////////////////////////////////////////
	private static String getVMSalesmanInventoryDcoOutUpload(TransferInoutDO transferInoutDO)
	{
		StringBuilder strXML = new StringBuilder();
		for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : transferInoutDO.vecOrderDetailDcos) 
		{
			strXML.append("<VMSalesmanInventoryDco>")
					.append("<VMSalesmanInventoryId></VMSalesmanInventoryId>")	
					.append("<Date>").append(CalendarUtils.getDeliverydate()).append("</Date>")
					.append("<EmpNo>").append(transferInoutDO.fromEmpNo).append("</EmpNo>")
					.append("<ItemCode>").append(deliveryAgentOrderDetailDco.itemCode).append("</ItemCode>")
					.append("<PrimaryQuantity>0</PrimaryQuantity>")
					.append("<SecondaryQuantity>0</SecondaryQuantity>")
					.append("<PrimaryReturnQuantity>").append(deliveryAgentOrderDetailDco.preCases).append("</PrimaryReturnQuantity>")
					.append("<SecondaryReturnQuantity>").append(deliveryAgentOrderDetailDco.preUnits).append("</SecondaryReturnQuantity>")
					.append("<FeederEmpNo>").append(transferInoutDO.toEmpNo).append("</FeederEmpNo>")
					
					.append("<AvailQty>").append(transferInoutDO.AvailQty).append("</AvailQty>")
					.append("<IsAppVerified>").append(transferInoutDO.IsAppVerified).append("</IsAppVerified>")
					.append("<MovementType>").append(transferInoutDO.MovementType).append("</MovementType>")
					.append("<MovementStatus>").append(transferInoutDO.MovementStatus).append("</MovementStatus>")
					.append("<MovementCode>").append(transferInoutDO.MovementCode).append("</MovementCode>")
					
					.append("</VMSalesmanInventoryDco>");
		}
		return strXML.toString();
	}
	
//	private static String getVMSalesmanInventoryDcoOut(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String strEmpNo, String strOutEmpNo)
//	{
//		String strXML = "";
//		for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : vecOrdProduct) 
//		{
//			strXML += "<VMSalesmanInventoryDco>")
//					 "<VMSalesmanInventoryId></VMSalesmanInventoryId>")	
//					 "<Date>")CalendarUtils.getDeliverydate()).append("</Date>")
//					 "<EmpNo>")strOutEmpNo).append("</EmpNo>")
//					 "<ItemCode>")deliveryAgentOrderDetailDco.itemCode).append("</ItemCode>")
//					 "<PrimaryQuantity>0</PrimaryQuantity>")
//					 "<SecondaryQuantity>0</SecondaryQuantity>")
//					 "<PrimaryReturnQuantity>")deliveryAgentOrderDetailDco.preCases).append("</PrimaryReturnQuantity>")
//					 "<SecondaryReturnQuantity>")deliveryAgentOrderDetailDco.preUnits).append("</SecondaryReturnQuantity>")
//					 "<FeederEmpNo>")strEmpNo).append("</FeederEmpNo>")
//					 "</VMSalesmanInventoryDco>";
//		}
//		return strXML.toString();
//	}
	public static String UpdateTransferOutStockUpload(TransferInoutDO transferInoutDO)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
	.append("<UpdateTransferInOrOutStock xmlns=\"http://tempuri.org/\">")
	.append("<objTransferInOrOutStock>")
	.append("<Inventories>").append(getVMSalesmanInventoryDcoOutUpload(transferInoutDO)).append("</Inventories>")
	.append("</objTransferInOrOutStock>")
	.append("</UpdateTransferInOrOutStock>")
		.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
//	public static String UpdateTransferOutStock(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String strEmpNo, String strOutEmpNo)
//	{
//		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
//	.append("<UpdateTransferInOrOutStock xmlns=\"http://tempuri.org/\">")
//	.append("<objTransferInOrOutStock>")
//	.append("<Inventories>")getVMSalesmanInventoryDcoOut(vecOrdProduct, strEmpNo, strOutEmpNo)).append("</Inventories>")
//	.append("</objTransferInOrOutStock>")
//	.append("</UpdateTransferInOrOutStock>")
//		.append(SOAP_FOOTER) ;
//		return strXML.toString();
//	}
	
	
	/**
	 * Method to build request to get all Inventory
	 * @param strSyncTime
	 * @param string 
	 * @return String
	 */
	public static String getAllPromotions(String strEmpNo , String lsd, String lst)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
					.append("<GetAllPromotions xmlns=\"http://tempuri.org/\">")
	     			.append("<UserCode>").append(strEmpNo).append("</UserCode>")
	     			.append("<lsd>").append(lsd).append("</lsd>")
	     			.append("<lst>").append(lst).append("</lst>")
	     			.append("</GetAllPromotions>")
					.append(SOAP_FOOTER) ;
		
		return strXML.toString();
	}
	
	public static String getAllPromotionsWithLastSynch(String strEmpNo , String lsd, String lst)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
					.append("<GetAllPromotions xmlns=\"http://tempuri.org/\">")
	     			.append("<UserCode>").append(strEmpNo).append("</UserCode>")
	     			.append("<lsd>").append(lsd).append("</lsd>")
	     			.append("<lst>").append(lsd).append("</lst>")
	     					.append("</GetAllPromotions>")
						.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	
	// TODO Auto-generated catch block
//	public static String getAllNotesByUserAndCustomerId(String salesmanCode,String customerId, String strSyncTime)
//	{
//		LogUtils.errorLog("getAllNotesByUserAndCustomerId - ", ""+strSyncTime);
//		String strXML = SOAP_HEADER +     
//						"<GetNotes xmlns=\"http://tempuri.org/\">"+
//	      				"<CustomerId>"+customerId+"</CustomerId>"+
//	      				"<PresellerId>"+salesmanCode+"</PresellerId>"+
//	      				"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
//	      				"</GetNotes>"+
//	      				SOAP_FOOTER ;
//		return strXML;
//	}
//	public static String deleteNotes(String noteId)
//	{
//		String strXML = SOAP_HEADER +     
//						"<DeleteNotes  xmlns=\"http://tempuri.org/\">"+
//						"<NoteIds>"+noteId+"</NoteIds>"+
//						"</DeleteNotes >"+
//						SOAP_FOOTER ;
//		return strXML;
//	}
//	
//	public static String updateCustomerGeoLocation(JourneyPlanDO mallsDetails, String empNo)
//	{
//		String strXML = SOAP_HEADER +     
//						"<UpdateClientLocation xmlns=\"http://tempuri.org/\">"+
//						"<Site>"+mallsDetails.site+"</Site>"+
//						"<Latitude>"+mallsDetails.geoCodeX+"</Latitude>"+
//						"<Longitude>"+mallsDetails.geoCodeY+"</Longitude>"+
//						"</UpdateClientLocation>"+
//						SOAP_FOOTER ;
//		return strXML;
//	}
//
//	public static String getConfirmOrderDetailsforDA(String empNo,String currentDate)
//	{
//		String strXML = SOAP_HEADER +     
//						"<getConfirmOrderDetailsforDA xmlns=\"http://tempuri.org/\">"+
//						"<EmpNo>"+empNo+"</EmpNo>"+
//						"<Date>"+currentDate+"</Date>"+
//						"</getConfirmOrderDetailsforDA>"+
//						SOAP_FOOTER ;
//		LogUtils.errorLog("strXML",""+strXML);
//		return strXML;
//	}
//	
//	public static String getCustomerOrderHistoryBYDA (String empNo,String currentDate)
//	{
//		String strXML = SOAP_HEADER +     
//						"<GetCustomerOrderHistoryBYDA xmlns=\"http://tempuri.org/\">"+
//						"<DeliveryAgentId>"+empNo+"</DeliveryAgentId>"+
//						"<Date>"+currentDate+"</Date>"+
//						"</GetCustomerOrderHistoryBYDA>"+
//						SOAP_FOOTER ;
//		LogUtils.errorLog("strXML",""+strXML);
//		return strXML;
//	}
//	
//	
//	public static String getDeliveryStatus(String empNo,String currentDate)
//	{
//		String strXML = SOAP_HEADER +     
//						"<CheckDeliveryStatus xmlns=\"http://tempuri.org/\">"+
//						"<EmpNo>"+empNo+"</EmpNo>"+
//						"<Date>"+currentDate+"</Date>"+
//						"</CheckDeliveryStatus>"+
//						SOAP_FOOTER ;
//		LogUtils.errorLog("strXML",""+strXML);
//		return strXML;
//	}
//	public static String getAllReasons(String strSyncTime)
//	{
//		String strXML = SOAP_HEADER +     
//						"<GetAllReasons xmlns=\"http://tempuri.org/\">"+
//						"<LastSync>"+strSyncTime+"</LastSync>"+
//						"</GetAllReasons>"+
//						SOAP_FOOTER ;
//		return strXML;
//	}
//	/**
//	 * Method to update the Delivery status of orders by delivery agent 
//	 * @param noteId
//	 * @return String
//	 */
//	
//	/**
//	 * 
//	 * @param strSyncTime
//	 * @return String
//	 */
//	public static String getSurveyQuestion(String strSyncTime)
//	{
//		String strXML = SOAP_HEADER +     
//						"<GetQuestionAnswer xmlns=\"http://tempuri.org/\">"+
//						"<LastSync>"+strSyncTime+"</LastSync>"+
//						"</GetQuestionAnswer>"+
//						SOAP_FOOTER ;
//		return strXML;
//	}
//	
//	public  static String generatePasscode(String preSellerId,String supervisorId)
//	{
//
//		String strXML = SOAP_HEADER +     
//	    				"<GetPassCode xmlns=\"http://tempuri.org/\">"+
//	    				"<SupervisorId>"+ supervisorId+"</SupervisorId>"+
//	    				"<PreSellerId>"+ preSellerId+"</PreSellerId>"+
//	    				"</GetPassCode>"+
//						SOAP_FOOTER ;
//		LogUtils.errorLog("strXML",""+strXML);
//		return strXML;
//	}
//	
//	
//	//Check PassCode Status
//	public static String insertEOT(String strEmpId, String strEOTType, String strReason, String strDateTime,String strOrders,String strInvoices, String strPayments, String signature)
//	{
//		String strXML = SOAP_HEADER+
//		 				"<InsertEOT xmlns=\"http://tempuri.org/\">"+
//	      				"<objEOTD>"+
//	      				"<EmpNo>"+strEmpId+"</EmpNo>"+
//	      				"<EOTType>"+strEOTType+"</EOTType>"+
//	      				"<Reason>"+strReason+"</Reason>"+
//	      				"<EOTTime>"+strDateTime+"</EOTTime>"+
//	      				"<OrderNumber>"+strOrders+"</OrderNumber>"+
//	      	            "<ReceiptNumber>"+strPayments+"</ReceiptNumber>"+
//	      	            "<InvoiceNumber>"+strInvoices+"</InvoiceNumber>"+
////	      	            "<Signature></Signature>"+
//	      	            "<Signature>"+URLEncoder.encode(""+signature)+"</Signature>"+
//	      				"</objEOTD>"+
//	      				"</InsertEOT>"+
//						SOAP_FOOTER;
//		LogUtils.errorLog("strXML - ", "strXML - "+strXML);
//		return strXML;
//	}
//	//login request
//	public static String getAllCategory(String strSyncTime)
//	{
//		String strXML = SOAP_HEADER+
//						"<GetAllCategory xmlns=\"http://tempuri.org/\">"+
//						"<LastSync>"+strSyncTime+"</LastSync>"+
//						"</GetAllCategory>"+
//						SOAP_FOOTER;
//		return strXML;
//	}
//	
//	public static String getItemPricing(String strSyncTime)
//	{
//		String strXML = SOAP_HEADER+
//	    				"<getPrice xmlns=\"http://tempuri.org/\">"+
//	    				"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
//	    				"</getPrice>"+
//						SOAP_FOOTER;
//		return strXML;
//	}
//	
//	public static String getFiveTopSellingItems(String empNo)
//	{
//		String strXML = SOAP_HEADER+
//	    				"<GetTopFiveSellingItem xmlns=\"http://tempuri.org/\" >"+
//	    				"<EmpNo>"+empNo+"</EmpNo>"+
//	    				"</GetTopFiveSellingItem>"+
//						SOAP_FOOTER;
//		return strXML;
//	}
//	
//	public static String getSplashScreenDataforSync(String UserCode , String lsd, String lst)
//	{
//		LogUtils.errorLog("GetHHSplashScreenDataforSync","GetHHSplashScreenDataforSync "+ lsd);
//		String strXML = SOAP_HEADER+
//				
//					"<GetHHSplashScreenDataforSync xmlns=\"http://tempuri.org/\">"+
//					"<UserCode>"+UserCode+"</UserCode>"+
//						"<lsd>"+lsd+"</lsd>"+
//						"<lst>"+lst+"</lst>"+
//						"</GetHHSplashScreenDataforSync>"+
//						SOAP_FOOTER;
//		return strXML;
//	}
//	
//	public static String getHouseholdMastersWithSync(String strSyncTime)
//	{
//		LogUtils.errorLog("GetHouseHoldMastersWithSync","GetHouseHoldMastersWithSync "+ strSyncTime);
//		String strXML = SOAP_HEADER +
//						"<GetHouseHoldMastersWithSync xmlns=\"http://tempuri.org/\">"+
//						"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
//						"</GetHouseHoldMastersWithSync>"+ 
//						SOAP_FOOTER;
//      
//		return strXML;
//	}
//
//	/**
//	 * @author kishore
//	 * Insert Recording
//	 * @param site
//	 * @param supervisorId
//	 * @param recName
//	 * @param filePath
//	 * @return
//	 */
//	public static String insertRecording(String site, String supervisorId, String recName, String filePath)
//	{
//		String strXML = SOAP_HEADER +
//							"<InsertRecording xmlns=\"http://tempuri.org/\">"+
//							"<objRecording>"+
//							"<Site>"+site+"</Site>"+
//							"<SupervisorId>"+supervisorId+"</SupervisorId>"+
//							"<RecordingName>"+recName+"</RecordingName>"+
//							"<FilePath>"+filePath+"</FilePath>"+
//							"</objRecording>"+
//							"</InsertRecording>"+
//						SOAP_FOOTER;
//		LogUtils.errorLog("requestXML", strXML);
//		return strXML;
//	}
//	
//	/**
//	 * Delete Recording
//	 * @param recordingId
//	 * @return
//	 */
//	public static String deleteRecording(String recordingId)
//	{
//		String strXML = SOAP_HEADER +
//						"<DeleteRecording xmlns=\"http://tempuri.org/\">"+
//						"<RecordingId>"+recordingId+"</RecordingId>"+
//						"</DeleteRecording>"+
//						SOAP_FOOTER;
//		
//		return strXML;
//	}
//	
//	/**
//	 * Update Recording
//	 * @param recordingId
//	 * @param newName
//	 * @return
//	 */
//	public static String updateRecording(String recordingId, String newName)
//	{
//		String strXML = SOAP_HEADER +
//						"<UpdateRecordingName xmlns=\"http://tempuri.org/\">"+
//						"<RecordingId>"+recordingId+"</RecordingId>"+
//						"<NewName>"+newName+"</NewName>"+
//						"</UpdateRecordingName>"+
//						SOAP_FOOTER;
//
//		return strXML;
//	}
//	/**
//	 * @param vecOptions
//	 * @return String
//	 */
//	public static String postSurveyAnswer(String strCustomerSitId, String strPresellerId,String strSupervisorId, Vector<QuestionOptionDO> vecOptions)
//	{
//		String strBody ="";
//		String strXML = SOAP_HEADER +     
//		  				"<ImportCustomersSurvey xmlns=\"http://tempuri.org/\">"+
//						"<CustomerSurveyRequest>"+
//						"<CustomersSurvey>"+
//						"<CustomerSurvey>"+
//						"<Site>"+strCustomerSitId+"</Site>"+
//						"<PresellerId>"+strPresellerId+"</PresellerId>"+
//						"<SupervisorId>"+strSupervisorId+"</SupervisorId>"+
//						"<Options>";
//		
//		 			for(QuestionOptionDO objQuestionOption : vecOptions)
//		 			{
//		 				strBody += "<Option>"+
//		 		          		   "<QuestionId>"+objQuestionOption.questionId+"</QuestionId>"+
//		 		          		   "<Question>"+objQuestionOption.questionDesc+"</Question>"+
//		 		          		   "<OptionId>"+objQuestionOption.optionId+"</OptionId>"+
//		 						   "<Option>"+objQuestionOption.optionDesc+"</Option>"+
//		 		        		   "</Option>";
//		 			}
//		
//		strXML = strXML+strBody+ "</Options>"+
//	    						 "</CustomerSurvey>"+
//								 "</CustomersSurvey>"+
//	      						 "</CustomerSurveyRequest>"+
//	    						 "</ImportCustomersSurvey>"+
//								 SOAP_FOOTER ;
////		writeToSdcards(strXML);
//		return strXML;
//	}
//	
////	public static void writeToSdcards(String strXML)
////	{
////		 try
////		 {
////			 File myFile = new File("/sdcard/request.xml");
////			 myFile.createNewFile();
////			 FileOutputStream fOut = new FileOutputStream(myFile);
////			 OutputStreamWriter myOutWriter = 	new OutputStreamWriter(fOut);
////			 myOutWriter.append(strXML);
////			 myOutWriter.close();
////			 fOut.close();
////		 } 
////		 catch (Exception e)
////		 {
////		 }
////	}
//	/**
//	 * Fetch all deleted items request
//	 * @param last sync time
//	 * @return String request.
//	 */
//	public static String getAllDeletedItems(String lastSync)
//	{
//		LogUtils.errorLog("GetAllHHDeletedItems","GetAllHHDeletedItems "+ lastSync);
//		
//		String strXML = SOAP_HEADER +
//						"<GetAllHHDeletedItems  xmlns=\"http://tempuri.org/\">"+
//						"<LastSync>"+lastSync+"</LastSync>"+
//						"</GetAllHHDeletedItems >"+
//						SOAP_FOOTER;
//		
//		return strXML;
//	}
//	
//	public static String getAllPassCode(String preselerId)
//	{
//		LogUtils.errorLog("GetAllPassCode - ", "GetAllPassCode - "+preselerId);
//		String strXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
//				"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
//					"<soap:Body>" +
//						"<GetAllPassCode xmlns=\"http://tempuri.org/\">" +
//							"<PreSellerId>"+preselerId+"</PreSellerId>" +
//						"</GetAllPassCode>" +
//					"</soap:Body>" +
//				"</soap:Envelope>";
//		return strXML;
//	}
//	
//	public static String getAllVehicles(String empNo)
//	{
//		LogUtils.errorLog("getAllVehicles - ", "getAllVehicles - "+empNo);
//		String strXML = SOAP_HEADER +
//						"<getAllVehicles xmlns=\"http://tempuri.org/\">" +
//							"<empNo>"+empNo+"</empNo>" +
//						"</getAllVehicles>" +
//						SOAP_FOOTER;
//		return strXML;
//	}
//	
//	public static String getAllAvailablePassCode(String empNo)
//	{
//		String strXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
//				"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
//					"<soap:Body>" +
//						"<GetAllAvailablePassCode xmlns=\"http://tempuri.org/\">" +
//							"<EmpId>"+empNo+"</EmpId>" +
//						"</GetAllAvailablePassCode>" +
//					"</soap:Body>" +
//				"</soap:Envelope>";
//		return strXML;
//	}
//	public static String getAllAvailableDAPassCode(String empNo)
//	{
//		String strXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
//				"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
//					"<soap:Body>" +
//						"<GetAllAvailableDAPassCodeRegionWise xmlns=\"http://tempuri.org/\">" +
//							"<EmpId>"+empNo+"</EmpId>" +
//						"</GetAllAvailableDAPassCodeRegionWise>" +
//					"</soap:Body>" +
//				"</soap:Envelope>";
//		return strXML;
//	}
//	/**
//	 * method to post the Scaned data
//	 * @param objScanResultObject 
//	 * @return String
//	 */
//	public static String importBarCodeInfo(ScanResultObject objScanResultObject)
//	{
//		String strXML = SOAP_HEADER +
//						"<ImportBarCodeInfo xmlns=\"http://tempuri.org/\">"+
//						"<BarcodeInfoRequest>"+
//						"<Barcodes>"+
//						"<Barcode>"+
//						"<CustomerCode>"+objScanResultObject.customerSiteId+"</CustomerCode>"+
//						"<Type>"+objScanResultObject.type+"</Type>"+
//						"<Location>"+objScanResultObject.location+"</Location>"+
//						"<BarCode>"+objScanResultObject.strBarcodeImage+"</BarCode>"+
//						"<Comments>"+objScanResultObject.comments+"</Comments>"+
//						"<ScanTime>"+objScanResultObject.time+"</ScanTime>"+
//						"<ItemCode>"+objScanResultObject.productId+"</ItemCode>"+
//						"<SalesmanCode>"+objScanResultObject.EmpId+"</SalesmanCode>"+
//						"<Quantity>"+objScanResultObject.Quantity+"</Quantity>"+
//						"</Barcode>"+
//						"</Barcodes>"+
//						"</BarcodeInfoRequest>"+
//						"</ImportBarCodeInfo>"+
//						SOAP_FOOTER;
//
//		return strXML;
//	}
//	
//	public static String securityCheckedCompleted(String truckNo)
//	{
//		String strBody = "";
//		String strXML = SOAP_HEADER +
//						"<SecurityCheckedCompleted xmlns=\"http://tempuri.org/\">"+
//						"<TruckNumber>"+truckNo+"</TruckNumber>";
//		strXML = strXML+strBody+
//					"</SecurityCheckedCompleted>"+
//			  SOAP_FOOTER;
//		LogUtils.errorLog("strXML", "strXML"+strXML);
//		return strXML;
//	}
//	
//	public static String logisticsCheckedCompleted(String truckNo)
//	{
//		String strBody = "";
//		String strXML = SOAP_HEADER +
//						"<LogisticsCheckCompleted xmlns=\"http://tempuri.org/\">"+
//						"<OrderNumber>"+truckNo+"</OrderNumber>";
//		strXML = strXML+strBody+
//					"</LogisticsCheckCompleted>"+
//			  SOAP_FOOTER;
//		LogUtils.errorLog("strXML", "strXML"+strXML);
//		return strXML;
//	}
//	
//	public static String getOrderPerDay(String salesManCode, String date)
//	{
//		String strXML = SOAP_HEADER +
//						"<GetOrderPerDay xmlns=\"http://tempuri.org/\">"+
//						"<SalesmanCode>"+salesManCode+"</SalesmanCode>"+
//						"<FetchDate>"+date+"</FetchDate>"+
//						"</GetOrderPerDay>"+
//						SOAP_FOOTER;
//		LogUtils.errorLog("strXML", "strXML"+strXML);
//		return strXML;
//	}
//	
//	public static String getReceiptPerDay(String salesManCode, String date)
//	{
//		String strXML = SOAP_HEADER +
//						"<GetReceiptPerDay xmlns=\"http://tempuri.org/\">"+
//						"<SalesmanCode>"+salesManCode+"</SalesmanCode>"+
//						"<FetchDate>"+date+"</FetchDate>"+
//						"</GetReceiptPerDay>"+
//						SOAP_FOOTER;
//		LogUtils.errorLog("strXML", "strXML"+strXML);
//		return strXML;
//	}
//	
//	public static String getJournyLogPerDay(String salesManCode)
//	{
//		String strXML = SOAP_HEADER +
//						"<GetRoutePlanPerDay  xmlns=\"http://tempuri.org/\">"+
//						"<SalesManCode>"+salesManCode+"</SalesManCode>"+
//						"</GetRoutePlanPerDay >"+
//						SOAP_FOOTER;
//
//		LogUtils.errorLog("strXML", "strXML"+strXML);
//		return strXML;
//	}
//	
//	public static String insertJournyLogPerDay(String salesManCode, String request)
//	{
//		String strXML = SOAP_HEADER +
//						"<InsertRoutePlanPerDay   xmlns=\"http://tempuri.org/\">"+
//						"<RequestString>"+request+"</RequestString>"+
//						"<SalesManCode>"+salesManCode+"</SalesManCode>"+
//						"</InsertRoutePlanPerDay  >"+
//						SOAP_FOOTER;
//
//		LogUtils.errorLog("strXML", "strXML"+strXML);
//		return strXML;
//	}
//	
//	 
//	public static String prepareJournyPlanXml(ArrayList<MallsDetails> vecJourneyLog)
//	{
//		StringBuilder builder = new StringBuilder();
//		builder.append("&lt;JournyLogs&gt;");
//		for(MallsDetails planDO :vecJourneyLog)
//		{
//			builder.append("&lt;JournyLog&gt;").
//			append("&lt;DateOfJourney&gt;").append(planDO.dateofJorney).append("&lt;/DateOfJourney&gt;").
//			append("&lt;PresellerId&gt;").append(planDO.presellerId).append("&lt;/PresellerId&gt;").
//			append("&lt;CustomerSiteId&gt;").append(planDO.customerSiteId).append("&lt;/CustomerSiteId&gt;").
//			append("&lt;CustomerPasscode&gt;").append(planDO.customerPasscode).append("&lt;/CustomerPasscode&gt;").
//			append("&lt;Stop&gt;").append(planDO.Stop).append("&lt;/Stop&gt;").
//			append("&lt;Distance&gt;").append(planDO.Distance).append("&lt;/Distance&gt;").
//			append("&lt;TravelTime&gt;").append(planDO.TravelTime).append("&lt;/TravelTime&gt;").
//			append("&lt;ArrivalTime&gt;").append(planDO.ActualArrivalTime).append("&lt;/ArrivalTime&gt;").
//			append("&lt;SeviceTime&gt;").append(planDO.SeviceTime).append("&lt;/SeviceTime&gt;").
//			append("&lt;ReasonForSkip&gt;").append(planDO.reasonForSkip).append("&lt;/ReasonForSkip&gt;").
//			append("&lt;OutTime&gt;").append(planDO.ActualOutTime).append("&lt;/OutTime&gt;").
//			append("&lt;TotalTimeAtOutLet&gt;").append(planDO.TotalTime).append("&lt;/TotalTimeAtOutLet&gt;").
//			append("&lt;/JournyLog&gt;");
//		}
//		builder.append("&lt;/JournyLogs&gt;");
//		
//		return builder.toString();
//	}
//	
//	//method to get all the EOT for supervisor
//	public static String insertCustomerVisit(ArrayList<MallsDetails> vecJourneyLog,String empNo)
//	{
//		String strBody = "";
//		String strXML =  SOAP_HEADER+
//		 				"<InsertCustomerVisit xmlns=\"http://tempuri.org/\">" +
//		 				"<ImportCustomerVisit>" +
//		 				"<CustomerVisits>";
//						
//		for(MallsDetails planDO :vecJourneyLog)
//		{
//			strBody =strBody+"<CustomerVisit>" +
//							 "<EmpNo>"+empNo+"</EmpNo>"+
//   							 "<CustomerSiteId>"+planDO.customerSiteId+"</CustomerSiteId>"+
//   							 "<Date>"+CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00"+"</Date>"+
//   							 "<ArrivalTime>"+planDO.ActualArrivalTime+"</ArrivalTime>"+
//   							 "<OutTime>"+(planDO.ActualOutTime!=null?planDO.ActualOutTime:planDO.ActualArrivalTime)+"</OutTime>"+
//   							 "<TotalTimeInMins>"+(planDO.TotalTime!=null ? planDO.TotalTime : "1")+"</TotalTimeInMins>" +
//   							 "</CustomerVisit>";
//		}
//
//		strXML = strXML	+
//				 strBody+
//				 "</CustomerVisits>" +
//				 "</ImportCustomerVisit>"+
//				"</InsertCustomerVisit>"+
//				SOAP_FOOTER;
//		
//		LogUtils.errorLog("getJourneyDetails", "getJourneyDetails "+strXML);
//		return strXML;
//	}
//	
//	//method to get all the EOT for supervisor
//	public static String getEOTDetails(String empNo, String currentDate)
//	{
//		String strXML =  SOAP_HEADER+
//		 				"<getEOTDetails xmlns=\"http://tempuri.org/\">"+
//						"<EmpNo>"+empNo+"</EmpNo>"+
//						"<DATE>"+currentDate+"</DATE>"+
//						"</getEOTDetails>"+
//						SOAP_FOOTER;
//		
//		LogUtils.errorLog("getEOTDetails", "getEOTDetails "+strXML);
//		return strXML;
//	}
//	
//	public static String prepareLogisticMismatchXml(ArrayList<DeliveryAgentOrderDetailDco> vector, String reason, String orderNumber)
//	{
//		String strDiscrepency = ""; 
//		for(DeliveryAgentOrderDetailDco obj : vector)
//		{
//			strDiscrepency += "<Discrepancy>" +
//					"<OrderNumber>"+orderNumber+"</OrderNumber>" +
//					"<OrderItem>"+obj.itemCode+"</OrderItem>" +
//					"<ExpectedQuantity>"+obj.preCases+"</ExpectedQuantity>" +
//					"<ActualQuantity>"+obj.cases+"</ActualQuantity>" +
//					"</Discrepancy>";
//		}
//		String strXML = SOAP_HEADER +
//				" <LogisticSupervisorMismatch xmlns=\"http://tempuri.org/\">" +
//				"<LogisticSupervisorDiscrepancy>" +
//				"<Reason>"+reason+"</Reason>" +
//				"<Discrepancies>" +
//				strDiscrepency +
//				"</Discrepancies>" +
//				"</LogisticSupervisorDiscrepancy>" +
//				"</LogisticSupervisorMismatch>" +
//				SOAP_FOOTER;
//		
//		LogUtils.errorLog("request XML", "strXML"+strXML);
//		
//		return strXML;
//	}
//	
//	//login request
//	public static String updatePasscodeStatus(String strPresellerId, String passcode)
//	{
//		String strXML = SOAP_HEADER+
//				 		"<UpdateDAPassCodeStatus xmlns=\"http://tempuri.org/\">"+
//	     				"<EmpNo>"+strPresellerId+"</EmpNo>"+
//				 		"<PassCode>"+passcode+"</PassCode>"+
//				 		"</UpdateDAPassCodeStatus>"+
//						SOAP_FOOTER;
//		
//		Log.e("strXML - ", "strXML - "+strXML);
//		return strXML;
//	}
//	
//	public static String insertCustomerGeoCode(JourneyPlanDO journeyPlanDO , String EmpNo)
//	{
//		String strXML = SOAP_HEADER+
//		 			"<InsertCustomerGeoCode xmlns=\"http://tempuri.org/\">"+
//		 				"<CustomerGeoCodeRequest>"+
//		 				"<Customers>"+
//		 					"<Customer>"+
//		 						"<SiteNumber>"+journeyPlanDO.site+"</SiteNumber>"+
//		 						"<EmpNo>"+EmpNo+"</EmpNo>"+
//		 						"<GEO_CODE_X>"+journeyPlanDO.geoCodeX+"</GEO_CODE_X>"+
//		 						"<GEO_CODE_Y>"+journeyPlanDO.geoCodeY+"</GEO_CODE_Y>"+
//		 					"</Customer>"+
//		 				"</Customers>"+
//		 			"</CustomerGeoCodeRequest>"+
//		 		  "</InsertCustomerGeoCode>"+
//						SOAP_FOOTER;
//		LogUtils.errorLog("strXML", ""+strXML);
//		return strXML;
//	}
//	public static String getPasscodeForDa(String EmpNo)
//	{
//		String strXML = SOAP_HEADER+
//							"<GetAllAvailableDAPassCode xmlns=\"http://tempuri.org/\">"+
// 							"<EmpId>"+EmpNo+"</EmpId>"+
// 							"</GetAllAvailableDAPassCode>"+
//	     				SOAP_FOOTER;
//		LogUtils.errorLog("strXML", ""+strXML);
//		return strXML;
//	}
//	
//	public static String insertHHCustomer(Vector<NewCustomerDO> vecNewCustomerDO, String route)
//	{
//		
//		String strHHCustomer = "";
//		
//		for(int i = 0; i < vecNewCustomerDO.size(); i++)
//		{
//			NewCustomerDO obj = vecNewCustomerDO.get(i);
//			
//			strHHCustomer = strHHCustomer+"<HHCustomerDco>"+
//								"<SiteId>"+obj.CustomerSiteId+"</SiteId>"+
//								"<SALESMAN>"+obj.salesman+"</SALESMAN>"+
//								"<SITE_NAME>"+obj.customerName+"</SITE_NAME>"+
//								"<PARTY_NAME>"+obj.customerName+"</PARTY_NAME>"+
//								"<REGION_CODE>"+obj.region+"</REGION_CODE>"+
//								"<REGION_DESCRIPTION>"+obj.region+"</REGION_DESCRIPTION>"+
//								"<COUNTRY_CODE>"+obj.countryName+"</COUNTRY_CODE>"+
//								"<COUNTRY_DESCRIPTION>"+obj.countryDesc+"</COUNTRY_DESCRIPTION>"+
//								"<ADDRESS1>"+obj.address1+"</ADDRESS1>"+
//								"<ADDRESS2>"+obj.address2+"</ADDRESS2>"+
//								"<ADDRESS3></ADDRESS3>"+
//								"<ADDRESS4></ADDRESS4>"+
//								"<PO_BOX_NUMBER></PO_BOX_NUMBER>"+
//								"<CITY>"+obj.CITY+"</CITY>"+
//								"<PAYMENT_TYPE></PAYMENT_TYPE>"+
//								"<PAYMENT_TERM_CODE></PAYMENT_TERM_CODE>"+
//								"<PAYMENT_TERM_DESCRIPTION></PAYMENT_TERM_DESCRIPTION>"+
//								"<CREDIT_LIMIT>0</CREDIT_LIMIT>"+
//								"<GEO_CODE_X>"+obj.latitude+"</GEO_CODE_X>"+
//								"<GEO_CODE_Y>"+obj.longitude+"</GEO_CODE_Y>"+
//								"<EMAIL>"+obj.email+"</EMAIL>"+
//								"<CONTACTPERSONNAME>"+obj.contactPerson+"</CONTACTPERSONNAME>"+
//								"<CONTACTPERSONMOBILENO>"+obj.mobileNo+"</CONTACTPERSONMOBILENO>"+
//								"<AppKey>"+obj.AppUUID+"</AppKey>"+
//								"<MOBILENO1>"+obj.landline+"</MOBILENO1>"+
//								"<CountryId>"+obj.countryId+"</CountryId>"+
//								"<DOB>"+obj.DOB+"</DOB>"+
//								"<AnniversaryDate>"+obj.anniversary+"</AnniversaryDate>"+
//								"<Source>"+route+"</Source>"+
//								"<customerType>"+1+"</customerType>"+
//								"<PASSCODE>"+12345+"</PASSCODE>"+ 
//								"<Buyer>"+obj.buyerStatus+"</Buyer>"+ 
//								"<CompetitionBrand>"+obj.competitionBrand+"</CompetitionBrand>"+ 
//								"<SKU>"+obj.sku+"</SKU>"+ 
//							"</HHCustomerDco>";
//		}
//		
//		String strXML = SOAP_HEADER +
//						"<InsertHHCustomerOffline xmlns=\"http://tempuri.org/\">"+
//						"<objImportCustomer>"+
//						"<Customers>"+
//						strHHCustomer +
//						"</Customers>"+
//					    "</objImportCustomer>"+
//					    "</InsertHHCustomerOffline>"+
//						SOAP_FOOTER;
//		
//		LogUtils.errorLog("error", strXML);
//		if(strXML.contains("&"))
//			strXML.replace("&", "&amp;");
//		
//		return strXML;
//	}
//	
//	public static String updateHHCustomerGEO(Vector<MallsDetails> vecNewCustomerDO)
//	{
//		String strHHCustomer = "";
//		for(int i = 0; i < vecNewCustomerDO.size(); i++)
//		{
//			MallsDetails obj = vecNewCustomerDO.get(i);
//			
//			strHHCustomer = "<HHCustomerDco>"+
//								"<SiteId>"+obj.customerSiteId+"</SiteId>"+
//								"<GEO_CODE_X>"+obj.Latitude+"</GEO_CODE_X>"+
//								"<GEO_CODE_Y>"+obj.Longitude+"</GEO_CODE_Y>"+
//							"</HHCustomerDco>";
//		}
//		
//		String strXML = SOAP_HEADER +
//				 		"<UpdateHHCustomer xmlns=\"http://tempuri.org/\">"+
//						"<objImportCustomer>"+
//						"<Customers>"+
//						strHHCustomer +
//						"</Customers>"+
//					    "</objImportCustomer>"+
//					    "</UpdateHHCustomer>"+
//						SOAP_FOOTER;
//		
//		LogUtils.errorLog("error", strXML);
//		
//		if(strXML.contains("&"))
//			strXML.replace("&", "&amp;");
//		
//		return strXML;
//	}
//	
//	public static String getSalesManLandmarkSynce(String salesmanCode, String lastSyncDate)
//	{
//		LogUtils.errorLog("GetSalesmanLandmarkWithSync - ", "GetSalesmanLandmarkWithSync "+lastSyncDate);
//		String strXML = SOAP_HEADER +
//						"<GetSalesmanLandmarkWithSync xmlns=\"http://tempuri.org/\">"+
//						"<SalesmanCode>"+salesmanCode+"</SalesmanCode>"+
//						"<LastSyncDate>"+lastSyncDate+"</LastSyncDate>"+
//						"</GetSalesmanLandmarkWithSync>"+
//						SOAP_FOOTER;
//		return strXML;
//	}
//	
//	/**
//	 * Method to build request to get all Vehicles
//	 * @param strSyncTime
//	 * @param string 
//	 * @return String
//	 */
//	public static String getVehicles(String empId , String lsd, String lst)
//	{
//		LogUtils.errorLog("getVehicles - ", "getVehicles - "+lsd+" - "+lst);
//		String strXML = SOAP_HEADER + 
//		 				"<GetVehicles xmlns=\"http://tempuri.org/\">"+
//		 				"<UserCode>"+empId+"</UserCode>"+
//		 				"<lsd>"+lsd+"</lsd>"+
//		 				"<lst>"+lst+"</lst>"+
//		 				"</GetVehicles>"+
//						SOAP_FOOTER ;
//		return strXML;
//	}
//	
//	/**
//	 * Method to build request to get Things to focus
//	 * @param strSyncTime
//	 * @param string 
//	 * @return String
//	 */
//	public static String getThingstofocus(String empId , String lsd, String lst)
//	{
//		LogUtils.errorLog("getThingstofocus - ", "getThingstofocus - "+lsd+" - "+lst);
//		String strXML = SOAP_HEADER + 
//		 				"<GetThingsToFocusesByUserId xmlns=\"http://tempuri.org/\">"+
//		 				"<UserCode>"+empId+"</UserCode>"+
//		 				"<lsd>"+lsd+"</lsd>"+
//		 				"<lst>"+lst+"</lst>"+
//		 				"</GetThingsToFocusesByUserId>"+
//						SOAP_FOOTER ;
//		LogUtils.errorLog("getThingstofocus Reqyest ", strXML);
//		return strXML;
//		
//	}
//	
//	/**
//	 * Method to build request to get all Inventory
//	 * @param strSyncTime
//	 * @param string 
//	 * @return String
//	 */
//	public static String getVMInventory(String strEmpNo , String strSyncTime, String strCurrentDate)
//	{
//		LogUtils.errorLog("getVMInventory - ", "getVMInventory - "+strSyncTime);
//		String strXML = SOAP_HEADER + 
//						"<GetVMSalesmanInventory xmlns=\"http://tempuri.org/\">"+
//	      				"<LastSyncDate></LastSyncDate>"+
//	      				"<Empno>"+strEmpNo+"</Empno>"+
//	      				"<Date>"+strCurrentDate+"</Date>"+
//	      				"</GetVMSalesmanInventory>"+
//						SOAP_FOOTER ;
//		return strXML;
//	}
//	
//	/**
//	 * Method to build request to get all Vehicles
//	 * @param strSyncTime
//	 * @param string 
//	 * @return String
//	 */
//	public static String getSequenceNoBySalesmanForHH(String strSalesmanNo)
//	{
//		LogUtils.errorLog("GetSequenceNoBySalesmanForHH - ", "GetSequenceNoBySalesmanForHH "+strSalesmanNo);
//		String strXML = SOAP_HEADER + 
//		 				"<GetAvailableTrxNos xmlns=\"http://tempuri.org/\">"+
//		 				"<UserCode>"+strSalesmanNo+"</UserCode>"+
//		 				"</GetAvailableTrxNos>"+
//						SOAP_FOOTER ;
//		return strXML;
//	}
//	
//	public static String getHHSummary(String empNo, String strSyncTime, String currentdate)
//	{
//		LogUtils.errorLog("getAllRegions - ", ""+strSyncTime);
//		String strXML =   SOAP_HEADER + 
//						  "<GetVMSummary xmlns=\"http://tempuri.org/\">"+
//					      "<LastSync>"+strSyncTime+"</LastSync>"+
//					      "<EmpNo>"+empNo+"</EmpNo>"+
//					      "<CurrentDate>"+currentdate+"</CurrentDate>"+
//					      "</GetVMSummary>"+
//						  SOAP_FOOTER ;
//		return strXML;
//	}
//	public static String getAllHHCustomerDeletedItems(String empNo, String strSyncTime)
//	{
//		LogUtils.errorLog("GetAllHHCustomerDeletedItems - ", "GetAllHHCustomerDeletedItems - "+strSyncTime);
//		String strXML =   SOAP_HEADER + 
//							"<GetAllHHCustomerDeletedItems xmlns=\"http://tempuri.org/\">"+
//							"<LastSync>"+strSyncTime+"</LastSync>"+
//							"<Empno>"+empNo+"</Empno>"+
//							"</GetAllHHCustomerDeletedItems>"+
//						  SOAP_FOOTER ;
//		
//		LogUtils.errorLog("strXML", "strXML - "+strXML);
//		return strXML;
//	}
//	
//	public static String insertStock(String empNo, ArrayList<DeliveryAgentOrderDetailDco> arrayList)
//	{
//		String body = "";
//		String strXML = SOAP_HEADER + 
//				 		"<InsertStock xmlns=\"http://tempuri.org/\">"+
//	      				"<objImportStock>"+
//	      				"<Inventories>";
//		for(DeliveryAgentOrderDetailDco detailDco : arrayList)
//		{
//			body = body + 
//					"<VMSalesmanInventoryDco>"+
//					"<VMSalesmanInventoryId>"+0+"</VMSalesmanInventoryId>"+
//					"<Date>"+CalendarUtils.getOrderPostDate()+"</Date>"+
//					"<EmpNo>"+empNo+"</EmpNo>"+
//					"<ItemCode>"+detailDco.itemCode+"</ItemCode>"+
//					"<PrimaryQuantity>"+detailDco.preCases+"</PrimaryQuantity>"+
//					"<SecondaryQuantity>"+detailDco.preUnits+"</SecondaryQuantity>"+
//					"</VMSalesmanInventoryDco>";
//		}
//		strXML = 	strXML+body+ 
//					"</Inventories>"+
//					"</objImportStock>"+
//	      			"</InsertStock>"+
//				    SOAP_FOOTER ;
//		
//		LogUtils.errorLog("strXML", "strXML - "+strXML);
//		return strXML;
//	}
//	
//	public static String insertCheckInDemandStock(Vector<CheckInDemandInventoryDO> vecInDemandInventoryDOs)
//	{
//		String body = "";
//		String strXML = SOAP_HEADER + 
//				 		"<ImportCheckInDemandStock xmlns=\"http://tempuri.org/\">"+
//	      				"<objImportStock>"+
//	      				"<Inventories>";
//		for(CheckInDemandInventoryDO checkInDemandInventoryDO : vecInDemandInventoryDOs)
//		{
//			body = body + 
//					"<VMSalesmanInventoryDco>"+
//					"<VMSalesmanInventoryId>"+0+"</VMSalesmanInventoryId>"+
//					"<Date>"+checkInDemandInventoryDO.Date+"</Date>"+
//					"<EmpNo>"+checkInDemandInventoryDO.EmpNo+"</EmpNo>"+
//					"<ItemCode>"+checkInDemandInventoryDO.ItemCode+"</ItemCode>"+
//					
////					"<PrimaryQuantity>"+detailDco.totalCases+"</PrimaryQuantity>"+
////					"<SecondaryQuantity>"+detailDco.totalCases+"</SecondaryQuantity>"+
//					
//					//Changed by awaneesh
//					"<PrimaryQuantity>"+checkInDemandInventoryDO.PrimaryQuantity+"</PrimaryQuantity>"+
//					"<SecondaryQuantity>"+checkInDemandInventoryDO.SecondaryQuantity+"</SecondaryQuantity>"+
//					"</VMSalesmanInventoryDco>";
//		}
//		strXML = 	strXML+body+ 
//					"</Inventories>"+
//					"</objImportStock>"+
//	      			"</ImportCheckInDemandStock>"+
//				    SOAP_FOOTER ;
//		
//		LogUtils.errorLog("strXML", "strXML - "+strXML);
//		return strXML;
//	}
//	
//	public static String insertDemandStock(Vector<CheckInDemandInventoryDO> vecInDemandInventoryDOs)
//	{
//		String body = "";
//		String strXML = SOAP_HEADER + 
//				 		"<InsertStock xmlns=\"http://tempuri.org/\">"+
//	      				"<objImportStock>"+
//	      				"<Inventories>";
//		for(CheckInDemandInventoryDO checkInDemandInventoryDO : vecInDemandInventoryDOs)
//		{
//			body = body + 
//					"<VMSalesmanInventoryDco>"+
//					"<VMSalesmanInventoryId>"+0+"</VMSalesmanInventoryId>"+
//					"<Date>"+checkInDemandInventoryDO.Date+"</Date>"+
//					"<EmpNo>"+checkInDemandInventoryDO.EmpNo+"</EmpNo>"+
//					"<ItemCode>"+checkInDemandInventoryDO.ItemCode+"</ItemCode>"+
//					
////					"<PrimaryQuantity>"+detailDco.totalCases+"</PrimaryQuantity>"+
////					"<SecondaryQuantity>"+detailDco.totalCases+"</SecondaryQuantity>"+
//					
//					//Changed by awaneesh
//					"<PrimaryQuantity>"+checkInDemandInventoryDO.PrimaryQuantity+"</PrimaryQuantity>"+
//					"<SecondaryQuantity>"+checkInDemandInventoryDO.SecondaryQuantity+"</SecondaryQuantity>"+
//					"</VMSalesmanInventoryDco>";
//		}
//		strXML = 	strXML+body+ 
//					"</Inventories>"+
//					"</objImportStock>"+
//	      			"</InsertStock>"+
//				    SOAP_FOOTER ;
//		
//		LogUtils.errorLog("strXML", "strXML - "+strXML);
//		return strXML;
//	}
//	
//	public static String getAdvanceOrderByEmpNo(String empNo, String strSyncTime)
//	{
//		LogUtils.errorLog("GetAdvanceOrderByEmpNo - ", "GetAdvanceOrderByEmpNo - "+strSyncTime);
//		String strXML =   	SOAP_HEADER + 
//							"<GetAdvanceOrderByEmpNo xmlns=\"http://tempuri.org/\">"+
//							"<EmpNo>"+empNo+"</EmpNo>"+
//							"</GetAdvanceOrderByEmpNo>"+
//							SOAP_FOOTER ;
//		
//		LogUtils.errorLog("strXML", "strXML - "+strXML);
//		return strXML;
//	}
//	
//	public static String updateReturnStock(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String strEmpNo)
//	{
//		String strXML =   	SOAP_HEADER + 
//							"<UpdateReturnStock xmlns=\"http://tempuri.org/\">"+
//							"<objImportStock>"+
//							"<Inventories>"+getVMSalesmanInventoryDcos(vecOrdProduct, strEmpNo)+"</Inventories>"+
//							"</objImportStock>"+
//							"</UpdateReturnStock>"+
//							SOAP_FOOTER ;
//		return strXML;
//	}
//	
//	private static String getVMSalesmanInventoryDcos(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String strEmpNo)
//	{
//		String strXML = "";
//		for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : vecOrdProduct) 
//		{
//			strXML += "<VMSalesmanInventoryDco>"+
//					 "<VMSalesmanInventoryId></VMSalesmanInventoryId>"+	
//					 "<Date>"+CalendarUtils.getOrderPostDate()+"</Date>"+
//					 "<EmpNo>"+strEmpNo+"</EmpNo>"+
//					 "<ItemCode>"+deliveryAgentOrderDetailDco.itemCode+"</ItemCode>"+
//					 "<PrimaryQuantity>0</PrimaryQuantity>"+
//					 "<SecondaryQuantity>0</SecondaryQuantity>"+
//					 "<PrimaryReturnQuantity>"+deliveryAgentOrderDetailDco.preCases+"</PrimaryReturnQuantity>"+
//					 "<SecondaryReturnQuantity>"+deliveryAgentOrderDetailDco.preUnits+"</SecondaryReturnQuantity>"+
//					 "<FeederEmpNo>0</FeederEmpNo>"+
//					 "</VMSalesmanInventoryDco>";
//		}
//		return strXML;
//	}
//	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	private static String getVMSalesmanInventoryDcoINUpload(TransferInoutDO transferInoutDO)
//	{
//		String strXML = "";
//		for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : transferInoutDO.vecOrderDetailDcos) 
//		{
//			strXML += "<VMSalesmanInventoryDco>"+
//					 "<VMSalesmanInventoryId></VMSalesmanInventoryId>"+	
//					 "<Date>"+CalendarUtils.getDeliverydate()+"</Date>"+
//					 "<EmpNo>"+transferInoutDO.fromEmpNo+"</EmpNo>"+
//					 "<ItemCode>"+deliveryAgentOrderDetailDco.itemCode+"</ItemCode>"+
//					 "<PrimaryQuantity>"+deliveryAgentOrderDetailDco.preCases+"</PrimaryQuantity>"+
//					 "<SecondaryQuantity>"+deliveryAgentOrderDetailDco.preUnits+"</SecondaryQuantity>"+
//					 "<PrimaryReturnQuantity>0</PrimaryReturnQuantity>"+
//					 "<SecondaryReturnQuantity>0</SecondaryReturnQuantity>"+
//					 "<FeederEmpNo>"+transferInoutDO.toEmpNo+"</FeederEmpNo>"+
//					 "</VMSalesmanInventoryDco>";
//		}
//		return strXML;
//	}
////	private static String getVMSalesmanInventoryDcoIN(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String strEmpNo, String strInEmpNo)
////	{
////		String strXML = "";
////		for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : vecOrdProduct) 
////		{
////			strXML += "<VMSalesmanInventoryDco>"+
////					 "<VMSalesmanInventoryId></VMSalesmanInventoryId>"+	
////					 "<Date>"+CalendarUtils.getDeliverydate()+"</Date>"+
////					 "<EmpNo>"+strEmpNo+"</EmpNo>"+
////					 "<ItemCode>"+deliveryAgentOrderDetailDco.itemCode+"</ItemCode>"+
////					 "<PrimaryQuantity>"+deliveryAgentOrderDetailDco.preCases+"</PrimaryQuantity>"+
////					 "<SecondaryQuantity>"+deliveryAgentOrderDetailDco.preUnits+"</SecondaryQuantity>"+
////					 "<PrimaryReturnQuantity>0</PrimaryReturnQuantity>"+
////					 "<SecondaryReturnQuantity>0</SecondaryReturnQuantity>"+
////					 "<FeederEmpNo>"+strInEmpNo+"</FeederEmpNo>"+
////					 "</VMSalesmanInventoryDco>";
////		}
////		return strXML;
////	}
////	public static String UpdateTransferInStock(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String strEmpNo, String strInEmpNo)
////	{
////		String strXML =   	SOAP_HEADER + 
////		"<UpdateTransferInOrOutStock xmlns=\"http://tempuri.org/\">"+
////		"<objTransferInOrOutStock>"+
////		"<Inventories>"+getVMSalesmanInventoryDcoIN(vecOrdProduct, strEmpNo, strInEmpNo)+"</Inventories>"+
////		"</objTransferInOrOutStock>"+
////		"</UpdateTransferInOrOutStock>"+
////		SOAP_FOOTER ;
////		return strXML;
////	}
//	
//	
//	public static String PostTransferOuts(Vector<TransferInoutDO> vecTransferInoutDOs)
//	{
//		String strXML =   	SOAP_HEADER + 
//		"<PostTransfers xmlns=\"http://tempuri.org/\">"+
//		"<objTransferHeaderDcos>"+
//		getTransferHeaderDCO(vecTransferInoutDOs)+
//		"</objTransferHeaderDcos>"+
//		"</PostTransfers>"+
//		SOAP_FOOTER ;
//		return strXML;
//	}
//	
//	private static String getTransferHeaderDCO(Vector<TransferInoutDO> vecTransferInoutDOs)
//	{
//		String strXML = "";
//		for (TransferInoutDO transferInoutDO : vecTransferInoutDOs) 
//		{
//			String transferType = "";
//			if(transferInoutDO.trnsferType.equalsIgnoreCase("IN"))
//				transferType = "1";
//			else
//				transferType = "0";
//			strXML += "<TransferHeaderDco>"+
//			"<TransferId>"+transferInoutDO.InventoryUID+"</TransferId>"+
//			"<SourceEmpCode>"+transferInoutDO.fromEmpNo+"</SourceEmpCode>"+
//			"<DestinationEmpCode>"+transferInoutDO.toEmpNo+"</DestinationEmpCode>"+
//			"<SourceVehicleCode>"+transferInoutDO.sourceVNO+"</SourceVehicleCode>"+
//			"<DestinationVehicleCode>"+transferInoutDO.destVNO+"</DestinationVehicleCode>"+
//			"<Date>"+transferInoutDO.Date+"</Date>"+
//			"<Type>"+transferType+"</Type>"+
//			"<OrderNumber>"+transferInoutDO.sourceOrderID+"</OrderNumber>"+
//			"<SourceOrderNumber>"+transferInoutDO.destOrderID+"</SourceOrderNumber>"+
//			"<Status>"+0+"</Status>"+
//			"<TransferDetailDcos>"+
//			getTransferDcos(transferInoutDO.vecOrderDetailDcos, transferInoutDO.InventoryUID, transferInoutDO.sourceOrderID)+
//			"</TransferDetailDcos>"+
//			"</TransferHeaderDco>";
//		}
//		return strXML;
//	}
//	
//	private static String getTransferDcos(ArrayList<DeliveryAgentOrderDetailDco> vecOrderDetailDcos, String transferId, String orderNumber)
//	{
//		String strXML = "";
//		for (DeliveryAgentOrderDetailDco detailDco:  vecOrderDetailDcos) 
//		{
//			strXML += "<TransferDetailDco>"+
//			"<TransferDetailId>"+detailDco.transferDetailID+"</TransferDetailId>"+
//			"<TransferId>"+transferId+"</TransferId>"+
//			"<ItemCode>"+detailDco.itemCode+"</ItemCode>"+
//			"<ReqestedCases>"+detailDco.preCases+"</ReqestedCases>"+
//			"<ReqestedUnits>"+detailDco.preUnits+"</ReqestedUnits>"+
//			"<TransferredCases>"+detailDco.preCases+"</TransferredCases>"+
//			"<TransferredUnits>"+detailDco.preUnits+"</TransferredUnits>"+
//			"<ReqestedQty>"+detailDco.totalCases+"</ReqestedQty>"+
//			"<TransferredQty>"+detailDco.totalCases+"</TransferredQty>"+
//			"<OrderNumber>"+orderNumber+"</OrderNumber>"+
//			"</TransferDetailDco>";
//		}
//		return strXML;
//	}
//	public static String UpdateTransferInStockUpload(TransferInoutDO transferInoutDO)
//	{
//		String strXML =   	SOAP_HEADER + 
//		"<UpdateTransferInOrOutStock xmlns=\"http://tempuri.org/\">"+
//		"<objTransferInOrOutStock>"+
//		"<Inventories>"+getVMSalesmanInventoryDcoINUpload(transferInoutDO)+"</Inventories>"+
//		"</objTransferInOrOutStock>"+
//		"</UpdateTransferInOrOutStock>"+
//		SOAP_FOOTER ;
//		return strXML;
//	}
//	///////////////////////////////////////////////////////////////////////////////
//	private static String getVMSalesmanInventoryDcoOutUpload(TransferInoutDO transferInoutDO)
//	{
//		String strXML = "";
//		for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : transferInoutDO.vecOrderDetailDcos) 
//		{
//			strXML += "<VMSalesmanInventoryDco>"+
//					 "<VMSalesmanInventoryId></VMSalesmanInventoryId>"+	
//					 "<Date>"+CalendarUtils.getDeliverydate()+"</Date>"+
//					 "<EmpNo>"+transferInoutDO.fromEmpNo+"</EmpNo>"+
//					 "<ItemCode>"+deliveryAgentOrderDetailDco.itemCode+"</ItemCode>"+
//					 "<PrimaryQuantity>0</PrimaryQuantity>"+
//					 "<SecondaryQuantity>0</SecondaryQuantity>"+
//					 "<PrimaryReturnQuantity>"+deliveryAgentOrderDetailDco.preCases+"</PrimaryReturnQuantity>"+
//					 "<SecondaryReturnQuantity>"+deliveryAgentOrderDetailDco.preUnits+"</SecondaryReturnQuantity>"+
//					 "<FeederEmpNo>"+transferInoutDO.toEmpNo+"</FeederEmpNo>"+
//					 "</VMSalesmanInventoryDco>";
//		}
//		return strXML;
//	}
//	
////	private static String getVMSalesmanInventoryDcoOut(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String strEmpNo, String strOutEmpNo)
////	{
////		String strXML = "";
////		for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : vecOrdProduct) 
////		{
////			strXML += "<VMSalesmanInventoryDco>"+
////					 "<VMSalesmanInventoryId></VMSalesmanInventoryId>"+	
////					 "<Date>"+CalendarUtils.getDeliverydate()+"</Date>"+
////					 "<EmpNo>"+strOutEmpNo+"</EmpNo>"+
////					 "<ItemCode>"+deliveryAgentOrderDetailDco.itemCode+"</ItemCode>"+
////					 "<PrimaryQuantity>0</PrimaryQuantity>"+
////					 "<SecondaryQuantity>0</SecondaryQuantity>"+
////					 "<PrimaryReturnQuantity>"+deliveryAgentOrderDetailDco.preCases+"</PrimaryReturnQuantity>"+
////					 "<SecondaryReturnQuantity>"+deliveryAgentOrderDetailDco.preUnits+"</SecondaryReturnQuantity>"+
////					 "<FeederEmpNo>"+strEmpNo+"</FeederEmpNo>"+
////					 "</VMSalesmanInventoryDco>";
////		}
////		return strXML;
////	}
//	public static String UpdateTransferOutStockUpload(TransferInoutDO transferInoutDO)
//	{
//		String strXML =   	SOAP_HEADER + 
//		"<UpdateTransferInOrOutStock xmlns=\"http://tempuri.org/\">"+
//		"<objTransferInOrOutStock>"+
//		"<Inventories>"+getVMSalesmanInventoryDcoOutUpload(transferInoutDO)+"</Inventories>"+
//		"</objTransferInOrOutStock>"+
//		"</UpdateTransferInOrOutStock>"+
//		SOAP_FOOTER ;
//		return strXML;
//	}
////	public static String UpdateTransferOutStock(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String strEmpNo, String strOutEmpNo)
////	{
////		String strXML =   	SOAP_HEADER + 
////		"<UpdateTransferInOrOutStock xmlns=\"http://tempuri.org/\">"+
////		"<objTransferInOrOutStock>"+
////		"<Inventories>"+getVMSalesmanInventoryDcoOut(vecOrdProduct, strEmpNo, strOutEmpNo)+"</Inventories>"+
////		"</objTransferInOrOutStock>"+
////		"</UpdateTransferInOrOutStock>"+
////		SOAP_FOOTER ;
////		return strXML;
////	}
//	
//	
//	/**
//	 * Method to build request to get all Inventory
//	 * @param strSyncTime
//	 * @param string 
//	 * @return String
//	 */
//	public static String getAllPromotions(String strEmpNo , String lsd, String lst)
//	{
//		String strXML = SOAP_HEADER + 
//						"<GetAllPromotions xmlns=\"http://tempuri.org/\">"+
//	     				"<UserCode>"+strEmpNo+"</UserCode>"+
//	     				"<lsd>"+lsd+"</lsd>"+
//	     				"<lst>"+lst+"</lst>"+
//	     				"</GetAllPromotions>"+
//						SOAP_FOOTER ;
//		return strXML;
//	}
//	
//	public static String getAllPromotionsWithLastSynch(String strEmpNo , String lsd, String lst)
//	{
//		String strXML = SOAP_HEADER + 
//						"<GetAllPromotions xmlns=\"http://tempuri.org/\">"+
//	     				"<UserCode>"+strEmpNo+"</UserCode>"+
//	     				"<lsd>"+lsd+"</lsd>"+
//	     				"<lst>"+lsd+"</lst>"+
//	     				"</GetAllPromotions>"+
//						SOAP_FOOTER ;
//		return strXML;
//	}
	//TODO Auto-generated catch block
	
	public static String updateDeliveryStatus(Vector<DeliveryAgentOrderDetailDco> vecSalesOrders, String deliveredBy, boolean isEOT)
	{
		arrOrderNumbers = new ArrayList<String>();
		
		//need to change the object type here
		String strOrders = "";
		String strXML = SOAP_HEADER +     
		 				"<UpdateDeliveryStatus xmlns=\"http://tempuri.org/\">"+
						"<RequestString>"+
						"&lt;DeliveryStatusUpdateRequest&gt;"+
						"&lt;DeliveryDetails&gt;";
		
						for(DeliveryAgentOrderDetailDco objProduct : vecSalesOrders)
						{
							if(arrOrderNumbers != null && !arrOrderNumbers.contains(objProduct.blaseOrderNumber))
								arrOrderNumbers.add(objProduct.blaseOrderNumber);
							
							LogUtils.errorLog("blaseOrderNumber", "blaseOrderNumber - Sended"+objProduct.blaseOrderNumber);
							strOrders = strOrders+"&lt;DeliveryDetail&gt;"+
										"&lt;BLASE_ORDER_NO&gt;"+objProduct.blaseOrderNumber+"&lt;/BLASE_ORDER_NO&gt;"+
										"&lt;ITEM_CODE&gt;"+objProduct.itemCode+"&lt;/ITEM_CODE&gt;"+
										"&lt;BLASE_ORDER_STATUS&gt;"+"D"+"&lt;/BLASE_ORDER_STATUS&gt;"+
										"&lt;DELIVERED_QUANTITY&gt;"+objProduct.cases+"&lt;/DELIVERED_QUANTITY&gt;"+
										"&lt;ACTUAL_SHIP_DATE&gt;"+(!isEOT ? (CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00"):(objProduct.actualShipedDate))+"&lt;/ACTUAL_SHIP_DATE&gt;"+
										"&lt;DeliveredBy&gt;"+deliveredBy+"&lt;/DeliveredBy&gt;"+
										"&lt;ORDER_INVOICE_NUMBER&gt;"+objProduct.orderInvoiceNumber+"&lt;/ORDER_INVOICE_NUMBER&gt;"+
										"&lt;/DeliveryDetail&gt;";
						}
			  strXML = 	strXML +strOrders+"&lt;/DeliveryDetails&gt;"+
			  					  		  "&lt;/DeliveryStatusUpdateRequest&gt;" +
			  					  		  "</RequestString>"+
			  					  		  "</UpdateDeliveryStatus>"+
						SOAP_FOOTER ;
		return strXML;
	}
	
	/**
	 * Method to update the Delivery status of orders by delivery agent 
	 * @param noteId
	 * @return String
	 */
	public static String updateDeliveryStatus_New(Vector<DeliveryAgentOrderDetailDco> vecSalesOrders, String deliveredBy, boolean isEOT)
	{
		arrOrderNumbers = new ArrayList<String>();
		//need to change the object type here
		String strOrders = "";
		String strXML = SOAP_HEADER +     
		 				"<UpdateDeliveryStatus_New xmlns=\"http://tempuri.org/\">"+
						"<RequestString>"+
						"&lt;DeliveryStatusUpdateRequest&gt;"+
						"&lt;DeliveryDetails&gt;";
		
						for(DeliveryAgentOrderDetailDco objProduct : vecSalesOrders)
						{
							if(arrOrderNumbers != null && !arrOrderNumbers.contains(objProduct.blaseOrderNumber))
								arrOrderNumbers.add(objProduct.blaseOrderNumber);
							
//							String disString = ((StringUtils.getFloat(objProduct.recommendedQty) ==0 || objProduct.cases >= StringUtils.getFloat(objProduct.recommendedQty) || objProduct.cases >= StringUtils.getFloat(objProduct.actualQtyShiped)) ? objProduct.strDiscount : "0") ;
							
							String disString =objProduct.strDiscount  ;
							LogUtils.errorLog("blaseOrderNumber", "blaseOrderNumber - Sended"+objProduct.blaseOrderNumber);
							strOrders = strOrders+"&lt;DeliveryDetail&gt;"+
										"&lt;BLASE_ORDER_NO&gt;"+objProduct.blaseOrderNumber+"&lt;/BLASE_ORDER_NO&gt;"+
										"&lt;ITEM_CODE&gt;"+objProduct.itemCode+"&lt;/ITEM_CODE&gt;"+
										"&lt;BLASE_ORDER_STATUS&gt;"+"D"+"&lt;/BLASE_ORDER_STATUS&gt;"+
										"&lt;DELIVERED_QUANTITY&gt;"+objProduct.cases+"&lt;/DELIVERED_QUANTITY&gt;"+
										"&lt;ACTUAL_SHIP_DATE&gt;"+(!isEOT ? (CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00"):((objProduct.actualShipedDate)))+"&lt;/ACTUAL_SHIP_DATE&gt;"+
										"&lt;DeliveredBy&gt;"+deliveredBy+"&lt;/DeliveredBy&gt;"+
										"&lt;ORDER_INVOICE_NUMBER&gt;"+objProduct.orderInvoiceNumber+"&lt;/ORDER_INVOICE_NUMBER&gt;"+
										"&lt;DISCOUNT&gt;"+(disString != null ? disString : "0")+"&lt;/DISCOUNT&gt;"+
										"&lt;/DeliveryDetail&gt;";
						}
			  strXML = 	strXML +strOrders+"&lt;/DeliveryDetails&gt;"+
			  					  		  "&lt;/DeliveryStatusUpdateRequest&gt;" +
			  					  		  "</RequestString>"+
			  					  		  "</UpdateDeliveryStatus_New>"+
						SOAP_FOOTER ;
			  LogUtils.errorLog("strXML", "strXML" + strXML);
		return strXML;
	}
	
//	public static String uploadLoadRequests(ArrayList<LoadRequestDO> vecLoad)
//	{
//		String strOrder  = ""; 
//		String strXML = SOAP_HEADER + "<PostMovementDetailsFromXML xmlns=\"http://tempuri.org/\">";
//				String strBody = "<objMovementHeaderDcos>";
//  							  		for(LoadRequestDO orderDO : vecLoad)
//  							  		{
//								  		strOrder  = strOrder+"<MovementHeaderDco>" +
//								  		  "<MovementCode>"+orderDO.MovementCode+"</MovementCode>"+
//								          "<PreMovementCode>"+orderDO.PreMovementCode+"</PreMovementCode>"+
//								          "<AppMovementId>"+orderDO.AppMovementId+"</AppMovementId>"+
//								          "<OrgCode>"+orderDO.OrgCode+"</OrgCode>"+
//								          "<UserCode>"+orderDO.UserCode+"</UserCode>"+
//								          "<WHKeeperCode>"+orderDO.UserCode+"</WHKeeperCode>"+
//								          "<CurrencyCode>"+orderDO.CurrencyCode+"</CurrencyCode>"+
//								          "<JourneyCode>"+orderDO.JourneyCode+"</JourneyCode>"+
//								          "<MovementDate>"+orderDO.MovementDate+"</MovementDate>"+
//								          "<MovementNote>"+orderDO.MovementNote+"</MovementNote>"+
//								          "<MovementType>"+orderDO.MovementType+"</MovementType>"+
//								          "<SourceVehicleCode>"+orderDO.SourceVehicleCode+"</SourceVehicleCode>"+
//								          "<DestinationVehicleCode>"+orderDO.DestinationVehicleCode+"</DestinationVehicleCode>"+
//								          "<VisitID>"+orderDO.VisitID+"</VisitID>"+
//								          "<CreatedOn>"+orderDO.CreatedOn+"</CreatedOn>"+
//								          "<Amount>"+orderDO.Amount+"</Amount>" +
//								          "<WHCode>"+orderDO.WHCode+"</WHCode>"+
//								          "<_MovementStatus>"+orderDO.MovementStatus+"</_MovementStatus>"+
//								          "<MovementStatus>"+orderDO.MovementStatus+"</MovementStatus>"+
//								          "<ProductType>"+orderDO.ProductType+"</ProductType>"+
//										getLoadDetail(orderDO.vecItems, orderDO)+
//    							  		"</MovementHeaderDco>";
//  							  		}
//	      					strBody = strBody+strOrder;
//	      					strXML = strXML+strBody+
//	      							  "</objMovementHeaderDcos>"+
//	      							  "</PostMovementDetailsFromXML>"+
//	      							  SOAP_FOOTER ;
//	      					LogUtils.errorLog("strXML", ""+strXML);
//	    return strXML;
//	}
	
	public static String VerifyRequestRequests(VerifyRequestDO verifyRequestDO)
	{
		StringBuilder strOrder = new StringBuilder();
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER) 
			  .append("<ShipStockMovementsFromXML xmlns=\"http://tempuri.org/\">")
			  .append("<objMovementHeaderDcos>");
		
		strOrder.append("<MovementHeaderDco>")
		  		 .append("<MovementCode>").append(verifyRequestDO.movementCode).append("</MovementCode>")
		         .append("<MovementStatus>100</MovementStatus>")
		         .append("<SalesmanSignature>").append(URLEncoder.encode(verifyRequestDO.salesmanSignature+"")).append("</SalesmanSignature>")
		         .append("<WHManagerSignature>").append(URLEncoder.encode(verifyRequestDO.logisticSignature+"")).append("</WHManagerSignature>")
		         .append(getVerifiItem(verifyRequestDO.vecVanLodDOs, verifyRequestDO.movementCode, verifyRequestDO.movementType))
		  		.append("</MovementHeaderDco>");
		strXML.append(strOrder)
			 .append("</objMovementHeaderDcos>")
			 .append("</ShipStockMovementsFromXML>")
			.append(SOAP_FOOTER) ;
		LogUtils.errorLog("strXML", ""+strXML);
		try
		{
			ConnectionHelper.writeIntoLog(strXML.toString(), "Shipment");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	    return strXML.toString();
	}
	
	private static String getVerifiItem(ArrayList<VanLoadDO> vecItems, String movementId, String movementType)
	{
		StringBuilder strXML = new StringBuilder();
		strXML.append("<MovementDetailDcos>");
		if(vecItems != null && vecItems.size() >0)
		{
			int count =0;
			for (VanLoadDO productDO : vecItems)
			{
		        count++;
				try {
					strXML.append("<MovementDetailDco>")
						.append("<LineNo>").append(count).append("</LineNo>")
						.append("<MovementCode>").append(movementId).append("</MovementCode>")
						.append("<ItemCode>").append(productDO.ItemCode).append("</ItemCode>")
						.append("<OrgCode></OrgCode>")
						.append("<ItemDescription>").append(URLEncoder.encode(productDO.Description,"UTF-8")).append("</ItemDescription>")
						.append("<ItemAltDescription></ItemAltDescription>")
						.append("<UOM>").append(productDO.UOM).append("</UOM>")
						.append("<QuantityLevel1>0</QuantityLevel1>")
						.append("<QuantityLevel2>0</QuantityLevel2>")
						.append("<QuantityLevel3>0</QuantityLevel3>")
						
						.append("<InProcessQuantityLevel1>0</InProcessQuantityLevel1>")
						.append("<InProcessQuantityLevel2>0</InProcessQuantityLevel2>")
						.append("<InProcessQuantityLevel3>0</InProcessQuantityLevel3>")
						
						.append("<ShippedQuantityLevel1>").append(productDO.shippedQuantityLevel1).append("</ShippedQuantityLevel1>")
						.append("<ShippedQuantityLevel2>").append(productDO.shippedQuantityLevel2).append("</ShippedQuantityLevel2>")
						.append("<ShippedQuantityLevel3>").append(productDO.shippedQuantityLevel3).append("</ShippedQuantityLevel3>")
						
						.append("<NonSellableQty>0</NonSellableQty>")
						.append("<QuantityBU>0</QuantityBU>")
						.append("<CurrencyCode>0</CurrencyCode>")
						.append("<PriceLevel1>0</PriceLevel1>")
						.append("<PriceLevel2>0</PriceLevel2>")
						.append("<PriceLevel3>0</PriceLevel3>")
						.append("<MovementReasonCode>").append(productDO.MovementReasonCode).append("</MovementReasonCode>")
						.append("<ExpiryDate>").append(productDO.ExpiryDate).append("</ExpiryDate>")
						.append("<CreatedOn>").append(productDO.CreatedOn).append("</CreatedOn>")
						.append("<MovementType>").append(movementType).append("</MovementType>")
						.append("<CancelledQuantity>").append(productDO.CancelledQuantity).append("</CancelledQuantity>")
						.append("<InProcessQuantity>").append(productDO.inProccessQty).append("</InProcessQuantity>")
						.append("<ShippedQuantity>").append(productDO.ShippedQuantity).append("</ShippedQuantity>")
						.append("</MovementDetailDco>");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
		strXML.append("</MovementDetailDcos>");
		return strXML.toString();
	}

//	private static String getLoadDetail(ArrayList<LoadRequestDetailDO> vecItems, LoadRequestDO loadRequestDO)
//	{
//		String strXML = "";
//		strXML = strXML + "<MovementDetailDcos>";
//		if(vecItems != null && vecItems.size() >0)
//		{
//			for (LoadRequestDetailDO productDO : vecItems)
//			{
//		        
//				try {
//					strXML = strXML + "<MovementDetailDco>"+
//							"<LineNo>"+productDO.LineNo+"</LineNo>"+
//							"<MovementCode>"+productDO.MovementCode+"</MovementCode>"+
//							"<ItemCode>"+productDO.ItemCode+"</ItemCode>"+
//							"<OrgCode>"+productDO.OrgCode+"</OrgCode>"+
//							"<ItemDescription>"+URLEncoder.encode(productDO.ItemDescription,"UTF-8")+"</ItemDescription>"+
//							"<ItemAltDescription>"+productDO.ItemAltDescription+"</ItemAltDescription>"+
//							"<UOM>"+productDO.UOM+"</UOM>"+
//							"<QuantityLevel1>"+productDO.QuantityLevel1+"</QuantityLevel1>"+
//							"<QuantityLevel2>"+productDO.QuantityLevel2+"</QuantityLevel2>"+
//							"<QuantityLevel3>"+productDO.QuantityLevel3+"</QuantityLevel3>"+
//							"<NonSellableQty>"+productDO.NonSellableQty+"</NonSellableQty>"+
//							"<QuantityBU>"+productDO.QuantityBU+"</QuantityBU>"+
//							"<CurrencyCode>"+productDO.CurrencyCode+"</CurrencyCode>"+
//							"<PriceLevel1>"+productDO.PriceLevel1+"</PriceLevel1>"+
//							"<PriceLevel2>"+productDO.PriceLevel2+"</PriceLevel2>"+
//							"<PriceLevel3>"+productDO.PriceLevel3+"</PriceLevel3>"+
//							"<MovementReasonCode>"+productDO.MovementReasonCode+"</MovementReasonCode>"+
//							"<ExpiryDate>"+productDO.ExpiryDate+"</ExpiryDate>"+
//							"<CreatedOn>"+productDO.CreatedOn+"</CreatedOn>"+
//							"<MovementType>"+loadRequestDO.MovementType+"</MovementType>"+
//							"<CancelledQuantity>"+productDO.CancelledQuantity+"</CancelledQuantity>"+
//							"<InProcessQuantity>"+productDO.InProcessQuantity+"</InProcessQuantity>"+
//							"<ShippedQuantity>"+productDO.ShippedQuantity+"</ShippedQuantity>"+
//							"</MovementDetailDco>";
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}	
//			}
//		}
//		strXML = strXML + "</MovementDetailDcos>";
//		return strXML;
//	}
	
	public static String getApprovedMovements(String movementCode, String appMovementCode)
	{
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER) 
					.append("<GetApprovedMovements xmlns=\"http://tempuri.org/\">")
	     			.append("<MovementCode>").append(movementCode).append("</MovementCode>")
	     			.append("<AppMovementCode>").append(appMovementCode).append("</AppMovementCode>")
	     			.append("</GetApprovedMovements>")
	     			.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	public static String getSkipReason(String SalesmanId, String synctime)
	{

		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
				.append("<GetAllTask xmlns=\"http://tempuri.org/\">" )
				.append("<UserCode>").append(SalesmanId).append("</UserCode>")
				.append("<LastSyncDate>").append(synctime).append("</LastSyncDate>")
				.append("</GetAllTask>")
				.append(SOAP_FOOTER) ;
		LogUtils.errorLog("strXML",""+strXML);

//		String strXML =  SOAP_HEADER+"<GetAllTask xmlns=\"http://tempuri.org/\">"+
//						"<UserCode>"+SalesmanId+"</UserCode>"+
//						"<LastSyncDate>"+synctime+"</LastSyncDate>"+
//						"</GetAllTask>"+
//						SOAP_FOOTER;;
		return strXML.toString();
	}
	
	public static String postReasons(ArrayList<PostReasonDO> arrList)
	{
		StringBuilder body = new StringBuilder();
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER) 
					 	.append("<InsertSkippingReason xmlns=\"http://tempuri.org/\">")
						.append("<SkippingReasonRequest>")
					 	.append("<SkippingReasons>");
			
			for(PostReasonDO detailDco : arrList)
			{
				body.append("<SkippingReason>")
						.append("<PresellerId>").append(detailDco.presellerId).append("</PresellerId>")
	            		.append("<SkippingDate>").append(detailDco.skippingDate).append("</SkippingDate>")
	            		.append("<Reason>").append(detailDco.reason).append("</Reason>")
	            		.append("<SiteId>").append(detailDco.customerSiteID).append("</SiteId>")
	            		.append("<Type>").append(detailDco.reasonType).append("</Type>")
						.append("</SkippingReason>");
			}
			strXML.append(body)
					.append("</SkippingReasons>")
					.append("</SkippingReasonRequest>")
					.append("</InsertSkippingReason>")
					.append(SOAP_FOOTER) ;
			
			LogUtils.errorLog("service_name", "strXML - "+strXML);
			return strXML.toString();
	}
	
	public static String getAssetMasters(String userCode , String lsd , String lst)
	{
		
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
					.append("<GetAssetMasters xmlns=\"http://tempuri.org/\">" )
					.append("<UserCode>").append(userCode).append("</UserCode>")
					 .append("<lsd>").append(lsd).append("</lsd>")
					 .append("<lst>").append(lst).append("</lst>")
					.append("</GetAssetMasters>")
	      			.append(SOAP_FOOTER) ;
	      			LogUtils.errorLog("strXML",""+strXML);
		
//		String strXML = SOAP_HEADER + 
//						"<GetAssetMasters xmlns=\"http://tempuri.org/\">"+
//	     				"<UserCode>"+userCode+"</UserCode>"+
//	     				"<lsd>"+lsd+"</lsd>"+
//	     				"<lst>"+lst+"</lst>"+
//	     				"</GetAssetMasters>"+
//						SOAP_FOOTER ;
//		return strXML;
		return strXML.toString();
	}
	
	public static String getAllTasks(String SalesmanId, String synctime)
	{
		
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
					.append("<GetAllTask xmlns=\"http://tempuri.org/\">" )
					.append("<UserCode>").append(SalesmanId).append("</UserCode>")
					 .append("<LastSyncDate>").append(synctime).append("</LastSyncDate>")
					.append("</GetAllTask>")
	      			.append(SOAP_FOOTER) ;
	      			LogUtils.errorLog("strXML",""+strXML);
		
//		String strXML =  SOAP_HEADER+"<GetAllTask xmlns=\"http://tempuri.org/\">"+
//						"<UserCode>"+SalesmanId+"</UserCode>"+
//						"<LastSyncDate>"+synctime+"</LastSyncDate>"+
//						"</GetAllTask>"+
//						SOAP_FOOTER;;
		return strXML.toString();
	}
	
	
	public static String postAsset(Vector<AssetTrackingDo> assetTrackingDos)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(SOAP_HEADER)
			  .append("<PostAsset xmlns=\"http://tempuri.org/\">")
			  .append("<objAssetDcos>")
			  .append(getAssetTrackingXml(assetTrackingDos))
			  .append("</objAssetDcos>")
			  .append("</PostAsset>")
			  .append(SOAP_FOOTER);
			
			return buffer.toString();
	}
	
	public static String PostAssetServiceRequest (Vector<AssetServiceDO> vecassetserDo)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(SOAP_HEADER)
			  .append("<PostAssetServiceRequest xmlns=\"http://tempuri.org/\">")
			  .append("<objAssetServiceRequestDcos>")
			  .append(getAssetServiceRequestXml(vecassetserDo))
			  .append("</objAssetServiceRequestDcos>")
			  .append("</PostAssetServiceRequest>")
			  .append(SOAP_FOOTER);
			
			return buffer.toString();
	}
	
	private static String getAssetServiceRequestXml(Vector<AssetServiceDO> vecassetserDo) 
	{
		
		StringBuffer buffer = new StringBuffer();
		
		for(AssetServiceDO assetServiceDO : vecassetserDo)
		{
			
			buffer
			 	  .append("<AssetServiceRequestDco>")
			 	  .append("<AssetServiceRequestId>").append(0).append("</AssetServiceRequestId>")
			 	  .append("<UserCode>").append(assetServiceDO.userCode).append("</UserCode>")
			      .append("<SiteNo>").append(assetServiceDO.siteNo).append("</SiteNo>")
			      .append("<RequestDate>").append(assetServiceDO.requestDate).append("</RequestDate>")
			      .append("<RequestImage>").append(assetServiceDO.requestImage).append("</RequestImage>")
			      .append("<JourneyCode>").append(assetServiceDO.journeyCode).append("</JourneyCode>")
			      .append("<VisitCode>").append(assetServiceDO.visitCode).append("</VisitCode>")
			      .append("<Status>").append(assetServiceDO.status).append("</Status>")
			      .append("<Notes>").append(assetServiceDO.notes).append("</Notes>")
				  .append("<IsApproved>").append(assetServiceDO.isApproved).append("</IsApproved>")
				  .append("<ServiceRequestAppId>").append(assetServiceDO.serviceRequestAppId).append("</ServiceRequestAppId>")
				  .append("</AssetServiceRequestDco>");
		}   
		return buffer.toString();
	}

	public static String postSurvey(CustomerSurveyDONew cusSurveyDo)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(SOAP_HEADER)
		      .append("<PostSurvey xmlns=\"http://tempuri.org/\">")
		      .append("<objSurveyDcos>")
		      .append(getSurveyXml(cusSurveyDo))
		      .append("</objSurveyDcos>")
		      .append("</PostSurvey>")
		      .append(SOAP_FOOTER);
		
		LogUtils.errorLog("PostSurveyXML", buffer.toString());
		return buffer.toString();
		
	}

	public static String postAssetsRequest(Vector<AssetRequestDO> VecassetRequestDOs)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(SOAP_HEADER)
		      .append("<InsertAssetCustomer xmlns=\"http://tempuri.org/\">")
		       .append("<objAssetCustomer>")
		      .append(getAssetReqxml(VecassetRequestDOs))
		      .append("</objAssetCustomer>")
		      .append("</InsertAssetCustomer>")
		      .append(SOAP_FOOTER);
		
		
		LogUtils.errorLog("InsertAssetCustomer ", buffer.toString());
		return buffer.toString();
		
	}

	private static String getAssetReqxml(Vector<AssetRequestDO> vecassetRequestDOs) 
	{
		StringBuffer buffer = new StringBuffer();
		
		for(AssetRequestDO assetRequestDO : vecassetRequestDOs)
		{
			buffer.append("<PostAssetCustomerDco>")
				  .append("<AssetId>").append(assetRequestDO.strReqAssest).append("</AssetId>")
				  .append("<SiteNo>").append(assetRequestDO.strReqSiteNumber).append("</SiteNo>")
				  .append("<InstallationDate>").append(assetRequestDO.strReqDate).append("</InstallationDate>")
				  .append("<Quantity>").append(assetRequestDO.quntityReq).append("</Quantity>")
				  .append("</PostAssetCustomerDco>");
		}
		return  buffer.toString();
	}

	private static Object getSurveyXml(CustomerSurveyDONew CusSurveyDo)
	{

		StringBuffer buffer = new StringBuffer();
		
		
			try {
				buffer.append("<SurveyDco>") 
					  .append("<SurveyAppId>").append(CusSurveyDo.SurveyAppId).append("</SurveyAppId>")
					  
				      .append("<SurveyId>").append(CusSurveyDo.serveyId).append("</SurveyId>")
				      .append("<UserCode>").append(CusSurveyDo.userCode).append("</UserCode>")
//			      .append("<ClientCode>").append(CusSurveyDo.clientCode).append("</ClientCode>")
				      .append("<ClientCode>").append(URLEncoder.encode(CusSurveyDo.clientCode,"UTF-8")).append("</ClientCode>")
				      .append("<Date>").append(CusSurveyDo.date).append("</Date>")
				      .append("<Latitude>").append(CusSurveyDo.lattitude).append("</Latitude>")
				      .append("<Longitude>").append(CusSurveyDo.longitude).append("</Longitude>")
				      .append("<UserRole>").append(CusSurveyDo.userRole).append("</UserRole>")
				      .append("<JourneyCode>").append(CusSurveyDo.journeyCode).append("</JourneyCode>")
				      .append("<VisitCode>").append(CusSurveyDo.visitCode).append("</VisitCode>")
				      .append("<SurveyResultDcos>")
				      .append(getSurveyResultXML(CusSurveyDo.srveyQues,CusSurveyDo.SurveyResultAppId,CusSurveyDo.resultServeyId,CusSurveyDo.serveyId))
				      .append("</SurveyResultDcos>")
				      .append("</SurveyDco>");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			      
		
		
		return buffer.toString();
	}

	private static Object getSurveyResultXML(Vector<SurveyQuestionDONew> srveyQues, String SurveyResultAppId, String resultServeyId, String serveyId) 
	{

		
		StringBuffer buffer = new StringBuffer();
		
		for(SurveyQuestionDONew suquesdo : srveyQues)
		{
			
			buffer
			 	  .append("<SurveyResultDco>")
			 	  .append("<SurveyResultAppId>").append(SurveyResultAppId).append("</SurveyResultAppId>")
			 	  .append("<SurveyResultId>").append(resultServeyId).append("</SurveyResultId>")
			      .append("<SurveyId>").append(serveyId).append("</SurveyId>")
			      .append("<QuestionId>").append(suquesdo.questionId).append("</QuestionId>")
			      .append("<Question>").append(suquesdo.question).append("</Question>")
			      .append("<OptionId>").append(suquesdo.optionId).append("</OptionId>")
			      .append("<Option>").append(suquesdo.option).append("</Option>")
				  .append("<Comment>").append(suquesdo.comments).append("</Comment>")
				  .append("</SurveyResultDco>");
			      
		}
		
		return buffer.toString();
	}

	private static String getAssetTrackingXml(Vector<AssetTrackingDo> assetTrackingDos)
	{
		StringBuffer buffer = new StringBuffer();
		
		for(AssetTrackingDo assetTrackingDo : assetTrackingDos)
		{
			String assetTrackingId = "0";
			if(!assetTrackingDo.assetTrackingId.contains("-"))
				assetTrackingId = assetTrackingDo.assetTrackingId;
			buffer.append("<AssetTrackingDco>")
				  .append("<AssetTrackingId>").append(0).append("</AssetTrackingId>")
				  .append("<AssetTrackingAppId>").append(assetTrackingDo.assetTrackingId).append("</AssetTrackingAppId>")
				  .append("<UserCode>").append(assetTrackingDo.userCode).append("</UserCode>")
				  .append("<SiteNo>").append(assetTrackingDo.siteNo).append("</SiteNo>")
				  .append("<VisitCode>").append(assetTrackingDo.visitedCode).append("</VisitCode>")
				  .append("<JourneyCode>").append(assetTrackingDo.journeyCode).append("</JourneyCode>")
				  .append("<Date>").append(assetTrackingDo.date).append("</Date>")
				  .append("<ImagePath>").append(assetTrackingDo.imagepath).append("</ImagePath>")
				  .append("<AssetTrackingDetailDcos>")
				  .append(getAssetTrackingDetailsXml(assetTrackingDo.vAssetTrackingDetailDos,assetTrackingId,assetTrackingDo.imagepath,assetTrackingDo.tempimagepath,assetTrackingDo.temperature))
				  .append("</AssetTrackingDetailDcos>")
				  .append("</AssetTrackingDco>");
		}
		return buffer.toString();
	}
	
	private static String getAssetTrackingDetailsXml(Vector<AssetTrackingDetailDo> vAssetTrackingDetailDos, String assetTrackingId, String imagepath, String tempimagepath, String temperature)
	{
		StringBuffer buffer = new StringBuffer();
		
		for(AssetTrackingDetailDo assetTrackingDetailDo : vAssetTrackingDetailDos)
		 {
			buffer.append("<AssetTrackingDetailDco>")
				  .append("<AssetId>").append(assetTrackingDetailDo.assetId).append("</AssetId>")
				  .append("<AssetTrackingDetailId>").append(0).append("</AssetTrackingDetailId>")
				  .append("<AssetTrackingDetailAppId>").append(assetTrackingDetailDo.assetTrackingDetailId).append("</AssetTrackingDetailAppId>")
				  .append("<AssetTrackingId>").append(assetTrackingId).append("</AssetTrackingId>")
				  .append("<ImagePath>").append(imagepath).append("</ImagePath>")
				  .append("<TempImagePath>").append(tempimagepath).append("</TempImagePath>")
				  .append("<Temparature>").append(temperature).append("</Temparature>")
           		  .append("<barCode>").append(assetTrackingDetailDo.barCode).append("</barCode>")
           		  .append("<ScanningTime>").append(assetTrackingDetailDo.scanningTime).append("</ScanningTime>")
           		  .append("</AssetTrackingDetailDco>");
		 }
		return buffer.toString();
	}
	
	public static String getStartJournyStart(Vector<JouneyStartDO> vAssetTrackingDetailDos)
	{
		StringBuffer buffer = new StringBuffer(SOAP_HEADER);
		buffer.append(" <PostJourneyDetails xmlns=\"http://tempuri.org/\">")
		.append("<objJourneyDco>");
		for(JouneyStartDO journeyStartDO : vAssetTrackingDetailDos)
		{
			buffer.append("<JourneyDco>")
				  .append("<UserCode>").append(journeyStartDO.userCode).append("</UserCode>")
				  .append("<JourneyCode>").append(journeyStartDO.journeyCode).append("</JourneyCode>")
				  .append("<Date>").append(journeyStartDO.date).append("</Date>")
           		  .append("<StartTime>").append(journeyStartDO.startTime).append("</StartTime>")
           		  .append("<EndTime>").append(journeyStartDO.endTime).append("</EndTime>")
           		  .append("<TotalTimeInMins>").append(journeyStartDO.TotalTimeInMins).append("</TotalTimeInMins>")
				  .append("<JourneyAppId>").append(journeyStartDO.journeyAppId).append("</JourneyAppId>")
				  .append("<OdometerReading>").append(journeyStartDO.odometerReading).append("</OdometerReading>")
           		  .append("<IsVanStockVerified>").append(journeyStartDO.IsVanStockVerified.toLowerCase()).append("</IsVanStockVerified>")
//           		  .append("<IsVanStockVerified>").append("true").append("</IsVanStockVerified>")
           		  .append("<VerifiedBy>").append(journeyStartDO.VerifiedBy).append("</VerifiedBy>")
           		  .append("<OdometerReadingStart>").append(journeyStartDO.OdometerReadingStart).append("</OdometerReadingStart>")
           		  .append("<OdometerReadingEnd>").append(journeyStartDO.OdometerReadingEnd).append("</OdometerReadingEnd>")
           		  .append("<StoreKeeperSignatureStartDay>").append(URLEncoder.encode(""+journeyStartDO.StoreKeeperSignatureStartDay)).append("</StoreKeeperSignatureStartDay>")
           		  .append("<SalesmanSignatureStartDay>").append(URLEncoder.encode(""+journeyStartDO.SalesmanSignatureStartDay)).append("</SalesmanSignatureStartDay>")
           		  .append("<StoreKeeperSignatureEndDay>").append(URLEncoder.encode(""+journeyStartDO.StoreKeeperSignatureEndDay)).append("</StoreKeeperSignatureEndDay>")
           		  .append("<SalesmanSignatureEndDay>").append(URLEncoder.encode(""+journeyStartDO.SalesmanSignatureEndDay)).append("</SalesmanSignatureEndDay>")
           		  .append("<VehicleCode>").append(URLEncoder.encode(""+journeyStartDO.vehicleCode)).append("</VehicleCode>")
           		  .append("</JourneyDco>");
		}
		buffer.append("</objJourneyDco>")
		.append("</PostJourneyDetails>")
		.append(SOAP_FOOTER);
		
		Log.e("PostJourneyDetails"," PostJourneyDetails = "+buffer.toString());
		return buffer.toString();
	}
	
	public static String getCustomerVisitXML(Vector<CustomerVisitDO> vAssetTrackingDetailDos)
	{
		StringBuffer buffer = new StringBuffer(SOAP_HEADER);
		buffer.append(" <PostClientVisits  xmlns=\"http://tempuri.org/\">")
		.append("<objClientVisitDco>");
		for(CustomerVisitDO assetTrackingDetailDo : vAssetTrackingDetailDos)
		{
			try {
				buffer.append("<ClientVisitDco>")
					  .append("<CustomerVisitId>").append(assetTrackingDetailDo.CustomerVisitId).append("</CustomerVisitId>")
					  .append("<CustomerVisitAppId>").append(assetTrackingDetailDo.CustomerVisitAppId).append("</CustomerVisitAppId>")
					  .append("<UserCode>").append(assetTrackingDetailDo.UserCode).append("</UserCode>")
					  .append("<JourneyCode>").append(assetTrackingDetailDo.JourneyCode).append("</JourneyCode>")
					  .append("<VisitCode>").append(assetTrackingDetailDo.VisitCode).append("</VisitCode>")
//           		  .append("<ClientCode>").append(assetTrackingDetailDo.ClientCode).append("</ClientCode>")
					  .append("<ClientCode>").append(URLEncoder.encode(assetTrackingDetailDo.ClientCode,"UTF-8")).append("</ClientCode>")
					  .append("<Date>").append(assetTrackingDetailDo.Date).append("</Date>")
					  .append("<ArrivalTime>").append(assetTrackingDetailDo.ArrivalTime).append("</ArrivalTime>")
					  .append("<OutTime>").append(assetTrackingDetailDo.OutTime).append("</OutTime>")
					  .append("<TotalTimeInMins>").append(assetTrackingDetailDo.TotalTimeInMins).append("</TotalTimeInMins>")
					  .append("<Latitude>").append(assetTrackingDetailDo.Latitude).append("</Latitude>")
					  .append("<Longitude>").append(assetTrackingDetailDo.Longitude).append("</Longitude>")
					  .append("<IsProductiveCall>").append(assetTrackingDetailDo.IsProductiveCall).append("</IsProductiveCall>")
					  .append("<TypeOfCall>").append(assetTrackingDetailDo.TypeOfCall).append("</TypeOfCall>")
					  .append("<ReasonType>").append(assetTrackingDetailDo.reasonType).append("</ReasonType>")
					  .append("<Reason>").append(assetTrackingDetailDo.reason).append("</Reason>")
					  .append("<VehicleCode>").append(assetTrackingDetailDo.vehicleNo).append("</VehicleCode>")
					  .append("<DriverCode>").append(assetTrackingDetailDo.UserCode).append("</DriverCode>")
					  .append("</ClientVisitDco>");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		buffer.append("</objClientVisitDco>")
		.append("</PostClientVisits >")
		.append(SOAP_FOOTER);
		LogUtils.debug("service_name", buffer.toString());
		return buffer.toString();
	}

	
	public static String postTask(Vector<MyActivityDO> vecMyActivityDOs)
	{
		String strXML =  SOAP_HEADER+
			"<InsertTaskOrder xmlns=\"http://tempuri.org/\">" +
			"<ImportHHTasks>"+
			"<HHTasks>"+
			getActivities(vecMyActivityDOs)+
			/*getServeys(vecCustomerSurveyDOs)+*/
			"</HHTasks>"+
			"</ImportHHTasks>"+
			"</InsertTaskOrder>"+
			SOAP_FOOTER;;
		return strXML;
	}
	
	public static String getActivities(Vector<MyActivityDO> vecMyActivityDOs)
	{
		String strXML = "";
		if(vecMyActivityDOs != null && vecMyActivityDOs.size() >0)
		{
			for (MyActivityDO myActivityDO : vecMyActivityDOs)
			{
				strXML += "<HHTaskDco>"+
				"<TaskId>"+myActivityDO.taskID+"</TaskId>" +
				"<Status>C</Status>"+
				"<TaskDetail1>"+myActivityDO.serverimagePath+"</TaskDetail1>"+
				"<TaskDetail2></TaskDetail2>"+
				"<Comment>"+myActivityDO.desccription+"</Comment>"+
				"</HHTaskDco>";
			}
		}
		return strXML;
	}
	
	public static String getServeys(Vector<CustomerSurveyDO> vecCustomerSurveyDOs)
	{
		String strXML = "";
		if(vecCustomerSurveyDOs != null && vecCustomerSurveyDOs.size() >0)
		{
			for (CustomerSurveyDO customerSurveyDO : vecCustomerSurveyDOs)
			{
				strXML += "<HHTaskDco>"+
				"<TaskId>"+customerSurveyDO.tskId+"</TaskId>" +
				"<Status>C</Status>"+
				"<TaskDetail1>"+getQuestions()+"</TaskDetail1>"+
				"<TaskDetail2>"+getAnswers(customerSurveyDO)+"</TaskDetail2>"+
				"</HHTaskDco>";
			}
		}
		return strXML;
	}
	
	private static String getQuestions()
	{
		String strQuestion = "1-Which brand of the following beauty products that you are aware of?$2-Promotion or advertisement always influences my intention towards a particular brand?$3-How much do you spend on beauty products a month?$4-Please state 4 beauty brands that you usually use?.";
		return strQuestion;
	}
	
	private static String getAnswers(CustomerSurveyDO customerSurveyDO)
	{
		String strAnswers = "";
		strAnswers += "1-";
		if(customerSurveyDO.Olay)
			strAnswers += "1,";
		if(customerSurveyDO.Pantene)
			strAnswers += "2,";
		if(customerSurveyDO.Lakme)
			strAnswers += "3,";
		if(customerSurveyDO.Elle18)
			strAnswers += "4";
		
		strAnswers += "$2-";
		if(customerSurveyDO.Agree)
			strAnswers += "true";
		else
			strAnswers += "false";
		strAnswers += "$3-"+customerSurveyDO.spent;
		strAnswers += "$4-";
		if(!customerSurveyDO.Brand1.equals(""))
			strAnswers += customerSurveyDO.Brand1;
		if(!customerSurveyDO.Brand2.equals(""))
			strAnswers += customerSurveyDO.Brand2;
		if(!customerSurveyDO.Brand3.equals(""))
			strAnswers += customerSurveyDO.Brand3;
		if(!customerSurveyDO.Brand4.equals(""))
			strAnswers += customerSurveyDO.Brand4;
		
		return strAnswers;
	}
	public static String getAllSurveyMastersWithLastSynch(String strEmpNo,String lsd, String lst) 
	{
		
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
			.append("<GetSurveyMasters  xmlns=\"http://tempuri.org/\">")
			.append("<UserCode>").append(strEmpNo).append("</UserCode>")
			.append("<lsd>").append(lsd).append("</lsd>")
			.append("<lst>").append(lst).append("</lst>")
			.append("</GetSurveyMasters>")
			.append(SOAP_FOOTER) ;
		
		
//		String strXML = SOAP_HEADER + 
//				"<GetSurveyMasters  xmlns=\"http://tempuri.org/\">"+
// 				"<UserCode>"+strEmpNo+"</UserCode>"+
// 				"<lsd>"+lsd+"</lsd>"+
// 				"<lst>"+lst+"</lst>"+
// 				"</GetSurveyMasters>"+
//				SOAP_FOOTER ;
		return strXML.toString();
	}
	
	public static String getAllAckTasks(String SalesmanId, String strSyncTime)
	{
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
			.append("<GetAllAcknowledgedTask  xmlns=\"http://tempuri.org/\">")
			.append("<SalesManCode>").append(SalesmanId).append("</SalesManCode>")
			.append("<LastSyncDate>").append(strSyncTime).append("</LastSyncDate>")
			.append("</GetAllAcknowledgedTask>")
			.append(SOAP_FOOTER) ;
		
//		String strXML =  SOAP_HEADER+
//						"<GetAllAcknowledgedTask xmlns=\"http://tempuri.org/\">"+
//						"<SalesManCode>"+SalesmanId+"</SalesManCode>"+
//						"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
//						"</GetAllAcknowledgedTask>"+
//						SOAP_FOOTER;;
		return strXML.toString();
	}
	public static String getAllCustomersByUserIDWithLastSynch(String strEmpNo,String lsd, String lst, String timeStamp) 
	{
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
			.append("<GetCustomersByUserId  xmlns=\"http://tempuri.org/\">")
			.append("<UserCode>").append(strEmpNo).append("</UserCode>")
			.append("<lsd>").append(lsd).append("</lsd>")
			.append("<lst>").append(lst).append("</lst>")
			.append("</GetCustomersByUserId>")
			.append(SOAP_FOOTER) ;
		
		
//		String strXML = SOAP_HEADER + 
//				 		"<GetCustomersByUserId xmlns=\"http://tempuri.org/\">"+
//						"<UserCode>"+strEmpNo+"</UserCode>"+
//						"<lsd>"+lsd+"</lsd>"+
//						"<lst>"+lst+"</lst>"+
//						"</GetCustomersByUserId>"+
//				SOAP_FOOTER ;
		return strXML.toString();
	}
	
	public static String getAllTrxHeaderForAppWithLastSynch(String strEmpNo,String lsd, String lst,int trxType) 
	{
		
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
			.append("<GetTrxHeaderForApp  xmlns=\"http://tempuri.org/\">")
			.append("<UserCode>").append(strEmpNo).append("</UserCode>")
			.append("<lsd>").append(lsd).append("</lsd>")
			.append("<lst>").append(lst).append("</lst>")
			.append("<TrxType>").append(trxType).append("</TrxType>")
			.append("</GetTrxHeaderForApp>")
			.append(SOAP_FOOTER) ;
		
		
//		String strXML = SOAP_HEADER + 
//				"<GetTrxHeaderForApp xmlns=\"http://tempuri.org/\">"+
// 				"<UserCode>"+strEmpNo+"</UserCode>"+
// 				"<lsd>"+lsd+"</lsd>"+
// 				"<lst>"+lst+"</lst>"+
// 				"<TrxType>"+trxType+"</TrxType>"+
// 				"</GetTrxHeaderForApp>"+
//				SOAP_FOOTER ;
		return strXML.toString();
	}
	public static String getAllJPAndRouteDetailsWithLastSynch(String strEmpNo,String lsd, String lst) 
	{
		
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
			.append("<GetJPAndRouteDetails  xmlns=\"http://tempuri.org/\">")
			.append("<UserCode>").append(strEmpNo).append("</UserCode>")
			.append("<lsd>").append(lsd).append("</lsd>")
			.append("<lst>").append(lst).append("</lst>")
			.append("</GetJPAndRouteDetails>")
			.append(SOAP_FOOTER) ;
		
		
		
//		String strXML = SOAP_HEADER + 
//				"<GetJPAndRouteDetails  xmlns=\"http://tempuri.org/\">"+
// 				"<UserCode>"+strEmpNo+"</UserCode>"+
// 				"<lsd>"+lsd+"</lsd>"+
// 				"<lst>"+lst+"</lst>"+
// 				"</GetJPAndRouteDetails >"+
//				SOAP_FOOTER ;
		
		return strXML.toString();
	}
	
	public static String deleteReceipt(String recept)
	{
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
			.append("<DeleteReceipt  xmlns=\"http://tempuri.org/\">")
			.append("<ReceiptCode>").append(recept).append("</ReceiptCode>")
			.append("</DeleteReceipt>")
			.append(SOAP_FOOTER) ;
		
//		String strXML = SOAP_HEADER+
//				 		"<DeleteReceipt xmlns=\"http://tempuri.org/\">"+
//						"<ReceiptCode>"+recept+"</ReceiptCode>"+
//						"</DeleteReceipt>"+
//						SOAP_FOOTER;
		LogUtils.errorLog("DeleteReceipt",""+strXML);
		
		return strXML.toString();
	}
	
	public static String registerGCMOnServer(String usedId, String gcmId)
	{
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
			.append("<updateDeviceId  xmlns=\"http://tempuri.org/\">")
			.append("<UserCode>").append(usedId).append("</UserCode>")
			.append("<GCMKey>").append(gcmId).append("</GCMKey>")
			.append("</updateDeviceId>")
			.append(SOAP_FOOTER) ;
		
		
//		String strXML = SOAP_HEADER+
//				"<updateDeviceId  xmlns=\"http://tempuri.org/\">"+
//				 "<UserCode>"+usedId+"</UserCode>"+
//				"<GCMKey>"+gcmId+"</GCMKey>"+
//				 "</updateDeviceId>"+
//				SOAP_FOOTER;
//				LogUtils.errorLog("DeleteReceiptDetailsFromApp",""+strXML);
	
		return strXML.toString();
	}
	

	public static String getReturnOrderStatusDetails(String orderNumber)
	{
		 
		
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
			.append("<GetReturnOrderStatusDetails  xmlns=\"http://tempuri.org/\">")
			.append("<OrderNumber>").append(orderNumber).append("</OrderNumber>")
			.append("</GetReturnOrderStatusDetails>")
			.append(SOAP_FOOTER) ;
		
//	   String strXML = 	SOAP_HEADER + 
//			            "<GetReturnOrderStatusDetails xmlns=\"http://tempuri.org/\">" +
//			            "<OrderNumber>"+orderNumber+"</OrderNumber>" +
//				        "</GetReturnOrderStatusDetails>" +
//			            SOAP_FOOTER;
	   return strXML.toString();
	   
	}
	
	
	public static String sendAllStoreCheck(Vector<InventoryDO> vecCompleteInventory)
	{
		
		StringBuffer strXML = new StringBuffer();
		StringBuffer strOrder = new StringBuffer();
		
		strXML.append(SOAP_HEADER)//Old one PostStorecheckDetails
		.append("<PostStoreCheckAndItemDetails xmlns=\"http://tempuri.org/\">")
		.append("<objStoreChecks>");
		
		StringBuffer strBody =new StringBuffer();
		
			for(InventoryDO inventoryDO : vecCompleteInventory)
			{
				StringBuffer strOrderItem = new StringBuffer();
				try {
					strOrder.append("<StoreCheckDco>")
					    .append("<StoreCheckId>0</StoreCheckId>")
					    .append("<UserCode>").append(inventoryDO.createdBy).append("</UserCode>")
					    .append("<JourneyCode>").append(inventoryDO.JourneyCode).append("</JourneyCode>")
					    .append("<VisitCode>").append(inventoryDO.VisitCode).append("</VisitCode>")
					    .append("<Date>").append(inventoryDO.date).append("</Date>" )
					    .append("<Status>").append(inventoryDO.Status).append("</Status>")
//					    .append("<ClientCode>").append(inventoryDO.site).append("</ClientCode>")
					    .append("<ClientCode>").append(URLEncoder.encode(inventoryDO.site,"UTF-8")).append("</ClientCode>")
					    .append("<StoreCheckAppId>").append(inventoryDO.inventoryId).append("</StoreCheckAppId>")
					    .append("<CustomerVisitAppId>").append(inventoryDO.inventoryId).append("</CustomerVisitAppId>" )
					    .append("<TotalCount>").append(inventoryDO.TotalCount).append("</TotalCount>" )
					    .append("<FoodCount>0</FoodCount>" )
					    .append("<NonFoodCount>0</NonFoodCount>" )
					    .append("<SalesmanName>").append(inventoryDO.SalesmanName).append("</SalesmanName>" ) 
					    .append("<UnitManagerCode></UnitManagerCode>" )
					    .append("<UnitManagerName></UnitManagerName>" )
//			        .append("<ClientName>").append(inventoryDO.ClientName).append("</ClientName>" )			       
					    .append("<ClientName>").append(URLEncoder.encode(inventoryDO.ClientName,"UTF-8")).append("</ClientName>" )
					    .append("<ChannelCode>").append(inventoryDO.ChannelCode).append("</ChannelCode>" )
					    .append("<Role>").append(inventoryDO.Role).append("</Role>" )
					    .append("<Region>").append(inventoryDO.Region).append("</Region>" )
					    .append("<RegionCode>").append(inventoryDO.RegionCode).append("</RegionCode>" )
					    .append("<NotApplicableItemCount>").append(inventoryDO.NotApplicableItemCount).append("</NotApplicableItemCount>" )
					    .append("<TotalApplicableCount>").append((inventoryDO.TotalCount - inventoryDO.NotApplicableItemCount)).append("</TotalApplicableCount>" )
					    .append("<TotalStoreCorekuCount>0</TotalStoreCorekuCount>" )
					    .append("<StartTime>").append(inventoryDO.starTime).append("</StartTime>" )
					    .append("<EndTime>").append(inventoryDO.endTime).append("</EndTime>")
					    .append("<StoreCheckItemDcos>");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
				
				for(InventoryDetailDO inventoryDetailDO : inventoryDO.vecInventoryDOs)
				{
					try 
					{
						if((inventoryDetailDO.status == 1) || (inventoryDetailDO.status == 0 /*&& inventoryDetailDO.QTY > 0*/))
//						if((inventoryDetailDO.status == 1) || (inventoryDetailDO.status == 0 && inventoryDetailDO.QTY > 0))
						{
							strOrderItem.append("<StoreCheckItemDco>")
							.append("<ItemCode>").append(inventoryDetailDO.itemCode).append("</ItemCode>")
							.append("<ItemDescription>").append(URLEncoder.encode(inventoryDetailDO.ItemDescription,"UTF-8")).append("</ItemDescription>")
							.append("<CategoryCode>").append(inventoryDetailDO.CategoryCode).append("</CategoryCode>")
							.append("<CategoryName>").append(inventoryDetailDO.CategoryName).append("</CategoryName>")
							.append("<BrandCode>").append(inventoryDetailDO.BrandCode).append("</BrandCode>")
							.append("<BrandName>").append(inventoryDetailDO.BrandName).append("</BrandName>")
							.append("<AgencyCode>").append(inventoryDetailDO.AgencyCode).append("</AgencyCode>")
							.append("<AgencyName>").append(inventoryDetailDO.AgencyName).append("</AgencyName>")
							.append("<Status>").append(inventoryDetailDO.status).append("</Status>")
							.append("<StoreCheckAppId>").append(inventoryDO.inventoryId).append("</StoreCheckAppId>")
							.append("<StoreCheckItemAppId>").append(inventoryDO.inventoryId).append("</StoreCheckItemAppId>")
							.append("<UOM>").append(inventoryDetailDO.UOM).append("</UOM>")
							.append("<Quantity>").append(inventoryDetailDO.QTY).append("</Quantity>")
							.append("</StoreCheckItemDco>");
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				
				strOrder.append(strOrderItem)
				.append("</StoreCheckItemDcos>")
				.append("<StoreCheckGroupDcos>");
				
				StringBuffer strGroupItem = new StringBuffer();
				
				for(InventoryGroupDO inventoryGroupDO : inventoryDO.vecGroupDOs)
				{
					strGroupItem.append("<StoreCheckGroupDco>")
		            .append("<StoreCheckAppId>").append(inventoryGroupDO.StoreCheckId).append("</StoreCheckAppId>")
		            .append("<StorecheckGroupAppId>").append(inventoryGroupDO.StoreCheckId).append("</StorecheckGroupAppId>")
		            .append("<StorecheckGroupId>0</StorecheckGroupId>")
		            .append("<StoreCheckId>0</StoreCheckId>")
		            .append("<ItemGroupLevel>1</ItemGroupLevel>")
		            .append("<ItemGroupCode>").append(inventoryGroupDO.ItemGroupCode).append("</ItemGroupCode>")
		            .append("<ItemGroupLevelName>").append(inventoryGroupDO.ItemGroupLevelName).append("</ItemGroupLevelName>")
		            .append("<TotalCount>").append(inventoryGroupDO.TotalCount).append("</TotalCount>")
		            .append("<TotalNotAvailableCount>").append(inventoryGroupDO.TotalNotAvailableCount).append("</TotalNotAvailableCount>")
		            .append("</StoreCheckGroupDco>");
				}
				
				strOrder.append(strGroupItem)
				.append("</StoreCheckGroupDcos>")
						.append("</StoreCheckDco>");
			}
			strBody.append(strOrder)
			.append("</objStoreChecks>");
			
			strXML.append(strBody)
			.append("</PostStoreCheckAndItemDetails>")
			.append(SOAP_FOOTER) ;
			
		System.out.println(strXML.toString());
		LogUtils.debug("strXML", strXML.toString());
		try
		{
			ConnectionHelper.writeIntoLog(strXML.toString(), "storecheck");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	    return strXML.toString();
	}
	
	public static String uploadLoadRequests(ArrayList<LoadRequestDO> vecLoad)
	{
		StringBuffer strXML = new StringBuffer();
		StringBuffer strOrder = new StringBuffer();
		StringBuffer strBody = new StringBuffer();
		
		strXML.append(SOAP_HEADER)
		.append("<PostMovementDetailsFromXML xmlns=\"http://tempuri.org/\">");
		
		strBody.append("<objMovementHeaderDcos>");
	  		for(LoadRequestDO orderDO : vecLoad)
	  		{
		  		strOrder.append("<MovementHeaderDco>")
		  		.append("<MovementCode>").append(orderDO.MovementCode).append("</MovementCode>")
		        .append("<PreMovementCode>").append(orderDO.PreMovementCode).append("</PreMovementCode>")
		        .append("<AppMovementId>").append(orderDO.AppMovementId).append("</AppMovementId>")
		        .append("<OrgCode>").append(orderDO.OrgCode).append("</OrgCode>")
		        .append("<UserCode>").append(orderDO.UserCode).append("</UserCode>")
		        .append("<WHKeeperCode>").append(orderDO.UserCode).append("</WHKeeperCode>")
		        .append("<CurrencyCode>").append(orderDO.CurrencyCode).append("</CurrencyCode>")
		        .append("<JourneyCode>").append(orderDO.JourneyCode).append("</JourneyCode>")
		        .append("<MovementDate>").append(orderDO.MovementDate).append("</MovementDate>")
		        .append("<MovementNote>").append(orderDO.MovementNote).append("</MovementNote>")
		        .append("<MovementType>").append(orderDO.MovementType).append("</MovementType>")
		        .append("<SourceVehicleCode>").append(orderDO.SourceVehicleCode).append("</SourceVehicleCode>")
		        .append("<DestinationVehicleCode>").append(orderDO.DestinationVehicleCode).append("</DestinationVehicleCode>")
		        .append("<VisitID>").append(orderDO.VisitID).append("</VisitID>")
		        .append("<CreatedOn>").append(orderDO.CreatedOn).append("</CreatedOn>")
		        .append("<Amount>").append(orderDO.Amount).append("</Amount>" )
		        .append("<WHCode>").append(orderDO.WHCode).append("</WHCode>")
		        .append("<_MovementStatus>").append(orderDO.MovementStatus).append("</_MovementStatus>")
		        .append("<MovementStatus>").append(orderDO.MovementStatus).append("</MovementStatus>")
		        .append("<ProductType>").append(orderDO.ProductType).append("</ProductType>")
		        .append("<Division>").append(orderDO.Division).append("</Division>")
		        .append(getLoadDetail(orderDO.vecItems, orderDO))
		        .append("</MovementHeaderDco>");
	  		}
			strBody.append(strOrder);
			strXML .append(strBody)
				  .append("</objMovementHeaderDcos>")
				  .append("</PostMovementDetailsFromXML>")
				  .append(SOAP_FOOTER) ;
	      					
//	    LogUtils.errorLog("strXML", ""+strXML);
	    System.out.println(strXML.toString());
	    return strXML.toString();
	}
	private static String getLoadDetail(ArrayList<LoadRequestDetailDO> vecItems, LoadRequestDO loadRequestDO)
	{
		StringBuffer strXML = new StringBuffer();
		strXML.append("<MovementDetailDcos>");
		if(vecItems != null && vecItems.size() >0)
		{
			for (LoadRequestDetailDO productDO : vecItems)
			{
		        
				try {
					strXML.append("<MovementDetailDco>")
						.append("<LineNo>").append(productDO.LineNo).append("</LineNo>")
						.append("<MovementCode>").append(productDO.MovementCode).append("</MovementCode>")
						.append("<ItemCode>").append(productDO.ItemCode).append("</ItemCode>")
						.append("<OrgCode>").append(productDO.OrgCode).append("</OrgCode>")
						.append("<ItemDescription>").append(URLEncoder.encode(productDO.ItemDescription,"UTF-8")).append("</ItemDescription>")
						.append("<ItemAltDescription>").append(URLEncoder.encode(productDO.ItemAltDescription,"UTF-8")).append("</ItemAltDescription>")
						.append("<UOM>").append(productDO.UOM).append("</UOM>")
						.append("<QuantityLevel1>").append(productDO.QuantityLevel1).append("</QuantityLevel1>")
						.append("<QuantityLevel2>").append(productDO.QuantityLevel2).append("</QuantityLevel2>")
						.append("<QuantityLevel3>").append(productDO.QuantityLevel3).append("</QuantityLevel3>")
						.append("<InProcessQuantityLevel1>").append(productDO.inProcessQuantityLevel1).append("</InProcessQuantityLevel1>")
						.append("<InProcessQuantityLevel2>").append(productDO.inProcessQuantityLevel2).append("</InProcessQuantityLevel2>")
						.append("<InProcessQuantityLevel3>").append(productDO.inProcessQuantityLevel3).append("</InProcessQuantityLevel3>")
						.append("<ShippedQuantityLevel1>").append(productDO.shippedQuantityLevel1).append("</ShippedQuantityLevel1>")
						.append("<ShippedQuantityLevel2>").append(productDO.shippedQuantityLevel2).append("</ShippedQuantityLevel2>")
						.append("<ShippedQuantityLevel3>").append(productDO.shippedQuantityLevel3).append("</ShippedQuantityLevel3>")
						
						.append("<NonSellableQty>").append(productDO.NonSellableQty).append("</NonSellableQty>")
						.append("<QuantityBU>").append(productDO.QuantityBU).append("</QuantityBU>")
						.append("<CurrencyCode>").append(productDO.CurrencyCode).append("</CurrencyCode>")
						.append("<PriceLevel1>").append(productDO.PriceLevel1).append("</PriceLevel1>")
						.append("<PriceLevel2>").append(productDO.PriceLevel2).append("</PriceLevel2>")
						.append("<PriceLevel3>").append(productDO.PriceLevel3).append("</PriceLevel3>")
						.append("<MovementReasonCode>").append(productDO.MovementReasonCode).append("</MovementReasonCode>")
						.append("<ExpiryDate>").append(productDO.ExpiryDate).append("</ExpiryDate>")
						.append("<CreatedOn>").append(productDO.CreatedOn).append("</CreatedOn>")
						.append("<MovementType>").append(loadRequestDO.MovementType).append("</MovementType>")
						.append("<CancelledQuantity>").append(productDO.CancelledQuantity).append("</CancelledQuantity>")
						.append("<InProcessQuantity>").append(productDO.InProcessQuantity).append("</InProcessQuantity>")
						.append("<ShippedQuantity>").append(productDO.ShippedQuantity).append("</ShippedQuantity>")
						.append("</MovementDetailDco>");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
		strXML.append("</MovementDetailDcos>");
		return strXML.toString();
	}
	
	

//	public static String sendAllStoreCheck(Vector<InventoryDO> vecCompleteInventory)
//	{
//		String strOrder  = "", strOrderItem = ""; 
//		String strGroup  = "", strGroupItem = ""; 
//		String strXML = SOAP_HEADER + "<PostStorecheckDetails xmlns=\"http://tempuri.org/\">"+
//									  "<objStoreChecks>";
//		
//		String strBody =		  "" ;
//									for(InventoryDO inventoryDO : vecCompleteInventory)
//									{
//										strOrderItem ="";
//										strOrder  = strOrder+ "<StoreCheckDco>"+
//								          "<StoreCheckId>0</StoreCheckId>" +
//								          "<UserCode>"+inventoryDO.createdBy+"</UserCode>"+
//								          "<JourneyCode>"+inventoryDO.JourneyCode+"</JourneyCode>"+
//								          "<VisitCode>"+inventoryDO.VisitCode+"</VisitCode>"+
//								          "<Date>"+inventoryDO.date+"</Date>" +
//								          "<Status>"+inventoryDO.Status+"</Status>"+
//								          "<ClientCode>"+inventoryDO.site+"</ClientCode>"+
//								          "<StoreCheckAppId>"+inventoryDO.inventoryId+"</StoreCheckAppId>"+
//								          "<CustomerVisitAppId>"+inventoryDO.inventoryId+"</CustomerVisitAppId>" +
//								          "<TotalCount>"+inventoryDO.TotalCount+"</TotalCount>" +
//								          "<FoodCount>0</FoodCount>" +
//								          "<NonFoodCount>0</NonFoodCount>" +
//								          "<SalesmanName>"+inventoryDO.SalesmanName+"</SalesmanName>" +
//								          "<UnitManagerCode></UnitManagerCode>" +
//								          "<UnitManagerName></UnitManagerName>" +
//								          "<ClientName>"+inventoryDO.ClientName+"</ClientName>" +
//								          "<ChannelCode>"+inventoryDO.ChannelCode+"</ChannelCode>" +
//								          "<Role>"+inventoryDO.Role+"</Role>" +
//								          "<Region>"+inventoryDO.Region+"</Region>" +
//								          "<RegionCode>"+inventoryDO.RegionCode+"</RegionCode>" +
//								          "<NotApplicableItemCount>"+inventoryDO.NotApplicableItemCount+"</NotApplicableItemCount>" +
//								          "<TotalApplicableCount>"+(inventoryDO.TotalCount - inventoryDO.NotApplicableItemCount)+"</TotalApplicableCount>" +
//								          "<TotalStoreCorekuCount>0</TotalStoreCorekuCount>" +
//								          "<StartTime>"+inventoryDO.date+"</StartTime>" +
//								          "<EndTime>"+inventoryDO.date+"</EndTime>"+
//								          "<StoreCheckItemDcos>"; 
//										
//										for(InventoryDetailDO inventoryDetailDO : inventoryDO.vecInventoryDOs)
//										{
//											try 
//											{
//												strOrderItem = strOrderItem+
//												  "<StoreCheckItemDco>"+
//												  "<ItemCode>"+inventoryDetailDO.itemCode+"</ItemCode>"+
//												  "<ItemDescription>"+URLEncoder.encode(inventoryDetailDO.ItemDescription,"UTF-8")+"</ItemDescription>"+
//												  "<CategoryCode>"+inventoryDetailDO.CategoryCode+"</CategoryCode>"+
//												  "<CategoryName>"+inventoryDetailDO.CategoryName+"</CategoryName>"+
//												  "<BrandCode>"+inventoryDetailDO.BrandCode+"</BrandCode>"+
//												  "<BrandName>"+inventoryDetailDO.BrandName+"</BrandName>"+
//												  "<AgencyCode>"+inventoryDetailDO.AgencyCode+"</AgencyCode>"+
//												  "<AgencyName>"+inventoryDetailDO.AgencyName+"</AgencyName>"+
//												  "<Status>"+inventoryDetailDO.status+"</Status>"+
//												  "<StoreCheckAppId>"+inventoryDO.inventoryId+"</StoreCheckAppId>"+
//												  "<StoreCheckItemAppId>"+inventoryDO.inventoryId+"</StoreCheckItemAppId>"+
//												  "</StoreCheckItemDco>";
//											} catch (UnsupportedEncodingException e) {
//												e.printStackTrace();
//											}
//										}
//										strOrder = strOrder+strOrderItem+
//										"</StoreCheckItemDcos>" +
//												
//										"<StoreCheckGroupDcos>";
//										for(InventoryGroupDO inventoryGroupDO : inventoryDO.vecGroupDOs)
//										{
//											strGroupItem = strGroupItem+
//								              "<StoreCheckGroupDco>" +
//								              "<StoreCheckAppId>"+inventoryGroupDO.StoreCheckId+"</StoreCheckAppId>" +
//								              "<StorecheckGroupAppId>"+inventoryGroupDO.StoreCheckId+"</StorecheckGroupAppId>"+
//								              "<StorecheckGroupId>0</StorecheckGroupId>"+
//								              "<StoreCheckId>0</StoreCheckId>"+
//								              "<ItemGroupLevel>1</ItemGroupLevel>"+
//								              "<ItemGroupCode>"+inventoryGroupDO.ItemGroupCode+"</ItemGroupCode>"+
//								              "<ItemGroupLevelName>"+inventoryGroupDO.ItemGroupLevelName+"</ItemGroupLevelName>"+
//								              "<TotalCount>"+inventoryGroupDO.TotalCount+"</TotalCount>"+
//								              "<TotalNotAvailableCount>"+inventoryGroupDO.TotalNotAvailableCount+"</TotalNotAvailableCount>"+
//								              "</StoreCheckGroupDco>";
//										}
//										strOrder = strOrder+strGroupItem+
//										"</StoreCheckGroupDcos>" +
//										"</StoreCheckDco>";
//									}
//									strBody = strBody+strOrder+"</objStoreChecks>";
//									
//									strXML = strXML+strBody+
//									"</PostStorecheckDetails>"+
//				SOAP_FOOTER ;
//			LogUtils.errorLog("strXML", strXML);
//			
////			try
////			{
////				InputStream is = new ByteArrayInputStream(strXML.getBytes());
////				ConnectionHelper.convertResponseToFile(is, "PostStorecheckDetails");
////			}
////			catch(Exception e)
////			{
////				e.printStackTrace();
////			}
//			
//	    return strXML;
//	}
	
	//03-11-2014 venkat for data syncing
	
	public static String getAllDataSync(String userCode,int lastDate,int lastTime)
	{
		StringBuilder strXML = new StringBuilder();
				strXML.append(SOAP_HEADER)
					.append("<GetAllDataSync  xmlns=\"http://tempuri.org/\">")
					.append("<UserCode>").append(userCode).append("</UserCode>")
					.append("<lsd>").append(lastDate).append("</lsd>")
					.append("<lst>").append(lastTime).append("</lst>")
					/******************Temp purpose*****************************/
//					.append("<lsd>").append("116067").append("</lsd>")
//					.append("<lst>").append("94216").append("</lst>")
					/******************Temp purpose*****************************/
					.append("</GetAllDataSync>")
					.append(SOAP_FOOTER) ;
//		return strXML.toString();
		
//		String strXML = SOAP_HEADER+
//				 		"<GetAllDataSync xmlns=\"http://tempuri.org/\">"+
//						"<UserCode>"+userCode+"</UserCode>"+
//						"<lsd>"+lastDate+"</lsd>"+
//						"<lst>"+lastTime+"</lst>"+
//						"</GetAllDataSync>"+
//						SOAP_FOOTER;
		LogUtils.errorLog("GetAllDataSync",""+strXML);
		return strXML.toString();
	}

	public static String postCompetitor(Vector<CompetitorItemDO> vecCompetitorItemDO) 
	{
		StringBuilder sb = new StringBuilder();
		sb.append(SOAP_HEADER)
		.append("<InsertCompetitor xmlns=\"http://tempuri.org/\">")
		.append("<objCompetitorDcos>")
		.append(getcompetitorXml(vecCompetitorItemDO))
		.append("</objCompetitorDcos>")
		.append("</InsertCompetitor>")
		.append(SOAP_FOOTER);
		
		return sb.toString();
	}
	
	private static String getcompetitorXml(Vector<CompetitorItemDO> vecCompetitorItemDO)
	{
		StringBuffer buffer = new StringBuffer();
	
		for(CompetitorItemDO competitorItemDO : vecCompetitorItemDO)
		{
			buffer.append("<CompetitorDco>")
				  .append("<CompetetorId>").append(0).append("</CompetetorId>")
				  .append("<CompetetorCode>").append("test").append("</CompetetorCode>")
				  .append("<CategoryCode>").append(competitorItemDO.category).append("</CategoryCode>")
				  .append("<OurBrand>").append(competitorItemDO.ourBrand).append("</OurBrand>")
				  .append("<BrandName>").append(competitorItemDO.brandname).append("</BrandName>")
				  .append("<Company>").append(competitorItemDO.company).append("</Company>")
				  .append("<ImagePath>").append(competitorItemDO.imageUploadpath).append("</ImagePath>")
				  .append("<Store>").append(competitorItemDO.store).append("</Store>")
				  .append("<Price>").append(competitorItemDO.price).append("</Price>")
				  .append("<Activity>").append(competitorItemDO.activity).append("</Activity>")
				  .append("<TypeOfPromotion>").append(competitorItemDO.promotiontype).append("</TypeOfPromotion>")
				  .append("<CreatedOn>").append(competitorItemDO.createdon).append("</CreatedOn>")
				  .append("</CompetitorDco>");
		}
		return buffer.toString();
	}

	public static String postInitiatives(String brand,String customerCode,InitiativeTradePlanImageDO initiativeDO) 
	{
		StringBuilder sb = new StringBuilder();
		sb.append(SOAP_HEADER)
		.append("<InsertInitiative xmlns=\"http://tempuri.org/\">")
		.append("<objInitiativeDco>")
		.append("<InitiativeId>").append(initiativeDO.InitiativeTradePlanId).append("</InitiativeId>")
		.append("<Title>").append(initiativeDO.Type).append("</Title>")
		.append("<Guidline>").append(initiativeDO.Reason).append("</Guidline>")
		.append("<Brand>").append(brand).append("</Brand>")
		.append("<BrandName>").append(brand).append("</BrandName>")
		.append("<BrandCode>").append(brand).append("</BrandCode>")
		.append("<CustomerCode>").append(customerCode).append("</CustomerCode>")
		.append("<StartDate>").append(initiativeDO.ImplementedOn).append("</StartDate>")
		.append("<Image>").append(initiativeDO.ExecutionUploadPicture).append("</Image>")
		.append("<Status>").append(initiativeDO.ExecutionStatus).append("</Status>")
		.append("<Month>").append(initiativeDO.VerifiedOn).append("</Month>")
		.append("<InitiativeMonth>").append(initiativeDO.VerifiedOn).append("</InitiativeMonth>")
		.append("<InitiativeYear>").append(initiativeDO.VerifiedOn).append("</InitiativeYear>")
		.append("<Planogram>").append(initiativeDO.ExecutionUploadPicture).append("</Planogram>")
		.append("<POSM>").append(0).append("</POSM>")
		.append("<ModifiedDate>").append(initiativeDO.ModifiedDate).append("</ModifiedDate>")
		.append("<ModifiedTime>").append(initiativeDO.ModifiedTime).append("</ModifiedTime>")
		.append("<IsChannelSpecified>").append("").append("</IsChannelSpecified>")
		.append("<ChannelCodes>").append(0).append("</ChannelCodes>")
		.append("<ModifiedBy>").append(initiativeDO.VerifiedBy).append("</ModifiedBy>")
		.append("<ModifiedOn>").append(initiativeDO.VerifiedOn).append("</ModifiedOn>")
		.append("<ExecutionStatus>").append(initiativeDO.ExecutionStatus).append("</ExecutionStatus>")
		.append("<ImplementedBy>").append(initiativeDO.ImplementedBy).append("</ImplementedBy>")
		.append("<ImplementedOn>").append(CalendarUtils.getCurrentDate().split("T")[0]).append("</ImplementedOn>")
		.append("<VerifiedBy>").append(initiativeDO.VerifiedBy).append("</VerifiedBy>")
		.append("<VerifiedOn>").append(CalendarUtils.getCurrentDate().split("T")[0]).append("</VerifiedOn>")
		.append("</objInitiativeDco>")
		.append("</InsertInitiative>")
		.append(SOAP_FOOTER);
		
		return sb.toString();
	}
	//need to remove
	private static String getAssetServiceRequestXml(AssetServiceDO assetserDo) 
	{
		
		StringBuffer buffer = new StringBuffer();
		
		
			
			buffer
			 	  .append("<AssetServiceRequestDco>")
			 	  .append("<AssetServiceRequestId>").append(assetserDo.assetServiceRequestId).append("</AssetServiceRequestId>")
			 	  .append("<UserCode>").append(assetserDo.userCode).append("</UserCode>")
			      .append("<SiteNo>").append(assetserDo.siteNo).append("</SiteNo>")
			      .append("<RequestDate>").append(assetserDo.requestDate).append("</RequestDate>")
			      .append("<RequestImage>").append(assetserDo.requestImage).append("</RequestImage>")
			      .append("<JourneyCode>").append(assetserDo.journeyCode).append("</JourneyCode>")
			      .append("<VisitCode>").append(assetserDo.visitCode).append("</VisitCode>")
			      .append("<Status>").append(assetserDo.status).append("</Status>")
			      .append("<Notes>").append(assetserDo.notes).append("</Notes>")
				  .append("<IsApproved>").append(assetserDo.isApproved).append("</IsApproved>")
				  .append("<ServiceRequestAppId>").append(assetserDo.serviceRequestAppId).append("</ServiceRequestAppId>")
				  .append("</AssetServiceRequestDco>");
			      
		return buffer.toString();
	}
	//need to remove
	public static String PostAssetServiceRequest (AssetServiceDO assetserDo)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(SOAP_HEADER)
			  .append("<PostAssetServiceRequest xmlns=\"http://tempuri.org/\">")
			  .append("<objAssetServiceRequestDcos>")
			  .append(getAssetServiceRequestXml(assetserDo))
			  .append("</objAssetServiceRequestDcos>")
			  .append("</PostAssetServiceRequest>")
			  .append(SOAP_FOOTER);
			
			return buffer.toString();
	}
	//need to remove
	public static String postAssetsRequest(String siteNo, String assetId, String dateTime, int quantity)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(SOAP_HEADER)
		      .append("<InsertAssetCustomer xmlns=\"http://tempuri.org/\">")
		      .append("<SiteNo>").append(siteNo).append("</SiteNo>")
		      .append("<AssetId>").append(assetId).append("</AssetId>")
		      .append("<InstalledOn>").append(dateTime).append("</InstalledOn>")
		       .append("<Quantity>").append(quantity).append("</Quantity>")
		      .append("</InsertAssetCustomer >")
		      .append(SOAP_FOOTER);
		
		
		LogUtils.errorLog("InsertAssetCustomer ", buffer.toString());
		return buffer.toString();
		
	}
	public static String initiativeImage(String brand,String customerCode,InitiativeTradePlanImageDO planogramDO) 
	{
		StringBuilder sb = new StringBuilder();
		sb.append(SOAP_HEADER)
		.append("<InsertInitiative xmlns=\"http://tempuri.org/\">")
		.append("<objInitiativeDTO>")
		.append("<InitiativeId>").append(planogramDO.InitiativeTradePlanId).append("</InitiativeId>")
		.append("<BrandCode>").append(brand).append("</BrandCode>")
		.append("<CustomerCode>").append(customerCode).append("</CustomerCode>")
		.append("<Planogram>").append(planogramDO.ExecutionUploadPicture).append("</Planogram>")
		.append("<ExecutionStatus>").append(1).append("</ExecutionStatus>")
		.append("<ImplementedBy>").append(planogramDO.ImplementedBy).append("</ImplementedBy>")
		.append("<ImplementedOn>").append(CalendarUtils.getTodaydate().split("T")[0]).append("</ImplementedOn>")
		.append("<VerifiedBy>").append("").append("</VerifiedBy>")
		.append("<VerifiedOn>").append(CalendarUtils.getTodaydate().split("T")[0]).append("</VerifiedOn>")
		.append("<CreatedBy>").append(CalendarUtils.getTodaydate().split("T")[0]).append("</CreatedBy>")
		.append("</objInitiativeDTO>")
		.append("</InsertInitiative>")
		.append(SOAP_FOOTER);
		
		return sb.toString();
	}

    
	//Survey -- module
	public static String getSurveyList(String userId)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("UserId=").append(userId);
		return sb.toString();
	}
	public static String getSurveyQuestions(String surveyId)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("SurveyId=").append(surveyId);
		return sb.toString();
	}
	//Onlie Submission
	public static String uploadSurvey(String  surveyId,Vector<SurveyQuestionNewDO> vecQuestions,String customerCode,String userId)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(saveQuestions(vecQuestions))
		.append("&SurveyId=").append(surveyId)
		.append("&UserId=").append(userId)
		.append("&IsActive=").append("1")
		.append("&CreatedBy=").append(userId)
		.append("&FirstName=").append(customerCode)
		.append("&LastName=").append("")
		.append("&PNR=").append("")
		.append("&From=").append("")
		.append("&To=").append("")
		.append("&Ex1=").append("")
		.append("&Remarks=").append("");
		return builder.toString();
			   
	}
	public static String saveQuestions(Vector<SurveyQuestionNewDO> vecQuestions)
	{
		String strNutritionParameter = "";
		
		int count = 0;
		for (int i = 0; i < vecQuestions.size(); i++)
		{
			if(vecQuestions.get(i).AnswerType.equalsIgnoreCase("CHECKBOX"))
			{
				if(vecQuestions.get(i).vecOptions!=null && vecQuestions.get(i).vecOptions.size()>0)
				{
					for(OptionsDO options:vecQuestions.get(i).vecOptions)
					{
						if(options.isChecked)
						{
							
							strNutritionParameter = strNutritionParameter+"&userSurveyAnswerDetails["+(i+count)+"].SurveyQuestionId="+vecQuestions.get(i).SurveyQuestionId
													+"&userSurveyAnswerDetails["+(i+count)+"].SurveyOptionId="+options.SurveyQuestionOptionId
													+"&userSurveyAnswerDetails["+(i+count)+"].AnswerTypeCode="+vecQuestions.get(i).AnswerType
													+"&userSurveyAnswerDetails["+(i+count)+"].SurveyAnswer="+options.SurveyQuestionOptionId;
							
							LogUtils.errorLog("CheckBoxSelectedFor", options.OptionName);
							count++;
						}
					}
					if(count>0)
						count--;
				}
			}
			else if(vecQuestions.get(i).AnswerType.equalsIgnoreCase("DROPDOWN"))
			{
				
				strNutritionParameter = strNutritionParameter+"&userSurveyAnswerDetails["+(i+count)+"].SurveyQuestionId="+vecQuestions.get(i).SurveyQuestionId
										+"&userSurveyAnswerDetails["+(i+count)+"].SurveyOptionId="+vecQuestions.get(i).SurveyQuestionOptionId
										+"&userSurveyAnswerDetails["+(i+count)+"].AnswerTypeCode="+vecQuestions.get(i).AnswerType
										+"&userSurveyAnswerDetails["+(i+count)+"].SurveyAnswer="+vecQuestions.get(i).SurveyQuestionOptionId;
			}
			else
			{
				
				strNutritionParameter = strNutritionParameter+"&userSurveyAnswerDetails["+(i+count)+"].SurveyQuestionId="+vecQuestions.get(i).SurveyQuestionId
										+"&userSurveyAnswerDetails["+(i+count)+"].SurveyOptionId="+vecQuestions.get(i).SurveyQuestionOptionId
										+"&userSurveyAnswerDetails["+(i+count)+"].AnswerTypeCode="+vecQuestions.get(i).AnswerType
										+"&userSurveyAnswerDetails["+(i+count)+"].SurveyAnswer="+vecQuestions.get(i).Answer;
			}
		}
		strNutritionParameter = strNutritionParameter.replaceFirst("&", "");
		return strNutritionParameter;
	}
	
//	OfflineSubmission
	public static String uploadOfflineSurvey(String  surveyId,Vector<SurveyQuestionNewDO> vecQuestions,String customerCode,String userId)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(sendOfflineData(vecQuestions))
		.append("&SurveyId=").append(surveyId)
		.append("&UserId=").append(userId)
		.append("&IsActive=").append("1")
		.append("&CreatedBy=").append(userId)
		.append("&FirstName=").append(customerCode)
		.append("&LastName=").append("")
		.append("&PNR=").append("")
		.append("&From=").append("")
		.append("&To=").append("")
		.append("&Ex1=").append("")
		.append("&Remarks=").append("");
		return builder.toString();
			   
	}
	public static String sendOfflineData(Vector<SurveyQuestionNewDO> vecQuestions)
	{
		String strNutritionParameter = "";
		
		int count = 0;
		for (int i = 0; i < vecQuestions.size(); i++)
		{
			if(vecQuestions.get(i).AnswerType.equalsIgnoreCase("CHECKBOX"))
			{
				if(vecQuestions.get(i).vecUserSurveyAnswers!=null && vecQuestions.get(i).vecUserSurveyAnswers.size()>0)
				{
					for(UserSurveyAnswerDO userSurveyAnswerDO:vecQuestions.get(i).vecUserSurveyAnswers)
					{
							
							strNutritionParameter = strNutritionParameter+"&userSurveyAnswerDetails["+(i+count)+"].SurveyQuestionId="+vecQuestions.get(i).SurveyQuestionId
													+"&userSurveyAnswerDetails["+(i+count)+"].SurveyOptionId="+userSurveyAnswerDO.SurveyOptionId
													+"&userSurveyAnswerDetails["+(i+count)+"].AnswerTypeCode="+vecQuestions.get(i).AnswerType
													+"&userSurveyAnswerDetails["+(i+count)+"].SurveyAnswer="+userSurveyAnswerDO.SurveyOptionId;
							
							count++;
					}
					if(count>0)
						count--;
				}
			}
			else
			{
				if(vecQuestions.get(i).vecUserSurveyAnswers!=null && vecQuestions.get(i).vecUserSurveyAnswers.size()>0)
				{
					for(UserSurveyAnswerDO userSurveyAnswerDO:vecQuestions.get(i).vecUserSurveyAnswers)
					{
				
				strNutritionParameter = strNutritionParameter+"&userSurveyAnswerDetails["+(i+count)+"].SurveyQuestionId="+vecQuestions.get(i).SurveyQuestionId
										+"&userSurveyAnswerDetails["+(i+count)+"].SurveyOptionId="+userSurveyAnswerDO.SurveyOptionId
										+"&userSurveyAnswerDetails["+(i+count)+"].AnswerTypeCode="+vecQuestions.get(i).AnswerType
										+"&userSurveyAnswerDetails["+(i+count)+"].SurveyAnswer="+userSurveyAnswerDO.Answer;
					}
				}
			}
		}
		strNutritionParameter = strNutritionParameter.replaceFirst("&", "");
		return strNutritionParameter;
	}
	public static String getUserSurveyAnswerByUserId(String userId,String surveyId)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("UserId=").append(userId)
		.append("&SurveyId=").append(surveyId)
		.append("&response_format=").append("json");
		return builder.toString();
			   
	}
	public static String getSurveyDetailsByUserId(String userId,String UserSurveyAnswerId)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("UserId=").append(userId)
		.append("&UserSurveyAnswerId=").append(UserSurveyAnswerId)
		.append("&response_format=").append("json");
		return builder.toString();
			   
	}
	public static String getSurveyListByUserIdBySync(String userId,int LSD,int LST)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("UserId=").append(userId)
		.append("&LSD=").append(LSD)
		.append("&LST=").append(LST);
		return builder.toString();
			   
	}
	
	/**
	 * @return
	 */
	public static String getAllSellingSKU() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append(SOAP_HEADER)
		.append("<GetAllSellingSkusClassifications xmlns=\"http://tempuri.org/\" />")
		.append(SOAP_FOOTER);
		
		return sb.toString();
	}
	
	public static String updateCustomerDetails(Vector<CustomerDao> vecCustomerDaO)
	{
		StringBuilder sb = new StringBuilder(),sb2,sb3;
		sb.append(SOAP_HEADER)
		.append("<UpdateCustomerDetails xmlns=\"http://tempuri.org/\">")
		.append("<objCustomerDcos>");
		
		sb2 = new StringBuilder();
		for (int i = 0; i < vecCustomerDaO.size(); i++) 
		{
			sb2.append("<CustomerDco>")
			.append("<CustomerSiteId>").append(vecCustomerDaO.get(i).site).append("</CustomerSiteId>")
			.append("<ContactPersonName>").append(vecCustomerDaO.get(i).ContactPersonName).append("</ContactPersonName>")
			.append("<Address1>").append(vecCustomerDaO.get(i).Address1).append("</Address1>")
			.append("<Address2>").append(vecCustomerDaO.get(i).Address2).append("</Address2>")
			.append("<Latitude>").append(vecCustomerDaO.get(i).GeoCodeX).append("</Latitude>")
			.append("<Longitude>").append(vecCustomerDaO.get(i).GeoCodeY).append("</Longitude>")
			.append("<MOBILENO1>").append(vecCustomerDaO.get(i).MobileNumber1).append("</MOBILENO1>")
			.append("<Attribute10>").append(vecCustomerDaO.get(i).Attribute10).append("</Attribute10>");
			
			sb3 = new StringBuilder();
			sb3.append("<objCustomerSpecialDays>");
			for (int j = 0; j < vecCustomerDaO.get(i).vecCustomerSplDay.size(); j++) 
			{
				sb3.append("<CustomerSpecialDayDco>")
				.append("<CustomerCode>").append(vecCustomerDaO.get(i).vecCustomerSplDay.get(j).customerCode).append("</CustomerCode>")
				.append("<Day>").append(vecCustomerDaO.get(i).vecCustomerSplDay.get(j).specialDay).append("</Day>")
				.append("<Description>").append(vecCustomerDaO.get(i).vecCustomerSplDay.get(j).description).append("</Description>")
				.append("</CustomerSpecialDayDco>");
			}
			sb3.append("</objCustomerSpecialDays>");

			sb2.append(sb3)
			.append("</CustomerDco>");
		}
		sb.append(sb2)
		.append("</objCustomerDcos>")
		.append("</UpdateCustomerDetails>")
		.append(SOAP_FOOTER);
		Log.d("updateCustomerDetailsXML", sb.toString());
		return sb.toString();
	}
	public static String updateCustomerGeoDetails (Vector<CustomerDao> vecCustomerDaO,String usercode,String reason)
	{
//		<objCustomerDcos>
// 				<CustomerDco>
//					<CustomerSiteId>DXA012</CustomerSiteId>
//					<Latitude>17.456112</Latitude>
//					<Longitude>78.3679566</Longitude>
//					<UserId>DXB1</UserId>
//				</CustomerDco>
		StringBuilder sb = new StringBuilder(),sb2,sb3;
		sb.append(SOAP_HEADER)
		.append("<UpdateCustomerGeoDetails xmlns=\"http://tempuri.org/\">")
		.append("<objCustomerDcos>");

		sb2 = new StringBuilder();
		for (int i = 0; i < vecCustomerDaO.size(); i++)
		{

			sb2	.append("<CustomerDco>")
			.append("<CustomerSiteId>").append(vecCustomerDaO.get(i).site).append("</CustomerSiteId>")
			.append("<UserId>").append(usercode).append("</UserId>")
			.append("<Latitude>").append(vecCustomerDaO.get(i).GeoCodeX).append("</Latitude>")
			.append("<Longitude>").append(vecCustomerDaO.get(i).GeoCodeY).append("</Longitude>")
			.append("<Reason>").append(reason).append("</Reason>")
					.append("</CustomerDco>");;



		}
		sb.append(sb2)
		.append("</objCustomerDcos>")
		.append("</UpdateCustomerGeoDetails>")
		.append(SOAP_FOOTER);
		Log.d("updateCustomerGeoDetailsXML", sb.toString());
		return sb.toString();
	}

	public static String uploadGRVImages(HashMap<String,ArrayList<DamageImageDO>> hmImages)
	{
		StringBuilder sb = new StringBuilder(),sb2,sb3;
		sb.append(SOAP_HEADER)
		.append("<PostGRVImages xmlns=\"http://tempuri.org/\">")
		.append("<objTrxHeaderDcos>");
		
		Set<String> keys = hmImages.keySet();
		
		sb2 = new StringBuilder();
		for (String key : keys) 
		{
			 sb2.append("<TrxHeaderDco>")
			.append("<TrxCode>").append(key).append("</TrxCode>");
			
			sb3 = new StringBuilder();
			sb3.append("<objOrderImages>");
			ArrayList<DamageImageDO> arrList = hmImages.get(key);
			for (int j = 0; j < arrList.size(); j++) 
			{
				 sb3.append("<OrderImageDco>")
				.append("<OrderNo>").append(arrList.get(j).OrderNo).append("</OrderNo>")
				.append("<ItemCode>").append(arrList.get(j).ItemCode).append("</ItemCode>")
				.append("<LineNo>").append(arrList.get(j).LineNo).append("</LineNo>")
				.append("<ImagePath>").append(arrList.get(j).ImagePath).append("</ImagePath>")
				.append("<CapturedDate>").append(arrList.get(j).CapturedDate).append("</CapturedDate>")
				.append("</OrderImageDco>");
			}
			sb3.append("</objOrderImages>");
			
			sb2.append(sb3)
			.append("</TrxHeaderDco>");
		}
		sb.append(sb2)
		.append("</objTrxHeaderDcos>")
		.append("</PostGRVImages>")
		.append(SOAP_FOOTER);
		Log.d("uploadGRVImages", sb.toString());
		return sb.toString();
	}
	public static String uploadInsertInvoicePrintImage
			(ArrayList<OrderPrintImageDO> arr)
	{
		StringBuilder sb = new StringBuilder(),sb2,sb3;
		sb.append(SOAP_HEADER)
		.append("<InsertInvoicePrintImage  xmlns=\"http://tempuri.org/\">")
		.append("<objTrxHeaderPrintDco>");
			for (int j = 0; arr!=null && j < arr.size(); j++)
			{
				OrderPrintImageDO obj = new OrderPrintImageDO();
				obj = arr.get(j);
				sb.append("<TrxHeaderPrintDco>")
					.append("<TrxCode>").append(obj.TrxCode).append("</TrxCode>")
				.append("<UserCode>").append(obj.UserCode).append("</UserCode>")
				.append("<ImagePath>").append(obj.ImagePath).append("</ImagePath>")
				.append("<ImageType>").append(obj.ImageType).append("</ImageType>")
				.append("<CaptureDate>").append(obj.CaptureDate).append("</CaptureDate>")
				.append("<Status>").append(obj.status).append("</Status>")
				.append("</TrxHeaderPrintDco>");
			}
		sb.append("</objTrxHeaderPrintDco>")

		.append("</InsertInvoicePrintImage>")
		.append(SOAP_FOOTER);
		Log.d("uploadGRVImages", sb.toString());
		return sb.toString();
	}

	public static String uploadChequeImages(HashMap<String,ArrayList<PostPaymentDetailDONew>> hmImages)
	{
		StringBuilder sb = new StringBuilder(),sb2,sb3;
		sb.append(SOAP_HEADER)
		.append("<PostChequeImages xmlns=\"http://tempuri.org/\">")
		.append("<objPaymentHeaderDcos>");
		
		Set<String> keys = hmImages.keySet();
		
		sb2 = new StringBuilder();
		for (String key : keys) 
		{
			 sb2.append("<PaymentHeaderDco>")
			.append("<Receipt_Number>").append(key).append("</Receipt_Number>");
			
			sb3 = new StringBuilder();
			sb3.append("<PaymentDetails>");
			ArrayList<PostPaymentDetailDONew> arrList = hmImages.get(key);
			for (int j = 0; j < arrList.size(); j++) 
 {
				PostPaymentDetailDONew obPaymentDetailDO = arrList.get(j);
				try {
					sb3.append("<PaymentDetailDco>")
							.append("<Receipt_Number>"
									+ obPaymentDetailDO.ReceiptNo
									+ "</Receipt_Number>")
							.append("<LineNo>" + obPaymentDetailDO.LineNo
									+ "</LineNo>")
							.append("<PaymentMode>"
									+ obPaymentDetailDO.PaymentTypeCode
									+ "</PaymentMode>")
							.append("<BankCode>" + obPaymentDetailDO.BankCode
									+ "</BankCode>")
							.append("<OtherBankName>"
									+ URLEncoder.encode(obPaymentDetailDO.UserDefinedBankName,"UTF-8")
									+ "</OtherBankName>")
							.append("<ChequeDate>" + obPaymentDetailDO.ChequeDate
									+ "</ChequeDate>")
							.append("<ChequeNo>" + obPaymentDetailDO.ChequeNo
									+ "</ChequeNo>")
							.append("<CCNo>" + obPaymentDetailDO.CCNo + "</CCNo>")
							.append("<CCExpiry>" + obPaymentDetailDO.CCExpiry
									+ "</CCExpiry>")
							.append("<SalesOrgCode>"
									+ obPaymentDetailDO.salesOrgCode
									+ "</SalesOrgCode>")
							.append("<ChequeImagePath>"
									+ obPaymentDetailDO.ChequeImagePath
									+ "</ChequeImagePath>")
							.append("<Amount>" + obPaymentDetailDO.Amount
									+ "</Amount>").append("</PaymentDetailDco>");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sb3.append("</PaymentDetails>");
			
			sb2.append(sb3)
			.append("</PaymentHeaderDco>");
		}
		sb.append(sb2)
		.append("</objPaymentHeaderDcos>")
		.append("</PostChequeImages>")
		.append(SOAP_FOOTER);
		Log.d("PostChequeImages ", sb.toString());
		return sb.toString();
	}
	
	public static String insertBlkInsertCustomerDocument(Vector<AddCustomerFilesDO> vecNewCustomerDO)
	{
		StringBuffer strHHCustomer = new StringBuffer();
		
		for(int i = 0; i < vecNewCustomerDO.size(); i++)
		{
			AddCustomerFilesDO obj = vecNewCustomerDO.get(i);
			
			strHHCustomer.append("<CustomerDocumentDco>")
					     .append("<SiteId>").append(obj.CustomerCode).append("</SiteId>")
			             .append("<ImgPath>").append(obj.ServerFilePath).append("</ImgPath>")
			             .append("<DocName>").append(obj.DocumentName).append("</DocName>")
			             .append("<Date>").append(obj.CreatedOn).append("</Date>")
					     .append("</CustomerDocumentDco>");
		}
		StringBuffer strXML = new StringBuffer();
		strXML.append(SOAP_HEADER)
					.append("<BlkInsertCustomerDocument xmlns=\"http://tempuri.org/\">")
					.append("<objImportCustomerDocument>")
					.append("<CustomerDocument>")
					.append(strHHCustomer)
					.append("</CustomerDocument>")
					.append("</objImportCustomerDocument>")
					.append("</BlkInsertCustomerDocument>")
					.append(SOAP_FOOTER);
		
		LogUtils.errorLog("error", strXML.toString());
		if(strXML.toString().contains("&"))
			strXML.toString().replace("&", "&amp;");
		
		return strXML.toString();
	}

	public static String insertNewTypeHHCustomer(Vector<CustomerDao> vecNewCustomerDO, String route)
	{
		StringBuffer strXML = new StringBuffer();
		
		StringBuffer strHHCustomer= new StringBuffer();
		
		for(int i = 0; i < vecNewCustomerDO.size(); i++)
		{
			CustomerDao obj = vecNewCustomerDO.get(i);
			
			strHHCustomer.append("<HHCustomerDco>")
					.append("<SiteId>").append(obj.site).append("</SiteId>")
			           .append("<CustomerName>").append(obj.SiteName).append("</CustomerName>")
			           .append("<AltCustomerName>").append(obj.SiteName).append("</AltCustomerName>")
			           .append("<CountryCode>").append(obj.CountryCode).append("</CountryCode>")
			           .append("<City>").append(obj.City).append("</City>")
			           .append("<Street>").append(obj.Street).append("</Street>")
			           .append("<ContactNo>").append(obj.PhoneNumber).append("</ContactNo>")
			           .append("<FaxNo></FaxNo>")
			           .append("<Address1>").append(obj.Address1).append("</Address1>")
			           .append("<Address2>").append(obj.Address2).append("</Address2>")
			           .append("<PoBoxNumber>").append(obj.PoNumber).append("</PoBoxNumber>")
			           .append("<RegionCode>").append(obj.RegionCode).append("</RegionCode>")
			           .append("<GEOCodeX>").append(obj.GeoCodeX).append("</GEOCodeX>")
			           .append("<GEOCodeY>").append(obj.GeoCodeY).append("</GEOCodeY>")
			           .append("<Email>").append(obj.Email).append("</Email>")
			           .append("<CreatedBy>").append(obj.CreatedBy).append("</CreatedBy>")
			           .append("<CreatedDate>").append(obj.CustAcctCreationDate).append("</CreatedDate>")
			           .append("<CreatedTime>").append(obj.CustAcctCreationDate).append("</CreatedTime>")
			           .append("<SalesOrgCode>").append(obj.SalesOrgCode).append("</SalesOrgCode>")
			           .append("<SalesOfficeCode></SalesOfficeCode>")
			           .append("<DivisionCode></DivisionCode>")
			           .append("<PaymentTerm>").append(obj.PaymentTermCode).append("</PaymentTerm>")
			           .append("<PriceList>").append(obj.PriceList).append("</PriceList>")
			           .append("<PaymentType>").append(obj.PaymentType).append("</PaymentType>")
			           .append("<CustomerType>").append(AppConstants.Customer).append("</CustomerType>")
			           .append("<ChannelCode>").append(obj.ChannelCode).append("</ChannelCode>")
			           .append("<SubChannelCode>").append(obj.SubChannelCode).append("</SubChannelCode>")
			           .append("<BillTo></BillTo>")
			           .append("<ShipTo></ShipTo>")
					   .append("</HHCustomerDco>");
		}
		
				strXML.append(SOAP_HEADER )
					.append("<BlkInsertHHCustomer xmlns=\"http://tempuri.org/\">")
					.append("<objImportCustomer>")
						.append("<Customers>")
						.append(strHHCustomer )
						.append("</Customers>")
					    .append("</objImportCustomer>")
					    .append("</BlkInsertHHCustomer>")
						.append(SOAP_FOOTER);
		
		LogUtils.debug("error", strXML.toString());
		if(strXML.toString().contains("&"))
			strXML.toString().replace("&", "&amp;");
		
		return strXML.toString();
	}
	public static String getVersionDetails(String deviceType,String versionNumber)
	{
		String strXML = SOAP_HEADER +
				"<GetVersionDetails xmlns=\"http://tempuri.org/\">"+
				"<DeviceType>"+deviceType+"</DeviceType>"+
				"<VersionNo>"+versionNumber+"</VersionNo>"+
			    "</GetVersionDetails>"+
				SOAP_FOOTER;
		LogUtils.errorLog("VersionRequest", strXML);
		return strXML;
	}
	
	/**
	 * Send All customer status.
	 * @param vecStatus
	 * @return String
	 */
	public static String sendAllStatus(Vector<StatusDO> vecStatus) 
	{
		StringBuffer strXML = new StringBuffer();
		StringBuffer strStatus = new StringBuffer();
		StatusDO objstatus = null;
		for (int i = 0; i < vecStatus.size(); i++) 
		{
			objstatus = vecStatus.get(i);
			strStatus.append("<DeviceStatusDco>")
			.append("<UUid>").append(objstatus.UUid).append("</UUid>")
	           .append("<Userid>").append(objstatus.Userid).append("</Userid>")
	           .append("<Customersite>").append(objstatus.Customersite).append("</Customersite>")
	           .append("<Date>").append(objstatus.Date).append("</Date>")
	           .append("<Visitcode>").append(objstatus.Visitcode).append("</Visitcode>")
	           .append("<JourneyCode>").append(objstatus.JourneyCode).append("</JourneyCode>")
	           .append("<Status>").append(objstatus.Status).append("</Status>")
	           .append("<Action>").append(objstatus.Action).append("</Action>")
	           .append("<Type>").append(objstatus.Type).append("</Type>")
	           .append("</DeviceStatusDco>");
		}
		strXML.append(SOAP_HEADER)
		.append("<InsertDeviceStatus xmlns=\"http://tempuri.org/\">")
			.append("<objDeviceStatusDco>")
			.append(strStatus)
			.append("</objDeviceStatusDco>")
		    .append("</InsertDeviceStatus>")
			.append(SOAP_FOOTER);
		
		if(strXML.toString().contains("&"))
			strXML.toString().replace("&", "&amp;");
		LogUtils.debug("strXML_InsertDeviceStatus", strXML.toString());
		return strXML.toString();
	}

	public static String getUserLoadRequestsPerDay(String stringFromPreference) 
	{
		StringBuffer strXML = new StringBuffer();
		strXML.append(SOAP_HEADER)
			.append("<GetUserLoadRequestsPerDay xmlns=\"http://tempuri.org/\">")
			.append("<UserCode>").append(stringFromPreference).append("</UserCode>")
			.append("</GetUserLoadRequestsPerDay>")
			.append(SOAP_FOOTER);
		
		LogUtils.debug("getUserLoadRequestsPerDay", strXML.toString());
		
		return strXML.toString();
	}
	
	public static String InsertServiceCapture(Vector<ServiceCaptureDO> vecServiceCaptureDOs) 
	{
		StringBuffer strXML = new StringBuffer();
		strXML.append(SOAP_HEADER)
			.append("<InsertServiceCapture xmlns=\"http://tempuri.org/\">");
			
		StringBuffer strBuff = new StringBuffer();
		for (int i = 0; i < vecServiceCaptureDOs.size(); i++) 
		{
			strBuff.append("<objServiceCapture>")
			.append("<UserCode>").append(vecServiceCaptureDOs.get(i).UserCode).append("</UserCode>")
			.append("<CustomerCode>").append(vecServiceCaptureDOs.get(i).CustomerCode).append("</CustomerCode>")
			.append("<BeforeImage>").append(vecServiceCaptureDOs.get(i).BeforeImage).append("</BeforeImage>")
			.append("<AfterImage>").append(vecServiceCaptureDOs.get(i).AfterImage).append("</AfterImage>")
			.append("<CreatedDate>").append(vecServiceCaptureDOs.get(i).CreatedDate).append("</CreatedDate>")
			.append("<Status>").append(vecServiceCaptureDOs.get(i).Status).append("</Status>")
			.append("</objServiceCapture>");
		}
		strXML.append(strBuff)
			.append("</InsertServiceCapture>")
			.append(SOAP_FOOTER);
		
		LogUtils.debug("getUserLoadRequestsPerDay", strXML.toString());
		
		return strXML.toString();
	}
	
	public static String InsertServiceCaptureSingle(ServiceCaptureDO vecServiceCaptureDOs) 
	{
		StringBuffer strXML = new StringBuffer();
		strXML.append(SOAP_HEADER)
			.append("<InsertServiceCapture xmlns=\"http://tempuri.org/\">")
				.append("<objServiceCapture>")
					.append("<UserCode>").append(vecServiceCaptureDOs.UserCode).append("</UserCode>")
					.append("<CustomerCode>").append(vecServiceCaptureDOs.CustomerCode).append("</CustomerCode>")
					.append("<BeforeImage>").append(vecServiceCaptureDOs.BeforeImage).append("</BeforeImage>")
					.append("<AfterImage>").append(vecServiceCaptureDOs.AfterImage).append("</AfterImage>")
					.append("<CreatedDate>").append(vecServiceCaptureDOs.CreatedDate).append("</CreatedDate>")
					.append("<Status>").append(vecServiceCaptureDOs.Status).append("</Status>")
				.append("</objServiceCapture>")
			.append("</InsertServiceCapture>")
			.append(SOAP_FOOTER);
		
		LogUtils.debug("getUserLoadRequestsPerDay", strXML.toString());
		
		return strXML.toString();
	}

	public static String sendAllCashDenom(String userID, Vector<CashDenomDO> vecCashdenom) 
	{
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(SOAP_HEADER)
			.append("<PostCasnDenomintaionsFromXML xmlns=\"http://tempuri.org/\">")
				.append("<objCashDenominationDcos>");
			
		StringBuffer sbuff = new StringBuffer();
		for (int i = 0; i < vecCashdenom.size(); i++) 
		{
			strBuff.append("<UserDenominationDetailsDco>")
				.append("<UserCode>").append(userID).append("</UserCode>")
				.append("<UserName>").append(vecCashdenom.get(i).UserCode).append("</UserName>")
				.append("<HelperCode>").append(vecCashdenom.get(i).HelperUserCode).append("</HelperCode>")
				.append("<HelperName></HelperName>")
				.append("<CollectionDate>").append(vecCashdenom.get(i).CollectionDate).append("</CollectionDate>");
				
				Denominations objDenom;
				if(StringUtils.getInt(vecCashdenom.get(i).Division) > 0)
					objDenom = vecCashdenom.get(i).objFoodDenom;
				else
					objDenom = vecCashdenom.get(i).objIceCreamDenom;
				
					strBuff.append("<ThousandNotes>").append(objDenom.Units1000).append("</ThousandNotes>")
					.append("<FiveHunderedNotes>").append(objDenom.Units500).append("</FiveHunderedNotes>")
					.append("<TwoHunderedNotes>").append(objDenom.Units200).append("</TwoHunderedNotes>")
					.append("<HunderedNotes>").append(objDenom.Units100).append("</HunderedNotes>")
					.append("<FiftyNotes>").append(objDenom.Units50).append("</FiftyNotes>")
					.append("<TwentyNotes>").append(objDenom.Units20).append("</TwentyNotes>")
					.append("<TenNotes>").append(objDenom.Units10).append("</TenNotes>")
					.append("<FiveNotes>").append(objDenom.Units5).append("</FiveNotes>")
					.append("<Coins>").append(objDenom.UnitsCoin).append("</Coins>")
					.append("<OtherCurrency>").append(objDenom.OtherCurrencyNote).append("</OtherCurrency>")
					.append("<OtherCurrencyAmount>").append(objDenom.OtherCurrency).append("</OtherCurrencyAmount>")
					.append("<VouchersAmount>").append(objDenom.Voucher).append("</VouchersAmount>")
					.append("<Vouchers>").append(objDenom.VoucherNo).append("</Vouchers>")
					.append("<CreditNotes>").append(objDenom.CashPaidCrNote).append("</CreditNotes>")
					.append("<CreditAmount>").append(objDenom.CashPaidCredit).append("</CreditAmount>")
					
				.append("<Division>").append(vecCashdenom.get(i).Division).append("</Division>")
				.append("<RouteNo>").append(vecCashdenom.get(i).RouteNo).append("</RouteNo>")
				.append("<TotalCash>").append(0).append("</TotalCash>")
				.append("<TotalAmount>").append(0).append("</TotalAmount>")
			.append("</UserDenominationDetailsDco>");
		}
		
		strBuff.append("</objCashDenominationDcos>")
			.append("</PostCasnDenomintaionsFromXML>")
			.append(SOAP_FOOTER);
		
		LogUtils.debug("sendAllCashDenom", strBuff.toString());
		return strBuff.toString();
	}

	public static String CheckManagerPassword(String userid,String asmPassword) 
	{
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(SOAP_HEADER)
			.append("<CheckManagerPassword xmlns=\"http://tempuri.org/\">")
				.append("<UserName>").append(userid).append("</UserName>")
				.append("<ManagerPassword>").append(asmPassword).append("</ManagerPassword>")
			.append("</CheckManagerPassword>")
		.append(SOAP_FOOTER);
		
		LogUtils.debug("CheckManagerPassword", strBuff.toString());
		return strBuff.toString();
	}
	
	public static String sendUnVisitedCustomers(ArrayList<JourneyPlanDO> arrJopurneyPlanDO) 
	{
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(SOAP_HEADER)
			.append("<PostUnvisitedCustomersFromXML xmlns=\"http://tempuri.org/\">")
			.append("<objUnvisitedCustomersDco>");
		
		for (int i = 0; i < arrJopurneyPlanDO.size(); i++) 
		{
			try {
				strBuff
				.append("<UnvisitedCustomersDco>")
					.append("<UserCode>").append(arrJopurneyPlanDO.get(i).userID).append("</UserCode>")
//				.append("<ClientCode>").append(arrJopurneyPlanDO.get(i).clientCode).append("</ClientCode>")
					.append("<ClientCode>").append(URLEncoder.encode(arrJopurneyPlanDO.get(i).clientCode,"UTF-8")).append("</ClientCode>")
					.append("<JourneyCode>").append(arrJopurneyPlanDO.get(i).JourneyCode).append("</JourneyCode>")
					.append("<JourneyDate>").append(arrJopurneyPlanDO.get(i).JourneyDate).append("</JourneyDate>")
					.append("<ReasonType>").append(arrJopurneyPlanDO.get(i).reasonType).append("</ReasonType>")
					.append("<Reason>").append(arrJopurneyPlanDO.get(i).reasonForSkip).append("</Reason>")
				.append("</UnvisitedCustomersDco>");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
				strBuff.append("</objUnvisitedCustomersDco>")
				.append("</PostUnvisitedCustomersFromXML>")
		.append(SOAP_FOOTER);
		
		LogUtils.debug("UnvisitedCustomersDco", strBuff.toString());
		return strBuff.toString();
	}

	public static String getAllDeleteLogs(String lsd, String lst)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
					.append("<GetAllDeleteLogs xmlns=\"http://tempuri.org/\">")
	     			.append("<lsd>").append(lsd).append("</lsd>")
	     			.append("<lst>").append(lst).append("</lst>")
	     			.append("</GetAllDeleteLogs>")
					.append(SOAP_FOOTER) ;
		
		return strXML.toString();
	}
	
	public static String getClearDataPermission(String empNo)
	{
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
					.append("<GetClearDataPermission xmlns=\"http://tempuri.org/\">" )
					.append("<UserCode>").append(empNo).append("</UserCode>")
					.append("</GetClearDataPermission>")
	      				.append(SOAP_FOOTER) ;
	      				LogUtils.errorLog("strXML",""+strXML);
		return strXML.toString();
	}
	
	public static String checkAppUpgrade(String strUserId, String deviceType,String versionNumber)
	{
		String strXML = SOAP_HEADER +
				"<CheckLatestApkVersion xmlns=\"http://tempuri.org/\">"+
				"<UserId>"+strUserId+"</UserId>"+
				"<ApkType>"+deviceType+"</ApkType>"+
				"<VersionNo>"+versionNumber+"</VersionNo>"+
			    "</CheckLatestApkVersion>"+
				SOAP_FOOTER;
		LogUtils.errorLog("VersionRequest", strXML);
		return strXML;
	}
}
