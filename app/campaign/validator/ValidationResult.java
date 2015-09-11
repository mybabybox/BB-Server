package campaign.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/12/14
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidationResult {
    public static ValidationResult VALIDATOR_NOT_FOUND = new ValidationResult(false, "Validator class not found");

    private boolean success;
    private List<String> messages = new ArrayList<>();
    private List<String> systemMessages = new ArrayList<>();

    public ValidationResult() { }

    public ValidationResult(boolean success, String message) {
        this.success = success;
        messages.add(message);
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public void addSystemMessage(String message) {
        systemMessages.add(message);
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<String> getSystemMessages() {
        return systemMessages;
    }
}
