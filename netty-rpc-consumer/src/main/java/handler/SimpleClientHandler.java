package handler;

import core.ClientRequest;
import core.DefaultFuture;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protocal.Request;
import protocal.Response;


public class SimpleClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        String message = msg.toString();
        System.out.println("[Server Handler] Received message: " + message);

        try {
            Response response = JSONObject.parseObject(msg, Response.class);
            if (response.getContent().equals("ping")) {
                System.out.println("[Client Handler] Received ping from server");
                Response pongResponse = new Response();
                pongResponse.setId(0L);
                pongResponse.setContent("pong");
                ctx.writeAndFlush(JSONObject.toJSONString(pongResponse) + "\r\n");
            }
            else {
                DefaultFuture.received(response);
            }
        } catch (Exception e) {
            System.err.println("[Server Handler] Failed to parse message: " + e.getMessage());
            ctx.writeAndFlush("Error: Invalid message format\r\n");
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("[Client] Exception caught: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
