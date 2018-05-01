package com.winit.alseer.salesman.adapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.GalleryItemClickListener;
import com.winit.alseer.salesman.common.ImageLoader;
import com.winit.alseer.salesman.common.ViewFlow;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;

public class ProductCatalogAdapter extends BaseAdapter 
{

	private  String sdCardPath;
	private Context context;
	private ViewHolder holder ;
	private  Vector<ViewHolder> vecViewHolder;
	private ViewFlow customGallery;
	private GalleryItemClickListener listener;
	private Vector<NameIDDo> vecProductImages;
	private ImageLoader imageLoaderForProductImages;
	private Vector<View> vecViewsForList = new Vector<View>();
	private int padding = 0;
	
	public ProductCatalogAdapter(Context context,Vector<NameIDDo> vecProducts,GalleryItemClickListener listener,ViewFlow  customGallery, String sdCardPath, int padding) 
	{
		this.sdCardPath = sdCardPath;
		this.listener=listener;
		this.context	=	context;
		vecViewHolder = new Vector<ViewHolder>();
		this.customGallery = customGallery;
		this.vecProductImages= vecProducts;
		this.padding = padding;
		imageLoaderForProductImages = new ImageLoader(context, 100, 100);
	}
	public void refresh(Vector<NameIDDo> vecProducts, int padding)
	{
		this.vecProductImages = new Vector<NameIDDo>();
		notifyDataSetChanged();
		clearBitmaps();
		this.vecProductImages= vecProducts;
		this.padding = padding;
		notifyDataSetChanged();
	}
	public void refresh()
	{
		notifyDataSetChanged();
	}
    @Override
    public int getItemViewType(int position) 
    {
        return position;
    }

    @Override
    public int getViewTypeCount() 
    {
    	if(vecProductImages!=null)
    		return vecProductImages.size();
    	else
    		return 0;
    }

    @Override
    public int getCount()
    {
    	if(vecProductImages!=null)
    		return vecProductImages.size();
    	else
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
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		if(convertView == null)
		{
			convertView           		= (LinearLayout)((Activity) context).getLayoutInflater().inflate(R.layout.product_catalog_cell, null);
			holder                		= new ViewHolder();
			holder.tvDetail       		= (TextView)convertView.findViewById(R.id.tvProductDescription);
			
			holder.tvOnlineOrder       	= (TextView)convertView.findViewById(R.id.tvOnlineOrder);
			holder.tvOnlineOrderValue  	= (TextView)convertView.findViewById(R.id.tvOnlineOrderValue);
			holder.tvCallCenter        	= (TextView)convertView.findViewById(R.id.tvCallCenter);
			holder.tvCallCenterNumber  	= (TextView)convertView.findViewById(R.id.tvCallCenterNumber);
			holder.llBottomOptions 	   	= (LinearLayout)convertView.findViewById(R.id.llBottomOptions);
			holder.rlMainLayout 	   	= (RelativeLayout)convertView.findViewById(R.id.rlMainLayout);
			
			holder.ivProductImage 		= (ImageView)convertView.findViewById(R.id.ivProductImage);
			holder.ivLeftArrow 			= (ImageView)convertView.findViewById(R.id.ivLeftArrow);
			holder.ivRightArrow 		= (ImageView)convertView.findViewById(R.id.ivRightArrow);
			holder.btnTwitter     		= (ImageButton)convertView.findViewById(R.id.btnTwitter);
			holder.btnFacebook    		= (ImageButton)convertView.findViewById(R.id.btnfacebook);
			
			convertView.setPadding(padding, 10, 10, 10);
			convertView.setTag(holder);
			vecViewHolder.add(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		vecViewsForList.add(holder.ivProductImage);
		holder.ivProductImage.setBackgroundDrawable(null);
		
		if(position == 0)
		{
			holder.ivLeftArrow.setVisibility(View.GONE);
		}
		else if(position ==(vecProductImages.size()-1))
		{
			holder.ivRightArrow.setVisibility(View.GONE);
		}
		else
		{
			holder.ivRightArrow.setVisibility(View.VISIBLE);
			holder.ivLeftArrow.setVisibility(View.VISIBLE);
		}
		holder.ivLeftArrow.setTag(position);
		holder.ivRightArrow.setTag(position);
		holder.ivRightArrow.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(customGallery!=null)
				{
					customGallery.setSelection(StringUtils.getInt(v.getTag().toString())+1);
				}
			}
		});
		
