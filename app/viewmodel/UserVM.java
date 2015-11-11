package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

import controllers.Application;
import models.Setting;
import models.User;

public class UserVM extends UserVMLite {
	@JsonProperty("firstName") public String firstName;
    @JsonProperty("lastName") public String lastName;
    @JsonProperty("email") public String email;
    @JsonProperty("birthYear") public String birthYear;
    @JsonProperty("gender") public String gender;
    @JsonProperty("aboutMe") public String aboutMe;
    @JsonProperty("location") public LocationVM location;
    @JsonProperty("setting") public SettingVM setting;
    @JsonProperty("isMobile") public boolean isMobile = false;

    // admin readyonly fields
    @JsonProperty("createdDate") public Long createdDate;
    @JsonProperty("lastLogin") public Long lastLogin;
    @JsonProperty("totalLogin") public Long totalLogin;
    @JsonProperty("isLoggedIn") public boolean isLoggedIn = false;
    @JsonProperty("isFbLogin") public boolean isFbLogin = false;
    @JsonProperty("emailValidated") public boolean emailValidated = false;
    @JsonProperty("newUser") public boolean newUser = false;
    @JsonProperty("isAdmin") public boolean isAdmin = false;

    public UserVM(User user) {
    	this(user, user);
    }
    
    public UserVM(User user, User localUser) {
    	super(user, localUser);
    	
    	if (!user.isLoggedIn()) {
    	    return;
    	}
    	
        this.email = user.email;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        if (user.userInfo != null) {
        	this.gender = user.userInfo.gender.name();
            this.birthYear = user.userInfo.birthYear;
            this.aboutMe = user.userInfo.aboutMe;
            this.location = new LocationVM(user.userInfo.location);
            if (user.id == localUser.id) {
                this.setting = new SettingVM(Setting.findByUserId(user.id));
            }
        }
        this.isMobile = Application.isMobileUser();
        
        this.createdDate = user.getCreatedDate().getTime();
        this.lastLogin = user.lastLogin.getTime();
        this.totalLogin = user.totalLogin;
        this.isLoggedIn = user.isLoggedIn();
        this.isFbLogin = user.fbLogin;
        this.emailValidated = user.emailValidated;
        this.newUser = user.isNewUser();
        this.isAdmin = user.isSuperAdmin();
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public LocationVM getLocation() {
        return location;
    }

    public void setLocation(LocationVM location) {
        this.location = location;
    }

    public Long getTotalLogin() {
        return totalLogin;
    }

    public void setTotalLogin(Long totalLogin) {
        this.totalLogin = totalLogin;
    }

    public Long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public boolean isFbLogin() {
        return isFbLogin;
    }

    public void setIsFbLogin(boolean isFbLogin) {
        this.isFbLogin = isFbLogin;
    }

    public boolean getEmailValidated() {
        return emailValidated;
    }

    public void setEmailValidated(boolean emailValidated) {
        this.emailValidated = emailValidated;
    }

    public boolean getNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "id=" + id + "\n" +
                "email=" + email + "\n" +
                "emailValidated=" + emailValidated + "\n" +
                "fbLogin=" + isFbLogin + "\n" +
                "signupDate=" + createdDate + "\n" +
                "lastLogin=" + lastLogin + "\n" +
                "totalLogin=" + totalLogin + "\n" +
                "numLikes=" + numLikes + "\n" +
                "numFollowers=" + numFollowers + "\n" +
                "numFollowings=" + numFollowings + "\n" +
                "numProducts=" + numProducts + "\n" +
                "numStories=" + numStories;
    }
}

