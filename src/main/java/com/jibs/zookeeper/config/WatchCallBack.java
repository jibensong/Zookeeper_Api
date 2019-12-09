package com.jibs.zookeeper.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class WatchCallBack implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {

    ZooKeeper zk;

    private MyConf myConf;

    CountDownLatch cd = new CountDownLatch(1);

    public WatchCallBack( ZooKeeper zk){
        this.zk = zk;
    }

    public void aWait(){
        zk.exists("/testConf", this, this, "ABC");
        try {
            cd.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        if(null !=  data){
            String s = new String(data);
            myConf.setConf(s);
            cd.countDown();
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {

        if(null != stat){
            zk.getData("/testConf",this,  this,"sdsdf");
        }
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                zk.getData("/testConf",this,  this,"sdsdf");
                break;
            case NodeDeleted:
                //容忍性
                myConf.setConf("");
                cd = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                zk.getData("/testConf",this,  this,"sdsdf");
                break;
            case NodeChildrenChanged:
                break;
        }
    }

    public MyConf getMyConf() {
        return myConf;
    }

    public void setMyConf(MyConf myConf) {
        this.myConf = myConf;
    }
}
