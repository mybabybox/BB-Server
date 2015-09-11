package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class TermsAndConditions {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
	
	public Date date = new Date();
	
	@Required
    @Column(length=5000)
	public String terms;
	
	@Required
    @Column(length=5000)
	public String privacy;
	
	public TermsAndConditions() {}
	
	@Transactional
	public static TermsAndConditions getTermsAndConditions() {
        Query q = JPA.em().createQuery("Select t from TermsAndConditions t where t.date = (Select Max(t.date) from TermsAndConditions t)");
        return (TermsAndConditions)q.getSingleResult();
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
