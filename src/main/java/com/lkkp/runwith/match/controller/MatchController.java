package com.lkkp.runwith.match.controller;

import com.lkkp.runwith.match.service.MatchingService;
import com.lkkp.runwith.match.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/match")
public class MatchController {

    private final MatchingService matchingService;
    private final MatchRepository matchRepository;

    @GetMapping("/request")
    public String requestMatch(@RequestParam Long userId, @RequestParam Integer distance) {
        matchingService.joinQueue(userId, distance);
        return "Match request submitted successfully";
    }

    @PostMapping("/completeBatch")
    public ResponseEntity<String> completeBatchGame(@RequestParam Long userId, @RequestParam Integer distance, @RequestParam Integer time) {
        matchingService.completeBatchGame(userId, distance, time);
        return ResponseEntity.ok("Batch game completed successfully");
    }

    // 완주 완료 처리
    @PostMapping("/complete")
    public ResponseEntity<String> completeMatch(@RequestParam Long matchId, @RequestParam Long memberId, @RequestParam Long completionTime) {
        matchingService.completeMatch(matchId, memberId, completionTime);
        return ResponseEntity.ok("Match completed successfully");
    }
}
