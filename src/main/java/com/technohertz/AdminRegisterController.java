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
import org.springframework.web.multipart.MultipartFile;

import com.technohertz.exception.ResourceNotFoundException;
import com.technohertz.model.AdminProfile;
import com.technohertz.model.AdminRegister;
import com.technohertz.model.CardCategory;
import com.technohertz.model.Empty;
import com.technohertz.model.UserProfile;
import com.technohertz.model.UserRegister;
import com.technohertz.payload.UploadFileResponse;
import com.technohertz.service.IAdminRegisterService;
import com.technohertz.service.IUserRegisterService;
import com.technohertz.service.impl.FileStorageService;
import com.technohertz.util.ResponseObject;

@RestController
@RequestMapping("/adminRest")
public class AdminRegisterController {

	@Autowired
	FileStorageService fileStorageService;
	@Autowired
	private IAdminRegisterService adminRegisterService;
	
	@Autowired
	private IUserRegisterService userRegisterService;
	
	@Autowired
	private Empty empty;
	@Autowired

	private EntityManager entitymanager;

	@Autowired
	private ResponseObject response;
	
	
	@GetMapping("/myprofile")
	public List<AdminRegister> getAllEmployees() {
		return adminRegisterService.getAll();
	}
	
	@SuppressWarnings("unused")
	@PostMapping("/deleteuser")
	public ResponseEntity<ResponseObject> deleteUserById(@RequestParam("userId") Integer userId) {
	Integer useriD=adminRegisterService.deleteUserById(userId);
	if(useriD!=null ||useriD==0) {
	response.setMessage("your data is deleted successfully");
	response.setData(useriD);
	response.setError("0");
	response.setStatus("success");
	return ResponseEntity.ok(response);	
	}
	else {
		response.setError("1");
		response.setMessage("wrong userId please enter numeric value");
		response.setData(empty);
		response.setStatus("FAIL");
		return ResponseEntity.ok(response);
	}
	
	}
	@SuppressWarnings("unused")
	@PostMapping("/deletefile")
	public ResponseEntity<ResponseObject> deleteUserFile(@RequestParam("fileId") Integer fileId) {
	Integer fileid=adminRegisterService.deleteUserFile(fileId);
	if(fileid!=null && fileid!=0) {
	response.setMessage("your data is deleted successfully");
	response.setData(fileid);
	response.setError("0");
	response.setStatus("success");
	return ResponseEntity.ok(response);	
	}
	else {
		response.setError("1");
		response.setMessage("File is not present");
		response.setData(empty);
		response.setStatus("FAIL");
		return ResponseEntity.ok(response);
	}
	
	}


