package com.lkkp.runwith.match.controller;

import com.lkkp.runwith.match.service.MatchService;
import com.lkkp.runwith.match.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/match")
public class MatchController {

    private final MatchService matchService;
    private final MatchRepository matchRepository;

    @GetMapping("/request")
    public String requestMatch(@RequestParam Long userId, @RequestParam String distance) {
        matchService.addToQueue(userId, distance);
        return "Match request submitted successfully";
    }
}
