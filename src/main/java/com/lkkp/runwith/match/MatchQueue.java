package com.lkkp.runwith.match;

import com.lkkp.runwith.member.Member;

import java.util.*;

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
    }

    private String getKey(Member member, Integer distance) {
        return (member.isGender() ? "M" : "F") + distance;
    }

    public void addMemberToQueue(Member member, Integer distance) {
        String key = getKey(member, distance);
        matchQueues.get(key).offer(member);
    }

    public Member getNextMember(Integer distance, Boolean gender) {
        String key = (gender ? "M" : "F") + distance;
        return matchQueues.get(key).poll();
    }

    public boolean isQueueEmpty(Integer distance, Boolean gender) {
        String key = (gender ? "M" : "F") + distance;
        return matchQueues.get(key).isEmpty();
    }

    public List<Queue<Member>> getAllQueues() {
        return new LinkedList<>(matchQueues.values());
    }
}
