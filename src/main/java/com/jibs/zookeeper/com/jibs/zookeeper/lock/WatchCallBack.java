package com.jibs.zookeeper.com.jibs.zookeeper.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WatchCallBack implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback , AsyncCallback.StatCallback {
    ZooKeeper zk;

    private String threadName;

    CountDownLatch cd = new CountDownLatch(1);

    private String pathName;

    public WatchCallBack(ZooKeeper zk){
        this.zk = zk;
    }

    public void tryLock() {
        try {
            zk.create("/lock",threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this,"abc");
            cd.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unLock(){
        try {
            zk.delete(pathName, -1);
            System.out.println(pathName+" over work...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        //如果第一个哥们，那个锁释放了，其实只有第二个收到了回调事件！
        //如果，不是第一个哥们，某一个，挂了，也能造成他后边的收到这个通知，从而让他后边那个跟去watch挂掉这个哥们前边的。。。
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/", false,this,"sdf");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
        }

    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        if(null != name){
            System.out.println("create node :"+ name);
            pathName = name;
            zk.getChildren("/", false,this,"sdf");
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        if(null != stat){
            //一定能看到自己前边的
            Collections.sort(children);
            int i = children.indexOf(pathName.substring(1));
            if(i == 0){
                System.out.println(threadName+"i am first");
                cd.countDown();
            } else {
                zk.exists("/"+children.get(i-1),this,this,"sdf");
            }

        }
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {

    }
}
