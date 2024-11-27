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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
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


    // 참가자를 매칭 큐에 추가
//    public void joinQueue(Long memberId, Integer distance) {
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        //rating이 없으면 배치 게임으로 넘김
//        if (!hasRating(memberId, distance)) {
//            startBatchGame(member, distance);
//        }
//        else {
//            matchQueue.addMemberToQueue(member, distance);
//            attemptMatch(distance, member.isGender());
//        }
//    }

    public boolean joinQueue(Long userId, Integer distance, Boolean gender) {
        // 이메일로 사용자 조회
        Member member = memberRepository.findById(userId).orElse(null);
        if (member == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        // 사용자를 큐에 추가
        matchQueue.addMemberToQueue(member, distance);

        log.info("User with ID: {} and email: {} added to queue for distance: {} and gender: {}",
                userId, member.getEmail(), distance, gender ? "Male" : "Female");

        // 매칭을 시도
        return tryMatchUser(distance, gender);
    }

    private boolean tryMatchUser(Integer distance, Boolean gender) {
        // 큐에서 대기 중인 사용자 가져오기
        Member nextMember = matchQueue.getNextMember(distance, gender);

        if (nextMember != null) {
            // 매칭이 성공하면 매칭된 사용자 반환
            return true;
        }

        // 매칭되지 않으면 대기
        return false;
    }

    public Long getMatchedUser(Integer distance, Boolean gender) {
        Member matchedUser = matchQueue.getNextMember(distance, gender);

        if (matchedUser != null) {
            return matchedUser.getId();
        }

        return null; // 매칭되지 않음
    }

    //rating이 있는지 확인
//    private boolean hasRating(Long memberId, Integer distance) {
//        return switch (distance) {
//            case 1 -> km1Repository.findByMemberId(memberId).isPresent();
//            case 3 -> km3Repository.findByMemberId(memberId).isPresent();
//            case 5 -> km5Repository.findByMemberId(memberId).isPresent();
//            default -> false;
//        };
//    }
//
//    //배치게임 시작
//    private void startBatchGame(Member member, Integer distance) {
//        // 배치 게임을 시작하는 로직
//        System.out.println("Starting batch game for member: " + member.getId() + ", distance: " + distance);
//        notifyUserBatchGameStart(member, distance); // Notify user about batch game start
//        // 예: 사용자에게 배치 게임이 시작되었음을 알리는 메시지 보내기 등.
//        // 실제 게임을 시작하는 API 호출 등의 로직을 여기에 추가할 수 있습니다.
//    }
//
//    // 배치 게임 시작 알림
//    private void notifyUserBatchGameStart(Member member, Integer distance) {
//        // Notify the user that the batch game has started
//        String message = String.format("Batch game started for member: %d, distance: %d", member.getId(), distance);
//        sendNotification(member, message);
//    }
//
//    // Placeholder for sending notifications
//    private void sendNotification(Member member, String message) {
//        System.out.println("Notification to member " + member.getId() + ": " + message);
//        // Implement actual notification logic (e.g., email, SMS, in-app message)
//    }
//
//    //배치게임 완료 후 레이팅 부여
//    @Transactional
//    public void completeBatchGame(Long memberId, Integer distance, Integer time) {
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        int initialRating = calculateInitialRating(time, distance, member.isGender());
//        updateRating(memberId, distance, initialRating);
//    }
//
//    //배치게임 레이팅 계산
//    private int calculateInitialRating(Integer time, Integer distance, boolean isMale) {
//        int[][] maleTimes = {
//                {336, 278, 236, 206, 184, 141}, // 1km
//                {1105, 922, 786, 686, 612, 463}, // 3km
//                {1889, 1579, 1352, 1184, 1060, 771} // 5km
//        };
//        int[][] femaleTimes = {
//                {380, 320, 275, 242, 217, 157}, // 1km
//                {1244, 1057, 913, 805, 723, 530}, // 3km
//                {2127, 1808, 1567, 1384, 1247, 884} // 5km
//        };
//
//        int[] times = isMale ? maleTimes[distance / 3] : femaleTimes[distance / 3];
//
//        int[] ratings = {1000, 1200, 1400, 1600, 1800, 2000};
//
//        for (int i = 0; i < times.length; i++) {
//            if (time <= times[i]) {
//                return ratings[i];
//            }
//        }
//        return 1000; // Default rating for slower times
//    }

    @Transactional
    public void attemptMatch(Integer distance, Boolean gender) {
        while (true) {
            synchronized (this) { // 동시성 문제 방지
                // 큐에서 멤버 가져오기
                Member member1 = matchQueue.getNextMember(distance, gender);
                Member member2 = matchQueue.getNextMember(distance, gender);

                // 큐에 사용자가 부족할 경우 멤버를 다시 큐에 추가하고 종료
                if (member1 == null || member2 == null) {
                    if (member1 != null) {
                        matchQueue.addMemberToQueue(member1, distance);
                        log.info("User with ID: {} added back to queue for distance: {} and gender: {}",
                                member1.getId(), distance, gender ? "Male" : "Female");
                    }
                    if (member2 != null) {
                        matchQueue.addMemberToQueue(member2, distance);
                        log.info("User with ID: {} added back to queue for distance: {} and gender: {}",
                                member2.getId(), distance, gender ? "Male" : "Female");
                    }
                    break; // 멤버 부족 시 루프 종료
                }

                // 두 멤버의 레이팅 차이를 계산하여 매치 시도
                int ratingDiff = getRatingDifference(member1, member2, distance);
                if (ratingDiff <= 100) {
                    // 매치 생성
                    createMatch(member1, member2, distance);
                    log.info("Match created between User1: {} and User2: {} for distance: {}",
                            member1.getId(), member2.getId(), distance);
                } else {
                    // 매치 실패 시 멤버를 다시 큐에 추가
                    log.info("Match failed due to rating difference: {}. Re-queuing members.", ratingDiff);
                    matchQueue.addMemberToQueue(member1, distance);
                    matchQueue.addMemberToQueue(member2, distance);
                }
            }

            // 큐가 비어 있으면 루프 종료
            if (matchQueue.isQueueEmpty(distance, gender)) {
                break;
            }
        }
    }


    private int getRatingDifference(Member member1, Member member2, Integer distance) {
        switch (distance) {
            case 1:
                return Math.abs(member1.getKm1().getRating() - member2.getKm1().getRating());
            case 3:
                return Math.abs(member1.getKm3().getRating() - member2.getKm3().getRating());
            case 5:
                return Math.abs(member1.getKm5().getRating() - member2.getKm5().getRating());
            default:
                throw new IllegalArgumentException("Invalid distance value: " + distance);
        }
    }
    @Transactional
    public Match createMatch(Member member1, Member member2, Integer distance) {
        Match match = new Match();
        match.setStartTime(LocalDateTime.now()); // 매치 시작 시간
        match.setMatchType("Ranked"); // 매치 유형 설정

        // 참가자 생성
        Participant participant1 = Participant.builder()
                .match(match)
                .member(member1)
                .build();
        Participant participant2 = Participant.builder()
                .match(match)
                .member(member2)
                .build();

        // 매치 및 참가자 저장
        matchRepository.save(match);
        participantRepository.save(participant1);
        participantRepository.save(participant2);

        // 매치 성공 알림
        notifyMatchSuccess(member1, member2, match);

        log.info("Match created successfully between {} and {} for distance: {}",
                member1.getEmail(), member2.getEmail(), distance);

        return match;
    }

    private void notifyMatchSuccess(Member member1, Member member2, Match match) {
        String matchId = match.getMatchId().toString();
        messagingTemplate.convertAndSend("/topic/match/" + member1.getId(), matchId);
        messagingTemplate.convertAndSend("/topic/match/" + member2.getId(), matchId);
    }

    //거리별 레이팅 가져오는 함수
    private int getRating(Long userId, Integer distance) {
        return switch (distance) {
            case 1 -> km1Repository.findByMemberId(userId).get().getRating();
            case 3 -> km3Repository.findByMemberId(userId).get().getRating();
            case 5 -> km5Repository.findByMemberId(userId).get().getRating();
            default -> throw new IllegalArgumentException("Invalid distance");
        };
    }

    //새로운 레이팅 부여
    public void newRating(Long user1Id, Long user2Id, Integer distance, String result) {
        Member user1 = memberRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("User1 not found"));
        Member user2 = memberRepository.findById(user2Id)
                .orElseThrow(() -> new RuntimeException("User2 not found"));

        int rating1 = getRating(user1Id, distance);
        int rating2 = getRating(user2Id, distance);

        double expectedScore1 = 1 / (1 + Math.pow(10, (rating2 - rating1) / 400.0));
        double expectedScore2 = 1 - expectedScore1;

        int k = 40;

        if ("user1".equals(result)) {
            rating1 += (int) (k * (1 - expectedScore1));
            rating2 += (int) (k * (0 - expectedScore2));
        }
        else if ("user2".equals(result)) {
            rating1 += (int) (k * (0 - expectedScore1));
            rating2 += (int) (k * (1 - expectedScore2));
        }
        else if ("draw".equals(result)) {
            rating1 += (int) (k * (0.5 - expectedScore1));
            rating2 += (int) (k * (0.5 - expectedScore2));
        }

        updateRating(user1Id, distance, rating1);
        updateRating(user2Id, distance, rating2);
    }


    //레이팅 업데이트
    private void updateRating(Long userId, Integer distance, int newRating) {
        switch (distance) {
            case 1:
                Km1 km1 = km1Repository.findByMemberId(userId).get();
                km1.setRating(newRating);
                km1Repository.save(km1);
                break;
            case 3:
                Km3 km3 = km3Repository.findByMemberId(userId).get();
                km3.setRating(newRating);
                km3Repository.save(km3);
                break;
            case 5:
                Km5 km5 = km5Repository.findByMemberId(userId).get();
                km5.setRating(newRating);
                km5Repository.save(km5);
                break;
            default:
                throw new IllegalArgumentException("Invalid distance");
        }
    }

    @Transactional
    public void completeMatch(Long matchId, Long memberId, Long completionTime) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        Participant participant = participantRepository.findByMatchIdAndMemberId(matchId, memberId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        participant.setCompleted(true);
        participant.setCompletionTime(completionTime);
        participantRepository.save(participant);

        // 두 유저가 모두 완주했는지 확인
        boolean allCompleted = match.getParticipants().stream().allMatch(Participant::isCompleted);
        if (allCompleted) {

            saveMatchRecords(match);
            endMatch(matchId);
        }
    }

    public void saveMatchRecords(Match match) {
        for (Participant participant : match.getParticipants()) {
            Member member = participant.getMember();

            Record record = new Record();
            record.setMember(member);
            record.setMatch(match);
            record.setDistance(match.getDistance()); // Assuming you have this information
            record.setRundate(new Date());
            record.setRunningTime(participant.getCompletionTime());

            //평균속도 계산
            float averageSpeed = calculateAverageSpeed(participant.getCompletionTime(), match.getDistance());
            record.setAverageSpeed(averageSpeed);

            //변화된 레이팅값 저장
            record.setChangeRating(0);

            recordRepository.save(record);
        }
    }

    // 매치 종료 처리
    private void endMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        List<Participant> participants = match.getParticipants();
        if (participants.size() == 2) {
            Participant participant1 = participants.get(0);
            Participant participant2 = participants.get(1);

            String result;
            if (participant1.getCompletionTime() < participant2.getCompletionTime()) {
                result = "user1";
            } else if (participant1.getCompletionTime() > participant2.getCompletionTime()) {
                result = "user2";
            } else {
                result = "draw";
            }

            newRating(participant1.getMember().getId(), participant2.getMember().getId(), match.getDistance(), result);

            System.out.println("Match " + matchId + " has ended. Result: " + result);
        }
    }

    private float calculateAverageSpeed(Long completionTime, Integer distance) {
        // Example calculation: distance / (completionTime / 60.0) if completionTime is in seconds
        return distance / (completionTime / 60.0f);
    }

    // 매치 조회, 업데이트, 삭제 등의 추가 메서드를 구현
}
