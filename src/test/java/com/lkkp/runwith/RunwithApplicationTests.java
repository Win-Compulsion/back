package com.lkkp.runwith;

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

    @Test
    void testJPA() {
        Member member1 = new Member();
        member1.setId(1L);
        member1.setName("LMH");
        member1.setNickname("Handsome");
        member1.setAge(25);
        this.memberRepository.save(member1);

        Assertions.assertEquals(member1.getId(), 1);

    }

}
