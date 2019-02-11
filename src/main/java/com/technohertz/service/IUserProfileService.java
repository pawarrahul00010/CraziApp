package com.technohertz.service;

import java.util.List;

import com.technohertz.model.UserProfile;
import com.technohertz.model.UserRegister;

public interface IUserProfileService {
	
	
	List<UserRegister> findByUserName(String userName);
	
	List<UserProfile> getProfileList();
}
