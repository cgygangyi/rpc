package netty.server;

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

public class NettyServer {
    public static void main(String[] args) {
        EventLoopGroup parentLoop = new NioEventLoopGroup();
        EventLoopGroup childLoop = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentLoop, childLoop);
            bootstrap.option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, false)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new SimpleServerHandler());
                            ch.pipeline().addLast(new StringEncoder());
                        }
                    });
            System.out.println("Server is starting...");
            ChannelFuture f = bootstrap.bind(8080).sync();
            System.out.println("Server started and listening on port 8080");
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            parentLoop.shutdownGracefully();
            childLoop.shutdownGracefully();
        }
    }
}
