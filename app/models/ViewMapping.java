package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.annotations.Index;

import common.cache.IconCache;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

/**
 * insert into icon (iconType,name,url) values ('GAME_LEVEL','1','/assets/app/images/game/levels/l_1.jpg');
 * 
 * @author keithlei
 *
 */
@Entity
public class ViewMapping {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	 
    public Long productId;
    
    public Long userId;
    
    public Date viewedDate;
    
    

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
