package com.lkkp.runwith.config.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;

    public WebSocketConfig(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // WebSocket 엔드포인트와 핸들러를 등록합니다.
        registry.addHandler(webSocketHandler, "/socket")
                .setAllowedOrigins("*") // CORS 설정, 모든 origin 허용
                .addInterceptors(new HttpSessionHandshakeInterceptor()); // 세션 핸드셰이크 추가
    }

    // ServerEndpointExporter는 Spring Boot에서 WebSocket 서버를 자동으로 등록할 수 있도록 합니다.
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
