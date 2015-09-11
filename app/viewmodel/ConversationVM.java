package viewmodel;

import java.util.Date;

import models.Conversation;
import models.User;

import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import domain.DefaultValues;

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
			this.lastMessage = trimLastMessage(conversation.getLastMessage(localUser));
			this.isToday = DateUtils.isSameDay(this.lastMessageDate, new Date());
			this.isRead = conversation.isReadBy(localUser);
		} catch(NullPointerException e){
			logger.underlyingLogger().error(e.getLocalizedMessage(), e);
		}
	}
	
	private String trimLastMessage(String message) {
		int count = DefaultValues.CONVERSATION_LAST_MESSAGE_COUNT;
		
		message = removeEmoticons(message);
		if (message.length() <= count)
			return message;
		return message.substring(0,count + 1) + " ...";
		
		/* no need trim emoticons anymore
		int end = message.indexOf('>', count);
		if (end == -1) {
			end = count;
		}
		return message.substring(0,end + 1) + " ...";
		*/
	}
	
	private String removeEmoticons(String message) {
		return message.replaceAll("<img([^>]*)>", " ");
	}
}
