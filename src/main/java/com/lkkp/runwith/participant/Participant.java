    package com.lkkp.runwith.participant;

    import com.lkkp.runwith.match.Match;
    import com.lkkp.runwith.member.Member;
    import jakarta.persistence.Entity;

    import jakarta.persistence.*;
    import lombok.*;
    import org.springframework.data.annotation.TypeAlias;

    @Getter
    @Setter
    @Builder
    @Entity
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "Participant", schema = "runwith")
    public class Participant {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", unique = true, nullable = false)
        private Long id;

        @Setter
        @Getter
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        private Member member;

        @Setter
        @Getter
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "match_id")
        private Match match;

        @Column(name = "completed")
        private boolean completed;

        @Column(name = "completionTime")
        private Long completionTime;

        // 빌더 패턴으로 Match와 Member를 설정하도록 변경
        public static Participant create(Match match, Member member) {
            return Participant.builder()
                    .match(match)
                    .member(member)
                    .completed(false)  // 기본값
                    .completionTime(null)  // 기본값
                    .build();
        }

        public Long getMemberId(){
            return member.getId();
        }

        public Long getMatchId(){
            return match.getMatchId();
        }
    }