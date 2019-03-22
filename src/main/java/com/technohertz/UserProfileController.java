package com.technohertz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import com.technohertz.common.Constant;
import com.technohertz.exception.ResourceNotFoundException;
import com.technohertz.model.Empty;
import com.technohertz.model.GetImage;
import com.technohertz.model.LikedUsers;
import com.technohertz.model.MediaFiles;
import com.technohertz.model.PendoraBox;
import com.technohertz.model.UserProfile;
import com.technohertz.model.UserRegister;
import com.technohertz.payload.UploadFileResponse;
import com.technohertz.repo.MediaFileRepo;
import com.technohertz.repo.UserProfileRepository;
import com.technohertz.repo.UserRegisterRepository;
import com.technohertz.service.IMediaFileService;
import com.technohertz.service.impl.FileStorageService;
import com.technohertz.util.CommonUtil;
import com.technohertz.util.ResponseObject;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

	@Autowired
	private UserProfileRepository userprofilerepo;

	@Autowired
	private CommonUtil commonUtil;

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
	public ResponseEntity<ResponseObject> getAllProfilesById(
			@RequestParam(value = "userId", required = false) Integer userId) {

		if (userId == null) {

			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else {

			List<MediaFiles> likedUsers = fileStorageService.getAllProfileById(userId);
			List<GetImage> image = new ArrayList<GetImage>();

			for (MediaFiles mediaFiles : likedUsers) {
				GetImage img = new GetImage();
				img.setUser(mediaFiles.getFilePath());
				img.setFileId(mediaFiles.getFileId());
				image.add(img);
			}

			response.setError("0");
			response.setMessage("successfully fetched");
			response.setData(image);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
		}
	}

	@PostMapping("/displayName")
	public ResponseEntity<ResponseObject> updateDisplayName(
			@RequestParam(value = "displayName", required = false) String displayName,
			@RequestParam(value = "profileid", required = false) String profileid) throws ResourceNotFoundException {

		if (displayName == null) {

			response.setError("1");
			response.setMessage("'displayName' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else if (profileid == null) {

			response.setError("1");
			response.setMessage("'profileid' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else {

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
			UserProfile updateDisplayName = null;

			profile = userprofilerepo.findById(id);
			if (!profile.isEmpty()) {

				profile.get(0).setDisplayName(displayName);
				profile.get(0).setProfileId(id);
				updateDisplayName = userprofilerepo.save(profile.get(0));

				try {
					UpdateRequest request = new UpdateRequest(profileid)
						    .setDisplayName(displayName);

						UserRecord userRecord = FirebaseAuth.getInstance().updateUser(request);
						System.out.println("Successfully updated user: " + userRecord.getUid());
				} catch (FirebaseAuthException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
				response.setMessage("your Display name updated successfully");
				response.setData(updateDisplayName);
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

	@PostMapping("/likes")
	public ResponseEntity<ResponseObject> totalLikes(@RequestParam(value = "fileid", required = false) Integer fileid,
			@RequestParam(value = "isLiked", required = false) Boolean isLiked,
			@RequestParam(value = "userId", required = false) Integer userId) {

		if (isLiked == null) {

			response.setError("1");
			response.setMessage("'isLiked' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else if (fileid == null) {

			response.setError("1");
			response.setMessage("'fileid' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else if (userId == null) {

			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else {

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

				count = mediaFiles.getLikes();
				LikedUsers likedUsers = likedUsersList.get(0);

				fileStorageService.deleteLike(likedUsers);

				mediaFiles.setLikes(count - 1);
				if (count <= 0) {

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
	public ResponseEntity<ResponseObject> totalRating(
			@RequestParam(value = "fileid", required = false) String userfileid,
			@RequestParam(value = "isRated", required = false) String isRated,
			@RequestParam(value = "rateCount", required = false) String rateCounts,
			@RequestParam(value = "userId", required = false) Integer userId) {

		if (isRated == null) {

			response.setError("1");
			response.setMessage("'isRated' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else if (rateCounts == null) {

			response.setError("1");
			response.setMessage("'rateCount' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else if (userfileid == null) {

			response.setError("1");
			response.setMessage("'fileid' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else if (userId == null) {

			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else {

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

			List<LikedUsers> likedUsersList = mediaFileService.getUserRatingByFileId(fileid, userId);

			MediaFiles mediaFiles = mediaFileRepo.getById(fileid);

			List<LikedUsers> likedUserlist = mediaFileService.getRatingByFileId(fileid);

			Float rating = 0.0f;

			if (rating == null || rating == 0) {

				rating = 0.0f;

			}

			if (likedUsersList.isEmpty()) {

				rating = commonUtil.getupdateRating(likedUserlist, rateCount, 0);

				LikedUsers likedUsers = new LikedUsers();
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
				response.setMessage("user rated with : " + cRate);
				response.setData(mediaFiles);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);

			} else {

				LikedUsers likedUsers = likedUsersList.get(0);
				rating = commonUtil.getupdateRating(likedUserlist, rateCount, likedUsers.getTypeId());
				if (rating >= 0) {

					likedUsers.setUserName(userRegister.getUserName());
					likedUsers.setMarkType(Constant.RATE);
					likedUsers.setUserId(userId);
					likedUsers.setRating(rateCount);
					mediaFiles.getLikedUsers().add(likedUsers);

				}

				mediaFiles.setIsRated(true);
				mediaFiles.setRating(rating);
				mediaFileRepo.save(mediaFiles);

				response.setError("0");
				response.setMessage("rating updated with " + cRate);
				response.setData(mediaFiles);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);
			}
		}
	}

	@SuppressWarnings("unused")
	@PostMapping("/aboutUs")
	public ResponseEntity<ResponseObject> updateStatus(
			@RequestParam(value = "aboutUs", required = false) String aboutUs,
			@RequestParam(value = "userid", required = false) String userid) throws ResourceNotFoundException {

		if (aboutUs == null) {

			response.setError("1");
			response.setMessage("'aboutUs' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else if (userid == null) {

			response.setError("1");
			response.setMessage("'userid' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else {
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
			UserProfile updateDisplayName = null;

			profile = userprofilerepo.findById(id);
			if (!profile.isEmpty()) {
				profile.get(0).setAboutUser(aboutUs);
				profile.get(0).setProfileId(id);
				updateDisplayName = userprofilerepo.save(profile.get(0));

				response.setMessage("your status updated successfully");
				response.setData(updateDisplayName);
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

	@PostMapping("/profile")
	public ResponseEntity<ResponseObject> saveProfile(
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "DisplayName", required = false) String DisplayName,
			@RequestParam(value = "aboutUser", required = false) String aboutUser,
			@RequestParam(value = "userId", required = false) Integer userId) {

		if (file == null) {

			response.setError("1");
			response.setMessage("'file' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else if (DisplayName == null) {

			response.setError("1");
			response.setMessage("'DisplayName' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else if (userId == null) {

			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else {

			UserProfile userProfile = fileStorageService.saveAllProfile(file, userId, DisplayName, aboutUser);
			MediaFiles files = mediaFileRepo.getOne(Integer.valueOf(
					String.valueOf(userProfile.getFiles().get(userProfile.getFiles().size() - 1).getFileId())));

			Object obj = new UploadFileResponse(files.getFilePath(), userProfile.getCurrentProfile(),
					file.getContentType(), file.getSize());

			if (!file.isEmpty()) {
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
	}

	@PostMapping("/uploadFile")
	public ResponseEntity<ResponseObject> updateProfile(
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "userId", required = false) Integer userId) {

		if (file == null) {

			response.setError("1");
			response.setMessage("'file' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else if (userId == null) {

			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else {

			UserProfile userProfile = fileStorageService.saveProfile(file, userId);

			if (userProfile != null) {
				MediaFiles files = mediaFileRepo.getOne(Integer.valueOf(
						String.valueOf(userProfile.getFiles().get(userProfile.getFiles().size() - 1).getFileId())));

				Object obj = new UploadFileResponse(userProfile.getCurrentProfile(), userProfile.getCurrentProfile(),
						file.getContentType(), file.getSize());
				if (!file.isEmpty()) {
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
			} else {
				response.setMessage("User does not exist please register first");

				response.setData(empty);
				response.setError("1");
				response.setStatus("FAIL");

				return ResponseEntity.ok(response);
			}
		}

	}

	@PostMapping("/upload") // //new annotation since 4.3
	public String singleFileUpload(@RequestParam(value = "file", required = false) MultipartFile file,
			RedirectAttributes redirectAttributes) {

		if (file == null) {

			redirectAttributes.addFlashAttribute("'file' is empty or null please check");

			return "redirect:/uploadStatus";

		} else {
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

	@SuppressWarnings("unused")
	@PostMapping("/saveToPendora") // //new annotation since 4.3
	public ResponseEntity<ResponseObject> pendoraBox(@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "userID", required = false) String userID,
			@RequestParam(value = "Message", required = false) String Message) {
		if (userID == null) {

			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");

			return ResponseEntity.ok(response);

		} else {

			UserProfile userProfile = fileStorageService.savePendoraBox(file, Integer.parseInt(userID), Message);
			
			
			if (file != null) {
				if (userProfile != null) {
					MediaFiles files = mediaFileRepo.getOne(Integer.valueOf(
							String.valueOf(userProfile.getFiles().get(userProfile.getFiles().size() - 1).getFileId())));

					Object obj = new UploadFileResponse(userProfile.getCurrentProfile(),
							userProfile.getCurrentProfile(), file.getContentType(), file.getSize());
					if (!file.isEmpty()) {
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
			} else {
				response.setMessage("message added to Pendora Box successfully");
				
				response.setData(userProfile.getPendoraBox().get(userProfile.getPendoraBox().size()-1).getMessageOrFile());
				response.setError("0");
				response.setStatus("SUCCESS");

				return ResponseEntity.ok(response);
			}
			if (userID == null) {
				response.setMessage("User does not exist please register first");

				response.setData(empty);
				response.setError("1");
				response.setStatus("FAIL");

				return ResponseEntity.ok(response);
			}

		}
		return null;
	}
	@SuppressWarnings("unused")
	@GetMapping("/getPendoraMessages/{userId}")
	public ResponseEntity<ResponseObject> getPendoraMessage(@PathVariable("userId") String userId)
	{
		List<PendoraBox> box =new ArrayList<PendoraBox>();
		List<PendoraBox> pendoraBoxs= fileStorageService.getMessagesById(userId);

		for(PendoraBox boxs :pendoraBoxs) {
			PendoraBox pendoraBox = new PendoraBox();
			pendoraBox.setPendoraId(boxs.getPendoraId());
			pendoraBox.setMessageOrFile(boxs.getMessageOrFile());
			box.add(pendoraBox);
			
		}
		
		if(box == null) {
			
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}
		else {
			response.setError("0");
			response.setMessage("User List are");
			response.setData(box);
			response.setStatus("Success");
			
			return ResponseEntity.ok(response);
		}
	}
	
	@SuppressWarnings("unused")
	@PostMapping("/deletePendoraMessages/{userId}")
	public ResponseEntity<ResponseObject> deletePendoraMessage(@PathVariable("userId") String userId)
	{
		Integer id= fileStorageService.deleteMessagesById(userId);

		
		
		if(id == null) {
			
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}
		else {
			response.setError("0");
			response.setMessage("User List are");
			response.setData(id);
			response.setStatus("Success");
			
			return ResponseEntity.ok(response);
		}
	}
}
