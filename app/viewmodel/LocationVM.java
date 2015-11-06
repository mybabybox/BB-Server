package viewmodel;

import models.Location;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationVM {
	@JsonProperty("id") public long id;
	@JsonProperty("type") public String type;
	@JsonProperty("name") public String name;
	@JsonProperty("displayName") public String displayName;
    
    public LocationVM(Location location) {
        this.id = location.id;
        this.type = location.locationType.toString();
        this.name = location.getName();
        this.displayName = location.getDisplayName();
    }
}
