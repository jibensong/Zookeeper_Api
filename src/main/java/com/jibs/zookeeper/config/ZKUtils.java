package com.jibs.zookeeper.config;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKUtils {
    private static ZooKeeper zk;

    private final static String address = "192.168.150.11:2181,192.168.150.12:2181/testConf";

    private static DefaultWatch watch = new DefaultWatch();

    private static CountDownLatch init = new CountDownLatch(1);
    public static ZooKeeper getZK(){
        try {
            zk = new ZooKeeper(address, 1000,watch );
            watch.setCc(init);
            init.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zk;
    }
}
