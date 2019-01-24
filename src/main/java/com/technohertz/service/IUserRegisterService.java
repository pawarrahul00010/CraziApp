package com.technohertz.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.technohertz.model.UserRegister;


public interface IUserRegisterService {
	

	public UserRegister save(UserRegister user);
	public List<UserRegister> saveMultiple(List<UserRegister> list);
	public void update(UserRegister user);
	public void deleteById(int userId);
	
	public UserRegister getOneById(int userId);
	public List<UserRegister> getAll();
	public Page<UserRegister> getAll(Specification<UserRegister> s,Pageable pageable);
	

}
