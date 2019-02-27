package com.technohertz.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.technohertz.common.Constant;
import com.technohertz.exception.FileStorageException;
import com.technohertz.exception.MyFileNotFoundException;
import com.technohertz.model.GroupProfile;
import com.technohertz.model.LikedUsers;
import com.technohertz.model.MediaFiles;
import com.technohertz.model.UserContact;
import com.technohertz.model.UserProfile;
import com.technohertz.model.UserRegister;
import com.technohertz.repo.GroupProfileRepository;
import com.technohertz.repo.MediaFileRepo;
import com.technohertz.repo.UserContactRepository;
import com.technohertz.repo.UserProfileRepository;
import com.technohertz.service.IUserContactService;
import com.technohertz.service.IUserRegisterService;
import com.technohertz.util.DateUtil;
import com.technohertz.util.FileStorageProperties;

@Service
public class FileStorageService {

	private final Path fileStorageLocation;
	@Autowired
	public EntityManager entityManager;

	@Autowired
	private MediaFileRepo mediaFileRepo;
	
	@Autowired
	private IUserContactService userContactService;

	
	@Autowired
	private IUserRegisterService userRegisterService; 

	
	@Autowired
	private UserProfileRepository userprofileRepo;
	@Autowired
	private UserContactRepository userContactRepo;

	@Autowired
	private GroupProfileRepository groupProfileRepository;
	
	@Autowired
	private GroupProfileServiceImpl groupProfileServiceImpl;

	@Autowired
	FileStorageProperties fileStorageProperty;

	@Autowired
	DateUtil dateUtil = new DateUtil();
	
	@Autowired
	Constant Constant ;

	@Autowired
	public FileStorageService(FileStorageProperties fileStorageProperties) {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}
	public GroupProfile saveGroupProfile(MultipartFile file, int userId,String fileType) {
		String fileName = StringUtils.cleanPath(String.valueOf(userId)+System.currentTimeMillis()+getFileExtension(file));
		   String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/downloadFile/")
	                .path(String.valueOf(fileName))
	                .toUriString();
		
		   List<GroupProfile> groupProfileList = null;
		try {
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}
			groupProfileList = groupProfileServiceImpl.findById(userId);
			if(groupProfileList.isEmpty()) {
				GroupProfile groupProfile = new GroupProfile();
			MediaFiles mediaFile = new MediaFiles();
			mediaFile.setFilePath(fileDownloadUri);
			mediaFile.setFileType(fileType);
			mediaFile.setIsLiked(false);
			mediaFile.setLikes(0l);
			mediaFile.setRating(0l);
			mediaFile.setIsRated(false);
			mediaFile.setIsBookMarked(false);
			mediaFile.setCreateDate(dateUtil.getDate());
			mediaFile.setLastModifiedDate(dateUtil.getDate());
			groupProfile.getFiles().add(mediaFile);
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return groupProfileRepository.save(groupProfile);
			}else {
				return  groupProfileList.get(0);
			}
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}
	

	public UserProfile storeFile(MultipartFile file, int userId,String fileType) {
		
			List<UserProfile> userprofile = null;
			userprofile = userprofileRepo.findById(userId);
			
					if (!userprofile.isEmpty()) {
					String fileName = StringUtils
							.cleanPath(String.valueOf(userId) + System.currentTimeMillis() + getFileExtension(file));
					String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
							.path(String.valueOf(fileName)).toUriString();
					try {
						if (fileName.contains("..")) {
							throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
						}
					
						MediaFiles mediaFile = new MediaFiles();
						mediaFile.setFilePath(fileDownloadUri);
						mediaFile.setFileType(fileType);
						mediaFile.setIsLiked(false);
						mediaFile.setLikes(0l);
						mediaFile.setRating(0l);
						mediaFile.setIsRated(false);
						mediaFile.setIsBookMarked(false);
						mediaFile.setCreateDate(dateUtil.getDate());
						mediaFile.setLastModifiedDate(dateUtil.getDate());
						userprofile.get(0).getFiles().add(mediaFile);
						Path targetLocation = this.fileStorageLocation.resolve(fileName);
						Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
	
						
						return userprofileRepo.save(userprofile.get(0));
						
				} catch (IOException ex) {
					
					throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
					
				}
			} else {
				
				return null;
			} 
		}
	
	
	public UserProfile saveProfile(MultipartFile file, int userId) {

		List<UserProfile> userprofile = null;
		

		userprofile = userprofileRepo.findById(userId);
		
		List<UserRegister> userList = userRegisterService.getById(userId);
		
		MediaFiles mfile = new MediaFiles();
		String fileName = StringUtils
				.cleanPath(String.valueOf(userId) + System.currentTimeMillis() + getFileExtension(file));
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/downloadFile/")
				.path(String.valueOf(fileName))
				.toUriString();
		
		try {
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + file);
			}
			
