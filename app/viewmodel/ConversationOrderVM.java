package viewmodel;

import models.ConversationOrder;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

public class ConversationOrderVM {
	private static final play.api.Logger logger = play.api.Logger.apply(ConversationVM.class);
	
	@JsonProperty("id") public Long id;
	@JsonProperty("conversationId") public Long conversationId;
	@JsonProperty("userId") public Long userId;
	@JsonProperty("userName") public String userName;
	@JsonProperty("offered") public boolean offered;
	@JsonProperty("offerDate") public Long offerDate;
	@JsonProperty("cancelled") public boolean cancelled;
    @JsonProperty("cancelDate") public Long cancelDate;
    @JsonProperty("accepted") public boolean accepted;
    @JsonProperty("acceptDate") public Long acceptDate;
    @JsonProperty("declined") public boolean declined;
    @JsonProperty("declineDate") public Long declineDate;
    @JsonProperty("active") public boolean active;
   
	public ConversationOrderVM(ConversationOrder order, User localUser) {
		User otherUser = order.conversation.otherUser(localUser);
		this.id = order.id;
		this.conversationId = order.conversation.id;
		this.userId = otherUser.id;
		this.userName = otherUser.displayName;
	}
}
