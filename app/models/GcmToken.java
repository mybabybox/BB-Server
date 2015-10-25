package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.db.jpa.JPA;

@Entity
public class GcmToken extends domain.Entity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String regId;

	private Long userId;

    /**
     * Ctor
     */
    public GcmToken() {}

    public static void createUpdateGcmKey(Long userId, String key) {
        GcmToken gcmToken = findByUserId(userId);
        if (gcmToken == null) {
            gcmToken = new GcmToken();
        }
        gcmToken.setUserId(userId);
        gcmToken.setRegId(key);
        gcmToken.merge();
    }

    ///////////////////////// Find APIs /////////////////////////
	public static GcmToken findByUserId(Long userId) {
		try { 
			Query q = JPA.em().createQuery("SELECT g FROM Gcm g where user_id = ?1");
			q.setParameter(1, userId);
			return (GcmToken) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
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
}
