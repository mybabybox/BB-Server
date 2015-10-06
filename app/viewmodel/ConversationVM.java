package viewmodel;

import java.util.Date;

import models.Conversation;
import models.Folder;
import models.Post;
import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class ConversationVM {
	private static final play.api.Logger logger = play.api.Logger.apply(ConversationVM.class);
	
	@JsonProperty("id") public Long id;
	@JsonProperty("postId") public Long postId;
	@JsonProperty("postImage") public Long postImage;
	@JsonProperty("postTitle") public String postTitle;
	@JsonProperty("postPrice") public Long postPrice;
	@JsonProperty("postSold") public Boolean postSold;
	@JsonProperty("userId") public Long userId;
	@JsonProperty("userName") public String userName;
	@JsonProperty("lastMessageDate") public Long lastMessageDate;
	@JsonProperty("lastMessage") public String lastMessage;
	@JsonProperty("unread") public Long unread = 0L;
	@JsonProperty("isRead") public Boolean isRead = false;
	@JsonProperty("isToday") public Boolean isToday;
	
	public ConversationVM(Conversation conversation, User localUser, User otherUser) {
		Post post = conversation.post;
		this.id = conversation.id;
		this.postId = post.id;
		this.postTitle = post.title;
		this.postPrice = post.price.longValue();
		this.postSold = post.sold;
		this.userId = otherUser.id;
		this.userName = otherUser.displayName;
		this.lastMessageDate = conversation.getUpdatedDate().getTime();
		this.unread = conversation.getUnreadCount(localUser);
		
		Long[] images = Folder.getResources(post.folder);
        if (images != null && images.length > 0) {
        	this.postImage = images[0];
        }
		
		try {
			this.lastMessage = conversation.lastMesage;
			this.isToday = DateUtils.isSameDay(conversation.getUpdatedDate(), new Date());
			this.isRead = conversation.isReadBy(localUser);
		} catch(NullPointerException e){
			logger.underlyingLogger().error(e.getLocalizedMessage(), e);
		}
	}
}
