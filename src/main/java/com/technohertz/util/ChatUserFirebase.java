package com.technohertz.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.technohertz.model.Chat;

public class ChatUserFirebase implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userId;

	private Map<String, ChatUserInnerFirebase> chatList=new TreeMap<String, ChatUserInnerFirebase>();

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Map<String, ChatUserInnerFirebase> getChatList() {
		return chatList;
	}

	public void setChatList(Map<String, ChatUserInnerFirebase> chatList) {
		this.chatList = chatList;
	}

	public ChatUserFirebase(String userId, Map<String, ChatUserInnerFirebase> chatList) {
		super();
		this.userId = userId;
		this.chatList = chatList;
	}



	
}
