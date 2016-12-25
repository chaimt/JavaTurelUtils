package com.angelsense.common.util;

import org.apache.log4j.MDC;

import java.util.*;

/**
 * Created by chaimturkel on 12/25/16.
 */
public class MDCStack {

    static public class MDCData {
        String key;
        Object value;

        public MDCData(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    static ThreadLocal<Stack<List<MDCData>>> keys = new ThreadLocal<Stack<List<MDCData>>>(){
        @Override protected Stack<List<MDCData>> initialValue() {
            final Stack stack = new Stack();
            stack.add(new ArrayList<>());
            return stack;
        }
    };

    static protected void rebuild(){
        final Stack<List<MDCData>> lists = keys.get();
        lists.forEach(mdcDatas -> {
            mdcDatas.forEach(mdcData -> MDC.put(mdcData.key,mdcData.value));
        });
    }

    static public void push(){
        final Stack<List<MDCData>> lists = keys.get();
        lists.add(new ArrayList<>());
    }

    static public void pop(){
        final Stack<List<MDCData>> lists = keys.get();
        if (lists.size()>0) {
            final List<MDCData> poll = lists.pop();
            poll.forEach(d -> MDC.remove(d.key));
            rebuild();
        }
        if (lists.size()==0){
            lists.add(new ArrayList<>());
        }
    }

    static public void put(String key, Object value){
        final Stack<List<MDCData>> lists = keys.get();
        final List<MDCData> peek = lists.peek();
        peek.add(new MDCData(key,value));
        MDC.put(key,value);
    }

    static public void remove(String key){
        final Stack<List<MDCData>> lists = keys.get();
        final List<MDCData> peek = lists.peek();
        peek.stream()
                .filter(mdcData -> mdcData.key.equals(key))
                .findFirst()
                .ifPresent(mdcData -> peek.remove(mdcData));
        MDC.remove(key);
    }

    static public void clear(){
        MDC.clear();
        final Stack<List<MDCData>> lists = keys.get();
        lists.clear();
        lists.add(new ArrayList<>());
    }


}
