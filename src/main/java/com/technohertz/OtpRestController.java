package com.technohertz;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.technohertz.model.Empty;
import com.technohertz.model.UserOtp;
import com.technohertz.model.UserRegister;
import com.technohertz.service.IUserRegisterService;
import com.technohertz.util.OtpUtil;
import com.technohertz.util.ResponseObject;

@RestController
@RequestMapping("/otpRest")
public class OtpRestController {
	@Autowired
	private Empty empty;
	@Autowired
	private IUserRegisterService userRegisterService;
	
	@Autowired
	private OtpUtil util;
	
	@Autowired
	private ResponseObject response;

	

		@RequestMapping(value = {"/forget/otp","/otp/resend"}, method = RequestMethod.POST)
		public ResponseEntity<ResponseObject> saveForgetOTP(@RequestParam(value ="mobilNumber", required=false) String mobilNumber){
			
			if(mobilNumber == null ) {
				
				response.setError("1");
				response.setMessage("'mobilNumber' is empty or null please check");
				response.setData(empty);
				response.setStatus("FAIL");
				
				return ResponseEntity.ok(response);
			
			}
			else {
			
			List<UserRegister> retrievedUserRegister = new ArrayList<UserRegister> ();
			
				
					 retrievedUserRegister = userRegisterService.findByMobileNumber(mobilNumber);
					 
					 if(!retrievedUserRegister.isEmpty()) {
						 
						 if((retrievedUserRegister.get(0).getUserOtp().getOtp() == 0) || 
								 (String.valueOf(retrievedUserRegister.get(0).getUserOtp().getOtp()) == null) || 
								 (String.valueOf(retrievedUserRegister.get(0).getUserOtp().getOtp()) == "")){
							 
							 UserOtp userOtp = new UserOtp();
								int otp = util.getOTP();
								userOtp.setOtp(otp);
								userOtp.setCreateDate(getDate());
								userOtp.setLastModifiedDate(getDate());
								retrievedUserRegister.get(0).setUserOtp(userOtp);
								
								userRegisterService.save(retrievedUserRegister.get(0));
								
								String message = "OTP send successfully enter OTP to verify";
								response.setError("0");
								response.setMessage(message);
								response.setData(retrievedUserRegister.get(0));
								response.setStatus("SUCCESS");
								return ResponseEntity.ok(response);
								
							
						}else {
							String message = "OTP send successfully enter OTP to verify";
							response.setError("0");
							response.setMessage(message);
							response.setData(retrievedUserRegister.get(0));
							response.setStatus("SUCCESS");
							return ResponseEntity.ok(response);
						}
						 
					} else {
						response.setError("1");
						response.setMessage("You are not a registered User please register first");
						response.setData(empty);
						response.setStatus("FAIL");
						return ResponseEntity.ok(response);
					}
			}
	
		}
		
	@RequestMapping(value = "/otp/verify", method = RequestMethod.POST)
	public ResponseEntity<ResponseObject> verifyOTP(@RequestParam(value ="userId", required=false) String userid,
			@RequestParam(value ="otp", required=false) String OTP){
		
		if(userid == null ) {
			
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
		
		}
		else if(OTP == null ) {
			
			response.setError("1");
			response.setMessage("'otp' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			return ResponseEntity.ok(response);
		
		}
		else {
			List<UserRegister> retrievedUserRegister = new ArrayList<UserRegister> ();
			
			int userId = 0;
			int otp = 0;
			try {
				userId = Integer.parseInt(userid);
				otp = Integer.parseInt(OTP);
			} catch (NumberFormatException e) {
				
				response.setError("1");
				response.setMessage("wrong userId and otp please enter numeric value");
				response.setData(empty);
				response.setStatus("FAIL");
				return ResponseEntity.ok(response);
				
			}
			
			if((userid != null) && (!userid.equals("") && (OTP != null) && (!OTP.equals("")))) {
				
				 retrievedUserRegister = userRegisterService.getById(userId);
				 
				 if(!retrievedUserRegister.isEmpty()) {				
		
					if(retrievedUserRegister.get(0).getUserOtp()!= null){
							
							if(retrievedUserRegister.get(0).getUserOtp().getOtp() == otp) {
								
								UserOtp saveUserOTP = new UserOtp();
								saveUserOTP.setOtp(0);
								saveUserOTP.setOtpId(retrievedUserRegister.get(0).getUserOtp().getOtpId());
								saveUserOTP.setCreateDate(retrievedUserRegister.get(0).getUserOtp().getCreateDate());
								saveUserOTP.setLastModifiedDate(getDate());
								retrievedUserRegister.get(0).setUserOtp(saveUserOTP);
								userRegisterService.save(retrievedUserRegister.get(0));
								
								response.setError("0");
								response.setMessage("verified");
								response.setData(retrievedUserRegister.get(0));
								response.setStatus("SUCCESS");
								return ResponseEntity.ok(response);
								
							}else {
								
								response.setError("1");
								response.setMessage("Sorry wrong OTP please try again...");
								response.setData(empty);
								response.setStatus("FAIL");
								return ResponseEntity.ok(response);
									
								}
				
						}else {
							response.setError("1");
							response.setMessage("click on resend OTP");
							response.setData(empty);
							response.setStatus("FAIL");
							return ResponseEntity.ok(response);
						}
						
				 } else {
						response.setError("1");
						response.setMessage("You are not a registered User please register first");
						response.setData(empty);
						response.setStatus("FAIL");
						return ResponseEntity.ok(response);
					}
			 }
			else {	
				
				response.setError("1");
				response.setMessage("wrong userId please enter numeric value");
				response.setData(empty);
				response.setStatus("FAIL");
				return ResponseEntity.ok(response);
			}
		}

	}
	
	public LocalDateTime getDate() {
		
	    LocalDateTime now = LocalDateTime.now();  
		   
		return now;
		
	}
}
