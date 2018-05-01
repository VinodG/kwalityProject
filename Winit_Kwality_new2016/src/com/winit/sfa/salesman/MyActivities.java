package com.winit.sfa.salesman;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.parsers.TaskToDoUpdateParser;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Base64;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.MyActivitiesDA;
import com.winit.alseer.salesman.dataaccesslayer.StatusDA;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.MyActivityDO;
import com.winit.alseer.salesman.dataobject.StatusDO;
import com.winit.alseer.salesman.utilities.BitmapsUtiles;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;

public class MyActivities extends BaseActivity{

	private LinearLayout llTasktodo;
	private TextView tvRecords;
	private ImageView img_cal;
	private ListView lv_myactivities;
	private  MyActivitiesAdapter myActivitiesAdapter;
	public static String APPFOLDERNAME ="Kwality";
	public static String APPFOLDERPATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+APPFOLDERNAME+"/";
	public static String APPMEDIAFOLDERNAME = "KwalityImages";
	public static String APPMEDIAFOLDERPATH = APPFOLDERPATH+APPMEDIAFOLDERNAME+"/"; 
	private int  CAMERA_REQUESTCODE = 2;
	private Vector<MyActivityDO> vecVecMyActivities ;
	private View  tempView;
	private String camera_imagepath;
	private final int DATE_DIALOG_ID = 0;
	private Button btnRefresh,btnCapture,btnFinish;
	private String taskId = "",strTitle = "",taskName="";
	private JourneyPlanDO journeyPlanDO;
	
	//header bar
	private TextView tvPageTitle;
	private Button btn;
	private ImageView ivDivHeader;

	private void setHeaderBar()
	{
		tvPageTitle	= (TextView) llTasktodo.findViewById(R.id.tvPageTitle);
		btn	= (Button) llTasktodo.findViewById(R.id.btn);
		ivDivHeader = (ImageView) llTasktodo.findViewById(R.id.ivDivHeaderBtn);
		tvPageTitle.setText("My Activities");
		ivDivHeader.setVisibility(View.GONE);
		btn.setText("");
		btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.calender), null, null, null);
		//			tvPageTitle.setTypeface(AppConstants.Roboto_Condensed);
		//			btn.setTypeface(AppConstants.Roboto_Condensed);
	}
	
	@Override
	public void initialize() 
	{
		if(getIntent().getExtras() != null)
			journeyPlanDO = (JourneyPlanDO) getIntent().getExtras().get("object"); 
		
		llTasktodo = (LinearLayout) inflater.inflate(R.layout.my_activities, null);
		setHeaderBar();
		
		img_cal  = (ImageView) llTasktodo.findViewById(R.id.img_cal);
		lv_myactivities = (ListView) llTasktodo.findViewById(R.id.lv_myactivities);
		btnRefresh = (Button) llTasktodo.findViewById(R.id.btnRefresh);
		btnCapture = (Button) llTasktodo.findViewById(R.id.btnCapture);
		btnFinish = (Button) llTasktodo.findViewById(R.id.btnFinish);
		llBody.addView(llTasktodo, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		taskId	  = getIntent().getStringExtra("taskId");
		strTitle  = getIntent().getStringExtra("strTitle");
		taskName  = getIntent().getStringExtra("taskName");
		tvRecords = (TextView) llTasktodo.findViewById(R.id.tvRecords);
		
		try
		{
			new File(APPMEDIAFOLDERPATH).mkdirs();
		} 
		catch (Exception e) {
			// TODO: handle exception
		}
		lv_myactivities.setCacheColorHint(0);
		lv_myactivities.setFadingEdgeLength(0);
		lv_myactivities.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) 
			{
				
			}
		});
		btn.setText(CalendarUtils.getCurrentDate());
