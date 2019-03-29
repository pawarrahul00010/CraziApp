package com.technohertz;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technohertz.common.Constant;
import com.technohertz.model.CardBookmarkUsers;
import com.technohertz.model.CardCategory;
import com.technohertz.model.Cards;
import com.technohertz.model.Empty;
import com.technohertz.model.GetImage;
import com.technohertz.model.GroupPoll;
import com.technohertz.model.GroupProfile;
import com.technohertz.model.LikedUsers;
import com.technohertz.model.MediaFiles;
import com.technohertz.model.SharedMedia;
import com.technohertz.model.UserProfile;
import com.technohertz.model.UserRegister;
import com.technohertz.payload.UploadFileResponse;
import com.technohertz.repo.MediaFileRepo;
import com.technohertz.repo.UserProfileRepository;
import com.technohertz.repo.UserRegisterRepository;
import com.technohertz.service.ICardService;
import com.technohertz.service.IMediaFileService;
import com.technohertz.service.IUserRegisterService;
import com.technohertz.service.impl.FileStorageService;
import com.technohertz.util.ChatUserListDash;
import com.technohertz.util.CommonUtil;
import com.technohertz.util.DateUtil;
import com.technohertz.util.MediaDash;
import com.technohertz.util.MediaDashResponse;
import com.technohertz.util.PollDash;
import com.technohertz.util.PollResList;
import com.technohertz.util.ResponseObject;

@RestController
public class FileController {
	@Autowired
	private Empty empty;
	
	@Autowired
	private DateUtil dateUtil;


	@Autowired
	private UserProfileRepository userProfileRepository;
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    
	@Autowired
	private MediaFileRepo mediaFileRepo;
	
	@Autowired
	private EntityManager entitymanager;
	
	@Autowired
	private ICardService cardService;
	
	@Autowired
	private IMediaFileService mediaFileService;

    @Autowired
    private FileStorageService fileStorageService;
    
	@Autowired
	private UserRegisterRepository registerRepository;
	
	@Autowired
	private IUserRegisterService userRegisterService;

	 @Autowired
	private Constant constant;
		
	@Autowired
	private CommonUtil commonUtil;

    @Autowired
	private ResponseObject response;
    
    private HttpSession session;
    
	static String FB_BASE_URL="https://craziapp-3c02b.firebaseio.com";
    
    
    @SuppressWarnings("unused")
	@PostMapping("/uploadFile")
    public ResponseEntity<ResponseObject> uploadFile(@RequestParam(value="file", required=false) MultipartFile file,
    		@RequestParam(value="fileType", required=false)String fileType,
    		@RequestParam(value = "userId", required=false) Integer  userId,@RequestParam(value = "themeName", required=false) String  themeName) {
     
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
			
		}else if(userId == null) {
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
			
    	UserProfile fileName = fileStorageService.storeFile(file, userId,fileType,themeName);
     
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
    }
    
