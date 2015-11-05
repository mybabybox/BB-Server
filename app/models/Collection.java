package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import play.db.jpa.JPA;
import domain.SocialObjectType;

@Entity
public class Collection extends SocialObject {

	@OneToMany(cascade=CascadeType.REMOVE, fetch = FetchType.LAZY)
	public List<Post> products = new ArrayList<Post>();

	@Column(length=2000)
	public String description;
	
	public Collection() {
		this.objectType = SocialObjectType.COLLECTION;
	}
	
	public Collection( User owner, String name) {
		this();
		this.name = name;
		this.owner = owner;
	}

	public Collection(User owner, String name, String description) {
		this(owner, name);
		this.description = description;
	}

	public static List<Collection> getUserProductCollections(Long id) {
		try {
            Query q = JPA.em().createQuery("SELECT p FROM Collection p where owner = ?1 and deleted = false");
            q.setParameter(1, User.findById(id));
            return (List<Collection>) q.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
	}
	
    public static Collection findById(Long id) {
        try { 
            Query q = JPA.em().createQuery("SELECT u FROM Collection u where id = ?1 and deleted = false");
            q.setParameter(1, id);
            return (Collection) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
