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
    @JsonProperty("closed") public boolean closed;

	public ConversationOrderVM(ConversationOrder order, User localUser) {
		User otherUser = order.conversation.otherUser(localUser);
		this.id = order.id;
		this.conversationId = order.conversation.id;
		this.userId = otherUser.id;
		this.userName = otherUser.displayName;
		if (order.getCreatedDate() != null) {
		    this.createdDate = order.getCreatedDate().getTime();
		}
		if (order.getUpdatedDate() != null) {
		    this.updatedDate = order.getUpdatedDate().getTime();
		}
		this.cancelled = order.cancelled;
		if (order.cancelDate != null) {
		    this.cancelDate = order.cancelDate.getTime();
		}
		this.accepted = order.accepted;
		if (order.acceptDate != null) {
		    this.acceptDate = order.acceptDate.getTime();
		}
		this.declined = order.declined;
		if (order.declineDate != null) {
		    this.declineDate = order.declineDate.getTime();
		}
		this.active = order.active;
		this.closed = order.isOrderClosed();
	}
}
