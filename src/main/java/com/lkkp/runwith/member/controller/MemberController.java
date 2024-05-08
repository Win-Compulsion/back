package com.lkkp.runwith.member.controller;

import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.member.dto.JoinRequest;
import com.lkkp.runwith.member.repository.MemberRepository;
import com.lkkp.runwith.member.service.MemberService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public Member getMember(@PathVariable("id") Long id) {
       // return memberRepository.getReferenceById(id);
        Optional<Member> result = memberRepository.findById(id);
        if(result.isPresent()){
            Member member1 = result.get();
            return member1;
        }
        return null;
    }

    // 모든 회원 조회
    @GetMapping("/member/all")
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }


    // 회원 가입
    @PostMapping("/member/join")
    public String join(@RequestBody JoinRequest joinRequest) {
        String result = memberService.memberJoin(joinRequest);

        if("success".equalsIgnoreCase(result)){
            return "success";
        }else{
            return "fail";
        }
    }







}
