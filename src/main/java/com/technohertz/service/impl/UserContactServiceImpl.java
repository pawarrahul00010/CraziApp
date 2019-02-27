package com.technohertz.service.impl;



import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.technohertz.model.UserContact;
import com.technohertz.repo.UserContactRepository;
import com.technohertz.service.IUserContactService;

@Service
public class UserContactServiceImpl implements IUserContactService {

	@Autowired
	private UserContactRepository userContactRepo;
	
	@PersistenceContext
	public EntityManager entityManager;

	@Override 
	public UserContact save(UserContact userContact) { 
		userContact.setCreateDate(userContact.getCreateDate()); 
		return userContactRepo.save(userContact); 
	}

	@Override 
	public List<UserContact> saveMultiple(List<UserContact> list) {
		return userContactRepo.saveAll(list);
		}

	@Override 
	public void update(UserContact userContact) { 
		userContact.setCreateDate(userContact.getCreateDate());
		userContactRepo.save(userContact);
	}

	@Override 
	public List<UserContact> getAll() { 
		List<UserContact> list=userContactRepo.findAll(); 

		return list; 
	}

	@Override 
	public Page<UserContact> getAll(Specification<UserContact> s, Pageable pageable)
		{ 
			Page<UserContact> page=userContactRepo.findAll(s, pageable); 
			
			return page; 
		}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getContactsByUserId(int userId) {
		// TODO Auto-generated method stub
		
	/*	return entityManager.createQuery("SELECT r.userContactList from UserRegister r WHERE "+
				"(:userId is null or r.userId=:userId)")
				.setParameter("userId", userId)
				.getResultList();
*/		
		return entityManager.createQuery("select ven.contactNumber from UserRegister itm inner join itm.userContactList ven where " +
				"(:userId is null or itm.userId=:userId)")
				.setParameter("userId", userId)
				.getResultList();
				
		
	}
	
	
	@Transactional
	@Override
	public void deleteByUserId(int userId) {
		
		 entityManager.createNativeQuery("DELETE from user_contact where userId=:userId")
		 .setParameter("userId", userId).executeUpdate();
		 
	}
	
	@Transactional
	@SuppressWarnings("unchecked")
	@Override
	public List<UserContact> getUserContactdetailByUserId(int userId) {
		
		return entityManager.createNativeQuery("select * from user_contact where userId=:userId", UserContact.class)
				 .setParameter("userId", userId).getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getUserContactsByUserId(int userId) {
		// TODO Auto-generated method stub
		return entityManager.createNativeQuery("select contact_number from user_contact where userId=:userId")
				 .setParameter("userId", userId).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserContact> getContactByMobileNumber(String mobilNumber) {
		
		return entityManager.createQuery("select itm from UserContact itm  where " +
				" itm.contactNumber=:contactNumber")
				.setParameter("contactNumber", mobilNumber)
				.getResultList();
	}
	



}
