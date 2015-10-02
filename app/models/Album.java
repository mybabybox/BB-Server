package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;

import play.data.validation.Constraints.Required;

import com.google.common.base.Objects;

/**
 * Represent a folder contains a set of Resources
 * 
 */
@Entity
public class Album extends SocialObject {

	public Album() {
	}

	public Album(String name) {
		this.name = name;
	}

	@OneToOne(cascade=CascadeType.REMOVE)
	public Resource cover_photo;

	@Lob
	public String description;
	
	@Required
	@ManyToOne
	public Folder folder;

	@OneToMany(cascade = CascadeType.REMOVE)
	public List<Resource> resources = new ArrayList<Resource>();

	@Override
	public String toString() {
		return super.toString() + " " + name;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Album) {
			final Album other = (Album) obj;
			return new EqualsBuilder().append(name, other.name).isEquals();
		} else {
			return false;
		}
	}
}
