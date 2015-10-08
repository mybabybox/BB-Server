
package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import play.db.jpa.JPA;
import be.objectify.deadbolt.core.models.Role;

@Entity
public class SecurityRole extends domain.Entity implements Role {
    private static final long serialVersionUID = 1L;
    
    public static final String USER = "USER";
    public static final String SYSTEM_USER = "SYSTEM_USER";
    public static final String SUPER_ADMIN = "SUPER_ADMIN";
    
    public SecurityRole() {}

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;

	public String roleName;
	
	public static enum RoleType {
	    USER,
	    SYSTEM_USER,
	    SUPER_ADMIN
	}
	
	@Override
	public String getName() {
		return roleName;
	}

	public static SecurityRole findByRoleName(String roleName) {
		Query q = JPA.em().createQuery("SELECT l from SecurityRole l where roleName = ?1");
		q.setParameter(1, roleName);
		try {
			return (SecurityRole) q.getSingleResult();
		} catch(NoResultException e) {
			return null;
		}
	}
	
	public static Long findRowCount() {
		CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
		CriteriaQuery<Long> cQuery = builder.createQuery(Long.class);
		Root<SecurityRole> from = cQuery.from(SecurityRole.class);
		CriteriaQuery<Long> select = cQuery.select(builder.count(from));
		return JPA.em().createQuery(select).getSingleResult();
	}
	
	@Override
	public void save() {
		super.save();
	}
}
