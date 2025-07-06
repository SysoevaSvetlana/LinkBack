package com.example.Link.dto;

import java.time.OffsetDateTime;

public record LinkResponse(
        Long id,
        String originalUrl,
        String shortCode,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
