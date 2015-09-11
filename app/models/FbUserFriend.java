package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import play.db.jpa.JPA;

@Entity
public class FbUserFriend {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	public Long id;
	
	@JsonProperty(value="id")
	public Long pkid;
	
	@ManyToOne
	public User user;
	
	public String email;
	
	public String name;
	
	public FbUserFriend() {
	}
	
	public static List<FbUserFriend> findByUserId(Long id) {
		Query q = JPA.em().createQuery("SELECT fbuf FROM FbUserFriend fbuf where user_id = ?1");
		q.setParameter(1, id);
		try {
		    return (List<FbUserFriend>)q.getResultList();
		} catch(NoResultException e) {
			return new ArrayList<FbUserFriend>();
		}
	}

	public void save() {
		JPA.em().persist(this);
		JPA.em().flush();
	}
}