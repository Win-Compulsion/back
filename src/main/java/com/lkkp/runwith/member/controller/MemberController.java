package com.lkkp.runwith.member.controller;

import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.member.dto.MemberDto;
import com.lkkp.runwith.member.repository.MemberRepository;
import com.lkkp.runwith.member.service.MemberService;
import com.lkkp.runwith.IntervalRank.Km1;
import com.lkkp.runwith.IntervalRank.Km3;
import com.lkkp.runwith.IntervalRank.Km5;
import com.lkkp.runwith.IntervalRank.repository.Km1Repository;
import com.lkkp.runwith.IntervalRank.repository.Km3Repository;
import com.lkkp.runwith.IntervalRank.repository.Km5Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final Km1Repository km1Repository;
    private final Km3Repository km3Repository;
    private final Km5Repository km5Repository;
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

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
        return memberRepository.findById(id).orElseThrow(
                NoSuchElementException::new);
    }

    @GetMapping("/member/findemail/{email:.+}")
    public MemberDto getMemberByEmail(@PathVariable("email") String email) {
        logger.info("Request received to find member by email: {}", email);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("No member found with email: {}", email);
                    return new NoSuchElementException("No member found with email: " + email);
                });

        MemberDto memberDto = MemberDto.toDto(member);
        logger.info("Successfully found member with email: {}", email);
        return memberDto;
    }

    // 모든 회원 조회
    @GetMapping("/member/all")
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
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


    @PostMapping("/member/edit/{id}")
    public String edit(@PathVariable("id") Long id, @RequestBody MemberDto memberDto) {
        String result = memberService.memEdit(id, memberDto);

        if("success".equalsIgnoreCase(result)){
            return "EDIT success";
        }else{
            return "EDIT fail";
        }
    }

    @PostMapping("/member/savedata")
    public String saveGender(@RequestBody Member member) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Received request: {}", member);

        try {
            logger.info("Received request: {}", member); // 요청 수신 로그

            // 기존 멤버를 찾거나 새로 생성
            Member existingMember = memberRepository.findByEmail(member.getEmail())
                    .orElse(null); // 이미 있는 회원은 가져오기, 없으면 null로 설정

            if (existingMember == null) {
                // 기존 멤버가 없으면 새로 생성
                logger.info("No existing member found. Creating new member for email: {}", member.getEmail());
                existingMember = new Member();
                existingMember.setEmail(member.getEmail());
            } else {
                logger.info("Found existing member for email: {}", member.getEmail());
            }

            // profileName이 null이면 기본값을 설정
            String profileName = member.getProfileName();
            if (profileName == null || profileName.isEmpty()) {
                logger.info("Profile name is null or empty, setting default value.");
                profileName = "defaultProfile"; // 기본값 설정
            }

            // 사용자 정보 설정
            existingMember.setProfileName(member.getProfileName());
            existingMember.setProfileImg(member.getProfileImg());
            existingMember.setGender(member.getGender());
            logger.info("Setting member data: {}", existingMember);

            // 멤버 저장
            memberRepository.save(existingMember);
            logger.info("Member data saved successfully for email: {}", member.getEmail());

            // 1km, 3km, 5km 기본 레이팅 데이터 생성 및 저장
            initializeRatingData(existingMember);

            return "GENDER save success";
        } catch (Exception e) {
            logger.error("Error saving member data: {}", e.getMessage());
            return "GENDER save fail: " + e.getMessage();
        }
    }

    private void initializeRatingData(Member member) {
        Logger logger = LoggerFactory.getLogger(this.getClass());

        // 1km 데이터 저장
        Km1 ratingData1km = new Km1();
        ratingData1km.setMember(member);
        ratingData1km.setRating(1000);
        ratingData1km.setWin(0);
        ratingData1km.setLose(0);
        ratingData1km.setBest_record(0.0);
        km1Repository.save(ratingData1km);
        logger.info("Initialized 1km rating data: {}", ratingData1km);

        // 3km 데이터 저장
        Km3 ratingData3km = new Km3();
        ratingData3km.setMember(member);
        ratingData3km.setRating(1000);
        ratingData3km.setWin(0);
        ratingData3km.setLose(0);
        ratingData3km.setBest_record(0.0);
        km3Repository.save(ratingData3km);
        logger.info("Initialized 3km rating data: {}", ratingData3km);

        // 5km 데이터 저장
        Km5 ratingData5km = new Km5();
        ratingData5km.setMember(member);
        ratingData5km.setRating(1000);
        ratingData5km.setWin(0);
        ratingData5km.setLose(0);
        ratingData5km.setBest_record(0.0);
        km5Repository.save(ratingData5km);
        logger.info("Initialized 5km rating data: {}", ratingData5km);
    }


}
