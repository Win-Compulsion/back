package com.lkkp.runwith.participant.repository;

import com.lkkp.runwith.participant.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    @Query("SELECT p FROM Participant p WHERE p.match.matchId = :matchId AND p.member.id = :memberId")
    Optional<Participant> findByMatchIdAndMemberId (Long matchId, Long memberId);

}
