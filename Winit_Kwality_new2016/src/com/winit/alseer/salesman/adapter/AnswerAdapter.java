package com.winit.alseer.salesman.adapter;

import httpimage.HttpImageManager;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.ImageLoader;
import com.winit.alseer.salesman.common.OptionsNames;
import com.winit.alseer.salesman.dataobject.OptionsDO;
import com.winit.alseer.salesman.dataobject.QuestionsDO;
import com.winit.alseer.salesman.dataobject.SurveyQuestionNewDO;
import com.winit.alseer.salesman.dataobject.UserSurveyAnswerDO;
import com.winit.alseer.salesman.listeners.DropdownSelectionListner;
import com.winit.alseer.salesman.utilities.CustomCheckBox;
import com.winit.alseer.salesman.utilities.RadioCustomCheckBox;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.MyApplicationNew;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;
import com.winit.sfa.salesman.SurveyQuestionActivity;

public class AnswerAdapter extends BaseAdapter
{
	private Context context;
	private Vector<SurveyQuestionNewDO> vecQuestion;
	private ImageLoader imageLoader;
	private DropdownSelectionListner dropdownSelectionListner;
	
	public AnswerAdapter(Context context,DropdownSelectionListner dropdownSelectionListner,Vector<SurveyQuestionNewDO> vecQuestion) 
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
				if(questionsDO.vecUserSurveyAnswers!=null && questionsDO.vecUserSurveyAnswers.size() > 0)
				{
					questionsDO.Answer = questionsDO.vecUserSurveyAnswers.get(0).Answer;
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
							cbOptionValue1.setClickable(false);
							TextView  tvOptionText2CB = (TextView)llImage.findViewById(R.id.tvOptionText);
							tvOptionText2CB.setText(options.OptionName);
						    llCheckRadio.addView(llImage, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
						    if(questionsDO.vecUserSurveyAnswers!=null && questionsDO.vecUserSurveyAnswers.size()>0)
							{
								for(UserSurveyAnswerDO userSurveyAnswerDO:questionsDO.vecUserSurveyAnswers)
								{
									if(options.SurveyQuestionOptionId.equalsIgnoreCase(userSurveyAnswerDO.SurveyOptionId))
										cbOptionValue1.setChecked(true);
								}
							}
					}
				}
	
				break;
				
			case RADIO:
				
