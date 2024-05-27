package com.lkkp.runwith.IntervalRank.repository;

import com.lkkp.runwith.IntervalRank.Km3;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Km3Repository extends JpaRepository<Km3, Long> {
    List<Km3> findByUserId(Long userId);
}
