package com.technohertz;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.technohertz.common.Constant;
import com.technohertz.exception.ResourceNotFoundException;
import com.technohertz.model.Empty;
import com.technohertz.model.GroupAdmin;
import com.technohertz.model.GroupPoll;
import com.technohertz.model.GroupProfile;
import com.technohertz.model.LikedUsers;
import com.technohertz.model.MediaFiles;
import com.technohertz.model.PollLikes;
import com.technohertz.model.PollOption;
import com.technohertz.model.UserContact;
import com.technohertz.model.UserRegister;
import com.technohertz.payload.UploadFileResponse;
import com.technohertz.repo.GroupPollRepository;
import com.technohertz.repo.GroupProfileRepository;
import com.technohertz.repo.MediaFileRepo;
import com.technohertz.repo.PollLikesRepository;
import com.technohertz.repo.PolloptionRepository;
import com.technohertz.repo.UserContactRepository;
import com.technohertz.repo.UserRegisterRepository;
import com.technohertz.service.IGroupProfileService;
import com.technohertz.service.IMediaFileService;
import com.technohertz.service.IUserContactService;
import com.technohertz.service.IUserRegisterService;
import com.technohertz.service.impl.FileStorageService;
import com.technohertz.util.CommonUtil;
import com.technohertz.util.GroupFirebase;
import com.technohertz.util.GroupResponse;
import com.technohertz.util.PollOptionResponce;
import com.technohertz.util.PollResponce;
import com.technohertz.util.ResponseObject;
@Transactional
@RestController
@RequestMapping("/groupRest")
public class GroupProfileController extends TimerTask {
	static String FB_BASE_URL="https://craziapp-3c02b.firebaseio.com";
	@Autowired
	private Empty empty;

	@Autowired
	PollLikesRepository pollLikesRepository;
	@Autowired
	private UserContactRepository userContactRepository;
	
	@Autowired
	private GroupResponse groupResponse;
	
	@Autowired
	private PolloptionRepository polloptionRepository ;
	
	@Autowired
	private IUserRegisterService userRegisterService;
	
	@Autowired
	private GroupPollRepository groupPollRepository;
	
	@Autowired
	private GroupProfileRepository groupProfileRepository;
	
	@Autowired
	private IGroupProfileService groupProfileService;

	@Autowired
	PollLikesRepository likesRepository;
	@Autowired
	private IUserContactService userContactService;

	@Autowired
	private IMediaFileService mediaFileService;

	@Autowired
	private UserRegisterRepository registerRepository;
	
	@Autowired
	private UserContactRepository contactRepository;


	@Autowired
	private MediaFileRepo mediaFileRepo;

	@Autowired
	private ResponseObject response;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private Constant constant;