//		tv_caldate.setOnClickListener(new OnClickListener() 
//		{
//			@Override
//			public void onClick(View v) 
//			{
//				img_cal.performClick();
//			}
//		});
		
		btnCapture.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v)
			{
				camera_imagepath = APPMEDIAFOLDERPATH+ "Kwality"+ System.currentTimeMillis()+ ".jpg";
				Uri outputFileUri = Uri.fromFile(new File(camera_imagepath));
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
				startActivityForResult(intent,CAMERA_REQUESTCODE);
				
			}
		});
		img_cal.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				try
				{
					tempView = v;
					showDialog(DATE_DIALOG_ID);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		loadData();
		myActivitiesAdapter = new MyActivitiesAdapter(new Vector<MyActivityDO>());
		lv_myactivities.setAdapter(myActivitiesAdapter);
		lv_myactivities.setDividerHeight(0);
		
		
		btnRefresh.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				loadData();
			}
		});
		
		btnFinish.setOnClickListener(new  OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				new Thread(new Runnable() 
				{
					@Override
					public void run() 
					{
						StatusDO statusDO = new StatusDO();
						statusDO.UUid ="";
						statusDO.Userid = preference.getStringFromPreference(Preference.SALESMANCODE, "");
						statusDO.Customersite =journeyPlanDO.site;
						statusDO.Date = CalendarUtils.getCurrentDateAsStringforStoreCheck();
						statusDO.Visitcode=journeyPlanDO.VisitCode;
						statusDO.JourneyCode = journeyPlanDO.VisitCode;
						statusDO.Status = "0";
						statusDO.Action =AppConstants.Action_CheckIn;
						statusDO.Type = AppConstants.Type_Task;
						new StatusDA().insertOptionStatus(statusDO);
						
						uploadData();
						runOnUiThread(new Runnable() 
						{
							@Override
							public void run() 
							{
								setResult(RESULT_OK);
								finish();
							}
						});
					}
				}).start();
				
			}
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		//getting current dateofJorney from Calendar
		  Calendar c 	= 	Calendar.getInstance();
		  c.add(Calendar.DAY_OF_MONTH, 0);
		  int cyear 	= 	c.get(Calendar.YEAR);
		  int cmonth 	= 	c.get(Calendar.MONTH);
		  int cday 		=	c.get(Calendar.DAY_OF_MONTH);
		    
		  int myHour 	= 	c.get(Calendar.HOUR_OF_DAY);
		  int myMinute 	= 	c.get(Calendar.MINUTE);
		    
		  switch (id) 
		  {
			  case DATE_DIALOG_ID:
				  return new DatePickerDialog(this, DateListener,  cyear, cmonth, cday);
		  }
		  return null;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		
		 if (resultCode == RESULT_OK && requestCode == CAMERA_REQUESTCODE)
		{
			switch (resultCode) 
			{
			case RESULT_CANCELED:
				Log.i("Camera", "User cancelled");
				break;

			case RESULT_OK:
				
				String strTitle = AppConstants.Task_Title1;
				AppConstants.imagePath = camera_imagepath;
				Intent in = new Intent(MyActivities.this, CaptureShelfPhotoActivity.class);
				in.putExtra("imagepath", AppConstants.imagePath);
				in.putExtra("taskId", taskId);
				in.putExtra("taskName", taskName);
				in.putExtra("strTitle", strTitle);
				in.putExtra("object", journeyPlanDO);
				startActivity(in);
				
				break;
			}

		}
		
	}
	
	/** method for dateofJorney picker **/
	  private DatePickerDialog.OnDateSetListener DateListener = new DatePickerDialog.OnDateSetListener()
	  {
		  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
		  {
			  Calendar currentCal = Calendar.getInstance();
			  Calendar tempCal 	= Calendar.getInstance();
			  tempCal.set(year, monthOfYear, dayOfMonth);
		    	
			  if(!tempCal.before(currentCal))
			  {
				  if(tempView != null)
				  {
					  String strDate = "", strMonth ="";
					  if(monthOfYear <9)
						  strMonth = "0"+(monthOfYear+1);
					  else
						  strMonth = ""+(monthOfYear+1);
			    		
					  if(dayOfMonth <10)
						  strDate = "0"+(dayOfMonth);
					  else
						  strDate = ""+(dayOfMonth);
			    		
					  btn.setText(strDate+" "+CalendarUtils.getMonthAsString(monthOfYear)+" "+year);
				  }
			  }
			  else
				  showCustomDialog(MyActivities.this, getResources().getString(R.string.warning), "Delivery date should be greater than current date.", getResources().getString(R.string.OK), null, "");
		  }
	  };
	  
	public class MyActivitiesAdapter extends BaseAdapter
	{

		private Vector<MyActivityDO> vecMyActivityDOs;
		public MyActivitiesAdapter(Vector<MyActivityDO> vecMyActivityDOs)
		{
			this.vecMyActivityDOs = vecMyActivityDOs;
		}

		@Override
		public int getCount()
		{
			if(vecMyActivityDOs != null && vecMyActivityDOs.size() >0)
				return vecMyActivityDOs.size();
			return 0;
		}

		@Override
		public Object getItem(int arg0) 
		{
			return arg0;
		}

		@Override
		public long getItemId(int arg0) 
		{
			return arg0;
		}

		public void refreshList(Vector<MyActivityDO> vecMyActivityDOs)
		{
			this.vecMyActivityDOs = vecMyActivityDOs;
			notifyDataSetChanged();
		}
		@Override
		public View getView(int pos, View view, ViewGroup arg2)
		{
			final MyActivityDO myActivityDO = vecMyActivityDOs.get(pos);
			
			view = getLayoutInflater().inflate(R.layout.my_activity_list_cell, null);
			TextView tv_listtitle = (TextView) view.findViewById(R.id.tv_listtitle);
			TextView tv_activitydesc = (TextView) view.findViewById(R.id.tv_activitydesc);
			ImageView iv_capturepic = (ImageView) view.findViewById(R.id.iv_capturepic);
			ImageView ivIsVerified	= (ImageView) view.findViewById(R.id.ivIsVerified);
			
			TextView tv_CustomerName = (TextView) view.findViewById(R.id.tv_CustomerName);
			TextView tv_Date = (TextView) view.findViewById(R.id.tv_Date);
			
			ImageView ivSttar1	= (ImageView) view.findViewById(R.id.ivSttar1);
			ImageView ivSttar2	= (ImageView) view.findViewById(R.id.ivSttar2);
			ImageView ivSttar3	= (ImageView) view.findViewById(R.id.ivSttar3);
			ImageView ivSttar4	= (ImageView) view.findViewById(R.id.ivSttar4);
			ImageView ivSttar5	= (ImageView) view.findViewById(R.id.ivSttar5);
			
			tv_Date.setText(" "+myActivityDO.strDate);
			tv_CustomerName.setText(" "+myActivityDO.strCustomerName);
			switch (myActivityDO.rating)
			{
				case 1:
					ivSttar1.setBackgroundResource(R.drawable.star1);
					break;
				case 2:
					ivSttar1.setBackgroundResource(R.drawable.star1);
					ivSttar2.setBackgroundResource(R.drawable.star1);
					break;
				case 3:
					ivSttar1.setBackgroundResource(R.drawable.star1);
					ivSttar2.setBackgroundResource(R.drawable.star1);
					ivSttar3.setBackgroundResource(R.drawable.star1);
					break;
				case 4:
					ivSttar1.setBackgroundResource(R.drawable.star1);
					ivSttar2.setBackgroundResource(R.drawable.star1);
					ivSttar3.setBackgroundResource(R.drawable.star1);
					ivSttar4.setBackgroundResource(R.drawable.star1);
					break;
				case 5:
					ivSttar1.setBackgroundResource(R.drawable.star1);
					ivSttar2.setBackgroundResource(R.drawable.star1);
					ivSttar3.setBackgroundResource(R.drawable.star1);
					ivSttar4.setBackgroundResource(R.drawable.star1);
					ivSttar5.setBackgroundResource(R.drawable.star1);
					break;	
				default:
					ivSttar1.setBackgroundResource(R.drawable.star2);
					ivSttar2.setBackgroundResource(R.drawable.star2);
					ivSttar3.setBackgroundResource(R.drawable.star2);
					ivSttar4.setBackgroundResource(R.drawable.star2);
					ivSttar5.setBackgroundResource(R.drawable.star2);
					break;
			}
			
			if(myActivityDO.isVerified.equalsIgnoreCase("true"))
			{
				ivIsVerified.setBackgroundResource(R.drawable.verified);
			}
			else 
			{
				ivIsVerified.setBackgroundResource(R.drawable.completed);
			}
			
			if(myActivityDO.imagePath != null && !myActivityDO.imagePath.equals(""))
			{
				Bitmap bitmap = getFromBsae64Image(myActivityDO.imagePath);
				iv_capturepic.setImageBitmap(bitmap);
			}
		
			if(myActivityDO.taskName.equalsIgnoreCase(AppConstants.Task_Title1))
				tv_listtitle.setText(AppConstants.Task_Title1);
			else if(myActivityDO.taskName.equalsIgnoreCase(AppConstants.Task_Title2))
				tv_listtitle.setText(AppConstants.Task_Title2);
			else if(myActivityDO.taskName.equalsIgnoreCase(AppConstants.Task_Title3))
			{
				tv_listtitle.setText(AppConstants.Task_Title3);
				iv_capturepic.setBackgroundResource(R.drawable.survayimage);
			}
			
			return view;
		}
		
	}
	
	public boolean createAppFolder()
	{
		return AppConstants.APPFOLDER.mkdirs();
	}
	public boolean createAppMediaFolder()
	{
		return AppConstants.APPMEDIAFOLDER.mkdirs();
	}
	public boolean checkAppMediaFolder()
	{
		if(AppConstants.APPMEDIAFOLDER.exists())
		{
			return true;
		}
		else
		{
			if(AppConstants.APPFOLDER.exists())
			{
				return createAppMediaFolder();
			}
			else
			{
				checkAppFolder();
				return createAppMediaFolder();
			}
		}
		
	}
	
	public boolean checkAppFolder()
	{
		if(AppConstants.APPFOLDER.exists())
		{
			return true;
		}
		else
		{
			return createAppFolder();
		}
	}
	private void loadData()
	{
		showLoader("Please wait...");
		new Thread(new Runnable() 
		{
			@Override
			public void run()
			{
				loadAllTask(preference.getStringFromPreference(Preference.EMP_NO, ""));
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						new Thread(new Runnable() 
						{
							@Override
							public void run() 
							{
								vecVecMyActivities = new MyActivitiesDA().getAllActivities();
								runOnUiThread(new Runnable() 
								{
									@Override
									public void run()
									{
										if(vecVecMyActivities != null && vecVecMyActivities.size() > 0)
										{
											myActivitiesAdapter.refreshList(vecVecMyActivities);
											tvRecords.setVisibility(View.GONE);
											lv_myactivities.setVisibility(View.VISIBLE);
										}
										else
										{
											tvRecords.setVisibility(View.VISIBLE);
											lv_myactivities.setVisibility(View.GONE);
										}
										hideLoader();
									}
								});
							}
						}).start();
					}
				});
			}
		}).start();
	}
	
	private Bitmap getbitmap(String imagepath) 
	{
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inScaled = false;
		Bitmap bitmap = BitmapFactory.decodeFile(imagepath, bmpFactoryOptions);
		return bitmap;
	}
	
	public void loadAllTask(String strSalesmanCode)
	{
		TaskToDoUpdateParser toDoUpdateParser	= new TaskToDoUpdateParser(MyActivities.this);
		new ConnectionHelper(null).sendRequest(MyActivities.this,BuildXMLRequest.getAllAckTasks(strSalesmanCode, preference.getStringFromPreference(Preference.GetAllAcknowledgedTask, "")), toDoUpdateParser, ServiceURLs.GetAllAcknowledgedTask);
	}
	
	private static Bitmap getFromBsae64Image(String imagePath)
	{
		//customer signature
		
		byte[] bytes = null;
		try {
			bytes = Base64.decode(imagePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes .length);
		
		
//		
//		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
//		bmpFactoryOptions.inScaled = false;
//		bitmap = BitmapFactory.decodeFile(imagePath, bmpFactoryOptions);
//		
//		ByteArrayOutputStream streams = new ByteArrayOutputStream();
//		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, streams);
		
		Bitmap bmp = BitmapsUtiles.getResizedBitmap(bitmap, 500, 800);
		return bmp;
	}
}
