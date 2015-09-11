package viewmodel;

import models.Emoticon;

import org.codehaus.jackson.annotate.JsonProperty;

public class EmoticonVM {
	@JsonProperty("name") public String name;
	@JsonProperty("code") public String code;
	@JsonProperty("url") public String url;
	
	public EmoticonVM(Emoticon emoticon) {
		this.name = emoticon.name;
		this.code = emoticon.code;
		this.url = emoticon.url;
	}
}
