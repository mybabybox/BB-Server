package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthUser;

import play.db.jpa.JPA;

/**
 * https://developers.facebook.com/docs/graph-api/reference/v2.0/user
 * 
 * @author keithlei
 *
 */
@Entity
public class FbUserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    public String fbId;
    public String email;
    public String name;
    public String firstName;
    public String lastName;
    public String profilePic;
    public String birthday;
    public String gender;
    public String ageRange;
    public String relationshipStatus;
    public String link;
    public int timezone;
    public String locale;
    public String educationType;
    public String educationSchool;
    public String educationYear;
    public String educationConcentration;
    public String coverId;
    public String coverSource;
    public String coverOffsetY;
    
    public FbUserInfo() {
    }
    
    public FbUserInfo(FacebookAuthUser fbAuthUser) {
        this.fbId = fbAuthUser.getId();
        this.email = fbAuthUser.getEmail();
        this.name = fbAuthUser.getName();
        this.firstName = fbAuthUser.getFirstName();
        this.lastName = fbAuthUser.getLastName();
        this.profilePic = fbAuthUser.getPicture();
        this.birthday = fbAuthUser.getBirthday();
        this.gender = fbAuthUser.getGender();
        this.ageRange = fbAuthUser.getAgeRange();
        this.relationshipStatus = fbAuthUser.getRelationshipStatus();
        this.link = fbAuthUser.getProfileLink();
        this.timezone = fbAuthUser.getTimezone();
        this.locale = fbAuthUser.getLocale().toString();
        this.educationType = fbAuthUser.getEducationType();
        this.educationSchool = fbAuthUser.getEducationSchool();
        this.educationYear = fbAuthUser.getEducationYear();
        this.educationConcentration = fbAuthUser.getEducationConcentration();
        this.coverId = fbAuthUser.getCoverId();
        this.coverSource = fbAuthUser.getCoverSource();
        this.coverOffsetY = fbAuthUser.getCoverOffsetY();
    }
    
    public static FbUserInfo findByUserId(Long id) {
    	Query q = JPA.em().createQuery("SELECT fbui FROM FbUserInfo fbui where user_id = ?1");
    	q.setParameter(1, id);
    	try {
    		return (FbUserInfo)q.getSingleResult();
    	} catch(NoResultException e) {
    		return null;
    	}
    }
    
    public void save() {
    	JPA.em().persist(this);
    	JPA.em().flush();
    }
}