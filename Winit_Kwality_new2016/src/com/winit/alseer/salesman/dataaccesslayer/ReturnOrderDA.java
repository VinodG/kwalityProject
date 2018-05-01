package com.winit.alseer.salesman.dataaccesslayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.DamageImageDO;
import com.winit.alseer.salesman.dataobject.OrderPrintImageDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.sfa.salesman.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class ReturnOrderDA extends BaseDA
{
	public HashMap<Integer,Vector<TrxHeaderDO>> getOrderSummary(String empNo,String customerSiteID)
	{
		synchronized(MyApplication.MyLock)
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			HashMap<Integer,Vector<TrxHeaderDO>> hmOrders   =  new HashMap<Integer, Vector<TrxHeaderDO>>();

			Vector<TrxHeaderDO> vecDetails = new Vector<TrxHeaderDO>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				String query = "SELECT TX.TrxCode,TX.AppTrxId,TX.OrgCode,TX.JourneyCode,TX.VisitCode,TX.UserCode,TX.ClientCode,TX.ClientBranchCode, " +
						"TX.TrxDate,TX.TrxType,TX.CurrencyCode,TX.PaymentType,TX.TotalAmount,TX.TotalDiscountAmount,TX.TotalTAXAmount ,TX.TrxReasonCode, " +
						"TX.ReferenceCode ,TX.ClientSignature ,TX.SalesmanSignature ,TX.Status ,TX.FreeNote ,TX.CreatedOn,TX.PreTrxCode ,TX.TRXStatus , " +
						"TX.BranchPlantCode ,TX.PrintingTimes ,TX.ApproveByCode ,TX.ApprovedDate ,TX.LPOCode ,TX.DeliveryDate , TX.UserCreditAccountCode,TC.SiteName,TC.Address1 " +
						"FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site " +
						"WHERE UserCode ='"+empNo+"' AND TrxDate like '%"+CalendarUtils.getOrderPostDate()+"%'" +
						"AND TrxType ='"+TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()+"' AND TX.ClientCode = '"+customerSiteID+"'";

				cursor		=	slDatabase.rawQuery(query, null);

				if(cursor.moveToFirst())
				{
					do
					{
						TrxHeaderDO orderDO 		= new TrxHeaderDO();

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

						orderDO.siteName			 = 	cursor.getString(31);
						orderDO.address				 = 	cursor.getString(32);


						orderDO.arrTrxDetailsDOs	=   getProductsOfOrder(slDatabase, orderDO.trxCode,orderDO.trxStatus);

						if(!hmOrders.containsKey(orderDO.trxStatus))
							vecDetails = new Vector<TrxHeaderDO>();
						else
							vecDetails = hmOrders.get(orderDO.trxStatus);

						if(orderDO.arrTrxDetailsDOs!=null && orderDO.arrTrxDetailsDOs.size()>0)
						{
							vecDetails.add(orderDO);
							hmOrders.put(orderDO.trxStatus, vecDetails);
						}

					}
					while(cursor.moveToNext());
				}

				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(slDatabase != null)
					slDatabase.close();
			}
			return hmOrders;
		}
	}

	public ArrayList<TrxDetailsDO> getProductsOfOrder(SQLiteDatabase sqLiteDatabase, String orderID,int trxStatus)
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
						"InProcessQuantity, ShippedQuantity, MissedBU, BatchNumber FROM tblTrxDetail WHERE TrxCode='"+orderID+"'";

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
						productDO.UOM						= cursor.getString(8);
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
						productDO.itemAltDescription		= cursor.getString(28);
						productDO.distributionCode			= cursor.getString(29);
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

						productDO.vecDamageImages	 		= getDamageIMagePic(sqLiteDatabase, orderID, productDO.itemCode);

						if(trxStatus == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER_STATUS_APPROVED())
						{
							productDO.collectedBU      =   productDO.approvedBU;;
							productDO.finalBU		   =   productDO.approvedBU;;
							productDO.quantityLevel3   =   productDO.approvedBU;;
							productDO.quantityBU	   =   productDO.approvedBU;;
						}

						vecItemList.add(productDO);
					}
					while(cursor.moveToNext());
				}

				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return vecItemList;
		}
	}

	public Vector<DamageImageDO> getDamageIMagePic(SQLiteDatabase sqLiteDatabase, String orderID, String itemCode)
	{
		synchronized(MyApplication.MyLock)
		{
			Vector<DamageImageDO> vecImage = new Vector<DamageImageDO>();
			Cursor cursor = null;
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();

				cursor = sqLiteDatabase.rawQuery("SELECT *FROM tblOrderImage where OrderNo ='"+orderID+"' AND ItemCode ='"+itemCode+"'", null);

				if(cursor.moveToFirst())
				{
					do
					{
						DamageImageDO damageImageDO = new DamageImageDO();
						damageImageDO.OrderNo      = cursor.getString(0);
						damageImageDO.ItemCode     = cursor.getString(1);
						damageImageDO.LineNo       = cursor.getString(2);
						damageImageDO.ImagePath    = cursor.getString(3);
						damageImageDO.CapturedDate = cursor.getString(4);
						vecImage.add(damageImageDO);
					}
					while(cursor.moveToNext());
				}

				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return vecImage;
		}
	}

	public void updateReturnOrderStatus(ArrayList<TrxHeaderDO> arrayList)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();

				SQLiteStatement stmtSelect = objSqliteDB.compileStatement("SELECT COUNT(*) FROM tblTrxHeader WHERE TrxCode = ?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblTrxHeader(TrxCode, AppTrxId, OrgCode, JourneyCode, VisitCode, UserCode, ClientCode, ClientBranchCode, " +
						"TrxDate, TrxType, CurrencyCode, PaymentType, TotalAmount, TotalDiscountAmount, TrxReasonCode,ClientSignature,SalesmanSignature,Status, FreeNote, " +
						"CreatedOn, PreTrxCode, TRXStatus, PrintingTimes, ApproveByCode, ApprovedDate,DeliveryDate,TrxSubType,LPO,Narrotion,RateDiff,SplDisc,Division,VAT) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("Update tblTrxHeader set TotalAmount=?, TotalDiscountAmount=?, TrxReasonCode=?, Status=?,TRXStatus=?, ApproveByCode=?, ApprovedDate=?,LPO=?,Narrotion=?,RateDiff=?,SplDisc=?,VAT=? where TrxCode=?");

				SQLiteStatement stmtSelectDetail = objSqliteDB.compileStatement("SELECT COUNT(*) FROM tblTrxDetail WHERE TrxCode = ? AND ItemCode=?");


				SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblTrxDetail (LineNo, TrxCode, ItemCode,OrgCode,TrxReasonCode, TrxDetailsNote, ItemType,BasePrice, UOM, " +
						"QuantityLevel1, QuantityLevel2, QuantityLevel3, QuantityBU, RequestedBU, ApprovedBU, CollectedBU, FinalBU, " +
						"PriceUsedLevel1, PriceUsedLevel2, PriceUsedLevel3, TotalDiscountPercentage,TotalDiscountAmount, " +
						"CalculatedDiscountPercentage, CalculatedDiscountAmount, UserDiscountPercentage, UserDiscountAmount,ItemDescription, " +
						"AffectedStock, Status, PromoID, PromoType, CreatedOn, TRXStatus, ExpiryDate, RelatedLineID, ItemGroupLevel5, " +
						"TaxType, SuggestedBU, PushedOn, Reason, CancelledQuantity, InProcessQuantity, ShippedQuantity, MissedBU, " +
						"BatchNumber,VAT,TotalVATAmount) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");


				SQLiteStatement stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblTrxDetail SET TrxReasonCode=?,TrxDetailsNote=?,ItemType=?,"
						+ "QuantityLevel1=?, QuantityLevel2=?, QuantityLevel3=?, QuantityBU=?, RequestedBU=?,ApprovedBU=?,CollectedBU=?,FinalBU=?, " +
						"PriceUsedLevel1=?, PriceUsedLevel2=?, PriceUsedLevel3=?, TotalDiscountPercentage=?,TotalDiscountAmount=?,AffectedStock=?,Status=?,TRXStatus=?,ExpiryDate=?,ItemGroupLevel5=?,"
						+ "SuggestedBU=?,Reason=?,CancelledQuantity=?,InProcessQuantity=?,ShippedQuantity=?,MissedBU =?,VAT=?,TotalVATAmount=? WHERE TrxCode = ? AND ItemCode=?");

				for(TrxHeaderDO trxHeaderDO:arrayList){
					stmtSelect.bindString(1, trxHeaderDO.trxCode);
					long count = stmtSelect.simpleQueryForLong();

					if(count <= 0 )
					{
						stmtInsert.bindString(1,  trxHeaderDO.trxCode);
						stmtInsert.bindString(2,  trxHeaderDO.appTrxId);
						stmtInsert.bindString(3,  trxHeaderDO.orgCode);
						stmtInsert.bindString(4,  trxHeaderDO.journeyCode);
						stmtInsert.bindString(5,  trxHeaderDO.visitCode);
						stmtInsert.bindString(6,  trxHeaderDO.userCode);
						stmtInsert.bindString(7,  trxHeaderDO.clientCode);
						stmtInsert.bindString(8,  trxHeaderDO.clientBranchCode);
						stmtInsert.bindString(9,  trxHeaderDO.trxDate);
						stmtInsert.bindLong(10,   trxHeaderDO.trxType);
						stmtInsert.bindString(11, trxHeaderDO.currencyCode);
						stmtInsert.bindLong(12,   trxHeaderDO.paymentType);
						stmtInsert.bindDouble(13, trxHeaderDO.totalAmount);
						stmtInsert.bindDouble(14, trxHeaderDO.totalDiscountAmount);
						stmtInsert.bindString(15, ""+trxHeaderDO.trxReasonCode);
						stmtInsert.bindString(16, ""+trxHeaderDO.clientSignature);
						stmtInsert.bindString(17, ""+trxHeaderDO.salesmanSignature);
						stmtInsert.bindLong(18,   trxHeaderDO.status);
						stmtInsert.bindString(19, ""+trxHeaderDO.freeNote);
						stmtInsert.bindString(20, ""+trxHeaderDO.createdOn);
						stmtInsert.bindString(21, trxHeaderDO.preTrxCode);
						stmtInsert.bindLong(22,   trxHeaderDO.trxStatus);
						stmtInsert.bindLong(23,   trxHeaderDO.printingTimes);
						stmtInsert.bindString(24, ""+trxHeaderDO.approveByCode);
						stmtInsert.bindString(25, ""+trxHeaderDO.approvedDate);
						stmtInsert.bindString(26, trxHeaderDO.deliveryDate);
						stmtInsert.bindLong(27, trxHeaderDO.trxSubType);

						stmtInsert.bindString(28, ""+trxHeaderDO.LPONo);
						stmtInsert.bindString(29, ""+trxHeaderDO.PromotionReason);
						stmtInsert.bindDouble(30, trxHeaderDO.rateDiff);
						stmtInsert.bindDouble(31, trxHeaderDO.specialDiscPercent);
						stmtInsert.bindLong(32, trxHeaderDO.Division);
						stmtInsert.bindDouble(33, trxHeaderDO.totalVATAmount);

						stmtInsert.executeInsert();
					}
					else{

						stmtUpdate.bindDouble(1, trxHeaderDO.totalAmount);
						stmtUpdate.bindDouble(2, trxHeaderDO.totalDiscountAmount);
						stmtUpdate.bindString(3, trxHeaderDO.trxReasonCode);
						stmtUpdate.bindLong(4, trxHeaderDO.status);
						stmtUpdate.bindLong(5, trxHeaderDO.trxStatus);
						stmtUpdate.bindString(6, trxHeaderDO.approveByCode);
						stmtUpdate.bindString(7, trxHeaderDO.approvedDate);

						stmtUpdate.bindString(8, ""+trxHeaderDO.LPONo);
						stmtUpdate.bindString(9, ""+trxHeaderDO.Narration);
						stmtUpdate.bindDouble(10, trxHeaderDO.rateDiff);
						stmtUpdate.bindDouble(11, trxHeaderDO.specialDiscPercent);
						stmtUpdate.bindDouble(12, trxHeaderDO.totalVATAmount);

						stmtUpdate.bindString(13, trxHeaderDO.trxCode);
						stmtUpdate.execute();
					}



					for(TrxDetailsDO trxDetailsDO : trxHeaderDO.arrTrxDetailsDOs)
					{
						stmtSelectDetail.bindString(1, trxHeaderDO.trxCode);
						stmtSelectDetail.bindString(2, trxDetailsDO.itemCode);
						long countL = stmtSelectDetail.simpleQueryForLong();

						if(countL <= 0)
						{
							stmtInsertOrder.bindLong(1, trxDetailsDO.lineNo);
							stmtInsertOrder.bindString(2, trxHeaderDO.trxCode);
							stmtInsertOrder.bindString(3, trxDetailsDO.itemCode);
							stmtInsertOrder.bindString(4, trxHeaderDO.orgCode);
							stmtInsertOrder.bindString(5, ""+trxDetailsDO.trxReasonCode);
							stmtInsertOrder.bindString(6, ""+trxDetailsDO.trxDetailsNote);
							stmtInsertOrder.bindString(7, trxDetailsDO.itemType);
							stmtInsertOrder.bindDouble(8, trxDetailsDO.basePrice);
							stmtInsertOrder.bindString(9, trxDetailsDO.UOM);
							stmtInsertOrder.bindDouble(10, trxDetailsDO.quantityLevel1);
							stmtInsertOrder.bindDouble(11, trxDetailsDO.quantityLevel2);
							stmtInsertOrder.bindLong(12, trxDetailsDO.quantityLevel3);
							stmtInsertOrder.bindLong(13, trxDetailsDO.quantityBU);
							stmtInsertOrder.bindLong(14, trxDetailsDO.requestedBU);
							stmtInsertOrder.bindLong(15, trxDetailsDO.approvedBU);
							stmtInsertOrder.bindLong(16, trxDetailsDO.collectedBU);
							stmtInsertOrder.bindLong(17, trxDetailsDO.finalBU);
							stmtInsertOrder.bindDouble(18, trxDetailsDO.priceUsedLevel1);
							stmtInsertOrder.bindDouble(19, trxDetailsDO.priceUsedLevel2);
							stmtInsertOrder.bindDouble(20, trxDetailsDO.priceUsedLevel3);
							stmtInsertOrder.bindDouble(21, trxDetailsDO.totalDiscountPercentage);
							stmtInsertOrder.bindDouble(22, trxDetailsDO.totalDiscountAmount);

							stmtInsertOrder.bindDouble(23, trxDetailsDO.calculatedDiscountPercentage);
							stmtInsertOrder.bindDouble(24, trxDetailsDO.calculatedDiscountAmount);
							stmtInsertOrder.bindDouble(25, trxDetailsDO.userDiscountPercentage);
							stmtInsertOrder.bindDouble(26, trxDetailsDO.userDiscountAmount);
							stmtInsertOrder.bindString(27, trxDetailsDO.itemDescription);
							stmtInsertOrder.bindLong(28, trxDetailsDO.affectedStock);
							stmtInsertOrder.bindLong(29, trxDetailsDO.status);

							stmtInsertOrder.bindString(30, ""+trxDetailsDO.promoID);
							stmtInsertOrder.bindString(31, ""+trxDetailsDO.promoType);
							stmtInsertOrder.bindString(32, ""+trxHeaderDO.createdOn);
							stmtInsertOrder.bindLong(33, trxDetailsDO.trxStatus);
							stmtInsertOrder.bindString(34, ""+trxDetailsDO.expiryDate);
							stmtInsertOrder.bindLong(35, trxDetailsDO.relatedLineID);
							stmtInsertOrder.bindString(36, trxDetailsDO.itemGroupLevel5);

							stmtInsertOrder.bindLong(37, trxDetailsDO.taxType);
							stmtInsertOrder.bindLong(38, trxDetailsDO.suggestedBU);
							stmtInsertOrder.bindString(39, ""+trxDetailsDO.pushedOn);
							stmtInsertOrder.bindString(40, ""+trxDetailsDO.reason);
							stmtInsertOrder.bindLong(41, trxDetailsDO.cancelledQuantity);
							stmtInsertOrder.bindLong(42, trxDetailsDO.inProcessQuantity);
							stmtInsertOrder.bindLong(43, trxDetailsDO.shippedQuantity);
							stmtInsertOrder.bindLong(44, trxDetailsDO.missedBU);
							stmtInsertOrder.bindString(45, ""+trxDetailsDO.batchCode);
							stmtInsertOrder.bindString(46, ""+trxDetailsDO.vatPercentage);
							stmtInsertOrder.bindString(47, ""+trxDetailsDO.VATAmountNew);

							stmtInsertOrder.executeInsert();
						}
						else
						{
							stmtUpdateOrder.bindString(1, trxDetailsDO.trxReasonCode);
							stmtUpdateOrder.bindString(2, trxDetailsDO.trxDetailsNote);
							stmtUpdateOrder.bindString(3, trxDetailsDO.itemType);
							stmtUpdateOrder.bindDouble(4, trxDetailsDO.quantityLevel1);
							stmtUpdateOrder.bindDouble(5, trxDetailsDO.quantityLevel2);
							stmtUpdateOrder.bindLong(6, trxDetailsDO.quantityLevel3);
							stmtUpdateOrder.bindLong(7, trxDetailsDO.quantityBU);
							stmtUpdateOrder.bindLong(8, trxDetailsDO.requestedBU);
							stmtUpdateOrder.bindLong(9, trxDetailsDO.approvedBU);
							stmtUpdateOrder.bindLong(10, trxDetailsDO.collectedBU);
							stmtUpdateOrder.bindLong(11, trxDetailsDO.finalBU);
							stmtUpdateOrder.bindDouble(12, trxDetailsDO.priceUsedLevel1);
							stmtUpdateOrder.bindDouble(13, trxDetailsDO.priceUsedLevel2);
							stmtUpdateOrder.bindDouble(14, trxDetailsDO.priceUsedLevel3);
							stmtUpdateOrder.bindDouble(15, trxDetailsDO.totalDiscountPercentage);
							stmtUpdateOrder.bindDouble(16, trxDetailsDO.totalDiscountAmount);
							stmtUpdateOrder.bindLong(17, trxDetailsDO.affectedStock);
							stmtUpdateOrder.bindLong(18, trxDetailsDO.status);
							stmtUpdateOrder.bindLong(19, trxDetailsDO.trxStatus);
							stmtUpdateOrder.bindString(20, trxDetailsDO.expiryDate);
							stmtUpdateOrder.bindString(21, trxDetailsDO.itemGroupLevel5);
							stmtUpdateOrder.bindLong(22, trxDetailsDO.suggestedBU);
							stmtUpdateOrder.bindString(23, trxDetailsDO.reason);
							stmtUpdateOrder.bindLong(24, trxDetailsDO.cancelledQuantity);
							stmtUpdateOrder.bindLong(25, trxDetailsDO.inProcessQuantity);
							stmtUpdateOrder.bindLong(26, trxDetailsDO.shippedQuantity);
							stmtUpdateOrder.bindLong(27, trxDetailsDO.missedBU);
							stmtUpdateOrder.bindString(28,""+trxDetailsDO.vatPercentage);
							stmtUpdateOrder.bindString(29,""+trxDetailsDO.VATAmountNew);
							stmtUpdateOrder.bindString(30, trxHeaderDO.trxCode);
							stmtUpdateOrder.bindString(31, trxDetailsDO.itemCode);
							stmtUpdateOrder.execute();
						}
					}
				}
				stmtSelect.close();
				stmtSelectDetail.close();
				stmtUpdate.close();
				stmtUpdateOrder.close();
				stmtInsert.close();
				stmtInsertOrder.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();

				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}

	public ArrayList<DamageImageDO> getAllDamagedImages() {
		synchronized (MyApplication.MyLock) {
			ArrayList<DamageImageDO> arrDamagedImageDOs=new ArrayList<DamageImageDO>();
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursorDamagedImages=null;
			String query="Select OrderNo, ItemCode, ImagePath from tblOrderImage where Status=0";
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursorDamagedImages = sqLiteDatabase.rawQuery(query, null);
				if(cursorDamagedImages.moveToFirst()){
					do {
						DamageImageDO damageImageDO = new DamageImageDO();
						damageImageDO.OrderNo=cursorDamagedImages.getString(0);
						damageImageDO.ItemCode=cursorDamagedImages.getString(1);
						damageImageDO.ImagePath=cursorDamagedImages.getString(2);
						arrDamagedImageDOs.add(damageImageDO);
					} while (cursorDamagedImages.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursorDamagedImages!=null && !cursorDamagedImages.isClosed())
					cursorDamagedImages.close();
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return arrDamagedImageDOs;
		}
	}
	public boolean insertAllOrderPrintImages(OrderPrintImageDO orderPrintImageDO)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;

			try {

				objSqliteDB = DatabaseHelper.openDataBase();

//				"tblOrderPrintImage" ("TrxCode" VARCHAR, "ImagePath" VARCHAR, "ImageType" VARCHAR, "UserCode" VARCHAR, "CaptureDate" DATETIME, "Status" INTEGER)
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT Count(*) from tblOrderPrintImage WHERE TrxCode =? ");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblOrderPrintImage( ImagePath, ImageType, UserCode, CaptureDate,Status,TrxCode ) VALUES(?,?,?,?,?,? )");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblOrderPrintImage SET ImagePath = ?, ImageType = ?, UserCode = ?, CaptureDate=?, Status=?  where   TrxCode =?");

				if(orderPrintImageDO!=null)
				{
					stmtSelectRec.bindString(1, orderPrintImageDO.TrxCode);

					long countRec = 0;
					if(!TextUtils.isEmpty(orderPrintImageDO.TrxCode))
						countRec = stmtSelectRec.simpleQueryForLong();

					if(countRec >0)
					{
						stmtUpdate.bindString(1, orderPrintImageDO.ImagePath);
						stmtUpdate.bindString(2, orderPrintImageDO.ImageType);
						stmtUpdate.bindString(3, orderPrintImageDO.UserCode);
						stmtUpdate.bindString(4, orderPrintImageDO.CaptureDate);
						stmtUpdate.bindLong(5, orderPrintImageDO.status);
						stmtUpdate.bindString(6, orderPrintImageDO.TrxCode);
						stmtUpdate.execute();
					}
					else
					{


						stmtInsert.bindString(1, orderPrintImageDO.ImagePath);
						stmtInsert.bindString(2, orderPrintImageDO.ImageType);
						stmtInsert.bindString(3, orderPrintImageDO.UserCode);
						stmtInsert.bindString(4, orderPrintImageDO.CaptureDate);
						stmtInsert.bindLong(5, orderPrintImageDO.status);
						stmtInsert.bindString(6, orderPrintImageDO.TrxCode);
						stmtInsert.executeInsert();
					}


				}

				stmtSelectRec.close();
				stmtUpdate.close();
				stmtInsert.close();

			} catch (Exception e){
				e.printStackTrace();
				return false;
			}finally{

				if(objSqliteDB!= null)
					objSqliteDB.close();
			}
			return true;
		}
	}
	public boolean  insertOrderPrintImages(Vector<OrderPrintImageDO> vecOrderPrintImageDO)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;

			try {

				objSqliteDB = DatabaseHelper.openDataBase();

//				"tblOrderPrintImage" ("TrxCode" VARCHAR, "ImagePath" VARCHAR, "ImageType" VARCHAR, "UserCode" VARCHAR, "CaptureDate" DATETIME, "Status" INTEGER)
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT Count(*) from tblOrderPrintImage WHERE TrxCode =? ");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblOrderPrintImage( ImagePath, ImageType, UserCode, CaptureDate,Status,TrxCode ) VALUES(?,?,?,?,?,? )");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblOrderPrintImage SET ImagePath = ?, ImageType = ?, UserCode = ?, CaptureDate=?, Status=?  where   TrxCode =?");
				for (int i= 0;i<vecOrderPrintImageDO.size();i++)
				{
					OrderPrintImageDO orderPrintImageDO = vecOrderPrintImageDO.get(i);
					if (orderPrintImageDO != null) {
						stmtSelectRec.bindString(1, orderPrintImageDO.TrxCode);

						long countRec = 0;
						if (!TextUtils.isEmpty(orderPrintImageDO.TrxCode))
							countRec = stmtSelectRec.simpleQueryForLong();

						if (countRec > 0) {
							stmtUpdate.bindString(1, orderPrintImageDO.ImagePath);
							stmtUpdate.bindString(2, orderPrintImageDO.ImageType);
							stmtUpdate.bindString(3, orderPrintImageDO.UserCode);
							stmtUpdate.bindString(4, orderPrintImageDO.CaptureDate);
							stmtUpdate.bindLong(5, orderPrintImageDO.status);
							stmtUpdate.bindString(6, orderPrintImageDO.TrxCode);
							stmtUpdate.execute();
						} else {


							stmtInsert.bindString(1, orderPrintImageDO.ImagePath);
							stmtInsert.bindString(2, orderPrintImageDO.ImageType);
							stmtInsert.bindString(3, orderPrintImageDO.UserCode);
							stmtInsert.bindString(4, orderPrintImageDO.CaptureDate);
							stmtInsert.bindLong(5, orderPrintImageDO.status);
							stmtInsert.bindString(6, orderPrintImageDO.TrxCode);
							stmtInsert.executeInsert();
						}


					}

				}
				stmtSelectRec.close();
				stmtUpdate.close();
				stmtInsert.close();

			} catch (Exception e){
				e.printStackTrace();
				return false;
			}finally{

				if(objSqliteDB!= null)
					objSqliteDB.close();
			}
			return true;
		}
	}

	public ArrayList<OrderPrintImageDO>  getAllOrderPrintImages() {
		synchronized (MyApplication.MyLock) {
			ArrayList<OrderPrintImageDO> arrOrderPrintImageDO=new ArrayList<OrderPrintImageDO>();
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursor =null;

			//	 ("TrxCode" VARCHAR, "ImagePath" VARCHAR, "ImageType" VARCHAR, "UserCode" VARCHAR, "CaptureDate" DATETIME, "Status" INTEGER)

			String query="Select TrxCode, ImagePath, ImageType, UserCode,CaptureDate,Status from tblOrderPrintImage where Status=0 OR Status=-100 ";
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor = sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst()){
					do {
						OrderPrintImageDO orderPrintImageDO = new OrderPrintImageDO();
						orderPrintImageDO.TrxCode=cursor.getString(0);
						orderPrintImageDO.ImagePath=cursor.getString(1);
						orderPrintImageDO.ImageType=cursor.getString(3);
						orderPrintImageDO.UserCode=cursor.getString(4);
						orderPrintImageDO.CaptureDate=cursor.getString(5);
						arrOrderPrintImageDO.add(orderPrintImageDO);
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
			return arrOrderPrintImageDO;
		}
	}
	public ArrayList<OrderPrintImageDO>  getAllOrderPrintImagesNew() {
		synchronized (MyApplication.MyLock) {
			ArrayList<OrderPrintImageDO> arrOrderPrintImageDO=new ArrayList<OrderPrintImageDO>();
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursor =null;

			//	 ("TrxCode" VARCHAR, "ImagePath" VARCHAR, "ImageType" VARCHAR, "UserCode" VARCHAR, "CaptureDate" DATETIME, "Status" INTEGER)

			String query="Select TrxCode, ImagePath, ImageType, UserCode,CaptureDate,Status from tblOrderPrintImage where Status= "+OrderPrintImageDO.STATUS_UPLOADED;
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor = sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst()){
					do {
						OrderPrintImageDO orderPrintImageDO = new OrderPrintImageDO();
						orderPrintImageDO.TrxCode=cursor.getString(0);
						orderPrintImageDO.ImagePath=cursor.getString(1);
						orderPrintImageDO.ImageType=cursor.getString(2);
						orderPrintImageDO.UserCode=cursor.getString(3);
						orderPrintImageDO.CaptureDate=cursor.getString(4);
//						Integer x=0;
//						try
//						{
//							x= 	(Integer) Integer.parseInt(cursor. getString(5));
//
//						}
//						catch (Exception e) {
						Integer x=OrderPrintImageDO.STATUS_UPLOADED;
//						}
						orderPrintImageDO.status=x ;
						arrOrderPrintImageDO.add(orderPrintImageDO);
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
			return arrOrderPrintImageDO;
		}
	}
	public void updateDamagedImageStatus(DamageImageDO damageImageDO,String serverUrl){
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase=null;
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement("Update tblOrderImage  set Status=?, ImagePath=? where OrderNo=? and ItemCode=? and ImagePath=?");
				sqLiteStatement.bindLong(1, damageImageDO.status);
				sqLiteStatement.bindString(2, serverUrl);
				sqLiteStatement.bindString(3, damageImageDO.OrderNo);
				sqLiteStatement.bindString(4, damageImageDO.ItemCode);
				sqLiteStatement.bindString(5, damageImageDO.ImagePath);
				sqLiteStatement.execute();
				sqLiteStatement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
		}
	}
	public void  updateOrderPrintImagesStatus(OrderPrintImageDO orderPrintImageDO,String serverUrl){
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase=null;
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				//	 ("TrxCode" VARCHAR, "ImagePath" VARCHAR, "ImageType" VARCHAR, "UserCode" VARCHAR, "CaptureDate" DATETIME, "Status" INTEGER)
				SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement("Update tblOrderPrintImage  set Status=?, ImagePath=? where TrxCode=?  ");
				sqLiteStatement.bindLong(1, orderPrintImageDO.status);
				sqLiteStatement.bindString(2, serverUrl);
				sqLiteStatement.bindString(3, orderPrintImageDO.TrxCode);
				sqLiteStatement.execute();
				sqLiteStatement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
		}
	}


	public HashMap<String,ArrayList<DamageImageDO>> getAllGRVImages() {
		synchronized (MyApplication.MyLock) {
			HashMap<String,ArrayList<DamageImageDO>> hmImages = new HashMap<String, ArrayList<DamageImageDO>>();
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursorDamagedImages=null;
			String query="SELECT distinct OI.OrderNo,OI.ItemCode,OI.LineNo,OI.ImagePath,OI.CapturedDate from tblOrderImage OI INNER JOIN tblTrxHeader TX where OI.Status = '"+TrxHeaderDO.get_TRX_Image_Upload_STATUS()+"' AND OI.OrderNo = TX.TrxCode AND TX.Status = '"+TrxHeaderDO.get_TRX_Upload_STATUS()+"' AND TX.TrxType ='"+TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()+"'";

			try {
				ArrayList<DamageImageDO> arrList = new ArrayList<DamageImageDO>();
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursorDamagedImages = sqLiteDatabase.rawQuery(query, null);
				if(cursorDamagedImages.moveToFirst()){
					do {
						DamageImageDO damageImageDO = new DamageImageDO();
						damageImageDO.OrderNo=cursorDamagedImages.getString(0);
						damageImageDO.ItemCode=cursorDamagedImages.getString(1);
						damageImageDO.LineNo =cursorDamagedImages.getString(2);
						damageImageDO.ImagePath =cursorDamagedImages.getString(3);
						damageImageDO.CapturedDate =cursorDamagedImages.getString(4);

						if(hmImages.containsKey(damageImageDO.OrderNo))
							arrList = hmImages.get(damageImageDO.OrderNo);
						else
							arrList = new ArrayList<DamageImageDO>();

						arrList.add(damageImageDO);
						hmImages.put(damageImageDO.OrderNo, arrList);

					} while (cursorDamagedImages.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursorDamagedImages!=null && !cursorDamagedImages.isClosed())
					cursorDamagedImages.close();
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return hmImages;
		}
	}

	public void updateDamagedImageUploadDetailStatus(ArrayList<String> arrList){
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase=null;
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();

				String orderNo = "";

				for(String key:arrList)
					orderNo = orderNo + key +",";

				if(orderNo!=null && orderNo.length()>0)
				{
					orderNo = orderNo.substring(0,orderNo.length()-1);

					SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement("Update tblOrderImage  set Status=? Where OrderNo IN('"+orderNo+"')");
					sqLiteStatement.bindLong(1,1);
					sqLiteStatement.execute();
					sqLiteStatement.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
		}
	}
}
