package com.winit.alseer.salesman.dataobject;


@SuppressWarnings("serial")
public class CustomerCreditLimitDo extends BaseComparableDO
{
	public String site = "";
	public String creditLimit = "";
	public String outStandingAmount = "";
	public String outStandingFoodAmount = "";
	public String outStandingTPTAmount = "";
	public String availbleLimit = "";
	public String excessPayment = "";
	
	//created by anil to display monthly sales amount
	public String monthlySalesAmount="";
	public String lastSalesInvoiceAmount="";

    public String totalOutStandingAmount="";
}
