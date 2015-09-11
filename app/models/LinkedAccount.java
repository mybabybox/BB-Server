package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import models.User;
import play.db.jpa.JPA;

import com.feth.play.module.pa.user.AuthUser;

@Entity
public class LinkedAccount extends domain.Entity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LinkedAccount(){}
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;

	@ManyToOne
	public User user;

	public String providerUserId;
	public String providerKey;

	public static LinkedAccount findByProviderKey(final User user, String key) {
		Query q = JPA.em().createQuery("SELECT l from LinkedAccount l  where user = ?1 and providerKey = ?2");
		q.setParameter(1, user);
		q.setParameter(2, key);
		return (LinkedAccount) q.getSingleResult();
	}

	public static LinkedAccount create(final AuthUser authUser) {
		final LinkedAccount ret = new LinkedAccount();
		ret.update(authUser);
		return ret;
	}
	
	public void update(final AuthUser authUser) {
		this.providerKey = authUser.getProvider();
		this.providerUserId = authUser.getId();
	}

	public static LinkedAccount create(final LinkedAccount acc) {
		final LinkedAccount ret = new LinkedAccount();
		ret.providerKey = acc.providerKey;
		ret.providerUserId = acc.providerUserId;

		return ret;
	}

	public LinkedAccount addUser(User user) {
		this.user = user;
		return this;
	}
}