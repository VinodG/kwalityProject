package com.winit.alseer.salesman.dataobject;

import java.io.Serializable;

@SuppressWarnings("serial")
public class NameIDDo implements Serializable
{
	public String strName ="";
	public String strId="";
	public String strType="";
	
	public float factor=1;
	public String UOM="";
	
	public String chequeNumber="";
	public String chequeAmount="";
	public String chequeDate="";
	public String BankName="";
	public String BranchName="";
}
