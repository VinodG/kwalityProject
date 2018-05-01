package com.winit.alseer.salesman.adapter;

import httpimage.HttpImageManager;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.winit.alseer.salesman.dataobject.DamageImageDO;
import com.winit.kwalitysfa.salesman.MyApplicationNew;
import com.winit.kwalitysfa.salesman.R;

public class CompetetorInfoGalleryAdapter extends PagerAdapter
{
	private Context context;
	private LayoutInflater inflater;
	private Vector<DamageImageDO> vecGallery;
	public CompetetorInfoGalleryAdapter(Context context,Vector<DamageImageDO> vecGallery) 
	{
		this.context = context;
		this.vecGallery = vecGallery;
	}

	@Override
	public int getCount() 
	{
		if(vecGallery!=null && vecGallery.size()>0)
			return vecGallery.size();
		else
			return 0;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) 
	{

		return view == ((LinearLayout) object);
	}
	@Override
	public Object instantiateItem(ViewGroup container, int position) 
	{
		final DamageImageDO galObject = vecGallery.get(position);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View itemView = inflater.inflate(R.layout.gallery_layout_cell, container, false);
		ImageView ivFullImage = (ImageView)itemView.findViewById(R.id.ivFullImage);
		final VideoView vVCaptureVedio = (VideoView)itemView.findViewById(R.id.vVCaptureVedio);
		FrameLayout flVideo= (FrameLayout)itemView.findViewById(R.id.flVideo);
		final ImageView btnPlay= (ImageView)itemView.findViewById(R.id.btnPlay);
		if(galObject.FileType.equalsIgnoreCase("Image"))
		{
			ivFullImage.setVisibility(View.VISIBLE);
			flVideo.setVisibility(View.GONE);
			ivFullImage.setTag(galObject.ImagePath);
			final Uri uri = Uri.parse(galObject.ImagePath);
			if (uri != null) 
			{
				Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivFullImage,galObject.ImagePath));
				if (bitmap != null) 
				{
					ivFullImage.setImageBitmap(bitmap);
				}
			}
		}
		else if(galObject.FileType.equalsIgnoreCase("Video"))
		{
			ivFullImage.setVisibility(View.GONE);
			flVideo.setVisibility(View.VISIBLE);
			 String thumbnailuri = getRealPathFromURI(context,galObject.ImagePath);
		     Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(thumbnailuri,MediaStore.Images.Thumbnails.MINI_KIND);
			 BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
			 vVCaptureVedio.setBackgroundDrawable(bitmapDrawable); 
			 
			 btnPlay.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) 
					{
						try
						{
							btnPlay.setVisibility(View.INVISIBLE);
							vVCaptureVedio.setBackgroundDrawable(null);
							vVCaptureVedio.setMediaController(new MediaController(context));       
							vVCaptureVedio.setVideoURI( Uri.parse(galObject.ImagePath));
							vVCaptureVedio.requestFocus();
							vVCaptureVedio.start();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				});

				// video finish listener
				vVCaptureVedio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) 
					{
						btnPlay.setVisibility(View.VISIBLE);
					}
				});
				
		}
		
		((ViewPager) container).addView(itemView);
		
		return itemView;
	}
	@Override
	public void destroyItem(View container, int position, Object object) 
	{
		((ViewPager) container).removeView((LinearLayout) object);
	}
	public HttpImageManager getHttpImageManager() {
		return ((MyApplicationNew) ((Activity) context)
				.getApplication()).getHttpImageManager();
	}
	public String getRealPathFromURI(Context context, String path) 
	{
		Cursor cursor = null;
		try { 
			Uri contentUri = Uri.parse(path);
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
}
