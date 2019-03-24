package com.technohertz.util;

import java.util.List;

import com.technohertz.model.GroupProfile;
import com.technohertz.model.MediaFiles;

public class GetProfile {

	private String CraziId;
	
	private String userName;

	private Integer userId;
	
	private Number photos;

	private Long likes;

	private Double rating;

	private String profilePath;

	private String abooutUser;


	private String displayName;

//private UserProfile userProfile;

	private List<MediaFiles> profileList;

	private List<MediaFiles> bookmarkList;

	private List<GroupProfile> groupList;

	


	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/*
	 * public UserProfile getUserProfile() { return userProfile; }
	 * 
	 * public void setUserProfile(UserProfile userProfile) { this.userProfile =
	 * userProfile; }
	 */
	public List<MediaFiles> getProfileList() {
		return profileList;
	}

	public void setProfileList(List<MediaFiles> profileList) {
		this.profileList = profileList;
	}

	public List<MediaFiles> getBookmarkList() {
		return bookmarkList;
	}

	public void setBookmarkList(List<MediaFiles> bookmarkList) {
		this.bookmarkList = bookmarkList;
	}

	public List<GroupProfile> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<GroupProfile> groupList) {
		this.groupList = groupList;
	}

	public Number getPhotos() {
		return photos;
	}

	public void setPhotos(Number photos) {
		this.photos = photos;
	}

	public Long getLikes() {
		return likes;
	}

	public void setLikes(Long likes) {
		this.likes = likes;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	/**
	 * @return the craziId
	 */
	public String getCraziId() {
		return CraziId;
	}

	/**
	 * @param craziId the craziId to set
	 */
	public void setCraziId(String craziId) {
		CraziId = craziId;
	}

	/**
	 * @return the imagePath
	 */

	
	/**
	 * @return the abooutUser
	 */
	public String getAbooutUser() {
		return abooutUser;
	}

	/**
	 * @param profilePath the profilePath to set
	 */
	public void setProfilePath(String profilePath) {
		this.profilePath = profilePath;
	}

	/**
	 * @param abooutUser the abooutUser to set
	 */
	public void setAbooutUser(String abooutUser) {
		this.abooutUser = abooutUser;
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
	 * @return the profilePath
	 */
	public String getProfilePath() {
		return profilePath;
	}
	
	

}
