package com.turel.utils.dropwizard;


import com.codahale.metrics.Gauge;
import org.joda.time.LocalDate;

import java.util.concurrent.atomic.LongAdder;

/**
 * Created by chaimturkel on 8/7/16.
 */
public class LongDailyGauge implements Gauge<Long>{

    private String name;
    private LocalDate lastUpdate;

    private final LongAdder count = new LongAdder();


    protected void checkDayReset(){
        if (!lastUpdate.equals(LocalDate.now())){
            resetValue();
            lastUpdate = LocalDate.now();
        }
    }

    public LongDailyGauge(String name){
        this.name = name;
        lastUpdate = LocalDate.now();
    }


    public void resetValue() {
        count.reset();
    }

    public void mark() {
        mark(1);
    }

    public void mark(long n) {
        count.add(n);
    }

    public String getName() {
        return name;
    }

    @Override
    public Long getValue() {
        checkDayReset();
        return count.longValue();
    }
}
