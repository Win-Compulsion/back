package com.lkkp.runwith.match;

import com.lkkp.runwith.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class MatchQueue {
    private final Map<String, Queue<Member>> matchQueues;

    public MatchQueue() {
        this.matchQueues = new HashMap<>();
        this.matchQueues.put("M1", new LinkedList<>());
        this.matchQueues.put("M3", new LinkedList<>());
        this.matchQueues.put("M5", new LinkedList<>());
        this.matchQueues.put("F1", new LinkedList<>());
        this.matchQueues.put("F3", new LinkedList<>());
        this.matchQueues.put("F5", new LinkedList<>());

        log.info("MatchQueue initialized with keys: {}", matchQueues.keySet());

    }

    private String getKey(Member member, Integer distance) {
        if (!List.of(1, 3, 5).contains(distance)) {
            throw new IllegalArgumentException("Invalid distance value: " + distance);
        }
        return (member.isGender() ? "M" : "F") + distance;
    }

    public void addMemberToQueue(Member member, Integer distance) {
        String key = getKey(member, distance);
        Queue<Member> queue = matchQueues.get(key);

        if (queue == null) {
            throw new IllegalStateException("Queue not found for key: " + key);
        }

        queue.offer(member);
        log.info("Added member to queue: Key = {}, Member ID = {}", key, member.getId());
    }

    public Member getNextMember(Integer distance, Boolean gender) {
        String key = (gender ? "M" : "F") + distance;
        Queue<Member> queue = matchQueues.get(key);

        if (queue == null) {
            throw new IllegalStateException("Queue not found for key: " + key);
        }

        return queue.poll();
    }

    public boolean isQueueEmpty(Integer distance, Boolean gender) {
        String key = (gender ? "M" : "F") + distance;
        Queue<Member> queue = matchQueues.get(key);

        if (queue == null) {
            log.warn("Queue not found for key: {}", key);
            return true;
        }

        return queue.isEmpty();
    }

    public Collection<Queue<Member>> getAllQueues() {
        return matchQueues.values();
    }
}
