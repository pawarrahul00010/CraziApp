package com.technohertz.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
@Entity
@Table(name = "CARDS")
@DynamicUpdate
public class Cards implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CARD_ID")
	private Integer CardId;

	@Column(name = "CARD_TEXT")
	private String CardText;

	@Column(name = "FILE_PATH")
	private String filePath;

	@Column(name = "CREATE_DATE")
	private LocalDateTime createDate;
	
	@JsonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name ="CATEGORY_ID")
	private CardCategory cardCategory;

	/**
	 * @return the cardId
	 */
	public Integer getCardId() {
		return CardId;
	}

	/**
	 * @param cardId the cardId to set
	 */
	public void setCardId(Integer cardId) {
		CardId = cardId;
	}

	/**
	 * @return the cardText
	 */
	public String getCardText() {
		return CardText;
	}

	/**
	 * @param cardText the cardText to set
	 */
	public void setCardText(String cardText) {
		CardText = cardText;
	}


	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @return the modifiedDate
	 */
	public LocalDateTime getCreateDate() {
		return createDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return the profile
	 */
	public CardCategory getCardCategory() {
		return cardCategory;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setCardCategory(CardCategory profile) {
		this.cardCategory = profile;
	}


}
