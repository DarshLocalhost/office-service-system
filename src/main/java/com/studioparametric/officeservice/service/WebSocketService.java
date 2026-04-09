package com.studioparametric.officeservice.service;

import com.studioparametric.officeservice.entity.Request;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendRequestUpdate(Request request) {
        log.info("Sending WebSocket update for request #{} to /topic/requests", request.getId());
        messagingTemplate.convertAndSend("/topic/requests", request);
    }

    public void notifyNewRequest(Request request) {
        log.info("Sending new request notification for request #{} to /topic/requests", request.getId());
        messagingTemplate.convertAndSend("/topic/requests", request);
    }
}
