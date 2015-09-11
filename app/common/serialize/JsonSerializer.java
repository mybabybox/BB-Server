package common.serialize;

import play.libs.Json;

public class JsonSerializer {

    public static String serialize(Object object) {
        return Json.toJson(object).toString();
    }
    
    public static Object deserialize(String json, Class<?> clazz) {
        return Json.fromJson(Json.parse(json), clazz);
    }
}
