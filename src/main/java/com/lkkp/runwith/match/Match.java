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

    @Column // 1km 3km 5km >> 1 3 5
    private Integer distance;

    @Column // 1 배치했음 0 배치해야됨
    private Boolean matchType;

    @Column
    private Integer matchResult;

    @OneToMany(mappedBy = "match")
    private List<Record> runRecords;

}
