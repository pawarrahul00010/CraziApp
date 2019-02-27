package com.technohertz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.technohertz.common.Constant;
import com.technohertz.exception.ResourceNotFoundException;
import com.technohertz.model.Empty;
import com.technohertz.model.GetImage;
import com.technohertz.model.LikedUsers;
import com.technohertz.model.MediaFiles;
import com.technohertz.model.UserProfile;
import com.technohertz.model.UserRegister;
import com.technohertz.payload.UploadFileResponse;
import com.technohertz.repo.MediaFileRepo;
import com.technohertz.repo.UserProfileRepository;
import com.technohertz.repo.UserRegisterRepository;
import com.technohertz.service.IMediaFileService;
import com.technohertz.service.IUserRegisterService;
import com.technohertz.service.impl.FileStorageService;
import com.technohertz.util.ResponseObject;


@RestController
@RequestMapping("/profile")
public class UserProfileController {
	
	@Autowired
	private UserProfileRepository userprofilerepo;
	
		
	@Autowired
	private Empty empty;
	
	@Autowired
	private UserRegisterRepository registerRepository;

	@Autowired
	private MediaFileRepo mediaFileRepo;
	
	@Autowired
	private IMediaFileService mediaFileService;

	@Autowired
	private ResponseObject response;
	
	@Autowired
	private FileStorageService fileStorageService;

	private static String UPLOADED_FOLDER = "F://temp//";
	@PostMapping("/getAllProfiles")
	public ResponseEntity<ResponseObject> getAllProfilesById(@RequestParam("userId") Integer userId) {
		
		List<MediaFiles> likedUsers=fileStorageService.getAllProfileById(userId);
		List<GetImage> image=new ArrayList<GetImage>();

		for(MediaFiles mediaFiles :likedUsers) {
			GetImage img = new GetImage();
			img.setUser(mediaFiles.getFilePath());
			img.setfileId(mediaFiles.getFileId());
			image.add(img);
		}

		response.setError("0");	
		response.setMessage("successfully fetched");
		response.setData(image);
		response.setStatus("SUCCESS");
		return ResponseEntity.ok(response);
		
	}


	@SuppressWarnings("unused")
	@PostMapping("/displayName")
	public ResponseEntity<ResponseObject> updateDisplayName(@RequestParam("displayName") String displayName,
			@RequestParam("profileid") String profileid) throws ResourceNotFoundException {


		if(displayName.equals("") && displayName == null && profileid.equals("") && profileid == null) {

			response.setError("1");	
			response.setMessage("wrong username and profileid please enter correct value");
			response.setData(empty);
			response.setStatus("FAIL");
			return ResponseEntity.ok(response);

		}
		else {

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

			List<UserProfile> profile = null;
			UserProfile updateDisplayName =null;

			profile = userprofilerepo.findById(id);
			if(!profile.isEmpty()) {

				profile.get(0).setDisplayName(displayName);
				profile.get(0).setProfileId(id);
				updateDisplayName = userprofilerepo.save(profile.get(0));

				response.setMessage("your Display name updated successfully");
				response.setData(updateDisplayName);
				response.setError("");
				response.setStatus("success");

				return ResponseEntity.ok(response);	
			}
			else {
				response.setMessage("user not available");
				response.setData(empty);
				response.setError("1");
				response.setStatus("fail");
				return ResponseEntity.ok(response);
			}
		}
	}



