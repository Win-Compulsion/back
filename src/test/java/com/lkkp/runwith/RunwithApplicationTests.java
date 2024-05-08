package com.lkkp.runwith;

import com.lkkp.runwith.match.Match;
import com.lkkp.runwith.match.repository.MatchRepository;
import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.member.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RunwithApplicationTests {

    @Autowired
    private MemberRepository memberRepository;
    private MatchRepository matchRepository;

    @Test
    void crudTest() {
        Member member = Member.builder()
                .id(1L)
                .name("LeeMinhong")
                .nickname("hong9")
                .age(25)
                .build();

        // Create Test
        memberRepository.save(member);

        // Get Test
        Member findMem = memberRepository.findById(1L).get();
        Assertions.assertEquals(findMem.getName(), "LeeMinhong");

    }

}
