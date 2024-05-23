package com.lkkp.runwith.member.controller;

import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.member.dto.MemberDto;
import com.lkkp.runwith.member.repository.MemberRepository;
import com.lkkp.runwith.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/hello")
    public String hello() {
        return "test Hello";
    }

    /*
        조회하는건 그냥 Controller에서 처리
        새로운 회원 저장 / 삭제는 Service로 구현 후 처리
     */

    // 회원 아이디로 조회 > Member JSON 반환
    @GetMapping("/member/{id}")
    public MemberDto getMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                    NoSuchElementException::new);

        return MemberDto.toDto(member);
    }

    // 모든 회원 조회
    @GetMapping("/member/all")
    public List<MemberDto> getAllMembers() {

        List<Member> members = memberRepository.findAll();
        List<MemberDto> memberDtos = new ArrayList<>();
        for (Member member : members) {
            memberDtos.add(MemberDto.toDto(member));
        }

        return memberDtos;
    }


    // 회원 가입
    @PostMapping("/member/join")
    public String join(@RequestBody MemberDto memberDto) {
        String result = memberService.memJoin(memberDto);

        if("success".equalsIgnoreCase(result)){
            return "JOIN success";
        }else{
            return "JOIN fail";
        }
    }

    // 회원 삭제
    @DeleteMapping("/member/leave/{id}")
    public String leave(@PathVariable("id") Long id){
        String result = memberService.memLeave(id);
        if("success".equalsIgnoreCase(result)){
            return "LEAVE success";
        }else{
            return "LEAVE fail";
        }
    }

    // 회원 수정
    @PostMapping("/member/edit/{id}")
    public String edit(@PathVariable("id") Long id, @RequestBody MemberDto memberDto) {
        String result = memberService.memEdit(id, memberDto);

        if("success".equalsIgnoreCase(result)){
            return "EDIT success";
        }else{
            return "EDIT fail";
        }
    }











}
