package com.technohertz.service.impl;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Override 
	public UserRegister getOneById(int userId) { return
			userRegisterRepo.getOne(userId); }

	@Override 
	public List<UserRegister> getAll() { 
		List<UserRegister> list=userRegisterRepo.findAll(); 

		return list; 
	}

	@Override 
	public Page<UserRegister> getAll(Specification<UserRegister> s, Pageable pageable)
		{ 
			Page<UserRegister> page=userRegisterRepo.findAll(s, pageable); 
			
			return page; 
		}




}
