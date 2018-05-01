package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import com.winit.alseer.salesman.dataobject.ChatMessagesDO;

public class ChatMessagesDA extends BaseDA 
{
	private Vector<ChatMessagesDO> vecChatMessagesDOs = new Vector<ChatMessagesDO>();
	
	public Vector<ChatMessagesDO> getAllChatMessages(String SenderId,String ReceiverId)
	{
		
		return vecChatMessagesDOs;
	}
	
	public void insertMessage(ChatMessagesDO chatMessagesDO)
	{
		
	}
}
