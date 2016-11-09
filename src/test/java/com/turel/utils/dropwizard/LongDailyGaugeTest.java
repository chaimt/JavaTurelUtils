package com.turel.utils.dropwizard;

import com.turel.utils.dropwizard.LongDailyGauge;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by chaimturkel on 8/7/16.
 */
public class LongDailyGaugeTest {

    @AfterClass
    static public void tearDown(){
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void markLongTest(){
        LongDailyGauge longLongDailyGauge = new LongDailyGauge("a.b.c");
        longLongDailyGauge.mark();
        Assert.assertEquals(1, (long)longLongDailyGauge.getValue());
    }

    @Test
    public void markNLongTest(){
        LongDailyGauge longLongDailyGauge = new LongDailyGauge("a.b.c");
        longLongDailyGauge.mark(22);
        Assert.assertEquals(22, (long)longLongDailyGauge.getValue());
    }

    @Test
    public void resetLongTest(){
        LongDailyGauge longLongDailyGauge = new LongDailyGauge("a.b.c");
        longLongDailyGauge.mark();
        Assert.assertEquals(1, (long)longLongDailyGauge.getValue());

        longLongDailyGauge.resetValue();
        Assert.assertEquals(0, (long)longLongDailyGauge.getValue());
    }

    @Test
    public void testDayChange(){
        LongDailyGauge longLongDailyGauge = new LongDailyGauge("a.b.c");
        longLongDailyGauge.mark(22);
        Assert.assertEquals(22, (long)longLongDailyGauge.getValue());


        DateTime tommarow = DateTime.now().plusDays(1);
            DateTimeUtils.setCurrentMillisFixed(tommarow.getMillis());

        Assert.assertEquals(0, (long)longLongDailyGauge.getValue());

    }
}
