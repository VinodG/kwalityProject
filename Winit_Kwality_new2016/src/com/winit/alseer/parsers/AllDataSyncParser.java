package com.winit.alseer.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.SyncData.SyncProcessListner;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
public class AllDataSyncParser extends BaseHandler
{
	private Context context;
	private StringBuilder sBuffer;
	private final static float  TOTAL_MAIN_MODULES=35.0f;
	private int  SYNCED_MAIN_MODULES=0;
	private int serverTimeCount=-1;
	SynLogDO syncCompleteLogDO = new SynLogDO();
	//CommonMastersData 
	private final int PARSING_SCOPE_BANKS = 1;
	private final int PARSING_SCOPE_VEHICLES = 2;
	private final int PARSING_SCOPE_USERS = 3;
	private final int PARSING_SCOPE_REASONS = 4;
	private final int PARSING_SCOPE_CURRENCIES = 5;
	//ItemMastersData
	private final int PARSING_SCOPE_AGENCIES = 6;
	private final int PARSING_SCOPE_BRANDS = 7;
	private final int PARSING_SCOPE_CATEGORIES = 8;
	private final int PARSING_SCOPE_ITEMS = 9;
	private final int PARSING_SCOPE_PRICE = 10;
	private final int PARSING_SCOPE_COMPETETIOR_BRANDS = 11;
	private final int PARSING_SCOPE_UOM_FACTORS = 12;
	private final int PARSING_SCOPE_SELLING_SKU_CLASSIFICATIONS = 27;
	private final int PARSING_SCOPE_CUSTOMER_SELLING_SKU_GROUPS = 32;
	private final int PARSING_SCOPE_SELLING_SKU = 33;
	private final int PARSING_SCOPE_GROUP_SELLING_SKU_CLASSIFICATIONS = 34;
	//AppActiveStatusResponse
	private final int PARSING_SCOPE_APP_ACTIVE_STATUS_RESPONSE = 13;
	//AssetMasterResponse
	private final int PARSING_SCOPE_ASSET_MASTER_RESPONSE = 14;
	//OfflineDataResponse
	private final int PARSING_SCOPE_OFFLINE_DATA_RESPONSE = 15;
	//CustomerMasterDataResponse
	private final int PARSING_SCOPE_CHANNELS = 16;
	private final int PARSING_SCOPE_SUB_CHANNELS = 17;
	private final int PARSING_SCOPE_LOCATIONS = 18;
	private final int PARSING_SCOPE_CUSTOMERS = 19;
	private final int PARSING_SCOPE_DAILY_JOURNEY_PLANS = 20;
	
	//DiscountAndInvoicesDataResponse
	private final int PARSING_SCOPE_DISCOUNTS = 21;
//	private final int PARSING_SCOPE_TXN = 22;
	//TrxHeaderResponse
	private final int PARSING_SCOPE_TRANSACTION_HEADER_RESPONSE = 23;
	//CustomerResponse
	private final int PARSING_SCOPE_CUSTOMER_RESPONSE = 24;
	//DAPassCodeResponse
	private final int PARSING_SCOPE_DAPASSCODE_RESPONSE = 25;
	//TaskResponse
	private final int PARSING_SCOPE_TASK_RESPONSE = 26;
	
	private final int PARSING_SCOPE_WAREHOUSE=28;
	private final int PARSING_SCOPE_SETTINGS=29;
	private final int PARSING_SCOPE_CASHDENOMINATIONS=30;
	
	private final int PARSING_SCOPE_RECOMMENDED_QUANTITY_RESPONSE = 35;
	private final int PARSING_SCOPE_DETAIL_STOCK_RESPONSE = 105;
	
	
	private final int PARSING_SCOPE_TRX_LOG_HEADERS=95;
	private final int PARSING_SCOPE_tRX_LOG_DETAILS=96;
	
	//missing sync
	private final int PARSING_SCOPE_INITIATIVE=31;
	private final int PARSING_SCOPE_PENDING_INVOICES = 100;
	
