package viewmodel;

import java.util.Date;

import models.Conversation;
import models.User;

import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class ConversationVM {
	private static final play.api.Logger logger = play.api.Logger.apply(ConversationVM.class);
	
	@JsonProperty("nm") public String name;
	@JsonProperty("uid") public Long userId;
	@JsonProperty("id") public Long id;
	@JsonProperty("lmd") public Date lastMessageDate;
	@JsonProperty("lm") public String lastMessage;
	@JsonProperty("ur") public Long unread = 0L;
	@JsonProperty("isRead") public Boolean isRead = false;
	@JsonProperty("isToday") public Boolean isToday;
	
	public ConversationVM(Conversation conversation, User localUser, User otherUser) {
		this.name = otherUser.displayName;
		this.userId = otherUser.id;
		this.id = conversation.id;
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
