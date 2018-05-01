package com.winit.alseer.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.sfa.salesman.MyApplication;

public class OrderTraceDA 
{
	public  HashMap<Integer,Vector<TrxHeaderDO>> getOrderTraceDetails()
	{
		synchronized(MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB 	=	null;
			Cursor cursor = null;
			
			HashMap<Integer,Vector<TrxHeaderDO>> hmDetails = new HashMap<Integer, Vector<TrxHeaderDO>>();
			
			String strSavedQuery = "SELECT TX.TrxCode,TX.AppTrxId,TX.OrgCode,TX.JourneyCode,TX.VisitCode,TX.UserCode,TX.ClientCode,TX.ClientBranchCode, " +
				       "TX.TrxDate,TX.TrxType,TX.CurrencyCode,TX.PaymentType,TX.TotalAmount,TX.TotalDiscountAmount,TX.TotalTAXAmount ,TX.TrxReasonCode, " +
				       "TX.ReferenceCode ,TX.ClientSignature ,TX.SalesmanSignature ,TX.Status ,TX.FreeNote ,TX.CreatedOn,TX.PreTrxCode ,TX.TRXStatus , " +
				       "TX.BranchPlantCode ,TX.PrintingTimes ,TX.ApproveByCode ,TX.ApprovedDate ,TX.LPOCode ,TX.DeliveryDate , TX.UserCreditAccountCode,TC.SiteName,TC.Address1 " +
				       "FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site Where TrxStatus=0 order by date(TrxDate) DESC";

			
			String strSetupQuery = "SELECT TX.TrxCode,TX.AppTrxId,TX.OrgCode,TX.JourneyCode,TX.VisitCode,TX.UserCode,TX.ClientCode,TX.ClientBranchCode, " +
		       "TX.TrxDate,TX.TrxType,TX.CurrencyCode,TX.PaymentType,TX.TotalAmount,TX.TotalDiscountAmount,TX.TotalTAXAmount ,TX.TrxReasonCode, " +
		       "TX.ReferenceCode ,TX.ClientSignature ,TX.SalesmanSignature ,TX.Status ,TX.FreeNote ,TX.CreatedOn,TX.PreTrxCode ,TX.TRXStatus , " +
		       "TX.BranchPlantCode ,TX.PrintingTimes ,TX.ApproveByCode ,TX.ApprovedDate ,TX.LPOCode ,TX.DeliveryDate , TX.UserCreditAccountCode,TC.SiteName,TC.Address1 " +
		       "FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site Where TrxStatus not in (3,12,13,100) AND TrxStatus > 0 order by date(TrxDate) DESC";
			
			String strCancelledQuery = "SELECT TX.TrxCode,TX.AppTrxId,TX.OrgCode,TX.JourneyCode,TX.VisitCode,TX.UserCode,TX.ClientCode,TX.ClientBranchCode, " +
				       "TX.TrxDate,TX.TrxType,TX.CurrencyCode,TX.PaymentType,TX.TotalAmount,TX.TotalDiscountAmount,TX.TotalTAXAmount ,TX.TrxReasonCode, " +
				       "TX.ReferenceCode ,TX.ClientSignature ,TX.SalesmanSignature ,TX.Status ,TX.FreeNote ,TX.CreatedOn,TX.PreTrxCode ,TX.TRXStatus , " +
				       "TX.BranchPlantCode ,TX.PrintingTimes ,TX.ApproveByCode ,TX.ApprovedDate ,TX.LPOCode ,TX.DeliveryDate , TX.UserCreditAccountCode,TC.SiteName,TC.Address1 " +
					"FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site Where TX.TrxStatus =3 OR TX.TrxStatus < 0 " +
					"order by date(TX.TrxDate) DESC";

			String strWareHouseQuery = "SELECT TX.TrxCode,TX.AppTrxId,TX.OrgCode,TX.JourneyCode,TX.VisitCode,TX.UserCode,TX.ClientCode,TX.ClientBranchCode, " +
				       "TX.TrxDate,TX.TrxType,TX.CurrencyCode,TX.PaymentType,TX.TotalAmount,TX.TotalDiscountAmount,TX.TotalTAXAmount ,TX.TrxReasonCode, " +
				       "TX.ReferenceCode ,TX.ClientSignature ,TX.SalesmanSignature ,TX.Status ,TX.FreeNote ,TX.CreatedOn,TX.PreTrxCode ,TX.TRXStatus , " +
				       "TX.BranchPlantCode ,TX.PrintingTimes ,TX.ApproveByCode ,TX.ApprovedDate ,TX.LPOCode ,TX.DeliveryDate , TX.UserCreditAccountCode,TC.SiteName,TC.Address1 " +
					"FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site Where TX.TrxStatus in (12,13) order by date(TX.TrxDate) DESC";
			
			String strDelievredQuery = "SELECT TX.TrxCode,TX.AppTrxId,TX.OrgCode,TX.JourneyCode,TX.VisitCode,TX.UserCode,TX.ClientCode,TX.ClientBranchCode, " +
				       "TX.TrxDate,TX.TrxType,TX.CurrencyCode,TX.PaymentType,TX.TotalAmount,TX.TotalDiscountAmount,TX.TotalTAXAmount ,TX.TrxReasonCode, " +
				       "TX.ReferenceCode ,TX.ClientSignature ,TX.SalesmanSignature ,TX.Status ,TX.FreeNote ,TX.CreatedOn,TX.PreTrxCode ,TX.TRXStatus , " +
				       "TX.BranchPlantCode ,TX.PrintingTimes ,TX.ApproveByCode ,TX.ApprovedDate ,TX.LPOCode ,TX.DeliveryDate , TX.UserCreditAccountCode,TC.SiteName,TC.Address1 " +
					"FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site Where TX.TrxStatus =100 order by date(TX.TrxDate) DESC";
			
			
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				cursor 		= objSqliteDB.rawQuery(strSavedQuery, null);
				hmDetails.put(TrxHeaderDO.get_TRX_STATUS_SAVED(),parseCursorForDetails(cursor,objSqliteDB));
				
				cursor 		= objSqliteDB.rawQuery(strSetupQuery, null);
				hmDetails.put(TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED(),parseCursorForDetails(cursor,objSqliteDB));
				
				
				cursor 		= objSqliteDB.rawQuery(strCancelledQuery, null);
				hmDetails.put(TrxHeaderDO.get_TRX_STATUS_CANCELLED(),parseCursorForDetails(cursor,objSqliteDB));
				
				cursor 		= objSqliteDB.rawQuery(strWareHouseQuery, null);
				hmDetails.put(TrxHeaderDO.get_TRX_STATUS_WAREHOUSE(),parseCursorForDetails(cursor,objSqliteDB));
				
				
				cursor 		= objSqliteDB.rawQuery(strDelievredQuery, null);
				hmDetails.put(TrxHeaderDO.get_TRX_STATUS_DELIVERY(),parseCursorForDetails(cursor,objSqliteDB));
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			
			return hmDetails;
		}
	}
	
