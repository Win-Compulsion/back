package com.lkkp.runwith.IntervalRank.repository;

import com.lkkp.runwith.IntervalRank.Km3;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Km3Repository extends JpaRepository<Km3, Long> {
    Optional<Km3> findByMemberId(Long memberId);

}
