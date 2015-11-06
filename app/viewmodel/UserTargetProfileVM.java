package viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import common.model.TargetProfile;

public class UserTargetProfileVM {
    @JsonProperty("gen") private String gender;
    @JsonProperty("loc") private String location;
    @JsonProperty("nc") private int numChildren;
    @JsonProperty("cgen") private String childrenGender;
    @JsonProperty("cmin") private int childrenMinAgeMonths;
    @JsonProperty("cmax") private int childrenMaxAgeMonths;
    @JsonProperty("soon") private boolean isSoonParent;
    @JsonProperty("new") private boolean isNewParent;
    
    // UI controlling flags
    @JsonProperty("pn") private boolean recommendPN;
    
    public UserTargetProfileVM(TargetProfile targetProfile) {
        this.gender = targetProfile.getParentGender().name();
        this.location = targetProfile.getLocation().getDisplayName();
        this.numChildren = targetProfile.getNumChildren();
        this.childrenGender = targetProfile.getChildrenGender().name();
        this.childrenMinAgeMonths = targetProfile.getChildrenMinAgeMonths();
        this.childrenMaxAgeMonths = targetProfile.getChildrenMaxAgeMonths();
        this.isSoonParent = targetProfile.isSoonParent();
        this.isNewParent = targetProfile.isNewParent();
        
        // UI controlling flags
        this.recommendPN = targetProfile.isPreNurseryApplicable();
    }
}
