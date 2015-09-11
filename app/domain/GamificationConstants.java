package domain;

/**
 * Created by IntelliJ IDEA.
 * Date: 9/11/14
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GamificationConstants {

    /**
     * To be in sync with game-constants.js
     */
    
    /**
     * Points
     * 10 points = $1 (as of 9 June 2015)
     * e.g. 1 referral = 25 points => 20 referral = 500 points = $50 coupon
     */
    public static final int POINTS_SIGNUP = 10;
    public static final int POINTS_UPLOAD_PROFILE_PHOTO = 10;
    public static final int POINTS_DAILY_SIGNIN = 2;
    public static final int POINTS_POST = 0;
    public static final int POINTS_COMMENT = 0;
    public static final int POINTS_LIKE = 0;             // not open yet

    public static final int POINTS_REFERRAL_SIGNUP = 25;
    public static final int POINTS_APP_LOGIN = 10;
    
    /**
     * Limit on Points accounting
     */
    public static final long LIMIT_POST = 5;
    public static final long LIMIT_COMMENT = 5;
    public static final long LIMIT_LIKE = 5;
    public static final long LIMIT_REFERRAL_SIGNUP = 20;    // FB signup referral
    public static final long LIMIT_DAILY_POINTS = 200;

    /**
     * Game levels
     */
    public static final int L1_FROM_POINTS = 0;
    public static final int L1_TO_POINTS = 30;
    public static final int L2_FROM_POINTS = 31;
    public static final int L2_TO_POINTS = 100;
    public static final int L3_FROM_POINTS = 101;
    public static final int L3_TO_POINTS = 300;
    public static final int L4_FROM_POINTS = 301;
    public static final int L4_TO_POINTS = 800;
    public static final int L5_FROM_POINTS = 801;
    public static final int L5_TO_POINTS = 1500;
    public static final int L6_FROM_POINTS = 1501;
    public static final int L6_TO_POINTS = 2500;
    public static final int L7_FROM_POINTS = 2501;
    public static final int L7_TO_POINTS = 4000;
    public static final int L8_FROM_POINTS = 4001;
    public static final int L8_TO_POINTS = 6000;
    public static final int L9_FROM_POINTS = 6001;
    public static final int L9_TO_POINTS = 100000;
    public static final int L10_FROM_POINTS = 100001;
    public static final int L10_TO_POINTS = 99999999;
}
