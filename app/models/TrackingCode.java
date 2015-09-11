package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class TrackingCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public String trackingSource;
    
	@Enumerated(EnumType.STRING)
	public TrackingTarget trackingTarget;
	
    public static enum TrackingTarget {
        SIGNUP_PAGE,
        SIGNUP_COMPLETE
    }

    public Boolean mobile;
    
    public Date date;

    public TrackingCode() {}
    
	public TrackingCode(String trackingSource, TrackingTarget trackingTarget, Boolean mobile) {
		this.trackingSource = trackingSource;
		this.trackingTarget = trackingTarget;
		this.mobile = mobile;
		this.date = new Date();
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
        return "TrackingCode{" +
                "trackingSource='" + trackingSource + '\'' +
                "trackingTarget='" + trackingTarget.name() + '\'' +
                "mobile='" + mobile + '\'' +
                "date='" + date + '\'' +
                '}';
    }
}
