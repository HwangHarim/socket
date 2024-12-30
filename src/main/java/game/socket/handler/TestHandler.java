package game.socket.handler;

import game.socket.service.ChatService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable // (1)
public class TestHandler extends ChannelInboundHandlerAdapter {

    private final ChatService chatService;

    public TestHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    // 클라이언트가 서버에 연결 될 때 호출된다.
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        chatService.addClient(ctx); // (2)
        log.info("Client connected: " + ctx.channel().remoteAddress());
    }

    // 클라이언트가 서버에서 연결이 종료될 때 호출이 됩니다.
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        chatService.removeClient(ctx); // (3)
        log.info("Client disconnected: " + ctx.channel().remoteAddress());
    }

    // 클라이언트로부터 메세지를 읽을 때 호출이 된다.
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // (4)
        log.info("channelRead called");
        try {
            String receivedMessage = (String) msg;
            chatService.sendRequestToClient(ctx, receivedMessage);
            log.info("received message: {}", receivedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 채널에서 예외가 발생할 때 호출이된다.
    // 클라이언트와의 연결을 닫아버리고 예외정보를 출력합니다.
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}