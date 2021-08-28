package com.kkwonsy.trp.entity;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "trp_trip")
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "city_id"}),
    },
    indexes = @Index(name = "itineraryIndex", columnList = "startAt, endAt")
)
public class Trip extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    private LocalDate startAt;

    private LocalDate endAt;

    @Builder
    public Trip(String title, Member member, City city, LocalDate startAt, LocalDate endAt) {
        this.title = title;
        this.member = member;
        this.city = city;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
