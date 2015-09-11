package domain;

import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.DateTime;

public class DefaultValues {

	public static final int MAX_SCHOOLS_SEARCH_COUNT = 100;
	public static final int KG_COUPON_FIX_2014_15 = 20010;	// coupon fix amount
	
    public static final int POST_PREVIEW_CHARS = 300;
    public static final int COMMENT_PREVIEW_CHARS = 200;
    public static final int DEFAULT_PREVIEW_CHARS = 200;
    
    public static final int ZODIAC_COMMUNITY_MIN_YEAR = 2010;
    public static final int ZODIAC_COMMUNITY_MIN_YEAR_FOR_INDEX = 2013;
    
    public static final int FRONTPAGE_HOT_POSTS_COUNT = 15;
    public static final int FRONTPAGE_HOT_COMMUNITIES_COUNT = 8;
    public static final int FRONTPAGE_HOT_COMMUNITIES_FOR_LAST_DAYS = 30;
    
    public static final int COMMENTS_PREVIEW_COUNT = 10;
    public static final int DEFAULT_UTILITY_COUNT = 5;
    public static final int DEFAULT_INFINITE_SCROLL_COUNT = 10;
    public static final int DEFAULT_PAGINATION_COUNT = 10;
    public static final int FEATURED_ARTICLES_COUNT = 6;
    public static final int ARTICLES_UTILITY_COUNT = 4;
    public static final int ARTICLES_RELATED_COUNT = 10;
    public static final int FRIENDS_UTILITY_COUNT = 9;
    
    public static final int CONVERSATION_LAST_MESSAGE_COUNT = 50;
    public static final int CONVERSATION_MESSAGE_COUNT = 10;
    public static final int CONVERSATION_COUNT = 100;
    
    public static final int GAME_TRANSACTION_PAGESIZE = 30;
    public static final int GAME_TRANSACTION_ADMIN_PAGESIZE = 200;
    
    public static final int NEW_COMMUNITY_MEMBERS_COUNT = 200;
    
    public static final int TOP_SCHOOLS_RANKING_COUNT = 20;
    
    public static Map<String, String> PARENT_BIRTH_YEARS = new LinkedHashMap<String, String>();
    public static Map<String, String> CHILD_BIRTH_YEARS = new LinkedHashMap<String, String>();
    
    public static final int PARENT_YEAR_MIN_AGE = 16;
    public static final int PARENT_YEAR_MAX_AGE = 50;
    public static final int CHILD_YEAR_MIN_AGE = -1;
    public static final int CHILD_YEAR_MAX_AGE = 14;

    public static final int MAX_ARTICLES_COUNT = 100;
    public static final int MAX_CAMPAIGN_COUNT = 100;

    public static final String GOOGLEMAP_PREFIX = "http://maps.google.com.hk/maps?q=";


    static {
        init();
    }
    
    private static void init() {
        int year = new DateTime().getYear();
        
        // parent age range
        for (int i = PARENT_YEAR_MIN_AGE; i <= PARENT_YEAR_MAX_AGE; i++) {
            PARENT_BIRTH_YEARS.put(String.valueOf(year - i), String.valueOf(year - i));
        }
        PARENT_BIRTH_YEARS.put(String.valueOf(year - PARENT_YEAR_MAX_AGE) + "之前", "<" + String.valueOf(year - PARENT_YEAR_MAX_AGE));
        
        // child age range
        for (int i = CHILD_YEAR_MIN_AGE; i <= CHILD_YEAR_MAX_AGE; i++) {
            CHILD_BIRTH_YEARS.put(String.valueOf(year - i), String.valueOf(year - i));
        }
        CHILD_BIRTH_YEARS.put(String.valueOf(year - CHILD_YEAR_MAX_AGE) + "之前", "<" + String.valueOf(year - CHILD_YEAR_MAX_AGE));
    }
}
