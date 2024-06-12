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

    @Column // 1km 3km 5km >> 1 3 5
    private Integer distance;

    @Column // 1 배치했음 0 배치해야됨
    private Boolean matchType;

    @Column
    private Integer matchResult;

    @OneToMany(mappedBy = "match")
    private List<Record> runRecords;

    public void setMatchId(Long id) {this.matchId = matchId;}

    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}

    public void setMatchType(Boolean matchType) {this.matchType = matchType;}

}
