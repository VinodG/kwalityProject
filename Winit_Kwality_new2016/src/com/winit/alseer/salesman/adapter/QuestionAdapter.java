package com.winit.alseer.salesman.adapter;

import httpimage.HttpImageManager;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.ImageLoader;
import com.winit.alseer.salesman.common.OptionsNames;
import com.winit.alseer.salesman.dataobject.OptionsDO;
import com.winit.alseer.salesman.dataobject.SurveyQuestionNewDO;
import com.winit.alseer.salesman.listeners.DropdownSelectionListner;
import com.winit.alseer.salesman.utilities.CustomCheckBox;
import com.winit.alseer.salesman.utilities.RadioCustomCheckBox;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.MyApplicationNew;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;
import com.winit.sfa.salesman.MediaOptionsActivity;
import com.winit.sfa.salesman.SurveyQuestionActivity;

public class QuestionAdapter extends BaseAdapter
{
	private Context context;
	private Vector<SurveyQuestionNewDO> vecQuestion;
	private ImageLoader imageLoader;
	private DropdownSelectionListner dropdownSelectionListner;
	
	public QuestionAdapter(Context context,DropdownSelectionListner dropdownSelectionListner,Vector<SurveyQuestionNewDO> vecQuestion) 
	{
		this.context = context;
		this.vecQuestion = vecQuestion;
		this.dropdownSelectionListner=dropdownSelectionListner;
		imageLoader = new ImageLoader(context, 90, 90);
	}

	@Override
	public int getCount() 
	{
		if(vecQuestion != null && vecQuestion.size() > 0)
			return vecQuestion.size();
		else return 0;
	}

