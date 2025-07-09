package com.example.Link.dto;


import jakarta.validation.constraints.*;

public record LinkRequest(
        @Size(max = 2048)
        @Pattern(regexp = "https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+",
                message = "URL must start with http:// or https://")
        String originalUrl
) {
}

