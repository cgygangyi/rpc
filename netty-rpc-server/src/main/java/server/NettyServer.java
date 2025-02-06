package server;

import handler.SimpleServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import constant.Constants;
import factory.ZooKeeperFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class NettyServer {
    public static void main(String[] args) {
        EventLoopGroup parentLoop = new NioEventLoopGroup();
        EventLoopGroup childLoop = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentLoop, childLoop)
                     .option(ChannelOption.SO_BACKLOG, 128)
                     .childOption(ChannelOption.SO_KEEPALIVE, false)
                     .channel(NioServerSocketChannel.class)
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         public void initChannel(SocketChannel ch) throws Exception {
                             ch.pipeline().addLast(new StringDecoder());
                             ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()));
                             ch.pipeline().addLast(new StringEncoder());
                             ch.pipeline().addLast(new IdleStateHandler(60, 45, 20, TimeUnit.SECONDS));
                             ch.pipeline().addLast(new SimpleServerHandler());
                         }
                     });

            System.out.println("[Server] Server is starting...");
            ChannelFuture f = bootstrap.bind(8080).sync();

            try {
                CuratorFramework client = ZooKeeperFactory.getClient();
                String path = Constants.SERVER_PATH + InetAddress.getLocalHost().getHostAddress();
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(path);
                System.out.println("[Server] Registered with ZooKeeper at path: " + path);
            } catch (Exception zkEx) {
                System.err.println("[Server] ZooKeeper registration failed: " + zkEx.getMessage());
            }

            System.out.println("[Server] Server started and listening on port 8080");
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            System.err.println("[Server] Exception occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("[Server] Shutting down server...");
            parentLoop.shutdownGracefully();
            childLoop.shutdownGracefully();
        }
    }
}
