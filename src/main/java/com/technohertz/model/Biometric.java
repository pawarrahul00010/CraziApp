package com.technohertz.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
@Entity
@Table(name = "Biometric_Table")
@DynamicUpdate
public class Biometric implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Biometric_ID")
	private Integer biometricId;
	@Column(name = "Biometric_Images")
	private String biometricImage;
	@Column(name = "Status")
	private Boolean isActive;
	@Column(name = "Biometric_Create_Date")
	private LocalDateTime createDate;
	
	@Column(name = "Biometric_Last_Modified_Date")
	private LocalDateTime lastModifiedDate;

	public Integer getBiometricId() {
		return biometricId;
	}

	public void setBiometricId(Integer biometricId) {
		this.biometricId = biometricId;
	}

	public String getBiometricImage() {
		return biometricImage;
	}

	public void setBiometricImage(String biometricImage) {
		this.biometricImage = biometricImage;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Biometric [biometricId=" + biometricId + ", biometricImage=" + biometricImage + ", isActive=" + isActive
				+ ", createDate=" + createDate + ", lastModifiedDate=" + lastModifiedDate + "]";
	}


	
}
