package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.db.jpa.JPA;

/**
 * INSERT INTO `EdmTemplate` (`htmlTemplate`, `txtTemplate`, `name`, `edmType`) VALUES
 *     ('views.html.edm.product_info', 'views.txt.edm.product_info', 'General Info', 'GENERAL_INFO'),
 *     ('views.html.edm.community', 'views.txt.edm.community', 'Community', 'COMMUNITY_INFO'),
 *     ('views.html.edm.promotion_and_events', 'views.txt.edm.promotion_and_events', 'Promotions', 'PROMO'),
 *     ('views.html.edm.advertising', 'views.txt.edm.advertising', 'Advertising', 'ADS');
 *     
 * @author keithlei
 *
 */
@Entity
public class EdmTemplate {

    public EdmTemplate() {}
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    public String name;
    
    public String htmlTemplate;
    
    public String txtTemplate;

    @Enumerated(EnumType.STRING)
    public EdmType edmType;
    
    public static enum EdmType {
        GENERAL_INFO,       // welcome, account, reminder
        COMMUNITY_INFO,     // events, community news
        PROMO,              // campaigns
        ADS
    }
    
    public static List<EdmTemplate> getAllEdmTemplates() {
        Query q = JPA.em().createQuery("select e from EdmTemplate e");
        try {
            return (List<EdmTemplate>)q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public static EdmTemplate getEdmTemplate(EdmType edmType) {
        Query q = JPA.em().createQuery("select e from EdmTemplate e where edmType = ?1");
        q.setParameter(1, edmType);
        try {
            return (EdmTemplate)q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public static EdmTemplate findById(Long id) {
        try { 
            Query q = JPA.em().createQuery("select e from EdmTemplate e where id = ?1");
            q.setParameter(1, id);
            return (EdmTemplate) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } 
    }
}