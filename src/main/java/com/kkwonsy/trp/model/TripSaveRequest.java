package com.kkwonsy.trp.model;

import java.time.LocalDate;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TripSaveRequest {

    @NotEmpty
    private String title;

    @NotNull
    private Long cityId;

    @NotNull
    private LocalDate startAt;

    @NotNull
    private LocalDate endAt;

    @Builder
    public TripSaveRequest(String title, Long cityId, LocalDate startAt, LocalDate endAt) {
        this.title = title;
        this.cityId = cityId;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
