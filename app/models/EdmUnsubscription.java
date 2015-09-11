package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.db.jpa.JPA;

@Entity
public class EdmUnsubscription extends domain.Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    public Long userId;
    
    public Long edmTemplateId;
    
    public EdmUnsubscription() {}
    
    public EdmUnsubscription(Long userId, Long edmTemplateId) {
        this.userId = userId;
        this.edmTemplateId = edmTemplateId;
    }
    
    public static EdmUnsubscription findById(Long id) {
        try { 
            Query q = JPA.em().createQuery("select u from EdmUnsubscription u where id = ?1");
            q.setParameter(1, id);
            return (EdmUnsubscription) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } 
    }
    
    public static List<EdmUnsubscription> getEdmUnsubscriptions(Long userId) {
        try { 
            Query q = JPA.em().createQuery("select u from EdmUnsubscription u where userId = ?1");
            q.setParameter(1, userId);
            return (List<EdmUnsubscription>) q.getResultList();
        } catch (NoResultException e) {
            return null;
        } 
    }
    
    public static EdmUnsubscription getEdmUnsubscription(Long userId, Long edmTemplateId) {
        try { 
            Query q = JPA.em().createQuery("select u from EdmUnsubscription u where userId = ?1 and edmTemplateId = ?2");
            q.setParameter(1, userId);
            q.setParameter(2, edmTemplateId);
            return (EdmUnsubscription) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } 
    }
}