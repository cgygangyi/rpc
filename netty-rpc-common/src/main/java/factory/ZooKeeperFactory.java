package factory;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class ZooKeeperFactory {
    private static CuratorFramework client;
    private static final String ZK_SERVER = "localhost:2181";

    public static CuratorFramework getClient() {
        if (client == null) {
            synchronized (ZooKeeperFactory.class) {
                if (client == null) {
                    // Create retry policy with exponential backoff
                    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
                    
                    // Create and start ZooKeeper client
                    client = CuratorFrameworkFactory.newClient(ZK_SERVER, retryPolicy);
                    client.start();
                    System.out.println("[ZooKeeper] Client started successfully");
                }
            }
        }
        return client;
    }

    public static void closeClient() {
        if (client != null) {
            client.close();
            client = null;
            System.out.println("[ZooKeeper] Client closed");
        }
    }
} 