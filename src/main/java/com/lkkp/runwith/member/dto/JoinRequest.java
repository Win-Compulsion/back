package com.lkkp.runwith.member.dto;

import lombok.Data;

@Data
public class JoinRequest {

    private Long id;
    private String name;
    private String nickname;
    private int age;

}
