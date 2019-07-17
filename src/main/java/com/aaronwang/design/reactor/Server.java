package com.aaronwang.design.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;


/**
 * https://github.com/kasun04/rnd/tree/master/nio-reactor
 * https://github.com/iluwatar/java-design-patterns/tree/master/reactor
 */
public class Server {


    public static void main(String[] args) throws IOException, InterruptedException {

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(9999));
        Reactor dispatcher = new Reactor(ssc);

        LoggingHandler loggingHandler = new LoggingHandler();
        dispatcher.registerHandler(loggingHandler, SelectionKey.OP_READ);
        dispatcher.handleEvents();
    }
}
