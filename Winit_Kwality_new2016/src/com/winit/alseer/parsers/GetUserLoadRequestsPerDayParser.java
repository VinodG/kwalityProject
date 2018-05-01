package com.winit.alseer.parsers;

import java.util.ArrayList;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.InventoryDA;
import com.winit.alseer.salesman.dataobject.LoadRequestDO;
import com.winit.alseer.salesman.dataobject.LoadRequestDetailDO;
import com.winit.alseer.salesman.dataobject.VanStockDO;
import com.winit.alseer.salesman.utilities.StringUtils;

public class GetUserLoadRequestsPerDayParser extends BaseHandler
{

	private Preference preference;
	private StringBuilder currentValue ;
	private boolean currentElement = false;
	private ArrayList<LoadRequestDO> arrLoadRequestDO = null;
	private LoadRequestDO objLoadRequestDO = null;
	private LoadRequestDetailDO objLoadRequestDetailDO = null;
	private boolean isMovementType = false, detailMovementCode = false;
	
	public GetUserLoadRequestsPerDayParser(Context context) 
	{
		super(context);
		preference 		= 	new Preference(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue  = new StringBuilder();
		if(localName.equalsIgnoreCase("MovementHeaders"))
		{
			arrLoadRequestDO = new ArrayList<LoadRequestDO>();
		}
		else if(localName.equalsIgnoreCase("MovementHeaderDco"))
		{
			objLoadRequestDO = new LoadRequestDO();
		}
		else if(localName.equalsIgnoreCase("MovementDetailDcos")) 
		{
			objLoadRequestDO.vecItems = new ArrayList<LoadRequestDetailDO>();
		}
		else if(localName.equalsIgnoreCase("MovementDetailDco")) 
		{
			objLoadRequestDetailDO = new LoadRequestDetailDO();
			isMovementType = true;
			detailMovementCode = true;
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("MovementCode"))
		{
			if(!detailMovementCode)
				objLoadRequestDO.MovementCode = currentValue.toString();
			else
				objLoadRequestDetailDO.MovementCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PreMovementCode"))
		{
			objLoadRequestDO.PreMovementCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AppMovementId"))
		{
			objLoadRequestDO.AppMovementId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("OrgCode"))
		{
			objLoadRequestDO.OrgCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("UserCode"))
		{
			objLoadRequestDO.UserCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("WHKeeperCode"))
		{
			objLoadRequestDO.WHKeeperCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CurrencyCode"))
		{
			objLoadRequestDO.CurrencyCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("JourneyCode"))
		{
			objLoadRequestDO.JourneyCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MovementDate"))
		{
			objLoadRequestDO.MovementDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MovementNote"))
		{
			objLoadRequestDO.MovementNote = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MovementType"))
		{
			if(!isMovementType)
				objLoadRequestDO.MovementType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ProductType"))
		{
			objLoadRequestDO.ProductType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SourceVehicleCode"))
		{
			objLoadRequestDO.SourceVehicleCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("DestinationVehicleCode"))
		{
			objLoadRequestDO.DestinationVehicleCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("VisitID"))
		{
			objLoadRequestDO.VisitID = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CreatedOn"))
		{
			objLoadRequestDO.CreatedOn = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Amount"))
		{
			objLoadRequestDO.Amount = StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("WHCode"))
		{
			objLoadRequestDO.WHCode = currentValue.toString();
		}
		/*else if(localName.equalsIgnoreCase("SalesmanSignature"))
		{
			objLoadRequestDO.SalesmanSignature = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("WHManagerSignature"))
		{
			objLoadRequestDO.WHManagerSignature = currentValue.toString();
		}*/
		else if(localName.equalsIgnoreCase("UserLoadType"))
		{
			objLoadRequestDO.UserLoadType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("_MovementStatus"))
		{
			if(!detailMovementCode)
				objLoadRequestDO.MovementStatus = currentValue.toString();
			else
				objLoadRequestDetailDO.MovementStatus = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MovementStatus"))
		{
			if(!detailMovementCode)
				objLoadRequestDO.MovementStatus = currentValue.toString();
			else
				objLoadRequestDetailDO.MovementStatus = currentValue.toString();
		}
		
		////////////////////////////////////
		else if(localName.equalsIgnoreCase("LineNo"))
		{
			objLoadRequestDetailDO.LineNo = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MovementCode"))
		{
			objLoadRequestDetailDO.MovementCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ItemCode"))
		{
			objLoadRequestDetailDO.ItemCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("OrgCode"))
		{
			objLoadRequestDetailDO.OrgCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ItemDescription"))
		{
			objLoadRequestDetailDO.ItemDescription = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ItemAltDescription"))
		{
			objLoadRequestDetailDO.ItemAltDescription = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("UOM"))
		{
			objLoadRequestDetailDO.UOM = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("QuantityLevel1"))
		{
			objLoadRequestDetailDO.QuantityLevel1 = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("QuantityLevel2"))
		{
			objLoadRequestDetailDO.QuantityLevel2 = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("QuantityLevel3"))
		{
			objLoadRequestDetailDO.QuantityLevel3 = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("NonSellableQty"))
		{
			objLoadRequestDetailDO.NonSellableQty = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("QuantityBU"))
		{
			objLoadRequestDetailDO.QuantityBU = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("CurrencyCode"))
		{
			objLoadRequestDetailDO.CurrencyCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PriceLevel1"))
		{
			objLoadRequestDetailDO.PriceLevel1 = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("PriceLevel2"))
		{
			objLoadRequestDetailDO.PriceLevel2 = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("PriceLevel3"))
		{
			objLoadRequestDetailDO.PriceLevel3 = StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("MovementReasonCode"))
		{
			objLoadRequestDetailDO.MovementReasonCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CreatedOn"))
		{
			objLoadRequestDetailDO.CreatedOn = currentValue.toString();
		}
//		else if(localName.equalsIgnoreCase("MovementType"))
//		{
//			objLoadRequestDetailDO.MovementType = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("_MovementStatus"))
//		{
//			objLoadRequestDetailDO.MovementStatus = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("MovementStatus"))
//		{
//			objLoadRequestDetailDO.MovementStatus = currentValue.toString();
//		}
		else if(localName.equalsIgnoreCase("CancelledQuantity"))
		{
			objLoadRequestDetailDO.CancelledQuantity = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("InProcessQuantity"))
		{
			objLoadRequestDetailDO.InProcessQuantity = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ShippedQuantity"))
		{
			objLoadRequestDetailDO.ShippedQuantity = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ExpiryDate"))
		{
			objLoadRequestDetailDO.ExpiryDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("InProcessQuantityLevel1"))
		{
			objLoadRequestDetailDO.inProcessQuantityLevel1 = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("InProcessQuantityLevel2"))
		{
			objLoadRequestDetailDO.inProcessQuantityLevel2 = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("InProcessQuantityLevel3"))
		{
			objLoadRequestDetailDO.inProcessQuantityLevel3 = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ShippedQuantityLevel1"))
		{
			objLoadRequestDetailDO.shippedQuantityLevel1 = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ShippedQuantityLevel2"))
		{
			objLoadRequestDetailDO.shippedQuantityLevel2 = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ShippedQuantityLevel3"))
		{
			objLoadRequestDetailDO.shippedQuantityLevel3 = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("MovementDetailDco"))
		{
			objLoadRequestDO.vecItems.add(objLoadRequestDetailDO);
			isMovementType = false;
			detailMovementCode = false;
		}
		//////////////////////////////////////
		
		else if(localName.equalsIgnoreCase("MovementHeaderDco"))
		{
			arrLoadRequestDO.add(objLoadRequestDO);
		}
		else if(localName.equalsIgnoreCase("MovementHeaders"))
		{
			insertData();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}

	private void insertData() 
	{
		Vector<VanStockDO> vecVanStockDOs = new Vector<VanStockDO>();
		VanStockDO objVanstDo = null;
		for(LoadRequestDO objloadLoadRequestDO : arrLoadRequestDO)
		{
			for(LoadRequestDetailDO objloadLoadRequestDetailDO : objloadLoadRequestDO.vecItems)
			{
				if(objloadLoadRequestDetailDO.MovementStatus.equalsIgnoreCase(""+LoadRequestDO.MOVEMENT_STATUS_APPROVED_VERIFY))
				{
					objVanstDo = new VanStockDO();
					objVanstDo.TrxCode	=	objloadLoadRequestDetailDO.MovementCode;
					objVanstDo.OrgCode = objloadLoadRequestDetailDO.OrgCode;
					objVanstDo.ItemCode = objloadLoadRequestDetailDO.ItemCode;
					objVanstDo.QuantityEach = objloadLoadRequestDetailDO.shippedQuantityLevel1;
					objVanstDo.ExpiryDate = objloadLoadRequestDetailDO.ExpiryDate;
					
					vecVanStockDOs.add(objVanstDo);
				}
			}
		}
		new InventoryDA().updateVanLoad(vecVanStockDOs);
		new InventoryDA().insertLoadRequests(arrLoadRequestDO);
	}
}
