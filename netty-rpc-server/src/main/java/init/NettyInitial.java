package init;

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
import handler.ServerHandler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

@Component
public class NettyInitial implements ApplicationListener<ContextRefreshedEvent> {
    
    private static final int MIN_PORT = 8080;
    private static final int MAX_PORT = 8090; // 设置最大尝试端口

    private int findAvailablePort() {
        for (int port = MIN_PORT; port <= MAX_PORT; port++) {
            try {
                // 尝试绑定端口
                new ServerSocket(port).close();
                System.out.println("Found available port: " + port);
                return port;
            } catch (IOException e) {
                System.out.println("Port " + port + " is in use, trying next port");
                continue;
            }
        }
        throw new RuntimeException("No available port found between " + MIN_PORT + " and " + MAX_PORT);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        EventLoopGroup parentLoop = new NioEventLoopGroup();
        EventLoopGroup childLoop = new NioEventLoopGroup();

        try {
            int port = findAvailablePort();
            
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
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    });

            System.out.println("[Server] Server is starting on port " + port);
            ChannelFuture f = bootstrap.bind(port).sync();

            try {
                CuratorFramework client = ZooKeeperFactory.getClient();
                String serverPath = Constants.SERVER_PATH + "/" + 
                    InetAddress.getLocalHost().getHostAddress() + "#" + 
                    port + "#" + 
                    getServerWeight();
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(serverPath);
                System.out.println("[Server] Registered with ZooKeeper at path: " + serverPath);
            } catch (Exception zkEx) {
                System.err.println("[Server] ZooKeeper registration failed: " + zkEx.getMessage());
            }

            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            parentLoop.shutdownGracefully();
            childLoop.shutdownGracefully();
        }
    }

    private int getServerWeight() {
        // get the number of cores as the weight
        return Runtime.getRuntime().availableProcessors();
    }
}
