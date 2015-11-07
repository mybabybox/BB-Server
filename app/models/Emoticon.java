package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;

import common.cache.IconCache;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Emoticon {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    public String name;
    
    public String code;
    
    public int seq;
    
    public String url;
    
    public Emoticon(){}
    
    public Emoticon(String name, String code, int seq, String url) {
        this.name = name;
        this.code = code;
        this.seq = seq;
        this.url = url;
    }
    
    public static List<Emoticon> loadEmoticons() {
        Query q = JPA.em().createQuery("Select i from Emoticon i order by seq");
        return (List<Emoticon>)q.getResultList();
    }
    
    public static List<Emoticon> getEmoticons() {
        return IconCache.getEmoticons();
    }

    public String getName() {
    	return name;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getUrl() {
    	return url;
    }
    
    @Transactional
    public void save() {
        JPA.em().persist(this);
        JPA.em().flush();	  
    }
      
    @Transactional
    public void delete() {
        JPA.em().remove(this);
    }
    
    @Transactional
    public void merge() {
        JPA.em().merge(this);
    }
    
    @Transactional
    public void refresh() {
        JPA.em().refresh(this);
    }
}
