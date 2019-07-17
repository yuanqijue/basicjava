package com.aaronwang.design.reactor;

public class LoggingHandler extends EventHandler {

    @Override
    public void doRead(Object value) {
        System.out.println("READ:" + value.toString());
    }

    @Override
    public void doWrite(Object value) {
//        SocketChannel channel = (SocketChannel) key.channel();
//
//        ByteBuffer buffer = ByteBuffer.wrap("Hello World".getBytes());
//        try {
//            channel.write(buffer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
