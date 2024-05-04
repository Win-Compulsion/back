package com.lkkp.runwith.match;

import com.lkkp.runwith.member.Member;
import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Member member1;

    @OneToOne
    private Member member2;


    @Temporal(TemporalType.TIMESTAMP)
    private Date matchDate;



    // Getters and setters
}