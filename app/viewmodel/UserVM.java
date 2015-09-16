package viewmodel;

import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

import controllers.Application;

public class UserVM {
    public Long id;
    public String firstName;
    public String lastName;
    public String displayName;
    public String email;
    public String birthYear;
    public String gender;
    public String aboutMe;
    public Long noOfFollwers;
    public int noOfFollowing;
    public boolean isLoggedIn = false;
    public boolean isSA = false;
    public boolean isBA = false;
    public boolean isCA = false;
    public boolean isE = false;
    public boolean isAdmin = false;
    public boolean isMobile = false;
    public boolean isFbLogin = false;
    public boolean isHomeTourCompleted = false;
    public Long productCount;
    public Long collectionCount;
    
    // signup verification
    public boolean emailValidated = false;
    public boolean newUser = false;
    
    // game
    public boolean enableSignInForToday = false;
    
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
