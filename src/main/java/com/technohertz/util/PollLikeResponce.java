package com.technohertz.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.technohertz.model.MediaFiles;
import com.technohertz.model.SecretConversation;
import com.technohertz.model.SharedMedia;
import com.technohertz.model.UserContact;

@Component
public class PollLikeResponce {

	
	
	private Integer pollId;
	
	

	private String pollName;
	

	private int createdBy;

	private int groupId;
	
	private PollOptionResponce pollOptionResponce;

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
	 * @return the pollOptionResponce
	 */
	public PollOptionResponce getPollOptionResponce() {
		return pollOptionResponce;
	}

	/**
	 * @param pollOptionResponce the pollOptionResponce to set
	 */
	public void setPollOptionResponce(PollOptionResponce pollOptionResponce) {
		this.pollOptionResponce = pollOptionResponce;
	}

	
}
