package com.winit.alseer.salesman.dataobject;

import java.io.Serializable;
import java.util.ArrayList;

public class VerifyRequestDO implements Serializable {
	public String movementCode 		= "";
	public String movementType		= "";
	public String movementStatus 	= "";
	public String logisticSignature = "";
	public String salesmanSignature = "";
	public ArrayList<VanLoadDO> vecVanLodDOs;
}
