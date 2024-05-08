package com.lkkp.runwith.member;

import com.lkkp.runwith.match.Match;
import com.lkkp.runwith.record.Record;
import jakarta.persistence.*;
import lombok.*;

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


    @OneToMany(mappedBy = "member")
    private List<Match> matches;


    @OneToMany(mappedBy = "member")
    private List<Record> runRecords;


//    @OneToMany(mappedBy = "member")
//    private List<IntervalRank> intervalRanks;

    // Getters and setters

}
