package com.winit.alseer.salesman.dataobject;

import java.util.ArrayList;


@SuppressWarnings("serial")
public class LoadRequestDO extends BaseComparableDO
{
	public String MovementCode="";
	public String PreMovementCode="";
	public String AppMovementId="";
	public String OrgCode="";
	public String UserCode="";
	public String WHKeeperCode="";
	public String CurrencyCode="";
	public String JourneyCode="";
	public String MovementDate="";
	public String MovementNote="";
	public String MovementType="";
	public String UserLoadType="";
	public String SourceVehicleCode="";
	public String DestinationVehicleCode="";
	public String Status="";
	public String VisitID="";
	public String MovementStatus="";
	public String CreatedOn="";
	public String ApproveByCode="";
	public String ApprovedDate="";
	public String JDETRXNumber="";
	public String ISStampDate="";
	public String ISFromPC="";
	public String OperatorCode="";
	public String IsDummyCount="";
	public float Amount=0;
	public String ModifiedDate="";
	public String ModifiedTime="";
	public String PushedOn="";
	public String ModifiedOn="";
	public String ProductType = "";
	public String WHCode = "";
	
	public ArrayList<LoadRequestDetailDO> vecItems  = new ArrayList<LoadRequestDetailDO>();
	public int Division = 0;
	
	public static final int MOVEMENT_STATUS_APPROVED_VERIFY 		= 100;
	public static final int MOVEMENT_STATUS_APPROVED				= 99;
	public static final int MOVEMENT_STATUS_APPROVED_VANSTOCK		= 7;
	public static final int MOVEMENT_STATUS_PENDING					= 2;
	public static final int MOVEMENT_STATUS_REJECTED				=-13;
}
