package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

/**
 * 
 */
@Entity
public class SiteTour  {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public Long userId;
	
	public boolean completed = false;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonIgnore
    public Date completionTime;

	@Enumerated(EnumType.STRING)
	public TourType tourType;
	
    public static enum TourType {
        HOME, 
        PROFILE,
        GAMIFICATION
    }
    
	public SiteTour(){}
	
	public SiteTour(Long userId, TourType tourType){
	    this.userId = userId;
	    this.tourType = tourType;
	}
	
	@Transactional
    public void complete() {
	    this.completed = true;
	    this.completionTime = new Date();
	}
	
	@Transactional
    public static List<SiteTour> getSiteTours(Long userId) {
	    Query q = JPA.em().createQuery("Select t from SiteTour t where userId = ?1");
        q.setParameter(1, userId);
        try {
            return (List<SiteTour>)q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
	}
	
	@Transactional
    public static SiteTour getSiteTour(Long userId, TourType tourType) {
        Query q = JPA.em().createQuery("Select t from SiteTour t where userId = ?1 and tourType = ?2");
        q.setParameter(1, userId);
        q.setParameter(2, tourType);
        try {
            return (SiteTour)q.getSingleResult();
        } catch (NoResultException e) {
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
	
    @Override
    public String toString() {
        return "SiteTour{" +
                "userId='" + userId + '\'' +
                "completed='" + completed + '\'' +
                "completionTime='" + completionTime + '\'' +
                "tourType='" + tourType.name() + '\'' +
                '}';
    }
}
