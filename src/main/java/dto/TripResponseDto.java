package dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TripResponseDto {

    private Long id;
    private String title;
    private Long memberId;
    private Long cityId;
    private LocalDate startAt;
    private LocalDate endAt;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    @Builder
    public TripResponseDto(Long id, String title, Long memberId, Long cityId, LocalDate startAt, LocalDate endAt,
        LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.id = id;
        this.title = title;
        this.memberId = memberId;
        this.cityId = cityId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }
}
