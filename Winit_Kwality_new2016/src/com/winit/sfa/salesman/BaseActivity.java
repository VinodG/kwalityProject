package com.winit.sfa.salesman;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.winit.alseer.parsers.AllDataSyncParser;
import com.winit.alseer.parsers.GetAllDeleteLogsParser;
import com.winit.alseer.parsers.GetAllMovements;
import com.winit.alseer.parsers.GetPromotionsParser;
import com.winit.alseer.parsers.GetVanLogParsar;
import com.winit.alseer.parsers.SplashDataSyncParser;
import com.winit.alseer.parsers.VersionCheckingHandler;
import com.winit.alseer.salesman.adapter.CheckoutAdapter;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.CustomEditText;
import com.winit.alseer.salesman.common.FilesStorage;
import com.winit.alseer.salesman.common.GetSplashData;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.common.SoftKeyboardStateHelper.SoftKeyboardStateListener;
import com.winit.alseer.salesman.common.UploadData;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.EOTDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataaccesslayer.ReturnOrderDA;
import com.winit.alseer.salesman.dataaccesslayer.StatusDA;
import com.winit.alseer.salesman.dataaccesslayer.StoreCheckDA;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataaccesslayer.TransactionsLogsDA;
import com.winit.alseer.salesman.dataobject.CheckVersionDO;
import com.winit.alseer.salesman.dataobject.CustomerVisitDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.LoginUserInfo;
import com.winit.alseer.salesman.dataobject.MenuDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.OptionsDO;
import com.winit.alseer.salesman.dataobject.OrderPrintImageDO;
import com.winit.alseer.salesman.dataobject.PostReasonDO;
import com.winit.alseer.salesman.dataobject.StatusDO;
import com.winit.alseer.salesman.dataobject.SurveyQuestionNewDO;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.TrxLogDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxLogHeaders;
import com.winit.alseer.salesman.dataobject.VanLoadDO;
import com.winit.alseer.salesman.dataobject.VehicleDO;
import com.winit.alseer.salesman.listeners.DropdownSelectionListner;
import com.winit.alseer.salesman.listeners.KeyClickListener;
import com.winit.alseer.salesman.listeners.VersionChangeListner;
import com.winit.alseer.salesman.utilities.BitmapConvertor;
import com.winit.alseer.salesman.utilities.BitmapUtilsLatLang;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.FileUtils;
import com.winit.alseer.salesman.utilities.FileUtils.DownloadListner;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.OnMonochromeCreated;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.utilities.SyncData;
import com.winit.alseer.salesman.utilities.SyncData.SyncProcessListner;
import com.winit.alseer.salesman.utilities.ZipUtils;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.AlarmReceiver;
import com.winit.kwalitysfa.salesman.R;
import com.winit.kwalitysfa.salesman.SplashScreenActivity;


public abstract class BaseActivity extends FragmentActivity  implements ConnectionExceptionListener,KeyClickListener,OnClickListener,SoftKeyboardStateListener,OnScrollListener,OnPageChangeListener
{
	public ProgressDialog progressdialog;
	public Button btnPrevious, btnNext, btnChkOutVan, btnChkInVan;
	public Button btnSetting, btnMessage, btnLoginLogout, btnBack, btnCheckOut, btnMenu;
	public LinearLayout llHeader, llBody, llSecurityHeader, llDashBoard, llMenu;
	public LayoutInflater inflater;
	public static String strPreviouslySelectedOption = "";
	public static String ACTION_QUIT = "com.winit.alseer.salesman.ACTION_QUIT";
	public Handler handler = new Handler();
	public TextView tvSecurityHeader;
	protected TextView tvUserName, tvUserType;
	public AlertDialog.Builder alertBuilder;
	public static float px;
	public Preference preference;
	public CustomDialog customDialog, upgradeDialog;
	private String mEnteredPasscode = "", mEnteredReason = "";//RecommendedQuantityResponse
	private ExpandableListView lvDashBoard;
	public ImageView btnLogo, ivLogOut,ivDivider;
	private Toast toast;
	public PendingIntent pIntent ;
	public String curencyCode= "AED";
	public DecimalFormat decimalFormat,percentFormat,amountFormate;
	private View globalView;
	public PopupWindow customKeyBoardpopup;
	private View customKeyBoard;
	public ImageView ivShoppingCart;
	public Button btnCartCount;
	private TextView tvDone, tvNext, tvOne, tvTwo, tvThree, tvFour, tvFive, tvSix,
			tvSeven, tvEight, tvNine, tvZero,tvClear,tvDot;

	private ImageView keyBack;
	//made static so that it doesn't crash while going to checkinoption from loadrequest screen
	public JourneyPlanDO mallsDetailss=null ;

	public DrawerLayout drawerLayout;
	private FrameLayout flMenu;
	public DatePickerDialog datePicker;
	private int groupPos = 0;
	Drawable popupDrawable;
	public boolean flagForReturnReason = false;

	public View viewForEditText;

	//temporary for OrderPrintImageDO
	OrderPrintImageDO orderPrintImageDO= new OrderPrintImageDO();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		pIntent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(getBaseContext(), SplashScreenActivity.class), getIntent().getFlags());
		Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(BaseActivity.this));
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.base);
		/**
		 * For optimization, we create the Drawable in onCreate().
		 */


		Bundle bundle  =  getIntent().getExtras();

		if(bundle != null && bundle.containsKey("mallsDetails"))
			mallsDetailss = (JourneyPlanDO) bundle.get("mallsDetails");

		Resources r 			= 		getResources();
		px 						= 		TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
		preference 				= 		new Preference(getApplicationContext());
		inflater 				= 		this.getLayoutInflater();// get the inflater object for inflating layouts.
		flMenu			 		= 		(FrameLayout) findViewById(R.id.flMenu);
		drawerLayout 	  		= 		(DrawerLayout) findViewById(R.id.drawerLayout);
		llBody 					= 		(LinearLayout) findViewById(R.id.llBody);
		llHeader 				= 		(LinearLayout) findViewById(R.id.llHeader);

		llDashBoard 			= 		(LinearLayout) findViewById(R.id.llDashBoard);

		btnMenu					= 		(Button) findViewById(R.id.btnMenu);
		btnLogo 				= 		(ImageView) findViewById(R.id.btnLogo);
		btnBack 				= 		(Button) findViewById(R.id.btnBack);
		btnSetting 				= 		(Button) findViewById(R.id.btnSetting);
		btnCheckOut 			= 		(Button) findViewById(R.id.btnLogOut);
		ivLogOut				= 		(ImageView) findViewById(R.id.ivLogOut);
		btnLoginLogout 			= 		(Button) findViewById(R.id.btnLoginLogout);
		btnMessage 				= 		(Button) findViewById(R.id.btnMessage);
		btnChkOutVan 			= 		(Button) findViewById(R.id.btnChkOutVan);
		btnChkInVan 			= 		(Button) findViewById(R.id.btnChkInVan);
		llSecurityHeader 		= 		(LinearLayout) findViewById(R.id.llSecurityHeader);
		tvSecurityHeader 		= 		(TextView) findViewById(R.id.tvSecurityHeader);
		tvUserName				= 		(TextView) findViewById(R.id.tvUsername);
		tvUserType				= 		(TextView) findViewById(R.id.tvUserType);
		lvDashBoard				= 		(ExpandableListView) findViewById(R.id.lvDashBoard);
		llMenu					= 		(LinearLayout) findViewById(R.id.llMenu);
		btnCartCount			=       (Button) findViewById(R.id.btnCartCount);

		ivShoppingCart			=		(ImageView) findViewById(R.id.ivShoppingCart);
		ivDivider				=		(ImageView) findViewById(R.id.ivDivider);

		btnCartCount.bringToFront();
		lvDashBoard.setGroupIndicator(null);

		//setting Type-faces Here
		tvUserName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvUserType.setTypeface(AppConstants.Roboto_Condensed);
		btnChkOutVan.setTypeface(AppConstants.Roboto_Condensed);
		btnChkInVan.setTypeface(AppConstants.Roboto_Condensed);

		//setting the User name and User-type as header
//		curencyCode = preference.getStringFromPreference(Preference.CURRENCY_CODE, "AED");
		if(preference.getStringFromPreference(Preference.USER_NAME, "") != null && !preference.getStringFromPreference(Preference.USER_NAME, "").equalsIgnoreCase(""))
			tvUserName.setText(preference.getStringFromPreference(Preference.USER_NAME, ""));

		if(preference.getStringFromPreference(Preference.SALESMANCODE, "") != null && !preference.getStringFromPreference(Preference.SALESMANCODE, "").equalsIgnoreCase(""))
		{
			tvUserType.setText(preference.getStringFromPreference(Preference.SALESMANCODE, ""));
		}

		Log.e("siteNo", "siteNo from Pref- "+preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, ""));

		decimalFormat = new DecimalFormat("##.##");
		decimalFormat.setMinimumFractionDigits(2);
		decimalFormat.setMaximumFractionDigits(2);

		percentFormat  = new DecimalFormat("##.##");
		percentFormat.setMinimumFractionDigits(0);
		percentFormat.setMaximumFractionDigits(2);


		amountFormate  = new DecimalFormat("#,##,##,##,###.##");
		amountFormate.setMinimumFractionDigits(2);
		amountFormate.setMaximumFractionDigits(2);

		btnSetting.setEnabled(true);
		btnSetting.setClickable(true);

		btnMessage.setEnabled(true);
		btnMessage.setClickable(true);

		llMenu.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				btnMenu.performClick();
			}
		});
		btnMenu.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				hideKeyBoard(v);
				hideCustomKeyBoard();
				TopBarMenuClick();

				if(isCheckin())
				{
					if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
						adapter.refreshDashBoardOptionsCustomAdapter(AppConstants.loadCheckinMenuforPreseller(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "")));
					else
						adapter.refreshDashBoardOptionsCustomAdapter(AppConstants.loadCheckinMenu());
				}
				else
				{
					if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
						adapter.refreshDashBoardOptionsCustomAdapter(AppConstants.loadMenuforPreseller(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "")));
					else
						adapter.refreshDashBoardOptionsCustomAdapter(AppConstants.loadMenu());
				}

				if(getApplicationContext() instanceof SalesManRecommendedOrder)
				{
					((SalesManRecommendedOrder) getApplicationContext()).checkCartSaveAvailability();
				}
			}
		});

		btnLogo.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				btnMenu.performClick();
			}
		});
		btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		btnLoginLogout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.do_you_want_to_logout), getResources().getString(R.string.Yes), getResources().getString(R.string.No), "logout");
			}
		});
		btnMessage.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

			}
		});

		btnCheckOut.setTag("");
		btnCheckOut.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showCustomDialog(BaseActivity.this, getString(R.string.warning), "Do you want to check out?", "Yes", "No", "checkout");
			}
		});

		btnCheckOut.setVisibility(View.VISIBLE);
		ivLogOut.setVisibility(View.VISIBLE);
		btnChkOutVan.setVisibility(View.GONE);
		btnChkInVan.setVisibility(View.GONE);

		btnSetting.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

			}
		});


		initialize();

		ivShoppingCart.setVisibility(View.GONE);
		btnCartCount.setVisibility(View.GONE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstants.ACTION_LOGOUT);
		registerReceiver(LogoutReceiver, filter);

		IntentFilter filters = new IntentFilter();
		filters.addAction(AppConstants.ACTION_HOUSE_LIST);
		registerReceiver(ActionHouseList, filters);

		IntentFilter filtersNew = new IntentFilter();
		filtersNew.addAction(AppConstants.ACTION_HOUSE_LIST_NEW);
		registerReceiver(ActionHouseListNew, filtersNew);

		IntentFilter filtersCURL = new IntentFilter();
		filtersCURL.addAction(AppConstants.ACTION_GOTO_CRLMAIN);
		registerReceiver(ActionCustomerList, filtersCURL);

		IntentFilter filtersAR = new IntentFilter();
		filtersAR.addAction(AppConstants.ACTION_GOTO_AR);
		registerReceiver(ActionAR, filtersAR);

		IntentFilter filtersCRL = new IntentFilter();
		filtersCRL.addAction(AppConstants.ACTION_GOTO_CRL);
		registerReceiver(ActionCRL, filtersCRL);

		IntentFilter filtersHome = new IntentFilter();
		filtersHome.addAction(AppConstants.ACTION_GOTO_HOME);
		registerReceiver(ActionHome, filtersHome);

		IntentFilter filtersHome2 = new IntentFilter();
		filtersHome2.addAction(AppConstants.ACTION_GOTO_JOURNEY);
		registerReceiver(ActionJourney, filtersHome2);

		IntentFilter filtersHome3 = new IntentFilter();
		filtersHome2.addAction(AppConstants.ACTION_GOTO_NEXT_DAY_JOURNEY);
		registerReceiver(ActionJourney, filtersHome3);

		IntentFilter filtersHome1 = new IntentFilter();
		filtersHome1.addAction(AppConstants.ACTION_GOTO_HOME1);
		registerReceiver(ActionHome1, filtersHome1);


		IntentFilter sqliteFileDownload = new IntentFilter();
		sqliteFileDownload.addAction(AppConstants.ACTION_SQLITE_FILE_DOWNLOAD);
		registerReceiver(ActionSQLitefileDownload, sqliteFileDownload);

		IntentFilter menuClick = new IntentFilter();
		menuClick.addAction(AppConstants.ACTION_MENUCLICK);
		registerReceiver(MenuClick, menuClick);


//		enableTouchToChilds(llBodyRight);

		if(AppConstants.productCatalogPath == null || AppConstants.productCatalogPath.length() <= 0)
			AppConstants.productCatalogPath = 	Environment.getExternalStorageDirectory().getAbsolutePath()+"/Kwality/";

		if(AppConstants.KwalityLogoPath == null || AppConstants.KwalityLogoPath.length() <= 0)
			AppConstants.KwalityLogoPath 	= 	AppConstants.productCatalogPath+"/"+AppConstants.APPMEDIALOGOFOLDERNAME;

		ivShoppingCart.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(!(BaseActivity.this instanceof SalesmanAddedItemCart)){
					Intent intent  =  new Intent(BaseActivity.this,SalesmanAddedItemCart.class);
					intent.putExtra("mallsDetails",mallsDetailss);
					startActivity(intent);
				}
			}
		});
		btnCartCount.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				ivShoppingCart.performClick();
			}
		});
		initializeKeyboard();

		strDashBoardOptions = AppConstants.presellerOptionNewGT;
		DashBoardLoogs = AppConstants.presellerOptionNewLoogsGT;

		strDashBoardOptionsafterCheckin = AppConstants.checkinOptionNewGT;
		DashBoardLoogsafterCheckin = AppConstants.checkinOptionNewLoogsGT;

		if(adapter == null)
		{
			adapter = new DashBoardOptionsCustomAdapter(AppConstants.loadMenu());
			lvDashBoard.setAdapter(adapter);
			lvDashBoard.setCacheColorHint(0);
			lvDashBoard.setScrollBarStyle(0);
			lvDashBoard.setScrollbarFadingEnabled(true);
			lvDashBoard.setDividerHeight(1);
			lvDashBoard.setDivider(getResources().getDrawable(R.drawable.saparetor_dash));
		}
		closeDrawer();
	}

	public boolean isCheckin()
	{
		return new CustomerDA().isCheckedIn();
	}

	public interface Predicate<T> {
		boolean apply(T type);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		String dbPath = getApplication().getFilesDir().toString() + "/";
		if(AppConstants.DATABASE_PATH == null || AppConstants.DATABASE_PATH.equalsIgnoreCase(""))
			AppConstants.DATABASE_PATH = preference.getStringFromPreference(preference.DataBasePath, dbPath);

		if(AppConstants.Roboto_Condensed == null)
			AppConstants.Roboto_Condensed = Typeface.createFromAsset(getApplicationContext().getAssets(), "Roboto_Condensed.ttf");
		if(AppConstants.Roboto_Condensed_Bold == null)
			AppConstants.Roboto_Condensed_Bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "Roboto_Condensed_bold.ttf");

		if(btnCartCount.getVisibility() == View.VISIBLE)
		{
			new Thread(new Runnable() {

				@Override
				public void run()
				{
					mallsDetailss.cartItemCount = new OrderDA().getCartCount(mallsDetailss.site, AppConstants.CART_TYPE);
					runOnUiThread(new  Runnable()
					{
						public void run()
						{
							btnCartCount.setText(mallsDetailss.cartItemCount+"");
						}
					});
				}
			}).start();
		}
	}

	public void isBuildExpired(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				final int isexpired = new CommonDA().isBuildExpired(preference.getStringFromPreference(Preference.BUILD_INSTALLATIONDATE, ""));
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(isexpired==1){
							Intent intentBrObj = new Intent();
							intentBrObj.setAction(AppConstants.ACTION_LOGOUT);
							sendBroadcast(intentBrObj);
						}
					}
				});
			}
		}).start();
	}
	public static <T> Collection<T> filter(Collection<T> col, Predicate<T> predicate) {

		Collection<T> result = new ArrayList<T>();
		if(col!=null)
		{
			for (T element : col) {
				if (predicate.apply(element)) {
					result.add(element);
				}
			}
		}
		return result;
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
		hideCustomKeyBoard();
	}

	@Override
	public void onPageScrollStateChanged(int arg0)
	{

	}

	@Override
	public void onPageSelected(int arg0)
	{
		refreshPager(arg0);
		hideCustomKeyBoard();
		hideKeyBoard(tvUserName);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {

		hideCustomKeyBoard();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{

	}

	@Override
	public void onEditClick(CustomEditText view,int level)
	{
		onKeyboardFocus(view,level,false);
	}

	public void refreshPager(int position)
	{
	}

	private void initializeKeyboard()
	{

		customKeyBoardpopup = new PopupWindow(BaseActivity.this);
		customKeyBoard = inflater.inflate(R.layout.popup_keyboard, null);
		customKeyBoardpopup.setContentView(customKeyBoard);
		customKeyBoardpopup.setBackgroundDrawable(new ColorDrawable(
				android.graphics.Color.TRANSPARENT));
		customKeyBoardpopup.setOutsideTouchable(true);
		customKeyBoardpopup.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
		customKeyBoardpopup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		customKeyBoardpopup.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				llBody.requestFocus();
			}
		});
		tvNext = (TextView) customKeyBoard.findViewById(R.id.next);
		tvNine = (TextView) customKeyBoard.findViewById(R.id.keyNine);
		tvEight = (TextView) customKeyBoard.findViewById(R.id.keyEight);
		tvSeven = (TextView) customKeyBoard.findViewById(R.id.keySeven);
		tvSix = (TextView) customKeyBoard.findViewById(R.id.keySix);
		tvFive = (TextView) customKeyBoard.findViewById(R.id.keyFive);
		tvFour = (TextView) customKeyBoard.findViewById(R.id.keyFour);
		tvThree = (TextView) customKeyBoard.findViewById(R.id.keyThree);
		tvTwo = (TextView) customKeyBoard.findViewById(R.id.keyTwo);
		tvOne = (TextView) customKeyBoard.findViewById(R.id.keyOne);
		tvZero = (TextView) customKeyBoard.findViewById(R.id.keyZero);
		tvDone = (TextView) customKeyBoard.findViewById(R.id.done);
		tvDot  = (TextView) customKeyBoard.findViewById(R.id.keyDot);
		tvClear = (TextView) customKeyBoard.findViewById(R.id.keyClear);
		keyBack = (ImageView) customKeyBoard.findViewById(R.id.keyBack);

		tvDone.setOnClickListener(this);
		tvNine.setOnClickListener(this);
		tvEight.setOnClickListener(this);
		tvSeven.setOnClickListener(this);
		tvSix.setOnClickListener(this);
		tvFive.setOnClickListener(this);
		tvFour.setOnClickListener(this);
		tvThree.setOnClickListener(this);
		tvTwo.setOnClickListener(this);
		tvOne.setOnClickListener(this);
		tvZero.setOnClickListener(this);
		tvDot.setOnClickListener(this);

		tvClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
