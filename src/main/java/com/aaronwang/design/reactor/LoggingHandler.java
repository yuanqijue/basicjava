package com.aaronwang.design.reactor;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class LoggingHandler extends EventHandler {

    @Override
    public void doRead(SocketChannel channel) {


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

        System.out.println("READ:" + inputStr);
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
