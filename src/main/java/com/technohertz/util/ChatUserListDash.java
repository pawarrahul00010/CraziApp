package com.technohertz.util;

import java.io.Serializable;
import java.util.List;

public class ChatUserListDash implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<MainChatDash> UserChatList;

	public List<MainChatDash> getUserChatList() {
		return UserChatList;
	}

	public void setUserChatList(List<MainChatDash> userChatList) {
		UserChatList = userChatList;
	}


}
