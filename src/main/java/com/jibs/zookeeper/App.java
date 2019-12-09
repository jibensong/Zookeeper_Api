package com.jibs.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {

        System.out.println( "Hello World!" );
        //zk是有session概念的,没有连接池的概念
        //watch 观察，回调
        //watch的注册值发生在读类型调用.get exites...
        //第一类:new zk时候，传入的watch,这个watch,session级别的，跟path,node没有关系
        final CountDownLatch cd = new CountDownLatch(1);
        final ZooKeeper zk = new ZooKeeper("192.168.150.11:2181,192.168.150.12:2181", 3000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                Event.KeeperState state = watchedEvent.getState();
                Event.EventType type = watchedEvent.getType();
                String path = watchedEvent.getPath();
                System.out.println(watchedEvent.toString());
                switch (state) {
                    case Unknown:
                        break;
                    case Disconnected:
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
                        System.out.println("connected");
                        cd.countDown();
                        break;
                    case AuthFailed:
                        break;
                    case ConnectedReadOnly:
                        break;
                    case SaslAuthenticated:
                        break;
                    case Expired:
                        break;
                }

                switch (type) {
                    case None:
                        break;
                    case NodeCreated:
                        break;
                    case NodeDeleted:
                        break;
                    case NodeDataChanged:
                        break;
                    case NodeChildrenChanged:
                        break;
                }

            }
        });
        cd.await();
        ZooKeeper.States state = zk.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("ing....");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("ed....");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }
        String pathName = zk.create("/xxoo", "olddata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        final Stat stat = new Stat();
        byte[] node = zk.getData("/xxoo", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("getData" + watchedEvent.toString());
                //ture default Watch 被重新注册 new zk的那个Watch
               // zk.getData("/xxoo",true,stat);
                try {
                    zk.getData("/xxoo",this,stat);
                } catch (KeeperException ex ){
                    ex.printStackTrace();
                } catch (InterruptedException ex){
                    ex.printStackTrace();
                }

            }
        }, stat);
        System.out.println(new String(node));
        Stat stat1 = zk.setData("/xxoo", "newdata".getBytes(), 0);
        Stat stat2 = zk.setData("/xxoo", "newdata01".getBytes(), stat1.getVersion());
        zk.getData("/xxoo", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int i, String s, Object ctx, byte[] data, Stat stat) {
                System.out.println("-----------async call back-----------------------");
                System.out.println(new String(data));
                System.out.println(ctx.toString());
            }
        }, "abc");
        System.out.println("-----------async over-----------------------");
    }
}
