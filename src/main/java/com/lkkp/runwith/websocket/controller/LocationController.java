package com.lkkp.runwith.websocket.controller;

import com.lkkp.runwith.location.Location;
import com.lkkp.runwith.location.repository.LocationRepository;
import com.lkkp.runwith.record.Record;
import com.lkkp.runwith.record.repository.RecordRepository;
import com.lkkp.runwith.websocket.LocationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class LocationController {
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RecordRepository recordRepository;

    @MessageMapping("/location")
    @SendTo("/topic/locations")
    public LocationMessage updateLocation(LocationMessage locationMessage) {
        Record record = recordRepository.findById(locationMessage.getRecordId())
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // Save location updates to the database
        Location location = new Location();
        location.setLatitude(locationMessage.getLatitude());
        location.setLongitude(locationMessage.getLongitude());
        location.setAltitude(locationMessage.getAltitude());
        location.setGPStime(LocalDateTime.now());
        location.setRecord(record);  // 매치 설정

        locationRepository.save(location);

        // Set the timestamp in the response message
        locationMessage.setTimestamp(location.getGPStime());

        return locationMessage;
    }
}
