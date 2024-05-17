package com.lkkp.runwith.match;

import com.lkkp.runwith.member.Member;
import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(name = "id")
    private Long id;


    @Column(name = "match_id")
    private Long matchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member1")
    private Member member;


//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "member2")
//    private Member member2;


    // Getters and setters
}