package netty.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.client.Response;
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
                    // Parse outer request
                    JSONObject outerRequest = JSONObject.parseObject(msg.toString());
                    if (outerRequest == null || outerRequest.getJSONObject("content") == null) {
                        throw new RuntimeException("Invalid request format");
                    }
                    
                    // Get actual request content from the content field
                    JSONObject innerContent = outerRequest.getJSONObject("content");
                    
                    // Build ServerRequest object
                    ServerRequest serverRequest = new ServerRequest();
                    serverRequest.setId(outerRequest.getLong("id"));
                    serverRequest.setCommand(innerContent.getString("command"));
                    
                    // Special handling for content - use directly if simple type
                    Object content = innerContent.get("content");
                    if (content instanceof JSONObject) {
                        serverRequest.setContent(content.toString());
                    } else {
                        serverRequest.setContent(content);
                    }
                    
                    System.out.println("[Server Handler] Parsed request: command=" + serverRequest.getCommand() 
                            + ", content=" + serverRequest.getContent());
                    
                    // Process request
                    Media medium = Media.getInstance();
                    Object result = medium.process(serverRequest);
                    
                    // Build response
                    Response response = new Response();
                    response.setId(serverRequest.getId());
                    response.setResult(result != null ? result.toString() : "OK");
                    
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
                    errorResponse.setResult("Error: " + e.getMessage());
                    ctx.writeAndFlush(JSONObject.toJSONString(errorResponse));
                    ctx.writeAndFlush("\r\n");
                }
            }
        });
    }
}