package com.turel.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

import org.joda.time.DateTime;
import org.json.JSONObject;

import com.google.gson.*;

/**
 * Created by chaimturkel on 8/29/16.
 */
public class ReflectionUtils {

    private static final GsonBuilder gsonBuilder = new GsonBuilder();

    static {
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                DateTime ret = new DateTime(json.getAsString());
                return ret.toDate();
            }
        });

        gsonBuilder.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        });
    }

    private static Field getField(String name, Class<?> cls) throws NoSuchFieldException {
        try {
            return cls.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            final String camelName = name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
            return cls.getDeclaredField(camelName);
        }
    }

    public static <T> T createObject(JSONObject obj, Class<T> cls) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        if (obj==null){
            throw new RuntimeException("Object to map is null, for class " + cls.toString());
        }

        Gson gson = gsonBuilder.create();
        return gson.fromJson(obj.toString(), cls);
    }

    public static boolean isStatic(Field field){
        return java.lang.reflect.Modifier.isStatic(field.getModifiers());
    }

    public static <T> List<String> getFields(Class<T> cls) {
        List<String> names = new ArrayList<>();
        for (Field field :  cls.getDeclaredFields()){
            if ((!field.isEnumConstant() && (!field.isSynthetic())) && (!isStatic(field))) {
                names.add(field.getName());
            }
        }
        return names;
    }

    public static void copyFields(Object source, Object dest){
        getFields(source.getClass())
                .forEach(field -> {
                    try {
                        final Field srcField = source.getClass().getDeclaredField(field);
                        srcField.setAccessible(true);
                        final Object value = srcField.get(source);

                        final Field destField = dest.getClass().getDeclaredField(field);
                        destField.setAccessible(true);
                        destField.set(dest,value);
                    } catch (NoSuchFieldException e) {
                    } catch (IllegalAccessException e) {
                    }
                });

    }
}
