package com.technohertz.util;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class MainChatDash implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<ChatListDash> mainChatList;

	public List<ChatListDash> getMainChatList() {
		return mainChatList;
	}

	public void setMainChatList(List<ChatListDash> mainChatList) {
		this.mainChatList = mainChatList;
	}
	

}
