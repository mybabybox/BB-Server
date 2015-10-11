package viewmodel;

import models.Conversation;
import models.Folder;
import models.Post;
import models.User;

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
	
	public ConversationVM(Conversation conversation, User localUser) {
		User otherUser = conversation.otherUser(localUser);
		Post post = conversation.post;
		this.id = conversation.id;
		this.postId = post.id;
		this.postTitle = post.title;
		this.postPrice = post.price.longValue();
		this.postSold = post.sold;
		this.userId = otherUser.id;
		this.userName = otherUser.displayName;
		this.lastMessageDate = conversation.getUpdatedDate().getTime();
		this.lastMessage = conversation.lastMesage;
		this.unread = conversation.getUnreadCount(localUser);
		
		Long[] images = Folder.getResources(post.folder);
        if (images != null && images.length > 0) {
        	this.postImage = images[0];
        }
	}
}