	private Vector<TrxHeaderDO> parseCursorForDetails(Cursor cursor,SQLiteDatabase objSqliteDB)
	{
		Vector<TrxHeaderDO> vecDetails = new Vector<TrxHeaderDO>();
		try
		{
			if(cursor.moveToFirst())
			{

				if(objSqliteDB == null || !objSqliteDB.isOpen())
					objSqliteDB = DatabaseHelper.openDataBase();
				
				do
				{
					TrxHeaderDO orderDO 				= new TrxHeaderDO();
					
					orderDO.trxCode    			= 	cursor.getString(0);
					orderDO.appTrxId			= 	cursor.getString(1);
					orderDO.orgCode				= 	cursor.getString(2);
					orderDO.journeyCode			= 	cursor.getString(3);
					orderDO.visitCode			= 	cursor.getString(4);
					orderDO.userCode			= 	cursor.getString(5);
					orderDO.clientCode			= 	cursor.getString(6);
					orderDO.clientBranchCode	= 	cursor.getString(7);
					orderDO.trxDate				= 	cursor.getString(8);
					orderDO.trxType				= 	cursor.getInt(9);	
					orderDO.currencyCode		= 	cursor.getString(10);
					orderDO.paymentType			= 	cursor.getInt(11);
					orderDO.totalAmount			= 	cursor.getFloat(12);
					orderDO.totalDiscountAmount	= 	cursor.getFloat(13);
					orderDO.totalTAXAmount		= 	cursor.getFloat(14);
					orderDO.trxReasonCode		= 	cursor.getString(15);
					
					orderDO.referenceCode		= 	cursor.getString(16);
					orderDO.clientSignature		= 	cursor.getString(17);
					orderDO.salesmanSignature	= 	cursor.getString(18);
					orderDO.status				= 	cursor.getInt(19);
					
					orderDO.freeNote			= 	cursor.getString(20);
					orderDO.createdOn			= 	cursor.getString(21);
					orderDO.preTrxCode			= 	cursor.getString(22);
					orderDO.trxStatus			= 	cursor.getInt(23);
					
					orderDO.branchPlantCode		= 	cursor.getString(24);
					orderDO.printingTimes		= 	cursor.getInt(25);
					orderDO.approveByCode		= 	cursor.getString(26);
					orderDO.approvedDate		= 	cursor.getString(27);
					orderDO.lPOCode				= 	cursor.getString(28);
					orderDO.deliveryDate		= 	cursor.getString(29);
					orderDO.userCreditAccountCode= 	cursor.getString(30);
					orderDO.siteName			= 	cursor.getString(31);
					orderDO.address				=	cursor.getString(32);
//					orderDO.geoLatitude			= 	Double.parseDouble(cursor.getString(31)==null?cursor.getString(31):"0");
//					orderDO.geoLongitude		= 	Double.parseDouble(cursor.getString(32)==null?cursor.getString(32):"0");
					
					orderDO.arrTrxDetailsDOs	=   getProductsOfOrder(objSqliteDB, orderDO.trxCode);
//						orderDO.arrPromotionDOs	    =   getTRXPromotions(objSqliteDB, orderDO.trxCode);
//						if(orderDO.arrTrxDetailsDOs != null && orderDO.arrTrxDetailsDOs.size() > 0)
//							vecOrderList.add(orderDO);
					vecDetails.add(orderDO);
				}
				while(cursor.moveToNext());
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vecDetails;
	}
	
	
	public ArrayList<TrxDetailsDO> getProductsOfOrder(SQLiteDatabase sqLiteDatabase, String orderID)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<TrxDetailsDO> vecItemList = new ArrayList<TrxDetailsDO>();
			Cursor cursor = null;
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT LineNo, TrxCode, ItemCode, OrgCode, TrxReasonCode, TrxDetailsNote,ItemType, " +
						       "BasePrice, UOM, QuantityLevel1, QuantityLevel2,QuantityLevel3, QuantityBU, RequestedBU, " +
						       "ApprovedBU, CollectedBU,"+
				               "FinalBU, PriceUsedLevel1, PriceUsedLevel2, PriceUsedLevel3,"+
				               "TaxPercentage, TotalDiscountPercentage, TotalDiscountAmount,"+
				               "CalculatedDiscountPercentage, CalculatedDiscountAmount,"+
				               "UserDiscountPercentage, UserDiscountAmount, ItemDescription,"+
				               "ItemAltDescription, DistributionCode, AffectedStock, Status,"+
				               "PromoID, PromoType, CreatedOn, TRXStatus, ExpiryDate,"+
				               "RelatedLineID, ItemGroupLevel5, TaxType, SuggestedBU, PushedOn,"+
				               "ModifiedDate, ModifiedTime, Reason, CancelledQuantity,"+
				               "InProcessQuantity, ShippedQuantity, MissedBU, BatchNumber FROM tblTrxDetail WHERE TrxCode='"+orderID+"' Order by ItemCode";
				
				cursor = sqLiteDatabase.rawQuery(query , null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						TrxDetailsDO productDO 				= 	new TrxDetailsDO();
						
						productDO.lineNo					= cursor.getInt(0);
						productDO.trxCode					= cursor.getString(1);
						productDO.itemCode					= cursor.getString(2);
						productDO.orgCode					= cursor.getString(3);
						productDO.trxReasonCode				= cursor.getString(4);
						productDO.trxDetailsNote			= cursor.getString(5);
						productDO.itemType					= cursor.getString(6);
						productDO.basePrice					= cursor.getFloat(7);
						productDO.UOM						= cursor.getString(8).trim();
						productDO.quantityLevel1			= cursor.getFloat(9);
						productDO.quantityLevel2			= cursor.getFloat(10);
						productDO.quantityLevel3			= cursor.getInt(11);
						
						productDO.quantityBU				= cursor.getInt(12);
						productDO.requestedBU				= cursor.getInt(13);
						
						productDO.approvedBU				= cursor.getInt(14);
						productDO.collectedBU				= cursor.getInt(15);
						
						productDO.finalBU					= cursor.getInt(16);
						productDO.priceUsedLevel1			= cursor.getFloat(17);
						productDO.priceUsedLevel2			= cursor.getFloat(18);
						productDO.priceUsedLevel3			= cursor.getFloat(19);
						
						productDO.taxPercentage				= cursor.getFloat(20);
						productDO.totalDiscountPercentage	= cursor.getFloat(21);
						productDO.totalDiscountAmount		= cursor.getFloat(22);
						productDO.calculatedDiscountPercentage= cursor.getFloat(23);
						productDO.calculatedDiscountAmount	= cursor.getFloat(24);
						
						
						productDO.userDiscountPercentage	= cursor.getFloat(25);
						productDO.userDiscountAmount		= cursor.getFloat(26);
						productDO.itemDescription			= cursor.getString(27);
						if(TextUtils.isEmpty(cursor.getString(28)))
							productDO.itemAltDescription			= "";
						else 
							productDO.itemAltDescription			= cursor.getString(28);
						
						if(TextUtils.isEmpty(cursor.getString(29)))
							productDO.distributionCode			= "";
						else 
							productDO.distributionCode		= cursor.getString(29);
						productDO.affectedStock				= cursor.getInt(30);
						productDO.status					= cursor.getInt(31);
						productDO.promoID					= cursor.getInt(32);
						productDO.promoType					= cursor.getString(33);
						productDO.createdOn					= cursor.getString(34);
						productDO.trxStatus					= cursor.getInt(35);
						productDO.expiryDate				= cursor.getString(36);
						
						productDO.relatedLineID				= cursor.getInt(37);
						productDO.itemGroupLevel5			= cursor.getString(38);
						productDO.taxType					= cursor.getInt(39);
						productDO.suggestedBU				= cursor.getInt(40);
						productDO.pushedOn					= cursor.getString(41);
						productDO.modifiedDate				= cursor.getString(42);
						productDO.modifiedTime				= cursor.getString(43);
						
						productDO.reason					= cursor.getString(44);
						productDO.cancelledQuantity			= cursor.getInt(45);
						productDO.inProcessQuantity			= cursor.getInt(46);
						productDO.shippedQuantity			= cursor.getInt(47);
						productDO.missedBU					= cursor.getInt(48);
						productDO.batchCode					= cursor.getString(49);
						
//						productDO.vecDamageImages	 		= getDamageIMagePic(sqLiteDatabase, orderID, productDO.itemCode);
						
						/*if(productDO.UOM.trim().equalsIgnoreCase(TrxDetailsDO.getItemUomLevel1()))
							productDO.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT1;
					      else if(productDO.UOM.trim().equalsIgnoreCase(TrxDetailsDO.getItemUomLevel2()))
					    	  productDO.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT2;
					      else if(productDO.UOM.trim().equalsIgnoreCase(TrxDetailsDO.getItemUomLevel3()))
					    	  productDO.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT3;*/
					      
						vecItemList.add(productDO);
					}
					while(cursor.moveToNext());
				}
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
	
			return vecItemList;
		}
	}
}
