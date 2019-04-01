package com.technohertz.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@SuppressWarnings("serial")
@Entity
@Table(name = "CHAT")
@DynamicUpdate
public class Chat implements Serializable {
	
	
	@Id
	@Column(name = "chatKey", nullable = false, length =5000)
	private String chatKey;

	
	@Column(name = "message", nullable = true,columnDefinition = "LONGBLOB")
	private String message;
	
	@Column(name = "time", nullable = true, length =20)
	private String time;

	@Column(name = "timestamp", nullable = true, length =50)
	private String timestamp;

	@Column(name = "user", nullable = true, length =50)
	private String user;


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getChatKey() {
		return chatKey;
	}

	public void setChatKey(String chatKey) {
		this.chatKey = chatKey;
	}
	
	
}
