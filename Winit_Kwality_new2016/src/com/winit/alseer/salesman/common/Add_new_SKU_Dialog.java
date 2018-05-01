package com.winit.alseer.salesman.common;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;


public class Add_new_SKU_Dialog extends Dialog
{
	public TextView tvItemCodeLabel,tvItem_DescriptionLabel,tvNoItemFound,tvAdd_New_SKU_Item,
	tvCategory,tvMainCategory,tvSubCategory,tvInventoryQty;
	public EditText etSearch;
	public ImageView cbList;
	public ListView lvPopupList;
	public ExpandableListView lvExPopupList;
	public LinearLayout llList,llResult,llBottomButtons,llFilterByCategory,llSearch,llButtonYellow,llButtonGreen;
	public Button btnAdd,btnCancel;
	public View ivViewSep;
	
	public ImageView ivSearchCross;

	public Add_new_SKU_Dialog(Context context) 
	{
		super(context,R.style.Dialog);
		LayoutInflater inflater =getLayoutInflater();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LinearLayout llEntirechrt = (LinearLayout) inflater.inflate(R.layout.add_new_sku_popup, null);
		this.setCancelable(true);
		setContentView(llEntirechrt, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
//		setContentView(llEntirechrt, new LayoutParams(new Preference(context).getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH,320)-30,new Preference(context).getIntFromPreference(Preference.DEVICE_DISPLAY_HEIGHT,320)*3/4-102));

		((BaseActivity)context).setTypeFaceRobotoNormal(llEntirechrt);

		tvItemCodeLabel			=	(TextView)llEntirechrt.findViewById(R.id.tvItemCodeLabel);
		llFilterByCategory		=	(LinearLayout)llEntirechrt.findViewById(R.id.llFilterByCategory);
		llSearch				=	(LinearLayout)llEntirechrt.findViewById(R.id.llSearch);
		tvItem_DescriptionLabel	=	(TextView)llEntirechrt.findViewById(R.id.tvItem_DescriptionLabel);
		tvInventoryQty			=	(TextView)llEntirechrt.findViewById(R.id.tvInventoryQty);
		tvSubCategory			=	(TextView)llEntirechrt.findViewById(R.id.tvSubCategory);
		ivViewSep				=	(View)llEntirechrt.findViewById(R.id.ivViewSep);
		tvMainCategory			=	(TextView)llEntirechrt.findViewById(R.id.tvMainCategory);
		tvAdd_New_SKU_Item		=	(TextView)llEntirechrt.findViewById(R.id.tvAdd_New_SKU_Item);
		tvCategory	 			=	(TextView)llEntirechrt.findViewById(R.id.tvCategory);
		etSearch	 			=	(EditText)llEntirechrt.findViewById(R.id.etSearch);
		cbList 					=	(ImageView)llEntirechrt.findViewById(R.id.cbList);
		lvPopupList		 		=	(ListView)llEntirechrt.findViewById(R.id.lvPopupList);
		lvExPopupList			=   (ExpandableListView)llEntirechrt.findViewById(R.id.lvExPopupList);
		llList					=	(LinearLayout)llEntirechrt.findViewById(R.id.llList);
		btnAdd 					=	(Button)llEntirechrt.findViewById(R.id.btnOne);
		btnCancel 				=	(Button)llEntirechrt.findViewById(R.id.btnTwo);
		tvNoItemFound			=	(TextView)llEntirechrt.findViewById(R.id.tvNoItemFound);
		llResult 				=	(LinearLayout)llEntirechrt.findViewById(R.id.llResult);
		llBottomButtons 		=	(LinearLayout)llEntirechrt.findViewById(R.id.llBottomButtons);
		llButtonGreen			=	(LinearLayout)llEntirechrt.findViewById(R.id.llButtonGreen);
		llButtonYellow			=	(LinearLayout)llEntirechrt.findViewById(R.id.llButtonYellow);
		
		ivSearchCross			=	(ImageView)llEntirechrt.findViewById(R.id.ivSearchCross);

		btnAdd.setText(context.getResources().getString(R.string.Add));
		btnCancel.setText(context.getResources().getString(R.string.cancel));

		tvNoItemFound.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvItemCodeLabel.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvItem_DescriptionLabel.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnAdd.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnCancel.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvAdd_New_SKU_Item.setTypeface(AppConstants.Roboto_Condensed_Bold);
		etSearch.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCategory.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvAdd_New_SKU_Item.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}

}