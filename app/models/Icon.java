package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

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
public class Icon {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    //@Index(name = "Idx_Icon_Name")
    public String name;
    
    @Enumerated(EnumType.STRING)
    public IconType iconType;
    
    public String info;
    
    public String url;
    
    public static enum IconType {
        CATEGORY, 
        GAME_LEVEL
    }
    
    public Icon(){}

    public Icon(String name, IconType iconType, String url) {
        this(name, iconType, "", url);
    }
    
    public Icon(String name, IconType iconType, String info, String url) {
        this.name = name;
        this.iconType = iconType;
        this.info = info;
        this.url = url;
    }
    
    public static List<Icon> loadCategoryIcons() {
        return loadIcons(IconType.CATEGORY);
    }
    
    public static List<Icon> loadIcons(IconType iconType) {
        Query q = JPA.em().createQuery("Select i from Icon i where iconType = ?1");
        q.setParameter(1, iconType);
        return (List<Icon>)q.getResultList();
    }

    public static Icon loadGameLevelIcon(int level) {
        Query q = JPA.em().createQuery("Select i from Icon i where name = ?1 and iconType = ?2");
        q.setParameter(1, String.valueOf(level));
        q.setParameter(2, IconType.GAME_LEVEL);
        try {
            return (Icon)q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public static List<Icon> getCommunityIcons() {
        return IconCache.getCommunityIcons();
    }
    
    public static Icon getGameLevelIcon(int level) {
        return IconCache.getGameLevelIcon(level);
    }
    
    public String getName() {
    	return name;
    }
    
    public IconType getIconType() {
        return iconType;
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
