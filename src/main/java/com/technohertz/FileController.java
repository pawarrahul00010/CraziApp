package com.technohertz;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.technohertz.common.Constant;
import com.technohertz.model.Empty;
import com.technohertz.model.GetImage;
import com.technohertz.model.LikedUsers;
import com.technohertz.model.MediaFiles;
import com.technohertz.model.UserProfile;
import com.technohertz.model.UserRegister;
import com.technohertz.payload.UploadFileResponse;
import com.technohertz.repo.MediaFileRepo;
import com.technohertz.repo.UserRegisterRepository;
import com.technohertz.service.IMediaFileService;
import com.technohertz.service.impl.FileStorageService;
import com.technohertz.util.ResponseObject;

@RestController
public class FileController {
	@Autowired
	private Empty empty;
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    
	@Autowired
	private MediaFileRepo mediaFileRepo;
	
	@Autowired
	private IMediaFileService mediaFileService;

    @Autowired
    private FileStorageService fileStorageService;
    
	@Autowired
	private UserRegisterRepository registerRepository;
	
	 @Autowired
	private Constant constant;

	
    @Autowired
	private ResponseObject response;
    
    
    @PostMapping("/uploadFile")
    public ResponseEntity<ResponseObject> uploadFile(@RequestParam("file") MultipartFile file,@RequestParam("fileType")String fileType,
    		@RequestParam(value = "userId") Integer  userId) {
     
    	UserProfile fileName = fileStorageService.storeFile(file, userId,fileType);

     
        if (fileName != null) {
			Object obj = new UploadFileResponse(fileName.getFiles().get(fileName.getFiles().size() - 1).getFilePath(),
					fileName.getFiles().get(0).getFilePath(), file.getContentType(), file.getSize());
			if (!file.isEmpty() || userId != null) {
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
    
    @PostMapping("/uploadMultipleFiles")
    public List<ResponseEntity<ResponseObject>> uploadMultipleFile(@RequestParam("files") MultipartFile[] files,@RequestParam("fileType")String fileType,
    		@RequestParam(value = "userId") int  userId) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file,fileType,userId))
                .collect(Collectors.toList());
    }
 
	/* @GetMapping("/downloadFile/{fileName:.+}") */
    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
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
			likedUsers.setRating(0);
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
				likedUsers.setRating(0);
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
				likedUsers.setRating(0);
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
	
	@PostMapping("/bookmark")
	public ResponseEntity<ResponseObject> bookmarking(@RequestParam("fileid") int fileid,
			@RequestParam(value = "userId") int  userId) {
		MediaFiles mediaFiles= mediaFileRepo.getById(fileid);
		UserRegister userRegister = registerRepository.getOne(userId);
		List<LikedUsers> likedUsersList= mediaFileService.getUserBookmarkByFileId(fileid, userId);

		if(likedUsersList.isEmpty()) {
			LikedUsers likedUsers = new LikedUsers();
			likedUsers.setUserName(userRegister.getUserName());
			likedUsers.setMarkType(Constant.BOOKMARK);
			likedUsers.setUserId(userId);
			likedUsers.setRating(0);
			mediaFiles.getLikedUsers().add(likedUsers); 
			mediaFileRepo.save(mediaFiles);
			
			response.setError("0");
			response.setMessage("user Bookmarked successfully");
			response.setData(mediaFiles);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
		}
		else {
			
			LikedUsers likedUsers = likedUsersList.get(0);
			
			response.setError("0");
			response.setMessage("user already Bookmarked successfully");
			response.setData(likedUsers);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
			
		}
	
		}
	
	@PostMapping("/getbookmark")
	public ResponseEntity<ResponseObject> getBookmarked(@RequestParam(value = "userId") int  userId) {

		List<MediaFiles> mediaFilesList= mediaFileService.getBookmarksByUserId(userId);
		
		List<GetImage> image=new ArrayList<GetImage>();


		if(mediaFilesList.isEmpty()) {
						
			response.setError("0");
			response.setMessage("user does not Bookmarked ");
			response.setData(empty);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
		}
		else {
			
			for(MediaFiles mediaFiles :mediaFilesList) {
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
	
	}
	
    @SuppressWarnings({ "static-access", "unused" })
	@RequestMapping(value ="/greet" ,method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<ResponseObject> uploadVideos(@RequestParam("file") MultipartFile file,
    		@RequestParam(value = "userId") Integer  userId,@RequestParam(value = "saySomething") String  saySomething) {

    	UserProfile fileName = fileStorageService.storeLiveFeedFile(file,saySomething, userId,constant.GREETINGCARD);

     
        Object obj=new UploadFileResponse(fileName.getFiles().get(fileName.getFiles().size()-1).getFilePath(),fileName.getFiles().get(0).getFilePath(),
                file.getContentType(), file.getSize());
        
        if (fileName != null) {
			if (!file.isEmpty() || userId != null) {
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
	
	@GetMapping("/getAllGreet")
	public ResponseEntity<ResponseObject> getAllProfilesById() {
		
		List<MediaFiles> likedUsers=fileStorageService.getAllGreetings();
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


}
