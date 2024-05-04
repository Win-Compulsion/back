package com.lkkp.runwith.member;

import com.lkkp.runwith.IntervalRank.IntervalRank;
import com.lkkp.runwith.match.Match;
import com.lkkp.runwith.record.Record;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Entity
@Table(name = "users")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String nickname;

    private int age;


    @OneToMany(mappedBy = "member")
    private List<Record> runRecords;

    @OneToOne
    private Match matchesAsUser1;

    @OneToOne
    private Match matchesAsUser2;

    @OneToMany(mappedBy = "member")
    private List<IntervalRank> intervalRanks;

    // Getters and setters

}
