package campaign.validator.impl;

import campaign.validator.ICampaignValidator;
import campaign.validator.ValidationResult;
import org.joda.time.DateTime;

/**
 * Created by IntelliJ IDEA.
 * Date: 9/12/14
 * Time: 9:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class NoOpValidator implements ICampaignValidator {
    private static final play.api.Logger logger = play.api.Logger.apply(NoOpValidator.class);

    @Override
    public ValidationResult validate(Long userId, DateTime startTime, DateTime endTime) {
        ValidationResult validationResult = new ValidationResult();
        boolean valid = true;

        logger.underlyingLogger().info("NoOpValidator - startTime="+startTime);

        validationResult.setSuccess(valid);
        return validationResult;
    }
}
