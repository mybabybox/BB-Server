package domain;

import java.util.Date;

import javax.persistence.AttributeOverrides;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@MappedSuperclass
public class Entity
{
  @Embedded
  @AttributeOverrides({@javax.persistence.AttributeOverride(name="createdDate", column=@javax.persistence.Column(name="CREATED_DATE")), @javax.persistence.AttributeOverride(name="updatedDate", column=@javax.persistence.Column(name="UPDATED_DATE"))})
  public AuditFields auditFields;
  
  public Entity()
  {
    this.auditFields = new AuditFields();
  }
  
  public void setCreatedDate(Date createdDate)
  {
      if (auditFields != null) {
        this.auditFields.setCreatedDate(createdDate);
      }
  }
  
  public void setUpdatedDate(Date updatedDate)
  {
      if (auditFields != null) {
        this.auditFields.setUpdatedDate(updatedDate);
      }
  }
  
  @JsonIgnore
  public Date getCreatedDate() {
      if (this.auditFields != null) {
          return this.auditFields.getCreatedDate();
      }
      return null;
  }
  
  @JsonIgnore
  public Date getUpdatedDate() {
      if (this.auditFields != null) {
          return this.auditFields.getUpdatedDate();
      }
      return null;
  }
  
  @Transactional
  public void save() {
      preSave();
      JPA.em().persist(this);
      JPA.em().flush();
      postSave();
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
  
  public void preSave() {
      
  }
  
  public void postSave() {
      
  }
}