package com.technohertz.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.technohertz.model.LikedUsers;
import com.technohertz.model.MediaFiles;
import com.technohertz.model.UserContact;
import com.technohertz.model.UserRegister;
import com.technohertz.service.IUserRegisterService;

@Component
public class CommonUtil {
	
	private HttpServletRequest request;


	@Autowired
	private IUserRegisterService userRegisterService;

	
	public Map<String, UserRegister> getContactWithDetails(List<String> contactList, List<UserRegister> retrivedUserList){
		
		Map<String, UserRegister> userList = new TreeMap<String, UserRegister>();
		
		for(String contactNumber : contactList){
			
			for(UserRegister userRegister : retrivedUserList) {
					
					if(contactNumber == userRegister.getMobilNumber() || userRegister.getMobilNumber().equals(contactNumber)) {
						
						userList.put(contactNumber, userRegister);
				}
			}
		}
		
		return userList;
	}

		public Map<String, UserContact> getContactProfileDetails(List<String> contactList,
				List<UserContact> retrivedContactList) {
			
			Map<String, UserContact> userContactList = new TreeMap<String, UserContact>();
			
			List<String> conList = getConList(retrivedContactList);
			
			List<String> notCraziUserList = new ArrayList<String>();
			
			
			Map<String, String> profileList = updateProfilePics(conList);
			
			for(String conts: contactList) {
						
				if(conList.contains(conts)) {
						
						for(UserContact userContact : retrivedContactList) {
							
							if(conts == userContact.getContactNumber() || userContact.getContactNumber().equals(conts)) {
								
								userContact.setProfilePic(profileList.get(conts));
								
								userContactList.put(conts, userContact);
						}
						
					}
					
				}else {
					
					notCraziUserList.add(conts);//send groupLink sms via sms gateway 
					
				}
			}
			
			System.out.println("These users are not a craziapp users"+notCraziUserList);
			//request.getSession().setAttribute("nonregistred", notCraziUserList);
			
			return userContactList;
		}

		
		private List<String> getConList(List<UserContact> retrivedContactList) {
			List<String> contactList = new ArrayList<String>();

			for(UserContact userContact : retrivedContactList) {
				
				contactList.add(userContact.getContactNumber());
			}
			return contactList;
		}


		public Map<String, String> updateProfilePics(List<String> contactList){
			
			List<UserRegister> retrivedUserList= userRegisterService.getAll();
			
			Map<String, UserRegister> userList = getContactWithDetails(contactList, retrivedUserList);
			
			Map<String, String> userCurrentProfileList = new TreeMap<String, String>();
		
			for(String contact : contactList) {
				
				if(userList.get(contact).getProfile().getCurrentProfile() == null) {
				
					userCurrentProfileList.put(contact, "https://upload.wikimedia.org/wikipedia/commons/8/89/Portrait_Placeholder.png");
						
				}else {
					
						userCurrentProfileList.put(contact, userList.get(contact).getProfile().getCurrentProfile());
				}
				
			}
			return userCurrentProfileList;
		}
		
		public Double getRating(List<MediaFiles> mediaFileslist) {
			
			Double rate = 0.0d;
			
			int count = 0;
			
			for(MediaFiles mediaFiles : mediaFileslist) {
				
				rate = rate + mediaFiles.getRating();
				count = count + 1;
			}
			rate = rate/count;
			 DecimalFormat df = new DecimalFormat("#.#");
		        System.out.print(df.format(rate));
			return Double.parseDouble(df.format(rate));
		}

		public Float getupdateRating(List<LikedUsers> likedUserlist, Float rateCount, Integer typeId) {
			
			Float rate = 0.0f;
			int count = 0;
			
			for(LikedUsers likedUser : likedUserlist) {
				
				if(likedUser.getTypeId()== typeId) {
					
				}else {
					
					rate = rate + likedUser.getRating();
					
					count = count + 1;
				}
				
			}
			rate = (rate+rateCount)/(count+1);
			
			return rate;
		}

		public Long getLikes(List<MediaFiles> profileList) {
			
			Long likes = 0l;
			
			for(MediaFiles mediaFiles:profileList) {
				
				likes = likes + mediaFiles.getLikes();
				
			}
			
			return likes;
		}
		

		public List<String> getNonCraziUsers(List<String> contactList, List<UserContact> retrivedContactList) {
			// TODO Auto-generated method stub
			List<String> conList = getConList(retrivedContactList);
			List<String> notCraziUserList = new ArrayList<String>();
			
			for(String conts: contactList) {
				
				if(!conList.contains(conts)) {
						
					notCraziUserList.add(conts);//send groupLink sms via sms gateway 
					
				}
			}
			return notCraziUserList;
		}

		
	}
