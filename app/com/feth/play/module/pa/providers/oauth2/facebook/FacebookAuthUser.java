package com.feth.play.module.pa.providers.oauth2.facebook;

import java.util.Locale;

import org.codehaus.jackson.JsonNode;

import com.feth.play.module.pa.providers.oauth2.BasicOAuth2AuthUser;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.ExtendedIdentity;
import com.feth.play.module.pa.user.LocaleIdentity;
import com.feth.play.module.pa.user.PicturedIdentity;
import com.feth.play.module.pa.user.ProfiledIdentity;

public class FacebookAuthUser extends BasicOAuth2AuthUser implements
		ExtendedIdentity, PicturedIdentity, ProfiledIdentity, LocaleIdentity {

	private static final long serialVersionUID = 1L;

	/*
	 * userInfoFields="id,email,cover,name,first_name,last_name,name_format,birthday,gender,age_range,relationship_status,location,link,timezone,locale,education,updated_time"
	 */
	private static abstract class Constants {
		public static final String ID = "id";                     // "616473731"
		public static final String EMAIL = "email";               // "joscha@feth.com"
		public static final String COVER = "cover";
		public static final String COVER_ID = "id";
		public static final String COVER_SOURCE = "source";
		public static final String COVER_OFFSET_Y = "offset_y";
		public static final String NAME = "name";                 // "Joscha Feth"
		public static final String FIRST_NAME = "first_name";     // "Joscha"
		public static final String LAST_NAME = "last_name";       // "Feth"
		public static final String BIRTHDAY = "birthday";         // "25/12/1980"
		public static final String GENDER = "gender";             // "male"
		public static final String AGE_RANGE = "age_range";
		public static final String RELATIONSHIP_STATUS = "relationship_status";
		public static final String LINK = "link";                 // "http://www.facebook.com/joscha.feth"
		public static final String TIME_ZONE = "timezone";        // 8
		public static final String LOCALE = "locale";             // "de_DE"
		public static final String EDUCATION = "education";
		public static final String EDUCATION_TYPE = "type";
		public static final String EDUCATION_SCHOOL = "school";
		public static final String EDUCATION_YEAR = "year";
		public static final String EDUCATION_CONCENTRATION = "concentration";
		public static final String VERIFIED = "verified";         // true
		public static final String UPDATED_TIME = "updated_time"; // "2012-04-26T20:22:52+0000"
		
	}

	private String email;
    private String coverId;
    private String coverSource;
    private String coverOffsetY;
    private String name;
    private String firstName;
    private String lastName;
    private String birthday;
    private String gender;
    private String ageRange;
    private String relationshipStatus;
    private String link;
    private int timezone;
    private String locale;
    private String educationType;
    private String educationSchool;
    private String educationYear;
    private String educationConcentration;
    private boolean verified = false;
    private String updatedTime;
    
    private JsonNode friends;
    
    public FacebookAuthUser withFBFriends(JsonNode result) {
    	friends = result;
    	return this;
    }
    
    public JsonNode  getFBFriends() {
    	return friends;
    }
    
    public FacebookAuthUser(final JsonNode node, final FacebookAuthInfo info,
    		final String state) {
        super(node.get(Constants.ID).asText(), info, state);
        
        if (node.has(Constants.NAME)) {
            this.name = node.get(Constants.NAME).asText();
        }
        if (node.has(Constants.FIRST_NAME)) {
            this.firstName = node.get(Constants.FIRST_NAME).asText();
        }
        if (node.has(Constants.LAST_NAME)) {
            this.lastName = node.get(Constants.LAST_NAME).asText();
        }
        if (node.has(Constants.LINK)) {
            this.link = node.get(Constants.LINK).asText();
        }
        if (node.has(Constants.GENDER)) {
            this.gender = node.get(Constants.GENDER).asText();
        }
        if (node.has(Constants.EMAIL)) {
            this.email = node.get(Constants.EMAIL).asText();
        }
        if (node.has(Constants.TIME_ZONE)) {
            this.timezone = node.get(Constants.TIME_ZONE).asInt();
        }
        if (node.has(Constants.LOCALE)) {
            this.locale = node.get(Constants.LOCALE).asText();
        }
        if (node.has(Constants.BIRTHDAY)) {
            this.birthday = node.get(Constants.BIRTHDAY).asText();
        }
        if (node.has(Constants.VERIFIED)) {
            this.verified = node.get(Constants.VERIFIED).asBoolean(false);
        }
        if (node.has(Constants.UPDATED_TIME)) {
            this.updatedTime = node.get(Constants.UPDATED_TIME).asText();
        }
        
        if (node.has(Constants.AGE_RANGE)) {
            this.ageRange = node.get(Constants.AGE_RANGE).asText();
        }
        if (node.has(Constants.RELATIONSHIP_STATUS)) {
            this.relationshipStatus = node.get(Constants.RELATIONSHIP_STATUS).asText();
        }
        if (node.has(Constants.COVER)) {
            this.coverId = node.get(Constants.COVER).get(Constants.COVER_ID).asText();
            this.coverSource = node.get(Constants.COVER).get(Constants.COVER_SOURCE).asText();
            this.coverOffsetY = node.get(Constants.COVER).get(Constants.COVER_OFFSET_Y).asText();
        }
        if (node.has(Constants.EDUCATION)) {
        	if (node.get(Constants.EDUCATION).has(Constants.EDUCATION_TYPE))
        	this.educationType = node.get(Constants.EDUCATION).get(Constants.EDUCATION_TYPE).asText();
        	
        	if (node.get(Constants.EDUCATION).has(Constants.EDUCATION_SCHOOL))
        	this.educationSchool = node.get(Constants.EDUCATION).get(Constants.EDUCATION_SCHOOL).asText();
        	
        	if (node.get(Constants.EDUCATION).has(Constants.EDUCATION_YEAR))
        	this.educationYear = node.get(Constants.EDUCATION).get(Constants.EDUCATION_YEAR).asText();
        	
        	if (node.get(Constants.EDUCATION).has(Constants.EDUCATION_CONCENTRATION))
        	this.educationConcentration = node.get(Constants.EDUCATION).get(Constants.EDUCATION_CONCENTRATION).asText();
        }
        
    }

	@Override
	public String getProvider() {
		return FacebookAuthProvider.PROVIDER_KEY;
	}

	public String getName() {
		return name;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getProfileLink() {
		return link;
	}

	public String getGender() {
		return gender;
	}

	public String getEmail() {
		return email;
	}

	public int getTimezone() {
		return timezone;
	}

	public String getPicture() {
		// According to
		// https://developers.facebook.com/docs/reference/api/#pictures
		return String.format("https://graph.facebook.com/%s/picture?type=square&width=70&height=70", getId());
	}

	public Locale getLocale() {
		return AuthUser.getLocaleFromString(locale);
	}

	public String getBirthday() {
		return birthday;
	}
	
	public String getCoverId() {
        return coverId;
    }

    public String getCoverSource() {
        return coverSource;
    }

    public String getCoverOffsetY() {
        return coverOffsetY;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    public String getEducationType() {
        return educationType;
    }

    public String getEducationSchool() {
        return educationSchool;
    }

    public String getEducationYear() {
        return educationYear;
    }

    public String getEducationConcentration() {
        return educationConcentration;
    }
    
	public boolean isVerified() {
	    return verified;
	}
	   
	public String getUpdatedTime() {
	    return updatedTime;
	}

}
