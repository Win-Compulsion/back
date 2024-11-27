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

    public boolean isAnyQueueSizeTwo(){
        for(Queue<Member> queue : matchQueues.values()){
            if(queue.size() == 2){
                return true;
            }
        }
        return false;
    }

    public Member findMemberInQueue(Long memberId) {
        // 해당 키에 대한 큐를 먼저 찾고, 큐에서 멤버를 찾는 방식으로 성능 개선
        for (Queue<Member> queue : matchQueues.values()) {
            for (Member member : queue) {
                if (member.getId().equals(memberId)) {
                    log.info("Member found in queue: Member ID = {}", memberId);
                    return member;
                }
            }
        }

        // 멤버가 없을 경우 로그 남기기
        log.info("Member not found in any queue: Member ID = {}", memberId);
        return null;
    }

    public boolean isMemberInQueue(Long memberId) {
        return findMemberInQueue(memberId) != null;
    }


    private String getKey(Member member, Integer distance) {
        if (!List.of(1, 3, 5).contains(distance)) {
            throw new IllegalArgumentException("Invalid distance value: " + distance);
        }
        return (member.isGender() ? "M" : "F") + distance;
    }

    public Long addMemberToQueue(Member member, Integer distance) {
        String key = (member.getGender() ? "M" : "F") + distance;
        Queue<Member> queue = matchQueues.get(key);

        if (queue == null) {
            throw new IllegalStateException("Queue not found for key: " + key);
        }

        queue.offer(member);
        log.info("Added member to queue: Key = {}, Member ID = {}", key, member.getId());
        log.info("Queue size = {}", queue.size());
        return member.getId();
    }
    private Long generateQueueId(String key) {
        return Long.valueOf(key.hashCode());  // key의 해시값을 사용하여 queueId 생성
    }

    public Member getNextMember(Integer distance, Boolean gender) {
        String key = (gender ? "M" : "F") + distance;
        Queue<Member> queue = matchQueues.get(key);

        if (queue == null || queue.isEmpty()) {
            return null;  // 큐가 비어 있으면 null 반환
        }

        Member member = queue.poll();
        log.info("Polled member from queue: Key = {}, Member ID = {}", key, member.getId());
        return member;
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
