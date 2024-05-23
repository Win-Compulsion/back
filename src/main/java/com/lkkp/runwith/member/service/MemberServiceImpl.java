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
    public String memJoin(MemberDto memberDto) {
        Member member = Member.builder()
                .email(memberDto.getEmail())
                .gender(memberDto.getGender())
                .profileName(memberDto.getProfileName())
                .profileImg(memberDto.getProfileImg())
                .role(memberDto.getRole())
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

        member.updateMember(memberDto.getProfileName(), memberDto.getProfileImg());
        memberRepository.save(member);


        return "success";
    }

}
