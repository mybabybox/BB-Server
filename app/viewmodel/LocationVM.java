package viewmodel;

import models.Location;

import org.codehaus.jackson.annotate.JsonProperty;

public class LocationVM {

    public long id;
    public String type;
    public String name;
    public String displayName;
    
    public LocationVM(Location location) {
        this.id = location.id;
        this.type = location.locationType.toString();
        this.name = location.getName();
        this.displayName = location.getDisplayName();
    }
}
