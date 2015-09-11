package customdata;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 7/25/14
 * Time: 11:29 PM
 */
public class LocationFK {
    // PLEASE reference back to Location table
    public static final String REGION_HK = "香港島";
    public static final String REGION_KL = "九龍";
    public static final String REGION_NT = "新界";

    public static final Map<String, Long> REGION_MAP = new HashMap<>();
    public static final Map<String, Long> DISTRICT_MAP = new HashMap<>();

    static {
        REGION_MAP.put(REGION_HK, 4l);
        REGION_MAP.put(REGION_KL, 9l);
        REGION_MAP.put(REGION_NT, 15l);

        DISTRICT_MAP.put("中西區", 5l);
        DISTRICT_MAP.put("東區", 6l);
        DISTRICT_MAP.put("南區", 7l);
        DISTRICT_MAP.put("灣仔區", 8l);
        DISTRICT_MAP.put("九龍城區", 10l);
        DISTRICT_MAP.put("觀塘區", 11l);
        DISTRICT_MAP.put("深水埗區", 12l);
        DISTRICT_MAP.put("黃大仙區", 13l);
        DISTRICT_MAP.put("油尖旺區", 14l);
        DISTRICT_MAP.put("西貢區", 16l);
        DISTRICT_MAP.put("北區", 17l);
        DISTRICT_MAP.put("沙田區", 18l);
        DISTRICT_MAP.put("大埔區", 19l);
        DISTRICT_MAP.put("葵青區", 20l);
        DISTRICT_MAP.put("荃灣區", 21l);
        DISTRICT_MAP.put("屯門區", 22l);
        DISTRICT_MAP.put("元朗區", 23l);
    }
}
