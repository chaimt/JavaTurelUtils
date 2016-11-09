package com.turel.utils.concurrency;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by chaimturkel on 9/21/16.
 */
public class CodeSynchronizerConditionTest {

    int data=0;


    @Test
    public void testOneNotBlockCondition(){
        data=0;

        CodeSynchronizerCondition codeSynchronizer = new CodeSynchronizerCondition();
        codeSynchronizer.enterOnce(() -> true, () -> {
            data=1;
        });

        Assert.assertEquals(1,data);

        codeSynchronizer.enterOnce(() -> false, () -> {
            data=2;
        });

        Assert.assertEquals(1,data);

    }



    @Test
    public void testOneNotBlock(){
        data=0;

        CodeSynchronizer codeSynchronizer = new CodeSynchronizer();
        codeSynchronizer.enterOnce(() -> {
                data=1;
        });

        Assert.assertEquals(1,data);

    }

    @Test
    public void testTwoNotBlock(){
        data=0;

        CodeSynchronizer codeSynchronizer = new CodeSynchronizer();
        codeSynchronizer.enterOnce(() -> {
            data=1;
        });

        codeSynchronizer.enterOnce(() -> {
            data=2;
        });

        Assert.assertEquals(2,data);

    }


    @Test
    public void testOneNotBlockThreaded() throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        data=0;

        CodeSynchronizer codeSynchronizer = new CodeSynchronizer();
        executor.submit(() ->{
            codeSynchronizer.enterOnce(() -> {
                data=1;
            });
        });

        executor.awaitTermination(5, TimeUnit.SECONDS);

        Assert.assertEquals(1,data);

    }

    @Test
    public void testTwoBlockThreaded() throws InterruptedException {

        data=0;

        final CodeSynchronizer codeSynchronizer = new CodeSynchronizer();
        Thread a = new Thread(() ->{
            codeSynchronizer.enterOnce(() -> {
                data=1;
                System.out.println(data);
            });
        });

        Thread b = new Thread(() ->{
            codeSynchronizer.enterOnce(() -> {
                data=2;
                System.out.println(data);
            });
        });

        a.start();
        Thread.sleep(100);
        b.start();

        Thread.sleep(1000);

        Assert.assertEquals(2,data);

    }

    @Test
    public void testTwoBlockConcurrentThreaded() throws InterruptedException {
        data=0;

        final CodeSynchronizer codeSynchronizer = new CodeSynchronizer();
        Thread a = new Thread(() ->{
            codeSynchronizer.enterOnce(() -> {
                data=1;
                System.out.println(data);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });

        Thread b = new Thread(() ->{
            codeSynchronizer.enterOnce(() -> {
                data=2;
                System.out.println(data);
            });
        });

        a.start();
        Thread.sleep(100);
        b.start();

        Thread.sleep(2000);

        Assert.assertEquals(1,data);

    }

}
