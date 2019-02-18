package com.technohertz;


import java.io.IOException;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.technohertz.common.Constant;
import com.technohertz.model.Empty;
import com.technohertz.model.LikedUsers;
import com.technohertz.model.MediaFiles;
import com.technohertz.model.UserProfile;
import com.technohertz.model.UserRegister;
import com.technohertz.payload.UploadFileResponse;
import com.technohertz.repo.MediaFileRepo;
import com.technohertz.repo.UserRegisterRepository;
import com.technohertz.service.impl.FileStorageService;
import com.technohertz.util.ResponseObject;

@RestController
@RequestMapping("/livefeed")
public class LiveFeedController {
	@Autowired
	private Empty empty;
    private static final Logger logger = LoggerFactory.getLogger(LiveFeedController.class);
	@Autowired
	private MediaFileRepo mediaFileRepo;
    @Autowired
    private FileStorageService fileStorageService;
	@Autowired
	private UserRegisterRepository registerRepository;
    @Autowired
	private ResponseObject response;
    
    @Autowired
	private Constant constant;

    @PostMapping("/video")
    public  ResponseEntity<ResponseObject> uploadVideos(@RequestParam("file") MultipartFile file,
    		@RequestParam(value = "userId") Integer  userId) {

    	UserProfile fileName = fileStorageService.storeFile(file, userId,constant.LIVEFEED);

     
        Object obj=new UploadFileResponse(fileName.getFiles().get(0).getFilePath(),fileName.getFiles().get(0).getFilePath(),
                file.getContentType(), file.getSize());
        if (!file.isEmpty()||userId!=null) {
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
    }
	@GetMapping("/listViewrs")
	public List<LikedUsers> getAllViewers(@RequestParam(value = "fileid") int  fileid) {
		return fileStorageService.getAll(fileid);
	}
	
	@PostMapping("/getAllVideos")
	public List<LikedUsers> getAllVideoById(@RequestParam(value = "userId") Integer  userId) {
		return fileStorageService.getAllVideoById(userId);
	}

	/* @GetMapping("/downloadFile/{fileName:.+}") */
    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName , HttpServletRequest request) {
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
		

		UserRegister userRegister =registerRepository.getOne(userId);
	    LikedUsers likedUsers=new LikedUsers();
		likedUsers.setUserName(userRegister.getUserName());
		likedUsers.setMarkType(Constant.LIKE);
		mediaFiles.getLikedUsers().add(likedUsers); 
		long count=0;

		if(mediaFiles.getLikes() == null) {
			count=0;
		} else{
			count=mediaFiles.getLikes();
		}
		if(isLiked==true && mediaFiles.getIsLiked()==false ) {
		count = count+1;
			mediaFiles.setLikes(count);
			mediaFiles.setIsLiked(isLiked);
			mediaFileRepo.save(mediaFiles);

			response.setError("0");
			response.setMessage("user liked successfully");
			response.setData(mediaFiles);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);

		}
		else {
	
		long totalcount=	mediaFiles.getLikes();
		count = totalcount-1;
			mediaFiles.setLikes(count);
			if(count>=0) {
			mediaFiles.setIsLiked(false);
			likedUsers.setMarkType("UNLIKED");
			mediaFileRepo.save(mediaFiles);
			}
			response.setError("1");
			response.setMessage("user unliked successfully");
			response.setData(empty);
			response.setStatus("FAIL");
			return ResponseEntity.ok(response);
		}
		}

	@SuppressWarnings("unused")
	@PostMapping("/rating")
	public ResponseEntity<ResponseObject> totalRating(@RequestParam("fileid") String userfileid,
			@RequestParam("isRated") String isRated,@RequestParam("rateCount") String rateCounts,
			@RequestParam(value = "userId") int  userId) {

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

			MediaFiles mediaFiles= mediaFileRepo.getById(fileid);

			UserRegister userRegister =registerRepository.getOne(userId);
			LikedUsers likedUsers=new LikedUsers();
			likedUsers.setUserName(userRegister.getUserName());
			likedUsers.setMarkType(Constant.RATE);
			mediaFiles.getLikedUsers().add(likedUsers); 

			//Long totalLikes=mediaFiles.getLikes();
			long rate=0;
			System.out.println(mediaFiles.getLikes());
			if(mediaFiles.getRating() == null) {

				rate=0;

			} else{

				rate=mediaFiles.getRating();
			}


			if(isRate==true&&  mediaFiles.getIsRated()==false ) {

			
				rate = rate+rateCount;
				mediaFiles.setRating(rate);
				mediaFiles.setIsRated(isRate);
				mediaFiles.setFileType("VIDEO");
				mediaFileRepo.save(mediaFiles);

				response.setError("0");
				response.setMessage("user rated with : "+rateCount);
				response.setData(mediaFiles);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);


			}
			else {

				long totalcount=	mediaFiles.getRating();
				rate = totalcount-Long.parseLong(rateCounts);
					mediaFiles.setLikes(rate);
					if(rate>=0) {
					mediaFiles.setIsRated(false);
					likedUsers.setMarkType("UNRATED");
					mediaFiles.setRating(rate);
				    mediaFileRepo.save(mediaFiles);
			}


				mediaFiles.setRating(rate);
				mediaFileRepo.save(mediaFiles);

				response.setError("1");
				response.setMessage("rating on image is not done");
				response.setData(empty);
				response.setStatus("FAIL");
				return ResponseEntity.ok(response);
			}
		}
	}

}
