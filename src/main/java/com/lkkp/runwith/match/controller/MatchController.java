package com.lkkp.runwith.match.controller;

import com.lkkp.runwith.match.service.MatchService;
import com.lkkp.runwith.match.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/match")
public class MatchController {

    private final MatchService matchService;
    private final MatchRepository matchRepository;

    @PostMapping("/request")
    public String requestMatch(@RequestBody MatchRequest matchRequest) {
        boolean success = matchService.addToQueue(matchRequest.getUserId(), matchRequest.getDistance(), matchRequest.isGender());
        return success ? "Match request submitted successfully" : "Failed to submit match request";
    }
}
