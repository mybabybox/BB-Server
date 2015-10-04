package viewmodel;

import java.util.Date;
import java.util.List;

import models.Message;
import models.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class MessageVM {
	@JsonProperty("id") public Long id;
	@JsonProperty("createdDate") public Date createdDate;
	@JsonProperty("senderId") public Long senderId;
	@JsonProperty("senderName") public String senderName;
	@JsonProperty("body") public String body;
	@JsonProperty("hasImage") public boolean hasImage = false;
	@JsonProperty("images") public Long images;
	
	public MessageVM(Message message) {
		this.id = message.id;
		this.createdDate = message.getCreatedDate();
		this.senderName = message.sender.name;
		this.senderId = message.sender.id;
		this.body = message.body;
		
		List<Resource> resources = null;
		if(message.folder != null && !CollectionUtils.isEmpty(message.folder.resources)) {
			this.hasImage = true;
			resources = Resource.findAllResourceOfFolder(message.folder.id);
			for (Resource rs : resources) {
				this.images = rs.id;
			}
		}
	}
}
