package com.winit.alseer.parsers;

import android.content.Context;
import android.util.Log;

import com.winit.alseer.salesman.dataaccesslayer.InitiativesDA;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataobject.InitiativeDO;
import com.winit.alseer.salesman.dataobject.InitiativeProductsDO;
import com.winit.alseer.salesman.dataobject.InitiativeTradePlanDO;
import com.winit.alseer.salesman.dataobject.InitiativeTradePlanImageDO;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.util.Vector;

public class InitiativeDataParser extends BaseHandler
{
	private boolean status = false;
	private String msgStatus = "";
	private boolean isFirst = true;
	private String  statusFrom="" ;
	private SynLogDO synLogDO = new SynLogDO();

	private Vector<InitiativeDO> vecInitiativeDO;
	private Vector<InitiativeProductsDO> vecInitiativeProductsDO;
	private Vector<InitiativeTradePlanDO> vecInitiativeTradePlanDo;
	private Vector<InitiativeTradePlanImageDO> vecInitiativeTradePlanImageDO;


	private  InitiativeDO  initiativeDO;
	private  InitiativeProductsDO  initiativeProductsDO;
	private  InitiativeTradePlanImageDO  initiativeTradePlanImageDO;
	private  InitiativeTradePlanDO  initiativeTradePlanDO;

	private Context context;


