package com.lkkp.runwith.match.service;

import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.match.Match;
import com.lkkp.runwith.IntervalRank.Km1;
import com.lkkp.runwith.IntervalRank.Km3;
import com.lkkp.runwith.IntervalRank.Km5;
import com.lkkp.runwith.member.repository.MemberRepository;
import com.lkkp.runwith.match.repository.MatchRepository;
import com.lkkp.runwith.IntervalRank.repository.Km1Repository;
import com.lkkp.runwith.IntervalRank.repository.Km3Repository;
import com.lkkp.runwith.IntervalRank.repository.Km5Repository;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MatchService {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private Km1Repository km1Repository;

    @Autowired
    private Km3Repository km3Repository;

    @Autowired
    private Km5Repository km5Repository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final int MATCH_THRESHOLD = 100;

    private final Map<String, Map<Boolean, List<String>>> waitingQueues = new HashMap<>();
    private final Map<String, Map<Boolean, Map<String, Integer>>> ratings = new HashMap<>();

    public void MatchingService() {
        waitingQueues.put("1km", new HashMap<>());
        waitingQueues.get("1km").put(true, new ArrayList<>());
        waitingQueues.get("1km").put(false, new ArrayList<>());

        waitingQueues.put("3km", new HashMap<>());
        waitingQueues.get("3km").put(true, new ArrayList<>());
        waitingQueues.get("3km").put(false, new ArrayList<>());

        waitingQueues.put("5km", new HashMap<>());
        waitingQueues.get("5km").put(true, new ArrayList<>());
        waitingQueues.get("5km").put(false, new ArrayList<>());

        ratings.put("1km", new HashMap<>());
        ratings.get("1km").put(true, new HashMap<>());
        ratings.get("1km").put(false, new HashMap<>());

        ratings.put("3km", new HashMap<>());
        ratings.get("3km").put(true, new HashMap<>());
        ratings.get("3km").put(false, new HashMap<>());

        ratings.put("5km", new HashMap<>());
        ratings.get("5km").put(true, new HashMap<>());
        ratings.get("5km").put(false, new HashMap<>());
    }

    public void addToQueue(Long userId, String distance) {
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        int rating = getRating(userId, distance);

        boolean gender = user.isGender(); // 남녀 구분

        List<String> queue = waitingQueues.get(distance).get(gender);
        Map<String, Integer> ratingMap = ratings.get(distance).get(gender);

        queue.add(user.getNickname());
        ratingMap.put(user.getNickname(), rating);

        tryMatchmaking(distance, gender);
    }

    private int getRating(Long userId, String distance) {
        switch (distance) {
            case "1km":
                return km1Repository.findByUserId(userId).get(0).getRating();
            case "3km":
                return km3Repository.findByUserId(userId).get(0).getRating();
            case "5km":
                return km5Repository.findByUserId(userId).get(0).getRating();
            default:
                throw new IllegalArgumentException("Invalid distance");
        }
    }

    private synchronized void tryMatchmaking(String distance, boolean gender) {
        List<String> queue = waitingQueues.get(distance).get(gender);
        Map<String, Integer> ratingMap = ratings.get(distance).get(gender);

        for (int i = 0; i < queue.size() - 1; i++) {
            String player1 = queue.get(i);
            int rating1 = ratingMap.get(player1);
            for (int j = i + 1; j < queue.size(); j++) {
                String player2 = queue.get(j);
                int rating2 = ratingMap.get(player2);
                if (Math.abs(rating1 - rating2) <= MATCH_THRESHOLD) {
                    // 매칭 성공
                    queue.remove(player1);
                    queue.remove(player2);
                    System.out.println("Match found: " + player1 + " vs " + player2);

                    // 매칭된 상대방 정보 전달
                    MatchedUserInfo player1Info = new MatchedUserInfo(player2, rating2);
                    MatchedUserInfo player2Info = new MatchedUserInfo(player1, rating1);
                    sendMatchedUserInfo(player1, player1Info);
                    sendMatchedUserInfo(player2, player2Info);
                    return;
                }
            }
        }
    }

    private void sendMatchedUserInfo(String playerNickname, MatchedUserInfo matchedUserInfo) {
        messagingTemplate.convertAndSend("/topic/match", matchedUserInfo);
    }

    public void updateRatings(Long user1Id, Long user2Id, String distance, String result) {
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
            rating1 += k * (1 - expectedScore1);
            rating2 += k * (0 - expectedScore2);
        } else if ("user2".equals(result)) {
            rating1 += k * (0 - expectedScore1);
            rating2 += k * (1 - expectedScore2);
        } else if ("draw".equals(result)) {
            rating1 += k * (0.5 - expectedScore1);
            rating2 += k * (0.5 - expectedScore2);
        }

        updateRating(user1Id, distance, rating1);
        updateRating(user2Id, distance, rating2);
    }


    private void updateRating(Long userId, String distance, int newRating) {
        switch (distance) {
            case "1km":
                Km1 km1 = km1Repository.findByUserId(userId).get(0);
                km1.setRating(newRating);
                km1Repository.save(km1);
                break;
            case "3km":
                Km3 km3 = km3Repository.findByUserId(userId).get(0);
                km3.setRating(newRating);
                km3Repository.save(km3);
                break;
            case "5km":
                Km5 km5 = km5Repository.findByUserId(userId).get(0);
                km5.setRating(newRating);
                km5Repository.save(km5);
                break;
            default:
                throw new IllegalArgumentException("Invalid distance");
        }
    }


    public static class MatchedUserInfo {
        private String matchedUserNickname;
        private int matchedUserRating;

        public MatchedUserInfo(String matchedUserNickname, int matchedUserRating) {
            this.matchedUserNickname = matchedUserNickname;
            this.matchedUserRating = matchedUserRating;
        }

        public String getMatchedUserNickname() {
            return matchedUserNickname;
        }

        public void setMatchedUserNickname(String matchedUserNickname) {
            this.matchedUserNickname = matchedUserNickname;
        }

        public int getMatchedUserRating() {
            return matchedUserRating;
        }

        public void setMatchedUserRating(int matchedUserRating) {
            this.matchedUserRating = matchedUserRating;
        }
    }

}
