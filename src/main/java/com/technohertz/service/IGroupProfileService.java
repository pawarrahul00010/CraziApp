package com.technohertz.service;

import java.util.List;

import com.technohertz.model.GroupProfile;
import com.technohertz.model.UserContact;

public interface IGroupProfileService {
	
	public GroupProfile save(GroupProfile groupProfile);

	public List<GroupProfile> findById(int groupId);
	
	List<GroupProfile> getUserGroupdetailByUserId(String userId);

	List<Integer> getUserGroupsByContact(String userId);

	public List<String> getUserGroupsByUserId(int userId);

	public List<String> getGroupContactListById(Integer groupId);

	public void deleteContactsById(Integer groupId, String contacts);

	public void deleteAdminFromGroupByContactId(Integer contactId);

	public void deleteGroupById(Integer groupId);



	GroupProfile createPoll(String pollName, String createdBy, Integer groupId, String optionNameList,
			String pollExpiryDate);

}
