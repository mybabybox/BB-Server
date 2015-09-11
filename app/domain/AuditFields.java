package domain;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class AuditFields {
	private String createdBy;
	private String updatedBy;
	@Temporal(TemporalType.TIMESTAMP)
	public Date createdDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getCreatedBy() {
        return createdBy;
    }
	
    public String getUpdatedBy() {
        return updatedBy;
    }
    
	public Date getCreatedDate() {
		return createdDate;
	}
	
	public Date getUpdatedDate() {
		return updatedDate;
	}
}
