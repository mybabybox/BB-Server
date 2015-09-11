package viewmodel;

import java.util.List;

public class FriendsParentVM {
	public int sn;
	public List<FriendsVM> friends;
	
	public FriendsParentVM(int sn, List<FriendsVM> friends) {
		this.sn = sn; 
		this.friends = friends;
	}
}
