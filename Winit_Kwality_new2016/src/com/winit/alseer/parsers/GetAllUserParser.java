package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.UserInfoDA;
import com.winit.alseer.salesman.dataobject.AllUsersDo;
import com.winit.alseer.salesman.dataobject.BlaseUserDco;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class GetAllUserParser extends BaseHandler
{
	private UserInfoDA userInfoBL;
	private AllUsersDo allUserOb;
	private BlaseUserDco blaseUserOb;
	private Vector<BlaseUserDco> vecBlaseUserDco;

	public GetAllUserParser(Context context)
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("GetAllUsersResult"))
		{
			allUserOb = new AllUsersDo();
		}
		else if(localName.equalsIgnoreCase("BlaseUsers")||localName.equalsIgnoreCase("Users"))
		{
			vecBlaseUserDco = new Vector<BlaseUserDco>();
			userInfoBL = new UserInfoDA();
		}
		else if(localName.equalsIgnoreCase("BlaseUserDco"))
		{
			blaseUserOb = new BlaseUserDco();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			preference.saveStringInPreference(ServiceURLs.GET_ALL_USERS+Preference.LAST_SYNC_TIME, currentValue.toString());
			LogUtils.errorLog("CurrentTime", "AllUser - "+currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("Status"))
		{
			allUserOb.strStatus = currentValue.toString();//Need to check
		}
		else if(localName.equalsIgnoreCase("Count"))
		{
			allUserOb.strCount = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("USERID"))
		{
			blaseUserOb.strUserid = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("USERNAME"))
		{
			blaseUserOb.strUserName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ROLE"))
		{
			blaseUserOb.strRole = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SALESMANCODE"))
		{
			blaseUserOb.strSaleManCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Token"))
		{
			blaseUserOb.strToken = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("EmpNo"))
		{
			blaseUserOb.empNo = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PhoneNumber"))
		{
			blaseUserOb.phoneNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ManagerEmpNo"))
		{
			blaseUserOb.strManagerCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("REGION"))
		{
			blaseUserOb.strREGION = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("UserType"))
		{
			blaseUserOb.strUserType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Target"))
		{
			blaseUserOb.Target = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AchievedTarget"))
		{
			blaseUserOb.AchievedTarget = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("WorkingDays"))
		{
			blaseUserOb.workingDays = currentValue.toString();
		}
		
		else if(localName.equalsIgnoreCase("TotalWorkingDays"))
		{
			blaseUserOb.TotalWorkingDays = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("intUserId"))
		{
			blaseUserOb.intUserId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PhoneNumber"))
		{
			blaseUserOb.PhoneNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("LocationCode"))
		{
			blaseUserOb.LocationCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("LocationName"))
		{
			blaseUserOb.LocationName = currentValue.toString();
		}
		
		else if(localName.equalsIgnoreCase("RouteNumber"))
		{
			blaseUserOb.RouteNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("FirstDiscount"))
		{
			blaseUserOb.FirstDiscount = StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("SecondDiscount"))
		{
			blaseUserOb.SecondDiscount = StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ReqSalesPerDay"))
		{
			blaseUserOb.ReqSalesPerDay = StringUtils.getFloat(currentValue.toString());
		}
		
		else if(localName.equalsIgnoreCase("FoodTarget"))
		{
			blaseUserOb.FoodTarget = StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("FoodAchivement"))
		{
			blaseUserOb.FoodAchivement = StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("FoodReqSalesForDay"))
		{
			blaseUserOb.FoodReqSalesForDay = StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("AdditionalTarget"))
		{
			blaseUserOb.AdditionalTarget = StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("AdditionalFoodTarget"))
		{
			blaseUserOb.AdditionalFoodTarget = StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ThirdPartyTarget"))
		{
			blaseUserOb.ThirdPartyTarget = StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("AchiveThirdPartyTarget"))
		{
			blaseUserOb.AchiveThirdPartyTarget = StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("AdditionalThirdPartyTarget"))
		{
			blaseUserOb.AdditionalThirdPartyTarget = StringUtils.getFloat(currentValue.toString());
		}

		else if(localName.equalsIgnoreCase("BlaseUserDco"))
		{
			vecBlaseUserDco.add(blaseUserOb);
		}
		
//		/AgencyId 
		else if(localName.equalsIgnoreCase("BlaseUsers")||localName.equalsIgnoreCase("Users"))
		{
			if(vecBlaseUserDco.size() > 0)
			{
				if(saveIntoUserTable(vecBlaseUserDco))
					preference.commitPreference();
			}
		}
	}
	//tblUOMFactor

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	public AllUsersDo getAllUsers()
	{
		return allUserOb;
	}
	private boolean saveIntoUserTable(Vector<BlaseUserDco> vector)
	{
		return userInfoBL.insertUser(vector);
	}
}
