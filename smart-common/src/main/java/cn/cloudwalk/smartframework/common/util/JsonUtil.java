package cn.cloudwalk.smartframework.common.util;

import cn.cloudwalk.smartframework.common.util.gson.GsonExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.thoughtworks.xstream.XStream;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author LIYANHUI
 */
public class JsonUtil {

    private static Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat(DateUtil.DATE_PATTERN.yyyy_MM_dd_HH_mm_ss.toString());
        builder.setExclusionStrategies(new GsonExclusionStrategy());
        gson = builder.serializeNulls().create();
    }

    public JsonUtil() {
    }

    public static Gson getGson() {
        return gson;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> json2Map(String jsonSrc) {
        return gson.fromJson(jsonSrc, Map.class);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> json2Map(Reader reader) {
        return gson.fromJson(reader, Map.class);
    }

    public static List json2List(String jsonSrc) {
        return gson.fromJson(jsonSrc, List.class);
    }

    public static List json2List(Reader reader) {
        return gson.fromJson(reader, List.class);
    }

    public static <T> T json2AnyType(Reader reader, Type type) {
        return gson.fromJson(reader, type);
    }

    public static <T> T json2AnyType(String jsonSrc, Type type) {
        return gson.fromJson(jsonSrc, type);
    }

    public static <T> T json2Object(String jsonSrc, Class<T> type) {
        return gson.fromJson(jsonSrc, type);
    }

    public static <T> T json2Object(Reader reader, Class<T> type) {
        return gson.fromJson(reader, type);
    }

    public static String json2Xml(String rootEleName, String jsonSrc) {
        XStream xStream = XmlUtil.xStream;
        xStream.alias(rootEleName, LinkedTreeMap.class);
        return xStream.toXML(json2Map(jsonSrc));
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(String jsonSrc, String key, Class<T> type) {
        return (T) json2Map(jsonSrc).get(key);
    }

    public static String object2Json(Object obj) {
        return gson.toJson(obj);
    }

    public static boolean validate(String jsonSrc) {
        try {
            gson.fromJson(jsonSrc, Object.class);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }
}
