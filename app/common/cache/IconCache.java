package common.cache;

import models.Emoticon;
import models.Icon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class IconCache {
    // Permanent cache loaded up on system startup.

    private static List<Emoticon> emoticons;
    private static List<Icon> communityIcons;
    private static Map<Integer, Icon> gameLevelIconsMap;

    static {
        emoticons = Emoticon.loadEmoticons();
        communityIcons = Icon.loadCategoryIcons();
        gameLevelIconsMap = new HashMap<Integer, Icon>();
    }

    public static List<Emoticon> getEmoticons() {
        return emoticons;
    }
    
    public static List<Icon> getCommunityIcons() {
		return communityIcons;
	}
    
    public static Icon getGameLevelIcon(int level) {
        if (gameLevelIconsMap.containsKey(level)) {
            return gameLevelIconsMap.get(level);
        }
        Icon icon = Icon.loadGameLevelIcon(level);
        gameLevelIconsMap.put(level, icon);
        return icon;
    }
}
