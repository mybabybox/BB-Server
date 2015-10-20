package common.model;

public class FeedFilter {

    public enum FeedType {
        HOME_EXPLORE,
        HOME_FOLLOWING,
        CATEGORY_POPULAR,
        CATEGORY_NEWEST,
        CATEGORY_PRICE_LOW_HIGH,
        CATEGORY_PRICE_HIGH_LOW,
        USER_POSTED,
        USER_LIKED,
        USER_FOLLOWING,
        PRODUCT_LIKES,
        PRODUCT_SUGGEST
    }

    public enum FeedProductType {
        ALL,
        NEW,
        USED
    }

    public FeedType feedType;
    public FeedProductType productType;
    public Long objId;

    public FeedFilter(FeedType feedType) {
        this(feedType, FeedProductType.ALL, -1L);
    }

    public FeedFilter(FeedType feedType, FeedProductType productType) {
        this(feedType, productType, -1L);
    }

    public FeedFilter(FeedType feedType, Long objId) {
        this(feedType, FeedProductType.ALL, objId);
    }

    public FeedFilter(FeedType feedType, FeedProductType productType, Long objId) {
        this.feedType = feedType;
        this.productType = productType;
        this.objId = objId;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        if (other == this)
            return true;

        if (!(other instanceof FeedFilter))
            return false;

        FeedFilter o = (FeedFilter) other;
        return this.feedType.equals(o.feedType) &&
                this.productType.equals(o.productType) &&
                this.objId.equals(o.objId);
    }

    @Override
    public String toString() {
        return "feedType=" + (feedType == null? "" : feedType.name()) +
                "\nproductType=" + (productType == null? "" : productType.name()) +
                "\nobjId=" + objId;
    }
}