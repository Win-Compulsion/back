package com.lkkp.runwith.match;


import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.participant.Participant;
import com.lkkp.runwith.record.Record;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Match {

    @Id
    @Column(name = "match_id")
    private Long matchId;


    @Column(nullable = true)
    private LocalTime startTime;

    @Column(nullable = true)
    private LocalTime endTime;

    @Column
    private String matchType;

    @Column
    private Integer MatchResult;

    @OneToMany(mappedBy = "match")
    private List<Record> runRecords;

}
