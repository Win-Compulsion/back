package com.lkkp.runwith.member.repository;

import com.lkkp.runwith.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
