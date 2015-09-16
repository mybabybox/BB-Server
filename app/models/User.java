package models;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import models.SocialRelation.Action;
import models.TokenAction.Type;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import play.Play;
import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import babybox.shopping.social.exception.SocialObjectNotCommentableException;
import babybox.shopping.social.exception.SocialObjectNotJoinableException;
import babybox.shopping.social.exception.SocialObjectNotLikableException;
import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;

import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthProvider;
import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthUser;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.FirstLastNameIdentity;
import com.google.common.collect.Lists;

import common.cache.FriendCache;
import common.collection.Pair;
import common.image.FaceFinder;
import common.utils.DateTimeUtil;
import common.utils.ImageFileUtil;
import common.utils.NanoSecondStopWatch;
import common.utils.StringUtil;
import domain.CommentType;
import domain.DefaultValues;
import domain.Followable;
import domain.ProductType;
import domain.SocialObjectType;
import domain.Socializable;

@Entity
public class User extends SocialObject implements Subject, Socializable, Followable {
    private static final play.api.Logger logger = play.api.Logger.apply(User.class);

    private static User BB_ADMIN;
    private static User BB_EDITOR;
    
    public static final String BB_ADMIN_NAME = "BabyBox 管理員";
    public static final String BB_EDITOR_NAME = "BabyBox 編輯";
    
    public String firstName;
    public String lastName;
    public String displayName;
    public String email;
    
    // Targeting info
    
    @OneToOne
    public UserInfo userInfo;
    
    @OneToMany
    public List<UserChild> children = new ArrayList<UserChild>();
   
    // fb info
    
    public boolean fbLogin;
    
    public boolean mobileSignup;
    
    @OneToOne
    @JsonIgnore
    public FbUserInfo fbUserInfo;
    
    @OneToMany
    @JsonIgnore
    public List<FbUserFriend> fbUserFriends;
    
    // stats
    
    public Long followingCount = 0L;
    
    public Long followersCount = 0L;
    
    public Long productCount = 0L;
    
    public Long commentsCount = 0L;
    
    public Long collectionCount = 0L;
    
    public Long likesCount = 0L;

    
    // system
    
    @JsonIgnore
    public boolean active;

    @JsonIgnore
    public boolean emailValidated;

    @JsonIgnore
    public boolean newUser;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonIgnore
    public Date lastLogin;

    @JsonIgnore
    public Long totalLogin = 0L;
    
    @ManyToMany
    @JsonIgnore
    public List<SecurityRole> roles;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    public List<LinkedAccount> linkedAccounts;

    @ManyToMany
    @JsonIgnore
    public List<UserPermission> permissions;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JsonIgnore
    public Folder albumPhotoProfile;
    
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JsonIgnore
    public Folder albumCoverProfile;

    @JsonIgnore
    public String lastLoginUserAgent;

    @Override
    @JsonIgnore
    public String getIdentifier() {
        return Long.toString(id);
    }

    @OneToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    public List<Folder> folders;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    public List<Album> album;

    @OneToMany
    @JsonIgnore
    public List<Conversation> conversations = new ArrayList<Conversation>();

    @Override
    @JsonIgnore
    public List<? extends Role> getRoles() {
        return roles;
    }

    @Override
    public List<? extends Permission> getPermissions() {
        return permissions;
    }

    public User() {
        this.objectType = SocialObjectType.USER;
    }

    public User(String firstName, String lastName, String displayName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.name = firstName;
    }

    public void likesOn(SocialObject target)
            throws SocialObjectNotLikableException {
        target.onLikedBy(this);
    }

    public Conversation sendMessage(User user, String msg) {
        Conversation conversation = Conversation.findByUsers(this, user);

        if (conversations == null || conversation == null) {
            conversation = new Conversation(this, user);
            conversations = Lists.newArrayList();
            conversations.add(conversation);
            user.conversations.add(conversation);
        }
        conversation.addMessage(this, msg);
        return conversation;
    }

