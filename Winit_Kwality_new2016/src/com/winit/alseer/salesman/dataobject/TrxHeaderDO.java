package com.winit.alseer.salesman.dataobject;

import com.winit.alseer.salesman.common.AppConstants;

import java.util.ArrayList;

public class TrxHeaderDO extends BaseObject implements Cloneable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String approveByCode;
    public String approvedDate;
    public String appTrxId;
    public String branchPlantCode;
    public String clientBranchCode;
    public String clientCode;
    public String clientSignature;
    public String createdOn;
    public String currencyCode;
    public String deliveryDate;
    public String freeNote="";
    public String journeyCode;
    public String lPOCode = "";
    public String modifiedDate;
    public String orgCode;
    public int paymentType;
    public String preTrxCode;
    public int printingTimes;
    public String pushedOn;
    public String referenceCode = "";
    public float remainingAmount;
    public String salesmanSignature;
    public int status;
    public float totalAmount;
    public float totalVATAmount;
    public float totalDiscountAmount;
    public float totalTAXAmount;
    public String trxCode;
    public String trxDate;
    public String trxReasonCode = "";
    public int trxStatus;
    public int trxType;
    public int trxSubType;
    public String updatedOn;
    public String oriSRINVNO;
    public String oriSRINVDATE;
    public String userCode;
    public String userCreditAccountCode= "";
    public String visitCode;
    public float specialDiscPercent = 0.0f;
    public float specialDiscount=0.0f;
    
    public ArrayList<TrxDetailsDO> arrTrxDetailsDOs   = new ArrayList<TrxDetailsDO>();
    public ArrayList<TrxPromotionDO> arrPromotionDOs  = new ArrayList<TrxPromotionDO>();
    
    public String siteName = "";
    public String address  = "";
    
    public String LPONo = "";
    public String Narration = "";
    public String returnReason = "";
    public float rateDiff = 0;
    
    public double geoLatitude = 0.0;
    public double geoLongitude = 0.0;
	public String reason = "";
	public String promotionalDiscount = "";
	public String PreSellerUserCode = "";
    public String PromotionReason = "";
	public int Division = 0;
	public String statementDiscount="";
	public float statementdiscountvalue;
    
    private static final int TRX_TYPE_ALL   = 0;
    private static final int TRX_TYPE_CART   		= AppConstants.CART_TYPE;
   
    /*
     * Types
     */
    public static final int TRX_TYPE_SALES_ORDER   = 1;
    public static final int TRX_TYPE_ADVANCE_ORDER = 3;
    public static final int TRX_TYPE_PRE_SALES_ORDER = 5;
//    private static final int TRX_TYPE_SALES_ORDER   = 1;
//    private static final int TRX_TYPE_ADVANCE_ORDER = 3;
//    public static final int TRX_TYPE_PRE_SALES_ORDER = 5;
    private static final int TRX_TYPE_GRV  = 4;

    private static final int TRX_TYPE_SAVED_ORDER = 10;//not related to backend
    private static final int TRX_TYPE_MISSED_ORDER  = 500;//not related to backend
    private static final int TRX_TYPE_FREE_DELIVERY  = 6;
    private static final int TRX_TYPE_FREE_ORDER  = 7;

    /*
     * Sub-Types
     */
    private static final int TRX_SUB_TYPE_SALES_ORDER   = 0;
    private static final int TRX_SUB_TYPE_SAVED_ORDER   = 1;
    private static final int TRX_SUB_TYPE_TELE_ORDER   = 2;
    

    /*
     * Trx Status
     */
