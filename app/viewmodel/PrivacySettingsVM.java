package viewmodel;

import models.PrivacySettings;

public class PrivacySettingsVM {

    public int activity = 1;
    public int joinedCommunity = 1;
    public int friendList = 1;
    public int detail = 1;
    
    public PrivacySettingsVM(PrivacySettings privacy) {
        if (privacy != null) {
            this.activity = privacy.showActivitiesTo;
            this.joinedCommunity = privacy.showJoinedcommunitiesTo;
            this.friendList = privacy.showFriendListTo;
            this.detail = privacy.showDetailsTo;
        }
    }
}