//				View etFocus = getWindow().getCurrentFocus();
//				if (etFocus instanceof EditText)
//		        {
//					EditText focusCurrent = (EditText)etFocus;
//					focusCurrent.setText("");
//		        }
				if(globalView != null && globalView instanceof EditText)
				{
					((EditText)globalView).setText("");
				}
			}
		});

		keyBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{

				// View etFocus = getWindow().getCurrentFocus();// edtSearch
				View etFocus = globalView;
				if (etFocus instanceof EditText)
				{
					EditText focusCurrent = (EditText)etFocus;
					focusCurrent.clearFocus();
					focusCurrent.requestFocus();
					focusCurrent.setCursorVisible(true);
					int start = focusCurrent.getSelectionStart();
					Editable editable = focusCurrent.getText();
					if( editable != null && start > 0 )
						editable.delete(start - 1, start);

				}


			}
		});

		tvDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				Log.e("tvDone", "tvDone");
				hideCustomKeyBoard();
			}
		});

		tvNext.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v)
			{
				try
				{
					/**
					 * Searching for an next Edit Text, which can take focus
					 * from the present EditText.
					 */
					/**
					 * This boolean is used to track whether the Focus
					 * is changing hands, or staying same.
					 * This situation happens when we reach the end of
					 * the View Hierarchy or we only have one Focusable View.
					 */
					boolean noFocusableView = false;
					View etFocus =  null;
					etFocus = globalView;
					View tmpView = null;
					int maxTry=50;
					int tried=0;
					while((tmpView = etFocus.focusSearch(View.FOCUS_FORWARD)) != null)
					{
						tried++;
						if(tmpView instanceof CustomEditText)
						{
							etFocus = tmpView;
							break;
						}
						if(tried==maxTry){
							break;
						}
					}
//					while((tmpView = etFocus.focusSearch(View.FOCUS_FORWARD)) != null)
//					{
//						if(tmpView instanceof EditText) 
//						{	
//							etFocus = tmpView;
//							break;
//						}
//					}
					/**
					 * No Focusable View.
					 */
					if(tmpView == null)
						noFocusableView = true;

					/**
					 * Finding the Scroll-able Parent in the Hierarchy.
					 * So that we can apply SmoothScroll.
					 */
					ViewGroup parent = (ViewGroup) etFocus.getParent();
					while(!parent.isScrollContainer())
					{
						/**
						 * Check for the parent, until we reach the base parent of
						 * the BaseActivity, which is R.id.llBody.
						 */
						if(parent.getId() == R.id.llBody)
						{
							break;
						}
						parent = (ViewGroup) parent.getParent();
					}

					/**
					 * The parent thus found can be null or
					 * a valid Scroll-able Parent.
					 */
					if(parent.isScrollContainer())
					{
						int location[] = new int[2];
						etFocus.getLocationOnScreen(location);

						int parentLocation[] = new int[2];
						parent.getLocationOnScreen(parentLocation);


//						int total = AppConstants.DEVICE_DISPLAY_HEIGHT - location[1] - (int)getResources().getDimension(R.dimen.popup_height);
//
//						if(total < /*AppConstants.KEYBOARD_OFFSET +*/ (int)getResources().getDimension(R.dimen.popup_height))
//						{
//							if(parent instanceof ExpandableListView || parent instanceof ListView)
//							{
//								((ListView)parent).smoothScrollBy(/*AppConstants.KEYBOARD_OFFSET +*/ (int)getResources().getDimension(R.dimen.popup_height) - total, 450);
//								((ListView)parent).invalidate()/*requestLayout()*/;
//							}
//							else if(parent instanceof ScrollView)
//							{
//								((ScrollView)parent).smoothScrollBy(/*AppConstants.KEYBOARD_OFFSET +*/ (int)getResources().getDimension(R.dimen.popup_height) - total, 450);
//								((ScrollView)parent).invalidate()/*requestLayout()*/;
//							}
//							else
//							{
//								Log.i("Class Info", ""+parent.getClass().getName());
//							}
//						}

						int delta = location[1] - parentLocation[1];
						if(parent instanceof ExpandableListView || parent instanceof ListView)
						{
							((ListView)parent).smoothScrollBy(delta, 450);
							((ListView)parent).invalidate()/*requestLayout()*/;
						}
						else if(parent instanceof ScrollView)
						{
							((ScrollView)parent).smoothScrollBy(delta, 450);
							((ScrollView)parent).invalidate()/*requestLayout()*/;
						}
						else
						{
							Log.i("Class Info", ""+parent.getClass().getName());
						}

					}

					globalView = etFocus;

					if (etFocus != null && etFocus instanceof EditText && !noFocusableView)
					{
						EditText nextEditText= (EditText) etFocus;

						if(nextEditText != null)
						{
							Log.e("nextEditText", "nextEditText");
							nextEditText.clearFocus();
							nextEditText.requestFocus();
							nextEditText.setCursorVisible(true);
							nextEditText.setSelection(nextEditText.getText().length());
							if(customKeyBoard != null && customKeyBoard.isShown())
								customKeyBoardpopup.dismiss();
						}
					}
					else if(customKeyBoard != null)
					{
						Log.e("nextEditText customKeyBoard", "nextEditText customKeyBoard");
						customKeyBoardpopup.dismiss();
					}

				}
				catch(Exception e)
				{
					e.printStackTrace();
					if(customKeyBoard!=null)
					{
						Log.e("nextEditText customKeyBoard Exception", "nextEditText customKeyBoard Exception");
						customKeyBoardpopup.dismiss();
					}
				}
			}
		});
	}

	@Override
	public void onClick(View v)
	{
		TextView tvLable = (TextView) v;
		if (globalView == null || globalView.getClass() != CustomEditText.class)
			return;
		EditText edittext = (EditText) globalView;
		edittext.setTextColor(Color.BLACK);
		Editable editable = edittext.getText();
		int start = edittext.getSelectionStart();

		if(tvLable.getText().toString().equalsIgnoreCase(".") && edittext.getText().toString().contains(".")
				&& edittext.getText().toString().contains(","))
		{

		}
		else
			editable.insert(start, tvLable.getText().toString());
	}


	public void hideCustomKeyBoard()
	{
		if(customKeyBoardpopup!=null && customKeyBoardpopup.isShowing())
			customKeyBoardpopup.dismiss();

		if(viewForEditText != null)
			viewForEditText = null;
	}

	public void onKeyboardFocus(View view,int level, boolean isCentre)
	{
		if((globalView == null || globalView!= view || !customKeyBoardpopup.isShowing()))
		{
//			updateKeyboard(level);
			globalView = view;
			hideKeyBoard(globalView);
			Log.e("onKeyboardFocus", "onKeyboardFocus");
			customKeyBoardpopup.dismiss();
			if(popupDrawable == null)
			{
				if(isCentre)
					popupDrawable = getResources().getDrawable(R.drawable.keyboard_bg);
				else
					popupDrawable = getResources().getDrawable(R.drawable.keyboard_bg_side);
			}

			int popupWidth = popupDrawable.getIntrinsicWidth();
			int popupHeight = popupDrawable.getIntrinsicHeight();
			int x = (globalView.getMeasuredWidth()/2) - (popupWidth/2) /*-100*/;
			int y =  0 ;
			int location[] = new int[2];
			view.getLocationOnScreen(location);

			AppConstants.DEVICE_DISPLAY_HEIGHT = preference.getIntFromPreference(Preference.DEVICE_DISPLAY_HEIGHT,0);
			Log.e("tag", AppConstants.DEVICE_DISPLAY_HEIGHT+"---"+location[0]+"---"+location[1]+"---"+customKeyBoardpopup.getContentView().getHeight());

			int total = AppConstants.DEVICE_DISPLAY_HEIGHT-location[1]-(int)getResources().getDimension(R.dimen.popup_height);

			int fiveDpInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 5, getResources().getDisplayMetrics());

			if(total<AppConstants.KEYBOARD_OFFSET)
			{
				/*
				 *  FOR 8-INCH  Y=-400
				 *  FOR 10-INCH Y=-350
				 */
				/**
				 * For reverse Pop-up, we need to subtract the
				 * Height of the Pop-up, so as to show it above the
				 * Anchor View.
				 */
				y = -popupHeight /*customKeyBoardpopup.getContentView().getMeasuredHeight()*/ - globalView.getMeasuredHeight()/*400*/ + globalView.getMeasuredHeight()/4;
				customKeyBoardpopup.getContentView().setPadding(fiveDpInPx, 0, fiveDpInPx, 2 * fiveDpInPx)/*setPadding(10,15,10,30)*/;

				if(isCentre)
					customKeyBoardpopup.getContentView().setBackgroundResource(R.drawable.keyboard_bg_reverse);
				else
					customKeyBoardpopup.getContentView().setBackgroundResource(R.drawable.keyboard_bg_side_reverse);

			}
			else
			{
				y = - globalView.getMeasuredHeight()/4;
				customKeyBoardpopup.getContentView().setPadding(fiveDpInPx, 3 * fiveDpInPx, fiveDpInPx, 0)/*setPadding(10,30,10,20)*/;
				if(isCentre)
					customKeyBoardpopup.getContentView().setBackgroundResource(R.drawable.keyboard_bg);
				else
					customKeyBoardpopup.getContentView().setBackgroundResource(R.drawable.keyboard_bg_side);
			}

			customKeyBoardpopup.showAsDropDown(globalView,x,y);

		}
		else if(globalView!=null)
			hideKeyBoard(globalView);

	}


	private void updateCheckoutStatus(String siteId)
	{
		String empId = preference.getStringFromPreference(Preference.EMP_NO, "");
		String checkOut = CalendarUtils.getCurrentTime();
		String date = CalendarUtils.getOrderPostDate();
//		new CustomerDetailsDA().updateCheckOutTimeByService(empId, date, siteId, checkOut);			
	}
//	public void uploadDatabaseIntoSDCARD()
//	{
//		try
//		{
//			showLoader(getResources().getString(R.string.please_wait_data_uploading));
//			new Thread(new Runnable()
//			{
//				@Override
//				public void run() 
//				{
//					try 
//					{
////						ServiceURLs.UPLOAD_DATABASE_URL = ServiceURLs.UPLOAD_DATABASE_URL.replace("{psid}", preference.getStringFromPreference(Preference.USER_ID, ""));
////						ServiceURLs.UPLOAD_DATABASE_URL = ServiceURLs.UPLOAD_DATABASE_URL.replace("{date}", CalendarUtils.getCurrentSalesDate()+ "-"+ CalendarUtils.getCurrentTimeToUpload());
//						//to be deleted, temporary
//						FilesStorage.copy(AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME, Environment.getExternalStorageDirectory().toString()+ "/" + AppConstants.DATABASE_NAME);
////						new HTTPHelper().uploadFileByMultipart(	ServiceURLs.UPLOAD_DATABASE_URL,AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME);
//						hideLoader();
//						showCustomDialog(BaseActivity.this, getResources().getString(R.string.successful),"Uploaded Debug logs successfully.", getResources().getString(R.string.OK), null, "");
//					} 
//					catch (Exception e)
//					{
//						e.printStackTrace();
//						showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Error occurred while uploading.", getResources().getString(R.string.OK), null, "");
//						hideLoader();
//					}
//				}
//			}).start();
//		}
//		catch (Exception e) 
//		{
//			e.printStackTrace();
//		}
//
//	}

	public void uploadDatabaseIntoSDCARD()
	{
		try
		{
			showLoader(getResources().getString(R.string.please_wait_data_uploading));
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						FilesStorage.copy(AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME, Environment.getExternalStorageDirectory().toString()+ "/" + AppConstants.DATABASE_NAME);
						String empNo=preference.getStringFromPreference(Preference.EMP_NO, "");

						ArrayList<File> arr=new ArrayList<File>();
						arr.add(new File(Environment.getExternalStorageDirectory().toString()+ "/" + AppConstants.DATABASE_NAME));
						try{
//							File f = new File(Environment.getExternalStorageDirectory().toString()+ "/" + "DeliveryLog.txt");
//							arr.add(f);
//						arr.add(new File(Environment.getExternalStorageDirectory().toString()+ "/" + AppConstants.ORDER_LOG));
						}catch(Exception e)
						{
							e.printStackTrace();
						}


						String zipFiilePath=Environment.getExternalStorageDirectory().toString()+ "/"+empNo+"_"+CalendarUtils.getOrderPostDate()+"_"+System.currentTimeMillis()+".zip";
						ZipUtils.zipFiles(arr, new File(zipFiilePath));

						if(!TextUtils.isEmpty(zipFiilePath)){
							File databaseFile = new File(zipFiilePath);
							if(databaseFile.exists()){
								uploadDB(zipFiilePath);
								databaseFile.delete();
							}
						}
						hideLoader();
						showCustomDialog(BaseActivity.this, getResources().getString(R.string.successful),"Uploaded Debug logs successfully.", getResources().getString(R.string.OK), null, "");
					}
					catch (Exception e)
					{
						e.printStackTrace();
						showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Error occurred while uploading.", getResources().getString(R.string.OK), null, "");
						hideLoader();
					}
				}
			}).start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private boolean uploadDB(String dbPath) {
		boolean isError = false;
		if(isNetworkConnectionAvailable(BaseActivity.this)){
			InputStream is = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(String.format(ServiceURLs.UPLOAD_DB, "competitor"));
				File filePath = new File(dbPath);

				if (filePath.exists()) {
					Log.e("uplaod", "called");
					MultipartEntity mpEntity = new MultipartEntity();
					ContentBody cbFile = new FileBody(filePath, "image/png");
					mpEntity.addPart("FileName", cbFile);
					httppost.setEntity(mpEntity);
					HttpResponse response;
					response = httpclient.execute(httppost);
					HttpEntity resEntity = response.getEntity();
					is = resEntity.getContent();
					if(is!=null)
						isError=false;
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
				isError = true;
			} catch (IOException e) {
				e.printStackTrace();
				isError = true;
			} catch (Exception e) {
				e.printStackTrace();
				isError = true;
			} finally {
			}
		}

		return isError;
	}

	public void performCheckouts(final boolean isCheckOut)
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				final String siteNo = preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");

				Log.e("siteNo", "siteNo - "+siteNo);
				updateCheckoutStatus(preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, ""));
//				new CustomerDetailsDA().updateJourneyLog(preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID,	""),
//														100);
//				new CustomerDA().updateCustomerCheckOutTime(CalendarUtils.getCurrentDateTime());

				new CustomerDA().updateLastJourneyLog();
				preference.removeFromPreference("lastservedcustomer");
				preference.commitPreference();

				final boolean isOrderPlace = new CommonDA().isOrderPlace(siteNo, CalendarUtils.getCurrentDateAsStringforStoreCheck());
				final Vector<String> vecstatusDO = new StatusDA().getCompletedOptionsStatus(mallsDetailss.site,AppConstants.Action_CheckIn);

				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						boolean checkOrderPlaced = false;
						if(isOrderPlace)
						{
							if(vecstatusDO.contains(AppConstants.Type_SalesOrder) ||
									vecstatusDO.contains(AppConstants.Type_PresalesOrder) ||
									vecstatusDO.contains(AppConstants.Type_Collections) ||
									vecstatusDO.contains(AppConstants.Type_ReturnOrder) ||
									vecstatusDO.contains(AppConstants.Type_MissedOrder) ||
									vecstatusDO.contains(AppConstants.Type_AdvancedOrder) ||
									vecstatusDO.contains(AppConstants.Type_SavedOrder))
							{
								checkOrderPlaced = true;
							}
						}


//						if(/*!isCheckOut || *//* || !preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_GT)*/)
						if(checkOrderPlaced)
						{
							updateCheckoutStatus(preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, ""));
							new CustomerDA().updateLastJourneyLog(preference.getStringFromPreference(Preference.VISIT_CODE, ""));
							preference.removeFromPreference("lastservedcustomer");
							preference.commitPreference();

							uploadData();
							if(BaseActivity.this instanceof CheckInOptionActivity)
							{
								finish();
							}
							Intent intentBrObj = new Intent();
							intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST);
							sendBroadcast(intentBrObj);
						}
						else
						{
//							showReasonOfCheckout(isCheckOut);
							showReasonOfCheckoutforZeroSales(isCheckOut);
//							showCustomDialog(this, getString(R.string.warning), "Do you want to check out?", "Yes", "No", "checkout");
						}

					}
				});
			}
		}).start();
	}

	public void performCheckouts(final boolean isCheckOut, final String strOptionSelected)
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				final String siteNo = preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");

				Log.e("siteNo", "siteNo - "+siteNo);
				updateCheckoutStatus(preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, ""));
//				new CustomerDetailsDA().updateJourneyLog(preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID,	""),
//														100);
//				new CustomerDA().updateCustomerCheckOutTime(CalendarUtils.getCurrentDateTime());

				new CustomerDA().updateLastJourneyLog();
				preference.removeFromPreference("lastservedcustomer");
				preference.commitPreference();

				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						if(!isCheckOut || new CommonDA().isOrderPlace(siteNo, CalendarUtils.getCurrentDateAsString()) || !preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_GT))
						{
							uploadData();
							if(BaseActivity.this instanceof CheckInOptionActivity)
							{
								finish();
							}
							Intent intentBrObj = new Intent();
							intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST);
							sendBroadcast(intentBrObj);

							//go to next after checkout by clicking menu item
							movetoNextPage(strOptionSelected);
						}
						else
						{
							showReasonOfCheckout(isCheckOut);
//							showCustomDialog(this, getString(R.string.warning), "Do you want to check out?", "Yes", "No", "checkout");
						}

					}
				});
			}
		}).start();
	}

	public void performCustomerCheckout()
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				final String siteNo = preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");

				Log.e("siteNo", "siteNo - "+siteNo);
				updateCheckoutStatus(preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, ""));
//				new CustomerDetailsDA().updateJourneyLog(preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID,	""),
//														100);
//				new CustomerDA().updateCustomerCheckOutTime(CalendarUtils.getCurrentDateTime());

