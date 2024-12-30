package game.socket.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestDecoder extends ByteToMessageDecoder {
    private final int DATA_LENGTH = 2048;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("readableBytes: {}" , in.readableBytes());
        if (in.readableBytes() < DATA_LENGTH) {
            return;
        }

        // (1)
        ByteBuf buffer = in.readBytes(DATA_LENGTH);
        String message = buffer.toString(StandardCharsets.UTF_8);
        out.add(message); // String 객체를 out에 추가
        buffer.release(); // (2)
    }
}