		holder.ivLeftArrow.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(customGallery!=null)
				{
					customGallery.setSelection(StringUtils.getInt(v.getTag().toString())-1);
				}
			}
		});
		
		/*holder.tvCallCenter.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
		holder.tvDetail.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
		holder.tvOnlineOrder.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
		holder.tvOnlineOrderValue.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
		holder.tvCallCenterNumber.setTypeface(AppConstants.Helvetica_LT_57_Condensed);*/
		
		holder.rlMainLayout.setTag(sdCardPath +vecProductImages.get(position).strId);
		holder.ivProductImage.setTag(sdCardPath + vecProductImages.get(position).strId);
		if(vecProductImages.get(position).strId!=null && !vecProductImages.get(position).strId.equals(""))
			imageLoaderForProductImages.DisplayImage(sdCardPath + vecProductImages.get(position).strId, sdCardPath + vecProductImages.get(position).strId, (Activity)context, holder.ivProductImage);
		
		if(position<vecProductImages.size())
			holder.tvDetail.setText(vecProductImages.get(position).strName);
		else
			holder.tvDetail.setText(vecProductImages.get(0).strName);
		
		holder.tvCallCenterNumber.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				String uri = "tel:"+"8005455";
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                ((Activity)context).startActivity(intent);
			}
		});
		
		holder.tvOnlineOrderValue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("http://www.galadarigroup.com/albums/galadari-ice-cream-co-ltd-l-l-c/"));
				context.startActivity(i);
			}
		});
		holder.btnTwitter .setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("https://twitter.com/AlSeer"));
				context.startActivity(i);

			}
		});
		holder.btnFacebook .setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("http://www.facebook.com/AlSeer"));
				context.startActivity(i);

			}
		});
		holder.rlMainLayout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Bitmap bitmap = null;
				File imgFile = new  File(v.getTag().toString());
				if(imgFile.exists())
				{
				    bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
				}
				listener.onItemClickListener(position,false,bitmap);
			}
		});
		holder.rlMainLayout.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v) 
			{
				Bitmap bitmap = null;
				File imgFile = new  File(v.getTag().toString());
				if(imgFile.exists())
				{
				    bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
				}
				listener.onItemClickListener(position,true, bitmap);
				return true;
			}
		});
		
		((BaseActivity)context).setTypeFaceRobotoNormal((ViewGroup) convertView);
		return convertView;
	}
	public void starAnimation(final View view, final int animation, int duration, final int visibility)
	{
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() 
		{									
			@Override
			public void run()
			{
				Animation anim = AnimationUtils.loadAnimation(context, animation);
				anim.reset();
				view.clearAnimation();
				view.startAnimation(anim);
				view.setVisibility(visibility);
			}
		},duration);
	}
	public void clearBitmaps()
	{
//		imageLoaderForProductImages.isThreadRun = false;
//		imageLoaderForProductImages.isContinue = false;
		imageLoaderForProductImages.stopThread();
		
		for(View v : vecViewsForList)
		{
		
			ImageView ivIcon= (ImageView)v;
			if(ivIcon != null)
			{
				if(ivIcon.getDrawable() != null)
					ivIcon.getDrawable().setCallback(null);
				if(ivIcon.getBackground() != null)
					ivIcon.getBackground().setCallback(null);
				
				ivIcon.setImageResource(R.drawable.water);
			}
		}
		
		vecViewsForList.clear();
		
		Vector<WeakReference<Bitmap>> vecBitmap = new Vector<WeakReference<Bitmap>>();

		
		Set<String> setMedia = imageLoaderForProductImages.cache.keySet();
	  
		for(String link : setMedia)
		{
			Bitmap bitmap = imageLoaderForProductImages.cache.remove(link);
			if(bitmap != null && !bitmap.isRecycled())
			{
				vecBitmap.add(new WeakReference<Bitmap>(bitmap));
			}
		}
		
		imageLoaderForProductImages.cache.clear();
		
//		for(int i=0;i<AppConstants.vecBitmap.size();i++)
//		{
//			Bitmap bitmap = AppConstants.vecBitmap.get(i);
//			try
//			{
//				if(bitmap != null && !bitmap.isRecycled())
//				{
//					vecBitmap.add(new WeakReference<Bitmap>(bitmap));
//				}
//			}
//			catch(Throwable e)
//			{
//			}
//		}
//		AppConstants.vecBitmap.clear();
		
		for(WeakReference<Bitmap> bitmap:vecBitmap)
		{
			if(bitmap.get() != null && !bitmap.get().isRecycled())
			{
				bitmap.get().recycle();
				bitmap.enqueue();
				bitmap = null;
			}
		}
		  
		vecBitmap.clear();
		
		System.gc();
	}
	class ViewHolder
	{
		LinearLayout llBottomOptions;
		RelativeLayout rlMainLayout;
		TextView tvDetail,tvOnlineOrder,tvOnlineOrderValue,tvCallCenter,tvCallCenterNumber ;
		ImageView ivProductImage,ivRightArrow,ivLeftArrow;
		ImageButton btnTwitter,btnFacebook;
		boolean isClicked = false;
	}
}
