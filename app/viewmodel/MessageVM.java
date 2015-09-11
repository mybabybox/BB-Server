package viewmodel;

import java.util.Date;
import java.util.List;

import models.Message;
import models.Resource;
import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class MessageVM {
	@JsonProperty("snm") public String senderName;
	@JsonProperty("suid") public Long senderUserID;
	@JsonProperty("id") public Long id;
	@JsonProperty("cd") public Date creationDate;
	@JsonProperty("txt") public String text;
	@JsonProperty("hasImage") public boolean hasImage=false;
	@JsonProperty("imgs") public Long images;
	
	public MessageVM(Message message) {
		User sender = message.userFrom;
		this.senderName = sender.name;
		this.senderUserID = sender.id;
		this.id = message.id;
		this.creationDate = message.date;
		this.text = message.body;
		
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
