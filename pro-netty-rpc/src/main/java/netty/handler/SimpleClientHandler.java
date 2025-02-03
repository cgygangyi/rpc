package netty.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.client.DefaultFuture;
import netty.client.Response;

public class SimpleClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (msg.equals("ping")) {
            System.out.println("[Client] Received ping from server");
            ctx.writeAndFlush("pong\r\n");
            return;
        }
        System.out.println("[Client] Received message from server: " + msg);
        Response response = JSONObject.parseObject(msg, Response.class);
        DefaultFuture.received(response);

        // ctx.close();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("[Client] Exception caught: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
