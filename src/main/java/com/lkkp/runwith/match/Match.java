package com.lkkp.runwith.match;


import com.lkkp.runwith.participant.Participant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Match {

    @Id
    @Column(name = "match_id")
    private Long matchId;

    @OneToOne
    @MapsId // @MapsID로 participant의 매치iD 매핑
    @JoinColumn(name = "match_id")
    private Participant participant;

    @Column(nullable = true)
    private LocalTime startTime;

    @Column(nullable = true)
    private LocalTime endTime;


}
