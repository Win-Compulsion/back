package com.lkkp.runwith.member;

import com.lkkp.runwith.IntervalRank.Km1;
import com.lkkp.runwith.IntervalRank.Km3;
import com.lkkp.runwith.IntervalRank.Km5;
import com.lkkp.runwith.participant.Participant;
import com.lkkp.runwith.member.dto.MemberDto;
import com.lkkp.runwith.record.Record;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "User_Info", schema = "runwith_db")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;


    @Column(nullable = false) // 사용자 구글 이메일
    private String email;

    @Column(nullable = false)
    private boolean gender;

    @Column(nullable = false)
    private String profileName;
    @Column(nullable = true)
    private String profileImg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Role role; //ROLE_USER


    @OneToMany(mappedBy = "member")
    private List<Participant> participants;


    @OneToMany(mappedBy = "member")
    private List<Record> runRecords;




    public static Member dtoToEntity(MemberDto dto){
        return Member.builder()
                .email(dto.getEmail())
                .gender(dto.isGender())
                .profileImg(dto.getProfileImg())
                .profileName(dto.getProfileName())
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return profileName;
    }

    public void setNickname(String profileName) {
        this.profileName = profileName;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

}
