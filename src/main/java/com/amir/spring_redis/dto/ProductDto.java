package com.amir.spring_redis.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ProductDto(

    Long id , 
    @NotBlank String name,
    @Positive BigDecimal price

) {

}
