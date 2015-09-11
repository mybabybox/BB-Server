package common.utils;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import models.Icon;

import org.joda.time.DateTime;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.Condition;
import com.github.fedy2.weather.data.unit.DegreeUnit;

import common.model.TodayWeatherInfo;

/**
 * https://github.com/fedy2/yahoo-weather-java-api
 * https://developer.yahoo.com/weather/
 * 
 * http://isithackday.com/geoplanet-explorer/index.php?woeid=24865698
 * http://weather.yahooapis.com/forecastrss?w=24865698&u=c
 * Hong Kong            24865698
 * Hong Kong Island     24703007
 * Kowloon              24703006
 * New Territories      24703004
 * 
 * @author keithlei
 *
 */
public class WeatherUtil {
    private static final play.api.Logger logger = play.api.Logger.apply(WeatherUtil.class);
    
    public static final String HONG_KONG_WOEID = "24865698";
    
    public static void fillInfo(TodayWeatherInfo info) throws JAXBException, IOException {
        YahooWeatherService service;
        service = new YahooWeatherService();
        try {
            Channel channel = service.getForecast(HONG_KONG_WOEID, DegreeUnit.CELSIUS);
            Condition condition = channel.getItem().getCondition();
            info.setTitle(channel.getTitle());
            info.setDescription(channel.getDescription());
            info.setCondition(condition.getText());
            info.setConditionCode(condition.getCode());
            info.setIcon(getIcon(condition.getCode(), new DateTime(condition.getDate())));
            info.setLocation(channel.getLocation().getCity());
            info.setTemperature(condition.getTemp());
            info.setUpdatedTime(new DateTime(condition.getDate()));
        } catch (Exception e) {
            logger.underlyingLogger().error(e.getLocalizedMessage());
        }
    }
    
    public static String getIcon(int conditionCode, DateTime updatedTime) {
        DateTime now = new DateTime();
        Icon icon = Icon.getWeatherIcon(conditionCode);
        if (icon == null || now.minusMinutes(60).isAfter(updatedTime.getMillis())) {
            int hour = now.getHourOfDay();
            if (hour >= 6 && hour <= 18) {      // day time 6am - 6pm
                logger.underlyingLogger().debug("Default weather icon to partly cloudy (day)");
                return "/assets/app/images/weather/weather_icons-11.png";
            }
            logger.underlyingLogger().debug("Default weather icon to partly cloudy (night)");
            return "/assets/app/images/weather/weather_icons-10.png";
        }
        return icon.url;
    }
    
    public static void debug() {
        YahooWeatherService service;
        try {
            service = new YahooWeatherService();
            
            Channel channel = service.getForecast("24865698", DegreeUnit.CELSIUS);
            debug(channel);
            
            channel = service.getForecast("24703007", DegreeUnit.CELSIUS);
            debug(channel);
            
            channel = service.getForecast("24703006", DegreeUnit.CELSIUS);
            debug(channel);
            
            channel = service.getForecast("24703004", DegreeUnit.CELSIUS);
            debug(channel);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void debug(Channel channel) {
        logger.underlyingLogger().info("=======================");
        logger.underlyingLogger().info(channel.getDescription());
        logger.underlyingLogger().info(channel.getTitle());
        logger.underlyingLogger().info(channel.getLink());
        logger.underlyingLogger().info(channel.getLanguage());
        logger.underlyingLogger().info(channel.getUnits().toString());
        logger.underlyingLogger().info(channel.getAstronomy().toString());
        logger.underlyingLogger().info(channel.getAtmosphere().toString());
        logger.underlyingLogger().info(channel.getWind().toString());
        logger.underlyingLogger().info(channel.getItem().getTitle());
        logger.underlyingLogger().info(channel.getItem().getDescription());
        logger.underlyingLogger().info(channel.getItem().getCondition().toString());
        logger.underlyingLogger().info(channel.getItem().getForecasts().toString());
        logger.underlyingLogger().info("");
    }
}
