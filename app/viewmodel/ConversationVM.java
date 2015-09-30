package viewmodel;

import java.util.Date;

import models.Conversation;
import models.User;

import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class ConversationVM {
	private static final play.api.Logger logger = play.api.Logger.apply(ConversationVM.class);
	
	@JsonProperty("id") public Long id;
	@JsonProperty("postId") public Long postId;
	@JsonProperty("postTitle") public String postTitle;
	@JsonProperty("postPrice") public Long postPrice;
	@JsonProperty("userId") public Long userId;
	@JsonProperty("userName") public String userName;
	@JsonProperty("lastMessageDate") public Date lastMessageDate;
	@JsonProperty("lastMessage") public String lastMessage;
	@JsonProperty("unread") public Long unread = 0L;
	@JsonProperty("isRead") public Boolean isRead = false;
	@JsonProperty("isToday") public Boolean isToday;
	
	public ConversationVM(Conversation conversation, User localUser, User otherUser) {
		this.id = conversation.id;
		this.postId = conversation.post.id;
		this.postTitle = conversation.post.title;
		this.postPrice = conversation.post.price.longValue();
		this.userId = otherUser.id;
		this.userName = otherUser.displayName;
		this.lastMessageDate = conversation.getUpdatedDate();
		this.unread = conversation.getUnreadCount(localUser);
		try{
			this.lastMessage = conversation.lastMesage;
			this.isToday = DateUtils.isSameDay(this.lastMessageDate, new Date());
			this.isRead = conversation.isReadBy(localUser);
		} catch(NullPointerException e){
			logger.underlyingLogger().error(e.getLocalizedMessage(), e);
		}
	}
}
