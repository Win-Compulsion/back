package com.lkkp.runwith.IntervalRank.repository;

import com.lkkp.runwith.IntervalRank.Km1;
import com.lkkp.runwith.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Km1Repository extends JpaRepository<Km1, Integer> {
    Optional<Km1> findByMemberId(Long memberId);

}
