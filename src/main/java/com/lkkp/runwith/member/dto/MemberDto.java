package com.lkkp.runwith.member.dto;

import com.lkkp.runwith.member.Gender;
import com.lkkp.runwith.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {

    private String email;
    private Gender gender;
    private String profileName;
    private String profileImg;

    public static MemberDto toDto(Member member) {
        return MemberDto.builder()
                .email(member.getEmail())
                .gender(member.getGender())
                .profileImg(member.getProfileImg())
                .profileName(member.getProfileName())
                .build();
    }


}
