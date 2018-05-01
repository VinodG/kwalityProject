package com.winit.alseer.salesman.dataobject;

import java.util.Vector;


@SuppressWarnings("serial")
public class ReturnOrderStatusDetailsDO extends BaseComparableDO
{
	public String Status         = "";
	public String Message        = "" ;
	public String ServerDateTime = "";
	public String Count          = "";
	public String ModifiedDate   = "";
	public String ModifiedTime   = "";
	
	public Vector<TrxHeaderDO> vecTrxHeadderDOs = new Vector<TrxHeaderDO>();
}
