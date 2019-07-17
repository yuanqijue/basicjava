package com.aaronwang.design.reactor;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;

public class Reactor {

    private ServerSocketChannel ssc;
    private Selector selector;

    private Map<Integer, EventHandler> eventHandlerMap = new ConcurrentHashMap<>();

    public Reactor(ServerSocketChannel ssc) throws IOException {
        this.ssc = ssc;
        selector = Selector.open();
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
        key.
        SelectionKey readKey = socketChannel.register(selector, OP_READ);
        // todo 绑定注册的handler,只绑定了读
        readKey.attach(eventHandlerMap.get(OP_READ));
    }

    private void onChannelReadable(SelectionKey key) {

        SocketChannel channel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(5);
        List<Byte> bs = new ArrayList<>();
        try {
            while (channel.read(buffer) != -1) {
                buffer.flip();
                if (buffer.limit() == 0) {
                    Thread.sleep(1000);
                }
                while (buffer.hasRemaining()) {
                    bs.add(buffer.get());
                }
                buffer.clear();
            }
            channel.close();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        String inputStr = new String(ArrayUtils.toPrimitive(bs.toArray(new Byte[0])), Charset.forName("utf-8"));


        EventHandler handler = (EventHandler) key.attachment();
        if (handler != null) {
            handler.read(inputStr);
        }
    }

    private static void onChannelWritable(SelectionKey key) throws IOException {
        EventHandler handler = (EventHandler) key.attachment();
        if (handler != null) {
            handler.write(key);
        }
    }

}
