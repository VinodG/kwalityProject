package com.winit.alseer.salesman.adapter;

import httpimage.HttpImageManager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.CustomEditText;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.UOMConversionFactorDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.MyApplicationNew;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;
import com.winit.sfa.salesman.SalesmanOrderPreview;
import com.winit.sfa.salesman.SavedOrderSummaryDetail;

public class OrderPreviewAdapter extends BaseAdapter
{
	private Context context;
	private boolean isMissedOrder;
	public ArrayList<TrxDetailsDO> arrTrxDetailsDOs   =null;
	private View view;
	private boolean isEditable;
	private JourneyPlanDO mallsDetails;
	private int trxType = 0;
	private int focItemCount = 0;
	private double invoiceAmount = 0;

	private String UNITS_LOCK = "UNITS_LOCK";

	public OrderPreviewAdapter(Context context, ArrayList<TrxDetailsDO> arrTrxDetailsDOs,boolean isMissedOrder,boolean isEditable, int trxType)
	{
		this.context 		 = context;
		this.arrTrxDetailsDOs = arrTrxDetailsDOs;
		this.isMissedOrder	= isMissedOrder;
		this.isEditable = isEditable;
		this.trxType = trxType;
	}

	public OrderPreviewAdapter(Context context, ArrayList<TrxDetailsDO> arrTrxDetailsDOs,boolean isEditable,JourneyPlanDO mallsDetails, int trxType)
	{
		this.context 		 = context;
		this.arrTrxDetailsDOs = arrTrxDetailsDOs;
		this.isEditable = isEditable;
		this.mallsDetails = mallsDetails;
		this.trxType = trxType;
	}

	@Override
	public int getCount()
	{
		if(arrTrxDetailsDOs != null && arrTrxDetailsDOs.size() > 0)
			return arrTrxDetailsDOs.size();

		return 0;
	}

	@Override
	public Object getItem(int position)
	{
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final TrxDetailsDO objItem = arrTrxDetailsDOs.get(position);
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.order_preview, null);
			viewHolder.tvOrderedItemName = (TextView) convertView.findViewById(R.id.tvOrderedItemName);
			viewHolder.tvOrderedItemDesc = (TextView) convertView.findViewById(R.id.tvOrderedItemDesc);
			viewHolder.etQuantity = (CustomEditText) convertView.findViewById(R.id.etQuantity);
			viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
			viewHolder.tvItemUOM = (TextView) convertView.findViewById(R.id.tvItemUOM);
			viewHolder.tvTotalPrice = (TextView) convertView.findViewById(R.id.tvTotalPrice);
			viewHolder.tvInvoiceAmount = (TextView) convertView.findViewById(R.id.tvInvoiceAmount);
			viewHolder.tvDiscount = (TextView) convertView.findViewById(R.id.tvDiscount);

			viewHolder.tvOrderedItemMeasurment = (TextView) convertView.findViewById(R.id.tvOrderedItemMeasurment);
			viewHolder.tvOrderedItemDesc2 = (TextView) convertView.findViewById(R.id.tvOrderedItemDesc2);
			viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
			viewHolder.ivSep = (ImageView) convertView.findViewById(R.id.ivSep);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.etQuantity.setVisibility(View.VISIBLE);
		viewHolder.ivSep.setVisibility(View.VISIBLE);
		viewHolder.tvOrderedItemName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		String imageURL = "brandimages/" + objItem.itemGroupLevel5 + ".png";
		final Uri uri = Uri.parse(imageURL);
		if (uri != null) {
			Bitmap bitmap = getHttpImageManager().loadImage(
					new HttpImageManager.LoadRequest(uri, viewHolder.ivImage, imageURL));
			if (bitmap != null) {
				viewHolder.ivImage.setImageBitmap(bitmap);
			}
		}
		viewHolder.tvItemUOM.setText(objItem.UOM);
		float stmtdis= objItem. calculatedDiscountPercentage;
		float promodis= objItem.totalDiscountPercentage;

//		float discount =objItem.statementDiscountAmnt+objItem.promotionalDiscountAmount;
		float discount  = ((objItem.calculatedDiscountPercentage+objItem.totalDiscountPercentage)*objItem.CSPrice * objItem.quantityBU/100);;

