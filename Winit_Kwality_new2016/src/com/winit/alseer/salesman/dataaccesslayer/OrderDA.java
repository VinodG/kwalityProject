package com.winit.alseer.salesman.dataaccesslayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.CustomerSite_NewDO;
import com.winit.alseer.salesman.dataobject.CustomerStatmentDO;
import com.winit.alseer.salesman.dataobject.DailySalesData;
import com.winit.alseer.salesman.dataobject.DamageImageDO;
import com.winit.alseer.salesman.dataobject.OrderDO;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.TrxPromotionDO;
import com.winit.alseer.salesman.dataobject.UOMConversionFactorDO;
import com.winit.alseer.salesman.dataobject.UserProductivityDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class OrderDA extends BaseDA
{
	public String insertOrderDetails_Promo(TrxHeaderDO trxHeaderDO)//tblUsers
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			String orderId = "";
			Cursor cursor = null;
			long lineNoCount = 0;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				if(TextUtils.isEmpty(trxHeaderDO.trxCode))
				{
					String query = "";
					String type = "";
					
					if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()) {
//						if(trxHeaderDO.Division <= 0)
//							type = AppConstants.GRV;
//						else
//							type = AppConstants.Food_GRV;
						if(trxHeaderDO.Division <= 0)
							type = AppConstants.GRV;
						else if(trxHeaderDO.Division == 1)
							type = AppConstants.Food_GRV;
						else
							type = AppConstants.TPT_GRV;

					}
					else {
						if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED()) {
//							if(trxHeaderDO.Division <= 0)
//								type = AppConstants.SAVED;
//							else
//								type = AppConstants.Food_SAVED;
							if(trxHeaderDO.Division <= 0)
								type = AppConstants.SAVED;
							else if(trxHeaderDO.Division == 1)
								type = AppConstants.Food_SAVED;
							else
								type = AppConstants.TPT_SAVED;
						}
						
						else if(trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER()) {
//							if(trxHeaderDO.Division <= 0)
//								type = AppConstants.FOC;
//							else
//								type = AppConstants.Food_FOC;
							if(trxHeaderDO.Division <= 0)
								type = AppConstants.FOC;
							else if(trxHeaderDO.Division == 1)
								type = AppConstants.Food_FOC;
							else
								type = AppConstants.TPT_FOC;
						}
						else {
//							if(trxHeaderDO.Division <= 0)
//								type = AppConstants.Order;
//							else
//								type = AppConstants.Food_Order;
							if(trxHeaderDO.Division <= 0)
								type = AppConstants.Order;
							else if(trxHeaderDO.Division == 1)
								type = AppConstants.Food_Order;
							else
								type = AppConstants.TPT_Order;
						}
					}
					query = "SELECT id from tblOfflineData where  Type ='"+type+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) Order By id Limit 1";
					
					cursor = objSqliteDB.rawQuery(query, null);
					if(cursor != null && cursor.moveToFirst())
						orderId = cursor.getString(0);
					
					LogUtils.debug("offlinedata_query", query);
					LogUtils.debug("offlinedata_query", orderId);

					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				else
					orderId = trxHeaderDO.trxCode;
				
				SQLiteStatement stmtSelectLineNODetail = objSqliteDB.compileStatement("SELECT IFNULL(max(LineNo),0) FROM tblTrxDetail where TrxCode=?");
				stmtSelectLineNODetail.bindString(1, orderId);
				lineNoCount = stmtSelectLineNODetail.simpleQueryForLong();
				
				if(!TextUtils.isEmpty(orderId))
				{
					objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+orderId+"'");
						
					
					SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblTrxHeader(TrxCode, AppTrxId, OrgCode, JourneyCode, VisitCode, UserCode, ClientCode, ClientBranchCode, " +
																			 "TrxDate, TrxType, CurrencyCode, PaymentType, TotalAmount, TotalDiscountAmount, TrxReasonCode,ClientSignature,SalesmanSignature,Status, FreeNote, " +
																			 "CreatedOn, PreTrxCode, TRXStatus, PrintingTimes, ApproveByCode, ApprovedDate,DeliveryDate,GeoX,GeoY,ReferenceCode,TrxSubType,SplDisc,LPO," +
							" Narrotion, Reason, RateDiff,Division,BranchPlantCode,VAT) " +
							"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						trxHeaderDO.trxCode    = orderId;
						trxHeaderDO.preTrxCode = orderId;
						
						stmtInsert.bindString(1,  trxHeaderDO.trxCode);
						stmtInsert.bindString(2,  trxHeaderDO.appTrxId);
						stmtInsert.bindString(3,  trxHeaderDO.orgCode);
						stmtInsert.bindString(4,  trxHeaderDO.journeyCode);
						stmtInsert.bindString(5,  trxHeaderDO.visitCode);
						stmtInsert.bindString(6,  trxHeaderDO.userCode);
						stmtInsert.bindString(7,  trxHeaderDO.clientCode);
						stmtInsert.bindString(8,  trxHeaderDO.clientBranchCode);
						stmtInsert.bindString(9,  ""+trxHeaderDO.trxDate);
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
					
						stmtInsert.bindLong(22,trxHeaderDO.trxStatus);
						
						stmtInsert.bindLong(23,   trxHeaderDO.printingTimes);
						stmtInsert.bindString(24, ""+trxHeaderDO.approveByCode);
						stmtInsert.bindString(25, ""+trxHeaderDO.approvedDate);
						stmtInsert.bindString(26, trxHeaderDO.deliveryDate);
						stmtInsert.bindString(27, ""+trxHeaderDO.geoLatitude);
						stmtInsert.bindString(28, ""+trxHeaderDO.geoLongitude);
						stmtInsert.bindString(29, ""+trxHeaderDO.referenceCode);
						stmtInsert.bindString(30, ""+trxHeaderDO.trxSubType);
						stmtInsert.bindString(31, ""+trxHeaderDO.specialDiscPercent);
						
						stmtInsert.bindString(32, ""+trxHeaderDO.LPONo);
						stmtInsert.bindString(33, ""+trxHeaderDO.PromotionReason);
						stmtInsert.bindString(34, ""+trxHeaderDO.returnReason);
						stmtInsert.bindString(35, ""+trxHeaderDO.rateDiff);
						stmtInsert.bindString(36, ""+trxHeaderDO.Division);
						stmtInsert.bindString(37, ""+trxHeaderDO.statementDiscount);
						stmtInsert.bindString(38, ""+trxHeaderDO.totalVATAmount);

						
						//updating user AchievedTarget per order
						
						if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED() && trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER())
						{
							float salesAmount=trxHeaderDO.totalAmount-trxHeaderDO.totalDiscountAmount;
							
							updateUserAchievedTarget(salesAmount, trxHeaderDO.userCode, objSqliteDB,trxHeaderDO.Division,0);
							
							//===============Start Updating Monthly Sales amount=========================

							/*
							 *  Need to change it to statement
							 */
							String query ="SELECT *from tblCustomerMonthlySales where  CustomerCode ='"+trxHeaderDO.clientCode+"'";
							
							cursor = objSqliteDB.rawQuery(query, null);
							if(cursor.moveToFirst())
							{
								SQLiteStatement stmtUpdateMonthlySalesAmount = objSqliteDB.compileStatement("update tblCustomerMonthlySales set SalesAmount=Cast((Cast(SalesAmount as float)+?) as float) where CustomerCode=?");
								stmtUpdateMonthlySalesAmount.bindDouble(1, salesAmount);
								stmtUpdateMonthlySalesAmount.bindString(2, trxHeaderDO.clientCode);
								stmtUpdateMonthlySalesAmount.execute();
								stmtUpdateMonthlySalesAmount.close();
							}
							else
							{
								SQLiteStatement stmtInsertMonthlySalesAmount = objSqliteDB.compileStatement("INSERT INTO tblCustomerMonthlySales(CustomerCode,SalesAmount) VALUES(?,?)");
								stmtInsertMonthlySalesAmount.bindString(1, trxHeaderDO.clientCode);
								stmtInsertMonthlySalesAmount.bindDouble(2, salesAmount);
								stmtInsertMonthlySalesAmount.executeInsert();
								stmtInsertMonthlySalesAmount.close();
							}
							
							if(cursor!=null && !cursor.isClosed())
								cursor.close();
							
							//===============Close Updating Monthly Sales amount=========================
						}
						//=====================newly added for return order
						if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED() && trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
						{
							
							float salesAmount=trxHeaderDO.totalAmount-trxHeaderDO.totalDiscountAmount;
							
							updateUserAchievedTargetforreturnorder(salesAmount, trxHeaderDO.userCode, objSqliteDB,trxHeaderDO.Division,0);
							
							//===============Start Updating Monthly Sales amount=========================

							/*
							 *  Need to change it to statement
							 */
							
						}
						
						if(objSqliteDB == null || !objSqliteDB.isOpen())
							objSqliteDB = DatabaseHelper.openDataBase();
						
						SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblTrxDetail (LineNo, TrxCode, ItemCode,OrgCode,TrxReasonCode, TrxDetailsNote, ItemType,BasePrice, UOM, " +
																						"QuantityLevel1, QuantityLevel2, QuantityLevel3, QuantityBU, RequestedBU, ApprovedBU, CollectedBU, FinalBU, " +
																						"PriceUsedLevel1, PriceUsedLevel2, PriceUsedLevel3, TotalDiscountPercentage,TotalDiscountAmount, " +
																						"CalculatedDiscountPercentage, CalculatedDiscountAmount, UserDiscountPercentage, UserDiscountAmount,ItemDescription, " +
																						"AffectedStock, Status, PromoID, PromoType, CreatedOn, TRXStatus, ExpiryDate, RelatedLineID, ItemGroupLevel5, " +
																						"TaxType, SuggestedBU, PushedOn, Reason, CancelledQuantity, InProcessQuantity, ShippedQuantity, MissedBU, " +
																						"BatchNumber,VAT,TotalVATAmount) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						
						SQLiteStatement stmtInsertItemImages = objSqliteDB.compileStatement("INSERT INTO tblOrderImage (OrderNo,ItemCode,LineNo,ImagePath,CapturedDate,Status) values(?,?,?,?,?,?)");
						
						for(TrxDetailsDO trxDetailsDO : trxHeaderDO.arrTrxDetailsDOs)
						{
							trxDetailsDO.lineNo = (int) ++lineNoCount;
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

//							stmtInsertOrder.bindDouble(22, trxDetailsDO.totalDiscountAmount);
							stmtInsertOrder.bindDouble(22, trxDetailsDO.promotionalDiscountAmount);

                            stmtInsertOrder.bindDouble(23, trxDetailsDO.calculatedDiscountPercentage);
							stmtInsertOrder.bindDouble(24, trxDetailsDO.calculatedDiscountAmount);

//								stmtInsertOrder.bindDouble(21, StringUtils.getDouble(trxHeaderDO.promotionalDiscount));
//
////							stmtInsertOrder.bindDouble(22, trxDetailsDO.totalDiscountAmount);
//							stmtInsertOrder.bindDouble(22,  (StringUtils.getDouble(trxHeaderDO.promotionalDiscount)*trxDetailsDO.basePrice)/100);
//
//							stmtInsertOrder.bindDouble(23,  StringUtils.getDouble(trxHeaderDO.statementDiscount));
//							stmtInsertOrder.bindDouble(24,  (StringUtils.getDouble(trxHeaderDO.statementDiscount)*trxDetailsDO.basePrice)/100);

							stmtInsertOrder.bindDouble(25, trxDetailsDO.userDiscountPercentage);
							stmtInsertOrder.bindDouble(26, trxDetailsDO.userDiscountAmount);
							stmtInsertOrder.bindString(27, trxDetailsDO.itemDescription);
							stmtInsertOrder.bindLong(28, trxDetailsDO.affectedStock);
//							stmtInsertOrder.bindLong(29, trxDetailsDO.status);
							stmtInsertOrder.bindLong(29, trxHeaderDO.status); //vinod

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
							
							try
							{
								if(trxDetailsDO.vecDamageImages!=null && trxDetailsDO.vecDamageImages.size()>0){
									for(DamageImageDO damageImageDO:trxDetailsDO.vecDamageImages){
										stmtInsertItemImages.bindString(1, trxHeaderDO.trxCode);
										stmtInsertItemImages.bindString(2, trxDetailsDO.itemCode);
										stmtInsertItemImages.bindLong(3, trxDetailsDO.lineNo);
										stmtInsertItemImages.bindString(4, damageImageDO.ImagePath);
										stmtInsertItemImages.bindString(5, damageImageDO.CapturedDate);
										stmtInsertItemImages.bindLong(6, damageImageDO.status);
										stmtInsertItemImages.execute();
									}
								}
								
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
						
						SQLiteStatement stmtOffers = objSqliteDB.compileStatement("INSERT INTO tblTrxPromotion (TrxCode, ItemCode, DiscountAmount, DiscountPercentage, OrgCode, PromotionID, " +
																				  "FactSheetCode, Status, CreatedOn, TrxStatus, PromotionType, TrxDetailsLineNo, ItemType, IsStructural) " +
																				  "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						 
						for(TrxPromotionDO trxPromotionDO : trxHeaderDO.arrPromotionDOs)
						{
							stmtOffers.bindString(1, trxHeaderDO.trxCode);
							stmtOffers.bindString(2, trxPromotionDO.itemCode);
							stmtOffers.bindDouble(3, trxPromotionDO.discountAmount);
							stmtOffers.bindDouble(4, trxPromotionDO.discountPercentage);
							stmtOffers.bindString(5, trxPromotionDO.orgCode);
							stmtOffers.bindLong(6, trxPromotionDO.promotionID);
							stmtOffers.bindString(7, trxPromotionDO.factSheetCode);
							stmtOffers.bindLong(8, trxPromotionDO.status);
							stmtOffers.bindString(9, trxHeaderDO.createdOn);
							stmtOffers.bindLong(10, trxHeaderDO.trxStatus);
							stmtOffers.bindLong(11, trxPromotionDO.promotionType);
							stmtOffers.bindLong(12, trxPromotionDO.trxDetailsLineNo);
							stmtOffers.bindString(13, trxPromotionDO.itemType);
							stmtOffers.bindLong(14, trxPromotionDO.isStructural);
							stmtOffers.executeInsert();
						}
						
						stmtInsert.executeInsert();
						stmtInsert.close();
						stmtInsertOrder.close();
						stmtOffers.close();
						stmtSelectLineNODetail.close();
					}
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return orderId;
		}
	}
	
	
	public void updateSavedOrder(ArrayList<TrxHeaderDO> arrayList)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null,orderCursor = null;
			String itemCodes="";
			String invoiceNumber = "";
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelect = objSqliteDB.compileStatement("SELECT COUNT(*) FROM tblTrxHeader WHERE TrxCode = ?");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("Update tblTrxHeader set TotalAmount=?, TotalDiscountAmount=?, TrxReasonCode=?, Status=?,TRXStatus=?, ApproveByCode=?, ApprovedDate=?,ClientSignature=?,SalesmanSignature=?,ReferenceCode=?,TrxCode=?,SplDisc=?,LPO=?,Narrotion=?,Reason=?,RateDiff=?,VAT=? where TrxCode=?");
				
				
//				SQLiteStatement stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblTrxDetail SET TrxReasonCode=?,TrxDetailsNote=?,ItemType=?,"
//						+ "QuantityLevel1=?, QuantityLevel2=?, QuantityLevel3=?, QuantityBU=?, RequestedBU=?,ApprovedBU=?,CollectedBU=?,FinalBU=?, " +
//						"PriceUsedLevel1=?, PriceUsedLevel2=?, PriceUsedLevel3=?, TotalDiscountPercentage=?,TotalDiscountAmount=?,AffectedStock=?,Status=?,TRXStatus=?,ExpiryDate=?,ItemGroupLevel5=?,"
//						+ "SuggestedBU=?,Reason=?,CancelledQuantity=?,InProcessQuantity=?,ShippedQuantity=?,MissedBU =?,UOM=?,TrxCode=? WHERE TrxCode = ? AND ItemCode=?");
				
				SQLiteStatement stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblTrxDetail SET TrxReasonCode=?,TrxDetailsNote=?,ItemType=?,"
						+ "QuantityLevel1=?, QuantityLevel2=?, QuantityLevel3=?, QuantityBU=?, RequestedBU=?,ApprovedBU=?,CollectedBU=?,FinalBU=?, " +
						"PriceUsedLevel1=?, PriceUsedLevel2=?, PriceUsedLevel3=?, TotalDiscountPercentage=?,TotalDiscountAmount=?,CalculatedDiscountPercentage=?,CalculatedDiscountAmount=?,AffectedStock=?,Status=?,TRXStatus=?,ExpiryDate=?,ItemGroupLevel5=?,"
						+ "SuggestedBU=?,Reason=?,CancelledQuantity=?,InProcessQuantity=?,ShippedQuantity=?,MissedBU =?,UOM=?,TrxCode=?,VAT=?,TotalVATAmount=? WHERE TrxCode = ? AND ItemCode=?");
				
				SQLiteStatement stmtSelectDetail = objSqliteDB.compileStatement("SELECT COUNT(*) FROM tblTrxDetail WHERE TrxCode = ? AND ItemCode=?");

				SQLiteStatement stmtSelectLineNODetail = objSqliteDB.compileStatement("SELECT IFNULL(max(LineNo),0) FROM tblTrxDetail where TrxCode=?");

				SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblTrxDetail (LineNo, TrxCode, ItemCode,OrgCode,TrxReasonCode, TrxDetailsNote, ItemType,BasePrice, UOM, " +
																				"QuantityLevel1, QuantityLevel2, QuantityLevel3, QuantityBU, RequestedBU, ApprovedBU, CollectedBU, FinalBU, " +
																				"PriceUsedLevel1, PriceUsedLevel2, PriceUsedLevel3, TotalDiscountPercentage,TotalDiscountAmount, " +
																				"CalculatedDiscountPercentage, CalculatedDiscountAmount, UserDiscountPercentage, UserDiscountAmount,ItemDescription, " +
																				"AffectedStock, Status, PromoID, PromoType, CreatedOn, TRXStatus, ExpiryDate, RelatedLineID, ItemGroupLevel5, " +
																				"TaxType, SuggestedBU, PushedOn, Reason, CancelledQuantity, InProcessQuantity, ShippedQuantity, MissedBU, " +
																				"BatchNumber,VAT,TotalVATAmount) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				
				for(TrxHeaderDO trxHeaderDO:arrayList){
					
					if(invoiceNumber == null || invoiceNumber.equalsIgnoreCase(""))
						invoiceNumber = trxHeaderDO.trxCode;
					
					stmtSelect.bindString(1, trxHeaderDO.trxCode);
					
					long count = stmtSelect.simpleQueryForLong();
					long preCount =0;
					String savedTrxCode="";
					long lineNOCount =0;
					if(count > 0 && trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && trxHeaderDO.trxStatus != TrxHeaderDO.get_TRX_STATUS_SAVED()){
						preCount =count;
						count=0;
						trxHeaderDO.referenceCode=trxHeaderDO.trxCode;
						savedTrxCode=trxHeaderDO.trxCode;
						trxHeaderDO.trxCode="";
					}
					else
					{
						stmtSelectLineNODetail.bindString(1, trxHeaderDO.trxCode);
						
						lineNOCount = stmtSelectLineNODetail.simpleQueryForLong();
						
						stmtUpdate.bindDouble(1, trxHeaderDO.totalAmount);
						stmtUpdate.bindDouble(2, trxHeaderDO.totalDiscountAmount);
						stmtUpdate.bindString(3, trxHeaderDO.trxReasonCode);
						stmtUpdate.bindLong(4, trxHeaderDO.status);
						stmtUpdate.bindLong(5, trxHeaderDO.trxStatus);
						stmtUpdate.bindString(6, trxHeaderDO.approveByCode+"");
						stmtUpdate.bindString(7, trxHeaderDO.approvedDate+"");
						stmtUpdate.bindString(8, trxHeaderDO.clientSignature+"");
						stmtUpdate.bindString(9, trxHeaderDO.salesmanSignature+"");
						stmtUpdate.bindString(10, trxHeaderDO.trxCode);
						stmtUpdate.bindString(11, invoiceNumber);
						stmtUpdate.bindString(12, ""+trxHeaderDO.specialDiscPercent);
						stmtUpdate.bindString(13, trxHeaderDO.LPONo);
						stmtUpdate.bindString(14, trxHeaderDO.PromotionReason);
						stmtUpdate.bindString(15, ""+trxHeaderDO.returnReason);
						stmtUpdate.bindString(16, ""+trxHeaderDO.rateDiff);
						stmtUpdate.bindString(17, ""+trxHeaderDO.totalVATAmount);
						stmtUpdate.bindString(18, trxHeaderDO.trxCode);
						
						stmtUpdate.execute();
					}
					
					if(count <= 0 )
					{
						trxHeaderDO.trxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
						trxHeaderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
						
						for(TrxDetailsDO trxDetailsDO:trxHeaderDO.arrTrxDetailsDOs)
							trxDetailsDO.trxCode = trxHeaderDO.trxCode;
						
						insertOrderDetails_Promo(trxHeaderDO);
						
						if(preCount>0){
							if(objSqliteDB==null || !objSqliteDB.isOpen())
								objSqliteDB = DatabaseHelper.openDataBase();
							SQLiteStatement stmtUpdateSavedOrder=objSqliteDB.compileStatement("Update tblTrxHeader set ReferenceCode=?, Status=? where TrxCode=?");
							stmtUpdateSavedOrder.bindString(1, trxHeaderDO.trxCode);
							stmtUpdateSavedOrder.bindLong(2, trxHeaderDO.status);
							stmtUpdateSavedOrder.bindString(3, savedTrxCode);
							long updatedRecords=stmtUpdateSavedOrder.executeUpdateDelete();
							LogUtils.debug("updatedRecords", ""+updatedRecords);
						}
					}
					else{
						
						String trxCodeQuery = "";
						boolean isSalesOrder = false;
						if(trxHeaderDO.trxStatus != TrxHeaderDO.get_TRX_STATUS_SAVED() &&
								(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() 
								|| trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER()
								|| trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY()
								|| trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER()))
						{
							/**No Need to take new order id**/
							/*trxCodeQuery = "SELECT id from tblOfflineData where  Type ='"+AppConstants.Order+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) Order By id Limit 1";
							orderCursor = objSqliteDB.rawQuery(trxCodeQuery, null);
							if(orderCursor != null && orderCursor.moveToFirst())
								invoiceNumber = orderCursor.getString(0);
							
							objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+invoiceNumber+"'");
							
							if(orderCursor!=null && !orderCursor.isClosed())
								orderCursor.close();*/
							
							if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED() && 
									trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER())
							{
								isSalesOrder = true;
								float salesAmount=trxHeaderDO.totalAmount-trxHeaderDO.totalDiscountAmount+trxHeaderDO.totalVATAmount;
/*
								float salesAmount=trxHeaderDO.totalAmount-trxHeaderDO.totalDiscountAmount;
*/

								updateUserAchievedTarget(salesAmount, trxHeaderDO.userCode, objSqliteDB,trxHeaderDO.Division,1);
								
								//===============Start Updating Monthly Sales amount=========================

								/*
								 *  Need to change it to statement
								 */
								String query ="SELECT *from tblCustomerMonthlySales where  CustomerCode ='"+trxHeaderDO.clientCode+"'";
								
								cursor = objSqliteDB.rawQuery(query, null);
								if(cursor.moveToFirst())
								{
									SQLiteStatement stmtUpdateMonthlySalesAmount = objSqliteDB.compileStatement("update tblCustomerMonthlySales set SalesAmount=Cast((Cast(SalesAmount as float)+?) as float) where CustomerCode=?");
									stmtUpdateMonthlySalesAmount.bindDouble(1, salesAmount);
									stmtUpdateMonthlySalesAmount.bindString(2, trxHeaderDO.clientCode);
									stmtUpdateMonthlySalesAmount.execute();
									stmtUpdateMonthlySalesAmount.close();
								}
								else
								{
									SQLiteStatement stmtInsertMonthlySalesAmount = objSqliteDB.compileStatement("INSERT INTO tblCustomerMonthlySales(CustomerCode,SalesAmount) VALUES(?,?)");
									stmtInsertMonthlySalesAmount.bindString(1, trxHeaderDO.clientCode);
									stmtInsertMonthlySalesAmount.bindDouble(2, salesAmount);
									stmtInsertMonthlySalesAmount.executeInsert();
									stmtInsertMonthlySalesAmount.close();
								}
								
								if(cursor!=null && !cursor.isClosed())
									cursor.close();
								
								//===============Close Updating Monthly Sales amount=========================
							}
						}
						
						itemCodes = "";
								
						for(TrxDetailsDO trxDetailsDO : trxHeaderDO.arrTrxDetailsDOs)
						{
							stmtSelectDetail.bindString(1, trxHeaderDO.trxCode);
							
							stmtSelectDetail.bindString(2, trxDetailsDO.itemCode);
							long countL = stmtSelectDetail.simpleQueryForLong();
							itemCodes = itemCodes+"'"+trxDetailsDO.itemCode+"'"+",";
							
							if(countL > 0 && isSalesOrder)
							{
								SQLiteStatement stmtDeleteOrder = objSqliteDB.compileStatement("DELETE FROM tblTrxDetail WHERE TrxCode = '"+trxHeaderDO.trxCode+"'");
								stmtDeleteOrder.executeUpdateDelete();
								countL = 0;
							}
							
							if(countL <= 0)
							{
								trxDetailsDO.lineNo = (int) ++lineNOCount;
								stmtInsertOrder.bindLong(1,trxDetailsDO.lineNo);
								stmtInsertOrder.bindString(2, invoiceNumber);
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
								stmtInsertOrder.bindDouble(22, trxDetailsDO.promotionalDiscountAmount);
								
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
								stmtUpdateOrder.bindDouble(16, trxDetailsDO.promotionalDiscountAmount);
								
								stmtUpdateOrder.bindDouble(17, trxDetailsDO.calculatedDiscountPercentage);
								stmtUpdateOrder.bindDouble(18, trxDetailsDO.calculatedDiscountAmount);
								
								stmtUpdateOrder.bindLong(19, trxDetailsDO.affectedStock);
								stmtUpdateOrder.bindLong(20, trxDetailsDO.status);
								stmtUpdateOrder.bindLong(21, trxDetailsDO.trxStatus);
								stmtUpdateOrder.bindString(22, trxDetailsDO.expiryDate);
								stmtUpdateOrder.bindString(23, trxDetailsDO.itemGroupLevel5);
								stmtUpdateOrder.bindLong(24, trxDetailsDO.suggestedBU);
								stmtUpdateOrder.bindString(25, trxDetailsDO.reason);
								stmtUpdateOrder.bindLong(26, trxDetailsDO.cancelledQuantity);
								stmtUpdateOrder.bindLong(27, trxDetailsDO.inProcessQuantity);
								stmtUpdateOrder.bindLong(28, trxDetailsDO.shippedQuantity);
								stmtUpdateOrder.bindLong(29, trxDetailsDO.missedBU);
								stmtUpdateOrder.bindString(30, trxDetailsDO.UOM);
								stmtUpdateOrder.bindString(31, invoiceNumber);
								stmtUpdateOrder.bindString(32,  trxDetailsDO.vatPercentage+"");
								stmtUpdateOrder.bindString(33,  trxDetailsDO.VATAmountNew+"");
								stmtUpdateOrder.bindString(34, trxHeaderDO.trxCode);
								stmtUpdateOrder.bindString(35, trxDetailsDO.itemCode);
								stmtUpdateOrder.execute();
							}
						}	
						
						try
						{
							if(itemCodes!=null && itemCodes.length()>0)
							{
								itemCodes = itemCodes.substring(0,itemCodes.length()-1);
								
								SQLiteStatement stmtUpdateDetail = objSqliteDB.compileStatement("UPDATE tblTrxDetail SET QuantityLevel1=?,QuantityBU=?,RequestedBU=? WHERE TrxCode=? AND ItemCode NOT IN ("+itemCodes+")");
								stmtUpdateDetail.bindDouble(1,0);
								stmtUpdateDetail.bindLong(2,0);
								stmtUpdateDetail.bindLong(3,0);
								
//								stmtUpdateDetail.bindString(4, trxHeaderDO.trxCode);
								stmtUpdateDetail.bindString(4, invoiceNumber);
								
								stmtUpdateDetail.execute();
								stmtUpdateDetail.close();
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				stmtSelect.close();
				stmtSelectDetail.close();
				stmtSelectLineNODetail.close();
				stmtUpdate.close();
				stmtUpdateOrder.close();
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
//			return invoiceNumber;
		}
	}
	
	
	public void cancelSavedOrder(String trxCode)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelect = objSqliteDB.compileStatement("SELECT COUNT(*) FROM tblTrxHeader WHERE TrxCode = ?");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("Update tblTrxHeader set Status=?,TRXStatus=? where TrxCode=?");
				
				
				stmtSelect.bindString(1, trxCode);
				long count = stmtSelect.simpleQueryForLong();
				
				if(count > 0 )
				{
					stmtUpdate.bindDouble(1, TrxHeaderDO.get_TRX_DATA_PENDING_STATUS());
					stmtUpdate.bindDouble(2, TrxHeaderDO.get_TRX_STATUS_CANCELLED());
					stmtUpdate.bindString(3, trxCode);
					stmtUpdate.execute();
							
				}
				
				stmtSelect.close();
				stmtUpdate.close();
				
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
	
	public void updateOrderStatus(String orderId)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				if(!TextUtils.isEmpty(orderId))
					objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+orderId+"'");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	
	}
	
	public String getFinalizedOrderID()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			String orderId = "";
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT id from tblOfflineData where  Type ='"+AppConstants.Order+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) Order By id Limit 1";
				cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
					orderId = cursor.getString(0);
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			
			return orderId;
		}
	
	}
	
	public String getVisitCodeByOrderId()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			String orderId = "";
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT id from tblOfflineData where  Type ='"+AppConstants.Order+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) Order By id Limit 1";
				cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
					orderId = cursor.getString(0);
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			
			return orderId;
		}
	
	}
																														//0-Return order rest all 1
	private void updateUserAchievedTarget(float invoiceAmount,String empNumber, SQLiteDatabase sqLiteDatabase, int division,int salestype){
		synchronized (MyApplication.MyLock) {
			try {
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();
				
				String updateQuery = "update tblUsers set AchievedTarget=Cast((Cast(AchievedTarget as float)+?) as float) where UserCode=?";
				if(division ==1)
					updateQuery = "update tblUsers set FoodAchivement=Cast((Cast(FoodAchivement as float)+?) as float) where UserCode=?";
				if(division ==2)
					updateQuery = "update tblUsers set AchiveThirdPartyTarget=Cast((Cast(AchiveThirdPartyTarget as float)+?) as float) where UserCode=?";




				SQLiteStatement stmtUpdate = sqLiteDatabase.compileStatement(updateQuery);
				stmtUpdate.bindDouble(1, invoiceAmount);
				stmtUpdate.bindString(2, empNumber);
				stmtUpdate.execute();
				stmtUpdate.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	//0-Return order rest all 1
		private void updateUserAchievedTargetforreturnorder(float invoiceAmount,String empNumber, SQLiteDatabase sqLiteDatabase, int division,int salestype){
		synchronized (MyApplication.MyLock) {
		try {
		if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
		sqLiteDatabase = DatabaseHelper.openDataBase();
		
		String updateQuery = "update tblUsers set AchievedTarget=Cast((Cast(AchievedTarget as float)-?) as float) where UserCode=?";
			if(division == 1)
				updateQuery = "update tblUsers set FoodAchivement=Cast((Cast(FoodAchivement as float)-?) as float) where UserCode=?";

			if(division == 2)
				updateQuery = "update tblUsers set  AchiveThirdPartyTarget=Cast((Cast( AchiveThirdPartyTarget as float)-?) as float) where UserCode=?";
		
		SQLiteStatement stmtUpdate = sqLiteDatabase.compileStatement(updateQuery);
		stmtUpdate.bindDouble(1, invoiceAmount);
		stmtUpdate.bindString(2, empNumber);
		stmtUpdate.execute();
		stmtUpdate.close();
		} catch (Exception e) {
		e.printStackTrace();
		}
		}
		}
	public String updateOrderDetails_Promo(OrderDO orderDO, Vector<ProductDO> vecOrderedProduct, ArrayList<ProductDO> vecOfferProducts, Preference preference)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			String orderId = "";
			Cursor cursor = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				if(orderDO.OrderId == null || orderDO.OrderId.length() <= 0)
				{
					String query = "SELECT id from tblOfflineData where  Type ='"+AppConstants.Order+"' AND status = 0 AND id NOT IN(SELECT OrderId FROM tblOrderHeader) Order By id Limit 1";
					cursor = objSqliteDB.rawQuery(query, null);
					if(cursor.moveToFirst())
					{
						orderId = cursor.getString(0);
					}
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				else
				{
					orderId = orderDO.OrderId;
				}
				
				if(orderId != null && !orderId.equalsIgnoreCase(""))
				{
					objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+orderId+"'");
						
					SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblOrderHeader(OrderId,AppOrderId,JourneyCode," +
												 "VisitCode,EmpNo,SiteNo,OrderDate,OrderType,SubType,CurrencyCode,PaymentType,TotalAmount," +
												 "TrxReasonCode,CustomerSignature,SalesmanSignature,PaymentCode,LPOCode,DeliveryDate," +
												 "StampDate,StampImage,TRXStatus,Status, SiteName, TotalDiscountAmount, VehicleCode, DeliveredBy) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					if(orderDO != null)
					{
						orderDO.OrderId = orderId;
						stmtInsert.bindString(1, orderDO.OrderId);
						stmtInsert.bindString(2, orderDO.strUUID);
						stmtInsert.bindString(3, orderDO.JourneyCode);
						stmtInsert.bindString(4, orderDO.VisitCode);
						stmtInsert.bindString(5, orderDO.empNo);
						stmtInsert.bindString(6, orderDO.CustomerSiteId);
						stmtInsert.bindString(7, orderDO.InvoiceDate);
						stmtInsert.bindString(8, orderDO.orderType);
						stmtInsert.bindString(9, orderDO.orderSubType);
						stmtInsert.bindString(10, orderDO.CurrencyCode);
						stmtInsert.bindString(11, orderDO.PaymentType);
						stmtInsert.bindString(12, ""+orderDO.TotalAmount);
						stmtInsert.bindString(13, orderDO.TrxReasonCode);
						stmtInsert.bindString(14, orderDO.strCustomerSign);
						stmtInsert.bindString(15, orderDO.strPresellerSign);
						stmtInsert.bindString(16, orderDO.PaymentCode);
						stmtInsert.bindString(17, orderDO.LPOCode);
						
						stmtInsert.bindString(18, orderDO.DeliveryDate);
						stmtInsert.bindString(19, orderDO.StampDate);
						stmtInsert.bindString(20, orderDO.StampImage);
						stmtInsert.bindString(21, orderDO.TRXStatus);
						stmtInsert.bindString(22, ""+orderDO.pushStatus);
						stmtInsert.bindString(23, ""+orderDO.strCustomerName);
						stmtInsert.bindString(24, ""+orderDO.Discount);
						stmtInsert.bindString(25, ""+orderDO.vehicleNo);
						stmtInsert.bindString(26, ""+orderDO.salesmanCode);
						
						SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblOrderDetail (LineNo, OrderNo, ItemCode," +
										" ItemType, ItemDescription, ItemPrice, UnitSellingPrice, UOM, Cases," +
								" Units, QuantityBU, DelivrdBU, PriceUsedLevel1, PriceUsedLevel2,TaxPercentage," +
								" TotalDiscountAmount, Status, PromoID, PromoType,TRXStatus, ExpiryDate, ReasonCode," +
								" RelatedLineID,TrxReasonCode, EmptyJarQT, EmptyJarDepositePrice, BatchNumber) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						
						int count = 0;
						
						for(ProductDO orderDetailsDO : vecOrderedProduct)
						{
							float discount ;
							try {
								discount = StringUtils.getFloat(orderDetailsDO.preUnits) * orderDetailsDO.discountAmount;
							} catch (Exception e) {
								discount = orderDetailsDO.discountAmount;
							}
							
							
							if(orderDetailsDO.LineNo == null || orderDetailsDO.LineNo.length() <= 0)
								orderDetailsDO.LineNo = ""+(vecOrderedProduct.size()+count++);
							
							stmtInsertOrder.bindString(1, ""+orderDetailsDO.LineNo);
							stmtInsertOrder.bindString(2, orderDO.OrderId);
							stmtInsertOrder.bindString(3, orderDetailsDO.SKU);
							
							if(orderDetailsDO.isPromotional)
								stmtInsertOrder.bindString(4, "F");
							else
								stmtInsertOrder.bindString(4, "O");
							
							
							
							
							stmtInsertOrder.bindString(5, orderDetailsDO.Description);
							stmtInsertOrder.bindString(6, ""+orderDetailsDO.itemPrice);
							stmtInsertOrder.bindString(7, ""+orderDetailsDO.unitSellingPrice);
							stmtInsertOrder.bindString(8, ""+orderDetailsDO.UOM);
							stmtInsertOrder.bindString(9, ""+orderDetailsDO.preCases);
							stmtInsertOrder.bindString(10, ""+orderDetailsDO.preUnits);
//							stmtInsertOrder.bindString(11, ""+orderDetailsDO.totalCases);
//							stmtInsertOrder.bindString(12, ""+orderDetailsDO.totalCases);
							
							stmtInsertOrder.bindString(11, ""+orderDetailsDO.totalUnits);
							stmtInsertOrder.bindString(12, ""+orderDetailsDO.totalUnits);
							
							stmtInsertOrder.bindString(13, ""+(orderDetailsDO.invoiceAmount+discount));
							stmtInsertOrder.bindString(14, ""+orderDetailsDO.invoiceAmount);
							stmtInsertOrder.bindString(15, "");
							stmtInsertOrder.bindString(16, ""+discount);
							
							
							stmtInsertOrder.bindString(17, ""+orderDO.pushStatus);
							stmtInsertOrder.bindString(18, ""+orderDetailsDO.promoCode);
							stmtInsertOrder.bindString(19, "");
							stmtInsertOrder.bindString(20, ""+orderDO.TRXStatus);
							
							stmtInsertOrder.bindString(21, ""+orderDetailsDO.strExpiryDate);
							stmtInsertOrder.bindString(22, ""+orderDetailsDO.reason);
							stmtInsertOrder.bindString(23, ""+orderDetailsDO.PromoLineNo);
							stmtInsertOrder.bindString(24, ""+orderDO.OrderId);
							stmtInsertOrder.bindString(25, ""+orderDetailsDO.recomUnits);
							stmtInsertOrder.bindString(26, ""+orderDetailsDO.depositPrice);
							stmtInsertOrder.bindString(27, ""+orderDetailsDO.BatchCode);
							stmtInsertOrder.executeInsert();
						}
						
						SQLiteStatement stmtOffers = objSqliteDB.compileStatement("INSERT INTO tblTrxPromotion (OrderId, ItemCode, DiscountAmount," +
								" DiscountPercentage, OrgCode, PromotionID, FactSheetCode, Status, CreatedOn," +
								" TrxStatus, PromotionType, TrxDetailsLineNo, ItemType, IsStructural)" +
								" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						
						for(ProductDO orderDetailsDO : vecOfferProducts)
						{
							stmtOffers.bindString(1, orderDO.OrderId);
							stmtOffers.bindString(2, orderDetailsDO.SKU);
							stmtOffers.bindString(3, ""+orderDetailsDO.discountAmount);
							stmtOffers.bindString(4, ""+orderDetailsDO.Discount);
							stmtOffers.bindString(5, "");
							
							stmtOffers.bindString(6, ""+orderDetailsDO.promotionId);
							stmtOffers.bindString(7, "");
							stmtOffers.bindString(8, ""+orderDO.pushStatus);
							stmtOffers.bindString(9, ""+CalendarUtils.getCurrentDateTime());
							stmtOffers.bindString(10, ""+orderDO.TRXStatus);
							stmtOffers.bindString(11, ""+orderDetailsDO.promotionType);
							
							stmtOffers.bindString(12, "1");
							stmtOffers.bindString(13, ""+orderDetailsDO.ItemType);
							stmtOffers.bindString(14, ""+orderDetailsDO.IsStructural);
							stmtOffers.executeInsert();
						}
						
						stmtInsert.executeInsert();
						stmtInsert.close();
						stmtInsertOrder.close();
						stmtOffers.close();
					}
				}
				return orderId;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return "";
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
	
	public String insertModifiedOrderDetails_Promo(OrderDO orderDO, Vector<ProductDO> vecOrderedProduct, ArrayList<ProductDO> vecOfferProducts, Preference preference)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			String orderId = orderDO.OrderId;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				if(orderId != null && !orderId.equalsIgnoreCase(""))
				{
					SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblOrderHeader(OrderId,AppOrderId,JourneyCode," +
												 "VisitCode,EmpNo,SiteNo,OrderDate,OrderType,SubType,CurrencyCode,PaymentType,TotalAmount," +
												 "TrxReasonCode,CustomerSignature,SalesmanSignature,PaymentCode,LPOCode,DeliveryDate," +
												 "StampDate,StampImage,TRXStatus,Status, SiteName,TotalDiscountAmount,VehicleCode,DeliveredBy) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					if(orderDO != null)
					{
						orderDO.OrderId = orderId;
						stmtInsert.bindString(1, orderDO.OrderId);
						stmtInsert.bindString(2, orderDO.strUUID);
						stmtInsert.bindString(3, orderDO.JourneyCode);
						stmtInsert.bindString(4, orderDO.VisitCode);
						stmtInsert.bindString(5, orderDO.empNo);
						stmtInsert.bindString(6, orderDO.CustomerSiteId);
						stmtInsert.bindString(7, orderDO.InvoiceDate);
						stmtInsert.bindString(8, orderDO.orderType);
						stmtInsert.bindString(9, orderDO.orderSubType);
						stmtInsert.bindString(10, orderDO.CurrencyCode);
						stmtInsert.bindString(11, orderDO.PaymentType);
						stmtInsert.bindString(12, ""+orderDO.TotalAmount);
						stmtInsert.bindString(13, orderDO.TrxReasonCode);
						stmtInsert.bindString(14, orderDO.strCustomerSign);
						stmtInsert.bindString(15, orderDO.strPresellerSign);
						stmtInsert.bindString(16, orderDO.PaymentCode);
						stmtInsert.bindString(17, orderDO.LPOCode);
						
						stmtInsert.bindString(18, orderDO.DeliveryDate);
						stmtInsert.bindString(19, orderDO.StampDate);
						stmtInsert.bindString(20, orderDO.StampImage);
						stmtInsert.bindString(21, orderDO.TRXStatus);
						stmtInsert.bindString(22, ""+orderDO.pushStatus);
						stmtInsert.bindString(23, ""+orderDO.strCustomerName);
						stmtInsert.bindString(24, ""+orderDO.Discount);
						stmtInsert.bindString(25, ""+orderDO.vehicleNo);
						stmtInsert.bindString(26, ""+orderDO.salesmanCode);
						
						SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblOrderDetail (LineNo, OrderNo, ItemCode," +
										" ItemType, ItemDescription, ItemPrice, UnitSellingPrice, UOM, Cases," +
								" Units, QuantityBU, DelivrdBU, PriceUsedLevel1, PriceUsedLevel2,TaxPercentage," +
								" TotalDiscountAmount, Status, PromoID, PromoType,TRXStatus, ExpiryDate, ReasonCode," +
								" RelatedLineID,TrxReasonCode, EmptyJarQT, EmptyJarDepositePrice, BatchNumber) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						
						int count = 0;
						
						for(ProductDO orderDetailsDO : vecOrderedProduct)
						{
							
							float discount ;
							try {
								discount = StringUtils.getFloat(orderDetailsDO.preUnits) * orderDetailsDO.discountAmount;
							} catch (Exception e) {
								discount = orderDetailsDO.discountAmount;
							}
							
							
							if(orderDetailsDO.LineNo == null || orderDetailsDO.LineNo.length() <= 0)
								orderDetailsDO.LineNo = ""+(vecOrderedProduct.size()+count++);
							
							stmtInsertOrder.bindString(1, ""+orderDetailsDO.LineNo);
							stmtInsertOrder.bindString(2, orderDO.OrderId);
							stmtInsertOrder.bindString(3, orderDetailsDO.SKU);
							
							if(orderDetailsDO.isPromotional)
								stmtInsertOrder.bindString(4, "F");
							else
								stmtInsertOrder.bindString(4, "O");
							
							stmtInsertOrder.bindString(5, orderDetailsDO.Description);
							stmtInsertOrder.bindString(6, ""+orderDetailsDO.itemPrice);
							stmtInsertOrder.bindString(7, ""+orderDetailsDO.unitSellingPrice);
							stmtInsertOrder.bindString(8, ""+orderDetailsDO.UOM);
							stmtInsertOrder.bindString(9, ""+orderDetailsDO.preCases);
							stmtInsertOrder.bindString(10, ""+orderDetailsDO.preUnits);
							stmtInsertOrder.bindString(11, ""+orderDetailsDO.preUnits);
							stmtInsertOrder.bindString(12, ""+orderDetailsDO.preUnits);
							
							stmtInsertOrder.bindString(13, ""+(orderDetailsDO.invoiceAmount+discount));
							stmtInsertOrder.bindString(14, ""+orderDetailsDO.invoiceAmount);
							stmtInsertOrder.bindString(15, "");
							
							stmtInsertOrder.bindString(16, ""+discount);
							
							
							stmtInsertOrder.bindString(17, ""+orderDO.pushStatus);
							stmtInsertOrder.bindString(18, ""+orderDetailsDO.promoCode);
							stmtInsertOrder.bindString(19, "");
							stmtInsertOrder.bindString(20, ""+orderDO.TRXStatus);
							
							stmtInsertOrder.bindString(21, ""+orderDetailsDO.strExpiryDate);
							stmtInsertOrder.bindString(22, ""+orderDetailsDO.reason);
							stmtInsertOrder.bindString(23, ""+orderDetailsDO.PromoLineNo);
							stmtInsertOrder.bindString(24, ""+orderDO.OrderId);
							stmtInsertOrder.bindString(25, ""+orderDetailsDO.recomUnits);
							stmtInsertOrder.bindString(26, ""+orderDetailsDO.depositPrice);
							stmtInsertOrder.bindString(27, ""+orderDetailsDO.BatchCode);
							stmtInsertOrder.executeInsert();
						}
						
						SQLiteStatement stmtOffers = objSqliteDB.compileStatement("INSERT INTO tblTrxPromotion (OrderId, ItemCode, DiscountAmount," +
								" DiscountPercentage, OrgCode, PromotionID, FactSheetCode, Status, CreatedOn," +
								" TrxStatus, PromotionType, TrxDetailsLineNo, ItemType, IsStructural)" +
								" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						
						for(ProductDO orderDetailsDO : vecOfferProducts)
						{
							stmtOffers.bindString(1, orderDO.OrderId);
							stmtOffers.bindString(2, orderDetailsDO.SKU);
							stmtOffers.bindString(3, ""+orderDetailsDO.discountAmount);
							stmtOffers.bindString(4, ""+orderDetailsDO.Discount);
							stmtOffers.bindString(5, "");
							
							stmtOffers.bindString(6, ""+orderDetailsDO.promotionId);
							stmtOffers.bindString(7, "");
							stmtOffers.bindString(8, ""+orderDO.pushStatus);
							stmtOffers.bindString(9, ""+CalendarUtils.getCurrentDateTime());
							stmtOffers.bindString(10, ""+orderDO.TRXStatus);
							stmtOffers.bindString(11, ""+orderDetailsDO.promotionType);
							
							stmtOffers.bindString(12, "1");
							stmtOffers.bindString(13, ""+orderDetailsDO.ItemType);
							stmtOffers.bindString(14, ""+orderDetailsDO.IsStructural);
							stmtOffers.executeInsert();
						}
						
						stmtInsert.executeInsert();
						stmtInsert.close();
						stmtInsertOrder.close();
						stmtOffers.close();
					}
				}
				return orderId;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	public String insertOrderDetails_PromoNoOffer(OrderDO orderDO, Vector<ProductDO> vecOrderedProduct, Preference preference)
	{
		
		
		if(vecOrderedProduct!=null && vecOrderedProduct.size()>0)
		{
			SQLiteDatabase objSqliteDB = null;
			String orderId = "";
			Cursor cursor = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				String query = "SELECT id from tblOfflineData where Type ='"+AppConstants.Order+"' AND status = 0 AND id NOT IN(SELECT OrderId FROM tblOrderHeader) Order By id Limit 1";
				cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					orderId = cursor.getString(0);
				}
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				if(orderId != null && !orderId.equalsIgnoreCase(""))
				{
					objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+orderId+"'");
					
					SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblOrderHeader(OrderId,AppOrderId,JourneyCode," +
							"VisitCode,EmpNo,SiteNo,OrderDate,OrderType,SubType,CurrencyCode,PaymentType,TotalAmount," +
							"TrxReasonCode,CustomerSignature,SalesmanSignature,PaymentCode,LPOCode,DeliveryDate," +
							"StampDate,StampImage,TRXStatus,Status,SiteName, TotalDiscountAmount, VehicleCode, DeliveredBy) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					if(orderDO != null)
					{
						orderDO.OrderId = orderId;
						stmtInsert.bindString(1, orderDO.OrderId);
						stmtInsert.bindString(2, orderDO.strUUID);
						stmtInsert.bindString(3, orderDO.JourneyCode);
						stmtInsert.bindString(4, orderDO.VisitCode);
						stmtInsert.bindString(5, orderDO.empNo);
						stmtInsert.bindString(6, orderDO.CustomerSiteId);
						stmtInsert.bindString(7, orderDO.InvoiceDate);
						stmtInsert.bindString(8, orderDO.orderType);
						stmtInsert.bindString(9, orderDO.orderSubType);
						stmtInsert.bindString(10, orderDO.CurrencyCode);
						stmtInsert.bindString(11, orderDO.PaymentType);
						stmtInsert.bindString(12, ""+orderDO.TotalAmount);
						stmtInsert.bindString(13, orderDO.TrxReasonCode);
						stmtInsert.bindString(14, orderDO.strCustomerSign);
						stmtInsert.bindString(15, orderDO.strPresellerSign);
						stmtInsert.bindString(16, orderDO.PaymentCode);
						stmtInsert.bindString(17, orderDO.LPOCode);
						
						stmtInsert.bindString(18, orderDO.DeliveryDate);
						stmtInsert.bindString(19, orderDO.StampDate);
						stmtInsert.bindString(20, orderDO.StampImage);
						stmtInsert.bindString(21, orderDO.TRXStatus);
						stmtInsert.bindString(22, ""+orderDO.pushStatus);
						stmtInsert.bindString(23, ""+orderDO.strCustomerName);
						stmtInsert.bindString(24, ""+orderDO.Discount);
						stmtInsert.bindString(25, ""+orderDO.vehicleNo);
						stmtInsert.bindString(26, ""+orderDO.salesmanCode);
						
						SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblOrderDetail (LineNo, OrderNo, ItemCode," +
								" ItemType, ItemDescription, ItemPrice, UnitSellingPrice, UOM, Cases," +
								" Units, QuantityBU, DelivrdBU, PriceUsedLevel1, PriceUsedLevel2,TaxPercentage," +
								" TotalDiscountAmount, Status, PromoID, PromoType,TRXStatus, ExpiryDate, ReasonCode," +
								" RelatedLineID,TrxReasonCode, EmptyJarQT, EmptyJarDepositePrice, BatchNumber) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						
						
						SQLiteStatement stmtInsertOrderImage = objSqliteDB.compileStatement("INSERT INTO tblOrderImage (OrderNo, ItemCode, LineNo," +
								" ImagePath, CapturedDate) VALUES(?,?,?,?,?)");
						
						int count = 1;
						for(ProductDO orderDetailsDO : vecOrderedProduct)
						{
							if(orderDetailsDO.LineNo == null || orderDetailsDO.LineNo.length() <= 0)
								orderDetailsDO.LineNo = ""+(vecOrderedProduct.size()+count++);
							
							stmtInsertOrder.bindString(1, ""+orderDetailsDO.LineNo);
							stmtInsertOrder.bindString(2, orderDO.OrderId);
							stmtInsertOrder.bindString(3, orderDetailsDO.SKU);
							
							if(orderDetailsDO.isPromotional)
								stmtInsertOrder.bindString(4, "F");
							else
								stmtInsertOrder.bindString(4, "O");
							
							stmtInsertOrder.bindString(5, orderDetailsDO.Description);
							stmtInsertOrder.bindString(6, ""+orderDetailsDO.itemPrice);
							stmtInsertOrder.bindString(7, ""+orderDetailsDO.unitSellingPrice);
							stmtInsertOrder.bindString(8, ""+orderDetailsDO.UOM);
							stmtInsertOrder.bindString(9, ""+orderDetailsDO.preCases);
							stmtInsertOrder.bindString(10, ""+orderDetailsDO.preUnits);
//						stmtInsertOrder.bindString(11, ""+orderDetailsDO.totalCases);
//						stmtInsertOrder.bindString(12, ""+orderDetailsDO.totalCases);
							
							stmtInsertOrder.bindString(11, ""+orderDetailsDO.totalUnits);
							stmtInsertOrder.bindString(12, ""+orderDetailsDO.totalUnits);
							
							stmtInsertOrder.bindString(13, ""+(orderDetailsDO.invoiceAmount + orderDetailsDO.discountAmount));
							stmtInsertOrder.bindString(14, ""+orderDetailsDO.invoiceAmount);
							stmtInsertOrder.bindString(15, "");
							stmtInsertOrder.bindString(16, ""+orderDetailsDO.discountAmount);
							
							stmtInsertOrder.bindString(17, ""+orderDO.pushStatus);
							stmtInsertOrder.bindString(18, ""+orderDetailsDO.promoCode);
							stmtInsertOrder.bindString(19, "");
							stmtInsertOrder.bindString(20, ""+orderDO.TRXStatus);
							
							stmtInsertOrder.bindString(21, ""+orderDetailsDO.strExpiryDate);
							stmtInsertOrder.bindString(22, ""+orderDetailsDO.reason);
							stmtInsertOrder.bindString(23, ""+orderDetailsDO.PromoLineNo);
							stmtInsertOrder.bindString(24, ""+orderDO.OrderId);
							stmtInsertOrder.bindString(26, ""+orderDetailsDO.depositPrice);
							stmtInsertOrder.bindString(27, ""+orderDetailsDO.BatchCode);
							stmtInsertOrder.executeInsert();
							
							if(orderDetailsDO.vecDamageImages != null && orderDetailsDO.vecDamageImages.size()>0)
							{
								int count1 = 1;
								for (String str : orderDetailsDO.vecDamageImages) {
									stmtInsertOrderImage.bindString(1, orderDO.OrderId);
									stmtInsertOrderImage.bindString(2, orderDetailsDO.SKU);
									stmtInsertOrderImage.bindString(3, (count1++)+"");
									stmtInsertOrderImage.bindString(4, str);
									stmtInsertOrderImage.bindString(5, CalendarUtils.getCurrentDateTime());
									stmtInsertOrderImage.executeInsert();
								}
							}
						}
						
						stmtInsert.executeInsert();
						stmtInsert.close();
						stmtInsertOrder.close();
					}
				}
				return orderId;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
		else
			return "";
	}
	
	public boolean updateAdvanceOrder(OrderDO orderDO)
	{
		SQLiteDatabase objSqliteDB = null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			
			SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblOrderHeader SET TRXStatus =? where OrderId = ?");
			if(orderDO != null)
			{
				stmtUpdate.bindString(1, "D");
				stmtUpdate.bindString(2, ""+orderDO.OrderId);
				stmtUpdate.execute();
			}
			stmtUpdate.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB != null)
				objSqliteDB.close();
		}
		return true;
	}
	
	public boolean updateOrderModifyInventoryStatus(Vector<ProductDO> vecProductDO, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				Cursor cursor 						= 	null;
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				String strUpdate 					= 	"Update tblVanStock set SellableQuantity=?, TotalQuantity=? where ItemCode = ?";
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				
				if(vecProductDO != null && vecProductDO.size() > 0)
				{
					for(ProductDO productDO : vecProductDO)
					{
						String strQuery 	= 	"SELECT SellableQuantity, TotalQuantity From tblVanStock where ItemCode = '"+productDO.SKU+"'";
						cursor 	= objSqliteDB.rawQuery(strQuery, null);
						if(cursor.moveToFirst())
						{
							float availQty 		= cursor.getFloat(0);
							float totalQty 		= cursor.getFloat(1);
							
							availQty 			= availQty + StringUtils.getFloat(productDO.ActpreUnits);
							
							if(availQty > 0 )
								stmtUpdateQty.bindString(1, ""+availQty);
							else
								stmtUpdateQty.bindString(1, "0");
							
							stmtUpdateQty.bindString(2, ""+totalQty);
							stmtUpdateQty.bindString(3, ""+productDO.SKU);
							stmtUpdateQty.execute();
						}
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
				}
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	public boolean updateInventoryStatusReturn(Vector<ProductDO> vecProductDO, String date, String userCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				Cursor cursor 						= 	null;
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtInsert 		= 	objSqliteDB.compileStatement("INSERT INTO tblVanStock (VanStockId, UserCode, ItemCode, SellableQuantity, ReturnedQuantity, TotalQuantity,BatchNumber,ExpiryDate) VALUES(?,?,?,?,?,?,?,?)");
				String strUpdate 				= 	"Update tblVanStock set SellableQuantity=?, TotalQuantity=?, ReturnedQuantity=?, NonSellableQuantity = ? where ItemCode = ?";
				
				
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				
				if(vecProductDO != null && vecProductDO.size() > 0)
				{
					for(ProductDO productDO : vecProductDO)
					{
						String strQuery 	= 	"SELECT SellableQuantity, TotalQuantity, ReturnedQuantity, NonSellableQuantity From tblVanStock where ItemCode = '"+productDO.SKU+"'";
						cursor 	= objSqliteDB.rawQuery(strQuery, null);
						if(cursor.moveToFirst())
						{
							float availQty 		      = cursor.getFloat(0);
							float totalQty 		      = cursor.getFloat(1);
							float returnQty			  = cursor.getFloat(2);
							float NonSellableQuantity = cursor.getFloat(3);
							if(productDO.reason.equalsIgnoreCase("Good Condition"))
							{
//								availQty 		= availQty + productDO.totalCases;
//								totalQty		= totalQty + productDO.totalCases;
								
								availQty 		= availQty + productDO.totalUnits;
								totalQty		= totalQty + productDO.totalUnits;
								returnQty		= returnQty + productDO.totalUnits;
							}
							else
							{
								availQty 		= availQty - productDO.totalUnits;
								if(!productDO.reason.equalsIgnoreCase(""))
									NonSellableQuantity		= NonSellableQuantity + productDO.totalUnits;
							}
							
							if(availQty > 0 )
								stmtUpdateQty.bindString(1, ""+availQty);
							else
								stmtUpdateQty.bindString(1, "0");
							
							stmtUpdateQty.bindString(2, ""+totalQty);
							stmtUpdateQty.bindString(3, ""+returnQty);
							stmtUpdateQty.bindString(4, ""+NonSellableQuantity);
							stmtUpdateQty.bindString(5, ""+productDO.SKU);
							stmtUpdateQty.execute();
						}
						else
						{
							if(productDO.reason.equalsIgnoreCase("Good Condition"))
							{
								stmtInsert.bindString(1, ""+(getVanStockId(objSqliteDB)+1));
								stmtInsert.bindString(2, ""+userCode);
								stmtInsert.bindString(3, ""+productDO.SKU);
								stmtInsert.bindString(4, ""+productDO.preUnits);
								stmtInsert.bindString(5, "0");
								stmtInsert.bindString(6, ""+productDO.preUnits);
								stmtInsert.bindString(7, ""+productDO.BatchCode);
								stmtInsert.bindString(8, ""+productDO.strExpiryDate);
								stmtInsert.executeInsert();
							}
						}
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
				}
				stmtUpdateQty.close();
				stmtInsert.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	private int getVanStockId(SQLiteDatabase objSqliteDB)
	{
		int id = 0;
		synchronized(MyApplication.MyLock) 
		{
			try 
			{
				Cursor cursor = null;
				if (objSqliteDB == null)
					objSqliteDB = DatabaseHelper.openDataBase();

				String strQuery = "SELECT MAX(VanStockId) FROM tblVanStock";
				cursor = objSqliteDB.rawQuery(strQuery, null);
				if (cursor.moveToFirst()) {
					id = cursor.getInt(0);
				}

				if (cursor != null && !cursor.isClosed())
					cursor.close();
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return id;
		
	}
	
	public boolean updateInventoryStatus(Vector<ProductDO> vecProductDO, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				Cursor cursor 						= 	null;
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				String strUpdate 					= 	"Update tblVanStock set SellableQuantity=?, TotalQuantity=? where ItemCode = ?";
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				
				if(vecProductDO != null && vecProductDO.size() > 0)
				{
					for(ProductDO productDO : vecProductDO)
					{
						String strQuery 	= 	"SELECT SellableQuantity, TotalQuantity From tblVanStock where ItemCode = '"+productDO.SKU+"'";
						cursor 	= objSqliteDB.rawQuery(strQuery, null);
						if(cursor.moveToFirst())
						{
							float availQty 		= cursor.getFloat(0);
							float totalQty 		= cursor.getFloat(1);
							if(productDO.reason.equalsIgnoreCase("Good Condition"))
							{
								availQty 		= availQty + StringUtils.getFloat(productDO.preUnits);
								totalQty		= totalQty + StringUtils.getFloat(productDO.preUnits);
							}
							else
							{
								availQty 		= availQty - StringUtils.getFloat(productDO.preUnits);
							}
							
							if(availQty > 0 )
								stmtUpdateQty.bindString(1, ""+availQty);
							else
								stmtUpdateQty.bindString(1, "0");
							
							stmtUpdateQty.bindString(2, ""+totalQty);
							stmtUpdateQty.bindString(3, ""+productDO.SKU);
							stmtUpdateQty.execute();
						}
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
				}
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	public boolean updateInventory_WhileOrder(ArrayList<TrxDetailsDO> vecDetailsDO, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				Cursor cursor 						= 	null;
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				String strUpdate 					= 	"Update tblVanStock set SellableQuantity=?, TotalQuantity=? where ItemCode = ?";
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				
				if(vecDetailsDO != null && vecDetailsDO.size() > 0)
				{
					for(TrxDetailsDO productDO : vecDetailsDO)
					{
						String strQuery 	= 	"SELECT SellableQuantity, TotalQuantity From tblVanStock where ItemCode = '"+productDO.itemCode+"'";
						cursor 	= objSqliteDB.rawQuery(strQuery, null);
						if(cursor.moveToFirst())
						{
							float availQty 		= cursor.getFloat(0);
							float totalQty 		= cursor.getFloat(1);
							UOMConversionFactorDO uomFactorDO = productDO.hashArrUoms.get(productDO.itemCode+/*objItem.UOM+*/"UNIT");
							availQty 			= availQty - productDO.quantityBU*uomFactorDO.eaConversion;
							
							if(availQty > 0 )
								stmtUpdateQty.bindString(1, ""+availQty);
							else
								stmtUpdateQty.bindString(1, "0");
							
							stmtUpdateQty.bindString(2, ""+totalQty);
							stmtUpdateQty.bindString(3, ""+productDO.itemCode);
							stmtUpdateQty.execute();
						}
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
				}
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	public boolean updateInventoryInStatus(Vector<ProductDO> vecProductDO, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				Cursor cursor 						= 	null;
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				String strUpdate 					= 	"Update tblVMSalesmanInventory  set availQty=? where ItemCode = ? AND  Date like ?";
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				
				if(vecProductDO != null && vecProductDO.size() > 0)
				{
					for(ProductDO productDO : vecProductDO)
					{
						String strQuery 	= 	"SELECT availQty From  tblVMSalesmanInventory where ItemCode = '"+productDO.SKU+"' AND Date LIKE '"+date+"%'";
						cursor 	= objSqliteDB.rawQuery(strQuery, null);
						if(cursor.moveToFirst())
						{
							float availQty 	= StringUtils.getFloat(cursor.getString(0));
							availQty 		= availQty + StringUtils.getFloat(productDO.preCases);
							
							if(availQty > 0 )
								stmtUpdateQty.bindString(1, ""+availQty);
							else
								stmtUpdateQty.bindString(1, "0");
							
							stmtUpdateQty.bindString(2, ""+productDO.SKU);
							stmtUpdateQty.bindString(3, date+"%");
							stmtUpdateQty.execute();
						}
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
				}
				
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	public boolean updateInventoryOutStatus(Vector<ProductDO> vecProductDO, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				Cursor cursor 						= 	null;
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				String strUpdate 					= 	"Update tblVMSalesmanInventory  set availQty=? where ItemCode = ? AND  Date like ?";
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				
				if(vecProductDO != null && vecProductDO.size() > 0)
				{
					for(ProductDO productDO : vecProductDO)
					{
						String strQuery 	= 	"SELECT availQty From  tblVMSalesmanInventory where ItemCode = '"+productDO.SKU+"' AND Date LIKE '"+date+"%'";
						cursor 	= objSqliteDB.rawQuery(strQuery, null);
						if(cursor.moveToFirst())
						{
							float availQty 	= StringUtils.getFloat(cursor.getString(0));
							availQty 		= availQty - StringUtils.getFloat(productDO.preCases);
							
							if(availQty > 0 )
								stmtUpdateQty.bindString(1, ""+availQty);
							else
								stmtUpdateQty.bindString(1, "0");
							
							stmtUpdateQty.bindString(2, ""+productDO.SKU);
							stmtUpdateQty.bindString(3, date+"%");
							stmtUpdateQty.execute();
						}
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
				}
				
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	public boolean InsertupdateInventoryStatus(Vector<ProductDO> vecProductDO, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				Cursor cursor 						= 	null;
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				String strUpdate 					= 	"Update tblVMSalesmanInventory  set availQty=? where ItemCode = ? AND  Date like ?";
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				
				if(vecProductDO != null && vecProductDO.size() > 0)
				{
					for(ProductDO productDO : vecProductDO)
					{
						String strQuery 	= 	"SELECT availQty From  tblVMSalesmanInventory where ItemCode = '"+productDO.SKU+"' AND Date LIKE '"+date+"%'";
						cursor 	= objSqliteDB.rawQuery(strQuery, null);
						if(cursor.moveToFirst())
						{
							float availQty 	= StringUtils.getFloat(cursor.getString(0));
							availQty 		= availQty + StringUtils.getFloat(productDO.preCases);
							
							if(availQty > 0 )
								stmtUpdateQty.bindString(1, ""+availQty);
							else
								stmtUpdateQty.bindString(1, "0");
							
							stmtUpdateQty.bindString(2, ""+productDO.SKU);
							stmtUpdateQty.bindString(3, date+"%");
							stmtUpdateQty.execute();
						}
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
				}
				
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	//Method to get the orders generated current day for the current customer  
	public Vector<OrderDO> getCustomerOrderList(String customerSiteID, String currentDate) 
	{
		synchronized(MyApplication.MyLock) 
		{
			OrderDO objCustomerOrder;
			Vector<OrderDO> vecOrderList  =  new Vector<OrderDO>();
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				
				String strQuery = "SELECT OrderId,AppOrderId,EmpNo,SiteNo,OrderDate,OrderType,SubType,TrxReasonCode," +
								  "DeliveryDate,TRXStatus,Status FROM tblOrderHeader where SiteNo ='"+customerSiteID+"' " +
								  "and (OrderDate like '"+currentDate+"%' OR DeliveryDate LIKE '"+CalendarUtils.getOrderPostDate()+"%')";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						objCustomerOrder 				= 	new OrderDO();
						objCustomerOrder.OrderId		=	cursor.getString(0);
						objCustomerOrder.strUUID 		=	cursor.getString(1);
						objCustomerOrder.empNo			=	cursor.getString(2);
						objCustomerOrder.CustomerSiteId =	cursor.getString(3);
						objCustomerOrder.InvoiceDate 	=	cursor.getString(4);
						objCustomerOrder.orderType		=	cursor.getString(5);
						
						objCustomerOrder.orderSubType 	=	cursor.getString(6);
						
						objCustomerOrder.TrxReasonCode 	=	cursor.getString(7);
						
						objCustomerOrder.DeliveryDate 	=	cursor.getString(8);
						objCustomerOrder.TRXStatus		=	cursor.getString(9);
						objCustomerOrder.pushStatus		=	cursor.getInt(10);
						vecOrderList.add(objCustomerOrder);
					}
					while(cursor.moveToNext());
				}
				

				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
//				strQuery = "SELECT ReceiptId,AppPaymentId,EmpNo,SiteID,PaymentDate,PaymentType FROM tblPaymentHeader where SiteID =='"+customerSiteID+"'" +
//						   " and PaymentDate like '"+CalendarUtils.getOrderPostDate()+"%'";
//				
//				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
//				
//				if(cursor.moveToFirst())
//				{
//					do
//					{
//						objCustomerOrder 				= 	new OrderDO();
//						objCustomerOrder.OrderId		=	cursor.getString(0);
//						objCustomerOrder.strUUID 		=	cursor.getString(1);
//						objCustomerOrder.empNo			=	cursor.getString(2);
//						objCustomerOrder.CustomerSiteId =	cursor.getString(3);
//						objCustomerOrder.InvoiceDate 	=	cursor.getString(4);
//						objCustomerOrder.orderType		=	cursor.getString(5);
//						objCustomerOrder.orderSubType 	=	AppConstants.Receipt;
//						vecOrderList.add(objCustomerOrder);
//					}
//					while(cursor.moveToNext());
//				}

//				if(cursor!=null && !cursor.isClosed())
//					cursor.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return vecOrderList;
		}
	}
	
	
	//Method to get the orders generated current day for the current customer  
		public Vector<OrderDO> getCustomerReturnOrderList(String customerSiteID, String currentDate) 
		{
			synchronized(MyApplication.MyLock) 
			{
				OrderDO objCustomerOrder;
				Vector<OrderDO> vecOrderList  =  new Vector<OrderDO>();
				SQLiteDatabase sqLiteDatabase =  null;
				Cursor cursor = null;
				
				try 
				{
					sqLiteDatabase =  DatabaseHelper.openDataBase();
					
					String strQuery = "SELECT OrderId,AppOrderId,EmpNo,SiteNo,OrderDate,OrderType,SubType,TrxReasonCode," +
									  "DeliveryDate,TRXStatus,Status,ReturnOrderStatus FROM tblOrderHeader where SiteNo ='"+customerSiteID+"' " +
									  "and (OrderDate like '"+currentDate+"%' OR DeliveryDate LIKE '"+CalendarUtils.getOrderPostDate()+"%') and OrderType='HH Return Order'";
					cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
					
					if(cursor.moveToFirst())
					{
						do
						{
							objCustomerOrder 				= 	new OrderDO();
							objCustomerOrder.OrderId		=	cursor.getString(0);
							objCustomerOrder.strUUID 		=	cursor.getString(1);
							objCustomerOrder.empNo			=	cursor.getString(2);
							objCustomerOrder.CustomerSiteId =	cursor.getString(3);
							objCustomerOrder.InvoiceDate 	=	cursor.getString(4);
							objCustomerOrder.orderType		=	cursor.getString(5);
							
							objCustomerOrder.orderSubType 	=	cursor.getString(6);
							
							objCustomerOrder.TrxReasonCode 	=	cursor.getString(7);
							
							objCustomerOrder.DeliveryDate 	=	cursor.getString(8);
							objCustomerOrder.TRXStatus		=	cursor.getString(9);
							objCustomerOrder.pushStatus	=	cursor.getInt(10);
							/*
							 *  status - 0 : pending
							 *  status - 1 : approved 
							 */
							objCustomerOrder.returnOrderStatus  =	cursor.getInt(11);
							
							objCustomerOrder.vecProductDO 		= getProductsOfOrderDetaails(sqLiteDatabase, objCustomerOrder.OrderId);
							vecOrderList.add(objCustomerOrder);
						}
						while(cursor.moveToNext());
					}
					

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
					if(sqLiteDatabase != null)
						sqLiteDatabase.close();
				}
				
				return vecOrderList;
			}
		}

	
	//Method to get the orders generated current day for the current customer  
	public Vector<OrderDO> getCustomerAdvanceOrderList(String customerSiteID, String currentDate, String orderType) 
	{
		synchronized(MyApplication.MyLock) 
		{
			OrderDO objCustomerOrder;
			Vector<OrderDO> vecOrderList  =  new Vector<OrderDO>();
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				
				String strQuery = "SELECT * FROM tblOrderHeader where SiteNo ='"+customerSiteID+"' and DeliveryDate like '"+currentDate+"%' AND SubType='"+orderType+"'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						objCustomerOrder 				= 	new OrderDO();
						objCustomerOrder.OrderId		=	cursor.getString(0);
						objCustomerOrder.PresellerId 	=	cursor.getString(1);
						objCustomerOrder.CustomerSiteId =	cursor.getString(2);
						objCustomerOrder.Discount		=	cursor.getInt(3);
						objCustomerOrder.TotalAmount 	=	cursor.getLong(4);
						objCustomerOrder.DeliveryStatus =	cursor.getString(5);
						objCustomerOrder.DeliveryDate	=	cursor.getString(6);
						objCustomerOrder.BalanceAmount 	=	cursor.getLong(7);
						objCustomerOrder.InvoiceNumber 	=	cursor.getString(8);
						objCustomerOrder.InvoiceDate	=	cursor.getString(9);
						objCustomerOrder.DeliveryAgentId=	cursor.getString(10);
						objCustomerOrder.orderType		=	cursor.getString(11);
						objCustomerOrder.strCustomerSign=	cursor.getString(12);
						objCustomerOrder.strPresellerSign=	cursor.getString(13);
						objCustomerOrder.strUUID 		=	cursor.getString(14);
						vecOrderList.add(objCustomerOrder);
					}
					while(cursor.moveToNext());
					
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return vecOrderList;
		}
	}


	public int getAdvenceOrderBySiteId(String orderType, String siteId, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			int status = 0;
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				
				String strQuery = "SELECT COUNT (*) FROM tblOrderHeader where SubType='"+orderType+"' AND SiteNo='"+siteId+"' AND DeliveryDate LIKE '"+date+"%'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return status;
		}
	}
	/**
	 * Get All user calls for current day.
	 */
	public UserProductivityDO getAllUserCallsDay()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			UserProductivityDO userProductivityDO = new UserProductivityDO();
			int status = 0;
			String strQuery = "";
			try
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				/**
				 * Productive Calls
				 */
				strQuery = "SELECT COUNT(DISTINCT ClientCode) from tblCustomerVisit WHERE IsProductiveCall=1 AND Date LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				userProductivityDO.productiveCalls = status;
				/**
				 * Actual Calls Scheduled
				 */
				strQuery = "SELECT COUNT(DISTINCT ClientCode) FROM tblDailyJourneyPlan WHERE ClientCode IN(SELECT DISTINCT ClientCode FROM tblCustomerVisit WHERE  Date like '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%')";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				userProductivityDO.actualCallsScheduled = status;
				/**
				 * Actual Calls UnScheduled
				 */
				strQuery = "SELECT COUNT(DISTINCT ClientCode) from tblDailyJourneyPlan WHERE ClientCode NOT IN(SELECT DISTINCT ClientCode FROM tblCustomerVisit WHERE  Date like '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%') AND JourneyDate like  '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				userProductivityDO.actualCallsUnScheduled = status;
				/**
				 * Zero Sales Outlet
				 */
				strQuery = "SELECT COUNT(DISTINCT CustomerSiteId) FROM tblSkipReasons WHERE SkipDate like '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				userProductivityDO.zeroSalesOutlet = status;
				/**
				 * Actual Calls
				 */
				strQuery =	"SELECT COUNT(DISTINCT ClientCode) FROM tblCustomerVisit WHERE Date LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				userProductivityDO.actualCalls = status;
				/**
				 * Scheduled Calls
				 */
				strQuery = "SELECT COUNT(*) FROM tblDailyJourneyPlan WHERE IsDeleted !='true' " +
						"AND JourneyDate like '%"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				userProductivityDO.scheduledCalls = status;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(sqLiteDatabase != null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return userProductivityDO;
		}
	}
	
	/**
	 * Get all user call for current Month
	 */
	public UserProductivityDO getAllUserCallsMonth()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			UserProductivityDO userProductivityDO = new UserProductivityDO();
			int status = 0;
			String strQuery = "";
			try
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				/**
				 * Productive Calls
				 */
				strQuery = "SELECT COUNT(DISTINCT ClientCode) from tblCustomerVisit WHERE IsProductiveCall=1 AND Date LIKE '"+CalendarUtils.getCurrentMonth()+"%'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				userProductivityDO.productiveCalls = status;
				/**
				 * Actual Calls Scheduled
				 */
				strQuery = "SELECT COUNT(DISTINCT ClientCode) FROM tblDailyJourneyPlan WHERE ClientCode IN(SELECT DISTINCT ClientCode FROM tblCustomerVisit WHERE  Date like '"+CalendarUtils.getCurrentMonth()+"%')";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				userProductivityDO.actualCallsScheduled = status;
				/**
				 * Actual Calls UnScheduled
				 */
				strQuery = "SELECT COUNT(DISTINCT ClientCode) from tblDailyJourneyPlan WHERE ClientCode NOT IN(SELECT DISTINCT ClientCode FROM tblCustomerVisit WHERE  Date like '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%')";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				userProductivityDO.actualCallsUnScheduled = status;
				/**
				 * Zero Sales Outlet
				 */
				strQuery = "SELECT COUNT(DISTINCT CustomerSiteId) FROM tblSkipReasons WHERE SkipDate like '"+CalendarUtils.getCurrentMonth()+"%'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				userProductivityDO.zeroSalesOutlet = status;
				/**
				 * Actual Calls
				 */
				strQuery =	"SELECT COUNT(DISTINCT ClientCode) FROM tblCustomerVisit WHERE Date LIKE '"+CalendarUtils.getCurrentMonth()+"%'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				userProductivityDO.actualCalls = status;
				/**
				 * Scheduled Calls
				 */
				strQuery = "SELECT COUNT(*) FROM tblDailyJourneyPlan WHERE IsDeleted !='true' " +
						"AND JourneyDate like '%"+CalendarUtils.getCurrentMonth()+"%'";
				
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				userProductivityDO.scheduledCalls = status;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(sqLiteDatabase != null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return userProductivityDO;
		}
	}
	
	public int getProductiveOrders()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			int status = 0;
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				String strQuery = "SELECT COUNT(DISTINCT ClientCode) from tblCustomerVisit WHERE IsProductiveCall=1 AND Date LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return status;
		}
	}
	public int getProductiveOrdersForMonth()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			int status = 0;
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				String strQuery = "SELECT COUNT(DISTINCT ClientCode) from tblCustomerVisit WHERE IsProductiveCall=1 AND Date LIKE '"+CalendarUtils.getCurrentMonth()+"%'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return status;
		}
	}
	public int getActualCalls()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			int status = 0;
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				String strQuery = "SELECT COUNT(DISTINCT ClientCode) FROM tblDailyJourneyPlan WHERE ClientCode IN(SELECT DISTINCT ClientCode FROM tblCustomerVisit WHERE  Date like '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%')";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return status;
		}
	}
	
	public int getActualCallsforMonth()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			int status = 0;
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				String strQuery = "SELECT COUNT(DISTINCT ClientCode) FROM tblDailyJourneyPlan WHERE ClientCode IN(SELECT DISTINCT ClientCode FROM tblCustomerVisit WHERE  Date like '"+CalendarUtils.getCurrentMonth()+"%')";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return status;
		}
	}
	
	public int getActualCallsUnscheduled()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			int status = 0;
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				String strQuery = "SELECT COUNT(DISTINCT ClientCode) from tblDailyJourneyPlan WHERE ClientCode NOT IN(SELECT DISTINCT ClientCode FROM tblCustomerVisit WHERE  Date like '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%') AND JourneyDate like  '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return status;
		}
	}
	
	public int getActualCallsUnscheduledForMonth()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			int status = 0;
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				String strQuery = "SELECT COUNT(DISTINCT ClientCode) from tblDailyJourneyPlan WHERE ClientCode NOT IN(SELECT DISTINCT ClientCode FROM tblCustomerVisit WHERE  Date like '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%')";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return status;
		}
	}
	
	public int getZeroSales()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			int status = 0;
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				String strQuery = "SELECT COUNT(DISTINCT CustomerSiteId) FROM tblSkipReasons WHERE SkipDate like '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return status;
		}
	}
	
	public int getZeroSalesforMonth()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			int status = 0;
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				String strQuery = "SELECT COUNT(DISTINCT CustomerSiteId) FROM tblSkipReasons WHERE SkipDate like '"+CalendarUtils.getCurrentMonth()+"%'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return status;
		}
	}
	
	public int getTodayCustomerVisitCount()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase 	= 	null;
			Cursor cursor = null;
			int visitedCustomerCount = 0;
			try 
			{
				sqLiteDatabase 		= 	DatabaseHelper.openDataBase();
				String query		=	"SELECT COUNT(DISTINCT ClientCode) FROM tblCustomerVisit WHERE Date LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				cursor 				= 	sqLiteDatabase .rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					visitedCustomerCount = cursor.getInt(0);
				}
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
			return visitedCustomerCount;
		}
	}
	
	public int getTodayCustomerVisitCountForMonth()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase 	= 	null;
			Cursor cursor = null;
			int visitedCustomerCount = 0;
			try 
			{
				sqLiteDatabase 		= 	DatabaseHelper.openDataBase();
				String query		=	"SELECT COUNT(DISTINCT ClientCode) FROM tblCustomerVisit WHERE Date LIKE '"+CalendarUtils.getCurrentMonth()+"%'";
				cursor 				= 	sqLiteDatabase .rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					visitedCustomerCount = cursor.getInt(0);
				}
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
			return visitedCustomerCount;
		}
	}
	
	public boolean insertAdvanceOrderDetails(Vector<CustomerSite_NewDO> vecSalesManCustomerDetailDOs)
	{
		SQLiteDatabase objSqliteDB = null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			
			SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblOrderHeader(OrderId,AppOrderId,JourneyCode," +
					 "VisitCode,EmpNo,SiteNo,OrderDate,OrderType,SubType,CurrencyCode,PaymentType,TotalAmount," +
					 "TrxReasonCode,CustomerSignature,SalesmanSignature,PaymentCode,LPOCode,DeliveryDate," +
					 "StampDate,StampImage,TRXStatus,Status) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblOrderDetail (LineNo, OrderNo, ItemCode," +
					" ItemType, ItemDescription, ItemPrice, UnitSellingPrice, UOM, Cases," +
			" Units, QuantityBU, DelivrdBU, PriceUsedLevel1, PriceUsedLevel2,TaxPercentage," +
			" TotalDiscountAmount, Status, PromoID, PromoType,TRXStatus, ExpiryDate, ReasonCode," +
			" RelatedLineID,TrxReasonCode,BatchNumber) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			SQLiteStatement stmtSelOrder = objSqliteDB.compileStatement("SELECT COUNT(*) FROM tblOrderHeader WHERE OrderId=?");
			
			
			if(vecSalesManCustomerDetailDOs != null && vecSalesManCustomerDetailDOs.size() > 0)
			{
				for(CustomerSite_NewDO coCustomerSite_NewDO : vecSalesManCustomerDetailDOs)
				{
					if(coCustomerSite_NewDO != null && coCustomerSite_NewDO.vecOrderDO != null && coCustomerSite_NewDO.vecOrderDO.size() > 0)
					{
						for(OrderDO orderDO : coCustomerSite_NewDO.vecOrderDO)
						{
							stmtSelOrder.bindString(1, orderDO.OrderId);
							long count = stmtSelOrder.simpleQueryForLong();
							if(count <= 0)
								insertAdvanceOrderDetails(objSqliteDB, orderDO, stmtInsert, stmtInsertOrder);
						}
					}
				}
			}
			
			stmtInsert.close();
			stmtInsertOrder.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB != null)
				objSqliteDB.close();
		}
		return true;
	}
	
	public void insertAdvanceOrderDetails(SQLiteDatabase objSqliteDB, OrderDO orderDO, SQLiteStatement stmtInsert, SQLiteStatement stmtInsertOrder)
	{
		try 
		{
			if(objSqliteDB == null || !objSqliteDB.isOpen())
				objSqliteDB = DatabaseHelper.openDataBase();
			
			if(orderDO != null && stmtInsert != null && stmtInsertOrder != null)
			{
				stmtInsert.bindString(1, orderDO.OrderId);
				stmtInsert.bindString(2, orderDO.strUUID);
				stmtInsert.bindString(3, orderDO.JourneyCode);
				stmtInsert.bindString(4, orderDO.VisitCode);
				stmtInsert.bindString(5, orderDO.empNo);
				stmtInsert.bindString(6, orderDO.CustomerSiteId);
				stmtInsert.bindString(7, orderDO.InvoiceDate);
				stmtInsert.bindString(8, orderDO.orderType);
				stmtInsert.bindString(9, orderDO.orderSubType);
				stmtInsert.bindString(10, orderDO.CurrencyCode);
				stmtInsert.bindString(11, orderDO.PaymentType);
				stmtInsert.bindString(12, ""+orderDO.TotalAmount);
				stmtInsert.bindString(13, orderDO.TrxReasonCode);
				stmtInsert.bindString(14, orderDO.strCustomerSign);
				stmtInsert.bindString(15, orderDO.strPresellerSign);
				stmtInsert.bindString(16, orderDO.PaymentCode);
				stmtInsert.bindString(17, orderDO.LPOCode);
				
				stmtInsert.bindString(18, orderDO.DeliveryDate);
				stmtInsert.bindString(19, orderDO.StampDate);
				stmtInsert.bindString(20, orderDO.StampImage);
				stmtInsert.bindString(21, orderDO.TRXStatus);
				stmtInsert.bindString(22, ""+orderDO.pushStatus);
				
				
				for(ProductDO orderDetailsDO : orderDO.vecProductDO)
				{
					stmtInsertOrder.bindString(1, orderDetailsDO.LineNo);
					stmtInsertOrder.bindString(2, orderDO.OrderId);
					stmtInsertOrder.bindString(3, orderDetailsDO.SKU);
					stmtInsertOrder.bindString(4, orderDetailsDO.ItemType);
					stmtInsertOrder.bindString(5, orderDetailsDO.Description);
					stmtInsertOrder.bindString(6, ""+orderDetailsDO.itemPrice);
					stmtInsertOrder.bindString(7, ""+orderDetailsDO.unitSellingPrice);
					stmtInsertOrder.bindString(8, ""+orderDetailsDO.UOM);
					stmtInsertOrder.bindString(9, ""+orderDetailsDO.preCases);
					stmtInsertOrder.bindString(10, ""+orderDetailsDO.preUnits);
					stmtInsertOrder.bindString(11, ""+orderDetailsDO.totalCases);
					stmtInsertOrder.bindString(12, ""+orderDetailsDO.totalCases);
					
					stmtInsertOrder.bindString(13, ""+orderDetailsDO.totalPrice);
					stmtInsertOrder.bindString(14, ""+orderDetailsDO.invoiceAmount);
					stmtInsertOrder.bindString(15, "");
					stmtInsertOrder.bindString(16, ""+orderDetailsDO.discountAmount);
					
					stmtInsertOrder.bindString(17, ""+orderDO.pushStatus);
					stmtInsertOrder.bindString(18, "");
					stmtInsertOrder.bindString(19, "");
					stmtInsertOrder.bindString(20, ""+orderDO.TRXStatus);
					
					stmtInsertOrder.bindString(21, ""+orderDetailsDO.strExpiryDate);
					stmtInsertOrder.bindString(22, ""+orderDetailsDO.reason);
					stmtInsertOrder.bindString(23, ""+orderDetailsDO.RelatedLineId);
					stmtInsertOrder.bindString(24, ""+orderDO.OrderId);
					stmtInsertOrder.bindString(25, ""+orderDetailsDO.BatchCode);
					stmtInsertOrder.executeInsert();
				}
				stmtInsert.executeInsert();
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public boolean updateStampImage(OrderDO orderDO)
	{
		SQLiteDatabase objSqliteDB = null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			SQLiteStatement stmtInsert = objSqliteDB.compileStatement("UPDATE tblOrderHeader SET StampImage=? WHERE OrderId = ?");
			if(orderDO != null)
			{
				stmtInsert.bindString(1, orderDO.StampImage);
				stmtInsert.bindString(2, ""+orderDO.OrderId);
				stmtInsert.execute();
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB != null)
				objSqliteDB.close();
		}
		
		return true;
	}
	
	public boolean updateInventoryInStatus(Vector<ProductDO> vecMainProducts, String empNo, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = 	null;
			Cursor cursor 			   = 	null;
			try 
			{
				objSqliteDB 					= 	DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec 		= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblCheckINDemandStockInventory WHERE Date LIKE '%"+date+"%' AND EmpNo=? AND ItemCode = ?");
				String strUpdate 					= 	"Update tblCheckINDemandStockInventory set PrimaryQuantity=?, SecondaryQuantity=?, advcCases=?,advcUnits=?, pushStatus = 0 WHERE Date LIKE '%"+date+"%' AND EmpNo=? AND ItemCode = ?";
				
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				SQLiteStatement stmtInsert 			= 	objSqliteDB.compileStatement("INSERT INTO tblCheckINDemandStockInventory " +
						                                                             "(Date,EmpNo,ItemCode,PrimaryQuantity,SecondaryQuantity,pushStatus,AdvcCases, AdvcUnits) VALUES(?,?,?,?,?,?,?,?)");
				
				if(vecMainProducts != null && vecMainProducts.size() > 0)
				{
					for(ProductDO productDO : vecMainProducts)
					{
						stmtSelectRec.bindString(1, empNo);
						stmtSelectRec.bindString(2, productDO.SKU);
						long count = stmtSelectRec.simpleQueryForLong();
						
						if(count > 0)
						{
							String strQuery 	= 	"SELECT PrimaryQuantity, SecondaryQuantity, advcCases, advcUnits From tblCheckINDemandStockInventory WHERE Date LIKE '%"+date+"%' AND EmpNo='"+empNo+"' AND ItemCode = '"+productDO.SKU+"'";
							cursor 	= objSqliteDB.rawQuery(strQuery, null);
							if(cursor.moveToFirst())
							{
								float primaryCase 	= cursor.getFloat(0);
								float primaryUnint 	= cursor.getFloat(1);
								float availCases 	= cursor.getFloat(2);
								float availUnits 	= cursor.getFloat(3);
								
								primaryCase			= primaryCase + StringUtils.getFloat(productDO.preCases);
								primaryUnint		= primaryUnint + StringUtils.getFloat(productDO.preCases);
								availCases			= availCases + StringUtils.getFloat(productDO.preCases);
								availUnits			= availUnits + StringUtils.getFloat(productDO.preUnits);
								
								stmtUpdateQty.bindString(1, ""+primaryCase);
								stmtUpdateQty.bindString(2, ""+primaryUnint);
								stmtUpdateQty.bindString(3, ""+availCases);
								stmtUpdateQty.bindString(4, ""+availUnits);
								stmtUpdateQty.bindString(5, ""+empNo);
								stmtUpdateQty.bindString(6, ""+productDO.SKU);
								stmtUpdateQty.execute();
								
								if(cursor != null && !cursor.isClosed())
									cursor.close();
							}
						}
						else
						{
							stmtInsert.bindString(1, date);
							stmtInsert.bindString(2, empNo);
							stmtInsert.bindString(3, productDO.SKU);
							stmtInsert.bindString(4, ""+productDO.preCases);
							stmtInsert.bindString(5, ""+productDO.preUnits);
							stmtInsert.bindString(6, "0");
							stmtInsert.bindString(7, ""+productDO.preCases);
							stmtInsert.bindString(8, ""+productDO.preUnits);
							stmtInsert.executeInsert();
						}
					}
				}
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}

	public void deleteOrder(OrderDO orderDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = 	null;
			Cursor cursor 			   = 	null;
			try 
			{
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				SQLiteStatement stmtDeleteOrder 	= 	objSqliteDB.compileStatement("Delete from tblOrderHeader Where OrderId = ?");
				SQLiteStatement stmtDeleteOrderDetail= 	objSqliteDB.compileStatement("Delete from tblOrderDetail Where OrderNo = ?");
				
				if(orderDO != null)
				{
					stmtDeleteOrder.bindString(1, orderDO.OrderId);
					stmtDeleteOrder.execute();
					
					stmtDeleteOrderDetail.bindString(1, orderDO.OrderId);
					stmtDeleteOrderDetail.execute();
				}
				stmtDeleteOrder.close();
				stmtDeleteOrderDetail.close();
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	public String getOrderId(int division)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			String orderId = "";
			Cursor cursor = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				String type = AppConstants.Order;
//				if(division > 0)
//					type = AppConstants.Food_Order;

				if( division <= 0)
					type = AppConstants.Order;
				else if(division == 1)
					type = AppConstants.Food_Order;
				else
					type = AppConstants.TPT_Order;
				
				String query = "SELECT id from tblOfflineData where  Type ='"+type+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) Order By id Limit 1";
				cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
					orderId = cursor.getString(0);
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			
			return orderId;
		}
	}
	
	public String getOrderIdBasedOnType(String type)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			String orderId = "";
			Cursor cursor = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT id from tblOfflineData where  Type ='"+type+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) Order By id Limit 1";
				cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
					orderId = cursor.getString(0);
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			
			return orderId;
		}
	}

	public void updateOrderNumber(String orderID) 
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+orderID+"'");
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
	
	public Vector<ProductDO> getProductsOfOrderDetaails(SQLiteDatabase sqLiteDatabase, String orderID)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<ProductDO> vecItemList = new Vector<ProductDO>();
			Cursor cursor = null;
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();
				
				cursor = sqLiteDatabase.rawQuery("SELECT OD.*, TP.UnitPerCase FROM tblOrderDetail OD INNER JOIN tblProducts TP ON TP.ItemCode = OD.ItemCode where OrderNo ='"+orderID+"' ORDER BY TP.DisplayOrder ASC", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						ProductDO productDO 	= 	new ProductDO();
						
						productDO.LineNo				= cursor.getString(0);
						productDO.OrderNo				= cursor.getString(1);
						productDO.SKU					= cursor.getString(2);
						productDO.ItemType				= cursor.getString(3);
						productDO.Description			= cursor.getString(4);
						productDO.itemPrice				= cursor.getFloat(5);
						productDO.unitSellingPrice		= cursor.getFloat(6);
						productDO.UOM					= cursor.getString(7);
						productDO.preCases				= cursor.getString(8);
						productDO.preUnits				= cursor.getString(9);
						
						productDO.UnitsPerCases			= cursor.getInt(27);
//						productDO.totalCases			= cursor.getFloat(10);
//						productDO.totalCases			= cursor.getFloat(11);
						
						getTotalUnits(productDO);
						
//						productDO.totalUnits			= (int)cursor.getFloat(10);
//						productDO.totalUnits			= (int)cursor.getFloat(11);
						
						productDO.invoiceAmount			= cursor.getFloat(12);
						productDO.invoiceAmount			= cursor.getFloat(13);
						
						productDO.priceUsedLevel1		= cursor.getFloat(12);
						productDO.priceUsedLevel2		= cursor.getFloat(13);
						
						productDO.discountAmount		= cursor.getFloat(15);
						
						productDO.promoCode				= cursor.getString(17);
						productDO.promotionType			= cursor.getString(18);
						productDO.strExpiryDate			= cursor.getString(20);
						productDO.reason				= cursor.getString(21);
						productDO.PromoLineNo			= cursor.getInt(22);
						
						if(cursor.getString(24) != null)
							productDO.recomUnits			= StringUtils.getFloat(cursor.getString(24));
						
						
						productDO.depositPrice			= cursor.getFloat(25);
						productDO.BatchCode				= cursor.getString(26);
						
						if(productDO.cases == null || productDO.cases.trim().equalsIgnoreCase(""))
							productDO.cases = "0";

						if(productDO.units == null || productDO.units.trim().equalsIgnoreCase(""))
							productDO.units = "0";
						
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

	public Vector<CustomerStatmentDO> getCustomerStatement(String fromDate,String toDate,String tempDate,String siteID, boolean isLessthan)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<CustomerStatmentDO> vecDetails = new Vector<CustomerStatmentDO>();
			Cursor cursor = null;
			SQLiteDatabase sqLiteDatabase = null;
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();
				
				String query;
				
				/*query = "SELECT * FROM(" +
						"SELECT " +
						"CASE WHEN (TrxType = 1 AND TrxStatus = 200) THEN 'Sales Order' " +
						"WHEN (TrxType = 5 AND TrxStatus = 100) THEN 'Pre Sales' "+
						"WHEN (TrxType = 6 AND TrxStatus = 200) THEN 'FOC Order' "+
						"WHEN (TrxType = 4 AND TrxStatus = 200) THEN 'GRV' " +
						"ELSE 'Advance Order' " +
						"END AS TrxType,TrxCode, TrxDate, (TotalAmount-TotalDiscountAmount) AS Amount,1 as type " +
						"FROM tblTrxHeader " +
						"WHERE TrxDate BETWEEN '"+fromDate+"' AND '"+toDate+"' " +
						"AND ((TrxType IN(1, 4, 6) AND TrxStatus = 200) " +
						"OR TrxType IN(3,5) AND TrxStatus = 100) AND ClientCode = '"+siteID+"' " +
						"UNION " +
						"SELECT 'Payment' AS TrxType, ReceiptId TrxCode, PaymentDate TrxDate, Amount AS Amount,2 as type " +
						"FROM tblPaymentHeader " +
						"WHERE PaymentDate BETWEEN '"+fromDate+"' AND '"+toDate+"' AND SiteId = '"+siteID+"' ) AS A " +
						"ORDER BY 3 DESC";*/
				
				query = "SELECT * FROM(" +
					      "SELECT " +
					      "CASE WHEN (TrxType = 1 AND TrxStatus = 200) THEN 'Sales Order' " +
					      "WHEN (TrxType = 6 AND TrxStatus = 200) THEN 'FOC Order' "+
					      "WHEN (TrxType = 4 AND TrxStatus = 200) THEN 'GRV' " +
					      "ELSE 'Advance Order' " +
					      "END AS TrxType,TrxCode, TrxDate, (TotalAmount-TotalDiscountAmount) AS Amount,1 as type " +
					      "FROM tblTrxHeader " +
					      "WHERE TrxDate BETWEEN '"+fromDate+"' AND '"+toDate+"' " +
					      "AND ((TrxType IN(1, 4, 6) AND TrxStatus = 200) " +
					      "OR TrxType IN(3) AND TrxStatus = 100) AND ClientCode = '"+siteID+"' " +
					      "UNION " +
					      "SELECT 'Payment' AS TrxType, ReceiptId TrxCode, PaymentDate TrxDate, Amount AS Amount,2 as type " +
					      "FROM tblPaymentHeader " +
					      "WHERE PaymentDate BETWEEN '"+fromDate+"' AND '"+toDate+"' AND SiteId = '"+siteID+"' ) AS A " +
					      "ORDER BY 3 DESC";
				
				LogUtils.errorLog("Customer Satement Qurey", ""+query);
				cursor = sqLiteDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						CustomerStatmentDO customerStatmentDO 	= 	new CustomerStatmentDO();
						
						customerStatmentDO.invoiceType = cursor.getString(0);
						customerStatmentDO.trxNumber   = cursor.getString(1);
						customerStatmentDO.trxDate     = cursor.getString(2);
						customerStatmentDO.amount      = cursor.getString(3);
						
						vecDetails.add(customerStatmentDO);
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
	
			return vecDetails;
		}
	}
	
	
	public Vector<CustomerStatmentDO> getPaymentSummaryReport(String fromDate,String toDate,String tempDate,String siteID)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<CustomerStatmentDO> vecDetails = new Vector<CustomerStatmentDO>();
			Cursor cursor = null;
			SQLiteDatabase sqLiteDatabase = null;
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();
				
				String query="";
				cursor = sqLiteDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						CustomerStatmentDO customerStatmentDO 	= 	new CustomerStatmentDO();
						
						customerStatmentDO.invoiceType = cursor.getString(0);
						customerStatmentDO.trxNumber   = cursor.getString(1);
						customerStatmentDO.trxDate     = cursor.getString(2);
						customerStatmentDO.amount      = cursor.getString(3);
						
						vecDetails.add(customerStatmentDO);
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
	
			return vecDetails;
		}
	}
	
	public int getCartCount(String site, int orderType) 
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			Vector<ProductDO> vecItemList = new Vector<ProductDO>();
			Cursor cursor = null;
			int count = 0;
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor = sqLiteDatabase.rawQuery("SELECT COUNT(TD.TrxCode) FROM tblTrxDetail TD INNER JOIN tblTrxHeader TH ON TH.TrxCode = TD.TrxCode WHERE TH.TrxType = '"+orderType+"' AND TH.ClientCode = '"+site+"'", null);
				
				if(cursor.moveToFirst())
				{
					count = cursor.getInt(0);
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
	
			return count;
		}
	}

	public void insertCartOrderDetails_Promo(TrxHeaderDO trxHeaderDO)
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
																	 "CreatedOn, PreTrxCode, TRXStatus, PrintingTimes, ApproveByCode, ApprovedDate,DeliveryDate,TrxSubType) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
					
					stmtInsert.executeInsert();
				}
				
				
				SQLiteStatement stmtSelectDetail = objSqliteDB.compileStatement("SELECT COUNT(*) FROM tblTrxDetail WHERE TrxCode = ? AND ItemCode=?");
				
				
				SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblTrxDetail (LineNo, TrxCode, ItemCode,OrgCode,TrxReasonCode, TrxDetailsNote, ItemType,BasePrice, UOM, " +
																				"QuantityLevel1, QuantityLevel2, QuantityLevel3, QuantityBU, RequestedBU, ApprovedBU, CollectedBU, FinalBU, " +
																				"PriceUsedLevel1, PriceUsedLevel2, PriceUsedLevel3, TotalDiscountPercentage,TotalDiscountAmount, " +
																				"CalculatedDiscountPercentage, CalculatedDiscountAmount, UserDiscountPercentage, UserDiscountAmount,ItemDescription, " +
																				"AffectedStock, Status, PromoID, PromoType, CreatedOn, TRXStatus, ExpiryDate, RelatedLineID, ItemGroupLevel5, " +
																				"TaxType, SuggestedBU, PushedOn, Reason, CancelledQuantity, InProcessQuantity, ShippedQuantity, MissedBU, " +
																				"BatchNumber) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				
				SQLiteStatement stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblTrxDetail SET QuantityLevel1=?, QuantityLevel2=?, QuantityLevel3=?, QuantityBU=?, RequestedBU=?, " +
						"PriceUsedLevel1=?, PriceUsedLevel2=?, PriceUsedLevel3=?, TotalDiscountPercentage=?,TotalDiscountAmount=?WHERE TrxCode = ? AND ItemCode=?");
				
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
						
						stmtInsertOrder.executeInsert();
					}
					else
					{
						stmtUpdateOrder.bindDouble(1, trxDetailsDO.quantityLevel1);
						stmtUpdateOrder.bindDouble(2, trxDetailsDO.quantityLevel2);
						stmtUpdateOrder.bindLong(3, trxDetailsDO.quantityLevel3);
						stmtUpdateOrder.bindLong(4, trxDetailsDO.quantityBU);
						stmtUpdateOrder.bindLong(5, trxDetailsDO.requestedBU);
						stmtUpdateOrder.bindDouble(6, trxDetailsDO.priceUsedLevel1);
						stmtUpdateOrder.bindDouble(7, trxDetailsDO.priceUsedLevel2);
						stmtUpdateOrder.bindDouble(8, trxDetailsDO.priceUsedLevel3);
						stmtUpdateOrder.bindDouble(9, trxDetailsDO.totalDiscountPercentage);
						stmtUpdateOrder.bindDouble(10, trxDetailsDO.totalDiscountAmount);
						stmtUpdateOrder.bindString(11, trxHeaderDO.trxCode);
						stmtUpdateOrder.bindString(12, trxDetailsDO.itemCode);
						stmtUpdateOrder.execute();
					}
				}
				
				
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

	public HashMap<String, Vector<TrxDetailsDO>> getCartDetail(String site)
	{
		return null;
	}
	
	public void deleteIfExistsInCart(String site)
	{
		
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = 	null;
			try 
			{
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				SQLiteStatement stmtDeleteCartOrder 	= 	objSqliteDB.compileStatement("Delete from tblTrxHeader Where TrxCode = ?");
				SQLiteStatement stmtDeleteCartOrderDetail= 	objSqliteDB.compileStatement("Delete from tblTrxDetail Where TrxCode = ?");
				
				if(site != null)
				{
					stmtDeleteCartOrder.bindString(1, site);
					stmtDeleteCartOrder.execute();
					
					stmtDeleteCartOrderDetail.bindString(1, site);
					stmtDeleteCartOrderDetail.execute();
				}
				stmtDeleteCartOrder.close();
				stmtDeleteCartOrderDetail.close();
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
	
	/**
	 * Updates the inventory if Return Order taken
	 * @param vecDetailsDO
	 * @param date
	 * @param userCode
	 * @return
	 */
	public boolean updateInventory_WhileReturnOrder(ArrayList<TrxDetailsDO> vecDetailsDO, String date,String userCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				Cursor cursor 						= 	null;
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				SQLiteStatement sqLiteInsertStatement=null,sqLiteSelectStatement=null;
				String strUpdate 					= 	"Update tblVanStock set SellableQuantity=?, TotalQuantity=? where ItemCode = ?";
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				String insertItem="Insert into tblVanStock (OrgCode,VanStockId,UserCode,ItemCode,SellableQuantity,ReturnedQuantity,TotalQuantity,BatchNumber,ExpiryDate,VehicleCode) values(?,?,?,?,?,?,?,?,?,?)";
				String selectmaxStockId="select max(VanStockId) from tblVanStock";
				if(vecDetailsDO != null && vecDetailsDO.size() > 0)
				{
					sqLiteInsertStatement = objSqliteDB.compileStatement(insertItem);
					sqLiteSelectStatement = objSqliteDB.compileStatement(selectmaxStockId);
					for(TrxDetailsDO productDO : vecDetailsDO)
					{
						if(productDO.itemType!=null && productDO.itemType.equalsIgnoreCase(TrxDetailsDO.get_TRX_ITEM_SELLABLE())){
							String strQuery 	= 	"SELECT SellableQuantity, TotalQuantity From tblVanStock where ItemCode = '"+productDO.itemCode+"'";
							
							cursor 	= objSqliteDB.rawQuery(strQuery, null);
							if(cursor.moveToFirst())
							{
								float availQty 		= cursor.getFloat(0);
								float totalQty 		= cursor.getFloat(1);
								availQty 			= availQty + productDO.collectedBU;
								
								if(availQty > 0 )
									stmtUpdateQty.bindString(1, ""+availQty);
								else
									stmtUpdateQty.bindString(1, "0");
								
								stmtUpdateQty.bindString(2, ""+totalQty);
								stmtUpdateQty.bindString(3, ""+productDO.itemCode);
								stmtUpdateQty.execute();
							}else{
								try {
									long id=sqLiteSelectStatement.simpleQueryForLong();
									id=id+1;
									sqLiteInsertStatement.bindString(1, "");
									sqLiteInsertStatement.bindLong(2, id);
									sqLiteInsertStatement.bindString(3, userCode);
									sqLiteInsertStatement.bindString(4, productDO.itemCode);
									sqLiteInsertStatement.bindLong(5, productDO.collectedBU);
									sqLiteInsertStatement.bindLong(6, 0);
									sqLiteInsertStatement.bindLong(7, productDO.collectedBU);
									sqLiteInsertStatement.bindString(8, "B1");
									sqLiteInsertStatement.bindString(9, CalendarUtils.getOrderPostDate());
									sqLiteInsertStatement.bindString(10, "");
									sqLiteInsertStatement.execute();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							if(cursor != null && !cursor.isClosed())
								cursor.close();
						}
					}
				}
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}


	public String updatePresellerOrder(ArrayList<TrxHeaderDO> arrayList,String userId, boolean isPreseller) 
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null,orderCursor = null;
			String itemCodes="";
			String invoiceNumber = "";
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelect = objSqliteDB.compileStatement("SELECT COUNT(*) FROM tblTrxHeader WHERE TrxCode = ?");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("Update tblTrxHeader set TotalAmount=?, TotalDiscountAmount=?, " +
						"TrxReasonCode=?, Status=?,TRXStatus=?, ApproveByCode=?, ApprovedDate=?,ClientSignature=?,SalesmanSignature=?," +
						"ReferenceCode=?,TrxCode=?,SplDisc=?,LPO=?,Narrotion=?,Reason=?,RateDiff=?," +
						"TrxType=?,UserCode=?,PresellerCode=?,TrxDate=?, VisitCode=?, JourneyCode=? " +
						"where TrxCode=?");
				
				SQLiteStatement stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblTrxDetail SET TrxReasonCode=?,TrxDetailsNote=?,ItemType=?,"
						+ "QuantityLevel1=?, QuantityLevel2=?, QuantityLevel3=?, QuantityBU=?, RequestedBU=?,ApprovedBU=?,CollectedBU=?,FinalBU=?, " +
						"PriceUsedLevel1=?, PriceUsedLevel2=?, PriceUsedLevel3=?, TotalDiscountPercentage=?,TotalDiscountAmount=?,AffectedStock=?,Status=?,TRXStatus=?,ExpiryDate=?,ItemGroupLevel5=?,"
						+ "SuggestedBU=?,Reason=?,CancelledQuantity=?,InProcessQuantity=?,ShippedQuantity=?,MissedBU =?,UOM=?,TrxCode=? WHERE TrxCode = ? AND ItemCode=?");
				
				SQLiteStatement stmtSelectDetail = objSqliteDB.compileStatement("SELECT COUNT(*) FROM tblTrxDetail WHERE TrxCode = ? AND ItemCode=?");

				SQLiteStatement stmtSelectLineNODetail = objSqliteDB.compileStatement("SELECT IFNULL(max(LineNo),0) FROM tblTrxDetail where TrxCode=?");

				SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblTrxDetail (LineNo, TrxCode, ItemCode,OrgCode,TrxReasonCode, TrxDetailsNote, ItemType,BasePrice, UOM, " +
																				"QuantityLevel1, QuantityLevel2, QuantityLevel3, QuantityBU, RequestedBU, ApprovedBU, CollectedBU, FinalBU, " +
																				"PriceUsedLevel1, PriceUsedLevel2, PriceUsedLevel3, TotalDiscountPercentage,TotalDiscountAmount, " +
																				"CalculatedDiscountPercentage, CalculatedDiscountAmount, UserDiscountPercentage, UserDiscountAmount,ItemDescription, " +
																				"AffectedStock, Status, PromoID, PromoType, CreatedOn, TRXStatus, ExpiryDate, RelatedLineID, ItemGroupLevel5, " +
																				"TaxType, SuggestedBU, PushedOn, Reason, CancelledQuantity, InProcessQuantity, ShippedQuantity, MissedBU, " +
																				"BatchNumber) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				for(TrxHeaderDO trxHeaderDO:arrayList){
					
					String trxCodeQuery = "";
					boolean updateAchivement = false;
					if(trxHeaderDO.trxStatus != TrxHeaderDO.get_TRX_STATUS_SAVED() &&
							(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() 
							|| trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER()
							|| trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY()
							|| trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER()
							|| trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER()))
					{
						
						updateAchivement = true;
						String type = "";
						if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()) {
//							if(trxHeaderDO.Division <= 0)
//								type = AppConstants.GRV;
//							else
//								type = AppConstants.Food_GRV;
							if(trxHeaderDO.Division <= 0)
								type = AppConstants.GRV;
							else if (trxHeaderDO.Division == 1)
								type = AppConstants.Food_GRV;
							else
								type = AppConstants.TPT_GRV;
						}
						else {
							if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED()) {
//								if(trxHeaderDO.Division <= 0)
//									type = AppConstants.SAVED;
//								else
//									type = AppConstants.Food_SAVED;
								if(trxHeaderDO.Division <= 0)
									type = AppConstants.SAVED;
								else if (trxHeaderDO.Division == 1)
									type = AppConstants.Food_SAVED;
								else
									type = AppConstants.TPT_SAVED;
							}
							
							else if(trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER()) {
//								if(trxHeaderDO.Division <= 0)
//									type = AppConstants.FOC;
//								else
//									type = AppConstants.Food_FOC;
								if(trxHeaderDO.Division <= 0)
									type = AppConstants.FOC;
								else if (trxHeaderDO.Division == 1)
									type = AppConstants.Food_FOC;
								else
									type = AppConstants.TPT_FOC;
							}
							else {
//								if(trxHeaderDO.Division <= 0)
//									type = AppConstants.Order;
//								else
//									type = AppConstants.Food_Order;
								if(trxHeaderDO.Division <= 0)
									type = AppConstants.Order;
								else if (trxHeaderDO.Division == 1)
									type = AppConstants.Food_Order;
								else
									type = AppConstants.TPT_Order;
							}
						}
						
//						if(trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
//							trxCodeQuery = "SELECT id from tblOfflineData where  Type ='"+AppConstants.FOC+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) Order By id Limit 1";
//						else
//							trxCodeQuery = "SELECT id from tblOfflineData where  Type ='"+AppConstants.Order+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) Order By id Limit 1";
						
							trxCodeQuery = "SELECT id from tblOfflineData where  Type ='"+type+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) Order By id Limit 1";
					
						orderCursor = objSqliteDB.rawQuery(trxCodeQuery, null);
						if(orderCursor != null && orderCursor.moveToFirst())
							invoiceNumber = orderCursor.getString(0);
						
//						objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+invoiceNumber+"'");
						
						if(orderCursor!=null && !orderCursor.isClosed())
							orderCursor.close();
						
						if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
						{
//							trxHeaderDO.trxType = TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER();
							if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_ADVANCE_ORDER_DELIVERED())
							{
								trxHeaderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
								//isPreseller = true;
							}
							else
								trxHeaderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_PRESALES_ORDER();
							
							trxHeaderDO.PreSellerUserCode = trxHeaderDO.userCode;
							trxHeaderDO.userCode = userId;
							
							if(!isPreseller)
							{
								if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
									trxHeaderDO.trxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
								else if(trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
									trxHeaderDO.trxType = TrxHeaderDO.get_TYPE_FREE_DELIVERY();
								else
									trxHeaderDO.trxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
							}
						}
						
//						if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED() && trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER())
//						{
							//===============Close Updating Monthly Sales amount=========================
//						}
					}
					
					if(invoiceNumber == null || invoiceNumber.equalsIgnoreCase(""))
						invoiceNumber = trxHeaderDO.trxCode;
					
					stmtSelect.bindString(1, trxHeaderDO.trxCode);
					
					long count = stmtSelect.simpleQueryForLong();
					long preCount = 0;
					String savedTrxCode = "";
					long lineNOCount = 0;
					if(count > 0 && trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && trxHeaderDO.trxStatus != TrxHeaderDO.get_TRX_STATUS_SAVED()){
						preCount = count;
						count=0;
						trxHeaderDO.referenceCode=trxHeaderDO.trxCode;
						savedTrxCode=trxHeaderDO.trxCode;
						trxHeaderDO.trxCode="";
					} else {
						
						stmtSelectLineNODetail.bindString(1, trxHeaderDO.trxCode);
						
						lineNOCount = stmtSelectLineNODetail.simpleQueryForLong();
						
						stmtUpdate.bindDouble(1, trxHeaderDO.totalAmount);
						stmtUpdate.bindDouble(2, trxHeaderDO.totalDiscountAmount);
						stmtUpdate.bindString(3, trxHeaderDO.trxReasonCode);
						stmtUpdate.bindLong(4, trxHeaderDO.status);
						stmtUpdate.bindLong(5, trxHeaderDO.trxStatus);
						stmtUpdate.bindString(6, trxHeaderDO.approveByCode+"");
						stmtUpdate.bindString(7, trxHeaderDO.approvedDate+"");
						stmtUpdate.bindString(8, trxHeaderDO.clientSignature+"");
						stmtUpdate.bindString(9, trxHeaderDO.salesmanSignature+"");
						stmtUpdate.bindString(10, trxHeaderDO.trxCode);
						stmtUpdate.bindString(11, invoiceNumber);
						stmtUpdate.bindString(12, ""+trxHeaderDO.specialDiscPercent);
						stmtUpdate.bindString(13, trxHeaderDO.LPONo);
						stmtUpdate.bindString(14, trxHeaderDO.PromotionReason);
						stmtUpdate.bindString(15, ""+trxHeaderDO.returnReason);
						stmtUpdate.bindString(16, ""+trxHeaderDO.rateDiff);
						stmtUpdate.bindString(17, ""+trxHeaderDO.trxType);
						stmtUpdate.bindString(18, ""+trxHeaderDO.userCode);
						stmtUpdate.bindString(19, ""+trxHeaderDO.PreSellerUserCode);
						stmtUpdate.bindString(20, ""+trxHeaderDO.trxDate);
						stmtUpdate.bindString(21, ""+trxHeaderDO.visitCode);
						stmtUpdate.bindString(22, ""+trxHeaderDO.journeyCode);
						stmtUpdate.bindString(23, trxHeaderDO.trxCode);
						
						stmtUpdate.execute();
					}
					
					
					if(count <= 0 )
					{
						trxHeaderDO.trxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
						trxHeaderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
						
						for(TrxDetailsDO trxDetailsDO:trxHeaderDO.arrTrxDetailsDOs)
							trxDetailsDO.trxCode = trxHeaderDO.trxCode;
						
						insertOrderDetails_Promo(trxHeaderDO);
						
						if(preCount>0){
							if(objSqliteDB==null || !objSqliteDB.isOpen())
								objSqliteDB = DatabaseHelper.openDataBase();
							SQLiteStatement stmtUpdateSavedOrder=objSqliteDB.compileStatement("Update tblTrxHeader set ReferenceCode=?, Status=?, TRXStatus = ? where TrxCode=?");
							stmtUpdateSavedOrder.bindString(1, trxHeaderDO.trxCode);
							stmtUpdateSavedOrder.bindLong(2, 1/*trxHeaderDO.status*/);
							stmtUpdateSavedOrder.bindLong(3, TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED());
							stmtUpdateSavedOrder.bindString(4, savedTrxCode);
							long updatedRecords=stmtUpdateSavedOrder.executeUpdateDelete();
							LogUtils.debug("updatedRecords", ""+updatedRecords);
						}
					}
					else{
						
						itemCodes = "";
							
						if(updateAchivement){
							
							
							if(cursor!=null && !cursor.isClosed())
								cursor.close();
							
							float salesAmount=trxHeaderDO.totalAmount-trxHeaderDO.totalDiscountAmount;
							
							updateUserAchievedTarget(salesAmount, trxHeaderDO.userCode, objSqliteDB,trxHeaderDO.Division,1);
							
							
							//===============Start Updating Monthly Sales amount=========================
							
							/*
							 *  Need to change it to statement
							 */
							String query ="SELECT *from tblCustomerMonthlySales where  CustomerCode ='"+trxHeaderDO.clientCode+"'";
							
							cursor = objSqliteDB.rawQuery(query, null);
							if(cursor.moveToFirst())
							{
								SQLiteStatement stmtUpdateMonthlySalesAmount = objSqliteDB.compileStatement("update tblCustomerMonthlySales set SalesAmount=Cast((Cast(SalesAmount as float)+?) as float) where CustomerCode=?");
								stmtUpdateMonthlySalesAmount.bindDouble(1, salesAmount);
								stmtUpdateMonthlySalesAmount.bindString(2, trxHeaderDO.clientCode);
								stmtUpdateMonthlySalesAmount.execute();
								stmtUpdateMonthlySalesAmount.close();
							}
							else
							{
								SQLiteStatement stmtInsertMonthlySalesAmount = objSqliteDB.compileStatement("INSERT INTO tblCustomerMonthlySales(CustomerCode,SalesAmount) VALUES(?,?)");
								stmtInsertMonthlySalesAmount.bindString(1, trxHeaderDO.clientCode);
								stmtInsertMonthlySalesAmount.bindDouble(2, salesAmount);
								stmtInsertMonthlySalesAmount.executeInsert();
								stmtInsertMonthlySalesAmount.close();
							}
							
							if(cursor!=null && !cursor.isClosed())
								cursor.close();
						}
						
						for(TrxDetailsDO trxDetailsDO : trxHeaderDO.arrTrxDetailsDOs)
						{
							stmtSelectDetail.bindString(1, trxHeaderDO.trxCode);
							
							stmtSelectDetail.bindString(2, trxDetailsDO.itemCode);
							long countL = stmtSelectDetail.simpleQueryForLong();
							itemCodes = itemCodes+"'"+trxDetailsDO.itemCode+"'"+",";
							if(countL <= 0)
							{
								trxDetailsDO.lineNo = (int) ++lineNOCount;
								stmtInsertOrder.bindLong(1,trxDetailsDO.lineNo);
								stmtInsertOrder.bindString(2, invoiceNumber);
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
								stmtInsertOrder.bindDouble(22, trxDetailsDO.promotionalDiscountAmount);
								
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
								stmtUpdateOrder.bindDouble(16, trxDetailsDO.promotionalDiscountAmount);
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
								stmtUpdateOrder.bindString(28, trxDetailsDO.UOM);
								stmtUpdateOrder.bindString(29, invoiceNumber);
								stmtUpdateOrder.bindString(30, trxHeaderDO.trxCode);
								stmtUpdateOrder.bindString(31, trxDetailsDO.itemCode);
								stmtUpdateOrder.execute();
							}
						}	
						
						try
						{
							if(itemCodes!=null && itemCodes.length()>0)
							{
								itemCodes = itemCodes.substring(0,itemCodes.length()-1);
								
								SQLiteStatement stmtUpdateDetail = objSqliteDB.compileStatement("UPDATE tblTrxDetail SET QuantityLevel1=?,QuantityBU=?,RequestedBU=? WHERE TrxCode=? AND ItemCode NOT IN ("+itemCodes+")");
								stmtUpdateDetail.bindDouble(1,0);
								stmtUpdateDetail.bindLong(2,0);
								stmtUpdateDetail.bindLong(3,0);
								
//								stmtUpdateDetail.bindString(4, trxHeaderDO.trxCode);
								stmtUpdateDetail.bindString(4, invoiceNumber);
								
								stmtUpdateDetail.execute();
								stmtUpdateDetail.close();
								
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				stmtSelect.close();
				stmtSelectDetail.close();
				stmtSelectLineNODetail.close();
				stmtUpdate.close();
				stmtUpdateOrder.close();
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
			return invoiceNumber;
		}
	}

	public Vector<DailySalesData> getTotalTrasactionDetail(String fromDate) {

			synchronized(MyApplication.MyLock)
			{
				DailySalesData dailysalesdata;
				Vector<DailySalesData> vecdailysales  =  new Vector<DailySalesData>();
				SQLiteDatabase sqLiteDatabase =  null;
				Cursor cursor = null;
				String arrivalTime="";
				String OutTime="";
				int i=1;

				try
				{
					sqLiteDatabase =  DatabaseHelper.openDataBase();

//					String strQuery = "Select CV.ClientCode,CV.ArrivalTime,CV.OutTime,CV.TotalTimeInMins,TH.Sales,GH.ReturnVal,PH.CollectionAmount from \n" +
//							"(\n" +
//							"Select ClientCode,VisitCode,MIN(ArrivalTime) ArrivalTime,MAX(OutTime) OutTime,SUM(TotalTimeInMins)  TotalTimeInMins\n" +
//							"from tblCustomerVisit Group by ClientCode,VisitCode\n" +
//							") as CV\n" +
//							"Left Join \n" +
//							"(\n" +
//							"SELECT ClientCode,SUM(TotalAmount-TotalDiscountAmount) Sales,VisitCode FROM tblTrxHeader  \n" +
//							"where TrxType='"+TrxHeaderDO.get_TRXTYPE_SALES_ORDER()+"' OR TrxType='"+TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER()+"' and TrxStatus = '"+TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED()+"' and Trxdate='"+fromDate+"'\n" +
//							"Group by ClientCode,VisitCode\n" +
//							") as TH ON TH.VisitCode=CV.VisitCode and TH.ClientCode=CV.ClientCode\n" +
//							"Left Join \n" +
//							"(\n" +
//							"SELECT ClientCode,SUM(TotalAmount-TotalDiscountAmount) ReturnVal,VisitCode FROM tblTrxHeader  \n" +
//							"where TrxType='"+TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()+"' and Trxdate='"+fromDate+"'\n" +
//							"Group by ClientCode,VisitCode\n" +
//							") as GH ON GH.VisitCode=CV.VisitCode and GH.ClientCode=CV.ClientCode\n" +
//							"Left join \n" +
//							"(\n" +
//							"SELECT SiteId,SUM(Amount) CollectionAmount,VisitCode FROM tblPaymentHeader TH \n" +
//							"where  PaymentDate='"+fromDate+"'\n" +
//							"Group by SiteId,VisitCode\n" +
//							") as PH ON PH.VisitCode=CV.VisitCode and PH.SiteId=CV.ClientCode";
					String strQuery = "Select CV.ClientCode,CV.ArrivalTime,CV.OutTime,CV.TotalTimeInMins,TH.Sales,GH.ReturnVal,PH.CollectionAmount,CV.SiteName from  \n" +
							"       (\n" +
							"       Select ClientCode,VisitCode,MIN(ArrivalTime) ArrivalTime,MAX(OutTime) OutTime,SUM(TotalTimeInMins)  TotalTimeInMins, TC.sitename  \n" +
							"       from tblCustomerVisit TV INNER JOIN tblCustomer TC ON TV.ClientCode=TC.site \n" +
							"    where Date like '"+fromDate+"%'\n" +
							"    Group by ClientCode,VisitCode\n" +
							"       ) as CV\n" +
							"       Left Join \n" +
							"       (\n" +
							"       SELECT ClientCode,SUM(TotalAmount-TotalDiscountAmount) Sales,VisitCode FROM tblTrxHeader \n" +
							"       where TrxType='"+TrxHeaderDO.get_TRXTYPE_SALES_ORDER()+"' OR TrxType='"+TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER()+"' and TrxStatus = '"+TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED()+"' and Trxdate like '"+fromDate+"%'\n" +
							"       Group by ClientCode,VisitCode\n" +
							"       ) as TH ON TH.VisitCode=CV.VisitCode and TH.ClientCode=CV.ClientCode\n" +
							"       Left Join \n" +
							"       (\n" +
							"       SELECT ClientCode,SUM(TotalAmount-TotalDiscountAmount) ReturnVal,VisitCode FROM tblTrxHeader  \n" +
							"       where TrxType='"+TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()+"' and Trxdate like '"+fromDate+"%'\n" +
							"       Group by ClientCode,VisitCode\n" +
							"       ) as GH ON GH.VisitCode=CV.VisitCode and GH.ClientCode=CV.ClientCode\n" +
							"       Left join \n" +
							"       (\n" +
							"       SELECT SiteId,SUM(Amount) CollectionAmount,VisitCode FROM tblPaymentHeader TH \n" +
							"       where  PaymentDate like '"+fromDate+"%'\n" +
							"       Group by SiteId,VisitCode\n" +
							"       ) as PH ON PH.VisitCode=CV.VisitCode and PH.SiteId=CV.ClientCode Order BY CV.ArrivalTime ASC";

					cursor	=	sqLiteDatabase.rawQuery(strQuery, null);

					if(cursor.moveToFirst())
					{
						do
						{
							dailysalesdata 				= 	new DailySalesData();
							dailysalesdata.SlNo		=	""+i;
							i++;
							dailysalesdata.Code		=	cursor.getString(0);
							arrivalTime 	=	cursor.getString(1).split("T")[1];
							OutTime =	cursor.getString(2).split("T")[1];
							dailysalesdata.Tspend	=	cursor.getString(3);
							dailysalesdata.TSales 	=	cursor.getInt(4)+"";
							dailysalesdata.Return 	=	cursor.getInt(5)+"";
							dailysalesdata.Collection	=	cursor.getInt(6)+"";
							dailysalesdata.SiteName	=	cursor.getString(7);

							dailysalesdata.Checkin= arrivalTime.substring(0,arrivalTime.lastIndexOf(':'));
							dailysalesdata.Checkout= OutTime.substring(0,OutTime.lastIndexOf(':'));
							vecdailysales.add(dailysalesdata);
						}
						while(cursor.moveToNext());


						if(cursor!=null && !cursor.isClosed())
							cursor.close();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
					if(sqLiteDatabase != null)
						sqLiteDatabase.close();
				}

				return vecdailysales;
			}

	}
	public Vector<DailySalesData> getTotalTrasactionDetailNew(String fromDate) {

			synchronized(MyApplication.MyLock)
			{
				DailySalesData dailysalesdata;
				Vector<DailySalesData> vecdailysales  =  new Vector<DailySalesData>();
				SQLiteDatabase sqLiteDatabase =  null;
				Cursor cursor = null;
				String arrivalTime="";
				String OutTime="";
				int i=1;

				try
				{
					sqLiteDatabase =  DatabaseHelper.openDataBase();

//					String strQuery = "Select CV.ClientCode,CV.ArrivalTime,CV.OutTime,CV.TotalTimeInMins,TH.Sales,GH.ReturnVal,PH.CollectionAmount from \n" +
//							"(\n" +
//							"Select ClientCode,VisitCode,MIN(ArrivalTime) ArrivalTime,MAX(OutTime) OutTime,SUM(TotalTimeInMins)  TotalTimeInMins\n" +
//							"from tblCustomerVisit Group by ClientCode,VisitCode\n" +
//							") as CV\n" +
//							"Left Join \n" +
//							"(\n" +
//							"SELECT ClientCode,SUM(TotalAmount-TotalDiscountAmount) Sales,VisitCode FROM tblTrxHeader  \n" +
//							"where TrxType='"+TrxHeaderDO.get_TRXTYPE_SALES_ORDER()+"' OR TrxType='"+TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER()+"' and TrxStatus = '"+TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED()+"' and Trxdate='"+fromDate+"'\n" +
//							"Group by ClientCode,VisitCode\n" +
//							") as TH ON TH.VisitCode=CV.VisitCode and TH.ClientCode=CV.ClientCode\n" +
//							"Left Join \n" +
//							"(\n" +
//							"SELECT ClientCode,SUM(TotalAmount-TotalDiscountAmount) ReturnVal,VisitCode FROM tblTrxHeader  \n" +
//							"where TrxType='"+TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()+"' and Trxdate='"+fromDate+"'\n" +
//							"Group by ClientCode,VisitCode\n" +
//							") as GH ON GH.VisitCode=CV.VisitCode and GH.ClientCode=CV.ClientCode\n" +
//							"Left join \n" +
//							"(\n" +
//							"SELECT SiteId,SUM(Amount) CollectionAmount,VisitCode FROM tblPaymentHeader TH \n" +
//							"where  PaymentDate='"+fromDate+"'\n" +
//							"Group by SiteId,VisitCode\n" +
//							") as PH ON PH.VisitCode=CV.VisitCode and PH.SiteId=CV.ClientCode";
					String strQuery = "Select CV.ClientCode,CV.ArrivalTime,CV.OutTime,CV.TotalTimeInMins,TH.Sales,GH.ReturnVal,PH.CollectionAmount,CV.SiteName from  \n" +
							"       (\n" +
							"       Select ClientCode,VisitCode,MIN(ArrivalTime) ArrivalTime,MAX(OutTime) OutTime,SUM(TotalTimeInMins)  TotalTimeInMins, TC.sitename  \n" +
							"       from tblCustomerVisit TV INNER JOIN tblCustomer TC ON TV.ClientCode=TC.site \n" +
							"    where Date like '"+fromDate+"%'\n" +
							"    Group by ClientCode,VisitCode\n" +
							"       ) as CV\n" +
							"       Left Join \n" +
							"       (\n" +
							"       SELECT ClientCode,SUM(TotalAmount-TotalDiscountAmount+TotalTAXAmount) Sales,VisitCode FROM tblTrxHeader \n" +
							"       where TrxType='"+TrxHeaderDO.get_TRXTYPE_SALES_ORDER()+"' OR TrxType='"+TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER()+"' and TrxStatus = '"+TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED()+"' and Trxdate like '"+fromDate+"%'\n" +
							"       Group by ClientCode,VisitCode\n" +
							"       ) as TH ON TH.VisitCode=CV.VisitCode and TH.ClientCode=CV.ClientCode\n" +
							"       Left Join \n" +
							"       (\n" +
							"       SELECT ClientCode,SUM(TotalAmount-TotalDiscountAmount+TotalTAXAmount) ReturnVal,VisitCode FROM tblTrxHeader  \n" +
							"       where TrxType='"+TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()+"' and Trxdate like '"+fromDate+"%'\n" +
							"       Group by ClientCode,VisitCode\n" +
							"       ) as GH ON GH.VisitCode=CV.VisitCode and GH.ClientCode=CV.ClientCode\n" +
							"       Left join \n" +
							"       (\n" +
							"       SELECT SiteId,SUM(Amount) CollectionAmount,VisitCode FROM tblPaymentHeader TH \n" +
							"       where  PaymentDate like '"+fromDate+"%'\n" +
							"       Group by SiteId,VisitCode\n" +
							"       ) as PH ON PH.VisitCode=CV.VisitCode and PH.SiteId=CV.ClientCode Order BY CV.ArrivalTime ASC";

					cursor	=	sqLiteDatabase.rawQuery(strQuery, null);

					if(cursor.moveToFirst())
					{
						do
						{
							dailysalesdata 				= 	new DailySalesData();
							dailysalesdata.SlNo		=	""+i;
							i++;
							dailysalesdata.Code		=	cursor.getString(0);
							arrivalTime 	=	cursor.getString(1).split("T")[1];
							OutTime =	cursor.getString(2).split("T")[1];
							dailysalesdata.Tspend	=	cursor.getString(3);
							dailysalesdata.TSales 	=	cursor.getInt(4)+"";
							dailysalesdata.Return 	=	cursor.getInt(5)+"";
							dailysalesdata.Collection	=	cursor.getInt(6)+"";
							dailysalesdata.SiteName	=	cursor.getString(7);

							dailysalesdata.Checkin= arrivalTime.substring(0,arrivalTime.lastIndexOf(':'));
							dailysalesdata.Checkout= OutTime.substring(0,OutTime.lastIndexOf(':'));
							vecdailysales.add(dailysalesdata);
						}
						while(cursor.moveToNext());


						if(cursor!=null && !cursor.isClosed())
							cursor.close();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
					if(sqLiteDatabase != null)
						sqLiteDatabase.close();
				}

				return vecdailysales;
			}

	}

//	public String updatePresellerOrderNew(ArrayList<TrxHeaderDO> arrayList,String userId, boolean isPreseller) 
//	{
//		synchronized (MyApplication.MyLock)
//		{
//			SQLiteDatabase objSqliteDB = null;
//			Cursor cursor = null,orderCursor = null;
//			String itemCodes="";
////			String invoiceNumber = "";
//			try 
//			{
//				objSqliteDB = DatabaseHelper.openDataBase();
//				
////				SQLiteStatement stmtSelect = objSqliteDB.compileStatement("SELECT COUNT(*) FROM tblTrxHeader WHERE TrxCode = ?");
//				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("Update tblTrxHeader set Status=? where TrxCode=?");
//				
//				SQLiteStatement stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblTrxDetail SET TrxReasonCode=?,TrxDetailsNote=?,ItemType=?,"
//						+ "QuantityLevel1=?, QuantityLevel2=?, QuantityLevel3=?, QuantityBU=?, RequestedBU=?,ApprovedBU=?,CollectedBU=?,FinalBU=?, " +
//						"PriceUsedLevel1=?, PriceUsedLevel2=?, PriceUsedLevel3=?, TotalDiscountPercentage=?,TotalDiscountAmount=?,AffectedStock=?,Status=?,TRXStatus=?,ExpiryDate=?,ItemGroupLevel5=?,"
//						+ "SuggestedBU=?,Reason=?,CancelledQuantity=?,InProcessQuantity=?,ShippedQuantity=?,MissedBU =?,UOM=?,TrxCode=? WHERE TrxCode = ? AND ItemCode=?");
//				
//				SQLiteStatement stmtSelectDetail = objSqliteDB.compileStatement("SELECT COUNT(*) FROM tblTrxDetail WHERE TrxCode = ? AND ItemCode=?");
//
//				SQLiteStatement stmtSelectLineNODetail = objSqliteDB.compileStatement("SELECT IFNULL(max(LineNo),0) FROM tblTrxDetail where TrxCode=?");
//
//				SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblTrxDetail (LineNo, TrxCode, ItemCode,OrgCode,TrxReasonCode, TrxDetailsNote, ItemType,BasePrice, UOM, " +
//																				"QuantityLevel1, QuantityLevel2, QuantityLevel3, QuantityBU, RequestedBU, ApprovedBU, CollectedBU, FinalBU, " +
//																				"PriceUsedLevel1, PriceUsedLevel2, PriceUsedLevel3, TotalDiscountPercentage,TotalDiscountAmount, " +
//																				"CalculatedDiscountPercentage, CalculatedDiscountAmount, UserDiscountPercentage, UserDiscountAmount,ItemDescription, " +
//																				"AffectedStock, Status, PromoID, PromoType, CreatedOn, TRXStatus, ExpiryDate, RelatedLineID, ItemGroupLevel5, " +
//																				"TaxType, SuggestedBU, PushedOn, Reason, CancelledQuantity, InProcessQuantity, ShippedQuantity, MissedBU, " +
//																				"BatchNumber) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
//				
//				for(TrxHeaderDO trxHeaderDO:arrayList){
//					
////					String trxCodeQuery = "";
//					if(trxHeaderDO.trxStatus != TrxHeaderDO.get_TRX_STATUS_SAVED() &&
//							(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() 
//							|| trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER()
//							|| trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY()
//							|| trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER()))
//					{
////						trxCodeQuery = "SELECT id from tblOfflineData where  Type ='"+AppConstants.Order+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) Order By id Limit 1";
////						orderCursor = objSqliteDB.rawQuery(trxCodeQuery, null);
////						if(orderCursor != null && orderCursor.moveToFirst())
////							invoiceNumber = orderCursor.getString(0);
//						
////						objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+invoiceNumber+"'");
//						
//						if(orderCursor!=null && !orderCursor.isClosed())
//							orderCursor.close();
//						
//						if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
//						{
//							trxHeaderDO.trxType = TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER();
//							if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_ADVANCE_ORDER_DELIVERED())
//							{
//								trxHeaderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
//								//isPreseller = true;
//							}
//							else
//								trxHeaderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_PRESALES_ORDER();
//							trxHeaderDO.PreSellerUserCode = trxHeaderDO.userCode;
//							trxHeaderDO.userCode = userId;
//							
//							if(!isPreseller)
//								trxHeaderDO.trxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
//						}
//						
////						if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED() && trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER())
////						{
//							float salesAmount=trxHeaderDO.totalAmount-trxHeaderDO.totalDiscountAmount;
//							
//							updateUserAchievedTarget(salesAmount, trxHeaderDO.userCode, objSqliteDB);
//							
//							//===============Start Updating Monthly Sales amount=========================
//
//							/*
//							 *  Need to change it to statement
//							 */
//							String query ="SELECT *from tblCustomerMonthlySales where  CustomerCode ='"+trxHeaderDO.clientCode+"'";
//							
//							cursor = objSqliteDB.rawQuery(query, null);
//							if(cursor.moveToFirst())
//							{
//								SQLiteStatement stmtUpdateMonthlySalesAmount = objSqliteDB.compileStatement("update tblCustomerMonthlySales set SalesAmount=Cast((Cast(SalesAmount as float)+?) as float) where CustomerCode=?");
//								stmtUpdateMonthlySalesAmount.bindDouble(1, salesAmount);
//								stmtUpdateMonthlySalesAmount.bindString(2, trxHeaderDO.clientCode);
//								stmtUpdateMonthlySalesAmount.execute();
//								stmtUpdateMonthlySalesAmount.close();
//							}
//							else
//							{
//								SQLiteStatement stmtInsertMonthlySalesAmount = objSqliteDB.compileStatement("INSERT INTO tblCustomerMonthlySales(CustomerCode,SalesAmount) VALUES(?,?)");
//								stmtInsertMonthlySalesAmount.bindString(1, trxHeaderDO.clientCode);
//								stmtInsertMonthlySalesAmount.bindDouble(2, salesAmount);
//								stmtInsertMonthlySalesAmount.executeInsert();
//								stmtInsertMonthlySalesAmount.close();
//							}
//							
//							if(cursor!=null && !cursor.isClosed())
//								cursor.close();
//							
//							//===============Close Updating Monthly Sales amount=========================
////						}
//					}
//					
////					if(invoiceNumber == null || invoiceNumber.equalsIgnoreCase(""))
////						invoiceNumber = trxHeaderDO.trxCode;
//					
//					stmtSelect.bindString(1, trxHeaderDO.trxCode);
//					
//					long count = stmtSelect.simpleQueryForLong();
//					
//					stmtSelectLineNODetail.bindString(1, trxHeaderDO.trxCode);
//					
//					long lineNOCount = stmtSelectLineNODetail.simpleQueryForLong();
//					
//					stmtUpdate.bindDouble(1, trxHeaderDO.totalAmount);
//					stmtUpdate.bindDouble(2, trxHeaderDO.totalDiscountAmount);
//					stmtUpdate.bindString(3, trxHeaderDO.trxReasonCode);
//					stmtUpdate.bindLong(4, trxHeaderDO.status);
//					stmtUpdate.bindLong(5, trxHeaderDO.trxStatus);
//					stmtUpdate.bindString(6, trxHeaderDO.approveByCode+"");
//					stmtUpdate.bindString(7, trxHeaderDO.approvedDate+"");
//					stmtUpdate.bindString(8, trxHeaderDO.clientSignature+"");
//					stmtUpdate.bindString(9, trxHeaderDO.salesmanSignature+"");
//					stmtUpdate.bindString(10, "");
//					stmtUpdate.bindString(11, trxHeaderDO.trxCode);
//					stmtUpdate.bindString(12, ""+trxHeaderDO.specialDiscPercent);
//					stmtUpdate.bindString(13, trxHeaderDO.LPONo);
//					stmtUpdate.bindString(14, trxHeaderDO.Narration);
//					stmtUpdate.bindString(15, ""+trxHeaderDO.returnReason);
//					stmtUpdate.bindString(16, ""+trxHeaderDO.rateDiff);
//					stmtUpdate.bindString(17, ""+trxHeaderDO.trxType);
//					stmtUpdate.bindString(18, ""+trxHeaderDO.userCode);
//					stmtUpdate.bindString(19, ""+trxHeaderDO.PreSellerUserCode);
//					stmtUpdate.bindString(20, ""+trxHeaderDO.trxDate);
//					stmtUpdate.bindString(21, trxHeaderDO.trxCode);
//						
//					stmtUpdate.execute();
//					
//					if(count <= 0 )
//					{
//						trxHeaderDO.trxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
//						trxHeaderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
//						
//						for(TrxDetailsDO trxDetailsDO:trxHeaderDO.arrTrxDetailsDOs)
//							trxDetailsDO.trxCode = trxHeaderDO.trxCode;
//						
//						insertOrderDetails_Promo(trxHeaderDO);
//					}
//					else{
//						
//						itemCodes = "";
//								
//						for(TrxDetailsDO trxDetailsDO : trxHeaderDO.arrTrxDetailsDOs)
//						{
//							stmtSelectDetail.bindString(1, trxHeaderDO.trxCode);
//							
//							stmtSelectDetail.bindString(2, trxDetailsDO.itemCode);
//							long countL = stmtSelectDetail.simpleQueryForLong();
//							itemCodes = itemCodes+"'"+trxDetailsDO.itemCode+"'"+",";
//							if(countL <= 0)
//							{
//								trxDetailsDO.lineNo = (int) ++lineNOCount;
//								stmtInsertOrder.bindLong(1,trxDetailsDO.lineNo);
//								stmtInsertOrder.bindString(2, invoiceNumber);
//								stmtInsertOrder.bindString(3, trxDetailsDO.itemCode);
//								stmtInsertOrder.bindString(4, trxHeaderDO.orgCode);
//								stmtInsertOrder.bindString(5, ""+trxDetailsDO.trxReasonCode);
//								stmtInsertOrder.bindString(6, ""+trxDetailsDO.trxDetailsNote);
//								stmtInsertOrder.bindString(7, trxDetailsDO.itemType);
//								stmtInsertOrder.bindDouble(8, trxDetailsDO.basePrice);
//								stmtInsertOrder.bindString(9, trxDetailsDO.UOM);
//								stmtInsertOrder.bindDouble(10, trxDetailsDO.quantityLevel1);
//								stmtInsertOrder.bindDouble(11, trxDetailsDO.quantityLevel2);
//								stmtInsertOrder.bindLong(12, trxDetailsDO.quantityLevel3);
//								stmtInsertOrder.bindLong(13, trxDetailsDO.quantityBU);
//								stmtInsertOrder.bindLong(14, trxDetailsDO.requestedBU);
//								stmtInsertOrder.bindLong(15, trxDetailsDO.approvedBU);
//								stmtInsertOrder.bindLong(16, trxDetailsDO.collectedBU);
//								stmtInsertOrder.bindLong(17, trxDetailsDO.finalBU);
//								stmtInsertOrder.bindDouble(18, trxDetailsDO.priceUsedLevel1);
//								stmtInsertOrder.bindDouble(19, trxDetailsDO.priceUsedLevel2);
//								stmtInsertOrder.bindDouble(20, trxDetailsDO.priceUsedLevel3);
//								stmtInsertOrder.bindDouble(21, trxDetailsDO.totalDiscountPercentage);
//								stmtInsertOrder.bindDouble(22, trxDetailsDO.promotionalDiscountAmount);
//								
//								stmtInsertOrder.bindDouble(23, trxDetailsDO.calculatedDiscountPercentage);
//								stmtInsertOrder.bindDouble(24, trxDetailsDO.calculatedDiscountAmount);
//								stmtInsertOrder.bindDouble(25, trxDetailsDO.userDiscountPercentage);
//								stmtInsertOrder.bindDouble(26, trxDetailsDO.userDiscountAmount);
//								stmtInsertOrder.bindString(27, trxDetailsDO.itemDescription);
//								stmtInsertOrder.bindLong(28, trxDetailsDO.affectedStock);
//								stmtInsertOrder.bindLong(29, trxDetailsDO.status);
//								
//								stmtInsertOrder.bindString(30, ""+trxDetailsDO.promoID);
//								stmtInsertOrder.bindString(31, ""+trxDetailsDO.promoType);
//								stmtInsertOrder.bindString(32, ""+trxHeaderDO.createdOn);
//								stmtInsertOrder.bindLong(33, trxDetailsDO.trxStatus);
//								stmtInsertOrder.bindString(34, ""+trxDetailsDO.expiryDate);
//								stmtInsertOrder.bindLong(35, trxDetailsDO.relatedLineID);
//								stmtInsertOrder.bindString(36, trxDetailsDO.itemGroupLevel5);
//								
//								stmtInsertOrder.bindLong(37, trxDetailsDO.taxType);
//								stmtInsertOrder.bindLong(38, trxDetailsDO.suggestedBU);
//								stmtInsertOrder.bindString(39, ""+trxDetailsDO.pushedOn);
//								stmtInsertOrder.bindString(40, ""+trxDetailsDO.reason);
//								stmtInsertOrder.bindLong(41, trxDetailsDO.cancelledQuantity);
//								stmtInsertOrder.bindLong(42, trxDetailsDO.inProcessQuantity);
//								stmtInsertOrder.bindLong(43, trxDetailsDO.shippedQuantity);
//								stmtInsertOrder.bindLong(44, trxDetailsDO.missedBU);
//								stmtInsertOrder.bindString(45, ""+trxDetailsDO.batchCode);
//								
//								stmtInsertOrder.executeInsert();
//							}
//							else
//							{
//								stmtUpdateOrder.bindString(1, trxDetailsDO.trxReasonCode);
//								stmtUpdateOrder.bindString(2, trxDetailsDO.trxDetailsNote);
//								stmtUpdateOrder.bindString(3, trxDetailsDO.itemType);
//								stmtUpdateOrder.bindDouble(4, trxDetailsDO.quantityLevel1);
//								stmtUpdateOrder.bindDouble(5, trxDetailsDO.quantityLevel2);
//								stmtUpdateOrder.bindLong(6, trxDetailsDO.quantityLevel3);
//								stmtUpdateOrder.bindLong(7, trxDetailsDO.quantityBU);
//								stmtUpdateOrder.bindLong(8, trxDetailsDO.requestedBU);
//								stmtUpdateOrder.bindLong(9, trxDetailsDO.approvedBU);
//								stmtUpdateOrder.bindLong(10, trxDetailsDO.collectedBU);
//								stmtUpdateOrder.bindLong(11, trxDetailsDO.finalBU);
//								stmtUpdateOrder.bindDouble(12, trxDetailsDO.priceUsedLevel1);
//								stmtUpdateOrder.bindDouble(13, trxDetailsDO.priceUsedLevel2);
//								stmtUpdateOrder.bindDouble(14, trxDetailsDO.priceUsedLevel3);
//								stmtUpdateOrder.bindDouble(15, trxDetailsDO.totalDiscountPercentage);
//								stmtUpdateOrder.bindDouble(16, trxDetailsDO.promotionalDiscountAmount);
//								stmtUpdateOrder.bindLong(17, trxDetailsDO.affectedStock);
//								stmtUpdateOrder.bindLong(18, trxDetailsDO.status);
//								stmtUpdateOrder.bindLong(19, trxDetailsDO.trxStatus);
//								stmtUpdateOrder.bindString(20, trxDetailsDO.expiryDate);
//								stmtUpdateOrder.bindString(21, trxDetailsDO.itemGroupLevel5);
//								stmtUpdateOrder.bindLong(22, trxDetailsDO.suggestedBU);
//								stmtUpdateOrder.bindString(23, trxDetailsDO.reason);
//								stmtUpdateOrder.bindLong(24, trxDetailsDO.cancelledQuantity);
//								stmtUpdateOrder.bindLong(25, trxDetailsDO.inProcessQuantity);
//								stmtUpdateOrder.bindLong(26, trxDetailsDO.shippedQuantity);
//								stmtUpdateOrder.bindLong(27, trxDetailsDO.missedBU);
//								stmtUpdateOrder.bindString(28, trxDetailsDO.UOM);
//								stmtUpdateOrder.bindString(29, invoiceNumber);
//								stmtUpdateOrder.bindString(30, trxHeaderDO.trxCode);
//								stmtUpdateOrder.bindString(31, trxDetailsDO.itemCode);
//								stmtUpdateOrder.execute();
//							}
//						}	
//						
//						try
//						{
//							if(itemCodes!=null && itemCodes.length()>0)
//							{
//								itemCodes = itemCodes.substring(0,itemCodes.length()-1);
//								
//								SQLiteStatement stmtUpdateDetail = objSqliteDB.compileStatement("UPDATE tblTrxDetail SET QuantityLevel1=?,QuantityBU=?,RequestedBU=? WHERE TrxCode=? AND ItemCode NOT IN ("+itemCodes+")");
//								stmtUpdateDetail.bindDouble(1,0);
//								stmtUpdateDetail.bindLong(2,0);
//								stmtUpdateDetail.bindLong(3,0);
//								
////								stmtUpdateDetail.bindString(4, trxHeaderDO.trxCode);
//								stmtUpdateDetail.bindString(4, invoiceNumber);
//								
//								stmtUpdateDetail.execute();
//								stmtUpdateDetail.close();
//								
//							}
//						}
//						catch(Exception e)
//						{
//							e.printStackTrace();
//						}
//					}
//				}
//				stmtSelect.close();
//				stmtSelectDetail.close();
//				stmtSelectLineNODetail.close();
//				stmtUpdate.close();
//				stmtUpdateOrder.close();
//				stmtInsertOrder.close();
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
//				
//				if(objSqliteDB != null)
//					objSqliteDB.close();
//			}
//			return invoiceNumber;
//		}
//	}
}
