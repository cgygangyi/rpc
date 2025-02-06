package factory;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZooKeeperFactory {

    private static CuratorFramework client;

    public static CuratorFramework getClient() {
        if (client == null) {
            synchronized (ZooKeeperFactory.class) {
                if (client == null) {
                    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
                    client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
                    client.start();
                    System.out.println("ZooKeeper client started successfully.");
                }
            }
        }
        return client;
    }

    public static void createNode(String path, String data) {
        try {
            CuratorFramework client = getClient();

            if (client.checkExists().forPath(path) == null) {
                client.create().forPath(path, data.getBytes());
                System.out.println("Node created at path: " + path + ", data: " + data);
            } else {
                System.out.println("Node already exists at path: " + path);
            }
        } catch (Exception e) {
            System.err.println("Error while creating node: " + e.getMessage());
        }
    }

    public static void closeClient() {
        if (client != null) {
            client.close();
            System.out.println("ZooKeeper client closed.");
        }
    }

    public static void main(String[] args) {
        String nodePath = "/netty1";
        String nodeData = "balabala";
        try {
            createNode(nodePath, nodeData);
        } finally {
            closeClient();
        }
    }
}
