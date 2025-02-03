package netty.client;

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
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.logging.LogLevel;

public class NettyClient {
    public static void main(String[] args) throws Exception {
        System.out.println("[Client] Starting client initialization...");
        String host = "localhost";
        int port = 8080;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            System.out.println("[Client] Configuring client bootstrap...");

            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("[Client] Initializing channel pipeline...");
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new SimpleClientHandler());
                        }
                    });

            System.out.println("[Client] Connecting to server at " + host + ":" + port);
            ChannelFuture f = b.connect(host, port).sync();

            System.out.println("[Client] Connected to server, sending message...");
            f.channel().writeAndFlush("Hello server\r\n");
            System.out.println("[Client] Message sent, waiting for response...");

            f.channel().closeFuture().sync();
        } catch (Exception e) {
            System.err.println("[Client] Error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("[Client] Shutting down client...");
            workerGroup.shutdownGracefully();
        }
    }
}