//package com.winit.sfa.salesman;
//
//import java.util.Vector;
//
//import android.graphics.Color;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.Gravity;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.winit.alseer.salesman.common.Preference;
//import com.winit.alseer.salesman.dataaccesslayer.ChatMessagesDA;
//import com.winit.alseer.salesman.dataobject.ChatMessagesDO;
//import com.winit.kwality.salesman.R;
//
//public class MessageViewActivity extends BaseActivity
//{
//	private LinearLayout llMessageView,llMsgView ;
//	private EditText etMsg;
//	private ImageView ivCamera,ivSend;
//	private Vector<ChatMessagesDO> vecChatMessagesDOs;
//	private String SenderId,ReceiverId;
//	
//	@Override
//	public void initialize() 
//	{
//		llMessageView			=	(LinearLayout)inflater.inflate(R.layout.user_message_view,null);
//		llBody.addView(llMessageView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//		
//		initializeControls();
//		
//		SenderId = preference.getStringFromPreference(Preference.USER_ID, "");
//		
//		loadData();
//		
//		etMsg.addTextChangedListener(new TextWatcher() {
//			
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) 
//			{
//				if(etMsg.getText().toString().length() > 0)
//				{
//					ivCamera.setVisibility(View.GONE);
//					ivSend.setVisibility(View.VISIBLE);
//				}
//				else
//				{
//					ivCamera.setVisibility(View.VISIBLE);
//					ivSend.setVisibility(View.GONE);
//				}
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,int after) 
//			{
//				
//			}
//			
//			@Override
//			public void afterTextChanged(Editable s) 
//			{
//				
//			}
//		});
//		
//		ivCamera.setOnClickListener(new OnClickListener() 
//		{
//			
//			@Override
//			public void onClick(View v) 
//			{
//				
//			}
//		});
//		
//		ivSend.setOnClickListener(new OnClickListener() 
//		{
//			
//			@Override
//			public void onClick(View v) 
//			{
//				final ChatMessagesDO chatMessagesDO = new ChatMessagesDO();
//				chatMessagesDO.MessageId 	= "";//Need to create Randomly
//				chatMessagesDO.MessageBody 	= etMsg.getText().toString();
//				chatMessagesDO.SenderId 	= SenderId;
//				chatMessagesDO.ReceiverId 	= ReceiverId;
//				chatMessagesDO.Status 		= "0";
//				
//				
//				addNewMessage(chatMessagesDO);
//				etMsg.setText("");
//				
//				new Thread(new Runnable() 
//				{
//					@Override
//					public void run() 
//					{
//						new ChatMessagesDA().insertMessage(chatMessagesDO);
//						//Need to implement service to send message
//					}
//				}).start();
//				
//				
//			}
//		});
//		
//		setTypeFaceRobotoNormal(llMessageView);
//	}
//	
//	
//	private void initializeControls()
//	{
//		llMsgView = (LinearLayout) llMessageView.findViewById(R.id.llMsgView);
//		etMsg = (EditText) llMessageView.findViewById(R.id.etMsg);
//		ivCamera = (ImageView) llMessageView.findViewById(R.id.ivCamera);
//		ivSend = (ImageView) llMessageView.findViewById(R.id.ivSend);
//	}
//	
//	private void loadData()
//	{
//		showLoader("Loading...");
//		new Thread(new Runnable() 
//		{
//			
//			@Override
//			public void run() 
//			{
//				vecChatMessagesDOs = new ChatMessagesDA().getAllChatMessages(SenderId,ReceiverId);
//				
//				runOnUiThread(new Runnable() 
//				{
//					@Override
//					public void run() 
//					{
//						if(vecChatMessagesDOs != null && vecChatMessagesDOs.size() > 0)
//						{
//							for(ChatMessagesDO chatMessagesDO : vecChatMessagesDOs)
//							{
//								addNewMessage(chatMessagesDO);
//							}
//						}
//						hideLoader();
//					}
//				});
//			}
//		}).start();
//	}
//	
//	private void addNewMessage(ChatMessagesDO chatMessagesDO)
//	{
//		final LinearLayout llmsg = (LinearLayout)inflater.inflate(R.layout.message_layout, null);
//		
//		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//		
//		
//		TextView tvMessage = (TextView) llmsg.findViewById(R.id.tvMessage);
//		ImageView ivImage =(ImageView) llmsg.findViewById(R.id.ivImage);
//		
//		
//		if(chatMessagesDO.SenderId.equalsIgnoreCase(SenderId))
//		{
//			layoutParams.gravity = Gravity.RIGHT;
//			llmsg.setGravity(Gravity.RIGHT);
//			llmsg.setBackgroundColor(Color.parseColor("#A0A0A0"));
//		}
//		else
//		{
//			layoutParams.gravity = Gravity.LEFT;
//			llmsg.setGravity(Gravity.LEFT);
//			llmsg.setBackgroundColor(Color.parseColor("#B0B0B0"));
//		}
//		layoutParams.setMargins(0, 20, 0, 0);
//		llmsg.setLayoutParams(layoutParams);
//		if(chatMessagesDO.AttachmentLink != null && !chatMessagesDO.AttachmentLink.equalsIgnoreCase(""))
//		{
//			ivImage.setVisibility(View.VISIBLE);
//			//Need to set the image background
//		}
//		else
//		{
//			ivImage.setVisibility(View.GONE);
//		}
//		if(chatMessagesDO.MessageBody != null && !chatMessagesDO.MessageBody.equalsIgnoreCase(""))
//			tvMessage.setText(chatMessagesDO.MessageBody);
//		
//		llMsgView.addView(llmsg);
//		llmsg.requestFocus();
//	}
//	
//	
//}
