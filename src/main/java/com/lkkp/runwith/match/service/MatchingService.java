package com.lkkp.runwith.match.service;


import com.lkkp.runwith.IntervalRank.Km1;
import com.lkkp.runwith.IntervalRank.Km3;
import com.lkkp.runwith.IntervalRank.Km5;
import com.lkkp.runwith.record.Record;
import com.lkkp.runwith.IntervalRank.repository.Km1Repository;
import com.lkkp.runwith.IntervalRank.repository.Km3Repository;
import com.lkkp.runwith.IntervalRank.repository.Km5Repository;
import com.lkkp.runwith.match.Match;
import com.lkkp.runwith.match.MatchQueue;
import com.lkkp.runwith.match.repository.MatchRepository;
import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.member.repository.MemberRepository;
import com.lkkp.runwith.participant.Participant;
import com.lkkp.runwith.participant.repository.ParticipantRepository;
import com.lkkp.runwith.record.repository.RecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final MatchQueue matchQueue;
    private final MatchRepository matchRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;
    private final Km1Repository km1Repository;
    private final Km3Repository km3Repository;
    private final Km5Repository km5Repository;
    private final RecordRepository recordRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final Map<Long, Match> activeMatches = new HashMap<>();

    // 활성 매치 조회
    public Optional<Match> findActiveMatchByUserId(Long userId) {
        return Optional.ofNullable(activeMatches.get(userId));
    }

    public void addActiveMatch(Long userId, Match match) {
        activeMatches.put(userId, match);
        log.info("Active match added: User ID = {}, Match = {}", userId, match.getMatchId());
    }

    public void removeActiveMatch(Long userId) {
        activeMatches.remove(userId);
        log.info("Active match removed for User ID = {}", userId);
    }

    public boolean hasActiveMatch(Long userId) {
        return activeMatches.containsKey(userId);
    }

    // 매칭 상태 확인
    public String getMatchStatus(Long queueId) {
        if (matchQueue.findMemberInQueue(queueId) != null) {
            return "waiting";
        }
        if (findActiveMatchByUserId(queueId).isPresent()) {
            return "success";
        }
        return "failed";
    }

    // 큐에 참가자 추가
    public boolean joinQueue(Long userId, Integer distance, Boolean gender) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        matchQueue.addMemberToQueue(member, distance);
        log.info("User {} added to queue: distance = {}, gender = {}", userId, distance, gender);
        return attemptMatch(distance, gender);
    }

    // 매칭 시도 (반복문으로 변경)
    @Transactional
    public boolean attemptMatch(Integer distance, Boolean gender) {
        while (true) {
            Member member1 = matchQueue.getNextMember(distance, gender);
            Member member2 = matchQueue.getNextMember(distance, gender);

            if (member1 == null || member2 == null) {
                if (member1 != null) matchQueue.addMemberToQueue(member1, distance);
                if (member2 != null) matchQueue.addMemberToQueue(member2, distance);
                break;
            }

            int ratingDiff = calculateRatingDifference(member1, member2, distance);
            if (ratingDiff <= 100) {
                createMatch(member1, member2, distance);
                log.info("Match created: {} vs {} for distance = {}", member1.getId(), member2.getId(), distance);
                return true;
            } else {
                matchQueue.addMemberToQueue(member1, distance);
                matchQueue.addMemberToQueue(member2, distance);
            }

            if (matchQueue.isQueueEmpty(distance, gender)) {
                break;
            }
        }
        return false;
    }

    private int calculateRatingDifference(Member member1, Member member2, Integer distance) {
        int rating1 = getRating(member1.getId(), distance);
        int rating2 = getRating(member2.getId(), distance);
        return Math.abs(rating1 - rating2);
    }

    private int getRating(Long userId, Integer distance) {
        return switch (distance) {
            case 1 -> km1Repository.findByMemberId(userId).map(Km1::getRating).orElse(1000);
            case 3 -> km3Repository.findByMemberId(userId).map(Km3::getRating).orElse(1000);
            case 5 -> km5Repository.findByMemberId(userId).map(Km5::getRating).orElse(1000);
            default -> throw new IllegalArgumentException("Invalid distance");
        };
    }

    @Transactional
    public Match createMatch(Member member1, Member member2, Integer distance) {
        Match match = Match.builder()
                .startTime(LocalDateTime.now())  // 현재 시간으로 시작 시간 설정
                .matchType("Ranked")  // 매치 타입 설정 (예: Ranked)
                .distance(distance)  // 매치 거리 설정
                .matchResult(0)  // 초기 값 (경기 결과는 아직 없음)
                .build();

        Participant participant1 = Participant.create(match, member1);
        Participant participant2 = Participant.create(match, member2);

        matchRepository.save(match);
        participantRepository.saveAll(List.of(participant1, participant2));

        notifyUsers(match, member1, member2);
        return match;
    }

    private void notifyUsers(Match match, Member... members) {
        for (Member member : members) {
            String topic = "/topic/match/" + member.getId();
            messagingTemplate.convertAndSend(topic, match.getMatchId().toString());
            log.info("Match notification sent to User ID = {}", member.getId());
        }
    }

    public void completeMatch(Long matchId, Long memberId, Long completionTime) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found: " + matchId));

        Participant participant = participantRepository.findByMatchIdAndMemberId(matchId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        participant.setCompleted(true);
        participant.setCompletionTime(completionTime);
        participantRepository.save(participant);

        if (match.getParticipants().stream().allMatch(Participant::isCompleted)) {
            finalizeMatch(match);
        }
    }

    private void finalizeMatch(Match match) {
        List<Participant> participants = match.getParticipants();
        if (participants.size() == 2) {
            String result = determineMatchResult(participants);
            newRating(participants.get(0).getMember().getId(), participants.get(1).getMember().getId(), match.getDistance(), result);
            log.info("Match finalized: Match ID = {}, Result = {}", match.getMatchId(), result);
        }
    }

    private String determineMatchResult(List<Participant> participants) {
        long time1 = participants.get(0).getCompletionTime();
        long time2 = participants.get(1).getCompletionTime();
        return time1 < time2 ? "user1" : (time1 > time2 ? "user2" : "draw");
    }

    // 매칭 결과 반영
    private void newRating(Long user1Id, Long user2Id, Integer distance, String result) {
        // 동일한 코드 유지
    }

    // 기타 메서드 생략 (코드 유지)
}
