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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Pendora_Box")
@DynamicUpdate
public class PendoraBox implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pendora_id")
	private Integer pendoraId;

	@Column(name = "message_or_file")
	private String messageOrFile;


	@JsonIgnore
	@OneToMany(cascade=javax.persistence.CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name="pendora_id")
	private List<MediaFiles> files=new ArrayList<MediaFiles>();
	
	@JsonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name ="USR_DET_ID")
	private UserProfile userProfile;

	/**
	 * @return the pendoraId
	 */
	public Integer getPendoraId() {
		return pendoraId;
	}


	/**
	 * @param pendoraId the pendoraId to set
	 */
	public void setPendoraId(Integer pendoraId) {
		this.pendoraId = pendoraId;
	}


	/**
	 * @return the messageOrFile
	 */
	public String getMessageOrFile() {
		return messageOrFile;
	}


	/**
	 * @param messageOrFile the messageOrFile to set
	 */
	public void setMessageOrFile(String messageOrFile) {
		this.messageOrFile = messageOrFile;
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
	 * @return the userProfile
	 */
	public UserProfile getUserProfile() {
		return userProfile;
	}


	/**
	 * @param userProfile the userProfile to set
	 */
	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}



}