		if (isMissedOrder)
			viewHolder.tvItemUOM.setText(TrxDetailsDO.ITEM_UOM_LEVEL3);
		viewHolder.tvItemUOM.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showItemUOM(v, objItem);
			}
		});

		viewHolder.tvOrderedItemName.setText(objItem.itemCode);
		viewHolder.tvOrderedItemMeasurment.setText(objItem.itemCode);
		viewHolder.tvOrderedItemDesc2.setText(objItem.brandName);

		LogUtils.debug("missed_bu_itemCode", "" + objItem.itemCode);
		LogUtils.debug("missed_bu_isMissedOrder", "" + isMissedOrder);
		LogUtils.debug("missed_bu_missedBU", "" + objItem.missedBU);
		if (isMissedOrder)
			viewHolder.etQuantity.setText("" + objItem.missedBU);
		else if (objItem.UOM.equalsIgnoreCase(TrxDetailsDO.ITEM_UOM_LEVEL3))
			viewHolder.etQuantity.setText("" + (int) objItem.quantityLevel1);
		else
			viewHolder.etQuantity.setText("" + ((BaseActivity) context).decimalFormat.format(objItem.quantityLevel1));

		focItemCount += objItem.quantityLevel1;

		/*if (isMissedOrder)
			viewHolder.etQuantity.setText("" + objItem.missedBU);
		else if (objItem.UOM.equalsIgnoreCase(TrxDetailsDO.ITEM_UOM_LEVEL3))
			viewHolder.etQuantity.setText("" + (int) objItem.quantityLevel1);
		else {
			if (objItem.missedBU > 0)
			{
				viewHolder.etQuantity.setText("" + objItem.missedBU);
				focItemCount += objItem.missedBU;
			} else {
				focItemCount += objItem.quantityLevel1;
				viewHolder.etQuantity.setText("" + (int) objItem.quantityLevel1);
			}
		}
*/

		viewHolder.tvPrice.setText(""+((BaseActivity)context).amountFormate.format(objItem.priceUsedLevel1));

