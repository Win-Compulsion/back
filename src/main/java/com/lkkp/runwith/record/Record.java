package com.lkkp.runwith.record;

import com.lkkp.runwith.location.Location;
import com.lkkp.runwith.match.Match;
import com.lkkp.runwith.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Running_Records", schema = "runwith_db")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @Column(name = "RunningDistance")
    private float distance;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "RunningDate")
    private Date rundate;

    @Column(name = "AverageSpeed")
    private float averageSpeed;
    @Column(name = "RunningTime")
    private Long runningTime;
    @Column(name = "changedRating")
    private Integer changeRating;

    @ManyToOne
    @JoinColumn(name = "user_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "match_ID")
    private Match match;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL)
    private List<Location> locations = new ArrayList<>();

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
