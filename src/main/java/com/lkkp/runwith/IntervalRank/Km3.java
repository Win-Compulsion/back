package com.lkkp.runwith.IntervalRank;

import com.lkkp.runwith.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Km3 {
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

    @OneToOne(mappedBy = "km3")
    private Member member;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

}

