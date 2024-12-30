package game.socket.service;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    //private final ClientMappingService clientMappingService;
    private final Map<ChannelHandlerContext, Integer> clientsOrderMap = new ConcurrentHashMap<>();
    private final Map<ChannelHandlerContext, ClientState> clientStates = new ConcurrentHashMap<>();
    private ChannelHandlerContext currentChattingClient;
    private int orderCounter = 0;

    private enum ClientState {
        NORMAL, CHATTING
    }

    // 클라이언트 연결 시 호출
    public void addClient(ChannelHandlerContext ctx) {
        //clientMappingService.addClient(ctx);
        clientsOrderMap.put(ctx, ++orderCounter);
        listClients();
    }

    // 클라이언트 연결 종료 시 호출
    public void removeClient(ChannelHandlerContext ctx) {
        clientsOrderMap.remove(ctx);
    }

    // 연결된 클라이언트 목록 출력
    public void listClients() {
        if (clientsOrderMap.isEmpty()) {
            log.info("현재 연결된 클라이언트가 없습니다.");
            return;
        }

        log.info("연결된 클라이언트 목록: \n");
        for (Map.Entry<ChannelHandlerContext, Integer> entry : clientsOrderMap.entrySet()) {
            ChannelHandlerContext ctx = entry.getKey();
            int port = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();
            int order = entry.getValue();
            log.info("Port: " + port + ", Order: " + order + "\n");
        }
    }

    // 특정 클라이언트에게 메시지 전송
    public void sendRequestToClient(ChannelHandlerContext ctx, String receivedMessage) {
        log.info("sendRequest: {}", receivedMessage);
        int port = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();
        String requestMessage = "What do you want? (Port: " + port + ", Order: " + clientsOrderMap.get(ctx) + ")\n";
        ByteBuf buf = Unpooled.copiedBuffer(requestMessage, CharsetUtil.UTF_8);
        ctx.writeAndFlush(buf);
    }

    // 30초마다 클라이언트의 연결 상태 로깅
    @Scheduled(fixedDelay = 30000)
    public void logClientsStatus() {
        for (Map.Entry<ChannelHandlerContext, Integer> entry : clientsOrderMap.entrySet()) {
            ChannelHandlerContext ctx = entry.getKey();
            int port = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();
            System.out.println("I'm live. Port: " + port + ", Order: " + entry.getValue());
        }
    }

    // 10초마다 모든 클라이언트에게 현재 시각과 포트 뿌리기
    @Scheduled(fixedRate = 10000)
    public void sendTimeToClients() {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
        for (ChannelHandlerContext ctx : clientsOrderMap.keySet()) {
            int port = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();
            String message = "Current Time: " + currentTime + ", Your Port: " + port + "\n";
            ByteBuf buf = Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);
            ctx.writeAndFlush(buf);
        }
    }
}
