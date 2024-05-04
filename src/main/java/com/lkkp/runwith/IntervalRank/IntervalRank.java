package com.lkkp.runwith.IntervalRank;

import com.lkkp.runwith.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "interval_ranks")
public class IntervalRank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String interval; // 각 구간

    private int rank; // 1, 2, 3 랭크

    @ManyToOne
    private Member member;

}
