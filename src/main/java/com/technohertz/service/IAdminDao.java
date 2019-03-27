package com.technohertz.service;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.technohertz.model.AdminProfile;
import com.technohertz.model.AdminRegister;
import com.technohertz.model.CardCategory;
import com.technohertz.model.UserRegister;



public interface IAdminDao {

	//public List<UserRegister> getUserList(Date date);
	public List<UserRegister> getUserList();
	
	public int deleteUser(int userId);
	public List<UserRegister> getUserList(String valueOf);

	public List<UserRegister> getUserAllList();

	public AdminRegister saveAdmin(String firstName, String lastName, String email, String password);

	public List<AdminRegister> adminLogin(String email, String password);

	//public MediaFiles storeFile(CommonsMultipartFile file);

	public CardCategory storeCards(MultipartFile file, Integer categoryId, String cardText, Character editable);



	List<CardCategory> getAllCategories(String categoryType);

	public List<CardCategory> getAllGreetinsByCategoryId(Integer categoryType);

	public Resource loadFileAsResource(String fileName);

	
	

	
	
	
}
