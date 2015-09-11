package viewmodel;

import java.util.List;

public class FriendWidgetParentVM {
	public Long sn;
	public List<FriendWidgetChildVM> friends;
	
	public FriendWidgetParentVM(Long sn, List<FriendWidgetChildVM> friends) {
		this.sn = sn; 
		this.friends = friends;
	}
}
