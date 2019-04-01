package com.technohertz.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Chat implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String chat;

	private List<Map<String, ChatUserFirebase> >  userList;

	public String getChat() {
		return chat;
	}

	public void setChat(String chat) {
		this.chat = chat;
	}

	public List<Map<String, ChatUserFirebase>> getUserList() {
		return userList;
	}

	public void setUserList(List<Map<String, ChatUserFirebase>> userList) {
		this.userList = userList;
	}

	public Chat(String chat, List<Map<String, ChatUserFirebase>> userList) {
		super();
		this.chat = chat;
		this.userList = userList;
	}

	
}