//				new CustomerDA().updateLastJourneyLog();
				new CustomerDA().updateLastJourneyLog(preference.getStringFromPreference(Preference.VISIT_CODE, ""));
				preference.removeFromPreference("lastservedcustomer");
				preference.commitPreference();

				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						if(new CommonDA().isOrderPlace(siteNo, CalendarUtils.getCurrentDateAsString()))
						{
							uploadData();
							if(BaseActivity.this instanceof CheckInOptionActivity)
							{
								finish();
							}
							Intent intentBrObj = new Intent();
							intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST_NEW);
							sendBroadcast(intentBrObj);
						}
						else
						{
							showReasonOfCheckout(false);
//							showCustomDialog(this, getString(R.string.warning), "Do you want to check out?", "Yes", "No", "checkout");
						}
					}
				});
			}
		}).start();
	}

	BroadcastReceiver LogoutReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			finish();
		}
	};

	BroadcastReceiver ActionHouseList = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if((BaseActivity.this instanceof PresellerJourneyPlan) || (BaseActivity.this instanceof VehicleList) || (BaseActivity.this instanceof VehicleListPreseller) || (BaseActivity.this instanceof SalesmanCustomerList)
					||  (BaseActivity.this instanceof SalesmanARCustomerList) ||  (BaseActivity.this instanceof SalesmanReplacementCustomerList));
			else
				finish();
		}
	};

	BroadcastReceiver ActionHouseListNew = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if((BaseActivity.this instanceof PresellerJourneyPlan) || (BaseActivity.this instanceof VehicleList) || (BaseActivity.this instanceof VehicleListPreseller) || (BaseActivity.this instanceof SalesmanCustomerList)
					|| (BaseActivity.this instanceof CheckInOptionActivity) || (BaseActivity.this instanceof SalesmanARCustomerList));
			else
				finish();
		}
	};

	BroadcastReceiver ActionAR = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if((BaseActivity.this instanceof PendingInvoices)
					|| (BaseActivity.this instanceof ReceivePaymentBySalesman))
				finish();
		}
	};

	BroadcastReceiver ActionCustomerList = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if((BaseActivity.this instanceof ReceivePaymentBySalesman)
					|| (BaseActivity.this instanceof SalesManTakeReturnOrder))
				finish();
		}
	};

	BroadcastReceiver ActionCRL = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if((BaseActivity.this instanceof PresellerJourneyPlan)
					|| (BaseActivity.this instanceof VehicleList)
					|| (BaseActivity.this instanceof VehicleListPreseller)
					|| (BaseActivity.this instanceof SalesmanCustomerList)
					|| (BaseActivity.this instanceof LoginAcivity))
			{

			}
			else
				finish();
		}
	};

	BroadcastReceiver ActionHome1 = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if((BaseActivity.this instanceof PresellerJourneyPlan)
					|| (BaseActivity.this instanceof VehicleList)
					|| (BaseActivity.this instanceof VehicleListPreseller)
					|| (BaseActivity.this instanceof LoginAcivity))
			{

			}
			else
				finish();
		}
	};

	BroadcastReceiver ActionJourney = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if((BaseActivity.this instanceof VehicleList || (BaseActivity.this instanceof VehicleListPreseller) )
					|| (BaseActivity.this instanceof LoginAcivity))
			{

			}
			else
				finish();
		}
	};
	BroadcastReceiver ActionHome = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
//			if((BaseActivity.this instanceof PresellerJourneyPlan) 
//					|| (BaseActivity.this instanceof VehicleList)
//					|| (BaseActivity.this instanceof LoginAcivity))
//			{
//				
//			}
//			else
//				finish();
		}
	};

	BroadcastReceiver ActionSQLitefileDownload = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Toast.makeText(BaseActivity.this, "SQLite file downloaded successfully.", Toast.LENGTH_SHORT).show();
		}
	};

	/**
	 * to show security buttons
	 */
	public void showSecurityButtons()
	{
		btnChkOutVan.setVisibility(View.VISIBLE);
		btnChkInVan.setVisibility(View.VISIBLE);
	}

	/**
	 * to hide security buttons
	 */
	public void hideSecurityButtons()
	{
		btnChkOutVan.setVisibility(View.GONE);
		btnChkInVan.setVisibility(View.GONE);
	}

	public abstract void initialize();

	/** Method to Show the alert dialog **/
	public void showDialog(Context context, String strTitle, String strMessage)
	{
		runOnUiThread(new RunShowDialog(context, strTitle, strMessage, null,null));
	}

	/** Method to Show the alert dialog with click listener **/
	public void showDialog(Context context, String strTitle, String strMessage,	DialogInterface.OnClickListener dialogClickListener)
	{
		runOnUiThread(new RunShowDialog(context, strTitle, strMessage,	dialogClickListener, null));
	}

	/** Method to Show the alert dialog with click listener **/
	public void showDialog(Context context, String strTitle, String strMessage,	DialogInterface.OnClickListener posDialogClickListener,	DialogInterface.OnClickListener negDialogClickListener)
	{
		runOnUiThread(new RunShowDialog(context, strTitle, strMessage,	posDialogClickListener, negDialogClickListener));
	}

	/**
	 * This method is to show the loading progress dialog when some other
	 * functionality is taking place.
	 **/
	public void showLoader(String msg)
	{
		runOnUiThread(new RunShowLoader(msg, ""));
	}

	/**
	 * This method is to show the loading progress dialog when some other
	 * functionality is taking place.
	 **/
	public void showLoader(String msg, String title)
	{
		runOnUiThread(new RunShowLoader(msg, title));
	}

	/** For hiding progress dialog (Loader ). **/
	public void hideLoader()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					if (progressdialog != null && progressdialog.isShowing())
						progressdialog.dismiss();
					progressdialog = null;
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	// For showing Dialog message.
	class RunShowDialog implements Runnable
	{
		private String strTitle;// Title of the dialog
		private String strMessage;// Message to be shown in dialog
		private Context context; // Context for showing dialog
		private DialogInterface.OnClickListener posDialogClickListener,	negDialogClickListener;

		public RunShowDialog(Context context, String strTitle,String strMessage,DialogInterface.OnClickListener posDialogClickListener,	DialogInterface.OnClickListener negDialogClickListener)
		{
			this.context = context;
			this.strTitle = strTitle;
			this.strMessage = strMessage;
			this.posDialogClickListener = posDialogClickListener;
			this.negDialogClickListener = negDialogClickListener;
		}

		@Override
		public void run()
		{
			// Creating Alert Dialog object
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(strTitle);// setting the title of the alert dialog
			builder.setCancelable(false);
			builder.setMessage(strMessage);// setting the message of the alert
			// dialog

			// Clicking on OK button, dismiss the dialog.
			if (posDialogClickListener == null)
			{
				builder.setPositiveButton(getResources().getString(R.string.OK)/* * getResources().getString * (R.string.Ok)*/,	new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog,	int which)
					{
						dialog.dismiss(); // dismiss the dialog.
						onDialogClick(strMessage);
					}
				});
			}
			else
			{
				builder.setPositiveButton(getResources().getString(R.string.OK)/** getResources().getString * (R.string.Ok) */,	posDialogClickListener);
			}
			if (negDialogClickListener != null)
			{
				builder.setNegativeButton(getResources().getString(	R.string.cancel), negDialogClickListener);
			}
			AlertDialog dialog = builder.create();
			if (!dialog.isShowing())
				dialog.show();// this is to show the dialog.
		}
	}

	public void onDialogClick(String mssg)
	{
	}

	// This is to show the loading progress dialog when some other functionality
	// is taking place.
	class RunShowLoader implements Runnable
	{
		private String strMsg;
		private String title;

		public RunShowLoader(String strMsg, String title)
		{
			this.strMsg = strMsg;
			this.title = title;
		}

		@Override
		public void run()
		{
			try
			{
				if (progressdialog == null|| (progressdialog != null && !progressdialog.isShowing()))
				{
					progressdialog = ProgressDialog.show(BaseActivity.this,	title, strMsg);
				}
				else if (progressdialog == null|| (progressdialog != null && progressdialog.isShowing()))
				{
					progressdialog.setMessage(strMsg);
				}
			}
			catch (Exception e)
			{
				progressdialog = null;
			}
		}
	}


	private String strDashBoardOptions[];
	private int DashBoardLoogs[];

	private String strDashBoardOptionsafterCheckin[];
	private int DashBoardLoogsafterCheckin[];


	private ExpandableListView lvDashboardOptions;
	private PopupWindow optionMenuPopup;
	public void showOptionPopup(View view)
	{
		strDashBoardOptions = AppConstants.presellerOptionNewGT;
		DashBoardLoogs = AppConstants.presellerOptionNewLoogsGT;

		strDashBoardOptionsafterCheckin = AppConstants.checkinOptionNewGT;
		DashBoardLoogsafterCheckin = AppConstants.checkinOptionNewLoogsGT;

		btnLogo.setBackgroundResource(R.drawable.logo_hov);
		optionMenuPopup = new PopupWindow(BaseActivity.this);
		LayoutInflater inflater = (LayoutInflater) BaseActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View llEntirechrt = inflater.inflate(R.layout.option_layout, null, true);
		optionMenuPopup = new PopupWindow(llEntirechrt, preference.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 50, LayoutParams.WRAP_CONTENT, true);
		optionMenuPopup.setOutsideTouchable(true);
		optionMenuPopup.setTouchInterceptor(new OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
				{
					optionMenuPopup.dismiss();
					return true;
				}
				return false;
			}
		});
		optionMenuPopup.setBackgroundDrawable(new BitmapDrawable());
		optionMenuPopup.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss()
			{
				btnLogo.setBackgroundResource(R.drawable.logo_click);
			}
		});
		LinearLayout llPopup = (LinearLayout) llEntirechrt.findViewById(R.id.llPopup);
		lvDashboardOptions = new ExpandableListView(BaseActivity.this);
		lvDashboardOptions.setGroupIndicator(null);

		if (strDashBoardOptions != null)
		{
			lvDashboardOptions.setCacheColorHint(0);
			lvDashboardOptions.setScrollBarStyle(0);
			lvDashboardOptions.setScrollbarFadingEnabled(true);
			lvDashboardOptions.setDivider(null);
			lvDashboardOptions.setAdapter(new DashBoardOptionsCustomAdapter(AppConstants.loadMenu()));
		}
		llPopup.addView(lvDashboardOptions,	new LinearLayout.LayoutParams(preference.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 50,LayoutParams.WRAP_CONTENT));
		optionMenuPopup.showAsDropDown(view, 10, 0);
	}

	@Override
	public void onBackPressed()
	{
		if(llDashBoard.isShown())
			TopBarMenuClick();
		else if(customKeyBoardpopup != null && customKeyBoardpopup.isShowing())
			customKeyBoardpopup.dismiss();
		else
			super.onBackPressed();
	}

	public class DashBoardOptionsCustomAdapter extends BaseExpandableListAdapter
	{
		private Vector<MenuDO> vecMenuDOs ;

		protected DashBoardOptionsCustomAdapter(Vector<MenuDO> vecMenuDOs)
		{
			this.vecMenuDOs = vecMenuDOs;
		}

		public void refreshDashBoardOptionsCustomAdapter(Vector<MenuDO> vecMenuDOs)
		{
			this.vecMenuDOs = vecMenuDOs;
			notifyDataSetInvalidated();
		}

		@Override
		public int getGroupCount()
		{
			return vecMenuDOs.size();
		}

		@Override
		public int getChildrenCount(int groupPosition)
		{
			return vecMenuDOs.get(groupPosition).vecMenuDOs.size();
		}

		@Override
		public Object getGroup(int groupPosition)
		{
			return vecMenuDOs.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition)
		{
			return vecMenuDOs.get(groupPosition).vecMenuDOs.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition)
		{
			return groupPosition;
		}

		@Override

		public long getChildId(int groupPosition, int childPosition)
		{
			return childPosition;
		}

		@Override
		public boolean hasStableIds()
		{
			return false;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,View convertView, ViewGroup parent)
		{
			convertView = (LinearLayout)inflater.inflate(R.layout.dashboard_options_cell, null);
			ImageView ivOptionIcon 	= 	(ImageView) convertView.findViewById(R.id.ivOptionIcon);
			ImageView ivArrowIcon 	= 	(ImageView) convertView.findViewById(R.id.ivArrowIcon);
			TextView tvOptionName 	= 	(TextView) convertView.findViewById(R.id.tvOptionName);
			LinearLayout rlCalender = 	(LinearLayout) convertView.findViewById(R.id.rlCalender);
			ImageView ivFooter		= 	(ImageView) convertView.findViewById(R.id.ivFooter);

			ivOptionIcon.setImageResource(vecMenuDOs.get(groupPosition).menuImage);
			tvOptionName.setText(vecMenuDOs.get(groupPosition).menuName);
			tvOptionName.setTypeface(AppConstants.Roboto_Condensed);

			if(vecMenuDOs.get(groupPosition).vecMenuDOs.size()>0)
				ivArrowIcon.setVisibility(View.VISIBLE);
			else
				ivArrowIcon.setVisibility(View.GONE);
			if(groupPos == groupPosition)
			{
				if(isExpanded)
				{
					ExpandableListView mExpandableListView = (ExpandableListView) parent;
					mExpandableListView.expandGroup(groupPosition);
				}
				else
				{
					ExpandableListView mExpandableListView = (ExpandableListView) parent;
					mExpandableListView.collapseGroup(groupPosition);
				}
			}


			if(vecMenuDOs.get(groupPosition).menuName.equalsIgnoreCase("footer"))
			{
				rlCalender.setVisibility(View.GONE);
				ivFooter.setVisibility(View.VISIBLE);
			}
			else
			{
				rlCalender.setVisibility(View.VISIBLE);
				ivFooter.setVisibility(View.GONE);
			}

			if(vecMenuDOs.get(groupPosition).vecMenuDOs.size() == 0)
			{
				convertView.setTag(vecMenuDOs.get(groupPosition).menuName);
				convertView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(final View v)
					{
						TopBarMenuClick();
						groupPos = -1;
						new Handler().postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								if(!v.getTag().toString().equalsIgnoreCase("Checkout"))
								{
									Intent intent = new Intent();
									intent.setAction(AppConstants.ACTION_GOTO_HOME);
									sendBroadcast(intent);
								}

								moveToNextAcivityGT(v.getTag().toString());
							}
						}, 400);
					}
				});
			}


			if(isExpanded)
			{
				ivArrowIcon.setBackgroundResource(R.drawable.menu_arrow_down);
			}
			else
				ivArrowIcon.setBackgroundResource(R.drawable.menu_arrow_right);

			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, (int) (55 * px)));

			return convertView;
		}

		@Override
		public View getChildView(final int groupPosition, int childPosition,boolean isLastChild, View convertView, ViewGroup parent)
		{
			Vector<MenuDO> vecDos = vecMenuDOs.get(groupPosition).vecMenuDOs;

			convertView = (LinearLayout)inflater.inflate(R.layout.dashboard_options_cell, null);
			convertView.setBackgroundResource(R.color.dark_menu);
			ImageView ivOptionIcon 	= 	(ImageView) convertView.findViewById(R.id.ivOptionIcon);
			TextView tvOptionName 	= 	(TextView) convertView.findViewById(R.id.tvOptionName);
			LinearLayout rlCalender = 	(LinearLayout) convertView.findViewById(R.id.rlCalender);
			ImageView ivFooter		= 	(ImageView) convertView.findViewById(R.id.ivFooter);
			ImageView ivArrowIcon 	= 	(ImageView) convertView.findViewById(R.id.ivArrowIcon);

			ivOptionIcon.setImageResource(vecDos.get(childPosition).menuImage);
			ivArrowIcon.setVisibility(View.GONE);
			tvOptionName.setText(vecDos.get(childPosition).menuName);
			tvOptionName.setTypeface(AppConstants.Roboto_Condensed);

//			if(strDashBoardOptions[position].equalsIgnoreCase("footer"))
//			{
//				rlCalender.setVisibility(View.GONE);
//				ivFooter.setVisibility(View.VISIBLE);
//			}
//			else
//			{
//				rlCalender.setVisibility(View.VISIBLE);
//				ivFooter.setVisibility(View.GONE);
//			}

			convertView.setTag(vecDos.get(childPosition).menuName);
			convertView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(final View v)
				{
					groupPos = groupPosition;

					TopBarMenuClick();
					new Handler().postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							Intent intent = new Intent();
							intent.setAction(AppConstants.ACTION_GOTO_HOME);
							sendBroadcast(intent);

							moveToNextAcivityGT(v.getTag().toString());
						}
					}, 400);
				}
			});

			convertView.setPadding(20, 5, 5, 5);
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, (int) (55 * px)));

			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition)
		{
			return true;
		}

	}

