package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

import models.User;

public class UserVMLite {
	@JsonProperty("id") public Long id;
    @JsonProperty("displayName") public String displayName;
    @JsonProperty("numFollowings") public Long numFollowings = 0L;
    @JsonProperty("numFollowers") public Long numFollowers = 0L;
    @JsonProperty("numProducts") public Long numProducts = 0L;
    @JsonProperty("numStories") public Long numStories = 0L;
    @JsonProperty("numLikes") public Long numLikes = 0L;
    @JsonProperty("numCollections") public Long numCollections = 0L;
    @JsonProperty("isFollowing") public boolean isFollowing = false;

    public UserVMLite(User user, User localUser) {
        this.id = user.id;
        this.displayName = user.displayName;
        this.numProducts = user.numProducts;
        this.numStories = user.numStories;
        this.numLikes = user.numLikes;
        this.numFollowers = user.numFollowers;
        this.numFollowings = user.numFollowings;
        this.numCollections = user.numCollections;
        if (!user.equals(localUser)) {
        	this.isFollowing = user.isFollowedBy(localUser);
        }
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
