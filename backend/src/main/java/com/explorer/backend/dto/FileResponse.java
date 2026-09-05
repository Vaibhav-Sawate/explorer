package com.explorer.backend.dto;

import java.time.Instant;

public record FileResponse (
        String name,
        String key,
        Long size,
        Instant lastModified //TImezone independent format exact
){
}
