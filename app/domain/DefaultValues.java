package domain;

import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.DateTime;

public class DefaultValues {
	public static final int DEFAULT_INFINITE_SCROLL_COUNT = 20;
	public static final int FEED_INFINITE_SCROLL_COUNT = 20;
	
	public static final int MAX_POST_IMAGES = 4;
	public static final int MAX_MESSAGE_IMAGES = 1;
	
    public static final int POST_PREVIEW_CHARS = 300;
    public static final int COMMENT_PREVIEW_CHARS = 200;
    public static final int DEFAULT_PREVIEW_CHARS = 200;
    
    public static final int LATEST_COMMENTS_COUNT = 3;
    
    public static final int SHORT_MESSAGE_COUNT = 50;
    
    public static final int CONVERSATION_MESSAGES_COUNT = 20;
    public static final int MAX_CONVERSATIONS_COUNT = 1000;
    
    public static final int ACTIVITY_NEW_COMMENT_MAX_FAN_OUT = 100;
    public static final int MAX_ACTIVITIES_COUNT = 100;
    
    public static final int DEFAULT_ADJUST_POST_SCORE = 10;
    
    public static Map<String, String> PARENT_BIRTH_YEARS = new LinkedHashMap<String, String>();
    public static Map<String, String> CHILD_BIRTH_YEARS = new LinkedHashMap<String, String>();
    
    public static final int PARENT_YEAR_MIN_AGE = 16;
    public static final int PARENT_YEAR_MAX_AGE = 50;
    public static final int CHILD_YEAR_MIN_AGE = -1;
    public static final int CHILD_YEAR_MAX_AGE = 14;

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
