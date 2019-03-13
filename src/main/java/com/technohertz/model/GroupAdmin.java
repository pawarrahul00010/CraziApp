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
@Table(name = "GroupAdmin")
@DynamicUpdate
public class GroupAdmin implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Admin_ID")
	private Integer adminId;

	@Column(name = "Contact_Id")
	private Integer ContactId;

	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}

	public Integer getContactId() {
		return ContactId;
	}

	public void setContactId(Integer contactId) {
		ContactId = contactId;
	}

	@Override
	public String toString() {
		return "GroupAdmin [adminId=" + adminId + ", ContactId=" + ContactId + "]";
	}




	
}
