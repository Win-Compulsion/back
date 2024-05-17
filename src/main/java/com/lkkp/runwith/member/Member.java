package com.lkkp.runwith.member;

import com.lkkp.runwith.match.Match;
import com.lkkp.runwith.member.dto.MemberDto;
import com.lkkp.runwith.record.Record;
import jakarta.persistence.*;
import lombok.*;

import java.security.Timestamp;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false) // 사용자 구글 이메일
    private String email;

    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String profileName;
    @Column(nullable = true)
    private String profileImg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Role role; //ROLE_USER


    @OneToMany(mappedBy = "member")
    private List<Match> matches;


    @OneToMany(mappedBy = "member")
    private List<Record> runRecords;


    @Builder
    public Member(String name, String email, Gender gender,String profileName, String profileImg, Role role){
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.profileName = profileName;
        this.profileImg = profileImg;
        this.role = role;
    }

    public static Member toEntity(MemberDto dto){
        return Member.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .gender(dto.getGender())
                .profileImg(dto.getProfileImg())
                .profileName(dto.getName())
                .role(dto.getRole())
                .build();
    }


    public void updateMember(String profileName, String profileImg) {
        this.profileName = profileName;
        this.profileImg = profileImg;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }

//    @OneToMany(mappedBy = "member")
//    private List<IntervalRank> intervalRanks;

}
