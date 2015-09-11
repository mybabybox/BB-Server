package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.google.common.base.Objects;

import models.User;
import play.data.format.Formats;
import play.db.jpa.JPA;

@Entity
public class TokenAction extends domain.Entity {

	public TokenAction() {}
	public enum Type {
		@Enumerated
		EMAIL_VERIFICATION,

		@Enumerated
		PASSWORD_RESET
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Verification time frame (until the user clicks on the link in the email)
	 * in seconds
	 * Defaults to one week
	 */
	private final static long VERIFICATION_TIME = 7 * 24 * 3600;

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;

	@Column(unique = true)
	public String token;

	@ManyToOne
	public User targetUser;

	public Type type;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date expires;

	
	public static TokenAction findByToken(final String token, final Type type) {
		return (TokenAction) JPA.em().createQuery("Select t from TokenAction t where token = ?1 and type = ?2").
		setParameter(1, token).
		setParameter(2, type).getSingleResult();
	}

	public static void deleteByUser(final User u, final Type type) {
		JPA.em().createQuery("DELETE TokenAction where targetUser.id = ?1 and type = ?2").
		setParameter(1, u.id).
		setParameter(2, type).executeUpdate();
	}

	public boolean isValid() {
		return this.expires.after(new Date());
	}

	public static TokenAction create(final Type type, final String token,
			final User targetUser) {
		final TokenAction ua = new TokenAction();
		ua.targetUser = targetUser;
		ua.token = token;
		ua.type = type;
		final Date created = new Date();
		ua.created = created;
		ua.expires = new Date(created.getTime() + VERIFICATION_TIME * 1000);
		ua.save();
		return ua;
	}
	
	@Override
	public int hashCode(){
	    return Objects.hashCode(token, type, targetUser.id);
	}
	
	@Override
	public boolean equals(final Object obj){
	    if(obj instanceof TokenAction){
	        final TokenAction other = (TokenAction) obj;
	        return new EqualsBuilder()
	            .append(token, other.token)
	            .append(type, other.type)
	            .append(targetUser.id, other.targetUser.id)
	            .isEquals();
	    } else{
	        return false;
	    }
	}
}
