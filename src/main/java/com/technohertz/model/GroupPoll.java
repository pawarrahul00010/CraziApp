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

@Entity
@Table(name = "Group_Poll")
@DynamicUpdate
public class GroupPoll implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "POLL_ID")
	private Integer pollId;

	@Column(name = "POLL_NAME")
	private String pollName;
	
	@Column(name = "poll_status")
	private String pollStatus;
	
	/**
	 * @return the pollStatus
	 */
	public String getPollStatus() {
		return pollStatus;
	}

	/**
	 * @param pollStatus the pollStatus to set
	 */
	public void setPollStatus(String pollStatus) {
		this.pollStatus = pollStatus;
	}

	@Column(name = "created_By", nullable = true, length = 40)
	private Integer createdBy;

	@Column(name = "GROUP_ID")
	private Integer groupId;
	
	@Column(name = "createDate", nullable = true)
	private LocalDateTime createDate;
	
	@Column(name = "expiry_date", nullable = true, length = 200)
	private LocalDateTime ExpiryDate;
	
	
	

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
	 * @return the expiryDate
	 */
	public LocalDateTime getExpiryDate() {
		return ExpiryDate;
	}

	/**
	 * @param expiryDate the expiryDate to set
	 */
	public void setExpiryDate(LocalDateTime expiryDate) {
		ExpiryDate = expiryDate;
	}

	@OneToMany(cascade = javax.persistence.CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "G_POLL_ID")
	private List<PollOption> pollOptions = new ArrayList<PollOption>();

	public Integer getPollId() {
		return pollId;
	}

	public void setPollId(Integer pollId) {
		this.pollId = pollId;
	}

	public String getPollName() {
		return pollName;
	}

	public void setPollName(String pollName) {
		this.pollName = pollName;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	
	public List<PollOption> getPollOptions() {
		return pollOptions;
	}

	public void setPollOptions(List<PollOption> pollOptions) {
		this.pollOptions = pollOptions;
	}

	@Override
	public String toString() {
		return "GroupPoll [pollId=" + pollId + ", pollName=" + pollName + ", createdBy=" + createdBy + ", groupId="
				+ groupId + ", pollOptions=" + pollOptions + "]";
	}


}
