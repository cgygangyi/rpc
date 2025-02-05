package netty.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;


public class MessageUtil {
    private static final String TYPE = "type";
    private static final String DATA = "data";
    private static final String HEARTBEAT = "heartbeat";
    private static final String PING = "ping";
    private static final String PONG = "pong";
    
    public static void sendHeartbeat(ChannelHandlerContext ctx, String action) {
        JSONObject message = new JSONObject();
        message.put(TYPE, HEARTBEAT);
        message.put(DATA, action);
        send(ctx, message);
    }

    public static void sendMessage(ChannelHandlerContext ctx, Object data) {
        JSONObject message = new JSONObject();
        message.put(TYPE, "message");
        message.put(DATA, data);
        send(ctx, message);
    }

    public static void handleMessage(ChannelHandlerContext ctx, String messageJson) {
        try {
            JSONObject message = JSON.parseObject(messageJson);
            String type = message.getString(TYPE);

            if (HEARTBEAT.equals(type)) {
                handleHeartbeat(ctx, message.getString(DATA));
            } else {
                handleBusinessMessage(ctx, message.get(DATA));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleHeartbeat(ChannelHandlerContext ctx, String action) {
        if (PING.equals(action)) {
            sendHeartbeat(ctx, PONG);
        }
    }
    
    private static void handleBusinessMessage(ChannelHandlerContext ctx, Object data) {
    }
    
    private static void send(ChannelHandlerContext ctx, JSONObject message) {
        ctx.writeAndFlush(message.toJSONString() + "\r\n");
    }
} 