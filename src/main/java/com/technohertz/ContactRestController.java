package com.technohertz;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.technohertz.model.Empty;
import com.technohertz.model.UserContact;
import com.technohertz.model.UserRegister;
import com.technohertz.service.IUserContactService;
import com.technohertz.service.IUserRegisterService;
import com.technohertz.util.DateUtil;
import com.technohertz.util.ResponseObject;

@RestController
@RequestMapping("/contactRest")
public class ContactRestController {
	@Autowired
	private Empty empty;
	@Autowired
	private IUserRegisterService userRegisterService;
	
	@Autowired
	private IUserContactService userContactService;
	
	@Autowired
	private DateUtil dateUtil;
	
	@Autowired
	private ResponseObject response;
	
	/**
	 * 2. Save Otp
	 * @param otp
	 * @param errors
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/contact", method = RequestMethod.POST)
	public ResponseEntity<ResponseObject> saveContact( @RequestParam("contactList") String userContactList,
			@RequestParam("userId") String userid, ModelMap map){
		
		if(userid.equals("") || userid == null || userContactList.equals("") || userContactList == null) {
			
			response.setError("1");
			response.setMessage("wrong userId and contactList please enter correct value");
			response.setData(empty);
			response.setStatus("FAIL");
			return ResponseEntity.ok(response);
		
		}
		else {
		
				List<String> contactlist = getContactList(userContactList);//convert from string to array
				
				List<UserRegister> retrivedUserList =(List<UserRegister>) userRegisterService.getAll();//get all user from database
				
				List<String> retrievedContactList = userRegisterService.getAllMobile();//get all mobile no from database
				
				List<String> craziContact = new ArrayList<String>();
				
				
				Map<String, UserRegister> userList = new TreeMap<String, UserRegister>();
				
				for(String contactNumber : contactlist){
					
					for(UserRegister userRegister : retrivedUserList) {
						
						if(retrievedContactList.contains(contactNumber)) {//check user is craziapp user or not
							
							if(contactNumber == userRegister.getMobilNumber() || userRegister.getMobilNumber().equals(contactNumber)) {
								
								craziContact.add(contactNumber);
								userList.put(contactNumber, userRegister);
							}
						}
					}
				}
					
				
				List<UserRegister> retrievedUserRegister = new ArrayList<UserRegister> ();
				
				UserRegister tosaveuserRegister = new UserRegister();
				
				int userId = 0;
				try {
					userId = Integer.parseInt(userid);
				} catch (NumberFormatException e) {
					
					response.setError("1");
					response.setMessage("wrong userId please enter numeric value");
					response.setData(empty);
					response.setStatus("FAIL");
					return ResponseEntity.ok(response);
					
				}
				
				try {
					 retrievedUserRegister = userRegisterService.getById(userId);
					  tosaveuserRegister = retrievedUserRegister.get(0);
						
					}catch (Exception e) {
						response.setError("1");
						response.setMessage("wrong userId please enter numeric value");
						response.setData(empty);
						response.setStatus("FAIL");
						return ResponseEntity.ok(response);
					}

				List<UserContact> userCon = userContactService.getUserContactdetailByUserId(userId);
				
				List<String> userContains = userContactService.getUserContactsByUserId(userId);
				System.out.println(userCon);
//				 userContactService.deleteByUserId(userId);
				
				for(String contactNumber : craziContact){	
					
					UserContact contact = new UserContact();
					
					if(!userContains.contains(contactNumber)) {
						UserRegister userRegister = userList.get(contactNumber);
						
						contact.setContactNumber(contactNumber);
						contact.setContactName(userRegister.getUserName());
						contact.setProfilePic(userRegister.getProfile().getCurrentProfile());
						contact.setCreateDate(dateUtil.getDate());
						tosaveuserRegister.getUserContactList().add(contact);
						}
					else {
						for(UserContact userContact : userCon) {
						tosaveuserRegister.getUserContactList().add(userContact);
						}
					}
				}		
								
				
			userRegisterService.save(tosaveuserRegister);
			
			String message = "Your CraziApp contacts are searched successfully";
			response.setError("0");
			response.setMessage(message);
			response.setData(tosaveuserRegister);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
				
		}
		
	}
	
	private List<String> getContactList(String userContactList) {
		// TODO Auto-generated method stub
		List<String> contactList = new ArrayList<String>();
		String sContact[] = userContactList.split(",");
		
		for(String userContact : sContact ) {
		
				contactList.add(userContact);
			}
		return contactList;
	}

	
	@RequestMapping(value = "/contactList", method = RequestMethod.GET)
	public ResponseEntity<ResponseObject> saveContact(){
		
		List<UserContact> userContactList = userContactService.getAll();
		
			String message = "CraziApp all contacts are fetched successfully";
			response.setError("0");
			response.setMessage(message);
			response.setData(userContactList);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
				
		}
		
}
