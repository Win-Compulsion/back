package com.lkkp.runwith.member.service;

import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.member.dto.JoinRequest;
import com.lkkp.runwith.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // 생성자 만들어서 주입
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    @Override
    public String memberJoin(JoinRequest joinRequest) {
        Member member = Member.builder()
                .id(joinRequest.getId())
                .name(joinRequest.getName())
                .nickname(joinRequest.getNickname())
                .age(joinRequest.getAge())
                .build();

        memberRepository.save(member);

        return "success";

    }
}