	private final int PARSING_SCOPE_PROMOTION_SC=50;
	private final int PARSING_SCOPE_PROMOTIONS=70;
	
	
	private final int PARSING_SCOPE_SERVERTIME = -2;
	private final int PARSING_SCOPE_INVALID = -1;
	private int parsingScope =  PARSING_SCOPE_INVALID;
	private String empNo;
	BaseHandler currentHandler;
	SynLogDO syncLogDO = new SynLogDO();
	public Preference preference;
	SyncProcessListner syncProcessListner=null;
	//TRXStatus
	public AllDataSyncParser(Context context)
	{
		super(context);
		this.context = context;
		preference = new Preference(context);
	}
	public AllDataSyncParser(Context context, String empNo) 
	{
		super(context);
		this.context = context;
		this.empNo = empNo;
		preference = new Preference(context);
	}
	public AllDataSyncParser(Context context, String empNo,SyncProcessListner syncProcessListner) 
	{
		super(context);
		this.context = context;
		this.empNo = empNo;
		this.syncProcessListner=syncProcessListner;
		preference = new Preference(context);
	}
	//tblSellingSKUClassification
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		switch (parsingScope)
		{
			case (PARSING_SCOPE_INVALID):
			{
				if (localName.equalsIgnoreCase("ServerTime"))
				{
					serverTimeCount++;
					if(serverTimeCount <1)
						parsingScope = PARSING_SCOPE_SERVERTIME;
				}
				else if (localName.equalsIgnoreCase("ServerDateTime"))
				{
					serverTimeCount++;
					if(serverTimeCount <1)
						parsingScope = PARSING_SCOPE_SERVERTIME;
				}
//				else if (localName.equalsIgnoreCase("ModifiedDate"))
//					parsingScope = PARSING_SCOPE_SERVERTIME;
//				else if (localName.equalsIgnoreCase("ModifiedTime"))
//					parsingScope = PARSING_SCOPE_SERVERTIME;
				
				else if (localName.equalsIgnoreCase("Banks"))
				{
					parsingScope   = PARSING_SCOPE_BANKS;
					currentHandler = new GetBanksParser(context);
				}
				else if (localName.equalsIgnoreCase("Settings"))
				{
					parsingScope   = PARSING_SCOPE_SETTINGS;
					currentHandler = new GetSettingsParser(context);
				}
				else if (localName.equalsIgnoreCase("CashDenominations"))
				{
					parsingScope   = PARSING_SCOPE_CASHDENOMINATIONS;
					currentHandler = new GetCashDenominationsParser(context);
				}
				else if (localName.equalsIgnoreCase("Vehicles"))
				{
					parsingScope   = PARSING_SCOPE_VEHICLES;
					currentHandler = new GetVehiclesParser(context);
				}
				
				else if (localName.equalsIgnoreCase("Users"))
				{
					parsingScope   = PARSING_SCOPE_USERS;
					currentHandler = new GetAllUserParser(context);
				}
				else if (localName.equalsIgnoreCase("Reasons"))
				{
					parsingScope   = PARSING_SCOPE_REASONS;
					currentHandler = new GetAllReasonsParser(context);
				}
				else if (localName.equalsIgnoreCase("Currencies"))
				{
					parsingScope   = PARSING_SCOPE_CURRENCIES;
					currentHandler = new GetCurrenciesParser(context);
				}
				else if (localName.equalsIgnoreCase("Agencies"))
				{
					parsingScope   = PARSING_SCOPE_AGENCIES;
					currentHandler = new AgenciesParser(context);
				}
				else if (localName.equalsIgnoreCase("Brands"))
				{
					parsingScope   = PARSING_SCOPE_BRANDS;
					currentHandler = new BrandsParser(context);
				}
				else if (localName.equalsIgnoreCase("Categories"))
				{
					parsingScope   = PARSING_SCOPE_CATEGORIES;
					currentHandler = new GetAllCategories(context);
				}
				else if (localName.equalsIgnoreCase("Items"))
				{
					parsingScope   = PARSING_SCOPE_ITEMS;
					currentHandler = new GetAllItemsParser(context);
				}
				
				else if (localName.equalsIgnoreCase("Price"))
				{
					parsingScope   = PARSING_SCOPE_PRICE;
					currentHandler = new GetPricingParser(context);
				}
				else if (localName.equalsIgnoreCase("CompetitorBrands"))
				{
					parsingScope   = PARSING_SCOPE_COMPETETIOR_BRANDS;
					currentHandler = new GetCompetitorDetail(context);
				}
				else if (localName.equalsIgnoreCase("UOMFactors"))
				{
					parsingScope   = PARSING_SCOPE_UOM_FACTORS;
					currentHandler = new UOMFactorParser(context);
				}
				else if (localName.equalsIgnoreCase("SellingSkusClassifications"))
				{
					parsingScope   = PARSING_SCOPE_SELLING_SKU_CLASSIFICATIONS;
					
					currentHandler = new GetSellingSKUClassificationsParser(context);
				}
				else if (localName.equalsIgnoreCase("CustomerSellingSKUGroups"))
				{
					parsingScope   = PARSING_SCOPE_CUSTOMER_SELLING_SKU_GROUPS;
					currentHandler = new CustomerSellingSKUGroupParser(context);
				}
				else if (localName.equalsIgnoreCase("SellingSKUs"))
				{
					parsingScope   = PARSING_SCOPE_SELLING_SKU;
					currentHandler = new GetSellingSKUParser(context);
				}
				else if (localName.equalsIgnoreCase("GroupSellingSKUClassifications"))
				{
					parsingScope   = PARSING_SCOPE_GROUP_SELLING_SKU_CLASSIFICATIONS;
					currentHandler = new GroupSellingSKUClassificationsParser(context);
				}
				else if (localName.equalsIgnoreCase("WareHouses"))
				{
					parsingScope   = PARSING_SCOPE_WAREHOUSE;
					currentHandler = new WareHouseParser(context);
				}
				
				///AppActiveStatusResponse
				else if (localName.equalsIgnoreCase("AppActiveStatusResponse"))
				{
					parsingScope   = PARSING_SCOPE_APP_ACTIVE_STATUS_RESPONSE;
					currentHandler = new GetAllMovements(context,empNo);
				}
				//VanStockDco
				//AssetMasterResponse
				else if (localName.equalsIgnoreCase("AssetMasterResponse"))
				{
					parsingScope   = PARSING_SCOPE_ASSET_MASTER_RESPONSE;
					currentHandler = new GetAssetMastersParser(context);
				}
				//OfflineDataResponse
				else if (localName.equalsIgnoreCase("OfflineDataResponse"))
				{
					parsingScope   = PARSING_SCOPE_OFFLINE_DATA_RESPONSE;
//					String salesManCode = 
					currentHandler = new GenerateOfflineDataParserNew(context,empNo);
				}
				//CustomerMastersData
				//not Channels
				else if (localName.equalsIgnoreCase("Channels"))
				{
					parsingScope   = PARSING_SCOPE_CHANNELS;
					currentHandler = new GenerateOfflineDataParserNew(context,empNo);
				}
				//not SubChannels
				else if (localName.equalsIgnoreCase("SubChannels"))
				{
					parsingScope   = PARSING_SCOPE_SUB_CHANNELS;
					currentHandler = new GenerateOfflineDataParserNew(context,empNo);
				}
				else if (localName.equalsIgnoreCase("Locations"))
				{
					parsingScope   = PARSING_SCOPE_LOCATIONS;
					currentHandler = new RegionListParser(context);
				}
				else if (localName.equalsIgnoreCase("Customers"))
				{
					parsingScope   = PARSING_SCOPE_CUSTOMERS;
					currentHandler = new GetCustomersByUserIdParser(context,empNo);
				}
				else if (localName.equalsIgnoreCase("DailyJourneyPlans"))
				{
					parsingScope   = PARSING_SCOPE_DAILY_JOURNEY_PLANS;
					currentHandler = new GetDailyJPAndRoute(context,empNo);
				}
				//DiscountAndInvoicesDataResponse
				else if (localName.equalsIgnoreCase("Discounts"))
				{
					parsingScope   = PARSING_SCOPE_DISCOUNTS;
					currentHandler = new GetDiscountParser(context);
				}
				else if (localName.equalsIgnoreCase("PendingSalesInvoices"))
				{
					parsingScope   = PARSING_SCOPE_PENDING_INVOICES;
					currentHandler = new CustomerPendingInvoiceParser(context);
				}
				else if (localName.equalsIgnoreCase("InitiativeResponse"))
				{
					parsingScope   = PARSING_SCOPE_INITIATIVE;
					currentHandler = new InitiativeDataParser(context);
				}

				else if (localName.equalsIgnoreCase("PromotionSCs"))
				{
					parsingScope   = PARSING_SCOPE_PROMOTION_SC;
					currentHandler = new PromotionSCParser(context);
				}
				
				//TrxHeaderResponse
				else if (localName.equalsIgnoreCase("TrxHeaders"))
				{
					parsingScope   = PARSING_SCOPE_TRANSACTION_HEADER_RESPONSE;
					currentHandler = new GetTrxHeaderForApp(context,empNo);
				}
//				//CustomerResponse
//				else if (localName.equalsIgnoreCase("Customers") && parsingScope != PARSING_SCOPE_CUSTOMERS)
//				{
//					parsingScope   = PARSING_SCOPE_CUSTOMER_RESPONSE;
//					currentHandler = new GetCustomersByUserIdParser(context,"");
//				}
				//DAPassCodeResponse
				else if (localName.equalsIgnoreCase("PassCodes"))
				{
					parsingScope   = PARSING_SCOPE_DAPASSCODE_RESPONSE;
					currentHandler = new DAPassCodeParser(context);
				}
				
				else if (localName.equalsIgnoreCase("PromotionsSync"))
				{
					parsingScope   = PARSING_SCOPE_PROMOTIONS;
					currentHandler = new GetPromotionsParser(context);
				}
				
				//TaskResponse
				else if (localName.equalsIgnoreCase("Tasks"))
				{
					parsingScope   = PARSING_SCOPE_TASK_RESPONSE;
					currentHandler = new TaskToDoParser(context);
				}
				else if (localName.equalsIgnoreCase("TrxLogHeaderDcos"))
				{
					parsingScope   = PARSING_SCOPE_TRX_LOG_HEADERS;
					currentHandler = new TrxLogHeaderParser(context);
				}
				
				else if (localName.equalsIgnoreCase("TrxLogDetailsDcos"))
				{
					parsingScope   = PARSING_SCOPE_tRX_LOG_DETAILS;
					currentHandler = new TrxLogDetailsParser(context);
				}
				else if (localName.equalsIgnoreCase("RecommendedQuantityResponse"))
				{
					parsingScope   = PARSING_SCOPE_RECOMMENDED_QUANTITY_RESPONSE;
					currentHandler = new GetRecommendedQuantityResponseParser(context);
				}
				else if (localName.equalsIgnoreCase("DetailedStockResponse"))
				{
					parsingScope   = PARSING_SCOPE_DETAIL_STOCK_RESPONSE;
					currentHandler = new GetDetailVanStockParser(context);
				}
				
				LogUtils.debug("localName_parser", localName);
				if(currentHandler != null)
					currentHandler.startElement(uri, localName, qName, attributes);
			}
			break;
			
			case (PARSING_SCOPE_BANKS):
			case (PARSING_SCOPE_SETTINGS):
			case (PARSING_SCOPE_CASHDENOMINATIONS):
			case (PARSING_SCOPE_VEHICLES): 
			case (PARSING_SCOPE_USERS): 
			case (PARSING_SCOPE_REASONS): 
			case (PARSING_SCOPE_CURRENCIES): 
			case (PARSING_SCOPE_AGENCIES): 
			case (PARSING_SCOPE_BRANDS): 
			case (PARSING_SCOPE_CATEGORIES): 
			case (PARSING_SCOPE_ITEMS): 
			case (PARSING_SCOPE_PRICE): 
			case (PARSING_SCOPE_COMPETETIOR_BRANDS): 
			case (PARSING_SCOPE_UOM_FACTORS):
			case (PARSING_SCOPE_SELLING_SKU_CLASSIFICATIONS):
			case (PARSING_SCOPE_CUSTOMER_SELLING_SKU_GROUPS):
			case (PARSING_SCOPE_SELLING_SKU):
			case (PARSING_SCOPE_GROUP_SELLING_SKU_CLASSIFICATIONS):
			case (PARSING_SCOPE_WAREHOUSE):
			case (PARSING_SCOPE_APP_ACTIVE_STATUS_RESPONSE):
			case (PARSING_SCOPE_ASSET_MASTER_RESPONSE): 
			case (PARSING_SCOPE_OFFLINE_DATA_RESPONSE):
			case (PARSING_SCOPE_CHANNELS): 
			case (PARSING_SCOPE_SUB_CHANNELS):
			case (PARSING_SCOPE_LOCATIONS): 
			case (PARSING_SCOPE_CUSTOMERS):
			case (PARSING_SCOPE_DAILY_JOURNEY_PLANS):
			case (PARSING_SCOPE_DISCOUNTS): 
			case (PARSING_SCOPE_TRANSACTION_HEADER_RESPONSE): 
			case (PARSING_SCOPE_CUSTOMER_RESPONSE): 
			case (PARSING_SCOPE_DAPASSCODE_RESPONSE): 
			case (PARSING_SCOPE_PENDING_INVOICES): 
			case (PARSING_SCOPE_INITIATIVE):
			case (PARSING_SCOPE_PROMOTION_SC):
			case (PARSING_SCOPE_TASK_RESPONSE): 
			case (PARSING_SCOPE_RECOMMENDED_QUANTITY_RESPONSE):
			case (PARSING_SCOPE_DETAIL_STOCK_RESPONSE):
			case (PARSING_SCOPE_TRX_LOG_HEADERS): 
			case (PARSING_SCOPE_tRX_LOG_DETAILS): 
			case (PARSING_SCOPE_PROMOTIONS): 
			{
				currentHandler.startElement(uri, localName, qName, attributes);
			}
			break;
		}
		
