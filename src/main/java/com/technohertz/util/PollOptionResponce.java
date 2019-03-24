package com.technohertz.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.technohertz.model.MediaFiles;
import com.technohertz.model.PollLikes;
import com.technohertz.model.SecretConversation;
import com.technohertz.model.SharedMedia;
import com.technohertz.model.UserContact;

@Component
public class PollOptionResponce {

	
	
	private Integer optionId;
	
	

	private String optionName;
	

	private Long totalLikes;

	private List<PollLikes> pollLikes;

	/**
	 * @return the optionId
	 */
	public Integer getOptionId() {
		return optionId;
	}

	/**
	 * @param optionId the optionId to set
	 */
	public void setOptionId(Integer optionId) {
		this.optionId = optionId;
	}

	/**
	 * @return the optionName
	 */
	public String getOptionName() {
		return optionName;
	}

	/**
	 * @param optionName the optionName to set
	 */
	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	/**
	 * @return the totalLikes
	 */
	public Long getTotalLikes() {
		return totalLikes;
	}

	/**
	 * @param long1 the totalLikes to set
	 */
	public void setTotalLikes(Long long1) {
		this.totalLikes = long1;
	}

	/**
	 * @return the pollLikes
	 */
	public List<PollLikes> getPollLikes() {
		return pollLikes;
	}

	/**
	 * @param list the pollLikes to set
	 */
	public void setPollLikes(List<PollLikes> list) {
		this.pollLikes = list;
	}

	
	
}