//		calculateDiscount(objItem);
		LogUtils.errorLog("rate_Diff", "totalDiscountAmount:"+objItem.totalDiscountAmount);
		LogUtils.errorLog("rate_Diff", "promotionalDiscountAmount:"+objItem.promotionalDiscountAmount);
		if(trxType  == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())//tblProducts
		{
			float dis  = ((objItem.calculatedDiscountPercentage+objItem.totalDiscountPercentage)*objItem.EAPrice * objItem.quantityBU/100);;
			viewHolder.tvTotalPrice.setText(""+((BaseActivity)context).amountFormate.format(objItem.EAPrice * objItem.quantityBU));
//			viewHolder.tvInvoiceAmount.setText(""+((BaseActivity)context).amountFormate.format((objItem.EAPrice * objItem.quantityBU)-objItem.totalDiscountAmount - objItem.promotionalDiscountAmount- objItem.statementDiscountAmnt));
//			viewHolder.tvInvoiceAmount.setText(""+((BaseActivity)context).amountFormate.format((objItem.EAPrice * objItem.quantityBU)-objItem.calculatedDiscountAmount));
			viewHolder.tvInvoiceAmount.setText(""+((BaseActivity)context).amountFormate.format((objItem.EAPrice * objItem.quantityBU)-dis));
//			viewHolder.tvDiscount.setText(""+ ((BaseActivity)context).amountFormate.format(objItem.totalDiscountAmount + objItem.promotionalDiscountAmount));
//			viewHolder.tvDiscount.setText(""+ ((BaseActivity)context).amountFormate.format(objItem.calculatedDiscountAmount));

			viewHolder.tvDiscount.setText(""+ ((BaseActivity)context).amountFormate.format(dis));
		}
		else if(trxType  == TrxHeaderDO.get_TRXTYPE_MISSED_ORDER())//tblProducts
		{
//			float discountAmount = (objItem.CSPrice*objItem.calculatedDiscountPercentage)/100.0f;
			float discountAmount = (objItem.CSPrice*objItem.calculatedDiscountPercentage+objItem.promotionalDiscountAmount)/100.0f;
			viewHolder.tvTotalPrice.setText(""+((BaseActivity)context).amountFormate.format(objItem.CSPrice * objItem.missedBU));
			viewHolder.tvInvoiceAmount.setText(""+((BaseActivity)context).amountFormate.format((objItem.CSPrice * objItem.missedBU)-(discountAmount * objItem.missedBU)));
			viewHolder.tvDiscount.setText(""+ ((BaseActivity)context).amountFormate.format(discountAmount * objItem.missedBU));
		}
		else
		{
			viewHolder.tvTotalPrice.setText(""+((BaseActivity)context).amountFormate.format(objItem.CSPrice * objItem.quantityBU));
//			if((objItem.CSPrice * objItem.quantityBU) > (objItem.promotionalDiscountAmount))
//			{
				float dis= ((objItem.calculatedDiscountPercentage+objItem.totalDiscountPercentage)*objItem.CSPrice * objItem.quantityBU/100);
//				float discount =objItem.calculatedDiscountAmount+objItem.promotionalDiscountAmount;
				//viewHolder.tvInvoiceAmount.setText(""+((BaseActivity)context).amountFormate.format((objItem.CSPrice * objItem.quantityBU)-objItem.totalDiscountAmount - objItem.promotionalDiscountAmount- objItem.statementDiscountAmnt));
//				viewHolder.tvInvoiceAmount.setText(""+((BaseActivity)context).amountFormate.format((objItem.CSPrice * objItem.quantityBU)-objItem.calculatedDiscountAmount));
				viewHolder.tvInvoiceAmount.setText(""+((BaseActivity)context).amountFormate.format((objItem.CSPrice * objItem.quantityBU)-dis));
				//	viewHolder.tvDiscount.setText(""+ ((BaseActivity)context).amountFormate.format(objItem.totalDiscountAmount + objItem.promotionalDiscountAmount+objItem.statementDiscountAmnt));
//				viewHolder.tvDiscount.setText(""+ ((BaseActivity)context).amountFormate.format(objItem.calculatedDiscountAmount));
//				viewHolder.tvDiscount.setText(""+ ((BaseActivity)context).amountFormate.format((objItem.calculatedDiscountPercentage+objItem.totalDiscountPercentage)*objItem.CSPrice * objItem.quantityBU));
				viewHolder.tvDiscount.setText(""+ ((BaseActivity)context).amountFormate.format(dis));
//			}
//			else
//			{
//				viewHolder.tvInvoiceAmount.setText("0");
//				viewHolder.tvDiscount.setText("0");
//			}
/*
			if(trxType != TrxHeaderDO.get_TYPE_FREE_DELIVERY())
				invoiceAmount += (objItem.CSPrice * objItem.quantityBU)-objItem.totalDiscountAmount - objItem.promotionalDiscountAmount-objItem.statementDiscountAmnt;
			else
				invoiceAmount += (objItem.CSPrice * objItem.quantityBU);
			if (objItem.missedBU > 0)
			{
				viewHolder.tvTotalPrice.setText(""+((BaseActivity)context).amountFormate.format(objItem.CSPrice * objItem.missedBU));
			} else {
				viewHolder.tvTotalPrice.setText(""+((BaseActivity)context).amountFormate.format(objItem.CSPrice * objItem.quantityBU));
			}
			float qty =objItem.missedBU>0 ? objItem.missedBU :objItem.quantityBU;
			if((objItem.CSPrice *  qty  > (objItem.promotionalDiscountAmount)))
			{
				//viewHolder.tvInvoiceAmount.setText(""+((BaseActivity)context).amountFormate.format((objItem.CSPrice * objItem.quantityBU)-objItem.totalDiscountAmount - objItem.promotionalDiscountAmount- objItem.statementDiscountAmnt));
				viewHolder.tvInvoiceAmount.setText(""+((BaseActivity)context).amountFormate.format((objItem.CSPrice * qty)-objItem.calculatedDiscountAmount));
				//	viewHolder.tvDiscount.setText(""+ ((BaseActivity)context).amountFormate.format(objItem.totalDiscountAmount + objItem.promotionalDiscountAmount+objItem.statementDiscountAmnt));
				viewHolder.tvDiscount.setText(""+ ((BaseActivity)context).amountFormate.format(objItem.calculatedDiscountAmount));
			}
			else
			{
				viewHolder.tvInvoiceAmount.setText("0");
				viewHolder.tvDiscount.setText("0");
			}*/

			if(trxType != TrxHeaderDO.get_TYPE_FREE_DELIVERY())
//				invoiceAmount += (objItem.CSPrice * objItem.quantityBU)-objItem.totalDiscountAmount - objItem.promotionalDiscountAmount-objItem.statementDiscountAmnt;
				invoiceAmount += (objItem.CSPrice * objItem.quantityBU)-(objItem.calculatedDiscountAmount+objItem.promotionalDiscountAmount);
			else
				invoiceAmount += (objItem.CSPrice * objItem.quantityBU);

		}

		viewHolder.tvOrderedItemDesc.setText(((""+objItem.itemDescription)).trim());
		((BaseActivity)context).setTypeFaceRobotoNormal((ViewGroup) convertView);
		viewHolder.tvOrderedItemDesc2.setTypeface(AppConstants.Roboto_Condensed_Bold);
		viewHolder.tvOrderedItemName.setTypeface(AppConstants.Roboto_Condensed_Bold);

		viewHolder.tvItemUOM.setTag(R.string.request_Bu,""+((int)objItem.requestedSalesBU));//casting to int to remove decimal

		viewHolder.etQuantity.setTag(R.string.key_product, objItem);

		viewHolder.etQuantity.setTag(R.string.key_uom,viewHolder.tvItemUOM);

		viewHolder.etQuantity.setTag(R.string.key_price,viewHolder.tvPrice);
		viewHolder.etQuantity.setTag(R.string.key_gsv,viewHolder.tvTotalPrice);
		viewHolder.etQuantity.setTag(R.string.key_discount,viewHolder.tvDiscount);
		viewHolder.etQuantity.setTag(R.string.key_niv,viewHolder.tvInvoiceAmount);

		viewHolder.tvItemUOM.setTag(R.string.key_details,viewHolder.etQuantity);
		
		/*if(isEditable)
		{
			if(objItem.promoType.equalsIgnoreCase(TrxDetailsDO.get_TRX_FOC_ITEM())){
				viewHolder.etQuantity.setEnabled(false);
				viewHolder.tvItemUOM.setEnabled(false);
			}else{
				viewHolder.etQuantity.setEnabled(true);
				viewHolder.tvItemUOM.setEnabled(true);
			}
		}
		else
		{
			viewHolder.etQuantity.setEnabled(false);
			viewHolder.tvItemUOM.setEnabled(false);
			
			viewHolder.tvItemUOM.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			viewHolder.etQuantity.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}*/
		viewHolder.etQuantity.setEnabled(false);
		viewHolder.tvItemUOM.setEnabled(false);
		viewHolder.etQuantity.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (hasFocus) {
					view  = v;
					viewHolder.etQuantity.addTextChangedListener(new TextChangeListener());
					LogUtils.debug("onFocusChange","calling");
//					view  = v;
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							try {
								((BaseActivity)context).hideKeyBoard(viewHolder.etQuantity);
								boolean isCentre = true;
								((BaseActivity) context).onKeyboardFocus(viewHolder.etQuantity,objItem.uomLevelUsed,isCentre);
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}, 100);
				}
