package com.lkkp.runwith.member.service;

import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.member.dto.MemberDto;
import com.lkkp.runwith.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor // 생성자 만들어서 주입
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    @Override
    public String memJoin(MemberDto memberDTO) {
        Member member = Member.builder()
                .id(memberDTO.getId())
                .name(memberDTO.getName())
                .nickname(memberDTO.getNickname())
                .age(memberDTO.getAge())
                .build();

        memberRepository.save(member);

        return "success";

    }

    @Override
    public String memLeave(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                NoSuchElementException::new);
        memberRepository.delete(member);
        return "success";
    }

    @Override
    public String memEdit(Long id, MemberDto memberDto) {
        Member member = memberRepository.findById(id).orElseThrow(
                NoSuchElementException::new);

        member.updateMember(memberDto.getId(), memberDto.getName(), memberDto.getNickname(), memberDto.getAge());
        memberRepository.save(member);


        return "success";
    }

}