	@PostMapping("/likes")
	public ResponseEntity<ResponseObject> totalLikes(@RequestParam("fileid") int fileid,@RequestParam("isLiked") boolean isLiked,
			@RequestParam(value = "userId") int  userId) {
		
		MediaFiles mediaFiles= mediaFileRepo.getById(fileid);
		UserRegister userRegister = registerRepository.getOne(userId);
		List<LikedUsers> likedUsersList= mediaFileService.getUserLikesByFileId(fileid, userId);
		
		long count=0;

		if(likedUsersList.isEmpty()) {
			count=mediaFiles.getLikes();
			LikedUsers likedUsers = new LikedUsers();
			likedUsers.setUserName(userRegister.getUserName());
			likedUsers.setMarkType(Constant.LIKE);
			likedUsers.setUserId(userId);
			mediaFiles.setLikes(count+1);
			mediaFiles.setIsLiked(true);
			mediaFiles.getLikedUsers().add(likedUsers); 
			mediaFileRepo.save(mediaFiles);
			
			response.setError("0");
			response.setMessage("user liked successfully");
			response.setData(mediaFiles);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);

		}
		else {
			count=mediaFiles.getLikes();
			LikedUsers likedUsers = likedUsersList.get(0);
			if(likedUsers.getMarkType().equals(Constant.UNLIKED) || likedUsers.getMarkType() == (Constant.UNLIKED)) {
				likedUsers.setUserName(userRegister.getUserName());
				likedUsers.setMarkType(Constant.LIKE);
				likedUsers.setUserId(userId);
				mediaFiles.setLikes(count+1);
				mediaFiles.setIsLiked(true);
				mediaFiles.getLikedUsers().add(likedUsers); 
				mediaFileRepo.save(mediaFiles);
				
				response.setError("0");
				response.setMessage("user liked successfully");
				response.setData(mediaFiles);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);

			}else {
				likedUsers.setUserName(userRegister.getUserName());
				likedUsers.setMarkType(Constant.UNLIKED);
				likedUsers.setUserId(userId);
				mediaFiles.setLikes(count-1);
				mediaFiles.setIsLiked(true);
				mediaFiles.getLikedUsers().add(likedUsers);
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
	public ResponseEntity<ResponseObject> totalRating(@RequestParam("fileid") String userfileid,
			@RequestParam("isRated") String isRated,@RequestParam("rateCount") String rateCounts,
			@RequestParam(value = "userId") int  userId) {

		int cRate = Integer.parseInt(rateCounts);
		if(userfileid.equals("") && userfileid == null && isRated.equals("") && isRated == null && rateCounts.equals("") && rateCounts == null) {

			response.setError("1");
			response.setMessage("wrong fileid, rateCount and isRated please enter correct value");
			response.setData(empty);
			response.setStatus("FAIL");
			return ResponseEntity.ok(response);

		}
		else {

			int fileid = 0;
			int rateCount = 0;
			boolean isRate = false;
			try {
				isRate = Boolean.parseBoolean(isRated);
				fileid = Integer.parseInt(userfileid);
				rateCount = Integer.parseInt(rateCounts);
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
			
			long rate=0;
			System.out.println(mediaFiles.getLikes());
			
			
			if(mediaFiles.getRating() == null || mediaFiles.getRating() == 0) {

				rate=0;

			} else{

				rate=mediaFiles.getRating();
			}

			if(likedUsersList.isEmpty()) {
				
				LikedUsers likedUsers=new LikedUsers();
				likedUsers.setUserName(userRegister.getUserName());
				likedUsers.setMarkType(Constant.RATE);
				likedUsers.setUserId(userId);
				likedUsers.setRating(rateCount);
				likedUsers.setTypeId(0);
				
				rate = rate+rateCount;
				mediaFiles.setRating(rate);
				mediaFiles.setIsRated(true);
				mediaFiles.getLikedUsers().add(likedUsers); 
				mediaFileRepo.save(mediaFiles);

				response.setError("0");
				response.setMessage("user rated with : "+cRate);
				response.setData(mediaFiles);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);

			}
			else {
				
				LikedUsers likedUsers=likedUsersList.get(0);
				
				/*
				 * if(isRate==true&& mediaFiles.getIsRated()==false ) {
				 * 
				 * 
				 * rate = rate+rateCount; mediaFiles.setRating(rate);
				 * mediaFiles.setIsRated(isRate); mediaFileRepo.save(mediaFiles);
				 * 
				 * response.setError("0"); response.setMessage("user rated with : "+rateCount);
				 * response.setData(mediaFiles); response.setStatus("SUCCESS"); return
				 * ResponseEntity.ok(response);
				 * 
				 * 
				 * } else {
				 */
						if(rate>=0) {
							rate = rate-likedUsers.getRating();
							rate = rate+rateCount;
							likedUsers.setUserName(userRegister.getUserName());
							likedUsers.setMarkType(Constant.RATE);
							likedUsers.setUserId(userId);
							likedUsers.setRating(rateCount);
							mediaFiles.getLikedUsers().add(likedUsers); 
							
						}

				mediaFiles.setIsRated(true);
				mediaFiles.setRating(rate);
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
	@PostMapping("/aboutUs")
	public ResponseEntity<ResponseObject> updateStatus(@RequestParam("aboutUs") String aboutUs,
			@RequestParam(value = "userid") String userid) throws ResourceNotFoundException {

		if(aboutUs.equals("") && aboutUs == null && userid.equals("") && userid == null ) {
 
			response.setError("1");
			response.setMessage("wrong aboutUs and userid please enter correct value");
			response.setData(empty);
			response.setStatus("FAIL");
			return ResponseEntity.ok(response);

		}
		else {

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

			List<UserProfile> profile = null;
			UserProfile updateDisplayName =null;


			profile = userprofilerepo.findById(id);
			if(!profile.isEmpty()) {
				profile.get(0).setAboutUser(aboutUs);
				profile.get(0).setProfileId(id);
				updateDisplayName = userprofilerepo.save(profile.get(0));

				response.setMessage("your status updated successfully");
				response.setData(updateDisplayName);
				response.setError("");
				response.setStatus("success");

				return ResponseEntity.ok(response);	

			}
			else {

				response.setMessage("user not available");
				response.setData(empty);
				response.setError("1");
				response.setStatus("fail");
				return ResponseEntity.ok(response);
			}
		}

	}
    @PostMapping("/profile")
    public  ResponseEntity<ResponseObject> saveProfile(@RequestParam("file") MultipartFile file,@RequestParam("DisplayName") String DisplayName,
    		@RequestParam(value = "userId") Integer  userId) {
     
    	UserProfile userProfile = fileStorageService.saveAllProfile(file,userId,DisplayName);
    	MediaFiles files=mediaFileRepo.getOne(Integer.valueOf(String.valueOf(userProfile.getFiles().get(userProfile.getFiles().size()-1).getFileId())));
       
       Object obj=new UploadFileResponse(files.getFilePath(), userProfile.getCurrentProfile(),
                file.getContentType(), file.getSize());
       if (!file.isEmpty()||userId!=null) {
			response.setMessage("your Profile Image updated successfully");

			response.setData(obj);
			response.setError("0");
			response.setStatus("SUCCESS");

			return ResponseEntity.ok(response);
		} else {
			response.setMessage("your Profile Image not updated");

			response.setData(empty);
			response.setError("1");
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);
		}
    }

    @PostMapping("/uploadFile")
	public ResponseEntity<ResponseObject> updateProfile(@RequestParam("file") MultipartFile file,@RequestParam(value = "userId") Integer userId) {
		
    	UserProfile userProfile = fileStorageService.saveProfile(file,userId);
		
		if (userProfile != null ) {
			MediaFiles files = mediaFileRepo.getOne(Integer.valueOf(
					String.valueOf(userProfile.getFiles().get(userProfile.getFiles().size() - 1).getFileId())));
			
			
			
			Object obj = new UploadFileResponse(userProfile.getCurrentProfile(), userProfile.getCurrentProfile(),
					file.getContentType(), file.getSize());
			if (!file.isEmpty() || userId != null) {
				response.setMessage("your Profile Image updated successfully");

				response.setData(obj);
				response.setError("0");
				response.setStatus("SUCCESS");

				return ResponseEntity.ok(response);
			} else {
				response.setMessage("your Profile Image not updated");

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

	@PostMapping("/upload") // //new annotation since 4.3
	public String singleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			return "redirect:uploadStatus";
		}

		try {

			// Get the file and save it somewhere
			byte[] bytes = file.getBytes();
			Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
			Files.write(path, bytes);

			redirectAttributes.addFlashAttribute("message",
					"You successfully uploaded '" + file.getOriginalFilename() + "'");

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/uploadStatus";
	}







}

