package com.technohertz.util;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class MediaDashResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer fileId;

	private String filePath;

	private String fileName;
	
	private String fileType;
	
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


	public Integer getFileId() {
		return fileId;
	}

	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	
}
