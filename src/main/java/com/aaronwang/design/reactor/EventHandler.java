package com.aaronwang.design.reactor;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class EventHandler {

    private static ExecutorService threadPool = Executors.newFixedThreadPool(5);

    public void read(final SocketChannel channel) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                doRead(channel);
            }
        });
    }

    public void write(final Object value) {
        doWrite(value);
    }


    public void doRead(SocketChannel channel) {
    }

    public void doWrite(Object value) {
    }

    public void stop() throws InterruptedException {
        threadPool.shutdown();
        threadPool.awaitTermination(4, TimeUnit.SECONDS);
    }
}
