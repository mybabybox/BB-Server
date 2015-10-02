package processor;

import common.utils.StringUtil;
import domain.SocialObjectType;
import models.LikeSocialRelation;
import models.User;
import play.db.jpa.JPA;
import javax.persistence.Query;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Date: 22/7/14
 * Time: 11:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class LikeSocialRelationManager {

    /**
     * @param user
     * @param objIds
     */
    public static Set<LikeSocialResult> getSocialRelationBy(User user, List<Long> objIds) {
        if (objIds == null || objIds.size() == 0) {
            return Collections.EMPTY_SET;
        }

        Set<LikeSocialResult> results = new HashSet<>();

        String idsForIn = StringUtil.collectionToString(objIds, ",");

        Query q = JPA.em().createQuery(
        		"Select sr.target, sr.targetType, sr.action from LikeSocialRelation sr where sr.actor = ?1 and sr.target in ("+idsForIn+")");
        q.setParameter(1, user.id);

        List<Object[]> qRes = q.getResultList();
        for (Object[] entry : qRes) {
            results.add(new LikeSocialResult((Long) entry[0], (SocialObjectType) entry[1], (LikeSocialRelation.Action) entry[2]));
        }
        return results;
    }

    /**
     * 
     */
    public static class LikeSocialResult {
        public Long id;
        public SocialObjectType socialObjectType;
        public LikeSocialRelation.Action action;

        public LikeSocialResult(Long id, SocialObjectType socialObjectType, LikeSocialRelation.Action action) {
            this.id = id;
            this.socialObjectType = socialObjectType;
            this.action = action;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LikeSocialResult that = (LikeSocialResult) o;

            if (action != that.action) return false;
            if (id != null ? !id.equals(that.id) : that.id != null) return false;
            if (socialObjectType != that.socialObjectType) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (socialObjectType != null ? socialObjectType.hashCode() : 0);
            result = 31 * result + (action != null ? action.hashCode() : 0);
            return result;
        }
    }
}