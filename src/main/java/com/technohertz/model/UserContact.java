package com.technohertz.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import org.hibernate.annotations.ColumnDefault;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.technohertz.util.SecretConvResponce;

@Transactional
@Entity
@Table(name = "USER_CONTACT")
public class UserContact implements Comparable<UserContact>, Serializable{

	

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CONTACT_ID")
	private int contactId;
	
	@Column(name = "CONTACT_NAME")
	private String contactName;

	@Column(name = "CONTACT_NUMBER")
	private String contactNumber;
	
	@Column(name = "profile_Pic")
	private String profilePic;
	
	@ColumnDefault("false")
	@Column(name = "ISBLOCKED")
	private boolean isBlocked;

	@ColumnDefault("false")
	@Column(name = "ISACTIVE")
	private boolean isActive;
	
	@Column(name = "CREATE_DATE", nullable = true, length = 200)
	private LocalDateTime createDate;
	
		@OneToMany(cascade=javax.persistence.CascadeType.ALL)
		@JoinColumn(name="CONTACT_ID")
		private List<SecretConversation> secretConversation=new ArrayList<SecretConversation>();
	
	/*
	 * @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE}) private
	 * List<GroupProfile> groupList = new ArrayList<>();
	 */
	
	//for joing the tables (many-to-many)
	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name = "group_profile_group_member",
	joinColumns = {
	@JoinColumn(name="contact_id") 
	},
	inverseJoinColumns = {
	@JoinColumn(name="group_id")
	}
	)
	private Set<GroupProfile> groupList;
	
	public UserContact() {
		super();
	}


	/**
	 * @param contactId
	 * @param contactName
	 * @param contactNumber
	 * @param profilePic
	 * @param isBlocked
	 * @param isActive
	 * @param createDate
	 * @param groupList
	 */
	public UserContact(int contactId, String contactName, String contactNumber, String profilePic, boolean isBlocked,
			boolean isActive, LocalDateTime createDate) {
		super();
		this.contactId = contactId;
		this.contactName = contactName;
		this.contactNumber = contactNumber;
		this.profilePic = profilePic;
		this.isBlocked = isBlocked;
		this.isActive = isActive;
		this.createDate = createDate;
	}


	/**
	 * @return the contactId
	 */
	public int getContactId() {
		return contactId;
	}

	/**
	 * @param contactId the contactId to set
	 */
	public void setContactId(int contactId) {
		this.contactId = contactId;
	}

	/**
	 * @return the contactName
	 */
	public String getContactName() {
		return contactName;
	}

	/**
	 * @param contactName the contactName to set
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	/**
	 * @return the contactNumber
	 */
	public String isContactNumber() {
		return contactNumber;
	}

	/**
	 * @param contactNumber the contactNumber to set
	 */
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	/**
	 * @return the isBlocked
	 */
	public boolean isBlocked() {
		return isBlocked;
	}

	/**
	 * @param isBlocked the isBlocked to set
	 */
	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
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

	/**
	 * @return the profilePic
	 */
	public String getProfilePic() {
		return profilePic;
	}


	/**
	 * @param profilePic the profilePic to set
	 */
	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}


	/**
	 * @return the contactNumber
	 */
	public String getContactNumber() {
		return contactNumber;
	}




	public Set<GroupProfile> getGroupList() {
		return groupList;
	}


	public void setGroupList(Set<GroupProfile> groupList) {
		this.groupList = groupList;
	}


	/**
	 * @return the secretConversation
	 */
	public List<SecretConversation> getSecretConversation() {
		return secretConversation;
	}


	/**
	 * @param secretConversation the secretConversation to set
	 */
	public void setSecretConversation(List<SecretConversation> secretConversation) {
		this.secretConversation = secretConversation;
	}


	@Override
	public String toString() {
		return "UserContact [contactId=" + contactId + ", contactName=" + contactName + ", contactNumber="
				+ contactNumber + ", profilePic=" + profilePic + ", isBlocked=" + isBlocked + ", isActive=" + isActive
				+ ", createDate=" + createDate + ", groupList=" + groupList + "]";
	}


	public int compareTo(UserContact o) {

		return (int) (o.getContactId()-this.getContactId());
	}



}
