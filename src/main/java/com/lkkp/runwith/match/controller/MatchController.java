package com.lkkp.runwith.match.controller;

import com.lkkp.runwith.match.service.MatchingService;
import com.lkkp.runwith.match.repository.MatchRepository;
import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/match")
public class MatchController {

    private final MatchingService matchingService;
    private final MemberRepository memberRepository;


    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> requestMatch(
            @RequestBody Map<String, Object> body) {

        String email = (String) body.get("email");
        String distanceStr = (String) body.get("distance");
        Map<String, Object> response = new HashMap<>();

        try {
            int distance = Integer.parseInt(distanceStr);
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

            Long userId = member.getId();
            boolean gender = member.getGender();

            // attemptMatch를 호출하여 매칭 시도
            matchingService.joinQueue(userId, distance, gender);

            response.put("success", true);
            response.put("message", "Waiting for a match...");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 매칭 상태 확인 API
    @GetMapping("/status/{queueId}")
    public ResponseEntity<String> getMatchStatus(@PathVariable Long queueId) {
        String status = matchingService.getMatchStatus(queueId);

        // JSON 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("queueId", queueId);
        response.put("status", status);
        log.info(response.get("status").toString());

        return ResponseEntity.ok(status);
    }

//    @PostMapping("/completeBatch")
//    public ResponseEntity<String> completeBatchGame(@RequestParam Long userId, @RequestParam Integer distance, @RequestParam Integer time) {
//        matchingService.completeBatchGame(userId, distance, time);
//        return ResponseEntity.ok("Batch game completed successfully");
//    }

    // 완주 완료 처리
    @PostMapping("/complete")
    public ResponseEntity<String> completeMatch(@RequestParam Long matchId, @RequestParam Long memberId, @RequestParam Long completionTime) {
        matchingService.completeMatch(matchId, memberId, completionTime);
        return ResponseEntity.ok("Match completed successfully");
    }
}
