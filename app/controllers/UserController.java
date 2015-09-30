package controllers;

import static play.data.Form.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Collection;
import models.Conversation;
import models.Emoticon;
import models.Location;
import models.Message;
import models.Post;
import models.Resource;
import models.SecondarySocialRelation;
import models.SiteTour;
import models.User;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;

import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import viewmodel.CollectionVM;
import viewmodel.ConversationVM;
import viewmodel.EmoticonVM;
import viewmodel.MessageVM;
import viewmodel.PostVMLite;
import viewmodel.ProfileVM;
import viewmodel.UserVM;
import viewmodel.UserVMLite;
import common.cache.CalServer;
import common.utils.HtmlUtil;
import common.utils.ImageFileUtil;
import common.utils.NanoSecondStopWatch;

public class UserController extends Controller {
    private static final play.api.Logger logger = play.api.Logger.apply(UserController.class);
    
    public static String getMobileUserKey(final play.mvc.Http.Request r, final Object key) {
		final String[] m = r.queryString().get(key);
		if(m != null && m.length > 0) {
			try {
				return URLDecoder.decode(m[0], "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
    
    @Transactional
    public static Result completeHomeTour() {
        final User localUser = Application.getLocalUser(session());
        SiteTour tour = SiteTour.getSiteTour(localUser.id, SiteTour.TourType.HOME);
        if (tour == null) {
            tour = new SiteTour(localUser.id, SiteTour.TourType.HOME);
            tour.complete();
            tour.save();
            logger.underlyingLogger().debug(String.format("[u=%d] User completed home tour", localUser.id));
        }
        return ok();
    }
    
	@Transactional
	public static Result getUserInfo() {
	    NanoSecondStopWatch sw = new NanoSecondStopWatch();
	    
		final User localUser = Application.getLocalUser(session());
		if (localUser == null) {
			return status(500);
		}
		
		UserVM userInfo = new UserVM(localUser);
		
		sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.getId()+"] getUserInfo(). Took "+sw.getElapsedMS()+"ms");
        }
		return ok(Json.toJson(userInfo));
	}
	
	@Transactional
	public static Result getUserInfoById(Long id) {
	    NanoSecondStopWatch sw = new NanoSecondStopWatch();
	    
		final User localUser = User.findById(id);
		if (localUser == null) {
			return status(500);
		}
		
		UserVM userInfo = new UserVM(localUser);
		
		sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.getId()+"] getUserInfo(). Took "+sw.getElapsedMS()+"ms");
        }
		return ok(Json.toJson(userInfo));
	}
	
	@Transactional(readOnly=true)
	public static Result aboutUser() {
		final User localUser = Application.getLocalUser(session());
		return ok(Json.toJson(localUser));
	}
	
	@Transactional
	public static Result uploadProfilePhoto() {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
		logger.underlyingLogger().info("STS [u="+localUser.id+"] uploadProfilePhoto");

		FilePart picture = request().body().asMultipartFormData().getFile("profile-photo");
		String fileName = picture.getFilename();

	    File file = picture.getFile();
	    try {
            File fileTo = ImageFileUtil.copyImageFileToTemp(file, fileName);
			localUser.setPhotoProfile(fileTo);
		} catch (IOException e) {
		    logger.underlyingLogger().error("Error in uploadProfilePhoto", e);
			return status(500);
		}
	    completeHomeTour();
		return ok();
	}
	
	@Transactional
	public static Result uploadProfilePhotoMobile() {
		final User localUser = Application.getLocalUser(session());
		FilePart picture = request().body().asMultipartFormData().getFile("club_image");
		String fileName = picture.getFilename();
		logger.underlyingLogger().info("STS [u="+localUser.id+"] uploadProfilePhotoMobile - "+fileName);
		request().body().asMultipartFormData().getFile("club_image");
	    File file = picture.getFile();
	    completeHomeTour();
		return ok();
	}
	
