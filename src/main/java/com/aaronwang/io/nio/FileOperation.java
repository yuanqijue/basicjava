package com.aaronwang.io.nio;

import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 场景：
 * 1. 大文件读取
 * 2. 文件拷贝
 * 3. 文件写入
 *
 * @author aaron
 */
public class FileOperation {

    public void copyFileWithChannel(String fromPath, String toPath) throws IOException {

        FileInputStream from = new FileInputStream(fromPath);
        FileOutputStream to = new FileOutputStream(toPath);
        FileChannel fromChannel = from.getChannel();
        FileChannel toChannel = to.getChannel();
        long start = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        fromChannel.transferTo(0, fromChannel.size(), toChannel);
        long end = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        System.out.println("copyFileWithChannel 耗时:" + (end - start));
        fromChannel.close();
        toChannel.close();
        from.close();
        to.close();
    }

    public void copyFileWithStream(String fromFilePath, String toFilePath) throws IOException {

        FileInputStream from = new FileInputStream(fromFilePath);
        FileOutputStream to = new FileOutputStream(toFilePath);

        long start = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        byte[] bs = new byte[1024];
        int length;
        while ((length = from.read(bs)) > 0) {
            to.write(bs, 0, length);
        }
        long end = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        System.out.println("copyFileWithStream 耗时:" + (end - start));
        from.close();
        to.close();
    }

    public void deleteFile(String filePath) {
        File file = new File(filePath);
        file.delete();
    }

    public void modifyFileWithChannel(String filePath) throws IOException {

        RandomAccessFile outputStream = new RandomAccessFile(filePath, "rw");
//        FileOutputStream outputStream = new FileOutputStream(filePath, true);
        FileChannel channel = outputStream.getChannel();

        //文本内容全部加载到内存
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        List<Byte> content = new LinkedList<>();
        while (channel.read(buffer) != -1) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                content.add(buffer.get());
            }
            buffer.clear();
        }

        // Netty ByteBuf

        // byte[] to Byte[] 内存拷贝
        content.addAll(0, Arrays.asList(ArrayUtils.toObject("modifyFileWithChannel 开始添加第一行 作者 王恒定\n".getBytes())));//

        content.addAll(Arrays.asList(ArrayUtils.toObject("modifyFileWithChannel 我修改了你，结束啦！！！作者 王恒定\n".getBytes())));//

        //  Byte[] to byte[] 内存拷贝
        buffer = ByteBuffer.wrap(ArrayUtils.toPrimitive(content.toArray(new Byte[0])));

        try {
            channel.write(buffer, 0);
            channel.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void modifyFileWithChannelTransfer(String filePath) throws IOException {

        RandomAccessFile r = new RandomAccessFile(filePath, "rw");
        RandomAccessFile temp = new RandomAccessFile(filePath + "~", "rw");
        long fileSize = r.length();
        FileChannel sourceChannel = r.getChannel();
        FileChannel targetChannel = temp.getChannel();

        byte[] insertBytes = "modifyFileWithChannelTransfer 开始添加第一行 作者 王恒定\n".getBytes();
        int offset = 0;
        sourceChannel.transferTo(offset, fileSize - offset, targetChannel);
        sourceChannel.truncate(offset);
        r.seek(offset);
        r.write(insertBytes);
        long newOffset = r.getFilePointer();
        targetChannel.position(0L);
        sourceChannel.transferFrom(targetChannel, newOffset, (fileSize - offset));
        r.seek(r.length());
        r.write("modifyFileWithChannelTransfer 我修改了你，结束啦！！！作者 王恒定\n".getBytes());
        sourceChannel.close();
        targetChannel.close();
        deleteFile(filePath + "~");
    }


    public static void main(String[] args) throws IOException {
        FileOperation operation = new FileOperation();

        // 拷贝
        String basePath = "/Users/aaron/workspace/basicjava/src/main/java/com/aaronwang/io/nio/";
//        operation.copyFileWithChannel(basePath + "CoreJava.rvt", basePath + "CoreJava 2.rvt");
//        operation.copyFileWithStream(basePath + "CoreJava.rvt", basePath + "CoreJava 3.rvt");

        // 删除
//        operation.deleteFile(basePath + "CoreJava 2.rvt");
//        operation.deleteFile(basePath + "CoreJava 3.rvt");


        // 修改
        operation.modifyFileWithChannel(basePath + "readme.txt");
        operation.modifyFileWithChannelTransfer(basePath + "readme.txt");
    }
}
