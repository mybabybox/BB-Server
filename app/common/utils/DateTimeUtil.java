package common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;

public class DateTimeUtil {
    
    private static DateTime TODAY;
    
    private static SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm");
    
    public static DateTime getToday() {
        DateTime now = DateTime.now();
        if (TODAY != null && 
                now.getYear() == TODAY.getYear() && 
                now.getMonthOfYear() == TODAY.getMonthOfYear() && 
                now.getDayOfMonth() == TODAY.getDayOfMonth()) {
            return TODAY;
        }
        
        TODAY = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0, 0);
        return TODAY;
    }
    
    public static DateTime getTomorrow() {
        return getToday().plusDays(1);
    }
    
    public static boolean isDateOfBirthValid(String year, String month, String day) {
        try {
            parseDate(year, month, day);
        } catch (IllegalFieldValueException ie) {
            return false;
        }
        return true;
    }
    
    public static boolean isDayOfMonthValid(String year, String month, String day) {
        try {
            parseDate(year, month, day);
        } catch (IllegalFieldValueException ie) {
            if ("dayOfMonth".equals(ie.getFieldName()))
                return false;
        }
        return true;
    }
    
    public static DateTime parseDate(String year, String month, String day) {
        if (year.startsWith("<")) {     // e.g. <1960
            year = year.replaceAll("<", "").trim();
        }
        
        if (StringUtils.isEmpty(day)) {
            day = "1";
        }
        
        int y = Integer.valueOf(year);
        int m = Integer.valueOf(month);
        int d = Integer.valueOf(day);
        
        if (d <= 0) {       // day is optional most of the times
            d = 1;
        }
        
        return new DateTime(y, m ,d, 12, 0);
    }
    
    public static String toString(Date date) {
        return formatter.format(date);
    }
}
