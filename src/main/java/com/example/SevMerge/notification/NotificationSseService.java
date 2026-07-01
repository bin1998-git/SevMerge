package com.example.SevMerge.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSseService {

    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public SseEmitter createConnection(Long memberId) {

        SseEmitter old = emitterMap.remove(memberId);
        if (old != null) {
            try {
                old.complete();
            } catch (Exception e) {
                // 빠른 페이지 이동 시 이전 emitter의 async context가 이미 종료된 경우
                // IllegalStateException 등이 발생할 수 있으나 무시해도 안전함
                log.debug("이전 SSE emitter 종료 중 무시 가능한 예외 발생 (memberId={}): {}", memberId, e.getMessage());
            }
        }

        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        emitterMap.put(memberId, emitter);

        emitter.onCompletion(() -> emitterMap.remove(memberId, emitter));
        emitter.onTimeout(() -> emitterMap.remove(memberId, emitter));
        emitter.onError((e) -> emitterMap.remove(memberId, emitter));

        try {
            emitter.send(SseEmitter.event().name("connect").data("connected!"));
        } catch (IOException e) {
            emitterMap.remove(memberId);
        }
        return emitter;
    }

    public void sendNotification(Long receiverId, NotificationResponse.ListDTO resDTO) {
        SseEmitter emitter = emitterMap.get(receiverId);
        if (emitter == null) return;

        try {
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(resDTO, MediaType.APPLICATION_JSON)
            );
        } catch (IOException e) {
            emitterMap.remove(receiverId, emitter);
        }
    }

    // 앱 종료 시작 시점(graceful shutdown 대기 전)에 열린 SSE를 모두 닫아 즉시 종료되도록 함
    @EventListener(ContextClosedEvent.class)
    public void closeAll() {
        emitterMap.values().forEach(SseEmitter::complete);
        emitterMap.clear();
    }

}
