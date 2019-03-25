package com.technohertz.util;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.technohertz.model.MediaFiles;
import com.technohertz.model.SecretConversation;
import com.technohertz.model.SharedMedia;
import com.technohertz.model.UserContact;

@Component
public class PollResponce {

	
	
	private Integer pollId;
	
	

	private String pollName;
	
	private LocalDateTime pollCreatedDate;
	private LocalDateTime pollExpiryDate;
	/**
	 * @return the pollExpiryDate
	 */
	public LocalDateTime getPollExpiryDate() {
		return pollExpiryDate;
	}

	/**
	 * @param pollExpiryDate the pollExpiryDate to set
	 */
	public void setPollExpiryDate(LocalDateTime pollExpiryDate) {
		this.pollExpiryDate = pollExpiryDate;
	}

	private int createdBy;

	private int groupId;
	
	private List<PollOptionResponce> pollOptions;

	/**
	 * @return the pollId
	 */
	public Integer getPollId() {
		return pollId;
	}

	/**
	 * @param pollId the pollId to set
	 */
	public void setPollId(Integer pollId) {
		this.pollId = pollId;
	}

	/**
	 * @return the pollName
	 */
	public String getPollName() {
		return pollName;
	}

	/**
	 * @param pollName the pollName to set
	 */
	public void setPollName(String pollName) {
		this.pollName = pollName;
	}

	/**
	 * @return the createdBy
	 */
	public int getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the groupId
	 */
	public int getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the pollOptions
	 */
	public List<PollOptionResponce> getPollOptions() {
		return pollOptions;
	}

	/**
	 * @param pollOptions the pollOptions to set
	 */
	public void setPollOptions(List<PollOptionResponce> pollOptions) {
		this.pollOptions = pollOptions;
	}

	/**
	 * @return the pollCreatedDate
	 */
	public LocalDateTime getPollCreatedDate() {
		return pollCreatedDate;
	}

	/**
	 * @param pollCreatedDate the pollCreatedDate to set
	 */
	public void setPollCreatedDate(LocalDateTime pollCreatedDate) {
		this.pollCreatedDate = pollCreatedDate;
	}


	
}
