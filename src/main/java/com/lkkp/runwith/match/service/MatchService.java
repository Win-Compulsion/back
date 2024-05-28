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
                return Km1Repository.findByUserId(userId).get(0).getRating();
            case "3km":
                return Km3Repository.findByUserId(userId).get(0).getRating();
            case "5km":
                return Km5Repository.findByUserId(userId).get(0).getRating();
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
        // WebSocket 메시지 전송 로직
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
