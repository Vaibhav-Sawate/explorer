package com.explorer.backend.dto;

import java.util.List;

public record ObjectSearchResponse(
        String bucket,
        String query,
        List<FileResponse> files
) {
}
