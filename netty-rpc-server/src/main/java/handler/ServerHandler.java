package handler;

import com.alibaba.fastjson.JSONObject;
import handler.param.ServerRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import medium.Media;
import protocal.Request;
import protocal.Response;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Executor exec = Executors.newFixedThreadPool(10);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = msg.toString();
        System.out.println("[Server Handler] Received message: " + message);

        try {
            Request request = JSONObject.parseObject(message, Request.class);

            // check if the request has command
            if (request.getCommand() == null) {
                // if no command, check the content
                String content = String.valueOf(request.getContent());
                if ("ping".equals(content)) {
                    handlePing(ctx, request.getId());
                }
                else {
                    handleTestMessage(ctx, request.getId());
                }
            }
            else {
                handleRpcRequest(ctx, request);
            }

        } catch (Exception e) {
            System.err.println("[Server Handler] Failed to parse message: " + e.getMessage());
            ctx.writeAndFlush("Error: Invalid message format\r\n");
        }
    }

    private void handlePing(ChannelHandlerContext ctx, long requestId) {
        Response response = new Response();
        response.setId(requestId);
        response.setContent("pong");
        sendResponse(ctx, response);
    }

    private void handleTestMessage(ChannelHandlerContext ctx, long requestId) {
        Response response = new Response();
        response.setId(requestId);
        response.setContent("OK");
        sendResponse(ctx, response);
    }

    private void handleRpcRequest(ChannelHandlerContext ctx, Request request) {
        exec.execute(() -> {
            try {
                Media medium = Media.getInstance();
                Response response = medium.process(request);
                sendResponse(ctx, response);
            } catch (Exception e) {
                System.err.println("[Server Handler] Error processing RPC request: " + e.getMessage());
                Response errorResponse = new Response();
                errorResponse.setId(request.getId());
                errorResponse.setContent("Error: " + e.getMessage());
                sendResponse(ctx, errorResponse);
            }
        });
    }

    private void sendResponse(ChannelHandlerContext ctx, Response response) {
        String responseJson = JSONObject.toJSONString(response);
        System.out.println("[Server Handler] Sending response: " + responseJson);
        ctx.writeAndFlush(responseJson + "\r\n");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("[Server] Read idle, closing connection");
                ctx.close();
            }
            else if (event.state() == IdleState.WRITER_IDLE) {
                System.out.println("[Server] Write idle, closing connection");
                ctx.close();
            }
            else if (event.state() == IdleState.ALL_IDLE) {
                System.out.println("[Server] All idle, sending ping");
                Response response = new Response();
                response.setId(0L);
                response.setContent("ping");
                sendResponse(ctx, response);
            }
        }
        super.userEventTriggered(ctx, evt);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("[Server] Exception caught: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}