//	public class DashBoardOptionsCustomAdapter extends BaseAdapter 
//	{
//		
//		protected DashBoardOptionsCustomAdapter(Vector<MenuDO> vecMenuDOs)
//		{
//			
//		}
//		
//		@Override
//		public int getCount() 
//		{
//			return strDashBoardOptions.length;
//		}
//
//		@Override
//		public Object getItem(int position) 
//		{
//			return position;
//		}
//
//		@Override
//		public long getItemId(int position)
//		{
//			return position;
//		}
//
//		@Override
//		public View getView(final int position, View convertView, ViewGroup parent)
//		{
//			convertView 			= 	inflater.inflate(R.layout.dashboard_options_cell, null);
//			ImageView ivOptionIcon 	= 	(ImageView) convertView.findViewById(R.id.ivOptionIcon);
//			TextView tvOptionName 	= 	(TextView) convertView.findViewById(R.id.tvOptionName);
//			LinearLayout rlCalender = 	(LinearLayout) convertView.findViewById(R.id.rlCalender);
//			ImageView ivFooter		= 	(ImageView) convertView.findViewById(R.id.ivFooter);
//			
//			ivOptionIcon.setImageResource(DashBoardLoogs[position]);
//			tvOptionName.setText(strDashBoardOptions[position]);
//			tvOptionName.setTypeface(AppConstants.Roboto_Condensed);
//			
//			if(strDashBoardOptions[position].equalsIgnoreCase("footer"))
//			{
//				rlCalender.setVisibility(View.GONE);
//				ivFooter.setVisibility(View.VISIBLE);
//			}
//			else
//			{
//				rlCalender.setVisibility(View.VISIBLE);
//				ivFooter.setVisibility(View.GONE);
//			}
//				
//			convertView.setTag(strDashBoardOptions[position]);
//			convertView.setOnClickListener(new OnClickListener() 
//			{
//				@Override
//				public void onClick(final View v) 
//				{
//					TopBarMenuClick();
//					new Handler().postDelayed(new Runnable()
//					{
//						@Override
//						public void run()
//						{
//							Intent intent = new Intent();
//							intent.setAction(AppConstants.ACTION_GOTO_HOME);
//							sendBroadcast(intent);
//							
//							moveToNextAcivityGT(v.getTag().toString());
//						}
//					}, 400);
//				}
//			});
//
//			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, (int) (55 * px)));
//			return convertView;
//		}
//	}




	public void moveToNextAcivityGT(String strOptionSelected)
	{
//		if(btnCheckOut.isShown())
		if(isCheckin())
		{
			/*if(strOptionSelected.equalsIgnoreCase(strDashBoardOptions[1]))
				showCustomDialog(BaseActivity.this, getString(R.string.warning), "Do you want to check out?", "Yes", "No", "checkout_"+strOptionSelected);
			else*/ if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
		{
			strDashBoardOptionsafterCheckin = AppConstants.checkinMenuOptionPresellerGT;
			DashBoardLoogsafterCheckin		= AppConstants.checkinMenuOptionPresellerLogosGT;

			movetoNextPagewhileCheckINforPreseller(strOptionSelected);
		}
		else
			movetoNextPagewhileCheckIN(strOptionSelected);
		}
		else
		{
			if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
			{
				strDashBoardOptions = AppConstants.menuOptionPreseller;
				DashBoardLoogs = AppConstants.menuOptionPresellerLogos;

				movetoNextPageforPreseller(strOptionSelected);
			}
			else
				movetoNextPage(strOptionSelected);
		}
	}

	public void movetoNextPage(String strOptionSelected)
	{//Journey Plan
//		menuClickBCRAction();
		if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[0])&& !(BaseActivity.this instanceof PresellerJourneyPlan))
		{
			//showCustomDialog(BaseActivity.this, getString(R.string.warning), "Do you want to finish the current task?", "Yes", "No", "journeyplan");
			menuClickBCRAction();
			Intent objIntent = new Intent();
			objIntent.setAction(AppConstants.ACTION_GOTO_JOURNEY);
			sendBroadcast(objIntent);

			Intent intent = new Intent(BaseActivity.this, PresellerJourneyPlan.class);
			// dont't delete below line by sudheer
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[1])  && !(BaseActivity.this instanceof ProductCatalogActivity))
		{//Product Catalog
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, ProductCatalogActivity.class);
				// don't delete this by sudheer 
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[2])&& !(BaseActivity.this instanceof SalesmanCustomerList))
		{//My Customers
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				//Customer list 
				Intent intent = new Intent(BaseActivity.this, SalesmanCustomerList.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				intent.putExtra("isHistory", false);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.loadMangeMentOption[0])&& !(BaseActivity.this instanceof AddStockInVehicle))
		{//Load Management-->Van Stock
			menuClickBCRAction();
			preference.saveBooleanInPreference(Preference.IS_VANSTOCK_FROM_MENU_OPTION, true);
			preference.commitPreference();

			Intent intent = new Intent(BaseActivity.this, AddStockInVehicle.class);
			intent.putExtra(Preference.VEHICLE_DO, (VehicleDO)preference.getVehicleObjectFromPreference("VehicleDO"));
			intent.putExtra("isPreview", true);
			// dont't delete below line by sudheer
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}
//		else if (strOptionSelected.equalsIgnoreCase(AppConstants.loadMangeMentOption[1]) && !(BaseActivity.this instanceof VerifyItemInVehicle))
//		{//Stock Received
//			menuClickBCRAction();
//			Intent intent = new Intent(BaseActivity.this,VanStockTransfer.class);
//			intent.putExtra("updatededDate",new ArrayList<VanLoadDO>());
//			// dont't delete below line by sudheer
//			intent.putExtra("mallsDetails",mallsDetails);
//			intent.putExtra("isMenu",false);
//			startActivityForResult(intent, 1111);
//		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.loadMangeMentOption[1]) && !(BaseActivity.this instanceof VerifyApprovedMomnetsActivity))
		{//Load Management-->Stock Received
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this,VerifyApprovedMomnetsActivity.class);
				intent.putExtra("load_type", AppConstants.LOAD_STOCK);
				intent.putExtra("isUnload",false);
				intent.putExtra("isMenu",false);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivityForResult(intent, 1111);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryOption[0]) && !(BaseActivity.this instanceof OrderSummary))
		{//Execution Summary-->Order Summary
			menuClickBCRAction();
			Intent intent = new Intent(BaseActivity.this, OrderSummary.class);
			// don't delete this by sudheer 
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryOption[1])  && !(BaseActivity.this instanceof PaymentSummaryActivity))
		{//Execution Summary-->Payment Summary
			menuClickBCRAction();
			Intent intent = new Intent(BaseActivity.this, PaymentSummaryActivity.class);
			// don't delete this by sudheer 
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}

		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryOption[2]) && !(BaseActivity.this instanceof SurveyNewActivity))
		{//Execution Summary-->Survey
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, SurveyNewActivity.class);
				// don't delete this by sudheer 
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryOption[3]) && !(BaseActivity.this instanceof CompetitorsListActivity))
		{//Execution Summary-->Competitive Execution
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, CompetitorsListActivity.class);
				// don't delete this by sudheer 
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryOption[4]) && !(BaseActivity.this instanceof AssetRequestActivity))
		{//Execution Summary-->Asset Activities
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, AssetsRequestCustomerList.class);
				// don't delete this by sudheer 
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryOption[5]) && !(BaseActivity.this instanceof OrderTraceActivity))
		{//Log Report
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				Intent intent = new Intent(BaseActivity.this, LogReportActivity.class);
//				intent.putExtra("mallsDetails",mallsDetails);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[5]) && !(BaseActivity.this instanceof Inventory_QTY))
		{//Stock Inventory
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
//			{
//				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
//			}
//			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, Inventory_QTY.class);
				startActivity(intent);
			}
		}
		/*else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[6]) && !(BaseActivity.this instanceof CustomerContactDetail))
		{//Add New Customers
			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, CustomerContactDetail.class);
				// don't delete this by sudheer 
				intent.putExtra("mallsDetails",mallsDetails);
				startActivity(intent);
			}
		}*/
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[6]) && !(BaseActivity.this instanceof CashDenominationActivity))
		{//Cash Denomination
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, CashDenominationActivity.class);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[7]) && !(BaseActivity.this instanceof DailySummaryActivity))
		{//Daily Summary
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
//			{
//				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
//			}
//			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, DailySummaryActivity.class);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[8]) && !(BaseActivity.this instanceof SalesmanSummaryofDay))
		{//EOT
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intents = new Intent(BaseActivity.this,	SalesmanSummaryofDay.class);
				intents.putExtra("dateofJorney", getCurrentdate());
				intents.putExtra("SalesmanCode", preference.getStringFromPreference(Preference.SALESMANCODE, ""));
				intents.putExtra("EmpId", preference.getStringFromPreference(Preference.EMP_NO, ""));
				intents.putExtra("isSupervisor", false);
				intents.putExtra("isEot", true);
				// don't delete this by sudheer 
				intents.putExtra("mallsDetails",mallsDetailss);
				preference.saveStringInPreference("EOTType", "Normal");
				preference.saveStringInPreference("EOTReason", "");
				preference.commitPreference();
				startActivity(intents);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.othersOption[0]) && !(BaseActivity.this instanceof Settings))
		{//Others-->Settings
			menuClickBCRAction();
			Intent intent = new Intent(BaseActivity.this, Settings.class);
			// don't delete this by sudheer 
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.othersOption[1]) && !(BaseActivity.this instanceof CompetitorsListActivity))
		{//Others-->Capture Competitor
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, CompetitorsListActivity.class);
				// don't delete this by sudheer 
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.othersOption[2]) && !(BaseActivity.this instanceof AboutApplicationActivity))
		{//Others-->About Application
			menuClickBCRAction();
			Intent intent = new Intent(BaseActivity.this, AboutApplicationActivity.class);
			// don't delete this by sudheer 
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[10]))
		{
			if (btnLoginLogout != null)
				btnLoginLogout.performClick();
		}
	}

	public void movetoNextPageforPreseller(String strOptionSelected)
	{//Journey Plan for Preseller
//		menuClickBCRAction();
		if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[0])&& !(BaseActivity.this instanceof PresellerJourneyPlan))
		{
			//showCustomDialog(BaseActivity.this, getString(R.string.warning), "Do you want to finish the current task?", "Yes", "No", "journeyplan");
			menuClickBCRAction();
			Intent objIntent = new Intent();
			objIntent.setAction(AppConstants.ACTION_GOTO_JOURNEY);
			sendBroadcast(objIntent);

			Intent intent = new Intent(BaseActivity.this, PresellerJourneyPlan.class);
			// dont't delete below line by sudheer
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[1])  && !(BaseActivity.this instanceof ProductCatalogActivity))
		{//Product Catalog
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, ProductCatalogActivity.class);
				// don't delete this by sudheer 
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[2])&& !(BaseActivity.this instanceof SalesmanCustomerList))
		{//My Customers
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				//Customer list 
				Intent intent = new Intent(BaseActivity.this, SalesmanCustomerList.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				intent.putExtra("isHistory", false);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryPresellerOption[0]) && !(BaseActivity.this instanceof OrderSummary))
		{//Execution Summary-->Order Summary
			menuClickBCRAction();
			Intent intent = new Intent(BaseActivity.this, OrderSummary.class);
			// don't delete this by sudheer 
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryPresellerOption[1])  && !(BaseActivity.this instanceof PaymentSummaryActivity))
		{//Execution Summary-->Payment Summary
			menuClickBCRAction();
			Intent intent = new Intent(BaseActivity.this, PaymentSummaryActivity.class);
			// don't delete this by sudheer 
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryPresellerOption[2]) && !(BaseActivity.this instanceof SurveyNewActivity))
		{//Execution Summary-->Survey
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, SurveyNewActivity.class);
				// don't delete this by sudheer 
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryPresellerOption[3]) && !(BaseActivity.this instanceof CompetitorsListActivity))
		{//Execution Summary-->Competitive Execution
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, CompetitorsListActivity.class);
				// don't delete this by sudheer 
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryPresellerOption[4]) && !(BaseActivity.this instanceof AssetRequestActivity))
		{//Execution Summary-->Asset Activities
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, AssetsRequestCustomerList.class);
				// don't delete this by sudheer 
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryPresellerOption[5]) && !(BaseActivity.this instanceof OrderTraceActivity))
		{//Execution Summary-->Order Trace
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				Intent intent = new Intent(BaseActivity.this, OrderTraceActivity.class);
//				intent.putExtra("mallsDetails",mallsDetails);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryPresellerOption[6]) && !(BaseActivity.this instanceof OrderTraceActivity))
		{//Log Report
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				Intent intent = new Intent(BaseActivity.this, LogReportActivity.class);
//				intent.putExtra("mallsDetails",mallsDetails);
				startActivity(intent);
			}
		}
		/*else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[4]) && !(BaseActivity.this instanceof CustomerContactDetail))
		{//Add New Customers
			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, CustomerContactDetail.class);
				// don't delete this by sudheer 
				intent.putExtra("mallsDetails",mallsDetails);
				startActivity(intent);
			}
		}*/
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[4]) && !(BaseActivity.this instanceof CashDenominationActivity))
		{//Cash Denomination
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, CashDenominationActivity.class);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[5]) && !(BaseActivity.this instanceof DailySummaryActivity))
		{//Daily Summary
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
//			{
//				showCustomDialog(BaseActivity.this, getResources()
// .getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
//			}
//			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, DailySummaryActivity.class);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[6]) && !(BaseActivity.this instanceof SalesmanSummaryofDay))
		{//EOT
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intents = new Intent(BaseActivity.this,	SalesmanSummaryofDay.class);
				intents.putExtra("dateofJorney", getCurrentdate());
				intents.putExtra("SalesmanCode", preference.getStringFromPreference(Preference.SALESMANCODE, ""));
				intents.putExtra("EmpId", preference.getStringFromPreference(Preference.EMP_NO, ""));
				intents.putExtra("isSupervisor", false);
				intents.putExtra("isEot", true);
				// don't delete this by sudheer 
				intents.putExtra("mallsDetails",mallsDetailss);
				preference.saveStringInPreference("EOTType", "Normal");
				preference.saveStringInPreference("EOTReason", "");
				preference.commitPreference();
				startActivity(intents);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.othersOption[0]) && !(BaseActivity.this instanceof Settings))
		{//Others-->Settings
			menuClickBCRAction();
			Intent intent = new Intent(BaseActivity.this, Settings.class);
			// don't delete this by sudheer 
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.othersOption[1]) && !(BaseActivity.this instanceof CompetitorsListActivity))
		{//Others-->Capture Competitor
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, CompetitorsListActivity.class);
				// don't delete this by sudheer 
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.othersOption[2]) && !(BaseActivity.this instanceof AboutApplicationActivity))
		{//Others-->About Application
			menuClickBCRAction();
			Intent intent = new Intent(BaseActivity.this, AboutApplicationActivity.class);
			// don't delete this by sudheer 
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[8]))
		{
			if (btnLoginLogout != null)
				btnLoginLogout.performClick();
		}
	}

	public void movetoNextPagewhileCheckIN(String strOptionSelected)
	{
		if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[0])&& !(BaseActivity.this instanceof CheckInOptionActivity))
		{//Customer Dashboard
			menuClickBCRAction();
			if(BaseActivity.this instanceof LoadRequestActivity
					|| BaseActivity.this instanceof AddNewLoadRequest
					|| BaseActivity.this instanceof AddStockInVehicle
					|| BaseActivity.this instanceof VerifyItemInVehicle
					|| BaseActivity.this instanceof MovementDetail)
			{
				finish();
			}
			else
			{
				Intent intent = new Intent(BaseActivity.this, CheckInOptionActivity.class);
				intent.putExtra("mallsDetails", mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[1]) && !(BaseActivity.this instanceof SalesmanARCustomerList))
		{//Collection
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, SalesmanARCustomerList.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				intent.putExtra("callfrom", "ArCollection");
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.loadMangeMentOption[0])&& !(BaseActivity.this instanceof AddStockInVehicle))
		{//VanStock
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				preference.saveBooleanInPreference(Preference.IS_VANSTOCK_FROM_MENU_OPTION, true);
				preference.commitPreference();

				Intent intent = new Intent(BaseActivity.this, AddStockInVehicle.class);
				intent.putExtra(Preference.VEHICLE_DO, (VehicleDO)preference.getVehicleObjectFromPreference("VehicleDO"));
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				intent.putExtra("isPreview", true);
				startActivity(intent);
			}
		}
