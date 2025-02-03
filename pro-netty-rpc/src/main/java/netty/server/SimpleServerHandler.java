package netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SimpleServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("[Server] Received message from client: " + msg);
        
        String response = "OK\r\n";
        System.out.println("[Server] Sending response: " + response);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("[Server] Exception caught: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
