package com.turel.utils.concurrency;

/**
 * Created by chaimturkel on 9/21/16.
 *
 * Will make sure that even from multiple threads you will not have code run twice at the same time.
 */
public class CodeSynchronizer extends CodeSynchronizerCondition {
    public void enterOnce(Runnable code) {
        super.enterOnce(() -> true, code);
    }
}
