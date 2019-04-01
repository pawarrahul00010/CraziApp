package com.technohertz.service;

import com.technohertz.model.Chat;
import com.technohertz.model.ChatUser;


public interface IUserChatService {
	


	public Chat save(Chat chat);

	public ChatUser saveChat(Object object);

	ChatUser saveChat(ChatUser chatUser);
	

}
