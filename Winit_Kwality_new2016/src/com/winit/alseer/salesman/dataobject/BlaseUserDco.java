package com.winit.alseer.salesman.dataobject;

@SuppressWarnings("serial")
public class BlaseUserDco extends BaseComparableDO 
{
	public String strUserid;
	public String strUserName;
	public String strRole;
	public String strSaleManCode;
	public String strManagerCode="";
	public String empNo="";
	public String strToken;
	public String strREGION;
	public String strUserType;
	public String Target;
	public String AchievedTarget;
	public String workingDays = "";
	public String strRoleId;
	public String description="";
	public String email;
	public String parentCode;
	public String isActive;
	public String creditLimit;
	public String phoneNumber="";
	public String category;

	public String intUserId="";
	
	public String TotalWorkingDays = "";
	public String PhoneNumber = "";
	public String LocationCode = "";
	public String LocationName = "";
	public String RouteNumber = "";
	
	public float FirstDiscount = 0.0f;
	public float SecondDiscount = 0.0f;
	public float ReqSalesPerDay = 0.0f;
	public float FoodTarget = 0;
	public float FoodAchivement = 0;
	public float FoodReqSalesForDay = 0;
	public float AdditionalTarget = 0;
	public float AdditionalFoodTarget = 0;

	//newly added for thirdparty  brandsThirdPartyTarget,AchiveThirdPartyTarget,AdditionalThirdPartyTarget
	public float ThirdPartyTarget = 0;
	public float AchiveThirdPartyTarget = 0;
	public float AdditionalThirdPartyTarget = 0;
}
