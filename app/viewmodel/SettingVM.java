package viewmodel;

import models.Setting;

import org.codehaus.jackson.annotate.JsonProperty;

public class SettingVM {
	@JsonProperty("id") public long id;
	@JsonProperty("androidVersion") public String androidVersion;
	@JsonProperty("iosVersion") public String iosVersion;
    
    public SettingVM(Setting setting) {
        this.id = setting.id;
        this.androidVersion = setting.androidVersion;
        this.iosVersion = setting.iosVersion;
    }
}