//		else if (strOptionSelected.equalsIgnoreCase(AppConstants.loadMangeMentOption[1]) && !(BaseActivity.this instanceof VanStockTransfer))
//		{//Van Stock Received
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
//			{
//				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
//			}
//			else
//			{
//				menuClickBCRAction();
//				Intent intent = new Intent(BaseActivity.this,VanStockTransfer.class);
//				intent.putExtra("updatededDate",new ArrayList<VanLoadDO>());
//				// dont't delete below line by sudheer
//				intent.putExtra("mallsDetails",mallsDetails);
//				intent.putExtra("isMenu",false);
//				startActivityForResult(intent, 1111);
//			}
//		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.loadMangeMentOption[1]) && !(BaseActivity.this instanceof VerifyItemInVehicle))
		{//Stock Received
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this,VerifyItemInVehicle.class);
				intent.putExtra("updatededDate",new ArrayList<VanLoadDO>());
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				intent.putExtra("isMenu",false);
				startActivityForResult(intent, 1111);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.checkinexecutionSummaryOption[0]) && !(BaseActivity.this instanceof OrderSummary))
		{//Order Summary
			menuClickBCRAction();
			Intent intent = new Intent(BaseActivity.this, OrderSummary.class);
			// dont't delete below line by sudheer
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.checkinexecutionSummaryOption[1])  && !(BaseActivity.this instanceof PaymentSummaryActivity))
		{//Payment Summary
			menuClickBCRAction();
			Intent intent = new Intent(BaseActivity.this, PaymentSummaryActivity.class);
			// dont't delete below line by sudheer
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.checkinexecutionSummaryOption[2]) && !(BaseActivity.this instanceof SurveyNewActivity))
		{//Survey
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, SurveyNewActivity.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.checkinexecutionSummaryOption[3]) && !(BaseActivity.this instanceof CompetitorsListActivity))
		{//Competitor Exceution
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, CompetitorsListActivity.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.checkinexecutionSummaryOption[4]) && !(BaseActivity.this instanceof AssetsRequestCustomerList))
		{//Asset Activities
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, AssetsRequestCustomerList.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[3]) && !(BaseActivity.this instanceof ProductCatalogActivity))
		{//Product Catalog
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, ProductCatalogActivity.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		/*else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[4]) && !(BaseActivity.this instanceof ItemPricingActivity))
		{//Item Pricing
			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, ItemPricingActivity.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetails);
				startActivity(intent);
			}
		}*/
//		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[5]) && !(BaseActivity.this instanceof NewProductLaunch))
//		{//New Launches
//			Intent intent = new Intent(BaseActivity.this, NewProductLaunch.class);
//			// dont't delete below line by sudheer
//			intent.putExtra("mallsDetails",mallsDetails);
//			startActivity(intent);
//		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[4])  && !(BaseActivity.this instanceof CompetitorsListActivity))
		{//Capture Competitor 
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, CompetitorsListActivity.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[5]) && !(BaseActivity.this instanceof Settings))
		{//Others-->Settings
			menuClickBCRAction();
			Intent intent = new Intent(BaseActivity.this, Settings.class);
			// don't delete this by sudheer 
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[6]))
		{//Checkout
			showCustomDialog(this, getString(R.string.warning), "Do you want to check out?", "Yes", "No", "checkout");
//			performCheckouts(true);
		}
		/*else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[8])  && !(BaseActivity.this instanceof CustomerContactDetail))
		{//Add New Customers
			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, CustomerContactDetail.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetails);
				startActivity(intent);
			}
		}*/
	}

	public void movetoNextPagewhileCheckINforPreseller(String strOptionSelected)
	{
		if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[0])&& !(BaseActivity.this instanceof CheckInOptionActivity))
		{//Customer Dashboard
			menuClickBCRAction();

			if(BaseActivity.this instanceof LoadRequestActivity
					|| BaseActivity.this instanceof AddNewLoadRequest
					|| BaseActivity.this instanceof AddStockInVehicle
					|| BaseActivity.this instanceof VerifyItemInVehicle
					|| BaseActivity.this instanceof MovementDetail)
			{
				finish();
			}
			else
			{
				Intent intent = new Intent(BaseActivity.this, CheckInOptionActivity.class);
				intent.putExtra("mallsDetails", mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[1]) && !(BaseActivity.this instanceof SalesmanARCustomerList))
		{//Collection
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, SalesmanARCustomerList.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				intent.putExtra("callfrom", "ArCollection");
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryOption[0]) && !(BaseActivity.this instanceof OrderSummary))
		{//Order Summary
			menuClickBCRAction();
			Intent intent = new Intent(BaseActivity.this, OrderSummary.class);
			// dont't delete below line by sudheer
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryOption[1])  && !(BaseActivity.this instanceof PaymentSummaryActivity))
		{//Payment Summary
			menuClickBCRAction();
			Intent intent = new Intent(BaseActivity.this, PaymentSummaryActivity.class);
			// dont't delete below line by sudheer
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryOption[2]) && !(BaseActivity.this instanceof SurveyNewActivity))
		{//Survey
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, SurveyNewActivity.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryOption[3]) && !(BaseActivity.this instanceof CompetitorsListActivity))
		{//Competitor Exceution
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, CompetitorsListActivity.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryOption[4]) && !(BaseActivity.this instanceof AssetsRequestCustomerList))
		{//Asset Activities
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, AssetsRequestCustomerList.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		/*else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryOption[5]) && !(BaseActivity.this instanceof OrderTraceActivity))
		{//Order Trace
			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				Intent intent = new Intent(BaseActivity.this, OrderTraceActivity.class);
//				intent.putExtra("mallsDetails",mallsDetails);
				startActivity(intent);
			}
		}*/
		else if (strOptionSelected.equalsIgnoreCase(AppConstants.executionSummaryOption[5]) && !(BaseActivity.this instanceof OrderTraceActivity))
		{//Log Report
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				Intent intent = new Intent(BaseActivity.this, LogReportActivity.class);
//				intent.putExtra("mallsDetails",mallsDetails);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[2]) && !(BaseActivity.this instanceof ProductCatalogActivity))
		{//Product Catalog
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, ProductCatalogActivity.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		/*else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[3]) && !(BaseActivity.this instanceof NewProductLaunch))
		{//New Launches
			Intent intent = new Intent(BaseActivity.this, NewProductLaunch.class);
			// dont't delete below line by sudheer
			intent.putExtra("mallsDetails",mallsDetails);
			startActivity(intent);
		}*/
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[3])  && !(BaseActivity.this instanceof CompetitorsListActivity))
		{//Capture Competitor 
//			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			if(isEOTDone())
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, CompetitorsListActivity.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetailss);
				startActivity(intent);
			}
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[4]) && !(BaseActivity.this instanceof Settings))
		{//Others-->Settings
			menuClickBCRAction();
			Intent intent = new Intent(BaseActivity.this, Settings.class);
			// don't delete this by sudheer menuOptionPreseller
			intent.putExtra("mallsDetails",mallsDetailss);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[5]))
		{//Checkout
			showCustomDialog(this, getString(R.string.warning), "Do you want to check out?", "Yes", "No", "checkout");
//			performCheckouts(true);
		}
		/*else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptionsafterCheckin[6])  && !(BaseActivity.this instanceof CustomerContactDetail))
		{//Add New Customers
			if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
			}
			else
			{
				menuClickBCRAction();
				Intent intent = new Intent(BaseActivity.this, CustomerContactDetail.class);
				// dont't delete below line by sudheer
				intent.putExtra("mallsDetails",mallsDetails);
				startActivity(intent);
			}
		}*/
	}

	public void ShowAlertDialog(String Tilte, final String Mesg)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
		builder.setTitle(Tilte);// setting the title of the alert dialog
		builder.setMessage(Mesg);// setting the message of the alert dialog

		// Clicking on OK button, dismiss the dialog.
		builder.setPositiveButton(getResources().getString(R.string.OK)/*
																		 * getResources().getString(R.string.Ok)
																		 */,
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss(); // dismiss the dialog.
					}
				});
		builder.create().show();// this is to s
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	// For showing Dialog message.
	class RunshowCustomDialogs implements Runnable
	{
		private String strTitle;// Title of the dialog
		private String strMessage;// Message to be shown in dialog
		private String firstBtnName;
		private String secondBtnName;
		private String from;
		private String params;
		private boolean isCancelable=false;
		private OnClickListener posClickListener;
		private OnClickListener negClickListener;

		public RunshowCustomDialogs(Context context, String strTitle,String strMessage, String firstBtnName, String secondBtnName,	String from, boolean isCancelable)
		{
			this.strTitle 		= strTitle;
			this.strMessage 	= strMessage;
			this.firstBtnName 	= firstBtnName;
			this.secondBtnName	= secondBtnName;
			this.isCancelable 	= isCancelable;
			if (from != null)
				this.from = from;
			else
				this.from = "";
		}

		public RunshowCustomDialogs(Context context, String strTitle,String strMessage, String firstBtnName, String secondBtnName,	String from, boolean isCancelable,String params)
		{
			this.strTitle 		= strTitle;
			this.strMessage 	= strMessage;
			this.firstBtnName 	= firstBtnName;
			this.secondBtnName	= secondBtnName;
			this.isCancelable 	= isCancelable;
			if (from != null)
				this.from = from;
			else
				this.from = "";

			if (params != null)
				this.params = params;
			else
				this.params = "";
		}

		@Override
		public void run()
		{
			if (customDialog != null && customDialog.isShowing())
				customDialog.dismiss();

			View view;
			if(from.equalsIgnoreCase("Storecheck"))
			{
				view = inflater.inflate(R.layout.popup_storecheck_confirmation, null);
				customDialog = new CustomDialog(BaseActivity.this, view, preference
						.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320)/2,LayoutParams.WRAP_CONTENT, true);
				customDialog.setCancelable(isCancelable);
				new Handler().postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						if(customDialog!=null && customDialog.isShowing())
							customDialog.dismiss();
						onButtonYesClick(from);
					}
				}, 2000);
				try{
					if (!customDialog.isShowing())
						customDialog.show();
				}catch(Exception e){}
			}
			else if(from.equalsIgnoreCase("ReceivePayment"))
			{
				view = inflater.inflate(R.layout.payment_confirmation, null);
				customDialog = new CustomDialog(BaseActivity.this, view, preference
						.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320)/2,LayoutParams.WRAP_CONTENT, true);
				customDialog.setCancelable(isCancelable);
				new Handler().postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						if(customDialog!=null && customDialog.isShowing())
							customDialog.dismiss();
						onButtonYesClick(from);
					}
				}, 2000);
				try{
					if (!customDialog.isShowing())
						customDialog.show();
				}catch(Exception e){}
			}
			else if(from.equalsIgnoreCase("checkout"))
			{
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						final Vector<String> arrReason = new CommonDA().getReasonBasedOnType("Sales Outlet");
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								View view = inflater.inflate(R.layout.custom_common_popup_checkout, null);

								customDialog = new CustomDialog(BaseActivity.this, view, preference
										.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40,
										LayoutParams.WRAP_CONTENT, true);
								customDialog.setCancelable(isCancelable);
								TextView tvTitle = (TextView) view.findViewById(R.id.tvTitlePopup);
								TextView tvMessage = (TextView) view.findViewById(R.id.tvMessagePopup);
								Button btnYes = (Button) view.findViewById(R.id.btnYesPopup);
								Button btnNo = (Button) view.findViewById(R.id.btnNoPopup);
								ListView lvSalesOutlet = (ListView) view.findViewById(R.id.lvSalesOutlet);
								final CheckoutAdapter adapterCheckout = new CheckoutAdapter(BaseActivity.this,arrReason);
								lvSalesOutlet.setAdapter(adapterCheckout);

								tvTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
								tvMessage.setTypeface(AppConstants.Roboto_Condensed_Bold);
								btnYes.setTypeface(AppConstants.Roboto_Condensed_Bold);
								btnNo.setTypeface(AppConstants.Roboto_Condensed_Bold);

								tvTitle.setText("" + strTitle);
								tvMessage.setText("" + strMessage);
								btnYes.setText("" + firstBtnName);

								if (secondBtnName != null && !secondBtnName.equalsIgnoreCase(""))
									btnNo.setText("" + secondBtnName);
								else
									btnNo.setVisibility(View.GONE);

								if(posClickListener == null)
									btnYes.setOnClickListener(new OnClickListener()
									{
										@Override
										public void onClick(View v)
										{
											customDialog.dismiss();

											if(adapterCheckout != null)
											{
												final String selectedOutlet = adapterCheckout.getSelecteSalesOutlet();
												if(selectedOutlet == null || selectedOutlet.equalsIgnoreCase(""))
												{
													showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Please select a Sales Outlet.", getResources().getString(R.string.OK), null, "");
												}
												else
												{
													new Thread(new Runnable()
													{
														@Override
														public void run()
														{
															new CustomerDA().updateSalesOutlet("Sales Outlet", selectedOutlet, mallsDetailss.site);
															uploadData();
															runOnUiThread(new Runnable()
															{
																@Override
																public void run()
																{
																	onButtonYesClick(from);
																}
															});
														}
													}).start();
												}
											}
										}
									});
								else
									btnYes.setOnClickListener(posClickListener);

								if(negClickListener == null)
									btnNo.setOnClickListener(new OnClickListener()
									{
										@Override
										public void onClick(View v)
										{
											customDialog.dismiss();
											onButtonNoClick(from);
										}
									});
								else
									btnNo.setOnClickListener(negClickListener);
								try{
									if (!customDialog.isShowing())
										customDialog.show();
								}catch(Exception e){}
							}
						});
					}
				}).start();
			}

			///----------------------------------------------//
			else if(from.equalsIgnoreCase("returnFinalize"))
			{
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						final Vector<String> arrReason = new Vector<String>();
						arrReason.add("Freezer Breakdown (Melted)");
						arrReason.add("Power Failure (Melted)");
						arrReason.add("Expiry");
						arrReason.add("General");
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								View view = inflater.inflate(R.layout.custom_common_popup_checkout, null);

								customDialog = new CustomDialog(BaseActivity.this, view, preference
										.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40,
										LayoutParams.WRAP_CONTENT, true);
								customDialog.setCancelable(isCancelable);
								TextView tvTitle = (TextView) view.findViewById(R.id.tvTitlePopup);
								TextView tvMessage = (TextView) view.findViewById(R.id.tvMessagePopup);
								Button btnYes = (Button) view.findViewById(R.id.btnYesPopup);
								Button btnNo = (Button) view.findViewById(R.id.btnNoPopup);
								ListView lvSalesOutlet = (ListView) view.findViewById(R.id.lvSalesOutlet);
								final CheckoutAdapter adapterCheckout = new CheckoutAdapter(BaseActivity.this,arrReason);
								lvSalesOutlet.setAdapter(adapterCheckout);

								tvTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
								tvMessage.setTypeface(AppConstants.Roboto_Condensed_Bold);
								btnYes.setTypeface(AppConstants.Roboto_Condensed_Bold);
								btnNo.setTypeface(AppConstants.Roboto_Condensed_Bold);

								tvTitle.setText("" + strTitle);
								tvMessage.setText("" + strMessage);
								btnYes.setText("" + firstBtnName);

								if (secondBtnName != null && !secondBtnName.equalsIgnoreCase(""))
									btnNo.setText("" + secondBtnName);
								else
									btnNo.setVisibility(View.GONE);

								if(posClickListener == null)
									btnYes.setOnClickListener(new OnClickListener()
									{
										@Override
										public void onClick(View v)
										{
											customDialog.dismiss();
											if (adapterCheckout.getSelecteSalesOutlet() != "")
												flagForReturnReason = true;
											else
												flagForReturnReason = false;
										}
									});
								else
									btnYes.setOnClickListener(posClickListener);

								if(negClickListener == null)
									btnNo.setOnClickListener(new OnClickListener()
									{
										@Override
										public void onClick(View v)
										{
											customDialog.dismiss();
											onButtonNoClick(from);
										}
									});
								else
									btnNo.setOnClickListener(negClickListener);
								try{
									if (!customDialog.isShowing())
										customDialog.show();
								}catch(Exception e){}
							}
						});
					}
				}).start();
			}
			else
			{
				view = inflater.inflate(R.layout.custom_common_popup, null);

				customDialog = new CustomDialog(BaseActivity.this, view, preference
						.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40,
						LayoutParams.WRAP_CONTENT, true);
				customDialog.setCancelable(isCancelable);
				TextView tvTitle = (TextView) view.findViewById(R.id.tvTitlePopup);
				TextView tvMessage = (TextView) view
						.findViewById(R.id.tvMessagePopup);
				Button btnYes = (Button) view.findViewById(R.id.btnYesPopup);
				Button btnNo = (Button) view.findViewById(R.id.btnNoPopup);

				tvTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
				tvMessage.setTypeface(AppConstants.Roboto_Condensed_Bold);
				btnYes.setTypeface(AppConstants.Roboto_Condensed_Bold);
				btnNo.setTypeface(AppConstants.Roboto_Condensed_Bold);

				tvTitle.setText("" + strTitle);
				tvMessage.setText("" + strMessage);
				btnYes.setText("" + firstBtnName);

				if (secondBtnName != null && !secondBtnName.equalsIgnoreCase(""))
					btnNo.setText("" + secondBtnName);
				else
					btnNo.setVisibility(View.GONE);

				if(posClickListener == null)
					btnYes.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							customDialog.dismiss();

							if(from!=null && from.equalsIgnoreCase("cancelorder"))
								onButtonYesClick(from,params);
							else
								onButtonYesClick(from);
						}
					});
				else
					btnYes.setOnClickListener(posClickListener);

				if(negClickListener == null)
					btnNo.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							customDialog.dismiss();
							onButtonNoClick(from);
						}
					});
				else
					btnNo.setOnClickListener(negClickListener);
				try{
					if (!customDialog.isShowing())
						customDialog.show();
				}catch(Exception e){}
			}
		}
	}

	public void onButtonNoClick(String from)
	{
	}

	/** Method to Show the alert dialog **/
	public void showCustomDialog(Context context, String strTitle,String strMessage, String firstBtnName, String secondBtnName,String from, OnClickListener posClickListener)
	{
		runOnUiThread(new RunshowCustomDialogs(context, strTitle, strMessage,firstBtnName, secondBtnName, from,true));
	}
	/** Method to Show the alert dialog **/
	public void showCustomDialog(Context context, String strTitle,String strMessage, String firstBtnName, String secondBtnName,String from)
	{
		runOnUiThread(new RunshowCustomDialogs(context, strTitle, strMessage,firstBtnName, secondBtnName, from, true));
	}

	/** Method to Show the alert dialog **/
	public void showCustomDialog(Context context, String strTitle,String strMessage, String firstBtnName, String secondBtnName,String from,String params)
	{
		runOnUiThread(new RunshowCustomDialogs(context, strTitle, strMessage,firstBtnName, secondBtnName, from, true,params));
	}

	/** Method to Show the alert dialog **/
	public void showCustomDialog(Context context, String strTitle,String strMessage, String firstBtnName, String secondBtnName,String from, boolean isCancelable)
	{
		runOnUiThread(new RunshowCustomDialogs(context, strTitle, strMessage,firstBtnName, secondBtnName, from, isCancelable));
	}

	public void startsmsbroadcast()
	{

		try{

			Intent myIntent = new Intent(BaseActivity.this, AlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(BaseActivity.this, 0, myIntent, 0);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

			Calendar calendar = Calendar.getInstance();

			// if it's after or equal 9 am schedule for next day
			if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 10) {
				calendar.add(Calendar.DAY_OF_YEAR, 1); // add, not set!
			}
			calendar.set(Calendar.HOUR_OF_DAY, 10);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);

			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			Log.d("Kwality","Time in mill"+calendar.getTimeInMillis());
			alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis() ,AlarmManager.INTERVAL_DAY, pendingIntent);

		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy()
	{
		final String siteNo = preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");
		Log.e("SiteNo","site = "+siteNo);
		hideLoader();
		super.onDestroy();

		try
		{
			if (customDialog != null && customDialog.isShowing())
				customDialog.dismiss();
			unregisterReceiver(LogoutReceiver);
			unregisterReceiver(ActionHouseList);
			unregisterReceiver(ActionHouseListNew);
			unregisterReceiver(ActionAR);
			unregisterReceiver(ActionCRL);
			unregisterReceiver(ActionHome);
			unregisterReceiver(ActionHome1);
			unregisterReceiver(ActionSQLitefileDownload);
			unregisterReceiver(ActionJourney);
			unregisterReceiver(ActionCustomerList);
			unregisterReceiver(MenuClick);

		}
		catch (IllegalArgumentException e)
		{
		}
		catch (Exception e)
		{
		}
	}

	@Override
	protected void onPause()
	{
		if(!(BaseActivity.this instanceof LoginAcivity))
			hideLoader();
		super.onPause();
	}

	public void hideKeyBoard(View v)
	{
		if(v != null)
		{
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}

	public String getCurrentdate()
	{
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		String strMonth = "", strDate = "";

		if (month < 9)
			strMonth = "0" + (month + 1);
		else
			strMonth = "" + (month + 1);

		if (day < 10)
			strDate = "0" + (day);
		else
			strDate = "" + (day);

		String strSelectedDate = year + "-" + strMonth + "-" + strDate;
		return strSelectedDate;
	}

	int countServiceCapturePending = 0;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 3000)
		{
			finish();
			setResult(3000);
		}
		else if(requestCode == AppConstants.CAMERA_PIC_BEFORE_SERVICE)
		{
			switch (resultCode)
			{
				case RESULT_CANCELED:
					Log.i("Camera", "User cancelled");
					break;

				case RESULT_OK:

					File f = new File(camera_imagepath);
					Bitmap bmp = BitmapUtilsLatLang.decodeSampledBitmapFromResource(f, 480,600);
					Bitmap bitmapProcessed = getBitMap(bmp,CalendarUtils.getCurrentDateTime());

					if (f.exists ()) f.delete ();
					try
					{
						FileOutputStream out = new FileOutputStream(f);
						bitmapProcessed.compress(Bitmap.CompressFormat.PNG, 100, out);
						out.flush();
						out.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}

					mBtBitmap = bitmapProcessed;
//					mBtBitmap = bmp;
					showDetails(mBtBitmap,requestCode);
					break;
			}
		}
		else if(requestCode == AppConstants.CAMERA_PIC_AFTER_SERVICE)
		{
			switch (resultCode)
			{
				case RESULT_CANCELED:
					Log.i("Camera", "User cancelled");
					break;

				case RESULT_OK:

					File f = new File(camera_imagepath);
					Bitmap bmp = BitmapUtilsLatLang.decodeSampledBitmapFromResource(f, 480,600);
					Bitmap bitmapProcessed = getBitMap(bmp,CalendarUtils.getCurrentDateTime());

					if (f.exists ()) f.delete ();
					try
					{
						FileOutputStream out = new FileOutputStream(f);
						bitmapProcessed.compress(Bitmap.CompressFormat.PNG, 100, out);
						out.flush();
						out.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}

					mBtBitmap = bitmapProcessed;
//					mBtBitmap = bmp;
					showDetails(mBtBitmap,requestCode);
					break;
			}
		}
		else if(requestCode == AppConstants.CAMERA_PIC_AFTER_INVOICE_PRINT)
		{
			switch (resultCode)
			{
				case RESULT_CANCELED:
					Log.i("Camera", "User cancelled");
					break;

				case RESULT_OK:

					File f = new File(camera_imagepath);
					orderPrintImageDO.ImagePath = camera_imagepath;
					Bitmap bmp = BitmapUtilsLatLang.decodeSampledBitmapFromResource(f, 480,600);
					Bitmap bitmapProcessed = getBitMap(bmp,CalendarUtils.getCurrentDateTime());

					if (f.exists ()) f.delete ();
					try
					{
						FileOutputStream out = new FileOutputStream(f);
						bitmapProcessed.compress(Bitmap.CompressFormat.PNG, 100, out);
						out.flush();
						out.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}

					mBtBitmap = bitmapProcessed;
//					mBtBitmap = bmp;
					showDetails(mBtBitmap,requestCode);
					break;
			}
		}
	}

	private Bitmap getBitMap(Bitmap bmp,String date)
	{
		Bitmap mBtBitmap = null;
		if(bmp != null)
		{
			mBtBitmap = BitmapUtilsLatLang.processBitmapforServiceCapture(bmp, date,mallsDetailss);
			if(bmp!=null && !bmp.isRecycled())
				bmp.recycle();

//			mBtBitmap = bitmapProcessed;
			return mBtBitmap;
		}
		return mBtBitmap;
	}

	public void onButtonYesClick(String from,String params)
	{

	}

	public void onButtonYesClick(String from)
	{
		if (from.equalsIgnoreCase("logout"))
		{
			// sending broadcast message for logout action
			Intent intentBrObj = new Intent();
			intentBrObj.setAction(AppConstants.ACTION_LOGOUT);
			sendBroadcast(intentBrObj);
			Intent intent = null;
			intent = new Intent(BaseActivity.this, LoginAcivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_right1, R.anim.slide_left1);
		}
		else if(from.equalsIgnoreCase("checkout"))
		{
			countServiceCapturePending = new StoreCheckDA().getServiceCapturePending(preference.getStringFromPreference(preference.USER_ID, ""), mallsDetailss.site);
			if(countServiceCapturePending > 0)
			{
				showCustomDialog(BaseActivity.this, getString(R.string.warning), "After Service image not taken.\nDo you wish to take after service image?", "Yes", "No", "afterservice");
			}
			else
			{
				performCheckouts(true);
			}
		}
		else if(from.contains("checkout_"))
		{
			String strOptionSelected = from.replace("checkout_", "");
			performCheckouts(true,strOptionSelected);
		}
		else if(from.equalsIgnoreCase("afterservice"))
		{
			captureImage(AppConstants.CAMERA_PIC_AFTER_SERVICE);
		}
		else if(from.equalsIgnoreCase("journeyplan"))
		{
			Intent objIntent = new Intent();
			objIntent.setAction(AppConstants.ACTION_GOTO_JOURNEY);
			sendBroadcast(objIntent);

			Intent intent = new Intent(BaseActivity.this, PresellerJourneyPlan.class);
			startActivity(intent);
		}
		else if(from.equalsIgnoreCase("nextdayjourneyplan"))
		{
			Intent objIntent = new Intent();
			objIntent.setAction(AppConstants.ACTION_GOTO_NEXT_DAY_JOURNEY);
			sendBroadcast(objIntent);

			Intent intent = new Intent(BaseActivity.this, PresellerNextDayJourneyPlan.class);
			startActivity(intent);
		}
	}

	public void showReasonOfCheckout(final boolean isCheckout)
	{
		final Vector<NameIDDo> vecReasons = new CommonDA().getReasonsByType(AppConstants.SKIP_JOURNEY_PLAN);
		CustomBuilder builder = new CustomBuilder(this, "Select Reason", true);
		builder.setSingleChoiceItems(vecReasons, -1, new CustomBuilder.OnClickListener()
		{
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject)
			{
				final NameIDDo objNameIDDo = (NameIDDo) selectedObject;
				showLoader(getResources().getString(R.string.please_wait));
				builder.dismiss();
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
//						for(String strSiteId :  AppConstants.skippedCustomerSitIds)
//						{
						Vector<PostReasonDO> vecPostReasons = new Vector<PostReasonDO>();
						PostReasonDO objPostReasonDO 			   = new PostReasonDO();
						objPostReasonDO.customerSiteID = preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");
						objPostReasonDO.presellerId    = preference.getStringFromPreference(Preference.EMP_NO, "");
						objPostReasonDO.reason         = ""+objNameIDDo.strName;
						objPostReasonDO.reasonType     = ""+objNameIDDo.strType;
						objPostReasonDO.reasonId     	= ""+objNameIDDo.strId;
						objPostReasonDO.skippingDate   = CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00";
						vecPostReasons.add(objPostReasonDO);
//						}
						if(vecPostReasons != null && vecPostReasons.size() > 0)
						{
							new CommonDA().insertAllReasons(vecPostReasons);

							uploadData();
						}
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								hideLoader();
								showPassCodeDialog(null, "finish", isCheckout);
							}
						});
					}
				}).start();
			}
		});
		builder.show();
	}

	public void showReasonOfCheckoutforZeroSales(final boolean isCheckout)
	{
		final Vector<NameIDDo> vecReasons = new CommonDA().getReasonsByType(AppConstants.ZERO_SALES);
		CustomBuilder builder = new CustomBuilder(this, "Please Select Zero Sales Reason.", true);
		builder.setSingleChoiceItems(vecReasons, -1, new CustomBuilder.OnClickListener()
		{
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject)
			{
				final NameIDDo objNameIDDo = (NameIDDo) selectedObject;
				showLoader(getResources().getString(R.string.please_wait));
				builder.dismiss();
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						Vector<PostReasonDO> vecPostReasons = new Vector<PostReasonDO>();
						PostReasonDO objPostReasonDO 			   = new PostReasonDO();
						objPostReasonDO.customerSiteID = preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");
						objPostReasonDO.presellerId    = preference.getStringFromPreference(Preference.EMP_NO, "");
						objPostReasonDO.reason         = ""+objNameIDDo.strName;
						objPostReasonDO.reasonType     = ""+objNameIDDo.strType;
						objPostReasonDO.reasonId     	= ""+objNameIDDo.strId;
						objPostReasonDO.skippingDate   = CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00";
						vecPostReasons.add(objPostReasonDO);

						if(vecPostReasons != null && vecPostReasons.size() > 0)
						{
							new CommonDA().insertAllReasons(vecPostReasons);

							updateCheckoutStatus(preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, ""));
							new CustomerDA().updateLastJourneyLog(preference.getStringFromPreference(Preference.VISIT_CODE, ""));
							preference.removeFromPreference("lastservedcustomer");
							preference.commitPreference();

							uploadData();
						}
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								hideLoader();
								performPasscodeAction(null, "finish", true);
//								showPassCodeDialog(null, "finish", isCheckout);
							}
						});
					}
				}).start();
			}
		});
		builder.show();
	}

	public void showPassCodeDialog(final NameIDDo nameIDDo, final String from, final boolean isCheckOut)
	{
		View view 						= 	inflater.inflate(R.layout.eot_popup, null);
		final CustomDialog customDialog = 	new CustomDialog(BaseActivity.this, view, preference.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40, LayoutParams.WRAP_CONTENT, true);
		TextView tvTitle 				= 	(TextView) view.findViewById(R.id.tvTitlePopup);
		final Button btnOkPopup 		= 	(Button) view.findViewById(R.id.btnOkPopup);
		final EditText etEnterValue 	= 	(EditText) view.findViewById(R.id.etEnterValue);
		final EditText etEnterReason	= 	(EditText) view.findViewById(R.id.etEnterReason);
		TextView tvSelectReason			= 	(TextView) view.findViewById(R.id.tvSelectReason);

		etEnterReason.setVisibility(View.GONE);
		tvSelectReason.setVisibility(View.GONE);
		customDialog.setCancelable(true);

		tvTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnOkPopup.setTypeface(AppConstants.Roboto_Condensed_Bold);
		etEnterValue.setTypeface(AppConstants.Roboto_Condensed);
		etEnterReason.setTypeface(AppConstants.Roboto_Condensed);
		tvSelectReason.setVisibility(View.GONE);

		tvTitle.setText("" + "Enter Passcode");
		btnOkPopup.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (etEnterValue.getText().toString().equalsIgnoreCase(""))
					showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Passcode field should not be empty.",getResources().getString(R.string.OK),null, "PassCodeshowAgain");
				else
				{
					showLoader("Please wait...");
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							NameIDDo nameIDDO	=  new CommonDA().validatePassCode(preference.getStringFromPreference(Preference.EMP_NO,""),etEnterValue.getText().toString());
							//Need to remove it.
							if(nameIDDO == null)
							{
								nameIDDO = new NameIDDo();
								nameIDDO.strId = "0";
								nameIDDO.strName = etEnterValue.getText().toString();
							}
							if (nameIDDO != null && nameIDDO.strId!= null && nameIDDO.strId.equalsIgnoreCase("0"))
							{
								new CommonDA().updatePasscodeStatus(nameIDDO.strName);

								runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										customDialog.dismiss();
										performPasscodeAction(nameIDDo, from, isCheckOut);
									}
								});
							}
							else if(nameIDDO != null && nameIDDO.strId!= null && nameIDDO.strId.equalsIgnoreCase("1"))
								showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Entered passcode is already used, please enter a valid passcode.",getResources().getString(R.string.OK),null, "");
							else
								showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Please enter a valid passcode.",getResources().getString(R.string.OK),null, "");

							hideLoader();
						}
					}).start();
				}
			}
		});
		if (!customDialog.isShowing())
			customDialog.show();
	}

	public void showReasonDialog(final TrxDetailsDO trxDetailsDO)
	{
		View view 						= 	inflater.inflate(R.layout.reason_popup, null);
		final CustomDialog customDialog = 	new CustomDialog(BaseActivity.this, view, preference.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40, LayoutParams.WRAP_CONTENT, true);
		TextView tvTitlePopup			= 	(TextView) view.findViewById(R.id.tvTitlePopup);
		final Button btnOkPopup 		= 	(Button) view.findViewById(R.id.btnOkPopup);
		final EditText etEnterReason	= 	(EditText) view.findViewById(R.id.etEnterReason);

		customDialog.setCancelable(true);

		tvTitlePopup.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnOkPopup.setTypeface(AppConstants.Roboto_Condensed_Bold);
		etEnterReason.setTypeface(AppConstants.Roboto_Condensed);

		tvTitlePopup.setText("" + "Enter Reason");
		if(!trxDetailsDO.trxDetailsNote.equalsIgnoreCase(""))
			etEnterReason.setText(trxDetailsDO.trxDetailsNote);

		btnOkPopup.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (etEnterReason.getText().toString().equalsIgnoreCase(""))
					showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Reason field should not be empty.",getResources().getString(R.string.OK),null, "ReasonShowAgain");
				else
				{
					trxDetailsDO.trxDetailsNote = etEnterReason.getText().toString();
					customDialog.dismiss();
				}
			}
		});
		if (!customDialog.isShowing())
			customDialog.show();
	}


	/**
	 * Method to check the Internet availability
	 *
	 * @param context
	 * @return boolean
	 */
	public boolean isNetworkConnectionAvailable(Context context)
	{
		// checking the Internet availability
		boolean isNetworkConnectionAvailable = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context	.getSystemService("connectivity");
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		if (activeNetworkInfo != null)
		{
			isNetworkConnectionAvailable = activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
		}

		return isNetworkConnectionAvailable;
	}

	public boolean isGPSEnable(Context context)
	{
		LocationManager	lm 			= 	(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		try
		{
			boolean gps_enabled		=	lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

			if(!gps_enabled)
				return false;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return true;
	}
	public void onConnectionException(Object msg)
	{
	}
//	public void getPaymentReceipts()
//	{
//		GenerateReceiptParser generateReceiptParser = new GenerateReceiptParser(BaseActivity.this);
//		HashMap<String,String> hashMap 				= new PaymentDetailDA().getReceiptCount();
//		ConnectionHelper connectionHelper 			= new ConnectionHelper(BaseActivity.this);
//		connectionHelper.sendRequest(BuildXMLRequest.getReceiptNumbers(hashMap,preference.getStringFromPreference(Preference.SALESMANCODE, "")),generateReceiptParser , ServiceURLs.GENRATE_RECEIPT);
//	}

	public void scheduleBackgroundtask()
	{
		try
		{
			AlarmManager alarms	 = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
			Intent intent 		 = new Intent(getApplicationContext(),AlaramReceiver.class);
			intent.putExtra(AlaramReceiver.ACTION_ALARM, AlaramReceiver.ACTION_ALARM);
			final PendingIntent pIntent = PendingIntent.getBroadcast(this,1234567, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			alarms.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AppConstants.TIME_FOR_BACKGROUND_TASK, pIntent);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void cancelScheduleTask()
	{
		Intent intent = new Intent(getApplicationContext(), AlaramReceiver.class);
		intent.putExtra(AlaramReceiver.ACTION_ALARM,AlaramReceiver.ACTION_ALARM);
		final PendingIntent pIntent = PendingIntent.getBroadcast(this, 1234567,intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		alarms.cancel(pIntent);
	}

	public void showEOTDialog(Context context, String strTitle)
	{
		runOnUiThread(new RunshowEOTDialog(context, strTitle));
	}

	// For showing EOT popup.
	class RunshowEOTDialog implements Runnable
	{
		private String strTitle;// Title of the dialog
		private Context context;

		public RunshowEOTDialog(Context context, String strTitle)
		{
			this.strTitle = strTitle;
			this.context = context;
		}

		@Override
		public void run()
		{
			View view 						= 	inflater.inflate(R.layout.eot_popup, null);
			final CustomDialog customDialog = 	new CustomDialog(BaseActivity.this, view, preference.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40, LayoutParams.WRAP_CONTENT, true);
			TextView tvTitle 				= 	(TextView) view.findViewById(R.id.tvTitlePopup);
			final Button btnOkPopup 		= 	(Button) view.findViewById(R.id.btnOkPopup);
			final EditText etEnterValue 	= 	(EditText) view.findViewById(R.id.etEnterValue);
			final EditText etEnterReason	= 	(EditText) view.findViewById(R.id.etEnterReason);
			TextView tvSelectReason			= 	(TextView) view.findViewById(R.id.tvSelectReason);

			customDialog.setCancelable(true);

			if(mEnteredPasscode != null && !mEnteredPasscode.equalsIgnoreCase(""))
				etEnterValue.setText(mEnteredPasscode);

			if(mEnteredReason != null && !mEnteredReason.equalsIgnoreCase(""))
				etEnterReason.setText(mEnteredReason);

			tvTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
			btnOkPopup.setTypeface(AppConstants.Roboto_Condensed_Bold);
			etEnterValue.setTypeface(AppConstants.Roboto_Condensed);
			etEnterReason.setTypeface(AppConstants.Roboto_Condensed);
			tvSelectReason.setVisibility(View.GONE);

			tvTitle.setText("" + strTitle);
			btnOkPopup.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (etEnterValue.getText().toString().equalsIgnoreCase(""))
					{
						mEnteredReason = ""+etEnterReason.getText().toString();
						mEnteredPasscode = "";
						showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Code field should not be empty.",getResources().getString(R.string.OK),null, "");
					}
					else if (etEnterReason.getText().toString().equalsIgnoreCase(""))
					{
						mEnteredPasscode = ""+etEnterValue.getText().toString();
						mEnteredReason = "";
						showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Please enter reason.",getResources().getString(R.string.OK),null, "");
					}
					else
					{
						new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								NameIDDo nameIDDO	=  new CommonDA().validatePassCode(preference.getStringFromPreference(Preference.EMP_NO,""),etEnterValue.getText().toString());
								if (nameIDDO != null && nameIDDO.strId!= null && nameIDDO.strId.equalsIgnoreCase("0"))
								{
									new CommonDA().updatePasscodeStatus(nameIDDO.strName);

									runOnUiThread(new Runnable()
									{
										@Override
										public void run()
										{
											customDialog.dismiss();
											Intent intents = new Intent(BaseActivity.this,	SalesmanSummaryofDay.class);
											intents.putExtra("dateofJorney", getCurrentdate());
											intents.putExtra("SalesmanCode", preference.getStringFromPreference(Preference.SALESMANCODE, ""));
											intents.putExtra("EmpId", preference.getStringFromPreference(Preference.EMP_NO, ""));
											intents.putExtra("isSupervisor", false);

											preference.saveStringInPreference("EOTType", "Normal");
											preference.saveStringInPreference("EOTReason", etEnterReason.getText().toString());
											preference.commitPreference();
											startActivity(intents);
										}
									});
								}
								else if(nameIDDO != null && nameIDDO.strId!= null && nameIDDO.strId.equalsIgnoreCase("1"))
									showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Entered passcode is already used, please enter a valid passcode.",getResources().getString(R.string.OK),null, "");
								else
									showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Please enter a valid passcode.",getResources().getString(R.string.OK),null, "");
							}
						}).start();
					}
				}
			});
			if (!customDialog.isShowing())
				customDialog.show();
		}
	}

	public void lockDrawer(final String from)
	{
		LogUtils.errorLog("Menu Drawer Locked in ", from);
		drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}

	public void toggleDrawer()
	{
		if(drawerLayout.isDrawerOpen(flMenu))
			drawerLayout.closeDrawer(flMenu);
		else
			drawerLayout.openDrawer(flMenu);
	}

	public void closeDrawer()
	{
		drawerLayout.closeDrawer(flMenu);
	}

	public void openDrawer()
	{
		if(drawerLayout.isDrawerOpen(Gravity.START))
		{
			drawerLayout.closeDrawer(Gravity.START);
		}
	}
	protected Animation animationBody1, animationBody2;
	private DashBoardOptionsCustomAdapter adapter;
	/**Method for Top Menu click **/
	public void TopBarMenuClick()
	{
		if(drawerLayout.isDrawerOpen(Gravity.START))
			drawerLayout.closeDrawer(Gravity.START);
		else
			drawerLayout.openDrawer(Gravity.START);
	}


	public void uploadData()
	{
 		if(isNetworkConnectionAvailable(BaseActivity.this))
		{
			UploadData uploadDeliveredOrder = new UploadData(BaseActivity.this, null, CalendarUtils.getOrderPostDate());
		}
	}

	OnTouchListener touch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				if(v.getId() != R.id.btnMenu)
				{
					if(llDashBoard.getVisibility() == View.VISIBLE)
					{
						btnMenu.performClick();
//						 openCloseMenu();
					}
				}
			}
			return false;
		}
	};

