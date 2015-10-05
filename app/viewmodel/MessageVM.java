package viewmodel;

import java.util.List;

import models.Message;
import models.Resource;

import org.apache.commons.collections.CollectionUtils;
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
		
		if (message.folder != null && !CollectionUtils.isEmpty(message.folder.resources)) {
			this.hasImage = true;
			List<Resource> resources = Resource.findAllResourceOfFolder(message.folder.id);
			for (Resource rs : resources) {
				this.image = rs.id;
				break;
			}
		}
	}
}
