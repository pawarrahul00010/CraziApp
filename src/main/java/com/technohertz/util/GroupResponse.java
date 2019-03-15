package com.technohertz.util;

import org.springframework.stereotype.Component;

@Component
public class GroupResponse {

	
	private Integer groupId;

	private String displayName;
	
	private Integer createdBy;

	private String currentProfile;

	private String aboutGroup;

	private Object files ;

	
	private Object groupMember;
	
	private Object groupAdminList;


	public Integer getGroupId() {
		return groupId;
	}


	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}


	public String getDisplayName() {
		return displayName;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	public Integer getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}


	public String getCurrentProfile() {
		return currentProfile;
	}


	public void setCurrentProfile(String currentProfile) {
		this.currentProfile = currentProfile;
	}


	public String getAboutGroup() {
		return aboutGroup;
	}


	public void setAboutGroup(String aboutGroup) {
		this.aboutGroup = aboutGroup;
	}


	public Object getFiles() {
		return files;
	}


	public void setFiles(Object files) {
		this.files = files;
	}


	public Object getGroupMember() {
		return groupMember;
	}


	public void setGroupMember(Object groupMember) {
		this.groupMember = groupMember;
	}


	public Object getGroupAdminList() {
		return groupAdminList;
	}


	public void setGroupAdminList(Object groupAdmin) {
		this.groupAdminList = groupAdmin;
	}


	@Override
	public String toString() {
		return "GroupResponse [groupId=" + groupId + ", displayName=" + displayName + ", createdBy=" + createdBy
				+ ", currentProfile=" + currentProfile + ", aboutGroup=" + aboutGroup + ", files=" + files
				+ ", groupMember=" + groupMember + ", groupAdminList=" + groupAdminList + "]";
	}

	
	
}
