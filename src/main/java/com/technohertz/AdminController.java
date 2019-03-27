package com.technohertz;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.technohertz.exception.MyFileNotFoundException;
import com.technohertz.model.AdminRegister;
import com.technohertz.model.CardCategory;
import com.technohertz.model.Cards;
import com.technohertz.model.UserRegister;
import com.technohertz.payload.UploadFileResponse;
import com.technohertz.repo.CardCategoryRepository;
import com.technohertz.service.IAdminDao;
import com.technohertz.service.impl.AdminProfileDaoImpl;
import com.technohertz.service.impl.FileStorageService;
import com.technohertz.util.FileStorageProperties;

@Controller
public class AdminController {

	@Autowired
	private FileStorageService fileStorageService;
	@Autowired
	AdminProfileDaoImpl adminProfileDaoImpl;
	@Autowired
	private IAdminDao adminDao;
	@Autowired
	EntityManager entityManager;
	@Autowired
	private HttpSession session;

	private static final String UPLOAD_DIRECTORY = "upload";

	@Autowired
	FileStorageProperties fileStorageProperty;

	/*
	 * @Autowired private FileStorageService fileStorageService;
	 */

	public static int BUFFER_SIZE = 1024 * 100;

	private Path fileStorageLocation;

	@RequestMapping("/users")
	public String getAllUsers(Model model) {

		List<UserRegister> userList = adminDao.getUserList();
		List<UserRegister> userLists = adminDao.getUserList(String.valueOf(new Date()));
		model.addAttribute("usernote", userLists);
		model.addAttribute("userList", userList);

		return "tables";
	}

	@RequestMapping("/dash")
	public String getDashboard(Model model) {

		return "indx";
	}

	@RequestMapping("/index")
	public String getindex(Model model) {

		return "index";
	}

	@RequestMapping("/card")
	public String saveCard(Model model) {

		return "index";
	}

	@RequestMapping("/register")
	public String getRegister(Model model) {

		return "register";
	}

	@RequestMapping(value = "/registerAdmin", method = RequestMethod.POST)
	public String registerAdmin(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
			@RequestParam("email") String email, @RequestParam("password") String password) {

		@SuppressWarnings("unused")
		AdminRegister register = adminDao.saveAdmin(firstName, lastName, email, password);
		return "index";

	}

