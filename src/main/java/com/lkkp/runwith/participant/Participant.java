package com.lkkp.runwith.participant;

import com.lkkp.runwith.match.Match;
import com.lkkp.runwith.member.Member;
import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.TypeAlias;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Participant", schema = "runwith_db")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;



}