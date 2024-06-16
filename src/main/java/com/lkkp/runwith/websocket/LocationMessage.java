package com.lkkp.runwith.websocket;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LocationMessage {
    private Long userId;
    private Float latitude;
    private Float longitude;
    private Float altitude;
    private LocalDateTime timestamp;
    private Long recordId;  // Match ID 추가

}