package com.lkkp.runwith.member;

import com.lkkp.runwith.match.Match;
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

    private String name;

    private String nickname;

    private int age;

    // 아래는 비공개 정보

    private String email;

    private Timestamp joinDate; // 생성 날짜

    private Timestamp lastLoginDate; // 최근 로그인 날짜


    @OneToMany(mappedBy = "member")
    private List<Match> matches;


    @OneToMany(mappedBy = "member")
    private List<Record> runRecords;

    public void updateMember(Long id, String name, String nickname, int age) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.age = age;

    }


//    @OneToMany(mappedBy = "member")
//    private List<IntervalRank> intervalRanks;

    // Getters and setters

}
