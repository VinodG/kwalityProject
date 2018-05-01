package com.winit.alseer.salesman.adapter;

import httpimage.HttpImageManager;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataobject.CompetitorItemDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.kwalitysfa.salesman.MyApplicationNew;
import com.winit.kwalitysfa.salesman.R;

public class CompetitorItemAdapter extends BaseAdapter 
{
	private Vector<CompetitorItemDO> vecCompetitorItem;
	private Context context;
    public static final String IMAGE_CACHE_DIR = "thumbs";
    private int mImageThumbSize;
    private int selPos = 0;

	public CompetitorItemAdapter(Context context, Vector<CompetitorItemDO> vecCompetitorItem) 
	{
		this.context = context;
		this.vecCompetitorItem = vecCompetitorItem;
        
	}

	@Override
	public int getCount() 
	{
		if(vecCompetitorItem!=null && vecCompetitorItem.size()>0)
			return vecCompetitorItem.size();
		else 
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		CompetitorItemDO competitorItemDO = vecCompetitorItem.get(position);
		final ViewHolder viewHolder;
//		if(convertView==null)
//		{
			convertView = LayoutInflater.from(context).inflate(R.layout.competitor_item_list_cell, null);
			viewHolder = new ViewHolder();

			viewHolder.ivCompetitorImage  = (ImageView)convertView.findViewById(R.id.ivCompetitorImage);
			viewHolder.tvBrand 		      = (TextView)convertView.findViewById(R.id.tvBrand);
			viewHolder.tvCompany 		  = (TextView)convertView.findViewById(R.id.tvCompany);
			viewHolder.tvDate 		      = (TextView)convertView.findViewById(R.id.tvDate);
			convertView.setTag(viewHolder);
			viewHolder.tvBrand.setTextColor(context.getResources().getColor(R.color.gray_dark));
			viewHolder.tvCompany.setTextColor(context.getResources().getColor(R.color.gray_dark));
			viewHolder.tvDate.setTextColor(context.getResources().getColor(R.color.gray_dark));
			
			viewHolder.tvBrand.setTypeface(AppConstants.Roboto_Condensed);
			viewHolder.tvCompany.setTypeface(AppConstants.Roboto_Condensed);
			viewHolder.tvDate.setTypeface(AppConstants.Roboto_Condensed);
			
			if(competitorItemDO.imagepath!=null && !competitorItemDO.imagepath.equalsIgnoreCase("") && !competitorItemDO.imagepath.contains(".mp4"))
			{
				if(!competitorItemDO.imagepath.contains("http://dev4.winitsoftware.com"))
				{
					Uri uri =Uri.parse(competitorItemDO.imagepath);
					
					if (uri != null) 
					{
						Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri, viewHolder.ivCompetitorImage,competitorItemDO.imagepath));
						if (bitmap != null) 
						{
							viewHolder.ivCompetitorImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 110, 110, false));
						}
						else
							viewHolder.ivCompetitorImage.setBackgroundResource(R.drawable.empty_photo);
					}
				}
					viewHolder.ivCompetitorImage.setBackgroundResource(R.drawable.empty_photo);
			}  
			else if(competitorItemDO.imagepath!=null)
			{
				 Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(competitorItemDO.imagepath,MediaStore.Images.Thumbnails.MINI_KIND); 
				 viewHolder.ivCompetitorImage.setImageBitmap(thumbnail);
			}
			else
				viewHolder.ivCompetitorImage.setBackgroundResource(0);

		viewHolder.tvBrand.setText(Html.fromHtml(competitorItemDO.brandname));
		viewHolder.tvCompany.setText(competitorItemDO.activity);
		viewHolder.tvDate.setText(CalendarUtils.getFormatForCompetitor(competitorItemDO.createdon));

		return convertView;
	}
	public void refreshCompetitorItems(Vector<CompetitorItemDO> vecCompetitorItem)
	{
		this.vecCompetitorItem = vecCompetitorItem;
		notifyDataSetChanged();
	}
	public void refreshCompetitorItemPos(int pos)
	{
		this.selPos = pos;
		notifyDataSetChanged();
	}
	public HttpImageManager getHttpImageManager () 
	{
		return ((MyApplicationNew) ((Activity) context).getApplication()).getHttpImageManager();
	}
	public class ViewHolder
	{
		ImageView ivCompetitorImage;
		TextView tvBrand,tvCompany,tvDate/*,tvCategory,tvPrice,tvActivity*/;
	}
}
