package com.lkkp.runwith.match.service;


import com.lkkp.runwith.IntervalRank.Km1;
import com.lkkp.runwith.IntervalRank.Km3;
import com.lkkp.runwith.IntervalRank.Km5;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MatchQueue matchQueue = new MatchQueue();
    private final MatchRepository matchRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;
    private final Km1Repository km1Repository;
    private final Km3Repository km3Repository;
    private final Km5Repository km5Repository;

    // 참가자를 매칭 큐에 추가
    public void joinQueue(Long memberId, Integer distance) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!hasRating(memberId, distance)) {
            startBatchGame(member, distance);
        }
        else {
            matchQueue.addMemberToQueue(member, distance);
            attemptMatch(distance, member.isGender());
        }
    }

    private boolean hasRating(Long memberId, Integer distance) {
        return switch (distance) {
            case 1 -> km1Repository.findByMemberId(memberId).isPresent();
            case 3 -> km3Repository.findByMemberId(memberId).isPresent();
            case 5 -> km5Repository.findByMemberId(memberId).isPresent();
            default -> false;
        };
    }

    private void startBatchGame(Member member, Integer distance) {
        // 배치 게임을 시작하는 로직
        System.out.println("Starting batch game for member: " + member.getId() + ", distance: " + distance);
        // 예: 사용자에게 배치 게임이 시작되었음을 알리는 메시지 보내기 등.
        // 실제 게임을 시작하는 API 호출 등의 로직을 여기에 추가할 수 있습니다.
    }

    @Transactional
    public void completeBatchGame(Long memberId, Integer distance, Integer time) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        int initialRating = calculateInitialRating(time, distance, member.isGender());
        updateRating(memberId, distance, initialRating);
    }

    private int calculateInitialRating(Integer time, Integer distance, boolean isMale) {
        int[][] maleTimes = {
                {336, 278, 236, 206, 184, 141}, // 1km
                {1105, 922, 786, 686, 612, 463}, // 3km
                {1889, 1579, 1352, 1184, 1060, 771} // 5km
        };
        int[][] femaleTimes = {
                {380, 320, 275, 242, 217, 157}, // 1km
                {1244, 1057, 913, 805, 723, 530}, // 3km
                {2127, 1808, 1567, 1384, 1247, 884} // 5km
        };

        int[] times = isMale ? maleTimes[distance / 3] : femaleTimes[distance / 3];

        int[] ratings = {1000, 1200, 1400, 1600, 1800, 2000};

        for (int i = 0; i < times.length; i++) {
            if (time <= times[i]) {
                return ratings[i];
            }
        }
        return 1000; // Default rating for slower times
    }

    // 매칭 시도
    @Transactional
    public void attemptMatch(Integer distance, Boolean gender) {

        while (!matchQueue.isQueueEmpty(distance, gender)) {
            Member member1 = matchQueue.getNextMember(distance, gender);
            Member member2 = matchQueue.getNextMember(distance, gender);

            if (member1 == null || member2 == null) {
                if (member1 != null) {
                    matchQueue.addMemberToQueue(member1, distance);
                }
                break;
            }
            if(distance == 1) {
                if (Math.abs(member1.getKm1().getRating() - member2.getKm1().getRating()) <= 100) { // 레이팅 차이 100 이하
                    createMatch(member1, member2, distance);
                }
                else {
                    matchQueue.addMemberToQueue(member1, distance);
                    matchQueue.addMemberToQueue(member2, distance);
                    break;
                }
            }
            if (distance == 3) {
                if (Math.abs(member1.getKm3().getRating() - member2.getKm3().getRating()) <= 100) { // 레이팅 차이 100 이하
                    createMatch(member1, member2, distance);
                }
                else {
                    matchQueue.addMemberToQueue(member1, distance);
                    matchQueue.addMemberToQueue(member2, distance);
                    break;
                }
            }
            if (distance == 5) {
                if (Math.abs(member1.getKm5().getRating() - member2.getKm5().getRating()) <= 100) { // 레이팅 차이 100 이하
                    createMatch(member1, member2, distance);
                }
                else {
                    matchQueue.addMemberToQueue(member1, distance);
                    matchQueue.addMemberToQueue(member2, distance);
                    break;
                }
            }
        }
    }

    // 매치 생성
    @Transactional
    public Match createMatch(Member member1, Member member2, Integer distance) {
        Match match = new Match();
        Participant participant1 = Participant.builder()
                .match(match)
                .member(member1)
                .build();
        Participant participant2 = Participant.builder()
                .match(match)
                .member(member2)
                .build();
        participantRepository.save(participant1);
        participantRepository.save(participant2);
        return matchRepository.save(match);
    }

    private int getRating(Long userId, Integer distance) {
        return switch (distance) {
            case 1 -> km1Repository.findByMemberId(userId).get().getRating();
            case 3 -> km3Repository.findByMemberId(userId).get().getRating();
            case 5 -> km5Repository.findByMemberId(userId).get().getRating();
            default -> throw new IllegalArgumentException("Invalid distance");
        };
    }

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

    // 매치 조회, 업데이트, 삭제 등의 추가 메서드를 구현
}