	public Vector<SurveyQuestionNewDO> getAdapterData()
	{
		return vecQuestion;
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

	public void refresh(Vector<SurveyQuestionNewDO> vecQuestion) 
	{
		this.vecQuestion = vecQuestion;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		final SurveyQuestionNewDO questionsDO = vecQuestion.get(position);
		//		if(convertView == null)
		convertView = LayoutInflater.from(context).inflate(R.layout.quesions_list_cell, null);

		TextView  tvQuestion = (TextView)convertView.findViewById(R.id.tvQuestionName);
		TextView  tvMandatory = (TextView)convertView.findViewById(R.id.tvMandatory);

		LinearLayout llOptions = (LinearLayout)convertView.findViewById(R.id.llOptions);
		LinearLayout llInnerLayout = null;
		
		switch (AppConstants.getOptionsTypes(questionsDO.AnswerType))
		{
		
		case YESNO:
			llInnerLayout=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.yesnolayout, null);
			
			final RadioCustomCheckBox  rbOptionValue1 = (RadioCustomCheckBox)llInnerLayout.findViewById(R.id.rbOptionValue1);
			final RadioCustomCheckBox  rbOptionValue2 = (RadioCustomCheckBox)llInnerLayout.findViewById(R.id.rbOptionValue2);
			TextView  tvOptionText1RB = (TextView)llInnerLayout.findViewById(R.id.tvOptionText1RB);
			TextView  tvOptionText2RB = (TextView)llInnerLayout.findViewById(R.id.tvOptionText2RB);

			if(questionsDO.vecOptions!=null && questionsDO.vecOptions.size() > 1)
			{
				tvOptionText1RB.setText(questionsDO.vecOptions.get(0).OptionName);
				tvOptionText2RB.setText(questionsDO.vecOptions.get(1).OptionName);
				
			}
			
			if(questionsDO.Answer.equalsIgnoreCase("Yes"))
			{
				rbOptionValue1.setChecked(true);
				rbOptionValue2.setChecked(false);
			}
			else if(questionsDO.Answer.equalsIgnoreCase("No"))
			{
				rbOptionValue1.setChecked(false);
				rbOptionValue2.setChecked(true);
			}
			
			rbOptionValue1.setTag(position);
			rbOptionValue1.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					vecQuestion.get(((Integer)v.getTag())).Answer="Yes";
					if(questionsDO.vecOptions!=null && questionsDO.vecOptions.size() > 1)
						vecQuestion.get(((Integer)v.getTag())).SurveyQuestionOptionId=questionsDO.vecOptions.get(0).SurveyQuestionOptionId;
					rbOptionValue1.setChecked(true);
					rbOptionValue2.setChecked(false);
				}
			});
			
			tvOptionText1RB.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) 
				{
					
					rbOptionValue1.performClick();
				}
			});
			
			rbOptionValue2.setTag(position);
			rbOptionValue2.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					vecQuestion.get(((Integer)v.getTag())).Answer="No";
					if(questionsDO.vecOptions!=null && questionsDO.vecOptions.size() > 1)
						vecQuestion.get(((Integer)v.getTag())).SurveyQuestionOptionId=questionsDO.vecOptions.get(1).SurveyQuestionOptionId;
					rbOptionValue1.setChecked(false);
					rbOptionValue2.setChecked(true);
				}
			});
			
			tvOptionText2RB.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) 
				{
					
					rbOptionValue2.performClick();
				}
			});
			
			break;

		
			case CHECKBOX:
				
				llInnerLayout=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.check_radio_options, null);
				LinearLayout llCheckRadio= (LinearLayout)llInnerLayout.findViewById(R.id.llCheckRadio);
				
				if(questionsDO.vecOptions!=null && questionsDO.vecOptions.size()>0)
				{
					for(OptionsDO options:questionsDO.vecOptions)
					{
							LinearLayout llImage=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.check_options_layout, null);
							
							final CustomCheckBox  cbOptionValue1 = (CustomCheckBox)llImage.findViewById(R.id.cbOptionValue);
							TextView  tvOptionText2CB = (TextView)llImage.findViewById(R.id.tvOptionText);
							
							tvOptionText2CB.setText(options.OptionName);
							
							if(options.isChecked)
								cbOptionValue1.setChecked(true);
							else
								cbOptionValue1.setChecked(false);


							cbOptionValue1.setTag(position);
							cbOptionValue1.setTag(R.string.add, options);
							cbOptionValue1.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View v) 
								{
									OptionsDO op = (OptionsDO) v.getTag(R.string.add);
									if(cbOptionValue1.isChecked())
									{
										cbOptionValue1.setChecked(false);
										op.isChecked=false;
									}
									else
									{
										cbOptionValue1.setChecked(true);
										op.isChecked=true;
										questionsDO.Answer = op.SurveyQuestionOptionId;
									}
								}
							});
							
							llImage.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) 
								{
									cbOptionValue1.performClick();
									
								}
							});
						    
						llCheckRadio.addView(llImage, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
					}
				}
				break;
			
			case RADIO:
				llInnerLayout=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.check_radio_options, null);
				
				final LinearLayout llRadioCheck= (LinearLayout)llInnerLayout.findViewById(R.id.llCheckRadio);
				
				if(questionsDO.vecOptions!=null && questionsDO.vecOptions.size()>0)
				{
					for(OptionsDO options:questionsDO.vecOptions)
					{
							LinearLayout llImage=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.radio_options_layout, null);
							
							final RadioCustomCheckBox  cbOptionValue1 = (RadioCustomCheckBox)llImage.findViewById(R.id.rbOptionValue);
							TextView  tvOptionText2CB = (TextView)llImage.findViewById(R.id.tvOptionTextRB);
				
							
							tvOptionText2CB.setText(options.OptionName);
							
							if(options.SurveyQuestionOptionId.equalsIgnoreCase(questionsDO.Answer))
								cbOptionValue1.setChecked(true);
							else
								cbOptionValue1.setChecked(false);
							
							

							cbOptionValue1.setTag(R.string.add, options);
							cbOptionValue1.setOnClickListener(new OnClickListener() 
						       {
								
								@Override
								public void onClick(View v) 
								{
									cbOptionValue1.setChecked(true);
									OptionsDO op = (OptionsDO) v.getTag(R.string.add);
									questionsDO.Answer = op.SurveyQuestionOptionId;
									questionsDO.SurveyQuestionOptionId = op.SurveyQuestionOptionId;
									refreshCheckBoxes(llRadioCheck,questionsDO);
								
								}
							});
							
							llImage.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) 
								{
									cbOptionValue1.performClick();
									
								}
							});
						    
							llRadioCheck.addView(llImage, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
					}
				}
				
				
				break;
			case STAR:
				llInnerLayout=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.ratingbar, null);
				final RatingBar rbRating = (RatingBar)llInnerLayout.findViewById(R.id.rbRating);
				
				if(questionsDO.Answer!=null && !questionsDO.Answer.equalsIgnoreCase(""))
				{
					rbRating.setRating(StringUtils.getFloat(questionsDO.Answer));
				}
				
				rbRating.setOnRatingBarChangeListener(new OnRatingBarChangeListener() 
				{
					
					@Override
					public void onRatingChanged(RatingBar ratingBar, float rating,boolean fromUser) 
					{
						ratingBar.setRating(rating);
						questionsDO.Answer = ""+rating;
					}
				});
				break;
				
			case MEDIA:
				llInnerLayout			   =(LinearLayout) LayoutInflater.from(context).inflate(R.layout.media_option, null);
				final ImageView  ivMediaType  = (ImageView)llInnerLayout.findViewById(R.id.mediaType);
				final ImageView  mediaOption  = (ImageView)llInnerLayout.findViewById(R.id.mediaOption);
				
				switch (AppConstants.getMediaOptionsTypes(questionsDO.AnswerType))
				{
					case IMAGE:
						ivMediaType.setBackgroundResource(R.drawable.spic);
						ivMediaType.setTag(OptionsNames.IMAGE);
						
						if(!questionsDO.Answer.equalsIgnoreCase(""))
						{
							final Uri uri = Uri.parse(questionsDO.Answer);

							//temp  -- dalayya
						       if (uri != null) 
						       {
						          Bitmap bitmap = getHttpImageManager().loadImage(
						          new HttpImageManager.LoadRequest(uri,  mediaOption,questionsDO.Answer));
						        if (bitmap != null) {
						        	mediaOption.setImageBitmap(bitmap);
						        }
						       }
						}
						break;
					case VIDEO:
						ivMediaType.setBackgroundResource(R.drawable.svid);
						ivMediaType.setTag(OptionsNames.VIDEO);
						if(!questionsDO.Answer.equalsIgnoreCase(""))
						{
						    Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(questionsDO.Answer,MediaStore.Images.Thumbnails.MINI_KIND); 
							mediaOption.setImageBitmap(thumbnail);
						}
					  
						break;
					case AUDIO:
						ivMediaType.setBackgroundResource(R.drawable.saud);
						ivMediaType.setTag(OptionsNames.AUDIO);
						if(!questionsDO.Answer.equalsIgnoreCase(""))
						{
							mediaOption.setBackgroundResource(R.drawable.saud_icn);
						}
						break;
				}
				
				ivMediaType.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) 
					{
						Intent intent = new Intent(context, MediaOptionsActivity.class);
						intent.putExtra("QuestionsDO", questionsDO);
						((SurveyQuestionActivity)context).startActivityForResult(intent, 400);
						
					}
				});
				
				break;
			case DROPDOWN:
				llInnerLayout		=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.dropdown, null);
				
				final TextView dropdown   = (TextView)llInnerLayout.findViewById(R.id.etOption);
				LinearLayout llDropDown   = (LinearLayout)llInnerLayout.findViewById(R.id.llDropDown);
				dropdown.setFocusable(false);
				
				if(questionsDO.vecOptions!=null)
					dropdown.setClickable(true);
				else
					dropdown.setClickable(false);
					
				if(!questionsDO.Answer.equalsIgnoreCase(""))
				{
					dropdown.setText(questionsDO.Answer);
				}
