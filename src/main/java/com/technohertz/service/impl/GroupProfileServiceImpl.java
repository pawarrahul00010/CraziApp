package com.technohertz.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.technohertz.model.GroupPoll;
import com.technohertz.model.GroupProfile;
import com.technohertz.model.PollOption;
import com.technohertz.model.UserContact;
import com.technohertz.repo.GroupProfileRepository;
import com.technohertz.repo.UserContactRepository;
import com.technohertz.service.IGroupProfileService;
import com.technohertz.util.DateUtil;

@Service
public class GroupProfileServiceImpl implements IGroupProfileService{
	
	
	@Autowired
	private GroupProfileRepository groupProfileRepository;

	
	@Autowired
	private UserContactRepository userContactRepository;
	
	@PersistenceContext
	public EntityManager entityManager;

@Autowired
DateUtil dateUtil;

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
	public void deleteContactsById(Integer groupId, String contactIdList) {
		
		contactIdList.replace("[", "(");
		contactIdList.replace("]", ")");
		System.out.println(contactIdList);
		entityManager.createNativeQuery(" DELETE FROM " + 
				" group_profile_group_member WHERE group_id=:group_id AND contact_id IN (:contact_id)")
				.setParameter("group_id", groupId)
				.setParameter("contact_id", contactIdList).executeUpdate();

	}

	@Override
	public void deleteAdminFromGroupByContact(String mobile_number) {

		entityManager.createQuery("delete from GroupAdmin where mobile_number=:mobile_number").setParameter("mobile_number", mobile_number).executeUpdate();
	}

	@Override
	public void deleteGroupById(Integer groupId) {
		
		groupProfileRepository.deleteById(groupId);
		
	}

	@Override
	public GroupProfile createPoll(String pollName, String createdBy, Integer groupId, String optionNameList,String pollExpiryDate) {
		GroupProfile groupProfile=groupProfileRepository.getOne(groupId);
		GroupPoll groupPoll =new GroupPoll();
		if(!groupProfile.equals(null))
		{
			UserContact phoneNumber=userContactRepository.getOne(Integer.parseInt(createdBy));
			if(!phoneNumber.equals(null))
			{
				groupPoll.setCreatedBy(Integer.parseInt(createdBy));
				groupPoll.setGroupId(groupId);
				groupPoll.setPollName(pollName);
				groupPoll.setCreateDate(dateUtil.getDate());
				String str = pollExpiryDate;
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

				groupPoll.setExpiryDate(dateTime);
				
				List<String> pollOptions = getoptionList(optionNameList);
				List<PollOption> optionsList= new ArrayList<PollOption>();
				if(!pollOptions.isEmpty())
				{
					for(String optionName :pollOptions)
					{
						PollOption option =new PollOption();
						option.setOptionName(optionName);
						option.setTotalLikes(0l);
						optionsList.add(option);
						
					}
					groupPoll.setPollOptions(optionsList);
					groupPoll.setPollStatus("ACTIVE");
				}
				groupProfile.getGroupPolls().add(groupPoll);
			}
		}
		
		return groupProfile;
	}
	private List<String> getoptionList(String optionNameList) {
		// TODO Auto-generated method stub
		List<String> optionList = new ArrayList<String>();
		String options[] = optionNameList.split(",");
		
		for(String userContact : options ) {
		
				optionList.add(userContact);
			}
		return optionList;
	}

	
}
