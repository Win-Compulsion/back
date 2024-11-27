package com.lkkp.runwith.match;


import com.lkkp.runwith.location.Location;
import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.participant.Participant;
import com.lkkp.runwith.record.Record;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Match_Info", schema = "runwith")
public class Match {

    @Id
    @Column(name = "match_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;


    @Column(name = "MatchStartTime", nullable = true)
    private LocalDateTime startTime;

    @Column(name = "MatchEndTime", nullable = true)
    private LocalDateTime endTime;

    @Column(name = "MatchDistance") // 1km 3km 5km >> 1 3 5
    private Integer distance;

    @Column(name = "MatchType") // 1 배치했음 0 배치해야됨
    private String matchType;

    @Column(name = "MatchResult")
    private Integer matchResult;

    @OneToMany(mappedBy = "match")
    private List<Participant> participants;

    @OneToMany(mappedBy = "match")
    private List<Record> runRecords;

    public void setMatchId(Long id) {this.matchId = matchId;}

    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}

    public void setMatchType(String matchType) {this.matchType = matchType;}

    public void setEndTime(LocalDateTime endTime) {this.endTime = endTime;}

    public void setMatchResult(Integer matchResult) {this.matchResult = matchResult;}

}
