package game.socket.socket;

import game.socket.decoder.TestDecoder;
import game.socket.handler.TestHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final TestHandler testHandler;

    // (1)
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // (2)
        ChannelPipeline pipeline = ch.pipeline();

        TestDecoder testDecoder = new TestDecoder();
        // (3)
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8), testDecoder, testHandler);

    }
}
