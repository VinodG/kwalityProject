package com.winit.alseer.salesman.dataobject;

import java.util.HashMap;
import java.util.Vector;

public class TrxDetailsDO extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int affectedStock=0;
	public int approvedBU=0;
	public float basePrice=0;
	public float calculatedDiscountAmount=0; ////statement discount amount
	public float calculatedstatementDiscountAmount=0;
	public float calculatedDiscountPercentage=0; //statement discount%
	public float calculatedstatementDiscountPercentage=0;
	public int cancelledQuantity=0;
	public int collectedBU=0;
	public String createdOn="";
	public String distributionCode = "";
	public String expiryDate = "";
	public int finalBU=0;
	public int inProcessQuantity=0;
	public String itemAltDescription="";
	public String itemCode="";
	public String itemDescription="";
	public String itemGroupLevel5="";//Brand ID
	public String itemType="";
	public int lineNo=0;
	public String modifiedDate="";
	public String modifiedTime="";
	public String orgCode="";
	public float priceUsedLevel1=0;
	public float vatPercentage=0;
	public float vatPercentagebackup=0;
	public float priceUsedLevel2=0;
	public float priceUsedLevel3=0;
	public int promoID=0;
	public String promoType = "";
	public String pushedOn="";
	public int quantityBU=0;
	public float quantityLevel1=0;
	public float quantityLevel2=0;
	public int quantityLevel3=0;
	public float quantityRecommended;//Recommended qty is always in units only
	public String recommendedUOM;
	public String reason = "";
	public int relatedLineID=0;
	public int requestedBU=0;//for return order
	public int shippedQuantity=0;
	public int status=0;
	public int suggestedBU=0;
	public float taxPercentage=0;
	public int taxType=0;
	public float totalDiscountAmount=0;
	public float promotionalDiscountAmount=0; //invoice /promodiscount amount
	public float statementDiscountAmnt=0;
	public float totalDiscountPercentage=0;  //invoice /promodiscount %
	public String trxCode="";
	public String trxDetailsNote = "";
	public String trxReasonCode = "";
	public int trxStatus=0;
	public String UOM="";
	public int uomLevelUsed=-1;
	public float userDiscountAmount=0;
	public float userDiscountPercentage=0;
	public int missedBU=0;
	public float requestedSalesBU=0;//to hold total requested qty by customer for sales order. from this value we need to calculate missedBU
	public HashMap<String,UOMConversionFactorDO> hashArrUoms = new HashMap<String, UOMConversionFactorDO>();
	public Vector<String> arrUoms = new Vector<String>();
	//category
	public String categoryId="";
	public String subcategory="";
	public String brandId="";
	public String categoryName="";
	
	public int DisplayOrder = -1;
	
	//added due to design
	public String agencyName="";
	
	//Need to ask Venky sir
	public String batchCode="";
	
	//Need to ask vinay
	public int productStatus=0;
	
	//To hold number of images for GRV
	public Vector<DamageImageDO> vecDamageImages;
	public String brandName = "";
	public String brandImage = "";
	public String packging;
	
	public float EAPrice=0;
	public float CSPrice=0;
	
	/**BY ANIL 10 Jan, 2015
	 * if missed quanity is there then we need to split into selected UOM and lowest UOM
	 * Note : These varibles are only created for display purpose please don't use in any calculation, 
	 * It may cause issue if any one try to use for calculation
	 *  
	 */
	public int displayQtyInSelectedUOM=0;
	public int displayQtyInLowestUOM=0;
	public String barCode 			=	"";

//	public String VATPer 			=	""; //vinod
//	public String VATAmount 		=	""; //vinod
	public float VATAmountNew 		=	0.0f; //vinod


	
	
	private static final String TRX_ITEM_SELLABLE = "G";
    private static final String TRX_ITEM_NON_SELLABLE = "D";
    private static final String TRX_FOC_ITEM = "F";
    private static final String TRX_NOREMAL_ITEM = "O";
    public static String get_TRX_ITEM_SELLABLE()
    {
    	return TRX_ITEM_SELLABLE;
    }
    public static String get_TRX_NOREMAL_ITEM()
    {
    	return TRX_NOREMAL_ITEM;
    }
    public static String get_TRX_FOC_ITEM()
    {
    	return TRX_FOC_ITEM;
    }
    public static String get_TRX_ITEM_NON_SELLABLE()
    {
    	return TRX_ITEM_NON_SELLABLE;
    }
    
    //==========================UOM Levels++++++++++++++++++++++++
    public static final int ITEM_UOM_LEVELINT1=1;//top level UOM
    public static final int ITEM_UOM_LEVELINT2=2;//middle level UOM
    public static final int ITEM_UOM_LEVELINT3=3;//bottom level UOM
    
    private static final String ITEM_UOM_LEVEL1="UNIT";//top level UOM
    private static final String ITEM_UOM_LEVEL2="Outer";//middle level UOM- this is not available for now
    public static final String ITEM_UOM_LEVEL3="PCS";//bottom level UOM
    
    public static String getItemUomLevel1()
    {
    	return ITEM_UOM_LEVEL1;
    }
    public static String getItemUomLevel2()
    {
    	return ITEM_UOM_LEVEL2;
    }
    public static String getItemUomLevel3()
    {
    	return ITEM_UOM_LEVEL3;
    }
}
