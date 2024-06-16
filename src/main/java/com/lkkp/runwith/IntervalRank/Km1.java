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
@Table(name = "1km_Info", schema = "runwith_db")
public class Km1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Integer rating;

    @Column(nullable = true)
    private Integer win;

    @Column(nullable = true)
    private Integer lose;

    @Column(nullable = true)
    private double best_record;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private Member member;


}
