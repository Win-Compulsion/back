package com.lkkp.runwith.participant.repository;

import com.lkkp.runwith.participant.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
