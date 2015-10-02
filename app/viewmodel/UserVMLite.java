package viewmodel;

import models.User;

public class UserVMLite {
    public Long id;
    public String displayName;
    public Long numFollowings = 0L;
    public Long numFollowers = 0L;
    public Long numProducts = 0L;
    public Long numStories = 0L;
    public Long numLikes = 0L;
    public Long numCollections = 0L;
    public boolean isFollowing = false;

    public UserVMLite(User user){
        this.id = user.id;
        this.displayName = user.displayName;
        this.numProducts = user.numProducts;
        this.numStories = user.numStories;
        this.numLikes = user.numLikes;
        this.numFollowers = user.numFollowers;
        this.numFollowings = user.numFollowings;
        this.numCollections = user.numCollections;
        this.isFollowing = true;	//TODO
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getNumFollowings() {
        return numFollowings;
    }

    public void setNumFollowings(Long numFollowings) {
        this.numFollowings = numFollowings;
    }

    public Long getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(Long numFollowers) {
        this.numFollowers = numFollowers;
    }

    public Long getNumProducts() {
		return numProducts;
	}

	public void setNumProducts(Long numProducts) {
		this.numProducts = numProducts;
	}

	public Long getNumStories() {
		return numStories;
	}

	public void setNumStories(Long numStories) {
		this.numStories = numStories;
	}

	public void setFollowing(boolean isFollowing) {
		this.isFollowing = isFollowing;
	}

	public Long getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(Long numLikes) {
        this.numLikes = numLikes;
    }

    public Long getNumCollections() {
        return numCollections;
    }

    public void setNumCollections(Long numCollections) {
        this.numCollections = numCollections;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }
}
