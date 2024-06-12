package com.lkkp.runwith.record;

import com.lkkp.runwith.match.Match;
import com.lkkp.runwith.member.Member;
import jakarta.persistence.*;
import lombok.Getter;

    public void setUserID()


import java.util.Date;

@Getter
@Entity
@Table(name = "Running_Records", schema = "runwith_db")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double distance;

    private int time; // in seconds

    @Temporal(TemporalType.TIMESTAMP)
    private Date rundate;

    private float averageSpeed;
    private Integer runningTime;
    private Integer changeRating;

    @ManyToOne
    @JoinColumn(name = "memberID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "matchID")
    private Match match;

    public void setMemberId(Long memberId) {
        if (member == null) {
            member = new Member();
        }
        member.setId(memberId);
    }

    public void setMatchId(Long matchId) {
        if (match == null) {
            match = new Match();
        }
        match.setMatchId(matchId);
    }

}
