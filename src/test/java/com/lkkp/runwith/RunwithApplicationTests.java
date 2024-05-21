package com.lkkp.runwith;

import com.lkkp.runwith.match.Match;
import com.lkkp.runwith.match.repository.MatchRepository;
import com.lkkp.runwith.participant.Participant;
import com.lkkp.runwith.participant.repository.ParticipantRepository;
import com.lkkp.runwith.member.Gender;
import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.member.Role;
import com.lkkp.runwith.member.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
@Transactional
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
                .id(1L)
                .name("LeeMinhong")
                .email("zif04045@naver.com")
                .gender(Gender.M)
                .profileName("홍구99")
                .role(Role.USER)
                .build();

        // Create Test
        memberRepository.save(member);

        // Get Test
        Member findMem = memberRepository.findById(1L).get();
        Assertions.assertEquals(findMem.getName(), "LeeMinhong");

    }

    @Test
    void matchTest(){
        Member member = Member.builder()
                .name("SongMin")
                .email("zif04045@naver.com")
                .gender(Gender.F)
                .profileName("ddd")
                .profileImg("d")
                .role(Role.USER)
                .build();
        memberRepository.save(member);

        Participant participant = Participant.builder()
                .member(member)
                .match_id(1L)
                .build();
        participantRepository.save(participant);

        Match match = Match.builder()
                .participant(participant)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .build();
        matchRepository.save(match);


//        Participant findParticipant = participantRepository.findById(1L).get();
//        Assertions.assertEquals(findParticipant.getId(), 1L);
    }

}