	@Transactional
	public static Result uploadCoverPhoto() {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
		
		logger.underlyingLogger().info("STS [u="+localUser.id+"] uploadCoverPhoto");

		FilePart picture = request().body().asMultipartFormData().getFile("profile-photo");
		String fileName = picture.getFilename();
	    
	    File file = picture.getFile();
	    try {
	    	File fileTo = ImageFileUtil.copyImageFileToTemp(file, fileName);
			localUser.setCoverPhoto(fileTo);
		} catch (IOException e) {
		    logger.underlyingLogger().error("Error in uploadCoverPhoto", e);
			return status(500);
		}
	    completeHomeTour();
		return ok();
	}
	
	@Transactional
	public static Result getProfileImage() {
	    response().setHeader("Cache-Control", "max-age=1");
	    final User localUser = Application.getLocalUser(session());
		
		if(User.isLoggedIn(localUser) && localUser.getPhotoProfile() != null) {
			return ok(localUser.getPhotoProfile().getRealFile());
		}
		
		try {
			return ok(User.getDefaultUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	@Transactional
	public static Result getCoverImage() {
	    response().setHeader("Cache-Control", "max-age=1");
	    final User localUser = Application.getLocalUser(session());
		
		if(User.isLoggedIn(localUser) && localUser.getCoverProfile() != null) {
			return ok(localUser.getCoverProfile().getRealFile());
		}
		
		try {
			return ok(User.getDefaultCoverPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}

	@Transactional
	public static Result updateUserProfileData() {
	    final User localUser = Application.getLocalUser(session());
	    
	    logger.underlyingLogger().info(String.format("[u=%d] updateUserProfileData", localUser.id));
	    
	    // Basic info
	    DynamicForm form = DynamicForm.form().bindFromRequest();
	    String parentDisplayName = form.get("parent_displayname");
	    String parentFirstName = form.get("parent_firstname");
	    String parentLastName = form.get("parent_lastname");
	    String parentAboutMe = form.get("parent_aboutme");
	    if (StringUtils.isEmpty(parentDisplayName) || StringUtils.isEmpty(parentFirstName) || StringUtils.isEmpty(parentLastName)) {
	        logger.underlyingLogger().error(String.format(
	                "[u=%d][displayname=%s][firstname=%s][lastname=%s] displayname, firstname or lastname missing", 
	                localUser.id, parentDisplayName, parentFirstName, parentLastName));
            return status(500, "請填寫您的顯示名稱與姓名");
        }
	    
	    parentDisplayName = parentDisplayName.trim();
	    parentFirstName = parentFirstName.trim();
	    parentLastName = parentLastName.trim();
	    if (parentAboutMe != null) {
	        parentAboutMe = parentAboutMe.trim();
	    }
	    
	    if (!localUser.displayName.equals(parentDisplayName)) {  
	        if (!User.isDisplayNameValid(parentDisplayName)) {
                logger.underlyingLogger().error(String.format(
                        "[u=%d][displayname=%s] displayname contains whitespace", localUser.id, parentDisplayName));
                return status(500, "\""+parentDisplayName+"\" 不可有空格");
	        }
	        if (User.isDisplayNameExists(parentDisplayName)) {
                logger.underlyingLogger().error(String.format(
                        "[u=%d][displayname=%s] displayname already exists", localUser.id, parentDisplayName));
                return status(500, "\""+parentDisplayName+"\" 已被選用。請選擇另一個顯示名稱重試");
            }
        }
        
		// UserInfo
        String parentBirthYear = form.get("parent_birth_year");
        Location parentLocation = Location.getLocationById(Integer.valueOf(form.get("parent_location")));
        
        if (StringUtils.isEmpty(parentBirthYear) || parentLocation == null) {
            logger.underlyingLogger().error(String.format(
                    "[u=%d][birthYear=%s][location=%s] birthYear or location missing", localUser.id, parentBirthYear, parentLocation.displayName));
            return status(500, "請填寫您的生日，地區");
        }
        
        localUser.displayName = parentDisplayName;
        localUser.name = parentDisplayName;
        localUser.firstName = parentFirstName;
        localUser.lastName = parentLastName;
        
        localUser.userInfo.birthYear = parentBirthYear;
        localUser.userInfo.location = parentLocation;
        localUser.userInfo.aboutMe = parentAboutMe;
        localUser.userInfo.save();
        localUser.save();
        
        /*
        ParentType parentType = ParentType.valueOf(form.get("parent_type"));
        int numChildren = Integer.valueOf(form.get("num_children"));
        if (ParentType.NA.equals(parentType)) {
            numChildren = 0;
        }
        
        if (parentBirthYear == null || parentLocation == null || parentType == null) {
            return status(500, "請填寫您的生日，地區，媽媽身份");
        }
        
        localUser.displayName = parentDisplayName;
        localUser.name = parentDisplayName;
        localUser.firstName = parentFirstName;
        localUser.lastName = parentLastName;
        
        UserInfo userInfo = new UserInfo();
        userInfo.birthYear = parentBirthYear;
        userInfo.location = parentLocation;
        userInfo.parentType = parentType;
        userInfo.aboutMe = parentAboutMe;
        
        if (ParentType.MOM.equals(parentType) || ParentType.SOON_MOM.equals(parentType)) {
            userInfo.gender = TargetGender.Female;
        } else if (ParentType.DAD.equals(parentType) || ParentType.SOON_DAD.equals(parentType)) {
            userInfo.gender = TargetGender.Male;
        } else {
            userInfo.gender = TargetGender.Female;   // default
        }
        userInfo.numChildren = numChildren;
        
        localUser.userInfo = userInfo;
        localUser.userInfo.save();
        
        // UseChild
        int maxChildren = (numChildren > 5)? 5 : numChildren;
        for (int i = 1; i <= maxChildren; i++) {
            String genderStr = form.get("bb_gender" + i);
            if (genderStr == null) {
                return status(500, "請選擇寶寶性別");
            }
            
            TargetGender bbGender = TargetGender.valueOf(form.get("bb_gender" + i));
            String bbBirthYear = form.get("bb_birth_year" + i);
            String bbBirthMonth = form.get("bb_birth_month" + i);
            String bbBirthDay = form.get("bb_birth_day" + i);
            
            if (bbBirthDay == null) {
                bbBirthDay = "";
            }
            
            if (!DateTimeUtil.isDateOfBirthValid(bbBirthYear, bbBirthMonth, bbBirthDay)) {
                return status(500, "寶寶生日日期格式不正確。請重試");
            }
            
            UserChild userChild = new UserChild();
            userChild.gender = bbGender;
            userChild.birthYear = bbBirthYear;
            userChild.birthMonth = bbBirthMonth;
            userChild.birthDay = bbBirthDay;
            
            userChild.save();
            localUser.children.add(userChild);
        }
        */
        
        return ok();
	}
	
    
    @Transactional
    public static Result getProfile(Long id) {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();
	    
    	User user = User.findById(id);
    	final User localUser = Application.getLocalUser(session());
		
		sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.getId()+"] getProfile(). Took "+sw.getElapsedMS()+"ms");
        }

    	return ok(Json.toJson(ProfileVM.profile(user,localUser)));
    }
    
    @Transactional
	public static Result getProfileImageByID(Long id) {
        response().setHeader("Cache-Control", "max-age=1");
        User user = User.findById(id);
    	
		if(User.isLoggedIn(user) && user.getPhotoProfile() != null) {
			return ok(user.getPhotoProfile().getRealFile());
		}
		
		try {
			return ok(User.getDefaultUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
    @Transactional
	public static Result getOriginalImageByID(Long id) {
        response().setHeader("Cache-Control", "max-age=1");
        User user = User.findById(id);

        if(User.isLoggedIn(user) && user.getPhotoProfile() != null) {
			return ok(user.getPhotoProfile().getRealFile());
		}
        
		try {
			return ok(User.getDefaultUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
    
	@Transactional
	public static Result getCoverImageByID(Long id) {
	    response().setHeader("Cache-Control", "max-age=1");
	    User user = User.findById(id);

	    if(User.isLoggedIn(user) && user.getCoverProfile() != null) {
			return ok(user.getCoverProfile().getRealFile());
		}
		try {
			return ok(User.getDefaultCoverPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	@Transactional
	public static Result getMiniVersionImageByID(Long id) {
	    response().setHeader("Cache-Control", "max-age=1");
		final User user = User.findById(id);
		
		if(User.isLoggedIn(user) && user.getPhotoProfile() != null) {
			return ok(new File(user.getPhotoProfile().getMini()));
		} 
		
		try {
			return ok(User.getDefaultThumbnailUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	@Transactional
	public static Result getMiniCommentVersionImageByID(Long id) {
	    response().setHeader("Cache-Control", "max-age=1");
		final User user = User.findById(id);
		
		if(User.isLoggedIn(user) && user.getPhotoProfile() != null) {
			return ok(new File(user.getPhotoProfile().getMiniComment()));
		} 
		
		try {
			return ok(User.getDefaultThumbnailUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	@Transactional
	public static Result getThumbnailVersionImageByID(Long id) {
	    response().setHeader("Cache-Control", "max-age=1");
		final User user = User.findById(id);
		
		if(User.isLoggedIn(user) && user.getPhotoProfile() != null) {
			return ok(new File(user.getPhotoProfile().getThumbnail()));
		}
		
		try {
			return ok(User.getDefaultThumbnailUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	@Transactional
	public static Result getThumbnailCoverImageByID(Long id) {
	    response().setHeader("Cache-Control", "max-age=1");
		final User user = User.findById(id);
		
		if(User.isLoggedIn(user) && user.getCoverProfile() != null) {
			return ok(new File(user.getCoverProfile().getThumbnail()));
		}
		
		try {
			return ok(User.getDefaultCoverPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	@Transactional
    public static Result getEmoticons() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();
        List<Emoticon> emoticons = Emoticon.getEmoticons();
        
        List<EmoticonVM> emoticonVMs = new ArrayList<>();
        for(Emoticon emoticon : emoticons) {
            EmoticonVM vm = new EmoticonVM(emoticon);
            emoticonVMs.add(vm);
        }

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("getEmoticons. Took "+sw.getElapsedMS()+"ms");
        }
        return ok(Json.toJson(emoticonVMs));
    }
	   
	@Transactional
	public static Result getMessages(Long id, Long offset) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
		List<MessageVM> vms = new ArrayList<>();
		Conversation conversation = Conversation.findById(id); 
		List<Message> messages =  conversation.getMessages(localUser, offset);
		if(messages != null ){
			for(Message message : messages) {
				MessageVM vm = new MessageVM(message);
				vms.add(vm);
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put("message", vms);
		map.put("counter", localUser.getUnreadConversationCount());

        sw.stop();
        logger.underlyingLogger().info("[u="+localUser.id+"][c="+id+"] getMessages(offset="+offset+"). Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(map));
	}
	
	@Transactional
    public static Result sendMessage() {
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        DynamicForm form = form().bindFromRequest();
        Long conversationId = Long.parseLong(form.get("conversationId"));
        Long receiverId = Long.parseLong(form.get("receiverId"));
        String msgText = HtmlUtil.convertTextToHtml(form.get("msgText"));
        Message message = Conversation.sendMessage(conversationId, localUser, msgText);
        
        Map<String, Object> map = new HashMap<>();
		map.put("id", message.id);
        return ok(Json.toJson(map));
    }
	
	@Transactional
    public static Result deleteConversation(Long id) {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
		
        Conversation.archiveConversation(id, localUser);
        return getAllConversations();
    }
	
	private static List<ConversationVM> getAllConversations(User localUser, ConversationVM newConversationVM) {
		List<ConversationVM> vms = new ArrayList<>();
		List<Conversation> conversations = localUser.findMyConversations();
		if (conversations != null) {
			User otherUser;
			for (Conversation conversation : conversations) {
				// archived, dont show
				if (conversation.isArchivedBy(localUser)) {
					continue;
				}

				// add new conversation to top of list
				if (newConversationVM != null && conversation.id == newConversationVM.id) {
					continue;
				}

				if (conversation.user1 == localUser) {
					otherUser = conversation.user2;
				} else { 
					otherUser = conversation.user1;
				}
				
				ConversationVM vm = new ConversationVM(conversation, localUser, otherUser);
				vms.add(vm);
			}
		}
		
		// always add new conversation to top of list
		if (newConversationVM != null) {
			vms.add(0,newConversationVM);
		}
		
		return vms;	
	}

	@Transactional
	public static Result getAllConversations() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
		List<ConversationVM> vms = getAllConversations(localUser, null);

        sw.stop();
        logger.underlyingLogger().info("[u="+localUser.id+"] getAllConversations. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(vms));
	}
	
	@Transactional
    public static Result openConversation(Long id) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        if (localUser.id == id) {
            logger.underlyingLogger().error(String.format("[u1=%d] [u2=%d] Same user. Will not open conversation", localUser.id, id));
            return status(500);
        }
        
        User otherUser = User.findById(id);
        Conversation conversation = Conversation.startConversation(localUser, otherUser);
        
        List<ConversationVM> vms = new ArrayList<>();
        ConversationVM conversationVM = new ConversationVM(conversation, localUser, otherUser);
		vms.add(conversationVM);
        
		logger.underlyingLogger().debug("[u1="+localUser.id+"][u2="+id+"] openConversation. Took "+sw.getElapsedMS()+"ms");
		
		return ok(Json.toJson(vms));
    }
	
	@Transactional
	public static Result uploadMessagePhoto() {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
		
        DynamicForm form = DynamicForm.form().bindFromRequest();
        String messageId = form.get("messageId");
        
        FilePart picture = request().body().asMultipartFormData().getFile("send-photo0");
        String fileName = picture.getFilename();
        
        File file = picture.getFile();
        try {
            File fileTo = ImageFileUtil.copyImageFileToTemp(file, fileName);
            Long id = Message.findById(Long.valueOf(messageId)).addPrivatePhoto(fileTo,localUser).id;
            return ok(id.toString());
        } catch (IOException e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
            return status(500);
        }
    }
	
	@Transactional
	public static Result getUnreadMessageCount() {
		final User localUser = Application.getLocalUser(session());
		Map<String, Long> vm = new HashMap<>();
		vm.put("count", localUser.getUnreadConversationCount());
		return ok(Json.toJson(vm));
	}
	
	@Transactional
	public static Result getMessageImage(Long id) {
	    response().setHeader("Cache-Control", "max-age=604800");
		return ok(Resource.findById(id).getThumbnailFile());
	}

    @Transactional
    public static Result getOriginalMessageImage(Long id) {
        response().setHeader("Cache-Control", "max-age=604800");
        return ok(Resource.findById(id).getRealFile());
    }

    @Transactional
    public static Result inviteByEmail(String email) {
		final User localUser = Application.getLocalUser(session());

        if (localUser.isLoggedIn()) {
            /*GameAccount gameAccount = GameAccount.findByUserId(localUser.id);
            gameAccount.sendInvitation(email);*/
        } else {
            logger.underlyingLogger().info("Not signed in. Skipped signup invitation to: "+email);
        }
		return ok();
	}
    
    
    @Transactional
	public static Result addProduct() {
    	User user = Application.getLocalUser(session());
    	DynamicForm form = DynamicForm.form().bindFromRequest();
        String postId = form.get("postId");
        if (logger.isDebugEnabled()) {
            logger.underlyingLogger().debug("uploadPhotoOfPost(p="+postId+")");
        }

        FilePart picture = request().body().asMultipartFormData().getFile("photo");
        System.out.println("HERE");
        
        if (picture == null) {
            return status(500);
        }
        
        System.out.println("picture");
        
        String fileName = picture.getFilename();
        File file = picture.getFile();
        try {
            File fileTo = ImageFileUtil.copyImageFileToTemp(file, fileName);
            Post product = new Post();
            product.setOwner(user);
            Long id = product.addPostPhoto(fileTo).id;
            return ok(id.toString());
        } catch (IOException e) {
        	e.printStackTrace();
            logger.underlyingLogger().error("Error in uploadPhotoOfPost", e);
            return status(500);
        }
	}
    
    @Transactional
    public static Result profile(Long id) {
        	NanoSecondStopWatch sw = new NanoSecondStopWatch();
    	    
        	User user = User.findById(id);
        	final User localUser = Application.getLocalUser(session());
    		
    		sw.stop();
            if (logger.underlyingLogger().isDebugEnabled()) {
                logger.underlyingLogger().debug("[u="+user.getId()+"] getProfile(). Took "+sw.getElapsedMS()+"ms");
            }
            return ok(Json.toJson(ProfileVM.profile(user,localUser)));
        	//return ok(views.html.babybox.web.profile.render(Json.stringify(Json.toJson(ProfileVM.profile(user,localUser))), Json.stringify(Json.toJson(new UserVM(localUser)))));
    }
    
    @Transactional
    public static Result getUserPosts(Long id, Long offset) {
    	List<Long> postIds = CalServer.getUserPostFeeds(id);
        
        if(postIds.size() == 0){
			return ok(Json.toJson(postIds));
		}
    	List<PostVMLite> vms = new ArrayList<>();
		for(Post product : Post.getPosts(postIds)) {
			PostVMLite vm = new PostVMLite(product);
			vms.add(vm);
		}
		return ok(Json.toJson(vms));
    }
    
    @Transactional
    public static Result getUserCollections(Long id) {
    	List<CollectionVM> vms = new ArrayList<>();
		for(Collection collection : Collection.getUserProductCollections(id)) {
			CollectionVM vm = new CollectionVM(collection);
			vms.add(vm);
		}
		return ok(Json.toJson(vms));
    }
    
    @Transactional
    public static Result followUser(Long id) {
    	User user = Application.getLocalUser(session());
    	user.onFollowedBy(User.findById(id));
		return ok();
    }
    
    @Transactional
    public static Result unfollowUser(Long id) {
    	User user = Application.getLocalUser(session());
    	user.onUnFollowedBy(User.findById(id));
		return ok();
    }
    
    @Transactional
    public static Result getFollowings(Long id, Long offset){
    	List<Long> followings = SecondarySocialRelation.getFollowings(id);
    	List<UserVMLite> userFollowings = new ArrayList<UserVMLite>();
    	
    	for(Long f : followings){
    		User user = User.findById(f);
    		UserVMLite uservm = new UserVMLite(user);
    		userFollowings.add(uservm);
    	}
    	return ok(Json.toJson(userFollowings));
    }
    
    @Transactional
    public static Result getFollowers(Long id, Long offset){
    	List<Long> followings = SecondarySocialRelation.getFollowers(id);
    	List<UserVMLite> userFollowers = new ArrayList<UserVMLite>();
    	
    	for(Long f : followings){
    		User user = User.findById(f);
    		UserVMLite uservm = new UserVMLite(user);
    		userFollowers.add(uservm);
    	}
    	return ok(Json.toJson(userFollowers));
    }
    
    @Transactional
    public static Result getUserLikedPosts(Long id, Long offset){
    	
    	List<Long> postIds = CalServer.getUserLikeFeeds(id);
        
        if(postIds.size() == 0){
			return ok(Json.toJson(postIds));
		}
    	List<PostVMLite> vms = new ArrayList<>();
		for(Post product : Post.getPosts(postIds)) {
			PostVMLite vm = new PostVMLite(product);
			vms.add(vm);
		}
		return ok(Json.toJson(vms));
    }
}
