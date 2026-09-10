package com.explorer.backend.dto;

public record CopyObjectResponse (
        String bucket,
        String sourceKey,
        String destinationKey
){
}
