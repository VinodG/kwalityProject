package com.winit.alseer.salesman.common;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Environment;
import android.widget.LinearLayout;

import com.winit.alseer.salesman.dataaccesslayer.ScanResultObject;
import com.winit.alseer.salesman.dataobject.CategoryDO;
import com.winit.alseer.salesman.dataobject.FeelingsDO;
import com.winit.alseer.salesman.dataobject.MenuDO;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.kwalitysfa.salesman.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class AppConstants 
{
	public static final int SYNC_COUNT 	= 400;
	public static final int TRN_NUMBER_LENGTH 	= 15;
    public static final double DIFFERENCE_BW_CREDIT_AVAILABLE = 100 ;
//    public static final double DISTANCE = 0;
    public static final double DISTANCE = 250;
    public static int DIVICE_WIDTH, DIVICE_HEIGHT;
	public static String APPLICATION_ID					="com.winit.alseer.salesman";
	public static String ACTION_SQLITE_FILE_DOWNLOAD	=	"com.winit.alseer.salesman.ACTION_SQLITE_FILE_DOWNLOAD";
	public static boolean isServeyCompleted = false;
	
//	public static String ALARM_TIME_LOAD_NEW_REQ_1		=	"";
//	public static String ALARM_TIME_LOAD_NEW_REQ_2		=	"";
	public static int ALARM_TIME_REQ_CODE1				=	1;
	public static int ALARM_TIME_REQ_CODE2				=	2;
	public static String ALARM_TIME						=	"com.winit.alseer.salesman.PARAMETER.ALARM_TIME";
	public static String ACTION_MENUCLICK				=	"com.winit.alseer.salesman.ACTION.MENUCLICK";
	public static String ACTION_LOGOUT					=	"com.winit.alseer.salesman.ACTION.LOGOUT";
	public static String ACTION_HOUSE_LIST				=	"com.winit.alseer.salesman.ACTION_HOUSE_LIST_NEW";
	public static String ACTION_HOUSE_LIST_NEW			=	"com.winit.alseer.salesman.ACTION_HOUSE_LIST";
	public static String ACTION_GOTO_TELEORDERS			=	"com.winit.alseer.salesman.ACTION_GOTO_TELEORDERS";
	public static String ACTION_GOTO_SETTINGS_FINISH	=	"com.winit.alseer.salesman.ACTION_GOTO_SETTINGS_FINISH";
	public static String ACTION_GOTO_AR					=	"com.winit.alseer.salesman.ACTION_GOTO_AR";
	public static String ACTION_GOTO_CRLMAIN			=	"com.winit.alseer.salesman.ACTION_GOTO_CRLMAIN";
	public static String ACTION_GOTO_CRL				=	"com.winit.alseer.salesman.ACTION_GOTO_CRL";
	public static String ACTION_GOTO_HOME				=	"com.winit.alseer.salesman.ACTION_GOTO_HOME";
	public static String ACTION_GOTO_JOURNEY			=	"com.winit.alseer.salesman.ACTION_GOTO_JOURNEY";
	public static String ACTION_GOTO_NEXT_DAY_JOURNEY	=	"com.winit.alseer.salesman.ACTION_GOTO_NEXT_DAY_JOURNEY";
	public static String ACTION_GOTO_HOME1				=	"com.winit.alseer.salesman.ACTION_GOTO_HOME1";
	public static String ACTION_REFRESH_LOAD_REQUEST	=	"com.winit.alseer.salesman.ACTION_REFRESH_LOAD_REQUEST";
	public static String ACTION_REFRESH_CHECKIN_OPTION	=	"com.winit.alseer.salesman.ACTION_REFRESH_CHECKIN_OPTION";
	public static long TIME_30_DAYS = -2592000000L;
	
	public static int LOAD_STOCK   = 1;
	public static int UNLOAD_STOCK = 2;
	public static int LOAD_VAN_TRANSFER_STOCK   = 3;
	public static int UNLOAD_VAN_TRANSFER_STOCK = 4;
	public static final String ONE_MONTH_DATA = "ONE MONTH DATA";
	public static String KwalityLogoPath = "";
	public static String CategoryIconsPath;
	public static String productCatalogPath;
	public boolean isDayStarted = false;
	public static int STORE_SIGN_START  = 1;
	public static int STORE_SIGN_END 	= 2;
	public static int SALES_SIGN_START  = 3;
	public static int SALES_SIGN_END 	= 4;
	public static String DATABASE_PATH 	= "";
	public static String DATABASE_NAME 	= "salesman.sqlite";
	public static String ORDER_LOG 	= "OrderLog.txt";
	public static Typeface Roboto_Condensed_Bold,Roboto_Condensed;
	
	public static final String DIVISION = "DIVISION";
	public static final String CheckGEOCODE = "CheckGEOCODE";
	public static final int CheckGEOCODETrue = 0;
	
	
	/*****************VERSION UPDATE*****************************/
	public final static int MAJOR_APP_UPDATE = 1;
	public final static int NORMAL_APP_UPDATE = 2;
	public final static int MINOR_APP_UPDATE = 3;
	
	public final static int VER_CHANGED 	= 100;
	public final static int VER_NOT_CHANGED = 101;
	public final static int VER_NO_BUTTON_CLICK = 102;
	public final static int VER_DO_EOT = 103;
	public final static int VER_DO_EOT_ADEOT = 104;
	public final static int VER_DO_ADEOT = 105;
	public final static int VER_UNABLE_TO_UPGRADE = 106;
	
	public final static int CALL_FROM_SETTINGS = 300;
	public final static int CALL_FROM_LOGIN = 301;
	public final static int CALL_FROM_EOT = 302;
	public final static int CALL_FROM_NOTIFICATION = 303;
	
	public static String ACTION_APP_UPGRADE				=	"com.winit.alseer.salesman.ACTION.APP_UPGRADE";
	/************************************************************/
	
	public static String COLLECTIONS    = "Collection";
	public static String INVOICES    = "Invoices";
	public static String CREDITNOTE    = "CreditNote";
	public static String STORECHECK    = "D-Check";
	
	public static String Action_CheckIn 	= "CheckInOption";
	public static String Type_StoreCheck 	= "StoreCheck";
	public static String Type_BeforService 	= "BeforService";
	public static String Type_AfterService 	= "AfterService";
	public static String Type_AfterPrintInvoice 	= "AfterPrintInvoice";
	public static String Type_Task 			= "Task";
	public static String Type_Assets 		= "Assets";
	public static String Type_Collections 	= "Collections";
	public static String Type_SalesOrder 	= "SalesOrder";
	public static String Type_FOCOrder	 	= "FOCOrder";
	public static String Type_PresalesOrder = "Presales";
	public static String Type_ReturnOrder 	= "ReturnOrder";
	public static String Type_MissedOrder 	= "MissedOrder";
	public static String Type_AdvancedOrder = "AdvancedOrder";
	public static String Type_SavedOrder 	= "SavedOrder";
	public static String Type_CustomerInfo  = "CustomerInfo";
	
	public static String Invoice_Type_Cash  	= "Cash Invoice";
	public static String Invoice_Type_Credit  	= "Credit Invoice";
	
	public static String Return_Type_Salable  		= "Salable";
	public static String Return_Type_Non_Salable  	= "Non Salable";
	
	public static final int Store_Check 	= 1000;
	public static final int Capture_Inventory 	= 1001;
	
	public static final int CAMERA_PIC_BEFORE_SERVICE = 1000;
	public static final int CAMERA_PIC_AFTER_SERVICE = 2000;

	public static final int CAMERA_PIC_AFTER_INVOICE_PRINT = 1002;

	public static final int ORDER_PUSH_LIMIT 		= 10;
	public static final int MAX_ORDER_PUSH_LIMIT 	= 5;
	
	public static String Device_Display_Width 		= Preference.DEVICE_DISPLAY_WIDTH;
	public static String Device_Display_Height 	  	= Preference.DEVICE_DISPLAY_HEIGHT;
	public static int DEVICE_DISPLAY_WIDTH_DEFAULT  = 800;
	public static int DEVICE_DISPLAY_HEIGHT_DEFAULT	= 1216;
	
	public static final String DEFAULT_CUSTOMER_PRICING_KEY = "CV";
	public static String JOURNEY_CALL 			= "Journey Call";
	
	public static String CUSTOMER_STATEMENT_SALES_INVOICE = "Sales Invoice";
	public static String CUSTOMER_STATEMENT_CREDIT_NOTE   = "Credit Note";
	public static String CUSTOMER_STATEMENT_PAYMENT  	  = "Payment";
	public static String CUSTOMER_STATEMENT_COLLECTION    = "Collection";
	
	public static final String HighlightItem    = "Yellow";
	
	public static String presellerOptionGT1[]   = {"Journey Plan","Load Management","My Customers",
		"AR Collection","Stock Inventory","Stock Verification", 
		"Add New Customer","Order Summary", "Payment Summary", 
		"Product Catalog","Today`s Dashboard","EOT", 
		"New Launches","Settings",	"Asset Request",
		"Competitor","Transfer", "About Application",
		"NotifiCations", "Logout","footer"};
	
	public static String presellerOptionNewGT[]   = {"Today Journey Plan","Product Catalog", "My Customers",
		"Load Management","Execution Summary","Stock Inventory",/*"Add New Customers",*/"Cash Denomination","Daily Summary","EOT","Others","Logout","footer"};
	
	public static int presellerOptionNewLoogsGT[]   = {R.drawable.journey_plan_icon,R.drawable.product_catalog_icon,R.drawable.mycustomer_icon,
		R.drawable.load_management_icon,R.drawable.orders_summary_icon,R.drawable.stock_invontery,/*R.drawable.add_necustomer_icon,*/R.drawable.cashdinomination,R.drawable.dailysummary,R.drawable.eot_icon,/*R.drawable.order_summary,*/
		R.drawable.about_application_icon,R.drawable.logout_menu_icon,R.drawable.footer_new};
	
	/******************************************************************************************************/
	public static String menuOptionPreseller[]   = {"Journey Plan","Product Catalog","My Customers",
		"Execution Summary",/*"Add New Customers",*/"Cash Denomination","Daily Summary","EOT","Others","Logout","footer"};
	
	public static int menuOptionPresellerLogos[]   = {R.drawable.journey_plan_icon,R.drawable.product_catalog_icon,R.drawable.mycustomer_icon,
		R.drawable.orders_summary_icon,/*R.drawable.add_necustomer_icon,*/R.drawable.cashdinomination,R.drawable.dailysummary,R.drawable.eot_icon,R.drawable.about_application_icon,
		R.drawable.logout_menu_icon,R.drawable.footer_new};
	
	public static String checkinMenuOptionPresellerGT[]   = {"Customer Dashboard", "Execution Summary", "Product Catalog",
		/*"New Launches",*/"Capture Competitor","Settings","Checkout"/*,"Add New Customers"*/};
	
	public static int checkinMenuOptionPresellerLogosGT[]   = {R.drawable.journey_plan_icon, R.drawable.arcollection_icon,
		R.drawable.product_catalog_icon,/*R.drawable.new_launch_icon,*/R.drawable.competitor_icon,R.drawable.settings_icon,R.drawable.order_summary/*,R.drawable.add_necustomer_icon*/};
	
	public static String executionSummaryPresellerOption[]   = {"Order Summary","Payment Summary","Survey","Competitive Execution",
		"Asset Activities","Order Trace","Log Report"};
	
	public static int executionSummaryPresellerOptionLoogs[]   = {R.drawable.order_summary,R.drawable.paymentsummary_icon,
		R.drawable.invontery,R.drawable.competitor_icon,R.drawable.assets_request_icon,R.drawable.assets_request_icon,R.drawable.paymentsummary_icon};
	/******************************************************************************************************/
	
	public static String loadMangeMentOption[]   = {"Van Stock",/*"Van Transfer",*/"Receive/Verify Stock"};
	
	public static int loadMangeMentLoogs[]   = {R.drawable.transfer_in,/*R.drawable.transfer_in,*/R.drawable.stockverification_icon};

	public static String othersOption[]   = {"Settings","Capture Competitor","About Application"};
	
	public static int othersOptionLoogs[]   = {R.drawable.settings_icon,R.drawable.competitor_icon,R.drawable.about};

	public static String executionSummaryOption[]   = {"Order Summary","Payment Summary","Survey","Competitive Execution","Asset Activities","Log Report"};
	
	public static int executionSummaryOptionLoogs[]   = {R.drawable.order_summary,R.drawable.paymentsummary_icon,R.drawable.invontery,R.drawable.competitor_icon,R.drawable.assets_request_icon,R.drawable.invontery};
	
	/****************************************************************************************/
	/*******************************After Checkin********************************************/
	public static String checkinOptionNewGT[]   = {"Customer Dashboard","Load Management","Execution Summary",
		"Product Catalog",/*"Item Details",*/"Capture Competitor","Settings","Checkout"/*,"Add New Customers"*/};
	
	public static int checkinOptionNewLoogsGT[]   = {R.drawable.journey_plan_icon,R.drawable.arcollection_icon,
		R.drawable.load_management_icon,R.drawable.product_catalog_icon,/*R.drawable.product_catalog_icon,*/R.drawable.competitor_icon,
		R.drawable.settings_icon,R.drawable.order_summary/*,R.drawable.add_necustomer_icon*/};
	
	public static String checkinexecutionSummaryOption[]   = {"Order Summary","Payment Summary","Survey","Competitive Execution","Asset Activities"};
	
	public static int checkinexecutionSummaryOptionLoogs[]   = {R.drawable.order_summary,R.drawable.paymentsummary_icon,
		R.drawable.invontery,R.drawable.competitor_icon,R.drawable.assets_request_icon};

	/****************************************************************************************/
	
	public static int presellerOptionLoogsGT[] = {R.drawable.journey_plan_icon,R.drawable.load_management_icon,R.drawable.mycustomer_icon,
		R.drawable.arcollection_icon,R.drawable.invontery, R.drawable.stockverification_icon,
		R.drawable.add_necustomer_icon,R.drawable.orders_summary_icon,R.drawable.paymentsummary_icon,
		R.drawable.product_catalog_icon,R.drawable.order_summary, R.drawable.eot_icon,
		R.drawable.new_launch_icon, R.drawable.settings_icon,R.drawable.assets_request_icon,
		R.drawable.competitor_icon,R.drawable.transfer_in,R.drawable.about_application_icon,
		R.drawable.about,R.drawable.logout_menu_icon,R.drawable.footer_new};
	
	public static int OLD_COIN_BOX_SCAN_REQUEST_CODE = 100000000;
	public static int NEW_COIN_BOX_SCAN_REQUEST_CODE = 200;
	public static int VM_PHOTOGRAPH_REQUEST_CODE = 300;
	public static ScanResultObject objScanResultObject;
    public static Vector<CategoryDO> vecCategories; 
	public static String RETURN_REASONS = "Return request reason";
    public static HashMap<String, CategoryDO> hmCateogories;
	public static int REQUEST_CODE = 0;
	public static String VendingMachineName = "";
    public static ProductDO objItem;
    public static int invoiceNo = 10;
    public static int customerSiteno = 10;
    public static String ITEM_TYPE_ORDER = "O";
    public static String ITEM_TYPE_PROMO = "F";
    
    public static boolean isRecomendedChanged = false;
    public static String SUB_CHANEL = "Grocery_123";
    
    public static boolean isTaskCompleted = false;
    
    
    public static final String SALESMAN_GT 	= "General Trade";
    public static final String SALESMAN_MT 	= "Modern Trade";
    public static final String SALESMAN_PD 	= "Parlour Delivery";
    public static final String SALESMAN_AM 	= "Asset Manager";
    
    public static final String MUST_HAVE 	= "Must Have";
    public static final String NEW_LAUNCHES	= "New Launches";
    public static final String FAVOURRITE	= "Favourite";
    
    public static final int SALES_ORDER_TYPE = 1, ADVANCE_ORDER_TYPE =0;
    public static final String PROMO_TYPE_RANGE 	= "RP";
    
    
    
    public static HashMap<String, Vector<ProductDO>> hmCapturedInventory;
    
    
    public static String HHOrder = "HH order";
    public static String APPORDER = "App order";
    public static String ADVANCE_ORDER = "Advance order";
    public static String TELEORDER = "Tele order";
    public static String CURRENTORDER = "Current Order";
    public static String FREE_DELIVERY_ORDER = "Free Delivery";
	public static long TIME_FOR_BACKGROUND_TASK = 10*60000;
	public static String CATEGORY	=	"4G";
	
	public static boolean CheckIN;
	public static boolean isSumeryVisited;
	public static String SKIPPED_CUSTOMERS;
	public static ArrayList<String> skippedCustomerSitIds;
	public static String strCheckIN;
	
	public static final String Movement	=	"Movement";
	public static final String Customer	=	"Customer";
	public static final String Order	=	"Order";
	public static final String GRV		=	"GRV";
	public static final String SAVED	=	"Saved";
	public static final String FOC		=	"FOC";
	public static final String Receipt	=	"Receipt";
	
	/***********************Food********************************/
	public static final String Food_Movement	=	"FMovement";
	public static final String Food_Order		=	"FOrder";
	public static final String Food_GRV			=	"FGRV";
	public static final String Food_SAVED		=	"FSaved";
	public static final String Food_FOC			=	"FFOC";
	public static final String Food_Receipt		=	"FReceipt";
	/***********************Food********************************/

//	AppConstants.FOrder
	public static final String TPT_Movement	=	"TPMovement";
	public static final String TPT_Order		=	"TPOrder";
	public static final String TPT_GRV			=	"TPGRV";
	public static final String TPT_SAVED		=	"TPSaved";
	public static final String TPT_FOC			=	"TPFOC";
	public static final String TPT_Receipt		=	"TPReceipt";

	public static final String RETURNORDER = "HH Return Order";
	public static final String REPLACEMETORDER = "Replace Order";
	public static final String LPO_ORDER 	= "LPO Order";
	public static final String MOVE_ORDER 	= "MOVE Order";
	public static final String TRNS_TYPE_IN = "IN";
	public static final String TRNS_TYPE_OUT = "OUT";
	public static String SKIP_JOURNEY_PLAN = "Skip Customer";
	public static String ZERO_SALES = "Zero Sales";
	public static String FOC_REASON = "FOC Reason";
	public static String PROMOTION_REASON = "Discount Reason";
	
	public static float DEVICE_DENSITY = 0;
	public static int DEVICE_WIDTH = 0;
	public static int DEVICE_HEIGHT = 0;
	
	public static String APPFOLDERNAME =".Kwality";
	public static String APPMEDIAFOLDERNAME = "KwalityImages";
	public static String APPMEDIALOGOFOLDERNAME = "KwalityLogo";
	public static String APPFOLDERPATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+APPFOLDERNAME+"/";
	public static String APPMEDIAFOLDERPATH = APPFOLDERPATH+APPMEDIAFOLDERNAME+"/"; 
	public static File   APPFOLDER = new File(APPFOLDERPATH);
	public static File   APPMEDIAFOLDER = new File(APPMEDIAFOLDERPATH);

	public static final int MAX_IMAGE_SIZE = 700;
	public static String imagePath = "";
	public static Bitmap assetbarcodeimagePath;
	public static Bitmap assettempimagePath;
	public static int DENISITY;
	public static int  AEDBelow2000 = 1;
	public static int  AEDBelow3000 = 2;
	public static int  AEDBelow1000 = 3;
	public static int  AEDAbove4000 = 4;
	public static int  AEDZero = 0;
	
	public static String Task1 = "Capture real photo of the shelf to validate the accuracy of the plan";
	public static String Task2 = "Capture Competitor Promotions & Marketing initiatives";
	public static String Task3 = "Consumer behaviour survey for Frozen products under brand building strategy";
	
	public static String Task_Title1 = "Capture Shelf Photo";
	public static String Task_Title2 = "Competitor Promotions";
	public static String Task_Title3 = "In store - Consumer Behaviour Survey";
	
	public static final int DISCOUNT_ALL_ITEM=0;
	public static final int DISCOUNT_ITEM=1;
	public static final int DISCOUNT_CATEGORY=2;
	public static final int DISCOUNT_BRAND=3;
	
	
	public static final int DISCOUNT_PERCENTAGE = 0;
	public static final int DISCOUNT_AMOUNT = 1;
	public static final int TYPE_ORDER = 1;
	
	public static final String CUSTOMER_CHANNEL_MODERN  = "19.MODERN TRADE";
	public static final String CUSTOMER_CHANNEL_GENERAL = "19.GENERAL TRADE";
	public static final String CUSTOMER_CHANNEL_HORECA  = "19.HORECA";
	public static final String CUSTOMER_CHANNEL_PARLOUR = "19.DELIVERY SERVICE";
	
	
	public static final String CUSTOMER_TYPE_CREDIT = "CREDIT";
	public static final String CUSTOMER_TYPE_CASH = "CASH";
	
	public static final String PAYMENT_NOTE_CASH   = "CASH";
	public static final String PAYMENT_NOTE_CHEQUE = "CHEQUE";
	
	public static final int MAXIMUM_DISATNCE_OUTLET = 2000;
	public static int GCMRegistrationAttempts ;
	public final static String SENDER_ID = "1003233556706";
//	public final static String SENDER_ID = "715019169923";
	public final static int MaximumGCMRegistrationAttempts = 3;
	
	public static final String CUSTOMER_SIGN = "customer";
	public static final String SALESMAN_SIGN = "salesman";
	
	public static final String ACCOUNT_COPY    = "ACCOUNT_COPY";
	public static final String COLLECTION_COPY = "COLLECTION_COPY";
	public static HashMap<String, String> hmSurvey;
	
	public static ArrayList<FeelingsDO> vecFeelingsDOs;
	
	public static String currentLat = "17.76142";
	public static String currentLng = "78.22424";
	
	//PAYMENT
	public static final String NOTE_TYPE_1000  = "1000_RUPEE_NOTE";
	public static final String NOTE_TYPE_500   = "500_RUPEE_NOTE";
	public static final String NOTE_TYPE_200   = "200_RUPEE_NOTE";
	public static final String NOTE_TYPE_100   = "100_RUPEE_NOTE";
	
	public static final int NOTE_1000_VALUE = 1000;
	public static final int NOTE_500_VALUE  = 500;	
	public static final int NOTE_200_VALUE  = 200;	
	public static final int NOTE_100_VALUE  = 100;
	public static final int CART_TYPE = 100;	
	
	public static final String PRIMARY_SHELF_PLACEMENT="Primary Shelf Placement";
	public static final String POSM_AVAILABILITY="POSM Availability";  
	
	
	public static String iconpaths = "";
	
	public static OptionsNames getMediaOptionsTypes(String option)
	 {
		 if(option.equalsIgnoreCase("IMAGE"))
			 return OptionsNames.IMAGE;
		 else if(option.equalsIgnoreCase("VIDEO"))
			 return OptionsNames.VIDEO;
		 else if(option.equalsIgnoreCase("AUDIO"))
			 return OptionsNames.AUDIO;
		return null;
	 }
	
	public static String CURRECNY_CODE = " AED";
	public static int DEVICE_DISPLAY_HEIGHT;
	public static final int KEYBOARD_OFFSET = 20;
	
	public static Vector<String> hashmapKeySet(HashMap<String, Vector<ProductDO>> hmdetails) 
	{
		Vector<String> vecCategoryIds = new Vector<String>();
		if(hmdetails != null)
		{
			Set<String> keySet = hmdetails.keySet();
			Iterator<String> iterator = keySet.iterator();
			while(iterator.hasNext())
				vecCategoryIds.add(iterator.next());
		}
		
		return vecCategoryIds;
	}
	
	public static Vector<String> hashmapKeySetLayout(HashMap<String, LinearLayout> hmdetails) 
	{
		Set<String> keySet = hmdetails.keySet();
		Iterator<String> iterator = keySet.iterator();
		Vector<String> vecCategoryIds = new Vector<String>();
		while(iterator.hasNext())
			vecCategoryIds.add(iterator.next());
		
		return vecCategoryIds;
	}
	public static OptionsNames getOptionsTypes(String option)
	 {
		 if(option.equalsIgnoreCase("CHECKBOX") )
			 return OptionsNames.CHECKBOX;
		 else  if(option.equalsIgnoreCase("IMAGE")||option.equalsIgnoreCase("VIDEO")||option.equalsIgnoreCase("AUDIO"))
			 return OptionsNames.MEDIA;
		 else  if(option.equalsIgnoreCase("YESNO"))
			 return OptionsNames.YESNO;
		 else  if(option.equalsIgnoreCase("RADIO"))
			 return OptionsNames.RADIO;
		 else  if(option.equalsIgnoreCase("SINGLE_LINE") || option.equalsIgnoreCase("MULTI_LINE") || option.equalsIgnoreCase("NUMERIC"))
			 return OptionsNames.SINGLE_LINE;
		 else  if(option.equalsIgnoreCase("DROPDOWN"))
			 return OptionsNames.DROPDOWN;
		 else  if(option.equalsIgnoreCase("EMOTION"))
			 return OptionsNames.EMOTION;
		 else  if(option.equalsIgnoreCase("STAR"))
			 return OptionsNames.STAR;
		return null;
			 
	 }
	
	//LOAD
	public static final String LOAD_VAN  	= "VL";
	public static final String UNLOAD_VAN   = "VU";
	public static final String UNLOAD_RVU   = "RVU";
	public static final String GRVD   = "GRVD";
	public static final String GRVG   = "GRVG";
	
	public static final String TRANSFER   	= "TL";
	public static final String GOLDEN_STORE	= "PerfectStorePercentage";
	
	public static final int GOLDEN_STORE_VAL = 1;
	public static final String DATE_KEY = "DATE_KEY";
	public static String PARTIAL_PAYMENT = "Partial Payment";
	public static String FORCE_CHECKIN = "Force Checkin";
	
	public static Vector<MenuDO> loadMenu()
	{
		Vector<MenuDO> vecMenuDOs = new Vector<MenuDO>();
		
		for (int i = 0; i < presellerOptionNewGT.length; i++) 
		{
			MenuDO menuDO = new MenuDO();
			menuDO.menuName = presellerOptionNewGT[i];
			menuDO.menuImage = presellerOptionNewLoogsGT[i];
			
			if(menuDO.menuName.equalsIgnoreCase("Load Management"))
			{
				for (int j = 0; j < loadMangeMentOption.length; j++) 
				{
					MenuDO loadmenuDO = new MenuDO();
					loadmenuDO.menuName = loadMangeMentOption[j];
					loadmenuDO.menuImage = loadMangeMentLoogs[j];
					menuDO.vecMenuDOs.add(loadmenuDO);
				}
				
			}
			else if(menuDO.menuName.equalsIgnoreCase("Execution Summary"))
			{
				for (int j = 0; j < executionSummaryOption.length; j++) 
				{
					MenuDO executionMenuDO = new MenuDO();
					executionMenuDO.menuName = executionSummaryOption[j];
					executionMenuDO.menuImage = executionSummaryOptionLoogs[j];
					menuDO.vecMenuDOs.add(executionMenuDO);
				}
			}
			
			else if(menuDO.menuName.equalsIgnoreCase("Others"))
			{
				for (int j = 0; j < othersOption.length; j++) 
				{
					MenuDO executionMenuDO = new MenuDO();
					executionMenuDO.menuName = othersOption[j];
					executionMenuDO.menuImage = othersOptionLoogs[j];
					menuDO.vecMenuDOs.add(executionMenuDO);
				}
			}
			
			vecMenuDOs.add(menuDO);
		}
		
		return vecMenuDOs;
	}
	
	public static Vector<MenuDO> loadCheckinMenu()
	{
		Vector<MenuDO> vecMenuDOs = new Vector<MenuDO>();
		
		for (int i = 0; i < checkinOptionNewGT.length; i++) 
		{
			MenuDO menuDO = new MenuDO();
			menuDO.menuName = checkinOptionNewGT[i];
			menuDO.menuImage = checkinOptionNewLoogsGT[i];
			
			if(menuDO.menuName.equalsIgnoreCase("Load Management"))
			{
				for (int j = 0; j < loadMangeMentOption.length; j++) 
				{
					MenuDO loadmenuDO = new MenuDO();
					loadmenuDO.menuName = loadMangeMentOption[j];
					loadmenuDO.menuImage = loadMangeMentLoogs[j];
					menuDO.vecMenuDOs.add(loadmenuDO);
				}
				
			}
			else if(menuDO.menuName.equalsIgnoreCase("Execution Summary"))
			{
				for (int j = 0; j < checkinexecutionSummaryOption.length; j++) 
				{
					MenuDO executionMenuDO = new MenuDO();
					executionMenuDO.menuName = checkinexecutionSummaryOption[j];
					executionMenuDO.menuImage = checkinexecutionSummaryOptionLoogs[j];
					menuDO.vecMenuDOs.add(executionMenuDO);
				}
			}
			
			vecMenuDOs.add(menuDO);
		}
		
		return vecMenuDOs;
	}
	
	public static Vector<MenuDO> loadMenuforPreseller(String SALESMAN_TYPE)
	{
		Vector<MenuDO> vecMenuDOs = new Vector<MenuDO>();
		
		for (int i = 0; i < menuOptionPreseller.length; i++) 
		{
			MenuDO menuDO = new MenuDO();
			menuDO.menuName = menuOptionPreseller[i];
			menuDO.menuImage = menuOptionPresellerLogos[i];
			
//			if(SALESMAN_TYPE.equalsIgnoreCase(Preference.PRESELLER))
//			{
				if(menuDO.menuName.equalsIgnoreCase("Execution Summary"))
				{
					for (int j = 0; j < executionSummaryOption.length; j++) 
					{
						MenuDO executionMenuDO = new MenuDO();
						executionMenuDO.menuName = executionSummaryOption[j];
						executionMenuDO.menuImage = executionSummaryOptionLoogs[j];
						menuDO.vecMenuDOs.add(executionMenuDO);
					}
				}
//			}
//			else
//			{
//				if(menuDO.menuName.equalsIgnoreCase("Execution Summary"))
//				{
//					for (int j = 0; j < executionSummaryPresellerOption.length; j++) 
//					{
//						MenuDO executionMenuDO = new MenuDO();
//						executionMenuDO.menuName = executionSummaryPresellerOption[j];
//						executionMenuDO.menuImage = executionSummaryPresellerOptionLoogs[j];
//						menuDO.vecMenuDOs.add(executionMenuDO);
//					}
//				}
//			}
			
			if(menuDO.menuName.equalsIgnoreCase("Others"))
			{
				for (int j = 0; j < othersOption.length; j++) 
				{
					MenuDO executionMenuDO = new MenuDO();
					executionMenuDO.menuName = othersOption[j];
					executionMenuDO.menuImage = othersOptionLoogs[j];
					menuDO.vecMenuDOs.add(executionMenuDO);
				}
			}
			vecMenuDOs.add(menuDO);
		}
		return vecMenuDOs;
	}
	
	public static Vector<MenuDO> loadCheckinMenuforPreseller(String SALESMAN_TYPE)
	{
		Vector<MenuDO> vecMenuDOs = new Vector<MenuDO>();
		
		for (int i = 0; i < checkinMenuOptionPresellerGT.length; i++) 
		{
			MenuDO menuDO = new MenuDO();
			menuDO.menuName = checkinMenuOptionPresellerGT[i];
			menuDO.menuImage = checkinMenuOptionPresellerLogosGT[i];
			
//			if(SALESMAN_TYPE.equalsIgnoreCase(Preference.PRESELLER))
//			{
				if(menuDO.menuName.equalsIgnoreCase("Execution Summary"))
				{
					for (int j = 0; j < executionSummaryOption.length; j++) 
					{
						MenuDO executionMenuDO = new MenuDO();
						executionMenuDO.menuName = executionSummaryOption[j];
						executionMenuDO.menuImage = executionSummaryOptionLoogs[j];
						menuDO.vecMenuDOs.add(executionMenuDO);
					}
				}
				
//			}
//			else
//			{
//				if(menuDO.menuName.equalsIgnoreCase("Execution Summary"))
//				{
//					for (int j = 0; j < checkinexecutionSummaryOption.length; j++) 
//					{
//						MenuDO executionMenuDO = new MenuDO();
//						executionMenuDO.menuName = checkinexecutionSummaryOption[j];
//						executionMenuDO.menuImage = checkinexecutionSummaryOptionLoogs[j];
//						menuDO.vecMenuDOs.add(executionMenuDO);
//					}
//				}
//			}
			vecMenuDOs.add(menuDO);
		}
		return vecMenuDOs;
	}
	
	
	public static boolean isOldPromotion = false;
	public static String customertype="";
	public static final int DIVISION_ICECREAM = 0;
	public static final int DIVISION_FOOD = 1;
}
