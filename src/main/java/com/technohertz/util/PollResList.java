package com.technohertz.util;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.technohertz.model.PollOption;

public class PollResList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer pollId;

	private String pollName;
	
	private String pollStatus;
	
//	private Integer createdBy;

	//private Integer groupId;
	//private String groupName;
	
	private LocalDateTime createDate;
	
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


	@OneToMany(cascade = javax.persistence.CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "POLL_ID")
	private List<PollOption> pollOptions = new ArrayList<PollOption>();


	
	public List<PollOption> getPollOptions() {
		return pollOptions;
	}

	public void setPollOptions(List<PollOption> pollOptions) {
		this.pollOptions = pollOptions;
	}

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

	public String getPollStatus() {
		return pollStatus;
	}

	public void setPollStatus(String pollStatus) {
		this.pollStatus = pollStatus;
	}

	public LocalDateTime getExpiryDate() {
		return ExpiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		ExpiryDate = expiryDate;
	}



}
