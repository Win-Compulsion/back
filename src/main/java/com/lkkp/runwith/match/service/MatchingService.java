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
import java.util.*;

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
    private Map<String, Queue<Member>> matchQueues = new HashMap<>();


    // 활성 매치 조회
    public Optional<Match> findActiveMatchByUserId(Long memberId) {
        // activeMatches에서 memberId를 기반으로 매칭을 찾음
        return Optional.ofNullable(activeMatches.get(memberId));
    }

    // findMemberInQueue 메서드를 MatchingService로 가져오기
    public Member findMemberInQueue(Long memberId) {
        return matchQueue.findMemberInQueue(memberId);
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

    // 매칭 성공 시
    public void createMatchAndUpdateStatus(Member member1, Member member2, Integer distance) {
        Match match = createMatch(member1, member2, distance);
        activeMatches.put(member1.getId(), match);
        activeMatches.put(member2.getId(), match);
        log.info("Match created for Members {} and {}", member1.getId(), member2.getId());
    }

    // 큐에서 멤버를 꺼낸 후, 매칭된 멤버를 찾을 때
    public String getMatchStatus(Long memberId) {
        if (activeMatches.containsKey(memberId)) {
            return "success";  // 매칭된 멤버는 성공 상태
        }
        if (findMemberInQueue(memberId) != null) {
            return "waiting";  // 큐에서 멤버가 있으면 대기 중
        }
        return "failed";  // 그 외의 경우는 실패
    }

    public Map<String, Object> joinQueue(Long userId, Integer distance, Boolean gender) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 참가자를 큐에 추가
        Long queueId = matchQueue.addMemberToQueue(member, distance); // queueId 반환하도록 수정

        log.info("User {} added to queue: distance = {}, gender = {}, queueId = {}", userId, distance, gender, queueId);

        // 매칭을 시도하고, 성공하면 "success", 아니면 "waiting"
        String status = attemptMatch(distance, gender) ? "success" : "waiting";

        // 응답을 반환 (queueId와 status 포함)
        Map<String, Object> response = new HashMap<>();
        response.put("queueId", queueId);
        response.put("status", status);

        return response;
    }

    // 매칭 시도 (반복문으로 변경)
    @Transactional
    public boolean attemptMatch(Integer distance, Boolean gender) {
        while (true) {
            Member member1 = matchQueue.getNextMember(distance, gender);
            Member member2 = matchQueue.getNextMember(distance, gender);

            // 두 명이 없으면 큐에 다시 넣고 종료
            if (member1 == null || member2 == null) {
                if (member1 != null) matchQueue.addMemberToQueue(member1, distance);
                if (member2 != null) matchQueue.addMemberToQueue(member2, distance);
                break;
            }

            // 두 명의 rating 차이를 계산
            int ratingDiff = calculateRatingDifference(member1, member2, distance);
            log.info("Attempting match: Member1 ID = {}, Member2 ID = {}, Rating Diff = {}",
                    member1.getId(), member2.getId(), ratingDiff);

            if (ratingDiff <= 100) {

                // 매칭 성공 시 처리
                Match match = createMatch(member1, member2, distance);
                log.info("Match created: {} vs {} for distance = {}", member1.getId(), member2.getId(), distance);
                activeMatches.put(member1.getId(), match);
                activeMatches.put(member2.getId(), match);
                return true;
            } else {
                // 매칭 실패 시 큐에 다시 넣기
                log.info("Match failed: Member1 ID = {}, Member2 ID = {}, Rating Diff = {}",
                        member1.getId(), member2.getId(), ratingDiff);
                matchQueue.addMemberToQueue(member1, distance);
                matchQueue.addMemberToQueue(member2, distance);
            }

            // 큐가 비었으면 종료
            if (matchQueue.isQueueEmpty(distance, gender)) {
                break;
            }
        }
        return false;
    }

    private int calculateRatingDifference(Member member1, Member member2, Integer distance) {
        int rating1 = getRating(member1.getId(), distance);
        int rating2 = getRating(member2.getId(), distance);
        log.info("Rating difference: Member1 ID = {}, Member2 ID = {}, Diff = {}", rating1, rating2, Math.abs(rating1-rating2));
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
        // 매치 생성
        Match match = Match.builder()
                .startTime(LocalDateTime.now())  // 현재 시간으로 시작 시간 설정
                .matchType("Ranked")  // 매치 타입 설정 (예: Ranked)
                .distance(distance)  // 매치 거리 설정
                .matchResult(0)  // 초기 값 (경기 결과는 아직 없음)
                .build();
        log.info("Match created: Match ID = {}", match.getMatchId());


        // 참가자 생성
        Participant participant1 = Participant.create(match, member1);  // 참가자1 생성
        Participant participant2 = Participant.create(match, member2);  // 참가자2 생성
        // 디버깅: 참가자 객체 확인
        log.info("Participant1 created: {}", participant1);
        log.info("Participant2 created: {}", participant2);

        // 객체들이 정상적으로 생성되었는지 확인 (디버깅 목적)
        if (participant1 == null || participant2 == null) {
            throw new IllegalStateException("Participants creation failed: " +
                    "participant1 = " + participant1 + ", participant2 = " + participant2);
        }

        // 매치와 참가자 저장
        matchRepository.save(match);  // 매치 저장
        participantRepository.saveAll(List.of(participant1, participant2));  // 참가자들 저장


        // 저장된 후 확인 로그
        log.info("Participants saved: Member1 = {}, Member2 = {}", member1.getId(), member2.getId());
        // 사용자에게 매치 알림 전송
//        notifyUsers(match, member1, member2);

        // 매치 객체 반환
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