    public SocialObject commentedOn(SocialObject target, String comment)
            throws SocialObjectNotCommentableException {
        return target.onComment(this, comment, CommentType.SIMPLE);
    }
    
    public void markNotificationRead(Notification notification) {
        notification.changeStatus(1);
    }

    public static User searchEmail(String email) {
        CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> root = criteria.from(User.class);
        criteria.select(root);
        Predicate predicate = (builder.equal(root.get("email"), email));
        criteria.where(predicate);
        return JPA.em().createQuery(criteria).getSingleResult();
    }

    public Resource setPhotoProfile(File file) throws IOException {
        ensureAlbumPhotoProfileExist();

        // Pre-process file to have face centered.
        BufferedImage croppedImage = FaceFinder.getSquarePictureWithFace(file);
        ImageFileUtil.writeFileWithImage(file, croppedImage);

        Resource newPhoto = this.albumPhotoProfile.addFile(file,
                SocialObjectType.PROFILE_PHOTO);
        this.albumPhotoProfile.setHighPriorityFile(newPhoto);
        newPhoto.save();

        // credit points if first time
        //GameAccount.setPointsForPhotoProfile(this);

        return newPhoto;
    }
    
    public Resource setCoverPhoto(File source) throws IOException {
        ensureCoverPhotoProfileExist();

        // Pre-process file to have face centered.
        BufferedImage croppedImage = FaceFinder.getRectPictureWithFace(source, 2.29d);
        ImageFileUtil.writeFileWithImage(source, croppedImage);

        Resource cover_photo = this.albumCoverProfile.addFile(source,
                SocialObjectType.COVER_PHOTO);
        this.albumCoverProfile.setHighPriorityFile(cover_photo);
        cover_photo.save();
        return cover_photo;
    }

    public void removePhotoProfile(Resource resource) throws IOException {
        this.albumPhotoProfile.removeFile(resource);
    }

    /**
     * get the photo profile
     * 
     * @return the resource, null if not exist
     */
    @JsonIgnore
    public Resource getPhotoProfile() {
        if (this.albumPhotoProfile != null) {
            Resource file = this.albumPhotoProfile.getHighPriorityFile();
            if (file != null) {
                return file;
            }
        }
        return null;
    }

    public String getPhotoProfileURL() {
        Resource resource = getPhotoProfile();
        if (resource == null) {
            return "";
        }
        return resource.getPath();
    }
    
    @JsonIgnore
    public Resource getCoverProfile() {
        if (this.albumCoverProfile != null) {
            Resource file = this.albumCoverProfile.getHighPriorityFile();
            if (file != null) {
                return file;
            }
        }
        return null;
    }

    @JsonIgnore
    public Resource getMiniProfileImage() {
        if (this.albumPhotoProfile != null) {
            Resource file = this.albumPhotoProfile.getHighPriorityFile();
            if (file != null) {
                return file;
            }
        }
        return null;
    }
    
    public String getCoverProfileURL() {
        Resource resource = getCoverProfile();
        if (resource == null) {
            return "";
        }
        return resource.getPath();
    }

    /**
     * ensure the existence of the system folder: albumPhotoProfile
     */
    private void ensureAlbumPhotoProfileExist() {
        if (this.albumPhotoProfile == null) {
            this.albumPhotoProfile = createAlbum("profile", "", true);
            this.merge();
        }
    }
    
    /**
     * ensure the existence of the system folder: albumPhotoProfile
     */
    private void ensureCoverPhotoProfileExist() {

        if (this.albumCoverProfile == null) {
            this.albumCoverProfile = createAlbum("cover", "", true);
            this.merge();
        }
    }
    
    @Transactional
    public Product createProduct(String name, String description, Category category, Long productPrize) 
            throws SocialObjectNotJoinableException {
        Product product = new Product(this, name, description, category, productPrize);
        product.save();
        this.productCount++;
        return product;
    }
    
