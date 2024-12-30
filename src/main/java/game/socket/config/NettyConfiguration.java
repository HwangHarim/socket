package game.socket.config;

import game.socket.socket.NettyChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
@RequiredArgsConstructor
public class NettyConfiguration {


    @Value("${netty.host}")
    private String host;

    @Value("${netty.tcp-port}")
    private int port;

    @Value("${netty.boss-count}")
    private int bossCount;

    @Value("${netty.worker-count}")
    private int workerCount;

    @Value("${netty.keep-alive}")
    private int keepAlive;

    @Value("${netty.backlog}")
    private int backlog;


    @Bean
    public ServerBootstrap serverBootstrap(NettyChannelInitializer nettyChannelInitializer) {
        // ServerBootstrap : 서버 설정을 도와주는 class
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup(), workerGroup()) // (1)
                .channel(NioServerSocketChannel.class)    // (2)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(nettyChannelInitializer);   // (3)
        serverBootstrap.option(ChannelOption.SO_BACKLOG, backlog);

        return serverBootstrap;
    }

    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(bossCount); // (4)
    }

    @Bean(destroyMethod = "shutdownGracefully") // (5)
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(workerCount); // (6)
    }

    @Bean
    public InetSocketAddress tcpPort() {
        // (7)
        return new InetSocketAddress(host, port); // 적절한 포트 번호와 호스트 정보로 변경해주세요.
    }
}
