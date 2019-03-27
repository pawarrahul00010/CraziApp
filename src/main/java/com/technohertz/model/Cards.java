package com.technohertz.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
	
	@Column(name = "EDITABLE")
	private char editable;

	@Column(name = "FILE_PATH")
	private String filePath;

	@Column(name = "CREATE_DATE")
	private LocalDateTime createDate;
	
	@JsonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name ="CATEGORY_ID")
	private CardCategory cardCategory;
	
	@JsonIgnore
	@OneToMany(cascade=javax.persistence.CascadeType.ALL,fetch=FetchType.LAZY)		
	@JoinColumn(name="CARD_ID")
	private List<CardBookmarkUsers> cardBookmarkUserList;
	@JsonIgnore
	@OneToMany(cascade=javax.persistence.CascadeType.ALL,fetch=FetchType.LAZY)		
	@JoinColumn(name="CARD_ID")
	private List<MediaFiles> mediaFiles;

	public Integer getCardId() {
		return CardId;
	}

	public void setCardId(Integer cardId) {
		CardId = cardId;
	}

	public String getCardText() {
		return CardText;
	}

	public void setCardText(String cardText) {
		CardText = cardText;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public CardCategory getCardCategory() {
		return cardCategory;
	}

	public void setCardCategory(CardCategory cardCategory) {
		this.cardCategory = cardCategory;
	}

	public List<CardBookmarkUsers> getCardBookmarkUserList() {
		return cardBookmarkUserList;
	}

	public void setCardBookmarkUserList(List<CardBookmarkUsers> cardBookmarkUserList) {
		this.cardBookmarkUserList = cardBookmarkUserList;
	}

	public char getEditable() {
		return editable;
	}

	public void setEditable(char editable) {
		this.editable = editable;
	}
	

	/**
	 * @return the mediaFiles
	 */
	public List<MediaFiles> getMediaFiles() {
		return mediaFiles;
	}

	/**
	 * @param mediaFiles the mediaFiles to set
	 */
	public void setMediaFiles(List<MediaFiles> mediaFiles) {
		this.mediaFiles = mediaFiles;
	}

	@Override
	public String toString() {
		return "Cards [CardId=" + CardId + ", CardText=" + CardText + ", editable=" + editable + ", filePath="
				+ filePath + ", createDate=" + createDate + ", cardCategory=" + cardCategory + ", cardBookmarkUserList="
				+ cardBookmarkUserList + "]";
	}

}
