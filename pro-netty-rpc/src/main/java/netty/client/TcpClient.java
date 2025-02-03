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
import netty.handler.SimpleClientHandler;

public class TcpClient {
    static final Bootstrap b = new Bootstrap();
    static ChannelFuture f;

    static {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        b.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true)
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
            ChannelFuture f = b.connect("localhost", 8080).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object send(ClientRequest request) {
        DefaultFuture future = new DefaultFuture(request);
        return f.channel().attr(AttributeKey.valueOf("msg")).get();
    }
}