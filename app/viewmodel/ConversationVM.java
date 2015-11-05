package viewmodel;

import models.Conversation;
import models.ConversationOrder;
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
	@JsonProperty("postOwner") public Boolean postOwner;
	@JsonProperty("postSold") public Boolean postSold;
	@JsonProperty("userId") public Long userId;
	@JsonProperty("userName") public String userName;
	@JsonProperty("lastMessage") public String lastMessage;
	@JsonProperty("lastMessageHasImage") public Boolean lastMessageHasImage;
	@JsonProperty("lastMessageDate") public Long lastMessageDate;
	@JsonProperty("unread") public Long unread = 0L;
	@JsonProperty("order") public ConversationOrderVM order;
	
	public ConversationVM(Conversation conversation, User localUser) {
		User otherUser = conversation.otherUser(localUser);
		Post post = conversation.post;
		this.id = conversation.id;
		this.postId = post.id;
		this.postTitle = post.title;
		this.postPrice = post.price.longValue();
		this.postOwner = post.owner.id == localUser.id;
		this.postSold = post.sold;
		this.userId = otherUser.id;
		this.userName = otherUser.displayName;
		this.lastMessage = conversation.lastMessage;
		this.lastMessageHasImage = conversation.lastMessageHasImage;
		this.lastMessageDate = conversation.lastMessageDate.getTime();
		this.unread = conversation.getUnreadCount(localUser);
		
		ConversationOrder order = ConversationOrder.getActiveOrder(conversation);
		if (order != null) {
		    this.order = new ConversationOrderVM(order, localUser);		    
		}
		
		Long[] images = post.getImages();
        if (images != null && images.length > 0) {
        	this.postImage = images[0];
        }
	}
}
