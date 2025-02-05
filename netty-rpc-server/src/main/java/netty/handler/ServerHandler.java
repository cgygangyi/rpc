package netty.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.utils.Response;
import netty.handler.param.ServerRequest;
import netty.medium.Media;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Executor exec = Executors.newFixedThreadPool(10);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("[Server Handler] Received message: " + msg.toString());
        
        exec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerRequest serverRequest = JSONObject.parseObject(msg.toString(), ServerRequest.class);
                    
                    // Process request
                    Media medium = Media.getInstance();
                    Response response = medium.process(serverRequest);
                    
                    // Send response
                    String responseJson = JSONObject.toJSONString(response);
                    System.out.println("[Server Handler] Sending response: " + responseJson);
                    ctx.writeAndFlush(responseJson);
                    ctx.writeAndFlush("\r\n");
                    
                } catch (Exception e) {
                    System.err.println("[Server Handler] Error processing request: " + e.getMessage());
                    e.printStackTrace();
                    
                    // Send error response
                    Response errorResponse = new Response();
                    errorResponse.setContent("Error: " + e.getMessage());
                    ctx.writeAndFlush(JSONObject.toJSONString(errorResponse));
                    ctx.writeAndFlush("\r\n");
                }
            }
        });
    }
}