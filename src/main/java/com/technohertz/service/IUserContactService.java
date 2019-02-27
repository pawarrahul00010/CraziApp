package com.technohertz.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.technohertz.model.UserContact;


public interface IUserContactService {
	

	public UserContact save(UserContact userOtp);
	public List<UserContact> saveMultiple(List<UserContact> list);

	/* public void updateOTPByUserId(UserContact userOtp); */
	public void update(UserContact userOtp);
	public List<UserContact> getAll();
	public Page<UserContact> getAll(Specification<UserContact> s,Pageable pageable);
	
	public List<Long> getContactsByUserId(int userId);
	
	
	public List<UserContact> getUserContactdetailByUserId(int userId);
	
	public List<String> getUserContactsByUserId(int userId);
	
	void deleteByUserId(int userId);
	
	public List<UserContact> getContactByMobileNumber(String mobilNumber);
	

}
