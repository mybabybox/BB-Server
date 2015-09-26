package viewmodel;

import models.User;

public class UserVMLite {
    public Long id;
    public String displayName;
    public Long numFollowings = 0L;
    public Long numFollowers = 0L;
    public Long numPosts = 0L;
    public Long numSold = 0L;
    public Long numLikes = 0L;
    public Long numCollections = 0L;
    public boolean isFollowing = false;

    public UserVMLite(User user){
        this.id = user.id;
        this.displayName = user.displayName;
        this.numPosts = user.productCount;
        this.numSold = 0L; //TODO
        this.numLikes = user.likesCount;
        this.numFollowers = user.followersCount;
        this.numFollowings = user.followingCount;
        this.numCollections = user.collectionCount;
        this.isFollowing = true;//TODO

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

    public Long getNumPosts() {
        return numPosts;
    }

    public void setNumPosts(Long numPosts) {
        this.numPosts = numPosts;
    }

    public Long getNumSold() {
        return numSold;
    }

    public void setNumSold(Long numSold) {
        this.numSold = numSold;
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
