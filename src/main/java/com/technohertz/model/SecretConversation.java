package com.technohertz.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.transaction.annotation.Transactional;

@Transactional
@Entity
@Table(name = "secret_conversation")
public class SecretConversation {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "conv_id")
	private Integer convId;

	@Column(name="status")
	private Boolean status;
	
	@Column(name = "secret_id")
	private Integer secretId;

	

	/**
	 * @return the secretId
	 */
	public Integer getSecretId() {
		return secretId;
	}

	/**
	 * @param secretId the secretId to set
	 */
	public void setSecretId(Integer secretId) {
		this.secretId = secretId;
	}

	/**
	 * @return the convId
	 */
	public Integer getConvId() {
		return convId;
	}

	/**
	 * @param convId the convId to set
	 */
	public void setConvId(Integer convId) {
		this.convId = convId;
	}

	/**
	 * @return the status
	 */
	public Boolean getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Boolean status) {
		this.status = status;
	}
	
	
}
