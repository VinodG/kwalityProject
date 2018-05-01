package com.winit.alseer.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.CategoryDO;
import com.winit.alseer.salesman.dataobject.ClassificationDO;
import com.winit.alseer.salesman.dataobject.DiscountDO;
import com.winit.alseer.salesman.dataobject.HHInventryQTDO;
import com.winit.alseer.salesman.dataobject.InventoryDO;
import com.winit.alseer.salesman.dataobject.InventoryDetailDO;
import com.winit.alseer.salesman.dataobject.InventoryGroupDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.dataobject.SellingSKUClassification;
import com.winit.alseer.salesman.dataobject.SlabBasedDiscountDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.UOMConversionDO;
import com.winit.alseer.salesman.dataobject.UOMConversionFactorDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.utilities.Tools;
import com.winit.sfa.salesman.MyApplication;
import com.winit.sfa.salesman.SalesManRecommendedOrder.DataFetchListner;

public class CaptureInventryDA extends BaseDA
{
	public static final int LAST_ORDER = 2;
	public static final int MODIFY = 4;
	public static final int DELIVERY = 3;
	private static final int BRAND_ITEMS = 6;
	public static final int RECOMENDED = 7;
	private boolean isRequestCanceled=false;
	
	//Order Modify
	public HashMap<String, Vector<ProductDO>> getLastOrderByOrderAndPreseller(String orderId, HashMap<String, HHInventryQTDO> hmInventory, int TYPE, String priceClass)
	{
		//
		String strPreviousOrder = "Select distinct P.ProductId, P.ItemCode, P.Description,P.Category, P.Brand,P.UOM,P.SecondaryUOM,P.CaseBarCode,P.UnitBarCode,"+
								  "P.ItemType,P.UnitPerCase,P.PricingKey,TC.CategoryName, OD.Cases,OD.Units from tblProducts P, tblCategory TC, tblOrderDetail OD" +
								  " where P.Category<>'' and P.ItemCode=OD.ItemCode and TC.CategoryId=P.Category and OD.OrderNo = '%s' AND OD.ItemType = 'O' Order by P.DisplayOrder ASC";
		
		String lastOrderQry 	= String.format(strPreviousOrder, orderId);

		SQLiteDatabase mDatabase = null;
		Cursor cursor =null;
		HashMap<String, Vector<ProductDO>> hmProducts =null;
		try
		{
			mDatabase  = DatabaseHelper.openDataBase();
			cursor     = mDatabase.rawQuery(lastOrderQry, null);
//			hmProducts = parseCursor(cursor, TYPE, hmInventory, priceClass, "","");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor!=null)
				cursor.close();
			if(mDatabase!=null)
				mDatabase.close();
		}
		return hmProducts;
	}
//	/**
//	 * 
//	 * @param objJourneyPlan
//	 * @param hmInventory
//	 * @param date
//	 * @param hashUomConveriosn
//	 * @param hmDistinctModifiedItem 
//	 * @return
//	 */
//	public Vector<ClassificationDO> getRecommendeOrder1(JourneyPlanDO objJourneyPlan, HashMap<String, HHInventryQTDO> hmInventory,String date, HashMap<String, TrxDetailsDO> hmDistinctModifiedItem)
//	{
//		synchronized (MyApplication.MyLock) {
//			SQLiteDatabase mDatabase = null;
//			Cursor cursor = null,cursorProducts =null;
//			Vector<ClassificationDO> vectDetails = new Vector<ClassificationDO>();
//			ArrayList<ClassificationDO> arrClassification = new ArrayList<ClassificationDO>();
//			try
//			{
//				String strClassification  = "SELECT SellingSKUClassificationId, Code, Description, Sequence FROM tblSellingSKUClassification ORDER By Sequence ASC";
//				mDatabase  = DatabaseHelper.openDataBase();
//				cursor     = mDatabase.rawQuery(strClassification, null);
//				
//				SellingSKU sellingSKU = new SellingSKUClassificationsDA().getCustomerSellingSKUGroup(objJourneyPlan, mDatabase);
//				
//				if(cursor.moveToFirst())
//				{
//					do
//					{
//						/**  1.Fetching Classifications  */
//						
//						ClassificationDO classificationDO =  new ClassificationDO();
//						
//						classificationDO.SellingSKUClassificationId   =  cursor.getInt(0);
//						classificationDO.Code  					      =  cursor.getString(1);
//						classificationDO.Description   				  =  cursor.getString(2);
//						classificationDO.Sequence	   				  =  cursor.getInt(3);
//						
//						arrClassification.add(classificationDO);
//						//Need to check price with Start Date and End date
//						/**  2. Get Product Details Based on classification */
//						
//						
//					}
//					while(cursor.moveToNext());
//				}
//				
//				
//				ClassificationDO classificationDO =  new ClassificationDO();
//				
//				classificationDO.SellingSKUClassificationId   =   100;
//				classificationDO.Code  					      =  "100";
//				classificationDO.Description   				  =  "ALL";
//				classificationDO.Sequence	   				  =   100;
//				
//				arrClassification.add(classificationDO);
//				
//				
//				if(arrClassification!=null && arrClassification.size()>0)
//				{
//					for(ClassificationDO claDo : arrClassification)
//					{
//						String strQuery ="";
//						if(claDo.Description.equalsIgnoreCase("ALL"))
//							strQuery 	= 	"SELECT PM.ItemCode, PM.Category, PM.Description, PM.UOM, PM.ItemType, PM.Brand, PM.Classifications, PM.DisplayOrder, TP.PRICECASES,TP.DISCOUNT, BN.BrandName,R.Quantity, R.UOM "+ 
//									"FROM tblProducts PM INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode and TP.UOM=PM.UOM "+
//									"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand " +
////									"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode "+
//									"LEFT OUTER JOIN tblRecommendedOrder R ON R.ItemCode = TP.ItemCode AND R.ClientCode = '"+objJourneyPlan.site+"' AND Date like '"+date+"%' "+
//									"WHERE TP.CUSTOMERPRICINGKEY = '"+objJourneyPlan.priceList+"' "+
//									"GROUP BY PM.ItemCode Order by BN.BrandName";
//						else if(sellingSKU!=null)
//							strQuery 	= 	"SELECT PM.ItemCode, PM.Category, PM.Description, PM.UOM, PM.ItemType, PM.Brand, PM.Classifications, PM.DisplayOrder, TP.PRICECASES,TP.DISCOUNT, BN.BrandName,R.Quantity, R.UOM "+ 
//									"FROM tblProducts PM INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode and TP.UOM=PM.UOM "+
////									"INNER JOIN tblCategory TC ON TC.CategoryId = PM.Category " +
//									"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand " +
//									"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode "+
//									"INNER JOIN tblGroupSellingSKUClassification  G ON G.SellingSKUId  = S.SellingSKUId "+
//									"LEFT OUTER JOIN tblRecommendedOrder R ON R.ItemCode = S.ItemCode AND R.ClientCode = '"+objJourneyPlan.site+"' AND Date like '"+date+"%'"+
//									" WHERE TP.CUSTOMERPRICINGKEY = '"+objJourneyPlan.priceList+"' "+
//									"AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' " +
//									"AND G.SellingSKUClassificationId ='"+claDo.SellingSKUClassificationId+"' " +
//									"GROUP BY PM.ItemCode Order by BN.BrandName, G.Priority";
//						
//						if(strQuery!=null && !strQuery.equalsIgnoreCase(""))
//						{
//							cursorProducts     			 = 	mDatabase.rawQuery(strQuery, null);
//							Object[] obj  =  parseRecommendedOrderCursor(cursorProducts,null,objJourneyPlan.priceList, claDo.Code, hmInventory,mDatabase,hmDistinctModifiedItem);
//							claDo.hmProducts = (HashMap<String, Vector<TrxDetailsDO>>) obj[0];
////							claDo.arrBrands =  (Vector<String>) obj[1];
////							sort(claDo.arrBrands);
//							if(claDo.hmProducts != null && claDo.hmProducts.size() > 0)
//								vectDetails.add(claDo);
//						}
//					}
//				}
//				
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//			finally
//			{
//				if(cursor!=null && !cursor.isClosed())
//					cursor.close();
//				if(cursorProducts!=null && !cursorProducts.isClosed())
//					cursorProducts.close();
//				
//				if(mDatabase!=null)
//					mDatabase.close();
//			}
//			return vectDetails;
//		}
//	}
	public void cancelRequest(boolean isCancel){
		this.isRequestCanceled=isCancel;
	}
	public void getRecommendeOrder(JourneyPlanDO objJourneyPlan,HashMap<String, TrxDetailsDO> hmSavedItems, 
														HashMap<String, HHInventryQTDO> hmInventory,
														String date, LinkedHashMap<String, 
														TrxDetailsDO> hmDistinctModifiedItem,HashMap<String, HashMap<String, Float>> hmPricing,
														int TRX_TYPE, DataFetchListner dataFetchListner, int division)
	{
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null,cursorProducts =null;
			ArrayList<ClassificationDO> arrClassification = new ArrayList<ClassificationDO>();
			try
			{
				String strClassification  = "SELECT SellingSKUClassificationId, Code, Description, Sequence FROM tblSellingSKUClassification ORDER By Sequence ASC";
				mDatabase  = DatabaseHelper.openDataBase();
				cursor     = mDatabase.rawQuery(strClassification, null);
				
				SellingSKUClassification sellingSKU = new SellingSKUClassificationsDA().getCustomerSellingSKUGroup(objJourneyPlan, mDatabase);
				
				if((TRX_TYPE != TrxHeaderDO.get_TYPE_FREE_DELIVERY() && TRX_TYPE != TrxHeaderDO.get_TYPE_FREE_ORDER()))
				{
					if(cursor.moveToFirst())
					{
						do
						{
							/**  1.Fetching Classifications  */
							
							ClassificationDO classificationDO =  new ClassificationDO();
							
							classificationDO.SellingSKUClassificationId   =  cursor.getInt(0);
							classificationDO.Code  					      =  cursor.getString(1);
							classificationDO.Description   				  =  cursor.getString(2);
							classificationDO.Sequence	   				  =  cursor.getInt(3);
							
							arrClassification.add(classificationDO);
							//Need to check price with Start Date and End date
							/**  2. Get Product Details Based on classification */
							
							
						}
						while(cursor.moveToNext());
					}
				}
				
				ClassificationDO classificationDO =  new ClassificationDO();
				
				classificationDO.SellingSKUClassificationId   =   100;
				classificationDO.Code  					      =  "100";
				classificationDO.Description   				  =  "ALL";
				classificationDO.Sequence	   				  =   100;
				
				arrClassification.add(classificationDO);
				
				
				if(arrClassification!=null && arrClassification.size()>0)
				{
					for(ClassificationDO claDo : arrClassification)
					{
						String strQuery ="";
						/**If requierment comes change category to subcategory**/
						if(claDo.Description.equalsIgnoreCase("ALL"))
//							strQuery 	= 	"SELECT PM.ItemCode, PM.Category , PM.Description, PM.UOM, PM.ItemType, PM.Brand, PM.Classifications, PM.DisplayOrder, TP.PRICECASES,TP.DISCOUNT, BN.BrandName,R.Quantity, R.UOM,BN.BrandImage,PM.CustomerGroupCodes,PM.Classifications,PM.DisplayOrder, UF.BarCode, PM.VATPer  "+
							strQuery 	= 	"SELECT PM.ItemCode, PM.Category , PM.Description, PM.UOM, PM.ItemType, PM.Brand, PM.Classifications, PM.DisplayOrder, TP.PRICECASES,TP.DISCOUNT, BN.BrandName,R.Quantity," +
                                    " R.UOM,BN.BrandImage,PM.CustomerGroupCodes,PM.Classifications,PM.DisplayOrder, UF.BarCode,''Priority,PM.VAT  "+
									"FROM tblProducts PM INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode AND PM.IsActive='True' and TP.UOM=PM.UOM AND TP.CUSTOMERPRICINGKEY = '"+objJourneyPlan.priceList+"' AND PM.Division = '"+division+"' "+
									"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand " +
									"INNER JOIN tblUOMFactor UF ON PM.ItemCode=UF.ItemCode " +
									"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' "+
									"LEFT OUTER JOIN tblRecommendedOrder R ON R.ItemCode = TP.ItemCode AND R.ClientCode = '"+objJourneyPlan.site+"' AND Date like '"+date+"%' "+
									"GROUP BY PM.ItemCode  "+ 
									"UNION "+
//									"SELECT PM.ItemCode, PM.Category , PM.Description, PM.UOM, PM.ItemType, PM.Brand, PM.Classifications, PM.DisplayOrder, TP.PRICECASES,TP.DISCOUNT, BN.BrandName,R.Quantity, R.UOM,BN.BrandImage,PM.CustomerGroupCodes,PM.Classifications,PM.DisplayOrder, UF.BarCode, PM.VATPer "+
									"SELECT PM.ItemCode, PM.Category , PM.Description, PM.UOM, PM.ItemType, PM.Brand, PM.Classifications, PM.DisplayOrder, TP.PRICECASES,TP.DISCOUNT, BN.BrandName,R.Quantity, " +
                                    "R.UOM,BN.BrandImage,PM.CustomerGroupCodes,PM.Classifications,PM.DisplayOrder, UF.BarCode,''Priority,PM.VAT  "+
									"FROM tblProducts PM INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode AND PM.IsActive='True' and TP.UOM=PM.UOM  AND PM.Division = '"+division+"' "+
									"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand " +
									"INNER JOIN tblUOMFactor UF ON PM.ItemCode=UF.ItemCode " +
									"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' "+
									"LEFT OUTER JOIN tblRecommendedOrder R ON R.ItemCode = TP.ItemCode AND R.ClientCode = '"+objJourneyPlan.site+"' AND Date like '"+date+"%' "+
									"WHERE PM.ItemCode NOT IN (SELECT ItemCode FROM tblPricing R WHERE R.CUSTOMERPRICINGKEY = '"+objJourneyPlan.priceList+"') " +
									"GROUP BY PM.ItemCode Order by PM.DisplayOrder ASC, PM.ItemCode";
						else if(sellingSKU!=null)
//							strQuery 	= 	"SELECT PM.ItemCode, PM.Category , PM.Description, PM.UOM, PM.ItemType, PM.Brand, PM.Classifications, PM.DisplayOrder, TP.PRICECASES,TP.DISCOUNT, BN.BrandName,R.Quantity, R.UOM,BN.BrandImage,PM.CustomerGroupCodes,PM.Classifications,PM.DisplayOrder, UF.BarCode, PM.VATPer, G.Priority  "+
							strQuery 	= 	"SELECT PM.ItemCode, PM.Category , PM.Description, PM.UOM, PM.ItemType, PM.Brand, PM.Classifications, PM.DisplayOrder, TP.PRICECASES,TP.DISCOUNT, BN.BrandName,R.Quantity," +
                                    " R.UOM,BN.BrandImage,PM.CustomerGroupCodes,PM.Classifications,PM.DisplayOrder, UF.BarCode,   G.Priority,PM.VAT  "+
									"FROM tblProducts PM INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode AND PM.IsActive='True' and TP.UOM=PM.UOM AND TP.CUSTOMERPRICINGKEY = '"+objJourneyPlan.priceList+"' AND PM.Division = '"+division+"' "+
									"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand " +
									"INNER JOIN tblUOMFactor UF ON PM.ItemCode=UF.ItemCode " +
									"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' "+
									"INNER JOIN tblGroupSellingSKUClassification  G ON G.SellingSKUId  = S.SellingSKUId AND G.SellingSKUClassificationId ='"+claDo.SellingSKUClassificationId+"' "+
									"LEFT OUTER JOIN tblRecommendedOrder R ON R.ItemCode = S.ItemCode AND R.ClientCode = '"+objJourneyPlan.site+"' AND Date like '"+date+"%' "+
									"GROUP BY PM.ItemCode "+ 
									"UNION "+ 
//									"SELECT PM.ItemCode, PM.Category , PM.Description, PM.UOM, PM.ItemType, PM.Brand, PM.Classifications, PM.DisplayOrder, TP.PRICECASES,TP.DISCOUNT, BN.BrandName,R.Quantity, R.UOM,BN.BrandImage,PM.CustomerGroupCodes,PM.Classifications,PM.DisplayOrder, UF.BarCode, PM.VATPer , G.Priority "+
									"SELECT PM.ItemCode, PM.Category , PM.Description, PM.UOM, PM.ItemType, PM.Brand, PM.Classifications, PM.DisplayOrder, TP.PRICECASES,TP.DISCOUNT, BN.BrandName,R.Quantity, " +
                                    "R.UOM,BN.BrandImage,PM.CustomerGroupCodes,PM.Classifications,PM.DisplayOrder, UF.BarCode,   G.Priority,PM.VAT "+
									"FROM tblProducts PM INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode AND PM.IsActive='True' and TP.UOM=PM.UOM  AND PM.Division = '"+division+"' "+
									"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand " +
									"INNER JOIN tblUOMFactor UF ON PM.ItemCode=UF.ItemCode " +
									"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' "+
									"INNER JOIN tblGroupSellingSKUClassification  G ON G.SellingSKUId  = S.SellingSKUId AND G.SellingSKUClassificationId ='"+claDo.SellingSKUClassificationId+"' "+
									"LEFT OUTER JOIN tblRecommendedOrder R ON R.ItemCode = S.ItemCode AND R.ClientCode = '"+objJourneyPlan.site+"' AND Date like '"+date+"%' "+
									"WHERE PM.ItemCode NOT IN (SELECT ItemCode FROM tblPricing R WHERE R.CUSTOMERPRICINGKEY = '"+objJourneyPlan.priceList+"') " + 
									"GROUP BY PM.ItemCode Order by PM.DisplayOrder ASC, PM.ItemCode";
						
						if(strQuery!=null && !strQuery.equalsIgnoreCase(""))
						{
							cursorProducts    =  mDatabase.rawQuery(strQuery, null);
							Object[] obj 	  =  parseRecommendedOrderCursor(cursorProducts,hmSavedItems, objJourneyPlan.priceList, claDo.Code, hmInventory,mDatabase,hmDistinctModifiedItem,hmPricing,TRX_TYPE,objJourneyPlan.IsVATApplicable);
							claDo.hmProducts  =  (HashMap<String, Vector<TrxDetailsDO>>) obj[0];
//							claDo.arrBrands =  (Vector<String>) obj[1];
							if(claDo.hmProducts != null && claDo.hmProducts.size() > 0 && !isRequestCanceled){
								dataFetchListner.dataFetched(claDo);
							}
						}
						if(isRequestCanceled)
							break;
					}
					if(!isRequestCanceled)
						dataFetchListner.completed();
				}else{
					dataFetchListner.completed();
				}
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
				if(!isRequestCanceled)
					dataFetchListner.completed();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(cursorProducts!=null && !cursorProducts.isClosed())
					cursorProducts.close();
				
				if(mDatabase!=null)
					mDatabase.close();
				if(!isRequestCanceled)
					dataFetchListner.completed();
			}
			
		}
	}
	public void sort(Vector<String> vec){
		Collections.sort(vec, new Comparator<String>() {
		    @Override
		   public int compare(String s1, String s2) {
		    	return s1.compareToIgnoreCase(s2);
		    }
		});
	}
	
	
	public Vector<TrxDetailsDO> getReturnItems(String catId, String orderedItemsList, JourneyPlanDO objJourneyPlan, boolean isStockCheck, String return_Type, int division)
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursorProducts =null;
		Vector<TrxDetailsDO> vectDetails = new Vector<TrxDetailsDO>();
		try
		{
			mDatabase  = DatabaseHelper.openDataBase();
			
			SellingSKUClassification sellingSKU = new SellingSKUClassificationsDA().getCustomerSellingSKUGroup(objJourneyPlan, mDatabase);
			
			String strQuery 	= 	null;
			/**If reverting back then change the Category to SubCategory**/
			if(catId==null){
				strQuery= 	"SELECT PM.ItemCode, PM.Category, PM.Description, PM.UOM, PM.ItemType, PM.Brand, PM.Classifications, PM.DisplayOrder," +
						" TP.PRICECASES,TP.DISCOUNT,BN.BrandName, UF.BarCode,PM.VAT "+
						"FROM tblProducts PM INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode and TP.UOM=PM.UOM AND PM.Division = '"+division+"' "+
						"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand " +
						"INNER JOIN tblUOMFactor UF ON TP.ItemCode=UF.ItemCode " +
						"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode "+
						"WHERE TP.CUSTOMERPRICINGKEY = '"+objJourneyPlan.priceList+"' "+
						" AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' AND PM.IsActive='True' " +
						"AND "+
						"PM.ItemCode NOT IN ("+orderedItemsList+") "+
						"GROUP BY PM.ItemCode "+
						"UNION "+
						"SELECT PM.ItemCode, PM.Category, PM.Description, PM.UOM, PM.ItemType, PM.Brand, PM.Classifications, PM.DisplayOrder," +
						" TP.PRICECASES,TP.DISCOUNT,BN.BrandName, UF.BarCode,PM.VAT "+
						"FROM tblProducts PM INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode and TP.UOM=PM.UOM AND PM.Division = '"+division+"' "+
						"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand " +
						"INNER JOIN tblUOMFactor UF ON TP.ItemCode=UF.ItemCode " +
						"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode "+
						"WHERE S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' AND PM.IsActive='True' " +
						"AND PM.ItemCode NOT IN ("+orderedItemsList+") "+
						"AND PM.ItemCode NOT IN (SELECT ItemCode FROM tblPricing R WHERE R.CUSTOMERPRICINGKEY = '"+objJourneyPlan.priceList+"') "+
						"GROUP BY PM.ItemCode Order by PM.DisplayOrder ASC, PM.ItemCode";
			}else{
				strQuery 	= 	"SELECT PM.ItemCode, PM.Category, PM.Description, PM.UOM, PM.ItemType, PM.Brand, PM.Classifications, PM.DisplayOrder," +
						" TP.PRICECASES,TP.DISCOUNT, BN.BrandName, UF.BarCode,PM.VAT "+
						"FROM tblProducts PM INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode and TP.UOM=PM.UOM AND PM.Division = '"+division+"' "+
						"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand " +
						"INNER JOIN tblUOMFactor UF ON TP.ItemCode=UF.ItemCode " +
						"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode "+
						"WHERE TP.CUSTOMERPRICINGKEY = '"+objJourneyPlan.priceList+"' "+
						"AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' AND PM.IsActive='True' " +
						"AND "+
						"TC.CategoryId ='" +catId+"' AND PM.ItemCode NOT IN ("+orderedItemsList+") "+
						"GROUP BY PM.ItemCode "+
						"UNION "+
						"SELECT PM.ItemCode, PM.Category, PM.Description, PM.UOM, PM.ItemType, PM.Brand, PM.Classifications, PM.DisplayOrder," +
						" TP.PRICECASES,TP.DISCOUNT, BN.BrandName, UF.BarCode,PM.VAT "+
						"FROM tblProducts PM INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode and TP.UOM=PM.UOM AND PM.Division = '"+division+"' "+
						"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand " +
						"INNER JOIN tblUOMFactor UF ON TP.ItemCode=UF.ItemCode " +
						"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode "+
						"WHERE S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' AND PM.IsActive='True' " +
						"AND TC.CategoryId ='" +catId+"' AND PM.ItemCode NOT IN ("+orderedItemsList+") "+
						"AND PM.ItemCode NOT IN (SELECT ItemCode FROM tblPricing R WHERE R.CUSTOMERPRICINGKEY = '"+objJourneyPlan.priceList+"') "+
						"GROUP BY PM.ItemCode Order by PM.DisplayOrder ASC, PM.ItemCode";
			}
					
			cursorProducts  = 	mDatabase.rawQuery(strQuery, null);
			if(cursorProducts.moveToFirst())
				vectDetails 	=   parseReturnItems(cursorProducts, objJourneyPlan.priceList,mDatabase,return_Type,objJourneyPlan.IsVATApplicable);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursorProducts!=null && !cursorProducts.isClosed())
				cursorProducts.close();
			
			if(mDatabase!=null)
				mDatabase.close();
		}
		return vectDetails;
	}
	
	public Vector<TrxDetailsDO> parseReturnItems(Cursor cursor,String strPriceClass, SQLiteDatabase mDatabase, String return_Type,Boolean IsVATApplicable)
	{
			
			Vector<TrxDetailsDO> vecProducts = new Vector<TrxDetailsDO>();
			try
			{
				if(mDatabase==null || !mDatabase.isOpen())
					mDatabase = DatabaseHelper.openDataBase();
				String query= "Select distinct ItemCode,UOM,Factor,EAConversion from tblUOMFactor where ItemCode='%s'";
				
				if(cursor.moveToFirst())
				{
					do
					{
						TrxDetailsDO trxDetailDo 					=	new TrxDetailsDO();
						trxDetailDo.itemCode 						= 	cursor.getString(0);
						trxDetailDo.categoryId 						= 	cursor.getString(1);
						trxDetailDo.itemDescription 				= 	cursor.getString(2);
						trxDetailDo.UOM 							= 	cursor.getString(3);
						if(return_Type.equalsIgnoreCase(AppConstants.Return_Type_Salable))
							trxDetailDo.itemType 						= TrxDetailsDO.get_TRX_ITEM_SELLABLE();
						else if(return_Type.equalsIgnoreCase(AppConstants.Return_Type_Non_Salable))
							trxDetailDo.itemType 						= TrxDetailsDO.get_TRX_ITEM_NON_SELLABLE();
//						trxDetailDo.itemType 						= 	"";//cursor.getString(4);
						trxDetailDo.itemGroupLevel5					= 	cursor.getString(5);
						trxDetailDo.basePrice						=   cursor.getFloat(8);
						trxDetailDo.totalDiscountPercentage 		=   cursor.getFloat(9);
						trxDetailDo.brandName	 					=   cursor.getString(10);
						trxDetailDo.barCode		 					=   cursor.getString(11);
						if(IsVATApplicable)
							trxDetailDo.vatPercentage		 					=   cursor.getFloat(12);
						else
							trxDetailDo.vatPercentage		 					=   0.0f;

						trxDetailDo.vatPercentagebackup=trxDetailDo.vatPercentage;
						trxDetailDo.requestedSalesBU				= 0;
						trxDetailDo.quantityLevel3   				= 0;
						trxDetailDo.missedBU   						= 0;
						
						//==============================Start fetching all available UOM's EA Conversion for this item======================
						Cursor cursorsUoms=mDatabase.rawQuery(String.format(query,trxDetailDo.itemCode), null);
						if(cursorsUoms.moveToFirst()){
							do {
								UOMConversionFactorDO conversionFactorDO = new UOMConversionFactorDO();
								conversionFactorDO.ItemCode		= 	cursorsUoms.getString(0);
								conversionFactorDO.UOM			=	cursorsUoms.getString(1);
								conversionFactorDO.factor		=	cursorsUoms.getFloat(2);
								conversionFactorDO.eaConversion	=	cursorsUoms.getFloat(3);
								
								String key = conversionFactorDO.ItemCode+conversionFactorDO.UOM;
								trxDetailDo.hashArrUoms.put(key,conversionFactorDO);
								trxDetailDo.arrUoms.add(conversionFactorDO.UOM);
							} while (cursorsUoms.moveToNext());
						}
						if(cursorsUoms!=null && !cursorsUoms.isClosed())
							cursorsUoms.close();
						//==============================End fetching all available UOM's for this item======================
						
						vecProducts.add(trxDetailDo);
						
//						sortVectorperDisplayOredr(vecProducts);
					}while(cursor.moveToNext());
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			return vecProducts;
	}
	/**
	 * 
	 * @param customerPricingKey
	 * @return Object[]
	 * Object[0] = HashMap<String, UOMConversionDO>
	 * Object[1] = HashMap<String, HashMap<String, Float>>
	 */
	public Object[] getPriceAndUOMConversion(String customerPricingKey){
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursor=null;
			Object objPricingAndConversion[]=new Object[2];
			HashMap<String, UOMConversionDO> hashUomConveriosn=new HashMap<String, UOMConversionDO>();
			HashMap<String, HashMap<String, Float>> hashMapPricing = new HashMap<String, HashMap<String,Float>>();
			try {
				sqLiteDatabase =DatabaseHelper.openDataBase();
				cursor = sqLiteDatabase.rawQuery("SELECT ItemCode,Conversion12,Conversion23,Conversion13 FROM vw_UOMConversion", null);
				if(cursor.moveToFirst()){
					do {
						UOMConversionDO uomConversionDO=new UOMConversionDO();
						uomConversionDO.conversion12 = cursor.getFloat(1);
						uomConversionDO.conversion13 = cursor.getFloat(2);
						uomConversionDO.conversion13 = cursor.getFloat(3);
						hashUomConveriosn.put(cursor.getString(0), uomConversionDO);
					} while (cursor.moveToNext());
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				cursor = sqLiteDatabase.rawQuery("SELECT ITEMCODE,UOM,PRICECASES FROM tblPricing where CUSTOMERPRICINGKEY='"+customerPricingKey+"' "
						+ "UNION"
						+ " SELECT ITEMCODE,UOM,PRICECASES FROM tblPricing where CUSTOMERPRICINGKEY='' "
						+ " and ITEMCODE not in(SELECT ITEMCODE FROM tblPricing where CUSTOMERPRICINGKEY='"+customerPricingKey+"')", null);
				if(cursor.moveToFirst()){
					do {
						String itemCode=cursor.getString(0);
						if(hashMapPricing.containsKey(itemCode)){
							hashMapPricing.get(itemCode).put(cursor.getString(1), cursor.getFloat(2));
						}else{
							HashMap<String, Float> hashPriceByUOM = new HashMap<String, Float>();
							hashPriceByUOM.put(cursor.getString(1), cursor.getFloat(2));
							hashMapPricing.put(itemCode, hashPriceByUOM);
						}
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
				objPricingAndConversion[0] = hashUomConveriosn;
				objPricingAndConversion[1] = hashMapPricing;
			}
			return objPricingAndConversion;
		}
	}
	
	public HashMap<String, HashMap<String, Float>> getPricing(String customerPricingKey, int tRX_TYPE){
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursor=null;
			HashMap<String, HashMap<String, Float>> hashMapPricing = new HashMap<String, HashMap<String,Float>>();
			try {
				sqLiteDatabase =DatabaseHelper.openDataBase();
				String query = "SELECT ITEMCODE,UOM,PRICECASES FROM tblPricing where CUSTOMERPRICINGKEY='"+customerPricingKey+"' "
						+ "UNION"
						+ " SELECT ITEMCODE,UOM,PRICECASES FROM tblPricing where CUSTOMERPRICINGKEY='101' "
						+ " and ITEMCODE not in(SELECT ITEMCODE FROM tblPricing where CUSTOMERPRICINGKEY='"+customerPricingKey+"')";
				
				cursor = sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst()){
					do {
						String itemCode=cursor.getString(0);
						if(hashMapPricing.containsKey(itemCode)){
//							if(tRX_TYPE != TrxHeaderDO.get_TYPE_FREE_DELIVERY())
								hashMapPricing.get(itemCode).put(cursor.getString(1), cursor.getFloat(2));
//							else
//								hashMapPricing.get(itemCode).put(cursor.getString(1), 0.0f);
						}else{
							HashMap<String, Float> hashPriceByUOM = new HashMap<String, Float>();
							hashPriceByUOM.put(cursor.getString(1), cursor.getFloat(2));
							hashMapPricing.put(itemCode, hashPriceByUOM);
						}
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return hashMapPricing;
		}
	}
	/**
	 * 
	 * @param cursor
	 * @param strPriceClass
	 * @param classificationID
	 * @param hmInventory
	 * @param sqLiteDatabase
	 * @param hashUomConveriosn
	 * @param hmDistinctModifiedItem 
	 * @param tRX_TYPE 
	 * @return Object[]
	 * Object[0] = HashMap<String, Vector<TrxDetailsDO>>
	 * Object[1] = Vector<String> arrBrands
	 */
	
	public Object[] parseRecommendedOrderCursor(Cursor cursor,HashMap<String, TrxDetailsDO> hmSavedItems,String strPriceClass, String classificationID, HashMap<String, HHInventryQTDO> hmInventory,SQLiteDatabase sqLiteDatabase, LinkedHashMap<String, TrxDetailsDO> hmDistinctModifiedItem,HashMap<String, HashMap<String, Float>> hmPricing, int tRX_TYPE,Boolean IsVATApplicable)
	{
			
			HashMap<String, Vector<TrxDetailsDO>> hmProducts = new HashMap<String, Vector<TrxDetailsDO>>();
//			Vector<String> arrBrands = new Vector<String>();
			Object obj[]=new Object[1];
			
			try
			{
				if(sqLiteDatabase==null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();
				String query= "Select distinct ItemCode,UOM,Factor,EAConversion from tblUOMFactor where ItemCode='%s'";
			
				if(cursor.moveToFirst())
				{
					do
					{
						TrxDetailsDO trxDetailDo 					=	new TrxDetailsDO();
						trxDetailDo.itemCode 						= 	cursor.getString(0);
						trxDetailDo.categoryId 						= 	cursor.getString(1);
						trxDetailDo.itemDescription 				= 	cursor.getString(2);
						trxDetailDo.UOM 							= 	cursor.getString(3);
						trxDetailDo.itemType 						= 	cursor.getString(4);
						trxDetailDo.itemGroupLevel5					= 	cursor.getString(5);
						trxDetailDo.brandId							= 	trxDetailDo.itemGroupLevel5;
						trxDetailDo.basePrice						=   cursor.getFloat(8);
						trxDetailDo.totalDiscountPercentage 		=   cursor.getFloat(9);
						trxDetailDo.calculatedDiscountPercentage	=   trxDetailDo.totalDiscountPercentage;
						
						if(trxDetailDo.totalDiscountPercentage>0)
							Log.e("kwality", "error");
						trxDetailDo.brandName	 					=   cursor.getString(10);
						trxDetailDo.categoryName 					=   trxDetailDo.brandName;
						trxDetailDo.quantityRecommended	 			=   cursor.getFloat(11);
						trxDetailDo.recommendedUOM 					=   cursor.getString(12);
						trxDetailDo.brandImage 						=   cursor.getString(13);
						trxDetailDo.packging 						=   cursor.getString(14);
						trxDetailDo.subcategory 					=   cursor.getString(15);
						trxDetailDo.DisplayOrder 					=   cursor.getInt(16);
						trxDetailDo.barCode		 					=   cursor.getString(17);
//						trxDetailDo.VATPer		 					=   cursor.getString(18);
						trxDetailDo.expiryDate						= CalendarUtils.getOrderPostDate();
						trxDetailDo.requestedSalesBU				= trxDetailDo.quantityRecommended;
						if (IsVATApplicable)
						trxDetailDo.vatPercentage                   =   cursor.getFloat(19);
						else
						trxDetailDo.vatPercentage                   =   0.0f;
//
//						trxDetailDo.VATPer = ( (trxDetailDo.VATPer == null) || (TextUtils.isEmpty(trxDetailDo.VATPer))) ? "0": trxDetailDo.VATAmount;
//						trxDetailDo.VATAmount = ""+ (trxDetailDo.basePrice * (Float.parseFloat(trxDetailDo.VATPer )))/100;
						//as Recommended qty is always in Units so items which are Recommended must be shown as EA UOM by default
						if(trxDetailDo.quantityRecommended>0)
							trxDetailDo.UOM = "PCS";
						
						//==============================Start fetching all available UOM's EA Conversion for this item======================
						Cursor cursorsUoms=sqLiteDatabase.rawQuery(String.format(query,trxDetailDo.itemCode), null);
						if(cursorsUoms.moveToFirst()){
							do {
								UOMConversionFactorDO conversionFactorDO = new UOMConversionFactorDO();
								conversionFactorDO.ItemCode		= 	cursorsUoms.getString(0);
								conversionFactorDO.UOM			=	cursorsUoms.getString(1);
								conversionFactorDO.factor		=	cursorsUoms.getFloat(2);
								conversionFactorDO.eaConversion	=	cursorsUoms.getFloat(3);
								
								String key = conversionFactorDO.ItemCode+conversionFactorDO.UOM;
								trxDetailDo.hashArrUoms.put(key,conversionFactorDO);
								trxDetailDo.arrUoms.add(conversionFactorDO.UOM);
								
								
							} while (cursorsUoms.moveToNext());
						}
						if(cursorsUoms!=null && !cursorsUoms.isClosed())
							cursorsUoms.close();
						//==============================End fetching all available UOM's for this item======================

						
						//if any recommended from back end then we  need to calculate other qty based on UOM
						if(trxDetailDo.requestedSalesBU>0){
							int availQty =0;
							if(hmInventory.containsKey(trxDetailDo.itemCode))
								 availQty = hmInventory.get(trxDetailDo.itemCode).totalQt;
							UOMConversionFactorDO uomFactorDO = trxDetailDo.hashArrUoms.get(trxDetailDo.itemCode+trxDetailDo.UOM+"");
							calculateOtherQty(trxDetailDo, uomFactorDO, availQty);
						}
						
						
						Vector<TrxDetailsDO> vecProducts = hmProducts.get(trxDetailDo.packging);
						
						if(vecProducts == null)
						{
							vecProducts = new Vector<TrxDetailsDO>(); 
							vecProducts.add(trxDetailDo);
							hmProducts.put(trxDetailDo.packging, vecProducts);
						}
						else
							vecProducts.add(trxDetailDo);
						
//						if(!arrBrands.contains(trxDetailDo.brandName))
//							arrBrands.add(trxDetailDo.brandName);
						
						if (hmPricing!=null && hmPricing.containsKey(trxDetailDo.itemCode)) {
							if(hmPricing.get(trxDetailDo.itemCode).containsKey(TrxDetailsDO.getItemUomLevel3()))
								trxDetailDo.EAPrice = hmPricing.get(trxDetailDo.itemCode).get(TrxDetailsDO.getItemUomLevel3());
							
							if(hmPricing.get(trxDetailDo.itemCode).containsKey(TrxDetailsDO.getItemUomLevel1()))
								trxDetailDo.CSPrice = hmPricing.get(trxDetailDo.itemCode).get(TrxDetailsDO.getItemUomLevel1());
						}

						
					if(hmSavedItems!=null && hmSavedItems.containsKey(trxDetailDo.itemCode))
					{
						trxDetailDo = hmSavedItems.get(trxDetailDo.itemCode);
//						trxDetailDo.requestedSalesBU = trxDetailDo.requestedSalesBU*((trxDetailDo.hashArrUoms.get(trxDetailDo.itemCode+"UNIT")).eaConversion);
//						trxDetailDo.quantityLevel1 = trxDetailDo.quantityLevel1*((trxDetailDo.hashArrUoms.get(trxDetailDo.itemCode+"UNIT")).eaConversion);
//						trxDetailDo.quantityBU = (int) trxDetailDo.quantityLevel1;
					}
						
					if(trxDetailDo.requestedSalesBU>0)
					{
						if(tRX_TYPE == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
						{
							int availQty =0;
							UOMConversionFactorDO uomFactorDO = trxDetailDo.hashArrUoms.get(trxDetailDo.itemCode+trxDetailDo.UOM+"");
							if(hmInventory.containsKey(trxDetailDo.itemCode))
								 availQty = (int)((hmInventory.get(trxDetailDo.itemCode).totalQt)/uomFactorDO.eaConversion);
							calculatePresalesQty(trxDetailDo, uomFactorDO, availQty);
						}
						hmDistinctModifiedItem.put(trxDetailDo.itemCode, trxDetailDo);
					}
						
//						sortVectorperDisplayOredr(vecProducts);
					if(isRequestCanceled)
						break;
					}while(cursor.moveToNext());
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				obj[0]=hmProducts;
//				obj[1]=arrBrands;
			}

			return obj;
	}
	
	private void calculateOtherQty(TrxDetailsDO trxDetailsDO,UOMConversionFactorDO uomFactorDO,int availInventoryQty){
		int quanity=0;
		if(uomFactorDO!=null)
		{
			quanity =(int) (/*uomFactorDO.eaConversion**/trxDetailsDO.requestedSalesBU);
			trxDetailsDO.missedBU = quanity - availInventoryQty;
			LogUtils.debug("missedBU", ""+trxDetailsDO.missedBU);
			if(uomFactorDO!=null && uomFactorDO.eaConversion!=0)
				trxDetailsDO.quantityLevel1 = (quanity-trxDetailsDO.missedBU)/uomFactorDO.eaConversion;
			else
				trxDetailsDO.quantityLevel1 = (quanity-trxDetailsDO.missedBU);
			trxDetailsDO.quantityLevel2=0;
			trxDetailsDO.quantityLevel3=0;
		}
			
		trxDetailsDO.quantityBU = quanity-trxDetailsDO.missedBU;
	}
	
	private void calculatePresalesQty(TrxDetailsDO trxDetailsDO,UOMConversionFactorDO uomFactorDO,int availInventoryQty){
		int quanity=0;
		if(uomFactorDO!=null)
		{
			quanity =(int) (/*uomFactorDO.eaConversion**/trxDetailsDO.requestedSalesBU);
			trxDetailsDO.missedBU = quanity - availInventoryQty;
			LogUtils.debug("missedBU", ""+trxDetailsDO.missedBU);
//			if(uomFactorDO!=null && uomFactorDO.eaConversion!=0)
//				trxDetailsDO.quantityLevel1 = (quanity-trxDetailsDO.missedBU)/uomFactorDO.eaConversion;
//			else
				trxDetailsDO.quantityLevel1 = (quanity-trxDetailsDO.missedBU);
			trxDetailsDO.quantityLevel2=0;
			trxDetailsDO.quantityLevel3=0;
		}
			
		trxDetailsDO.quantityBU = quanity-trxDetailsDO.missedBU;
	}
	/**
	 * @param vecProducts
	 */
	private void sortVectorperDisplayOredr(Vector<ProductDO> vecProducts) 
	{
		Collections.sort(vecProducts, new Comparator<ProductDO>()
		{
			@Override
			public int compare(ProductDO lhs, ProductDO rhs) 
			{
				return new Integer(lhs.DisplayOrder).compareTo(new Integer(rhs.DisplayOrder));
			}
		});
	}

	public float getCreditLimit(String CustomersiteId)
	{
		String strCreditLimt = "Select CREDIT_LIMIT from tblCustomerSites where CustomersiteId ='"+CustomersiteId+"'";
		
		float creditLimit = 0;
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		try
		{
			mDatabase = DatabaseHelper.openDataBase();
			cursor = mDatabase.rawQuery(strCreditLimt, null);
			
			if(cursor != null && cursor.moveToFirst())
				creditLimit  = StringUtils.getFloat(cursor.getString(0));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor!=null)
				cursor.close();
			if(mDatabase!=null)
				mDatabase.close();
		}
		return creditLimit;
	}
	
	public DiscountDO getDisocunt(String siteNumber, String CATID, String SKU)
	{
		DiscountDO objDiscount = null;
		Cursor cursor = null;
		SQLiteDatabase mDatabase = null;
		try
		{
			mDatabase 			= DatabaseHelper.openDataBase();
		
			String strItemPrice =   "SELECT Level, DiscountType, Discount FROM tblDiscountMaster WHERE SiteNumber = '"+siteNumber+"' AND "+
									"( "+
									"Level = '0' AND CODE = 'ALL' "+
									"OR "+
									"Level = '1' AND CODE = '"+SKU+"' "+
									"OR "+
									"Level = '2' AND CODE = '"+CATID+"' "+
									")";
			
			cursor 				= mDatabase.rawQuery(strItemPrice, null);
			if(cursor.moveToFirst())
			{
				objDiscount = new DiscountDO();
				objDiscount.level = cursor.getString(0);
				objDiscount.discountType = cursor.getInt(1);
				objDiscount.discount = cursor.getFloat(2);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor != null)
				cursor.close();
			if(mDatabase != null)
				mDatabase.close();
		}
		return objDiscount;
	}
	public DiscountDO getCaseVAlueAndTax(String sku, String pricingKey, String UOM)
	{
		DiscountDO objDiscount = null;
		Cursor cursor = null;
		SQLiteDatabase mDatabase = null;
		try
		{
			mDatabase 			= DatabaseHelper.openDataBase();
		    
			String strItemPrice = "select PRICECASES, TaxGroupCode, TaxPercentage from tblPricing where " +
								  "ITEMCODE = '"+sku+"' and CUSTOMERPRICINGKEY = '"+pricingKey+"' AND UOM='"+UOM+"'";
			
			cursor 				= mDatabase.rawQuery(strItemPrice, null);
			if(cursor.moveToFirst())
			{
				objDiscount = new DiscountDO();
				objDiscount.perCaseValue = cursor.getString(0);
				objDiscount.TaxGroupCode = cursor.getString(1);
				objDiscount.TaxPercentage = Tools.str2Float(cursor.getString(2));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor != null)
				cursor.close();
			if(mDatabase != null)
				mDatabase.close();
		}
		return objDiscount;
	}
	
	public String getCustomerPricingKey(String strCustomerSiteId)
	{
		Cursor cursor = null;
		String strPricingKey = "206";
		SQLiteDatabase mDatabase = null;
		
		try 
		{
			mDatabase = DatabaseHelper.openDataBase();
			String query="SELECT CUSTPRICECLASS from tblCustomerGroup where CUSTOMER_SITE_ID='"+strCustomerSiteId+"'";
			cursor = mDatabase.rawQuery(query, null);
			if(cursor != null && cursor.moveToFirst())
				strPricingKey = cursor.getString(0);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor !=  null)
				cursor.close();
			if(mDatabase!=null)
				mDatabase.close();
		}
		
		return strPricingKey; 
	}
	
	public float getPendingAmount(String customerSiteId)
	{ 
		 SQLiteDatabase sqLiteDatabase =  null;
		 Cursor cursor = null;
		 try 
		 {
			 sqLiteDatabase =  DatabaseHelper.openDataBase();
			 cursor = sqLiteDatabase.rawQuery("Select Sum(BalanceAmount) from tblPendingInvoices where CustomersiteId ='"+customerSiteId+"'", null);
			 if(cursor.moveToFirst())
			 {
				return StringUtils.getFloat(cursor.getString(0));
			 }
		 } 
		 catch (Exception e)
		 {
			 e.printStackTrace();
		 }
		 finally
		 {
			 if(cursor!=null)
				 cursor.close();
			 if(sqLiteDatabase!=null)
				 sqLiteDatabase.close();
		 }
		 return 0;
	}
	
	public HashMap<String, Vector<ProductDO>> getLast6MonthOrdersByCustomer(String site, String priceClass) 
	{
		String strPreviousOrder = "Select distinct P.ProductId, P.ItemCode, P.Description,P.Category, P.Brand,P.UOM,P.SecondaryUOM,P.CaseBarCode,P.UnitBarCode,"+
								  "P.ItemType,P.UnitPerCase,P.PricingKey,TC.CategoryName, OD.CasesDelivered,OD.UnitsDelivered from tblProducts P, tblCategory TC, tblInvoiceOrderDetails OD" +
								  " where P.Category<>'' and P.ItemCode=OD.SKU and TC.CategoryId=P.Category and " +
								  "OD.OrderId IN(SELECT OrderId FROM tblInvoiceOrders WHERE CustomerSiteId = "+site+") AND " +
								  "OD.SKU IN(select DISTINCT ITEMCODE from tblVanStock where SellableQuantity > 0) GROUP BY P.ItemCode Order by P.DisplayOrder ASC";

		SQLiteDatabase mDatabase = null;
		Cursor cursor =null;
		HashMap<String, Vector<ProductDO>> hmProducts =null;
		try
		{
			mDatabase  = DatabaseHelper.openDataBase();
			cursor     = mDatabase.rawQuery(strPreviousOrder, null);
//			hmProducts = parseCursor(cursor, LAST_ORDER, null, priceClass,"","");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor!=null)
				cursor.close();
			if(mDatabase!=null)
				mDatabase.close();
		}
		return hmProducts;
	}
	
	public void insertInventory_Brand(InventoryDO inventoryDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				 
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblInventory (InventoryId, SiteNo, uploadStatus, Status, CreatedOn, CreatedBy) VALUES(?,?,?,?,?,?)");
				
				SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblInventoryDetails (InventoryDetailsId, InventoryId, ItemCode, InventoryQuantity, RecomendedQuantity) VALUES(?,?,?,?,?)");
				if(inventoryDO != null)
				{
					stmtInsert.bindString(1, inventoryDO.inventoryId);
					stmtInsert.bindString(2, inventoryDO.site);
					stmtInsert.bindString(3, ""+inventoryDO.uplaodStatus);
					stmtInsert.bindString(4, ""+inventoryDO.uplaodStatus);
					stmtInsert.bindString(5, ""+inventoryDO.date);
					stmtInsert.bindString(6, inventoryDO.createdBy);
					
					int count = 1;
					for(InventoryDetailDO inventoryDetailDO : inventoryDO.vecInventoryDOs)
					{
						stmtInsertOrder.bindString(1, ""+(count++));
						stmtInsertOrder.bindString(2, inventoryDO.inventoryId);
						stmtInsertOrder.bindString(3, inventoryDetailDO.itemCode);
						stmtInsertOrder.bindString(4, ""+inventoryDetailDO.inventoryQty);
						stmtInsertOrder.bindString(5, ""+inventoryDetailDO.recmQty);
						stmtInsertOrder.executeInsert();
					}
					stmtInsert.executeInsert();
					stmtInsert.close();
				}
				stmtInsertOrder.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	public Vector<String> getPricingUOM(String itemcode, String pricing, SQLiteDatabase sqlLite)
	{
		Vector<String> vector = new Vector<String>();
		try
		{
			if(sqlLite == null || !sqlLite.isOpen())
				sqlLite = DatabaseHelper.openDataBase();
			
			String strQuery = "SELECT UOM FROM tblPricing WHERE  ITEMCODE = '"+itemcode+"' AND CUSTOMERPRICINGKEY ='"+pricing+"'";
			Cursor c=sqlLite.rawQuery(strQuery, null); 
			if(c.moveToFirst())
			{
				do
				{
					vector.add(c.getString(0));
				}
				while(c.moveToNext());
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return vector;
	}
	
	public HashMap<String, Vector<ProductDO>> getBrandItems(String mainCat, String brand, String priceclass)
	{
		synchronized(MyApplication.MyLock) 
		{
			if(mainCat.contains("'"))
				mainCat = mainCat.replaceAll("'", "''");
			
			if(brand.contains("'"))
				brand = brand.replaceAll("'", "''");
			
			String strPreviousOrder = "Select distinct * from tblProducts WHERE Category  = '"+mainCat+"' AND BRAND ='"+brand+"' Order by DisplayOrder ASC";
	
			SQLiteDatabase mDatabase =	null;
			Cursor cursor 			 = 	null;
			
			HashMap<String, Vector<ProductDO>> hmProducts = null;
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor = mDatabase.rawQuery(strPreviousOrder, null);
//				hmProducts = parseCursor(cursor, BRAND_ITEMS, null, priceclass,"","");
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
	
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(mDatabase!=null)
					mDatabase.close();
			}
			
			return hmProducts;
		}
	}
	
	public HashMap<String, Vector<ProductDO>> getBrandItems_New(String brand, String priceclass, String site)
	{
		synchronized(MyApplication.MyLock) 
		{
			if(brand.contains("'"))
				brand = brand.replaceAll("'", "''");
			
			String strPreviousOrder = "Select distinct TP.*, TC.CategoryName from tblProducts TP INNER JOIN tblCategory TC ON TP.Category=TC.CategoryId WHERE TP.BRAND ='"+brand+"' Order by TP.DisplayOrder ASC";
	
			SQLiteDatabase mDatabase =	null;
			Cursor cursor 			 = 	null;
			
			HashMap<String, Vector<ProductDO>> hmProducts = null;
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor = mDatabase.rawQuery(strPreviousOrder, null);
//				hmProducts = parseCursor(cursor, BRAND_ITEMS, null, priceclass,"", site);
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
	
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(mDatabase!=null)
					mDatabase.close();
			}
			
			return hmProducts;
		}
	}
	
	
	public HashMap<String, Vector<ProductDO>> getBrandItems_WithPrice(String brand, String priceclass)
	{
		synchronized(MyApplication.MyLock) 
		{
			if(brand.contains("'"))
				brand = brand.replaceAll("'", "''");
			
			String strPreviousOrder = "Select distinct TP.*, TC.CategoryName from tblProducts TP INNER JOIN tblCategory TC ON TP.Category=TC.CategoryId" +
					                  " WHERE TP.BRAND ='"+brand+"' AND tp.ItemCode IN(select ITEMCODE from tblPricing where CUSTOMERPRICINGKEY='"+priceclass+"' and IsExpired='False')" +
					                  " AND TP.ItemCode IN(SELECT ItemCode FROM tblVanStock WHERE SellableQuantity > 0) Order by TP.DisplayOrder ASC";
	
			SQLiteDatabase mDatabase =	null;
			Cursor cursor 			 = 	null;
			
			HashMap<String, Vector<ProductDO>> hmProducts = null;
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor = mDatabase.rawQuery(strPreviousOrder, null);
//				hmProducts = parseCursor(cursor, BRAND_ITEMS, null, priceclass,"","");
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
	
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(mDatabase!=null)
					mDatabase.close();
			}
			
			return hmProducts;
		}
	}
	
	public HashMap<String, Vector<ProductDO>> getBrandItems_Default(String brand, String priceclass, String site)
	{
		synchronized(MyApplication.MyLock) 
		{
			int count = 1;
			
			String strPreviousOrder = "Select distinct TP.*, TC.CategoryName from tblProducts TP INNER JOIN tblCategory TC ON TP.Category=TC.CategoryId INNER JOIN tblVanStock TV ON TV.ItemCode = TP.ItemCode ORDER BY TP.DisplayOrder ASC,TV.ItemCode LIMIT "+count;
	
			if(brand.contains(AppConstants.MUST_HAVE))
			{
				count = 5;
				strPreviousOrder = "Select distinct TP.*, TC.CategoryName, TP.rowId from tblProducts TP INNER JOIN tblCategory TC ON TP.Category=TC.CategoryId INNER JOIN tblVanStock TV ON TV.ItemCode = TP.ItemCode WHERE TP.rowId NOT IN  (Select distinct  TP.rowId from tblProducts TP INNER JOIN tblCategory TC ON TP.Category=TC.CategoryId INNER JOIN tblVanStock TV ON TV.ItemCode = TP.ItemCode  ORDER BY TV.ItemCode  LIMIT 1) ORDER BY TP.DisplayOrder ASC, TV.ItemCode  LIMIT "+count;
			}
			else if(brand.contains(AppConstants.FAVOURRITE))
			{
				count = 10;
				strPreviousOrder = "Select distinct TP.*, TC.CategoryName, TP.rowId from tblProducts TP INNER JOIN tblCategory TC ON TP.Category=TC.CategoryId INNER JOIN tblVanStock TV ON TV.ItemCode = TP.ItemCode WHERE TP.rowId NOT IN  (Select distinct  TP.rowId from tblProducts TP INNER JOIN tblCategory TC ON TP.Category=TC.CategoryId INNER JOIN tblVanStock TV ON TV.ItemCode = TP.ItemCode  ORDER BY TV.ItemCode  LIMIT 6) ORDER BY TP.DisplayOrder ASC, TV.ItemCode  LIMIT "+count;
			}
			
			SQLiteDatabase mDatabase =	null;
			Cursor cursor 			 = 	null;
			
			HashMap<String, Vector<ProductDO>> hmProducts = null;
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor = mDatabase.rawQuery(strPreviousOrder, null);
//				hmProducts = parseCursor(cursor, BRAND_ITEMS, null, priceclass, brand,site);
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
	
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(mDatabase!=null)
					mDatabase.close();
			}
			
			return hmProducts;
		}
	}
	
	public void insertInventory(InventoryDO inventoryDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				 
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblStoreCheck (StoreCheckId,UserCode,JourneyCode,VisitCode,Date,Status,ClientCode, StoreCheckAppId,CustomerVisitAppId,TotalCount,FoodCount,NonFoodCount,SalesmanName,UnitManagerCode,UnitManagerName,ClientName,ChannelCode,Role,Region,RegionCode,NotApplicableItemCount,TotalApplicableCount,TotalStoreCorekuCount,StartTime,EndTime) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblStoreCheckItem (StoreCheckItemId, StoreCheckId,ItemCode,ItemDescription,CategoryCode,CategoryName,BrandCode,BrandName,AgencyCode,AgencyName,Status,StoreCheckAppId,StoreCheckItemAppId,UOM,Quantity) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtInsertGroup = objSqliteDB.compileStatement("INSERT INTO tblStoreCheckGroup (StorecheckGroupId, StoreCheckId, ItemGroupLevel,ItemGroupLevelName, TotalCount, TotalNotAvailableCount,ItemGroupCode) VALUES(?,?,?,?,?,?,?)");
				if(inventoryDO != null)
				{
					inventoryDO.endTime=CalendarUtils.getCurrentDateTime();
					stmtInsert.bindString(1, inventoryDO.inventoryId);
					stmtInsert.bindString(2, ""+inventoryDO.createdBy);
					stmtInsert.bindString(3, ""+inventoryDO.JourneyCode);
					stmtInsert.bindString(4, ""+inventoryDO.VisitCode);
					stmtInsert.bindString(5, inventoryDO.date);
					stmtInsert.bindString(6, inventoryDO.Status);
					stmtInsert.bindString(7, inventoryDO.site);
					
					stmtInsert.bindString(8, inventoryDO.inventoryId);
					stmtInsert.bindString(9, ""+inventoryDO.inventoryId);
					stmtInsert.bindString(10, ""+inventoryDO.TotalCount);
					stmtInsert.bindString(11, "0");
					stmtInsert.bindString(12, "0");
					stmtInsert.bindString(13, ""+inventoryDO.SalesmanName);
					stmtInsert.bindString(14, "");
					
					stmtInsert.bindString(15, "");
					stmtInsert.bindString(16, ""+inventoryDO.ClientName);
					stmtInsert.bindString(17, ""+inventoryDO.ChannelCode);
					stmtInsert.bindString(18, ""+inventoryDO.Role);
					stmtInsert.bindString(19, ""+inventoryDO.Region);
					stmtInsert.bindString(20, ""+inventoryDO.RegionCode);
					stmtInsert.bindString(21, ""+inventoryDO.NotApplicableItemCount);
					
					stmtInsert.bindString(22, ""+(inventoryDO.TotalCount - inventoryDO.NotApplicableItemCount));
					stmtInsert.bindString(23, "0");
					stmtInsert.bindString(24, ""+inventoryDO.starTime);
					stmtInsert.bindString(25, ""+inventoryDO.endTime);
					
					int count = 1;
					for(InventoryDetailDO inventoryDetailDO : inventoryDO.vecInventoryDOs)
					{
						stmtInsertOrder.bindString(1, ""+(count++));
						stmtInsertOrder.bindString(2, inventoryDO.inventoryId);
						stmtInsertOrder.bindString(3, ""+inventoryDetailDO.itemCode);
						stmtInsertOrder.bindString(4, ""+inventoryDetailDO.ItemDescription);
						stmtInsertOrder.bindString(5, ""+inventoryDetailDO.CategoryCode);
						stmtInsertOrder.bindString(6, ""+inventoryDetailDO.CategoryName);
						stmtInsertOrder.bindString(7, ""+inventoryDetailDO.BrandCode);
						stmtInsertOrder.bindString(8, ""+inventoryDetailDO.BrandName);
						stmtInsertOrder.bindString(9, ""+inventoryDetailDO.AgencyCode);
						stmtInsertOrder.bindString(10, ""+inventoryDetailDO.AgencyName);
						stmtInsertOrder.bindString(11, ""+inventoryDetailDO.status);
						stmtInsertOrder.bindString(12, ""+inventoryDO.inventoryId);
						stmtInsertOrder.bindString(13, ""+inventoryDO.inventoryId);
						stmtInsertOrder.bindString(14, ""+inventoryDetailDO.UOM);
						stmtInsertOrder.bindString(15, ""+inventoryDetailDO.QTY);
						stmtInsertOrder.executeInsert();
					}
					
					int countGroup = 1;
					for (InventoryGroupDO objinveInventoryGroupDO : inventoryDO.vecGroupDOs) 
					{
						stmtInsertGroup.bindString(1, "" + (countGroup++));
						stmtInsertGroup.bindString(2, ""+inventoryDO.inventoryId);
						stmtInsertGroup.bindString(3, ""+objinveInventoryGroupDO.ItemGroupLevel);
						stmtInsertGroup.bindString(4, ""+objinveInventoryGroupDO.ItemGroupLevelName);
						stmtInsertGroup.bindLong(5, objinveInventoryGroupDO.TotalCount);
						stmtInsertGroup.bindLong(6, objinveInventoryGroupDO.TotalNotAvailableCount);
						stmtInsertGroup.bindString(7, ""+objinveInventoryGroupDO.ItemGroupCode);
						stmtInsertGroup.executeInsert();
					}
					
					stmtInsert.executeInsert();
					stmtInsert.close();
					stmtInsertOrder.close();
					stmtInsertGroup.close();
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(objSqliteDB != null&& objSqliteDB.isOpen())
					objSqliteDB.close();
			}
		}
	}
	
	/**
	 * @param objProduct
	 * @param site
	 * @param categoryId 
	 */
	public HashMap<String, ProductDO> getStoreCheckedItems(String site, String categoryId) 
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		HashMap<String, ProductDO> hashItemMissingInInventory=new HashMap<String, ProductDO>();
		ProductDO productDO;
		synchronized(MyApplication.MyLock) 
		{
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				String currentDate = CalendarUtils.getCurrentDateAsStringforStoreCheck();
				String query = "";
				
				if(categoryId != null)
					query = "SELECT TCSI.ItemCode, TCSI.ItemDescription, TCSI.BrandCode, TCSI.BrandName, TCSI.Status, TCSI.UOM, TCSI.Quantity FROM tblStoreCheckItem  TCSI Inner join tblStoreCheck TSC on TCSI.StoreCheckId =TSC.StoreCheckId where TSC.ClientCode ='"+site+"' and TSC.Date like '%"+currentDate+"%' and TCSI.CategoryCode = '"+categoryId+"'";
				else
					query = "SELECT TCSI.ItemCode, TCSI.ItemDescription, TCSI.BrandCode, TCSI.BrandName, TCSI.Status, TCSI.UOM, TCSI.Quantity FROM tblStoreCheckItem  TCSI Inner join tblStoreCheck TSC on TCSI.StoreCheckId =TSC.StoreCheckId where TSC.ClientCode ='"+site+"' and TSC.Date like '%"+currentDate+"%'";//and TCSI.Status ='1'
				
				cursor  = mDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						productDO=new ProductDO();
						productDO.SKU = cursor.getString(0);
						productDO.Description = cursor.getString(1);
						productDO.brandcode = cursor.getString(2);
						productDO.brand = cursor.getString(3);
						productDO.status = cursor.getInt(4);
						productDO.StoreUOM = cursor.getString(5);
						productDO.PcsQTY = cursor.getString(6);
						hashItemMissingInInventory.put(productDO.SKU,productDO);
					}while(cursor.moveToNext());
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
				if(mDatabase != null)
					mDatabase.close();
			}
			return hashItemMissingInInventory;
		}
	}
	public Vector<CategoryDO> getAllCategories() {
		synchronized (MyApplication.MyLock) {
			Vector<CategoryDO> vecCategoryDOs=new Vector<CategoryDO>();
			
			CategoryDO categoryDO=new CategoryDO();
			categoryDO.categoryId = "ALL";
			categoryDO.categoryName = "All Categories";
			categoryDO.parentCode = "ALL";
			vecCategoryDOs.add(categoryDO);
			
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursor=null;
			String query = "select CategoryId,CategoryName,ParentCode from tblCategory order by CategoryName";
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor=sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst()){
					do {
						categoryDO = new CategoryDO();
						categoryDO.categoryId=cursor.getString(0);
						categoryDO.categoryName=cursor.getString(1);
						categoryDO.parentCode=cursor.getString(2);
						vecCategoryDOs.add(categoryDO);
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return vecCategoryDOs;
		}
	}
	
	public Vector<CategoryDO> getAllCategories(JourneyPlanDO mallsDetails,int type,int division) 
	{
		synchronized (MyApplication.MyLock) 
		{
			Vector<CategoryDO> vecCategoryDOs=new Vector<CategoryDO>();
			
			CategoryDO categoryDO=new CategoryDO();
			categoryDO.categoryId = "ALL";
			categoryDO.categoryName = "All Categories";
			categoryDO.parentCode = "ALL";
			vecCategoryDOs.add(categoryDO);
			
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursor=null;
//			String query = "select CategoryId,CategoryName,ParentCode from tblCategory order by CategoryName";
			
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				
				SellingSKUClassification sellingSKU = new SellingSKUClassificationsDA().getCustomerSellingSKUGroup(mallsDetails, sqLiteDatabase);
				String query = "";
				if(type == 1)//CaptureInventory
				{
					query = "select CategoryId,CategoryName,ParentCode from tblCategory " +
							"where ParentCode in " +
								"(SELECT TP.CustomerGroupCodes " +
								"from tblProducts TP " +
								"INNER JOIN tblSellingSKU S ON S.ItemCode = TP.ItemCode AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' " +
								"AND S.IsCoreSKU = 'True' AND TP.IsActive='True' " +
								"INNER JOIN tblBrand TB ON TB.BrandId = TP.BRAND GROUP BY TP.ItemCode Order by TP.DisplayOrder ASC,TB.BrandName, S.IsCoreSKUPriority) " +
							"order by CategoryName";
				}
				else if(type == 2)//Sales Order
				{
					/*query = "select CategoryId,CategoryName,ParentCode " +
							"from tblCategory " +
							"where ParentCode in " +
								"(SELECT PM.CustomerGroupCodes " +
								"FROM tblProducts PM " +
								"INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode AND PM.IsActive='True' and TP.UOM=PM.UOM AND TP.CUSTOMERPRICINGKEY = '101' " +
								"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand " +
								"INNER JOIN tblUOMFactor UF ON PM.ItemCode=UF.ItemCode " +
								"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' " +
								"LEFT OUTER JOIN tblRecommendedOrder R ON R.ItemCode = TP.ItemCode AND R.ClientCode = '"+mallsDetails.site+"' AND Date like '"+CalendarUtils.getOrderPostDate()+"%' " +
								"WHERE PM.Division ='"+division+"' " +
								"GROUP BY PM.ItemCode  " +
								"UNION SELECT PM.CustomerGroupCodes " +
								"FROM tblProducts PM " +
								"INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode AND PM.IsActive='True' and TP.UOM=PM.UOM  " +
								"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand INNER JOIN tblUOMFactor UF ON PM.ItemCode=UF.ItemCode " +
								"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' " +
								"LEFT OUTER JOIN tblRecommendedOrder R ON R.ItemCode = TP.ItemCode AND R.ClientCode = '"+mallsDetails.site+"' AND Date like '"+CalendarUtils.getOrderPostDate()+"%' " +
								"WHERE PM.ItemCode NOT IN (SELECT ItemCode FROM tblPricing R WHERE R.CUSTOMERPRICINGKEY = '"+mallsDetails.priceList+"') AND PM.Division ='"+division+"' " +
								"GROUP BY PM.ItemCode ) " +
							"order by CategoryName";*/
					
//					if(division==0)
					query = "select CategoryId,CategoryName,ParentCode " +
							"from tblCategory " +
							"where ParentCode in " +
								"(SELECT PM.Brand " +
								"FROM tblProducts PM " +
								"INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode AND PM.IsActive='True' and TP.UOM=PM.UOM AND TP.CUSTOMERPRICINGKEY = '101' " +
								"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand " +
								"INNER JOIN tblUOMFactor UF ON PM.ItemCode=UF.ItemCode " +
								"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' " +
								"LEFT OUTER JOIN tblRecommendedOrder R ON R.ItemCode = TP.ItemCode AND R.ClientCode = '"+mallsDetails.site+"' AND Date like '"+CalendarUtils.getOrderPostDate()+"%' " +
								"WHERE PM.Division ='"+division+"' " +
								"GROUP BY PM.ItemCode  " +
								"UNION SELECT PM.Brand " +
								"FROM tblProducts PM " +
								"INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode AND PM.IsActive='True' and TP.UOM=PM.UOM  " +
								"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand INNER JOIN tblUOMFactor UF ON PM.ItemCode=UF.ItemCode " +
								"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' " +
								"LEFT OUTER JOIN tblRecommendedOrder R ON R.ItemCode = TP.ItemCode AND R.ClientCode = '"+mallsDetails.site+"' AND Date like '"+CalendarUtils.getOrderPostDate()+"%' " +
								"WHERE PM.ItemCode NOT IN (SELECT ItemCode FROM tblPricing R WHERE R.CUSTOMERPRICINGKEY = '"+mallsDetails.priceList+"') AND PM.Division ='"+division+"' " +
								"GROUP BY PM.ItemCode ) " +
							"order by CategoryName";
					
//					else
//						if(division==1)
//						{
//							
//							query =	"SELECT DISTINCT SUBCATEGORYID,SUBCATEGORYNAME,TSC.PARENTCODE FROM tblSubCategory TSC INNER JOIN tblCategory TC WHERE TC.PARENTCODE IN " +
//										"(SELECT PM.Brand " +
//										"FROM tblProducts PM " +
//										"INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode AND PM.IsActive='True' and TP.UOM=PM.UOM AND TP.CUSTOMERPRICINGKEY = '101' " +
//										"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand " +
//										"INNER JOIN tblUOMFactor UF ON PM.ItemCode=UF.ItemCode " +
//										"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' " +
//										"LEFT OUTER JOIN tblRecommendedOrder R ON R.ItemCode = TP.ItemCode AND R.ClientCode = '"+mallsDetails.site+"' AND Date like '"+CalendarUtils.getOrderPostDate()+"%' " +
//										"WHERE PM.Division ='"+division+"' " +
//										"GROUP BY PM.ItemCode  " +
//										"UNION SELECT PM.Brand " +
//										"FROM tblProducts PM " +
//										"INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode AND PM.IsActive='True' and TP.UOM=PM.UOM  " +
//										"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand INNER JOIN tblUOMFactor UF ON PM.ItemCode=UF.ItemCode " +
//										"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' " +
//										"LEFT OUTER JOIN tblRecommendedOrder R ON R.ItemCode = TP.ItemCode AND R.ClientCode = '"+mallsDetails.site+"' AND Date like '"+CalendarUtils.getOrderPostDate()+"%' " +
//										"WHERE PM.ItemCode NOT IN (SELECT ItemCode FROM tblPricing R WHERE R.CUSTOMERPRICINGKEY = '"+mallsDetails.priceList+"') AND PM.Division ='"+division+"' " +
//										"GROUP BY PM.ItemCode ) " +
//									"order by SUBCATEGORYNAME";
//							
//						}
				}
				else if(type == 3)//Return Order
				{
					
					query = "select CategoryId,CategoryName,ParentCode " +
							"from tblCategory " +
							"where ParentCode in " +
								"(SELECT PM.Brand " +
								"FROM tblProducts PM " +
								"INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode and TP.UOM=PM.UOM " +
								"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand " +
								"INNER JOIN tblUOMFactor UF ON TP.ItemCode=UF.ItemCode " +
								"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode " +
								"WHERE TP.CUSTOMERPRICINGKEY = '"+mallsDetails.priceList+"' AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' AND PM.IsActive='True' AND PM.Division ='"+division+"' " +
								"GROUP BY PM.ItemCode " +
								"UNION SELECT PM.Brand " +
								"FROM tblProducts PM " +
								"INNER JOIN tblPricing TP ON TP.ITEMCODE = PM.ItemCode and TP.UOM=PM.UOM " +
								"INNER JOIN tblBrand BN ON BN.BrandId = PM.Brand " +
								"INNER JOIN tblUOMFactor UF ON TP.ItemCode=UF.ItemCode " +
								"INNER JOIN tblSellingSKU S ON S.ItemCode = PM.ItemCode " +
								"WHERE S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' AND PM.IsActive='True' AND PM.ItemCode NOT IN " +
								"(SELECT ItemCode FROM tblPricing R WHERE R.CUSTOMERPRICINGKEY = '"+mallsDetails.priceList+"') AND PM.Division ='"+division+"' " +
								"GROUP BY PM.ItemCode ) " +
							"order by CategoryName";
					
					
				}
				
				cursor=sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst()){
					do {
						categoryDO = new CategoryDO();
						categoryDO.categoryId=cursor.getString(0);
						categoryDO.categoryName=cursor.getString(1);
						categoryDO.parentCode=cursor.getString(2);
						vecCategoryDOs.add(categoryDO);
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return vecCategoryDOs;
		}
	}
	
	/**
	 * Gets All SubCategories
	 * @return
	 */
	public Vector<CategoryDO> getAllSubCategories() {
		synchronized (MyApplication.MyLock) {
			Vector<CategoryDO> vecCategoryDOs=new Vector<CategoryDO>();
			
			CategoryDO categoryDO=new CategoryDO();
			categoryDO.categoryId = "ALL";
			categoryDO.categoryName = "All Categories";
			categoryDO.parentCode = "ALL";
			vecCategoryDOs.add(categoryDO);
			
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursor=null;
			String query = "SELECT SubCategoryId,SubCategoryName,ParentCode FROM tblSubCategory ORDER BY SubCategoryName ASC";
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor=sqLiteDatabase.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst()){
					do {
						categoryDO = new CategoryDO();
						categoryDO.categoryId=cursor.getString(0);
						categoryDO.categoryName=cursor.getString(1);
						categoryDO.parentCode=cursor.getString(2);
						vecCategoryDOs.add(categoryDO);
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return vecCategoryDOs;
		}
	}
	public HashMap<String, Vector<SlabBasedDiscountDO>> getSlabBasedDisCount(String orgCode,String channelType) {
		synchronized (MyApplication.MyLock) {
			HashMap<String, Vector<SlabBasedDiscountDO>> hashSlabBasedDis=new HashMap<String, Vector<SlabBasedDiscountDO>>();
			
			String slabBasedDisc="{\"Disccount\":[{\"ItemCode\":\"21032551\",\"MinQty\":\"0\",\"MaxQty\":\"3\",\"Channel\":\"HTS\",\"ORGCode\":\"1010\",\"Disc\":\"17\"},"
					+ "{\"ItemCode\":\"21032551\",\"MinQty\":\"3\",\"MaxQty\":\"5\",\"Channel\":\"HTS\",\"ORGCode\":\"1010\",\"Disc\":\"18\"},"
					+ "{\"ItemCode\":\"21032551\",\"MinQty\":\"5\",\"MaxQty\":\"9999\",\"Channel\":\"HTS\",\"ORGCode\":\"1010\",\"Disc\":\"19\"},"
					+ "{\"ItemCode\":\"21111279\",\"MinQty\":\"0\",\"MaxQty\":\"5\",\"Channel\":\"HTS\",\"ORGCode\":\"1010\",\"Disc\":\"10\"},"
					+ "{\"ItemCode\":\"21111279\",\"MinQty\":\"5\",\"MaxQty\":\"9999\",\"Channel\":\"HTS\",\"ORGCode\":\"1010\",\"Disc\":\"15\"},"
					+ "{\"ItemCode\":\"21032521\",\"MinQty\":\"0\",\"MaxQty\":\"5\",\"Channel\":\"HTS\",\"ORGCode\":\"1010\",\"Disc\":\"10\"},"
					+ "{\"ItemCode\":\"21032521\",\"MinQty\":\"5\",\"MaxQty\":\"9999\",\"Channel\":\"HTS\",\"ORGCode\":\"1010\",\"Disc\":\"15\"},"
					+ "{\"ItemCode\":\"20006574\",\"MinQty\":\"0\",\"MaxQty\":\"9999\",\"Channel\":\"HTS\",\"ORGCode\":\"1010\",\"Disc\":\"10\"},"
					+ "{\"ItemCode\":\"20032574\",\"MinQty\":\"0\",\"MaxQty\":\"9999\",\"Channel\":\"HTS\",\"ORGCode\":\"1010\",\"Disc\":\"10\"},"
					+ "{\"ItemCode\":\"21078285\",\"MinQty\":\"0\",\"MaxQty\":\"9999\",\"Channel\":\"HTS\",\"ORGCode\":\"1010\",\"Disc\":\"10\"},"
					+ "{\"ItemCode\":\"21078325\",\"MinQty\":\"0\",\"MaxQty\":\"9999\",\"Channel\":\"HTS\",\"ORGCode\":\"1010\",\"Disc\":\"10\"},"
					+ "{\"ItemCode\":\"21108302\",\"MinQty\":\"0\",\"MaxQty\":\"9999\",\"Channel\":\"HTS\",\"ORGCode\":\"1010\",\"Disc\":\"10\"},"
					+ "{\"ItemCode\":\"20225423\",\"MinQty\":\"0\",\"MaxQty\":\"9999\",\"Channel\":\"HTS\",\"ORGCode\":\"1010\",\"Disc\":\"14\"},"
					+ "{\"ItemCode\":\"20225419\",\"MinQty\":\"0\",\"MaxQty\":\"3\",\"Channel\":\"HTS\",\"ORGCode\":\"1010\",\"Disc\":\"14\"},"
					+ "{\"ItemCode\":\"20225422\",\"MinQty\":\"0\",\"MaxQty\":\"3\",\"Channel\":\"HTS\",\"ORGCode\":\"1010\",\"Disc\":\"14\"},"
					+ "{\"ItemCode\":\"20230577\",\"MinQty\":\"0\",\"MaxQty\":\"3\",\"Channel\":\"HTS\",\"ORGCode\":\"1010\",\"Disc\":\"14\"}]}";
			
			try {
				JSONObject jsonSlabBasedDiscount=new JSONObject(slabBasedDisc);
				JSONArray jsonArray = jsonSlabBasedDiscount.getJSONArray("Disccount");
				for(int i=0;i<jsonArray.length();i++){
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String itemCode=jsonObject.getString("ItemCode");
					String salesOrgCode=jsonObject.getString("ORGCode");
					String channel=jsonObject.getString("Channel");
					if(salesOrgCode.equalsIgnoreCase(orgCode) && channel.equalsIgnoreCase(channelType)){
						Vector<SlabBasedDiscountDO> vecData = hashSlabBasedDis.get(itemCode);
						if(vecData==null){
							vecData=new Vector<SlabBasedDiscountDO>();
							SlabBasedDiscountDO slabBasedDiscountDO=new SlabBasedDiscountDO();
							slabBasedDiscountDO.miniMumQty = jsonObject.getInt("MinQty");
							slabBasedDiscountDO.maxQty = jsonObject.getInt("MaxQty");
							slabBasedDiscountDO.discPercent = jsonObject.getInt("Disc");
							vecData.add(slabBasedDiscountDO);
							hashSlabBasedDis.put(itemCode, vecData);
						}else{
							SlabBasedDiscountDO slabBasedDiscountDO=new SlabBasedDiscountDO();
							slabBasedDiscountDO.miniMumQty = jsonObject.getInt("MinQty");
							slabBasedDiscountDO.maxQty = jsonObject.getInt("MaxQty");
							slabBasedDiscountDO.discPercent = jsonObject.getInt("Disc");
							vecData.add(slabBasedDiscountDO);
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return hashSlabBasedDis;
		}
	}
	
	public String getPromotionDiscountForcustomer(String userCode, int division)
	{
		synchronized (MyApplication.MyLock)
		{
			String promotionDiscount = "";
			SQLiteDatabase sqlitDatabase = null;
			Cursor cursor = null;
			try
			{
				sqlitDatabase = DatabaseHelper.openDataBase();
				String query = "";
//				String query = "SELECT PBD.PromotionId, PA.Code, " +
//						"CASE WHEN SC.Discount > 0 THEN SC.Discount " +
//						"ELSE PBD.Discount END AS Discount " +
//						"FROM  tblPromotionAssignment PA " +
//						"LEFT JOIN tblPromotionSC SC ON SC.PromotionId =PA.PromotionId " +
//						"LEFT JOIN tblPromotionBrandDiscount PBD ON  PBD.PromotionId = PA.PromotionId " +
//						"WHERE  PA.Code = '"+userCode+"'";
				
				
				if(AppConstants.isOldPromotion)
				{
					query = "SELECT " +
							"CASE WHEN SC.Discount > 0 THEN SC.Discount ELSE PBD.Discount END AS Discount " +
							"FROM tblCustomer C LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson " +
							"LEFT OUTER JOIN tblPromotionAssignment PA ON PA.Code=C.Site " +
							"LEFT OUTER JOIN tblPromotionSC SC ON SC.PromotionId =PA.PromotionId " +
							"LEFT OUTER JOIN tblPromotionBrandDiscount PBD ON  PBD.PromotionId = PA.PromotionId " +
							"WHERE C.Site = '"+userCode+"'";
				}
				else {

					if(division==2)
						query = "SELECT TInvDiscYH FROM tblCustomer WHERE Site = '" + userCode + "'";
					else if(division==1)
						query = "SELECT FInvDiscYH FROM tblCustomer WHERE Site = '" + userCode + "'";
					else
						query = "SELECT Attribute11 FROM tblCustomer WHERE Site = '" + userCode + "'";
				}
				
				cursor = sqlitDatabase.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
//					promotionDiscount = cursor.getString(2);
					promotionDiscount = cursor.getString(0);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqlitDatabase != null && sqlitDatabase.isOpen())
					sqlitDatabase.close();
			}
			return promotionDiscount;
		}
	}
	
	
	public String getStatementDiscountForcustomer(String userCode, int division)
	{
		synchronized (MyApplication.MyLock)
		{
			String promotionDiscount = "";
			SQLiteDatabase sqlitDatabase = null;
			Cursor cursor = null;
			try
			{
				sqlitDatabase = DatabaseHelper.openDataBase();
				String query = "";
//				String query = "SELECT PBD.PromotionId, PA.Code, " +
//						"CASE WHEN SC.Discount > 0 THEN SC.Discount " +
//						"ELSE PBD.Discount END AS Discount " +
//						"FROM  tblPromotionAssignment PA " +
//						"LEFT JOIN tblPromotionSC SC ON SC.PromotionId =PA.PromotionId " +
//						"LEFT JOIN tblPromotionBrandDiscount PBD ON  PBD.PromotionId = PA.PromotionId " +
//						"WHERE  PA.Code = '"+userCode+"'";
				
				
				if(AppConstants.isOldPromotion)
				{
					query = "SELECT " +
							"CASE WHEN SC.Discount > 0 THEN SC.Discount ELSE PBD.Discount END AS Discount " +
							"FROM tblCustomer C LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson " +
							"LEFT OUTER JOIN tblPromotionAssignment PA ON PA.Code=C.Site " +
							"LEFT OUTER JOIN tblPromotionSC SC ON SC.PromotionId =PA.PromotionId " +
							"LEFT OUTER JOIN tblPromotionBrandDiscount PBD ON  PBD.PromotionId = PA.PromotionId " +
							"WHERE C.Site = '"+userCode+"'";
				}
				else {
					if(division==2)
					query = "SELECT TStatDiscYH FROM tblCustomer WHERE Site = '" + userCode + "'";
					else if(division==1)
						query = "SELECT FStatDiscYH FROM tblCustomer WHERE Site = '" + userCode + "'";
					else
						query = "SELECT Attribute13 FROM tblCustomer WHERE Site = '" + userCode + "'";


				}
				cursor = sqlitDatabase.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
//					promotionDiscount = cursor.getString(2);
					promotionDiscount = cursor.getString(0);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqlitDatabase != null && sqlitDatabase.isOpen())
					sqlitDatabase.close();
			}
			return promotionDiscount;
		}
	}
	
	public ArrayList<String> getRateDiffDiscount()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqliteDb = null;
			Cursor cursor = null;
			ArrayList<String> arrRateDiff = new ArrayList<String>();
			try
			{
				sqliteDb = DatabaseHelper.openDataBase();
//				String query = "SELECT FirstDiscount,SecondDiscount FROM tblUsers WHERE UserCode = '"+userCode+"'";
				String query = "SELECT SettingValue FROM tblSettings WHERE SettingName ='FirstDiscount' OR SettingName ='SecondDiscount'";
				cursor = sqliteDb.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					if(cursor.getString(0) != null && !cursor.getString(0).equalsIgnoreCase(""))
						arrRateDiff.add(cursor.getString(0));
//					if(cursor.getString(1) != null && !cursor.getString(1).equalsIgnoreCase(""))
//						arrRateDiff.add(cursor.getString(1));
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqliteDb != null && sqliteDb.isOpen())
					sqliteDb.close();
			}
			return arrRateDiff;
		}
	}
	
}
