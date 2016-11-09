package com.turel.utils;

import java.time.LocalTime;
import java.time.temporal.TemporalAmount;
import java.util.function.Supplier;

/**
 * Created by chaimturkel on 7/17/16.
 */
public class TimedCacheValue<T> {

    private TemporalAmount intervalAmount;
    private LocalTime lastSuccess = null;
    private T lastValue;

    private Supplier<T> func;

    public TimedCacheValue(final TemporalAmount intervalAmount, Supplier<T> func){
        this.intervalAmount = intervalAmount;
        reset();
        this.func = func;
    }

    public T get(){
        if (LocalTime.now().minus(intervalAmount).isAfter(lastSuccess)){
            setValue(func.get());
        }
        return lastValue;
    }
    
    public void reset() {
    	lastSuccess = LocalTime.now().minus(intervalAmount);
    }

    private void setValue(T newValue){
        lastValue = newValue;
        lastSuccess = LocalTime.now();
    }
}
