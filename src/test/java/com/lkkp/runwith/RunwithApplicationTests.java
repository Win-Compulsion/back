package com.lkkp.runwith;

import com.lkkp.runwith.match.Match;
import com.lkkp.runwith.match.repository.MatchRepository;
import com.lkkp.runwith.participant.Participant;
import com.lkkp.runwith.participant.repository.ParticipantRepository;
import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.member.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
//@Transactional
@SpringBootTest
class RunwithApplicationTests {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private MatchRepository matchRepository;


    @Test
    void crudTest() {
        Member member = Member.builder()
                .email("zif04045@naver.com")
                .gender(true)
                .profileName("홍구99")
                .build();

        // Create Test
        memberRepository.save(member);

        // Get Test
       // Member findMem = memberRepository.findById(4L).get();
        //Assertions.assertEquals(findMem.getName(), "LeeMinhong");

    }

    @Test
    void matchTest(){
        Member member = Member.builder()
                //.name("Kang")
                .email("cyd04045@naver.com")
                .gender(true)
                .profileName("ddd")
                .profileImg("d")
                .build();
        memberRepository.save(member);


        Match match = Match.builder()
                .matchId(1L)
                //.startTime(LocalTime.of(10, 0))
                //.endTime(LocalTime.of(12, 0))
                .build();
        matchRepository.save(match);

        Participant participant = Participant.builder()
                .member(member)
                .match(match)
                .build();
        participantRepository.save(participant);


//        Participant findParticipant = participantRepository.findById(1L).get();
//        Assertions.assertEquals(findParticipant.getId(), 1L);
    }

}
