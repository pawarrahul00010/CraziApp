package com.technohertz.service.impl;



import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.technohertz.model.UserRegister;
import com.technohertz.repo.UserRegisterRepository;
import com.technohertz.service.IUserRegisterService;

@Service
public class UserRegisterServiceImpl implements IUserRegisterService {

	@Autowired
	private UserRegisterRepository userRegisterRepo;

	@Autowired
	public EntityManager entityManager;

	@Override 
	public UserRegister save(UserRegister user) { 
		user.setCreateDate(user.getLastModifiedDate()); 
		return userRegisterRepo.save(user); 
	}

	@Override 
	public List<UserRegister> saveMultiple(List<UserRegister> list) {
		return userRegisterRepo.saveAll(list);
		}

	@Override 
	public void update(UserRegister user) { 
		user.setCreateDate(userRegisterRepo.getOne(user.getUserId()) .getCreateDate());
		user.setLastModifiedDate(user.getLastModifiedDate()); 
		userRegisterRepo.save(user);
	}

	@Override 
	public void deleteById(int userId) {
		if(userRegisterRepo.existsById(userId))
			{ 
				userRegisterRepo.deleteById(userId); 
			} 
		}
	//@Cacheable(value="craziCache",key="#userId",unless="#result==null")
	@Override 
	public UserRegister getOneById(int userId) { 
		return
			userRegisterRepo.getOne(userId); }
	//@Cacheable(value="craziCache",key="#userId",unless="#result==null")
	@Override 
	public List<UserRegister> getAll() { 
		List<UserRegister> list=userRegisterRepo.findAll(); 

		return list; 
	}
	//@Cacheable(value="craziCache",key="#userId",unless="#result==null")
	@Override 
	public Page<UserRegister> getAll(Specification<UserRegister> s, Pageable pageable)
		{ 
			Page<UserRegister> page=userRegisterRepo.findAll(s, pageable); 
			
			return page; 
		}

//	@Override
//	public List<UserRegister> findByUserNameAndPassword(String userName, String password) {
//	List<UserRegister> user=userRegisterRepo.findByUserNameAndPassword(userName, password);
//		return user;
//	}
	//@Cacheable(value="userNameCache",key="#userName",unless="#result==null")
	@Override
	public List<UserRegister> findByUserName(String userName) {
		List<UserRegister> userList=userRegisterRepo.findByUserName(userName);
		return userList;
	}
	//@Cacheable(value="mobileNumberCache",key="#user",unless="#result==null")
	@Override
	public List<UserRegister> findByMobileNumber(String user) {
		List<UserRegister> userList=userRegisterRepo.findByMobileNumber(user);
		return userList;
	}

	//@Cacheable(value="craziCache",key="#userId",unless="#result==null")
	@Override
	public List<UserRegister> getById(Integer userId) {
		List<UserRegister> idList=userRegisterRepo.getById(userId);
		return idList;
	}
	//@Cacheable(value="craziCache",key="#mobilNumber,userName",unless="#result==null")
	@SuppressWarnings("unchecked")
	@Override
	public List<UserRegister> findByMobileOrUserName(Long mobilNumber, String userName) {
		
		return entityManager.createQuery("from UserRegister r WHERE "+
				"(:userName is null or r.userName=:userName) and "+
				"(:mobilNumber is null or r.mobilNumber=:mobilNumber)")
				.setParameter("userName", userName)
				.setParameter("mobilNumber", mobilNumber).getResultList();

	}
	

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllMobile() {
		
		return entityManager.createQuery("SELECT r.mobilNumber from UserRegister r ")
				.getResultList();

	}


}
