package com.kkwonsy.trp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "trp_city")
@Table(
    indexes = {
        @Index(name = "createdAtIndex", columnList = "createdAt"),
        @Index(name = "nameIndex", columnList = "name")
    }
)
public class City extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Builder
    public City(String name) {
        this.name = name;
    }
}
