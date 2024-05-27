package com.lkkp.runwith.IntervalRank.repository;
import com.lkkp.runwith.IntervalRank.Km1;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface Km1Repository extends JpaRepository<Km1, Long> {
    List<Km1> findByUserId(Long userId);
}