  /*  @Transactional
    public Community createCommunity(String name, String description, CommunityType type, String icon) 
            throws SocialObjectNotJoinableException {
        if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(description) || 
                Strings.isNullOrEmpty(icon) || type == null) {
            logger.underlyingLogger().warn("Missing parameters to createCommunity");
            return null;
        }
        Community community = new Community(name, description, this, type);
        community.icon = icon;
        community.save();
        community.ownerAsMember(this);
        return community;
    }*/

    /**
     * create a folder with the type: IMG (contain only image Resource types)
     * 
     * @param name
     * @param description
     * @param system
     * @return
     */
    public Folder createAlbum(String name, String description, Boolean system) {

        if (ensureFolderExistWithGivenName(name)) {
            Folder folder = createFolder(name, description,
                    SocialObjectType.FOLDER, system);
            folders.add(folder);
            this.merge(); // Add folder to existing User as new albumn
            return folder;
        }
        return null;
    }

    public Album createAlbum(String name, String description, Boolean system,
            SocialObjectType type) {

        if (ensureAlbumExistWithGivenName(name)) {
            Album _album = createAlbum(name, description,
                    SocialObjectType.ALBUMN, system);
            album.add(_album);
            this.merge();
            return _album;
        }
        return null;
    }

    private boolean ensureAlbumExistWithGivenName(String name) {

        if (album == null) {
            album = new ArrayList<>();
        }

        if (album.contains(new Album(name))) {
            return false;
        }

        return true;
    }

    private Album createAlbum(String name, String description,
            SocialObjectType type, Boolean system) {
        Folder folder = createFolder(name, description,
                SocialObjectType.FOLDER, system);

        Album _album = new Album(name);
        _album.owner = this;
        _album.name = name;
        _album.description = description;
        _album.objectType = type;
        _album.system = system;
        _album.folder = folder;
        _album.save();
        return _album;
    }

    private Folder createFolder(String name, String description,
            SocialObjectType type, Boolean system) {

        Folder folder = new Folder(name);
        folder.owner = this;
        folder.name = name;
        folder.description = description;
        folder.objectType = type;
        folder.system = system;
        folder.save();
        return folder;
    }

    private boolean ensureFolderExistWithGivenName(String name) {

        if (album != null && album.contains(new Folder(name))) {
            return false;
        }

        album = new ArrayList<>();
        return true;
    }

    public static boolean existsByAuthUserIdentity(
            final AuthUserIdentity identity) {
        final Query exp;
        if (identity instanceof UsernamePasswordAuthUser) {
            exp = getUsernamePasswordAuthUserFind((UsernamePasswordAuthUser) identity);
        } else {
            exp = getAuthUserFind(identity);
        }
        return exp.getResultList().size() > 0;
    }

    private static Query getAuthUserFind(final AuthUserIdentity identity) {
        Query q = JPA.em().createQuery(
                "SELECT u FROM User u, IN (u.linkedAccounts) l where active = ?1 and l.providerUserId = ?2 and l.providerKey = ?3 and u.deleted = false");
        q.setParameter(1, true);
        q.setParameter(2, identity.getId());
        q.setParameter(3, identity.getProvider());
        return q;
    }

    public static User findByAuthUserIdentity(final AuthUserIdentity identity) {
        if (identity == null) {
            return null;
        }
        
        if (identity instanceof UsernamePasswordAuthUser) {
            // Bypass login
            if ("dev".equals(controllers.Application.APPLICATION_ENV) && 
                    controllers.Application.LOGIN_BYPASS_ALL == true) {
                return User.findByEmail(identity.getId());
            }
            return findByUsernamePasswordIdentity((UsernamePasswordAuthUser) identity);
        } else {
            try {
                return (User) getAuthUserFind(identity).getSingleResult();
            } catch(NoResultException e) {
                return null;
            } catch (Exception e) {
                logger.underlyingLogger().error("Error in findByAuthUserIdentity", e);
                return null;
            }
        }
    }

