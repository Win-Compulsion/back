package com.lkkp.runwith.record;

import com.lkkp.runwith.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "records")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double distance;

    private int time; // in seconds

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;



    // Getters and setters
}
