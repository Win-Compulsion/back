package com.lkkp.runwith.match.controller;

import com.lkkp.runwith.match.service.MatchingService;
import com.lkkp.runwith.match.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/match")
public class MatchController {

    private final MatchingService matchService;
    private final MatchRepository matchRepository;

    @GetMapping("/request")
    public String requestMatch(@RequestParam Long userId, @RequestParam Integer distance) {
        matchService.joinQueue(userId, distance);
        return "Match request submitted successfully";
    }
}
