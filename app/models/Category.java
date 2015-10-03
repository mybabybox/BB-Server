package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;

import common.cache.CategoryCache;

import domain.Likeable;
import domain.Postable;
import domain.SocialObjectType;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Category extends SocialObject implements Likeable, Postable, Comparable<Category> {

	public String icon;

	@Column(length=2000)
	public String description;
    
	@Enumerated(EnumType.STRING)
	public CategoryType categoryType;
    
	public int seq;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JsonIgnore
	public Folder albumPhotoProfile;
	
	@OneToMany(cascade = CascadeType.REMOVE)
	public List<Folder> folders;
	
	public static enum CategoryType {
		PUBLIC
	}
	
	public Category() {
		this.objectType = SocialObjectType.CATEGORY;
	}
	
	public Category(String name, String description, User owner, String icon, int seq) {
		this();
		this.name = name;
		this.description = description;
		this.categoryType = CategoryType.PUBLIC;
		this.owner = owner;
		this.icon = icon;
		this.seq = seq;
		this.system = true;
	}
	
	public static List<Category> loadCategories() {
		try {
            Query q = JPA.em().createQuery("SELECT c FROM Category c where deleted = 0 order by seq");
            return (List<Category>) q.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

	@Transactional
	public static Category findById(Long id) {
		Category category = CategoryCache.getCategory(id);
		if (category != null) {
			return category;
		}

		try {
            Query q = JPA.em().createQuery("SELECT c FROM Category c where id = ?1 and deleted = 0");
            q.setParameter(1, id);
            return (Category) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

	public static List<Category> getAllCategories() {
		return CategoryCache.getAllCategories();
	}
	
	@Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Category) {
            final Category other = (Category) o;
            return new EqualsBuilder().append(id, other.id).isEquals();
        } 
        return false;
    }
    
    @Override
    public int compareTo(Category o) {
        if (this.system != o.system) {
            return this.system.compareTo(o.system);
        }
        if (this.categoryType != null && o.categoryType != null && 
        		this.categoryType != o.categoryType) {
            return this.categoryType.compareTo(o.categoryType);
        }
        return this.name.compareTo(o.name);
    }
}