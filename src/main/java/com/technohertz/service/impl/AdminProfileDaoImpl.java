package com.technohertz.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.technohertz.exception.FileStorageException;
import com.technohertz.exception.MyFileNotFoundException;
import com.technohertz.model.AdminProfile;
import com.technohertz.model.AdminRegister;
import com.technohertz.model.CardCategory;
import com.technohertz.model.Cards;
import com.technohertz.model.MediaFiles;
import com.technohertz.model.UserRegister;
import com.technohertz.repo.AdminProfileRepository;
import com.technohertz.repo.AdminRegisterRepository;
import com.technohertz.repo.CardCategoryRepository;
import com.technohertz.service.IAdminDao;
import com.technohertz.util.DateUtil;





@Service
public class AdminProfileDaoImpl implements IAdminDao {
	private static final String UPLOAD_DIRECTORY = "upload";
	Date currentDate = GregorianCalendar.getInstance().getTime();
	Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String sDate = formatter.format(currentDate);
	@Autowired 
	CardCategoryRepository cardCategoryRepository;
	@Autowired
	AdminRegisterRepository adminRegisterRepository;
	@Autowired
	private JavaMailSender sender;
	@Autowired
	AdminProfileRepository adminProfileRepository;
	@Autowired
	EntityManager entityManager;
	@Autowired
	private IAdminDao adminDao;
	
	@Autowired
	private DateUtil dateUtil;
	private  Path fileStorageLocation;
	
	org.hibernate.Transaction tx=null;
	@Transactional
	@SuppressWarnings("unchecked")
	@Override
	public List<UserRegister> getUserList() {
		/*
		 * Session session= HibernateUtil.getSessionFactory().openSession(); try {
		 * 
		 * tx=session.beginTransaction(); List<UserRegister> userRegisters=
		 * session.createQuery("from UserRegister").list(); tx.commit();
		 * session.close(); return userRegisters; } catch (HibernateException e) {
		 * if(tx!=null) { tx.rollback(); }
		 * 
		 * e.printStackTrace(); } return null;
		 */
		
		return entityManager
		.createQuery("from UserRegister").getResultList();
	}

	/*
	 * @Autowired public void getFilePath(FileStorageProperties
	 * fileStorageProperties) { this.fileStorageLocation =
	 * Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
	 * 
	 * try { Files.createDirectories(this.fileStorageLocation); } catch (Exception
	 * ex) { throw new
	 * FileStorageException("Could not create the directory where the uploaded files will be stored."
	 * , ex); } }
	 */
	@Transactional
	@Override
	public int deleteUser(int userId) {
		// TODO Auto-generated method stub
		UserRegister user = new UserRegister();
		user.setUserId(userId);
		entityManager
		.createNativeQuery("delete from UserRegister where userId=:userId")
		.setParameter("userId", userId).executeUpdate();
		
		 return userId;
	}

