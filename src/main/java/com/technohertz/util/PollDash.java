package com.technohertz.util;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.technohertz.model.PollOption;

public class PollDash implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//private Integer pollId;

	//private String pollName;
	
	//private String pollStatus;
	
//	private Integer createdBy;

	private Integer groupId;
	private String groupName;
	private String groupProf;
	
	private LocalDateTime createDate;
	
//	private LocalDateTime ExpiryDate;
	
	
	

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

	
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupProf() {
		return groupProf;
	}

	public void setGroupProf(String groupProf) {
		this.groupProf = groupProf;
	}
	


}
