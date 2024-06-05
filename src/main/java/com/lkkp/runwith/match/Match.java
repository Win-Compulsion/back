package com.lkkp.runwith.match;


import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.participant.Participant;
import com.lkkp.runwith.record.Record;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Match_Info", schema = "runwith_db")
public class Match {

    @Id
    @Column(name = "match_id")
    private Long matchId;


    @Column(nullable = true)
    private LocalDateTime startTime;

    @Column(nullable = true)
    private LocalDateTime endTime;

    @Column
    private String matchType;

    @Column
    private Integer MatchResult;

    @OneToMany(mappedBy = "match")
    private List<Record> runRecords;

    public void setMatchId(Long id) {this.matchId = matchId;}

    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}

    public void setMatchType(String matchType) {this.matchType = matchType;}

}
