package com.turel.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Chaim on 09/03/2017.
 */
public class CollectionUtils {
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static List<String> getListFromCommaString(final String tableList){
        return (StringUtils.isEmpty(tableList) ?
                new LinkedList<String>() :
                Arrays.asList(tableList.split("\\s*,\\s*"))).stream().map(String::trim).collect(Collectors.toList());

    }

    public static Set<String> getSetFromCommaString(final String tableList) {
        return (StringUtils.isEmpty(tableList) ?
                new HashSet<>() :
                Arrays.asList(tableList.split("\\s*,\\s*")).stream().map(String::trim).collect(Collectors.toSet()));

    }

    private static Object getRecursiveData(Map<String, Object> data, final List<String> path) {
        final Object o = data.get(path.get(0));
        path.remove(0);
        if (path.size() == 0)
            return o;
        return getRecursiveData((Map<String, Object>) o, path);
    }

    public static Object getRecursiveData(Map<String, Object> data, final String path) {
        return getRecursiveData(data, Arrays.asList(path.split("\\.")).stream().map(String::trim).collect(Collectors.toList()));

    }
}
