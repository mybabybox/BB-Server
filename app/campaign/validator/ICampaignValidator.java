package campaign.validator;

import org.joda.time.DateTime;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/12/14
 * Time: 10:57 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ICampaignValidator {

    public ValidationResult validate(Long userId, DateTime startTime, DateTime endTime);

}
