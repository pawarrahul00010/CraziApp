package com.technohertz.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.technohertz.model.MediaFiles;
import com.technohertz.model.SecretConversation;
import com.technohertz.model.SharedMedia;
import com.technohertz.model.UserContact;

@Component
public class LoginResponce {

	
	
	private Integer userId;
	
	

	private String userName;
	

	private String displayName;

	private String mobilNumber;

	private String currentProfile;

	private String aboutUser;

	private Object UserData ;
	
	private List<MediaFiles> mediaFiles;
	
	private List<SecretConversation> secretConversation;
	
	private List<UserContact> userContact;

	
	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
	 * @return the currentProfile
	 */
	public String getCurrentProfile() {
		return currentProfile;
	}

	/**
	 * @param currentProfile the currentProfile to set
	 */
	public void setCurrentProfile(String currentProfile) {
		this.currentProfile = currentProfile;
	}

	/**
	 * @return the aboutUser
	 */
	public String getAboutUser() {
		return aboutUser;
	}

	/**
	 * @param aboutUser the aboutUser to set
	 */
	public void setAboutUser(String aboutUser) {
		this.aboutUser = aboutUser;
	}

	/**
	 * @return the userData
	 */
	public Object getUserData() {
		return UserData;
	}

	/**
	 * @param userData the userData to set
	 */
	public void setUserData(Object userData) {
		UserData = userData;
	}

	/**
	 * @return the mobilNumber
	 */
	public String getMobilNumber() {
		return mobilNumber;
	}

	/**
	 * @param mobilNumber the mobilNumber to set
	 */
	public void setMobilNumber(String mobilNumber) {
		this.mobilNumber = mobilNumber;
	}


	/**
	 * @return the mediaFiles
	 */
	public List<MediaFiles> getMediaFiles() {
		return mediaFiles;
	}



	public void setMediaFiles(List<SharedMedia> media) {
		// TODO Auto-generated method stub
		
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

	/**
	 * @return the userContact
	 */
	public List<UserContact> getUserContact() {
		return userContact;
	}

	/**
	 * @param userContact the userContact to set
	 */
	public void setUserContact(List<UserContact> userContact) {
		this.userContact = userContact;
	}


}
