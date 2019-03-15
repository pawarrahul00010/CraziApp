package com.technohertz;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.technohertz.exception.ResourceNotFoundException;
import com.technohertz.model.Biometric;
import com.technohertz.model.Empty;
import com.technohertz.model.MediaFiles;
import com.technohertz.model.UserContact;
import com.technohertz.model.UserOtp;
import com.technohertz.model.UserProfile;
import com.technohertz.model.UserRegister;
import com.technohertz.repo.UserRegisterRepository;
import com.technohertz.service.IGroupProfileService;
import com.technohertz.service.IMediaFileService;
import com.technohertz.service.IUserRegisterService;
import com.technohertz.service.impl.FileStorageService;
import com.technohertz.util.CommonUtil;
import com.technohertz.util.GetProfile;
import com.technohertz.util.GetProfiles;
import com.technohertz.util.OtpUtil;
import com.technohertz.util.ResponseObject;
import com.technohertz.util.sendSMS;

@RestController
@RequestMapping("/userRest")
public class UserRegisterController {

	@Autowired
	private IUserRegisterService userRegisterService;
	
	@Autowired
	private IMediaFileService mediaFileService;

	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private Empty empty;
	
	@Autowired
	private IGroupProfileService groupProfileService;

	@Autowired
	UserRegisterRepository userRegisterRepository; 
	
	@Autowired
	private EntityManager entitymanager;

	@Autowired
	private ResponseObject response;
	
	@Autowired
	private OtpUtil util;
	
	
	@Autowired
	private FileStorageService fileStorageService;
 
	
	@Autowired
	private sendSMS sms;
	
	
	@SuppressWarnings("unused")
	@GetMapping("/myprofile")
	public ResponseEntity<ResponseObject> getAllEmployees() {

		List<UserRegister> userLists= userRegisterService.getAll();
		List<GetProfiles> profile=new ArrayList<GetProfiles>();

		for(UserRegister userRegister :userLists) {
			GetProfiles getProfile = new GetProfiles();
			getProfile.setMobileno(userRegister.getMobilNumber());
			getProfile.setUserName(userRegister.getUserName());
			getProfile.setUserId(userRegister.getUserId());
			getProfile.setFilePath(userRegister.getProfile().getCurrentProfile());
			profile.add(getProfile);
		}
		
		if(profile == null) {
			
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}
		else {
			response.setError("0");
			response.setMessage("User List are");
			response.setData(profile);
			response.setStatus("Success");
			
			return ResponseEntity.ok(response);
		}
	}

	@GetMapping("/myprofile/{userId}")
	public ResponseEntity<ResponseObject> getUser(@PathVariable(value = "userId", required=false) String userid) {
		
		if(userid == null) {
			
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
			
			int userId = 0;
			List<UserRegister> register = new ArrayList<UserRegister>();
			try {
				
				userId = Integer.parseInt(userid);
				register = userRegisterService.getById(userId);
				
			} catch (NumberFormatException e) {
				
				response.setError("1");
				response.setMessage("wrong userId please enter numeric value");
				response.setData(empty);
				response.setStatus("FAIL");
				return ResponseEntity.ok(response);
				
			}
			
			if(!register.isEmpty()) {
			response.setMessage("your data is retrived successfully");
			response.setData(register);
			response.setError("0");
			response.setStatus("success");
			return ResponseEntity.ok(response);	
			}
			else {
				response.setMessage("no data found");
				response.setData(empty);
				response.setError("0");
				response.setStatus("FAIL");
				return ResponseEntity.ok(response);		
			}
			
		}
	}
	
