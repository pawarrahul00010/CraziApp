package com.technohertz.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.technohertz.model.Chat;

public class ChatUserInnerFirebase implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userChatId;

	private Map<String, Chat> chatList=new TreeMap<String, Chat>();


	public String getUserChatId() {
		return userChatId;
	}

	public void setUserChatId(String userChatId) {
		this.userChatId = userChatId;
	}

	public Map<String, Chat> getChatList() {
		return chatList;
	}

	public void setChatList(Map<String, Chat> chatList) {
		this.chatList = chatList;
	}

	public ChatUserInnerFirebase(String userChatId, Map<String, Chat> chatList) {
		super();
		this.userChatId = userChatId;
		this.chatList = chatList;
	}



	
}
