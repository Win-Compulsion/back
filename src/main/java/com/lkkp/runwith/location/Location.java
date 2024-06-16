package com.lkkp.runwith.location;

import com.lkkp.runwith.match.Match;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gps_data", schema = "runwith_db")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Setter
    @Getter
    private Long userId;

    @Setter
    @Getter
    private Float latitude;

    @Setter
    @Getter
    private Float longitude;

    @Setter
    @Getter
    private Float altitude;

    @Getter
    @Setter
    private LocalDateTime GPStime;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

}
