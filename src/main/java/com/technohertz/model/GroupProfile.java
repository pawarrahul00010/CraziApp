package com.technohertz.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Group_Profile")
@DynamicUpdate
public class GroupProfile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "GROUP_ID")
	private Integer groupId;

	@Column(name = "Display_Name")
	private String displayName;
	
	@Column(name = "created_By")
	private Integer createdBy;

	@Column(name = "current_Profile")
	private String currentProfile;

	@Column(name = "About_Group")
	private String aboutGroup;

	@JsonIgnore
	@OneToMany(cascade = javax.persistence.CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "GROUP_ID")
	private List<MediaFiles> files = new ArrayList<MediaFiles>();

	@JsonIgnore
	 @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	@JoinTable(name = "group_profile_group_member", joinColumns = {@JoinColumn(name="groupId")},
													inverseJoinColumns = {@JoinColumn(name="contactId")})
	private List<UserContact> groupMember = new ArrayList<UserContact>();

	/**
	 * @return the groupId
	 */
	public Integer getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
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
	 * @return the createdBy
	 */
	public Integer getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
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
	 * @return the aboutGroup
	 */
	public String getAboutGroup() {
		return aboutGroup;
	}

	/**
	 * @param aboutGroup the aboutGroup to set
	 */
	public void setAboutGroup(String aboutGroup) {
		this.aboutGroup = aboutGroup;
	}

	/**
	 * @return the files
	 */
	public List<MediaFiles> getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(List<MediaFiles> files) {
		this.files = files;
	}

	/**
	 * @return the groupMember
	 */
	public List<UserContact> getGroupMember() {
		return groupMember;
	}

	/**
	 * @param groupMember the groupMember to set
	 */
	public void setGroupMember(List<UserContact> groupMember) {
		this.groupMember = groupMember;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GroupProfile [groupId=" + groupId + ", displayName=" + displayName + ", createdBy=" + createdBy
				+ ", currentProfile=" + currentProfile + ", aboutGroup=" + aboutGroup + ", files=" + files
				+ ", groupMember=" + groupMember + "]";
	}

}
