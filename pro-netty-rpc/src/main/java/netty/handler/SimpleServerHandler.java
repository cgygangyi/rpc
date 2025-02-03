package netty.handler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class SimpleServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("[Server] Received message from client: " + msg);

        String response = "OK\r\n";
        System.out.println("[Server] Sending response: " + response);
        ctx.writeAndFlush(response);
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
                ctx.writeAndFlush("ping\r\n");
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
