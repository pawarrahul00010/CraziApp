package com.technohertz.model;

import java.time.LocalDateTime;

public class GetImage {


private String user;
private Integer fileId;
private LocalDateTime createDate;
public String getUser() {
	return user;
}
public void setUser(String user) {
	this.user = user;
}
public Integer getFileId() {
	return fileId;
}
public void setFileId(Integer fileId) {
	this.fileId = fileId;
}
public LocalDateTime getCreateDate() {
	return createDate;
}
public void setCreateDate(LocalDateTime createDate) {
	this.createDate = createDate;
}
@Override
public String toString() {
	return "GetImage [user=" + user + ", fileId=" + fileId + ", createDate=" + createDate + "]";
}


}
