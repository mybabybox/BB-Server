package viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplicationInfoVM {
    @JsonProperty("baseUrl") private String baseUrl;
    
    public ApplicationInfoVM(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
