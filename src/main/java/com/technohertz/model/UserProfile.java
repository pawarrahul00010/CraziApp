package com.technohertz.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "User_Profile")
@DynamicUpdate
public class UserProfile implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USR_DET_ID")
	private Integer profileId;

	@Column(name = "Display_Name")
	private String displayName;

	@Column(name = "current_Profile")
	private String currentProfile;
	
	@Column(name = "About_User")
	private String aboutUser;
	
	@Column(name = "File_Create_Date")
	private LocalDateTime createDate;


	 @JsonIgnore
	@OneToMany(cascade=javax.persistence.CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name="USR_DET_ID")
	private List<MediaFiles> files=new ArrayList<MediaFiles>();

	 @JsonIgnore
	 @OneToMany(cascade=javax.persistence.CascadeType.ALL,fetch=FetchType.LAZY)
	 @JoinColumn(name="USR_DET_ID")
	 private List<SharedMedia> media=new ArrayList<SharedMedia>();

	public Integer getProfileId() {
		return profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}


	public List<MediaFiles> getFiles() {
		return files;
	}

	public void setFiles(List<MediaFiles> files) {
		this.files = files;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getAboutUser() {
		return aboutUser;
	}

	public void setAboutUser(String aboutUser) {
		this.aboutUser = aboutUser;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserProfile [profileId=" + profileId + ", displayName=" + displayName + ", currentProfile="
				+ currentProfile + ", aboutUser=" + aboutUser + ", files=" + files + "]";
	}

	/**
	 * @return the media
	 */
	public List<SharedMedia> getMedia() {
		return media;
	}

	/**
	 * @param media the media to set
	 */
	public void setMedia(List<SharedMedia> media) {
		this.media = media;
	}

	/**
	 * @return the createDate
	 */
	public LocalDateTime getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	
}
