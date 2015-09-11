package common.model;

import models.Location;
import models.User;
import models.UserChild;

import org.joda.time.DateTime;
import org.joda.time.Months;

import common.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 31/5/14
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class TargetProfile {
    private static final play.api.Logger logger = play.api.Logger.apply(TargetProfile.class);

    // parent
    private TargetGender parentGender;
    private Location location;

    // children
    private int numChildren;
    private TargetGender childrenGender;
    private int childrenMinAgeMonths;
    private int childrenMaxAgeMonths;
    
    private List<TargetYear> childYears;
    private List<Integer> childMonths;
    private List<DateTime> childBirthDates;
    
    // TODO: Add Twins Target Support

    public static TargetProfile fromUser(User user) {
        TargetProfile profile = new TargetProfile();
        List<TargetYear> childYears = new ArrayList<TargetYear>();
        List<Integer> childMonths = new ArrayList<Integer>();
        List<DateTime> childBirthDates = new ArrayList<DateTime>();
        
        if (user.userInfo == null) {
            logger.underlyingLogger().warn("[u="+user.id+"] UserInfo is null");
            return null;
        }
        
        // parent
        profile.parentGender = user.userInfo.gender;
        profile.location = user.userInfo.location;

        // children
        TargetGender childrenGender = TargetGender.Both;
        Integer childrenMinAge = null;
        Integer childrenMaxAge = null;

        List<UserChild> children = user.getChildren();
        if (children != null) {
            profile.numChildren = children.size();

            boolean hasBoy = false, hasGirl = false;
            for (UserChild child : children) {
                if (TargetGender.Male.equals(child.gender)) {
                    hasBoy = true;
                }
                else if (TargetGender.Female.equals(child.gender)) {
                    hasGirl = true;
                }

                if (child.birthYear != null && child.birthMonth != null) {
                    DateTime birthDate = DateTimeUtil.parseDate(child.birthYear, child.birthMonth, child.birthDay);
                    Months months = Months.monthsBetween(birthDate, DateTime.now());

                    if (childrenMinAge == null || months.getMonths() < childrenMinAge) {
                        childrenMinAge = months.getMonths();
                    }
                    if (childrenMaxAge == null || months.getMonths() > childrenMaxAge) {
                        childrenMaxAge = months.getMonths();
                    }
                    childYears.add(TargetYear.valueOf(birthDate));
                    childMonths.add(months.getMonths());
                    childBirthDates.add(birthDate);
                }
            }
            if (hasBoy && hasGirl) {
                childrenGender = TargetGender.Both;
            } else if (hasBoy) {
                childrenGender = TargetGender.Male;
            } else if (hasGirl) {
                childrenGender = TargetGender.Female;
            }
        }
        
        profile.childrenGender = childrenGender;
        profile.childrenMinAgeMonths = (childrenMinAge == null) ? Integer.MIN_VALUE : childrenMinAge;
        profile.childrenMaxAgeMonths = (childrenMaxAge == null) ? Integer.MAX_VALUE : childrenMaxAge;
        profile.childYears = childYears;
        profile.childMonths = childMonths;
        profile.childBirthDates = childBirthDates;
        
        return profile;
    }

    public TargetGender getParentGender() {
        return parentGender;
    }

    public Location getLocation() {
        return location;
    }

    public int getNumChildren() {
        return numChildren;
    }

    public TargetGender getChildrenGender() {
        return childrenGender;
    }

    public int getChildrenMinAgeMonths() {
        return childrenMinAgeMonths;
    }

    public int getChildrenMaxAgeMonths() {
        return childrenMaxAgeMonths;
    }

    public List<TargetYear> getChildYears() {
        return childYears;
    }
    
    public List<Integer> getChildMonths() {
        return childMonths;
    }
    
    public List<DateTime> getChildBirthDates() {
        return childBirthDates;
    }
    
    public boolean isSoonParent() {
        return childrenMinAgeMonths < 0;
    }
    
    public boolean isNewParent() {
        return childrenMinAgeMonths >= 0 && childrenMinAgeMonths < 12;
    }

    public boolean isPreNurseryApplicable() {
        for (DateTime birthDate : getChildBirthDates()) {
            Months months = Months.monthsBetween(birthDate, DateTime.now());
            if (months.getMonths() >= 6 && months.getMonths() <= 30) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "TargetProfile{" +
                "parentGender=" + parentGender +
                ", location='" + location + '\'' +
                ", numChildren=" + numChildren +
                ", childrenGender=" + childrenGender +
                ", childrenMinAgeMonths=" + childrenMinAgeMonths +
                ", childrenMaxAgeMonths=" + childrenMaxAgeMonths +
                '}';
    }
}
