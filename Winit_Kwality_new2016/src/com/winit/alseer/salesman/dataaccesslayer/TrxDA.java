package com.winit.alseer.salesman.dataaccesslayer;

import java.util.HashMap;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.BrandDO;
import com.winit.alseer.salesman.dataobject.HHInventryQTDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.UOMConversionDO;
import com.winit.alseer.salesman.dataobject.UOMConversionFactorDO;
import com.winit.sfa.salesman.MyApplication;

public class TrxDA {
	
	/*
	 *  1st Object "HashMap<String,Vector<TrxDetailsDO>>"
	 *  2nd Object "HashMap<String,TrxDetailsDO>"
	 *  3rd Object is brand info 
	 */
	
	public Object[] getCartDetails(String clientCode, HashMap<String, UOMConversionDO> hashUomConveriosn, HashMap<String, HHInventryQTDO> hmInventory) 
	{
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			HashMap<String,Vector<TrxDetailsDO>> hmCartProducts  = new HashMap<String, Vector<TrxDetailsDO>>(); 
			HashMap<String,TrxDetailsDO> hmItems  = new HashMap<String,TrxDetailsDO>(); 
			HashMap<String,String> hmBrandInfo  = new HashMap<String,String>(); 
			Vector<TrxDetailsDO> vecDetails;
			Object[] objects = new Object[3];
			try {
				
				mDatabase = DatabaseHelper.openDataBase();
				String query = "SELECT DISTINCT TX.LineNo,TX.TrxCode,TX.ItemCode,TX.OrgCode,TX.TrxReasonCode, " +
						"TX.TrxDetailsNote,TX.ItemType,TX.BasePrice,TX.UOM,TX.QuantityLevel1,TX.QuantityLevel2," +
						"TX.QuantityLevel3,TX.QuantityBU,TX.RequestedBU,TX.ApprovedBU,TX.CollectedBU,TX.FinalBU," +
						"TX.PriceUsedLevel1," +
						"TX.PriceUsedLevel2,TX.PriceUsedLevel3,TX.TaxPercentage," +
						"TX.TotalDiscountPercentage,TX.TotalDiscountAmount,TX.CalculatedDiscountPercentage,TX.CalculatedDiscountAmount, " +
						"TX.UserDiscountPercentage,TX.UserDiscountAmount,TX.ItemDescription,TX.ItemAltDescription,TX.DistributionCode, " +
						"TX.AffectedStock,TX.TRXStatus,TX.PromoID,TX.PromoType,TX.CreatedOn,TX.TRXStatus," +
						"TX.ExpiryDate,TX.RelatedLineID," +
						"TX.ItemGroupLevel5,TX.TaxType,TX.SuggestedBU,TX.PushedOn," +
						"TX.ModifiedDate,TX.ModifiedTime,TX.Reason," +
						"TX.CancelledQuantity,TX.InProcessQuantity,TX.ShippedQuantity,TX.MissedBU,TX.BatchNumber,TB.BrandId,TB.BrandName" +
						" FROM tblTrxDetail TX INNER JOIN tblProducts TP ON TP.ItemCode = TX.Itemcode INNER JOIN tblBrand TB ON TB.BrandId = TP.Brand WHERE trxCode IN (SELECT trxCode FROM tblTrxHeader WHERE ClientCode="+clientCode+" AND TrxType ='"+TrxHeaderDO.get_TRXTYPE_CART()+"')"; 
				
				cursor = mDatabase.rawQuery(query, null);
				String lastBrandId=null;
				BrandDO brandDO =null;
				if (cursor.moveToFirst())
				{
					do {
						
						TrxDetailsDO trxDetailDo 				= 	new TrxDetailsDO();
						
						trxDetailDo.lineNo					= cursor.getInt(0);
						trxDetailDo.trxCode					= cursor.getString(1);
						trxDetailDo.itemCode					= cursor.getString(2);
						trxDetailDo.orgCode					= cursor.getString(3);
						trxDetailDo.trxReasonCode				= cursor.getString(4);
						trxDetailDo.trxDetailsNote			= cursor.getString(5);
						trxDetailDo.itemType					= cursor.getString(6);
						trxDetailDo.basePrice					= cursor.getFloat(7);
						trxDetailDo.UOM						= cursor.getString(8);
						trxDetailDo.quantityLevel1			= cursor.getFloat(9);
						trxDetailDo.quantityLevel2			= cursor.getFloat(10);
						trxDetailDo.quantityLevel3			= cursor.getInt(11);
						
						trxDetailDo.quantityBU				= cursor.getInt(12);
						trxDetailDo.requestedBU				= cursor.getInt(13);
						
						trxDetailDo.approvedBU				= cursor.getInt(14);
						trxDetailDo.collectedBU				= cursor.getInt(15);
						
						trxDetailDo.finalBU					= cursor.getInt(16);
						trxDetailDo.priceUsedLevel1			= cursor.getFloat(17);
						trxDetailDo.priceUsedLevel2			= cursor.getFloat(18);
						trxDetailDo.priceUsedLevel3			= cursor.getFloat(19);
						
						trxDetailDo.taxPercentage				= cursor.getFloat(20);
						trxDetailDo.totalDiscountPercentage	= cursor.getFloat(21);
						trxDetailDo.totalDiscountAmount		= cursor.getFloat(22);
						trxDetailDo.calculatedDiscountPercentage= cursor.getFloat(23);
						trxDetailDo.calculatedDiscountAmount	= cursor.getFloat(24);
						
						
						trxDetailDo.userDiscountPercentage	= cursor.getFloat(25);
						trxDetailDo.userDiscountAmount		= cursor.getFloat(26);
						trxDetailDo.itemDescription			= cursor.getString(27);
						trxDetailDo.itemAltDescription		= cursor.getString(28);
						trxDetailDo.distributionCode			= cursor.getString(29);
						trxDetailDo.affectedStock				= cursor.getInt(30);
						trxDetailDo.status					= cursor.getInt(31);
						trxDetailDo.promoID					= cursor.getInt(32);
						trxDetailDo.promoType					= cursor.getString(33);
						trxDetailDo.createdOn					= cursor.getString(34);
						trxDetailDo.trxStatus					= cursor.getInt(35);
						trxDetailDo.expiryDate				= cursor.getString(36);
						
						trxDetailDo.relatedLineID				= cursor.getInt(37);
						trxDetailDo.itemGroupLevel5			= cursor.getString(38);
						trxDetailDo.taxType					= cursor.getInt(39);
						trxDetailDo.suggestedBU				= cursor.getInt(40);
						trxDetailDo.pushedOn					= cursor.getString(41);
						trxDetailDo.modifiedDate				= cursor.getString(42);
						trxDetailDo.modifiedTime				= cursor.getString(43);
						
						trxDetailDo.reason					= cursor.getString(44);
						trxDetailDo.cancelledQuantity			= cursor.getInt(45);
						trxDetailDo.inProcessQuantity			= cursor.getInt(46);
						trxDetailDo.shippedQuantity			= cursor.getInt(47);
						trxDetailDo.missedBU					= cursor.getInt(48);
						trxDetailDo.batchCode					= cursor.getString(49);
						int quantity						= trxDetailDo.missedBU+trxDetailDo.quantityBU;
						String brandId						= cursor.getString(50);
						String brandName					= cursor.getString(51);
						
						//deciding UOM level
						/*if(trxDetailDo.UOM.equalsIgnoreCase(TrxDetailsDO.getItemUomLevel1()))
							trxDetailDo.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT1;
						else if(trxDetailDo.UOM.equalsIgnoreCase(TrxDetailsDO.getItemUomLevel2()))
							trxDetailDo.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT2;
						else if(trxDetailDo.UOM.equalsIgnoreCase(TrxDetailsDO.getItemUomLevel3()))
							trxDetailDo.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT3;*/
						if(trxDetailDo.requestedSalesBU>0){
							int availQty =0;
							if(hmInventory.containsKey(trxDetailDo.itemCode))
								 availQty = hmInventory.get(trxDetailDo.itemCode).totalQt;
							UOMConversionDO uomConversionDO = hashUomConveriosn.get(trxDetailDo.itemCode);
							calculateOtherQty(trxDetailDo, uomConversionDO, availQty,quantity);
						}
						//==============================Start fetching all available UOM's for this item======================
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
						
						if(!hmCartProducts.containsKey(brandId))
							vecDetails = new Vector<TrxDetailsDO>();
						else
							vecDetails = hmCartProducts.get(brandId);
							
						vecDetails.add(trxDetailDo);
						
						hmCartProducts.put(brandId, vecDetails);
						hmBrandInfo.put(brandId,brandName);
						hmItems.put(trxDetailDo.itemCode,trxDetailDo);
						
					} while (cursor.moveToNext());
					
					if (cursor != null && !cursor.isClosed())
						cursor.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();
				if (mDatabase != null)
					mDatabase.close();
				objects[0] = hmCartProducts;
				objects[1] = hmItems;
				objects[2] = hmBrandInfo;
			}
			return objects;
		}
	}
	private void calculateOtherQty(TrxDetailsDO trxDetailsDO,UOMConversionDO uomConversionDO,int availInventoryQty,int quanity){
		switch (trxDetailsDO.uomLevelUsed) {
		case TrxDetailsDO.ITEM_UOM_LEVELINT1:
			trxDetailsDO.requestedSalesBU =quanity/uomConversionDO.conversion13;
			if(availInventoryQty<quanity)
				trxDetailsDO.missedBU = (int) (quanity-availInventoryQty);
			
			if(uomConversionDO.conversion13!=0)
				trxDetailsDO.quantityLevel1 = (quanity-trxDetailsDO.missedBU)/uomConversionDO.conversion13;
			else
				trxDetailsDO.quantityLevel1 = (quanity-trxDetailsDO.missedBU);
			trxDetailsDO.quantityLevel2=0;
			trxDetailsDO.quantityLevel3=0;
			break;
		case TrxDetailsDO.ITEM_UOM_LEVELINT2:
			trxDetailsDO.requestedSalesBU =quanity/uomConversionDO.conversion12;
			if(availInventoryQty<quanity)
				trxDetailsDO.missedBU = (int) (quanity-availInventoryQty);
			if(uomConversionDO.conversion12!=0)
				trxDetailsDO.quantityLevel2 = (quanity-trxDetailsDO.missedBU)/uomConversionDO.conversion12;
			else
				trxDetailsDO.quantityLevel2 = (quanity-trxDetailsDO.missedBU);
			trxDetailsDO.quantityLevel1=0;
			trxDetailsDO.quantityLevel3=0;
			break;
		case TrxDetailsDO.ITEM_UOM_LEVELINT3:
			trxDetailsDO.requestedSalesBU =quanity;
			if(availInventoryQty<quanity)
				trxDetailsDO.missedBU = (int) (quanity-availInventoryQty);
			trxDetailsDO.quantityLevel3 = (int) (quanity - trxDetailsDO.missedBU);
			trxDetailsDO.quantityLevel1=0;
			trxDetailsDO.quantityLevel2=0;
			break;
		default:
			break;
		}
		trxDetailsDO.quantityBU = (int) (quanity-trxDetailsDO.missedBU);
	}
	public static void updateCartDetails(TrxHeaderDO trxHeaderDO)
	{
		
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			try 
			{
				
				mDatabase = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdateOrder = mDatabase.compileStatement("UPDATE tblTrxDetail SET QuantityLevel1=?, QuantityLevel2=?, QuantityLevel3=?, QuantityBU=?, RequestedBU=?, " +
						"PriceUsedLevel1=?, PriceUsedLevel2=?, PriceUsedLevel3=?, TotalDiscountPercentage=?,TotalDiscountAmount=?WHERE TrxCode = ? AND ItemCode=?");
				
				for(TrxDetailsDO trxDetailsDO : trxHeaderDO.arrTrxDetailsDOs)
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
				
				stmtUpdateOrder.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			if (mDatabase != null)
				mDatabase.close();
			}
		}
		
	}
	
}
