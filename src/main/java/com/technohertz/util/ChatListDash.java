package com.technohertz.util;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class ChatListDash implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<ChatDash> chatList;

	public List<ChatDash> getChatList() {
		return chatList;
	}

	public void setChatList(List<ChatDash> chatList) {
		this.chatList = chatList;
	}

	
}
