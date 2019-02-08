package com.technohertz;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.technohertz.exception.ResourceNotFoundException;
import com.technohertz.model.Biometric;
import com.technohertz.model.UserOtp;
import com.technohertz.model.UserProfile;
import com.technohertz.model.UserRegister;
import com.technohertz.service.IUserRegisterService;
import com.technohertz.util.OtpUtil;
import com.technohertz.util.ResponseObject;
import com.technohertz.util.sendSMS;

@RestController
@RequestMapping("/userRest")
public class UserRegisterController {

	@Autowired
	private IUserRegisterService userRegisterService;

	@Autowired

	private EntityManager entitymanager;

	@Autowired
	private ResponseObject response;
	
	@Autowired
	private OtpUtil util;
	
	@Autowired
	private sendSMS sms;
	
	
	@GetMapping("/myprofile")
	public List<UserRegister> getAllEmployees() {
		return userRegisterService.getAll();
	}

	@GetMapping("/myprofile/{userId}")
	public ResponseEntity<ResponseObject> getAllEmployees(@PathVariable(value = "userId") String userid) {
		
		
		int userId = 0;
		List<UserRegister> register = new ArrayList<UserRegister>();
		try {
			
			userId = Integer.parseInt(userid);
			register = userRegisterService.getById(userId);
			
		} catch (NumberFormatException e) {
			
			response.setError("1");
			response.setMessage("wrong userId please enter numeric value");
			response.setData("[]");
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
			response.setData("[]");
			response.setError("0");
			response.setStatus("FAIL");
			return ResponseEntity.ok(response);		
		}
		
	}

	@SuppressWarnings("unused")
	@PostMapping("/login")
	public ResponseEntity<ResponseObject> loginCredential(@RequestParam("user") String user,@RequestParam("pass") String pass)
			throws ResourceNotFoundException {
		
			if(user.equals("") && user == null && pass.equals("") && pass == null) {
				
				response.setError("1");
				response.setMessage("wrong username and password please enter correct value");
				response.setData("[]");
				response.setStatus("FAIL");
				return ResponseEntity.ok(response);
			
			}
			else {
				UserRegister userRegister=null;
				List<UserRegister> userRegisterList = userRegisterService.findByUserName(user);
				List<UserRegister> userList = userRegisterService.findByMobileNumber(user);
				/*get from database*/
				
					if(userRegisterList.isEmpty())
					{
						
						if(userList.isEmpty())
						{
							
						
						response.setMessage("user not found please try again");
						response.setError("1");
						response.setStatus("FAIL");
						response.setData(userRegisterList);
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
						response.setStatus("true");
						response.setMessage("Logged in successfully");
						response.setError("");
						response.setData(userList);
						return ResponseEntity.ok(response);
						
					}else {
						 response.setStatus("false");
						response.setMessage("please check username or password");
						response.setError("1");
						response.setData("[]");
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
						response.setStatus("true");
						response.setMessage("Logged in successfully");
						response.setError("");
						response.setData(userRegisterList);
						return ResponseEntity.ok(response);
						
					}else {
						 response.setStatus("false");
						response.setMessage("please check username or password");
						response.setError("1");
						response.setData("[]");
						return ResponseEntity.ok(response);
					}
				}
			}
		}


	
	@PostMapping("/saveall")
	public ResponseEntity<ResponseObject> addUser(@RequestParam String userName,
												@RequestParam String password,
												@RequestParam String mobileNumber) {
		
		if(userName.equals("")|| userName == null ||
				password.equals("") || password == null ||
				mobileNumber.equals("") || mobileNumber == null) {
			
			response.setError("1");
			response.setMessage("wrong userName, password, mobileNumber please enter correct value");
			response.setData("[]");
			response.setStatus("FAIL");
			return ResponseEntity.ok(response);
		
		}else {
		
				UserRegister user = new UserRegister();
		
				UserProfile profile = new UserProfile();
				//MediaFiles mediaFiles=new MediaFiles();
				Biometric biometric=new Biometric();
		
				user.setUserName(userName);
				user.setPassword(password);
				user.setMobilNumber(mobileNumber);
				user.setIsActive(true);
				user.setSourceFrom("Laptop");
				user.setToken(getRandomNumber());
				user.setCreateDate(getDate());
				user.setLastModifiedDate(getDate());
				profile.setDisplayName(user.getUserName());
				//profile.getFiles().add(mediaFiles);
				biometric.setIsActive(true);
				user.setProfile(profile);
				user.getBiometric().add(biometric);	
				UserOtp userOtp = new UserOtp();
				
				if(!userExists(mobileNumber) ){
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
					response.setError("");
					response.setData(user);
					
					return ResponseEntity.ok(response);
					
				}
				else {
		
					response.setStatus("FAIL");
					response.setMessage(" user is allready Registered");
					response.setError("1");
					response.setData("[]");
						return ResponseEntity.ok(response);
				}
			}
		}
		


	private boolean userExists(String mobileNumber)
	{
		String hql="FROM UserRegister as ur WHERE ur.mobilNumber= ?1";
		int count=entitymanager.createQuery(hql).setParameter(1, mobileNumber).getResultList().size();
		
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
}
