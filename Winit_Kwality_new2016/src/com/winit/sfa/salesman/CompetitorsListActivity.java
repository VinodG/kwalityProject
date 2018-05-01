package com.winit.sfa.salesman;

import httpimage.HttpImageManager;

import java.io.File;
import java.util.Locale;
import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.adapter.CompetitorItemAdapter;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.LocationUtility.LocationResult;
import com.winit.alseer.salesman.common.PlayVideoView;
import com.winit.alseer.salesman.dataaccesslayer.CompetitorDL;
import com.winit.alseer.salesman.dataobject.CompetitorItemDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.imageloader.UrlImageViewHelper;
import com.winit.alseer.salesman.utilities.BitmapUtilsLatLang;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.MyApplicationNew;
import com.winit.kwalitysfa.salesman.R;

public class CompetitorsListActivity extends BaseActivity implements LocationResult
{
	private LinearLayout llCompetitorListView;
	private Button btnCaptureCompetitors/*,btnSubmit*/;
	private LinearLayout /*llCaptureImage,*/llSubmit;
	private ListView lvCompstitorInfo;
	private CompetitorItemAdapter competitorItemAdapter;
	private Vector<CompetitorItemDO> vecCompetitorItems,draft;
	private TextView tvTitle,tvNodata;
	private EditText etSearch;
    public static final String IMAGE_CACHE_DIR = "thumbs";
    private int mImageThumbSize;
    private Dialog dialogDetails;
    
    
    //Details
    private LinearLayout viewDetails;
    private ImageView ivCompetitor;
    private Button btn_submit/*,btnCaptureImage*/;
	private TextView tvBrandOur,tvCompany, tvBrandComp, tvPrice, tvDateTime, tvNote;
	private PlayVideoView vVCompetitor;
	private RelativeLayout rlVideo;
	private ImageView btnPlay;
	private Dialog dialogVideo;
	private CompetitorItemDO competitorItemDO;
    private JourneyPlanDO journeyPlanDO;
    private String lat = "", lang = "";
    private Bitmap mBtBitmap = null;
	private Bitmap bitmapProcessed;
	
	private ImageView ivSearchCross;
    
