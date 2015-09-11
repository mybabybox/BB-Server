package campaign.validator.impl;

import campaign.validator.ICampaignValidator;
import campaign.validator.ValidationResult;
import org.joda.time.DateTime;
import play.db.jpa.JPA;

import javax.persistence.Query;
import java.math.BigInteger;

/**
 * Created by IntelliJ IDEA.
 * Date: 9/12/14
 * Time: 9:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProfilePicPostCommentValidator implements ICampaignValidator {
    private static final play.api.Logger logger = play.api.Logger.apply(ProfilePicPostCommentValidator.class);

    @Override
    public ValidationResult validate(Long userId, DateTime startTime, DateTime endTime) {
        ValidationResult validationResult = new ValidationResult();
        boolean valid = true;

        // Adjust validation start date to T-3 (since annoucement made early)
        startTime = startTime.minusDays(3);
        logger.underlyingLogger().info("ProfilePicPostCommentValidator - startTime="+startTime);

        /*
        if (!hasQualifiedProfilePic(userId, startTime, endTime)) {
            valid = false;
            validationResult.addMessage("尚未上載個人頭像照片");
            validationResult.addSystemMessage("Missing profile pic");
        }
        */
        if (!hasQualifiedPost(userId, startTime, endTime) && !hasQualifiedComment(userId, startTime, endTime)) {
            valid = false;
            validationResult.addMessage("尚未發佈話題或回覆");
            validationResult.addSystemMessage("Missing post or comment");
        }

        validationResult.setSuccess(valid);
        return validationResult;
    }

    boolean hasQualifiedProfilePic(Long userId, DateTime startTime, DateTime endTime) {
        Query q = JPA.em().createNativeQuery("select EXISTS(select 1 from Resource r where r.owner_id=?1 and (r.CREATED_DATE between ?2 and ?3) and r.objectType = 'PROFILE_PHOTO')");
        q.setParameter(1, userId);
        q.setParameter(2, startTime.toDate());
        q.setParameter(3, endTime.toDate());
        BigInteger count = (BigInteger) q.getSingleResult();
        return count.intValue() == 1;
    }

    boolean hasQualifiedPost(Long userId, DateTime startTime, DateTime endTime) {
        Query q = JPA.em().createNativeQuery("select EXISTS(select 1 from Post p where p.owner_id=?1 and (p.CREATED_DATE between ?2 and ?3) and p.deleted = 0)");
        q.setParameter(1, userId);
        q.setParameter(2, startTime.toDate());
        q.setParameter(3, endTime.toDate());
        BigInteger count = (BigInteger) q.getSingleResult();
        return count.intValue() == 1;
    }

    boolean hasQualifiedComment(Long userId, DateTime startTime, DateTime endTime) {
        Query q = JPA.em().createNativeQuery("select EXISTS(select 1 from Comment c where c.owner_id=?1 and (c.CREATED_DATE between ?2 and ?3) and c.deleted = 0)");
        q.setParameter(1, userId);
        q.setParameter(2, startTime.toDate());
        q.setParameter(3, endTime.toDate());
        BigInteger count = (BigInteger) q.getSingleResult();
        return count.intValue() == 1;
    }
}