				llInnerLayout=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.check_radio_options, null);
				LinearLayout llRadioCheck= (LinearLayout)llInnerLayout.findViewById(R.id.llCheckRadio);
				
				if(questionsDO.vecOptions!=null && questionsDO.vecOptions.size()>0)
				{
					for(OptionsDO options:questionsDO.vecOptions)
					{
							LinearLayout llImage=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.radio_options_layout, null);
							
							final RadioCustomCheckBox  cbOptionValue1 = (RadioCustomCheckBox)llImage.findViewById(R.id.rbOptionValue);
							cbOptionValue1.setClickable(false);
							TextView  tvOptionText2CB = (TextView)llImage.findViewById(R.id.tvOptionTextRB);
							tvOptionText2CB.setText(options.OptionName);
							llRadioCheck.addView(llImage, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
						    if(questionsDO.vecUserSurveyAnswers!=null && questionsDO.vecUserSurveyAnswers.size()>0)
							{
								for(UserSurveyAnswerDO userSurveyAnswerDO:questionsDO.vecUserSurveyAnswers)
								{
									if(options.SurveyQuestionOptionId.equalsIgnoreCase(userSurveyAnswerDO.SurveyOptionId))
										cbOptionValue1.setChecked(true);
								}
							}
					}
				}
				
				
				break;
			case STAR:
				llInnerLayout=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.ratingbar, null);
				final RatingBar rbRating = (RatingBar)llInnerLayout.findViewById(R.id.rbRating);
				rbRating.setClickable(false);
				if(questionsDO.vecUserSurveyAnswers!=null && questionsDO.vecUserSurveyAnswers.size() > 0)
				{
					questionsDO.Answer = questionsDO.vecUserSurveyAnswers.get(0).Answer;
				}
				if(!questionsDO.Answer.equalsIgnoreCase(""))
				{
					rbRating.setRating(StringUtils.getFloat(questionsDO.Answer));
				}
				
				break;
				
			case MEDIA:
				llInnerLayout			   =(LinearLayout) LayoutInflater.from(context).inflate(R.layout.media_option, null);
				final ImageView  ivMediaType  = (ImageView)llInnerLayout.findViewById(R.id.mediaType);
				final ImageView  mediaOption  = (ImageView)llInnerLayout.findViewById(R.id.mediaOption);
				ivMediaType.setClickable(false);
				//SampleAnswer: ~/Data/UploadedFiles/Images/635492580896360445.jpg
				if(questionsDO.vecUserSurveyAnswers!=null && questionsDO.vecUserSurveyAnswers.size() > 0)
				{
					questionsDO.Answer = questionsDO.vecUserSurveyAnswers.get(0).Answer;
					
				}
				
				switch (AppConstants.getMediaOptionsTypes(questionsDO.AnswerType))
				{
					case IMAGE:
						ivMediaType.setBackgroundResource(R.drawable.spic);
						ivMediaType.setTag(OptionsNames.IMAGE);
						
						if(!questionsDO.Answer.equalsIgnoreCase(""))
						{
							
							
							if(questionsDO.Answer.contains("http")||questionsDO.Answer.contains("https"))
							{
								mediaOption.setTag(questionsDO.Answer);
								imageLoader.DisplayImage(questionsDO.Answer.hashCode()+"", questionsDO.Answer, ((SurveyQuestionActivity)context), mediaOption);
							}
							else
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
				
				break;
			case DROPDOWN:
				llInnerLayout		=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.dropdown, null);
				final TextView dropdown   = (TextView)llInnerLayout.findViewById(R.id.etOption);
				
				if(questionsDO.vecUserSurveyAnswers!=null && questionsDO.vecUserSurveyAnswers.size() > 0)
				{
					questionsDO.Answer = questionsDO.vecUserSurveyAnswers.get(0).Answer;
				}
				
				if(!questionsDO.Answer.equalsIgnoreCase(""))
				{
					dropdown.setText(questionsDO.Answer);
				}
//				
				
				break;
			case EMOTION:
				llInnerLayout=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.emotion_options, null);
				LinearLayout llEmotionLayout = (LinearLayout)llInnerLayout.findViewById(R.id.llEmotions);
				if(questionsDO.vecUserSurveyAnswers!=null && questionsDO.vecUserSurveyAnswers.size()>0)
				{
					for(UserSurveyAnswerDO options:questionsDO.vecUserSurveyAnswers)
					{
						if(options.Answer!=null && !options.Answer.equalsIgnoreCase(""))
						{
							LinearLayout llImage=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.emotion_image_cell, null);
							ImageView ivImg = (ImageView)llImage.findViewById(R.id.ivEmotionImage);
							TextView tvEmotionText = (TextView)llImage.findViewById(R.id.tvEmotionText);
							tvEmotionText.setText(options.EmotionName);
							
							ivImg.setTag(options.Answer);
							imageLoader.DisplayImage(options.Answer.hashCode()+"", options.Answer, ((SurveyQuestionActivity)context), ivImg);
						       
						    llEmotionLayout.addView(llImage, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
						}
					}
				}
				
				break;
			
			case SINGLE_LINE:
				llInnerLayout=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.edit_line, null);
				final EditText     etOption       = (EditText)llInnerLayout.findViewById(R.id.etOption);
				etOption.setEnabled(false);
				ImageView ivEditDrawable = (ImageView)llInnerLayout.findViewById(R.id.ivEditDrawable);
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
				if(questionsDO.vecUserSurveyAnswers!=null && questionsDO.vecUserSurveyAnswers.size() > 0)
				{
					questionsDO.Answer = questionsDO.vecUserSurveyAnswers.get(0).Answer;
				}
				if(!questionsDO.Answer.equalsIgnoreCase(""))
				{
					etOption.setText(questionsDO.Answer);
				}
				
				
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
	
//	public HttpImageManager getHttpImageManager () 
//	{
//	     return ((MerchandiserApplication) ((Activity) context).getApplication()).getHttpImageManager();
//	 }
	
	public void setAnswers(QuestionsDO questionDO)
	{
		
	}
	public HttpImageManager getHttpImageManager() {
		return ((MyApplicationNew) ((Activity) context)
				.getApplication()).getHttpImageManager();
	}
}
