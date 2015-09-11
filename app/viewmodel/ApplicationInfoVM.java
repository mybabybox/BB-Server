package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

public class ApplicationInfoVM {
    @JsonProperty("baseUrl") private String baseUrl;
    
    public ApplicationInfoVM(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
