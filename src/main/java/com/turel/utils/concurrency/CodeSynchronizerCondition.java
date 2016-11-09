package com.turel.utils.concurrency;

import java.util.function.BooleanSupplier;

/**
 * Created by chaimturkel on 9/21/16.
 *
 * Will make sure that even from multiple threads you will not have code run twice at the same time.
 */
public class CodeSynchronizerCondition {
    private volatile boolean inProcess = false;

    public void enterOnce(BooleanSupplier enterContition, Runnable code){
        if (inProcess || !enterContition.getAsBoolean()) return;
        inProcess = true;
        try {
            code.run();
        }finally {
            inProcess = false;
        }
    }
}
