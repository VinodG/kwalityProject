package com.winit.alseer.salesman.dataobject;

import java.util.ArrayList;

public class PromotionDO {
	public String Description;
	public String PromoItemCode;
	public int PromoItemQty;
	public String FOCItemCode;
	public int FOCItemQty;
	public ArrayList<PromotionDO> arrPromotion=new ArrayList<PromotionDO>();
	public PromotionDO(){
		preparePromotionData();
	}
	public PromotionDO(String Description, String PromoItemCode,
			int PromoItemQty, String FOCItemCode, int FOCItemQty) {
		this.Description = Description;
		this.PromoItemCode = PromoItemCode;
		this.PromoItemQty = PromoItemQty;
		this.FOCItemCode = FOCItemCode;
		this.FOCItemQty = FOCItemQty;
	}
	
	public void preparePromotionData(){
		
		arrPromotion.add(new PromotionDO("NYLE  HERBAL HAIR OIL 24X200 ML","CK006",5,"CK006",1));
		arrPromotion.add(new PromotionDO("NYLE ANTI DANDRUFF HAIR OIL ","CK031",5,"CK006",1));
		arrPromotion.add(new PromotionDO("NYLE  ANTI HAIR FALL OIL 24X200 ML","CK060",5,"CK006",1));
		arrPromotion.add(new PromotionDO("CAVIN KARE FAIREVER CREAM 240X25 GMS","CK001",5,"CK001",1));
		arrPromotion.add(new PromotionDO("CAVIN KARE FAIREVER CREAM 120X50 GMS","CK002",5,"CK002",1));
		arrPromotion.add(new PromotionDO("CAVIN KARE FAIREVER FRUIT 120X50GMS","CK011",5,"CK011",1));
		arrPromotion.add(new PromotionDO("CAVIN KARE FAIREVER FRUIT CREAM 240X25G","CK037",5,"CK037",1));
		arrPromotion.add(new PromotionDO("FAIREVER  FACE WASH FRUIT  8X6X100 ML","CK050",5,"CK050",1));
		arrPromotion.add(new PromotionDO("FAIREVER  FACE WASHSAFFRON  8X6X100 ML","CK053",5,"CK053",1));
		arrPromotion.add(new PromotionDO("CK FAIREVER HERBAL CREAM 240X25G","CK056",5,"CK056",1));
		arrPromotion.add(new PromotionDO("CK FAIREVER HERBAL CREAM 120X50G","CK057",5,"CK057",1));
		arrPromotion.add(new PromotionDO("DABUR VATIKA HAIR OIL 48X125 ML","DB020",5,"DB020",1));
		arrPromotion.add(new PromotionDO("DABUR VATIKA HAIR OIL 24X250 ML","DB021",5,"DB021",1));
		arrPromotion.add(new PromotionDO("DABUR VATIKA HAIR OIL 12X400 ML","DB022",5,"DB022",1));
		arrPromotion.add(new PromotionDO("###DABUR GLUCOSE REGULAR 24X450 GMS","DB130",7,"DB130",1));
		arrPromotion.add(new PromotionDO("DABUR GLUCOSE REGULAR 450G+50G FREE","DB13001",7,"DB13001",1));
		arrPromotion.add(new PromotionDO("###DABUR GLUCOSE ORANGE 24X450 GMS","DB131",7,"DB131",1));
		arrPromotion.add(new PromotionDO("DABUR GLUCOSE ORANGE 450G+50G FREE","DB13101",7,"DB13101",1));
		arrPromotion.add(new PromotionDO("DABUR GLUCOSE PINEAPPLE 24X450 GMS","DB132",7,"DB132",1));
		arrPromotion.add(new PromotionDO("DABUR GLUCOSE PINEAPPLE 450G+50G FREE","DB13201",7,"DB13201",1));
		arrPromotion.add(new PromotionDO("###DABUR GLUCOSE MANGO 24X450 GMS","DB133",7,"DB133",1));
		arrPromotion.add(new PromotionDO("DABUR GLUCOSE MANGO 450G+50G FREE","DB13301",7,"DB13301",1));
		arrPromotion.add(new PromotionDO("###DABUR GLUCOSE LEMON 24X450 GMS","DB134",7,"DB134",1));
		arrPromotion.add(new PromotionDO("DABUR GLUCOSE LEMON 450G+50G FREE","DB13401",7,"DB13401",1));
		arrPromotion.add(new PromotionDO("HEINZ TOMATO KETCHUP 12X300GM","HE143",5,"HE143",1));
		arrPromotion.add(new PromotionDO("REPLAZE MINITABS 12 X 100'S","RP001",11,"RP001",1));
		arrPromotion.add(new PromotionDO("REPLAZE STEVIA 12 X 50'S","RP008",11,"RP008",1));
		arrPromotion.add(new PromotionDO("REPLAZE ORIGINAL MINIPACK 12X50G","RP009",11,"RP009",1));
	}
}
