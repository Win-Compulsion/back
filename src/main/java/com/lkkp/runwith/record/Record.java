package com.lkkp.runwith.record;

import com.lkkp.runwith.member.Member;
import jakarta.persistence.*;
import lombok.Getter;


import java.util.Date;

@Getter
@Entity
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double distance;

    private int time; // in seconds

    @Temporal(TemporalType.TIMESTAMP)
    private Date rundate;

    @ManyToOne
    @JoinColumn(name = "memberID")
    private Member member;


    // Getters and setters
}
