package dto;

import java.time.LocalDateTime;

import com.kkwonsy.trp.entity.City;

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

    public static CityResponseDto buildFrom(City city) {
        return CityResponseDto.builder()
            .id(city.getId())
            .name(city.getName())
            .createdAt(city.getCreatedAt())
            .lastModifiedAt(city.getLastModifiedAt())
            .build();
    }
}