			List<UserContact> contactList= userContactService.getContactByMobileNumber(userList.get(0).getMobilNumber());
			
					if (!userList.isEmpty()) {
						
						if (!userprofile.isEmpty()) {
							mfile.setFilePath(fileDownloadUri);
							mfile.setIsLiked(false);
							mfile.setLikes(0l);
							mfile.setRating(0l);
							mfile.setIsRated(false);
							mfile.setIsBookMarked(false);
							mfile.setCreateDate(dateUtil.getDate());
							mfile.setLastModifiedDate(dateUtil.getDate());
							mfile.setFileType("PROFILE");
							userprofile.get(0).setProfileId(userId);
							userprofile.get(0).setCurrentProfile(fileDownloadUri);
							userprofile.get(0).getFiles().add(mfile);
							Path targetLocation = this.fileStorageLocation.resolve(fileName);
							Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
							
							for(UserContact userContact : contactList) {
								
								userContact.setProfilePic(fileDownloadUri);
								
								userContactRepo.save(userContact);
							}
							
							return userprofileRepo.save(userprofile.get(0));
							
						}else {
							
							return null;
						}
						
					}else {
					
					return null;
					
					}
				} catch (IOException ex) {
				
					throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
			
				}
		}
	
	public UserProfile saveAllProfile(MultipartFile file, int userId,String DisplayName) {
		// Normalize file name

		
		
		String fileName = StringUtils
				.cleanPath(String.valueOf(userId) + System.currentTimeMillis() + getFileExtension(file));
		
		 String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/downloadFile/")
	                .path(String.valueOf(String.valueOf(fileName)))
	                .toUriString();

		try {
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + file);
			}
			List<UserProfile> userprofile = null;
			MediaFiles mfile = new MediaFiles();
			userprofile = userprofileRepo.findById(userId);
			mfile.setFilePath(fileDownloadUri);
			mfile.setIsLiked(false);
			mfile.setIsRated(false);
			mfile.setLikes(0l);
			mfile.setRating(0l);
			mfile.setIsBookMarked(false);
			mfile.setCreateDate(dateUtil.getDate());
			mfile.setLastModifiedDate(dateUtil.getDate());
			mfile.setFileType("PROFILE");
			userprofile.get(0).setProfileId(userId);
			userprofile.get(0).setCurrentProfile(fileDownloadUri);
			userprofile.get(0).setDisplayName(DisplayName);
			userprofile.get(0).getFiles().add(mfile);
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return userprofileRepo.save(userprofile.get(0));
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	public GroupProfile savegroupProfile(MultipartFile file, int userId) {
		String fileName = StringUtils
				.cleanPath(String.valueOf(userId) + System.currentTimeMillis() + getFileExtension(file));
		
		 String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/downloadFile/")
	                .path(String.valueOf(fileName))
	                .toUriString();
		
		try {
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + file);
			}
			List<GroupProfile> groupProfile = null;
			MediaFiles mfile = new MediaFiles();
			groupProfile = groupProfileServiceImpl.findById(userId);
			mfile.setFilePath(fileDownloadUri);
			// mfile.setFileId(id);
			mfile.setCreateDate(dateUtil.getDate());
			mfile.setLastModifiedDate(dateUtil.getDate());
			mfile.setIsLiked(false);
			mfile.setLikes(0l);
			mfile.setFileType(Constant.GROUPPROFILE);
			mfile.setRating(0l);
			mfile.setIsRated(false);
			mfile.setIsBookMarked(false);
			
			groupProfile.get(0).setCurrentProfile(fileDownloadUri);
			groupProfile.get(0).getFiles().add(mfile);
			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return groupProfileRepository.save(groupProfile.get(0));
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	public Resource loadFile(String filename) {
		try {
			final Path rootLocation = Paths.get(fileStorageProperty.getUploadDir()).toAbsolutePath().normalize();
			Path file = rootLocation.resolve(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("FAIL!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("FAIL!");
		}
	}

	public void deleteAll() {
		final Path rootLocation = Paths.get(fileStorageProperty.getUploadDir()).toAbsolutePath().normalize();
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }

	private String getFileExtension(MultipartFile file) {
		String name = file.getOriginalFilename();
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return ""; // empty extension
		}
		return name.substring(lastIndexOf);
	}
	@SuppressWarnings("unchecked")
	@Cacheable(value="viewerCache",key="#fileid",unless="#result==null")
	public List<LikedUsers> getAll(int fileid) {
	return entityManager.createNativeQuery("select l.user_name from liked_users l where l.file_id=:fileid and type=:type")
						.setParameter("fileid", fileid)
						.setParameter("type", Constant.LIKE)
						.getResultList();
	
	}
	
	@Transactional
	@SuppressWarnings("unchecked")
	public List<MediaFiles> getAllProfileById(Integer userId) {
		return entityManager.createNativeQuery("select * from media_files  where usr_det_id=:userId AND File_Type=:PROFILE ORDER BY file_id  DESC",MediaFiles.class)
				.setParameter("userId", userId)	.setParameter("PROFILE", "PROFILE")
				.getResultList();
	}
	
	@Transactional
	@SuppressWarnings({ "unchecked", "static-access" })
	public List<MediaFiles> getAllGreetings() {
		return entityManager.createNativeQuery("select * from media_files  where File_Type=:GREETING ORDER BY file_id  DESC",MediaFiles.class)
				.setParameter("GREETING", Constant.GREETING)
				.getResultList();
	}

	
	   @SuppressWarnings("unchecked")  
	   public List<Integer> getFileId(int userId) { 
		   return entityManager.
			  createNativeQuery("select l.file_id from media_files l where l.usr_det_id=:userId AND File_Type=:PROFILE"
			  )  .setParameter("userId", userId).setParameter("PROFILE", "PROFILE") 
			  .getResultList(); 
		   }
	 	

		@SuppressWarnings("unchecked")
		//@Cacheable(value="videoCache",key="#userId",unless="#result==null")
		public List<MediaFiles> getAllVideoById(Integer userId) {
			return entityManager.createNativeQuery("select * from media_files  where usr_det_id=:userId AND File_Type=:VIDEO ORDER BY file_id  DESC",MediaFiles.class)
					.setParameter("userId", userId)	.setParameter("VIDEO", "LIVEFEED")
					.getResultList();
		}
		
		public UserProfile storeLiveFeedFile(MultipartFile file,String text , int userId,String fileType) {
			
			
			List<UserProfile> userprofile = null;
			userprofile = userprofileRepo.findById(userId);
			
			if (!userprofile.isEmpty()) {
				String fileName = StringUtils
						.cleanPath(String.valueOf(userId) + System.currentTimeMillis() + getFileExtension(file));
				String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
						.path(String.valueOf(fileName)).toUriString();
				try {
					if (fileName.contains("..")) {
						throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
					}
					MediaFiles mediaFile = new MediaFiles();
					mediaFile.setFilePath(fileDownloadUri);
					mediaFile.setFileType(fileType);
					mediaFile.setIsLiked(false);
					mediaFile.setLikes(0l);
					mediaFile.setRating(0l);
					mediaFile.setText(text);
					mediaFile.setIsRated(false);
					mediaFile.setIsBookMarked(false);
					mediaFile.setCreateDate(dateUtil.getDate());
					mediaFile.setLastModifiedDate(dateUtil.getDate());
					userprofile.get(0).getFiles().add(mediaFile);
					Path targetLocation = this.fileStorageLocation.resolve(fileName);
					Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

					return userprofileRepo.save(userprofile.get(0));
				} catch (IOException ex) {
					throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
				} 
			}
			else {
				return null;
			}
		}
}
