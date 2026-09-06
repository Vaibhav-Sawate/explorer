package com.explorer.backend.dto;

import java.time.Instant;
import java.util.Map;

public record ObjectMetadataResponse (
        String key,
        Long size,
        Instant lastModified,
        String contentType,
        String eTag,
        Map<String, String> metadata
){

}
