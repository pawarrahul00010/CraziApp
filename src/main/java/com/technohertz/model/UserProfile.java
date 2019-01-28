package com.technohertz.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "User_Profile")
@DynamicUpdate
public class UserProfile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USR_DET_ID")
	private Integer profileId;

	@Column(name = "Display_Name")
	private String displayName;
	@Column(name = "About_User")
	private String aboutUser;

	@OneToMany(cascade=CascadeType.ALL,mappedBy="profile")
	private List<MediaFiles> files=new ArrayList<MediaFiles>();


	@JsonIgnore
	 @JsonBackReference
	@OneToOne
	@PrimaryKeyJoinColumn
	private UserRegister register;
	
	public UserRegister getRegister() {
		return register;
	}

	public void setRegister(UserRegister register) {
		this.register = register;
	}



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

	@Override
	public String toString() {
		return "UserProfile [profileId=" + profileId + ", displayName=" + displayName + ", aboutUser=" + aboutUser
				+ ", files=" + files + ", register=" + register + "]";
	}


	

}
