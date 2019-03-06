package com.technohertz.util;

import java.math.BigInteger;
import java.util.List;

import com.technohertz.model.GroupProfile;
import com.technohertz.model.MediaFiles;

public class GetProfile {


private String user;

private Integer userId;

//private UserProfile userProfile;

private List<MediaFiles> profileList;

private List<MediaFiles> bookmarkList;

private List<GroupProfile> groupList;

private Number  photos;

private Long likes;

private Float rating;

public String getUser() {
	return user;
}

public void setUser(String user) {
	this.user = user;
}

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

public Number  getPhotos() {
	return photos;
}

public void setPhotos(Number  photos) {
	this.photos = photos;
}

public Long getLikes() {
	return likes;
}

public void setLikes(Long likes) {
	this.likes = likes;
}

public Float getRating() {
	return rating;
}

public void setRating(Float rating) {
	this.rating = rating;
}


}
