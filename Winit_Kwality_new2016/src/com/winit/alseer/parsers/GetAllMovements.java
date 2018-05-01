package com.winit.alseer.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.InventoryDA;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataobject.LoadRequestDO;
import com.winit.alseer.salesman.dataobject.LoadRequestDetailDO;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.dataobject.VanStockDO;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class GetAllMovements extends BaseHandler
{
	private final int TYPE_MOVEMENT_HEADER = 1, TYPE_MOVEMENT_DETAILS = 2,TYPE_VANSTOCK_DETAILS = 3;
	private int PARSE_TYPE = -1;
	private HashMap<String, LoadRequestDO> hashMap = new HashMap<String, LoadRequestDO>();
	private LoadRequestDO loadRequestDO;
	private LoadRequestDetailDO loadRequestDetailDO;
	private VanStockDO vanStockDO;
	private Vector<VanStockDO> vecVanStocks;
	private String movementCode2 = "", movementCode1 = "";
	private SynLogDO synLogDO = new SynLogDO();
	
	public GetAllMovements(Context context, String empNo) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		
		if(localName.equalsIgnoreCase("MovementHeaders"))
		{
			PARSE_TYPE = TYPE_MOVEMENT_HEADER;
			hashMap = new HashMap<String, LoadRequestDO>();
		}
		else if(localName.equalsIgnoreCase("MovementHeaderDco"))
		{
			loadRequestDO = new LoadRequestDO();
		}
		else if(localName.equalsIgnoreCase("MovementDetails"))
		{
			PARSE_TYPE = TYPE_MOVEMENT_DETAILS;
		}
		else if(localName.equalsIgnoreCase("MovementDetailDco"))
		{
			loadRequestDetailDO = new LoadRequestDetailDO();
		}
		else if(localName.equalsIgnoreCase("VanStocks"))
		{
			vecVanStocks=new Vector<VanStockDO>();
			PARSE_TYPE = TYPE_VANSTOCK_DETAILS;
		}
		else if(localName.equalsIgnoreCase("VanStockDco"))
		{
			vanStockDO = new VanStockDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		/*if(localName.equalsIgnoreCase("ServerTime"))
			preference.saveStringInPreference(ServiceURLs.GetAllMovements_Sync+empNo+Preference.LAST_SYNC_TIME, currentValue.toString());
		else */
		if(localName.equalsIgnoreCase("Status"))
		{
			synLogDO.action = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ServerTime"))
		{
			synLogDO.TimeStamp = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			synLogDO.UPMJ = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			synLogDO.UPMT = currentValue.toString();
		}
		//11-03-2014 Adding a new parameter with respect to new syncing parser i.e AppActiveStatusResponse 
		else if(localName.equalsIgnoreCase("GetAppActiveStatusResult")||localName.equalsIgnoreCase("AppActiveStatusResponse"))
		{
			insertMovementDetailsDetail();
			synLogDO.entity = ServiceURLs.GetAppActiveStatus;
			new SynLogDA().insertSynchLog(synLogDO);
		}
		switch (PARSE_TYPE) 
		{
			case TYPE_MOVEMENT_HEADER:
				if(localName.equalsIgnoreCase("MovementCode"))
				{
					movementCode1 = currentValue.toString();
					loadRequestDO.MovementCode = currentValue.toString();
					
					/*********Need To Check*********/
					loadRequestDO.PreMovementCode=	""+loadRequestDO.MovementCode;
					loadRequestDO.Status	     = "1";
					
					loadRequestDO.ApproveByCode  =	"0";
					loadRequestDO.ISFromPC		 =	"0";
					loadRequestDO.OperatorCode	 =	"0";
					loadRequestDO.IsDummyCount	 =	"0";
					/**************************************/
				}
				else if(localName.equalsIgnoreCase("OrgCode"))
				{
					loadRequestDO.OrgCode		=	currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("UserCode"))
				{
					loadRequestDO.UserCode		=	currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("WHKeeperCode"))
				{
					loadRequestDO.WHKeeperCode	=	currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("CurrencyCode"))
				{
					loadRequestDO.CurrencyCode	=	currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("JourneyCode"))
				{
					loadRequestDO.JourneyCode	=	currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("MovementDate"))
				{
					loadRequestDO.MovementDate	=	currentValue.toString();
					
					/*********Need To Check*********/
					loadRequestDO.ApprovedDate	=	loadRequestDO.MovementDate;
					loadRequestDO.JDETRXNumber	=	loadRequestDO.MovementCode;
					loadRequestDO.ISStampDate	=	loadRequestDO.MovementDate;
					loadRequestDO.PushedOn		=	loadRequestDO.MovementDate;
					loadRequestDO.ModifiedOn	=	loadRequestDO.MovementDate;
					/**************************************/
				}
				else if(localName.equalsIgnoreCase("MovementNote"))
				{
					loadRequestDO.MovementNote	=	currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("MovementType"))
				{
					loadRequestDO.MovementType	=	currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("SourceVehicleCode"))
				{
					loadRequestDO.SourceVehicleCode	=	currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("DestinationVehicleCode"))
				{
					loadRequestDO.DestinationVehicleCode	=	currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("VisitID"))
				{
					loadRequestDO.VisitID			=	currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("CreatedOn"))
				{
					loadRequestDO.CreatedOn			=	currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Amount"))
				{
					loadRequestDO.Amount			=	StringUtils.getFloat(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("UserLoadType"))
				{
					loadRequestDO.UserLoadType		=	currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("_MovementStatus"))
				{
				}
				else if(localName.equalsIgnoreCase("MovementStatus"))
				{
					loadRequestDO.MovementStatus =currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("MovementHeaderDco"))
				{
					hashMap.put(movementCode1, loadRequestDO);
				}
				
//				loadRequestDO.PreMovementCode=	""+loadRequestDO.MovementCode;
//				loadRequestDO.AppMovementId	=	""+StringUtils.getUniqueUUID();
//				
//				loadRequestDO.ApproveByCode	=	"0";
//				loadRequestDO.ISFromPC		=	"0";
//				loadRequestDO.OperatorCode	=	"0";
//				loadRequestDO.IsDummyCount	=	"0";
				
//				loadRequestDO.ModifiedDate	=	CalendarUtils.getOrderPostDate();
//				loadRequestDO.ModifiedTime	=	CalendarUtils.getRetrunTime();
				
//				loadRequestDO.PushedOn		=	loadRequestDO.MovementDate;
//				loadRequestDO.ModifiedOn	=	loadRequestDO.MovementDate;
//				loadRequestDO.ApprovedDate	=	loadRequestDO.MovementDate;
//				loadRequestDO.JDETRXNumber	=	loadRequestDO.MovementCode;
//				loadRequestDO.ISStampDate	=	loadRequestDO.MovementDate;
//				
				break;
			case TYPE_MOVEMENT_DETAILS:
				if(localName.equalsIgnoreCase("LineNo"))
				{
					loadRequestDetailDO.LineNo = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("MovementCode"))
				{
					movementCode2 = currentValue.toString();
					/*********Need To Check*********/
					loadRequestDetailDO.MovementStatus	=	"Approved";
					loadRequestDetailDO.Status			=	"Approved";
					/**************************************/
					loadRequestDetailDO.MovementCode = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("ItemCode"))
				{
					loadRequestDetailDO.ItemCode = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("OrgCode"))
				{
					loadRequestDetailDO.OrgCode = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("ItemDescription"))
				{
					loadRequestDetailDO.ItemDescription = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("ItemAltDescription"))
				{
					loadRequestDetailDO.ItemAltDescription = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("UOM"))
				{
					loadRequestDetailDO.UOM = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("QuantityLevel1"))
				{
					loadRequestDetailDO.QuantityLevel1 = StringUtils.getInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("QuantityLevel2"))
				{
					loadRequestDetailDO.QuantityLevel2 = StringUtils.getInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("QuantityLevel3"))
				{
					loadRequestDetailDO.QuantityLevel3 = StringUtils.getInt(currentValue.toString());
				}
				
				else if(localName.equalsIgnoreCase("InProcessQuantityLevel1"))
				{
					loadRequestDetailDO.inProcessQuantityLevel1 = StringUtils.getInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("InProcessQuantityLevel2"))
				{
					loadRequestDetailDO.inProcessQuantityLevel2 = StringUtils.getInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("InProcessQuantityLevel3"))
				{
					loadRequestDetailDO.inProcessQuantityLevel3 = StringUtils.getInt(currentValue.toString());
				}
				
				else if(localName.equalsIgnoreCase("ShippedQuantityLevel1"))
				{
					loadRequestDetailDO.shippedQuantityLevel1 = StringUtils.getInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("ShippedQuantityLevel2"))
				{
					loadRequestDetailDO.shippedQuantityLevel2 = StringUtils.getInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("ShippedQuantityLevel3"))
				{
					loadRequestDetailDO.shippedQuantityLevel3 = StringUtils.getInt(currentValue.toString());
				}
				
				
				else if(localName.equalsIgnoreCase("NonSellableQty"))
				{
					loadRequestDetailDO.NonSellableQty = StringUtils.getInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("QuantityBU"))
				{
					loadRequestDetailDO.QuantityBU = StringUtils.getInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("QuantitySU"))
				{
					loadRequestDetailDO.QuantitySU = StringUtils.getInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("CurrencyCode"))
				{
					loadRequestDetailDO.CurrencyCode = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("PriceLevel1"))
				{
					loadRequestDetailDO.PriceLevel1 = StringUtils.getFloat(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("PriceLevel2"))
				{
					loadRequestDetailDO.PriceLevel2 = StringUtils.getFloat(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("PriceLevel3"))
				{
					loadRequestDetailDO.PriceLevel3 = StringUtils.getFloat(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("MovementReasonCode"))
				{
					loadRequestDetailDO.MovementReasonCode = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("ExpiryDate"))
				{
					loadRequestDetailDO.ExpiryDate = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("CreatedOn"))
				{
					loadRequestDetailDO.CreatedOn = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("MovementType"))
				{
				}
				else if(localName.equalsIgnoreCase("_MovementStatus"))
				{
				}
				else if(localName.equalsIgnoreCase("MovementStatus"))
				{
//					loadRequestDetailDO.MovementStatus	=	"Approved";
				}
				else if(localName.equalsIgnoreCase("CancelledQuantity"))
				{
					loadRequestDetailDO.CancelledQuantity =	 StringUtils.getInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("InProcessQuantity"))
				{
					loadRequestDetailDO.InProcessQuantity =	 StringUtils.getInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("ShippedQuantity"))
				{
					loadRequestDetailDO.ShippedQuantity =	 StringUtils.getInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("MovementDetailDco"))
				{
					if(hashMap.containsKey(movementCode2))
					{
						LoadRequestDO requestDO = hashMap.get(movementCode2);
						if(requestDO.vecItems == null)
							requestDO.vecItems = new ArrayList<LoadRequestDetailDO>();
						requestDO.vecItems.add(loadRequestDetailDO);
						hashMap.put(movementCode2, requestDO);
					}
				}
				
				break;
				
			case TYPE_VANSTOCK_DETAILS:
				 if(localName.equalsIgnoreCase("ItemCode"))
				 {
						vanStockDO.ItemCode = currentValue.toString();
				 }
				 else if(localName.equalsIgnoreCase("UserCode"))
				 {
						vanStockDO.UserCode = currentValue.toString();
				 }
				 else if(localName.equalsIgnoreCase("QuantityEach"))
				 {
						vanStockDO.QuantityEach = StringUtils.getInt(currentValue.toString());
				 }
				 else if(localName.equalsIgnoreCase("AvailableQuantity"))
				 {
						vanStockDO.AvailableQuantity = StringUtils.getInt(currentValue.toString());
				 }
				 else if(localName.equalsIgnoreCase("SellableQuantity"))
				 {
						vanStockDO.SellableQuantity =  StringUtils.getInt(currentValue.toString());
				 }
				 else if(localName.equalsIgnoreCase("ReturnedQuantity"))
				 {
						vanStockDO.ReturnedQuantity =  StringUtils.getInt(currentValue.toString());
				 }
				 else if(localName.equalsIgnoreCase("TotalQuantity"))
				 {
						vanStockDO.TotalQuantity =  StringUtils.getInt(currentValue.toString());
				 }
				 else if(localName.equalsIgnoreCase("BatchNumber"))
				 {
						vanStockDO.BatchNumber = currentValue.toString();
				 }
				 else if(localName.equalsIgnoreCase("ExpiryDate"))
				 {
						vanStockDO.ExpiryDate = currentValue.toString();
				 }
				 else if(localName.equalsIgnoreCase("VanStockDco"))
				 {
					 vecVanStocks.add(vanStockDO);
				 }
				 
				break;
				
			default:
				break;
		}
	}
	
	private void insertMovementDetailsDetail()
	{
		InventoryDA inventoryDA = new InventoryDA();
		inventoryDA.insertLoadRequests(hashMap);
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
}
