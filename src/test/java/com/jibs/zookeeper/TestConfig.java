package com.jibs.zookeeper;

import com.jibs.zookeeper.config.MyConf;
import com.jibs.zookeeper.config.WatchCallBack;
import com.jibs.zookeeper.config.ZKUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.*;

public class TestConfig {

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
    public void getConf(){
        WatchCallBack watchCallBack = new WatchCallBack(zk);
        MyConf myConf = new MyConf();
        watchCallBack.setMyConf(myConf);
        watchCallBack.aWait();
        while(true){
            if(myConf.getConf().equals("")){
                System.out.println("conf diu le......");
                watchCallBack.aWait();
            } else {
                System.out.println(myConf.getConf());
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
