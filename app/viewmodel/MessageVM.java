package viewmodel;

import models.Message;

import org.codehaus.jackson.annotate.JsonProperty;

public class MessageVM {
	@JsonProperty("id") public Long id;
	@JsonProperty("createdDate") public Long createdDate;
	@JsonProperty("senderId") public Long senderId;
	@JsonProperty("senderName") public String senderName;
	@JsonProperty("body") public String body;
	@JsonProperty("hasImage") public boolean hasImage = false;
	@JsonProperty("image") public Long image = -1L;
	@JsonProperty("system") public boolean system = false;
	
	public MessageVM(Message message) {
		this.id = message.id;
		this.createdDate = message.getCreatedDate().getTime();
		this.senderName = message.sender.name;
		this.senderId = message.sender.id;
		this.body = message.body;
		this.system = message.system;
		
		Long image = message.getImage();
        if (image != null) {
        	this.hasImage = true;
        	this.image = image;
        }
	}
}
