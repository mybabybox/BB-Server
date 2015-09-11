package domain;

import java.util.Date;

public interface Updatable {
	  public abstract void setUpdatedBy(String paramString);
	  
	  public abstract void setUpdatedDate(Date paramDate);
}