//				
				dropdown.setOnClickListener(new OnClickListener() 
				{
					
					@Override
					public void onClick(View v) 
					{
						((BaseActivity)context).popupWindowOptions(questionsDO,(TextView)v, questionsDO.vecOptions,dropdownSelectionListner);
						
						if(v.getTag()!=null)
						{
							OptionsDO option = (OptionsDO) v.getTag();
							questionsDO.Answer = option.SurveyQuestionOptionId;
							questionsDO.SurveyQuestionOptionId = option.SurveyQuestionOptionId;
						}
						
					}
				});
				llDropDown.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) 
					{
						dropdown.performClick();
						
					}
				});
				
				break;
			case EMOTION:
				llInnerLayout=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.emotion_options, null);
				final LinearLayout llEmotionLayout = (LinearLayout)llInnerLayout.findViewById(R.id.llEmotions);
				if(questionsDO.vecOptions!=null && questionsDO.vecOptions.size()>0)
				{
					for(OptionsDO options:questionsDO.vecOptions)
					{
						if(options.ImagePath!=null && !options.ImagePath.equalsIgnoreCase(""))
						{
							LinearLayout llImage=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.emotion_image_cell, null);
							ImageView ivImg = (ImageView)llImage.findViewById(R.id.ivEmotionImage);
							ImageView ivVisited = (ImageView)llImage.findViewById(R.id.ivVisited);
							TextView tvEmotionText = (TextView)llImage.findViewById(R.id.tvEmotionText);
							
							if(options.ImagePath.equalsIgnoreCase(questionsDO.Answer))
								ivVisited.setVisibility(View.VISIBLE);
							else
								ivVisited.setVisibility(View.GONE);
							
							tvEmotionText.setText(options.EmotionName);
							
							
							ivImg.setTag(options.ImagePath);
							imageLoader.DisplayImage(options.ImagePath.hashCode()+"", options.ImagePath, ((SurveyQuestionActivity)context), ivImg);


							
						       ivImg.setTag(R.string.add, options);
						       ivImg.setOnClickListener(new OnClickListener() 
						       {
								
								@Override
								public void onClick(View v) 
								{
									OptionsDO op = (OptionsDO) v.getTag(R.string.add);
									questionsDO.Answer = op.ImagePath;
									questionsDO.SurveyQuestionOptionId = op.SurveyQuestionOptionId;
									refreshViwa(llEmotionLayout,questionsDO);
									
								}
							});
						       llEmotionLayout.addView(llImage, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
						}      
					}
				}
				
				break;
			
			case SINGLE_LINE:
				llInnerLayout=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.edit_line, null);
				final EditText     etOption       = (EditText)llInnerLayout.findViewById(R.id.etOption);
				ImageView ivEditDrawable = (ImageView)llInnerLayout.findViewById(R.id.ivEditDrawable);
				if(!questionsDO.Answer.equalsIgnoreCase(""))
				{
					etOption.setText(questionsDO.Answer);
				}
				
				if(questionsDO.AnswerType.equalsIgnoreCase("MULTI_LINE"))
				{
					ivEditDrawable.setVisibility(View.GONE);
					etOption.setBackgroundResource(R.drawable.download);
				}
				else
				{
					ivEditDrawable.setVisibility(View.VISIBLE);
					etOption.setBackgroundResource(0);
				}
				
				if(questionsDO.AnswerType.equalsIgnoreCase("MULTI_LINE"))
				{
					etOption.setHint("Ex: Sample Text");
					etOption.setSingleLine(false);
					etOption.setMaxLines(6);
				}
				else if(questionsDO.AnswerType.equalsIgnoreCase("NUMERIC"))
				{
					etOption.setHint("Ex: 1234");
					etOption.setInputType(InputType.TYPE_CLASS_NUMBER);
				}	
				else
					etOption.setHint("Ex: Sample Text");
				
				if(position==vecQuestion.size()-1)
					etOption.setImeOptions(EditorInfo.IME_ACTION_DONE);
				else
					etOption.setImeOptions(EditorInfo.IME_ACTION_NEXT);
				
				etOption.addTextChangedListener(new TextWatcher() {
					
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						questionsDO.Answer=s.toString();
					}
					
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
						
					}
					
					@Override
					public void afterTextChanged(Editable s) {
						
					}
				});
				
				break;
		}

		tvQuestion.setText(""+(position+1)+". "+vecQuestion.get(position).QuestionName);
		if(questionsDO.IsMandatory.equalsIgnoreCase("true"))
			tvMandatory.setVisibility(View.VISIBLE);
		else
			tvMandatory.setVisibility(View.GONE);

		if(llInnerLayout!=null)
			llOptions.addView(llInnerLayout, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

		((BaseActivity)context).setTypeFaceRobotoNormal(llInnerLayout);
		tvQuestion.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		return convertView;
	}
	
	
	public void refreshViwa(LinearLayout llout, SurveyQuestionNewDO questionsDO)
	{
		if(llout!=null && llout.getChildCount()>0)
		{
			for(int i=0;i<llout.getChildCount();i++)
			{
				LinearLayout llImage=(LinearLayout) llout.getChildAt(i);
				ImageView ivImg = (ImageView)llImage.findViewById(R.id.ivEmotionImage);
				ImageView ivVisited = (ImageView)llImage.findViewById(R.id.ivVisited);
				OptionsDO op = (OptionsDO) ivImg.getTag(R.string.add);
				
				if(op.ImagePath.equalsIgnoreCase(questionsDO.Answer))
					ivVisited.setVisibility(View.VISIBLE);
				else
					ivVisited.setVisibility(View.GONE);
				
			}
		}
	}
	public void refreshCheckBoxes(LinearLayout llout, SurveyQuestionNewDO questionsDO)
	{
		if(llout!=null && llout.getChildCount()>0)
		{
			for(int i=0;i<llout.getChildCount();i++)
			{
				LinearLayout llImage=(LinearLayout) llout.getChildAt(i);
				
				final RadioCustomCheckBox  cbOptionValue1 = (RadioCustomCheckBox)llImage.findViewById(R.id.rbOptionValue);
				
				OptionsDO op = (OptionsDO) cbOptionValue1.getTag(R.string.add);
				
				if(op.SurveyQuestionOptionId.equalsIgnoreCase(questionsDO.Answer))
				
					cbOptionValue1.setChecked(true);
				else
					cbOptionValue1.setChecked(false);
				
			}
		}
	}
	public HttpImageManager getHttpImageManager() {
		return ((MyApplicationNew) ((Activity) context)
				.getApplication()).getHttpImageManager();
	}
}
