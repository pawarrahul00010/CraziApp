package com.technohertz.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Shared_Media")
@DynamicUpdate
public class SharedMedia implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "file_Id")
	private Integer fileId;

	@Column(name = "File_Path")
	private String filePath;

	@Column(name = "form_User")
	private String formUser;

	@Column(name = "to_User")
	private String toUser;

	@Column(name = "fileName")
	private String fileName;

	@Column(name = "share_date")
	private LocalDateTime shareDate;

	/**
	 * @return the shareDate
	 */
	public LocalDateTime getShareDate() {
		return shareDate;
	}

	/**
	 * @param shareDate the shareDate to set
	 */
	public void setShareDate(LocalDateTime shareDate) {
		this.shareDate = shareDate;
	}

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "USR_DET_ID")
	private UserProfile profile;

	/**
	 * @return the fileId
	 */
	public Integer getFileId() {
		return fileId;
	}

	/**
	 * @param fileId the fileId to set
	 */
	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @return the formUser
	 */
	public String getFormUser() {
		return formUser;
	}

	/**
	 * @param formUser the formUser to set
	 */
	public void setFormUser(String formUser) {
		this.formUser = formUser;
	}

	/**
	 * @return the toUser
	 */
	public String getToUser() {
		return toUser;
	}

	/**
	 * @param toUser the toUser to set
	 */
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	/**
	 * @return the profile
	 */
	@JsonIgnore
	public UserProfile getProfile() {
		return profile;
	}

	/**
	 * @param profile the profile to set
	 */
	@JsonIgnore
	public void setProfile(UserProfile profile) {
		this.profile = profile;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
