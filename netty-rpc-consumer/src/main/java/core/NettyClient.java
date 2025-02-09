package core;

import com.alibaba.fastjson.JSONObject;
import constant.Constants;
import factory.ZooKeeperFactory;
import handler.SimpleClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.curator.framework.CuratorFramework;
import protocal.Response;
import zk.ServerWatcher;

import java.util.List;

public class NettyClient {
    public static final Bootstrap b = new Bootstrap();
    private static ChannelFuture f;
    
    static {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        b.group(workerGroup)
         .channel(NioSocketChannel.class)
         .option(ChannelOption.SO_KEEPALIVE, true)
         .handler(new ChannelInitializer<SocketChannel>() {
             @Override
             public void initChannel(SocketChannel ch) throws Exception {
                 ch.pipeline().addLast(new StringEncoder());
                 ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()));
                 ch.pipeline().addLast(new StringDecoder());
                 ch.pipeline().addLast(new SimpleClientHandler());
             }
         });

        try {
            CuratorFramework client = ZooKeeperFactory.getClient();
            ServerWatcher watcher = new ServerWatcher();
            
            List<String> servers = client.getChildren().forPath(Constants.SERVER_PATH);
            if (!servers.isEmpty()) {
                watcher.processServerList(servers);
            }
            
            client.getChildren().usingWatcher(watcher).forPath(Constants.SERVER_PATH);
            System.out.println("[NettyClient] ZooKeeper watcher registered");
        } catch (Exception e) {
            System.err.println("[NettyClient] Failed to register ZooKeeper watcher: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static Response send(ClientRequest request) {
        ChannelFuture channelFuture = ChannelManager.get();
        if (channelFuture == null) {
            throw new RuntimeException("No available server connection");
        }

        DefaultFuture future = new DefaultFuture(request);

        try {
            String message = JSONObject.toJSONString(request) + "\r\n";
            channelFuture.channel().writeAndFlush(message);
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send request", e);
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            ClientRequest clientRequest = new ClientRequest("Hello Server!");
            Response response = NettyClient.send(clientRequest);
            System.out.println("Got response: " + response.getContent());
        }

    }
}