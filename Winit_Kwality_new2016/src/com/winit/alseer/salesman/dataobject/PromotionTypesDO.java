package com.winit.alseer.salesman.dataobject;


@SuppressWarnings("serial")
public class PromotionTypesDO extends BaseComparableDO
{
	public String PromotionTypeId;
	public String Description;
	
	//Promotion Types
	private static final int INSTANT_PROMOTIONS=100;
	private static final int STRUCTURAL_PROMOTIONS=101;
	private static final int SLAB_PROMOTIONS=102;
	private static final int BUNDLE_PROMOTIONS=104;
	private static final int INVOICE_PROMOTIONS=105;
	private static final int FOC_PROMOTION=106;
	public static int get_TYPE_INSTANT_PROMOTIONS(){
		return INSTANT_PROMOTIONS;
	}
	public static int get_TYPE_FOC_PROMOTION(){
		return FOC_PROMOTION;
	}
	public static int get_TYPE_STRUCTURAL_PROMOTIONS(){
		return STRUCTURAL_PROMOTIONS;
	}
	
	public static int get_TYPE_SLAB_PROMOTIONS(){
		return SLAB_PROMOTIONS;
	}
	
	public static int get_TYPE_BUNDLE_PROMOTIONS(){
		return BUNDLE_PROMOTIONS;
	}
	
	public static int get_TYPE_INVOICE_PROMOTIONS(){
		return INVOICE_PROMOTIONS;
	}
}