	@GetMapping("/userprofile/{userId}")
	public ResponseEntity<ResponseObject> getUserProfile(@PathVariable(value = "userId", required=false) String userid) {
		
		if(userid == null) {
			
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
			
			int userId = 0;
			List<UserRegister> register = new ArrayList<UserRegister>();
			try {
				
				userId = Integer.parseInt(userid);
				register = userRegisterService.getById(userId);
				
			} catch (NumberFormatException e) {
				
				response.setError("1");
				response.setMessage("wrong userId please enter numeric value");
				response.setData(empty);
				response.setStatus("FAIL");
				return ResponseEntity.ok(response);
				
			}
			
			if(!register.isEmpty()) {
				
				UserRegister userRegister = register.get(0);
				
				GetProfile getProfile = new GetProfile();
				
				getProfile.setUserId(userId);
				
				getProfile.setUser(userRegister.getUserName());
				
				getProfile.setGroupList(groupProfileService.getUserGroupdetailByUserId(userRegister.getMobilNumber()));
				getProfile.setBookmarkList(mediaFileService.getBookmarksByUserId(userId));
				List<MediaFiles> profileList = fileStorageService.getAllProfileById(userRegister.getProfile().getProfileId());
				getProfile.setPhotos(mediaFileService.getAllProfileCountById(userId));
				Float rating = commonUtil.getRating(profileList);
				Long likes = commonUtil.getLikes(profileList);
				
				getProfile.setRating(rating);
				getProfile.setLikes(likes);
	//			getProfile.setUserProfile(userProfile);
				getProfile.setProfileList(profileList);
			
			response.setMessage("your data is retrived successfully");
			response.setData(getProfile);
			response.setError("0");
			response.setStatus("success");
			return ResponseEntity.ok(response);	
			}
			else {
				response.setMessage("no data found");
				response.setData(empty);
				response.setError("0");
				response.setStatus("FAIL");
				return ResponseEntity.ok(response);		
			}
			
		}
	}