	@GetMapping("/myprofile/{adminId}")
	public ResponseEntity<ResponseObject> getAllEmployees(@PathVariable(value = "adminId") String adminId) {
		int adminid = 0;
		List<AdminRegister> register = new ArrayList<AdminRegister>();
		try {
			
			adminid = Integer.parseInt(adminId);
			register = adminRegisterService.getById(adminid);
			
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

	@SuppressWarnings("unused")
	@PostMapping("/adminlogin")
	public ResponseEntity<ResponseObject> loginCredential(@RequestParam("mailid") String mailid,@RequestParam("pass") String pass)
			throws ResourceNotFoundException {
		
			if(mailid.equals("") && mailid == null && pass.equals("") && pass == null) {
				
				response.setError("1");
				response.setMessage("wrong username and password please enter correct value");
				response.setData(empty);
				response.setStatus("FAIL");
				return ResponseEntity.ok(response);
			
			}
			else {
				AdminRegister adminRegister=null;
				List<AdminRegister> admindata = adminRegisterService.findByMailId(mailid);
				List<UserRegister> userData=userRegisterService.getAll();
				/*get from database*/
						if(admindata.isEmpty() && userData.isEmpty())
						{
							
						
						response.setMessage("user not found please try again");
						response.setError("1");
						response.setStatus("FAIL");
						response.setData(empty);
						return ResponseEntity.ok(response);
					}
					else {
						adminRegister = admindata.get(0);
						adminRegister.setUserList(userData);
						String mailId=adminRegister.getMailId();
						String password = adminRegister.getPassword();
						Boolean userStatus=adminRegister.getIsActive();
			
					
					if (mailId.equals(mailid)  && password.equals(pass) && userStatus==true) 
					{
						response.setStatus("SUCCESS");
						response.setMessage("Logged in successfully");
						response.setError("0");
						response.setData(admindata);
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

	@PostMapping("/blockUser")
	public ResponseEntity<ResponseObject> blockUser(@RequestParam int userID,
												@RequestParam Boolean isActive) {
		List<UserRegister> user=userRegisterService.getById(userID);
		if(!user.isEmpty()) {
			user.get(0).setIsActive(false);
			UserRegister userRegister=userRegisterService.save(user.get(0));
			response.setStatus("SUCCESS");
			response.setMessage("user is blocked successfully");
			response.setError("0");
			response.setData(userRegister);
			return ResponseEntity.ok(response);
			
		}
		else {
			 response.setStatus("FAIL");
				response.setMessage("please check username or password");
				response.setError("1");
				response.setData(empty);
				return ResponseEntity.ok(response);
		}
	}
	
	@PostMapping("/saveAdmin")
	public ResponseEntity<ResponseObject> addAdmin(@RequestParam String mailId,
												@RequestParam String password) {
		
		if(mailId.equals("")|| mailId == null ||
				password.equals("") || password == null) {
			
			response.setError("1");
			response.setMessage("wrong userName, password, mobileNumber please enter correct value");
			response.setData(empty);
			response.setStatus("FAIL");
			return ResponseEntity.ok(response);
		
		}else {
		
				AdminRegister admin = new AdminRegister();
				admin.setPassword(password);
				admin.setMailId(mailId);
				admin.setIsActive(true);
				admin.setCreateDate(getDate());
				admin.setLastModifiedDate(getDate());
				admin.setToken(getRandomNumber());
				admin.setSourceFrom("Laptop");
				if(!userExists(mailId)){
			
					adminRegisterService.save(admin);
					response.setStatus("Success");
					response.setMessage("  CraziApp Admin Registration is successful");
					response.setError("0");
					response.setData(admin);
					
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
		


	private boolean userExists(String mailID)
	{
		String hql="FROM AdminRegister as ur WHERE ur.mailId= ?1";
		int count=entitymanager.createQuery(hql).setParameter(1, mailID).getResultList().size();
		
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
	
	
    @SuppressWarnings("unused")
	@PostMapping("/uploadCard")
    public ResponseEntity<ResponseObject> uploadCards(@RequestParam(value="file", required=false) MultipartFile file,
    		@RequestParam(value="fileType", required=false)String fileType,
    		@RequestParam(value = "categoryId", required=false) Integer  categoryId,
    		@RequestParam(value = "cardText", required=false) String cardText) {
     
    	if(file == null) {
			response.setError("1");
			response.setMessage("'file' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}
		else if(fileType == null) {
			response.setError("1");
			response.setMessage("'fileType' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(cardText == null) {
			response.setError("1");
			response.setMessage("'cardText' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(categoryId == null) {
			response.setError("1");
			response.setMessage("'categoryId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
			
    	CardCategory cardCategory = fileStorageService.storeCards(file,categoryId,cardText);
     
        if (cardCategory != null) {
			Object obj = new UploadFileResponse(cardCategory.getCards().get(cardCategory.getCards().size()-1).getFilePath(),
					cardCategory.getCards().get(cardCategory.getCards().size()-1).getFilePath(), file.getContentType(), file.getSize());
			if (!file.isEmpty() || categoryId != null) {
				response.setMessage("your File is uploaded successfully");

				response.setData(obj);
				response.setError("0");
				response.setStatus("SUCCESS");

				return ResponseEntity.ok(response);
			} else {
				response.setMessage("your File is not uploaded");

				response.setData(empty);
				response.setError("1");
				response.setStatus("FAIL");

				return ResponseEntity.ok(response);
			} 
		}else {
			response.setMessage("User does not exist please register first");

			response.setData(empty);
			response.setError("1");
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);
		}
	  }
    }
    
    @SuppressWarnings("unused")
	@PostMapping("/addCategory")
    public ResponseEntity<ResponseObject> uploadCategory(@RequestParam(value="file", required=false) MultipartFile file,
    		@RequestParam(value="categoryName", required=false)String categoryName,
    		@RequestParam(value="adminId", required=false)Integer adminId,
    		@RequestParam(value = "categoryType", required=false) String  categoryType) {
     
    	if(file == null) {
			response.setError("1");
			response.setMessage("'file' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}
		else if(categoryName == null) {
			response.setError("1");
			response.setMessage("'CategoryName' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(categoryType == null) {
			response.setError("1");
			response.setMessage("'categoryType' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(adminId == null) {
			response.setError("1");
			response.setMessage("'adminId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
			
    	AdminProfile adminProfile = fileStorageService.storeCards(file, adminId,categoryName,categoryType);
    	
    	List<CardCategory> cardCategoryList = adminProfile.getCardCategories();
     
        if (adminProfile != null) {
        	
        	Object obj=new UploadFileResponse(cardCategoryList.get(cardCategoryList.size()-1).getFilePath(),
        			cardCategoryList.get(cardCategoryList.size()-1).getFilePath(), file.getContentType(), file.getSize());
			
			if (!file.isEmpty() || adminId != null) {
				response.setMessage("your File is uploaded successfully");

				response.setData(obj);
				response.setError("0");
				response.setStatus("SUCCESS");

				return ResponseEntity.ok(response);
			} else {
				response.setMessage("your File is not uploaded");

				response.setData(empty);
				response.setError("1");
				response.setStatus("FAIL");

				return ResponseEntity.ok(response);
			} 
		}else {
			response.setMessage("User does not exist please register first");

			response.setData(empty);
			response.setError("1");
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);
		}
	  }
    }    	
	
	
	
	
	
	
	
}
