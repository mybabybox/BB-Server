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
	
	public MessageVM(Message message) {
		this.id = message.id;
		this.createdDate = message.getCreatedDate().getTime();
		this.senderName = message.sender.name;
		this.senderId = message.sender.id;
		this.body = message.body;
		
		Long image = message.getImage();
        if (image != null) {
        	this.hasImage = true;
        	this.image = image;
        }
	}
}
