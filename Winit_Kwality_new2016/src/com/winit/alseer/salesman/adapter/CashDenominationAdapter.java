///**
// * 
// */
//package com.winit.alseer.salesman.adapter;
//
//import httpimage.HttpImageManager;
//
//import java.util.Vector;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.winit.alseer.salesman.dataobject.CashDenominationDO;
//import com.winit.kwality.salesman.R;
//import com.winit.sfa.salesman.ReceivePaymentActivity;
//
///**
// * @author Aritra.Pal
// *
// */
//public class CashDenominationAdapter extends BaseAdapter
//{
//
//	private Context context;
//	private Vector<CashDenominationDO> vecDetails;
//	/**
//	 * @param receivePaymentActivity
//	 * @param vecChequePayment
//	 * @param b
//	 */
//	public CashDenominationAdapter(Context context,Vector<CashDenominationDO> vecDetails) 
//	{
//		this.context = context;
//		this.vecDetails = vecDetails;
//	}
//
//	/**
//	 * @param vecChequePayment2
//	 */
//	public void refresh(Vector<CashDenominationDO> vecDetails) 
//	{
//		this.vecDetails = vecDetails;
//		notifyDataSetChanged();
//	}
//
//	/* (non-Javadoc)
//	 * @see android.support.v4.view.PagerAdapter#getCount()
//	 */
//	@Override
//	public int getCount() 
//	{
//		if(vecDetails != null && vecDetails.size() > 0)
//			return vecDetails.size();
//		else
//		   return 0;
//	}
//
//	@Override
//	public Object getItem(int position) {
//		return position;
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		
//		convertView = LayoutInflater.from(context).inflate(R.layout.payment_cash_denomination_item,null);
//		
//		TextView tvCount = (TextView)convertView.findViewById(R.id.tvCount);
//		ImageView ivItem = (ImageView)convertView.findViewById(R.id.ivItem);
//		
//		CashDenominationDO cashDenominationDO  = vecDetails.get(position);
//		tvCount.setText(""+cashDenominationDO.count);
//		
//		 String imageURL = "cashNotes/"+cashDenominationDO.Name+".png";
//		    final Uri uri = Uri.parse(imageURL);
//		    ivItem.setImageResource(R.drawable.deafaultcashdemo);
//		    if (uri != null) {
//		     Bitmap bitmap = ((ReceivePaymentActivity) context).getHttpImageManager().loadImage(
//		       new HttpImageManager.LoadRequest(uri, ivItem,imageURL));
//		     if (bitmap != null) {
//		      ivItem.setImageBitmap(bitmap);
//		     }
//		    }
//		
//
//		return convertView;
//	}
//	
//}
