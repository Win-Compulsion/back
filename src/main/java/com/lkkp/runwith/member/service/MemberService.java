package com.lkkp.runwith.member.service;

import com.lkkp.runwith.member.dto.MemberDto;

public interface MemberService {

    String memJoin(MemberDto memberDto);

    String memLeave(Long id);

    String memEdit(Long id, MemberDto memberDto);
}