		if (parsingScope != PARSING_SCOPE_INVALID)
			sBuffer = new StringBuilder();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		switch (parsingScope)
		{
			case (PARSING_SCOPE_BANKS):
			case (PARSING_SCOPE_SETTINGS):
			case (PARSING_SCOPE_CASHDENOMINATIONS):
			case (PARSING_SCOPE_VEHICLES): 
			case (PARSING_SCOPE_USERS): 
			case (PARSING_SCOPE_REASONS): 
			case (PARSING_SCOPE_CURRENCIES): 
			case (PARSING_SCOPE_AGENCIES): 
			case (PARSING_SCOPE_BRANDS): 
			case (PARSING_SCOPE_CATEGORIES): 
			case (PARSING_SCOPE_ITEMS): 
			case (PARSING_SCOPE_PRICE): 
			case (PARSING_SCOPE_COMPETETIOR_BRANDS): 
			case (PARSING_SCOPE_UOM_FACTORS):
			case (PARSING_SCOPE_SELLING_SKU_CLASSIFICATIONS):
			case (PARSING_SCOPE_CUSTOMER_SELLING_SKU_GROUPS):
			case (PARSING_SCOPE_SELLING_SKU):
			case (PARSING_SCOPE_GROUP_SELLING_SKU_CLASSIFICATIONS):
			case (PARSING_SCOPE_WAREHOUSE):
			case (PARSING_SCOPE_APP_ACTIVE_STATUS_RESPONSE):
			case (PARSING_SCOPE_ASSET_MASTER_RESPONSE): 
			case (PARSING_SCOPE_OFFLINE_DATA_RESPONSE):
			case (PARSING_SCOPE_CHANNELS): 
			case (PARSING_SCOPE_SUB_CHANNELS):
			case (PARSING_SCOPE_LOCATIONS): 
			case (PARSING_SCOPE_CUSTOMERS):
			case (PARSING_SCOPE_DAILY_JOURNEY_PLANS):
			case (PARSING_SCOPE_DISCOUNTS): 
			case (PARSING_SCOPE_TRANSACTION_HEADER_RESPONSE): 
			case (PARSING_SCOPE_CUSTOMER_RESPONSE): 
			case (PARSING_SCOPE_DAPASSCODE_RESPONSE): 
			case (PARSING_SCOPE_PENDING_INVOICES): 
			case (PARSING_SCOPE_INITIATIVE):
			case (PARSING_SCOPE_PROMOTION_SC):
			case (PARSING_SCOPE_TASK_RESPONSE):
			case (PARSING_SCOPE_RECOMMENDED_QUANTITY_RESPONSE):
			case (PARSING_SCOPE_DETAIL_STOCK_RESPONSE):
			case (PARSING_SCOPE_TRX_LOG_HEADERS): 
			case (PARSING_SCOPE_tRX_LOG_DETAILS): 
			case (PARSING_SCOPE_PROMOTIONS): 
			{
				currentHandler.currentValue = sBuffer;
				currentHandler.endElement(uri, localName, qName);
				
				if(localName.equalsIgnoreCase("Banks")||
				   localName.equalsIgnoreCase("Vehicles")||
				   localName.equalsIgnoreCase("Users") ||
				   localName.equalsIgnoreCase("Reasons") ||
				   localName.equalsIgnoreCase("Currencies") ||
				   localName.equalsIgnoreCase("Agencies") ||
				   localName.equalsIgnoreCase("Brands") ||
				   localName.equalsIgnoreCase("Categories") ||
				   localName.equalsIgnoreCase("Items") ||
				   localName.equalsIgnoreCase("Price") ||
				   localName.equalsIgnoreCase("CompetitorBrands") || 
				   localName.equalsIgnoreCase("UOMFactors") ||
				   localName.equalsIgnoreCase("SellingSkusClassifications") ||
				   localName.equalsIgnoreCase("CustomerSellingSKUGroups") ||
				   localName.equalsIgnoreCase("SellingSKUs") ||
				   localName.equalsIgnoreCase("GroupSellingSKUClassifications") ||
				   localName.equalsIgnoreCase("WareHouses") ||
				   localName.equalsIgnoreCase("AppActiveStatusResponse") ||
				   localName.equalsIgnoreCase("AssetMasterResponse") ||
				   localName.equalsIgnoreCase("OfflineDataResponse") ||
				   localName.equalsIgnoreCase("Channels") || 
				   localName.equalsIgnoreCase("SubChannels") ||
				   localName.equalsIgnoreCase("Locations")||
				   localName.equalsIgnoreCase("Customers")||
				   localName.equalsIgnoreCase("DailyJourneyPlans")||
				   localName.equalsIgnoreCase("Discounts")||
				   localName.equalsIgnoreCase("TrxHeaders")||
				   localName.equalsIgnoreCase("PassCodes")||
				   localName.equalsIgnoreCase("Settings")||
				   localName.equalsIgnoreCase("CashDenominations")||
				   localName.equalsIgnoreCase("PendingSalesInvoices")||
				   localName.equalsIgnoreCase("PromotionSCs")||
				   localName.equalsIgnoreCase("RecommendedQuantityResponse")||
				   localName.equalsIgnoreCase("DetailedStockResponse")||
				   localName.equalsIgnoreCase("PromotionsSync")||
				   localName.equalsIgnoreCase("TrxLogHeaderDcos")||
				   localName.equalsIgnoreCase("TrxLogDetailsDcos")||
				   localName.equalsIgnoreCase("Tasks") ||
				   localName.equalsIgnoreCase("InitiativeResponse"))

					
				{
					LogUtils.errorLog("localName", localName+" Completed");
					parsingScope = PARSING_SCOPE_INVALID;
					currentHandler = null;
					
					if(syncProcessListner!=null){
						SYNCED_MAIN_MODULES++;
						int percentage=(int) ((SYNCED_MAIN_MODULES/TOTAL_MAIN_MODULES)*100);
						if(percentage<=100){
							String msg="Syncing Data..."+percentage+"%";
							syncProcessListner.progress(msg);
						}
					}
				}
			}
			break;
			
			case (PARSING_SCOPE_SERVERTIME):
			{
				if(localName.equalsIgnoreCase("ServerTime"))
				{
					syncLogDO.TimeStamp = sBuffer.toString();
//					preference.saveStringInPreference(ServiceURLs.GetCommonMasterDataSync+Preference.LAST_SYNC_TIME, sBuffer.toString());
//					parsingScope = PARSING_SCOPE_INVALID;
					if(serverTimeCount==0){
						preference.saveStringInPreference(ServiceURLs.GetCommonMasterDataSync+Preference.LAST_SYNC_TIME, sBuffer.toString());
						LogUtils.debug("loadIncrementalData", "syncLogDO.TimeStamp = > "+syncLogDO.TimeStamp);
					}
				}
				else if(localName.equalsIgnoreCase("ServerDateTime"))
				{
					syncLogDO.TimeStamp = sBuffer.toString();
//					preference.saveStringInPreference(ServiceURLs.GetCommonMasterDataSync+Preference.LAST_SYNC_TIME, sBuffer.toString());
//					parsingScope = PARSING_SCOPE_INVALID;
					if(serverTimeCount==0){
						preference.saveStringInPreference(ServiceURLs.GetCommonMasterDataSync+Preference.LAST_SYNC_TIME, sBuffer.toString());
						LogUtils.debug("loadIncrementalData", "syncLogDO.TimeStamp = > "+syncLogDO.TimeStamp);
					}
				}
				if(localName.equalsIgnoreCase("ModifiedDate"))
				{
					syncLogDO.UPMJ = sBuffer.toString();
//					preference.saveStringInPreference(ServiceURLs.GetCommonMasterDataSync+Preference.LAST_SYNC_TIME, sBuffer.toString());
//					parsingScope = PARSING_SCOPE_INVALID;
					if(serverTimeCount==0){
						preference.saveStringInPreference(ServiceURLs.GetCommonMasterDataSync+Preference.LAST_SYNC_TIME, sBuffer.toString());
						LogUtils.debug("loadIncrementalData", "syncLogDO.UPMJ = > "+syncLogDO.UPMJ);
					}
				}
				else if(localName.equalsIgnoreCase("Status"))
				{
					syncLogDO.action =  /*sBuffer.toString();*/currentValue.toString();
				}
				if(localName.equalsIgnoreCase("ModifiedTime"))
				{
					syncLogDO.UPMT = sBuffer.toString();
//					preference.saveStringInPreference(ServiceURLs.GetCommonMasterDataSync+Preference.LAST_SYNC_TIME, sBuffer.toString());
					parsingScope = PARSING_SCOPE_INVALID;
					syncLogDO.entity = ServiceURLs.GetCommonMasterDataSync;
//					new SynLogDA().insertSynchLog(syncLogDO);
					if(serverTimeCount == 0){
						syncCompleteLogDO.entity = ServiceURLs.GetCommonMasterDataSync;
						syncCompleteLogDO.TimeStamp = syncLogDO.TimeStamp;
						syncCompleteLogDO.action = syncLogDO.action;
						syncCompleteLogDO.UPMJ = syncLogDO.UPMJ;
						syncCompleteLogDO.UPMT = syncLogDO.UPMT;
						preference.saveStringInPreference(ServiceURLs.GetCommonMasterDataSync+Preference.LAST_SYNC_TIME, sBuffer.toString());
						new SynLogDA().insertSynchLog(syncLogDO);
						serverTimeCount++;
					}
				}
			}
			break;
			
			case (PARSING_SCOPE_INVALID):
			{
				if(localName.equalsIgnoreCase("GetAllDataSyncResponse"))
				{
					if(syncProcessListner!=null){
						SYNCED_MAIN_MODULES=(int) TOTAL_MAIN_MODULES;
						int percentage=(int) ((SYNCED_MAIN_MODULES/TOTAL_MAIN_MODULES)*100);
						String msg="Syncing Data..."+percentage+"%";
						syncProcessListner.progress(msg);
					}
//					new SynLogDA().insertSynchLog(syncCompleteLogDO);
					preference.saveIntInPreference(Preference.SYNC_STATUS, 1);
					preference.commitPreference();
				}
//					preference.commitPreference();
			}
			break;
		}
	}

	public void characters(char[] ch, int start, int length) 
	{
		if (parsingScope == PARSING_SCOPE_INVALID || ch == null || length == 0 || sBuffer == null)
			return;

		try
		{
			sBuffer.append(ch, start, length);
		}
		catch (Exception e)
		{
	   		LogUtils.errorLog(this.getClass().getName(), "XML ch[] appending exception:"+e.getMessage() );
		}
	}
}
