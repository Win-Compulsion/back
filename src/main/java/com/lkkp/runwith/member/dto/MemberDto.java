package com.lkkp.runwith.member.dto;

import com.lkkp.runwith.member.Gender;
import com.lkkp.runwith.member.Role;
import lombok.Data;

@Data

public class MemberDto {

    private Long id;
    private String name;
    private String email;
    private Gender gender;
    private String profileName;
    private String profileImg;
    private Role role;

}
