package com.winit.alseer.salesman.dataobject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import android.widget.EditText;

@SuppressWarnings("serial")
public class ProductDO implements Serializable, Cloneable
{
	public String LineNo = "";
	public String OrderNo = "" ;
	public String RelatedLineId = "";
	public String ProductId = "";
	public String CategoryId ="" ;
	public String SKU = "";
	public String Description ="";
	public String Description1 ="";
	public int UnitsPerCases;
	public String BatchCode = "";
	public String UOM =""; 
	public String secondaryUOM =""; 
	public String primaryUOM =""; 
	public String CaseBarCode ="";
	public String UnitBarCode;
	public String ItemType;
	public String ItemSubType ="";
	public String IsStructural;
	public String PricingKey;
	public String brand = "";
	public String brandcode = "";
	public String invoiceNo = "";
	public String cases = "";
	public String units = "";
	public String promotionId = "";
	public String promotionType = "";
	
	public String preCases = "0";
	public String preUnits = "0";
	
	public String StoreUOM ="PCS";
	public String PcsQTY = "-1";
	public String UnitQTY = "";
	public int status = 0;
	//Not to modify
	public String ActpreUnits = "0";
	public String ActpreCases = "0";
	public float totalCases = 0;
	
	public int totalUnits = 0;
	public String recomCases = "0.0";
	public float recomUnits = 0;
	public String strExpiryDate = "";
	
	public float deliveredCases;
	public float orderedCases;
	public float inventoryQty=0.0f;
	public int orderedUnits;
	
	public float itemPrice;
	public float totalPrice;
	
	//added by awaneesh for promotion
	public float returnedCases;
	public int returnedUnits;
	public boolean isReccomended = false;
	public boolean isDiscountApplied = false;
	public float Discount = 0;
	public boolean isCaptured = true;
	
	public int discountType =0;//0 == percentage and 1==amount
	
	//added by awaneesh for promotion
	public float actualDiscount = 0;
	public String reason ="";
	public String remarks = "";
	public boolean isMusthave;
	public boolean isFavourite;
	public boolean isNew;
	public int musthaveQty;
	public int missedQty=0;
	public boolean isReturnCategory;
	public float casePrice 		  = 0;
	public float unitSellingPrice = 0;
	public float invoiceAmount    = 0;
	public float discountAmount   = 0;
	
	public float priceUsedLevel1  = 0;
	public float priceUsedLevel2   = 0;
	
	public EditText etCases = null,etUnits = null, etEmptyJar=null;
	public boolean isAdvanceOrder;
	
	public boolean isPromotional = false;
	public float depositPrice;
	public float emptyCasePrice = 0;
	public float DiscountAmt = 0;
	public int PromoLineNo;
	public String promoCode;
	public String TaxGroupCode = "0";
	public float TaxPercentage = 0;
	
	public int Classifications = -1;
	public int DisplayOrder = -1;
	public int productStatus = 0;
	public String categoryName="";
	public String subCategoryName="";
	
	public ArrayList<String> vecDamageImages;
	
	public Vector<DamageImageDO> vecDamageImagesNew;
	public Vector<String> vecUOM = new Vector<String>();
	
	public boolean isPromoOrder = false;
	
	@Override
	public Object clone() throws CloneNotSupportedException 
	{
		ProductDO clone = new ProductDO();
		
		clone.ProductId 	= 	this.ProductId;
		clone.SKU 			= 	this.SKU;
		clone.CategoryId 	=	this.CategoryId;
		clone.Description 	= 	this.Description;
		clone.UnitsPerCases	=	this.UnitsPerCases;
		clone.BatchCode 	= 	this.BatchCode;
		clone.UOM 			= 	this.UOM;
		clone.CaseBarCode 	= 	this.CaseBarCode;
		clone.UnitBarCode	= 	this.UnitBarCode;
		clone.ItemType		= 	this.ItemType;
		clone.PricingKey	= 	this.PricingKey;
		clone.brand			= 	this.brand;
		clone.cases 		= 	this.cases;
		clone.units 		= 	this.units;
		clone.preCases 		= 	this.preCases;
		clone.preUnits 		= 	this.preUnits;
		clone.orderedCases	= 	this.orderedCases;
		clone.orderedUnits	= 	this.orderedUnits;
		clone.itemPrice		= 	this.itemPrice;
		clone.returnedCases	= 	this.returnedCases;
		clone.returnedUnits	= 	this.returnedUnits;
		clone.recomCases	= 	this.recomCases;
		clone.recomUnits	= 	this.recomUnits;
		clone.reason 		= 	this.reason;
		clone.isMusthave	= 	this.isMusthave;
		clone.musthaveQty 	= 	this.musthaveQty;
		clone.categoryName  =   this.categoryName;
		clone.discountAmount=   this.discountAmount;
		clone.strExpiryDate =   this.strExpiryDate;
		clone.primaryUOM 	=   this.primaryUOM;
		return clone;
	}
}
