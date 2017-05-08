package com.turel.utils.random.testClasses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Chaim on 08/05/2017.
 */
public class HashWrapper {
    private HashMap<String, ValueClass> hashMap = new HashMap<String, ValueClass>();
    private HashMap<Object, ValueClass> objectHashMap = new HashMap<Object, ValueClass>();
    private HashMap noGenericsHashMap = new HashMap();

    private Map<String,List<ValueClass>> complexList;
    private Map<String,Set<ValueClass>> complexSet;
    private Map<String,Map<String,ValueClass>> complexMap;

    public HashMap<String, ValueClass> getHashMap() {
        return hashMap;
    }
    public void setHashMap(HashMap<String, ValueClass> hashMap) {
        this.hashMap = hashMap;
    }
    public HashMap getNoGenericsHashMap() {
        return noGenericsHashMap;
    }
    public void setNoGenericsHashMap(HashMap noGenericsHashMap) {
        this.noGenericsHashMap = noGenericsHashMap;
    }
    public HashMap<Object, ValueClass> getObjectHashMap() {
        return objectHashMap;
    }
    public void setObjectHashMap(HashMap<Object, ValueClass> objectHashMap) {
        this.objectHashMap = objectHashMap;
    }
    public Map<String, List<ValueClass>> getComplexList() {
        return complexList;
    }
    public void setComplexList(Map<String, List<ValueClass>> complexList) {
        this.complexList = complexList;
    }
    public Map<String, Set<ValueClass>> getComplexSet() {
        return complexSet;
    }
    public void setComplexSet(Map<String, Set<ValueClass>> complexSet) {
        this.complexSet = complexSet;
    }
    public Map<String, Map<String, ValueClass>> getComplexMap() {
        return complexMap;
    }
    public void setComplexMap(Map<String, Map<String, ValueClass>> complexMap) {
        this.complexMap = complexMap;
    }
}
