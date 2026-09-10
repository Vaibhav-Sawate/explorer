package com.explorer.backend.dto;

public record DeleteObjectResponse(
        String bucket,
        String key
) {
}