	@Override
	public List<UserRegister> getUserList(String date) {
		// 

		@SuppressWarnings("unchecked")
		List<UserRegister> userList=
		 (List<UserRegister>)
		 entityManager
				.createNativeQuery("SELECT * FROM user_register ORDER BY userid DESC LIMIT 10",UserRegister.class)
				.getResultList();
		return userList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserRegister> getUserAllList() {
		// TODO Auto-generated method stub
		return (List<UserRegister>)
				entityManager
				.createNativeQuery("SELECT * FROM user_register ORDER BY userid DESC LIMIT 50",UserRegister.class)
				.getResultList();
	}
	Date date = Calendar.getInstance().getTime();
	@Override
	public AdminRegister saveAdmin(String firstName, String lastName, String email, String password) {
		AdminRegister adminRegister =new AdminRegister();
		adminRegister.setFirstName(firstName);
		adminRegister.setLastName(lastName);
		adminRegister.setMailId(email);
		adminRegister.setPassword(password);
		adminRegister.setIsActive(true);
		adminRegister.setCreateDate(dateUtil.getDate());
		
		adminRegister.setLastModifiedDate(dateUtil.getDate());
		adminRegister.setSourceFrom("Laptop");
		return adminRegisterRepository.save(adminRegister);
	}

	@Override
	public List<AdminRegister> adminLogin(String email) {
		List<AdminRegister> adminRegister=null;
		try {
		List<AdminRegister> reg=	 entityManager
				.createQuery("SELECT a FROM AdminRegister a WHERE a.mailId=:email").setParameter("email", email).getResultList();
	
		if(!reg.isEmpty()) {
		return reg;
	}
	else {
		return adminRegister;
	}

	}catch (Exception e) {
		return adminRegister;
	}
		}
	
	
	/*
	 * @Transactional
	 * 
	 * @Override public CardCategory storeFile(CommonsMultipartFile file) {
	 * 
	 * String fileName = StringUtils .cleanPath( + System.currentTimeMillis() +
	 * getFileExtension(file));
	 * 
	 * String fileDownloadUri =
	 * ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
	 * .path(String.valueOf(fileName)).build().toUri().toString(); try { if
	 * (fileName.contains("..")) { }
	 * 
	 * MediaFiles mediaFile = new MediaFiles();
	 * 
	 * mediaFile.setFilePath(fileDownloadUri); mediaFile.setCreateDate(sDate);
	 * mediaFile.setLastModifiedDate(sDate);
	 * 
	 * long length = file.getSize();
	 * 
	 * 
	 * String uploadPath = "C:"+ File.separator + UPLOAD_DIRECTORY;
	 * 
	 * // creates the directory if it does not exist File uploadDir = new
	 * File(uploadPath); if (!uploadDir.exists()) { uploadDir.mkdir(); }
	 * 
	 * String filePath = uploadPath + File.separator + fileName; File storeFile =
	 * new File(filePath);
	 * 
	 * Files.copy(file.getInputStream(), storeFile.toPath(),
	 * StandardCopyOption.REPLACE_EXISTING);
	 * 
	 * 
	 * 
	 * 
	 * ht.getSessionFactory().getCurrentSession().save(mediaFile); return mediaFile;
	 * 
	 * } catch (Exception ex) {
	 * 
	 * ex.printStackTrace(); } return null; }
	 */
		private String getFileExtension(MultipartFile file) {
			String name = file.getOriginalFilename();
			int lastIndexOf = name.lastIndexOf(".");
			if (lastIndexOf == -1) {
				return ""; // empty extension
			}
			return name.substring(lastIndexOf);

	}

				@Override
				public CardCategory storeCards(MultipartFile file, Integer categoryId, String cardText, Character editable) {
					// TODO Auto-generated method stub
					
					List<CardCategory> cardCategoryList = entityManager.createQuery("from CardCategory where categoryId=:categoryId").setParameter("categoryId", categoryId).getResultList();
					
					Cards cards = new Cards();
					
					String fileName = StringUtils
							.cleanPath( + System.currentTimeMillis() + getFileExtension(file));
		
							String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
							.path(String.valueOf(fileName)).build().toUri().toString();
							try {
							if (fileName.contains("..")) {
							}
		
							
								
								cards.setFilePath(fileDownloadUri);
								cards.setCreateDate(dateUtil.getDate());
								cards.setEditable(editable);
								cards.setCardCategory(cardCategoryList.get(0));
								cards.setCardText(cardText);
								
								cardCategoryList.get(0).getCards().add(cards);
		
							long length = file.getSize();
		
		
							String uploadPath = "C:"+ File.separator + UPLOAD_DIRECTORY;
		
							// creates the directory if it does not exist
							File uploadDir = new File(uploadPath);
							if (!uploadDir.exists()) {
							uploadDir.mkdir();
							}
							
							String filePath = uploadPath + File.separator + fileName;
							File storeFile = new File(filePath);
		
							Files.copy(file.getInputStream(), storeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
							((HibernateTemplate) entityManager).save(cardCategoryList);
							return cardCategoryList.get(0);
		
							}catch(Exception ex){
								ex.printStackTrace();
								System.out.println(ex.getMessage());
								return null;
				}
			}
		
					
	/*
	 * public CardCategory storeCards(MultipartFile file, Integer categoryId,String
	 * cardText, Character editable) {
	 * 
	 * List<CardCategory> cardCategoryList =
	 * cardCategoryRepository.getById(categoryId);
	 * 
	 * Cards cards = new Cards(); String fileName = StringUtils
	 * .cleanPath(String.valueOf(categoryId) + System.currentTimeMillis() +
	 * getFileExtension(file)); String fileDownloadUri =
	 * ServletUriComponentsBuilder.fromCurrentContextPath() .path("/downloadFile/")
	 * .path(String.valueOf(fileName)) .toUriString();
	 * 
	 * try { if (fileName.contains("..")) { throw new
	 * FileStorageException("Sorry! Filename contains invalid path sequence " +
	 * file); }
	 * 
	 * 
	 * if (!cardCategoryList.isEmpty()) {
	 * 
	 * cards.setFilePath(fileDownloadUri); cards.setCreateDate(dateUtil.getDate());
	 * cards.setEditable(editable); cards.setCardCategory(cardCategoryList.get(0));
	 * cards.setCardText(cardText);
	 * 
	 * cardCategoryList.get(0).getCards().add(cards);
	 * 
	 * Path targetLocation = this.fileStorageLocation.resolve(fileName);
	 * Files.copy(file.getInputStream(), targetLocation,
	 * StandardCopyOption.REPLACE_EXISTING); return
	 * ht.getSessionFactory().getCurrentSession().save(cardCategoryList.get(0));
	 * 
	 * }else {
	 * 
	 * return null; }
	 * 
	 * } catch (IOException ex) {
	 * 
	 * throw new FileStorageException("Could not store file " + fileName +
	 * ". Please try again!", ex);
	 * 
	 * } }
	 */
		
			
			@Override
			@SuppressWarnings("unchecked")
			public List<CardCategory> getAllCategories(String categoryType) {
				return entityManager
				.createNativeQuery("from CardCategory where categoryType=:categoryType ORDER BY categoryId  DESC")
						.setParameter("categoryType", categoryType)
						.getResultList();
			}

			
			@SuppressWarnings("unchecked")
			public List<CardCategory> getAllGreetinsByCategoryId(Integer categoryId) {
			return entityManager
					.createNativeQuery("from CardCategory where categoryId=:categoryId",CardCategory.class)
					.setParameter("categoryId", categoryId).getResultList();
			
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

			String card="POSTCARD";
			@SuppressWarnings("unchecked")
			public List<CardCategory> getAllPostCards(Integer adminId) {
				return 	entityManager.createNativeQuery("SELECT * from card_category WHERE CATEGORY_TYPE=:card ",CardCategory.class)
					.setParameter("card", card)
						.getResultList();
				
				
				// TODO Auto-generated method stub
				
			}
			String gcard="GREETINGCARD";
			@SuppressWarnings("unchecked")
			public List<CardCategory> getAllGreatingCards(Integer adminId) {
				return 	entityManager.createNativeQuery("SELECT * from card_category WHERE CATEGORY_TYPE=:gcard ",CardCategory.class)
					.setParameter("gcard", gcard)
						.getResultList();
				
				
				// TODO Auto-generated method stub
				
			}
			@SuppressWarnings("unchecked")
			public List<Cards> getAllCards(Integer categoryId) {
				return 	entityManager.createNativeQuery("SELECT * from cards WHERE CATEGORY_ID=:categoryId ",Cards.class)
						.setParameter("categoryId", categoryId)
						.getResultList();
			}

			
			public String forgetPasswordMail(String mail)
			{
				MimeMessage message = sender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message);
				 List<AdminRegister> register = adminDao.adminLogin(mail);
				Long token=register.get(0).getToken();
				Integer adminId=register.get(0).getAdminId();
				try {
					helper.setTo(mail);
					helper.setText("Wel-Come to CRAZIAPP \n \n "+"  http://localhost:8080/resetPassword/"+token+ "/"+adminId+   "    \n This is an Authentication mail from CRAZIAPP please click on the link to verify your account");
					helper.setSubject("CRAZIAPP ADMIN");
				} catch (MessagingException e) {
					e.printStackTrace();
					return "Error while sending mail ..";
				}
				sender.send(message);
				return "Mail Sent Success!";
			}

			public AdminRegister resetPassword(Long token) {
			AdminRegister adminRegister=adminRegisterRepository.findByToken(token);
				return adminRegister;
			}
	
}