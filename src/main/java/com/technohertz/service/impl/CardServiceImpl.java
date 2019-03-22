package com.technohertz.service.impl;



import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.technohertz.model.CardBookmarkUsers;
import com.technohertz.model.Cards;
import com.technohertz.model.MediaFiles;
import com.technohertz.repo.CardRepository;
import com.technohertz.service.ICardService;

@Service
public class CardServiceImpl implements ICardService {

	@Autowired
	private CardRepository cardRepo;
	
	@PersistenceContext
	public EntityManager entityManager;

	@Override
	public Cards getById(Integer cardId) {
		// TODO Auto-generated method stub
		return cardRepo.getOne(cardId);
	}

	@Override
	public void save(Cards cards) {
		
		cardRepo.save(cards);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CardBookmarkUsers> getUserBookmarkByCardId(Integer cardId, Integer userId, String cardType) {
		
			return entityManager.createNativeQuery("select * FROM card_bookmark_users WHERE card_id=:cardId AND user_id=:userId AND card_type=:cardType",CardBookmarkUsers.class)
					.setParameter("cardId", cardId)
					.setParameter("cardType", cardType)
					.setParameter("userId", userId).getResultList();
		}

	@SuppressWarnings("unchecked")
	@Override
	public List<Cards> getCardBookmarksByUserId(Integer userId, String cardType) {
		
		return entityManager.createNativeQuery("SELECT * FROM cards WHERE card_id IN (SELECT card_id from card_bookmark_users where user_id=:userId and card_type=:cardType) ",Cards.class)
				.setParameter("cardType", cardType)
				.setParameter("userId", userId).getResultList();
	}


}
