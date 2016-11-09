package com.turel.utils.dropwizard;

import com.codahale.metrics.Gauge;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by chaimturkel on 8/22/16.
 */
public class SimpleGauge implements Gauge<Long> {
    private String name;
    private final AtomicLong count = new AtomicLong();


    public SimpleGauge(String name){
        this.name = name;
    }

    public void resetValue() {
        count.set(0);
    }

    public void mark() {
        mark(1);
    }

    public void mark(long n) {
        count.addAndGet(n);
    }

    public void setValue(long n){
        count.set(n);
    }

    public String getName() {
        return name;
    }

    @Override
    public Long getValue() {
        return count.longValue();
    }
}
