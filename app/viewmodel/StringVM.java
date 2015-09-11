package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by IntelliJ IDEA.
 * Date: 15/2/15
 * Time: 11:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringVM {
    @JsonProperty("value") public String value;

    public StringVM(String value) {
        this.value = value;
    }
}
