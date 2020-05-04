package com.aaronwang.threads;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;


/**
 *
 * JOL
 * https://hg.openjdk.java.net/code-tools/jol/file/56dbba3b2c20/jol-samples/src/main/java/org/openjdk/jol/samples/JOLSample_01_Basic.java
 *
 */
public class ObjectLayout {

    public static void main(String[] args) throws InterruptedException {

        // 使用 -XX:-UseCompressedOops 时，kclass pointer 不压缩占8个字节，默认压缩占4个字节。

        System.out.println(VM.current().details());

        // 偏向锁未启动
        T t = new T();
        // 普通对象，无锁，001
        System.out.println("synchronized before:" + ClassLayout.parseInstance(t).toPrintable());
        synchronized (t) {
            // 升级为轻量级锁 00
            System.out.println("synchronized:" + ClassLayout.parseInstance(t).toPrintable());
        }

        // 4秒后自动加匿名偏向锁。
        Thread.sleep(4000);

        T t2 = new T();
        // 匿名偏向，101
        System.out.println("synchronized before:" + ClassLayout.parseInstance(t2).toPrintable());
        synchronized (t2) {
            // 偏向锁，101
            System.out.println("synchronized:" + ClassLayout.parseInstance(t2).toPrintable());
        }
        // 偏向锁，101
        System.out.println("synchronized after:" + ClassLayout.parseInstance(t2).toPrintable());

    }

    static class T {
        private long i = 4;
        private volatile String str = "WHD";
    }
}
