package com.example.SevMerge.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 구독 요청이 들어오는 경로 설정
        // 구독이란클라이언트가 서버에 연결된 상태에서 특정 주제(Topic)나
        // 이벤트의 데이터를 지속적으로 수신하겠다고 등록하는 과정
        registry.enableSimpleBroker("/topic");

        // 클라이언트가 메시지를 보낼 때 사용할 경로 접두사 설정
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // "/ws/chat" 클라이언트가 WebSocket 최초 연결할 때 접속 주소
        registry.addEndpoint("/ws/chat")
                // WebSocket Handshake 시 기존 HttpSession을 WebSocket 세션으로 전달
                // 이걸 통해 WebSocket에서도 "sessionUser" 꺼낼 수 있음
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                // chat.js의 new SockJS("/ws/chat") 와 연결
                .withSockJS();
    }
}
