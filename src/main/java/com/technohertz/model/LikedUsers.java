package com.technohertz.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "Liked_Users")
@DynamicUpdate
public class LikedUsers {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "typeId")
	private Integer typeId;
	
	@Column(name = "user_Name")
	private String userName;

	@Column(name = "type")
	private String markType;

	@Column(name = "userId")
	private int userId;
	
	@ColumnDefault("0")
	@Column(name = "rating")
	private Float rating;

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * @return the markType
	 */
	public String getMarkType() {
		return markType;
	}
	/**
	 * @param markType the markType to set
	 */
	public void setMarkType(String markType) {
		this.markType = markType;
	}
	
	/**
	 * @return the typeId
	 */
	public Integer getTypeId() {
		return typeId;
	}
	/**
	 * @param typeId the typeId to set
	 */
	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}
	
	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	
	/**
	 * @return the rating
	 */
	public Float getRating() {
		return rating;
	}
	/**
	 * @param rating the rating to set
	 */
	public void setRating(Float rating) {
		this.rating = rating;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LikedUsers [typeId=" + typeId + ", userName=" + userName + ", markType=" + markType + ", userId="
				+ userId + ", rating=" + rating + "]";
	}

}
