package common.model;

import java.io.IOException;
import java.io.Serializable;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;

import common.cache.JedisCache;

public class TodayWeatherInfo implements Serializable {
    private static final play.api.Logger logger = play.api.Logger.apply(TodayWeatherInfo.class);
    
    private static final long serialVersionUID = -247911530124299908L;

    private static int REFRESH_MINS = 10;
    private static int REFRESH_SECS = REFRESH_MINS * 60;
    
    private String location;
    private String title;
    private String description;
    private String condition;
    private int conditionCode;
    private String icon;
    private int temperature;    // 28°C
    private String dayOfWeek;   // 星期一
    private String today;       // 9月25日
    private DateTime updatedTime;
    
    private TodayWeatherInfo() {
    }
    
    /**
     * 
     * 
     * @return
     */
    public static TodayWeatherInfo getInfo() {
        TodayWeatherInfo info = 
                (TodayWeatherInfo)JedisCache.cache().getObj(JedisCache.TODAY_WEATHER_KEY, TodayWeatherInfo.class);
        if (info == null) {
            int refreshSecs = 60;   // retry after 60 secs if failed to get weather info 
            info = new TodayWeatherInfo();
            try {
                fillInfo(info);
                refreshSecs = TodayWeatherInfo.REFRESH_SECS;    // got weather info
            } catch (JAXBException e) {
                logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
            } catch (IOException e) {
                logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
            } catch (RuntimeException e) {
                logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
            }
            JedisCache.cache().putObj(JedisCache.TODAY_WEATHER_KEY, info, refreshSecs);
        }
        return info;
    }
    
    public static void clearInfo() {
        JedisCache.cache().remove(JedisCache.TODAY_WEATHER_KEY);
    }
    
    private static TodayWeatherInfo fillInfo(TodayWeatherInfo info) throws JAXBException, IOException {
        //WeatherUtil.fillInfo(info);
        
        DateTime now = new DateTime();
        int dayOfWeek = now.getDayOfWeek();
        if (dayOfWeek == 1) {
            info.dayOfWeek = "星期一";
        } else if (dayOfWeek == 2) {
            info.dayOfWeek = "星期二";
        } else if (dayOfWeek == 3) {
            info.dayOfWeek = "星期三";
        } else if (dayOfWeek == 4) {
            info.dayOfWeek = "星期四";
        } else if (dayOfWeek == 5) {
            info.dayOfWeek = "星期五";
        } else if (dayOfWeek == 6) {
            info.dayOfWeek = "星期六";
        } else if (dayOfWeek == 7) {
            info.dayOfWeek = "星期日";
        }
        
        int month = now.getMonthOfYear();
        int day = now.getDayOfMonth();
        info.today = month + "月" + day + "日";
        
        return info;
    }
    
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(int conditionCode) {
        this.conditionCode = conditionCode;
    }
    
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getTemperature() {
        return temperature;
    }
    
    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
    
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getToday() {
        return today;
    }
    
    public void setToday(String today) {
        this.today = today;
    }
    
    public DateTime getUpdatedTime() {
        return updatedTime;
    }
    
    public void setUpdatedTime(DateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
    
    @Override
    public String toString() {
        return "[location=" + location + "|title=" + title + "|description=" + description + 
                "|condition=" + condition + "|conditionCode=" + conditionCode + "|temperature=" + temperature + 
                "|dayOfWeek=" + dayOfWeek + "|today=" + today + "|updatedTime=" + updatedTime + "]";
    }
}