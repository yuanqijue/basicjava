package com.aaronwang.design.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 9999));
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        byte[] bytes = new byte[1024];
//
//        socketChannel.read(buffer);
//        buffer.flip();
//        buffer.get(bytes);
//        System.out.println(new String(bytes, Charset.forName("utf-8")));

        Scanner scan = new Scanner(System.in);
        String line = scan.nextLine();
        socketChannel.write(ByteBuffer.wrap(line.getBytes()));
        Thread.sleep(10000);
        socketChannel.write(ByteBuffer.wrap("结束".getBytes()));
        Thread.sleep(10000);
        socketChannel.close();

        Thread.sleep(10000);
    }
}
