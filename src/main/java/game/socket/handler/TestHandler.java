package game.socket.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
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
        // msg가 ByteBuf인지 확인합니다.
        if (msg instanceof ByteBuf) {
            ByteBuf mBuf = (ByteBuf) msg;

            try {
                // 데이터를 공유 버퍼(buff)에 축적
                buff.writeBytes(mBuf);

                // 클라이언트로 데이터를 전송하고 채널을 닫습니다.
                final ChannelFuture f = ctx.writeAndFlush(buff.copy());
                f.addListener(ChannelFutureListener.CLOSE);
            } finally {
                // ByteBuf의 리소스를 해제합니다.
                mBuf.release();
            }
        } else {
            // msg가 ByteBuf가 아닌 경우, 파이프라인을 통해 다음 핸들러로 메시지를 전달
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        ctx.close();
        cause.printStackTrace();
    }
}