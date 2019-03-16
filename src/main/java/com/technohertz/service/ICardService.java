package com.technohertz.service;

import java.util.List;

import com.technohertz.model.CardBookmarkUsers;
import com.technohertz.model.Cards;

public interface ICardService {

	Cards getById(Integer cardId);

	void save(Cards cards);

	List<CardBookmarkUsers> getUserBookmarkByCardId(Integer cardId, Integer userId, String cardType);

	List<Cards> getCardBookmarksByUserId(Integer userId, String cardType);
	

}
