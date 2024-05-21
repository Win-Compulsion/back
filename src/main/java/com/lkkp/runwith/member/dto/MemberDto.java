package com.lkkp.runwith.member.dto;

import com.lkkp.runwith.member.Gender;
import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.member.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDto {

    private Long id;
    private String name;
    private String email;
    private Gender gender;
    private String profileName;
    private String profileImg;
    private Role role;

    public static MemberDto toDto(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .gender(member.getGender())
                .profileImg(member.getProfileImg())
                .profileName(member.getName())
                .role(member.getRole())
                .build();
    }


}
