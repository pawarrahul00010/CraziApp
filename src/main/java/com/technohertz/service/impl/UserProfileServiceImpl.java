package com.technohertz.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.technohertz.model.UserProfile;
import com.technohertz.model.UserRegister;
import com.technohertz.repo.UserProfileRepository;
import com.technohertz.service.IUserProfileService;
@Service
public class UserProfileServiceImpl implements IUserProfileService{
	
	@Autowired
	private UserProfileRepository userprofilerepo;


	@Override
	public List<UserProfile> getProfileList() {

		return userprofilerepo.findAll();
	}

	
}
