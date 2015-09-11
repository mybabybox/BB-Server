package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	public String name;

	@Transactional
	public static Category findById(Long id) {
        try {
            Query q = JPA.em().createQuery("SELECT c FROM Category c where id = ?1");
            q.setParameter(1, id);
            return (Category) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}