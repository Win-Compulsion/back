package com.lkkp.runwith.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, ClientData> clientDataMap = new ConcurrentHashMap<>();
    private final double targetDistance = 5.0;  // 예시 목표 거리 (킬로미터)

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        clientDataMap.put(session.getId(), new ClientData(0, 0.0, false));  // 시작 시 승패는 false
        log.info("New WebSocket connection established: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        log.info("Received message: " + payload);

        // JSON 데이터 파싱 (경과 시간 및 이동 거리)
        Map<String, Object> receivedData = parseJson(payload);
        int elapsedTime = (int) receivedData.get("elapsed_time");
        double distanceCovered = Double.parseDouble((String) receivedData.get("distance_covered"));

        // 클라이언트 데이터 업데이트
        ClientData clientData = clientDataMap.get(session.getId());
        clientData.setElapsedTime(elapsedTime);
        clientData.setDistanceCovered(distanceCovered);

        // 목표 거리를 완주했는지 확인
        if (!clientData.isFinished() && clientData.getDistanceCovered() >= targetDistance) {
            clientData.setFinished(true);
            sendRaceResult(session.getId(), "You Win!");
        }

        // 상대방에게 승패 정보를 포함한 데이터 전송
        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen() && !s.getId().equals(session.getId())) {
                ClientData opponentData = clientDataMap.get(session.getId());
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("opponent_elapsed_time", opponentData.getElapsedTime());
                responseData.put("opponent_distance", opponentData.getDistanceCovered());
                responseData.put("opponent_finished", opponentData.isFinished());
                s.sendMessage(new TextMessage(toJson(responseData)));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        clientDataMap.remove(session.getId());
        log.info("WebSocket connection closed: " + session.getId());
    }

    private Map<String, Object> parseJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON format: " + json, e);
        }
    }

    private String toJson(Map<String, Object> data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException("Error converting to JSON: " + data, e);
        }
    }

    private void sendRaceResult(String winnerSessionId, String resultMessage) {
        // 승리 결과를 상대방에게 전송
        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen() && !s.getId().equals(winnerSessionId)) {
                Map<String, Object> resultData = new HashMap<>();
                resultData.put("race_result", resultMessage);
                try {
                    s.sendMessage(new TextMessage(toJson(resultData)));
                } catch (IOException e) {
                    log.error("Error sending race result to opponent", e);
                }
            }
        }
    }

    private static class ClientData {
        private int elapsedTime;
        private double distanceCovered;
        private boolean isFinished;

        public ClientData(int elapsedTime, double distanceCovered, boolean isFinished) {
            this.elapsedTime = elapsedTime;
            this.distanceCovered = distanceCovered;
            this.isFinished = isFinished;
        }

        public int getElapsedTime() {
            return elapsedTime;
        }

        public void setElapsedTime(int elapsedTime) {
            this.elapsedTime = elapsedTime;
        }

        public double getDistanceCovered() {
            return distanceCovered;
        }

        public void setDistanceCovered(double distanceCovered) {
            this.distanceCovered = distanceCovered;
        }

        public boolean isFinished() {
            return isFinished;
        }

        public void setFinished(boolean finished) {
            isFinished = finished;
        }
    }
}