//				else
//					view = null;

			}
		});


		return convertView;
	}

	private void calculateDiscount(TrxDetailsDO objItem)
	{
		if(mallsDetails != null)
		{
			float discount = (objItem.CSPrice * objItem.quantityBU * StringUtils.getFloat(mallsDetails.PromotionalDiscount))/100;
			objItem.totalDiscountAmount = objItem.totalDiscountAmount + discount+objItem.statementDiscountAmnt;
		}
	}

	private void showItemUOM(final View v, final TrxDetailsDO trxDetailsDO)
	{
		if(trxDetailsDO.arrUoms.size()>0 ){
			CustomBuilder customBuilder = new CustomBuilder(context, "Select UOM", false);
			customBuilder.setSingleChoiceItems(trxDetailsDO.arrUoms, trxDetailsDO.UOM, new CustomBuilder.OnClickListener()
			{
				@Override
				public void onClick(CustomBuilder builder, Object selectedObject)
				{
					v.setTag((String)selectedObject);
					((TextView)v).setText((String)selectedObject);
					trxDetailsDO.UOM = (String)selectedObject;
					/*if(trxDetailsDO.UOM.equalsIgnoreCase(TrxDetailsDO.getItemUomLevel1()))
						trxDetailsDO.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT1;
					else if(trxDetailsDO.UOM.equalsIgnoreCase(TrxDetailsDO.getItemUomLevel2()))
						trxDetailsDO.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT2;
					else if(trxDetailsDO.UOM.equalsIgnoreCase(TrxDetailsDO.getItemUomLevel3()))
						trxDetailsDO.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT3;*/
					handleText((String)v.getTag(R.string.request_Bu),trxDetailsDO,(View)v.getTag(R.string.key_details));

					builder.dismiss();
				}
			});
			customBuilder.show();
		}
	}

	private void handleText(CharSequence s,TrxDetailsDO obj,View updatedView) {

		synchronized (UNITS_LOCK) {
			try {
				TrxDetailsDO objItem = null;
				if (view != null) {
					objItem = (TrxDetailsDO) view.getTag(R.string.key_product);
				}
				else if(obj!=null)
					objItem = obj;
				if (objItem != null)
				{
					if(s==null)
						s="";
					objItem.requestedSalesBU = StringUtils.getFloat(s.toString());
					UOMConversionFactorDO uomFactorDO = objItem.hashArrUoms.get(objItem.itemCode+objItem.UOM+"");
					int quanity=0;
					if(uomFactorDO!=null)
					{
						quanity =(int) (uomFactorDO.eaConversion*objItem.requestedSalesBU);

						if(context instanceof SalesmanOrderPreview)
							objItem.missedBU = ((SalesmanOrderPreview)context).isInventoryAvail(objItem,quanity);
						else
							objItem.missedBU = ((SavedOrderSummaryDetail)context).isInventoryAvail(objItem,quanity);
						if(uomFactorDO!=null && uomFactorDO.eaConversion!=0)
							objItem.quantityLevel1 = (quanity-objItem.missedBU)/uomFactorDO.eaConversion;
						else
							objItem.quantityLevel1 = (quanity-objItem.missedBU);
						objItem.quantityLevel2=0;
						objItem.quantityLevel3=0;
					}

					objItem.quantityBU = quanity-objItem.missedBU;

					if(view!=null)
						updatedView = view;

					if(context instanceof SalesmanOrderPreview)
						((SalesmanOrderPreview)context).calculatePrice(updatedView);
					else
						((SavedOrderSummaryDetail)context).calculatePrice();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}


	/**
	 * method to refresh the List View
	 * @param vecOrderPreview
	 */
	@SuppressWarnings("unchecked")
	public void refresh(ArrayList<TrxDetailsDO> arrTrxDetailsDOs)
	{
		this.arrTrxDetailsDOs = arrTrxDetailsDOs;
		this.notifyDataSetChanged();
	}

	public HttpImageManager getHttpImageManager() {
		return ((MyApplicationNew) ((Activity) context)
				.getApplication()).getHttpImageManager();
	}
	class TextChangeListener implements TextWatcher {

		public TextChangeListener() {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
								  int count) {
			try {
				handleText(s,null,null);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		@Override
		public void afterTextChanged(Editable s) {
//				refreshListItems(view);
		}

	}

	public void refreshListItems(View view)
	{

		if(view!=null)
		{
			TrxDetailsDO objDo  = (TrxDetailsDO) view.getTag(R.string.key_product);

			((TextView)view.getTag(R.string.key_discount)).setText(""+ ((BaseActivity)context).amountFormate.format(objDo.totalDiscountAmount));
			((TextView)view.getTag(R.string.key_price)).setText(""+((BaseActivity)context).amountFormate.format(objDo.priceUsedLevel1));
			((TextView)view.getTag(R.string.key_gsv)).setText(""+((BaseActivity)context).amountFormate.format(objDo.priceUsedLevel1 * objDo.quantityLevel1));
			((TextView)view.getTag(R.string.key_niv)).setText(""+((BaseActivity)context).amountFormate.format((objDo.priceUsedLevel1 * objDo.quantityLevel1)-objDo.totalDiscountAmount));

			((TextView)view.getTag(R.string.key_uom)).setTag(R.string.request_Bu,""+objDo.requestedSalesBU);
		}
	}

	public int getFOCQuantity()
	{
		return focItemCount;
	}

	public class ViewHolder{
		TextView tvOrderedItemName 	;
		TextView tvOrderedItemDesc 	 ;
		CustomEditText etQuantity 		;
		TextView tvPrice 	 		;
		TextView tvItemUOM 	 		 ;
		TextView tvTotalPrice 	 	 ;
		TextView tvInvoiceAmount 	 ;
		TextView tvDiscount 	 	 ;
		TextView tvOrderedItemMeasurment 	;
		TextView tvOrderedItemDesc2 	 ;
		ImageView ivImage		;
		ImageView ivSep			;
	}

	public double getInvoiceAmount()
	{
		return invoiceAmount;
	}
}
