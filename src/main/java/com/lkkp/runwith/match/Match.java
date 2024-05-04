package com.lkkp.runwith.match;

import com.lkkp.runwith.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private int age;

    @Temporal(TemporalType.TIMESTAMP)
    private Date matchDate;



    // Getters and setters
}