	@Override
	public void initialize() 
	{
		llCompetitorListView = (LinearLayout) inflater.inflate(R.layout.capture_competitor_info, null);
		llBody.addView(llCompetitorListView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		setTypeFaceRobotoNormal(llCompetitorListView);
		if(getIntent().getExtras() != null)
		{
			journeyPlanDO = (JourneyPlanDO) getIntent().getExtras().get("Object");
		}
		initializeControls();
		bindingControls();
		tvTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnCaptureCompetitors.setTypeface(Typeface.DEFAULT_BOLD);
	}
	
	private void initializeControls() 
	{
		draft = new Vector<CompetitorItemDO>();
		tvTitle = (TextView) llCompetitorListView.findViewById(R.id.tvTitle);
		btnCaptureCompetitors  = (Button) 		llCompetitorListView.findViewById(R.id.btnCaptureCompetitors);
//		btnSubmit       = (Button) 		llCompetitorListView.findViewById(R.id.btnSubmit);
//		llCaptureImage  = (LinearLayout) 		llCompetitorListView.findViewById(R.id.llCaptureImage);
		llSubmit       = (LinearLayout) 		llCompetitorListView.findViewById(R.id.llSubmit);
		lvCompstitorInfo = (ListView) 		llCompetitorListView.findViewById(R.id.lvCompetotor);
		etSearch		 = (EditText)		llCompetitorListView.findViewById(R.id.etSearch);
		tvNodata		 = (TextView)		llCompetitorListView.findViewById(R.id.tvNodata);
		ivSearchCross			=	(ImageView)llCompetitorListView.findViewById(R.id.ivSearchCross);
	}
	
	private void bindingControls()
	{
		draft = new Vector<CompetitorItemDO>();
		competitorItemAdapter = new CompetitorItemAdapter(CompetitorsListActivity.this, draft);
		lvCompstitorInfo.setAdapter(competitorItemAdapter);

		lvCompstitorInfo.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) 
			{
				competitorItemDO = vecCompetitorItems.get(position);
				
				competitorItemAdapter.refreshCompetitorItemPos(position);
				
				showDetails();
				
//				Intent intent = new Intent(CompetitorsListActivity.this,CompetitorsItemDetailsActivity.class);
//				intent.putExtra("competitorItemDO", competitorItemDO);
//				startActivity(intent);
			}
		});
		
		llSubmit.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});

		btnCaptureCompetitors.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(CompetitorsListActivity.this,CompetitorInfoMerchandiserActivity.class);
				intent.putExtra("Object", journeyPlanDO);
				startActivity(intent);
			}
		});
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				showAllData();
			}
		});
		etSearch.addTextChangedListener(new TextWatcher() 
	    {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				if(!TextUtils.isEmpty(s))
					ivSearchCross.setVisibility(View.VISIBLE);
				else
					ivSearchCross.setVisibility(View.GONE);
				if(!s.toString().equalsIgnoreCase(""))
				{
					draft.clear();
					String str = s.toString().toLowerCase(Locale.US);
					
					if(vecCompetitorItems != null && vecCompetitorItems.size() > 0)
					{
						for(CompetitorItemDO files : vecCompetitorItems)
						{
							if(files.brandname!=null && !files.brandname.equalsIgnoreCase(""))
							{
								String st = files.brandname.toLowerCase(Locale.US);
								if(st.contains(str))
								{
									draft.add(files);
								}
							}
						}
					}
					if(draft.size() == 0)
					{
						tvNodata.setVisibility(View.VISIBLE);
						lvCompstitorInfo.setVisibility(View.GONE);
						if(competitorItemAdapter!=null)
						{
							competitorItemAdapter.refreshCompetitorItems(draft);
						}
						else
						{
							competitorItemAdapter = new CompetitorItemAdapter(CompetitorsListActivity.this, draft);
							lvCompstitorInfo.setAdapter(competitorItemAdapter);
						}
					}
					else
					{
						tvNodata.setVisibility(View.GONE);
						lvCompstitorInfo.setVisibility(View.VISIBLE);
						competitorItemAdapter.refreshCompetitorItems(draft);
					}
				}
				else
				{
					showAllData();
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});
	}
	
	private void showAllData()
	{
		if(vecCompetitorItems != null && vecCompetitorItems.size() > 0)
		{
			tvNodata.setVisibility(View.GONE);
			lvCompstitorInfo.setVisibility(View.VISIBLE);
		}
		else
		{
			tvNodata.setVisibility(View.VISIBLE);
			lvCompstitorInfo.setVisibility(View.GONE);
		}
		competitorItemAdapter.refreshCompetitorItems(vecCompetitorItems);
	}

	private void loadData() 
	{
		vecCompetitorItems = new CompetitorDL().getAllCompetitorDO();
		bindCompetitorItems(vecCompetitorItems);
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadData();
	}

	private void bindCompetitorItems(Vector<CompetitorItemDO> vecCompetitorItems) 
	{
		competitorItemAdapter.refreshCompetitorItems(vecCompetitorItems);
	}
	
	
	//--------------------------Show Details
	private void showDetails()
	{
		// Details
        viewDetails = (LinearLayout)LayoutInflater.from(getApplicationContext()).inflate(R.layout.competitor_details, null);
        
        btn_submit       = (Button)viewDetails.findViewById(R.id.btnSubmit);
		ivCompetitor 	 = (ImageView)viewDetails.findViewById(R.id.ivCompetitor);
//		btnCaptureImage  = (Button)viewDetails.findViewById(R.id.btnCaptureImage);
		tvBrandOur 		 = (TextView)viewDetails.findViewById(R.id.tvBrandOur);
		tvCompany 		 = (TextView)viewDetails.findViewById(R.id.tvCompany);
		tvBrandComp 		 = (TextView)viewDetails.findViewById(R.id.tvBrandComp);
		tvPrice          = (TextView)viewDetails.findViewById(R.id.tvPrice);
		tvDateTime       = (TextView)viewDetails.findViewById(R.id.tvDateTime);
		tvNote           = (TextView)viewDetails.findViewById(R.id.tvNote);
		
		vVCompetitor     = (PlayVideoView)viewDetails.findViewById(R.id.vVCompetitor);
		rlVideo 		 = (RelativeLayout)viewDetails.findViewById(R.id.rlVideo);
		btnPlay			 = (ImageView)viewDetails.findViewById(R.id.btnPlay);
		
		dialogDetails = new Dialog(CompetitorsListActivity.this, android.R.style.Theme_Holo_Dialog);
		dialogDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogDetails.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogDetails.addContentView(viewDetails, new LayoutParams(AppConstants.DEVICE_WIDTH-20, AppConstants.DEVICE_HEIGHT-20));
		dialogDetails.show();
		
		setTypeFaceRobotoNormal(viewDetails);
		
		bindingControlsDetails();
	}
	
	private void bindingControlsDetails()
	{
		loadDataDetails();
		btn_submit.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				dialogDetails.dismiss();
			}
		});

		btnPlay.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				final Uri uri = Uri.parse(competitorItemDO.imagepath);
				if(uri != null)
				{
					showVideo(uri);
				}
			}
		});
	}

	private void loadDataDetails() 
	{
		if (competitorItemDO.imagepath != null && !competitorItemDO.imagepath.equalsIgnoreCase("")) 
		{
			if(!competitorItemDO.imagepath.contains(".mp4"))
			{
				ivCompetitor.setVisibility(View.VISIBLE);
				rlVideo.setVisibility(View.GONE);
				
				if(!competitorItemDO.imagepath.contains("http://dev4.winitsoftware.com"))
				{
					final Uri uri = Uri.parse(competitorItemDO.imagepath);

					if (uri != null) 
					{
//						Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivCompetitor,
//										competitorItemDO.imagepath));
						
						File f = new File(competitorItemDO.imagepath);
						Bitmap bmp = BitmapUtilsLatLang.decodeSampledBitmapFromResource(f, 720,1280);
						Bitmap bitmapProcessed = getBitMap(bmp,CalendarUtils.getFormatedDatefromCompetitor(competitorItemDO.createdon));
						mBtBitmap = bitmapProcessed;
						
						if (mBtBitmap != null) 
						{
							ivCompetitor.setBackgroundResource(0);
							ivCompetitor.setImageBitmap(bitmapProcessed);
						}
					}
				}
				else if(competitorItemDO.imagepath != null)
					 UrlImageViewHelper.setUrlDrawable(ivCompetitor, competitorItemDO.imagepath, R.drawable.empty,UrlImageViewHelper.CACHE_DURATION_ONE_DAY);
				else
					ivCompetitor.setBackgroundResource(R.drawable.empty);
			}
			else
			{
				ivCompetitor.setVisibility(View.GONE);
				rlVideo.setVisibility(View.VISIBLE);
			}
			
		}
		tvBrandOur.setText(competitorItemDO.ourBrand);
		tvBrandComp.setText(Html.fromHtml(competitorItemDO.brandname));
		tvDateTime.setText(CalendarUtils.getFormatedDatefromCompetitor(competitorItemDO.createdon));
		tvCompany.setText(competitorItemDO.company);
		tvPrice.setText(curencyCode+" "+amountFormate.format(StringUtils.getInt(competitorItemDO.price)));
//		tvPrice.setText(competitorItemDO.price);
		tvNote.setText(Html.fromHtml(competitorItemDO.activity));
	}
	public HttpImageManager getHttpImageManager() {
		return ((MyApplicationNew) ((Activity) CompetitorsListActivity.this)
				.getApplication()).getHttpImageManager();
	}
	
	//----------Show Video
	private void showVideo(final Uri uri)
	{
		new Handler().postDelayed(new Runnable() 
		{
			@Override
			public void run() 
			{
				View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.video_screen, null);

				dialogVideo = new Dialog(getApplicationContext(), android.R.style.Theme_Holo_Dialog);
				dialogVideo.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialogVideo.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
				dialogVideo.addContentView(view,new LayoutParams(AppConstants.DEVICE_WIDTH-20,AppConstants.DEVICE_HEIGHT-20));
//				dialogVideo.getWindow().getAttributes().windowAnimations = R.style.NewLancheDialogAnimation;
				dialogVideo.show();

				vVCompetitor = (PlayVideoView) llCompetitorListView.findViewById(R.id.vVCompetitor);
				ImageView btnPlay			= (ImageView) view.findViewById(R.id.btnPlay);

				btnPlay.setOnClickListener(new OnClickListener() 
				{

					@Override
					public void onClick(View v) 
					{
//						final Uri uri = Uri.parse(competitorItemDO.imagepath);
						if(uri != null)
						{
							vVCompetitor.setMediaController(new MediaController(CompetitorsListActivity.this));       
							vVCompetitor.setVideoURI(uri);
							vVCompetitor.requestFocus();
							vVCompetitor.start();
							
							vVCompetitor.setOnCompletionListener(new OnCompletionListener() {
								
								@Override
								public void onCompletion(MediaPlayer mp) {
									dialogVideo.dismiss();
								}
							});
						}
					}
				});
			}
		}, 100);
	}
	
	@Override
	public void onBackPressed() {
		if(dialogDetails != null && dialogDetails.isShowing())
			dialogDetails.dismiss();
		else if(dialogVideo != null && dialogVideo.isShowing())
			dialogVideo.dismiss();
		else
			super.onBackPressed();
	}
	
	@Override
	public void gotLocation(Location loc) 
	{
		if(loc!=null)
		{
			lat	 = StringUtils.getStringFromDouble(loc.getLatitude());
			lang = StringUtils.getStringFromDouble(loc.getLongitude());

			DisplayMetrics metrics = getResources().getDisplayMetrics();

			Log.e("Density ", metrics.density+"");

			AppConstants.DEVICE_DENSITY = metrics.density;
		}
	}
	
	private Bitmap getBitMap(Bitmap bmp,String date)
	{
		Bitmap mBtBitmap = null;
		if(bmp != null)
		{
			bitmapProcessed = BitmapUtilsLatLang.processBitmapforCompetitor(bmp, lat, lang, date);
			if(bmp!=null && !bmp.isRecycled())
				bmp.recycle();

			mBtBitmap = bitmapProcessed;
			return mBtBitmap;
		}
		return mBtBitmap;
	}
}