	public InitiativeDataParser(InputStream is)
	{
		super(is);
		// TODO Auto-generated constructor stub
	}
	public InitiativeDataParser(Context context)
	{
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("Initiatives"))
		{
			vecInitiativeDO = new Vector<InitiativeDO>();
		}
		else if(localName.equalsIgnoreCase("InitiativeDco"))
		{
			initiativeDO = new InitiativeDO();
			statusFrom="InitiativeDO";
		}else
		if(localName.equalsIgnoreCase("InitiativeProducts"))
		{
			vecInitiativeProductsDO = new Vector<InitiativeProductsDO>();
		}
		else if(localName.equalsIgnoreCase("InitiativeProductDco"))
		{
			initiativeProductsDO = new InitiativeProductsDO();
			statusFrom="InitiativeProductsDO";
		}else
		if(localName.equalsIgnoreCase("InitiativeTradePlans"))
		{
			vecInitiativeTradePlanDo= new Vector<InitiativeTradePlanDO>();
		}
		else if(localName.equalsIgnoreCase("InitiativeTradePlanDco"))
		{
			statusFrom="InitiativeTradePlanDO";
			initiativeTradePlanDO = new InitiativeTradePlanDO();
		}
		if(localName.equalsIgnoreCase("InitiativeTradePlanImages"))
		{
			vecInitiativeTradePlanImageDO = new Vector<InitiativeTradePlanImageDO>();
		}
		else if(localName.equalsIgnoreCase("InitiativeTradePlanImageDco"))
		{
			statusFrom="InitiativeTradePlanImageDO";
			initiativeTradePlanImageDO = new InitiativeTradePlanImageDO();
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException
	{
		currentElement  = false;
		if( isFirst == true)
		{
			if (localName.equalsIgnoreCase("status"))
			{
				Log.e("status", "status - " + currentValue.toString());
				if (currentValue.toString().equalsIgnoreCase("Success"))
				{
					status = true;
				}

				msgStatus= currentValue.toString();
			}
			if(localName.equalsIgnoreCase("Status"))
			{
				synLogDO.action = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("ServerTime"))
			{
				synLogDO.TimeStamp = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("ModifiedDate"))
			{
				synLogDO.UPMJ = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("ModifiedTime"))
			{
				synLogDO.UPMT = currentValue.toString();
				isFirst =false;
			}

		}else


		if(localName.equalsIgnoreCase("InitiativeId") && statusFrom.equalsIgnoreCase("InitiativeDO"))
		{

			initiativeDO.InitiativeId=Integer.parseInt("0"+currentValue.toString());
		}else  if(localName.equalsIgnoreCase("Title")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.Title= currentValue.toString();

		}else if(localName.equalsIgnoreCase("Brand")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.Brand= currentValue.toString();
		}else if(localName.equalsIgnoreCase("StartDate")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.StartDate= currentValue.toString();
		}else if(localName.equalsIgnoreCase("Image")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.Image= currentValue.toString();
		}else if(localName.equalsIgnoreCase("Status") )
		{
			if(statusFrom.equalsIgnoreCase("InitiativeDO") )
				initiativeDO.Status= currentValue.toString();
			if(statusFrom.equalsIgnoreCase("InitiativeTradePlanDO") )
				initiativeTradePlanDO.Status = currentValue.toString();

		}else if(localName.equalsIgnoreCase("Month")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.Month= currentValue.toString();
		}else if(localName.equalsIgnoreCase("InitiativeMonth")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.InitiativeMonth= currentValue.toString();
		}else if(localName.equalsIgnoreCase("InitiativeYear")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.InitiativeYear= currentValue.toString();
		}else if(localName.equalsIgnoreCase("Planogram")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.Planogram= currentValue.toString();
		}else if(localName.equalsIgnoreCase("POSM")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.POSM = currentValue.toString();
		}else if(localName.equalsIgnoreCase("ModifiedDate")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.ModifiedDate= Integer.parseInt("0"+currentValue.toString()) ;
		}
		else if(localName.equalsIgnoreCase("ModifiedTime")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.ModifiedTime= Integer.parseInt("0"+currentValue.toString()) ;
		}else if(localName.equalsIgnoreCase("IsChannelSpecified")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.IsChannelSpecified= currentValue.toString();
		}else if(localName.equalsIgnoreCase("ModifiedBy")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.ModifiedBy= currentValue.toString();
		}else if(localName.equalsIgnoreCase("ModifiedOn")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.ModifiedOn = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ExecutionStatus")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.ExecutionStatus= currentValue.toString();
		}else if(localName.equalsIgnoreCase("ImplementedOn")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.ImplementedOn= currentValue.toString();
		}else if(localName.equalsIgnoreCase("VerifiedOn")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.VerifiedOn = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ExecutionStatus")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.ExecutionStatus= currentValue.toString();
		}else if(localName.equalsIgnoreCase("ImplementedOn")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.ImplementedOn= currentValue.toString();
		}else if(localName.equalsIgnoreCase("VerifiedOn")&& statusFrom.equalsIgnoreCase("InitiativeDO"))
		{
			initiativeDO.VerifiedOn = currentValue.toString();
		}
		//InitiativeProductDco
		else if(localName.equalsIgnoreCase("InitiativeId")&& statusFrom.equalsIgnoreCase("InitiativeProductsDO"))
		{
			initiativeProductsDO.InitiativeId= Integer.parseInt("0"+currentValue.toString());
		}else if(localName.equalsIgnoreCase("InitiativeProductId")&& statusFrom.equalsIgnoreCase("InitiativeProductsDO"))
		{
			initiativeProductsDO.InitiativeProductId=Integer.parseInt("0"+currentValue.toString());
		}else if(localName.equalsIgnoreCase("ItemCode")&& statusFrom.equalsIgnoreCase("InitiativeProductsDO"))
		{
			initiativeProductsDO.ItemCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ItemCode")&& statusFrom.equalsIgnoreCase("InitiativeProductsDO"))
		{
			initiativeProductsDO.ItemCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate")&& statusFrom.equalsIgnoreCase("InitiativeProductsDO"))
		{
			initiativeProductsDO.ModifiedDate= Integer.parseInt("0"+currentValue.toString());
		}else if(localName.equalsIgnoreCase("ModifiedTime")&& statusFrom.equalsIgnoreCase("InitiativeProductsDO"))
		{
			initiativeProductsDO.ModifiedTime=Integer.parseInt("0"+currentValue.toString());
		}
		//InitiativeTradePlanDco

		else if(localName.equalsIgnoreCase("InitiativeId")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
		{
			initiativeTradePlanDO.InitiativeId=Integer.parseInt("0"+currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("InitiativeTradePlanId")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
		{
			initiativeTradePlanDO.InitiativeTradePlanId =Integer.parseInt("0"+currentValue.toString());
		}else if(localName.equalsIgnoreCase("OutletId")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
		{
			initiativeTradePlanDO.OutletId = currentValue.toString();
		}else if(localName.equalsIgnoreCase("OutletName")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
		{
			initiativeTradePlanDO.OutletName = currentValue.toString();
		}else if(localName.equalsIgnoreCase("KBDGE")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
		{
			initiativeTradePlanDO.KBDGE = currentValue.toString();
		}else if(localName.equalsIgnoreCase("FD")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
		{
			initiativeTradePlanDO.FD = currentValue.toString();
		}else if(localName.equalsIgnoreCase("FDCost")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
		{
			initiativeTradePlanDO.FDCost = currentValue.toString();
		}else if(localName.equalsIgnoreCase("GE")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
		{
			initiativeTradePlanDO.GE = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("GECost")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
		{
			initiativeTradePlanDO.GECost = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("FSU")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
		{
			initiativeTradePlanDO.FSU = currentValue.toString();
		}else if(localName.equalsIgnoreCase("FSUCost")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
		{
			initiativeTradePlanDO.FSUCost = currentValue.toString();
		}else if(localName.equalsIgnoreCase("StartDate")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
		{
			initiativeTradePlanDO.StartDate = currentValue.toString();
		}else if(localName.equalsIgnoreCase("EndDate")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
		{
			initiativeTradePlanDO.EndDate = currentValue.toString();
		}else if(localName.equalsIgnoreCase("Budget")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
		{
			initiativeTradePlanDO.Budget = currentValue.toString();
		}
		else/* if(localName.equalsIgnoreCase("Status"))
		{
			initiativeTradePlanDO.Status = currentValue.toString();
		}else */if(localName.equalsIgnoreCase("ImageUrl")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
			{
				initiativeTradePlanDO.ImageUrl = currentValue.toString();
			}else if(localName.equalsIgnoreCase("ImplementedOn")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
			{
				initiativeTradePlanDO.ImplementedOn = currentValue.toString();
			}else if(localName.equalsIgnoreCase("Branding")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
			{
				initiativeTradePlanDO.Branding = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("BrandingCost")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
			{
				initiativeTradePlanDO.BrandingCost = currentValue.toString();
			}else if(localName.equalsIgnoreCase("ModifiedDate")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
			{
				initiativeTradePlanDO.ModifiedDate = Integer.parseInt("0"+currentValue.toString());
			}else if(localName.equalsIgnoreCase("ModifiedTime")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
			{
				initiativeTradePlanDO.ModifiedTime = Integer.parseInt("0"+currentValue.toString());
			}else if(localName.equalsIgnoreCase("CustomerVisitAppId")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanDO"))
			{
				initiativeTradePlanDO.CustomerVisitAppId = currentValue.toString();
			}

			//InitiativeTradePlanImagesDCo
			else if(localName.equalsIgnoreCase("InitiativeTradePlanImageIdd")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanImageDO"))
			{
				initiativeTradePlanImageDO.InitiativeTradePlanImageIdd =  currentValue.toString() ;
			}
			else if(localName.equalsIgnoreCase("InitiativeTradePlanId")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanImageDO"))
			{
				initiativeTradePlanImageDO.InitiativeTradePlanId = Integer.parseInt("0"+currentValue.toString());
			}
			else if(localName.equalsIgnoreCase("Type")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanImageDO"))
			{
				initiativeTradePlanImageDO.Type = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("ExecutionPicture")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanImageDO"))
			{
				initiativeTradePlanImageDO.ExecutionPicture = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("ExecutionStatus")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanImageDO"))
			{
				initiativeTradePlanImageDO.ExecutionStatus = Integer.parseInt("0"+currentValue.toString());
			}
			else if(localName.equalsIgnoreCase("ImplementedBy")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanImageDO"))
			{
				initiativeTradePlanImageDO.ImplementedBy = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("ImplementedOn")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanImageDO"))
			{
				initiativeTradePlanImageDO.ImplementedOn = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("VerifiedBy")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanImageDO"))
			{
				initiativeTradePlanImageDO.VerifiedBy = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("VerifiedOn")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanImageDO"))
			{
				initiativeTradePlanImageDO.VerifiedOn = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("Reason")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanImageDO"))
			{
				initiativeTradePlanImageDO.Reason = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("JourneyCode")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanImageDO"))
			{
				initiativeTradePlanImageDO.JourneyCode = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("VisitCode")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanImageDO"))
			{
				initiativeTradePlanImageDO.VisitCode = currentValue.toString();
			}else if(localName.equalsIgnoreCase("ModifiedDate")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanImageDO"))
			{
				initiativeTradePlanImageDO.ModifiedDate = Integer.parseInt("0"+currentValue.toString());
			}else if(localName.equalsIgnoreCase("ModifiedTime")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanImageDO"))
			{
				initiativeTradePlanImageDO.ModifiedTime = Integer.parseInt("0"+currentValue.toString());
			}
			else  if(localName.equalsIgnoreCase("ModifiedTime")&& statusFrom.equalsIgnoreCase("InitiativeTradePlanImageDO"))
			{
				initiativeTradePlanImageDO.ModifiedTime = Integer.parseInt("0"+currentValue.toString());
			}
			else if(localName.equalsIgnoreCase("InitiativeDco"))
			{
				vecInitiativeDO.add(initiativeDO);
			}
			else if(localName.equalsIgnoreCase("InitiativeProductDco"))
			{
				vecInitiativeProductsDO.add(initiativeProductsDO);
			}
			else if(localName.equalsIgnoreCase("InitiativeTradePlanDco"))
			{
				 vecInitiativeTradePlanDo.add(initiativeTradePlanDO);
			}
			else if(localName.equalsIgnoreCase("InitiativeTradePlanImageDco"))
			{
				 vecInitiativeTradePlanImageDO.add(initiativeTradePlanImageDO);
			}
			else
			if(localName.equalsIgnoreCase("InitiativeResponse") )
			{
				synLogDO.entity = ServiceURLs.InsertInitiatives;
				new SynLogDA().insertSynchLog(synLogDO);
				new InitiativesDA().insertInitiative(vecInitiativeDO);
				new InitiativesDA().insertInitiativeProducts(vecInitiativeProductsDO);
				new InitiativesDA().insertInitiativeTradePlans(vecInitiativeTradePlanDo);
				new InitiativesDA().insertInitiativeTradePlanImages(vecInitiativeTradePlanImageDO);
			}



	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}

	public boolean getResponseStatus()
	{
		if(msgStatus != null && msgStatus.equalsIgnoreCase("Success"))
			return true;
		return false;
	}

/*	public boolean getStatus()
	{
		if(Msg!=null && Msg.equalsIgnoreCase("SiteId Already exists."))
			return true;
		return status;
	}

	public String getCustomerSiteId()
	{
		return customerSiteId;
	}*/
}
