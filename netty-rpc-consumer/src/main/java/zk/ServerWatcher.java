package zk;

import core.ChannelManager;
import core.NettyClient;
import factory.ZooKeeperFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import io.netty.channel.ChannelFuture;

import java.util.List;

public class ServerWatcher implements CuratorWatcher {

    public void processServerList(List<String> serverPaths) {
        System.out.println("Processing server list: " + serverPaths);
        
        ChannelManager.clear();
        
        for(String serverInfo : serverPaths) {
            String[] parts = serverInfo.split("#");
            if (parts.length == 3) {
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);
                ChannelFuture channelFuture = NettyClient.b.connect(host, port);
                ChannelManager.addServer(serverInfo, channelFuture);
            }
        }
    }

    public void process(WatchedEvent event) throws Exception {
        System.out.println("process------------------------");
        CuratorFramework client = ZooKeeperFactory.getClient();
        String path = event.getPath();
        client.getChildren().usingWatcher(this).forPath(path);
        List<String> newServerPaths = client.getChildren().forPath(path);
        processServerList(newServerPaths); 
    }
}