package com.winit.alseer.salesman.dataobject;

@SuppressWarnings("serial")
public class SkippingReasonDO extends BaseObject
{
//	CREATE TABLE tblSkipReasons(PresellerId VARCHAR, SkipDate VARCHAR, Reason VARCHAR, ReasonId VARCHAR, ReasonType VARCHAR, CustomerSiteId VARCHAR, "Status" VARCHAR, "UploadStatus" VARCHAR)
	public String PresellerId   = "";
	public String SkipDate = "";
	public String Reason   = "";
	public String ReasonType = "";
	public String CustomerSiteId = "";
	public String ReasonId = "";
	public String Status = "";
	public String UploadStatus = "";
}
