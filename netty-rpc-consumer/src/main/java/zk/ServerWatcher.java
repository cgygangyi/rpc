package zk;

import core.ChannelManager;
import core.NettyClient;
import core.TcpClient;
import factory.ZooKeeperFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import io.netty.channel.ChannelFuture;
import constant.Constants;

import java.util.List;

public class ServerWatcher implements CuratorWatcher {

    public void processServerList(List<String> serverPaths) {
        System.out.println("Processing server list: " + serverPaths);
        
        ChannelManager.realServerPath.clear();
        for(String serverInfo : serverPaths){
            ChannelManager.realServerPath.add(serverInfo);
        }

        ChannelManager.clearChnannel();
        for(String realServer : ChannelManager.realServerPath){
            String[] str = realServer.split("#");
            if (str.length == 2) {
                System.out.println("Connecting to server: " + str[0] + ":" + str[1]);
                ChannelFuture channelFuture = NettyClient.b.connect(str[0], Integer.valueOf(str[1]));
                ChannelManager.addChnannel(channelFuture);
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