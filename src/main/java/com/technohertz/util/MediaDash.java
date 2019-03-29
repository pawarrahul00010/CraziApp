package com.technohertz.util;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class MediaDash implements Serializable {

	private static final long serialVersionUID = 1L;

	//private Integer fileId;

	//private List<String> filePathList;

	private Integer fromUser;

	private String toUser;
	
	private Integer toUserId;

	//private String fileName;
	
	//private String fileType;
	
	private String receiverProfile;

	private LocalDateTime shareDate;

	/**
	 * @return the shareDate
	 */
	public LocalDateTime getShareDate() {
		return shareDate;
	}

	/**
	 * @param shareDate the shareDate to set
	 */
	public void setShareDate(LocalDateTime shareDate) {
		this.shareDate = shareDate;
	}


	/**
	 * @return the toUser
	 */
	public String getToUser() {
		return toUser;
	}

	/**
	 * @param toUser the toUser to set
	 */
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public String getReceiverProfile() {
		return receiverProfile;
	}

	public void setReceiverProfile(String receiverProfile) {
		this.receiverProfile = receiverProfile;
	}

	public Integer getFromUser() {
		return fromUser;
	}

	public void setFromUser(Integer fromUser) {
		this.fromUser = fromUser;
	}

	public Integer getToUserId() {
		return toUserId;
	}

	public void setToUserId(Integer toUserId) {
		this.toUserId = toUserId;
	}

}
