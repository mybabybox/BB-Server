package viewmodel;

import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

import controllers.Application;

public class UserVM {
    @JsonProperty("id") public Long id;
    @JsonProperty("firstName") public String firstName;
    @JsonProperty("lastName") public String lastName;
    @JsonProperty("displayName") public String displayName;
    @JsonProperty("email") public String email;
    @JsonProperty("birthYear") public String birthYear;
    @JsonProperty("gender") public String gender;
    @JsonProperty("aboutMe") public String aboutMe;
    @JsonProperty("noOfFollwers") public Long noOfFollwers;
    @JsonProperty("noOfFollowing") public int noOfFollowing;
    @JsonProperty("isLoggedIn") public boolean isLoggedIn = false;
    @JsonProperty("isSA") public boolean isSuperAdmin = false;
    @JsonProperty("isBA") public boolean isBusinessAdmin = false;
    @JsonProperty("isCA") public boolean isCommunityAdmin = false;
    @JsonProperty("isE") public boolean isEditor = false;
    @JsonProperty("isAdmin") public boolean isAdmin = false;
    @JsonProperty("isMobile") public boolean isMobile = false;
    @JsonProperty("isFbLogin") public boolean isFbLogin = false;
    @JsonProperty("isHomeTourCompleted") public boolean isHomeTourCompleted = false;
    @JsonProperty("productCount") public Long productCount;
    @JsonProperty("collectionCount") public Long collectionCount;
    
    // signup verification
    @JsonProperty("emailValidated") public boolean emailValidated = false;
    @JsonProperty("newUser") public boolean newUser = false;
    
    // game
    @JsonProperty("enableSignInForToday") public boolean enableSignInForToday = false;
    
	public UserVM(User user) {
		if(user == null){
			return;
		}
	    this.id = user.id;
	    this.isLoggedIn = user.isLoggedIn();
	    if (user.isLoggedIn()) {
    		this.firstName = user.firstName;
    		this.lastName = user.lastName;
    		this.displayName = user.displayName;
    		this.email = user.email;
    		if(user.userInfo != null) {
    			this.birthYear = user.userInfo.birthYear;
    			if(user.userInfo.gender != null) {
    				this.gender = user.userInfo.gender.name();
    			}
    			this.aboutMe = user.userInfo.aboutMe;
        		//this.noOfGroups = user.getListOfJoinedCommunityIds().size();
    		}
    		this.productCount = user.productCount;
    		this.collectionCount = user.collectionCount;
	    }
	    
	    this.emailValidated = user.emailValidated;
	    this.newUser = user.newUser;
	}
}
