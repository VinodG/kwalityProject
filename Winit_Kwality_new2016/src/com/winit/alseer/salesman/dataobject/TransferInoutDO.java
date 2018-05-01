package com.winit.alseer.salesman.dataobject;

import java.io.Serializable;
import java.util.ArrayList;

public class TransferInoutDO implements Serializable
{
	public ArrayList<DeliveryAgentOrderDetailDco> vecOrderDetailDcos;
	public String InventoryUID   = "";
	public String fromEmpNo		 = "";
	public String toEmpNo 		 = "";
	public String trnsferType    = "";
	public String transferStatus = "";
	public String sourceVNO		 = "";
	public String destVNO		 = "";
	public String Date		 	 = "";
	public String customerName   = "";
	public String sourceOrderID  = "";	
	public String destOrderID    = "";
	
	public boolean IsAppVerified = true;
	public int MovementType		 = 4;	
	public int MovementStatus	 = 0;	
	public String MovementCode   = "";	
	public static final double AvailQty = 0.0;
}
