package com.lkkp.runwith.user;

import com.lkkp.runwith.match.Match;
import com.lkkp.runwith.record.Record;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    @ManyToOne
    private Match match;



    // Getters and setters

}
