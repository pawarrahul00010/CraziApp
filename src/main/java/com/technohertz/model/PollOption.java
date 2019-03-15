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
@Table(name = "POLL_OPTION")
@DynamicUpdate
public class PollOption implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "OPTION_ID")
	private Integer optionId;

	@Column(name = "OPTION_NAME")
	private String optionName;
	
	@JsonIgnore
	@OneToMany(cascade = javax.persistence.CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "OPTION_ID")
	private List<PollLikes> pollLikes = new ArrayList<PollLikes>();

	public Integer getOptionId() {
		return optionId;
	}

	public void setOptionId(Integer optionId) {
		this.optionId = optionId;
	}

	public String getOptionName() {
		return optionName;
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	public List<PollLikes> getPollOptions() {
		return pollLikes;
	}

	public void setPollOptions(List<PollLikes> pollOptions) {
		this.pollLikes = pollOptions;
	}

	@Override
	public String toString() {
		return "PollOption [optionId=" + optionId + ", optionName=" + optionName + ", pollLikes=" + pollLikes + "]";
	}


}
