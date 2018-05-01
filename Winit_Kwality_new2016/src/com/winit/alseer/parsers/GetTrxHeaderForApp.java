package com.winit.alseer.parsers;

import android.content.Context;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.ReturnOrderDA;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.TrxPromotionDO;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class GetTrxHeaderForApp  extends BaseHandler
{

	private ArrayList<TrxHeaderDO> arrTrxHeaderDOs=null;
	private TrxHeaderDO trxHeaderDO;
	private TrxDetailsDO trxDetailsDO;
	private TrxPromotionDO trxPromotionDO;
	
	private final int PARSING_SCOPE_TRX_HEADER=1;
	private final int PARSING_SCOPE_OBJ_TRX_DETAILS=2;
	private final int PARSING_SCOPE_OBJ_TRX_PROMOTIONS=3;
	private final int PARSING_SCOPE_SERVER=4;
	
	private final int PARSING_SCOPE_INVALID = -1;
	private int parsingScope		=PARSING_SCOPE_INVALID;
	
	
	private String empNo;
	SynLogDO syncLogDO = new SynLogDO();
	public GetTrxHeaderForApp(Context context, String strEmpNo) 
	{
		super(context);
		this.empNo = strEmpNo;
	}
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		switch (parsingScope)
		{
		
			case (PARSING_SCOPE_INVALID):
			{
				if(localName.equalsIgnoreCase("GetTrxHeaderForAppResult"))
				{
					parsingScope = PARSING_SCOPE_SERVER;
				}
				else if(localName.equalsIgnoreCase("TrxHeaders"))
				{
					arrTrxHeaderDOs = new ArrayList<TrxHeaderDO>();
					parsingScope = PARSING_SCOPE_TRX_HEADER;
				}
				
				
			}
			break;
			case (PARSING_SCOPE_TRX_HEADER):
			{
				if(localName.equalsIgnoreCase("TrxHeaderDco"))
				{
					trxHeaderDO=new TrxHeaderDO();
				}
				else if(localName.equalsIgnoreCase("objTrxDetails"))
				{
					trxHeaderDO.arrTrxDetailsDOs = new ArrayList<TrxDetailsDO>();
					parsingScope = PARSING_SCOPE_OBJ_TRX_DETAILS;
				}
				else if(localName.equalsIgnoreCase("objTrxPromotions"))
				{
					trxHeaderDO.arrPromotionDOs = new ArrayList<TrxPromotionDO>();
					parsingScope = PARSING_SCOPE_OBJ_TRX_PROMOTIONS;
				}
//				else if(localName.equalsIgnoreCase("objOrderImages"))
//					{
//						trxHeaderDO.arrTrxDetailsDOs = new ArrayList<TrxDetailsDO>();
//						parsingScope = PARSING_SCOPE_ORDER_IMAGES;
//					}
			}
			break;
			case (PARSING_SCOPE_OBJ_TRX_DETAILS):
			{
				if(localName.equalsIgnoreCase("TrxDetailDco"))
				{
					trxDetailsDO = new TrxDetailsDO();
				}
			}
			break;
			case (PARSING_SCOPE_OBJ_TRX_PROMOTIONS):
			{
				if(localName.equalsIgnoreCase("TrxPromotionDco"))
				{
					trxPromotionDO = new TrxPromotionDO();
				}
			}
			break;
			
		}
		if (parsingScope != PARSING_SCOPE_INVALID)
		{
			currentElement  = true;
			currentValue = new StringBuilder();
			
		}
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		switch (parsingScope) 
		{
			case PARSING_SCOPE_SERVER:
			{
				if(localName.equalsIgnoreCase("ServerTime"))
				{
					syncLogDO.TimeStamp = currentValue.toString();
					preference.saveStringInPreference(ServiceURLs.GetTrxHeaderForApp+Preference.LAST_SYNC_TIME, currentValue.toString());
				}
				if(localName.equalsIgnoreCase("ModifiedDate"))
				{
					syncLogDO.UPMJ = currentValue.toString();
					preference.saveStringInPreference(ServiceURLs.GetTrxHeaderForApp+Preference.LAST_SYNC_TIME, currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("Status"))
				{
					syncLogDO.action =  currentValue.toString();
				}
				if(localName.equalsIgnoreCase("ModifiedTime"))
				{
					syncLogDO.UPMT = currentValue.toString();
					preference.saveStringInPreference(ServiceURLs.GetTrxHeaderForApp+Preference.LAST_SYNC_TIME, currentValue.toString());
					parsingScope = PARSING_SCOPE_INVALID;
					syncLogDO.entity = ServiceURLs.GetTrxHeaderForApp;
					new SynLogDA().insertSynchLog(syncLogDO);
				}
			}
			break;
			case 	PARSING_SCOPE_TRX_HEADER:
			{
				if(localName.equalsIgnoreCase("TrxCode")){trxHeaderDO.trxCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("AppTrxId")){trxHeaderDO.appTrxId = currentValue.toString();}
				else if(localName.equalsIgnoreCase("OrgCode")){trxHeaderDO.orgCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("JourneyCode")){trxHeaderDO.journeyCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("VisitCode")){trxHeaderDO.visitCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("UserCode")){trxHeaderDO.userCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("ClientCode")){trxHeaderDO.clientCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("ClientBranchCode")){trxHeaderDO.clientBranchCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("StrTrxDate")){trxHeaderDO.trxDate = currentValue.toString();}
				else if(localName.equalsIgnoreCase("TrxType")){trxHeaderDO.trxType = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("CurrencyCode")){trxHeaderDO.currencyCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("PaymentType")){trxHeaderDO.paymentType = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("RemainingAmount")){trxHeaderDO.remainingAmount = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("TotalAmount")){trxHeaderDO.totalAmount = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("TotalDiscountAmount")){trxHeaderDO.totalDiscountAmount = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("TotalTAXAmount")){trxHeaderDO.totalTAXAmount = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("TrxReasonCode")){trxHeaderDO.trxReasonCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("ReferenceCode")){trxHeaderDO.referenceCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("ClientSignature")){trxHeaderDO.clientSignature = currentValue.toString();}
				else if(localName.equalsIgnoreCase("SalesmanSignature")){trxHeaderDO.salesmanSignature = currentValue.toString();}
				else if(localName.equalsIgnoreCase("Status")){trxHeaderDO.status = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("VisitID")){trxHeaderDO.visitCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("FreeNote")){trxHeaderDO.freeNote = currentValue.toString();}
				else if(localName.equalsIgnoreCase("CreatedOn")){trxHeaderDO.createdOn = currentValue.toString();}
				else if(localName.equalsIgnoreCase("PreTrxCode")){trxHeaderDO.preTrxCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("TRXStatus")){trxHeaderDO.trxStatus = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("BranchPlantCode")){trxHeaderDO.branchPlantCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("UpdatedOn")){trxHeaderDO.updatedOn = currentValue.toString();}
				else if(localName.equalsIgnoreCase("PrintingTimes")){trxHeaderDO.printingTimes = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("ApproveByCode")){trxHeaderDO.approveByCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("StrApprovedDate")){trxHeaderDO.approvedDate = currentValue.toString();}
				else if(localName.equalsIgnoreCase("LPOCode")){trxHeaderDO.lPOCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("StrDeliveryDate")){trxHeaderDO.deliveryDate = currentValue.toString();}
				else if(localName.equalsIgnoreCase("UserCreditAccountCode")){trxHeaderDO.userCreditAccountCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("PushedOn")){trxHeaderDO.pushedOn = currentValue.toString();}
				else if(localName.equalsIgnoreCase("ModifiedDate")){trxHeaderDO.modifiedDate = currentValue.toString();}
				else if(localName.equalsIgnoreCase("ModifiedTime")){trxHeaderDO.modifiedDate = currentValue.toString();}
				else if(localName.equalsIgnoreCase("TrxSubType")){trxHeaderDO.trxSubType = StringUtils.getInt(currentValue.toString());}
				
				
				else if(localName.equalsIgnoreCase("LPO")){trxHeaderDO.LPONo = currentValue.toString();}
				else if(localName.equalsIgnoreCase("Narrotion")){trxHeaderDO.Narration = currentValue.toString();}
				else if(localName.equalsIgnoreCase("RateDiff")){trxHeaderDO.rateDiff = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("SplDisc")){trxHeaderDO.specialDiscPercent = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("Division")){trxHeaderDO.Division = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("VAT")){trxHeaderDO.totalVATAmount = StringUtils.getFloat(currentValue.toString());}

				else if(localName.equalsIgnoreCase("TrxHeaderDco"))
				{
					arrTrxHeaderDOs.add(trxHeaderDO);
				}
				
				else if(localName.equalsIgnoreCase("TrxHeaders"))
				{
					insertTrxHeaderForAppData();
				}
			}
			break;
			case PARSING_SCOPE_OBJ_TRX_DETAILS:
			{
				if(localName.equalsIgnoreCase("LineNo")){trxDetailsDO.lineNo = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("TrxCode")){trxDetailsDO.trxCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("ItemCode")){trxDetailsDO.itemCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("OrgCode")){trxDetailsDO.orgCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("TrxReasonCode")){trxDetailsDO.trxReasonCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("TrxDetailsNote")){trxDetailsDO.trxDetailsNote = currentValue.toString();}
				else if(localName.equalsIgnoreCase("ItemType")){trxDetailsDO.itemType = currentValue.toString();}
				else if(localName.equalsIgnoreCase("BasePrice")){trxDetailsDO.basePrice = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("UOM")){trxDetailsDO.UOM = currentValue.toString();}
				else if(localName.equalsIgnoreCase("QuantityLevel1")){trxDetailsDO.quantityLevel1 = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("QuantityLevel2")){trxDetailsDO.quantityLevel2 = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("QuantityLevel3")){trxDetailsDO.quantityLevel3 = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("QuantityBU")){trxDetailsDO.quantityBU = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("RequestedBU")){trxDetailsDO.requestedBU = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("ApprovedBU")){trxDetailsDO.approvedBU = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("CollectedBU")){trxDetailsDO.collectedBU = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("FinalBU")){trxDetailsDO.finalBU = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("PriceUsedLevel1")){trxDetailsDO.priceUsedLevel1 = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("PriceUsedLevel2")){trxDetailsDO.priceUsedLevel2 = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("PriceUsedLevel3")){trxDetailsDO.priceUsedLevel3 = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("TaxPercentage")){trxDetailsDO.taxPercentage = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("TotalDiscountPercentage")){trxDetailsDO.totalDiscountPercentage = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("TotalDiscountAmount")){trxDetailsDO.totalDiscountAmount = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("CalculatedDiscountPercentage")){trxDetailsDO.calculatedDiscountPercentage = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("CalculatedDiscountAmount")){trxDetailsDO.calculatedDiscountAmount = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("UserDiscountPercentage")){trxDetailsDO.userDiscountPercentage = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("UserDiscountAmount")){trxDetailsDO.userDiscountAmount = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("ItemDescription")){trxDetailsDO.itemDescription = currentValue.toString();}
				else if(localName.equalsIgnoreCase("ItemAltDescription")){trxDetailsDO.itemAltDescription = currentValue.toString();}
				else if(localName.equalsIgnoreCase("DistributionCode")){trxDetailsDO.distributionCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("AffectedStock")){trxDetailsDO.affectedStock = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("Status")){trxDetailsDO.status = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("PromoID")){trxDetailsDO.promoID = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("PromoType")){trxDetailsDO.promoType = currentValue.toString();}
				else if(localName.equalsIgnoreCase("CreatedOn")){trxDetailsDO.createdOn = currentValue.toString();}
				else if(localName.equalsIgnoreCase("TRXStatus")){trxDetailsDO.trxStatus = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("StrExpiryDate")){trxDetailsDO.expiryDate = currentValue.toString();}
				else if(localName.equalsIgnoreCase("RelatedLineID")){trxDetailsDO.relatedLineID = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("ItemGroupLevel5")){trxDetailsDO.itemGroupLevel5 = currentValue.toString();}
				else if(localName.equalsIgnoreCase("TaxType")){trxDetailsDO.taxType = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("SuggestedBU")){trxDetailsDO.suggestedBU = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("PushedOn")){trxDetailsDO.pushedOn = currentValue.toString();}
				else if(localName.equalsIgnoreCase("ModifiedDate")){trxDetailsDO.modifiedDate = currentValue.toString();}
				else if(localName.equalsIgnoreCase("ModifiedTime")){trxDetailsDO.modifiedTime = currentValue.toString();}
				else if(localName.equalsIgnoreCase("Reason")){trxDetailsDO.reason = currentValue.toString();}
				else if(localName.equalsIgnoreCase("CancelledQuantity")){trxDetailsDO.cancelledQuantity = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("InProcessQuantity")){trxDetailsDO.inProcessQuantity = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("ShippedQuantity")){trxDetailsDO.shippedQuantity = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("MissedBU")){trxDetailsDO.missedBU = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("VAT")){trxDetailsDO.vatPercentage = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("TotalVATAmount")){trxDetailsDO.VATAmountNew = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("BatchNumber")){trxDetailsDO.batchCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("TrxDetailDco")){trxHeaderDO.arrTrxDetailsDOs.add(trxDetailsDO);}
				else if(localName.equalsIgnoreCase("objTrxDetails"))
				{
//					trxHeaderDO.arrTrxDetailsDOs.add(trxDetailsDO);
					parsingScope = PARSING_SCOPE_TRX_HEADER;
				}
				
			}
			break;
			case PARSING_SCOPE_OBJ_TRX_PROMOTIONS:
			{
				 if(localName.equalsIgnoreCase("TrxCode")){trxPromotionDO.trxCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("ItemCode")){trxPromotionDO.itemCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("DiscountAmount")){trxPromotionDO.discountAmount = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("DiscountPercentage")){trxPromotionDO.discountPercentage = StringUtils.getFloat(currentValue.toString());}
				else if(localName.equalsIgnoreCase("OrgCode")){trxPromotionDO.orgCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("PromotionID")){trxPromotionDO.promotionID = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("FactSheetCode")){trxPromotionDO.factSheetCode = currentValue.toString();}
				else if(localName.equalsIgnoreCase("Status")){trxPromotionDO.status = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("CreatedOn")){trxPromotionDO.createdOn = currentValue.toString();}
				else if(localName.equalsIgnoreCase("TrxStatus")){trxPromotionDO.trxStatus = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("PromotionType")){trxPromotionDO.promotionType = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("TrxDetailsLineNo")){trxPromotionDO.trxDetailsLineNo = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("ItemType")){trxPromotionDO.itemType = currentValue.toString();}
				else if(localName.equalsIgnoreCase("IsStructural")){trxPromotionDO.isStructural = StringUtils.getInt(currentValue.toString());}
				else if(localName.equalsIgnoreCase("PushedOn")){trxPromotionDO.pushedOn = currentValue.toString();}
				else if(localName.equalsIgnoreCase("ModifiedDate")){trxPromotionDO.modifiedDate = currentValue.toString();}
				else if(localName.equalsIgnoreCase("ModifiedTime")){trxPromotionDO.modifiedTime = currentValue.toString();}
				else if(localName.equalsIgnoreCase("TrxPromotionDco"))
				{
					trxHeaderDO.arrPromotionDOs.add(trxPromotionDO);
				}
				else if(localName.equalsIgnoreCase("objTrxPromotions"))
				{
					parsingScope = PARSING_SCOPE_TRX_HEADER;
				}
			}
			break;

		default:
			break;
		}
		
		
	}
	
	private boolean insertTrxHeaderForAppData() 
	{
		new ReturnOrderDA().updateReturnOrderStatus(arrTrxHeaderDOs);
		// insertIntoDataBase
		return false;
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	

}
