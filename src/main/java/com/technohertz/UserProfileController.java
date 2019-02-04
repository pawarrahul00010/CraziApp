package com.technohertz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.technohertz.exception.ResourceNotFoundException;
import com.technohertz.model.ExceptionHandle;
import com.technohertz.model.MediaFiles;
import com.technohertz.model.UserProfile;
import com.technohertz.payload.UploadFileResponse;
import com.technohertz.repo.MediaFileRepo;
import com.technohertz.repo.UserProfileRepository;
import com.technohertz.service.impl.FileStorageService;


@RestController
@RequestMapping("/profile")
public class UserProfileController {
	@Autowired
	private UserProfileRepository userprofilerepo;
	
	  @Autowired
	  private FileStorageService fileStorageService;
	  
	  @Autowired
	  private MediaFileRepo mediaFileRepo;
	  
	  private static String UPLOADED_FOLDER = "F://temp//";
		
	  
	  @PutMapping("/user/displayName/{id}")
		public ResponseEntity<ExceptionHandle> updateDisplayName(@RequestParam("displayName") String displayName,@PathVariable(value = "id") int  id) throws ResourceNotFoundException {
			ExceptionHandle exceptionHandle =new ExceptionHandle();
			List<UserProfile> profile = null;
			 UserProfile updateDisplayName =null;
			if(id!=0) {
				 profile =  userprofilerepo.findById(id);
					if(!profile.isEmpty()) {
				 
				 profile.get(0).setDisplayName(displayName);
					profile.get(0).setProfileId(id);
					 updateDisplayName = userprofilerepo.save(profile.get(0));
				exceptionHandle.setMassage("your Display name updated successfully");
				exceptionHandle.setObject(updateDisplayName);
				exceptionHandle.setError_code("");
				exceptionHandle.setStatus("success");
				return ResponseEntity.ok(exceptionHandle);	
				}
					else {
						exceptionHandle.setMassage("user not available");
						exceptionHandle.setObject("[]");
						exceptionHandle.setError_code("1");
						exceptionHandle.setStatus("fail");
						return ResponseEntity.ok(exceptionHandle);
					}
			}
			else {
				exceptionHandle.setMassage("something gone wrong");
				exceptionHandle.setObject("[]");
				exceptionHandle.setError_code("1");
				exceptionHandle.setStatus("fail");
				return ResponseEntity.ok(exceptionHandle);
			}
			
		}
	  
		@PutMapping("/user/aboutUs/{id}")
		public ResponseEntity<ExceptionHandle> updateStatus(@RequestParam("aboutUs") String aboutUs,@PathVariable(value = "id") int  id) throws ResourceNotFoundException {
			ExceptionHandle exceptionHandle =new ExceptionHandle();
			List<UserProfile> profile = null;
			 UserProfile updateDisplayName =null;
			if(id!=0) {
				 profile =  userprofilerepo.findById(id);
					if(!profile.isEmpty()) {
						 profile.get(0).setAboutUser(aboutUs);
							profile.get(0).setProfileId(id);
					 updateDisplayName = userprofilerepo.save(profile.get(0));
				exceptionHandle.setMassage("your status updated successfully");
				exceptionHandle.setObject(updateDisplayName);
				exceptionHandle.setError_code("");
				exceptionHandle.setStatus("success");
				return ResponseEntity.ok(exceptionHandle);	
				}
					else {
						exceptionHandle.setMassage("user not available");
						exceptionHandle.setObject("[]");
						exceptionHandle.setError_code("1");
						exceptionHandle.setStatus("fail");
						return ResponseEntity.ok(exceptionHandle);
					}
			}
			else {
				exceptionHandle.setMassage("something gone wrong");
				exceptionHandle.setObject("[]");
				exceptionHandle.setError_code("1");
				exceptionHandle.setStatus("fail");
				return ResponseEntity.ok(exceptionHandle);
			}
			
		}
	
		 @PutMapping("/uploadFile/{id}")
		    public UploadFileResponse updateProfile(@RequestParam("file") MultipartFile file,@PathVariable(value = "id") int id) {
		    	UserProfile userProfile = fileStorageService.saveProfile(file,id);
		    	MediaFiles files=mediaFileRepo.getOne(Integer.valueOf(String.valueOf(userProfile.getFiles().get(userProfile.getFiles().size()-1).getFileId())));
		        System.out.println(files);
		    	String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
		                .path("/downloadFile/")
		                .path(String.valueOf(files.getFileId()))
		                .toUriString();
		      
				return new UploadFileResponse(userProfile.getCurrentProfile(), fileDownloadUri,
		                file.getContentType(), file.getSize());
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
