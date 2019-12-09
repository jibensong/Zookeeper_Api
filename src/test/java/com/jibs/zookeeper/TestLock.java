package com.jibs.zookeeper;

import com.jibs.zookeeper.com.jibs.zookeeper.lock.WatchCallBack;
import com.jibs.zookeeper.config.ZKUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLock {
    ZooKeeper zk;

    @Before
    public void conn(){
        zk = ZKUtils.getZK();
    }

    @After
    public void close(){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void lock(){
        for (int i = 0; i < 10; i++) {

            new Thread(){
                @Override
                public void run(){
                    WatchCallBack watchCallBack = new WatchCallBack(zk);
                    //每一个线程 去抢锁
                    String name = Thread.currentThread().getName();
                    watchCallBack.setThreadName(name);
                    watchCallBack.tryLock();
                    //干活
                    System.out.println(name+"working...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //释放锁
                    watchCallBack.unLock();
                }
            }.start();
        }

    }
}
