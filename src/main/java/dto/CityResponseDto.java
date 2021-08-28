package dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CityResponseDto {

    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    @Builder
    public CityResponseDto(Long id, String name, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }
}
