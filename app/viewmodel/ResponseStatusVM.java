package viewmodel;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import domain.SocialObjectType;

public class ResponseStatusVM {
	@JsonProperty("objType") public String objType;
	@JsonProperty("objId") public long objId;
	@JsonProperty("userId") public long userId;
	@JsonProperty("success") public boolean success;
	@JsonProperty("messages") public List<String> messages;

	public ResponseStatusVM(SocialObjectType objType, long objId, long userId, boolean success) {
		this(objType, objId, userId, success, new ArrayList<String>());
	}
	
	public ResponseStatusVM(SocialObjectType objType, long objId, long userId, boolean success, String message) {
		this(objType, objId, userId, success, new ArrayList<String>());
		messages.add(message);
	}
	
	public ResponseStatusVM(SocialObjectType objType, long objId, long userId, boolean success, List<String> messages) {
		this.objType = objType.name();
	    this.objId = objId;
	    this.userId = userId;
	    this.success = success;
	    this.messages = messages;
	}
}
