package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.data.DynamicForm;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import domain.SocialObjectType;

@Entity
public class ReportedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    @Enumerated(EnumType.STRING)
    public SocialObjectType objectType;
    
    public Long socialObjectID;

    public String description;
    
    public enum ReportType{
        SPAM,
        UNAUTHORIZED_AD,
        INAPPROPRIATE,
        COMPROMISED;
    }
    
    @Enumerated(EnumType.STRING)
    public ReportType reportType;
    
    public Long reportedBy;
    
    public ReportedObject() {
        this.reportedDate = new Date();
    }
    
    public ReportedObject(DynamicForm form, Long userID) {
        this();
        String socialObjectID = form.get("socialObjectID");
        String objectType = form.get("objectType");
        String reportType = form.get("reportType");
        String description = form.get("description");
        
        this.socialObjectID = Long.parseLong(socialObjectID);
        this.description = description;
        this.setObjectType(objectType);
        this.setReportType(reportType);
        this.reportedBy = userID;
        this.save();
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SocialObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        switch(objectType) {
        case "POST":
            this.objectType = SocialObjectType.PRODUCT;
            break;
        case "COMMUNITY":
            this.objectType = SocialObjectType.COMMUNITY;
            break;
        case "USER":
            this.objectType = SocialObjectType.USER;
            break;
        case "COMMENT":
            this.objectType = SocialObjectType.COMMENT;
            break;
        case "QUESTION":
            this.objectType = SocialObjectType.QUESTION;
            break;
        case "ANSWER":
            this.objectType = SocialObjectType.ANSWER;
            break;
        }
    }

    public Long getSocialObjectID() {
        return socialObjectID;
    }

    public void setSocialObjectID(Long socialObjectID) {
        this.socialObjectID = socialObjectID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = ReportType.valueOf(reportType);
    }

    public Date getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(Date reportedDate) {
        this.reportedDate = reportedDate;
    }

    public Long getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(Long reportedBy) {
        this.reportedBy = reportedBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date reportedDate;

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