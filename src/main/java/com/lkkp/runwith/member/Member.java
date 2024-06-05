package com.lkkp.runwith.member;

import com.lkkp.runwith.IntervalRank.Km1;
import com.lkkp.runwith.IntervalRank.Km3;
import com.lkkp.runwith.IntervalRank.Km5;
import com.lkkp.runwith.participant.Participant;
import com.lkkp.runwith.member.dto.MemberDto;
import com.lkkp.runwith.record.Record;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.servlet.tags.form.SelectTag;

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


    @Column(nullable = false) // 사용자 구글 이메일
    private String email;

    @Column(nullable = false)
    private Boolean gender;

    @Column(nullable = false)
    private String profileName;
    @Column(nullable = true)
    private String profileImg;


    @OneToOne(mappedBy = "member")
    private Km1 km1;
    @OneToOne(mappedBy = "member")
    private Km3 km3;
    @OneToOne(mappedBy = "member")
    private Km5 km5;


    @OneToMany(mappedBy = "member")
    private List<Participant> participants;


    @OneToMany(mappedBy = "member")
    private List<Record> runRecords;




    public static Member dtoToEntity(MemberDto dto){
        return Member.builder()
                .email(dto.getEmail())
                .gender(dto.getGender())
                .profileImg(dto.getProfileImg())
                .profileName(dto.getProfileName())
                .build();
    }


    public void updateMember(String profileName, String profileImg) {
        this.profileName = profileName;
        this.profileImg = profileImg;
    }

    public boolean isGender(){
        return gender;
    }



}
