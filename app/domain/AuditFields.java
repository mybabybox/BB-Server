package domain;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class AuditFields {
	@Temporal(TemporalType.TIMESTAMP)
	public Date createdDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}
	
	public Date getUpdatedDate() {
		return updatedDate;
	}
}