    @Transactional
    public static User findByUsernamePasswordIdentity(
            final UsernamePasswordAuthUser identity) {
        try {
            return (User) getUsernamePasswordAuthUserFind(identity)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            logger.underlyingLogger().error("Error in findByUsernamePasswordIdentity", e);
            return null;
        }
    }

    @Transactional
    @JsonIgnore
    private static Query getUsernamePasswordAuthUserFind(
            final UsernamePasswordAuthUser identity) {

        Query q = JPA.em().createQuery(
                "SELECT u FROM User u, IN (u.linkedAccounts) l where active = ?1 and email = ?2 and  l.providerKey = ?3 and u.deleted = false");
        q.setParameter(1, true);
        q.setParameter(2, identity.getEmail());
        q.setParameter(3, identity.getProvider());
        return q;
    }

    public void merge(final User otherUser) {
        for (final LinkedAccount acc : otherUser.linkedAccounts) {
            this.linkedAccounts.add(LinkedAccount.create(acc));
        }
        // do all other merging stuff here - like resources, etc.

        // deactivate the merged user that got added to this one
        otherUser.active = false;
        this.merge();
        otherUser.merge();
    }

    public static User create(final AuthUser authUser) {
        final User user = new User();
        user.roles = Collections.singletonList(
                SecurityRole.findByRoleName(SecurityRole.RoleType.USER.name()));
        user.setCreatedDate(new Date());
        user.active = true;
        user.newUser = true;
        user.lastLogin = new Date();
        user.totalLogin = 1L;
        user.fbLogin = false;
        
        if (authUser instanceof EmailIdentity) {
            final EmailIdentity identity = (EmailIdentity) authUser;
            user.email = identity.getEmail();
            user.emailValidated = false;
        }

        /* 
         * User name inherited from SocialObject and it's being used 
         * in many places. No longer valid as we only shows user display name
         * now. User name will be set to display name during signup info step.
         * See Application.doSaveSignupInfo 
         * 
        if (authUser instanceof NameIdentity) {
            final NameIdentity identity = (NameIdentity) authUser;
            final String name = identity.getName();
            if (name != null) {
                user.name = name;
            }
        }
         */
        
        if (authUser instanceof FirstLastNameIdentity) {
            final FirstLastNameIdentity identity = (FirstLastNameIdentity) authUser;
            final String firstName = identity.getFirstName();
            final String lastName = identity.getLastName();
            if (firstName != null) {
                user.firstName = firstName;
            }
            if (lastName != null) {
                user.lastName = lastName;
            }
        }

        if (authUser instanceof FacebookAuthUser) {
            final FacebookAuthUser fbAuthUser = (FacebookAuthUser) authUser;
            FbUserInfo fbUserInfo = new FbUserInfo(fbAuthUser);
            fbUserInfo.save();
            
            // TODO - keith
            // save FbUserFriend here
            
            user.fbLogin = true;
            user.fbUserInfo = fbUserInfo;
            user.emailValidated = fbAuthUser.isVerified();
        }
        
        user.save();
        user.linkedAccounts = Collections.singletonList(
                LinkedAccount.create(authUser).addUser(user));
         //user.saveManyToManyAssociations("roles");
         //user.saveManyToManyAssociations("permissions");
        
        if (authUser instanceof FacebookAuthUser) {
            saveFbFriends(authUser, user);
        }
        return user;
    }

