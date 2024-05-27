package com.lkkp.runwith.match.repository;

import com.lkkp.runwith.match.Match;
import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.IntervalRank.Km1;
import com.lkkp.runwith.IntervalRank.Km3;
import com.lkkp.runwith.IntervalRank.Km5;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
