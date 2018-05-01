package com.winit.alseer.salesman.dataobject;


public class VanLoadDO extends BaseObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String CategoryId		= "";
	public String subCategoryId		= "";
	public float SellableQuantity	= 0;
	public float SellableCase	= 0;
	public float TotalQuantity		= 0;	
	public String ItemCode			= "";
	public String Description		= "";
	public int UnitsPerCases		= 0;
	public String BatchCode			= "";
	public String ExpiryDate		= "";
	public String UOM				= "";
	public int inventoryQty;
	public String unitSellingPrice  = "";
	public float invoiceAmount      = 0;
	public float discountAmount  	= 0;
	public String customerPriceClass = "";
	public String itemType			= "";
	public boolean ischecked = false;
	
	public boolean itemChecked = false;
	public float CancelledQuantity	= 0;
	public int ShippedQuantity;
	
	public int TotalQuantityToUnload	= 0;
	
	public String MovementReasonCode = "";
	
	public String CreatedOn = "";
	
	public String MovementType = "";
	
	public float inProccessQty = 0;
	
	
	public int quantityLevel1=0;
	public int quantityLevel2=0;
	public int quantityLevel3=0;
	
	public int inProcessQuantityLevel1=0;
	public int inProcessQuantityLevel2=0;
	public int inProcessQuantityLevel3=0;
	
	public int shippedQuantityLevel1=0;
	public int shippedQuantityLevel2=0;
	public int shippedQuantityLevel3=0;
	
	public int eaConversion = 0;
	public String HighlightItem		= ""; 
	public String RecomendedLoadQuantity		= "";
	
	public String IsActive	= "";
	
	public String UserCode = "";  
}
