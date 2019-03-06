package com.technohertz.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.technohertz.model.GroupProfile;
import com.technohertz.model.UserContact;
import com.technohertz.repo.GroupProfileRepository;
import com.technohertz.service.IGroupProfileService;

@Service
public class GroupProfileServiceImpl implements IGroupProfileService{
	
	
	@Autowired
	private GroupProfileRepository groupProfileRepository;
	
	@PersistenceContext
	public EntityManager entityManager;



	public GroupProfile save(GroupProfile groupProfile) {
		
		return groupProfileRepository.save(groupProfile);
	}

	@Override
	public List<GroupProfile> findById(int groupId) {
		// TODO Auto-generated method stub
		
		return entityManager.createQuery(" SELECT r from GroupProfile r WHERE r.groupId=:groupId",GroupProfile.class)
				.setParameter("groupId", groupId).getResultList();
		
	}

	@Transactional
	@SuppressWarnings("unchecked")
	@Override
	public List<GroupProfile> getUserGroupdetailByUserId(String contactNumber) {
		
		return entityManager.createNativeQuery(
				"SELECT * FROM group_profile WHERE group_id IN"
						+"( SELECT group_id FROM  " 
								+ "group_profile_group_member   WHERE contact_id IN "
								+"(SELECT contact_id FROM user_contact WHERE contact_number=:contactNumber)"
								+ "GROUP BY group_id "
						+" )", GroupProfile.class)
				 .setParameter("contactNumber", contactNumber).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getUserGroupsByContact(String contactNumber) {
		// TODO Auto-generated method stub
		return entityManager.createNativeQuery(" SELECT " + 
													"	g.group_profile_group_id FROM " + 
													"	group_profile_group_member g  INNER JOIN group_profile u"
													+ " ON g.group_member_contact_id IN "
															+ "("
															+ " SELECT contact_id FROM user_contact WHERE contact_number=:contactNumber"
															+ ")"
													+ "GROUP BY g.group_profile_group_id")
				.setParameter("contactNumber", contactNumber).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getUserGroupsByUserId(int userId) {
		
		return entityManager.createNativeQuery(" SELECT display_name FROM group_profile WHERE created_by=:userId")
				.setParameter("userId", userId).getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getGroupContactListById(Integer groupId) {
		
		List<UserContact> userContactList = entityManager.createQuery(" SELECT g.groupMember FROM GroupProfile g WHERE "+
				"(:groupId is null or g.groupId=:groupId)")
				.setParameter("groupId", groupId).getResultList();
		List<String> contactList = new ArrayList<String>();
		
		for(UserContact userContact : userContactList) {
			
			String contact = userContact.getContactNumber();
			
			contactList.add(contact);
		}
		return contactList;
	}

	@Transactional
	@Override
	public void deleteContactsById(Integer groupId, String contactList) {
		
		contactList.replace("[", "(");
		contactList.replace("]", ")");
		System.out.println(contactList);
		entityManager.createNativeQuery(" DELETE FROM " + 
				" group_profile_group_member WHERE group_id=:group_id AND contact_id IN (:contactList)")
				.setParameter("group_id", groupId)
				.setParameter("contactList", contactList).executeUpdate();

	}

}
