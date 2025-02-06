package handler;

import core.DefaultFuture;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import utils.Response;


public class SimpleClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("[Client Handler] Received raw message: '" + msg + "'");
        
        if (msg.equals("ping")) {
            System.out.println("[Client Handler] Received ping from server");
            ctx.writeAndFlush("pong\r\n");
            return;
        }
        
        try {
            Response response = JSONObject.parseObject(msg, Response.class);
            System.out.println("[Client Handler] Parsed response with ID: " + response.getId());
            DefaultFuture.received(response);
        } catch (Exception e) {
            System.err.println("[Client Handler] Error parsing response: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("[Client] Exception caught: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
