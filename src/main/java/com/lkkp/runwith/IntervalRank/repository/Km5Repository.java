package com.lkkp.runwith.IntervalRank.repository;

import com.lkkp.runwith.IntervalRank.Km5;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Km5Repository extends JpaRepository<Km5, Long> {
    List<Km5> findByUserId(Long userId);
}
