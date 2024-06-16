package com.lkkp.runwith.IntervalRank;

import com.lkkp.runwith.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "3km_Info", schema = "runwith_db")
public class Km3 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "rating", nullable = true)
    private Integer rating;

    @Column(name = "win", nullable = true)
    private Integer win;

    @Column(name = "lose", nullable = true)
    private Integer lose;

    @Column(name = "best_record", nullable = true)
    private double best_record;

    @OneToOne
    @JoinColumn(name = "user_ID", nullable = true)
    private Member member;

}

