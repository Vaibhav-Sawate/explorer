package com.explorer.backend.controller;

import com.explorer.backend.dto.ObjectListResponse;
import com.explorer.backend.dto.ObjectMetadataResponse;
import com.explorer.backend.service.S3Service;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/buckets")
    public List<String> listBuckets() {
        return s3Service.listBuckets();
    }

    @GetMapping("/buckets/{bucket}/objects")
    public ObjectListResponse listObjects(
            @PathVariable String bucket,
            @RequestParam(defaultValue = "")
            String prefix,

            @RequestParam(required = false)
            @Min(value =1, message="maxKeys must be greater than 0")
            Integer maxKeys,

            @RequestParam(required = false)
            String continuationToken
    ) {
        return s3Service.listObjects(bucket, prefix,  maxKeys, continuationToken);
    }

    @GetMapping("/buckets/{bucket}/object")
    public ObjectMetadataResponse getObjectMetadata(
            @PathVariable String bucket,

            @RequestParam
            @NotBlank (message = "key must not be blank")
            String key
    ) {
        return s3Service.getObjectMetadata(bucket, key);
    }
}
