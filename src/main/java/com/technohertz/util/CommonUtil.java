package com.technohertz.util;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.technohertz.model.LikedUsers;
import com.technohertz.model.MediaFiles;
import com.technohertz.model.UserContact;
import com.technohertz.model.UserRegister;
import com.technohertz.service.IUserRegisterService;

@Component
public class CommonUtil {


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
			
			Map<String, String> profileList = updateProfilePics(contactList);
			
				for(String contact : contactList) {
					
					for(UserContact userContact : retrivedContactList) {
						
						if(contact == userContact.getContactNumber() || userContact.getContactNumber().equals(contact)) {
							
							userContact.setProfilePic(profileList.get(contact));
							
							userContactList.put(contact, userContact);
					}
					
				}
			
			}
			return userContactList;
		}

		public Map<String, String> updateProfilePics(List<String> contactList){
			
			List<UserRegister> retrivedUserList= userRegisterService.getAll();
			
			Map<String, UserRegister> userList = getContactWithDetails(contactList, retrivedUserList);
			
			Map<String, String> userCurrentProfileList = new TreeMap<String, String>();
		
			for(String contact : contactList) {
				
						userCurrentProfileList.put(contact, userList.get(contact).getProfile().getCurrentProfile());
				}
				
			return userCurrentProfileList;
		}
		
		public Float getRating(List<MediaFiles> mediaFileslist) {
			
			Float rate = 0.0f;
			
			int count = 0;
			
			for(MediaFiles mediaFiles : mediaFileslist) {
				
				rate = rate + mediaFiles.getRating();
				count = count + 1;
			}
			rate = rate/count;
			
			return rate;
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

		
	}
