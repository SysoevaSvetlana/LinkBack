package com.example.Link.dto;


import jakarta.validation.constraints.*;

public record LinkRequest(
        @NotBlank String originalUrl
) {
}

