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
	@JsonProperty("createdDate") public Long createdDate;
	@JsonProperty("updatedDate") public Long updatedDate;
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
		this.createdDate = order.getCreatedDate().getTime();
		this.updatedDate = order.getUpdatedDate().getTime();
		this.cancelled = order.cancelled;
		this.cancelDate = order.cancelDate.getTime();
		this.accepted = order.accepted;
		this.acceptDate = order.acceptDate.getTime();
		this.declined = order.declined;
		this.declineDate = order.declineDate.getTime();
		this.active = order.active;
	}
}
