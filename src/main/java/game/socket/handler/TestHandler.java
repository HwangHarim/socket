package game.socket.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable // (1)
@RequiredArgsConstructor
public class TestHandler extends ChannelInboundHandlerAdapter {

    private int DATA_LENGTH = 2048;
    private ByteBuf buff;

    // 핸들러가 생성될 때 호출되는 메소드
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        buff = ctx.alloc().buffer(DATA_LENGTH);
    }

    // 핸들러가 제거될 때 호출되는 메소드
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        buff = null;
    }

    // 클라이언트와 연결되어 트래픽을 생성할 준비가 되었을 때 호출되는 메소드
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String remoteAddress = ctx.channel().remoteAddress().toString();
        log.info("Remote Address: " + remoteAddress);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof String) {
            // byte[]를 ByteBuf로 변환
            byte[] byteArray = ((String) msg).getBytes();
            ByteBuf byteBuf = Unpooled.wrappedBuffer(byteArray);

            // 변환된 ByteBuf 처리
            System.out.println("Received ByteBuf: " + byteBuf.toString(io.netty.util.CharsetUtil.UTF_8));

            // 필요에 따라 ByteBuf를 release해야 함
            byteBuf.release();
        } else {
            // 다른 타입의 메시지 처리
            System.err.println("Unsupported message type: " + msg.getClass().getName());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        ctx.close();
        cause.printStackTrace();
    }
}