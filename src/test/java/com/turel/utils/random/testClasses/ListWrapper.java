package com.turel.utils.random.testClasses;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Chaim on 08/05/2017.
 */
public class ListWrapper {private List<ValueClass> a;
    private List b;
    private List<List<ValueClass>> complexList;
    private List<Set<ValueClass>> complexSet;
    private List<Map<String,ValueClass>> complexMap;


    public List<ValueClass> getA() {
        return a;
    }
    public void setA(List<ValueClass> a) {
        this.a = a;
    }
    public List getB() {
        return b;
    }
    public void setB(List b) {
        this.b = b;
    }
    public List<List<ValueClass>> getComplexList() {
        return complexList;
    }
    public void setComplexList(List<List<ValueClass>> complexList) {
        this.complexList = complexList;
    }
    public List<Set<ValueClass>> getComplexSet() {
        return complexSet;
    }
    public void setComplexSet(List<Set<ValueClass>> complexSet) {
        this.complexSet = complexSet;
    }
    public List<Map<String, ValueClass>> getComplexMap() {
        return complexMap;
    }
    public void setComplexMap(List<Map<String, ValueClass>> complexMap) {
        this.complexMap = complexMap;
    }
}
