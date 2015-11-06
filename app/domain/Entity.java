package domain;

import java.util.Date;

import javax.persistence.AttributeOverrides;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;

import models.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.F;

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
		
    	JPA.em().persist(this);
 		JPA.em().flush();
 		postSave();
		//setCreatedDate(new Date());
		
	}

	@Transactional
	public void delete() {
		JPA.em().remove(this);
	}

	@Transactional
	public void merge() {
		//setUpdatedDate(new Date());
		JPA.em().merge(this);
	}

	@Transactional
	public void refresh() {
		JPA.em().refresh(this);
	}

	public void postSave() {

	}
}