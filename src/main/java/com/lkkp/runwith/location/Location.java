package com.lkkp.runwith.location;

import com.lkkp.runwith.record.Record;
import com.lkkp.runwith.match.Match;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "gps_data", schema = "runwith_db")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GPSDataID", nullable = false)
    private Long Id;

    @Column(name = "Latitude")
    private Float latitude;

    @Column(name = "Longitude")
    private Float longitude;

    @Column(name = "Altitude")
    private Float altitude;

    @Column(name = "GPStime")
    private LocalDateTime GPStime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private Record record;

}
