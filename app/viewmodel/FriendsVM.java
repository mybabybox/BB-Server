package viewmodel;

import java.util.ArrayList;
import java.util.List;

import models.Location;
import models.User;

public class FriendsVM {
	public Long id;
	public String nm;
	public String dn;
	public Location ln;
	
	public FriendsVM(Long id, String nm, String dn, Location ln) {
		this.id = id;
		this.nm = nm;
		this.dn = dn;
		this.ln = ln;
	}
	
	public static List<FriendsVM> friends(User user) {
		List<FriendsVM> friends = new ArrayList<>();
		for(User friend : user.getFriends()) {
			friends.add(new FriendsVM(friend.id, friend.firstName + " " + friend.lastName ,friend.displayName, friend.userInfo == null ? null : friend.userInfo.location ));
		}
		return friends;
	}
}