//	public void enableTouchToChilds(ViewGroup viewGroup)
//	 {
//		 viewGroup.setOnTouchListener(touch);
//		 if(viewGroup.getChildCount() > 0)
//		 {
//			 for(int i=0;i<viewGroup.getChildCount();i++)
//			 {
//				 if(viewGroup.getChildAt(i) instanceof ViewGroup)
//					 enableTouchToChilds((ViewGroup)viewGroup.getChildAt(i));
//				 else
//					 viewGroup.getChildAt(i).setOnTouchListener(touch);
//			 }
//		 }
//	 }

	public void showToast(String message)
	{
		if(toast != null)
			toast.cancel();

		toast = Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}

	public void setTypeFaceRobotoBold(ViewGroup group)
	{
		int count = group.getChildCount();
		View v;
		for(int i = 0; i < count; i++) {
			v = group.getChildAt(i);
			if(v instanceof TextView || v instanceof Button || v instanceof EditText/*etc.*/)
				((TextView)v).setTypeface(AppConstants.Roboto_Condensed_Bold);
			else if(v instanceof ViewGroup)
				setTypeFaceRobotoBold((ViewGroup)v);
		}
	}

	public void setTypeFaceRobotoNormal(ViewGroup group)
	{
		int count = group.getChildCount();
		View v;
		for(int i = 0; i < count; i++) {
			v = group.getChildAt(i);
			if(v instanceof TextView || v instanceof Button || v instanceof EditText/*etc.*/)
				((TextView)v).setTypeface(AppConstants.Roboto_Condensed);
			else if(v instanceof ViewGroup)
				setTypeFaceRobotoNormal((ViewGroup)v);
		}
	}

	public boolean downloadSQLITE(final String downloadUrl,DownloadListner downloadListner) {

		String strFile="";
		strFile = FileUtils.downloadSQLITE(downloadUrl,AppConstants.DATABASE_PATH, BaseActivity.this,"salesman",downloadListner);
//		copyAssets();
//
		if(strFile == null)
			return false;
		else
			return true;
//        return true;
	}
	private void copyAssets() {
		AssetManager assetManager = getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e("tag", "Failed to get asset file list.", e);
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open("salesman.sqlite");
			File outFile = new File(AppConstants.DATABASE_PATH, "salesman.sqlite");
			out = new FileOutputStream(outFile);
			copyFile(in, out);
		} catch(IOException e) {
			Log.e("tag", "Failed to copy asset file: " + "salesman.sqlite", e);
		}
		finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// NOOP
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// NOOP
				}
			}
		}

	}
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}


	public void loadSplashData()
	{
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				if(isNetworkConnectionAvailable(BaseActivity.this))
				{
					GetSplashData getSplashData = new GetSplashData(BaseActivity.this);
					getSplashData.execute("");
				}
			}
		}, 6000);
	}

	public void creditLimitPopup(final JourneyPlanDO mallsDetails, final int invoiceCount,final boolean isFromSalesRecommendedOrder,final TrxHeaderDO trxHeaderDO)
	{
		View view = inflater.inflate(R.layout.cradit_limit_popup, null);
		final CustomDialog mCustomDialog = new CustomDialog(BaseActivity.this, view, preference
				.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40,LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(false);

		ImageView ivCancel = (ImageView) view.findViewById(R.id.ivCancel);
		TextView tv_poptitle = (TextView) view.findViewById(R.id.tv_poptitle);
		Button btn_ModifyOrder = (Button) view.findViewById(R.id.btn_ModifyOrder);
		Button btn_MakePayment = (Button) view.findViewById(R.id.btn_MakePayment);
		Button btn_Continue = (Button) view.findViewById(R.id.btn_Continue);

		if((trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() || trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER())  &&
				mallsDetailss.customerPaymentType!= null &&
				(mallsDetailss.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) || AppConstants.customertype.equalsIgnoreCase("Cash")) &&
				!preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
			btn_Continue.setVisibility(View.GONE);

		tv_poptitle.setTypeface(AppConstants.Roboto_Condensed_Bold);

		if(isFromSalesRecommendedOrder)
			btn_ModifyOrder.setVisibility(View.VISIBLE);
		else
			btn_ModifyOrder.setVisibility(View.INVISIBLE);

		btn_Continue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mCustomDialog.dismiss();
				if(isFromSalesRecommendedOrder)
					onButtonYesClick("continuecreditOrder");
				else
					finish();
			}
		});

		ivCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				mCustomDialog.dismiss();
			}
		});
		btn_ModifyOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCustomDialog.dismiss();
				if(isFromSalesRecommendedOrder)
					onButtonYesClick("modifyOrder");
				else
					finish();
			}
		});

