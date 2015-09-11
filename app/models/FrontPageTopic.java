package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.builder.EqualsBuilder;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class FrontPageTopic extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(FrontPageTopic.class);

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
	public String name = "";
	
	@Lob
	public String description = "";
	
	public String image;
	
	public String url;
	
	public String attribute;
	
	public int seq;
	
	public int noClicks = 0;
	
	public Date publishedDate;

	public Boolean active = false;
	
	public Boolean mobile = false;
	
    public Boolean deleted = false; 
    
    @Enumerated(EnumType.STRING)
	public TopicType topicType;
	
	public static enum TopicType {
	    SLIDER,
	    PROMO,
	    PROMO_2,
	    FEATURED,
        GAME
    }
	
	@Enumerated(EnumType.STRING)
    public TopicSubType topicSubType;
    
    public static enum TopicSubType {
        NONE,
        FLASH,
        IMAGE,
        HOT_COMM,
        PK_VIEW
    }
	
	public FrontPageTopic() {}
	
	public static FrontPageTopic findById(Long id) {
		Query q = JPA.em().createQuery("SELECT f FROM FrontPageTopic f where id = ?1 and deleted = false");
		q.setParameter(1, id);
		return (FrontPageTopic) q.getSingleResult();
	}
	
	@Transactional
	public static List<FrontPageTopic> getActiveFrontPageTopics(TopicType topicType) {
        Query q = JPA.em().createQuery("SELECT f FROM FrontPageTopic f where topicType = ?1 and active = true and deleted = false order by seq");
        q.setParameter(1, topicType);
        try {
            return (List<FrontPageTopic>) q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
	
	@Transactional
    public static List<FrontPageTopic> getActiveFrontPageTopics() {
        Query q = JPA.em().createQuery("SELECT f FROM FrontPageTopic f where active = true and deleted = false order by seq");
        try {
            return (List<FrontPageTopic>) q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
	
	@Override
    public boolean equals(Object o) {
        if (o != null && o instanceof FrontPageTopic) {
            final FrontPageTopic other = (FrontPageTopic) o;
            return new EqualsBuilder().append(id, other.id).isEquals();
        } 
        return false;
    }
	
    @Override
    public String toString() {
        return "FrontPageTopic{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                "clicks='" + noClicks + '\'' +
                "published='" + publishedDate + '\'' +
                '}';
    }
}
