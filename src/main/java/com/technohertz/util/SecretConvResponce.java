package com.technohertz.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.technohertz.model.SecretConversation;
import com.technohertz.model.UserRegister;

@Component
public class SecretConvResponce {

	
	private List<UserRegister> userRegister;
	
	private List<SecretConversation> secretConversation;
	
	private Integer convId;
	
	

	private Integer secretConvId;	


	private String userName;

	

	/**
	 * @return the userRegister
	 */
	public List<UserRegister> getUserRegister() {
		return userRegister;
	}


	/**
	 * @param userRegister the userRegister to set
	 */
	public void setUserRegister(List<UserRegister> userRegister) {
		this.userRegister = userRegister;
	}


	/**
	 * @return the convId
	 */
	public Integer getConvId() {
		return convId;
	}


	/**
	 * @param convId the convId to set
	 */
	public void setConvId(Integer convId) {
		this.convId = convId;
	}


	/**
	 * @return the secretConvId
	 */
	public Integer getSecretConvId() {
		return secretConvId;
	}


	/**
	 * @param secretConvId the secretConvId to set
	 */
	public void setSecretConvId(Integer secretConvId) {
		this.secretConvId = secretConvId;
	}


	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}


	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}


	/**
	 * @return the secretConversation
	 */
	public List<SecretConversation> getSecretConversation() {
		return secretConversation;
	}


	/**
	 * @param secretConversation the secretConversation to set
	 */
	public void setSecretConversation(List<SecretConversation> secretConversation) {
		this.secretConversation = secretConversation;
	}


	


}
