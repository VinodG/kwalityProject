package com.winit.alseer.salesman.dataobject;

@SuppressWarnings("serial")
public class LoadRequestDetailDO extends BaseComparableDO
{
	public String LineNo="";
	public String MovementCode="";
	public String ItemCode="";
	public String OrgCode="";
	public String ItemDescription="";
	public String ItemAltDescription="";
	public String MovementStatus="";
	public String UOM="";
	public int QuantityLevel1=0;
	public int QuantityLevel2=0;
	public int QuantityLevel3=0;
	
	public int inProcessQuantityLevel1=0;
	public int inProcessQuantityLevel2=0;
	public int inProcessQuantityLevel3=0;
	
	public int shippedQuantityLevel1=0;
	public int shippedQuantityLevel2=0;
	public int shippedQuantityLevel3=0;
	
	public int QuantityBU=0;
	public int QuantitySU=0;
	public int NonSellableQty=0;
	public String CurrencyCode="";
	public float PriceLevel1=0;
	public float PriceLevel2=0;
	public float PriceLevel3=0;
	public String MovementReasonCode="";
	public String ExpiryDate="";
	public String Note="";
	public String AffectedStock="";
	public String Status="";
	public String DistributionCode="";
	public String CreatedOn="";
	public String ModifiedDate="";
	public String ModifiedTime="";
	public String PushedOn="";
	public int CancelledQuantity=0;
	public int InProcessQuantity=0;
	public int ShippedQuantity=0;
	public String ModifiedOn="";
}