	@SuppressWarnings("unused")
	@PostMapping("/login")
	public ResponseEntity<ResponseObject> loginCredential(@RequestParam(value ="user", required=false) String user,
			@RequestParam(value ="pass", required=false) String pass)
			throws ResourceNotFoundException {
		
		if(user == null) {
			
			response.setError("1");
			response.setMessage("'user' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(pass == null) {
			
			response.setError("1");
			response.setMessage("'pass' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
				UserRegister userRegister=null;
				List<UserRegister> userRegisterList = userRegisterService.findByUserName(user);
				
				/*get from database*/
				
					if(userRegisterList.isEmpty())
					{
						List<UserRegister> userList = userRegisterService.findByMobileNumber(user);
						if(userList.isEmpty())
						{
							
						
						response.setMessage("user not found please try again");
						response.setError("1");
						response.setStatus("FAIL");
						response.setData(empty);
						return ResponseEntity.ok(response);
					}
					else {
						userRegister = userList.get(0);
						String name = userRegister.getUserName();
						String mobileNumber=userRegister.getMobilNumber();
						String password = userRegister.getPassword();
						Boolean userStatus=userRegister.getIsActive();
			
					
					if (mobileNumber.equals(user)  && password.equals(pass) && userStatus==true) 
					{
						response.setStatus("SUCCESS");
						response.setMessage("Logged in successfully");
						response.setError("0");
						response.setData(userList);
						return ResponseEntity.ok(response);
						
					}else {
						 response.setStatus("FAIL");
						response.setMessage("please check username or password");
						response.setError("1");
						response.setData(empty);
						return ResponseEntity.ok(response);
					}
				}
		}
					else {
						userRegister = userRegisterList.get(0);
						String name = userRegister.getUserName();
						String password = userRegister.getPassword();
						Boolean userStatus=userRegister.getIsActive();
			
					
					if (name.equals(user)  && password.equals(pass) && userStatus==true) 
					{
						response.setStatus("SUCCESS");
						response.setMessage("Logged in successfully");
						response.setError("0");
						response.setData(userRegisterList);
						return ResponseEntity.ok(response);
						
					}else {
						 response.setStatus("FAIL");
						response.setMessage("please check username or password");
						response.setError("1");
						response.setData(empty);
						return ResponseEntity.ok(response);
					}
				}
			}
		}


	
	@PostMapping("/saveall")
	public ResponseEntity<ResponseObject> addUser(@RequestParam (value ="userName", required=false)String userName,
			@RequestParam (value ="password", required=false)String password,
			@RequestParam (value ="mobileNumber", required=false)String mobileNumber) {
		
		if(userName == null) {
			
			response.setError("1");
			response.setMessage("'userName' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(password == null) {
			
			response.setError("1");
			response.setMessage("'password' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(mobileNumber == null) {
			
			response.setError("1");
			response.setMessage("'mobileNumber' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
		
				UserRegister user = new UserRegister();
				UserContact userContact = new UserContact();
				UserProfile profile = new UserProfile();
				//MediaFiles mediaFiles=new MediaFiles();
				Biometric biometric=new Biometric();
		
				user.setUserName(userName);
				user.setPassword(password);
				user.setMobilNumber(mobileNumber);
				user.setIsActive(true);
				user.setSourceFrom("Laptop");
				List<UserContact> userContactList=new ArrayList<>();
				
				userContact.setContactNumber(mobileNumber);
				userContact.setContactName(userName);
				userContact.setCreateDate(getDate());
	
				userContactList.add(userContact);
				user.setUserContactList(userContactList);
				user.setToken(getRandomNumber());
				user.setCreateDate(getDate());
				user.setLastModifiedDate(getDate());
				profile.setDisplayName(user.getUserName());
				
				//profile.getFiles().add(mediaFiles);
				biometric.setIsActive(true);
				user.setProfile(profile);
				user.getBiometric().add(biometric);	
				UserOtp userOtp = new UserOtp();
				
				if(!userExists(mobileNumber) && !userNameExists(userName)){
					
					userOtp.setIs_active(true);
					userOtp.setCreateDate(getDate());
					userOtp.setLastModifiedDate(getDate());
					int OTP = util.getOTP();
					userOtp.setOtp(OTP);
					user.setUserOtp(userOtp);
		
					userRegisterService.save(user);
					
					sms.sendSms(String.valueOf(mobileNumber), "Your CraziApp Registration is successful enter OTP to verify : "+OTP);
					
					response.setStatus("Success");
					response.setMessage("  CraziApp Registration is successful");
					response.setError("0");
					response.setData(user);
					
					return ResponseEntity.ok(response);
					
				}
				else {
					
					response.setStatus("FAIL");
					response.setMessage(" user is allready Registered");
					response.setError("1");
					response.setData(empty);
						return ResponseEntity.ok(response);
				}
			}
		}
		

		@Cacheable(value="mobileNumberCache",key="#mobileNumber",unless="#result==null")
		private boolean userExists(String mobileNumber)
		{
			String hql="FROM UserRegister as ur WHERE ur.mobilNumber= ?1";
			int count=entitymanager.createQuery(hql).setParameter(1, mobileNumber).getResultList().size();
			
			return count>0 ? true : false;
			
		}
		
		@Cacheable(value="userNameCache",key="#userName",unless="#result==null")
		private boolean userNameExists(String userName)
		{
			String hql="FROM UserRegister as ur WHERE ur.userName= ?1";
			int count=entitymanager.createQuery(hql).setParameter(1, userName).getResultList().size();
			
			return count>0 ? true : false;
			
		}
		
		private LocalDateTime getDate() {
	
			LocalDateTime now = LocalDateTime.now();
	
			return now;
	
		}

		private int getRandomNumber() {
	
			int rand = new Random().nextInt(10000); 
			  
			return rand;
	
		}
	
	
	@PostMapping("/ptrn/unlk")
	public ResponseEntity<ResponseObject> patternCredential(@RequestParam(value ="userId", required=false) String userId,
			@RequestParam(value ="pattern", required=false) String pattern)
			throws ResourceNotFoundException {
		
		if(userId == null) {
			
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(pattern == null) {
			
			response.setError("1");
			response.setMessage("'pattern' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else  {
				
				int userid = 0;
				try {
					userid = Integer.parseInt(userId);
				} catch (NumberFormatException e) {

					response.setError("1");
					response.setMessage("wrong userId please enter numeric value");
					response.setData(empty);
					response.setStatus("FAIL");
					return ResponseEntity.ok(response);

				}
				
				UserRegister userRegister = userRegisterService.getOneById(userid);

						String dbpattern = userRegister.getPattern();
					
					if (dbpattern.equals(pattern) || dbpattern == pattern) 
					{
						response.setStatus("SUCCESS");
						response.setMessage("Logged in successfully");
						response.setError("0");
						response.setData(userRegister);
						return ResponseEntity.ok(response);
						
					}else {
						 response.setStatus("FAIL");
						response.setMessage("pattern does not matched please check");
						response.setError("1");
						response.setData(empty);
						return ResponseEntity.ok(response);
					}
				
			}
		}

	
	@PostMapping(value = {"/ptrn/save","/ptrn/update"})
	public ResponseEntity<ResponseObject> patternSave(@RequestParam(value ="userId", required=false) String userId,
			@RequestParam(value ="pattern", required=false) String pattern)
			throws ResourceNotFoundException {
		
		if(userId == null) {
			
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(pattern == null) {
			
			response.setError("1");
			response.setMessage("'pattern' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else  {
				
				int userid = 0;
				try {
					userid = Integer.parseInt(userId);
				} catch (NumberFormatException e) {

					response.setError("1");
					response.setMessage("wrong userId please enter numeric value");
					response.setData(empty);
					response.setStatus("FAIL");
					return ResponseEntity.ok(response);

				}
				
				UserRegister userRegister = userRegisterService.getOneById(userid);

					userRegister.setPattern(pattern);
					
					userRegisterService.save(userRegister);
					
					response.setStatus("SUCCESS");
					response.setMessage("Pattern updated successfully");
					response.setError("0");
					response.setData(userRegister);
					return ResponseEntity.ok(response);
						
			}
		}

	
	@PostMapping(value = {"/ptrn/remove"})
	public ResponseEntity<ResponseObject> patternRemove(@RequestParam(value ="userId", required=false) String userId)
			throws ResourceNotFoundException {
		
		if(userId == null) {
			
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
				
				int userid = 0;
				try {
					userid = Integer.parseInt(userId);
				} catch (NumberFormatException e) {

					response.setError("1");
					response.setMessage("wrong userId please enter numeric value");
					response.setData(empty);
					response.setStatus("FAIL");
					return ResponseEntity.ok(response);

				}
				
				UserRegister userRegister = userRegisterService.getOneById(userid);

					userRegister.setPattern(null);
					
					userRegisterService.save(userRegister);
					
					response.setStatus("SUCCESS");
					response.setMessage("pattern removed successfully");
					response.setError("0");
					response.setData(userRegister);
					return ResponseEntity.ok(response);
						
			}
		}
	
	
	
	
	

	@PostMapping("/swithmobilenumber")
	public ResponseEntity<ResponseObject> swithMobileNumber(
			@RequestParam(value = "oldMobileNumber", required = false) String oldMobileNumber,
			@RequestParam(value = "newMobileNumber", required = false) String newMobileNumber) throws ResourceNotFoundException {

		if (oldMobileNumber == null) {

			response.setError("1");
			response.setMessage("'old mobile number' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else if (newMobileNumber == null) {

			response.setError("1");
			response.setMessage("'profileid' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else {

			Long mobileNumber = 0l;
			try {
				mobileNumber = Long.parseLong(oldMobileNumber);
			} catch (NumberFormatException e) {

				response.setError("1");
				response.setMessage("wrong MobileNumber please enter numeric value");
				response.setData(empty);
				response.setStatus("FAIL");
				return ResponseEntity.ok(response);

			}

			List<UserRegister> register = null;
			UserRegister updateMobileNumber = null;
			UserOtp userOtp =new UserOtp();
			register = userRegisterRepository.findByMobileNumber(oldMobileNumber);
			if (!register.isEmpty()) {
				
				register.get(0).setMobilNumber(newMobileNumber);
				updateMobileNumber = userRegisterRepository.save(register.get(0));

				response.setMessage("your Display name updated successfully");
				response.setData("Your New Mobile number : "+newMobileNumber);
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

}
