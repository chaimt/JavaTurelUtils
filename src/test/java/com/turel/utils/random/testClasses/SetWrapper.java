package com.turel.utils.random.testClasses;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Chaim on 08/05/2017.
 */
public class SetWrapper {private Set<ValueClass> mySet;
    private Set mySetNoGenerics;
    private Set<List<ValueClass>> complexList;
    private Set<Set<ValueClass>> complexSet;
    private Set<Map<String,ValueClass>> complexMap;


    public Set getMySetNoGenerics() {
        return mySetNoGenerics;
    }

    public void setMySetNoGenerics(Set mySetNoGenerics) {
        this.mySetNoGenerics = mySetNoGenerics;
    }

    public Set<ValueClass> getMySet() {
        return mySet;
    }

    public void setMySet(Set<ValueClass> mySet) {
        this.mySet = mySet;
    }

    public Set<List<ValueClass>> getComplexList() {
        return complexList;
    }

    public void setComplexList(Set<List<ValueClass>> complexList) {
        this.complexList = complexList;
    }

    public Set<Set<ValueClass>> getComplexSet() {
        return complexSet;
    }

    public void setComplexSet(Set<Set<ValueClass>> complexSet) {
        this.complexSet = complexSet;
    }

    public Set<Map<String, ValueClass>> getComplexMap() {
        return complexMap;
    }

    public void setComplexMap(Set<Map<String, ValueClass>> complexMap) {
        this.complexMap = complexMap;
    }
}