	@Autowired
	private FileStorageService fileStorageService;

	 
		@PostMapping("/create")
		public ResponseEntity<ResponseObject> createGroup(@RequestParam(value ="contactList", required=false) String contacts,
				@RequestParam(value ="file", required=false) MultipartFile file, 
				@RequestParam(value ="groupName", required=false) String groupName,
				@RequestParam(value ="userId", required=false) Integer userId) {

			if(contacts == null ) {
				
				response.setError("1");
				response.setMessage("'contactList' is empty or null please check");
				response.setData(empty);
				response.setStatus("FAIL");
				
				return ResponseEntity.ok(response);
			
			}
			else if (file == null) {

				response.setError("1");
				response.setMessage("'file' is empty or null please check");
				response.setData(empty);
				response.setStatus("FAIL");
				
				return ResponseEntity.ok(response);

			}else if (groupName == null) {
				response.setError("1");
				response.setMessage("'groupName' is empty or null please check");
				response.setData(empty);
				response.setStatus("FAIL");
				
				return ResponseEntity.ok(response);

			}else if (userId == null) {

				response.setError("1");
				response.setMessage("'userId' is empty or null please check");
				response.setData(empty);
				response.setStatus("FAIL");
				
				return ResponseEntity.ok(response);

			} else {
				
				try {
					FirebaseOptions options = new FirebaseOptions.Builder()
							.setCredentials(GoogleCredentials
									.fromStream(new ClassPathResource("/craziapp-3c02b-firebase-adminsdk-rrs6o-3add9ace15.json").getInputStream()))
							.setDatabaseUrl(FB_BASE_URL).build();
					if (FirebaseApp.getApps().isEmpty()) {
						FirebaseApp.initializeApp(options);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				FirebaseDatabase database = FirebaseDatabase.getInstance();

				List<UserContact> retrivedContactList = userContactService.getAll();// get all user from database

				List<UserContact> contactlist = new ArrayList<UserContact>();

				List<String> contactList = getContactList(contacts);

				Map<String, UserContact> contactProfileList = commonUtil.getContactProfileDetails(contactList,
						retrivedContactList);

				GroupProfile groupProfile = new GroupProfile();

				for (String contact : contactList) {

					UserContact userContact = contactProfileList.get(contact);

					contactlist.add(userContact);
				}

				@SuppressWarnings("static-access")
				GroupProfile mediaFiles = fileStorageService.saveGroupProfile(file, userId, constant.GROUPPROFILE);

				groupProfile.setGroupMember(contactlist);
				groupProfile.setCurrentProfile(mediaFiles.getCurrentProfile());
				groupProfile.setDisplayName(groupName);
				groupProfile.setCreatedBy(userId);
				//groupProfile.setCreateDate(System.currentTimeMillis());
				
				
				Set<GroupAdmin> adminList = new HashSet<GroupAdmin>();
				List<UserRegister> userList = userRegisterService.getById(userId);
				UserRegister userRegister = null;
				if(!userList.isEmpty()) {
					userRegister = userList.get(0);
					//if(groupProfile.getAdminSet().contains(contactProfileList.get(userList.get(0).getMobilNumber()))) {
						
						GroupAdmin groupAdmin = new GroupAdmin();
						groupAdmin.setContactId(contactProfileList.get(userList.get(0).getMobilNumber()).getContactId());
						
						adminList.add(groupAdmin);
						groupProfile.setAdminSet(adminList);
					//}
				}

				
				groupProfileService.save(groupProfile);
				//DatabaseReference ref = database.getReference();


				Map<String, Object> groups = new HashMap<>();
				groups.put(groupProfile.getGroupId().toString(), new GroupFirebase( groupProfile.getDisplayName(), userRegister.getUserName(), "+919657070183"));
			
				//ref.child("group").setValueAsync("group", new GroupFirebase(groupProfile.getGroupId(), groupProfile.getDisplayName(), userRegister.getUserName(), groupProfile.getGroupMember()));
				//ref.getDatabase().getReference().push().c.getReferenceFromUrl(FB_BASE_URL).setValueAsync(groups);
				database.getReference().child("groups").updateChildrenAsync(groups);
		
				//database.getReference().child("groups").setValueAsync( new GroupFirebase(1, groupProfile.getDisplayName(), userRegister.getUserName(), "+919657070183"));
				//database.getReference().child("groups").push().setValueAsync(new GroupFirebase(groupProfile.getGroupId()));
				//usersRef.setValueAsync(groups);
				groupResponse.setGroupId(groupProfile.getGroupId());
				groupResponse.setGroupMember(groupProfile.getGroupMember());
				groupResponse.setAboutGroup(groupProfile.getAboutGroup());
				groupResponse.setCreatedBy(groupProfile.getCreatedBy());
				groupResponse.setCurrentProfile(groupProfile.getCurrentProfile());
				groupResponse.setDisplayName(groupProfile.getDisplayName());
				groupResponse.setFiles(groupProfile.getFiles());
				groupResponse.setGroupAdminList(groupProfile.getAdminSet());

				response.setStatus("Success");
				response.setMessage("Group Created successfully");
				response.setError("0");
				response.setData(groupResponse);

				return ResponseEntity.ok(response);

			}

		}
		


	@PostMapping("/make/admin")
	public ResponseEntity<ResponseObject> updateGroupAdmin(@RequestParam(value ="contactId", required=false) Integer contactId,
			@RequestParam(value ="adminContactId", required=false) Integer adminContactId, 
			@RequestParam(value ="groupId", required=false) Integer groupId) {

		if(adminContactId == null ) {
			
			response.setError("1");
			response.setMessage("'adminContactId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
		
		}
		else if(contactId == null ) {
			
			response.setError("1");
			response.setMessage("'contactId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
		
		}
		else if (groupId == null) {

			response.setError("1");
			response.setMessage("'groupId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);

		}else {


			List<GroupProfile> getGroupUserList = groupProfileService.findById(groupId);//get group details

			//List<String> conlist = groupProfileService.getGroupContactListById(groupId);//list of mobile no.

			GroupProfile groupProfile = getGroupUserList.get(0);
			
			List<Integer> adminList = getAdminList(groupProfile.getAdminSet());
			
			if(adminList.contains(adminContactId)) {
				
				for(UserContact userContact : groupProfile.getGroupMember()) {
					
					if(userContact.getContactId() == contactId || contactId.equals(userContact.getContactId())) {
						
						GroupAdmin groupAdmin = new GroupAdmin();
						groupAdmin.setContactId(contactId);
			
						groupProfile.getAdminSet().add(groupAdmin);
					}
				}
				
				groupProfileService.save(groupProfile);

				groupResponse.setGroupId(groupProfile.getGroupId());
				groupResponse.setGroupMember(groupProfile.getGroupMember());
				groupResponse.setAboutGroup(groupProfile.getAboutGroup());
				groupResponse.setCreatedBy(groupProfile.getCreatedBy());
				groupResponse.setCurrentProfile(groupProfile.getCurrentProfile());
				groupResponse.setDisplayName(groupProfile.getDisplayName());
				groupResponse.setFiles(groupProfile.getFiles());
				groupResponse.setGroupAdminList(groupProfile.getAdminSet());

				response.setStatus("Success");
				response.setMessage("Contact made as admin successfully");
				response.setError("0");
				response.setData(groupResponse);

				return ResponseEntity.ok(response);
				
			}else {
				
				response.setStatus("Fail");
				response.setMessage("Sorry You are not a admin please be admin first.");
				response.setError("0");
				response.setData(empty);

				return ResponseEntity.ok(response);
				
			}


		}

	}

	private List<Integer> getAdminList(Set<GroupAdmin> adminSet) {

		List<Integer> adminList = new ArrayList<Integer>();
		for(GroupAdmin groupAdmin : adminSet) {
			
			adminList.add(groupAdmin.getContactId());
		}
		return adminList;
	}

	@PostMapping("/update/contact")
	public ResponseEntity<ResponseObject> updateGroup(@RequestParam(value ="contactNumber", required=false) String contact,
			@RequestParam(value ="adminContactId", required=false) Integer adminContactId, 
			@RequestParam(value ="groupId", required=false) Integer groupId) {


		if(adminContactId == null ) {
			
			response.setError("1");
			response.setMessage("'adminContactId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
		
		}
		else if(contact == null ) {
			
			response.setError("1");
			response.setMessage("'contactid' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
		
		}
		else if (groupId == null) {

			response.setError("1");
			response.setMessage("'groupId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);

		}else {
			
			List<GroupProfile> getGroupUserList = groupProfileService.findById(groupId);//get group details

			//List<String> conlist = groupProfileService.getGroupContactListById(groupId);//list of mobile no.

			GroupProfile groupProfile = getGroupUserList.get(0);
			List<String> contactsList = getContactsList(groupProfile.getGroupMember());//exist contact list
			List<Integer> adminList = getAdminList(groupProfile.getAdminSet());
			
			if(adminList.contains(adminContactId)) {
				
				if(!contactsList.contains(contact)) {
				
					List<UserContact> retrivedContactList = userContactService.getAll();// get all user from database
		
					List<String> contactList = getContactList(contact);
		
					Map<String, UserContact> contactProfileList = commonUtil.getContactProfileDetails(contactList,
							retrivedContactList);
		
					List<String> conlist = groupProfileService.getGroupContactListById(groupId);
		
					List<UserContact> contactlist = getContactListTosave(contactList, conlist, contactProfileList,
							groupProfile);
		
					groupProfile.setGroupMember(contactlist);
					groupProfile.setGroupId(groupId);
		
					groupProfileService.save(groupProfile);
		
					groupResponse.setGroupId(groupProfile.getGroupId());
					groupResponse.setGroupMember(groupProfile.getGroupMember());
					groupResponse.setAboutGroup(groupProfile.getAboutGroup());
					groupResponse.setCreatedBy(groupProfile.getCreatedBy());
					groupResponse.setCurrentProfile(groupProfile.getCurrentProfile());
					groupResponse.setDisplayName(groupProfile.getDisplayName());
					groupResponse.setFiles(groupProfile.getFiles());
					groupResponse.setGroupAdminList(groupProfile.getAdminSet());
		
					response.setStatus("Success");
					response.setMessage("Contact added in Group successfully");
					response.setError("0");
					response.setData(groupResponse);
		
					return ResponseEntity.ok(response);
	
			}else {
				response.setStatus("Fail");
				response.setMessage("Sorry contact already exist.");
				response.setError("1");
				response.setData(empty);

				return ResponseEntity.ok(response);
			}
		}else {
			response.setStatus("Fail");
			response.setMessage("Sorry You are not a admin please be admin first.");
			response.setError("1");
			response.setData(empty);

			return ResponseEntity.ok(response);
		}
	}
	}
	private List<String> getContactsList(List<UserContact> groupMember) {

		List<String> contactList = new ArrayList<String>();
		for(UserContact userContact : groupMember) {
			contactList.add(userContact.getContactNumber());
		}
		return contactList;
	}


	@PostMapping("/make/normal")
	public ResponseEntity<ResponseObject> makeNormalUser(@RequestParam(value ="contactId", required=false) Integer contactId,
			@RequestParam(value ="adminContactId", required=false) Integer adminContactId, 
			@RequestParam(value ="groupId", required=false) Integer groupId) {

		if(adminContactId == null ) {
			
			response.setError("1");
			response.setMessage("'adminContactId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
		
		}
		else if(contactId == null ) {
			
			response.setError("1");
			response.setMessage("'contactId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
		
		}
		else if (groupId == null) {

			response.setError("1");
			response.setMessage("'groupId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);

		}else {


			List<GroupProfile> getGroupUserList = groupProfileService.findById(groupId);//get group details

			//List<String> conlist = groupProfileService.getGroupContactListById(groupId);//list of mobile no.

			GroupProfile groupProfile = getGroupUserList.get(0);
			
			List<Integer> adminList = getAdminList(groupProfile.getAdminSet());
			
			if(adminList.contains(adminContactId)) {
				
				groupProfileService.deleteAdminFromGroupByContactId(contactId);
				
				groupResponse.setGroupId(groupProfile.getGroupId());
				groupResponse.setGroupMember(groupProfile.getGroupMember());
				groupResponse.setAboutGroup(groupProfile.getAboutGroup());
				groupResponse.setCreatedBy(groupProfile.getCreatedBy());
				groupResponse.setCurrentProfile(groupProfile.getCurrentProfile());
				groupResponse.setDisplayName(groupProfile.getDisplayName());
				groupResponse.setFiles(groupProfile.getFiles());
				groupResponse.setGroupAdminList(groupProfile.getAdminSet());

				response.setStatus("Success");
				response.setMessage("Contact made as normal user successfully");
				response.setError("0");
				response.setData(groupResponse);

				return ResponseEntity.ok(response);
				
			}else {
				
				response.setStatus("Fail");
				response.setMessage("Sorry You are not a admin please be admin first.");
				response.setError("0");
				response.setData(empty);

				return ResponseEntity.ok(response);
				
			}


		}

	}

	private List<UserContact> getContactListTosave(List<String> contactList, List<String> conlist,
			Map<String, UserContact> contactProfileList, GroupProfile groupProfile) {

		for (String contact : contactList) {

			if (!conlist.contains(contact)) {

				UserContact userContact = contactProfileList.get(contact);

				groupProfile.getGroupMember().add(userContact);
			}
		}

		return groupProfile.getGroupMember();
	}

	@PostMapping("/delete/contact")
	public ResponseEntity<ResponseObject> deleteContactFromGroup(@RequestParam(value ="contactid", required=false) Integer contactid,
			@RequestParam(value ="adminContactId", required=false) Integer adminContactId, 
			@RequestParam(value ="groupId", required=false) Integer groupId) {

		if(contactid == null ) {
			
			response.setError("1");
			response.setMessage("'contactid' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
		
		}
		else if (groupId == null) {

			response.setError("1");
			response.setMessage("'groupId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);

		}else {

			List<GroupProfile> getGroupUserList = groupProfileService.findById(groupId);//get group details

			//List<String> conlist = groupProfileService.getGroupContactListById(groupId);//list of mobile no.

			GroupProfile groupProfile = getGroupUserList.get(0);

			List<Integer> adminList = getAdminList(groupProfile.getAdminSet());
			
			if(adminList.contains(adminContactId)) {
				if(adminList.contains(contactid)) {
					
					groupProfileService.deleteAdminFromGroupByContactId(contactid);
				}
				
				groupProfileService.deleteContactsById(groupId, String.valueOf(contactid));
	
				groupResponse.setGroupId(groupProfile.getGroupId());
				groupResponse.setGroupMember(groupProfile.getGroupMember());
				groupResponse.setAboutGroup(groupProfile.getAboutGroup());
				groupResponse.setCreatedBy(groupProfile.getCreatedBy());
				groupResponse.setCurrentProfile(groupProfile.getCurrentProfile());
				groupResponse.setDisplayName(groupProfile.getDisplayName());
				groupResponse.setFiles(groupProfile.getFiles());
				groupResponse.setGroupAdminList(groupProfile.getAdminSet());
	
				response.setStatus("Success");
				response.setMessage("contact removed from Group successfully");
				response.setError("0");
				response.setData(groupResponse);
	
				return ResponseEntity.ok(response);

			}else {
	
				response.setStatus("Fail");
				response.setMessage("Sorry You are not a admin please be admin first.");
				response.setError("0");
				response.setData(empty);
	
				return ResponseEntity.ok(response);
			}
		}
	}

	@PostMapping("/leave/group")
	public ResponseEntity<ResponseObject> leaveFromGroup(@RequestParam(value ="contactid", required=false) String contactid,
			@RequestParam(value ="groupId", required=false) Integer groupId) {

		if(contactid == null ) {
			
			response.setError("1");
			response.setMessage("'contactid' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
		
		}
		else if (groupId == null) {

			response.setError("1");
			response.setMessage("'groupId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);

		}else {

			List<GroupProfile> getGroupUserList = groupProfileService.findById(groupId);//get group details

			//List<String> conlist = groupProfileService.getGroupContactListById(groupId);//list of mobile no.

			GroupProfile groupProfile = getGroupUserList.get(0);

			List<Integer> adminList = getAdminList(groupProfile.getAdminSet());
			int contactId = Integer.parseInt(contactid);
			
			if(adminList.contains(contactId)) {
				
				if(adminList.size()==1 ) {
					
					if(groupProfile.getGroupMember().size()==1) {
						
						groupProfileService.deleteGroupById(groupId);
						groupProfileService.deleteAdminFromGroupByContactId(contactId);
						
						response.setStatus("Success");
						response.setMessage("contact removed from Group successfully");
						response.setError("0");
						response.setData(empty);
			
						return ResponseEntity.ok(response);
						
					}
					else {
							
						UserContact userContact = groupProfile.getGroupMember().get(0);
							
						GroupAdmin groupAdmin = new GroupAdmin();
						groupAdmin.setContactId(userContact.getContactId());
						
						groupProfile.getAdminSet().add(groupAdmin);
						groupProfileService.deleteContactsById(groupId, contactid);
					
					}
				}
				
			}else{
				UserContact userContact = groupProfile.getGroupMember().get(0);
				
				GroupAdmin groupAdmin = new GroupAdmin();
				groupAdmin.setContactId(userContact.getContactId());
				
				groupProfile.getAdminSet().add(groupAdmin);
				groupProfileService.deleteContactsById(groupId, contactid);
				
				
			}
			
			groupResponse.setGroupId(groupProfile.getGroupId());
			groupResponse.setGroupMember(groupProfile.getGroupMember());
			groupResponse.setAboutGroup(groupProfile.getAboutGroup());
			groupResponse.setCreatedBy(groupProfile.getCreatedBy());
			groupResponse.setCurrentProfile(groupProfile.getCurrentProfile());
			groupResponse.setDisplayName(groupProfile.getDisplayName());
			groupResponse.setFiles(groupProfile.getFiles());
			groupResponse.setGroupAdminList(groupProfile.getAdminSet());
			
			response.setStatus("Success");
			response.setMessage("contact removed from Group successfully");
			response.setError("0");
			response.setData(groupResponse);
			
			return ResponseEntity.ok(response);
		}
	}
	
	@SuppressWarnings("unused")
	@PostMapping("/displayName/{groupId}")
	public ResponseEntity<ResponseObject> updateDisplayName(@RequestParam(value ="displayName", required=false) String displayName,
			@PathVariable(value = "groupId", required=false) String profileid) throws ResourceNotFoundException {

		if(displayName == null ) {
			
			response.setError("1");
			response.setMessage("'displayName' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
		
		}
		else if (profileid == null) {

			response.setError("1");
			response.setMessage("'groupId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);

		}else {


			int id = 0;
			try {
				id = Integer.parseInt(profileid);
			} catch (NumberFormatException e) {

				response.setError("1");
				response.setMessage("wrong profileId please enter numeric value");
				response.setData(empty);
				response.setStatus("FAIL");
				return ResponseEntity.ok(response);

			}

			List<GroupProfile> GroupProfileList = null;
			GroupProfile updateDisplayName = null;

			GroupProfileList = groupProfileService.findById(id);
			if (!GroupProfileList.isEmpty()) {

				GroupProfile groupProfile = GroupProfileList.get(0);
				groupProfile.setDisplayName(displayName);
				groupProfile.setCurrentProfile(groupProfile.getFiles().get(0).getFilePath());
				System.out.println(groupProfile.getCreatedBy());
				groupProfile.setCreatedBy(GroupProfileList.get(0).getCreatedBy());
				groupProfile.setAboutGroup(GroupProfileList.get(0).getAboutGroup());
				groupProfile.setGroupId(id);
				updateDisplayName = groupProfileService.save(groupProfile);

				groupResponse.setGroupId(groupProfile.getGroupId());
				groupResponse.setGroupMember(groupProfile.getGroupMember());
				groupResponse.setAboutGroup(groupProfile.getAboutGroup());
				groupResponse.setCreatedBy(groupProfile.getCreatedBy());
				groupResponse.setCurrentProfile(groupProfile.getCurrentProfile());
				groupResponse.setDisplayName(groupProfile.getDisplayName());
				groupResponse.setFiles(groupProfile.getFiles());
				groupResponse.setGroupAdminList(groupProfile.getAdminSet());

				response.setMessage("your Display name updated successfully");
				response.setData(groupResponse);
				response.setError("");
				response.setStatus("success");

				return ResponseEntity.ok(response);
			} else {
				response.setMessage("user not available");
				response.setData(empty);
				response.setError("1");
				response.setStatus("fail");
				return ResponseEntity.ok(response);
			}
		}
	}

	@PostMapping("/like")
	public ResponseEntity<ResponseObject> totalLikes(@RequestParam(value ="fileid", required=false) Integer fileid,
			@RequestParam(value ="isLiked", required=false) Boolean isLiked, 
			@RequestParam(value = "userId", required=false) Integer userId) {

		
		if(fileid == null ) {
			
			response.setError("1");
			response.setMessage("'fileid' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
		
		}
		else if (isLiked == null) {

			response.setError("1");
			response.setMessage("'isLiked' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);

		}else if (userId == null) {

			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);

		}else {
			
			MediaFiles mediaFiles = mediaFileRepo.getById(fileid);
			UserRegister userRegister = registerRepository.getOne(userId);
			List<LikedUsers> likedUsersList = mediaFileService.getUserLikesByFileId(fileid, userId);
	
			long count = 0;
	
			if (likedUsersList.isEmpty()) {
				count = mediaFiles.getLikes();
				LikedUsers likedUsers = new LikedUsers();
				likedUsers.setUserName(userRegister.getUserName());
				likedUsers.setMarkType(Constant.LIKE);
				likedUsers.setUserId(userId);
				mediaFiles.setLikes(count + 1);
				mediaFiles.setIsLiked(true);
				mediaFiles.getLikedUsers().add(likedUsers);
				mediaFileRepo.save(mediaFiles);
	
				response.setError("0");
				response.setMessage("user liked successfully");
				response.setData(mediaFiles);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);
	
			} else {
	
				count=mediaFiles.getLikes();
				
				LikedUsers likedUsers = likedUsersList.get(0);
			
				fileStorageService.deleteLike(likedUsers);
				
				mediaFiles.setLikes(count-1);
				
				if(count<=0) {
					
					mediaFiles.setIsLiked(false);
					
					}
				
				mediaFileRepo.save(mediaFiles);
				
				response.setError("0");
				response.setMessage("user unliked successfully");
				response.setData(mediaFiles);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);
	
				
			}
	
		}
	}
	
	@SuppressWarnings("unused")
	@PostMapping("/rating")
	public ResponseEntity<ResponseObject> totalRating(@RequestParam(value ="fileid", required=false) String userfileid,
			@RequestParam(value ="isRated", required=false) String isRated, 
			@RequestParam(value ="rateCount", required=false) String rateCounts,
			@RequestParam(value = "userId", required=false) Integer userId) {

		if(isRated == null) {
			
			response.setError("1");
			response.setMessage("'isRated' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(rateCounts == null) {
			
			response.setError("1");
			response.setMessage("'rateCount' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(userfileid == null) {
			
			response.setError("1");
			response.setMessage("'fileid' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(userId == null) {
			
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
		
			int cRate = Integer.parseInt(rateCounts);

			int fileid = 0;
			Float rateCount = 0.0f;
			boolean isRate = false;
			try {
				isRate = Boolean.parseBoolean(isRated);
				fileid = Integer.parseInt(userfileid);
				rateCount = Float.parseFloat(rateCounts);
			} catch (Exception e) {

				response.setError("1");
				response.setMessage("wrong fileid and rateCount please enter numeric value");
				response.setData(empty);
				response.setStatus("FAIL");
				return ResponseEntity.ok(response);

			}
			UserRegister userRegister = registerRepository.getOne(userId);
			
			
			
			List<LikedUsers> likedUsersList= mediaFileService.getUserRatingByFileId(fileid, userId);

			MediaFiles mediaFiles= mediaFileRepo.getById(fileid);
			
			List<LikedUsers> likedUserlist = mediaFileService.getRatingByFileId(fileid);
			
			Float rating = 0.0f;
			
			if(rating == null || rating == 0) {

				rating=0.0f;

			} 

			if(likedUsersList.isEmpty()) {
				
				rating = commonUtil.getupdateRating(likedUserlist, rateCount, 0);
				
				LikedUsers likedUsers=new LikedUsers();
				likedUsers.setUserName(userRegister.getUserName());
				likedUsers.setMarkType(Constant.RATE);
				likedUsers.setUserId(userId);
				likedUsers.setRating(rateCount);
				likedUsers.setTypeId(0);
				
				mediaFiles.setRating(rating);
				mediaFiles.setIsRated(true);
				mediaFiles.getLikedUsers().add(likedUsers); 
				mediaFileRepo.save(mediaFiles);

				response.setError("0");
				response.setMessage("user rated with : "+cRate);
				response.setData(mediaFiles);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);

			}else {
				
				LikedUsers likedUsers=likedUsersList.get(0);
				
				rating = commonUtil.getupdateRating(likedUserlist, rateCount, likedUsers.getTypeId());
				
				likedUsers.setUserName(userRegister.getUserName());
				likedUsers.setMarkType(Constant.RATE);
				likedUsers.setUserId(userId);
				likedUsers.setRating(rateCount);
				mediaFiles.getLikedUsers().add(likedUsers); 
							

				mediaFiles.setIsRated(true);
				mediaFiles.setRating(rating);
				mediaFileRepo.save(mediaFiles);

				response.setError("0");
				response.setMessage("rating updated with "+cRate);
				response.setData(mediaFiles);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);
			}
		}

	}
	@SuppressWarnings("unused")
	@PutMapping("/aboutUs/{id}")
	public ResponseEntity<ResponseObject> updateStatus(@RequestParam(value ="aboutUs", required=false) String aboutUs,
			@PathVariable(value = "id", required=false) String userid) throws ResourceNotFoundException {

		if(aboutUs == null ) {
			
			response.setError("1");
			response.setMessage("'aboutUs' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
		
		}
		else if (userid == null) {

			response.setError("1");
			response.setMessage("'id' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);

		}else {

			int id = 0;
			int rateCount = 0;
			boolean isRated = false;
			try {
				id = Integer.parseInt(userid);
			} catch (NumberFormatException e) {

				response.setError("1");
				response.setMessage("wrong fileid and rateCount please enter numeric value");
				response.setData(empty);
				response.setStatus("FAIL");
				return ResponseEntity.ok(response);

			}

			List<GroupProfile> profile = null;
			GroupProfile updateDisplayName = null;

			profile = groupProfileService.findById(id);
			if (!profile.isEmpty()) {
				profile.get(0).setAboutGroup(aboutUs);
				profile.get(0).setGroupId(id);
				updateDisplayName = groupProfileService.save(profile.get(0));

				response.setMessage("your status updated successfully");
				response.setData(updateDisplayName);
				response.setError("0");
				response.setStatus("success");

				return ResponseEntity.ok(response);

			} else {

				response.setMessage("user not available");
				response.setData(empty);
				response.setError("1");
				response.setStatus("fail");
				return ResponseEntity.ok(response);
			}
		}
	}

	@PutMapping("/uploadFile/{userId}")
	public ResponseEntity<ResponseObject> updateProfile(@RequestParam(value ="file", required=false) MultipartFile file,
			@PathVariable(value = "userId", required=false) Integer userId) {
		
		if(file == null ) {
			
			response.setError("1");
			response.setMessage("'file' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
		
		}
		else if (userId == null) {

			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);

		}else {
		
			GroupProfile groupProfile = fileStorageService.savegroupProfile(file, userId);
			MediaFiles files = mediaFileRepo.getOne(Integer
					.valueOf(String.valueOf(groupProfile.getFiles().get(groupProfile.getFiles().size() - 1).getFileId())));
			System.out.println(files);

			Object obj = new UploadFileResponse(groupProfile.getCurrentProfile(), groupProfile.getCurrentProfile(),
					file.getContentType(), file.getSize());
	
			if (!file.isEmpty()) {
				response.setMessage("your Profile Image updated successfully");
	
				response.setData(obj);
				response.setError("0");
				response.setStatus("success");
	
				return ResponseEntity.ok(response);
			} else {
				response.setMessage("your Profile Image not updated");
	
				response.setData(empty);
				response.setError("1");
				response.setStatus("fail");
	
				return ResponseEntity.ok(response);
			}
		}
	}
	private List<String> getContactList(String userContactList) {

		List<String> contactList = new ArrayList<String>();
		String sContact[] = userContactList.split(",");

		for (String userContact : sContact) {

			try {
				contactList.add(userContact);
			} catch (NumberFormatException e) {
				continue;
			}

		}
		return contactList;
	}

	private List<Integer> getContactIdList(String userContactList) {

		List<Integer> contactList = new ArrayList<Integer>();
		String sContact[] = userContactList.split(",");

		for (String userContact : sContact) {

			try {
				contactList.add(Integer.parseInt(userContact));
			} catch (NumberFormatException e) {
				continue;
			}

		}
		return contactList;
	}
	
	
	@PostMapping("/createpoll")
	public ResponseEntity<ResponseObject> createPoll(@RequestParam("pollName") String pollName,
													@RequestParam("createdBy")	String createdBy,
													@RequestParam("groupId")	Integer groupId,
													@RequestParam("optionNameList")	String optionNameList,
													@RequestParam("pollExpiryDate")	String pollExpiryDate
																				) 
	{
		
		GroupProfile groupProfile =groupProfileService.createPoll(pollName,createdBy,groupId,optionNameList,pollExpiryDate);
		groupProfileRepository.save(groupProfile);
		PollResponce pollResponce =new  PollResponce();
		pollResponce.setPollId(groupProfile.getGroupPolls().get(groupProfile.getGroupPolls().size()-1).getPollId());
		pollResponce.setPollName(groupProfile.getGroupPolls().get(groupProfile.getGroupPolls().size()-1).getPollName());
		pollResponce.setCreatedBy(Integer.parseInt(createdBy));
		pollResponce.setPollCreatedDate(groupProfile.getGroupPolls().get(0).getCreateDate());
		pollResponce.setPollExpiryDate(groupProfile.getGroupPolls().get(0).getExpiryDate());
		pollResponce.setGroupId(groupProfile.getGroupId());
		
		List<PollOptionResponce> pollOptionResponces= new ArrayList<PollOptionResponce>();
		
			List<PollOption> pollOptions= groupProfile.getGroupPolls().get(groupProfile.getGroupPolls().size()-1).getPollOptions();
		for(PollOption option : pollOptions) {
			PollOptionResponce pollOptionResponce = new PollOptionResponce();
			pollOptionResponce.setOptionId(option.getOptionId());
		pollOptionResponce.setOptionName(option.getOptionName());
		pollOptionResponce.setTotalLikes(option.getTotalLikes()	);
		pollOptionResponce.setPollLikes(option.getPollLikes());
		pollOptionResponces.add(pollOptionResponce);
		
		pollResponce.setPollOptions(pollOptionResponces);
		}
		
		
		if (!groupProfile.equals(null)) {
			response.setMessage("Poll is created successfully");

			response.setData(pollResponce);
			response.setError("0");
			response.setStatus("success");

			return ResponseEntity.ok(response);
		} else {
			response.setMessage("Something gone wrong");

			response.setData(empty);
			response.setError("1");
			response.setStatus("fail");

			return ResponseEntity.ok(response);
		}
		
	}
	
	@Transactional
	@PostMapping("/polllikes")
	public ResponseEntity<ResponseObject> pollLikes(@RequestParam(value="pollId", required=false) Integer pollId,
			@RequestParam(value="isLiked", required=false) Boolean isLiked,
			@RequestParam(value = "contactId", required=false) Integer  contactId) {
	
		if(pollId == null) {
			response.setError("1");
			response.setMessage("'pollId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}
		else if(isLiked == null) {
			response.setError("1");
			response.setMessage("'isLiked' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(contactId == null) {
			response.setError("1");
			response.setMessage("'contact' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
		
		PollOption polloption= polloptionRepository.getOne(pollId);
		List<PollLikes> likedUsersList= entityManager.createNativeQuery("SELECT * from poll_likes where contact_id=:contactId AND option_id=:pollId AND like_status=true").setParameter("contactId", contactId).setParameter("pollId",pollId).getResultList();
				
		UserContact phoneNumber=userContactRepository.getOne(contactId);
		long count=0;

		if(likedUsersList.isEmpty()) {
			
			count=polloption.getTotalLikes();
			PollLikes likedUsers = new PollLikes();
			likedUsers.setLikeStatus(isLiked);
			likedUsers.setContactId(contactId);
			
			polloption.setTotalLikes(count+1);
			polloption.getPollLikes().add(likedUsers); 
			polloptionRepository.save(polloption);
			
			response.setError("0");
			response.setMessage("user liked successfully");
			response.setData(polloption);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);

		}
		else {
			
				count=polloption.getTotalLikes();
				entityManager.createNativeQuery("delete p from poll_likes p where p.contact_id=:contactId AND  p.option_id=:pollId AND p.like_status=true").setParameter("contactId", contactId).setParameter("pollId",pollId).executeUpdate();
				polloption.setTotalLikes(count-1);
				
				polloptionRepository.save(polloption);
				
				response.setError("0");
				response.setMessage("user unliked successfully");
				response.setData(polloption);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);
			}
		}	
	}

	@Scheduled(cron = "0 0 0 * * *")
	//@Scheduled(cron = "*/10 * * * * *")
	public void schedulejob()
	{
		String status="exired";
		LocalDateTime currDate=LocalDateTime.now();
		System.out.println(currDate);
		entityManager.createNativeQuery("UPDATE group_poll SET poll_status=:status where expiry_date<= :currDate").setParameter("status", status).setParameter("currDate", currDate).executeUpdate();
	}
	
	
	@GetMapping("/getPoll")
	public ResponseEntity<ResponseObject> getPollsByPollId(@RequestParam("optionId") Integer optionId)
	{
		List<GroupPoll> groupPolls= groupPollRepository.findAllByPollId(optionId);
		if(!groupPolls.isEmpty()) {
		response.setError("0");
		response.setMessage("polls fetched successfully");
		response.setData(groupPolls);
		response.setStatus("SUCCESS");
		return ResponseEntity.ok(response);
		}
		else
		{
			response.setError("1");
			response.setMessage("no data found");
			response.setData(empty);
			response.setStatus("FAIL");
			return ResponseEntity.ok(response);
		}
		
	}
	@SuppressWarnings("unchecked")
	@GetMapping("/getAllPoll")
	public ResponseEntity<ResponseObject> getAllPollsByGroupId(@RequestParam("groupId") Integer groupId)
	{
		String status="active";
		List<GroupPoll> groupProfiles= entityManager.createNativeQuery("SELECT * FROM  group_poll WHERE group_id=:groupId AND poll_status=:status",GroupPoll.class).setParameter("groupId", groupId).setParameter("status", status).getResultList();
		if(!groupProfiles.isEmpty()) {
			response.setError("0");
			response.setMessage("polls fetched successfully");
			response.setData(groupProfiles);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
			}
			else
			{
				response.setError("1");
				response.setMessage("no data found");
				response.setData(empty);
				response.setStatus("FAIL");
				return ResponseEntity.ok(response);
			}
	}
Integer pollId;
	@PostMapping("/deletePoll")
	public void deletePoll(@RequestParam("pollId") Integer pollId,@RequestParam Long time )
	{
		this.pollId=pollId;
		try {
			Thread.sleep(time);
			
			run();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	
	public void run() {
		
		int a=entityManager.createQuery("delete from GroupPoll g WHERE  g.pollId= :pollId")
		.setParameter("pollId", pollId).executeUpdate();
		System.out.println(a);
	       
	}
	
}
