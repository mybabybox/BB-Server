package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class PrivacySettings {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    public int showActivitiesTo;
    public int showJoinedcommunitiesTo;
    public int showFriendListTo;
    public int showDetailsTo;
    
    @ManyToOne
    public User user;
    
    public static PrivacySettings findByUserId(Long id) {
        try {
            Query q = JPA.em().createQuery("SELECT p FROM PrivacySettings p where p.user.id = ?1");
            q.setParameter(1, id);
            return (PrivacySettings) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    
    @Transactional
    public void save() {
        JPA.em().persist(this);
        JPA.em().flush();     
    }
      
    @Transactional
    public void delete() {
        JPA.em().remove(this);
    }
    
    @Transactional
    public void merge() {
        JPA.em().merge(this);
    }
    
    @Transactional
    public void refresh() {
        JPA.em().refresh(this);
    }
}