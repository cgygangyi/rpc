package netty.handler;


import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import netty.utils.Response;
import netty.client.TcpClient;
import netty.handler.param.ServerRequest;

public class SimpleServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("[Server] Received message from client: " + msg);
//        ctx.writeAndFlush("OK\r\n");
//        System.out.println("[Server] Send response to client");

        ServerRequest request = JSONObject.parseObject(msg.toString(), ServerRequest.class);

        Response response = new Response();
        response.setId(request.getId());
        response.setContent("OK");

        ctx.writeAndFlush(JSONObject.toJSONString(response));
        ctx.writeAndFlush("\r\n");
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
                TcpClient.send("ping");
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
