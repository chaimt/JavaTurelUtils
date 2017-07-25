package com.turel.utils.js;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.Assert;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Created by Chaim on 20/07/2017.
 */
public class udfScripts {

    private Object invokeFunction(String name, Object... args) {
        try {
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("JavaScript");
//            ScriptEngine nashorn = factory.getEngineByName("nashorn");
            final File file = new File(".", "src/main/resources/js/dateFunctions.js");
            engine.eval(new java.io.FileReader(file));
            Invocable inv = (Invocable) engine;
            return inv.invokeFunction(name, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testInputInternal() throws ScriptException, NoSuchMethodException, FileNotFoundException {
        final Object o = invokeFunction("testInputInternal", "Sat Dec 31 2016 16:00:00 GMT-0800 (PST)");
        System.out.println(o.toString());
    }

    @Test
    public void getBetweenMonthsInclusiveInternal() throws FileNotFoundException, ScriptException, NoSuchMethodException {
        ScriptObjectMirror res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsInclusiveInternal", "Sat Dec 31 2016 16:00:00 GMT-0800 (PST)", "Sun Jan 01 2017 06:44:58 GMT-0800 (PST)");
        Assert.assertEquals(1, res.get("0"));

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsInclusiveInternal", "Sat Dec 31 2018 16:00:00 GMT-0000 (UTC)", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(0, res.size());


        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsInclusiveInternal", "Sat Dec 31 2016 16:00:00 GMT-0000 (UTC)", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(2, res.size());
        Assert.assertEquals(12, res.get("0"));
        Assert.assertEquals(1, res.get("1"));

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsInclusiveInternal", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)", "Sun March 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(3, res.size());
        Assert.assertEquals(1, res.get("0"));
        Assert.assertEquals(2, res.get("1"));
        Assert.assertEquals(3, res.get("2"));

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsInclusiveInternal", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)", "Sun August 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(8, res.size());
        Assert.assertEquals(1, res.get("0"));
        Assert.assertEquals(2, res.get("1"));
        Assert.assertEquals(3, res.get("2"));
        Assert.assertEquals(4, res.get("3"));
        Assert.assertEquals(5, res.get("4"));
        Assert.assertEquals(6, res.get("5"));
        Assert.assertEquals(7, res.get("6"));
        Assert.assertEquals(8, res.get("7"));


        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsInclusiveInternal", "Sun Jan 01 2015 06:44:58 GMT-0000 (UTC)", "Sun August 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(32, res.size());
        Assert.assertEquals(1, res.get("0"));
        Assert.assertEquals(2, res.get("1"));
        Assert.assertEquals(3, res.get("2"));
        Assert.assertEquals(4, res.get("3"));
        Assert.assertEquals(5, res.get("4"));
        Assert.assertEquals(6, res.get("5"));
        Assert.assertEquals(7, res.get("6"));
        Assert.assertEquals(8, res.get("7"));
        Assert.assertEquals(9, res.get("8"));
        Assert.assertEquals(10, res.get("9"));
        Assert.assertEquals(11, res.get("10"));
        Assert.assertEquals(12, res.get("11"));

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsInclusiveInternal", "2017-06-04 11:22:40 UTC", "2999-07-06 03:00:59 UTC");
        Assert.assertTrue(res.size()<100);


    }

    @Test
    public void getBetweenMonthsLeftInclusiveInternal() throws FileNotFoundException, ScriptException, NoSuchMethodException {

        ScriptObjectMirror res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsLeftInclusiveInternal", "Sat Dec 31 2016 16:00:00 GMT-0000 (UTC)", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(1, res.size());
        Assert.assertEquals(12, res.get("0"));

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsLeftInclusiveInternal", "Sat Dec 31 2018 16:00:00 GMT-0000 (UTC)", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(0, res.size());

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsLeftInclusiveInternal", "Sun Jan 08 2017 06:44:58 GMT-0000 (UTC)", "Sun Jan 20 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(0, res.size());

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsLeftInclusiveInternal", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)", "Sun March 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(2, res.size());
        Assert.assertEquals(1, res.get("0"));
        Assert.assertEquals(2, res.get("1"));

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsLeftInclusiveInternal", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)", "Sun March 10 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(2, res.size());
        Assert.assertEquals(1, res.get("0"));
        Assert.assertEquals(2, res.get("1"));

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsLeftInclusiveInternal", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)", "Sun August 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(7, res.size());
        Assert.assertEquals(1, res.get("0"));
        Assert.assertEquals(2, res.get("1"));
        Assert.assertEquals(3, res.get("2"));
        Assert.assertEquals(4, res.get("3"));
        Assert.assertEquals(5, res.get("4"));
        Assert.assertEquals(6, res.get("5"));
        Assert.assertEquals(7, res.get("6"));


        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsLeftInclusiveInternal", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(0, res.size());

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsLeftInclusiveInternal", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)", "Sun March 30 2017 12:00:00 GMT-0000 (UTC)");
        Assert.assertEquals(2, res.size());
        Assert.assertEquals(1, res.get("0"));
        Assert.assertEquals(2, res.get("1"));

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsLeftInclusiveInternal", "Sun Jan 01 2015 06:44:58 GMT-0000 (UTC)", "Sun August 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(31, res.size());
        Assert.assertEquals(1, res.get("0"));
        Assert.assertEquals(2, res.get("1"));
        Assert.assertEquals(3, res.get("2"));
        Assert.assertEquals(4, res.get("3"));
        Assert.assertEquals(5, res.get("4"));
        Assert.assertEquals(6, res.get("5"));
        Assert.assertEquals(7, res.get("6"));
        Assert.assertEquals(8, res.get("7"));
        Assert.assertEquals(9, res.get("8"));
        Assert.assertEquals(10, res.get("9"));
        Assert.assertEquals(11, res.get("10"));
        Assert.assertEquals(12, res.get("11"));

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsLeftInclusiveInternal", "Fri June 30 2017 04:34:22 GMT-0000 (UTC)", "Tue February 27 2018 04:34:58 GMT-0000 (UTC)");
        Assert.assertEquals(8, res.size());
        Assert.assertEquals(6, res.get("0"));
        Assert.assertEquals(7, res.get("1"));
        Assert.assertEquals(8, res.get("2"));
        Assert.assertEquals(9, res.get("3"));
        Assert.assertEquals(10, res.get("4"));
        Assert.assertEquals(11, res.get("5"));
        Assert.assertEquals(12, res.get("6"));
        Assert.assertEquals(1, res.get("7"));

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsLeftInclusiveInternal", "2017-06-04 11:22:40 UTC", "2999-07-06 03:00:59 UTC");
        Assert.assertTrue(res.size()<100);


    }

    @Test
    public void getBetweenMonthsRightInclusiveInternal() throws FileNotFoundException, ScriptException, NoSuchMethodException {
        ScriptObjectMirror res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsRightInclusiveInternal", "Sat Dec 31 2016 16:00:00 GMT-0000 (UTC)", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(1, res.size());
        Assert.assertEquals(1, res.get("0"));

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsRightInclusiveInternal", "Sat Dec 31 2018 16:00:00 GMT-0000 (UTC)", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(0, res.size());


        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsRightInclusiveInternal", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)", "Sun March 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(2, res.size());
        Assert.assertEquals(2, res.get("0"));
        Assert.assertEquals(3, res.get("1"));

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsRightInclusiveInternal", "Sun Jan 05 2017 06:44:58 GMT-0000 (UTC)", "Sun March 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(2, res.size());
        Assert.assertEquals(2, res.get("0"));
        Assert.assertEquals(3, res.get("1"));

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsRightInclusiveInternal", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)", "Sun August 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(7, res.size());
        Assert.assertEquals(2, res.get("0"));
        Assert.assertEquals(3, res.get("1"));
        Assert.assertEquals(4, res.get("2"));
        Assert.assertEquals(5, res.get("3"));
        Assert.assertEquals(6, res.get("4"));
        Assert.assertEquals(7, res.get("5"));
        Assert.assertEquals(8, res.get("6"));


        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsRightInclusiveInternal", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(0, res.size());

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsRightInclusiveInternal", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)", "Sun March 30 2017 12:00:00 GMT-0000 (UTC)");
        Assert.assertEquals(2, res.size());
        Assert.assertEquals(2, res.get("0"));
        Assert.assertEquals(3, res.get("1"));

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsRightInclusiveInternal", "Sun Jan 01 2015 06:44:58 GMT-0000 (UTC)", "Sun August 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(31, res.size());
        Assert.assertEquals(2, res.get("0"));
        Assert.assertEquals(3, res.get("1"));
        Assert.assertEquals(4, res.get("2"));
        Assert.assertEquals(5, res.get("3"));
        Assert.assertEquals(6, res.get("4"));
        Assert.assertEquals(7, res.get("5"));
        Assert.assertEquals(8, res.get("6"));
        Assert.assertEquals(9, res.get("7"));
        Assert.assertEquals(10, res.get("8"));
        Assert.assertEquals(11, res.get("9"));
        Assert.assertEquals(12, res.get("10"));

        res = (ScriptObjectMirror) invokeFunction("getBetweenMonthsRightInclusiveInternal", "2017-06-04 11:22:40 UTC", "2999-07-06 03:00:59 UTC");
        Assert.assertTrue(res.size()<100);

    }

    @Test
    public void getEndMonthsLeftInclusiveInternal(){
        ScriptObjectMirror res = (ScriptObjectMirror) invokeFunction("getEndMonthsLeftInclusiveInternal", "Sat Dec 31 2016 16:00:00 GMT-0000 (UTC)", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(1, res.size());
        LocalDateTime testDateTime = LocalDateTime.ofEpochSecond(((Double) res.get("0")).longValue()/1000, 0, ZoneOffset.UTC);
        Assert.assertEquals(LocalDateTime.of(2016, 12, 31,0,0),testDateTime);

        res = (ScriptObjectMirror) invokeFunction("getEndMonthsLeftInclusiveInternal", "Sat Dec 31 2016 00:00:00 GMT-0000 (UTC)", "Sun Jan 01 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(1, res.size());
        testDateTime = LocalDateTime.ofEpochSecond(((Double) res.get("0")).longValue()/1000, 0, ZoneOffset.UTC);
        Assert.assertEquals(LocalDateTime.of(2016, 12, 31,0,0),testDateTime);

        res = (ScriptObjectMirror) invokeFunction("getEndMonthsLeftInclusiveInternal", "Sun Jan 01 2017 00:00:00 GMT-0000 (UTC)", "Sun Jan 02 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(0, res.size());

        res = (ScriptObjectMirror) invokeFunction("getEndMonthsLeftInclusiveInternal", "Sun Jan 01 2017 00:00:00 GMT-0000 (UTC)", "Sun Feb 02 2017 06:44:58 GMT-0000 (UTC)");
        Assert.assertEquals(1, res.size());
        testDateTime = LocalDateTime.ofEpochSecond(((Double) res.get("0")).longValue()/1000, 0, ZoneOffset.UTC);
        Assert.assertEquals(LocalDateTime.of(2017, 01, 31,0,0),testDateTime);

        res = (ScriptObjectMirror) invokeFunction("getEndMonthsLeftInclusiveInternal", "2017-06-04 11:22:40 UTC", "2017-07-06 03:00:59 UTC");
        Assert.assertEquals(1, res.size());
        testDateTime = LocalDateTime.ofEpochSecond(((Double) res.get("0")).longValue()/1000, 0, ZoneOffset.UTC);
        Assert.assertEquals((LocalDateTime.of(2017,6,30,0,0)),testDateTime);

        res = (ScriptObjectMirror) invokeFunction("getEndMonthsLeftInclusiveInternal", "2017-06-04 11:22:40 UTC", "2017-09-06 03:00:59 UTC");
        Assert.assertEquals(3, res.size());
        Assert.assertEquals((LocalDateTime.of(2017,6,30,0,0)),LocalDateTime.ofEpochSecond(((Double) res.get("0")).longValue()/1000, 0, ZoneOffset.UTC));
        Assert.assertEquals((LocalDateTime.of(2017,7,31,0,0)),LocalDateTime.ofEpochSecond(((Double) res.get("1")).longValue()/1000, 0, ZoneOffset.UTC));
        Assert.assertEquals((LocalDateTime.of(2017,8,31,0,0)),LocalDateTime.ofEpochSecond(((Double) res.get("2")).longValue()/1000, 0, ZoneOffset.UTC));

        res = (ScriptObjectMirror) invokeFunction("getEndMonthsLeftInclusiveInternal", "2017-06-04 11:22:40 UTC", "2999-07-06 03:00:59 UTC");
        Assert.assertTrue(res.size()<100);

        res = (ScriptObjectMirror) invokeFunction("getEndMonthsLeftInclusiveInternal", "2017-06-04 11:20:51 UTC", "2999-01-01 00:00:00 UTC");
        Assert.assertTrue(res.size()<100);

        res = (ScriptObjectMirror) invokeFunction("getEndMonthsLeftInclusiveInternal", "2017-06-04 11:20:51 UTC", "2999-01-01 00:00:00 UTC");
        Assert.assertTrue(res.size()<100);

    }
}