	@RequestMapping(value = "/adminLogin", method = RequestMethod.POST)
	public String adminindex(@RequestParam("email") String email, @RequestParam("password") String password,
			HttpServletRequest request) {
		List<AdminRegister> register = adminDao.adminLogin(email, password);

		if (register != null) {

			if (register.get(0).getMailId().equals(email) && register.get(0).getPassword().equals(password)
					&& register.get(0).getIsActive() == true) {
				session = request.getSession();
				session.setAttribute("loggeduser", register.get(0));

				return "indx";
			} else {

				return "index";
			}

		} else {

			return "index";
		}

	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public String adminLogout(HttpServletRequest request) {
		session = request.getSession();
		session.removeAttribute("loggeduser");

		return "redirect:index";

	}

	@RequestMapping("/forgot")
	public String getForgot(Model model) {

		return "forgot-password";
	}

	@RequestMapping("/404")
	public String get404(Model model) {

		return "404";
	}

	@SuppressWarnings("unused")
	@RequestMapping("/delete")
	public String deleteUserById(@RequestParam("userId") Integer userId, Model map) {

		Integer useriD = adminDao.deleteUser(userId);

		map.addAttribute("msg", "user deleted successfully" + userId);

		return "redirect:users";

	}

	@RequestMapping("/getUsers")
	public String getNotification(Model map) {

		List<UserRegister> userList = adminDao.getUserList(String.valueOf(new Date()));
		map.addAttribute("usernote", userList);

		return "tables";

	}

	@RequestMapping("/getAllnotes")
	public String getAllNotification(Model map) {

		List<UserRegister> userList = adminDao.getUserAllList();
		System.out.println(userList);
		map.addAttribute("usernote", userList);

		return "tables";

	}

	@RequestMapping("/getGreetCat")
	public String getGreetingCategories(@RequestParam String categoryType, Model map) {

		List<CardCategory> CardCategoryList = adminDao.getAllCategories(categoryType);

		map.addAttribute("cardList", CardCategoryList);

		return "category";

	}

	@RequestMapping("/getPostCat")
	public String getPostCategories(@RequestParam String categoryType, Model map) {

		List<CardCategory> CardCategoryList = adminDao.getAllCategories(categoryType);

		map.addAttribute("cardList", CardCategoryList);

		return "category";

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

	public Resource loadFileAsResource(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found " + fileName, ex);
		}
	}

	@SuppressWarnings("unchecked")

	/*
	 * @RequestMapping("/downloadFile/{fileName:.+}") public
	 * ResponseEntity<Resource> downloadFile(@RequestParam String fileName,
	 * HttpServletRequest request) { // Load file as Resource Resource resource =
	 * loadFileAsResource(fileName);
	 * 
	 * // Try to determine file's content type String contentType = null; try {
	 * contentType =
	 * request.getServletContext().getMimeType(resource.getFile().getAbsolutePath())
	 * ; } catch (IOException ex) { ex.printStackTrace(); }
	 * 
	 * // Fallback to the default content type if type could not be determined
	 * if(contentType == null) { contentType = "application/octet-stream"; }
	 * HttpHeaders header = new HttpHeaders();
	 * header.setContentType(MediaType.parseMediaType(contentType)); header.
	 * 
	 * 
	 * return new ResponseEntity(header,HttpStatus.OK);
	 * .contentType(MediaType.parseMediaType(contentType))
	 * .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
	 * resource.getFilename() + "\"") .body(resource); }
	 */
	/*
	 * @RequestMapping(value = "/saveCards",method = RequestMethod.POST) public
	 * String saveCards(@RequestParam("file") CommonsMultipartFile file , Model map)
	 * {
	 * 
	 * System.out.println("------------------------------------");
	 * 
	 * MediaFiles fileName = adminDao.storeFile(file); Object obj = new
	 * UploadFileResponse(fileName.getFilePath(), fileName.getFilePath(),
	 * file.getContentType(), file.getSize());
	 * 
	 * map.addAttribute("msg", obj); return "upload-gallery"; }
	 */
	@RequestMapping("/save")
	public String storeCategory() {

		return "upload-gallery";
	}
	
	@RequestMapping("/saveCard1")
	public String storeCard(@RequestParam("categoryId") Integer categoryId, Model map) {
		
		map.addAttribute("categoryId", categoryId);
		
		List<Cards> cardList=adminProfileDaoImpl.getAllCards(categoryId);
		
		List<Cards> cardlist = new ArrayList<Cards>();
		for(Cards cards: cardList) {
			Cards card = new Cards();
			card.setCardId(cards.getCardId());
			card.setFilePath(cards.getFilePath());
			cardlist.add(card);
		}
		map.addAttribute("cardlist", cardlist);
		
		
		return "PostCard_Upload";
	}
	
	@RequestMapping("/getPostCards")
	public String getCardCatagory(@RequestParam("adminId") Integer adminId,Model map) {

		List<CardCategory> cardCategorylist=adminProfileDaoImpl.getAllPostCards(adminId);
		
		List<CardCategory> categoryList = new ArrayList<CardCategory>();
		for(CardCategory cardCategory: cardCategorylist) {
			CardCategory card = new CardCategory();
			card.setCategoryId(cardCategory.getCategoryId());
			card.setFilePath(cardCategory.getFilePath());
			
			categoryList.add(card);
		}
		map.addAttribute("cardList", categoryList);
		
		return "Card_Catagory_gallery";
	}
	@RequestMapping("/getGreatingCards")
	public String getGreatingCatagory(@RequestParam("adminId") Integer adminId,Model map) {
		
		List<CardCategory> cardCategorylist=adminProfileDaoImpl.getAllGreatingCards(adminId);
		
		List<CardCategory> categoryList = new ArrayList<CardCategory>();
		for(CardCategory cardCategory: cardCategorylist) {
			CardCategory card = new CardCategory();
			card.setCategoryId(cardCategory.getCategoryId());
			card.setFilePath(cardCategory.getFilePath());
			
			categoryList.add(card);
		}
		map.addAttribute("cardList", categoryList);
		
		return "Card_Catagory_gallery";
	}
	@RequestMapping("/getPCards")
	public String getPostCards(@RequestParam("categoryId") Integer categoryId,Model map) {
		
		List<Cards> cardList=adminProfileDaoImpl.getAllCards(categoryId);
		
		List<Cards> cardlist = new ArrayList<Cards>();
		for(Cards cards: cardList) {
			Cards card = new Cards();
			card.setCardId(cards.getCardId());
			card.setFilePath(cards.getFilePath());
			cardlist.add(card);
		}
		map.addAttribute("cardlist", cardlist);
		
		return "PostCards";
	}
		

	@RequestMapping("/saveCard")
	public String storeCards() {

		return "upload-card";
	}

	@SuppressWarnings("unused")
	@RequestMapping(value = "/uploadCard", method = RequestMethod.POST)
	public String uploadCards(@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "categoryId", required = false) Integer categoryId,
			@RequestParam(value = "editable", required = false) Character editable,
			@RequestParam(value = "cardText", required = false) String cardText, Model map) {

		if (file == null) {
			map.addAttribute("msg", "'file' is empty or null please check");
			return "404";

		} else if (editable == null) {
			map.addAttribute("msg", "'editable' is empty or null please check");
			return "404";

		} else if (categoryId == null) {
			map.addAttribute("msg", "'categoryId' is empty or null please check");
			return "404";

		} else {

			CardCategory cardCategory = adminDao.storeCards(file, categoryId, cardText, editable);

			if (cardCategory != null) {
				Object obj = new UploadFileResponse(
						cardCategory.getCards().get(cardCategory.getCards().size() - 1).getFilePath(),
						cardCategory.getCards().get(cardCategory.getCards().size() - 1).getFilePath(),
						file.getContentType(), file.getSize());
				if (!file.isEmpty() || categoryId != null) {
					map.addAttribute("msg", "your File is uploaded successfully");

					return "upload-card";
				} else {
					map.addAttribute("msg", "your File is not uploaded");

					return "404";
				}
			} else {
				map.addAttribute("msg", "Category does not exist please add first");

				return "404";
			}
		}
	}
	@SuppressWarnings("unused")
	@RequestMapping(value = "/uploadPostCard", method = RequestMethod.POST)
	public String uploadPostCard(@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "categoryId", required = false) Integer categoryId,
			@RequestParam(value = "editable", required = false) Character editable,
			 Model map) {

		if (file == null) {
			map.addAttribute("msg", "'file' is empty or null please check");
			return "404";

		} else if (editable == null) {
			map.addAttribute("msg", "'editable' is empty or null please check");
			return "404";

		} else if (categoryId == null) {
			map.addAttribute("msg", "'categoryId' is empty or null please check");
			return "404";

		} else {

			CardCategory cardCategory = fileStorageService.storePostCards(file, categoryId,editable);

			if (cardCategory != null) {
				Object obj = new UploadFileResponse(
						cardCategory.getCards().get(cardCategory.getCards().size() - 1).getFilePath(),
						cardCategory.getCards().get(cardCategory.getCards().size() - 1).getFilePath(),
						file.getContentType(), file.getSize());
				
				List<Cards> cardList=adminProfileDaoImpl.getAllCards(categoryId);
				
				List<Cards> cardlist = new ArrayList<Cards>();
				for(Cards cards: cardList) {
					Cards card = new Cards();
					card.setCardId(cards.getCardId());
					card.setFilePath(cards.getFilePath());
					cardlist.add(card);
				}
				map.addAttribute("cardlist", cardlist);
				map.addAttribute("categoryId", categoryId);
				if (!file.isEmpty() || categoryId != null) {
					map.addAttribute("cardList", cardCategory.getCards());

					return "PostCard_Upload";
				} else {
					map.addAttribute("msg", "your File is not uploaded");

					return "404";
				}
			} else {
				map.addAttribute("msg", "Category does not exist please add first");

				return "404";
			}
		}
	}
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model, String error, String logout) {
		if (error != null)
			model.addAttribute("error", "Your username and password is invalid.");

		if (logout != null)
			model.addAttribute("message", "You have been logged out successfully.");
		return "login";
	}

	@RequestMapping(value = { "/", "/welcome" }, method = RequestMethod.GET)
	public String welcome(Model model) {
		return "welcome";
	}

	@SuppressWarnings("unused")
	@RequestMapping(value = "/addCategory", method = RequestMethod.POST)
	public String uploadCategory(@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "categoryName", required = false) String categoryName,
			@RequestParam(value = "adminId", required = false) Integer adminId,
			@RequestParam(value = "categoryType", required = false) String categoryType, Model map,
			HttpServletRequest request) {

		if (file == null) {
			map.addAttribute("msg", "'file' is empty or null please check");
			return "404";
		} else if (categoryName == null) {
			map.addAttribute("msg", "'categoryName' is empty or null please check");
			return "404";

		} else if (categoryType == null) {
			map.addAttribute("msg", "'categoryType' is empty or null please check");
			return "404";

		} else if (adminId == null) {
			map.addAttribute("msg", "'adminId' is empty or null please check");
			return "404";

		} else {

			fileStorageService.storeCards(file, adminId, categoryName, categoryType, request);

			// List<CardCategory> cardCategoryList = adminProfile.getCardCategories();

			// if (adminProfile != null) {

			// Object obj=new
			// UploadFileResponse(cardCategoryList.get(cardCategoryList.size()-1).getFilePath(),
			// cardCategoryList.get(cardCategoryList.size()-1).getFilePath(),
			// file.getContentType(), file.getSize());

			if (!file.isEmpty() || adminId != null) {
				map.addAttribute("msg", "your File is uploaded successfully");

				return "upload-gallery";

			} else {
				map.addAttribute("msg", "your File is not uploaded");

				return "404";
			}
			// }else {
			// map.addAttribute("msg", "User does not exist please register first");
			// return "404";
		}
	}
	

}
//}
