package viewmodel;

import models.Setting;
import models.SystemInfo;

import org.codehaus.jackson.annotate.JsonProperty;

public class SettingVM {
	@JsonProperty("id") public long id;
	@JsonProperty("emailNewPost") public Boolean emailNewPost;
	@JsonProperty("emailNewConversation") public Boolean emailNewConversation;
	@JsonProperty("emailNewComment") public Boolean emailNewComment;
	@JsonProperty("emailNewPromotions") public Boolean emailNewPromotions;
	@JsonProperty("pushNewConversation") public Boolean pushNewConversation;
	@JsonProperty("pushNewComment") public Boolean pushNewComment;
	@JsonProperty("pushNewFollow") public Boolean pushNewFollow;
	@JsonProperty("pushNewFeedback") public Boolean pushNewFeedback;
	@JsonProperty("pushNewPromotions") public Boolean pushNewPromotions;

	@JsonProperty("systemAndroidVersion") public String systemAndroidVersion;
    @JsonProperty("systemIosVersion") public String systemIosVersion;
    
    public SettingVM(Setting setting) {
        this.id = setting.id;
        if (setting.notificationSetting != null) {
            this.emailNewPost = setting.notificationSetting.emailNewPost;
            this.emailNewConversation = setting.notificationSetting.emailNewConversation;
            this.emailNewComment = setting.notificationSetting.emailNewComment;
            this.emailNewPromotions = setting.notificationSetting.emailNewPromotions;
            this.pushNewConversation = setting.notificationSetting.pushNewConversation;
            this.pushNewComment = setting.notificationSetting.pushNewComment;
            this.pushNewFollow = setting.notificationSetting.pushNewFollow;
            this.pushNewFeedback = setting.notificationSetting.pushNewFeedback;
            this.pushNewPromotions = setting.notificationSetting.pushNewPromotions;
        }
        
        this.systemAndroidVersion = SystemInfo.getInfo().androidVersion;
        this.systemIosVersion = SystemInfo.getInfo().iosVersion;
    }
}