	private static void saveFbFriends(final AuthUser authUser, final User user) {
		final FacebookAuthUser fbAuthUser = (FacebookAuthUser) authUser;
		JsonNode frds = fbAuthUser.getFBFriends();
		
		if (frds.has("data")) {
			List<FbUserFriend> fbUserFriends = null;
			try{
			    fbUserFriends = new ObjectMapper().readValue(frds.get("data").traverse(), new TypeReference<List<FbUserFriend>>() {});
			} catch(Exception e) {
				
			}
			for(FbUserFriend frnd : fbUserFriends) {
				frnd.user = user;
				frnd.save();
			}
			logger.underlyingLogger().info("[u="+user.id+"] saveFbFriends="+fbUserFriends.size());
		}
	}

    public static void merge(final AuthUser oldUser, final AuthUser newUser) {
        User.findByAuthUserIdentity(oldUser).merge(
                User.findByAuthUserIdentity(newUser));
    }

    @JsonIgnore
    public Set<String> getProviders() {
        final Set<String> providerKeys = new HashSet<String>(
                linkedAccounts.size());
        for (final LinkedAccount acc : linkedAccounts) {
            providerKeys.add(acc.providerKey);
        }
        return providerKeys;
    }

    public static void addLinkedAccount(final AuthUser oldUser,
            final AuthUser newUser) {
        final User u = User.findByAuthUserIdentity(oldUser);
        u.linkedAccounts.add(LinkedAccount.create(newUser));
        u.save();
    }

    public static void setLastLoginDate(final AuthUser knownUser) {
        final User u = User.findByAuthUserIdentity(knownUser);
        u.lastLogin = new Date();
        u.save();
    }

    public static User findByEmail(final String email) {
        try {
            Query q = JPA.em().createQuery(
                    "SELECT u FROM User u where active = ?1 and email = ?2 and deleted = false");
            q.setParameter(1, true);
            q.setParameter(2, email);
            return (User) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            logger.underlyingLogger().error("Error in findByEmail", e);
            return null;
        }
    }
    
    public static User findByFbEmail(final String email) {
        try {
            Query q = JPA.em().createQuery(
                    "SELECT u FROM User u where active = ?1 and email = ?2 and providerKey = ?3 and deleted = false");
            q.setParameter(1, true);
            q.setParameter(2, email);
            q.setParameter(3, FacebookAuthProvider.PROVIDER_KEY);
            return (User) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            logger.underlyingLogger().error("Error in findByFbEmail", e);
            return null;
        }
    }
    
    @Transactional
    public boolean isSuperAdmin() {
        for (SecurityRole role : roles) {
            if (SecurityRole.RoleType.SUPER_ADMIN.name().equals(role.roleName)) {
                return true;
            }
        }
        return false;
    }
    
    @Transactional
    public static User getBBAdmin() {
        if (BB_ADMIN != null)
            return BB_ADMIN;
        User superAdmin = getSuperAdmin(BB_ADMIN_NAME);
        if (superAdmin == null) {
            superAdmin = getSuperAdmin();
        }
        BB_ADMIN = superAdmin;
        return superAdmin;
    }
    
    @Transactional
    public static User getBBEditor() {
        if (BB_EDITOR != null)
            return BB_EDITOR;
        User superAdmin = getSuperAdmin(BB_EDITOR_NAME);
        if (superAdmin == null) {
            superAdmin = getSuperAdmin();
        }
        BB_EDITOR = superAdmin;
        return superAdmin;
    }
    
    @Transactional
    public static User getSuperAdmin(String name) {
        Query q = JPA.em().createQuery(
                "SELECT u FROM User u where name = ?1 and active = ?2 and system = ?3 and deleted = false");
        q.setParameter(1, name);
        q.setParameter(2, true);
        q.setParameter(3, true);
        try {
            User sysUser = (User)q.getSingleResult();
            return sysUser;
        } catch (NoResultException e) {
            logger.underlyingLogger().error("SuperAdmin not found - "+name, e);
            return null;
        }
    }
    