//		if(invoiceCount <= 0)
//			btn_MakePayment.setVisibility(View.GONE);

		btn_MakePayment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCustomDialog.dismiss();

				if(isFromSalesRecommendedOrder)
				{
					onButtonYesClick("creditLimitPopup");
				}
				else
				{
					Intent intent = new Intent(BaseActivity.this, PendingInvoices.class);
					intent.putExtra("mallsDetails", mallsDetails);
					intent.putExtra("isFromPayment", true);
					intent.putExtra("AR", true);
					intent.putExtra("isFromOrderPreview", true);
					intent.putExtra("trxHeaderDO", trxHeaderDO);
					intent.putExtra("isExceed", true);
					startActivityForResult(intent, 5000);

				}
			}
		});

		try{
			if (!mCustomDialog.isShowing())
				mCustomDialog.show();
		}catch(Exception e){}
	}

	public void storeImage(Bitmap imageData,final String filename)
	{
		try {
			imageData = Bitmap.createScaledBitmap(imageData, 250, 80, false);

			BitmapConvertor convertor = new BitmapConvertor(BaseActivity.this, new OnMonochromeCreated()
			{
				@Override
				public void onCompleted(String path)
				{
//					showToast(""+path);
					preference.saveStringInPreference(filename, path);
					preference.commitPreference();
				}
			});
			convertor.convertBitmap(imageData, filename);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	public void performPasscodeAction(NameIDDo nameIDDo, String from, boolean isCheckOut)
	{
		if(isCheckOut)
		{
			Intent intentBrObj = new Intent();
			intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST);
			sendBroadcast(intentBrObj);
		}
		else if(from.equalsIgnoreCase("finish"))
			finish();
	}

	/**
	 * Method to get the refreshed van load data
	 */
	public void loadAllMovements_Sync(String mgs, String empNo)
	{
		showLoader(mgs);
		GetAllMovements getAllMovements = new GetAllMovements(BaseActivity.this, empNo);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetAppActiveStatus);

		String lsd = "1";
		String lst = "1";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}

		new ConnectionHelper(null).sendRequest(BaseActivity.this,BuildXMLRequest.getActiveStatus(empNo, lsd, lst), getAllMovements, ServiceURLs.GetAppActiveStatus);
	}

	/**
	 * Method to get the refreshed van load data
	 */
	public boolean loadVanStock_Sync(String mgs, String empNo)
	{
		showLoader(mgs);
		GetVanLogParsar getVanLogParsar = new GetVanLogParsar(BaseActivity.this, empNo);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetVanStockLogDetail);

		String lsd = "0";
		String lst = "0";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}

		new ConnectionHelper(null).sendRequest(BaseActivity.this,BuildXMLRequest.getVanStockLogDetail(empNo, lsd, lst), getVanLogParsar, ServiceURLs.GetVanStockLogDetail);

		return getVanLogParsar.getStatus();
	}

	public void performCustomerServed()
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				final String siteNo = preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");

				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
//						if(new CommonDA().isOrderPlace(siteNo, CalendarUtils.getOrderPostDate()))
//						{
						uploadData();
						if(BaseActivity.this instanceof CheckInOptionActivity)
						{
							finish();
						}
						Intent intentBrObj = new Intent();
						intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST_NEW);
						sendBroadcast(intentBrObj);
//						}
//						else
//						{
//							showReasonOfCheckout(false);
//						}
					}
				});
			}
		}).start();
	}

	public void loadPromotions(String empId)
	{
		GetPromotionsParser getPromotionsParser = new GetPromotionsParser(BaseActivity.this, empId);

		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetAllPromotions);

		String lsd = 0+"";
		String lst = 0+"";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		new ConnectionHelper(null).sendRequest(BaseActivity.this,BuildXMLRequest.getAllPromotionsWithLastSynch(empId, lsd,lst), getPromotionsParser, ServiceURLs.GetAllPromotions);
	}

	public void loadSplashScreenData(String userCode)
	{
		String lsd = "0", lst = "0";

		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC);
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		new ConnectionHelper(null).sendRequest_Bulk(BaseActivity.this,BuildXMLRequest.getSplashScreenDataforSync(userCode,lsd,  lst), new SplashDataSyncParser(BaseActivity.this), ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC);
	}

	public String getAddress(JourneyPlanDO mallsDetails)
	{
//		String address = "";
//		if(objMallsDetails.addresss1 != null && objMallsDetails.addresss1.length() > 0 )
//			address  = objMallsDetails.addresss1;
//		
//		if(objMallsDetails.addresss2 != null && objMallsDetails.addresss2.length() > 0 )
//		{	
//			if(!address.equalsIgnoreCase(""))
//				address  = address +", "+objMallsDetails.addresss2;
//			else
//				address = objMallsDetails.addresss2;
//		}
//		
//		if(objMallsDetails.addresss3 != null && objMallsDetails.addresss3.length() > 0 )
//		{
//			if(!address.equalsIgnoreCase(""))
//				address  = address +", "+objMallsDetails.addresss3;
//			else
//				address = objMallsDetails.addresss3;
//		}
		String address= "" ;
		if(mallsDetails.addresss1!=null&&!mallsDetails.addresss1.equalsIgnoreCase(""));
		{
			address = mallsDetails.addresss1;
			LogUtils.infoLog("addresss1", mallsDetails.addresss1);
		}
		if(address.equalsIgnoreCase("") && mallsDetails.addresss2!=null&&!mallsDetails.addresss2.equalsIgnoreCase(""))
		{
			address = mallsDetails.addresss2;
			LogUtils.infoLog("addresss2", mallsDetails.addresss2);
		}
		else if(mallsDetails.addresss2!=null&&!mallsDetails.addresss2.equalsIgnoreCase(""))
		{
			address = address + ", " +mallsDetails.addresss2;
			LogUtils.infoLog("addresss2", mallsDetails.addresss2);
		}
		if(address.equalsIgnoreCase("") && mallsDetails.addresss3!=null&&!mallsDetails.addresss3.equalsIgnoreCase(""))
		{
			address = mallsDetails.addresss3;
			LogUtils.infoLog("addresss3", mallsDetails.addresss3);
		}
		else if(mallsDetails.addresss3!=null&&!mallsDetails.addresss3.equalsIgnoreCase(""))
		{
			address = address + "\n" +mallsDetails.addresss3;
			LogUtils.infoLog("addresss3", mallsDetails.addresss3);
		}

//		if(address.equalsIgnoreCase("") && mallsDetails.city!=null&&!mallsDetails.city.equalsIgnoreCase(""))
//		{
//			address = mallsDetails.city;
//			LogUtils.infoLog("city", mallsDetails.city);
//		}
//		else if(mallsDetails.city!=null&&!mallsDetails.city.equalsIgnoreCase(""))
//		{
//			address = address + "\n" +mallsDetails.city;
//			LogUtils.infoLog("city", mallsDetails.city);
//		}

		return address;
	}

	public static String getMimeType(final String filename)
	{
		// There does not seem to be a way to ask the OS or file itself for this
		// information, so unfortunately resorting to extension sniffing.

		int pos = filename.lastIndexOf('.');
		if (pos != -1) {
			String ext = filename.substring(filename.lastIndexOf('.') + 1,
					filename.length());

			if (ext.equalsIgnoreCase("mp3"))
				return "audio/mpeg";
			if (ext.equalsIgnoreCase("aac"))
				return "audio/aac";
			if (ext.equalsIgnoreCase("wav"))
				return "audio/wav";
			if (ext.equalsIgnoreCase("ogg"))
				return "audio/ogg";
			if (ext.equalsIgnoreCase("mid"))
				return "audio/midi";
			if (ext.equalsIgnoreCase("midi"))
				return "audio/midi";
			if (ext.equalsIgnoreCase("wma"))
				return "audio/x-ms-wma";

			if (ext.equalsIgnoreCase("mp4"))
				return "video/mp4";
			if (ext.equalsIgnoreCase("avi"))
				return "video/x-msvideo";
			if (ext.equalsIgnoreCase("wmv"))
				return "video/x-ms-wmv";

			if (ext.equalsIgnoreCase("png"))
				return "image/png";
			if (ext.equalsIgnoreCase("jpg"))
				return "image/jpeg";
			if (ext.equalsIgnoreCase("jpe"))
				return "image/jpeg";
			if (ext.equalsIgnoreCase("jpeg"))
				return "image/jpeg";
			if (ext.equalsIgnoreCase("gif"))
				return "image/gif";

			if (ext.equalsIgnoreCase("xml"))
				return "text/xml";
			if (ext.equalsIgnoreCase("txt"))
				return "text/plain";
			if (ext.equalsIgnoreCase("cfg"))
				return "text/plain";
			if (ext.equalsIgnoreCase("csv"))
				return "text/plain";
			if (ext.equalsIgnoreCase("conf"))
				return "text/plain";
			if (ext.equalsIgnoreCase("rc"))
				return "text/plain";
			if (ext.equalsIgnoreCase("htm"))
				return "text/html";
			if (ext.equalsIgnoreCase("html"))
				return "text/html";

			if (ext.equalsIgnoreCase("pdf"))
				return "application/pdf";
			if (ext.equalsIgnoreCase("apk"))
				return "application/vnd.android.package-archive";

			// Additions and corrections are welcomed.
		}
		return "*/*";
	}

	@Override
	public void onSoftKeyboardClosed() {

	}

	@Override
	public void onSoftKeyboardOpened(int keyboardHeightInPx) {

		Log.e("onSoftKeyboardOpened", "onSoftKeyboardOpened");

		if (customKeyBoardpopup != null && customKeyBoardpopup.isShowing())
			customKeyBoardpopup.dismiss();

	}

	public String getRealPathFromURI(Context context, Uri contentUri)
	{
		Cursor cursor = null;
		try {
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

	public void popupWindowOptions(final SurveyQuestionNewDO questionsDO, final TextView tvText,final Vector<OptionsDO> vecOptions,final DropdownSelectionListner dropdownSelectionListner)
	{

		CustomBuilder builder = new CustomBuilder(BaseActivity.this, "Select Options", true);
		builder.setSingleChoiceItems(vecOptions, null, new CustomBuilder.OnClickListener()
		{
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject)
			{
				final OptionsDO optionsDO = (OptionsDO) selectedObject;
				builder.dismiss();
				builder.dismiss();
				tvText.setText(optionsDO.OptionName);
				questionsDO.Answer = optionsDO.OptionName;
				questionsDO.SurveyQuestionOptionId = optionsDO.SurveyQuestionOptionId;
				dropdownSelectionListner.onOptionSelected(questionsDO);
				tvText.setTag(optionsDO);
			}
		});
		builder.show();

	}

	public void loadIncrementalData(LoginUserInfo loginUserInfo)
	{
		showLoader("Syncing Data...");
		AllDataSyncParser allDataSyncParser = new AllDataSyncParser(BaseActivity.this, loginUserInfo.strEmpNo);

		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetCommonMasterDataSync);

		String lsd = 1+"";
		String lst = 1+"";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;

		}
		else
		{
			lsd = preference.getStringFromPreference(Preference.LSD, lsd);
			lst = preference.getStringFromPreference(Preference.LST, lst);
		}
		new ConnectionHelper(null).sendRequest(BaseActivity.this,BuildXMLRequest.getAllDataSync(loginUserInfo.strEmpNo, StringUtils.getInt(lsd), StringUtils.getInt(lst)), allDataSyncParser, ServiceURLs.GetAllDataSync);
	}

	public int getResId(String image)
	{
		int id = -1;
		try
		{
			id = getResources().getIdentifier(image.toLowerCase(), "drawable", getPackageName());

			if(id <= 0)
				id = R.drawable.brand1;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			id = R.drawable.brand1;
		}

		return id;
	}

//	private void updateKeyboard(int levelUsed)
//	{
//		if(tvDot!=null)
//		{
//			if(levelUsed == 3 || levelUsed == -1)
//				tvDot.setEnabled(false);
//			else
//				tvDot.setEnabled(true);
//		}
//	}

	private void menuClickBCRAction()
	{
		Intent intent = new  Intent();
		intent.setAction(AppConstants.ACTION_MENUCLICK);
		sendBroadcast(intent);
	}
	BroadcastReceiver MenuClick = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if(isCheckin())
			{
				if(!(context instanceof VehicleList) && !(context instanceof VehicleListPreseller) && !(context instanceof SalesmanCheckIn)  && !(context instanceof CheckInOptionActivity)
						&& !(context instanceof PresellerJourneyPlan) && !(context instanceof SalesmanCustomerList))
					finish();
			}
			else if(!(context instanceof VehicleList) && !(context instanceof VehicleListPreseller)/* && !(context instanceof PresellerJourneyPlan) 
					&& !(context instanceof SalesmanCustomerList)*/)
				finish();
		}
	};

	public boolean checkPlayStoreAvailability()
	{
		int resultCode =
				GooglePlayServicesUtil.
						isGooglePlayServicesAvailable(getApplicationContext());
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public void showFullScreenDialog(Dialog objAddNewSKUDialog)
	{
		// for show full screen
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(objAddNewSKUDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;

		objAddNewSKUDialog.show();
		objAddNewSKUDialog.getWindow().setAttributes(lp);
	}

	public String camera_imagepath = "";
	public Bitmap mBtBitmap = null;
	public Dialog dialogDetails;
	public void captureImage(int cameraPicBeforeService)
	{
		if(isDeviceSupportCamera())
		{
			File file    = FileUtils.getOutputImageFile("CaptureService");
			if(file!=null)
			{
				camera_imagepath   = file.getAbsolutePath();
				Uri fileUri  = Uri.fromFile(file);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				intent.putExtra("fileName",file.getName());
				intent.putExtra("filePath", file.getAbsolutePath());
				startActivityForResult(intent, cameraPicBeforeService);
			}
		}
		else
		{
			Toast.makeText(BaseActivity.this,"Sorry Device not supported to camera", Toast.LENGTH_SHORT).show();
		}
	}

	public boolean isDeviceSupportCamera()
	{
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public void showDetails(final Bitmap bitmap, final int requestCode)
	{
		LinearLayout llout = (LinearLayout) inflater.inflate(R.layout.capture_planogram_popup, null);
		Button btnSubmit = (Button)llout.findViewById(R.id.btnSubmit);
		TextView tvAddComments = (TextView)llout.findViewById(R.id.tvAddComments);
		final EditText etNotes = (EditText)llout.findViewById(R.id.edtNotes);
		ImageView ivCapturedPlanImage = (ImageView)llout.findViewById(R.id.ivCapturedPlanImage);

		tvAddComments.setVisibility(View.GONE);
		etNotes.setVisibility(View.GONE);

		tvAddComments.setText("Add File Name");
		etNotes.setHint("Enter File Name");
		etNotes.setText("");
		btnSubmit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showLoader("Please wait..");
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						final StatusDO statusDO = new StatusDO();
						statusDO.UUid ="";
						statusDO.Userid = preference.getStringFromPreference(Preference.SALESMANCODE, "");
						statusDO.Customersite =mallsDetailss.site;
						statusDO.Date = CalendarUtils.getCurrentDateAsStringforStoreCheck();
						statusDO.Visitcode=mallsDetailss.VisitCode;
						statusDO.JourneyCode = mallsDetailss.JourneyCode;
						statusDO.Status = "0";
						statusDO.Action = AppConstants.Action_CheckIn;
						if(requestCode == AppConstants.CAMERA_PIC_BEFORE_SERVICE)
							statusDO.Type = AppConstants.Type_BeforService;
						else if(requestCode == AppConstants.CAMERA_PIC_AFTER_SERVICE)
							statusDO.Type = AppConstants.Type_AfterService;
						else if(requestCode == AppConstants.CAMERA_PIC_AFTER_INVOICE_PRINT)
							statusDO.Type = AppConstants.Type_AfterPrintInvoice;
						new StatusDA().insertOptionStatus(statusDO);

						new StoreCheckDA().insertServiceImage(preference.getStringFromPreference(preference.USER_ID, ""), mallsDetailss.site, camera_imagepath, requestCode);


//						if( requestCode != AppConstants.CAMERA_PIC_AFTER_INVOICE_PRINT)
//						{
//							new StoreCheckDA().insertServiceImage(preference.getStringFromPreference(preference.USER_ID, ""), mallsDetailss.site, camera_imagepath, requestCode);
//						}
//						else if( requestCode == AppConstants.CAMERA_PIC_AFTER_INVOICE_PRINT)
//						{
//							if(!TextUtils.isEmpty(orderPrintImageDO.TrxCode))
//								new ReturnOrderDA().insertAllOrderPrintImages(orderPrintImageDO);
//						}

						uploadData();

						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								hideLoader();

								if( ! statusDO.Type .equalsIgnoreCase( AppConstants.Type_AfterPrintInvoice)) {
									if (dialogDetails != null)
										dialogDetails.dismiss();

									if (countServiceCapturePending > 0)
										performCheckouts(true);

									LogUtils.debug("refreshListCheckInOption", AppConstants.ACTION_REFRESH_CHECKIN_OPTION);
									Intent intent = new Intent();
									intent.setAction(AppConstants.ACTION_REFRESH_CHECKIN_OPTION);
									sendBroadcast(intent);
								}
								else
								{
									if (dialogDetails != null)
										dialogDetails.dismiss();
								}
							}
						});
					}
				}).start();
			}
		});


		if(bitmap!=null)
			ivCapturedPlanImage.setImageBitmap(bitmap);

		dialogDetails = new Dialog(BaseActivity.this, android.R.style.Theme_Holo_Dialog);
		dialogDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogDetails.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		Display display = ((WindowManager)BaseActivity.this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		AppConstants.DEVICE_WIDTH 	= display.getWidth();
		AppConstants.DEVICE_HEIGHT 	= display.getHeight();

		dialogDetails.addContentView(llout, new LayoutParams(AppConstants.DEVICE_WIDTH-5, AppConstants.DEVICE_HEIGHT-5));
		dialogDetails.show();

		setTypeFaceRobotoNormal(llout);
	}

	public String isPerfectStoreBase = "";
