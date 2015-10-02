package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

import models.Location;
import models.User;

public class ProfileVM {
	
	@JsonProperty("id")  public long id;
    @JsonProperty("dn")	 public String displayName;
    @JsonProperty("yr")  public String birthYear;
    @JsonProperty("gd")  public String gender;
    @JsonProperty("a")   public String aboutMe;
    @JsonProperty("loc") public Location location;
    
    @JsonProperty("n_l") public long numLikes = 0L;
    @JsonProperty("n_fr") public long numFollowers = 0L;
    @JsonProperty("n_fg") public long numFollowings = 0L;
    @JsonProperty("n_p") public long numProducts;
    @JsonProperty("n_c") public long numCollections;

    // admin readonly fields
    @JsonProperty("n")  public String name;
    @JsonProperty("mb")  public boolean mobileSignup;
    @JsonProperty("fb")  public boolean fbLogin;
    @JsonProperty("vl")  public boolean emailValidated;
    @JsonProperty("em")  public String email;
    
    @JsonProperty("ilu")  public boolean isLoggedinUser = false;
    @JsonProperty("ifu")  public boolean isFollowdByUser = false;
    
    public String sd;
    public String ll;
    public Long tl;
    public Long qc;
    public Long ac;
    public Long lc;
    
    public static ProfileVM profile(User user, User localUser) {
        ProfileVM vm = new ProfileVM();
        vm.displayName = user.displayName;
        
        if(user.id == localUser.id){
        	vm.isLoggedinUser = true;
        	vm.isFollowdByUser = false;
        }
        if(user.userInfo != null) {
        	vm.birthYear = user.userInfo.birthYear;
			if(user.userInfo.gender != null) {
				vm.gender = user.userInfo.gender.name();
			}
			vm.aboutMe = user.userInfo.aboutMe;
			vm.location = user.userInfo.location;
		}
        
        vm.id = user.id;
        vm.numLikes = user.numLikes;
        vm.numProducts = user.numProducts;
        vm.numCollections = user.numCollections;
        vm.numFollowers = user.numFollowers;
        vm.numFollowings = user.numFollowings;
        vm.isFollowdByUser = user.isFollowedBy(localUser);
        return vm;
    }
}