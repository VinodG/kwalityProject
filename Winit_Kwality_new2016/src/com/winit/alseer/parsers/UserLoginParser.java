package com.winit.alseer.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataobject.LoginUserInfo;

public class UserLoginParser extends BaseHandler
{
	private StringBuilder currentValue ;
	private boolean currentElement = false;
	private LoginUserInfo objLoginUserInfo;
	private Preference preference;
	
	public UserLoginParser(Context context) 
	{
		super(context);
		preference 		= 	new Preference(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue  = new StringBuilder();
		if(localName.equalsIgnoreCase("CheckLoginResult"))
		{
			objLoginUserInfo = new LoginUserInfo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("Status"))
		{
			objLoginUserInfo.strStatus = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Message"))
		{
			objLoginUserInfo.strMessage =  currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("USERID"))
		{
			objLoginUserInfo.strUserId =  currentValue.toString();
			preference.saveStringInPreference(Preference.USER_NAME, objLoginUserInfo.strUserId);
			preference.commitPreference();
		}
		else if(localName.equalsIgnoreCase("USERNAME"))
		{
			objLoginUserInfo.strUserName =  currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ROLE"))
		{
			objLoginUserInfo.strRole =  currentValue.toString();
			preference.saveStringInPreference(Preference.USER_TYPE, objLoginUserInfo.strRole);
			preference.commitPreference();
		}
		else if(localName.equalsIgnoreCase("REGION"))
		{
			objLoginUserInfo.strREGION =  currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Token"))
		{
			objLoginUserInfo.strToken =  currentValue.toString();
			preference.saveStringInPreference("strToken", objLoginUserInfo.strToken);
			preference.commitPreference();
		}
		else if(localName.equalsIgnoreCase("EmpNo"))
		{
			objLoginUserInfo.strEmpNo =  currentValue.toString();
			preference.saveStringInPreference(Preference.EMP_NO, objLoginUserInfo.strEmpNo);
			preference.commitPreference();
		}
		else if(localName.equalsIgnoreCase("SALESMANCODE"))
		{
			objLoginUserInfo.strSalesmanCode =  currentValue.toString();
			preference.saveStringInPreference(Preference.SALESMANCODE, objLoginUserInfo.strSalesmanCode);
			preference.commitPreference();
		}
		else if(localName.equalsIgnoreCase("UserType"))
		{
			objLoginUserInfo.strSalemanType =  currentValue.toString();
			preference.saveStringInPreference(Preference.SALESMAN_TYPE, objLoginUserInfo.strSalemanType);
			preference.commitPreference();
		}
		else if(localName.equalsIgnoreCase("intUserId"))
		{
			objLoginUserInfo.intUserId =  currentValue.toString();
			preference.saveStringInPreference(Preference.INT_USER_ID, objLoginUserInfo.intUserId);
			preference.commitPreference();
		}
		
		else if(localName.equalsIgnoreCase("SalesOrgCode"))
		{
			objLoginUserInfo.strORGCode =  currentValue.toString();
			preference.saveStringInPreference(Preference.ORG_CODE, objLoginUserInfo.strORGCode);
			preference.commitPreference();
		}
//		to saving the entire object
//		else if(localName.equalsIgnoreCase("CheckLoginResult"))
//		{
//			preference.saveObjectInPreference("LoggedInUser", objLoginUserInfo);
//			preference.commitPreference();
//		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	public LoginUserInfo getLoggedInUserInfo()
	{
		return objLoginUserInfo;
	}
}
