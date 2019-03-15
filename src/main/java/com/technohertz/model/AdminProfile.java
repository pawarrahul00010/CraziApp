package com.technohertz.model;

import java.io.Serializable;
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
@Table(name = "Admin_Profile")
@DynamicUpdate
public class AdminProfile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "admin_ID")
	private Integer profileId;

	@Column(name = "Display_Name")
	private String displayName;

	@Column(name = "current_Profile")
	private String currentProfile;

	@JsonIgnore
	@OneToMany(cascade = javax.persistence.CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_ID")
	private List<MediaFiles> files = new ArrayList<MediaFiles>();

	@OneToMany(cascade = javax.persistence.CascadeType.ALL, mappedBy = "profile")
	private List<CardCategory> cardCategories = new ArrayList<CardCategory>();

	public Integer getProfileId() {
		return profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getCurrentProfile() {
		return currentProfile;
	}

	public void setCurrentProfile(String currentProfile) {
		this.currentProfile = currentProfile;
	}

	public List<MediaFiles> getFiles() {
		return files;
	}

	public void setFiles(List<MediaFiles> files) {
		this.files = files;
	}

	public List<CardCategory> getCardCategories() {
		return cardCategories;
	}

	public void setCardCategories(List<CardCategory> cardCategories) {
		this.cardCategories = cardCategories;
	}

	@Override
	public String toString() {
		return "AdminProfile [profileId=" + profileId + ", displayName=" + displayName + ", currentProfile="
				+ currentProfile + ", files=" + files + ", cardCategories=" + cardCategories + "]";
	}


}
