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

        Reactor dispatcher = new Reactor(9999);

        LoggingHandler loggingHandler = new LoggingHandler();
        dispatcher.registerHandler(loggingHandler, SelectionKey.OP_READ);
        dispatcher.handleEvents();
    }
}
