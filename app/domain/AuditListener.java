package domain;

import com.google.common.base.Preconditions;
import java.util.Date;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class AuditListener
{
  @PrePersist
  public void creation(Object entity)
  {
    Preconditions.checkArgument(entity != null);
    if (Creatable.class.isAssignableFrom(entity.getClass()))
    {
      Date now = new Date();
      Creatable creatable = (Creatable)entity;
      creatable.setCreatedDate(now);
    }
  }
  
  @PreUpdate
  public void updation(Object entity)
  {
    Preconditions.checkArgument(entity != null);
    if (Updatable.class.isAssignableFrom(entity.getClass()))
    {
      Date now = new Date();
      Updatable updatable = (Updatable)entity;
      updatable.setUpdatedDate(now);
    }
  }
}