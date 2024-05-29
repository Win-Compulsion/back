package com.lkkp.runwith.member.repository;

import com.lkkp.runwith.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email); // email을 통해 회원인지 확인
    Optional<Member> findByNickname(String profilename);
    Optional<Member> findById(Long id);
}