    @PostMapping("/uploadMultipleFiles")
    public List<ResponseEntity<ResponseObject>> uploadMultipleFile(@RequestParam("files") MultipartFile[] files,@RequestParam("fileType")String fileType,
    		@RequestParam(value = "userId") int  userId,@RequestParam(value = "themeName") String  themeName) {
    	
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file,fileType,userId,themeName))
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
	public ResponseEntity<ResponseObject> totalLikes(@RequestParam(value="fileid", required=false) Integer fileid,
			@RequestParam(value="isLiked", required=false) Boolean isLiked,
			@RequestParam(value = "userId", required=false) Integer  userId) {
	
		if(fileid == null) {
			response.setError("1");
			response.setMessage("'fileid' is empty or null please check");
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
			
		}else if(userId == null) {
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
		
		MediaFiles mediaFiles= mediaFileRepo.getById(fileid);
		UserRegister userRegister = registerRepository.getOne(userId);
		List<LikedUsers> likedUsersList= mediaFileService.getUserLikesByFileId(fileid, userId);
		
		long count=0;

		if(likedUsersList.isEmpty()) {
			
			count=mediaFiles.getLikes();
			LikedUsers likedUsers = new LikedUsers();
			likedUsers.setUserName(userRegister.getUserName());
			likedUsers.setMarkType(Constant.LIKE);
			likedUsers.setRating(0.0f);
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
	public ResponseEntity<ResponseObject> totalRating(@RequestParam(value="fileid", required=false) String userfileid,
			@RequestParam(value="isRated", required=false) String isRated,
			@RequestParam(value="rateCount", required=false) String rateCounts,
			@RequestParam(value = "userId", required=false) Integer  userId) {
		
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
	
	@PostMapping("/bookmark")
	public ResponseEntity<ResponseObject> bookmarking(@RequestParam(value="fileid", required = false) Integer fileid,
			@RequestParam(value = "userId", required = false) Integer  userId) {
		
		if(userId == null) {
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}
		else if(fileid == null) {
			response.setError("1");
			response.setMessage("'fileid' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
		
		MediaFiles mediaFiles= mediaFileRepo.getById(fileid);
		UserRegister userRegister = registerRepository.getOne(userId);
		List<LikedUsers> likedUsersList= mediaFileService.getUserBookmarkByFileId(fileid, userId);

		if(likedUsersList.isEmpty()) {
			LikedUsers likedUsers = new LikedUsers();
			likedUsers.setUserName(userRegister.getUserName());
			likedUsers.setMarkType(Constant.BOOKMARK);
			likedUsers.setCreateDate(dateUtil.getDate());
			likedUsers.setUserId(userId);
			likedUsers.setRating(0.0f);
			mediaFiles.setIsBookMarked(true);
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
	}
	
	@PostMapping("/getbookmark")
	public ResponseEntity<ResponseObject> getBookmarked(@RequestParam(value = "userId", required = false) Integer  userId) {

		if(userId == null) {
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
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
				
				for(LikedUsers likedUsers:mediaFiles.getLikedUsers()) {
					if((likedUsers.getUserId()==userId || userId.equals(likedUsers.getUserId())) && ("BOOKMARKED".equals(likedUsers.getMarkType()) || likedUsers.getMarkType()=="BOOKMARKED")) {
						
						img.setCreateDate(likedUsers.getCreateDate());
					}
				}
				
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
	}
	
	@PostMapping("/getmedia")
	public ResponseEntity<ResponseObject> getAllMedia(@RequestParam(value = "userId", required = false) Integer  userId) {
		
		if(userId == null) {
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
			
			List<UserRegister> register = new ArrayList<UserRegister>();
			
			register = userRegisterService.getById(userId);
			
			if(!register.isEmpty()) {
				
				List<MediaFiles> mediaFilesList= mediaFileService.getAllMediaByUserId(register.get(0).getProfile().getProfileId());
				if(mediaFilesList.isEmpty()) {
					
					response.setError("0");
					response.setMessage("user does not have any files");
					response.setData(empty);
					response.setStatus("SUCCESS");
					return ResponseEntity.ok(response);
				}
				else {
					
					response.setError("0");	
					response.setMessage("successfully fetched");
					response.setData(mediaFilesList);
					response.setStatus("SUCCESS");
					return ResponseEntity.ok(response);
					
				}
			}else {
				response.setError("1");
				response.setMessage("user does not exist");
				response.setData(empty);
				response.setStatus("FAIL");
				
				return ResponseEntity.ok(response);
			}
			
		
		}
	
	}

	  
    @SuppressWarnings("unused")
   	@PostMapping("/shreImage")
       public ResponseEntity<ResponseObject> shreImage(@RequestParam(value="file", required=false) MultipartFile file,
       		@RequestParam(value="fileType", required=false)String fileType,
       		@RequestParam(value = "fromUserId", required=false) String  fromUserId,@RequestParam(value = "toUserId", required=false) String  toUserId) {
        
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
   			
   		}else if(fromUserId == null) {
   			response.setError("1");
   			response.setMessage("'userId' is empty or null please check");
   			response.setData(empty);
   			response.setStatus("FAIL");
   			
   			return ResponseEntity.ok(response);
   			
   		}else {
   			
       	UserProfile fileName = fileStorageService.shareFile(file, fromUserId,toUserId,fileType);
        SharedMedia sharedMedia =new SharedMedia();
        
           if (fileName != null) {
        	   
   			Object obj = new UploadFileResponse(fileName.getFiles().get(fileName.getFiles().size() - 1).getFilePath(),
   					fileName.getFiles().get(0).getFilePath(), file.getContentType(), file.getSize());
   			
   			if (!file.isEmpty() || fromUserId != null) {
   				sharedMedia.setFilePath(fileName.getFiles().get(fileName.getFiles().size() - 1).getFilePath());
				sharedMedia.setFormUser(fromUserId);
				sharedMedia.setToUser(toUserId);
				sharedMedia.setFileType(fileType);
				sharedMedia.setFileId(fileName.getFiles().get(fileName.getFiles().size() - 1).getFileId());
				sharedMedia.setFileName(file.getOriginalFilename());
				sharedMedia.setShareDate(dateUtil.getDate());
				fileName.getMedia().add(sharedMedia);
				userProfileRepository.save(fileName);
   				response.setMessage("your File is Shared successfully");
   				
   				response.setData(sharedMedia);
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
       


    @SuppressWarnings({ "static-access", "unused" })
	@RequestMapping(value ="/greet" ,method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<ResponseObject> uploadVideos(@RequestParam(value ="file", required = false) MultipartFile file,
    		@RequestParam(value = "userId", required = false) Integer  userId,
    		@RequestParam(value = "saySomething", required = false) String  saySomething) {

    	if(userId == null) {
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}
    	else if(file == null) {
			response.setError("1");
			response.setMessage("'file' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}
    	else if(saySomething == null) {
			response.setError("1");
			response.setMessage("'saySomething' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
		}
    	else {
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
	
    }
	@PostMapping("/getAllGreet")
	public ResponseEntity<ResponseObject> getAllGreetingByCategoryId(
    		@RequestParam(value = "categoryId", required = false) Integer  categoryId) {
		
		if(categoryId == null) {
			response.setError("1");
			response.setMessage("'categoryId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}
    	else {
			List<CardCategory> cardList=fileStorageService.getAllGreetinsByCategoryId(categoryId);
	
			response.setError("0");	
			response.setMessage("successfully fetched");
			response.setData(cardList.get(0).getCards());
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
			
		}

	}
	
	@PostMapping("/getAllCategory")
	public ResponseEntity<ResponseObject> getAllCategory(@RequestParam(value = "categoryType", required = false) String  categoryType) {
		

		if(categoryType == null) {
			response.setError("1");
			response.setMessage("'categoryType' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}
    	else {
	
			List<CardCategory> CardCategoryList=fileStorageService.getAllCategories(categoryType);
	
			response.setError("0");	
			response.setMessage("successfully fetched");
			response.setData(CardCategoryList);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
    	}
	}

	
	@PostMapping("/cardBookmark")
	public ResponseEntity<ResponseObject> cardBookmarking(@RequestParam(value="cardId", required = false) Integer cardId,
			@RequestParam(value = "userId", required = false) Integer  userId,
			@RequestParam(value = "cardType", required = false) String  cardType) {
		
		if(userId == null) {
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}
		else if(cardId == null) {
			response.setError("1");
			response.setMessage("'cardId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(cardType == null) {
			response.setError("1");
			response.setMessage("'cardType' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
		
			Cards cards= cardService.getById(cardId);
		UserRegister userRegister = registerRepository.getOne(userId);
		List<CardBookmarkUsers> cardBookmarkUsersList= cardService.getUserBookmarkByCardId(cardId, userId, cardType);

		if(cardBookmarkUsersList.isEmpty()) {
			
			CardBookmarkUsers cardBookmarkUsers = new CardBookmarkUsers();
			cardBookmarkUsers.setUserName(userRegister.getUserName());
			cardBookmarkUsers.setCreateDate(dateUtil.getDate());
			cardBookmarkUsers.setCardType(cardType);
			cardBookmarkUsers.setUserId(userId);
			cards.getCardBookmarkUserList().add(cardBookmarkUsers); 
			cardService.save(cards);
			
			response.setError("0");
			response.setMessage("user Bookmarked successfully");
			response.setData(cards);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
		}
		else {
			
			CardBookmarkUsers cardBookmarkUsers = cardBookmarkUsersList.get(0);
			
			response.setError("0");
			response.setMessage("user already Bookmarked successfully");
			response.setData(cardBookmarkUsers);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
			
		}
	
		}
	}
	
	@PostMapping("/getCardBookmark")
	public ResponseEntity<ResponseObject> getCardBookmarks(@RequestParam(value = "userId", required = false) Integer  userId,
			@RequestParam(value = "cardType", required = false) String  cardType) {

		if(userId == null) {
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(cardType == null) {
			response.setError("1");
			response.setMessage("'cardType' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
		List<Cards> cardsList= cardService.getCardBookmarksByUserId(userId, cardType);
		
		List<GetImage> image=new ArrayList<GetImage>();


		if(cardsList.isEmpty()) {
						
			response.setError("0");
			response.setMessage("user does not Bookmarked any cards");
			response.setData(empty);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
		}
		else {
			
				/*
				 * for(Cards cards :cardsList) { GetImage img = new GetImage();
				 * img.setUser(cards.getFilePath()); img.setfileId(cards.getFileId());
				 * image.add(img);
				 * 
				 * }
				 */
			response.setError("0");	
			response.setMessage("successfully fetched");
			response.setData(cardsList);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
			
		}
		}
	}
	
	
	@SuppressWarnings({ "unchecked" })
	@PostMapping("/getmediaOfDashboard")
	public ResponseEntity<ResponseObject> getMediaDashBoard(@RequestParam(value = "userId", required = false) Integer  userId,
			@RequestParam(value = "mediaType", required = false) String  mediaType,
			@RequestParam(value = "daysType", required = false) String  daysType) {

		if(userId == null) {
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(daysType == null) {
			response.setError("1");
			response.setMessage("'daysType' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(mediaType == null) {
			response.setError("1");
			response.setMessage("'mediaType' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
			
			List<SharedMedia> mediaFilesList = new ArrayList<SharedMedia>();
			
			if(daysType=="WEEK" || "WEEK".equalsIgnoreCase(daysType)) {
				
				mediaFilesList= fileStorageService.getmediaByUserIdandMediaTypeandWeekType(userId, mediaType, daysType);
				
			}else if(daysType=="MONTH" || "MONTH".equalsIgnoreCase(daysType)){
				
				 mediaFilesList= fileStorageService.getmediaByUserIdandMediaTypeandMonthType(userId, mediaType, daysType);
				
			}else {
				
			 mediaFilesList= fileStorageService.getmediaByUserIdandMediaTypeandYearType(userId, mediaType, daysType);
				
			}
			
						
			String hql="FROM UserRegister";
			List<UserRegister> userList=entitymanager.createQuery(hql).getResultList();
			
			Map<String, String> nameList = getNameList(userList);
			Map<String, String> profileList = getProfileList(userList);
			
			List<MediaDash> mediaList = new ArrayList<MediaDash>();
		
//			for(SharedMedia share : mediaFilesList) {
//				List<String> fileList = new ArrayList<String>();
//				
//				for(SharedMedia sharedMedia1: mediaFilesList) {
//					
//					for(SharedMedia sharedMedia: mediaFilesList ) {
//						
//						if(sharedMedia1.getToUser() == sharedMedia.getToUser() || sharedMedia1.getToUser().equals(sharedMedia.getToUser())
//								||sharedMedia1.getFormUser() == sharedMedia.getFormUser() || sharedMedia1.getFormUser().equals(sharedMedia.getToUser())) {
//							fileList.add(sharedMedia1.getFilePath());
//						}
//					}
//				}
//			
//			}
			
			Map<String , List<String>> fileMap= new HashMap<String, List<String>>();
			
			List<String> fileList = new ArrayList<String>();
			
			for(SharedMedia share : mediaFilesList) {
				
				for(SharedMedia sharedMedia: mediaFilesList) {
					
					if(sharedMedia.getToUser().equals(share.getToUser()))
						
						fileList.add(share.getFilePath());
					
				}
				fileMap.put(String.valueOf(share.getToUser()), fileList);
			}
			
			Map<String, SharedMedia> mediaMap = new HashMap<String, SharedMedia>();
			
			for(SharedMedia media : mediaFilesList) {
				
				mediaMap.put(media.getToUser(), media);
			}
			
			for(SharedMedia sharedMedia : mediaFilesList) {
			
				
				String id = sharedMedia.getToUser();
				
				MediaDash media = new MediaDash();
				//media.setFileId(sharedMedia.getFileId());
				//media.setFileName(sharedMedia.getFileName());
				//media.setFilePathList(mediaMap.get(id));
				//media.setFileType(sharedMedia.getFileType());
				media.setToUser(nameList.get(id));
				media.setReceiverProfile(profileList.get(id));
				media.setFromUser(Integer.valueOf(sharedMedia.getFormUser()));
				media.setShareDate(sharedMedia.getShareDate());
				media.setToUserId(Integer.valueOf(id));
				
				mediaList.add(media);
			}
			
		if(mediaList.isEmpty()) {
						
			response.setError("0");
			response.setMessage("user does not have any files in last "+daysType);
			response.setData(mediaList);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
		}
		else {
			
			
			response.setError("0");	
			response.setMessage("successfully fetched of "+mediaType+"'s of last "+daysType);
			response.setData(mediaList);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
			
		}
		}
	}
	
	
	@PostMapping("/getmediaOfDashboardByToUser")
	public ResponseEntity<ResponseObject> getMediaDashBoardByUserId(@RequestParam(value = "userId", required = false) Integer  userId,
			@RequestParam(value = "mediaType", required = false) String  mediaType,
			@RequestParam(value = "toUserId", required = false) Integer  toUserId,
			@RequestParam(value = "daysType", required = false) String  daysType) {

		if(userId == null) {
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(toUserId == null) {
			response.setError("1");
			response.setMessage("'toUserId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(daysType == null) {
			response.setError("1");
			response.setMessage("'daysType' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(mediaType == null) {
			response.setError("1");
			response.setMessage("'mediaType' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
			
			List<SharedMedia> mediaFilesList = new ArrayList<SharedMedia>();
			
			if(daysType=="WEEK" || "WEEK".equalsIgnoreCase(daysType)) {
				
				mediaFilesList= fileStorageService.getmediaByUserIdandMediaTypeandWeekType(userId, mediaType, daysType, toUserId);
				
			}else if(daysType=="MONTH" || "MONTH".equalsIgnoreCase(daysType)){
				
				 mediaFilesList= fileStorageService.getmediaByUserIdandMediaTypeandMonthType(userId, mediaType, daysType, toUserId);
				
			}else {
				
			 mediaFilesList= fileStorageService.getmediaByUserIdandMediaTypeandYearType(userId, mediaType, daysType, toUserId);
				
			}
			
						
			/*
			 * String hql="FROM UserRegister"; List<UserRegister>
			 * userList=entitymanager.createQuery(hql).getResultList();
			 * 
			 * Map<String, String> nameList = getNameList(userList); Map<String, String>
			 * profileList = getProfileList(userList);
			 */
			
			List<MediaDashResponse> mediaList = new ArrayList<MediaDashResponse>();
		
			Map<String , List<String>> fileMap= new HashMap<String, List<String>>();
			
			List<String> fileList = new ArrayList<String>();
			
			for(SharedMedia share : mediaFilesList) {
				
				for(SharedMedia sharedMedia: mediaFilesList) {
					
					if(sharedMedia.getToUser().equals(share.getToUser()))
						
						fileList.add(share.getFilePath());
					
				}
				fileMap.put(String.valueOf(share.getToUser()), fileList);
			}
			

			
			Map<String, SharedMedia> mediaMap = new HashMap<String, SharedMedia>();
			
			for(SharedMedia media : mediaFilesList) {
				
				mediaMap.put(media.getToUser(), media);
			}
			
			for(SharedMedia sharedMedia : mediaFilesList) {
			
				String id = sharedMedia.getToUser();
				
				MediaDashResponse media = new MediaDashResponse();
				media.setFileId(sharedMedia.getFileId());
				media.setFileName(sharedMedia.getFileName());
				media.setFilePath(sharedMedia.getFilePath());
				media.setFileType(sharedMedia.getFileType());
				media.setShareDate(sharedMedia.getShareDate());
				
				mediaList.add(media);
			}
			
		if(mediaList.isEmpty()) {
						
			response.setError("0");
			response.setMessage("user does not have any files in last "+daysType);
			response.setData(mediaList);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
		}
		else {
			
			
			response.setError("0");	
			response.setMessage("successfully fetched of "+mediaType+"'s of last "+daysType);
			response.setData(mediaList);
			response.setStatus("SUCCESS");
			return ResponseEntity.ok(response);
			
		}
		}
	}
	
	
	private Map<String, String> getProfileList(List<UserRegister> userList) {
		
	Map<String, String> userNameList = new HashMap<String, String>();
		
		for( UserRegister user: userList) {
			
			UserProfile userProfile = user.getProfile();
			
			userNameList.put(String.valueOf(user.getUserId()), userProfile.getCurrentProfile());
		}
		return userNameList;
	}

	private Map<String, String> getNameList(List<UserRegister> userList) {
		
		Map<String, String> userNameList = new HashMap<String, String>();
		
		for( UserRegister user: userList) {
			
			userNameList.put(String.valueOf(user.getUserId()), user.getUserName());
		}
		return userNameList;
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/getPollOfDashboard")
	public ResponseEntity<ResponseObject> getPollDashBoard(@RequestParam(value = "userId", required = false) Integer  userId,
			@RequestParam(value = "daysType", required = false) String  daysType) {
		
		if(userId == null) {
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(daysType == null) {
			response.setError("1");
			response.setMessage("'daysType' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
			
			List<GroupPoll> pollList = new ArrayList<GroupPoll>();
			
			if(daysType=="WEEK" || "WEEK".equalsIgnoreCase(daysType)) {
				
				pollList= fileStorageService.getPollsByUserIdandWeekType(userId, daysType);
				
			}else if(daysType=="MONTH" || "MONTH".equalsIgnoreCase(daysType)){
				
				pollList= fileStorageService.getPollsByUserIdandMonthType(userId, daysType);
				
			}else {
				
				pollList= fileStorageService.getPollsByUserIdandYearType(userId, daysType);
				
			}
			
			String hql="FROM GroupProfile";
			List<GroupProfile> groupList=entitymanager.createQuery(hql).getResultList();
			
			Map<Integer, String> groupName = new HashMap<Integer, String>();
			Map<Integer, String> groupPro = new HashMap<Integer, String>();
			
			for(GroupProfile groupProfile :groupList) {
				
				groupName.put(groupProfile.getGroupId(), groupProfile.getDisplayName());
				groupPro.put(groupProfile.getGroupId(), groupProfile.getCurrentProfile());
			}
			
			List<PollDash> pollList1 = new ArrayList<PollDash>();
			for(GroupPoll groupPoll: pollList) {
				
				PollDash pollDash = new PollDash();
				//pollDash.setPollId(groupPoll.getPollId());
				pollDash.setCreateDate(groupPoll.getCreateDate());
				//pollDash.setPollName(groupPoll.getPollName());
				//pollDash.setPollOptions(groupPoll.getPollOptions());
				//pollDash.setExpiryDate(groupPoll.getExpiryDate());
				//pollDash.setCreatedBy(groupPoll.getCreatedBy());
				pollDash.setGroupId(groupPoll.getGroupId());
				//pollDash.setPollStatus(groupPoll.getPollStatus());
				pollDash.setGroupName(groupName.get(groupPoll.getGroupId()));
				pollDash.setGroupProf(groupPro.get(groupPoll.getGroupId()));
				pollList1.add(pollDash);
			}
			
			if(pollList.isEmpty()) {
				
				response.setError("0");
				response.setMessage("user does not created any polls in last "+daysType);
				response.setData(pollList1);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);
			}
			else {
				
				
				response.setError("0");	
				response.setMessage("successfully fetched of poll's of last "+daysType);
				response.setData(pollList1);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);
				
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/getPollOfDashboardbyGroupId")
	public ResponseEntity<ResponseObject> getPollDashResult(@RequestParam(value = "userId", required = false) Integer  userId,
			@RequestParam(value = "groupId", required = false) Integer  groupId,
			@RequestParam(value = "daysType", required = false) String  daysType) {
		
		if(userId == null) {
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(groupId == null) {
			response.setError("1");
			response.setMessage("'groupId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(daysType == null) {
			response.setError("1");
			response.setMessage("'daysType' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
			
			List<GroupPoll> pollList = new ArrayList<GroupPoll>();
			
			if(daysType=="WEEK" || "WEEK".equalsIgnoreCase(daysType)) {
				
				pollList= fileStorageService.getPollsByUserIdandWeekType(userId, daysType,groupId);
				
			}else if(daysType=="MONTH" || "MONTH".equalsIgnoreCase(daysType)){
				
				pollList= fileStorageService.getPollsByUserIdandMonthType(userId, daysType,groupId);
				
			}else {
				
				pollList= fileStorageService.getPollsByUserIdandYearType(userId, daysType,groupId);
				
			}
			
			String hql="FROM GroupProfile";
			List<GroupProfile> groupList=entitymanager.createQuery(hql).getResultList();
			
			Map<Integer, String> groupName = new HashMap<Integer, String>();
			
			for(GroupProfile groupProfile :groupList) {
				
				groupName.put(groupProfile.getGroupId(), groupProfile.getDisplayName());
			}
			
			List<PollResList> pollList1 = new ArrayList<PollResList>();
			for(GroupPoll groupPoll: pollList) {
				
				PollResList pollDash = new PollResList();
				pollDash.setPollId(groupPoll.getPollId());
				//pollDash.setCreateDate(groupPoll.getCreateDate());
				pollDash.setPollName(groupPoll.getPollName());
				pollDash.setPollOptions(groupPoll.getPollOptions());
				pollDash.setExpiryDate(groupPoll.getExpiryDate());
				//pollDash.setCreatedBy(groupPoll.getCreatedBy());
				//pollDash.setGroupId(groupPoll.getGroupId());
				pollDash.setPollStatus(groupPoll.getPollStatus());
				//pollDash.setGroupName(groupName.get(groupPoll.getGroupId()));
				pollList1.add(pollDash);
			}
			
			if(pollList1.isEmpty()) {
				
				response.setError("0");
				response.setMessage("user does not created any polls in last "+daysType);
				response.setData(pollList1);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);
			}
			else {
				
				
				response.setError("0");	
				response.setMessage("successfully fetched of poll's of last "+daysType);
				response.setData(pollList1);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);
				
			}
		}
	}
	
	
	
	@PostMapping("/getChatOfDashboard")
	public ResponseEntity<ResponseObject> getChatDashBoard(@RequestParam(value = "userName", required = false) String  userName,
			@RequestParam(value = "daysType", required = false) String  daysType) {
		
		if(userName == null) {
			response.setError("1");
			response.setMessage("'userName' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(daysType == null) {
			response.setError("1");
			response.setMessage("'daysType' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
			
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


			
			List<GroupPoll> pollList = new ArrayList<GroupPoll>();
			
			if(daysType=="WEEK" || "WEEK".equalsIgnoreCase(daysType)) {
				
				//pollList= getChatByUserNameandWeekType(userName, daysType);
				
			}else if(daysType=="MONTH" || "MONTH".equalsIgnoreCase(daysType)){
				
				//pollList= getChatByUserNameandMonthType(userName, daysType);
				
			}else {
				
				//pollList= getChatByUserNameandYearType(userName, daysType);
				
			}
			
			
			
			
			// Get a reference to our posts
			//final FirebaseDatabase database = FirebaseDatabase.getInstance();
			DatabaseReference ref = database.getReference("chat");

			// Attach a listener to read the data at our posts reference
			
			// final ChatUserDash chatting = new ChatUserDash();
			List<Object> objectList = new ArrayList<Object>();
			ref.addValueEventListener(new ValueEventListener() {
			  @Override
			  public void onDataChange(DataSnapshot dataSnapshot) {
				   dataSnapshot.getValue();
			    System.out.println("--------@@@ Chatting @@@------"+ dataSnapshot.getValue());
			    
			    response.setError("0");	
				response.setMessage("successfully fetched of poll's of last "+daysType);
				response.setData(dataSnapshot.getValue());
				response.setStatus("SUCCESS");
				objectList.add(dataSnapshot.getValue());
				
			  }

			  @Override
			  public void onCancelled(DatabaseError databaseError) {
			    System.out.println("The read failed: " + databaseError.getCode());
			    
			    response.setError("0");	
				response.setMessage("successfully fetched of poll's of last "+daysType);
				response.setData(databaseError.getCode());
				response.setStatus("SUCCESS");
				objectList.add(databaseError.getCode());
			  }
			});
			
			//return ResponseEntity.ok(response);
			/*Map<String, Object> groups = new HashMap<>();
			groups.put(groupProfile.getGroupId().toString(), new GroupFirebase(groupProfile.getGroupId(), groupProfile.getDisplayName(), userRegister.getMobilNumber(), "https://cdn.techgyd.com/Whatsapp-DP-for-Group-4.png"));
		
			//ref.child("group").setValueAsync("group", new GroupFirebase(groupProfile.getGroupId(), groupProfile.getDisplayName(), userRegister.getUserName(), groupProfile.getGroupMember()));
			//ref.getDatabase().getReference().push().c.getReferenceFromUrl(FB_BASE_URL).setValueAsync(groups);
			database.getReference().child("groups").updateChildrenAsync(groups);

			
			String hql="FROM GroupProfile";
			List<GroupProfile> groupList=entitymanager.createQuery(hql).getResultList();
			
			Map<Integer, String> groupName = new HashMap<Integer, String>();
			Map<Integer, String> groupPro = new HashMap<Integer, String>();
			
			for(GroupProfile groupProfile :groupList) {
				
				groupName.put(groupProfile.getGroupId(), groupProfile.getDisplayName());
				groupPro.put(groupProfile.getGroupId(), groupProfile.getCurrentProfile());
			}
			
			List<PollDash> pollList1 = new ArrayList<PollDash>();
			for(GroupPoll groupPoll: pollList) {
				
				PollDash pollDash = new PollDash();
				//pollDash.setPollId(groupPoll.getPollId());
				pollDash.setCreateDate(groupPoll.getCreateDate());
				//pollDash.setPollName(groupPoll.getPollName());
				//pollDash.setPollOptions(groupPoll.getPollOptions());
				//pollDash.setExpiryDate(groupPoll.getExpiryDate());
				//pollDash.setCreatedBy(groupPoll.getCreatedBy());
				pollDash.setGroupId(groupPoll.getGroupId());
				//pollDash.setPollStatus(groupPoll.getPollStatus());
				pollDash.setGroupName(groupName.get(groupPoll.getGroupId()));
				pollDash.setGroupProf(groupPro.get(groupPoll.getGroupId()));
				pollList1.add(pollDash);
			}
			
			if(chatting.isEmpty()) {
				
				response.setError("0");
				response.setMessage("user does not created any polls in last "+daysType);
				response.setData(chatting);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);
			}
			else {*/
				
				
				response.setError("0");	
				response.setMessage("successfully fetched of poll's of last "+daysType);
				response.setData(objectList);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);
				
			//}
		}
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/getChatOfDashboardbySenderUser")
	public ResponseEntity<ResponseObject> getChatDashResult(@RequestParam(value = "userId", required = false) Integer  userId,
			@RequestParam(value = "groupId", required = false) Integer  groupId,
			@RequestParam(value = "daysType", required = false) String  daysType) {
		
		if(userId == null) {
			response.setError("1");
			response.setMessage("'userId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(groupId == null) {
			response.setError("1");
			response.setMessage("'groupId' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else if(daysType == null) {
			response.setError("1");
			response.setMessage("'daysType' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
		}else {
			
			List<GroupPoll> pollList = new ArrayList<GroupPoll>();
			
			if(daysType=="WEEK" || "WEEK".equalsIgnoreCase(daysType)) {
				
				//pollList= fileStorageService.getPollsByUserIdandWeekType(userId, daysType,groupId);
				
			}else if(daysType=="MONTH" || "MONTH".equalsIgnoreCase(daysType)){
				
				//pollList= fileStorageService.getPollsByUserIdandMonthType(userId, daysType,groupId);
				
			}else {
				
				//pollList= fileStorageService.getPollsByUserIdandYearType(userId, daysType,groupId);
				
			}
			
			String hql="FROM GroupProfile";
			List<GroupProfile> groupList=entitymanager.createQuery(hql).getResultList();
			
			Map<Integer, String> groupName = new HashMap<Integer, String>();
			
			for(GroupProfile groupProfile :groupList) {
				
				groupName.put(groupProfile.getGroupId(), groupProfile.getDisplayName());
			}
			
			List<PollResList> pollList1 = new ArrayList<PollResList>();
			for(GroupPoll groupPoll: pollList) {
				
				PollResList pollDash = new PollResList();
				pollDash.setPollId(groupPoll.getPollId());
				//pollDash.setCreateDate(groupPoll.getCreateDate());
				pollDash.setPollName(groupPoll.getPollName());
				pollDash.setPollOptions(groupPoll.getPollOptions());
				pollDash.setExpiryDate(groupPoll.getExpiryDate());
				//pollDash.setCreatedBy(groupPoll.getCreatedBy());
				//pollDash.setGroupId(groupPoll.getGroupId());
				pollDash.setPollStatus(groupPoll.getPollStatus());
				//pollDash.setGroupName(groupName.get(groupPoll.getGroupId()));
				pollList1.add(pollDash);
			}
			
			if(pollList1.isEmpty()) {
				
				response.setError("0");
				response.setMessage("user does not created any polls in last "+daysType);
				response.setData(empty);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);
			}
			else {
				
				
				response.setError("0");	
				response.setMessage("successfully fetched of poll's of last "+daysType);
				response.setData(pollList1);
				response.setStatus("SUCCESS");
				return ResponseEntity.ok(response);
				
			}
		}
	}
}
