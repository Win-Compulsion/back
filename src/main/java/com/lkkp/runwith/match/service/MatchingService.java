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
        matchQueue.addMemberToQueue(member, distance);
        attemptMatch(distance, member.isGender());
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
                } else {
                    matchQueue.addMemberToQueue(member1, distance);
                    matchQueue.addMemberToQueue(member2, distance);
                    break;
                }
            }
            if (distance == 3) {
                if (Math.abs(member1.getKm3().getRating() - member2.getKm3().getRating()) <= 100) { // 레이팅 차이 100 이하
                    createMatch(member1, member2, distance);
                } else {
                    matchQueue.addMemberToQueue(member1, distance);
                    matchQueue.addMemberToQueue(member2, distance);
                    break;
                }
            }
            if (distance == 5) {
                if (Math.abs(member1.getKm5().getRating() - member2.getKm5().getRating()) <= 100) { // 레이팅 차이 100 이하
                    createMatch(member1, member2, distance);
                } else {
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

    private int getRating(Long userId, String distance) {
        return switch (distance) {
            case "1km" -> km1Repository.findByMemberId(userId).get().getRating();
            case "3km" -> km3Repository.findByMemberId(userId).get().getRating();
            case "5km" -> km5Repository.findByMemberId(userId).get().getRating();
            default -> throw new IllegalArgumentException("Invalid distance");
        };
    }

    public void newRating(Long user1Id, Long user2Id, String distance, String result) {
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
        } else if ("user2".equals(result)) {
            rating1 += (int) (k * (0 - expectedScore1));
            rating2 += (int) (k * (1 - expectedScore2));
        } else if ("draw".equals(result)) {
            rating1 += (int) (k * (0.5 - expectedScore1));
            rating2 += (int) (k * (0.5 - expectedScore2));
        }

        updateRating(user1Id, distance, rating1);
        updateRating(user2Id, distance, rating2);
    }


    private void updateRating(Long userId, String distance, int newRating) {
        switch (distance) {
            case "1km":
                Km1 km1 = km1Repository.findByMemberId(userId).get();
                km1.setRating(newRating);
                km1Repository.save(km1);
                break;
            case "3km":
                Km3 km3 = km3Repository.findByMemberId(userId).get();
                km3.setRating(newRating);
                km3Repository.save(km3);
                break;
            case "5km":
                Km5 km5 = km5Repository.findByMemberId(userId).get();
                km5.setRating(newRating);
                km5Repository.save(km5);
                break;
            default:
                throw new IllegalArgumentException("Invalid distance");
        }
    }

    // 매치 조회, 업데이트, 삭제 등의 추가 메서드를 구현하세요
}