    @Transactional
    public static User getSuperAdmin() {
        Query q = JPA.em().createQuery(
                "SELECT u FROM User u where id = ?1 and active = ?2 and system = ?3 and deleted = false");
        q.setParameter(1, 1);
        q.setParameter(2, true);
        q.setParameter(3, true);
        try {
            User sysUser = (User)q.getSingleResult();
            return sysUser;
        } catch (NoResultException e) {
            logger.underlyingLogger().error("SuperAdmin not found", e);
            return null;
        }
    }
    
    @Transactional
    public boolean isBusinessAdmin() {
        for (SecurityRole role : roles) {
            if (SecurityRole.RoleType.BUSINESS_ADMIN.name().equals(role.roleName)) {
                return true;
            }
        }
        if (isSuperAdmin()) {
            return true;
        }
        return false;
    }
    
    @Transactional
    public boolean isCommunityAdmin() {
        for (SecurityRole role : roles) {
            if (SecurityRole.RoleType.COMMUNITY_ADMIN.name().equals(role.roleName)) {
                return true;
            }
        }
        if (isSuperAdmin()) {
            return true;
        }
        return false;
    }
    
    @Transactional
    public boolean isEditor() {
        for (SecurityRole role : roles) {
            if (SecurityRole.RoleType.EDITOR.name().equals(role.roleName)) {
                return true;
            }
        }
        if (isSuperAdmin()) {
            return true;
        }
        return false;
    }
    
    public static boolean isDisplayNameValid(String displayName) {
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(displayName);
        boolean containsWhitespace = matcher.find();
        return !containsWhitespace;
    }
    
