package viewmodel;

import java.util.Date;
import java.util.List;

import models.Message;
import models.Resource;
import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class MessageVM {
	public String snm;
	public Long suid;
	public Long id;
	public Date cd;
	public String text;
	public boolean hasImage=false;
	public Long imgs;
	
	public MessageVM(Message message) {
		User sender = message.userFrom;
		this.snm = sender.name;
		this.suid = sender.id;
		this.id = message.id;
		this.cd = message.date;
		this.text = message.body;
		
		List<Resource> resources = null;
		if(message.folder != null && !CollectionUtils.isEmpty(message.folder.resources)) {
			this.hasImage = true;
			resources = Resource.findAllResourceOfFolder(message.folder.id);
			for (Resource rs : resources) {
				this.imgs = rs.id;
			}
		}
	}
}
