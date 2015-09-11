package models;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class TargetingSocialObject extends SocialObject {

    @Column(nullable=false)
    public boolean excludeFromTargeting = false;
    
    @Enumerated(EnumType.STRING)
    public TargetingType targetingType;
    
    @Column(nullable=true)          // targetingType, targetingInfo combined must be unique
    public String targetingInfo;
    
    @Column(nullable=true)
    public Integer targetAgeMinMonth;
    
    @Column(nullable=true)
    public Integer targetAgeMaxMonth;
    
    @Column(nullable=true)
    public Integer targetGender;
   
    @Column(nullable=true)
    public Integer targetParentGender;
    
    @ManyToOne
    public Location targetLocation;
    
    public static enum TargetingType {
        ALL_MOMS_DADS,
        NEW_MOMS_DADS,
        SOON_MOMS_DADS,
        PUBLIC,
        SCORE,
        ZODIAC_YEAR,
        ZODIAC_YEAR_MONTH,
        LOCATION_DISTRICT,
        LOCATION_REGION,
        PLAYGROUP,
        PRE_NURSERY,
        KINDY,
        OTHER
    }
    
    public TargetingSocialObject() {
    }

    public boolean isExcludeFromTargeting() {
        return excludeFromTargeting;
    }

    public void setExcludeFromTargeting(boolean excludeFromTargeting) {
        this.excludeFromTargeting = excludeFromTargeting;
    }

    public TargetingType getTargetingType() {
        return targetingType;
    }

    public void setTargetingType(TargetingType targetingType) {
        this.targetingType = targetingType;
    }

    public String getTargetingInfo() {
        return targetingInfo;
    }

    public void setTargetingInfo(String targetingInfo) {
        this.targetingInfo = targetingInfo;
    }

    public int getTargetAgeMinMonth() {
        return targetAgeMinMonth;
    }

    public void setTargetAgeMinMonth(int targetAgeMinMonth) {
        this.targetAgeMinMonth = targetAgeMinMonth;
    }

    public int getTargetAgeMaxMonth() {
        return targetAgeMaxMonth;
    }

    public void setTargetAgeMaxMonth(int targetAgeMaxMonth) {
        this.targetAgeMaxMonth = targetAgeMaxMonth;
    }

    public int getTargetGender() {
        return targetGender;
    }

    public void setTargetGender(int targetGender) {
        this.targetGender = targetGender;
    }

    public int getTargetParentGender() {
        return targetParentGender;
    }

    public void setTargetParentGender(int targetParentGender) {
        this.targetParentGender = targetParentGender;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }
}
