package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.db.jpa.JPA;

@Entity
public class Gcm extends domain.Entity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String reg_id;

	private Long user_id;

    /**
     * Ctor
     */
    public Gcm() {}

    public static void createUpdateGcmKey(Long userId, String key) {
        Gcm gcm = findByUserId(userId);
        if (gcm == null) {
            gcm = new Gcm();
        }
        gcm.setUser_id(userId);
        gcm.setReg_id(key);
        gcm.merge();
    }

    ///////////////////////// Find APIs /////////////////////////
	public static Gcm findByUserId(Long userId) {
		try { 
			Query q = JPA.em().createQuery("SELECT g FROM Gcm g where user_id = ?1");
			q.setParameter(1, userId);
			return (Gcm) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} 
	}


	public Long getId() {
		return id;
	}

	public String getReg_id() {
		return reg_id;
	}

	public void setReg_id(String reg_id) {
		this.reg_id = reg_id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
}
