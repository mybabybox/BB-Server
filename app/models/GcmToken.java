package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

@Entity
public class GcmToken extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GcmToken.class);
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@Required
	public Long userId;
	
	@Required
	public String regId;
	
	@Required
	public Long versionCode;
	
	@Required
	public Boolean deleted = false;

    /**
     * Ctor
     */
    public GcmToken() {
    }

    public static void createUpdateGcmKey(Long userId, String key, Long versionCode) {
        GcmToken gcmToken = findByUserIdVersionCode(userId, versionCode);
        if (gcmToken == null) {
            markDelete(userId);     // mark delete old versions
            gcmToken = new GcmToken();
        }
        
        gcmToken.setUserId(userId);
        gcmToken.setRegId(key);
        gcmToken.setVersionCode(versionCode);
        gcmToken.save();
    }

    ///////////////////////// Find APIs /////////////////////////
	public static GcmToken findByUserId(Long userId) {
		try { 
			Query q = JPA.em().createQuery("SELECT g FROM GcmToken g where userId = ?1 and deleted = false order by CREATED_DATE desc");
			q.setParameter(1, userId);
			q.setMaxResults(1);
			
			if (q.getMaxResults() > 1) {
	            logger.underlyingLogger().error("[u="+userId+"] has "+q.getMaxResults()+" GCM tokens!!");
	        }
			
			return (GcmToken) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} 
	}
	
	public static GcmToken findByUserIdVersionCode(Long userId, Long versionCode) {
        try { 
            Query q = JPA.em().createQuery("SELECT g FROM GcmToken g where userId = ?1 and versionCode = ?2 and deleted = false");
            q.setParameter(1, userId);
            q.setParameter(2, versionCode);
            return (GcmToken) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } 
    }

	public static void markDelete(Long userId) {
        try {
            Query q = JPA.em().createQuery("update GcmToken set deleted = 1 where userId = ?1");
            q.setParameter(1, userId);
            q.executeUpdate();
        } catch (Exception e) {
            logger.underlyingLogger().error("Failed to mark delete GcmToken for userId="+userId, e);
        }
    }
	
	public Long getId() {
		return id;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Long getVersionCode() {
	    return versionCode;
	}
	
	public void setVersionCode(Long versionCode) {
	    this.versionCode = versionCode;
	}
}
