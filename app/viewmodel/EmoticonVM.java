package viewmodel;

import models.Emoticon;

import org.codehaus.jackson.annotate.JsonProperty;

public class EmoticonVM {
	public String name;
	public String code;
	public String url;
	
	public EmoticonVM(Emoticon emoticon) {
		this.name = emoticon.name;
		this.code = emoticon.code;
		this.url = emoticon.url;
	}
}
