package com.example.mybatplusdemo.entity.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperApp {
    public static void main(String[] args) throws Exception {
//        connectZookeeper();
//        connectByCurator();
        startInterProcessMutex();
    }

    private static void startInterProcessMutex() {
        CuratorFramework zkClient = getZkClient();
        String lockPath = "/lock";
        InterProcessMutex lock = new InterProcessMutex(zkClient, lockPath);
        //模拟50个线程抢锁
        for (int i = 0; i < 50; i++) {
            new Thread(new TestThread(i, lock)).start();
        }
    }

    private static void connectByCurator() throws Exception {
        CuratorFramework curatorFramework= CuratorFrameworkFactory.
                builder().connectString("192.168.10.10:2181," +
                        "192.168.10.10:2181,192.168.10.10:2181").
                sessionTimeoutMs(4000).retryPolicy(new
                        ExponentialBackoffRetry(1000,3)).
                namespace("").build();
        curatorFramework.start();
        Stat stat=new Stat();
        //查询节点数据
        byte[] bytes = curatorFramework.getData().storingStatIn(stat).forPath("/runoob");
        System.out.println(new String(bytes));
        curatorFramework.close();
    }

    private static void connectZookeeper() {
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            ZooKeeper zooKeeper =
                    new ZooKeeper("192.168.10.10:2181," +
                            "192.168.10.10:2181,192.168.10.10:2181",
                            4000, new Watcher() {
                        @Override
                        public void process(WatchedEvent event) {
                            if (Event.KeeperState.SyncConnected == event.getState()) {
                                //如果收到了服务端的响应事件，连接成功
                                countDownLatch.countDown();
                            }
                        }
                    });
            countDownLatch.await();
            //CONNECTED
            System.out.println("zookeeper状态：" + zooKeeper.getState());
            zooKeeper.create("/runoob","0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        }
    }

    static class TestThread implements Runnable {
        private Integer threadFlag;
        private InterProcessMutex lock;

        public TestThread(Integer threadFlag, InterProcessMutex lock) {
            this.threadFlag = threadFlag;
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                lock.acquire();
                System.out.println("第"+threadFlag+"线程获取到了锁");
                //等到1秒后释放锁
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static CuratorFramework getZkClient() {
        String zkServerAddress = "192.168.10.10:2181";
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3, 5000);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString(zkServerAddress)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        return zkClient;
    }
}