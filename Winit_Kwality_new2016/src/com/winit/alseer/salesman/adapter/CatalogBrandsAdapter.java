package com.winit.alseer.salesman.adapter;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.dataobject.BrandDO;
import com.winit.alseer.salesman.imageloader.UrlImageViewCallback;
import com.winit.alseer.salesman.imageloader.UrlImageViewHelper;
import com.winit.kwalitysfa.salesman.R;

public class CatalogBrandsAdapter extends BaseAdapter 
{
	Vector<BrandDO> vecBrandDOs;
	Context context;
    public static final String IMAGE_CACHE_DIR = "thumbs";
    private int mImageThumbSize;

	public CatalogBrandsAdapter(Context context, Vector<BrandDO> vecBrandDOs) 
	{
		this.context = context;
		this.vecBrandDOs = vecBrandDOs;
	}

	@Override
	public int getCount() 
	{
		if(vecBrandDOs != null && vecBrandDOs.size()>0)
			return vecBrandDOs.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return vecBrandDOs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void refreshAdapter(Vector<BrandDO> vecBrands)
	{
		vecBrandDOs = vecBrands;
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		BrandDO brand = vecBrandDOs.get(position);
		if(convertView == null)
		{
			viewHolder  = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.catelog_brand, null);
			viewHolder.tvCatalogBrand = (TextView)convertView.findViewById(R.id.tvCatalogBrand);
			viewHolder.ivCatalogBrand = (ImageView) convertView.findViewById(R.id.ivCatalogBrand);
			viewHolder.llBottom		  = (LinearLayout)convertView.findViewById(R.id.llBottom);
			convertView.setTag(viewHolder);
			
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tvCatalogBrand.setText(brand.brandName);
		
		//
//		if(brand.category_id!=null && !brand.category_id.equalsIgnoreCase(""))
//			AppConstants.setBackgroundForBottom(brand.category_id,viewHolder.llBottom);
		
		if(brand.image !=null && !brand.image.equalsIgnoreCase("") && !brand.image.contains(".mp4"))
		{
			if(!brand.image.contains("http://dev4.winitsoftware.com"))
			{
				UrlImageViewHelper.setUrlDrawable(viewHolder.ivCatalogBrand, brand.image, R.drawable.empty, new UrlImageViewCallback() {
					
					@Override
					public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url,
							boolean loadedFromCache) {
						if(!loadedFromCache)
						{
							imageView.setImageBitmap(loadedBitmap);
						}
					}
				});
				
//				Uri uri =Uri.parse(brand.image);
//				
//				if (uri != null) 
//				{
//					Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri, viewHolder.ivCatalogBrand,brand.image));
//					if (bitmap != null) 
//					{
//						viewHolder.ivCatalogBrand.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 110, 110, false));
//					}
//				}
			}
			else if(brand.image != null)
				 UrlImageViewHelper.setUrlDrawable(viewHolder.ivCatalogBrand, brand.image, R.drawable.empty,UrlImageViewHelper.CACHE_DURATION_ONE_DAY);

		
		
		}
//		final Uri uri = Uri.parse(brand.image);
//
//	       if (uri != null) {
//	        Bitmap bitmap = getHttpImageManager().loadImage(
//	          new HttpImageManager.LoadRequest(uri,  viewHolder.ivCatalogBrand,brand.image));
//	        if (bitmap != null) {
//	        	viewHolder.ivCatalogBrand.setImageBitmap(bitmap);
//	        }
//	       }
	       // Animation Start
	       
//	     TranslateAnimation transAnimation = null;
//	    if(position % 8 == 0)   
//	    	transAnimation= new TranslateAnimation(-100, 0, 0, 0);
//	    else if(position % 8 == 1)   
//	    	transAnimation= new TranslateAnimation(-50, 0, 0, 0);
//	    else if(position % 8 == 2)   
//	    	transAnimation= new TranslateAnimation(0, 0, -100, 0);
//	    else if(position % 8 == 3)
//	    	transAnimation= new TranslateAnimation(0, 0, -50, 0);
//	    if(position % 8 == 4)   
//	    	transAnimation= new TranslateAnimation(100, 0, 0, 0);
//	    else if(position % 8 == 5)   
//	    	transAnimation= new TranslateAnimation(50, 0, 0, 0);
//	    else if(position % 8 == 6)   
//	    	transAnimation= new TranslateAnimation(0, 0, 100, 0);
//	    else if(position % 8 == 7)
//	    	transAnimation= new TranslateAnimation(0, 0, 50, 0);
//		transAnimation.setDuration(1000);
//		convertView.startAnimation(transAnimation);
	       
	       // Animation End
	       
		return convertView;
	}
//	public HttpImageManager getHttpImageManager () 
//	{
//	     return ((MerchandiserApplication) ((Activity) context).getApplication()).getHttpImageManager();
//	 }
	private class ViewHolder
	{
		private TextView     tvCatalogBrand;
		private ImageView    ivCatalogBrand;
		private LinearLayout llBottom;
	}

	
}
