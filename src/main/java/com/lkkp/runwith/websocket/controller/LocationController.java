package com.lkkp.runwith.websocket.controller;

import com.lkkp.runwith.location.Location;
import com.lkkp.runwith.location.repository.LocationRepository;
import com.lkkp.runwith.match.Match;
import com.lkkp.runwith.match.repository.MatchRepository;
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
    private MatchRepository matchRepository;

    @MessageMapping("/location")
    @SendTo("/topic/locations")
    public LocationMessage updateLocation(LocationMessage locationMessage) {
        Match match = matchRepository.findById(locationMessage.getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // Save location updates to the database
        Location location = new Location();
        location.setUserId(locationMessage.getUserId());
        location.setLatitude(locationMessage.getLatitude());
        location.setLongitude(locationMessage.getLongitude());
        location.setAltitude(locationMessage.getAltitude());
        location.setGPStime(LocalDateTime.now());
        location.setMatch(match);  // 매치 설정

        locationRepository.save(location);

        // Set the timestamp in the response message
        locationMessage.setTimestamp(location.getGPStime());

        return locationMessage;
    }
}
