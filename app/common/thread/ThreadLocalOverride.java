package common.thread;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * Date: 17/3/15
 * Time: 11:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThreadLocalOverride {

    private static ThreadLocal<Boolean> skipNotificationLocal = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };
    private static ThreadLocal<Date> socialUpdatedDateLocal = new ThreadLocal<Date>() {
        @Override
        protected Date initialValue() {
            return null;
        }
    };


    public static void disableNotification(boolean off) {
        skipNotificationLocal.set(off);
    }

    public static boolean isDisableNotification() {
        return skipNotificationLocal.get();
    }

    public static void setSocialUpdatedDate(Date date) {
        socialUpdatedDateLocal.set(date);
    }

    public static Date getSocialUpdatedDate() {
        return socialUpdatedDateLocal.get();
    }
}