//    private static final int TRX_STATUS_SALES_ORDER_DELIVERED  = 200;
    public static final int TRX_STATUS_SALES_ORDER_DELIVERED  = 200;
    public static final int TRX_STATUS_ADVANCE_ORDER_DELIVERED  = 3;
    public static final int TRX_STATUS_PRESALES_ORDER  = 100;
    public static final int TRX_STATUS_SAVED  	   = 0;
    private static final int TRX_STATUS_CANCELLED  = -100;
    private static final int TRX_STATUS_DELETED    = -150;
    
    private static final int TRX_STATUS_GRV_PENDING     = 1;
    public static final int TRX_STATUS_GRV_APPROVED    = 2;
    
    /*
     * Data Push Status (Status Column in tblTrxHeader)
     */
    
    private static final int TRX_UPLOAD_STATUS = 1;
    private static final int TRX_DATA_PENDING_STATUS = 0;
    private static final int TRX_DATA_PENDING_STATUS_UNKNOWN = 2;
    private static final int TRX_DATA_PUSHED_STATUS = 1;
    private static final int TRX_IMAGE_UPLOADED_STATUS = 100;

    /*
     *  WareHouse Status
     */
    private static final int TRX_STATUS_WAREHOUSE = 1002;
    private static final int TRX_STATUS_DELIVERY = 1003;
    
    /*
     *  App Status For identifying saved modified and saved finlaized
     */
    public static final int TRX_STATUS_SAVED_MODIFY = 1004;
    private static final int TRX_STATUS_SAVED_FINALIZED = 1005;
    
    /*
     *  Payment Type
     */
    private static final int TRX_PAYMENT_TYPE_CREDIT = 0;
    private static final int TRX_PAYMENT_TYPE_CASH = 1;

    public static int get_TRX_PAYMENT_TYPE_CREDIT()
    {
    	return TRX_PAYMENT_TYPE_CREDIT;
    }
    
    public static int get_TRX_PAYMENT_TYPE_CASH()
    {
    	return TRX_PAYMENT_TYPE_CASH;
    }
    
    public static int get_TRX_STATUS_PRESALES_ORDER()
    {
    	return TRX_STATUS_PRESALES_ORDER;
    }
    
    public static int get_TRX_STATUS_SALES_ORDER_DELIVERED()
    {
    	return TRX_STATUS_SALES_ORDER_DELIVERED;
    }
    
    public static int get_TRX_STATUS_ADVANCE_ORDER_DELIVERED()
    {
    	return TRX_STATUS_ADVANCE_ORDER_DELIVERED;
    }
    
    
    public static int get_TRX_STATUS_SAVED_FINALIZED()
    {
    	return TRX_STATUS_SAVED_FINALIZED;
    }
    
    public static int get_TRX_STATUS_SAVED_MODIFIED()
    {
    	return TRX_STATUS_SAVED_MODIFY;
    }
    
    public static int get_TRX_STATUS_CANCELLED()
    {
    	return TRX_STATUS_CANCELLED;
    }
    
    public static int get_TRX_STATUS_WAREHOUSE()
    {
    	return TRX_STATUS_WAREHOUSE;
    }
    
    public static int get_TRX_STATUS_DELIVERY()
    {
    	return TRX_STATUS_DELIVERY;
    }
    
    public static int get_TRXTYPE_ALL()
    {
    	return TRX_TYPE_ALL;
    }
    
    public static int get_TRX_Upload_STATUS()
    {
    	return TRX_UPLOAD_STATUS;
    }
    
    public static int get_TRX_DATA_PENDING_STATUS()
    {
    	return TRX_DATA_PENDING_STATUS;
    }
    public static int get_TRX_DATA_PENDING_STATUS_UNKNOWN()
    {
    	return TRX_DATA_PENDING_STATUS_UNKNOWN;
    }
    
    public static int get_TRX_DATA_PUSHED_STATUS()
    {
    	return TRX_DATA_PUSHED_STATUS;
    }
    
    public static int get_TRX_Image_Upload_STATUS()
    {
    	return TRX_IMAGE_UPLOADED_STATUS;
    }
    
    public static int get_TRXTYPE_SALES_ORDER()
    {
    	return TRX_TYPE_SALES_ORDER;
    }
    
    public static int get_TRXTYPE_PRESALES_ORDER()
    {
    	return TRX_TYPE_PRE_SALES_ORDER;
    }
    
    public static int get_TRXTYPE_RETURN_ORDER()
    {
    	return TRX_TYPE_GRV;
    }
    
    public static int get_TRX_SUBTYPE_SALES_ORDER()
    {
    	return TRX_SUB_TYPE_SALES_ORDER;
    }
    
    public static int get_TRX_SUBTYPE_SAVED_ORDER()
    {
    	return TRX_SUB_TYPE_SAVED_ORDER;
    }
    
    public static int get_TRX_SUBTYPE_TELE_ORDER()
    {
    	return TRX_SUB_TYPE_TELE_ORDER;
    }

    public static int get_TRXTYPE_RETURN_ORDER_STATUS_PENDING()
    {
    	return TRX_STATUS_GRV_PENDING;
    }

    public static int get_TRXTYPE_RETURN_ORDER_STATUS_APPROVED()
    {
    	return TRX_STATUS_GRV_APPROVED;
    }

    public static int get_TRXTYPE_RETURN_ORDER_STATUS_REJECTED()
    {
    	return TRX_STATUS_CANCELLED;
    }
    
    public static int get_TRXTYPE_MISSED_ORDER()
    {
    	return TRX_TYPE_MISSED_ORDER;
    }
    
    public static int get_TRXTYPE_ADVANCE_ORDER()
    {
    	return TRX_TYPE_ADVANCE_ORDER;
    }
    
    public static int get_TRXTYPE_SAVED_ORDER()
    {
    	return TRX_TYPE_SAVED_ORDER;
    }
    
    public static int get_TRXTYPE_CART()
    {
    	return TRX_TYPE_CART;
    }
    public static int get_TYPE_FREE_DELIVERY()
    {
    	return TRX_TYPE_FREE_DELIVERY;
    }
    
    public static int get_TYPE_FREE_ORDER()
    {
    	return TRX_TYPE_FREE_ORDER;
    }
    
    public static int get_TRX_STATUS_SAVED()
    {
    	return TRX_STATUS_SAVED;
    }
    
    private static final int DIVISION_ICECREAM = 0;
    private static final int DIVISION_FOOD = 1;
    private static final int DIVISION_THIRD_PARTY= 2;

    public static int get_DIVISION_ICECREAM() {
    	return DIVISION_ICECREAM;
    }
    
    public static int get_DIVISION_FOOD() {
    	return DIVISION_FOOD;
    }
    public static int get_DIVISION_THIRD_PARTY() {
    	return DIVISION_THIRD_PARTY;
    }

    public Object clone(){  
        try{  
            return super.clone();  
        }catch(Exception e){ 
            return null; 
        }
    }
 
}
