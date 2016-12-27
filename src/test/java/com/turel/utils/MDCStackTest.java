package com.turel.utils;

import org.apache.log4j.MDC;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by chaimturkel on 12/25/16.
 */
public class MDCStackTest {

    @Before
    public void init(){
        MDCStack.clear();
    }

    @Test
    public void noPush(){
        MDCStack.put("a","1");
        Assert.assertEquals("1",MDC.get("a"));
    }

    @Test
    public void push(){
        MDCStack.put("a","1");
        Assert.assertEquals("1",MDC.get("a"));
        MDCStack.push();
        Assert.assertEquals("1",MDC.get("a"));
        MDCStack.put("a","10");
        MDCStack.put("b","3");
        Assert.assertEquals("10",MDC.get("a"));
        Assert.assertEquals("3",MDC.get("b"));
    }

    @Test
    public void pop(){
        MDCStack.push();
        MDCStack.put("a","1");
        MDCStack.put("b","3");
        Assert.assertEquals("1",MDC.get("a"));
        Assert.assertEquals("3",MDC.get("b"));
        MDCStack.pop();
        Assert.assertNull(MDC.get("a"));
        Assert.assertNull(MDC.get("b"));
    }

    @Test
    public void popDuplicate(){
        MDCStack.put("a","1");
        Assert.assertEquals("1",MDC.get("a"));
        MDCStack.push();
        Assert.assertEquals("1",MDC.get("a"));
        MDCStack.put("a","10");
        MDCStack.put("b","3");
        Assert.assertEquals("10",MDC.get("a"));
        Assert.assertEquals("3",MDC.get("b"));
        MDCStack.pop();
        Assert.assertNull(MDC.get("b"));
        Assert.assertEquals("1",MDC.get("a"));
    }

    @Test
    public void popDuplicateWithPush(){
        MDCStack.push();
        MDCStack.put("a","1");
        Assert.assertEquals("1",MDC.get("a"));
        MDCStack.push();
        Assert.assertEquals("1",MDC.get("a"));
        MDCStack.put("a","10");
        MDCStack.put("b","3");
        Assert.assertEquals("10",MDC.get("a"));
        Assert.assertEquals("3",MDC.get("b"));
        MDCStack.pop();
        Assert.assertNull(MDC.get("b"));
        Assert.assertEquals("1",MDC.get("a"));
    }

    @Test
    public void nested(){
        MDCStack.put("a","1");
        MDCStack.push();
        MDCStack.put("b","1");
        MDCStack.push();
        MDCStack.put("c","1");
        Assert.assertEquals("1",MDC.get("a"));
        Assert.assertEquals("1",MDC.get("b"));
        Assert.assertEquals("1",MDC.get("c"));
        MDCStack.pop();
        Assert.assertEquals("1",MDC.get("a"));
        Assert.assertEquals("1",MDC.get("b"));
        Assert.assertNull(MDC.get("c"));
        MDCStack.pop();
        Assert.assertEquals("1",MDC.get("a"));
        Assert.assertNull(MDC.get("b"));
        Assert.assertNull(MDC.get("c"));
        MDCStack.pop();
        Assert.assertNull(MDC.get("a"));
        Assert.assertNull(MDC.get("b"));
        Assert.assertNull(MDC.get("c"));
    }


    @Test
    public void invalidPop(){
        MDCStack.push();
        MDCStack.pop();
        MDCStack.pop();
        MDCStack.pop();
        MDCStack.put("a","b");
        Assert.assertEquals("b",MDC.get("a"));
    }

    @Test
    public void remove(){
        MDCStack.put("a","1");
        MDCStack.push();
        MDCStack.put("a","b");
        MDCStack.put("c","d");
        Assert.assertEquals("b",MDC.get("a"));
        Assert.assertEquals("d",MDC.get("c"));
        MDCStack.remove("a");
        Assert.assertNull(MDC.get("a"));
        MDCStack.pop();
        Assert.assertEquals("1",MDC.get("a"));
        Assert.assertNull(MDC.get("b"));
    }


}
