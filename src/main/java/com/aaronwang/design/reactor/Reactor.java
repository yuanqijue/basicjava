package com.aaronwang.design.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;

public class Reactor {

    private ServerSocketChannel ssc;
    private Selector selector;

    private Map<Integer, EventHandler> eventHandlerMap = new ConcurrentHashMap<>();

    public Reactor(int port) throws IOException {
        selector = SelectorProvider.provider().openSelector();
//        this.ssc = ssc;
        ssc = SelectorProvider.provider().openServerSocketChannel();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(port));
        //
        SelectionKey key = ssc.register(selector, OP_ACCEPT);
//        key.attach(key);
    }

    public void handleEvents() throws IOException, InterruptedException {
        while (!Thread.interrupted()) {
            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<SelectionKey> selected = selector.selectedKeys();
            Iterator<SelectionKey> it = selected.iterator();
            while (it.hasNext()) {
                dispatch(it.next());
            }
            selected.clear();
        }
    }

    private void dispatch(SelectionKey key) throws IOException, InterruptedException {

        if (key.isAcceptable()) {
            onChannelAcceptable(key);
        } else if (key.isReadable()) {
            onChannelReadable(key);
        } else if (key.isWritable()) {
            onChannelWritable(key);
        }
    }

    /**
     * 一个事件只支持注册一个handler
     *
     * @param handler
     * @param event
     * @return
     * @throws ClosedChannelException
     */
    public Reactor registerHandler(EventHandler handler, Integer event) throws ClosedChannelException {
        eventHandlerMap.put(event, handler);
        return this;
    }

    public void removeHandler(EventHandler handler, Integer event) {
        eventHandlerMap.remove(event);
    }

    private void onChannelAcceptable(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        SelectionKey readKey = socketChannel.register(selector, OP_READ);
        readKey.attach(eventHandlerMap.get(OP_READ));

        //https://github.com/netty/netty/issues/924
        key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT);
    }

    private void onChannelReadable(SelectionKey key) {

        SocketChannel channel = (SocketChannel) key.channel();

        EventHandler handler = (EventHandler) key.attachment();
        if (handler != null) {
            handler.read(channel);
        }
        key.cancel();
    }

    private static void onChannelWritable(SelectionKey key) throws IOException {
        EventHandler handler = (EventHandler) key.attachment();
        if (handler != null) {
            handler.write(key);
        }
    }

}
