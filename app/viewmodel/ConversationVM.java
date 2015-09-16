package viewmodel;

import java.util.Date;

import models.Conversation;
import models.User;

import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import domain.DefaultValues;

public class ConversationVM {
	private static final play.api.Logger logger = play.api.Logger.apply(ConversationVM.class);
	
	public String nm;
	public Long uid;
	public Long id;
	public Date lmd;
	public String lm;
	public Long ur = 0L;
	public Boolean isRead = false;
	public Boolean isToday;
	
	public ConversationVM(Conversation conversation, User localUser, User otherUser) {
		this.nm = otherUser.displayName;
		this.uid = otherUser.id;
		this.id = conversation.id;
		this.lmd = conversation.getUpdatedDate();
		this.ur = conversation.getUnreadCount(localUser);
		try{
			this.lm = trimLastMessage(conversation.getLastMessage(localUser));
			this.isToday = DateUtils.isSameDay(this.lmd, new Date());
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
