package viewmodel;

import models.Icon;

import org.codehaus.jackson.annotate.JsonProperty;

public class IconVM {
	@JsonProperty("name") public String name;
	@JsonProperty("url") public String url;
	
	public IconVM(Icon icon) {
		this.name = icon.name;
		this.url = icon.url;
	}
}
