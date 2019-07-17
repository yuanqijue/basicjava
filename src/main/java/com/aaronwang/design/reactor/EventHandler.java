package com.aaronwang.design.reactor;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class EventHandler {

    private static ExecutorService threadPool = Executors.newFixedThreadPool(5);

    public void read(final Object value) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                doRead(value);
            }
        });
    }

    public void write(final Object value) {
        doWrite(value);
    }


    public void doRead(Object value) {
    }

    public void doWrite(Object value) {
    }

    public void stop() throws InterruptedException {
        threadPool.shutdown();
        threadPool.awaitTermination(4, TimeUnit.SECONDS);
    }
}