    @Transactional
    public static boolean isDisplayNameExists(String displayName) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        Query q = JPA.em().createQuery(
                "SELECT count(u) FROM User u where " +  
                        "system = false and deleted = false and " + 
                        "displayName = ?1");
        q.setParameter(1, displayName);
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            logger.underlyingLogger().error("[displayName="+displayName+"][count="+count+"] already exists");
        }
        boolean exists = count > 0;
        
        sw.stop();
        logger.underlyingLogger().info("isDisplayNameExists="+exists+". Took "+sw.getElapsedMS()+"ms");
        return exists;
    }

    @Transactional
    public static Long getTodaySignupCount() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        Query q = JPA.em().createQuery(
                "SELECT count(u) FROM User u where " +  
                        "system = false and deleted = false and " + 
                        "CREATED_DATE >= ?1 and CREATED_DATE < ?2");
        q.setParameter(1, DateTimeUtil.getToday().toDate());
        q.setParameter(2, DateTimeUtil.getTomorrow().toDate());
        Long count = (Long)q.getSingleResult();

        sw.stop();
        logger.underlyingLogger().info("getTodaySignupCount="+count+". Took "+sw.getElapsedMS()+"ms");
        return count;
    }

    @Transactional
    public static Pair<Integer,String> getAndroidTargetEdmUsers() {
        StringBuilder sb = new StringBuilder();

        Query q = JPA.em().createNativeQuery(
            "select CONCAT(id,',',email,',',firstName,',',lastName,',') from User where deleted=0 and emailValidated=1 and "+
            "email is not null and email not like '%abc.com' and email not like '%xxx.com' and firstName is not null and lastName is not null and id not in (1,2,4,5,102,1098,1124,575,1374,1119,1431) "+
            "and id not in (select g.userId from gameaccounttransaction g where g.transactionDescription like '%APP%') "+
            "and (lastLoginUserAgent is NULL OR lastLoginUserAgent not like '%iphone%') "+
            "order by id");
        List<Object> results = (List<Object>) q.getResultList();
        for (Object res : results) {
            if (res instanceof String) {
                sb.append((String)res).append("\n");
            } else {
                try {
                    sb.append(new String((byte[])res, "UTF-8")).append("\n");
                } catch (Exception e) {
                    logger.underlyingLogger().error("Failed to create string");
                }
            }
        }
        return new Pair<>(results.size(),sb.toString());
    }

    @JsonIgnore
    public LinkedAccount getAccountByProvider(final String providerKey) {
        return LinkedAccount.findByProviderKey(this, providerKey);
    }

    public static void verify(final User unverified) {
        // You might want to wrap this into a transaction
        unverified.emailValidated = true;
        unverified.save();
        TokenAction.deleteByUser(unverified, Type.EMAIL_VERIFICATION);
    }

    public void changePassword(final UsernamePasswordAuthUser authUser,
            final boolean create) {
        LinkedAccount a = this.getAccountByProvider(authUser.getProvider());
        if (a == null) {
            if (create) {
                a = LinkedAccount.create(authUser);
                a.user = this;
            } else {
                throw new RuntimeException(
                        "Account not enabled for password usage");
            }
        }
        a.providerUserId = authUser.getHashedPassword();
        a.save();
    }

    public void resetPassword(final UsernamePasswordAuthUser authUser,
            final boolean create) {
        // You might want to wrap this into a transaction
        this.changePassword(authUser, create);
        TokenAction.deleteByUser(this, Type.PASSWORD_RESET);
    }

    public static User findById(Long id) {
        try { 
            Query q = JPA.em().createQuery("SELECT u FROM User u where id = ?1 and deleted = false");
            q.setParameter(1, id);
            return (User) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            logger.underlyingLogger().error("Error in findById", e);
            return null;
        }
    }

    public static File getDefaultUserPhoto() throws FileNotFoundException {
         return new File(Play.application().configuration().getString("storage.user.noimage"));
    }
    
    public static File getDefaultThumbnailUserPhoto() throws FileNotFoundException {
        return new File(Play.application().configuration().getString("storage.user.noimage"));
    }

    public static File getDefaultCoverPhoto() throws FileNotFoundException {
         return new File(Play.application().configuration().getString("storage.user.cover.noimage"));
    }
    
    public int doUnLike(Long id, SocialObjectType type) {
        Query query = JPA.em().createQuery(
                "SELECT sr FROM PrimarySocialRelation sr where sr.targetType = ?4 and sr.action = ?3 and " + 
                "(sr.target = ?1 and sr.actor = ?2)", PrimarySocialRelation.class);
        query.setParameter(1, id);
        query.setParameter(2, this.id);
        query.setParameter(3, PrimarySocialRelation.Action.LIKED);
        query.setParameter(4, type);
        
        try {
        	PrimarySocialRelation sr = (PrimarySocialRelation) query.getSingleResult();
        	sr.delete();

        	//GameAccountStatistics.recordunLike(this.id);
        } catch (NoResultException e) {
        	logger.underlyingLogger().error(String.format("[u=%d][sr.actor=%d][sr.type=%s] like not found", this.id, id, type.name()), e);
        } catch (Exception e) {
            logger.underlyingLogger().error("Error in doUnLike", e);
        }
        return 1;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
        if (this.totalLogin == null) {
            this.totalLogin = 1L;   // prev user, at least 1 login
        }
        this.totalLogin++;
    }

    public List<UserChild> getChildren() {
        return children;
    }

    public void setChildren(List<UserChild> children) {
        this.children = children;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isEmailValidated() {
        return emailValidated;
    }

    public void setEmailValidated(boolean emailValidated) {
        this.emailValidated = emailValidated;
    }

    public boolean isHomeTourCompleted() {
        List<SiteTour> tours = SiteTour.getSiteTours(id);
        if (tours != null) {
            for (SiteTour tour : tours) {
                if (SiteTour.TourType.HOME.equals(tour.tourType)) {
                    return true;        
                }
            }
        }
        return false;
    }
    
    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }
        
    public List<LinkedAccount> getLinkedAccounts() {
        return linkedAccounts;
    }

    public void setLinkedAccounts(List<LinkedAccount> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }

    public Folder getAlbumPhotoProfile() {
        return albumPhotoProfile;
    }

    public void setAlbumPhotoProfile(Folder albumPhotoProfile) {
        this.albumPhotoProfile = albumPhotoProfile;
    }

    public Folder getAlbumCoverProfile() {
        return albumCoverProfile;
    }

    public void setAlbumCoverProfile(Folder albumCoverProfile) {
        this.albumCoverProfile = albumCoverProfile;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }

    public List<Album> getAlbum() {
        return album;
    }

    public void setAlbum(List<Album> album) {
        this.album = album;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    public void setRoles(List<SecurityRole> roles) {
        this.roles = roles;
    }

    public void setPermissions(List<UserPermission> permissions) {
        this.permissions = permissions;
    }
    
    public List<Conversation> findMyConversations() {
        return Conversation.findAllConversations(this,DefaultValues.CONVERSATION_COUNT);
    }
    
    public Conversation findMyConversationWith(User u) {
        return Conversation.findByUsers(this, u);
    }
    
    public void startChat(User user2) {
        Conversation.startConversation(this, user2);
    }
    
    public Long getUnreadConversationCount() {
        return Conversation.getUnreadConversationCount(this.id);
    }

    ///////////////////////////////////////////////
    // Login, noLogin
    private static final Long NO_LOGIN_ID = -1L;

    public boolean isLoggedIn() {
        return isLoggedIn(this);
    }
    
    public static boolean isLoggedIn(User user) {
        return user != null && user.id != NO_LOGIN_ID;
    }

    public static boolean isLoggedIn(Long userId) {
        return userId != NO_LOGIN_ID;
    }
    
    public static User noLoginUser() {
        User noLoginUser = new User();
        noLoginUser.id = NO_LOGIN_ID;
        return noLoginUser;
    }
    ///////////////////////////////////////////////

	public Collection createCollection(String name, String description,
			Category category) {
		Collection collection = new Collection(this, name, description, category);
	        collection.save();
	        this.collectionCount++;
	        return collection;
	}
	
	public Collection createCollection(String name) {
		Collection collection = new Collection(this, name);
	        collection.save();
	        this.collectionCount++;
	        return collection;
	}
	
	
	@Override
    public void onFollowedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[user="+user.id+"][u="+id+"] User onFollowedBy");
        }

        recordFollow(user);
        user.followersCount++;
        this.followingCount++;
    }
    
    @Override
    public void onUnFollowedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[user="+user.id+"][u="+id+"] User onUnFollowedBy");
        }
        user.followersCount--;
        this.followingCount--;
        Query q = JPA.em().createQuery("Delete from SecondarySocialRelation sa where actor = ?1 and action = ?2 and target = ?3 and actorType = ?4 and targetType = ?5");
        q.setParameter(1, user.id);
		q.setParameter(2, SecondarySocialRelation.Action.FOLLOWED);
		q.setParameter(3, this.id);
		q.setParameter(4, SocialObjectType.USER);
		q.setParameter(5, SocialObjectType.USER);
		q.executeUpdate();
    }
    
    @JsonIgnore
    public boolean isFollowedBy(User user) {
    	Query q = JPA.em().createQuery("Select sa from SecondarySocialRelation sa where actor = ?1 and action = ?2 and target = ?3 and actorType = ?4 and targetType = ?5");
		q.setParameter(1, this.id);
		q.setParameter(2, SecondarySocialRelation.Action.FOLLOWED);
		q.setParameter(3, user.id);
		q.setParameter(4, SocialObjectType.USER);
		q.setParameter(5, SocialObjectType.USER);
		if(q.getResultList().size() > 0 ) {
			return true;
		}
        return false;
    }

	public List<Collection> getUserCollection() {
		try {
			Query q = JPA.em().createQuery("SELECT p FROM Collection p where deleted = false and owner = ?");
			q.setParameter(1, this);
			return (List<Collection>) q.getResultList();
		} catch (NoResultException nre) {
			return null;
		}
	}
}