//	private void insertCustomerVisit()
//	{
//		TrxLogDetailsDO trxLogDetailsDO = new TrxLogDetailsDO();
//		trxLogDetailsDO.CustomerCode = mallsDetailss.site;
//		trxLogDetailsDO.CustomerName = mallsDetailss.siteName;
//		trxLogDetailsDO.columnName  = TrxLogHeaders.COL_TOTAL_ACTUAL_CALLS;
//		trxLogDetailsDO.Date = CalendarUtils.getOrderPostDate();
//		trxLogDetailsDO.TimeStamp = CalendarUtils.getCurrentDateAsStringForJourneyPlan();
//		trxLogDetailsDO.IsJp  = new CustomerDA().isCustomerIsInJourneyPlan(mallsDetailss.site,trxLogDetailsDO.Date)?"True":"False";
//		new TransactionsLogsDA().updateLogReport(trxLogDetailsDO);
//
//		CustomerVisitDO visitDO 		= new CustomerVisitDO();
//		visitDO.CustomerVisitId			= StringUtils.getUniqueUUID();
//		visitDO.UserCode				= preference.getStringFromPreference(Preference.SALESMANCODE, "");
//		visitDO.JourneyCode				= mallsDetailss.JourneyCode;
//		visitDO.VisitCode				= mallsDetailss.VisitCode;
//		visitDO.ClientCode				= mallsDetailss.site;
//		visitDO.Date					= CalendarUtils.getCurrentDateTime();/** Sample : 2014-12-19T12:07:29 */
//		visitDO.ArrivalTime				= CalendarUtils.getCurrentDateTime();
//		visitDO.OutTime					= "";
//		visitDO.TotalTimeInMins			= "";
//		visitDO.Latitude				= preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 25.4544450);
//		visitDO.Longitude				= preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 65.23232323);
//		visitDO.CustomerVisitAppId		= visitDO.CustomerVisitId;
//		visitDO.IsProductiveCall		= "";
//		visitDO.TypeOfCall				= isPerfectStoreBase;
//		visitDO.Status					= "";
//
//		visitDO.vehicleNo				= ""+preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
//		visitDO.UserId					= ""+preference.getStringFromPreference(Preference.EMP_NO, "");
//
//		new CustomerDA().insertCustomerVisits(visitDO);//tblJourney
//	}

	public void syncData(SyncProcessListner syncProcessListner){
		SyncData.setListner(syncProcessListner);
		Intent uploadTraIntent=new Intent(this,SyncData.class);
		startService(uploadTraIntent);
	}

	public void syncPromotions()
	{
		try {
			syncDeletedLogs();
			ConnectionHelper connectionHelper= new ConnectionHelper(BaseActivity.this);
			GetPromotionsParser allDataSyncParser = new GetPromotionsParser(BaseActivity.this, preference.getStringFromPreference(Preference.EMP_NO, ""));
			SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetAllPromotions);
			String lsd = 0+"";
			String lst = 0+"";
			if(synLogDO != null)
			{
				lsd = synLogDO.UPMJ;
				lst = synLogDO.UPMT;
			}
			else
			{
				lsd = preference.getStringFromPreference(Preference.LSD, lsd);
				lst = preference.getStringFromPreference(Preference.LST, lst);
			}
			connectionHelper.sendRequest(BaseActivity.this,BuildXMLRequest.getAllPromotions(preference.getStringFromPreference(Preference.EMP_NO, ""), lsd, lst), allDataSyncParser, ServiceURLs.GetAllPromotions);
		} catch (Exception e) {
		}

	}

	public void syncDeletedLogs()
	{
		try {
			ConnectionHelper connectionHelper= new ConnectionHelper(BaseActivity.this);
			GetAllDeleteLogsParser allDeleteLogsParser = new GetAllDeleteLogsParser(BaseActivity.this);
			SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetAllDeleteLogs);
			String lsd = 0+"";
			String lst = 0+"";
			if(synLogDO != null)
			{
				lsd = synLogDO.UPMJ;
				lst = synLogDO.UPMT;
			}
			else
			{
				lsd = preference.getStringFromPreference(Preference.LSD, lsd);
				lst = preference.getStringFromPreference(Preference.LST, lst);
			}
			connectionHelper.sendRequest(BaseActivity.this,BuildXMLRequest.getAllDeleteLogs(lsd, lst), allDeleteLogsParser, ServiceURLs.GetAllDeleteLogs);
		} catch (Exception e) {
		}

	}

	public void loadIncrementalData(String empNo)
	{
		LogUtils.debug("loadIncrementalData", "started");
		ConnectionHelper connectionHelper = new ConnectionHelper(BaseActivity.this);;
		showLoader("Syncing Data...");
		AllDataSyncParser allDataSyncParser = new AllDataSyncParser(BaseActivity.this, empNo,null);

		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetCommonMasterDataSync);//please don't change this entity

		String lsd = 0+"";
		String lst = 0+"";
		if(synLogDO != null)
		{
			lsd =synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		else
		{
			lsd = preference.getStringFromPreference(Preference.LSD, lsd);
			lst = preference.getStringFromPreference(Preference.LST, lst);
		}
		connectionHelper.sendRequest(BaseActivity.this,BuildXMLRequest.getAllDataSync(preference.getStringFromPreference(Preference.EMP_NO, ""), StringUtils.getInt(lsd), StringUtils.getInt(lst)), allDataSyncParser, ServiceURLs.GetAllDataSync);
		connectionHelper.sendRequestForSurveyModule(BaseActivity.this, BuildXMLRequest.getSurveyListByUserIdBySync(preference.getStringFromPreference(Preference.EMP_NO, ""), StringUtils.getInt(lsd), StringUtils.getInt(lst)), ServiceURLs.GET_SURVEY_LIST_BY_USER_ID_BY_SYNC);
	}

	public boolean isEOTDone()
	{
//		return false;
		return new EOTDA().isEOTDone(preference.getStringFromPreference(preference.USER_ID, ""));
	}

	private VersionCheckingHandler versionCheckingHandler;
	public void callCheckVersionWebService(VersionChangeListner changeListner, int methodOrigin)
	{
		if(isNetworkConnectionAvailable(BaseActivity.this))
		{
			LogUtils.debug("VERSION_CONTROL", "network_avail");
			//Call version Managment service
			versionCheckingHandler = new VersionCheckingHandler(BaseActivity.this);
			ConnectionHelper coh = new ConnectionHelper(null);
//			coh.sendRequest(BuildXMLRequest.getVersionDetails("Android",""+getVersionCode()), versionCheckingHandler, ServiceURLs.GetVersionDetails, "", preference);

			LogUtils.debug("VERSION_CONTROL", BuildXMLRequest.checkAppUpgrade(preference.getStringFromPreference(Preference.EMP_NO, ""),"1",""+getVersionCode()));
			LogUtils.debug("VERSION_CONTROL", ""+getVersionCode());
			LogUtils.debug("VERSION_CONTROL", ServiceURLs.checkAppUpgrade);


			coh.sendRequest(BuildXMLRequest.checkAppUpgrade(preference.getStringFromPreference(Preference.EMP_NO, ""),"1",""+getVersionCode()), versionCheckingHandler, ServiceURLs.checkAppUpgrade, "", preference);
			if(versionCheckingHandler.getData()!=null)
			{
				LogUtils.debug("VERSION_CONTROL", "handler not null");
				if(versionCheckingHandler.getData() instanceof CheckVersionDO)
				{
					LogUtils.debug("VERSION_CONTROL", "do version managment");
					doVersionManagement((CheckVersionDO)versionCheckingHandler.getData(), changeListner, methodOrigin);
				}
				else
				{
					LogUtils.debug("VERSION_CONTROL", "not instance");
					changeListner.onVersionChanged(AppConstants.VER_NOT_CHANGED);
				}
			}
			else
			{
				LogUtils.debug("VERSION_CONTROL", "handler null");
				changeListner.onVersionChanged(AppConstants.VER_NOT_CHANGED);
			}
		}
		else
		{
			LogUtils.debug("VERSION_CONTROL", "network not avail");
			changeListner.onVersionChanged(AppConstants.VER_NOT_CHANGED);
		}
	}

	/**
	 * Do version Management
	 * */
	public void doVersionManagement(CheckVersionDO checkVersion, VersionChangeListner changeListner, int methodOrigin)
	{
		String sampleUrl=checkVersion.APKFileName;

		if(sampleUrl.contains("localhost"))
			sampleUrl = sampleUrl.replace("localhost", "10.20.53.18");

		if(checkVersion.StatusCode==AppConstants.MINOR_APP_UPDATE || checkVersion.StatusCode==AppConstants.NORMAL_APP_UPDATE)
		{
			LogUtils.debug("VERSION_CONTROL", "minor update");
			if (methodOrigin == AppConstants.CALL_FROM_SETTINGS && checkNormalOrders_())
				changeListner.onVersionChanged(AppConstants.VER_DO_EOT);
			else if (methodOrigin == AppConstants.CALL_FROM_LOGIN && checkNormalOrders_())
				changeListner.onVersionChanged(AppConstants.VER_DO_EOT);
			else
			{
				showDialogueForUpGrade("We have a Update release for you. It's recommended that you update the app to continue using.", checkVersion.StatusCode,sampleUrl, changeListner);
				changeListner.onVersionChanged(AppConstants.VER_CHANGED);
			}
		}
		//Show Alert to user for forcefully update the application. 
		else if(checkVersion.StatusCode==AppConstants.MAJOR_APP_UPDATE)
		{
			LogUtils.debug("VERSION_CONTROL", "major update");
			if (methodOrigin == AppConstants.CALL_FROM_SETTINGS && checkNormalOrders_())
				changeListner.onVersionChanged(AppConstants.VER_DO_EOT);
			else if (methodOrigin == AppConstants.CALL_FROM_LOGIN && checkNormalOrders_())
				changeListner.onVersionChanged(AppConstants.VER_DO_EOT);
			else
			{
				showDialogueForUpGrade("We have a Major Update release for you. It's Mandatory that you update the app to continue using.", checkVersion.StatusCode,sampleUrl, changeListner);
				changeListner.onVersionChanged(AppConstants.VER_CHANGED);
			}
		}
		else
		{
			changeListner.onVersionChanged(AppConstants.VER_NOT_CHANGED);
		}
	}

	private boolean checkNormalOrders_()
	{
		boolean isDatatoUpload = false;

		if (preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
			isDatatoUpload = new EOTDA().getEOTDetailsForPreSales();
		else
			isDatatoUpload = new EOTDA().getEOTDetailsForVansales();

		if(isDatatoUpload)
		{
			if(isEOTDone())
				return true;
			return true;
		}
		else if(!isEOTDone())
			return true;

		return false;
	}

	private void showDialogueForUpGrade(final String strMessage,final int ResultCode, final String aPKFileName, final VersionChangeListner changeListner)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					hideLoader();

					if (customDialog != null && customDialog.isShowing())
						customDialog.dismiss();

					if(upgradeDialog != null && !upgradeDialog.isShowing())
						upgradeDialog.dismiss();

					View view = getLayoutInflater().inflate(R.layout.custom_common_popup, null);
					int devic_width 	= preference.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 480);
					if(BaseActivity.px > 1)
						upgradeDialog = new CustomDialog(BaseActivity.this, view, devic_width, LayoutParams.WRAP_CONTENT);
					else
						upgradeDialog = new CustomDialog(BaseActivity.this, view, devic_width, LayoutParams.WRAP_CONTENT);

					if(upgradeDialog!=null)
						upgradeDialog.setOnKeyListener(dialogKeyListner);

					TextView tvTitleCustomPopup  = (TextView) view.findViewById(R.id.tvTitlePopup);
					TextView tvMsgCustomPopup    = (TextView) view.findViewById(R.id.tvMessagePopup);
					final Button btnPositivePopup = (Button) view.findViewById(R.id.btnYesPopup);
					final Button btnNegetivePopup = (Button) view.findViewById(R.id.btnNoPopup);

					tvTitleCustomPopup.setVisibility(View.VISIBLE);
					tvTitleCustomPopup.setText(getResources().getString(R.string.alert));
					tvMsgCustomPopup.setText(strMessage);
					btnNegetivePopup.setVisibility(View.VISIBLE);
					android.view.ViewGroup.LayoutParams layoutParams = btnNegetivePopup.getLayoutParams();

					if(ResultCode==AppConstants.MAJOR_APP_UPDATE)
					{
						btnPositivePopup.setVisibility(View.GONE);
						((MarginLayoutParams) layoutParams).setMargins(0, 0, 0, 0);
						btnNegetivePopup.setLayoutParams(layoutParams);
					}
					else
						btnPositivePopup.setVisibility(View.VISIBLE);

					btnNegetivePopup.setText(getResources().getString(R.string.upgrade_now));
					btnNegetivePopup.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							if(upgradeDialog!=null)
								upgradeDialog.dismiss();
							try
							{
								goToPlayStoreOrDownloadAPKFile(false,aPKFileName, changeListner, ResultCode);
							}
							catch (Exception e)
							{
								changeListner.onVersionChanged(AppConstants.VER_NO_BUTTON_CLICK);
//								moveToNext();
							}
						}
					});

					btnPositivePopup.setText(getResources().getString(R.string.latter));
					btnPositivePopup.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							if(upgradeDialog!=null)
								upgradeDialog.dismiss();
							changeListner.onVersionChanged(AppConstants.VER_NO_BUTTON_CLICK);
//							moveToNext();
						}
					});

					upgradeDialog.setCancelable(false);

					if(upgradeDialog != null  && !upgradeDialog.isShowing())
						upgradeDialog.show();// this is to show the dialog.

					setTypeFace((ViewGroup)view);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	DialogInterface.OnKeyListener dialogKeyListner = new DialogInterface.OnKeyListener()
	{

		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
		{
			if (keyCode == KeyEvent.KEYCODE_BACK)
			{
				LogUtils.errorLog("Keywork Back", "Called");
				if(dialog!=null)
					dialog.dismiss();
//	            finish();
				return true;
			}
			return false;
		}
	};

	private String apkFilePath="";
	private void goToPlayStoreOrDownloadAPKFile(boolean isPlayStore,final String apkFileUrl, final VersionChangeListner changeListner, final int requestCode)
	{
		showLoader("Downloading .apk file...");

		new Thread(new Runnable()
		{
			public void run()
			{
				apkFilePath = FileUtils.downloadAPKFile(apkFileUrl,BaseActivity.this);
//				apkFilePath = FileUtils.downloadAPKFile("http://kwalitypic.dyndns.org:81/KwalitySFA/Data/APKFolder/Kwality_VS3.4_19.apk",BaseActivity.this);

				LogUtils.errorLog("FilePath", apkFilePath);
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						hideLoader();

						if(apkFilePath!=null)
						{
//							clearSynchTime(preference.getStringFromPreference(Preference.TEMP_EMP_NO,""));
							preference.saveStringInPreference(Preference.TEMP_EMP_NO, "");
							preference.commitPreference();

							if(requestCode == AppConstants.MAJOR_APP_UPDATE)
							{
								preference.saveBooleanInPreference(Preference.IS_SQLITE_DOWNLOADED, false);
								preference.commitPreference();
							}
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(Uri.fromFile(new File(apkFilePath)), "application/vnd.android.package-archive");
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
							finish();
						}
						else
						{
							changeListner.onVersionChanged(AppConstants.VER_UNABLE_TO_UPGRADE);
//							moveToNext();
						}
					}
				});
			}
		}).start();
	}

	public void setTypeFace(ViewGroup group)
	{
		int count = group.getChildCount();
		View v;
		for(int i = 0; i < count; i++) {
			v = group.getChildAt(i);
			if(v instanceof TextView || v instanceof Button /*etc.*/)
				((TextView)v).setTypeface(AppConstants.Roboto_Condensed_Bold);
			else if(v instanceof ViewGroup)
				setTypeFace((ViewGroup)v);
		}
	}

	//For VersionManagemnet
//	public int getVersionCode()
//	{
//		int versionNumber=1;
//		try 
//		{
//			String number = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
//			versionNumber= Integer.parseInt(number.substring(number.lastIndexOf(".")+1, number.length()));
//			LogUtils.errorLog("versionCode", ""+versionNumber);
//		} 
//		catch (NameNotFoundException e) 
//		{
//			e.printStackTrace();
//		}
//		return versionNumber;
//	}

	public String getVersionCode()
	{
		String number = "1";
		try
		{
			int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			number = versionCode+"";
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return number;
	}

	public String getVersionName()
	{
		String number = "";
		try
		{
			number = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			LogUtils.errorLog("versionName", ""+number);
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return number;
	}

	public void clearSynchTime(String oldEmpNo)
	{
		new SynLogDA().clearSynchLog();

		preference.removeFromPreference(ServiceURLs.GET_DISCOUNTS+oldEmpNo+Preference.LAST_SYNC_TIME);

		preference.removeFromPreference(ServiceURLs.GET_CUSTOMER_GROUP+oldEmpNo+Preference.LAST_SYNC_TIME);

		preference.removeFromPreference(ServiceURLs.GET_CUSTOMER_SITE+oldEmpNo+Preference.LAST_SYNC_TIME);

		preference.removeFromPreference(ServiceURLs.GET_PENDING_SALES_INVOICE+oldEmpNo+Preference.LAST_SYNC_TIME);

		preference.removeFromPreference(ServiceURLs.GET_CUSTOMER_HISTORY_WITH_SYNC+oldEmpNo+Preference.LAST_SYNC_TIME);

		preference.removeFromPreference(ServiceURLs.GetHouseHoldMastersWithSync+Preference.LAST_SYNC_TIME);

		preference.removeFromPreference(Preference.OFFLINE_DATE);

		preference.removeFromPreference(ServiceURLs.GET_ALL_VEHICLES+oldEmpNo+Preference.LAST_SYNC_TIME);

		preference.removeFromPreference(ServiceURLs.GET_All_PRICE_WITH_SYNC+Preference.LAST_SYNC_TIME);

		preference.removeFromPreference(ServiceURLs.GET_HH_DELETED_CUSTOMERS+Preference.LAST_SYNC_TIME);

		preference.removeFromPreference(ServiceURLs.GET_JOURNEY_PLAN+oldEmpNo+Preference.LAST_SYNC_TIME);

		preference.removeFromPreference(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC+Preference.LAST_SYNC_TIME);

		preference.removeFromPreference(ServiceURLs.GetHouseHoldMastersWithSync+Preference.LAST_SYNC_TIME);

		preference.removeFromPreference(Preference.GetAllPromotions);

		preference.saveBooleanInPreference(Preference.IS_EOT_DONE, false);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{//to be uncommented
				new EOTDA().insertEOT(preference.getStringFromPreference(preference.USER_ID, ""), preference.getStringFromPreference(preference.USER_NAME, ""),"False");
			}
		}).start();

		preference.saveBooleanInPreference(Preference.IsStockVerified, false);

		preference.saveIntInPreference(Preference.STARTDAY_VALUE, 0);

		preference.saveStringInPreference(Preference.STARTDAY_TIME, "");

		preference.commitPreference();
	}

	public void SetDisableButton(final Button btn)
	{
		btn.setClickable(false);
		btn.setEnabled(false);

		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				btn.setClickable(true);
				btn.setEnabled(true);
			}
		}, 1000);
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState)
	{
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("DBPATH", AppConstants.DATABASE_PATH);
		savedInstanceState.putInt("DIVICE_WIDTH", AppConstants.DIVICE_WIDTH);
		savedInstanceState.putInt("DIVICE_HEIGHT", AppConstants.DIVICE_HEIGHT);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		AppConstants.DATABASE_PATH 		= 	savedInstanceState.getString("DBPATH");
		AppConstants.DIVICE_WIDTH 		= 	savedInstanceState.getInt("DIVICE_WIDTH");
		AppConstants.DIVICE_HEIGHT 		= 	savedInstanceState.getInt("DIVICE_HEIGHT");

		if(AppConstants.Roboto_Condensed == null)
			AppConstants.Roboto_Condensed = Typeface.createFromAsset(getApplicationContext().getAssets(), "Roboto_Condensed.ttf");
		if(AppConstants.Roboto_Condensed_Bold == null)
			AppConstants.Roboto_Condensed_Bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "Roboto_Condensed_bold.ttf");
	}
	public  void writeLogForEOT(String str)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/EOTLog.txt", true);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(str.getBytes());

			bos.flush();
			bos.close();
			fos.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
