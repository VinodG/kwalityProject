package com.winit.alseer.salesman.dataobject;

import java.io.Serializable;

import com.winit.alseer.salesman.common.AppConstants;
@SuppressWarnings("serial")
public class CustomerSurveyDO_Edit implements Serializable
{
	public String serveyId = "";
	public String tskId = "";
	public boolean Olay = true;
	public boolean Pantene;
	public boolean Lakme;
	public boolean Elle18 = true;
	public boolean Agree = true;
	public int spent = AppConstants.AEDBelow3000;
	public String Brand1 = "Lakme";
	public String Brand2 = "";
	public String Brand3 = "Pantene";
	public String Brand4 = "";
	public String isVerified = "";
	public String isPushStatus = "";
	public String date = "";
	public String latitud = "";
	public String langitude = "";
	public String taskName = "";
	public String strCusomerName = "";
}
