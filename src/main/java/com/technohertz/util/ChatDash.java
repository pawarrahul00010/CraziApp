package com.technohertz.util;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class ChatDash implements Serializable {

	private static final long serialVersionUID = 1L;

	private String message;

	private String user;
	
	private String time;
	
	private String timestamp;
	
	private String chatKey;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}


	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String string) {
		this.timestamp = string;
	}

	public String getChatKey() {
		return chatKey;
	}

	public void setChatKey(String chatKey) {
		this.chatKey = chatKey;
	}


	
}
